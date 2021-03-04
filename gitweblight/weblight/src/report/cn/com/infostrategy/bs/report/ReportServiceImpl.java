package cn.com.infostrategy.bs.report;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.common.WebCallBeanIfc;
import cn.com.infostrategy.bs.common.WebCallIDFactory;
import cn.com.infostrategy.bs.report.style1.StyleReport_1_BuildDataIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WebCallParVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;
import cn.com.infostrategy.to.print.PubPrintItemBandVO;
import cn.com.infostrategy.to.print.PubPrintTempletVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.BillChartVO;
import cn.com.infostrategy.to.report.ReportUtil;
import cn.com.infostrategy.to.report.WordFileUtil;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;
import cn.com.infostrategy.ui.report.ReportServiceIfc;

/**
 * 报表模板的服务!!
 * @author user
 *
 */
public class ReportServiceImpl implements ReportServiceIfc {

	/**
	 * 取得一个SessionID,关同时放入服务器端缓存
	 */
	public String registerWebCallSessionID(WebCallParVO _parvo) throws Exception {
		WLTInitContext initContext = new WLTInitContext();
		String str_sessionid = initContext.getCurrSession().getHttpsessionid(); //sessionID
		String str_loginUserId = initContext.getCurrSession().getLoginUserId(); //登录用户编码
		String str_callid = str_sessionid + "_" + str_loginUserId + "_" + System.currentTimeMillis(); //利用某种算法计算出一个CallID.是用户ID+IP+当前时间
		WebCallIDFactory.getInstance().putWebCallSessionId(str_callid, _parvo); //送入服务器缓存,下一次Web访问,WebCallServlet就会从该缓存中取到参数
		return str_callid; //返回该ID
	}

	/**
	 * 进行一次远程调用，取得一个OfficeServletCall的SeeeionId,为下一次Office控件调用时用
	 * @param _parVO
	 * @return
	 * @throws Exception
	 */
	public String registerOfficeCallSessionID(OfficeCompentControlVO _vo) throws Exception {
		WLTInitContext initContext = new WLTInitContext();
		String str_sessionid = initContext.getCurrSession().getHttpsessionid();
		String str_loginUserId = initContext.getCurrSession().getLoginUserId(); //登录用户编码
		String str_callid = str_sessionid + "_" + str_loginUserId + "_" + System.currentTimeMillis(); //利用某种算法计算出一个CallID.是用户ID+IP+当前时间
		WebCallIDFactory.getInstance().putOfficeCallSessionId(str_callid, _vo); //注册
		return str_callid; //返回sessionId
	}

	/**
	 * 
	 * @param _htmlContent
	 * @return
	 * @throws Exception
	 */
	public String registerHtmlContentSessionID(String _htmlContent) throws Exception {
		WLTInitContext initContext = new WLTInitContext();
		String str_sessionid = initContext.getCurrSession().getHttpsessionid(); //sessionID
		String str_loginUserId = initContext.getCurrSession().getLoginUserId(); //登录用户编码
		String str_htmlcontentid = str_sessionid + "_" + str_loginUserId + "_" + System.currentTimeMillis(); //利用某种算法计算出一个CallID.是用户ID+IP+当前时间
		WebCallIDFactory.getInstance().putHtmlContentSessionId(str_htmlcontentid, _htmlContent); //送入服务器缓存,下一次Web访问,WebCallServlet就会从该缓存中取到参数
		return str_htmlcontentid; //返回该ID
	}

