package com.pushworld.ipushgrc.bs.law.p010;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.to.common.CurrSessionVO;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;

import com.pushworld.ipushgrc.ui.ZipFileUtil;

public class ExportOrImportLawUtil {
	private CommDMO commDMO = new CommDMO();
	private static String schedule = "暂无信息";//更新进度

	public HashMap getXMlFromTable1000Records(String _dsName, String table, int _beginNo) throws Exception {
		String _tableName = table;
		int li_batchRecords = 500; // 一次取500条记录!!!
		int li_endNo = _beginNo + li_batchRecords; // [>=1 and <=1000][>=1001
		// and <=2000][>=2001 and
		// <=3000]
		String dbType = ServerEnvironment.getDefaultDataSourceType();
		StringBuffer sql_sb = new StringBuffer();
		if (_dsName != null && !_dsName.equals("null")) {
			DataSourceVO vo = ServerEnvironment.getInstance().getDataSourceVO(_dsName);
			if (vo != null) {
				dbType = vo.getDbtype();
			}
		}
		//以前按 order by 1 排序有问题，法规增量导入，子表主键未按大小排序，但是否增量又按子表主键比较的，故导致部分子表数据未导入【李春娟/2015-04-21】
		if (dbType.equalsIgnoreCase("MYSQL") || dbType.equalsIgnoreCase("DB2")) {//江西银行数据库是DB2，故增加该判断【李春娟/2016-08-09】
			sql_sb.append("select *  from " + _tableName + " order by id limit " + _beginNo + "," + li_batchRecords);
		} else if (dbType.equalsIgnoreCase("ORACLE")) {
			sql_sb.append("select " + _tableName + ".* from (select " + _tableName + ".*,Rownum RN from (select " + _tableName + ".* from " + _tableName + " order by id )" + _tableName);
			sql_sb.append("  where Rownum <=" + (_beginNo + li_batchRecords) + ") " + _tableName);
			sql_sb.append(" where RN > " + _beginNo);
		} else if (dbType.equals("SQLSERVER")) {
			StringBuilder sb_sql_new = new StringBuilder(); //
			sb_sql_new.append("with _t1 as "); //
			sb_sql_new.append("("); //
			sb_sql_new.append("select row_number() over (order by id asc) _rownum,");
			sb_sql_new.append(" * from " + _tableName); // 将原来的select后面开始的内容接上来!
			sb_sql_new.append(") ");
			sb_sql_new.append("select top " + (li_batchRecords) + " * from _t1 where _rownum >= " + _beginNo + ""); // 分页!!!
			sql_sb.append(sb_sql_new.toString()); //
		}
		return getXMLMapDataBySQL(_dsName, sql_sb.toString(), _tableName, (_beginNo + li_batchRecords)); //
	}

	public HashMap getXMlFromTable500RecordsByCondition(String _dsName, String table, int _beginNo, String joinSql, String _condition) throws Exception {
		String _tableName = table;
		int li_batchRecords = 500; // 一次取500条记录!!!
		int li_endNo = _beginNo + li_batchRecords; // [>=1 and <=1000][>=1001
		// and <=2000][>=2001 and
		// <=3000]
		String dbType = ServerEnvironment.getDefaultDataSourceType();
		StringBuffer sql_sb = new StringBuffer();
		if (_dsName != null && !_dsName.equals("null")) {
			DataSourceVO vo = ServerEnvironment.getInstance().getDataSourceVO(_dsName);
			if (vo != null) {
				dbType = vo.getDbtype();
			}
		}
		if (_condition != null && !_condition.equals("")) {
			if (_condition.trim().indexOf("where") != 0) {
				_condition = " where " + _condition;
			} else {
				_condition = " " + _condition;
			}
		} else {
			_condition = " ";
		}
		if (joinSql == null || joinSql.equals("")) {
			joinSql = " ";
		}
		//以前按 order by 1 排序有问题，法规增量导入，子表主键未按大小排序，但是否增量又按子表主键比较的，故导致部分子表数据未导入【李春娟/2015-04-21】
		if (dbType.equalsIgnoreCase("MYSQL")|| dbType.equalsIgnoreCase("DB2")) {//江西银行数据库是DB2，故增加该判断【李春娟/2016-08-09】
			sql_sb.append("select " + _tableName + ".*  from " + _tableName + " " + joinSql + " " + _condition + " order by id limit " + _beginNo + "," + li_batchRecords);
		} else if (dbType.equalsIgnoreCase("ORACLE")) {
			sql_sb.append("select " + _tableName + ".* from (select " + _tableName + ".*,Rownum RN from (select " + _tableName + ".* from " + _tableName + " " + joinSql + " " + _condition + "  order by id )" + _tableName);
			sql_sb.append("  where Rownum <=" + (_beginNo + li_batchRecords) + ") " + _tableName);
			sql_sb.append(" where RN > " + _beginNo);
		} else if (dbType.equals("SQLSERVER")) {
			StringBuilder sb_sql_new = new StringBuilder(); //
			sb_sql_new.append("with _t1 as "); //
			sb_sql_new.append("("); //
			sb_sql_new.append("select row_number() over (order by id asc) _rownum,");
			sb_sql_new.append(" * from " + _tableName + " " + joinSql + " " + _condition); // 将原来的select后面开始的内容接上来!
			sb_sql_new.append(") ");
			sb_sql_new.append("select top " + (li_batchRecords) + " * from _t1 where _rownum >= " + _beginNo + ""); // 分页!!!
			sql_sb.append(sb_sql_new.toString()); //
		}
		return getXMLMapDataBySQL(_dsName, sql_sb.toString(), _tableName, (_beginNo + li_batchRecords)); //
	}

