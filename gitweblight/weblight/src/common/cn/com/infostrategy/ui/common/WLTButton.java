package cn.com.infostrategy.ui.common;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ButtonDefineVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.ui.mdata.BillButtonPanel;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardFrame;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListButtonActinoListener;
import cn.com.infostrategy.ui.mdata.BillListButtonClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.JepFormulaParseAtUI;
import cn.com.infostrategy.ui.mdata.WLTActionListener;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;
import cn.com.infostrategy.ui.sysapp.myfavorite.JoinMyFavoriteBtn;
import cn.com.infostrategy.ui.workflow.engine.WorkflowUIUtil;
import cn.com.infostrategy.ui.workflow.msg.PassReadBtn;

/**
 * ƽ̨��ť,�ǳ������Ķ���!!
 * ����Ҫ����!!�ȿ���ע���ƽ̨��ť,Ҳ����ֱ�Ӵ���!!!
 * �ȿ���
 * @author xch
 *
 */
public class WLTButton extends JButton implements MouseListener {

	private static final long serialVersionUID = 2752965276881657192L;

	private ButtonDefineVO btnDefineVo = null; //
	private int directType = 3; //��������

	private BillButtonPanel billButtonPanelFrom = null; //�������ĸ���ť�������
	private BillPanel billPanelFrom = null; //
	private BillFormatPanel billFormatPanelFrom = null; //�����BillFormatPanel,�򻹿���ȡ��BillFormatPanel���..

	private WLTActionListener wltActionListener = null; // 
	private ActionListener btnActionListener = null; //
	private JepFormulaParse formulaParse = null; //��ʽ����

	private boolean bo_onlyico = false;
	private ActionListener realActionListener = null; //ֱ��Ψһע��ļ����¼�!! 
	private ActionListener custActionListener = null; //ͨ��API��ע����¼�!!һ����������Զ�ǵ���,���Һ���ע��Ļ���ǰ��ע���!
	private ActionListener popMenuActionListener = null;

	private JPopupMenu popMenu = new JPopupMenu(); //
	private JMenuItem menuItem_info = new JMenuItem("�鿴��ť��Ϣ"); //
	private JMenuItem menuItem_edit = new JMenuItem("�༭��ť����"); //
	private JMenuItem menuItem_regCode = new JMenuItem("�޸�ע�����"); //

	private JMenuItem menuItem_showAllBtn = new JMenuItem("�鿴���а�ť"); //
	private JMenuItem menuItem_resetOrder = new JMenuItem("������ʾ˳��"); //

	private String regCodeStr = null; //

	//��ť����
	public static String COMM = "��ͨ��ť";

	public static String LIST_POPINSERT = "�б�������";
	public static String LIST_ROWINSERT = "�б�������";
	public static String LIST_POPEDIT = "�б����༭";
	public static String LIST_POPEDIT_ITEMS = "�б����༭ĳ����";
	public static String LIST_DELETE = "�б�ֱ��ɾ��";
	public static String LIST_DELETEROW = "�б���ɾ��";
	public static String LIST_SHOWCARD = "�б�鿴";
	public static String LIST_SHOWCARD2 = "�б�鿴_�༭��ʽ";
	public static String LIST_SAVE = "�б���";
	public static String LIST_HIDDENSHOWCOL = "�б�_����/��ʾ��";
	public static String LIST_SEARCHFROMTHIS = "�б�_����в���";
	public static String LIST_EXPORTEXCEL = "�б�_����Excel";
	public static String LIST_WEIDUSRCH = "�б�_ά�Ȳ�ѯ";
	public static String LIST_ROWMOVEUP = "�б�_������";
	public static String LIST_ROWMOVEDOWN = "�б�_������";
	public static String LIST_FAVORITE = "�б�_�����ղ�"; //�����ղ� �����/2012-09-04��
	public static String LIST_PASSREAD = "�б�_����"; //���� �����/2012-11-28��

	public static String LIST_WORKFLOW_DRAFTTASK = "�б�_�������ݸ���"; //�ݸ���
	public static String LIST_WORKFLOW_DEALTASK = "�б�_��������������"; //������
	public static String LIST_WORKFLOW_OFFTASK = "�б�_�������Ѱ�����"; //�Ѱ���
	public static String LIST_WORKFLOWPROCESS = "�б�_����������"; //����������!!
	public static String LIST_WORKFLOWMONITOR = "�б�_���������"; //���̼��!!
	public static String LIST_WORKFLOWSTART_MONITOR = "�б�_����������/���"; //���̷���/���!!

	public static String CARD_INSERT = "��Ƭ����";
	public static String CARD_EDIT = "��Ƭ�༭";
	public static String CARD_DELETE = "��Ƭɾ��";
	public static String CARD_SAVE = "��Ƭ����";

	public static String TREE_INSERT = "��������";
	public static String TREE_EDIT = "���ͱ༭";
	public static String TREE_DELETE = "����ɾ��";
	public static String TREE_SHOWCARD = "���Ͳ鿴";