	/**
	 * 取得一个Html所有的内容!!根据入参!!!
	 * @return
	 * @throws Exception
	 */
	public String getHtmlContent(WebCallParVO _parVO) throws Exception {
		String str_className = _parVO.getCallClassName(); //反射类
		HashMap map_pars = _parVO.getParsMap(); //参数
		WLTInitContext initContext = new WLTInitContext(); //
		StringBuffer sb_html = new StringBuffer(); //
		try {
			WebCallBeanIfc bean = (WebCallBeanIfc) Class.forName(str_className).newInstance(); //
			sb_html.append(bean.getHtmlContent(map_pars)); //
			initContext.commitAllTrans(); //提交事务!!
		} catch (Exception ex) {
			initContext.rollbackAllTrans(); //回滚所有事务..
			ex.printStackTrace();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ex.printStackTrace(new PrintWriter(bos, true));
			byte exbytes[] = bos.toByteArray();
			bos.close();
			String sb_exstack = new String(exbytes, "GBK");
			sb_exstack = (new TBUtil()).replaceAll(sb_exstack, "\r", "<br>");
			StringBuffer sb_exception = new StringBuffer();
			sb_exception.append("<html>\r\n");
			sb_exception.append("<body>\r\n");
			sb_exception.append("<font size=2 color=\"red\">\r\n");
			sb_exception.append(sb_exstack);
			sb_exception.append("</font></body></html>\r\n");
			sb_html.append(sb_exception.toString());
		} finally {
			initContext.closeAllConn(); //
			initContext.release(); //
		}
		sb_html.append("<!-- 实际调用的页面生成类[" + str_className + "] -->\r\n"); //
		return sb_html.toString(); //
	}

	public void deleteOneBillCellTemplet(String _id) throws Exception {
		boolean delete = TBUtil.getTBUtil().getSysOptionBooleanValue("报表模板删除时是否删除业务数据", true);//太平报表模板用在诉讼案件模块，里面的数据（pub_billcelltemplet_data）不建议删除，故增加参数【李春娟/2018-12-28】
		ArrayList list = new ArrayList();
		if (delete) {
			list.add("delete from pub_billcelltemplet_data where templet_h_id='" + _id + "'");
		}
		list.add("delete from pub_billcelltemplet_d    where templet_h_id='" + _id + "'"); //
		list.add("delete from pub_billcelltemplet_h    where id='" + _id + "'"); //
		new CommDMO().executeBatchByDS(null, list);
	}

	/**
	 * 生成导出Excel模板数据的SQL
	 * @param _filename
	 * @param _code
	 */
	public String getExportCellTempletSQL(String _code, String _dbtype, boolean _iswrap) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("delete pub_billcelltemplet_d where templet_h_id in " + "(select id from pub_billcelltemplet_h where templetcode = '" + _code + "');\r\n");
		sb_sql.append("delete pub_billcelltemplet_h where templetcode ='" + _code + "';\r\n");
		sb_sql.append("\r\n");

		HashVO[] hv_data = commDMO.getHashVoArrayByDS(null, "select * from pub_billcelltemplet_h where templetcode ='" + _code + "'"); //
		if (hv_data.length <= 0) {
			return "";
		}

		String str_parent_id = hv_data[0].getStringValue("id"); //

		String[] str_keys = hv_data[0].getKeys(); //取得所有字段名.
		String str_columnnames = ""; //所有字段名
		String str_columvalues = ""; //所有字段值
		for (int i = 0; i < str_keys.length; i++) {
			str_columnnames = str_columnnames + str_keys[i];
			if (str_keys[i].equalsIgnoreCase("id")) { //如果是主键
				str_columvalues = str_columvalues + (_dbtype.equals(WLTConstants.SQLSERVER) ? "dbo." : "") + "fn_getsequence('S_PUB_BILLCELLTEMPLET_H')"; //
			} else {
				str_columvalues = str_columvalues + getInsertValue(hv_data[0].getStringValue(str_keys[i])); //
			}

			if (i != str_keys.length - 1) {
				str_columnnames = str_columnnames + ",";
				str_columvalues = str_columvalues + ",";
			}

			if (_iswrap) { //如果换行
				//str_columnnames = str_columnnames + "\r\n";
				str_columvalues = str_columvalues + " --" + str_keys[i] + "\r\n";
			}
		}

		sb_sql.append("update pub_sequence set currvalue=currvalue+1 where sename='S_PUB_BILLCELLTEMPLET_H';\r\n");
		if (_iswrap) { //如果换行
			sb_sql.append("insert into pub_billcelltemplet_h (" + str_columnnames + ")\r\nvalues\r\n(\r\n" + str_columvalues + ");\r\n\r\n");
		} else {
			sb_sql.append("insert into pub_billcelltemplet_h (" + str_columnnames + ") values (" + str_columvalues + ");\r\n\r\n");
		}

