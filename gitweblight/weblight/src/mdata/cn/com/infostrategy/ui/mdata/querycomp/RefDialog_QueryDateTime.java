/**************************************************************************
 * $RCSfile: RefDialog_QueryDateTime.java,v $  $Revision: 1.9 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

public final class RefDialog_QueryDateTime extends AbstractRefDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	private CommonDateQueryPanel commonDateQueryPanel; ////
	private WLTButton btn_confirm, btn_cancel; //
	private RefItemVO returnRefItemVO = null; //
	private String timeType = null;//"年;季;月;天;日历",可以是这五项的任意组合，前四项表示可选择的日期类型，最后一个[日历]表示可以用日历的方式选择两个日期作为区间段【李春娟/2012-06-20】

	public RefDialog_QueryDateTime(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
	}

	public RefDialog_QueryDateTime(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, CommUCDefineVO _dfvo) {
		super(_parent, _title, refItemVO, panel);
		timeType = _dfvo.getConfValue("查询时日期类型");
	}

	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;

	}

	@Override
	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //
		commonDateQueryPanel = new CommonDateQueryPanel(timeType);
		this.getContentPane().add(commonDateQueryPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(), WLTPanel.HORIZONTAL_FROM_MIDDLE);
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
			onConfirm(); //
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //
		}
	}

	private void onConfirm() {
		RefItemVO refItemVO = commonDateQueryPanel.getSelectedRefItemVO(); //
		if (refItemVO == null) {
			return; //
		}
		this.returnRefItemVO = refItemVO; //
		this.setCloseType(1); //
		this.dispose(); //
	}

	private void onCancel() {
		this.returnRefItemVO = null; //
		this.setCloseType(2); //
		this.dispose(); //
	}

	public int getInitWidth() {
		return 520;
	}

	public int getInitHeight() {
		return 400;
	}

}
