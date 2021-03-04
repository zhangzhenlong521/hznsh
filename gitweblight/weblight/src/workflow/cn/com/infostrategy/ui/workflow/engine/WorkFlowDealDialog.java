package cn.com.infostrategy.ui.workflow.engine;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.workflow.engine.DealTaskVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTTextArea;

/**
 * ���������˹�ѡ������������!!!
 * ���������ǰ������Ŀ��������Ĵ�����棬������һ����������ʾ���в������
 * �����ĳ��µķ�ʽ��,��workFlowProcessDialog
 * @author xch
 *
 */
public class WorkFlowDealDialog extends BillDialog implements ActionListener, MouseListener {

	private static final long serialVersionUID = 8323211408012241414L;
	private BillVO billVO = null; //ҵ������VO.
	private WFParVO firstTaskVO = null; //����
	private String str_dealtype = null; //�������ͣ���Submit,Reject,Cancel��֮��

	private JComboBox combobox_selectedUser = null; //
	private JComboBox combobox_reasonCode = null; //ԭ��
	private JTextArea textArea;

	private JButton btn_confirm, btn_cancel; ////
	private int closetype = -1; //

	/**
	 * 
	 * @param _parent
	 * @param _firstTaskVO
	 */
	public WorkFlowDealDialog(Container _parent, BillVO _billVO, WFParVO _firstTaskVO, String _dealtype) {
		super(_parent, "WorkFlow Deal", 355, 350); //
		this.billVO = _billVO; //
		this.firstTaskVO = _firstTaskVO; //
		this.str_dealtype = _dealtype; //
		this.setResizable(true); //
		initialize();
	}

	private void initialize() {
		this.getContentPane().setLayout(null); //

		int li_y = 5; //
		if (this.firstTaskVO.isIsprocessed() && this.firstTaskVO.isIsassignapprover()) //������ս���,���˹�ѡ�������,����Ҫ�г�������
		{
			JPanel panel_user = getUserPanel(); //
			panel_user.setBounds(5, li_y, 340, 30); //
			this.getContentPane().add(panel_user); //
			li_y = li_y + 35; //
		}

		if (this.firstTaskVO.isIsneedmsg()) { //���������������
			if (this.firstTaskVO.getReasonCodeComBoxItemVOs() != null) { //�����reasonCodeSQL����
				JPanel panel_reasoncode = getReasoncodePanel(this.firstTaskVO.getReasonCodeComBoxItemVOs()); //
				panel_reasoncode.setBounds(5, li_y, 340, 30); //
				this.getContentPane().add(panel_reasoncode); //
				li_y = li_y + 35; //
			}

			JPanel panel_msg = getMsgPanel(); ////....
			panel_msg.setBounds(5, li_y, 340, 145); //
			this.getContentPane().add(panel_msg); //
			li_y = li_y + 150; //
		}

		JPanel southpanel = getSouthPanel();
		southpanel.setBounds(5, li_y, 310, 25); //
		li_y = li_y + 35; //
		this.getContentPane().add(southpanel); //
		this.setSize(445, li_y + 25); //
	}

