package cn.com.infostrategy.ui.sysapp.dictmanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPopupButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_TextField;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

public class WLTTableDfManageMentPanel extends AbstractWorkPanel implements BillListSelectListener {
	private BillListPanel billListTable = null;
	private BillListPanel billListColumns = null;
	private BillListPanel billListColumnsDFhave = null;
	private BillListPanel billListColumnsDBhave = null;
	private BillListPanel billListCompareTableBHhaveColumnsBHhaveDB = null;
	private BillListPanel billListCompareTableBHhaveColumnsOnlyDBhave = null;
	private BillListPanel billListCompareTableBHhaveColumnsOnlyDFhave = null;
	private BillListPanel billListTableCompareDFhave = null;
	private BillListPanel billListTableCompareDBhave = null;
	private BillListPanel billListTableCompareBHhave = null;
	private WLTButton exportSQL = null;
	private WLTButton exportSQL2 = null;
	private WLTButton exportSQL3 = null;
	private WLTButton exportALTERSQL = null;
	private WLTButton exportAllALTERSQL = null;
	private BillListPanel billListTableDatabase = null;
	private BillListPanel billListColumnsDatabase = null;
	private QueryCPanel_TextField textFieldDatabase = null;
	private WLTButton bt_1Datebase = null;
	private QueryCPanel_TextField qt = null;

