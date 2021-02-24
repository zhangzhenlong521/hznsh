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
 * ���߱༭��������ɶ�ִ̬�д���Ĳ���������!!!
 * �⽫���Ժ�ƽ̨�ǳ����һ������,����˵�ǿ����Ժ�����߼��������߱���!!!��ƽ̨�����һ����������!!
 * ��̬�����빫ʽ�ศ���,�����ֲ���ʽ�Ĳ���!!!����Ҳ��ǿ��!!!
 * 
 * ��Ҫ����:��ʾ��������ҳǩ! ���Է��㸴�Ʒ�����!!! �����Բ鿴��Ҫ��,����TBUtil,String,����ط���!! ���ṩһЩ���õ��㷨�ṹ!!!
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
			textField.setText("��Ӧ�ĳ����෽��,����cn.com.infostrategy.bs.mdata.BillDataBSFilterIFC.filterBillData(HashVO[] _hvs)"); //

			//�����в�
			textArea_code = new MultiStyleTextPanel(); //
			textArea_code.setFont(LookAndFeel.font); //
			textArea_code.addKeyWordStyle(new String[] { "for", "if(", "else", "(", ")", }, Color.RED, false); //
			textArea_code.addKeyWordStyle(new String[] { "{", "}", "return" }, Color.BLUE, false); //
			textArea_code.addKeyWordStyle(new String[] { "//" }, Color.GREEN, false); //

			this.getContentPane().add(textField, BorderLayout.NORTH); ////
			this.getContentPane().add(new JScrollPane(textArea_code), BorderLayout.CENTER); ////
			this.getContentPane().add(getSouthButtonPanel(), BorderLayout.SOUTH); ////

			if (getInitRefItemVO() != null && getInitRefItemVO().getId() != null) {
				textArea_code.setText(getInitRefItemVO().getId()); //��ʼ��ֵ!
			}
		} catch (Exception ex) {
			MessageBox.showException(this.getParentContainer(), ex); //
		}
	}

	private JPanel getSouthButtonPanel() {
		JPanel panel = new JPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");

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
			onConfirm(); //ȷ��!!!!!
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //ȡ��!!!!!
		}
	}

	/**
	 * ȷ��!!!
	 */
	private void onConfirm() {
		this.setCloseType(1); //
		returnRefItemVO = new RefItemVO(textArea_code.getText(), null, textArea_code.getText()); ////
		this.dispose(); //
	}

	/**
	 * ȡ��!!!
	 */
	private void onCancel() {
		this.setCloseType(2); //
		returnRefItemVO = null; //
		this.dispose(); //
	}

}
