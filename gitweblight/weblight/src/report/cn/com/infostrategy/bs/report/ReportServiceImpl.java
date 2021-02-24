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
 * ����ģ��ķ���!!
 * @author user
 *
 */
public class ReportServiceImpl implements ReportServiceIfc {

	/**
	 * ȡ��һ��SessionID,��ͬʱ����������˻���
	 */
	public String registerWebCallSessionID(WebCallParVO _parvo) throws Exception {
		WLTInitContext initContext = new WLTInitContext();
		String str_sessionid = initContext.getCurrSession().getHttpsessionid(); //sessionID
		String str_loginUserId = initContext.getCurrSession().getLoginUserId(); //��¼�û�����
		String str_callid = str_sessionid + "_" + str_loginUserId + "_" + System.currentTimeMillis(); //����ĳ���㷨�����һ��CallID.���û�ID+IP+��ǰʱ��
		WebCallIDFactory.getInstance().putWebCallSessionId(str_callid, _parvo); //�������������,��һ��Web����,WebCallServlet�ͻ�Ӹû�����ȡ������
		return str_callid; //���ظ�ID
	}

	/**
	 * ����һ��Զ�̵��ã�ȡ��һ��OfficeServletCall��SeeeionId,Ϊ��һ��Office�ؼ�����ʱ��
	 * @param _parVO
	 * @return
	 * @throws Exception
	 */
	public String registerOfficeCallSessionID(OfficeCompentControlVO _vo) throws Exception {
		WLTInitContext initContext = new WLTInitContext();
		String str_sessionid = initContext.getCurrSession().getHttpsessionid();
		String str_loginUserId = initContext.getCurrSession().getLoginUserId(); //��¼�û�����
		String str_callid = str_sessionid + "_" + str_loginUserId + "_" + System.currentTimeMillis(); //����ĳ���㷨�����һ��CallID.���û�ID+IP+��ǰʱ��
		WebCallIDFactory.getInstance().putOfficeCallSessionId(str_callid, _vo); //ע��
		return str_callid; //����sessionId
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
		String str_loginUserId = initContext.getCurrSession().getLoginUserId(); //��¼�û�����
		String str_htmlcontentid = str_sessionid + "_" + str_loginUserId + "_" + System.currentTimeMillis(); //����ĳ���㷨�����һ��CallID.���û�ID+IP+��ǰʱ��
		WebCallIDFactory.getInstance().putHtmlContentSessionId(str_htmlcontentid, _htmlContent); //�������������,��һ��Web����,WebCallServlet�ͻ�Ӹû�����ȡ������
		return str_htmlcontentid; //���ظ�ID
	}

