package com.pushworld.icheck.ui.p030;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
* 【李春娟/2016-08-22】
* @author  lcj
*/
public class SelectCheckListRefDialog extends AbstractRefDialog implements ActionListener {
	private static final long serialVersionUID = -1197369897915101643L;
	private BillTreePanel treePanel;
	private BillCardPanel cardPanel;
	private WLTButton btn_confirm, btn_cancel;
	private RefItemVO returnVO;

	public SelectCheckListRefDialog(Container parent, String title, RefItemVO initRefItemVO, BillPanel billPanel) {
		super(parent, title, initRefItemVO, billPanel);
		if (billPanel instanceof BillCardPanel) {
			cardPanel = (BillCardPanel) billPanel;
		}

	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnVO;
	}

	@Override
	public void initialize() {
		treePanel = new BillTreePanel("CK_PROJECT_LIST_SCY_E01");
		treePanel.queryDataByCondition(null);
		treePanel.getBillTreeBtnPanel().setVisible(false);
		this.getContentPane().add(treePanel, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	public void onConfirm() {
		try {
			if (treePanel.getSelectedVO() == null) {
				MessageBox.showSelectOne(this);
				return; //
			}
			BillVO billvo = treePanel.getSelectedVO(); //
			if (cardPanel != null) {
				BillVO[] pathVO = treePanel.getSelectedParentPathVOs();
				if (pathVO != null && pathVO.length > 0) {
					if (pathVO.length < 3) {
						MessageBox.show(this, "请选择第三级目录!");
						return;
					} else {
						cardPanel.setRealValueAt("firstname", pathVO[0].getStringValue("listname"));
						cardPanel.setRealValueAt("secondname", pathVO[1].getStringValue("listname"));
						cardPanel.setRealValueAt("thirdname", pathVO[2].getStringValue("listname"));
					}
				}
				String code = billvo.getStringValue("code");
				cardPanel.setRealValueAt("code", code);
				cardPanel.setRealValueAt("firstid", billvo.getStringValue("firstid"));
				cardPanel.setRealValueAt("secondid", billvo.getStringValue("secondid"));
				cardPanel.setRealValueAt("thirdid", billvo.getStringValue("thirdid"));
				cardPanel.setRealValueAt("parentid", billvo.getStringValue("thirdid"));//第三级为直接父亲节点
				cardPanel.setRealValueAt("linkcode",  billvo.getStringValue("linkcode"));
			}
			String listname = billvo.getStringValue("listname");
			returnVO = new RefItemVO(listname, "", listname);
			this.dispose(); //
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	public void onCancel() {
		returnVO = null;
		this.dispose();
	}

}
