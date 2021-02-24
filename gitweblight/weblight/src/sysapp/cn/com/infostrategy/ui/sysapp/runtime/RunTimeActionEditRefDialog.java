package cn.com.infostrategy.ui.sysapp.runtime;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.MultiStyleTextPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 在线编辑代码的生成动态执行代码的参照配置器!!!
 * 这将是以后平台非常大的一个亮点,可以说是可能以后大量逻辑都是在线编码!!!将平台变成了一个开发工具!!
 * 动态代码与公式相辅相成,可以弥补公式的不足!!!功能也更强大!!!
 * 
 * 需要增加:显示帮助的两页签! 可以方便复制方法名!!! 还可以查看主要类,比如TBUtil,String,等相关方法!! 还提供一些常用的算法结构!!!
 * @author Administrator
 *
 */
public class RunTimeActionEditRefDialog extends AbstractRefDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JTextField textField = null; //
	private MultiStyleTextPanel textArea_code; //
	private WLTButton btn_confirm, btn_cancel; //

	private RefItemVO returnRefItemVO = null; //

	public RunTimeActionEditRefDialog(Container _parent, String _title, RefItemVO _refItemVO, BillPanel _billPanelFrom, String _templetName) {
		super(_parent, _title, _refItemVO, _billPanelFrom);
	}

	@Override
	public void initialize() {
		try {
			textField = new JTextField(); //
			textField.setEditable(false); //
			textField.setText("对应的抽象类方法,比如cn.com.infostrategy.bs.mdata.BillDataBSFilterIFC.filterBillData(HashVO[] _hvs)"); //

			//代码中部
			textArea_code = new MultiStyleTextPanel(); //
			textArea_code.setFont(LookAndFeel.font); //
			textArea_code.addKeyWordStyle(new String[] { "for", "if(", "else", "(", ")", }, Color.RED, false); //
			textArea_code.addKeyWordStyle(new String[] { "{", "}", "return" }, Color.BLUE, false); //
			textArea_code.addKeyWordStyle(new String[] { "//" }, Color.GREEN, false); //

			this.getContentPane().add(textField, BorderLayout.NORTH); ////
			this.getContentPane().add(new JScrollPane(textArea_code), BorderLayout.CENTER); ////
			this.getContentPane().add(getSouthButtonPanel(), BorderLayout.SOUTH); ////

			if (getInitRefItemVO() != null && getInitRefItemVO().getId() != null) {
				textArea_code.setText(getInitRefItemVO().getId()); //初始化值!
			}
		} catch (Exception ex) {
			MessageBox.showException(this.getParentContainer(), ex); //
		}
	}

	private JPanel getSouthButtonPanel() {
		JPanel panel = new JPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");

		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this);

		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;//
	}

	@Override
	public int getInitHeight() {
		return 500; //
	}

	@Override
	public int getInitWidth() {
		return 600;
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm(); //确定!!!!!
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //取消!!!!!
		}
	}

	/**
	 * 确定!!!
	 */
	private void onConfirm() {
		this.setCloseType(1); //
		returnRefItemVO = new RefItemVO(textArea_code.getText(), null, textArea_code.getText()); ////
		this.dispose(); //
	}

	/**
	 * 取消!!!
	 */
	private void onCancel() {
		this.setCloseType(2); //
		returnRefItemVO = null; //
		this.dispose(); //
	}

}
