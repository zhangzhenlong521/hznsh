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
 * ��� �����Ա ��ť
 * 
 * @author hm
 * 
 */
public class Btn_ShowUserActionListener implements WLTActionListener {
	private BillCardPanel cardPanel = null;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		if (cardPanel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INIT)) {
			MessageBox.show(cardPanel, "���״̬������ִ�д˲�����");
			return;
		}
		onShowUser();
	}

	/**
	 * ��� �����Ա
	 */
	public void onShowUser() {
		try {
			String str_rolecode = new TBUtil().getSysOptionStringValue("��������Ա��ɫ��", "��������Ա"); //
			String str_roleid = UIUtil.getStringValueByDS(null, "select id from pub_role where code='" + str_rolecode + "'"); //
			if (str_roleid == null) {
				MessageBox.show(cardPanel, "ϵͳ��Ҫһ������Ϊ[" + str_rolecode + "]�Ľ�ɫ,����ϵϵͳ����Ա!"); //
				return; //
			}
			BillVO vo  = cardPanel.getBillVO();
			String cmpfileids = vo.getStringValue("cmp_cmpfile_id");
			String blcorpid = null;
			if(cmpfileids!=null && !cmpfileids.equals("")){
				blcorpid = UIUtil.getStringValueByDS(null, "select blcorpid from cmp_cmpfile where id = "+ cmpfileids);
			}else{
				MessageBox.show(cardPanel, "����ѡ��һ�������ļ�");
				return;
			}
			BillListDialog listDialog = new BillListDialog(cardPanel, "�����Ա", "PUB_USER_CODE1"); // //
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
