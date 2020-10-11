package cn.com.pushworld.salary.ui.report.p010;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.report.ReportExportWord;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * Ա������ָ�����ƽ�����.�Լ���ְ�������.
 * 
 * @author haoming create by 2013-10-15
 */
public class YearPersonCheckReportWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private SalaryServiceIfc service;
	private JPanel mainPanel = new JPanel(new BorderLayout());
	BillQueryPanel queryPanel = new BillQueryPanel("SAL_SALARYBILL_CODE2");
	private WLTButton btn_view, btn_exportExcel, btn_export_word;
	private HashMap<String, HashVO> id_uservo = new HashMap<String, HashVO>();

	public void initialize() {
		try {
			try {
				HashVO vos[] = UIUtil.getHashVoArrayByDS(null, "select * from v_sal_personinfo");
				for (int i = 0; i < vos.length; i++) {
					id_uservo.put(vos[i].getStringValue("id"), vos[i]);
				}
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
			queryPanel.addBillQuickActionListener(this);
			QueryCPanel_UIRefPanel month_endRef = (QueryCPanel_UIRefPanel) queryPanel.getCompentByKey("month_end");
			String checkDate = new SalaryUIUtil().getCheckDate();
			month_endRef.setValue(checkDate);
			QueryCPanel_UIRefPanel month_startRef = (QueryCPanel_UIRefPanel) queryPanel.getCompentByKey("month_start");
			if (checkDate != null && checkDate.length() > 4) {
				month_startRef.setValue(checkDate.substring(0, 4) + "-01");
			}
			JLabel label = new JLabel(UIUtil.getImage("process.gif"));
			mainPanel.add(label, BorderLayout.NORTH);
			JPanel topPanel = new WLTPanel(new BorderLayout());
			WLTPanel btn_panel = new WLTPanel(new FlowLayout(FlowLayout.LEFT));
			btn_panel.setOpaque(false);
			btn_view = new WLTButton("Ԥ��", UIUtil.getImage("eye.png"));
			btn_view.addActionListener(this);
			btn_exportExcel = new WLTButton("����Excel", UIUtil.getImage("icon_xls.gif"));
			btn_export_word = new WLTButton("����Word", UIUtil.getImage("word.jpg"));
			btn_panel.add(btn_view);
			btn_panel.add(btn_exportExcel);
			btn_panel.add(btn_export_word);
			btn_export_word.addActionListener(this);
			btn_exportExcel.addActionListener(this);
			topPanel.setOpaque(false);
			topPanel.add(queryPanel, BorderLayout.CENTER);
			topPanel.add(btn_panel, BorderLayout.SOUTH);
			topPanel.setMinimumSize(new Dimension(100, 80));
			WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, topPanel, mainPanel);
			splitPanel.setDividerLocation(80);
			splitPanel.setOpaque(false);
			this.add(splitPanel);
			java.util.Timer timer = new java.util.Timer();
			timer.schedule(new TimerTask() {
				public void run() {
					onQuery();
				}
			}, 50);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private SalaryServiceIfc getService() {
		if (service == null) {
			try {
				service = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
		return service;
	}

	private WLTTabbedPane tabp;

	private void onQuery() {
		try {
			if (!queryPanel.checkValidate()) {
				return;
			}
			String beginDate = (String) queryPanel.getValueAt("month_start").toString();
			String endDate = (String) queryPanel.getValueAt("month_end").toString();
			String[] logid = UIUtil.getStringArrayFirstColByDS(null, "select * from sal_target_check_log where checkdate>='" + beginDate + "' and checkdate<='" + endDate + "'");
			if (logid.length > 0) {
				Object[][] obj = getService().calcYearPersonCheckReport(logid);
				mainPanel.removeAll();
				mainPanel.setLayout(new BorderLayout());
				tabp = new WLTTabbedPane();
				for (int i = 0; i < obj.length; i++) {
					BillCellPanel cellP = new BillCellPanel((BillCellVO) obj[i][1]);
					tabp.addTab((String) obj[i][0], cellP);
				}
				mainPanel.add(tabp);
				mainPanel.updateUI();
				for (int i = 0; i < obj.length; i++) {
					BillCellPanel cellP = (BillCellPanel) tabp.getComponentAt(i);
					cellP.setAllowShowPopMenu(false);
					if (cellP != null && cellP.getColumnCount() > 1 && cellP.getRowCount() > 1) {
						cellP.setLockedCell(1, 1);
					}
				}
			} else {
				mainPanel.add(new WLTLabel("û���ҵ����˼ƻ�"));
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	public void actionPerformed(ActionEvent actionevent) {
		if (actionevent.getSource() == btn_view) {
			onView();
		} else if (actionevent.getSource() == btn_export_word) {
			onExport();
		} else if (actionevent.getSource() == btn_exportExcel) {
			onExportExcel();
		} else {
			onQuery();
		}
	}

	/*
	 * ����word��ʽԤ����
	 */
	public void onView() {
		if (tabp != null) {
			BillCellPanel cellPanel = (BillCellPanel) tabp.getComponentAt(tabp.getSelectedIndex());
			JButton btn = tabp.getJButtonAt(tabp.getSelectedIndex());
			String checktype = btn.getText();
			try {
				List<HashVO> viewHashvoList = new ArrayList<HashVO>();
				int[] rows = cellPanel.getTable().getSelectedRows();
				try {
					if (rows.length > 0) {
						int colcount = cellPanel.getTable().getColumnCount();
						for (int i = 0; i < rows.length; i++) {
							if (rows[i] > 0) {
								HashVO hvo = new HashVO();
								BillCellItemVO itemvo = (BillCellItemVO) cellPanel.getTable().getValueAt(rows[i], 0);
								String userid = (String) itemvo.getCustProperty("userid");
								HashVO uservo = id_uservo.get(userid);
								if (uservo == null) {
									continue;
								}
								hvo.setAttributeValue("code", uservo.getStringValue("code", ""));
								hvo.setAttributeValue("tellerno", uservo.getStringValue("tellerno", ""));
								hvo.setAttributeValue("maindeptid", uservo.getStringValue("maindeptid", ""));
								hvo.setAttributeValue("userid", userid);
								hvo.setAttributeValue("id", userid);
								for (int j = 0; j < colcount; j++) {
									String key = cellPanel.getValueAt(0, j);
									String value = cellPanel.getValueAt(rows[i], j);
									hvo.setAttributeValue(key, value);
								}
								viewHashvoList.add(hvo);
							}
						}
					}
				} catch (Exception ex) {
					MessageBox.showException(this, ex);

				}
				BillDialog dialog = new BillDialog(mainPanel, "Ԥ��", 1000, 800);
				if (viewHashvoList.size() == 0) {
					MessageBox.show(mainPanel, "��ѡ��1-10������Ԥ��.");
					return;
				} else {
					WLTTabbedPane tabpane = new WLTTabbedPane();
					BillCellVO cellvo = null;
					for (int i = 0; i < viewHashvoList.size(); i++) {
						if (i > 10) {
							break;
						}
						HashVO hvo = viewHashvoList.get(i);
						String cellTemplet = getMenuConfMapValueAsStr(checktype);
						try {
							if ("�߹�".equals(checktype) || "�в�".equals(checktype)) {
								if (TBUtil.isEmpty(cellTemplet)) {
									cellTemplet = "������Ϫũ������Ա�����˱�";
								}
							} else if ("һ����Ա".equals(checktype)) {
								if (TBUtil.isEmpty(cellTemplet)) {
									cellTemplet = "������Ϫũ������Ա�����˱�(һ��Ա��)";
								}
							}
							cellvo = UIUtil.getMetaDataService().getBillCellVO(cellTemplet, null, null);
						} catch (Exception ex) {
							WLTLogger.getLogger(YearPersonCheckReportWKPanel.class).error("��������û���ҵ�[" + checktype + "]��BillCellPanelģ��.");
						}
						if (cellvo == null) {
							BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
							listpanel.QueryDataByCondition(" templetcode like '%����%'");
							BillListDialog listdialog = new BillListDialog(mainPanel, "��ѡ�����Ӧ��ģ��", listpanel);
							listdialog.setVisible(true);
							if (listdialog.getCloseType() != 1) {
								return;
							}
							BillVO rtvos[] = listdialog.getReturnBillVOs();
							if (rtvos.length > 0) {
								cellvo = UIUtil.getMetaDataService().getBillCellVO(rtvos[0].getStringValue("templetcode"), null, null);
							} else {
								return;
							}
						}
						BillCellVO cvo = getService().parseCellTempetToWord(cellvo, hvo);
						BillCellPanel cellpanel = new BillCellPanel();
						cellpanel.setToolBarVisiable(false);
						cellpanel.loadBillCellData(cvo);
						if (viewHashvoList.size() == 1) {
							dialog.getContentPane().add(cellpanel);
						} else {
							tabpane.addTab(hvo.getStringValue("����"), cellpanel);
						}
					}
					if (viewHashvoList.size() > 1) {
						dialog.add(tabpane);
					}
				}
				dialog.locationToCenterPosition();
				dialog.setVisible(true);
			} catch (Exception ex) {
				MessageBox.showException(this, ex);
			}
		}
	}

	/**
	 * @param _exportType
	 *            0��ȫ������,1��ѡ���Ե���
	 */
	public void onExport() {
		int index = MessageBox.showOptionDialog(mainPanel, "��ѡ�񵼳���ʽ", "��ʾ", new String[] { "����ѡ����", "����������" });
		if (index < 0) {
			return;
		}
		onExportWord(index);
	}

	private HashVO exportVOs[];

	public void onExportWord(int _exportType) {
		JButton btn = tabp.getJButtonAt(tabp.getSelectedIndex());
		final String checktype = btn.getText();
		exportVOs = null;
		BillCellPanel cellPanel = (BillCellPanel) tabp.getComponentAt(tabp.getSelectedIndex());
		List<HashVO> selecthashvolist = new ArrayList<HashVO>();
		int[] row = null;
		if (_exportType == 0) {
			row = cellPanel.getTable().getSelectedRows();
		} else {
			int rowcount = cellPanel.getRowCount();
			row = new int[rowcount];
			for (int i = 0; i < rowcount; i++) {
				row[i] = i;
			}
		}
		try {
			if (row.length > 0) {
				int colcount = cellPanel.getTable().getColumnCount();
				for (int i = 0; i < row.length; i++) {
					if (row[i] > 0) {
						HashVO hvo = new HashVO();
						BillCellItemVO itemvo = (BillCellItemVO) cellPanel.getTable().getValueAt(row[i], 0);
						String userid = (String) itemvo.getCustProperty("userid");
						HashVO uservo = id_uservo.get(userid);
						if (uservo == null) {
							continue;
						}
						hvo.setAttributeValue("code", uservo.getStringValue("code", ""));
						hvo.setAttributeValue("tellerno", uservo.getStringValue("tellerno", ""));
						hvo.setAttributeValue("maindeptid", uservo.getStringValue("maindeptid", ""));
						hvo.setAttributeValue("userid", userid);
						hvo.setAttributeValue("id", userid);
						for (int j = 0; j < colcount; j++) {
							String key = cellPanel.getValueAt(0, j);
							String value = cellPanel.getValueAt(row[i], j);
							hvo.setAttributeValue(key, value);
						}
						selecthashvolist.add(hvo);
					}
				}
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);

		}
		if (selecthashvolist.size() == 0) {
			MessageBox.show(mainPanel, "��ѡ��ɵ���������.");
			return;
		}
		exportVOs = selecthashvolist.toArray(new HashVO[0]);
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("��ѡ��Ҫ���浽��Ŀ¼,��������" + exportVOs.length + "���ļ�.");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		int result = fc.showSaveDialog(this);

		if (result != JFileChooser.APPROVE_OPTION) { // �������ȷ����
			return;
		}
		final String savePath = fc.getSelectedFile().getPath();
		new SplashWindow(mainPanel, "���ڵ���...", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				SplashWindow splash = (SplashWindow) e.getSource();
				try {
					BillCellVO cellvo = null;
					String cellTemplet = getMenuConfMapValueAsStr(checktype);
					try {
						if ("�߹�".equals(checktype) || "�в�".equals(checktype)) {
							if (TBUtil.isEmpty(cellTemplet)) {
								cellTemplet = "������Ϫũ������Ա�����˱�";
							}
						} else if ("һ����Ա".equals(checktype)) {
							if (TBUtil.isEmpty(cellTemplet)) {
								cellTemplet = "������Ϫũ������Ա�����˱�(һ��Ա��)";
							}
						}
						cellvo = UIUtil.getMetaDataService().getBillCellVO(cellTemplet, null, null);
					} catch (Exception ex) {
						WLTLogger.getLogger(YearPersonCheckReportWKPanel.class).error("��������û���ҵ�[" + checktype + "]��BillCellPanelģ��.");
					}
					if (cellvo == null) {
						BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
						listpanel.QueryDataByCondition(" templetcode like '%����%'");
						BillListDialog listdialog = new BillListDialog(mainPanel, "��ѡ�����Ӧ��ģ��", listpanel);
						listdialog.setVisible(true);
						if (listdialog.getCloseType() != 1) {
							return;
						}
						BillVO rtvos[] = listdialog.getReturnBillVOs();
						if (rtvos.length > 0) {
							cellvo = UIUtil.getMetaDataService().getBillCellVO(rtvos[0].getStringValue("templetcode"), null, null);
						} else {
							return;
						}
					}
					int count = exportVOs.length;
					ReportExportWord exportword = new ReportExportWord();
					for (int i = 0; i < count; i++) {
						BillCellVO cellvo_new = SalaryUIUtil.getService().parseCellTempetToWord(cellvo, exportVOs[i]);
						exportword.exportWordFile(cellvo_new, savePath, exportVOs[i].getStringValue("����"));
						splash.setWaitInfo("��������:" + (i + 1) + "/" + count);
					}
					MessageBox.show(splash, "�����ɹ�");
				} catch (Exception ex) {
					splash.closeWindow();
					MessageBox.showException(mainPanel, ex);
				}
			}
		}, false);

	}

	/**
	 * ���б����ݵ�����excel�ļ���
	 */
	private void onExportExcel() {
		JButton btn = tabp.getJButtonAt(tabp.getSelectedIndex());
		BillCellPanel cellPanel = (BillCellPanel) tabp.getComponentAt(tabp.getSelectedIndex());
		String filename = btn.getText();
		cellPanel.exportExcel(filename);
	}

	private String getToday() {
		GregorianCalendar gc = new GregorianCalendar();
		int year = gc.get(Calendar.YEAR);
		int month = gc.get(Calendar.MONTH) + 1;
		String month_str = month < 10 ? ("0" + month) : ("" + month);
		return year + "-" + month_str;
	}
}
