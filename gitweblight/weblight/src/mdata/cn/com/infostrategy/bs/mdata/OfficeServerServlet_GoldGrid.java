package cn.com.infostrategy.bs.mdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import DBstep.iMsgServer2000;
import cn.com.infostrategy.bs.common.ServerEnvironment;

/**
 * ����Office�Ŀؼ�
 * ���ý��Ƽ��ĵ���������
 * @author xch
 *
 */
public class OfficeServerServlet_GoldGrid extends HttpServlet {

	private int mFileSize;
	private byte[] mFileBody;
	private String mFileName;
	private String mFileType;
	private String mFileDate;
	private String mFileID;
	private String mFileTempletName; //

	private String mRecordID;
	private String mTemplate;
	private String mDateTime;
	private String mOption;
	private String mMarkName;
	private String mPassword;
	private String mMarkList;
	private String mBookmark;
	private String mDescript;
	private String mHostName;
	private String mMarkGuid;
	private String mCommand;
	private String mContent;
	private String mHtmlName;
	private String mDirectory;
	private String mFilePath;
	private String subdir;

	private String mUserName;
	private int mColumns;
	private int mCells;
	private String mMyDefine1;
	private String mLocalFile;
	private String mRemoteFile;
	private String mLabelName;
	private String mImageName;
	private String mTableContent;

	private String Sql;

	//��ӡ����
	private String mOfficePrints;
	private int mCopies;
	//�Զ�����Ϣ����
	private String mInfo;
	private iMsgServer2000 MsgObj;

