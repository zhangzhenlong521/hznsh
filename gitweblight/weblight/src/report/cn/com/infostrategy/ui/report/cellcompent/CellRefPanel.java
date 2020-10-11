package cn.com.infostrategy.ui.report.cellcompent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.report.BillCellPanel;

public class CellRefPanel extends JPanel {

	private static final long serialVersionUID = -2414934083635057265L;

	private String type = null; //
	private JTextField refTextField = null;

	public CellRefPanel(String _type) {
		this.type = _type; //
		refTextField = new JTextField(); //

		JButton btn_ref = null;
		if (_type.equals(BillCellPanel.ITEMTYPE_DATE)) {
			btn_ref = new JButton(UIUtil.getImage("date.gif")); //不同的控件使用不同的标标!!!
		} else if (_type.equals(BillCellPanel.ITEMTYPE_DATETIME)) {
			btn_ref = new JButton(UIUtil.getImage("time.gif")); //不同的控件使用不同的标标!!!
		}

		btn_ref.setRequestFocusEnabled(false); //
		btn_ref.setPreferredSize(new Dimension(19, 20)); // 按扭的宽度与高度

		this.setLayout(new BorderLayout(0, 0));
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
		refTextField.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //在列表中文本框的边框必须是这个!!!
		this.add(refTextField, BorderLayout.CENTER); //先加入文本框

		JPanel panel_east = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); //
		panel_east.add(btn_ref);
		this.add(panel_east, BorderLayout.EAST); //
	}

	public void setValue(String _value) {
		refTextField.setText(_value);
	}

	public String getValue() {
		return refTextField.getText();
	}

	public String getType() {
		return type;
	}

}
