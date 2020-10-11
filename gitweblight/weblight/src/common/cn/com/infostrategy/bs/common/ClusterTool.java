/**************************************************************************
 * $RCSfile: ClusterTool.java,v $  $Revision: 1.9 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.bs.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;

/**
 * 集群处理工具!!!!!这才是集群逻辑的核心所在!!
 * 有一个最重要的参数,【ClusterPorts】,格式是"9001-9003",新的语法是"168.5.129.20[9001-9002];168.5.129.12[9001-9002];168.5.129.13[9001]"
 * 它的原理是,根据定义的端口序列,一个个的循环去探测其当前并发数,找到一个当前并发数在50以内的就返回!!
 * 之所以每次都当场探测,就是防止一些服务器中途死了或退出等原因!!
 * @author xch
 *
 */
public class ClusterTool {

	public static Long li_thisServerLoginCount = new Long(0); //
	private static Logger logger = WLTLogger.getLogger(ClusterTool.class); //

	private TBUtil tbUtil = null; //

	/**
	 * 取得集群时实际的端口!!核心逻辑开始的地方!!! 第一次请求时是先进行集群计算! 如果没定义集群则直接返回请求的原始IP与端口!!
	 * 由于在上海农商行项目中遇到IP与端口进行了转换!! 所以可以在xml文件中定义ForceIPPort_ForClientCall变量,强行指定客户端请求的地址!
	 * 同时新版本增加了将客户端请求的IP与端口也传过来了! 换说话说,只要同时更新了客户端,像上海农商行这种问题,不定义变量也是可以的!
	 * 同时还做了内外网IP与端口转换的功能,即在上海农商行这种情况,又想做集群,则必须使用map转换功能,即先定义内网人地址,然后转换成实际外网的地址!
	 * @param _port 实际访问的端口
	 * @return
	 */
	public String[] getRealCallIPAndPort(String _inputIP, String _currIP, int _currPort, String _context) {
		String str_forceIPPort = ServerEnvironment.getProperty("ForceIPPort_ForClientCall"); //强制定义的为客户端定义的IP与端口号!! 像在上海农商行中就遇到这种问题! 但如果定义了这个变量就相当于没有集群效果了!!
		if (str_forceIPPort != null) { //如果有定义,这个格式必须有端口,即必须是【192.168.0.19:9001】的格式!!
			String str_forceIP = str_forceIPPort.substring(0, str_forceIPPort.indexOf(":"));
			String str_forcePort = str_forceIPPort.substring(str_forceIPPort.indexOf(":") + 1, str_forceIPPort.length()); //
			return new String[] { str_forceIP, str_forcePort }; //如果定义了,则优先以它返回!!!!			
		}

		String str_thisIP = null;
		String str_thisPort = null; //
		if (_inputIP != null) { //如果有输入的地址,即新版本中增加的!,则拿输入的!!
			str_thisIP = _inputIP.substring(0, _inputIP.indexOf(":")); //原始输入的IP
			str_thisPort = _inputIP.substring(_inputIP.indexOf(":") + 1, _inputIP.length()); //
		} else {
			str_thisIP = _currIP; //以前的根据Request.getServer来取的! 其实在上海农商行项目中的网络环境是有问题的!
			str_thisPort = "" + _currPort; //
		}

		String str_clusterPorts = ServerEnvironment.getProperty("ClusterPorts"); //集群端口数!!!是9001-9003的格式!!!
		if (str_clusterPorts == null || str_clusterPorts.trim().equals("")) { //如果没有定义,则直接返回!!!
			return new String[] { str_thisIP, str_thisPort }; //返回输入的地址与端口,为了防止防火墙的IP转换造成的问题
		}

		String[][] all_IPAndPorts = getAllIPAndPort(str_clusterPorts); //取得所有的Ip与端口号!,如果是旧的机制
		if (all_IPAndPorts == null || all_IPAndPorts.length <= 0) { //可能因为某种原因计算不出来
			return new String[] { str_thisIP, str_thisPort };
		}

		int li_pos = (int) (li_thisServerLoginCount % all_IPAndPorts.length); //找到位置,取余,如果是0,则返回0,如果是1则返回1,如果是4则返回4,如果是5则返回0,这样就会形成一个死循环!从而形成负载均匀的效果!!!! 1%3=1;2/3=2;3%3=0;
		synchronized (li_thisServerLoginCount) {
			li_thisServerLoginCount++; //一定要累加一下,才能形成循环!!!!
		}
		String str_clusterIp = all_IPAndPorts[li_pos][0]; //对应坐标位置上的IP地址!
		String str_clusterPort = all_IPAndPorts[li_pos][1]; //对应坐标位置上的端口号!

		String[] str_return_ip_port = null;
		if (str_clusterIp.equals(str_thisIP) && str_clusterPort.equals(str_thisPort)) { //如果正好等于本端口,直接返回算了,从而提高性能,因为没有必要探测了!!!因为既然已经连上了,则说明自己是活着的!!!!
			logger.debug("服务器端做集群计算时,发现取得的地址与端口[" + str_thisIP + ":" + str_thisPort + "]正好是当前访问的,故不做是否存活探测而直接返回!!!"); //这可能在防火墙映射IP时有问题!!
			str_return_ip_port = all_IPAndPorts[li_pos]; //
		} else { //探测一下该地址是否活着,然后返回!!!
			str_return_ip_port = getAliveMachine(all_IPAndPorts, li_pos, _context); //遍历取得活着的端口!!!即必须该端口是活着的才行!!!
		}

		//如果有映射,则必须进行再转换一下! 因为一般网络环境下,是不需要映射的,即从客户端去访问该地址与从服务器端去访问该地址效果是一样的! 但在不少网络环境下两者是有区别的! 比如有的防火墙做了IP转换与映射!
		HashMap maps = null; //
		String str_cluster_map = ServerEnvironment.getProperty("ClusterPortsMap"); //映射定义
		if (str_cluster_map != null && !str_cluster_map.trim().equals("")) { //如果定义了映射!!!
			maps = getTBUtil().convertStrToMapByExpress(str_cluster_map, ";", "=>"); //分解成哈希表!
		}
		if (str_return_ip_port[2].equals("Y")) { //如果是硬集群的,即新的机制!!则是直接拿IP与端口一起转换的!
			String str_key = str_return_ip_port[0] + ":" + str_return_ip_port[1]; //
			if (maps != null && maps.containsKey(str_key)) {
				String str_convertIPPort = (String) maps.get(str_key); //转换后的Ip
				String str_convertIP = str_convertIPPort.substring(0, str_convertIPPort.indexOf(":")); //转换后的IP
				String str_convertPort = str_convertIPPort.substring(str_convertIPPort.indexOf(":") + 1, str_convertIPPort.length()); //转换后的端口
				return new String[] { str_convertIP, str_convertPort }; //
			} else { //不转换
				return new String[] { str_return_ip_port[0], str_return_ip_port[1] }; //
			}
		} else { //如果是软集群,即一台硬件上的不同端口,则IP仍然使用传入的IP,只对端口进行替换!!因为这里返回的IP永远是127.0.0.1,把它给客户端是不对的!
			String str_key = str_return_ip_port[1]; //端口
			if (maps != null && maps.containsKey(str_key)) {
				String str_convertPort = (String) maps.get(str_key); //转换后的端口
				return new String[] { str_thisIP, str_convertPort }; //IP使用转入的!
			} else { //不转换!
				return new String[] { str_thisIP, str_return_ip_port[1] }; //	
			}
		}
	}

