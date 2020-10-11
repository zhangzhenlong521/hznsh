package cn.com.infostrategy.bs.sysapp.login;

import java.io.FileOutputStream;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WebCallBeanIfc;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * 2016-1-28 主页面新闻打开页面！
 * @author  yuanjiangxiao 
 * 
 */
public class NewsPrimaryKeyFileShow implements WebCallBeanIfc {
	private String table_main;
	private String imagefileDirectory;

	public String getHtmlContent(HashMap _map) throws Exception {
		String sid = (String) _map.get("id");//ID
		table_main = (String) _map.get("table_main");//表名称
		String url = (String) _map.get("url");//其实可以不用要这个参数
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<html>\n");
		sb_html.append(getMeta());
		sb_html.append("<head><title></title>\r\n");
		sb_html.append(getStyle());
		sb_html.append(getJs());
		sb_html.append("</head>\r\n");

		sb_html.append("<body>");
		sb_html.append("<div style=\"text-align:right\">");
//		sb_html.append(getFontChangeSize());
		sb_html.append(getPrintDiv());
		sb_html.append("</div>");
		sb_html.append("<div style=\"margin:20 40;\">");
		sb_html.append("<b class=\"b1\"></b><b class=\"b2 d1\"></b><b class=\"b3 d1\"></b><b class=\"b4 d1\"></b>");
		sb_html.append("<div class=\"b d1 k\">");
		sb_html.append("<div id=\"content\" class=\"content\">\r\n");
		String sr_content = "";
		try {
			HashVO[] mainVO = new CommDMO().getHashVoArrayByDS(null, "select * from  " + table_main + "   where  id=" + sid);// table_main_file
			if (mainVO != null && mainVO.length > 0) {
				String title = mainVO[0].getStringValue("title");//标题
				String createdate = getCreateDate(mainVO[0].getStringValue("createtime"));//创建时间
				String creater = mainVO[0].getStringValue("creater");//创建人
				String image = mainVO[0].getStringValue("image");//图片
				sr_content = mainVO[0].getStringValue("contentmsg"); //正文
				String attachfile = mainVO[0].getStringValue("attachfile"); //附件
				sb_html.append("<h1 align=\"center\">" + title + "</h1>\r\n");
				sb_html.append("<p class=\"time\">创建日期：" + createdate + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;创建人：" + creater + "</p>\r\n");
				if (image != null && !image.equals("")) {
					//线获取图片编码
					String img64code = getImage64Code(image);
					//在本地图片目录生成图片
					imagefileDirectory = ServerEnvironment.getProperty("WLTUPLOADFILEDIR");
					if (imagefileDirectory == null || imagefileDirectory.equals("")) {
						imagefileDirectory = "C:/WebPushTemp";
					}
					imagefileDirectory = imagefileDirectory + "/image/";
					java.io.File fileDir = new java.io.File(imagefileDirectory);
					if (!fileDir.exists()) {
						fileDir.mkdir();
					}
					imagefileDirectory = imagefileDirectory + image + ".jpg"; //都存储在本地默认目录的image目录下
					//下载图片到本地 每次都下载，如果有重名则覆盖
					TBUtil tbutil = new TBUtil().getTBUtil();
					byte[] bytes = tbutil.convert64CodeToBytes(img64code);
					tbutil.writeBytesToOutputStream(new FileOutputStream(imagefileDirectory), bytes); //写文件!!
					sb_html.append("<div align=\"center\"><img  src=" + imagefileDirectory + " width=\"400\" height=\"200\"/></div>");//稍微放大
				}
				if (sr_content != null) { //同一个条目下的内容，如果换行需要每段首行空两格！P标签用样式控制。
					String contents[] = sr_content.split("\n");
					StringBuffer sb = new StringBuffer();
					for (int j = 0; j < contents.length; j++) {
						sb.append("<p class=\"text\">&nbsp;&nbsp;");//第一行需要空两个格子
						sb.append(contents[j]);
						sb.append("</p>");
					}
					sb.append("\r\n");
					sr_content = sb.toString();
				}

				if (sr_content != null && !sr_content.trim().equals("")) {
					sb_html.append(sr_content);
				}
				if (attachfile != null) {
					sb_html.append("<p class=\"text\">附件：" + getHtmlFileHref(attachfile, url));
					sb_html.append("</p>\r\n");
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
		//System.out.println("sb_html============" + sb_html);
		return sb_html.toString();
	}

	private String getCreateDate(String stringValue) {
		if (stringValue == null || stringValue.equals("")) {
			return "";
		}
		return stringValue.substring(0, 10);//2016-01-22
	}

	private String getHtmlFileHref(String realfiles, String url) {
		StringBuffer returnStr = new StringBuffer("");
		if (realfiles != null && !"".equals(realfiles)) {
			String[] realfilenames = realfiles.split(";");
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
		sb.append("body {\n");
		sb.append("    background: #fff;font-family: \"微软雅黑\",MicrosoftYaHei,\"宋体\";\n");
		sb.append("}\n");
		sb.append("p {TEXT-INDENT: 2em;line-height:150%}\n");
		sb.append("a{color:blue;text-decoration: underline;font-size:12px;font-family:\"宋体\",arial;}\n");
		sb.append("a:hover{color: red;text-decoration: underline;}\n");
		sb.append(" .content {width:100%;height:100%;border:0px solid #ccc;padding:20px 20px 20px 20px;margin:0 auto;\n");
		sb.append("}\n");
		sb.append(".time {FONT-SIZE::16px;color:#999}\n");
		sb.append(".text{margin:25px 0;font-size:14px;line-height:24px;text-indent:28px;color:#333}");
		sb.append("#top-link {PADDING-RIGHT:10px; PADDING-LEFT:10px;FONT-WEIGHT:bold; RIGHT:5px;PADDING-BOTTOM:10px;COLOR:green;BOTTOM:5px;PADDING-TOP:10px;POSITION:fixed;" + "TEXT-DECORATION:none}\n");
		sb.append(".b1,.b2,.b3,.b4,.b1b,.b2b,.b3b,.b4b,.b{display:block;overflow:hidden;}\n");
		sb.append(".b1,.b2,.b3,.b1b,.b2b,.b3b{height:1px;}\n");
		sb.append(".b2,.b3,.b4,.b2b,.b3b,.b4b,.b{border-left:1px solid #999;border-right:1px solid #999;}\n");
		sb.append(".b1,.b1b{margin:0 5px;background:#999;}\n");
		sb.append(".b2,.b2b{margin:0 3px;border-width:2px;}\n");
		sb.append(".b3,.b3b{margin:0 2px;}\n");
		sb.append(".b4,.b4b{height:2px;margin:0 1px;}\n");
		sb.append(".d1{background:#F7F8F9;}\n");
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

	private String getImage64Code(String _batid) throws Exception {
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_imgupload where batchid='" + _batid + "' order by seq"); //
		StringBuilder sb_64code = new StringBuilder(); //
		String str_item = null; //
		for (int i = 0; i < hvs.length; i++) { //遍历!
			for (int j = 0; j < 10; j++) {
				str_item = hvs[i].getStringValue("img" + j); //
				if (str_item != null && !str_item.equals("")) { //如果有值
					sb_64code.append(str_item.trim()); //拼接起来!!
				} else { //如果值为空
					break; //中断退出!!!因为只可能是最后一行有零头,所以只需要内循环中断即可!!
				}
			}
		}
		return sb_64code.toString(); //
	}
}
