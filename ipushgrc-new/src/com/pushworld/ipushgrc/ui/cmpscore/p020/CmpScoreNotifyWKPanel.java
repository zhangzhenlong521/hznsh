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

	BillListPanel panel_1 ,panel_2 ; // δ֪ͨ����֪ͨ����panel��1Ϊδ֪ͨ��2Ϊ��֪ͨ
	WLTButton btn_notify , btn_declare ,btn_query ,btn_print,but_print,but_close;// ����֪ͨ��ť , �ö���ť ,�����ť
	BillDialog billdialog ;
	BillHtmlPanel htmlpanel ;
	
	@Override
	public void initialize() {
		panel_1=new BillListPanel("CMP_SCORE_RECORD_CODE1_1");
		panel_2=new BillListPanel("CMP_SCORE_RECORD_CODE1_2");
		
		panel_2.setDataFilterCustCondition(" sendstate <> '1' ");
		//panel_1 ֪ͨ�������沼��
		btn_notify = new WLTButton("����֪ͨ");
		panel_1.addBillListButton(btn_notify); // ����֪ͨ
		btn_notify.addActionListener(this);
		btn_print = new WLTButton("��ӡ");
		panel_1.addBillListButton(btn_print); //��ӡ
		btn_print.addActionListener(this);
		panel_1.repaintBillListButton(); //ˢ�°�ť�б�
		panel_1.addBillListMouseDoubleClickedListener(this); //˫���¼�
		
		btn_query = panel_2.getBillListBtn("comm_listselect"); //��д�����ť
		btn_query.addActionListener(this);
		panel_2.repaintBillListButton();
		panel_2.addBillListMouseDoubleClickedListener(this); //˫���¼�
		
		JTabbedPane pane=new JTabbedPane();
		pane.addTab("֪ͨ����", panel_1);
		pane.addTab("֪ͨ�ѷ�",panel_2);
		this.add(pane);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if(obj == btn_notify )
			onNotify();  //����֪ͨ
		else if(obj == btn_declare)
			onDeclare(); //����ö�
		else if(obj == btn_query)
			onQuery();   //���
		else if(obj == btn_print)
			onPrint();   //��ӡ
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
	
	// ��ӡ
	private void onPrint() {
		BillVO[] billvo = panel_1.getSelectedBillVOs(); //��������
		if(billvo.length == 0){
			MessageBox.show(panel_1,"��ѡ���¼���в�����");
			return ;
		}
		if(billvo.length > 1){
			MessageBox.show(panel_1,"һ��ֻ�ܴ�ӡһ����Ϣ��");
			return ;
			/*
			 * һ��ֻ�ܴ�ӡһ����Ϣ����Ϊ������Ա��������������Υ����¼���ͬ����ӡ��ҳ����Ϣ���и��Բ�ͬ
			String dept_name = billvo[0].getStringValue("deptid");
			for(int i = 1 ; i < billvo.length ; i ++ ){
				if(!dept_name.equals(billvo[i].getStringValue("deptid"))){
					MessageBox.show(panel_1,"ֻ��֪ͨ����һ����������Ϣ!");
					return ;
				}
			}
			*/
		}
		billdialog = new BillDialog(panel_1,"��ӡ֪ͨ��",800,550);
		htmlpanel = getBillHtmlPanel(billvo);
		
		billdialog.setLayout(new BorderLayout());
		billdialog.add(htmlpanel);	
		
		billdialog.setVisible(true);
	}
	// ��ӡ
	private void onPrint(BillListPanel panel) {
		BillVO[] billvo = panel.getSelectedBillVOs(); //��������
		if(billvo.length == 0){
			MessageBox.show(panel,"��ѡ���¼���в�����");
			return ;
		}
		if(billvo.length > 1){
			String dept_name = billvo[0].getStringValue("deptid");
			for(int i = 1 ; i < billvo.length ; i ++ ){
				if(!dept_name.equals(billvo[i].getStringValue("deptid"))){
					MessageBox.show(panel,"ֻ��֪ͨ����һ����������Ϣ!");
					return ;
				}
			}
		}
		billdialog = new BillDialog(panel,"��ӡ֪ͨ��",800,550);
		htmlpanel = getBillHtmlPanel(billvo);
		
		billdialog.setLayout(new BorderLayout());
		billdialog.add(htmlpanel);	
		
		billdialog.setVisible(true);
	}

	// ����֪ͨ����
	private void onNotify() {
		// ��ʵ���ǽ� sendstate��״̬��Ϊ��֪ͨ��Ȼ��ˢ�µ�ǰ���panel_1��ͬ��Ҳ���԰�panel_2Ҳˢ�ˣ��Ե�ͬ��Щ
		BillVO[] billvo = panel_1.getSelectedBillVOs() ; //ȡ�õ�ǰѡ����е�billvo
		if(billvo.length == 0){
			MessageBox.showSelectOne(panel_1);
			return ;
		}
		if(!MessageBox.confirm(panel_1,"ȷ������֪ͨ��")){
			return ;
		}
		String sql_conditon = "''" ;
		for(int i = 0 ; i < billvo.length ; i ++){
			sql_conditon = sql_conditon + "," + billvo[i].getStringValue("id");
		}
		//�������
//		System.out.println("����Դ���ͣ�" + ServerEnvironment.getDefaultDataSourceType()); ;
		String sql_update = " update "+billvo[0].getSaveTableName()+" set sendstate = '2' " +  // ��֪ͨ״̬
				", applyendtime = + '"+this.getDaysAfterCurr(10) +"'" +  // ��ǰʱ������˺�10��
				"where id in ( "+sql_conditon+" ) " ;
		try {
			UIUtil.executeUpdateByDS(null, sql_update);  //ִ�и��²���
			panel_1.refreshData(); //ˢ��δ֪ͨpanelҳ��
			panel_2.refreshData() ; //ˢ����֪ͨpanelҳ��
		}  catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//����ö�
	private void onDeclare(){
		BillVO billvo = panel_2.getSelectedBillVO() ; //ȡ�õ�ǰѡ����е�billvo
		if(billvo == null){
			MessageBox.showSelectOne(panel_2);
			return ;
		}
		if(!billvo.getStringValue("sendstate").equals("5")){  //ֻ�вö�״̬�����ݲ������ö�
			MessageBox.show(panel_2,"��״̬�µļ�¼�������ö���");
			return ;
		}
		BillCardPanel cardPanel = new BillCardPanel("CMP_SCORE_RECORD_CODE1_3");  //��ǰ��Ƭpanel
		cardPanel.setBillVO(billvo); //
		// ���� ������ �� ������������
		cardPanel.setValueAt("resultuser", new RefItemVO(ClientEnvironment.getInstance().getLoginUserID(),"",ClientEnvironment.getInstance().getLoginUserName()));
		cardPanel.setValueAt("resultdate", new RefItemVO(UIUtil.getCurrDate(),"",UIUtil.getCurrDate()));
		
		BillCardDialog dialog = new BillCardDialog(panel_2,"����ö�",cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);//����
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) { //�������˱���,����ֽ����˲ö���Ȼ��Ϳ��Ե�������¼�롱������ȥ�����ֽ�����Ӧ�޸�
			String sql_update = "update "+billvo.getSaveTableName()+" set sendstate = '4' where id = '"+billvo.getStringValue("id")+"'" ;
			try {
				UIUtil.executeUpdateByDS(null, sql_update);  //��״̬��Ϊ�Ѹ��飬�������̽�����
				panel_2.refreshCurrSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void onQuery() {
		BillVO billvo = panel_2.getSelectedBillVO() ; //ȡ�õ�ǰѡ����е�billvo
		if(billvo == null){
			MessageBox.showSelectOne(panel_2);
			return ;
		}
		QueryMsg(billvo);
	}
	
	// ��Ƭ������ʾ
	private void QueryMsg(BillVO billvo) {
		BillCardPanel cardPanel = null ;
		BillCardDialog dialog = null ; 
		String sendstate = billvo.getStringValue("sendstate") ; // ״̬
		if(sendstate.equals("2")){ // ��֪ͨ δ���и��� ״̬
			cardPanel = new BillCardPanel("CMP_SCORE_RECORD_CODE1_1");  //��ǰ��Ƭpanel
			cardPanel.setBillVO(billvo); //
			dialog = new BillCardDialog(panel_2,"֪ͨ����",cardPanel,WLTConstants.BILLDATAEDITSTATE_INIT);
		}else if(sendstate.equals("3")){ // ����������
			cardPanel = new BillCardPanel(panel_2.templetVO);  //��ǰ��Ƭpanel
			cardPanel.setBillVO(billvo); //
			cardPanel.setGroupVisiable("����ö�", false);
			dialog = new BillCardDialog(panel_2,"֪ͨ����",cardPanel,WLTConstants.BILLDATAEDITSTATE_INIT);
		}else{ // ��������״̬���Ѹ���͵ȴ��ö���������ȫ������
			cardPanel = new BillCardPanel(panel_2.templetVO);  //��ǰ��Ƭpanel
			cardPanel.setBillVO(billvo); //
			dialog = new BillCardDialog(panel_2,"֪ͨ����",cardPanel,WLTConstants.BILLDATAEDITSTATE_INIT);
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
	
	// ƴ��HTML
	private BillHtmlPanel getBillHtmlPanel(BillVO[] billvos){
		BillHtmlPanel htmlpanel = new BillHtmlPanel(true);
		String date = UIUtil.getCurrDate();  //��ӡ���ӵ�����
		StringBuffer sb = new StringBuffer();

		sb.append("<HTML>");
		sb.append("<style>.table1{border-collapse:collapse;border:1px solid #000}");
		sb.append(".table1 td{border:1px solid #000;padding:5px}");
		sb.append("body,td,th {background-color:#F0F0F0; margin:0; font-size:12px; font-family:����, arial; color:#333;}");
		sb.append("p {TEXT-INDENT: 2em;line-height:150%}");
//		sb.append("a{color:blue;text-decoration: underline;font-size:12px;font-family:����,arial;}");
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
		
		sb.append("<body><div style=text-align:right;background-color:#fff><input type=button value=��ӡ onClick=doPrint()></div><div class=content>");
		sb.append("<p class=title0>���ִ���֪ͨ</p>");
		BillVO billvo = billvos[0];  //ȡ��һ��ֵ ����ͨ�õ���ֵ���� �о���̫���ʣ���Ϊһ����λ�в�ͬ����Υ���Ĺ涨���ܲ�ͬ�����ʺ�д��
		sb.append("<p>"+billvo.getStringViewValue("deptid")+"��</p>");
		sb.append("<p>����"+billvo.getStringValue("code")+"��Υ�������ʵȷ�ϣ����ݡ�"
				+billvo.getStringViewValue("scorestand").substring(0,billvo.getStringViewValue("scorestand").length()-1)+"��׼�����ֶ��㵥λ�������Ա���п۷����£�</p>");
		sb.append("<p><TABLE><TR><TD>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</TD><td><TABLE class=table1>");
		sb.append("<TR><Td>�¼�ȷ�ϵ����</Td><Td>���۷�������</Td><Td>��������</Td><Td>�۷�</Td><Td>�۷�����</Td></TR>");
		BillVO billvo_ = null ;
		for(int i = 0 ; i < billvos.length ; i ++){  // ѭ��ѡ������ݣ��������,�˴�����涨ֻ�ܴ�ӡһ���Ļ����Ͳ���ѭ���ˡ�
			billvo_ = billvos[i] ;
			sb.append("<tr>");
			sb.append("<td>"+billvo_.getStringValue("code")+"</td><td>"+billvo_.getStringViewValue("userid")+"</td>");
			sb.append("<td>"+billvo_.getStringViewValue("deptid")+"</td><td>"+billvo_.getStringValue("scorelost")+"</td><td>"+billvo_.getStringValue("scoredate")+"</td>");
			sb.append("</tr>");
		}
		sb.append("</TABLE></td></tr></table></p>");
		sb.append("<p>�㵥λ�����������ˣ������յ���֪ͨ��֮����ʮ�������ϼ���ز�������������룬���ڹ涨������δ���");
		sb.append("���渴�����룬ϵͳ�����涨ִ�п۷ֿ��ˡ�</p>");
		sb.append("<br><br>");
		sb.append("<p align=right>"+billvo.getStringViewValue("createorg")+"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>");
		sb.append("<p align=right>"+date.substring(0,4)+"��"+date.substring(5,7)+"��"+date.substring(8)+"��&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</p>");
		sb.append("<br><br><br>");
		
		htmlpanel.loadHtml(sb.toString());
		return htmlpanel ;
	}
	
	private String getDaysAfterCurr(int mount){//�õ��������ڼ���֮�������
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DATE, mount);
		return format.format(cal.getTime());
	}
}
