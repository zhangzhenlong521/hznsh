package com.pushworld.ipushgrc.bs.law.p010;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WebCallBeanIfc;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * 法律法规明细查询 com.pushworld.ipushgrc.ui.law.p010.LawEditWKPanel 外规维护
 * com.pushworld.ipushgrc.ui.law.p020.LawQueryWKPanel 外规查询
 * 和com.pushworld.pushgrc.ui.law.interceptor.OarInquire中引用到
 * 
 * 2012-1-30提交修改，内容字体大小可放大，边框加圆角！
 * @author
 * 
 */
public class LawPrimaryKeyAllFileInquire implements WebCallBeanIfc {
	private boolean showAnnex = false; // 是否显示附件路径
	private boolean showFormAnnex = new TBUtil().getSysOptionBooleanValue("制度预览是否显示相关表单附件", false);; //是否显示相关表单附件
	private boolean cancopy = new TBUtil().getSysOptionBooleanValue("制度是否允许拷贝", true);
	private String table_main;
	private CommDMO dmo = new CommDMO();
	private TBUtil tbutil = new TBUtil();

	public String getHtmlContent(HashMap _map) throws Exception {
		showAnnex = tbutil.getSysOptionBooleanValue("法规制度查看时是否显示附件", false);

		String sid = (String) _map.get("id");
		table_main = (String) _map.get("table_main");
		String table_main_file = (String) _map.get("table_main_file");
		String column_name = (String) _map.get("column_name");
		String column_title = (String) _map.get("column_title");
		String table_content = (String) _map.get("table_content");
		String law_rule_content = (String) _map.get("content");
		String content_foreignkey = (String) _map.get("content_foreignkey");
		String content_filekey = (String) _map.get("content_filekey");
		String url = (String) _map.get("url");
		String[] primarykey = (String[]) _map.get("primarykey");
		String[] hlightitem = (String[]) _map.get("hlightitem");
		StringBuffer sb_html = new StringBuffer(); //
		//		sb_html.append(getDocType());  //加上这个标准，格式就错乱！
		sb_html.append("<html>\n");
		sb_html.append(getMeta());
		sb_html.append("<head><title></title>\r\n");
		sb_html.append(getStyle());
		sb_html.append(getJs());
		sb_html.append("</head>\r\n");
		if (!cancopy && table_main.contains("rule")) {
			sb_html.append(pbShortcutKeyJS());
			sb_html.append("<body oncontextmenu=\"return false\" onselectstart=\"return false\" ondragstart=\"return false\" onbeforecopy=\"return false\" oncopy=document.selection.empty() onselect=document.selection.empty()>\r\n");
			sb_html.append("<div style=\"text-align:right\">");
			sb_html.append(getFontChangeSize());
			sb_html.append("</div>");
		} else {
			sb_html.append("<body>");
			sb_html.append("<div style=\"text-align:right\">");
			sb_html.append(getFontChangeSize());
			sb_html.append(getPrintDiv());
			sb_html.append("</div>");
		}
		sb_html.append("<div style=\"margin:20 40;\">");
		sb_html.append("<b class=\"b1\"></b><b class=\"b2 d1\"></b><b class=\"b3 d1\"></b><b class=\"b4 d1\"></b>");
		sb_html.append("<div class=\"b d1 k\">");
		sb_html.append("<div id=\"content\" class=\"content\">\r\n");
		String sr_content = "";
		try {
			HashVO[] hvos = dmo.getHashVoArrayAsTreeStructByDS(null, "select * from  " + table_content + "   where  " + content_foreignkey + "=" + sid + " order by linkcode,abs(id)", "id", "parentid", "seq", null);

			HashVO[] mainVO = dmo.getHashVoArrayByDS(null, "select * from  " + table_main + "   where  id=" + sid);// table_main_file
			sb_html.append("<p class=\"title0\">" + mainVO[0].getStringValue(column_name) + "</p>\r\n");
			sb_html.append("</B></font>\r\n");
			for (int i = 0; i < hvos.length; i++) {
				StringBuffer sb_all = new StringBuffer();
				String str_title = hvos[i].getStringValue(column_title, "");
				String itemID = hvos[i].getStringValue("id");
				String state = hvos[i].getStringValue("state");//新增的子表状态，是否被修订
				boolean lightFlag = false; //条目题目是否高亮该段落
				if (itemID != null && hlightitem != null) {
					for (int j = 0; j < hlightitem.length; j++) {
						if (hlightitem[j] != null && hlightitem[j].equals(itemID)) {
							lightFlag = true;
							break;
						}
					}
				}
				sr_content = hvos[i].getStringValue(law_rule_content);
				if (sr_content != null) { //同一个条目下的内容，如果换行需要每段首行空两格！P标签用样式控制。[郝明2012-3-21]
					String contents[] = sr_content.split("\n");
					StringBuffer sb = new StringBuffer();
					for (int j = 0; j < contents.length; j++) {
						if (j == 0) {
							sb.append(contents[j]);
						} else {
							sb.append("<p>");
							sb.append(contents[j]);
							sb.append("</p>");
						}
					}
					sr_content = sb.toString();
				}
				String[] ss = new String[2];
				if (str_title != null && str_title.contains(" ")) {
					ss[0] = str_title.substring(0, str_title.indexOf(" ")).trim();
					ss[1] = str_title.substring(str_title.indexOf(" "));
				} else {
					ss[0] = str_title;
					ss[1] = "";
				}

				if ("Y".equals(state)) {//【李春娟/2015-04-20】
					sb_html.append("<span style=\"background-color:#cccccc\">");
				}
				if (lightFlag) {
					str_title = "<a name=\"light\"></a><span style=\"background-color:#AABB00\">" + str_title + "</span>";
				}
				if (str_title != null && (str_title.contains("章") && ss[0].startsWith("第") && ss[0].endsWith("章"))) {
					sb_html.append("<p  id=\"tt1\" class=\"title1\">" + str_title.toString().replaceAll("null", "") + "</p>");
					if (sr_content != null && !sr_content.trim().equals("")) {
						sb_html.append("<p>&nbsp;&nbsp;");
					}
				} else if (str_title != null && (str_title.contains("节") && ss[0].startsWith("第") && ss[0].endsWith("节"))) {
					sb_html.append("<p  id=\"tt2\" class=\"title2\">" + str_title.toString().replaceAll("null", "") + "</p>");
					if (sr_content != null && !sr_content.trim().equals("")) {
						sb_html.append("<p>&nbsp;&nbsp;");
					}
				} else if (str_title != null && (str_title.contains("条") && ss[0].startsWith("第") && ss[0].endsWith("条"))) {
					sb_html.append("<p><span id=\"tt3\" class=\"title3\">" + str_title.toString().replaceAll("null", "") + "</span>\r\n");
				} else {
					sb_html.append("<p><span id=\"tt3\" class=\"title3\">" + str_title.toString().replaceAll("null", "") + "</span>\r\n");
				}
				sb_all.append(sr_content);
				String color = sb_all.toString().replaceAll("null", "");
				if (primarykey != null) {
					for (int j = 0; j < primarykey.length; j++) {
						color = color.replaceAll(primarykey[j], "<span  id=\"word\" class=\"word\">" + primarykey[j] + "</span>\r\n");
					}
				}
				sb_html.append(color);
				sb_html.append("</p>\r\n");
				if (showAnnex && hvos[i].getStringValue(content_filekey) != null && !hvos[i].getStringValue(content_filekey).equals("")) {
					sb_html.append("<span class=\"font-size:12px\">" + getHtmlFileHref(hvos[i].getStringValue(content_filekey), url) + "</span>");
				}
				if ("Y".equals(state)) {//【李春娟/2015-04-20】
					sb_html.append("</span>");
				}
			}
			if (table_main_file != null && showAnnex) {
				sb_html.append("<p>" + getHtmlFileHref(mainVO[0].getStringValue(table_main_file), url));
				sb_html.append("</p>\r\n");
			}
			if (showFormAnnex && table_main.contains("rule")) { //制度
				String formids = mainVO[0].getStringValue("formids");
				if (!tbutil.isEmpty(formids)) {
					String BILLFILE[] = dmo.getStringArrayFirstColByDS(null, "select BILLFILE from BILLMODEL where id in(" + tbutil.getInCondition(formids) + ")");
					List allfile = new ArrayList();
					for (int j = 0; j < BILLFILE.length; j++) {
						if (!tbutil.isEmpty(BILLFILE[j])) {
							String currFiles[] = tbutil.split(BILLFILE[j], ";");
							for (int k = 0; k < currFiles.length; k++) {
								allfile.add(currFiles[k]);
							}
						}
					}
					if (allfile.size() > 0) {
						sb_html.append("<p><b>相关表单:</b></p>");
						if (tbutil.getSysOptionBooleanValue("制度显示相关表单附件是否换行", true)) {
							for (int j = 0; j < allfile.size(); j++) {
								String fileName = (String) allfile.get(j);
								sb_html.append("<p>" + getHtmlFileHref(fileName, url));
								sb_html.append("</p>\r\n");
							}
						} else {
							StringBuffer allfileStr = new StringBuffer();
							for (int j = 0; j < allfile.size(); j++) {
								String fileName = (String) allfile.get(j);
								allfileStr.append(";" + fileName);
							}
							allfileStr.append(";");
							sb_html.append("<p>" + getHtmlFileHref(allfileStr.toString(), url));
							sb_html.append("</p>\r\n");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		sb_html.append("</div>\r\n");
		sb_html.append("</div>");
		sb_html.append("<b class=\"b4b d1\"></b><b class=\"b3b d1\"></b><b class=\"b2b d1\"></b><b class=\"b1b\"></b>");
		sb_html.append("</div>");

		sb_html.append(getJumpTop());
		sb_html.append("<br><br>\r\n");
		sb_html.append("</body>\r\n");
		sb_html.append("</html>\r\n");

		return sb_html.toString();
	}

	private String getHtmlFileHref(String realfiles, String url) {
		StringBuffer returnStr = new StringBuffer("");
		if (realfiles != null && !"".equals(realfiles)) {
			String[] realfilenames = tbutil.split(realfiles, ";");
			if (realfilenames != null && realfilenames.length > 0) {
				for (int i = 0; i < realfilenames.length; i++) {
					if (realfilenames[i] != null && !"".equals(realfilenames[i])) {
						if (i == 0)
							returnStr.append("&nbsp;<a  target=\"_blank\" href=\"" + url + "/DownLoadFileServlet?pathtype=upload&filename=" + realfilenames[i] + "\">" + getViewFileName(realfilenames[i]) + "</a>");
						else {
							returnStr.append("、<a target=\"_blank\" href=\"" + url + "/DownLoadFileServlet?pathtype=upload&filename=" + realfilenames[i] + "\">" + getViewFileName(realfilenames[i]) + "</a>");
						}
					}
				}
			}
		}
		return returnStr.toString();
	}

	// 取得显示的文件名!即去掉索引号
	private String getViewFileName(String _realFileName) {
		if (_realFileName != null && _realFileName.indexOf("/") != -1) {
			String param = _realFileName.substring(_realFileName.lastIndexOf("/") + 1, _realFileName.length());
			if (param != null && param.startsWith("N")) {
				return (new TBUtil().convertHexStringToStr(param.substring(param.indexOf("_") + 1, param.lastIndexOf("."))) + (param.substring(param.lastIndexOf("."), param.length()))); //
			} else {
				return param; // 以前的版本也有存路径的？
			}
		} else {
			if (_realFileName == null || _realFileName.indexOf("_") < 0) {
				return _realFileName; //
			}
			return _realFileName.substring(_realFileName.indexOf("_") + 1, _realFileName.length()); //
		}
	}

	public String getStyle() {
		StringBuffer sb = new StringBuffer();
		sb.append("<style>\n");
		sb.append("body,td,th {\n");
		sb.append("    background-color:#fff; margin:0; font-size:12px; font-family:\"宋体\", arial; color:#333;\n");
		sb.append("}\n");
		sb.append("p {TEXT-INDENT: 2em;line-height:150%}\n");
		sb.append("a{color:blue;text-decoration: underline;font-size:12px;font-family:\"宋体\",arial;}\n");
		sb.append("a:hover{color: red;text-decoration: underline;}\n");
		sb.append(" .content {width:100%;height:100%;border:0px solid #ccc;padding:20px 20px 20px 20px;margin:0 auto;\n");
		sb.append("background-image: -moz-linear-gradient(top, #FEFEFE, #D6D6D6);\n");
		sb.append("background-image: -webkit-gradient(linear,left top,left bottom,color-stop(0, #FEFEFE),color-stop(1, #D6D6D6));\n");
		sb.append("filter:  progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='#FEFEFE', endColorstr='#D6D6D6');\n");
		sb.append("-ms-filter: \"progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='#FEFEFE', endColorstr='#D6D6D6')\";\n");
		sb.append("}\n");
		sb.append(".title0 {color:#ff0000; FONT-SIZE: 24px;font-weight:bold;text-align:center;}\n");
		sb.append(".title1 {FONT-SIZE: 20px;font-weight:bold;text-align:center;}\n");
		sb.append(".title2 {FONT-SIZE: 18px;font-weight:bold;text-align:center;}\n");
		sb.append(".title3 {FONT-SIZE: 12px;font-weight:bold;}\n");
		sb.append(".word {background-color:#FF9632}\n");
		sb.append("#top-link {PADDING-RIGHT:10px; PADDING-LEFT:10px;FONT-WEIGHT:bold; RIGHT:5px;PADDING-BOTTOM:10px;COLOR:green;BOTTOM:5px;PADDING-TOP:10px;POSITION:fixed;" + "TEXT-DECORATION:none}\n");
		sb.append(".b1,.b2,.b3,.b4,.b1b,.b2b,.b3b,.b4b,.b{display:block;overflow:hidden;}\n");
		sb.append(".b1,.b2,.b3,.b1b,.b2b,.b3b{height:1px;}\n");
		sb.append(".b2,.b3,.b4,.b2b,.b3b,.b4b,.b{border-left:1px solid #999;border-right:1px solid #999;}\n");
		sb.append(".b1,.b1b{margin:0 5px;background:#999;}\n");
		sb.append(".b2,.b2b{margin:0 3px;border-width:2px;}\n");
		sb.append(".b3,.b3b{margin:0 2px;}\n");
		sb.append(".b4,.b4b{height:2px;margin:0 1px;}\n");
		sb.append(".d1{background:#F7F8F9;}\n");
		if (!cancopy && table_main.contains("rule")) {
			sb.append("@media print{body {display:none}}\n");//如果是制度，并且不可打印，则需要增加一句（打印时内容不显示），否则不点击html页面的时候使用ctrl+p可以进行打印。【李春娟/2012-06-07】
		}
		sb.append(".edit {background-color:#ccccff}\n");//已被修订的标志【李春娟/2014-04-20】
		sb.append("</style>\n");
		sb.append("<style type=\"text/css\" media=print>\r\n");
		sb.append(".noprint{display:none}");
		sb.append("</style>\n");
		return sb.toString();
	}

	public String getPrintDiv() {
		StringBuffer sb_print = new StringBuffer();
		sb_print.append("<input class=\"noprint\" type=\"button\" value=\"打印\" onClick=\"doPrint()\"/>");
		return sb_print.toString();
	}

	public String getFontChangeSize() {
		return "<span class=\"noprint\"><span  style=\"margin-right:10px;margin-top:5px\"><font style=\"font-size:12px\">字体：</font><a style=\"font-size:16px\" href=\"javascript:dozoom(4)\" >大</a></span><span  style=\"margin-right:20px;margin-top:5px\"><a style=\"font-size:12px\" href=\"javascript:dozoom(0)\">小</a></span></span>";
	}

	public String getJs() {
		StringBuffer sb_print = new StringBuffer();
		sb_print.append("<script>\n");
		sb_print.append(" location.hash=\"light\"; ");
		sb_print.append("function doPrint(){\n");//ie的js打印很弱，一些特殊的css+div就识别不好。导致外规第一页老空，固加如一下一段代码来控制打印，但是目前直接打印有页眉页脚，调用ActiveX可以去掉，目前没搞。可以在浏览器中页面设置把页眉页脚去掉。
		sb_print.append("var headstr = \"<html><head><title></title></head><body>\"\r\n");
		sb_print.append("var footstr=\"</body>\"\r\n");
		sb_print.append("var newstr=document.all.item(\"content\").innerHTML\r\n");
		sb_print.append("var oldstr=document.body.innerHTML\r\n");
		sb_print.append("document.body.innerHTML = headstr+newstr+footstr\r\n");
		sb_print.append("window.print();\n");
		sb_print.append("document.body.innerHTML = oldstr\r\n");
		sb_print.append("return false\r\n");
		sb_print.append("}\n");
		sb_print.append("function dozoom(size){\n");
		sb_print.append("document.body.style.fontSize=(size+12)+'px';\n");
		sb_print.append("var tt1=document.getElementsByName(\"tt1\");\n");
		sb_print.append("var tt2=document.getElementsByName(\"tt2\");\n");
		sb_print.append("var tt3=document.getElementsByName(\"tt3\");\n");
		sb_print.append("var b = document.getElementsByName(\"con\");\n");
		sb_print.append("for(var i =0;i<tt1.length;i++){\n");
		sb_print.append("tt1[i].style.fontSize = (size+20) +'px';\n");
		sb_print.append("}\n");
		sb_print.append("for(var i =0;i<tt2.length;i++){\n");
		sb_print.append("tt2[i].style.fontSize = (size+18) + 'px';\n");
		sb_print.append("}\n");
		sb_print.append("for(var i =0;i<tt3.length;i++){\n");
		sb_print.append("tt3[i].style.fontSize = (size+12) + 'px';\n");
		sb_print.append("}}\n");
		sb_print.append("</script>\n");

		return sb_print.toString();
	}

	public String getJumpTop() {
		StringBuffer sb_str = new StringBuffer();
		sb_str.append("<div style=\"float:right;padding-left:98px\">");
		sb_str.append("<a class=\"noprint\"  id=\"top-link\" href=\"javascript:scroll(0,0)\">↑Top</a>");
		sb_str.append("</div>");
		return sb_str.toString();
	}

	public String getMeta() {
		StringBuffer sb_meta = new StringBuffer();
		sb_meta.append("<META http-equiv=\"X-UA-Compatible\" content=\"IE=EmulateIE7\" />\n");
		sb_meta.append("<META http-equiv=Content-Type content=\"text/html; charset=gb2312\">\n");
		return sb_meta.toString();
	}

	public String getDocType() {
		return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n";
	}

	/*
	 * 屏蔽 ctrl+p的快捷打印按钮[王雷提出]。以后所有屏蔽按钮都再这里实现 [2012-5-21郝明]
	 */
	public String pbShortcutKeyJS() {
		StringBuffer js = new StringBuffer();
		js.append("<script>");
		js.append("function KeyDown(){");
		js.append("if((window.event.ctrlKey)&& (window.event.keyCode==80)){");
		js.append("alert(\"禁止打印!\");return false;");
		js.append("}");
		js.append("};document.onkeydown = KeyDown");
		js.append("</script>");
		return js.toString();
	}
}
