package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.sysapp.SysUIUtil;

/**
 * ����ʷ�����������
 * @author Administrator
 *
 */
public class CopyHistMsgDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = -6507817796258173261L;
	private String str_msg = null; //
	private String str_msg_real = null; //����ǰ����ʵ����!���������,����ǰ��һ��!
	private String msgViewReason = null; //�ɱ��鿴�����ص�ԭ��!!
	private JTextArea textArea = null; //
	private WLTButton btn_copy, btn_confirm, btn_reason, btn_unencry; //
	private boolean isCanCopy = true; //
	private BillVO billVO = null; //

	public CopyHistMsgDialog(Container owner, String title, String _msg, boolean _isCanCopy) throws HeadlessException {
		this(owner, title, _msg, _msg, null, _isCanCopy, null); //
	}

	public CopyHistMsgDialog(Container owner, String title, String _msg, String _msg_real, String _viewReason, boolean _isCanCopy, BillVO _billVO) throws HeadlessException {
		super(owner, title, 550, 300);
		if (ClientEnvironment.isAdmin()) {
			this.setTitle(this.getTitle() + " ��CopyHistMsgDialog��"); //
		}
		this.str_msg = _msg; //
		this.str_msg_real = _msg_real; //
		this.msgViewReason = _viewReason; //
		this.isCanCopy = _isCanCopy; //
		this.billVO = _billVO; //
		initialize(); //
	}

	private void initialize() {
		textArea = new WLTTextArea(str_msg); //
		textArea.setEditable(false); //
		textArea.setLineWrap(true); //
		textArea.setBackground(new Color(250, 252, 250)); //
		JScrollPane scrollPanel = new JScrollPane(textArea); //
		scrollPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); //
		scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); //

		this.getContentPane().add(scrollPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //

		//�ȿ��ͻ��˻����Ƿ���������?���û��,������!�����,����ݱ���������ö�����Ա���Щ��������Ա�鿴(��Ҫ�����������)? Ȼ��ƥ���Ƿ����?
		//�������,��ֱ�ӽ���!���������,������,��������! �ȴ��ֹ����!!!
		String str_queryTable = null; //
		String str_saveTable = null; //
		if ("*****".equals(str_msg) && this.billVO != null) { //�������Ǽ��ܵ�!
			str_queryTable = this.billVO.getQueryTableName(); //
			str_saveTable = this.billVO.getSaveTableName(); //
			boolean isCanDo = new SysUIUtil().isCanDoAsSuperAdmin(this, str_queryTable, str_saveTable, true); //���ǰ���ģʽ����һ��!�����ǰ���ģʽ,�����һ���͵���һ����ܲ���!
			if (isCanDo) {
				this.textArea.setText(this.str_msg_real); //
				textArea.setForeground(Color.RED); //
				btn_unencry.setEnabled(false); //
			}
		}
	}

	private JPanel getSouthPanel() {
		WLTPanel panel = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new FlowLayout(FlowLayout.CENTER), LookAndFeel.defaultShadeColor1, false); //
		btn_copy = new WLTButton("���Ƶ��ҵ������"); //
		btn_confirm = new WLTButton(" ȷ  �� ");//  
		btn_reason = new WLTButton("(��)����ԭ��"); //
		btn_unencry = new WLTButton("�������"); //

		btn_confirm.addCustPopMenuItem("�鿴�������", "office_131.gif", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onViewReason();  //
			}

		}); //

		btn_reason.setToolTipText("��Щ������������Ǽ��ܵ�(����Ȩ�鿴),��������ť�����˽�Ϊʲô���Ҽ���,��Ϊʲô���ܲ鿴֮!�в������Զ���������!"); //
		btn_unencry.setToolTipText("������ǳ�������Ա,����Խ���������н��ܲ鿴!���еĹ���Ա��Ҫ����ָ������!�в������Զ���������!");

		btn_copy.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		btn_reason.addActionListener(this); //
		btn_unencry.addActionListener(this); //

		if (isCanCopy) {
			panel.add(btn_copy); //
		}
		panel.add(btn_confirm); //
		panel.add(btn_reason); //

		if ("*****".equals(str_msg)) { //�����*��,��˵���Ǽ�����,���н��ܰ�ť,Ҳ����˵,���û�м������!��û�б�Ҫ���ڽ��ܰ�ť!
			panel.add(btn_unencry); //	
		}

		//�ʴ��ͻ����������ܰ�ť�����Ǹ�ɶ��,ǿ��Ҫ��ȥ��! ʵ�������Ժ�ʹ�ù����п϶�����Ҫ��!
		//admin=Y��Զ��ʾ,������ά��������֪����������Ϊʲô����Ȩ���鿴��!!!
		if (!ClientEnvironment.isAdmin() && !TBUtil.getTBUtil().getSysOptionBooleanValue("�������鿴���ʱ�Ƿ���ʾ���ܽ��ܰ�ť", true)) {
			btn_reason.setVisible(false);
			btn_unencry.setVisible(false);
		}
		return panel; //
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_copy) {
			onCopy(); //
		} else if (_event.getSource() == btn_confirm) {
			onConfirm(); //
		} else if (_event.getSource() == btn_reason) {
			onViewReason(); //
		} else if (_event.getSource() == btn_unencry) {
			onUnEncry(); //
		}
	}

	private void onCopy() {
		this.setCloseType(1); //
		this.dispose(); //
	}

	private void onConfirm() {
		this.setCloseType(2); //
		this.dispose(); //
	}

	private void onViewReason() {
		MessageBox.show(this, this.msgViewReason); //
	}

	/**
	 * ����!
	 */
	private void onUnEncry() {
		String str_queryTable = null; //
		String str_saveTable = null; //
		if (this.billVO != null) {
			str_queryTable = this.billVO.getQueryTableName(); //
			str_saveTable = this.billVO.getSaveTableName(); //
		}
		boolean isCanDo = new SysUIUtil().isCanDoAsSuperAdmin(this, str_queryTable, str_saveTable); //���Ƿ���Գ�������Ա��ݲ鿴???
		if (isCanDo) { //�������!
			this.textArea.setText(this.str_msg_real); //
			textArea.setForeground(Color.RED); //
			btn_unencry.setEnabled(false); //
		}
	}

}
