package com.pushworld.ipushgrc.bs.wfrisk;

import java.util.HashMap;

import javax.swing.text.DefaultStyledDocument;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ImageServlet;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

import com.pushworld.ipushgrc.bs.StyleDocumentConverter;

/**
 * 将体系文件生成HTML的构造器!!
 * 这将是以后需要反复优化的关键类!!其中的方法结构要进一步搞得更清晰些!!
 * @author xch
 *
 */
public class WFRiskHtmlBuilder {

	private CommDMO commDMO = new CommDMO(); //
	private final String popDivStart = "<div class='div_textarea' onclick='popDiv(this)'>"; //html 弹出Div
	private final String popDivEnd = "</div>";
	private TBUtil tbUtil = new TBUtil();

	//几种皮肤
	//页头-文字色, 页头-渐变开始色, 页头-渐变结束色, 分组-文字色, 分组-渐变开始色, 分组-渐变结束色, 栏目-文字色, 栏目-渐变开始色, 栏目-渐变结束色, 单元格-文字色, 单元格-渐变开始色, 单元格-渐变结束色
	private final String[][] look_and_feel = { { "#FFFFFF", "#888888", "#1A1A1A", "#009BE6", "#273038", "#FEFEFE", "#000000", "#C5EAF8", "#C5EAF8", "#000000", "#FEFEFE", "#D6D6D6" },
			{ "#000000", "#F0F3F5", "#9CAACB", "#009BE6", "#B5E5F9", "#FEFEFE", "#000000", "#C5EAF8", "#C5EAF8", "#000000", "#FEFEFE", "#D6D6D6" }, { "#04A3E6", "#5E6464", "#262827", "#000000", "#D2D7DA", "#FBFBFB", "#000000", "#C5EAF8", "#C5EAF8", "#000000", "#FEFEFE", "#D6D6D6" }, };

	/**
	 * 输出Html的内容!!!
	 */
	public String getHtmlContent(String _cmpfileId, String _regid, int _htmlStyle) throws Exception {
		return getHtml(1, _cmpfileId, _regid, null, _htmlStyle); //
	}

	/**
	 * 创建历史版本时调用的方法!!!
	 * @param _cmpfileId
	 * @param _cmpfileHistId
	 * @return
	 * @throws Exception
	 */
	public String getHtmlContentByHist(String _cmpfileId, String _cmpfileHistId, int _htmlStyle) throws Exception {
		return getHtml(2, _cmpfileId, null, _cmpfileHistId, _htmlStyle); //
	}

