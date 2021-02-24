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
 * 平台按钮,非常讲究的对象!!
 * 背景要渐变!!既可以注册成平台按钮,也可以直接创建!!!
 * 既可以
 * @author xch
 *
 */
public class WLTButton extends JButton implements MouseListener {

	private static final long serialVersionUID = 2752965276881657192L;

	private ButtonDefineVO btnDefineVo = null; //
	private int directType = 3; //渐变类型

	private BillButtonPanel billButtonPanelFrom = null; //从属于哪个按钮栏面板中
	private BillPanel billPanelFrom = null; //
	private BillFormatPanel billFormatPanelFrom = null; //如果是BillFormatPanel,则还可以取得BillFormatPanel句柄..

	private WLTActionListener wltActionListener = null; // 
	private ActionListener btnActionListener = null; //
	private JepFormulaParse formulaParse = null; //公式引擎

	private boolean bo_onlyico = false;
	private ActionListener realActionListener = null; //直接唯一注册的监听事件!! 
	private ActionListener custActionListener = null; //通过API再注册的事件!!一旦有了则永远是调它,而且后来注册的会冲掉前面注册的!
	private ActionListener popMenuActionListener = null;

	private JPopupMenu popMenu = new JPopupMenu(); //
	private JMenuItem menuItem_info = new JMenuItem("查看按钮信息"); //
	private JMenuItem menuItem_edit = new JMenuItem("编辑按钮属性"); //
	private JMenuItem menuItem_regCode = new JMenuItem("修改注册参数"); //

	private JMenuItem menuItem_showAllBtn = new JMenuItem("查看所有按钮"); //
	private JMenuItem menuItem_resetOrder = new JMenuItem("重置显示顺序"); //

	private String regCodeStr = null; //

	//按钮类型
	public static String COMM = "普通按钮";

	public static String LIST_POPINSERT = "列表弹出新增";
	public static String LIST_ROWINSERT = "列表行新增";
	public static String LIST_POPEDIT = "列表弹出编辑";
	public static String LIST_POPEDIT_ITEMS = "列表弹出编辑某几项";
	public static String LIST_DELETE = "列表直接删除";
	public static String LIST_DELETEROW = "列表行删除";
	public static String LIST_SHOWCARD = "列表查看";
	public static String LIST_SHOWCARD2 = "列表查看_编辑公式";
	public static String LIST_SAVE = "列表保存";
	public static String LIST_HIDDENSHOWCOL = "列表_隐藏/显示列";
	public static String LIST_SEARCHFROMTHIS = "列表_结果中查找";
	public static String LIST_EXPORTEXCEL = "列表_导出Excel";
	public static String LIST_WEIDUSRCH = "列表_维度查询";
	public static String LIST_ROWMOVEUP = "列表_行上移";
	public static String LIST_ROWMOVEDOWN = "列表_行下移";
	public static String LIST_FAVORITE = "列表_加入收藏"; //加入收藏 【杨科/2012-09-04】
	public static String LIST_PASSREAD = "列表_传阅"; //传阅 【杨科/2012-11-28】

	public static String LIST_WORKFLOW_DRAFTTASK = "列表_工作流草稿箱"; //草稿箱
	public static String LIST_WORKFLOW_DEALTASK = "列表_工作流待办任务"; //待办箱
	public static String LIST_WORKFLOW_OFFTASK = "列表_工作流已办任务"; //已办箱
	public static String LIST_WORKFLOWPROCESS = "列表_工作流处理"; //工作流处理!!
	public static String LIST_WORKFLOWMONITOR = "列表_工作流监控"; //流程监控!!
	public static String LIST_WORKFLOWSTART_MONITOR = "列表_工作流发起/监控"; //流程发起/监控!!

	public static String CARD_INSERT = "卡片新增";
	public static String CARD_EDIT = "卡片编辑";
	public static String CARD_DELETE = "卡片删除";
	public static String CARD_SAVE = "卡片保存";

	public static String TREE_INSERT = "树型新增";
	public static String TREE_EDIT = "树型编辑";
	public static String TREE_DELETE = "树型删除";
	public static String TREE_SHOWCARD = "树型查看";

