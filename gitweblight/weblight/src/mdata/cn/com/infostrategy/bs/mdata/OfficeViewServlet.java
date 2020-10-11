package cn.com.infostrategy.bs.mdata;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WebCallIDFactory;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;

/**
 * 使用第三方控件查看Office文件!!
 * 目前可支持金格与千航两家!!对平台来说访问的路径是一样的,在weblight.xml定义参数OFFICEACTIVEXTYPE指定是什么厂家的控件.
 * 但服务器端处理数据的类却是不一样的!
 * 
 * @author xch
 * 
 */
public class OfficeViewServlet extends HttpServlet {

	private static final long serialVersionUID = 8377535581987772546L;

	@Override
	protected void service(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		try {
			if (_request.getParameter("innerframe") != null) {
				_response.setContentType("text/html"); //
				_response.setCharacterEncoding("gb2312"); //
				PrintWriter out = _response.getWriter(); //
				out.println(getGoldGridDiv(_request.getParameter("innerframe"))); // 假层...
				return; //
			}
			boolean bo_ifhasfloat = false; // 是否要用浮动层挡住.
			String str_requestOfficeTypePar = _request.getParameter("OFFICEACTIVEXTYPE"); // 先从传过来的参数取,有两种值,一种是GOLDGRID,一种是NTKO
			if (str_requestOfficeTypePar != null) { // 如果从URL中传过来了参数!!!
				String str_isfloat = _request.getParameter("ISFLOAT"); //
				bo_ifhasfloat = ((str_isfloat == null || str_isfloat.equals("N")) ? false : true); //
				if (str_requestOfficeTypePar.equalsIgnoreCase("GOLDGRID")) { // 如果是金格
					deal_GoldGrid(_request, _response, bo_ifhasfloat);
				} else if (str_requestOfficeTypePar.equalsIgnoreCase("NTKO")) { //
					deal_NTKO(_request, _response, bo_ifhasfloat);
				} else if (str_requestOfficeTypePar.equalsIgnoreCase("DOWNLOAD")) {
					// 不用控件，直接下载
					deal_download(_request, _response, bo_ifhasfloat);
				} else {
					_response.setContentType("text/html"); //
					_response.setCharacterEncoding("gb2312"); //
					PrintWriter out = _response.getWriter(); //
					out.println("直接从URL中传过了未知的OFFICEACTIVEXTYPE[" + str_requestOfficeTypePar + "]!!"); //
					return;
				}
			} else { // 如果没有指定,则从系统参数取.
				String str_officeActivexType = ServerEnvironment.getProperty("OFFICEACTIVEXTYPE"); //
				if (str_officeActivexType == null) { // 如果控件为空!!
					_response.setContentType("text/html"); //
					_response.setCharacterEncoding("gb2312"); //
					PrintWriter out = _response.getWriter(); //
					out.println("必须在平台配置文件中指定 OFFICEACTIVEXTYPE ,即Office控件提供商!!"); //
				} else {
					if (str_officeActivexType.length() > 2) {
						bo_ifhasfloat = true; //
					}

					if (str_officeActivexType.indexOf("金格") == 0) { // 金格
						deal_GoldGrid(_request, _response, bo_ifhasfloat);
					} else if (str_officeActivexType.indexOf("千航") == 0) { // 千航
						deal_NTKO(_request, _response, bo_ifhasfloat);
					} else {
						_response.setContentType("text/html"); //
						_response.setCharacterEncoding("gb2312"); //
						PrintWriter out = _response.getWriter(); //
						out.println("在平台配置文件中指定了未知的Office控件提供商[" + str_officeActivexType + "]!!"); //
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			e.printStackTrace(new PrintWriter(bos, true));
			byte exbytes[] = bos.toByteArray();
			bos.close();
			String sb_exstack = new String(exbytes, "GBK");
			sb_exstack = (new TBUtil()).replaceAll(sb_exstack, "\r", "<br>");
			StringBuffer sb_exception = new StringBuffer();
			sb_exception.append("<html>\r\n");
			sb_exception.append("<body>\r\n");
			sb_exception.append("<font size=2 color=\"red\">\r\n");
			sb_exception.append(sb_exstack);
			sb_exception.append("</font>\r\n");
			sb_exception.append("</body>\r\n");
			sb_exception.append("</html>\r\n");
			_response.setContentType("text/html"); //
			_response.setCharacterEncoding("gb2312"); //
			PrintWriter out = _response.getWriter(); //
			out.println(sb_exception.toString()); //
		}
	}

	/**
	 * 江西金格控件的处理逻辑...
	 * 
	 * @param request
	 * @param response
	 * @param bo_ifhasfloat
	 * @throws ServletException
	 * @throws IOException
	 */

	// 差一个按钮权限配置 即：是否能编辑
	private void deal_GoldGrid(HttpServletRequest request, HttpServletResponse response, boolean _ifhasfloat) throws ServletException, IOException {
		response.setContentType("text/html"); //
		response.setCharacterEncoding("gb2312"); //
		PrintWriter out = response.getWriter(); //
		if (request.getParameter("innerframe") != null) {
			out.println("<html>\r\n"); //
			out.println("<body bgcolor=\"#A6CAF0\" topmargin=0 leftmargin=0 rightmargin=0 bottommargin=0 marginwidth=0 marginheight=0>\r\n"); //
			out.println("<font size=3 color=\"#FFFFFF\">Office [Word/Excel/WPS]</font>\r\n");
			out.println("<body>\r\n");
			out.println("</html>\r\n");
			return; //
		}

		// 自动获取OfficeServer和OCX文件完整URL路径
		String mRecordID = new String(request.getParameter("RecordID").getBytes("ISO-8859-1"), "GBK"); // 文件的唯一标识!
		String mFileType = "." + request.getParameter("filetype"); // 文件类型
		String templetfilename = request.getParameter("templetfilename"); // 模板文件名
		String sessionId = request.getParameter("sessionid"); // sessionID,用于生成书签替换

		String mHttpUrlName = request.getRequestURI();
		String mScriptName = request.getServletPath(); //
		String mServerUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + mHttpUrlName.substring(0, mHttpUrlName.lastIndexOf(mScriptName)) + "/OfficeServerServlet_GoldGrid";// 取得OfficeServer文件的完整URL
		String mUserName = "Push World";

		OfficeCompentControlVO controlvo = null; //
		boolean isEditSaveAsEditable = true; // liuxuanfei
		boolean bo_editable = true; //
		boolean bo_printable = true; //
		String str_subdir = null; //
		HashMap bookMarkMap = null; //
		if (sessionId != null) {
			controlvo = WebCallIDFactory.getInstance().getOfficeCallParMap(sessionId); // 在服务器端取得session..
			if (controlvo != null) {
				bo_editable = controlvo.isEditable(); //
				bo_printable = controlvo.isPrintable(); //
				str_subdir = controlvo.getSubdir(); //
				isEditSaveAsEditable = controlvo.isEditSaveAsEditable(); // liuxuanfei
				if (controlvo.getBookmarkValue() != null) {
					bookMarkMap = (HashMap) controlvo.getBookmarkValue().clone(); // 取得书签,一定要克隆一下,因为下面马上就从session中删除了!
				}
			}
			WebCallIDFactory.getInstance().clearOfficeCallSession(sessionId); // 清空会话
		}
		if (str_subdir != null && !"".equals(str_subdir)) {
			mServerUrl = mServerUrl + "?subdir=" + str_subdir;
		}
		String mFileName = mRecordID + mFileType; // 取得完整的文档名称
		out.write(getHtmlHeader()); //
		out.write("<script language=javascript>\r\n");

		out.write("//作用:加载插件\r\n");
		out.write("function load(){\r\n");

		out.write("  try{\r\n");
		out.write("    //以下属性必须设置,实始化iWebOffice\r\n");
		out.write("    webform.WebOffice.WebUrl=\"" + mServerUrl + "\";  //服务器交换数据包的路径,最重要,一点不能错.\r\n"); // WebUrl:系统服务器路径，与服务器文件交互操作，如保存、打开文档，重要文件
		out.write("    webform.WebOffice.RecordID=\"" + mRecordID + "\";  //唯一标识\r\n"); // RecordID:本文档记录编号
		out.write("    webform.WebOffice.Template=\"" + (templetfilename == null ? "" : templetfilename) + "\";  //模板名\r\n"); // 模板编号
		out.write("    webform.WebOffice.FileType=\"" + mFileType + "\";  //文件类型,有.doc .docx .xls .wps\r\n"); // 文档类型
		out.write("    webform.WebOffice.Filepath=\"C:/\";  //文件类型,有.doc .xls .wps\r\n"); // 文档类型

		// .doc
		// .xls
		// .wps
		out.write("    webform.WebOffice.FileName=\"" + mFileName + "\";  //文件名\r\n"); // 文件名
		out.write("    webform.WebOffice.UserName=\"" + mUserName + "\";  //用户名,保留痕迹时需要\r\n"); // 操作用户名,痕迹保留需要,可能需要使用系统登录用户名
		out.write("    webform.WebOffice.EditType=\"0\";  //编辑类型\r\n"); // 编辑类型
		// 方式一、方式二
		// ,第一位可以为0,1,2,3
		// 其中:0不可编辑;1可以编辑,无痕迹;2可以编辑,有痕迹,不能修订;3可以编辑,有痕迹,能修订.第二位可以为0,1
		// 其中:0不可批注,1可以批注。可以参考iWebOffice2006的EditType属性
		out.write("    webform.WebOffice.MaxFileSize = 64 * 1024;\r\n"); // 最大的文档大小控制，默认是64M，现在设置成16M
		out.write("\r\n");
		out.write("    //以下属性可以不要\r\n");
		out.write("    webform.WebOffice.Language=\"CH\";\r\n"); // Language:多语言支持显示选择
		// CH 简体
		// TW繁体
		// EN英文\r\n
		out.write("    webform.WebOffice.PenColor=\"#FF0000\"; //PenColor:默认批注颜色\r\n");
		out.write("    webform.WebOffice.PenWidth=\"1\";  //PenWidth:默认批注笔宽\r\n");
		out.write("    webform.WebOffice.Print=\"0\";  //Print:默认是否可以打印:1可以打印批注,0不可以打印批注\r\n");
		out.write("    webform.WebOffice.ShowToolBar=\"0\";  //ShowToolBar:是否显示工具栏:1显示,0不显示\r\n");
		out.write("    webform.WebOffice.ShowMenu=\"1\"; //是否显示菜单\r\n");
		out.write("\r\n");
		out.write("    webform.WebOffice.WebOpen();  //打开远程文档,根据上面定义的参数,打开服务器端文件,将会调用服务器端OPTION=\"LOADFILE\"项,OPTION=\"LOADTEMPLATE\"\r\n");
		out.write("    webform.WebOffice.WebToolsEnable(\"Standard\",2521," + bo_printable + "); //屏蔽打印按钮,这个方法很特别\r\n");
		// out.write(" webform.WebOffice.EditType=\"3,1\"; //显示类型 \r\n"); //
		// //加上这句就显示批注
		out.write("    webform.WebOffice.ShowType=\"2,1\"; //显示类型 \r\n"); // //文档显示方式
		// 1:表示文字批注
		// 2:表示手写批注
		// 0:表示文档核稿
		out.write("    webform.WebOffice.WebSetProtect(" + (!bo_editable) + ",\"\");   //是否保护文档,非常关键,有时需要控制只能显示,不能拷贝. \r\n"); // 是否保护

		out.write("\r\n");
		out.write("    //输出书签替换,可以有多个,是根据sessionID从服务器端内存中取出循环输出!\r\n");

		if (bookMarkMap != null) {
			String[] str_keys = (String[]) bookMarkMap.keySet().toArray(new String[0]); //
			for (int i = 0; i < str_keys.length; i++) {
				String str_value = (String) bookMarkMap.get(str_keys[i]); //
				if (str_value != null) {
					out.write("    webform.WebOffice.WebSetBookMarks('" + str_keys[i] + "','" + str_value + "');\r\n");
				} else {
					out.write("    //webform.WebOffice.WebSetBookMarks('" + str_keys[i] + "','" + str_value + "');\r\n");
				}
			}
		}

		out.write("  }catch(e){  \r\n");
		out.write("    alert(e.description);  //显示出错误信息 \r\n");
		out.write("  }\r\n");
		out.write("}\r\n");
		out.write("\r\n");

		out.write("//作用:退出iWebOffice\r\n");
		out.write("function unLoad(){\r\n");
		out.write("  try{\r\n");
		out.write("  if (!webform.WebOffice.WebClose()){\r\n");
		out.write("  }else{\r\n");
		out.write("  }\r\n");
		out.write("  }catch(e){alert(e.description);}\r\n");
		out.write("}\r\n");
		out.write("\r\n");

		out.write("//作用:显示批注\r\n");
		out.write("function showComment(){\r\n");
		out.write("  try{\r\n");
		out.write(" webform.WebOffice.webSetProtect(false,\"\");\r\n");
		out.write("  webform.WebOffice.EditType=\"3,1\";\r\n");
		out.write("  webform.WebOffice.ShowType=\"2,1\";\r\n");
		out.write("  }catch(e){alert(e.description);}\r\n");
		out.write("}\r\n");
		out.write("\r\n");

		out.write("//作用:隐藏批注\r\n");
		out.write("function hideComment(){\r\n");
		out.write("  try{\r\n");
		out.write("webform.WebOffice.webSetProtect(false,\"\");\r\n");
		out.write("  webform.WebOffice.EditType=\"1,1\";\r\n");
		out.write("  webform.WebOffice.ShowType=\"2,1\";\r\n");
		out.write("  }catch(e){alert(e.description);}\r\n");
		out.write("}\r\n");
		out.write("\r\n");

		out.write("//作用:保存文件至远程服务器\r\n");
		out.write("function saveFileToRemoteServer(){\r\n");
		out.write("  if(webform.WebOffice.WebSave()){ \r\n");
		out.write("     alert('保存数据至服务器成功!');  \r\n");
		out.write("  }\r\n");
		out.write("}\r\n");
		out.write("\r\n");

		out.write("//作用:插入图片\r\n");
		out.write("function insertPicture(){\r\n");
		out.write("  try{\r\n");
		out.write("webform.WebOffice.WebInsertImage(\"\",'082.jpg',true,5) ;  \r\n");
		out.write("  }catch(e){alert(e.description);}\r\n");
		out.write("}\r\n");
		out.write("\r\n");

		out.write("//作用:加入水印\r\n");// 如果需要图片水印，调用WaterMarkPicture
		out.write("function addWater(){\r\n");
		out.write("  try{\r\n");
		out.write("  webform.WebOffice.webSetProtect(false,\"\");\r\n");
		out.write(" var WordObject=webform.WebOffice.WebObject; \r\n");
		out.write(" var n=WordObject.Application.ActiveDocument.PageSetup.DifferentFirstPageHeaderFooter;   \r\n");
		out.write(" if(n==\"9999999\"){\r\n");// 含有分节符的文档
		out.write(" WordObject.Application.ActiveWindow.ActivePane.View.SeekView = 2;  \r\n");
		out.write(" WaterMark(WordObject); \r\n");// 添加文字水印
		out.write(" WordObject.Application.ActiveDocument.Sections(3).Range.Select();\r\n");
		out.write(" WordObject.Application.ActiveWindow.ActivePane.View.SeekView = 9;\r\n");
		out.write(" WaterMark(WordObject); \r\n");
		out.write(" WordObject.Application.ActiveWindow.ActivePane.VerticalPercentScrolled = 2 ;\r\n");
		out.write(" }else if(n==\"0\"){  \r\n");// 含有分栏分页及正常的文档
		out.write(" WordObject.Application.ActiveWindow.ActivePane.View.SeekView = 9; \r\n");
		out.write(" WaterMark(WordObject);\r\n");
		out.write(" }else if(n==\"-1\"){ \r\n");// 含有分隔符
		out.write(" WordObject.Application.ActiveWindow.ActivePane.View.SeekView = 2; \r\n");
		out.write("  WaterMark(WordObject);");
		out.write("  WordObject.Application.ActiveWindow.ActivePane.View.SeekView = 1;\r\n ");
		out.write("  WaterMark(WordObject);  \r\n");
		out.write(" }\r\n");
		out.write("  }catch(e){alert(e.description);}\r\n");
		out.write("}\r\n");
		out.write("\r\n");

		// 加文字水印
		out.write("function WaterMark(WordObject){\r\n");
		out.write("  try{\r\n");
		out.write("WordObject.Application.Selection.HeaderFooter.Shapes.AddTextEffect(0, \"公司绝密\", \"宋体\", 1, false, false, 0, 0).Select();\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.TextEffect.NormalizedHeight = false;\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.Line.Visible = false;\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.Fill.Visible = true;\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.Fill.Solid();\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.Fill.ForeColor.RGB = 10444703;\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.Fill.Transparency = 0;\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.Rotation = 315;\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.LockAspectRatio=true;\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.Height= WordObject.Application.CentimetersToPoints(4.92);\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.Width=WordObject.Application.CentimetersToPoints(19.69);\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.WrapFormat.AllowOverlap = true;\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.WrapFormat.Side=3;\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.WrapFormat.Type=3;\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.RelativeHorizontalPosition = 0;\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.RelativeVerticalPosition = 0;\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.Left = -999995;\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.Top = -999995;\r\n");
		out.write("WordObject.Application.ActiveWindow.ActivePane.View.SeekView = 0;\r\n");
		out.write("  }catch(e){alert(e.description);}\r\n");
		out.write("}\r\n");
		out.write("\r\n");

		// 加图片水印 传入一个绝对路径的图片
		out.write("function WaterMarkPicture(WordObject){\r\n");
		out.write("  try{\r\n");
		out.write("WordObject.Application.Selection.HeaderFooter.Shapes.AddPicture('C:/11.jpg',false,true).Select();\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.PictureFormat.Brightness = 0.15;\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.PictureFormat.Contrast = 0.15;\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.LockAspectRatio =true;\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.Height=WordObject.Application.CentimetersToPoints(10.98);\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.Width=WordObject.Application.CentimetersToPoints(14.65);\r\n");
		out.write("WordObject.Application.Selection.ShapeRange.WrapFormat.AllowOverlap = true; \r\n");
		out.write("WordObject.Application.Selection.ShapeRange.WrapFormat.Side=true; \r\n");
		out.write("WordObject.Application.Selection.ShapeRange.WrapFormat.Type=3;   \r\n");
		out.write("WordObject.Application.Selection.ShapeRange.RelativeHorizontalPosition = 0;  \r\n");
		out.write("WordObject.Application.Selection.ShapeRange.RelativeVerticalPosition = 0;  \r\n");
		out.write("WordObject.Application.Selection.ShapeRange.Left = -999995;   \r\n");
		out.write("WordObject.Application.Selection.ShapeRange.Top = -999995;  \r\n");
		out.write("WordObject.Application.ActiveWindow.ActivePane.View.SeekView = 0;  \r\n");
		out.write("  }catch(e){alert(e.description);}\r\n");
		out.write("}\r\n");
		out.write("\r\n");

		out.write("function swingCall(_type){\r\n");
		out.write("  if(_type=='closedoc'){\r\n");
		out.write("  unLoad();\r\n");
		out.write("  }\r\n");
		out.write("}\r\n");

		out.write("</script>\r\n");
		out.write("\r\n");
		out.write("</head>\r\n");
		out.write("<body bgcolor=\"#ffffff\" topmargin=5 leftmargin=5 rightmargin=0 bottommargin=0 marginwidth=0 marginheight=0 onload=\"load()\" onunload=\"unLoad()\">  <!--引导和退出iWebOffice-->\r\n");

		out.write("<form name=\"webform\" method=\"post\" action=\"\"> <!--保存iWebOffice后提交表单信息-->\r\n");

		out.write("<input type=\"button\" name=\"button\" class=\"btn_style\" onclick=\"JavaScript:saveFileToRemoteServer();\"  value=\"保存\" " + (isEditSaveAsEditable ? (bo_editable ? "" : " disabled") : (bo_editable ? " disabled" : "")) + ">\r\n");

		// out.write("<input type=\"button\" name=\"button\" class=\"btn_style\"
		// onclick=\"JavaScript:showComment();\" value=\"显示批注\">\r\n");
		// out.write("<input type=\"button\" name=\"button\" class=\"btn_style\"
		// onclick=\"JavaScript:hideComment();\" value=\"隐藏批注\">\r\n");
		// out.write("<input type=\"button\" name=\"button\" class=\"btn_style\"
		// onclick=\"JavaScript:insertPicture();\" value=\"插入图片\" " +
		// (bo_editable ? "" : " disabled") + ">\r\n");
		// out.write("<input type=\"button\" name=\"button\" class=\"btn_style\"
		// onclick=\"JavaScript:addWater();\" value=\"加入水印\" " + (!bo_editable ?
		// "" : " disabled") + ">\r\n");
		out.write("<input type=\"hidden\" name=\"RecordID\" value=\"" + mRecordID + "\"><br>\r\n");

		out.write("<object id=\"WebOffice\" width=\"99%\" height=\"628\" classid=\"clsid:8B23EA28-723C-402F-92C4-59BE0E063499\" codebase=\"./applet/iWebOffice2006.cab#version=7,6,0,0\"></object>\r\n");
		out.write("</form>\r\n");
		if (_ifhasfloat) {
			out.write("<div id=\"iframediv\" style=\"position:absolute;left:7;top:25;width:500;\"><iframe id=\"iframe1\" src=\"./OfficeViewServlet?innerframe=GOLDGRID\" frameborder=\"0\" width=500 height=19></iframe></div>\r\n");
		}
		out.write("</body>\r\n");
		out.write("</html>\r\n");
	}

	/**
	 * 重庆千航控件的处理逻辑...
	 * 
	 * @param request
	 * @param response
	 * @param _ifhasfloat
	 * @throws ServletException
	 * @throws IOException
	 */
	private void deal_NTKO(HttpServletRequest request, HttpServletResponse response, boolean _ifhasfloat) throws ServletException, IOException {

		synchronized (this) {
			request.setCharacterEncoding("GBK");
			response.setContentType("text/html"); //
			response.setCharacterEncoding("gb2312"); //
			PrintWriter out = response.getWriter(); //		
			// office 控件机构选择
			// 默认是民生银行
			TBUtil tbUtil = new TBUtil();

			// 自动获取OfficeServer和OCX文件完整URL路径
			String mRecordID = new String(request.getParameter("RecordID").getBytes("ISO-8859-1"), "GBK"); // 文件的唯一标识!
			String mFileType = request.getParameter("filetype"); // 文件类型
			String templetfilename = request.getParameter("templetfilename"); // 模板文件名
			String sessionId = request.getParameter("sessionid"); // sessionID,用于生成书签替换
			String fromClientDir = request.getParameter("fromclientdir"); // 是否从客户端生成页面
			String str_saveasable = request.getParameter("bo_saveasable");
			String mHttpUrlName = request.getRequestURI(); //
			String mScriptName = request.getServletPath(); //
			String transpro = System.getProperty("transpro");
			if (transpro == null || "".equals(transpro.trim())) {
				transpro = "http";
			}
			String mServerUrl1 = transpro + "://" + request.getServerName() + ":" + request.getServerPort() + mHttpUrlName.substring(0, mHttpUrlName.lastIndexOf(mScriptName)) + "/";
			String uploadfiledir = null;
			String mServerUrl = transpro + "://" + request.getServerName() + ":" + request.getServerPort() + mHttpUrlName.substring(0, mHttpUrlName.lastIndexOf(mScriptName)) + "/OfficeServerServlet_NTKO";// 取得OfficeServer文件的完整URL

			if (System.getProperty("CALLURL") != null && !"".equals(System.getProperty("CALLURL"))) {
				mServerUrl1 = System.getProperty("CALLURL") + "/";
				mServerUrl = mServerUrl1 + "OfficeServerServlet_NTKO";
			}

			String textwater = request.getParameter("textwater");
			String picwater = request.getParameter("picwater");
			String picposition = request.getParameter("picposition");
			String fromtype = request.getParameter("fromtype");//如果有水印，水印图片是否存在db（数据库）中
			String isHtml = (String) request.getAttribute("isHtml"); //是否是HTML的链接打开的
			if (isHtml == null) {//直接传一个参数就可以只读之前就有这个参数加了一个判断/sunfujun/20130107邮储
				isHtml = request.getParameter("isHtml");
			}
			String str_subdir = request.getParameter("subdir");//加个参数就可以读upload目录下的文件/sunfujun/20130107邮储
			OfficeCompentControlVO controlvo = null; //
			boolean bo_editable = true; //
			boolean bo_printable = true; //
			boolean bo_saveasable = false;//一般另存为不需要打开但有些地方特殊
			boolean bo_titlebar = true; //
			boolean bo_menubar = true;
			boolean bo_toolbar = true;
			boolean bo_menutoolbar = true;
			boolean ifshowsave = true;//是否显示保存
			boolean ifshowprint_tao = true;//是否显示套打
			boolean ifshowprint_all = true;//是否显示全打
			boolean ifshowprint_fen = true;//是否显示分打
			boolean ifshowprint = true;//是否显示打印
			boolean ifshowwater = true;//是否显示水印
			boolean ifshowshowcomment = true;//是否显示显示批注
			boolean ifshowhidecomment = true;//隐藏批注
			boolean ifshowedit = true;//修订
			boolean ifshowshowedit = true;//显示修订
			boolean ifshowhideedit = true;//隐藏修订
			boolean ifshowacceptedit = true;//接收修订
			boolean ifshowclose = true;//关闭
			boolean ifselfdesc = false;//是否特殊定义即自己写的billofficepanel
			boolean ifshowresult = true;
			boolean isEditSaveAsEditable = true; // liuxuanfei
			String encryCode = null; // liuxuanfei
			HashMap bookMarkMap = null; //
			String saveAsName = "";
			if (sessionId != null) {
				controlvo = WebCallIDFactory.getInstance().getOfficeCallParMap(sessionId); // 在服务器端取得session..
				if (controlvo != null) {
					bo_saveasable = controlvo.isSaveas();
					bo_editable = controlvo.isEditable(); //
					bo_printable = controlvo.isPrintable(); //
					str_subdir = controlvo.getSubdir(); //
					bo_titlebar = controlvo.isTitlebar();
					bo_menubar = controlvo.isMenubar();
					bo_toolbar = controlvo.isToolbar();
					bo_menutoolbar = controlvo.isMenutoolbar();

					ifshowsave = controlvo.isIfshowsave();//是否显示保存
					ifshowprint_tao = controlvo.isIfshowprint_tao();//是否显示套打
					ifshowprint_all = controlvo.isIfshowprint_all();//是否显示全打
					ifshowprint_fen = controlvo.isIfshowprint_fen();//是否显示分打
					ifshowprint = controlvo.isIfshowprint();//是否显示打印
					ifshowwater = controlvo.isIfshowwater();//是否显示水印
					ifshowshowcomment = controlvo.isIfshowshowcomment();//是否显示显示批注
					ifshowhidecomment = controlvo.isIfshowhidecomment();//隐藏批注
					ifshowedit = controlvo.isIfshowedit();//修订
					ifshowshowedit = controlvo.isIfshowshowedit();//显示修订
					ifshowhideedit = controlvo.isIfshowhideedit();//隐藏修订
					ifshowacceptedit = controlvo.isIfshowacceptedit();//接收修订
					ifshowclose = controlvo.isIfshowclose();//关闭
					ifselfdesc = controlvo.isIfselfdesc();
					isEditSaveAsEditable = controlvo.isEditSaveAsEditable(); // liuxuanfei
					encryCode = controlvo.getEncryCode(); // liuxuanfei
					ifshowresult = controlvo.ifshowresult();
					if (controlvo.getBookmarkValue() != null) {
						bookMarkMap = (HashMap) controlvo.getBookmarkValue().clone(); // 取得书签,一定要克隆一下,因为下面马上就从session中删除了!
					}
					saveAsName = controlvo.getSaveAsName();//设置另存为的默认文件名称【李春娟/2012-03-12】
				}
				WebCallIDFactory.getInstance().clearOfficeCallSession(sessionId); // 清空会话
			}
			if (isHtml != null && "Y".equals(isHtml)) {
				bo_menubar = false; //不显示菜单栏
				bo_toolbar = false; //不显示工具栏 \r\n");
				bo_menutoolbar = false; //不显示工具菜单 
				bo_editable = false;//不允许编辑,拷贝
			}

			String mFileName = mRecordID + "." + mFileType; // 取得完整的文档名称

			out.write(getHtmlHeader());
			out.write("\r\n");

			out.write("<script language=\"JavaScript\">\r\n");
			out.write("//打开页面时初始化,在body的onload事件时调用\r\n");
			out.write("function load(){\r\n");
			StringBuffer sb_openfileurl = new StringBuffer(mServerUrl + "?action=open"); // 先指定动作!!
			sb_openfileurl.append("&RecordID=" + mRecordID); //
			sb_openfileurl.append("&filetype=" + mFileType); //

			if (str_subdir != null) {
				sb_openfileurl.append("&subdir=" + str_subdir); //
			}

			if (templetfilename != null) {
				sb_openfileurl.append("&templetfilename=" + templetfilename); //
			}
			if (fromClientDir != null && !"".equals(fromClientDir)) {
				String newurl = new TBUtil().replaceAll(fromClientDir, "\\", "/");

				sb_openfileurl.append("&fromclientdir=" + newurl.substring(0, newurl.length() - 1)); //
			}

			String isAbsoluteSeverDir = request.getParameter("isAbsoluteSeverDir");
			if ("Y".equals(isAbsoluteSeverDir)) {
				sb_openfileurl.append("&isAbsoluteSeverDir=Y");
			}

			out.write("  TANGER_OCX_OBJ.OpenFromURL(\"" + sb_openfileurl.toString() + "\");  \r\n"); // 打开时自动加载文件!!
			//******************李春娟添加,显示水印效果********************//
			//	if(bo_editable){        //加上水印了肯定不可用编辑了！！
			if (textwater != null && !"".equals(textwater) && (mFileType.equalsIgnoreCase("doc") || mFileType.equalsIgnoreCase("docx") || mFileType.equalsIgnoreCase("wps"))) {
				String str_textwater = tbUtil.convertHexStringToStr(textwater);
				out.write("addWater('" + str_textwater + "'); \r\n");
			}
			if (picwater != null && !"".equals(picwater)) {
				if (picposition == null || "".equals(picposition)) {
					if (mFileType.equalsIgnoreCase("doc") || mFileType.equalsIgnoreCase("docx")) {
						if (fromtype != null && fromtype.equalsIgnoreCase("db")) {//图片是否保存在数据库中，如果是，则默认保存在上传图片表中pub_filewatermark
							out.write("addWaterPicture1('" + mServerUrl1 + "ImageServlet?fromtype=db&imgname=" + tbUtil.convertHexStringToStr(picwater) + "',330,580);\r\n");
						} else {
							out.write("addWaterPicture1('" + mServerUrl1 + "images/watermark/" + tbUtil.convertHexStringToStr(picwater) + "',330,580);\r\n");
						}
					} else if (mFileType.equalsIgnoreCase("wps")) {
						out.write("addWaterPicture('" + mServerUrl1 + "images/watermark/" + tbUtil.convertHexStringToStr(picwater) + "',330,580);\r\n");
					}

				} else {
					String[] position = picposition.split(",");
					if (position.length == 2) {
						if (mFileType.equalsIgnoreCase("doc") || mFileType.equalsIgnoreCase("docx")) {
							if (fromtype != null && fromtype.equalsIgnoreCase("db")) {//图片是否保存在数据库中，如果是，则默认保存在上传图片表中pub_filewatermark
								out.write("addWaterPicture1('" + mServerUrl1 + "ImageServlet?fromtype=db&imgname=" + tbUtil.convertHexStringToStr(picwater) + "'," + position[0] + "," + position[1] + ");\r\n");
							} else {
								out.write("addWaterPicture1('" + mServerUrl1 + "images/watermark/" + tbUtil.convertHexStringToStr(picwater) + "'," + position[0] + "," + position[1] + ");\r\n");
							}

						} else if (mFileType.equalsIgnoreCase("wps")) {
							out.write("addWaterPicture('" + mServerUrl1 + "images/watermark/" + tbUtil.convertHexStringToStr(picwater) + "'," + position[0] + "," + position[1] + ");\r\n");
						}
					} else {
						if (mFileType.equalsIgnoreCase("doc") || mFileType.equalsIgnoreCase("docx")) {
							if (fromtype != null && fromtype.equalsIgnoreCase("db")) {//图片是否保存在数据库中，如果是，则默认保存在上传图片表中pub_filewatermark
								out.write("addWaterPicture1('" + mServerUrl1 + "ImageServlet?fromtype=db&imgname=" + tbUtil.convertHexStringToStr(picwater) + "',350,600);\r\n");
							} else {
								out.write("addWaterPicture1('" + mServerUrl1 + "images/watermark/" + tbUtil.convertHexStringToStr(picwater) + "',350,600);\r\n");
							}

						} else if (mFileType.equalsIgnoreCase("wps")) {
							out.write("addWaterPicture('" + mServerUrl1 + "images/watermark/" + tbUtil.convertHexStringToStr(picwater) + "',350,600);\r\n");
						}
					}
				}
			}
			//	}		
			//************************************************//
			if (bo_titlebar) {
				out.write("  TANGER_OCX_OBJ.Titlebar=true; //显示标题栏 \r\n"); // 显示标题栏	
			} else {
				out.write("  TANGER_OCX_OBJ.Titlebar=false; //显示标题栏 \r\n"); // 显示标题栏
			}
			if (bo_menubar)
				out.write("  TANGER_OCX_OBJ.Menubar=true;  //显示菜单 \r\n"); // 显示菜单
			else
				out.write("  TANGER_OCX_OBJ.Menubar=false;  //显示菜单 \r\n"); // 显示菜单
			if (bo_toolbar)
				out.write("  TANGER_OCX_OBJ.Toolbars=true; //显示工具栏 \r\n"); // 显示工具栏
			else
				out.write("  TANGER_OCX_OBJ.Toolbars=false; //显示工具栏 \r\n"); // 显示工具栏
			if (bo_menutoolbar)
				out.write("  TANGER_OCX_OBJ.IsShowToolMenu=true;  //显示工具菜单 \r\n"); // 显示工具菜单
			else
				out.write("  TANGER_OCX_OBJ.IsShowToolMenu=false;  //显示工具菜单 \r\n"); // 显示工具菜单
			out.write("  TANGER_OCX_OBJ.FileSave=false;  //是否可以保存文档 \r\n");
			out.write("  TANGER_OCX_OBJ.FileNew=false;  //是否可以创建文档 \r\n");
			out.write("  TANGER_OCX_OBJ.FileOpen=false;  //是否可以打开文档 \r\n");
			out.write("  TANGER_OCX_OBJ.FileClose=false;  //是否可以关闭文档 \r\n");

			out.write("  TANGER_OCX_OBJ.FileSaveAs=" + bo_saveasable + ";  //是否可以另存为文档 \r\n");
			out.write("  TANGER_OCX_OBJ.FilePrint=" + (bo_printable) + ";  //是否可以打印 \r\n");
			out.write("  TANGER_OCX_OBJ.FilePrintPreview=" + (bo_printable) + ";  //是否可以打印预览 \r\n");

			out.write("\r\n");
			out.write("  //输出书签替换,可以有多个,是根据sessionID从服务器端内存中取出循环输出!\r\n");
			if (bookMarkMap != null) {
				String[] str_keys = (String[]) bookMarkMap.keySet().toArray(new String[0]); //
				for (int i = 0; i < str_keys.length; i++) {
					String str_value = (String) bookMarkMap.get(str_keys[i]); //
					if (str_value != null) {
						out.write("  TANGER_OCX_OBJ.SetBookmarkValue('" + str_keys[i] + "','" + str_value + "');\r\n");
					} else {
						out.write("  //TANGER_OCX_OBJ.SetBookmarkValue('" + str_keys[i] + "','" + str_value + "');\r\n");
					}
				}
			}

			out.write(" TANGER_OCX_OBJ.IsNoCopy=" + (!bo_editable) + "; \r\n"); // 允许拷贝
			out.write("  TANGER_OCX_OBJ.SetReadOnly (" + (!bo_editable) + ",\"\");   //只读状态 \r\n");

			//以后会加上文档系统自动加密
			// 这里为什么要加上保护, 是因为在【兴业银行全面合规管理系统-合同管理-我的示范合同文本-打印历史】中在线打印默认是只读的, 文本框也被限制不可编辑..
			// Why? 在其他在线试用里面是可以的呢? 调用的同一段代码!! 可纳闷.. 所以只好在这里自动再重新设置一遍保护即可越过只读状态, 只有文本框可编辑, 其他地方受保护!!
			if (encryCode != null && !encryCode.equals("")) {
				//先取消保护, 再进行保护.. 否则已经加过保护的, 再次直接加保护会出现问题..
				out.write("  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + encryCode + "'); \r\n");
				out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(1,true,'" + encryCode + "'); \r\n");
			}

			out.write("}\r\n\r\n");

			out.write("//保存文档,在点击保存按钮时调用.\r\n");
			out.write("function saveFileToRemoteServer(){\r\n");

			/***
			 * Gwang 2012-4-21修改
			 * SaveAsOtherFormatToURL方法用来将文档保存为其他格式的文件
			 * 但在编辑docx文件时, 保存时会报错，报错信息为：发生错误:保存指定格式到临时文件目录发生错误！
			 * 所以还是用SaveToURL方法, 毕竟docx用的机率比较高!
			 */
			//out.write("  var v_result=TANGER_OCX_OBJ.SaveAsOtherFormatToURL(5,\"" + mServerUrl + "?action=save" + (str_subdir == null ? "" : ("&subdir=" + str_subdir)) + "\",\"EDITFILE\",\"\",\"" + mFileName + "\",\"0\");\r\n");
			out.write("  var v_result=TANGER_OCX_OBJ.SaveToURL(\"" + mServerUrl + "?action=save" + (str_subdir == null ? "" : ("&subdir=" + str_subdir)) + "\",\"EDITFILE\",\"\",\"" + mFileName + "\",\"0\");\r\n");
			out.write("  document.title = \"button_save_click\";\r\n"); //
			out.write("  dealresult.value = v_result;\r\n"); //以前是弹出的,但老是容易造成窗口躲到后面去,导致卡住!!!
			out.write("}\r\n\r\n");

			out.write("//打印文档,在点击套打按钮时调用.\r\n");
			out.write("function printFileSelf(){\r\n");
			out.write("  document.title = \"button_printtao_click\";\r\n"); //
			out.write("}\r\n\r\n");

			out.write("//打印文档,在点击打印按钮时调用.\r\n");
			out.write("function printFileDirectSelf(){\r\n");
			out.write("  document.title = \"button_printdirect_click\";\r\n"); //
			out.write("}\r\n\r\n");

			out.write("//打印文档,在点击全打按钮时调用.\r\n");
			out.write("function printAllSelf(){\r\n");
			out.write("  document.title = \"button_printall_click\";\r\n"); //
			out.write("}\r\n\r\n");

			out.write("//打印文档,在点击分打按钮时调用.\r\n");
			out.write("function splitPrintSelf(){\r\n");
			out.write("  document.title = \"button_printfen_click\";\r\n"); //
			out.write("}\r\n\r\n");
			out.write("//打印文档,在点击套打按钮时调用.\r\n");
			out.write("function printFile(){\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("  TANGER_OCX_OBJ.FilePrint=true;\r\n"); // 解决屏蔽工具栏打印功能后，打印界面不能加载的问题
			out.write("  var v_result=TANGER_OCX_OBJ.PrintOut(true);\r\n"); //
			out.write("  TANGER_OCX_OBJ.FilePrint=false;\r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("}\r\n\r\n");

			out.write("//打印文档,在点击打印按钮时调用.\r\n");
			out.write("function printFileDirect(){\r\n");
			out.write("  TANGER_OCX_OBJ.FilePrint=true;\r\n"); // 解决屏蔽工具栏打印功能后，打印界面不能加载的问题
			out.write("  var v_result=TANGER_OCX_OBJ.PrintOut(true);\r\n"); //
			out.write("  TANGER_OCX_OBJ.FilePrint=false;\r\n");
			out.write("}\r\n\r\n");

			out.write("//打印文档，可获得点击关闭、确定、取消按钮的返回值 \r\n"); //yinliang add
			out.write("function printoutword(){\r\n");
			out.write("TANGER_OCX_OBJ.Activate(); var retValue ; var doctype = TANGER_OCX_OBJ.DocType; \r\n");
			out.write("if(doctype == 1){ \r\n");
			out.write("try{ retValue = TANGER_OCX_OBJ.ActiveDocument.Application.Dialogs(88).Show(); \r\n");
			out.write("}catch(err){alert(err);} \r\n");
			out.write("}else if(doctype == 2){ \r\n");
			out.write("try{retValue = TANGER_OCX_OBJ.ActiveDocument.Application.Dialogs(8).Show(); \r\n");
			out.write("}catch(ee){alert(ee);}} return retValue ;} \r\n");

			//		out.write("//打印文档,在点击打印按钮时调用,是否弹出打印窗口.\r\n");  
			//		out.write("function printFileDirect2(flag){\r\n");
			//		out.write("  TANGER_OCX_OBJ.FilePrint=true;\r\n"); // 解决屏蔽工具栏打印功能后，打印界面不能加载的问题
			//		out.write("  var v_result=TANGER_OCX_OBJ.PrintOut(flag);\r\n"); //
			//		out.write("  alert(v_result);\r\n"); //
			//		out.write("  TANGER_OCX_OBJ.FilePrint=false;\r\n");
			//		out.write("}\r\n\r\n");

			//
			out.write("//打印文档,在点击全打按钮时调用.\r\n");
			out.write("function printAll(){\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("  TANGER_OCX_OBJ.ActiveDocument.Application.Selection.Wholestory();\r\n");
			out.write("	 TANGER_OCX_OBJ.ActiveDocument.Application.Selection.Font.Color=0;\r\n");
			out.write("  TANGER_OCX_OBJ.FilePrint=true;\r\n"); // 解决屏蔽工具栏打印功能后，打印界面不能加载的问题
			out.write("  TANGER_OCX_OBJ.ActiveDocument.Application.Selection.MoveLeft();\r\n"); //
			out.write("  var v_result=TANGER_OCX_OBJ.PrintOut(true);\r\n"); //
			out.write("  TANGER_OCX_OBJ.ActiveDocument.Undo(3);\r\n"); //设置字体颜色应该算两次操作，一次选择一次改色
			out.write("  TANGER_OCX_OBJ.FilePrint=false;\r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("}\r\n\r\n");

			out.write("//打印文档,在点击分打按钮时调用.\r\n");
			out.write("function splitPrint(){\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("  TANGER_OCX_OBJ.ActiveDocument.Application.Selection.Wholestory();\r\n");
			out.write("	 TANGER_OCX_OBJ.ActiveDocument.Application.Selection.Font.Color=0;\r\n");
			out.write("  TANGER_OCX_OBJ.ActiveDocument.SelectAllEditableRanges(-1);\r\n");
			out.write("  TANGER_OCX_OBJ.ActiveDocument.Application.Selection.Font.Color=16777215;\r\n"); //
			out.write("  TANGER_OCX_OBJ.ActiveDocument.Application.Selection.Font.UnderlineColor=0; \r\n"); //
			out.write("  TANGER_OCX_OBJ.ActiveDocument.Application.Selection.MoveLeft();\r\n"); //
			out.write("  TANGER_OCX_OBJ.FilePrint=true;\r\n");
			out.write("  var v_result=TANGER_OCX_OBJ.PrintOut(true);\r\n"); //
			out.write(" TANGER_OCX_OBJ.ActiveDocument.Undo(6);\r\n"); //设置字体颜色应该算两次操作，一次选择一次改色
			out.write("  var v_result=TANGER_OCX_OBJ.PrintOut(true);\r\n"); //	b			
			out.write("  TANGER_OCX_OBJ.FilePrint=false;\r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("}\r\n\r\n");

			out.write("//打印文档,在点击全打按钮时调用.\r\n");
			out.write("function mytest(){\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("  TANGER_OCX_OBJ.ActiveDocument.Application.Selection.Wholestory();\r\n");
			out.write(" with (TANGER_OCX_OBJ.ActiveDocument.Application.Selection){\r\n");
			out.write("this.Font.NameFarEast = \"仿宋_GB2312\";\r\n");
			out.write("this.Font.NameAscii = \"\";\r\n");
			out.write("this.Font.NameOther = \"\";\r\n");
			out.write("this.Font.Name = \"\";\r\n");
			//		out.write("this.Italic = false;\r\n");
			//		out.write("this.Underline = 0;\r\n");
			//		out.write("this.UnderlineColor = 0;\r\n");
			//		out.write("this.StrikeThrough = false;\r\n");
			//		out.write("this.DoubleStrikeThrough = false;\r\n");
			//		out.write("this.Outline = false;\r\n");
			//		out.write("this.Emboss = false;\r\n");
			//		out.write("this.Shadow = false;\r\n");
			//		out.write("this.Hidden = false;\r\n");
			//		out.write("this.SmallCaps = false;\r\n");
			//		out.write("this.AllCaps = false;\r\n");
			//		out.write("this.Color = 16777215;\r\n");
			//		out.write("this.Engrave = false;\r\n");
			//		out.write("this.Superscript = false;\r\n");
			//		out.write("this.Subscript = false;\r\n");
			//		out.write("this.Spacing = 0;\r\n");
			//		out.write("this.Scaling = 100;\r\n");
			//		out.write("this.Position = 0;\r\n");
			//		out.write("this.Animation = 0;\r\n");
			//        out.write("this.DisableCharacterSpaceGrid = false;\r\n");
			//        out.write("this.EmphasisMark = 0;\r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("}}\r\n");

			out.write("//作用:显示批注\r\n");
			out.write("function showComment(){\r\n");
			out.write("  try{\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("  TANGER_OCX_OBJ.ActiveDocument.TrackRevisions=true;\r\n");
			out.write("  TANGER_OCX_OBJ.ActiveDocument.ShowRevisions=true;\r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n");
			out.write("\r\n");

			out.write("//作用:隐藏批注\r\n");
			out.write("function hideComment(){\r\n");
			out.write("  try{\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("  TANGER_OCX_OBJ.ActiveDocument.TrackRevisions=false;\r\n");
			out.write("  TANGER_OCX_OBJ.ActiveDocument.ShowRevisions=false;\r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n");
			out.write("\r\n");

			out.write("//作用:修订，痕迹保留\r\n");
			out.write("function edit(){\r\n");
			out.write("  try{\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("	TANGER_OCX_OBJ.ActiveDocument.TrackRevisions = true;\r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n");
			out.write("\r\n");

			out.write("//作用:显示痕迹\r\n");
			out.write("function showEdit(){\r\n");
			out.write("  try{\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("	TANGER_OCX_OBJ.ActiveDocument.ShowRevisions=true;   \r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n");
			out.write("\r\n");

			out.write("//作用:隐藏痕迹\r\n");
			out.write("function hideEdit(){\r\n");
			out.write("  try{\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("	TANGER_OCX_OBJ.ActiveDocument.ShowRevisions=false;   \r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n");
			out.write("\r\n");

			out.write("//作用:接受修订\r\n");
			out.write("function acceptEdit(){\r\n");
			out.write("  try{\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("	TANGER_OCX_OBJ.ActiveDocument.AcceptAllRevisions();   \r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n");
			out.write("\r\n");

			out.write("//加文字水印,在点击水印按钮时调用.\r\n");
			out.write("function addWater(text){\r\n");
			out.write(" try{\r\n"); //
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");// 取消文件保护状态
			out.write(" var ActiveDocument = TANGER_OCX_OBJ.ActiveDocument;\r\n"); //
			out.write(" for(var i=1;i<=ActiveDocument.Sections.Count;i++){\r\n");
			out.write(" ActiveDocument.Sections(i).Range.Select();\r\n"); //
			out.write(" ActiveDocument.ActiveWindow.ActivePane.View.SeekView = 9;\r\n"); //
			out.write(" var Selection = ActiveDocument.Application.Selection;\r\n"); //
			out.write(" Selection.HeaderFooter.Shapes.AddTextEffect(0,  text, \"宋体\", 1, false, false, 0, 0).Select();\r\n"); //
			out.write(" Selection.ShapeRange.TextEffect.NormalizedHeight = false;\r\n"); //
			out.write(" Selection.ShapeRange.Line.Visible = false;\r\n"); //
			out.write("Selection.ShapeRange.Fill.Visible = true;\r\n");
			out.write("Selection.ShapeRange.Fill.Solid();\r\n");
			out.write("Selection.ShapeRange.Fill.ForeColor.RGB = 15780320;\r\n");
			out.write("Selection.ShapeRange.Fill.Transparency = 0.5;\r\n");
			out.write("Selection.ShapeRange.Rotation = 315;\r\n");
			out.write("Selection.ShapeRange.LockAspectRatio = true;\r\n");
			out.write("Selection.ShapeRange.Height = ActiveDocument.Application.CentimetersToPoints(4.13);\r\n");
			out.write("Selection.ShapeRange.Width = ActiveDocument.Application.CentimetersToPoints(16.52);\r\n");
			out.write("Selection.ShapeRange.WrapFormat.AllowOverlap = true;\r\n");
			out.write("Selection.ShapeRange.WrapFormat.Side = 3;\r\n");
			out.write("Selection.ShapeRange.WrapFormat.Type = 3;\r\n");
			out.write("Selection.ShapeRange.RelativeHorizontalPosition = 0;\r\n");
			out.write("Selection.ShapeRange.RelativeVerticalPosition = 0;\r\n");
			out.write("Selection.ShapeRange.Left = -999995;\r\n");
			out.write("Selection.ShapeRange.Top = -999995;\r\n");
			out.write("Selection.Sections(1).Headers(1).Range.Borders.Enable=false;ActiveDocument.ActiveWindow.ActivePane.View.SeekView = 0;\r\n");
			out.write(" }\r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");// 文件保护
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n\r\n");

			out.write("function removeWater(text){\r\n");
			out.write(" try{\r\n"); //
			out.write("var aa = TANGER_OCX_OBJ.ActiveDocument.Application;\r\n");
			out.write("aa.ActiveWindow.ActivePane.View.SeekView=9;\r\n");
			out.write("for(var i=0;i<text.length;i++){aa.selection.DELETE();}aa.selection.Text=\"\";\r\n"); //
			out.write("aa.selection.Sections(1).Headers(1).Range.Borders.Enable=false;aa.ActiveWindow.ActivePane.View.SeekView=0;\r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n\r\n");

			out.write("function removePageFooterRight(text){\r\n");
			out.write(" try{\r\n"); //
			out.write("var aa = TANGER_OCX_OBJ.ActiveDocument.Application;\r\n");
			out.write("aa.ActiveWindow.ActivePane.View.SeekView=10;\r\n");
			out.write("for(var i=0;i<text.length-1;i++){aa.selection.DELETE();} aa.selection.Text=\"\";\r\n"); //
			out.write("aa.ActiveWindow.ActivePane.View.SeekView=0;\r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n\r\n");

			//
			out.write("function addPageFooterRight(text){\r\n");
			out.write(" try{\r\n"); //
			out.write("var aa = TANGER_OCX_OBJ.ActiveDocument.Application;\r\n");
			out.write("aa.ActiveWindow.ActivePane.View.SeekView=10;\r\n");
			out.write("for(var i=0;i<text.length-1;i++){aa.selection.DELETE();}aa.selection.Text=text;\r\n");
			out.write("aa.selection.ParagraphFormat.Alignment=2;\r\n");
			out.write("aa.ActiveWindow.ActivePane.View.SeekView=0;\r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n\r\n");
			//TANGER_OCX_OBJ.ActiveDocument.Sections(1).Footers(1).PageNumbers.Add(1,true);

			out.write("function AddDocHeader(text){\r\n"); // yinliang 
			out.write(" try{\r\n"); //
			out.write("with(TANGER_OCX_OBJ.ActiveDocument.Application){");
			out.write("Selection.HomeKey(6,0);\r\n");
			out.write("Selection.TypeText(text);\r\n");
			out.write("Selection.TypeParagraph();\r\n");
			out.write("Selection.HomeKey(6,1);\r\n");
			out.write("Selection.ParagraphFormat.Alignment = 1;\r\n");
			out.write("Selection.MoveDown(5, 3, 0);\r\n");
			out.write(" } }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n\r\n");

			out.write("function addPageHeaderRight(text){\r\n");
			out.write(" try{\r\n");
			out.write("var aa = TANGER_OCX_OBJ.ActiveDocument.Application;\r\n");
			out.write("aa.ActiveWindow.ActivePane.View.SeekView=9;\r\n");
			out.write("for(var i=0;i<text.length-1;i++){aa.selection.DELETE();}aa.selection.Text=text;\r\n");
			out.write("aa.selection.ParagraphFormat.Alignment=2;\r\n");
			out.write("aa.selection.Font.Color=15777215;aa.selection.Sections(1).Headers(1).Range.Borders.Enable=false;aa.ActiveWindow.ActivePane.View.SeekView=0;\r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n\r\n");

			//TANGER_OCX_OBJ.ActiveDocument.Sections(1).Footers(1).PageNumbers.Add(1,true);
			out.write("function removePageHeaderRight(text){\r\n");
			out.write(" try{\r\n"); //
			out.write("var aa = TANGER_OCX_OBJ.ActiveDocument.Application;\r\n");
			out.write("aa.ActiveWindow.ActivePane.View.SeekView=9;\r\n");
			out.write("for(var i=0;i<text.length-1;i++){aa.selection.DELETE();} aa.selection.Text=\"\";\r\n"); //
			out.write("aa.selection.Sections(1).Headers(1).Range.Borders.Enable=false;aa.ActiveWindow.ActivePane.View.SeekView=0;\r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n\r\n");

			out.write("function setPageFooterColorB(){\r\n");
			out.write(" try{\r\n"); //
			out.write("var aa = TANGER_OCX_OBJ.ActiveDocument.Application;\r\n");
			out.write("aa.ActiveWindow.ActivePane.View.SeekView=10;var Selection = aa.Selection;\r\n");
			out.write("Selection.Font.Color=1;aa.selection.Sections(1).Headers(1).Range.Borders.Enable=false;aa.ActiveWindow.ActivePane.View.SeekView=0;\r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n\r\n");

			out.write("function setPageFooterColorW(){\r\n");
			out.write(" try{\r\n"); //
			out.write("var aa = TANGER_OCX_OBJ.ActiveDocument.Application;\r\n");
			out.write("aa.ActiveWindow.ActivePane.View.SeekView=10;\r\n");
			out.write("aa.selection.Font.Color=16777215;aa.selection.Sections(1).Headers(1).Range.Borders.Enable=false;aa.ActiveWindow.ActivePane.View.SeekView=0;\r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n\r\n");

			//doc加图片水印
			out.write("//加图片水印,在点击水印按钮时调用.\r\n");
			out.write("function addWaterPicture1(URL,x,y){\r\n");
			out.write(" try{\r\n"); //
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");// 取消文件保护状态
			out.write(" var ActiveDocument = TANGER_OCX_OBJ.ActiveDocument;\r\n"); //
			out.write(" for(var i=1;i<=ActiveDocument.Sections.Count;i++){\r\n");
			out.write(" ActiveDocument.Sections(i).Range.Select();\r\n"); //
			out.write(" ActiveDocument.ActiveWindow.ActivePane.View.SeekView = 9;\r\n"); //
			out.write(" var Selection = ActiveDocument.Application.Selection;\r\n"); //		
			out.write(" Selection.HeaderFooter.Shapes.AddPicture(URL,false,true).Select();\r\n"); //	
			//		out.write(" Selection.ShapeRange.Name = \"WordPictureWatermark1\";\r\n"); //
			out.write(" Selection.ShapeRange.LockAspectRatio = true;\r\n");
			out.write(" Selection.ShapeRange.WrapFormat.AllowOverlap = true;\r\n");
			out.write(" Selection.ShapeRange.WrapFormat.Side = 3;\r\n");
			out.write(" Selection.ShapeRange.WrapFormat.Type = 3;\r\n");
			out.write(" Selection.ShapeRange.RelativeHorizontalPosition =0;\r\n");
			out.write(" Selection.ShapeRange.RelativeVerticalPosition =0;\r\n");
			out.write(" Selection.ShapeRange.Left = x;\r\n");
			out.write(" Selection.ShapeRange.Top = y;\r\n");
			out.write(" ActiveDocument.ActiveWindow.ActivePane.View.SeekView = 0;\r\n");
			out.write("}\r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");// 文件保护
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n\r\n");

			//wps偶尔会报错！！ doc 图片加载不上
			out.write("//加图片水印,在点击水印按钮时调用.\r\n");
			out.write("function addWaterPicture(URL,x,y){\r\n");
			out.write(" try{\r\n"); //
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){alert(e.description);}");// 取消文件保护状态
			out.write(" var ActiveDocument = TANGER_OCX_OBJ.ActiveDocument;\r\n"); //
			out.write(" for(var i=1;i<=ActiveDocument.Sections.Count;i++){\r\n");
			out.write(" ActiveDocument.Sections(i).Range.Select();\r\n"); //
			out.write(" ActiveDocument.ActiveWindow.ActivePane.View.SeekView = 9;\r\n"); //
			out.write(" var Selection = ActiveDocument.Application.Selection;\r\n"); //	
			out.write("  TANGER_OCX_OBJ.AddPicFromURL(URL,true,x,y,1,100,0);\r\n");
			out.write(" Selection.ShapeRange.WrapFormat.AllowOverlap = true;\r\n");
			out.write(" Selection.ShapeRange.WrapFormat.Side = 3;\r\n");
			out.write(" Selection.ShapeRange.WrapFormat.Type = 3;\r\n");
			out.write(" ActiveDocument.ActiveWindow.ActivePane.View.SeekView = 0;\r\n");
			out.write("}\r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");// 文件保护
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n\r\n");

			out.write("//关闭控件,在body的onunload事件时调用.\r\n");
			out.write("function unLoad(){\r\n");
			out.write(" TANGER_OCX_OBJ.Close();  \r\n"); //
			out.write(" window.opener=null;  \r\n"); //
			out.write(" window.open(\"\",\"_self\");  \r\n"); //
			out.write(" window.close();  \r\n"); //
			out.write("}\r\n\r\n");

			out.write("function swingCall(_type){\r\n");
			out.write("  if(_type=='closedoc'){\r\n");
			out.write("  unLoad();\r\n");
			out.write("  }else if(_type=='savefile'){\r\n");
			out.write("  saveFileToRemoteServer();}\r\n");
			out.write("}\r\n");

			out.write("</script>\r\n");
			out.write("</head>\r\n");
			out.write("<body bgcolor=\"#ffffff\"  width=100%  height=100%  topmargin=5 leftmargin=5 rightmargin=0 bottommargin=0 marginwidth=0 marginheight=0 onload=\"JavaScript:load();\" onunload=\"JavaScript:unLoad();\">\r\n"); //

			if (ifselfdesc) {//是自己写的则按自定的来
				String str_1 = "" + (ifshowsave && (isEditSaveAsEditable ? (bo_editable ? true : false) : (bo_editable ? false : true))); //
				String str_2 = "" + (ifshowwater && bo_editable ? true : false); //
				String[][] buttontype = new String[][] {
				// 参数分别为：按钮是否显示，按钮方法，按钮显示名称
						{ str_1, "saveFileToRemoteServer()", "保存" },//
						{ ifshowprint_tao + "", "printFileSelf()", "套打" },//
						{ ifshowprint_all + "", "printAllSelf()", "全打" },//
						{ ifshowprint_fen + "", "splitPrintSelf()", "分打" },//
						{ ifshowprint + "", "printFileDirectSelf()", "打印" },//
						{ str_2, "addWater('" + ServerEnvironment.getProperty("LICENSEDTO") + "')", "水印" },//
						{ ifshowshowcomment + "", "showComment()", "显示批注" },//
						{ ifshowhidecomment + "", "hideComment()", "隐藏批注" },//
						{ ifshowedit + "", "edit()", "修订" },//
						{ ifshowshowedit + "", "showEdit()", "显示痕迹" },//
						{ ifshowhideedit + "", "hideEdit()", "隐藏痕迹" },//
						{ ifshowacceptedit + "", "acceptEdit()", "接受修订" },//
						{ ifshowclose + "", "unLoad()", "关闭" } //
				};
				out.write(setButtonIsShow(buttontype));
			} else {
				if (!(isHtml != null && "Y".equals(isHtml))) {
					String str_1 = "" + (isEditSaveAsEditable ? (bo_editable ? true : false) : (bo_editable ? false : true)); //
					String str_2 = "" + (bo_editable ? true : false); //
					String[][] buttontype = new String[][] {
					// 参数分别为： 按钮类型，按钮是否显示，按钮方法，按钮名称
							{ "NTKO_有保存按钮", str_1, "saveFileToRemoteServer()", "保存" },//
							{ "NTKO_有套打按钮", "", "printFile()", "套打" },//
							{ "NTKO_有全打按钮", "", "printAll()", "全打" },//
							{ "NTKO_有分打按钮", "", "splitPrint()", "分打" },//
							{ "NTKO_有打印按钮", "", "printFileDirectSelf", "打印" },//
							{ "NTKO_有水印按钮", str_2, "addWater('" + ServerEnvironment.getProperty("LICENSEDTO") + "')", "水印" },//
							{ "NTKO_有显示批注按钮", "", "showComment()", "显示批注" },//
							{ "NTKO_有隐藏批注按钮", "", "hideComment()", "隐藏批注" },//
							{ "NTKO_有修订按钮", "", "edit()", "修订" },//
							{ "NTKO_有显示痕迹按钮", "", "showEdit()", "显示痕迹" },//
							{ "NTKO_有隐藏痕迹按钮", "", "hideEdit()", "隐藏痕迹" },//
							{ "NTKO_有接受修订按钮", "", "acceptEdit()", "接受修订" },//
							{ "NTKO_有关闭按钮", "", "unLoad()", "关闭" } //				
					};
					// 处理按钮显示情况
					out.write(setButtonIsShow(tbUtil, buttontype));
				}
			}

			//操作结果，帮助文档显示时候不需要。
			if (ifshowresult) {
				out.write("<input type=\"text\" id=\"dealresult\" value=\"\" size=\"30\" style=\"border-left:0; border-bottom:0; border-right:0; border-top:0;color:#0000FF;font-weight:bold\" readonly=true>\r\n");
			}
			//系列重要参数!!千航有两种产品模式:
			//第一种是授仅给某一家开发商(比如普信),然后由这个开发商给最终用户(比如宁波银行)使用,这种情况下,这个软件只限于这一家最终客户(比如宁波银行)使用,而不能给其他客户使用!!! 且必须有MakerCaption与MakerKey这两个参数!!
			//第二种是开发商(比如普信)或最终用户买断使用权,然后可以任意使用!! 这时就不需要MakerCaption与MakerKey
			String str_clsid = tbUtil.getSysOptionStringValue("NTKO_clsid", "C9BC4DFF-4248-4a3c-8A49-63A7D317F404"); // 产品的clsid号，比较重要的属性，一定要与cab包中的clsid号对应，否则可能造成大麻烦
			String str_version = tbUtil.getSysOptionStringValue("NTKO_Version", "4,0,1,8"); //// 版本号

			String str_productCaption = tbUtil.getSysOptionStringValue("NTKO_ProductCaption", "宁波银行合规风险管理系统"); //产品标题【兴业银行全面合规管理系统/302FBB2D66E54110E6290E3F66F529CD28104624】【】
			String str_productKey = tbUtil.getSysOptionStringValue("NTKO_ProductKey", "77D6F250E11DA231EED3B33003925661EB111981"); //产品键值

			String str_makerCaption = tbUtil.getSysOptionStringValue("NTKO_MakerCaption", "北京普信管理咨询有限公司"); //创建者,凡是通过普信购买的,都用这个
			String str_makerKey = tbUtil.getSysOptionStringValue("NTKO_MakerKey", "07E148645F62214BF48F7FB0EC9BDBF327EA975D"); //创建的key,凡是通过普信购买的,都用这个

			//增加鼠标移入激活文档，否则其他word中拷贝后，千航控件里的word工具栏不可用了【李春娟/2016-02-23】
			out.write("<object id=\"TANGER_OCX_OBJ\" classid=\"clsid:" + str_clsid + "\" codebase=\"./applet/OfficeControl.cab#Version=" + str_version + "\" width=\"100%\" height=\"100%\" onMouseOver={Activate(true);} onMouseOut={Activate(true);}>\r\n");
			out.write("  <param name=\"Caption\"          value=\"Word/Excel/WPS\">\r\n");
			out.write("  <param name=\"ProductCaption\"   value=\"" + str_productCaption + "\">\r\n"); // 用户名称
			out.write("  <param name=\"ProductKey\"       value=\"" + str_productKey + "\">\r\n"); // 与用户名称密切关联的一个校验码
			if (tbUtil.getSysOptionBooleanValue("NTKO_NeedMaker", true)) {
				out.write("  <param name=\"MakerCaption\"     value=\"" + str_makerCaption + "\">\r\n");
				out.write("  <param name=\"MakerKey\"         value=\"" + str_makerKey + "\">\r\n"); // 用户名称
			}
			out.write("</object>\r\n");

			if (_ifhasfloat) { // 为了盗版.
				out.write("<div id=\"iframediv\" style=\"position:absolute;left:650;top:47;\"><iframe id=\"iframe1\" src=\"./OfficeViewServlet?innerframe=NTKO\" frameborder=\"0\" width=250 height=22></iframe></div>\r\n");
			}
			///设置另存为的默认文件名称，宁波需求，代码由孙福君提供【李春娟/2012-03-12】
			out.write("<script type=\"text/javascript\" for=\"TANGER_OCX_OBJ\" ");
			out.write("	event=\"OnFileCommand(item,cancle)\">");
			out.write(" if(item==4){");
			out.write("TANGER_OCX_OBJ.WebFileName=\"" + saveAsName + "\";");
			out.write("TANGER_OCX_OBJ.ShowDialog(2);");
			out.write("TANGER_OCX_OBJ.CancelLastCommand = true;");
			out.write(" }</script>");
			out.write("</body>\r\n");
			out.write("</html>\r\n");
		}
	}

	/**
	 * 系统配置下，设置按钮是否显示
	 * 
	 * @param buttontype
	 * @param tbUtil
	 */
	private String setButtonIsShow(TBUtil tbUtil, String[][] buttontypes) {
		String[] btnstr = null;
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < buttontypes.length; i++) {
			btnstr = buttontypes[i];
			if (tbUtil.getSysOptionBooleanValue(btnstr[0], true) && !"false".equals(btnstr[1])) {
				sb.append("<input type=\"button\" name=\"button\" class=\"btn_style\" onclick=\"JavaScript:" + btnstr[2] + ";\" value=\"" + btnstr[3] + "\"  >\r\n");
			}
		}
		return sb.toString();
	}

	/**
	 * 自定义按钮配置
	 * @param buttontype 是否显示
	 * @return
	 */
	private String setButtonIsShow(String[][] buttontype) {
		String[] btnstr = null;
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < buttontype.length; i++) {
			btnstr = buttontype[i];
			if (btnstr[0].equals("true")) // 如果是true，则显示
				sb.append("<input type=\"button\" name=\"button\" class=\"btn_style\" onclick=\"JavaScript:" + btnstr[1] + ";\" value=\"" + btnstr[2] + "\" >\r\n");
		}
		return sb.toString();
	}

	/**
	 * 直接下载文件
	 * 
	 * @param request
	 * @param response
	 * @param _ifhasfloat
	 * @throws ServletException
	 * @throws IOException
	 */
	private void deal_download(HttpServletRequest request, HttpServletResponse response, boolean _ifhasfloat) throws ServletException, IOException {
		request.setCharacterEncoding("GBK");
		TBUtil tb = new TBUtil();
		String str_RecordID = tb.convertHexStringToStr(request.getParameter("RecordID"));
		String str_fileType = request.getParameter("filetype");
		String str_fileName = null;

		if (str_fileType != null && !str_fileType.equals("")) {
			str_fileName = str_RecordID + "." + str_fileType;
		} else {
			str_fileName = str_RecordID;
		}

		response.setContentType("Application/octet-stream"); //
		response.setCharacterEncoding("GB2312");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + new String(str_fileName.getBytes("GB2312"), "ISO-8859-1") + "\"");

		String str_filepath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile/" + str_fileName;

		ServletOutputStream out = response.getOutputStream();
		try {
			InputStream in = new FileInputStream(str_filepath);

			int b = 0;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			StringBuffer strbf_result = new StringBuffer().append("<html>\r\n").append("<head>\r\n").append("<title>File Not Found</title>\r\n").append("</head>\r\n").append("<body>\r\n").append("文件未找到！\r\n").append("</body>\r\n").append("</html>\r\n");
			byte[] b = strbf_result.toString().getBytes("GBK");
			out.write(b);
		}
	}

	private String getHtmlHeader() {
		StringBuffer sb_html = new StringBuffer(); //
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<title>Office Compent View</title>\r\n");
		sb_html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n");
		sb_html.append("<style   type=\"text/css\">\r\n");
		sb_html.append("<!--   \r\n");
		sb_html.append(" .btn_style {\r\n");
		sb_html.append(" BORDER-RIGHT: #999999 1px solid; BORDER-TOP: #999999 1px solid; BORDER-LEFT: #999999 1px solid; BORDER-BOTTOM: #999999 1px solid; FONT-SIZE: 12px; HEIGHT: 18px; WIDTH=55px; BACKGROUND-COLOR: #EEEEEE\r\n");
		sb_html.append("} \r\n");
		sb_html.append("-->   \r\n");
		sb_html.append("</style>\r\n");

		return sb_html.toString(); //
	}

	/**
	 * 假层.
	 * 
	 * @return
	 */
	private String getGoldGridDiv(String _type) {
		StringBuffer sb_html = new StringBuffer(); //
		if (_type.equalsIgnoreCase("GOLDGRID")) {
			sb_html.append("<html>\r\n"); //
			sb_html.append("<body bgcolor=\"#A6CAF0\" topmargin=0 leftmargin=0 rightmargin=0 bottommargin=0 marginwidth=0 marginheight=0  >\r\n"); //
			sb_html.append("<font size=3 color=\"#FFFFFF\">Office [World/Excel/WPS]</font>\r\n");
			sb_html.append("<body>\r\n");
			sb_html.append("</html>\r\n");
		} else if (_type.equalsIgnoreCase("NTKO")) {
			sb_html.append("<html>\r\n"); //
			sb_html.append("<body bgcolor=\"#D4D0C8\" topmargin=0 leftmargin=0 rightmargin=0 bottommargin=0 marginwidth=0 marginheight=0  >\r\n"); //
			sb_html.append("<body>\r\n");
			sb_html.append("</html>\r\n");
		}
		return sb_html.toString(); //
	}
}
