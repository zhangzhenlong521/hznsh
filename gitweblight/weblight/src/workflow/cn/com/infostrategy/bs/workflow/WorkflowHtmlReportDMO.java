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
 * 生成工作流报表的后台处理类!!!
 * 
 * @author xch
 * 
 */
public class WorkflowHtmlReportDMO implements WebCallBeanIfc {

	private TBUtil tbUtil = null; //
	private CommDMO commDMO = null; //

	private HashMap allConfMap = null; //
	private String str_prinstanceid = null; //流程实例
	private String str_loginUserId = null; //登录用户
	private BillVO billVO = null; //单据VO
	private String str_templetCode = null; //模板编码!
	private String showType = null; //
	private String showStyle = null; //因兴业提出按钮显示风格不同，所以添加该属性控制输出风格
	private String[] userroles = null; //

	/**
	 * 取得html内容!!
	 */
	public String getHtmlContent(HashMap map) throws Exception {
		allConfMap = map; //
		str_prinstanceid = (String) map.get("prinstanceid"); //流程实例字段,大量信息都可以从这个中取得!!!
		str_loginUserId = (String) map.get("userid"); //登录用户id!

		userroles = (String[]) map.get("roles"); //角色
		showType = map.get("showType") == null ? "1" : map.get("showType").toString(); //显示类型
		showStyle = map.get("showStyle") == null ? "1" : map.get("showStyle").toString(); //显示风格
		billVO = (BillVO) map.get("billvo"); //单据值!!
		String htmlreport = map.get("html文件名") == null ? "" : map.get("html文件名").toString();
		String wordreport = map.get("word文件名") == null ? "" : map.get("word文件名").toString();
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<!-- 下列参数是系统预置的(格式是${参数名}):\r\n"); //
		sb_html.append("【templetname】【流程监控】\r\n");
		sb_html.append("【发起人】【发起时间】【发起人部门】【发起人机构类别】\r\n");
		sb_html.append("【终审人】【终审时间】【终审意见】【终审人部门】【结束类型】【结束环节】\r\n");
		sb_html.append("【某环节上的处理信息合并[环节编码]】【某环节上的处理人[环节编码]】【某环节上的处理时间[环节编码]】【某环节上的处理意见[环节编码]】【某环节上的处理人所属机构[环节编码]】\r\n");
		sb_html.append("【某环节上的所有子流程处理信息合并[环节编码]】\r\n");
		sb_html.append("-->\r\n"); //
		sb_html.append("<!-- 流程实例id=[" + str_prinstanceid + "],用户id=[" + str_loginUserId + "] -->\r\n"); //

		str_templetCode = billVO.getTempletCode(); //模板编码!!
		String str_billtype = billVO.getStringValue("billtype"); // 单据类型
		String str_busitype = billVO.getStringValue("busitype"); // 业务类型
		sb_html.append("<!-- 单据类型=[" + str_billtype + "],业务类型=[" + str_busitype + "],模板编码=[" + str_templetCode + "] -->\r\n"); ////

		if (!showStyle.equals("1")) { //只显示流程处理日志!!
			sb_html.append("<!-- 因为showstyle=1,所以直接输出!! -->\r\n"); //
			sb_html.append(getDefaultHtmlReport()); //
			return sb_html.toString();//
		}

		//		String str_sql = "select id,processid,htmlreport,wordreport, report_roles from pub_workflowassign where billtypecode='" + str_billtype + "' and busitypecode='" + str_busitype + "'"; //
		//		HashVO[] hvo_report = getCommDMO().getHashVoArrayByDS(null, str_sql); //应该能找到一条记录!!
		//		if (hvo_report.length > 0) { // 如果找到对应数据,即找到了配置的记录!!!
		sb_html.append("<!-- 根据单据类型[" + str_billtype + "],业务类型[" + str_busitype + "],从表pub_workflowassign中找到匹配记录,html报表模板文件名=[" + htmlreport + "],word报表模板文件名=[" + wordreport + "] -->\r\n"); //
		//			String htmlreport = hvo_report[0].getStringValue("htmlreport"); //html报表!!
		if (htmlreport != null && !htmlreport.trim().equals("")) { //如果Html模板不为空,则使用Html报表模板!!
			//				if (htmlreport.indexOf(";") > 0) { //如果有分号!!
			//					HashMap tmpMap = getTBUtil().convertStrToMapByExpress(htmlreport, ";", "=>", true); //
			//					String str_savetableName = billVO.getSaveTableName(); //保存的表名!
			//					if (tmpMap.containsKey(str_savetableName.toLowerCase())) { //如果找到
			//						htmlreport = (String) tmpMap.get(str_savetableName.toLowerCase()); //
			//					} else if (tmpMap.containsKey("*")) {
			//						htmlreport = (String) tmpMap.get("*"); //
			//					} else {
			//						sb_html.append("根据html报表定义[" + htmlreport + "],没有找到与本单据保存表[" + str_savetableName + "]匹配的html模板!\r\n"); //
			//						return sb_html.toString(); //直接返回!!!
			//					}
			//				}
			sb_html.append("<!-- 使用Html格式的报表模板[" + htmlreport + "] -->\r\n"); //
			String str_filecontent = new FrameWorkCommServiceImpl().getServerResourceFile(htmlreport, "GBK"); //取得文件内容
			String[] str_macroitems = getTBUtil().getMacroList(str_filecontent); //取得所有宏代码段!!
			for (int i = 0; i < str_macroitems.length; i++) {
				if (str_macroitems[i].startsWith("${") && str_macroitems[i].endsWith("}")) { // 如果宏代码段!!
					String str_macrokey = str_macroitems[i].substring(2, str_macroitems[i].length() - 1); //取得宏代码!
					if (str_macrokey.equals("发起人")) {
						String str_createserName = getCommDMO().getStringValueByDS(null, "select t2.name from pub_wf_prinstance t1,pub_user t2 where t1.creater = t2.id and t1.id='" + str_prinstanceid + "'"); ////
						sb_html.append(str_createserName); //
					} else if (str_macrokey.equals("发起时间")) {
						String str_createtime = getCommDMO().getStringValueByDS(null, "select createtime from pub_wf_prinstance where id='" + str_prinstanceid + "'"); //
						sb_html.append(str_createtime); //
					} else if (str_macrokey.equals("发起人部门")) {
						String[] str_createDeptName = getCommDMO().getStringArrayFirstColByDS(null, "select t4.name from pub_wf_prinstance t1,pub_user t2,pub_user_post t3,pub_corp_dept t4 where t1.creater = t2.id and t2.id=t3.userid and t3.userdept=t4.id and t1.id='" + str_prinstanceid + "'"); //
						if (str_createDeptName != null && str_createDeptName.length > 0) {
							for (int j = 0; j < str_createDeptName.length; j++) {
								sb_html.append(str_createDeptName[j] + ";"); //
							}
						} else {
							sb_html.append("没有找到发起人部门"); //
						}
					} else if (str_macrokey.equals("发起人所属分行")) { //类型为。。分行，总行
						String str_createCorpType = getCommDMO().getStringValueByDS(null, "select t4.bl_fengh_name from pub_wf_prinstance t1,pub_user t2,pub_user_post t3,pub_corp_dept t4 where t1.creater = t2.id and t2.id=t3.userid and t3.userdept=t4.id and t1.id='" + str_prinstanceid + "'"); //
						if (str_createCorpType == null || "".equals(str_createCorpType)) {
							sb_html.append("总行");
						} else {
							sb_html.append(str_createCorpType); //
						}
					} else if (str_macrokey.equals("终审人")) { //
						String str_endUserName = getCommDMO().getStringValueByDS(null, "select t2.name from pub_wf_prinstance t1,pub_user t2 where t1.lastsubmiter=t2.id and t1.id='" + str_prinstanceid + "'"); //
						sb_html.append(str_endUserName); ////
					} else if (str_macrokey.equals("终审时间")) { //
						String str_endtime = getCommDMO().getStringValueByDS(null, "select endtime from pub_wf_prinstance where id='" + str_prinstanceid + "'");
						sb_html.append(str_endtime); ////
					} else if (str_macrokey.equals("终审意见")) { //
						String str_endmsg = getCommDMO().getStringValueByDS(null, "select lastsubmitmsg from pub_wf_prinstance where id='" + str_prinstanceid + "'");
						sb_html.append(str_endmsg); ////
					} else if (str_macrokey.equals("终审人部门")) { //
						String[] str_createDeptName = getCommDMO().getStringArrayFirstColByDS(null, "select t4.name from pub_wf_prinstance t1,pub_user t2,pub_user_post t3,pub_corp_dept t4 where t1.lastsubmiter = t2.id and t2.id=t3.userid and t3.userdept=t4.id and t1.id='" + str_prinstanceid + "'"); //
						if (str_createDeptName != null && str_createDeptName.length > 0) {
							for (int j = 0; j < str_createDeptName.length; j++) {
								sb_html.append(str_createDeptName[j] + ";"); //
							}
						} else {
							sb_html.append("没有找到终审人部门"); //
						}
					} else if (str_macrokey.equals("结束类型")) {
						String str_endtype = getCommDMO().getStringValueByDS(null, "select endtype from pub_wf_prinstance where id='" + str_prinstanceid + "'");
						sb_html.append(str_endtype); ////
					} else if (str_macrokey.equals("结束环节")) {
						String str_endactivity = getCommDMO().getStringValueByDS(null, "select t2.wfname from pub_wf_prinstance t1,pub_wf_activity t2 where t1.lastsubmitactivity=t2.id and t1.id='" + str_prinstanceid + "'"); //
						sb_html.append(str_endactivity); ////
					} else if (str_macrokey.startsWith("某环节上的处理信息合并")) {
						sb_html.append(getOneActivityLastSubmitInfoSpan(str_macrokey)); //
					} else if (str_macrokey.startsWith("某环节上的处理人[")) {
						sb_html.append(getOneActivityLastSubmitInfoItem(str_macrokey, "realsubmitername")); //
					} else if (str_macrokey.startsWith("某环节上的处理人其他信息")) {//sunfujun/20120806/汪勇需要联系方式
						sb_html.append(getOneActivitySubmiterInfo(str_macrokey)); //
					} else if (str_macrokey.startsWith("某环节上的处理人所属机构")) {
						sb_html.append(getOneActivityLastSubmitInfoItem(str_macrokey, "realsubmitcorpname")); //
					} else if (str_macrokey.startsWith("某环节上的处理时间")) {
						sb_html.append(getOneActivityLastSubmitInfoItem(str_macrokey, "submittime")); //
					} else if (str_macrokey.startsWith("某环节上的处理意见")) {
						sb_html.append(getOneActivityLastSubmitInfoItem(str_macrokey, "submitmessage")); //
					} else if (str_macrokey.startsWith("某环节上的所有子流程处理信息合并")) {
						sb_html.append(getOneActivityChildWFLastSubmitInfoSpan(str_macrokey)); //所有子流程即相关部门意见!!这个环节编码是主流程中的环节编码!!其实以后也可以搞成流程编码加上环节编码!
					} else if (str_macrokey.equals("流程监控")) { //直接将流程监控统统搞出来!
						if (!showType.equals("3")) {
							sb_html.append(getMessageByGroup()); // 动态生成流程监控的html!!
						} else if (showType.equals("3")) { //
							sb_html.append(getWorkFlowTraceHtml());
						}
					} else if (str_macrokey.equals("templetname")) { // 如果模板名
						sb_html.append(billVO.getTempletName()); //
					} else if (str_macrokey.startsWith("反射类")) { //可以直接写一个类名,然后反射调用,取得其返回的html输出!!
						sb_html.append(getRefectClassHtml(str_macrokey)); //
					} else {
						String str_billvalue = billVO.getStringViewValue(str_macrokey); //
						if (billVO.containsKey(str_macrokey)) { //如果没有
							sb_html.append(convertStrToHtml(str_billvalue)); //取单据!
						} else {
							sb_html.append("【${" + str_macrokey + "}没定义】"); //
							//sb_html.append("单据中没有定义key[" + str_macrokey + "]"); //
						}
					}
				} else {
					sb_html.append(str_macroitems[i]); // 直接加入
				}
			}
		} else if (wordreport != null && !"".equals(wordreport.trim())) { // 如果word模板不为空,则使用Word格式的报表模板!!
			sb_html.append("<!-- 使用Word格式的报表模板[" + wordreport + "] -->\r\n"); //
			String str_filecontent = new FrameWorkCommServiceImpl().getServerResourceFile(wordreport, "UTF-8"); // 取得文件内容
			String[] str_macroitems = getTBUtil().getMacroList(str_filecontent); // 得到所有段..
			for (int i = 0; i < str_macroitems.length; i++) { // 遍历所有宏代码段
				if (str_macroitems[i].startsWith("${") && str_macroitems[i].endsWith("}")) { // 如果宏代码段!!
					System.out.println("转换前:" + str_macroitems[i]); //
					String str_macrokey = clearWordFormat(str_macroitems[i]); // 先截掉<>段,因为有时word会自动在中间插入大量的<>段
					System.out.println("转换后:" + str_macrokey); //
					System.out.println(); //
					String str_billvalue = billVO.getStringViewValue(str_macrokey); //
					sb_html.append(str_billvalue == null ? "" : str_billvalue);
				} else {
					sb_html.append(str_macroitems[i]);
				}
			}

			new FrameWorkCommServiceImpl().writeFile("C:/xyz.doc", sb_html.toString(), "UTF-8"); // 表格的word生成,已在服务器生成,下面是如果在客户端调用的问题..
			return "111";
		} else { // 如果两者都为空!!
			sb_html.append("<!-- html模板与word模板定义都为空!直接使用默认格式输出,流程监控结果如下: -->\r\n"); //
			sb_html.append(getDefaultHtmlReport()); //
		}
		//		} else {
		//			sb_html.append("<!--根据单据编码[" + str_billtype + "],业务类型编码[" + str_busitype + "],没有找到映射的流程.: -->\r\n"); //
		//			sb_html.append(getHtmlHeader()); //
		//			sb_html.append(getWorkFlowTraceHtml());
		//			sb_html.append(getHtmlTail());
		//		}
		return sb_html.toString();//
	}

