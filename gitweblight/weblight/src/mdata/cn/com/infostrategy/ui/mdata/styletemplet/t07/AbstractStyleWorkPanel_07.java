/**************************************************************************
 * $RCSfile: AbstractStyleWorkPanel_07.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t07;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.AbstractCustomerButtonBarPanel;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;

public abstract class AbstractStyleWorkPanel_07 extends AbstractStyleWorkPanel implements BillCardEditListener, BillListSelectListener {
	protected BillListPanel parentBillListPanel = null; // 主表,入口表,左表
	protected BillListPanel childBillListPanel = null; // 子表,关联表,处理表,右表
	protected BillCardPanel childBillCardPanel = null;

	protected JPanel toftPanel = null;
	protected CardLayout cardlayout = null;

	protected AbstractCustomerButtonBarPanel panel_customer = null;

	private IUIIntercept_07 uiIntercept = null; // ui端拦截器

	public abstract String getParentTempletCode(); //

	public abstract String getParentAssocField();

	public abstract String getChildTempletCode(); //

	public abstract String getChildAssocField(); //

	public int getOrientation() {
		return JSplitPane.VERTICAL_SPLIT;
	}

	public boolean isShowsystembutton() {
		return true;
	}

	/**
	 * 初始化页面!!
	 */
	public void initialize() {
		super.initialize(); //
		if (getUiinterceptor() != null && !getUiinterceptor().trim().equals("")) {
			try {
				uiIntercept = (IUIIntercept_07) Class.forName(getUiinterceptor().trim()).newInstance(); //
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}

		this.setLayout(new BorderLayout());
		JSplitPane splitPanel = null;
		if (getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
			splitPanel = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, getParentBillListPanel(), getChildPanel());
		} else {
			splitPanel = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, getParentBillListPanel(), getChildPanel());
		}
		this.add(splitPanel, BorderLayout.CENTER);
	}

	protected JPanel getChildPanel() {
		JPanel rpanel = new JPanel();
		rpanel.setLayout(new BorderLayout());

		toftPanel = new JPanel();
		cardlayout = new CardLayout();
		toftPanel.setLayout(cardlayout);

		toftPanel.add("list", getChildBillListPanel());
		toftPanel.add("card", getChildBillCardPanel());
		rpanel.add(toftPanel, BorderLayout.CENTER);

		rpanel.add(getBtnBarPanel(), BorderLayout.NORTH);
		initSysBtnPanel(); //
		return rpanel;
	}

	/**
	 * 获取主表所在的BillListPanel
	 *
	 * @return BillListPanel
	 */
	public BillListPanel getParentBillListPanel() {
		if (parentBillListPanel == null) {
			parentBillListPanel = new BillListPanel(getParentTempletCode());
			parentBillListPanel.setItemEditable(false);
			parentBillListPanel.addBillListSelectListener(this); //
			parentBillListPanel.getQuickQueryPanel().setVisible(true); //
		}
		return parentBillListPanel;
	}

	/**
	 * 获取子表所在的BillListPanel
	 *
	 * @return BillListPanel
	 */
	public BillListPanel getChildBillListPanel() {
		if (childBillListPanel == null) {
			childBillListPanel = new BillListPanel(getChildTempletCode());
			childBillListPanel.setAllItemValue("listiseditable", WLTConstants.BILLCOMPENTEDITABLE_NONE);
		}
		return childBillListPanel;
	}

	/**
	 * 子表卡片
	 * @return
	 */
	public BillCardPanel getChildBillCardPanel() {
		if (childBillCardPanel == null) {
			childBillCardPanel = new BillCardPanel(childBillListPanel.getTempletVO());
			childBillCardPanel.addBillCardEditListener(this); // 注册自己事件监听!!
		}
		return childBillCardPanel;
	}

	/**
	 * 初始化系统按钮!!
	 */
	private void initSysBtnPanel() {
		hiddenAllSysButtons(); //
		if (isCanInsert()) {
			getSysButton(BTN_INSERT).setVisible(true); //
		}

		if (isCanEdit()) {
			getSysButton(BTN_EDIT).setVisible(true); //
		}

		if (isCanDelete()) {
			getSysButton(BTN_DELETE).setVisible(true); //
		}

		getSysButton(BTN_REFRESH).setVisible(true); //
	}

	/**
	 * 新增动作!!
	 */
	protected void onInsert() {
		try {
			//childBillListPanel.getTable().editingStopped(new ChangeEvent(childBillListPanel.getTable()));  //
			int li_row = parentBillListPanel.getSelectedRow();
			if (li_row < 0) {
				return;
			}

			String str_parentid = parentBillListPanel.getRealValueAtModel(li_row, getParentAssocField()); //
			childBillCardPanel.insertRow(); //
			childBillCardPanel.setValueAt(this.getChildAssocField(), new StringItemVO(str_parentid)); //
			childBillCardPanel.setEditableByInsertInit(); //
			hiddenAllSysButtons();
			getSysButton(BTN_SAVE).setVisible(true); //
			getSysButton(BTN_SAVE_RETURN).setVisible(true); //
			getSysButton(BTN_RETURN).setVisible(true); //
			switchToCard();

			// 执行拦截器操作!!
			if (uiIntercept != null) {
				try {
					uiIntercept.actionAfterInsert(childBillCardPanel); // 执行删除前的动作!!
				} catch (Exception e) {
					MessageBox.showException(this, e);
				}
			}
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	protected void onEdit() {
		int li_row = childBillListPanel.getSelectedRow(); // 取得选中的行!!
		if (li_row < 0) {
			return;
		}

		try {
			childBillCardPanel.setBillVO(childBillListPanel.getBillVO(li_row)); //
			childBillCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);

			hiddenAllSysButtons(); //
			getSysButton(BTN_SAVE).setVisible(true); //
			getSysButton(BTN_SAVE_RETURN).setVisible(true); //
			getSysButton(BTN_RETURN).setVisible(true); //

			switchToCard();
		} catch (Exception ex) {
			MessageBox.show(AbstractStyleWorkPanel_07.this, WLTConstants.STRING_OPERATION_FAILED + ":" + ex.getMessage(), WLTConstants.MESSAGE_ERROR);
		}
	}

	protected void onDelete() {
		int li_row = childBillListPanel.getTable().getSelectedRow(); // 取得选中的行!!
		if (li_row < 0) {
			return;
		}
		// 执行拦截器删除前操作!!
		if (uiIntercept != null) {
			try {
				uiIntercept.actionBeforeDelete(childBillListPanel, li_row); // 执行删除前的动作!!
			} catch (Exception e) {
				MessageBox.showException(this, e);
				return; // 不往下走了!!
			}
		}

		// 提交删除数据!!!
		try {
			BillVO vo = childBillListPanel.getBillVO(li_row); //
			dealDelete(vo); // 真正删除
			childBillListPanel.removeRow(li_row); // 如果成功
			childBillListPanel.clearDeleteBillVOs();
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	protected void onSave() {
		childBillListPanel.getTable().editingStopped(new ChangeEvent(childBillListPanel.getTable()));
		if (childBillCardPanel.getEditState() == WLTConstants.BILLDATAEDITSTATE_INSERT) { // 如果是新增提交
			BillVO billVO = childBillCardPanel.getBillVO(); //

			//检查必输入项....
			try {
				if (!dealInsert(billVO))// 新增提交
					return;
				childBillCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
				HashMap map = childBillCardPanel.getAllObjectValuesWithHashMap();
				childBillListPanel.insertRowWithInitStatus(childBillListPanel.getSelectedRow(), map);
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
				return;
			}
		} else if (childBillCardPanel.getEditState() == WLTConstants.BILLDATAEDITSTATE_UPDATE) { // 如果是修改提交
			BillVO billVO = childBillCardPanel.getBillVO(); //
			// 检查必输项
			try {
				if (!dealUpdate(billVO))
					return; // 修改提交
				childBillListPanel.setValueAtRow(childBillListPanel.getSelectedRow(), billVO);
				childBillListPanel.setRowStatusAs(childBillListPanel.getSelectedRow(), "INIT");
				childBillCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
			} catch (Exception e1) {
				MessageBox.showException(this, e1);
				return;
			}
		}
		childBillListPanel.setAllRowStatusAs("INIT");
		return;
	};

	protected void onSaveReturn() {
		onSave();
		onReturn();
	}

	protected void onReturn() {
		initSysBtnPanel(); //
		switchToList(); //
	}

	protected void onRefresh() {
		int li_row = getParentBillListPanel().getSelectedRow();
		if (li_row < 0) {
			return;
		}

		String str_id = getParentBillListPanel().getBillVO(li_row).getStringValue(this.getParentAssocField()); //
		getChildBillListPanel().QueryDataByCondition(this.getChildAssocField() + "='" + str_id + "'"); ////..
	}

	private void switchToCard() {
		cardlayout.show(toftPanel, "card");
		setCurrShowType(CARDTYPE);
	}

	private void switchToList() {
		cardlayout.show(toftPanel, "list");
		setCurrShowType(LISTTYPE);
	}

	protected boolean dealInsert(BillVO _insertVO) throws Exception {
		// 执行新增提交前的拦截器
		if (this.uiIntercept != null) {
			try {
				uiIntercept.dealCommitBeforeInsert(this, _insertVO);
			} catch (Exception e) {
				MessageBox.showException(this, e);
				return false;
			}
		}

		BillVO returnVO = getMetaService().style07_dealInsert(childBillListPanel.getDataSourceName(), getBsinterceptor(), _insertVO); // 直接提交数据库,这里可能抛异常!!

		// 执行新增提交后的拦截器
		if (this.uiIntercept != null) {
			uiIntercept.dealCommitAfterInsert(this, returnVO); //
		}
		return true;
	}

	protected boolean dealUpdate(BillVO _updateVO) throws Exception {
		if (this.uiIntercept != null) {
			try {
				uiIntercept.dealCommitBeforeUpdate(this, _updateVO); // 修改提交前拦截器
			} catch (Exception ex) {
				MessageBox.showException(this, ex);
				return false;
			}
		}

		BillVO returnvo = getMetaService().style07_dealUpdate(childBillListPanel.getDataSourceName(), getBsinterceptor(), _updateVO); //

		if (this.uiIntercept != null) {
			uiIntercept.dealCommitAfterUpdate(this, returnvo); //
		}
		return true;
	}

	protected void dealDelete(BillVO _deleteVO) throws Exception {
		if (this.uiIntercept != null) {
			try {
				uiIntercept.dealCommitBeforeDelete(this, _deleteVO);
			} catch (Exception ex) {
				MessageBox.showException(this, ex);
				return;
			}
		}

		getMetaService().style07_dealDelete(childBillListPanel.getDataSourceName(), getBsinterceptor(), _deleteVO); //

		if (this.uiIntercept != null) {
			uiIntercept.dealCommitAfterDelete(this, _deleteVO);
		}
	}

	/**
	 * 卡片会调用这里
	 */
	public void onBillCardValueChanged(BillCardEditEvent _evt) {
		if (uiIntercept != null) {
			BillCardPanel card_tmp = (BillCardPanel) _evt.getSource(); //
			String tmp_itemkey = _evt.getItemKey(); //
			try {
				uiIntercept.actionAfterUpdate(card_tmp, tmp_itemkey);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) //选中的是哪一行!!
	{
		if (getCurrShowType() != CARDTYPE) {
			BillVO vo = _event.getCurrSelectedVO();
			String str_id = vo.getStringValue(this.getParentAssocField());
			getChildBillListPanel().QueryDataByCondition(getChildAssocField() + "='" + str_id + "'");
		}
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