	/**
	 * �ع�service����
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setBufferSize(51200);  //
		executeRun(request, response); //
	}

	/**
	 * ʵ�ʴ���Զ�̵��ô���
	 * @param request
	 * @param response
	 */
	public void executeRun(HttpServletRequest request, HttpServletResponse response) {
		MsgObj = new iMsgServer2000(); //������Ϣ������,���ǽ���ṩ��һ����

		mOption = "";
		mRecordID = "";
		mTemplate = "";
		mFileBody = null;
		mFileName = "";
		mFileType = "";
		mFileSize = 0;
		mFileID = "";
		mDateTime = "";
		mMarkName = "";
		mPassword = "";
		mMarkList = "";
		mBookmark = "";
		mMarkGuid = "";
		mDescript = "";
		mCommand = "";
		mContent = "";
		mLabelName = "";
		mImageName = "";
		mTableContent = "";
		mMyDefine1 = "";
		mOfficePrints = "0";
		subdir = request.getParameter("subdir");

		mFilePath = request.getSession().getServletContext().getRealPath(""); //ȡ�÷�����·��
		try {
			if (request.getMethod().equalsIgnoreCase("POST")) {
				MsgObj.MsgVariant(readPackage(request)); //���ͻ��˵������װ��һ������!!!
				if (MsgObj.GetMsgByName("DBSTEP").equalsIgnoreCase("DBSTEP")) { //����ǺϷ�����Ϣ��
					mOption = MsgObj.GetMsgByName("OPTION"); //ȡ�ò�����Ϣ
					mUserName = MsgObj.GetMsgByName("USERNAME"); //ȡ��ϵͳ�û�
					//System.out.println(mOption); //��ӡ��������Ϣ
					if (mOption.equalsIgnoreCase("LOADFILE")) { //����Ǵӷ������˼����ļ�
						loadFile(); //�����ļ�..
					} else if (mOption.equalsIgnoreCase("SAVEFILE")) { //����Ǳ����ļ�
						saveFiel(); //�ϴ������ļ�..
					} else if (mOption.equalsIgnoreCase("INSERTIMAGE")) { // ����Ĵ���Ϊ���������ͼƬ
						mRecordID = MsgObj.GetMsgByName("RECORDID"); // ȡ���ĵ����
						mLabelName = MsgObj.GetMsgByName("LABELNAME"); // ��ǩ��
						mImageName = MsgObj.GetMsgByName("IMAGENAME"); // ͼƬ��
						//						mFilePath = mFilePath + "\\Document\\" + mImageName; // ͼƬ�ڷ�����������·��
						mFilePath = mFilePath + "\\" + mImageName; // ͼƬ�ڷ�����������·��
						mFileType = mImageName.substring(mImageName.length() - 4).toLowerCase(); // ȡ���ļ�������
						System.out.println(mFilePath);
						System.out.println(mFileType);
						MsgObj.MsgTextClear();
						if (MsgObj.MsgFileLoad(mFilePath)) { // ����ͼƬ
							MsgObj.SetMsgByName("IMAGETYPE", mFileType); // ָ��ͼƬ������
							MsgObj.SetMsgByName("POSITION", mLabelName); // ���ò����λ��[��ǩ������]
							MsgObj.SetMsgByName("STATUS", "����ͼƬ�ɹ�!"); // ����״̬��Ϣ
							System.out.println(MsgObj.GetMsgByName("STATUS"));
							MsgObj.MsgError(""); // ���������Ϣ
						} else {
							MsgObj.MsgError("����ͼƬʧ��!"); // ���ô�����Ϣ
						}
					}
				} else {
					MsgObj.MsgError("�ͻ��˷������ݰ�����!");
					MsgObj.MsgTextClear();
					MsgObj.MsgFileClear();
				}
			} else {
				MsgObj.MsgError("��ʹ��Post����");
				MsgObj.MsgTextClear();
				MsgObj.MsgFileClear();
			}
			sendPackage(response); //��ͻ��������Ϣ��,������Ǿ���ѹ������!
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * ��һ���ļ�
	 */
	private void loadFile() throws Exception {
		mRecordID = MsgObj.GetMsgByName("RECORDID"); //ȡ���ĵ����
		mFileName = MsgObj.GetMsgByName("FILENAME"); //ȡ���ĵ�����
		mFileType = MsgObj.GetMsgByName("FILETYPE"); //ȡ���ĵ�����
		mFileTempletName = MsgObj.GetMsgByName("TEMPLATE"); //ģ���ļ���

		MsgObj.MsgTextClear(); //����ı���Ϣ
		String str_filename = null;
		if (subdir == null) {
			str_filename = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile" + "/" + mFileName; //
		} else {
			str_filename = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/" + subdir + "/" + mFileName; //
		}
		System.out.println("׼�������ļ�[" + str_filename + "]!"); //
		File downloadfile = new File(str_filename); //
		if (downloadfile.exists()) { //����ļ�����,�����֮!!!
			FileInputStream fins = new FileInputStream(downloadfile);
			int filelength = new Long(downloadfile.length()).intValue();
			byte[] filecontent = new byte[filelength];
			fins.read(filecontent);
			MsgObj.MsgFileBody(filecontent); //���ļ���Ϣ���
			fins.close(); //
			System.out.println("�����ļ�[" + str_filename + "]�ɹ�!"); //
		} else { //����ļ�������,��ģ���ļ����Ƴɸ��ļ�,Ȼ�����֮!
			if (mFileTempletName != null && !mFileTempletName.trim().equals("")) { //���������ģ��,��û��ģ��
				java.net.URL fileurl = this.getClass().getResource("/" + mFileTempletName + mFileType); //
				if (fileurl != null) { //����и��ļ�
					File file_templet = new File(fileurl.toURI()); //ȡ��Դ�ļ�,����ClassPATH·����ȡ.
					FileInputStream fin_templet = new FileInputStream(file_templet); //����ģ���ļ�
					int filelength_templet = new Long(file_templet.length()).intValue();
					byte[] filecontent_templet = new byte[filelength_templet];
					fin_templet.read(filecontent_templet); //
					fin_templet.close(); //

					//�ȸ���һ��
					FileOutputStream fout_newfile = new FileOutputStream(str_filename, false);
					fout_newfile.write(filecontent_templet); //��Ҫ��ѹ������ݰ�,�������ɵ�word�ļ���Word�Ǵ򲻿���!
					fout_newfile.close(); //

					MsgObj.MsgFileBody(filecontent_templet); //���ļ���Ϣ���
					System.out.println("�����ļ�[" + str_filename + "]ʱ���ָ��ļ�������,������ģ���ļ�[" + mFileTempletName + "],�ɹ�ʹ��ģ���ļ�����֮!"); //
				} else {
					System.out.println("�����ļ�[" + str_filename + "]ʱ���ָ��ļ�������,������ģ���ļ�[" + mFileTempletName + "],ͬʱҲû�з��ָ�ģ���ļ�,ʹ�ÿհ��ļ�!"); //
				}
			} else {
				System.out.println("�����ļ�[" + str_filename + "]ʱ���ָ��ļ�������,Ҳû�ж���ģ���ļ�,ʹ�ÿհ��ļ�!"); //
			}
		}
		MsgObj.SetMsgByName("STATUS", "�򿪳ɹ�!"); //����״̬��Ϣ
		MsgObj.MsgError(""); //���������Ϣ
	}

	/**
	 * �ϴ������ļ�..
	 * @throws Exception
	 */
	private void saveFiel() throws Exception {
		mRecordID = MsgObj.GetMsgByName("RECORDID"); //ȡ���ĵ����
		mFileName = MsgObj.GetMsgByName("FILENAME"); //ȡ���ĵ�����
		mFileType = MsgObj.GetMsgByName("FILETYPE"); //ȡ���ĵ�����
		mFileSize = MsgObj.MsgFileSize(); //ȡ���ĵ���С
		mFileBody = MsgObj.MsgFileBody(); //ȡ���ļ�����
		mFilePath = ""; //�������Ϊ�ļ�������д�ļ�·��
		mDescript = "ͨ�ð汾"; //�汾˵��

		//��mFileBody�е����ݱ��浽����Ŀ¼��!!
		File filedir = null;
		if (subdir == null) {
			filedir = new File(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile"); //
		} else {
			filedir = new File(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/" + subdir); //
		}
		if (!filedir.exists()) {
			filedir.mkdirs(); //���������,�򴴽���Ŀ¼!
		}

		File uploadfile = new File(filedir + "/" + mFileName); //
		FileOutputStream fout = new FileOutputStream(uploadfile, false);
		fout.write(MsgObj.ToDocument(mFileBody)); //��Ҫ��ѹ������ݰ�,�������ɵ�word�ļ���Word�Ǵ򲻿���!
		fout.close(); //
		MsgObj.MsgFileClear(); //����ĵ�����
		MsgObj.MsgTextClear(); //����ı���Ϣ
		System.out.println("�ϴ��ļ�[" + uploadfile.getAbsolutePath() + "]�ɹ�!!"); //
	}

	/**
	 * ���մӿͻ��˷�������Word�ļ����ݰ�!!
	 * �ͻ�����C����д��Activex�ؼ�,ͨ����ȡ��Ӧ��word�ļ���ȡ����������������ݣ�Ȼ����httpЭ�鷢�͵�����������!
	 * ��������һ������HttpЭ���C���Կͻ�����Java���Է������˵�ͨѶ����!!!
	 * @param request
	 * @return �ӿͻ��˴����Ķ��������ݰ�!
	 */
	private byte[] readPackage(HttpServletRequest request) {
		byte mStream[] = null;
		int totalRead = 0;
		int readBytes = 0;
		int totalBytes = 0;
		try {
			totalBytes = request.getContentLength();
			mStream = new byte[totalBytes];
			while (totalRead < totalBytes) {
				request.getInputStream();
				readBytes = request.getInputStream().read(mStream, totalRead, totalBytes - totalRead);
				totalRead += readBytes;
				continue;
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}
		return (mStream);
	}

	/**
	 * �����������ͻ��˷������ݰ�!!!
	 * �ͻ�����C����д��Activex���յ������ݰ�,Ȼ������һ��Word�ļ���Ȼ����ÿͻ���ϵͳ��Word�ں˴����word�ļ�!!
	 * @param response
	 */
	private void sendPackage(HttpServletResponse response) {
		try {
			ServletOutputStream OutBinarry = response.getOutputStream();
			byte[] bytes = MsgObj.MsgVariant(); //
			response.setContentLength(bytes.length); //
			OutBinarry.write(bytes);
			OutBinarry.flush();
			OutBinarry.close();
		} catch (IOException e) {
			e.printStackTrace(); //
		}
	}

}
