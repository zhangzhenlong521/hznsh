package cn.com.infostrategy.bs.common;

import java.io.Serializable;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.Queue;
import cn.com.infostrategy.to.common.WLTLogger;

/**
 * ���������������й���,��ͻ��˲�һ��,���еĿͻ��˶���������ȡ!!���в�����ƿ���ܴ�!
 * ��û��ʲô���õİ취��?
 * @author xch
 *
 */
public class ServerSequenceFactory implements Serializable {

	private static final long serialVersionUID = -4971518506217700714L;

	private static ServerSequenceFactory factory = new ServerSequenceFactory();

	private static Hashtable ht = new Hashtable();

	private Logger logger = WLTLogger.getLogger(ServerSequenceFactory.class);

	//private long li_batchnumber = 20; //һ�δ����ݿ�ȡ����!!
	private CommDMO commDMO = null;

	private boolean isDebugLog = true; //
	private String str_index = cn.com.infostrategy.bs.common.SystemOptions.getStringValue("seq_prefix", null); //

	private ServerSequenceFactory() {
	}

	public final static ServerSequenceFactory getInstance() {
		return factory;
	}

	/**
	 * ȡ��һ������!!����Ϊ�ͻ��˵��õ�!!!
	 * @param _sequenceName
	 * @param _batch
	 * @return
	 */
	public synchronized long[] getBatchSequence(String _sequenceName, int _batch, long _dbbatch) {
		long[] ll_ids = new long[_batch];
		for (int i = 0; i < _batch; i++) {
			ll_ids[i] = getSequenceFromCache(_sequenceName, _dbbatch); //ѭ��ȥȡһ��������,һ�δ����ݿ�ȡ20��
		}
		return ll_ids;
	}

	public synchronized long getSequenceFromCache(String _dataSource, String _sequenceName, long _dbbatch) {
		return getSequenceFromCache(_dataSource, _sequenceName, _dbbatch, true); //
	}

	public synchronized long getSequenceFromCache(String _dataSource, String _sequenceName, long _dbbatch, boolean _isDebugLog) {
		this.isDebugLog = _isDebugLog; //
		long[] ll_newIds;
		try {
			ll_newIds = getSequenceFromDB(_dataSource, _sequenceName, _dbbatch);//ȥ���ݿ�ȡ��һ������!!!һ����10����!!
			long ll_beginID = ll_newIds[0]; //
			long ll_endID = ll_newIds[1]; //
			Queue queue = new Queue(); //����һ������
			if (str_index == null) {
				for (long i = ll_beginID; i <= ll_endID; i++) {
					queue.push(new Long(i)); //��ƨ�ɺ������!Ӧ����һ���Ӳ���20��!!
				}
			} else {
				for (long i = ll_beginID; i <= ll_endID; i++) {
					queue.push(new Long(str_index + i)); //��ƨ�ɺ������!Ӧ����һ���Ӳ���20��!!
				}
			}
			return ((Long) queue.pop()).longValue(); //�ٴ�ͷ��ȡһ������
		} catch (Exception e) { //
			e.printStackTrace();
			return 0;
		}
	}

	public synchronized long getSequenceFromCache(String _sequenceName, long _dbbatch) {
		return getSequenceFromCache(_sequenceName, _dbbatch, true); //
	}

