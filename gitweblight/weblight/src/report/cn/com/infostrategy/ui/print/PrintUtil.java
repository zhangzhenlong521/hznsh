package cn.com.infostrategy.ui.print;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.jdesktop.jdic.desktop.Desktop;

import cn.com.infostrategy.to.common.ClassFileVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;

public class PrintUtil {

	public void openExcelPrint(String _fileName, HashMap _dataMap) throws Exception {
		String str_tempetfileName = System.getProperty("ClientCodeCache") + _fileName;
		File templetFile = new File(str_tempetfileName); //
		if (!templetFile.exists()) {

			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkCommServiceIfc.class);
			ClassFileVO fileVO = service.downloadFile("download\\" + _fileName); //
			//File file = new File(str_tempetfileName); //
			System.out.println("创建新文件[" + str_tempetfileName + "]"); //
			templetFile.createNewFile();
			FileOutputStream out = new FileOutputStream(templetFile); //
			out.write(fileVO.getByteCodes());
			out.close();

			//copy from templet
			//templetFile = new File(str_tempetfileName); //
		}

		String str_newfileName = System.getProperty("ClientCodeCache") + "printexcel_" + System.currentTimeMillis() + "_" + _fileName; //
		saveToExcel(str_tempetfileName, str_newfileName, _dataMap); //复制新文件

		//String str_excelpath = new TBUtil().getExcelExePath();
		//String str_command = str_excelpath + " \"" + str_newfileName + "\"";
		//System.out.println(str_command); //
		Desktop.open(new File(str_newfileName)); //打开新文件
	}

	public void saveToExcel(String _templetfileName, String _newfileName, HashMap _mapdata) throws Exception {
		TBUtil tbUtil = new TBUtil(); //
		FileInputStream in = new FileInputStream(_templetfileName); //
		POIFSFileSystem fs = new POIFSFileSystem(in); //找开一个文件

		HSSFWorkbook wb = new HSSFWorkbook(fs); //打开面板
		HSSFSheet sheet = wb.getSheetAt(0); //取得第一个页签
		int li_firstrow = sheet.getFirstRowNum();
		int li_lastrow = sheet.getLastRowNum();
		//System.out.println("行范围:[" + li_firstrow + "]-[" + li_lastrow + "]");
		if (_mapdata != null) {
			for (int i = li_firstrow; i <= li_lastrow; i++) {
				HSSFRow row = sheet.getRow(i); //取得第一行
				if (row != null) { //如果某一行不为空
					int li_firstcol = row.getFirstCellNum();
					int li_lastcol = row.getLastCellNum();
					for (int j = li_firstcol; j <= li_lastcol; j++) {
						HSSFCell cell = row.getCell((short) j); //取得第一列
						if (cell != null) { //如果某个格子不为空!
							if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) { //如果格子中的类型是字符型
								//cell.setEncoding((short) 1); //设置编码,只有设成这样,中文才不会乱码,否则会出现中文乱码的情况!!!
								String str_value = cell.getStringCellValue(); //
								//System.out.println("[" + i + "][" + j + "]:" + str_value); //
								if (str_value != null) { //如果格子中的内容不为空..
									if (str_value.startsWith("${")) {
										//System.out.println("发现需要替换的[" + i + "][" + j + "]:" + str_value); //
										str_value = str_value.substring(1, str_value.length()); //去掉第一个$符号

										try {
											String[] str_keys = tbUtil.getFormulaMacPars(str_value); //
											boolean bo_isconvert = false; //是否发生转换
											for (int k = 0; k < str_keys.length; k++) {
												if (_mapdata.containsKey(str_keys[k])) {
													String str_convertvalue = (String) _mapdata.get(str_keys[k]); //从客户端缓存取数!!
													if (str_convertvalue == null) {
														str_convertvalue = ""; //如果是空,则用某个字符取代
													}
													str_value = tbUtil.replaceAll(str_value, "{" + str_keys[k] + "}", str_convertvalue); //替换
													bo_isconvert = true;
												}
												//System.out.println("替换[" + i + "][" + j + "]: [" + str_keys[k] + "][" + str_convertvalue + "]"); //
											}

											if (bo_isconvert) { //如果发生转换
												cell.setCellValue(str_value); //
											} else {
												cell.setCellValue(""); //
											}
										} catch (Exception e) {
											e.printStackTrace(); //
										}
									}
								}
							}
						}
					}
				}
			}
		}

		in.close(); //先关闭

		FileOutputStream fileOut = new FileOutputStream(_newfileName);
		wb.write(fileOut);
		fileOut.close();
	}
}
