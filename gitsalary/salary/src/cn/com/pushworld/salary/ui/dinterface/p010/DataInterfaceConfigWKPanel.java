package cn.com.pushworld.salary.ui.dinterface.p010;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Date;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;

/**
 * 数据接口配置界面。
 * @author haoming,zhangyingchuang
 * create by 2014-7-2
 */
public class DataInterfaceConfigWKPanel extends AbstractWorkPanel implements BillListSelectListener, ChangeListener, ActionListener {

	private static final long serialVersionUID = 8499213841433283052L;
	private BillListPanel parentpanel = null;
	private BillListPanel childrenpanel = null;
	private WLTButton add_btn, edit_btn, del_btn, view_btn, up_btn, down_btn, btn_action, btn_synchronous, history_btn, btn_cancel,btn_impall;
	private WLTButton add_btn1, edit_btn1, del_btn1, view_btn1, up_btn1, down_btn1;
	private BillQueryPanel billQueryPanel, queryPanel;
	private SalaryTBUtil salaryTBUtil = new SalaryTBUtil();
	private TBUtil tbUtil = new TBUtil();
	private BillDialog billDialog = null;
	private JTabbedPane tabbedPane = null;
	private HashMap timemap = null;
	private String iname = null;
	private HashMap hasclickTabMap;
	private BillListPanel billListPanel;
	private String currtime = null;
	private JPanel centerpanel = null;
	private StringBuffer fag=null;

