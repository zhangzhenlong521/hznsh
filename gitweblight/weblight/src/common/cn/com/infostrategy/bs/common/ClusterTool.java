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
 * ��Ⱥ������!!!!!����Ǽ�Ⱥ�߼��ĺ�������!!
 * ��һ������Ҫ�Ĳ���,��ClusterPorts��,��ʽ��"9001-9003",�µ��﷨��"168.5.129.20[9001-9002];168.5.129.12[9001-9002];168.5.129.13[9001]"
 * ����ԭ����,���ݶ���Ķ˿�����,һ������ѭ��ȥ̽���䵱ǰ������,�ҵ�һ����ǰ��������50���ڵľͷ���!!
 * ֮����ÿ�ζ�����̽��,���Ƿ�ֹһЩ��������;���˻��˳���ԭ��!!
 * @author xch
 *
 */
public class ClusterTool {

	public static Long li_thisServerLoginCount = new Long(0); //
	private static Logger logger = WLTLogger.getLogger(ClusterTool.class); //

	private TBUtil tbUtil = null; //

	/**
	 * ȡ�ü�Ⱥʱʵ�ʵĶ˿�!!�����߼���ʼ�ĵط�!!! ��һ������ʱ���Ƚ��м�Ⱥ����! ���û���弯Ⱥ��ֱ�ӷ��������ԭʼIP��˿�!!
	 * �������Ϻ�ũ������Ŀ������IP��˿ڽ�����ת��!! ���Կ�����xml�ļ��ж���ForceIPPort_ForClientCall����,ǿ��ָ���ͻ�������ĵ�ַ!
	 * ͬʱ�°汾�����˽��ͻ��������IP��˿�Ҳ��������! ��˵��˵,ֻҪͬʱ�����˿ͻ���,���Ϻ�ũ������������,���������Ҳ�ǿ��Ե�!
	 * ͬʱ������������IP��˿�ת���Ĺ���,�����Ϻ�ũ�����������,��������Ⱥ,�����ʹ��mapת������,���ȶ��������˵�ַ,Ȼ��ת����ʵ�������ĵ�ַ!
	 * @param _port ʵ�ʷ��ʵĶ˿�
	 * @return
	 */
	public String[] getRealCallIPAndPort(String _inputIP, String _currIP, int _currPort, String _context) {
		String str_forceIPPort = ServerEnvironment.getProperty("ForceIPPort_ForClientCall"); //ǿ�ƶ����Ϊ�ͻ��˶����IP��˿ں�!! �����Ϻ�ũ�����о�������������! ���������������������൱��û�м�ȺЧ����!!
		if (str_forceIPPort != null) { //����ж���,�����ʽ�����ж˿�,�������ǡ�192.168.0.19:9001���ĸ�ʽ!!
			String str_forceIP = str_forceIPPort.substring(0, str_forceIPPort.indexOf(":"));
			String str_forcePort = str_forceIPPort.substring(str_forceIPPort.indexOf(":") + 1, str_forceIPPort.length()); //
			return new String[] { str_forceIP, str_forcePort }; //���������,��������������!!!!			
		}

		String str_thisIP = null;
		String str_thisPort = null; //
		if (_inputIP != null) { //���������ĵ�ַ,���°汾�����ӵ�!,���������!!
			str_thisIP = _inputIP.substring(0, _inputIP.indexOf(":")); //ԭʼ�����IP
			str_thisPort = _inputIP.substring(_inputIP.indexOf(":") + 1, _inputIP.length()); //
		} else {
			str_thisIP = _currIP; //��ǰ�ĸ���Request.getServer��ȡ��! ��ʵ���Ϻ�ũ������Ŀ�е����绷�����������!
			str_thisPort = "" + _currPort; //
		}

		String str_clusterPorts = ServerEnvironment.getProperty("ClusterPorts"); //��Ⱥ�˿���!!!��9001-9003�ĸ�ʽ!!!
		if (str_clusterPorts == null || str_clusterPorts.trim().equals("")) { //���û�ж���,��ֱ�ӷ���!!!
			return new String[] { str_thisIP, str_thisPort }; //��������ĵ�ַ��˿�,Ϊ�˷�ֹ����ǽ��IPת����ɵ�����
		}

		String[][] all_IPAndPorts = getAllIPAndPort(str_clusterPorts); //ȡ�����е�Ip��˿ں�!,����ǾɵĻ���
		if (all_IPAndPorts == null || all_IPAndPorts.length <= 0) { //������Ϊĳ��ԭ����㲻����
			return new String[] { str_thisIP, str_thisPort };
		}

		int li_pos = (int) (li_thisServerLoginCount % all_IPAndPorts.length); //�ҵ�λ��,ȡ��,�����0,�򷵻�0,�����1�򷵻�1,�����4�򷵻�4,�����5�򷵻�0,�����ͻ��γ�һ����ѭ��!�Ӷ��γɸ��ؾ��ȵ�Ч��!!!! 1%3=1;2/3=2;3%3=0;
		synchronized (li_thisServerLoginCount) {
			li_thisServerLoginCount++; //һ��Ҫ�ۼ�һ��,�����γ�ѭ��!!!!
		}
		String str_clusterIp = all_IPAndPorts[li_pos][0]; //��Ӧ����λ���ϵ�IP��ַ!
		String str_clusterPort = all_IPAndPorts[li_pos][1]; //��Ӧ����λ���ϵĶ˿ں�!

		String[] str_return_ip_port = null;
		if (str_clusterIp.equals(str_thisIP) && str_clusterPort.equals(str_thisPort)) { //������õ��ڱ��˿�,ֱ�ӷ�������,�Ӷ��������,��Ϊû�б�Ҫ̽����!!!��Ϊ��Ȼ�Ѿ�������,��˵���Լ��ǻ��ŵ�!!!!
			logger.debug("������������Ⱥ����ʱ,����ȡ�õĵ�ַ��˿�[" + str_thisIP + ":" + str_thisPort + "]�����ǵ�ǰ���ʵ�,�ʲ����Ƿ���̽���ֱ�ӷ���!!!"); //������ڷ���ǽӳ��IPʱ������!!
			str_return_ip_port = all_IPAndPorts[li_pos]; //
		} else { //̽��һ�¸õ�ַ�Ƿ����,Ȼ�󷵻�!!!
			str_return_ip_port = getAliveMachine(all_IPAndPorts, li_pos, _context); //����ȡ�û��ŵĶ˿�!!!������ö˿��ǻ��ŵĲ���!!!
		}

		//�����ӳ��,����������ת��һ��! ��Ϊһ�����绷����,�ǲ���Ҫӳ���,���ӿͻ���ȥ���ʸõ�ַ��ӷ�������ȥ���ʸõ�ַЧ����һ����! ���ڲ������绷�����������������! �����еķ���ǽ����IPת����ӳ��!
		HashMap maps = null; //
		String str_cluster_map = ServerEnvironment.getProperty("ClusterPortsMap"); //ӳ�䶨��
		if (str_cluster_map != null && !str_cluster_map.trim().equals("")) { //���������ӳ��!!!
			maps = getTBUtil().convertStrToMapByExpress(str_cluster_map, ";", "=>"); //�ֽ�ɹ�ϣ��!
		}
		if (str_return_ip_port[2].equals("Y")) { //�����Ӳ��Ⱥ��,���µĻ���!!����ֱ����IP��˿�һ��ת����!
			String str_key = str_return_ip_port[0] + ":" + str_return_ip_port[1]; //
			if (maps != null && maps.containsKey(str_key)) {
				String str_convertIPPort = (String) maps.get(str_key); //ת�����Ip
				String str_convertIP = str_convertIPPort.substring(0, str_convertIPPort.indexOf(":")); //ת�����IP
				String str_convertPort = str_convertIPPort.substring(str_convertIPPort.indexOf(":") + 1, str_convertIPPort.length()); //ת����Ķ˿�
				return new String[] { str_convertIP, str_convertPort }; //
			} else { //��ת��
				return new String[] { str_return_ip_port[0], str_return_ip_port[1] }; //
			}
		} else { //�������Ⱥ,��һ̨Ӳ���ϵĲ�ͬ�˿�,��IP��Ȼʹ�ô����IP,ֻ�Զ˿ڽ����滻!!��Ϊ���ﷵ�ص�IP��Զ��127.0.0.1,�������ͻ����ǲ��Ե�!
			String str_key = str_return_ip_port[1]; //�˿�
			if (maps != null && maps.containsKey(str_key)) {
				String str_convertPort = (String) maps.get(str_key); //ת����Ķ˿�
				return new String[] { str_thisIP, str_convertPort }; //IPʹ��ת���!
			} else { //��ת��!
				return new String[] { str_thisIP, str_return_ip_port[1] }; //	
			}
		}
	}

