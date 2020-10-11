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
		//数据源查询页签开始
		bt_1Datebase = new WLTButton("导出");
		bt_1Datebase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onClick();
			}
		});
		billListTableDatabase = new BillListPanel(new DefaultTMO("数据源所有表名", new String[][] { { "表名", "100" }, { "说明", "150" } }));
		billListTableDatabase.getBillListBtnPanel().addButton(bt_1Datebase);
		billListTableDatabase.getBillListBtnPanel().paintButton();
		billListTableDatabase.addBillListSelectListener(this);
		billListTableDatabase.getQuickQueryPanel().setVisible(true);
		onshow(null);
		textFieldDatabase = new QueryCPanel_TextField(billListTableDatabase.getTempletItemVO("表名"));
		billListColumnsDatabase = new BillListPanel(new DefaultTMO("列名", new String[][] { { "列名", "100" }, { "说明", "150" }, { "类型", "150" }, { "长度", "150" }, { "是否能空", "150" } }));
		WLTSplitPane spDatabase = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billListTableDatabase, billListColumnsDatabase);
		myBillQueryPanelDb(billListTableDatabase.getQuickQueryPanel(), "DB");
		//数据源查询页签结束
		//平台表查询与对比开始-----------------------
		exportSQL = new WLTButton("导出CREATE语句");
		exportSQL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myExport(e);
			}
		});
		exportSQL2 = new WLTButton("导出CREATE语句");
		exportSQL2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myExport(e);
			}
		});
		exportSQL3 = new WLTButton("导出CREATE语句");
		exportSQL3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myExport(e);
			}
		});
		DefaultTMO dtmo = new DefaultTMO("平台所有表", new String[][] { { "表名", "100" }, { "说明", "150" } });
		billListTable = new BillListPanel(dtmo);
		billListTableCompareDFhave = new BillListPanel(new DefaultTMO("只平台定义有的表", new String[][] { { "表名", "100" }, { "说明", "150" } }));
		billListTableCompareDBhave = new BillListPanel(new DefaultTMO("只数据库有的表", new String[][] { { "表名", "100" }, { "说明", "150" } }));
		billListTableCompareBHhave = new BillListPanel(new DefaultTMO("两者都有的表名", new String[][] { { "表名", "100" }, { "说明", "150" } }));
		billListTable.getQuickQueryPanel().setVisible(true);
		billListTable.getBillListBtnPanel().addButton(exportSQL);
		billListTable.getBillListBtnPanel().paintButton();
		billListTableCompareDFhave.getBillListBtnPanel().addButton(exportSQL2);
		billListTableCompareDFhave.getBillListBtnPanel().paintButton();
		billListTableCompareBHhave.getBillListBtnPanel().addButton(exportSQL3);
		billListTableCompareBHhave.getBillListBtnPanel().paintButton();
		qt = new QueryCPanel_TextField(billListTable.getTempletItemVO("表名"));
		billListTable.addBillListSelectListener(this);
		billListColumns = new BillListPanel(new DefaultTMO("列名", new String[][] { { "列名", "100" }, { "说明", "150" }, { "类型", "150" }, { "长度", "150" }, { "是否能空", "150" } }));
		billListColumnsDFhave = new BillListPanel(new DefaultTMO("列名", new String[][] { { "列名", "100" }, { "说明", "150" }, { "类型", "150" }, { "长度", "150" }, { "是否能空", "150" } }));
		billListColumnsDBhave = new BillListPanel(new DefaultTMO("列名", new String[][] { { "列名", "100" }, { "说明", "150" }, { "类型", "150" }, { "长度", "150" }, { "是否能空", "150" } }));
		billListCompareTableBHhaveColumnsBHhaveDB = new BillListPanel(new DefaultTMO("数据源与平台定义都有的列（数据源/平台定义）", new String[][] { { "列名", "200" }, { "说明", "200" }, { "类型", "200" }, { "长度", "200" }, { "是否能空", "150" } }));
		billListCompareTableBHhaveColumnsOnlyDFhave = new BillListPanel(new DefaultTMO("只平台有的列", new String[][] { { "列名", "100" }, { "说明", "150" }, { "类型", "150" }, { "长度", "150" }, { "是否能空", "150" } }));
		exportALTERSQL = new WLTButton("导出选中表的ALTER语句");
		exportAllALTERSQL = new WLTButton("导出所有都有表的ALTER语句");
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
		billListCompareTableBHhaveColumnsOnlyDBhave = new BillListPanel(new DefaultTMO("只数据源有的列", new String[][] { { "列名", "100" }, { "说明", "150" }, { "类型", "150" }, { "长度", "150" }, { "是否能空", "150" } }));
		billListTableCompareDFhave.addBillListSelectListener(this);
		billListTableCompareDBhave.addBillListSelectListener(this);
		billListTableCompareBHhave.addBillListSelectListener(this);
		WLTTabbedPane wlttp = new WLTTabbedPane();
		WLTTabbedPane wlttpCompare = new WLTTabbedPane();
		WLTTabbedPane wlttpCompareTableBHhave = new WLTTabbedPane();
		wlttpCompareTableBHhave.addTab("只平台有的列", billListCompareTableBHhaveColumnsOnlyDFhave);
		wlttpCompareTableBHhave.addTab("只数据源有的列", billListCompareTableBHhaveColumnsOnlyDBhave);
		wlttpCompareTableBHhave.addTab("两者都有的列", billListCompareTableBHhaveColumnsBHhaveDB);
		try {
			SysAppServiceIfc sysAppServiceIfc = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
			String[][] s = sysAppServiceIfc.getAllTableDefineNames();
			if (s != null && s.length > 0) {
				for (int i = 0; i < s.length; i++) {
					int li_newrow = billListTable.addEmptyRow(false);
					billListTable.setValueAt(new StringItemVO(s[i][0]), li_newrow, "表名");
					billListTable.setValueAt(new StringItemVO(s[i][1]), li_newrow, "说明");
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
		wlttpCompare.addTab("只平台有", spDFhava);
		wlttpCompare.addTab("只数据源有", spDBhava);
		wlttpCompare.addTab("两者都有", spBHhava);
		wlttp.addTab("平台定义表管理", sp);
		wlttp.addTab("平台表与数据源表对比管理", wlttpCompare);
		wlttp.addTab("查询数据库表", spDatabase);
		billListTable.getQuickQueryPanel().removeAll();
		myBillQueryPanelDb(billListTable.getQuickQueryPanel(), "");
		this.add(wlttp);
		//平台表查询与对比结束-----------------------
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if ("平台所有表".equals(((BillListPanel) _event.getSource()).getTempletVO().getTempletcode())) {
			billListColumns.removeAllRows();
			String tableName = null;
			StringItemVO tableNameVO = (StringItemVO) _event.getCurrSelectedVO().getObject("表名");
			if (tableNameVO != null) {
				tableName = tableNameVO.getStringValue();
			}
			try {
				SysAppServiceIfc sysAppServiceIfc = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
				String[][] Columns = sysAppServiceIfc.getAllColumnsDefineNames(tableName);
				if (Columns != null) {
					for (int i = 0; i < Columns.length; i++) {
						int li_newrow = billListColumns.addEmptyRow(false);
						billListColumns.setValueAt(new StringItemVO(Columns[i][0]), li_newrow, "列名");
						billListColumns.setValueAt(new StringItemVO(Columns[i][1]), li_newrow, "说明");
						billListColumns.setValueAt(new StringItemVO(Columns[i][2]), li_newrow, "类型");
						billListColumns.setValueAt(new StringItemVO(Columns[i][3]), li_newrow, "长度");
						billListColumns.setValueAt(new StringItemVO(Columns[i][4]), li_newrow, "是否能空");
					}
				}
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ("只数据库有的表".equals(((BillListPanel) _event.getSource()).getTempletVO().getTempletcode())) {
			billListColumnsDBhave.removeAllRows();
			String tableName = null;
			StringItemVO tableNameVO = (StringItemVO) _event.getCurrSelectedVO().getObject("表名");
			if (tableNameVO != null) {
				tableName = tableNameVO.getStringValue();
			}
			try {
				FrameWorkCommServiceIfc frameWorkCommServiceIfc = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
				TableDataStruct tds = frameWorkCommServiceIfc.getTableDataStructByDS(null, "select * from  " + tableName + " WHERE 1 = 1");
				if (tds != null) {

					for (int i = 0; i < tds.getHeaderName().length; i++) {
						int li_newrow = billListColumnsDBhave.addEmptyRow(false);
						billListColumnsDBhave.setValueAt(new StringItemVO(tds.getHeaderName()[i]), li_newrow, "列名");
						billListColumnsDBhave.setValueAt(new StringItemVO(tds.getHeaderName()[i]), li_newrow, "说明");
						billListColumnsDBhave.setValueAt(new StringItemVO(tds.getHeaderTypeName()[i]), li_newrow, "类型");
						billListColumnsDBhave.setValueAt(new StringItemVO(String.valueOf(tds.getHeaderLength()[i])), li_newrow, "长度");
						billListColumnsDBhave.setValueAt(new StringItemVO(tds.getIsNullAble()[i]), li_newrow, "是否能空");
					}
				}
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ("只平台定义有的表".equals(((BillListPanel) _event.getSource()).getTempletVO().getTempletcode())) {
			billListColumnsDFhave.removeAllRows();
			String tableName = null;
			StringItemVO tableNameVO = (StringItemVO) _event.getCurrSelectedVO().getObject("表名");
			if (tableNameVO != null) {
				tableName = tableNameVO.getStringValue();
			}
			try {
				SysAppServiceIfc sysAppServiceIfc = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
				String[][] Columns = sysAppServiceIfc.getAllColumnsDefineNames(tableName);
				if (Columns != null) {
					for (int i = 0; i < Columns.length; i++) {
						int li_newrow = billListColumnsDFhave.addEmptyRow(false);
						billListColumnsDFhave.setValueAt(new StringItemVO(Columns[i][0]), li_newrow, "列名");
						billListColumnsDFhave.setValueAt(new StringItemVO(Columns[i][1]), li_newrow, "说明");
						billListColumnsDFhave.setValueAt(new StringItemVO(Columns[i][2]), li_newrow, "类型");
						billListColumnsDFhave.setValueAt(new StringItemVO(Columns[i][3]), li_newrow, "长度");
						billListColumnsDFhave.setValueAt(new StringItemVO(Columns[i][4]), li_newrow, "是否能空");
					}
				}
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ("两者都有的表名".equals(((BillListPanel) _event.getSource()).getTempletVO().getTempletcode())) {
			billListCompareTableBHhaveColumnsBHhaveDB.removeAllRows();
			billListCompareTableBHhaveColumnsOnlyDBhave.removeAllRows();
			billListCompareTableBHhaveColumnsOnlyDFhave.removeAllRows();
			String tableName = null;
			StringItemVO tableNameVO = (StringItemVO) _event.getCurrSelectedVO().getObject("表名");
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
							billListCompareTableBHhaveColumnsBHhaveDB.setValueAt(new StringItemVO(((String[][]) Columns.get(2))[i][0]), li_newrow, "列名");
							billListCompareTableBHhaveColumnsBHhaveDB.setValueAt(new StringItemVO(((String[][]) Columns.get(2))[i][1]), li_newrow, "说明");
							billListCompareTableBHhaveColumnsBHhaveDB.setValueAt(new StringItemVO(((String[][]) Columns.get(2))[i][2]), li_newrow, "类型");
							billListCompareTableBHhaveColumnsBHhaveDB.setValueAt(new StringItemVO(((String[][]) Columns.get(2))[i][3]), li_newrow, "长度");
							billListCompareTableBHhaveColumnsBHhaveDB.setValueAt(new StringItemVO(((String[][]) Columns.get(2))[i][4]), li_newrow, "是否能空");
						}
					}
					if (Columns.get(0) != null && ((String[][]) Columns.get(0)).length > 0) {
						for (int i = 0; i < ((String[][]) Columns.get(0)).length; i++) {
							int li_newrow = billListCompareTableBHhaveColumnsOnlyDBhave.addEmptyRow(false);
							billListCompareTableBHhaveColumnsOnlyDBhave.setValueAt(new StringItemVO(((String[][]) Columns.get(0))[i][0]), li_newrow, "列名");
							billListCompareTableBHhaveColumnsOnlyDBhave.setValueAt(new StringItemVO(((String[][]) Columns.get(0))[i][1]), li_newrow, "说明");
							billListCompareTableBHhaveColumnsOnlyDBhave.setValueAt(new StringItemVO(((String[][]) Columns.get(0))[i][2]), li_newrow, "类型");
							billListCompareTableBHhaveColumnsOnlyDBhave.setValueAt(new StringItemVO(((String[][]) Columns.get(0))[i][3]), li_newrow, "长度");
							billListCompareTableBHhaveColumnsOnlyDBhave.setValueAt(new StringItemVO(((String[][]) Columns.get(0))[i][4]), li_newrow, "是否能空");
						}
					}
					if (Columns.get(1) != null && ((String[][]) Columns.get(1)).length > 0) {
						for (int i = 0; i < ((String[][]) Columns.get(1)).length; i++) {
							int li_newrow = billListCompareTableBHhaveColumnsOnlyDFhave.addEmptyRow(false);
							billListCompareTableBHhaveColumnsOnlyDFhave.setValueAt(new StringItemVO(((String[][]) Columns.get(1))[i][0]), li_newrow, "列名");
							billListCompareTableBHhaveColumnsOnlyDFhave.setValueAt(new StringItemVO(((String[][]) Columns.get(1))[i][1]), li_newrow, "说明");
							billListCompareTableBHhaveColumnsOnlyDFhave.setValueAt(new StringItemVO(((String[][]) Columns.get(1))[i][2]), li_newrow, "类型");
							billListCompareTableBHhaveColumnsOnlyDFhave.setValueAt(new StringItemVO(((String[][]) Columns.get(1))[i][3]), li_newrow, "长度");
							billListCompareTableBHhaveColumnsOnlyDFhave.setValueAt(new StringItemVO(((String[][]) Columns.get(1))[i][4]), li_newrow, "是否能空");
						}
					}
				}
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ("数据源所有表名".equals(((BillListPanel) _event.getSource()).getTempletVO().getTempletcode())) {
			billListColumnsDatabase.removeAllRows();
			String tableName = null;
			StringItemVO tableNameVO = (StringItemVO) _event.getCurrSelectedVO().getObject("表名");
			if (tableNameVO != null) {
				tableName = tableNameVO.getStringValue();
				try {
					FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);

					TableDataStruct tableDataStruct = service.getTableDataStructByDS(null, "select * from " + tableName + " where 1=1");

					if (tableDataStruct != null) {
						for (int i = 0; i < tableDataStruct.getHeaderName().length; i++) {
							int li_newrow = billListColumnsDatabase.addEmptyRow(false);
							billListColumnsDatabase.setValueAt(new StringItemVO(tableDataStruct.getHeaderName()[i]), li_newrow, "列名");
							billListColumnsDatabase.setValueAt(new StringItemVO(tableDataStruct.getHeaderName()[i]), li_newrow, "说明");
							billListColumnsDatabase.setValueAt(new StringItemVO(tableDataStruct.getHeaderTypeName()[i]), li_newrow, "类型");
							billListColumnsDatabase.setValueAt(new StringItemVO(tableDataStruct.getHeaderLength()[i] + ""), li_newrow, "长度");
							billListColumnsDatabase.setValueAt(new StringItemVO(tableDataStruct.getIsNullAble()[i]), li_newrow, "是否能空");
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
					billListTable.setValueAt(new StringItemVO(s[i][0]), li_newrow, "表名");
					billListTable.setValueAt(new StringItemVO(s[i][1]), li_newrow, "说明");
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
					billListTableCompareDFhave.setValueAt(new StringItemVO(s[i][0]), li_newrow, "表名");
					billListTableCompareDFhave.setValueAt(new StringItemVO(s[i][1]), li_newrow, "说明");
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
					billListTableCompareDBhave.setValueAt(new StringItemVO(s[i][0]), li_newrow, "表名");
					billListTableCompareDBhave.setValueAt(new StringItemVO(s[i][1]), li_newrow, "说明");
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
					billListTableCompareBHhave.setValueAt(new StringItemVO(s[i][0]), li_newrow, "表名");
					billListTableCompareBHhave.setValueAt(new StringItemVO(s[i][1]), li_newrow, "说明");
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
				int li_newrow = billListTableDatabase.addEmptyRow(false); //必须设置为false
				billListTableDatabase.setValueAt(new StringItemVO(s[i]), li_newrow, "表名");
				billListTableDatabase.setValueAt(new StringItemVO(s[i]), li_newrow, "说明");
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
					String sql = service.getCreateSQLByTabDefineName(tableVO[i].getRealValue("表名"));
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
				return;// 点击取消什么也不做
			}

			if (chooser.getSelectedFile().isFile()) {
				MessageBox.show(this, "批量下载时必须选择一个目录,系统将会把所有文件都下载该目录下!!"); //
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
				MessageBox.show("请选择一条或多条定义表来导出CREATE SQL语句！");
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
				MessageBox.show("平台定义无多出字段！");
				return;
			}
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
			StringBuffer sbb = new StringBuffer();
			for(int i=0;i<billvos.length;i++){
				sbb.append(service.getAlterSQLByTabDefineName(null,
						((StringItemVO)billvo.getObject("表名")).getStringValue(),
						((StringItemVO)billvos[i].getObject("列名")).getStringValue(),
						((StringItemVO)billvos[i].getObject("类型")).getStringValue(),
						((StringItemVO)billvos[i].getObject("长度")).getStringValue()));
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
				return;// 点击取消什么也不做
			}

			if (chooser.getSelectedFile().isFile()) {
				MessageBox.show(this, "批量下载时必须选择一个目录,系统将会把所有文件都下载该目录下!!"); //
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
				MessageBox.show("平台定义无多出字段！");
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
				return;// 点击取消什么也不做
			}

			if (chooser.getSelectedFile().isFile()) {
				MessageBox.show(this, "批量下载时必须选择一个目录,系统将会把所有文件都下载该目录下!!"); //
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
				return;// 点击取消什么也不做
			}

			if (chooser.getSelectedFile().isFile()) {
				MessageBox.show(this, "批量下载时必须选择一个目录,系统将会把所有文件都下载该目录下!!"); //
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
		JMenuItem menuItem_commquerydb = new JMenuItem(UIUtil.getLanguage("通用查询")); //
		JMenuItem menuItem_complexquerydb = new JMenuItem(UIUtil.getLanguage("高级查询")); //
		JMenuItem menuItem_exportConditiontoXMLdb = new JMenuItem(UIUtil.getLanguage("导出条件")); //

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
		if (ClientEnvironment.getInstance().isAdmin()) { // 如果是管理身份
			popupdb.add(menuItem_exportConditiontoXMLdb); //
		}

		WLTPopupButton popupButtondb = new WLTPopupButton(WLTPopupButton.TYPE_WITH_RIGHT_TOGGLE, UIUtil.getLanguage("查询"), null, popupdb); //
		popupButtondb.getButton().setToolTipText("点击查询"); //
		popupButtondb.getButton().setFont(new Font("宋体", Font.BOLD, 13)); //
		popupButtondb.getButton().setForeground(Color.BLUE); //
		popupButtondb.getButton().setBackground(LookAndFeel.systembgcolor); //
		popupButtondb.getButton().setIcon(UIUtil.getImage("office_089.gif")); //
		popupButtondb.getPopButton().setBackground(LookAndFeel.systembgcolor); //
		if ("DB".equals(type)) {
			popupButtondb.getButton().addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					String tableNamePattern = null;
					if (!"".equals(textFieldDatabase.getText())) {
						tableNamePattern = "%" + textFieldDatabase.getText().trim() + "%"; //注意要写“%”			
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
								billListTable.setValueAt(new StringItemVO(s[i][0]), li_newrow, "表名");
								billListTable.setValueAt(new StringItemVO(s[i][1]), li_newrow, "说明");
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
		popupButtondb.setPreferredSize(new Dimension(80, 20)); // 设大小
		btnpaneldb.add(popupButtondb, BorderLayout.NORTH);
		WLTButton btn_resetdb = new WLTButton("清空条件");
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
		JPanel panel_quickquery_northdb = new JPanel(new BorderLayout()); // 在模板中定义的按钮
		panel_quickquery_northdb.setOpaque(false); //
		if ("DB".equals(type)) {
			panel_quickquery_northdb.add(billListTableDatabase.getBillListBtnPanel()); //
		} else {
			panel_quickquery_northdb.add(billListTable.getBillListBtnPanel());
		}
		panel_northdb.add(panel_quickquery_northdb, BorderLayout.NORTH); // 如果快速查询显示,则将工具条放在上方!!
		panel_northdb.add(billQueryPanel, BorderLayout.CENTER); // 快速查询面板
		panel_northdb.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, Color.GRAY)); //
		if ("DB".equals(type)) {
			billListTableDatabase.add(panel_northdb, BorderLayout.NORTH); //
		} else {
			billListTable.add(panel_northdb, BorderLayout.NORTH);
		}
	}

}
