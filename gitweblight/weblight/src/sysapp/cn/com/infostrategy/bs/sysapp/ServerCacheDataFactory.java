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
 * ���з����������ݻ���,�������,��Ա,��,��Ϊ�ڹ�����,���յȵط�,����Ƶ���õ���Щ����!!����Щ���������ر��,�Ҹ����ֲ�Ƶ��,�����ر��ʺ�������!!!
 * @author Administrator
 */
public class ServerCacheDataFactory {

	private static ServerCacheDataFactory factoryInstance = null;

	private HashMap cacheData = new HashMap(); //; //

	private Logger logger = WLTLogger.getLogger(ServerCacheDataFactory.class); //

	//������Ҫ���㼸������:
	//1.������С(�����ڴ�������)  2.Ƶ��ʹ��(����û��Ҫ)
	public static HashVO[] static_vos_allMenu = null; //
	public static HashVO[] static_vos_lookandfeel = null; //
	public static HashVO[] static_vos_corptypedef = null; //
	public static String[] static_str_commroles = null; //��һ���û�,��ͨԱ����ͨ�ý�ɫ!!!

	private ServerCacheDataFactory() {
	}

	public static ServerCacheDataFactory getInstance() {
		if (factoryInstance == null) {
			factoryInstance = new ServerCacheDataFactory();
		}
		return factoryInstance;
	}

	//ע����ͻ�������,�����,�򸲸�֮
	public void registeTableCacheData(String _keyName, String _tableName) {
		registeTableCacheData(_keyName, _tableName, "*"); //
	}

	//ע����ͻ�������,�����,�򸲸�֮
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
			logger.debug("Ϊ��[" + _tableName + "]����ע���˱��ͽṹ����,��ʱ[" + (ll_2 - ll_1) + "]����"); //
			if (isfirst) {
				registeOtherServerInsCacheData("registeTableCacheData", new Class[] { String.class, String.class, String.class, Boolean.class }, new Object[] { _keyName, _tableName, _selCols, false });
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//ע�����ͻ�������,�����,�򸲸�֮
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
			logger.debug("Ϊ��[" + _tableName + "]����ע�������ͽṹ����,��ʱ[" + (ll_2 - ll_1) + "]����"); //
			if (isfirst) {
				registeOtherServerInsCacheData("registeTreeCacheData", new Class[] { String.class, String.class, String.class, String.class, String.class, Boolean.class }, new Object[] { _keyName, _tableName, _idField, _parentId, _seqField, false });
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ˢ�����м�Ⱥ�������Ļ���
	 * @param _methodname
	 * @param _parclasses
	 * @param _parobjs
	 * @throws Exception
	 */
	public void registeOtherServerInsCacheData(final String _methodname, final Class[] _parclasses, final Object[] _parobjs) throws Exception {
		if (!"".equals(ServerEnvironment.getProperty("ClusterIPPorts", ""))) { // http://127.0.0.1:9001/psbc;http://127.0.0.1:9002/psbc����ʽ��������˼�Ⱥ�ĵ�ַ
			final String[] realadress = TBUtil.getTBUtil().split(ServerEnvironment.getProperty("ClusterIPPorts"), ";"); // �û��ظ���½�Ǹ�����Ҳ�õ����������
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
							conn.setConnectTimeout(10000);//��������Ϊ��0��Ҳ�������ã��ʸĻ�ԭ���롾���/2018-06-25��
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

	//�õ���������
	public HashVO[] getCacheData(String _keyName) {
		HashVO[] hvs = (HashVO[]) cacheData.get(_keyName); //
		//logger.debug("��ϵͳ���ݻ���ServerCacheDataFactory��ȡkeyΪ[" + _keyName + "]�Ļ���,����[" + (hvs == null ? "0" : hvs.length) + "]��..");
		return hvs;
	}

	//ȡ�ñ��ͽṹ����,��������ڵĻ������Զ�����
	public HashVO[] getCacheTableDataByAutoCreate(String _keyName, String _tableName) {
		return getCacheTableDataByAutoCreate(_keyName, _tableName, "*"); //
	}

	public HashVO[] getCacheTableDataByAutoCreate(String _keyName, String _tableName, String _selCols) {
		if (cacheData.containsKey(_keyName)) {
			return getCacheData(_keyName); //
		} else {
			registeTableCacheData(_keyName, _tableName, _selCols); //��ע��,��ȡ!!
			return getCacheData(_keyName); //
		}
	}

	//ȡ�ñ��ͽṹ����,��������ڵĻ������Զ�����
	public HashVO[] getCacheTreeDataByAutoCreate(String _keyName, String _tableName, String _idField, String _parentId, String _seqField) {
		if (cacheData.containsKey(_keyName)) {
			return getCacheData(_keyName); //
		} else {
			registeTreeCacheData(_keyName, _tableName, _idField, _parentId, _seqField); //��ע��һ����������!!!
			return getCacheData(_keyName); //
		}
	}

	//�����õĻ�����,��Ա�����ɹ̶��ķ���!!ʡ��ÿ�ζ�Ҫ�Ͳ���!!
	public HashVO[] getCorpCacheDataByAutoCreate() {
		return getCacheTreeDataByAutoCreate("���л���", "pub_corp_dept", "id", "parentid", "seq"); //
	}

	//ɾ��ĳ�����͵Ļ���!!
	public void clearCacheData(String _keyName) {
		cacheData.remove(_keyName); //
	}

	//����ע���������!!
	public void registeCorpCacheData() {
		registeTreeCacheData("���л���", "pub_corp_dept", "id", "parentid", "seq"); //
	}

	//����ע���������!!
	public void registeUserCacheData() {
		registeTableCacheData("������Ա", "pub_user", "id,code,name"); //
	}

	//�õ�������Ա����
	public HashVO[] getUserCacheDataByAutoCreate() {
		return getCacheTableDataByAutoCreate("������Ա", "pub_user", "id,code,name"); //
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
