package cn.com.infostrategy.bs.sysapp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.Logger;

import cn.com.infostrategy.bs.common.BSUtil;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.RemoteCallParVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;

/**
 * 所有服务器端数据缓存,比如机构,人员,等,因为在工作流,参照等地方,到处频繁用到这些数据!!而这些数据量又特别大,且更新又不频繁,所以特别适合做缓存!!!
 * @author Administrator
 */
public class ServerCacheDataFactory {

	private static ServerCacheDataFactory factoryInstance = null;

	private HashMap cacheData = new HashMap(); //; //

	private Logger logger = WLTLogger.getLogger(ServerCacheDataFactory.class); //

	//做缓存要满足几个条件:
	//1.数量量小(否则内存有问题)  2.频繁使用(否则没必要)
	public static HashVO[] static_vos_allMenu = null; //
	public static HashVO[] static_vos_lookandfeel = null; //
	public static HashVO[] static_vos_corptypedef = null; //
	public static String[] static_str_commroles = null; //像一般用户,普通员工等通用角色!!!

	private ServerCacheDataFactory() {
	}

	public static ServerCacheDataFactory getInstance() {
		if (factoryInstance == null) {
			factoryInstance = new ServerCacheDataFactory();
		}
		return factoryInstance;
	}

	//注册表型缓存数据,如果有,则覆盖之
	public void registeTableCacheData(String _keyName, String _tableName) {
		registeTableCacheData(_keyName, _tableName, "*"); //
	}

	//注册表型缓存数据,如果有,则覆盖之
	public void registeTableCacheData(String _keyName, String _tableName, String _selCols) {
		registeTableCacheData(_keyName, _tableName, _selCols, true);
	}

