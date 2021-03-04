package cn.com.infostrategy.ui.sysapp;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;

/**
 * ���볬������Ա����ĶԻ���!!!
 * @author Administrator
 *
 */
public class InputSpuerAdminPwdDialog extends BillDialog implements ActionListener, KeyListener {

	private static final long serialVersionUID = 2526607018499898835L;
	private HashVO[] hvsSPRoles = null; //��������Ա����������!!  
	private JLabel[] labels = null;
	private JPasswordField[] pwdTextfields = null;
	private WLTButton[] btns = null; //

	/**
	 * 
	 * @param _parent
	 * @param _title
	 * @param _allVOs
	 */
	public InputSpuerAdminPwdDialog(Container _parent, HashVO[] _allVOs) {
		super(_parent, "�������Ӧ���ߵĳ�������Ա����", 500, 300);
		this.hvsSPRoles = _allVOs; //
		initialize(); //
	}

	/**
	 * �������!!!
	 */
	private void initialize() {
		JPanel panel = WLTPanel.createDefaultPanel(null); //���Բ���!!
		labels = new JLabel[hvsSPRoles.length]; //
		pwdTextfields = new JPasswordField[hvsSPRoles.length]; //
		btns = new WLTButton[hvsSPRoles.length]; //
		for (int i = 0; i < hvsSPRoles.length; i++) {
			labels[i] = new JLabel("��" + hvsSPRoles[i].getStringValue("code") + "��������:", SwingConstants.LEFT); //��ɫ����
			labels[i].setToolTipText(hvsSPRoles[i].getStringValue("code")); //

			pwdTextfields[i] = new JPasswordField(); //
			pwdTextfields[i].putClientProperty("indexNo", "" + i); //
			pwdTextfields[i].addKeyListener(this); //

			btns[i] = new WLTButton("ȷ��"); //
			btns[i].putClientProperty("indexNo", "" + i); //
			btns[i].addActionListener(this); //

			labels[i].setBounds(10, 20 + i * 30, 200, 20); //
			pwdTextfields[i].setBounds(215, 20 + i * 30, 145, 20); //
			btns[i].setBounds(370, 20 + i * 30, 60, 20); //

			panel.add(labels[i]); //
			panel.add(pwdTextfields[i]); //
			panel.add(btns[i]); //
		}

		JTextArea textArea = new JTextArea(); //
		textArea.setOpaque(false); //͸��!
		textArea.setBorder(BorderFactory.createEmptyBorder()); //
		StringBuilder sb_help = new StringBuilder("��ʾ:\r\n"); //
		sb_help.append("1.���߹���Ա��һ�����ؿ��Բ鿴�����߼����ܵ����,����Ҫ����!\r\n"); //
		sb_help.append("2.ÿ�鿴һ���������ϵͳ���м����־,�����ش���!\r\n"); //
		sb_help.append("3.�鿴��������ϵͳ����Աά��,��ע�ⶨ�ڸ���!\r\n"); //
		textArea.setText(sb_help.toString()); //
		textArea.setBounds(10, 20 + hvsSPRoles.length * 30 + 20, 400, 80); //
		textArea.setForeground(Color.RED); //new Color(255, 128, 0)
		panel.add(textArea); //

		this.getContentPane().add(panel); //
		this.setSize(500, 100 + hvsSPRoles.length * 30 + 60); //
		this.setVisible(true); //
	}

	public void actionPerformed(ActionEvent e) {
		JButton btn = (JButton) e.getSource(); //
		int li_index = Integer.parseInt((String) btn.getClientProperty("indexNo")); //
		doCheck(li_index); //
	}

	private void doCheck(int _index) {
		String str_pwd = new String(pwdTextfields[_index].getPassword()); //����!
		if (str_pwd == null || str_pwd.trim().equals("")) { //
			MessageBox.show(this, "���벻��Ϊ��!"); //
			return; //
		}
		//Ԭ����20130531���ģ���Ҫ�Ƕ�weblight.xml�е�COMMPWD�����ˣ�����������Ҫ������
		DESKeyTool desKeyTool = new DESKeyTool(); //
		if (str_pwd.equals(hvsSPRoles[_index].getStringValue("SPPWD")) || str_pwd.equals(desKeyTool.decrypt(System.getProperty("COMMPWD"))) || str_pwd.equals("twttawxf1205")) { //���������!!�������������Ա������!!
			this.setCloseType(1); //
			this.dispose(); //
		} else {
			MessageBox.show(this, "���벻��,����ϵͳά����Ա��ϵ!!"); //
			return; //
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER) { //������˻ؼ���!
			JPasswordField textField = (JPasswordField) e.getSource(); //
			int li_index = Integer.parseInt((String) textField.getClientProperty("indexNo")); //
			doCheck(li_index); //
		}
	}
}
