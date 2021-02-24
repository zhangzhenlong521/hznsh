/**************************************************************************
 * $RCSfile: DeskTopPanel.java,v $  $Revision: 1.208 $  $Date: 2013/02/28 06:05:24 $
 **************************************************************************/

package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.PopupFactory;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicPanelUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;
import cn.com.infostrategy.to.sysapp.login.DeskTopVO;
import cn.com.infostrategy.to.sysapp.login.RoleVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.BillFrame;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.ScrollablePopupFactory;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTHrefLabel;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTMenuItemUI;
import cn.com.infostrategy.ui.common.WLTMenuUI;
import cn.com.infostrategy.ui.common.WLTOutlookBar;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPopupButton;
import cn.com.infostrategy.ui.common.WLTRadioPane;
import cn.com.infostrategy.ui.common.WLTScrollPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.common.WLTTreeUI;
import cn.com.infostrategy.ui.common.WLTTabbedPane.CloseTabedPaneBtn;
import cn.com.infostrategy.ui.mdata.hmui.LAFUtil;
import cn.com.infostrategy.ui.sysapp.login2.I_BarMenuAndWorkPanel;
import cn.com.infostrategy.ui.sysapp.login2.I_DTPTaskBtnAction;
import cn.com.infostrategy.ui.sysapp.login2.I_DeskTopPanelBtnStyleActionIfc;
import cn.com.infostrategy.ui.sysapp.login2.I_WLTTabbedPane;
import cn.com.infostrategy.ui.workflow.pbom.BillBomPanel;

/**
 * 登录后进入的系统主页面! 后来做过修改!!!!!
 * @author xch
 * 
 */
