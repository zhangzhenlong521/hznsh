package cn.com.pushworld.salary.bs.dinterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.pushworld.salary.to.SalaryTBUtil;

/**
 * 数据接口服务器端通用方法。
 * 
 * @author haoming create by 2014-7-2
 */
public class DataInterfaceDMO extends AbstractDMO {
	private TBUtil tbutil = new TBUtil();
	private CommDMO dmo = new CommDMO();
	private SalaryTBUtil stbutil = new SalaryTBUtil();

	public static final String Syn_Log_Exec_Type_TBSJ = "同步数据"; // 数据同步;删除数据;创建表;修改表;
	public static final String Syn_Log_Exec_Type_SCSJ = "删除数据";
	public static final String Syn_Log_Exec_Type_CJB = "创建表";
	public static final String Syn_Log_Exec_Type_XGB = "修改表";
	public static final String Syn_Log_Exec_Type_Other = "其他"; // 这个标识，可能需要做一个系统中配置的表，和数据下发的文件对比表。看看那个多，那个少。

	public static final String Syn_Log_Convert_Type_SJZH = "数据转换"; // 数据转换;备份数据;删除数据;创建表;
	public static final String Syn_Log_Convert_Type_BFSJ = "备份数据";
	public static final String Syn_Log_Convert_Type_SCSJ = "删除数据";
	public static final String Syn_Log_Convert_Type_CJB = "创建表";

	public static final String Syn_Log_Exec_State_Success = "成功";
	public static final String Syn_Log_Exec_State_Fail = "失败";

	protected String ftppath = tbutil
			.getSysOptionStringValue("数据接口FTP路径", null);
	protected String datacode = tbutil.getSysOptionStringValue("数据接口编码格式",
			"GBK");
	protected boolean flg = tbutil.getSysOptionBooleanValue("接口配置是否加入deptid",
			false);// zzl[2017/6/2]
	
	protected boolean soleflg = tbutil.getSysOptionBooleanValue("数据接口设置是否添加唯一字段ID",
			false);// zzl[2018-10-25]
	
	protected String splitStr = tbutil.getSysOptionStringValue("数据接口导入数据的分隔符号",
			";").trim();// zzl[2018-10-25]

	/*
	 * 根据接口配置接口，手工执行创建表，
	 */
	public void createTableByConfig(String _dataIFCmainID) throws Exception {
		createTableByConfig(_dataIFCmainID, "", "");
	}

	/*
	 * 创建数据表核心类
	 */
	private void createTableByConfig(String _dataIFCmainID, String _jobid,
			String _datadate) throws Exception {
		HashVO data_ifc_mainVOs[] = dmo.getHashVoArrayByDS(null,
				"select * from sal_data_interface_main where id = "
						+ _dataIFCmainID); // 找到接口主表配置
		if (data_ifc_mainVOs.length == 0) {
			throw new Exception("传入的接口同步主表不存在主键为" + _dataIFCmainID
					+ "的配置项,请检查!");
		}
		HashVO[] data_ifc_item_config = dmo.getHashVoArrayByDS(null,
				"select * from sal_data_interface_item_conf where parentid = "
						+ _dataIFCmainID + " order by seq");// 接口字段配置

		if (data_ifc_item_config.length == 0) {
			throw new Exception("接口没有配置表结构.");
		}
		String str_dbtype = ServerEnvironment.getDefaultDataSourceType(); // 数据源类型!关键
		String str_tname = data_ifc_mainVOs[0].getStringValue("tablename"); // 表名
		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append("create table " + str_tname + "("); //
		sb_sql.append(" logid decimal(22),");
		if (flg) {
			sb_sql.append(" deptid decimal(22),");
		}
		if(soleflg){
			sb_sql.append(" id decimal(22) primary key,");//zzl[2018-11-23] 主键索引会降低插入的速度
		}
		List indexItem = new ArrayList();
		StringBuffer msg = new StringBuffer();
		for (int i = 0; i < data_ifc_item_config.length; i++) {
			HashVO itemvo = data_ifc_item_config[i];
			String str_colName = stbutil.convertIntColToEn(i + 1,false); //
			String str_colType = itemvo.getStringValue("itemtype"); // 类型,比如varchar,decimal
			String str_colLength = itemvo.getStringValue("itemwidth"); //
			String itemChinaName = itemvo.getStringValue("itemname");
			String isindex = itemvo.getStringValue("ifindex");
			sb_sql.append(str_colName + " "
					+ convertRealColType(str_colType, str_dbtype) + "("
					+ str_colLength + ") "); //
			if (str_dbtype.equalsIgnoreCase("MYSQL")
					&& !tbutil.isEmpty(itemChinaName)) {
				sb_sql.append(" comment '" + itemChinaName + "'");
			}
			if (i != data_ifc_item_config.length - 1) { // 如果没有定义主键,且又是最后一个
				sb_sql.append(",\r\n"); // //
			}
			if ("Y".equals(isindex)) {
				indexItem.add(str_colName);
			}
		}
		sb_sql.append(")"); //
		if (str_dbtype.equalsIgnoreCase("MYSQL")) {
			sb_sql.append(" DEFAULT CHARSET = GBK"); //
			sb_sql.append(" comment '"
					+ data_ifc_mainVOs[0].getStringValue("iname") + "'"); //
		}
		// if (str_dbtype.equalsIgnoreCase("DB2")) {
		// sb_sql.append(" IN PUSHSPACE"); //
		// }

		String str_createsql = sb_sql.toString(); //
		if (str_dbtype.equalsIgnoreCase("DB2")) {
			str_createsql = str_createsql.toUpperCase(); //
		}
		dmo.executeUpdateByDS(null, str_createsql); //
		
		msg.append(str_createsql);
		// 创建索引!!!
		if (indexItem != null && indexItem.size() > 0) { // 可能没定义索引
			for (int j = 0; j < indexItem.size(); j++) {
				String str_indexCols = (String) indexItem.get(j); //
				String str_indexName = "PK_" + str_tname.toUpperCase() + "_"
						+ j;
				String str_indexsql = "create index " + str_indexName + " on "
						+ str_tname + "(" + str_indexCols + ")"; //
				str_indexsql = str_indexsql.trim(); //
				if (str_dbtype.equalsIgnoreCase("DB2")) {
					str_indexsql = str_indexsql.toUpperCase(); // 转大写!!
				}
				dmo.executeUpdateByDS(null, str_indexsql); //
				msg.append(";\r\n" + str_indexsql);
			}
		}
		String logid = dmo.getSequenceNextValByDS(null,
				"S_SAL_DATA_INTERFACE_LOG");
		InsertSQLBuilder loginsertsql = getSynLog(logid, _dataIFCmainID,
				_jobid, "", data_ifc_mainVOs[0].getStringValue("iname"), "",
				Syn_Log_Exec_Type_CJB, Syn_Log_Exec_State_Success, "表"
						+ str_tname + "创建成功;\r\n执行语句" + msg.toString());
		UpdateSQLBuilder updateMainTable = new UpdateSQLBuilder(
				"sal_data_interface_main");
		updateMainTable.setWhereCondition(" id=" + _dataIFCmainID);
		updateMainTable.putFieldValue("lasttime", tbutil.getCurrTime());
		dmo.executeBatchByDSImmediately(null, new String[] {
				loginsertsql.getSQL(), updateMainTable.getSQL() }, false);
	}

