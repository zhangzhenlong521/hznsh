package com.pushworld.ipushgrc.ui.wfrisk.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

import com.pushworld.ipushgrc.ui.law.LawShowHtmlDialog;

public class ImportLawDialog extends BillDialog implements BillTreeSelectListener, ActionListener, BillListHtmlHrefListener, BillListMouseDoubleClickedListener {
	private String cmpfileid;
	private String cmpfilename;
	private String processid;
	private String processcode;
	private String processname;
	private String activityid;
	private String activitycode;
	private String activityname;
	private String lawid;
	private String lawname;
	private String wftype;
	private boolean editable;
	private WLTButton btn_importlaw, btn_deletelaw, btn_linkAll, btn_linkItem, btn_importlawitem, btn_showall, btn_confirm, btn_cancel, btn_close;
	private BillTreePanel treePanel = null; //�������
	private BillCardPanel cardPanel = null;
	private BillListPanel billlist_law;
	private BillListDialog dialog_law;
	private BillDialog dialog_lawitem;
	private HashMap tempMap = new HashMap();
	private HashMap addtempMap = new HashMap();
	private ArrayList insertsqls = new ArrayList();
	private WLTButton btn_show = null;
	private boolean canClose = false;
	private String loginuserid = ClientEnvironment.getInstance().getLoginUserID(); //��¼�û���id

	public ImportLawDialog(Container _parent, String _title, String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, boolean _editable) {
		this(_parent, _title, _cmpfileid, _cmpfilename, _processid, _processcode, _processname, null, null, null, WFGraphEditItemPanel.TYPE_WF, _editable);
	}

