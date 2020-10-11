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
 * 人员姓名更新
 * 
 * @author [张营闯/2014-05-04]
 */
public class ChangeUserNameWKPanel extends AbstractWorkPanel implements ActionListener {

	private WLTButton changename;

	public void initialize() {
		JPanel panelMain = new JPanel(new FlowLayout());
		WLTLabel labInfo = new WLTLabel("将系统中的真实姓名更换为设置的随机姓名");
		panelMain.add(labInfo);
		changename = new WLTButton("更新pub_user表中的人员姓名");
		changename.addActionListener(this);
		panelMain.add(changename);
		this.add(panelMain);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == changename) {
			if (!MessageBox.confirm(this, "混淆前请做好备份,操作后数据将不可恢复,继续吗？")) {
				return;
			}
			RandomUserName userName = new RandomUserName();
			try {
				userName.changeUserName();
			} catch (WLTRemoteException e1) {
				MessageBox.show(this, "糟糕！姓名替换失败了");
				e1.printStackTrace();
			} catch (Exception e1) {
				MessageBox.show(this, "糟糕！姓名替换失败了");
				e1.printStackTrace();
			}
			MessageBox.show(this, "已成功更新人员信息！");
		}

	}

}
