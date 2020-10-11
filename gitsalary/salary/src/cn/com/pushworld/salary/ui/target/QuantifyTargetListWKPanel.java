package cn.com.pushworld.salary.ui.target;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.*;
import cn.com.infostrategy.ui.mdata.*;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.paymanage.RefDialog_Month;
import jxl.write.Boolean;

/***
 * ���Ŷ���ָ��
 * ���������˲��ŵĶ���ָ��
 * ���ɿ��˱��ʱ����Ҫ���ɵ��ǲ���Ҫ���
 * ��Ҫ���ݵ�����������Զ����ɵ÷�
 */
public class QuantifyTargetListWKPanel extends AbstractWorkPanel implements ActionListener, BillTreeSelectListener, ChangeListener {

	private BillTreePanel treePanel; //ָ��������
	private BillListPanel bp_targetlist, listPanel; //ָ���б�
	private WLTButton btn_add0, btn_edit0, btn_delete0, btn_add, btn_edit, btn_delete, btn_action; //�б��ϵ����а�ť
	private WLTButton btn_tree_add, btn_tree_edit, btn_tree_del; //�������ϵİ�ť
	private WLTTabbedPane maintab = null;
	private TBUtil tbUtil;
	private boolean wgflg= false;
	public void initialize() {
		tbUtil = new TBUtil();
		wgflg = tbUtil.getSysOptionBooleanValue("���ż����Ƿ��б�չʾ",false);//zzl[2020-5-14] �Ƿ������ż������б�չʾ
		bp_targetlist = new BillListPanel("SAL_TARGET_LIST_CODE_QUANTIFY");
		bp_targetlist.setDataFilterCustCondition("type='���Ŷ���ָ��'");
		initBtn();
		bp_targetlist.QueryDataByCondition(null);
		maintab = new WLTTabbedPane();
		maintab.addTab("ȫ����ʾ", bp_targetlist);
		JPanel blankPanel = new JPanel(new BorderLayout());
		maintab.addTab("��ָ�������ʾ", blankPanel);
		maintab.addChangeListener(this);
		this.add(maintab);
	}

	public void initBtn() {
		btn_add0 = new WLTButton("����");
		btn_edit0 = new WLTButton("�޸�");
		btn_delete0 = new WLTButton("ɾ��");
		btn_action = new WLTButton("����", UIUtil.getImage("bug_2.png"));
		btn_add0.addActionListener(this);
		btn_edit0.addActionListener(this);
		btn_delete0.addActionListener(this);
		btn_action.addActionListener(this);
		//2020��7��12��12:40:13 fj  ���ǹ���ԱȨ�޿�������������ť
		if(!ClientEnvironment.isAdmin()){
			btn_add0.setVisible(false);
			btn_edit0.setVisible(false);
			btn_delete0.setVisible(false);
		}
		bp_targetlist.addBatchBillListButton(new WLTButton[] { btn_add0, btn_edit0, btn_delete0, btn_action });
		bp_targetlist.repaintBillListButton();
	}

