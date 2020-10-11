package cn.com.pushworld.salary.ui.tools;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 人员履职导入
 * @author haoming
 * create by 2013-11-12
 */
public class UserDutyImportWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = -684596798691266152L;
	private WLTButton btn_upload, btn_import, btn_download;
	private BillListPanel billListPanel_data = null;
	private HashMap[] exceldatas = null;
	private HashVO[] hvos_data = null;
	private Boolean importmark = false;
	private String[][] strs_field = { { "姓名", "登录号", "岗位名称", "部门名称", "任职日期", "结束日期", "任职描述" }, { "username", "code", "postname", "deptname", "startdate", "enddate", "descr" } }; //表字段

	public void initialize() {
		String[][] strs_header = { { "姓名", "70" }, { "登录号", "70" }, { "岗位名称", "100" }, { "部门名称", "100" }, { "任职日期", "100" }, { "结束日期", "100" }, { "任职描述", "100" }, { "校验结果", "120" }, { "导入结果", "120" } };
		billListPanel_data = new BillListPanel(new DefaultTMO("人员履职导入", strs_header));

		btn_upload = new WLTButton("上传数据");
		btn_upload.addActionListener(this);
		btn_import = new WLTButton("执行导入");
		btn_import.addActionListener(this);
		//btn_delete = new WLTButton("执行删除");
		//btn_delete.addActionListener(this);
		btn_download = new WLTButton("模版下载");
		btn_download.addActionListener(this);

		billListPanel_data.addBatchBillListButton(new WLTButton[] { btn_upload, btn_import });
		billListPanel_data.repaintBillListButton();

		this.add(billListPanel_data, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_upload) {
			uploadData(); //上传数据
		} else if (e.getSource() == btn_import) {
			if (importmark) {
				MessageBox.show(this, "数据已导入!");
				return;
			}

			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					dataImport(); //数据导入
				}
			}, 366, 366);
		} else if (e.getSource() == btn_download) {
			downLoadTemplet(); //模版下载
		}

	}

	public void uploadData() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogTitle("请选择一个Excel文件");
		chooser.setApproveButtonText("选择");

		FileFilter filter = new FileNameExtensionFilter("Microsoft Office Excel 工作表", "xls", "xlsx");
		chooser.setFileFilter(filter);
		int flag = chooser.showOpenDialog(this);
		if (flag != JFileChooser.APPROVE_OPTION || chooser.getSelectedFile() == null) {
			return;
		}
		final String str_path = chooser.getSelectedFile().getAbsolutePath();

		if (!(str_path.toLowerCase().endsWith(".xls") || str_path.toLowerCase().endsWith(".xlsx"))) {
			MessageBox.show(this, "请选择一个Excel文件!");
			return;
		}

		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				exceldatas = getExcelFileData(str_path);
			}
		}, 366, 366);

		if (exceldatas == null || exceldatas.length <= 0) {
			MessageBox.show(this, "Excel数据为空");
			return;
		}

		ArrayList al_excel = gethavsAL(exceldatas);

		if (al_excel == null || al_excel.size() <= 0) {
			MessageBox.show(this, "Excel数据为空");
			return;
		}

		hvos_data = compare((HashVO[]) al_excel.get(0));

		refreshData();
		importmark = false;
	}

	private void dataImport() {
		HashMap hm_user = null;
		HashMap code_user_map = null;
		try {
			hm_user = UIUtil.getHashMapBySQLByDS(null, "select name,id from sal_personinfo");
			code_user_map = UIUtil.getHashMapBySQLByDS(null, "select code,id from sal_personinfo");
		} catch (WLTRemoteException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (hm_user == null) {
			return;
		}
		ArrayList list_sqls = new ArrayList();
		LinkedHashMap<String, String> errMap = new LinkedHashMap<String, String>();
		HashSet<String> oldduty = new HashSet<String>();
		try {
			String[][] oldduties = UIUtil.getStringArrayByDS(null, "select username,postname,deptname,startdate,enddate from sal_personduties_his");
			for (int i = 0; i < oldduties.length; i++) {
				oldduty.add(oldduties[i][0] + "_" + oldduties[i][1] + "_" + oldduties[i][2] + "_" + oldduties[i][3] + "_" + oldduties[i][4]);
			}
		} catch (WLTRemoteException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		try {
			String date = UIUtil.getCurrDate();
			for (int i = 0; i < hvos_data.length; i++) {
				if (hvos_data[i].getStringValue("校验结果", "").equals("")) {
					if (hvos_data[i] == null) {
						continue;
					}
					String excel_username = hvos_data[i].getStringValue("姓名", "");
					String excel_usercode = hvos_data[i].getStringValue("登录号", "");
					String userid = null;
					if (TBUtil.isEmpty(excel_username) && TBUtil.isEmpty(excel_username)) {
						errMap.put(excel_username, i + "");
						hvos_data[i].setAttributeValue("导入结果", "姓名登录号不能同时为空");
						continue;
					}
					if (!TBUtil.isEmpty(excel_usercode)) {
						userid = (String) code_user_map.get(excel_usercode);
					} else {
						userid = (String) hm_user.get(excel_usercode);
					}
					if (userid == null) {
						errMap.put(excel_username, i + "");
						hvos_data[i].setAttributeValue("导入结果", "系统不存在该人员");
						continue;
					}
					InsertSQLBuilder sb_insert = new InsertSQLBuilder("sal_personduties_his");
					sb_insert.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_" + "SAL_PERSONDUTIES_HIS"));
					sb_insert.putFieldValue("userid", userid);
					sb_insert.putFieldValue("createuser", "外部导入");
					sb_insert.putFieldValue("createdate", date);
					for (int j = 0; j < strs_field[0].length; j++) {
						if (hvos_data[i].getStringValue(strs_field[0][j], "").equals("")) {
							continue;
						}
						sb_insert.putFieldValue(strs_field[1][j], hvos_data[i].getStringValue(strs_field[0][j], ""));
					}
					String compareKey = hvos_data[i].getStringValue("登录号", "") + "_" + hvos_data[i].getStringValue("姓名", "") + "_" + hvos_data[i].getStringValue("岗位名称", "") + "_" + hvos_data[i].getStringValue("部门名称", "") + "_" + hvos_data[i].getStringValue("任职日期", "") + "_" + hvos_data[i].getStringValue("结束日期", "");
					if (!oldduty.contains(compareKey)) {
						list_sqls.add(sb_insert.getSQL());
						hvos_data[i].setAttributeValue("导入结果", "已正确导入");
					} else {
						hvos_data[i].setAttributeValue("导入结果", "数据已存在");
					}
				}
			}
			if (list_sqls.size() == 0) {
				MessageBox.showWarn(this, "没有得到可执行的sql,请联系软件提供商.");
				refreshData();
				return;
			}
			if (errMap.size() > 0) {
				StringBuffer sb = new StringBuffer();
				String errname[] = errMap.keySet().toArray(new String[0]);
				for (int i = 0; i < errname.length; i++) {
					sb.append(errname[i] + "、");
				}
				if (sb.length() > 0) {
					if (!MessageBox.confirm(this, "系统中没有找到" + sb.substring(0, sb.length() - 1) + "\r\n是否继续导入?")) {
						return;
					}
				}

			}
			UIUtil.executeBatchByDS(null, list_sqls);
			importmark = true;
			MessageBox.show(this, "导入成功!");
			refreshData();
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		MessageBox.show(this, "导入失败!");
	}

	private void refreshData() {
		billListPanel_data.putValue(hvos_data);
		for (int i = 0; i < hvos_data.length; i++) {
			if (!hvos_data[i].getStringValue("校验结果", "").equals("")) {
				for (int j = 0; j < strs_field[0].length; j++) {
					billListPanel_data.setItemForeGroundColor("FF0000", i, strs_field[0][j]);
				}
				billListPanel_data.setItemForeGroundColor("FF0000", i, "校验结果");
			}
		}
	}

	private HashVO[] compare(HashVO[] hvos) {
		for (int i = 0; i < hvos.length; i++) {
			if (hvos[i].getStringValue("姓名", "").equals("")) {
				hvos[i].setAttributeValue("校验结果", "用户名为空;");
			}
		}
		return hvos;
	}

	// 将excel所有sheet值 转化为 hvos组
	private ArrayList gethavsAL(HashMap[] hms) {
		ArrayList al_excel = new ArrayList();
		for (int s = 0; s < hms.length; s++) {
			HashMap exceldata = hms[s];
			int rownum = (Integer) exceldata.get("rownum");
			int colnum = (Integer) exceldata.get("colnum");
			if (rownum <= 1) {
				continue;
			}

			HashVO[] hvos = new HashVO[rownum - 1];
			for (int i = 1; i < rownum; i++) { //去掉表头
				hvos[i - 1] = new HashVO();
				for (int j = 0; j < colnum; j++) {
					String data = "" + exceldata.get(i + "_" + j);
					if (data.equals("null")) {
						data = "";
					}

					String data_0 = "" + exceldata.get("0_" + j);
					if (!(data_0.equals("null") || data_0.equals(""))) {
						hvos[i - 1].setAttributeValue(data_0, data);
					}
				}
			}

			if (hvos != null && hvos.length > 0) {
				al_excel.add(hvos);
			}
		}

		return al_excel;
	}

	// 获取excel所有sheet值
	public HashMap[] getExcelFileData(String _filename) {
		if (_filename.endsWith("xls")) {
			return getExcelFileData_xls(_filename);
		}
		return getExcelFileData_xlsx(_filename);
	}

	// 获取excel所有sheet值 xls
	private HashMap[] getExcelFileData_xls(String _filename) {
		HashMap[] contents = null;
		FileInputStream in = null;

		try {
			in = new FileInputStream(_filename);
			POIFSFileSystem fs = new POIFSFileSystem(in);
			HSSFWorkbook wb = new HSSFWorkbook(fs);

			int sheetnum = wb.getNumberOfSheets();
			contents = new HashMap[sheetnum];

			for (int s = 0; s < 1; s++) { //取第一个sheet
				HashMap content = new HashMap();
				HSSFSheet sheet = wb.getSheetAt(s);

				int li_lastrow = sheet.getLastRowNum() + 1;
				int li_colmax = 0;
				for (int i = 0; i < li_lastrow; i++) {
					HSSFRow row = sheet.getRow(i);
					if (row != null) {
						int li_lastcol = row.getLastCellNum();
						if (li_lastcol >= li_colmax) {
							li_colmax = li_lastcol; // 每行col数可能不一样
						}
						for (int j = 0; j < li_lastcol; j++) {
							HSSFCell cell = row.getCell((int) j);
							if (cell != null) {
								String str_value = getCellValue_xls(cell, _filename, s, i, j);
								content.put(i + "_" + j, str_value);
							}
						}
					}
				}
				content.put("rownum", li_lastrow);
				content.put("colnum", li_colmax);
				content.put("sheetindex", s);
				content.put("sheetname", sheet.getSheetName());
				content.put("filename", _filename.substring(_filename.lastIndexOf("\\") + 1, _filename.lastIndexOf(".")));
				contents[s] = content;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return contents;
	}

	// 获取excel所有sheet值 xlsx
	private HashMap[] getExcelFileData_xlsx(String _filename) {
		HashMap[] contents = null;
		FileInputStream in = null;

		try {
			in = new FileInputStream(_filename);
			XSSFWorkbook xwb = new XSSFWorkbook(in);

			int sheetnum = xwb.getNumberOfSheets();
			contents = new HashMap[sheetnum];

			for (int s = 0; s < 1; s++) { //取第一个sheet
				HashMap content = new HashMap();
				XSSFSheet sheet = xwb.getSheetAt(s);

				int li_lastrow = sheet.getPhysicalNumberOfRows();
				int li_colmax = 0;
				for (int i = 0; i < li_lastrow; i++) {
					XSSFRow row = sheet.getRow(i);
					if (row != null) {
						int li_lastcol = row.getPhysicalNumberOfCells();
						if (li_lastcol >= li_colmax) {
							li_colmax = li_lastcol; // 每行col数可能不一样
						}
						for (int j = 0; j < li_lastcol; j++) {
							XSSFCell cell = row.getCell(j);
							if (cell != null) {
								// String str_value = row.getCell(j).toString();
								String str_value = getCellValue_xlsx(cell, _filename, s, i, j);
								content.put(i + "_" + j, str_value);
							}
						}
					}
				}
				content.put("rownum", li_lastrow);
				content.put("colnum", li_colmax);
				content.put("sheetindex", s);
				content.put("sheetname", sheet.getSheetName());
				content.put("filename", _filename.substring(_filename.lastIndexOf("\\") + 1, _filename.lastIndexOf(".")));
				contents[s] = content;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return contents;
	}

	// 获取xls单元格值
	private String getCellValue_xls(HSSFCell cell, String _filename, int _sheetIndex, int row, int col) {
		String str_value = "";
		int li_cellType = cell.getCellType();
		if (li_cellType == HSSFCell.CELL_TYPE_STRING) {
			str_value = cell.getStringCellValue();
		} else if (li_cellType == HSSFCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date d = cell.getDateCellValue();
				DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
				str_value = formater.format(d);
				if (str_value.substring(0, 4).equals("1900")) {
					str_value = String.valueOf((long) cell.getNumericCellValue());
				}
			} else {
				str_value = getNumber(cell.getNumericCellValue());
			}
		} else if (li_cellType == HSSFCell.CELL_TYPE_FORMULA) {
			try {
				str_value = "" + cell.getStringCellValue();
			} catch (Exception ex) {
				WLTLogger.getLogger(UserDutyImportWKPanel.class).error("文件[" + _filename + "]第[" + _sheetIndex + "]sheet[" + "]第[" + row + "]行[" + col + "]列的格子的公式值转字符出错!");
				try {
					str_value = getNumber(cell.getNumericCellValue());
				} catch (Exception exx) {
					WLTLogger.getLogger(UserDutyImportWKPanel.class).error("文件[" + _filename + "]第[" + _sheetIndex + "]sheet[" + "]第[" + row + "]行[" + col + "]列的格子的公式值转数字出错!");
					/*
					 * cell.setCellType(Cell.CELL_TYPE_STRING); //强制转化为字符 输出错误
					 * str_value = cell.getStringCellValue();
					 */
				}

			}
		} else if (li_cellType == HSSFCell.CELL_TYPE_ERROR) {
			str_value = "" + cell.getErrorCellValue();
		} else {
			str_value = cell.getStringCellValue();
		}

		return str_value;
	}

	// 获取xlsx单元格值
	private String getCellValue_xlsx(XSSFCell cell, String _filename, int _sheetIndex, int row, int col) {
		String str_value = "";
		int li_cellType = cell.getCellType();
		if (li_cellType == XSSFCell.CELL_TYPE_STRING) {
			str_value = cell.getStringCellValue();
		} else if (li_cellType == XSSFCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(cell)) {
				Date d = cell.getDateCellValue();
				DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
				str_value = formater.format(d);
				if (str_value.substring(0, 4).equals("1900")) {
					str_value = String.valueOf((long) cell.getNumericCellValue());
				}
			} else {
				str_value = getNumber(cell.getNumericCellValue());
			}
		} else if (li_cellType == XSSFCell.CELL_TYPE_FORMULA) {
			try {
				str_value = "" + cell.getStringCellValue();
			} catch (Exception ex) {
				WLTLogger.getLogger(UserDutyImportWKPanel.class).error("文件[" + _filename + "]第[" + _sheetIndex + "]sheet[" + "]第[" + row + "]行[" + col + "]列的格子的公式值转字符出错!");
				try {
					str_value = getNumber(cell.getNumericCellValue());
				} catch (Exception exx) {
					WLTLogger.getLogger(UserDutyImportWKPanel.class).error("文件[" + _filename + "]第[" + _sheetIndex + "]sheet[" + "]第[" + row + "]行[" + col + "]列的格子的公式值转数字出错!");
					/*
					 * cell.setCellType(Cell.CELL_TYPE_STRING); //强制转化为字符 输出错误
					 * str_value = cell.getStringCellValue();
					 */
				}

			}
		} else if (li_cellType == XSSFCell.CELL_TYPE_ERROR) {
			str_value = "" + cell.getErrorCellValue();
		} else {
			str_value = cell.getStringCellValue();
		}

		return str_value;
	}

	private String getNumber(double num) {
		String value = "";
		if (("" + num).indexOf("E") > 0 || ("" + num).endsWith(".0")) {
			DecimalFormat df = new DecimalFormat("0");
			value = "" + df.format(num);
		} else {
			try {
				value = "" + Integer.parseInt("" + num);
			} catch (NumberFormatException e) {
				value = "" + num;
			}
		}

		return value;
	}

	public void downLoadTemplet() {
		JFileChooser chooser = new JFileChooser();
		try {
			if ((ClientEnvironment.str_downLoadFileDir == null) || ("".equals(ClientEnvironment.str_downLoadFileDir))) {
				ClientEnvironment.str_downLoadFileDir = "C://";
			}
			File f = new File(new File(ClientEnvironment.str_downLoadFileDir + "人员导入模版.xls").getCanonicalPath());
			chooser.setSelectedFile(f);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		int li_rewult = chooser.showSaveDialog(this);
		if (li_rewult == 0) {
			File chooseFile = chooser.getSelectedFile();
			if (chooseFile != null) {
				ClientEnvironment.str_downLoadFileDir = chooseFile.getParent();
				String str_pathdir = chooseFile.getParent();
				if (str_pathdir.endsWith("\\")) {
					str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1);
				}
				FileOutputStream fo = null;
				try {
					byte[] data = UIUtil.getCommonService().getServerResourceFile2("/userimport.xls", "GBK");
					fo = new FileOutputStream(chooseFile);
					fo.write(data);
					MessageBox.show(this, "下载文件成功!");
					return;
				} catch (Exception e) {
					if (fo != null) {
						try {
							fo.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					e.printStackTrace();
				} finally {
					if (fo != null) {
						try {
							fo.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				MessageBox.show(this, "下载文件失败!");
			}
		}
	}
}