	/**
	 * 取得所有活着的端口!!!就是要控测一下该端口是否活着!!!如果不活则返回!!
	 * @param _allPorts
	 * @param _parPort
	 * @return
	 */
	private String[] getAliveMachine(String[][] _allIPAndPorts, int _pos, String _context) {
		if (_context.startsWith("/")) {
			_context = _context.substring(1, _context.length()); ////
		}
		int[] li_pos_cycles = new int[_allIPAndPorts.length]; //先算出所有可能循环的下标位置,定义成新的数组!!
		int li_tmpPos = _pos; //
		for (int i = 0; i < li_pos_cycles.length; i++) {
			li_pos_cycles[i] = li_tmpPos; //
			li_tmpPos++; //加一,
			if (li_tmpPos >= _allIPAndPorts.length) { //如果是5个,则下标位置是0-4,则当是4还行,但当是5时就不行了,必须归0!
				li_tmpPos = 0;
			}
		}

		for (int i = 0; i < li_pos_cycles.length; i++) { //遍历所有可能的IP与端口,以前还有个找最轻的机器的算法,但因为判断是否最轻的变量本身就不准,所以干脆去掉了!先直接返回只要活着就行了!
			int li_pos = li_pos_cycles[i]; //位置坐标!!!
			String str_ip = _allIPAndPorts[li_pos][0]; //IP地址
			String str_port = _allIPAndPorts[li_pos][1]; //端口
			String str_url = "http://" + str_ip + ":" + str_port + "/" + _context + "/login?logintype=getCurrThreads"; //是否活着
			String str_currThreads = getHttpConnectionText(str_url); //是否连接得上,以后还要考虑计算出各自的在线人数,然后找出最闲的!!!
			logger.debug("服务器端做集群计算时,探测URL[" + str_url + "]得到返回值[" + str_currThreads + "],如果不为空,则说明是活着的.."); //
			if (str_currThreads != null) { //如果连接得上,则说明是活着的,返回之!!
				return _allIPAndPorts[li_pos]; //
			}
		}

		return _allIPAndPorts[_pos]; //如果不知什么都没找到,则返回开始的!
	}

