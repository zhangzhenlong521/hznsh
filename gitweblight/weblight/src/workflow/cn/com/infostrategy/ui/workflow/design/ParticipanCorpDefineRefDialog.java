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
 * �������в��벿�Ź�ʽ����Ĳ���!!! ����Ҫ,�µĻ��ƿ��Է�����̬������,����ֱ��ͨ���������ɫ�㶨!!
 * ����������������Ҫ,�����ڸ��ָ��ӱ仯!!! �ر��Ǹ��ݻ������ͽ��и��ּ���!!!
 * @author xch
 *
 */
public class ParticipanCorpDefineRefDialog extends AbstractRefDialog implements ActionListener {

	private static final long serialVersionUID = 751089599977539836L;

	private WLTButton btn_1, btn_2, btn_3, btn_4, btn_5; //
	private WLTButton btn_reset; //���
	protected WLTButton btn_confirm, btn_cancel, btn_wrap;
	private JTextArea textArea_1, textArea_help = null;
	private String str_text = null; //

	public ParticipanCorpDefineRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
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
		btn_4 = new WLTButton("����4"); //
		btn_5 = new WLTButton("����5"); //
		btn_reset = new WLTButton("���"); //

		btn_1.setToolTipText("�����еķ��ɺϹ沿"); //
		btn_2.setToolTipText("�Ҵ����˵����л�����Χ"); //
		btn_3.setToolTipText("�Ҵ����˵�һ�������µ��ض���չ����(���沿)"); //
		btn_4.setToolTipText("�Ҵ����˵�һ�������µ��ض��������ͽ����ض���չ����"); //
		btn_5.setToolTipText("�������������ҵ�ָ������,�ٸ�����̽������������̽"); //

		btn_1.addActionListener(this); //
		btn_2.addActionListener(this); //
		btn_3.addActionListener(this); //
		btn_4.addActionListener(this); //
		btn_5.addActionListener(this); //
		btn_reset.addActionListener(this); //

		panel.add(btn_1); //
		panel.add(btn_2); //
		panel.add(btn_3); //
		panel.add(btn_4); //
		panel.add(btn_5); //
		panel.add(btn_reset); //

		return panel; //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");
		btn_wrap = new WLTButton("�Զ�����"); //

		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		btn_wrap.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		panel.add(btn_wrap); //