		// 把每个Item的列和数据写入文件
		String _sql_item_context = "Select * From pub_billcelltemplet_d Where templet_h_id='" + str_parent_id + "'";
		HashVO[] hv_item = commDMO.getHashVoArrayByDS(null, _sql_item_context); //
		for (int i = 0; i < hv_item.length; i++) {
			String[] str_itemkeys = hv_item[i].getKeys(); //得到所有列名
			String str_itemcolumnnames = ""; //所有字段名
			String str_iemcolumvalues = ""; //所有字段值
			for (int j = 0; j < str_itemkeys.length; j++) {
				str_itemcolumnnames = str_itemcolumnnames + str_itemkeys[j];
				if (str_itemkeys[j].equalsIgnoreCase("id")) { //如果是主键
					str_iemcolumvalues = str_iemcolumvalues + (_dbtype.equals(WLTConstants.SQLSERVER) ? "dbo." : "") + "fn_getsequence('S_PUB_BILLCELLTEMPLET_D')"; //采用序列取数..
				} else if (str_itemkeys[j].equalsIgnoreCase("templet_h_id")) { //如果是父记录主键
					str_iemcolumvalues = str_iemcolumvalues + (_dbtype.equals(WLTConstants.SQLSERVER) ? "dbo." : "") + "fn_getsequence('S_PUB_BILLCELLTEMPLET_H')"; //采用序列取数..
				} else {
					str_iemcolumvalues = str_iemcolumvalues + getInsertValue(hv_item[i].getStringValue(str_itemkeys[j])); //
				}

				if (j != str_itemkeys.length - 1) {
					str_itemcolumnnames = str_itemcolumnnames + ",";
					str_iemcolumvalues = str_iemcolumvalues + ",";
				}

				if (_iswrap) { //如果换行
					//str_itemcolumnnames = str_itemcolumnnames + "\r\n";
					str_iemcolumvalues = str_iemcolumvalues + " --" + str_itemkeys[j] + "\r\n";
				}
			}

			sb_sql.append("update pub_sequence set currvalue=currvalue+1 where sename='S_PUB_BILLCELLTEMPLET_D';\r\n");
			if (_iswrap) { //如果换行
				sb_sql.append("insert into pub_billcelltemplet_d (" + str_itemcolumnnames + ")\r\nvalues\r\n(\r\n" + str_iemcolumvalues + ");\r\n\r\n");
			} else {
				sb_sql.append("insert into pub_billcelltemplet_d (" + str_itemcolumnnames + ") values (" + str_iemcolumvalues + ");\r\n\r\n");
			}
		}