	public WLTSplitPane getWLTSplitPane() {
		treePanel = new BillTreePanel("SAL_TARGET_CATALOG_CODE1");
		treePanel.queryDataByCondition(null);
		treePanel.addBillTreeSelectListener(this);
		btn_tree_add = WLTButton.createButtonByType(WLTButton.TREE_INSERT);
		btn_tree_edit = WLTButton.createButtonByType(WLTButton.TREE_EDIT);
		btn_tree_del = new WLTButton("ɾ��");
		btn_tree_del.addActionListener(this);
		//2020��7��12��12:40:13 fj  ���ǹ���ԱȨ�޿�������������ť
		if(!ClientEnvironment.isAdmin()){
			btn_tree_add.setVisible(false);
			btn_tree_edit.setVisible(false);
			btn_tree_del.setVisible(false);
		}
		treePanel.addBatchBillTreeButton(new WLTButton[] { btn_tree_add, btn_tree_edit, btn_tree_del });
		treePanel.repaintBillTreeButton();
		listPanel = new BillListPanel("SAL_TARGET_LIST_CODE_QUANTIFY");
		listPanel.setDataFilterCustCondition("type='���Ŷ���ָ��'");
		listPanel.setBillQueryPanelVisible(false);
		btn_add = new WLTButton("����");
		btn_edit = new WLTButton("�޸�");
		btn_delete = new WLTButton("ɾ��");
		btn_add.addActionListener(this);
		btn_edit.addActionListener(this);
		btn_delete.addActionListener(this);
		if(!ClientEnvironment.isAdmin()){
			btn_add.setVisible(false);
			btn_edit.setVisible(false);
			btn_delete.setVisible(false);
		}
		listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete });
		listPanel.repaintBillListButton();
		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, treePanel, listPanel);
		splitPane.setDividerLocation(220);
		return splitPane;
	}

	//������������÷����µ�ָ��, ��������Ҷ�ӽڵ�����
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (_event.getCurrSelectedNode().isRoot()) {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					listPanel.QueryDataByCondition(null);
				}
			});
		} else if (_event.getCurrSelectedVO() == null) {
			listPanel.clearTable();
		} else {
			if (_event.getCurrSelectedNode().isLeaf()) {
				BillVO billVO = _event.getCurrSelectedVO();
				final String catalogID = billVO.getStringValue("id");
				new SplashWindow(this, new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						listPanel.QueryDataByCondition("catalogid=" + catalogID);
					}
				});
			} else {
				final DefaultMutableTreeNode node = _event.getCurrSelectedNode();
				new SplashWindow(this, new AbstractAction() {
					public void actionPerformed(ActionEvent arg0) {
						ArrayList<String> ids = new ArrayList<String>();
						getAllChildIDs(node, ids);
						listPanel.QueryDataByCondition("catalogid in (" + TBUtil.getTBUtil().getInCondition(ids) + ")");
					}
				});
			}
		}
	}

	//�ݹ�ó�����Ҷ�ӽڵ��ID
	private void getAllChildIDs(DefaultMutableTreeNode node, ArrayList<String> ids) {
		Enumeration<DefaultMutableTreeNode> children = node.children();
		BillVO nodeVO = null;
		while (children.hasMoreElements()) {
			DefaultMutableTreeNode child = children.nextElement();
			if (child.isLeaf()) { // �Ƿ�Ҷ�ӽڵ�
				nodeVO = treePanel.getBillVOFromNode(child);
				ids.add(nodeVO.getStringValue("id"));
			} else {
				// ��Ҷ�ӽڵ�������ݹ������
				getAllChildIDs(child, ids);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_add) {
			onAdd(listPanel, 1);
		} else if (e.getSource() == btn_edit) {
			onEdit(listPanel);
		} else if (e.getSource() == btn_delete) {
			onDelete(listPanel);
		} else if (e.getSource() == btn_edit0) {
			onEdit(bp_targetlist);
		} else if (e.getSource() == btn_delete0) {
			onDelete(bp_targetlist);
		} else if (e.getSource() == btn_add0) {
			onAdd(bp_targetlist, 0);
		} else if (e.getSource() == btn_tree_del) {
			onTreeDelete();
		} else {
			if(wgflg){
				deptListZs();
			}else{
				onAction();
			}
		}
	}

	/**
	 * ����
	 */
	private void onAdd(BillListPanel listPanel, int tab) {
		BillCardPanel cardPanel = null;
		if (tab == 1) {
			BillVO billVO = treePanel.getSelectedVO();
			if (billVO == null) {
				MessageBox.show(this, "����ѡ��һ��ָ�����.");
				return;
			}
			if (!treePanel.getSelectedNode().isLeaf()) {
				MessageBox.show(this, "��ѡ����ĩ���ķ���.");
				return;
			}
			//����һ����Ƭ���
			cardPanel = new BillCardPanel(listPanel.getTempletVO());
			cardPanel.insertRow();
			cardPanel.setEditableByInsertInit();
			//����Ĭ��ֵ
			cardPanel.setValueAt("catalogid", new RefItemVO(billVO.getStringValue("id"), "", billVO.getStringValue("name")));
		} else {
			//����һ����Ƭ���
			cardPanel = new BillCardPanel(listPanel.getTempletVO());
			cardPanel.insertRow();
			cardPanel.setEditableByInsertInit();
		}
		cardPanel.setRealValueAt("type", "���Ŷ���ָ��");

		//������
		BillCardDialog cardDialog = new BillCardDialog(listPanel, "�������Ŷ���ָ��", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setVisible(true);

		//ȷ������
		if (cardDialog.getCloseType() == 1) {
			int li_newrow = listPanel.newRow(false); //
			listPanel.setBillVOAt(li_newrow, cardDialog.getBillVO(), false);
			listPanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //�����б���е�����Ϊ��ʼ��״̬.
			listPanel.setSelectedRow(li_newrow); //		
			listPanel.refreshCurrSelectedRow();
		}
	}

	/**
	 * �༭
	 */
	private void onEdit(BillListPanel listPanel) {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(listPanel);
			return;
		}
		//����һ����Ƭ���
		BillCardPanel cardPanel = new BillCardPanel(listPanel.getTempletVO());
		cardPanel.setBillVO(billVO);

		//������
		BillCardDialog dialog = new BillCardDialog(this, "�޸Ĳ��Ŷ���ָ��", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true);
		//ȷ������
		if (dialog.getCloseType() == 1) {
			if (listPanel.getSelectedRow() == -1) {
			} else {
				listPanel.setBillVOAt(listPanel.getSelectedRow(), dialog.getBillVO());
				listPanel.setRowStatusAs(listPanel.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
				listPanel.refreshCurrSelectedRow();
			}
		}
	}

	/**
	 * ɾ��
	 */
	private void onDelete(BillListPanel listPanel) {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(listPanel);
			return;
		}
		listPanel.doDelete(false);
		try {
			UIUtil.executeBatchByDS(null, new String[] { "delete from sal_target_checkeddept where targetid=" + billVO.getPkValue() });
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	//ָ�������ɾ��, �������Լ��÷����µ�ָ��һ��ɾ��!
	private void onTreeDelete() {
		if (treePanel.getSelectedNode() == null || !treePanel.getSelectedNode().isLeaf()) {
			MessageBox.show(this, "��ѡ��һ��ĩ��������ɾ������!"); //
			return;
		}
		BillVO billVO = treePanel.getSelectedVO();
		try {
			String count = UIUtil.getStringValueByDS(null, "select count(id) from sal_target_list where catalogid=" + billVO.getStringValue("id"));
			//			if (!"0".equals(count)) {
			//				MessageBox.show(this, "�������Ѿ���ʹ��,����ɾ��!"); //
			//				return;
			//			}

			String catalogName = billVO.getStringValue("name");
			String catalogID = billVO.getStringValue("id");
			if (!MessageBox.confirm(this, "Ҫɾ���ķ��ࡾ" + catalogName + "���´��ڡ�" + count + "����ָ������, ����ؽ�������!\nȷ��Ҫ����ɾ����Щ������?")) {
				return;
			}

			ArrayList<String> sqlList = new ArrayList<String>();
			sqlList.add("delete from sal_target_catalog where  id=" + catalogID);
			sqlList.add("delete from sal_target_list where catalogid=" + catalogID);
			UIUtil.executeBatchByDS(null, sqlList);
			treePanel.delCurrNode(); //
			treePanel.updateUI();
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	public void stateChanged(ChangeEvent e) {
		if (maintab.getSelectedIndex() == 1 && listPanel == null) {
			onClickSecTab();
		}
	}

	private void onClickSecTab() {
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				maintab.getComponentAt(1).add(getWLTSplitPane(), BorderLayout.CENTER);
			}
		});
	}

	/**
	 * �鿴��־���״̬�Ƿ�
	 * @return
	 */
	public boolean checkEditable() {
		return true;
	}

	private void onAction() {
		BillVO targetVO = bp_targetlist.getSelectedBillVO();
		if (targetVO == null) {
			MessageBox.showSelectOne(bp_targetlist);
			return;
		}
		String date = getDate(this);
		if (TBUtil.getTBUtil().isEmpty(date)) {
			return;
		}
		String msg = "";
		try {
			msg = getService().calcOneDeptTargetDL(targetVO.convertToHashVO(), date);
			MessageBox.show(bp_targetlist, msg);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}

	}

	private SalaryServiceIfc services;

	private SalaryServiceIfc getService() {
		if (services == null) {
			try {
				services = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return services;
	}

	private String getDate(Container _parent) {
		String selectDate = "2013-08";
		try {
			RefDialog_Month chooseMonth = new RefDialog_Month(_parent, "��ѡ��Ҫ���˵��·�", null, null);
			chooseMonth.initialize();
			chooseMonth.setVisible(true);
			selectDate = chooseMonth.getReturnRefItemVO().getName();
			return selectDate;
		} catch (Exception e) {
			WLTLogger.getLogger(QuantifyTargetListWKPanel.class).error(e);
		}
		return "2013-08";
	}

	/**
	 * zzl[2020-5-14]
	 * ����ָ�������ԭ�����ı�չʾ���������б�չʾ
	 */
	public void deptListZs(){
		BillVO targetVO = bp_targetlist.getSelectedBillVO();
		String date = getDate(this);
		if (TBUtil.getTBUtil().isEmpty(date)) {
			return;
		}
		Pub_Templet_1VO templetVO = new Pub_Templet_1VO();
		templetVO.setTempletname("����ָ�����");
		String [] columns = new String[]{"targetname","checkeddeptname","rtobj","value","process"};
		String [] columnNames=new String[]{"ָ������","�����˲���","ʵ�����ֵ","�÷�","�������"};
		templetVO.setRealViewColumns(columns);
		templetVO.setIsshowlistpagebar(false);
		templetVO.setIsshowlistopebar(false);
		templetVO.setListheaderisgroup(false);
		templetVO.setIslistpagebarwrap(false);
		templetVO.setIsshowlistquickquery(false);
		templetVO.setIscollapsequickquery(true);
		templetVO.setIslistautorowheight(true);
		Pub_Templet_1_ItemVO[] templetItemVOs = new Pub_Templet_1_ItemVO[5];
		for(int i=0;i<columns.length;i++){
			templetItemVOs[i]=new Pub_Templet_1_ItemVO();
			templetItemVOs[i].setListisshowable(true);
			templetItemVOs[i].setPub_Templet_1VO(templetVO);
			templetItemVOs[i].setListwidth(150);
			templetItemVOs[i].setItemtype("�ı���");
			templetItemVOs[i].setListiseditable("4");
			templetItemVOs[i].setItemkey(columns[i].toString());
			templetItemVOs[i].setItemname(columnNames[i].toString());
		}
		templetVO.setItemVos(templetItemVOs);
		BillListPanel list = new BillListPanel(templetVO);
		try{
			HashVO [] vos =getService().calcOneDeptTargetDL2(targetVO.convertToHashVO(), date);
			list.putValue(vos);
		}catch (Exception e){
			e.printStackTrace();
		}
		BillListDialog dialog=new BillListDialog(bp_targetlist,"����ָ�����",list,1000,800,true);
		dialog.getBtn_confirm().setVisible(false);
		dialog.setVisible(true);
	}
}