	public void initialize() {
		parentpanel = new BillListPanel("SAL_DATA_INTERFACE_MAIN_CODE1");
		add_btn = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		add_btn.addActionListener(this);
		edit_btn = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		del_btn = new WLTButton("删除");
		del_btn.addActionListener(this);
		view_btn = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		up_btn = new WLTButton(UIUtil.getImage("up1.gif"));
		down_btn = new WLTButton(UIUtil.getImage("down1.gif")); //
		up_btn.setPreferredSize(new Dimension(20, 20)); //
		down_btn.setPreferredSize(new Dimension(20, 20)); //
		up_btn.setToolTipText("上移顺序"); //
		down_btn.setToolTipText("下移顺序"); //
		up_btn.addActionListener(this); //
		down_btn.addActionListener(this); //
		btn_action = new WLTButton("创建表");//zzl[2018-10-19]
		btn_action.addActionListener(this);
		btn_synchronous = new WLTButton("同步数据");//zzl[2018-10-19]
		btn_synchronous.addActionListener(this);
		history_btn = new WLTButton("查看历史数据");
		history_btn.addActionListener(this);
		btn_impall=new WLTButton("批量同步数据");//zzl[2018-11-23] 批量同步接口数据
		btn_impall.addActionListener(this);
		if(ClientEnvironment.isAdmin()){
			parentpanel.addBatchBillListButton(new WLTButton[] { add_btn, edit_btn, del_btn, view_btn, btn_action, btn_synchronous, history_btn, up_btn, down_btn, btn_impall});
		}else{
			parentpanel.addBatchBillListButton(new WLTButton[] {btn_impall});
		}
		parentpanel.repaintBillListButton();
		parentpanel.addBillListSelectListener(this);
		billQueryPanel = parentpanel.getQuickQueryPanel();
		billQueryPanel.addBillQuickActionListener(this);
		childrenpanel = new BillListPanel("SAL_DATA_INTERFACE_ITEM_CONF_CODE1");
		add_btn1 = new WLTButton("新增");
		add_btn1.addActionListener(this);
		edit_btn1 = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		del_btn1 = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		view_btn1 = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		up_btn1 = new WLTButton(UIUtil.getImage("up1.gif"));
		down_btn1 = new WLTButton(UIUtil.getImage("down1.gif")); //
		up_btn1.setPreferredSize(new Dimension(20, 20)); //
		down_btn1.setPreferredSize(new Dimension(20, 20)); //
		up_btn1.setToolTipText("上移顺序"); //
		down_btn1.setToolTipText("下移顺序"); //
		up_btn1.addActionListener(this); //
		down_btn1.addActionListener(this); //
		if(ClientEnvironment.isAdmin()){
			childrenpanel.addBatchBillListButton(new WLTButton[] { add_btn1, edit_btn1, del_btn1, view_btn1, up_btn1, down_btn1 });
		}
		childrenpanel.repaintBillListButton();
		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, parentpanel, childrenpanel);
		splitPane.setDividerLocation(280);
		this.add(splitPane);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == add_btn1) {
			try {
				doAddItem();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == add_btn) {
			try {
				doAddParent();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == del_btn) {
			doDeleteAll();
		} else if (e.getSource() == billQueryPanel) {
			parentpanel.QueryData(billQueryPanel.getQuerySQL() + " order by seq asc");
			childrenpanel.clearTable();

		} else if (e.getSource() == up_btn) {
			moveUp(parentpanel);
		} else if (e.getSource() == down_btn) {
			moveDown(parentpanel);
		} else if (e.getSource() == up_btn1) {
			moveUp(childrenpanel);
		} else if (e.getSource() == down_btn1) {
			moveDown(childrenpanel);
		} else if (e.getSource() == btn_action) {
			onAction();
		} else if (e.getSource() == btn_synchronous) {
			onSynchronous();
		} else if (e.getSource() == history_btn) {
			try {
				showHistory(null);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		} else if (e.getSource() == jump) {
			try {
				RefDialog_Date date = new RefDialog_Date(tabbedPane, "请选择一个日期", new RefItemVO(oldselectdate, oldselectdate, oldselectdate), null);
				date.initialize();
				date.setVisible(true);
				if (date.getCloseType() == 1) {
					RefItemVO select = date.getReturnRefItemVO();
					if (select != null) {
						oldselectdate = select.getId();
						showHistory(select.getId());
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if(e.getSource() == btn_impall ){
			impAllDate();
		}
	}
/**
 * zzl[2018-11-23] 批量同步數據接口數據
 * 
 */
	private void impAllDate() {		
		final BillVO [] vo = parentpanel.getAllBillVOs();
		if (vo == null) {
			return;
		}
		final SalaryServiceIfc ifc;
		try {
			ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			final String uid = JOptionPane.showInputDialog(parentpanel, "请输入要手工同步的日期(格式:YYYY-MM-DD)", tbUtil.getCurrDate());
			if (tbUtil.isEmpty(uid)) {
				return;
			}
			fag=new StringBuffer();
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					try {
						for(int i=0;i<vo.length;i++){
							String count=ifc.impReadDataFromFile(vo[i].getStringValue("id"), uid);
							fag.append(count+"\n");
						}
					} catch (Exception e1) {
						fag.append("执行失败!");
						e1.printStackTrace();
					}
				}
			});
			MessageBox.show(this, fag.toString());
			parentpanel.refreshCurrSelectedRow();
		} catch (Exception e1) {
			MessageBox.show(this, e1);
		}
	}

	private void doAddParent() throws Exception {
		String sql = "select max(seq) from sal_data_interface_main ";
		String index = UIUtil.getStringValueByDS(null, sql);
		if (index == null || index.equals("")) {
			index = "0";
		}
		int seq = Integer.parseInt(index) + 1;
		sql = "select max(tablename) from sal_data_interface_main ";
		index = UIUtil.getStringValueByDS(null, sql);
		if (index == null || index.equals("")) {
			index = "sal_data_ifc_001";
		} else {
			String indexs = index.substring(index.lastIndexOf("_") + 1, index.length());
//			int i = Integer.parseInt(indexs);
//			if (i < 9) {
//				i = i + 1;
//				index = "sal_data_ifc_00" + i;
//			} else if (i < 99) {
//				i = i + 1;
//				index = "sal_data_ifc_0" + i;
//			} else {
//				i = i + 1;
//				index = "sal_data_ifc_" + i;
//			}
		}
		Pub_Templet_1VO tempcode = parentpanel.getTempletVO();
		BillCardPanel cardPanel = new BillCardPanel(tempcode);
		cardPanel.insertRow();
		cardPanel.setValueAt("seq", new StringItemVO(seq + ""));
		cardPanel.setValueAt("tablename", new StringItemVO(index));
		cardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		BillCardDialog cardDialog = new BillCardDialog(this, "新增", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setVisible(true);
		parentpanel.refreshData();
	}

	private void moveUp(BillListPanel listPanel) {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(listPanel);
			return;
		}
		if (listPanel == childrenpanel) {
			billVO = parentpanel.getSelectedBillVO();
			String lasttime = billVO.getStringValue("lasttime");
			if (lasttime != null && !lasttime.equals("")) {
				return;
			}
		}
		listPanel.moveUpRow();
		int count = listPanel.getRowCount();
		for (int i = 1; i <= count; i++) {
			listPanel.setValueAt(i, i - 1, "seq");
			if (listPanel == childrenpanel) {
				listPanel.setValueAt(salaryTBUtil.convertIntColToEn(i,false), i - 1, "itemcode");
			}
		}
		listPanel.saveData();
	}

	private void moveDown(BillListPanel listPanel) {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(listPanel);
			return;
		}
		if (listPanel == childrenpanel) {
			billVO = parentpanel.getSelectedBillVO();
			String lasttime = billVO.getStringValue("lasttime");
			if (lasttime != null && !lasttime.equals("")) {
				return;
			}
		}
		listPanel.moveDownRow();
		int count = listPanel.getRowCount();
		for (int i = 1; i <= count; i++) {
			listPanel.setValueAt(i, i - 1, "seq");
			if (listPanel == childrenpanel) {
				listPanel.setValueAt(salaryTBUtil.convertIntColToEn(i,false), i - 1, "itemcode");
			}
		}
		listPanel.saveData();
	}

	public void onBillListSelectChanged(BillListSelectionEvent e) {
		BillVO billVO = e.getCurrSelectedVO();
		if (billVO == null || billVO.getStringValue("id") == null) {
			childrenpanel.clearTable();
		} else {
			childrenpanel.QueryDataByCondition("parentid = '" + billVO.getStringValue("id") + "'");
		}
	}

	private void doAddItem() throws Exception {
		BillVO billVO = parentpanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(childrenpanel);
			return;
		}
		String sql = "select max(seq) from sal_data_interface_item_conf where parentid ='" + billVO.getStringValue("id") + "'";
		String index = UIUtil.getStringValueByDS(null, sql);
		if (index == null || index.equals("")) {
			index = "0";
		}
		int seq = Integer.parseInt(index) + 1;
		String code = salaryTBUtil.convertIntColToEn(seq,false);
		String id = billVO.getStringValue("id");
		Pub_Templet_1VO tempcode = childrenpanel.getTempletVO();
		BillCardPanel cardPanel = new BillCardPanel(tempcode);
		cardPanel.insertRow();
		cardPanel.setValueAt("parentid", new StringItemVO(id));
		cardPanel.setValueAt("seq", new StringItemVO(seq + ""));
		cardPanel.setValueAt("itemcode", new StringItemVO(code));
		cardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		BillCardDialog cardDialog = new BillCardDialog(this, "新增", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setVisible(true);
		childrenpanel.refreshData();
	}

	private void doDeleteAll() {
		BillVO billVO = parentpanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(parentpanel);
			return;
		}
		List sqllist = new ArrayList();
		String state = billVO.getStringValue("lasttime");
		if (state != null && !state.equals("")) {
			MessageBox.showWarn(parentpanel, "该接口已同步，此操作将删除该接口已同步数据，操作不可逆");
			String sql1 = "drop table " + billVO.getStringValue("tablename");
			sqllist.add(sql1);
		}
		if (MessageBox.confirmDel(parentpanel)) {
			parentpanel.doDelete(true);
			String sql = "delete from sal_data_interface_item_conf where parentid ='" + billVO.getStringValue("id") + "'";
			sqllist.add(sql);
			try {
				UIUtil.executeBatchByDS(null, sqllist);
				//待插入日志记录
				childrenpanel.refreshData();
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void onAction() {
		BillVO vo = parentpanel.getSelectedBillVO();
		BillVO billVOs[] = childrenpanel.getAllBillVOs();
		if (vo == null || billVOs == null) {
			return;
		}
		billVOs[0].convertToHashVO();
		List<HashVO> list = new ArrayList<HashVO>();
		HashVO hashVO = vo.convertToHashVO();
		for (int i = 0; i < billVOs.length; i++) {
			list.add(billVOs[i].convertToHashVO());
		}
		HashVO hashVO2[] = list.toArray(new HashVO[0]);
		SalaryServiceIfc ifc;
		try {
			ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			try {
				String[][] DBdate = ifc.compareDBTOConfig(hashVO, hashVO2);
				if (DBdate != null && DBdate.length > 0) {
					String actiontime = tbUtil.getCurrTime();
					StringBuilder sqlinfo = new StringBuilder();
					StringBuilder createDB = new StringBuilder("");
					StringBuilder modifyDB = new StringBuilder("");
					List<String> sqllist = new ArrayList<String>();
					String sql = "update sal_data_interface_main set lasttime = '" + actiontime + "' where id ='" + vo.getStringValue("id") + "'";
					sqllist.add(sql);
					for (int i = 0; i < DBdate.length; i++) {
						if (DBdate[i][4].equals("新增列")) {
							createDB.append("新增字段：" + DBdate[i][0] + " 字段大小：" + DBdate[i][5] + "\r\n");
						} else if (DBdate[i][4].equals("修改列")) {
							modifyDB.append("修改字段：" + DBdate[i][0] + " 大小从" + DBdate[i][6] + "→" + DBdate[i][5] + "\r\n");
						}
						sqllist.add(DBdate[i][3]);
					}
					sqlinfo.append("您确定执行如下操作吗？\r\n");
					sqlinfo.append(createDB);
					sqlinfo.append(modifyDB);
					if (MessageBox.confirm(this, sqlinfo)) {
						UIUtil.executeBatchByDS(null, sqllist);
						MessageBox.show(this,"创建成功");
					}
				}
			} catch (Exception e) {
				ifc.createTableByConfig(vo.getStringValue("id"));
				MessageBox.show(this,"创建成功");
			}

		} catch (WLTRemoteException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		parentpanel.refreshCurrSelectedRow();
	}

	public void onSynchronous() {
		final BillVO vo = parentpanel.getSelectedBillVO();
		if (vo == null) {
			return;
		}
		final SalaryServiceIfc ifc;
		try {
			ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			final String uid = JOptionPane.showInputDialog(parentpanel, "请输入要手工同步的日期(格式:YYYY-MM-DD)", tbUtil.getCurrDate());
			if (tbUtil.isEmpty(uid)) {
				return;
			}
			fag=new StringBuffer();
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					try {
						ifc.readDataFromFile(vo.getStringValue("id"), uid);
						fag.append("执行成功!");
					} catch (Exception e1) {
						fag.append("执行失败!");
						e1.printStackTrace();
					}
				}
			});
			MessageBox.show(this, fag);
			parentpanel.refreshCurrSelectedRow();
		} catch (Exception e1) {
			MessageBox.show(this, e1);
		}
	}

	private void showHistory(String _selectdate) throws Exception {
		BillVO billVO = parentpanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String lasttime = billVO.getStringValue("lasttime");
		if (lasttime == null || lasttime.equals("")) {
			MessageBox.show(parentpanel, "该接口还没有进行同步");
			return;
		}
		String tablename = billVO.getStringValue("tablename");
		String parentid = billVO.getStringValue("id");
		iname = billVO.getStringValue("iname");
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sdf = null;
		if (tbUtil.isEmpty(_selectdate)) {
			centerpanel = new JPanel(new BorderLayout());
			String pattern = "yyyy-MM-dd";
			sdf = new SimpleDateFormat(pattern);
			Date date = sdf.parse(lasttime);
			c.setTime(date);
		} else {
			String pattern = "yyyy-MM-dd";
			sdf = new SimpleDateFormat(pattern);
			Date date = sdf.parse(_selectdate);
			c.setTime(date);
		}
		String exectimes = null;
		String exectime = null;
		tabbedPane = new JTabbedPane();
		timemap = new HashMap();
		hasclickTabMap = new HashMap();
		for (int i = 0; i < 7; i++) {
			if (i == 0) {
				c.add(Calendar.DAY_OF_YEAR, 0);
				Date today_plus1 = c.getTime();
				String indextime = sdf.format(today_plus1);
				exectimes = indextime;
				exectime = exectimes;
				currtime = exectime;
			} else {
				c.add(Calendar.DAY_OF_YEAR, -1);
				Date today_plus1 = c.getTime();
				String indextime = sdf.format(today_plus1);
				exectimes = indextime;
				exectime = exectimes;
			}
			timemap.put(i, exectime);
			tabbedPane.addTab(exectime, getbilllistpanel(tablename, parentid));
		}
		tabbedPane.setSelectedIndex(0);
		tabbedPane.addChangeListener(this);
		billListPanel = ((BillListPanel) tabbedPane.getSelectedComponent());
		billListPanel.QueryDataByCondition("logid in (select id from sal_data_interface_log where exectype ='同步数据' and state ='成功' and iname ='" + iname + "' and datadate like '%" + currtime + "%' )");
		billListPanel.setCanShowCardInfo(false);
		billListPanel.getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				billListPanel.QueryDataByCondition("logid in (select id from sal_data_interface_log where exectype ='同步数据' and state ='成功' and iname ='" + iname + "' and datadate like '%" + currtime + "%' )");
			}
		});
		if (_selectdate != null && billDialog != null) {
			centerpanel.removeAll();
			centerpanel.setLayout(new BorderLayout());
			centerpanel.add(tabbedPane, BorderLayout.CENTER);
			centerpanel.updateUI();
			return;
		}
		billDialog = new BillDialog(parentpanel);
		billDialog.setLayout(new BorderLayout());
		centerpanel.add(tabbedPane);
		billDialog.getContentPane().add(centerpanel, BorderLayout.CENTER);
		billDialog.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
		billDialog.setSize(800, 700);
		billDialog.locationToCenterPosition();
		billDialog.setVisible(true);
	}

	public void stateChanged(ChangeEvent e) {
		int index = tabbedPane.getSelectedIndex();
		final String exc = (String) timemap.get(index);
		final BillListPanel listPanel = (BillListPanel) tabbedPane.getSelectedComponent();
		listPanel.QueryDataByCondition("logid in (select id from sal_data_interface_log where exectype ='同步数据' and state ='成功' and iname ='" + iname + "' and datadate like '%" + exc + "%' )");
		listPanel.setCanShowCardInfo(false);
		queryPanel = listPanel.getQuickQueryPanel();
		queryPanel.addBillQuickActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listPanel.QueryDataByCondition("logid in (select id from sal_data_interface_log where exectype ='同步数据' and state ='成功' and iname ='" + iname + "' and datadate like '%" + exc + "%' )");

			}
		});
		hasclickTabMap.put(index, listPanel);

	}

	WLTButton jump = null;
	private String oldselectdate = null;

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		jump = new WLTButton("跳转到");
		jump.addActionListener(this);
		btn_cancel = new WLTButton("关闭");
		btn_cancel.addActionListener(this); //
		panel.add(jump); //
		panel.add(btn_cancel); //
		return panel;
	}

	private void onCancel() {
		billDialog.setCloseType(billDialog.CANCEL);
		billDialog.dispose(); //
	}

	private BillListPanel getbilllistpanel(String tablename, String parentid) throws Exception {
		DateCreateDmo tmo = new DateCreateDmo(tablename, parentid, "sal_data_interface_item_conf", true);
		BillListPanel billListPanel = new BillListPanel(tmo);
		return billListPanel;
	}
}
