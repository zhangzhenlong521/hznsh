package com.pushworld.ipushlbs.ui.constactcheck.p020;

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
 * @since 2011.01.13
 *
 */
public class ShowUnDealMsgAction implements WLTActionListener{
	BillCardPanel cardPanel ; // 按钮所在的cardpanel
	
	public void actionPerformed(WLTActionEvent event) throws Exception {
		cardPanel = (BillCardPanel) event.getBillPanelFrom(); //按钮所在cardpanel
		BillVO billvo = cardPanel.getBillVO();                //当前panel的billvo
		String deal_id = billvo.getStringValue("DEAL_ID"); // 获取deal id
		if(deal_id == null || "".equals(deal_id)){
			MessageBox.show(cardPanel,"没有相关数据！");
			return ;
		}else{
			showAttribute(deal_id);
		}
	}
	// 弹出属性窗口
	private void showAttribute(String deal_id) {
		BillVO[] billvos ;
		String sql_query = "" ;
		String tmp_code = "" ;
			sql_query = "select * from LBS_UNSTDFILE where id = " + deal_id ; //查出当前合同所对应的信息
			tmp_code = "LBS_UNSTDFILE_CODE3" ;
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