	/*
	 * 创建接口同步日志
	 */
	private InsertSQLBuilder getSynLog(String _id, String _parentid,
			String _jobid, String _dataDate, String _name, String _filepath,
			String _exectype, String _state, String descr) throws Exception {
		InsertSQLBuilder loginsertsql = new InsertSQLBuilder(
				"sal_data_interface_log");
		if (tbutil.isEmpty(_id)) {
			_id = dmo.getSequenceNextValByDS(null, "S_SAL_DATA_INTERFACE_LOG");
		}
		loginsertsql.putFieldValue("id", _id);
		loginsertsql.putFieldValue("parentid", _parentid);
		loginsertsql.putFieldValue("jobid", _jobid);
		loginsertsql.putFieldValue("datadate", _dataDate);
		loginsertsql.putFieldValue("exectime", tbutil.getCurrTime());
		loginsertsql.putFieldValue("iname", _name); // 接口名称
		loginsertsql.putFieldValue("filepath", _filepath);// 文件路径
		loginsertsql.putFieldValue("exectype", _exectype);
		loginsertsql.putFieldValue("state", _state);
		if (descr != null && descr.length() > 1950) {
			descr = descr.substring(0, 1950) + "...";
		}
		loginsertsql.putFieldValue("descr", descr);
		return loginsertsql;
	}

	/*
	 * 从数据上传目录读取数据并插入到数据库 记录读取日志 _currFolder 是数据下发平台创建的文件。
	 * zzl[同步的核心数据   有需要部门的导入数据方法九台计算风险指标    还有减值系统只需要导入计算减值的数据]
	 */
	public void readDataFromFile(String _dataIFCmainID, String _dataDate,
			String _jobid) throws Exception {
		HashVO data_ifc_mainVOs[] = dmo.getHashVoArrayByDS(null,
				"select * from sal_data_interface_main where id = "
						+ _dataIFCmainID); //
		if (data_ifc_mainVOs.length == 0) {
			throw new Exception("传入的接口同步主表不存在主键为" + _dataIFCmainID
					+ "的配置项,请检查!");
		}

		HashVO[] data_ifc_item_config = dmo.getHashVoArrayByDS(null,
				"select * from sal_data_interface_item_conf where parentid = "
						+ _dataIFCmainID);
				syn_data_from_file(data_ifc_mainVOs[0], data_ifc_item_config,
					_dataDate, _jobid);


	}

