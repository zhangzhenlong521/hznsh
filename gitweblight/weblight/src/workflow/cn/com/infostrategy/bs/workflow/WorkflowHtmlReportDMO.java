package cn.com.infostrategy.bs.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.FrameWorkCommServiceImpl;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WebCallBeanIfc;
import cn.com.infostrategy.bs.mdata.MetaDataDMO;
import cn.com.infostrategy.bs.sysapp.SysAppDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.LinkForeignTableDefineVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;

/**
 * ���ɹ���������ĺ�̨������!!!
 * 
 * @author xch
 * 
 */
public class WorkflowHtmlReportDMO implements WebCallBeanIfc {

	private TBUtil tbUtil = null; //
	private CommDMO commDMO = null; //

	private HashMap allConfMap = null; //
	private String str_prinstanceid = null; //����ʵ��
	private String str_loginUserId = null; //��¼�û�
	private BillVO billVO = null; //����VO
	private String str_templetCode = null; //ģ�����!
	private String showType = null; //
	private String showStyle = null; //����ҵ�����ť��ʾ���ͬ��������Ӹ����Կ���������
	private String[] userroles = null; //

	/**
	 * ȡ��html����!!
	 */
	public String getHtmlContent(HashMap map) throws Exception {
		allConfMap = map; //
		str_prinstanceid = (String) map.get("prinstanceid"); //����ʵ���ֶ�,������Ϣ�����Դ������ȡ��!!!
		str_loginUserId = (String) map.get("userid"); //��¼�û�id!

		userroles = (String[]) map.get("roles"); //��ɫ
		showType = map.get("showType") == null ? "1" : map.get("showType").toString(); //��ʾ����
		showStyle = map.get("showStyle") == null ? "1" : map.get("showStyle").toString(); //��ʾ���
		billVO = (BillVO) map.get("billvo"); //����ֵ!!
		String htmlreport = map.get("html�ļ���") == null ? "" : map.get("html�ļ���").toString();
		String wordreport = map.get("word�ļ���") == null ? "" : map.get("word�ļ���").toString();
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<!-- ���в�����ϵͳԤ�õ�(��ʽ��${������}):\r\n"); //
		sb_html.append("��templetname�������̼�ء�\r\n");
		sb_html.append("�������ˡ�������ʱ�䡿�������˲��š��������˻������\r\n");
		sb_html.append("�������ˡ�������ʱ�䡿������������������˲��š����������͡����������ڡ�\r\n");
		sb_html.append("��ĳ�����ϵĴ�����Ϣ�ϲ�[���ڱ���]����ĳ�����ϵĴ�����[���ڱ���]����ĳ�����ϵĴ���ʱ��[���ڱ���]����ĳ�����ϵĴ������[���ڱ���]����ĳ�����ϵĴ�������������[���ڱ���]��\r\n");
		sb_html.append("��ĳ�����ϵ����������̴�����Ϣ�ϲ�[���ڱ���]��\r\n");
		sb_html.append("-->\r\n"); //
		sb_html.append("<!-- ����ʵ��id=[" + str_prinstanceid + "],�û�id=[" + str_loginUserId + "] -->\r\n"); //

		str_templetCode = billVO.getTempletCode(); //ģ�����!!
		String str_billtype = billVO.getStringValue("billtype"); // ��������
		String str_busitype = billVO.getStringValue("busitype"); // ҵ������
		sb_html.append("<!-- ��������=[" + str_billtype + "],ҵ������=[" + str_busitype + "],ģ�����=[" + str_templetCode + "] -->\r\n"); ////

		if (!showStyle.equals("1")) { //ֻ��ʾ���̴�����־!!
			sb_html.append("<!-- ��Ϊshowstyle=1,����ֱ�����!! -->\r\n"); //
			sb_html.append(getDefaultHtmlReport()); //
			return sb_html.toString();//
		}

		//		String str_sql = "select id,processid,htmlreport,wordreport, report_roles from pub_workflowassign where billtypecode='" + str_billtype + "' and busitypecode='" + str_busitype + "'"; //
		//		HashVO[] hvo_report = getCommDMO().getHashVoArrayByDS(null, str_sql); //Ӧ�����ҵ�һ����¼!!
		//		if (hvo_report.length > 0) { // ����ҵ���Ӧ����,���ҵ������õļ�¼!!!
		sb_html.append("<!-- ���ݵ�������[" + str_billtype + "],ҵ������[" + str_busitype + "],�ӱ�pub_workflowassign���ҵ�ƥ���¼,html����ģ���ļ���=[" + htmlreport + "],word����ģ���ļ���=[" + wordreport + "] -->\r\n"); //
		//			String htmlreport = hvo_report[0].getStringValue("htmlreport"); //html����!!
		if (htmlreport != null && !htmlreport.trim().equals("")) { //���Htmlģ�岻Ϊ��,��ʹ��Html����ģ��!!
			//				if (htmlreport.indexOf(";") > 0) { //����зֺ�!!
			//					HashMap tmpMap = getTBUtil().convertStrToMapByExpress(htmlreport, ";", "=>", true); //
			//					String str_savetableName = billVO.getSaveTableName(); //����ı���!
			//					if (tmpMap.containsKey(str_savetableName.toLowerCase())) { //����ҵ�
			//						htmlreport = (String) tmpMap.get(str_savetableName.toLowerCase()); //
			//					} else if (tmpMap.containsKey("*")) {
			//						htmlreport = (String) tmpMap.get("*"); //
			//					} else {
			//						sb_html.append("����html������[" + htmlreport + "],û���ҵ��뱾���ݱ����[" + str_savetableName + "]ƥ���htmlģ��!\r\n"); //
			//						return sb_html.toString(); //ֱ�ӷ���!!!
			//					}
			//				}
			sb_html.append("<!-- ʹ��Html��ʽ�ı���ģ��[" + htmlreport + "] -->\r\n"); //
			String str_filecontent = new FrameWorkCommServiceImpl().getServerResourceFile(htmlreport, "GBK"); //ȡ���ļ�����
			String[] str_macroitems = getTBUtil().getMacroList(str_filecontent); //ȡ�����к�����!!
			for (int i = 0; i < str_macroitems.length; i++) {
				if (str_macroitems[i].startsWith("${") && str_macroitems[i].endsWith("}")) { // ���������!!
					String str_macrokey = str_macroitems[i].substring(2, str_macroitems[i].length() - 1); //ȡ�ú����!
					if (str_macrokey.equals("������")) {
						String str_createserName = getCommDMO().getStringValueByDS(null, "select t2.name from pub_wf_prinstance t1,pub_user t2 where t1.creater = t2.id and t1.id='" + str_prinstanceid + "'"); ////
						sb_html.append(str_createserName); //
					} else if (str_macrokey.equals("����ʱ��")) {
						String str_createtime = getCommDMO().getStringValueByDS(null, "select createtime from pub_wf_prinstance where id='" + str_prinstanceid + "'"); //
						sb_html.append(str_createtime); //
					} else if (str_macrokey.equals("�����˲���")) {
						String[] str_createDeptName = getCommDMO().getStringArrayFirstColByDS(null, "select t4.name from pub_wf_prinstance t1,pub_user t2,pub_user_post t3,pub_corp_dept t4 where t1.creater = t2.id and t2.id=t3.userid and t3.userdept=t4.id and t1.id='" + str_prinstanceid + "'"); //
						if (str_createDeptName != null && str_createDeptName.length > 0) {
							for (int j = 0; j < str_createDeptName.length; j++) {
								sb_html.append(str_createDeptName[j] + ";"); //
							}
						} else {
							sb_html.append("û���ҵ������˲���"); //
						}
					} else if (str_macrokey.equals("��������������")) { //����Ϊ�������У�����
						String str_createCorpType = getCommDMO().getStringValueByDS(null, "select t4.bl_fengh_name from pub_wf_prinstance t1,pub_user t2,pub_user_post t3,pub_corp_dept t4 where t1.creater = t2.id and t2.id=t3.userid and t3.userdept=t4.id and t1.id='" + str_prinstanceid + "'"); //
						if (str_createCorpType == null || "".equals(str_createCorpType)) {
							sb_html.append("����");
						} else {
							sb_html.append(str_createCorpType); //
						}
					} else if (str_macrokey.equals("������")) { //
						String str_endUserName = getCommDMO().getStringValueByDS(null, "select t2.name from pub_wf_prinstance t1,pub_user t2 where t1.lastsubmiter=t2.id and t1.id='" + str_prinstanceid + "'"); //
						sb_html.append(str_endUserName); ////
					} else if (str_macrokey.equals("����ʱ��")) { //
						String str_endtime = getCommDMO().getStringValueByDS(null, "select endtime from pub_wf_prinstance where id='" + str_prinstanceid + "'");
						sb_html.append(str_endtime); ////
					} else if (str_macrokey.equals("�������")) { //
						String str_endmsg = getCommDMO().getStringValueByDS(null, "select lastsubmitmsg from pub_wf_prinstance where id='" + str_prinstanceid + "'");
						sb_html.append(str_endmsg); ////
					} else if (str_macrokey.equals("�����˲���")) { //
						String[] str_createDeptName = getCommDMO().getStringArrayFirstColByDS(null, "select t4.name from pub_wf_prinstance t1,pub_user t2,pub_user_post t3,pub_corp_dept t4 where t1.lastsubmiter = t2.id and t2.id=t3.userid and t3.userdept=t4.id and t1.id='" + str_prinstanceid + "'"); //
						if (str_createDeptName != null && str_createDeptName.length > 0) {
							for (int j = 0; j < str_createDeptName.length; j++) {
								sb_html.append(str_createDeptName[j] + ";"); //
							}
						} else {
							sb_html.append("û���ҵ������˲���"); //
						}
					} else if (str_macrokey.equals("��������")) {
						String str_endtype = getCommDMO().getStringValueByDS(null, "select endtype from pub_wf_prinstance where id='" + str_prinstanceid + "'");
						sb_html.append(str_endtype); ////
					} else if (str_macrokey.equals("��������")) {
						String str_endactivity = getCommDMO().getStringValueByDS(null, "select t2.wfname from pub_wf_prinstance t1,pub_wf_activity t2 where t1.lastsubmitactivity=t2.id and t1.id='" + str_prinstanceid + "'"); //
						sb_html.append(str_endactivity); ////
					} else if (str_macrokey.startsWith("ĳ�����ϵĴ�����Ϣ�ϲ�")) {
						sb_html.append(getOneActivityLastSubmitInfoSpan(str_macrokey)); //
					} else if (str_macrokey.startsWith("ĳ�����ϵĴ�����[")) {
						sb_html.append(getOneActivityLastSubmitInfoItem(str_macrokey, "realsubmitername")); //
					} else if (str_macrokey.startsWith("ĳ�����ϵĴ�����������Ϣ")) {//sunfujun/20120806/������Ҫ��ϵ��ʽ
						sb_html.append(getOneActivitySubmiterInfo(str_macrokey)); //
					} else if (str_macrokey.startsWith("ĳ�����ϵĴ�������������")) {
						sb_html.append(getOneActivityLastSubmitInfoItem(str_macrokey, "realsubmitcorpname")); //
					} else if (str_macrokey.startsWith("ĳ�����ϵĴ���ʱ��")) {
						sb_html.append(getOneActivityLastSubmitInfoItem(str_macrokey, "submittime")); //
					} else if (str_macrokey.startsWith("ĳ�����ϵĴ������")) {
						sb_html.append(getOneActivityLastSubmitInfoItem(str_macrokey, "submitmessage")); //
					} else if (str_macrokey.startsWith("ĳ�����ϵ����������̴�����Ϣ�ϲ�")) {
						sb_html.append(getOneActivityChildWFLastSubmitInfoSpan(str_macrokey)); //���������̼���ز������!!������ڱ������������еĻ��ڱ���!!��ʵ�Ժ�Ҳ���Ը�����̱�����ϻ��ڱ���!
					} else if (str_macrokey.equals("���̼��")) { //ֱ�ӽ����̼��ͳͳ�����!
						if (!showType.equals("3")) {
							sb_html.append(getMessageByGroup()); // ��̬�������̼�ص�html!!
						} else if (showType.equals("3")) { //
							sb_html.append(getWorkFlowTraceHtml());
						}
					} else if (str_macrokey.equals("templetname")) { // ���ģ����
						sb_html.append(billVO.getTempletName()); //
					} else if (str_macrokey.startsWith("������")) { //����ֱ��дһ������,Ȼ�������,ȡ���䷵�ص�html���!!
						sb_html.append(getRefectClassHtml(str_macrokey)); //
					} else {
						String str_billvalue = billVO.getStringViewValue(str_macrokey); //
						if (billVO.containsKey(str_macrokey)) { //���û��
							sb_html.append(convertStrToHtml(str_billvalue)); //ȡ����!
						} else {
							sb_html.append("��${" + str_macrokey + "}û���塿"); //
							//sb_html.append("������û�ж���key[" + str_macrokey + "]"); //
						}
					}
				} else {
					sb_html.append(str_macroitems[i]); // ֱ�Ӽ���
				}
			}
		} else if (wordreport != null && !"".equals(wordreport.trim())) { // ���wordģ�岻Ϊ��,��ʹ��Word��ʽ�ı���ģ��!!
			sb_html.append("<!-- ʹ��Word��ʽ�ı���ģ��[" + wordreport + "] -->\r\n"); //
			String str_filecontent = new FrameWorkCommServiceImpl().getServerResourceFile(wordreport, "UTF-8"); // ȡ���ļ�����
			String[] str_macroitems = getTBUtil().getMacroList(str_filecontent); // �õ����ж�..
			for (int i = 0; i < str_macroitems.length; i++) { // �������к�����
				if (str_macroitems[i].startsWith("${") && str_macroitems[i].endsWith("}")) { // ���������!!
					System.out.println("ת��ǰ:" + str_macroitems[i]); //
					String str_macrokey = clearWordFormat(str_macroitems[i]); // �Ƚص�<>��,��Ϊ��ʱword���Զ����м���������<>��
					System.out.println("ת����:" + str_macrokey); //
					System.out.println(); //
					String str_billvalue = billVO.getStringViewValue(str_macrokey); //
					sb_html.append(str_billvalue == null ? "" : str_billvalue);
				} else {
					sb_html.append(str_macroitems[i]);
				}
			}

			new FrameWorkCommServiceImpl().writeFile("C:/xyz.doc", sb_html.toString(), "UTF-8"); // ����word����,���ڷ���������,����������ڿͻ��˵��õ�����..
			return "111";
		} else { // ������߶�Ϊ��!!
			sb_html.append("<!-- htmlģ����wordģ�嶨�嶼Ϊ��!ֱ��ʹ��Ĭ�ϸ�ʽ���,���̼�ؽ������: -->\r\n"); //
			sb_html.append(getDefaultHtmlReport()); //
		}
		//		} else {
		//			sb_html.append("<!--���ݵ��ݱ���[" + str_billtype + "],ҵ�����ͱ���[" + str_busitype + "],û���ҵ�ӳ�������.: -->\r\n"); //
		//			sb_html.append(getHtmlHeader()); //
		//			sb_html.append(getWorkFlowTraceHtml());
		//			sb_html.append(getHtmlTail());
		//		}
		return sb_html.toString();//
	}

