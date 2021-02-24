/**************************************************************************
 * $RCSfile: RefDialog_Color.java,v $  $Revision: 1.6 $  $Date: 2012/10/08 02:22:48 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillPanel;

public final class RefDialog_Color extends AbstractRefDialog {

	private static final long serialVersionUID = 1L;
	private JColorChooser colorChoose = null; //颜色选择面板

	private RefItemVO currRefItemVO = null; //

	public RefDialog_Color(Container _parent, String _title, RefItemVO value, BillPanel panel) {
		super(_parent, _title, value, panel);
	}

	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout());
		colorChoose = new JColorChooser(Color.white); //
		this.getContentPane().add(colorChoose, BorderLayout.CENTER);
		this.getContentPane().add(getSourthPanel(), BorderLayout.SOUTH);
	}

	private JPanel getSourthPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new FlowLayout()); //
		JButton btn_confirm = new JButton("确定");
		JButton btn_cancel = new JButton("取消");
		btn_confirm.setPreferredSize(new Dimension(70, 20)); //
		btn_cancel.setPreferredSize(new Dimension(70, 20)); //
		btn_confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});
		btn_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		panel.add(btn_confirm);
		panel.add(btn_cancel);
		return panel;
	}

	private void onConfirm() {
		Color selectedColor = colorChoose.getColor(); //
		if (selectedColor == null) {
			MessageBox.show(this, "请选择一种颜色,执行此操作.");
			return;
		}
		String str_color = selectedColor.getRed() + "," + selectedColor.getGreen() + "," + selectedColor.getBlue(); //
		currRefItemVO = new RefItemVO(str_color, null, str_color); //
		this.setCloseType(1);
		this.dispose();
	}

	private void onCancel() {
		currRefItemVO = null;
		this.setCloseType(2);
		this.dispose();
	}

	public RefItemVO getReturnRefItemVO() {
		return currRefItemVO;
	}

}