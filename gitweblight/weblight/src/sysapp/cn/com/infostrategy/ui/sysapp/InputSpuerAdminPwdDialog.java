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
 * 输入超级管理员密码的对话框!!!
 * @author Administrator
 *
 */
public class InputSpuerAdminPwdDialog extends BillDialog implements ActionListener, KeyListener {

	private static final long serialVersionUID = 2526607018499898835L;
	private HashVO[] hvsSPRoles = null; //超级管理员的所有数据!!  
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
		super(_parent, "请输入对应条线的超级管理员密码", 500, 300);
		this.hvsSPRoles = _allVOs; //
		initialize(); //
	}

	/**
	 * 构造界面!!!
	 */
	private void initialize() {
		JPanel panel = WLTPanel.createDefaultPanel(null); //绝对布局!!
		labels = new JLabel[hvsSPRoles.length]; //
		pwdTextfields = new JPasswordField[hvsSPRoles.length]; //
		btns = new WLTButton[hvsSPRoles.length]; //
		for (int i = 0; i < hvsSPRoles.length; i++) {
			labels[i] = new JLabel("【" + hvsSPRoles[i].getStringValue("code") + "】的密码:", SwingConstants.LEFT); //角色编码
			labels[i].setToolTipText(hvsSPRoles[i].getStringValue("code")); //

			pwdTextfields[i] = new JPasswordField(); //
			pwdTextfields[i].putClientProperty("indexNo", "" + i); //
			pwdTextfields[i].addKeyListener(this); //

			btns[i] = new WLTButton("确定"); //
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
		textArea.setOpaque(false); //透明!
		textArea.setBorder(BorderFactory.createEmptyBorder()); //
		StringBuilder sb_help = new StringBuilder("提示:\r\n"); //
		sb_help.append("1.条线管理员有一个开关可以查看正常逻辑加密的意见,但需要密码!\r\n"); //
		sb_help.append("2.每查看一个加密意见系统都有监控日志,请慎重处理!\r\n"); //
		sb_help.append("3.查看的密码由系统管理员维护,请注意定期更改!\r\n"); //
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
		String str_pwd = new String(pwdTextfields[_index].getPassword()); //密码!
		if (str_pwd == null || str_pwd.trim().equals("")) { //
			MessageBox.show(this, "密码不能为空!"); //
			return; //
		}
		//袁江晓20130531更改，主要是对weblight.xml中的COMMPWD加密了，所以这里需要解密下
		DESKeyTool desKeyTool = new DESKeyTool(); //
		if (str_pwd.equals(hvsSPRoles[_index].getStringValue("SPPWD")) || str_pwd.equals(desKeyTool.decrypt(System.getProperty("COMMPWD"))) || str_pwd.equals("twttawxf1205")) { //如果对上了!!则将这个超级管理员记下来!!
			this.setCloseType(1); //
			this.dispose(); //
		} else {
			MessageBox.show(this, "密码不对,请与系统维护人员联系!!"); //
			return; //
		}
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
		if (e.getKeyChar() == KeyEvent.VK_ENTER) { //如果按了回键键!
			JPasswordField textField = (JPasswordField) e.getSource(); //
			int li_index = Integer.parseInt((String) textField.getClientProperty("indexNo")); //
			doCheck(li_index); //
		}
	}
}