	/**
	 * 清空一个字符串的中word格式,即<>包含的内容
	 */
	private String clearWordFormat(String _text) {
		String str_text = _text.substring(2, _text.length() - 1); //
		String str_remain = str_text; //
		String str_return = null; //
		int li_count = 1;
		for (;;) {
			if (str_remain.indexOf("<") == 0) { // 如果是<开始
				str_remain = str_remain.substring(str_remain.indexOf(">") + 1, str_remain.length()); // 去掉第一对.
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
				break; // 怕万一死循环!!
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
	 * 根据模板编码与BillVO,自动生成一个表格!这个表格固定是两列N行!!
	 * 以后要在模板中定义某一项是否参与报表输出!! 只能参与的输出!!
	 * @return
	 */
	private String getBillVOAsHtml() throws Exception { //
		StringBuilder sb_html = new StringBuilder(); //
		//现在模板在xml里面，导致工作流导出意见，没有表单数据。调用统一去模板的接口。郝明[20121105]
		MetaDataDMO dmo = ServerEnvironment.getMetaDataDMO();
		Pub_Templet_1VO templet = dmo.getPub_Templet_1VO(str_templetCode);
		Pub_Templet_1_ItemVO templetItemVOs[] = templet.getItemVos(); //所有模板子表VO
		ArrayList al_temp = new ArrayList(); //
		for (int i = 0; i < templetItemVOs.length; i++) {
			String str_itemkey = templetItemVOs[i].getItemkey(); //
			String str_itemname = templetItemVOs[i].getItemname(); //
			String str_itemtype = templetItemVOs[i].getItemtype(); //
			String str_tiptext = templetItemVOs[i].getItemtiptext(); //
			if (str_tiptext != null) {
				str_itemname = str_tiptext; //
			}
			if (str_itemtype.equals("Label") || str_itemtype.equals("按钮")) {
				continue;
			}
			String str_isexport = templetItemVOs[i].getCardisexport(); //是否输出,不输出,半列输出,一列输出!
			if (str_isexport != null) { //如果不为空为输出!即如果为空,则不输出!! 以前是为空时默认输出,现在改成为空时默认不输出!! 因为实际上在表单中只有少部分是需要输出的!! 这样配置量更小!【xch/2012-11-09】
				if (str_isexport.equals("0")) { //不输出
				} else if (str_isexport.equals("1")) { //当半列输出
					appendBillDataBuilder(al_temp, str_itemkey, str_itemname, "半列"); //
				} else if (str_isexport.equals("2")) { //当全列输出,由于时间限制,暂不实现,以后要实现的!
					appendBillDataBuilder(al_temp, str_itemkey, str_itemname, "全列"); //
				}
			}
		}

		sb_html.append("\r\n"); //
		sb_html.append("<table align=\"left\" style=\"border-collapse:collapse;\" border=\"0\" cellpadding=\"5\">\r\n"); ////
		HashVO[] hvs_mycorp = new SysAppDMO().getParentCorpVOByMacro(2, this.str_loginUserId, "$本机构"); //
		String str_myCorpName = null; //
		String str_myCorpType = null; //
		if (hvs_mycorp != null && hvs_mycorp.length > 0) {//【李春娟/2016-06-24】
			str_myCorpName = hvs_mycorp[0].getStringValue("name"); //
			str_myCorpType = hvs_mycorp[0].getStringValue("corptype"); //
		}

		//主标题
		String str_tile = (String) allConfMap.get("标题");
		sb_html.append("<tr><td align=\"center\" colspan=4><B>" + ServerEnvironment.getProperty("LICENSEDTO") + "" + str_myCorpName + "-" + this.billVO.getTempletName() + "" + (str_tile == null ? "" : str_tile) + "</B></td></tr>\r\n\r\n"); //将单据信息输出!!!

		//输出表单数据!!!!遍历每一行数据!
		int li_label_width = 105; //
		int li_text_width = 205; //
		for (int i = 0; i < al_temp.size(); i++) {
			String[] str_rowItem = (String[]) al_temp.get(i); //这一行数据!
			if (str_rowItem.length >= 2) { //如果是两列,则非常简单◆
				sb_html.append("<tr>\r\n"); //
				String str_itemkey_1 = str_rowItem[0].substring(str_rowItem[0].indexOf("■") + 1, str_rowItem[0].indexOf("◆")); //第一列!
				String str_itemname_1 = str_rowItem[0].substring(str_rowItem[0].indexOf("◆") + 1, str_rowItem[0].length()); //
				sb_html.append("<td class=\"style_trtd\" align=\"right\" bgcolor=\"#F8F8F8\" width=" + li_label_width + " alt=\"" + str_itemkey_1 + "\">" + convertStrToHtml(str_itemname_1, str_itemkey_1 + "(itemname为空)") + "</td>\r\n"); //
				sb_html.append("<td class=\"style_trtd\" width=" + li_text_width + ">" + convertStrToHtml(billVO.getStringViewValue(str_itemkey_1), "&nbsp;") + "</td>\r\n"); //
				String str_itemkey_2 = str_rowItem[1].substring(str_rowItem[1].indexOf("■") + 1, str_rowItem[1].indexOf("◆")); //第二列!
				String str_itemname_2 = str_rowItem[1].substring(str_rowItem[1].indexOf("◆") + 1, str_rowItem[1].length()); //
				sb_html.append("<td class=\"style_trtd\" align=\"right\" bgcolor=\"#F8F8F8\" width=" + li_label_width + " >" + convertStrToHtml(str_itemname_2, str_itemkey_2 + "(itemname为空)") + "</td>\r\n"); //
				sb_html.append("<td class=\"style_trtd\" width=" + li_text_width + ">" + convertStrToHtml(billVO.getStringViewValue(str_itemkey_2), "&nbsp;") + "</td>\r\n"); //
				sb_html.append("</tr>\r\n\r\n"); //
			} else if (str_rowItem.length == 1) { //如果只有一列,则要看是全列还是半列
				String str_itemkey_1 = str_rowItem[0].substring(str_rowItem[0].indexOf("■") + 1, str_rowItem[0].indexOf("◆")); //
				String str_itemname_1 = str_rowItem[0].substring(str_rowItem[0].indexOf("◆") + 1, str_rowItem[0].length()); //
				if (str_rowItem[0].startsWith("全列■")) { //如果是全列,则内容跨三列
					sb_html.append("<tr>\r\n"); // 
					sb_html.append("<td class=\"style_trtd\" align=\"right\" bgcolor=\"#F8F8F8\" width=" + li_label_width + " alt=\"" + str_itemkey_1 + "\">" + convertStrToHtml(str_itemname_1, str_itemkey_1 + "(itemname为空)") + "</td>\r\n"); //
					sb_html.append("<td class=\"style_trtd\" width=" + (li_text_width + li_label_width + li_text_width) + " colspan=3>" + convertStrToHtml(billVO.getStringViewValue(str_itemkey_1), "&nbsp;") + "</td>\r\n"); //
					sb_html.append("</tr>\r\n\r\n"); //
				} else if (str_rowItem[0].startsWith("半列■")) { //如果是半列,则只输出前一半,后一半是补空值!
					sb_html.append("<tr>\r\n"); //
					sb_html.append("<td class=\"style_trtd\" align=\"right\" bgcolor=\"#F8F8F8\" width=" + li_label_width + " alt=\"" + str_itemkey_1 + "\">" + convertStrToHtml(str_itemname_1, str_itemkey_1 + "(itemname为空)") + "</td>\r\n"); //
					sb_html.append("<td class=\"style_trtd\" width=" + li_text_width + ">" + convertStrToHtml(billVO.getStringViewValue(str_itemkey_1), "&nbsp;") + "</td>\r\n"); //

					sb_html.append("<td class=\"style_trtd\" align=\"right\" bgcolor=\"#F8F8F8\" width=" + li_label_width + " alt=\"" + str_itemkey_1 + "\">&nbsp;</td>\r\n"); //
					sb_html.append("<td class=\"style_trtd\" width=" + li_text_width + ">&nbsp;</td>\r\n"); //
					sb_html.append("</tr>\r\n\r\n"); //
				}
			}
		}

		//根据指定的环节名称模糊匹配!,即法律意见书等
		boolean isFilterByCustomer = ("Y".equals(allConfMap.get("是否自定义条件"))); //有可能是自定义条件
		HashMap consMap = new HashMap(); //
		if (isFilterByCustomer) {
			//String str_formula = "表单项=aa,bb,cc;流程环节=风险合规部门,行领导;角色=部门负责人,处室负责人,行领导"; //
			String str_formula = (String) allConfMap.get("条件公式"); //表单项=name,code;流程环节=风险合规部门,行领导;角色=部门负责人;处室负责人;
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
		if (isFilterByCustomer) { //如果指定了另外字段,则
			String[] str_billCols = (String[]) consMap.get("表单项"); //new String[] { "aa", "bb", "cc" }; //
			if (str_billCols != null) {
				for (int b = 0; b < str_billCols.length; b++) { //遍历各个字段..
					for (int i = 0; i < templetItemVOs.length; i++) { //遍历模板各项!
						if (templetItemVOs[i].getItemkey().equalsIgnoreCase(str_billCols[b])) { //如果找到
							String str_itemName = templetItemVOs[i].getItemname(); //名称
							String str_itemtipText = templetItemVOs[i].getItemtiptext(); //以后可能需要使用帮助
							if (str_itemName == null || str_itemName.trim().equals("")) { //如果名称为空,则用帮助!!
								sb_billColValues.append(str_itemtipText + "<br>"); //
							} else {
								sb_billColValues.append(str_itemName + "<br>"); //
							}
							String str_colValue = billVO.getStringViewValue(str_billCols[b]); //
							str_colValue = (str_colValue == null ? "无&nbsp;" : str_colValue);
							sb_billColValues.append(str_colValue + "<br><br>"); //
						}
					}
				}
			}
		}

		sb_html.append("\r\n<!-- 计算流程意见单的核心逻辑: -->\n"); //
		sb_html.append("<!-- 1.首先根据传入的条件看是否是\"流程意见单\"等具有\"流程环节\"与\"角色\"条件,如果有,则根据其判断! -->\n"); //
		sb_html.append("<!-- 2.如果没有定义,则直接根据流程图中的各环节上的参数\"是否导出报表\"来决定输出!! -->\n"); //
		sb_html.append("<!-- 3.上面两种情况无论哪种过滤后,还要根据条件\"工作流导出报表是否进行组间隔离\",进行组间过滤,即只输出本人所在机构的数据!比如我的机构类型是【一级分行部门】,而某组名叫【一级分行】,则输出之! -->\n"); //

		//根据本人的机构类型,然后决定只输入本组的 ,即如果本人是[二级分行部门],则只输出[二级分行]组中的记录!!!
		//邮储是一个涌道是一个机构,然后要求只导出本机构的数据!但有的银行(比如兴业银行)要求可以导出所有组!!! 或者说,有的组其实不是一个机构,而就是一个组,这时就需要灵活配置!
		boolean isOnlyExportMyGroup = getTBUtil().getSysOptionBooleanValue("工作流导出报表是否进行组间隔离", false); //这个要搞成参数xch!!
		sb_html.append("<!-- 系统参数【工作流导出报表是否进行组间隔离】=【" + isOnlyExportMyGroup + "】 -->\n");

		//【法律意见书@二级分行◆表单项=name,code;流程环节=风险合规部门,行领导;角色=部门负责人;处室负责人;】
		String[] str_filt_actNames = (String[]) consMap.get("流程环节"); //new String[] { "风险合规部门", "行领导" }; //
		String[] str_filt_roleNames = (String[]) consMap.get("角色"); // new String[] { "部门负责人", "处室负责人", "行领导" }; //

		//加上工作流处理历史!
		ArrayList list_1 = new ArrayList(); //
		HashVO[] hvs_tasks = getMonitorDealPoolTasks(this.str_prinstanceid, this.str_loginUserId); //取得各个处理任务..
		//遍历所有数据进行过滤
		for (int i = 0; i < hvs_tasks.length; i++) { //
			String str_issubmit = hvs_tasks[i].getStringValue("issubmit"); //
			if (!"Y".equals(str_issubmit)) { //
				continue; //
			}
			String str_parentinstanceid = hvs_tasks[i].getStringValue("parentinstanceid"); //流程实例id
			String str_wfactname = hvs_tasks[i].getStringValue("curractivity_wfname"); //环节名称prinstanceid_fromparentactivityname
			String str_parentActName = hvs_tasks[i].getStringValue("prinstanceid_fromparentactivityname"); //环节名称
			String str_deptname = hvs_tasks[i].getStringValue("participant_userdeptname"); //机构名称!!!
			String str_userName = hvs_tasks[i].getStringValue("participant_username"); //人员名称!!!
			String str_userRoleNames = hvs_tasks[i].getStringValue("participant_userrolename"); //人员角色名称!!!!
			String str_isExport = hvs_tasks[i].getStringValue("curractivity_isneedreport"); //主流程指定是否导出!!
			String str_fromparentactivityisneedreport = hvs_tasks[i].getStringValue("prinstanceid_fromparentactivityisneedreport"); //来源环节是否输出!!

			String str_thisActName = str_wfactname; //
			if (str_parentinstanceid != null) { //如果是子程,是使用父亲流程的环节名称!
				str_thisActName = str_parentActName; //
			}
			str_thisActName = getTBUtil().replaceAll(str_thisActName, "\r", ""); //
			str_thisActName = getTBUtil().replaceAll(str_thisActName, "\n", ""); //
			str_thisActName = getTBUtil().replaceAll(str_thisActName, " ", ""); //

			sb_html.append("\r\n\r\n<!-- ★准备过滤的:环节【" + str_thisActName + "】 机构【" + str_deptname + "】 人员【" + str_userName + "】★ -->\n"); //

			boolean isImport = true; //
			if (isFilterByCustomer) { //只输出特定环节的意见,比如"法律合规部的",在输出法律意见书时就是这样的!!即法律意见书只是输出法律部的最终意见!! 其他的都不要!!!
				//环节是否匹配成功
				boolean isActMatched = false;
				if (str_filt_actNames == null || str_filt_actNames.length <= 0) { //如果没有指定机构,则默认认为都出来!
					isActMatched = true; //
				} else {
					for (int j = 0; j < str_filt_actNames.length; j++) { //遍历当前环节
						if (str_thisActName.indexOf(str_filt_actNames[j]) >= 0) { //如果这条任务的环节名称真好包含参数中的某一个条件,则就认为成功了!
							isActMatched = true; //
							break; //
						}
					}
				}
				sb_html.append("<!-- 根据指定环节与角色过滤模式,环节名称匹配结果:" + isActMatched + ",本任务的环节名称[" + str_thisActName + "],条件是[" + getSBStr(str_filt_actNames) + "] -->\n");

				//角色是否匹配成功
				boolean isRoleMatched = false; //
				String[] str_thisRoleItems = new String[0]; //
				if (str_filt_roleNames == null || str_filt_roleNames.length <= 0) { //如果没定义,则认为成功!!
					isRoleMatched = true; //
				} else {
					if (str_userRoleNames != null) {
						str_thisRoleItems = getTBUtil().split(str_userRoleNames, ";"); //这条任务有多个角色
					}
					for (int j = 0; j < str_filt_roleNames.length; j++) { //遍历当前环节!!
						boolean isFind = false; //
						for (int k = 0; k < str_thisRoleItems.length; k++) { //遍历这条任务的所有角色!!
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
				sb_html.append("<!-- 角色名称匹配结果:" + isRoleMatched + ",本任务的人员角色名称[" + getSBStr(str_thisRoleItems) + "],条件是[" + getSBStr(str_filt_roleNames) + "] -->\n");
				if (isActMatched && isRoleMatched) {
					isImport = true;
				} else {
					isImport = false; //
				}
			} else { //标准的判断逻辑,即根据流程图中的XX环节是否导出报表来定义!!
				if ("Y".equals(str_isExport) || "Y".equals(str_fromparentactivityisneedreport)) {
					sb_html.append("<!-- 直接根据参数\"是否导出报表\"模式：因为流程图上中指定该环节参与导出,要输出(但后面还有其他过滤条件) -->\n"); //
					isImport = true; //
				} else {
					sb_html.append("<!-- 直接根据参数\"是否导出报表\"模式：因为流程图上中没有指定该环节参与导出,所以被过滤了 -->\n"); //
					isImport = false; //
				}
			}

			String str_thisPrGroupName = hvs_tasks[i].getStringValue("curractivity_belongdeptgroup"); //来源环节名称!!
			if (str_parentinstanceid != null) { //如果是子流程,则拿父亲的流程
				str_thisPrGroupName = hvs_tasks[i].getStringValue("prinstanceid_fromparentactivitybldeptGroup"); //来源环节名称!,二级分行
			}

			//System.out.println("[" + str_myCorpType + "][" + str_thisPrGroupName + "]"); //
			if (str_thisPrGroupName == null) {
				sb_html.append("<!-- 没有定义组,所以不做任何组间过滤计算,统统输出! -->\n"); //
			} else {
				if (str_myCorpType != null && str_myCorpType.startsWith(str_thisPrGroupName)) { //与我同组,才加入
					hvs_tasks[i].setAttributeValue("$isSameGroupAsMe", "Y"); //
					sb_html.append("<!-- 这条任务的所属组[" + str_thisPrGroupName + "]与我的机构类型[" + str_myCorpType + "]匹配成功,所以跳过(注意前面可能已经过滤了)!! -->\n"); //
				} else {
					hvs_tasks[i].setAttributeValue("$isSameGroupAsMe", "N"); //
					if (isOnlyExportMyGroup) { //
						sb_html.append("<!-- 被过滤了,原因是:要求需要组间过滤(即只导出本组(甬道)),而这条任务的所属组[" + str_thisPrGroupName + "]与我的机构类型[" + str_myCorpType + "]匹配失败! -->\n"); //
						isImport = false; //
					} else {
						sb_html.append("<!-- 这条任务的所属组[" + str_thisPrGroupName + "]与我的机构类型[" + str_myCorpType + "]虽然不匹配,但因为没有要求根据组不同而过滤,所以不做处理(注意前面可能已经过滤了)!! -->\n"); //
					}
				}
			}
			if (isImport) {
				list_1.add(hvs_tasks[i]); //
			}
		}
		HashVO[] hvs_filter = (HashVO[]) list_1.toArray(new HashVO[0]); //
		sb_html.append("\r\n<!-- 计算最后意见之前的清单(这些数据后面要根据相同部门/人员合并最后一条意见处理) -->\n"); //
		for (int i = 0; i < hvs_filter.length; i++) { //
			sb_html.append("<!-- 机构[" + hvs_filter[i].getStringValue("participant_userdeptname") + "],人员[" + hvs_filter[i].getStringValue("participant_username") + "] -->\n"); //
		}
		sb_html.append("<!-- End -->\r\n\r\n"); //

		//邮储客户说加个分栏!!
		if (!isFilterByCustomer) {
			sb_html.append("<tr><td  class=\"style_trtd\" bgcolor=\"#FFFFD7\" colspan=4 align=\"center\">&nbsp;【流程意见清单】&nbsp;</td></tr>\n"); //
		}

		ArrayList al_list = getDealMsgDistinctList(hvs_filter, true); //做唯一性合并处理

		StringBuilder sb_wfListHtml = new StringBuilder(); //
		int li_allItemCount = 0; //
		boolean isFirst = true; //
		for (int i = 0; i < al_list.size(); i++) {
			ArrayList al_row = (ArrayList) al_list.get(i); //
			li_allItemCount = li_allItemCount + al_row.size(); //
			for (int j = 0; j < al_row.size(); j++) {
				HashVO hvoitem = (HashVO) al_row.get(j); //
				sb_wfListHtml.append("<!-- 机构[" + hvoitem.getStringValue("participant_userdeptname") + "],人员[" + hvoitem.getStringValue("participant_username") + "] -->\n"); //
			}
			if (isFilterByCustomer) { //如果是补充意见方式,比如法律意见书,它需要将表单上的新加的内容与流程中的内容合在一起!!
				for (int j = 0; j < al_row.size(); j++) {
					sb_wfListHtml.append("<tr>\n"); //
					sb_wfListHtml.append("<td class=\"style_trtd\" align=\"left\" valign=\"top\" colspan=3>" + getTextHtml(al_row.get(j)) + "</td>\n"); //行合并
					sb_wfListHtml.append("</tr>\n"); //
				}
			} else {
				HashVO firstVO = (HashVO) al_row.get(0); //
				//hvs_tasks[i].getStringValue("$isSameGroupAsMe") +
				String str_deptName = firstVO.getStringValue("participant_userdeptname"); //机构名称,比如山东省分行-人力资源部
				if ("Y".equals(firstVO.getStringValue("$isSameGroupAsMe"))) { //是否与我同组!!???
					if (str_deptName.indexOf("-") > 0) {
						str_deptName = str_deptName.substring(str_deptName.indexOf("-") + 1, str_deptName.length()); //取最后一个部门??
					}
				}
				str_deptName = "<span title=\"" + firstVO.getStringValue("participant_userdeptname") + "\">" + str_deptName + "</span>"; //将机构全称提示出来!

				if (isFirst) { //第一个加上送审部门标记
					str_deptName = "<font color=\"blue\">【送审部门】</font>" + str_deptName; //
					isFirst = false; //
				}

				String str_parentinstanceid = firstVO.getStringValue("parentinstanceid"); //父流程
				if (str_parentinstanceid != null) {
					str_deptName = "<font color=\"blue\">【会办】</font>" + str_deptName; //
				}

				if (al_row.size() == 1) {
					sb_wfListHtml.append("<tr>\n"); //
					sb_wfListHtml.append("<td class=\"style_trtd\" align=\"center\" bgcolor=\"#F8F8F8\" width=" + li_label_width + ">" + str_deptName + "</td>\n"); //行合并
					sb_wfListHtml.append("<td class=\"style_trtd\" align=\"left\" valign=\"top\" colspan=3>" + getTextHtml(al_row.get(0)) + "</td>\r\n"); //行合并
					sb_wfListHtml.append("</tr>\n"); //
				} else { //如果有多个
					sb_wfListHtml.append("<tr>\n"); //
					sb_wfListHtml.append("<td class=\"style_trtd\" align=\"center\" bgcolor=\"#F8F8F8\" width=" + li_label_width + " rowspan=" + al_row.size() + ">" + str_deptName + "</td>\n"); //行合并
					sb_wfListHtml.append("<td class=\"style_trtd\" align=\"left\"  valign=\"top\" colspan=3>" + getTextHtml(al_row.get(0)) + "</td>\n"); //行合并
					sb_wfListHtml.append("</tr>\n"); //
					for (int j = 1; j < al_row.size(); j++) {
						sb_wfListHtml.append("<tr>\n"); //
						sb_wfListHtml.append("<td class=\"style_trtd\" align=\"left\" valign=\"top\" colspan=3>" + getTextHtml(al_row.get(j)) + "</td>\n"); //行合并
						sb_wfListHtml.append("</tr>\n"); //
					}
				}
			}
		}
		if (isFilterByCustomer) { //如果是手工添加的,即将表单与流程中的合在一起!!
			sb_html.append("<tr>\n");
			sb_html.append("<td class=\"style_trtd\" align=\"center\"  valign=\"middle\" bgcolor=\"#F6F6F6\" rowspan=" + (li_allItemCount + 1) + ">法律审查意见</td>\n"); //
			sb_html.append("<td class=\"style_trtd\" align=\"left\"    valign=\"top\" colspan=3>" + sb_billColValues.toString() + "</td>\n"); //将单据信息输出!!!
			sb_html.append("</tr>\n"); //
			sb_html.append(sb_wfListHtml.toString()); //
		} else {
			sb_html.append(sb_wfListHtml.toString()); //
		}
		sb_html.append("<tr><td class=\"style_trtd\" align=\"right\" valign=\"top\" colspan=4>导出时间:" + getTBUtil().getCurrTime() + "<br><br></td></tr>\r\n"); //将单据信息输出!!!
		sb_html.append("</table>\r\n\r\n"); //
		return sb_html.toString(); //
	}

	private String getTextHtml(Object _obj) {
		HashVO hvo = (HashVO) _obj; //
		StringBuilder sb_text = new StringBuilder(); //
		String str_userName = hvo.getStringValue("participant_username"); //参与者名称,在授权时,这里还是领导,而实际上输出的是领导的意见! 而不是
		String str_userRoleName = hvo.getStringValue("participant_userrolename"); //参与者角色名称

		String str_userLabel = "经办人"; //
		String[] str_cons = new String[] { "行领导", "部门负责人", "处室负责人" }; //
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
		sb_text.append(str_userLabel + ":" + str_userName + "&nbsp;&nbsp;&nbsp;经办时间:" + hvo.getStringValue("submittime") + "<br>"); //
		sb_text.append("处理意见:" + hvo.getStringValue("submitmessage_real", "") + "<br>"); //submitmessage,直接拿解密后的
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
	 * 取得唯一性的清单...即一个部门只会出现一次,一个部门内部的一个人员也只会出现一次!
	 * @return
	 */
	private ArrayList getDealMsgDistinctList(HashVO[] _hvs, boolean _isOnlyLastUser) {
		LinkedHashMap deptMap = new LinkedHashMap(); //用来判断是否只出现过一次!

		int li_maxcount = 2; //遇到4层机构算成三层的情况
		for (int i = 0; i < _hvs.length; i++) { //
			String str_deptname = _hvs[i].getStringValue("participant_userdeptname"); //机构名称!!!
			int li_count = getTBUtil().findCount(str_deptname, "-"); //
			if (li_count > li_maxcount) { //可能有三个
				li_maxcount = li_count; //
			}
		}

		for (int i = 0; i < _hvs.length; i++) { //
			String str_deptname = _hvs[i].getStringValue("participant_userdeptname"); //机构名称!!!
			str_deptname = trimDeptNameByPos(str_deptname, li_maxcount); //
			String str_userName = _hvs[i].getStringValue("participant_username"); //人员名称!!!
			if (deptMap.containsKey(str_deptname)) { //如果已注册
				LinkedHashMap userMap = (LinkedHashMap) deptMap.get(str_deptname); //
				if (_isOnlyLastUser) { //邮储说一个部门中每个人的最后意见都要,但兴业说只要一个部门的最终意见
					userMap.clear(); //先清空,再加入,则自然就是最后一个人的意见
					userMap.put(str_userName, _hvs[i]); //则加入,如果已有,则什么都不做
				} else {
					if (!userMap.containsKey(str_userName)) { //如果这个人员不存在
						userMap.put(str_userName, _hvs[i]); //则加入,如果已有,则什么都不做
					}
				}
			} else { //如果机构都不存在!
				LinkedHashMap userMap = new LinkedHashMap(); //
				userMap.put(str_userName, _hvs[i]); //
				deptMap.put(str_deptname, userMap); //
			}
		}

		ArrayList list_dist = new ArrayList(); //
		LinkedHashMap[] list_Maps = (LinkedHashMap[]) deptMap.values().toArray(new LinkedHashMap[0]); //
		for (int i = 0; i < list_Maps.length; i++) {
			HashVO[] vos = (HashVO[]) list_Maps[i].values().toArray(new HashVO[0]); //所有VO
			ArrayList al_row = new ArrayList(); //
			for (int j = 0; j < vos.length; j++) {
				al_row.add(vos[j]); //
			}
			list_dist.add(al_row); //
		}

		return list_dist; //
	}

	//如果发现有两个以上的中杠,则裁掉最后一个,因为在兴业中遇到同时出现【总行-计划财务部-综合管理处】与【总行-计划财务部】,其实他们应该算一个!!
	//考虑到不同项目中的层级可能不一样,所以说这个参数"2"应该是可配置的参数!!!
	private String trimDeptNameByPos(String _deptname, int li_maxcount) {
		//int li_maxcount = 2; //这个参数以后可以根据其他项目而做调整!
		if (li_maxcount > 0) { //
			int li_count = getTBUtil().findCount(_deptname, "-"); //看总共有几个中杠???
			if (li_count >= li_maxcount) { //如果超过了!!!
				String str_1 = ""; //
				String str_remain = _deptname; //
				for (int i = 0; i < li_maxcount; i++) {
					str_1 = str_1 + str_remain.substring(0, str_remain.indexOf("-")) + "-"; //第几个中杠??
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

	//卡片中导出时存在半列与全列的计算,需要处理!
	private void appendBillDataBuilder(ArrayList _list, String _itemKey, String _itemName, String _type) {
		if (_type.equals("半列")) {
			if (_list.size() == 0) { //如果没有,则直接加入
				_list.add(new String[] { "半列■" + _itemKey + "◆" + _itemName }); //	
			} else { //如果前面有,则看是在上一行屁股后面加,还是另起一行
				String[] str_lastItem = (String[]) _list.get(_list.size() - 1); //
				if (str_lastItem.length >= 2) { //如果已经满2个了,则强行换行!
					_list.add(new String[] { "半列■" + _itemKey + "◆" + _itemName }); //
				} else { //如果只有一个,则要看是全列还是半列,如果是全列,则强行换行,如果是半列,则加上!
					if (str_lastItem[0].startsWith("全列■")) { //如果是全列
						_list.add(new String[] { "半列■" + _itemKey + "◆" + _itemName }); //
					} else { //如果是半列
						String[] str_newItem = new String[] { str_lastItem[0], "半列■" + _itemKey + "◆" + _itemName }; //
						_list.set(_list.size() - 1, str_newItem); //重新设置
					}
				}
			}
		} else if (_type.equals("全列")) { //如果加的是个全列,则非常简单!直接新加入一行
			_list.add(new String[] { "全列■" + _itemKey + "◆" + _itemName }); //
		}
	}

	/**
	 * 取得工作流日志的表格.这种已经不需要了
	 * @return
	 */
	private String getWorkFlowTraceHtml() throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("\r\n<table align=\"left\" width=\"70%\" style=\"border-collapse:collapse;\" border=\"0\" cellpadding=\"5\">\r\n"); //
		HashVO[] hvs_tasks = getMonitorDealPoolTasks(this.str_prinstanceid, this.str_loginUserId); // 取得各个处理任务..
		if (hvs_tasks.length > 0) {
			for (int i = 0; i < hvs_tasks.length; i++) {
				String str_issubmit = hvs_tasks[i].getStringValue("issubmit"); //是否提交
				if ("Y".equals(str_issubmit)) {
					continue; //
				}
				String str_wfActName = hvs_tasks[i].getStringValue("curractivity_wfname2"); //环节名称
				String str_submitUserCorp = hvs_tasks[i].getStringValue("realsubmitcorpname", ""); //
				String str_submitUserName = hvs_tasks[i].getStringValue("realsubmitername", ""); //
				String str_submitTime = hvs_tasks[i].getStringValue("submittime", ""); //提交时间
				String str_submitmessage = hvs_tasks[i].getStringValue("submitmessage", ""); //提交意见!
				str_submitmessage = getTBUtil().replaceAll(str_submitmessage, "\n", "<br>"); //
				sb_html.append("<tr><td class=\"style_trtd\"  bgcolor=\"#F8F8F8\">【流程步骤】:" + str_wfActName + "</td><td class=\"style_trtd\"  bgcolor=\"#F8F8F8\">【提交机构】:" + str_submitUserCorp + "<td></tr>\r\n"); //
				sb_html.append("<tr><td class=\"style_trtd\"  bgcolor=\"#F8F8F8\">【提交人员】:" + str_submitUserName + "</td><td class=\"style_trtd\"  bgcolor=\"#F8F8F8\">【提交时间】:" + str_submitTime + "<td></tr>\r\n"); //
				sb_html.append("<tr><td colspan=2  class=\"style_trtd\">【提交意见】:" + str_submitmessage + "\r\n<br><br></td></tr>\r\n");
			}
		}
		sb_html.append("</table>\r\n");
		return sb_html.toString(); //
	}

	private String convertStrToHtml(String _text) {
		return convertStrToHtml(_text, "无"); //
	}

	private String convertStrToHtml(String _text, String _nvl) {
		if (_text == null) {
			return _nvl; //
		}
		return getTBUtil().replaceAll(_text, "\n", "<br>"); //
	}

	/**
	 * 取得流程监控时各条处理记录
	 */
	public HashVO[] getMonitorDealPoolTasks(String _prinstanceid, String _loginUserId) throws Exception { //
		String str_rootInstId = getCommDMO().getStringValueByDS(null, "select rootinstanceid from pub_wf_prinstance where id='" + _prinstanceid + "'"); //
		if (str_rootInstId == null) {
			str_rootInstId = _prinstanceid; //
		}
		return new WorkFlowServiceImpl().getMonitorTransitions(str_rootInstId, true, false, _loginUserId); //
	}

	/**
	 * 取得某个环节上的最后一条处理信息项
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
			String str_submiter = hvs[li_pos].getStringValue("realsubmitername"); //提交人
			//String str_submitercorp = hvs[li_pos].getStringValue("realsubmitcorpname"); //
			String str_submittime = hvs[li_pos].getStringValue("submittime"); //提交时间
			String str_submitmessage = hvs[li_pos].getStringValue("submitmessage"); //提交意见
			StringBuilder sb_info = new StringBuilder(); //
			sb_info.append("提交人:" + str_submiter + "&nbsp;&nbsp;" + "提交时间:" + str_submittime + "<br>"); //
			sb_info.append(convertStrToHtml(str_submitmessage) + "<br>"); //
			return sb_info.toString(); //
		} else {
			return "&nbsp;";
		}
	}

	/**
	 * 取得某个反射类的值!比如:cn.com.pushworld.bs.MyHtmlDMO.getHtml("类型","3","是否多选","Y")
	 * 但实际上这个类方法的入参与返回值都是HashMap,然后平台自动将配置中的奇偶位作为key/value送到方法的入参中!以后想扩展更多入参时非常方便,只要在平台代码中在入参map中加入更多值即可!
	 * 返回值也是map,这也是为了以后扩展方便,即可以返回多个值,然后平台进行判断输出!! 现在必须返回一个key名为HtmlContent的值!
	 * @param _macroKey
	 * @return
	 * @throws Exception
	 */
	private String getRefectClassHtml(String _macroKey) throws Exception {
		String str_clsName = _macroKey.substring(_macroKey.indexOf("[") + 1, _macroKey.length() - 1); //即反射类三个字后面有[类名],比如:【反射类[cn.com.pushworld.bs.MyHtmlDMO.getHtml("类型","3","是否多选","Y")]】
		HashMap parMap = new HashMap(); //入参!!
		parMap.put("工作流报表DMO", this); //将自己送进去!这样接收者就能拿到后使用该类中的相关方法!!
		parMap.put("$帮助说明", "返回值中必须有一个key名为HtmlContent的值,返回的html内容就放在这里面!"); //塞入这个帮助说明是为了开发人员在debug时好知道如何使用这个！！
		HashMap returnMap = getTBUtil().reflectCallCommMethod(str_clsName, parMap); //反射调用!!这是一个万能的,可以无限扩展的机制!!入参以后可以扩展,返回值也可以扩展!
		if (returnMap == null) {
			return "执行反射类【" + str_clsName + "】失败,请至控制台查看详细!注意方法入参与返回值必须都是HashMap"; //
		}
		String str_html = (String) returnMap.get("HtmlContent"); //
		if (str_html == null) {
			return "执行反射类【" + str_clsName + "】成功,但返回的Map中没有名为\"HtmlContent\"的key"; //
		} else {
			return str_html; //
		}
	}

	/**
	 * 根据环节编码取得某一环节上的提交信息!!
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
		sb_sql.append("t1.submittime,"); //提交时间
		sb_sql.append("t1.realsubmitername,"); //提交人名
		sb_sql.append("t1.realsubmitcorpname,"); //提交机构名
		sb_sql.append("t1.submitmessage "); //提交意见
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
	 * 获取某一环节处理人的其他信息
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
	 * 取得某一个环节中的所有子流程的最后意见!!
	 * @param _macroKey
	 * @return
	 * @throws Exception
	 */
	private String getOneActivityChildWFLastSubmitInfoSpan(String _macroKey) throws Exception {
		String str_activitycode = _macroKey.substring(_macroKey.indexOf("[") + 1, _macroKey.length() - 1); //
		HashVO[] hvs = getOneActivityChildWFLastSubmitVO(str_activitycode); //
		StringBuilder sb_info = new StringBuilder(); //
		for (int i = 0; i < hvs.length; i++) {
			String str_submiter = hvs[i].getStringValue("realsubmitername"); //提交人
			String str_submittime = hvs[i].getStringValue("submittime"); //提交时间
			String str_submitmessage = hvs[i].getStringValue("submitmessage"); //提交意见
			sb_info.append("提交人:" + str_submiter + "&nbsp;&nbsp;" + "提交时间:" + str_submittime + "<br>"); //
			sb_info.append(convertStrToHtml(str_submitmessage)); //
			sb_info.append("<br>"); //
		}
		return sb_info.toString(); //
	}

	/**
	 * 根据某一个环节编码取得该环节中的所有子流程的最终意见!!
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
	 * 取得流程监控时所有的环节..
	 */
	public HashVO[] getMonitorActivitys(String _prinstanceid) throws Exception { //
		String str_sql = "select distinct curractivity from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' order by batchno"; //
		LinkForeignTableDefineVO lfvo_1 = new LinkForeignTableDefineVO("pub_wf_activity", "code,wfname,approvemodel,approvenumber", "id", "curractivity"); //
		HashVO[] hvs = getCommDMO().getHashVoArrayBySubSQL(null, str_sql, new LinkForeignTableDefineVO[] { lfvo_1 }); //
		return hvs; //
	}

	/**
	 * 取得流程监控时所有的执行步骤..
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
	 * 返回Html头部..
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
		sb_html.append(" font-size: 12px; color: #000000; line-height: 18px; font-family: 宋体\r\n");
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
		sb_html.append("<input type=\"button\" style=\"BORDER-RIGHT: #999999 1px solid; BORDER-TOP: #999999 1px solid; BORDER-LEFT: #999999 1px solid; BORDER-BOTTOM: #999999 1px solid; FONT-SIZE: 12px; HEIGHT: 18px; WIDTH=55px; BACKGROUND-COLOR: #EEEEEE\" value=\"打印\" onClick=\"JavaScript:window.print();\"><br><br>\r\n"); //
		return sb_html.toString(); //
	}

	/**
	 * 返回Html的尾部..
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
		sb_sql.append("t2.id   activityid, "); // 环节主键
		sb_sql.append("t2.iscanlookidea   curractivity_iscanlookidea, "); // 环节主键
		sb_sql.append("t2.isneedreport   curractivity_isneedreport, "); // 环节主键
		sb_sql.append("t2.wfname  activityname, "); // 环节名称
		sb_sql.append("t1.participant_user  participant_user, "); // 人员主键
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
							html.append("&nbsp;&nbsp;" + tbUtil.replaceAll(hvo.getStringValue("submitmessage", ""), "\n", "<br>&nbsp;&nbsp;"));// (2009-07-10宁波)hvo.getStringValue("submitmessage")修改为hvo,getStringValue("submitmessage","");
						} else {
							html.append("&nbsp;&nbsp;*******");// (2009-07-10宁波)hvo.getStringValue("submitmessage")修改为hvo,getStringValue("submitmessage","");
						}
					} else {
						html.append("&nbsp;&nbsp;" + tbUtil.replaceAll(hvo.getStringValue("submitmessage", ""), "\n", "<br>&nbsp;&nbsp;"));// (2009-07-10宁波)hvo.getStringValue("submitmessage")修改为hvo,getStringValue("submitmessage","");
					}
				} else {
					html.append("&nbsp;&nbsp;*******");// (2009-07-10宁波)hvo.getStringValue("submitmessage")修改为hvo,getStringValue("submitmessage","");
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

	//添加属性Get方法  LiGuoli 2012-04-22
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
