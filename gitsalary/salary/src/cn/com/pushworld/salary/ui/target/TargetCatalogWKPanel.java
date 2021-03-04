package cn.com.pushworld.salary.ui.target;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/***
 * 指标类型维护
 * @author Gwang
 * 2013-6-24 下午03:43:14
 */
public class TargetCatalogWKPanel extends AbstractWorkPanel implements ActionListener, BillTreeSelectListener{

	private BillTreePanel treePanel;
	private BillCardPanel cardPanel;
	private WLTButton btn_delete;

	@Override
	public void initialize() {
		treePanel = new BillTreePanel("SAL_TARGET_CATALOG_CODE1");
		btn_delete = new WLTButton("删除");
		btn_delete.addActionListener(this);
		WLTButton btn_edit = WLTButton.createButtonByType(WLTButton.TREE_EDIT);
		btn_edit.setText("修改");
		btn_edit.setToolTipText("修改");
		treePanel.addBatchBillTreeButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.TREE_INSERT), btn_edit, btn_delete });
		treePanel.repaintBillTreeButton();
		treePanel.queryDataByCondition(null);
		treePanel.addBillTreeSelectListener(this);
		cardPanel = new BillCardPanel(treePanel.getTempletVO());
		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, treePanel, cardPanel);
		this.add(splitPane);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_delete) {
			onDelete();
		}
	}

	private void onDelete() {
		if (treePanel.getSelectedNode() == null || !treePanel.getSelectedNode().isLeaf()) {
			MessageBox.show(this, "请选择一个末级结点进行删除操作!"); //
			return;
		}
		BillVO billVO = treePanel.getSelectedVO();
		try {
			String count = UIUtil.getStringValueByDS(null, "select count(id) from sal_target_list where catalogid=" + billVO.getStringValue("id"));
			if (!"0".equals(count)) {
				MessageBox.show(this, "请类型已经被使用,不能删除!"); //
				return;
			}

			if (!MessageBox.confirm(this, "您确定要删除该记录吗?")) {
				return;
			}
			UIUtil.executeUpdateByDS(null, "delete from " + treePanel.getTempletVO().getSavedtablename() + " where  id=" + billVO.getStringValue("id"));
			treePanel.delCurrNode(); //
			treePanel.updateUI();
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (_event.getCurrSelectedNode().isRoot() || _event.getCurrSelectedVO() == null) {
			cardPanel.clear();
			return;
		}
		BillVO billVO = _event.getCurrSelectedVO();
		cardPanel.queryDataByCondition("id=" + billVO.getStringValue("id"));
	}

}