	/**
	 * ȡ��һ��Html���е�����!!�������!!!
	 * @return
	 * @throws Exception
	 */
	public String getHtmlContent(WebCallParVO _parVO) throws Exception {
		String str_className = _parVO.getCallClassName(); //������
		HashMap map_pars = _parVO.getParsMap(); //����
		WLTInitContext initContext = new WLTInitContext(); //
		StringBuffer sb_html = new StringBuffer(); //
		try {
			WebCallBeanIfc bean = (WebCallBeanIfc) Class.forName(str_className).newInstance(); //
			sb_html.append(bean.getHtmlContent(map_pars)); //
			initContext.commitAllTrans(); //�ύ����!!
		} catch (Exception ex) {
			initContext.rollbackAllTrans(); //�ع���������..
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
		sb_html.append("<!-- ʵ�ʵ��õ�ҳ��������[" + str_className + "] -->\r\n"); //
		return sb_html.toString(); //
	}

	public void deleteOneBillCellTemplet(String _id) throws Exception {
		boolean delete = TBUtil.getTBUtil().getSysOptionBooleanValue("����ģ��ɾ��ʱ�Ƿ�ɾ��ҵ������", true);//̫ƽ����ģ���������ϰ���ģ�飬��������ݣ�pub_billcelltemplet_data��������ɾ���������Ӳ��������/2018-12-28��
		ArrayList list = new ArrayList();
		if (delete) {
			list.add("delete from pub_billcelltemplet_data where templet_h_id='" + _id + "'");
		}
		list.add("delete from pub_billcelltemplet_d    where templet_h_id='" + _id + "'"); //
		list.add("delete from pub_billcelltemplet_h    where id='" + _id + "'"); //
		new CommDMO().executeBatchByDS(null, list);
	}

	/**
	 * ���ɵ���Excelģ�����ݵ�SQL
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

		String[] str_keys = hv_data[0].getKeys(); //ȡ�������ֶ���.
		String str_columnnames = ""; //�����ֶ���
		String str_columvalues = ""; //�����ֶ�ֵ
		for (int i = 0; i < str_keys.length; i++) {
			str_columnnames = str_columnnames + str_keys[i];
			if (str_keys[i].equalsIgnoreCase("id")) { //���������
				str_columvalues = str_columvalues + (_dbtype.equals(WLTConstants.SQLSERVER) ? "dbo." : "") + "fn_getsequence('S_PUB_BILLCELLTEMPLET_H')"; //
			} else {
				str_columvalues = str_columvalues + getInsertValue(hv_data[0].getStringValue(str_keys[i])); //
			}

			if (i != str_keys.length - 1) {
				str_columnnames = str_columnnames + ",";
				str_columvalues = str_columvalues + ",";
			}

			if (_iswrap) { //�������
				//str_columnnames = str_columnnames + "\r\n";
				str_columvalues = str_columvalues + " --" + str_keys[i] + "\r\n";
			}
		}

		sb_sql.append("update pub_sequence set currvalue=currvalue+1 where sename='S_PUB_BILLCELLTEMPLET_H';\r\n");
		if (_iswrap) { //�������
			sb_sql.append("insert into pub_billcelltemplet_h (" + str_columnnames + ")\r\nvalues\r\n(\r\n" + str_columvalues + ");\r\n\r\n");
		} else {
			sb_sql.append("insert into pub_billcelltemplet_h (" + str_columnnames + ") values (" + str_columvalues + ");\r\n\r\n");
		}

		// ��ÿ��Item���к�����д���ļ�
		String _sql_item_context = "Select * From pub_billcelltemplet_d Where templet_h_id='" + str_parent_id + "'";
		HashVO[] hv_item = commDMO.getHashVoArrayByDS(null, _sql_item_context); //
		for (int i = 0; i < hv_item.length; i++) {
			String[] str_itemkeys = hv_item[i].getKeys(); //�õ���������
			String str_itemcolumnnames = ""; //�����ֶ���
			String str_iemcolumvalues = ""; //�����ֶ�ֵ
			for (int j = 0; j < str_itemkeys.length; j++) {
				str_itemcolumnnames = str_itemcolumnnames + str_itemkeys[j];
				if (str_itemkeys[j].equalsIgnoreCase("id")) { //���������
					str_iemcolumvalues = str_iemcolumvalues + (_dbtype.equals(WLTConstants.SQLSERVER) ? "dbo." : "") + "fn_getsequence('S_PUB_BILLCELLTEMPLET_D')"; //��������ȡ��..
				} else if (str_itemkeys[j].equalsIgnoreCase("templet_h_id")) { //����Ǹ���¼����
					str_iemcolumvalues = str_iemcolumvalues + (_dbtype.equals(WLTConstants.SQLSERVER) ? "dbo." : "") + "fn_getsequence('S_PUB_BILLCELLTEMPLET_H')"; //��������ȡ��..
				} else {
					str_iemcolumvalues = str_iemcolumvalues + getInsertValue(hv_item[i].getStringValue(str_itemkeys[j])); //
				}

				if (j != str_itemkeys.length - 1) {
					str_itemcolumnnames = str_itemcolumnnames + ",";
					str_iemcolumvalues = str_iemcolumvalues + ",";
				}

				if (_iswrap) { //�������
					//str_itemcolumnnames = str_itemcolumnnames + "\r\n";
					str_iemcolumvalues = str_iemcolumvalues + " --" + str_itemkeys[j] + "\r\n";
				}
			}

			sb_sql.append("update pub_sequence set currvalue=currvalue+1 where sename='S_PUB_BILLCELLTEMPLET_D';\r\n");
			if (_iswrap) { //�������
				sb_sql.append("insert into pub_billcelltemplet_d (" + str_itemcolumnnames + ")\r\nvalues\r\n(\r\n" + str_iemcolumvalues + ");\r\n\r\n");
			} else {
				sb_sql.append("insert into pub_billcelltemplet_d (" + str_itemcolumnnames + ") values (" + str_iemcolumvalues + ");\r\n\r\n");
			}
		}

		return sb_sql.toString(); //
	}

	/**
	 * ����_value��ȷ��Ҫ�����ֵ
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
	 * _tablename ����
	 * _excelitemkey excel�ؼ��ֶ���
	 * _corpitemkey �����ֶ���
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
	 * ����BillCellVO����HTML���
	 * @param _cellVO
	 * @return
	 * @throws Exception
	 */
	public String getHtmlTableByBillCellVO(BillCellVO _cellVO) throws Exception {
		return null; //
	}

	/**
	 * ��һ��HashVO[]���з���ͳ��..
	 */
	public HashVO[] groupHashVOs(HashVO[] _hvs, String _groupFunc, String _groupField) throws Exception {
		return new ReportDMO().groupHashVOs(_hvs, _groupFunc, _groupField); //
	}

	/**
	 * ȡ��ά�ȶ�����
	 * @param _builderClassName
	 * @return
	 * @throws Exception
	 */
	public HashMap getMultiLevelReportGroup(String _builderClassName) throws Exception {
		MultiLevelReportDataBuilderIFC builder = (MultiLevelReportDataBuilderIFC) Class.forName(_builderClassName).newInstance();
		String[] str_groupfieldnames = builder.getGroupFieldNames(); //���������ֶ�
		String[] str_sumfieldnames = builder.getSumFiledNames(); //������ܵ��ֶ�
		HashMap sortmap = builder.getGroupFieldOrderConfig(); //ȡ���ֶ���������

		HashMap hm_return = new HashMap(); //
		hm_return.put("AllGroupFields", str_groupfieldnames); //
		hm_return.put("AllComputeFields", str_sumfieldnames); //
		hm_return.put("groupfieldorder", sortmap);//�����ֶ��������ã�

		//ȡ��Ԥ�õ�ͳ������
		if (builder instanceof MultiLevelReportDataBuilderAdapter) {
			BeforeHandGroupTypeVO[] beforeHandVOs_grid = ((MultiLevelReportDataBuilderAdapter) builder).getBeforehandGroupType_Grid(); //�����Ԥ������
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

			BeforeHandGroupTypeVO[] beforeHandVOs_chart = ((MultiLevelReportDataBuilderAdapter) builder).getBeforehandGroupType_Chart(); //ͼ���Ԥ������!
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
			BeforeHandGroupTypeVO[] beforeHandVOs_Splider = ((MultiLevelReportDataBuilderAdapter) builder).getBeforehandGroupType_Splider(); //�״��Ԥ������
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

			hm_return.put("drillclass", ((MultiLevelReportDataBuilderAdapter) builder).getDrillActionClassPath()); //�ͻ�����ȡ��ʵ���ݵ�ʵ����
			hm_return.put("drilltempletcode", ((MultiLevelReportDataBuilderAdapter) builder).getDrillTempletCode()); //�ͻ�����ȡ��ʵ���ݵ�ģ��
			hm_return.put("drillgroupbind", ((MultiLevelReportDataBuilderAdapter) builder).getDrilGroupBind()); //ά��֮��İ󶨣���
			hm_return.put("zeroReportConfMap", ((MultiLevelReportDataBuilderAdapter) builder).getZeroReportConfMap()); //��㱨���ƣ���
			hm_return.put("dateGroupDefineMap", ((MultiLevelReportDataBuilderAdapter) builder).getDateGroupDefineMap()); //ʱ���ֶζ���ά�ȣ���
		}

		return hm_return; //
	}

	/*
	 * ���ݲ�ѯ����,��������ʵ������
	 */
	public HashVO[] queryMultiLevelReportData(HashMap _queryCondition, String _builderClassName, String[] _colGroupFields, String[][] _computeFields, boolean _isCtrlDown) throws Exception {
		MultiLevelReportDataBuilderIFC builder = (MultiLevelReportDataBuilderIFC) Class.forName(_builderClassName).newInstance();
		String[] str_allGroups = builder.getGroupFieldNames(); //��������!!!
		HashVO[] hvs_initData = null; //
		if (_isCtrlDown && builder instanceof MultiLevelReportDataBuilderAdapter) { //�������Ctrl��,
			HashVO[] demoVOs = ((MultiLevelReportDataBuilderAdapter) builder).buildDemoData(); //���������Ctrl��,����ȡʵ������,����Demo����
			if (demoVOs != null) { //
				hvs_initData = demoVOs; //
			} else {
				//���Ϊ��
				//ǿ�д�����̬����!!
				hvs_initData = builder.buildReportData(_queryCondition); //ʵ�ʹ������ϸ����!!!
			}
		} else { //���û�а�Ctrl��
			hvs_initData = builder.buildReportData(_queryCondition); //ʵ�ʹ������ϸ����!!!
		}

		if (_colGroupFields == null) { //���û�з���,����ѯ������ϸ
			return hvs_initData;
		}

		//ƴ�ɷ����е��ַ���,����SQLһ�����﷨,���з������!!
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
			if (_computeFields[i][2].indexOf("-") > 0) { //�����@
				str_realField = _computeFields[i][2].substring(0, _computeFields[i][2].lastIndexOf("-")); ////
			} else {
				str_realField = _computeFields[i][2]; //
			}
			str_computeFields = str_computeFields + _computeFields[i][0] + "(" + str_realField + ") " + _computeFields[i][2]; //��ĳ���ֶν��л���,����sum(���) ���
			if (i != _computeFields.length - 1) {
				str_computeFields = str_computeFields + ","; //
			}
		}

		ReportUtil reportUtil = new ReportUtil(); //
		//TBUtil.getTBUtil().writeHashToHtmlTableFile(hvs_initData, "C:/�鿴1.htm"); //����ļ����´��������ӵ�����ʲô!!
		//Ԭ����20121102���     ��VO���б�������	��ȡ�ֶε�����Ϊ---��ѡ��---�滻Ϊ���ַ�������ҪΪ��ȡ������ͳ�Ƶ�ʱ��������ʾ---��ѡ��---������
		String str_temp[] = str_groupFieldNames.split(",");//�Ƚ��ֶ��Զ��Ž�ȡ
		if (null != hvs_initData) {
			for (int i = 0; i < hvs_initData.length; i++) {
				for (int j = 0; j < str_temp.length; j++) {
					String temp = hvs_initData[i].getStringValue(str_temp[j]);
					if (null != temp && temp.equals("---��ѡ��---")) {
						hvs_initData[i].setAttributeValue(str_temp[j], "");
					}
				}
			}
		}
		//TBUtil.getTBUtil().writeHashToHtmlTableFile(hvs_initData, "C:/�鿴2.htm"); //����ļ����´��������ӵ�����ʲô!!
		HashVO[] hvs_group = reportUtil.groupHashVOs(hvs_initData, str_computeFields, str_groupFieldNames); //���ɻ�������,����ǰ̨��Ҫ����������!��Ϊ�����ǰ̨������������,���ܲŻ��!
		//
		return hvs_group; //����ͳ�ƺ������,
	}

