package cn.com.infostrategy.bs.common;

import java.io.Serializable;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.Queue;
import cn.com.infostrategy.to.common.WLTLogger;

/**
 * 服务器端主健序列工厂,与客户端不一样,所有的客户端都将从这里取!!所有并发的瓶颈很大!
 * 有没有什么更好的办法呢?
 * @author xch
 *
 */
public class ServerSequenceFactory implements Serializable {

	private static final long serialVersionUID = -4971518506217700714L;

	private static ServerSequenceFactory factory = new ServerSequenceFactory();

	private static Hashtable ht = new Hashtable();

	private Logger logger = WLTLogger.getLogger(ServerSequenceFactory.class);

	//private long li_batchnumber = 20; //一次从数据库取多少!!
	private CommDMO commDMO = null;

	private boolean isDebugLog = true; //
	private String str_index = cn.com.infostrategy.bs.common.SystemOptions.getStringValue("seq_prefix", null); //

	private ServerSequenceFactory() {
	}

	public final static ServerSequenceFactory getInstance() {
		return factory;
	}

	/**
	 * 取得一批主键!!这是为客户端调用的!!!
	 * @param _sequenceName
	 * @param _batch
	 * @return
	 */
	public synchronized long[] getBatchSequence(String _sequenceName, int _batch, long _dbbatch) {
		long[] ll_ids = new long[_batch];
		for (int i = 0; i < _batch; i++) {
			ll_ids[i] = getSequenceFromCache(_sequenceName, _dbbatch); //循环去取一个个主键,一次从数据库取20个
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
			ll_newIds = getSequenceFromDB(_dataSource, _sequenceName, _dbbatch);//去数据库取到一批数据!!!一般是10左右!!
			long ll_beginID = ll_newIds[0]; //
			long ll_endID = ll_newIds[1]; //
			Queue queue = new Queue(); //创建一个队列
			if (str_index == null) {
				for (long i = ll_beginID; i <= ll_endID; i++) {
					queue.push(new Long(i)); //从屁股后面插入!应该是一下子插入20个!!
				}
			} else {
				for (long i = ll_beginID; i <= ll_endID; i++) {
					queue.push(new Long(str_index + i)); //从屁股后面插入!应该是一下子插入20个!!
				}
			}
			return ((Long) queue.pop()).longValue(); //再从头部取一个返回
		} catch (Exception e) { //
			e.printStackTrace();
			return 0;
		}
	}

	public synchronized long getSequenceFromCache(String _sequenceName, long _dbbatch) {
		return getSequenceFromCache(_sequenceName, _dbbatch, true); //
	}

