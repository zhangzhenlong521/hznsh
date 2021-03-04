package cn.com.pushworld.zt.ui.other;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.sysapp.other.ExcelToDBIfc;
import cn.com.pushworld.salary.to.SalaryFomulaParseUtil;
import cn.com.pushworld.salary.to.SalaryTBUtil;

/**
 * ����EXCEL���������ݿ�  yangke & hm
 * ��Ͷ��ʾ����
 */

public class ExcelToDBWKPanel extends AbstractWorkPanel implements BillListHtmlHrefListener, ActionListener, BillListSelectListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private WLTButton btn_insert, btn_edit, btn_delete, btn_upload, btn_import, btn_exit, btn_delete_his, btn_exit_his, btn_create_report, btn_exit_look, btn_look_check, btn_edit_templet_1, btn_edit_templet_2;
	private BillListPanel billListPanel = null;
	private BillListPanel billListPanel_his = null;
	private HashMap[] exceldatas = null;
	private ArrayList al_excel = new ArrayList();
	private BillCardPanel panel_time = null;
	private String updaterate = "";
	private BillDialog dialog = null;
	private BillDialog dialog_look = null;
	private WLTSplitPane splitpanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT);

	public void initialize() {
		billListPanel = new BillListPanel("EXCEL_TAB_CODE2");
		billListPanel.setQuickQueryPanelVisiable(false);
		billListPanel.addBillListSelectListener(this);
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		// btn_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		btn_edit = new WLTButton("�޸�");
		btn_edit.addActionListener(this);
		btn_delete = new WLTButton("ɾ��");
		btn_delete.addActionListener(this);
		btn_upload = new WLTButton("�ϴ�����");
		btn_upload.addActionListener(this);

		billListPanel.addBillListHtmlHrefListener(this);

		splitpanel.setLeftComponent(billListPanel);
		billListPanel_his = new BillListPanel(new DefaultTMO("", new String[][] { { "�ϴ�ʱ��", "150" }, { "��������", "70" }, { "�鿴", "70" } }));
		billListPanel_his.setRowNumberChecked(true);
		// billListPanel_his = new BillListPanel(getHvos());
		billListPanel_his.getTempletItemVO("�鿴").setListishtmlhref(true);
		//billListPanel_his.removeRow(0);

		btn_delete_his = new WLTButton("ɾ��");
		btn_delete_his.addActionListener(this);

		/*
		 * billListPanel_his.addBatchBillListButton(new WLTButton[] {
		 * btn_delete_his }); billListPanel_his.repaintBillListButton();
		 */

		btn_create_report = new WLTButton("����");
		btn_create_report.addActionListener(this);
		billListPanel_his.addBillListHtmlHrefListener(this);
		billListPanel_his.addBatchBillListButton(new WLTButton[] { btn_create_report, btn_delete_his });
		billListPanel_his.repaintBillListButton();
		splitpanel.setRightComponent(billListPanel_his);
		this.add(splitpanel);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_edit) {
			onBillListPopEdit();
			billListPanel.refreshCurrSelectedRow();
		} else if (e.getSource() == btn_edit) {
			onBillListPopEdit();
			billListPanel.refreshCurrSelectedRow();
		} else if (e.getSource() == btn_delete) {
			onDelete();
		} else if (e.getSource() == btn_delete_his) {
			onDelete_his();
		} else if (e.getSource() == btn_upload) {
			onUpload();
		} else if (e.getSource() == btn_import) {
			HashMap hm_excelname = new HashMap();

			try {
				HashVO[] hvos = UIUtil.getHashVoArrayByDS(null, "select excelname,updaterate from excel_tab");
				for (int i = 0; i < hvos.length; i++) {
					String excelname = hvos[i].getStringValue("excelname", "");
					String updaterate = hvos[i].getStringValue("updaterate", "");
					if (!("".equals(excelname))) {
						hm_excelname.put(excelname, updaterate);
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			String error = "";
			String error_rate = "";
			for (int s = 0; s < exceldatas.length; s++) {
				String sheetname = exceldatas[s].get("sheetname") + "";
				if (!hm_excelname.containsKey(sheetname)) {
					error += "[" + sheetname + "]";
				}

				if (hm_excelname.containsKey(sheetname)) {
					String rate = (String) hm_excelname.get(sheetname);
					if (!rate.equals(updaterate)) {
						error_rate += "[" + sheetname + "]";
					}
				}
			}

			if (!error.equals("")) {
				MessageBox.show(this, "Excel��sheet��Ϊ" + error + "�����ݲ�����!���½����ٵ���!");
				return;
			}

			if (!error_rate.equals("")) {
				MessageBox.show(this, "Excel��sheet��Ϊ" + error_rate + "�����ݵĸ���Ƶ�ʲ���[" + updaterate + "]!�����ٵ���!");
				return;
			}

			BillVO billVO = panel_time.getBillVO();
			String year = billVO.getStringValue("year");
			String month = billVO.getStringValue("month");

			if (updaterate.equals("���")) {
				if (year == null || year.equals("") || year.equals("---��ѡ��---")) {
					MessageBox.show(this, "��ѡ�����!");
					return;
				}
			} else {
				if (year == null || year.equals("") || month == null || month.equals("") || year.equals("---��ѡ��---") || month.equals("---��ѡ��---")) {
					MessageBox.show(this, "��ѡ����ݺ��·�!");
					return;
				}
			}

			if (MessageBox.confirm(this, "��ȷ��Ҫ�����б���������?")) {
				new SplashWindow(this, new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						doImport((SplashWindow) e.getSource());
					}
				}, 366, 366);
			}

			dialog.dispose();
		} else if (e.getSource() == btn_exit) {
			dialog.dispose();
		} else if (e.getSource() == btn_exit_his) {
			dialog.dispose();
		} else if (e.getSource() == btn_exit_look) {
			dialog_look.dispose();
		} else if (e.getSource() == btn_create_report) {
			onCreateReport();
		} else if (e.getSource() == btn_look_check) {
			onCheck();
		} else if (e.getSource() == btn_edit_templet_1 || btn_edit_templet_2 == e.getSource()) {
			BillVO billVO_ = billListPanel.getSelectedBillVO();
			String excelname = billVO_.getStringValue("EXCELNAME");
			BillCellPanel cellpanel = new BillCellPanel(excelname + "_У��ģ��");
			BillDialog dialog = new BillDialog(billListPanel_show, 800, 600);
			dialog.getContentPane().add(cellpanel);
			dialog.setVisible(true);
		}
	}

	/*
	 * У������
	 */
	private void onCheck() {
		BillVO billVO_ = billListPanel.getSelectedBillVO();
		String excelname = billVO_.getStringValue("EXCELNAME");
		try {
			BillCellVO cellvo = ((FrameWorkMetaDataServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkMetaDataServiceIfc.class)).getBillCellVO(excelname + "_У��ģ��", null, null);
			BillCellItemVO[][] vos = cellvo.getCellItemVOs();

			BillVO bvo[] = billListPanel_show.getAllBillVOs();
			List hvolist = new ArrayList<HashVO>();
			for (int i = 0; i < bvo.length; i++) {
				hvolist.add(bvo[i].convertToHashVO());
			}
			HashVO hvos[] = (HashVO[]) hvolist.toArray(new HashVO[0]);

			HashVO basevo = billListPanel_his.getSelectedBillVO().convertToHashVO();
			String date = basevo.getStringValue("��������");
			if (date != null) {
				date = date.replace("��", "-");
				date = date.substring(0, date.length() - 1);
			}
			basevo.setAttributeValue("��������", date);
			SalaryFomulaParseUtil util = new SalaryFomulaParseUtil();
			for (int i = 0; i < vos.length; i++) {
				for (int j = 0; j < vos[i].length; j++) {
					String value = vos[i][j].getCellvalue();
					if (!TBUtil.isEmpty(value)) {
						if (value.length() >= 1) {
							if (value.startsWith(":")) {
								util.putDefaultFactorValue(excelname, hvos);
								util.putDefaultFactorVO("�ַ���", "excel([" + excelname + "],\"����\",\"" + value.substring(1) + "\")", "���" + i + j, "", "");
								StringBuffer sb = new StringBuffer();
								Object rtvalue = util.onExecute(util.getFoctorHashVO("���" + i + j), basevo, sb);
								System.out.println(rtvalue + "\r\n" + sb);
								if (!(rtvalue == null || String.valueOf(rtvalue).equals("") || String.valueOf(rtvalue).equals("1.0") || String.valueOf(rtvalue).equals("1") || String.valueOf(rtvalue).equalsIgnoreCase("true"))) {
									billListPanel_show.setItemForeGroundColor("FF0000", i, convertIntColToEn(j + 1));
								} else {
									billListPanel_show.setItemForeGroundColor("636363", i, convertIntColToEn(j + 1));
								}
							}
						}
					}
				}
			}
			billListPanel_show.repaint();
		} catch (WLTRemoteException e) {
			createTemplet(excelname);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createTemplet(String excelname) {
		BillCellVO cellvo = new BillCellVO();
		try {
			cellvo.setId(UIUtil.getSequenceNextValByDS(null, "S_PUB_BILLCELLTEMPLET_H"));
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		cellvo.setTempletcode(excelname + "_У��ģ��");
		cellvo.setTempletname(excelname + "_У��ģ��");
		BillVO vos[] = billListPanel_show.getAllBillVOs();

		List<BillCellItemVO[]> cellvos = new ArrayList<BillCellItemVO[]>();
		for (int i = 0; i < vos.length; i++) {
			String keys[] = vos[i].getKeys();
			List rcells = new ArrayList<BillCellItemVO>();
			for (int j = 5; j < keys.length; j++) {
				BillCellItemVO item = new BillCellItemVO();
				item.setCellvalue(vos[i].getStringValue(keys[j]));
				rcells.add(item);
			}
			cellvos.add((BillCellItemVO[]) rcells.toArray(new BillCellItemVO[0]));
		}
		cellvo.setCellItemVOs(cellvos.toArray(new BillCellItemVO[0][0]));
		cellvo.setRowlength(cellvos.size());
		cellvo.setCollength(cellvos.get(0).length);
		try {
			UIUtil.getMetaDataService().saveBillCellVO(null, cellvo);
		} catch (WLTRemoteException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		BillCellPanel cellpanel = new BillCellPanel(excelname + "_У��ģ��");
		BillDialog dialog = new BillDialog(billListPanel_show, 800, 600);
		dialog.getContentPane().add(cellpanel);
		dialog.setVisible(true);
	}

	private void onCreateReport() {
		BillVO billVO_ = billListPanel.getSelectedBillVO();
		String table_name = billVO_.getStringValue("tablename");
		String excelname = billVO_.getStringValue("EXCELNAME");
		BillVO checkvos[] = billListPanel_his.getCheckedBillVOs();
		if (checkvos == null || checkvos.length == 0) {
			MessageBox.show(billListPanel_his, "�빴ѡҪͳ�Ƶ����ݡ�");
			return;
		}
		if (checkvos.length == 1) {
			MessageBox.show(billListPanel_his, "�����ٹ�ѡ2�����ݽ��л���");
			return;
		}
		BillCellVO templetcellvo = null;
		try {
			templetcellvo = ((FrameWorkMetaDataServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkMetaDataServiceIfc.class)).getBillCellVO(excelname + "_У��ģ��", null, null);
		} catch (Exception ex) {
			BillVO bvo = billListPanel_his.getBillVO(0);
			HashVO hvos[];
			try {
				hvos = UIUtil.getHashVoArrayByDS(null, "select * from " + table_name + " where creattime='" + bvo.getStringValue("�ϴ�ʱ��") + "'");
				if (hvos.length == 0) {
					return;
				}
				InsertSQLBuilder isql = new InsertSQLBuilder("pub_billcelltemplet_h");
				BillCellVO cellvo = new BillCellVO();
				try {
					cellvo.setId(UIUtil.getSequenceNextValByDS(null, "S_PUB_BILLCELLTEMPLET_H"));
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				cellvo.setTempletcode(excelname + "_У��ģ��");
				cellvo.setTempletname(excelname + "_У��ģ��");

				List<BillCellItemVO[]> cellvos = new ArrayList<BillCellItemVO[]>();
				for (int i = 0; i < hvos.length; i++) {
					String keys[] = hvos[i].getKeys();
					List rcells = new ArrayList<BillCellItemVO>();
					for (int j = 4; j < keys.length; j++) {
						BillCellItemVO item = new BillCellItemVO();
						item.setCellvalue(hvos[i].getStringValue(keys[j]));
						rcells.add(item);
					}
					cellvos.add((BillCellItemVO[]) rcells.toArray(new BillCellItemVO[0]));
				}
				cellvo.setCellItemVOs(cellvos.toArray(new BillCellItemVO[0][0]));
				cellvo.setRowlength(cellvos.size());
				cellvo.setCollength(cellvos.get(0).length);
				try {
					UIUtil.getMetaDataService().saveBillCellVO(null, cellvo);
				} catch (WLTRemoteException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				BillCellPanel cellpanel = new BillCellPanel(excelname + "_У��ģ��");
				BillDialog dialog = new BillDialog(billListPanel_show, 800, 600);
				dialog.getContentPane().add(cellpanel);
				dialog.setVisible(true);
				templetcellvo = cellpanel.getBillCellVO();
			} catch (WLTRemoteException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return;
		}

		try {
			List<String[][]> data = new ArrayList<String[][]>();
			for (int i = 0; i < checkvos.length; i++) {
				String str[][] = UIUtil.getStringArrayByDS(null, "select * from " + table_name + " where creattime= '" + checkvos[i].getStringValue("�ϴ�ʱ��") + "'");
				data.add(str);
			}
			SalaryTBUtil stbutil = new SalaryTBUtil();
			String currtime = UIUtil.getServerCurrTime();
			List sqllist = new ArrayList();
			BillCellItemVO itemvos[][] = templetcellvo.getCellItemVOs();
			SalaryFomulaParseUtil util = new SalaryFomulaParseUtil();
			for (int i = 0; i < itemvos.length; i++) {
				InsertSQLBuilder insertSQL = new InsertSQLBuilder(table_name);
				for (int j = 0; j < itemvos[i].length; j++) {
					String templetcellvalue = itemvos[i][j].getCellvalue();
					if (templetcellvalue != null && templetcellvalue.contains("${") && templetcellvalue.contains("}")) {
						String[] str = TBUtil.getTBUtil().getMacroList(templetcellvalue);
						StringBuffer sb = new StringBuffer();
						for (int k = 0; k < str.length; k++) {
							if (!TBUtil.isEmpty(str[k]) && str[k].startsWith("${")) {
								String parse = str[k].substring(2, str[k].length() - 1);
								Object value = util.execFormula(parse);
								if (value != null) {
									sb.append(value);
								}
							} else {
								sb.append(str[k]);
							}
						}
						templetcellvalue = sb.toString();
					}
					int calctype = 0; //0�����㣬1��ͣ�2��ƽ��
					if ("���".equals(itemvos[i][j].getCellkey())) {
						calctype = 1;
					} else if ("��ƽ��".equals(itemvos[i][j].getCellkey())) {
						calctype = 2;
					}
					insertSQL.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_" + table_name));
					insertSQL.putFieldValue("year", "2013");
					insertSQL.putFieldValue("creattime", currtime);
					String lastValue = null;
					boolean iscalc = false; //�Ѿ������
					try {
						double currValue = 0;

						if (calctype == 0) {
						} else {
							for (int j2 = 0; j2 < data.size(); j2++) {
								String[][] data_other = data.get(j2);
								String other_cell_value = data_other[i][j + 4];
								double other_value = 0;
								if (!TBUtil.isEmpty(other_cell_value)) {
									other_value = Double.parseDouble(other_cell_value);
									iscalc = true;
								}
								currValue += other_value;
							}
							if (calctype == 2) {
								currValue = currValue / data.size();
							}
							if (iscalc) {
								lastValue = BigDecimal.valueOf(currValue).toPlainString() + "";
							}else{
								lastValue = "";
							}
						}
					} catch (Exception ex) {
						lastValue = templetcellvalue;
					}
					if (lastValue != null) {
						insertSQL.putFieldValue(convertIntColToEn(j + 1), lastValue);
					} else if (!TBUtil.isEmpty(templetcellvalue)) {
						insertSQL.putFieldValue(convertIntColToEn(j + 1), templetcellvalue);
					}
				}
				sqllist.add(insertSQL);
				//				System.out.println(insertSQL.getSQL());
			}
			UIUtil.executeBatchByDS(null, sqllist);
			billListPanel_his.removeAllRows();
			billListPanel_his.putValue(getHvos());
		} catch (WLTRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//������ת����Ӣ�ı���
	private String convertIntColToEn(int _index) {
		StringBuffer sb = new StringBuffer();
		int in = _index / 26;
		if (in == 0) { //��������һ��
			char ccc = (char) (65 - 1 + _index);
			sb.append(ccc);
		} else {
			char ccc = (char) (65 - 1 + _index % 26);
			sb.append(ccc);
			sb.insert(0, convertIntColToEn(in));
		}
		return sb.toString();
	}

	private void onDelete() {
		int li_selRow = billListPanel.getSelectedRow();
		if (li_selRow < 0) {
			MessageBox.showSelectOne(billListPanel);
			return;
		}

		ArrayList list_sqls = new ArrayList();

		BillVO billVO = billListPanel.getSelectedBillVO();
		String id = billVO.getStringValue("id");
		String tablename = billVO.getStringValue("tablename");
		String del_sql = "delete from excel_tab where id='" + id + "'";
		list_sqls.add(del_sql);

		if (tablename != null && !tablename.equals("")) {
			String drop_sql = "drop table " + tablename + ""; // ��Ҳɾ��
			list_sqls.add(drop_sql);
		}

		try {
			if (MessageBox.confirm(this, "��ȷ��Ҫɾ��������¼?")) {
				UIUtil.executeBatchByDS(null, list_sqls);
				billListPanel.refreshData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void onUpload() {
		int li_selRow = billListPanel.getSelectedRow();
		if (li_selRow < 0) {
			MessageBox.showSelectOne(billListPanel);
			return;
		}

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

		al_excel = gethavsAL(exceldatas);

		if (al_excel == null || al_excel.size() <= 0) {
			MessageBox.show(this, "Excel����Ϊ��");
			return;
		}

		BillVO billVO = billListPanel.getSelectedBillVO();
		updaterate = billVO.getStringValue("updaterate");
		panel_time = getTimePanel(updaterate);
		panel_time.setAutoscrolls(false);

		dialog = new BillDialog(this, "����Ԥ��-" + str_path.substring(str_path.lastIndexOf("\\") + 1, str_path.length()), 800, 600);
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(panel_time, "North");
		dialog.getContentPane().add(getDataTabPanel(), "Center");
		dialog.getContentPane().add(getBtnPanel(), "South");
		dialog.setVisible(true);
	}

	private void doImport(SplashWindow sw) {
		try {
			ArrayList list_sqls = new ArrayList();
			ArrayList list_del_sqls = new ArrayList();
			StringBuffer sb_del = new StringBuffer(); // �����ظ���ʾ
			sb_del.append("");
			StringBuffer sb_add = new StringBuffer(); // �����ظ���ʾ
			sb_add.append("");

			BillVO billVO = panel_time.getBillVO();
			String year = billVO.getStringValue("year");
			String month = billVO.getStringValue("month");

			if (year != null && year.equals("---��ѡ��---")) {
				year = "";
			}
			if (month != null && month.equals("---��ѡ��---")) {
				month = "";
			}

			/*			BillVO billVO_ = billListPanel.getSelectedBillVO();
						String str_id = billVO_.getStringValue("id");
						String str_excelname = billVO_.getStringValue("excelname");
						String table_name = billVO_.getStringValue("tablename");*/

			String creattime = UIUtil.getServerCurrTime();

			String del_sql = "";
			int maxid = getMaxID();
			for (int s = 0; s < exceldatas.length; s++) {
				//for (int s = 0; s < 1; s++) { // ֻȡ��һ��sheet
				HashMap exceldata = exceldatas[s];

				int rownum = (Integer) exceldata.get("rownum");
				int colnum = (Integer) exceldata.get("colnum");
				if (rownum <= 0 || colnum <= 0) {
					continue; // ���ڿ�sheet ����
				}

				String[] strs = getColname(colnum); // excel��ͷ��ĸ��
				HashMap hm_fl = getFiledLength(exceldata); // excelÿ�����ݳ���

				//String filename = exceldata.get("filename")+""; 
				String sheetname = exceldata.get("sheetname") + "";
				String table_name = getTablename(sheetname); //excel sheet��Ӧ�ı���

				// String table_name = getTablename(str_excelname);
				if (table_name == null || table_name.equals("")) {
					String new_id = "" + maxid;
					maxid++;
					/*
					 * //excel sheet����ϵ InsertSQLBuilder sb_excel_tab = new
					 * InsertSQLBuilder("excel_tab");
					 * sb_excel_tab.putFieldValue("id",
					 * UIUtil.getSequenceNextValByDS(null, "S_EXCEL_TAB"));
					 * sb_excel_tab.putFieldValue("excelname",
					 * filename+"_"+sheetname);
					 * sb_excel_tab.putFieldValue("tablename",
					 * "excel_tab_"+new_id);
					 * list_sqls.add(sb_excel_tab.getSQL());
					 */

					// ����excel_tab�б�����
					UpdateSQLBuilder sb_excel_tab = new UpdateSQLBuilder("excel_tab", "excelname='" + sheetname + "'");
					sb_excel_tab.putFieldValue("tablename", "excel_tab_" + new_id);
					sb_excel_tab.putFieldValue("updatetime", creattime);
					list_sqls.add(sb_excel_tab.getSQL());

					// �����±�
					String table_sql = creatTable(strs, hm_fl, new_id); // �������
					list_sqls.add(table_sql);

					table_name = "excel_tab_" + new_id;
				} else {
					// �ж������Ƿ����
					String str_where = getWhere(year, month);
					if (!str_where.equals("")) {
						String sql = "select count(*) cou from " + table_name + " where " + str_where;
						HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, sql);
						if (hvs != null && hvs.length > 0) {
							int cou = hvs[0].getIntegerValue("cou", 0);
							if (cou > 0) {
								sb_del.append("����[" + sheetname + "]" + getStr(year, month) + "�������Ѵ���,�Ƿ񸲸�?\r\n");
								//sb_add.append("�ñ���" + getStr(year, month) + "�������Ѵ��ڣ��Ƿ��½�����?\r\n");
								del_sql = "delete from " + table_name + " where " + str_where;
								list_del_sqls.add(del_sql);
							}
						}
					}

					// �������ݿ��ֶμ�����
					HashMap hm = getTableCol(table_name); // ���ݿ��ֶμ�����
					final FrameWorkMetaDataServiceIfc service = UIUtil.getMetaDataService();
					for (int i = 0; i < strs.length; i++) {
						int l = (Integer) hm_fl.get(strs[i]);
						if (hm.containsKey(strs[i])) {
							int ll = (Integer) hm.get(strs[i]);
							if (l > ll) {
								String upd_sql = getAlterModifySQL(table_name, strs[i], "varchar(" + l + ")", null); // �ֶθ������
								list_sqls.add(upd_sql);
							}
						} else {
							String add_sql = service.getAddColumnSql(null, table_name, strs[i], "varchar", "" + l); // ׷���ֶ�
							if (add_sql.endsWith(";")) {
								list_sqls.add(add_sql.substring(0, add_sql.length() - 1));
							} else {
								list_sqls.add(add_sql);
							}
						}
					}

					// ����excel_tab�б�����
					UpdateSQLBuilder sb_excel_tab = new UpdateSQLBuilder("excel_tab", "excelname='" + sheetname + "'");
					sb_excel_tab.putFieldValue("updatetime", creattime);
					list_sqls.add(sb_excel_tab.getSQL());
				}

				// ��������sql
				for (int i = 0; i < rownum; i++) {
					InsertSQLBuilder sb_excel_data = new InsertSQLBuilder(table_name);
					sb_excel_data.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_" + table_name.toUpperCase()));
					for (int j = 0; j < colnum; j++) {
						String data = "" + exceldata.get(i + "_" + j);
						if (!(data.equals("") || data.equals("null"))) {
							sb_excel_data.putFieldValue(strs[j], data);
						}

						if (year != null && !year.equals("")) {
							sb_excel_data.putFieldValue("year", year);
						}
						if (month != null && !month.equals("")) {
							sb_excel_data.putFieldValue("month", month);
						}
						sb_excel_data.putFieldValue("creattime", creattime);
					}
					list_sqls.add(sb_excel_data.getSQL());
				}
			}

			if (!sb_del.toString().equals("")) {
				sw.closeSplashWindow();
				if (MessageBox.confirm(this, sb_del.toString())) {
					for (int i = 0; i < list_sqls.size(); i++) {
						list_del_sqls.add(list_sqls.get(i));
					}
					UIUtil.executeBatchByDS(null, list_del_sqls); // ����
					try {
						doIFC("���ǵ���ɹ�", year, month);
					} catch (Exception e) {
						MessageBox.show(this, "���ݵ���ɹ�!��չ��ִ��ʧ��!");
					}
					MessageBox.show(this, "���ݵ���ɹ�!");
					//billListPanel.refreshCurrSelectedRow();
					billListPanel.refreshData();
				}
				/*
				 * }else{ if(MessageBox.confirm(this, sb_add.toString())){
				 * UIUtil.executeBatchByDS(null, list_sqls); //�½�����
				 * MessageBox.show(this, "���ݵ���ɹ�!"); } }
				 */
			} else {
				UIUtil.executeBatchByDS(null, list_sqls);
				try {
					doIFC("��������ɹ�", year, month);
				} catch (Exception e) {
					MessageBox.show(this, "���ݵ���ɹ�!��չ��ִ��ʧ��!");
				}
				sw.closeSplashWindow();
				MessageBox.show(this, "���ݵ���ɹ�!");
				//billListPanel.refreshCurrSelectedRow();
				billListPanel.refreshData();
			}
		} catch (Exception e) {
			sw.closeSplashWindow();
			MessageBox.show(this, "���ݵ���ʧ��!");
			e.printStackTrace();
		}
	}

	private void doIFC(String state, String year, String month) throws Exception {
		BillVO billVO_ = billListPanel.getSelectedBillVO();
		String tablename[] = UIUtil.getStringArrayFirstColByDS(null, "select tablename from excel_tab where id=" + billVO_.getStringValue("id"));
		String classname = billVO_.getStringValue("classname");
		if (classname != null && !classname.equals("")) {
			Object obj = Class.forName(classname).newInstance();
			ExcelToDBIfc ifc = (ExcelToDBIfc) obj;
			ifc.Action(state, year, month, tablename[0]);
		}
	}

	// �ֶθ������
	private String getAlterModifySQL(String tableName, String key, String typeL, String _dbtype) {
		if (_dbtype == null) {
			_dbtype = ClientEnvironment.getInstance().getDefaultDataSourceType();
		}
		StringBuffer sb_sql = new StringBuffer();
		if ("sqlserver".equalsIgnoreCase(_dbtype)) {
			sb_sql.append(" alter table " + tableName + " alter column  " + key + " " + typeL);
		} else {
			sb_sql.append(" alter table " + tableName + " modify  " + key + " " + typeL);
		}
		return sb_sql.toString();
	}

	// ��ȡ���ݿ��ֶμ�����
	private HashMap getTableCol(String _table_name) {
		HashMap hm = new HashMap();
		try {
			TableDataStruct struct = UIUtil.getTableDataStructByDS(null, "select * from " + _table_name + " where 1=2");
			String[] headnames = struct.getHeaderName();
			int[] headerLength = struct.getHeaderLength();
			for (int i = 0; i < headnames.length; i++) {
				hm.put(headnames[i], headerLength[i]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hm;
	}

	// ��ȡ�����մ��ظ���ʾ
	private String getStr(String year, String month) {
		String str = "";
		if (year != null && !year.equals("")) {
			str += "[" + year + "]��";
		}
		if (month != null && !month.equals("")) {
			str += "[" + month + "]��";
		}

		return str;
	}

	// ��ȡ�����մ��ж�����
	private String getWhere(String year, String month) {
		String sql_where = "";
		if (year != null && !year.equals("")) {
			sql_where += " and year='" + year + "' ";
		}
		if (month != null && !month.equals("")) {
			sql_where += " and month='" + month + "' ";
		}

		if (!sql_where.equals("")) {
			sql_where = sql_where.substring(4, sql_where.length());
		}

		return sql_where;
	}

	// �������
	private String creatTable(String[] strs, HashMap hm, String seq) {
		StringBuffer sb = new StringBuffer();
		String dbtype = ClientEnvironment.getInstance().getDefaultDataSourceType();

		sb.append("create table excel_tab_" + seq + "(");

		if ("Oracle".equalsIgnoreCase(dbtype)) {
			sb.append("id number primary key, ");
		} else {
			sb.append("id int primary key, ");
		}

		sb.append("year varchar(20), ");
		sb.append("month varchar(20), ");
		sb.append("creattime varchar(20), ");
		for (int i = 0; i < strs.length; i++) {
			String l = "" + hm.get(strs[i]);
			if ((i + 1) == strs.length) {
				sb.append(strs[i] + " varchar(" + l + ") ");
			} else {
				sb.append(strs[i] + " varchar(" + l + "), ");
			}
		}
		sb.append(")");

		return sb.toString();
	}

	// ��ȡexcel sheet��Ӧ�ı���
	private String getTablename(String _excelname) {
		String table_name = "";
		try {
			String sql = "select tablename from excel_tab where excelname='" + _excelname + "'";
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, sql);
			if (hvs != null && hvs.length > 0) {
				table_name = hvs[0].getStringValue("tablename", "");
			}
		} catch (Exception e) {
		}
		return table_name;
	}

	// ��ȡexcel sheet��Ӧ�ı���
	private int getMaxID() {
		String table_name = "";
		int id = 0;
		try {
			String sql = "select tablename from excel_tab where tablename is not null order by id desc";
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, sql);
			if (hvs != null && hvs.length > 0) {
				for (int i = 0; i < hvs.length; i++) {
					table_name = hvs[i].getStringValue("tablename", "");
					if (!table_name.equals("")) {
						int temp_id = Integer.parseInt(table_name.substring(table_name.lastIndexOf("_") + 1, table_name.length()));
						if (temp_id > id) {
							id = temp_id;
						}
					}
				}
			}
		} catch (Exception e) {
		}

		/*		String id = "0";
				if (!table_name.equals("")) {
					id = table_name.substring(table_name.lastIndexOf("_") + 1, table_name.length());
				}*/

		return id + 1;
	}

	// ����Ԥ��ҳ��
	private JPanel getBtnPanel() {
		btn_import = new WLTButton("����");
		btn_exit = new WLTButton("ȡ��");

		btn_import.addActionListener(this);
		btn_exit.addActionListener(this);

		WLTPanel btnPanel = new WLTPanel(1, new FlowLayout(1));
		btnPanel.add(btn_import);
		btnPanel.add(btn_exit);

		return btnPanel;
	}

	// ����tab
	private JTabbedPane getDataTabPanel() {
		JTabbedPane tabbedPane = new JTabbedPane();

		for (int i = 0; i < al_excel.size(); i++) {
			HashVO[] hvos = (HashVO[]) al_excel.get(i);
			String[][] strs_header = new String[hvos[0].getKeys().length][2];
			for (int j = 0; j < hvos[0].getKeys().length; j++) {
				strs_header[j] = new String[] { getColumnName(j + 1), "85" };
			}
			//BillListPanel billListPanel = new BillListPanel((HashVO[]) al_excel.get(i));
			BillListPanel billListPanel = new BillListPanel(new DefaultTMO("", strs_header));
			billListPanel.putValue(hvos);
			tabbedPane.addTab("" + exceldatas[i].get("sheetname"), billListPanel);
		}

		/*		if (al_excel.size() > 0) {
					// ֻȡ��һ��sheet
					BillListPanel billListPanel = new BillListPanel((HashVO[]) al_excel.get(0));
					tabbedPane.addTab("" + exceldatas[0].get("sheetname"), billListPanel);
				}*/

		return tabbedPane;
	}

	// ����ѡ��ҳ��
	private BillCardPanel getTimePanel(String str) {
		BillCardPanel cardPanel = new BillCardPanel("EXCEL_TAB_CODE1");
		Pub_Templet_1VO templetVO = cardPanel.getTempletVO();
		templetVO.setTempletname("��������");
		templetVO.getItemVo("year").setCardisshowable(true);
		if (str.equals("�¶�")) {
			templetVO.getItemVo("month").setCardisshowable(true);
		}

		BillCardPanel cardPanel_new = new BillCardPanel(templetVO);
		cardPanel_new.setEditableByEditInit();
		cardPanel_new.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardPanel_new.setVisiable("excelname", false);
		cardPanel_new.setVisiable("updaterate", false);
		cardPanel_new.setVisiable("classname", false);

		String currDate = UIUtil.getCurrDate();
		cardPanel_new.setRealValueAt("year", currDate.substring(0, 4));
		if (str.equals("�¶�")) {
			cardPanel_new.setRealValueAt("month", "" + currDate.substring(currDate.indexOf("-") + 1, currDate.lastIndexOf("-")));
		}

		return cardPanel_new;
	}

	// ��excel����sheetֵ ת��Ϊ hvos��
	private ArrayList gethavsAL(HashMap[] hms) {
		ArrayList al_excel = new ArrayList();
		for (int s = 0; s < hms.length; s++) {
			HashMap exceldata = hms[s];
			int rownum = (Integer) exceldata.get("rownum");
			int colnum = (Integer) exceldata.get("colnum");
			String[] strs = getColname(colnum);
			HashVO[] hvos = new HashVO[rownum];
			for (int i = 0; i < rownum; i++) {
				hvos[i] = new HashVO();
				for (int j = 0; j < colnum; j++) {
					String data = "" + exceldata.get(i + "_" + j);
					if (data.equals("null")) {
						data = "";
					}
					hvos[i].setAttributeValue(strs[j], data);
				}
			}

			if (hvos != null && hvos.length > 0) {
				al_excel.add(hvos);
			}
		}

		return al_excel;
	}

	// ��ȡexcelÿ�����ݳ���
	private HashMap getFiledLength(HashMap exceldata) {
		HashMap filed = new HashMap();

		int rownum = (Integer) exceldata.get("rownum");
		int colnum = (Integer) exceldata.get("colnum");
		String[] strs = getColname(colnum);
		for (int i = 0; i < rownum; i++) {
			if (i == 0) {
				for (int j = 0; j < colnum; j++) {
					filed.put(strs[j], 50);
				}
			}
			for (int j = 0; j < colnum; j++) {
				String excel_value = "" + exceldata.get(i + "_" + j);
				int l = (Integer) filed.get(strs[j]);

				int length = 0;
				try {
					length = excel_value.toString().getBytes("GBK").length;
				} catch (Exception e) {
					length = new TBUtil().getStrUnicodeLength(excel_value.toString());
				}

				if (length > l) {
					filed.put(strs[j], length);
				}
			}
		}
		return filed;
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

			for (int s = 0; s < sheetnum; s++) {
				HashMap content = new HashMap();
				HSSFSheet sheet = wb.getSheetAt(s);

				int li_lastrow = sheet.getLastRowNum() + 1;
				int li_rowmax = 0;
				int li_colmax = 0;
				for (int i = 0; i < li_lastrow; i++) {
					HSSFRow row = sheet.getRow(i);
					if (row != null) {
						int li_lastcol = row.getLastCellNum();
						/*						if (li_lastcol >= li_colmax) {
													li_colmax = li_lastcol; // ÿ��col�����ܲ�һ��
												}*/
						for (int j = 0; j < li_lastcol; j++) {
							HSSFCell cell = row.getCell((int) j);
							if (cell != null) {
								String str_value = getCellValue_xls(cell, _filename, s, i, j);
								if (str_value != null && !str_value.equals("")) {
									content.put(i + "_" + j, str_value);
									if (i >= li_rowmax) {
										li_rowmax = i;
									}
									if (j >= li_colmax) {
										li_colmax = j;
									}
								}
							}
						}
					}
				}
				content.put("rownum", li_rowmax + 1);
				content.put("colnum", li_colmax + 1);
				content.put("sheetindex", s);
				content.put("sheetname", sheet.getSheetName());
				content.put("filename", _filename.substring(_filename.lastIndexOf("\\") + 1, _filename.lastIndexOf(".")));
				contents[s] = content;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
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

			for (int s = 0; s < sheetnum; s++) {
				HashMap content = new HashMap();
				XSSFSheet sheet = xwb.getSheetAt(s);

				int li_lastrow = sheet.getPhysicalNumberOfRows();
				int li_rowmax = 0;
				int li_colmax = 0;
				for (int i = 0; i < li_lastrow; i++) {
					XSSFRow row = sheet.getRow(i);
					if (row != null) {
						int li_lastcol = row.getLastCellNum();
						/*						if (li_lastcol >= li_colmax) {
													li_colmax = li_lastcol; // ÿ��col�����ܲ�һ��
												}*/
						for (int j = 0; j < li_lastcol; j++) {
							XSSFCell cell = row.getCell(j);
							if (cell != null) {
								// String str_value = row.getCell(j).toString();
								String str_value = getCellValue_xlsx(cell, _filename, s, i, j);
								if (str_value != null && !str_value.equals("")) {
									content.put(i + "_" + j, str_value);
									if (i >= li_rowmax) {
										li_rowmax = i;
									}
									if (j >= li_colmax) {
										li_colmax = j;
									}
								}
							}
						}
					}
				}
				content.put("rownum", li_rowmax + 1);
				content.put("colnum", li_colmax + 1);
				content.put("sheetindex", s);
				content.put("sheetname", sheet.getSheetName());
				content.put("filename", _filename.substring(_filename.lastIndexOf("\\") + 1, _filename.lastIndexOf(".")));
				contents[s] = content;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
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
				System.err.println("�ļ�[" + _filename + "]��[" + _sheetIndex + "]sheet[" + "]��[" + row + "]��[" + col + "]�еĸ��ӵĹ�ʽֵת�ַ�����!");

				try {
					str_value = getNumber(cell.getNumericCellValue());
				} catch (Exception exx) {
					System.err.println("�ļ�[" + _filename + "]��[" + _sheetIndex + "]sheet[" + "]��[" + row + "]��[" + col + "]�еĸ��ӵĹ�ʽֵת���ֳ���!");

					/*
					 * cell.setCellType(Cell.CELL_TYPE_STRING); //ǿ��ת��Ϊ�ַ� �������
					 * str_value = cell.getStringCellValue();
					 */
				}

			}
		} else if (li_cellType == HSSFCell.CELL_TYPE_ERROR) {
			//str_value = "" + cell.getErrorCellValue();
			cell.setCellType(Cell.CELL_TYPE_STRING);
			str_value = cell.getStringCellValue();
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
				System.err.println("�ļ�[" + _filename + "]��[" + _sheetIndex + "]sheet[" + "]��[" + row + "]��[" + col + "]�еĸ��ӵĹ�ʽֵת�ַ�����!");

				try {
					str_value = getNumber(cell.getNumericCellValue());
				} catch (Exception exx) {
					System.err.println("�ļ�[" + _filename + "]��[" + _sheetIndex + "]sheet[" + "]��[" + row + "]��[" + col + "]�еĸ��ӵĹ�ʽֵת���ֳ���!");

					/*
					 * cell.setCellType(Cell.CELL_TYPE_STRING); //ǿ��ת��Ϊ�ַ� �������
					 * str_value = cell.getStringCellValue();
					 */
				}

			}
		} else if (li_cellType == XSSFCell.CELL_TYPE_ERROR) {
			//str_value = "" + cell.getErrorCellValue();
			cell.setCellType(Cell.CELL_TYPE_STRING);
			str_value = cell.getStringCellValue();
		} else {
			str_value = cell.getStringCellValue();
		}

		return str_value;
	}

	private String getNumber(double num) {
		/*		String value = "";
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

				return value;*/
		return new DecimalFormat("#").format(num);
	}

	// ��ȡexcel��ͷ��ĸ��
	private String[] getColname(int l) {
		String[] strs = new String[l];
		for (int i = 0; i < l; i++) {
			strs[i] = getColumnName(i + 1); // 0��ͷ
		}
		return strs;
	}

	// ��һ������ת��Ϊ��ĸ 1-A
	private String getColumnName(int columnNum) {
		String result = "";

		int first;
		int last;
		if (columnNum > 256)
			columnNum = 256;
		first = columnNum / 27;
		last = columnNum - (first * 26);

		if (first > 0)
			result = String.valueOf((char) (first + 64));

		if (last > 0)
			result = result + String.valueOf((char) (last + 64));

		return result.toUpperCase();
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if (_event.getItemkey().equals("UPDATETIME")) {

			dialog.setVisible(true);
		} else if (_event.getItemkey().equals("�鿴")) {
			onLook_his();
		} else if (_event.getItemkey().equals("ɾ��")) {
			onDelete_his();
		}
	}

	BillListPanel billListPanel_show;

	private void onLook_his() {
		int li_selRow = billListPanel_his.getSelectedRow();
		if (li_selRow < 0) {
			MessageBox.showSelectOne(billListPanel_his);
			return;
		}

		BillVO billVO_ = billListPanel.getSelectedBillVO();
		String table_name = billVO_.getStringValue("tablename");

		BillVO billVO = billListPanel_his.getSelectedBillVO();
		String upload_time = billVO.getStringValue("�ϴ�ʱ��");
		String data_time = billVO.getStringValue("��������");
		String year = data_time.substring(0, data_time.indexOf("��"));
		String month = "";
		if (data_time.indexOf("��") > 0) {
			month = data_time.substring(data_time.indexOf("��") + 1, data_time.indexOf("��"));
		}

		HashVO[] hvs = null;
		String sql = "select * from " + table_name + " where creattime='" + upload_time + "' and " + getWhere(year, month);

		try {
			hvs = UIUtil.getHashVoArrayByDS(null, sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (hvs != null && hvs.length > 0) {
			String[][] strs_header = new String[hvs[0].getKeys().length][2];
			strs_header[0] = new String[] { "id", "85" };
			strs_header[1] = new String[] { "year", "85" };
			strs_header[2] = new String[] { "month", "85" };
			strs_header[3] = new String[] { "creattime", "85" };
			for (int i = 4; i < hvs[0].getKeys().length; i++) {
				strs_header[i] = new String[] { getColumnName(i + 1 - 4), "85" };
			}

			billListPanel_show = new BillListPanel(new DefaultTMO("", strs_header));
			billListPanel_show.putValue(hvs);

			//BillListPanel billListPanel = new BillListPanel(hvs);
			billListPanel_show.getTempletItemVO("year").setItemname("���");
			billListPanel_show.getTempletItemVO("month").setItemname("�·�");
			billListPanel_show.getTempletItemVO("creattime").setItemname("�ϴ�ʱ��");

			billListPanel_show.getTable().getColumn("year").setHeaderValue("���");
			billListPanel_show.getTable().getColumn("month").setHeaderValue("�·�");
			billListPanel_show.getTable().getColumn("creattime").setHeaderValue("�ϴ�ʱ��");

			billListPanel_show.getTempletItemVO("id").setCardisshowable(false);
			if (!(data_time.indexOf("��") > 0)) {
				billListPanel_show.getTempletItemVO("month").setCardisshowable(false);
			}

			billListPanel_show.setItemVisible("id", false);
			if (!(data_time.indexOf("��") > 0)) {
				billListPanel_show.setItemVisible("month", false);
			}

			btn_exit_look = new WLTButton("�ر�");
			btn_exit_look.addActionListener(this);
			btn_look_check = new WLTButton("У��");
			btn_look_check.addActionListener(this);
			btn_edit_templet_2 = new WLTButton("�޸�ģ��");
			btn_edit_templet_2.addActionListener(this);
			WLTPanel btnPanel = new WLTPanel(1, new FlowLayout(1));
			btnPanel.add(btn_edit_templet_2);
			btnPanel.add(btn_look_check);
			btnPanel.add(btn_exit_look);

			dialog_look = new BillDialog(this, "���ݲ鿴", 800, 600);
			dialog_look.getContentPane().setLayout(new BorderLayout());
			dialog_look.getContentPane().add(billListPanel_show, "Center");
			dialog_look.getContentPane().add(btnPanel, "South");
			dialog_look.setVisible(true);
		} else {
			MessageBox.show(this, "�����ѱ�ɾ��!");
		}
	}

	private void onDelete_his() {
		int li_selRow = billListPanel_his.getSelectedRow();
		if (li_selRow < 0) {
			MessageBox.showSelectOne(billListPanel_his);
			return;
		}

		BillVO billVO_ = billListPanel.getSelectedBillVO();
		String table_name = billVO_.getStringValue("tablename");

		BillVO billVO = billListPanel_his.getSelectedBillVO();
		String upload_time = billVO.getStringValue("�ϴ�ʱ��");
		String data_time = billVO.getStringValue("��������");
		String year = data_time.substring(0, data_time.indexOf("��"));
		String month = "";
		if (data_time.indexOf("��") > 0) {
			month = data_time.substring(data_time.indexOf("��") + 1, data_time.indexOf("��"));
		}

		String del_sql = "delete from " + table_name + " where creattime='" + upload_time + "' and " + getWhere(year, month);

		try {
			if (MessageBox.confirm(this, "��ȷ��Ҫɾ�������ϴ�������?")) {
				UIUtil.executeUpdateByDS(null, del_sql);
				billListPanel_his.removeSelectedRows(); // ɾ��һ������
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private HashVO[] getHvos() {
		BillVO billVO_ = billListPanel.getSelectedBillVO();
		String table_name = billVO_.getStringValue("tablename");

		HashVO[] hvs = null;
		try {
			String sql = "select distinct year, month, creattime from " + table_name + " order by creattime";
			hvs = UIUtil.getHashVoArrayByDS(null, sql);
			for (int i = 0; i < hvs.length; i++) {
				hvs[i].setAttributeValue("�ϴ�ʱ��", hvs[i].getStringValue("creattime", ""));
				String year = hvs[i].getStringValue("year", "");
				String month = hvs[i].getStringValue("month", "");
				if (month.equals("")) {
					hvs[i].setAttributeValue("��������", year + "��");
				} else {
					hvs[i].setAttributeValue("��������", year + "��" + month + "��");
				}
				hvs[i].setAttributeValue("�鿴", "�鿴");
				hvs[i].setAttributeValue("ɾ��", "ɾ��");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hvs;
	}

	private void onBillListPopEdit() {
		BillListPanel billList = billListPanel;

		BillVO billVO = billList.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		BillCardPanel cardPanel = new BillCardPanel(billList.templetVO);
		cardPanel.setLoaderBillFormatPanel(billList.getLoaderBillFormatPanel());
		cardPanel.setBillVO(billVO);

		BillCardDialog dialog = new BillCardDialog(billList, billList.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);

		String tablename = billVO.getStringValue("TABLENAME", "");
		if (!tablename.equals("")) {
			try {
				HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select count(id) cou from " + tablename);
				int cou = hvs[0].getIntegerValue("cou", 0);
				if (cou > 0) {
					cardPanel.setEditable("UPDATERATE", false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		dialog.setVisible(true);
	}

	public void onBillListSelectChanged(BillListSelectionEvent event) {
		billListPanel_his.removeAllRows();
		billListPanel_his.putValue(getHvos());

	}

}