	/**
	 * ���һ���ַ�������word��ʽ,��<>����������
	 */
	private String clearWordFormat(String _text) {
		String str_text = _text.substring(2, _text.length() - 1); //
		String str_remain = str_text; //
		String str_return = null; //
		int li_count = 1;
		for (;;) {
			if (str_remain.indexOf("<") == 0) { // �����<��ʼ
				str_remain = str_remain.substring(str_remain.indexOf(">") + 1, str_remain.length()); // ȥ����һ��.
			} else {
				if (str_remain.indexOf("<") > 0) {
					str_return = str_remain.substring(0, str_remain.indexOf("<")); //
				} else {
					str_return = str_remain; //
				}
				break;
			}

			li_count++;
			if (li_count >= 3000) {
				break; // ����һ��ѭ��!!
			}
		}
		return str_return; //
	}

	private String getDefaultHtmlReport() throws Exception {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append(getHtmlHeader()); //
		sb_html.append(getBillVOAsHtml()); //
		sb_html.append(getHtmlTail()); //
		return sb_html.toString(); //
	}

	/**
	 * ����ģ�������BillVO,�Զ�����һ�����!������̶�������N��!!
	 * �Ժ�Ҫ��ģ���ж���ĳһ���Ƿ���뱨�����!! ֻ�ܲ�������!!
	 * @return
	 */
	private String getBillVOAsHtml() throws Exception { //
		StringBuilder sb_html = new StringBuilder(); //
		//����ģ����xml���棬���¹��������������û�б����ݡ�����ͳһȥģ��Ľӿڡ�����[20121105]
		MetaDataDMO dmo = ServerEnvironment.getMetaDataDMO();
		Pub_Templet_1VO templet = dmo.getPub_Templet_1VO(str_templetCode);
		Pub_Templet_1_ItemVO templetItemVOs[] = templet.getItemVos(); //����ģ���ӱ�VO
		ArrayList al_temp = new ArrayList(); //
		for (int i = 0; i < templetItemVOs.length; i++) {
			String str_itemkey = templetItemVOs[i].getItemkey(); //
			String str_itemname = templetItemVOs[i].getItemname(); //
			String str_itemtype = templetItemVOs[i].getItemtype(); //
			String str_tiptext = templetItemVOs[i].getItemtiptext(); //
			if (str_tiptext != null) {
				str_itemname = str_tiptext; //
			}
			if (str_itemtype.equals("Label") || str_itemtype.equals("��ť")) {
				continue;
			}
			String str_isexport = templetItemVOs[i].getCardisexport(); //�Ƿ����,�����,�������,һ�����!
			if (str_isexport != null) { //�����Ϊ��Ϊ���!�����Ϊ��,�����!! ��ǰ��Ϊ��ʱĬ�����,���ڸĳ�Ϊ��ʱĬ�ϲ����!! ��Ϊʵ�����ڱ���ֻ���ٲ�������Ҫ�����!! ������������С!��xch/2012-11-09��
				if (str_isexport.equals("0")) { //�����
				} else if (str_isexport.equals("1")) { //���������
					appendBillDataBuilder(al_temp, str_itemkey, str_itemname, "����"); //
				} else if (str_isexport.equals("2")) { //��ȫ�����,����ʱ������,�ݲ�ʵ��,�Ժ�Ҫʵ�ֵ�!
					appendBillDataBuilder(al_temp, str_itemkey, str_itemname, "ȫ��"); //
				}
			}
		}

		sb_html.append("\r\n"); //
		sb_html.append("<table align=\"left\" style=\"border-collapse:collapse;\" border=\"0\" cellpadding=\"5\">\r\n"); ////
		HashVO[] hvs_mycorp = new SysAppDMO().getParentCorpVOByMacro(2, this.str_loginUserId, "$������"); //
		String str_myCorpName = null; //
		String str_myCorpType = null; //
		if (hvs_mycorp != null && hvs_mycorp.length > 0) {//�����/2016-06-24��
			str_myCorpName = hvs_mycorp[0].getStringValue("name"); //
			str_myCorpType = hvs_mycorp[0].getStringValue("corptype"); //
		}

		//������
		String str_tile = (String) allConfMap.get("����");
		sb_html.append("<tr><td align=\"center\" colspan=4><B>" + ServerEnvironment.getProperty("LICENSEDTO") + "" + str_myCorpName + "-" + this.billVO.getTempletName() + "" + (str_tile == null ? "" : str_tile) + "</B></td></tr>\r\n\r\n"); //��������Ϣ���!!!

		//���������!!!!����ÿһ������!
		int li_label_width = 105; //
		int li_text_width = 205; //
		for (int i = 0; i < al_temp.size(); i++) {
			String[] str_rowItem = (String[]) al_temp.get(i); //��һ������!
			if (str_rowItem.length >= 2) { //���������,��ǳ��򵥡�
				sb_html.append("<tr>\r\n"); //
				String str_itemkey_1 = str_rowItem[0].substring(str_rowItem[0].indexOf("��") + 1, str_rowItem[0].indexOf("��")); //��һ��!
				String str_itemname_1 = str_rowItem[0].substring(str_rowItem[0].indexOf("��") + 1, str_rowItem[0].length()); //
				sb_html.append("<td class=\"style_trtd\" align=\"right\" bgcolor=\"#F8F8F8\" width=" + li_label_width + " alt=\"" + str_itemkey_1 + "\">" + convertStrToHtml(str_itemname_1, str_itemkey_1 + "(itemnameΪ��)") + "</td>\r\n"); //
				sb_html.append("<td class=\"style_trtd\" width=" + li_text_width + ">" + convertStrToHtml(billVO.getStringViewValue(str_itemkey_1), "&nbsp;") + "</td>\r\n"); //
				String str_itemkey_2 = str_rowItem[1].substring(str_rowItem[1].indexOf("��") + 1, str_rowItem[1].indexOf("��")); //�ڶ���!
				String str_itemname_2 = str_rowItem[1].substring(str_rowItem[1].indexOf("��") + 1, str_rowItem[1].length()); //
				sb_html.append("<td class=\"style_trtd\" align=\"right\" bgcolor=\"#F8F8F8\" width=" + li_label_width + " >" + convertStrToHtml(str_itemname_2, str_itemkey_2 + "(itemnameΪ��)") + "</td>\r\n"); //
				sb_html.append("<td class=\"style_trtd\" width=" + li_text_width + ">" + convertStrToHtml(billVO.getStringViewValue(str_itemkey_2), "&nbsp;") + "</td>\r\n"); //
				sb_html.append("</tr>\r\n\r\n"); //
			} else if (str_rowItem.length == 1) { //���ֻ��һ��,��Ҫ����ȫ�л��ǰ���
				String str_itemkey_1 = str_rowItem[0].substring(str_rowItem[0].indexOf("��") + 1, str_rowItem[0].indexOf("��")); //
				String str_itemname_1 = str_rowItem[0].substring(str_rowItem[0].indexOf("��") + 1, str_rowItem[0].length()); //
				if (str_rowItem[0].startsWith("ȫ�С�")) { //�����ȫ��,�����ݿ�����
					sb_html.append("<tr>\r\n"); // 
					sb_html.append("<td class=\"style_trtd\" align=\"right\" bgcolor=\"#F8F8F8\" width=" + li_label_width + " alt=\"" + str_itemkey_1 + "\">" + convertStrToHtml(str_itemname_1, str_itemkey_1 + "(itemnameΪ��)") + "</td>\r\n"); //
					sb_html.append("<td class=\"style_trtd\" width=" + (li_text_width + li_label_width + li_text_width) + " colspan=3>" + convertStrToHtml(billVO.getStringViewValue(str_itemkey_1), "&nbsp;") + "</td>\r\n"); //
					sb_html.append("</tr>\r\n\r\n"); //
				} else if (str_rowItem[0].startsWith("���С�")) { //����ǰ���,��ֻ���ǰһ��,��һ���ǲ���ֵ!
					sb_html.append("<tr>\r\n"); //
					sb_html.append("<td class=\"style_trtd\" align=\"right\" bgcolor=\"#F8F8F8\" width=" + li_label_width + " alt=\"" + str_itemkey_1 + "\">" + convertStrToHtml(str_itemname_1, str_itemkey_1 + "(itemnameΪ��)") + "</td>\r\n"); //
					sb_html.append("<td class=\"style_trtd\" width=" + li_text_width + ">" + convertStrToHtml(billVO.getStringViewValue(str_itemkey_1), "&nbsp;") + "</td>\r\n"); //

					sb_html.append("<td class=\"style_trtd\" align=\"right\" bgcolor=\"#F8F8F8\" width=" + li_label_width + " alt=\"" + str_itemkey_1 + "\">&nbsp;</td>\r\n"); //
					sb_html.append("<td class=\"style_trtd\" width=" + li_text_width + ">&nbsp;</td>\r\n"); //
					sb_html.append("</tr>\r\n\r\n"); //
				}
			}
		}

		//����ָ���Ļ�������ģ��ƥ��!,������������
		boolean isFilterByCustomer = ("Y".equals(allConfMap.get("�Ƿ��Զ�������"))); //�п������Զ�������
		HashMap consMap = new HashMap(); //
		if (isFilterByCustomer) {
			//String str_formula = "����=aa,bb,cc;���̻���=���պϹ沿��,���쵼;��ɫ=���Ÿ�����,���Ҹ�����,���쵼"; //
			String str_formula = (String) allConfMap.get("������ʽ"); //����=name,code;���̻���=���պϹ沿��,���쵼;��ɫ=���Ÿ�����;���Ҹ�����;
			consMap = getTBUtil().convertStrToMapByExpress(str_formula, ";", "="); //
			String[] str_keys = (String[]) consMap.keySet().toArray(new String[0]); //
			for (int i = 0; i < str_keys.length; i++) {
				String str_value = (String) consMap.get(str_keys[i]); //
				//System.out.println("[" + str_keys[i] + "]=[" + str_value + "]"); //
				if (str_value != null) {
					consMap.put(str_keys[i], getTBUtil().split(str_value, ",")); //
				}
			}
		}
		StringBuilder sb_billColValues = new StringBuilder(); //
		if (isFilterByCustomer) { //���ָ���������ֶ�,��
			String[] str_billCols = (String[]) consMap.get("����"); //new String[] { "aa", "bb", "cc" }; //
			if (str_billCols != null) {
				for (int b = 0; b < str_billCols.length; b++) { //���������ֶ�..
					for (int i = 0; i < templetItemVOs.length; i++) { //����ģ�����!
						if (templetItemVOs[i].getItemkey().equalsIgnoreCase(str_billCols[b])) { //����ҵ�
							String str_itemName = templetItemVOs[i].getItemname(); //����
							String str_itemtipText = templetItemVOs[i].getItemtiptext(); //�Ժ������Ҫʹ�ð���
							if (str_itemName == null || str_itemName.trim().equals("")) { //�������Ϊ��,���ð���!!
								sb_billColValues.append(str_itemtipText + "<br>"); //
							} else {
								sb_billColValues.append(str_itemName + "<br>"); //
							}
							String str_colValue = billVO.getStringViewValue(str_billCols[b]); //
							str_colValue = (str_colValue == null ? "��&nbsp;" : str_colValue);
							sb_billColValues.append(str_colValue + "<br><br>"); //
						}
					}
				}
			}
		}

		sb_html.append("\r\n<!-- ��������������ĺ����߼�: -->\n"); //
		sb_html.append("<!-- 1.���ȸ��ݴ�����������Ƿ���\"���������\"�Ⱦ���\"���̻���\"��\"��ɫ\"����,�����,��������ж�! -->\n"); //
		sb_html.append("<!-- 2.���û�ж���,��ֱ�Ӹ�������ͼ�еĸ������ϵĲ���\"�Ƿ񵼳�����\"���������!! -->\n"); //
		sb_html.append("<!-- 3.������������������ֹ��˺�,��Ҫ��������\"���������������Ƿ����������\",����������,��ֻ����������ڻ���������!�����ҵĻ��������ǡ�һ�����в��š�,��ĳ�����С�һ�����С�,�����֮! -->\n"); //

		//���ݱ��˵Ļ�������,Ȼ�����ֻ���뱾��� ,�����������[�������в���],��ֻ���[��������]���еļ�¼!!!
		//�ʴ���һ��ӿ����һ������,Ȼ��Ҫ��ֻ����������������!���е�����(������ҵ����)Ҫ����Ե���������!!! ����˵,�е�����ʵ����һ������,������һ����,��ʱ����Ҫ�������!
		boolean isOnlyExportMyGroup = getTBUtil().getSysOptionBooleanValue("���������������Ƿ����������", false); //���Ҫ��ɲ���xch!!
		sb_html.append("<!-- ϵͳ���������������������Ƿ���������롿=��" + isOnlyExportMyGroup + "�� -->\n");

		//�����������@�������С�����=name,code;���̻���=���պϹ沿��,���쵼;��ɫ=���Ÿ�����;���Ҹ�����;��
		String[] str_filt_actNames = (String[]) consMap.get("���̻���"); //new String[] { "���պϹ沿��", "���쵼" }; //
		String[] str_filt_roleNames = (String[]) consMap.get("��ɫ"); // new String[] { "���Ÿ�����", "���Ҹ�����", "���쵼" }; //

		//���Ϲ�����������ʷ!
		ArrayList list_1 = new ArrayList(); //
		HashVO[] hvs_tasks = getMonitorDealPoolTasks(this.str_prinstanceid, this.str_loginUserId); //ȡ�ø�����������..
		//�����������ݽ��й���
		for (int i = 0; i < hvs_tasks.length; i++) { //
			String str_issubmit = hvs_tasks[i].getStringValue("issubmit"); //
			if (!"Y".equals(str_issubmit)) { //
				continue; //
			}
			String str_parentinstanceid = hvs_tasks[i].getStringValue("parentinstanceid"); //����ʵ��id
			String str_wfactname = hvs_tasks[i].getStringValue("curractivity_wfname"); //��������prinstanceid_fromparentactivityname
			String str_parentActName = hvs_tasks[i].getStringValue("prinstanceid_fromparentactivityname"); //��������
			String str_deptname = hvs_tasks[i].getStringValue("participant_userdeptname"); //��������!!!
			String str_userName = hvs_tasks[i].getStringValue("participant_username"); //��Ա����!!!
			String str_userRoleNames = hvs_tasks[i].getStringValue("participant_userrolename"); //��Ա��ɫ����!!!!
			String str_isExport = hvs_tasks[i].getStringValue("curractivity_isneedreport"); //������ָ���Ƿ񵼳�!!
			String str_fromparentactivityisneedreport = hvs_tasks[i].getStringValue("prinstanceid_fromparentactivityisneedreport"); //��Դ�����Ƿ����!!

			String str_thisActName = str_wfactname; //
			if (str_parentinstanceid != null) { //������ӳ�,��ʹ�ø������̵Ļ�������!
				str_thisActName = str_parentActName; //
			}
			str_thisActName = getTBUtil().replaceAll(str_thisActName, "\r", ""); //
			str_thisActName = getTBUtil().replaceAll(str_thisActName, "\n", ""); //
			str_thisActName = getTBUtil().replaceAll(str_thisActName, " ", ""); //

			sb_html.append("\r\n\r\n<!-- ��׼�����˵�:���ڡ�" + str_thisActName + "�� ������" + str_deptname + "�� ��Ա��" + str_userName + "���� -->\n"); //

			boolean isImport = true; //
			if (isFilterByCustomer) { //ֻ����ض����ڵ����,����"���ɺϹ沿��",��������������ʱ����������!!�����������ֻ��������ɲ����������!! �����Ķ���Ҫ!!!
				//�����Ƿ�ƥ��ɹ�
				boolean isActMatched = false;
				if (str_filt_actNames == null || str_filt_actNames.length <= 0) { //���û��ָ������,��Ĭ����Ϊ������!
					isActMatched = true; //
				} else {
					for (int j = 0; j < str_filt_actNames.length; j++) { //������ǰ����
						if (str_thisActName.indexOf(str_filt_actNames[j]) >= 0) { //�����������Ļ���������ð��������е�ĳһ������,�����Ϊ�ɹ���!
							isActMatched = true; //
							break; //
						}
					}
				}
				sb_html.append("<!-- ����ָ���������ɫ����ģʽ,��������ƥ����:" + isActMatched + ",������Ļ�������[" + str_thisActName + "],������[" + getSBStr(str_filt_actNames) + "] -->\n");

				//��ɫ�Ƿ�ƥ��ɹ�
				boolean isRoleMatched = false; //
				String[] str_thisRoleItems = new String[0]; //
				if (str_filt_roleNames == null || str_filt_roleNames.length <= 0) { //���û����,����Ϊ�ɹ�!!
					isRoleMatched = true; //
				} else {
					if (str_userRoleNames != null) {
						str_thisRoleItems = getTBUtil().split(str_userRoleNames, ";"); //���������ж����ɫ
					}
					for (int j = 0; j < str_filt_roleNames.length; j++) { //������ǰ����!!
						boolean isFind = false; //
						for (int k = 0; k < str_thisRoleItems.length; k++) { //����������������н�ɫ!!
							if (str_thisRoleItems[k].indexOf(str_filt_roleNames[j]) >= 0) {
								isFind = true;
								break; //
							}
						}
						if (isFind) { //
							isRoleMatched = true; //
							break; //
						}
					}
				}
				sb_html.append("<!-- ��ɫ����ƥ����:" + isRoleMatched + ",���������Ա��ɫ����[" + getSBStr(str_thisRoleItems) + "],������[" + getSBStr(str_filt_roleNames) + "] -->\n");
				if (isActMatched && isRoleMatched) {
					isImport = true;
				} else {
					isImport = false; //
				}
			} else { //��׼���ж��߼�,����������ͼ�е�XX�����Ƿ񵼳�����������!!
				if ("Y".equals(str_isExport) || "Y".equals(str_fromparentactivityisneedreport)) {
					sb_html.append("<!-- ֱ�Ӹ��ݲ���\"�Ƿ񵼳�����\"ģʽ����Ϊ����ͼ����ָ���û��ڲ��뵼��,Ҫ���(�����滹��������������) -->\n"); //
					isImport = true; //
				} else {
					sb_html.append("<!-- ֱ�Ӹ��ݲ���\"�Ƿ񵼳�����\"ģʽ����Ϊ����ͼ����û��ָ���û��ڲ��뵼��,���Ա������� -->\n"); //
					isImport = false; //
				}
			}

			String str_thisPrGroupName = hvs_tasks[i].getStringValue("curractivity_belongdeptgroup"); //��Դ��������!!
			if (str_parentinstanceid != null) { //�����������,���ø��׵�����
				str_thisPrGroupName = hvs_tasks[i].getStringValue("prinstanceid_fromparentactivitybldeptGroup"); //��Դ��������!,��������
			}

			//System.out.println("[" + str_myCorpType + "][" + str_thisPrGroupName + "]"); //
			if (str_thisPrGroupName == null) {
				sb_html.append("<!-- û�ж�����,���Բ����κ������˼���,ͳͳ���! -->\n"); //
			} else {
				if (str_myCorpType != null && str_myCorpType.startsWith(str_thisPrGroupName)) { //����ͬ��,�ż���
					hvs_tasks[i].setAttributeValue("$isSameGroupAsMe", "Y"); //
					sb_html.append("<!-- ���������������[" + str_thisPrGroupName + "]���ҵĻ�������[" + str_myCorpType + "]ƥ��ɹ�,��������(ע��ǰ������Ѿ�������)!! -->\n"); //
				} else {
					hvs_tasks[i].setAttributeValue("$isSameGroupAsMe", "N"); //
					if (isOnlyExportMyGroup) { //
						sb_html.append("<!-- ��������,ԭ����:Ҫ����Ҫ������(��ֻ��������(��)),�����������������[" + str_thisPrGroupName + "]���ҵĻ�������[" + str_myCorpType + "]ƥ��ʧ��! -->\n"); //
						isImport = false; //
					} else {
						sb_html.append("<!-- ���������������[" + str_thisPrGroupName + "]���ҵĻ�������[" + str_myCorpType + "]��Ȼ��ƥ��,����Ϊû��Ҫ������鲻ͬ������,���Բ�������(ע��ǰ������Ѿ�������)!! -->\n"); //
					}
				}
			}
			if (isImport) {
				list_1.add(hvs_tasks[i]); //
			}
		}
		HashVO[] hvs_filter = (HashVO[]) list_1.toArray(new HashVO[0]); //
		sb_html.append("\r\n<!-- ����������֮ǰ���嵥(��Щ���ݺ���Ҫ������ͬ����/��Ա�ϲ����һ���������) -->\n"); //
		for (int i = 0; i < hvs_filter.length; i++) { //
			sb_html.append("<!-- ����[" + hvs_filter[i].getStringValue("participant_userdeptname") + "],��Ա[" + hvs_filter[i].getStringValue("participant_username") + "] -->\n"); //
		}
		sb_html.append("<!-- End -->\r\n\r\n"); //

		//�ʴ��ͻ�˵�Ӹ�����!!
		if (!isFilterByCustomer) {
			sb_html.append("<tr><td  class=\"style_trtd\" bgcolor=\"#FFFFD7\" colspan=4 align=\"center\">&nbsp;����������嵥��&nbsp;</td></tr>\n"); //
		}

		ArrayList al_list = getDealMsgDistinctList(hvs_filter, true); //��Ψһ�Ժϲ�����

		StringBuilder sb_wfListHtml = new StringBuilder(); //
		int li_allItemCount = 0; //
		boolean isFirst = true; //
		for (int i = 0; i < al_list.size(); i++) {
			ArrayList al_row = (ArrayList) al_list.get(i); //
			li_allItemCount = li_allItemCount + al_row.size(); //
			for (int j = 0; j < al_row.size(); j++) {
				HashVO hvoitem = (HashVO) al_row.get(j); //
				sb_wfListHtml.append("<!-- ����[" + hvoitem.getStringValue("participant_userdeptname") + "],��Ա[" + hvoitem.getStringValue("participant_username") + "] -->\n"); //
			}
			if (isFilterByCustomer) { //����ǲ��������ʽ,���編�������,����Ҫ�����ϵ��¼ӵ������������е����ݺ���һ��!!
				for (int j = 0; j < al_row.size(); j++) {
					sb_wfListHtml.append("<tr>\n"); //
					sb_wfListHtml.append("<td class=\"style_trtd\" align=\"left\" valign=\"top\" colspan=3>" + getTextHtml(al_row.get(j)) + "</td>\n"); //�кϲ�
					sb_wfListHtml.append("</tr>\n"); //
				}
			} else {
				HashVO firstVO = (HashVO) al_row.get(0); //
				//hvs_tasks[i].getStringValue("$isSameGroupAsMe") +
				String str_deptName = firstVO.getStringValue("participant_userdeptname"); //��������,����ɽ��ʡ����-������Դ��
				if ("Y".equals(firstVO.getStringValue("$isSameGroupAsMe"))) { //�Ƿ�����ͬ��!!???
					if (str_deptName.indexOf("-") > 0) {
						str_deptName = str_deptName.substring(str_deptName.indexOf("-") + 1, str_deptName.length()); //ȡ���һ������??
					}
				}
				str_deptName = "<span title=\"" + firstVO.getStringValue("participant_userdeptname") + "\">" + str_deptName + "</span>"; //������ȫ����ʾ����!

				if (isFirst) { //��һ�����������ű��
					str_deptName = "<font color=\"blue\">�������š�</font>" + str_deptName; //
					isFirst = false; //
				}

				String str_parentinstanceid = firstVO.getStringValue("parentinstanceid"); //������
				if (str_parentinstanceid != null) {
					str_deptName = "<font color=\"blue\">����졿</font>" + str_deptName; //
				}

				if (al_row.size() == 1) {
					sb_wfListHtml.append("<tr>\n"); //
					sb_wfListHtml.append("<td class=\"style_trtd\" align=\"center\" bgcolor=\"#F8F8F8\" width=" + li_label_width + ">" + str_deptName + "</td>\n"); //�кϲ�
					sb_wfListHtml.append("<td class=\"style_trtd\" align=\"left\" valign=\"top\" colspan=3>" + getTextHtml(al_row.get(0)) + "</td>\r\n"); //�кϲ�
					sb_wfListHtml.append("</tr>\n"); //
				} else { //����ж��
					sb_wfListHtml.append("<tr>\n"); //
					sb_wfListHtml.append("<td class=\"style_trtd\" align=\"center\" bgcolor=\"#F8F8F8\" width=" + li_label_width + " rowspan=" + al_row.size() + ">" + str_deptName + "</td>\n"); //�кϲ�
					sb_wfListHtml.append("<td class=\"style_trtd\" align=\"left\"  valign=\"top\" colspan=3>" + getTextHtml(al_row.get(0)) + "</td>\n"); //�кϲ�
					sb_wfListHtml.append("</tr>\n"); //
					for (int j = 1; j < al_row.size(); j++) {
						sb_wfListHtml.append("<tr>\n"); //
						sb_wfListHtml.append("<td class=\"style_trtd\" align=\"left\" valign=\"top\" colspan=3>" + getTextHtml(al_row.get(j)) + "</td>\n"); //�кϲ�
						sb_wfListHtml.append("</tr>\n"); //
					}
				}
			}
		}
		if (isFilterByCustomer) { //������ֹ���ӵ�,�������������еĺ���һ��!!
			sb_html.append("<tr>\n");
			sb_html.append("<td class=\"style_trtd\" align=\"center\"  valign=\"middle\" bgcolor=\"#F6F6F6\" rowspan=" + (li_allItemCount + 1) + ">����������</td>\n"); //
			sb_html.append("<td class=\"style_trtd\" align=\"left\"    valign=\"top\" colspan=3>" + sb_billColValues.toString() + "</td>\n"); //��������Ϣ���!!!
			sb_html.append("</tr>\n"); //
			sb_html.append(sb_wfListHtml.toString()); //
		} else {
			sb_html.append(sb_wfListHtml.toString()); //
		}
		sb_html.append("<tr><td class=\"style_trtd\" align=\"right\" valign=\"top\" colspan=4>����ʱ��:" + getTBUtil().getCurrTime() + "<br><br></td></tr>\r\n"); //��������Ϣ���!!!
		sb_html.append("</table>\r\n\r\n"); //
		return sb_html.toString(); //
	}

