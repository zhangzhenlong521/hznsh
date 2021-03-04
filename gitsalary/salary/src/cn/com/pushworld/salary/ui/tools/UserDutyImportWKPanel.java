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
 * ��Ա��ְ����
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
	private String[][] strs_field = { { "����", "��¼��", "��λ����", "��������", "��ְ����", "��������", "��ְ����" }, { "username", "code", "postname", "deptname", "startdate", "enddate", "descr" } }; //���ֶ�

	public void initialize() {
		String[][] strs_header = { { "����", "70" }, { "��¼��", "70" }, { "��λ����", "100" }, { "��������", "100" }, { "��ְ����", "100" }, { "��������", "100" }, { "��ְ����", "100" }, { "У����", "120" }, { "������", "120" } };
		billListPanel_data = new BillListPanel(new DefaultTMO("��Ա��ְ����", strs_header));

		btn_upload = new WLTButton("�ϴ�����");
		btn_upload.addActionListener(this);
		btn_import = new WLTButton("ִ�е���");
		btn_import.addActionListener(this);
		//btn_delete = new WLTButton("ִ��ɾ��");
		//btn_delete.addActionListener(this);
		btn_download = new WLTButton("ģ������");
		btn_download.addActionListener(this);

		billListPanel_data.addBatchBillListButton(new WLTButton[] { btn_upload, btn_import });
		billListPanel_data.repaintBillListButton();

		this.add(billListPanel_data, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_upload) {
			uploadData(); //�ϴ�����
		} else if (e.getSource() == btn_import) {
			if (importmark) {
				MessageBox.show(this, "�����ѵ���!");
				return;
			}

			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					dataImport(); //���ݵ���
				}
			}, 366, 366);
		} else if (e.getSource() == btn_download) {
			downLoadTemplet(); //ģ������
		}

	}

	public void uploadData() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setDialogTitle("��ѡ��һ��Excel�ļ�");
		chooser.setApproveButtonText("ѡ��");

		FileFilter filter = new FileNameExtensionFilter("Microsoft Office Excel ������", "xls", "xlsx");
		chooser.setFileFilter(filter);
		int flag = chooser.showOpenDialog(this);
		if (flag != JFileChooser.APPROVE_OPTION || chooser.getSelectedFile() == null) {
			return;
		}
		final String str_path = chooser.getSelectedFile().getAbsolutePath();

		if (!(str_path.toLowerCase().endsWith(".xls") || str_path.toLowerCase().endsWith(".xlsx"))) {
			MessageBox.show(this, "��ѡ��һ��Excel�ļ�!");
			return;
		}

		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				exceldatas = getExcelFileData(str_path);
			}
		}, 366, 366);

		if (exceldatas == null || exceldatas.length <= 0) {
			MessageBox.show(this, "Excel����Ϊ��");
			return;
		}

		ArrayList al_excel = gethavsAL(exceldatas);

		if (al_excel == null || al_excel.size() <= 0) {
			MessageBox.show(this, "Excel����Ϊ��");
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
				if (hvos_data[i].getStringValue("У����", "").equals("")) {
					if (hvos_data[i] == null) {
						continue;
					}
					String excel_username = hvos_data[i].getStringValue("����", "");
					String excel_usercode = hvos_data[i].getStringValue("��¼��", "");
					String userid = null;
					if (TBUtil.isEmpty(excel_username) && TBUtil.isEmpty(excel_username)) {
						errMap.put(excel_username, i + "");
						hvos_data[i].setAttributeValue("������", "������¼�Ų���ͬʱΪ��");
						continue;
					}
					if (!TBUtil.isEmpty(excel_usercode)) {
						userid = (String) code_user_map.get(excel_usercode);
					} else {
						userid = (String) hm_user.get(excel_usercode);
					}
					if (userid == null) {
						errMap.put(excel_username, i + "");
						hvos_data[i].setAttributeValue("������", "ϵͳ�����ڸ���Ա");
						continue;
					}
					InsertSQLBuilder sb_insert = new InsertSQLBuilder("sal_personduties_his");
					sb_insert.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_" + "SAL_PERSONDUTIES_HIS"));
					sb_insert.putFieldValue("userid", userid);
					sb_insert.putFieldValue("createuser", "�ⲿ����");
					sb_insert.putFieldValue("createdate", date);
					for (int j = 0; j < strs_field[0].length; j++) {
						if (hvos_data[i].getStringValue(strs_field[0][j], "").equals("")) {
							continue;
						}
						sb_insert.putFieldValue(strs_field[1][j], hvos_data[i].getStringValue(strs_field[0][j], ""));
					}
					String compareKey = hvos_data[i].getStringValue("��¼��", "") + "_" + hvos_data[i].getStringValue("����", "") + "_" + hvos_data[i].getStringValue("��λ����", "") + "_" + hvos_data[i].getStringValue("��������", "") + "_" + hvos_data[i].getStringValue("��ְ����", "") + "_" + hvos_data[i].getStringValue("��������", "");
					if (!oldduty.contains(compareKey)) {
						list_sqls.add(sb_insert.getSQL());
						hvos_data[i].setAttributeValue("������", "����ȷ����");
					} else {
						hvos_data[i].setAttributeValue("������", "�����Ѵ���");
					}
				}
			}
			if (list_sqls.size() == 0) {
				MessageBox.showWarn(this, "û�еõ���ִ�е�sql,����ϵ����ṩ��.");
				refreshData();
				return;
			}
			if (errMap.size() > 0) {
				StringBuffer sb = new StringBuffer();
				String errname[] = errMap.keySet().toArray(new String[0]);
				for (int i = 0; i < errname.length; i++) {
					sb.append(errname[i] + "��");
				}
				if (sb.length() > 0) {
					if (!MessageBox.confirm(this, "ϵͳ��û���ҵ�" + sb.substring(0, sb.length() - 1) + "\r\n�Ƿ��������?")) {
						return;
					}
				}

			}
			UIUtil.executeBatchByDS(null, list_sqls);
			importmark = true;
			MessageBox.show(this, "����ɹ�!");
			refreshData();
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		MessageBox.show(this, "����ʧ��!");
	}

	private void refreshData() {
		billListPanel_data.putValue(hvos_data);
		for (int i = 0; i < hvos_data.length; i++) {
			if (!hvos_data[i].getStringValue("У����", "").equals("")) {
				for (int j = 0; j < strs_field[0].length; j++) {
					billListPanel_data.setItemForeGroundColor("FF0000", i, strs_field[0][j]);
				}
				billListPanel_data.setItemForeGroundColor("FF0000", i, "У����");
			}
		}
	}

	private HashVO[] compare(HashVO[] hvos) {
		for (int i = 0; i < hvos.length; i++) {
			if (hvos[i].getStringValue("����", "").equals("")) {
				hvos[i].setAttributeValue("У����", "�û���Ϊ��;");
			}
		}
		return hvos;
	}

	// ��excel����sheetֵ ת��Ϊ hvos��
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
			for (int i = 1; i < rownum; i++) { //ȥ����ͷ
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

	// ��ȡexcel����sheetֵ
	public HashMap[] getExcelFileData(String _filename) {
		if (_filename.endsWith("xls")) {
			return getExcelFileData_xls(_filename);
		}
		return getExcelFileData_xlsx(_filename);
	}

	// ��ȡexcel����sheetֵ xls
	private HashMap[] getExcelFileData_xls(String _filename) {
		HashMap[] contents = null;
		FileInputStream in = null;

		try {
			in = new FileInputStream(_filename);
			POIFSFileSystem fs = new POIFSFileSystem(in);
			HSSFWorkbook wb = new HSSFWorkbook(fs);

			int sheetnum = wb.getNumberOfSheets();
			contents = new HashMap[sheetnum];

			for (int s = 0; s < 1; s++) { //ȡ��һ��sheet
				HashMap content = new HashMap();
				HSSFSheet sheet = wb.getSheetAt(s);

				int li_lastrow = sheet.getLastRowNum() + 1;
				int li_colmax = 0;
				for (int i = 0; i < li_lastrow; i++) {
					HSSFRow row = sheet.getRow(i);
					if (row != null) {
						int li_lastcol = row.getLastCellNum();
						if (li_lastcol >= li_colmax) {
							li_colmax = li_lastcol; // ÿ��col�����ܲ�һ��
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

	// ��ȡexcel����sheetֵ xlsx
	private HashMap[] getExcelFileData_xlsx(String _filename) {
		HashMap[] contents = null;
		FileInputStream in = null;

		try {
			in = new FileInputStream(_filename);
			XSSFWorkbook xwb = new XSSFWorkbook(in);

			int sheetnum = xwb.getNumberOfSheets();
			contents = new HashMap[sheetnum];

			for (int s = 0; s < 1; s++) { //ȡ��һ��sheet
				HashMap content = new HashMap();
				XSSFSheet sheet = xwb.getSheetAt(s);

				int li_lastrow = sheet.getPhysicalNumberOfRows();
				int li_colmax = 0;
				for (int i = 0; i < li_lastrow; i++) {
					XSSFRow row = sheet.getRow(i);
					if (row != null) {
						int li_lastcol = row.getPhysicalNumberOfCells();
						if (li_lastcol >= li_colmax) {
							li_colmax = li_lastcol; // ÿ��col�����ܲ�һ��
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

	// ��ȡxls��Ԫ��ֵ
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
				WLTLogger.getLogger(UserDutyImportWKPanel.class).error("�ļ�[" + _filename + "]��[" + _sheetIndex + "]sheet[" + "]��[" + row + "]��[" + col + "]�еĸ��ӵĹ�ʽֵת�ַ�����!");
				try {
					str_value = getNumber(cell.getNumericCellValue());
				} catch (Exception exx) {
					WLTLogger.getLogger(UserDutyImportWKPanel.class).error("�ļ�[" + _filename + "]��[" + _sheetIndex + "]sheet[" + "]��[" + row + "]��[" + col + "]�еĸ��ӵĹ�ʽֵת���ֳ���!");
					/*
					 * cell.setCellType(Cell.CELL_TYPE_STRING); //ǿ��ת��Ϊ�ַ� �������
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

	// ��ȡxlsx��Ԫ��ֵ
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
				WLTLogger.getLogger(UserDutyImportWKPanel.class).error("�ļ�[" + _filename + "]��[" + _sheetIndex + "]sheet[" + "]��[" + row + "]��[" + col + "]�еĸ��ӵĹ�ʽֵת�ַ�����!");
				try {
					str_value = getNumber(cell.getNumericCellValue());
				} catch (Exception exx) {
					WLTLogger.getLogger(UserDutyImportWKPanel.class).error("�ļ�[" + _filename + "]��[" + _sheetIndex + "]sheet[" + "]��[" + row + "]��[" + col + "]�еĸ��ӵĹ�ʽֵת���ֳ���!");
					/*
					 * cell.setCellType(Cell.CELL_TYPE_STRING); //ǿ��ת��Ϊ�ַ� �������
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
			File f = new File(new File(ClientEnvironment.str_downLoadFileDir + "��Ա����ģ��.xls").getCanonicalPath());
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
					MessageBox.show(this, "�����ļ��ɹ�!");
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
				MessageBox.show(this, "�����ļ�ʧ��!");
			}
		}
	}
}
