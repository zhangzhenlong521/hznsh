package cn.com.infostrategy.ui.mdata.styletemplet.t06;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JSplitPane;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryEvent;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * 风格模板6的抽象类!!!!模板6是两个表，其中编辑修改第二张表
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_06 extends AbstractWorkPanel implements BillListSelectListener, ActionListener, BillListAfterQueryListener {

	protected BillListPanel parentBillListPanel = null; // 主表,入口表,左表
	protected BillListPanel childBilllListPanel = null; // 子表,关联表,处理表,右表
	private WLTButton btn_insert, btn_update, btn_delete, btn_list; //增,删,改

	public abstract String getParentTempletCode(); //

	public abstract String getParentAssocField();

	public abstract String getChildTempletCode(); //

	public abstract String getChildAssocField();

	/**
	 *  默认是左右分隔!
	 * @return
	 */
	public int getOrientation() {
		return JSplitPane.VERTICAL_SPLIT;
	}

	public boolean isShowsystembutton() {
		return true;
	}

	public String getCustomerpanel() {
		return null;
	}

	public void initialize() {
		try {
			parentBillListPanel = new BillListPanel(getParentTempletCode()); //主表
			parentBillListPanel.setItemEditable(false); //
			parentBillListPanel.addBillListSelectListener(this); //
			parentBillListPanel.addBillListAfterQueryListener(this); //

			childBilllListPanel = new BillListPanel(getChildTempletCode()); //
			btn_insert = new WLTButton("新增"); //
			btn_insert.addActionListener(this); //
			btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //修改
			btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //删除
			btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //浏览
			childBilllListPanel.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_list }); //
			childBilllListPanel.repaintBillListButton(); //刷新按钮

			JSplitPane splitPanel = new WLTSplitPane(getOrientation(), parentBillListPanel, childBilllListPanel);
			splitPanel.setDividerLocation(250);
			splitPanel.setOneTouchExpandable(true);

			this.setLayout(new BorderLayout()); //
			this.add(splitPanel, BorderLayout.CENTER); //

			afterInitialize(); //后续初始化
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void afterInitialize() throws Exception {
	}

	/**
	 * 列表选择变化!!!
	 */
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO selVO = _event.getCurrSelectedVO(); //选用的数据!!
		if (selVO == null) {
			return;
		}
		String str_parentidValue = selVO.getStringValue(getParentAssocField()); //
		childBilllListPanel.QueryDataByCondition(getChildAssocField() + "='" + str_parentidValue + "'"); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_insert) {
			onInsert(); //
		}
	}

	public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
		childBilllListPanel.clearTable(); //
	}

	/**
	 * 获取子表的BillListPanel
	 * 
	 * @return BillListPanel
	 */
	public BillListPanel getChildBillListPanel() {
		return childBilllListPanel;
	}

	/**
	 * 获取主表的BillListPanel
	 * 
	 * @return BillListPanel
	 */
	public BillListPanel getParentBillListPanel() {
		return parentBillListPanel;
	}

	protected void onInsert() {
		BillVO billVO = parentBillListPanel.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请选择[" + parentBillListPanel.getTempletVO().getTempletname() + "]的一条记录进行此操作!"); //
			return; //
		}
		HashMap defaultValueMap = new HashMap(); //
		defaultValueMap.put(getChildAssocField(), billVO.getStringValue(getParentAssocField())); ////
		childBilllListPanel.doInsert(defaultValueMap); //做插入操作!!
	}

}
