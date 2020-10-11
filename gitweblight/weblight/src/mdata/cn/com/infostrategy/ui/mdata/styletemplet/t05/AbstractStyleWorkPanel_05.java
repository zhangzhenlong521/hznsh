/**************************************************************************
 * $RCSfile: AbstractStyleWorkPanel_05.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t05;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListEditEvent;
import cn.com.infostrategy.ui.mdata.BillListEditListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;

/**
 * 风格模板5,左边是一颗树,右边是一个表,但表格新增是像风格模板2一样是层显示方式
 * 与风格模板4很相似
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_05 extends AbstractStyleWorkPanel implements BillCardEditListener, BillListEditListener, BillTreeSelectListener {
	
	private BillTreePanel billTreePanel = null;
	private BillListPanel billListPanel = null; //表格!!!
	private BillCardPanel billCardPanel = null; //表格!!!

	private JPanel panel_allscreem = null;
	private CardLayout cardlayout = null;

	private IUIIntercept_05 uiIntercept = null; //ui端拦截器

	//树的配置
	public abstract String getTreeTempeltCode(); //树和模板编码.		

	public abstract String getTreeAssocField(); //树中与表格关联的字段名

	//表格配置
	public abstract String getTableTempletCode(); //表的模板编码.

	public abstract String getTableAssocField(); //表格中与树关联的字段

	/**
	 * 初始化页面
	 */
	public void initialize() {
		super.initialize(); //

		if (getUiinterceptor() != null && !getUiinterceptor().trim().equals("")) {
			try {
				uiIntercept = (IUIIntercept_05) Class.forName(getUiinterceptor().trim()).newInstance(); //
			} catch (Exception e) {
				e.printStackTrace(); //
			}
		}

		this.setLayout(new BorderLayout());
		Box box = Box.createVerticalBox();
		box.add(getBtnBarPanel());
		initSysBtnPanel();

		panel_allscreem = new JPanel();
		cardlayout = new CardLayout();
		panel_allscreem.setLayout(cardlayout);

		panel_allscreem.add(getBillListPanel(), "table"); //
		panel_allscreem.add(getBillCardPanel(), "card"); //

		JSplitPane splitPanel = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, getBillTreePanel(), panel_allscreem);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(box, BorderLayout.NORTH);
		panel.add(splitPanel, BorderLayout.CENTER);

		this.add(panel, BorderLayout.CENTER);
	}

	private void initSysBtnPanel() {
		hiddenAllSysButtons();
		if (isCanInsert()) {
			getSysButton(BTN_INSERT).setVisible(true); //
		}

		if (isCanDelete()) {
			getSysButton(BTN_DELETE).setVisible(true); //
		}

		if (isCanEdit()) {
			getSysButton(BTN_EDIT).setVisible(true); //
		}

		if (isCanInsert() || isCanDelete() || isCanEdit()) {
			getSysButton(BTN_SAVE).setVisible(true); //
		}

		getSysButton(BTN_LIST).setVisible(true); //查看
	}

	public void onBillCardValueChanged(BillCardEditEvent _evt) {

	}

	public void onBillListValueChanged(BillListEditEvent _evt) {

	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		BillVO billVO = _event.getCurrSelectedVO();
		if (billVO == null) {
			return;
		}
		String str_tree_value = ((StringItemVO) billVO.getObject(getTreeAssocField())).getStringValue(); //
		String str_condition = getTableAssocField() + "='" + str_tree_value + "'";
		getBillListPanel().QueryDataByCondition(str_condition); //
	}

	protected void onInsert() {
		try {
			BillVO billVO = getBillTreePanel().getSelectedVO(); //
			if (billVO == null) {
				return;
			}

			hiddenAllSysButtons();
			getSysButton(BTN_SAVE).setVisible(true); //保存
			getSysButton(BTN_SAVE_RETURN).setVisible(true); //保存返回
			getSysButton(BTN_RETURN).setVisible(true); //返回

			StringItemVO str_accovalue = (StringItemVO) billVO.getObject(getTreeAssocField()); //
			getBillCardPanel().insertRow(); //
			getBillCardPanel().setValueAt(getTableAssocField(), str_accovalue);
			getBillCardPanel().setEditableByInsertInit(); //
			switchToCard();
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	protected void onSave() throws Exception {
		try {
			getBillCardPanel().updateData();
		} catch (Exception e) {
			throw (e);
		} //
	}

	protected void onDelete() {

	};

	protected void onSaveReturn() {
		try {
			onSave();
			onReturn(); //
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 返回..
	 */
	protected void onReturn() {
		initSysBtnPanel(); //
		switchToTable(); //
	}

	/**
	 * 查看
	 */
	protected void onList() {

	}

	/**
	 * 获取BillListPanel..
	 * @return BillListPanel
	 */
	public BillListPanel getBillListPanel() {
		if (billListPanel == null) {
			billListPanel = new BillListPanel(getTableTempletCode());
			billListPanel.addBillListEditListener(this);
			billListPanel.getQuickQueryPanel().setVisible(false); //隐藏查询框
		}
		return billListPanel;
	}

	/**
	 * 获取BillListPanel..
	 * @return BillListPanel
	 */
	public BillCardPanel getBillCardPanel() {
		if (billCardPanel == null) {
			billCardPanel = new BillCardPanel(getTableTempletCode());
			billCardPanel.addBillCardEditListener(this); //
		}
		return billCardPanel;
	}

	public BillTreePanel getBillTreePanel() {
		if (billTreePanel == null) {
			billTreePanel = new BillTreePanel(getTreeTempeltCode());
			billTreePanel.addBillTreeSelectListener(this); //
			billTreePanel.queryDataByCondition("1=1"); //
		}
		return billTreePanel;
	}

	private void switchToCard() {
		cardlayout.show(panel_allscreem, "card"); //
	}

	private void switchToTable() {
		cardlayout.show(panel_allscreem, "table"); //
	}

	public boolean isCanDelete() {
		return true;
	}

	public boolean isCanEdit() {
		return false;
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

	public boolean isShowsystembutton() {
		return true;
	}

}