	private JPanel getUserPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new FlowLayout(FlowLayout.LEFT)); //
		JLabel label = new JLabel("�ύ����:", JLabel.RIGHT); //
		label.setPreferredSize(new Dimension(80, 20));

		combobox_selectedUser = new JComboBox(); //���������˵�..
		combobox_selectedUser.setPreferredSize(new Dimension(120, 20)); ////
		//		if (firstTaskVO.getDealTaskVOs() != null) {  ////
		//			for (int i = 0; i < firstTaskVO.getDealTaskVOs().length; i++) {  //
		//				combobox_selectedUser.addItem(firstTaskVO.getDealTaskVOs()[i]); //
		//				combobox_selectedUser.setSelectedIndex(i); //Ĭ��ѡ�������
		//			}
		//		}

		panel.setPreferredSize(new Dimension(200, 20));
		panel.add(label); //
		panel.addMouseListener(this); //
		panel.add(combobox_selectedUser);

		if (firstTaskVO.getCurractivityCheckUserPanel() != null && !firstTaskVO.getCurractivityCheckUserPanel().trim().equals("")) {
			try {
				AbstractWorkFlowCheckUserPanel checkUserPanel = (AbstractWorkFlowCheckUserPanel) Class.forName(firstTaskVO.getCurractivityCheckUserPanel()).newInstance(); //
				checkUserPanel.setWorkFlowDialog(this); //
				panel.add(checkUserPanel); //
				panel.setPreferredSize(new Dimension(320, 30));
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}
		return panel;
	}

	private JPanel getReasoncodePanel(ComBoxItemVO[] _comBoxItemVOs) {
		JPanel panel = new JPanel(); //
		panel.setLayout(new FlowLayout(FlowLayout.LEFT)); //
		JLabel label = new JLabel("Reason Code:", JLabel.RIGHT); //
		label.setPreferredSize(new Dimension(80, 20));

		combobox_reasonCode = new JComboBox(); //���������˵�...
		for (int i = 0; i < _comBoxItemVOs.length; i++) {
			combobox_reasonCode.addItem(_comBoxItemVOs[i]); ////..
		}

		combobox_reasonCode.setPreferredSize(new Dimension(200, 20)); //
		panel.setPreferredSize(new Dimension(260, 20)); //
		panel.add(label); //
		panel.add(combobox_reasonCode);
		return panel;
	}

	public void setSelectUser(String _userid) {
		int li_count = combobox_selectedUser.getItemCount();
		for (int i = 0; i < li_count; i++) {
			if (combobox_selectedUser.getItemAt(i) != null) {
				DealTaskVO vo = (DealTaskVO) combobox_selectedUser.getItemAt(i);
				if (vo.getParticipantUserId().equals(_userid)) {
					combobox_selectedUser.setSelectedIndex(i);
					break; //
				}
			}
		}

	}

	/**
	 * ��Ϣ���
	 * @return
	 */
	private JPanel getMsgPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new FlowLayout(FlowLayout.LEFT)); //
		JLabel label = new JLabel("���:", JLabel.RIGHT); //
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setPreferredSize(new Dimension(80, 100)); //

		textArea = new WLTTextArea();
		textArea.setLineWrap(true); //
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setPreferredSize(new Dimension(250, 100)); //

		panel.setPreferredSize(new Dimension(320, 100)); //
		panel.add(label); //
		panel.add(scrollPane); //
		return panel; //
	}

	/**
	 * ��ť���
	 * @return
	 */
	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new FlowLayout()); //
		btn_confirm = new WLTButton(UIUtil.getLanguage("ȷ��")); //
		btn_cancel = new WLTButton(UIUtil.getLanguage("ȡ��")); //
		btn_confirm.setPreferredSize(new Dimension(70, 20)); //
		btn_cancel.setPreferredSize(new Dimension(70, 20)); //
		btn_confirm.addActionListener(this); //
		btn_cancel.addActionListener(this); //

		panel.setPreferredSize(new Dimension(310, 20)); //
		panel.add(btn_confirm);
		panel.add(btn_cancel);
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm(); //ȷ��
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //ȡ��
		}
	}

	private void onConfirm() {
		if (this.firstTaskVO.isIsprocessed()) //������ս���
		{
			if (this.firstTaskVO.isIsassignapprover()) { //�����Ҫ�˹�ѡ�������,�����ҳ����ѡ��ļ�¼����!
				int li_row = combobox_selectedUser.getSelectedIndex();
				if (li_row < 0) {
					MessageBox.show(this, "Please select an user!"); //
					return;
				}

				this.firstTaskVO.setSelectedRow(li_row); //����ѡ�е���!
			}
		}

		////..
		if (combobox_reasonCode != null) {
			this.firstTaskVO.setSelectedReasonCode(combobox_reasonCode.getSelectedIndex()); //
		}

		//���������!
		if (this.firstTaskVO.isIsneedmsg()) {
			this.firstTaskVO.setMessage(textArea.getText()); //��������
		}

		closetype = 1;
		this.dispose(); //
	}

	private void onCancel() {
		closetype = 2;
		this.dispose(); //
	}

	public int getClosetype() {
		return closetype;
	}

	public WFParVO getReturnVO() {
		return firstTaskVO;
	}

	/**
	 * ѡ��һ���û�..
	 */
	private void onCheckUser() {
		String str_checkuserDialog = firstTaskVO.getCurractivityCheckUserPanel(); //
		MessageBox.show(this, str_checkuserDialog); //
	}

	public void mouseClicked(MouseEvent e) {
		if (ClientEnvironment.getInstance().isAdmin()) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				DealTaskVO vo = (DealTaskVO) combobox_selectedUser.getSelectedItem();
				if (vo != null) {
					StringBuffer sb_msg = new StringBuffer();
					sb_msg.append("FromActivity:[" + vo.getFromActivityId() + "," + vo.getFromActivityName() + "]\r\n"); //
					sb_msg.append("Transition:[" + vo.getTransitionId() + "," + vo.getTransitionName() + "]\r\n"); //
					sb_msg.append("CurrActivity:[" + vo.getCurrActivityId() + "," + vo.getCurrActivityName() + "]\r\n"); //
					sb_msg.append("ParticipantUser:" + vo.getParticipantUserName() + "\r\n"); //
					MessageBox.show(this, sb_msg.toString()); //
				}
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	public JComboBox getCombobox_selectedUser() {
		return combobox_selectedUser;
	}

	public void setCombobox_selectedUser(JComboBox combobox_selectedUser) {
		this.combobox_selectedUser = combobox_selectedUser;
	}

	public JTextArea getTextArea() {
		return textArea;
	}

	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}

	public BillVO getBillVO() {
		return billVO;
	}

	public void setBillVO(BillVO billVO) {
		this.billVO = billVO;
	}

}