	/**
	 * 做一个连接测试,如果连上了,则返回true,否则返回false..
	 * @param _url
	 * @return
	 */
	private String getHttpConnectionText(String _url) {
		try {
			URLConnection conn = new URL(_url).openConnection();
			if (_url.startsWith("https")) {
				new BSUtil().addHttpsParam(conn);
			}
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setConnectTimeout(10000); //只等10秒,注销掉,遇到过WebSphere访问不上的问题!!!//张珍龙改为了0，也不起作用，故改回原代码【李春娟/2018-06-25】
			conn.setReadTimeout(10000); //只等5秒,注销掉,遇到过WebSphere访问不上的问题!!!
			InputStream intStream = conn.getInputStream(); //
			BufferedReader in = new BufferedReader(new InputStreamReader(intStream));
			String str;
			StringBuffer sb_html = new StringBuffer();
			while ((str = in.readLine()) != null) {
				sb_html.append(str); //
			}
			in.close();
			return sb_html.toString(); //
		} catch (Exception ex) {
			System.err.println("寻找集群端口时,连接[" + _url + "]异常," + ex.getClass() + "," + ex.getMessage()); //
			//ex.printStackTrace(); //
			return null;
		}
	}

	/**
	 * 取得所有端口号!!!
	 * @param _ports
	 * @return
	 */
	private int[] getAllPortArray(String _ports) {
		try {
			if (_ports.indexOf("-") < 0) {
				return new int[] { Integer.parseInt(_ports) };
			}
			int li_beginPort = Integer.parseInt(_ports.substring(0, _ports.indexOf("-"))); //开始的端口号!!!!!
			int li_endPort = Integer.parseInt(_ports.substring(_ports.indexOf("-") + 1, _ports.length())); //结束的端口号!!!!
			int[] li_array = new int[li_endPort - li_beginPort + 1]; //
			for (int i = 0; i < li_array.length; i++) {
				li_array[i] = li_beginPort + i; // 
			}
			return li_array; //返回所有可能的端口号!!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * 解析语法返回集群的实际地址与端口号,它兼容旧的配置语言,旧的语法是"9001-9003",新的语法是"168.5.129.20[9001-9002];168.5.129.12[9001-9002];168.5.129.13[9001]"
	 * 即新的语法有IP了,即支持多台物理机的集群了!!然后每台物理机上又是多个连续的端口,也可能一台物理机就一个端口!
	 * 需要注意的是内外网地址的区别,即因为计算空闲的算法是在后台进行的,所以应该是内网IP,但完全有可能这些内网IP提供给客户端是不能用的!因为客户端可能用的是外网ip与端口!
	 * 关于内外网IP转换的问题,以后再解决! 即以后需要再加一个参数用于指定这个映射,比如"168.5.129.20:9001=>168.5.129.20:8888;"
	 * @param _formula
	 * @return
	 */
	private String[][] getAllIPAndPort(String _formula) {
		try {
			if (_formula.indexOf("[") > 0 && _formula.indexOf("]") > 0) { //如果有[],说明是新的机制!!则使用新算法
				String[] str_items = getTBUtil().split(_formula, ";"); //先以分号分隔!!!
				ArrayList al_tmp = new ArrayList(); //
				for (int i = 0; i < str_items.length; i++) { //遍历
					int li_pos_1 = str_items[i].indexOf("["); //
					int li_pos_2 = str_items[i].indexOf("]"); //
					String str_ip = str_items[i].substring(0, li_pos_1); //
					String str_ports = str_items[i].substring(li_pos_1 + 1, li_pos_2); //
					int[] li_ports = getAllPortArray(str_ports); //计算所有端口序列!!
					for (int j = 0; j < li_ports.length; j++) { //遍历所有端口!!!
						al_tmp.add(new String[] { str_ip, "" + li_ports[j], "Y" }); //加入!!
					}
				}
				String[][] str_return = new String[al_tmp.size()][3]; //
				for (int i = 0; i < al_tmp.size(); i++) { //
					str_return[i] = (String[]) al_tmp.get(i); //
				}
				return str_return; //
			} else { //如果是旧的机制,比如"9001-9003"
				int[] li_ports = getAllPortArray(_formula); //取得端口列表!
				if (li_ports == null || li_ports.length == 0) { //如果有端口!!!
					return null;
				}
				String[][] str_return = new String[li_ports.length][3]; //
				for (int i = 0; i < str_return.length; i++) {
					str_return[i][0] = "127.0.0.1"; //IP就是本地
					str_return[i][1] = "" + li_ports[i]; //端口
					str_return[i][2] = "N"; //
				}
				return str_return; //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil(); //
		return tbUtil; //
	}

}

/**************************************************************************
 * $RCSfile: ClusterTool.java,v $  $Revision: 1.9 $  $Date: 2012/09/14 09:17:30 $
 *
 * $Log: ClusterTool.java,v $
 * Revision 1.9  2012/09/14 09:17:30  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2012/08/28 09:40:48  Administrator
 * *** empty log message ***
 *
 * Revision 1.8  2011/10/10 06:31:35  wanggang
 * restore
 *
 * Revision 1.6  2011/07/13 12:20:44  xch123
 * *** empty log message ***
 *
 * Revision 1.5  2010/12/29 13:33:43  xch123
 * *** empty log message ***
 *
 * Revision 1.4  2010/12/28 10:28:50  xch123
 * 12月28日提交
 *
 * Revision 1.3  2010/08/20 06:05:46  xuchanghua
 * *** empty log message ***
 *
 *
 **************************************************************************/