	/*
	 * 同步数据核心逻辑
	 * zzl  加入区分ID  区分DEPTID
	 */
	private void syn_data_from_file(HashVO _mainVO, HashVO[] _childItemVOs,
			String _datadate, String _jobid) throws Exception {
		if (tbutil.isEmpty(ftppath)) {
			throw new Exception("系统参数[数据接口FTP路径]为空，请配置!");
		}
		String _folder = _datadate.replace("-", ""); // 传入的是日期，截取为同步目录
		String parentFolder = _folder.substring(0, 6);
		String folderAbsolutePath = tbutil.replaceAll(ftppath, "\\",
				File.separator)
				+ File.separator
				+ parentFolder
				+ File.separator
				+ _folder
				+ File.separator; //
		String fileAbsolutePath = folderAbsolutePath+ _mainVO.getStringValue("filename");
		if (flg) {
			// 看看有没有历史数据有的话，先删除掉。然后在执行.
			HashVO[] old_his = dmo.getHashVoArrayByDS(null,
					"select * from sal_data_interface_log where filepath like '%"
							+ _folder + "%' and exectype='"
							+ Syn_Log_Exec_Type_TBSJ + "' and parentid="
							+ _mainVO.getStringValue("id")
							+ " order by exectime desc"); // 检查最近同步的记录
			if (old_his != null && old_his.length > 0) {
				List list = new ArrayList();
				StringBuffer msg = new StringBuffer();
				for (int i = 0; i < old_his.length; i++) {
					HashVO loghisvo = old_his[i];
					msg.append(loghisvo.getStringValue("exectime") + ",");
					String deloldsql = "delete from "
							+ _mainVO.getStringValue("tablename")
							+ " where logid = " + loghisvo.getStringValue("id");
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>"+deloldsql);
					list.add(deloldsql);
				}
				String newlogid = dmo.getSequenceNextValByDS(null,
						"S_SAL_DATA_INTERFACE_LOG");
				InsertSQLBuilder dellogsql = getSynLog(newlogid, _mainVO
						.getStringValue("id"), _jobid, _datadate, _mainVO
						.getStringValue("iname"), "", Syn_Log_Exec_Type_SCSJ,
						Syn_Log_Exec_State_Success, "系统发现该接口已经在["
								+ msg.substring(0, msg.length() - 1)
								+ "]同步过,先清理历史数据.");
				list.add(dellogsql);
				dmo.executeBatchByDSImmediately(null, list);
			}
			ArrayList a = getFiles(folderAbsolutePath);
			if(a.size()<=0){
				throw new Exception("文件不存在,可能没上传");
			}
			for (int f = 0; f < a.size(); f++) {
				File filesum = (File) a.get(f);
				fileAbsolutePath=folderAbsolutePath+"/"+filesum.getName()+"/"+ _mainVO.getStringValue("filename");
		File file = new File(fileAbsolutePath);
//		if (!file.exists()) {
//			throw new Exception(fileAbsolutePath + "文件不存在，能需要还未上传，或双方接口配置不一致.");
//		}
		String str = tbutil.readFromInputStreamToStr(new FileInputStream(file));
		String[] allline = str.split("\n");
//		for (int i = 0; i < allline.length; i++) {
//			String values[] = allline[i].split(splitStr);
//			for (int j = 0; j < values.length; j++) {
//				System.out.println(values[j]);
//			}
//		}
		InputStreamReader read = new InputStreamReader(
				new FileInputStream(file), datacode);
		BufferedReader filereader = new BufferedReader(read);
		String strcontent = null;

		String tableName = _mainVO.getStringValue("tablename");
		// 检测表是否存在,如果存在判断结构是否变化
		try {
			dmo.getStringValueByDS(null, "select * from " + tableName
					+ " where 1=2");
			String[][] different = compareDBTOConfig(_mainVO, _childItemVOs);// 看表结构有没有变化，一般是不变的。
			if (different != null && different.length > 0) {
				List sql = new ArrayList();
				StringBuffer descr = new StringBuffer();
				for (int i = 0; i < different.length; i++) {
					sql.add(different[i][3]);
					descr.append(different[i][3] + "\r\n");
				}
				dmo.executeBatchByDSImmediately(null, sql, false); //
				InsertSQLBuilder logsql = getSynLog(null, _mainVO
						.getStringValue("id"), _jobid, _datadate, _mainVO
						.getStringValue("iname"), "", Syn_Log_Exec_Type_XGB,
						Syn_Log_Exec_State_Success, "系统发现表结构变化，自动执行脚本:\r\n"
								+ descr);
				dmo.executeUpdateByDSImmediately(null, logsql);
			}
		} catch (Exception ex) { // 不存在，直接创建
			createTableByConfig(_mainVO.getStringValue("id"));
		}
		ArrayList list = new ArrayList();
		//
		int rowcount = 0; // 记录生成多少条数据.
		String logid = dmo.getSequenceNextValByDS(null,
				"S_SAL_DATA_INTERFACE_LOG");
		String deptid=dmo.getStringValueByDS(null,"select id from pub_corp_dept where code='"+filesum.getName()+"'");
		String countid=null;
		if(soleflg ){
			countid=dmo.getStringValueByDS(null,"select max(id) from "+tableName);
			if(countid.equals(null) || countid==null || countid.equals("") || countid.equals(" "))
				countid="0";
			}else{
				countid=String.valueOf(Integer.parseInt(countid)+10);
		}
		int countvalue=Integer.parseInt(countid);
		while ((strcontent = filereader.readLine()) != null) { // 按行读取。以下逻辑可以用策略替换，可以按行读取，也可以是xml读取。
			String itemvalues[] = strcontent.split(splitStr); // 截取实际值
			rowcount++;
			if (itemvalues.length != _childItemVOs.length) {
				filereader.close(); // 一定要关闭流
				InsertSQLBuilder inisert = getSynLog(logid, _mainVO
						.getStringValue("id"), _jobid, _datadate, _mainVO
						.getStringValue("iname"), fileAbsolutePath,
						Syn_Log_Exec_Type_Other, Syn_Log_Exec_State_Fail,
						fileAbsolutePath + "文件字段长度和系统配置不一直.文件["
								+ itemvalues.length + "]列,系统配置["
								+ _childItemVOs.length + "]列");
				dmo.executeUpdateByDSImmediately(null, inisert);
				throw new Exception(fileAbsolutePath + "文件字段长度和系统配置不一直.文件["
						+ itemvalues.length + "]列,系统配置[" + _childItemVOs.length
						+ "]列");
			}
			InsertSQLBuilder insert = new InsertSQLBuilder(tableName);
			insert.putFieldValue("logid", logid);
			insert.putFieldValue("deptid", deptid);
			if(soleflg){
				countvalue++;		
				insert.putFieldValue("id", countvalue);
			}
			for (int i = 0; i < itemvalues.length; i++) {
				String value = itemvalues[i];
				value = value.replace("\"", "").trim();
				insert.putFieldValue(stbutil.convertIntColToEn(i + 1,false), value);
			}
			list.add(insert);
			if (list.size() == 5000) { // 5000行提交一次。否则性能跟不上
				dmo.executeBatchByDSImmediately(null, list, false);
				list.clear();
			}
		}
		filereader.close();
		InsertSQLBuilder inisert = getSynLog(logid, _mainVO
				.getStringValue("id"), _jobid, _datadate, _mainVO
				.getStringValue("iname"), fileAbsolutePath,
				Syn_Log_Exec_Type_TBSJ, Syn_Log_Exec_State_Success, "本次同步"
						+ rowcount + "条记录.");
		list.add(inisert); // 加入日志
		UpdateSQLBuilder updateMainTable = new UpdateSQLBuilder(
				"sal_data_interface_main");
		updateMainTable
				.setWhereCondition(" id=" + _mainVO.getStringValue("id"));
		updateMainTable.putFieldValue("lasttime", tbutil.getCurrTime());
		list.add(updateMainTable);
		dmo.executeBatchByDSImmediately(null, list, false);
			}
		}else{
			File file = new File(fileAbsolutePath);
			if (!file.exists()) {
				throw new Exception(fileAbsolutePath + "文件不存在，能需要还未上传，或双方接口配置不一致.");
			}
			String str = tbutil.readFromInputStreamToStr(new FileInputStream(file));
			String[] allline = str.split("\n");
//			for (int i = 0; i < allline.length; i++) {
//				String values[] = allline[i].split(splitStr);
//				for (int j = 0; j < values.length; j++) {
//					System.out.println(values[j]);
//				}
//			}
			InputStreamReader read = new InputStreamReader(
					new FileInputStream(file), datacode);
			BufferedReader filereader = new BufferedReader(read);
			String strcontent = null;

			String tableName = _mainVO.getStringValue("tablename");
			// 检测表是否存在,如果存在判断结构是否变化
			try {
				dmo.getStringValueByDS(null, "select * from " + tableName
						+ " where 1=2");
				String[][] different = compareDBTOConfig(_mainVO, _childItemVOs);// 看表结构有没有变化，一般是不变的。
				if (different != null && different.length > 0) {
					List sql = new ArrayList();
					StringBuffer descr = new StringBuffer();
					for (int i = 0; i < different.length; i++) {
						sql.add(different[i][3]);
						descr.append(different[i][3] + "\r\n");
					}
					dmo.executeBatchByDSImmediately(null, sql, false); //
					InsertSQLBuilder logsql = getSynLog(null, _mainVO
							.getStringValue("id"), _jobid, _datadate, _mainVO
							.getStringValue("iname"), "", Syn_Log_Exec_Type_XGB,
							Syn_Log_Exec_State_Success, "系统发现表结构变化，自动执行脚本:\r\n"
									+ descr);
					dmo.executeUpdateByDSImmediately(null, logsql);
				}
			} catch (Exception ex) { // 不存在，直接创建
				createTableByConfig(_mainVO.getStringValue("id"));
			}

			// 看看有没有历史数据有的话，先删除掉。然后在执行.
			HashVO[] old_his = dmo.getHashVoArrayByDS(null,
					"select * from sal_data_interface_log where filepath like '%"
							+ _folder + "%' and exectype='"
							+ Syn_Log_Exec_Type_TBSJ + "' and parentid="
							+ _mainVO.getStringValue("id")
							+ " order by exectime desc"); // 检查最近同步的记录
			if (old_his != null && old_his.length > 0) {
				List list = new ArrayList();
				StringBuffer msg = new StringBuffer();
				for (int i = 0; i < old_his.length; i++) {
					HashVO loghisvo = old_his[i];
					msg.append(loghisvo.getStringValue("exectime") + ",");
					String deloldsql = "delete from "
							+ _mainVO.getStringValue("tablename")
							+ " where logid = " + loghisvo.getStringValue("id");
					list.add(deloldsql);
				}
				String newlogid = dmo.getSequenceNextValByDS(null,
						"S_SAL_DATA_INTERFACE_LOG");
				InsertSQLBuilder dellogsql = getSynLog(newlogid, _mainVO
						.getStringValue("id"), _jobid, _datadate, _mainVO
						.getStringValue("iname"), "", Syn_Log_Exec_Type_SCSJ,
						Syn_Log_Exec_State_Success, "系统发现该接口已经在["
								+ msg.substring(0, msg.length() - 1)
								+ "]同步过,先清理历史数据.");
				list.add(dellogsql);
				dmo.executeBatchByDSImmediately(null, list);
			}
			ArrayList list = new ArrayList();
			//
			int rowcount = 0; // 记录生成多少条数据.
			String logid = dmo.getSequenceNextValByDS(null,
					"S_SAL_DATA_INTERFACE_LOG");
			String countid=null;
			if(soleflg ){
				countid=dmo.getStringValueByDS(null,"select max(id) from "+tableName);
				if(countid.equals(null) || countid==null || countid.equals("") || countid.equals(" "))
					countid="0";
				}else{
					countid=String.valueOf(Integer.parseInt(countid)+10);
			}
			int countvalue=Integer.parseInt(countid);
			while ((strcontent = filereader.readLine()) != null) { // 按行读取。以下逻辑可以用策略替换，可以按行读取，也可以是xml读取。
				String itemvalues[] = strcontent.split(splitStr); // 截取实际值
				rowcount++;
				if (itemvalues.length != _childItemVOs.length) {
					filereader.close(); // 一定要关闭流
					InsertSQLBuilder inisert = getSynLog(logid, _mainVO
							.getStringValue("id"), _jobid, _datadate, _mainVO
							.getStringValue("iname"), fileAbsolutePath,
							Syn_Log_Exec_Type_Other, Syn_Log_Exec_State_Fail,
							fileAbsolutePath + "文件字段长度和系统配置不一直.文件["
									+ itemvalues.length + "]列,系统配置["
									+ _childItemVOs.length + "]列");
					dmo.executeUpdateByDSImmediately(null, inisert);
					throw new Exception(fileAbsolutePath + "文件字段长度和系统配置不一直.文件["
							+ itemvalues.length + "]列,系统配置[" + _childItemVOs.length
							+ "]列");
				}
				InsertSQLBuilder insert = new InsertSQLBuilder(tableName);
				insert.putFieldValue("logid", logid);
				if(soleflg){
					countvalue++;
					insert.putFieldValue("id", countvalue);
				}
				for (int i = 0; i < itemvalues.length; i++) {
					String value = itemvalues[i];
					value = value.replace("\"", "").trim();
					insert.putFieldValue(stbutil.convertIntColToEn(i + 1,false), value);
				}
				list.add(insert);
				if (list.size() == 5000) { // 5000行提交一次。否则性能跟不上
					dmo.executeBatchByDSImmediately(null, list, false);
					list.clear();
				}
			}
			filereader.close();
			InsertSQLBuilder inisert = getSynLog(logid, _mainVO
					.getStringValue("id"), _jobid, _datadate, _mainVO
					.getStringValue("iname"), fileAbsolutePath,
					Syn_Log_Exec_Type_TBSJ, Syn_Log_Exec_State_Success, "本次同步"
							+ rowcount + "条记录.");
			list.add(inisert); // 加入日志
			UpdateSQLBuilder updateMainTable = new UpdateSQLBuilder(
					"sal_data_interface_main");
			updateMainTable
					.setWhereCondition(" id=" + _mainVO.getStringValue("id"));
			updateMainTable.putFieldValue("lasttime", tbutil.getCurrTime());
			list.add(updateMainTable);
			dmo.executeBatchByDSImmediately(null, list, false);
			
		}
	}
	private String convertRealColType(String _type, String _dbType) {
		if (_type.equalsIgnoreCase("decimal")
				|| _type.equalsIgnoreCase("number")) { // 如果是数字类型
			if (_dbType.equalsIgnoreCase("ORACLE")) {
				return "number"; //
			} else {
				return "decimal"; //
			}
		} else if (_type.equalsIgnoreCase("varchar")) {
			if (_dbType.equalsIgnoreCase("ORACLE")) {
				return "varchar2"; //
			} else {
				return "varchar"; //
			}
		} else if (_type.equalsIgnoreCase("char")) {
			return "char"; //
		} else {
			return _type; //
		}
	}

