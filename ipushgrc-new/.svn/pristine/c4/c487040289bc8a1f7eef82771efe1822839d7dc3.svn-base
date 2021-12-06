package com.pushworld.ipushlbs.ui.powermanage.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;
/**
 * 引用申请授权按钮
 * @author yinliang
 * @since  2011.12.15
 */
public class Btn_ApplyActionListener implements WLTActionListener{
	BillCardPanel cardPanel = null;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		onApply();
	}
	//引用操作
	private void onApply(){
		//加载审批通过的，未发布的 授权申请
		final BillListDialog dialog = new BillListDialog(cardPanel,"","LBS_POWERAPPLY_CODE2");
		dialog.getBtn_confirm().addActionListener(new ActionListener(){
			// 改变[确定]按钮操作,必须放在dialog.setVisible(true)前。。。
			public void actionPerformed(ActionEvent e) {
				BillListPanel panel = dialog.getBilllistPanel();
				BillVO billvo = panel.getSelectedBillVO(); //取得选中的列
				dialog.closeMe();
				// 将 授权申请中已有的信息添加到增加的授权管理中
				cardPanel.setValueAt("REFAPPLY", new StringItemVO(billvo.getStringValue("ID"))); // 将引用的哪一条申请赋值
				cardPanel.setValueAt("ACCEPTER", billvo.getRefItemVOValue("APPLIER")); //受权机构负责人，申请人
				cardPanel.setValueAt("ACCEPTDEPT", billvo.getRefItemVOValue("APPLYDEPT")); //受权机构，申请机构,
				cardPanel.setValueAt("TYPE", billvo.getComBoxItemVOValue("POWERTYPE")); //授权类型,授权类型				
				cardPanel.setValueAt("AUTHORDATE", billvo.getRefItemVOValue("BTIME")); //授权日期
				cardPanel.setValueAt("ENDDATE", billvo.getRefItemVOValue("ENDTIME")); //授权到期日期
				cardPanel.setValueAt("DOCUMENT",billvo.getRefItemVOValue("ADJUNT")); //授权书，附件		
			}
			
		});
		dialog.setVisible(true);
	}
}
