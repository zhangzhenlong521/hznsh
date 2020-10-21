package com.pushworld.ipushgrc.ui.icheck.p090;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;

/**
 * zzl[2017-01-19]
 * 
 * 昌吉客户提出计划选择了机构方案中只显示计划中选择的机构
 */
public class CheckRefDialog_Corp_Tree extends AbstractRefDialog implements
		ActionListener, BillTreeSelectListener {

	private BillFormatPanel billFormatPanel = null; //
	private BillTreePanel billTreePanel = null; //
	private BillListPanel billListPanel = null; //
	private BillListPanel billListPanel_add = null; //
	private BillTreePanel tree = null;
	private BillCardPanel billCardPanel = null;
	private WLTButton btn_confirm, btn_cancel, btn_adduser, btn_deleteuser;
	private RefItemVO returnRefItemVO = null; //
	private Container parent;
	private String itemKey;

	public CheckRefDialog_Corp_Tree(Container _parent, String _title,
			RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		billCardPanel = (BillCardPanel) panel;
		parent = _parent;
		CardCPanel_Ref ref = (CardCPanel_Ref) parent;
		itemKey = ref.getItemKey();
		BillCardPanel cardPanel = (BillCardPanel) panel;
	}

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		tree = new BillTreePanel("PUB_CORP_DEPT_CODE1");
		String id = billCardPanel.getBillVO().getStringValue("planid");
		String checkeddept = null;
		try {
			checkeddept = UIUtil.getStringValueByDS(null,
					"select checkeddept from CK_PLAN where id='" + id + "'");

			checkeddept = checkeddept.replace(";", ",");
			checkeddept = checkeddept.substring(1, checkeddept.length() - 1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tree.queryDataByCondition("id in("+checkeddept+")");
		tree.reSetTreeChecked(true);
		this.add(tree, BorderLayout.CENTER); //
		this.add(getSouthPanel(), BorderLayout.SOUTH); //

	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}
	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");

		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}


	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btn_confirm){
			BillVO [] billVO_list = tree.getCheckedVOs();
			if (billVO_list.length<0) {
				MessageBox.show(this, "请加入人员!"); //
				return;
			}
			StringBuilder userids = new StringBuilder(";");
			StringBuilder useridnames = new StringBuilder(";");
			for (int i = 0; i < billVO_list.length; i++) {
				HashVO hvo = billVO_list[i].convertToHashVO(); //
				userids.append(hvo.getStringValue("id") + ";");
				useridnames.append(hvo.getStringValue("name") + ";");
			}
			returnRefItemVO = new RefItemVO(); //
			returnRefItemVO.setId(userids.toString()); //
			returnRefItemVO.setCode(null);
			returnRefItemVO.setName(useridnames.toString()); //
			this.setCloseType(BillDialog.CONFIRM); //
			this.dispose(); //
		}else if(e.getSource()==btn_cancel){
			onCancel();
		}
		
		
	}
	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL); //
		this.dispose(); //
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent event) {
		// TODO Auto-generated method stub

	}

}