	public static int BTN_HEIGHT = 23;
	private Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR); //�ȴ��Ĺ��!
	private WLTButtonUI buttonUI = null; //
	private boolean isRightBtnShowInfo = true; //

	//���ڰ�Ť���õ���������! ��ǰ��һ������ʱҪ��3���ط�,һ�Ǽ�һ����̬����,�����ڸ÷����м�һ��,�����ڵ���߼�������һ���ж�!!
	public static String[][] getSysRegButtonType() {
		return new String[][] { //
		{ WLTButton.COMM, "��ͨ��ť", WLTButton.COMM, null },//��ͨ��ť

				//�б�ϵ��
				{ WLTButton.LIST_POPINSERT, "����", WLTButton.LIST_POPINSERT, null },//�б�������
				{ WLTButton.LIST_POPEDIT, "�޸�", WLTButton.LIST_POPEDIT, null },//�б����޸�
				{ WLTButton.LIST_DELETE, "ɾ��", WLTButton.LIST_DELETE, null },//�б�ֱ��ɾ��
				{ WLTButton.LIST_SHOWCARD, "���", WLTButton.LIST_SHOWCARD, null },//�б�鿴
				{ WLTButton.LIST_ROWINSERT, "������", WLTButton.LIST_ROWINSERT, null },//�б�������
				{ WLTButton.LIST_DELETEROW, "ɾ����", WLTButton.LIST_DELETEROW, null },//�б���ɾ��
				{ WLTButton.LIST_SAVE, "����", WLTButton.LIST_SAVE, null },//�б���
				{ WLTButton.LIST_POPEDIT_ITEMS, "�༭����", WLTButton.LIST_POPEDIT_ITEMS, null },//�б����༭ĳ����
				{ WLTButton.LIST_SHOWCARD2, "�鿴�༭��ʽ", WLTButton.LIST_SHOWCARD2, null },//�б�鿴_�༭��ʽ
				{ WLTButton.LIST_HIDDENSHOWCOL, "������ʾ��", WLTButton.LIST_HIDDENSHOWCOL, null },//�б�_����/��ʾ��
				{ WLTButton.LIST_SEARCHFROMTHIS, "����в���", WLTButton.LIST_SEARCHFROMTHIS, null },//�б�_����в���
				{ WLTButton.LIST_EXPORTEXCEL, "����Excel", WLTButton.LIST_EXPORTEXCEL, null },//�б�_����Excel
				{ WLTButton.LIST_WEIDUSRCH, "��ά�Ȳ鿴", WLTButton.LIST_WEIDUSRCH, null },//�б�_ά�Ȳ�ѯ
				{ WLTButton.LIST_ROWMOVEUP, "����", WLTButton.LIST_ROWMOVEUP, null },//�б�_������
				{ WLTButton.LIST_ROWMOVEDOWN, "����", WLTButton.LIST_ROWMOVEDOWN, null },//�б�_������
				{ WLTButton.LIST_FAVORITE, "�����ղ�", WLTButton.LIST_FAVORITE, null },//�б�_�����ղ�
				{ WLTButton.LIST_PASSREAD, "����", WLTButton.LIST_PASSREAD, null },//�б�_���� �����/2012-11-28��

				{ WLTButton.LIST_WORKFLOW_DRAFTTASK, "�ݸ���", WLTButton.LIST_WORKFLOW_DRAFTTASK, "office_167.gif" },//�б�_�������ݸ��䣬ע��д���ˣ���ť���͵�NAMEҲֱ�ӿ���û�иġ����/2012-03-02��
				{ WLTButton.LIST_WORKFLOW_DEALTASK, "��������", WLTButton.LIST_WORKFLOW_DEALTASK, "office_036.gif" },//�б�_��������������
				{ WLTButton.LIST_WORKFLOW_OFFTASK, "�Ѱ�����", WLTButton.LIST_WORKFLOW_OFFTASK, "office_138.gif" },//�б�_�������Ѱ�����
				{ WLTButton.LIST_WORKFLOWPROCESS, "���̴���", WLTButton.LIST_WORKFLOWPROCESS, "zt_057.gif" },//�б�_����������
				{ WLTButton.LIST_WORKFLOWMONITOR, "���̼��", WLTButton.LIST_WORKFLOWMONITOR, "office_046.gif" },//�б�_���������
				{ WLTButton.LIST_WORKFLOWSTART_MONITOR, "���̷���/���", WLTButton.LIST_WORKFLOWSTART_MONITOR, "office_046.gif" },//�б�_����������/���

				//��Ƭϵ��
				{ WLTButton.CARD_INSERT, "����", WLTButton.CARD_INSERT, null },//��Ƭ����
				{ WLTButton.CARD_EDIT, "�༭", WLTButton.CARD_EDIT, null },//��Ƭ�༭
				{ WLTButton.CARD_DELETE, "ɾ��", WLTButton.CARD_DELETE, null },//��Ƭɾ��
				{ WLTButton.CARD_SAVE, "����", WLTButton.CARD_SAVE, null },//��Ƭ����

				//����ϵ��
				{ WLTButton.TREE_INSERT, "����", WLTButton.TREE_INSERT, null },//��������
				{ WLTButton.TREE_EDIT, "�༭", WLTButton.TREE_EDIT, null },//���ͱ༭
				{ WLTButton.TREE_DELETE, "ɾ��", WLTButton.TREE_DELETE, null },//����ɾ��
				{ WLTButton.TREE_SHOWCARD, "���", WLTButton.TREE_SHOWCARD, null }, //���Ͳ鿴
		};
	}

	/**
	 * Ĭ�Ϲ��췽������,������ֱ�Ӵ����Ť,����ʹ���в����ķ�����
	 */
	private WLTButton() {
	}

	/**
	 * ֱ��ͨ��һ���ı�����
	 * @param _text
	 */
	public WLTButton(String _text) {
		super(_text);
		this.btnDefineVo = new ButtonDefineVO(_text); //
		this.btnDefineVo.setBtntype(COMM); //
		this.btnDefineVo.setBtntext(_text); //
		this.btnDefineVo.setBtndescr("ֱ�Ӵ����İ�ť(��ϵͳע��)");
		resetSize();
	}

	public WLTButton(String _text, int _directType) {
		super(_text);
		this.directType = _directType;
		this.btnDefineVo = new ButtonDefineVO(_text); //
		this.btnDefineVo.setBtntype(COMM); //
		this.btnDefineVo.setBtntext(_text); //
		this.btnDefineVo.setBtndescr("ֱ�Ӵ����İ�ť(��ϵͳע��)");
		resetSize();
	}

	public WLTButton(String _text, BillPanel _billPanelFrom) {
		super(_text);
		this.btnDefineVo = new ButtonDefineVO("ֱ�Ӵ����İ�ť(��ϵͳע��)"); //
		this.btnDefineVo.setBtntype(COMM); //
		this.btnDefineVo.setBtntext(_text); //
		this.billPanelFrom = _billPanelFrom;
		this.btnDefineVo.setBtndescr("ֱ�Ӵ����İ�ť(��ϵͳע��)");
		resetSize();
	}

	public WLTButton(Icon icon) {
		super(icon);
		this.btnDefineVo = new ButtonDefineVO(""); //
		this.btnDefineVo.setBtntype(COMM); //
		this.btnDefineVo.setBtntext(""); //
		this.btnDefineVo.setBtndescr("ֱ��ʹ��Icon�����İ�ť(��ϵͳע��)");
		this.bo_onlyico = true; //
		if (icon instanceof ImageIcon) {
			String str_p = ((ImageIcon) icon).getDescription(); //
			this.btnDefineVo.setBtnimg(str_p); //
		}
		resetSize();
	}

	public WLTButton(Icon icon, BillPanel _billPanelFrom) {
		super(icon);
		this.btnDefineVo = new ButtonDefineVO(""); //
		this.btnDefineVo.setBtntype(COMM); //
		this.btnDefineVo.setBtntext(""); //
		this.btnDefineVo.setBtndescr("ֱ��ʹ��Icon�����İ�ť(��ϵͳע��)"); //
		this.billPanelFrom = _billPanelFrom;
		this.bo_onlyico = true; //
		resetSize();
	}

	public WLTButton(String text, String _icon) {
		this(text, UIUtil.getImage(_icon));
		this.btnDefineVo.setBtnimg(_icon); //
		this.btnDefineVo.setBtndescr("ֱ��ʹ��Icon�����İ�ť(��ϵͳע��)"); //
	}

	public WLTButton(String text, String _icon, BillPanel _billPanelFrom) {
		this(text, UIUtil.getImage(_icon));
		this.btnDefineVo.setBtnimg(_icon); //
		this.billPanelFrom = _billPanelFrom;
	}

	public WLTButton(String text, Icon icon) {
		super(text, icon);
		this.btnDefineVo = new ButtonDefineVO(text); //
		this.btnDefineVo.setBtntype(COMM); //
		this.btnDefineVo.setBtntext(text); //
		this.btnDefineVo.setBtndescr("ֱ��ʹ��Text,Icon�����İ�ť(��ϵͳע��)"); //
		if (icon instanceof ImageIcon) {
			String str_p = ((ImageIcon) icon).getDescription(); //
			this.btnDefineVo.setBtnimg(str_p); //
		}
		resetSize();
	}

	public WLTButton(String text, Icon icon, int _directType) {
		super(text, icon);
		this.directType = _directType;
		this.btnDefineVo = new ButtonDefineVO(text); //
		this.btnDefineVo.setBtntype(COMM); //
		this.btnDefineVo.setBtntext(text); //
		this.btnDefineVo.setBtndescr("ֱ��ʹ��Text,Icon�����İ�ť(��ϵͳע��)"); //
		resetSize();
	}

	/**
	 * ָ����崴��
	 * @param text
	 * @param icon
	 * @param _billPanelFrom
	 */
	public WLTButton(String text, Icon icon, BillPanel _billPanelFrom) {
		super(text, icon);
		this.btnDefineVo = new ButtonDefineVO(text); //
		this.btnDefineVo.setBtntype(COMM); //
		this.btnDefineVo.setBtntext(text); //
		this.btnDefineVo.setBtndescr("ֱ��ʹ��Text,Icon�����İ�ť(��ϵͳע��)"); //
		this.billPanelFrom = _billPanelFrom;
		resetSize();
	}

	/**
	 * 
	 * @param code
	 * @param _text
	 * @param _type
	 * @param _icon
	 */
	public WLTButton(String code, String _text, String _type, String _icon) {
		super(_text, UIUtil.getImage(_icon));
		this.btnDefineVo = new ButtonDefineVO(code); //
		this.btnDefineVo.setBtntext(_text); //
		if (_type != null && !_type.trim().equals("")) {
			this.btnDefineVo.setBtntype(_type);
		} else {
			this.btnDefineVo.setBtntype(COMM); //
		}
		this.btnDefineVo.setBtnimg(_icon);
		this.btnDefineVo.setBtndescr("ֱ��ʹ��Text,Icon�����İ�ť(��ϵͳע��)"); //
		resetSize();
	}

	/**
	 * ͨ��ע������ʹ���..
	 * 
	 * @param _btnDefineVO
	 */
	public WLTButton(ButtonDefineVO _btnDefineVO) {
		super(_btnDefineVO.getBtntext()); //���ð�ť����
		if (_btnDefineVO.getBtnimg() != null) {
			this.setIcon(UIUtil.getImage(_btnDefineVO.getBtnimg())); //
		}
		this.btnDefineVo = _btnDefineVO; //
		resetSize();
	}

	public WLTButton(ButtonDefineVO _btnDefineVO, BillPanel _billPanelFrom) {
		super(_btnDefineVO.getBtntext()); //���ð�ť����
		this.btnDefineVo = _btnDefineVO; //
		this.billPanelFrom = _billPanelFrom;
		this.resetSize();
	}

	/**
	 * ֱ��ʹ��WLTButton.LIST_POPINSERT��̬��������һ����ť!!
	 * @param _btntype
	 * @return
	 */
	public static WLTButton createButtonByType(String _btntype) {
		return createButtonByType(_btntype, null); //
	}

	/**
	 * ֱ��ʹ�þ�̬��������
	 * @param _type
	 * @return
	 */
	public static WLTButton createButtonByType(String _btntype, String _btntext) {
		String[][] str_allbtntypes = getSysRegButtonType(); //
		String str_btntype = null; //
		String str_btntext = null; //
		String str_btnimg = null; //
		String str_btndesc = null; //
		for (int i = 0; i < str_allbtntypes.length; i++) {
			if (str_allbtntypes[i][0].equals(_btntype)) { //����ҵ�
				str_btntype = _btntype; //
				if (_btntext == null) {
					str_btntext = str_allbtntypes[i][1]; //
				} else {
					str_btntext = _btntext; //
				}
				str_btnimg = str_allbtntypes[i][3]; //
				str_btndesc = "��̬����ֱ�Ӵ����İ�ť(��ϵͳע��)"; //
				break; //
			}
		}
		if (str_btntype == null) { //���û�ҵ�!!
			str_btntype = WLTButton.COMM; //
			str_btntext = "δ֪����"; //
			str_btndesc = "��̬����������δ֪��ť����[" + _btntype + "]"; //
		}
		ButtonDefineVO btnDefineVo = new ButtonDefineVO(str_btntext); //
		btnDefineVo.setBtntype(str_btntype); //
		btnDefineVo.setBtntext(str_btntext); //
		btnDefineVo.setBtnimg(str_btnimg); //ͼƬ
		btnDefineVo.setBtndescr(str_btndesc);
		return new WLTButton(btnDefineVo); //
	}

	/**
	 * ���ô�С..
	 */
	private void resetSize() {
		this.setUI(getButtonUI()); //�������Լ���UI!!!
		this.setFocusable(false); //
		this.setName(this.getText());
		if (!bo_onlyico) {
			this.setBackground(LookAndFeel.btnbgcolor); //
			//this.setForeground(Color.WHITE); //
		}

		//this.setForeground(Color.WHITE); //
		//		Font font = new Font("System", Font.PLAIN, 12); //
		Font font = LookAndFeel.font; //gaofeng
		this.setFont(font); //
		if (btnDefineVo.getBtntooltiptext() != null && !btnDefineVo.getBtntooltiptext().trim().equals("")) {
			this.setToolTipText(btnDefineVo.getBtntooltiptext()); //
		} else {
			this.setToolTipText(this.getText()); //
		}
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font); //
		//this.setFocusable(false); //
		int li_length = getBtnLength(fm, this.getText());

		if (getIcon() != null) {
			if (getText() == null || getText().trim().equals("")) {
				li_length = 20;
			} else {
				li_length = li_length + 20; //
			}
		} else {

		}
		//		if(new TBUtil().getSysOptionBooleanValue("ƽ̨�а�ť�ı߿��Ƿ�͹����ʾ", false)){
		//			this.setBorder(BorderFactory.createRaisedBevelBorder());
		//		}
		this.setMargin(new Insets(0, 0, 0, 0)); //
		this.setPreferredSize(new Dimension(li_length, this.BTN_HEIGHT));

		//������ť����¼�
		realActionListener = new WLTBActionListener();
		this.directAddActionListener(realActionListener); //����3*7=21,��ע���¼�

		//�����Ҽ������˵��߼�
		popMenuActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPopMenuClicked(e); //����Ҽ������˵�
			}
		};
		menuItem_info.setIcon(UIUtil.getImage("office_014.gif")); //
		menuItem_edit.setIcon(UIUtil.getImage("office_026.gif")); //
		menuItem_showAllBtn.setIcon(UIUtil.getImage("office_128.gif")); //
		menuItem_resetOrder.setIcon(UIUtil.getImage("office_176.gif")); //
		menuItem_regCode.setIcon(UIUtil.getImage("office_178.gif")); //

		menuItem_info.setPreferredSize(new Dimension(105, 19)); //
		menuItem_edit.setPreferredSize(new Dimension(105, 19)); //
		menuItem_showAllBtn.setPreferredSize(new Dimension(105, 19)); //
		menuItem_resetOrder.setPreferredSize(new Dimension(105, 19)); //
		menuItem_regCode.setPreferredSize(new Dimension(105, 19)); //

		menuItem_info.addActionListener(popMenuActionListener); //
		menuItem_edit.addActionListener(popMenuActionListener); //
		menuItem_showAllBtn.addActionListener(popMenuActionListener); //
		menuItem_resetOrder.addActionListener(popMenuActionListener); //
		menuItem_regCode.addActionListener(popMenuActionListener); //

		popMenu.add(menuItem_info); //
		if (ClientEnvironment.getInstance().isAdmin()) {
			popMenu.add(menuItem_edit); //
			popMenu.addSeparator(); //��һ���ָ���...
			popMenu.add(menuItem_showAllBtn); //
			popMenu.add(menuItem_resetOrder); //
			popMenu.add(menuItem_regCode); //
		}

		this.addMouseListener(this); //
	}

	//�ڲ˵����Ҽ��������Լ��ĵ���¼�
	public void addCustPopMenuItem(String _text, String _icon, ActionListener _action) {
		JMenuItem menuitem = new JMenuItem(_text);
		if (_icon != null) {
			menuitem.setIcon(UIUtil.getImage(_icon)); //
		}
		menuitem.addActionListener(_action); //
		if (popMenu != null) {
			popMenu.add(menuitem); //
		}
	}

	private WLTButtonUI getButtonUI() {
		if (buttonUI == null) {
			buttonUI = new WLTButtonUI(this.directType); //
		}
		return buttonUI; //
	}

	/**
	 * �����ť���߼�
	 */
	private void onButtontnClick(ActionEvent e) {
		Cursor oldCurSor = this.getCursor(); //
		try {
			this.setCursor(waitCursor); //���ʱ������ɵȴ�״̬!! ����ܹؼ�,�ܴ�������ͻ������!
			if (this.custActionListener != null) { //������û��Զ����¼�,����֮,����������.
				this.custActionListener.actionPerformed(e); //
				return;
			}

			String str_btntype = btnDefineVo.getBtntype(); //��ť����
			String str_btncode = btnDefineVo.getCode(); //
			String str_btntext = btnDefineVo.getBtntext(); //

			String[] str_clickingFormulas = btnDefineVo.getClickingformulaArray(); //���ǰ�¼�
			String[] str_clickedFormulas = btnDefineVo.getClickedformulaArray(); //������¼�

			//�ȴ����ù�ʽ����
			if ((str_clickingFormulas != null && str_clickingFormulas.length > 0) || (str_clickedFormulas != null && str_clickedFormulas.length > 0)) { //ֻҪ���ǰ������ʽ������һ��
				if (formulaParse == null) {
					//��ʼ��һ��format��壬��������ڣ����ֵ����null
					if (billPanelFrom != null) {
						billFormatPanelFrom = this.billPanelFrom.getLoaderBillFormatPanel();
					}

					if (billFormatPanelFrom != null) {
						formulaParse = new JepFormulaParseAtUI(this.billPanelFrom, this, billFormatPanelFrom, null, null, null); //����BillFormat��ʹ�ø��ӵĽ�����,�����Դ���Format�еĹ�ʽ,�Ǻ�������ע�ṫʽ
					} else {
						formulaParse = new JepFormulaParseAtUI(this.billPanelFrom, this); //���ǲ���ͨ��ForMatPanel����,��ֻ�ܽ���һ���Թ�ʽ
					}
				}
			}

			//��������˵��ǰ�Ĺ�ʽ,��ִ��֮!
			if (str_clickingFormulas != null && str_clickingFormulas.length > 0) { //����е��ǰ�¼�...
				execFormula(formulaParse, this.billPanelFrom, str_clickingFormulas); //ִ��ǰ�ڹ�ʽ
			}

			//ʵ���߼�
			if (str_btntype != null && !str_btntype.trim().equals("") && !str_btntype.trim().equals(COMM)) { //���ⲻ����ͨ��ť,����Ԥ���߼���,�����ƽ̨Ԥ���߼�..
				if (str_btntype.equals(LIST_POPINSERT)) { //�б�������
					onBillListPopInsert(); //
				} else if (str_btntype.equals(LIST_ROWINSERT)) { //�б�������
					onBillListInsertRow(); //
				} else if (str_btntype.equals(LIST_POPEDIT)) { //�б����༭
					onBillListPopEdit();
				} else if (str_btntype.equals(LIST_POPEDIT_ITEMS)) { //�б����༭ĳ����
					onBillListPopEditItems();
				} else if (str_btntype.equals(LIST_DELETE)) { //�б�ֱ��ɾ��
					onBillListDelete();
				} else if (str_btntype.equals(LIST_DELETEROW)) { //�б���ɾ��
					onBillListRemoveRow();
				} else if (str_btntype.equals(LIST_SHOWCARD)) { //�б�鿴(���)
					onBillListSelect();
				} else if (str_btntype.equals(LIST_SHOWCARD2)) { //�б�鿴(���,ִ�б༭��ʽ)
					onBillListSelect2();
				} else if (str_btntype.equals(LIST_SAVE)) { //�б���
					onBillListSave();
				} else if (str_btntype.equals(LIST_HIDDENSHOWCOL)) { //�б�_����/��ʾ��
					onBillListHiddenShowCol();
				} else if (str_btntype.equals(LIST_SEARCHFROMTHIS)) { //�б�_����в���!
					onBillListSearchFromThis();
				} else if (str_btntype.equals(LIST_WORKFLOW_DRAFTTASK)) { //"�б�_�������ݸ���"
					onBillListWorkFlow_DraftTask();
				} else if (str_btntype.equals(LIST_WORKFLOW_DEALTASK)) { //"�б�_��������������"
					onBillListWorkFlow_DealTask();
				} else if (str_btntype.equals(LIST_WORKFLOW_OFFTASK)) { //"�б�_�������Ѱ�����"
					onBillListWorkFlow_OffTask();
				} else if (str_btntype.equals(LIST_WORKFLOWPROCESS)) { //"�б�_����������"
					onBillListWorkFlowProcess();
				} else if (str_btntype.equals(LIST_WORKFLOWMONITOR)) { //"�б�_���������"
					onBillListWorkFlowMonitor();
				} else if (str_btntype.equals(LIST_WORKFLOWSTART_MONITOR)) { //"�б�_����������/���"
					onBillListWorkFlowStartOrMonitor();
				} else if (str_btntype.equals(LIST_EXPORTEXCEL)) { //"�б�_����Excel"
					onBillListExportExcel();
				} else if (str_btntype.equals(LIST_ROWMOVEUP)) { //"�б�_������"
					onBillListRowUp();
				} else if (str_btntype.equals(LIST_ROWMOVEDOWN)) { //"�б�_������"
					onBillListRowDown();
				} else if (str_btntype.equals(LIST_FAVORITE)) { //"�б�_�����ղ�"
					onBillListFavorite(); //�����ղ� �����/2012-09-04��
				} else if (str_btntype.equals(LIST_PASSREAD)) { //"�б�_����"
					onBillListPassRead(); //���� �����/2012-11-28��
				} else if (str_btntype.equals(CARD_INSERT)) { //"��Ƭ_����"
					onBillCardInsert();
				} else if (str_btntype.equals(CARD_EDIT)) { //"��Ƭ_�༭"
					onBillCardEdit();
				} else if (str_btntype.equals(CARD_DELETE)) { //"��Ƭ_ɾ��"
					onBillCardDelete();
				} else if (str_btntype.equals(CARD_SAVE)) { //"��Ƭ_����"
					onBillCardSave();
				} else if (str_btntype.equals(TREE_INSERT)) { //"��_����"
					onBillTreeInsert();
				} else if (str_btntype.equals(TREE_EDIT)) { //"��_�༭"
					onBillTreeEdit();
				} else if (str_btntype.equals(TREE_DELETE)) { //"��_ɾ��"
					onBillTreedDelete();
				} else if (str_btntype.equals(TREE_SHOWCARD)) { //"��_�鿴"
					onBillTreeSelect();
				} else if (str_btntype.equals(LIST_WEIDUSRCH)) {
					if (formulaParse == null) {
						//��ʼ��һ��format��壬��������ڣ����ֵ����null
						billFormatPanelFrom = this.billPanelFrom.getLoaderBillFormatPanel();

						if (billFormatPanelFrom != null) {
							formulaParse = new JepFormulaParseAtUI(this.billPanelFrom, this, billFormatPanelFrom, null, null, null); //����BillFormat��ʹ�ø��ӵĽ�����,�����Դ���Format�еĹ�ʽ,�Ǻ�������ע�ṫʽ
						} else {
							formulaParse = new JepFormulaParseAtUI(this.billPanelFrom, this); //���ǲ���ͨ��ForMatPanel����,��ֻ�ܽ���һ���Թ�ʽ
						}
					}
					execFormula(formulaParse, this.billPanelFrom, new String[] { "execWLTAction(\"cn.com.infostrategy.ui.common.WeiduSearchButton\");" }); //ִ��ǰ�ڹ�ʽ
				} else {
					throw new WLTAppException("δ֪�İ�ť����[" + str_btntype + "]");
				}
			}

			//�����ʽ..
			if (str_clickedFormulas != null && str_clickedFormulas.length > 0) { //����е��ǰ�¼�...
				execFormula(formulaParse, this.billPanelFrom, str_clickedFormulas); //ִ��ǰ�ڹ�ʽ
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		} finally {
			this.setCursor(oldCurSor); //���ָ����!
		}
	}

	/**
	 * �б�������.
	 */
	private void onBillListPopInsert() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		BillCardPanel cardPanel = new BillCardPanel(billList.templetVO); //����һ����Ƭ���
		BillCardDialog dialog = new BillCardDialog(billList, billList.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //������Ƭ������
		cardPanel.setLoaderBillFormatPanel(billList.getLoaderBillFormatPanel()); //���б��BillFormatPanel�ľ��������Ƭ
		cardPanel.insertRow(); //��Ƭ����һ��!
		cardPanel.setEditableByInsertInit(); //���ÿ�Ƭ�༭״̬Ϊ����ʱ������

		//���ǰ������
		for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
			BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
			BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, cardPanel, billList); //
			action.onBillListAddButtonClicking(event); //���ǰִ��.....
		}

		//dialog.setModal(false);  //
		dialog.setVisible(true); //��ʾ��Ƭ����
		if (dialog.getCloseType() == 1 || dialog.getCloseType() == 100) { //�����ǵ��ȷ������!����Ƭ�е����ݸ����б�!
			int li_newrow = billList.newRow(false, false); //
			billList.setBillVOAt(li_newrow, dialog.getBillVO(), false);
			billList.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //�����б���е�����Ϊ��ʼ��״̬.

			////�����������
			for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
				BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
				BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, cardPanel, billList); //
				action.onBillListAddButtonClicked(event); ////�����ִ��....
			}

			billList.setSelectedRow(li_newrow); //
		}
	}

	/**
	 * �б�������
	 * @throws Exception
	 */
	private void onBillListInsertRow() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		int li_currrow = billList.getSelectedRow(); // ȡ�õ�ǰ��
		int li_rowcount = billList.getRowCount();
		if (li_currrow < 0) { // û��ѡ�е������Ҫ���������
			if (li_rowcount > 0) {
				billList.setSelectedRow(billList.getRowCount() - 1);
			}
		}
		int newrow = billList.newRow(); //
		if (!billList.templetVO.getIsshowlistpagebar().booleanValue()) { // ����ҳ�����������һ��seq�ֶ�
			String seqfild = billList.getTempletVO().getTreeseqfield();//�������������������У����ø��ֶ����򣬷���Ĭ��Ϊseq
			if (seqfild == null || seqfild.trim().equals("")) {
				seqfild = "seq";
			}
			for (int i = 0; i < billList.getRowCount(); i++) {
				//��ǰ��������һ������������ƻ����Ʋ���������󣬷������ݲ�û�б䣬��Ϊ��ǰ����û�д������seqΪ�յ����
				if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
					billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild); //
					if (!WLTConstants.BILLDATAEDITSTATE_INSERT.equals(billList.getRowNumberEditState(i))) {
						billList.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
					}
				}
			}
		}
	}

	/**
	 * �б����༭
	 * @throws Exception
	 */
	private void onBillListPopEdit() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //

		BillVO billVO = billList.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		BillCardPanel cardPanel = new BillCardPanel(billList.templetVO);
		cardPanel.setLoaderBillFormatPanel(billList.getLoaderBillFormatPanel());
		cardPanel.setBillVO(billVO); //
		for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
			BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
			BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, cardPanel, billList); //
			action.onBillListEditButtonClicking(event); //���ǰִ��.....
		}

		BillCardDialog dialog = new BillCardDialog(billList, billList.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1 || dialog.getCloseType() == 100) {
			billList.setBillVOAt(billList.getSelectedRow(), dialog.getBillVO(), false); //
			billList.setRowStatusAs(billList.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
				BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
				BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, cardPanel, billList); //
				action.onBillListEditButtonClicked(event); //�����ִ��.....
			}
		}
	}

	//�б����༭ĳ����!!!
	private void onBillListPopEditItems() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		BillVO billVO = billList.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		String str_itemkeys = getParameter("itemkey"); //
		if (str_itemkeys == null || str_itemkeys.trim().equals("")) {
			MessageBox.show(this, "�����ڰ�ť�����ж���һ����Ϊ[itemkey]�İ�ť����!"); //
			return;
		}

		TBUtil tbUtil = new TBUtil(); //
		String[] str_itemKeyArray = tbUtil.split(str_itemkeys, ","); ////
		Pub_Templet_1VO cloneTempletVO = (Pub_Templet_1VO) new TBUtil().deepClone(billList.templetVO); ////
		for (int i = 0; i < cloneTempletVO.getItemVos().length; i++) {
			if (tbUtil.isExistInArray(cloneTempletVO.getItemVos()[i].getItemkey(), str_itemKeyArray, true)) { //�����ģ�������ڶ�����,��ǿ�ж���Ϊ��Ƭ��ʾ!
				cloneTempletVO.getItemVos()[i].setCardisshowable(Boolean.TRUE); //
			} else {
				cloneTempletVO.getItemVos()[i].setCardisshowable(Boolean.FALSE); //
			}
		}

		BillCardPanel cardPanel = new BillCardPanel(cloneTempletVO);
		cardPanel.setLoaderBillFormatPanel(billList.getLoaderBillFormatPanel());
		cardPanel.setBillVO(billVO); //

		for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
			BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
			BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, cardPanel, billList); //
			action.onBillListEditButtonClicking(event); //���ǰִ��.....
		}

		BillCardDialog dialog = new BillCardDialog(billList, cloneTempletVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			billList.setBillVOAt(billList.getSelectedRow(), dialog.getBillVO()); //
			billList.setRowStatusAs(billList.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);

			for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
				BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
				BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, cardPanel, billList); //
				action.onBillListEditButtonClicked(event); //�����ִ��.....
			}
		}
	}

	/**
	 * �б�ֱ��ɾ��
	 * @throws Exception
	 */
	private void onBillListDelete() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		int li_selRow = billList.getSelectedRow();
		if (li_selRow < 0) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (!MessageBox.confirmDel(billList)) {
			return; //
		}
		for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
			BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
			BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, billList); //
			action.onBillListDeleteButtonClicking(event); //���ǰִ��.....
		}

		//�������г�����
		billList.doDelete(true); //��������ɾ������!!!

		for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
			BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
			BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, billList); //
			action.onBillListDeleteButtonClicked(event); //���ǰִ��.....
		}
	}

	/**
	 * �б���ɾ��
	 * @throws Exception
	 */
	private void onBillListRemoveRow() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		billList.removeSelectedRows(); //
		if (!billList.templetVO.getIsshowlistpagebar().booleanValue()) { // ����ҳ�����������һ��seq�ֶ�
			String seqfild = billList.getTempletVO().getTreeseqfield();//�������������������У����ø��ֶ����򣬷���Ĭ��Ϊseq
			if (seqfild == null || seqfild.trim().equals("")) {
				seqfild = "seq";
			}
			for (int i = 0; i < billList.getRowCount(); i++) {
				//��ǰ��������һ������������ƻ����Ʋ���������󣬷������ݲ�û�б䣬��Ϊ��ǰ����û�д������seqΪ�յ����
				if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
					billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild); //
					billList.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				}
			}
		}
	}

	/**
	 * �б����鿴/���
	 * @throws Exception
	 */
	private void onBillListSelect() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		BillVO billVO = billList.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this); //Ϊ��ͳһ��ʾ��Ϣ�����޸ġ����/2012-05-02��
			return;
		}

		BillCardPanel cardPanel = new BillCardPanel(billList.templetVO); //
		cardPanel.setBillVO(billVO); //

		for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
			BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
			BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, cardPanel, billList); //
			action.onBillListLookAtButtonClicking(event); //���ǰִ��.....
		}

		String str_recordName = billVO.toString(); //
		BillCardFrame frame = new BillCardFrame(billList, billList.templetVO.getTempletname() + "[" + str_recordName + "]", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT, true);
		frame.getBtn_confirm().setVisible(false); //
		frame.getBtn_save().setVisible(false); //
		cardPanel.setEditable(false); //
		frame.setVisible(true); //
	}

	/**
	 * �б����鿴/���(ִ�б༭��ʽ)
	 * @throws Exception
	 */
	private void onBillListSelect2() throws Exception {

		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		BillVO billVO = billList.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this); //
			return;
		}

		final BillCardPanel cardPanel = new BillCardPanel(billList.templetVO); //
		cardPanel.setBillVO(billVO); //

		Pub_Templet_1_ItemVO[] templetItemVOs = cardPanel.getTempletVO().getItemVos();
		for (int i = 0; i < templetItemVOs.length; i++) { //�������пؼ�!!
			final String str_itemkey = templetItemVOs[i].getItemkey();
			String str_type = templetItemVOs[i].getItemtype();
			AbstractWLTCompentPanel compentPanel = null; //�ȶ���ÿؼ�!!!һ����©!!
			if (str_type.equals(WLTConstants.COMP_LABEL)) { //Label
			} else if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || str_type.equals(WLTConstants.COMP_NUMBERFIELD) || str_type.equals(WLTConstants.COMP_PASSWORDFIELD)) { //�ı���,���ֿ�,�����
				cardPanel.execEditFormula(str_itemkey);
			} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { //������
				cardPanel.execEditFormula(str_itemkey);
			} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || //���Ͳ���1
					str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || //���Ͳ���1
					str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //��ѡ����1
					str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || //�Զ������
					str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //�б�ģ�����
					str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //����ģ�����
					str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //ע���������
					str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //ע�����
					str_type.equals(WLTConstants.COMP_DATE) || //����
					str_type.equals(WLTConstants.COMP_DATETIME) || //ʱ��
					str_type.equals(WLTConstants.COMP_BIGAREA) || //���ı���
					//str_type.equals(WLTConstants.COMP_FILECHOOSE) || //�ļ�ѡ���!!!����ǰҲ�ǵ�һ�����մ���,�������������û�Ҫ�����õ���ҳ������,�Է��ϴ�����ҳϵͳ��Ч��,�ٵ�һ��,��
					str_type.equals(WLTConstants.COMP_COLOR) || //��ɫѡ���
					str_type.equals(WLTConstants.COMP_CALCULATE) || //������
					str_type.equals(WLTConstants.COMP_PICTURE) || //ͼƬѡ���
					str_type.equals(WLTConstants.COMP_EXCEL) || //EXCEL
					str_type.equals(WLTConstants.COMP_OFFICE) //office
			) { //����Ǹ��ֲ���
				cardPanel.execEditFormula(str_itemkey);
			} else if (str_type.equals(WLTConstants.COMP_TEXTAREA)) { //�����ı���
				cardPanel.execEditFormula(str_itemkey);
			} else if (str_type.equals(WLTConstants.COMP_BUTTON)) { //��ť
				cardPanel.execEditFormula(str_itemkey);
			} else if (str_type.equals(WLTConstants.COMP_CHECKBOX)) { //��ѡ��
				cardPanel.execEditFormula(str_itemkey);
			} else {
				continue; //
			}
		}

		for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
			BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
			BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, cardPanel, billList); //
			action.onBillListLookAtButtonClicking(event); //���ǰִ��.....
		}

		String str_recordName = billVO.toString(); //
		BillCardFrame frame = new BillCardFrame(billList, billList.templetVO.getTempletname() + "[" + str_recordName + "]", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT, true);
		frame.getBtn_confirm().setVisible(false); //
		frame.getBtn_save().setVisible(false); //
		cardPanel.setEditable(false); //
		frame.setVisible(true); //
	}

	/**
	 * �б�ֱ�ӱ���
	 * @throws Exception
	 */
	private void onBillListSave() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		if (billList.checkValidate()) {
			if (billList.saveData()) {//������ݷ����Ƿ�ɹ���������ʾ�����/2013-06-05��
				MessageBox.show(this, "����ɹ�!");
			}
		}
	}

	/**
	 * �б�����/��ʾ��
	 * @throws Exception
	 */
	private void onBillListHiddenShowCol() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		billList.reShowHideTableColumn(); //
	}

	/**
	 * ����в���
	 */
	private void onBillListSearchFromThis() {
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		billList.quickSearch(); //
	}

	//�������鿴�ݸ���
	private void onBillListWorkFlow_DraftTask() {
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		String str_orderCons = billList.getOrderCondition(); //
		String str_userid = ClientEnvironment.getCurrLoginUserVO().getId(); //�û�id
		billList.queryDataByCondition("create_userid = '" + str_userid + "' and wfprinstanceid is null", str_orderCons); //��ѯһ��
	}

	//��������!!
	private void onBillListWorkFlow_DealTask() {
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		String str_templetCode = billList.getTempletVO().getTempletcode(); //
		String str_tableName = billList.getTempletVO().getTablename(); //
		String str_savedTableName = billList.getTempletVO().getSavedtablename(); //����ı���
		String str_pkName = billList.getTempletVO().getPkname(); //
		String str_sql = new WorkflowUIUtil().getDealTaskSQL(str_templetCode, str_tableName, str_savedTableName, str_pkName, ClientEnvironment.getInstance().getLoginUserID(), null, false); //
		billList.queryDataByDS(null, str_sql, true); //ֱ��ͨ��SQL��ѯ!!
	}

	//�Ѱ�����!!
	private void onBillListWorkFlow_OffTask() {
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		String str_templetCode = billList.getTempletVO().getTempletcode(); //
		String str_tableName = billList.getTempletVO().getTablename(); //
		String str_savedTableName = billList.getTempletVO().getSavedtablename(); //����ı���
		String str_pkName = billList.getTempletVO().getPkname(); //
		String str_sql = new WorkflowUIUtil().getOffTaskSQL(str_templetCode, str_tableName, str_savedTableName, str_pkName, ClientEnvironment.getInstance().getLoginUserID(), null, false); //�Ѱ�����
		billList.queryDataByDS(null, str_sql, true); //ֱ��ͨ��SQL����!!!
	}

	/**
	 * ����������!
	 */
	private void onBillListWorkFlowProcess() {
		new cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory().dealAction("deal", (BillListPanel) this.getBillPanelFrom(), null); //������!
	}

	/**
	 * �б��������
	 * @throws Exception
	 */
	private void onBillListWorkFlowMonitor() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		BillVO billVO = billList.getSelectedBillVO(); //	
		if (billVO == null) {
			MessageBox.showSelectOne(billList); //
			return; //
		}

		if (!billVO.containsKey("wfprinstanceid")) {
			MessageBox.show(billList, "ѡ�еļ�¼��û�ж��幤�����ֶ�(wfprinstanceid)!"); //
			return; //
		}

		String str_wfprinstanceid = billVO.getStringValue("wfprinstanceid"); //
		if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) {
			MessageBox.show(billList, "��������ֵΪ��,��û����������!"); //
			return; //
		}

		cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog wfMonitorDialog = new cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog(billList, str_wfprinstanceid, billVO); //
		wfMonitorDialog.setMaxWindowMenuBar();
		wfMonitorDialog.setVisible(true); //
	}

	/**
	 * �б���������/���
	 * @throws Exception
	 */
	private void onBillListWorkFlowStartOrMonitor() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		BillVO billVO = billList.getSelectedBillVO(); //	
		if (billVO == null) {
			MessageBox.showSelectOne(billList); //
			return; //
		}
		if (!billVO.containsKey("wfprinstanceid")) {
			MessageBox.show(billList, "ѡ�еļ�¼��û�ж��幤�����ֶ�(wfprinstanceid)!"); //
			return; //
		}
		String str_wfprinstanceid = billVO.getStringValue("wfprinstanceid"); //
		if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) {//�������δ�����������̣�����������
			onBillListWorkFlowProcess();
		} else {
			cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog wfMonitorDialog = new cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog(billList, str_wfprinstanceid, billVO); //
			wfMonitorDialog.setMaxWindowMenuBar();
			wfMonitorDialog.setVisible(true); //
		}
	}

	/**
	 * �б���Excel
	 * @throws Exception
	 */
	private void onBillListExportExcel() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		billList.exportToExcel(); //��γ�Excel
	}
	/**
	 * �б�������
	 * @throws Exception
	 */
	private void onBillListRowUp() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		billList.moveUpRow(); //����
		int li_rowcount = billList.getRowCount();
		String seqfild = billList.getTempletVO().getTreeseqfield();//�������������������У����ø��ֶ����򣬷���Ĭ��Ϊseq
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		for (int i = 0; i < li_rowcount; i++) {
			//��ǰ��������һ������������ƻ����Ʋ���������󣬷������ݲ�û�б䣬��Ϊ��ǰ����û�д������seqΪ�յ���������Ҵ�����seqfildֵ�����ֵ������
			//ע��ڶ����ж���billList.getRealValueAtModel()�õ������ַ�������billList.getValueAt()�õ�����StringItemVO����
			if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
				billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild); //
				if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(billList.getRowNumberEditState(i))) {//����ǳ�ʼ״̬�����ø��£���������״̬������ִ��update���ܱ��桾���/2014-10-31��
					billList.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				}
			}
		}
	}

	/**
	 * �б�������
	 * @throws Exception
	 */
	private void onBillListRowDown() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		billList.moveDownRow(); //����
		int li_rowcount = billList.getRowCount();
		String seqfild = billList.getTempletVO().getTreeseqfield();//�������������������У����ø��ֶ����򣬷���Ĭ��Ϊseq
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		for (int i = 0; i < li_rowcount; i++) {
			//��ǰ��������һ������������ƻ����Ʋ���������󣬷������ݲ�û�б䣬��Ϊ��ǰ����û�д������seqΪ�յ����
			if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
				billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild); //
				if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(billList.getRowNumberEditState(i))) {//����ǳ�ʼ״̬�����ø��£���������״̬������ִ��update���ܱ��桾���/2014-10-31��
					billList.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				}
			}
		}
	}

	/**
	 * �б�����ղ�
	 * @throws Exception
	 */
	private void onBillListFavorite() throws Exception { //�����ղ� �����/2012-09-04��
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom();

		BillVO billVO = billList.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		new JoinMyFavoriteBtn(billList);
	}

	/**
	 * �б���
	 * @throws Exception
	 */
	private void onBillListPassRead() throws Exception { //���� �����/2012-11-28��
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom();

		BillVO billVO = billList.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		new PassReadBtn(billList, "��ͨ������Ϣ");
	}

	/**
	 * ��Ƭ����
	 * @throws Exception
	 */
	private void onBillCardInsert() throws Exception {
		checkBillCardType();
		BillCardPanel billCard = (BillCardPanel) this.getBillPanelFrom(); //
		billCard.insertRow(); //
		billCard.setEditableByInsertInit(); //
	}

	/**
	 * ��Ƭ�༭
	 * @throws Exception
	 */
	private void onBillCardEdit() throws Exception {
		checkBillCardType();
		BillCardPanel billCard = (BillCardPanel) this.getBillPanelFrom(); //
		billCard.updateCurrRow(); //
		billCard.setEditableByEditInit(); //
	}

	/**
	 * ��Ƭɾ��
	 * @throws Exception
	 */
	private void onBillCardDelete() throws Exception {
		checkBillCardType();
		BillCardPanel billCard = (BillCardPanel) this.getBillPanelFrom(); //
		if (MessageBox.showConfirmDialog(billCard, "�������ɾ���ü�¼��?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return; //
		}
		String str_sql = "delete from " + billCard.getTempletVO().getSavedtablename() + " where " + billCard.getTempletVO().getPkname() + "='" + billCard.getRealValueAt(billCard.getTempletVO().getPkname()) + "'"; //
		UIUtil.executeUpdateByDS(billCard.getDataSourceName(), str_sql); //�ύ���ݿ�
		billCard.clear();
	}

	/**
	 * ��Ƭ����
	 * @throws Exception
	 */
	private void onBillCardSave() throws Exception {
		checkBillCardType();
		BillCardPanel billCard = (BillCardPanel) this.getBillPanelFrom(); //
		if (billCard.checkValidate(billCard)) { //��ǰû����������߳���У�飡ֱ�ӱ���[����2012-03-01]
			billCard.updateData();
			MessageBox.show(this, "����ɹ�!"); //��ʾ�ɹ���
		}
	}

	/**
	 * ������
	 * @throws Exception
	 */
	private void onBillTreeInsert() throws Exception {
		checkBillTreeType();
		BillTreePanel billTree = (BillTreePanel) this.getBillPanelFrom(); //
		if (billTree.getSelectedNode() == null) {
			MessageBox.show(this, "��ѡ��һ������������������!"); //
			return;
		}

		BillVO billVO = billTree.getSelectedVO();
		BillCardPanel insertCardPanel = new BillCardPanel(billTree.getTempletVO()); //
		if (billVO != null) { //���ѡ�еĲ��Ǹ����
			insertCardPanel.insertRow(); //����һ��
			insertCardPanel.setEditableByInsertInit(); //
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) billTree.getSelectedNode();
			if (!parentNode.isRoot()) { //
				BillVO parentVO = billVO; //
				String parent_id = ((StringItemVO) parentVO.getObject(billTree.getTempletVO().getTreepk())).getStringValue(); //
				insertCardPanel.setCompentObjectValue(billTree.getTempletVO().getTreeparentpk(), new StringItemVO(parent_id)); //
			}
		} else { //���ѡ�е��Ǹ����
			insertCardPanel.insertRow(); //
			insertCardPanel.setEditableByInsertInit(); //
		}

		BillCardDialog dialog = new BillCardDialog(billTree, "����", insertCardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == BillDialog.CONFIRM) {
			BillVO newVO = insertCardPanel.getBillVO(); //
			newVO.setToStringFieldName(billTree.getTempletVO().getTreeviewfield()); //
			billTree.addNode(newVO); //
		}
	}

	/**
	 * ���༭..
	 * @throws Exception
	 */
	private void onBillTreeEdit() throws Exception {
		checkBillTreeType();
		BillTreePanel billTree = (BillTreePanel) this.getBillPanelFrom(); //
		if (billTree.getSelectedNode() == null) {
			MessageBox.show(this, "��ѡ��һ�������б༭����!"); //
			return;
		}

		BillVO billVO = billTree.getSelectedVO();
		BillCardPanel editCardPanel = new BillCardPanel(billTree.getTempletVO()); //

		if (billVO != null) { //���ѡ�еĲ��Ǹ����
			editCardPanel.queryDataByCondition("id='" + billVO.getPkValue() + "'"); //
			editCardPanel.setEditableByEditInit(); //
		} else { //���ѡ�е��Ǹ����
			MessageBox.show(this, "����㲻�ɱ༭!!"); //
			return; //���û��ѡ��һ�������ֱ�ӷ���
		}

		BillCardDialog dialog = new BillCardDialog(billTree, "�༭", editCardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == BillDialog.CONFIRM) {
			BillVO newVO = editCardPanel.getBillVO(); //
			newVO.setToStringFieldName(billTree.getTempletVO().getTreeviewfield()); //
			billTree.setBillVOForCurrSelNode(newVO); //�����л�д����
			billTree.updateUI(); //��ǰ��billTree.getJTree().updateUI();��ı�չ��������ͼ�꣬���2012-02-23�޸�
		}
	}

	/**
	 * ��ɾ��
	 * @throws Exception
	 */
	private void onBillTreedDelete() throws Exception {
		checkBillTreeType();
		BillTreePanel billTree = (BillTreePanel) this.getBillPanelFrom(); //
		if (billTree.getSelectedNode() == null) {
			MessageBox.show(this, "��ѡ��һ��������ɾ������!"); //
			return;
		}
		BillTreeNodeVO treeNodeVO = billTree.getSelectedTreeNodeVO();//��ǰ��ֱ��ɾ��ѡ�нڵ㣬���ӽڵ㲻ɾ���������ӽڵ��ٴμ���ʱ�Ҳ������ڵ�������ʾ�����ĵ�һ���Ĵ����ָ�Ϊһ��ɾ��������ڵ㣡�����/2012-02-29��
		BillVO[] childVOs = billTree.getSelectedChildPathBillVOs(); //ȡ������ѡ�е�
		if (MessageBox.showConfirmDialog(this, "��ȷ��Ҫɾ����¼��" + treeNodeVO.toString() + "����?\r\n�⽫һ��ɾ�����¹���" + childVOs.length + "���������¼,����ؽ�������!", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}
		Vector v_sqls = new Vector(); //�˵�ɾ��
		for (int i = 0; i < childVOs.length; i++) {
			v_sqls.add("delete from " + childVOs[i].getSaveTableName() + " where " + childVOs[i].getPkName() + "='" + childVOs[i].getPkValue() + "'"); //
		}
		UIUtil.executeBatchByDS(null, v_sqls); //ִ�����ݿ����!!
		billTree.delCurrNode(); //
		billTree.updateUI();
	}

	/**
	 * �������鿴/���
	 * @throws Exception
	 */
	private void onBillTreeSelect() throws Exception {
		checkBillTreeType();
		BillTreePanel billTree = (BillTreePanel) this.getBillPanelFrom(); //
		if (billTree.getSelectedNode() == null) {
			MessageBox.show(this, "��ѡ��һ�������в鿴!"); //
			return;
		}

		BillVO billVO = billTree.getSelectedVO();
		BillCardPanel editCardPanel = new BillCardPanel(billTree.getTempletVO()); //

		if (billVO != null) { //���ѡ�еĲ��Ǹ����
			editCardPanel.queryDataByCondition("id='" + billVO.getPkValue() + "'"); //
			editCardPanel.setEditable(false); //
			editCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); //
		} else { //���ѡ�е��Ǹ����
			MessageBox.show(this, "����㲻�ɲ鿴!!"); //
			return; //���û��ѡ��һ�������ֱ�ӷ���
		}

		int li_width = (int) editCardPanel.getPreferredSize().getWidth() + 30; //
		int li_height = (int) editCardPanel.getPreferredSize().getHeight() + 60; //
		if (li_width < 500) {
			li_width = 500;
		}
		if (li_width > 1000) {
			li_width = 1000;
		}

		if (li_height < 200) {
			li_height = 200;
		}
		if (li_height > 730) {
			li_height = 730;
		}

		BillDialog dialog = new BillDialog(this, "�鿴", li_width, li_height); //
		dialog.getContentPane().add(editCardPanel); //
		dialog.setVisible(true); //
	}

	/**
	 * У����������BillListPanel���Ȳ���Ϊ��,Ҳ��������Ʒ��
	 * @throws Exception
	 */
	private void checkBillListType() throws Exception {
		checkBillPanelFromIsNull();
		if (!(billPanelFrom instanceof BillListPanel)) {
			throw new WLTAppException("��岻��BillList����,�ð�ť������[" + this.btnDefineVo.getBtntype() + "],���������һ��BillList�����ܴ���Ԥ���߼�!"); //
		}
	}

	/**
	 * У����������BillListPanel���Ȳ���Ϊ��,Ҳ��������Ʒ��
	 * @throws Exception
	 */
	private void checkBillCardType() throws Exception {
		checkBillPanelFromIsNull();
		if (!(billPanelFrom instanceof BillCardPanel)) {
			throw new WLTAppException("��岻��BillCard����,�ð�ť������[" + this.btnDefineVo.getBtntype() + "],���������һ����岻��BillCard���������ܴ���Ԥ���߼�!"); //
		}
	}

	/**
	 * У����������BillListPanel���Ȳ���Ϊ��,Ҳ��������Ʒ��
	 * @throws Exception
	 */
	private void checkBillTreeType() throws Exception {
		checkBillPanelFromIsNull();
		if (!(billPanelFrom instanceof BillTreePanel)) {
			throw new WLTAppException("��岻��BillTree����,�ð�ť������[" + this.btnDefineVo.getBtntype() + "],���������һ��BillTree�����ܴ���Ԥ���߼�!"); //
		}
	}

	private void checkBillPanelFromIsNull() throws Exception {
		if (this.getBillPanelFrom() == null) {
			throw new WLTAppException("billPanelFromΪnull,û�ж���ð�ť�������ĸ����,�޷���������Ϊ[" + this.btnDefineVo.getBtntype() + "]��Ԥ���߼�!");
		}
	}

	/**
	 * ִ�й�ʽ
	 * @param _jepParse
	 * @param _billPanel
	 * @param _formulas
	 * @throws Exception
	 */
	private void execFormula(JepFormulaParse _jepParse, BillPanel _billPanel, String[] _formulas) throws Exception {
		if (_jepParse == null) {
			return;
		}
		if (_formulas != null) {
			for (int i = 0; i < _formulas.length; i++) {
				Object obj = _jepParse.execFormula(_formulas[i]); //ִ��ǰ�ι�ʽ,ִ��ĳһ����ʽ,Ȼ�󷵻�ֵ,������ص���һ���쳣,������֮
				if (obj instanceof Exception) {
					throw (Exception) obj; //
				}
			}
		}
	}

	/**
	 * ֱ������¼�,�÷�����˽�е�,��
	 * @param l
	 */
	private void directAddActionListener(ActionListener l) {
		super.addActionListener(l);
	}

	/**
	 * �ع������¼�����,�����һ��ע�ᰴť������"Ԥ���߼�,��ִ��ǰ��ʽ,ִ�к�ʽ"���߼�,��������Ա������ֱ��ͨ����APIע���¼�
	 * ��ôAPIע��ļ����߽������ǰ�����߼�,����Զֻ��֤�����һ��ע�����Ч!
	 * �����ζ��WLTButton��Զֻ��һ����������!!��������߶����������Լ�����!!!
	 */
	@Override
	public void addActionListener(ActionListener l) {
		this.custActionListener = l; //
	}

	/**
	 * ���Ӱ�ť�Զ�������¼�
	 * һ���������Զ����¼�,��ƽ̨ע����¼������������Ч.
	 * ��Ϊ��ʱһ��ģ����ע��İ�ť������ĵط����õ�,����ĵط����¼��ֲ���ƽ̨��ע�ᶯ��,�����Լ����¼�,��ʱ������ܾ�������!
	 * ���仰˵���ǽ��ð�ť��������Ȩ��,������¼����߼���Ҫ�Լ���!
	 * @param _custActionListener
	 */
	public void addCustActionListener(ActionListener _custActionListener) {
		this.custActionListener = _custActionListener; //
	}

	public ActionListener getCustActionListener() {
		return custActionListener;
	}

	/**
	 * �Ҽ������˵���ʾ.
	 */
	private void onPopMenuClicked(ActionEvent e) {
		if (e.getSource() == menuItem_info) {
			onShowBtnInfo(); //�鿴��ť��Ϣ
		} else if (e.getSource() == menuItem_edit) { //�༭ע�ᰴť
			onEditRegisterBtn(); //
		} else if (e.getSource() == menuItem_showAllBtn) {
			onShowAllBtnInfo(); //��ʾ���а�ť��Ϣ
		} else if (e.getSource() == menuItem_resetOrder) {
			onResetBillPanelOrderSeq(); //���ð�ť������е�λ��.
		} else if (e.getSource() == menuItem_regCode) { //ע����
			onUpdateRegCode(); //�޸�ע����!!!
		}
	}

	/**
	 * ȡ�ñ���ť��������Ҫ��Ϣ!
	 * @return
	 */
	public String getButtonInfoMsg() {
		StringBuilder sb_info = new StringBuilder(); //
		sb_info.append("��ť����=��" + convertNullStr(this.btnDefineVo.getCode()) + "��\r\n"); //
		sb_info.append("��ť����=��" + convertNullStr(this.btnDefineVo.getBtntext()) + "��\r\n"); //
		sb_info.append("��ť����=��" + convertNullStr(this.btnDefineVo.getBtntype()) + "��\r\n"); //
		sb_info.append("�Ƿ�ע�ᰴť=��" + this.btnDefineVo.isRegisterBtn() + "��\r\n"); //

		sb_info.append("��ťͼ��=[" + convertNullStr(this.btnDefineVo.getBtnimg()) + "]\r\n"); //
		sb_info.append("��ť��ǩ=[" + convertNullStr(this.btnDefineVo.getBtntooltiptext()) + "]\r\n"); //
		sb_info.append("��ť˵��=[" + convertNullStr(this.btnDefineVo.getBtndescr()) + "]\r\n"); //

		sb_info.append("���ǰ��ʽ=[" + convertNullStr(this.btnDefineVo.getClickingformula()) + "]\r\n"); //
		sb_info.append("�����ʽ=[" + convertNullStr(this.btnDefineVo.getClickedformula()) + "]\r\n"); //

		sb_info.append("����ĸ�λ=[" + convertNullStr(this.btnDefineVo.getAllowposts()) + "]\r\n"); //
		sb_info.append("����Ľ�ɫ=[" + convertNullStr(this.btnDefineVo.getAllowroles()) + "]\r\n"); //
		sb_info.append("�������Ա=[" + convertNullStr(this.btnDefineVo.getAllowusers()) + "]\r\n"); //
		sb_info.append("Ȩ�޼�����=��" + convertNullStr(this.btnDefineVo.getAllowResult()) + "��\r\n"); //
		sb_info.append("��ڼ�����=��" + getAllActionListenerNames() + "��\r\n"); //
		sb_info.append("ת����ʵ���߼���=��" + (this.custActionListener == null ? "" : (custActionListener.getClass().getName())) + "��\r\n"); //
		sb_info.append("������BillButtonPanel=��" + getObjectClassName(getBillButtonPanelFrom()) + "��\r\n");
		sb_info.append("������BillPanel=��" + getObjectClassName(getBillPanelFrom()) + "��\r\n");
		sb_info.append("������BillFormatPanel=��" + getObjectClassName(getBillFormatPanelFrom()) + "��\r\n");

		sb_info.append("\r\n���и�����·��(����ʹ��):\r\n"); //
		int li_count = 1;
		sb_info.append("(" + li_count + ")" + this.getClass().getName() + "\r\n"); //
		java.awt.Container par = this;
		StringBuilder sb_clsParent = new StringBuilder(); //
		while (par.getParent() != null) {
			String str_clasName = par.getParent().getClass().getName(); //
			li_count++;
			sb_clsParent.append("(" + li_count + ")" + str_clasName + "\r\n"); //
			par = par.getParent(); //
		}
		sb_clsParent.append("ʹ��BillCardPanel card = (BillCardPanel) SwingUtilities.getAncestorOfClass(BillCardPanel.class, this);���Եõ�ĳ���������");
		sb_info.append(sb_clsParent.toString()); //

		return sb_info.toString(); //
	}

	/**
	 * ��ʾ��ť��Ϣ
	 */
	private void onShowBtnInfo() {
		MessageBox.showTextArea(this, "��ť��Ϣ", getButtonInfoMsg(), 500, 400); //
	}

	/**
	 * ���ٱ༭��ť����
	 */
	private void onEditRegisterBtn() {
		if (!this.btnDefineVo.isRegisterBtn()) {
			MessageBox.show(this, "����ע���͵İ�ť,��ֱ����ӵ�,���ܽ��б༭����!"); //
			return;
		}

		BillCardPanel cardPanel = new BillCardPanel("pub_regbuttons_CODE1"); //
		cardPanel.queryDataByCondition("code='" + this.btnDefineVo.getCode() + "'"); //
		BillCardDialog dialog = new BillCardDialog(this, "�༭��ť����", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		cardPanel.setEditable("code", false); //�༭�ǲ��ܸĵ�,�޸ı༭������ģ�������и�
		dialog.setVisible(true); //

		if (dialog.getCloseType() == BillDialog.CONFIRM) {
			this.btnDefineVo.setClickingformula(cardPanel.getRealValueAt("clickingformula")); //
			this.btnDefineVo.setClickedformula(cardPanel.getRealValueAt("clickedformula")); //
			this.btnDefineVo.setBtntype(cardPanel.getRealValueAt("btntype")); //��ť����
			this.btnDefineVo.setBtntext(cardPanel.getRealValueAt("btntext")); //
			this.setText(this.btnDefineVo.getBtntext()); //
		}
	}

	/**
	 * ��ʾ��ť���(BillButtonPanel)�����а�ť����Ϣ,����ð�ť������һ����ť����еĻ�
	 */
	private void onShowAllBtnInfo() {
		BillButtonPanel btnPanel = this.getBillButtonPanelFrom();
		if (btnPanel == null) {
			MessageBox.show(this, "�ð�ť������һ����ť���в��ܲ鿴�����ֵܰ�ť��Ϣ!"); //
			return;
		}

		WLTButton[] allBtns = btnPanel.getAllButtons(); //
		StringBuilder sb_allInfo = new StringBuilder(); //
		sb_allInfo.append("һ����[" + allBtns.length + "]���İ�ť.\r\n");
		for (int i = 0; i < allBtns.length; i++) {
			sb_allInfo.append("*********************��" + (i + 1) + "��*********************\r\n");
			sb_allInfo.append(allBtns[i].getButtonInfoMsg());
			sb_allInfo.append("\r\n");
		}
		MessageBox.showTextArea(this, "���а�ť��Ϣ", sb_allInfo.toString(), 500, 400); //
	}

	public String getRegCodeStr() {
		return regCodeStr;
	}

	public void setRegCodeStr(String regCodeStr) {
		this.regCodeStr = regCodeStr;
	}

	/**
	 * ���ð�ť˳��
	 */
	private void onResetBillPanelOrderSeq() {
		MessageBox.showTextArea(this, "���ð�ť��ʾ˳��,������..."); //
	}

	/**
	 * �޸�ע����!!
	 */
	private void onUpdateRegCode() {
		try {
			JPanel panel = WLTPanel.createDefaultPanel(null); //
			String str_oldImeName = null; //
			if (btnDefineVo != null && btnDefineVo.getBtnimg() != null) {
				str_oldImeName = btnDefineVo.getBtnimg(); //
			}
			CardCPanel_Ref refReg = new CardCPanel_Ref("btnimg", "��ťͼƬ", "", "ͼƬѡ���", 100, 150, new RefItemVO(str_oldImeName, null, str_oldImeName), null); //
			refReg.setBounds(10, 10, 350, 25); //
			panel.add(refReg); //

			BillDialog idalog = new BillDialog(this, "�޸�ͼƬ", 400, 120);
			idalog.getContentPane().add(panel); //
			idalog.addConfirmButtonPanel(); //
			idalog.setVisible(true); //
			if (idalog.getCloseType() == 1) {
				String str_newImgName = refReg.getTextField().getText(); //
				if (str_newImgName == null || str_newImgName.equals("")) {
					MessageBox.show(this, "ͼƬΪ��,��ѡ��һ��ͼƬ!"); //
					return;
				}
				String str_count = UIUtil.getStringValueByDS(null, "select count(*) from pub_option where parkey='" + this.regCodeStr + "'"); //
				int li_count = Integer.parseInt(str_count); //
				if (li_count > 0) { //�����,���޸�
					UIUtil.executeUpdateByDSPS(null, "update pub_option set parvalue='" + str_newImgName + "' where parkey='" + regCodeStr + "'"); //
					reloadCache(); //
					MessageBox.show(null, "ϵͳ���в���,�޸��²���Ϊ[" + str_newImgName + "],��ˢ�»���,�����µ�¼����!"); //
				} else { //
					String str_newid = UIUtil.getSequenceNextValByDS(null, "S_PUB_OPTION"); //
					UIUtil.executeUpdateByDSPS(null, "insert into pub_option (id,parkey,parvalue) values (" + str_newid + ",'" + regCodeStr + "','" + str_newImgName + "')"); //
					reloadCache(); //
					MessageBox.show(null, "ϵͳ��û�в���,��������Ϊ[" + str_newImgName + "],��ˢ�»���,�����µ�¼����!"); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private void reloadCache() throws Exception {
		String[][] str_result = UIUtil.getCommonService().reLoadDataFromDB(false); //
		if (str_result != null) {
			ClientEnvironment.setClientSysOptions(UIUtil.getCommonService().getAllOptions()); //���µõ����в���
		}
	}

	/**
	 * ȡ��һ�����������
	 * @param _obj
	 * @return
	 */
	private String getObjectClassName(Object _obj) {
		if (_obj == null) {
			return "";
		}
		String str_className = _obj.getClass().getName(); //
		str_className = str_className.substring(str_className.lastIndexOf(".") + 1, str_className.length()); //
		return str_className + "<" + _obj.hashCode() + ">"; //
	}

	/**
	 * ���м��������ơ�
	 * @return
	 */
	public String getAllActionListenerNames() {
		ActionListener[] allActions = this.getActionListeners(); //
		StringBuilder sb_text = new StringBuilder(); //
		if (allActions != null && allActions.length > 0) {
			for (int i = 0; i < allActions.length; i++) { //
				sb_text.append(allActions[i].getClass().getName()); //
				if (allActions.length > 1) {
					sb_text.append(";"); //
				}
			}
		}
		return sb_text.toString(); //
	}

	/**
	 * ȡ�ð�ť�������
	 * @return
	 */
	public ButtonDefineVO getBtnDefineVo() {
		return btnDefineVo;
	}

	/**
	 * ȡ�ð�ť����Ĳ���!!!
	 * @param _key
	 * @return
	 */
	public String getParameter(String _key) {
		if (getBtnDefineVo() == null) {
			return null;
		}
		String str_pardef = getBtnDefineVo().getBtnpars(); //
		if (str_pardef == null || str_pardef.trim().equals("")) {
			return null;
		}
		HashMap map = new TBUtil().convertStrToMapByExpress(str_pardef, ";", "="); //
		return (String) map.get(_key); //
	}

	private String convertNullStr(String _str) {
		if (_str == null) {
			return "";
		}
		return _str;
	}

	@Override
	public void updateUI() {
		this.setUI(getButtonUI()); //
		this.revalidate(); //
		this.repaint(); //
	}

	/**
	 * ȡ�ð�ť�Ŀ��
	 * @param _fm
	 * @param _text
	 * @return
	 */
	private int getBtnLength(FontMetrics _fm, String _text) {
		int li_length = SwingUtilities.computeStringWidth(_fm, _text);
		li_length = li_length + 20; //
		if (li_length > 200) {
			li_length = 200;
		}
		return li_length; //
	}

	public BillButtonPanel getBillButtonPanelFrom() {
		return billButtonPanelFrom;
	}

	public void setBillButtonPanelFrom(BillButtonPanel billButtonPanelFrom) {
		this.billButtonPanelFrom = billButtonPanelFrom;
	}

	public BillPanel getBillPanelFrom() {
		return billPanelFrom;
	}

	public void setBillPanelFrom(BillPanel billPanelFrom) {
		this.billPanelFrom = billPanelFrom;
	}

	public BillFormatPanel getBillFormatPanelFrom() {
		return billFormatPanelFrom;
	}

	public void setBillFormatPanelFrom(BillFormatPanel billFormatPanelFrom) {
		this.billFormatPanelFrom = billFormatPanelFrom;
	}

	/**
	 * ����Ҽ�ʱ�����˵�.
	 */
	public void mouseClicked(MouseEvent e) {
		if (isRightBtnShowInfo) {
			if (e.getButton() == MouseEvent.BUTTON3) { //���������Ҽ�
				if (regCodeStr != null) {
					menuItem_regCode.setVisible(true); //
				} else {
					menuItem_regCode.setVisible(false); //
				}
				this.popMenu.show(this, e.getX(), e.getY()); //��ʾ�����˵�!!..
			}
		}
	}

	/**
	 * �������ʱ,Ӧ�ñ仯�߿�,����ʾһ�ֶ���.
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * ����Ƴ�ʱ,Ӧ�ñ仯�߿�,����ʾһ�ֶ���.
	 */
	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void setRightBtnShowInfo(boolean isRightBtnShowInfo) {
		this.isRightBtnShowInfo = isRightBtnShowInfo;
	}

	/**
	 * ��ť�����¼�..
	 * @author xch
	 */
	class WLTBActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			onButtontnClick(e); //Ĭ�Ͽ϶������߼���!!
		}
	}

}
