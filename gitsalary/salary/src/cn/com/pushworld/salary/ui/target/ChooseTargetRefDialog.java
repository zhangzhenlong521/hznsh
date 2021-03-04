package cn.com.pushworld.salary.ui.target;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * @author Gwang
 * 还用吗???
 * 2013-7-4 上午09:56:21
 */
public class ChooseTargetRefDialog extends AbstractRefDialog implements ActionListener, BillTreeSelectListener {

	private WLTButton btnConfirm; //确定按钮
	private WLTButton btnCancel; // 取消按钮
	private BillListPanel listPanel;
	private RefItemVO refItemVO;
	
	public ChooseTargetRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		this.refItemVO = refItemVO;
	}
	
	@Override
	public RefItemVO getReturnRefItemVO() {
		return refItemVO;
	}

	@Override
	public void initialize() {
		
//		this.getContentPane().setLayout(new BorderLayout());
//		this.setSize(800, 600);
//		btnConfirm = new WLTButton("确定");
//		btnCancel = new WLTButton("取消");
//		btnConfirm.addActionListener(this);
//		btnCancel.addActionListener(this);		
//		JPanel southPanel = new JPanel();
//		southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
//		southPanel.add(btnConfirm);
//		southPanel.add(btnCancel);
//		
//		treePanel = new BillTreePanel("SAL_TARGET_CATALOG_CODE1");
//		treePanel.setMoveUpDownBtnVisiable(false);
//		treePanel.queryDataByCondition(null);
//		treePanel.addBillTreeSelectListener(this);
//
//		listPanel = new BillListPanel("SAL_TARGET_LIST_CODE1");
//		listPanel.setItemVisible("type", false);
//		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, treePanel, listPanel);
//		splitPane.setDividerLocation(220);
//		
//		this.getContentPane().add(splitPane, BorderLayout.CENTER);
//		this.getContentPane().add(southPanel, BorderLayout.SOUTH);		
		this.add(new JLabel("cn.com.pushworld.salary.ui.target.ChooseTargetRefDialog还用吗?\n 不知道, 看不到我这句话就不要了吧..."));
	}


	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		if (_event.getCurrSelectedNode().isRoot() || _event.getCurrSelectedVO() == null) {
			listPanel.clearTable();
		} else {
			BillVO billVO = _event.getCurrSelectedVO();
			listPanel.QueryDataByCondition("catalogid=" + billVO.getStringValue("id"));
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if (btnConfirm == e.getSource()) {
			onConfirm(); // 点击确定
		} else if (btnCancel == e.getSource()) {
			onCancel(); // 点击取消
		}
	}
	
	
	/**
	 * 取消
	 */
	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL); //
		this.dispose();
	}

	/**
	 * 确定
	 */
	private void onConfirm() {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		
		//billcard.setValueAt("AUTHOR", new RefItemVO(_billvo.getStringValue("userid"), "", _billvo.getStringValue("username")));//
		
		// 返回参照
		HashVO hashvo = new HashVO();
		hashvo.setAttributeValue("id", new StringItemVO(billVO.getStringValue("id")));
		hashvo.setAttributeValue("code", new StringItemVO(""));
		hashvo.setAttributeValue("name", new StringItemVO(billVO.getStringValue("name")));
		refItemVO = new RefItemVO(hashvo); //

		this.setCloseType(BillDialog.CONFIRM); //
		this.dispose(); //
	}

}
