package com.pushworld.ipushlbs.ui.constactcheck.p010;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;
/**
 * ��ͬ��顾�½��������У����鿴���ԡ���ť�¼�
 * @author yinliang
 * @since 2011.01.11
 *
 */
public class ShowDealMsgAction implements WLTActionListener{
	BillCardPanel cardPanel ; // ��ť���ڵ�cardpanel
	
	public void actionPerformed(WLTActionEvent event) throws Exception {
		cardPanel = (BillCardPanel) event.getBillPanelFrom(); //��ť����cardpanel
		BillVO billvo = cardPanel.getBillVO();                //��ǰpanel��billvo
		String deal_id = billvo.getStringValue("DEALDOC_NAME"); // ��ȡdeal id
		if(deal_id == null || deal_id.trim().length() == 0){
			MessageBox.show(cardPanel,"û��������ݣ�");
			return ;
		}else{
			String codename = cardPanel.getTempletVO().getTempletcode();
			showAttribute(deal_id,codename);
		}
	}
	// �������Դ���
	private void showAttribute(String deal_id,String codename) {
		BillVO[] billvos ;
		String sql_query = "" ;
		String tmp_code = "" ;
		if(codename.equals("FORMAT_DEAL_CHECK_CODE1")){ //����Ǹ�ʽ����ϵİ�ť
			sql_query = "select * from v_lbs_stand_check_file where id = " + deal_id ; //�����ǰ��ͬ����Ӧ����Ϣ
			tmp_code = "LBS_STAND_CHECK_FILE_CODE1" ;
		}else if(codename.equals("UNFORMAT_DEAL_CHECK_CODE1")){ //����ǷǸ�ʽ����ϵİ�ť
			sql_query = "select * from LBS_UNSTDFILE where id = " + deal_id ; //�����ǰ��ͬ����Ӧ����Ϣ
			tmp_code = "LBS_UNSTDFILE_CODE3" ;
		}
		try {
			billvos = UIUtil.getBillVOsByDS(null, sql_query, UIUtil.getPub_Templet_1VO(tmp_code));
			BillCardDialog cardDialog = new BillCardDialog(cardPanel,"�鿴��ͬ����",tmp_code,600,700,billvos[0]); //������������DIALOG
			cardDialog.getCardPanel().setEditable(false);
			cardDialog.getBtn_confirm().setVisible(false);
			cardDialog.getBtn_save().setVisible(false);
			cardDialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
