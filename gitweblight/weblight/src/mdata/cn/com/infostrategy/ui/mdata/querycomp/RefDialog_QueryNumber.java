package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

public class RefDialog_QueryNumber extends AbstractRefDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	private JPanel contentpanel; ////
	private WLTButton btn_confirm, btn_cancel; //
	private RefItemVO returnRefItemVO = null; //
	JTextField smallnumber, bignumber;

	public RefDialog_QueryNumber(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
	}

	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;

	}

	@Override
	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //
		this.setSize(new Dimension(400, 130));
		contentpanel = new JPanel();
		JLabel desc = new JLabel("              请输入要查询的数字区间：                    " + "                         ");
		JLabel bigMark = new JLabel("从");
		smallnumber = new JFormattedTextField();
		smallnumber.setHorizontalAlignment(SwingConstants.RIGHT); //
		smallnumber.setDocument(new IntegerDoucument());
		smallnumber.setPreferredSize(new Dimension(100, 20));

		JLabel samllMark = new JLabel("到");
		bignumber = new JFormattedTextField();
		bignumber.setHorizontalAlignment(SwingConstants.RIGHT); //
		bignumber.setDocument(new IntegerDoucument());
		bignumber.setPreferredSize(new Dimension(100, 20));
		contentpanel.add(desc);
		contentpanel.add(bigMark);
		contentpanel.add(smallnumber);
		contentpanel.add(samllMark);
		contentpanel.add(bignumber);

		this.getContentPane().add(contentpanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //

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
		String small = smallnumber.getText();
		String big = bignumber.getText();
		HashVO hvo = new HashVO(); // 
		StringBuilder sb_querycondition = new StringBuilder(); //
		small = small.trim();
		big = big.trim();
		if (small.equals("") && big.equals("")) {
			sb_querycondition.append("(1=1)"); //
		} else if (!small.equals("")) {
			if (!big.equals("")) {
				if (Double.parseDouble(small) < Double.parseDouble(big)) {//判断一下输入两个数的大小，sql中永远是大于等于小的数，小于等于大的数。【李春娟/2012-03-29】
					sb_querycondition.append("({itemkey}>=" + small + " and {itemkey}<=" + big + ")"); //
				} else {
					sb_querycondition.append("({itemkey}>=" + big + " and {itemkey}<=" + small + ")"); //
				}
			} else {
				sb_querycondition.append("{itemkey}>=" + small); //
			}
		} else {
			sb_querycondition.append("{itemkey}<=" + big); //
		}
		hvo.setAttributeValue("querycondition", sb_querycondition.toString()); //		
		this.returnRefItemVO = new RefItemVO(small + ";" + big, "", "从" + small + "到" + big, hvo);
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

	class IntegerDoucument extends PlainDocument {
		private static final long serialVersionUID = -6061518182446972408L;

		public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
			char[] charofs = s.toCharArray();// 查看字符串的每个字符是否是数字
			boolean isNum = true;
			for (int i = 0; i < charofs.length; i++) {
				if ("-0123456789.".indexOf(charofs[i]) < 0) {
					isNum = false;
					break;
				}
			}
			if (isNum) { // 如果是数字就插入!!
				if (offset == 0 && (s.equals("."))) {
					Toolkit.getDefaultToolkit().beep(); // 放个声音!!
					return; //
				} else {
					super.insertString(offset, s, attributeSet);
				}
			} else {
				Toolkit.getDefaultToolkit().beep(); // 放个声音!!
				return; // 直接返回不插入了!!
			}
		}
	}

}
