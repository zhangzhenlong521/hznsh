package cn.com.pushworld.salary.ui.tools;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;

/**
 * ��Ա��������
 * 
 * @author [��Ӫ��/2014-05-04]
 */
public class ChangeUserNameWKPanel extends AbstractWorkPanel implements ActionListener {

	private WLTButton changename;

	public void initialize() {
		JPanel panelMain = new JPanel(new FlowLayout());
		WLTLabel labInfo = new WLTLabel("��ϵͳ�е���ʵ��������Ϊ���õ��������");
		panelMain.add(labInfo);
		changename = new WLTButton("����pub_user���е���Ա����");
		changename.addActionListener(this);
		panelMain.add(changename);
		this.add(panelMain);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == changename) {
			if (!MessageBox.confirm(this, "����ǰ�����ñ���,���������ݽ����ɻָ�,������")) {
				return;
			}
			RandomUserName userName = new RandomUserName();
			try {
				userName.changeUserName();
			} catch (WLTRemoteException e1) {
				MessageBox.show(this, "��⣡�����滻ʧ����");
				e1.printStackTrace();
			} catch (Exception e1) {
				MessageBox.show(this, "��⣡�����滻ʧ����");
				e1.printStackTrace();
			}
			MessageBox.show(this, "�ѳɹ�������Ա��Ϣ��");
		}

	}

}