	/*
	 * 获取所有目录
	 * 
	 * @张营闯
	 */
	public String[] getFiledesc() {
		if (ftppath == null) {
			return null;
		}
		File file = new File(ftppath);
		List list = new ArrayList();
		if (file.isDirectory()) {
			File file2[] = file.listFiles();
			for (int i = 0; i < file2.length; i++) {
				if (file2[i].isDirectory()) {
					File[] listfiles = file2[i].listFiles();
					for (int j = 0; j < listfiles.length; j++) {
						if (listfiles[j].isDirectory()) {
							String pathes = listfiles[j].getName();
							list.add(pathes);
						}
					}
				}
			}
		}
		String filepath[] = (String[]) list.toArray(new String[0]);
		for (int i = 0; i < filepath.length - 1; i++) {
			for (int j = i + 1; j < filepath.length; j++) {
				if (filepath[i].compareToIgnoreCase(filepath[j]) < 0) {
					String temp = filepath[i];
					filepath[i] = filepath[j];
					filepath[j] = temp;
				}
			}
		}
		return filepath;
	}

	/*
	 * 定时器一般凌晨3点，调用次函数 通过定时器调用的数据同步 log表的JobID为空，说明是手动执行
	 */
	public boolean syn_data_bytimer(String _jobid, String _datadate)
			throws Exception {
		// String currFolder = "20140709"; // 自动同步的话，就是同步昨天日期的数据。
		//
		String filefolder = _datadate.replace("-", "");
		/*
		 * File ok_file = new File(ftppath + File.separator + filefolder +
		 * File.separator + "OK.DAT"); // 成功标记文件. if (!ok_file.exists()) {
		 * String logid = dmo.getSequenceNextValByDS(null,
		 * "S_SAL_DATA_INTERFACE_LOG"); InsertSQLBuilder logsql =
		 * getSynLog(logid, "-1", _jobid, _datadate, "数据接口标记OK.DAT文件",
		 * ok_file.getAbsolutePath(), Syn_Log_Exec_Type_TBSJ,
		 * Syn_Log_Exec_State_Fail, "系统没有发现" + filefolder + "目录下OK.DAT文件.");
		 * dmo.executeUpdateByDSImmediately(null, logsql); // throw new
		 * Exception("系统没有发现" + ok_file.getAbsolutePath() + "文件."); }
		 */
		HashVO[] data_ifc_mainVOs = dmo
				.getHashVoArrayByDS(null,
						"select * from sal_data_interface_main where activeflag='Y' order by seq"); // 找出所有需要处理的接口
		HashVO[] data_ifc_item_config = dmo.getHashVoArrayByDS(null,
				"select * from sal_data_interface_item_conf order by seq");// 接口字段配置

		HashMap<String, List> main_item_config_map = new HashMap(); // 主子表对应

		for (int i = 0; i < data_ifc_item_config.length; i++) {
			String mainid = data_ifc_item_config[i].getStringValue("parentid"); // 父亲ID
			if (main_item_config_map.containsKey(mainid)) {
				List list = main_item_config_map.get(mainid);
				list.add(data_ifc_item_config[i]);
			} else {
				List list = new ArrayList();
				list.add(data_ifc_item_config[i]);
				main_item_config_map.put(mainid, list);
			}
		}
		int syn_success = 0;
		StringBuffer err = new StringBuffer();
		for (int i = 0; i < data_ifc_mainVOs.length; i++) {
			try {
				List childList = main_item_config_map.get(data_ifc_mainVOs[i]
						.getStringValue("id"));
				if (childList.size() > 0) {
					syn_data_from_file(data_ifc_mainVOs[i],
							(HashVO[]) childList.toArray(new HashVO[0]),
							_datadate, _jobid);
				}
				syn_success++;
			} catch (Exception e) {
				err.append(e.toString() + "\r\n");
			}
		}
		if (err.length() > 0) {
			throw new Exception(err.toString());
		}
		return true;
	}

	/*
	 * 对比数据平台上传的文件 和 本地 配置的 差异。
	 * 通过传入的文件路径。获取其下所有子文件名称和系统sal_data_interface_main表中记录的做对比。最终插入到log表中
	 */
	public void compareDataIFCAndConfig(String _currFolder) {

	}

	/*
	 * 自动比较表是否发生变化。并返回执行sql.
	 */
	public String[][] compareDBTOConfig(HashVO hashVO, HashVO[] hashVOs)
			throws Exception {
		if (hashVOs == null || hashVOs.length < 1) {
			return null;
		}
		List<String[]> list = new ArrayList<String[]>();
		HashMap<String, String> dbmap = new HashMap<String, String>();
		String tablename = hashVO.getStringValue("tablename");
		TableDataStruct dataStruct = dmo.getTableDataStructByDS(null,
				"select * from " + tablename + " where 1=2");
		if (dataStruct == null) {
			return null;
		}
		String[] headername = dataStruct.getHeaderName();
		int[] headerlength = dataStruct.getPrecision();
		int[] scale = dataStruct.getScale();
		for (int i = 0; i < headername.length; i++) {
			if (scale[i] == 0) {
				dbmap.put(headername[i], headerlength[i] + "");
			} else {
				dbmap.put(headername[i], headerlength[i] + "," + scale[i]);
			}
		}
		for (int i = 0; i < hashVOs.length; i++) {
			String itemname = hashVOs[i].getStringValue("itemname");
			String itemcode = hashVOs[i].getStringValue("itemcode");
			String itemwidth = hashVOs[i].getStringValue("itemwidth");
			String itemtype = hashVOs[i].getStringValue("itemtype");
			if (dbmap.containsKey(itemcode)) {
				String itemdbwidth = dbmap.get(itemcode);
				if (itemdbwidth.equals(itemwidth)) {
					continue;
				} else {
					String sql = "alter table " + tablename + " modify "
							+ itemcode + " " + itemtype + "(" + itemwidth + ")";
					String[] info = new String[] { itemname, itemcode,
							itemtype, sql, "修改列", itemwidth, itemdbwidth };
					list.add(info);
				}
			} else {
				String sql = "alter table  " + tablename + " add " + itemcode
						+ " " + itemtype + "(" + itemwidth + ")";
				String[] info = new String[] { itemname, itemcode, itemtype,
						sql, "新增列", itemwidth };
				list.add(info);
			}
		}
		return list.toArray(new String[0][0]);
	}

