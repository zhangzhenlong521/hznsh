package cn.com.infostrategy.ui.sysapp.refdialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.mdata.querycomp.CommonDateQueryPanel;

/**
 * 能用万能的日期选择参照!以后所有的涉及到日期选择的查询条件时都用这个参照!!!
 * @author xch
 *
 */
public class CommonDateTimeRefDialog extends AbstractRefDialog implements ActionListener {

	private static final long serialVersionUID = 1L;
	private String str_constraint = null; //
	private CommonDateQueryPanel commonDateQueryPanel; //
	private WLTButton btn_confirm, btn_cancel;
	private RefItemVO returnRefItemVO = null; //

	public CommonDateTimeRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
	}

	/**
	 * 
	 * @param _parent
	 * @param _title
	 * @param refItemVO
	 * @param panel
	 * @param _constraint 有约束!!
	 */
	public CommonDateTimeRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String _constraint) {
		super(_parent, _title, refItemVO, panel); //
		this.str_constraint = _constraint; //
	}

	@Override
	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //
		commonDateQueryPanel = new CommonDateQueryPanel(this.str_constraint); //
		this.getContentPane().add(commonDateQueryPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
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
		return 510;
	}

	public int getInitHeight() {
		return 365;
	}

}
