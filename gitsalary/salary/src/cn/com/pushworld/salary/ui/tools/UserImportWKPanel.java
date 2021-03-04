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
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class UserImportWKPanel extends AbstractWorkPanel implements ActionListener {
	private WLTButton btn_upload, btn_import, btn_download;
	private BillListPanel billListPanel_data = null;
	private HashMap[] exceldatas = null;
	private HashVO[] hvos_data = null;
	private Boolean importmark = false;
	private String[][] strs_field = {
			{ "��¼��", "����", "�Ա�", "��������", "������", "��Ա��", "���֤��", "ְ��", "����λ", "�θ�����", "��λϵ��", "��λ����", "����", "ѧ��", "��ҵѧУ", "רҵ", "ְ��", "ְ��Ƹ������", "������ò", "��ͬ��������", "�μӹ�������", "���뱾������", "����", "����", "������Ů��������", "�����˺�", "�����˺�", "��������", "�����˺�", "ÿ�µ�������", "���Ͻ�", "ס��������", "ͳ�﷽ʽ", "ͳ��ϵ��" },
			{ "code", "name", "sex", "birthday", "maindeptid", "tellerno", "cardid", "position", "mainstation", "stationdate", "stationratio", "stationkind", "age", "degree", "university", "specialities", "posttitle", "posttitleapplydate", "politicalstatus", "contractdate", "joinworkdate", "joinselfbankdate", "workage", "selfbankage", "onlychildrenbthday", "selfbankaccount", "otheraccount",
					"familyname", "familyaccount", "areaallowance", "pension", "housingfund", "planway", "planratio" } }; //���ֶ�

	public void initialize() {
		String[][] strs_header = { { "��¼��", "70" }, { "����", "70" }, { "�Ա�", "70" }, { "��������", "70" }, { "������", "70" }, { "��Ա��", "70" }, { "���֤��", "100" }, { "ְ��", "70" }, { "����λ", "70" }, { "�θ�����", "70" }, { "��λϵ��", "70" }, { "��λ����", "70" }, { "����", "70" }, { "ѧ��", "70" }, { "��ҵѧУ", "100" }, { "רҵ", "70" }, { "ְ��", "70" }, { "ְ��Ƹ������", "100" }, { "������ò", "70" }, { "��ͬ��������", "100" },
				{ "�μӹ�������", "100" }, { "���뱾������", "100" }, { "����", "70" }, { "����", "70" }, { "������Ů��������", "120" }, { "�����˺�", "100" }, { "�����˺�", "100" }, { "��������", "100" }, { "�����˺�", "100" }, { "ÿ�µ�������", "100" }, { "���Ͻ�", "70" }, { "ס��������", "90" }, { "ͳ�﷽ʽ", "70" }, { "ͳ��ϵ��", "70" }, { "У����", "120" }, { "������", "120" } };
		billListPanel_data = new BillListPanel(new DefaultTMO("��Ա��Ϣ����", strs_header));

		btn_upload = new WLTButton("�ϴ�����");
		btn_upload.addActionListener(this);
		btn_import = new WLTButton("ִ�е���");
		btn_import.addActionListener(this);
		//btn_delete = new WLTButton("ִ��ɾ��");
		//btn_delete.addActionListener(this);
		btn_download = new WLTButton("ģ������");
		btn_download.addActionListener(this);

		billListPanel_data.addBatchBillListButton(new WLTButton[] { btn_upload, btn_import, btn_download });
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
		HashMap hm_user = new HashMap();
		try {
			HashVO[] hvos_user = UIUtil.getHashVoArrayByDS(null, "select id, name from sal_personinfo");
			for (int i = 0; i < hvos_user.length; i++) {
				String id = hvos_user[i].getStringValue("id", "");
				String name = hvos_user[i].getStringValue("name", "");
				if (!("".equals(name))) {
					hm_user.put(name, id);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		HashMap hm_other = new HashMap();
		try {
			HashVO[] hvos_other = UIUtil.getHashVoArrayByDS(null, "select username, deptid, postname from v_pub_user_post_1 where isdefault = 'Y'");
			for (int i = 0; i < hvos_other.length; i++) {
				String username = hvos_other[i].getStringValue("username", "");
				if (!("".equals(username))) {
					hm_other.put(username, hvos_other[i]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		ArrayList list_sqls = new ArrayList();
		try {
			for (int i = 0; i < hvos_data.length; i++) {
				if (hvos_data[i].getStringValue("У����", "").equals("")) {
					HashVO hvo = null;
					if (hm_other.containsKey(hvos_data[i].getStringValue("����", ""))) {
						hvo = (HashVO) hm_other.get(hvos_data[i].getStringValue("����", ""));
					}

					if (hm_user.containsKey(hvos_data[i].getStringValue("����", ""))) {
						UpdateSQLBuilder sb_update = new UpdateSQLBuilder("sal_personinfo", "id=" + hm_user.get(hvos_data[i].getStringValue("����", "")));
						for (int j = 0; j < strs_field[0].length; j++) {
							if (hvos_data[i].getStringValue(strs_field[0][j], "").equals("")) {
								continue;
							}
							if (strs_field[0][j].equals("������") || strs_field[0][j].equals("����λ")) {
								if (hvo != null) {
									if (strs_field[0][j].equals("������")) {
										sb_update.putFieldValue(strs_field[1][j], hvo.getStringValue("deptid", ""));
										hvos_data[i].setAttributeValue("������", hvo.getStringValue("deptid", ""));
									} else {
										sb_update.putFieldValue(strs_field[1][j], hvo.getStringValue("postname", ""));
										hvos_data[i].setAttributeValue("����λ", hvo.getStringValue("postname", ""));
									}
								}
							} else {
								sb_update.putFieldValue(strs_field[1][j], hvos_data[i].getStringValue(strs_field[0][j], ""));
							}
						}
						list_sqls.add(sb_update.getSQL());

						hvos_data[i].setAttributeValue("������", "�Ѹ���");
					} else {
						InsertSQLBuilder sb_insert = new InsertSQLBuilder("sal_personinfo");
						sb_insert.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_" + "sal_personinfo".toUpperCase()));
						for (int j = 0; j < strs_field[0].length; j++) {
							if (hvos_data[i].getStringValue(strs_field[0][j], "").equals("")) {
								continue;
							}
							if (strs_field[0][j].equals("������") || strs_field[0][j].equals("����λ")) {
								if (hvo != null) {
									if (strs_field[0][j].equals("������")) {
										sb_insert.putFieldValue(strs_field[1][j], hvo.getStringValue("deptid", ""));
										hvos_data[i].setAttributeValue("������", hvo.getStringValue("deptid", ""));
									} else {
										sb_insert.putFieldValue(strs_field[1][j], hvo.getStringValue("postname", ""));
										hvos_data[i].setAttributeValue("����λ", hvo.getStringValue("postname", ""));
									}
								}
							} else {
								sb_insert.putFieldValue(strs_field[1][j], hvos_data[i].getStringValue(strs_field[0][j], ""));
							}
						}
						list_sqls.add(sb_insert.getSQL());

						hvos_data[i].setAttributeValue("������", "�ѵ���");
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

			String idcard = hvos[i].getStringValue("���֤��", "");
			if (!idcard.equals("") && idcard.length() == 18) {
				hvos[i].setAttributeValue("��������", idcard.substring(6, 10) + "-" + idcard.substring(10, 12) + "-" + idcard.substring(12, 14));
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
			if (hvos == null) {
				continue;
			}
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
				WLTLogger.getLogger(UserImportWKPanel.class).error("�ļ�[" + _filename + "]��[" + _sheetIndex + "]sheet[" + "]��[" + row + "]��[" + col + "]�еĸ��ӵĹ�ʽֵת�ַ�����!");
				try {
					str_value = getNumber(cell.getNumericCellValue());
				} catch (Exception exx) {
					WLTLogger.getLogger(UserImportWKPanel.class).error("�ļ�[" + _filename + "]��[" + _sheetIndex + "]sheet[" + "]��[" + row + "]��[" + col + "]�еĸ��ӵĹ�ʽֵת���ֳ���!");
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
				WLTLogger.getLogger(UserImportWKPanel.class).error("�ļ�[" + _filename + "]��[" + _sheetIndex + "]sheet[" + "]��[" + row + "]��[" + col + "]�еĸ��ӵĹ�ʽֵת�ַ�����!");
				try {
					str_value = getNumber(cell.getNumericCellValue());
				} catch (Exception exx) {
					WLTLogger.getLogger(UserImportWKPanel.class).error("�ļ�[" + _filename + "]��[" + _sheetIndex + "]sheet[" + "]��[" + row + "]��[" + col + "]�еĸ��ӵĹ�ʽֵת���ֳ���!");
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
