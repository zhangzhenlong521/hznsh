package com.pushworld.ipushlbs.ui.constactcheck.p010;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;
/**
 * 合同审查【新建】窗口中，【查看属性】按钮事件
 * @author yinliang
 * @since 2011.01.11
 *
 */
public class ShowDealMsgAction implements WLTActionListener{
	BillCardPanel cardPanel ; // 按钮所在的cardpanel
	
	public void actionPerformed(WLTActionEvent event) throws Exception {
		cardPanel = (BillCardPanel) event.getBillPanelFrom(); //按钮所在cardpanel
		BillVO billvo = cardPanel.getBillVO();                //当前panel的billvo
		String deal_id = billvo.getStringValue("DEALDOC_NAME"); // 获取deal id
		if(deal_id == null || deal_id.trim().length() == 0){
			MessageBox.show(cardPanel,"没有相关数据！");
			return ;
		}else{
			String codename = cardPanel.getTempletVO().getTempletcode();
			showAttribute(deal_id,codename);
		}
	}
	// 弹出属性窗口
	private void showAttribute(String deal_id,String codename) {
		BillVO[] billvos ;
		String sql_query = "" ;
		String tmp_code = "" ;
		if(codename.equals("FORMAT_DEAL_CHECK_CODE1")){ //如果是格式审查上的按钮
			sql_query = "select * from v_lbs_stand_check_file where id = " + deal_id ; //查出当前合同所对应的信息
			tmp_code = "LBS_STAND_CHECK_FILE_CODE1" ;
		}else if(codename.equals("UNFORMAT_DEAL_CHECK_CODE1")){ //如果是非格式审查上的按钮
			sql_query = "select * from LBS_UNSTDFILE where id = " + deal_id ; //查出当前合同所对应的信息
			tmp_code = "LBS_UNSTDFILE_CODE3" ;
		}
		try {
			billvos = UIUtil.getBillVOsByDS(null, sql_query, UIUtil.getPub_Templet_1VO(tmp_code));
			BillCardDialog cardDialog = new BillCardDialog(cardPanel,"查看合同属性",tmp_code,600,700,billvos[0]); //根据条件创建DIALOG
			cardDialog.getCardPanel().setEditable(false);
			cardDialog.getBtn_confirm().setVisible(false);
			cardDialog.getBtn_save().setVisible(false);
			cardDialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