	// 取内容
	private HashMap<String, Object> getXMLMapDataBySQL(String _dsName, String _sql, String _tabName, int _lastrow) throws Exception { // 
		StringBuilder sb_xml = new StringBuilder(); //
		sb_xml.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n"); //
		sb_xml.append("<root tablename=\"" + _tabName + "\">\r\n"); //
		HashVOStruct hvst = commDMO.getHashVoStructByDS(_dsName, _sql); //
		String[] str_keys = hvst.getHeaderName(); // 列名
		HashVO[] hvs = hvst.getHashVOs(); //
		String str_itemValue = null; //
		int li_returnRecordCount = 0; //
		for (int i = 0; i < hvs.length; i++) { // 遍历各行!!
			sb_xml.append("<!--" + (i + 1) + "-->\r\n"); //
			sb_xml.append("<record tabname=\"" + _tabName + "\">\r\n"); //
			for (int j = 0; j < str_keys.length; j++) { // 遍历各列!!
				if (str_keys[j].equalsIgnoreCase("RN"))
					continue; // 由于用RN来进行条数的范围过滤，所以多出RN无用列。
				str_itemValue = hvs[i].getStringValue(str_keys[j], ""); // 取得值!!
				if (str_itemValue == null || str_itemValue.trim().equals(""))
					continue;
				if (str_itemValue.indexOf("<") >= 0 || str_itemValue.indexOf(">") >= 0 || str_itemValue.indexOf("&") >= 0) { // 如果本身的<>
					str_itemValue = "<![CDATA[" + str_itemValue + "]]>"; //
				}
				sb_xml.append("  <col name=\"" + str_keys[j] + "\">" + str_itemValue + "</col>\r\n"); // //
			}
			sb_xml.append("</record>\r\n"); //
			sb_xml.append("\r\n"); //
			li_returnRecordCount++; //
		}
		sb_xml.append("</root>\r\n"); //

		HashMap<String, Object> returnMap = new HashMap<String, Object>(); //
		returnMap.put("记录数", new Integer(li_returnRecordCount)); //
		returnMap.put("结束点", new Integer(_lastrow)); //
		returnMap.put("内容", sb_xml.toString()); // x
		return returnMap; // 返回新的id值!
	}