	public void initialize() {
		//����Դ��ѯҳǩ��ʼ
		bt_1Datebase = new WLTButton("����");
		bt_1Datebase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onClick();
			}
		});
		billListTableDatabase = new BillListPanel(new DefaultTMO("����Դ���б���", new String[][] { { "����", "100" }, { "˵��", "150" } }));
		billListTableDatabase.getBillListBtnPanel().addButton(bt_1Datebase);
		billListTableDatabase.getBillListBtnPanel().paintButton();
		billListTableDatabase.addBillListSelectListener(this);
		billListTableDatabase.getQuickQueryPanel().setVisible(true);
		onshow(null);
		textFieldDatabase = new QueryCPanel_TextField(billListTableDatabase.getTempletItemVO("����"));
		billListColumnsDatabase = new BillListPanel(new DefaultTMO("����", new String[][] { { "����", "100" }, { "˵��", "150" }, { "����", "150" }, { "����", "150" }, { "�Ƿ��ܿ�", "150" } }));
		WLTSplitPane spDatabase = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billListTableDatabase, billListColumnsDatabase);
		myBillQueryPanelDb(billListTableDatabase.getQuickQueryPanel(), "DB");
		//����Դ��ѯҳǩ����
		//ƽ̨���ѯ��Աȿ�ʼ-----------------------
		exportSQL = new WLTButton("����CREATE���");
		exportSQL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myExport(e);
			}
		});
		exportSQL2 = new WLTButton("����CREATE���");
		exportSQL2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myExport(e);
			}
		});
		exportSQL3 = new WLTButton("����CREATE���");
		exportSQL3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myExport(e);
			}
		});
		DefaultTMO dtmo = new DefaultTMO("ƽ̨���б�", new String[][] { { "����", "100" }, { "˵��", "150" } });
		billListTable = new BillListPanel(dtmo);
		billListTableCompareDFhave = new BillListPanel(new DefaultTMO("ֻƽ̨�����еı�", new String[][] { { "����", "100" }, { "˵��", "150" } }));
		billListTableCompareDBhave = new BillListPanel(new DefaultTMO("ֻ���ݿ��еı�", new String[][] { { "����", "100" }, { "˵��", "150" } }));
		billListTableCompareBHhave = new BillListPanel(new DefaultTMO("���߶��еı���", new String[][] { { "����", "100" }, { "˵��", "150" } }));
		billListTable.getQuickQueryPanel().setVisible(true);
		billListTable.getBillListBtnPanel().addButton(exportSQL);
		billListTable.getBillListBtnPanel().paintButton();
		billListTableCompareDFhave.getBillListBtnPanel().addButton(exportSQL2);
		billListTableCompareDFhave.getBillListBtnPanel().paintButton();
		billListTableCompareBHhave.getBillListBtnPanel().addButton(exportSQL3);
		billListTableCompareBHhave.getBillListBtnPanel().paintButton();
		qt = new QueryCPanel_TextField(billListTable.getTempletItemVO("����"));
		billListTable.addBillListSelectListener(this);
		billListColumns = new BillListPanel(new DefaultTMO("����", new String[][] { { "����", "100" }, { "˵��", "150" }, { "����", "150" }, { "����", "150" }, { "�Ƿ��ܿ�", "150" } }));
		billListColumnsDFhave = new BillListPanel(new DefaultTMO("����", new String[][] { { "����", "100" }, { "˵��", "150" }, { "����", "150" }, { "����", "150" }, { "�Ƿ��ܿ�", "150" } }));
		billListColumnsDBhave = new BillListPanel(new DefaultTMO("����", new String[][] { { "����", "100" }, { "˵��", "150" }, { "����", "150" }, { "����", "150" }, { "�Ƿ��ܿ�", "150" } }));
		billListCompareTableBHhaveColumnsBHhaveDB = new BillListPanel(new DefaultTMO("����Դ��ƽ̨���嶼�е��У�����Դ/ƽ̨���壩", new String[][] { { "����", "200" }, { "˵��", "200" }, { "����", "200" }, { "����", "200" }, { "�Ƿ��ܿ�", "150" } }));
		billListCompareTableBHhaveColumnsOnlyDFhave = new BillListPanel(new DefaultTMO("ֻƽ̨�е���", new String[][] { { "����", "100" }, { "˵��", "150" }, { "����", "150" }, { "����", "150" }, { "�Ƿ��ܿ�", "150" } }));
		exportALTERSQL = new WLTButton("����ѡ�б��ALTER���");
		exportAllALTERSQL = new WLTButton("�������ж��б��ALTER���");
		exportALTERSQL.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				myExportAlter(e);
			}
			
		});
		exportAllALTERSQL.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				myExportAllAlter(e);
			}
			
		});
		billListCompareTableBHhaveColumnsOnlyDFhave.getBillListBtnPanel().addButton(exportALTERSQL);
		billListCompareTableBHhaveColumnsOnlyDFhave.getBillListBtnPanel().addButton(exportAllALTERSQL);
		billListCompareTableBHhaveColumnsOnlyDFhave.getBillListBtnPanel().paintButton();
		billListCompareTableBHhaveColumnsOnlyDBhave = new BillListPanel(new DefaultTMO("ֻ����Դ�е���", new String[][] { { "����", "100" }, { "˵��", "150" }, { "����", "150" }, { "����", "150" }, { "�Ƿ��ܿ�", "150" } }));
		billListTableCompareDFhave.addBillListSelectListener(this);
		billListTableCompareDBhave.addBillListSelectListener(this);
		billListTableCompareBHhave.addBillListSelectListener(this);
		WLTTabbedPane wlttp = new WLTTabbedPane();
		WLTTabbedPane wlttpCompare = new WLTTabbedPane();
		WLTTabbedPane wlttpCompareTableBHhave = new WLTTabbedPane();
		wlttpCompareTableBHhave.addTab("ֻƽ̨�е���", billListCompareTableBHhaveColumnsOnlyDFhave);
		wlttpCompareTableBHhave.addTab("ֻ����Դ�е���", billListCompareTableBHhaveColumnsOnlyDBhave);
		wlttpCompareTableBHhave.addTab("���߶��е���", billListCompareTableBHhaveColumnsBHhaveDB);
		try {
			SysAppServiceIfc sysAppServiceIfc = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
			String[][] s = sysAppServiceIfc.getAllTableDefineNames();
			if (s != null && s.length > 0) {
				for (int i = 0; i < s.length; i++) {
					int li_newrow = billListTable.addEmptyRow(false);
					billListTable.setValueAt(new StringItemVO(s[i][0]), li_newrow, "����");
					billListTable.setValueAt(new StringItemVO(s[i][1]), li_newrow, "˵��");
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getAllDFhaveTable();
		getAllDBhaveTable();
		getAllBHhaveTable();
		WLTSplitPane sp = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billListTable, billListColumns);
		WLTSplitPane spDFhava = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billListTableCompareDFhave, billListColumnsDFhave);
		WLTSplitPane spDBhava = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billListTableCompareDBhave, billListColumnsDBhave);
		WLTSplitPane spBHhava = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billListTableCompareBHhave, wlttpCompareTableBHhave);
		wlttpCompare.addTab("ֻƽ̨��", spDFhava);
		wlttpCompare.addTab("ֻ����Դ��", spDBhava);
		wlttpCompare.addTab("���߶���", spBHhava);
		wlttp.addTab("ƽ̨��������", sp);
		wlttp.addTab("ƽ̨��������Դ��Աȹ���", wlttpCompare);
		wlttp.addTab("��ѯ���ݿ��", spDatabase);
		billListTable.getQuickQueryPanel().removeAll();
		myBillQueryPanelDb(billListTable.getQuickQueryPanel(), "");
		this.add(wlttp);
		//ƽ̨���ѯ��ԱȽ���-----------------------
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if ("ƽ̨���б�".equals(((BillListPanel) _event.getSource()).getTempletVO().getTempletcode())) {
			billListColumns.removeAllRows();
			String tableName = null;
			StringItemVO tableNameVO = (StringItemVO) _event.getCurrSelectedVO().getObject("����");
			if (tableNameVO != null) {
				tableName = tableNameVO.getStringValue();
			}
			try {
				SysAppServiceIfc sysAppServiceIfc = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
				String[][] Columns = sysAppServiceIfc.getAllColumnsDefineNames(tableName);
				if (Columns != null) {
					for (int i = 0; i < Columns.length; i++) {
						int li_newrow = billListColumns.addEmptyRow(false);
						billListColumns.setValueAt(new StringItemVO(Columns[i][0]), li_newrow, "����");
						billListColumns.setValueAt(new StringItemVO(Columns[i][1]), li_newrow, "˵��");
						billListColumns.setValueAt(new StringItemVO(Columns[i][2]), li_newrow, "����");
						billListColumns.setValueAt(new StringItemVO(Columns[i][3]), li_newrow, "����");
						billListColumns.setValueAt(new StringItemVO(Columns[i][4]), li_newrow, "�Ƿ��ܿ�");
					}
				}
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ("ֻ���ݿ��еı�".equals(((BillListPanel) _event.getSource()).getTempletVO().getTempletcode())) {
			billListColumnsDBhave.removeAllRows();
			String tableName = null;
			StringItemVO tableNameVO = (StringItemVO) _event.getCurrSelectedVO().getObject("����");
			if (tableNameVO != null) {
				tableName = tableNameVO.getStringValue();
			}
			try {
				FrameWorkCommServiceIfc frameWorkCommServiceIfc = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
				TableDataStruct tds = frameWorkCommServiceIfc.getTableDataStructByDS(null, "select * from  " + tableName + " WHERE 1 = 1");
				if (tds != null) {

					for (int i = 0; i < tds.getHeaderName().length; i++) {
						int li_newrow = billListColumnsDBhave.addEmptyRow(false);
						billListColumnsDBhave.setValueAt(new StringItemVO(tds.getHeaderName()[i]), li_newrow, "����");
						billListColumnsDBhave.setValueAt(new StringItemVO(tds.getHeaderName()[i]), li_newrow, "˵��");
						billListColumnsDBhave.setValueAt(new StringItemVO(tds.getHeaderTypeName()[i]), li_newrow, "����");
						billListColumnsDBhave.setValueAt(new StringItemVO(String.valueOf(tds.getHeaderLength()[i])), li_newrow, "����");
						billListColumnsDBhave.setValueAt(new StringItemVO(tds.getIsNullAble()[i]), li_newrow, "�Ƿ��ܿ�");
					}
				}
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ("ֻƽ̨�����еı�".equals(((BillListPanel) _event.getSource()).getTempletVO().getTempletcode())) {
			billListColumnsDFhave.removeAllRows();
			String tableName = null;
			StringItemVO tableNameVO = (StringItemVO) _event.getCurrSelectedVO().getObject("����");
			if (tableNameVO != null) {
				tableName = tableNameVO.getStringValue();
			}
			try {
				SysAppServiceIfc sysAppServiceIfc = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
				String[][] Columns = sysAppServiceIfc.getAllColumnsDefineNames(tableName);
				if (Columns != null) {
					for (int i = 0; i < Columns.length; i++) {
						int li_newrow = billListColumnsDFhave.addEmptyRow(false);
						billListColumnsDFhave.setValueAt(new StringItemVO(Columns[i][0]), li_newrow, "����");
						billListColumnsDFhave.setValueAt(new StringItemVO(Columns[i][1]), li_newrow, "˵��");
						billListColumnsDFhave.setValueAt(new StringItemVO(Columns[i][2]), li_newrow, "����");
						billListColumnsDFhave.setValueAt(new StringItemVO(Columns[i][3]), li_newrow, "����");
						billListColumnsDFhave.setValueAt(new StringItemVO(Columns[i][4]), li_newrow, "�Ƿ��ܿ�");
					}
				}
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ("���߶��еı���".equals(((BillListPanel) _event.getSource()).getTempletVO().getTempletcode())) {
			billListCompareTableBHhaveColumnsBHhaveDB.removeAllRows();
			billListCompareTableBHhaveColumnsOnlyDBhave.removeAllRows();
			billListCompareTableBHhaveColumnsOnlyDFhave.removeAllRows();
			String tableName = null;
			StringItemVO tableNameVO = (StringItemVO) _event.getCurrSelectedVO().getObject("����");
			if (tableNameVO != null) {
				tableName = tableNameVO.getStringValue();
			}
			try {
				SysAppServiceIfc sysAppServiceIfc = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
				List Columns = sysAppServiceIfc.getCompareLISTByTabName(null, tableName);
				if (Columns != null) {
					if (Columns.get(2) != null && ((String[][]) Columns.get(2)).length > 0) {
						for (int i = 0; i < ((String[][]) Columns.get(2)).length; i++) {
							int li_newrow = billListCompareTableBHhaveColumnsBHhaveDB.addEmptyRow(false);
							billListCompareTableBHhaveColumnsBHhaveDB.setValueAt(new StringItemVO(((String[][]) Columns.get(2))[i][0]), li_newrow, "����");
							billListCompareTableBHhaveColumnsBHhaveDB.setValueAt(new StringItemVO(((String[][]) Columns.get(2))[i][1]), li_newrow, "˵��");
							billListCompareTableBHhaveColumnsBHhaveDB.setValueAt(new StringItemVO(((String[][]) Columns.get(2))[i][2]), li_newrow, "����");
							billListCompareTableBHhaveColumnsBHhaveDB.setValueAt(new StringItemVO(((String[][]) Columns.get(2))[i][3]), li_newrow, "����");
							billListCompareTableBHhaveColumnsBHhaveDB.setValueAt(new StringItemVO(((String[][]) Columns.get(2))[i][4]), li_newrow, "�Ƿ��ܿ�");
						}
					}
					if (Columns.get(0) != null && ((String[][]) Columns.get(0)).length > 0) {
						for (int i = 0; i < ((String[][]) Columns.get(0)).length; i++) {
							int li_newrow = billListCompareTableBHhaveColumnsOnlyDBhave.addEmptyRow(false);
							billListCompareTableBHhaveColumnsOnlyDBhave.setValueAt(new StringItemVO(((String[][]) Columns.get(0))[i][0]), li_newrow, "����");
							billListCompareTableBHhaveColumnsOnlyDBhave.setValueAt(new StringItemVO(((String[][]) Columns.get(0))[i][1]), li_newrow, "˵��");
							billListCompareTableBHhaveColumnsOnlyDBhave.setValueAt(new StringItemVO(((String[][]) Columns.get(0))[i][2]), li_newrow, "����");
							billListCompareTableBHhaveColumnsOnlyDBhave.setValueAt(new StringItemVO(((String[][]) Columns.get(0))[i][3]), li_newrow, "����");
							billListCompareTableBHhaveColumnsOnlyDBhave.setValueAt(new StringItemVO(((String[][]) Columns.get(0))[i][4]), li_newrow, "�Ƿ��ܿ�");
						}
					}
					if (Columns.get(1) != null && ((String[][]) Columns.get(1)).length > 0) {
						for (int i = 0; i < ((String[][]) Columns.get(1)).length; i++) {
							int li_newrow = billListCompareTableBHhaveColumnsOnlyDFhave.addEmptyRow(false);
							billListCompareTableBHhaveColumnsOnlyDFhave.setValueAt(new StringItemVO(((String[][]) Columns.get(1))[i][0]), li_newrow, "����");
							billListCompareTableBHhaveColumnsOnlyDFhave.setValueAt(new StringItemVO(((String[][]) Columns.get(1))[i][1]), li_newrow, "˵��");
							billListCompareTableBHhaveColumnsOnlyDFhave.setValueAt(new StringItemVO(((String[][]) Columns.get(1))[i][2]), li_newrow, "����");
							billListCompareTableBHhaveColumnsOnlyDFhave.setValueAt(new StringItemVO(((String[][]) Columns.get(1))[i][3]), li_newrow, "����");
							billListCompareTableBHhaveColumnsOnlyDFhave.setValueAt(new StringItemVO(((String[][]) Columns.get(1))[i][4]), li_newrow, "�Ƿ��ܿ�");
						}
					}
				}
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ("����Դ���б���".equals(((BillListPanel) _event.getSource()).getTempletVO().getTempletcode())) {
			billListColumnsDatabase.removeAllRows();
			String tableName = null;
			StringItemVO tableNameVO = (StringItemVO) _event.getCurrSelectedVO().getObject("����");
			if (tableNameVO != null) {
				tableName = tableNameVO.getStringValue();
				try {
					FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);

					TableDataStruct tableDataStruct = service.getTableDataStructByDS(null, "select * from " + tableName + " where 1=1");

					if (tableDataStruct != null) {
						for (int i = 0; i < tableDataStruct.getHeaderName().length; i++) {
							int li_newrow = billListColumnsDatabase.addEmptyRow(false);
							billListColumnsDatabase.setValueAt(new StringItemVO(tableDataStruct.getHeaderName()[i]), li_newrow, "����");
							billListColumnsDatabase.setValueAt(new StringItemVO(tableDataStruct.getHeaderName()[i]), li_newrow, "˵��");
							billListColumnsDatabase.setValueAt(new StringItemVO(tableDataStruct.getHeaderTypeName()[i]), li_newrow, "����");
							billListColumnsDatabase.setValueAt(new StringItemVO(tableDataStruct.getHeaderLength()[i] + ""), li_newrow, "����");
							billListColumnsDatabase.setValueAt(new StringItemVO(tableDataStruct.getIsNullAble()[i]), li_newrow, "�Ƿ��ܿ�");
						}
					}
				} catch (WLTRemoteException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getAllTable() {
		try {
			SysAppServiceIfc sysAppServiceIfc = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
			String[][] s = sysAppServiceIfc.getAllTableDefineNames();
			if (s != null && s.length > 0) {
				for (int i = 0; i < s.length; i++) {
					int li_newrow = billListTable.addEmptyRow(false);
					billListTable.setValueAt(new StringItemVO(s[i][0]), li_newrow, "����");
					billListTable.setValueAt(new StringItemVO(s[i][1]), li_newrow, "˵��");
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getAllDFhaveTable() {
		try {
			SysAppServiceIfc sysAppServiceIfc = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
			String[][] s = sysAppServiceIfc.getAllTableOnlyDFhave();
			if (s != null && s.length > 0) {
				for (int i = 0; i < s.length; i++) {
					int li_newrow = billListTableCompareDFhave.addEmptyRow(false);
					billListTableCompareDFhave.setValueAt(new StringItemVO(s[i][0]), li_newrow, "����");
					billListTableCompareDFhave.setValueAt(new StringItemVO(s[i][1]), li_newrow, "˵��");
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getAllDBhaveTable() {
		try {
			SysAppServiceIfc sysAppServiceIfc = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
			String[][] s = sysAppServiceIfc.getAllTableOnlyDBhave();
			if (s != null && s.length > 0) {
				for (int i = 0; i < s.length; i++) {
					int li_newrow = billListTableCompareDBhave.addEmptyRow(false);
					billListTableCompareDBhave.setValueAt(new StringItemVO(s[i][0]), li_newrow, "����");
					billListTableCompareDBhave.setValueAt(new StringItemVO(s[i][1]), li_newrow, "˵��");
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getAllBHhaveTable() {
		try {
			SysAppServiceIfc sysAppServiceIfc = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
			String[][] s = sysAppServiceIfc.getAllTableBHhave();
			if (s != null && s.length > 0) {
				for (int i = 0; i < s.length; i++) {
					int li_newrow = billListTableCompareBHhave.addEmptyRow(false);
					billListTableCompareBHhave.setValueAt(new StringItemVO(s[i][0]), li_newrow, "����");
					billListTableCompareBHhave.setValueAt(new StringItemVO(s[i][1]), li_newrow, "˵��");
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onshow(String tableNamePattern) {
		billListTableDatabase.removeAllRows();
		try {
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
			String[] s = service.getAllSysTables(null, tableNamePattern);
			for (int i = 0; i < s.length; i++) {
				int li_newrow = billListTableDatabase.addEmptyRow(false); //��������Ϊfalse
				billListTableDatabase.setValueAt(new StringItemVO(s[i]), li_newrow, "����");
				billListTableDatabase.setValueAt(new StringItemVO(s[i]), li_newrow, "˵��");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void downLoadToEveryWhereMy(String _path, BillVO[] tableVO) throws Exception {

		if (tableVO != null && tableVO.length > 0) {
			File file = new File(_path);
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				for (int i = 0; i < tableVO.length; i++) {
					String sql = service.getCreateSQLByTabDefineName(tableVO[i].getRealValue("����"));
					out.write(sql);
				}
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void onBatchDownLoadFileMy(BillVO[] tableVO) {
		try {
			File f = null;
			JFileChooser chooser = null;
			if (ClientEnvironment.str_downLoadFileDir.endsWith("/") || ClientEnvironment.str_downLoadFileDir.endsWith("\\")) {
				f = new File(ClientEnvironment.str_downLoadFileDir);
				chooser = new JFileChooser(f); //
			} else {
				f = new File(ClientEnvironment.str_downLoadFileDir + "/");
				chooser = new JFileChooser(f);
			}
			chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
				public boolean accept(File file) {
					String filename = file.getName();
					return filename.endsWith(".sql");
				}

				public String getDescription() {
					return "*.sql";
				}
			});
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int li_rewult = chooser.showSaveDialog(this);
			if (li_rewult == JFileChooser.CANCEL_OPTION) {
				return;// ���ȡ��ʲôҲ����
			}

			if (chooser.getSelectedFile().isFile()) {
				MessageBox.show(this, "��������ʱ����ѡ��һ��Ŀ¼,ϵͳ����������ļ������ظ�Ŀ¼��!!"); //
				return;
			}
			String str_pathdir = chooser.getSelectedFile().getAbsolutePath(); // ;
			if (str_pathdir.endsWith("\\")) {
				str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1); //
			}			
			downLoadToEveryWhereMy(str_pathdir, tableVO);
			ClientEnvironment.str_downLoadFileDir = str_pathdir;
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void myExport(ActionEvent e) {
		try {
			BillVO[] billvos = ((BillListPanel) ((WLTButton) e.getSource()).getBillPanelFrom()).getSelectedBillVOs();
			if (billvos == null || billvos.length <= 0) {
				MessageBox.show("��ѡ��һ������������������CREATE SQL��䣡");
				return;
			}
			onBatchDownLoadFileMy(billvos);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	
	
	
	private void myExportAlter(ActionEvent e) {
		try {
			BillVO billvo=billListTableCompareBHhave.getSelectedBillVO();
			BillVO[] billvos = ((BillListPanel) ((WLTButton) e.getSource()).getBillPanelFrom()).getAllBillVOs();
			if (billvos == null || billvos.length <= 0) {
				MessageBox.show("ƽ̨�����޶���ֶΣ�");
				return;
			}
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
			StringBuffer sbb = new StringBuffer();
			for(int i=0;i<billvos.length;i++){
				sbb.append(service.getAlterSQLByTabDefineName(null,
						((StringItemVO)billvo.getObject("����")).getStringValue(),
						((StringItemVO)billvos[i].getObject("����")).getStringValue(),
						((StringItemVO)billvos[i].getObject("����")).getStringValue(),
						((StringItemVO)billvos[i].getObject("����")).getStringValue()));
			}
			
			File f = null;
			JFileChooser chooser = null;
			if (ClientEnvironment.str_downLoadFileDir.endsWith("/") || ClientEnvironment.str_downLoadFileDir.endsWith("\\")) {
				f = new File(ClientEnvironment.str_downLoadFileDir);
				chooser = new JFileChooser(f); //
			} else {
				f = new File(ClientEnvironment.str_downLoadFileDir + "/");
				chooser = new JFileChooser(f);
			}
			chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
				public boolean accept(File file) {
					String filename = file.getName();
					return filename.endsWith(".sql");
				}

				public String getDescription() {
					return "*.sql";
				}
			});
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int li_rewult = chooser.showSaveDialog(this);
			if (li_rewult == JFileChooser.CANCEL_OPTION) {
				return;// ���ȡ��ʲôҲ����
			}

			if (chooser.getSelectedFile().isFile()) {
				MessageBox.show(this, "��������ʱ����ѡ��һ��Ŀ¼,ϵͳ����������ļ������ظ�Ŀ¼��!!"); //
				return;
			}
			String str_pathdir = chooser.getSelectedFile().getAbsolutePath(); // ;
			if (str_pathdir.endsWith("\\")) {
				str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1); //
			}
			ClientEnvironment.str_downLoadFileDir = str_pathdir;
			BufferedWriter out = new BufferedWriter(new FileWriter(str_pathdir));
			out.write(sbb.toString());
			out.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private void myExportAllAlter(ActionEvent e) {
		try {
			
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
			StringBuffer sbb = new StringBuffer();
			sbb.append(service.getAllAlterSQLByTabDefineName());
			if (sbb == null || sbb.length() <= 0) {
				MessageBox.show("ƽ̨�����޶���ֶΣ�");
				return;
			}
			File f = null;
			JFileChooser chooser = null;
			if (ClientEnvironment.str_downLoadFileDir.endsWith("/") || ClientEnvironment.str_downLoadFileDir.endsWith("\\")) {
				f = new File(ClientEnvironment.str_downLoadFileDir);
				chooser = new JFileChooser(f); //
			} else {
				f = new File(ClientEnvironment.str_downLoadFileDir + "/");
				chooser = new JFileChooser(f);
			}
			chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
				public boolean accept(File file) {
					String filename = file.getName();
					return filename.endsWith(".sql");
				}

				public String getDescription() {
					return "*.sql";
				}
			});
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int li_rewult = chooser.showSaveDialog(this);
			if (li_rewult == JFileChooser.CANCEL_OPTION) {
				return;// ���ȡ��ʲôҲ����
			}

			if (chooser.getSelectedFile().isFile()) {
				MessageBox.show(this, "��������ʱ����ѡ��һ��Ŀ¼,ϵͳ����������ļ������ظ�Ŀ¼��!!"); //
				return;
			}
			String str_pathdir = chooser.getSelectedFile().getAbsolutePath(); // ;
			if (str_pathdir.endsWith("\\")) {
				str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1); //
			}
			ClientEnvironment.str_downLoadFileDir = str_pathdir;
			BufferedWriter out = new BufferedWriter(new FileWriter(str_pathdir));
			out.write(sbb.toString());
			out.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	public void onClick() {
		try {
			File f = null;
			JFileChooser chooser = null;
			if (ClientEnvironment.str_downLoadFileDir.endsWith("/") || ClientEnvironment.str_downLoadFileDir.endsWith("\\")) {
				f = new File(ClientEnvironment.str_downLoadFileDir);
				chooser = new JFileChooser(f); //
			} else {
				f = new File(ClientEnvironment.str_downLoadFileDir + "/");
				chooser = new JFileChooser(f);
			}
			chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
				public boolean accept(File file) {
					String filename = file.getName();
					return filename.endsWith(".txt");
				}

				public String getDescription() {
					return "*.txt";
				}
			});
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int li_rewult = chooser.showSaveDialog(this);
			if (li_rewult == JFileChooser.CANCEL_OPTION) {
				return;// ���ȡ��ʲôҲ����
			}

			if (chooser.getSelectedFile().isFile()) {
				MessageBox.show(this, "��������ʱ����ѡ��һ��Ŀ¼,ϵͳ����������ļ������ظ�Ŀ¼��!!"); //
				return;
			}
			String str_pathdir = chooser.getSelectedFile().getAbsolutePath(); // ;
			if (str_pathdir.endsWith("\\")) {
				str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1); //
			}
			ClientEnvironment.str_downLoadFileDir = str_pathdir;
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			FrameWorkCommServiceIfc s1 = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
			String[] a = s1.getAllSysTables(null, null);
			StringBuffer s = new StringBuffer();
			BufferedWriter out = new BufferedWriter(new FileWriter(str_pathdir));
			for (int i = 0; i < a.length; i++) {
				s.append(service.reverseCreateJavaCode(a[i]));
				out.write(s.toString());
				s.delete(0, s.length());
			}
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void myBillQueryPanelDb(BillQueryPanel billQueryPanel, String type) {
		WLTPanel btnpaneldb = new WLTPanel();
		btnpaneldb.setLayout(new BorderLayout());
		WLTPanel qtpaneldb = new WLTPanel();
		qtpaneldb.setLayout(new BorderLayout());
		JPopupMenu popupdb = new JPopupMenu("PopupMenu");
		JMenuItem menuItem_commquerydb = new JMenuItem(UIUtil.getLanguage("ͨ�ò�ѯ")); //
		JMenuItem menuItem_complexquerydb = new JMenuItem(UIUtil.getLanguage("�߼���ѯ")); //
		JMenuItem menuItem_exportConditiontoXMLdb = new JMenuItem(UIUtil.getLanguage("��������")); //

		menuItem_commquerydb.setBackground(LookAndFeel.systembgcolor);
		menuItem_complexquerydb.setBackground(LookAndFeel.systembgcolor);
		menuItem_exportConditiontoXMLdb.setBackground(LookAndFeel.systembgcolor);

		menuItem_commquerydb.setPreferredSize(new Dimension(78, 19));
		menuItem_complexquerydb.setPreferredSize(new Dimension(78, 19));
		menuItem_exportConditiontoXMLdb.setPreferredSize(new Dimension(73, 19));
		menuItem_commquerydb.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

			}
		});
		menuItem_complexquerydb.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

			}
		});
		menuItem_exportConditiontoXMLdb.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

			}
		});

		popupdb.add(menuItem_commquerydb); //
		popupdb.add(menuItem_complexquerydb); //
		if (ClientEnvironment.getInstance().isAdmin()) { // ����ǹ������
			popupdb.add(menuItem_exportConditiontoXMLdb); //
		}

		WLTPopupButton popupButtondb = new WLTPopupButton(WLTPopupButton.TYPE_WITH_RIGHT_TOGGLE, UIUtil.getLanguage("��ѯ"), null, popupdb); //
		popupButtondb.getButton().setToolTipText("�����ѯ"); //
		popupButtondb.getButton().setFont(new Font("����", Font.BOLD, 13)); //
		popupButtondb.getButton().setForeground(Color.BLUE); //
		popupButtondb.getButton().setBackground(LookAndFeel.systembgcolor); //
		popupButtondb.getButton().setIcon(UIUtil.getImage("office_089.gif")); //
		popupButtondb.getPopButton().setBackground(LookAndFeel.systembgcolor); //
		if ("DB".equals(type)) {
			popupButtondb.getButton().addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					String tableNamePattern = null;
					if (!"".equals(textFieldDatabase.getText())) {
						tableNamePattern = "%" + textFieldDatabase.getText().trim() + "%"; //ע��Ҫд��%��			
					}
					onshow(tableNamePattern);
				}

			});
		} else {
			popupButtondb.getButton().addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					billListTable.removeAllRows();
					SysAppServiceIfc sysAppServiceIfc;
					try {
						sysAppServiceIfc = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
						String[][] s = sysAppServiceIfc.getAllTableDefineNames(qt.getText());
						if (s != null && s.length > 0) {
							for (int i = 0; i < s.length; i++) {
								int li_newrow = billListTable.addEmptyRow(false);
								billListTable.setValueAt(new StringItemVO(s[i][0]), li_newrow, "����");
								billListTable.setValueAt(new StringItemVO(s[i][1]), li_newrow, "˵��");
							}
						}
					} catch (WLTRemoteException e1) {
						e1.printStackTrace();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			});
		}
		popupButtondb.setPreferredSize(new Dimension(80, 20)); // ���С
		btnpaneldb.add(popupButtondb, BorderLayout.NORTH);
		WLTButton btn_resetdb = new WLTButton("�������");
		if ("DB".equals(type)) {
			btn_resetdb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					textFieldDatabase.reset();
				}
			});
		} else {
			btn_resetdb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					qt.reset();
				}
			});
		}
		btn_resetdb.setBackground(LookAndFeel.systembgcolor); //
		btn_resetdb.setBorder(BorderFactory.createMatteBorder(0, 1, 1, 1, Color.GRAY)); //
		JPanel panel_temp_2db = new JPanel(new BorderLayout(0, 0)); //
		panel_temp_2db.setOpaque(false);
		panel_temp_2db.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		panel_temp_2db.add(btn_resetdb, BorderLayout.NORTH);
		if ("DB".equals(type)) {
			textFieldDatabase.setOpaque(false);
		} else {
			qt.setOpaque(false);
		}
		btnpaneldb.add(panel_temp_2db, BorderLayout.CENTER); //
		if ("DB".equals(type)) {
			qtpaneldb.add(textFieldDatabase, BorderLayout.CENTER);
		} else {
			qtpaneldb.add(qt, BorderLayout.CENTER);
		}
		billQueryPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		billQueryPanel.add(qtpaneldb, BorderLayout.WEST);
		billQueryPanel.add(btnpaneldb, BorderLayout.EAST); //
		JPanel panel_northdb = new JPanel();
		panel_northdb.setLayout(new BorderLayout());
		panel_northdb.setOpaque(false); //
		JPanel panel_quickquery_northdb = new JPanel(new BorderLayout()); // ��ģ���ж���İ�ť
		panel_quickquery_northdb.setOpaque(false); //
		if ("DB".equals(type)) {
			panel_quickquery_northdb.add(billListTableDatabase.getBillListBtnPanel()); //
		} else {
			panel_quickquery_northdb.add(billListTable.getBillListBtnPanel());
		}
		panel_northdb.add(panel_quickquery_northdb, BorderLayout.NORTH); // ������ٲ�ѯ��ʾ,�򽫹����������Ϸ�!!
		panel_northdb.add(billQueryPanel, BorderLayout.CENTER); // ���ٲ�ѯ���
		panel_northdb.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY)); //
		if ("DB".equals(type)) {
			billListTableDatabase.add(panel_northdb, BorderLayout.NORTH); //
		} else {
			billListTable.add(panel_northdb, BorderLayout.NORTH);
		}
	}

}