	public void registeTableCacheData(String _keyName, String _tableName, String _selCols, Boolean isfirst) {
		try {
			long ll_1 = System.currentTimeMillis(); //
			CommDMO commDMO = new CommDMO(); //
			HashVO[] hvs = commDMO.getHashVoArrayByDS(null, "select " + _selCols + " from " + _tableName); //
			cacheData.put(_keyName, hvs); //
			long ll_2 = System.currentTimeMillis(); //
			logger.debug("为表[" + _tableName + "]重新注册了表型结构缓存,耗时[" + (ll_2 - ll_1) + "]毫秒"); //
			if (isfirst) {
				registeOtherServerInsCacheData("registeTableCacheData", new Class[] { String.class, String.class, String.class, Boolean.class }, new Object[] { _keyName, _tableName, _selCols, false });
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//注册树型缓存数据,如果有,则覆盖之
	public void registeTreeCacheData(String _keyName, String _tableName, String _idField, String _parentId, String _seqField) {
		registeTreeCacheData(_keyName, _tableName, _idField, _parentId, _seqField, true);
	}

	public void registeTreeCacheData(String _keyName, String _tableName, String _idField, String _parentId, String _seqField, Boolean isfirst) {
		try {
			long ll_1 = System.currentTimeMillis(); //
			CommDMO commDMO = new CommDMO(); //
			HashVO[] hvs = commDMO.getHashVoArrayAsTreeStructByDS(null, "select * from " + _tableName, _idField, _parentId, _seqField, null); //
			cacheData.put(_keyName, hvs); //
			long ll_2 = System.currentTimeMillis(); //
			logger.debug("为表[" + _tableName + "]重新注册了树型结构缓存,耗时[" + (ll_2 - ll_1) + "]毫秒"); //
			if (isfirst) {
				registeOtherServerInsCacheData("registeTreeCacheData", new Class[] { String.class, String.class, String.class, String.class, String.class, Boolean.class }, new Object[] { _keyName, _tableName, _idField, _parentId, _seqField, false });
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 刷新所有集群服务器的缓存
	 * @param _methodname
	 * @param _parclasses
	 * @param _parobjs
	 * @throws Exception
	 */
	public void registeOtherServerInsCacheData(final String _methodname, final Class[] _parclasses, final Object[] _parobjs) throws Exception {
		if (!"".equals(ServerEnvironment.getProperty("ClusterIPPorts", ""))) { // http://127.0.0.1:9001/psbc;http://127.0.0.1:9002/psbc的形式如果配置了集群的地址
			final String[] realadress = TBUtil.getTBUtil().split(ServerEnvironment.getProperty("ClusterIPPorts"), ";"); // 用户重复登陆那个问题也用到了这个参数
			new Thread(new Runnable() {
				public void run() {
					for (int i = 0; i < realadress.length; i++) {
						try {
							RemoteCallParVO parVO = new RemoteCallParVO();
							parVO.setMethodName(_methodname);
							parVO.setParClasses(_parclasses);
							parVO.setParObjs(_parobjs);
							HttpURLConnection conn = (HttpURLConnection) new URL(realadress[i] + "/WebCallServlet?StrParCallClassName=cn.com.infostrategy.bs.common.RegisteCacheDataMaService&isdispatch=Y").openConnection();
							if (realadress[i].startsWith("https")) {
								new BSUtil().addHttpsParam(conn);
							}
							conn.setDoInput(true);
							conn.setDoOutput(true);
							conn.setUseCaches(false);
							conn.setConnectTimeout(10000);//张珍龙改为了0，也不起作用，故改回原代码【李春娟/2018-06-25】
							conn.setReadTimeout(10000);
							conn.setRequestProperty("Content-type", "application/x-compress");
							byte[] bytes = serialize(parVO);
							conn.setRequestProperty("Content-Length", "" + bytes.length);
							OutputStream request_out = conn.getOutputStream();
							request_out.write(bytes);
							request_out.flush();
							request_out.close();
							InputStream intStream = conn.getInputStream();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}
	}

	//得到缓存数据
	public HashVO[] getCacheData(String _keyName) {
		HashVO[] hvs = (HashVO[]) cacheData.get(_keyName); //
		//logger.debug("从系统数据缓存ServerCacheDataFactory中取key为[" + _keyName + "]的缓存,共有[" + (hvs == null ? "0" : hvs.length) + "]条..");
		return hvs;
	}

	//取得表型结构数据,如果不存在的话还会自动创建
	public HashVO[] getCacheTableDataByAutoCreate(String _keyName, String _tableName) {
		return getCacheTableDataByAutoCreate(_keyName, _tableName, "*"); //
	}

	public HashVO[] getCacheTableDataByAutoCreate(String _keyName, String _tableName, String _selCols) {
		if (cacheData.containsKey(_keyName)) {
			return getCacheData(_keyName); //
		} else {
			registeTableCacheData(_keyName, _tableName, _selCols); //先注册,再取!!
			return getCacheData(_keyName); //
		}
	}

	//取得表型结构数据,如果不存在的话还会自动创建
	public HashVO[] getCacheTreeDataByAutoCreate(String _keyName, String _tableName, String _idField, String _parentId, String _seqField) {
		if (cacheData.containsKey(_keyName)) {
			return getCacheData(_keyName); //
		} else {
			registeTreeCacheData(_keyName, _tableName, _idField, _parentId, _seqField); //先注册一个树型数据!!!
			return getCacheData(_keyName); //
		}
	}

	//将常用的机构树,人员等做成固定的方法!!省得每次都要送参数!!
	public HashVO[] getCorpCacheDataByAutoCreate() {
		return getCacheTreeDataByAutoCreate("所有机构", "pub_corp_dept", "id", "parentid", "seq"); //
	}

	//删除某种类型的缓存!!
	public void clearCacheData(String _keyName) {
		cacheData.remove(_keyName); //
	}

	//重新注册机构数据!!
	public void registeCorpCacheData() {
		registeTreeCacheData("所有机构", "pub_corp_dept", "id", "parentid", "seq"); //
	}

	//重新注册机构数据!!
	public void registeUserCacheData() {
		registeTableCacheData("所有人员", "pub_user", "id,code,name"); //
	}

	//得到所有人员数据
	public HashVO[] getUserCacheDataByAutoCreate() {
		return getCacheTableDataByAutoCreate("所有人员", "pub_user", "id,code,name"); //
	}

	private byte[] serialize(Object _obj) {
		ByteArrayOutputStream buf = null;
		ObjectOutputStream out = null;
		try {
			buf = new ByteArrayOutputStream();
			out = new ObjectOutputStream(buf);
			out.writeObject(_obj);
			byte[] bytes = buf.toByteArray();
			return bytes;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null; //
		} finally {
			try {
				out.close();
			} catch (Exception e) {
			}
		}
	}

}