	/**
	 * ȡ�����л��ŵĶ˿�!!!����Ҫ�ز�һ�¸ö˿��Ƿ����!!!��������򷵻�!!
	 * @param _allPorts
	 * @param _parPort
	 * @return
	 */
	private String[] getAliveMachine(String[][] _allIPAndPorts, int _pos, String _context) {
		if (_context.startsWith("/")) {
			_context = _context.substring(1, _context.length()); ////
		}
		int[] li_pos_cycles = new int[_allIPAndPorts.length]; //��������п���ѭ�����±�λ��,������µ�����!!
		int li_tmpPos = _pos; //
		for (int i = 0; i < li_pos_cycles.length; i++) {
			li_pos_cycles[i] = li_tmpPos; //
			li_tmpPos++; //��һ,
			if (li_tmpPos >= _allIPAndPorts.length) { //�����5��,���±�λ����0-4,����4����,������5ʱ�Ͳ�����,�����0!
				li_tmpPos = 0;
			}
		}

		for (int i = 0; i < li_pos_cycles.length; i++) { //�������п��ܵ�IP��˿�,��ǰ���и�������Ļ������㷨,����Ϊ�ж��Ƿ�����ı�������Ͳ�׼,���Ըɴ�ȥ����!��ֱ�ӷ���ֻҪ���ž�����!
			int li_pos = li_pos_cycles[i]; //λ������!!!
			String str_ip = _allIPAndPorts[li_pos][0]; //IP��ַ
			String str_port = _allIPAndPorts[li_pos][1]; //�˿�
			String str_url = "http://" + str_ip + ":" + str_port + "/" + _context + "/login?logintype=getCurrThreads"; //�Ƿ����
			String str_currThreads = getHttpConnectionText(str_url); //�Ƿ����ӵ���,�Ժ�Ҫ���Ǽ�������Ե���������,Ȼ���ҳ����е�!!!
			logger.debug("������������Ⱥ����ʱ,̽��URL[" + str_url + "]�õ�����ֵ[" + str_currThreads + "],�����Ϊ��,��˵���ǻ��ŵ�.."); //
			if (str_currThreads != null) { //������ӵ���,��˵���ǻ��ŵ�,����֮!!
				return _allIPAndPorts[li_pos]; //
			}
		}

		return _allIPAndPorts[_pos]; //�����֪ʲô��û�ҵ�,�򷵻ؿ�ʼ��!
	}

