/**************************************************************************
 * $RCSfile: MenuConfigDialog.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:19:32 $
 **************************************************************************/
package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.sysapp.login.TMO_Pub_Menu;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;

public class MenuConfigDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	BillCardPanel cardPanel = null;
	WLTButton btn_save, btn_confirm, btn_cancel;

	int closeType = -1;

	HashVO hvo = new HashVO(); //

	public MenuConfigDialog(Container _parent, String _id) {
		super(_parent, "菜单快速编辑", 700, 525); //
		this.getContentPane().setLayout(new BorderLayout());
		cardPanel = new BillCardPanel(new TMO_Pub_Menu());
		cardPanel.queryDataByCondition("id=" + _id); //

		this.getContentPane().add(cardPanel, BorderLayout.CENTER);
		cardPanel.setEditableByEditInit(); //
		cardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		cardPanel.setEditable("SEQ", false); //
		cardPanel.setEditable("PARENTMENUID_NAME", false); //

		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_save = new WLTButton("保存");
		btn_cancel = new WLTButton("取消");

		btn_save.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_save); //
		panel.add(btn_cancel); //
		this.getContentPane().add(panel, BorderLayout.SOUTH);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {
			try {
				cardPanel.updateData();
			} catch (Exception ex) {
				MessageBox.showException(this, ex);
			} //
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	public void onConfirm() {
		try {
			cardPanel.updateData();
			BillVO billVO = cardPanel.getBillVO(); //
			hvo.setAttributeValue("NAME", billVO.getStringValue("NAME")); //
			hvo.setAttributeValue("ENAME", billVO.getStringValue("ENAME")); //

			hvo.setAttributeValue("USECMDTYPE", billVO.getStringValue("USECMDTYPE")); //

			hvo.setAttributeValue("COMMANDTYPE", billVO.getStringValue("COMMANDTYPE")); //
			hvo.setAttributeValue("COMMAND", billVO.getStringValue("COMMAND")); //
			hvo.setAttributeValue("COMMANDTYPE2", billVO.getStringValue("COMMANDTYPE2")); //
			hvo.setAttributeValue("COMMAND2", billVO.getStringValue("COMMAND2")); //
			hvo.setAttributeValue("COMMANDTYPE3", billVO.getStringValue("COMMANDTYPE3")); //
			hvo.setAttributeValue("COMMAND3", billVO.getStringValue("COMMAND3")); //
			hvo.setAttributeValue("HELPFILE", billVO.getStringValue("HELPFILE")); //
			hvo.setAttributeValue("ISEXTEND", billVO.getStringValue("ISEXTEND")); //
			hvo.setAttributeValue("EXTENDHEIGHT", billVO.getStringValue("EXTENDHEIGHT")); //
			hvo.setAttributeValue("OPENTYPE", billVO.getStringValue("OPENTYPE")); //
			hvo.setAttributeValue("OPENTYPEWEIGHT", billVO.getStringValue("OPENTYPEWEIGHT")); //
			hvo.setAttributeValue("OPENTYPEHEIGHT", billVO.getStringValue("OPENTYPEHEIGHT")); //
			closeType = 1; //
			this.dispose(); //
		} catch (Exception e) {
			MessageBox.showException(this, e);
		} //
	}

	public void onCancel() {
		closeType = 2;
		this.dispose();
	}

	public int getCloseType() {
		return closeType;
	}

	public BillCardPanel getCardPanel() {
		return cardPanel;
	}

	public HashVO getHvo() {
		return hvo;
	}

}
