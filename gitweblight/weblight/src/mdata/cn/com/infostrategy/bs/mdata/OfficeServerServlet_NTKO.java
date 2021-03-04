package cn.com.infostrategy.bs.mdata;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.bs.common.BSUtil;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.TBUtil;

import com.jspsmart.upload.SmartUpload;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.rtf.RtfWriter2;

public class OfficeServerServlet_NTKO extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public void service(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		_request.setCharacterEncoding("GBK");
		_response.setCharacterEncoding("GBK");
		_response.setBufferSize(51200); //
		String str_action = _request.getParameter("action"); //
		if (str_action.equals("open")) {
			loadFile(_request, _response); //
		} else if (str_action.equals("save")) {
			saveFile(_request, _response); //
		}
	}

	/***************************************************************************
	 * 打开服务器端文件..
	 * 
	 * @param _request
	 * @param _response
	 * @throws ServletException
	 * @throws IOException
	 * @throws
	 */
	private void loadFile(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		String mFileType = _request.getParameter("filetype"); // 文件类型
		String templetfilename = _request.getParameter("templetfilename");
		String fromClientDir = _request.getParameter("fromclientdir"); // 是否从客户端生成文件
		try {
			String mRecordID = new String(_request.getParameter("RecordID").getBytes("ISO-8859-1"), "GBK"); // 文件的唯一标识!
			String str_filedir = null; //
			if (fromClientDir != null && !"".equals(fromClientDir)) {
				str_filedir = fromClientDir; //
			} else {
				String str_subdir = _request.getParameter("subdir");
				if (str_subdir == null) {
					str_filedir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile"; //officecompfile
				} else {
					if ("Y".equals(_request.getParameter("isAbsoluteSeverDir"))) { //如果是服务器的绝对路径 Gwang 2012-09-17修改
						str_filedir = str_subdir; //从前台已经计算出来了subdir=[D:\TomCat5.5.23\webapps\psbc]
					} else {
						if (str_subdir.startsWith("/")) {
							str_filedir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + str_subdir; //
						} else {
							str_filedir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/" + str_subdir; //
						}
					}
				}
			}
			//问题:当附件是docx时点[编辑]会报错"文件传输错误!"
			//原因:是由于文件名中包括了一层目录如(20120421/N101_CEC4B5B561616161.docx), 但如果是doc类型就是没事, 没搞清千航控件是怎么处理的!
			//将文件名和路径分开后以上问题解决!【李春娟/2017-09-25】
			String str_filename = mRecordID + "." + mFileType; //
			if (str_filename.contains("/")) {
				if (str_filename.startsWith("/")) {
					str_filedir += str_filename.substring(0, str_filename.lastIndexOf("/"));
				} else {
					str_filedir += "/" + str_filename.substring(0, str_filename.lastIndexOf("/"));
				}
				str_filename = str_filename.substring(str_filename.lastIndexOf("/") + 1);
			}
			String str_filefullpathname = str_filedir + "/" + str_filename; //

			_response.setContentType("application/octet-stream");
			_response.setHeader("Content-Disposition", "attachment; filename=" + str_filename); //str_filename attachment

			byte[] bytes = getFileContentBytes(str_filefullpathname); //取文件内容试试!
			if (bytes != null) { //如成功找到文件,则输出
				_response.setContentLength(bytes.length); //
				java.io.OutputStream outStream = _response.getOutputStream();
				outStream.write(bytes); //
				outStream.close();
			} else { //如果找不到文件,则直接输出!!!
				System.out.println("office控件加载文件[" + str_filefullpathname + "]时发现没有(可能是本地或远程),尝试加载模板!!"); ////
				java.net.URL fileurl = null; //
				if (templetfilename != null) { // 如果定义了模板文件,则找模板文件
					fileurl = this.getClass().getResource("/" + templetfilename + "." + mFileType); ////
					if (fileurl == null) { // 如果有该模板文件
						fileurl = this.getClass().getResource("/blank." + mFileType); ////
					}
				} else { // 如果没有定义模板
					fileurl = this.getClass().getResource("/blank." + mFileType); ////
				}

				if (fileurl == null) { //如果模板与blank.doc都没有,则凭空创建!!
					System.out.println("使用千航运控件没有发现文件,也没发现模板文件,也没发现[blank." + mFileType + "]文件,使用itext创建空白文档!"); //
					bytes = getDefaultDoc(_response, mFileType, "未找到模板文件或blank." + mFileType + ",直接创建文档!"); //
				} else { //如果找到模板
					bytes = new TBUtil().readFromInputStreamToBytes(new FileInputStream(new File(fileurl.toURI()))); //
				}
				_response.setContentLength(bytes.length); //
				java.io.OutputStream outStream = _response.getOutputStream();
				outStream.write(bytes); //
				outStream.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			try {
				byte[] bytes = getDefaultDoc(_response, mFileType, "因为发生异常[" + ex.getMessage() + "," + ex.getClass() + "],所以先创建一个空白文档"); //
				_response.setContentLength(bytes.length); //
				java.io.OutputStream outStream = _response.getOutputStream();
				outStream.write(bytes); //
				outStream.close();
			} catch (Exception exx) {
				exx.printStackTrace(); //
			}
		}
	}

	/**
	 * 取得文件内容
	 * @param _fileName
	 * @return
	 */
	private byte[] getFileContentBytes(String _fileName) throws Exception {
		String str_newWebUrl = ServerEnvironment.getProperty("FullTextSearchURL"); //邮储中集群时不能使用nfs与ftp,所以只能http从另一台机器上存取...
		if (str_newWebUrl != null) { //如果定义了是从远程中取
			System.out.println("千航控件加载文件时发现定义了FullTextSearchURL=[" + str_newWebUrl + "],所以要转向远程取文件[" + _fileName + "]..."); ////
			HashMap requestMap = new HashMap();
			requestMap.put("Action", "download"); //
			requestMap.put("FileName", _fileName); //必须是路径全称

			HashMap rtMap = new BSUtil().callWebUrl(str_newWebUrl + "/UploadFileServlet", requestMap); //

			if ("Y".equals(rtMap.get("isException"))) { //如果发生异常
				Exception ex = (Exception) rtMap.get("Exception"); //
				throw new Exception("远程请求文件发生异常", ex); //
			}

			return (byte[]) rtMap.get("ByteCodes"); //返回文件实际内容!
		} else { //否则直接从本地取!!! 以后还要增加从数据库取的逻辑!!!即千航控件的数据也可以存到数据库中???
			System.out.println("千航控件直接从本地取文件[" + _fileName + "]..."); //
			File tFile = new File(_fileName);
			if (tFile.exists()) { // 如果文件存在,
				return new TBUtil().readFromInputStreamToBytes(new FileInputStream(tFile)); //
			} else {
				return null;
			}
		}
	}

	/**
	 * 保存文件.
	 * 
	 * @param _request
	 * @param _response
	 * @throws ServletException
	 * @throws IOException
	 */
	private void saveFile(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		try {
			String str_subdir = _request.getParameter("subdir");
			String str_filedir = null; //
			if (str_subdir == null) {
				str_filedir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile"; //
			} else {
				str_filedir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/" + str_subdir; //
			}

			SmartUpload mySmartUpload = new SmartUpload();
			mySmartUpload.initialize(this.getServletConfig(), _request, _response);
			mySmartUpload.upload(); // 上传文件..
			com.jspsmart.upload.File upFile = mySmartUpload.getFiles().getFile(0); //这是Smart中的File...
			String str_fileName = upFile.getFileName(); //文件名...
			byte[] bytes = new byte[upFile.getSize()]; //文件内容!
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = upFile.getBinaryData(i); //
			}
			String str_fileFullName = str_filedir + "/" + str_fileName; //文件全称!

			String str_newWebUrl = ServerEnvironment.getProperty("FullTextSearchURL"); //邮储中集群时不能使用nfs与ftp,所以只能http从另一台机器上存取...
			if (str_newWebUrl != null) { //如果定义了,则再次送到远程!!!
				System.out.println("使用千航控件保存文件[" + str_fileFullName + "]至远程WebUrl[" + str_newWebUrl + "]..."); //

				HashMap requestMap = new HashMap(); //
				requestMap.put("Action", "upload"); //上传动作
				requestMap.put("FileName", str_fileFullName); //文件全路径!!!
				requestMap.put("ByteCodes", bytes); //

				HashMap rtMap = new BSUtil().callWebUrl(str_newWebUrl + "/UploadFileServlet", requestMap); ////远程调用!
			} else { //保存到本地!!!
				System.out.println("使用千航控件成功将文件保存至本地路径[" + str_fileFullName + "]..."); //
				File fileDir = new File(str_filedir); //
				if (!fileDir.exists()) {
					fileDir.mkdirs(); // 如果不存在,则创建目录!!
				}
				new TBUtil().writeBytesToOutputStream(new FileOutputStream(str_fileFullName), bytes); //写文件
			}
			_response.setContentType("text/html"); //
			_response.setCharacterEncoding("gb2312"); //
			PrintWriter out = _response.getWriter(); //
			out.println("成功将文件保存到服务器!!!");//与其显示16进制文件名不如不显示
			out.close(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 如果找不到默认模板，则用itext创建空白文档
	 * @param e 
	 * @return
	 * @throws IOException 
	 * @throws DocumentException 
	 */
	private byte[] getDefaultDoc(HttpServletResponse _response, String _mFileType, String _text) throws IOException, DocumentException {
		Document document = new Document(PageSize.A4);// 设置纸张大小 		
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		RtfWriter2.getInstance(document, byteOutStream);// 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中 
		document.open();
		Chunk chunk = new Chunk(_text);
		document.add(chunk);
		document.close();

		byte[] byte_defaultdoc = byteOutStream.toByteArray();
		return byte_defaultdoc; //
	}

}
