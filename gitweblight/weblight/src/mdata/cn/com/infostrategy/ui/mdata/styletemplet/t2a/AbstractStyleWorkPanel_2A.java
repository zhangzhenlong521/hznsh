package cn.com.infostrategy.ui.mdata.styletemplet.t2a;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;

/**
 * 风格模板11,即两张表,是一对一关系(即一条记录绑定一条记录),一张主表,一张子表,进去时显示主表列表,然后选择一条记录，点击新增按钮出列子表卡片页面.
 * 操作完成后回到主表页面,即主表是列表，子表是卡片
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_2A extends AbstractStyleWorkPanel {

	private static final long serialVersionUID = -8841608749741379946L;

	private BillListPanel parentBillListPanel = null; //主表列表面板

	private BillCardPanel childBillCardPanel = null; //子表卡片面板

	private CardLayout cardLayout = null; //

	private JPanel toftPanel = null; //装载CardLayout的面板..

	protected abstract String getParentTempletCode(); //取得主表模板编码

	protected abstract String getParentAssocField(); //取得主表关联字段

	protected abstract String getChildTempletCode(); //取得子表模板编码

	protected abstract String getChildAssocField(); //取得子表关联字段

	private int leveltype = WLTConstants.LEVELTYPE_LIST; //

	Pub_Templet_1VO parentTempletVO = null;
	Pub_Templet_1VO childTempletVO = null;

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
			toftPanel.add("list", getParentBillListPanel()); //
			toftPanel.add("card", getChildBillCardPanel()); //
			this.add(toftPanel, BorderLayout.CENTER);
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 初始化系统按钮拦.
	 */
	private void initSysBtnPanel() {
		hiddenAllSysButtons(); //隐藏所有按钮!!!
		if (isCanInsert()) {
			getSysButton(BTN_INSERT).setVisible(true);
		}

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
	public BillCardPanel getChildBillCardPanel() {
		if (childBillCardPanel == null) {
			childBillCardPanel = new BillCardPanel(childTempletVO); //
		}
		return childBillCardPanel;
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
		if (getLeveltype() == WLTConstants.LEVELTYPE_LIST) { //如果是列表状态
			return getParentBillListPanel().getSelectedBillVO();
		} else if (getLeveltype() == WLTConstants.LEVELTYPE_CARD) { //如果是卡片状态
			return getChildBillCardPanel().getBillVO(); //
		} else {
			return null;
		}
	}

	public void writeBackWFPrinstance(BillVO _billVO) {
		getChildBillCardPanel().setRealValueAt("wfprinstanceid", _billVO.getStringValue("wfprinstanceid")); //向页面回写流程后生成的主键
	}

	protected void onInsert() {
		try {
			int li_row = getParentBillListPanel().getSelectedRow();
			if (li_row < 0) {
				return;
			}

			BillVO billVO = getParentBillListPanel().getBillVO(li_row); ////
			String str_parentID = billVO.getStringValue(getParentAssocField()); ////

			//		String str_condition = getChildAssocField() + "='" + str_parentID + "'";
			//		String str_sql = "select 1 from " + getChildBillCardPanel().getTempletVO().getTablename() + " where " + str_condition;
			//		try {
			//			String[][] str_data = UIUtil.getStringArrayByDS(getChildBillCardPanel().getTempletVO().getDatasourcename(), str_sql);
			//			if (str_data != null && str_data.length > 0) {
			//				MessageBox.show(this, "已经有绑定的数据，不能再新增了!");
			//				return;
			//			}
			//		} catch (Exception e) {
			//			e.printStackTrace();
			//		} //

			getChildBillCardPanel().insertRow(); ////
			getChildBillCardPanel().setCompentObjectValue(getChildAssocField(), new StringItemVO(str_parentID)); //

			hiddenAllSysButtons(); //隐藏所有按钮!!!
			getSysButton(BTN_SAVE).setVisible(true); //
			getSysButton(BTN_SAVE_RETURN).setVisible(true); //
			getSysButton(BTN_RETURN).setVisible(true); //

			if (isCanWorkFlowDeal()) {
				if (getChildBillCardPanel().containsItemKey("WFPRINSTANCEID")) {
					refreshWorkFlowPanel(getChildBillCardPanel().getRealValueAt("WFPRINSTANCEID")); //
				}
			}

			getChildBillCardPanel().setEditableByInsertInit(); //
			getSysBtnPanel().updateUI(); //

			switchToCard();
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	/**
	 * 
	 */
	protected void onDelete() {
		try {
			int li_row = getParentBillListPanel().getSelectedRow(); //
			BillVO billVO = getParentBillListPanel().getSelectedBillVO(); //
			if (billVO == null) {
				MessageBox.show(this, "Please select a order."); //
				return;
			}

			String str_tablename = billVO.getSaveTableName(); //
			String str_pkfieldname = billVO.getPkName(); //
			String str_pkvalue = billVO.getPkValue(); //

			String str_sql = "delete from " + str_tablename + " where " + str_pkfieldname + "='" + str_pkvalue + "'";
			UIUtil.executeUpdateByDS(null, str_sql); //
			getParentBillListPanel().removeRow(li_row); //
			MessageBox.show(this, "Delete Success!"); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	protected void onEdit() {
		int li_row = getParentBillListPanel().getSelectedRow();
		if (li_row < 0) {
			return;
		}

		BillVO billVO = getParentBillListPanel().getBillVO(li_row); ////
		String str_parentID = billVO.getStringValue(getParentAssocField()); ////
		String str_condition = getChildAssocField() + "='" + str_parentID + "'";
		getChildBillCardPanel().queryDataByCondition(str_condition); //
		getChildBillCardPanel().setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		getChildBillCardPanel().setEditableByEditInit(); //

		hiddenAllSysButtons(); //隐藏所有按钮!!!
		getSysButton(BTN_SAVE).setVisible(true); //
		getSysButton(BTN_SAVE_RETURN).setVisible(true); //
		getSysButton(BTN_RETURN).setVisible(true); //
		getSysButton(BTN_PRINT).setVisible(true); //

		if (isCanWorkFlowDeal()) {
			if (getChildBillCardPanel().containsItemKey("WFPRINSTANCEID")) {
				refreshWorkFlowPanel(getChildBillCardPanel().getRealValueAt("WFPRINSTANCEID")); //
			}
		}

		getSysBtnPanel().updateUI(); //

		switchToCard();
	}

	protected void onSave() throws Exception {
		try {
			getChildBillCardPanel().updateData();
			MessageBox.show(this, "Save Data Success!"); //
		} catch (Exception e) {
			e.printStackTrace();
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
		getChildBillCardPanel().queryDataByCondition(str_condition); //
		getChildBillCardPanel().setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); //

		hiddenAllSysButtons(); //隐藏所有按钮!!!
		getSysButton(BTN_RETURN).setVisible(true); //
		getSysButton(BTN_PRINT).setVisible(true); //
		getSysBtnPanel().updateUI(); //

		switchToCard();
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