	public static int BTN_HEIGHT = 23;
	private Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR); //等待的光标!
	private WLTButtonUI buttonUI = null; //
	private boolean isRightBtnShowInfo = true; //

	//给在按扭配置的下拉框用! 当前增一种类型时要改3个地方,一是加一个静态变量,二是在该方法中加一项,三是在点击逻辑中增加一个判断!!
	public static String[][] getSysRegButtonType() {
		return new String[][] { //
		{ WLTButton.COMM, "普通按钮", WLTButton.COMM, null },//普通按钮

				//列表系列
				{ WLTButton.LIST_POPINSERT, "新增", WLTButton.LIST_POPINSERT, null },//列表弹出新增
				{ WLTButton.LIST_POPEDIT, "修改", WLTButton.LIST_POPEDIT, null },//列表弹出修改
				{ WLTButton.LIST_DELETE, "删除", WLTButton.LIST_DELETE, null },//列表直接删除
				{ WLTButton.LIST_SHOWCARD, "浏览", WLTButton.LIST_SHOWCARD, null },//列表查看
				{ WLTButton.LIST_ROWINSERT, "新增行", WLTButton.LIST_ROWINSERT, null },//列表行新增
				{ WLTButton.LIST_DELETEROW, "删除行", WLTButton.LIST_DELETEROW, null },//列表行删除
				{ WLTButton.LIST_SAVE, "保存", WLTButton.LIST_SAVE, null },//列表保存
				{ WLTButton.LIST_POPEDIT_ITEMS, "编辑几项", WLTButton.LIST_POPEDIT_ITEMS, null },//列表弹出编辑某几项
				{ WLTButton.LIST_SHOWCARD2, "查看编辑公式", WLTButton.LIST_SHOWCARD2, null },//列表查看_编辑公式
				{ WLTButton.LIST_HIDDENSHOWCOL, "隐藏显示列", WLTButton.LIST_HIDDENSHOWCOL, null },//列表_隐藏/显示列
				{ WLTButton.LIST_SEARCHFROMTHIS, "结果中查找", WLTButton.LIST_SEARCHFROMTHIS, null },//列表_结果中查找
				{ WLTButton.LIST_EXPORTEXCEL, "导出Excel", WLTButton.LIST_EXPORTEXCEL, null },//列表_导出Excel
				{ WLTButton.LIST_WEIDUSRCH, "分维度查看", WLTButton.LIST_WEIDUSRCH, null },//列表_维度查询
				{ WLTButton.LIST_ROWMOVEUP, "上移", WLTButton.LIST_ROWMOVEUP, null },//列表_行上移
				{ WLTButton.LIST_ROWMOVEDOWN, "下移", WLTButton.LIST_ROWMOVEDOWN, null },//列表_行下移
				{ WLTButton.LIST_FAVORITE, "加入收藏", WLTButton.LIST_FAVORITE, null },//列表_加入收藏
				{ WLTButton.LIST_PASSREAD, "传阅", WLTButton.LIST_PASSREAD, null },//列表_传阅 【杨科/2012-11-28】

				{ WLTButton.LIST_WORKFLOW_DRAFTTASK, "草稿箱", WLTButton.LIST_WORKFLOW_DRAFTTASK, "office_167.gif" },//列表_工作流草稿箱，注释写错了，按钮类型的NAME也直接拷贝没有改【李春娟/2012-03-02】
				{ WLTButton.LIST_WORKFLOW_DEALTASK, "待办任务", WLTButton.LIST_WORKFLOW_DEALTASK, "office_036.gif" },//列表_工作流待办任务
				{ WLTButton.LIST_WORKFLOW_OFFTASK, "已办任务", WLTButton.LIST_WORKFLOW_OFFTASK, "office_138.gif" },//列表_工作流已办任务
				{ WLTButton.LIST_WORKFLOWPROCESS, "流程处理", WLTButton.LIST_WORKFLOWPROCESS, "zt_057.gif" },//列表_工作流处理
				{ WLTButton.LIST_WORKFLOWMONITOR, "流程监控", WLTButton.LIST_WORKFLOWMONITOR, "office_046.gif" },//列表_工作流监控
				{ WLTButton.LIST_WORKFLOWSTART_MONITOR, "流程发起/监控", WLTButton.LIST_WORKFLOWSTART_MONITOR, "office_046.gif" },//列表_工作流发起/监控

				//卡片系列
				{ WLTButton.CARD_INSERT, "新增", WLTButton.CARD_INSERT, null },//卡片新增
				{ WLTButton.CARD_EDIT, "编辑", WLTButton.CARD_EDIT, null },//卡片编辑
				{ WLTButton.CARD_DELETE, "删除", WLTButton.CARD_DELETE, null },//卡片删除
				{ WLTButton.CARD_SAVE, "保存", WLTButton.CARD_SAVE, null },//卡片保存

				//树型系列
				{ WLTButton.TREE_INSERT, "新增", WLTButton.TREE_INSERT, null },//树型新增
				{ WLTButton.TREE_EDIT, "编辑", WLTButton.TREE_EDIT, null },//树型编辑
				{ WLTButton.TREE_DELETE, "删除", WLTButton.TREE_DELETE, null },//树型删除
				{ WLTButton.TREE_SHOWCARD, "浏览", WLTButton.TREE_SHOWCARD, null }, //树型查看
		};
	}

	/**
	 * 默认构造方法禁用,即不让直接创建铵扭,必须使用有参数的方法。
	 */
	private WLTButton() {
	}

	/**
	 * 直接通过一个文本创建
	 * @param _text
	 */
	public WLTButton(String _text) {
		super(_text);
		this.btnDefineVo = new ButtonDefineVO(_text); //
		this.btnDefineVo.setBtntype(COMM); //
		this.btnDefineVo.setBtntext(_text); //
		this.btnDefineVo.setBtndescr("直接创建的按钮(非系统注册)");
		resetSize();
	}

	public WLTButton(String _text, int _directType) {
		super(_text);
		this.directType = _directType;
		this.btnDefineVo = new ButtonDefineVO(_text); //
		this.btnDefineVo.setBtntype(COMM); //
		this.btnDefineVo.setBtntext(_text); //
		this.btnDefineVo.setBtndescr("直接创建的按钮(非系统注册)");
		resetSize();
	}

	public WLTButton(String _text, BillPanel _billPanelFrom) {
		super(_text);
		this.btnDefineVo = new ButtonDefineVO("直接创建的按钮(非系统注册)"); //
		this.btnDefineVo.setBtntype(COMM); //
		this.btnDefineVo.setBtntext(_text); //
		this.billPanelFrom = _billPanelFrom;
		this.btnDefineVo.setBtndescr("直接创建的按钮(非系统注册)");
		resetSize();
	}

	public WLTButton(Icon icon) {
		super(icon);
		this.btnDefineVo = new ButtonDefineVO(""); //
		this.btnDefineVo.setBtntype(COMM); //
		this.btnDefineVo.setBtntext(""); //
		this.btnDefineVo.setBtndescr("直接使用Icon创建的按钮(非系统注册)");
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
		this.btnDefineVo.setBtndescr("直接使用Icon创建的按钮(非系统注册)"); //
		this.billPanelFrom = _billPanelFrom;
		this.bo_onlyico = true; //
		resetSize();
	}

	public WLTButton(String text, String _icon) {
		this(text, UIUtil.getImage(_icon));
		this.btnDefineVo.setBtnimg(_icon); //
		this.btnDefineVo.setBtndescr("直接使用Icon创建的按钮(非系统注册)"); //
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
		this.btnDefineVo.setBtndescr("直接使用Text,Icon创建的按钮(非系统注册)"); //
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
		this.btnDefineVo.setBtndescr("直接使用Text,Icon创建的按钮(非系统注册)"); //
		resetSize();
	}

	/**
	 * 指定面板创建
	 * @param text
	 * @param icon
	 * @param _billPanelFrom
	 */
	public WLTButton(String text, Icon icon, BillPanel _billPanelFrom) {
		super(text, icon);
		this.btnDefineVo = new ButtonDefineVO(text); //
		this.btnDefineVo.setBtntype(COMM); //
		this.btnDefineVo.setBtntext(text); //
		this.btnDefineVo.setBtndescr("直接使用Text,Icon创建的按钮(非系统注册)"); //
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
		this.btnDefineVo.setBtndescr("直接使用Text,Icon创建的按钮(非系统注册)"); //
		resetSize();
	}

	/**
	 * 通过注册的类型创建..
	 * 
	 * @param _btnDefineVO
	 */
	public WLTButton(ButtonDefineVO _btnDefineVO) {
		super(_btnDefineVO.getBtntext()); //设置按钮文字
		if (_btnDefineVO.getBtnimg() != null) {
			this.setIcon(UIUtil.getImage(_btnDefineVO.getBtnimg())); //
		}
		this.btnDefineVo = _btnDefineVO; //
		resetSize();
	}

	public WLTButton(ButtonDefineVO _btnDefineVO, BillPanel _billPanelFrom) {
		super(_btnDefineVO.getBtntext()); //设置按钮文字
		this.btnDefineVo = _btnDefineVO; //
		this.billPanelFrom = _billPanelFrom;
		this.resetSize();
	}

	/**
	 * 直接使用WLTButton.LIST_POPINSERT静态变量创建一个按钮!!
	 * @param _btntype
	 * @return
	 */
	public static WLTButton createButtonByType(String _btntype) {
		return createButtonByType(_btntype, null); //
	}

	/**
	 * 直接使用静态变量创建
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
			if (str_allbtntypes[i][0].equals(_btntype)) { //如果找到
				str_btntype = _btntype; //
				if (_btntext == null) {
					str_btntext = str_allbtntypes[i][1]; //
				} else {
					str_btntext = _btntext; //
				}
				str_btnimg = str_allbtntypes[i][3]; //
				str_btndesc = "静态方法直接创建的按钮(非系统注册)"; //
				break; //
			}
		}
		if (str_btntype == null) { //如果没找到!!
			str_btntype = WLTButton.COMM; //
			str_btntext = "未知类型"; //
			str_btndesc = "静态方法创建的未知按钮类型[" + _btntype + "]"; //
		}
		ButtonDefineVO btnDefineVo = new ButtonDefineVO(str_btntext); //
		btnDefineVo.setBtntype(str_btntype); //
		btnDefineVo.setBtntext(str_btntext); //
		btnDefineVo.setBtnimg(str_btnimg); //图片
		btnDefineVo.setBtndescr(str_btndesc);
		return new WLTButton(btnDefineVo); //
	}

	/**
	 * 重置大小..
	 */
	private void resetSize() {
		this.setUI(getButtonUI()); //必须用自己的UI!!!
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
		//		if(new TBUtil().getSysOptionBooleanValue("平台中按钮的边框是否凸出显示", false)){
		//			this.setBorder(BorderFactory.createRaisedBevelBorder());
		//		}
		this.setMargin(new Insets(0, 0, 0, 0)); //
		this.setPreferredSize(new Dimension(li_length, this.BTN_HEIGHT));

		//监听按钮点击事件
		realActionListener = new WLTBActionListener();
		this.directAddActionListener(realActionListener); //不管3*7=21,先注册事件

		//增加右键弹出菜单逻辑
		popMenuActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPopMenuClicked(e); //点击右键弹出菜单
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
			popMenu.addSeparator(); //加一个分隔条...
			popMenu.add(menuItem_showAllBtn); //
			popMenu.add(menuItem_resetOrder); //
			popMenu.add(menuItem_regCode); //
		}

		this.addMouseListener(this); //
	}

	//在菜单的右键中增加自己的点击事件
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
	 * 点击按钮的逻辑
	 */
	private void onButtontnClick(ActionEvent e) {
		Cursor oldCurSor = this.getCursor(); //
		try {
			this.setCursor(waitCursor); //点击时将光标变成等待状态!! 这个很关键,能大大提升客户体验度!
			if (this.custActionListener != null) { //如果有用户自定义事件,则处理之,并立即返回.
				this.custActionListener.actionPerformed(e); //
				return;
			}

			String str_btntype = btnDefineVo.getBtntype(); //按钮类型
			String str_btncode = btnDefineVo.getCode(); //
			String str_btntext = btnDefineVo.getBtntext(); //

			String[] str_clickingFormulas = btnDefineVo.getClickingformulaArray(); //点击前事件
			String[] str_clickedFormulas = btnDefineVo.getClickedformulaArray(); //点击后事件

			//先创建好公式引擎
			if ((str_clickingFormulas != null && str_clickingFormulas.length > 0) || (str_clickedFormulas != null && str_clickedFormulas.length > 0)) { //只要点击前与点击后公式定义了一个
				if (formulaParse == null) {
					//初始化一下format面板，如果不存在，这个值将是null
					if (billPanelFrom != null) {
						billFormatPanelFrom = this.billPanelFrom.getLoaderBillFormatPanel();
					}

					if (billFormatPanelFrom != null) {
						formulaParse = new JepFormulaParseAtUI(this.billPanelFrom, this, billFormatPanelFrom, null, null, null); //如是BillFormat则使用复杂的解释器,即可以处理Format中的公式,是后面三个注册公式
					} else {
						formulaParse = new JepFormulaParseAtUI(this.billPanelFrom, this); //如是不是通过ForMatPanel构建,则只能解析一般性公式
					}
				}
			}

			//如果定义了点击前的公式,则执行之!
			if (str_clickingFormulas != null && str_clickingFormulas.length > 0) { //如果有点击前事件...
				execFormula(formulaParse, this.billPanelFrom, str_clickingFormulas); //执行前期公式
			}

			//实际逻辑
			if (str_btntype != null && !str_btntype.trim().equals("") && !str_btntype.trim().equals(COMM)) { //如题不是普通按钮,即有预置逻辑的,则调用平台预置逻辑..
				if (str_btntype.equals(LIST_POPINSERT)) { //列表弹出新增
					onBillListPopInsert(); //
				} else if (str_btntype.equals(LIST_ROWINSERT)) { //列表行新增
					onBillListInsertRow(); //
				} else if (str_btntype.equals(LIST_POPEDIT)) { //列表弹出编辑
					onBillListPopEdit();
				} else if (str_btntype.equals(LIST_POPEDIT_ITEMS)) { //列表弹出编辑某几项
					onBillListPopEditItems();
				} else if (str_btntype.equals(LIST_DELETE)) { //列表直接删除
					onBillListDelete();
				} else if (str_btntype.equals(LIST_DELETEROW)) { //列表行删除
					onBillListRemoveRow();
				} else if (str_btntype.equals(LIST_SHOWCARD)) { //列表查看(浏览)
					onBillListSelect();
				} else if (str_btntype.equals(LIST_SHOWCARD2)) { //列表查看(浏览,执行编辑公式)
					onBillListSelect2();
				} else if (str_btntype.equals(LIST_SAVE)) { //列表保存
					onBillListSave();
				} else if (str_btntype.equals(LIST_HIDDENSHOWCOL)) { //列表_隐藏/显示列
					onBillListHiddenShowCol();
				} else if (str_btntype.equals(LIST_SEARCHFROMTHIS)) { //列表_结果中查找!
					onBillListSearchFromThis();
				} else if (str_btntype.equals(LIST_WORKFLOW_DRAFTTASK)) { //"列表_工作流草稿箱"
					onBillListWorkFlow_DraftTask();
				} else if (str_btntype.equals(LIST_WORKFLOW_DEALTASK)) { //"列表_工作流待办任务"
					onBillListWorkFlow_DealTask();
				} else if (str_btntype.equals(LIST_WORKFLOW_OFFTASK)) { //"列表_工作流已办任务"
					onBillListWorkFlow_OffTask();
				} else if (str_btntype.equals(LIST_WORKFLOWPROCESS)) { //"列表_工作流处理"
					onBillListWorkFlowProcess();
				} else if (str_btntype.equals(LIST_WORKFLOWMONITOR)) { //"列表_工作流监控"
					onBillListWorkFlowMonitor();
				} else if (str_btntype.equals(LIST_WORKFLOWSTART_MONITOR)) { //"列表_工作流发起/监控"
					onBillListWorkFlowStartOrMonitor();
				} else if (str_btntype.equals(LIST_EXPORTEXCEL)) { //"列表_导出Excel"
					onBillListExportExcel();
				} else if (str_btntype.equals(LIST_ROWMOVEUP)) { //"列表_行上移"
					onBillListRowUp();
				} else if (str_btntype.equals(LIST_ROWMOVEDOWN)) { //"列表_行下移"
					onBillListRowDown();
				} else if (str_btntype.equals(LIST_FAVORITE)) { //"列表_加入收藏"
					onBillListFavorite(); //加入收藏 【杨科/2012-09-04】
				} else if (str_btntype.equals(LIST_PASSREAD)) { //"列表_传阅"
					onBillListPassRead(); //传阅 【杨科/2012-11-28】
				} else if (str_btntype.equals(CARD_INSERT)) { //"卡片_新增"
					onBillCardInsert();
				} else if (str_btntype.equals(CARD_EDIT)) { //"卡片_编辑"
					onBillCardEdit();
				} else if (str_btntype.equals(CARD_DELETE)) { //"卡片_删除"
					onBillCardDelete();
				} else if (str_btntype.equals(CARD_SAVE)) { //"卡片_保存"
					onBillCardSave();
				} else if (str_btntype.equals(TREE_INSERT)) { //"树_新增"
					onBillTreeInsert();
				} else if (str_btntype.equals(TREE_EDIT)) { //"树_编辑"
					onBillTreeEdit();
				} else if (str_btntype.equals(TREE_DELETE)) { //"树_删除"
					onBillTreedDelete();
				} else if (str_btntype.equals(TREE_SHOWCARD)) { //"树_查看"
					onBillTreeSelect();
				} else if (str_btntype.equals(LIST_WEIDUSRCH)) {
					if (formulaParse == null) {
						//初始化一下format面板，如果不存在，这个值将是null
						billFormatPanelFrom = this.billPanelFrom.getLoaderBillFormatPanel();

						if (billFormatPanelFrom != null) {
							formulaParse = new JepFormulaParseAtUI(this.billPanelFrom, this, billFormatPanelFrom, null, null, null); //如是BillFormat则使用复杂的解释器,即可以处理Format中的公式,是后面三个注册公式
						} else {
							formulaParse = new JepFormulaParseAtUI(this.billPanelFrom, this); //如是不是通过ForMatPanel构建,则只能解析一般性公式
						}
					}
					execFormula(formulaParse, this.billPanelFrom, new String[] { "execWLTAction(\"cn.com.infostrategy.ui.common.WeiduSearchButton\");" }); //执行前期公式
				} else {
					throw new WLTAppException("未知的按钮类型[" + str_btntype + "]");
				}
			}

			//点击后公式..
			if (str_clickedFormulas != null && str_clickedFormulas.length > 0) { //如果有点击前事件...
				execFormula(formulaParse, this.billPanelFrom, str_clickedFormulas); //执行前期公式
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		} finally {
			this.setCursor(oldCurSor); //最后恢复光标!
		}
	}

	/**
	 * 列表弹出新增.
	 */
	private void onBillListPopInsert() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		BillCardPanel cardPanel = new BillCardPanel(billList.templetVO); //创建一个卡片面板
		BillCardDialog dialog = new BillCardDialog(billList, billList.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //弹出卡片新增框
		cardPanel.setLoaderBillFormatPanel(billList.getLoaderBillFormatPanel()); //将列表的BillFormatPanel的句柄传给卡片
		cardPanel.insertRow(); //卡片新增一行!
		cardPanel.setEditableByInsertInit(); //设置卡片编辑状态为新增时的设置

		//点击前拦截器
		for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
			BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
			BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, cardPanel, billList); //
			action.onBillListAddButtonClicking(event); //点击前执行.....
		}

		//dialog.setModal(false);  //
		dialog.setVisible(true); //显示卡片窗口
		if (dialog.getCloseType() == 1 || dialog.getCloseType() == 100) { //如是是点击确定返回!将则卡片中的数据赋给列表!
			int li_newrow = billList.newRow(false, false); //
			billList.setBillVOAt(li_newrow, dialog.getBillVO(), false);
			billList.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.

			////点击后拦截器
			for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
				BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
				BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, cardPanel, billList); //
				action.onBillListAddButtonClicked(event); ////点击后执行....
			}

			billList.setSelectedRow(li_newrow); //
		}
	}

	/**
	 * 列表行新增
	 * @throws Exception
	 */
	private void onBillListInsertRow() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		int li_currrow = billList.getSelectedRow(); // 取得当前行
		int li_rowcount = billList.getRowCount();
		if (li_currrow < 0) { // 没有选中的情况下要新增到最后
			if (li_rowcount > 0) {
				billList.setSelectedRow(billList.getRowCount() - 1);
			}
		}
		int newrow = billList.newRow(); //
		if (!billList.templetVO.getIsshowlistpagebar().booleanValue()) { // 不分页的情况下重置一下seq字段
			String seqfild = billList.getTempletVO().getTreeseqfield();//如果配置了树面板排序列，则用该字段排序，否则默认为seq
			if (seqfild == null || seqfild.trim().equals("")) {
				seqfild = "seq";
			}
			for (int i = 0; i < billList.getRowCount(); i++) {
				//以前遇到过，一个表进行行上移或下移操作并保存后，发现数据并没有变，因为以前这里没有处理如果seq为空的情况
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
	 * 列表弹出编辑
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
			action.onBillListEditButtonClicking(event); //点击前执行.....
		}

		BillCardDialog dialog = new BillCardDialog(billList, billList.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1 || dialog.getCloseType() == 100) {
			billList.setBillVOAt(billList.getSelectedRow(), dialog.getBillVO(), false); //
			billList.setRowStatusAs(billList.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
				BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
				BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, cardPanel, billList); //
				action.onBillListEditButtonClicked(event); //点击后执行.....
			}
		}
	}

	//列表弹出编辑某几项!!!
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
			MessageBox.show(this, "必须在按钮定义中定义一个名为[itemkey]的按钮参数!"); //
			return;
		}

		TBUtil tbUtil = new TBUtil(); //
		String[] str_itemKeyArray = tbUtil.split(str_itemkeys, ","); ////
		Pub_Templet_1VO cloneTempletVO = (Pub_Templet_1VO) new TBUtil().deepClone(billList.templetVO); ////
		for (int i = 0; i < cloneTempletVO.getItemVos().length; i++) {
			if (tbUtil.isExistInArray(cloneTempletVO.getItemVos()[i].getItemkey(), str_itemKeyArray, true)) { //如果该模板子项在定义中,则强行定义为卡片显示!
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
			action.onBillListEditButtonClicking(event); //点击前执行.....
		}

		BillCardDialog dialog = new BillCardDialog(billList, cloneTempletVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			billList.setBillVOAt(billList.getSelectedRow(), dialog.getBillVO()); //
			billList.setRowStatusAs(billList.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);

			for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
				BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
				BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, cardPanel, billList); //
				action.onBillListEditButtonClicked(event); //点击后执行.....
			}
		}
	}

	/**
	 * 列表直接删除
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
			action.onBillListDeleteButtonClicking(event); //点击前执行.....
		}

		//真正进行除操作
		billList.doDelete(true); //真正进行删除操作!!!

		for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
			BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
			BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, billList); //
			action.onBillListDeleteButtonClicked(event); //点击前执行.....
		}
	}

	/**
	 * 列表行删除
	 * @throws Exception
	 */
	private void onBillListRemoveRow() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		billList.removeSelectedRows(); //
		if (!billList.templetVO.getIsshowlistpagebar().booleanValue()) { // 不分页的情况下重置一下seq字段
			String seqfild = billList.getTempletVO().getTreeseqfield();//如果配置了树面板排序列，则用该字段排序，否则默认为seq
			if (seqfild == null || seqfild.trim().equals("")) {
				seqfild = "seq";
			}
			for (int i = 0; i < billList.getRowCount(); i++) {
				//以前遇到过，一个表进行行上移或下移操作并保存后，发现数据并没有变，因为以前这里没有处理如果seq为空的情况
				if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
					billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild); //
					billList.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				}
			}
		}
	}

	/**
	 * 列表弹出查看/浏览
	 * @throws Exception
	 */
	private void onBillListSelect() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		BillVO billVO = billList.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this); //为了统一提示信息，故修改【李春娟/2012-05-02】
			return;
		}

		BillCardPanel cardPanel = new BillCardPanel(billList.templetVO); //
		cardPanel.setBillVO(billVO); //

		for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
			BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
			BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, cardPanel, billList); //
			action.onBillListLookAtButtonClicking(event); //点击前执行.....
		}

		String str_recordName = billVO.toString(); //
		BillCardFrame frame = new BillCardFrame(billList, billList.templetVO.getTempletname() + "[" + str_recordName + "]", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT, true);
		frame.getBtn_confirm().setVisible(false); //
		frame.getBtn_save().setVisible(false); //
		cardPanel.setEditable(false); //
		frame.setVisible(true); //
	}

	/**
	 * 列表弹出查看/浏览(执行编辑公式)
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
		for (int i = 0; i < templetItemVOs.length; i++) { //遍历所有控件!!
			final String str_itemkey = templetItemVOs[i].getItemkey();
			String str_type = templetItemVOs[i].getItemtype();
			AbstractWLTCompentPanel compentPanel = null; //先定义好控件!!!一个不漏!!
			if (str_type.equals(WLTConstants.COMP_LABEL)) { //Label
			} else if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || str_type.equals(WLTConstants.COMP_NUMBERFIELD) || str_type.equals(WLTConstants.COMP_PASSWORDFIELD)) { //文本框,数字框,密码框
				cardPanel.execEditFormula(str_itemkey);
			} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { //下拉框
				cardPanel.execEditFormula(str_itemkey);
			} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || //表型参照1
					str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || //树型参照1
					str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //多选参照1
					str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || //自定义参照
					str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //列表模板参照
					str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //树型模板参照
					str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //注册样板参照
					str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //注册参照
					str_type.equals(WLTConstants.COMP_DATE) || //日历
					str_type.equals(WLTConstants.COMP_DATETIME) || //时间
					str_type.equals(WLTConstants.COMP_BIGAREA) || //大文本框
					//str_type.equals(WLTConstants.COMP_FILECHOOSE) || //文件选择框!!!（以前也是当一个参照处理,但后来无数处用户要求将其拿到主页面上来,以符合大量网页系统的效果,少点一次,）
					str_type.equals(WLTConstants.COMP_COLOR) || //颜色选择框
					str_type.equals(WLTConstants.COMP_CALCULATE) || //计算器
					str_type.equals(WLTConstants.COMP_PICTURE) || //图片选择框
					str_type.equals(WLTConstants.COMP_EXCEL) || //EXCEL
					str_type.equals(WLTConstants.COMP_OFFICE) //office
			) { //如果是各种参照
				cardPanel.execEditFormula(str_itemkey);
			} else if (str_type.equals(WLTConstants.COMP_TEXTAREA)) { //多行文本框
				cardPanel.execEditFormula(str_itemkey);
			} else if (str_type.equals(WLTConstants.COMP_BUTTON)) { //按钮
				cardPanel.execEditFormula(str_itemkey);
			} else if (str_type.equals(WLTConstants.COMP_CHECKBOX)) { //勾选框
				cardPanel.execEditFormula(str_itemkey);
			} else {
				continue; //
			}
		}

		for (int i = 0; i < billList.getV_listbtnListener().size(); i++) {
			BillListButtonActinoListener action = (BillListButtonActinoListener) billList.getV_listbtnListener().get(i); //
			BillListButtonClickedEvent event = new BillListButtonClickedEvent(this.btnDefineVo.getCode(), this, cardPanel, billList); //
			action.onBillListLookAtButtonClicking(event); //点击前执行.....
		}

		String str_recordName = billVO.toString(); //
		BillCardFrame frame = new BillCardFrame(billList, billList.templetVO.getTempletname() + "[" + str_recordName + "]", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT, true);
		frame.getBtn_confirm().setVisible(false); //
		frame.getBtn_save().setVisible(false); //
		cardPanel.setEditable(false); //
		frame.setVisible(true); //
	}

	/**
	 * 列表直接保存
	 * @throws Exception
	 */
	private void onBillListSave() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		if (billList.checkValidate()) {
			if (billList.saveData()) {//这里根据返回是否成功来进行提示【李春娟/2013-06-05】
				MessageBox.show(this, "保存成功!");
			}
		}
	}

	/**
	 * 列表隐藏/显示列
	 * @throws Exception
	 */
	private void onBillListHiddenShowCol() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		billList.reShowHideTableColumn(); //
	}

	/**
	 * 结果中查找
	 */
	private void onBillListSearchFromThis() {
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		billList.quickSearch(); //
	}

	//工作流查看草稿箱
	private void onBillListWorkFlow_DraftTask() {
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		String str_orderCons = billList.getOrderCondition(); //
		String str_userid = ClientEnvironment.getCurrLoginUserVO().getId(); //用户id
		billList.queryDataByCondition("create_userid = '" + str_userid + "' and wfprinstanceid is null", str_orderCons); //查询一把
	}

	//待办任务!!
	private void onBillListWorkFlow_DealTask() {
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		String str_templetCode = billList.getTempletVO().getTempletcode(); //
		String str_tableName = billList.getTempletVO().getTablename(); //
		String str_savedTableName = billList.getTempletVO().getSavedtablename(); //保存的表名
		String str_pkName = billList.getTempletVO().getPkname(); //
		String str_sql = new WorkflowUIUtil().getDealTaskSQL(str_templetCode, str_tableName, str_savedTableName, str_pkName, ClientEnvironment.getInstance().getLoginUserID(), null, false); //
		billList.queryDataByDS(null, str_sql, true); //直接通过SQL查询!!
	}

	//已办任务!!
	private void onBillListWorkFlow_OffTask() {
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		String str_templetCode = billList.getTempletVO().getTempletcode(); //
		String str_tableName = billList.getTempletVO().getTablename(); //
		String str_savedTableName = billList.getTempletVO().getSavedtablename(); //保存的表名
		String str_pkName = billList.getTempletVO().getPkname(); //
		String str_sql = new WorkflowUIUtil().getOffTaskSQL(str_templetCode, str_tableName, str_savedTableName, str_pkName, ClientEnvironment.getInstance().getLoginUserID(), null, false); //已办任务
		billList.queryDataByDS(null, str_sql, true); //直接通过SQL关联!!!
	}

	/**
	 * 工作流处理!
	 */
	private void onBillListWorkFlowProcess() {
		new cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory().dealAction("deal", (BillListPanel) this.getBillPanelFrom(), null); //处理动作!
	}

	/**
	 * 列表工作流监控
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
			MessageBox.show(billList, "选中的记录中没有定义工作流字段(wfprinstanceid)!"); //
			return; //
		}

		String str_wfprinstanceid = billVO.getStringValue("wfprinstanceid"); //
		if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) {
			MessageBox.show(billList, "工作流例值为空,还没有启动流程!"); //
			return; //
		}

		cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog wfMonitorDialog = new cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog(billList, str_wfprinstanceid, billVO); //
		wfMonitorDialog.setMaxWindowMenuBar();
		wfMonitorDialog.setVisible(true); //
	}

	/**
	 * 列表工作流发起/监控
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
			MessageBox.show(billList, "选中的记录中没有定义工作流字段(wfprinstanceid)!"); //
			return; //
		}
		String str_wfprinstanceid = billVO.getStringValue("wfprinstanceid"); //
		if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) {//如果流程未发起，则发起流程，否则监控流程
			onBillListWorkFlowProcess();
		} else {
			cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog wfMonitorDialog = new cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog(billList, str_wfprinstanceid, billVO); //
			wfMonitorDialog.setMaxWindowMenuBar();
			wfMonitorDialog.setVisible(true); //
		}
	}

	/**
	 * 列表导出Excel
	 * @throws Exception
	 */
	private void onBillListExportExcel() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		billList.exportToExcel(); //层次出Excel
	}
	/**
	 * 列表行上移
	 * @throws Exception
	 */
	private void onBillListRowUp() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		billList.moveUpRow(); //上移
		int li_rowcount = billList.getRowCount();
		String seqfild = billList.getTempletVO().getTreeseqfield();//如果配置了树面板排序列，则用该字段排序，否则默认为seq
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		for (int i = 0; i < li_rowcount; i++) {
			//以前遇到过，一个表进行行上移或下移操作并保存后，发现数据并没有变，因为以前这里没有处理如果seq为空的情况。并且处理了seqfild值非数字的情况，
			//注意第二个判断用billList.getRealValueAtModel()得到的是字符串，而billList.getValueAt()得到的是StringItemVO对象
			if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
				billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild); //
				if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(billList.getRowNumberEditState(i))) {//如果是初始状态再设置更新，否则新增状态的数据执行update不能保存【李春娟/2014-10-31】
					billList.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				}
			}
		}
	}

	/**
	 * 列表行下移
	 * @throws Exception
	 */
	private void onBillListRowDown() throws Exception {
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom(); //
		billList.moveDownRow(); //下移
		int li_rowcount = billList.getRowCount();
		String seqfild = billList.getTempletVO().getTreeseqfield();//如果配置了树面板排序列，则用该字段排序，否则默认为seq
		if (seqfild == null || seqfild.trim().equals("")) {
			seqfild = "seq";
		}
		for (int i = 0; i < li_rowcount; i++) {
			//以前遇到过，一个表进行行上移或下移操作并保存后，发现数据并没有变，因为以前这里没有处理如果seq为空的情况
			if (billList.getValueAt(i, seqfild) == null || !((i + 1) + "").equals(billList.getRealValueAtModel(i, seqfild))) {
				billList.setValueAt(new StringItemVO("" + (i + 1)), i, seqfild); //
				if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(billList.getRowNumberEditState(i))) {//如果是初始状态再设置更新，否则新增状态的数据执行update不能保存【李春娟/2014-10-31】
					billList.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				}
			}
		}
	}

	/**
	 * 列表加入收藏
	 * @throws Exception
	 */
	private void onBillListFavorite() throws Exception { //加入收藏 【杨科/2012-09-04】
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
	 * 列表传阅
	 * @throws Exception
	 */
	private void onBillListPassRead() throws Exception { //传阅 【杨科/2012-11-28】
		checkBillListType();
		BillListPanel billList = (BillListPanel) this.getBillPanelFrom();

		BillVO billVO = billList.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		new PassReadBtn(billList, "普通传阅消息");
	}

	/**
	 * 卡片新增
	 * @throws Exception
	 */
	private void onBillCardInsert() throws Exception {
		checkBillCardType();
		BillCardPanel billCard = (BillCardPanel) this.getBillPanelFrom(); //
		billCard.insertRow(); //
		billCard.setEditableByInsertInit(); //
	}

	/**
	 * 卡片编辑
	 * @throws Exception
	 */
	private void onBillCardEdit() throws Exception {
		checkBillCardType();
		BillCardPanel billCard = (BillCardPanel) this.getBillPanelFrom(); //
		billCard.updateCurrRow(); //
		billCard.setEditableByEditInit(); //
	}

	/**
	 * 卡片删除
	 * @throws Exception
	 */
	private void onBillCardDelete() throws Exception {
		checkBillCardType();
		BillCardPanel billCard = (BillCardPanel) this.getBillPanelFrom(); //
		if (MessageBox.showConfirmDialog(billCard, "您真的想删除该记录吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return; //
		}
		String str_sql = "delete from " + billCard.getTempletVO().getSavedtablename() + " where " + billCard.getTempletVO().getPkname() + "='" + billCard.getRealValueAt(billCard.getTempletVO().getPkname()) + "'"; //
		UIUtil.executeUpdateByDS(billCard.getDataSourceName(), str_sql); //提交数据库
		billCard.clear();
	}

	/**
	 * 卡片保存
	 * @throws Exception
	 */
	private void onBillCardSave() throws Exception {
		checkBillCardType();
		BillCardPanel billCard = (BillCardPanel) this.getBillPanelFrom(); //
		if (billCard.checkValidate(billCard)) { //以前没有做必填或者长度校验！直接保存[郝明2012-03-01]
			billCard.updateData();
			MessageBox.show(this, "保存成功!"); //提示成功！
		}
	}

	/**
	 * 树新增
	 * @throws Exception
	 */
	private void onBillTreeInsert() throws Exception {
		checkBillTreeType();
		BillTreePanel billTree = (BillTreePanel) this.getBillPanelFrom(); //
		if (billTree.getSelectedNode() == null) {
			MessageBox.show(this, "请选择一个父结点进行新增操作!"); //
			return;
		}

		BillVO billVO = billTree.getSelectedVO();
		BillCardPanel insertCardPanel = new BillCardPanel(billTree.getTempletVO()); //
		if (billVO != null) { //如果选中的不是根结点
			insertCardPanel.insertRow(); //新增一行
			insertCardPanel.setEditableByInsertInit(); //
			DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) billTree.getSelectedNode();
			if (!parentNode.isRoot()) { //
				BillVO parentVO = billVO; //
				String parent_id = ((StringItemVO) parentVO.getObject(billTree.getTempletVO().getTreepk())).getStringValue(); //
				insertCardPanel.setCompentObjectValue(billTree.getTempletVO().getTreeparentpk(), new StringItemVO(parent_id)); //
			}
		} else { //如果选中的是根结点
			insertCardPanel.insertRow(); //
			insertCardPanel.setEditableByInsertInit(); //
		}

		BillCardDialog dialog = new BillCardDialog(billTree, "新增", insertCardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == BillDialog.CONFIRM) {
			BillVO newVO = insertCardPanel.getBillVO(); //
			newVO.setToStringFieldName(billTree.getTempletVO().getTreeviewfield()); //
			billTree.addNode(newVO); //
		}
	}

	/**
	 * 树编辑..
	 * @throws Exception
	 */
	private void onBillTreeEdit() throws Exception {
		checkBillTreeType();
		BillTreePanel billTree = (BillTreePanel) this.getBillPanelFrom(); //
		if (billTree.getSelectedNode() == null) {
			MessageBox.show(this, "请选择一个结点进行编辑操作!"); //
			return;
		}

		BillVO billVO = billTree.getSelectedVO();
		BillCardPanel editCardPanel = new BillCardPanel(billTree.getTempletVO()); //

		if (billVO != null) { //如果选中的不是根结点
			editCardPanel.queryDataByCondition("id='" + billVO.getPkValue() + "'"); //
			editCardPanel.setEditableByEditInit(); //
		} else { //如果选中的是根结点
			MessageBox.show(this, "根结点不可编辑!!"); //
			return; //如果没有选择一个结点则直接返回
		}

		BillCardDialog dialog = new BillCardDialog(billTree, "编辑", editCardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == BillDialog.CONFIRM) {
			BillVO newVO = editCardPanel.getBillVO(); //
			newVO.setToStringFieldName(billTree.getTempletVO().getTreeviewfield()); //
			billTree.setBillVOForCurrSelNode(newVO); //向树中回写数据
			billTree.updateUI(); //以前是billTree.getJTree().updateUI();会改变展开和收缩图标，李春娟2012-02-23修改
		}
	}

	/**
	 * 树删除
	 * @throws Exception
	 */
	private void onBillTreedDelete() throws Exception {
		checkBillTreeType();
		BillTreePanel billTree = (BillTreePanel) this.getBillPanelFrom(); //
		if (billTree.getSelectedNode() == null) {
			MessageBox.show(this, "请选择一个结点进行删除操作!"); //
			return;
		}
		BillTreeNodeVO treeNodeVO = billTree.getSelectedTreeNodeVO();//以前是直接删除选中节点，但子节点不删除，导致子节点再次加载时找不到父节点于是显示在树的第一级的错误，现改为一并删除其子孙节点！【李春娟/2012-02-29】
		BillVO[] childVOs = billTree.getSelectedChildPathBillVOs(); //取得所有选中的
		if (MessageBox.showConfirmDialog(this, "您确定要删除记录【" + treeNodeVO.toString() + "】吗?\r\n这将一并删除其下共【" + childVOs.length + "】条子孙记录,请务必谨慎操作!", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}
		Vector v_sqls = new Vector(); //菜单删除
		for (int i = 0; i < childVOs.length; i++) {
			v_sqls.add("delete from " + childVOs[i].getSaveTableName() + " where " + childVOs[i].getPkName() + "='" + childVOs[i].getPkValue() + "'"); //
		}
		UIUtil.executeBatchByDS(null, v_sqls); //执行数据库操作!!
		billTree.delCurrNode(); //
		billTree.updateUI();
	}

	/**
	 * 树弹出查看/浏览
	 * @throws Exception
	 */
	private void onBillTreeSelect() throws Exception {
		checkBillTreeType();
		BillTreePanel billTree = (BillTreePanel) this.getBillPanelFrom(); //
		if (billTree.getSelectedNode() == null) {
			MessageBox.show(this, "请选择一个结点进行查看!"); //
			return;
		}

		BillVO billVO = billTree.getSelectedVO();
		BillCardPanel editCardPanel = new BillCardPanel(billTree.getTempletVO()); //

		if (billVO != null) { //如果选中的不是根结点
			editCardPanel.queryDataByCondition("id='" + billVO.getPkValue() + "'"); //
			editCardPanel.setEditable(false); //
			editCardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); //
		} else { //如果选中的是根结点
			MessageBox.show(this, "根结点不可查看!!"); //
			return; //如果没有选择一个结点则直接返回
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

		BillDialog dialog = new BillDialog(this, "查看", li_width, li_height); //
		dialog.getContentPane().add(editCardPanel); //
		dialog.setVisible(true); //
	}

	/**
	 * 校验面板必须是BillListPanel，既不能为空,也必须类型品配
	 * @throws Exception
	 */
	private void checkBillListType() throws Exception {
		checkBillPanelFromIsNull();
		if (!(billPanelFrom instanceof BillListPanel)) {
			throw new WLTAppException("面板不是BillList类型,该按钮类型是[" + this.btnDefineVo.getBtntype() + "],必须从属于一个BillList面板才能处理预置逻辑!"); //
		}
	}

	/**
	 * 校验面板必须是BillListPanel，既不能为空,也必须类型品配
	 * @throws Exception
	 */
	private void checkBillCardType() throws Exception {
		checkBillPanelFromIsNull();
		if (!(billPanelFrom instanceof BillCardPanel)) {
			throw new WLTAppException("面板不是BillCard类型,该按钮类型是[" + this.btnDefineVo.getBtntype() + "],必须从属于一个面板不是BillCard类型面板才能处理预置逻辑!"); //
		}
	}

	/**
	 * 校验面板必须是BillListPanel，既不能为空,也必须类型品配
	 * @throws Exception
	 */
	private void checkBillTreeType() throws Exception {
		checkBillPanelFromIsNull();
		if (!(billPanelFrom instanceof BillTreePanel)) {
			throw new WLTAppException("面板不是BillTree类型,该按钮类型是[" + this.btnDefineVo.getBtntype() + "],必须从属于一个BillTree面板才能处理预置逻辑!"); //
		}
	}

	private void checkBillPanelFromIsNull() throws Exception {
		if (this.getBillPanelFrom() == null) {
			throw new WLTAppException("billPanelFrom为null,没有定义该按钮从属于哪个面板,无法处理类型为[" + this.btnDefineVo.getBtntype() + "]的预置逻辑!");
		}
	}

	/**
	 * 执行公式
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
				Object obj = _jepParse.execFormula(_formulas[i]); //执行前段公式,执行某一个公式,然后返回值,如果返回的是一个异常,则重抛之
				if (obj instanceof Exception) {
					throw (Exception) obj; //
				}
			}
		}
	}

	/**
	 * 直接添回事件,该方法是私有的,即
	 * @param l
	 */
	private void directAddActionListener(ActionListener l) {
		super.addActionListener(l);
	}

	/**
	 * 重构监听事件方法,即如果一个注册按钮本身有"预置逻辑,有执行前公式,执行后公式"等逻辑,但开发人员后来又直接通过该API注册事件
	 * 那么API注册的监听者将冲掉以前所有逻辑,即永远只保证是最后一个注册的有效!
	 * 这就意味着WLTButton永远只有一个监听者了!!这个监听者都就是在它自己里面!!!
	 */
	@Override
	public void addActionListener(ActionListener l) {
		this.custActionListener = l; //
	}

	/**
	 * 增加按钮自定义监听事件
	 * 一旦增加了自定义事件,则平台注册的事件将被冲掉而无效.
	 * 因为有时一个模板中注册的按钮会在虽的地方被用到,但别的地方的事件又不是平台的注册动作,而是自己的事件,这时这个功能就有用了!
	 * 换句话说就是借用按钮的样子与权限,但点击事件的逻辑需要自己搞!
	 * @param _custActionListener
	 */
	public void addCustActionListener(ActionListener _custActionListener) {
		this.custActionListener = _custActionListener; //
	}

	public ActionListener getCustActionListener() {
		return custActionListener;
	}

	/**
	 * 右键弹出菜单显示.
	 */
	private void onPopMenuClicked(ActionEvent e) {
		if (e.getSource() == menuItem_info) {
			onShowBtnInfo(); //查看按钮信息
		} else if (e.getSource() == menuItem_edit) { //编辑注册按钮
			onEditRegisterBtn(); //
		} else if (e.getSource() == menuItem_showAllBtn) {
			onShowAllBtnInfo(); //显示所有按钮信息
		} else if (e.getSource() == menuItem_resetOrder) {
			onResetBillPanelOrderSeq(); //重置按钮在面板中的位置.
		} else if (e.getSource() == menuItem_regCode) { //注册码
			onUpdateRegCode(); //修改注册码!!!
		}
	}

	/**
	 * 取得本按钮的所有重要信息!
	 * @return
	 */
	public String getButtonInfoMsg() {
		StringBuilder sb_info = new StringBuilder(); //
		sb_info.append("按钮编码=【" + convertNullStr(this.btnDefineVo.getCode()) + "】\r\n"); //
		sb_info.append("按钮文字=【" + convertNullStr(this.btnDefineVo.getBtntext()) + "】\r\n"); //
		sb_info.append("按钮类型=【" + convertNullStr(this.btnDefineVo.getBtntype()) + "】\r\n"); //
		sb_info.append("是否注册按钮=【" + this.btnDefineVo.isRegisterBtn() + "】\r\n"); //

		sb_info.append("按钮图标=[" + convertNullStr(this.btnDefineVo.getBtnimg()) + "]\r\n"); //
		sb_info.append("按钮标签=[" + convertNullStr(this.btnDefineVo.getBtntooltiptext()) + "]\r\n"); //
		sb_info.append("按钮说明=[" + convertNullStr(this.btnDefineVo.getBtndescr()) + "]\r\n"); //

		sb_info.append("点击前公式=[" + convertNullStr(this.btnDefineVo.getClickingformula()) + "]\r\n"); //
		sb_info.append("点击后公式=[" + convertNullStr(this.btnDefineVo.getClickedformula()) + "]\r\n"); //

		sb_info.append("允许的岗位=[" + convertNullStr(this.btnDefineVo.getAllowposts()) + "]\r\n"); //
		sb_info.append("允许的角色=[" + convertNullStr(this.btnDefineVo.getAllowroles()) + "]\r\n"); //
		sb_info.append("允许的人员=[" + convertNullStr(this.btnDefineVo.getAllowusers()) + "]\r\n"); //
		sb_info.append("权限计算结果=【" + convertNullStr(this.btnDefineVo.getAllowResult()) + "】\r\n"); //
		sb_info.append("入口监听者=【" + getAllActionListenerNames() + "】\r\n"); //
		sb_info.append("转调的实际逻辑类=【" + (this.custActionListener == null ? "" : (custActionListener.getClass().getName())) + "】\r\n"); //
		sb_info.append("所属的BillButtonPanel=【" + getObjectClassName(getBillButtonPanelFrom()) + "】\r\n");
		sb_info.append("所属的BillPanel=【" + getObjectClassName(getBillPanelFrom()) + "】\r\n");
		sb_info.append("所属的BillFormatPanel=【" + getObjectClassName(getBillFormatPanelFrom()) + "】\r\n");

		sb_info.append("\r\n所有父亲类路径(可以使用):\r\n"); //
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
		sb_clsParent.append("使用BillCardPanel card = (BillCardPanel) SwingUtilities.getAncestorOfClass(BillCardPanel.class, this);可以得到某个父亲面板");
		sb_info.append(sb_clsParent.toString()); //

		return sb_info.toString(); //
	}

	/**
	 * 显示按钮信息
	 */
	private void onShowBtnInfo() {
		MessageBox.showTextArea(this, "按钮信息", getButtonInfoMsg(), 500, 400); //
	}

	/**
	 * 快速编辑按钮属性
	 */
	private void onEditRegisterBtn() {
		if (!this.btnDefineVo.isRegisterBtn()) {
			MessageBox.show(this, "不是注册型的按钮,是直接添加的,不能进行编辑处理!"); //
			return;
		}

		BillCardPanel cardPanel = new BillCardPanel("pub_regbuttons_CODE1"); //
		cardPanel.queryDataByCondition("code='" + this.btnDefineVo.getCode() + "'"); //
		BillCardDialog dialog = new BillCardDialog(this, "编辑按钮配置", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		cardPanel.setEditable("code", false); //编辑是不能改的,修改编辑必须在模板配置中改
		dialog.setVisible(true); //

		if (dialog.getCloseType() == BillDialog.CONFIRM) {
			this.btnDefineVo.setClickingformula(cardPanel.getRealValueAt("clickingformula")); //
			this.btnDefineVo.setClickedformula(cardPanel.getRealValueAt("clickedformula")); //
			this.btnDefineVo.setBtntype(cardPanel.getRealValueAt("btntype")); //按钮类型
			this.btnDefineVo.setBtntext(cardPanel.getRealValueAt("btntext")); //
			this.setText(this.btnDefineVo.getBtntext()); //
		}
	}

	/**
	 * 显示按钮面板(BillButtonPanel)中所有按钮的信息,如果该按钮存在于一个按钮面板中的话
	 */
	private void onShowAllBtnInfo() {
		BillButtonPanel btnPanel = this.getBillButtonPanelFrom();
		if (btnPanel == null) {
			MessageBox.show(this, "该按钮不存于一个按钮栏中不能查看其他兄弟按钮信息!"); //
			return;
		}

		WLTButton[] allBtns = btnPanel.getAllButtons(); //
		StringBuilder sb_allInfo = new StringBuilder(); //
		sb_allInfo.append("一共有[" + allBtns.length + "]个的按钮.\r\n");
		for (int i = 0; i < allBtns.length; i++) {
			sb_allInfo.append("*********************【" + (i + 1) + "】*********************\r\n");
			sb_allInfo.append(allBtns[i].getButtonInfoMsg());
			sb_allInfo.append("\r\n");
		}
		MessageBox.showTextArea(this, "所有按钮信息", sb_allInfo.toString(), 500, 400); //
	}

	public String getRegCodeStr() {
		return regCodeStr;
	}

	public void setRegCodeStr(String regCodeStr) {
		this.regCodeStr = regCodeStr;
	}

	/**
	 * 重置按钮顺序
	 */
	private void onResetBillPanelOrderSeq() {
		MessageBox.showTextArea(this, "重置按钮显示顺序,开发中..."); //
	}

	/**
	 * 修改注册码!!
	 */
	private void onUpdateRegCode() {
		try {
			JPanel panel = WLTPanel.createDefaultPanel(null); //
			String str_oldImeName = null; //
			if (btnDefineVo != null && btnDefineVo.getBtnimg() != null) {
				str_oldImeName = btnDefineVo.getBtnimg(); //
			}
			CardCPanel_Ref refReg = new CardCPanel_Ref("btnimg", "按钮图片", "", "图片选择框", 100, 150, new RefItemVO(str_oldImeName, null, str_oldImeName), null); //
			refReg.setBounds(10, 10, 350, 25); //
			panel.add(refReg); //

			BillDialog idalog = new BillDialog(this, "修改图片", 400, 120);
			idalog.getContentPane().add(panel); //
			idalog.addConfirmButtonPanel(); //
			idalog.setVisible(true); //
			if (idalog.getCloseType() == 1) {
				String str_newImgName = refReg.getTextField().getText(); //
				if (str_newImgName == null || str_newImgName.equals("")) {
					MessageBox.show(this, "图片为空,请选择一个图片!"); //
					return;
				}
				String str_count = UIUtil.getStringValueByDS(null, "select count(*) from pub_option where parkey='" + this.regCodeStr + "'"); //
				int li_count = Integer.parseInt(str_count); //
				if (li_count > 0) { //如果有,则修改
					UIUtil.executeUpdateByDSPS(null, "update pub_option set parvalue='" + str_newImgName + "' where parkey='" + regCodeStr + "'"); //
					reloadCache(); //
					MessageBox.show(null, "系统已有参数,修改新参数为[" + str_newImgName + "],已刷新缓存,请重新登录即可!"); //
				} else { //
					String str_newid = UIUtil.getSequenceNextValByDS(null, "S_PUB_OPTION"); //
					UIUtil.executeUpdateByDSPS(null, "insert into pub_option (id,parkey,parvalue) values (" + str_newid + ",'" + regCodeStr + "','" + str_newImgName + "')"); //
					reloadCache(); //
					MessageBox.show(null, "系统还没有参数,新增参数为[" + str_newImgName + "],已刷新缓存,请重新登录即可!"); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private void reloadCache() throws Exception {
		String[][] str_result = UIUtil.getCommonService().reLoadDataFromDB(false); //
		if (str_result != null) {
			ClientEnvironment.setClientSysOptions(UIUtil.getCommonService().getAllOptions()); //重新得到所有参数
		}
	}

	/**
	 * 取得一个对象的名称
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
	 * 所有监听器名称。
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
	 * 取得按钮定义对象
	 * @return
	 */
	public ButtonDefineVO getBtnDefineVo() {
		return btnDefineVo;
	}

	/**
	 * 取得按钮定义的参数!!!
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
	 * 取得按钮的宽度
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
	 * 点击右键时弹出菜单.
	 */
	public void mouseClicked(MouseEvent e) {
		if (isRightBtnShowInfo) {
			if (e.getButton() == MouseEvent.BUTTON3) { //如果点击的右键
				if (regCodeStr != null) {
					menuItem_regCode.setVisible(true); //
				} else {
					menuItem_regCode.setVisible(false); //
				}
				this.popMenu.show(this, e.getX(), e.getY()); //显示弹出菜单!!..
			}
		}
	}

	/**
	 * 鼠标移入时,应该变化边框,以显示一种动感.
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * 鼠标移出时,应该变化边框,以显示一种动感.
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
	 * 按钮监听事件..
	 * @author xch
	 */
	class WLTBActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			onButtontnClick(e); //默认肯定是有逻辑的!!
		}
	}

}