	/**
	 * 
	 * @param _dsName
	 * @param path
	 * @param _compareTable
	 * @param conditionMap
	 *            所有的条件 是否备份，增量还是替换
	 * @throws Exception
	 */
	public String importXmlToTable1000Records(String _dsName, String path, HashMap _compareTable, HashMap conditionMap) throws Exception {
		schedule = "正在解压...";
		ZipFileUtil.unzip(path); // 这就解压一把
		String fileType = path.substring(path.lastIndexOf(".") + 1, path.length());
		String returnMsg = null;
		try {
			path = path.substring(0, path.length() - fileType.length() - 1);
			java.io.File mainFile = new java.io.File(path + File.separator + "main.xml");
			if (!mainFile.exists()) {
				throw new Exception("压缩包中没有main.xml主文件，不能导入！");
			}
			SAXBuilder builder = new SAXBuilder();
			org.jdom.Document rootNode = builder.build(mainFile);
			Element element = rootNode.getRootElement();
			boolean dump = false; // 是否备份，默认不备份
			String type = null; // 本次导入数据的类型，默认直接导入。不做删除操作
			HashMap nagetiveItem = (HashMap) conditionMap.get("nagetiveitem");
			if (conditionMap != null) {
				Object obj = conditionMap.get("dump");
				if (obj != null) {
					if (obj.equals(true)) {
						dump = true;
					}
				}
				obj = conditionMap.get("type");
				if (obj != null) {
					type = (String) obj;
				}
			}
			// main_tables要不要 待定。
			List tableFile = element.getChildren("table");
			String newTable = "";
			for (int j = 0; j < tableFile.size(); j++) { // 一张表
				Element tableElement = (Element) tableFile.get(j);
				String tablename = tableElement.getAttributeValue("name").toUpperCase(); // 表名
				tablename = tablename.toUpperCase();
				if (_compareTable != null && _compareTable.containsKey(tablename)) {
					newTable = (String) _compareTable.get(tablename);
					newTable = newTable.toUpperCase();
				} else {
					newTable = tablename;
				}
				// 备份
				if (dump) {
					schedule = "正在备份表[" + tablename + "]的数据到[" + (tablename + "_temp") + "]!";
					dumpTable(_dsName, newTable);
				}
				// 更新类型
				int nowMaxId = 0;
				TableDataStruct tabstrct = commDMO.getTableDataStructByDS(_dsName, "select * from " + newTable + " where 1=2"); //
				HashSet<String> hstCols = new HashSet<String>(); // 存放对比字段
				String[] str_cols = tabstrct.getHeaderName(); //
				for (int k = 0; k < str_cols.length; k++) {
					hstCols.add(str_cols[k].toUpperCase()); //
				}
				String key = str_cols[0];

				String max = commDMO.getStringValueByDS(_dsName, "select max(abs(" + key + ")) from " + newTable + " where " + key + " < 0");
				if (max != null && !max.equals("")) {
					nowMaxId = Integer.parseInt(max);
				}
				if ("替换".equals(type)) {
					String dbType = ServerEnvironment.getDefaultDataSourceType();
					StringBuffer sql_sb = new StringBuffer();
					if (_dsName != null && !_dsName.equals("null")) {
						DataSourceVO vo = ServerEnvironment.getInstance().getDataSourceVO(_dsName);
						if (vo != null) {
							dbType = vo.getDbtype();
						}
					}

					if (dbType.equalsIgnoreCase("DB2")) {//db2缓存池大小有限，删除大量数据需要先停止记录日志。并且在一个事务开始执行。[郝明2012-03-27]
						schedule = "正在整理表[" + tablename + "]的数据!";
						commDMO.executeBatchByDSImmediately(_dsName, new String[] { "alter table law_law_item activate not logged initially ", "delete from " + newTable + " where " + key + " <0" });
						//
					} else {
						schedule = "正在整理表[" + tablename + "]的数据!";
						commDMO.executeBatchByDSImmediately(_dsName, new String[] { "delete from " + newTable + " where " + key + " <0" });
					}
				}
				List fileList = tableElement.getChildren("file");
				boolean isLastFile = false; // 增量中用到。是否是最后一个文件。这种情况是文件为负的ID。加入100个文件，第50个就已经是旧数据。后面的肯定都是旧的！
				int fileSize = fileList.size();
				int firstfile = -1;
				for (int k = 0; k < fileSize; k++) {
					if (isLastFile && "增量".equals(type)) {
						break;
					}
					Element fileElement = (Element) fileList.get(k);
					List<String> insertSqlList = new ArrayList<String>();
					java.io.File current_file = null;
					if (fileElement.getAttributeValue("path") != null && !fileElement.getAttributeValue("path").equals("")) {
						current_file = new java.io.File(path + File.separator + fileElement.getAttributeValue("path"));
					} else {
						break;
					}
					rootNode = builder.build(current_file);
					element = rootNode.getRootElement();
					List recordNodeList = element.getChildren("record");
					if ("增量".equals(type)) {
						if (recordNodeList.size() > 0) {
							int firstId = 0;
							int lastId = 0;
							// 找到第一个ID
							Element firstRecord = (Element) recordNodeList.get(0);
							List colList = firstRecord.getChildren("col");
							for (int m = 0; m < colList.size(); m++) {
								Element colElement = (Element) colList.get(m);
								String colName = colElement.getAttributeValue("name").toUpperCase();
								String value = colElement.getText();
								if ("ID".equalsIgnoreCase(colName)) {
									if (value != null && !value.equals("")) {
										firstId = Integer.parseInt(value);
									}
									break;
								}
							}
							// 找到最后一个ID
							Element lastRecord = (Element) recordNodeList.get(recordNodeList.size() - 1);
							colList = lastRecord.getChildren("col");
							for (int m = 0; m < colList.size(); m++) {
								Element colElement = (Element) colList.get(m);
								String colName = colElement.getAttributeValue("name").toUpperCase();
								String value = colElement.getText();
								if ("ID".equalsIgnoreCase(colName)) {
									lastId = Integer.parseInt(value);
									break;
								}
							}
							if (firstId < 0) { // 如果是小于0的 那么第一条是本文件中ID最大的。
								if (nowMaxId >= Math.abs(firstId)) {
									isLastFile = true;
								}
							} else { // 是正的
								if (nowMaxId >= Math.abs(lastId)) { // 是负的此文件以后的文件全部都是旧数据
									continue; // 如果数据库中的当前最大ID
									// 已经大于一个500条文件中最大的一条记录。说明此文件已经包含。直接跳过
								}
							}
							if (firstfile < 0) { //设置起始文件
								firstfile = k;
							}
						}
						for (int l = 0; l < recordNodeList.size(); l++) {
							Element recodeElement = (Element) recordNodeList.get(l);
							List colList = recodeElement.getChildren("col");
							InsertSQLBuilder insertSQL = new InsertSQLBuilder(newTable);
							boolean isnewData = true; // 是否是新数据
							for (int m = 0; m < colList.size(); m++) {
								Element colElement = (Element) colList.get(m);
								String colName = colElement.getAttributeValue("name").toUpperCase();
								String value = colElement.getText();
								if (_compareTable != null && _compareTable.containsKey(tablename + "_" + colName)) {
									colName = (String) _compareTable.get(tablename + "_" + colName);
								}
								if ("ID".equalsIgnoreCase(colName)) { // 需要对比ID大小
									int v = Integer.parseInt(value);
									if (Math.abs(v) <= nowMaxId) { // 如果文件中的ID小于现在的最大ID。说明已经包含。
										isnewData = false;
										break;
									}
								}
								if (hstCols != null && hstCols.contains(colName)) { // 如果现在表存在导入的字段。
									if (nagetiveItem.containsKey(newTable.toLowerCase() + "_" + colName.toLowerCase())) { // 需要负
										if (value.contains("-")) {//如果包括负号，则不转换
											insertSQL.putFieldValue(colName, value);
										} else { // 搞成负的
											if (value != null && !value.equals("")) {
												if ("law_law_reflaw".equalsIgnoreCase(newTable + "_" + colName) || "law_law_reflaw2".equalsIgnoreCase(newTable + "_" + colName)) {//修订或废止的法规【李春娟/2015-04-21】
													value = TBUtil.getTBUtil().replaceAll(value, ";", ";-");
													value = TBUtil.getTBUtil().replaceAll(value, "$", "$-");
													insertSQL.putFieldValue(colName, value.substring(0, value.length() - 1));
												} else {
													int v = Integer.parseInt(value);
													insertSQL.putFieldValue(colName, 0 - v);
												}
											} else {
												insertSQL.putFieldValue(colName, value);
											}
										}
									} else {
										insertSQL.putFieldValue(colName, value);
									}
								}
							}
							if (isnewData) {
								insertSqlList.add(insertSQL.getSQL());
							}
						}
					} else {
						if (firstfile < 0)
							firstfile = 0; //设置起始文件
						for (int l = 0; l < recordNodeList.size(); l++) {
							Element recodeElement = (Element) recordNodeList.get(l);
							List colList = recodeElement.getChildren("col");
							InsertSQLBuilder insertSQL = new InsertSQLBuilder(newTable);
							for (int m = 0; m < colList.size(); m++) {
								Element colElement = (Element) colList.get(m);
								String colName = colElement.getAttributeValue("name").toUpperCase();
								String value = colElement.getText();
								if (_compareTable != null && _compareTable.containsKey(tablename + "_" + colName)) {
									colName = (String) _compareTable.get(tablename + "_" + colName);
								}
								if (hstCols != null && hstCols.contains(colName)) { // 如果现在表存在导入的字段。
									if (nagetiveItem.containsKey(newTable.toLowerCase() + "_" + colName.toLowerCase())) { // 需要负
										if (value.contains("-")) {//如果包括负号，则不转换
											insertSQL.putFieldValue(colName, value);
										} else { // 搞成负的
											if (value != null && !value.equals("")) {
												if ("law_law_reflaw".equalsIgnoreCase(newTable + "_" + colName) || "law_law_reflaw2".equalsIgnoreCase(newTable + "_" + colName)) {//修订或废止的法规【李春娟/2015-04-21】
													value = TBUtil.getTBUtil().replaceAll(value, ";", ";-");
													value = TBUtil.getTBUtil().replaceAll(value, "$", "$-");
													insertSQL.putFieldValue(colName, value.substring(0, value.length() - 1));
												} else {
													int v = Integer.parseInt(value);
													insertSQL.putFieldValue(colName, 0 - v);
												}
											} else {
												insertSQL.putFieldValue(colName, value);
											}
										}
									} else {
										insertSQL.putFieldValue(colName, value);
									}
								}
							}
							insertSqlList.add(insertSQL.getSQL());
						}
					}
					schedule = "正在更新[" + tablename + "]表的数据。进度[" + ((k - firstfile + 1) + "/" + (fileSize - firstfile) + "=" + ((k - firstfile + 1) * 100 / (fileSize - firstfile))) + "%]";
					if ("替换".equals(type)) {
						commDMO.executeBatchByDSImmediately(_dsName, insertSqlList);
					} else {
						commDMO.executeBatchByDS(_dsName, insertSqlList, false, false);
					}

				}
				if ("law_law".equalsIgnoreCase(newTable)) {
					//新增条数
					int insertcount = Integer.parseInt(commDMO.getStringValueByDS(_dsName, "select count(*) from " + newTable + " where " + key + " < -" + nowMaxId));
					String[] reflaws = commDMO.getStringArrayFirstColByDS(_dsName, "select reflaw from " + newTable + " where reflaw is not null and " + key + " < -" + nowMaxId);
					ArrayList reflawList = new ArrayList();//修订的法规主表id
					ArrayList reflawitemList = new ArrayList();//修订的法规子表id
					//修订的法规数据格式：;-23248;-23251;-23217$-335630;
					for (int i = 0; i < reflaws.length; i++) {//修订的法规，主表需改状态为“部分失效”，如关联子表，则子表状态也需修改
						String temp = reflaws[i];
						if (temp != null && temp.contains(";")) {
							String[] splits = TBUtil.getTBUtil().split(temp, ";");
							if (splits != null) {
								for (int k = 0; k < splits.length; k++) {
									if (splits[k] != null && splits[k].contains("$")) {//如果关联了修订的法规子表
										String[] ids = TBUtil.getTBUtil().split(splits[k], "$");
										if (!reflawList.contains(ids[0])) {//去重复
											reflawList.add(ids[0]);
										}
										if (!reflawitemList.contains(ids[1])) {//去重复
											reflawitemList.add(ids[1]);
										}
									} else {//如果只关联了修订的法规主表
										if (!reflawList.contains(splits[k])) {//去重复
											reflawList.add(splits[k]);
										}
									}
								}
							}
						}
					}
					ArrayList reflaw2List = new ArrayList();//废止的法规id
					String[] reflaw2s = commDMO.getStringArrayFirstColByDS(_dsName, "select reflaw2 from " + newTable + " where reflaw2 is not null and " + key + " < -" + nowMaxId);
					//废止的法规数据格式：;-23264;-23263;-23262;-23261;
					for (int i = 0; i < reflaw2s.length; i++) {//废止的法规，主表需改状态为“失效”
						String temp = reflaw2s[i];
						if (temp != null && temp.contains(";")) {
							String[] splits = TBUtil.getTBUtil().split(temp, ";");
							if (splits != null) {
								for (int k = 0; k < splits.length; k++) {
									if (!reflaw2List.contains(splits[k])) {//去重复
										reflaw2List.add(splits[k]);
									}
								}
							}
						}
					}
					ArrayList sqlList = new ArrayList();
					if ("增量".equals(type)) {//增量导入，则修改修订和废止的法规状态
						if (reflawList.size() > 0) {
							sqlList.add("update law_law set state='部分失效' where state<>'失效' and id in(" + TBUtil.getTBUtil().getInCondition(reflawList) + ")");
						}
						if (reflawitemList.size() > 0) {
							sqlList.add("update law_law_item set state='Y' where id in(" + TBUtil.getTBUtil().getInCondition(reflawitemList) + ")");
						}
						if (reflaw2List.size() > 0) {
							sqlList.add("update law_law set state='失效' where id in(" + TBUtil.getTBUtil().getInCondition(reflaw2List) + ")");
						}
						returnMsg = "本次增量导入";
					} else {//整体替换，则无需修改修订和废止的法规状态
						returnMsg = "本次整体替换";
					}
					returnMsg += "，新增了" + insertcount + "条法规，修订了" + reflawList.size() + "条法规，废止了" + reflaw2List.size() + "条法规。";

					//增加法规导入日志【李春娟 /2015-04-21】
					InsertSQLBuilder sqlBuilder = new InsertSQLBuilder("law_law_importlog");
					sqlBuilder.putFieldValue("id", commDMO.getSequenceNextValByDS(null, "s_law_law_importlog"));
					sqlBuilder.putFieldValue("descr", returnMsg);
					CurrSessionVO session = new WLTInitContext().getCurrSession();
					sqlBuilder.putFieldValue("userid", session.getLoginUserId());
					sqlBuilder.putFieldValue("username", session.getLoginUserName());
					sqlBuilder.putFieldValue("deptid", session.getLoginUserPKDept());
					if (session.getLoginUserPKDept() != null && !session.getLoginUserPKDept().equals("")) {
						sqlBuilder.putFieldValue("deptname", commDMO.getStringValueByDS(null, "select name from pub_corp_dept where id =" + session.getLoginUserPKDept()));
					}
					sqlBuilder.putFieldValue("createdate", TBUtil.getTBUtil().getCurrDate());
					sqlList.add(sqlBuilder.getSQL());
					commDMO.executeBatchByDS(null, sqlList);
				}
			}
			schedule = "success";
		} catch (Exception e) {
			schedule = "failed";
			throw e;
		} finally {
			schedule = "success";
			// 删除上传和解压的文件
			deleteFile(path);
			deleteFile(path + "." + fileType);
			return returnMsg;
		}
	}