		return panel;
	}

	private JTextArea getHelpTextArea() {
		textArea_help = new JTextArea(); //
		textArea_help.setFont(LookAndFeel.font); //
		textArea_help.setEditable(false); //
		textArea_help.setLineWrap(false); //
		textArea_help.setBackground(new Color(240, 240, 240)); //
		textArea_help.append("getWFCorp(\"type=ĳ�����͵Ļ���\",\"����=����\",\"������̽���ӻ�������=���в���\",\"������̽���ӻ�����չ����=���з��沿;\",\"������̽�Ƿ��������=Y\");  //ֱ�Ӹ��ݻ��������ҳ����µ������ӽṹ\r\n"); //

		textArea_help.append("\r\n**************************�������̴����˼���(���Ȳ���)********************************\r\n"); //
		textArea_help.append("getWFCorp(\"type=���̴��������ڻ���\",\"������̽���ӻ�������=һ�����в���\",\"������̽���ӻ�����չ����=һ�����кϹ沿;һ�����з��ɲ�;\",\"������̽�Ƿ��������=Y\");  //�ҳ��봴���˵�ͬһ�����������ӻ���\r\n\r\n"); //
		textArea_help
				.append("getWFCorp(\"type=���̴��������ڻ����ķ�Χ\",\"������̽���ӻ�������=һ�����в���\",\"������̽���ӻ�����չ����=һ�����кϹ沿;һ�����з��ɲ�;\",\"������̽�Ƿ��������=Y\");     //�ҳ��봴���˵Ļ���������ͬ�ĵ�һ���ϼ������µ������ӻ���,������������ڲ�����\"**��������\"���Զ��ص�\"��������\",���統��������\"һ�����в���\"ʱ��\"һ�����в�����������\"ʱ�����ҵ��丸�׻������е�һ������Ϊ\"һ�����в���\"�Ļ���,֮���Խ�[�����ķ�Χ],��������һ�����ҵ��캣��Χһ��,���뱾��������ͬ�Ķ������ҵķ�Χ֮��!\r\n\r\n"); //
		textArea_help.append("getWFCorp(\"type=���̴�����ĳ���͵��ϼ�����\",\"�״����ݵ��ĸ�����=һ������\",\"������̽���ӻ�������=һ�����в���\");\r\n\r\n"); //
		textArea_help.append("getWFCorp(\"type=���̴�����ĳ���͵��ϼ�����\",\"�״����ݵ��ĸ�����=*=>һ������\",\"������̽���ӻ�����չ����=һ�����з��沿\"); \r\n\r\n"); //
		textArea_help.append("getWFCorp(\"type=���̴�����ĳ���͵��ϼ�����\",\"�״����ݵ��ĸ�����=һ��֧�в���/һ��֧��=>һ��֧��;һ�����в���=>һ������\",\"������̽���ӻ�������=һ������=>һ�����в���;��������=>�������в���/��������\");\r\n\r\n"); //
		textArea_help.append("getWFCorp(\"type=���̴�����ĳ���͵��ϼ�����\",\"�״����ݵ��ĸ�����=һ�����в���/һ��֧��=>һ������;�������в���/����֧��=>��������\",\"������̽���ӻ�������=һ�����в���\",\"������̽���ӻ�����չ����=һ�����кϹ沿;һ�����з��ɲ�;\",\"������̽�Ƿ��������=Y\"); \r\n\r\n"); //

		textArea_help.append("\r\n**************************���ݵ�¼�˼���********************************\r\n"); //
		textArea_help.append("getWFCorp(\"type=��¼�����ڻ���\",\"������̽���ӻ�������=һ�����в���\",\"������̽���ӻ�����չ����=һ�����кϹ沿;һ�����з��ɲ�;\",\"������̽�Ƿ��������=Y\"); //�ҳ����¼�˵�ͬһ�����������ӻ���\r\n\r\n"); //
		textArea_help
				.append("getWFCorp(\"type=��¼�����ڻ����ķ�Χ\",\"������̽���ӻ�������=һ�����в���\",\"������̽���ӻ�����չ����=һ�����кϹ沿;һ�����з��ɲ�;\",\"������̽�Ƿ��������=Y\");  //�ҳ����¼�˵Ļ���������ͬ�ĵ�һ���ϼ������µ������ӻ���,������������ڲ�����\"**��������\"���Զ��ص�\"��������\",���統��������\"һ�����в���\"ʱ��\"һ�����в�����������\"ʱ�����ҵ��丸�׻������е�һ������Ϊ\"һ�����в���\"�Ļ���,֮���Խ�[�����ķ�Χ],��������һ�����ҵ��캣��Χһ��,���뱾��������ͬ�Ķ������ҵķ�Χ֮��!\r\n\r\n"); //
		textArea_help.append("getWFCorp(\"type=��¼��ĳ���͵��ϼ�����\",\"�״����ݵ��ĸ�����=һ�����в���/һ��֧��=>һ������;�������в���/����֧��=>��������\",\"������̽���ӻ�������=һ�����в���\",\"������̽���ӻ�����չ����=һ�����кϹ沿;һ�����з��ɲ�;\",\"������̽�Ƿ��������=Y\");\r\n\r\n"); ////

		textArea_help.append("\r\n��ע:\r\n"); //
		textArea_help.append("1.������÷�����Ҫ���������:\r\n"); //
		textArea_help.append("  ��һ����ֱ�Ӳ��ô��������ڻ���(�������ڲ���ת)\r\n");
		textArea_help.append("  �ڶ�����ȡ������ĳ�������ϼ�������Χ�µ�ĳ����չ���͵Ļ���(����������һ�������µķ��沿),�ⳣ�������ڵ��˷��в���,Ҫ��ͬһ�����µ���һ������(���編�沿)ʱ\r\n");
		textArea_help.append("  ��������ֱ��ȡĳ���������µ�ĳ����չ���͵Ļ���(���������µķ��沿),�������޹�,ֱ�Ӹ��ݻ��������ж�!�ⳣ�������ڵ������в���!\r\n"); //
		textArea_help.append("2.������ʹ�ô����˽��м���,����ʹ�õ�¼�˼���,��Ϊ��ʱһ�������ж�����Դ������,��ÿ�����ߵĻ����ָ���һ��,���Բ�ͬ�����ϵĴ����˵�¼����ʱ,������ݵ�¼�˼���,����Ҫ�нϸ��ӵ��߼��ж�,�����ݴ����˼�����û������鷳:\r\n"); //
		textArea_help.setSelectionStart(0); //
		textArea_help.setSelectionEnd(0); //
		return textArea_help; //
	}

	/**
	 * ȷ��
	 */
	private void onConfirm() {
		this.setCloseType(1); //
		str_text = textArea_1.getText().trim(); //
		if (!str_text.equals("")) {
			if (!str_text.startsWith("getWFCorp(")) {
				MessageBox.show(this, "��ʽû���ԡ�getWFCorp(����ͷ,���ǲ��Ե�����,����������!!"); ////
				return; //
			}
		}

		//TBUtil tbUtil = new TBUtil();
		//str_text = tbUtil.replaceAll(str_text, " ", ""); //�滻���ո�
		//str_text = tbUtil.replaceAll(str_text, "\r", ""); //ȥ�����з�
		//str_text = tbUtil.replaceAll(str_text, "\n", ""); //
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

	private void onWrap() {
		if (btn_wrap.getText().endsWith("�Զ�����")) {
			textArea_help.setLineWrap(true); //
			btn_wrap.setText("ȡ������"); //
		} else if (btn_wrap.getText().endsWith("ȡ������")) {
			textArea_help.setLineWrap(false); //
			btn_wrap.setText("�Զ�����"); //
		}
	}

	@Override
	public int getInitHeight() {
		return 480; //
	}

	@Override
	public int getInitWidth() {
		return 800; //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm(); //
		} else if (e.getSource() == btn_cancel) {
			onCanCel(); //
		} else if (e.getSource() == btn_wrap) {
			onWrap(); //
		} else if (e.getSource() == btn_1) {
			textArea_1.setText("getWFCorp(\"type=ĳ�����͵Ļ���\",\"����=����\",\"������̽���ӻ�����չ����=���ɺϹ沿\");"); //�����еķ��沿
		} else if (e.getSource() == btn_2) {
			textArea_1.setText("getWFCorp(\"type=���̴��������ڻ����ķ�Χ\");"); //�����˵����ڻ���!!
		} else if (e.getSource() == btn_3) {
			textArea_1.setText("getWFCorp(\"type=���̴�����ĳ���͵��ϼ�����\",\"�״����ݵ��ĸ�����=һ������\",\"������̽���ӻ�����չ����=һ�����з��ɺϹ沿\");"); //
		} else if (e.getSource() == btn_4) {
			textArea_1.setText("getWFCorp(\"type=���̴�����ĳ���͵��ϼ�����\",\r\n\"�״����ݵ��ĸ�����=һ������\",\r\n\"������̽���ӻ�������=һ�����в���\",\r\n\"������̽���ӻ�����չ����=���ɺϹ沿\"\r\n);"); //
		} else if (e.getSource() == btn_5) {
			textArea_1.setText("getWFCorp(\"type=���̴�����ĳ���͵��ϼ�����\",\r\n\"�״����ݵ��ĸ�����=һ�����в���/һ������/һ��֧��=>һ������;�������в���/��������/����֧��=>��������\",\r\n\"������̽���ӻ�������=һ������=>һ�����в���;��������=>�������в���\",\r\n\"������̽���ӻ�����չ����=���ɺϹ沿\",\r\n\"������̽���ӻ�������=$������\"\r\n);"); //
		} else if (e.getSource() == btn_reset) {
			textArea_1.setText(""); //
		}
	}

}