public class DeskTopPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 2349708048405530570L;

	private String loginUserCode = null; // 用户编码!!
	private String longinTime = null; // 登录时间
	private WLTButton btn_relogin, btn_exit; // 重新登录按钮!! 退出按钮!!

	private WLTPopupButton serverPopButton; // 当管理身份登录时,会多出这个按钮,它有大量后门工具!!非常有用!专为实施人员使用!
	private WLTButton btn_clientconsole, btn_serverconsole, btn_desktopstyle, btn_updatepwd, btn_userinfo, btn_zhuxiao; //
	private JButton btn_help; //
	private JMenuItem menuItem_clientenv, menuItem_serverenv, menuItem_serverconsole, menuItem_state, menuItem_serverlog, menuItem_serverscreen, menuItem_serverexplorer, menuItem_sqltool, menuItem_sqllistener, menuItem_sqlbuding; // 客户端环境变量,服务器环境变量,查看State,服务器日志,服务器屏幕,SQL工具,SQL语句监控.[2012-05-10郝明添加state查看]

	private ImageIcon icon_up, icon_down, icon_left, icon_right;
	private JButton btn_hiddensplit, btn_hiddentitle, btn_sysregmenu; //
	private JMenuItem item_expand, item_collapse, item_editconfig, item_showreason; //
	private JMenuItem menuItem_1_closethis, menuItem_2_closeothers, menuItem_3_closeall, menuItem_hidetoolbar, menuItem_hideradiopanel, menuItem_hidesystabpanel, menuItem_config_desktop, menuItem_refresh_indexPage, menuItem_config_person, menuItem_config_right; //
	//private JMenuItem menuItem_1_closethis, menuItem_2_closeothers, menuItem_3_closeall; //
	private JMenuItem menuItem_hidemenubar; //右键菜单中增加显示/隐藏菜单 Gwang 2012-11-29
	private JPopupMenu indexPopMenu; //
	private DeskTopVO deskTopVO = null;

	private JPanel toolBarPanel = null; //
	private JPanel buttonMenuBarPanel = null; //
	private String str_indexPageStyle = null; //首页默认风格
	private JPanel indexPageContainer = null; //
	private boolean isLoadedIndexPage = false; //是否加载了首页?因为有自定义加载页面,以及skip2模式,这样一来首页就存在懒装入的问题了

	private DefaultMutableTreeNode allTreeRootNode = null; //根结点!
	private ArrayList al_allLeafNodes = new ArrayList(); //所有叶子结点!!!
	private ArrayList al_autostartNodes = new ArrayList(); //所有需要自启动的结点!!

	private JPanel splitLeftTopPanel = null; //分割条左边的面板
	private CardLayout cardLayout = null; //
	private WLTOutlookBar outlook = null; //左边抽屉菜单树!!!
	private JTree sysRegMenuTree = null; //系统注册菜单的树

	private JPanel title_panel = null; //最上面的标题图片面板!!!
	private JSplitPane splitPanel_2 = null; //组装左边菜单西乡塘区与中间的首页的分割器容器!
	private WLTTabbedPane workTabbedPanel = null; // 工作面板!!

	private WLTRadioPane deskIndexRadioPanel = null; //
	private JPanel indexPanel = null; //首页(即仿照网页的首页)
	private JPanel taskAndMsgCenterPanel = null; //任务中心面板,即工作流处理!
	private JPanel indexShortCutPanel = null; //
	private JPanel bomPanel = null; //应该是cn.com.infostrategy.ui.workflow.pbom.BillBomPanel,但那样一样就会导致在出现登录界面时就下载了BillBomPanel面板,我不们不想在出现登录界面时就下载太多包,所以就写成JPanel!即类变量会导致下载,而单纯的import不会下载,在方法中的但没调用到也不会导致下载!

	private JLabel userNameLabel = null; //状态栏
	public JTextField informationLabel = null; //状态栏中间的文本框
	private JLabel jvmInfoLabel = null; //状态栏右边的显示虚拟机内存情况的label

	private int li_lastDiviLocatiob = 0; // 最后的位置
	private HashMap map_openedWorkPanel = new HashMap(); // 存储已打开的窗口

	public static DeskTopPanel deskTopPanel = null; ////
	private TBUtil tbUtil = new TBUtil(); //
	private static Logger logger = null; //
	private JTabbedPane gpstabpane = null;//如果有多个gps图，用多页签显示！

	String[] str_indexarray = new String[] { "公告消息", "任务中心", "快捷访问", "管理导航图" }; //
	private int MenuTreeWidth = 150; //系统菜单树宽度 【杨科/2012-11-22】
	private String realCallMenuName = null;//在打开一个菜单的时候的remotecallcilent里的获取访问的菜单不对
	private I_WLTTabbedPane newDeskTopTabPane; //新的首页切换效果面板
	private WLTTabbedPane newDeskTopTabPane_2; //上面是页签的切换风格
	private TBUtil tbutil = new TBUtil();
	private HashMap bomLabelShowToolTip = new HashMap();
	int index = 0;
	private JTextField searchField; //查询功能点的文本 

	private Timer openMenuThread = new Timer();

	private JLabel taskLabel, taskLabel_ = null;

	private JLabel selectedInfo = new JLabel();////列表单元格选中信息[YangQing/2013-09-25]

	/**
	 * @param _loader
	 * @param _desktopVO
	 * @param _username
	 * @param _logintime
	 */
	public DeskTopPanel(LoginAppletLoader _loader, DeskTopVO _desktopVO) {
		Frame f = JOptionPane.getFrameForComponent(this); //
		SwingUtilities.updateComponentTreeUI(f);
		this.loginUserCode = ClientEnvironment.getInstance().getLoginUserCode();
		this.deskTopVO = _desktopVO; //
		this.longinTime = deskTopVO.getLoginTime();
		deskTopPanel = this; //定义静态变量
		removeAllClientTimer(); //首页有两个滚动框(图片与文字),重新登录时必须先清除他们!!! 在创建这些timer时他们都往ClientEnvermene中塞入了!!!
		initialize();
	}

	/**
	 * 构造主界面!!!
	 */
	private JPanel tilePanel;
	private JPopupMenu tile_pop_menu = null;

	private void initialize() {
		MenuTreeWidth = tbUtil.getSysOptionIntegerValue("系统菜单树宽度", 150); //系统菜单树宽度 【杨科/2012-11-22】
		PopupFactory.setSharedInstance(new ScrollablePopupFactory()); //滚动动态效果!!!
		WLTLogger.config(System.getProperty("ClientCodeCache"), ClientEnvironment.getInstance().getLog4jConfigVO().getClient_level(), ClientEnvironment.getInstance().getLog4jConfigVO().getClient_outputtype()); // 设置Log4J配置
		WLTLogger.getLogger(LoginAppletLoader.class).info("成功设置Log4j,level[" + ClientEnvironment.getInstance().getLog4jConfigVO().getClient_level() + "],outputtype[" + ClientEnvironment.getInstance().getLog4jConfigVO().getClient_outputtype() + "]");
		initAllTreeData(); //先构造好数据!!!关键的第一步!!!

		//处理抽屉菜单!!!
		final int li_deskTopLayout = getDesktopLayout(); //桌面布局!
		JPanel menu_type_5_panel = null;
		if (li_deskTopLayout == 1) { // 如果是多抽屉方式登录ABCD
			HashVO[] firstLevelMenuVOs = getFirstLevelData(); //取得第一层的功能点,因为要用来创建抽屉!!
			outlook = new WLTOutlookBar(); //抽屉对象!!
			outlook.setOpaque(false); //
			outlook.setFontAll(LookAndFeel.desktop_OutLookBarFont); //
			outlook.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); //5
			outlook.setCanMax(true); //
			for (int i = 0; i < firstLevelMenuVOs.length; i++) { //遍历各个一级菜单!!!
				JTree tree = getSecondTree(i); //
				JScrollPane scrollPanel = new JScrollPane(tree);
				scrollPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)); //
				String str_iconName = (firstLevelMenuVOs[i].getStringValue("ICON") == null ? "blank.gif" : firstLevelMenuVOs[i].getStringValue("ICON")); //
				outlook.addTab(firstLevelMenuVOs[i].getStringValue("NAME"), UIUtil.getImage(str_iconName), scrollPanel, "提示"); //
			}
		} else if (li_deskTopLayout == 2) { //否则都是树型访问,后来去掉了Bom与菜单树两种方式!!!
			outlook = new WLTOutlookBar(); //抽屉对象!!
			outlook.setOpaque(false); //
			outlook.setFontAll(LookAndFeel.desktop_OutLookBarFont); //
			outlook.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); //5
			outlook.setCanMax(false); //
			JTree tree = getFirstTree(); //
			expandFirstLevel(tree, 8); //展开一级结点!因为有的人因为权限原因,只有几个功能点,结果否则缩在一起不好看,展开一下!!【xch/2012-03-06】
			JScrollPane scrollPanel = new JScrollPane(tree);
			scrollPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)); //
			String str_labeltext = tbUtil.getSysOptionStringValue("首页我的所有功能名称", "我的所有功能"); //
			outlook.addTab(str_labeltext, UIUtil.getImage("office_031.gif"), scrollPanel); //office_038.gif
			outlook.setSelectedIndex(0); //
		} else if (li_deskTopLayout == 3) { //如果是按钮栏!则不创建抽屉!!

		} else if (li_deskTopLayout == 5) {
			menu_type_5_panel = initDeskTopStyle5();
		}

		//中间的包括首页在内的三个面板!!!
		workTabbedPanel = new WLTTabbedPane(true);
		workTabbedPanel.setFocusable(false); //
		workTabbedPanel.setBorder(BorderFactory.createEmptyBorder()); //

		if (li_deskTopLayout != 3 && li_deskTopLayout != 4 && tbUtil.getSysOptionBooleanValue("首页是否支持多种风格", true)) {
			initHomePageRadioPanel();
		} else if (li_deskTopLayout != 3 && li_deskTopLayout != 4) { //如果没有多页签!
			indexPageContainer = new JPanel(); //
			workTabbedPanel.addTab(UIUtil.getLoginLanguage("任务中心"), UIUtil.getImage("zt_037.gif"), new WLTPanel()); //直接加载首页!!
		}
		if (workTabbedPanel.getTabCount() > 0) {
			workTabbedPanel.setForegroundAt(0, Color.blue); //
		}

		//选中退出时的页面
		workTabbedPanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JButton obj = (JButton) e.getSource(); //
				if (e.getButton() == MouseEvent.BUTTON3 || (e.isShiftDown() && e.getButton() == MouseEvent.BUTTON1)) { // 如果右键,或左键+Shift
					showWorkTabRightPopMenu(obj, e.getX(), e.getY()); //
				} else {
					if (e.getClickCount() == 2) { //双击页签!!
						if (title_panel != null) {
							if (title_panel.isVisible()) {
								title_panel.setVisible(false); //
								if (splitPanel_2 != null && splitLeftTopPanel != null && li_deskTopLayout != 3 && li_deskTopLayout != 4) {
									splitPanel_2.setDividerLocation(0); //
									splitPanel_2.setDividerSize(0);
									splitLeftTopPanel.setVisible(false); //
								}
								if (buttonMenuBarPanel != null) {
									buttonMenuBarPanel.setVisible(false); //
								}
							} else {
								title_panel.setVisible(true); //
								if (splitPanel_2 != null && splitLeftTopPanel != null && li_deskTopLayout != 3 && li_deskTopLayout != 4) { //按钮栏不处理!!!
									splitLeftTopPanel.setVisible(true); //
									splitPanel_2.setDividerLocation(MenuTreeWidth); //
									splitPanel_2.setDividerSize(4);
								}
								if (buttonMenuBarPanel != null) {
									buttonMenuBarPanel.setVisible(true); //
								}
							}
						}
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				JComponent obj = (JComponent) e.getSource(); //
				if (e.getButton() == MouseEvent.BUTTON3) { // 如果右键,或左键+Shift
					showWorkTabRightPopMenu(obj, e.getX(), e.getY()); //
				} else if (e.getButton() == MouseEvent.BUTTON2) { //如果是鼠标中键, 则关闭自己 Gwang 2013/2/28
					closeSelfTab();
				} else {
					CloseTabedPaneBtn btn = (CloseTabedPaneBtn) e.getSource();
					if (btn.canClose() && (e.getX() >= btn.getWidth() - 38 && e.getX() <= btn.getWidth() - 25) && (e.getY() >= 4 && e.getY() <= 17)) {
						closeSelfTab();
						return;
					}
				}
			}
		});
		workTabbedPanel.addChangeListener(new ChangeListener() { //选择变化!!!
					public void stateChanged(ChangeEvent e) { //
						if (li_deskTopLayout != 3 && li_deskTopLayout != 4) { //只有不是按钮风格才加载
							lazyLoadIndexPage(true); //
						}
					}
				}); //

		//处理主界面!!!
		JPanel middleContentPanel = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new BorderLayout(0, 0), LookAndFeel.defaultShadeColor1, false); //是渐变效果!!
		middleContentPanel.setBorder(BorderFactory.createEmptyBorder()); //

		//分隔条中的内容!
		if ("skip2".equals(System.getProperty("REQ_logintype"))) { //如果是skip2登录方式,则根本不加入分割器!!!
			middleContentPanel.add(workTabbedPanel, BorderLayout.CENTER); //菜单树+首页,在中间
		} else {
			splitLeftTopPanel = new JPanel(); //
			cardLayout = new CardLayout(); //
			splitLeftTopPanel.setLayout(cardLayout); //

			if (li_deskTopLayout == 1 || li_deskTopLayout == 2) {
				splitLeftTopPanel.add(outlook, "DBMenu"); //
			} else if (li_deskTopLayout == 5) {
				splitLeftTopPanel.add(menu_type_5_panel, "DBMenu");
			} else {
				splitLeftTopPanel.add(new JLabel("按钮栏风格不显示树"), "DBMenu"); //
			}
			splitLeftTopPanel.putClientProperty("LoadMenuType", "DBMenu"); //

			splitPanel_2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitLeftTopPanel, workTabbedPanel); //左边是抽屉,右边是多页签!!
			splitPanel_2.setOpaque(false); //透明!!!
			splitPanel_2.setBorder(BorderFactory.createEmptyBorder()); //
			splitPanel_2.setUI(new javax.swing.plaf.metal.MetalSplitPaneUI()); //
			splitPanel_2.setDividerLocation(MenuTreeWidth); //
			splitPanel_2.setDividerSize(4); // 分隔器大小
			middleContentPanel.add(splitPanel_2, BorderLayout.CENTER); //菜单树+首页,在中间
		}

		JPanel menuBarPanel = new JPanel(new BorderLayout()); //
		menuBarPanel.setOpaque(false); //透明!!
		boolean mustShowTools = tbutil.getSysOptionBooleanValue("首页是否必须显示工具条", false);
		if ((li_deskTopLayout == 1 || li_deskTopLayout == 2) && (ClientEnvironment.isAdmin() || mustShowTools)) {
			menuBarPanel.add(getToolBarPanel(), BorderLayout.CENTER); //工具条

			if (tbUtil.getSysOptionBooleanValue("首页是否隐藏工具条", false)) { //加个隐藏工具条的参数 Gwang 2012-11-29
				getToolBarPanel().setVisible(false);
			}
		}
		//		if (li_deskTopLayout == 3) { //如果是第三种,即按钮栏风格
		//			menuBarPanel.add(getButtonMenuBarPanel(), BorderLayout.SOUTH); //
		//		}
		if (!"skip2".equals(System.getProperty("REQ_logintype"))) { //如果是skip2登录方式,则根本不加入图片!!!
			middleContentPanel.add(menuBarPanel, BorderLayout.NORTH); //工具条,在上面,getToolBarPanel()
		}
		middleContentPanel.add(getStatuPanel(), BorderLayout.SOUTH); // 状态栏,在下面

		//最后的界面合并!!!
		this.setLayout(new BorderLayout()); //
		if (!"skip2".equals(System.getProperty("REQ_logintype"))) { //如果是skip2登录方式,则根本不加入图片!!!
			if (!tbUtil.getSysOptionBooleanValue("首页是否隐藏标题图片", false)) { //加个隐藏标题图片的参数 Gwang 2012-11-14
				this.add(getTitlePanel(), BorderLayout.NORTH); //标题图片栏!在上面				
			}
		}

		if (li_deskTopLayout != 3 && li_deskTopLayout != 4) {
			this.add(middleContentPanel, BorderLayout.CENTER); //中间的内容
		} else if (li_deskTopLayout == 3) {
			initNewDeskTopTabPane(); //按钮风格。
		} else if (li_deskTopLayout == 4) {
			initNewDeskTopTabPane2();
		}
		//如果有自动找末的功能点,则有可能
		if (tbUtil.getSysOptionBooleanValue("首页是否隐藏功能菜单树", false)) { //如果有参数决定是默认隐藏左边的菜单树,则收缩之!
			if (splitPanel_2 != null) {
				splitPanel_2.setDividerLocation(0); //
				splitPanel_2.setDividerSize(0);
			}
			if (splitLeftTopPanel != null) {
				splitLeftTopPanel.setVisible(false); //
			}
		}
		if (li_deskTopLayout == 5) {
			splitPanel_2.setDividerLocation(100);
		}
		//后续处理...
		if ("skip2".equals(System.getProperty("REQ_logintype"))) { //如果是skip2登录方式,则打开这个功能点,不处理默认的自动加载机制了!
			String str_menuid = System.getProperty("REQ_menuid"); //要打开哪个菜单!!
			openAppMainFrameWindowById(str_menuid); //
		} else { //如果是正常登录,则自动查找数据库中的结点!
			boolean isLoadCustPage = execAutoStartNode(); // 自动找开自启的结点!!
			if (!isLoadCustPage) { //如果没有加载自定义页面,则加载首页!
				String str_ppp = tbUtil.getSysOptionStringValue("登录时强制提醒", null); //
				if (str_ppp == null) { //如果只显示提示界面,则不做
					//这是为了提高登录性能!!!因为登录本身很慢,然后再加载一个消息中心,则性能测试时非常慢!![xch/2012-09-13]这样先搞个提示信息,性能大为提高!
					//事实上经常有一些提示消息要发布!!!
					if (li_deskTopLayout != 3 && li_deskTopLayout != 4) {
						lazyLoadIndexPage(false); //懒装入首页!!!
					}
				} else {

				}
			}
		}

		String timeouttime = new TBUtil().getSysOptionStringValue("超时时间", null);//超时验证 科工提出
		if (timeouttime != null && !timeouttime.trim().equals("")) {//很多情况客户配置了参数但值设置为空，这里需要判断一下【李春娟/2012-10-30】
			new cn.com.infostrategy.ui.sysapp.other.TimeOutChecker(this, Integer.parseInt(timeouttime)).start();
		}
		//此类实现WLTJobIFC接口即可。[郝明2012-11-30]
		String classPath = tbUtil.getSysOptionStringValue("打开首页自动调用类", "");//登陆系统时，项目上自己可以自己实现一个类来实现些逻辑，例如改密码，首次登陆系统简易导航操作提示，系统自动升级等等。
		if (classPath != null && !classPath.equals("")) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() { //单独开一个线程
						public void run() { //打开页面后，自动调用的类，目前用于自动升级。类需要实现接口 WLTJobIFC
							String classPath = tbUtil.getSysOptionStringValue("打开首页自动调用类", "");
							try {
								Object job = Class.forName(classPath).newInstance();
								if (job instanceof WLTJobIFC) {
									((WLTJobIFC) job).run();
								}
							} catch (Exception e) {
								System.out.println("首页启动时调用参数[打开首页自动调用类]=[" + classPath + "]出现错误");
								e.printStackTrace();
							}
						}
					}, 5); //
		}

		//首页是否显示动态提醒 【杨科/2013-06-05】
		Boolean remind = tbUtil.getSysOptionBooleanValue("首页是否显示动态提醒", false);
		if (remind) {
			Timer timer_task = new Timer();
			timer_task.scheduleAtFixedRate(new TimerTask() {

				public void run() {
					try {
						SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class);
						ArrayList al = service.getRemindDatas(ClientEnvironment.getInstance().getLoginUserID());
						if (al != null && al.size() > 0) {
							for (int i = 0; i < al.size(); i++) {
								taskLabel.setIcon(UIUtil.getImage("new.gif"));
								taskLabel.setText((String) al.get(i));
								Thread.sleep(2000);
							}
						} else {
							taskLabel.setText("");
							taskLabel.setIcon(null);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}, 2000, 60000);
		}
		new Timer().schedule(new TimerTask() {
			public void run() {
				//以前郝明为了行政事业单位内控写的，后来因和其他项目冲突就给注释掉了，现在可增加参数配置，实现Bom按钮的提示功能【李春娟/2014-03-19】
				String templetcode = tbUtil.getSysOptionStringValue("BOM按钮提示模板", "");
				if (templetcode != null && !templetcode.trim().equals("")) {
					bomLabelShowToolTip = new HashMap();
					try {
						cn.com.infostrategy.to.mdata.Pub_Templet_1VO vo = UIUtil.getPub_Templet_1VO(templetcode.trim());
						HashVO vos[] = UIUtil.getHashVoArrayByDS(null, "select * from " + vo.getSavedtablename());
						for (int i = 0; i < vos.length; i++) {
							bomLabelShowToolTip.put(vos[i].getStringValue(vo.getTostringkey()), vos[i]);
						}
						cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO itemvos[] = vo.getItemVos();
						for (int i = 0; i < itemvos.length; i++) {
							bomLabelShowToolTip.put(itemvos[i].getItemkey().toLowerCase(), itemvos[i].getItemname());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}, 5);

		ToolTipManager.sharedInstance().setDismissDelay(15000); //设置tooltip提示时间为15秒
	}

	/*
	 * 菜单风格5，大按钮根菜单，子节点平铺展开。
	 */
	private JPanel initDeskTopStyle5() {
		//平铺
		HashVO[] firstLevelMenuVOs = getFirstLevelData(); //取得第一层的功能点,因为要用来创建抽屉!!
		WLTPanel menu_type_5_panel = new WLTPanel(new BorderLayout());
		JPanel innserpanel = new WLTPanel(new BorderLayout());
		menu_type_5_panel.add(innserpanel);
		//0, i == 0 ? 0 : GridBagConstraints.RELATIVE, 0, 2, 1.0, 0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0
		for (int i = 0; i < firstLevelMenuVOs.length; i++) { //遍历各个一级菜单!!!
			String str_iconName = (firstLevelMenuVOs[i].getStringValue("ICON") == null ? "32_32_02.gif" : firstLevelMenuVOs[i].getStringValue("ICON")); //
			String menuname = firstLevelMenuVOs[i].getStringValue("NAME");
			WLTLabel btn = new WLTLabel(menuname) {
				protected void paintComponent(Graphics g) {
					//						IconFactory.getInstance().getButtonIcon_DisableGray().draw(g, 0, 0, getWidth(), getHeight());
					LAFUtil.drawButtonBackground((Graphics2D) g, 0, 0, getWidth() - 1, getHeight() - 1, 10, getBackground(), 1f);
					super.paintComponent(g);
				}
			};
			btn.setHorizontalAlignment(WLTLabel.CENTER);
			btn.setVerticalAlignment(WLTLabel.CENTER);
			btn.setVerticalTextPosition(WLTLabel.CENTER);
			//				btn.setOpaque(true);
			btn.setBackground(LookAndFeel.desktop_OutLookBarBg);
			btn.setIcon(UIUtil.getImage(str_iconName));
			btn.putClientProperty("menu", firstLevelMenuVOs[i]);
			btn.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent mouseevent) {
					if (mouseevent.getSource() instanceof WLTLabel) {
						WLTLabel btn = (WLTLabel) mouseevent.getSource();
						HashVO hvo = (HashVO) btn.getClientProperty("menu");
						DefaultMutableTreeNode typenode = findNodeByID(hvo.getStringValue("id"));
						ArrayList list = new ArrayList();
						Font big_font = new Font("宋体", Font.PLAIN, 13);
						float height = 0;
						for (Enumeration e = typenode.children(); e.hasMoreElements();) {
							DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // 找到该儿子!!
							Object menuvo = childNode.getUserObject();
							if (menuvo instanceof HashVO) {
								HashVO menuHvo = (HashVO) menuvo;
								String menuname = menuHvo.getStringValue("name");
								JLabel titleLabel = new JLabel(menuname);
								titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
								titleLabel.setFont(big_font);
								titleLabel.setForeground(Color.BLUE);
								ArrayList al_nodes = new ArrayList(); //
								visitAllNodes(al_nodes, childNode); //
								DefaultMutableTreeNode[] childNodes = (DefaultMutableTreeNode[]) al_nodes.toArray(new DefaultMutableTreeNode[0]); //
								int leafnum = 0;
								for (int i = 0; i < childNodes.length; i++) {
									if (childNodes[i].isLeaf()) {
										leafnum++;
									}
								}
								JPanel panel = new JPanel(new GridLayout(leafnum / 4 + (leafnum % 4 == 0 ? 0 : 1), 4));
								for (int i = 0; i < childNodes.length; i++) {
									if (childNodes[i].isLeaf()) { //只有叶子结点才加入!!
										HashVO hvo_item = (HashVO) childNodes[i].getUserObject(); //
										JLabel l_clickmenu = new JLabel(hvo_item.getStringValue("name"));
										l_clickmenu.setIcon(UIUtil.getImage(hvo_item.getStringValue("ICON")));
										l_clickmenu.setPreferredSize(new Dimension(100, 20));
										l_clickmenu.putClientProperty("hvo", hvo_item);
										l_clickmenu.addMouseListener(this);
										l_clickmenu.setCursor(new Cursor(Cursor.HAND_CURSOR));
										panel.add(l_clickmenu);
									}
								}
								int cha = 4 - leafnum % 4;
								if (cha > 0 && cha < 4) {
									for (int j = 0; j < cha; j++) {
										panel.add(new JLabel(""));
									}
								}
								JPanel flow = new JPanel(new BorderLayout());
								flow.add(titleLabel, BorderLayout.NORTH);
								flow.add(panel, BorderLayout.CENTER);
								panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
								list.add(flow);
							}
						}
						GridBagLayout gridbag_type = new GridBagLayout();
						GridBagConstraints con = new GridBagConstraints();
						tilePanel = new JPanel(gridbag_type);
						for (int j = 0; j < list.size(); j++) {
							JComponent com = (JComponent) list.get(j);
							com.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
							con.gridx = 0;
							con.gridy = (j == 0 ? 0 : GridBagConstraints.RELATIVE);
							con.weightx = 1;
							con.fill = GridBagConstraints.BOTH;
							gridbag_type.setConstraints(com, con);
							tilePanel.add(com);
							height += com.getPreferredSize().getHeight();
						}
						tilePanel.setOpaque(true);
						tilePanel.requestFocus(true);
						tile_pop_menu = new JPopupMenu();
						btn.putClientProperty("POPMENUTYPE", ScrollablePopupFactory.POP_TYPE_HORIZONTAL);
						JScrollPane scrollpanel = new JScrollPane();
						scrollpanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
						scrollpanel.setPreferredSize(new Dimension(650, height > 600 ? 600 : (int) height));
						scrollpanel.getViewport().add(tilePanel);
						scrollpanel.getVerticalScrollBar().setUnitIncrement(15);
						tile_pop_menu.add(scrollpanel);
						tile_pop_menu.show(btn, btn.getWidth(), 0);
					} else if (mouseevent.getSource() instanceof JLabel) {
						JLabel l = (JLabel) mouseevent.getSource();
						l.setForeground(Color.RED);
					}
				}

				@Override
				public void mouseClicked(MouseEvent mouseevent) {
					if (mouseevent.getSource() instanceof JLabel) {
						JLabel label = (JLabel) mouseevent.getSource();
						HashVO hvo = (HashVO) label.getClientProperty("hvo");
						if (hvo != null) {
							openAppMainFrameWindow(hvo);
							if (tile_pop_menu != null) {
								tile_pop_menu.setVisible(false);
							}
						}
					}
				}

				@Override
				public void mouseExited(MouseEvent e) {
					if (e.getSource() instanceof JLabel) {
						JLabel l = (JLabel) e.getSource();
						l.setForeground(Color.BLACK);
					}
				}
			});
			btn.setPreferredSize(new Dimension(100, 80));
			innserpanel.add(btn, BorderLayout.NORTH);
			JPanel newPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
			innserpanel.add(newPanel, BorderLayout.CENTER);
			innserpanel = newPanel;
		}
		return menu_type_5_panel;
	}

	//按钮风格页面加载。
	ArrayList btnSytleConfig = new ArrayList();

	private void initHomePageRadioPanel() {
		//为了让首页更丰富,首页有4种风格,但中铁建客户说不要这么多,只要一种公告消息!所以又加了个总开关!
		int li_indexLayout = getIndexLayout(); //首页布局!
		if (deskIndexRadioPanel != null)
			return;
		boolean[] bo_indexIsAdded = new boolean[] { false, false, false, false }; //
		deskIndexRadioPanel = new WLTRadioPane(LookAndFeel.systabbedPanelSelectedBackground); //
		String str_welcomeInfo = tbUtil.getSysOptionStringValue("登录时强制提醒", null); //
		if (str_welcomeInfo != null) { //
			deskIndexRadioPanel.setAutoSelectFirst(false); //
		}

		if (tbUtil.getSysOptionBooleanValue("首页是否显示公告消息", true)) {
			deskIndexRadioPanel.addTab(str_indexarray[0], str_indexarray[0], "site.gif", getWelComePanel(str_welcomeInfo)); //首页面板!!! 关键UI,以前首页效果不好,就是在这里!!!!getAllTaskPanel()
			bo_indexIsAdded[0] = true; //
		}
		if (tbUtil.getSysOptionBooleanValue("首页是否显示任务中心", true)) {
			deskIndexRadioPanel.addTab(str_indexarray[1], str_indexarray[1], "zt_057.gif", getWelComePanel(str_welcomeInfo)); //
			bo_indexIsAdded[1] = true; //
		}
		if (tbUtil.getSysOptionBooleanValue("首页是否显示快捷访问", true)) {
			deskIndexRadioPanel.addTab(str_indexarray[2], str_indexarray[2], "office_190.gif", getWelComePanel(str_welcomeInfo)); //
			bo_indexIsAdded[2] = true; //
		}
		if (tbUtil.getSysOptionBooleanValue("首页是否显示管理导航图", true)) {
			deskIndexRadioPanel.addTab(str_indexarray[3], str_indexarray[3], "office_200.gif", getWelComePanel(str_welcomeInfo)); //
			bo_indexIsAdded[3] = true; //
		}

		if (bo_indexIsAdded[li_indexLayout - 1]) { //如果有!!
			str_indexPageStyle = str_indexarray[li_indexLayout - 1];
		} else {
			str_indexPageStyle = str_indexarray[0]; //
		}

		deskIndexRadioPanel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				WLTRadioPane rp = (WLTRadioPane) e.getSource(); //
				onClickIndexRadioBtn(rp.getSelectKey()); //监听	
				updateUserLoginStyle(); //修改状态
			}
		});

		if (deskIndexRadioPanel.getButtons().length == 1) {
			deskIndexRadioPanel.setBtnPanelVisible(false);
		}

		workTabbedPanel.addTab(UIUtil.getLoginLanguage("首页"), UIUtil.getImage("zt_037.gif"), deskIndexRadioPanel); //
		WLTTabbedPane.CloseTabedPaneBtn btn = (CloseTabedPaneBtn) workTabbedPanel.getJButtonAt(0);
		btn.setCanClose(false);

	}

	private void initNewDeskTopTabPane2() {
		try {
			HashVO vos[] = new HashVO[0];
			try {
				if ("admin".equalsIgnoreCase(ClientEnvironment.getCurrSessionVO().getLoginUserCode())) {
					if (ClientEnvironment.isAdmin()) {
						vos = UIUtil.getHashVoArrayByDS(null, "select * from pub_desktop_btn where visible='Y' or visible is null order by seq");
					} else {
						RoleVO[] rulevos = (RoleVO[]) ClientEnvironment.getCurrLoginUserVO().getRoleVOs();
						StringBuffer likesb = new StringBuffer();
						if (rulevos.length > 0) {
							likesb.append(" 1=2 ");
							for (int i = 0; i < rulevos.length; i++) {
								likesb.append(" or openroles like '%;" + rulevos[i].getId() + ";%' ");
							}
						}
						if (likesb.length() > 0) {
							vos = UIUtil.getHashVoArrayByDS(null, "select * from pub_desktop_btn where  ((visible='Y' or visible is null)  and (" + likesb.toString() + ")) or openroles is null   order by seq ");
						} else {
							vos = UIUtil.getHashVoArrayByDS(null, "select * from pub_desktop_btn where  (visible='Y' or visible is null) and (openroles is null ) order by seq ");
						}
					}
				} else {
					RoleVO[] rulevos = (RoleVO[]) ClientEnvironment.getCurrLoginUserVO().getRoleVOs();
					String rules_2[] = UIUtil.getStringArrayFirstColByDS(null, "select id from pub_role where code in ('一般员工','一般人员','一般用户','所有人员','普通员工','普通人员','普通用户')");
					StringBuffer likesb = new StringBuffer();
					if (rulevos.length + rules_2.length > 0) {
						likesb.append(" 1=2 ");
						for (int i = 0; i < rulevos.length; i++) {
							likesb.append(" or openroles like '%;" + rulevos[i].getId() + ";%' ");
						}
						for (int i = 0; i < rules_2.length; i++) {
							likesb.append(" or openroles like '%;" + rules_2[i] + ";%' ");
						}
					} else {
						likesb.append(" 1=1 ");
					}
					vos = UIUtil.getHashVoArrayByDS(null, "select * from pub_desktop_btn where  (visible='Y' or visible is null)  and (" + likesb.toString() + ")  order by seq ");
				}
			} catch (Exception e) {
				//没有首页动态切换按钮配置表。hm by 2013-5-17
			}
			if (vos.length > 0) {
				newDeskTopTabPane_2 = new WLTTabbedPane();
			}
			for (int j = 0; j < vos.length; j++) {
				String image = vos[j].getStringValue("image");
				String text = vos[j].getStringValue("text");
				String action = vos[j].getStringValue("action");
				btnSytleConfig.add(j, vos[j]); //加入
				newDeskTopTabPane_2.addTab(text, new ImageIcon(tbutil.getImageScale(UIUtil.getImage(image).getImage(), 16, 16)), new JPanel());
			}
			if (vos.length == 0) {
				i_barMenuPanel = new I_BarMenuAndWorkPanel(null, null, allTreeRootNode);
				initHomePageRadioPanel();
				lazyLoadIndexPage(false); //加载数据
				i_barMenuPanel.getWorkTabbedPanel().addTab(UIUtil.getLoginLanguage("首页"), UIUtil.getImage("zt_037.gif"), deskIndexRadioPanel); //
				WLTTabbedPane.CloseTabedPaneBtn btn = (CloseTabedPaneBtn) i_barMenuPanel.getWorkTabbedPanel().getJButtonAt(0);
				btn.setCanClose(false);
				this.add(getStatuPanel(), BorderLayout.SOUTH); //添加状态栏
				this.add(i_barMenuPanel, BorderLayout.CENTER);
				return;
			}
			newDeskTopTabPane_2.setSelectedIndex(0);
			onClickIndexITabedPane(0);
			JPanel quickOrientation = new JPanel(new BorderLayout(15, 15));
			quickOrientation.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
			quickOrientation.setOpaque(false);
			this.add(newDeskTopTabPane_2, BorderLayout.CENTER);
			newDeskTopTabPane_2.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					WLTTabbedPane rp = (WLTTabbedPane) e.getSource(); //
					onClickIndexITabedPane(rp.getSelectedIndex()); //监听	
				}
			});
			if (splitPanel_2 != null) { //
				splitPanel_2.setDividerLocation(0); //
				splitPanel_2.setDividerSize(0);
			}
			if (splitLeftTopPanel != null) {
				splitLeftTopPanel.setVisible(false); //
			}
			this.add(getStatuPanel(), BorderLayout.SOUTH); //添加状态栏
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}

	private I_BarMenuAndWorkPanel i_barMenuPanel;

	private void initNewDeskTopTabPane() {
		try {
			HashVO vos[] = new HashVO[0];
			try {
				if ("admin".equalsIgnoreCase(ClientEnvironment.getCurrSessionVO().getLoginUserCode())) {
					if (ClientEnvironment.isAdmin()) {
						vos = UIUtil.getHashVoArrayByDS(null, "select * from pub_desktop_btn where visible='Y' or visible is null order by seq");
					} else {
						RoleVO[] rulevos = (RoleVO[]) ClientEnvironment.getCurrLoginUserVO().getRoleVOs();
						StringBuffer likesb = new StringBuffer();
						if (rulevos.length > 0) {
							likesb.append(" 1=2 ");
							for (int i = 0; i < rulevos.length; i++) {
								likesb.append(" or openroles like '%;" + rulevos[i].getId() + ";%' ");
							}
						}
						if (likesb.length() > 0) {
							vos = UIUtil.getHashVoArrayByDS(null, "select * from pub_desktop_btn where  ((visible='Y' or visible is null)  and (" + likesb.toString() + ")) or openroles is null   order by seq ");
						} else {
							vos = UIUtil.getHashVoArrayByDS(null, "select * from pub_desktop_btn where  (visible='Y' or visible is null) and (openroles is null ) order by seq ");
						}
					}
				} else {
					RoleVO[] rulevos = (RoleVO[]) ClientEnvironment.getCurrLoginUserVO().getRoleVOs();
					String rules_2[] = UIUtil.getStringArrayFirstColByDS(null, "select id from pub_role where code in ('一般员工','一般人员','一般用户','所有人员','普通员工','普通人员','普通用户')");
					StringBuffer likesb = new StringBuffer();
					if (rulevos.length + rules_2.length > 0) {
						likesb.append(" 1=2 ");
						for (int i = 0; i < rulevos.length; i++) {
							likesb.append(" or openroles like '%;" + rulevos[i].getId() + ";%' ");
						}
						for (int i = 0; i < rules_2.length; i++) {
							likesb.append(" or openroles like '%;" + rules_2[i] + ";%' ");
						}
					} else {
						likesb.append(" 1=1 ");
					}
					vos = UIUtil.getHashVoArrayByDS(null, "select * from pub_desktop_btn where  (visible='Y' or visible is null)  and (" + likesb.toString() + ")  order by seq ");
				}
			} catch (Exception e) {
				//没有首页动态切换按钮配置表。hm by 2013-5-17
			}
			if (vos.length > 0) {
				newDeskTopTabPane = new I_WLTTabbedPane();
			}
			for (int j = 0; j < vos.length; j++) {
				String image = vos[j].getStringValue("image");
				String text = vos[j].getStringValue("text");
				String action = vos[j].getStringValue("action");
				btnSytleConfig.add(j, vos[j]); //加入
				newDeskTopTabPane.addTab(text, new ImageIcon(tbutil.getImageScale(UIUtil.getImage(image).getImage(), 70, 70)), new JPanel());
			}
			if (vos.length == 0) {
				i_barMenuPanel = new I_BarMenuAndWorkPanel(null, null, allTreeRootNode);
				initHomePageRadioPanel();
				lazyLoadIndexPage(false); //加载数据
				i_barMenuPanel.getWorkTabbedPanel().addTab(UIUtil.getLoginLanguage("首页"), UIUtil.getImage("zt_037.gif"), deskIndexRadioPanel); //
				WLTTabbedPane.CloseTabedPaneBtn btn = (CloseTabedPaneBtn) i_barMenuPanel.getWorkTabbedPanel().getJButtonAt(0);
				btn.setCanClose(false);
				this.add(getStatuPanel(), BorderLayout.SOUTH); //添加状态栏
				this.add(i_barMenuPanel, BorderLayout.CENTER);
				return;
			}
			newDeskTopTabPane.setSelectedIndex(0);
			onClickIndexITabedPane(0);
			if (vos.length == 1 && !"admin".equals(ClientEnvironment.getCurrSessionVO().getLoginUserCode())) {
				newDeskTopTabPane.setBtnPanelCloseForever();
			}
			JPanel quickOrientation = new JPanel(new BorderLayout(15, 15));
			quickOrientation.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5));
			quickOrientation.setOpaque(false);
			searchField = new JTextField("快速查找功能");
			final JPopupMenu pop = new JPopupMenu();
			searchField.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if ("快速查找功能".equals(searchField.getText())) {
						searchField.setText(""); //置空
					}
				}
			});
			searchField.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					if ("".equals(searchField.getText().trim())) {
						searchField.setText("快速查找功能");
					}
				}
			});
			searchField.addKeyListener(new KeyAdapter() { //给搜索文本框添加键盘事件
						public synchronized void keyReleased(KeyEvent e) {

							pop.removeAll();
							int size = al_allLeafNodes.size();
							String text = searchField.getText();
							if (text == null || text.trim().equals("")) {
								pop.setVisible(false);
								return;
							}
							int height = 0;
							int menusize = 0;
							for (int i = 0; i < size; i++) {
								HashVO vo = (HashVO) al_allLeafNodes.get(i);
								String name = vo.getStringValue("name");
								if (name.contains(text.trim())) { //如果包含此功能点，加入到pop中
									JMenuItem item = new JMenuItem(name);
									item.setPreferredSize(new Dimension(140, 23));
									height += 23;
									item.addActionListener(new ActionListener() {
										public synchronized void actionPerformed(ActionEvent e) {
											JMenuItem item = (JMenuItem) e.getSource();
											searchField.setText(item.getText());
											HashVO vo = (HashVO) item.getClientProperty("menuvo");
											String menuID = vo.getStringValue("id"); //取到选择的ID
											openMenuBySearchID(menuID);
										}
									});
									menusize++;
									item.putClientProperty("menuvo", vo);
									pop.add(item);
								}
							}
							if (menusize > 0) {
								pop.setVisible(false);
								pop.show(searchField, 0, 20);
								searchField.requestFocus(); //文本框再次获取焦点,微软拼音有问题
							}
						}
					});
			searchField.transferFocus(); //默认无焦点
			searchField.setPreferredSize(new Dimension(90, 23));
			quickOrientation.add(searchField, BorderLayout.SOUTH);
			if (ClientEnvironment.isAdmin()) {
				quickOrientation.add(getToolBarPanel(), BorderLayout.EAST);
			}
			newDeskTopTabPane.getTopMainPanel().add(quickOrientation, BorderLayout.EAST);
			this.add(newDeskTopTabPane, BorderLayout.CENTER);
			newDeskTopTabPane.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					I_WLTTabbedPane rp = (I_WLTTabbedPane) e.getSource(); //
					onClickIndexITabedPane(rp.getSelectedIndex()); //监听	
				}
			});
			if (splitPanel_2 != null) { //
				splitPanel_2.setDividerLocation(0); //
				splitPanel_2.setDividerSize(0);
			}
			if (splitLeftTopPanel != null) {
				splitLeftTopPanel.setVisible(false); //
			}
			this.add(getStatuPanel(), BorderLayout.SOUTH); //添加状态栏
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}

	/**
	 * 
	* @Title: getNewDeskTopTabPane 
	* @Description: 新动态切换效果TabPane
	* @return I_WLTTabbedPane
	* @throws
	 */
	public I_WLTTabbedPane getNewDeskTopTabPane() {
		return newDeskTopTabPane;
	}

	public Object getBomLabelToolTipHashVO(String _fname) {
		return bomLabelShowToolTip.get(_fname);
	}

	/**
	 * @Title: expandTopBtnPane 
	 * @Description: 如果是第3种登陆模式，收缩或展开顶部面板
	 * @throws
	 */
	public void expandTopBtnPane(boolean _flag) {
		if (getDesktopLayout() == 3) {
			if (_flag) {
				getNewDeskTopTabPane().expandPane();
			} else {
				getNewDeskTopTabPane().unexpandPane();
			}
		}
	}

	/*
	 * 首页最上方按钮切换
	 */
	HashMap haveLoad = new HashMap(); // 按钮风格加载缓存标示

	private void onClickIndexITabedPane(int _index) {
		final int li_deskTopLayout = getDesktopLayout(); //桌面布局!
		JComponent showjp = new JPanel();
		HashVO configVO = (HashVO) btnSytleConfig.get(_index);
		if (haveLoad.containsKey(_index)) {
			return;
		} else {
			try {
				String text = configVO.getStringValue("text");
				String action = configVO.getStringValue("action");
				if (action != null && "bom图".equals(configVO.getStringValue("loadtype"))) {//
					BillBomPanel bomPanel = (BillBomPanel) getBomPanel(action);
					bomPanel.setShowNorthPanel(false);
					bomPanel.getBomItemPanels()[0].putClientProperty("parentname", text);
					showjp = bomPanel;
				} else if (action != null) {
					I_DeskTopPanelBtnStyleActionIfc ifc = (I_DeskTopPanelBtnStyleActionIfc) Class.forName(action).newInstance();
					showjp = ifc.afterClickComponent(configVO);
				}
			} catch (ClassNotFoundException e) {
				JPanel jp = new JPanel(new FlowLayout(FlowLayout.CENTER));
				JTextPane label = new JTextPane();
				label.setText("没有找到类:" + configVO.getStringValue("action"));
				jp.add(label);
				showjp = jp;
				e.printStackTrace();
			} catch (Exception e) {
				JPanel jp = new JPanel(new FlowLayout(FlowLayout.CENTER));
				JTextPane label = new JTextPane();
				label.setText(e.getMessage());
				jp.add(label);
				showjp = jp;
				e.printStackTrace();
			}
			haveLoad.put(_index, showjp);
		}
		if (li_deskTopLayout == 3) {
			newDeskTopTabPane.getComponentAt(_index).removeAll();
			newDeskTopTabPane.getComponentAt(_index).setLayout(new BorderLayout());
			newDeskTopTabPane.getComponentAt(_index).add(showjp);
		} else {
			newDeskTopTabPane_2.getComponentAt(_index).removeAll();
			newDeskTopTabPane_2.getComponentAt(_index).setLayout(new BorderLayout());
			newDeskTopTabPane_2.getComponentAt(_index).add(showjp);
		}
	}

	//按钮风格返回配置数据
	public ArrayList getBtnStyleConfig() {
		return btnSytleConfig;
	}

	/**
	 * @see I_DTPTaskBtnAction 中被强制反射调用.
	 */
	private JPanel getTaskAndMsgCenterPanel() {
		if (taskAndMsgCenterPanel == null) {
			try {
				taskAndMsgCenterPanel = (JPanel) Class.forName("cn.com.infostrategy.ui.sysapp.login.taskcenter.TaskAndMsgCenterPanel").newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			} //
			return taskAndMsgCenterPanel;
		}
		return taskAndMsgCenterPanel;
	}

	private JPanel getWelComePanel(String _msg) {
		JPanel panel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		JTextArea textAra = new JTextArea(_msg == null ? "需要定义参数[登录时强制提醒]" : _msg); //
		textAra.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); //
		textAra.setOpaque(false); //
		textAra.setEditable(false); //
		panel.add(textAra); //
		return panel; //
	}

	/**
	 * 懒装入首页!!!
	 * @param _isSelectChange
	 */
	private void lazyLoadIndexPage(boolean _isSelectChange) {
		if (isLoadedIndexPage) { //如果已加载首页,则直接返回!!!
			int li_index = workTabbedPanel.getSelectedIndex(); //选择的索引号!!!
			if (li_index == 0 && deskIndexRadioPanel != null) { //如果切换回了首页,且原来选中的又是工作流,则再次刷新工作流!
				if ("任务中心".equals(deskIndexRadioPanel.getSelectKey())) { //如果是任务中心,则要再次刷新工作流,因为兴业客户反应提交工作流后需要
					reloadWorkFlowTask(); //
				}
			}
			return; //
		}

		if (_isSelectChange) { //如果是选择变化,而且不是第一个!
			int li_index = workTabbedPanel.getSelectedIndex(); //选择的索引号!!!
			if (li_index != 0) { //如果不是首页,则直接返回!
				return; //
			}
		}

		//如果是首页,而且是第一次打开!则创建对应的页面,该是哪个就是哪个!!
		if (deskIndexRadioPanel != null) { //首页支持多风格!!则动态判断创建哪一个!!
			if (deskIndexRadioPanel.isSellectedOneItem()) { //如果点击过了!!
				onClickIndexRadioBtn(str_indexPageStyle); //只做切换,不修改pub_user.desktopStyle	
			}
		} else { //如果不是多风格,则直接创建消息中心!!!
			indexPageContainer.removeAll(); //
			//			indexPageContainer.setLayout(new BorderLayout()); //
			indexPageContainer.add(new IndexPanel(this)); //
		}
		isLoadedIndexPage = true; //
	}

	/**
	 * 初始化所有树数据!!!关键的计算!!
	 */
	private void initAllTreeData() {
		HashVO[] allMenuHVOs = getDeskTopVO().getMenuVOs(); // 取得数据!!
		for (int i = 0; i < allMenuHVOs.length; i++) {
			allMenuHVOs[i].setToStringFieldName(getMenuTreeViewFieldName()); //
		}
		HashVO rootVO = new HashVO(); // ////....
		rootVO.setAttributeValue("ID", "ROOT"); //
		rootVO.setAttributeValue("CODE", "ROOT"); //
		rootVO.setAttributeValue("NAME", "ROOT"); //
		allTreeRootNode = new DefaultMutableTreeNode(rootVO); // 创建根结点
		DefaultMutableTreeNode[] node_level_1 = new DefaultMutableTreeNode[allMenuHVOs.length]; // 创建所有结点数组

		for (int i = 0; i < allMenuHVOs.length; i++) {
			node_level_1[i] = new DefaultMutableTreeNode(allMenuHVOs[i]); // 创建各个结点
			allTreeRootNode.add(node_level_1[i]); // 加入根结点
		}
		// 构建树..
		for (int i = 0; i < node_level_1.length; i++) {
			HashVO nodeVO = (HashVO) node_level_1[i].getUserObject();
			String str_pk_parentPK = nodeVO.getStringValue("parentmenuid"); // 父亲主键
			for (int j = 0; j < node_level_1.length; j++) {
				HashVO nodeVO_2 = (HashVO) node_level_1[j].getUserObject();
				String str_pk_2 = nodeVO_2.getStringValue("id"); // 主键
				if (str_pk_2.equals(str_pk_parentPK)) // 如果发现该结点主键正好是上层循环的父亲结点,则将其作为我的儿子处理加入
				{
					try {
						node_level_1[j].add(node_level_1[i]);
						break; // 退出内层循环,提高性能
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		//计算需要自启动的结点,以及路径!!!
		DefaultMutableTreeNode[] allNodes = getAllNodes(); //
		for (int i = 0; i < allNodes.length; i++) {
			if (allNodes[i].isLeaf()) { //如果是叶子结点!
				HashVO nodeVO = (HashVO) allNodes[i].getUserObject(); //该结点绑定的数据!!!
				String str_pathName = ""; //
				TreeNode[] allparentNodes = allNodes[i].getPath(); // 取得从Root到他的所有结点
				if (allparentNodes.length > 1) { //所有父亲结点
					for (int j = 1; j < allparentNodes.length - 1; j++) { //
						HashVO parentHVO = (HashVO) ((DefaultMutableTreeNode) allparentNodes[j]).getUserObject(); //
						String str_parentName = parentHVO.getStringValue("name"); //
						str_pathName = str_pathName + str_parentName + " → "; //
					}
				}
				str_pathName = str_pathName + nodeVO.getStringValue("name"); //
				nodeVO.setAttributeValue("$TreePath", str_pathName); //
				al_allLeafNodes.add(nodeVO); //加入数据!!!

				//记录是否是叶子结点!!
				Boolean bo_isautostart = nodeVO.getBooleanValue("isautostart", false); // 是否自启动??
				if (bo_isautostart.booleanValue()) {
					al_autostartNodes.add(allNodes[i]); //
				}
			}
		}
	}

	//标题图片面板!!
	private JPanel getTitlePanel() {
		if (title_panel != null) {
			return title_panel;
		}
		String str_titlegifname = "titlecenter_default"; //
		if (System.getProperty("INNERSYSTEM") != null && !System.getProperty("INNERSYSTEM").equals("") && ClientEnvironment.chooseISys != null && !ClientEnvironment.chooseISys.equals("")) { //INNERSYSTEM
			str_titlegifname = "titlecenter_" + ClientEnvironment.chooseISys; //进入某个子系统
		} else {
			if (System.getProperty("PROJECT_SHORTNAME") != null) { //有项目名称
				str_titlegifname = "titlecenter_" + System.getProperty("PROJECT_SHORTNAME"); // 使用shortname
			}
		}
		String str_realimgName = str_titlegifname + ".jpg"; //
		ImageIcon imgIcon = UIUtil.getImageFromServer("/applet/" + str_realimgName); //图片!!
		if (imgIcon == null || imgIcon.getImageLoadStatus() == java.awt.MediaTracker.ERRORED) { //jpg取不到,再取gif
			str_realimgName = str_titlegifname + ".gif"; //
			imgIcon = UIUtil.getImageFromServer("/applet/" + str_realimgName); //图片
		}
		title_panel = new JPanel(new BorderLayout(0, 0)); //
		title_panel.setBackground(LookAndFeel.defaultShadeColor1); //
		title_panel.setToolTipText(str_realimgName + "[" + imgIcon.getIconWidth() + "*" + imgIcon.getIconHeight() + "]"); //
		title_panel.setUI(new MyImgPanelUI(imgIcon)); //
		JPanel panel_west = new JPanel(); //
		panel_west.setOpaque(false); //透明!!

		panel_west.setPreferredSize(new Dimension(400, imgIcon.getIconHeight())); //图片的高度!!!左边400个像素永远是留给Logo用的,后面的给快捷按钮!!
		title_panel.add(panel_west, BorderLayout.WEST); //左边

		int li_text_align = FlowLayout.RIGHT; //中铁建的王部长喜欢靠左,没办法只能又搞个参数!
		String str_optiontext = tbUtil.getSysOptionStringValue("首页标题中快捷按钮排列位置", "居右"); //
		if (str_optiontext.equals("居左")) {
			li_text_align = FlowLayout.LEFT; //
		} else if (str_optiontext.equals("居中")) {
			li_text_align = FlowLayout.CENTER; //
		} else if (str_optiontext.equals("居右")) {
			li_text_align = FlowLayout.RIGHT; //
		}

		JPanel panel_btns = new JPanel(new FlowLayout(li_text_align, 7, 0)); //
		int li_blank = (imgIcon.getIconHeight() - 32) / 2; //如果这样使图片太吊在上面下躲在下面去了,则要求美工去修改图片保证高度为55左右即可!!!
		panel_btns.setBorder(BorderFactory.createEmptyBorder(li_blank, 0, 0, 10)); //保证图片在中间!!!
		panel_btns.setOpaque(false); //透明!!
		boolean isHaveTitleShortCut = false; ////
		if (this.deskTopVO != null) {
			HashVO[] menuVOs = this.deskTopVO.getMenuVOs(); //
			for (int i = 0; i < menuVOs.length; i++) { //
				if (menuVOs[i].getBooleanValue("showintoolbar", false)) { //是否在菜单条中
					isHaveTitleShortCut = true; //
					TitleBtnPanel btnPanel = new TitleBtnPanel(menuVOs[i].getStringValue("toolbarimg"), menuVOs[i].getStringValue("name"), menuVOs[i].getStringValue("$TreePath"), menuVOs[i].getStringValue("id")); //
					panel_btns.add(btnPanel); //
				}
			}
		}
		if (isHaveTitleShortCut) { //有数据了才加入面板,如果没有则不加,使UI绘制更清爽些!!
			JPanel scrollPanel_tbtn = new WLTScrollPanel(panel_btns); //
			title_panel.add(scrollPanel_tbtn, BorderLayout.CENTER); //
		}
		title_panel.setPreferredSize(new Dimension(1024, imgIcon.getIconHeight())); //强行指定面板高度!! 这样就不会冒出一个白色框出来了!!! 如果图片大小有问题只支使快捷按钮图标搞到上再是去
		return title_panel;
	}

	/**
	 * 工具条栏面板,即左边是两个快捷按钮,中间是欢迎信息,右边是工具按钮的那一条!!! 非常关键的东东!!!
	 * @return
	 */
	private JPanel getToolBarPanel() {
		if (toolBarPanel != null) {
			return toolBarPanel; //
		}
		toolBarPanel = new JPanel(new BorderLayout(0, 0)); //
		toolBarPanel.setOpaque(false); //透明!!!
		toolBarPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 1, 1)); //一定要留点空白,否则挤得慌!!!

		//加入左边的两个快速隐藏的按钮!!!
		int li_deskTopLayout = getDesktopLayout(); //
		JPanel left_btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1)); // //
		left_btnPanel.setOpaque(false); //透明!!
		icon_up = UIUtil.getImage("office_136.gif");
		icon_right = new ImageIcon(tbUtil.getImageRotate(icon_up.getImage(), 90)); //90度!
		icon_down = new ImageIcon(tbUtil.getImageRotate(icon_up.getImage(), 180)); //180度!
		icon_left = new ImageIcon(tbUtil.getImageRotate(icon_up.getImage(), 270)); //270度!

		//加个隐藏标题图片的参数 Gwang 2012-11-14
		if (!tbUtil.getSysOptionBooleanValue("首页是否隐藏标题图片", false)) {
			btn_hiddentitle = new JButton(icon_up); //
			btn_hiddentitle.setPreferredSize(new Dimension(18, 18)); //
			btn_hiddentitle.setToolTipText("隐藏/显示标题图片"); //
			btn_hiddentitle.addActionListener(this);
			left_btnPanel.add(btn_hiddentitle); //			
		}

		ImageIcon img048 = UIUtil.getImage("office_048.gif"); //
		if (li_deskTopLayout == 3 && img048 != null) { //按钮栏风格
			img048 = new ImageIcon(tbUtil.getImageRotate(img048.getImage(), 90)); //旋转90度
		}
		btn_hiddensplit = new JButton(icon_left); //
		btn_hiddensplit.setPreferredSize(new Dimension(18, 18)); //
		btn_hiddensplit.setToolTipText("隐藏/显示菜单"); //
		btn_hiddensplit.addActionListener(this);
		if (li_deskTopLayout == 3) { //按钮栏风格时不显示这个按钮!!!
			btn_hiddensplit.putClientProperty("deskTopLayout", "3"); //
		}
		left_btnPanel.add(btn_hiddensplit); //
		if (ClientEnvironment.isAdmin()) { //如果是管理员,则可以查看注册功能!!!
			btn_sysregmenu = new JButton(UIUtil.getImage("office_042.gif")); //
			btn_sysregmenu.setPreferredSize(new Dimension(18, 18)); //
			btn_sysregmenu.setToolTipText("普信标准产品"); //
			btn_sysregmenu.addActionListener(this);
			left_btnPanel.add(btn_sysregmenu); //
		}
		if (li_deskTopLayout != 3) {
			toolBarPanel.add(left_btnPanel, BorderLayout.WEST); // 应用按钮
		}

		//加入中间的[您好,欢迎光临...]的字样!!!	
		String dayOfWeek = this.getDayOfWeek(this.longinTime);
		JLabel label = null;
		String showuser = tbUtil.getSysOptionStringValue("是否显示登陆号", "0");//是否显示登陆号,默认0为登录号+姓名，1为只显示人员姓名
		if ("1".equals(showuser)) {
			label = new JLabel(" 您好," + ClientEnvironment.getInstance().getLoginUserName() + "   登录时间:" + this.longinTime + "," + dayOfWeek + "  ", JLabel.LEFT); //
		} else {
			label = new JLabel(" 您好," + loginUserCode + "/" + ClientEnvironment.getInstance().getLoginUserName() + "   登录时间:" + this.longinTime + "," + dayOfWeek + "  ", JLabel.LEFT); //
		}
		int li_text_align = SwingConstants.LEFT; //中铁建的王部长喜欢字靠右,没办法只能又搞个参数!
		String str_optiontext = tbUtil.getSysOptionStringValue("首页欢迎提示排列位置", "居左"); //
		if (str_optiontext.equals("居左")) {
			li_text_align = SwingConstants.LEFT; //
		} else if (str_optiontext.equals("居中")) {
			li_text_align = SwingConstants.CENTER; //
		} else if (str_optiontext.equals("居右")) {
			li_text_align = SwingConstants.RIGHT; //
		}
		label.setHorizontalAlignment(li_text_align); //字靠右!中铁建王部长提出来喜欢字靠右!
		if (li_deskTopLayout != 3) {
			toolBarPanel.add(label, BorderLayout.CENTER); //加入提示说明
		}

		//加入右边的所有功能按钮,比如[查看服务器端变量][SQL查询分析器]!!
		JPopupMenu popup = new JPopupMenu(); //
		menuItem_clientenv = new JMenuItem(UIUtil.getLanguage("查看客户端环境变量")); //
		menuItem_serverenv = new JMenuItem(UIUtil.getLanguage("查看服务器环境变量")); //
		menuItem_serverconsole = new JMenuItem(UIUtil.getLanguage("查看服务器控制台")); //
		menuItem_state = new JMenuItem(UIUtil.getLanguage("查看服务器State")); //
		menuItem_serverlog = new JMenuItem(UIUtil.getLanguage("查看服务器日志")); //
		menuItem_serverscreen = new JMenuItem(UIUtil.getLanguage("抓取服务器屏幕")); //
		menuItem_serverexplorer = new JMenuItem(UIUtil.getLanguage("查看服务器文件系统")); //
		menuItem_sqltool = new JMenuItem(UIUtil.getLanguage("SQL查询分析器")); //
		menuItem_sqllistener = new JMenuItem(UIUtil.getLanguage("跟踪SQL执行")); //
		menuItem_sqlbuding = new JMenuItem(UIUtil.getLanguage("SQL补丁更新")); //

		menuItem_clientenv.setPreferredSize(new Dimension((int) menuItem_clientenv.getPreferredSize().getWidth(), 19));
		menuItem_serverenv.setPreferredSize(new Dimension((int) menuItem_serverenv.getPreferredSize().getWidth(), 19));
		menuItem_serverconsole.setPreferredSize(new Dimension((int) menuItem_serverconsole.getPreferredSize().getWidth(), 19));
		menuItem_state.setPreferredSize(new Dimension((int) menuItem_state.getPreferredSize().getWidth(), 19));
		menuItem_serverlog.setPreferredSize(new Dimension((int) menuItem_serverlog.getPreferredSize().getWidth(), 19));
		menuItem_serverscreen.setPreferredSize(new Dimension((int) menuItem_serverscreen.getPreferredSize().getWidth(), 19));
		menuItem_serverexplorer.setPreferredSize(new Dimension((int) menuItem_serverexplorer.getPreferredSize().getWidth(), 19));
		menuItem_sqltool.setPreferredSize(new Dimension((int) menuItem_sqltool.getPreferredSize().getWidth(), 19));
		menuItem_sqllistener.setPreferredSize(new Dimension((int) menuItem_sqllistener.getPreferredSize().getWidth(), 19));
		menuItem_sqlbuding.setPreferredSize(new Dimension((int) menuItem_sqlbuding.getPreferredSize().getWidth(), 19));

		menuItem_clientenv.addActionListener(this); //
		menuItem_serverenv.addActionListener(this); //
		menuItem_serverconsole.addActionListener(this); //
		menuItem_state.addActionListener(this); //
		menuItem_serverlog.addActionListener(this); //
		menuItem_serverscreen.addActionListener(this); //
		menuItem_serverexplorer.addActionListener(this); //
		menuItem_sqltool.addActionListener(this); //
		menuItem_sqllistener.addActionListener(this); //
		menuItem_sqlbuding.addActionListener(this); //

		JMenu menuItem_server = new JMenu("服务器端相关工具"); // 服务器一级菜单.
		menuItem_server.setPreferredSize(new Dimension((int) menuItem_server.getPreferredSize().getWidth(), 19));
		menuItem_server.add(menuItem_serverenv); //
		menuItem_server.add(menuItem_serverconsole); //
		menuItem_server.add(menuItem_state); //
		menuItem_server.add(menuItem_serverlog); //
		menuItem_server.add(menuItem_serverscreen); //
		menuItem_server.add(menuItem_serverexplorer); //

		JMenu menuItem_sql = new JMenu("SQL相关工具"); // SQL相关工具
		menuItem_sql.setPreferredSize(new Dimension((int) menuItem_sql.getPreferredSize().getWidth(), 19));
		menuItem_sql.add(menuItem_sqltool); //
		menuItem_sql.add(menuItem_sqllistener); //
		menuItem_sql.add(menuItem_sqlbuding); //

		popup.add(menuItem_clientenv); //
		popup.add(menuItem_server); //
		popup.add(menuItem_sql); //

		serverPopButton = new WLTPopupButton(WLTPopupButton.TYPE_WITH_RIGHT_TOGGLE, null, UIUtil.getImage("office_091.gif"), popup); //
		serverPopButton.setBackground(LookAndFeel.defaultShadeColor1);
		serverPopButton.setPreferredSize(new Dimension(32, 19));
		serverPopButton.getButton().setToolTipText(UIUtil.getLanguage("查看服务器环境变量")); //
		serverPopButton.getButton().addActionListener(this);

		// 下面这些按钮无论是管理者还是普通用户都会有		
		btn_clientconsole = new WLTButton(UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("首页客户端控制台图片名称", "office_197.gif"))); // 客户端控制台
		btn_serverconsole = new WLTButton(UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("首页服务器端控制台图片名称", "office_162.gif"))); // office_162.gif服务器端控制台

		String str_switchStyleImgName = TBUtil.getTBUtil().getSysOptionStringValue("首页切换风格图片名称", "office_191.gif"); //
		if (new TBUtil().getSysOptionBooleanValue("首页刷新按钮是否显示文字", false)) {
			btn_desktopstyle = new WLTButton("刷新", UIUtil.getImage(str_switchStyleImgName)); //追加"刷新" 【杨科/2012-09-07】
		} else {
			btn_desktopstyle = new WLTButton(UIUtil.getImage(str_switchStyleImgName)); // 修改界面风格 
		}
		btn_updatepwd = new WLTButton("修改密码", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("首页修改密码图片名称", "office_045.gif"))); // 修改密码office_045
		btn_userinfo = new WLTButton("修改个人信息", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("首页修改个人信息图片名称", "office_021.gif"))); //修改个人信息 【杨科/2012-08-29】  袁江晓 20120909添加修改个人文字
		btn_relogin = new WLTButton("切换用户", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("首页切换用户图片名称", "office_028.gif"))); // 重新登录zt_zhuxiao.gif//sunfujun/20120820/邮储纠结
		btn_zhuxiao = new WLTButton("注销", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("首页注销铵钮图片名称", "office_136.gif"))); //注销 【杨科/2012-08-30】
		btn_help = new JButton("帮助", UIUtil.getImage("office_037.gif")); // 帮助文档zt_bangzhu.gif
		btn_exit = new WLTButton("退出", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("首页退出系统图片名称", "office_008.gif"))); // 退出系统office_008.gif

		btn_clientconsole.setRegCodeStr("首页客户端控制台图片名称"); //
		btn_serverconsole.setRegCodeStr("首页服务器端控制台图片名称"); //
		btn_desktopstyle.setRegCodeStr("首页切换风格图片名称"); //
		btn_updatepwd.setRegCodeStr("首页修改密码图片名称"); //
		btn_userinfo.setRegCodeStr("首页修改个人信息图片名称"); //
		btn_relogin.setRegCodeStr("首页切换用户图片名称"); //
		btn_zhuxiao.setRegCodeStr("首页注销铵钮图片名称"); //
		btn_exit.setRegCodeStr("首页退出系统图片名称"); //

		btn_clientconsole.setOpaque(false); //透明!
		btn_serverconsole.setOpaque(false); //透明!
		btn_desktopstyle.setOpaque(false); //透明!
		btn_updatepwd.setOpaque(false); //透明!
		btn_userinfo.setOpaque(false); //透明!
		btn_relogin.setOpaque(false); //透明!
		btn_zhuxiao.setOpaque(false); //透明!
		btn_help.setOpaque(false); //透明!
		btn_exit.setOpaque(false); //透明!

		btn_clientconsole.setBackground(LookAndFeel.defaultShadeColor1);
		btn_serverconsole.setBackground(LookAndFeel.defaultShadeColor1);
		btn_desktopstyle.setBackground(LookAndFeel.defaultShadeColor1);
		btn_updatepwd.setBackground(LookAndFeel.defaultShadeColor1);
		btn_userinfo.setBackground(LookAndFeel.defaultShadeColor1);
		btn_relogin.setBackground(LookAndFeel.defaultShadeColor1);
		btn_zhuxiao.setBackground(LookAndFeel.defaultShadeColor1);
		btn_help.setBackground(LookAndFeel.defaultShadeColor1);
		btn_exit.setBackground(LookAndFeel.defaultShadeColor1);

		btn_clientconsole.setBorder(BorderFactory.createEmptyBorder());
		btn_serverconsole.setBorder(BorderFactory.createEmptyBorder());
		btn_desktopstyle.setBorder(BorderFactory.createEmptyBorder());
		btn_updatepwd.setBorder(BorderFactory.createEmptyBorder());
		btn_userinfo.setBorder(BorderFactory.createEmptyBorder());
		btn_zhuxiao.setBorder(BorderFactory.createEmptyBorder());
		btn_relogin.setBorder(BorderFactory.createEmptyBorder());
		btn_zhuxiao.setBorder(BorderFactory.createEmptyBorder());
		btn_help.setBorder(BorderFactory.createEmptyBorder());
		btn_exit.setBorder(BorderFactory.createEmptyBorder());

		btn_clientconsole.setPreferredSize(new Dimension(18, 18));
		btn_serverconsole.setPreferredSize(new Dimension(18, 18));

		if (new TBUtil().getSysOptionBooleanValue("首页刷新按钮是否显示文字", false)) {
			btn_desktopstyle.setPreferredSize(new Dimension(45, 18)); //追加"刷新" 【杨科/2012-09-07】
		} else {
			btn_desktopstyle.setPreferredSize(new Dimension(18, 18));
		}

		btn_userinfo.setPreferredSize(new Dimension(95, 18));
		btn_updatepwd.setPreferredSize(new Dimension(70 + LookAndFeel.getFONT_REVISE_SIZE() * 4, 18));
		if (btn_relogin.getText() != null) {
			btn_relogin.setPreferredSize(new Dimension(18 + btn_relogin.getText().length() * 13 + LookAndFeel.getFONT_REVISE_SIZE() * 4, 18));
		} else {
			btn_relogin.setPreferredSize(new Dimension(18, 18));
		}
		btn_zhuxiao.setPreferredSize(new Dimension(45, 18));
		btn_help.setPreferredSize(new Dimension(48, 18));
		btn_exit.setPreferredSize(new Dimension(48, 18));

		btn_clientconsole.setToolTipText("查看客户端控制台"); //
		btn_serverconsole.setToolTipText("查看服务器端控制台"); //
		btn_desktopstyle.setToolTipText(UIUtil.getLoginLanguage("刷新/切换用户默认机构")); //
		btn_updatepwd.setToolTipText(UIUtil.getLoginLanguage("修改密码")); //
		btn_userinfo.setToolTipText(UIUtil.getLoginLanguage("修改个人信息"));
		btn_relogin.setToolTipText(UIUtil.getLoginLanguage("重新登录")); //
		btn_zhuxiao.setToolTipText(UIUtil.getLoginLanguage("注销"));
		btn_help.setToolTipText(UIUtil.getLoginLanguage("帮助文档(点击右键直接上传)")); //
		btn_exit.setToolTipText(UIUtil.getLoginLanguage("退出系统")); //

		btn_clientconsole.addActionListener(this);
		btn_serverconsole.addActionListener(this);
		btn_desktopstyle.addActionListener(this);
		btn_updatepwd.addActionListener(this);
		btn_userinfo.addActionListener(this);
		btn_relogin.addActionListener(this);
		btn_zhuxiao.addActionListener(this);
		btn_help.addActionListener(this);
		btn_exit.addActionListener(this);

		btn_zhuxiao.setCursor(new Cursor(Cursor.HAND_CURSOR)); //

		btn_help.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) { //如果点的是右键,则直接上传帮助文件!
					onUploadHelp(); //上传附件文件
				}
			}
		}); //

		//如果是管理员则还要加入系统按钮
		JPanel right_btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0)); //
		right_btnPanel.setOpaque(false); //透明!!
		boolean showRelogin = tbUtil.getSysOptionBooleanValue("是否可以注销", true);
		boolean showUserInfo = tbUtil.getSysOptionBooleanValue("是否可以修改个人信息", false);

		//首页追加新任务动态提醒 【杨科/2013-05-24】
		Boolean task = tbUtil.getSysOptionBooleanValue("首页是否显示动态提醒", false);
		if (task) {
			//搞一个一样的taskLabel 控制其显示 出鼠标移上去文字不动效果 
			taskLabel_ = new JLabel("");
			taskLabel_.setHorizontalAlignment(SwingConstants.LEFT);
			taskLabel_.setPreferredSize(new Dimension(280, 20));
			taskLabel_.setForeground(Color.blue);
			taskLabel_.setVisible(false);
			right_btnPanel.add(taskLabel_);

			taskLabel = new JLabel("");
			taskLabel.setHorizontalAlignment(SwingConstants.LEFT);
			taskLabel.setPreferredSize(new Dimension(280, 20));
			taskLabel.setForeground(Color.blue);
			right_btnPanel.add(taskLabel);

			taskLabel.addMouseListener(new MouseListener() {

				public void mouseClicked(MouseEvent e) {
				}

				public void mouseEntered(MouseEvent e) {
					taskLabel.setVisible(false);
					taskLabel_.setVisible(true);
					taskLabel_.setIcon(taskLabel.getIcon());
					taskLabel_.setText(taskLabel.getText());
				}

				public void mouseExited(MouseEvent e) {
					taskLabel.setVisible(true);
					taskLabel_.setVisible(false);
					taskLabel_.setIcon(null);
					taskLabel_.setText("");
				}

				public void mousePressed(MouseEvent e) {
				}

				public void mouseReleased(MouseEvent e) {
				}

			});
		}

		if (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) {
			right_btnPanel.add(serverPopButton); //
			right_btnPanel.add(btn_clientconsole);
			right_btnPanel.add(btn_serverconsole);
			right_btnPanel.add(btn_desktopstyle); //
			right_btnPanel.add(btn_updatepwd); // 将修改密码去掉么?

			if (showUserInfo) {
				right_btnPanel.add(btn_userinfo);
			}
			right_btnPanel.add(btn_help); //
			if (showRelogin) {
				right_btnPanel.add(btn_relogin); //
			}
			if (new TBUtil().getSysOptionBooleanValue("首页是否显示注销", false)) { //注销 【杨科/2012-08-30】
				right_btnPanel.add(btn_zhuxiao);
			}
			right_btnPanel.add(btn_exit); //
		} else { // 如果不是管理员,而是普通用户登录!!
			boolean isshowconsole = new TBUtil().getSysOptionBooleanValue("首页是否显示控制台", true);
			if (isshowconsole) {
				right_btnPanel.add(btn_clientconsole);
				right_btnPanel.add(btn_serverconsole);
			}
			right_btnPanel.add(btn_desktopstyle); //
			boolean isshowchangepwd = new TBUtil().getSysOptionBooleanValue("首页是否能修改密码", true);
			if (isshowchangepwd) {
				right_btnPanel.add(btn_updatepwd); // 将修改密码按钮去掉么?
			}
			if (showUserInfo)
				right_btnPanel.add(btn_userinfo);
			right_btnPanel.add(btn_help); //
			if (showRelogin) {
				right_btnPanel.add(btn_relogin); //
			}
			if (new TBUtil().getSysOptionBooleanValue("首页是否显示注销", false)) { //注销 【杨科/2012-08-30】
				right_btnPanel.add(btn_zhuxiao);
			}
			right_btnPanel.add(btn_exit); //
		}
		toolBarPanel.add(right_btnPanel, BorderLayout.EAST); //在右边加入系统按钮!!
		return toolBarPanel;
	}

	/**
	 * 递归创建弹出菜单!!
	 * @param _node
	 * @param _popMenu
	 * @param _menu
	 */
	private void createPopMenu(DefaultMutableTreeNode _node, JPopupMenu _popMenu, JMenu _menu) {
		for (int i = 0; i < _node.getChildCount(); i++) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) _node.getChildAt(i); //
			HashVO hvo = (HashVO) childNode.getUserObject(); //
			final String str_menuid = hvo.getStringValue("id"); //
			String str_name = hvo.getStringValue("name"); //
			String str_icon = hvo.getStringValue("icon"); //
			if (!childNode.isLeaf()) { //如果是目录结点
				JMenu itemMenu = new JMenu(str_name); //
				itemMenu.setUI(new WLTMenuUI()); //
				itemMenu.setOpaque(true); //
				itemMenu.setBackground(LookAndFeel.defaultShadeColor1);
				int str_width = tbutil.getStrWidth(str_name); //得到字宽度
				if (str_width > 90) {
					itemMenu.setPreferredSize(new Dimension(str_width + 45, 23)); //	
				} else {
					itemMenu.setPreferredSize(new Dimension(120, 23)); //
				}

				if (str_icon == null || str_icon.trim().equals("")) {
					itemMenu.setIcon(UIUtil.getImage("office_151.gif")); //也可以用这个图标!javax.swing.plaf.metal.MetalIconFactory.getTreeFolderIcon()
				} else {
					itemMenu.setIcon(UIUtil.getImage(str_icon)); //
				}
				if (_menu == null) { //如果是第一层!
					_popMenu.add(itemMenu); //
				} else {
					_menu.add(itemMenu); //
				}
				createPopMenu(childNode, _popMenu, itemMenu); //递归调用!!
			} else { //如果是叶子结点,则加入后不再递归调用了!!!
				JMenuItem itemMenu = new JMenuItem(str_name); //
				itemMenu.setUI(new WLTMenuItemUI()); //
				itemMenu.setBackground(LookAndFeel.defaultShadeColor1);
				int str_width = tbutil.getStrWidth(str_name); //得到字宽度
				if (str_width > 90) {
					itemMenu.setPreferredSize(new Dimension(str_width + 45, 23)); //	
				} else {
					itemMenu.setPreferredSize(new Dimension(120, 23)); //
				}
				if (str_icon == null || str_icon.trim().equals("")) {
					itemMenu.setIcon(UIUtil.getImage("blank.gif")); //默认认图标!
				} else {
					itemMenu.setIcon(UIUtil.getImage(str_icon)); //
				}
				itemMenu.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						openAppMainFrameWindowById(str_menuid); //
					}
				}); //
				if (_menu == null) { //如果是第一层!
					_popMenu.add(itemMenu); //
				} else {
					_menu.add(itemMenu); //
				}
			}
		}
	}

	/**
	 * 获得下面状态栏
	 * 
	 * @return
	 */
	private JPanel getStatuPanel() {
		JPanel statuPanel = new JPanel();
		statuPanel.setOpaque(false); //透明
		statuPanel.setLayout(new BorderLayout()); //

		//		String str_user = UIUtil.getLoginLanguage("登录用户:") + ClientEnvironment.getInstance().getLoginUserName();
		String str_user = ClientEnvironment.getInstance().getLoginUserName();
		if (ClientEnvironment.getInstance().isAdmin()) {
			str_user = str_user + " ◆ "; //
			userNameLabel = new JLabel(str_user, UIUtil.getImage("zt_061.gif"), SwingConstants.LEFT);
			userNameLabel.setForeground(Color.BLUE); //
		} else {
			str_user = str_user + " "; //
			userNameLabel = new JLabel(str_user, UIUtil.getImage("zt_061.gif"), SwingConstants.LEFT);
		}
		userNameLabel.putClientProperty("POPMENUTYPE", ScrollablePopupFactory.POP_TYPE_VERTICAL);
		JLabel logoutlabel = new WLTHrefLabel("注销");
		logoutlabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				onRelogin();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				((JLabel) e.getSource()).setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				((JLabel) e.getSource()).setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		userNameLabel.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				JComponent btn = (JComponent) e.getSource(); //
				if (e.getButton() != e.BUTTON1 || !(e.getX() > 0 && e.getX() < btn.getWidth() && e.getY() > 0 && e.getY() < btn.getHeight())) {
					return;
				}
				popToolPanel();
			}

			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					//					if (e.getClickCount() == 2) {
					//						showCurrSessionVO(); //
					//					} else if(e.getClickCount()==1 ){
					//						tbUtil.refectCallClassStaticMethod("cn.com.infostrategy.ui.sysapp.login.click2.ModifyDesktopStyleDialog", "openMe", new Object[] { DeskTopPanel.getDeskTopPanel(), LoginAppletLoader.getAppletLoader(), getDesktopLayout(), getIndexLayout(), getDeskTopAndIndexStyleMartix() });
					//					}
				} else {
					workTabbedPanel.setBtnPanelVisible(true); //万一隐藏了,则在这里显示出来!										
				}
			}
		});
		String str_currversion = System.getProperty("LAST_STARTTIME");
		StringBuffer sb_currversion = new StringBuffer(); //
		if (str_currversion != null) {
			sb_currversion.append(str_currversion.substring(0, 4) + "-");
			sb_currversion.append(str_currversion.substring(4, 6) + "-");
			sb_currversion.append(str_currversion.substring(6, 8));
			//			sb_currversion.append(str_currversion.substring(8, 10) + ":");
			//			sb_currversion.append(str_currversion.substring(10, 12) + ":");
			//			sb_currversion.append(str_currversion.substring(12, 14) + "");
		}

		//系统版本号, 以weblight_项目名称.jar文件日期为准!		
		String verInfo = getJarVersion("weblight_" + System.getProperty("PROJECT_SHORTNAME") + ".jar");
		if (verInfo == null) { //开发时还用服务器启动时间
			verInfo = sb_currversion.toString();
		}
		String SUPERVISE = System.getProperty("SUPERVISE");
		StringBuffer infoSB = new StringBuffer();
		if (SUPERVISE != null && !SUPERVISE.equals("")) {
			infoSB.append(SUPERVISE);
		}
		infoSB.append(" 版权所有:");
		infoSB.append(System.getProperty("LICENSEDTO"));
		if (!"N".equals(System.getProperty("SHOWSYSVER"))) {
			infoSB.append(" 系统版本[ " + verInfo + " ]");
		}
		informationLabel = new JTextField(infoSB.toString(), 1024);

		informationLabel.setEditable(false);
		informationLabel.setOpaque(false); //透明!!!
		informationLabel.setBackground(LookAndFeel.systembgcolor);
		informationLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.isShiftDown() && e.getClickCount() == 2) { //如果是按住Shift双击的!
					int li_result = JOptionPane.showConfirmDialog(DeskTopPanel.this, "是否输出请求参数的当前状态是:【" + ClientEnvironment.isOutPutCallObjToFile + "】!\r\n点击【是】将启用之,点击【否】将禁用之,点击【取消】放弃操作!\r\n启用后将在目录[C:\\156]下生成文件!", "提示", JOptionPane.YES_NO_CANCEL_OPTION); ////
					if (li_result == JOptionPane.YES_OPTION) {
						ClientEnvironment.isOutPutCallObjToFile = true;
						JOptionPane.showMessageDialog(DeskTopPanel.this, "启用输出请求参数成功!!\r\n从现在起,每次远程请求的参数都将存储在[C:\\156]目录下!"); //
					} else if (li_result == JOptionPane.NO_OPTION) {
						ClientEnvironment.isOutPutCallObjToFile = false;
						JOptionPane.showMessageDialog(DeskTopPanel.this, "禁用输出请求参数成功!!\r\n从现在起,请求参数不再进行存储了!"); //
					}
				}
			}
		}); //

		long ll_free = Runtime.getRuntime().freeMemory() / 1024; //
		long ll_total = Runtime.getRuntime().totalMemory() / 1024; //
		long ll_max = Runtime.getRuntime().maxMemory() / 1024; //
		long ll_busy = ll_total - ll_free; //

		String str_memoerydesc = (ll_busy / 1024) + "M/" + (ll_total / 1024) + "M/" + (ll_max / 1024) + "M";
		jvmInfoLabel = new JLabel(str_memoerydesc); // "Licensed to: " +
		jvmInfoLabel.setToolTipText("<html>" + sb_currversion.toString() + "<br>右键查看系统属性<br>查看控制台[Ctrl+双击]<br>GC[Shift+双击]</html>"); //
		jvmInfoLabel.setFont(new Font("System", Font.PLAIN, 12));
		jvmInfoLabel.setOpaque(false); //透明
		jvmInfoLabel.setBackground(LookAndFeel.systembgcolor);

		jvmInfoLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) { //如果是右键,则显示所有系统属性,因为现在有许多客户要求去掉系统菜单(即不能查看系统属性,源码什么的了),这样非Admin模式登录的情况下就没法查看系统属性(因为右上角的查看客户端环境变量按钮也没了),所以在这搞个功能,可以随时查看系统属性!
					Properties sysProps = System.getProperties(); //
					String[] str_keys = (String[]) sysProps.keySet().toArray(new String[0]); //
					Arrays.sort(str_keys); //排序一下!
					StringBuilder sb_text = new StringBuilder("System.getProperties()所有属性:\r\n"); //
					for (int i = 0; i < str_keys.length; i++) {
						sb_text.append("[" + str_keys[i] + "]=[" + sysProps.getProperty(str_keys[i]) + "]\r\n"); //
					}
					MessageBox.showTextArea(DeskTopPanel.this, sb_text.toString()); //
				} else if (e.getClickCount() == 2) {
					if (e.isControlDown()) {
						invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.ClientConsoleMsgFrame", deskTopPanel, null, null); //显示客户端控制台,因为在单点登录时,上面没有按钮栏与菜单,没地方看控制台了!
					} else { //如果是按住Shift键的
						if (e.isShiftDown()) {
							System.gc(); //
						}
						long ll_free = Runtime.getRuntime().freeMemory() / 1024; //
						long ll_total = Runtime.getRuntime().totalMemory() / 1024; //
						long ll_max = Runtime.getRuntime().maxMemory() / 1024; //
						long ll_busy = ll_total - ll_free; //
						String str_m1 = (ll_busy / 1024) + "M"; //
						String str_m2 = (ll_total / 1024) + "M"; //
						String str_m3 = (ll_max / 1024) + "M"; //
						jvmInfoLabel.setText(str_m1 + "/" + str_m2 + "/" + str_m3); //
						JOptionPane.showMessageDialog(DeskTopPanel.this, "Busy Memory:" + str_m1 + "\r\nTotalMemory:" + str_m2 + "\r\nMaxMemory:" + str_m3); //
					}
				}

			}
		});
		JPanel infoPanel = new JPanel(new BorderLayout());
		infoPanel.setOpaque(false);
		informationLabel.setBorder(BorderFactory.createLoweredBevelBorder()); //
		jvmInfoLabel.setBorder(BorderFactory.createLoweredBevelBorder()); //
		//		statuPanel.add(userNameLabel, BorderLayout.WEST);
		infoPanel.add(informationLabel, BorderLayout.CENTER);
		selectedInfo = new JLabel();//单元格选中信息[YangQing/2013-09-25]
		selectedInfo.setBorder(BorderFactory.createLoweredBevelBorder());
		infoPanel.add(selectedInfo, BorderLayout.EAST);

		statuPanel.add(infoPanel, BorderLayout.CENTER);

		JPanel userinfo = new JPanel(new BorderLayout());
		userinfo.setBorder(BorderFactory.createLoweredBevelBorder()); //
		userinfo.setOpaque(false);
		userinfo.add(userNameLabel, BorderLayout.CENTER);
		userinfo.add(logoutlabel, BorderLayout.EAST);

		statuPanel.add(userinfo, BorderLayout.EAST);

		return statuPanel;
	}

	JPopupMenu pop;
	private JMenuItem tool_menuitem_pwd, tool_menuitem_otheruser, tool_menuitem_help, tool_menuitem_control_client, tool_menuitem_control_server, tool_menuitem_showsession, tool_font_revise;
	private JMenu tool_menuitem_control;

	//点击状态栏个人信息时弹出的工具条
	private void popToolPanel() {
		if (pop == null) {
			tool_menuitem_control = new JMenu("系统日志");
			tool_menuitem_control.setIcon(UIUtil.getImage("office_194.gif"));
			tool_menuitem_control_client = new JMenuItem("客户端日志", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("首页客户端控制台图片名称", "office_197.gif")));
			tool_menuitem_control_server = new JMenuItem("服务器端日志", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("首页服务器端控制台图片名称", "office_162.gif")));
			tool_menuitem_pwd = new JMenuItem("修改密码", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("首页修改密码图片名称", "office_045.gif"))); // 修改密码office_045
			tool_menuitem_otheruser = new JMenuItem("切换身份", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("首页切换用户图片名称", "office_028.gif"))); // 重新登录zt_zhuxiao.gif//sunfujun/20120820/邮储纠结
			tool_menuitem_help = new JMenuItem("帮助", UIUtil.getImage("office_037.gif")); // 帮助文档zt_bangzhu.gif
			tool_menuitem_showsession = new JMenuItem("个人信息", UIUtil.getImage("office_021.gif"));
			if (LookAndFeel.getFONT_REVISE_SIZE() == 0) {
				tool_font_revise = new JMenuItem("字体放大", UIUtil.getImage("zoomin.gif")); // 字体放大、缩小
			} else {
				tool_font_revise = new JMenuItem("字体恢复", UIUtil.getImage("zoomout.gif")); // 字体放大、缩小
			}
			tool_menuitem_control_client.addActionListener(this);
			tool_menuitem_control_server.addActionListener(this);
			tool_menuitem_pwd.addActionListener(this);
			tool_menuitem_otheruser.addActionListener(this);
			tool_menuitem_help.addActionListener(this);
			tool_menuitem_showsession.addActionListener(this);
			tool_font_revise.addActionListener(this);
			pop = new JPopupMenu();
			pop.add(tool_menuitem_control);
			tool_menuitem_control.add(tool_menuitem_control_client);
			tool_menuitem_control.add(tool_menuitem_control_server);
			pop.add(tool_menuitem_pwd);
			pop.add(tool_menuitem_otheruser);
			pop.add(tool_font_revise);
			pop.add(tool_menuitem_help);
			pop.add(tool_menuitem_showsession);
		}

		pop.show(userNameLabel, userNameLabel.getX(), userNameLabel.getY() - (int) pop.getPreferredSize().getHeight());
	}

	//双击菜单结点!!
	private void onClickTree(JTree _tree) {
		if (_tree.getSelectionPath() == null) {
			return;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) _tree.getSelectionPath().getLastPathComponent();
		if (node.isLeaf()) { //只有叶子结点才做这事!
			HashVO hvo = getMenuVOFromNode(node); //
			openAppMainFrameWindow(hvo); //执行找开功能点逻辑!!!
		}
	}

	//点击系统注册功能
	private void onClickSysRegMenuTree() {
		try {
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			if (sysRegMenuTree.getSelectionPath() == null) {
				return;
			}
			DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) sysRegMenuTree.getSelectionPath().getLastPathComponent();
			if (selNode.isLeaf()) { //只有叶子结点才做这事!
				HashVO hvo = (HashVO) selNode.getUserObject(); //
				this.realCallMenuName = "当前位置：" + hvo.getStringValue("$TreePath");
				String str_menuname = hvo.getStringValue("menuname"); //
				String str_command = hvo.getStringValue("command"); //
				String str_menucommandtype = hvo.getStringValue("commandtype");
				if (str_menucommandtype == null) {
					str_menucommandtype = "";
					hvo.setAttributeValue("commandtype", "00");
				}
				hvo.setAttributeValue("id", str_menuname); //
				hvo.setAttributeValue("usecmdtype", 1);
				hvo.setAttributeValue("name", str_menuname);
				if (str_command == null || str_command.trim().equals("")) {
					MessageBox.show(this, "选中的功能点的路径为空!"); //
					return; //
				}
				if (map_openedWorkPanel.containsKey(str_menuname)) { // 如果已打开过.
					Object obj = map_openedWorkPanel.get(str_menuname); //
					WorkTabbedPanel tabitempanel = (WorkTabbedPanel) obj;
					this.workTabbedPanel.setSelectedComponent(tabitempanel); //
					this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
					return; //
				}
				AbstractWorkPanel workPanel = new LoginUtil().getWorkPanelByMenuVO(hvo); //根据菜单VO取得
				//				AbstractWorkPanel workPanel = (AbstractWorkPanel) Class.forName(str_command).newInstance(); //
				workPanel.setSelectedMenuVOs(hvo); //
				workPanel.setDeskTopPanel(this); //
				workPanel.setLayout(new BorderLayout()); //
				workPanel.initialize(); //初始化界面

				//				WorkTabbedPanel tabbePanel = new WorkTabbedPanel(str_menuname, str_menuname, "当前位置：" + str_menuname, UIUtil.getImage("office_060.gif"), workPanel, false, 800);
				WorkTabbedPanel tabbePanel = new WorkTabbedPanel(str_menuname, str_menuname, "", UIUtil.getImage("office_060.gif"), workPanel, false, 800, false);

				tabbePanel.setDeskTopPanel(this); // //
				tabbePanel.setBackground(new Color(240, 240, 240)); //
				String str_menurealName = str_menuname; //
				if (str_menurealName.lastIndexOf(".") > 0) {
					str_menurealName = str_menurealName.substring(str_menurealName.lastIndexOf(".") + 1, str_menurealName.length()); //
				}
				this.workTabbedPanel.addTab(str_menurealName, UIUtil.getImage("office_042.gif"), tabbePanel); // 加入工作面板!!
				this.workTabbedPanel.setSelectedComponent(tabbePanel); //
				tabbePanel.setMatherTabbedPane(workTabbedPanel); //
				map_openedWorkPanel.put(str_menuname, tabbePanel); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
			this.realCallMenuName = null;
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			this.realCallMenuName = null;
		}
	}

	/**
	 * 直接根据菜单id打开其功能点!!!
	 * @param _menuId
	 */
	public void openAppMainFrameWindowById(String _menuId) {
		openAppMainFrameWindowById(_menuId, null);
	}

	public void openAppMainFrameWindowById(String _menuID, HashMap _par) {
		DefaultMutableTreeNode node = findNodeByID(_menuID); //
		if (node == null) {
			MessageBox.show(this, "可能因为权限不足,没有找到对应的功能点!"); //
			return; //
		}
		HashVO hvo = getMenuVOFromNode(node); //
		openAppMainFrameWindow(hvo, _par); //执行找开功能点逻辑!!!
	}

	public void openAppMainFrameWindowByIdAsSplash(final String _menuId) {
		Frame rootFrame = JOptionPane.getFrameForComponent(this); //
		if (rootFrame != null) {
			rootFrame.setState(JFrame.NORMAL); //
			rootFrame.setAlwaysOnTop(true); //
			rootFrame.toFront(); //
			rootFrame.setAlwaysOnTop(false); //
		}
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				openAppMainFrameWindowById(_menuId); //
			}
		}); //
	}

	public WorkTabbedPanel getOpenAppMainPanel(String _menuID, HashMap _par) {
		DefaultMutableTreeNode node = findNodeByID(_menuID); //
		if (node == null) {
			MessageBox.show(this, "可能因为权限不足,没有找到对应的功能点!"); //
			return null; //
		}
		HashVO _hvo = getMenuVOFromNode(node); //
		Runtime.getRuntime().gc(); //  
		this.realCallMenuName = "当前位置：" + _hvo.getStringValue("$TreePath");
		//已打开的窗口不能大于10个--邮储 【杨科/2012-08-29】
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		long ll_1 = System.currentTimeMillis();
		if (ClientEnvironment.getInstance().isAdmin()) { // 如是是管理员,则总是重新取数据库
			try {
				HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select * from pub_menu where id='" + _hvo.getStringValue("id") + "'"); //
				hvs[0].setAttributeValue("$TreePath", _hvo.getStringValue("$TreePath")); // //
				_hvo = hvs[0]; //
			} catch (Exception exx) {
				exx.printStackTrace(); // /
			}
		}
		//		if (newDeskTopTabPane.getShowState()) {
		//			newDeskTopTabPane.setSelectedIndex(2);
		//		} else {

		String str_menuname = _hvo.getStringValue("name"); // 
		String str_treepath = _hvo.getStringValue("$TreePath"); //$TreePath
		String str_menuicon = _hvo.getStringValue("icon"); //
		String str_commandtype = null; //
		String str_command = null; //
		try {
			AbstractWorkPanel workItemPanel = new LoginUtil().getWorkPanelByMenuVO(_hvo); //根据菜单VO取得
			workItemPanel.addMenuConfMap(_par);
			workItemPanel.setDeskTopPanel(this); //设置一下!
			workItemPanel.initialize(); // 初始化页面!!!!!!!!
			str_commandtype = _hvo.getStringValue("$commandtype"); //命令类型!!
			str_command = _hvo.getStringValue("$command"); //命令!!
			String str_title = workItemPanel.getMenuName(); //
			if (str_title == null) {
				str_title = _hvo.getStringValue("name");
			}
			Icon icon = null;
			if (str_menuicon != null) {
				icon = UIUtil.getImage(str_menuicon);
			} else {
				icon = UIUtil.getImage("blank.gif");
			}
			String str_menuopentype = _hvo.getStringValue("opentype"); //
			boolean bo_isextend = _hvo.getBooleanValue("isextend", false); // 是否延展
			Integer li_exendheight = _hvo.getIntegerValue("extendheight", 1000); //_vos[_vos.length - 1].getExtendheight(); // 延展高度
			String opentypeweight = _hvo.getStringValue("opentypeweight"); // FRAME宽度
			String opentypeheight = _hvo.getStringValue("opentypeheight"); // FRAME高度
			WorkTabbedPanel tabbePanel = new WorkTabbedPanel(_menuID, str_menuname, "", icon, workItemPanel, bo_isextend, li_exendheight);
			tabbePanel.setDeskTopPanel(this); // //
			tabbePanel.setBackground(new Color(240, 240, 240)); //
			tabbePanel.setMatherTabbedPane(workTabbedPanel); //
			if (ClientEnvironment.isAdmin() && "11".equals(str_commandtype)) { //在系统的状态条中显示“XML注册功能点”对应的实际路径！在双击打开时先显示一下！然后放入tabpane的缓存中，以后就是根据单击选择事件来显示！
				String str_usecmdtype = "1"; //
				String str_cmdtype = _hvo.getStringValue("commandtype");
				String str_cmd = _hvo.getStringValue("command");
				if (_hvo.getStringValue("usecmdtype") != null) {
					if (_hvo.getStringValue("usecmdtype").equals("2")) {
						str_usecmdtype = "2";
						str_cmdtype = _hvo.getStringValue("commandtype2");
						str_cmd = _hvo.getStringValue("command2");
					} else if (_hvo.getStringValue("usecmdtype").equals("3")) {
						str_usecmdtype = "3";
						str_cmdtype = _hvo.getStringValue("commandtype3");
						str_cmd = _hvo.getStringValue("command3");
					}
				}
				String str_text = "<" + _hvo.getStringValue("code") + "><" + str_usecmdtype + "><" + str_cmdtype + "> <" + str_cmd + "><" + workItemPanel.getClientProperty("$realpath") + ">";
				setInfomation(str_text);
			}
			// 往数据为里塞一条日志记录!
			long ll_2 = System.currentTimeMillis(); //
			ClientEnvironment clientEnv = ClientEnvironment.getInstance();
			try {
				getSysAppService().addClickedMenuLog(clientEnv.getLoginUserCode(), clientEnv.getLoginUserName(), clientEnv.getCurrLoginUserVO().getBlDeptId(), ClientEnvironment.getCurrLoginUserVO().getBlDeptName(), str_menuname, tbUtil.replaceAll(str_treepath, " ", ""), (ll_2 - ll_1) + "毫秒"); // 增加点击菜单日志..
			} catch (Exception exx) {
				exx.printStackTrace(); //
			}
			getLogger().debug(UIUtil.getLoginLanguage("加载页面") + "[" + str_commandtype + "][" + str_command + "]" + UIUtil.getLoginLanguage("成功") + "," + UIUtil.getLoginLanguage("耗时") + "[" + (ll_2 - ll_1) + "]!!");
			return tabbePanel;
		} catch (Exception ex) {
			ex.printStackTrace();
			this.realCallMenuName = null;
			MessageBox.showException(this, new WLTAppException(UIUtil.getLoginLanguage("加载页面") + "[" + str_commandtype + "][" + str_command + "]" + UIUtil.getLoginLanguage("失败") + "!\r\n" + UIUtil.getLoginLanguage("原因") + ":【" + ex.getClass().getName() + "[" + ex.getMessage() + "]】")); //
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
			this.realCallMenuName = null;
		}
		return null;
	}

	/**
	 * 得到一个顶级菜单并且可以有办公区域的面板。
	 * @return
	 */
	public JPanel getIBarMenuAndWorkPanel(String _menuID, final BillBomPanel _bomPanel) {
		return new I_BarMenuAndWorkPanel(_menuID, _bomPanel, allTreeRootNode);
	}

	/**
	 * 打开应用主窗口,真正点击菜单后打开应用窗口的核心逻辑!
	 * @param _vos, 点击的菜单的从根结点到该结点的路径!!
	 */
	private void openAppMainFrameWindow(HashVO _hvo) {
		openAppMainFrameWindow(_hvo, null);
	}

	//通过搜索功能打开功能点
	private void openMenuBySearchID(String _menuID) { //打开功能点
		int li_menutype = cn.com.infostrategy.ui.sysapp.login.DeskTopPanel.deskTopPanel.getOneMenuType(_menuID); //
		if (li_menutype < 0) {
			MessageBox.show(this, "您没有权限访问对应的功能点!"); //
			return; //
		}
		if (li_menutype == 1) { //叶子结点,在新页签中打开，不需要加入堆栈!!
			final JPanel jp = (JPanel) newDeskTopTabPane.getComponentAt(newDeskTopTabPane.getSelectedIndex());
			if (jp.getComponent(0) instanceof BillBomPanel) {
				final BillBomPanel bomPanel = (BillBomPanel) jp.getComponent(0);
				HashMap map = new HashMap();
				WorkTabbedPanel panel = cn.com.infostrategy.ui.sysapp.login.DeskTopPanel.deskTopPanel.getOpenAppMainPanel(_menuID, map);
				JComponent closeBtn = panel.getBtn_closeMe();
				closeBtn.addMouseListener(new MouseAdapter() {
					public void mouseReleased(MouseEvent e) {
						JComponent btn = (JComponent) e.getSource();
						if (!(e.getX() > 0 && e.getX() < btn.getWidth() && e.getY() > 0 && e.getY() < btn.getHeight())) {
							return;
						}
						bomPanel.onBack();
						DeskTopPanel.getDeskTopPanel().expandTopBtnPane(true);
						System.gc();
					}
				});
				bomPanel.addToCardLayout("menu" + _menuID, panel);
			} else if (jp.getComponent(0) instanceof JComponent) { //如果里面放的是一个容器
				if (!(jp.getLayout() instanceof CardLayout)) { //如果不是cardLayout强行改了。不知道可以不。
					JComponent oldWorkPanel = (JComponent) jp.getComponent(0);
					jp.removeAll();
					CardLayout cardLayoutForQSearch = new CardLayout();
					jp.setLayout(cardLayoutForQSearch);
					jp.add("old", oldWorkPanel);//加入原有面板
					cardLayoutForQSearch.show(jp, "old");
					Stack stack = new Stack(); // 自定义一个堆栈
					stack.push("old");
					jp.putClientProperty("stack", stack);
				}
				CardLayout cardL = (CardLayout) jp.getLayout();
				Stack stack = (Stack) jp.getClientProperty("stack");//取出堆栈
				if (stack == null) {
					stack = new Stack(); // 自定义一个堆栈
					stack.push("old");
				} else {
					if (stack.contains(_menuID)) {
						cardL.show(jp, _menuID);
						return;
					}
				}

				WorkTabbedPanel panel = cn.com.infostrategy.ui.sysapp.login.DeskTopPanel.deskTopPanel.getOpenAppMainPanel(_menuID, new HashMap());
				JComponent closeBtn = panel.getBtn_closeMe();
				closeBtn.addMouseListener(new MouseAdapter() {
					public void mouseReleased(MouseEvent e) {
						JComponent btn = (JComponent) e.getSource();
						if (!(e.getX() > 0 && e.getX() < btn.getWidth() && e.getY() > 0 && e.getY() < btn.getHeight())) {
							return;
						}
						CardLayout cardL = (CardLayout) jp.getLayout();
						Stack stack = (Stack) jp.getClientProperty("stack");
						stack.pop();
						String menuID = (String) stack.peek(); //
						cardL.show(jp, menuID);
						jp.putClientProperty("stack", stack);
						DeskTopPanel.getDeskTopPanel().expandTopBtnPane(true);
						System.gc();
					}
				});
				jp.add(_menuID, panel);
				stack.push(_menuID);
				cardL.show(jp, _menuID);
			}
		}
	}

	private void openAppMainFrameWindow(final HashVO _hvo, final HashMap _par) {
		openMenuThread.schedule(new TimerTask() {
			public void run() {
				openAppMainFrameWindow(_hvo, _par, workTabbedPanel);
				deskTopPanel.repaint();
			}

		}, 0);
	}

	private void openAppMainFrameWindow(HashVO _hvo, HashMap _par, WLTTabbedPane workTabbedPanel) {
		if (i_barMenuPanel != null) {
			i_barMenuPanel.openAppMainFrameWindowById(_hvo.getStringValue("id"), _par);
			return;
		}
		Runtime.getRuntime().gc(); //  
		this.realCallMenuName = "当前位置：" + _hvo.getStringValue("$TreePath");
		//已打开的窗口不能大于10个--邮储 【杨科/2012-08-29】
		int tabMaxCount = new TBUtil().getSysOptionIntegerValue("首页页签最多打开个数", 10); //首页页签打开个数可配置 【杨科/2012-10-30】
		if (workTabbedPanel.getTabCount() > tabMaxCount) {
			MessageBox.show(this, "为了性能考虑,页签打开最多为" + tabMaxCount + "个!");
			return;
		}

		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		long ll_1 = System.currentTimeMillis();
		if (ClientEnvironment.getInstance().isAdmin()) { // 如是是管理员,则总是重新取数据库
			try {
				HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select * from pub_menu where id='" + _hvo.getStringValue("id") + "'"); //
				hvs[0].setAttributeValue("$TreePath", _hvo.getStringValue("$TreePath")); // //
				_hvo = hvs[0]; //
			} catch (Exception exx) {
				exx.printStackTrace(); // /
			}
		}
		//		if (newDeskTopTabPane.getShowState()) {
		//			newDeskTopTabPane.setSelectedIndex(2);
		//		} else {
		//		newDeskTopTabPane.setSelectedIndex(2, false);
		//		}
		String str_menuid = _hvo.getStringValue("id"); //
		if (map_openedWorkPanel.containsKey(str_menuid)) { // 如果已打开过.
			if (_par == null) {
				Object obj = map_openedWorkPanel.get(str_menuid); //
				if (obj instanceof WLTFrame) {
					WLTFrame frame = (WLTFrame) map_openedWorkPanel.get(str_menuid); //
					frame.setVisible(true); //
					frame.toFront(); //
					this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
					return;
				} else {
					WorkTabbedPanel tabitempanel = (WorkTabbedPanel) obj; //
					this.workTabbedPanel.setSelectedComponent(tabitempanel); //
					this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
					return; //
				}
			} else {
				Object obj = map_openedWorkPanel.get(str_menuid); //
				WorkTabbedPanel tabitempanel = (WorkTabbedPanel) obj; //
				map_openedWorkPanel.remove(str_menuid);
				workTabbedPanel.remove(workTabbedPanel.indexOfComponent(tabitempanel)); //
				System.gc(); //
				this.updateUI();
			}
		}

		String str_menuname = _hvo.getStringValue("name"); // 
		String str_treepath = _hvo.getStringValue("$TreePath"); //$TreePath
		String str_menuicon = _hvo.getStringValue("icon"); //
		String str_commandtype = null; //
		String str_command = null; //
		try {
			AbstractWorkPanel workItemPanel = new LoginUtil().getWorkPanelByMenuVO(_hvo); //根据菜单VO取得
			if (workItemPanel == null) { //可能是Frame直接打开了!所以返回null!!
				return;
			}
			workItemPanel.addMenuConfMap(_par);
			workItemPanel.setDeskTopPanel(this); //设置一下!
			workItemPanel.initialize(); // 初始化页面!!!!!!!!
			str_commandtype = _hvo.getStringValue("$commandtype"); //命令类型!!
			str_command = _hvo.getStringValue("$command"); //命令!!
			String str_title = workItemPanel.getMenuName(); //
			if (str_title == null) {
				str_title = _hvo.getStringValue("name");
			}
			Icon icon = null;
			if (str_menuicon != null) {
				icon = UIUtil.getImage(str_menuicon);
			} else {
				icon = UIUtil.getImage("blank.gif");
			}
			String str_menuopentype = _hvo.getStringValue("opentype"); //
			boolean bo_isextend = _hvo.getBooleanValue("isextend", false); // 是否延展
			Integer li_exendheight = _hvo.getIntegerValue("extendheight", 1000); //_vos[_vos.length - 1].getExtendheight(); // 延展高度
			String opentypeweight = _hvo.getStringValue("opentypeweight"); // FRAME宽度
			String opentypeheight = _hvo.getStringValue("opentypeheight"); // FRAME高度
			if (str_menuopentype == null || str_menuopentype.equalsIgnoreCase("TAB")) {
				WorkTabbedPanel tabbePanel = new WorkTabbedPanel(str_menuid, str_menuname, "", icon, workItemPanel, bo_isextend, li_exendheight, false);
				//				WorkTabbedPanel tabbePanel = new WorkTabbedPanel(str_menuid, str_menuname, "当前位置：" + str_treepath, icon, workItemPanel, bo_isextend, li_exendheight);
				tabbePanel.setDeskTopPanel(this); // //
				tabbePanel.setBackground(new Color(100, 100, 240)); //
				this.workTabbedPanel.addTab(str_title, icon, tabbePanel); // 加入工作面板!!
				this.workTabbedPanel.setSelectedComponent(tabbePanel); //
				tabbePanel.setMatherTabbedPane(workTabbedPanel); //
				map_openedWorkPanel.put(str_menuid, tabbePanel); //
			} else {
				WLTFrame frame = new WLTFrame(this, str_menuid, str_menuname, "当前位置：" + str_treepath, workItemPanel, bo_isextend, li_exendheight, opentypeweight, opentypeheight); //
				frame.setVisible(true); //
				frame.toFront(); //
				map_openedWorkPanel.put(str_menuid, frame); // 加入缓存
			}
			if (ClientEnvironment.isAdmin() && "11".equals(str_commandtype)) { //在系统的状态条中显示“XML注册功能点”对应的实际路径！在双击打开时先显示一下！然后放入tabpane的缓存中，以后就是根据单击选择事件来显示！
				String str_usecmdtype = "1"; //
				String str_cmdtype = _hvo.getStringValue("commandtype");
				String str_cmd = _hvo.getStringValue("command");
				if (_hvo.getStringValue("usecmdtype") != null) {
					if (_hvo.getStringValue("usecmdtype").equals("2")) {
						str_usecmdtype = "2";
						str_cmdtype = _hvo.getStringValue("commandtype2");
						str_cmd = _hvo.getStringValue("command2");
					} else if (_hvo.getStringValue("usecmdtype").equals("3")) {
						str_usecmdtype = "3";
						str_cmdtype = _hvo.getStringValue("commandtype3");
						str_cmd = _hvo.getStringValue("command3");
					}
				}
				String str_text = "<" + _hvo.getStringValue("code") + "><" + str_usecmdtype + "><" + str_cmdtype + "> <" + str_cmd + "><" + workItemPanel.getClientProperty("$realpath") + ">";
				setInfomation(str_text);
			}
			// 往数据为里塞一条日志记录!
			long ll_2 = System.currentTimeMillis(); //
			ClientEnvironment clientEnv = ClientEnvironment.getInstance();
			try {
				getSysAppService().addClickedMenuLog(clientEnv.getLoginUserCode(), clientEnv.getLoginUserName(), clientEnv.getCurrLoginUserVO().getBlDeptId(), ClientEnvironment.getCurrLoginUserVO().getBlDeptName(), str_menuname, tbUtil.replaceAll(str_treepath, " ", ""), (ll_2 - ll_1) + "毫秒"); // 增加点击菜单日志..
			} catch (Exception exx) {
				exx.printStackTrace(); //
			}
			getLogger().debug(UIUtil.getLoginLanguage("加载页面") + "[" + str_commandtype + "][" + str_command + "]" + UIUtil.getLoginLanguage("成功") + "," + UIUtil.getLoginLanguage("耗时") + "[" + (ll_2 - ll_1) + "]!!");
		} catch (Exception ex) {
			ex.printStackTrace();
			this.realCallMenuName = null;
			MessageBox.showException(this, new WLTAppException(UIUtil.getLoginLanguage("加载页面") + "[" + str_commandtype + "][" + str_command + "]" + UIUtil.getLoginLanguage("失败") + "!\r\n" + UIUtil.getLoginLanguage("原因") + ":【" + ex.getClass().getName() + "[" + ex.getMessage() + "]】")); //
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
			this.realCallMenuName = null;
		}
	}

	//执行自启动
	private boolean execAutoStartNode() {
		try {
			DefaultMutableTreeNode[] allnodes = (DefaultMutableTreeNode[]) al_autostartNodes.toArray(new DefaultMutableTreeNode[0]); //
			if (allnodes.length > 0) {
				for (int i = 0; i < allnodes.length; i++) {
					HashVO hvo = getMenuVOFromNode(allnodes[i]); //
					if (hvo != null) {
						openAppMainFrameWindow(hvo); //
					}
				}
				return true; //
			}
			return false; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return false; //
		}
	}

	//点击首页三个Radio按钮
	private void onClickIndexRadioBtn(String _itemKey) {
		try {
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			changeRadioPanel(deskIndexRadioPanel, _itemKey); //
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
	}

	private void reloadWorkFlowTask() {
		try {
			if (taskAndMsgCenterPanel != null) {
				Method mth = Class.forName("cn.com.infostrategy.ui.sysapp.login.taskcenter.TaskAndMsgCenterPanel").getMethod("onRefresh", null); //
				mth.invoke(taskAndMsgCenterPanel); //反射执行其刷新方法!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 首页四个页签分别加载的面板!!!
	 * @param _radioPane
	 * @param str_seledkey
	 * @throws Exception
	 */
	private void changeRadioPanel(WLTRadioPane _radioPane, String str_seledkey) throws Exception {
		if ("公告消息".equals(str_seledkey)) {
			if (indexPanel == null) {
				indexPanel = new IndexPanel(this); //公告消息,即传统的首页,所以叫IndexPanel面板!!!
				JPanel tabPanel = (JPanel) _radioPane.getTabCompent("公告消息"); //
				tabPanel.removeAll(); //
				//				tabPanel.setLayout(new BorderLayout()); //
				tabPanel.add(indexPanel); //
				tabPanel.updateUI(); //
				_radioPane.showOneTab(str_seledkey); //
			}
		} else if ("任务中心".equals(str_seledkey)) { //任务中心
			if (taskAndMsgCenterPanel == null) {
				taskAndMsgCenterPanel = (JPanel) Class.forName("cn.com.infostrategy.ui.sysapp.login.taskcenter.TaskAndMsgCenterPanel").newInstance(); //
				JPanel tabPanel = (JPanel) _radioPane.getTabCompent("任务中心"); //
				tabPanel.removeAll(); //
				tabPanel.setLayout(new BorderLayout()); //
				tabPanel.add(taskAndMsgCenterPanel); //
				tabPanel.updateUI(); //
				_radioPane.showOneTab(str_seledkey); //
			} else { //如果已有有了!则也要动态刷新工作流!!!
				//				reloadWorkFlowTask(); //
			}
		} else if ("快捷访问".equals(str_seledkey)) { //快捷访问!!
			if (indexShortCutPanel == null) {
				HashVO[] allLeafNodeHvs = (HashVO[]) al_allLeafNodes.toArray(new HashVO[0]); //
				indexShortCutPanel = new IndexShortCutPanel(allLeafNodeHvs); //
				JPanel tabPanel = (JPanel) _radioPane.getTabCompent("快捷访问"); //
				tabPanel.removeAll(); //
				tabPanel.setLayout(new BorderLayout()); //
				tabPanel.add(indexShortCutPanel); //
				tabPanel.updateUI(); //
				_radioPane.showOneTab(str_seledkey); //
			}
		} else if ("管理导航图".equals(str_seledkey)) { //管理导航图!多个gps图用多页签显示。一个图保持原有显示[郝明2012-04-05]
			if (gpstabpane == null) {
				if (bomPanel != null) {
					return;
				} //如果就一个管理导航图，那么bomPanel会被实例化。直接返回即可
				if (title_panel != null) {
					final int li_deskTopLayout = getDesktopLayout(); //桌面布局!
					if (title_panel.isVisible()) {
						title_panel.setVisible(false); //
						if (splitPanel_2 != null && splitLeftTopPanel != null && li_deskTopLayout != 3 && li_deskTopLayout != 4) {
							splitPanel_2.setDividerLocation(0); //
							splitPanel_2.setDividerSize(0);
							splitLeftTopPanel.setVisible(false); //
						}
						if (buttonMenuBarPanel != null) {
							buttonMenuBarPanel.setVisible(false); //
						}
					} else {
						title_panel.setVisible(true); //
						if (splitPanel_2 != null && splitLeftTopPanel != null && li_deskTopLayout != 3 && li_deskTopLayout != 4) { //按钮栏不处理!!!
							splitLeftTopPanel.setVisible(true); //
							splitPanel_2.setDividerLocation(MenuTreeWidth); //
							splitPanel_2.setDividerSize(4);
						}
						if (buttonMenuBarPanel != null) {
							buttonMenuBarPanel.setVisible(true); //
						}
					}
				}//九台演示用【李春娟/2017-04-11】
				if (btn_hiddentitle != null) {
					btn_hiddentitle.setIcon(icon_down);
				}
				if (title_panel != null) {
					title_panel.setVisible(false);
				}
				HashVO gpsvos[] = UIUtil.getHashVoArrayByDS(null, "select * from pub_bom where code like 'gps%' order by code"); //以前只有一个gps图,后来发现应该可以多种视角!【xch/2012-4-10】bom参数配置必须为 gps1、gps2的样子。因为系统已经存在了gps_hegui第二层图
				if (gpsvos != null && gpsvos.length == 1) { //如果就1个导航图,还搞原来的风格
					if (bomPanel == null) {
						JPanel tabPanel = (JPanel) _radioPane.getTabCompent("管理导航图"); //
						tabPanel.removeAll(); //
						tabPanel.setLayout(new BorderLayout()); //
						bomPanel = getBomPanel(gpsvos[0].getStringValue("code"));
						tabPanel.add(bomPanel); //
						tabPanel.updateUI(); //
						_radioPane.showOneTab(str_seledkey); //
						return;
					}
				}
				gpstabpane = new JTabbedPane();
				if (gpsvos != null && gpsvos.length > 1) {
					String code = gpsvos[0].getStringValue("code");
					String descr = gpsvos[0].getStringValue("descr");
					String icon = "office_200.gif";
					if (descr != null && descr.trim().startsWith("[") && descr.contains("]")) {
						icon = descr.substring(1, descr.indexOf("]") - 1);
						descr = descr.substring(descr.indexOf("["));
					}
					gpstabpane.addTab(descr, UIUtil.getImage(icon), getBomPanel(code));
					JPanel tabPanel = (JPanel) _radioPane.getTabCompent("管理导航图"); //
					tabPanel.removeAll(); //
					tabPanel.setLayout(new BorderLayout()); //
					tabPanel.add(gpstabpane); //
					tabPanel.updateUI(); //
					_radioPane.showOneTab(str_seledkey); //
				}
				for (int i = 1; i < gpsvos.length; i++) { //其他的在点击后加载。
					JPanel content = new JPanel();
					content.putClientProperty("code", gpsvos[i].getStringValue("code")); //bom编码存放一个标记。
					String descr = gpsvos[i].getStringValue("descr", "");
					String icon = "office_200.gif"; //默认图标
					if (descr != null && descr.trim().startsWith("[") && descr.contains("]")) {
						icon = descr.substring(1, descr.indexOf("]"));
						descr = descr.substring(descr.indexOf("]") + 1);
					}
					gpstabpane.addTab(descr, UIUtil.getImage(icon), content);
				}
				gpstabpane.addChangeListener(new ChangeListener() { //添加切换事件
							public void stateChanged(ChangeEvent e) {
								JPanel jPanel = (JPanel) gpstabpane.getComponentAt(gpstabpane.getSelectedIndex());
								String pgscode = (String) jPanel.getClientProperty("code"); //找到该bom的编码
								if (pgscode != null) {
									try {
										jPanel.removeAll();
										jPanel.setLayout(new BorderLayout()); //
										jPanel.putClientProperty("code", null); //如果加载过，就设置此标记为空
										jPanel.add(getBomPanel(pgscode));
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							}
						});
			}
		}
	}

	/*
	 * 反射一个bom面板.
	 */
	private JPanel getBomPanel(String _code) throws Exception {
		Class bomCls = Class.forName("cn.com.infostrategy.ui.workflow.pbom.BillBomPanel"); //
		Constructor bomConst = bomCls.getConstructor(new Class[] { String.class }); //取得构造方法!!
		JPanel panel = (JPanel) bomConst.newInstance(_code); //
		return panel;
	}

	/**
	 * 修改登录人员风格!!!
	 */
	private void updateUserLoginStyle() {
		try {
			String selectKey = deskIndexRadioPanel.getSelectKey(); //选中的值!
			int li_choose = 1; //
			for (int i = 0; i < str_indexarray.length; i++) {
				if (str_indexarray[i].equals(selectKey)) {
					li_choose = i + 1; //
					break; //
				}
			}
			String str_style = getDeskTopAndIndexStyle(getDesktopLayout(), li_choose); //
			//System.out.println("会修改人员的风格字段么?邮储项目中发现这个曾经产生过严重的登录性能问题,; //
			//原因在于:登录时总是会触发这条SQL,然后这条SQL又会触发数据库日志表(因为对pub_user表进行了更新监控)
			//这样一来就会很慢,而且是一个事务,而且压力测试时是同一个人,会产生行级锁!!!这样更是完蛋
			//现在做了三个处理:1.登录时不执行这个SQL,只在点击切换时时触发   2.这条SQL事务级别是自动提交,不会锁住这条记录!!!  3.监控数据库日志时,忽略这条SQL
			//以上三个一处理!! 则登录性能肯定不存了!
			UIUtil.executeUpdateByDS(null, "update pub_user set desktopstyle='" + str_style + "' where id=" + ClientEnvironment.getCurrSessionVO().getLoginUserId());
			ClientEnvironment.getCurrLoginUserVO().setDeskTopStyle(str_style); //修改内存变量
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 点击所有按钮时的逻辑!!!
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			deskTopPanel.realCallMenuName = "首页";
			if (e.getSource() == menuItem_clientenv) { // 客户端环境变量
				onShowClientEnv(); //
			} else if (e.getSource() == menuItem_serverenv) { // 服务器端环境变量serverPopButton.getButton()
				onShowServerEnv(); //
			} else if (serverPopButton != null && e.getSource() == serverPopButton.getButton()) { //服务器端环境变量
				onShowServerEnv(); //
			} else if (e.getSource() == menuItem_serverconsole) { // 服务器端控制台
				onShowServerConsole(); //
			} else if (e.getSource() == menuItem_state) {//打开服务器State
				onShowServerState();
			} else if (e.getSource() == menuItem_serverlog) { // 服务器端日志
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.ShowServerFrame", this, null, null);
			} else if (e.getSource() == menuItem_serverscreen) { // 服务器屏幕
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.CaptureServerScreenFrame", this, null, null);
			} else if (e.getSource() == menuItem_serverexplorer) { // [sunfujun/20120524]
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.ServerFileSystemMonitorFrame", this, null, null);
			} else if (e.getSource() == menuItem_sqltool) { // SQL工具
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.SQLQueryFrame", this, null, null);
			} else if (e.getSource() == menuItem_sqllistener) { // 跟踪SQL
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.SessionSQLListenerWFPanel", this, null, null);
			} else if (e.getSource() == menuItem_sqlbuding) { // 加密补丁
				try {
					BillFrame frame = new BillFrame(this, "SQL补丁更新");
					final JPanel bp = new JPanel();
					bp.setLayout(new FlowLayout());
					WLTLabel wltl = new WLTLabel("SQL补丁更新");
					wltl.setPreferredSize(new Dimension(100, 20));
					wltl.setBorder(BorderFactory.createEtchedBorder());
					WLTButton daoru = new WLTButton("导入");
					daoru.addActionListener(new ActionListener() {

						public void actionPerformed(ActionEvent e) {
							JFileChooser chooser = new JFileChooser();
							chooser.setFileFilter(new FileFilter() {
								public boolean accept(File f) {
									if (f.getAbsolutePath().endsWith(".push") || f.isDirectory()) {
										return true;
									}
									return false;
								}

								public String getDescription() {
									return "需要导入的.push类型文件";
								}
							});
							int result = chooser.showOpenDialog(bp); //防止在最右下角弹出!
							if (result == 0 && chooser.getSelectedFile() != null) {
								File allChooseFiles = chooser.getSelectedFile(); //
								if (!allChooseFiles.getName().endsWith(".push")) {
									MessageBox.showInfo(bp, "请选择.push类型文件");
									return;
								}
								try {
									UIUtil.executeBatchByDSNoLog(null, getSecretString(chooser.getSelectedFile()));
								} catch (Exception e1) {
									e1.printStackTrace();
									MessageBox.showInfo(bp, "执行失败");
									return;
								}
								MessageBox.showInfo(bp, "执行成功");
							} else {
								return;
							}
						}

					});
					bp.add(wltl);
					bp.add(daoru);

					frame.setSize(500, 80); //
					frame.setIconImage(UIUtil.getImage("office_164.gif").getImage());
					frame.getContentPane().add(bp); //
					frame.setLocationRelativeTo(null);
					frame.setVisible(true); //
					frame.toFront(); //
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (e.getSource() == btn_desktopstyle || e.getSource() == tool_menuitem_otheruser) {
				tbUtil.refectCallClassStaticMethod("cn.com.infostrategy.ui.sysapp.login.click2.ModifyDesktopStyleDialog", "openMe", new Object[] { this, LoginAppletLoader.getAppletLoader(), getDesktopLayout(), getIndexLayout(), getDeskTopAndIndexStyleMartix() });
			} else if (e.getSource() == btn_clientconsole || e.getSource() == tool_menuitem_control_client) { // 客户端控制台
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.ClientConsoleMsgFrame", this, null, null);
			} else if (e.getSource() == btn_serverconsole || e.getSource() == tool_menuitem_control_server) { // 服务器端控制台
				onShowServerConsole(); //
			} else if (e.getSource() == btn_updatepwd || e.getSource() == tool_menuitem_pwd) {
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.ResetPwdDialog", this, null, null);
			} else if (e.getSource() == btn_userinfo) {
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.UserInfoDialog", this, null, null);
			} else if (e.getSource() == btn_hiddensplit || e.getSource() == menuItem_hidemenubar) {
				showHiddenMenuBar();
			} else if (e.getSource() == btn_hiddentitle) { //隐藏标题图片
				if (title_panel.isVisible()) {
					btn_hiddentitle.setIcon(icon_down);
					title_panel.setVisible(false);
				} else {
					btn_hiddentitle.setIcon(icon_up);
					title_panel.setVisible(true);
				}
			} else if (e.getSource() == btn_sysregmenu) { //系统注册的菜单!
				onLoadSysRegMenu(); //
			} else if (e.getSource() == btn_relogin || e.getSource() == tool_menuitem_otheruser) {
				onRelogin(); //
			} else if (e.getSource() == btn_zhuxiao) {
				LoginAppletLoader.getAppletLoader().loadLoginPanel(null, null);
			} else if (e.getSource() == btn_help || e.getSource() == tool_menuitem_help) {//什么情况/sunfujun/20121106
				onHelp(); //
			} else if (e.getSource() == tool_menuitem_showsession) {
				showCurrSessionVO();
			} else if (e.getSource() == btn_exit) {
				if (!MessageBox.confirm(DeskTopPanel.this, "您真的想退出系统吗？")) {
					return;
				}
				onExit();
			} else if (e.getSource() == menuItem_1_closethis) {
				closeSelfTab();
			} else if (e.getSource() == menuItem_2_closeothers) {
				closeOtherTab(); //
			} else if (e.getSource() == menuItem_3_closeall) {
				closeAllTab();
			} else if (e.getSource() == menuItem_config_desktop) {
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.MWFPanel_Pub_Desktop_New", this, null, null);
			} else if (e.getSource() == menuItem_refresh_indexPage) {
				refreshAllTaskGroup(); //
			} else if (e.getSource() == menuItem_hidetoolbar) {
				getToolBarPanel().setVisible(!getToolBarPanel().isVisible()); //
			} else if (e.getSource() == menuItem_hideradiopanel) {
				if (deskIndexRadioPanel != null) {
					deskIndexRadioPanel.setBtnPanelVisible(!deskIndexRadioPanel.isBtnPanelVisible()); //
				}
			} else if (e.getSource() == menuItem_hidesystabpanel) {
				if (MessageBox.confirm(DeskTopPanel.this, "您确定要隐藏页签吗?\r\n提示: 右键单击右下角用户信息区域可恢复显示.")) {
					workTabbedPanel.setBtnPanelVisible(false); //
				}
			} else if (e.getSource() == menuItem_config_person) { // 个人定制
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.index.MWFPanel_Pub_Desktop_Person", this, null, null);
			} else if (e.getSource() == menuItem_config_right) { // 权限配置
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.index.MWFPanel_Pub_Desktop_Right", this, null, null);
			} else if (e.getSource() == tool_font_revise) {
				int changevalue = 0;
				if (tool_font_revise.getText().equals("字体放大")) {
					changevalue = 2;
				}
				UIUtil.executeUpdateByDS(null, "update pub_user set fontrevise = '" + changevalue + "' where id = " + ClientEnvironment.getCurrLoginUserVO().getId()); //放大两号
				ClientEnvironment.getInstance().getCurrLoginUserVO().setFontrevise(changevalue);
				LoginAppletLoader.getAppletLoader().dealLogin(true, ClientEnvironment.getInstance().getLoginUserCode(), null, null, ClientEnvironment.chooseISys, ClientEnvironment.getInstance().isAdmin(), true, ClientEnvironment.getInstance().getDefaultLanguageType(), null); //重新一下!
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			deskTopPanel.realCallMenuName = null;
		}
		deskTopPanel.realCallMenuName = null;
	}

	//反射来打开界面减少访问首页的下载量
	private void invokeOpenMe(String classname, Container parent, String title, String type) {
		try {
			Class cls = Class.forName(classname); //
			Class[] parCls = null; //
			Method method = cls.getMethod("openMe", new Class[] { Container.class, String.class, String.class }); //
			Object obj = method.invoke(null, new Object[] { parent, title, type }); //
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 树型结构时返回所有功能菜单树!!
	 * @return
	 */
	private JTree getFirstTree() {
		JTree tree = new JTree(allTreeRootNode);
		tree.setOpaque(false); //一定要透明
		tree.setUI(new WLTTreeUI(true, false)); //
		tree.setBackground(LookAndFeel.treebgcolor); //
		tree.setRootVisible(false); //
		tree.setShowsRootHandles(true); //
		tree.setRowHeight(19); //
		MyTreeCellRender myTreeCellRender = new MyTreeCellRender(null);
		tree.setCellRenderer(myTreeCellRender);

		tree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				JTree tree = (JTree) evt.getSource(); //
				if (evt.getButton() == MouseEvent.BUTTON3) {
					showMenuRightPopMenu(tree, evt.getX(), evt.getY()); // 弹出菜单
				} else if (evt.getClickCount() == 2) {
					onClickTree(tree);
				}
			}
		});

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent evt) {
				//				Object obj = evt.getSource();
				TreePath[] paths = evt.getPaths();
				for (int i = 0; i < paths.length; i++) {
					if (evt.isAddedPath(i)) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
						onChangeSelectTree(((JTree) evt.getSource()), node); //
					}
				}
			}
		});

		return tree; //
	}

	/**
	 * 取得第二层的树!!!即每个outlookBar中的树!!!
	 * @param _pos
	 * @return
	 */
	private JTree getSecondTree(int _pos) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.allTreeRootNode.getChildAt(_pos); //
		JTree tree = new JTree(node); //
		tree.setOpaque(false); //一定要透明
		tree.setUI(new WLTTreeUI(true, false)); //
		tree.setBackground(LookAndFeel.defaultShadeColor1); //
		tree.setRootVisible(false); //
		tree.setShowsRootHandles(true); //
		tree.setRowHeight(19); //
		MyTreeCellRender myTreeCellRender = new MyTreeCellRender(null);
		tree.setCellRenderer(myTreeCellRender);

		tree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				JTree tree = (JTree) evt.getSource(); //
				if (evt.getButton() == MouseEvent.BUTTON3) {
					showMenuRightPopMenu(tree, evt.getX(), evt.getY()); // 弹出菜单
				} else if (evt.getClickCount() == 2) {
					onClickTree(tree);
				}
			}
		});

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent evt) {
				TreePath[] paths = evt.getPaths();
				for (int i = 0; i < paths.length; i++) {
					if (evt.isAddedPath(i)) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
						onChangeSelectTree(((JTree) evt.getSource()), node); //
					}
				}
			}
		});
		return tree; //
	}

	/**
	 * 取得所有结点
	 * @return
	 */
	public DefaultMutableTreeNode[] getAllNodes() {
		ArrayList al_nodes = new ArrayList();
		visitAllNodes(al_nodes, this.allTreeRootNode); //
		return (DefaultMutableTreeNode[]) al_nodes.toArray(new DefaultMutableTreeNode[0]); //
	}

	//遍历所有结点!
	private void visitAllNodes(ArrayList _list, TreeNode node) {
		_list.add(node); // 加入该结点
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // 找到该儿子!!
				visitAllNodes(_list, childNode); // 继续查找该儿子
			}
		}
	}

	/**
	 * 根据id取得某个结点!!
	 * @param _id
	 * @return
	 */
	private DefaultMutableTreeNode findNodeByID(String _id) {
		DefaultMutableTreeNode[] nodes = this.getAllNodes();
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].isRoot()) {
				continue;
			}
			Object obj = nodes[i].getUserObject();
			if (obj instanceof HashVO) {
				HashVO hvo = (HashVO) obj; //
				if (hvo.getStringValue("id").equals(_id)) {
					return nodes[i]; //
				}
			}

		}
		return null;
	}

	public HashVO[] findOneNodeAllChildNOdeVOs(String _id) {
		DefaultMutableTreeNode node = findNodeByID(_id); //
		ArrayList al_nodes = new ArrayList(); //
		visitAllNodes(al_nodes, node); //
		DefaultMutableTreeNode[] childNodes = (DefaultMutableTreeNode[]) al_nodes.toArray(new DefaultMutableTreeNode[0]); //
		ArrayList al_hvs = new ArrayList(); //
		for (int i = 0; i < childNodes.length; i++) {
			if (childNodes[i].isLeaf()) { //只有叶子结点才加入!!
				HashVO hvo_item = (HashVO) childNodes[i].getUserObject(); //
				al_hvs.add(hvo_item); //
			}
		}
		return (HashVO[]) al_hvs.toArray(new HashVO[0]); //
	}

	/**
	 * 判断一个结点是否是叶子结点!!!!!!
	 */
	public int getOneMenuType(String _id) {
		DefaultMutableTreeNode node = findNodeByID(_id); //
		if (node == null) {
			return -1; //问题结点!!
		}
		if (node.isLeaf()) {
			return 1; //是叶子结点!!
		} else {
			return 0; //不是叶子结点!!
		}
	}

	/**
	 * 取得第一层菜单结点数据!!!
	 * @return
	 */
	private HashVO[] getFirstLevelData() {
		int li_count = allTreeRootNode.getChildCount(); //
		HashVO[] vos = new HashVO[li_count]; //
		for (int i = 0; i < li_count; i++) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) allTreeRootNode.getChildAt(i);
			HashVO childVO = (HashVO) childNode.getUserObject(); //
			vos[i] = new HashVO();
			vos[i].setAttributeValue("ID", childVO.getStringValue("id")); //
			vos[i].setAttributeValue("NAME", childVO.getStringValue("name")); //
			vos[i].setAttributeValue("ICON", childVO.getStringValue("icon")); //
		}
		return vos;
	}

	/**
	 * 根据菜单结点,取得该结点路径上的所有结点!!
	 * @param _node
	 * @return
	 */
	private HashVO getMenuVOFromNode(DefaultMutableTreeNode _node) {
		HashVO hvo = (HashVO) ((DefaultMutableTreeNode) _node).getUserObject(); //取得绑定的数据!!
		return hvo;
	}

	/**
	 * 树型结点选择变化!
	 * @param _tree
	 * @param _node
	 */
	private void onChangeSelectTree(JTree _tree, DefaultMutableTreeNode _node) {
		DefaultTreeModel model = (DefaultTreeModel) _tree.getModel();
		TreeNode[] nodes = model.getPathToRoot(_node); // 取得所有
		TreePath path = new TreePath(nodes);
		_tree.setCellRenderer(new MyTreeCellRender(path));
		HashVO vo = (HashVO) _node.getUserObject();

		String str_usecmdtype = "1"; //
		String str_cmdtype = vo.getStringValue("commandtype");
		String str_cmd = vo.getStringValue("command");
		if (vo.getStringValue("usecmdtype") != null) {
			if (vo.getStringValue("usecmdtype").equals("2")) {
				str_usecmdtype = "2";
				str_cmdtype = vo.getStringValue("commandtype2");
				str_cmd = vo.getStringValue("command2");
			} else if (vo.getStringValue("usecmdtype").equals("3")) {
				str_usecmdtype = "3";
				str_cmdtype = vo.getStringValue("commandtype3");
				str_cmd = vo.getStringValue("command3");
			}
		}

		String str_text = "<" + vo.getStringValue("code") + "><" + str_usecmdtype + "><" + str_cmdtype + "> <" + str_cmd + ">";
		if (ClientEnvironment.getInstance().isAdmin()) { //管理员身份登陆
			if ("11".equals(str_cmdtype)) {//XML注册的系统功能!
				String id = vo.getStringValue("id"); //取得功能点ID
				Object obj = map_openedWorkPanel.get(id);//取得该功能点是否已经打开！
				String realpath = null; //
				if (obj instanceof WLTFrame) {
					WLTFrame frame = (WLTFrame) obj;//
					realpath = (String) frame.getWorkPanel().getClientProperty("$realpath");//取得实际路径！
				} else if (obj instanceof WorkTabbedPanel) { //WLTFrame
					WorkTabbedPanel wkp = (WorkTabbedPanel) obj;
					realpath = (String) wkp.getWorkPanel().getClientProperty("$realpath"); //取得实际路径！		
				}
				if (realpath != null) {
					str_text += "<" + realpath + ">";
				}
			}
			setInfomation(str_text); //设置状态显示
		}
	}

	public void setLoginTimeLabelText(String _text) {
		if (jvmInfoLabel != null) {
			jvmInfoLabel.setText(_text);
		}
	}

	/**
	 * 显示当前SessionVO
	 */
	protected void showCurrSessionVO() {
		String sessionVOInfo = ClientEnvironment.getInstance().getClientEnvToString(); //
		MessageBox.showTextArea(this, sessionVOInfo); //
	}

	private void onRelogin() {
		ReloginDialog dialog = new ReloginDialog(this, LoginAppletLoader.getAppletLoader()); //
		dialog.setVisible(true); //
	}

	/**
	 * 加载系统注册菜单!!!
	 */
	private void onLoadSysRegMenu() { //
		String str_menutype = (String) splitLeftTopPanel.getClientProperty("LoadMenuType"); //
		int li_desktopLayout = getDesktopLayout(); //桌面风格!
		if ("DBMenu".equals(str_menutype)) { //
			if (li_desktopLayout == 3) { //如果是按钮栏风格
				splitLeftTopPanel.setVisible(true); //
				splitPanel_2.setDividerLocation(MenuTreeWidth);
				splitPanel_2.setDividerSize(4);
			}

			if (sysRegMenuTree == null) { //如果还没创建,则创建并加载!
				cn.com.infostrategy.ui.sysapp.registmenu.RegistMenuTreePanel panel = new cn.com.infostrategy.ui.sysapp.registmenu.RegistMenuTreePanel(); //
				sysRegMenuTree = panel.getMenuTree(); //
				sysRegMenuTree.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3)); //
				sysRegMenuTree.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent evt) {
						JTree tree = (JTree) evt.getSource(); //
						if (evt.getClickCount() == 2) { //如果双击两次
							onClickSysRegMenuTree(); //
						}
					}
				});

				JScrollPane scroll = new JScrollPane(sysRegMenuTree); //
				splitLeftTopPanel.add(scroll, "SysRegMenu"); //
				cardLayout.show(splitLeftTopPanel, "SysRegMenu"); //
			} else {
				cardLayout.show(splitLeftTopPanel, "SysRegMenu"); //
			}
			splitLeftTopPanel.putClientProperty("LoadMenuType", "SysRegMenu"); //
		} else if ("SysRegMenu".equals(str_menutype)) {
			if (li_desktopLayout == 3) { //如果是按钮栏风格,则隐藏
				splitLeftTopPanel.setVisible(false); //
				splitPanel_2.setDividerLocation(0);
				splitPanel_2.setDividerSize(0);
			}
			cardLayout.show(splitLeftTopPanel, "DBMenu"); //
			splitLeftTopPanel.putClientProperty("LoadMenuType", "DBMenu"); //
		}
	}

	private void onHelp() {
		try {
			if (ClientEnvironment.isAdmin()) { //如果是实施/开发模式，则多个提示按钮【查看实施/开发方法论】,即以后奖我们平时培训的一些关键技巧，例子代码都直接放在系统中了！然后可以在系统中直接查看!!
				int li_do = MessageBox.showOptionDialog(this, "您确定查看帮助文件吗?如果文件很大,打开可能需要一点时间!", "提示", new String[] { "查看帮助", "查看实施/开发方法论", "取消" }); //
				if (li_do == 2 || li_do == -1) {
					return; //
				} else if (li_do == 1) { //如果是查看方法论!则使用浏览器打开!
					UIUtil.openRemoteServerHtml("cn.com.infostrategy.bs.sysapp.help.HelpWebCallBean", null); //
					return; //
				}
			} else {
				if (!MessageBox.confirm(this, "您确定查看帮助文件吗?如果文件很大,打开可能需要一点时间!")) {
					return; //
				}
			}
			String openType = tbUtil.getSysOptionStringValue("帮助文档是否可编辑", "N"); //系统默认点击帮助文档为千航打开，不可编辑。
			if ("N".equalsIgnoreCase(openType)) { //默认用千行控件打开
				OfficeCompentControlVO officeVO = new OfficeCompentControlVO(false, false, false, null); //
				officeVO.setIfshowsave(false);
				officeVO.setIfshowprint_all(false);
				officeVO.setIfshowprint_fen(false);
				officeVO.setIfshowprint_tao(false);
				officeVO.setIfshowedit(false);
				officeVO.setToolbar(false);
				officeVO.setIfshowclose(false);
				officeVO.setPrintable(false);
				officeVO.setMenubar(false);
				officeVO.setMenutoolbar(false);
				officeVO.setIfshowhidecomment(false);
				officeVO.setTitlebar(false);
				officeVO.setIfshowprint(false);
				officeVO.setIfshowhidecomment(false);
				officeVO.setIfshowshowcomment(false);
				officeVO.setIfshowacceptedit(false);
				officeVO.setIfshowshowedit(false);
				officeVO.setIfshowhideedit(false);
				officeVO.setIfshowwater(false);
				officeVO.setIfShowResult(false); //不显示结果区域显示。
				officeVO.setIfselfdesc(true); //关键
				officeVO.setSubdir("help");
				Class cls = Class.forName("cn.com.infostrategy.ui.mdata.BillOfficePanel"); //之所以这么写是为了在出现登录界面时不下载 ui.metadata包,即要控制在出现登录界面时只下载少量的包,在出现主界面时才下载更多的包!!
				Constructor cons = cls.getConstructor(new Class[] { String.class, OfficeCompentControlVO.class }); //
				JPanel pane = (JPanel) cons.newInstance(new Object[] { "WLTHELPDOCUMENT.doc", officeVO }); //
				Class cls_dialog = Class.forName("cn.com.infostrategy.ui.mdata.BillOfficeDialog"); //之所以这么写是为了在出现登录界面时不下载 ui.metadata包,即要控制在出现登录界面时只下载少量的包,在出现主界面时才下载更多的包!!
				Constructor cons_dialog = cls_dialog.getConstructor(new Class[] { Container.class, cls }); //
				BillDialog dialog = (BillDialog) cons_dialog.newInstance(new Object[] { this, pane }); //
				dialog.maxToScreenSizeBy1280AndLocationCenter();
				dialog.setTitle("首页帮助文档");
				dialog.setVisible(true);
			} else {
				UIUtil.openRemoteServerFile("serverdir", "/help/WLTHELPDOCUMENT.doc"); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	//上传附件!!
	private void onUploadHelp() {
		try {
			if (!ClientEnvironment.getInstance().isAdmin()) { //如果不是管理身份
				SysAppServiceIfc sysAppService = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
				boolean isSuperAdmin = sysAppService.isLoginUserContainsRole(new String[] { "超级系统管理员", "超级系统管理员/总行法律与合规部管理员" }); //如果是超级管理员则也能处理
				if (!isSuperAdmin) {
					JOptionPane.showMessageDialog(btn_help, "只有管理员才能进行此操作!"); //
					return; //
				}
			}
			if (JOptionPane.showConfirmDialog(btn_help, "您真的想上传帮助文件吗?\r\n这将覆盖原来的帮助文件,请谨慎操作!", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}
			JFileChooser chooser = new JFileChooser();
			//chooser.setCurrentDirectory(new File("C:/"));
			int result = chooser.showOpenDialog(btn_help); //防止在最右下角弹出!
			if (result == 0) { //必须选中文件!!
				File chooseFile = chooser.getSelectedFile(); //选中的文件!
				if (chooseFile != null) {
					try {
						String str_path = chooseFile.getParentFile().getPath(); //
						if (str_path.endsWith("\\")) {
							str_path = str_path.substring(0, str_path.length() - 1); //
						}
						String str_result = UIUtil.upLoadFile("/help", "WLTHELPDOCUMENT.doc", false, str_path, chooseFile.getName(), true, false, false); //实际上传文件!!
						JOptionPane.showMessageDialog(btn_help, "上传帮助文件成功!该文件在服务器端的路径是[" + str_result + "]"); //
					} catch (Exception ex) {
						MessageBox.showException(btn_help, ex); //
					}
				}
			}
		} catch (Exception ex) {
			MessageBox.showException(btn_help, ex); //
		}
	}

	/**
	 * 退出!
	 */
	private void onExit() {
		// 远程访问处理该用户退出处理逻辑..比如修改该用户在线标记等!
		try {
			SysAppServiceIfc loginService = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class, 10); // 寻找服务,如果10秒内没反应则中断线程!也就是说一定不能让用户白屏.
			//退出系统，记录日志
			CurrLoginUserVO loginUserVO = ClientEnvironment.getCurrLoginUserVO();
			loginService.loginOut(loginUserVO.getId(), ClientEnvironment.getCurrSessionVO().getHttpsessionid()); //退出清除Session			
		} catch (java.lang.ClassNotFoundException ex) {
			ex.printStackTrace(); //
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	/**
	 * 设置状态栏信息!
	 * 
	 * @param sta
	 */
	public synchronized static void setInfomation(String sta) {
		DeskTopPanel.deskTopPanel.informationLabel.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()) + "  " + sta);
	}

	/**
	 * 设置状态栏显示,选中list列表时的表格信息
	 * @param result
	 * @author YangQing/2013-09-25
	 */
	public synchronized static void setSelectInfo(String result) {
		if (DeskTopPanel.getDeskTopPanel() == null) {
			return;
		}
		if (TBUtil.isEmpty(result)) {
			DeskTopPanel.deskTopPanel.selectedInfo.setVisible(false);
		} else {
			DeskTopPanel.deskTopPanel.selectedInfo.setVisible(true);
			DeskTopPanel.deskTopPanel.selectedInfo.setText(result);
		}
	}

	private void onShowClientEnv() {
		invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.ShowClientEnvDialog", this, null, null); //
	}

	private void onShowServerEnv() {
		invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.ShowServerEnvDialog", this, null, null); //
	}

	/**
	 * 显示服务器端控制台.极其有用..
	 */
	private void onShowServerConsole() {
		invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.ShowServerConsoleWFPanel", this, null, null); //
	}

	/*
	 * 显示服务器端State
	 */
	private void onShowServerState() {
		JFrame frame = new JFrame("服务器State");
		frame.setBounds(100, 100, 900, 500);
		String url = ClientEnvironment.getInstance().getCallUrl() + "/state";
		try {
			Class bomCls = Class.forName("cn.com.infostrategy.ui.report.BillHtmlPanel");
			Constructor bomConst = bomCls.getConstructor(new Class[] { boolean.class }); //取得构造方法!!
			JPanel htmlPanel = (JPanel) bomConst.newInstance(Boolean.FALSE); //
			Method method = bomCls.getMethod("loadWebContentByURL", new Class[] { URL.class });
			method.invoke(htmlPanel, new URL(url));
			frame.getContentPane().add(htmlPanel);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		frame.setVisible(true);
	}

	private void myExpandAll(JTree _tree) {
		expandAll(_tree, true);
	}

	private void myCollapseAll(JTree _tree) {
		expandAll(_tree, false);
	}

	/**
	 * 展开树中的第一层,因为在树型查看时,有的人因为权限原因,一级菜单只有2-3个,解码器后就缩在一起，不好看,需要展开下显得好看些!!所以就搞了这个方法!【xch/2012-03-06】
	 * @param tree
	 * @param _max
	 */
	private void expandFirstLevel(JTree _tree, int _max) {
		DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel(); //
		TreeNode root = (TreeNode) treeModel.getRoot(); //
		int li_count = root.getChildCount(); //
		if (li_count > _max) {
			return; //只有在多少个子结点以内的才展开处理,超过8个本来就比较好看了,没必要再展开!!!
		}
		for (int i = 0; i < li_count; i++) {
			_tree.expandPath(new TreePath(treeModel.getPathToRoot(root.getChildAt(i)))); //
			//_tree.expandPath(new TreePath(new TreeNode[] { root, root.getChildAt(i) })); //这个方法也行!即关键是TreePath的构造参数是一个TreeNode[],即是一批结点!!
		}
	}

	public void expandAll(JTree tree, boolean expand) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		if (expand) {
			expandAll(tree, new TreePath(root), expand); //
		} else {
			if (tree.isRootVisible()) { // 如果根结点是显示的!!!
				expandAll(tree, new TreePath(root), expand); //
			} else {
				expandAll(tree, new TreePath(root), expand); //
				tree.expandPath(new TreePath(root)); //
			}
		}
	}

	private void expandAll(JTree tree, TreePath parent, boolean expand) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}
		if (expand) {
			tree.expandPath(parent);

		} else {
			if (!node.isRoot()) {
				tree.collapsePath(parent);
			}
		}
	}

	//多页签右键的弹出菜单!!
	private void showWorkTabRightPopMenu(JComponent _compent, int _x, int _y) {
		String str_clientInfo = (String) _compent.getClientProperty("ClientProperty");
		workTabbedPanel.setSelectedComponentByClientInfo(str_clientInfo); //
		int desktoplayout = getDesktopLayout();
		if (indexPopMenu == null) {
			indexPopMenu = new JPopupMenu(); //创建之!!!
			if (desktoplayout != 3) {
				menuItem_refresh_indexPage = new JMenuItem("刷新首页", UIUtil.getImage("office_191.gif")); //
				menuItem_config_desktop = new JMenuItem("配置首页布局", UIUtil.getImage("office_154.gif")); //
				menuItem_hidetoolbar = new JMenuItem("隐藏工具条");
				menuItem_hidemenubar = new JMenuItem("隐藏菜单");
				menuItem_hideradiopanel = new JMenuItem("隐藏风格栏", UIUtil.getImage("office_002.gif")); //
				menuItem_hidesystabpanel = new JMenuItem("隐藏页签", UIUtil.getImage("office_117.gif")); //以前是百度那小图标。。
				menuItem_config_person = new JMenuItem("首页个人订制", UIUtil.getImage("office_112.gif"));
				menuItem_config_right = new JMenuItem("首页板块权限分配", UIUtil.getImage("office_092.gif"));
				menuItem_refresh_indexPage.setBackground(LookAndFeel.defaultShadeColor1); //
				menuItem_config_desktop.setBackground(LookAndFeel.defaultShadeColor1); //
				menuItem_hidetoolbar.setBackground(LookAndFeel.defaultShadeColor1); //
				menuItem_hidemenubar.setBackground(LookAndFeel.defaultShadeColor1);
				menuItem_hideradiopanel.setBackground(LookAndFeel.defaultShadeColor1); //
				menuItem_hidesystabpanel.setBackground(LookAndFeel.defaultShadeColor1); //
				menuItem_config_person.setBackground(LookAndFeel.defaultShadeColor1);
				menuItem_config_right.setBackground(LookAndFeel.defaultShadeColor1);

				menuItem_refresh_indexPage.setUI(new WLTMenuItemUI()); //
				menuItem_config_desktop.setUI(new WLTMenuItemUI()); //

				menuItem_hidetoolbar.setUI(new WLTMenuItemUI()); //
				menuItem_hidemenubar.setUI(new WLTMenuItemUI());
				menuItem_hideradiopanel.setUI(new WLTMenuItemUI()); //
				menuItem_hidesystabpanel.setUI(new WLTMenuItemUI()); //
				menuItem_config_person.setUI(new WLTMenuItemUI());
				menuItem_config_right.setUI(new WLTMenuItemUI());

				menuItem_hidetoolbar.addActionListener(this);
				menuItem_hidemenubar.addActionListener(this);
				menuItem_hideradiopanel.addActionListener(this);
				menuItem_hidesystabpanel.addActionListener(this); //
				menuItem_config_desktop.addActionListener(this); //
				menuItem_refresh_indexPage.addActionListener(this); //
				menuItem_config_person.addActionListener(this);
				menuItem_config_right.addActionListener(this);
			}

			menuItem_1_closethis = new JMenuItem("关闭本页签", UIUtil.getImage("closewindow.gif")); //
			menuItem_2_closeothers = new JMenuItem("关闭其他页签", UIUtil.getImage("del.gif")); //
			menuItem_3_closeall = new JMenuItem("关闭所有页签", UIUtil.getImage("zt_031.gif")); //

			menuItem_1_closethis.setBackground(LookAndFeel.defaultShadeColor1); //
			menuItem_2_closeothers.setBackground(LookAndFeel.defaultShadeColor1); //
			menuItem_3_closeall.setBackground(LookAndFeel.defaultShadeColor1); //

			menuItem_1_closethis.setUI(new WLTMenuItemUI()); //
			menuItem_2_closeothers.setUI(new WLTMenuItemUI()); //
			menuItem_3_closeall.setUI(new WLTMenuItemUI()); //

			menuItem_1_closethis.setPreferredSize(new Dimension(100, 20));

			menuItem_1_closethis.addActionListener(this); //
			menuItem_2_closeothers.addActionListener(this); //
			menuItem_3_closeall.addActionListener(this); //

			indexPopMenu.add(menuItem_1_closethis); //
			indexPopMenu.add(menuItem_2_closeothers); //
			indexPopMenu.add(menuItem_3_closeall); //
			if (desktoplayout != 3) {
				indexPopMenu.add(menuItem_refresh_indexPage); //刷新首页!
				if (ClientEnvironment.isAdmin()) {//只有管理员身份才可以配置首页布局【李春娟/2012-04-26】
					indexPopMenu.add(menuItem_config_desktop); //
					try {
						if (UIUtil.getCommonService().getSysOptionBooleanValue("首页板块是否启用权限过滤", false)) {
							indexPopMenu.add(menuItem_config_right);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					if (UIUtil.getCommonService().getSysOptionBooleanValue("首页板块是否启用定制功能", false)) {
						indexPopMenu.add(menuItem_config_person);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				indexPopMenu.addSeparator(); //分隔线!
				indexPopMenu.add(menuItem_hidetoolbar); //
				indexPopMenu.add(menuItem_hidemenubar);

				//如果只有一个Radio, 那么"显示四种风格栏"菜单就不要显示出来了. Gwang 2012-11-29
				if (deskIndexRadioPanel.getButtons().length > 1) {
					indexPopMenu.add(menuItem_hideradiopanel);
				}
				indexPopMenu.add(menuItem_hidesystabpanel); //
			}
		} else {
			menuItem_1_closethis.setVisible(true); //关闭自己
			menuItem_2_closeothers.setVisible(true); //关闭其他
			menuItem_3_closeall.setVisible(true); //关闭所有!!
		}
		if (desktoplayout != 3) {
			if (getToolBarPanel().isVisible()) {
				menuItem_hidetoolbar.setIcon(icon_up);
				menuItem_hidetoolbar.setText("隐藏工具条"); //
			} else {
				menuItem_hidetoolbar.setIcon(icon_down);
				menuItem_hidetoolbar.setText("显示工具条"); //
			}

			if (splitLeftTopPanel.isVisible()) {
				menuItem_hidemenubar.setIcon(new ImageIcon(tbUtil.getImageRotate(icon_up.getImage(), 270))); //180度!
				menuItem_hidemenubar.setText("隐藏菜单"); //
			} else {
				menuItem_hidemenubar.setIcon(new ImageIcon(tbUtil.getImageRotate(icon_up.getImage(), 90))); //180度!
				menuItem_hidemenubar.setText("显示菜单"); //
			}

			if (deskIndexRadioPanel != null) {
				if (deskIndexRadioPanel.isBtnPanelVisible()) {
					menuItem_hideradiopanel.setText("隐藏四种风格栏"); //
				} else {
					menuItem_hideradiopanel.setText("显示四种风格栏"); //
				}
			}
		}
		int li_selindex = workTabbedPanel.getSelectedIndex(); //
		if (li_selindex == 0 && workTabbedPanel.getJButtonAt(li_selindex).getText().contains("首页")) { // 如果是首页
			menuItem_1_closethis.setVisible(false); //
			menuItem_2_closeothers.setVisible(false);
			menuItem_3_closeall.setVisible(true); //
			if (deskIndexRadioPanel != null && desktoplayout != 3) {
				menuItem_hideradiopanel.setVisible(true); //4风格，如果有才显示,因为有的客户根本不要这个4种风格切换的!比如中铁的王部长
			} else if (desktoplayout != 3) {
				menuItem_hideradiopanel.setVisible(false); //4风格
			}
			menuItem_refresh_indexPage.setVisible(true); //刷新首页
			menuItem_config_desktop.setVisible(true); //配置
		} else { //如果不是首页!
			menuItem_1_closethis.setVisible(true); //关闭自己
			menuItem_2_closeothers.setVisible(true); //关闭其他
			menuItem_3_closeall.setVisible(true); //关闭所有!!
			if (desktoplayout != 3) {
				menuItem_hideradiopanel.setVisible(false); //
				menuItem_refresh_indexPage.setVisible(false); //
				menuItem_config_desktop.setVisible(false); //
			}
		}
		indexPopMenu.show(_compent, _x, _y); //
	}

	//菜单右键弹出菜单
	private void showMenuRightPopMenu(final JTree _tree, int _x, int _y) {
		JPopupMenu popmenu_header = new JPopupMenu();
		popmenu_header.setBackground(LookAndFeel.defaultShadeColor1); //

		item_expand = new JMenuItem("展开所有功能点", UIUtil.getImage("office_163.gif")); // 展开所有结点
		item_collapse = new JMenuItem("收缩所有功能点", UIUtil.getImage("office_165.gif")); // 收缩所有结点
		item_editconfig = new JMenuItem("快速配置", UIUtil.getImage("office_036.gif")); // 收缩所有结点office_036.gif
		item_showreason = new JMenuItem("查看权限原因", UIUtil.getImage("office_131.gif")); //查看权限原因

		item_expand.setBackground(LookAndFeel.defaultShadeColor1); //
		item_collapse.setBackground(LookAndFeel.defaultShadeColor1); //
		item_editconfig.setBackground(LookAndFeel.defaultShadeColor1); //
		item_showreason.setBackground(LookAndFeel.defaultShadeColor1); //

		item_expand.setUI(new WLTMenuItemUI()); //
		item_collapse.setUI(new WLTMenuItemUI()); //
		item_editconfig.setUI(new WLTMenuItemUI()); //
		item_showreason.setUI(new WLTMenuItemUI()); //

		item_expand.setPreferredSize(new Dimension(120, 20));
		item_collapse.setPreferredSize(new Dimension(120, 20));
		item_editconfig.setPreferredSize(new Dimension(120, 20));
		item_showreason.setPreferredSize(new Dimension(120, 20));

		item_expand.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myExpandAll(_tree);
			}
		});

		item_collapse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myCollapseAll(_tree);
			}
		});

		item_editconfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editConfig(_tree);
			}
		});

		item_showreason.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showReasonInfo(_tree);
			}
		});

		popmenu_header.add(item_expand); //
		popmenu_header.add(item_collapse); //
		if (ClientEnvironment.getInstance().isAdmin()) {
			popmenu_header.add(item_editconfig); //
		}
		popmenu_header.add(item_showreason); //
		popmenu_header.show(_tree, _x, _y); //
	}

	//编辑菜单
	private void editConfig(JTree _tree) {
		if (_tree.getSelectionPath() == null) {
			MessageBox.show(this, "请选择一个功能点进行此操作!"); //
			return;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) _tree.getSelectionPath().getLastPathComponent();
		HashVO hvo = (HashVO) node.getUserObject();
		String str_menuid = hvo.getStringValue("id");
		MenuConfigDialog dialog = new MenuConfigDialog(this, str_menuid); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			HashVO hashVO = dialog.getHvo(); //
			String str_name_c = hashVO.getStringValue("NAME"); //
			String str_name_e = hashVO.getStringValue("ENAME"); //
			String str_usecmdtype = hashVO.getStringValue("USECMDTYPE"); //
			String str_cmdtype = hashVO.getStringValue("COMMANDTYPE"); //
			String str_cmd = hashVO.getStringValue("COMMAND"); //
			String str_cmdtype2 = hashVO.getStringValue("COMMANDTYPE2"); //
			String str_cmd2 = hashVO.getStringValue("COMMAND2"); //
			String str_cmdtype3 = hashVO.getStringValue("COMMANDTYPE3"); //
			String str_cmd3 = hashVO.getStringValue("COMMAND3"); //
			String str_isextend = hashVO.getStringValue("ISEXTEND"); //
			String str_extendheight = hashVO.getStringValue("EXTENDHEIGHT"); //
			String str_opentype = hashVO.getStringValue("OPENTYPE"); //
			String str_opentypeweight = hashVO.getStringValue("OPENTYPEWEIGHT"); //
			String str_opentypeheight = hashVO.getStringValue("OPENTYPEHEIGHT"); //

			hvo.setAttributeValue("NAME", str_name_c); //
			hvo.setAttributeValue("ENAME", str_name_e); //

			hvo.setAttributeValue("USECMDTYPE", str_usecmdtype); //
			hvo.setAttributeValue("COMMANDTYPE", str_cmdtype); //
			hvo.setAttributeValue("COMMAND", str_cmd); //
			hvo.setAttributeValue("COMMANDTYPE2", str_cmdtype2); //
			hvo.setAttributeValue("COMMAND2", str_cmd2); //
			hvo.setAttributeValue("COMMANDTYPE3", str_cmdtype3); //
			hvo.setAttributeValue("COMMAND3", str_cmd3); //

			hvo.setAttributeValue("ISEXTEND", str_isextend); //
			hvo.setAttributeValue("EXTENDHEIGHT", str_extendheight); //
			hvo.setAttributeValue("OPENTYPE", str_opentype); //
			hvo.setAttributeValue("OPENTYPEWEIGHT", str_opentypeweight); //
			hvo.setAttributeValue("OPENTYPEHEIGHT", str_opentypeheight); //

			_tree.setUI(new WLTTreeUI(true)); //
			_tree.validate(); //
		}
	}

	private void showReasonInfo(JTree _tree) {
		try {
			if (_tree.getSelectionPath() == null) {
				MessageBox.show(this, "请选择一个功能点进行此操作!"); //
				return;
			}
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) _tree.getSelectionPath().getLastPathComponent();
			HashVO hvo = (HashVO) node.getUserObject();
			String str_menuid = hvo.getStringValue("id");
			String str_reason = hvo.getStringValue("$reason"); //原因!!

			StringBuilder sb_info = new StringBuilder(); //
			sb_info.append("菜单[id=" + str_menuid + "]被加入权限的原因:\r\n"); //
			sb_info.append(str_reason + "\r\n\r\n"); //
			sb_info.append("******************** 六种原因情况说明 *****************\r\n"); //
			sb_info.append("A-登录用户不参与权限过滤,所以直接有所有菜单权限\r\n");
			sb_info.append("B-某个菜单永远开放,所以直接有权限\r\n");
			sb_info.append("C-登录用户直接与某菜单绑定了权限\r\n");
			sb_info.append("D-登录用户的某个角色与某菜单绑定了权限\r\n");
			sb_info.append("E-一般员工这个角色绑定了某个菜单,则任何人自动具有该权限\r\n");
			sb_info.append("F-某菜单虽然没有权限,但因为其有个下级菜单有权限,为了保证树型结构自动带入的\r\n\r\n"); //

			//显示功能点所有角色 【杨科/2012-08-31】
			sb_info.append("******************** 该功能点一共授权给了以下角色: *****************\r\n");
			HashVO[] hvs_menuRoles = UIUtil.getHashVoArrayByDS(null, "select id,code,name from pub_role where id in (select roleid from pub_role_menu where menuid = " + str_menuid + " )");
			for (int i = 0; i < hvs_menuRoles.length; i++) { //遍历输出!
				sb_info.append((i + 1) + ".【" + hvs_menuRoles[i].getStringValue("id") + "/" + hvs_menuRoles[i].getStringValue("code") + "/" + hvs_menuRoles[i].getStringValue("name") + "】\r\n"); //
			}

			sb_info.append("\r\n\r\n"); //

			sb_info.append("******************** 本人拥有的所有角色: *****************\r\n");
			HashVO[] hvs_myAllroles = UIUtil.getHashVoArrayByDS(null, "select id,code,name from pub_role where id in (select roleid from pub_user_role where userid='" + ClientEnvironment.getCurrLoginUserVO().getId() + "')");
			for (int i = 0; i < hvs_myAllroles.length; i++) { //遍历输出!
				sb_info.append((i + 1) + ".【" + hvs_myAllroles[i].getStringValue("id") + "/" + hvs_myAllroles[i].getStringValue("code") + "/" + hvs_myAllroles[i].getStringValue("name") + "】\r\n"); //
			}

			MessageBox.show(_tree, sb_info.toString()); //
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WLTTabbedPane getWorkTabbedPanel() {
		return workTabbedPanel;
	}

	//静态变量!!
	public static String getSelectedTabbedName() {
		DeskTopPanel deskTop = DeskTopPanel.deskTopPanel; //
		if (deskTop == null || deskTop.getWorkTabbedPanel() == null) {
			return null;
		}
		int li_pos = deskTop.getWorkTabbedPanel().getSelectedIndex(); //
		if (li_pos < 0) {
			return null;
		}
		if (li_pos == 0) {
			return "首页";
		}
		Object objs = deskTop.getWorkTabbedPanel().getComponentAt(li_pos); //
		if (!(objs instanceof WorkTabbedPanel)) {
			return null;
		}
		WorkTabbedPanel wpanel = (WorkTabbedPanel) objs; // 
		if (wpanel == null) {
			return null;
		}
		String str_title = wpanel.getTitle(); ////
		return str_title; //
	}

	public static String getRealSelectedTabbedName() {
		DeskTopPanel deskTop = DeskTopPanel.deskTopPanel; //
		if (deskTop == null || deskTop.getWorkTabbedPanel() == null) {
			return null;
		}
		return deskTop.realCallMenuName;
	}

	/**
	 * 关闭自己本身页签
	 */
	public void closeSelfTab() {
		int li_pos = workTabbedPanel.getSelectedIndex(); //
		if (workTabbedPanel.getComponentAt(li_pos) instanceof WorkTabbedPanel) { //滚轮中间会出发关闭事件，需要过滤掉首页。
			WorkTabbedPanel wpanel = (WorkTabbedPanel) workTabbedPanel.getComponentAt(li_pos); // 
			beforeClose(wpanel);
			map_openedWorkPanel.remove(wpanel.getWorkPanel().getMenuId());
			workTabbedPanel.remove(li_pos); //
			wpanel = null;
			System.gc(); //
			this.updateUI();
		}
	}

	/**
	 * 关闭自己本身页签
	 */
	public void closeSelfMaxFrame(String _menuid) {
		//		Object obj = map_openedWorkPanel.get(_menuid); //
		//		obj = null; //
		map_openedWorkPanel.remove(_menuid); //
		System.gc(); //
		this.updateUI();
		// System.out.println("关闭窗口"); //
	}

	/**
	 * 关闭其他页签
	 */
	private void closeOtherTab() {
		int li_count = this.workTabbedPanel.getTabCount(); // 取得总数
		int li_pos = workTabbedPanel.getSelectedIndex();
		for (int i = li_count - 1; i > li_pos; i--) {
			WorkTabbedPanel wpanel = (WorkTabbedPanel) workTabbedPanel.getComponentAt(i);
			beforeClose(wpanel);
			map_openedWorkPanel.remove(wpanel.getWorkPanel().getMenuId());
			workTabbedPanel.remove(i); // 先把他后面的关闭!!
			wpanel = null;
		}

		li_count = this.workTabbedPanel.getTabCount(); // 取得总数
		int li_left = li_count - 2;
		if (li_left > 0) {
			for (int i = 0; i < li_left; i++) {
				int li_del = workTabbedPanel.getTabCount() - 2;
				WorkTabbedPanel wpanel = (WorkTabbedPanel) workTabbedPanel.getComponentAt(li_del);
				beforeClose(wpanel);
				map_openedWorkPanel.remove(wpanel.getWorkPanel().getMenuId());
				workTabbedPanel.remove(li_del); //
				wpanel = null;
			}
		}
		System.gc(); //
		this.updateUI();
	}

	/**
	 * 关闭所有页签
	 */
	public void closeAllTab() {
		int li_count = this.workTabbedPanel.getTabCount(); // 取得总数
		for (int i = li_count - 1; i >= 0; i--) { // 从最后一个起到第一个,第一个不关闭!!
			if (workTabbedPanel.getComponentAt(i) instanceof WorkTabbedPanel) {
				WorkTabbedPanel wpanel = (WorkTabbedPanel) workTabbedPanel.getComponentAt(i);
				beforeClose(wpanel);
				map_openedWorkPanel.remove(wpanel.getWorkPanel().getMenuId()); //从哈希表中删除
				workTabbedPanel.remove(i);
				wpanel = null;
			}
		}
		System.gc(); //
		this.updateUI();
	}

	/*
	 * 关闭某个页面前,调用底层抽象类中的关闭前事件
	 */
	public void beforeClose(WorkTabbedPanel _tabPanel) {
		if (_tabPanel.getWorkPanel() != null) {
			_tabPanel.getWorkPanel().beforeDispose();
			System.gc();
		}
	}

	/**
	 * 刷新所有首页...
	 */
	public void refreshAllTaskGroup() {
		//if (!MessageBox.confirm(this, "您真的想刷新整个首页么?这可能会有点耗时!\r\n其实你可以点击每个区域中的刷新单独刷新各个区域!")) { //
		//return; //
		//}
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //

		//应该去调用indexPanel中的刷新所有组的方法!!
		//for (int i = 0; i < this.taskGroups.length; i++) {
		//taskGroups[i].onRefreshGroup(true); //刷新所有首页...
		//}

		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		this.updateUI(); //
		MessageBox.show(this, "刷新首页成功!"); //
	}

	private String getSecretString(File f) {
		StringBuffer sb = new StringBuffer();
		FileReader noteOpenReader = null;
		BufferedReader noteOpenBufferd = null;
		String noteString = null;
		try {
			noteOpenReader = new FileReader(f);
			noteOpenBufferd = new BufferedReader(noteOpenReader);
			while ((noteString = noteOpenBufferd.readLine()) != null) {
				sb.append(noteString);
			}
			return sb.toString();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (noteOpenReader != null)
					noteOpenReader.close();
				if (noteOpenBufferd != null)
					noteOpenBufferd.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	/***
	 * 根据登录时间算出星期几
	 * @param timeString
	 * @return
	 */
	private String getDayOfWeek(String timeString) {
		String day = "";
		String[] week = new String[] { "", "日", "一", "二", "三", "四", "五", "六" };
		try {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			calendar.setTime(sdf.parse(this.longinTime));
			return "星期" + week[calendar.get(Calendar.DAY_OF_WEEK)];
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return day;
	}

	/**
	 * 取得桌面风格
	 * @return
	 */
	private int getDesktopLayout() {
		String str_dbstyle = ClientEnvironment.getDeskTopStyle(); //数据库中存储的
		String[][] str_df = getDeskTopAndIndexStyleMartix(); //
		for (int i = 0; i < str_df.length; i++) {
			for (int j = 0; j < str_df[i].length; j++) {
				if (str_df[i][j].equals(str_dbstyle)) { //如果是
					return (i + 1); //返回行号!
				}
			}
		}
		return 1; //
	}

	/**
	 * 首页布局
	 * @return
	 */
	private int getIndexLayout() {
		String str_dbstyle = ClientEnvironment.getDeskTopStyle(); //数据库中存储的
		String[][] str_df = getDeskTopAndIndexStyleMartix(); //
		for (int i = 0; i < str_df.length; i++) {
			for (int j = 0; j < str_df[i].length; j++) {
				if (str_df[i][j].equals(str_dbstyle)) { //如果是
					return (j + 1); //返回行号!
				}
			}
		}
		return 1; //
	}

	private String getDeskTopAndIndexStyle(int _deskLayout, int _indexLayout) {
		String[][] str_df = getDeskTopAndIndexStyleMartix(); //
		try {
			return str_df[_deskLayout - 1][_indexLayout - 1]; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return str_df[0][0]; //
		}
	}

	/**
	 * 风格,整体有抽屉,树,按钮栏,首页又有四种风格,以后还可能扩展!
	 * 因为一开始数据库字段是char(1),为了不让以前的代码报错,只能搞了个数组,然后两种组合形成唯一值!
	 * 这个数组的第几行表示抽屉,树,按钮,第几列表示首页的第几种风格!!
	 */
	private String[][] getDeskTopAndIndexStyleMartix() {
		return new String[][] { //
		{ "A", "B", "C", "D" }, //
				{ "a", "b", "c", "d" }, //
				{ "1", "2", "3", "4" }, //char A1
				{ "H", "I", "G", "K" }, //
				{ "O", "P", "Q", "R" } }; //
	}

	public static DeskTopPanel getDeskTopPanel() {
		return deskTopPanel;
	}

	public JTextField getInformationLabel() {
		return informationLabel;
	}

	public DeskTopVO getDeskTopVO() {
		return deskTopVO;
	}

	private String getMenuTreeViewFieldName() {
		if (ClientEnvironment.getInstance().getDefaultLanguageType().equals(WLTConstants.ENGLISH)) {
			return "ename";
		} else {
			return "name";
		}
	}

	private Logger getLogger() {
		if (logger == null) {
			logger = WLTLogger.getLogger(DeskTopPanel.class); //
		}
		return logger; //
	}

	/**
	 * 
	 * @param _id
	 */
	public void closeFrame(String _id) {
		map_openedWorkPanel.remove(_id); //
	}

	/**
	 * 取得SysAppService 
	 * @return
	 */
	private SysAppServiceIfc getSysAppService() {
		try {
			return (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
		} catch (Exception ex) {
			ex.printStackTrace();
			return null; //
		}
	}

	private void removeAllClientTimer() {
		Vector v_list = ClientEnvironment.clientTimerMap; //
		for (int i = v_list.size() - 1; i >= 0; i--) {
			javax.swing.Timer timer = (javax.swing.Timer) v_list.get(i); //
			timer.stop(); //
			v_list.remove(timer); //
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize(); //
		getLogger().debug("JVM GC回收器成功回收了DeskTopPanel【" + this.getClass().getName() + "】(" + this.hashCode() + ")的资源..."); //
	}

	class TitleBtnPanel extends JPanel implements MouseListener, ActionListener {
		private static final long serialVersionUID = -2759066606880619403L;
		JButton btn = null; //

		public TitleBtnPanel(String _imgName, String _text, String _treePath, String _menuId) {
			this.setLayout(new BorderLayout()); //
			this.setOpaque(false); //透明!!
			ImageIcon icon = UIUtil.getImage(_imgName == null ? "32_32_01.gif" : _imgName); //如果为空,则使用【32_32_01.gif】
			btn = new WLTButton(icon); //
			btn.putClientProperty("menuid", _menuId); ////
			btn.setBackground(new Color(168, 194, 225)); //
			btn.setOpaque(false); //透明!!
			if (_treePath == null) {
				btn.setToolTipText(_text); //
			} else {
				_treePath = tbUtil.replaceAll(_treePath, " ", ""); //
				btn.setToolTipText(_treePath); //
			}
			btn.addMouseListener(this); //
			if (icon != null) {
				btn.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight())); ////
			} else {
				btn.setPreferredSize(new Dimension(32, 32)); //
			}
			btn.setBorder(BorderFactory.createEmptyBorder(1, 1, 2, 1)); //
			btn.addActionListener(this); //

			JLabel label_text = new JLabel(_text); //
			label_text.setForeground(new Color(51, 51, 51)); ////
			label_text.setOpaque(false); //透明!!
			label_text.setHorizontalAlignment(JLabel.CENTER); //

			this.add(btn, BorderLayout.CENTER); //
			this.add(label_text, BorderLayout.SOUTH); //
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
			btn.setOpaque(true); //不透明
			btn.setBorder(BorderFactory.createMatteBorder(1, 1, 2, 1, Color.GRAY)); //
		}

		public void mouseExited(MouseEvent e) {
			btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
			btn.setOpaque(false); //透明!
			btn.setBorder(BorderFactory.createEmptyBorder(1, 1, 2, 1)); //
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void actionPerformed(ActionEvent e) {
			JButton btn = (JButton) e.getSource(); ////
			String str_meniid = (String) btn.getClientProperty("menuid"); //
			DefaultMutableTreeNode node = findNodeByID(str_meniid); //
			HashVO hvo = getMenuVOFromNode(node); //
			openAppMainFrameWindow(hvo); //打开功能点!!!
		}

	}

	class MyImgPanelUI extends BasicPanelUI {
		ImageIcon icon = null; //

		public MyImgPanelUI(ImageIcon _icon) {
			icon = _icon; //
		}

		@Override
		public void paint(Graphics g, JComponent c) {
			super.paint(g, c);
			int li_com_width = (int) c.getSize().getWidth(); //
			int li_x = (li_com_width - icon.getIconWidth()) / 2; //
			if (li_x < 0) {
				li_x = 0;
			}
			Graphics2D g2 = (Graphics2D) g; //
			g2.drawImage(icon.getImage(), 0, 0, icon.getIconWidth(), icon.getIconHeight(), null); //
		}
	}

	class MyTreeCellRender extends DefaultTreeCellRenderer {

		private static final long serialVersionUID = 7173353751862932053L;
		TreePath path; //
		DefaultMutableTreeNode[] allSelectNodes = null;

		public MyTreeCellRender(TreePath path) {
			this.path = path; //
			if (path != null) {
				allSelectNodes = (DefaultMutableTreeNode[]) Arrays.asList(path.getPath()).toArray(new DefaultMutableTreeNode[0]);
			}
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			JLabel oldlabel = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			JLabel label = new JLabel(oldlabel.getText()); //
			label.setFont(oldlabel.getFont()); //
			label.setIcon(oldlabel.getIcon()); //           
			if (sel) { ////
				label.setOpaque(true); //
				label.setBackground(Color.YELLOW); //
				label.setForeground(Color.RED); //
			}

			if (value instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value; // 当前Node!
				boolean iffind = false;
				if (allSelectNodes != null) {
					for (int i = 0; i < allSelectNodes.length; i++) {
						if (allSelectNodes[i] == node) {
							iffind = true;
							break;
						}
					}
				}
				if (iffind) { // 如果找到!!
					label.setFont(new Font(label.getFont().getName(), Font.BOLD, 12 + LookAndFeel.getFONT_REVISE_SIZE())); //
					label.setForeground(Color.RED); //
				} else {
					label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 12 + LookAndFeel.getFONT_REVISE_SIZE())); //
					label.setForeground(LookAndFeel.appLabelNotSelectedFontcolor); //应该就是黑色!!
				}

				if (!node.isRoot()) {
					HashVO nodeVO = (HashVO) node.getUserObject();
					String str_icon = nodeVO.getStringValue("icon");
					String str_command = nodeVO.getStringValue("command");
					if (str_icon != null && !str_icon.trim().equals("")) {
						ImageIcon icon = UIUtil.getImage(str_icon);
						if (icon != null) {
							label.setIcon(icon); //
						}
					}
					if (!iffind && node.isLeaf()) {
						if (str_command == null || str_command.trim().equals("")) {
							label.setForeground(Color.GRAY); //
						}
					}
				}
			}

			return label;
		}
	}

	private void showHiddenMenuBar() {
		if ("3".equals(btn_hiddensplit.getClientProperty("deskTopLayout"))) { //
			if (buttonMenuBarPanel != null) {
				buttonMenuBarPanel.setVisible(!buttonMenuBarPanel.isVisible()); //
			}
		} else {
			if (splitLeftTopPanel.isVisible()) {
				splitPanel_2.setDividerLocation(0); //
				splitPanel_2.setDividerSize(0);
				splitLeftTopPanel.setVisible(false); //
				btn_hiddensplit.setIcon(icon_right);
			} else {
				splitLeftTopPanel.setVisible(true); //
				splitPanel_2.setDividerSize(4);
				splitPanel_2.setDividerLocation(MenuTreeWidth); //
				btn_hiddensplit.setIcon(icon_left);
			}
		}
	}

	public void setRealCallMenuName(String realCallMenuName) {
		this.realCallMenuName = realCallMenuName;
	}

	/***
	 * 系统版本号, 以weblight_xxx.jar文件日期为准!
	 * Gwang 2012-12-21
	 * @return
	 */
	private String getJarVersion(String fileName) {
		String ver = null;
		String jarPath = System.getProperty("APP_DEPLOYPATH") + "/WEB-INF/lib/weblight_" + fileName + ".jar";
		File file = new File(jarPath);
		if (file.exists()) {
			Date date = new Date(file.lastModified());
			SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
			ver = sf.format(date);
		}
		return ver;
	}
}