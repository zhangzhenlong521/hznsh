package cn.com.infostrategy.ui.mdata.styletemplet.t08;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.AggBillVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.AbstractCustomerButtonBarPanel;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListEditEvent;
import cn.com.infostrategy.ui.mdata.BillListEditListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;
import cn.com.infostrategy.ui.workflow.engine.WorkflowDealBtnPanel;

public abstract class AbstractStyleWorkPanel_08 extends AbstractStyleWorkPanel implements BillCardEditListener, BillListEditListener, BillListSelectListener {
	protected BillListPanel parent_BillListPanel = null; //�����б�
	protected BillCardPanel parent_BillCardPanel = null; //����Ƭ
	protected BillListPanel child_BillListPanel = null; //�ӱ��б�

	protected AbstractCustomerButtonBarPanel panel_customer = null; //
	protected IUIIntercept_08 uiinterceptor = null; //

	protected CardLayout cardlayout = null; //
	private JPanel topanel = null; //
	private JPanel childcustomerpanel = null;// �ӱ���ɾ�����.
	protected JPanel btnpanel = null; //

	public abstract String getParentTableTempletcode(); //����ģ�����

	public abstract String getParentAssocField(); //

	public abstract String getChildTableTempletcode(); //�ӱ�ģ�����

	public abstract String getChildAssocField(); //�ӱ�����ֶ�

	public boolean isShowsystembutton() {
		return true;
	}

	public void initialize() {
		super.initialize(); //

		/**
		 * ����UI��������
		 */
		if (getUiinterceptor() != null && !getUiinterceptor().trim().equals("")) {
			try {
				uiinterceptor = (IUIIntercept_08) Class.forName(getUiinterceptor().trim()).newInstance(); //
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}

		this.setLayout(new BorderLayout()); //

		JPanel panel_ppp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); //
		panel_ppp.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		panel_ppp.add(getBtnBarPanel()); ////��Ť�����(ϵͳ��ť+�û��Զ��尴ť+WorkFlow���)
		if (isCanWorkFlowDeal()) { //�����������
			WorkflowDealBtnPanel wfbtnpanel = new WorkflowDealBtnPanel(getParent_BillListPanel()); //
			panel_ppp.add(wfbtnpanel); //
		}

		this.add(panel_ppp, BorderLayout.NORTH); //

		initSysBtnPanel(); //��ʼ��ϵͳ��ť!!!!!

		WLTSplitPane splitPane = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, getParentPanel(), getChild_BillListPanel()); //
		this.add(splitPane, BorderLayout.CENTER); //

