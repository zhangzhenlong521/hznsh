package com.pushworld.ipushgrc.ui.cmpscore.p020;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JTabbedPane;

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

public class CmpScoreNotifyWKPanel extends AbstractWorkPanel implements ActionListener,BillListMouseDoubleClickedListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8376252236712624771L;

	BillListPanel panel_1 ,panel_2 ; // 未通知和已通知两个panel，1为未通知，2为已通知
	WLTButton btn_notify , btn_declare ,btn_query ,btn_print,but_print,but_close;// 发送通知按钮 , 裁定按钮 ,浏览按钮
	BillDialog billdialog ;
	BillHtmlPanel htmlpanel ;
	
	@Override
	public void initialize() {
		panel_1=new BillListPanel("CMP_SCORE_RECORD_CODE1_1");
		panel_2=new BillListPanel("CMP_SCORE_RECORD_CODE1_2");
		
		panel_2.setDataFilterCustCondition(" sendstate <> '1' ");
		//panel_1 通知待发界面布置
		btn_notify = new WLTButton("发送通知");
		panel_1.addBillListButton(btn_notify); // 发送通知
		btn_notify.addActionListener(this);
		btn_print = new WLTButton("打印");
		panel_1.addBillListButton(btn_print); //打印
		btn_print.addActionListener(this);
		panel_1.repaintBillListButton(); //刷新按钮列表
		panel_1.addBillListMouseDoubleClickedListener(this); //双击事件
		
		btn_query = panel_2.getBillListBtn("comm_listselect"); //重写浏览按钮
		btn_query.addActionListener(this);
		panel_2.repaintBillListButton();
		panel_2.addBillListMouseDoubleClickedListener(this); //双击事件
		
		JTabbedPane pane=new JTabbedPane();
		pane.addTab("通知待发", panel_1);
		pane.addTab("通知已发",panel_2);
		this.add(pane);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj == btn_notify )
			onNotify();  //发送通知
		else if(obj == btn_declare)
			onDeclare(); //复议裁定
		else if(obj == btn_query)
			onQuery();   //浏览
		else if(obj == btn_print)
			onPrint();   //打印
		else if(obj == but_close)
			onClose(e);
		else if(obj == but_print)
			onPrintRel();
		
	}
	
	private void onPrintRel(){
		
	}
	
	private void onClose(ActionEvent e) {
		billdialog.closeMe();
	}
	
	// 打印
	private void onPrint() {
		BillVO[] billvo = panel_1.getSelectedBillVOs(); //发布多条
		if(billvo.length == 0){
			MessageBox.show(panel_1,"请选择记录进行操作！");
			return ;
		}
		if(billvo.length > 1){
			MessageBox.show(panel_1,"一次只能打印一条信息！");
			return ;
			/*
			 * 一次只能打印一条信息，因为各个人员所属机构或者所违规的事件不同，打印的页面信息会有各自不同
			String dept_name = billvo[0].getStringValue("deptid");
			for(int i = 1 ; i < billvo.length ; i ++ ){
				if(!dept_name.equals(billvo[i].getStringValue("deptid"))){
					MessageBox.show(panel_1,"只能通知发布一个机构的信息!");
					return ;
				}
			}
			*/
		}
		billdialog = new BillDialog(panel_1,"打印通知单",800,550);
		htmlpanel = getBillHtmlPanel(billvo);
		
		billdialog.setLayout(new BorderLayout());
		billdialog.add(htmlpanel);	
		
		billdialog.setVisible(true);
	}
	// 打印
	private void onPrint(BillListPanel panel) {
		BillVO[] billvo = panel.getSelectedBillVOs(); //发布多条
		if(billvo.length == 0){
			MessageBox.show(panel,"请选择记录进行操作！");
			return ;
		}
		if(billvo.length > 1){
			String dept_name = billvo[0].getStringValue("deptid");
			for(int i = 1 ; i < billvo.length ; i ++ ){
				if(!dept_name.equals(billvo[i].getStringValue("deptid"))){
					MessageBox.show(panel,"只能通知发布一个机构的信息!");
					return ;
				}
			}
		}
		billdialog = new BillDialog(panel,"打印通知单",800,550);
		htmlpanel = getBillHtmlPanel(billvo);
		
		billdialog.setLayout(new BorderLayout());
		billdialog.add(htmlpanel);	
		
		billdialog.setVisible(true);
	}

	// 发送通知方法
	private void onNotify() {
		// 其实就是将 sendstate的状态改为已通知，然后刷新当前这个panel_1，同样也可以把panel_2也刷了，显得同步些
		BillVO[] billvo = panel_1.getSelectedBillVOs() ; //取得当前选择的行的billvo
		if(billvo.length == 0){
			MessageBox.showSelectOne(panel_1);
			return ;
		}
		if(!MessageBox.confirm(panel_1,"确定发送通知吗？")){
			return ;
		}
		String sql_conditon = "''" ;
		for(int i = 0 ; i < billvo.length ; i ++){
			sql_conditon = sql_conditon + "," + billvo[i].getStringValue("id");
		}
		//更新语句
//		System.out.println("数据源类型：" + ServerEnvironment.getDefaultDataSourceType()); ;
		String sql_update = " update "+billvo[0].getSaveTableName()+" set sendstate = '2' " +  // 已通知状态
				", applyendtime = + '"+this.getDaysAfterCurr(10) +"'" +  // 当前时间向后退后10天
				"where id in ( "+sql_conditon+" ) " ;
		try {
			UIUtil.executeUpdateByDS(null, sql_update);  //执行更新操作
			panel_1.refreshData(); //刷新未通知panel页面
			panel_2.refreshData() ; //刷新已通知panel页面
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//复议裁定
	private void onDeclare(){
		BillVO billvo = panel_2.getSelectedBillVO() ; //取得当前选择的行的billvo
		if(billvo == null){
			MessageBox.showSelectOne(panel_2);
			return ;
		}
		if(!billvo.getStringValue("sendstate").equals("5")){  //只有裁定状态的数据才允许被裁定
			MessageBox.show(panel_2,"此状态下的记录不允许被裁定！");
			return ;
		}
		BillCardPanel cardPanel = new BillCardPanel("CMP_SCORE_RECORD_CODE1_3");  //当前卡片panel
		cardPanel.setBillVO(billvo); //
		// 插入 申请人 和 申请日期数据
		cardPanel.setValueAt("resultuser", new RefItemVO(ClientEnvironment.getInstance().getLoginUserID(),"",ClientEnvironment.getInstance().getLoginUserName()));
		cardPanel.setValueAt("resultdate", new RefItemVO(UIUtil.getCurrDate(),"",UIUtil.getCurrDate()));
		
		BillCardDialog dialog = new BillCardDialog(panel_2,"复议裁定",cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);//更新
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) { //如果点击了保存,则表现进行了裁定，然后就可以到“积分录入”功能中去将积分进行相应修改
			String sql_update = "update "+billvo.getSaveTableName()+" set sendstate = '4' where id = '"+billvo.getStringValue("id")+"'" ;
			try {
				UIUtil.executeUpdateByDS(null, sql_update);  //将状态改为已复议，整个过程结束！
				panel_2.refreshCurrSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void onQuery() {
		BillVO billvo = panel_2.getSelectedBillVO() ; //取得当前选择的行的billvo
		if(billvo == null){
			MessageBox.showSelectOne(panel_2);
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
			dialog = new BillCardDialog(panel_2,"通知详情",cardPanel,WLTConstants.BILLDATAEDITSTATE_INIT);
		}else if(sendstate.equals("3")){ // 复议审批中
			cardPanel = new BillCardPanel(panel_2.templetVO);  //当前卡片panel
			cardPanel.setBillVO(billvo); //
			cardPanel.setGroupVisiable("复议裁定", false);
			dialog = new BillCardDialog(panel_2,"通知详情",cardPanel,WLTConstants.BILLDATAEDITSTATE_INIT);
		}else{ // 其他两种状态，已复议和等待裁定，都可以全部看到
			cardPanel = new BillCardPanel(panel_2.templetVO);  //当前卡片panel
			cardPanel.setBillVO(billvo); //
			dialog = new BillCardDialog(panel_2,"通知详情",cardPanel,WLTConstants.BILLDATAEDITSTATE_INIT);
		}
		dialog.setVisible(true); //
		
	}
	
	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent event) {
		if(event.getSource() == panel_1)
			onPrint(panel_1);
		if(event.getSource() == panel_2)
			//QueryMsg(event.getBillListPanel().getSelectedBillVO());
			onPrint(panel_2);
	}
	
	// 拼出HTML
	private BillHtmlPanel getBillHtmlPanel(BillVO[] billvos){
		BillHtmlPanel htmlpanel = new BillHtmlPanel(true);
		String date = UIUtil.getCurrDate();  //打印单子的日期
		StringBuffer sb = new StringBuffer();

		sb.append("<HTML>");
		sb.append("<style>.table1{border-collapse:collapse;border:1px solid #000}");
		sb.append(".table1 td{border:1px solid #000;padding:5px}");
		sb.append("body,td,th {background-color:#F0F0F0; margin:0; font-size:12px; font-family:宋体, arial; color:#333;}");
		sb.append("p {TEXT-INDENT: 2em;line-height:150%}");
//		sb.append("a{color:blue;text-decoration: underline;font-size:12px;font-family:宋体,arial;}");
//		sb.append("a:hover{color: red;text-decoration: underline;}");
		sb.append(" .content {width:100%;height:100%;border:1px;text-align:center solid #ccc;padding:20px 80px 20px 50px;margin:0 auto;");
		sb.append("background-image: -moz-linear-gradient(top, #FEFEFE, #D6D6D6);");
		sb.append("background-image: -webkit-gradient(linear,left top,left bottom,color-stop(0, #FEFEFE),color-stop(1, #D6D6D6));");
		sb.append("filter:  progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='#FEFEFE', endColorstr='#D6D6D6');");
		sb.append("-ms-filter: progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr='#FEFEFE', endColorstr='#D6D6D6');}");
		sb.append(".title0 {color:#ff0000; FONT-SIZE: 24px;font-weight:bold;text-align:center;}");
//		sb.append(".title1 {FONT-SIZE: 20px;font-weight:bold;text-align:center;}");
//		sb.append(".title2 {FONT-SIZE: 18px;font-weight:bold;text-align:center;}");
//		sb.append(".title3 {FONT-SIZE: 12px;font-weight:bold;}");
		sb.append(".word {background-color:#FF9632}");
		sb.append("#top-link {PADDING-RIGHT:10px; PADDING-LEFT:10px;FONT-WEIGHT:bold; ");
		sb.append("RIGHT:5px;PADDING-BOTTOM:10px;COLOR:green;BOTTOM:5px;PADDING-TOP:10px;POSITION:fixed;TEXT-DECORATION:none}");
		sb.append("</style>");
		sb.append("<script>");
//		sb.append("function closeHtml(){window.opener=null; window.close();}");
		sb.append("function doPrint(){window.print();}");
		sb.append("</script>");
		
		sb.append("<body><div style=text-align:right;background-color:#fff><input type=button value=打印 onClick=doPrint()></div><div class=content>");
		sb.append("<p class=title0>积分处罚通知</p>");
		BillVO billvo = billvos[0];  //取第一个值 进行通用的数值配置 感觉不太合适，因为一个单位中不同的人违反的规定可能不同，不适合写死
		sb.append("<p>"+billvo.getStringViewValue("deptid")+"：</p>");
		sb.append("<p>根据"+billvo.getStringValue("code")+"号违规操作事实确认，依据《"
				+billvo.getStringViewValue("scorestand").substring(0,billvo.getStringViewValue("scorestand").length()-1)+"标准》，现对你单位及相关人员进行扣分如下：</p>");
		sb.append("<p><TABLE><TR><TD>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</TD><td><TABLE class=table1>");
		sb.append("<TR><Td>事件确认单编号</Td><Td>被扣分人姓名</Td><Td>所属机构</Td><Td>扣分</Td><Td>扣分日期</Td></TR>");
		BillVO billvo_ = null ;
		for(int i = 0 ; i < billvos.length ; i ++){  // 循环选择的数据，插入表中,此处如果规定只能打印一条的话，就不用循环了。
			billvo_ = billvos[i] ;
			sb.append("<tr>");
			sb.append("<td>"+billvo_.getStringValue("code")+"</td><td>"+billvo_.getStringViewValue("userid")+"</td>");
			sb.append("<td>"+billvo_.getStringViewValue("deptid")+"</td><td>"+billvo_.getStringValue("scorelost")+"</td><td>"+billvo_.getStringValue("scoredate")+"</td>");
			sb.append("</tr>");
		}
		sb.append("</TABLE></td></tr></table></p>");
		sb.append("<p>你单位和上述积分人，可在收到本通知单之日起十日内向上级相关部门提出复议申请，如在规定期限内未提出");
		sb.append("书面复议申请，系统将按规定执行扣分考核。</p>");
		sb.append("<br><br>");
		sb.append("<p align=right>"+billvo.getStringViewValue("createorg")+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>");
		sb.append("<p align=right>"+date.substring(0,4)+"年"+date.substring(5,7)+"月"+date.substring(8)+"日&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>");
		sb.append("<br><br><br>");
		
		htmlpanel.loadHtml(sb.toString());
		return htmlpanel ;
	}
	
	private String getDaysAfterCurr(int mount){//得到距离现在几天之后的日期
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE, mount);
		return format.format(cal.getTime());
	}
}
