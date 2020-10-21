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
			String startDate = simple.format(new Date());// 开始时间
			// 获取到导入路径(全量导入时，导入日期是从已导入的日期开始导入)
			String lastImport = linuxUtil.getLastImport1();// 获取到上一次导入的最后一天的数据
			System.out.println("获取到上一次导入的最后一个文件夹：" + lastImport);
			String currentImport = linuxUtil.getNextDay(lastImport);// 从当前日期开始
			String importTable = "ALL";// 导入的表
			Long importLength = linuxUtil.getImportLength();// 计算导入文件大小
			// //调用导入方法
			Connection login = linuxUtil.login("192.168.1.25", "oracle",
					"ABCabc@123");// 登录Linux系统
			String execute = linuxUtil.execute(login,
					"java -jar /home/oracle/wnImportTest.jar");// 执行导入操作
			String result = "";
			String endDate = simple.format(new Date());// 导入结束时间
			String finalImport = linuxUtil.getLastImport1();// 获取到最后的导入日期
			String fileRoute = currentImport + "-" + finalImport;
			insert.putFieldValue("STARTDATA", startDate);
			insert.putFieldValue("ENDDATE", endDate);
			insert.putFieldValue("IMPORTTABLE", importTable);
			insert.putFieldValue("IMPORTRESULT", result);
			insert.putFieldValue("FILELENGTH", importLength);
			insert.putFieldValue("IMPORTROUTE", fileRoute);
			insert.putFieldValue("operationAtaff", loginUserCode);
			insert.putFieldValue("operationtype", "全量导入");
			dmo.executeUpdateByDS(null, insert.getSQL());
			str = "数据导入成功,导入文件为:" + finalImport;
		} catch (Exception e) {
			str = "数据导入失败,错误原因为:" + e.getStackTrace();
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 导入一天的数据
	 * 
	 * @param date
	 *            :日期
	 * @return
	 */
	public String ImportDay(String date) {
		String result = "";
		InsertSQLBuilder insert = new InsertSQLBuilder("IMPORTLOG");
		Connection conn = null;
		try {
			SimpleDateFormat simple = new SimpleDateFormat(
					"yyyy-MM-dd HH24:mm:ss");
			String startDate = simple.format(new Date());// 开始导入的时间
			String importRoute = "/home/oracle/wnData/" + date;// 导入路径设置
			String importTable = date + "/all";// 设置导入表
			long fileLength = linuxUtil.getImportLength(date);// 获取导入文件大小
			conn = linuxUtil.login("192.168.1.25", "oracle", "ABCabc@123");
			String str = linuxUtil.execute(conn, "java -jar ImportOneDayDate  "
					+ date);// 执行导入操作
			String endDate = simple.format(new Date());
			insert.putFieldValue("STARTDATA", startDate);
			insert.putFieldValue("ENDDATE", endDate);
			insert.putFieldValue("IMPORTROUTE", importRoute);
			insert.putFieldValue("IMPORTRESULT", str);
			insert.putFieldValue("IMPORTTABLE", importTable);
			insert.putFieldValue("FILELENGTH", fileLength);
			dmo.executeUpdateByDS(null, insert.getSQL());
			result = "数据导入成功,导入文件路径为【" + importRoute + "】";
		} catch (Exception e) {
			result = "数据导入失败,请检查数据文件";
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 导入某张表一天的数据
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
			String startDate = simple.format(new Date());// 开始导入的时间
			String importRoute = "/home/oracle/wnData/" + filePath;// 导入路径设置
			String importTable = "";// 设置导入表
			if (filePath.contains("_add_2")) {// add表
				importTable = filePath.substring(filePath.lastIndexOf("/") + 1,
						filePath.lastIndexOf("_add_2"));
			} else {
				importTable = filePath.substring(filePath.lastIndexOf("/") + 1,
						filePath.lastIndexOf("_all_2"));
			}

			long fileLength = linuxUtil.getImportLength(filePath);// 获取导入文件大小
			conn = linuxUtil.login("192.168.1.25", "oracle", "ABCabc@123");
			String str = linuxUtil.execute(conn, "java -jar ImportOneDayDate  "
					+ filePath);// 执行导入操作
			String endDate = simple.format(new Date());
			insert.putFieldValue("STARTDATA", startDate);
			insert.putFieldValue("ENDDATE", endDate);
			insert.putFieldValue("IMPORTROUTE", importRoute);
			insert.putFieldValue("IMPORTRESULT", str);
			insert.putFieldValue("IMPORTTABLE", importTable);
			insert.putFieldValue("FILELENGTH", fileLength);
			dmo.executeUpdateByDS(null, insert.getSQL());
			result = "数据导入成功,导入文件路径为【" + importRoute + "】";
		} catch (Exception e) {
			result = "数据导入失败,请检查数据文件";
			e.printStackTrace();
		}
		return result;
	}

}