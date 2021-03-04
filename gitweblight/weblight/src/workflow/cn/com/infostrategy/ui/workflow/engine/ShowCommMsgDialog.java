package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 工作流流程处理中查看常用意见
 * @author lichunjuan
 *
 */
public class ShowCommMsgDialog extends BillDialog implements ActionListener {

	private BillListPanel msgsListPanel; //
	private WLTButton btn_confirm, btn_cancel;
	private int closeType = -1;
	public static String msg = "";
	private String addmsg = "";

	/**
	 * 构造方法
	 * @param _
	 */
	public ShowCommMsgDialog(Container _parent) {
		super(_parent, "查看常用意见", 500, 400); //
		initialize(); //
	}

	public ShowCommMsgDialog(Container _parent, String msg) {
		super(_parent, "查看常用意见", 500, 400); //
		this.addmsg = msg;
		initialize(); //
	}

	private void initialize() {
		msgsListPanel = new BillListPanel(getTMO());
		msgsListPanel.setHeaderCanSort(false);
		msgsListPanel.setItemEditable(false);
		msgsListPanel.addBatchBillListButton(new WLTButton[]{WLTButton.createButtonByType(WLTButton.LIST_POPINSERT), WLTButton.createButtonByType(WLTButton.LIST_POPEDIT), WLTButton.createButtonByType(WLTButton.LIST_DELETE)});
		msgsListPanel.repaintBillListButton();
		try {
			//String[] dbmsg = UIUtil.getCommonService().getStringArrayFirstColByDS(null, "select * from pub_comboboxdict where");
			msgsListPanel.queryDataByCondition(" type ='常用意见' and code='" + ClientEnvironment.getCurrLoginUserVO().getId() + "' ", " PK_PUB_COMBOBOXDICT ");
//			for (int i = 0; i < dbmsg.length; i++) {
//				msgsListPanel.setValueAt(new StringItemVO(dbmsg[i]), msgsListPanel.newRow(), 1);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
//		if (!"".equals(addmsg)) {
//			msgsListPanel.setValueAt(new StringItemVO(addmsg), msgsListPanel.newRow(), 1);
//		}
		msgsListPanel.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT);
		msgsListPanel.addBillListMouseDoubleClickedListener(new BillListMouseDoubleClickedListener() {
			public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent _event) {
				onConfirm();
			}
		});
		this.getContentPane().add(msgsListPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");

		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	public void onConfirm() {
		if (msgsListPanel.getSelectedBillVO() == null) {
			MessageBox.show(this, "请选择一条意见！");
			return;
		} else {
			msg = msgsListPanel.getValueAt(msgsListPanel.getSelectedRow(), 1).toString();
			closeType = BillDialog.CONFIRM;
		}
		this.setVisible(false);
	}

	public void onCancel() {
		closeType = BillDialog.CANCEL;
		this.setVisible(false);
	}

	public int getCloseType() {
		return closeType;
	}

	private AbstractTMO getTMO() {
		HashVO parentVO = new HashVO(); //
		parentVO.setAttributeValue("templetcode", "常见意见");//如不加，点击按钮可能会报错
		parentVO.setAttributeValue("tablename", "PUB_COMBOBOXDICT");
		parentVO.setAttributeValue("pkname", "PK_PUB_COMBOBOXDICT");
		parentVO.setAttributeValue("savedtablename", "PUB_COMBOBOXDICT");
		parentVO.setAttributeValue("pksequencename", "S_PUB_COMBOBOXDICT");
		HashVO[] childVOs = new HashVO[4];
		childVOs[0] = new HashVO();
		childVOs[0].setAttributeValue("itemkey", "name");
		childVOs[0].setAttributeValue("itemname", "意见内容");
		childVOs[0].setAttributeValue("itemtype", WLTConstants.COMP_TEXTAREA); // 都是多行文本框
		childVOs[0].setAttributeValue("listwidth", "400"); //
		childVOs[0].setAttributeValue("cardwidth", "400*300");
		childVOs[0].setAttributeValue("listisshowable", "Y");
		childVOs[0].setAttributeValue("cardisshowable", "Y");
		childVOs[0].setAttributeValue("issave", "Y");
		childVOs[1] = new HashVO();
		childVOs[1].setAttributeValue("itemkey", "code");
		childVOs[1].setAttributeValue("itemname", "用户ID");
		childVOs[1].setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD);
		childVOs[1].setAttributeValue("listisshowable", "N");
		childVOs[1].setAttributeValue("cardisshowable", "N");
		childVOs[1].setAttributeValue("defaultvalueformula", "getLoginUserId()");
		childVOs[1].setAttributeValue("issave", "Y");
		childVOs[2] = new HashVO();
		childVOs[2].setAttributeValue("itemkey", "PK_PUB_COMBOBOXDICT"); //
		childVOs[2].setAttributeValue("itemname", "ID");
		childVOs[2].setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD);
		childVOs[2].setAttributeValue("listisshowable", "N");
		childVOs[2].setAttributeValue("cardisshowable", "N");
		childVOs[2].setAttributeValue("issave", "Y");
		childVOs[3] = new HashVO();
		childVOs[3].setAttributeValue("itemkey", "TYPE"); //
		childVOs[3].setAttributeValue("itemname", "类型");
		childVOs[3].setAttributeValue("itemtype", WLTConstants.COMP_TEXTFIELD);
		childVOs[3].setAttributeValue("listisshowable", "N");
		childVOs[3].setAttributeValue("cardisshowable", "N");
		childVOs[3].setAttributeValue("issave", "Y");
		childVOs[3].setAttributeValue("defaultvalueformula", "\"常用意见\"");
		AbstractTMO tmo = new DefaultTMO(parentVO, childVOs);
		return tmo;
	}

}