		return sb_sql.toString(); //
	}

	/**
	 * 根据_value来确定要插入的值
	 * @param _value
	 * @return
	 */
	private String getInsertValue(String _value) {
		String str_value = null;
		if (_value == null || _value.equals("")) {
			str_value = "null";
		} else {
			str_value = "'" + convert(_value) + "'";
		}
		return str_value;
	}

	private String convert(String _str) {
		if (_str == null) {
			return "";
		}
		return _str.replaceAll("'", "''");
	}

	public HashMap sumBillData(String[] billno, String[] cellkey) throws Exception {
		HashVO[] hashvo3 = null;
		HashMap temp = new HashMap();
		Double value = 0.0;
		for (int i = 0; i < billno.length; i++) {
			hashvo3 = ServerEnvironment.getCommDMO().getHashVoArrayByDS(null, "select * from pub_billcelltemplet_data where billno='" + billno[i] + "'");
			for (int j = 0; j < hashvo3.length; j++) {

				for (int k = 0; k < cellkey.length; k++) {

					if (temp.get(hashvo3[j].getStringValue("cellkey")) == null) {
						if (cellkey[k].equals(hashvo3[j].getStringValue("cellkey"))) {
							temp.put(cellkey[k], hashvo3[j].getStringValue("cellvalue"));

							break;
						}
					} else {
						if (cellkey[k].equals(hashvo3[j].getStringValue("cellkey"))) {
							String tempvalue = "";
							if (hashvo3[j].getStringValue("cellvalue") == null || hashvo3[j].getStringValue("cellvalue").trim().equals("")) {
								tempvalue = "0";
							} else {
								tempvalue = hashvo3[j].getStringValue("cellvalue");
							}
							try {
								if (temp.get(cellkey[k]) == null || ((String) temp.get(cellkey[k])).trim().equals("")) {
									value = Double.parseDouble(tempvalue);
									temp.put(cellkey[k], Double.toString(value));
									break;
								} else {
									value = Double.parseDouble((String) temp.get(cellkey[k])) + Double.parseDouble(tempvalue);
									temp.put(cellkey[k], Double.toString(value));
									break;
								}

							} catch (Exception ex) {
								value = 0.0;//Integer.parseInt((String) temp.get(cellkey[k]));
								temp.put(cellkey[k], Double.toString(value));
								ex.printStackTrace();
							} finally {

							}
						}
					}

				}
			}
		}
		return temp;
	}

	public HashMap getSumCorpBillDataByContent(String[] billno, String[] cellkey) throws Exception {
		HashVO[] hashvo3 = null;
		HashMap temp = new HashMap();
		String value = "";
		for (int i = 0; i < billno.length; i++) {
			hashvo3 = ServerEnvironment.getCommDMO().getHashVoArrayByDS(null, "select * from pub_billcelltemplet_data where billno='" + billno[i] + "'");
			for (int j = 0; j < hashvo3.length; j++) {

				for (int k = 0; k < cellkey.length; k++) {

					if (temp.get(hashvo3[j].getStringValue("cellkey")) == null) {
						if (cellkey[k].equals(hashvo3[j].getStringValue("cellkey"))) {
							temp.put(cellkey[k], hashvo3[j].getStringValue("cellvalue"));
							break;
						}
					} else {
						if (cellkey[k].equals(hashvo3[j].getStringValue("cellkey"))) {

							try {

								value = (String) temp.get(cellkey[k]) + "\r\n" + hashvo3[j].getStringValue("cellvalue");
								temp.put(cellkey[k], value);
								break;

							} catch (Exception ex) {

								ex.printStackTrace();
							}
						}
					}

				}
			}
		}
		return temp;

	}

	/**
	 * _tablename 表名
	 * _excelitemkey excel控件字段名
	 * _corpitemkey 机构字段名
	 * 
	 */
	public HashMap getSumCorpBillDataByID(String _tablename, String _excelitemkey, String _corpitemkey, String _corpid, String[] _keys) throws Exception {
		HashVO[] hashvo1 = ServerEnvironment.getCommDMO().getHashVoArrayByDS(null, "select linkcode from pub_corp_dept where id='" + _corpid + "'");
		HashVO[] hashvo2 = ServerEnvironment.getCommDMO().getHashVoArrayByDS(null, "select id from pub_corp_dept where linkcode like '" + hashvo1[0].getStringValue("linkcode") + "%'");
		HashVO[] hashvo = null;
		String[] billno = new String[hashvo2.length];
		for (int i = 0; i < hashvo2.length; i++) {
			hashvo = ServerEnvironment.getCommDMO().getHashVoArrayByDS(null, "select " + _excelitemkey + " from " + _tablename + " where " + _corpitemkey + "in (" + hashvo2[i].getStringValue("id") + ")");
			billno[i] = hashvo[i].getStringValue(_excelitemkey);
		}

		return this.sumBillData(billno, _keys);
	}

	/**
	 * 根据BillCellVO生成HTML表格
	 * @param _cellVO
	 * @return
	 * @throws Exception
	 */
	public String getHtmlTableByBillCellVO(BillCellVO _cellVO) throws Exception {
		return null; //
	}

	/**
	 * 对一个HashVO[]进行分组统计..
	 */
	public HashVO[] groupHashVOs(HashVO[] _hvs, String _groupFunc, String _groupField) throws Exception {
		return new ReportDMO().groupHashVOs(_hvs, _groupFunc, _groupField); //
	}

	/**
	 * 取得维度定义类
	 * @param _builderClassName
	 * @return
	 * @throws Exception
	 */
	public HashMap getMultiLevelReportGroup(String _builderClassName) throws Exception {
		MultiLevelReportDataBuilderIFC builder = (MultiLevelReportDataBuilderIFC) Class.forName(_builderClassName).newInstance();
		String[] str_groupfieldnames = builder.getGroupFieldNames(); //参与分组的字段
		String[] str_sumfieldnames = builder.getSumFiledNames(); //参与汇总的字段
		HashMap sortmap = builder.getGroupFieldOrderConfig(); //取得字段排序配置

		HashMap hm_return = new HashMap(); //
		hm_return.put("AllGroupFields", str_groupfieldnames); //
		hm_return.put("AllComputeFields", str_sumfieldnames); //
		hm_return.put("groupfieldorder", sortmap);//加入字段排序配置！

		//取得预置的统计类型
		if (builder instanceof MultiLevelReportDataBuilderAdapter) {
			BeforeHandGroupTypeVO[] beforeHandVOs_grid = ((MultiLevelReportDataBuilderAdapter) builder).getBeforehandGroupType_Grid(); //网格的预置条件
			if (beforeHandVOs_grid != null && beforeHandVOs_grid.length > 0) {
				for (int i = 0; i < beforeHandVOs_grid.length; i++) {
					if (beforeHandVOs_grid[i].getRowHeaderGroupFields() == null) {
						beforeHandVOs_grid[i].setRowHeaderGroupFields(new String[0]);
					}
					if (beforeHandVOs_grid[i].getColHeaderGroupFields() == null) {
						beforeHandVOs_grid[i].setColHeaderGroupFields(new String[0]);
					}
				}
				hm_return.put("BeforeHandGroupType_Grid", beforeHandVOs_grid); //
			}

			BeforeHandGroupTypeVO[] beforeHandVOs_chart = ((MultiLevelReportDataBuilderAdapter) builder).getBeforehandGroupType_Chart(); //图表的预置条件!
			if (beforeHandVOs_chart != null && beforeHandVOs_chart.length > 0) {
				for (int i = 0; i < beforeHandVOs_chart.length; i++) {
					if (beforeHandVOs_chart[i].getRowHeaderGroupFields() == null) {
						beforeHandVOs_chart[i].setRowHeaderGroupFields(new String[0]);
					}
					if (beforeHandVOs_chart[i].getColHeaderGroupFields() == null) {
						beforeHandVOs_chart[i].setColHeaderGroupFields(new String[0]);
					}
				}
				hm_return.put("BeforeHandGroupType_Chart", beforeHandVOs_chart); //
			}
			BeforeHandGroupTypeVO[] beforeHandVOs_Splider = ((MultiLevelReportDataBuilderAdapter) builder).getBeforehandGroupType_Splider(); //雷达的预置条件
			if (beforeHandVOs_Splider != null && beforeHandVOs_Splider.length > 0) {
				for (int i = 0; i < beforeHandVOs_Splider.length; i++) {
					if (beforeHandVOs_Splider[i].getRowHeaderGroupFields() == null) {
						beforeHandVOs_Splider[i].setRowHeaderGroupFields(new String[0]);
					}
					if (beforeHandVOs_Splider[i].getColHeaderGroupFields() == null) {
						beforeHandVOs_Splider[i].setColHeaderGroupFields(new String[0]);
					}
				}
				hm_return.put("BeforeHandGroupType_Splider", beforeHandVOs_Splider); //
			}

			hm_return.put("drillclass", ((MultiLevelReportDataBuilderAdapter) builder).getDrillActionClassPath()); //客户端钻取真实数据的实现类
			hm_return.put("drilltempletcode", ((MultiLevelReportDataBuilderAdapter) builder).getDrillTempletCode()); //客户端钻取真实数据的模板
			hm_return.put("drillgroupbind", ((MultiLevelReportDataBuilderAdapter) builder).getDrilGroupBind()); //维度之间的绑定！！
			hm_return.put("zeroReportConfMap", ((MultiLevelReportDataBuilderAdapter) builder).getZeroReportConfMap()); //零汇报机制！！
			hm_return.put("dateGroupDefineMap", ((MultiLevelReportDataBuilderAdapter) builder).getDateGroupDefineMap()); //时间字段定义维度！！
		}

		return hm_return; //
	}

	/*
	 * 根据查询条件,创建报表实际数据
	 */
	public HashVO[] queryMultiLevelReportData(HashMap _queryCondition, String _builderClassName, String[] _colGroupFields, String[][] _computeFields, boolean _isCtrlDown) throws Exception {
		MultiLevelReportDataBuilderIFC builder = (MultiLevelReportDataBuilderIFC) Class.forName(_builderClassName).newInstance();
		String[] str_allGroups = builder.getGroupFieldNames(); //所有组名!!!
		HashVO[] hvs_initData = null; //
		if (_isCtrlDown && builder instanceof MultiLevelReportDataBuilderAdapter) { //如果按了Ctrl键,
			HashVO[] demoVOs = ((MultiLevelReportDataBuilderAdapter) builder).buildDemoData(); //如果按的是Ctrl键,则不是取实际数据,而是Demo数据
			if (demoVOs != null) { //
				hvs_initData = demoVOs; //
			} else {
				//如果为空
				//强行创建动态组名!!
				hvs_initData = builder.buildReportData(_queryCondition); //实际构造的明细数据!!!
			}
		} else { //如果没有按Ctrl键
			hvs_initData = builder.buildReportData(_queryCondition); //实际构造的明细数据!!!
		}

		if (_colGroupFields == null) { //如果没有分组,即查询所有明细
			return hvs_initData;
		}

		//拼成分组列的字符串,即像SQL一样的语法,进行分组汇总!!
		String str_groupFieldNames = ""; //
		for (int i = 0; i < _colGroupFields.length; i++) {
			str_groupFieldNames = str_groupFieldNames + _colGroupFields[i];
			if (i != _colGroupFields.length - 1) {
				str_groupFieldNames = str_groupFieldNames + ","; //
			}
		}

		String str_computeFields = "";
		for (int i = 0; i < _computeFields.length; i++) {
			String str_realField = null; //
			if (_computeFields[i][2].indexOf("-") > 0) { //如果有@
				str_realField = _computeFields[i][2].substring(0, _computeFields[i][2].lastIndexOf("-")); ////
			} else {
				str_realField = _computeFields[i][2]; //
			}
			str_computeFields = str_computeFields + _computeFields[i][0] + "(" + str_realField + ") " + _computeFields[i][2]; //对某个字段进行汇总,比如sum(金额) 金额
			if (i != _computeFields.length - 1) {
				str_computeFields = str_computeFields + ","; //
			}
		}

		ReportUtil reportUtil = new ReportUtil(); //
		//TBUtil.getTBUtil().writeHashToHtmlTableFile(hvs_initData, "C:/查看1.htm"); //输出文件看下处理后的样子到底是什么!!
		//袁江晓20121102添加     对VO进行遍历，将	所取字段的内容为---请选择---替换为空字符串，主要为了取消掉在统计的时候结果列显示---请选择---的字样
		String str_temp[] = str_groupFieldNames.split(",");//先将字段以逗号截取
		if (null != hvs_initData) {
			for (int i = 0; i < hvs_initData.length; i++) {
				for (int j = 0; j < str_temp.length; j++) {
					String temp = hvs_initData[i].getStringValue(str_temp[j]);
					if (null != temp && temp.equals("---请选择---")) {
						hvs_initData[i].setAttributeValue(str_temp[j], "");
					}
				}
			}
		}
		//TBUtil.getTBUtil().writeHashToHtmlTableFile(hvs_initData, "C:/查看2.htm"); //输出文件看下处理后的样子到底是什么!!
		HashVO[] hvs_group = reportUtil.groupHashVOs(hvs_initData, str_computeFields, str_groupFieldNames); //生成汇总数据,这是前台需要的最精简的数据!因为必须给前台返回最精简的数据,性能才会高!
		//
		return hvs_group; //返回统计后的数据,
	}

	//钻取多维报表的明细数据!!!
	public HashVO[] queryMultiLevelReportDataDrillDetail(HashMap _queryCondition, String _builderClassName, String _ids) throws Exception {
		String[] str_idItems = TBUtil.getTBUtil().split(_ids, ";"); //分割!!
		MultiLevelReportDataBuilderIFC builder = (MultiLevelReportDataBuilderIFC) Class.forName(_builderClassName).newInstance(); //		
		HashVO[] hvs_initData = builder.buildReportData(_queryCondition); //这才是真正的明细数据!!
		ArrayList al_return = new ArrayList(); //
		for (int i = 0; i < hvs_initData.length; i++) {
			String str_id = hvs_initData[i].getStringValue("id"); //
			if (str_id != null && TBUtil.getTBUtil().isExistInArray(str_id, str_idItems)) { //如果存在于数组中!!
				al_return.add(hvs_initData[i]); //
			}
		}
		return (HashVO[]) al_return.toArray(new HashVO[0]); //返回结果!!
	}

	/**
	 * 将一个图表导出成一个Word文件!!!返回的是Wrod的XML格式
	 * @param _jFreeChart
	 * @return
	 * @throws Exception
	 */
	public String exportChartAsWordXML(JFreeChart _jFreeChart) throws Exception {
		WordFileUtil wordFileUtil = new WordFileUtil();
		String head = wordFileUtil.getWordFileBegin();//得到WORD头
		String end = wordFileUtil.getWordFileEnd();//得到WORD尾
		//		StringBuffer imgs = new StringBuffer();

		//byte[] byte_1 = 
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ChartUtilities.writeChartAsJPEG(out, _jFreeChart, 10, 10);//.writeBufferedImageAsJPEG(arg0, _jFreeChart.createBufferedImage(5, 5)); //
		byte[] byte_1 = new byte[out.size()];
		out.write(byte_1);
		out.flush();
		String str_img64code = new String(org.jfreechart.xml.util.Base64.encode(byte_1)); //jfreechart-1.0.14升级修改类路径jfree为jfreechart 【杨科/2013-06-09】
		String center = wordFileUtil.getImageXml(str_img64code, 500, 500);

		String wordgragic = head + center + end;
		return wordgragic;
	}

	/**
	 * 风格报表1的构造数据的过程!!
	 */
	public HashVO[] styleReport_1_BuildData(HashMap _condition, String _className, CurrLoginUserVO _loginUserVO) throws Exception { //
		StyleReport_1_BuildDataIFC builder = (StyleReport_1_BuildDataIFC) Class.forName(_className).newInstance();//
		HashVO[] hvs = builder.buildDataByCondition(_condition, _loginUserVO); //
		return hvs;
	}

	/**
	 * 风格报表2生成构造数据
	 * @param _condition
	 * @param _className
	 * @param _loginUserVO
	 * @return
	 * @throws Exception
	 */
	public BillCellVO styleReport_2_BuildData(HashMap _condition, String _className, CurrLoginUserVO _loginUserVO) throws Exception {
		return new cn.com.infostrategy.bs.report.style2.BuildDataDMO().styleReport_2_BuildData(_condition, _className, _loginUserVO); //
	}

	/**
	 * 风格报表3生成构造数据(图表chart)
	 * @param _condition
	 * @param _className
	 * @param _loginUserVO
	 * @return
	 * @throws Exception
	 */
	public BillChartVO styleReport_3_BuildData(HashMap _condition, String _className, CurrLoginUserVO _loginUserVO) throws Exception {
		return new cn.com.infostrategy.bs.report.style3.BuildDataDMO().styleReport_3_BuildData(_condition, _className, _loginUserVO); //
	}

	public void clearTempTableSql() throws Exception {
		String sessionid = new WLTInitContext().getCurrSession().getHttpsessionid(); //当前的用户Session...
		CommDMO dmo = new CommDMO();
		dmo.executeUpdateByDS(null, "delete from pub_sqlincons where sessionid='" + sessionid + "'");
	}

	public String getOracleLongInSql(String[] itemvalues, String itemkey) throws Exception {
		ReportDMO dmo = new ReportDMO();
		return dmo.getOracleLongInSqlCondition(itemvalues, itemkey);
	}

	/**
	 * 取得打印模板
	 */
	public PubPrintTempletVO getPubPrintTempletVO(String _templetCode) throws Exception {
		return new ReportDMO().getPubPrintTempletVO(_templetCode); //返回模板VO..
	}

	/**
	 * 导入模板
	 */
	public void importPrintTemplet(String _billtempletCode, String _templetCode) throws Exception {
		new ReportDMO().importPrintTemplet(_billtempletCode, _templetCode);
	}

	/**
	 * 保存模板定义
	 */
	public void savePrintTempletItemBands(String _templetcode, PubPrintItemBandVO[] _itemBandVOs) throws Exception {
		new ReportDMO().savePrintTempletItemBands(_templetcode, _itemBandVOs);
	}

	public void deleteOnePrintTemplet(String _id) throws Exception {
		new ReportDMO().deleteOnePrintTemplet(_id);
	}
}
