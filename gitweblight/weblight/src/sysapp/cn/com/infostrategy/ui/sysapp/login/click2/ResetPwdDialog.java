package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;
import java.awt.Container;

import cn.com.infostrategy.ui.common.BillDialog;

/**
 * ������ʵ���޸������Dialog���ɸ��Ǹ����е�һЩ���� ��ʵ���Լ����޸�������߼�
 *
 * @author Administrator
 *
 */
public class ResetPwdDialog extends BillDialog {

	private ResetPwdPanel resetPWdPanel = null; //
	/**
	 *
	 */
	private static final long serialVersionUID = -9186210344819389340L;

	/**
	 * ���췽��
	 *
	 * @param _parent:ĸ��
	 * @param _name:����Ҫ��ʾ��Title
	 */
	public ResetPwdDialog(Container _parent) {
		super(_parent, "�����޸�", 410, 230); //�޸�����!!������Ҳ��ʾ�����󣬴��ڸ߶���Ҫ�Ӹߡ����/2012-06-27��
		this.getContentPane().setLayout(new BorderLayout()); //
		resetPWdPanel = new ResetPwdPanel(this); //����Ҫ�ĳ���˵��д�һ��,
		this.getContentPane().add(resetPWdPanel, BorderLayout.CENTER); //
	}

	public static void openMe(java.awt.Container _parent, String _tile, String _type) {
		try {
			BillDialog dialog = new ResetPwdDialog(_parent);
			dialog.setVisible(true); //
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@Override
	/**
	 * �ع���ʼ��귽��,����֪Ϊʲô����??? ��ǰ�ǿ��Ե�!!
	 */
	public void initFocusAfterWindowOpened() {
		resetPWdPanel.mysetFocus(); //
	}

	/**
	 * ��дBillDialog�ķ���
	 */
	public void closeMe() {
		this.setCloseType(resetPWdPanel.getCloseType());
		this.dispose();
	}

}
