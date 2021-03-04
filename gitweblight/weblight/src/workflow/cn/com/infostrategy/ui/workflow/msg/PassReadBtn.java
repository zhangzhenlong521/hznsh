package cn.com.infostrategy.ui.workflow.msg;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 表单传阅按钮 【杨科/2012-11-28】
 */

public class PassReadBtn implements ActionListener{
	private BillListPanel billList = null;
	private BillCardPanel cardPanel;
	private WLTButton btn_passread, btn_exit; 
	private BillDialog dialog = null;
	private String msgtype;
	private String taskid, dealpoolid, prinstanceid;
	
	public PassReadBtn(BillListPanel billList, String msgtype){
		this(billList, msgtype, "", "", "");
	}
	
    public PassReadBtn(BillListPanel billList, String msgtype, String taskid, String dealpoolid, String prinstanceid){
        this.billList = billList;
        this.msgtype = msgtype;
        this.taskid = taskid;
        this.dealpoolid = dealpoolid;
        this.prinstanceid = prinstanceid;
        
        cardPanel = new BillCardPanel("PUB_MESSAGE_BTN");
        cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		cardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardPanel.getBillCardBtnPanel().setVisible(false);
		cardPanel.setGroupVisiable("意见列表", false);
		cardPanel.setVisiable("opinion", false);
        
        btn_passread = new WLTButton("传阅"); 
		btn_exit = new WLTButton("返回"); 
		
		btn_passread.addActionListener(this); 
		btn_exit.addActionListener(this); 
		
        WLTPanel btnPanel = new WLTPanel(1, new FlowLayout(1));
		btnPanel.add(btn_passread);
		btnPanel.add(btn_exit);
		
		dialog = new BillDialog(billList, "传阅", 600, 400);
		dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(cardPanel, "Center");
        dialog.getContentPane().add(btnPanel, "South");
        dialog.setVisible(true);
    }

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_passread) {
			try {
				cardPanel.stopEditing();
				if(cardPanel.getRealValueAt("RECEIVER") == null && cardPanel.getRealValueAt("RECVCORP") == null && cardPanel.getRealValueAt("RECVROLE") == null) {
					MessageBox.show(dialog, "传阅范围不能为空!");
					return;
				}
				if (!cardPanel.checkValidate()) {
					return;
				}
				
				BillVO billVO = cardPanel.getBillVO(); 
				
				String templetcode = billList.templetVO.getTempletcode();
				String templetname = billList.templetVO.getTempletname();
				String tablename = billList.templetVO.getTablename();
				String pkey = billList.templetVO.getPkname();
				String pvalue = billList.getSelectedBillVO().getPkValue();
				String tostr = billList.getSelectedBillVO().toString();
				
			    String newid = UIUtil.getSequenceNextValByDS(null, "s_pub_message");
				UIUtil.executeUpdateByDS(null, new InsertSQLBuilder("pub_message", new String[][] {
						{ "id", newid }, 
						{ "msgtype", msgtype }, 
						{ "msgtitle", templetname }, 
						{ "receiver", billVO.getStringValue("receiver") }, 
						{ "recvcorp", billVO.getStringValue("recvcorp") }, 
						{ "recvrole", billVO.getStringValue("recvrole") }, 
						{ "msgopinion", billVO.getStringValue("msgopinion") }, 
						{ "sender", billVO.getStringValue("sender") }, 
						{ "sendercorp", billVO.getStringValue("sendercorp") }, 
						{ "senddate", billVO.getStringValue("senddate") }, 
						{ "isdelete", "N" }, 
						{ "tostr", tostr }, 
						{ "templetcode", templetcode }, 
						{ "templetname", templetname }, 
						{ "tablename", tablename }, 
						{ "pkey", pkey }, 
						{ "pvalue", pvalue }, 
						{ "taskid", taskid }, 
						{ "dealpoolid", dealpoolid }, 
						{ "prinstanceid", prinstanceid },
						{ "creattime", UIUtil.getCurrTime() } 
						}).getSQL());
				
				MessageBox.show("传阅成功!");
				dialog.dispose();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}else if (e.getSource() == btn_exit) {
			dialog.dispose();
		}		
	}

}
