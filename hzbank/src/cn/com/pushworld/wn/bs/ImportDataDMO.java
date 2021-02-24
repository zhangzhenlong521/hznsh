package cn.com.pushworld.wn.bs;

import java.text.SimpleDateFormat;
import java.util.Date;

import ch.ethz.ssh2.*;
import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.pushworld.wn.to.*;

public class ImportDataDMO {
	private CommDMO dmo = new CommDMO();
	private LinuxImportdataUtil linuxUtil = LinuxImportdataUtil.getInstance();
	private String loginUserCode = ClientEnvironment.getInstance()
			.getLoginUserCode();

	public String ImportAll() {
		String str = "";
		InsertSQLBuilder insert = new InsertSQLBuilder("IMPORTLOG");
		try {
			SimpleDateFormat simple = new SimpleDateFormat(
					"yyyy-MM-dd HH24:mm:ss");
			String startDate = simple.format(new Date());// ��ʼʱ��
			// ��ȡ������·��(ȫ������ʱ�����������Ǵ��ѵ�������ڿ�ʼ����)
			String lastImport = linuxUtil.getLastImport1();// ��ȡ����һ�ε�������һ�������
			System.out.println("��ȡ����һ�ε�������һ���ļ��У�" + lastImport);
			String currentImport = linuxUtil.getNextDay(lastImport);// �ӵ�ǰ���ڿ�ʼ
			String importTable = "ALL";// ����ı�
			Long importLength = linuxUtil.getImportLength();// ���㵼���ļ���С
			// //���õ��뷽��
			Connection login = linuxUtil.login("192.168.1.25", "oracle",
					"ABCabc@123");// ��¼Linuxϵͳ
			String execute = linuxUtil.execute(login,
					"java -jar /home/oracle/wnImportTest.jar");// ִ�е������
			String result = "";
			String endDate = simple.format(new Date());// �������ʱ��
			String finalImport = linuxUtil.getLastImport1();// ��ȡ�����ĵ�������
			String fileRoute = currentImport + "-" + finalImport;
			insert.putFieldValue("STARTDATA", startDate);
			insert.putFieldValue("ENDDATE", endDate);
			insert.putFieldValue("IMPORTTABLE", importTable);
			insert.putFieldValue("IMPORTRESULT", result);
			insert.putFieldValue("FILELENGTH", importLength);
			insert.putFieldValue("IMPORTROUTE", fileRoute);
			insert.putFieldValue("operationAtaff", loginUserCode);
			insert.putFieldValue("operationtype", "ȫ������");
			dmo.executeUpdateByDS(null, insert.getSQL());
			str = "���ݵ���ɹ�,�����ļ�Ϊ:" + finalImport;
		} catch (Exception e) {
			str = "���ݵ���ʧ��,����ԭ��Ϊ:" + e.getStackTrace();
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * ����һ�������
	 * 
	 * @param date
	 *            :����
	 * @return
	 */
	public String ImportDay(String date) {
		String result = "";
		InsertSQLBuilder insert = new InsertSQLBuilder("IMPORTLOG");
		Connection conn = null;
		try {
			SimpleDateFormat simple = new SimpleDateFormat(
					"yyyy-MM-dd HH24:mm:ss");
			String startDate = simple.format(new Date());// ��ʼ�����ʱ��
			String importRoute = "/home/oracle/wnData/" + date;// ����·������
			String importTable = date + "/all";// ���õ����
			long fileLength = linuxUtil.getImportLength(date);// ��ȡ�����ļ���С
			conn = linuxUtil.login("192.168.1.25", "oracle", "ABCabc@123");
			String str = linuxUtil.execute(conn, "java -jar ImportOneDayDate  "
					+ date);// ִ�е������
			String endDate = simple.format(new Date());
			insert.putFieldValue("STARTDATA", startDate);
			insert.putFieldValue("ENDDATE", endDate);
			insert.putFieldValue("IMPORTROUTE", importRoute);
			insert.putFieldValue("IMPORTRESULT", str);
			insert.putFieldValue("IMPORTTABLE", importTable);
			insert.putFieldValue("FILELENGTH", fileLength);
			dmo.executeUpdateByDS(null, insert.getSQL());
			result = "���ݵ���ɹ�,�����ļ�·��Ϊ��" + importRoute + "��";
		} catch (Exception e) {
			result = "���ݵ���ʧ��,���������ļ�";
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * ����ĳ�ű�һ�������
	 * 
	 * @param filePath
	 * @return
	 */
	public String importOne(String filePath) {
		String result = "";
		InsertSQLBuilder insert = new InsertSQLBuilder("IMPORTLOG");
		Connection conn = null;
		try {
			SimpleDateFormat simple = new SimpleDateFormat(
					"yyyy-MM-dd HH24:mm:ss");
			String startDate = simple.format(new Date());// ��ʼ�����ʱ��
			String importRoute = "/home/oracle/wnData/" + filePath;// ����·������
			String importTable = "";// ���õ����
			if (filePath.contains("_add_2")) {// add��
				importTable = filePath.substring(filePath.lastIndexOf("/") + 1,
						filePath.lastIndexOf("_add_2"));
			} else {
				importTable = filePath.substring(filePath.lastIndexOf("/") + 1,
						filePath.lastIndexOf("_all_2"));
			}

			long fileLength = linuxUtil.getImportLength(filePath);// ��ȡ�����ļ���С
			conn = linuxUtil.login("192.168.1.25", "oracle", "ABCabc@123");
			String str = linuxUtil.execute(conn, "java -jar ImportOneDayDate  "
					+ filePath);// ִ�е������
			String endDate = simple.format(new Date());
			insert.putFieldValue("STARTDATA", startDate);
			insert.putFieldValue("ENDDATE", endDate);
			insert.putFieldValue("IMPORTROUTE", importRoute);
			insert.putFieldValue("IMPORTRESULT", str);
			insert.putFieldValue("IMPORTTABLE", importTable);
			insert.putFieldValue("FILELENGTH", fileLength);
			dmo.executeUpdateByDS(null, insert.getSQL());
			result = "���ݵ���ɹ�,�����ļ�·��Ϊ��" + importRoute + "��";
		} catch (Exception e) {
			result = "���ݵ���ʧ��,���������ļ�";
			e.printStackTrace();
		}
		return result;
	}

}