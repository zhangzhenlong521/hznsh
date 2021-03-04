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
 * 将历史意见拷贝过来
 * @author Administrator
 *
 */
public class CopyHistMsgDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = -6507817796258173261L;
	private String str_msg = null; //
	private String str_msg_real = null; //加密前的真实数据!如果不加密,则与前者一样!
	private String msgViewReason = null; //可被查看或被隐藏的原因!!
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
			this.setTitle(this.getTitle() + " 【CopyHistMsgDialog】"); //
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

		//先看客户端缓存是否有特殊标记?如果没有,则算了!如果有,则根据表名计算出该对象可以被哪些超级管理员查看(需要新增这个方法)? 然后匹配是否存在?
		//如果存在,则直接解密!如果不存在,则跳过,不做处理! 等待手工点击!!!
		String str_queryTable = null; //
		String str_saveTable = null; //
		if ("*****".equals(str_msg) && this.billVO != null) { //如果意见是加密的!
			str_queryTable = this.billVO.getQueryTableName(); //
			str_saveTable = this.billVO.getSaveTableName(); //
			boolean isCanDo = new SysUIUtil().isCanDoAsSuperAdmin(this, str_queryTable, str_saveTable, true); //先是安静模式计算一下!必须是安静模式,否则第一个就弹出一个框很不好!
			if (isCanDo) {
				this.textArea.setText(this.str_msg_real); //
				textArea.setForeground(Color.RED); //
				btn_unencry.setEnabled(false); //
			}
		}
	}

	private JPanel getSouthPanel() {
		WLTPanel panel = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new FlowLayout(FlowLayout.CENTER), LookAndFeel.defaultShadeColor1, false); //
		btn_copy = new WLTButton("复制到我的意见中"); //
		btn_confirm = new WLTButton(" 确  定 ");//  
		btn_reason = new WLTButton("(非)加密原因"); //
		btn_unencry = new WLTButton("解密意见"); //

		btn_confirm.addCustPopMenuItem("查看加密意见", "office_131.gif", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onViewReason();  //
			}

		}); //

		btn_reason.setToolTipText("有些环节意见对我是加密的(我无权查看),点击这个按钮可以了解为什么对我加密,或为什么我能查看之!有参数可以定义其隐藏!"); //
		btn_unencry.setToolTipText("如果你是超级管理员,则可以将加密意进行解密查看!但有的管理员需要输入指定密码!有参数可以定义其隐藏!");

		btn_copy.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		btn_reason.addActionListener(this); //
		btn_unencry.addActionListener(this); //

		if (isCanCopy) {
			panel.add(btn_copy); //
		}
		panel.add(btn_confirm); //
		panel.add(btn_reason); //

		if ("*****".equals(str_msg)) { //如果是*号,则说明是加密了,才有解密按钮,也就是说,如果没有加密意见!则没有必要存在解密按钮!
			panel.add(btn_unencry); //	
		}

		//邮储客户看不懂加密按钮到底是干啥的,强烈要求去掉! 实际上在以后使用过程中肯定还是要的!
		//admin=Y永远显示,必须在维护过程中知道到底是因为什么而有权利查看的!!!
		if (!ClientEnvironment.isAdmin() && !TBUtil.getTBUtil().getSysOptionBooleanValue("工作流查看意见时是否显示加密解密按钮", true)) {
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
	 * 解密!
	 */
	private void onUnEncry() {
		String str_queryTable = null; //
		String str_saveTable = null; //
		if (this.billVO != null) {
			str_queryTable = this.billVO.getQueryTableName(); //
			str_saveTable = this.billVO.getSaveTableName(); //
		}
		boolean isCanDo = new SysUIUtil().isCanDoAsSuperAdmin(this, str_queryTable, str_saveTable); //看是否可以超级管理员身份查看???
		if (isCanDo) { //如果可以!
			this.textArea.setText(this.str_msg_real); //
			textArea.setForeground(Color.RED); //
			btn_unencry.setEnabled(false); //
		}
	}

}
