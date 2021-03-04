package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JPanel;

import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.HFlowLayoutPanel;
import cn.com.infostrategy.ui.mdata.VFlowLayoutPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;

/**
 * 通用查询面板
 * @author xch
 *
 */
public class CommQueryDialog extends BillDialog implements ActionListener {
	private VFlowLayoutPanel queryPanel = null; //查询面板..
	private WLTButton btn_confirm, btn_reset, btn_cancel; //确定,清空,取消
	int closeType = -1; //

	public CommQueryDialog(Container _parent, String _title, int _width, int li_height, VFlowLayoutPanel _queryPanel) {
		super(_parent, _title, _width, li_height);
		this.queryPanel = _queryPanel;
		initialize(); //
	}

	/**
	 * 初始化页面
	 */
	private void initialize() {
		this.getContentPane().add(queryPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	/**
	 * 按钮栏面板..
	 * @return
	 */
	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		panel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor);
		btn_confirm = new WLTButton("确定"); //
		btn_reset = new WLTButton("清空"); //
		btn_cancel = new WLTButton("取消"); //
		btn_cancel.addActionListener(this); //
		btn_reset.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_reset); //
		panel.add(btn_cancel); //
		return panel;
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { //
			this.closeType = BillDialog.CONFIRM; //
			this.dispose(); //
		} else if (e.getSource() == btn_reset) { //
			resetAllCompent(); //清空所有控件
		} else if (e.getSource() == btn_cancel) { //
			this.closeType = BillDialog.CANCEL; //
			this.dispose(); //
		}
	}

	/**
	 * 清空所有控件..
	 */
	public void resetAllCompent() {
		AbstractWLTCompentPanel[] allCompents = getAllQueryCompents(); //
		for (int i = 0; i < allCompents.length; i++) {
			allCompents[i].reset(); //清空某个控件!
		}
	}

	/**
	 * 
	 * @return
	 */
	public AbstractWLTCompentPanel[] getAllQueryCompents() {
		ArrayList al_allcompents = new ArrayList(); //
		JComponent[] rows = queryPanel.getAllCompents(); //
		for (int i = 0; i < rows.length; i++) {
			HFlowLayoutPanel rowPanel = (HFlowLayoutPanel) rows[i]; //
			JComponent[] rowcompents = rowPanel.getAllCompents(); //
			for (int j = 0; j < rowcompents.length; j++) {
				al_allcompents.add(rowcompents[j]); //
			}
		}
		return (AbstractWLTCompentPanel[]) al_allcompents.toArray(new AbstractWLTCompentPanel[0]); //
	}

	public int getCloseType() {
		return closeType;
	}

}
