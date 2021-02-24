/**************************************************************************
 * $RCSfile: AbstractStyleWorkPanel_04.java,v $  $Revision: 1.7 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t04;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * 风格模板,左边中树,右边是列表
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_04 extends AbstractWorkPanel implements ActionListener, BillTreeSelectListener {

	private BillTreePanel billTreePanel = null; //树型模板!
	private BillListPanel billListPanel = null; //表格!!!

	private WLTButton btn_insert, btn_update, btn_delete; //增,删,改
	private TBUtil tbUtil = new TBUtil(); //

	public abstract String getTreeTempeltCode(); //树和模板编码.

	public abstract String getTableTempletCode(); //表的模板编码.

	public abstract String getTreeAssocField(); //树中与表格关联的字段名

	public abstract String getTableAssocField(); //表格中与树关联的字段

	public abstract String getCustAfterInitClass(); //后初始化类!!

	public AbstractStyleWorkPanel_04() {
		super(); //
	}

	/**
	 * 构造方法!!!
	 * @param _parMap
	 */
	public AbstractStyleWorkPanel_04(HashMap _parMap) {
		super(_parMap); //
	}

	/**
	 * 初始化页面
	 */
	public void initialize() {
		billTreePanel = new BillTreePanel(getTreeTempeltCode()); //机构树,PUB_CORP_DEPT_1
		billTreePanel.setMoveUpDownBtnVisiable(false); //隐藏!
		billTreePanel.queryDataByCondition(null); //
		billTreePanel.addBillTreeSelectListener(this); //刷新事件监听!!

		billListPanel = new BillListPanel(getTableTempletCode()); //部门岗责表!CMP_DEPTDUTY_CODE1"
		btn_insert = new WLTButton("新增"); //
		btn_insert.addActionListener(this); //

		btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //修改
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //删除

		if (isCanEdit()) {
			billListPanel.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete }); //批量设置按钮!!!
			billListPanel.repaintBillListButton(); //
		}

		WLTSplitPane split = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTreePanel, billListPanel); //

		this.setLayout(new BorderLayout()); //
		this.add(split); //

		if (getCustAfterInitClass() != null) { //如果不为空!!!
			try {
				IAfterInit_StyleWorkPanel_04 itcpt = (IAfterInit_StyleWorkPanel_04) (Class.forName(getCustAfterInitClass()).newInstance()); //
				itcpt.afterInitialize(this); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_insert) {
			onInsert(); //
		}
	}

	/**
	 * 新增!!
	 */
	private void onInsert() {
		BillVO billVO = billTreePanel.getSelectedVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请选择左边树中一条记录进行此操作!"); //
			return;
		}
		HashMap defaultValueMap = new HashMap(); //
		defaultValueMap.put(getTableAssocField(), billVO.getStringValue(getTreeAssocField())); //
		billListPanel.doInsert(defaultValueMap); //
	}

	/**
	 * 树型选择变化!!
	 */
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		billListPanel.clearTable(); //先清空数据!!!
		BillVO billVO = _event.getCurrSelectedVO(); //
		if (billVO == null) {
			return;
		}
		String str_treepkvalue = billVO.getStringValue(getTreeAssocField()); //机构树的id.
		billListPanel.QueryDataByCondition(getTableAssocField() + "='" + tbUtil.getNullCondition(str_treepkvalue) + "'"); //
	}

	public BillTreePanel getBillTreePanel() {
		return billTreePanel;
	}

	public BillListPanel getBillListPanel() {
		return billListPanel;
	}

	//是否可编辑!!!默认是
	public boolean isCanEdit() {
		return true;
	}

}
