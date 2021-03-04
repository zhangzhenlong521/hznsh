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
 * ������???
 * 2013-7-4 ����09:56:21
 */
public class ChooseTargetRefDialog extends AbstractRefDialog implements ActionListener, BillTreeSelectListener {

	private WLTButton btnConfirm; //ȷ����ť
	private WLTButton btnCancel; // ȡ����ť
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
//		btnConfirm = new WLTButton("ȷ��");
//		btnCancel = new WLTButton("ȡ��");
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
		this.add(new JLabel("cn.com.pushworld.salary.ui.target.ChooseTargetRefDialog������?\n ��֪��, ����������仰�Ͳ�Ҫ�˰�..."));
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
			onConfirm(); // ���ȷ��
		} else if (btnCancel == e.getSource()) {
			onCancel(); // ���ȡ��
		}
	}
	
	
	/**
	 * ȡ��
	 */
	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL); //
		this.dispose();
	}

	/**
	 * ȷ��
	 */
	private void onConfirm() {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		
		//billcard.setValueAt("AUTHOR", new RefItemVO(_billvo.getStringValue("userid"), "", _billvo.getStringValue("username")));//
		
		// ���ز���
		HashVO hashvo = new HashVO();
		hashvo.setAttributeValue("id", new StringItemVO(billVO.getStringValue("id")));
		hashvo.setAttributeValue("code", new StringItemVO(""));
		hashvo.setAttributeValue("name", new StringItemVO(billVO.getStringValue("name")));
		refItemVO = new RefItemVO(hashvo); //

		this.setCloseType(BillDialog.CONFIRM); //
		this.dispose(); //
	}

}