	/**
	 * ȡ��һ�����е�ֵ,����Ϊ��������������õ�!!
	 * @param _sequenceName
	 * @return
	 */
	public synchronized long getSequenceFromCache(String _sequenceName, long _dbbatch, boolean _isDebugLog) {
		this.isDebugLog = _isDebugLog; //
		try {
			if (ht.containsKey(_sequenceName.toUpperCase())) { //�����������
				Queue queue = (Queue) ht.get(_sequenceName.toUpperCase()); //ȡ�ö�Ӧ�Ķ���!!
				if (queue.isEmpty()) { //����ǿյ�,��ȥԶ��ȡ!!
					long[] ll_newIds = getSequenceFromDB(_sequenceName, _dbbatch); //ȥ���ݿ�ȡ��һ������!!!һ����10����!!
					long ll_beginID = ll_newIds[0]; //
					long ll_endID = ll_newIds[1]; //
					if (str_index == null) {
						for (long i = ll_beginID; i <= ll_endID; i++) {
							queue.push(new Long(i)); //��ƨ�ɺ������!Ӧ����һ���Ӳ���20��!!
						}
					} else {
						for (long i = ll_beginID; i <= ll_endID; i++) {
							queue.push(new Long(str_index + i)); //��ƨ�ɺ������!Ӧ����һ���Ӳ���20��!!
						}
					}
					return ((Long) queue.pop()).longValue(); //�ٴ�ͷ��ȡһ������
				} else {
					return ((Long) queue.pop()).longValue(); //����ֵ
				}
			} else { //��������ж�û���������!!��Ҫ��������
				long[] ll_newIds = getSequenceFromDB(_sequenceName, _dbbatch); //
				long ll_beginID = ll_newIds[0]; //
				long ll_endID = ll_newIds[1]; //
				Queue queue = new Queue(); //����һ������
				if (str_index == null) {
					for (long i = ll_beginID; i <= ll_endID; i++) {
						queue.push(new Long(i)); //��ƨ�ɺ������!Ӧ����һ���Ӳ���20��!!
					}
				} else {
					for (long i = ll_beginID; i <= ll_endID; i++) {
						queue.push(new Long(str_index + i)); //��ƨ�ɺ������!Ӧ����һ���Ӳ���20��!!
					}
				}
				ht.put(_sequenceName.toUpperCase(), queue); //�����д����ϣ����!!!
				return ((Long) queue.pop()).longValue(); //�ٴ�ͷ��ȡһ������!!
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	private synchronized long[] getSequenceFromDB(String _sequenceName, long li_batchnumber) throws Exception {
		return getSequenceFromDB(ServerEnvironment.getInstance().getDefaultDataSourceName(), _sequenceName, li_batchnumber);
	}

	/**
	 * �����ݿ�ȡһ�����У��������ύ!!,���ǵ���Ⱥ,����ʹ�õݹ鷽ʽȡ!!!
	 * @param _sequenceName
	 * @return
	 * @throws Exception
	 */
	private synchronized long[] getSequenceFromDB(String _dataSource, String _sequenceName, long li_batchnumber) throws Exception {
		String str_datasourcename = _dataSource;//ServerEnvironment.getInstance().getDefaultDataSourceName();
		if (!ht.containsKey(_sequenceName.toUpperCase())) { //����ǵ�һ��,��������������,������һ�αȽ����������ݿ��е�ʵ��������С!
			//updateSequenceValueByMaxID(str_datasourcename, _sequenceName.toUpperCase()); //�޸�����ֵ,�Ա�֤����ֵһ���ȱ���ʵ��ֵ��!��Ϊ�����������ֳ�����ģ��ʱ���������ظ�,�ǳ�����!!!�������Ϊÿ���������м��������ֻ��һ��!
		}
		String str_currvalue_sql = "select currvalue from pub_sequence where sename='" + _sequenceName.toUpperCase() + "'"; //
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(str_datasourcename, str_currvalue_sql, isDebugLog); //
		if (hvs.length == 0) { //���û������,���Ȳ�������,һ��Ҫ��sename��Ψһ��Լ��!!!!!
			try {
				String str_insert_sql = "insert into pub_sequence (sename,currvalue) select '" + _sequenceName.toUpperCase() + "','0' from wltdual where not exists (select '1' from pub_sequence where sename='" + _sequenceName.toUpperCase() + "')"; //����0,��Ϊ��Ⱥʱ���ܻ��������,��ʹ���Ӳ�ѯУ��,���������Ȳ�����,�����׳��쳣,����Ҫ���쳣�Ե�!!!!
				getCommDMO().executeUpdateByDSImmediately(str_datasourcename, str_insert_sql, isDebugLog); //�����ʼ����ֵ
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
			return getSequenceByCycle(str_datasourcename, _sequenceName.toUpperCase(), 0, 0 + li_batchnumber, li_batchnumber); //
		} else {
			long ll_oldVvalue = Long.parseLong(hvs[0].getStringValue("currvalue").trim()); //ũ��һͼ�����õ�sybase���ݿ⣬�ֶι涨ֻ��Ϊchar��varchar���ͣ���������ǰ�Ĵ���hvs[0].getLognValue("currvalue").longValue(); �ᱨ��
			long ll_newvalue = ll_oldVvalue + li_batchnumber; //��ǰ��20��,��0+20
			return getSequenceByCycle(str_datasourcename, _sequenceName.toUpperCase(), ll_oldVvalue, ll_newvalue, li_batchnumber); //�µ�ֵ!!,���ǵ���Ⱥʱ����ѭ��!!����������ظ�,����ʹ����ѭ���ķ�ʽȡ!!!
		}
	}

	//��ѭ��ȡ������!!!
	private synchronized long[] getSequenceByCycle(String _datasourcename, String _seName, long _oldValue, long _newValue, long _li_batchnumber) throws Exception {
		String str_update_sql = "update pub_sequence set currvalue='" + _newValue + "' where sename='" + _seName.toUpperCase() + "' and currvalue='" + _oldValue + "'"; //�ұ����Ǵ������õ���������¼,����������õ�������,��˵������ļ�Ⱥ����������������,���һ�Ҫ������һ����!�����Ͳ��ᷢ���ظ���������!!
		int li_result = getCommDMO().executeUpdateByDSImmediately(_datasourcename, str_update_sql, isDebugLog); //��������
		if (li_result == 0) { //�����Ȼû���µ�,��˵���Ǳ�����������,�����ȡһ����,Ȼ���ټ�������!!!
			String str_currvalue_sql = "select currvalue from pub_sequence where sename='" + _seName + "'"; //ȡ�ö�Ӧ�ļ�¼!!!!
			HashVO[] hvs = getCommDMO().getHashVoArrayByDS(_datasourcename, str_currvalue_sql, isDebugLog); //
			long ll_oldvalue = Long.parseLong(hvs[0].getStringValue("currvalue").trim()); //����Ǽ�Ⱥʱ,���ĳ����������ִ�е����ʱ,��һ��������Ҳȥȡ����,����ܻ�ȡ��ͬ����ֵ,�����ܻ���������ظ�������!!!
			long ll_newvalue = ll_oldvalue + _li_batchnumber; //��ǰ��20��,��0+20
			return getSequenceByCycle(_datasourcename, _seName, ll_oldvalue, ll_newvalue, _li_batchnumber); //�ݹ����,ֱ���ҵ�����Ҫ��!!!!!!!��ؼ��ĵط�!!!!!
		} else {
			return new long[] { _oldValue + 1, _newValue }; //������µ���,��˵���ǶԵ�!!!
		}
	}

	//Ϊ�˷�ֹ�����ظ�,�ȴӱ�ȡһ��,�ҳ�����IDȻ���ٷ�д���б�!!!!
	private void updateSequenceValueByMaxID(String _dsname, String _sename) {
		try {
			String str_tablename = _sename.substring(2, _sename.length()); //�ҳ�����
			String str_pkfieldName = null;
			if (str_tablename.equalsIgnoreCase("PUB_TEMPLET_1")) {
				str_pkfieldName = "pk_pub_templet_1"; //
			} else if (str_tablename.equalsIgnoreCase("PUB_TEMPLET_1_ITEM")) {
				str_pkfieldName = "pk_pub_templet_1_item"; //
			} else if (str_tablename.equalsIgnoreCase("PUB_COMBOBOXDICT")) {
				str_pkfieldName = "pk_pub_comboboxdict"; //
			} else {
				str_pkfieldName = getCommDMO().getStringValueByDS(_dsname, "select pkname from pub_templet_1 where tablename='" + str_tablename + "'");
				if (str_pkfieldName == null) {
					str_pkfieldName = "id"; //Ĭ��������ID.
				}
			}

			logger.debug("��һ��Ϊ����[" + _sename.toUpperCase() + "]�����Ա�֤�ȱ��[" + str_tablename + "." + str_pkfieldName + "]��"); //
			String str_maxid = getCommDMO().getStringValueByDS(_dsname, "select max(" + str_pkfieldName + ") from " + str_tablename); //�ҳ�����Ŀǰ����ʵ��IDֵ!!
			if (str_maxid != null && !str_maxid.trim().equals("")) { //���������������,��������������
				long ll_maxid = Long.parseLong(str_maxid); //
				String str_securrvalue = getCommDMO().getStringValueByDS(_dsname, "select currvalue from pub_sequence where sename='" + _sename.toUpperCase() + "'"); //�ҳ�����Ŀǰ����ʵ��IDֵ!!
				if (str_securrvalue != null) { //����Ѿ�ע�������,��Ƚ����ߴ�С!
					long ll_securrvalue = Long.parseLong(str_securrvalue); //
					if (ll_maxid > ll_securrvalue) { //������ݿ��е�ʵ��ֵ���д���,���޸�����ֵ!��֮��������!!!
						getCommDMO().executeUpdateByDSImmediately(_dsname, "update pub_sequence set currvalue='" + (ll_maxid + 1) + "' where sename='" + _sename.toUpperCase() + "'"); //��������,sybase���ݿ��ϸ����ִ�Сд�����Ա������ֶ�������ΪСд����
						logger.debug("��������[" + _sename.toUpperCase() + "]ֵ�Ա�֤��[" + str_tablename + "." + str_pkfieldName + "]ֵ��,����ԭֵ[" + ll_securrvalue + "],����ID���[" + ll_maxid + "]!"); //
					}
				} else { //�����ûע�������,��ֱ������һ���ȵ�ǰʵ�ʱ��д�1��ֵ!
					getCommDMO().executeUpdateByDSImmediately(_dsname, "insert into pub_sequence (sename,currvalue) values ('" + _sename.toUpperCase() + "','" + (ll_maxid + 1) + "')"); //��������
					logger.debug("��������[" + _sename.toUpperCase() + "]ֵ�Ա�֤��[" + str_tablename + "." + str_pkfieldName + "]ֵ��,����ԭ��ֵ,����ID���[" + ll_maxid + "]!"); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private CommDMO getCommDMO() {
		if (commDMO != null) {
			return commDMO; //
		}
		commDMO = ServerEnvironment.getCommDMO(); //
		return commDMO;
	}
}