	private String getTextHtml(Object _obj) {
		HashVO hvo = (HashVO) _obj; //
		StringBuilder sb_text = new StringBuilder(); //
		String str_userName = hvo.getStringValue("participant_username"); //����������,����Ȩʱ,���ﻹ���쵼,��ʵ������������쵼�����! ������
		String str_userRoleName = hvo.getStringValue("participant_userrolename"); //�����߽�ɫ����

		String str_userLabel = "������"; //
		String[] str_cons = new String[] { "���쵼", "���Ÿ�����", "���Ҹ�����" }; //
		if (str_userRoleName != null) {
			String[] str_roleItems = getTBUtil().split1(str_userRoleName, ";"); //
			for (int i = 0; i < str_cons.length; i++) {
				boolean isFrind = false; //
				for (int j = 0; j < str_roleItems.length; j++) {
					if (str_roleItems[j].indexOf(str_cons[i]) > 0) {
						isFrind = true; //
						break; //
					}
				}

				if (isFrind) {
					str_userLabel = str_cons[i]; //
					break; //
				}
			}

		}
		sb_text.append(str_userLabel + ":" + str_userName + "&nbsp;&nbsp;&nbsp;����ʱ��:" + hvo.getStringValue("submittime") + "<br>"); //
		sb_text.append("�������:" + hvo.getStringValue("submitmessage_real", "") + "<br>"); //submitmessage,ֱ���ý��ܺ��
		sb_text.append("<br>"); //
		return sb_text.toString(); //
	}

