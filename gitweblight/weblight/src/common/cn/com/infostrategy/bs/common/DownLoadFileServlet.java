package cn.com.infostrategy.bs.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.infostrategy.to.common.TBUtil;

public class DownLoadFileServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private int threadLimit = -1; //�߳�����,�������������ж��ٸ��û�ͬʱ���أ�����������������ʾ����ȴ���
	private int li_OneUserSpeed = -1; //1500; //����ÿ���û����ص�����ٶ�,��KB/��
	public static int threadCount = 0; //�߳��ۼӼ���������,��Ϊ�������̶߳�Ҫʹ�����,���Ա�����ȫ�־�̬����
	private static Hashtable hm_FileCache = new Hashtable(); //(HashMap) Collections.synchronizedMap(new HashMap()); //�����ļ��Ĺ�ϣ��,������Щ�ļ�ֻȡ��һ��,Ȼ��ͷ��ڻ�������,������ÿ�ζ�ȡ,����ͻ��˲��,ϵͳ����ʱ�������ǳ���!!

	@Override
	protected void service(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		//_request.setCharacterEncoding("GBK"); //
		FileInputStream fileIn = null; //
		ServletOutputStream resOutStream = null; //
		try {
			String str_pathtype = _request.getParameter("pathtype"); //upload
			String str_iscachefile = _request.getParameter("iscachefile"); ////upload
			String str_fileName = _request.getParameter("filename"); //
			if (str_fileName == null || str_fileName.trim().equals("")) {
				StringBuilder sb_help = new StringBuilder(); //
				sb_help.append("����ָ��filename����,��ʽ�ǡ�/abc.doc��,��/dir1/dir2/abc.doc��������,���ļ��������ǿ��Դ���Ŀ¼��,����ƴ���ڲ���pathtyp�����γ�һ���������ļ�ȫ·��!<br>\r\n"); //
				sb_help.append("����pathtype�ǿ�ѡ��,���Ϊ��,��Ĭ�ϸ�Ŀ¼�ǡ�%WebRoot%��Ŀ¼,���硾D:\\TomCat5.5.23\\webapps\\ipushgrc��<br>\r\n");
				sb_help.append("���pathtyp=upload,���Ŀ¼�ǡ�%WLTUPLOADFILEDIR%\\upload��,���б���%WLTUPLOADFILEDIR%��weblight.xml���õĲ���,����ֵ�����ǡ�C:\\WebPushTemp��!<br>\r\n"); //
				sb_help.append("���pathtyp=officecompfile,���Ŀ¼�ǡ�%WLTUPLOADFILEDIR%\\officecompfile��<br>\r\n"); //
				sb_help.append("���pathtyp=serverdir,���Ŀ¼ֱ���ǡ�%WLTUPLOADFILEDIR%\\��,��ֱ�ӴӲ���ָ���ĸ�Ŀ¼������!<br>\r\n"); //
				sb_help.append("����iscachefile=Y,��ʾһ�����غ���Զ���������,����webpushjre.exe������������,Ĭ��Ϊ��!<br><br>\r\n"); //
				sb_help.append("�������������:<br>\r\n"); //
				sb_help.append("/DownLoadFileServlet?filename=/abc.doc<br>\r\n"); //
				sb_help.append("/DownLoadFileServlet?filename=/abc.doc&pathtype=upload<br>\r\n"); //
				sb_help.append("/DownLoadFileServlet?filename=/abc.doc&pathtype=office&iscachefile=Y<br>\r\n"); //
				sb_help.append("/DownLoadFileServlet?filename=/dir1/abc.doc&pathtype=serverdir&iscachefile=Y<br>\r\n"); //
				writeErrorMsg(_response, sb_help.toString()); //
				return; //ֱ���˳�
			}
		
			
			str_fileName = URLDecoder.decode(str_fileName, "GBK");//��ü�����䣬������������,������new String(str_fileName.getBytes("ISO-8859-1"), "GBK")���ַ�ʽ���ǻ����롾����/2012-6-19��
			str_fileName = new String(str_fileName.getBytes("ISO-8859-1"), "GBK"); //ת��������!
			//by haoming 20160519 ��Ҫ��·��תһ�����ж�һ�Ρ�
			if(str_fileName!=null && str_fileName.contains("..")){//̫ƽ�ں�ɨ�跢�ִ�©����DownLoadFileServlet?filename=/../../../../../../../../../../../../etc/passwd%00.html&iscachefile=Y  
				writeErrorMsg(_response,"����·���зǷ�����"); //
				return;
			}
			if (!str_fileName.startsWith("/")) { //�������/��ͷ,���Զ�����/
				str_fileName = "/" + str_fileName; //
			}
			//System.out.println("DownLoadFileServlet:��Ҫ���ص��ļ�Ϊ��" + str_fileName);
			String str_realpath = "";
			if (str_pathtype == null) { //·������
				str_realpath = ServerEnvironment.getProperty("SERVERREALPATH"); //
			} else if (str_pathtype.equalsIgnoreCase("upload")) {
				str_realpath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/upload//"; //
			} else if (str_pathtype.equalsIgnoreCase("office")) {
				str_realpath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile//"; //
			} else if (str_pathtype.equalsIgnoreCase("serverdir")) {
				str_realpath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "//"; //������ϴ�������Ŀ¼�µ�ĳ��ֱ��Ŀ¼!!
			} else if (str_pathtype.equalsIgnoreCase("realdir")) { //����Ƿ������ľ���·�� Gwang 2012-09-17�޸�
				str_realpath = "";
			}

			//·������,���鷳,��ǰ������WebSphere·���ٸ�����
			if (!str_realpath.endsWith("/")) { //�������/��β
				str_realpath = str_realpath + "/"; //��һ��·��,��Ϊ��websphere�еĲ���·����һ����....installedApps/PC-200910091202Node01Cell/cmb1_war.ear/cmb1.war��������!������/Ŀ¼��β��!!!
			}

			String str_realfilename = str_fileName.substring(str_fileName.lastIndexOf("/") + 1, str_fileName.length()); //ʵ���ļ���,��ȥ������Ŀ¼����ļ���!
			String str_downloadFileName = str_realpath.substring(0, str_realpath.length() - 1) + str_fileName; ////
			//System.out.println("DownLoadFileServlet����Ҫ���ص��ļ���ȫ·����" + str_downloadFileName);
			if (threadLimit > 0) { //���ָ��������
				DownLoadFileServlet.threadCount++; //��ȫ�ֱ����������м�1
				if (DownLoadFileServlet.threadCount > threadLimit) { //���ж��Ƿ񳬹�����
					writeErrorMsg(_response, "����[" + threadLimit + "]��ͬʱ������,���Ժ�������...."); //
					return; //ֱ���˳�
				}
			}

			//�ȶ�ȡ�ļ�,�����ֽ�����byte[]������,ʵ������ʱ���������������洦���.
			byte[] bytes = null;
			if (str_iscachefile != null && str_iscachefile.equalsIgnoreCase("Y") && hm_FileCache.containsKey(str_fileName)) { //���ָ���˻��洦��,������������
				bytes = (byte[]) hm_FileCache.get(str_fileName); //
				System.out.println("DownLoadFileServlet�����ļ���" + str_fileName + "��(�ӻ���ȡ)"); //
			} else { //�����Ǵ�Ӳ�̶��ļ�
				String str_newWebUrl = ServerEnvironment.getProperty("FullTextSearchURL"); //�ʴ��м�Ⱥʱ����ʹ��nfs��ftp,����ֻ��http����һ̨�����ϴ�ȡ...
				if (str_newWebUrl != null) { //����������Ǵ�Զ����ȡ
					//System.out.println("��Ϊ������FullTextSearchURL=[" + str_newWebUrl + "],����Ҫת��Զ��ȡ�ļ�[" + str_fileName + "]..."); //
					HashMap requestMap = new HashMap();
					requestMap.put("Action", "download"); //
					requestMap.put("FileName", str_downloadFileName); //������·��ȫ��

					HashMap rtMap = new BSUtil().callWebUrl(str_newWebUrl + "/UploadFileServlet", requestMap); //

					if ("Y".equals(rtMap.get("isException"))) { //��������쳣
						Exception ex = (Exception) rtMap.get("Exception"); //
						throw new Exception("Զ�������ļ������쳣", ex); //
					}
					bytes = (byte[]) rtMap.get("ByteCodes"); //���ص��ֽ���!!
					if (str_iscachefile != null && str_iscachefile.equalsIgnoreCase("Y")) { //�����ָ���˻���,���������뻺��
						hm_FileCache.put(str_fileName, bytes); //
					}
				} else {
					File file = new File(str_downloadFileName); //�����ļ�����
					if (file.exists()) { //����ļ���Ŀ¼��!
						fileIn = new FileInputStream(file); //�����ļ���ȡ������
						ByteArrayOutputStream bout = new ByteArrayOutputStream(); //
						byte[] tmpBys = new byte[5012]; //ԭ����2048,���ڸ���!! ��������,����Щ,���������ڴ濪��!
						int li_pos = -1; //
						while ((li_pos = fileIn.read(tmpBys)) != -1) { //ѭ�������ļ�,һ�ζ�2���ֽ�,����ʽ��ȡ!!!������Щ!!��ǰ��һ���Ӷ���һ����������,�������ܲ��ȶ�,��û��֤��!! ֻ��JDK�ı�׼�������������ֶ���!!!
							bout.write(tmpBys, 0, li_pos); //����,�ؼ�!!!����ָ��λ��!!
						}
						bytes = bout.toByteArray(); //�õ��ֽ�!!!
						try {
							bout.close(); //�ر������!
						} catch (Exception e) {
							e.printStackTrace(); //
						}
						try {
							fileIn.close(); //�����ر����ͷ�
							fileIn = null; //
						} catch (Exception e) {
							e.printStackTrace(); //
						}
						System.out.println("DownLoadFileServlet�����ļ���" + str_downloadFileName + "��(��Ӳ�̶�)"); //
						if (str_iscachefile != null && str_iscachefile.equalsIgnoreCase("Y")) { //�����ָ���˻���,���������뻺��
							hm_FileCache.put(str_fileName, bytes); //
						}
					} else { //����������ݿ���ȡ!!!����������µĹ���!!
						System.out.println("��Ӳ����û�����ļ�[" + str_downloadFileName + "],ת������ݿ��,�Ƿ����뻺��=[" + str_iscachefile + "]"); //
						bytes = new BSUtil().getFileContentFromDB(str_fileName, "��Ӳ����û�����ļ�[" + new TBUtil().replaceHtmlEncode(str_downloadFileName) + "],ת������ݿ��..."); //by haoming 20160519���ǰ̨��js©��
						if (str_iscachefile != null && str_iscachefile.equalsIgnoreCase("Y")) { //�����ָ���˻���,���������뻺��
							hm_FileCache.put(str_fileName, bytes); //���뻺��!!!
						}
					}
				}
			}

			if (bytes == null) {
				writeErrorMsg(_response, "�ļ���" + str_downloadFileName + "��ȡ��������Ϊ��!!"); ////
				return;
			}

			//��������ͻ��������ķ�ʽ����ļ���
			_response.setBufferSize(51200); //��������Ϊ50K,��ǰ��chunkedģʽ,�϶�����!
			_response.setContentLength(bytes.length); //һ��Ҫ����,������ܱ��[Transfer-Encoding]=[chunked],Ȼ�������½�,�ر����е�Win7������...
			setMimeType(_response, str_realfilename, bytes.length); //����Mime����,��ؼ�!!!!!!!!!!
			resOutStream = _response.getOutputStream(); //�õ������,�����������������ݺ�������
			int li_start = 0; //����������ʱ�ֲ�����
			int li_onecyclesize = 5012; //ÿ��ѭ��������!!һ����2048!! ������������,��ͨ�������ֱ�����رȽ�,������2048��û����������,�����5012,����������! ��˵���������⣺һ�������㻹����Ч���ģ��������ǵĴ�������IE�����ܲ������µ�!����û��Ӳ�˵�!
			while (1 == 1) { //����ѭ��,��ͻ����������,ֱ�����ݽ���������ͨ��ÿ���1K���ݾ���Ϣһ��ʱ��ķ�ʽ����������
				int li_end = li_start + li_onecyclesize; //�����β����
				if (li_end >= bytes.length - 1) { //�����β����λ���ѳ����ļ�����
					resOutStream.write(bytes, li_start, bytes.length - li_start); //
					//System.out.println("������ʣ���" + (bytes.length - li_start) + "�ֽڵ�����..");
					break; //�ж�ѭ��
				} else {
					resOutStream.write(bytes, li_start, li_onecyclesize); //ÿ��ѭ��ֻ���1024�ֽڵ�����,��1K������
					li_start = li_start + li_onecyclesize; //λ�������1024
					//System.out.println("��[" + (li_start / 1024) + "]�����1024�ֽڵ�����..");
				}
			}
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
			_response.setContentType("text/html"); //
			_response.setCharacterEncoding("GBK"); //
			_response.getWriter().println(new TBUtil().getExceptionStringBuffer(_ex, true, true)); //
		} finally {
			if (threadLimit > 0) {
				DownLoadFileServlet.threadCount--; //���������ȫ�ֱ����м�1,������������������...
			}
			try {
				if (fileIn != null) {
					fileIn.close(); //
				}
			} catch (Exception e) {
			}
			try {
				if (resOutStream != null) {
					resOutStream.flush(); //
					resOutStream.close(); //
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * ����Mime����.
	 * @param _response
	 * @param _realfilename
	 * @param _length
	 */
	private void setMimeType(HttpServletResponse _response, String _realfilename, int _length) throws Exception {
		if (_realfilename.lastIndexOf(".") < 0) { //���û�к��
			_response.setContentType("application/octet-stream"); //������������
			_response.setHeader("Content-Disposition", "attachment; filename=" + convertFileName(_realfilename));
		} else {
			String str_filetype = _realfilename.substring(_realfilename.lastIndexOf(".") + 1, _realfilename.length()); //
			String[] str_headerType = new BSUtil().getMimeTypeByDocType(str_filetype, convertFileName(_realfilename)); //
			_response.setContentType(str_headerType[0]); //�����������
			_response.setHeader("Content-Disposition", str_headerType[1]); //�����������!!!
		}
		_response.setContentLength(_length); //�����������
	}

	//����ļ�����16���Ƶ���ν����,����Ҫת���ɿ��ö�������!!
	private String convertFileName(String _realfilename) throws UnsupportedEncodingException {
		TBUtil tbUtil = new TBUtil(); //
		int li_pos = _realfilename.indexOf("_"); //���Ƿ����»���
		if (li_pos < 0) {
			li_pos = -1;
		}
		int li_pos2 = _realfilename.lastIndexOf("."); //
		String str_masterName = _realfilename.substring(li_pos + 1, li_pos2 >= 0 ? li_pos2 : _realfilename.length()); //����
		if (tbUtil.isHexStr(str_masterName)) { //���������16����������
			str_masterName = tbUtil.convertHexStringToStr(str_masterName); //ת��!
			if (li_pos2 >= 0) {
				_realfilename = str_masterName + "." + _realfilename.substring(li_pos2 + 1, _realfilename.length()); //ȫ��!!
			} else {
				_realfilename = str_masterName;
			}
		} else { //�������16����,����Ȼ��������,��ϵͳ����һ����
			String str_className = tbUtil.getSysOptionStringValue("��Ŀ�Զ��帽���ļ�����ת����", ""); //"cn.com.infostrategy.bs.common.ConvertRefFileNameUtil"; //���������ת������!
			if (str_className != null && !str_className.trim().equals("")) { //�������!!
				String str_newName = (String) new TBUtil().refectCallClassStaticMethod(str_className, "convertName", new String[] { _realfilename }); //
				if (str_newName != null) {
					_realfilename = str_newName; //ת��һ��!
				}
			} else {
				if (li_pos2 >= 0) {
					_realfilename = str_masterName + "." + _realfilename.substring(li_pos2 + 1, _realfilename.length()); //ȫ��!!
				} else {
					_realfilename = str_masterName;
				}
			}
		}
		//System.out.println("��ͻ����������ʵ�ļ���[" + _realfilename + "]"); //
		_realfilename = new String(_realfilename.getBytes(), "ISO-8859-1"); //��������תһ��,��������ʾ����!������IE9�л���������ز��˵Ĵ���!!
		return _realfilename; //
	}

	//���������Ϣ..
	private void writeErrorMsg(HttpServletResponse _response, String _message) throws Exception {
		_response.setContentType("text/html"); //
		_response.setCharacterEncoding("GBK"); //
		_response.getWriter().println("<html><body><font size=2 color=\"red\">" + new TBUtil().replaceHtmlEncode(_message) + "</font></body></html>"); //���������������������ʾ�Ժ�����.
	}
}