	/**
	 * 取得一个序列的值,这是为服务器其他类调用的!!
	 * @param _sequenceName
	 * @return
	 */
	public synchronized long getSequenceFromCache(String _sequenceName, long _dbbatch, boolean _isDebugLog) {
		this.isDebugLog = _isDebugLog; //
		try {
			if (ht.containsKey(_sequenceName.toUpperCase())) { //如果缓存中有
				Queue queue = (Queue) ht.get(_sequenceName.toUpperCase()); //取得对应的对列!!
				if (queue.isEmpty()) { //如果是空的,则去远程取!!
					long[] ll_newIds = getSequenceFromDB(_sequenceName, _dbbatch); //去数据库取到一批数据!!!一般是10左右!!
					long ll_beginID = ll_newIds[0]; //
					long ll_endID = ll_newIds[1]; //
					if (str_index == null) {
						for (long i = ll_beginID; i <= ll_endID; i++) {
							queue.push(new Long(i)); //从屁股后面插入!应该是一下子插入20个!!
						}
					} else {
						for (long i = ll_beginID; i <= ll_endID; i++) {
							queue.push(new Long(str_index + i)); //从屁股后面插入!应该是一下子插入20个!!
						}
					}
					return ((Long) queue.pop()).longValue(); //再从头部取一个返回
				} else {
					return ((Long) queue.pop()).longValue(); //返回值
				}
			} else { //如果缓存中都没有这个对列!!则要创建队列
				long[] ll_newIds = getSequenceFromDB(_sequenceName, _dbbatch); //
				long ll_beginID = ll_newIds[0]; //
				long ll_endID = ll_newIds[1]; //
				Queue queue = new Queue(); //创建一个队列
				if (str_index == null) {
					for (long i = ll_beginID; i <= ll_endID; i++) {
						queue.push(new Long(i)); //从屁股后面插入!应该是一下子插入20个!!
					}
				} else {
					for (long i = ll_beginID; i <= ll_endID; i++) {
						queue.push(new Long(str_index + i)); //从屁股后面插入!应该是一下子插入20个!!
					}
				}
				ht.put(_sequenceName.toUpperCase(), queue); //将队列存入哈希缓存!!!
				return ((Long) queue.pop()).longValue(); //再从头部取一个返回!!
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
	 * 从数据库取一批序列，并立即提交!!,考虑到集群,必须使用递归方式取!!!
	 * @param _sequenceName
	 * @return
	 * @throws Exception
	 */
	private synchronized long[] getSequenceFromDB(String _dataSource, String _sequenceName, long li_batchnumber) throws Exception {
		String str_datasourcename = _dataSource;//ServerEnvironment.getInstance().getDefaultDataSourceName();
		if (!ht.containsKey(_sequenceName.toUpperCase())) { //如果是第一次,即不包含队列名,则先做一次比较序列与数据库中的实际主键大小!
			//updateSequenceValueByMaxID(str_datasourcename, _sequenceName.toUpperCase()); //修改序列值,以保证序列值一定比表中实际值大!因为经常发生到现场导入模板时发生主键重复,非常烦人!!!这个动作为每个序列在中间件启动后只做一次!
		}
		String str_currvalue_sql = "select currvalue from pub_sequence where sename='" + _sequenceName.toUpperCase() + "'"; //
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(str_datasourcename, str_currvalue_sql, isDebugLog); //
		if (hvs.length == 0) { //如果没有数据,则先插入数据,一定要对sename做唯一性约束!!!!!
			try {
				String str_insert_sql = "insert into pub_sequence (sename,currvalue) select '" + _sequenceName.toUpperCase() + "','0' from wltdual where not exists (select '1' from pub_sequence where sename='" + _sequenceName.toUpperCase() + "')"; //插入0,因为集群时可能会造成抢先,故使用子查询校验,即有人抢先插入了,所以抛出异常,所以要将异常吃掉!!!!
				getCommDMO().executeUpdateByDSImmediately(str_datasourcename, str_insert_sql, isDebugLog); //插入初始化的值
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
			return getSequenceByCycle(str_datasourcename, _sequenceName.toUpperCase(), 0, 0 + li_batchnumber, li_batchnumber); //
		} else {
			long ll_oldVvalue = Long.parseLong(hvs[0].getStringValue("currvalue").trim()); //农行一图两表，用的sybase数据库，字段规定只能为char和varchar类型，这里用以前的代码hvs[0].getLognValue("currvalue").longValue(); 会报错！
			long ll_newvalue = ll_oldVvalue + li_batchnumber; //往前加20个,即0+20
			return getSequenceByCycle(str_datasourcename, _sequenceName.toUpperCase(), ll_oldVvalue, ll_newvalue, li_batchnumber); //新的值!!,考虑到集群时的死循环!!会造成主键重复,所以使用死循环的方式取!!!
		}
	}

	//死循环取得数据!!!
	private synchronized long[] getSequenceByCycle(String _datasourcename, String _seName, long _oldValue, long _newValue, long _li_batchnumber) throws Exception {
		String str_update_sql = "update pub_sequence set currvalue='" + _newValue + "' where sename='" + _seName.toUpperCase() + "' and currvalue='" + _oldValue + "'"; //我必须是处理我拿到的那条记录,如果不是我拿到的那条,则说明被别的集群服务器抢先拿走了,则我还要继续下一个找!这样就不会发生重复的问题了!!
		int li_result = getCommDMO().executeUpdateByDSImmediately(_datasourcename, str_update_sql, isDebugLog); //立即更新
		if (li_result == 0) { //如果竟然没更新到,则说明是被别人抢走了,则从新取一下数,然后再继续调用!!!
			String str_currvalue_sql = "select currvalue from pub_sequence where sename='" + _seName + "'"; //取得对应的记录!!!!
			HashVO[] hvs = getCommDMO().getHashVoArrayByDS(_datasourcename, str_currvalue_sql, isDebugLog); //
			long ll_oldvalue = Long.parseLong(hvs[0].getStringValue("currvalue").trim()); //如果是集群时,如果某个服务器正执行到这儿时,另一个服务器也去取数了,则可能会取到同样的值,即可能会造成主键重复的问题!!!
			long ll_newvalue = ll_oldvalue + _li_batchnumber; //往前加20个,即0+20
			return getSequenceByCycle(_datasourcename, _seName, ll_oldvalue, ll_newvalue, _li_batchnumber); //递归调用,直到找到我想要的!!!!!!!最关键的地方!!!!!
		} else {
			return new long[] { _oldValue + 1, _newValue }; //如果更新到了,则说明是对的!!!
		}
	}

	//为了防止主键重复,先从表取一下,找出最大的ID然后再反写序列表!!!!
	private void updateSequenceValueByMaxID(String _dsname, String _sename) {
		try {
			String str_tablename = _sename.substring(2, _sename.length()); //找出表名
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
					str_pkfieldName = "id"; //默认主键叫ID.
				}
			}

			logger.debug("第一次为序列[" + _sename.toUpperCase() + "]处理以保证比表格[" + str_tablename + "." + str_pkfieldName + "]大"); //
			String str_maxid = getCommDMO().getStringValueByDS(_dsname, "select max(" + str_pkfieldName + ") from " + str_tablename); //找出表中目前最大的实际ID值!!
			if (str_maxid != null && !str_maxid.trim().equals("")) { //如果表中已有数据,即表中已有数据
				long ll_maxid = Long.parseLong(str_maxid); //
				String str_securrvalue = getCommDMO().getStringValueByDS(_dsname, "select currvalue from pub_sequence where sename='" + _sename.toUpperCase() + "'"); //找出表中目前最大的实际ID值!!
				if (str_securrvalue != null) { //如果已经注册过序列,则比较两者大小!
					long ll_securrvalue = Long.parseLong(str_securrvalue); //
					if (ll_maxid > ll_securrvalue) { //如果数据库中的实际值序列大了,则修改序列值!反之则不做处理!!!
						getCommDMO().executeUpdateByDSImmediately(_dsname, "update pub_sequence set currvalue='" + (ll_maxid + 1) + "' where sename='" + _sename.toUpperCase() + "'"); //立即更新,sybase数据库严格区分大小写，所以表名和字段名都改为小写的了
						logger.debug("更新序列[" + _sename.toUpperCase() + "]值以保证比[" + str_tablename + "." + str_pkfieldName + "]值大,序列原值[" + ll_securrvalue + "],表中ID最大[" + ll_maxid + "]!"); //
					}
				} else { //如果还没注册过序列,则直接新增一个比当前实际表中大1的值!
					getCommDMO().executeUpdateByDSImmediately(_dsname, "insert into pub_sequence (sename,currvalue) values ('" + _sename.toUpperCase() + "','" + (ll_maxid + 1) + "')"); //立即更新
					logger.debug("新增序列[" + _sename.toUpperCase() + "]值以保证比[" + str_tablename + "." + str_pkfieldName + "]值大,序列原无值,表中ID最大[" + ll_maxid + "]!"); //
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