	private String getSBStr(String[] _array) {
		if (_array == null) {
			return ""; //
		}
		StringBuilder sb_text = new StringBuilder(); //
		for (int i = 0; i < _array.length; i++) {
			sb_text.append(_array[i] + ";"); //
		}
		return sb_text.toString(); //
	}

	/**
	 * ȡ��Ψһ�Ե��嵥...��һ������ֻ�����һ��,һ�������ڲ���һ����ԱҲֻ�����һ��!
	 * @return
	 */
	private ArrayList getDealMsgDistinctList(HashVO[] _hvs, boolean _isOnlyLastUser) {
		LinkedHashMap deptMap = new LinkedHashMap(); //�����ж��Ƿ�ֻ���ֹ�һ��!

		int li_maxcount = 2; //����4����������������
		for (int i = 0; i < _hvs.length; i++) { //
			String str_deptname = _hvs[i].getStringValue("participant_userdeptname"); //��������!!!
			int li_count = getTBUtil().findCount(str_deptname, "-"); //
			if (li_count > li_maxcount) { //����������
				li_maxcount = li_count; //
			}
		}

		for (int i = 0; i < _hvs.length; i++) { //
			String str_deptname = _hvs[i].getStringValue("participant_userdeptname"); //��������!!!
			str_deptname = trimDeptNameByPos(str_deptname, li_maxcount); //
			String str_userName = _hvs[i].getStringValue("participant_username"); //��Ա����!!!
			if (deptMap.containsKey(str_deptname)) { //�����ע��
				LinkedHashMap userMap = (LinkedHashMap) deptMap.get(str_deptname); //
				if (_isOnlyLastUser) { //�ʴ�˵һ��������ÿ���˵���������Ҫ,����ҵ˵ֻҪһ�����ŵ��������
					userMap.clear(); //�����,�ټ���,����Ȼ�������һ���˵����
					userMap.put(str_userName, _hvs[i]); //�����,�������,��ʲô������
				} else {
					if (!userMap.containsKey(str_userName)) { //��������Ա������
						userMap.put(str_userName, _hvs[i]); //�����,�������,��ʲô������
					}
				}
			} else { //���������������!
				LinkedHashMap userMap = new LinkedHashMap(); //
				userMap.put(str_userName, _hvs[i]); //
				deptMap.put(str_deptname, userMap); //
			}
		}

		ArrayList list_dist = new ArrayList(); //
		LinkedHashMap[] list_Maps = (LinkedHashMap[]) deptMap.values().toArray(new LinkedHashMap[0]); //
		for (int i = 0; i < list_Maps.length; i++) {
			HashVO[] vos = (HashVO[]) list_Maps[i].values().toArray(new HashVO[0]); //����VO
			ArrayList al_row = new ArrayList(); //
			for (int j = 0; j < vos.length; j++) {
				al_row.add(vos[j]); //
			}
			list_dist.add(al_row); //
		}

		return list_dist; //
	}