	private String getHtml(int _type, String _cmpfileId, String _regid, String _cmpfileHistId, int _htmlStyle) throws Exception {
		String show_processref1 = tbUtil.getSysOptionHashItemStringValue("体系流程_一图两表自定义格式类", "HTML", null);////项目中可自定义一图两表生成word和html的格式【李春娟/2012-05-21】
		if (show_processref1 != null && !"".equals(show_processref1)) {
			HashMap hashmap = new HashMap();
			hashmap.put("type", _type);
			hashmap.put("cmpfileId", _cmpfileId);
			hashmap.put("regid", _regid);
			hashmap.put("cmpfileHistId", _cmpfileHistId);
			hashmap.put("htmlStyle", _htmlStyle);
			hashmap.put("parent", this);
			hashmap = tbUtil.reflectCallCommMethod(show_processref1 + ".getHtml()", hashmap);
			return (String) hashmap.get("returnString");
		}

		StringBuffer sb_html = new StringBuffer(); //		
		HashVO[] hvs_fileinfo = commDMO.getHashVoArrayByDS(null, "select cmpfile.*,dict.name state from v_cmp_cmpfile cmpfile,pub_comboboxdict dict where cmpfile.filestate=dict.id and dict.type='文件状态' and cmpfile.id =" + _cmpfileId); //
		String fileName = hvs_fileinfo[0].getStringValue("cmpfilename");
		sb_html.append(getHTMLHeader(fileName, _htmlStyle)); //输出开始

		sb_html.append("<div class='page_bar'>" + fileName + "</div>\r\n");
		sb_html.append("<TABLE class='myTable' cellSpacing=1>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append("	<TD class='key'>文件名称</TD>\r\n");
		sb_html.append(" 	<TD class='val'>");
		sb_html.append(hvs_fileinfo[0].getStringValue("cmpfilename"));
		sb_html.append("</TD>\r\n");
		sb_html.append(" 	<TD class='key'>文件分类</TD>\r\n");
		sb_html.append(" 	<TD class='val'>");
		sb_html.append(hvs_fileinfo[0].getStringValue("cmpfiletype"));
		sb_html.append("</TD>\r\n");
		sb_html.append(" 	<TD class='key'>文件状态</TD>\r\n");
		sb_html.append(" 	<TD class='val'>");
		sb_html.append(hvs_fileinfo[0].getStringValue("state"));
		sb_html.append("</TD></TR>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append(" 	<TD class='key'>发布日期</TD>\r\n");
		sb_html.append(" 	<TD class='val'>");
		sb_html.append(hvs_fileinfo[0].getStringValue("publishdate", ""));
		sb_html.append("</TD>\r\n");
		sb_html.append(" 	<TD class='key'>版本号</TD>\r\n");
		sb_html.append(" 	<TD class='val'>");
		sb_html.append(hvs_fileinfo[0].getStringValue("versionno", ""));
		sb_html.append("</TD>\r\n");
		sb_html.append("	<TD class='key'>主管/所属部门</TD>\r\n");
		sb_html.append(" 	<TD class='val'>");
		sb_html.append(hvs_fileinfo[0].getStringValue("blcorpname"));
		sb_html.append("</TD></TR>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append(" 	<TD class='key'>业务活动分类</TD>\r\n");
		sb_html.append(" 	<TD class='val'>");
		sb_html.append(hvs_fileinfo[0].getStringValue("bsactname", ""));
		sb_html.append("</TD>\r\n");
		sb_html.append("  	<TD class='key'>内控五要素分类</TD>\r\n");
		sb_html.append(" 	<TD class='val'>");
		sb_html.append(hvs_fileinfo[0].getStringValue("ictypename", ""));
		sb_html.append("</TD>\r\n");
		sb_html.append("  	<TD class='key'>文件编码</TD>\r\n");
		sb_html.append("  	<TD class='val'>");
		sb_html.append(hvs_fileinfo[0].getStringValue("cmpfilecode", ""));
		sb_html.append("</TD></TR>\r\n");
		boolean showreffile = tbUtil.getSysOptionBooleanValue("流程文件是否由正文生成word", true);
		if (!showreffile) {//如果不显示正文，则显示文件的子项
			HashMap itemMap = new WFRiskBSUtil().getCmpFileItemDocument(_cmpfileId, false);//返回一个流程文件中的富文本框中实际对应的Document对象，如果没有格式则返回字符串
			String str_item_target = null;
			String str_item_userarea = null;
			String str_item_keywords = null;
			String str_item_duty = null;
			String str_item_bsprcp = null;
			String str_item_addenda = (String) itemMap.get("item_addenda");

			StyleDocumentConverter converter = new StyleDocumentConverter();

			if (itemMap.get("item_target") instanceof String) {
				str_item_target = (String) itemMap.get("item_target");
			} else {
				DefaultStyledDocument doc = (DefaultStyledDocument) itemMap.get("item_target");
				converter.setDoc(doc);
				str_item_target = converter.toHtml();
			}
			if (itemMap.get("item_userarea") instanceof String) {
				str_item_userarea = (String) itemMap.get("item_userarea");
			} else {
				DefaultStyledDocument doc = (DefaultStyledDocument) itemMap.get("item_userarea");
				converter.setDoc(doc);
				str_item_userarea = converter.toHtml();
			}
			if (itemMap.get("item_keywords") instanceof String) {
				str_item_keywords = (String) itemMap.get("item_keywords");
			} else {
				DefaultStyledDocument doc = (DefaultStyledDocument) itemMap.get("item_keywords");
				converter.setDoc(doc);
				str_item_keywords = converter.toHtml();
			}
			if (itemMap.get("item_duty") instanceof String) {
				str_item_duty = (String) itemMap.get("item_duty");
			} else {
				DefaultStyledDocument doc = (DefaultStyledDocument) itemMap.get("item_duty");
				converter.setDoc(doc);
				str_item_duty = converter.toHtml();
			}
			if (itemMap.get("item_bsprcp") instanceof String) {
				str_item_bsprcp = (String) itemMap.get("item_bsprcp");
			} else {
				DefaultStyledDocument doc = (DefaultStyledDocument) itemMap.get("item_bsprcp");
				converter.setDoc(doc);
				str_item_bsprcp = converter.toHtml();
			}
			String show_item = tbUtil.getSysOptionStringValue("体系流程_流程文件子项的显示", null);

			if (show_item == null) {
				sb_html.append("  <TR>\r\n");
				sb_html.append("  	<TD class='key textarea'>目的</TD>\r\n");
				sb_html.append("  	<TD class='val' colspan='5'>");
				sb_html.append(convertStr(str_item_target, 200));
				sb_html.append("</TD></TR>\r\n");
				sb_html.append("  <TR>\r\n");
				sb_html.append("  	<TD class='key textarea'>适用范围</TD>\r\n");
				sb_html.append("  	<TD class='val' colspan='5'>");
				sb_html.append(convertStr(str_item_userarea, 200));
				sb_html.append("</TD></TR>\r\n");
				sb_html.append("  <TR>\r\n");
				sb_html.append("  	<TD class='key textarea'>定义、缩写和分类</TD>\r\n");
				sb_html.append("  	<TD class='val' colspan='5'>");
				sb_html.append(convertStr(str_item_keywords, 200));
				sb_html.append("</TD></TR>\r\n");
				sb_html.append("  <TR>\r\n");
				sb_html.append("  	<TD class='key textarea'>职责与权限</TD>\r\n");
				sb_html.append("  	<TD class='val' colspan='5'>");
				sb_html.append(convertStr(str_item_duty, 200));
				sb_html.append("</TD></TR>\r\n");
				sb_html.append("  <TR>\r\n");
				sb_html.append("  	<TD class='key textarea'>基本原则</TD>\r\n");
				sb_html.append("  	<TD class='val' colspan='5'>");
				sb_html.append(convertStr(str_item_bsprcp, 200));
				sb_html.append("</TD></TR>\r\n");
				sb_html.append("  <TR>\r\n");
				sb_html.append("  	<TD class='key textarea'>相关文件、附录</TD>\r\n");
				sb_html.append("  	<TD class='val' colspan='5'>");
				sb_html.append(convertStr(str_item_addenda, 200));
				sb_html.append("</TD></TR>\r\n");
			} else {
				String[] show_items = tbUtil.split(show_item, ";");
				if ("Y".equals(show_items[0])) {
					sb_html.append("  <TR>\r\n");
					sb_html.append("  	<TD class='key textarea'>目的</TD>\r\n");
					sb_html.append("  	<TD class='val' colspan='5'>");
					sb_html.append(convertStr(str_item_target, 200));
					sb_html.append("</TD></TR>\r\n");
				}
				if ("Y".equals(show_items[1])) {
					sb_html.append("  <TR>\r\n");
					sb_html.append("  	<TD class='key textarea'>适用范围</TD>\r\n");
					sb_html.append("  	<TD class='val' colspan='5'>");
					sb_html.append(convertStr(str_item_userarea, 200));
					sb_html.append("</TD></TR>\r\n");
				}
				if ("Y".equals(show_items[2])) {
					sb_html.append("  <TR>\r\n");
					sb_html.append("  	<TD class='key textarea'>定义、缩写和分类</TD>\r\n");
					sb_html.append("  	<TD class='val' colspan='5'>");
					sb_html.append(convertStr(str_item_keywords, 200));
					sb_html.append("</TD></TR>\r\n");
				}
				if ("Y".equals(show_items[3])) {
					sb_html.append("  <TR>\r\n");
					sb_html.append("  	<TD class='key textarea'>职责与权限</TD>\r\n");
					sb_html.append("  	<TD class='val' colspan='5'>");
					sb_html.append(convertStr(str_item_duty, 200));
					sb_html.append("</TD></TR>\r\n");
				}
				if ("Y".equals(show_items[4])) {
					sb_html.append("  <TR>\r\n");
					sb_html.append("  	<TD class='key textarea'>基本原则</TD>\r\n");
					sb_html.append("  	<TD class='val' colspan='5'>");
					sb_html.append(convertStr(str_item_bsprcp, 200));
					sb_html.append("</TD></TR>\r\n");
				}
				if ("Y".equals(show_items[5])) {
					sb_html.append("  <TR>\r\n");
					sb_html.append("  	<TD class='key textarea'>相关文件、附录</TD>\r\n");
					sb_html.append("  	<TD class='val' colspan='5'>");
					sb_html.append(convertStr(str_item_addenda, 200));
					sb_html.append("</TD></TR>\r\n");
				}
			}
		}
		sb_html.append("</TABLE>\r\n");
		sb_html.append("<br/>\r\n");

		//将文件本身内容身成一个表格,比如将文件名作为标题彩色大号显示,然后面有张表,显示具体基本信息!!!
		String str_sql = "select id,code,name from pub_wf_process where cmpfileid=" + _cmpfileId + " order by userdef04,id"; //
		HashVO[] hvs_wfs = commDMO.getHashVoArrayByDS(null, str_sql); //查询
		for (int i = 0; i < hvs_wfs.length; i++) { //遍历多个图片!!
			String str_wf_process_id = hvs_wfs[i].getStringValue("id"); //

			sb_html.append("<TABLE class='myTable' cellSpacing=1>\r\n");
			sb_html.append("<TR><TD>");
			sb_html.append("<div class='group_bar' onclick=\"doDraw(this, 'div_allprocess_" + str_wf_process_id + "')\">- " + (i + 1) + "、流程名称：" + hvs_wfs[i].getStringValue("name") + "</div><br>");
			sb_html.append("<div id = 'div_allprocess_" + str_wf_process_id + "'>");
			if (_type == 1) { //如果是直接点击查看,则访问
				sb_html.append("<img src='./ImageServlet?fromtype=cache&iszip=Y&cacheid=" + _regid + "&imgname=" + str_wf_process_id + "' usemap=#Map_" + str_wf_process_id + " border=1>\r\n"); //先输出图片,图片的实际内容在最大后输出!!使用了ImageServlet
			} else if (_type == 2) { //如果是存储历史版本,则访问图片
				sb_html.append("<img src='./WebDispatchServlet?cls=com.pushworld.ipushgrc.bs.wfrisk.WFRiskHistImageDispatchWeb&cmpfilehistid=" + _cmpfileHistId + "&imgname=IMAGE_" + str_wf_process_id + "' usemap=#Map_" + str_wf_process_id + " border=1>\r\n");
			}

			HashVO[] hvs_activitys = null; //找出这个流程的所有环节,这里不用排序，根据位置设置热点
			hvs_activitys = commDMO.getHashVoArrayByDS(null, "select id,wfname,x,y,width,height from pub_wf_activity where processid=" + str_wf_process_id);

			HashVO[] hvs_risk = commDMO.getHashVoArrayByDS(null, "select wfprocess_id,wfactivity_id,riskcode,riskname,riskdescr,possible,serious,rank from cmp_risk where cmpfile_id=" + _cmpfileId + " and wfprocess_id=" + str_wf_process_id + " order by rank,id"); //
			String sql_opereq = "select dept.name operatedept,post.name operatepost,opereq.operaterefpost,opereq.operatedesc,opereq.operatereq,opereq.opeinput,opereq.opeoutput,opereq.wfactivity_id from cmp_cmpfile_wfopereq opereq left join pub_corp_dept dept on opereq.operatedept=dept.id left join pub_post post on opereq.operatepost=post.id  where wfprocess_id="
					+ str_wf_process_id + " order by opereq.id";
			HashVO[] hvs_opereq = commDMO.getHashVoArrayByDS(null, sql_opereq);
			if (hvs_activitys.length > 0) {
				sb_html.append("<map name=Map_" + str_wf_process_id + ">\r\n");
				for (int j = 0; j < hvs_activitys.length; j++) {
					String str_activityId = hvs_activitys[j].getStringValue("id"); //
					String str_activityName = hvs_activitys[j].getStringValue("wfname"); //
					int li_x = hvs_activitys[j].getIntegerValue("x"); //
					int li_y = hvs_activitys[j].getIntegerValue("y"); //
					int li_width = hvs_activitys[j].getIntegerValue("width"); //
					int li_height = hvs_activitys[j].getIntegerValue("height"); //
					int li_x_2 = li_x + li_width / 2; //
					int li_y_2 = li_y + li_height; //point

					String str_opereqInfo = this.getOpereqInfoByOneActivity(hvs_opereq, str_activityId); //
					sb_html.append("<area shape=rect coords='" + li_x + "," + li_y + "," + li_x_2 + "," + li_y_2 + "' ");
					sb_html.append("alt='" + (str_opereqInfo == null ? str_activityName : str_opereqInfo) + "' ");
					sb_html.append("href='#activity_" + str_activityId + "' ");
					sb_html.append("onclick=\"showDiv('div_activity_" + str_wf_process_id + "')\" ");
					sb_html.append("border=1>\r\n"); //

					String str_riskInfo = getRiskInfoByOneActivity(hvs_risk, str_activityId); //
					sb_html.append("<area shape=rect coords='" + li_x + "," + li_y + "," + (li_x_2 + li_width / 2) + "," + li_y_2 + "' ");
					sb_html.append("alt='" + (str_riskInfo == null ? str_activityName : str_riskInfo) + "' ");
					sb_html.append("href='#activity_" + str_activityId + "' ");
					sb_html.append("onclick=\"showDiv('div_activity_" + str_wf_process_id + "')\" ");
					sb_html.append("border=1>\r\n"); //
				}
				sb_html.append("</map>\r\n"); //
			}

			//输出某个流程的两表信息,即流程相关与环节相关的所有信息!!!
			sb_html.append(getAllWFTwoGridInfo(str_wf_process_id));
			sb_html.append("</TD></TR>\r\n");
			sb_html.append("</TABLE>\r\n");
			sb_html.append("</div>");
		}

		ImageServlet.removeCacheCode(); //清除缓存数据!!一定要手动调一下!!!否则会造成内存溢出!!!
		sb_html.append("\r\n");
		sb_html.append("</BODY>\r\n"); //
		sb_html.append("</HTML>\r\n"); //
		return sb_html.toString(); ////
	}

	private String getHTMLHeader(String _title, int skinType) {
		String[] lookandfeel = this.look_and_feel[skinType];
		StringBuffer sb_html = new StringBuffer();
		sb_html.append("<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.0 Transitional//EN'>\r\n");
		sb_html.append("<HTML><HEAD><TITLE>");
		sb_html.append(_title);
		sb_html.append("</TITLE>\r\n");
		sb_html.append("<META http-equiv=Content-Type content='text/html; charset=gbk'>\r\n");
		/*********** CSS *************/
		sb_html.append(StyleDocumentConverter.getCSS());//富文本框的格式
		sb_html.append("<style>\r\n");
		sb_html.append("	body,td,th {\r\n");
		sb_html.append("		background-color:#FFFFFF; margin:0; font-size:12px; font-family:'宋体', arial;\r\n");
		sb_html.append("	}\r\n");
		sb_html.append("	.myTable {\r\n");
		sb_html.append("		margin:10;CLEAR: both;FONT-SIZE: 13px;width:95%;background-color:#ACB7C4;\r\n");
		sb_html.append("	}\r\n");
		sb_html.append("	td.title {\r\n");
		sb_html.append("		color:" + lookandfeel[6] + ";background-color:#C5EAF8;text-align:center;padding:2px 5px 0 5px;\r\n");
		//栏目渐变
		sb_html.append("		background-image: -moz-linear-gradient(top, " + lookandfeel[7] + ", " + lookandfeel[8] + "); /* FF 3.6 */ \r\n");
		sb_html.append("		background-image: -webkit-gradient(linear,left top,left bottom,color-stop(0, " + lookandfeel[7] + "),color-stop(1, " + lookandfeel[8] + "));/* Safari & Chrome */ \r\n");
		sb_html.append("		filter: progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='" + lookandfeel[7] + "', endColorstr='" + lookandfeel[8] + "');/* IE6 & IE7 */ \r\n");
		sb_html.append("		-ms-filter: \"progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='" + lookandfeel[7] + "', endColorstr='" + lookandfeel[8] + "')\";/* IE8 */ \r\n");
		sb_html.append("	}\r\n");
		sb_html.append("	td.key {\r\n");
		sb_html.append("		width:10%;background-color:#E7EEF8;text-align:left;padding:2px 5px 0 5px;\r\n");
		//单元格渐变
		sb_html.append("		background-image: -moz-linear-gradient(top, " + lookandfeel[10] + ", " + lookandfeel[11] + "); /* FF 3.6 */ \r\n");
		sb_html.append("		background-image: -webkit-gradient(linear,left top,left bottom,color-stop(0, " + lookandfeel[10] + "),color-stop(1, " + lookandfeel[11] + "));/* Safari & Chrome */ \r\n");
		sb_html.append("		filter: progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='" + lookandfeel[10] + "', endColorstr='" + lookandfeel[11] + "');/* IE6 & IE7 */ \r\n");
		sb_html.append("		-ms-filter: \"progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='" + lookandfeel[10] + "', endColorstr='" + lookandfeel[11] + "')\";/* IE8 */ \r\n");
		sb_html.append("	}\r\n");
		sb_html.append("	td.val {\r\n");
		sb_html.append("		width:20%;background-color:#ffffff;text-align:left;padding:2px 5px 0 5px;\r\n");
		sb_html.append("	}\r\n");

		sb_html.append("	td.textcenter {\r\n");
		sb_html.append("		text-align:center;\r\n");
		sb_html.append("	}\r\n");

		sb_html.append("	td.textarea {\r\n");
		sb_html.append("		height:40px;\r\n");
		sb_html.append("	}\r\n");

		sb_html.append("	.page_bar {\r\n");
		sb_html.append("		color:" + lookandfeel[0] + ";font-size:14px;font-weight:bold;text-align:center;height:24px;margin:0 0 5px 0;padding:0 0 0 10px;line-height:24px;\r\n");
		//页头渐变
		sb_html.append("		background-image: -moz-linear-gradient(top, " + lookandfeel[1] + ", " + lookandfeel[2] + "); /* FF 3.6 */ \r\n");
		sb_html.append("		background-image: -webkit-gradient(linear,left top,left bottom,color-stop(0, " + lookandfeel[1] + "),color-stop(1, " + lookandfeel[2] + "));/* Safari & Chrome */ \r\n");
		sb_html.append("		filter: progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='" + lookandfeel[1] + "', endColorstr='" + lookandfeel[2] + "');/* IE6 & IE7 */ \r\n");
		sb_html.append("		-ms-filter: \"progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='" + lookandfeel[1] + "', endColorstr='" + lookandfeel[2] + "')\";/* IE8 */ \r\n");
		sb_html.append("	}\r\n");
		sb_html.append("	.group_bar {\r\n");
		sb_html.append("		color:" + lookandfeel[3] + ";font-size:12px;height:24px;margin:0 0 5px 0;padding:0 0 0 10px;line-height:24px;cursor:hand;\r\n");
		//分组渐变
		sb_html.append("		background-image: -moz-linear-gradient(top, " + lookandfeel[4] + ", " + lookandfeel[5] + "); /* FF 3.6 */ \r\n");
		sb_html.append("		background-image: -webkit-gradient(linear,left top,right top,color-stop(0, " + lookandfeel[4] + "),color-stop(1, " + lookandfeel[5] + "));/* Safari & Chrome */ \r\n");
		sb_html.append("		filter: progid:DXImageTransform.Microsoft.gradient(GradientType=1,startColorstr='" + lookandfeel[4] + "', endColorstr='" + lookandfeel[5] + "');/* IE6 & IE7 */ \r\n");
		sb_html.append("		-ms-filter: \"progid:DXImageTransform.Microsoft.gradient(GradientType=1,startColorstr='" + lookandfeel[4] + "', endColorstr='" + lookandfeel[5] + "')\";/* IE8 */ \r\n");
		sb_html.append("	}\r\n");
		sb_html.append("	.footer_bar {\r\n");
		sb_html.append("		color:#999999;font-size:12px;text-align:center;background-color:#CCCCCC;height:18px;margin:0 0 5px 0;padding:0 0 0 10px;line-height:18px;\r\n");
		sb_html.append("	}\r\n");
		//弹出Div
		sb_html.append("    .div_textarea {\r\n");
		sb_html.append("    	height:40px;overflow:hidden;cursor:hand;\r\n");
		sb_html.append("    }\r\n");
		sb_html.append("    .div_pop {\r\n");
		sb_html.append("        background:#E5F3F9;width:800px;height:600px;line-height:20px;vertical-align:middle;position:absolute;clear:left;\r\n");
		sb_html.append("        padding:10px 0 10px 5px;margin-bottom:10px;border:1px solid #ddd;filter:alpha(opacity=90);opacity:.9;overflow:auto;\r\n");
		sb_html.append("    }\r\n");
		sb_html.append("</style>\r\n\r\n");

		/*********** JS *************/
		sb_html.append("<script>\r\n");
		sb_html.append("function $(element){\r\n");
		sb_html.append("	return element = document.getElementById(element);\r\n");
		sb_html.append("}\r\n");
		sb_html.append("//层展开\r\n");
		sb_html.append("function drawDown(div){\r\n");
		sb_html.append("	var h=div.offsetHeight;\r\n");
		sb_html.append("	var maxh=500;\r\n");
		sb_html.append("	function dmove(){\r\n");
		sb_html.append("		h+=50; //速度\r\n");
		sb_html.append("		if(h>=maxh){\r\n");
		sb_html.append("			div.style.height='50px';\r\n");
		sb_html.append("            clearInterval(iIntervalId);\r\n");
		sb_html.append("        }else{\r\n");
		sb_html.append("            div.style.display='block';\r\n");
		sb_html.append("            div.style.height=h+'px';\r\n");
		sb_html.append("        }\r\n");
		sb_html.append("    }\r\n");
		sb_html.append("    iIntervalId=setInterval(dmove,2);\r\n");
		sb_html.append("}\r\n\r\n");
		sb_html.append("//层收缩\r\n");
		sb_html.append("function drawUp(div){\r\n");
		sb_html.append("    var h=div.offsetHeight;\r\n");
		sb_html.append("    var maxh=500;\r\n");
		sb_html.append("    if (h > maxh) {\r\n");
		sb_html.append("    	h = maxh;\r\n");
		sb_html.append("    }\r\n");
		sb_html.append("    function dmove(){\r\n");
		sb_html.append("        h-=50;//速度\r\n");
		sb_html.append("        if(h<=0){\r\n");
		sb_html.append("            div.style.display='none';\r\n");
		sb_html.append("            clearInterval(iIntervalId);\r\n");
		sb_html.append("        }else{\r\n");
		sb_html.append("            div.style.height=h+'px';\r\n");
		sb_html.append("        }\r\n");
		sb_html.append("    }\r\n");
		sb_html.append("    iIntervalId=setInterval(dmove,2);\r\n");
		sb_html.append("}\r\n");
		sb_html.append("//层收缩,展开\r\n");
		sb_html.append("function doDraw(obj,divID){\r\n");
		sb_html.append("    var text = obj.innerText;\r\n");
		sb_html.append("    var s = text.substr(0,2);\r\n");
		sb_html.append("    if(s == '+ ' || s == '- ')\r\n");
		sb_html.append("    {\r\n");
		sb_html.append("        text = text.substr(2, text.length);\r\n");
		sb_html.append("    }\r\n");
		sb_html.append("    var div=$(divID);\r\n");
		sb_html.append("    if(div.style.display=='none'){\r\n");
		sb_html.append("        drawDown(div);\r\n");
		sb_html.append("        obj.innerHTML = '- ' + text;\r\n");
		sb_html.append("    }else{\r\n");
		sb_html.append("        drawUp(div);\r\n");
		sb_html.append("        obj.innerHTML = '+ ' + text;\r\n");
		sb_html.append("    }\r\n");
		sb_html.append("}\r\n");
		sb_html.append("function showDiv(divID){\r\n");
		sb_html.append("	$(divID).style.display=\"\";\r\n");
		sb_html.append("}\r\n");

		sb_html.append("function popDiv(obj){\r\n");
		sb_html.append("    var div = document.getElementById('div_pop');\r\n");
		sb_html.append("    var divBody = document.getElementById('div_pop_body');\r\n");
		sb_html.append("    if (div.style.display=='none'){\r\n");
		sb_html.append("        divBody.innerHTML = obj.innerHTML;\r\n");
		sb_html.append("        div.style.display = '';\r\n");
		sb_html.append("        div.style.left=(document.body.clientWidth-div.clientWidth)/2+document.body.scrollLeft;\r\n");
		sb_html.append("        div.style.top=(document.body.clientHeight-div.clientHeight)/2+document.body.scrollTop;\r\n");
		sb_html.append("    }else {\r\n");
		sb_html.append("        divBody.innerHTML = '';\r\n");
		sb_html.append("        div.style.display = 'none';\r\n");
		sb_html.append("    }\r\n");
		sb_html.append("}\r\n");

		sb_html.append("</script>\r\n");
		sb_html.append("</HEAD>\r\n");
		sb_html.append("<BODY>\r\n");

		//定义弹出Div
		sb_html.append("<div id='div_pop' class='div_pop' style='display:none'>\r\n");
		sb_html.append("    <div style='position:absolute;right:4px;top:4px'>\r\n");
		sb_html.append("        <a href='javascript:;' onclick='popDiv();' title='关闭'>关闭</a>\r\n");
		sb_html.append("    </div>\r\n");
		sb_html.append("    <div id='div_pop_body' style='padding:20 20 20 20'></div>\r\n");
		sb_html.append("</div>\r\n");
		return sb_html.toString();
	}

	/**
	 * 取得一个流程的所有相关的表格信息!!!
	 * @param _processId
	 * @return
	 */
	private String getAllWFTwoGridInfo(String _processId) throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		String show_wfref = tbUtil.getSysOptionStringValue("体系流程_流程相关", null);
		String show_activityref = tbUtil.getSysOptionStringValue("体系流程_环节相关", null);

		sb_html.append("<div class='group_bar' onclick=\"doDraw(this, 'div_process_" + _processId + "')\">- 流程相关信息</div>\r\n"); //
		sb_html.append("<div id='div_process_" + _processId + "'>\r\n"); //
		sb_html.append("<table class='myTable' cellSpacing=1><TR><TD>\r\n"); ////

		if (show_wfref == null || "".equals(show_wfref.trim())) {//如果没有配置流程相关是否关联信息，则全部显示，否则根据配置的是否关联来显示或不显示关联信息
			sb_html.append(getWfdescByProcessId(_processId));//流程概述
			sb_html.append(getLawByProcessId(_processId));//流程相关法规
			sb_html.append(getRuleByProcessId(_processId));//流程相关制度
			sb_html.append(getCheckpointByProcessId(_processId));//流程相关检查要点
		} else {
			String[] show_wfrefs = tbUtil.split(show_wfref, ";");
			if ("Y".equals(show_wfrefs[0])) {
				sb_html.append(getWfdescByProcessId(_processId));//流程概述
			}
			if ("Y".equals(show_wfrefs[1])) {
				sb_html.append(getLawByProcessId(_processId));//流程相关法规
			}
			if ("Y".equals(show_wfrefs[2])) {
				sb_html.append(getRuleByProcessId(_processId));//流程相关制度
			}
			if ("Y".equals(show_wfrefs[3])) {
				sb_html.append(getCheckpointByProcessId(_processId));//流程相关检查要点
			}
		}
		sb_html.append("</TD></TR></table></div>\r\n");//流程相关结束

		sb_html.append("<div class='group_bar' onclick=\"doDraw(this, 'div_activity_" + _processId + "')\">- 环节相关信息</div>\r\n"); //
		sb_html.append("<div id='div_activity_" + _processId + "'>\r\n"); //
		sb_html.append("<table class='myTable' cellSpacing=1>\r\n"); ////

		//这里环节需要排序
		HashVO[] hvs = null;
		String dbType = ServerEnvironment.getInstance().getDefaultDataSourceType().toUpperCase();//取得数据库类型,因为要集成以前系统中排序以“环节”开头的问题，使用了cast()函数，下面sql只能在mysql执行，以后要判断数据库！！！
		if (dbType.equalsIgnoreCase("MYSQL")) {
			hvs = commDMO.getHashVoArrayByDS(null, "select act.id,act.wfname from pub_wf_activity act left join pub_wf_group grp on act.processid = grp.processid and act.belongstationgroup = grp.wfname where act.processid =" + _processId + " order by act.processid,grp.y,cast(act.uiname as signed)");//查询该流程文件所有的环节信息，排序字段processid,belongstationgroup必须有
		} else if (dbType.equalsIgnoreCase("ORACLE")) {
			hvs = commDMO.getHashVoArrayByDS(null, "select act.id,act.wfname from pub_wf_activity act left join pub_wf_group grp on act.processid = grp.processid and act.belongstationgroup = grp.wfname where act.processid =" + _processId + " order by act.processid,grp.y,to_number(act.uiname)");
		} else { //SQLSERVER,DB2
			hvs = commDMO.getHashVoArrayByDS(null, "select id,wfname from pub_wf_activity where processid =" + _processId + " order by processid,belongstationgroup,uiname");
		}
		for (int i = 0; i < hvs.length; i++) {
			String activityid = hvs[i].getStringValue("id");
			sb_html.append("<table class='myTable' cellSpacing=1><tr><td>\r\n"); ////
			sb_html.append("<a name = 'activity_" + activityid + "'>【" + hvs[i].getStringValue("wfname") + "】</a>\r\n"); //环节链接

			if (show_activityref == null || "".equals(show_activityref.trim())) {//如果没有配置环节相关是否关联信息，则全部显示，否则根据配置的是否关联来显示或不显示关联信息
				sb_html.append(getOperaterequireByActivityId(activityid)); //环节操作要求
				sb_html.append(getLawByActivityId(activityid)); //环节相关法规
				sb_html.append(getRuleByActivityId(activityid)); //环节相关制度
				sb_html.append(getCheckpointByActivityId(activityid)); //环节相关检查要点
				sb_html.append(getRiskByActivityId(activityid)); //环节风险点
			} else {
				String[] show_activityrefs = tbUtil.split(show_activityref, ";");
				if ("Y".equals(show_activityrefs[0])) {
					sb_html.append(getOperaterequireByActivityId(activityid)); //环节操作要求
				}
				if ("Y".equals(show_activityrefs[1])) {
					sb_html.append(getLawByActivityId(activityid)); //环节相关法规
				}
				if ("Y".equals(show_activityrefs[2])) {
					sb_html.append(getRuleByActivityId(activityid)); //环节相关制度
				}
				if ("Y".equals(show_activityrefs[3])) {
					sb_html.append(getCheckpointByActivityId(activityid)); //环节相关检查要点
				}
				//第四项 相关罚则默认不显示 
				//if ("Y".equals(show_activityrefs[5])) {
				sb_html.append(getRiskByActivityId(activityid)); //环节风险点
				//}
			}
			sb_html.append("</td></tr></table>\r\n"); ////
		}
		sb_html.append("</table></div>\r\n"); ////
		return sb_html.toString(); //
	}

	/**
	 * 流程相关的流程概述
	 * @param _processid 流程id
	 * @return
	 * @throws Exception
	 */
	private String getWfdescByProcessId(String _processid) throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		HashVO[] hvs = commDMO.getHashVoArrayByDS(null, " select * from cmp_cmpfile_wfdesc where wfprocess_id=" + _processid + " order by id");
		if (hvs == null || hvs.length == 0) {
			return "";
		}
		sb_html.append("<TABLE class='myTable' cellSpacing=1>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append("	<TD class='title' colspan='4'>流程概述</TD></TR>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append("	<TD class='key'>流程编码</TD>\r\n");
		sb_html.append(" 	<TD class='val '>");
		sb_html.append(hvs[0].getStringValue("wfprocess_code", ""));
		sb_html.append("</TD>\r\n");
		sb_html.append(" 	<TD class='key'>流程名称</TD>\r\n");
		sb_html.append(" 	<TD class='val'>");
		sb_html.append(hvs[0].getStringValue("wfprocess_name", ""));
		sb_html.append("</TD></TR>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append(" 	<TD class='key'>流程概述</TD>\r\n");
		sb_html.append(" 	<TD class='val' colspan='3'>");
		sb_html.append(convertStr(hvs[0].getStringValue("wfdesc", ""), 200));
		sb_html.append("</TD></TR>\r\n");
		sb_html.append("</TABLE>\r\n");
		return sb_html.toString();
	}

	/**
	 * 流程相关法规
	 * @param _processid 流程id
	 * @return
	 * @throws Exception
	 */
	private String getLawByProcessId(String _processid) throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		HashVO[] hvs = commDMO.getHashVoArrayByDS(null, " select * from cmp_cmpfile_law where relationtype='流程' and wfprocess_id=" + _processid + " order by id");
		if (hvs == null || hvs.length == 0) {
			return "";
		}
		sb_html.append("<TABLE class='myTable' cellSpacing=1>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append("	<TD class='title' colspan='3'>相关法规</TD></TR>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append("	<TD class='key textcenter'>法规名称</TD>\r\n");
		sb_html.append("	<TD class='key textcenter'>法规条文标题</TD>\r\n");
		sb_html.append("	<TD class='key textcenter'>法规条文内容</TD></TR>\r\n");
		for (int i = 0; i < hvs.length; i++) {
			sb_html.append("  <TR>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(hvs[i].getStringValue("law_name"));
			sb_html.append("</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(hvs[i].getStringValue("lawitem_title"));
			sb_html.append("</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(convertStr(hvs[i].getStringValue("lawitem_content"), 60));//这里应该链接法规内容
			sb_html.append("</TD></TR>\r\n");
		}
		sb_html.append("</TABLE>\r\n");
		return sb_html.toString();
	}

	/**
	 * 流程相关制度
	 * @param _processid 流程id
	 * @return
	 * @throws Exception
	 */
	private String getRuleByProcessId(String _processid) throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		HashVO[] hvs = commDMO.getHashVoArrayByDS(null, " select * from cmp_cmpfile_rule where relationtype='流程' and wfprocess_id=" + _processid + " order by id");
		if (hvs == null || hvs.length == 0) {
			return "";
		}
		sb_html.append("<TABLE class='myTable' cellSpacing=1>\r\n");
		boolean hasruleitem = tbUtil.getSysOptionBooleanValue("制度是否分条目", true);
		if (hasruleitem) {
			sb_html.append("  <TR>\r\n");
			sb_html.append("	<TD class='title' colspan='3'>相关制度</TD></TR>\r\n");
			sb_html.append("  <TR>\r\n");
			sb_html.append("	<TD class='key textcenter'>制度名称</TD>\r\n");
			sb_html.append("	<TD class='key textcenter'>制度条文标题</TD>\r\n");
			sb_html.append("	<TD class='key textcenter'>制度条文内容</TD></TR>\r\n");
			for (int i = 0; i < hvs.length; i++) {
				sb_html.append("  <TR>\r\n");
				sb_html.append(" 	<TD class='val'>");
				sb_html.append(hvs[i].getStringValue("rule_name"));
				sb_html.append("</TD>\r\n");
				sb_html.append(" 	<TD class='val'>");
				sb_html.append(hvs[i].getStringValue("ruleitem_title", ""));
				sb_html.append("</TD>\r\n");
				sb_html.append(" 	<TD class='val'>");
				sb_html.append(convertStr(hvs[i].getStringValue("ruleitem_content"), 60));//这里应该链接制度内容
				sb_html.append("</TD></TR>\r\n");
			}
		} else {
			sb_html.append("  <TR>\r\n");
			sb_html.append("	<TD class='title'>相关制度</TD></TR>\r\n");
			sb_html.append("  <TR>\r\n");
			sb_html.append("	<TD class='key textcenter'>制度名称</TD>\r\n");
			for (int i = 0; i < hvs.length; i++) {
				sb_html.append("  <TR>\r\n");
				sb_html.append(" 	<TD class='val'>");
				sb_html.append(hvs[i].getStringValue("rule_name"));
				sb_html.append("</TD></TR>\r\n");
			}
		}
		sb_html.append("</TABLE>\r\n");
		return sb_html.toString();
	}

	/**
	 * 流程相关检查要点
	 * @param _processid 流程id
	 * @return
	 * @throws Exception
	 */
	private String getCheckpointByProcessId(String _processid) throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		HashVO[] hvs = commDMO.getHashVoArrayByDS(null, " select * from cmp_cmpfile_checkpoint where relationtype='流程' and wfprocess_id=" + _processid + " order by id");
		if (hvs == null || hvs.length == 0) {
			return "";
		}
		sb_html.append("<TABLE class='myTable' cellSpacing=1>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append("	<TD class='title' colspan='4'>相关检查要点</TD></TR>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append("	<TD class='key textcenter'>检查要点类型</TD>\r\n");
		sb_html.append("	<TD class='key textcenter'>检查项目</TD>\r\n");
		sb_html.append("	<TD class='key textcenter'>检查要点</TD>\r\n");
		sb_html.append("	<TD class='key textcenter'>检查方法</TD></TR>\r\n");
		for (int i = 0; i < hvs.length; i++) {
			sb_html.append("  <TR>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(hvs[i].getStringValue("checktype_name", ""));
			sb_html.append("</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(convertStr(hvs[i].getStringValue("checkitem_project"), 60));
			sb_html.append("</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(convertStr(hvs[i].getStringValue("checkitem_point"), 60));//
			sb_html.append("</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(convertStr(hvs[i].getStringValue("checkitem_method"), 60));//
			sb_html.append("</TD></TR>\r\n");
		}
		sb_html.append("</TABLE>\r\n");
		return sb_html.toString();
	}

	/**
	 * 环节相关法规
	 * @param _activityid 环节id
	 * @return
	 * @throws Exception
	 */
	private String getLawByActivityId(String _activityid) throws Exception {
		HashVO[] hvs = commDMO.getHashVoArrayByDS(null, " select * from cmp_cmpfile_law where wfactivity_id=" + _activityid + " order by id");
		if (hvs == null || hvs.length == 0) {
			return "";
		}
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<TABLE class='myTable' cellSpacing=1>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append("	<TD class='title' colspan='3'>相关法规</TD></TR>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append("	<TD class='key textcenter'>法规名称</TD>\r\n");
		sb_html.append("	<TD class='key textcenter'>法规条文标题</TD>\r\n");
		sb_html.append("	<TD class='key textcenter'>法规条文内容</TD></TR>\r\n");
		for (int i = 0; i < hvs.length; i++) {
			sb_html.append("  <TR>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(hvs[i].getStringValue("law_name"));
			sb_html.append("</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(hvs[i].getStringValue("lawitem_title"));
			sb_html.append("</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(convertStr(hvs[i].getStringValue("lawitem_content"), 60));
			sb_html.append("</TD></TR>\r\n");
		}
		sb_html.append("</TABLE>\r\n");
		return sb_html.toString();
	}

	/**
	 * 环节相关制度
	 * @param _activityid 环节id
	 * @return
	 * @throws Exception
	 */
	private String getRuleByActivityId(String _activityid) throws Exception {
		StringBuffer sb_html = new StringBuffer(); //
		HashVO[] hvs = commDMO.getHashVoArrayByDS(null, " select * from cmp_cmpfile_rule where  wfactivity_id=" + _activityid + " order by id");
		if (hvs == null || hvs.length == 0) {
			return "";
		}
		sb_html.append("<TABLE class='myTable' cellSpacing=1>\r\n");
		boolean hasruleitem = tbUtil.getSysOptionBooleanValue("制度是否分条目", true);
		if (hasruleitem) {
			sb_html.append("  <TR>\r\n");
			sb_html.append("	<TD class='title' colspan='3'>相关制度</TD></TR>\r\n");
			sb_html.append("  <TR>\r\n");
			sb_html.append("	<TD class='key textcenter'>制度名称</TD>\r\n");
			sb_html.append("	<TD class='key textcenter'>制度条文标题</TD>\r\n");
			sb_html.append("	<TD class='key textcenter'>制度条文内容</TD></TR>\r\n");
			for (int i = 0; i < hvs.length; i++) {
				sb_html.append("  <TR>\r\n");
				sb_html.append(" 	<TD class='val'>");
				sb_html.append(hvs[i].getStringValue("rule_name"));
				sb_html.append("</TD>\r\n");
				sb_html.append(" 	<TD class='val'>");
				sb_html.append(hvs[i].getStringValue("ruleitem_title", ""));
				sb_html.append("</TD>\r\n");
				sb_html.append(" 	<TD class='val'>");
				sb_html.append(convertStr(hvs[i].getStringValue("ruleitem_content"), 60));//这里应该链接制度内容
				sb_html.append("</TD></TR>\r\n");
			}
		} else {
			sb_html.append("  <TR>\r\n");
			sb_html.append("	<TD class='title'>相关制度</TD></TR>\r\n");
			sb_html.append("  <TR>\r\n");
			sb_html.append("	<TD class='key textcenter'>制度名称</TD>\r\n");
			for (int i = 0; i < hvs.length; i++) {
				sb_html.append("  <TR>\r\n");
				sb_html.append(" 	<TD class='val'>");
				sb_html.append(hvs[i].getStringValue("rule_name"));
				sb_html.append("</TD></TR>\r\n");
			}
		}
		sb_html.append("</TABLE>\r\n");
		return sb_html.toString();
	}

	/**
	 * 环节相关检查要点
	 * @param _activityid 环节id
	 * @return
	 * @throws Exception
	 */
	private String getCheckpointByActivityId(String _activityid) throws Exception {
		HashVO[] hvs = commDMO.getHashVoArrayByDS(null, " select * from cmp_cmpfile_checkpoint where wfactivity_id=" + _activityid + " order by id");
		if (hvs == null || hvs.length == 0) {
			return "";
		}
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<TABLE class='myTable' cellSpacing=1>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append("	<TD class='title' colspan='4'>相关检查要点</TD></TR>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append("	<TD class='key textcenter'>检查要点类型</TD>\r\n");
		sb_html.append("	<TD class='key textcenter'>检查项目</TD>\r\n");
		sb_html.append("	<TD class='key textcenter'>检查要点</TD>\r\n");
		sb_html.append("	<TD class='key textcenter'>检查方法</TD></TR>\r\n");
		for (int i = 0; i < hvs.length; i++) {
			sb_html.append("  <TR>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(hvs[i].getStringValue("checktype_name", ""));
			sb_html.append("</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(convertStr(hvs[i].getStringValue("checkitem_project"), 60));
			sb_html.append("</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(convertStr(hvs[i].getStringValue("checkitem_point"), 60));//
			sb_html.append("</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(convertStr(hvs[i].getStringValue("checkitem_method"), 60));//
			sb_html.append("</TD></TR>\r\n");
		}
		sb_html.append("</TABLE>\r\n");
		return sb_html.toString();
	}

	/**
	 * 环节相关操作要求
	 * @param _activityid 环节id
	 * @return
	 * @throws Exception
	 */
	private String getOperaterequireByActivityId(String _activityid) throws Exception {
		HashVO[] hvs = commDMO
				.getHashVoArrayByDS(
						null,
						" select dept.name operatedept,post.name operatepost,opereq.operatedesc,opereq.operatereq,opereq.operaterefpost,opereq.opeinput,opereq.opeoutput from cmp_cmpfile_wfopereq opereq  left join pub_corp_dept dept on opereq.operatedept=dept.id left join  pub_post post on opereq.operatepost=post.id  where  wfactivity_id="
								+ _activityid + " order by opereq.id");
		if (hvs == null || hvs.length == 0) {
			return "";
		}
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<TABLE class='myTable' cellSpacing=1>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append("	<TD class='title' colspan='4'>操作要求</TD></TR>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append("	<TD class='key'>操作部门</TD>\r\n");
		sb_html.append(" 	<TD class='val '>");
		sb_html.append(hvs[0].getStringValue("operatedept", ""));
		sb_html.append("</TD>\r\n");
		sb_html.append(" 	<TD class='key'>操作岗位</TD>\r\n");
		sb_html.append(" 	<TD class='val'>");
		sb_html.append(hvs[0].getStringValue("operatepost", ""));
		sb_html.append("</TD></TR>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append(" 	<TD class='key'>职责</TD>\r\n");
		sb_html.append(" 	<TD class='val' colspan='3'>");
		sb_html.append(convertStr(hvs[0].getStringValue("operatedesc"), 200));
		sb_html.append("</TD></TR>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append(" 	<TD class='key'>操作规范</TD>\r\n");
		sb_html.append(" 	<TD class='val' colspan='3'>");
		sb_html.append(convertStr(hvs[0].getStringValue("operatereq"), 200));
		sb_html.append("</TD></TR>\r\n");
		sb_html.append("  <TR>\r\n");
		sb_html.append(" 	<TD class='key'>相关岗位</TD>\r\n");
		sb_html.append(" 	<TD class='val' colspan='3'>");
		String posts = hvs[0].getStringValue("operaterefpost", "");
		if (!"".equals(posts)) {
			posts = tbUtil.getInCondition(tbUtil.split(posts, ";"));
			posts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_post where id in(" + posts + ")")).replaceAll("'", "");
			sb_html.append((posts.equals("-99999") ? "" : posts));
		}
		sb_html.append("</TD></TR>\r\n");

		String str_opeinput = hvs[0].getStringValue("opeinput", true);
		String str_opeoutput = hvs[0].getStringValue("opeoutput", true);
		if (!"".equals(str_opeinput)) {
			sb_html.append("  <TR>\r\n");
			sb_html.append("	 <TD class='key'>输入</TD>\r\n");
			sb_html.append(" 	<TD class='val' colspan='3'>");
			sb_html.append(convertStr(str_opeinput, 200));
			sb_html.append("</TD></TR>\r\n");
		}
		if (!"".equals(str_opeoutput)) {
			sb_html.append("  <TR>\r\n");
			sb_html.append(" 	<TD class='key'>输出</TD>\r\n");
			sb_html.append(" 	<TD class='val' colspan='3'>");
			sb_html.append(convertStr(str_opeoutput, 200));
			sb_html.append("</TD></TR>\r\n");
		}
		sb_html.append("</TABLE>\r\n");
		return sb_html.toString();
	}

	/**
	 * 环节风险点
	 * @param _activityid 环节id
	 * @return
	 * @throws Exception
	 */
	private String getRiskByActivityId(String _activityid) throws Exception {
		HashVO[] hvs = commDMO.getHashVoArrayByDS(null, "select * from cmp_risk where wfactivity_id=" + _activityid + " order by rank,id");
		if (hvs == null || hvs.length == 0) {
			return "";
		}
		//财务损失、合规损失、声誉损失,未显示其值，只显ID,修改[YangQing/2014-03-12]
		String sql1 = "select id,code from pub_comboboxdict where type='风险损失_财务损失'";
		String sql2 = "select id,code from pub_comboboxdict where type='风险损失_合规因素'";
		String sql3 = "select id,code from pub_comboboxdict where type='风险损失_声誉影响'";

		HashMap<String, String> finalostMap = commDMO.getHashMapBySQLByDS(null, sql1);
		HashMap<String, String> cmplostMap = commDMO.getHashMapBySQLByDS(null, sql2);
		HashMap<String, String> honorlostMap = commDMO.getHashMapBySQLByDS(null, sql3);

		StringBuffer sb_html = new StringBuffer(); //		
		for (int i = 0; i < hvs.length; i++) {
			String finalost = hvs[i].getStringValue("finalost", "");
			String cmplost = hvs[i].getStringValue("cmplost", "");
			String honorlost = hvs[i].getStringValue("honorlost", "");

			sb_html.append("<TABLE class='myTable' cellSpacing=1>\r\n");
			sb_html.append("  <TR>\r\n");
			sb_html.append("	<TD class='title' colspan='6'>风险点" + (i + 1) + "</TD></TR>\r\n");
			sb_html.append("  <TR>\r\n");
			sb_html.append("	<TD class='key'>风险编码</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(hvs[i].getStringValue("riskcode"));
			sb_html.append("</TD>\r\n");
			sb_html.append(" 	<TD class='key'>风险名称</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(hvs[i].getStringValue("riskname"));
			sb_html.append("</TD>\r\n");
			sb_html.append(" 	<TD class='key'>风险分类</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(hvs[i].getStringValue("risktype"));
			sb_html.append("</TD></TR>\r\n");
			sb_html.append("  <TR>\r\n");
			sb_html.append("	<TD class='key'>风险等级</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(hvs[i].getStringValue("rank"));
			sb_html.append("</TD>\r\n");
			sb_html.append(" 	<TD class='key'>可能性</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(hvs[i].getStringValue("possible"));
			sb_html.append("</TD>\r\n");
			sb_html.append("	<TD class='key'>影响程度</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(hvs[i].getStringValue("serious"));
			sb_html.append("</TD></TR>\r\n");
			sb_html.append("  <TR>\r\n");
			sb_html.append(" 	<TD class='key'>财务损失</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(finalostMap.containsKey(finalost) ? finalostMap.get(finalost) : "");
			sb_html.append("</TD>\r\n");
			sb_html.append("	<TD class='key'>合规损失</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(cmplostMap.containsKey(cmplost) ? cmplostMap.get(cmplost) : "");
			sb_html.append("</TD>\r\n");
			sb_html.append(" 	<TD class='key'>声誉损失</TD>\r\n");
			sb_html.append(" 	<TD class='val'>");
			sb_html.append(honorlostMap.containsKey(honorlost) ? honorlostMap.get(honorlost) : "");
			sb_html.append("</TD></TR>\r\n");
			sb_html.append("  <TR>\r\n");
			sb_html.append(" 	<TD class='key'>风险描述</TD>\r\n");
			sb_html.append(" 	<TD class='val' colspan='5'>");
			sb_html.append(convertStr(hvs[i].getStringValue("riskdescr"), 200));
			sb_html.append("</TD></TR>\r\n");
			//			sb_html.append("  <TR>\r\n");//去掉这两个描述，录入界面已经屏蔽掉了【李春娟/2014-08-04】
			//			sb_html.append("	 <TD class='key'>流程内控制措施</TD>\r\n");
			//			sb_html.append(" 	<TD class='val' colspan='5'>");
			//			sb_html.append(convertStr(hvs[i].getStringValue("ctrlfn"), 200));
			//			sb_html.append("</TD></TR>\r\n");
			//			sb_html.append("  <TR>\r\n");
			//			sb_html.append(" 	<TD class='key'>流程外控制措施</TD>\r\n");
			//			sb_html.append(" 	<TD class='val' colspan='5'>");
			//			sb_html.append(convertStr(hvs[i].getStringValue("ctrlfn2"), 200));
			//			sb_html.append("</TD></TR>\r\n");
			sb_html.append("  <TR>\r\n");
			sb_html.append(" 	<TD class='key'>控制措施</TD>\r\n");
			sb_html.append(" 	<TD class='val' colspan='5'>");
			sb_html.append(convertStr(hvs[i].getStringValue("ctrlfn3"), 200));
			sb_html.append("</TD></TR>\r\n");
			sb_html.append("</TABLE>\r\n");
		}
		return sb_html.toString();
	}

	/**
	 * 取得某一个环节上的操作要求信息!!
	 * @param _hvs
	 * @param _activityID
	 * @return
	 * @throws Exception 
	 */
	private String getOpereqInfoByOneActivity(HashVO[] _hvs, String _activityID) throws Exception {
		StringBuffer sb_tip = new StringBuffer(); //
		for (int i = 0; i < _hvs.length; i++) {
			if (_hvs[i].getStringValue("wfactivity_id") != null && _hvs[i].getStringValue("wfactivity_id").equals(_activityID)) {
				String deptandpost = _hvs[i].getStringValue("operatedept");
				String str_post = _hvs[i].getStringValue("operatepost");
				if (deptandpost == null && str_post == null) {//单选，如果操作部门和岗位都为空的话，输出无
					deptandpost = "无";
				} else if (deptandpost == null) {
					deptandpost = str_post;
				} else if (deptandpost != null && str_post != null) {
					deptandpost = deptandpost + "/" + str_post;
				}
				sb_tip.append("操作部门/岗位:" + deptandpost + "\r\n"); //
				sb_tip.append("职责:" + _hvs[i].getStringValue("operatedesc", true) + "\r\n");
				sb_tip.append("操作规范:" + _hvs[i].getStringValue("operatereq", true) + "\r\n");
				sb_tip.append("相关岗位:"); //
				String posts = _hvs[i].getStringValue("operaterefpost", "");
				if (!"".equals(posts)) {
					posts = tbUtil.getInCondition(tbUtil.split(posts, ";"));
					posts = tbUtil.getInCondition(commDMO.getStringArrayFirstColByDS(null, "select name from pub_post where id in(" + posts + ")")).replaceAll("'", "");
					sb_tip.append((posts.equals("-99999") ? "" : posts));
				}
				sb_tip.append("\r\n");
				String str_opeinput = _hvs[i].getStringValue("opeinput", true);
				if (!"".equals(str_opeinput)) {
					sb_tip.append("输入:" + str_opeinput + "\r\n");
				}
				String str_opeoutput = _hvs[i].getStringValue("opeoutput", true);
				if (!"".equals(str_opeoutput)) {
					sb_tip.append("输出:" + str_opeoutput + "\r\n");
				}
			}
		}
		return sb_tip.toString(); //
	}

	/**
	 * 取得某一个环节上的所有风险点信息!!
	 * @param _hvs
	 * @param _activityID
	 * @return
	 */
	private String getRiskInfoByOneActivity(HashVO[] _hvs, String _activityID) {
		int li_count = 0; //
		StringBuffer sb_tip = new StringBuffer(); //
		for (int i = 0; i < _hvs.length; i++) {
			if (_hvs[i].getStringValue("wfactivity_id") != null && _hvs[i].getStringValue("wfactivity_id").equals(_activityID)) {
				li_count++; //
				if (li_count != 1) {
					sb_tip.append("\r\n");
				}
				sb_tip.append("【" + li_count + "】风险点:" + _hvs[i].getStringValue("riskcode") + "/" + _hvs[i].getStringValue("riskname") + "\r\n"); //
				sb_tip.append(" 可能性:" + _hvs[i].getStringValue("possible", true));
				sb_tip.append("      影响程度:" + _hvs[i].getStringValue("serious", true) + "\r\n");
				sb_tip.append(" 风险等级:" + _hvs[i].getStringValue("rank", true) + "\r\n"); //
				sb_tip.append(" 风险描述:" + _hvs[i].getStringValue("riskdescr", true) + "\r\n");
			}
		}
		return sb_tip.toString(); //
	}

	/**
	 * 有的内容，比如法规条文内容等内容比较多，html撑开很难看，可以只看到部分文字，点开查看全文
	 * @param _str
	 * @return
	 */
	public String convertStr(String _str, int _showlength) {
		if (_str == null) {
			return "";
		}
		_str = _str.replaceAll("\n", "");
		_str = _str.replaceAll("\r\n", "");
		if (_str.length() < _showlength) {
			return _str;
		}
		return popDivStart + _str + popDivEnd;
	}
}
