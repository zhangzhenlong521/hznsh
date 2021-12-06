package com.pushworld.ipushgrc.ui.cmpevent.p010;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

/**
 * 点击 浏览人员 按钮
 * 
 * @author hm
 * 
 */
public class Btn_ShowUserActionListener implements WLTActionListener {
	private BillCardPanel cardPanel = null;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		if (cardPanel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INIT)) {
			MessageBox.show(cardPanel, "浏览状态不允许执行此操作！");
			return;
		}
		onShowUser();
	}

	/**
	 * 点击 浏览人员
	 */
	public void onShowUser() {
		try {
			String str_rolecode = new TBUtil().getSysOptionStringValue("风险评估员角色名", "风险评估员"); //
			String str_roleid = UIUtil.getStringValueByDS(null, "select id from pub_role where code='" + str_rolecode + "'"); //
			if (str_roleid == null) {
				MessageBox.show(cardPanel, "系统需要一个编码为[" + str_rolecode + "]的角色,请联系系统管理员!"); //
				return; //
			}
			BillVO vo  = cardPanel.getBillVO();
			String cmpfileids = vo.getStringValue("cmp_cmpfile_id");
			String blcorpid = null;
			if(cmpfileids!=null && !cmpfileids.equals("")){
				blcorpid = UIUtil.getStringValueByDS(null, "select blcorpid from cmp_cmpfile where id = "+ cmpfileids);
			}else{
				MessageBox.show(cardPanel, "请先选择一个流程文件");
				return;
			}
			BillListDialog listDialog = new BillListDialog(cardPanel, "浏览人员", "PUB_USER_CODE1"); // //
			String sqlCondition = "id in (select userid from pub_user_role where roleid= '" + str_roleid + "' and userdept = "+ blcorpid +" )";
			listDialog.getBilllistPanel().QueryDataByCondition(sqlCondition);
			listDialog.getBilllistPanel().setDataFilterCustCondition(sqlCondition);
			listDialog.getBtn_confirm().setVisible(false);
			listDialog.setVisible(true);
		} catch (Exception _ex) {
			MessageBox.showException(cardPanel, _ex); // //
		}
	}
}