	//����������������ϵ��и�,��õ����һ��,��Ϊ����ҵ������ͬʱ���֡�����-�ƻ�����-�ۺϹ������롾����-�ƻ����񲿡�,��ʵ����Ӧ����һ��!!
	//���ǵ���ͬ��Ŀ�еĲ㼶���ܲ�һ��,����˵�������"2"Ӧ���ǿ����õĲ���!!!
	private String trimDeptNameByPos(String _deptname, int li_maxcount) {
		//int li_maxcount = 2; //��������Ժ���Ը���������Ŀ��������!
		if (li_maxcount > 0) { //
			int li_count = getTBUtil().findCount(_deptname, "-"); //���ܹ��м����и�???
			if (li_count >= li_maxcount) { //���������!!!
				String str_1 = ""; //
				String str_remain = _deptname; //
				for (int i = 0; i < li_maxcount; i++) {
					str_1 = str_1 + str_remain.substring(0, str_remain.indexOf("-")) + "-"; //�ڼ����и�??
					str_remain = str_remain.substring(str_remain.indexOf("-") + 1, str_remain.length()); //
				}
				str_1 = str_1.substring(0, str_1.length() - 1); //
				return str_1; //
			} else {
				return _deptname; //
			}
		} else {
			return _deptname; //
		}
	}

