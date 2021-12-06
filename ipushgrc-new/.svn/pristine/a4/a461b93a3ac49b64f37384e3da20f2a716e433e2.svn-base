package com.pushworld.ipushgrc.ui.cmpscore.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.report.BillHtmlPanel;

/**
 * 积分复议裁定
 * @author p17
 *getList("CMP_SCORE_RECORD_CODE1_2")
 */
public class CmpScoreDecideWKPanel extends AbstractWorkPanel implements ActionListener,BillListMouseDoubleClickedListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6185759633280357032L;
	private BillListPanel panel=null;
	WLTButton btn_notify , btn_declare ,btn_query ,btn_print,but_print,but_close;// 发送通知按钮 , 裁定按钮 ,浏览按钮
	BillDialog billdialog ;
	BillHtmlPanel htmlpanel ;
	@Override
	public void initialize() {
		panel=new BillListPanel("CMP_SCORE_RECORD_CODE1_2");
		
		panel.setDataFilterCustCondition(" sendstate = '5' ");
		btn_declare = new WLTButton("裁定");
		panel.addBillListButton(btn_declare);
		btn_declare.addActionListener(this);
		btn_query = panel.getBillListBtn("comm_listselect"); //重写浏览按钮
		btn_query.addActionListener(this);
		panel.repaintBillListButton();
		panel.addBillListMouseDoubleClickedListener(this); //双击事件
		
		this.add(panel);
	}
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj == btn_declare)
			onDeclare(); //复议裁定
		else if(obj == btn_query)
			onQuery();   //浏览
		else if(obj == but_close)
			onClose(e);
		
	}
	
	private void onClose(ActionEvent e) {
		billdialog.closeMe();
	}

	// 点击浏览按钮，各个状态下的数据会跳出有不同条目的卡片信息
	private void onQuery() {
		BillVO billvo = panel.getSelectedBillVO() ; //取得当前选择的行的billvo
		if(billvo == null){
			MessageBox.showSelectOne(panel);
			return ;
		}
		QueryMsg(billvo);
	}
	// 卡片具体显示
	private void QueryMsg(BillVO billvo) {
		BillCardPanel cardPanel = null ;
		BillCardDialog dialog = null ; 
		String sendstate = billvo.getStringValue("sendstate") ; // 状态
		if(sendstate.equals("2")){ // 已通知 未进行复议 状态
			cardPanel = new BillCardPanel("CMP_SCORE_RECORD_CODE1_1");  //当前卡片panel
			cardPanel.setBillVO(billvo); //
			dialog = new BillCardDialog(panel,"通知详情",cardPanel,WLTConstants.BILLDATAEDITSTATE_INIT);
		}else if(sendstate.equals("3")){ // 复议审批中
			cardPanel = new BillCardPanel("CMP_SCORE_RECORD_CODE1_1_2");  //当前卡片panel
			cardPanel.setBillVO(billvo); //
			dialog = new BillCardDialog(panel,"通知详情",cardPanel,WLTConstants.BILLDATAEDITSTATE_INIT);
		}else{ // 其他两种状态，已复议和等待裁定，都可以全部看到
			cardPanel = new BillCardPanel(panel.templetVO);  //当前卡片panel
			cardPanel.setBillVO(billvo); //
			dialog = new BillCardDialog(panel,"通知详情",cardPanel,WLTConstants.BILLDATAEDITSTATE_INIT);
		}
		dialog.setVisible(true); //
		
	}
	//复议裁定
	private void onDeclare(){
		BillVO billvo = panel.getSelectedBillVO() ; //取得当前选择的行的billvo
		if(billvo == null){
			MessageBox.showSelectOne(panel);
			return ;
		}
		if(!billvo.getStringValue("sendstate").equals("5")){  //只有裁定状态的数据才允许被裁定
			MessageBox.show(panel,"此状态下的记录不允许被裁定！");
			return ;
		}
		BillCardPanel cardPanel = new BillCardPanel("CMP_SCORE_RECORD_CODE1_2");  //当前卡片panel
		cardPanel.setBillVO(billvo); //
		// 插入 申请人 和 申请日期数据
		cardPanel.setValueAt("resultuser", new RefItemVO(ClientEnvironment.getInstance().getLoginUserID(),"",ClientEnvironment.getInstance().getLoginUserName()));
		cardPanel.setValueAt("resultdate", new RefItemVO(UIUtil.getCurrDate(),"",UIUtil.getCurrDate()));
		
		BillCardDialog dialog = new BillCardDialog(panel,"复议裁定",cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);//更新
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) { //如果点击了保存,则表现进行了裁定，然后就可以到“积分录入”功能中去将积分进行相应修改
			String sql_update = "update "+billvo.getSaveTableName()+" set sendstate = '4' where id = '"+billvo.getStringValue("id")+"'" ;
			try {
				UIUtil.executeUpdateByDS(null, sql_update);  //将状态改为已复议，整个过程结束！
				 //计算总分
//				String totalscore = billvo.getStringValue("totalscore");
//					if("".equals(totalscore) || totalscore == null){  //如果之前没有过扣分
//						UIUtil.executeUpdateByDS(null,"update cmp_score_record set totalscore = '"+billvo.getStringValue("resultscore")+"' where id = "+billvo.getStringValue("id") );
//					}else{
//						UIUtil.executeUpdateByDS(null,"update cmp_score_record set totalscore = totalscore + '"+billvo.getStringValue("resultscore")+"' where id = "+billvo.getStringValue("id") );
//					}
				String currentYear=new SimpleDateFormat("yyyy").format(new Date());
				
				sql_update = " update "+billvo.getSaveTableName()+" set totalscore = " +
						"(select sum(resultscore) from  " +billvo.getSaveTableName()+
								"  where userid = " + billvo.getStringValue("userid")+" and scoredate like '%"+currentYear+"%' )"+
				" where userid = " + billvo.getStringValue("userid") + "  and scoredate like '%"+currentYear+"%'";
				UIUtil.executeUpdateByDS(null, sql_update);//修改累计扣分
				panel.refreshCurrSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent event) {
		QueryMsg(event.getBillListPanel().getSelectedBillVO());
		
	}

}
