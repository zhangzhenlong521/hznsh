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
			btn_ref = new JButton(UIUtil.getImage("date.gif")); //��ͬ�Ŀؼ�ʹ�ò�ͬ�ı��!!!
		} else if (_type.equals(BillCellPanel.ITEMTYPE_DATETIME)) {
			btn_ref = new JButton(UIUtil.getImage("time.gif")); //��ͬ�Ŀؼ�ʹ�ò�ͬ�ı��!!!
		}

		btn_ref.setRequestFocusEnabled(false); //
		btn_ref.setPreferredSize(new Dimension(19, 20)); // ��Ť�Ŀ����߶�

		this.setLayout(new BorderLayout(0, 0));
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
		refTextField.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //���б����ı���ı߿���������!!!
		this.add(refTextField, BorderLayout.CENTER); //�ȼ����ı���

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
