package cn.com.infostrategy.ui.workflow.design;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * ������������ִ�к�Ĵ�������������
 * @author xch
 *
 */
public class TransitionExecInterceptRefDialog extends AbstractRefDialog implements ActionListener {

	private static final long serialVersionUID = 751089599977539836L;

	private WLTButton btn_1, btn_2, btn_3; //
	private WLTButton btn_reset; //���
	protected WLTButton btn_confirm, btn_cancel;
	private JTextArea textArea_1 = null, textArea_help = null;
	private String str_text = null; //

	public TransitionExecInterceptRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return new RefItemVO(str_text, null, str_text);
	}

	@Override
	public void initialize() {
		textArea_1 = new JTextArea(); //
		textArea_1.setFont(LookAndFeel.font); //
		textArea_1.setLineWrap(true); //
		if (getInitRefItemVO() != null) {
			textArea_1.setText(this.getInitRefItemVO().getId()); //
		}

		WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, new JScrollPane(textArea_1), new JScrollPane(getHelpTextArea())); //
		splitPanel.setDividerLocation(100); //
		this.getContentPane().add(splitPanel); //
		this.getContentPane().add(getNorthBtnPanel(), BorderLayout.NORTH); //��ť���
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getNorthBtnPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		btn_1 = new WLTButton("����1"); //
		btn_2 = new WLTButton("����2"); //
		btn_3 = new WLTButton("����3"); //
		btn_reset = new WLTButton("���"); //

		btn_1.addActionListener(this); //
		btn_2.addActionListener(this); //
		btn_3.addActionListener(this); //
		btn_reset.addActionListener(this); //

		panel.add(btn_1); //
		panel.add(btn_2); //
		panel.add(btn_3); //
		panel.add(btn_reset); //

		return panel; //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");

		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_cancel); //

		return panel;
	}

	private JTextArea getHelpTextArea() {
		textArea_help = new JTextArea(); //
		textArea_help.setFont(LookAndFeel.font); //
		textArea_help.setEditable(false); //
		textArea_help.setLineWrap(false); //
		textArea_help.setBackground(new Color(240, 240, 240)); //

		textArea_help.append("���������߹���ִ�еĶ���������:\r\n"); ////
		textArea_help.append("setWFRouterMarkValue(\"���ɲ��Ƿ�����\",\"Y\");\r\n");
		textArea_help.append("addWFRouterMarkValue(\"���ɲ��Ƿ�����\",\"Y\");\r\n");
		textArea_help.append("=>cn.com.pushworld.TestTransIntercept;  //����Ҫ�̳��ڽӿ�[cn.com.infostrategy.bs.workflow.WorkFlowTransitionExecIfc]\r\n");

		textArea_help.setSelectionStart(0); //
		textArea_help.setSelectionEnd(0); //
		return textArea_help; //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm(); //
		} else if (e.getSource() == btn_cancel) {
			onCanCel(); //
		} else if (e.getSource() == btn_1) {
			String str_text = "setWFRouterMarkValue(\"���ɲ��Ƿ�����\",\"Y\");\r\n"; //
			if (MessageBox.confirm(this, str_text)) {
				textArea_1.setText(str_text);
			}
		} else if (e.getSource() == btn_2) {
			String str_text = "addWFRouterMarkValue(\"���ɲ��Ƿ�����\",\"Y\");";
			if (MessageBox.confirm(this, str_text)) {
				textArea_1.setText(str_text);
			}
		} else if (e.getSource() == btn_3) {
			String str_text = "=>cn.com.pushworld.TestTransIntercept"; //ִ���࣡����
			if (MessageBox.confirm(this, str_text)) {
				textArea_1.setText(str_text);
			}
		} else if (e.getSource() == btn_reset) {
			textArea_1.setText(""); //
		}
	}

	/**
	 * ȷ��
	 */
	private void onConfirm() {
		this.setCloseType(1); //
		str_text = textArea_1.getText().trim(); //
		this.dispose(); //
	}

	/**
	 * ȡ��
	 */
	private void onCanCel() {
		this.setCloseType(2); //
		str_text = null;
		this.dispose(); //
	}

	@Override
	public int getInitHeight() {
		return 480; //
	}

	@Override
	public int getInitWidth() {
		return 800; //
	}

}
