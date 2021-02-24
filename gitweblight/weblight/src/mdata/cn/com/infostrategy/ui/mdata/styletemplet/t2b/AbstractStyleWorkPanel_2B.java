package cn.com.infostrategy.ui.mdata.styletemplet.t2b;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;

/**
 * 风格模板13,即两张表实际上是主子表,先选中主表，然后点击一条记录进行子表编辑，即主子表都是列表模式,但同时只能看到一个,只编辑子表，而且子表是列表模式
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_2B extends AbstractStyleWorkPanel {

	private static final long serialVersionUID = -8841608749741379946L;

	private BillListPanel parentBillListPanel = null; //主表列表面板

	private BillListPanel childBillListPanel = null; //子表列表面板

	private JPanel childcustomerpanel = null;// 子表增删改面板.

	private CardLayout cardLayout = null; //

	private JPanel toftPanel = null; //装载CardLayout的面板..

	private IUIIntercept_2B uiinterceptor = null; //前端拦截器

	protected abstract String getParentTempletCode(); //取得主表模板编码

	protected abstract String getParentAssocField(); //取得主表关联字段

	protected abstract String getChildTempletCode(); //取得子表模板编码

	protected abstract String getChildAssocField(); //取得子表关联字段

	private int leveltype = WLTConstants.LEVELTYPE_LIST; //

	Pub_Templet_1VO parentTempletVO = null;
	Pub_Templet_1VO childTempletVO = null;

	/**
	 * 初始化页面
	 */
	public void initialize() {
		super.initialize();

		try {
			Pub_Templet_1VO[] templetVOs = UIUtil.getPub_Templet_1VOs(new String[] { getParentTempletCode(), getChildTempletCode() }); //
			parentTempletVO = templetVOs[0];
			childTempletVO = templetVOs[1];

			this.setLayout(new BorderLayout()); //
			this.add(getBtnBarPanel(), BorderLayout.NORTH);
			initSysBtnPanel(); //

			cardLayout = new CardLayout(); //
			toftPanel = new JPanel(cardLayout); //
			toftPanel.add("list", getParentBillListPanel()); //主表,列表样式.
			toftPanel.add("card", getChildBillListPanel()); //子表,列表样式.
			this.add(toftPanel, BorderLayout.CENTER); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 初始化系统按钮拦.
	 */
	private void initSysBtnPanel() {
		hiddenAllSysButtons(); //隐藏所有按钮!!!

		if (isCanEdit()) {
			getSysButton(BTN_EDIT).setVisible(true);
		}

		if (isCanDelete()) {
			getSysButton(BTN_DELETE).setVisible(true);
		}

		getSysButton(BTN_LIST).setVisible(true); //查看

		if (isCanWorkFlowDeal()) {
			//getSysButton(BTN_WORKFLOW_SUBMIT).setVisible(true); //流程处理
		}

		if (isCanWorkFlowMonitor()) {
			getSysButton(BTN_WORKFLOW_MONITOR).setVisible(true); //流程监控
		}

		getSysBtnPanel().updateUI(); //
	}

	/**
	 * 取得主表列表面板..
	 * @return
	 */
	public BillListPanel getParentBillListPanel() {
		if (parentBillListPanel == null) {
			parentBillListPanel = new BillListPanel(parentTempletVO); //
			parentBillListPanel.setItemEditable(false); //
			parentBillListPanel.getQuickQueryPanel().setVisible(true); //
		}
		return parentBillListPanel;
	}

	/**
	 * 取得子表卡片面板..
	 * @return
	 */
	public BillListPanel getChildBillListPanel() {
		if (childBillListPanel == null) {
			childBillListPanel = new BillListPanel(childTempletVO); //
			childBillListPanel.setCustomerNavigationJPanel(getChildCustomerJPanel()); //
			childBillListPanel.setCustomerNavigationJPanelVisible(true); //先隐藏掉!!
			childBillListPanel.setLoadedWorkPanel(this);
			childBillListPanel.setItemEditable(true); //
		}
		return childBillListPanel;
	}

	public void switchToCard() {
		cardLayout.show(toftPanel, "card"); //
		leveltype = WLTConstants.LEVELTYPE_CARD;
	}

	public void switchToList() {
		cardLayout.show(toftPanel, "list"); //
		leveltype = WLTConstants.LEVELTYPE_LIST;
	}

	/**
	 * 返回工作流处理的数据VO
	 */
	public BillVO getWorkFlowDealBillVO() {
		return getParentBillListPanel().getSelectedBillVO(); //
	}

	protected void onEdit() {
		int li_row = getParentBillListPanel().getSelectedRow();
		if (li_row < 0) {
			return;
		}

		BillVO billVO = getParentBillListPanel().getBillVO(li_row); ////
		String str_parentID = billVO.getStringValue(getParentAssocField()); ////
		String str_condition = getChildAssocField() + "='" + str_parentID + "'";

		getChildBillListPanel().QueryDataByCondition(str_condition); //刷新数据
		getChildBillListPanel().setItemEditable(true);

		hiddenAllSysButtons(); //隐藏所有按钮!!!
		getSysButton(BTN_SAVE).setVisible(true); //
		getSysButton(BTN_SAVE_RETURN).setVisible(true); //
		getSysButton(BTN_RETURN).setVisible(true); //

		getSysBtnPanel().updateUI(); //
		switchToCard();
	}

	/**
	 * 保存按钮操作!!
	 */
	protected void onSave() {
		getChildBillListPanel().stopEditing();

		if (!getChildBillListPanel().checkValidate()) { //校验
			return;
		}

		//HashMap BillVOMap = new HashMap();
		try {
			BillVO[] insertvo = getChildBillListPanel().getInsertedBillVOs();
			BillVO[] updatevo = getChildBillListPanel().getUpdatedBillVOs();
			BillVO[] deletevo = getChildBillListPanel().getDeletedBillVOs();

			// 执行提交前拦截.
			if (this.uiinterceptor != null) {
				try {
					uiinterceptor.dealBeforeCommit(getChildBillListPanel(), insertvo, deletevo, updatevo);
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageBox.show(this, ex.getMessage(), WLTConstants.MESSAGE_WARN);
					return;
				}
			}

			//真正远程提交数据库!!!!
			//HashMap returnMap = getMetaService().style13_dealCommit(getChildBillListPanel().getDataSourceName(), getBsinterceptor(), insertvo, deletevo, updatevo); //真正远程访问!!

			getChildBillListPanel().clearDeleteBillVOs();
			for (int i = 0; i < getChildBillListPanel().getTable().getRowCount(); i++) {
				RowNumberItemVO itemvo = (RowNumberItemVO) getChildBillListPanel().getValueAt(i, "_RECORD_ROW_NUMBER");
				if (itemvo.getState().equals("UPDATE")) {
					if (getChildBillListPanel().containsItemKey("VERSION")) { //如果有版本字段!!
						int version = new Integer(getChildBillListPanel().getValueAt(i, "VERSION").toString()).intValue() + 1;
						getChildBillListPanel().setValueAt(new Integer(version).toString(), i, "VERSION");
					}
				}
			}
			//
			//			BillVO[] returnInsertVOs = (BillVO[]) returnMap.get("INSERT"); //...
			//			BillVO[] returnDeleteVOs = (BillVO[]) returnMap.get("DELETE"); //...
			//			BillVO[] returnUpdateVOs = (BillVO[]) returnMap.get("UPDATE"); //...

			// 执行提交后拦截.

			//getChildBillListPanel().stopEditing();
			getChildBillListPanel().setAllRowStatusAs("INIT");
			setInformation("Save Success"); //
			//MessageBox.show(this, WLTConstants.STRING_OPERATION_SUCCESS); //
		} catch (Exception e) {
			e.printStackTrace(); //
			MessageBox.showException(this, e); //
		}
	}

	protected void onSaveReturn() throws Exception {
		try {
			onSave();
			onReturn();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onReturn() {
		hiddenAllSysButtons(); //隐藏所有按钮!!!
		initSysBtnPanel(); //
		switchToList();
	}

	protected void onList() {
		int li_row = getParentBillListPanel().getSelectedRow();
		if (li_row < 0) {
			return;
		}

		BillVO billVO = getParentBillListPanel().getBillVO(li_row); ////
		String str_parentID = billVO.getStringValue(getParentAssocField()); ////
		String str_condition = getChildAssocField() + "='" + str_parentID + "'";
		getChildBillListPanel().QueryDataByCondition(str_condition); //
		getChildBillListPanel().setItemEditable(false); //

		hiddenAllSysButtons(); //隐藏所有按钮!!!
		getSysButton(BTN_RETURN).setVisible(true); //
		getSysBtnPanel().updateUI(); //
		switchToCard();
	}

	protected JPanel getChildCustomerJPanel() {
		if (childcustomerpanel != null)
			return childcustomerpanel;

		childcustomerpanel = new JPanel();
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		layout.setHgap(10);
		childcustomerpanel.setLayout(layout);

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

	protected void onChildInsert() {
		String str_parentpk = getParentBillListPanel().getRealValueAtModel(getParentBillListPanel().getSelectedRow(), getParentAssocField());
		int li_row = getChildBillListPanel().newRow(); //
		getChildBillListPanel().setValueAt(str_parentpk, li_row, getChildAssocField());

		//		// 执行子表新增 后拦截;
		//		if (uiinterceptor != null) {
		//			try {
		//				uiinterceptor.actionAfterInsert_child(getChildBillListPanel(), li_row);
		//			} catch (Exception e1) {
		//				MessageBox.show(this, e1.getMessage(), WLTConstants.MESSAGE_WARN);
		//				e1.printStackTrace();
		//				return;
		//			}
		//		}
	}

	protected void onChildDelete() {
		int li_selrow = getChildBillListPanel().getSelectedRow();
		if (li_selrow < 0) {
			return;
		}

		//		// 执行子表删除前拦截;
		//		if (uiinterceptor != null) {
		//			try {
		//				uiinterceptor.actionBeforeDelete_child(child_BillListPanel, li_selrow);
		//			} catch (Exception e1) {
		//				MessageBox.show(this, e1.getMessage(), WLTConstants.MESSAGE_WARN);
		//				e1.printStackTrace();
		//				return;
		//			}
		//		}
		getChildBillListPanel().removeRow();
	}

	public void onChildRefresh() {
		String str_parentpk = getParentBillListPanel().getRealValueAtModel(getParentBillListPanel().getSelectedRow(), getParentAssocField());
		getChildBillListPanel().QueryDataByCondition(getChildAssocField() + "='" + str_parentpk + "'"); //刷新子表数据!
	}

	/**
	 * 是否允许新增
	 */
	public boolean isCanInsert() {
		return true;
	}

	/**
	 * 是否允许删除
	 */
	public boolean isCanDelete() {
		return true;
	}

	/**
	 * 是否允许编辑操作
	 */
	public boolean isCanEdit() {
		return true;
	}

	/**
	 * 是否显示系统按扭栏
	 */
	public boolean isShowsystembutton() {
		return true;
	}

	/**
	 * 是否显示工作流按钮栏
	 */
	public boolean isCanWorkFlowDeal() {
		return false;
	}

	public boolean isCanWorkFlowMonitor() {
		return false;
	}

	public int getLeveltype() {
		return leveltype;
	}

}