	/*
	 * 数据同步完成后，调用该方法进行数据转换。
	 */
	public boolean convertIFCDataToReport(String _jobid, String dataDate)
			throws Exception {
		HashVO[] convertMainVOS = dmo.getHashVoArrayByDS(null,
				"select * from sal_convert_ifcdata order by seq"); // 找出数据转换策略
		HashVO[] convertChildConfVOs = dmo.getHashVoArrayByDS(null,
				"select * from sal_convert_ifcdata_item_conf order by seq");
		HashMap<String, List> main_item_config_map = new HashMap(); // 主子表对应
		for (int i = 0; i < convertChildConfVOs.length; i++) {
			String mainid = convertChildConfVOs[i].getStringValue("parentid"); // 父亲ID
			if (main_item_config_map.containsKey(mainid)) {
				List list = main_item_config_map.get(mainid);
				list.add(convertChildConfVOs[i]);
			} else {
				List list = new ArrayList();
				list.add(convertChildConfVOs[i]);
				main_item_config_map.put(mainid, list);
			}
		}
		StringBuffer err = new StringBuffer();
		for (int i = 0; i < convertMainVOS.length; i++) {
			List childList = main_item_config_map.get(convertMainVOS[i]
					.getStringValue("id"));
			if (childList.size() > 0) {
				try {
					convertIFCDataToReport(convertMainVOS[i],
							(HashVO[]) childList.toArray(new HashVO[0]),
							_jobid, dataDate); // 开始执行
				} catch (Exception ex) {
					err.append(ex.toString() + "\r\n");
				}
			}
		}
		if (err.length() > 0) {
			throw new Exception(err.toString());
		}
		return true;
	}

	/*
	 * 手动执行调用
	 */
	public void convertIFCDataToReportByHand(String _mainid, String dataDate)
			throws Exception {
		HashVO[] convertMainVOS = dmo.getHashVoArrayByDS(null,
				"select * from sal_convert_ifcdata where id = " + _mainid); // 找出数据转换策略
		HashVO[] convertChildConfVOs = dmo.getHashVoArrayByDS(null,
				"select * from sal_convert_ifcdata_item_conf where parentid="
						+ _mainid + " order by seq");
		if (convertMainVOS.length > 0) {
			convertIFCDataToReport(convertMainVOS[0], convertChildConfVOs,
					null, dataDate);
		}
	}

	/*
	 * 把接口数据转换为系统可识别的报表数据. 需要判断数据日期是不是月底数据。如果是，进行数据备份。
	 */
	private void convertIFCDataToReport(HashVO _mainConfigVO,
			HashVO[] _itemConfigvos, String _jobid, String dataDate)
			throws Exception {
		// 检查表存在否，不存在直接创建。表包中必须有logid字段，还有年，月字段。
		autoCheckTableExistsAndCreate(_mainConfigVO, _itemConfigvos, _jobid,
				dataDate);
		// 找到同步数据的日志ID。

		HashVO[] old_his = dmo.getHashVoArrayByDS(null,
				"select * from sal_convert_ifcdata_log where datadate='"
						+ dataDate + "' and exectype='"
						+ Syn_Log_Convert_Type_SJZH + "' and parentid="
						+ _mainConfigVO.getStringValue("id")
						+ " order by exectime desc"); // 检查最近同步的记录
		if (old_his != null && old_his.length > 0) {
			List list = new ArrayList();
			StringBuffer msg = new StringBuffer();
			for (int i = 0; i < old_his.length; i++) {
				HashVO loghisvo = old_his[i];
				msg.append(loghisvo.getStringValue("exectime") + ",");
				String deloldsql = "delete from "
						+ _mainConfigVO.getStringValue("tablename")
						+ " where logid = " + loghisvo.getStringValue("id");
				list.add(deloldsql);
			}
			InsertSQLBuilder dellogsql = getConvertLog(null, _mainConfigVO
					.getStringValue("id"), _mainConfigVO
					.getStringValue("reportname"), _jobid, dataDate,
					Syn_Log_Convert_Type_SCSJ, Syn_Log_Exec_State_Success,
					"系统发现[" + dataDate + "]数据已经在["
							+ msg.substring(0, msg.length() - 1)
							+ "]转换过,先清理历史数据.");
			list.add(dellogsql);
			dmo.executeBatchByDSImmediately(null, list, false);
		}
		String policyClass = _mainConfigVO.getStringValue("etlpolicy");
		if (!tbutil.isEmpty(policyClass) || _itemConfigvos == null
				|| _itemConfigvos.length == 0) { // 如果没有配置子表，那么说明是用策略类
			try {
				Object obj = Class.forName(policyClass).newInstance();
				if (obj instanceof DataIFCConvertToReport) {
					((DataIFCConvertToReport) obj).convert(_mainConfigVO,
							_jobid, dataDate);
				} else {
					InsertSQLBuilder dellogsql = getConvertLog(
							null,
							_mainConfigVO.getStringValue("id"),
							_mainConfigVO.getStringValue("reportname"),
							_jobid,
							dataDate,
							Syn_Log_Convert_Type_SJZH,
							Syn_Log_Exec_State_Fail,
							"["
									+ dataDate
									+ "]数据转换失败,原因："
									+ _mainConfigVO
											.getStringValue("reportname")
									+ "接口数据转换为报表数据配置策略需要实现[DataIFCConvertToReport]接口.");
					dmo.executeUpdateByDSImmediately(null, dellogsql);
					throw new Exception(_mainConfigVO
							.getStringValue("reportname")
							+ "接口数据转换为报表数据配置策略需要实现[DataIFCConvertToReport]接口.");
				}
			} catch (Exception ex) {
				InsertSQLBuilder dellogsql = getConvertLog(null, _mainConfigVO
						.getStringValue("id"), _mainConfigVO
						.getStringValue("reportname"), _jobid, dataDate,
						Syn_Log_Convert_Type_SJZH, Syn_Log_Exec_State_Fail, "["
								+ dataDate + "]数据转换失败,原因：" + ex.getMessage());
				dmo.executeUpdateByDSImmediately(null, dellogsql);
				throw ex;
			}
		} else {
			String sourceifc = _mainConfigVO.getStringValue("sourceifc"); // 得到接口配置主表ID
			HashVO[] ifcconfig = dmo.getHashVoArrayByDS(null,
					"select * from sal_data_interface_main where id ="
							+ sourceifc);// 配置表
			if (ifcconfig == null || ifcconfig.length == 0) {
				throw new Exception(_mainConfigVO.getStringValue("reportname")
						+ "接口数据转换配置的数据接口来源为空.");
			}
			String syndatalogid = null;
			//
			if (tbutil.isEmpty(_jobid) || "-1".equals(_jobid)) { // 非自动任务执行。
				syndatalogid = dmo.getStringValueByDS(null,
						"select id from sal_data_interface_log where parentid = "
								+ ifcconfig[0].getStringValue("id")
								+ " and exectype='" + Syn_Log_Exec_Type_TBSJ
								+ "' and state='" + Syn_Log_Exec_State_Success
								+ "' and datadate='" + dataDate
								+ "' order by exectime desc");
				if (tbutil.isEmpty(syndatalogid)) { // 如果没有找到同步日志。直接报错。
					throw new Exception("[" + dataDate + "]数据没有同步过,请检查.");
				}
			} else {
				syndatalogid = dmo.getStringValueByDS(null,
						"select id from sal_data_interface_log where parentid = "
								+ ifcconfig[0].getStringValue("id")
								+ " and exectype='" + Syn_Log_Exec_Type_TBSJ
								+ "' and state='" + Syn_Log_Exec_State_Success
								+ "' and jobid=" + _jobid);
			}

			HashVO[] datas = dmo.getHashVoArrayByDS(null, "select * from "
					+ ifcconfig[0].getStringValue("tablename")
					+ " where logid = " + syndatalogid); // 找到本次同步过来的所有数据
			String convertDataLogid = dmo.getSequenceNextValByDS(null,
					"S_SAL_CONVERT_IFCDATA_LOG");
			List insertDatasSql = new ArrayList();
			String year = dataDate.substring(0, 4);
			String month = dataDate.substring(5, 7);
			for (int i = 0; i < datas.length; i++) { //
				InsertSQLBuilder insert = new InsertSQLBuilder(_mainConfigVO
						.getStringValue("tablename"));
				insert.putFieldValue("logid", convertDataLogid);
				insert.putFieldValue("year", year);
				insert.putFieldValue("month", month);
				for (int j = 0; j < _itemConfigvos.length; j++) { //
					String itemcode = _itemConfigvos[j]
							.getStringValue("itemcode");// 报表数据中的字段
					String ifcitem = _itemConfigvos[j]
							.getStringValue("ifcitem"); // 接口表中的字段
					boolean flag = datas[i].containsKey(ifcitem.toUpperCase())
							|| datas[i].containsKey(ifcitem.toLowerCase()); // 是否包含此字段
					if (flag) {
						insert.putFieldValue(itemcode, datas[i]
								.getStringValue(ifcitem));
					}
				}
				insertDatasSql.add(insert);
			}
			// 插入执行历史
			InsertSQLBuilder logsql = getConvertLog(convertDataLogid,
					_mainConfigVO.getStringValue("id"), _mainConfigVO
							.getStringValue("reportname"), _jobid, dataDate,
					Syn_Log_Convert_Type_SJZH, Syn_Log_Exec_State_Success,
					"数据转换成功，共计" + insertDatasSql.size() + "条");
			insertDatasSql.add(logsql);

			UpdateSQLBuilder updateMainTable = new UpdateSQLBuilder(
					"sal_convert_ifcdata");
			updateMainTable.setWhereCondition(" id="
					+ _mainConfigVO.getStringValue("id"));
			updateMainTable.putFieldValue("lasttime", tbutil.getCurrTime());
			insertDatasSql.add(updateMainTable);
			dmo.executeBatchByDSImmediately(null, insertDatasSql, false); // 插入数据库
		}
	}