	//��Ƭ�е���ʱ���ڰ�����ȫ�еļ���,��Ҫ����!
	private void appendBillDataBuilder(ArrayList _list, String _itemKey, String _itemName, String _type) {
		if (_type.equals("����")) {
			if (_list.size() == 0) { //���û��,��ֱ�Ӽ���
				_list.add(new String[] { "���С�" + _itemKey + "��" + _itemName }); //	
			} else { //���ǰ����,��������һ��ƨ�ɺ����,��������һ��
				String[] str_lastItem = (String[]) _list.get(_list.size() - 1); //
				if (str_lastItem.length >= 2) { //����Ѿ���2����,��ǿ�л���!
					_list.add(new String[] { "���С�" + _itemKey + "��" + _itemName }); //
				} else { //���ֻ��һ��,��Ҫ����ȫ�л��ǰ���,�����ȫ��,��ǿ�л���,����ǰ���,�����!
					if (str_lastItem[0].startsWith("ȫ�С�")) { //�����ȫ��
						_list.add(new String[] { "���С�" + _itemKey + "��" + _itemName }); //
					} else { //����ǰ���
						String[] str_newItem = new String[] { str_lastItem[0], "���С�" + _itemKey + "��" + _itemName }; //
						_list.set(_list.size() - 1, str_newItem); //��������
					}
				}
			}
		} else if (_type.equals("ȫ��")) { //����ӵ��Ǹ�ȫ��,��ǳ���!ֱ���¼���һ��
			_list.add(new String[] { "ȫ�С�" + _itemKey + "��" + _itemName }); //
		}
	}

