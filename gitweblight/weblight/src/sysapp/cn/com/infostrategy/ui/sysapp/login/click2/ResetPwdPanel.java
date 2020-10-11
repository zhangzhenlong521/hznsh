package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTPanelUI;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

/**
 * �޸��û���������!!
 * @author xch
 *
 */
public class ResetPwdPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JDialog parentDialog = null;
	private HashMap menuConfMap = null; //

	private JLabel jlb_user = null;
	private JLabel jlb_name = null;
	private JLabel jlb_pwd1 = null;
	private JLabel jlb_pwd2 = null;
	private JLabel jlb_pwd3 = null;
	private JTextField jtf_user = null;
	private JTextField jtf_name = null;
	protected JPasswordField jpf_pwd1 = null;
	protected JPasswordField jpf_pwd2 = null;
	protected JPasswordField jpf_pwd3 = null;
	private JButton jbt_confirm = null;
	private JButton jbt_cancel = null;
	protected SysAppServiceIfc uls = null;
	private int closeType = -1;

	private String pwdlength = null;

	public ResetPwdPanel() {
		this(null, null); //
	}

	public ResetPwdPanel(JDialog _dialog) {
		this(_dialog, null); //
	}

	public ResetPwdPanel(HashMap _menuConfMap) {
		this(null, _menuConfMap); //
	}

	public ResetPwdPanel(JDialog _dialog, HashMap _menuConfMap) {
		parentDialog = _dialog;
		menuConfMap = _menuConfMap; //

		this.setLayout(null); //
		this.setBackground(LookAndFeel.defaultShadeColor1); //
		this.setUI(new WLTPanelUI(WLTPanelUI.INCLINE_NW_TO_SE, Color.WHITE, true)); //
		jlb_user = getJlb("�û���");
		jlb_name = getJlb("����");
		jlb_pwd1 = getJlb("ԭʼ����");

		if (menuConfMap != null && menuConfMap.containsKey("���")) { //��ͬ����Ŀ�Ŀ�Ȳ�һ��!��ҵ��ƹ�һ��Ҫ��8-15,һ��Ҫ��10-16
			pwdlength = (String) menuConfMap.get("���");
			jlb_pwd2 = getJlb("������(" + pwdlength + "λ)");
		} else {
			pwdlength = new TBUtil().getSysOptionStringValue("��Ա�����޸Ŀ��", "6-10");
			if (pwdlength == null || pwdlength.equals("") || pwdlength.equals("0")) {
				jlb_pwd2 = getJlb("������");
			} else {
				jlb_pwd2 = getJlb("������(" + pwdlength + "λ)");
			}

		}
		jlb_pwd3 = getJlb("��������������");

		jlb_user.setBounds(5, 15, 150, 20); //
		jlb_name.setBounds(5, 40, 150, 20);
		jlb_pwd1.setBounds(5, 65, 150, 20); //
		jlb_pwd2.setBounds(5, 90, 150, 20); //
		jlb_pwd3.setBounds(5, 115, 150, 20); //

		jtf_user = new JTextField(ClientEnvironment.getInstance().getLoginUserCode());
		jtf_user.setEditable(false);
		jtf_name = new JTextField(ClientEnvironment.getInstance().getLoginUserName());
		jtf_name.setEditable(false);
		jpf_pwd1 = new JPasswordField();
		jpf_pwd2 = new JPasswordField();
		jpf_pwd3 = new JPasswordField();

		Border border = BorderFactory.createLineBorder(LookAndFeel.compBorderLineColor); //
		jtf_user.setBorder(border); //
		jtf_name.setBorder(border);
		jpf_pwd1.setBorder(border); //
		jpf_pwd2.setBorder(border); //
		jpf_pwd3.setBorder(border); //

		jpf_pwd2.setToolTipText("��������ĸ, ����, �����ַ����������ϵ����");

		jtf_user.setBounds(160, 15, 155, 20); //
		jtf_name.setBounds(160, 40, 155, 20);
		jpf_pwd1.setBounds(160, 65, 155, 20); //
		jpf_pwd2.setBounds(160, 90, 155, 20); //
		jpf_pwd3.setBounds(160, 115, 155, 20); //

		jbt_confirm = new JButton("ȷ��");
		jbt_confirm.setPreferredSize(new Dimension(70, 20));
		jbt_confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});
		jbt_cancel = new JButton("ȡ��");
		jbt_cancel.setPreferredSize(new Dimension(70, 20));
		jbt_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		jbt_confirm.setBounds(150, 150, 60, 20);
		jbt_cancel.setBounds(215, 150, 60, 20);

		this.add(jlb_user); //
		this.add(jlb_name);
		this.add(jlb_pwd1); //
		this.add(jlb_pwd2); //
		this.add(jlb_pwd3); //

		this.add(jtf_user); //
		this.add(jtf_name);
		this.add(jpf_pwd1); //
		this.add(jpf_pwd2); //
		this.add(jpf_pwd3); //

		this.add(jbt_confirm); //
		this.add(jbt_cancel); //

		mysetFocus(); //
	}

	public int getCloseType() {
		return closeType;
	}

	/**
	 * ���ǰ��Ŀ���˵��
	 *
	 * @param _text
	 * @return
	 */
	private JLabel getJlb(String _text) {
		JLabel label = new JLabel(_text);
		label.setHorizontalAlignment(JLabel.RIGHT);
		return label;
	}

	/**
	 * �˲�ԭʼ�����������ȷ�ԣ��ɸ��Ǳ�������ʵ���Լ��ĺ˲��߼�
	 *
	 * @return,���Ϊfalse��˵����ԭʼ�������
	 */
	protected boolean checkUserPwd() {
		return true; //
	}

	public void mysetFocus() {
		jlb_pwd1.requestFocus(); //��Ϊ��ʼ���!����֪ΪʲôûЧ��,ͨ���ع�BillDialog.initFocusAfterWindowOpened()����Ҳ����! ��֪Ϊʲô??
	}

	/**
	 * �˲�����������Ƿ�Ϻ�Ҫ��
	 *
	 * @return
	 */
	private boolean checkCondition() {
		String str_oldpwd = new String(jpf_pwd1.getPassword()); //������
		String sr_newpwd = new String(jpf_pwd2.getPassword()); //������
		String sr_newpwd2 = new String(jpf_pwd3.getPassword()); //������

		if (str_oldpwd.equals("")) {
			JOptionPane.showMessageDialog(this, "��������ԭʼ����!");
			return false;
		}

		if (sr_newpwd.equals("")) {
			JOptionPane.showMessageDialog(this, "������������!");
			return false;
		}

		if (sr_newpwd2.equals("")) {
			JOptionPane.showMessageDialog(this, "���ٴ�����������!");
			return false;
		}

		if (!sr_newpwd.equals(sr_newpwd2)) {
			MessageBox.show(this, "������������벻ƥ�䣬����������!"); //
			return false;
		}

		if (sr_newpwd.equals(str_oldpwd)) {
			MessageBox.show(this, "�����벻�����������ͬ,���޸�!"); //
			return false;
		}

		//�Ƿ�������븴�����ж�
		String pwdOption = new TBUtil().getSysOptionStringValue("���븴�����ж�", null);
		
		//׷�ӹ���Ա��������У����� �����/2013-04-27��
		String pwd_admin = new TBUtil().getSysOptionStringValue("����Ա�������", ""); //admin;admin1,10-16,2
		if(!pwd_admin.equals("")&&pwd_admin.contains(",")){
			String[] pwd_admins = pwd_admin.split(",");	
			String str_user = jtf_user.getText();
			
			if(pwd_admins[0]!=null&&!pwd_admins[0].equals("")&&pwd_admins[0].contains(";")){
				String[] str_users = pwd_admins[0].split(";");	
				for(int i=0;i<str_users.length;i++){
					if(str_user.equals(str_users[i])){
						pwdlength = pwd_admins[1];
						pwdOption = pwd_admins[2];
					}
				}
			}else{
				if(str_user.equals(pwd_admins[0])){
					pwdlength = pwd_admins[1];
					pwdOption = pwd_admins[2];
				}			
			}
		}
		
		if (pwdlength == null || "".equals(pwdlength)) {
			if (sr_newpwd.length() < 6 || sr_newpwd.length() > 10) { //6-10λ֮��
				MessageBox.show(this, "�����������볤����6-10λ֮��!"); //
				return false;
			}
		} else if (pwdlength.contains("-")) {
			String[] str_pwdlength = pwdlength.split("-");
			if (sr_newpwd.length() < Integer.parseInt(str_pwdlength[0]) || sr_newpwd.length() > Integer.parseInt(str_pwdlength[1])) { //10-16λ֮��
				MessageBox.show(this, "�����������볤����" + pwdlength + "λ֮��!"); //
				return false;
			}

		} else if (sr_newpwd.length() != Integer.parseInt(pwdlength) && !pwdlength.equals("0")) {
			MessageBox.show(this, "�����������볤��Ϊ" + pwdlength + "λ!"); //
			return false;
		}

		//�Ƿ�������븴�����ж�
		if (pwdOption != null) {
			HashMap parmap = new HashMap();
			parmap.put("pwd", sr_newpwd);
			String isPass = "";
			String msg = "";
			if (pwdOption.contains(".")) {
				HashMap hashmap = new TBUtil().reflectCallCommMethod(pwdOption, parmap);
				isPass = hashmap.get("isPass").toString();
				msg = hashmap.get("msg") != null ? hashmap.get("msg").toString() : null;
			} else { //����ϵͳĬ�ϵĻ���
				int level = 0;
				try {
					level = Integer.parseInt(pwdOption);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				boolean flag = new TBUtil().checkPasswordComplex(sr_newpwd, level);
				if (flag) {
					return true;
				} else {
					MessageBox.show(this, "��������ɴ�д��ĸ��Сд��ĸ�����֡������ַ��е�" + level + "�����!");
					return false;
				}
			}
			if (isPass.equals("0")) {
				if (msg == null || msg.equals("")) {
					msg = "�����������ĸ�����֡������ַ��е�2�����!";
				}
				MessageBox.show(this, msg);
				return false;
			}
		}
		return true;
	}

	/**
	 * ����ȷ����ť
	 */
	private void onConfirm() {
		if (!checkCondition()) {
			return;
		}

		try {
			String str_user = jtf_user.getText(); //
			String str_oldpwd = new String(jpf_pwd1.getPassword()); //������
			String sr_newpwd = new String(jpf_pwd2.getPassword()); //������
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			String ret_value=service.resetPwd(str_user, str_oldpwd, sr_newpwd); //
			//Ԭ���� 20180725�޸� ��Ҫ�����̫ƽ��Ŀ����־��صı����ͻ�Ҫ������error����ӷ���ֵ���������Ϊ1���ʾ�޸ĳɹ�������ѷ���ֵ������ʾ
			if (ret_value!=null&&ret_value.equals("1")) {
				MessageBox.show(this, "����������������!"); //
				closeType = 1;
				if (parentDialog != null) {
					parentDialog.dispose(); //�رմ���
				}
			}else {
				MessageBox.show(this, ret_value); //
			}
			
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	/**
	 * ����ȡ����ť
	 */
	private void onCancel() {
		jpf_pwd1.setText("");
		jpf_pwd2.setText("");
		jpf_pwd3.setText("");
		closeType = 2;
		if (parentDialog != null) {
			parentDialog.dispose(); //�رմ���
		}
	}
}
