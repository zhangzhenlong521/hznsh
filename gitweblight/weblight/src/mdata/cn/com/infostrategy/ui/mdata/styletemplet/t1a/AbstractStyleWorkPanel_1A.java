package cn.com.infostrategy.ui.mdata.styletemplet.t1a;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListEditListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;

/**
 * 处理一个实体! 但搞成两个页签,第一个签是卡片,第二个页签是列表!!!上来先是卡片状态! 然后可以在列表页签中查询!!
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_1A extends AbstractStyleWorkPanel implements BillCardEditListener, BillListEditListener {

	private static final long serialVersionUID = 8348548020808291175L;

	private BillCardPanel billCardPanel = null; //

	private BillListPanel billListPanel = null; //

	private IUIIntercept_1A uiIntercept = null; //

	protected abstract String getTempletCodeCard();

	protected abstract String getTempletCodeList();

	public void initialize() {
		super.initialize(); //
		/**
		 * 创建UI端拦截器
		 */
		if (getUiinterceptor() != null && !getUiinterceptor().trim().equals("")) {
			try {
				uiIntercept = (IUIIntercept_1A) Class.forName(getUiinterceptor().trim()).newInstance(); //
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.setLayout(new BorderLayout()); //

		JPanel jpanelFirst = new JPanel(new BorderLayout());
		jpanelFirst.add(getBillCardPanel(), BorderLayout.CENTER); //
		jpanelFirst.add(getBtnBarPanel(), BorderLayout.NORTH); //

		JPanel jpanelSecond = new JPanel(new BorderLayout());

		JPanel btnBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		jpanelSecond.add(btnBarPanel, BorderLayout.NORTH);
		jpanelSecond.add(getBillListPanel(), BorderLayout.CENTER);

		JTabbedPane jTabbedPane = new JTabbedPane();

		jTabbedPane.add("详细", jpanelFirst);
		jTabbedPane.add("列表", jpanelSecond);

		this.add(jTabbedPane, BorderLayout.CENTER); //
		initsysbuton();
		try {
			getBillCardPanel().insertRow(); //
			getBillCardPanel().setEditableByInsertInit(); //

		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	public BillCardPanel getBillCardPanel() {
		if (billCardPanel == null) {
			billCardPanel = new BillCardPanel(getTempletCodeCard()); //"ESS_leavewithdraw_CODE1"
			billCardPanel.addBillCardEditListener(this); //
		}
		return billCardPanel;
	}

	public BillListPanel getBillListPanel() {
		if (billListPanel == null) {
			billListPanel = new BillListPanel(getTempletCodeList()); //"ESS_leavewithdraw_CODE1"
			billListPanel.addBillListEditListener(this); //
		}
		return billListPanel;
	}

	/**
	 * 初始化按钮
	 */
	private void initsysbuton() {
		getSysButton(BTN_INSERT).setVisible(true); //
		getSysButton(BTN_SAVE).setVisible(true); //
		getSysButton(BTN_WORKFLOW_SUBMIT).setVisible(true); //新增流程时只有提交按钮!!!
	}

	public void onInsert() {
		try {
			initsysbuton();
			getBillCardPanel().insertRow(); //
			getBillCardPanel().setEditableByInsertInit(); //
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	public void onSave() {
		if (billCardPanel.checkValidate()) {
			try {
				dealSave(); //
				MessageBox.show(this, UIUtil.getLanguage("保存成功!"));
			} catch (Exception e) {
				MessageBox.showWarn(this, e.getMessage());
				e.printStackTrace();
			} //
		}

	}

	public void dealSave() throws Exception {
		BillVO billVO = getBillCardPanel().getBillVO();
		if (getBillCardPanel().getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
			if (uiIntercept != null) {
				uiIntercept.beforeInsert(getBillCardPanel(), billVO); //新增前处理,可以有机会修改一下BillVO
			}
			getBillCardPanel().setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
		} else if (getBillCardPanel().getEditState().equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
			if (uiIntercept != null) {
				uiIntercept.beforeUpdate(getBillCardPanel(), billVO); //
			}
		}
	}

	/**
	 * 取得工作流处理的BillVO
	 * @return
	 */
	public BillVO getWorkFlowDealBillVO() {
		return getBillCardPanel().getBillVO(); //
	}

	/**
	 * 向页面回写流程实例
	 * @param _prInstanceId
	 */
	public void writeBackWFPrinstance(BillVO _billVO) {
		getBillCardPanel().setRealValueAt("wfprinstanceid", _billVO.getStringValue("wfprinstanceid")); //向页面回写流程后生成的主键
	}

	public void onBillCardValueChanged(BillCardEditEvent _evt) {

	}

	public boolean isCanDelete() {
		return false;
	}

	public boolean isCanEdit() {
		return false;
	}

	public boolean isCanInsert() {
		return false;
	}

	public boolean isCanWorkFlowDeal() {
		return false;
	}

	public boolean isCanWorkFlowMonitor() {
		return false;
	}

	public boolean isShowsystembutton() {
		return true;
	}

}