	//��ȡ��ά�������ϸ����!!!
	public HashVO[] queryMultiLevelReportDataDrillDetail(HashMap _queryCondition, String _builderClassName, String _ids) throws Exception {
		String[] str_idItems = TBUtil.getTBUtil().split(_ids, ";"); //�ָ�!!
		MultiLevelReportDataBuilderIFC builder = (MultiLevelReportDataBuilderIFC) Class.forName(_builderClassName).newInstance(); //		
		HashVO[] hvs_initData = builder.buildReportData(_queryCondition); //�������������ϸ����!!
		ArrayList al_return = new ArrayList(); //
		for (int i = 0; i < hvs_initData.length; i++) {
			String str_id = hvs_initData[i].getStringValue("id"); //
			if (str_id != null && TBUtil.getTBUtil().isExistInArray(str_id, str_idItems)) { //���������������!!
				al_return.add(hvs_initData[i]); //
			}
		}
		return (HashVO[]) al_return.toArray(new HashVO[0]); //���ؽ��!!
	}

	/**
	 * ��һ��ͼ������һ��Word�ļ�!!!���ص���Wrod��XML��ʽ
	 * @param _jFreeChart
	 * @return
	 * @throws Exception
	 */
	public String exportChartAsWordXML(JFreeChart _jFreeChart) throws Exception {
		WordFileUtil wordFileUtil = new WordFileUtil();
		String head = wordFileUtil.getWordFileBegin();//�õ�WORDͷ
		String end = wordFileUtil.getWordFileEnd();//�õ�WORDβ
		//		StringBuffer imgs = new StringBuffer();

		//byte[] byte_1 = 
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ChartUtilities.writeChartAsJPEG(out, _jFreeChart, 10, 10);//.writeBufferedImageAsJPEG(arg0, _jFreeChart.createBufferedImage(5, 5)); //
		byte[] byte_1 = new byte[out.size()];
		out.write(byte_1);
		out.flush();
		String str_img64code = new String(org.jfreechart.xml.util.Base64.encode(byte_1)); //jfreechart-1.0.14�����޸���·��jfreeΪjfreechart �����/2013-06-09��
		String center = wordFileUtil.getImageXml(str_img64code, 500, 500);

		String wordgragic = head + center + end;
		return wordgragic;
	}

