package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * ����Ȩ�޲���Map�������õĲ���,����ӳ���Ǻ��ֹ��˷�ʽ(��������/��Ա����),����������Ա�ֶηֱ���ʲô?
 * @author xch
 *
 */
public class DataPolicyMapConfRefDialog extends AbstractRefDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JComboBox comBox = null; //
	private JTextField text_1, text_2, text_3 = null; //
	private WLTButton btn_confirm, btn_cancel; //

	private RefItemVO returnRefVO = null; //

	public DataPolicyMapConfRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
	}

	@Override
	public void initialize() {
		TBUtil tbUtil = new TBUtil(); //
		RefItemVO initRefVO = this.getInitRefItemVO(); //
		HashMap map1 = null; //
		String str_otherpar = null; //
		if (initRefVO != null) {
			int li_pos = initRefVO.getId().indexOf("@"); //
			if (li_pos > 0) { //�����@
				String str_1 = initRefVO.getId().substring(0, li_pos); //
				str_otherpar = initRefVO.getId().substring(li_pos + 1, initRefVO.getId().length()); //
				map1 = tbUtil.convertStrToMapByExpress(str_1); //ת��һ��
			} else {
				map1 = tbUtil.convertStrToMapByExpress(initRefVO.getId()); //ת��һ��
			}
		}

		JPanel contentPanel = WLTPanel.createDefaultPanel(null); //
		//���˷�ʽ
		JLabel label_0 = new JLabel("���˷�ʽ", JLabel.RIGHT); //
		label_0.setBounds(10, 10, 90, 20); //
		contentPanel.add(label_0); //
		comBox = new JComboBox(); //
		comBox.addItem("��������"); //
		comBox.addItem("��Ա����"); //
		comBox.setBounds(110, 10, 150, 20); //
		contentPanel.add(comBox); //
		if (map1 != null && map1.get("���˷�ʽ") != null) {
			comBox.setSelectedItem(map1.get("���˷�ʽ")); //
		}

		//�����ֶ�
		JLabel label_1 = new JLabel("�����ֶ���", JLabel.RIGHT); //
		label_1.setToolTipText("Ĭ����createcorp");  //
		label_1.setBounds(10, 40, 90, 22); //
		text_1 = new JTextField(); //
		text_1.setBounds(110, 40, 300, 22); //
		contentPanel.add(label_1); //
		contentPanel.add(text_1); //
		if (map1 != null && map1.get("�����ֶ���") != null) {
			text_1.setText((String) map1.get("�����ֶ���")); //
		}

		//��Ա�ֶ�
		JLabel label_2 = new JLabel("��Ա�ֶ���", JLabel.RIGHT); //
		label_2.setToolTipText("Ĭ����createuser");  //
		label_2.setBounds(10, 70, 90, 22); //
		text_2 = new JTextField(); //
		text_2.setBounds(110, 70, 300, 22); //
		contentPanel.add(label_2); //
		contentPanel.add(text_2); //
		if (map1 != null && map1.get("��Ա�ֶ���") != null) {
			text_2.setText((String) map1.get("��Ա�ֶ���")); //
		}

		//��������!
		JLabel label_3 = new JLabel("��������", JLabel.RIGHT); //
		label_3.setBounds(10, 100, 90, 22); //
		text_3 = new JTextField(); //
		text_3.setBounds(110, 100, 300, 22); //
		contentPanel.add(label_3); //
		contentPanel.add(text_3); //
		if (str_otherpar != null) {
			text_3.setText(str_otherpar); //
		}

		//��ť
		btn_confirm = new WLTButton("ȷ��"); //
		btn_cancel = new WLTButton("ȡ��"); //
		btn_confirm.setBounds(150, 135, 60, 20); //
		btn_cancel.setBounds(225, 135, 60, 20); //
		btn_confirm.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		contentPanel.add(btn_confirm); //
		contentPanel.add(btn_cancel); //

		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(contentPanel); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	private void onConfirm() {
		StringBuilder sb_text = new StringBuilder(); //
		String str_combox = (String) comBox.getSelectedItem(); //
		String str_1 = text_1.getText().trim(); //
		String str_2 = text_2.getText().trim(); //
		sb_text.append("���˷�ʽ=" + str_combox + ";"); //

		if (str_combox.equals("��������") && str_1.equals("")) {
			if (!MessageBox.confirm(this, "��ѡ����ǻ�������,��ȴû�ж�������ֶ���,�Ƿ����?")) {
				return;
			}
		}
		
		if (str_combox.equals("��Ա����") && str_2.equals("")) {
			if (!MessageBox.confirm(this, "��ѡ�������Ա����,��ȴû�ж�����Ա�ֶ���,�Ƿ����?")) {
				return;
			}
		}

		if (!str_1.equals("")) {
			sb_text.append("�����ֶ���=" + str_1 + ";"); //
		}
		if (!str_2.equals("")) {
			sb_text.append("��Ա�ֶ���=" + str_2 + ";"); //
		}
		if (!(text_3.getText().trim().equals(""))) {
			sb_text.append("@" + text_3.getText().trim()); //
		}
		returnRefVO = new RefItemVO(sb_text.toString(), null, sb_text.toString()); //
		this.setCloseType(1); //
		this.dispose(); //
	}

	private void onCancel() {
		returnRefVO = null; //
		this.setCloseType(2); //
		this.dispose(); //
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefVO;
	}

	public int getInitWidth() {
		return 450;
	}

	public int getInitHeight() {
		return 200;
	}

}
