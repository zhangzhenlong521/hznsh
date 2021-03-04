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
 * ʹ�õ������ؼ��鿴Office�ļ�!!
 * Ŀǰ��֧�ֽ����ǧ������!!��ƽ̨��˵���ʵ�·����һ����,��weblight.xml�������OFFICEACTIVEXTYPEָ����ʲô���ҵĿؼ�.
 * ���������˴������ݵ���ȴ�ǲ�һ����!
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
				out.println(getGoldGridDiv(_request.getParameter("innerframe"))); // �ٲ�...
				return; //
			}
			boolean bo_ifhasfloat = false; // �Ƿ�Ҫ�ø����㵲ס.
			String str_requestOfficeTypePar = _request.getParameter("OFFICEACTIVEXTYPE"); // �ȴӴ������Ĳ���ȡ,������ֵ,һ����GOLDGRID,һ����NTKO
			if (str_requestOfficeTypePar != null) { // �����URL�д������˲���!!!
				String str_isfloat = _request.getParameter("ISFLOAT"); //
				bo_ifhasfloat = ((str_isfloat == null || str_isfloat.equals("N")) ? false : true); //
				if (str_requestOfficeTypePar.equalsIgnoreCase("GOLDGRID")) { // ����ǽ��
					deal_GoldGrid(_request, _response, bo_ifhasfloat);
				} else if (str_requestOfficeTypePar.equalsIgnoreCase("NTKO")) { //
					deal_NTKO(_request, _response, bo_ifhasfloat);
				} else if (str_requestOfficeTypePar.equalsIgnoreCase("DOWNLOAD")) {
					// ���ÿؼ���ֱ������
					deal_download(_request, _response, bo_ifhasfloat);
				} else {
					_response.setContentType("text/html"); //
					_response.setCharacterEncoding("gb2312"); //
					PrintWriter out = _response.getWriter(); //
					out.println("ֱ�Ӵ�URL�д�����δ֪��OFFICEACTIVEXTYPE[" + str_requestOfficeTypePar + "]!!"); //
					return;
				}
			} else { // ���û��ָ��,���ϵͳ����ȡ.
				String str_officeActivexType = ServerEnvironment.getProperty("OFFICEACTIVEXTYPE"); //
				if (str_officeActivexType == null) { // ����ؼ�Ϊ��!!
					_response.setContentType("text/html"); //
					_response.setCharacterEncoding("gb2312"); //
					PrintWriter out = _response.getWriter(); //
					out.println("������ƽ̨�����ļ���ָ�� OFFICEACTIVEXTYPE ,��Office�ؼ��ṩ��!!"); //
				} else {
					if (str_officeActivexType.length() > 2) {
						bo_ifhasfloat = true; //
					}

					if (str_officeActivexType.indexOf("���") == 0) { // ���
						deal_GoldGrid(_request, _response, bo_ifhasfloat);
					} else if (str_officeActivexType.indexOf("ǧ��") == 0) { // ǧ��
						deal_NTKO(_request, _response, bo_ifhasfloat);
					} else {
						_response.setContentType("text/html"); //
						_response.setCharacterEncoding("gb2312"); //
						PrintWriter out = _response.getWriter(); //
						out.println("��ƽ̨�����ļ���ָ����δ֪��Office�ؼ��ṩ��[" + str_officeActivexType + "]!!"); //
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
	 * �������ؼ��Ĵ����߼�...
	 * 
	 * @param request
	 * @param response
	 * @param bo_ifhasfloat
	 * @throws ServletException
	 * @throws IOException
	 */

	// ��һ����ťȨ������ �����Ƿ��ܱ༭
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

		// �Զ���ȡOfficeServer��OCX�ļ�����URL·��
		String mRecordID = new String(request.getParameter("RecordID").getBytes("ISO-8859-1"), "GBK"); // �ļ���Ψһ��ʶ!
		String mFileType = "." + request.getParameter("filetype"); // �ļ�����
		String templetfilename = request.getParameter("templetfilename"); // ģ���ļ���
		String sessionId = request.getParameter("sessionid"); // sessionID,����������ǩ�滻

		String mHttpUrlName = request.getRequestURI();
		String mScriptName = request.getServletPath(); //
		String mServerUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + mHttpUrlName.substring(0, mHttpUrlName.lastIndexOf(mScriptName)) + "/OfficeServerServlet_GoldGrid";// ȡ��OfficeServer�ļ�������URL
		String mUserName = "Push World";

		OfficeCompentControlVO controlvo = null; //
		boolean isEditSaveAsEditable = true; // liuxuanfei
		boolean bo_editable = true; //
		boolean bo_printable = true; //
		String str_subdir = null; //
		HashMap bookMarkMap = null; //
		if (sessionId != null) {
			controlvo = WebCallIDFactory.getInstance().getOfficeCallParMap(sessionId); // �ڷ�������ȡ��session..
			if (controlvo != null) {
				bo_editable = controlvo.isEditable(); //
				bo_printable = controlvo.isPrintable(); //
				str_subdir = controlvo.getSubdir(); //
				isEditSaveAsEditable = controlvo.isEditSaveAsEditable(); // liuxuanfei
				if (controlvo.getBookmarkValue() != null) {
					bookMarkMap = (HashMap) controlvo.getBookmarkValue().clone(); // ȡ����ǩ,һ��Ҫ��¡һ��,��Ϊ�������Ͼʹ�session��ɾ����!
				}
			}
			WebCallIDFactory.getInstance().clearOfficeCallSession(sessionId); // ��ջỰ
		}
		if (str_subdir != null && !"".equals(str_subdir)) {
			mServerUrl = mServerUrl + "?subdir=" + str_subdir;
		}
		String mFileName = mRecordID + mFileType; // ȡ���������ĵ�����
		out.write(getHtmlHeader()); //
		out.write("<script language=javascript>\r\n");

		out.write("//����:���ز��\r\n");
		out.write("function load(){\r\n");

		out.write("  try{\r\n");
		out.write("    //�������Ա�������,ʵʼ��iWebOffice\r\n");
		out.write("    webform.WebOffice.WebUrl=\"" + mServerUrl + "\";  //�������������ݰ���·��,����Ҫ,һ�㲻�ܴ�.\r\n"); // WebUrl:ϵͳ������·������������ļ������������籣�桢���ĵ�����Ҫ�ļ�
		out.write("    webform.WebOffice.RecordID=\"" + mRecordID + "\";  //Ψһ��ʶ\r\n"); // RecordID:���ĵ���¼���
		out.write("    webform.WebOffice.Template=\"" + (templetfilename == null ? "" : templetfilename) + "\";  //ģ����\r\n"); // ģ����
		out.write("    webform.WebOffice.FileType=\"" + mFileType + "\";  //�ļ�����,��.doc .docx .xls .wps\r\n"); // �ĵ�����
		out.write("    webform.WebOffice.Filepath=\"C:/\";  //�ļ�����,��.doc .xls .wps\r\n"); // �ĵ�����

		// .doc
		// .xls
		// .wps
		out.write("    webform.WebOffice.FileName=\"" + mFileName + "\";  //�ļ���\r\n"); // �ļ���
		out.write("    webform.WebOffice.UserName=\"" + mUserName + "\";  //�û���,�����ۼ�ʱ��Ҫ\r\n"); // �����û���,�ۼ�������Ҫ,������Ҫʹ��ϵͳ��¼�û���
		out.write("    webform.WebOffice.EditType=\"0\";  //�༭����\r\n"); // �༭����
		// ��ʽһ����ʽ��
		// ,��һλ����Ϊ0,1,2,3
		// ����:0���ɱ༭;1���Ա༭,�޺ۼ�;2���Ա༭,�кۼ�,�����޶�;3���Ա༭,�кۼ�,���޶�.�ڶ�λ����Ϊ0,1
		// ����:0������ע,1������ע�����Բο�iWebOffice2006��EditType����
		out.write("    webform.WebOffice.MaxFileSize = 64 * 1024;\r\n"); // �����ĵ���С���ƣ�Ĭ����64M���������ó�16M
		out.write("\r\n");
		out.write("    //�������Կ��Բ�Ҫ\r\n");
		out.write("    webform.WebOffice.Language=\"CH\";\r\n"); // Language:������֧����ʾѡ��
		// CH ����
		// TW����
		// ENӢ��\r\n
		out.write("    webform.WebOffice.PenColor=\"#FF0000\"; //PenColor:Ĭ����ע��ɫ\r\n");
		out.write("    webform.WebOffice.PenWidth=\"1\";  //PenWidth:Ĭ����ע�ʿ�\r\n");
		out.write("    webform.WebOffice.Print=\"0\";  //Print:Ĭ���Ƿ���Դ�ӡ:1���Դ�ӡ��ע,0�����Դ�ӡ��ע\r\n");
		out.write("    webform.WebOffice.ShowToolBar=\"0\";  //ShowToolBar:�Ƿ���ʾ������:1��ʾ,0����ʾ\r\n");
		out.write("    webform.WebOffice.ShowMenu=\"1\"; //�Ƿ���ʾ�˵�\r\n");
		out.write("\r\n");
		out.write("    webform.WebOffice.WebOpen();  //��Զ���ĵ�,�������涨��Ĳ���,�򿪷��������ļ�,������÷�������OPTION=\"LOADFILE\"��,OPTION=\"LOADTEMPLATE\"\r\n");
		out.write("    webform.WebOffice.WebToolsEnable(\"Standard\",2521," + bo_printable + "); //���δ�ӡ��ť,����������ر�\r\n");
		// out.write(" webform.WebOffice.EditType=\"3,1\"; //��ʾ���� \r\n"); //
		// //����������ʾ��ע
		out.write("    webform.WebOffice.ShowType=\"2,1\"; //��ʾ���� \r\n"); // //�ĵ���ʾ��ʽ
		// 1:��ʾ������ע
		// 2:��ʾ��д��ע
		// 0:��ʾ�ĵ��˸�
		out.write("    webform.WebOffice.WebSetProtect(" + (!bo_editable) + ",\"\");   //�Ƿ񱣻��ĵ�,�ǳ��ؼ�,��ʱ��Ҫ����ֻ����ʾ,���ܿ���. \r\n"); // �Ƿ񱣻�

		out.write("\r\n");
		out.write("    //�����ǩ�滻,�����ж��,�Ǹ���sessionID�ӷ��������ڴ���ȡ��ѭ�����!\r\n");

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
		out.write("    alert(e.description);  //��ʾ��������Ϣ \r\n");
		out.write("  }\r\n");
		out.write("}\r\n");
		out.write("\r\n");

		out.write("//����:�˳�iWebOffice\r\n");
		out.write("function unLoad(){\r\n");
		out.write("  try{\r\n");
		out.write("  if (!webform.WebOffice.WebClose()){\r\n");
		out.write("  }else{\r\n");
		out.write("  }\r\n");
		out.write("  }catch(e){alert(e.description);}\r\n");
		out.write("}\r\n");
		out.write("\r\n");

		out.write("//����:��ʾ��ע\r\n");
		out.write("function showComment(){\r\n");
		out.write("  try{\r\n");
		out.write(" webform.WebOffice.webSetProtect(false,\"\");\r\n");
		out.write("  webform.WebOffice.EditType=\"3,1\";\r\n");
		out.write("  webform.WebOffice.ShowType=\"2,1\";\r\n");
		out.write("  }catch(e){alert(e.description);}\r\n");
		out.write("}\r\n");
		out.write("\r\n");

		out.write("//����:������ע\r\n");
		out.write("function hideComment(){\r\n");
		out.write("  try{\r\n");
		out.write("webform.WebOffice.webSetProtect(false,\"\");\r\n");
		out.write("  webform.WebOffice.EditType=\"1,1\";\r\n");
		out.write("  webform.WebOffice.ShowType=\"2,1\";\r\n");
		out.write("  }catch(e){alert(e.description);}\r\n");
		out.write("}\r\n");
		out.write("\r\n");

		out.write("//����:�����ļ���Զ�̷�����\r\n");
		out.write("function saveFileToRemoteServer(){\r\n");
		out.write("  if(webform.WebOffice.WebSave()){ \r\n");
		out.write("     alert('�����������������ɹ�!');  \r\n");
		out.write("  }\r\n");
		out.write("}\r\n");
		out.write("\r\n");

		out.write("//����:����ͼƬ\r\n");
		out.write("function insertPicture(){\r\n");
		out.write("  try{\r\n");
		out.write("webform.WebOffice.WebInsertImage(\"\",'082.jpg',true,5) ;  \r\n");
		out.write("  }catch(e){alert(e.description);}\r\n");
		out.write("}\r\n");
		out.write("\r\n");

		out.write("//����:����ˮӡ\r\n");// �����ҪͼƬˮӡ������WaterMarkPicture
		out.write("function addWater(){\r\n");
		out.write("  try{\r\n");
		out.write("  webform.WebOffice.webSetProtect(false,\"\");\r\n");
		out.write(" var WordObject=webform.WebOffice.WebObject; \r\n");
		out.write(" var n=WordObject.Application.ActiveDocument.PageSetup.DifferentFirstPageHeaderFooter;   \r\n");
		out.write(" if(n==\"9999999\"){\r\n");// ���зֽڷ����ĵ�
		out.write(" WordObject.Application.ActiveWindow.ActivePane.View.SeekView = 2;  \r\n");
		out.write(" WaterMark(WordObject); \r\n");// �������ˮӡ
		out.write(" WordObject.Application.ActiveDocument.Sections(3).Range.Select();\r\n");
		out.write(" WordObject.Application.ActiveWindow.ActivePane.View.SeekView = 9;\r\n");
		out.write(" WaterMark(WordObject); \r\n");
		out.write(" WordObject.Application.ActiveWindow.ActivePane.VerticalPercentScrolled = 2 ;\r\n");
		out.write(" }else if(n==\"0\"){  \r\n");// ���з�����ҳ���������ĵ�
		out.write(" WordObject.Application.ActiveWindow.ActivePane.View.SeekView = 9; \r\n");
		out.write(" WaterMark(WordObject);\r\n");
		out.write(" }else if(n==\"-1\"){ \r\n");// ���зָ���
		out.write(" WordObject.Application.ActiveWindow.ActivePane.View.SeekView = 2; \r\n");
		out.write("  WaterMark(WordObject);");
		out.write("  WordObject.Application.ActiveWindow.ActivePane.View.SeekView = 1;\r\n ");
		out.write("  WaterMark(WordObject);  \r\n");
		out.write(" }\r\n");
		out.write("  }catch(e){alert(e.description);}\r\n");
		out.write("}\r\n");
		out.write("\r\n");

		// ������ˮӡ
		out.write("function WaterMark(WordObject){\r\n");
		out.write("  try{\r\n");
		out.write("WordObject.Application.Selection.HeaderFooter.Shapes.AddTextEffect(0, \"��˾����\", \"����\", 1, false, false, 0, 0).Select();\r\n");
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

		// ��ͼƬˮӡ ����һ������·����ͼƬ
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
		out.write("<body bgcolor=\"#ffffff\" topmargin=5 leftmargin=5 rightmargin=0 bottommargin=0 marginwidth=0 marginheight=0 onload=\"load()\" onunload=\"unLoad()\">  <!--�������˳�iWebOffice-->\r\n");

		out.write("<form name=\"webform\" method=\"post\" action=\"\"> <!--����iWebOffice���ύ����Ϣ-->\r\n");

		out.write("<input type=\"button\" name=\"button\" class=\"btn_style\" onclick=\"JavaScript:saveFileToRemoteServer();\"  value=\"����\" " + (isEditSaveAsEditable ? (bo_editable ? "" : " disabled") : (bo_editable ? " disabled" : "")) + ">\r\n");

		// out.write("<input type=\"button\" name=\"button\" class=\"btn_style\"
		// onclick=\"JavaScript:showComment();\" value=\"��ʾ��ע\">\r\n");
		// out.write("<input type=\"button\" name=\"button\" class=\"btn_style\"
		// onclick=\"JavaScript:hideComment();\" value=\"������ע\">\r\n");
		// out.write("<input type=\"button\" name=\"button\" class=\"btn_style\"
		// onclick=\"JavaScript:insertPicture();\" value=\"����ͼƬ\" " +
		// (bo_editable ? "" : " disabled") + ">\r\n");
		// out.write("<input type=\"button\" name=\"button\" class=\"btn_style\"
		// onclick=\"JavaScript:addWater();\" value=\"����ˮӡ\" " + (!bo_editable ?
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
	 * ����ǧ���ؼ��Ĵ����߼�...
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
			// office �ؼ�����ѡ��
			// Ĭ������������
			TBUtil tbUtil = new TBUtil();

			// �Զ���ȡOfficeServer��OCX�ļ�����URL·��
			String mRecordID = new String(request.getParameter("RecordID").getBytes("ISO-8859-1"), "GBK"); // �ļ���Ψһ��ʶ!
			String mFileType = request.getParameter("filetype"); // �ļ�����
			String templetfilename = request.getParameter("templetfilename"); // ģ���ļ���
			String sessionId = request.getParameter("sessionid"); // sessionID,����������ǩ�滻
			String fromClientDir = request.getParameter("fromclientdir"); // �Ƿ�ӿͻ�������ҳ��
			String str_saveasable = request.getParameter("bo_saveasable");
			String mHttpUrlName = request.getRequestURI(); //
			String mScriptName = request.getServletPath(); //
			String transpro = System.getProperty("transpro");
			if (transpro == null || "".equals(transpro.trim())) {
				transpro = "http";
			}
			String mServerUrl1 = transpro + "://" + request.getServerName() + ":" + request.getServerPort() + mHttpUrlName.substring(0, mHttpUrlName.lastIndexOf(mScriptName)) + "/";
			String uploadfiledir = null;
			String mServerUrl = transpro + "://" + request.getServerName() + ":" + request.getServerPort() + mHttpUrlName.substring(0, mHttpUrlName.lastIndexOf(mScriptName)) + "/OfficeServerServlet_NTKO";// ȡ��OfficeServer�ļ�������URL

			if (System.getProperty("CALLURL") != null && !"".equals(System.getProperty("CALLURL"))) {
				mServerUrl1 = System.getProperty("CALLURL") + "/";
				mServerUrl = mServerUrl1 + "OfficeServerServlet_NTKO";
			}

			String textwater = request.getParameter("textwater");
			String picwater = request.getParameter("picwater");
			String picposition = request.getParameter("picposition");
			String fromtype = request.getParameter("fromtype");//�����ˮӡ��ˮӡͼƬ�Ƿ����db�����ݿ⣩��
			String isHtml = (String) request.getAttribute("isHtml"); //�Ƿ���HTML�����Ӵ򿪵�
			if (isHtml == null) {//ֱ�Ӵ�һ�������Ϳ���ֻ��֮ǰ���������������һ���ж�/sunfujun/20130107�ʴ�
				isHtml = request.getParameter("isHtml");
			}
			String str_subdir = request.getParameter("subdir");//�Ӹ������Ϳ��Զ�uploadĿ¼�µ��ļ�/sunfujun/20130107�ʴ�
			OfficeCompentControlVO controlvo = null; //
			boolean bo_editable = true; //
			boolean bo_printable = true; //
			boolean bo_saveasable = false;//һ�����Ϊ����Ҫ�򿪵���Щ�ط�����
			boolean bo_titlebar = true; //
			boolean bo_menubar = true;
			boolean bo_toolbar = true;
			boolean bo_menutoolbar = true;
			boolean ifshowsave = true;//�Ƿ���ʾ����
			boolean ifshowprint_tao = true;//�Ƿ���ʾ�״�
			boolean ifshowprint_all = true;//�Ƿ���ʾȫ��
			boolean ifshowprint_fen = true;//�Ƿ���ʾ�ִ�
			boolean ifshowprint = true;//�Ƿ���ʾ��ӡ
			boolean ifshowwater = true;//�Ƿ���ʾˮӡ
			boolean ifshowshowcomment = true;//�Ƿ���ʾ��ʾ��ע
			boolean ifshowhidecomment = true;//������ע
			boolean ifshowedit = true;//�޶�
			boolean ifshowshowedit = true;//��ʾ�޶�
			boolean ifshowhideedit = true;//�����޶�
			boolean ifshowacceptedit = true;//�����޶�
			boolean ifshowclose = true;//�ر�
			boolean ifselfdesc = false;//�Ƿ����ⶨ�弴�Լ�д��billofficepanel
			boolean ifshowresult = true;
			boolean isEditSaveAsEditable = true; // liuxuanfei
			String encryCode = null; // liuxuanfei
			HashMap bookMarkMap = null; //
			String saveAsName = "";
			if (sessionId != null) {
				controlvo = WebCallIDFactory.getInstance().getOfficeCallParMap(sessionId); // �ڷ�������ȡ��session..
				if (controlvo != null) {
					bo_saveasable = controlvo.isSaveas();
					bo_editable = controlvo.isEditable(); //
					bo_printable = controlvo.isPrintable(); //
					str_subdir = controlvo.getSubdir(); //
					bo_titlebar = controlvo.isTitlebar();
					bo_menubar = controlvo.isMenubar();
					bo_toolbar = controlvo.isToolbar();
					bo_menutoolbar = controlvo.isMenutoolbar();

					ifshowsave = controlvo.isIfshowsave();//�Ƿ���ʾ����
					ifshowprint_tao = controlvo.isIfshowprint_tao();//�Ƿ���ʾ�״�
					ifshowprint_all = controlvo.isIfshowprint_all();//�Ƿ���ʾȫ��
					ifshowprint_fen = controlvo.isIfshowprint_fen();//�Ƿ���ʾ�ִ�
					ifshowprint = controlvo.isIfshowprint();//�Ƿ���ʾ��ӡ
					ifshowwater = controlvo.isIfshowwater();//�Ƿ���ʾˮӡ
					ifshowshowcomment = controlvo.isIfshowshowcomment();//�Ƿ���ʾ��ʾ��ע
					ifshowhidecomment = controlvo.isIfshowhidecomment();//������ע
					ifshowedit = controlvo.isIfshowedit();//�޶�
					ifshowshowedit = controlvo.isIfshowshowedit();//��ʾ�޶�
					ifshowhideedit = controlvo.isIfshowhideedit();//�����޶�
					ifshowacceptedit = controlvo.isIfshowacceptedit();//�����޶�
					ifshowclose = controlvo.isIfshowclose();//�ر�
					ifselfdesc = controlvo.isIfselfdesc();
					isEditSaveAsEditable = controlvo.isEditSaveAsEditable(); // liuxuanfei
					encryCode = controlvo.getEncryCode(); // liuxuanfei
					ifshowresult = controlvo.ifshowresult();
					if (controlvo.getBookmarkValue() != null) {
						bookMarkMap = (HashMap) controlvo.getBookmarkValue().clone(); // ȡ����ǩ,һ��Ҫ��¡һ��,��Ϊ�������Ͼʹ�session��ɾ����!
					}
					saveAsName = controlvo.getSaveAsName();//�������Ϊ��Ĭ���ļ����ơ����/2012-03-12��
				}
				WebCallIDFactory.getInstance().clearOfficeCallSession(sessionId); // ��ջỰ
			}
			if (isHtml != null && "Y".equals(isHtml)) {
				bo_menubar = false; //����ʾ�˵���
				bo_toolbar = false; //����ʾ������ \r\n");
				bo_menutoolbar = false; //����ʾ���߲˵� 
				bo_editable = false;//������༭,����
			}

			String mFileName = mRecordID + "." + mFileType; // ȡ���������ĵ�����

			out.write(getHtmlHeader());
			out.write("\r\n");

			out.write("<script language=\"JavaScript\">\r\n");
			out.write("//��ҳ��ʱ��ʼ��,��body��onload�¼�ʱ����\r\n");
			out.write("function load(){\r\n");
			StringBuffer sb_openfileurl = new StringBuffer(mServerUrl + "?action=open"); // ��ָ������!!
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

			out.write("  TANGER_OCX_OBJ.OpenFromURL(\"" + sb_openfileurl.toString() + "\");  \r\n"); // ��ʱ�Զ������ļ�!!
			//******************������,��ʾˮӡЧ��********************//
			//	if(bo_editable){        //����ˮӡ�˿϶������ñ༭�ˣ���
			if (textwater != null && !"".equals(textwater) && (mFileType.equalsIgnoreCase("doc") || mFileType.equalsIgnoreCase("docx") || mFileType.equalsIgnoreCase("wps"))) {
				String str_textwater = tbUtil.convertHexStringToStr(textwater);
				out.write("addWater('" + str_textwater + "'); \r\n");
			}
			if (picwater != null && !"".equals(picwater)) {
				if (picposition == null || "".equals(picposition)) {
					if (mFileType.equalsIgnoreCase("doc") || mFileType.equalsIgnoreCase("docx")) {
						if (fromtype != null && fromtype.equalsIgnoreCase("db")) {//ͼƬ�Ƿ񱣴������ݿ��У�����ǣ���Ĭ�ϱ������ϴ�ͼƬ����pub_filewatermark
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
							if (fromtype != null && fromtype.equalsIgnoreCase("db")) {//ͼƬ�Ƿ񱣴������ݿ��У�����ǣ���Ĭ�ϱ������ϴ�ͼƬ����pub_filewatermark
								out.write("addWaterPicture1('" + mServerUrl1 + "ImageServlet?fromtype=db&imgname=" + tbUtil.convertHexStringToStr(picwater) + "'," + position[0] + "," + position[1] + ");\r\n");
							} else {
								out.write("addWaterPicture1('" + mServerUrl1 + "images/watermark/" + tbUtil.convertHexStringToStr(picwater) + "'," + position[0] + "," + position[1] + ");\r\n");
							}

						} else if (mFileType.equalsIgnoreCase("wps")) {
							out.write("addWaterPicture('" + mServerUrl1 + "images/watermark/" + tbUtil.convertHexStringToStr(picwater) + "'," + position[0] + "," + position[1] + ");\r\n");
						}
					} else {
						if (mFileType.equalsIgnoreCase("doc") || mFileType.equalsIgnoreCase("docx")) {
							if (fromtype != null && fromtype.equalsIgnoreCase("db")) {//ͼƬ�Ƿ񱣴������ݿ��У�����ǣ���Ĭ�ϱ������ϴ�ͼƬ����pub_filewatermark
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
				out.write("  TANGER_OCX_OBJ.Titlebar=true; //��ʾ������ \r\n"); // ��ʾ������	
			} else {
				out.write("  TANGER_OCX_OBJ.Titlebar=false; //��ʾ������ \r\n"); // ��ʾ������
			}
			if (bo_menubar)
				out.write("  TANGER_OCX_OBJ.Menubar=true;  //��ʾ�˵� \r\n"); // ��ʾ�˵�
			else
				out.write("  TANGER_OCX_OBJ.Menubar=false;  //��ʾ�˵� \r\n"); // ��ʾ�˵�
			if (bo_toolbar)
				out.write("  TANGER_OCX_OBJ.Toolbars=true; //��ʾ������ \r\n"); // ��ʾ������
			else
				out.write("  TANGER_OCX_OBJ.Toolbars=false; //��ʾ������ \r\n"); // ��ʾ������
			if (bo_menutoolbar)
				out.write("  TANGER_OCX_OBJ.IsShowToolMenu=true;  //��ʾ���߲˵� \r\n"); // ��ʾ���߲˵�
			else
				out.write("  TANGER_OCX_OBJ.IsShowToolMenu=false;  //��ʾ���߲˵� \r\n"); // ��ʾ���߲˵�
			out.write("  TANGER_OCX_OBJ.FileSave=false;  //�Ƿ���Ա����ĵ� \r\n");
			out.write("  TANGER_OCX_OBJ.FileNew=false;  //�Ƿ���Դ����ĵ� \r\n");
			out.write("  TANGER_OCX_OBJ.FileOpen=false;  //�Ƿ���Դ��ĵ� \r\n");
			out.write("  TANGER_OCX_OBJ.FileClose=false;  //�Ƿ���Թر��ĵ� \r\n");

			out.write("  TANGER_OCX_OBJ.FileSaveAs=" + bo_saveasable + ";  //�Ƿ�������Ϊ�ĵ� \r\n");
			out.write("  TANGER_OCX_OBJ.FilePrint=" + (bo_printable) + ";  //�Ƿ���Դ�ӡ \r\n");
			out.write("  TANGER_OCX_OBJ.FilePrintPreview=" + (bo_printable) + ";  //�Ƿ���Դ�ӡԤ�� \r\n");

			out.write("\r\n");
			out.write("  //�����ǩ�滻,�����ж��,�Ǹ���sessionID�ӷ��������ڴ���ȡ��ѭ�����!\r\n");
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

			out.write(" TANGER_OCX_OBJ.IsNoCopy=" + (!bo_editable) + "; \r\n"); // ������
			out.write("  TANGER_OCX_OBJ.SetReadOnly (" + (!bo_editable) + ",\"\");   //ֻ��״̬ \r\n");

			//�Ժ������ĵ�ϵͳ�Զ�����
			// ����ΪʲôҪ���ϱ���, ����Ϊ�ڡ���ҵ����ȫ��Ϲ����ϵͳ-��ͬ����-�ҵ�ʾ����ͬ�ı�-��ӡ��ʷ�������ߴ�ӡĬ����ֻ����, �ı���Ҳ�����Ʋ��ɱ༭..
			// Why? �������������������ǿ��Ե���? ���õ�ͬһ�δ���!! ������.. ����ֻ���������Զ�����������һ�鱣������Խ��ֻ��״̬, ֻ���ı���ɱ༭, �����ط��ܱ���!!
			if (encryCode != null && !encryCode.equals("")) {
				//��ȡ������, �ٽ��б���.. �����Ѿ��ӹ�������, �ٴ�ֱ�Ӽӱ������������..
				out.write("  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + encryCode + "'); \r\n");
				out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(1,true,'" + encryCode + "'); \r\n");
			}

			out.write("}\r\n\r\n");

			out.write("//�����ĵ�,�ڵ�����水ťʱ����.\r\n");
			out.write("function saveFileToRemoteServer(){\r\n");

			/***
			 * Gwang 2012-4-21�޸�
			 * SaveAsOtherFormatToURL�����������ĵ�����Ϊ������ʽ���ļ�
			 * ���ڱ༭docx�ļ�ʱ, ����ʱ�ᱨ��������ϢΪ����������:����ָ����ʽ����ʱ�ļ�Ŀ¼��������
			 * ���Ի�����SaveToURL����, �Ͼ�docx�õĻ��ʱȽϸ�!
			 */
			//out.write("  var v_result=TANGER_OCX_OBJ.SaveAsOtherFormatToURL(5,\"" + mServerUrl + "?action=save" + (str_subdir == null ? "" : ("&subdir=" + str_subdir)) + "\",\"EDITFILE\",\"\",\"" + mFileName + "\",\"0\");\r\n");
			out.write("  var v_result=TANGER_OCX_OBJ.SaveToURL(\"" + mServerUrl + "?action=save" + (str_subdir == null ? "" : ("&subdir=" + str_subdir)) + "\",\"EDITFILE\",\"\",\"" + mFileName + "\",\"0\");\r\n");
			out.write("  document.title = \"button_save_click\";\r\n"); //
			out.write("  dealresult.value = v_result;\r\n"); //��ǰ�ǵ�����,������������ɴ��ڶ㵽����ȥ,���¿�ס!!!
			out.write("}\r\n\r\n");

			out.write("//��ӡ�ĵ�,�ڵ���״�ťʱ����.\r\n");
			out.write("function printFileSelf(){\r\n");
			out.write("  document.title = \"button_printtao_click\";\r\n"); //
			out.write("}\r\n\r\n");

			out.write("//��ӡ�ĵ�,�ڵ����ӡ��ťʱ����.\r\n");
			out.write("function printFileDirectSelf(){\r\n");
			out.write("  document.title = \"button_printdirect_click\";\r\n"); //
			out.write("}\r\n\r\n");

			out.write("//��ӡ�ĵ�,�ڵ��ȫ��ťʱ����.\r\n");
			out.write("function printAllSelf(){\r\n");
			out.write("  document.title = \"button_printall_click\";\r\n"); //
			out.write("}\r\n\r\n");

			out.write("//��ӡ�ĵ�,�ڵ���ִ�ťʱ����.\r\n");
			out.write("function splitPrintSelf(){\r\n");
			out.write("  document.title = \"button_printfen_click\";\r\n"); //
			out.write("}\r\n\r\n");
			out.write("//��ӡ�ĵ�,�ڵ���״�ťʱ����.\r\n");
			out.write("function printFile(){\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("  TANGER_OCX_OBJ.FilePrint=true;\r\n"); // ������ι�������ӡ���ܺ󣬴�ӡ���治�ܼ��ص�����
			out.write("  var v_result=TANGER_OCX_OBJ.PrintOut(true);\r\n"); //
			out.write("  TANGER_OCX_OBJ.FilePrint=false;\r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("}\r\n\r\n");

			out.write("//��ӡ�ĵ�,�ڵ����ӡ��ťʱ����.\r\n");
			out.write("function printFileDirect(){\r\n");
			out.write("  TANGER_OCX_OBJ.FilePrint=true;\r\n"); // ������ι�������ӡ���ܺ󣬴�ӡ���治�ܼ��ص�����
			out.write("  var v_result=TANGER_OCX_OBJ.PrintOut(true);\r\n"); //
			out.write("  TANGER_OCX_OBJ.FilePrint=false;\r\n");
			out.write("}\r\n\r\n");

			out.write("//��ӡ�ĵ����ɻ�õ���رա�ȷ����ȡ����ť�ķ���ֵ \r\n"); //yinliang add
			out.write("function printoutword(){\r\n");
			out.write("TANGER_OCX_OBJ.Activate(); var retValue ; var doctype = TANGER_OCX_OBJ.DocType; \r\n");
			out.write("if(doctype == 1){ \r\n");
			out.write("try{ retValue = TANGER_OCX_OBJ.ActiveDocument.Application.Dialogs(88).Show(); \r\n");
			out.write("}catch(err){alert(err);} \r\n");
			out.write("}else if(doctype == 2){ \r\n");
			out.write("try{retValue = TANGER_OCX_OBJ.ActiveDocument.Application.Dialogs(8).Show(); \r\n");
			out.write("}catch(ee){alert(ee);}} return retValue ;} \r\n");

			//		out.write("//��ӡ�ĵ�,�ڵ����ӡ��ťʱ����,�Ƿ񵯳���ӡ����.\r\n");  
			//		out.write("function printFileDirect2(flag){\r\n");
			//		out.write("  TANGER_OCX_OBJ.FilePrint=true;\r\n"); // ������ι�������ӡ���ܺ󣬴�ӡ���治�ܼ��ص�����
			//		out.write("  var v_result=TANGER_OCX_OBJ.PrintOut(flag);\r\n"); //
			//		out.write("  alert(v_result);\r\n"); //
			//		out.write("  TANGER_OCX_OBJ.FilePrint=false;\r\n");
			//		out.write("}\r\n\r\n");

			//
			out.write("//��ӡ�ĵ�,�ڵ��ȫ��ťʱ����.\r\n");
			out.write("function printAll(){\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("  TANGER_OCX_OBJ.ActiveDocument.Application.Selection.Wholestory();\r\n");
			out.write("	 TANGER_OCX_OBJ.ActiveDocument.Application.Selection.Font.Color=0;\r\n");
			out.write("  TANGER_OCX_OBJ.FilePrint=true;\r\n"); // ������ι�������ӡ���ܺ󣬴�ӡ���治�ܼ��ص�����
			out.write("  TANGER_OCX_OBJ.ActiveDocument.Application.Selection.MoveLeft();\r\n"); //
			out.write("  var v_result=TANGER_OCX_OBJ.PrintOut(true);\r\n"); //
			out.write("  TANGER_OCX_OBJ.ActiveDocument.Undo(3);\r\n"); //����������ɫӦ�������β�����һ��ѡ��һ�θ�ɫ
			out.write("  TANGER_OCX_OBJ.FilePrint=false;\r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("}\r\n\r\n");

			out.write("//��ӡ�ĵ�,�ڵ���ִ�ťʱ����.\r\n");
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
			out.write(" TANGER_OCX_OBJ.ActiveDocument.Undo(6);\r\n"); //����������ɫӦ�������β�����һ��ѡ��һ�θ�ɫ
			out.write("  var v_result=TANGER_OCX_OBJ.PrintOut(true);\r\n"); //	b			
			out.write("  TANGER_OCX_OBJ.FilePrint=false;\r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("}\r\n\r\n");

			out.write("//��ӡ�ĵ�,�ڵ��ȫ��ťʱ����.\r\n");
			out.write("function mytest(){\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("  TANGER_OCX_OBJ.ActiveDocument.Application.Selection.Wholestory();\r\n");
			out.write(" with (TANGER_OCX_OBJ.ActiveDocument.Application.Selection){\r\n");
			out.write("this.Font.NameFarEast = \"����_GB2312\";\r\n");
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

			out.write("//����:��ʾ��ע\r\n");
			out.write("function showComment(){\r\n");
			out.write("  try{\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("  TANGER_OCX_OBJ.ActiveDocument.TrackRevisions=true;\r\n");
			out.write("  TANGER_OCX_OBJ.ActiveDocument.ShowRevisions=true;\r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n");
			out.write("\r\n");

			out.write("//����:������ע\r\n");
			out.write("function hideComment(){\r\n");
			out.write("  try{\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("  TANGER_OCX_OBJ.ActiveDocument.TrackRevisions=false;\r\n");
			out.write("  TANGER_OCX_OBJ.ActiveDocument.ShowRevisions=false;\r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n");
			out.write("\r\n");

			out.write("//����:�޶����ۼ�����\r\n");
			out.write("function edit(){\r\n");
			out.write("  try{\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("	TANGER_OCX_OBJ.ActiveDocument.TrackRevisions = true;\r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n");
			out.write("\r\n");

			out.write("//����:��ʾ�ۼ�\r\n");
			out.write("function showEdit(){\r\n");
			out.write("  try{\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("	TANGER_OCX_OBJ.ActiveDocument.ShowRevisions=true;   \r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n");
			out.write("\r\n");

			out.write("//����:���غۼ�\r\n");
			out.write("function hideEdit(){\r\n");
			out.write("  try{\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("	TANGER_OCX_OBJ.ActiveDocument.ShowRevisions=false;   \r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n");
			out.write("\r\n");

			out.write("//����:�����޶�\r\n");
			out.write("function acceptEdit(){\r\n");
			out.write("  try{\r\n");
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");
			out.write("	TANGER_OCX_OBJ.ActiveDocument.AcceptAllRevisions();   \r\n");
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n");
			out.write("\r\n");

			out.write("//������ˮӡ,�ڵ��ˮӡ��ťʱ����.\r\n");
			out.write("function addWater(text){\r\n");
			out.write(" try{\r\n"); //
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");// ȡ���ļ�����״̬
			out.write(" var ActiveDocument = TANGER_OCX_OBJ.ActiveDocument;\r\n"); //
			out.write(" for(var i=1;i<=ActiveDocument.Sections.Count;i++){\r\n");
			out.write(" ActiveDocument.Sections(i).Range.Select();\r\n"); //
			out.write(" ActiveDocument.ActiveWindow.ActivePane.View.SeekView = 9;\r\n"); //
			out.write(" var Selection = ActiveDocument.Application.Selection;\r\n"); //
			out.write(" Selection.HeaderFooter.Shapes.AddTextEffect(0,  text, \"����\", 1, false, false, 0, 0).Select();\r\n"); //
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
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");// �ļ�����
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

			//doc��ͼƬˮӡ
			out.write("//��ͼƬˮӡ,�ڵ��ˮӡ��ťʱ����.\r\n");
			out.write("function addWaterPicture1(URL,x,y){\r\n");
			out.write(" try{\r\n"); //
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){}");// ȡ���ļ�����״̬
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
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");// �ļ�����
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n\r\n");

			//wpsż���ᱨ���� doc ͼƬ���ز���
			out.write("//��ͼƬˮӡ,�ڵ��ˮӡ��ťʱ����.\r\n");
			out.write("function addWaterPicture(URL,x,y){\r\n");
			out.write(" try{\r\n"); //
			//out.write("  try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('" + mycode + "');} catch(e){alert(e.description);}");// ȡ���ļ�����״̬
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
			//out.write("  TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,\"" + mycode + "\"); \r\n");// �ļ�����
			out.write("  }catch(e){alert(e.description);}\r\n");
			out.write("}\r\n\r\n");

			out.write("//�رտؼ�,��body��onunload�¼�ʱ����.\r\n");
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

			if (ifselfdesc) {//���Լ�д�����Զ�����
				String str_1 = "" + (ifshowsave && (isEditSaveAsEditable ? (bo_editable ? true : false) : (bo_editable ? false : true))); //
				String str_2 = "" + (ifshowwater && bo_editable ? true : false); //
				String[][] buttontype = new String[][] {
				// �����ֱ�Ϊ����ť�Ƿ���ʾ����ť��������ť��ʾ����
						{ str_1, "saveFileToRemoteServer()", "����" },//
						{ ifshowprint_tao + "", "printFileSelf()", "�״�" },//
						{ ifshowprint_all + "", "printAllSelf()", "ȫ��" },//
						{ ifshowprint_fen + "", "splitPrintSelf()", "�ִ�" },//
						{ ifshowprint + "", "printFileDirectSelf()", "��ӡ" },//
						{ str_2, "addWater('" + ServerEnvironment.getProperty("LICENSEDTO") + "')", "ˮӡ" },//
						{ ifshowshowcomment + "", "showComment()", "��ʾ��ע" },//
						{ ifshowhidecomment + "", "hideComment()", "������ע" },//
						{ ifshowedit + "", "edit()", "�޶�" },//
						{ ifshowshowedit + "", "showEdit()", "��ʾ�ۼ�" },//
						{ ifshowhideedit + "", "hideEdit()", "���غۼ�" },//
						{ ifshowacceptedit + "", "acceptEdit()", "�����޶�" },//
						{ ifshowclose + "", "unLoad()", "�ر�" } //
				};
				out.write(setButtonIsShow(buttontype));
			} else {
				if (!(isHtml != null && "Y".equals(isHtml))) {
					String str_1 = "" + (isEditSaveAsEditable ? (bo_editable ? true : false) : (bo_editable ? false : true)); //
					String str_2 = "" + (bo_editable ? true : false); //
					String[][] buttontype = new String[][] {
					// �����ֱ�Ϊ�� ��ť���ͣ���ť�Ƿ���ʾ����ť��������ť����
							{ "NTKO_�б��水ť", str_1, "saveFileToRemoteServer()", "����" },//
							{ "NTKO_���״�ť", "", "printFile()", "�״�" },//
							{ "NTKO_��ȫ��ť", "", "printAll()", "ȫ��" },//
							{ "NTKO_�зִ�ť", "", "splitPrint()", "�ִ�" },//
							{ "NTKO_�д�ӡ��ť", "", "printFileDirectSelf", "��ӡ" },//
							{ "NTKO_��ˮӡ��ť", str_2, "addWater('" + ServerEnvironment.getProperty("LICENSEDTO") + "')", "ˮӡ" },//
							{ "NTKO_����ʾ��ע��ť", "", "showComment()", "��ʾ��ע" },//
							{ "NTKO_��������ע��ť", "", "hideComment()", "������ע" },//
							{ "NTKO_���޶���ť", "", "edit()", "�޶�" },//
							{ "NTKO_����ʾ�ۼ���ť", "", "showEdit()", "��ʾ�ۼ�" },//
							{ "NTKO_�����غۼ���ť", "", "hideEdit()", "���غۼ�" },//
							{ "NTKO_�н����޶���ť", "", "acceptEdit()", "�����޶�" },//
							{ "NTKO_�йرհ�ť", "", "unLoad()", "�ر�" } //				
					};
					// ����ť��ʾ���
					out.write(setButtonIsShow(tbUtil, buttontype));
				}
			}

			//��������������ĵ���ʾʱ����Ҫ��
			if (ifshowresult) {
				out.write("<input type=\"text\" id=\"dealresult\" value=\"\" size=\"30\" style=\"border-left:0; border-bottom:0; border-right:0; border-top:0;color:#0000FF;font-weight:bold\" readonly=true>\r\n");
			}
			//ϵ����Ҫ����!!ǧ�������ֲ�Ʒģʽ:
			//��һ�����ڽ���ĳһ�ҿ�����(��������),Ȼ������������̸������û�(������������)ʹ��,���������,������ֻ������һ�����տͻ�(������������)ʹ��,�����ܸ������ͻ�ʹ��!!! �ұ�����MakerCaption��MakerKey����������!!
			//�ڶ����ǿ�����(��������)�������û����ʹ��Ȩ,Ȼ���������ʹ��!! ��ʱ�Ͳ���ҪMakerCaption��MakerKey
			String str_clsid = tbUtil.getSysOptionStringValue("NTKO_clsid", "C9BC4DFF-4248-4a3c-8A49-63A7D317F404"); // ��Ʒ��clsid�ţ��Ƚ���Ҫ�����ԣ�һ��Ҫ��cab���е�clsid�Ŷ�Ӧ�����������ɴ��鷳
			String str_version = tbUtil.getSysOptionStringValue("NTKO_Version", "4,0,1,8"); //// �汾��

			String str_productCaption = tbUtil.getSysOptionStringValue("NTKO_ProductCaption", "�������кϹ���չ���ϵͳ"); //��Ʒ���⡾��ҵ����ȫ��Ϲ����ϵͳ/302FBB2D66E54110E6290E3F66F529CD28104624������
			String str_productKey = tbUtil.getSysOptionStringValue("NTKO_ProductKey", "77D6F250E11DA231EED3B33003925661EB111981"); //��Ʒ��ֵ

			String str_makerCaption = tbUtil.getSysOptionStringValue("NTKO_MakerCaption", "�������Ź�����ѯ���޹�˾"); //������,����ͨ�����Ź����,�������
			String str_makerKey = tbUtil.getSysOptionStringValue("NTKO_MakerKey", "07E148645F62214BF48F7FB0EC9BDBF327EA975D"); //������key,����ͨ�����Ź����,�������

			//����������뼤���ĵ�����������word�п�����ǧ���ؼ����word�������������ˡ����/2016-02-23��
			out.write("<object id=\"TANGER_OCX_OBJ\" classid=\"clsid:" + str_clsid + "\" codebase=\"./applet/OfficeControl.cab#Version=" + str_version + "\" width=\"100%\" height=\"100%\" onMouseOver={Activate(true);} onMouseOut={Activate(true);}>\r\n");
			out.write("  <param name=\"Caption\"          value=\"Word/Excel/WPS\">\r\n");
			out.write("  <param name=\"ProductCaption\"   value=\"" + str_productCaption + "\">\r\n"); // �û�����
			out.write("  <param name=\"ProductKey\"       value=\"" + str_productKey + "\">\r\n"); // ���û��������й�����һ��У����
			if (tbUtil.getSysOptionBooleanValue("NTKO_NeedMaker", true)) {
				out.write("  <param name=\"MakerCaption\"     value=\"" + str_makerCaption + "\">\r\n");
				out.write("  <param name=\"MakerKey\"         value=\"" + str_makerKey + "\">\r\n"); // �û�����
			}
			out.write("</object>\r\n");

			if (_ifhasfloat) { // Ϊ�˵���.
				out.write("<div id=\"iframediv\" style=\"position:absolute;left:650;top:47;\"><iframe id=\"iframe1\" src=\"./OfficeViewServlet?innerframe=NTKO\" frameborder=\"0\" width=250 height=22></iframe></div>\r\n");
			}
			///�������Ϊ��Ĭ���ļ����ƣ��������󣬴������︣���ṩ�����/2012-03-12��
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
	 * ϵͳ�����£����ð�ť�Ƿ���ʾ
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
	 * �Զ��尴ť����
	 * @param buttontype �Ƿ���ʾ
	 * @return
	 */
	private String setButtonIsShow(String[][] buttontype) {
		String[] btnstr = null;
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < buttontype.length; i++) {
			btnstr = buttontype[i];
			if (btnstr[0].equals("true")) // �����true������ʾ
				sb.append("<input type=\"button\" name=\"button\" class=\"btn_style\" onclick=\"JavaScript:" + btnstr[1] + ";\" value=\"" + btnstr[2] + "\" >\r\n");
		}
		return sb.toString();
	}

	/**
	 * ֱ�������ļ�
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
			StringBuffer strbf_result = new StringBuffer().append("<html>\r\n").append("<head>\r\n").append("<title>File Not Found</title>\r\n").append("</head>\r\n").append("<body>\r\n").append("�ļ�δ�ҵ���\r\n").append("</body>\r\n").append("</html>\r\n");
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
	 * �ٲ�.
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
