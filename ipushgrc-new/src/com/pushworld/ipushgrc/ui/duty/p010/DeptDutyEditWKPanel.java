package com.pushworld.ipushgrc.ui.duty.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.t04.DefaultStyleWorkPanel_04;
import cn.com.infostrategy.ui.sysapp.corpdept.SeqListDialog;

/**
 * 部门岗职维护!! 左边是机构树! 右边是机构的职责表[cmp_deptduty]
 * @author xch
 *
 */
public class DeptDutyEditWKPanel extends AbstractWorkPanel implements ActionListener {
	BillListPanel dutyList = null;

	@Override
	public void initialize() {
		HashMap parMap = new HashMap(); //
		parMap.put("$TreeTempletCode", "PUB_CORP_DEPT_1"); //树型模板
		parMap.put("$TableTempletCode", "CMP_DEPTDUTY_CODE1"); //表型模板!!
		parMap.put("$TreeJoinField", "id"); //
		parMap.put("$TableJoinField", "deptid"); //		
		parMap.put("是否可编辑", isCanEdit() ? "Y" : "N"); //		

		DefaultStyleWorkPanel_04 panel = new DefaultStyleWorkPanel_04(parMap); //
		panel.initialize(); //
		if (isCanEdit()) {
			WLTButton btn_seq = new WLTButton("排序");//在编辑界面增加职责排序按钮【李春娟/2014-12-16】
			btn_seq.addActionListener(this);
			dutyList = panel.getBillListPanel();
			dutyList.addBillListButton(btn_seq);
			dutyList.repaintBillListButton();
		}
		this.add(panel); //
	}

	/**
	 * 职责排序【李春娟/2014-12-16】
	 */
	private void onSeqDuty() {
		SeqListDialog dialog_post = new SeqListDialog(this, "职责排序", dutyList.getTempletVO(), dutyList.getAllBillVOs());
		dialog_post.setVisible(true);
		if (dialog_post.getCloseType() == 1) {//如果点击确定返回，则需要刷新一下页面
			dutyList.refreshData(); //
		}
	}

	public boolean isCanEdit() {
		return true;
	}

	public void actionPerformed(ActionEvent e) {
		onSeqDuty();
	}
}
