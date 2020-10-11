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
 * ����ҵ���ʴ�������,��Ⱥʱ�ڶ�̨����������һ̨д�ļ�ʱ,����ӳ�䲻����ʹ��! ftpҲ����!
 * Ȼ���ֻ��ʹ��Http���������һ��! ��ǰ������д��һ��,����һ�������ĳ���! û�з���ƽ̨��!!
 * ������,���ʴ��ֳ���д��һ��!
 * ��Ҫ�ر�ǿ������,�����û�������κε���������!���Ǵ�JDK�е���!�����ζ�ſ��Ե��������������һ��jar,Ȼ������һ��������TomCat��,Ȼ��ר����������ϴ��ļ�������!
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
					if (str_actionType.equals("upload")) { //�ϴ��ļ�
						rtMap = uploadFile(parMap); //
					} else if (str_actionType.equals("download")) { //�����ļ�
						rtMap = downloadFile(parMap); //
					} else if (str_actionType.equals("delete")) { //ɾ���ļ�
						rtMap = deleteFile(parMap); //ɾ���ļ�
					} else if (str_actionType.equals("fullSearch")) { //ȫ�Ĳ����ļ�!
						rtMap = fullSearchFile(parMap); //ɾ���ļ�
					} else {
						System.out.println("UploadFileServlet������,��������δ֪��Action[" + str_actionType + "]!"); //
					}
				} else {
					System.out.println("UploadFileServlet������,��ActionΪ��!"); //
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
				rtMap.put("isException", "Y"); //�Ƿ����쳣
				rtMap.put("Exception", ex); //
			}
			ObjectOutputStream rsout = new ObjectOutputStream(_response.getOutputStream()); //
			rsout.writeObject(rtMap); //
			rsout.close(); //�ر���!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * �ϴ��ļ�!!!
	 * @param _parMap
	 * @return
	 */
	private HashMap uploadFile(HashMap _parMap) throws Exception {
		String str_fileName = (String) _parMap.get("FileName"); //�ɵ��ļ���
		System.out.println("UploadFileServlet���ϴ��ļ�����,�ļ���[" + str_fileName + "]......."); //
		byte[] bytes = (byte[]) _parMap.get("ByteCodes"); //�ļ�����
		String str_dir = str_fileName.substring(0, str_fileName.lastIndexOf("/")); //�ļ�Ŀ¼....
		File filedir = new File(str_dir); // �ϴ��ļ���Ŀ¼
		if (!filedir.exists()) { //���Ŀ¼������,�򴴽�Ŀ¼! ÿ����ļ��ڵ�һ���ļ�ʱ�Ż���ô�!
			filedir.mkdirs(); // ����Ŀ¼
		}
		File file = new File(str_fileName); //
		file.createNewFile(); //�������ļ�
		FileOutputStream out = new FileOutputStream(file, false); //д�ļ���,�Ǹ���ģʽ!! 
		out.write(bytes); //д�ļ�!!
		out.close(); //�ر���

		//д�ļ�!!!
		HashMap rtMap = new HashMap(); //
		rtMap.put("ReturnResult", str_fileName); //
		return rtMap; //
	}

	/**
	 * �����ļ�!!!
	 * @param _parMap
	 * @return
	 */
	private HashMap downloadFile(HashMap _parMap) throws Exception {
		String str_FileName = (String) _parMap.get("FileName"); //��Ҫ���ص��ļ���
		System.out.println("UploadFileServlet�������ļ�����,ʵ�ʶ�ȡ�ļ�[" + str_FileName + "]"); //
		File file = new File(str_FileName); //
		if (!file.exists()) {
			throw new Exception("Զ���ļ�[" + file + "]������"); //
		}
		byte[] bytes = readFromInputStreamToBytes(new FileInputStream(file)); //
		HashMap fileDataMap = new HashMap(); //
		fileDataMap.put("ByteCodes", bytes); //
		fileDataMap.put("OldFileName", str_FileName); //

		return fileDataMap; //
	}

	/**
	 * ɾ���ļ�
	 * @param parMap
	 * @return
	 */
	private HashMap deleteFile(HashMap _parMap) { ///
		String str_FileName = (String) _parMap.get("FileName"); //��Ҫ���ص��ļ���
		File fl = new File(str_FileName); //
		HashMap rtMap = new HashMap(); //
		if (fl.exists()) { //����ļ�����.
			fl.delete(); //ɾ���ļ�
			System.out.println("UploadFileServlet��ɾ���ļ�����,ɾ���ļ�[" + str_FileName + "]�ɹ�!"); //
			rtMap.put("ReturnResult", "ɾ���ļ�[" + str_FileName + "]�ɹ�!"); //
		} else {
			System.out.println("UploadFileServlet��ɾ���ļ�����,�������ļ���[" + str_FileName + "]������!"); //
			rtMap.put("ReturnResult", "ɾ���ļ�[" + str_FileName + "]ʱ�����ļ�������!"); //
		}

		return rtMap; //
	}

	/**
	 * ȫ�ļ����ļ�!ʹ��poi,��Lucense
	 * @param _parMap
	 * @return
	 */
	private HashMap fullSearchFile(HashMap _parMap) { //
		HashMap rtMap = new HashMap();
		return null; //
	}

	/**
	 * д�ļ�
	 * @param _out
	 * @param _bys
	 */
	public void writeBytesToOutputStream(OutputStream _out, byte[] _bys) {
		ByteArrayInputStream bins = null; //
		try {
			bins = new ByteArrayInputStream(_bys); // Java�ٷ���վǿ�ҽ���ʹ�øö����������,˵�Ǹ���׳,��ƽ��,���ȶ�!!!��Ϊ����һ��������!���ڴ���Ӳ�����ľ��Ѻ�!
			byte[] tmpbys = new byte[2048]; //
			int pos = -1; //
			while ((pos = bins.read(tmpbys)) != -1) { // ѭ������
				_out.write(tmpbys, 0, pos); // д��
			}
		} catch (Exception ex) { //
			ex.printStackTrace(); //
		} finally {
			try {
				_out.close(); // �ر�������!!
			} catch (Exception exx1) {
			}
			try {
				bins.close(); // �ر������!!!
			} catch (Exception exx1) {
			}
		}
	}

	// ��ȡһ��������,�����������ֽ�! ������ļ�
	private byte[] readFromInputStreamToBytes(InputStream _ins) {
		ByteArrayOutputStream bout = null; //
		try {
			bout = new ByteArrayOutputStream(); // Java�ٷ���վǿ�ҽ���ʹ�øö����������,˵�Ǹ���׳,��ƽ��,���ȶ�!!!��Ϊ����һ��������!���ڴ���Ӳ�����ľ��Ѻ�!
			byte[] bys = new byte[2048]; //
			int pos = -1;
			while ((pos = _ins.read(bys)) != -1) { // ͨ��ѭ����ȡ,������,���ȶ�!!��Լ�ڴ�!
				bout.write(bys, 0, pos); //
			}
			byte[] returnBys = bout.toByteArray(); //
			return returnBys; //
		} catch (Exception ex) { //
			ex.printStackTrace(); //
			return null;
		} finally {
			try {
				bout.close(); // �ر������!!!
			} catch (Exception exx1) {
			}
			try {
				_ins.close(); // �ر�������!!
			} catch (Exception exx1) {
			}
		}
	}

	/**
	 * ��һ��16���Ƹ�ʽ���ַ���ת����ԭʼ���ַ�����ʽ!!!!
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
	 * ��16�����ַ���ת���ɶ���������
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