	/*
	 * 自动创建表 _datadate 可以为空
	 */
	public void autoCheckTableExistsAndCreate(HashVO _mainConfvo,
			HashVO[] _childConfig, String _jobid, String _datadate)
			throws Exception {
		String tablename = _mainConfvo.getStringValue("tablename");
		try {
			dmo.getStringValueByDS(null, "select * from " + tablename
					+ " where 1=2"); // 检查表是否存在
		} catch (Exception ex) {
			if (_childConfig == null || _childConfig.length == 0) {
				InsertSQLBuilder logsql = getConvertLog(null, _mainConfvo
						.getStringValue("id"), _mainConfvo
						.getStringValue("reportname"), _jobid, _datadate,
						Syn_Log_Convert_Type_CJB, Syn_Log_Exec_State_Fail,
						"创建表[" + tablename + "]失败，原因:没有找到表结构配置");
				dmo.executeUpdateByDSImmediately(null, logsql);
				throw new Exception("系统不能自动创建["
						+ _mainConfvo.getStringValue("reportname")
						+ "]报表结构.原因：表结构没有配置");
			}
			StringBuffer sb = new StringBuffer();
			String dbtype = ServerEnvironment.getDefaultDataSourceType();
			sb.append("create table " + tablename + "(");
			sb.append("year " + convertRealColType("varchar", dbtype) + "(" + 4
					+ "),"); //
			sb.append("month " + convertRealColType("varchar", dbtype) + "("
					+ 2 + "),"); //
			sb.append("logid " + convertRealColType("decimal", dbtype) + "("
					+ 22 + ")"); //
			for (int i = 0; i < _childConfig.length; i++) {
				String type = "varchar";
				if (_childConfig[i].getStringValue("itemwidth", "20").contains(
						",")) {
					type = "decimal";
				}
				sb.append("," + _childConfig[i].getStringValue("itemcode")
						+ " " + convertRealColType(type, dbtype) + "("
						+ _childConfig[i].getStringValue("itemwidth", "20")
						+ ") "); //
				if (dbtype.equalsIgnoreCase("MYSQL")
						&& !tbutil.isEmpty(_childConfig[i]
								.getStringValue("itemname"))) {
					sb.append(" comment '"
							+ _childConfig[i].getStringValue("itemname") + "'");
				}
			}
			sb.append(")");
			if (dbtype.equalsIgnoreCase("MYSQL")) {
				sb.append(" DEFAULT CHARSET = GBK"); //
				sb.append(" comment '"
						+ _mainConfvo.getStringValue("reportname") + "'"); //
			}
			dmo.executeUpdateByDS(null, sb.toString());
			InsertSQLBuilder logsql = getConvertLog(null, _mainConfvo
					.getStringValue("id"), _mainConfvo
					.getStringValue("reportname"), _jobid, _datadate,
					Syn_Log_Convert_Type_CJB, Syn_Log_Exec_State_Success,
					"创建表[" + tablename + "],执行语句" + sb.toString());
			dmo.executeUpdateByDSImmediately(null, logsql);
		}
	}

	/*
	 * 获取通用转换sql
	 */
	public InsertSQLBuilder getConvertLog(String _id, String _parentid,
			String _name, String _jobid, String _datadate, String _exectype,
			String _state, String descr) throws Exception {
		InsertSQLBuilder loginsertsql = new InsertSQLBuilder(
				"sal_convert_ifcdata_log");
		if (tbutil.isEmpty(_id)) { // 一般日志ID是传入的，其他地方会用到。
			_id = dmo.getSequenceNextValByDS(null, "S_SAL_CONVERT_IFCDATA_LOG");
		}
		loginsertsql.putFieldValue("id", _id);
		loginsertsql.putFieldValue("parentid", _parentid);
		loginsertsql.putFieldValue("jobid", _jobid);// 文件路径
		loginsertsql.putFieldValue("datadate", _datadate); // 接口名称
		loginsertsql.putFieldValue("exectime", tbutil.getCurrTime());
		loginsertsql.putFieldValue("reportname", _name); // 接口名称
		loginsertsql.putFieldValue("exectype", _exectype);
		loginsertsql.putFieldValue("state", _state);
		if (descr != null && descr.length() > 1950) {
			descr = descr.substring(0, 1950) + "...";
		}
		loginsertsql.putFieldValue("descr", descr);
		return loginsertsql;
	}

