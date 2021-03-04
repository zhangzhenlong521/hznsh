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
	protected BillListPanel parent_BillListPanel = null; //主表列表
	protected BillCardPanel parent_BillCardPanel = null; //主表卡片
	protected BillListPanel child_BillListPanel = null; //子表列表

	protected AbstractCustomerButtonBarPanel panel_customer = null; //
	protected IUIIntercept_08 uiinterceptor = null; //

	protected CardLayout cardlayout = null; //
	private JPanel topanel = null; //
	private JPanel childcustomerpanel = null;// 子表增删改面板.
	protected JPanel btnpanel = null; //

	public abstract String getParentTableTempletcode(); //主表模板编码

	public abstract String getParentAssocField(); //

	public abstract String getChildTableTempletcode(); //子表模板编码

	public abstract String getChildAssocField(); //子表关联字段

	public boolean isShowsystembutton() {
		return true;
	}

	public void initialize() {
		super.initialize(); //

		/**
		 * 创建UI端拦截器
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
		panel_ppp.add(getBtnBarPanel()); ////按扭栏面板(系统按钮+用户自定义按钮+WorkFlow面板)
		if (isCanWorkFlowDeal()) { //如果处理工作流
			WorkflowDealBtnPanel wfbtnpanel = new WorkflowDealBtnPanel(getParent_BillListPanel()); //
			panel_ppp.add(wfbtnpanel); //
		}

		this.add(panel_ppp, BorderLayout.NORTH); //

		initSysBtnPanel(); //初始化系统按钮!!!!!

		WLTSplitPane splitPane = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, getParentPanel(), getChild_BillListPanel()); //
		this.add(splitPane, BorderLayout.CENTER); //

		parent_BillCardPanel.addBillCardEditListener(this); // 注册自己事件监听!!
		child_BillListPanel.addBillListEditListener(this);
	}

	private void initSysBtnPanel() {
		hiddenAllSysButtons(); //
		if (isCanInsert()) {
			getSysButton(BTN_INSERT).setVisible(true); //新增
		}

		if (isCanEdit()) {
			getSysButton(BTN_EDIT).setVisible(true); //编辑
		}

		if (isCanDelete()) {
			getSysButton(BTN_DELETE).setVisible(true); //删除
		}

		getSysButton(BTN_LIST).setVisible(true); //查看
	}

	/**
	 * 用户自定义面板
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

		child_BillListPanel = new BillListPanel(getChildTableTempletcode()); //创建子表列表!!
		child_BillListPanel.setCustomerNavigationJPanel(getChildCustomerJPanel()); //
		child_BillListPanel.setCustomerNavigationJPanelVisible(false); //先隐藏掉!!
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

		btn_insert.setToolTipText("新增记录");
		btn_delete.setToolTipText("删除记录");
		btn_refresh.setToolTipText("刷新");

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
	 * 新增按钮操作
	 */
	public void onInsert() {
		try {
			child_BillListPanel.setItemEditableByInit(); //
			child_BillListPanel.clearTable(); //清空数据
			child_BillListPanel.setCustomerNavigationJPanel(getChildCustomerJPanel()); //

			parent_BillCardPanel.insertRow();
			parent_BillCardPanel.setEditableByInsertInit();

			child_BillListPanel.setCustomerNavigationJPanelVisible(true); //

			hiddenAllSysButtons();
			getSysButton(BTN_SAVE).setVisible(true); //保存
			getSysButton(BTN_SAVE_RETURN).setVisible(true); //保存返回
			getSysButton(BTN_RETURN).setVisible(true); //返回

			switchToCard(); //

			// 执行主表新增 后拦截;
			if (uiinterceptor != null) {
				try {
					uiinterceptor.actionAfterInsert_parent(parent_BillCardPanel);
				} catch (Exception e1) {
					MessageBox.showException(this, e1);
					setInformation("拦截器新增后动作异常");
					return;
				}
			}
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	/**
	 * 编辑按钮操作!
	 */
	public void onEdit() {
		try {
			int li_selectedRow = parent_BillListPanel.getTable().getSelectedRow();
			if (li_selectedRow < 0) {
				return;
			}

			child_BillListPanel.setItemEditableByInit(); //子表可编辑
			child_BillListPanel.setCustomerNavigationJPanelVisible(true); //
			child_BillListPanel.repaint(); //

			parent_BillCardPanel.setBillVO(parent_BillListPanel.getBillVO(li_selectedRow)); //设置卡片中数据
			parent_BillCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //设计主表卡片编辑状态!!
			parent_BillCardPanel.setEditableByEditInit();

			hiddenAllSysButtons();
			getSysButton(BTN_SAVE).setVisible(true); //保存
			getSysButton(BTN_SAVE_RETURN).setVisible(true); //保存返回
			getSysButton(BTN_RETURN).setVisible(true); //返回

			switchToCard(); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	/**
	 * 删除按钮操作!
	 */
	protected void onDelete() {
		int li_selectedRow = parent_BillListPanel.getTable().getSelectedRow();
		if (li_selectedRow < 0) {
			return;
		}

		// 删除时级联.....
		if (MessageBox.confirm(this, "您确定要删除选中记录吗?这将删除子表中的关联记录!")) {
			// 执行主表删除前拦截动作;
			if (uiinterceptor != null) {
				try {
					uiinterceptor.actionBeforeDelete_parent(parent_BillListPanel, parent_BillListPanel.getSelectedRow());
				} catch (Exception e1) {
					MessageBox.showException(this, e1);
					return;
				}
			}

			// 主表只能删除一条纪录，所以billvo也只有一个.
			AggBillVO aggvo = new AggBillVO();
			aggvo.setParentVO(parent_BillListPanel.getBillVO(li_selectedRow)); // 只能删除一条纪录，所以billvo也只有一个.

			// 子表的数据!!
			VectorMap child_billvo = new VectorMap();
			BillVO[] tmp_childVOs = new BillVO[child_BillListPanel.getTable().getRowCount()];
			for (int j = 0; j < tmp_childVOs.length; j++) {
				tmp_childVOs[j] = child_BillListPanel.getBillVO(j);
				tmp_childVOs[j].setEditType(WLTConstants.BILLDATAEDITSTATE_DELETE); //
			}
			child_billvo.put("1", tmp_childVOs); //
			aggvo.setChildVOMaps(child_billvo);

			try {
				// 执行删除;
				dealDelete(aggvo);

				child_BillListPanel.removeAllRows();
				child_BillListPanel.clearDeleteBillVOs();
				parent_BillListPanel.removeRow();
				parent_BillListPanel.clearDeleteBillVOs();// 清除删除的纪录
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	/**
	 * 保存
	 */
	protected void onSave() {
		child_BillListPanel.stopEditing();
		if (!parent_BillCardPanel.checkValidate()) { //校验主表
			return;
		}

		if (!child_BillListPanel.checkValidate()) { //校验子表
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
				if (parent_BillCardPanel.getEditState() == WLTConstants.BILLDATAEDITSTATE_INSERT) { // 如果是新增提交
					if (!this.dealInsert(aggvo))
						return;
				} else if (parent_BillCardPanel.getEditState() == WLTConstants.BILLDATAEDITSTATE_UPDATE) { // 如果是修改提交
					if (!this.dealUpdate(aggvo))
						return;
				}

				// 设置显示信息
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
				setInformation("保存成功");
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
	 * 保存并返回
	 * 
	 */
	public void onSaveReturn() {
		child_BillListPanel.stopEditing();
		if (!parent_BillCardPanel.checkValidate()) { //校验主表
			return;
		}
		if (!child_BillListPanel.checkValidate()) { //校验子表
			return;
		}
		realSave(); //
		onReturn(); //
	}

	/**
	 * 编辑按钮操作!
	 */
	protected void onList() {
		try {
			int li_selectedRow = parent_BillListPanel.getTable().getSelectedRow();
			if (li_selectedRow < 0) {
				return;
			}

			child_BillListPanel.setItemEditable(false); //子表可编辑
			parent_BillCardPanel.setBillVO(parent_BillListPanel.getBillVO(li_selectedRow)); //设置卡片中数据
			parent_BillCardPanel.setEditable(false); //
			parent_BillCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); //设计主表卡片编辑状态!!

			hiddenAllSysButtons();
			getSysButton(BTN_RETURN).setVisible(true); //返回
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
				child_BillListPanel.QueryDataByCondition(getChildAssocField() + "='" + str_parent_id + "'"); //刷新子表数据!
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
	 * 卡片事件..
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
	 * 事件监听,用于实现update拦截器
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
			child_BillListPanel.QueryDataByCondition(getChildAssocField() + "='" + str_parent_id + "'"); //刷新子表数据!
		}
	}

	protected void onChildInsert() {
		int li_row = child_BillListPanel.newRow(); //
		child_BillListPanel.setValueAt(parent_BillCardPanel.getValueAt(getParentAssocField()), li_row, getChildAssocField());
		child_BillListPanel.setValueAt(new Integer(1).toString(), child_BillListPanel.getSelectedRow(), "VERSION");
		// 执行子表新增 后拦截;
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

		// 执行子表删除前拦截;
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
	 * 新增 根据主表的BILLCARD来确定.
	 * 
	 */
	protected boolean dealInsert(AggBillVO _insertVO) throws Exception {
		// 执行新增提交前的拦截器
		setInformation("执行拦截器新增前处理");
		if (this.uiinterceptor != null) {
			try {
				uiinterceptor.dealCommitBeforeInsert(this, _insertVO);
			} catch (Exception e) {
				MessageBox.showException(this, e);
				return false;
			}
		}

		AggBillVO returnVO = getMetaService().style08_dealInsert(parent_BillListPanel.getDataSourceName(), getBsinterceptor(), _insertVO); // 直接提交数据库,这里可能抛异常!!
		setInformation("执行拦截器新增后处理");
		// 执行新增提交前的拦截器
		if (this.uiinterceptor != null)
			uiinterceptor.dealCommitAfterInsert(this, returnVO); //
		return true;
	}

	/**
	 * 修改提交
	 * 
	 */
	protected boolean dealUpdate(AggBillVO _updateVO) throws Exception {
		setInformation("执行拦截器修改前处理");
		if (this.uiinterceptor != null) {
			try {
				uiinterceptor.dealCommitBeforeUpdate(this, _updateVO); // 修改提交前拦截器
			} catch (Exception ex) {
				MessageBox.showException(this, ex);
				return false;
			}
		}

		AggBillVO returnvo = getMetaService().style08_dealUpdate(parent_BillListPanel.getDataSourceName(), getBsinterceptor(), _updateVO); //

		setInformation("执行拦截器修改后处理");
		if (this.uiinterceptor != null) {
			uiinterceptor.dealCommitAfterUpdate(this, returnvo); //
		}
		return true;

	}

	/**
	 * 删除提交 主表删除时调用
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
		//UI端删除后拦截器!!
		if (this.uiinterceptor != null) {
			uiinterceptor.dealCommitAfterDelete(this, _deleteVO);
		}
	}

	public void onChildRefresh() {
		String str_parent_id = this.parent_BillCardPanel.getCompentRealValue(getParentAssocField()); //
		child_BillListPanel.QueryDataByCondition(getChildAssocField() + "='" + str_parent_id + "'"); //刷新子表数据!
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