	/**
	 * ��һ�����Ӳ���,���������,�򷵻�true,���򷵻�false..
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
			conn.setConnectTimeout(10000); //ֻ��10��,ע����,������WebSphere���ʲ��ϵ�����!!!//��������Ϊ��0��Ҳ�������ã��ʸĻ�ԭ���롾���/2018-06-25��
			conn.setReadTimeout(10000); //ֻ��5��,ע����,������WebSphere���ʲ��ϵ�����!!!
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
			System.err.println("Ѱ�Ҽ�Ⱥ�˿�ʱ,����[" + _url + "]�쳣," + ex.getClass() + "," + ex.getMessage()); //
			//ex.printStackTrace(); //
			return null;
		}
	}

	/**
	 * ȡ�����ж˿ں�!!!
	 * @param _ports
	 * @return
	 */
	private int[] getAllPortArray(String _ports) {
		try {
			if (_ports.indexOf("-") < 0) {
				return new int[] { Integer.parseInt(_ports) };
			}
			int li_beginPort = Integer.parseInt(_ports.substring(0, _ports.indexOf("-"))); //��ʼ�Ķ˿ں�!!!!!
			int li_endPort = Integer.parseInt(_ports.substring(_ports.indexOf("-") + 1, _ports.length())); //�����Ķ˿ں�!!!!
			int[] li_array = new int[li_endPort - li_beginPort + 1]; //
			for (int i = 0; i < li_array.length; i++) {
				li_array[i] = li_beginPort + i; // 
			}
			return li_array; //�������п��ܵĶ˿ں�!!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * �����﷨���ؼ�Ⱥ��ʵ�ʵ�ַ��˿ں�,�����ݾɵ���������,�ɵ��﷨��"9001-9003",�µ��﷨��"168.5.129.20[9001-9002];168.5.129.12[9001-9002];168.5.129.13[9001]"
	 * ���µ��﷨��IP��,��֧�ֶ�̨������ļ�Ⱥ��!!Ȼ��ÿ̨����������Ƕ�������Ķ˿�,Ҳ����һ̨�������һ���˿�!
	 * ��Ҫע�������������ַ������,����Ϊ������е��㷨���ں�̨���е�,����Ӧ��������IP,����ȫ�п�����Щ����IP�ṩ���ͻ����ǲ����õ�!��Ϊ�ͻ��˿����õ�������ip��˿�!
	 * ����������IPת��������,�Ժ��ٽ��! ���Ժ���Ҫ�ټ�һ����������ָ�����ӳ��,����"168.5.129.20:9001=>168.5.129.20:8888;"
	 * @param _formula
	 * @return
	 */
	private String[][] getAllIPAndPort(String _formula) {
		try {
			if (_formula.indexOf("[") > 0 && _formula.indexOf("]") > 0) { //�����[],˵�����µĻ���!!��ʹ�����㷨
				String[] str_items = getTBUtil().split(_formula, ";"); //���Էֺŷָ�!!!
				ArrayList al_tmp = new ArrayList(); //
				for (int i = 0; i < str_items.length; i++) { //����
					int li_pos_1 = str_items[i].indexOf("["); //
					int li_pos_2 = str_items[i].indexOf("]"); //
					String str_ip = str_items[i].substring(0, li_pos_1); //
					String str_ports = str_items[i].substring(li_pos_1 + 1, li_pos_2); //
					int[] li_ports = getAllPortArray(str_ports); //�������ж˿�����!!
					for (int j = 0; j < li_ports.length; j++) { //�������ж˿�!!!
						al_tmp.add(new String[] { str_ip, "" + li_ports[j], "Y" }); //����!!
					}
				}
				String[][] str_return = new String[al_tmp.size()][3]; //
				for (int i = 0; i < al_tmp.size(); i++) { //
					str_return[i] = (String[]) al_tmp.get(i); //
				}
				return str_return; //
			} else { //����ǾɵĻ���,����"9001-9003"
				int[] li_ports = getAllPortArray(_formula); //ȡ�ö˿��б�!
				if (li_ports == null || li_ports.length == 0) { //����ж˿�!!!
					return null;
				}
				String[][] str_return = new String[li_ports.length][3]; //
				for (int i = 0; i < str_return.length; i++) {
					str_return[i][0] = "127.0.0.1"; //IP���Ǳ���
					str_return[i][1] = "" + li_ports[i]; //�˿�
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
 * 12��28���ύ
 *
 * Revision 1.3  2010/08/20 06:05:46  xuchanghua
 * *** empty log message ***
 *
 *
 **************************************************************************/