	/**
	 * 
	 * @param zzl
	 *            【2017/6/2】
	 * @return 遍历文件
	 */
	public static ArrayList getFiles(String aDir) {
		ArrayList files = new ArrayList();
		try {
			File dirx = new File(aDir);
			File[] dirFilesx = dirx.listFiles();
				for (int k = 0; k < dirFilesx.length; k++) {
					System.out.print("|");
					File file = dirFilesx[k];
					String fileName = file.getName();
					System.out.println("-" + fileName);
					if (file.isDirectory()) {
						String dirx1 = aDir + "\\" + fileName;
						getFiles(dirx1);
					}
					files.add(file);
				}
		} catch (Exception e) {	
			e.printStackTrace();
		}
		return files;

	}
/**
 * 
 * @param dataIFCmainID
 * @param datadate
 * @param string
 * @return zzl[2018-11-23] 批量同步數據接口
 */
	public String impReadDataFromFile(String _dataIFCmainID, String _dataDate,
			String _jobid) {
		HashVO data_ifc_mainVOs[];
		String str="";
		try {
			data_ifc_mainVOs = dmo.getHashVoArrayByDS(null,
					"select * from sal_data_interface_main where id = "
							+ _dataIFCmainID);		if (data_ifc_mainVOs.length == 0) {
								throw new Exception("传入的接口同步主表不存在主键为" + _dataIFCmainID
										+ "的配置项,请检查!");
							}

							HashVO[] data_ifc_item_config = dmo.getHashVoArrayByDS(null,
									"select * from sal_data_interface_item_conf where parentid = "
											+ _dataIFCmainID);
							str=imp_all_data_from_file(data_ifc_mainVOs[0], data_ifc_item_config,
										_dataDate, _jobid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //

		return str;
	}
	
	public String imp_all_data_from_file(HashVO _mainVO, HashVO[] _childItemVOs,
			String _datadate, String _jobid) throws Exception {
		String str="";
		if (tbutil.isEmpty(ftppath)) {
			return str="系统参数[数据接口FTP路径]为空，请配置!";
		}
		String _folder = _datadate.replace("-", ""); // 传入的是日期，截取为同步目录
		String parentFolder = _folder.substring(0, 6);
		String folderAbsolutePath = tbutil.replaceAll(ftppath, "\\",
				File.separator)
				+ File.separator
				+ parentFolder
				+ File.separator
				+ _folder
				+ File.separator; //
		String fileAbsolutePath = folderAbsolutePath+ _mainVO.getStringValue("filename");
		String filename=_mainVO.getStringValue("iname");
		if (flg) {
			// 看看有没有历史数据有的话，先删除掉。然后在执行.
			HashVO[] old_his = dmo.getHashVoArrayByDS(null,
					"select * from sal_data_interface_log where filepath like '%"
							+ _folder + "%' and exectype='"
							+ Syn_Log_Exec_Type_TBSJ + "' and parentid="
							+ _mainVO.getStringValue("id")
							+ " order by exectime desc"); // 检查最近同步的记录
			if (old_his != null && old_his.length > 0) {
				List list = new ArrayList();
				StringBuffer msg = new StringBuffer();
				for (int i = 0; i < old_his.length; i++) {
					HashVO loghisvo = old_his[i];
					msg.append(loghisvo.getStringValue("exectime") + ",");
					String deloldsql = "delete from "
							+ _mainVO.getStringValue("tablename")
							+ " where logid = " + loghisvo.getStringValue("id");
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>"+deloldsql);
					list.add(deloldsql);
				}
				String newlogid = dmo.getSequenceNextValByDS(null,
						"S_SAL_DATA_INTERFACE_LOG");
				InsertSQLBuilder dellogsql = getSynLog(newlogid, _mainVO
						.getStringValue("id"), _jobid, _datadate, _mainVO
						.getStringValue("iname"), "", Syn_Log_Exec_Type_SCSJ,
						Syn_Log_Exec_State_Success, "系统发现该接口已经在["
								+ msg.substring(0, msg.length() - 1)
								+ "]同步过,先清理历史数据.");
				list.add(dellogsql);
				dmo.executeBatchByDSImmediately(null, list);
			}
			ArrayList a = getFiles(folderAbsolutePath);
			if(a.size()<=0){
				return str="["+filename+"]文件不存在,可能没上传";
			}
			for (int f = 0; f < a.size(); f++) {
				File filesum = (File) a.get(f);
				fileAbsolutePath=folderAbsolutePath+"/"+filesum.getName()+"/"+ _mainVO.getStringValue("filename");
		File file = new File(fileAbsolutePath);
		if (!file.exists()) {
			return str=fileAbsolutePath + "文件不存在,请上传数据";
		}
//		String str = tbutil.readFromInputStreamToStr(new FileInputStream(file));
//		String[] allline = str.split("\n");
//		for (int i = 0; i < allline.length; i++) {
//			String values[] = allline[i].split(splitStr);
//			for (int j = 0; j < values.length; j++) {
//				System.out.println(values[j]);
//			}
//		}
		InputStreamReader read = new InputStreamReader(
				new FileInputStream(file), datacode);
		BufferedReader filereader = new BufferedReader(read);
		String strcontent = null;

		String tableName = _mainVO.getStringValue("tablename");
		// 检测表是否存在,如果存在判断结构是否变化
		try {
			dmo.getStringValueByDS(null, "select * from " + tableName
					+ " where 1=2");
			String[][] different = compareDBTOConfig(_mainVO, _childItemVOs);// 看表结构有没有变化，一般是不变的。
			if (different != null && different.length > 0) {
				List sql = new ArrayList();
				StringBuffer descr = new StringBuffer();
				for (int i = 0; i < different.length; i++) {
					sql.add(different[i][3]);
					descr.append(different[i][3] + "\r\n");
				}
				dmo.executeBatchByDSImmediately(null, sql, false); //
				InsertSQLBuilder logsql = getSynLog(null, _mainVO
						.getStringValue("id"), _jobid, _datadate, _mainVO
						.getStringValue("iname"), "", Syn_Log_Exec_Type_XGB,
						Syn_Log_Exec_State_Success, "系统发现表结构变化，自动执行脚本:\r\n"
								+ descr);
				dmo.executeUpdateByDSImmediately(null, logsql);
			}
		} catch (Exception ex) { // 不存在，直接创建
			createTableByConfig(_mainVO.getStringValue("id"));
		}
		ArrayList list = new ArrayList();
		//
		int rowcount = 0; // 记录生成多少条数据.
		String logid = dmo.getSequenceNextValByDS(null,
				"S_SAL_DATA_INTERFACE_LOG");
		String deptid=dmo.getStringValueByDS(null,"select id from pub_corp_dept where code='"+filesum.getName()+"'");
		String countid=null;
		if(soleflg ){
			countid=dmo.getStringValueByDS(null,"select max(id) from "+tableName);
			if(countid.equals(null) || countid==null || countid.equals("") || countid.equals(" "))
				countid="0";
			}else{
				countid=String.valueOf(Integer.parseInt(countid)+10);
		}
		int countvalue=Integer.parseInt(countid);
		while ((strcontent = filereader.readLine()) != null) { // 按行读取。以下逻辑可以用策略替换，可以按行读取，也可以是xml读取。
			String itemvalues[] = strcontent.split(splitStr); // 截取实际值
			rowcount++;
			if (itemvalues.length != _childItemVOs.length) {
				filereader.close(); // 一定要关闭流
				InsertSQLBuilder inisert = getSynLog(logid, _mainVO
						.getStringValue("id"), _jobid, _datadate, _mainVO
						.getStringValue("iname"), fileAbsolutePath,
						Syn_Log_Exec_Type_Other, Syn_Log_Exec_State_Fail,
						fileAbsolutePath + "文件字段长度和系统配置不一直.文件["
								+ itemvalues.length + "]列,系统配置["
								+ _childItemVOs.length + "]列");
				dmo.executeUpdateByDSImmediately(null, inisert);
				return str=fileAbsolutePath + "文件字段长度和系统配置不一直.文件["
				+ itemvalues.length + "]列,系统配置[" + _childItemVOs.length
				+ "]列";
			}
			InsertSQLBuilder insert = new InsertSQLBuilder(tableName);
			insert.putFieldValue("logid", logid);
			insert.putFieldValue("deptid", deptid);
			if(soleflg){
				countvalue++;		
				insert.putFieldValue("id", countvalue);
			}
			for (int i = 0; i < itemvalues.length; i++) {
				String value = itemvalues[i];
				value = value.replace("\"", "").trim();
				insert.putFieldValue(stbutil.convertIntColToEn(i + 1,false), value);
			}
			list.add(insert);
			if (list.size() == 5000) { // 5000行提交一次。否则性能跟不上
				dmo.executeBatchByDSImmediately(null, list, false);
				list.clear();
			}
		}
		filereader.close();
		InsertSQLBuilder inisert = getSynLog(logid, _mainVO
				.getStringValue("id"), _jobid, _datadate, _mainVO
				.getStringValue("iname"), fileAbsolutePath,
				Syn_Log_Exec_Type_TBSJ, Syn_Log_Exec_State_Success, "本次同步"
						+ rowcount + "条记录.");
		list.add(inisert); // 加入日志
		UpdateSQLBuilder updateMainTable = new UpdateSQLBuilder(
				"sal_data_interface_main");
		updateMainTable
				.setWhereCondition(" id=" + _mainVO.getStringValue("id"));
		updateMainTable.putFieldValue("lasttime", tbutil.getCurrTime());
		list.add(updateMainTable);
		dmo.executeBatchByDSImmediately(null, list, false);
			}
		}else{
			File file = new File(fileAbsolutePath);
			if (!file.exists()) {
				return str=fileAbsolutePath + "文件不存在，请上传数据";
			}
//			String str = tbutil.readFromInputStreamToStr(new FileInputStream(file));
//			String[] allline = str.split("\n");
//			for (int i = 0; i < allline.length; i++) {
//				String values[] = allline[i].split(splitStr);
//				for (int j = 0; j < values.length; j++) {
//					System.out.println(values[j]);
//				}
//			}
			InputStreamReader read = new InputStreamReader(
					new FileInputStream(file), datacode);
			BufferedReader filereader = new BufferedReader(read);
			String strcontent = null;

			String tableName = _mainVO.getStringValue("tablename");
			// 检测表是否存在,如果存在判断结构是否变化
			try {
				dmo.getStringValueByDS(null, "select * from " + tableName
						+ " where 1=2");
				String[][] different = compareDBTOConfig(_mainVO, _childItemVOs);// 看表结构有没有变化，一般是不变的。
				if (different != null && different.length > 0) {
					List sql = new ArrayList();
					StringBuffer descr = new StringBuffer();
					for (int i = 0; i < different.length; i++) {
						sql.add(different[i][3]);
						descr.append(different[i][3] + "\r\n");
					}
					dmo.executeBatchByDSImmediately(null, sql, false); //
					InsertSQLBuilder logsql = getSynLog(null, _mainVO
							.getStringValue("id"), _jobid, _datadate, _mainVO
							.getStringValue("iname"), "", Syn_Log_Exec_Type_XGB,
							Syn_Log_Exec_State_Success, "系统发现表结构变化，自动执行脚本:\r\n"
									+ descr);
					dmo.executeUpdateByDSImmediately(null, logsql);
				}
			} catch (Exception ex) { // 不存在，直接创建
				createTableByConfig(_mainVO.getStringValue("id"));
			}

			// 看看有没有历史数据有的话，先删除掉。然后在执行.
			HashVO[] old_his = dmo.getHashVoArrayByDS(null,
					"select * from sal_data_interface_log where filepath like '%"
							+ _folder + "%' and exectype='"
							+ Syn_Log_Exec_Type_TBSJ + "' and parentid="
							+ _mainVO.getStringValue("id")
							+ " order by exectime desc"); // 检查最近同步的记录
			if (old_his != null && old_his.length > 0) {
				List list = new ArrayList();
				StringBuffer msg = new StringBuffer();
				for (int i = 0; i < old_his.length; i++) {
					HashVO loghisvo = old_his[i];
					msg.append(loghisvo.getStringValue("exectime") + ",");
					String deloldsql = "delete from "
							+ _mainVO.getStringValue("tablename")
							+ " where logid = " + loghisvo.getStringValue("id");
					list.add(deloldsql);
				}
				String newlogid = dmo.getSequenceNextValByDS(null,
						"S_SAL_DATA_INTERFACE_LOG");
				InsertSQLBuilder dellogsql = getSynLog(newlogid, _mainVO
						.getStringValue("id"), _jobid, _datadate, _mainVO
						.getStringValue("iname"), "", Syn_Log_Exec_Type_SCSJ,
						Syn_Log_Exec_State_Success, "系统发现该接口已经在["
								+ msg.substring(0, msg.length() - 1)
								+ "]同步过,先清理历史数据.");
				list.add(dellogsql);
				dmo.executeBatchByDSImmediately(null, list);
			}
			ArrayList list = new ArrayList();
			//
			int rowcount = 0; // 记录生成多少条数据.
			String logid = dmo.getSequenceNextValByDS(null,
					"S_SAL_DATA_INTERFACE_LOG");
			String countid=null;
			if(soleflg ){
				countid=dmo.getStringValueByDS(null,"select max(id) from "+tableName);
				if(countid.equals(null) || countid==null || countid.equals("") || countid.equals(" "))
					countid="0";
				}else{
					countid=String.valueOf(Integer.parseInt(countid)+10);
			}
			int countvalue=Integer.parseInt(countid);
			while ((strcontent = filereader.readLine()) != null) { // 按行读取。以下逻辑可以用策略替换，可以按行读取，也可以是xml读取。
				String itemvalues[] = strcontent.split(splitStr); // 截取实际值
				rowcount++;
				if (itemvalues.length != _childItemVOs.length) {
					filereader.close(); // 一定要关闭流
					InsertSQLBuilder inisert = getSynLog(logid, _mainVO
							.getStringValue("id"), _jobid, _datadate, _mainVO
							.getStringValue("iname"), fileAbsolutePath,
							Syn_Log_Exec_Type_Other, Syn_Log_Exec_State_Fail,
							fileAbsolutePath + "文件字段长度和系统配置不一直.文件["
									+ itemvalues.length + "]列,系统配置["
									+ _childItemVOs.length + "]列");
					dmo.executeUpdateByDSImmediately(null, inisert);
					return str=fileAbsolutePath + "文件字段长度和系统配置不一直.文件["
					+ itemvalues.length + "]列,系统配置[" + _childItemVOs.length
					+ "]列";
				}
				InsertSQLBuilder insert = new InsertSQLBuilder(tableName);
				insert.putFieldValue("logid", logid);
				if(soleflg){
					countvalue++;
					insert.putFieldValue("id", countvalue);
				}
				for (int i = 0; i < itemvalues.length; i++) {
					String value = itemvalues[i];
					value = value.replace("\"", "").trim();
					insert.putFieldValue(stbutil.convertIntColToEn(i + 1,false), value);
				}
				list.add(insert);
				if (list.size() == 5000) { // 5000行提交一次。否则性能跟不上
					dmo.executeBatchByDSImmediately(null, list, false);
					list.clear();
				}
			}
			filereader.close();
			InsertSQLBuilder inisert = getSynLog(logid, _mainVO
					.getStringValue("id"), _jobid, _datadate, _mainVO
					.getStringValue("iname"), fileAbsolutePath,
					Syn_Log_Exec_Type_TBSJ, Syn_Log_Exec_State_Success, "本次同步"
							+ rowcount + "条记录.");
			list.add(inisert); // 加入日志
			UpdateSQLBuilder updateMainTable = new UpdateSQLBuilder(
					"sal_data_interface_main");
			updateMainTable
					.setWhereCondition(" id=" + _mainVO.getStringValue("id"));
			updateMainTable.putFieldValue("lasttime", tbutil.getCurrTime());
			list.add(updateMainTable);
			dmo.executeBatchByDSImmediately(null, list, false);
			return "["+filename+"]文件同步成功";
		}

	return str;
		
	}
}