	public static String getSchedule() {
		return schedule;
	}

	public void dumpTable(String dbName, String tableName, String dumpName) throws Exception {
		String[][] str_allTables = commDMO.getAllSysTableAndDescr(null, null, false, true); //
		if (tableName == null || tableName.equals("") || dumpName == null || dumpName.equals("")) {
			return;
		}
		for (int i = 0; i < str_allTables.length; i++) {
			if (dumpName.equalsIgnoreCase(str_allTables[i][0])) {
				// 如果找到了备份表。那么就删掉备份表。重新备份
				String dropTableSql = "drop table " + dumpName;
				commDMO.executeUpdateByDS(dbName, dropTableSql);
				// 删除成功在备份
				break;
			}
		}
		// 重备份
		String createTableSql = "create table " + dumpName + " as select * from " + tableName;
		commDMO.executeUpdateByDS(dbName, createTableSql);
		// 备份成功！
	}

	public void dumpTable(String dbName, String tableName) throws Exception {
		this.dumpTable(dbName, tableName, tableName + "_temp");
	}

	public boolean deleteFile(String path) {
		File cacheFile = new File(path);
		boolean flag = false;
		if (cacheFile.isDirectory()) {
			if (cacheFile.exists() && cacheFile.isDirectory()) {
				File[] cache_files = cacheFile.listFiles();
				for (int j = 0; j < cache_files.length; j++) {
					cache_files[j].delete();
				}
				flag = cacheFile.delete();
			}
		} else {
			flag = cacheFile.delete();
		}
		return flag;
	}
}
