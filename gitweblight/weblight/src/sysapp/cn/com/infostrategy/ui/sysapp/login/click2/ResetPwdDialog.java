package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;
import java.awt.Container;

import cn.com.infostrategy.ui.common.BillDialog;

/**
 * 该类是实现修改密码的Dialog，可覆盖该类中的一些方法 来实现自己的修改密码的逻辑
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
	 * 构造方法
	 *
	 * @param _parent:母板
	 * @param _name:窗口要显示的Title
	 */
	public ResetPwdDialog(Container _parent) {
		super(_parent, "密码修改", 410, 230); //修改密码!!将姓名也显示出来后，窗口高度需要加高【李春娟/2012-06-27】
		this.getContentPane().setLayout(new BorderLayout()); //
		resetPWdPanel = new ResetPwdPanel(this); //必须要改成与菜单中打开一样,
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
	 * 重构初始光标方法,但不知为什么不行??? 以前是可以的!!
	 */
	public void initFocusAfterWindowOpened() {
		resetPWdPanel.mysetFocus(); //
	}

	/**
	 * 重写BillDialog的方法
	 */
	public void closeMe() {
		this.setCloseType(resetPWdPanel.getCloseType());
		this.dispose();
	}

}
