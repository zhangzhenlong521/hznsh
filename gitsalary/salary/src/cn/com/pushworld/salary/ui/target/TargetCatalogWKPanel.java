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
 * ָ������ά��
 * @author Gwang
 * 2013-6-24 ����03:43:14
 */
public class TargetCatalogWKPanel extends AbstractWorkPanel implements ActionListener, BillTreeSelectListener{

	private BillTreePanel treePanel;
	private BillCardPanel cardPanel;
	private WLTButton btn_delete;

	@Override
	public void initialize() {
		treePanel = new BillTreePanel("SAL_TARGET_CATALOG_CODE1");
		btn_delete = new WLTButton("ɾ��");
		btn_delete.addActionListener(this);
		WLTButton btn_edit = WLTButton.createButtonByType(WLTButton.TREE_EDIT);
		btn_edit.setText("�޸�");
		btn_edit.setToolTipText("�޸�");
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
			MessageBox.show(this, "��ѡ��һ��ĩ��������ɾ������!"); //
			return;
		}
		BillVO billVO = treePanel.getSelectedVO();
		try {
			String count = UIUtil.getStringValueByDS(null, "select count(id) from sal_target_list where catalogid=" + billVO.getStringValue("id"));
			if (!"0".equals(count)) {
				MessageBox.show(this, "�������Ѿ���ʹ��,����ɾ��!"); //
				return;
			}

			if (!MessageBox.confirm(this, "��ȷ��Ҫɾ���ü�¼��?")) {
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
