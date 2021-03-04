/**************************************************************************
 * $RCSfile: StyleConfigRefDialog.java,v $  $Revision: 1.12 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.config;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractTempletRefPars;

/**
 * 各种风格模板配置界面
 * @author xch
 *
 */
public class StyleConfigRefDialog extends AbstractRefDialog {

	private static final long serialVersionUID = -7521165672198422057L;
	private Container parentContainer = null;
	private String title = null; //
	private String initstring = null; //
	private String str_commandtype = null;
	private JButton btn_ok = null; //确定按钮
	private JButton btn_cancel = null; //取消按钮
	private JButton btn_close = null; //关闭按钮

	AbstractTempletRefPars parap = null;

	String result = ""; //

	public StyleConfigRefDialog(Container _parent, String _title, RefItemVO value, BillPanel panel) {
		super(_parent, _title, value, panel);
		this.parentContainer = _parent; //
		this.title = _title; //
	}

	/**
	 * 初始化页面,实现抽象方法!!
	 */
	public void initialize() {
		if (title.equals("路径1")) {
			str_commandtype = ((BillCardPanel) getBillPanel()).getRealValueAt("COMMANDTYPE"); //菜单类型
		} else if (title.equals("路径2")) {
			str_commandtype = ((BillCardPanel) getBillPanel()).getRealValueAt("COMMANDTYPE2"); //菜单类型
		} else if (title.equals("路径3")) {
			str_commandtype = ((BillCardPanel) getBillPanel()).getRealValueAt("COMMANDTYPE3"); //菜单类型
		}

		if (getInitRefItemVO() != null) {
			initstring = ((RefItemVO) getInitRefItemVO()).getName(); //
			if (initstring != null) {
				this.setTitle(this.getTitle() + "[" + initstring + "]"); //
			}
		}

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(getMainPanel(), BorderLayout.CENTER); //主面板!!!,所有变化都在这里!
		this.getContentPane().add(getSthPanel(), BorderLayout.SOUTH);

		if ("00".equals(str_commandtype) || "99".equals(str_commandtype)) {
			this.setSize(650, 135); //如果是自定义WorkPanel，则将窗口缩小!!!
			resetLocation();
		}
	}

	private JPanel getMainPanel() {
		if (initstring == null) {
			initstring = "";
		}

		if (str_commandtype == null) {
			JPanel panel = new JPanel(); //
			panel.add(new JLabel("请选择一个命令类型!")); //
			return panel; //
		}

		if (str_commandtype.equals("00")) { //自定义WorkpPanel
			parap = new StyleConfigPanel_00(initstring);
		} else if (str_commandtype.equals("0A")) { //Format公式面板
			parap = new StyleConfigPanel_0A(initstring);
		} else if (str_commandtype.equals("11")) { //已注册功能点!!!!
			parap = new StyleConfigPanel_11(initstring);
		} else if (str_commandtype.equals("99")) { //自己定义Frame
			parap = new StyleConfigPanel_99(initstring);
		} else { //各种风格模板(大量的,快速的)
			parap = new StyleConfigPanel_Styles(initstring); //
		}
		return parap;
	}

	private void resetLocation() {
		Frame frame = JOptionPane.getFrameForComponent(parentContainer);
		double ld_screenWidth = frame.getSize().getWidth();
		double ld_screenHeight = frame.getSize().getHeight();
		double ld_x = frame.getLocation().getX();
		double ld_y = frame.getLocation().getY();

		double ld_thisX = ld_x + ld_screenWidth / 2 - this.getSize().getWidth() / 2; //
		double ld_thisY = ld_y + ld_screenHeight / 2 - this.getSize().getHeight() / 2; //
		if (ld_thisX < 0) {
			ld_thisX = 0;
		}
		if (ld_thisY < 0) {
			ld_thisY = 0;
		}
		this.setLocation(new Double(ld_thisX).intValue(), new Double(ld_thisY).intValue()); //
	}

	public RefItemVO getReturnRefItemVO() {
		return new RefItemVO(result, result, result);
	}

	private JPanel getSthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
		btn_ok = new JButton("确定"); //
		btn_cancel = new JButton("取消"); //
		btn_close = new JButton("关闭"); //

		btn_ok.setPreferredSize(new Dimension(60, 20));
		btn_cancel.setPreferredSize(new Dimension(60, 20));
		btn_close.setPreferredSize(new Dimension(60, 20));

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

		btn_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//setResult(initstring);
				onCancel();
			}
		});

		panel.add(btn_ok);
		//panel.add(btn_cancel);
		panel.add(btn_close);
		return panel;
	}

	private boolean setResult() {
		setCloseType(1); //
		result = "";
		if (parap == null) {
			return true;
		}
		try {
			parap.stopEdit();
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
			return false; //
		}
		VectorMap pars = parap.getParameters(); //取得所有参数
		String[][] data = pars.getAllDataAsString(); //转换成字符串数组
		if (str_commandtype.equals("0A")) { //如果是格式化面板
			for (int i = 0; i < data.length; i++) { //
				if (data[i][1].equals("")) { //
					MessageBox.show(this, "请设置全部参数!"); //
					return false; //
				}
				result = result + data[i][0] + "=" + data[i][1] + ";"; //
			}
			result = result.substring(0, result.length() - 1); //
		} else { //如果其他风格
			for (int i = 0; i < data.length; i++) {
				result = result + data[i][1];
			}
		}
		return true;
	}

	private void onConfirm() {
		setCloseType(1);
		if (setResult()) {
			this.dispose();
		}
	}

	private void onCancel() {
		setCloseType(2);
		this.dispose();
	}

	public int getInitWidth() {
		return 950;
	}

	public int getInitHeight() {
		return 525;
	}

}
