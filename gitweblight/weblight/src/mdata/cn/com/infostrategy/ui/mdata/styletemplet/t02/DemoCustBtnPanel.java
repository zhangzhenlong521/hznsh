package cn.com.infostrategy.ui.mdata.styletemplet.t02;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.AbstractCustomerButtonBarPanel;

public class DemoCustBtnPanel extends AbstractCustomerButtonBarPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1874360922721140625L;

	public void initialize() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
		JButton btn_1 = new WLTButton("Òþ²ØÐÐ");
		btn_1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onclick();

			}
		});
		JButton btn_2 = new WLTButton("ÏÔÊ¾ÐÐ");
		btn_2.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onclick2();

			}
		});

		this.add(btn_1);
		this.add(btn_2);
	}

	private void onclick() {
		AbstractStyleWorkPanel_02 stylePanel = (AbstractStyleWorkPanel_02) this.getParentWorkPanel();
	}

	private void onclick2() {
		AbstractStyleWorkPanel_02 stylePanel = (AbstractStyleWorkPanel_02) this.getParentWorkPanel();
	}
}
