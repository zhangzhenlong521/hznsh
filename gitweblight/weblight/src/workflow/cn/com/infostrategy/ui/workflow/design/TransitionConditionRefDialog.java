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
 * 工作流中连线条件的定义参照！
 * @author xch
 *
 */
public class TransitionConditionRefDialog extends AbstractRefDialog implements ActionListener {

	private static final long serialVersionUID = 751089599977539836L;

	private WLTButton btn_1, btn_2, btn_3, btn_4; //
	private WLTButton btn_reset; //清空
	protected WLTButton btn_confirm, btn_cancel;
	private JTextArea textArea_1 = null, textArea_help = null;
	private String str_text = null; //

	public TransitionConditionRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
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
		this.getContentPane().add(getNorthBtnPanel(), BorderLayout.NORTH); //按钮面板
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getNorthBtnPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		btn_1 = new WLTButton("范例1"); //
		btn_2 = new WLTButton("范例2"); //
		btn_3 = new WLTButton("范例3"); //
		btn_4 = new WLTButton("范例4(是否走过某条线)"); //
		btn_reset = new WLTButton("清空"); //

		btn_1.addActionListener(this); //
		btn_2.addActionListener(this); //
		btn_3.addActionListener(this); //
		btn_4.addActionListener(this); //
		btn_reset.addActionListener(this); //

		panel.add(btn_1); //
		panel.add(btn_2); //
		panel.add(btn_3); //
		panel.add(btn_4); //
		panel.add(btn_reset); //

		return panel; //
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

	private JTextArea getHelpTextArea() {
		textArea_help = new JTextArea(); //
		textArea_help.setFont(LookAndFeel.font); //
		textArea_help.setEditable(false); //
		textArea_help.setLineWrap(false); //
		textArea_help.setBackground(new Color(240, 240, 240)); //

		textArea_help.append("连线上支持的公式函数有:\r\n"); //
		textArea_help.append("getWFRouterMarkValue(\"法律部是否已走\"); \r\n");
		textArea_help.append("getWFRouterMarkCount(\"法律部是否已走\");\r\n");
		textArea_help.append("getWFBillItemValue(\"报送金额\");\r\n");
		textArea_help.append("getOneTransThrowCount(\"WAT01\");  //判断某条线是否走过,参数是连线编码\r\n");
		textArea_help.append("setExtConfMap(\"源头环节出去的二次角色条件\",\"综合员/员工\",\"目标环节进入的二次角色过滤\",\"处室负责人\");\r\n\r\n");

		textArea_help.append("以前都是在环节上后台进行【机构】+【角色】的定义！后来在实际应用中发现,同样一个环节,从不同的连线过来时,【参与者机构】虽然一样,但【参与者角色】却不一样！\r\n"); //
		textArea_help.append("所以觉得在连线上定义能解决此问题！现在就在连线上增加参数【源头环节出去的二次角色条件】【目标环节进入的二次角色过滤】乖参数！\r\n\r\n"); //
		textArea_help.append("这就是说,将以前在环节上定义【机构】+【角色】变成了：环节上只定义【机构】,而角色则在连线上定义！！！\r\n\r\n"); //
		textArea_help.append("连线上原来的条件还是能用的,只不过可以配置第二个公式！即setExtConfMap(....),从而指定根据角色过滤...\r\n"); //
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
			String str_text = "if(getWFRouterMarkValue(\"法律部是否已走\")==\"Y\",\"true\",\"false\");"; //
			if (MessageBox.confirm(this, str_text)) {
				textArea_1.setText(str_text);
			}
		} else if (e.getSource() == btn_2) {
			String str_text = "if(getWFBillItemValue(\"风险金额\")>100,\"true\",\"false\");"; //
			if (MessageBox.confirm(this, str_text)) {
				textArea_1.setText(str_text);
			}
		} else if (e.getSource() == btn_3) {
			String str_text = "if(getWFRouterMarkValue(\"法律部是否已走\")==\"Y\",\"true\",\"false\");\r\nsetExtConfMap(\"源头环节出去的二次角色条件\",\"综合员/员工\",\"目标环节进入的二次角色过滤\",\"处室负责人/部门负责人\");"; //
			if (MessageBox.confirm(this, str_text)) {
				textArea_1.setText(str_text);
			}
		} else if (e.getSource() == btn_4) {
			String str_text = "if(getOneTransThrowCount(\"WC_Y2-WC-LD\")>=1,\"true\",\"false\");"; //
			if (MessageBox.confirm(this, str_text)) {
				textArea_1.setText(str_text);
			}
		} else if (e.getSource() == btn_reset) {
			textArea_1.setText(""); //
		}
	}

	/**
	 * 确定
	 */
	private void onConfirm() {
		this.setCloseType(1); //
		str_text = textArea_1.getText().trim(); //
		this.dispose(); //
	}

	/**
	 * 取消
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
