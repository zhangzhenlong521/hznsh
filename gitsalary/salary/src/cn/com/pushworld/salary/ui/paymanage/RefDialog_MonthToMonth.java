/**************************************************************************
 * $RCSfile: RefDialog_Date.java,v $  $Revision: 1.10 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.pushworld.salary.ui.paymanage;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 选择从某月份到某月份【李春娟/2014-01-08】
 * @author lcj
 *
 */
public final class RefDialog_MonthToMonth extends AbstractRefDialog {
	private static final long serialVersionUID = 1801252787329199249L;
	private final JComboBox jboxMonth1 = new JComboBox();
	private final JComboBox jboxMonth2 = new JComboBox();
	private JButton assureButton = null;
	private JButton cancelButton = null;

	public RefDialog_MonthToMonth(Container _parent, String _title, RefItemVO value, BillPanel panel) throws Exception {
		super(_parent, _title, value, panel);
	}

	public void initialize() {
		this.setResizable(false);
		RefItemVO initVO = getInitRefItemVO();
		if (initVO == null || initVO.getId() == null) {
			jboxMonth1.setSelectedItem("1");
			jboxMonth2.setSelectedItem("12");
		} else {
			jboxMonth1.setSelectedItem(initVO.getId().split("-")[0]);
			jboxMonth2.setSelectedItem(initVO.getId().split("-")[1]);
		}
		JTextField cmb_textField = ((JTextField) ((JComponent) jboxMonth1.getEditor().getEditorComponent())); //
		cmb_textField.setEditable(false);//设置文本框中不可输入
		JTextField cmb_textField2 = ((JTextField) ((JComponent) jboxMonth2.getEditor().getEditorComponent())); //
		cmb_textField2.setEditable(false);
		JPanel con = WLTPanel.createDefaultPanel(null, WLTPanel.HORIZONTAL_FROM_MIDDLE);
		jboxMonth1.setPreferredSize(new Dimension(40, 18));
		for (int j = 1; j < 13; j++) {
			jboxMonth1.addItem("" + j);
		}
		for (int j = 1; j < 13; j++) {
			jboxMonth2.addItem("" + j);
		}
		jboxMonth1.setBounds(40, 10, 50, 18);
		jboxMonth2.setBounds(140, 10, 50, 18);

		JLabel label1 = new JLabel("从");
		JLabel label2 = new JLabel("月");
		JLabel label3 = new JLabel("至");
		JLabel label4 = new JLabel("月");
		label1.setBounds(20, 10, 30, 18);
		label2.setBounds(100, 10, 30, 18);
		label3.setBounds(120, 10, 30, 18);
		label4.setBounds(200, 10, 30, 18);
		con.add(label1);
		con.add(label2);
		con.add(label3);
		con.add(label4);
		con.add(jboxMonth2);
		con.add(jboxMonth1);
		assureButton = new WLTButton(UIUtil.getLanguage("确定"));
		assureButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				onConfirm(evt);
			}
		});
		cancelButton = new WLTButton(UIUtil.getLanguage("取消"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent evt) {
				onCancel();
			}
		});
		assureButton.setBounds(45, 60, 60, 20);
		cancelButton.setBounds(130, 60, 60, 20);
		con.add(assureButton);
		con.add(cancelButton);
		this.setFocusable(true);
		this.setLayout(new BorderLayout());
		this.add(con, BorderLayout.CENTER);
	}

	public int getInitWidth() {
		return 238;
	}

	public int getInitHeight() {
		return 120;
	}

	private void onConfirm(final ActionEvent evt) {
		setCloseType(1);
		this.dispose();
	}

	private void onCancel() {
		setCloseType(2);
		this.dispose();
	}

	public RefItemVO getReturnRefItemVO() {
		int m1 = Integer.parseInt(jboxMonth1.getSelectedItem() + "");
		int m2 = Integer.parseInt(jboxMonth2.getSelectedItem() + "");
		if (m1 > m2) {
			int tmp_m = m1;
			m1 = m2;
			m2 = tmp_m;
		}
		return new RefItemVO(m1 + "-" + m2, null, "从" + m1 + "月至" + m2 + "月");
	}
}
