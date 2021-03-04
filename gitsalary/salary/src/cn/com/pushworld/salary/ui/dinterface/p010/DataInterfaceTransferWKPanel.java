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

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Date;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;

public class DataInterfaceTransferWKPanel extends AbstractWorkPanel implements BillListSelectListener, ChangeListener, ActionListener {
	private BillListPanel parentpanel;
	private BillQueryPanel billQueryPanel, queryPanel;
	private BillListPanel childrenpanel;
	private WLTButton add_btn, edit_btn, del_btn, view_btn, history_btn, up_btn, down_btn, btn_cancel, btn_convert;
	private WLTButton add_btn1, edit_btn1, del_btn1, view_btn1, up_btn1, down_btn1;
	private WLTSplitPane splitPane;
	private SalaryTBUtil salaryTBUtil = new SalaryTBUtil();
	private TBUtil tbUtil = new TBUtil();
	private BillDialog billDialog = null;
	private JPanel centerpanel = null;
	private JTabbedPane tabbedPane = null;
	private HashMap timemap = null;
	private String iname = null;
	private HashMap hasclickTabMap;
	private BillListPanel billListPanel;
	private String currtime = null;

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		parentpanel = new BillListPanel("SAL_CONVERT_IFCDATA_CODE1");
		add_btn = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		add_btn.addActionListener(this);
		edit_btn = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		del_btn = new WLTButton("ɾ��");
		del_btn.addActionListener(this);
		view_btn = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		up_btn = new WLTButton(UIUtil.getImage("up1.gif"));
		down_btn = new WLTButton(UIUtil.getImage("down1.gif")); //
		up_btn.setPreferredSize(new Dimension(20, 20)); //
		down_btn.setPreferredSize(new Dimension(20, 20)); //
		up_btn.setToolTipText("����˳��"); //
		down_btn.setToolTipText("����˳��"); //
		up_btn.addActionListener(this); //
		down_btn.addActionListener(this); //
		history_btn = new WLTButton("�鿴��ʷ����");
		history_btn.addActionListener(this);
		btn_convert = new WLTButton("ת��");
		btn_convert.addActionListener(this);
		parentpanel.addBatchBillListButton(new WLTButton[] { add_btn, edit_btn, del_btn, view_btn, btn_convert, history_btn, up_btn, down_btn });
		parentpanel.repaintBillListButton();
		parentpanel.addBillListSelectListener(this);
		billQueryPanel = parentpanel.getQuickQueryPanel();
		billQueryPanel.addBillQuickActionListener(this);
		childrenpanel = new BillListPanel("SAL_CONVERT_IFCDATA_ITEM_CONF_CODE1");
		add_btn1 = new WLTButton("����");
		add_btn1.addActionListener(this);
		edit_btn1 = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		del_btn1 = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		view_btn1 = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		up_btn1 = new WLTButton(UIUtil.getImage("up1.gif"));
		down_btn1 = new WLTButton(UIUtil.getImage("down1.gif")); //
		up_btn1.setPreferredSize(new Dimension(20, 20)); //
		down_btn1.setPreferredSize(new Dimension(20, 20)); //
		up_btn1.setToolTipText("����˳��"); //
		down_btn1.setToolTipText("����˳��"); //
		up_btn1.addActionListener(this); //
		down_btn1.addActionListener(this); //
		childrenpanel.addBatchBillListButton(new WLTButton[] { add_btn1, edit_btn1, del_btn1, view_btn1, up_btn1, down_btn1 });
		childrenpanel.repaintBillListButton();

