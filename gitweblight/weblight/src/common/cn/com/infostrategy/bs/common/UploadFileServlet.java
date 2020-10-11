package cn.com.infostrategy.bs.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 在兴业与邮储都遇到,集群时第二台服务器往第一台写文件时,网络映射不允许使用! ftp也不让!
 * 然后就只能使用Http再请求计算一次! 以前刘旋飞写过一个,但是一个独立的程序! 没有放在平台中!!
 * 不得已,在邮储现场现写了一个!
 * 需要特别强调的是,这个类没有引用任何第三方的类!都是纯JDK中的类!这就意味着可以单独将这个类打包成一个jar,然后部署在一个单独的TomCat中,然后专门用来解决上传文件的问题!
 * @author Administrator
 *
 */
public class UploadFileServlet extends HttpServlet {

	private static final long serialVersionUID = 8299470204258626993L;

	@Override
	protected void service(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException {
		try {

			ObjectInputStream rqin = new ObjectInputStream(_request.getInputStream());
			HashMap parMap = (HashMap) rqin.readObject(); //
			rqin.close(); //

			HashMap rtMap = new HashMap(); //
			try {
				String str_actionType = (String) parMap.get("Action"); //
				if (str_actionType != null) {
					if (str_actionType.equals("upload")) { //上传文件
						rtMap = uploadFile(parMap); //
					} else if (str_actionType.equals("download")) { //下载文件
						rtMap = downloadFile(parMap); //
					} else if (str_actionType.equals("delete")) { //删除文件
						rtMap = deleteFile(parMap); //删除文件
					} else if (str_actionType.equals("fullSearch")) { //全文查找文件!
						rtMap = fullSearchFile(parMap); //删除文件
					} else {
						System.out.println("UploadFileServlet被调用,但发现是未知的Action[" + str_actionType + "]!"); //
					}
				} else {
					System.out.println("UploadFileServlet被调用,但Action为空!"); //
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
				rtMap.put("isException", "Y"); //是否发生异常
				rtMap.put("Exception", ex); //
			}
			ObjectOutputStream rsout = new ObjectOutputStream(_response.getOutputStream()); //
			rsout.writeObject(rtMap); //
			rsout.close(); //关闭流!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 上传文件!!!
	 * @param _parMap
	 * @return
	 */
	private HashMap uploadFile(HashMap _parMap) throws Exception {
		String str_fileName = (String) _parMap.get("FileName"); //旧的文件名
		System.out.println("UploadFileServlet做上传文件处理,文件名[" + str_fileName + "]......."); //
		byte[] bytes = (byte[]) _parMap.get("ByteCodes"); //文件内容
		String str_dir = str_fileName.substring(0, str_fileName.lastIndexOf("/")); //文件目录....
		File filedir = new File(str_dir); // 上传文件的目录
		if (!filedir.exists()) { //如果目录不存在,则创建目录! 每天的文件在第一个文件时才会调用此!
			filedir.mkdirs(); // 创建目录
		}
		File file = new File(str_fileName); //
		file.createNewFile(); //创建新文件
		FileOutputStream out = new FileOutputStream(file, false); //写文件流,是覆盖模式!! 
		out.write(bytes); //写文件!!
		out.close(); //关闭流

		//写文件!!!
		HashMap rtMap = new HashMap(); //
		rtMap.put("ReturnResult", str_fileName); //
		return rtMap; //
	}

	/**
	 * 下载文件!!!
	 * @param _parMap
	 * @return
	 */
	private HashMap downloadFile(HashMap _parMap) throws Exception {
		String str_FileName = (String) _parMap.get("FileName"); //需要下载的文件名
		System.out.println("UploadFileServlet做下载文件处理,实际读取文件[" + str_FileName + "]"); //
		File file = new File(str_FileName); //
		if (!file.exists()) {
			throw new Exception("远程文件[" + file + "]不存在"); //
		}
		byte[] bytes = readFromInputStreamToBytes(new FileInputStream(file)); //
		HashMap fileDataMap = new HashMap(); //
		fileDataMap.put("ByteCodes", bytes); //
		fileDataMap.put("OldFileName", str_FileName); //

		return fileDataMap; //
	}

	/**
	 * 删除文件
	 * @param parMap
	 * @return
	 */
	private HashMap deleteFile(HashMap _parMap) { ///
		String str_FileName = (String) _parMap.get("FileName"); //需要下载的文件名
		File fl = new File(str_FileName); //
		HashMap rtMap = new HashMap(); //
		if (fl.exists()) { //如果文件存在.
			fl.delete(); //删除文件
			System.out.println("UploadFileServlet做删除文件处理,删除文件[" + str_FileName + "]成功!"); //
			rtMap.put("ReturnResult", "删除文件[" + str_FileName + "]成功!"); //
		} else {
			System.out.println("UploadFileServlet做删除文件处理,但发现文件件[" + str_FileName + "]不存在!"); //
			rtMap.put("ReturnResult", "删除文件[" + str_FileName + "]时发现文件不存在!"); //
		}

		return rtMap; //
	}

	/**
	 * 全文检索文件!使用poi,或Lucense
	 * @param _parMap
	 * @return
	 */
	private HashMap fullSearchFile(HashMap _parMap) { //
		HashMap rtMap = new HashMap();
		return null; //
	}

	/**
	 * 写文件
	 * @param _out
	 * @param _bys
	 */
	public void writeBytesToOutputStream(OutputStream _out, byte[] _bys) {
		ByteArrayInputStream bins = null; //
		try {
			bins = new ByteArrayInputStream(_bys); // Java官方网站强烈建议使用该对象读流数据,说是更健壮,更平缓,更稳定!!!因为它是一步步读的!对内存与硬盘消耗均友好!
			byte[] tmpbys = new byte[2048]; //
			int pos = -1; //
			while ((pos = bins.read(tmpbys)) != -1) { // 循环读入
				_out.write(tmpbys, 0, pos); // 写入
			}
		} catch (Exception ex) { //
			ex.printStackTrace(); //
		} finally {
			try {
				_out.close(); // 关闭输入流!!
			} catch (Exception exx1) {
			}
			try {
				bins.close(); // 关闭输出流!!!
			} catch (Exception exx1) {
			}
		}
	}

	// 读取一个输入流,返回其所有字节! 比如读文件
	private byte[] readFromInputStreamToBytes(InputStream _ins) {
		ByteArrayOutputStream bout = null; //
		try {
			bout = new ByteArrayOutputStream(); // Java官方网站强烈建议使用该对象读流数据,说是更健壮,更平缓,更稳定!!!因为它是一步步读的!对内存与硬盘消耗均友好!
			byte[] bys = new byte[2048]; //
			int pos = -1;
			while ((pos = _ins.read(bys)) != -1) { // 通过循环读取,更流畅,更稳定!!节约内存!
				bout.write(bys, 0, pos); //
			}
			byte[] returnBys = bout.toByteArray(); //
			return returnBys; //
		} catch (Exception ex) { //
			ex.printStackTrace(); //
			return null;
		} finally {
			try {
				bout.close(); // 关闭输出流!!!
			} catch (Exception exx1) {
			}
			try {
				_ins.close(); // 关闭输入流!!
			} catch (Exception exx1) {
			}
		}
	}

	/**
	 * 将一个16进制格式的字符串转换成原始的字符串格式!!!!
	 * 
	 * @return
	 */
	private String convertHexStringToStr(String _hexText) {
		try {
			byte[] byte_return = convertHexStringToBytes(_hexText);
			if (byte_return == null) {
				return null;
			}
			String str_return = new String(byte_return, "GBK"); //
			return str_return; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * 将16进制字符串转换成二进制数组
	 * 
	 * @param _hexText
	 * @return
	 */
	private byte[] convertHexStringToBytes(String _hexText) {
		if (_hexText == null) {
			return null;
		}

		try {
			String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
			byte[] byte_return = new byte[_hexText.length() / 2]; //
			for (int r = 0; r < _hexText.length() / 2; r++) {
				String str_item = _hexText.substring(r * 2, r * 2 + 2); // ////
				String str_1 = str_item.substring(0, 1); //
				String str_2 = str_item.substring(1, 2); //
				int li_pos_1 = 0; //
				for (int i = 0; i < pseudo.length; i++) {
					if (pseudo[i].equalsIgnoreCase(str_1)) {
						li_pos_1 = i;
						break;
					}
				}

				int li_pos_2 = 0; //
				for (int i = 0; i < pseudo.length; i++) {
					if (pseudo[i].equalsIgnoreCase(str_2)) {
						li_pos_2 = i;
						break;
					}
				}

				int li_value = 16 * li_pos_1 + li_pos_2; //
				byte by_value = (byte) li_value; //
				byte_return[r] = by_value; //
			}

			return byte_return;
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null; //
		}
	}

}
