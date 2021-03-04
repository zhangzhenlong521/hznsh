package cn.com.pushworld.salary.ui.baseinfo.p050;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 选择人员, 返回人员, 并设置父亲卡片上的岗位和部门
 * @author Gwang
 * 2013-7-16 下午02:57:47
 */
public class ChooseUserRefDialog extends AbstractRefDialog implements ActionListener, BillTreeSelectListener {

	private static final long serialVersionUID = 1L;
	private BillCardPanel parentCardPanel;
	private BillTreePanel billTreePanel;
	private BillListPanel billListPanel;
	private WLTButton btnConfirm, btnCanel;
	private RefItemVO returnRefItemVO = null;
	
	/**
	 * @param parent
	 * @param title
	 * @param initRefItemVO
	 * @param billPanel
	 */
	public ChooseUserRefDialog(Container parent, String title, RefItemVO initRefItemVO, BillPanel billPanel) {
		super(parent, title, initRefItemVO, billPanel);
		this.parentCardPanel = (BillCardPanel)billPanel;
	}
	

	/* (non-Javadoc)
	 * @see cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog#getReturnRefItemVO()
	 */
	@Override
	public RefItemVO getReturnRefItemVO() {
		return this.returnRefItemVO;
	}

	/* (non-Javadoc)
	 * @see cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog#initialize()
	 */
	@Override
	public void initialize() {
		billTreePanel = new BillTreePanel("PUB_CORP_DEPT_CODE1");
		billTreePanel.queryDataByCondition("1=1");
		billTreePanel.addBillTreeSelectListener(this);		
		
		billListPanel = new BillListPanel("PUB_USER_POST_DEFAULT");
		billListPanel.setQuickQueryPanelVisiable(false);
		billListPanel.getBillListBtnPanel().setVisible(true);
		
		WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel, billListPanel);
		this.add(splitPanel);
		this.add(getSouthPanel(), BorderLayout.SOUTH);
	}
	
	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
		btnConfirm = new WLTButton("确定");
		btnCanel = new WLTButton("取消");
		btnConfirm.addActionListener(this);
		btnCanel.addActionListener(this);
		panel.add(btnConfirm);
		panel.add(btnCanel);
		return panel;
	}
	
	/* (non-Javadoc)
	 * @see cn.com.infostrategy.ui.mdata.BillTreeSelectListener#onBillTreeSelectChanged(cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent)
	 */
	public void onBillTreeSelectChanged(BillTreeSelectionEvent event) {
		BillVO billVO = billTreePanel.getSelectedVO();
		if (billVO != null) {
			billListPanel.QueryDataByCondition(" deptid = '" + billVO.getStringValue("id") + "'");
		}		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnConfirm) {
			onConfirm();
		}else if (e.getSource() == btnCanel) {
			onCanel();
		}		
	}

	/**
	 * 确认返回
	 */
	private void onConfirm() {

		BillVO listVO = billListPanel.getSelectedBillVO();		
		if (listVO == null) {
			MessageBox.show(this, "请选择一个人员!");
			return;
		}
				
		//设置返回对象
		String userID = listVO.getStringValue("userid");
		String userCode = listVO.getStringValue("usercode");
		String userName = listVO.getStringValue("username");
		returnRefItemVO = new RefItemVO(userID, userCode, userName);
		
		//设置父亲卡片上的值
		parentCardPanel.setValueAt("username", new StringItemVO(userName));
		parentCardPanel.setValueAt("postid", new StringItemVO(listVO.getStringValue("postid")));
		parentCardPanel.setValueAt("postname", new StringItemVO(listVO.getStringValue("postname")));
		parentCardPanel.setValueAt("deptid", new StringItemVO(listVO.getStringValue("deptid")));
		parentCardPanel.setValueAt("deptname", new StringItemVO(listVO.getStringValue("deptname")));
		
		this.setCloseType(BillDialog.CONFIRM);
		this.dispose();
		
	}
	

	/**
	 * 取消
	 */
	private void onCanel() {
		this.setCloseType(BillDialog.CANCEL);
		this.dispose();		
	}







}