		parent_BillCardPanel.addBillCardEditListener(this); // ע���Լ��¼�����!!
		child_BillListPanel.addBillListEditListener(this);
	}

	private void initSysBtnPanel() {
		hiddenAllSysButtons(); //
		if (isCanInsert()) {
			getSysButton(BTN_INSERT).setVisible(true); //����
		}

		if (isCanEdit()) {
			getSysButton(BTN_EDIT).setVisible(true); //�༭
		}

		if (isCanDelete()) {
			getSysButton(BTN_DELETE).setVisible(true); //ɾ��
		}

		getSysButton(BTN_LIST).setVisible(true); //�鿴
	}

	/**
	 * �û��Զ������
	 * 
	 * @return JPanel
	 */
	protected JPanel getCustomerBtnPanel() {
		try {
			panel_customer = (AbstractCustomerButtonBarPanel) Class.forName(getCustBtnPanelName()).newInstance();
			panel_customer.setParentWorkPanel(this); //
			panel_customer.initialize(); //
			return panel_customer;
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		return new JPanel();
	}

	protected JPanel getParentPanel() {
		cardlayout = new CardLayout();
		topanel = new JPanel(cardlayout);
		topanel.add("list", getParent_BillListPanel()); //
		topanel.add("card", getParent_BillCardPanel());
		return topanel;
	}

	public BillListPanel getParent_BillListPanel() {
		if (parent_BillListPanel != null) {
			return parent_BillListPanel;
		}
		parent_BillListPanel = new BillListPanel(getParentTableTempletcode());
		parent_BillListPanel.setLoadedWorkPanel(this); //
		parent_BillListPanel.setItemEditable(false); //
		parent_BillListPanel.addBillListSelectListener(this); //
		parent_BillListPanel.getQuickQueryPanel().setVisible(true); //
		return parent_BillListPanel;
	}

	public BillCardPanel getParent_BillCardPanel() {
		if (parent_BillCardPanel != null) {
			return parent_BillCardPanel;
		}
		parent_BillCardPanel = new BillCardPanel(parent_BillListPanel.getTempletVO());
		parent_BillCardPanel.setLoadedWorkPanel(this);
		return parent_BillCardPanel;
	}

	public BillListPanel getChild_BillListPanel() {
		if (child_BillListPanel != null) {
			return child_BillListPanel;
		}

		child_BillListPanel = new BillListPanel(getChildTableTempletcode()); //�����ӱ��б�!!
		child_BillListPanel.setCustomerNavigationJPanel(getChildCustomerJPanel()); //
		child_BillListPanel.setCustomerNavigationJPanelVisible(false); //�����ص�!!
		child_BillListPanel.setLoadedWorkPanel(this);
		child_BillListPanel.setItemEditable(false); //
		return child_BillListPanel;
	}

	protected JPanel getChildCustomerJPanel() {
		if (childcustomerpanel != null)
			return childcustomerpanel;

		childcustomerpanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));

		JButton btn_insert = new JButton(UIUtil.getImage("insert.gif")); //
		JButton btn_delete = new JButton(UIUtil.getImage("delete.gif")); //
		JButton btn_refresh = new JButton(UIUtil.getImage("refresh.gif")); //

		btn_insert.setToolTipText("������¼");
		btn_delete.setToolTipText("ɾ����¼");
		btn_refresh.setToolTipText("ˢ��");

		btn_insert.setPreferredSize(new Dimension(18, 18));
		btn_delete.setPreferredSize(new Dimension(18, 18));
		btn_refresh.setPreferredSize(new Dimension(18, 18));

		btn_insert.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onChildInsert();
			}
		});

		btn_delete.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onChildDelete();
			}
		});

		btn_refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onChildRefresh();
			}
		});

		childcustomerpanel.add(btn_insert);
		childcustomerpanel.add(btn_delete);
		childcustomerpanel.add(btn_refresh);

		return childcustomerpanel;
	}

	/**
	 * ������ť����
	 */
	public void onInsert() {
		try {
			child_BillListPanel.setItemEditableByInit(); //
			child_BillListPanel.clearTable(); //�������
			child_BillListPanel.setCustomerNavigationJPanel(getChildCustomerJPanel()); //

			parent_BillCardPanel.insertRow();
			parent_BillCardPanel.setEditableByInsertInit();

			child_BillListPanel.setCustomerNavigationJPanelVisible(true); //

			hiddenAllSysButtons();
			getSysButton(BTN_SAVE).setVisible(true); //����
			getSysButton(BTN_SAVE_RETURN).setVisible(true); //���淵��
			getSysButton(BTN_RETURN).setVisible(true); //����

			switchToCard(); //

			// ִ���������� ������;
			if (uiinterceptor != null) {
				try {
					uiinterceptor.actionAfterInsert_parent(parent_BillCardPanel);
				} catch (Exception e1) {
					MessageBox.showException(this, e1);
					setInformation("���������������쳣");
					return;
				}
			}
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	/**
	 * �༭��ť����!
	 */
	public void onEdit() {
		try {
			int li_selectedRow = parent_BillListPanel.getTable().getSelectedRow();
			if (li_selectedRow < 0) {
				return;
			}

			child_BillListPanel.setItemEditableByInit(); //�ӱ�ɱ༭
			child_BillListPanel.setCustomerNavigationJPanelVisible(true); //
			child_BillListPanel.repaint(); //

			parent_BillCardPanel.setBillVO(parent_BillListPanel.getBillVO(li_selectedRow)); //���ÿ�Ƭ������
			parent_BillCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //�������Ƭ�༭״̬!!
			parent_BillCardPanel.setEditableByEditInit();

			hiddenAllSysButtons();
			getSysButton(BTN_SAVE).setVisible(true); //����
			getSysButton(BTN_SAVE_RETURN).setVisible(true); //���淵��
			getSysButton(BTN_RETURN).setVisible(true); //����

			switchToCard(); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * ɾ����ť����!
	 */
	protected void onDelete() {
		int li_selectedRow = parent_BillListPanel.getTable().getSelectedRow();
		if (li_selectedRow < 0) {
			return;
		}

		// ɾ��ʱ����.....
		if (MessageBox.confirm(this, "��ȷ��Ҫɾ��ѡ�м�¼��?�⽫ɾ���ӱ��еĹ�����¼!")) {
			// ִ������ɾ��ǰ���ض���;
			if (uiinterceptor != null) {
				try {
					uiinterceptor.actionBeforeDelete_parent(parent_BillListPanel, parent_BillListPanel.getSelectedRow());
				} catch (Exception e1) {
					MessageBox.showException(this, e1);
					return;
				}
			}

			// ����ֻ��ɾ��һ����¼������billvoҲֻ��һ��.
			AggBillVO aggvo = new AggBillVO();
			aggvo.setParentVO(parent_BillListPanel.getBillVO(li_selectedRow)); // ֻ��ɾ��һ����¼������billvoҲֻ��һ��.

			// �ӱ������!!
			VectorMap child_billvo = new VectorMap();
			BillVO[] tmp_childVOs = new BillVO[child_BillListPanel.getTable().getRowCount()];
			for (int j = 0; j < tmp_childVOs.length; j++) {
				tmp_childVOs[j] = child_BillListPanel.getBillVO(j);
				tmp_childVOs[j].setEditType(WLTConstants.BILLDATAEDITSTATE_DELETE); //
			}
			child_billvo.put("1", tmp_childVOs); //
			aggvo.setChildVOMaps(child_billvo);

			try {
				// ִ��ɾ��;
				dealDelete(aggvo);

				child_BillListPanel.removeAllRows();
				child_BillListPanel.clearDeleteBillVOs();
				parent_BillListPanel.removeRow();
				parent_BillListPanel.clearDeleteBillVOs();// ���ɾ���ļ�¼
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	/**
	 * ����
	 */
	protected void onSave() {
		child_BillListPanel.stopEditing();
		if (!parent_BillCardPanel.checkValidate()) { //У������
			return;
		}

		if (!child_BillListPanel.checkValidate()) { //У���ӱ�
			return;
		}
		realSave(); //
		return;
	}

	private void realSave() {
		try {
			AggBillVO aggvo = new AggBillVO();
			aggvo.setParentVO(parent_BillCardPanel.getBillVO()); //
			VectorMap child_billvo = new VectorMap();
			child_billvo.put("1", child_BillListPanel.getBillVOs()); //
			aggvo.setChildVOMaps(child_billvo); //
			try {
				if (parent_BillCardPanel.getEditState() == WLTConstants.BILLDATAEDITSTATE_INSERT) { // ����������ύ
					if (!this.dealInsert(aggvo))
						return;
				} else if (parent_BillCardPanel.getEditState() == WLTConstants.BILLDATAEDITSTATE_UPDATE) { // ������޸��ύ
					if (!this.dealUpdate(aggvo))
						return;
				}

				// ������ʾ��Ϣ
				if (parent_BillCardPanel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
					int li_newrow = parent_BillListPanel.newRow();
					parent_BillListPanel.setBillVOAt(li_newrow, parent_BillCardPanel.getBillVO()); //
					parent_BillListPanel.getTable().setRowSelectionInterval(li_newrow, li_newrow); //
					parent_BillCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
					parent_BillListPanel.setValueAt(new RowNumberItemVO(WLTConstants.BILLDATAEDITSTATE_INIT, li_newrow + 1), li_newrow, "_RECORD_ROW_NUMBER");
				} else if (parent_BillCardPanel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
					if (parent_BillListPanel.getSelectedRow() >= 0) {
						parent_BillListPanel.setBillVOAt(parent_BillListPanel.getSelectedRow(), parent_BillCardPanel.getBillVO()); //
					}
				}
				child_BillListPanel.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
				parent_BillListPanel.setRowStatusAs(parent_BillListPanel.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT); //
				child_BillListPanel.repaint(); //
				setInformation("����ɹ�");
			} catch (Exception e) {
				MessageBox.showException(this, e);
				return;
			}
		} catch (Exception e) {
			MessageBox.showException(this, e);
			return;
		}
	}

	/**
	 * ���沢����
	 * 
	 */
	public void onSaveReturn() {
		child_BillListPanel.stopEditing();
		if (!parent_BillCardPanel.checkValidate()) { //У������
			return;
		}
		if (!child_BillListPanel.checkValidate()) { //У���ӱ�
			return;
		}
		realSave(); //
		onReturn(); //
	}

	/**
	 * �༭��ť����!
	 */
	protected void onList() {
		try {
			int li_selectedRow = parent_BillListPanel.getTable().getSelectedRow();
			if (li_selectedRow < 0) {
				return;
			}

			child_BillListPanel.setItemEditable(false); //�ӱ�ɱ༭
			parent_BillCardPanel.setBillVO(parent_BillListPanel.getBillVO(li_selectedRow)); //���ÿ�Ƭ������
			parent_BillCardPanel.setEditable(false); //
			parent_BillCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); //�������Ƭ�༭״̬!!

			hiddenAllSysButtons();
			getSysButton(BTN_RETURN).setVisible(true); //����
			switchToCard(); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	public void onReturn() {
		initSysBtnPanel(); //
		child_BillListPanel.setCustomerNavigationJPanelVisible(false); ///
		BillVO selectedBillVO = parent_BillListPanel.getSelectedBillVO(); //
		if (selectedBillVO != null) {
			String str_parent_id = selectedBillVO.getStringValue(getParentAssocField()); //
			if (str_parent_id != null) {
				child_BillListPanel.QueryDataByCondition(getChildAssocField() + "='" + str_parent_id + "'"); //ˢ���ӱ�����!
			}
		}
		child_BillListPanel.setItemEditable(false); //
		switchToList(); //
	}

	public void switchToCard() {
		cardlayout.show(topanel, "card"); //
		updateButtonUI(); //
	}

	public void switchToList() {
		cardlayout.show(topanel, "list"); //
		updateButtonUI(); //
	}

	/**
	 * ��Ƭ�¼�..
	 */
	public void onBillCardValueChanged(BillCardEditEvent _evt) {
		if (uiinterceptor != null) {
			BillCardPanel card_tmp = (BillCardPanel) _evt.getSource(); //
			String tmp_itemkey = _evt.getItemKey(); //
			try {
				uiinterceptor.actionAfterUpdate_parent(card_tmp, tmp_itemkey);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	/**
	 * �¼�����,����ʵ��update������
	 */
	public void onBillListValueChanged(BillListEditEvent _evt) {
		if (uiinterceptor != null) {
			BillListPanel list_tmp = (BillListPanel) _evt.getSource(); //
			String tmp_itemkey = _evt.getItemKey(); //
			try {
				uiinterceptor.actionAfterUpdate_child(list_tmp, tmp_itemkey, list_tmp.getSelectedRow());
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO billVO = _event.getCurrSelectedVO();
		String str_parent_id = billVO.getStringValue(getParentAssocField()); //\
		if (str_parent_id != null) {
			child_BillListPanel.QueryDataByCondition(getChildAssocField() + "='" + str_parent_id + "'"); //ˢ���ӱ�����!
		}
	}

	protected void onChildInsert() {
		int li_row = child_BillListPanel.newRow(); //
		child_BillListPanel.setValueAt(parent_BillCardPanel.getValueAt(getParentAssocField()), li_row, getChildAssocField());
		child_BillListPanel.setValueAt(new Integer(1).toString(), child_BillListPanel.getSelectedRow(), "VERSION");
		// ִ���ӱ����� ������;
		if (uiinterceptor != null) {
			try {
				uiinterceptor.actionAfterInsert_child(child_BillListPanel, li_row);
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
			}
		}
	}

	protected void onChildDelete() {
		int li_selrow = child_BillListPanel.getTable().getSelectedRow();
		if (li_selrow < 0) {
			return;
		}

		// ִ���ӱ�ɾ��ǰ����;
		if (uiinterceptor != null) {
			try {
				uiinterceptor.actionBeforeDelete_child(child_BillListPanel, li_selrow);
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
				return;
			}
		}
		child_BillListPanel.removeRow();
	}

	/**
	 * ���� ���������BILLCARD��ȷ��.
	 * 
	 */
	protected boolean dealInsert(AggBillVO _insertVO) throws Exception {
		// ִ�������ύǰ��������
		setInformation("ִ������������ǰ����");
		if (this.uiinterceptor != null) {
			try {
				uiinterceptor.dealCommitBeforeInsert(this, _insertVO);
			} catch (Exception e) {
				MessageBox.showException(this, e);
				return false;
			}
		}

		AggBillVO returnVO = getMetaService().style08_dealInsert(parent_BillListPanel.getDataSourceName(), getBsinterceptor(), _insertVO); // ֱ���ύ���ݿ�,����������쳣!!
		setInformation("ִ����������������");
		// ִ�������ύǰ��������
		if (this.uiinterceptor != null)
			uiinterceptor.dealCommitAfterInsert(this, returnVO); //
		return true;
	}

	/**
	 * �޸��ύ
	 * 
	 */
	protected boolean dealUpdate(AggBillVO _updateVO) throws Exception {
		setInformation("ִ���������޸�ǰ����");
		if (this.uiinterceptor != null) {
			try {
				uiinterceptor.dealCommitBeforeUpdate(this, _updateVO); // �޸��ύǰ������
			} catch (Exception ex) {
				MessageBox.showException(this, ex);
				return false;
			}
		}

		AggBillVO returnvo = getMetaService().style08_dealUpdate(parent_BillListPanel.getDataSourceName(), getBsinterceptor(), _updateVO); //

		setInformation("ִ���������޸ĺ���");
		if (this.uiinterceptor != null) {
			uiinterceptor.dealCommitAfterUpdate(this, returnvo); //
		}
		return true;

	}

	/**
	 * ɾ���ύ ����ɾ��ʱ����
	 * 
	 */
	protected void dealDelete(AggBillVO _deleteVO) throws Exception {
		if (this.uiinterceptor != null) {
			try {
				uiinterceptor.dealCommitBeforeDelete(this, _deleteVO);
			} catch (Exception ex) {
				MessageBox.showException(this, ex);
				return;
			}
		}
		getMetaService().style08_dealDelete(parent_BillListPanel.getDataSourceName(), getBsinterceptor(), _deleteVO); //
		//UI��ɾ����������!!
		if (this.uiinterceptor != null) {
			uiinterceptor.dealCommitAfterDelete(this, _deleteVO);
		}
	}

	public void onChildRefresh() {
		String str_parent_id = this.parent_BillCardPanel.getCompentRealValue(getParentAssocField()); //
		child_BillListPanel.QueryDataByCondition(getChildAssocField() + "='" + str_parent_id + "'"); //ˢ���ӱ�����!
	}

	public AbstractCustomerButtonBarPanel getPanel_customer() {
		return panel_customer;
	}

	public boolean isCanDelete() {
		return true;
	}

	public boolean isCanEdit() {
		return true;
	}

	public boolean isCanInsert() {
		return true;
	}

	public boolean isCanWorkFlowDeal() {
		return false;
	}

	public boolean isCanWorkFlowMonitor() {
		return false;
	}

}
