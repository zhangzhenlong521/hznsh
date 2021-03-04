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
	 * �򿪷��������ļ�..
	 * 
	 * @param _request
	 * @param _response
	 * @throws ServletException
	 * @throws IOException
	 * @throws
	 */
	private void loadFile(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		String mFileType = _request.getParameter("filetype"); // �ļ�����
		String templetfilename = _request.getParameter("templetfilename");
		String fromClientDir = _request.getParameter("fromclientdir"); // �Ƿ�ӿͻ��������ļ�
		try {
			String mRecordID = new String(_request.getParameter("RecordID").getBytes("ISO-8859-1"), "GBK"); // �ļ���Ψһ��ʶ!
			String str_filedir = null; //
			if (fromClientDir != null && !"".equals(fromClientDir)) {
				str_filedir = fromClientDir; //
			} else {
				String str_subdir = _request.getParameter("subdir");
				if (str_subdir == null) {
					str_filedir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile"; //officecompfile
				} else {
					if ("Y".equals(_request.getParameter("isAbsoluteSeverDir"))) { //����Ƿ������ľ���·�� Gwang 2012-09-17�޸�
						str_filedir = str_subdir; //��ǰ̨�Ѿ����������subdir=[D:\TomCat5.5.23\webapps\psbc]
					} else {
						if (str_subdir.startsWith("/")) {
							str_filedir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + str_subdir; //
						} else {
							str_filedir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/" + str_subdir; //
						}
					}
				}
			}
			//����:��������docxʱ��[�༭]�ᱨ��"�ļ��������!"
			//ԭ��:�������ļ����а�����һ��Ŀ¼��(20120421/N101_CEC4B5B561616161.docx), �������doc���;���û��, û����ǧ���ؼ�����ô�����!
			//���ļ�����·���ֿ�������������!�����/2017-09-25��
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

			byte[] bytes = getFileContentBytes(str_filefullpathname); //ȡ�ļ���������!
			if (bytes != null) { //��ɹ��ҵ��ļ�,�����
				_response.setContentLength(bytes.length); //
				java.io.OutputStream outStream = _response.getOutputStream();
				outStream.write(bytes); //
				outStream.close();
			} else { //����Ҳ����ļ�,��ֱ�����!!!
				System.out.println("office�ؼ������ļ�[" + str_filefullpathname + "]ʱ����û��(�����Ǳ��ػ�Զ��),���Լ���ģ��!!"); ////
				java.net.URL fileurl = null; //
				if (templetfilename != null) { // ���������ģ���ļ�,����ģ���ļ�
					fileurl = this.getClass().getResource("/" + templetfilename + "." + mFileType); ////
					if (fileurl == null) { // ����и�ģ���ļ�
						fileurl = this.getClass().getResource("/blank." + mFileType); ////
					}
				} else { // ���û�ж���ģ��
					fileurl = this.getClass().getResource("/blank." + mFileType); ////
				}

				if (fileurl == null) { //���ģ����blank.doc��û��,��ƾ�մ���!!
					System.out.println("ʹ��ǧ���˿ؼ�û�з����ļ�,Ҳû����ģ���ļ�,Ҳû����[blank." + mFileType + "]�ļ�,ʹ��itext�����հ��ĵ�!"); //
					bytes = getDefaultDoc(_response, mFileType, "δ�ҵ�ģ���ļ���blank." + mFileType + ",ֱ�Ӵ����ĵ�!"); //
				} else { //����ҵ�ģ��
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
				byte[] bytes = getDefaultDoc(_response, mFileType, "��Ϊ�����쳣[" + ex.getMessage() + "," + ex.getClass() + "],�����ȴ���һ���հ��ĵ�"); //
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
	 * ȡ���ļ�����
	 * @param _fileName
	 * @return
	 */
	private byte[] getFileContentBytes(String _fileName) throws Exception {
		String str_newWebUrl = ServerEnvironment.getProperty("FullTextSearchURL"); //�ʴ��м�Ⱥʱ����ʹ��nfs��ftp,����ֻ��http����һ̨�����ϴ�ȡ...
		if (str_newWebUrl != null) { //����������Ǵ�Զ����ȡ
			System.out.println("ǧ���ؼ������ļ�ʱ���ֶ�����FullTextSearchURL=[" + str_newWebUrl + "],����Ҫת��Զ��ȡ�ļ�[" + _fileName + "]..."); ////
			HashMap requestMap = new HashMap();
			requestMap.put("Action", "download"); //
			requestMap.put("FileName", _fileName); //������·��ȫ��

			HashMap rtMap = new BSUtil().callWebUrl(str_newWebUrl + "/UploadFileServlet", requestMap); //

			if ("Y".equals(rtMap.get("isException"))) { //��������쳣
				Exception ex = (Exception) rtMap.get("Exception"); //
				throw new Exception("Զ�������ļ������쳣", ex); //
			}

			return (byte[]) rtMap.get("ByteCodes"); //�����ļ�ʵ������!
		} else { //����ֱ�Ӵӱ���ȡ!!! �Ժ�Ҫ���Ӵ����ݿ�ȡ���߼�!!!��ǧ���ؼ�������Ҳ���Դ浽���ݿ���???
			System.out.println("ǧ���ؼ�ֱ�Ӵӱ���ȡ�ļ�[" + _fileName + "]..."); //
			File tFile = new File(_fileName);
			if (tFile.exists()) { // ����ļ�����,
				return new TBUtil().readFromInputStreamToBytes(new FileInputStream(tFile)); //
			} else {
				return null;
			}
		}
	}

	/**
	 * �����ļ�.
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
			mySmartUpload.upload(); // �ϴ��ļ�..
			com.jspsmart.upload.File upFile = mySmartUpload.getFiles().getFile(0); //����Smart�е�File...
			String str_fileName = upFile.getFileName(); //�ļ���...
			byte[] bytes = new byte[upFile.getSize()]; //�ļ�����!
			for (int i = 0; i < bytes.length; i++) {
				bytes[i] = upFile.getBinaryData(i); //
			}
			String str_fileFullName = str_filedir + "/" + str_fileName; //�ļ�ȫ��!

			String str_newWebUrl = ServerEnvironment.getProperty("FullTextSearchURL"); //�ʴ��м�Ⱥʱ����ʹ��nfs��ftp,����ֻ��http����һ̨�����ϴ�ȡ...
			if (str_newWebUrl != null) { //���������,���ٴ��͵�Զ��!!!
				System.out.println("ʹ��ǧ���ؼ������ļ�[" + str_fileFullName + "]��Զ��WebUrl[" + str_newWebUrl + "]..."); //

				HashMap requestMap = new HashMap(); //
				requestMap.put("Action", "upload"); //�ϴ�����
				requestMap.put("FileName", str_fileFullName); //�ļ�ȫ·��!!!
				requestMap.put("ByteCodes", bytes); //

				HashMap rtMap = new BSUtil().callWebUrl(str_newWebUrl + "/UploadFileServlet", requestMap); ////Զ�̵���!
			} else { //���浽����!!!
				System.out.println("ʹ��ǧ���ؼ��ɹ����ļ�����������·��[" + str_fileFullName + "]..."); //
				File fileDir = new File(str_filedir); //
				if (!fileDir.exists()) {
					fileDir.mkdirs(); // ���������,�򴴽�Ŀ¼!!
				}
				new TBUtil().writeBytesToOutputStream(new FileOutputStream(str_fileFullName), bytes); //д�ļ�
			}
			_response.setContentType("text/html"); //
			_response.setCharacterEncoding("gb2312"); //
			PrintWriter out = _response.getWriter(); //
			out.println("�ɹ����ļ����浽������!!!");//������ʾ16�����ļ������粻��ʾ
			out.close(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ����Ҳ���Ĭ��ģ�壬����itext�����հ��ĵ�
	 * @param e 
	 * @return
	 * @throws IOException 
	 * @throws DocumentException 
	 */
	private byte[] getDefaultDoc(HttpServletResponse _response, String _mFileType, String _text) throws IOException, DocumentException {
		Document document = new Document(PageSize.A4);// ����ֽ�Ŵ�С 		
		ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
		RtfWriter2.getInstance(document, byteOutStream);// ����һ����д��(Writer)��document���������ͨ����д��(Writer)���Խ��ĵ�д�뵽������ 
		document.open();
		Chunk chunk = new Chunk(_text);
		document.add(chunk);
		document.close();

		byte[] byte_defaultdoc = byteOutStream.toByteArray();
		return byte_defaultdoc; //
	}

}
