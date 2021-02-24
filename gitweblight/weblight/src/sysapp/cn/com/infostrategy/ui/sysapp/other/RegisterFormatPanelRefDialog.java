/**************************************************************************
 * $RCSfile: RegisterFormatPanelRefDialog.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:19:33 $
 **************************************************************************/
package cn.com.infostrategy.ui.sysapp.other;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.mdata.styletemplet.config.StyleConfigPanel_0A;

/**
 * 自定义参照,用在风格模板维护的样板公式中
 * @author xch
 *
 */
public class RegisterFormatPanelRefDialog extends AbstractRefDialog {

	private static final long serialVersionUID = -7521165672198422057L;

	private JButton btn_ok = null; //确定按钮
	private JButton btn_cancel = null; //取消按钮
	private RefItemVO currRefItemVO = null;
	protected StyleConfigPanel_0A contentpanel = null;

	public RegisterFormatPanelRefDialog(Container _parent, String _title, RefItemVO value, BillPanel panel) {
		super(_parent, _title, value, panel);
	}

	/**
	 * 初始化页面,实现抽象方法!!
	 */
	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout());
		contentpanel = new StyleConfigPanel_0A(""); //自定主页面
		contentpanel.getSourthPanel().setVisible(false); //
		if (getInitRefItemVO() != null) {
			contentpanel.getTextArea().setText(getInitRefItemVO().getId()); //
		}
		
		this.getContentPane().add(contentpanel, BorderLayout.CENTER); //主面板!!!,所有变化都在这里!
		this.getContentPane().add(getSthPanel(), BorderLayout.SOUTH);
	}

	public RefItemVO getReturnRefItemVO() {
		return currRefItemVO; //
	}

	private JPanel getSthPanel() {
		JPanel panel = new JPanel();
		btn_ok = new JButton("确定"); //
		btn_cancel = new JButton("取消"); //

		btn_ok.setPreferredSize(new Dimension(60, 20));
		btn_cancel.setPreferredSize(new Dimension(60, 20));

		btn_ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});

		btn_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		panel.setLayout(new FlowLayout(FlowLayout.CENTER)); //水平布局
		panel.add(btn_ok);
		panel.add(btn_cancel);
		return panel;
	}

	private void onConfirm() {
		currRefItemVO = new RefItemVO(contentpanel.getTextArea().getText(), null, contentpanel.getTextArea().getText()); //
		this.setCloseType(1);
		this.dispose();
	}

	protected void onCancel() {
		this.setCloseType(2);
		this.dispose();
	}

	public int getInitWidth() {
		return 900;
	}

	public int getInitHeight() {
		return 500;
	}

}