		splitPane = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, parentpanel, childrenpanel);
		splitPane.setDividerLocation(280);
		this.add(splitPane);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
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
		} else if (e.getSource() == history_btn) {
			try {
				showHistory(null);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		} else if (e.getSource() == btn_convert) {
			onConvert();
		} else if (e.getSource() == jump) {
			try {
				RefDialog_Date date = new RefDialog_Date(tabbedPane, "��ѡ��һ������", new RefItemVO(oldselectdate, oldselectdate, oldselectdate), null);
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
		}
	}

	private void doAddParent() throws Exception {
		String sql = "select max(seq) from sal_convert_ifcdata ";
		String index = UIUtil.getStringValueByDS(null, sql);
		if (index == null || index.equals("")) {
			index = "0";
		}
		int seq = Integer.parseInt(index) + 1;
		if (index == null || index.equals("")) {
			index = "sal_reportstore_001";
		} else {
			String indexs = index.substring(index.lastIndexOf("_") + 1, index.length());
			int i = Integer.parseInt(indexs);
			if (i < 9) {
				i = i + 1;
				index = "sal_reportstore_00" + i;
			} else if (i < 99) {
				i = i + 1;
				index = "sal_reportstore_0" + i;
			} else {
				i = i + 1;
				index = "sal_reportstore_" + i;
			}
		}
		Pub_Templet_1VO tempcode = parentpanel.getTempletVO();
		BillCardPanel cardPanel = new BillCardPanel(tempcode);
		cardPanel.insertRow();
		cardPanel.setValueAt("tablename", new StringItemVO(index));
		cardPanel.setValueAt("seq", new StringItemVO(seq + ""));
		cardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		BillCardDialog cardDialog = new BillCardDialog(this, "����", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setVisible(true);
		parentpanel.refreshData();
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
		String sql = "select max(seq) from sal_convert_ifcdata_item_conf where parentid ='" + billVO.getStringValue("id") + "'";
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
		BillCardDialog cardDialog = new BillCardDialog(this, "����", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setVisible(true);
		childrenpanel.refreshData();
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

	private void doDeleteAll() {
		BillVO billVO = parentpanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(parentpanel);
			return;
		}
		List sqllist = new ArrayList();
		String state = billVO.getStringValue("lasttime");
		if (state != null && !state.equals("")) {
			MessageBox.showWarn(parentpanel, "�ýӿ���ͬ�����˲�����ɾ���ýӿ���ͬ�����ݣ�����������");
			String sql1 = "drop table " + billVO.getStringValue("tablename");
			sqllist.add(sql1);
		}
		if (MessageBox.confirmDel(parentpanel)) {
			parentpanel.doDelete(true);
			String sql = "delete from sal_convert_ifcdata_item_conf where parentid ='" + billVO.getStringValue("id") + "'";
			sqllist.add(sql);
			try {
				UIUtil.executeBatchByDS(null, sqllist);
				//��������־��¼
				childrenpanel.refreshData();
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
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
			MessageBox.show(parentpanel, "�ýӿ����ݻ�û�н���ͬ��");
			return;
		}
		String tablename = billVO.getStringValue("tablename");
		String parentid = billVO.getStringValue("id");
		iname = billVO.getStringValue("sourceifc");
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
		billListPanel.QueryDataByCondition("logid in (select id from sal_convert_ifcdata_log where exectype ='����ת��' and state ='�ɹ�' and reportname =(select iname from sal_data_interface_main where id ='" + iname + "') and datadate like '%" + currtime + "%' )");
		billListPanel.setCanShowCardInfo(false);
		billListPanel.getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				billListPanel.QueryDataByCondition("logid in (select id from sal_convert_ifcdata_log where exectype ='����ת��' and state ='�ɹ�' and reportname =(select iname from sal_data_interface_main where id ='" + iname + "') and datadate like '%" + currtime + "%' )");
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
		listPanel.QueryDataByCondition("logid in (select id from sal_convert_ifcdata_log where exectype ='����ת��' and state ='�ɹ�' and reportname =(select iname from sal_data_interface_main where id ='" + iname + "') and  datadate like '%" + exc + "%' )");
		listPanel.setCanShowCardInfo(false);
		queryPanel = listPanel.getQuickQueryPanel();
		queryPanel.addBillQuickActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listPanel.QueryDataByCondition("logid in (select id from sal_convert_ifcdata_log where exectype ='����ת��' and state ='�ɹ�' and reportname =(select iname from sal_data_interface_main where id ='" + iname + "') and  datadate like '%" + exc + "%' )");
			}
		});
		hasclickTabMap.put(index, listPanel);
	}

	WLTButton jump = null;
	private String oldselectdate = null;

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		jump = new WLTButton("��ת��");
		jump.addActionListener(this);
		btn_cancel = new WLTButton("�ر�");
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
		DateCreateDmo tmo = new DateCreateDmo(tablename, parentid, "sal_convert_ifcdata_item_conf", true);
		BillListPanel billListPanel = new BillListPanel(tmo);
		return billListPanel;
	}

	private void onConvert() {
		BillVO vo = parentpanel.getSelectedBillVO();
		if (vo == null) {
			return;
		}
		SalaryServiceIfc ifc;
		try {
			ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			String uid = JOptionPane.showInputDialog(parentpanel, "������Ҫ�ֹ�ͬ��������(��ʽ:YYYY-MM-DD)", tbUtil.getCurrDate());
			if (tbUtil.isEmpty(uid)) {
				return;
			}
			ifc.convertIFCDataToReportByHand(vo.getStringValue("id"), uid);
			MessageBox.show(this, "ִ�гɹ�!");
			parentpanel.refreshCurrSelectedRow();
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}
}