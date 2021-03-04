package cn.com.infostrategy.bs.common;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpClient;

/**
 * FTP工具!!! 主要有上传与下载FTP两个方法!!
 * 我们上传附件以前都是一个目录,但在集群部署时,需要专门将附件存储在另一台服务器上!
 * 一种办法是映射一个网络驱动器,直接上传! 另一种办法上建一个FTP服务器,通过Ftp上传!
 * 如果使用FTP上传就需要使用到这个类中的方法! 即服务器端拿到文件内容后再次使用FTP上传到FTP服务器上!
 * 我们使用的是标准的JDK中自带的sun.net.ftp.FtpClient,其他开源的FTP工具类多的是,比如Apache中就有一个!
 * @author xch
 *
 */
public class FtpDealUtil {

	public FtpDealUtil() {
	}

	/**
	 * @param _ftpServerUrl  格式是[mike:mike123@192.68.0.9:21]
	 * @param sourceData
	 * @param _fileName
	 * @throws Exception
	 */
	public void upFile(String _ftpServerUrl, byte[] sourceData, String _fileName) throws Exception {
		try {
			int li_ps = _ftpServerUrl.indexOf("@"); //
			String str_1 = _ftpServerUrl.substring(0, li_ps); //
			String str_2 = _ftpServerUrl.substring(li_ps + 1, _ftpServerUrl.length()); //

			String str_ip = str_2.substring(0, str_2.indexOf(":")); //
			int port = Integer.parseInt(str_2.substring(str_2.indexOf(":") + 1, str_2.length())); //

			String str_user = str_1.substring(0, str_1.indexOf(":")); //
			String str_pwd = str_1.substring(str_1.indexOf(":") + 1, str_1.length()); //

			upFile(str_ip, port, str_user, str_pwd, sourceData, _fileName); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	* 将一个二进制内容以某个文件名上传到FTP服务器上,!!!
	* 上传文件只能使用二进制模式，当文件存在时再次上传则会覆盖!!!
	* _fileName是从根目录开始带路径的,比如[/dir1/dir2/test.txt] ,如果文件想直接存储在根目录下,则也要写[/test.txt]
	*/
	public void upFile(String _ip, int _port, String _user, String _pwd, byte[] _dataBytes, String _fileName) throws Exception {
		FtpClient ftpclient = new FtpClient(_ip, _port); //创建FTP连接对象!!
		ftpclient.login(_user, _pwd); //连接..

		mkDirRes(ftpclient, _fileName.substring(0, _fileName.lastIndexOf("/"))); //先递归创建目录!!
		ftpclient.binary(); //此行代码必须放在buildList之后 !!

		TelnetOutputStream ftpOut = ftpclient.put(_fileName);
		ftpOut.write(_dataBytes, 0, _dataBytes.length); //写数据!
		//ftpOut.flush();
		ftpOut.close();

		//退出FTP连接!!
		ftpclient.sendServer("QUIT\r\n");
		int reply = ftpclient.readServerResponse(); //取得服务器的返回信息
	}

	/**
	 * 从FTP服务器上下载某个文件
	 * @param _fileName  是从根目录开始的某个文件名,比如[/dir1/dir2/test.txt]
	 * @return 返回数据内容!! byte[]
	 * @throws Exception
	 */
	public byte[] downFile(String _ip, int _port, String _user, String _pwd, String _fileName) throws Exception {
		FtpClient ftpclient = new FtpClient(_ip, _port); //创建FTP连接对象!!
		ftpclient.login(_user, _pwd); //连接..

		ftpclient.binary(); //一定要使用二进制模式!!

		TelnetInputStream ftpIn = ftpclient.get(_fileName);
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		byte[] buf = new byte[204800];
		int bufsize = 0;

		while ((bufsize = ftpIn.read(buf, 0, buf.length)) != -1) {
			byteOut.write(buf, 0, bufsize);
		}
		byte[] return_arraybyte = byteOut.toByteArray();
		byteOut.close();
		ftpIn.close();

		//退出Ftp连接
		ftpclient.sendServer("QUIT\r\n");
		int reply = ftpclient.readServerResponse(); //取得服务器的返回信息
		return return_arraybyte;
	}

	/**
	* 在FTP服务器上建立指定的目录,当目录已经存在的情下不会影响目录下的文件,这样用以判断FTP
	* 上传文件时保证目录的存在目录格式必须以"/"根目录开头
	* @param pathList String
	* @throws Exception
	*/
	public void mkDirRes(FtpClient _ftpclient, String _path) throws Exception {
		_ftpclient.ascii();
		StringTokenizer s = new StringTokenizer(_path, "/"); //sign
		int count = s.countTokens(); //
		String pathName = "";
		while (s.hasMoreElements()) {
			pathName = pathName + "/" + (String) s.nextElement();
			try {
				_ftpclient.sendServer("XMKD " + pathName + "\r\n"); //创建目录!!!
			} catch (Exception e) {
				e = null;
			}
			int reply = _ftpclient.readServerResponse();
		}
		_ftpclient.binary();
	}

	/**
	* 取得指定目录下的所有文件名，不包括目录名称
	* 分析nameList得到的输入流中的数，得到指定目录下的所有文件名
	* @param fullPath String
	* @return ArrayList
	* @throws Exception
	*/
	public String[] listFileNames(String _ip, int _port, String _user, String _pwd, String _fullPath) throws Exception {
		FtpClient ftpclient = new FtpClient(_ip, _port); //创建FTP连接对象!!
		ftpclient.login(_user, _pwd); //连接..

		ftpclient.ascii(); //注意，使用字符模式
		TelnetInputStream list = ftpclient.nameList(_fullPath); //
		byte[] names = new byte[20480];
		int bufsize = 0;
		bufsize = list.read(names, 0, names.length); //从流中读取
		list.close();
		ArrayList namesList = new ArrayList();
		int i = 0;
		int j = 0;
		while (i < bufsize) {
			if (names[i] == 10) { //字符模式为10，二进制模式为13
				String tempName = new String(names, j, i - j);
				namesList.add(tempName);
				j = i + 1; //上一次位置字符模式
			}
			i = i + 1;
		}

		//退出Ftp连接
		ftpclient.sendServer("QUIT\r\n");
		int reply = ftpclient.readServerResponse(); //取得服务器的返回信息

		return (String[]) namesList.toArray(new String[0]);
	}

	//例子..
	public static void main(String[] _args) {
		try {
			//FtpDealUtil myFtp = new FtpDealUtil();
			//byte[] data = new TBUtil().readFromInputStreamToBytes(new FileInputStream("C:/123.txt")); //
			//myFtp.upFile("127.0.0.1", 21, "mike", "mike123", data, "/k1/k2/k3/kk.txt"); //
			System.out.println("上传文件成功"); //上传成功!!!
		} catch (Exception e) {
			e.printStackTrace();
		} //
	}

}