	public ImportLawDialog(Container _parent, String _title, String _cmpfileid, String _cmpfilename, String _processid, String _processcode, String _processname, String _activityid, String _activitycode, String _activityname, String _wftype, boolean _editable) {
		super(_parent, _title, 900, 650);
		this.getContentPane().setLayout(new BorderLayout()); //
		this.cmpfileid = _cmpfileid;
		this.cmpfilename = _cmpfilename;
		this.processid = _processid;
		this.processcode = _processcode;
		this.processname = _processname;
		this.activityid = _activityid;
		this.activitycode = _activitycode;
		this.activityname = _activityname;
		this.wftype = _wftype;
		this.editable = _editable;
		billlist_law = new BillListPanel("CMP_CMPFILE_LAW_CODE1");
		billlist_law.addBillListHtmlHrefListener(this);
		billlist_law.addBillListMouseDoubleClickedListener(this);//��ʵ�����̲ο�ʾ������ʱ���Ὣʾ�����̴���Frame�ڶ�����ʾ����ʱ���˫���б�ᵯ��Frame�Ͳص�����ȥ�ˣ��������Զ���˫���¼�������dialog�����/2012-07-19��
		if (cmpfileid == null) {
			billlist_law.getTempletItemVO("cmpfile_name").setCardisshowable(false);//������������ļ���Ĺ���������ʾ�����ļ����ơ����/2012-05-11��
			billlist_law.setItemVisible("cmpfile_name", false);//��Ϊ�����billlist_law �Ѿ���ʼ�����ˣ�����setListisshowable(false) �ǲ������õģ�ֻ���������á�
		}
		if (WFGraphEditItemPanel.TYPE_WF.equals(this.wftype)) {
			billlist_law.getTempletItemVO("wfactivity_name").setCardisshowable(false);
			billlist_law.setDataFilterCustCondition("relationtype='" + WFGraphEditItemPanel.TYPE_WF + "' and wfprocess_id=" + this.processid);
			billlist_law.QueryDataByCondition(null);
		} else {
			billlist_law.setDataFilterCustCondition("wfactivity_id=" + this.activityid);
			billlist_law.QueryDataByCondition(null);
		}
		if (editable) {
			if (billlist_law.getRowCount() > 0) {
				BillVO[] billvos = billlist_law.getAllBillVOs();
				for (int i = 0; i < billvos.length; i++) {
					tempMap.put(billvos[i].getStringValue("law_id") + "-" + billvos[i].getStringValue("lawitem_id"), billvos[i]);
				}
			}
			btn_importlaw = new WLTButton("��������");
			btn_importlaw.addActionListener(this);
			btn_deletelaw = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
			btn_deletelaw.addActionListener(this);
			billlist_law.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			billlist_law.addBatchBillListButton(new WLTButton[] { btn_importlaw, btn_deletelaw });
		}
		btn_show = new WLTButton("���"); //
		btn_show.addActionListener(this);
		billlist_law.addBillListButton(btn_show);
		billlist_law.repaintBillListButton();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(billlist_law, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}

	/**
	 * �ж��Ƿ���ʾ�����ڣ����û�������Ϣ������ʾ�Ƿ����������ѡ���ǡ����򵯳��������棬���ѡ�񡾷�����ʾ�����ڡ������/2012-05-28��
	 * @return
	 */
	public boolean isShowDialog() {
		if (this.editable && billlist_law.getRowCount() == 0) {
			if (MessageBox.confirm(this, "��" + this.wftype + "û����ط���,�Ƿ�����?")) {//����ɱ༭�����б���û�м�¼������ʾ�Ƿ�����������/2012-03-13��
				onImportLaw();
			} else {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_importlaw) {
			onImportLaw();
		} else if (e.getSource() == btn_deletelaw) {
			onDeletelaw();
		} else if (e.getSource() == btn_linkAll) {
			onLinkAll();
		} else if (e.getSource() == btn_importlawitem) { //
			onImportLawItem();
		} else if (e.getSource() == btn_showall) {
			onShow();
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		} else if (e.getSource() == btn_close) {
			onClose();
		} else if (e.getSource() == btn_show) {
			onBillListSelect();
		} else if (e.getSource() == btn_linkItem) {
			onLinkItem();
		}
	}

	private void onBillListSelect() {
		BillVO billVO = billlist_law.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(billlist_law.templetVO); //
		cardPanel.setBillVO(billVO); //
		String str_recordName = billVO.toString(); //
		BillCardDialog dialog = new BillCardDialog(billlist_law, billlist_law.templetVO.getTempletname() + "[" + str_recordName + "]", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT, true);
		dialog.getBtn_confirm().setVisible(false); //
		dialog.getBtn_save().setVisible(false); //
		cardPanel.setEditable(false); //
		dialog.setVisible(true); //
	}

	private void onClose() {
		this.dispose();
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_close = new WLTButton("�ر�");
		btn_close.addActionListener(this); //
		panel.add(btn_close); //
		return panel;
	}

	private void onImportLaw() {
		dialog_law = new BillListDialog(this, "��������", "LAW_LAW_CODE2", 700, 640);
		BillListPanel billlist_law = dialog_law.getBilllistPanel();
		billlist_law.addBillListHtmlHrefListener(this);
		billlist_law.setItemVisible("showdetail", false);
		btn_linkAll = dialog_law.getBtn_confirm();
		btn_linkAll.setText("����ȫ��");
		btn_linkAll.setToolTipText("����ȫ��");
		btn_linkAll.setPreferredSize(new Dimension(100, 23));
		btn_linkAll.setIcon(UIUtil.getImage("book_link.png"));
		btn_linkAll.addActionListener(this);

		//����Ϊ������Ŀ Gwang 2012-10-24
		btn_linkItem = dialog_law.getBtn_cancel();
		btn_linkItem.setText("������Ŀ");
		btn_linkItem.setToolTipText("������Ŀ");
		btn_linkItem.setPreferredSize(new Dimension(100, 23));
		btn_linkItem.setIcon(UIUtil.getImage("page_link.png"));
		btn_linkItem.addActionListener(this);

		WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //��Ƭ�����ť
		billlist_law.addBatchBillListButton(new WLTButton[] { btn_show });
		billlist_law.repaintBillListButton();
		//����ΪCheckbox Gwang 2012-10-24
		billlist_law.setRowNumberChecked(true);
		dialog_law.setVisible(true);
	}

	private void onDeletelaw() {
		BillVO billvo = billlist_law.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (MessageBox.showConfirmDialog(this, "��ȷ��Ҫɾ����?") != JOptionPane.YES_OPTION) {
			return; //
		}
		try {
			UIUtil.executeUpdateByDS(null, "delete from cmp_cmpfile_law where id=" + billvo.getStringValue("id"));
			billlist_law.removeRow(billlist_law.getSelectedRow()); //
			//MessageBox.show(this, "ɾ���ɹ�!");
			tempMap.remove(billvo.getStringValue("law_id") + "-" + billvo.getStringValue("lawitem_id"));
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private BillTreePanel getTreePanel() {
		treePanel = new BillTreePanel("LAW_LAW_ITEM_CODE1");
		treePanel.setMoveUpDownBtnVisiable(false);
		BillTreeNodeVO treenode = new BillTreeNodeVO(-1, this.lawname);
		treePanel.getRootNode().setUserObject(treenode);
		treePanel.queryDataByCondition("lawid=" + this.lawid);
		treePanel.addBillTreeSelectListener(this);
		return treePanel;
	}

	private BillCardPanel getCardPanel() {
		cardPanel = new BillCardPanel("LAW_LAW_ITEM_CODE1");
		btn_importlawitem = new WLTButton("����", "office_199.gif");
		btn_showall = new WLTButton("�鿴(" + tempMap.size() + ")", "office_062.gif");

		btn_importlawitem.addActionListener(this);
		btn_showall.addActionListener(this);

		cardPanel.addBatchBillCardButton(new WLTButton[] { btn_importlawitem, btn_showall });
		cardPanel.repaintBillCardButton();
		return cardPanel;
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		BillVO billvo = _event.getCurrSelectedVO();
		if (billvo != null) {
			cardPanel.queryDataByCondition("id=" + billvo.getStringValue("id"));
		} else {
			cardPanel.clear();
		}
	}

	private void onImportLawItem() {
		try {
			String lawitem_id = null;
			String lawitem_title = null;
			String lawitem_content;
			if (treePanel.getSelectedNode() == treePanel.getRootNode()) {
				if (tempMap.containsKey(this.lawid + "-null") || addtempMap.containsKey(this.lawid + "-null")) {
					MessageBox.show(this, "�÷����Ѽ��룬�����ظ�����!");
					return;
				}
				lawitem_title = "��ȫ�ġ�";
				lawitem_content = "��ȫ�ġ�";
			} else {
				BillVO billvo = treePanel.getSelectedVO();
				if (billvo == null) {
					MessageBox.show(this, "��ѡ��һ���ڵ���д˲���!");
					return;
				}
				lawitem_id = billvo.getStringValue("id");
				lawitem_title = billvo.getStringValue("itemtitle");
				lawitem_content = billvo.getStringValue("itemcontent");
				if (tempMap.containsKey(this.lawid + "-" + lawitem_id) || addtempMap.containsKey(this.lawid + "-" + lawitem_id)) {
					MessageBox.show(this, "�÷��������Ѽ��룬�����ظ�����!");
					return;
				}
			}
			BillVO billvo = null;
			if (billlist_law.getRowCount() > 0) {
				billvo = billlist_law.getBillVO(0);
			} else {
				int a = billlist_law.addEmptyRow();
				billvo = billlist_law.getBillVO(a);
				billlist_law.removeRow();
			}
			billvo.setObject("cmpfile_name", new StringItemVO(this.cmpfilename));
			billvo.setObject("wfprocess_code", new StringItemVO(this.processcode));
			billvo.setObject("wfprocess_name", new StringItemVO(this.processname));
			billvo.setObject("wfactivity_name", new StringItemVO(this.activityname));
			billvo.setObject("law_id", new StringItemVO(this.lawid));
			billvo.setObject("law_name", new StringItemVO(this.lawname));
			billvo.setObject("lawitem_title", new StringItemVO(lawitem_title));
			billvo.setObject("lawitem_content", new StringItemVO(lawitem_content));
			addtempMap.put(this.lawid + "-" + lawitem_id, billvo);

			InsertSQLBuilder isql_insert = new InsertSQLBuilder("cmp_cmpfile_law"); // 
			String str_id = UIUtil.getSequenceNextValByDS(null, "S_CMP_CMPFILE_LAW");//������ǰȡ��ID��sequenceֵ ����pub_sequence ��������¼��sename='ID'�����´������Ҫִ��sql��update pub_sequence set sename='S_CMP_CMPFILE_LAW' where sename='ID'�����/2012-07-17��
			isql_insert.putFieldValue("id", str_id);
			isql_insert.putFieldValue("cmpfile_id", this.cmpfileid);
			isql_insert.putFieldValue("cmpfile_name", this.cmpfilename);
			isql_insert.putFieldValue("wfprocess_id", this.processid);
			isql_insert.putFieldValue("wfprocess_code", this.processcode);
			isql_insert.putFieldValue("wfprocess_name", this.processname);
			isql_insert.putFieldValue("law_id", this.lawid);
			isql_insert.putFieldValue("law_name", this.lawname);
			isql_insert.putFieldValue("lawitem_id", lawitem_id);
			isql_insert.putFieldValue("lawitem_title", lawitem_title);
			isql_insert.putFieldValue("lawitem_content", lawitem_content);
			if (this.activityid == null) {
				isql_insert.putFieldValue("relationtype", WFGraphEditItemPanel.TYPE_WF);
			} else {
				isql_insert.putFieldValue("relationtype", WFGraphEditItemPanel.TYPE_ACTIVITY);
				isql_insert.putFieldValue("wfactivity_id", this.activityid);
				isql_insert.putFieldValue("wfactivity_code", this.activitycode);
				isql_insert.putFieldValue("wfactivity_name", this.activityname);
			}
			insertsqls.add(isql_insert.getSQL());
			btn_showall.setText("�鿴(" + (tempMap.size() + addtempMap.size()) + ")");
			btn_showall.setToolTipText("�鿴(" + (tempMap.size() + addtempMap.size()) + ")");
			//MessageBox.show(this, "����ɹ�!");//չ������̫�࣬Ϊ���ٵ�����������ﲻ��ʾ�ˡ����/2015-04-21��
			canClose = true;
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}

	private void onShow() {
		if (tempMap.size() + addtempMap.size() == 0) {//���û����صļ��Ҫ�㣬����ʾ������ٲ鿴�����/2012-03-27��
			MessageBox.show(this, "���ȼ����ٽ��в鿴!");
			return;
		}
		BillListDialog billdialog = new BillListDialog(dialog_lawitem, "������ط���", "CMP_CMPFILE_LAW_CODE1", 850, 550);
		BillListPanel billlist_showlaw = billdialog.getBilllistPanel();
		if (cmpfileid == null) {
			billlist_showlaw.getTempletItemVO("cmpfile_name").setCardisshowable(false);//������������ļ���Ĺ���������ʾ�����ļ����ơ����/2012-05-11��
			billlist_showlaw.setItemVisible("cmpfile_name", false);//��Ϊ�����billlist_showlaw �Ѿ���ʼ�����ˣ�����setListisshowable(false) �ǲ������õģ�ֻ���������á�
		}
		billlist_showlaw.addBillListHtmlHrefListener(this);
		WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD2);
		btn_show.setText("���");
		btn_show.setToolTipText("���");
		btn_show.setPreferredSize(new Dimension(45, 23));
		billlist_showlaw.addBillListButton(btn_show);
		billlist_showlaw.repaintBillListButton();
		if (WFGraphEditItemPanel.TYPE_WF.equals(this.wftype)) {
			billlist_showlaw.setItemVisible("wfactivity_name", false);
		}
		billlist_showlaw.setQuickQueryPanelVisiable(false);
		Iterator it1 = tempMap.keySet().iterator();
		Iterator it2 = addtempMap.keySet().iterator();
		while (it1.hasNext()) {
			BillVO billvo = (BillVO) tempMap.get(it1.next());
			billlist_showlaw.addRow(billvo);
		}
		while (it2.hasNext()) {
			BillVO billvo = (BillVO) addtempMap.get(it2.next());
			billlist_showlaw.addRow(billvo);
		}
		billdialog.getBtn_confirm().setVisible(false);
		billdialog.getBtn_cancel().setText("�ر�");
		billdialog.getBtn_cancel().setToolTipText("�ر�");
		billdialog.setVisible(true);
	}

	private void onConfirm() {
		if (!canClose) {
			MessageBox.show(this, "��ѡ��һ���������ģ��������");
			return;
		}
		if (insertsqls.size() > 0) {
			try {
				UIUtil.executeBatchByDS(null, insertsqls);
				billlist_law.QueryDataByCondition(null);
				tempMap.putAll(addtempMap);
				addtempMap.clear();
				insertsqls.clear();
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
		}
		dialog_lawitem.setCloseType(1);
		dialog_lawitem.dispose();
	}

	private void onCancel() {
		addtempMap.clear();
		insertsqls.clear();
		dialog_lawitem.setCloseType(2);
		dialog_lawitem.dispose();
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		showHtmlDialog(_event.getBillListPanel());
	}

	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent _event) {
		onBillListSelect();
	}

	/**
	 * ���html����ʾ������ϸ
	 */
	private void showHtmlDialog(BillListPanel _listpanel) {
		BillVO billvo = _listpanel.getSelectedBillVO();
		String str_lawid = null;
		if ("".equals(billvo.getStringValue("law_id", ""))) {
			str_lawid = billvo.getStringValue("id");
		} else {
			str_lawid = billvo.getStringValue("law_id");
		}
		new LawShowHtmlDialog(_listpanel, str_lawid);
	}

	/***
	 * ��������ȫ��
	 * Gwang 2012-10-24
	 */
	private void onLinkAll() {
		if (dialog_law.getBilllistPanel().getCheckedRows().length < 1) {
			MessageBox.show(this, "������ѡ��һ����¼,ִ�д˲���");
			return; //
		}
		//���ͨ��CheckBoxѡ��, ��ֱ�ӹ�����ѡ�е����. ��Ҫѡ����Ŀ��!(��Ŀ���������������Ч��, ��Ϊ���������¶��ǲ���������Ŀ��!)
		this.importCheckedLaw();
		dialog_law.dispose();//�رշ���ҳ��
	}

	/***
	 * ֱ�ӹ���CheckBoxѡ�е����. ��Ҫѡ����Ŀ��!(��Ŀ���������������Ч��, ��Ϊ���������¶��ǲ���������Ŀ��!)
	 * Gwang 2012-10-24
	 */
	private void importCheckedLaw() {
		BillVO[] checkedVOs = dialog_law.getBilllistPanel().getCheckedBillVOs();
		ArrayList sqlList = new ArrayList();
		String lawitem_title = "��ȫ�ġ�";
		String lawitem_content = "��ȫ�ġ�";
		String key = "";
		for (BillVO billVO : checkedVOs) {
			key = billVO.getStringValue("id") + "-null";
			if (tempMap.containsKey(key)) {
				continue;
			}

			BillVO billvo = null;
			if (billlist_law.getRowCount() > 0) {
				billvo = billlist_law.getBillVO(0);
			} else {
				int a = billlist_law.addEmptyRow();
				billvo = billlist_law.getBillVO(a);
				billlist_law.removeRow();
			}
			billvo.setObject("cmpfile_name", new StringItemVO(this.cmpfilename));
			billvo.setObject("wfprocess_code", new StringItemVO(this.processcode));
			billvo.setObject("wfprocess_name", new StringItemVO(this.processname));
			billvo.setObject("wfactivity_name", new StringItemVO(this.activityname));
			billvo.setObject("law_id", new StringItemVO(billVO.getStringValue("id")));
			billvo.setObject("law_name", new StringItemVO(billVO.getStringValue("lawname")));
			billvo.setObject("lawitem_title", new StringItemVO(lawitem_title));
			billvo.setObject("lawitem_content", new StringItemVO(lawitem_content));
			tempMap.put(key, billvo);

			InsertSQLBuilder isql_insert = new InsertSQLBuilder("cmp_cmpfile_law"); // 
			String pk = null;
			try {
				pk = UIUtil.getSequenceNextValByDS(null, "S_CMP_CMPFILE_LAW");
			} catch (Exception e) {
				e.printStackTrace();
			}
			isql_insert.putFieldValue("id", pk);
			isql_insert.putFieldValue("cmpfile_id", this.cmpfileid);
			isql_insert.putFieldValue("cmpfile_name", this.cmpfilename);
			isql_insert.putFieldValue("wfprocess_id", this.processid);
			isql_insert.putFieldValue("wfprocess_code", this.processcode);
			isql_insert.putFieldValue("wfprocess_name", this.processname);
			isql_insert.putFieldValue("law_id", billVO.getStringValue("id"));
			isql_insert.putFieldValue("law_name", billVO.getStringValue("lawname"));
			isql_insert.putFieldValue("lawitem_id", "");
			isql_insert.putFieldValue("lawitem_title", lawitem_title);
			isql_insert.putFieldValue("lawitem_content", lawitem_content);
			if (this.activityid == null) {
				isql_insert.putFieldValue("relationtype", WFGraphEditItemPanel.TYPE_WF);
			} else {
				isql_insert.putFieldValue("relationtype", WFGraphEditItemPanel.TYPE_ACTIVITY);
				isql_insert.putFieldValue("wfactivity_id", this.activityid);
				isql_insert.putFieldValue("wfactivity_code", this.activitycode);
				isql_insert.putFieldValue("wfactivity_name", this.activityname);
			}
			sqlList.add(isql_insert.getSQL());
		}
		if (sqlList.size() > 0) {
			try {
				UIUtil.executeBatchByDS(null, sqlList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			billlist_law.QueryDataByCondition(null);
		}
	}

	/***
	 * ����������Ŀ  Gwang 2012-10-24
	 */
	private void onLinkItem() {
		if (dialog_law.getBilllistPanel().getCheckedRows().length != 1) {
			MessageBox.showSelectOne(this);
			return; //
		}
		BillVO billvo_law = dialog_law.getBilllistPanel().getCheckedBillVOs()[0];
		dialog_law.dispose();//�رշ���ҳ��
		this.lawid = billvo_law.getStringValue("id");
		this.lawname = billvo_law.getStringValue("lawname");

		dialog_lawitem = new BillDialog(this, "��ѡ��һ���������ģ��������", 800, 600);//��������
		WLTSplitPane splitPanel = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, getTreePanel(), getCardPanel());
		splitPanel.setDividerLocation(230);

		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		panel.setLayout(new FlowLayout());
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");
		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this);
		panel.add(btn_confirm);
		panel.add(btn_cancel);
		canClose = false;
		dialog_lawitem.getContentPane().add(splitPanel, BorderLayout.CENTER);
		dialog_lawitem.getContentPane().add(panel, BorderLayout.SOUTH);
		dialog_lawitem.setVisible(true);
		if (dialog_lawitem.getCloseType() == -1) {
			this.onCancel();
		}
	}
}