	/**
	 * ȡ�ù�������־�ı��.�����Ѿ�����Ҫ��
	 * @return
	 */
	private String getWorkFlowTraceHtml() throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("\r\n<table align=\"left\" width=\"70%\" style=\"border-collapse:collapse;\" border=\"0\" cellpadding=\"5\">\r\n"); //
		HashVO[] hvs_tasks = getMonitorDealPoolTasks(this.str_prinstanceid, this.str_loginUserId); // ȡ�ø�����������..
		if (hvs_tasks.length > 0) {
			for (int i = 0; i < hvs_tasks.length; i++) {
				String str_issubmit = hvs_tasks[i].getStringValue("issubmit"); //�Ƿ��ύ
				if ("Y".equals(str_issubmit)) {
					continue; //
				}
				String str_wfActName = hvs_tasks[i].getStringValue("curractivity_wfname2"); //��������
				String str_submitUserCorp = hvs_tasks[i].getStringValue("realsubmitcorpname", ""); //
				String str_submitUserName = hvs_tasks[i].getStringValue("realsubmitername", ""); //
				String str_submitTime = hvs_tasks[i].getStringValue("submittime", ""); //�ύʱ��
				String str_submitmessage = hvs_tasks[i].getStringValue("submitmessage", ""); //�ύ���!
				str_submitmessage = getTBUtil().replaceAll(str_submitmessage, "\n", "<br>"); //
				sb_html.append("<tr><td class=\"style_trtd\"  bgcolor=\"#F8F8F8\">�����̲��衿:" + str_wfActName + "</td><td class=\"style_trtd\"  bgcolor=\"#F8F8F8\">���ύ������:" + str_submitUserCorp + "<td></tr>\r\n"); //
				sb_html.append("<tr><td class=\"style_trtd\"  bgcolor=\"#F8F8F8\">���ύ��Ա��:" + str_submitUserName + "</td><td class=\"style_trtd\"  bgcolor=\"#F8F8F8\">���ύʱ�䡿:" + str_submitTime + "<td></tr>\r\n"); //
				sb_html.append("<tr><td colspan=2  class=\"style_trtd\">���ύ�����:" + str_submitmessage + "\r\n<br><br></td></tr>\r\n");
			}
		}
		sb_html.append("</table>\r\n");
		return sb_html.toString(); //
	}

	private String convertStrToHtml(String _text) {
		return convertStrToHtml(_text, "��"); //
	}

	private String convertStrToHtml(String _text, String _nvl) {
		if (_text == null) {
			return _nvl; //
		}
		return getTBUtil().replaceAll(_text, "\n", "<br>"); //
	}

	/**
	 * ȡ�����̼��ʱ���������¼
	 */
	public HashVO[] getMonitorDealPoolTasks(String _prinstanceid, String _loginUserId) throws Exception { //
		String str_rootInstId = getCommDMO().getStringValueByDS(null, "select rootinstanceid from pub_wf_prinstance where id='" + _prinstanceid + "'"); //
		if (str_rootInstId == null) {
			str_rootInstId = _prinstanceid; //
		}
		return new WorkFlowServiceImpl().getMonitorTransitions(str_rootInstId, true, false, _loginUserId); //
	}

	/**
	 * ȡ��ĳ�������ϵ����һ��������Ϣ��
	 * @param _macroKey
	 * @param _itemKey
	 * @return
	 * @throws Exception
	 */
	private String getOneActivityLastSubmitInfoItem(String _macroKey, String _itemKey) throws Exception {
		String str_activitycode = _macroKey.substring(_macroKey.indexOf("[") + 1, _macroKey.length() - 1); //
		HashVO[] hvs = getOneActivitySubmitInfo(str_activitycode); //
		if (hvs.length > 0) {
			return hvs[hvs.length - 1].getStringValue(_itemKey); //
		} else {
			return "&nbsp;";
		}
	}

	private String getOneActivityLastSubmitInfoSpan(String _macroKey) throws Exception {
		String str_activitycode = _macroKey.substring(_macroKey.indexOf("[") + 1, _macroKey.length() - 1); //
		HashVO[] hvs = getOneActivitySubmitInfo(str_activitycode); //
		if (hvs.length > 0) {
			int li_pos = hvs.length - 1; //
			String str_submiter = hvs[li_pos].getStringValue("realsubmitername"); //�ύ��
			//String str_submitercorp = hvs[li_pos].getStringValue("realsubmitcorpname"); //
			String str_submittime = hvs[li_pos].getStringValue("submittime"); //�ύʱ��
			String str_submitmessage = hvs[li_pos].getStringValue("submitmessage"); //�ύ���
			StringBuilder sb_info = new StringBuilder(); //
			sb_info.append("�ύ��:" + str_submiter + "&nbsp;&nbsp;" + "�ύʱ��:" + str_submittime + "<br>"); //
			sb_info.append(convertStrToHtml(str_submitmessage) + "<br>"); //
			return sb_info.toString(); //
		} else {
			return "&nbsp;";
		}
	}

	/**
	 * ȡ��ĳ���������ֵ!����:cn.com.pushworld.bs.MyHtmlDMO.getHtml("����","3","�Ƿ��ѡ","Y")
	 * ��ʵ��������෽��������뷵��ֵ����HashMap,Ȼ��ƽ̨�Զ��������е���żλ��Ϊkey/value�͵������������!�Ժ�����չ�������ʱ�ǳ�����,ֻҪ��ƽ̨�����������map�м������ֵ����!
	 * ����ֵҲ��map,��Ҳ��Ϊ���Ժ���չ����,�����Է��ض��ֵ,Ȼ��ƽ̨�����ж����!! ���ڱ��뷵��һ��key��ΪHtmlContent��ֵ!
	 * @param _macroKey
	 * @return
	 * @throws Exception
	 */
	private String getRefectClassHtml(String _macroKey) throws Exception {
		String str_clsName = _macroKey.substring(_macroKey.indexOf("[") + 1, _macroKey.length() - 1); //�������������ֺ�����[����],����:��������[cn.com.pushworld.bs.MyHtmlDMO.getHtml("����","3","�Ƿ��ѡ","Y")]��
		HashMap parMap = new HashMap(); //���!!
		parMap.put("����������DMO", this); //���Լ��ͽ�ȥ!���������߾����õ���ʹ�ø����е���ط���!!
		parMap.put("$����˵��", "����ֵ�б�����һ��key��ΪHtmlContent��ֵ,���ص�html���ݾͷ���������!"); //�����������˵����Ϊ�˿�����Ա��debugʱ��֪�����ʹ���������
		HashMap returnMap = getTBUtil().reflectCallCommMethod(str_clsName, parMap); //�������!!����һ�����ܵ�,����������չ�Ļ���!!����Ժ������չ,����ֵҲ������չ!
		if (returnMap == null) {
			return "ִ�з����ࡾ" + str_clsName + "��ʧ��,��������̨�鿴��ϸ!ע�ⷽ������뷵��ֵ���붼��HashMap"; //
		}
		String str_html = (String) returnMap.get("HtmlContent"); //
		if (str_html == null) {
			return "ִ�з����ࡾ" + str_clsName + "���ɹ�,�����ص�Map��û����Ϊ\"HtmlContent\"��key"; //
		} else {
			return str_html; //
		}
	}

	/**
	 * ���ݻ��ڱ���ȡ��ĳһ�����ϵ��ύ��Ϣ!!
	 * @param _activityCode
	 * @return
	 */
	public HashVO[] getOneActivitySubmitInfo(String _activityCode) throws Exception {
		String str_rootInstId = getCommDMO().getStringValueByDS(null, "select rootinstanceid from pub_wf_prinstance where id='" + str_prinstanceid + "'"); //
		if (str_rootInstId == null) {
			str_rootInstId = str_prinstanceid; //
		}
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select "); //
		sb_sql.append("t1.submittime,"); //�ύʱ��
		sb_sql.append("t1.realsubmitername,"); //�ύ����
		sb_sql.append("t1.realsubmitcorpname,"); //�ύ������
		sb_sql.append("t1.submitmessage "); //�ύ���
		sb_sql.append("from pub_wf_dealpool t1,pub_wf_activity t2 ");
		sb_sql.append("where t1.rootinstanceid='" + str_rootInstId + "'"); //
		sb_sql.append("and t1.issubmit='Y' "); //
		sb_sql.append("and t1.curractivity=t2.id "); //
		sb_sql.append("and t2.code='" + _activityCode + "' "); //
		sb_sql.append("order by t1.id"); //
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, sb_sql.toString()); //
		return hvs; //
	}

	/**
	 * ��ȡĳһ���ڴ����˵�������Ϣ
	 * @param _activityCode
	 * @return
	 * @throws Exception
	 */
	public String getOneActivitySubmiterInfo(String _macroKey) throws Exception {
		String inf = _macroKey.substring(_macroKey.indexOf("[") + 1, _macroKey.length() - 1);
		if (inf.indexOf(",") > 0) {
			String str_activitycode = inf.split(",")[0];
			String key = inf.split(",")[1];
			HashVO[] hvs = getOneActivitySubmitInfo(str_activitycode);
			if (hvs.length > 0) {
				String userid = hvs[hvs.length - 1].getStringValue("realsubmiter");
				if (userid != null && !"".equals(userid.trim())) {
					String rtnstr = getCommDMO().getStringValueByDS(null, "select " + key + " from pub_user where id='" + userid + "' ");
					if (rtnstr == null) {
						return "&nbsp;";
					}
					return rtnstr;
				} else {
					return "&nbsp;";
				}
			} else {
				return "&nbsp;";
			}
		}
		return "&nbsp;";
	}

	/**
	 * ȡ��ĳһ�������е����������̵�������!!
	 * @param _macroKey
	 * @return
	 * @throws Exception
	 */
	private String getOneActivityChildWFLastSubmitInfoSpan(String _macroKey) throws Exception {
		String str_activitycode = _macroKey.substring(_macroKey.indexOf("[") + 1, _macroKey.length() - 1); //
		HashVO[] hvs = getOneActivityChildWFLastSubmitVO(str_activitycode); //
		StringBuilder sb_info = new StringBuilder(); //
		for (int i = 0; i < hvs.length; i++) {
			String str_submiter = hvs[i].getStringValue("realsubmitername"); //�ύ��
			String str_submittime = hvs[i].getStringValue("submittime"); //�ύʱ��
			String str_submitmessage = hvs[i].getStringValue("submitmessage"); //�ύ���
			sb_info.append("�ύ��:" + str_submiter + "&nbsp;&nbsp;" + "�ύʱ��:" + str_submittime + "<br>"); //
			sb_info.append(convertStrToHtml(str_submitmessage)); //
			sb_info.append("<br>"); //
		}
		return sb_info.toString(); //
	}

	/**
	 * ����ĳһ�����ڱ���ȡ�øû����е����������̵��������!!
	 * @param _activityCode
	 * @return
	 * @throws Exception
	 */
	private HashVO[] getOneActivityChildWFLastSubmitVO(String _activityCode) throws Exception {
		String str_rootInstId = getCommDMO().getStringValueByDS(null, "select rootinstanceid from pub_wf_prinstance where id='" + str_prinstanceid + "'"); //
		if (str_rootInstId == null) {
			str_rootInstId = str_prinstanceid; //
		}
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select ");
		sb_sql.append("t1.id, ");
		sb_sql.append("t1.prinstanceid, ");
		sb_sql.append("t1.parentinstanceid, ");
		sb_sql.append("t1.rootinstanceid, ");
		sb_sql.append("t1.submittime, ");
		sb_sql.append("t1.realsubmitername, ");
		sb_sql.append("t1.realsubmitcorpname, ");
		sb_sql.append("t1.submitmessage, ");
		sb_sql.append("t1.lifecycle, ");
		sb_sql.append("t2.fromparentactivity, ");
		sb_sql.append("t3.code ");
		sb_sql.append("from pub_wf_dealpool t1 ");
		sb_sql.append("left join pub_wf_prinstance t2 on t1.prinstanceid=t2.id ");
		sb_sql.append("left join pub_wf_activity   t3 on t2.fromparentactivity=t3.id ");
		sb_sql.append("where t1.rootinstanceid='" + str_rootInstId + "' and t1.parentinstanceid='" + str_rootInstId + "' and t1.issubmit='Y' ");
		sb_sql.append("and t3.code='" + _activityCode + "' ");
		sb_sql.append("and t1.lifecycle='EC' ");
		sb_sql.append("order by id ");
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, sb_sql.toString()); //
		return hvs; //
	}

	/**
	 * ȡ�����̼��ʱ���еĻ���..
	 */
	public HashVO[] getMonitorActivitys(String _prinstanceid) throws Exception { //
		String str_sql = "select distinct curractivity from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' order by batchno"; //
		LinkForeignTableDefineVO lfvo_1 = new LinkForeignTableDefineVO("pub_wf_activity", "code,wfname,approvemodel,approvenumber", "id", "curractivity"); //
		HashVO[] hvs = getCommDMO().getHashVoArrayBySubSQL(null, str_sql, new LinkForeignTableDefineVO[] { lfvo_1 }); //
		return hvs; //
	}

	/**
	 * ȡ�����̼��ʱ���е�ִ�в���..
	 */
	public HashVO[] getMonitorTransitions(String _prinstanceid, String _activityid) throws Exception { //
		String str_sql = "select id,prinstanceid,transition,participant_user,participant_userdept,participant_userrole,isreceive,receivetime,issubmit,submitisapprove,submittime,submitmessage from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' and curractivity=" + _activityid + " and issubmit='Y' order by id"; //
		LinkForeignTableDefineVO lfvo_1 = new LinkForeignTableDefineVO("pub_wf_transition", "wfname", "id", "transition"); //
		LinkForeignTableDefineVO lfvo_2 = new LinkForeignTableDefineVO("pub_user", "code,name", "id", "participant_user"); //
		LinkForeignTableDefineVO lfvo_3 = new LinkForeignTableDefineVO("pub_corp_dept", "code,name", "id", "participant_userdept"); //
		LinkForeignTableDefineVO lfvo_4 = new LinkForeignTableDefineVO("pub_role", "code,name", "id", "participant_userrole"); //
		HashVO[] hvs = getCommDMO().getHashVoArrayBySubSQL(null, str_sql, new LinkForeignTableDefineVO[] { lfvo_1, lfvo_2, lfvo_3, lfvo_4 }); //
		return hvs; //
	}

	/**
	 * ����Htmlͷ��..
	 * 
	 * @return
	 */
	private String getHtmlHeader() {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<meta http-equiv=\"Content-Language\" content=\"zh-cn\">\r\n");
		sb_html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n");
		// sb_html.append("<title></title>\r\n");
		sb_html.append("<style type=\"text/css\">\r\n");
		sb_html.append(".style_table {\r\n");
		sb_html.append(" margin-left: 0px; margin-right: 0px; margin-top: 3px; margin-bottom: 8px\r\n");
		sb_html.append("}\r\n");
		sb_html.append(".style_td {\r\n");
		sb_html.append(" font-size: 12px; color: #000000; line-height: 18px; font-family: ����\r\n");
		sb_html.append("}\r\n");
		sb_html.append(".style_trtd {\r\n");
		sb_html.append("  word-break:break-all;border-color:#888888 #888888 #888888 #888888;border-style:solid;border-top-width:1px;border-right-width:1px;border-bottom-width:1px;border-left-width:1px;font-size:12px;\r\n");
		sb_html.append("}\r\n");
		sb_html.append(".p_text {\r\n");
		sb_html.append(" font-size: 12px;\r\n");
		sb_html.append("}\r\n");
		sb_html.append(".p_text_header {\r\n");
		sb_html.append(" font-size: 14px;\r\n");
		sb_html.append("}\r\n");
		sb_html.append("</style>\r\n");
		sb_html.append("</head>\r\n");
		sb_html.append("<body  bgcolor=\"#FFFFFF\" topmargin=1 leftmargin=1 rightmargin=0 bottommargin=0 marginwidth=0 marginheight=0>\r\n"); //
		sb_html.append("<input type=\"button\" style=\"BORDER-RIGHT: #999999 1px solid; BORDER-TOP: #999999 1px solid; BORDER-LEFT: #999999 1px solid; BORDER-BOTTOM: #999999 1px solid; FONT-SIZE: 12px; HEIGHT: 18px; WIDTH=55px; BACKGROUND-COLOR: #EEEEEE\" value=\"��ӡ\" onClick=\"JavaScript:window.print();\"><br><br>\r\n"); //
		return sb_html.toString(); //
	}

	/**
	 * ����Html��β��..
	 * 
	 * @return
	 */
	private String getHtmlTail() {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("</body>\r\n");
		sb_html.append("</html>\r\n");
		return sb_html.toString(); //
	}

	private CommDMO getCommDMO() {
		if (commDMO == null) {
			commDMO = new CommDMO();
		}
		return commDMO;
	}

	private TBUtil getTBUtil() {
		if (tbUtil == null) {
			tbUtil = new TBUtil();
		}
		return tbUtil; //
	}

	private String getMessageByGroup() throws Exception {
		return getHtml(getMessage(this.str_prinstanceid));
	}

	public VectorMap getMessage(String _prinstanceid) throws Exception { // 
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("select ");
		sb_sql.append("t1.id id,");
		sb_sql.append("t2.belongdeptgroup belongdeptgroup, ");
		sb_sql.append("t2.id   activityid, "); // ��������
		sb_sql.append("t2.iscanlookidea   curractivity_iscanlookidea, "); // ��������
		sb_sql.append("t2.isneedreport   curractivity_isneedreport, "); // ��������
		sb_sql.append("t2.wfname  activityname, "); // ��������
		sb_sql.append("t1.participant_user  participant_user, "); // ��Ա����
		sb_sql.append("t3.name  submitname, ");
		sb_sql.append("t1.submittime submittime, ");
		sb_sql.append("t1.submitmessage submitmessage  ");
		sb_sql.append("from pub_wf_dealpool t1  ");
		sb_sql.append("left join pub_wf_activity t2 on t1.curractivity = t2.id ");
		sb_sql.append("left join pub_user t3 on t1.participant_user=t3.id ");
		sb_sql.append("where t1.prinstanceid='" + _prinstanceid + "' and t1.issubmit='Y' and t2. isneedreport='Y' and t2.activitytype<>'END' ");
		sb_sql.append("order by t1.id ");

		CommDMO commDMO = new CommDMO();
		HashVO[] hvs_msg = commDMO.getHashVoArrayByDS(null, sb_sql.toString());
		VectorMap v_map = new VectorMap();
		if (showType.equals("2")) {
			for (int i = 0; i < hvs_msg.length; i++) {
				if (hvs_msg[i].getStringValue("belongdeptgroup") != null) {
					if (v_map.containsKey(hvs_msg[i].getStringValue("belongdeptgroup"))) {
						VectorMap temp_map = (VectorMap) v_map.get(hvs_msg[i].getStringValue("belongdeptgroup"));
						temp_map.put(hvs_msg[i].getStringValue("id"), hvs_msg[i]);
					} else {
						VectorMap temp_map = new VectorMap();
						temp_map.put(hvs_msg[i].getStringValue("id"), hvs_msg[i]);
						v_map.put(hvs_msg[i].getStringValue("belongdeptgroup"), temp_map);
					}
				}
			}
			return v_map;
		}
		for (int i = 0; i < hvs_msg.length; i++) {
			if (hvs_msg[i].getStringValue("belongdeptgroup") != null) {
				if (v_map.containsKey(hvs_msg[i].getStringValue("belongdeptgroup"))) {
					VectorMap temp_map = (VectorMap) v_map.get(hvs_msg[i].getStringValue("belongdeptgroup"));
					temp_map.remove(hvs_msg[i].getStringValue("participant_user"));
					temp_map.put(hvs_msg[i].getStringValue("participant_user"), hvs_msg[i]);
				} else {
					VectorMap temp_map = new VectorMap();
					temp_map.put(hvs_msg[i].getStringValue("participant_user"), hvs_msg[i]);
					v_map.put(hvs_msg[i].getStringValue("belongdeptgroup"), temp_map);
				}
			}
		}
		return v_map;
	}

	private String getHtml(VectorMap map) {
		TBUtil tbUtil = new TBUtil(); //
		StringBuffer html = new StringBuffer();
		html.append("<html>\r\n");
		html.append("<body>\r\n");
		html.append("<table  border=\"0\" width = '100%' align=\"center\">\r\n");
		String[] allGroups = map.getKeysAsString();
		for (int i = 0; i < allGroups.length; i++) {
			html.append("<tr>\r\n");
			html.append("<td bgcolor=\"CCCCCC\">\r\n");
			html.append(allGroups[i]);
			html.append("</td>\r\n");
			html.append("</tr>\r\n");
			html.append("<tr>\r\n");
			html.append("<td>");

			VectorMap allUsers = (VectorMap) map.get(allGroups[i]); //
			for (int j = 0; j < allUsers.size(); j++) {
				HashVO hvo = (HashVO) allUsers.get(j); //
				if (j != 0) {
					html.append("<br><br>\r\n"); //
				}

				boolean iscanlook = false;
				String temprolse = hvo.getStringValue("curractivity_iscanlookidea");

				String[] temp_activity = null;
				if ("Y".equals(hvo.getStringValue("curractivity_isneedreport"))) {
					if (temprolse != null && temprolse.indexOf(";") > 0) {
						temp_activity = temprolse.split(";");

						if (userroles.length <= 1) {
							for (int k = 0; k < temp_activity.length; k++) {
								if (userroles[0].equals(temp_activity[k])) {
									iscanlook = true;
								}
							}
						} else {
							iscanlook = true;
							for (int n = 0; n < userroles.length; n++) {
								for (int k = 0; k < temp_activity.length; k++) {
									if (!userroles[n].equals(temp_activity[k])) {
										iscanlook = false;
									} else {

									}
								}
							}
						}
						if (iscanlook) {
							html.append("&nbsp;&nbsp;" + tbUtil.replaceAll(hvo.getStringValue("submitmessage", ""), "\n", "<br>&nbsp;&nbsp;"));// (2009-07-10����)hvo.getStringValue("submitmessage")�޸�Ϊhvo,getStringValue("submitmessage","");
						} else {
							html.append("&nbsp;&nbsp;*******");// (2009-07-10����)hvo.getStringValue("submitmessage")�޸�Ϊhvo,getStringValue("submitmessage","");
						}
					} else {
						html.append("&nbsp;&nbsp;" + tbUtil.replaceAll(hvo.getStringValue("submitmessage", ""), "\n", "<br>&nbsp;&nbsp;"));// (2009-07-10����)hvo.getStringValue("submitmessage")�޸�Ϊhvo,getStringValue("submitmessage","");
					}
				} else {
					html.append("&nbsp;&nbsp;*******");// (2009-07-10����)hvo.getStringValue("submitmessage")�޸�Ϊhvo,getStringValue("submitmessage","");
				}

				html.append("<br>\r\n"); //
				html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
				html.append("<font color=\"#4A4AFF\">" + hvo.getStringValue("activityname") + ":" + hvo.getStringValue("submitname") + "&nbsp;&nbsp;" + hvo.getStringValue("submittime") + "</font>\r\n"); //
			}

			html.append("</td>\r\n");
			html.append("</tr>\r\n");
		}
		html.append("</table>\r\n");
		html.append("</body>\r\n");
		html.append("</html>\r\n");
		return html.toString();
	}

	//�������Get����  LiGuoli 2012-04-22
	public BillVO getBillVO() {
		return billVO;
	}

	public String getStr_prinstanceid() {
		return str_prinstanceid;
	}

	public String getStr_loginUserId() {
		return str_loginUserId;
	}
}
