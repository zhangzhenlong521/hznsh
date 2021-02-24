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
			System.out.println("�������ļ�[" + str_tempetfileName + "]"); //
			templetFile.createNewFile();
			FileOutputStream out = new FileOutputStream(templetFile); //
			out.write(fileVO.getByteCodes());
			out.close();

			//copy from templet
			//templetFile = new File(str_tempetfileName); //
		}

		String str_newfileName = System.getProperty("ClientCodeCache") + "printexcel_" + System.currentTimeMillis() + "_" + _fileName; //
		saveToExcel(str_tempetfileName, str_newfileName, _dataMap); //�������ļ�

		//String str_excelpath = new TBUtil().getExcelExePath();
		//String str_command = str_excelpath + " \"" + str_newfileName + "\"";
		//System.out.println(str_command); //
		Desktop.open(new File(str_newfileName)); //�����ļ�
	}

	public void saveToExcel(String _templetfileName, String _newfileName, HashMap _mapdata) throws Exception {
		TBUtil tbUtil = new TBUtil(); //
		FileInputStream in = new FileInputStream(_templetfileName); //
		POIFSFileSystem fs = new POIFSFileSystem(in); //�ҿ�һ���ļ�

		HSSFWorkbook wb = new HSSFWorkbook(fs); //�����
		HSSFSheet sheet = wb.getSheetAt(0); //ȡ�õ�һ��ҳǩ
		int li_firstrow = sheet.getFirstRowNum();
		int li_lastrow = sheet.getLastRowNum();
		//System.out.println("�з�Χ:[" + li_firstrow + "]-[" + li_lastrow + "]");
		if (_mapdata != null) {
			for (int i = li_firstrow; i <= li_lastrow; i++) {
				HSSFRow row = sheet.getRow(i); //ȡ�õ�һ��
				if (row != null) { //���ĳһ�в�Ϊ��
					int li_firstcol = row.getFirstCellNum();
					int li_lastcol = row.getLastCellNum();
					for (int j = li_firstcol; j <= li_lastcol; j++) {
						HSSFCell cell = row.getCell((short) j); //ȡ�õ�һ��
						if (cell != null) { //���ĳ�����Ӳ�Ϊ��!
							if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) { //��������е��������ַ���
								//cell.setEncoding((short) 1); //���ñ���,ֻ���������,���ĲŲ�������,��������������������!!!
								String str_value = cell.getStringCellValue(); //
								//System.out.println("[" + i + "][" + j + "]:" + str_value); //
								if (str_value != null) { //��������е����ݲ�Ϊ��..
									if (str_value.startsWith("${")) {
										//System.out.println("������Ҫ�滻��[" + i + "][" + j + "]:" + str_value); //
										str_value = str_value.substring(1, str_value.length()); //ȥ����һ��$����

										try {
											String[] str_keys = tbUtil.getFormulaMacPars(str_value); //
											boolean bo_isconvert = false; //�Ƿ���ת��
											for (int k = 0; k < str_keys.length; k++) {
												if (_mapdata.containsKey(str_keys[k])) {
													String str_convertvalue = (String) _mapdata.get(str_keys[k]); //�ӿͻ��˻���ȡ��!!
													if (str_convertvalue == null) {
														str_convertvalue = ""; //����ǿ�,����ĳ���ַ�ȡ��
													}
													str_value = tbUtil.replaceAll(str_value, "{" + str_keys[k] + "}", str_convertvalue); //�滻
													bo_isconvert = true;
												}
												//System.out.println("�滻[" + i + "][" + j + "]: [" + str_keys[k] + "][" + str_convertvalue + "]"); //
											}

											if (bo_isconvert) { //�������ת��
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

		in.close(); //�ȹر�

		FileOutputStream fileOut = new FileOutputStream(_newfileName);
		wb.write(fileOut);
		fileOut.close();
	}
}
