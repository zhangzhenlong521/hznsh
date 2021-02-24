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
 * 处理Office的控件
 * 利用金格科技的第三方技术
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

	//打印控制
	private String mOfficePrints;
	private int mCopies;
	//自定义信息传递
	private String mInfo;
	private iMsgServer2000 MsgObj;

	/**
	 * 重构service方法
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setBufferSize(51200);  //
		executeRun(request, response); //
	}

	/**
	 * 实际处理远程调用处理
	 * @param request
	 * @param response
	 */
	public void executeRun(HttpServletRequest request, HttpServletResponse response) {
		MsgObj = new iMsgServer2000(); //创建信息包对象,这是金格提供的一个类

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

		mFilePath = request.getSession().getServletContext().getRealPath(""); //取得服务器路径
		try {
			if (request.getMethod().equalsIgnoreCase("POST")) {
				MsgObj.MsgVariant(readPackage(request)); //将客户端的请求包装成一个对象!!!
				if (MsgObj.GetMsgByName("DBSTEP").equalsIgnoreCase("DBSTEP")) { //如果是合法的信息包
					mOption = MsgObj.GetMsgByName("OPTION"); //取得操作信息
					mUserName = MsgObj.GetMsgByName("USERNAME"); //取得系统用户
					//System.out.println(mOption); //打印出调试信息
					if (mOption.equalsIgnoreCase("LOADFILE")) { //如果是从服务器端加载文件
						loadFile(); //加载文件..
					} else if (mOption.equalsIgnoreCase("SAVEFILE")) { //如果是保存文件
						saveFiel(); //上传保存文件..
					} else if (mOption.equalsIgnoreCase("INSERTIMAGE")) { // 下面的代码为插入服务器图片
						mRecordID = MsgObj.GetMsgByName("RECORDID"); // 取得文档编号
						mLabelName = MsgObj.GetMsgByName("LABELNAME"); // 标签名
						mImageName = MsgObj.GetMsgByName("IMAGENAME"); // 图片名
						//						mFilePath = mFilePath + "\\Document\\" + mImageName; // 图片在服务器的完整路径
						mFilePath = mFilePath + "\\" + mImageName; // 图片在服务器的完整路径
						mFileType = mImageName.substring(mImageName.length() - 4).toLowerCase(); // 取得文件的类型
						System.out.println(mFilePath);
						System.out.println(mFileType);
						MsgObj.MsgTextClear();
						if (MsgObj.MsgFileLoad(mFilePath)) { // 调入图片
							MsgObj.SetMsgByName("IMAGETYPE", mFileType); // 指定图片的类型
							MsgObj.SetMsgByName("POSITION", mLabelName); // 设置插入的位置[书签对象名]
							MsgObj.SetMsgByName("STATUS", "插入图片成功!"); // 设置状态信息
							System.out.println(MsgObj.GetMsgByName("STATUS"));
							MsgObj.MsgError(""); // 清除错误信息
						} else {
							MsgObj.MsgError("插入图片失败!"); // 设置错误信息
						}
					}
				} else {
					MsgObj.MsgError("客户端发送数据包错误!");
					MsgObj.MsgTextClear();
					MsgObj.MsgFileClear();
				}
			} else {
				MsgObj.MsgError("请使用Post方法");
				MsgObj.MsgTextClear();
				MsgObj.MsgFileClear();
			}
			sendPackage(response); //向客户端输出消息包,这个包是经过压缩过的!
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * 打开一个文件
	 */
	private void loadFile() throws Exception {
		mRecordID = MsgObj.GetMsgByName("RECORDID"); //取得文档编号
		mFileName = MsgObj.GetMsgByName("FILENAME"); //取得文档名称
		mFileType = MsgObj.GetMsgByName("FILETYPE"); //取得文档类型
		mFileTempletName = MsgObj.GetMsgByName("TEMPLATE"); //模板文件名

		MsgObj.MsgTextClear(); //清除文本信息
		String str_filename = null;
		if (subdir == null) {
			str_filename = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile" + "/" + mFileName; //
		} else {
			str_filename = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/" + subdir + "/" + mFileName; //
		}
		System.out.println("准备加载文件[" + str_filename + "]!"); //
		File downloadfile = new File(str_filename); //
		if (downloadfile.exists()) { //如果文件存在,则加载之!!!
			FileInputStream fins = new FileInputStream(downloadfile);
			int filelength = new Long(downloadfile.length()).intValue();
			byte[] filecontent = new byte[filelength];
			fins.read(filecontent);
			MsgObj.MsgFileBody(filecontent); //将文件信息打包
			fins.close(); //
			System.out.println("加载文件[" + str_filename + "]成功!"); //
		} else { //如果文件不存在,则将模板文件复制成该文件,然后加载之!
			if (mFileTempletName != null && !mFileTempletName.trim().equals("")) { //如果定义了模板,则看没有模板
				java.net.URL fileurl = this.getClass().getResource("/" + mFileTempletName + mFileType); //
				if (fileurl != null) { //如果有该文件
					File file_templet = new File(fileurl.toURI()); //取资源文件,即从ClassPATH路径中取.
					FileInputStream fin_templet = new FileInputStream(file_templet); //读出模板文件
					int filelength_templet = new Long(file_templet.length()).intValue();
					byte[] filecontent_templet = new byte[filelength_templet];
					fin_templet.read(filecontent_templet); //
					fin_templet.close(); //

					//先复制一份
					FileOutputStream fout_newfile = new FileOutputStream(str_filename, false);
					fout_newfile.write(filecontent_templet); //需要解压这个数据包,否则生成的word文件用Word是打不开的!
					fout_newfile.close(); //

					MsgObj.MsgFileBody(filecontent_templet); //将文件信息打包
					System.out.println("加载文件[" + str_filename + "]时发现该文件不存在,定义了模板文件[" + mFileTempletName + "],成功使用模板文件复制之!"); //
				} else {
					System.out.println("加载文件[" + str_filename + "]时发现该文件不存在,定义了模板文件[" + mFileTempletName + "],同时也没有发现该模板文件,使用空白文件!"); //
				}
			} else {
				System.out.println("加载文件[" + str_filename + "]时发现该文件不存在,也没有定义模板文件,使用空白文件!"); //
			}
		}
		MsgObj.SetMsgByName("STATUS", "打开成功!"); //设置状态信息
		MsgObj.MsgError(""); //清除错误信息
	}

	/**
	 * 上传保存文件..
	 * @throws Exception
	 */
	private void saveFiel() throws Exception {
		mRecordID = MsgObj.GetMsgByName("RECORDID"); //取得文档编号
		mFileName = MsgObj.GetMsgByName("FILENAME"); //取得文档名称
		mFileType = MsgObj.GetMsgByName("FILETYPE"); //取得文档类型
		mFileSize = MsgObj.MsgFileSize(); //取得文档大小
		mFileBody = MsgObj.MsgFileBody(); //取得文件内容
		mFilePath = ""; //如果保存为文件，则填写文件路径
		mDescript = "通用版本"; //版本说明

		//将mFileBody中的内容保存到本地目录下!!
		File filedir = null;
		if (subdir == null) {
			filedir = new File(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile"); //
		} else {
			filedir = new File(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/" + subdir); //
		}
		if (!filedir.exists()) {
			filedir.mkdirs(); //如果不存在,则创建该目录!
		}

		File uploadfile = new File(filedir + "/" + mFileName); //
		FileOutputStream fout = new FileOutputStream(uploadfile, false);
		fout.write(MsgObj.ToDocument(mFileBody)); //需要解压这个数据包,否则生成的word文件用Word是打不开的!
		fout.close(); //
		MsgObj.MsgFileClear(); //清除文档内容
		MsgObj.MsgTextClear(); //清除文本信息
		System.out.println("上传文件[" + uploadfile.getAbsolutePath() + "]成功!!"); //
	}

	/**
	 * 接收从客户端发过来的Word文件数据包!!
	 * 客户端是C语言写的Activex控件,通过读取对应的word文件，取得其二进制数据内容，然后用http协议发送到服务器端来!
	 * 本质上是一个基于Http协议的C语言客户端与Java语言服务器端的通讯过程!!!
	 * @param request
	 * @return 从客户端传来的二进制数据包!
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
	 * 服务器端往客户端发送数据包!!!
	 * 客户端是C语言写的Activex接收到该数据包,然后生成一个Word文件，然后调用客户端系统的Word内核打开这个word文件!!
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