	/**
	 * ��񱨱�1�Ĺ������ݵĹ���!!
	 */
	public HashVO[] styleReport_1_BuildData(HashMap _condition, String _className, CurrLoginUserVO _loginUserVO) throws Exception { //
		StyleReport_1_BuildDataIFC builder = (StyleReport_1_BuildDataIFC) Class.forName(_className).newInstance();//
		HashVO[] hvs = builder.buildDataByCondition(_condition, _loginUserVO); //
		return hvs;
	}

	/**
	 * ��񱨱�2���ɹ�������
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
	 * ��񱨱�3���ɹ�������(ͼ��chart)
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
		String sessionid = new WLTInitContext().getCurrSession().getHttpsessionid(); //��ǰ���û�Session...
		CommDMO dmo = new CommDMO();
		dmo.executeUpdateByDS(null, "delete from pub_sqlincons where sessionid='" + sessionid + "'");
	}

	public String getOracleLongInSql(String[] itemvalues, String itemkey) throws Exception {
		ReportDMO dmo = new ReportDMO();
		return dmo.getOracleLongInSqlCondition(itemvalues, itemkey);
	}

	/**
	 * ȡ�ô�ӡģ��
	 */
	public PubPrintTempletVO getPubPrintTempletVO(String _templetCode) throws Exception {
		return new ReportDMO().getPubPrintTempletVO(_templetCode); //����ģ��VO..
	}

	/**
	 * ����ģ��
	 */
	public void importPrintTemplet(String _billtempletCode, String _templetCode) throws Exception {
		new ReportDMO().importPrintTemplet(_billtempletCode, _templetCode);
	}

	/**
	 * ����ģ�嶨��
	 */
	public void savePrintTempletItemBands(String _templetcode, PubPrintItemBandVO[] _itemBandVOs) throws Exception {
		new ReportDMO().savePrintTempletItemBands(_templetcode, _itemBandVOs);
	}

	public void deleteOnePrintTemplet(String _id) throws Exception {
		new ReportDMO().deleteOnePrintTemplet(_id);
	}
}
