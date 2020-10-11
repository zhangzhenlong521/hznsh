package cn.com.infostrategy.bs.common;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;
import sun.net.ftp.FtpClient;

/**
 * FTP����!!! ��Ҫ���ϴ�������FTP��������!!
 * �����ϴ�������ǰ����һ��Ŀ¼,���ڼ�Ⱥ����ʱ,��Ҫר�Ž������洢����һ̨��������!
 * һ�ְ취��ӳ��һ������������,ֱ���ϴ�! ��һ�ְ취�Ͻ�һ��FTP������,ͨ��Ftp�ϴ�!
 * ���ʹ��FTP�ϴ�����Ҫʹ�õ�������еķ���! �����������õ��ļ����ݺ��ٴ�ʹ��FTP�ϴ���FTP��������!
 * ����ʹ�õ��Ǳ�׼��JDK���Դ���sun.net.ftp.FtpClient,������Դ��FTP����������,����Apache�о���һ��!
 * @author xch
 *
 */
public class FtpDealUtil {

	public FtpDealUtil() {
	}

	/**
	 * @param _ftpServerUrl  ��ʽ��[mike:mike123@192.68.0.9:21]
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
	* ��һ��������������ĳ���ļ����ϴ���FTP��������,!!!
	* �ϴ��ļ�ֻ��ʹ�ö�����ģʽ�����ļ�����ʱ�ٴ��ϴ���Ḳ��!!!
	* _fileName�ǴӸ�Ŀ¼��ʼ��·����,����[/dir1/dir2/test.txt] ,����ļ���ֱ�Ӵ洢�ڸ�Ŀ¼��,��ҲҪд[/test.txt]
	*/
	public void upFile(String _ip, int _port, String _user, String _pwd, byte[] _dataBytes, String _fileName) throws Exception {
		FtpClient ftpclient = new FtpClient(_ip, _port); //����FTP���Ӷ���!!
		ftpclient.login(_user, _pwd); //����..

		mkDirRes(ftpclient, _fileName.substring(0, _fileName.lastIndexOf("/"))); //�ȵݹ鴴��Ŀ¼!!
		ftpclient.binary(); //���д���������buildList֮�� !!

		TelnetOutputStream ftpOut = ftpclient.put(_fileName);
		ftpOut.write(_dataBytes, 0, _dataBytes.length); //д����!
		//ftpOut.flush();
		ftpOut.close();

		//�˳�FTP����!!
		ftpclient.sendServer("QUIT\r\n");
		int reply = ftpclient.readServerResponse(); //ȡ�÷������ķ�����Ϣ
	}

	/**
	 * ��FTP������������ĳ���ļ�
	 * @param _fileName  �ǴӸ�Ŀ¼��ʼ��ĳ���ļ���,����[/dir1/dir2/test.txt]
	 * @return ������������!! byte[]
	 * @throws Exception
	 */
	public byte[] downFile(String _ip, int _port, String _user, String _pwd, String _fileName) throws Exception {
		FtpClient ftpclient = new FtpClient(_ip, _port); //����FTP���Ӷ���!!
		ftpclient.login(_user, _pwd); //����..

		ftpclient.binary(); //һ��Ҫʹ�ö�����ģʽ!!

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

		//�˳�Ftp����
		ftpclient.sendServer("QUIT\r\n");
		int reply = ftpclient.readServerResponse(); //ȡ�÷������ķ�����Ϣ
		return return_arraybyte;
	}

	/**
	* ��FTP�������Ͻ���ָ����Ŀ¼,��Ŀ¼�Ѿ����ڵ����²���Ӱ��Ŀ¼�µ��ļ�,���������ж�FTP
	* �ϴ��ļ�ʱ��֤Ŀ¼�Ĵ���Ŀ¼��ʽ������"/"��Ŀ¼��ͷ
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
				_ftpclient.sendServer("XMKD " + pathName + "\r\n"); //����Ŀ¼!!!
			} catch (Exception e) {
				e = null;
			}
			int reply = _ftpclient.readServerResponse();
		}
		_ftpclient.binary();
	}

	/**
	* ȡ��ָ��Ŀ¼�µ������ļ�����������Ŀ¼����
	* ����nameList�õ����������е������õ�ָ��Ŀ¼�µ������ļ���
	* @param fullPath String
	* @return ArrayList
	* @throws Exception
	*/
	public String[] listFileNames(String _ip, int _port, String _user, String _pwd, String _fullPath) throws Exception {
		FtpClient ftpclient = new FtpClient(_ip, _port); //����FTP���Ӷ���!!
		ftpclient.login(_user, _pwd); //����..

		ftpclient.ascii(); //ע�⣬ʹ���ַ�ģʽ
		TelnetInputStream list = ftpclient.nameList(_fullPath); //
		byte[] names = new byte[20480];
		int bufsize = 0;
		bufsize = list.read(names, 0, names.length); //�����ж�ȡ
		list.close();
		ArrayList namesList = new ArrayList();
		int i = 0;
		int j = 0;
		while (i < bufsize) {
			if (names[i] == 10) { //�ַ�ģʽΪ10��������ģʽΪ13
				String tempName = new String(names, j, i - j);
				namesList.add(tempName);
				j = i + 1; //��һ��λ���ַ�ģʽ
			}
			i = i + 1;
		}

		//�˳�Ftp����
		ftpclient.sendServer("QUIT\r\n");
		int reply = ftpclient.readServerResponse(); //ȡ�÷������ķ�����Ϣ

		return (String[]) namesList.toArray(new String[0]);
	}

	//����..
	public static void main(String[] _args) {
		try {
			//FtpDealUtil myFtp = new FtpDealUtil();
			//byte[] data = new TBUtil().readFromInputStreamToBytes(new FileInputStream("C:/123.txt")); //
			//myFtp.upFile("127.0.0.1", 21, "mike", "mike123", data, "/k1/k2/k3/kk.txt"); //
			System.out.println("�ϴ��ļ��ɹ�"); //�ϴ��ɹ�!!!
		} catch (Exception e) {
			e.printStackTrace();
		} //
	}

}
