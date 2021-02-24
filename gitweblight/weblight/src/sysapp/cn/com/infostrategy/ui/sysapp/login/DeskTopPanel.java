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
 * ��¼������ϵͳ��ҳ��! ���������޸�!!!!!
 * @author xch
 * 
 */
public class DeskTopPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 2349708048405530570L;

	private String loginUserCode = null; // �û�����!!
	private String longinTime = null; // ��¼ʱ��
	private WLTButton btn_relogin, btn_exit; // ���µ�¼��ť!! �˳���ť!!

	private WLTPopupButton serverPopButton; // ��������ݵ�¼ʱ,���������ť,���д������Ź���!!�ǳ�����!רΪʵʩ��Աʹ��!
	private WLTButton btn_clientconsole, btn_serverconsole, btn_desktopstyle, btn_updatepwd, btn_userinfo, btn_zhuxiao; //
	private JButton btn_help; //
	private JMenuItem menuItem_clientenv, menuItem_serverenv, menuItem_serverconsole, menuItem_state, menuItem_serverlog, menuItem_serverscreen, menuItem_serverexplorer, menuItem_sqltool, menuItem_sqllistener, menuItem_sqlbuding; // �ͻ��˻�������,��������������,�鿴State,��������־,��������Ļ,SQL����,SQL�����.[2012-05-10�������state�鿴]

	private ImageIcon icon_up, icon_down, icon_left, icon_right;
	private JButton btn_hiddensplit, btn_hiddentitle, btn_sysregmenu; //
	private JMenuItem item_expand, item_collapse, item_editconfig, item_showreason; //
	private JMenuItem menuItem_1_closethis, menuItem_2_closeothers, menuItem_3_closeall, menuItem_hidetoolbar, menuItem_hideradiopanel, menuItem_hidesystabpanel, menuItem_config_desktop, menuItem_refresh_indexPage, menuItem_config_person, menuItem_config_right; //
	//private JMenuItem menuItem_1_closethis, menuItem_2_closeothers, menuItem_3_closeall; //
	private JMenuItem menuItem_hidemenubar; //�Ҽ��˵���������ʾ/���ز˵� Gwang 2012-11-29
	private JPopupMenu indexPopMenu; //
	private DeskTopVO deskTopVO = null;

	private JPanel toolBarPanel = null; //
	private JPanel buttonMenuBarPanel = null; //
	private String str_indexPageStyle = null; //��ҳĬ�Ϸ��
	private JPanel indexPageContainer = null; //
	private boolean isLoadedIndexPage = false; //�Ƿ��������ҳ?��Ϊ���Զ������ҳ��,�Լ�skip2ģʽ,����һ����ҳ�ʹ�����װ���������

	private DefaultMutableTreeNode allTreeRootNode = null; //�����!
	private ArrayList al_allLeafNodes = new ArrayList(); //����Ҷ�ӽ��!!!
	private ArrayList al_autostartNodes = new ArrayList(); //������Ҫ�������Ľ��!!

	private JPanel splitLeftTopPanel = null; //�ָ�����ߵ����
	private CardLayout cardLayout = null; //
	private WLTOutlookBar outlook = null; //��߳���˵���!!!
	private JTree sysRegMenuTree = null; //ϵͳע��˵�����

	private JPanel title_panel = null; //������ı���ͼƬ���!!!
	private JSplitPane splitPanel_2 = null; //��װ��߲˵������������м����ҳ�ķָ�������!
	private WLTTabbedPane workTabbedPanel = null; // �������!!

	private WLTRadioPane deskIndexRadioPanel = null; //
	private JPanel indexPanel = null; //��ҳ(��������ҳ����ҳ)
	private JPanel taskAndMsgCenterPanel = null; //�����������,������������!
	private JPanel indexShortCutPanel = null; //
	private JPanel bomPanel = null; //Ӧ����cn.com.infostrategy.ui.workflow.pbom.BillBomPanel,������һ���ͻᵼ���ڳ��ֵ�¼����ʱ��������BillBomPanel���,�Ҳ��ǲ����ڳ��ֵ�¼����ʱ������̫���,���Ծ�д��JPanel!��������ᵼ������,��������import��������,�ڷ����еĵ�û���õ�Ҳ���ᵼ������!

	private JLabel userNameLabel = null; //״̬��
	public JTextField informationLabel = null; //״̬���м���ı���
	private JLabel jvmInfoLabel = null; //״̬���ұߵ���ʾ������ڴ������label

	private int li_lastDiviLocatiob = 0; // ����λ��
	private HashMap map_openedWorkPanel = new HashMap(); // �洢�Ѵ򿪵Ĵ���

	public static DeskTopPanel deskTopPanel = null; ////
	private TBUtil tbUtil = new TBUtil(); //
	private static Logger logger = null; //
	private JTabbedPane gpstabpane = null;//����ж��gpsͼ���ö�ҳǩ��ʾ��

	String[] str_indexarray = new String[] { "������Ϣ", "��������", "��ݷ���", "������ͼ" }; //
	private int MenuTreeWidth = 150; //ϵͳ�˵������ �����/2012-11-22��
	private String realCallMenuName = null;//�ڴ�һ���˵���ʱ���remotecallcilent��Ļ�ȡ���ʵĲ˵�����
	private I_WLTTabbedPane newDeskTopTabPane; //�µ���ҳ�л�Ч�����
	private WLTTabbedPane newDeskTopTabPane_2; //������ҳǩ���л����
	private TBUtil tbutil = new TBUtil();
	private HashMap bomLabelShowToolTip = new HashMap();
	int index = 0;
	private JTextField searchField; //��ѯ���ܵ���ı� 

	private Timer openMenuThread = new Timer();

	private JLabel taskLabel, taskLabel_ = null;

	private JLabel selectedInfo = new JLabel();////�б�Ԫ��ѡ����Ϣ[YangQing/2013-09-25]

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
		deskTopPanel = this; //���徲̬����
		removeAllClientTimer(); //��ҳ������������(ͼƬ������),���µ�¼ʱ�������������!!! �ڴ�����Щtimerʱ���Ƕ���ClientEnvermene��������!!!
		initialize();
	}

	/**
	 * ����������!!!
	 */
	private JPanel tilePanel;
	private JPopupMenu tile_pop_menu = null;

	private void initialize() {
		MenuTreeWidth = tbUtil.getSysOptionIntegerValue("ϵͳ�˵������", 150); //ϵͳ�˵������ �����/2012-11-22��
		PopupFactory.setSharedInstance(new ScrollablePopupFactory()); //������̬Ч��!!!
		WLTLogger.config(System.getProperty("ClientCodeCache"), ClientEnvironment.getInstance().getLog4jConfigVO().getClient_level(), ClientEnvironment.getInstance().getLog4jConfigVO().getClient_outputtype()); // ����Log4J����
		WLTLogger.getLogger(LoginAppletLoader.class).info("�ɹ�����Log4j,level[" + ClientEnvironment.getInstance().getLog4jConfigVO().getClient_level() + "],outputtype[" + ClientEnvironment.getInstance().getLog4jConfigVO().getClient_outputtype() + "]");
		initAllTreeData(); //�ȹ��������!!!�ؼ��ĵ�һ��!!!

		//�������˵�!!!
		final int li_deskTopLayout = getDesktopLayout(); //���沼��!
		JPanel menu_type_5_panel = null;
		if (li_deskTopLayout == 1) { // ����Ƕ���뷽ʽ��¼ABCD
			HashVO[] firstLevelMenuVOs = getFirstLevelData(); //ȡ�õ�һ��Ĺ��ܵ�,��ΪҪ������������!!
			outlook = new WLTOutlookBar(); //�������!!
			outlook.setOpaque(false); //
			outlook.setFontAll(LookAndFeel.desktop_OutLookBarFont); //
			outlook.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); //5
			outlook.setCanMax(true); //
			for (int i = 0; i < firstLevelMenuVOs.length; i++) { //��������һ���˵�!!!
				JTree tree = getSecondTree(i); //
				JScrollPane scrollPanel = new JScrollPane(tree);
				scrollPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)); //
				String str_iconName = (firstLevelMenuVOs[i].getStringValue("ICON") == null ? "blank.gif" : firstLevelMenuVOs[i].getStringValue("ICON")); //
				outlook.addTab(firstLevelMenuVOs[i].getStringValue("NAME"), UIUtil.getImage(str_iconName), scrollPanel, "��ʾ"); //
			}
		} else if (li_deskTopLayout == 2) { //���������ͷ���,����ȥ����Bom��˵������ַ�ʽ!!!
			outlook = new WLTOutlookBar(); //�������!!
			outlook.setOpaque(false); //
			outlook.setFontAll(LookAndFeel.desktop_OutLookBarFont); //
			outlook.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0)); //5
			outlook.setCanMax(false); //
			JTree tree = getFirstTree(); //
			expandFirstLevel(tree, 8); //չ��һ�����!��Ϊ�е�����ΪȨ��ԭ��,ֻ�м������ܵ�,�����������һ�𲻺ÿ�,չ��һ��!!��xch/2012-03-06��
			JScrollPane scrollPanel = new JScrollPane(tree);
			scrollPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)); //
			String str_labeltext = tbUtil.getSysOptionStringValue("��ҳ�ҵ����й�������", "�ҵ����й���"); //
			outlook.addTab(str_labeltext, UIUtil.getImage("office_031.gif"), scrollPanel); //office_038.gif
			outlook.setSelectedIndex(0); //
		} else if (li_deskTopLayout == 3) { //����ǰ�ť��!�򲻴�������!!

		} else if (li_deskTopLayout == 5) {
			menu_type_5_panel = initDeskTopStyle5();
		}

		//�м�İ�����ҳ���ڵ��������!!!
		workTabbedPanel = new WLTTabbedPane(true);
		workTabbedPanel.setFocusable(false); //
		workTabbedPanel.setBorder(BorderFactory.createEmptyBorder()); //

		if (li_deskTopLayout != 3 && li_deskTopLayout != 4 && tbUtil.getSysOptionBooleanValue("��ҳ�Ƿ�֧�ֶ��ַ��", true)) {
			initHomePageRadioPanel();
		} else if (li_deskTopLayout != 3 && li_deskTopLayout != 4) { //���û�ж�ҳǩ!
			indexPageContainer = new JPanel(); //
			workTabbedPanel.addTab(UIUtil.getLoginLanguage("��������"), UIUtil.getImage("zt_037.gif"), new WLTPanel()); //ֱ�Ӽ�����ҳ!!
		}
		if (workTabbedPanel.getTabCount() > 0) {
			workTabbedPanel.setForegroundAt(0, Color.blue); //
		}

		//ѡ���˳�ʱ��ҳ��
		workTabbedPanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				JButton obj = (JButton) e.getSource(); //
				if (e.getButton() == MouseEvent.BUTTON3 || (e.isShiftDown() && e.getButton() == MouseEvent.BUTTON1)) { // ����Ҽ�,�����+Shift
					showWorkTabRightPopMenu(obj, e.getX(), e.getY()); //
				} else {
					if (e.getClickCount() == 2) { //˫��ҳǩ!!
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
								if (splitPanel_2 != null && splitLeftTopPanel != null && li_deskTopLayout != 3 && li_deskTopLayout != 4) { //��ť��������!!!
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
				if (e.getButton() == MouseEvent.BUTTON3) { // ����Ҽ�,�����+Shift
					showWorkTabRightPopMenu(obj, e.getX(), e.getY()); //
				} else if (e.getButton() == MouseEvent.BUTTON2) { //���������м�, ��ر��Լ� Gwang 2013/2/28
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
		workTabbedPanel.addChangeListener(new ChangeListener() { //ѡ��仯!!!
					public void stateChanged(ChangeEvent e) { //
						if (li_deskTopLayout != 3 && li_deskTopLayout != 4) { //ֻ�в��ǰ�ť���ż���
							lazyLoadIndexPage(true); //
						}
					}
				}); //

		//����������!!!
		JPanel middleContentPanel = new WLTPanel(WLTPanel.HORIZONTAL_FROM_MIDDLE, new BorderLayout(0, 0), LookAndFeel.defaultShadeColor1, false); //�ǽ���Ч��!!
		middleContentPanel.setBorder(BorderFactory.createEmptyBorder()); //

		//�ָ����е�����!
		if ("skip2".equals(System.getProperty("REQ_logintype"))) { //�����skip2��¼��ʽ,�����������ָ���!!!
			middleContentPanel.add(workTabbedPanel, BorderLayout.CENTER); //�˵���+��ҳ,���м�
		} else {
			splitLeftTopPanel = new JPanel(); //
			cardLayout = new CardLayout(); //
			splitLeftTopPanel.setLayout(cardLayout); //

			if (li_deskTopLayout == 1 || li_deskTopLayout == 2) {
				splitLeftTopPanel.add(outlook, "DBMenu"); //
			} else if (li_deskTopLayout == 5) {
				splitLeftTopPanel.add(menu_type_5_panel, "DBMenu");
			} else {
				splitLeftTopPanel.add(new JLabel("��ť�������ʾ��"), "DBMenu"); //
			}
			splitLeftTopPanel.putClientProperty("LoadMenuType", "DBMenu"); //

			splitPanel_2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, splitLeftTopPanel, workTabbedPanel); //����ǳ���,�ұ��Ƕ�ҳǩ!!
			splitPanel_2.setOpaque(false); //͸��!!!
			splitPanel_2.setBorder(BorderFactory.createEmptyBorder()); //
			splitPanel_2.setUI(new javax.swing.plaf.metal.MetalSplitPaneUI()); //
			splitPanel_2.setDividerLocation(MenuTreeWidth); //
			splitPanel_2.setDividerSize(4); // �ָ�����С
			middleContentPanel.add(splitPanel_2, BorderLayout.CENTER); //�˵���+��ҳ,���м�
		}

		JPanel menuBarPanel = new JPanel(new BorderLayout()); //
		menuBarPanel.setOpaque(false); //͸��!!
		boolean mustShowTools = tbutil.getSysOptionBooleanValue("��ҳ�Ƿ������ʾ������", false);
		if ((li_deskTopLayout == 1 || li_deskTopLayout == 2) && (ClientEnvironment.isAdmin() || mustShowTools)) {
			menuBarPanel.add(getToolBarPanel(), BorderLayout.CENTER); //������

			if (tbUtil.getSysOptionBooleanValue("��ҳ�Ƿ����ع�����", false)) { //�Ӹ����ع������Ĳ��� Gwang 2012-11-29
				getToolBarPanel().setVisible(false);
			}
		}
		//		if (li_deskTopLayout == 3) { //����ǵ�����,����ť�����
		//			menuBarPanel.add(getButtonMenuBarPanel(), BorderLayout.SOUTH); //
		//		}
		if (!"skip2".equals(System.getProperty("REQ_logintype"))) { //�����skip2��¼��ʽ,�����������ͼƬ!!!
			middleContentPanel.add(menuBarPanel, BorderLayout.NORTH); //������,������,getToolBarPanel()
		}
		middleContentPanel.add(getStatuPanel(), BorderLayout.SOUTH); // ״̬��,������

		//���Ľ���ϲ�!!!
		this.setLayout(new BorderLayout()); //
		if (!"skip2".equals(System.getProperty("REQ_logintype"))) { //�����skip2��¼��ʽ,�����������ͼƬ!!!
			if (!tbUtil.getSysOptionBooleanValue("��ҳ�Ƿ����ر���ͼƬ", false)) { //�Ӹ����ر���ͼƬ�Ĳ��� Gwang 2012-11-14
				this.add(getTitlePanel(), BorderLayout.NORTH); //����ͼƬ��!������				
			}
		}

		if (li_deskTopLayout != 3 && li_deskTopLayout != 4) {
			this.add(middleContentPanel, BorderLayout.CENTER); //�м������
		} else if (li_deskTopLayout == 3) {
			initNewDeskTopTabPane(); //��ť���
		} else if (li_deskTopLayout == 4) {
			initNewDeskTopTabPane2();
		}
		//������Զ���ĩ�Ĺ��ܵ�,���п���
		if (tbUtil.getSysOptionBooleanValue("��ҳ�Ƿ����ع��ܲ˵���", false)) { //����в���������Ĭ��������ߵĲ˵���,������֮!
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
		//��������...
		if ("skip2".equals(System.getProperty("REQ_logintype"))) { //�����skip2��¼��ʽ,���������ܵ�,������Ĭ�ϵ��Զ����ػ�����!
			String str_menuid = System.getProperty("REQ_menuid"); //Ҫ���ĸ��˵�!!
			openAppMainFrameWindowById(str_menuid); //
		} else { //�����������¼,���Զ��������ݿ��еĽ��!
			boolean isLoadCustPage = execAutoStartNode(); // �Զ��ҿ������Ľ��!!
			if (!isLoadCustPage) { //���û�м����Զ���ҳ��,�������ҳ!
				String str_ppp = tbUtil.getSysOptionStringValue("��¼ʱǿ������", null); //
				if (str_ppp == null) { //���ֻ��ʾ��ʾ����,����
					//����Ϊ����ߵ�¼����!!!��Ϊ��¼�������,Ȼ���ټ���һ����Ϣ����,�����ܲ���ʱ�ǳ���!![xch/2012-09-13]�����ȸ����ʾ��Ϣ,���ܴ�Ϊ���!
					//��ʵ�Ͼ�����һЩ��ʾ��ϢҪ����!!!
					if (li_deskTopLayout != 3 && li_deskTopLayout != 4) {
						lazyLoadIndexPage(false); //��װ����ҳ!!!
					}
				} else {

				}
			}
		}

		String timeouttime = new TBUtil().getSysOptionStringValue("��ʱʱ��", null);//��ʱ��֤ �ƹ����
		if (timeouttime != null && !timeouttime.trim().equals("")) {//�ܶ�����ͻ������˲�����ֵ����Ϊ�գ�������Ҫ�ж�һ�¡����/2012-10-30��
			new cn.com.infostrategy.ui.sysapp.other.TimeOutChecker(this, Integer.parseInt(timeouttime)).start();
		}
		//����ʵ��WLTJobIFC�ӿڼ��ɡ�[����2012-11-30]
		String classPath = tbUtil.getSysOptionStringValue("����ҳ�Զ�������", "");//��½ϵͳʱ����Ŀ���Լ������Լ�ʵ��һ������ʵ��Щ�߼�����������룬�״ε�½ϵͳ���׵���������ʾ��ϵͳ�Զ������ȵȡ�
		if (classPath != null && !classPath.equals("")) {
			Timer timer = new Timer();
			timer.schedule(new TimerTask() { //������һ���߳�
						public void run() { //��ҳ����Զ����õ��࣬Ŀǰ�����Զ�����������Ҫʵ�ֽӿ� WLTJobIFC
							String classPath = tbUtil.getSysOptionStringValue("����ҳ�Զ�������", "");
							try {
								Object job = Class.forName(classPath).newInstance();
								if (job instanceof WLTJobIFC) {
									((WLTJobIFC) job).run();
								}
							} catch (Exception e) {
								System.out.println("��ҳ����ʱ���ò���[����ҳ�Զ�������]=[" + classPath + "]���ִ���");
								e.printStackTrace();
							}
						}
					}, 5); //
		}

		//��ҳ�Ƿ���ʾ��̬���� �����/2013-06-05��
		Boolean remind = tbUtil.getSysOptionBooleanValue("��ҳ�Ƿ���ʾ��̬����", false);
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
				//��ǰ����Ϊ��������ҵ��λ�ڿ�д�ģ��������������Ŀ��ͻ�͸�ע�͵��ˣ����ڿ����Ӳ������ã�ʵ��Bom��ť����ʾ���ܡ����/2014-03-19��
				String templetcode = tbUtil.getSysOptionStringValue("BOM��ť��ʾģ��", "");
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

		ToolTipManager.sharedInstance().setDismissDelay(15000); //����tooltip��ʾʱ��Ϊ15��
	}

	/*
	 * �˵����5����ť���˵����ӽڵ�ƽ��չ����
	 */
	private JPanel initDeskTopStyle5() {
		//ƽ��
		HashVO[] firstLevelMenuVOs = getFirstLevelData(); //ȡ�õ�һ��Ĺ��ܵ�,��ΪҪ������������!!
		WLTPanel menu_type_5_panel = new WLTPanel(new BorderLayout());
		JPanel innserpanel = new WLTPanel(new BorderLayout());
		menu_type_5_panel.add(innserpanel);
		//0, i == 0 ? 0 : GridBagConstraints.RELATIVE, 0, 2, 1.0, 0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0
		for (int i = 0; i < firstLevelMenuVOs.length; i++) { //��������һ���˵�!!!
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
						Font big_font = new Font("����", Font.PLAIN, 13);
						float height = 0;
						for (Enumeration e = typenode.children(); e.hasMoreElements();) {
							DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // �ҵ��ö���!!
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
									if (childNodes[i].isLeaf()) { //ֻ��Ҷ�ӽ��ż���!!
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

	//��ť���ҳ����ء�
	ArrayList btnSytleConfig = new ArrayList();

	private void initHomePageRadioPanel() {
		//Ϊ������ҳ���ḻ,��ҳ��4�ַ��,���������ͻ�˵��Ҫ��ô��,ֻҪһ�ֹ�����Ϣ!�����ּ��˸��ܿ���!
		int li_indexLayout = getIndexLayout(); //��ҳ����!
		if (deskIndexRadioPanel != null)
			return;
		boolean[] bo_indexIsAdded = new boolean[] { false, false, false, false }; //
		deskIndexRadioPanel = new WLTRadioPane(LookAndFeel.systabbedPanelSelectedBackground); //
		String str_welcomeInfo = tbUtil.getSysOptionStringValue("��¼ʱǿ������", null); //
		if (str_welcomeInfo != null) { //
			deskIndexRadioPanel.setAutoSelectFirst(false); //
		}

		if (tbUtil.getSysOptionBooleanValue("��ҳ�Ƿ���ʾ������Ϣ", true)) {
			deskIndexRadioPanel.addTab(str_indexarray[0], str_indexarray[0], "site.gif", getWelComePanel(str_welcomeInfo)); //��ҳ���!!! �ؼ�UI,��ǰ��ҳЧ������,����������!!!!getAllTaskPanel()
			bo_indexIsAdded[0] = true; //
		}
		if (tbUtil.getSysOptionBooleanValue("��ҳ�Ƿ���ʾ��������", true)) {
			deskIndexRadioPanel.addTab(str_indexarray[1], str_indexarray[1], "zt_057.gif", getWelComePanel(str_welcomeInfo)); //
			bo_indexIsAdded[1] = true; //
		}
		if (tbUtil.getSysOptionBooleanValue("��ҳ�Ƿ���ʾ��ݷ���", true)) {
			deskIndexRadioPanel.addTab(str_indexarray[2], str_indexarray[2], "office_190.gif", getWelComePanel(str_welcomeInfo)); //
			bo_indexIsAdded[2] = true; //
		}
		if (tbUtil.getSysOptionBooleanValue("��ҳ�Ƿ���ʾ������ͼ", true)) {
			deskIndexRadioPanel.addTab(str_indexarray[3], str_indexarray[3], "office_200.gif", getWelComePanel(str_welcomeInfo)); //
			bo_indexIsAdded[3] = true; //
		}

		if (bo_indexIsAdded[li_indexLayout - 1]) { //�����!!
			str_indexPageStyle = str_indexarray[li_indexLayout - 1];
		} else {
			str_indexPageStyle = str_indexarray[0]; //
		}

		deskIndexRadioPanel.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				WLTRadioPane rp = (WLTRadioPane) e.getSource(); //
				onClickIndexRadioBtn(rp.getSelectKey()); //����	
				updateUserLoginStyle(); //�޸�״̬
			}
		});

		if (deskIndexRadioPanel.getButtons().length == 1) {
			deskIndexRadioPanel.setBtnPanelVisible(false);
		}

		workTabbedPanel.addTab(UIUtil.getLoginLanguage("��ҳ"), UIUtil.getImage("zt_037.gif"), deskIndexRadioPanel); //
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
					String rules_2[] = UIUtil.getStringArrayFirstColByDS(null, "select id from pub_role where code in ('һ��Ա��','һ����Ա','һ���û�','������Ա','��ͨԱ��','��ͨ��Ա','��ͨ�û�')");
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
				//û����ҳ��̬�л���ť���ñ�hm by 2013-5-17
			}
			if (vos.length > 0) {
				newDeskTopTabPane_2 = new WLTTabbedPane();
			}
			for (int j = 0; j < vos.length; j++) {
				String image = vos[j].getStringValue("image");
				String text = vos[j].getStringValue("text");
				String action = vos[j].getStringValue("action");
				btnSytleConfig.add(j, vos[j]); //����
				newDeskTopTabPane_2.addTab(text, new ImageIcon(tbutil.getImageScale(UIUtil.getImage(image).getImage(), 16, 16)), new JPanel());
			}
			if (vos.length == 0) {
				i_barMenuPanel = new I_BarMenuAndWorkPanel(null, null, allTreeRootNode);
				initHomePageRadioPanel();
				lazyLoadIndexPage(false); //��������
				i_barMenuPanel.getWorkTabbedPanel().addTab(UIUtil.getLoginLanguage("��ҳ"), UIUtil.getImage("zt_037.gif"), deskIndexRadioPanel); //
				WLTTabbedPane.CloseTabedPaneBtn btn = (CloseTabedPaneBtn) i_barMenuPanel.getWorkTabbedPanel().getJButtonAt(0);
				btn.setCanClose(false);
				this.add(getStatuPanel(), BorderLayout.SOUTH); //���״̬��
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
					onClickIndexITabedPane(rp.getSelectedIndex()); //����	
				}
			});
			if (splitPanel_2 != null) { //
				splitPanel_2.setDividerLocation(0); //
				splitPanel_2.setDividerSize(0);
			}
			if (splitLeftTopPanel != null) {
				splitLeftTopPanel.setVisible(false); //
			}
			this.add(getStatuPanel(), BorderLayout.SOUTH); //���״̬��
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
					String rules_2[] = UIUtil.getStringArrayFirstColByDS(null, "select id from pub_role where code in ('һ��Ա��','һ����Ա','һ���û�','������Ա','��ͨԱ��','��ͨ��Ա','��ͨ�û�')");
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
				//û����ҳ��̬�л���ť���ñ�hm by 2013-5-17
			}
			if (vos.length > 0) {
				newDeskTopTabPane = new I_WLTTabbedPane();
			}
			for (int j = 0; j < vos.length; j++) {
				String image = vos[j].getStringValue("image");
				String text = vos[j].getStringValue("text");
				String action = vos[j].getStringValue("action");
				btnSytleConfig.add(j, vos[j]); //����
				newDeskTopTabPane.addTab(text, new ImageIcon(tbutil.getImageScale(UIUtil.getImage(image).getImage(), 70, 70)), new JPanel());
			}
			if (vos.length == 0) {
				i_barMenuPanel = new I_BarMenuAndWorkPanel(null, null, allTreeRootNode);
				initHomePageRadioPanel();
				lazyLoadIndexPage(false); //��������
				i_barMenuPanel.getWorkTabbedPanel().addTab(UIUtil.getLoginLanguage("��ҳ"), UIUtil.getImage("zt_037.gif"), deskIndexRadioPanel); //
				WLTTabbedPane.CloseTabedPaneBtn btn = (CloseTabedPaneBtn) i_barMenuPanel.getWorkTabbedPanel().getJButtonAt(0);
				btn.setCanClose(false);
				this.add(getStatuPanel(), BorderLayout.SOUTH); //���״̬��
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
			searchField = new JTextField("���ٲ��ҹ���");
			final JPopupMenu pop = new JPopupMenu();
			searchField.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if ("���ٲ��ҹ���".equals(searchField.getText())) {
						searchField.setText(""); //�ÿ�
					}
				}
			});
			searchField.addFocusListener(new FocusAdapter() {
				public void focusLost(FocusEvent e) {
					if ("".equals(searchField.getText().trim())) {
						searchField.setText("���ٲ��ҹ���");
					}
				}
			});
			searchField.addKeyListener(new KeyAdapter() { //�������ı�����Ӽ����¼�
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
								if (name.contains(text.trim())) { //��������˹��ܵ㣬���뵽pop��
									JMenuItem item = new JMenuItem(name);
									item.setPreferredSize(new Dimension(140, 23));
									height += 23;
									item.addActionListener(new ActionListener() {
										public synchronized void actionPerformed(ActionEvent e) {
											JMenuItem item = (JMenuItem) e.getSource();
											searchField.setText(item.getText());
											HashVO vo = (HashVO) item.getClientProperty("menuvo");
											String menuID = vo.getStringValue("id"); //ȡ��ѡ���ID
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
								searchField.requestFocus(); //�ı����ٴλ�ȡ����,΢��ƴ��������
							}
						}
					});
			searchField.transferFocus(); //Ĭ���޽���
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
					onClickIndexITabedPane(rp.getSelectedIndex()); //����	
				}
			});
			if (splitPanel_2 != null) { //
				splitPanel_2.setDividerLocation(0); //
				splitPanel_2.setDividerSize(0);
			}
			if (splitLeftTopPanel != null) {
				splitLeftTopPanel.setVisible(false); //
			}
			this.add(getStatuPanel(), BorderLayout.SOUTH); //���״̬��
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}

	/**
	 * 
	* @Title: getNewDeskTopTabPane 
	* @Description: �¶�̬�л�Ч��TabPane
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
	 * @Description: ����ǵ�3�ֵ�½ģʽ��������չ���������
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
	 * ��ҳ���Ϸ���ť�л�
	 */
	HashMap haveLoad = new HashMap(); // ��ť�����ػ����ʾ

	private void onClickIndexITabedPane(int _index) {
		final int li_deskTopLayout = getDesktopLayout(); //���沼��!
		JComponent showjp = new JPanel();
		HashVO configVO = (HashVO) btnSytleConfig.get(_index);
		if (haveLoad.containsKey(_index)) {
			return;
		} else {
			try {
				String text = configVO.getStringValue("text");
				String action = configVO.getStringValue("action");
				if (action != null && "bomͼ".equals(configVO.getStringValue("loadtype"))) {//
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
				label.setText("û���ҵ���:" + configVO.getStringValue("action"));
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

	//��ť��񷵻���������
	public ArrayList getBtnStyleConfig() {
		return btnSytleConfig;
	}

	/**
	 * @see I_DTPTaskBtnAction �б�ǿ�Ʒ������.
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
		JTextArea textAra = new JTextArea(_msg == null ? "��Ҫ�������[��¼ʱǿ������]" : _msg); //
		textAra.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); //
		textAra.setOpaque(false); //
		textAra.setEditable(false); //
		panel.add(textAra); //
		return panel; //
	}

	/**
	 * ��װ����ҳ!!!
	 * @param _isSelectChange
	 */
	private void lazyLoadIndexPage(boolean _isSelectChange) {
		if (isLoadedIndexPage) { //����Ѽ�����ҳ,��ֱ�ӷ���!!!
			int li_index = workTabbedPanel.getSelectedIndex(); //ѡ���������!!!
			if (li_index == 0 && deskIndexRadioPanel != null) { //����л�������ҳ,��ԭ��ѡ�е����ǹ�����,���ٴ�ˢ�¹�����!
				if ("��������".equals(deskIndexRadioPanel.getSelectKey())) { //�������������,��Ҫ�ٴ�ˢ�¹�����,��Ϊ��ҵ�ͻ���Ӧ�ύ����������Ҫ
					reloadWorkFlowTask(); //
				}
			}
			return; //
		}

		if (_isSelectChange) { //�����ѡ��仯,���Ҳ��ǵ�һ��!
			int li_index = workTabbedPanel.getSelectedIndex(); //ѡ���������!!!
			if (li_index != 0) { //���������ҳ,��ֱ�ӷ���!
				return; //
			}
		}

		//�������ҳ,�����ǵ�һ�δ�!�򴴽���Ӧ��ҳ��,�����ĸ������ĸ�!!
		if (deskIndexRadioPanel != null) { //��ҳ֧�ֶ���!!��̬�жϴ�����һ��!!
			if (deskIndexRadioPanel.isSellectedOneItem()) { //����������!!
				onClickIndexRadioBtn(str_indexPageStyle); //ֻ���л�,���޸�pub_user.desktopStyle	
			}
		} else { //������Ƕ���,��ֱ�Ӵ�����Ϣ����!!!
			indexPageContainer.removeAll(); //
			//			indexPageContainer.setLayout(new BorderLayout()); //
			indexPageContainer.add(new IndexPanel(this)); //
		}
		isLoadedIndexPage = true; //
	}

	/**
	 * ��ʼ������������!!!�ؼ��ļ���!!
	 */
	private void initAllTreeData() {
		HashVO[] allMenuHVOs = getDeskTopVO().getMenuVOs(); // ȡ������!!
		for (int i = 0; i < allMenuHVOs.length; i++) {
			allMenuHVOs[i].setToStringFieldName(getMenuTreeViewFieldName()); //
		}
		HashVO rootVO = new HashVO(); // ////....
		rootVO.setAttributeValue("ID", "ROOT"); //
		rootVO.setAttributeValue("CODE", "ROOT"); //
		rootVO.setAttributeValue("NAME", "ROOT"); //
		allTreeRootNode = new DefaultMutableTreeNode(rootVO); // ���������
		DefaultMutableTreeNode[] node_level_1 = new DefaultMutableTreeNode[allMenuHVOs.length]; // �������н������

		for (int i = 0; i < allMenuHVOs.length; i++) {
			node_level_1[i] = new DefaultMutableTreeNode(allMenuHVOs[i]); // �����������
			allTreeRootNode.add(node_level_1[i]); // ��������
		}
		// ������..
		for (int i = 0; i < node_level_1.length; i++) {
			HashVO nodeVO = (HashVO) node_level_1[i].getUserObject();
			String str_pk_parentPK = nodeVO.getStringValue("parentmenuid"); // ��������
			for (int j = 0; j < node_level_1.length; j++) {
				HashVO nodeVO_2 = (HashVO) node_level_1[j].getUserObject();
				String str_pk_2 = nodeVO_2.getStringValue("id"); // ����
				if (str_pk_2.equals(str_pk_parentPK)) // ������ָý�������������ϲ�ѭ���ĸ��׽��,������Ϊ�ҵĶ��Ӵ������
				{
					try {
						node_level_1[j].add(node_level_1[i]);
						break; // �˳��ڲ�ѭ��,�������
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
		//������Ҫ�������Ľ��,�Լ�·��!!!
		DefaultMutableTreeNode[] allNodes = getAllNodes(); //
		for (int i = 0; i < allNodes.length; i++) {
			if (allNodes[i].isLeaf()) { //�����Ҷ�ӽ��!
				HashVO nodeVO = (HashVO) allNodes[i].getUserObject(); //�ý��󶨵�����!!!
				String str_pathName = ""; //
				TreeNode[] allparentNodes = allNodes[i].getPath(); // ȡ�ô�Root���������н��
				if (allparentNodes.length > 1) { //���и��׽��
					for (int j = 1; j < allparentNodes.length - 1; j++) { //
						HashVO parentHVO = (HashVO) ((DefaultMutableTreeNode) allparentNodes[j]).getUserObject(); //
						String str_parentName = parentHVO.getStringValue("name"); //
						str_pathName = str_pathName + str_parentName + " �� "; //
					}
				}
				str_pathName = str_pathName + nodeVO.getStringValue("name"); //
				nodeVO.setAttributeValue("$TreePath", str_pathName); //
				al_allLeafNodes.add(nodeVO); //��������!!!

				//��¼�Ƿ���Ҷ�ӽ��!!
				Boolean bo_isautostart = nodeVO.getBooleanValue("isautostart", false); // �Ƿ�������??
				if (bo_isautostart.booleanValue()) {
					al_autostartNodes.add(allNodes[i]); //
				}
			}
		}
	}

	//����ͼƬ���!!
	private JPanel getTitlePanel() {
		if (title_panel != null) {
			return title_panel;
		}
		String str_titlegifname = "titlecenter_default"; //
		if (System.getProperty("INNERSYSTEM") != null && !System.getProperty("INNERSYSTEM").equals("") && ClientEnvironment.chooseISys != null && !ClientEnvironment.chooseISys.equals("")) { //INNERSYSTEM
			str_titlegifname = "titlecenter_" + ClientEnvironment.chooseISys; //����ĳ����ϵͳ
		} else {
			if (System.getProperty("PROJECT_SHORTNAME") != null) { //����Ŀ����
				str_titlegifname = "titlecenter_" + System.getProperty("PROJECT_SHORTNAME"); // ʹ��shortname
			}
		}
		String str_realimgName = str_titlegifname + ".jpg"; //
		ImageIcon imgIcon = UIUtil.getImageFromServer("/applet/" + str_realimgName); //ͼƬ!!
		if (imgIcon == null || imgIcon.getImageLoadStatus() == java.awt.MediaTracker.ERRORED) { //jpgȡ����,��ȡgif
			str_realimgName = str_titlegifname + ".gif"; //
			imgIcon = UIUtil.getImageFromServer("/applet/" + str_realimgName); //ͼƬ
		}
		title_panel = new JPanel(new BorderLayout(0, 0)); //
		title_panel.setBackground(LookAndFeel.defaultShadeColor1); //
		title_panel.setToolTipText(str_realimgName + "[" + imgIcon.getIconWidth() + "*" + imgIcon.getIconHeight() + "]"); //
		title_panel.setUI(new MyImgPanelUI(imgIcon)); //
		JPanel panel_west = new JPanel(); //
		panel_west.setOpaque(false); //͸��!!

		panel_west.setPreferredSize(new Dimension(400, imgIcon.getIconHeight())); //ͼƬ�ĸ߶�!!!���400��������Զ������Logo�õ�,����ĸ���ݰ�ť!!
		title_panel.add(panel_west, BorderLayout.WEST); //���

		int li_text_align = FlowLayout.RIGHT; //��������������ϲ������,û�취ֻ���ָ������!
		String str_optiontext = tbUtil.getSysOptionStringValue("��ҳ�����п�ݰ�ť����λ��", "����"); //
		if (str_optiontext.equals("����")) {
			li_text_align = FlowLayout.LEFT; //
		} else if (str_optiontext.equals("����")) {
			li_text_align = FlowLayout.CENTER; //
		} else if (str_optiontext.equals("����")) {
			li_text_align = FlowLayout.RIGHT; //
		}

		JPanel panel_btns = new JPanel(new FlowLayout(li_text_align, 7, 0)); //
		int li_blank = (imgIcon.getIconHeight() - 32) / 2; //�������ʹͼƬ̫���������¶�������ȥ��,��Ҫ������ȥ�޸�ͼƬ��֤�߶�Ϊ55���Ҽ���!!!
		panel_btns.setBorder(BorderFactory.createEmptyBorder(li_blank, 0, 0, 10)); //��֤ͼƬ���м�!!!
		panel_btns.setOpaque(false); //͸��!!
		boolean isHaveTitleShortCut = false; ////
		if (this.deskTopVO != null) {
			HashVO[] menuVOs = this.deskTopVO.getMenuVOs(); //
			for (int i = 0; i < menuVOs.length; i++) { //
				if (menuVOs[i].getBooleanValue("showintoolbar", false)) { //�Ƿ��ڲ˵�����
					isHaveTitleShortCut = true; //
					TitleBtnPanel btnPanel = new TitleBtnPanel(menuVOs[i].getStringValue("toolbarimg"), menuVOs[i].getStringValue("name"), menuVOs[i].getStringValue("$TreePath"), menuVOs[i].getStringValue("id")); //
					panel_btns.add(btnPanel); //
				}
			}
		}
		if (isHaveTitleShortCut) { //�������˲ż������,���û���򲻼�,ʹUI���Ƹ���ˬЩ!!
			JPanel scrollPanel_tbtn = new WLTScrollPanel(panel_btns); //
			title_panel.add(scrollPanel_tbtn, BorderLayout.CENTER); //
		}
		title_panel.setPreferredSize(new Dimension(1024, imgIcon.getIconHeight())); //ǿ��ָ�����߶�!! �����Ͳ���ð��һ����ɫ�������!!! ���ͼƬ��С������ֻ֧ʹ��ݰ�ťͼ��㵽������ȥ
		return title_panel;
	}

	/**
	 * �����������,�������������ݰ�ť,�м��ǻ�ӭ��Ϣ,�ұ��ǹ��߰�ť����һ��!!! �ǳ��ؼ��Ķ���!!!
	 * @return
	 */
	private JPanel getToolBarPanel() {
		if (toolBarPanel != null) {
			return toolBarPanel; //
		}
		toolBarPanel = new JPanel(new BorderLayout(0, 0)); //
		toolBarPanel.setOpaque(false); //͸��!!!
		toolBarPanel.setBorder(BorderFactory.createEmptyBorder(3, 0, 1, 1)); //һ��Ҫ����հ�,���򼷵û�!!!

		//������ߵ������������صİ�ť!!!
		int li_deskTopLayout = getDesktopLayout(); //
		JPanel left_btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1)); // //
		left_btnPanel.setOpaque(false); //͸��!!
		icon_up = UIUtil.getImage("office_136.gif");
		icon_right = new ImageIcon(tbUtil.getImageRotate(icon_up.getImage(), 90)); //90��!
		icon_down = new ImageIcon(tbUtil.getImageRotate(icon_up.getImage(), 180)); //180��!
		icon_left = new ImageIcon(tbUtil.getImageRotate(icon_up.getImage(), 270)); //270��!

		//�Ӹ����ر���ͼƬ�Ĳ��� Gwang 2012-11-14
		if (!tbUtil.getSysOptionBooleanValue("��ҳ�Ƿ����ر���ͼƬ", false)) {
			btn_hiddentitle = new JButton(icon_up); //
			btn_hiddentitle.setPreferredSize(new Dimension(18, 18)); //
			btn_hiddentitle.setToolTipText("����/��ʾ����ͼƬ"); //
			btn_hiddentitle.addActionListener(this);
			left_btnPanel.add(btn_hiddentitle); //			
		}

		ImageIcon img048 = UIUtil.getImage("office_048.gif"); //
		if (li_deskTopLayout == 3 && img048 != null) { //��ť�����
			img048 = new ImageIcon(tbUtil.getImageRotate(img048.getImage(), 90)); //��ת90��
		}
		btn_hiddensplit = new JButton(icon_left); //
		btn_hiddensplit.setPreferredSize(new Dimension(18, 18)); //
		btn_hiddensplit.setToolTipText("����/��ʾ�˵�"); //
		btn_hiddensplit.addActionListener(this);
		if (li_deskTopLayout == 3) { //��ť�����ʱ����ʾ�����ť!!!
			btn_hiddensplit.putClientProperty("deskTopLayout", "3"); //
		}
		left_btnPanel.add(btn_hiddensplit); //
		if (ClientEnvironment.isAdmin()) { //����ǹ���Ա,����Բ鿴ע�Ṧ��!!!
			btn_sysregmenu = new JButton(UIUtil.getImage("office_042.gif")); //
			btn_sysregmenu.setPreferredSize(new Dimension(18, 18)); //
			btn_sysregmenu.setToolTipText("���ű�׼��Ʒ"); //
			btn_sysregmenu.addActionListener(this);
			left_btnPanel.add(btn_sysregmenu); //
		}
		if (li_deskTopLayout != 3) {
			toolBarPanel.add(left_btnPanel, BorderLayout.WEST); // Ӧ�ð�ť
		}

		//�����м��[����,��ӭ����...]������!!!	
		String dayOfWeek = this.getDayOfWeek(this.longinTime);
		JLabel label = null;
		String showuser = tbUtil.getSysOptionStringValue("�Ƿ���ʾ��½��", "0");//�Ƿ���ʾ��½��,Ĭ��0Ϊ��¼��+������1Ϊֻ��ʾ��Ա����
		if ("1".equals(showuser)) {
			label = new JLabel(" ����," + ClientEnvironment.getInstance().getLoginUserName() + "   ��¼ʱ��:" + this.longinTime + "," + dayOfWeek + "  ", JLabel.LEFT); //
		} else {
			label = new JLabel(" ����," + loginUserCode + "/" + ClientEnvironment.getInstance().getLoginUserName() + "   ��¼ʱ��:" + this.longinTime + "," + dayOfWeek + "  ", JLabel.LEFT); //
		}
		int li_text_align = SwingConstants.LEFT; //��������������ϲ���ֿ���,û�취ֻ���ָ������!
		String str_optiontext = tbUtil.getSysOptionStringValue("��ҳ��ӭ��ʾ����λ��", "����"); //
		if (str_optiontext.equals("����")) {
			li_text_align = SwingConstants.LEFT; //
		} else if (str_optiontext.equals("����")) {
			li_text_align = SwingConstants.CENTER; //
		} else if (str_optiontext.equals("����")) {
			li_text_align = SwingConstants.RIGHT; //
		}
		label.setHorizontalAlignment(li_text_align); //�ֿ���!�����������������ϲ���ֿ���!
		if (li_deskTopLayout != 3) {
			toolBarPanel.add(label, BorderLayout.CENTER); //������ʾ˵��
		}

		//�����ұߵ����й��ܰ�ť,����[�鿴�������˱���][SQL��ѯ������]!!
		JPopupMenu popup = new JPopupMenu(); //
		menuItem_clientenv = new JMenuItem(UIUtil.getLanguage("�鿴�ͻ��˻�������")); //
		menuItem_serverenv = new JMenuItem(UIUtil.getLanguage("�鿴��������������")); //
		menuItem_serverconsole = new JMenuItem(UIUtil.getLanguage("�鿴����������̨")); //
		menuItem_state = new JMenuItem(UIUtil.getLanguage("�鿴������State")); //
		menuItem_serverlog = new JMenuItem(UIUtil.getLanguage("�鿴��������־")); //
		menuItem_serverscreen = new JMenuItem(UIUtil.getLanguage("ץȡ��������Ļ")); //
		menuItem_serverexplorer = new JMenuItem(UIUtil.getLanguage("�鿴�������ļ�ϵͳ")); //
		menuItem_sqltool = new JMenuItem(UIUtil.getLanguage("SQL��ѯ������")); //
		menuItem_sqllistener = new JMenuItem(UIUtil.getLanguage("����SQLִ��")); //
		menuItem_sqlbuding = new JMenuItem(UIUtil.getLanguage("SQL��������")); //

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

		JMenu menuItem_server = new JMenu("����������ع���"); // ������һ���˵�.
		menuItem_server.setPreferredSize(new Dimension((int) menuItem_server.getPreferredSize().getWidth(), 19));
		menuItem_server.add(menuItem_serverenv); //
		menuItem_server.add(menuItem_serverconsole); //
		menuItem_server.add(menuItem_state); //
		menuItem_server.add(menuItem_serverlog); //
		menuItem_server.add(menuItem_serverscreen); //
		menuItem_server.add(menuItem_serverexplorer); //

		JMenu menuItem_sql = new JMenu("SQL��ع���"); // SQL��ع���
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
		serverPopButton.getButton().setToolTipText(UIUtil.getLanguage("�鿴��������������")); //
		serverPopButton.getButton().addActionListener(this);

		// ������Щ��ť�����ǹ����߻�����ͨ�û�������		
		btn_clientconsole = new WLTButton(UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("��ҳ�ͻ��˿���̨ͼƬ����", "office_197.gif"))); // �ͻ��˿���̨
		btn_serverconsole = new WLTButton(UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("��ҳ�������˿���̨ͼƬ����", "office_162.gif"))); // office_162.gif�������˿���̨

		String str_switchStyleImgName = TBUtil.getTBUtil().getSysOptionStringValue("��ҳ�л����ͼƬ����", "office_191.gif"); //
		if (new TBUtil().getSysOptionBooleanValue("��ҳˢ�°�ť�Ƿ���ʾ����", false)) {
			btn_desktopstyle = new WLTButton("ˢ��", UIUtil.getImage(str_switchStyleImgName)); //׷��"ˢ��" �����/2012-09-07��
		} else {
			btn_desktopstyle = new WLTButton(UIUtil.getImage(str_switchStyleImgName)); // �޸Ľ����� 
		}
		btn_updatepwd = new WLTButton("�޸�����", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("��ҳ�޸�����ͼƬ����", "office_045.gif"))); // �޸�����office_045
		btn_userinfo = new WLTButton("�޸ĸ�����Ϣ", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("��ҳ�޸ĸ�����ϢͼƬ����", "office_021.gif"))); //�޸ĸ�����Ϣ �����/2012-08-29��  Ԭ���� 20120909����޸ĸ�������
		btn_relogin = new WLTButton("�л��û�", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("��ҳ�л��û�ͼƬ����", "office_028.gif"))); // ���µ�¼zt_zhuxiao.gif//sunfujun/20120820/�ʴ�����
		btn_zhuxiao = new WLTButton("ע��", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("��ҳע���ťͼƬ����", "office_136.gif"))); //ע�� �����/2012-08-30��
		btn_help = new JButton("����", UIUtil.getImage("office_037.gif")); // �����ĵ�zt_bangzhu.gif
		btn_exit = new WLTButton("�˳�", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("��ҳ�˳�ϵͳͼƬ����", "office_008.gif"))); // �˳�ϵͳoffice_008.gif

		btn_clientconsole.setRegCodeStr("��ҳ�ͻ��˿���̨ͼƬ����"); //
		btn_serverconsole.setRegCodeStr("��ҳ�������˿���̨ͼƬ����"); //
		btn_desktopstyle.setRegCodeStr("��ҳ�л����ͼƬ����"); //
		btn_updatepwd.setRegCodeStr("��ҳ�޸�����ͼƬ����"); //
		btn_userinfo.setRegCodeStr("��ҳ�޸ĸ�����ϢͼƬ����"); //
		btn_relogin.setRegCodeStr("��ҳ�л��û�ͼƬ����"); //
		btn_zhuxiao.setRegCodeStr("��ҳע���ťͼƬ����"); //
		btn_exit.setRegCodeStr("��ҳ�˳�ϵͳͼƬ����"); //

		btn_clientconsole.setOpaque(false); //͸��!
		btn_serverconsole.setOpaque(false); //͸��!
		btn_desktopstyle.setOpaque(false); //͸��!
		btn_updatepwd.setOpaque(false); //͸��!
		btn_userinfo.setOpaque(false); //͸��!
		btn_relogin.setOpaque(false); //͸��!
		btn_zhuxiao.setOpaque(false); //͸��!
		btn_help.setOpaque(false); //͸��!
		btn_exit.setOpaque(false); //͸��!

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

		if (new TBUtil().getSysOptionBooleanValue("��ҳˢ�°�ť�Ƿ���ʾ����", false)) {
			btn_desktopstyle.setPreferredSize(new Dimension(45, 18)); //׷��"ˢ��" �����/2012-09-07��
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

		btn_clientconsole.setToolTipText("�鿴�ͻ��˿���̨"); //
		btn_serverconsole.setToolTipText("�鿴�������˿���̨"); //
		btn_desktopstyle.setToolTipText(UIUtil.getLoginLanguage("ˢ��/�л��û�Ĭ�ϻ���")); //
		btn_updatepwd.setToolTipText(UIUtil.getLoginLanguage("�޸�����")); //
		btn_userinfo.setToolTipText(UIUtil.getLoginLanguage("�޸ĸ�����Ϣ"));
		btn_relogin.setToolTipText(UIUtil.getLoginLanguage("���µ�¼")); //
		btn_zhuxiao.setToolTipText(UIUtil.getLoginLanguage("ע��"));
		btn_help.setToolTipText(UIUtil.getLoginLanguage("�����ĵ�(����Ҽ�ֱ���ϴ�)")); //
		btn_exit.setToolTipText(UIUtil.getLoginLanguage("�˳�ϵͳ")); //

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
				if (e.getButton() == MouseEvent.BUTTON3) { //���������Ҽ�,��ֱ���ϴ������ļ�!
					onUploadHelp(); //�ϴ������ļ�
				}
			}
		}); //

		//����ǹ���Ա��Ҫ����ϵͳ��ť
		JPanel right_btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0)); //
		right_btnPanel.setOpaque(false); //͸��!!
		boolean showRelogin = tbUtil.getSysOptionBooleanValue("�Ƿ����ע��", true);
		boolean showUserInfo = tbUtil.getSysOptionBooleanValue("�Ƿ�����޸ĸ�����Ϣ", false);

		//��ҳ׷��������̬���� �����/2013-05-24��
		Boolean task = tbUtil.getSysOptionBooleanValue("��ҳ�Ƿ���ʾ��̬����", false);
		if (task) {
			//��һ��һ����taskLabel ��������ʾ ���������ȥ���ֲ���Ч�� 
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
			right_btnPanel.add(btn_updatepwd); // ���޸�����ȥ��ô?

			if (showUserInfo) {
				right_btnPanel.add(btn_userinfo);
			}
			right_btnPanel.add(btn_help); //
			if (showRelogin) {
				right_btnPanel.add(btn_relogin); //
			}
			if (new TBUtil().getSysOptionBooleanValue("��ҳ�Ƿ���ʾע��", false)) { //ע�� �����/2012-08-30��
				right_btnPanel.add(btn_zhuxiao);
			}
			right_btnPanel.add(btn_exit); //
		} else { // ������ǹ���Ա,������ͨ�û���¼!!
			boolean isshowconsole = new TBUtil().getSysOptionBooleanValue("��ҳ�Ƿ���ʾ����̨", true);
			if (isshowconsole) {
				right_btnPanel.add(btn_clientconsole);
				right_btnPanel.add(btn_serverconsole);
			}
			right_btnPanel.add(btn_desktopstyle); //
			boolean isshowchangepwd = new TBUtil().getSysOptionBooleanValue("��ҳ�Ƿ����޸�����", true);
			if (isshowchangepwd) {
				right_btnPanel.add(btn_updatepwd); // ���޸����밴ťȥ��ô?
			}
			if (showUserInfo)
				right_btnPanel.add(btn_userinfo);
			right_btnPanel.add(btn_help); //
			if (showRelogin) {
				right_btnPanel.add(btn_relogin); //
			}
			if (new TBUtil().getSysOptionBooleanValue("��ҳ�Ƿ���ʾע��", false)) { //ע�� �����/2012-08-30��
				right_btnPanel.add(btn_zhuxiao);
			}
			right_btnPanel.add(btn_exit); //
		}
		toolBarPanel.add(right_btnPanel, BorderLayout.EAST); //���ұ߼���ϵͳ��ť!!
		return toolBarPanel;
	}

	/**
	 * �ݹ鴴�������˵�!!
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
			if (!childNode.isLeaf()) { //�����Ŀ¼���
				JMenu itemMenu = new JMenu(str_name); //
				itemMenu.setUI(new WLTMenuUI()); //
				itemMenu.setOpaque(true); //
				itemMenu.setBackground(LookAndFeel.defaultShadeColor1);
				int str_width = tbutil.getStrWidth(str_name); //�õ��ֿ��
				if (str_width > 90) {
					itemMenu.setPreferredSize(new Dimension(str_width + 45, 23)); //	
				} else {
					itemMenu.setPreferredSize(new Dimension(120, 23)); //
				}

				if (str_icon == null || str_icon.trim().equals("")) {
					itemMenu.setIcon(UIUtil.getImage("office_151.gif")); //Ҳ���������ͼ��!javax.swing.plaf.metal.MetalIconFactory.getTreeFolderIcon()
				} else {
					itemMenu.setIcon(UIUtil.getImage(str_icon)); //
				}
				if (_menu == null) { //����ǵ�һ��!
					_popMenu.add(itemMenu); //
				} else {
					_menu.add(itemMenu); //
				}
				createPopMenu(childNode, _popMenu, itemMenu); //�ݹ����!!
			} else { //�����Ҷ�ӽ��,�������ٵݹ������!!!
				JMenuItem itemMenu = new JMenuItem(str_name); //
				itemMenu.setUI(new WLTMenuItemUI()); //
				itemMenu.setBackground(LookAndFeel.defaultShadeColor1);
				int str_width = tbutil.getStrWidth(str_name); //�õ��ֿ��
				if (str_width > 90) {
					itemMenu.setPreferredSize(new Dimension(str_width + 45, 23)); //	
				} else {
					itemMenu.setPreferredSize(new Dimension(120, 23)); //
				}
				if (str_icon == null || str_icon.trim().equals("")) {
					itemMenu.setIcon(UIUtil.getImage("blank.gif")); //Ĭ����ͼ��!
				} else {
					itemMenu.setIcon(UIUtil.getImage(str_icon)); //
				}
				itemMenu.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						openAppMainFrameWindowById(str_menuid); //
					}
				}); //
				if (_menu == null) { //����ǵ�һ��!
					_popMenu.add(itemMenu); //
				} else {
					_menu.add(itemMenu); //
				}
			}
		}
	}

	/**
	 * �������״̬��
	 * 
	 * @return
	 */
	private JPanel getStatuPanel() {
		JPanel statuPanel = new JPanel();
		statuPanel.setOpaque(false); //͸��
		statuPanel.setLayout(new BorderLayout()); //

		//		String str_user = UIUtil.getLoginLanguage("��¼�û�:") + ClientEnvironment.getInstance().getLoginUserName();
		String str_user = ClientEnvironment.getInstance().getLoginUserName();
		if (ClientEnvironment.getInstance().isAdmin()) {
			str_user = str_user + " �� "; //
			userNameLabel = new JLabel(str_user, UIUtil.getImage("zt_061.gif"), SwingConstants.LEFT);
			userNameLabel.setForeground(Color.BLUE); //
		} else {
			str_user = str_user + " "; //
			userNameLabel = new JLabel(str_user, UIUtil.getImage("zt_061.gif"), SwingConstants.LEFT);
		}
		userNameLabel.putClientProperty("POPMENUTYPE", ScrollablePopupFactory.POP_TYPE_VERTICAL);
		JLabel logoutlabel = new WLTHrefLabel("ע��");
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
					workTabbedPanel.setBtnPanelVisible(true); //��һ������,����������ʾ����!										
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

		//ϵͳ�汾��, ��weblight_��Ŀ����.jar�ļ�����Ϊ׼!		
		String verInfo = getJarVersion("weblight_" + System.getProperty("PROJECT_SHORTNAME") + ".jar");
		if (verInfo == null) { //����ʱ���÷���������ʱ��
			verInfo = sb_currversion.toString();
		}
		String SUPERVISE = System.getProperty("SUPERVISE");
		StringBuffer infoSB = new StringBuffer();
		if (SUPERVISE != null && !SUPERVISE.equals("")) {
			infoSB.append(SUPERVISE);
		}
		infoSB.append(" ��Ȩ����:");
		infoSB.append(System.getProperty("LICENSEDTO"));
		if (!"N".equals(System.getProperty("SHOWSYSVER"))) {
			infoSB.append(" ϵͳ�汾[ " + verInfo + " ]");
		}
		informationLabel = new JTextField(infoSB.toString(), 1024);

		informationLabel.setEditable(false);
		informationLabel.setOpaque(false); //͸��!!!
		informationLabel.setBackground(LookAndFeel.systembgcolor);
		informationLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.isShiftDown() && e.getClickCount() == 2) { //����ǰ�סShift˫����!
					int li_result = JOptionPane.showConfirmDialog(DeskTopPanel.this, "�Ƿ������������ĵ�ǰ״̬��:��" + ClientEnvironment.isOutPutCallObjToFile + "��!\r\n������ǡ�������֮,������񡿽�����֮,�����ȡ������������!\r\n���ú���Ŀ¼[C:\\156]�������ļ�!", "��ʾ", JOptionPane.YES_NO_CANCEL_OPTION); ////
					if (li_result == JOptionPane.YES_OPTION) {
						ClientEnvironment.isOutPutCallObjToFile = true;
						JOptionPane.showMessageDialog(DeskTopPanel.this, "���������������ɹ�!!\r\n��������,ÿ��Զ������Ĳ��������洢��[C:\\156]Ŀ¼��!"); //
					} else if (li_result == JOptionPane.NO_OPTION) {
						ClientEnvironment.isOutPutCallObjToFile = false;
						JOptionPane.showMessageDialog(DeskTopPanel.this, "���������������ɹ�!!\r\n��������,����������ٽ��д洢��!"); //
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
		jvmInfoLabel.setToolTipText("<html>" + sb_currversion.toString() + "<br>�Ҽ��鿴ϵͳ����<br>�鿴����̨[Ctrl+˫��]<br>GC[Shift+˫��]</html>"); //
		jvmInfoLabel.setFont(new Font("System", Font.PLAIN, 12));
		jvmInfoLabel.setOpaque(false); //͸��
		jvmInfoLabel.setBackground(LookAndFeel.systembgcolor);

		jvmInfoLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) { //������Ҽ�,����ʾ����ϵͳ����,��Ϊ���������ͻ�Ҫ��ȥ��ϵͳ�˵�(�����ܲ鿴ϵͳ����,Դ��ʲô����),������Adminģʽ��¼������¾�û���鿴ϵͳ����(��Ϊ���ϽǵĲ鿴�ͻ��˻���������ťҲû��),��������������,������ʱ�鿴ϵͳ����!
					Properties sysProps = System.getProperties(); //
					String[] str_keys = (String[]) sysProps.keySet().toArray(new String[0]); //
					Arrays.sort(str_keys); //����һ��!
					StringBuilder sb_text = new StringBuilder("System.getProperties()��������:\r\n"); //
					for (int i = 0; i < str_keys.length; i++) {
						sb_text.append("[" + str_keys[i] + "]=[" + sysProps.getProperty(str_keys[i]) + "]\r\n"); //
					}
					MessageBox.showTextArea(DeskTopPanel.this, sb_text.toString()); //
				} else if (e.getClickCount() == 2) {
					if (e.isControlDown()) {
						invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.ClientConsoleMsgFrame", deskTopPanel, null, null); //��ʾ�ͻ��˿���̨,��Ϊ�ڵ����¼ʱ,����û�а�ť����˵�,û�ط�������̨��!
					} else { //����ǰ�סShift����
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
		selectedInfo = new JLabel();//��Ԫ��ѡ����Ϣ[YangQing/2013-09-25]
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

	//���״̬��������Ϣʱ�����Ĺ�����
	private void popToolPanel() {
		if (pop == null) {
			tool_menuitem_control = new JMenu("ϵͳ��־");
			tool_menuitem_control.setIcon(UIUtil.getImage("office_194.gif"));
			tool_menuitem_control_client = new JMenuItem("�ͻ�����־", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("��ҳ�ͻ��˿���̨ͼƬ����", "office_197.gif")));
			tool_menuitem_control_server = new JMenuItem("����������־", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("��ҳ�������˿���̨ͼƬ����", "office_162.gif")));
			tool_menuitem_pwd = new JMenuItem("�޸�����", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("��ҳ�޸�����ͼƬ����", "office_045.gif"))); // �޸�����office_045
			tool_menuitem_otheruser = new JMenuItem("�л����", UIUtil.getImage(TBUtil.getTBUtil().getSysOptionStringValue("��ҳ�л��û�ͼƬ����", "office_028.gif"))); // ���µ�¼zt_zhuxiao.gif//sunfujun/20120820/�ʴ�����
			tool_menuitem_help = new JMenuItem("����", UIUtil.getImage("office_037.gif")); // �����ĵ�zt_bangzhu.gif
			tool_menuitem_showsession = new JMenuItem("������Ϣ", UIUtil.getImage("office_021.gif"));
			if (LookAndFeel.getFONT_REVISE_SIZE() == 0) {
				tool_font_revise = new JMenuItem("����Ŵ�", UIUtil.getImage("zoomin.gif")); // ����Ŵ���С
			} else {
				tool_font_revise = new JMenuItem("����ָ�", UIUtil.getImage("zoomout.gif")); // ����Ŵ���С
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

	//˫���˵����!!
	private void onClickTree(JTree _tree) {
		if (_tree.getSelectionPath() == null) {
			return;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) _tree.getSelectionPath().getLastPathComponent();
		if (node.isLeaf()) { //ֻ��Ҷ�ӽ���������!
			HashVO hvo = getMenuVOFromNode(node); //
			openAppMainFrameWindow(hvo); //ִ���ҿ����ܵ��߼�!!!
		}
	}

	//���ϵͳע�Ṧ��
	private void onClickSysRegMenuTree() {
		try {
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			if (sysRegMenuTree.getSelectionPath() == null) {
				return;
			}
			DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) sysRegMenuTree.getSelectionPath().getLastPathComponent();
			if (selNode.isLeaf()) { //ֻ��Ҷ�ӽ���������!
				HashVO hvo = (HashVO) selNode.getUserObject(); //
				this.realCallMenuName = "��ǰλ�ã�" + hvo.getStringValue("$TreePath");
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
					MessageBox.show(this, "ѡ�еĹ��ܵ��·��Ϊ��!"); //
					return; //
				}
				if (map_openedWorkPanel.containsKey(str_menuname)) { // ����Ѵ򿪹�.
					Object obj = map_openedWorkPanel.get(str_menuname); //
					WorkTabbedPanel tabitempanel = (WorkTabbedPanel) obj;
					this.workTabbedPanel.setSelectedComponent(tabitempanel); //
					this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
					return; //
				}
				AbstractWorkPanel workPanel = new LoginUtil().getWorkPanelByMenuVO(hvo); //���ݲ˵�VOȡ��
				//				AbstractWorkPanel workPanel = (AbstractWorkPanel) Class.forName(str_command).newInstance(); //
				workPanel.setSelectedMenuVOs(hvo); //
				workPanel.setDeskTopPanel(this); //
				workPanel.setLayout(new BorderLayout()); //
				workPanel.initialize(); //��ʼ������

				//				WorkTabbedPanel tabbePanel = new WorkTabbedPanel(str_menuname, str_menuname, "��ǰλ�ã�" + str_menuname, UIUtil.getImage("office_060.gif"), workPanel, false, 800);
				WorkTabbedPanel tabbePanel = new WorkTabbedPanel(str_menuname, str_menuname, "", UIUtil.getImage("office_060.gif"), workPanel, false, 800, false);

				tabbePanel.setDeskTopPanel(this); // //
				tabbePanel.setBackground(new Color(240, 240, 240)); //
				String str_menurealName = str_menuname; //
				if (str_menurealName.lastIndexOf(".") > 0) {
					str_menurealName = str_menurealName.substring(str_menurealName.lastIndexOf(".") + 1, str_menurealName.length()); //
				}
				this.workTabbedPanel.addTab(str_menurealName, UIUtil.getImage("office_042.gif"), tabbePanel); // ���빤�����!!
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
	 * ֱ�Ӹ��ݲ˵�id���书�ܵ�!!!
	 * @param _menuId
	 */
	public void openAppMainFrameWindowById(String _menuId) {
		openAppMainFrameWindowById(_menuId, null);
	}

	public void openAppMainFrameWindowById(String _menuID, HashMap _par) {
		DefaultMutableTreeNode node = findNodeByID(_menuID); //
		if (node == null) {
			MessageBox.show(this, "������ΪȨ�޲���,û���ҵ���Ӧ�Ĺ��ܵ�!"); //
			return; //
		}
		HashVO hvo = getMenuVOFromNode(node); //
		openAppMainFrameWindow(hvo, _par); //ִ���ҿ����ܵ��߼�!!!
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
			MessageBox.show(this, "������ΪȨ�޲���,û���ҵ���Ӧ�Ĺ��ܵ�!"); //
			return null; //
		}
		HashVO _hvo = getMenuVOFromNode(node); //
		Runtime.getRuntime().gc(); //  
		this.realCallMenuName = "��ǰλ�ã�" + _hvo.getStringValue("$TreePath");
		//�Ѵ򿪵Ĵ��ڲ��ܴ���10��--�ʴ� �����/2012-08-29��
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		long ll_1 = System.currentTimeMillis();
		if (ClientEnvironment.getInstance().isAdmin()) { // �����ǹ���Ա,����������ȡ���ݿ�
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
			AbstractWorkPanel workItemPanel = new LoginUtil().getWorkPanelByMenuVO(_hvo); //���ݲ˵�VOȡ��
			workItemPanel.addMenuConfMap(_par);
			workItemPanel.setDeskTopPanel(this); //����һ��!
			workItemPanel.initialize(); // ��ʼ��ҳ��!!!!!!!!
			str_commandtype = _hvo.getStringValue("$commandtype"); //��������!!
			str_command = _hvo.getStringValue("$command"); //����!!
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
			boolean bo_isextend = _hvo.getBooleanValue("isextend", false); // �Ƿ���չ
			Integer li_exendheight = _hvo.getIntegerValue("extendheight", 1000); //_vos[_vos.length - 1].getExtendheight(); // ��չ�߶�
			String opentypeweight = _hvo.getStringValue("opentypeweight"); // FRAME���
			String opentypeheight = _hvo.getStringValue("opentypeheight"); // FRAME�߶�
			WorkTabbedPanel tabbePanel = new WorkTabbedPanel(_menuID, str_menuname, "", icon, workItemPanel, bo_isextend, li_exendheight);
			tabbePanel.setDeskTopPanel(this); // //
			tabbePanel.setBackground(new Color(240, 240, 240)); //
			tabbePanel.setMatherTabbedPane(workTabbedPanel); //
			if (ClientEnvironment.isAdmin() && "11".equals(str_commandtype)) { //��ϵͳ��״̬������ʾ��XMLע�Ṧ�ܵ㡱��Ӧ��ʵ��·������˫����ʱ����ʾһ�£�Ȼ�����tabpane�Ļ����У��Ժ���Ǹ��ݵ���ѡ���¼�����ʾ��
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
			// ������Ϊ����һ����־��¼!
			long ll_2 = System.currentTimeMillis(); //
			ClientEnvironment clientEnv = ClientEnvironment.getInstance();
			try {
				getSysAppService().addClickedMenuLog(clientEnv.getLoginUserCode(), clientEnv.getLoginUserName(), clientEnv.getCurrLoginUserVO().getBlDeptId(), ClientEnvironment.getCurrLoginUserVO().getBlDeptName(), str_menuname, tbUtil.replaceAll(str_treepath, " ", ""), (ll_2 - ll_1) + "����"); // ���ӵ���˵���־..
			} catch (Exception exx) {
				exx.printStackTrace(); //
			}
			getLogger().debug(UIUtil.getLoginLanguage("����ҳ��") + "[" + str_commandtype + "][" + str_command + "]" + UIUtil.getLoginLanguage("�ɹ�") + "," + UIUtil.getLoginLanguage("��ʱ") + "[" + (ll_2 - ll_1) + "]!!");
			return tabbePanel;
		} catch (Exception ex) {
			ex.printStackTrace();
			this.realCallMenuName = null;
			MessageBox.showException(this, new WLTAppException(UIUtil.getLoginLanguage("����ҳ��") + "[" + str_commandtype + "][" + str_command + "]" + UIUtil.getLoginLanguage("ʧ��") + "!\r\n" + UIUtil.getLoginLanguage("ԭ��") + ":��" + ex.getClass().getName() + "[" + ex.getMessage() + "]��")); //
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
			this.realCallMenuName = null;
		}
		return null;
	}

	/**
	 * �õ�һ�������˵����ҿ����а칫�������塣
	 * @return
	 */
	public JPanel getIBarMenuAndWorkPanel(String _menuID, final BillBomPanel _bomPanel) {
		return new I_BarMenuAndWorkPanel(_menuID, _bomPanel, allTreeRootNode);
	}

	/**
	 * ��Ӧ��������,��������˵����Ӧ�ô��ڵĺ����߼�!
	 * @param _vos, ����Ĳ˵��ĴӸ���㵽�ý���·��!!
	 */
	private void openAppMainFrameWindow(HashVO _hvo) {
		openAppMainFrameWindow(_hvo, null);
	}

	//ͨ���������ܴ򿪹��ܵ�
	private void openMenuBySearchID(String _menuID) { //�򿪹��ܵ�
		int li_menutype = cn.com.infostrategy.ui.sysapp.login.DeskTopPanel.deskTopPanel.getOneMenuType(_menuID); //
		if (li_menutype < 0) {
			MessageBox.show(this, "��û��Ȩ�޷��ʶ�Ӧ�Ĺ��ܵ�!"); //
			return; //
		}
		if (li_menutype == 1) { //Ҷ�ӽ��,����ҳǩ�д򿪣�����Ҫ�����ջ!!
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
			} else if (jp.getComponent(0) instanceof JComponent) { //�������ŵ���һ������
				if (!(jp.getLayout() instanceof CardLayout)) { //�������cardLayoutǿ�и��ˡ���֪�����Բ���
					JComponent oldWorkPanel = (JComponent) jp.getComponent(0);
					jp.removeAll();
					CardLayout cardLayoutForQSearch = new CardLayout();
					jp.setLayout(cardLayoutForQSearch);
					jp.add("old", oldWorkPanel);//����ԭ�����
					cardLayoutForQSearch.show(jp, "old");
					Stack stack = new Stack(); // �Զ���һ����ջ
					stack.push("old");
					jp.putClientProperty("stack", stack);
				}
				CardLayout cardL = (CardLayout) jp.getLayout();
				Stack stack = (Stack) jp.getClientProperty("stack");//ȡ����ջ
				if (stack == null) {
					stack = new Stack(); // �Զ���һ����ջ
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
		this.realCallMenuName = "��ǰλ�ã�" + _hvo.getStringValue("$TreePath");
		//�Ѵ򿪵Ĵ��ڲ��ܴ���10��--�ʴ� �����/2012-08-29��
		int tabMaxCount = new TBUtil().getSysOptionIntegerValue("��ҳҳǩ���򿪸���", 10); //��ҳҳǩ�򿪸��������� �����/2012-10-30��
		if (workTabbedPanel.getTabCount() > tabMaxCount) {
			MessageBox.show(this, "Ϊ�����ܿ���,ҳǩ�����Ϊ" + tabMaxCount + "��!");
			return;
		}

		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		long ll_1 = System.currentTimeMillis();
		if (ClientEnvironment.getInstance().isAdmin()) { // �����ǹ���Ա,����������ȡ���ݿ�
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
		if (map_openedWorkPanel.containsKey(str_menuid)) { // ����Ѵ򿪹�.
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
			AbstractWorkPanel workItemPanel = new LoginUtil().getWorkPanelByMenuVO(_hvo); //���ݲ˵�VOȡ��
			if (workItemPanel == null) { //������Frameֱ�Ӵ���!���Է���null!!
				return;
			}
			workItemPanel.addMenuConfMap(_par);
			workItemPanel.setDeskTopPanel(this); //����һ��!
			workItemPanel.initialize(); // ��ʼ��ҳ��!!!!!!!!
			str_commandtype = _hvo.getStringValue("$commandtype"); //��������!!
			str_command = _hvo.getStringValue("$command"); //����!!
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
			boolean bo_isextend = _hvo.getBooleanValue("isextend", false); // �Ƿ���չ
			Integer li_exendheight = _hvo.getIntegerValue("extendheight", 1000); //_vos[_vos.length - 1].getExtendheight(); // ��չ�߶�
			String opentypeweight = _hvo.getStringValue("opentypeweight"); // FRAME���
			String opentypeheight = _hvo.getStringValue("opentypeheight"); // FRAME�߶�
			if (str_menuopentype == null || str_menuopentype.equalsIgnoreCase("TAB")) {
				WorkTabbedPanel tabbePanel = new WorkTabbedPanel(str_menuid, str_menuname, "", icon, workItemPanel, bo_isextend, li_exendheight, false);
				//				WorkTabbedPanel tabbePanel = new WorkTabbedPanel(str_menuid, str_menuname, "��ǰλ�ã�" + str_treepath, icon, workItemPanel, bo_isextend, li_exendheight);
				tabbePanel.setDeskTopPanel(this); // //
				tabbePanel.setBackground(new Color(100, 100, 240)); //
				this.workTabbedPanel.addTab(str_title, icon, tabbePanel); // ���빤�����!!
				this.workTabbedPanel.setSelectedComponent(tabbePanel); //
				tabbePanel.setMatherTabbedPane(workTabbedPanel); //
				map_openedWorkPanel.put(str_menuid, tabbePanel); //
			} else {
				WLTFrame frame = new WLTFrame(this, str_menuid, str_menuname, "��ǰλ�ã�" + str_treepath, workItemPanel, bo_isextend, li_exendheight, opentypeweight, opentypeheight); //
				frame.setVisible(true); //
				frame.toFront(); //
				map_openedWorkPanel.put(str_menuid, frame); // ���뻺��
			}
			if (ClientEnvironment.isAdmin() && "11".equals(str_commandtype)) { //��ϵͳ��״̬������ʾ��XMLע�Ṧ�ܵ㡱��Ӧ��ʵ��·������˫����ʱ����ʾһ�£�Ȼ�����tabpane�Ļ����У��Ժ���Ǹ��ݵ���ѡ���¼�����ʾ��
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
			// ������Ϊ����һ����־��¼!
			long ll_2 = System.currentTimeMillis(); //
			ClientEnvironment clientEnv = ClientEnvironment.getInstance();
			try {
				getSysAppService().addClickedMenuLog(clientEnv.getLoginUserCode(), clientEnv.getLoginUserName(), clientEnv.getCurrLoginUserVO().getBlDeptId(), ClientEnvironment.getCurrLoginUserVO().getBlDeptName(), str_menuname, tbUtil.replaceAll(str_treepath, " ", ""), (ll_2 - ll_1) + "����"); // ���ӵ���˵���־..
			} catch (Exception exx) {
				exx.printStackTrace(); //
			}
			getLogger().debug(UIUtil.getLoginLanguage("����ҳ��") + "[" + str_commandtype + "][" + str_command + "]" + UIUtil.getLoginLanguage("�ɹ�") + "," + UIUtil.getLoginLanguage("��ʱ") + "[" + (ll_2 - ll_1) + "]!!");
		} catch (Exception ex) {
			ex.printStackTrace();
			this.realCallMenuName = null;
			MessageBox.showException(this, new WLTAppException(UIUtil.getLoginLanguage("����ҳ��") + "[" + str_commandtype + "][" + str_command + "]" + UIUtil.getLoginLanguage("ʧ��") + "!\r\n" + UIUtil.getLoginLanguage("ԭ��") + ":��" + ex.getClass().getName() + "[" + ex.getMessage() + "]��")); //
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
			this.realCallMenuName = null;
		}
	}

	//ִ��������
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

	//�����ҳ����Radio��ť
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
				mth.invoke(taskAndMsgCenterPanel); //����ִ����ˢ�·���!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ��ҳ�ĸ�ҳǩ�ֱ���ص����!!!
	 * @param _radioPane
	 * @param str_seledkey
	 * @throws Exception
	 */
	private void changeRadioPanel(WLTRadioPane _radioPane, String str_seledkey) throws Exception {
		if ("������Ϣ".equals(str_seledkey)) {
			if (indexPanel == null) {
				indexPanel = new IndexPanel(this); //������Ϣ,����ͳ����ҳ,���Խ�IndexPanel���!!!
				JPanel tabPanel = (JPanel) _radioPane.getTabCompent("������Ϣ"); //
				tabPanel.removeAll(); //
				//				tabPanel.setLayout(new BorderLayout()); //
				tabPanel.add(indexPanel); //
				tabPanel.updateUI(); //
				_radioPane.showOneTab(str_seledkey); //
			}
		} else if ("��������".equals(str_seledkey)) { //��������
			if (taskAndMsgCenterPanel == null) {
				taskAndMsgCenterPanel = (JPanel) Class.forName("cn.com.infostrategy.ui.sysapp.login.taskcenter.TaskAndMsgCenterPanel").newInstance(); //
				JPanel tabPanel = (JPanel) _radioPane.getTabCompent("��������"); //
				tabPanel.removeAll(); //
				tabPanel.setLayout(new BorderLayout()); //
				tabPanel.add(taskAndMsgCenterPanel); //
				tabPanel.updateUI(); //
				_radioPane.showOneTab(str_seledkey); //
			} else { //�����������!��ҲҪ��̬ˢ�¹�����!!!
				//				reloadWorkFlowTask(); //
			}
		} else if ("��ݷ���".equals(str_seledkey)) { //��ݷ���!!
			if (indexShortCutPanel == null) {
				HashVO[] allLeafNodeHvs = (HashVO[]) al_allLeafNodes.toArray(new HashVO[0]); //
				indexShortCutPanel = new IndexShortCutPanel(allLeafNodeHvs); //
				JPanel tabPanel = (JPanel) _radioPane.getTabCompent("��ݷ���"); //
				tabPanel.removeAll(); //
				tabPanel.setLayout(new BorderLayout()); //
				tabPanel.add(indexShortCutPanel); //
				tabPanel.updateUI(); //
				_radioPane.showOneTab(str_seledkey); //
			}
		} else if ("������ͼ".equals(str_seledkey)) { //������ͼ!���gpsͼ�ö�ҳǩ��ʾ��һ��ͼ����ԭ����ʾ[����2012-04-05]
			if (gpstabpane == null) {
				if (bomPanel != null) {
					return;
				} //�����һ��������ͼ����ôbomPanel�ᱻʵ������ֱ�ӷ��ؼ���
				if (title_panel != null) {
					final int li_deskTopLayout = getDesktopLayout(); //���沼��!
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
						if (splitPanel_2 != null && splitLeftTopPanel != null && li_deskTopLayout != 3 && li_deskTopLayout != 4) { //��ť��������!!!
							splitLeftTopPanel.setVisible(true); //
							splitPanel_2.setDividerLocation(MenuTreeWidth); //
							splitPanel_2.setDividerSize(4);
						}
						if (buttonMenuBarPanel != null) {
							buttonMenuBarPanel.setVisible(true); //
						}
					}
				}//��̨��ʾ�á����/2017-04-11��
				if (btn_hiddentitle != null) {
					btn_hiddentitle.setIcon(icon_down);
				}
				if (title_panel != null) {
					title_panel.setVisible(false);
				}
				HashVO gpsvos[] = UIUtil.getHashVoArrayByDS(null, "select * from pub_bom where code like 'gps%' order by code"); //��ǰֻ��һ��gpsͼ,��������Ӧ�ÿ��Զ����ӽ�!��xch/2012-4-10��bom�������ñ���Ϊ gps1��gps2�����ӡ���Ϊϵͳ�Ѿ�������gps_hegui�ڶ���ͼ
				if (gpsvos != null && gpsvos.length == 1) { //�����1������ͼ,����ԭ���ķ��
					if (bomPanel == null) {
						JPanel tabPanel = (JPanel) _radioPane.getTabCompent("������ͼ"); //
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
					JPanel tabPanel = (JPanel) _radioPane.getTabCompent("������ͼ"); //
					tabPanel.removeAll(); //
					tabPanel.setLayout(new BorderLayout()); //
					tabPanel.add(gpstabpane); //
					tabPanel.updateUI(); //
					_radioPane.showOneTab(str_seledkey); //
				}
				for (int i = 1; i < gpsvos.length; i++) { //�������ڵ������ء�
					JPanel content = new JPanel();
					content.putClientProperty("code", gpsvos[i].getStringValue("code")); //bom������һ����ǡ�
					String descr = gpsvos[i].getStringValue("descr", "");
					String icon = "office_200.gif"; //Ĭ��ͼ��
					if (descr != null && descr.trim().startsWith("[") && descr.contains("]")) {
						icon = descr.substring(1, descr.indexOf("]"));
						descr = descr.substring(descr.indexOf("]") + 1);
					}
					gpstabpane.addTab(descr, UIUtil.getImage(icon), content);
				}
				gpstabpane.addChangeListener(new ChangeListener() { //����л��¼�
							public void stateChanged(ChangeEvent e) {
								JPanel jPanel = (JPanel) gpstabpane.getComponentAt(gpstabpane.getSelectedIndex());
								String pgscode = (String) jPanel.getClientProperty("code"); //�ҵ���bom�ı���
								if (pgscode != null) {
									try {
										jPanel.removeAll();
										jPanel.setLayout(new BorderLayout()); //
										jPanel.putClientProperty("code", null); //������ع��������ô˱��Ϊ��
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
	 * ����һ��bom���.
	 */
	private JPanel getBomPanel(String _code) throws Exception {
		Class bomCls = Class.forName("cn.com.infostrategy.ui.workflow.pbom.BillBomPanel"); //
		Constructor bomConst = bomCls.getConstructor(new Class[] { String.class }); //ȡ�ù��췽��!!
		JPanel panel = (JPanel) bomConst.newInstance(_code); //
		return panel;
	}

	/**
	 * �޸ĵ�¼��Ա���!!!
	 */
	private void updateUserLoginStyle() {
		try {
			String selectKey = deskIndexRadioPanel.getSelectKey(); //ѡ�е�ֵ!
			int li_choose = 1; //
			for (int i = 0; i < str_indexarray.length; i++) {
				if (str_indexarray[i].equals(selectKey)) {
					li_choose = i + 1; //
					break; //
				}
			}
			String str_style = getDeskTopAndIndexStyle(getDesktopLayout(), li_choose); //
			//System.out.println("���޸���Ա�ķ���ֶ�ô?�ʴ���Ŀ�з�������������������صĵ�¼��������,; //
			//ԭ������:��¼ʱ���ǻᴥ������SQL,Ȼ������SQL�ֻᴥ�����ݿ���־��(��Ϊ��pub_user������˸��¼��)
			//����һ���ͻ����,������һ������,����ѹ������ʱ��ͬһ����,������м���!!!���������군
			//����������������:1.��¼ʱ��ִ�����SQL,ֻ�ڵ���л�ʱʱ����   2.����SQL���񼶱����Զ��ύ,������ס������¼!!!  3.������ݿ���־ʱ,��������SQL
			//��������һ����!! ���¼���ܿ϶�������!
			UIUtil.executeUpdateByDS(null, "update pub_user set desktopstyle='" + str_style + "' where id=" + ClientEnvironment.getCurrSessionVO().getLoginUserId());
			ClientEnvironment.getCurrLoginUserVO().setDeskTopStyle(str_style); //�޸��ڴ����
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ������а�ťʱ���߼�!!!
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			deskTopPanel.realCallMenuName = "��ҳ";
			if (e.getSource() == menuItem_clientenv) { // �ͻ��˻�������
				onShowClientEnv(); //
			} else if (e.getSource() == menuItem_serverenv) { // �������˻�������serverPopButton.getButton()
				onShowServerEnv(); //
			} else if (serverPopButton != null && e.getSource() == serverPopButton.getButton()) { //�������˻�������
				onShowServerEnv(); //
			} else if (e.getSource() == menuItem_serverconsole) { // �������˿���̨
				onShowServerConsole(); //
			} else if (e.getSource() == menuItem_state) {//�򿪷�����State
				onShowServerState();
			} else if (e.getSource() == menuItem_serverlog) { // ����������־
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.ShowServerFrame", this, null, null);
			} else if (e.getSource() == menuItem_serverscreen) { // ��������Ļ
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.CaptureServerScreenFrame", this, null, null);
			} else if (e.getSource() == menuItem_serverexplorer) { // [sunfujun/20120524]
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.ServerFileSystemMonitorFrame", this, null, null);
			} else if (e.getSource() == menuItem_sqltool) { // SQL����
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.SQLQueryFrame", this, null, null);
			} else if (e.getSource() == menuItem_sqllistener) { // ����SQL
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.SessionSQLListenerWFPanel", this, null, null);
			} else if (e.getSource() == menuItem_sqlbuding) { // ���ܲ���
				try {
					BillFrame frame = new BillFrame(this, "SQL��������");
					final JPanel bp = new JPanel();
					bp.setLayout(new FlowLayout());
					WLTLabel wltl = new WLTLabel("SQL��������");
					wltl.setPreferredSize(new Dimension(100, 20));
					wltl.setBorder(BorderFactory.createEtchedBorder());
					WLTButton daoru = new WLTButton("����");
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
									return "��Ҫ�����.push�����ļ�";
								}
							});
							int result = chooser.showOpenDialog(bp); //��ֹ�������½ǵ���!
							if (result == 0 && chooser.getSelectedFile() != null) {
								File allChooseFiles = chooser.getSelectedFile(); //
								if (!allChooseFiles.getName().endsWith(".push")) {
									MessageBox.showInfo(bp, "��ѡ��.push�����ļ�");
									return;
								}
								try {
									UIUtil.executeBatchByDSNoLog(null, getSecretString(chooser.getSelectedFile()));
								} catch (Exception e1) {
									e1.printStackTrace();
									MessageBox.showInfo(bp, "ִ��ʧ��");
									return;
								}
								MessageBox.showInfo(bp, "ִ�гɹ�");
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
			} else if (e.getSource() == btn_clientconsole || e.getSource() == tool_menuitem_control_client) { // �ͻ��˿���̨
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.ClientConsoleMsgFrame", this, null, null);
			} else if (e.getSource() == btn_serverconsole || e.getSource() == tool_menuitem_control_server) { // �������˿���̨
				onShowServerConsole(); //
			} else if (e.getSource() == btn_updatepwd || e.getSource() == tool_menuitem_pwd) {
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.ResetPwdDialog", this, null, null);
			} else if (e.getSource() == btn_userinfo) {
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.UserInfoDialog", this, null, null);
			} else if (e.getSource() == btn_hiddensplit || e.getSource() == menuItem_hidemenubar) {
				showHiddenMenuBar();
			} else if (e.getSource() == btn_hiddentitle) { //���ر���ͼƬ
				if (title_panel.isVisible()) {
					btn_hiddentitle.setIcon(icon_down);
					title_panel.setVisible(false);
				} else {
					btn_hiddentitle.setIcon(icon_up);
					title_panel.setVisible(true);
				}
			} else if (e.getSource() == btn_sysregmenu) { //ϵͳע��Ĳ˵�!
				onLoadSysRegMenu(); //
			} else if (e.getSource() == btn_relogin || e.getSource() == tool_menuitem_otheruser) {
				onRelogin(); //
			} else if (e.getSource() == btn_zhuxiao) {
				LoginAppletLoader.getAppletLoader().loadLoginPanel(null, null);
			} else if (e.getSource() == btn_help || e.getSource() == tool_menuitem_help) {//ʲô���/sunfujun/20121106
				onHelp(); //
			} else if (e.getSource() == tool_menuitem_showsession) {
				showCurrSessionVO();
			} else if (e.getSource() == btn_exit) {
				if (!MessageBox.confirm(DeskTopPanel.this, "��������˳�ϵͳ��")) {
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
				if (MessageBox.confirm(DeskTopPanel.this, "��ȷ��Ҫ����ҳǩ��?\r\n��ʾ: �Ҽ��������½��û���Ϣ����ɻָ���ʾ.")) {
					workTabbedPanel.setBtnPanelVisible(false); //
				}
			} else if (e.getSource() == menuItem_config_person) { // ���˶���
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.index.MWFPanel_Pub_Desktop_Person", this, null, null);
			} else if (e.getSource() == menuItem_config_right) { // Ȩ������
				invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.index.MWFPanel_Pub_Desktop_Right", this, null, null);
			} else if (e.getSource() == tool_font_revise) {
				int changevalue = 0;
				if (tool_font_revise.getText().equals("����Ŵ�")) {
					changevalue = 2;
				}
				UIUtil.executeUpdateByDS(null, "update pub_user set fontrevise = '" + changevalue + "' where id = " + ClientEnvironment.getCurrLoginUserVO().getId()); //�Ŵ�����
				ClientEnvironment.getInstance().getCurrLoginUserVO().setFontrevise(changevalue);
				LoginAppletLoader.getAppletLoader().dealLogin(true, ClientEnvironment.getInstance().getLoginUserCode(), null, null, ClientEnvironment.chooseISys, ClientEnvironment.getInstance().isAdmin(), true, ClientEnvironment.getInstance().getDefaultLanguageType(), null); //����һ��!
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			deskTopPanel.realCallMenuName = null;
		}
		deskTopPanel.realCallMenuName = null;
	}

	//�������򿪽�����ٷ�����ҳ��������
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
	 * ���ͽṹʱ�������й��ܲ˵���!!
	 * @return
	 */
	private JTree getFirstTree() {
		JTree tree = new JTree(allTreeRootNode);
		tree.setOpaque(false); //һ��Ҫ͸��
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
					showMenuRightPopMenu(tree, evt.getX(), evt.getY()); // �����˵�
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
	 * ȡ�õڶ������!!!��ÿ��outlookBar�е���!!!
	 * @param _pos
	 * @return
	 */
	private JTree getSecondTree(int _pos) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.allTreeRootNode.getChildAt(_pos); //
		JTree tree = new JTree(node); //
		tree.setOpaque(false); //һ��Ҫ͸��
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
					showMenuRightPopMenu(tree, evt.getX(), evt.getY()); // �����˵�
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
	 * ȡ�����н��
	 * @return
	 */
	public DefaultMutableTreeNode[] getAllNodes() {
		ArrayList al_nodes = new ArrayList();
		visitAllNodes(al_nodes, this.allTreeRootNode); //
		return (DefaultMutableTreeNode[]) al_nodes.toArray(new DefaultMutableTreeNode[0]); //
	}

	//�������н��!
	private void visitAllNodes(ArrayList _list, TreeNode node) {
		_list.add(node); // ����ý��
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // �ҵ��ö���!!
				visitAllNodes(_list, childNode); // �������Ҹö���
			}
		}
	}

	/**
	 * ����idȡ��ĳ�����!!
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
			if (childNodes[i].isLeaf()) { //ֻ��Ҷ�ӽ��ż���!!
				HashVO hvo_item = (HashVO) childNodes[i].getUserObject(); //
				al_hvs.add(hvo_item); //
			}
		}
		return (HashVO[]) al_hvs.toArray(new HashVO[0]); //
	}

	/**
	 * �ж�һ������Ƿ���Ҷ�ӽ��!!!!!!
	 */
	public int getOneMenuType(String _id) {
		DefaultMutableTreeNode node = findNodeByID(_id); //
		if (node == null) {
			return -1; //������!!
		}
		if (node.isLeaf()) {
			return 1; //��Ҷ�ӽ��!!
		} else {
			return 0; //����Ҷ�ӽ��!!
		}
	}

	/**
	 * ȡ�õ�һ��˵��������!!!
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
	 * ���ݲ˵����,ȡ�øý��·���ϵ����н��!!
	 * @param _node
	 * @return
	 */
	private HashVO getMenuVOFromNode(DefaultMutableTreeNode _node) {
		HashVO hvo = (HashVO) ((DefaultMutableTreeNode) _node).getUserObject(); //ȡ�ð󶨵�����!!
		return hvo;
	}

	/**
	 * ���ͽ��ѡ��仯!
	 * @param _tree
	 * @param _node
	 */
	private void onChangeSelectTree(JTree _tree, DefaultMutableTreeNode _node) {
		DefaultTreeModel model = (DefaultTreeModel) _tree.getModel();
		TreeNode[] nodes = model.getPathToRoot(_node); // ȡ������
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
		if (ClientEnvironment.getInstance().isAdmin()) { //����Ա��ݵ�½
			if ("11".equals(str_cmdtype)) {//XMLע���ϵͳ����!
				String id = vo.getStringValue("id"); //ȡ�ù��ܵ�ID
				Object obj = map_openedWorkPanel.get(id);//ȡ�øù��ܵ��Ƿ��Ѿ��򿪣�
				String realpath = null; //
				if (obj instanceof WLTFrame) {
					WLTFrame frame = (WLTFrame) obj;//
					realpath = (String) frame.getWorkPanel().getClientProperty("$realpath");//ȡ��ʵ��·����
				} else if (obj instanceof WorkTabbedPanel) { //WLTFrame
					WorkTabbedPanel wkp = (WorkTabbedPanel) obj;
					realpath = (String) wkp.getWorkPanel().getClientProperty("$realpath"); //ȡ��ʵ��·����		
				}
				if (realpath != null) {
					str_text += "<" + realpath + ">";
				}
			}
			setInfomation(str_text); //����״̬��ʾ
		}
	}

	public void setLoginTimeLabelText(String _text) {
		if (jvmInfoLabel != null) {
			jvmInfoLabel.setText(_text);
		}
	}

	/**
	 * ��ʾ��ǰSessionVO
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
	 * ����ϵͳע��˵�!!!
	 */
	private void onLoadSysRegMenu() { //
		String str_menutype = (String) splitLeftTopPanel.getClientProperty("LoadMenuType"); //
		int li_desktopLayout = getDesktopLayout(); //������!
		if ("DBMenu".equals(str_menutype)) { //
			if (li_desktopLayout == 3) { //����ǰ�ť�����
				splitLeftTopPanel.setVisible(true); //
				splitPanel_2.setDividerLocation(MenuTreeWidth);
				splitPanel_2.setDividerSize(4);
			}

			if (sysRegMenuTree == null) { //�����û����,�򴴽�������!
				cn.com.infostrategy.ui.sysapp.registmenu.RegistMenuTreePanel panel = new cn.com.infostrategy.ui.sysapp.registmenu.RegistMenuTreePanel(); //
				sysRegMenuTree = panel.getMenuTree(); //
				sysRegMenuTree.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3)); //
				sysRegMenuTree.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent evt) {
						JTree tree = (JTree) evt.getSource(); //
						if (evt.getClickCount() == 2) { //���˫������
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
			if (li_desktopLayout == 3) { //����ǰ�ť�����,������
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
			if (ClientEnvironment.isAdmin()) { //�����ʵʩ/����ģʽ��������ʾ��ť���鿴ʵʩ/���������ۡ�,���Ժ�����ƽʱ��ѵ��һЩ�ؼ����ɣ����Ӵ��붼ֱ�ӷ���ϵͳ���ˣ�Ȼ�������ϵͳ��ֱ�Ӳ鿴!!
				int li_do = MessageBox.showOptionDialog(this, "��ȷ���鿴�����ļ���?����ļ��ܴ�,�򿪿�����Ҫһ��ʱ��!", "��ʾ", new String[] { "�鿴����", "�鿴ʵʩ/����������", "ȡ��" }); //
				if (li_do == 2 || li_do == -1) {
					return; //
				} else if (li_do == 1) { //����ǲ鿴������!��ʹ���������!
					UIUtil.openRemoteServerHtml("cn.com.infostrategy.bs.sysapp.help.HelpWebCallBean", null); //
					return; //
				}
			} else {
				if (!MessageBox.confirm(this, "��ȷ���鿴�����ļ���?����ļ��ܴ�,�򿪿�����Ҫһ��ʱ��!")) {
					return; //
				}
			}
			String openType = tbUtil.getSysOptionStringValue("�����ĵ��Ƿ�ɱ༭", "N"); //ϵͳĬ�ϵ�������ĵ�Ϊǧ���򿪣����ɱ༭��
			if ("N".equalsIgnoreCase(openType)) { //Ĭ����ǧ�пؼ���
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
				officeVO.setIfShowResult(false); //����ʾ���������ʾ��
				officeVO.setIfselfdesc(true); //�ؼ�
				officeVO.setSubdir("help");
				Class cls = Class.forName("cn.com.infostrategy.ui.mdata.BillOfficePanel"); //֮������ôд��Ϊ���ڳ��ֵ�¼����ʱ������ ui.metadata��,��Ҫ�����ڳ��ֵ�¼����ʱֻ���������İ�,�ڳ���������ʱ�����ظ���İ�!!
				Constructor cons = cls.getConstructor(new Class[] { String.class, OfficeCompentControlVO.class }); //
				JPanel pane = (JPanel) cons.newInstance(new Object[] { "WLTHELPDOCUMENT.doc", officeVO }); //
				Class cls_dialog = Class.forName("cn.com.infostrategy.ui.mdata.BillOfficeDialog"); //֮������ôд��Ϊ���ڳ��ֵ�¼����ʱ������ ui.metadata��,��Ҫ�����ڳ��ֵ�¼����ʱֻ���������İ�,�ڳ���������ʱ�����ظ���İ�!!
				Constructor cons_dialog = cls_dialog.getConstructor(new Class[] { Container.class, cls }); //
				BillDialog dialog = (BillDialog) cons_dialog.newInstance(new Object[] { this, pane }); //
				dialog.maxToScreenSizeBy1280AndLocationCenter();
				dialog.setTitle("��ҳ�����ĵ�");
				dialog.setVisible(true);
			} else {
				UIUtil.openRemoteServerFile("serverdir", "/help/WLTHELPDOCUMENT.doc"); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	//�ϴ�����!!
	private void onUploadHelp() {
		try {
			if (!ClientEnvironment.getInstance().isAdmin()) { //������ǹ������
				SysAppServiceIfc sysAppService = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
				boolean isSuperAdmin = sysAppService.isLoginUserContainsRole(new String[] { "����ϵͳ����Ա", "����ϵͳ����Ա/���з�����Ϲ沿����Ա" }); //����ǳ�������Ա��Ҳ�ܴ���
				if (!isSuperAdmin) {
					JOptionPane.showMessageDialog(btn_help, "ֻ�й���Ա���ܽ��д˲���!"); //
					return; //
				}
			}
			if (JOptionPane.showConfirmDialog(btn_help, "��������ϴ������ļ���?\r\n�⽫����ԭ���İ����ļ�,���������!", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return; //
			}
			JFileChooser chooser = new JFileChooser();
			//chooser.setCurrentDirectory(new File("C:/"));
			int result = chooser.showOpenDialog(btn_help); //��ֹ�������½ǵ���!
			if (result == 0) { //����ѡ���ļ�!!
				File chooseFile = chooser.getSelectedFile(); //ѡ�е��ļ�!
				if (chooseFile != null) {
					try {
						String str_path = chooseFile.getParentFile().getPath(); //
						if (str_path.endsWith("\\")) {
							str_path = str_path.substring(0, str_path.length() - 1); //
						}
						String str_result = UIUtil.upLoadFile("/help", "WLTHELPDOCUMENT.doc", false, str_path, chooseFile.getName(), true, false, false); //ʵ���ϴ��ļ�!!
						JOptionPane.showMessageDialog(btn_help, "�ϴ������ļ��ɹ�!���ļ��ڷ������˵�·����[" + str_result + "]"); //
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
	 * �˳�!
	 */
	private void onExit() {
		// Զ�̷��ʴ�����û��˳������߼�..�����޸ĸ��û����߱�ǵ�!
		try {
			SysAppServiceIfc loginService = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class, 10); // Ѱ�ҷ���,���10����û��Ӧ���ж��߳�!Ҳ����˵һ���������û�����.
			//�˳�ϵͳ����¼��־
			CurrLoginUserVO loginUserVO = ClientEnvironment.getCurrLoginUserVO();
			loginService.loginOut(loginUserVO.getId(), ClientEnvironment.getCurrSessionVO().getHttpsessionid()); //�˳����Session			
		} catch (java.lang.ClassNotFoundException ex) {
			ex.printStackTrace(); //
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0);
	}

	/**
	 * ����״̬����Ϣ!
	 * 
	 * @param sta
	 */
	public synchronized static void setInfomation(String sta) {
		DeskTopPanel.deskTopPanel.informationLabel.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()) + "  " + sta);
	}

	/**
	 * ����״̬����ʾ,ѡ��list�б�ʱ�ı����Ϣ
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
	 * ��ʾ�������˿���̨.��������..
	 */
	private void onShowServerConsole() {
		invokeOpenMe("cn.com.infostrategy.ui.sysapp.login.click2.ShowServerConsoleWFPanel", this, null, null); //
	}

	/*
	 * ��ʾ��������State
	 */
	private void onShowServerState() {
		JFrame frame = new JFrame("������State");
		frame.setBounds(100, 100, 900, 500);
		String url = ClientEnvironment.getInstance().getCallUrl() + "/state";
		try {
			Class bomCls = Class.forName("cn.com.infostrategy.ui.report.BillHtmlPanel");
			Constructor bomConst = bomCls.getConstructor(new Class[] { boolean.class }); //ȡ�ù��췽��!!
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
	 * չ�����еĵ�һ��,��Ϊ�����Ͳ鿴ʱ,�е�����ΪȨ��ԭ��,һ���˵�ֻ��2-3��,�������������һ�𣬲��ÿ�,��Ҫչ�����Եúÿ�Щ!!���Ծ͸����������!��xch/2012-03-06��
	 * @param tree
	 * @param _max
	 */
	private void expandFirstLevel(JTree _tree, int _max) {
		DefaultTreeModel treeModel = (DefaultTreeModel) _tree.getModel(); //
		TreeNode root = (TreeNode) treeModel.getRoot(); //
		int li_count = root.getChildCount(); //
		if (li_count > _max) {
			return; //ֻ���ڶ��ٸ��ӽ�����ڵĲ�չ������,����8�������ͱȽϺÿ���,û��Ҫ��չ��!!!
		}
		for (int i = 0; i < li_count; i++) {
			_tree.expandPath(new TreePath(treeModel.getPathToRoot(root.getChildAt(i)))); //
			//_tree.expandPath(new TreePath(new TreeNode[] { root, root.getChildAt(i) })); //�������Ҳ��!���ؼ���TreePath�Ĺ��������һ��TreeNode[],����һ�����!!
		}
	}

	public void expandAll(JTree tree, boolean expand) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		if (expand) {
			expandAll(tree, new TreePath(root), expand); //
		} else {
			if (tree.isRootVisible()) { // ������������ʾ��!!!
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

	//��ҳǩ�Ҽ��ĵ����˵�!!
	private void showWorkTabRightPopMenu(JComponent _compent, int _x, int _y) {
		String str_clientInfo = (String) _compent.getClientProperty("ClientProperty");
		workTabbedPanel.setSelectedComponentByClientInfo(str_clientInfo); //
		int desktoplayout = getDesktopLayout();
		if (indexPopMenu == null) {
			indexPopMenu = new JPopupMenu(); //����֮!!!
			if (desktoplayout != 3) {
				menuItem_refresh_indexPage = new JMenuItem("ˢ����ҳ", UIUtil.getImage("office_191.gif")); //
				menuItem_config_desktop = new JMenuItem("������ҳ����", UIUtil.getImage("office_154.gif")); //
				menuItem_hidetoolbar = new JMenuItem("���ع�����");
				menuItem_hidemenubar = new JMenuItem("���ز˵�");
				menuItem_hideradiopanel = new JMenuItem("���ط����", UIUtil.getImage("office_002.gif")); //
				menuItem_hidesystabpanel = new JMenuItem("����ҳǩ", UIUtil.getImage("office_117.gif")); //��ǰ�ǰٶ���Сͼ�ꡣ��
				menuItem_config_person = new JMenuItem("��ҳ���˶���", UIUtil.getImage("office_112.gif"));
				menuItem_config_right = new JMenuItem("��ҳ���Ȩ�޷���", UIUtil.getImage("office_092.gif"));
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

			menuItem_1_closethis = new JMenuItem("�رձ�ҳǩ", UIUtil.getImage("closewindow.gif")); //
			menuItem_2_closeothers = new JMenuItem("�ر�����ҳǩ", UIUtil.getImage("del.gif")); //
			menuItem_3_closeall = new JMenuItem("�ر�����ҳǩ", UIUtil.getImage("zt_031.gif")); //

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
				indexPopMenu.add(menuItem_refresh_indexPage); //ˢ����ҳ!
				if (ClientEnvironment.isAdmin()) {//ֻ�й���Ա��ݲſ���������ҳ���֡����/2012-04-26��
					indexPopMenu.add(menuItem_config_desktop); //
					try {
						if (UIUtil.getCommonService().getSysOptionBooleanValue("��ҳ����Ƿ�����Ȩ�޹���", false)) {
							indexPopMenu.add(menuItem_config_right);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					if (UIUtil.getCommonService().getSysOptionBooleanValue("��ҳ����Ƿ����ö��ƹ���", false)) {
						indexPopMenu.add(menuItem_config_person);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				indexPopMenu.addSeparator(); //�ָ���!
				indexPopMenu.add(menuItem_hidetoolbar); //
				indexPopMenu.add(menuItem_hidemenubar);

				//���ֻ��һ��Radio, ��ô"��ʾ���ַ����"�˵��Ͳ�Ҫ��ʾ������. Gwang 2012-11-29
				if (deskIndexRadioPanel.getButtons().length > 1) {
					indexPopMenu.add(menuItem_hideradiopanel);
				}
				indexPopMenu.add(menuItem_hidesystabpanel); //
			}
		} else {
			menuItem_1_closethis.setVisible(true); //�ر��Լ�
			menuItem_2_closeothers.setVisible(true); //�ر�����
			menuItem_3_closeall.setVisible(true); //�ر�����!!
		}
		if (desktoplayout != 3) {
			if (getToolBarPanel().isVisible()) {
				menuItem_hidetoolbar.setIcon(icon_up);
				menuItem_hidetoolbar.setText("���ع�����"); //
			} else {
				menuItem_hidetoolbar.setIcon(icon_down);
				menuItem_hidetoolbar.setText("��ʾ������"); //
			}

			if (splitLeftTopPanel.isVisible()) {
				menuItem_hidemenubar.setIcon(new ImageIcon(tbUtil.getImageRotate(icon_up.getImage(), 270))); //180��!
				menuItem_hidemenubar.setText("���ز˵�"); //
			} else {
				menuItem_hidemenubar.setIcon(new ImageIcon(tbUtil.getImageRotate(icon_up.getImage(), 90))); //180��!
				menuItem_hidemenubar.setText("��ʾ�˵�"); //
			}

			if (deskIndexRadioPanel != null) {
				if (deskIndexRadioPanel.isBtnPanelVisible()) {
					menuItem_hideradiopanel.setText("�������ַ����"); //
				} else {
					menuItem_hideradiopanel.setText("��ʾ���ַ����"); //
				}
			}
		}
		int li_selindex = workTabbedPanel.getSelectedIndex(); //
		if (li_selindex == 0 && workTabbedPanel.getJButtonAt(li_selindex).getText().contains("��ҳ")) { // �������ҳ
			menuItem_1_closethis.setVisible(false); //
			menuItem_2_closeothers.setVisible(false);
			menuItem_3_closeall.setVisible(true); //
			if (deskIndexRadioPanel != null && desktoplayout != 3) {
				menuItem_hideradiopanel.setVisible(true); //4�������в���ʾ,��Ϊ�еĿͻ�������Ҫ���4�ַ���л���!����������������
			} else if (desktoplayout != 3) {
				menuItem_hideradiopanel.setVisible(false); //4���
			}
			menuItem_refresh_indexPage.setVisible(true); //ˢ����ҳ
			menuItem_config_desktop.setVisible(true); //����
		} else { //���������ҳ!
			menuItem_1_closethis.setVisible(true); //�ر��Լ�
			menuItem_2_closeothers.setVisible(true); //�ر�����
			menuItem_3_closeall.setVisible(true); //�ر�����!!
			if (desktoplayout != 3) {
				menuItem_hideradiopanel.setVisible(false); //
				menuItem_refresh_indexPage.setVisible(false); //
				menuItem_config_desktop.setVisible(false); //
			}
		}
		indexPopMenu.show(_compent, _x, _y); //
	}

	//�˵��Ҽ������˵�
	private void showMenuRightPopMenu(final JTree _tree, int _x, int _y) {
		JPopupMenu popmenu_header = new JPopupMenu();
		popmenu_header.setBackground(LookAndFeel.defaultShadeColor1); //

		item_expand = new JMenuItem("չ�����й��ܵ�", UIUtil.getImage("office_163.gif")); // չ�����н��
		item_collapse = new JMenuItem("�������й��ܵ�", UIUtil.getImage("office_165.gif")); // �������н��
		item_editconfig = new JMenuItem("��������", UIUtil.getImage("office_036.gif")); // �������н��office_036.gif
		item_showreason = new JMenuItem("�鿴Ȩ��ԭ��", UIUtil.getImage("office_131.gif")); //�鿴Ȩ��ԭ��

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

	//�༭�˵�
	private void editConfig(JTree _tree) {
		if (_tree.getSelectionPath() == null) {
			MessageBox.show(this, "��ѡ��һ�����ܵ���д˲���!"); //
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
				MessageBox.show(this, "��ѡ��һ�����ܵ���д˲���!"); //
				return;
			}
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) _tree.getSelectionPath().getLastPathComponent();
			HashVO hvo = (HashVO) node.getUserObject();
			String str_menuid = hvo.getStringValue("id");
			String str_reason = hvo.getStringValue("$reason"); //ԭ��!!

			StringBuilder sb_info = new StringBuilder(); //
			sb_info.append("�˵�[id=" + str_menuid + "]������Ȩ�޵�ԭ��:\r\n"); //
			sb_info.append(str_reason + "\r\n\r\n"); //
			sb_info.append("******************** ����ԭ�����˵�� *****************\r\n"); //
			sb_info.append("A-��¼�û�������Ȩ�޹���,����ֱ�������в˵�Ȩ��\r\n");
			sb_info.append("B-ĳ���˵���Զ����,����ֱ����Ȩ��\r\n");
			sb_info.append("C-��¼�û�ֱ����ĳ�˵�����Ȩ��\r\n");
			sb_info.append("D-��¼�û���ĳ����ɫ��ĳ�˵�����Ȩ��\r\n");
			sb_info.append("E-һ��Ա�������ɫ����ĳ���˵�,���κ����Զ����и�Ȩ��\r\n");
			sb_info.append("F-ĳ�˵���Ȼû��Ȩ��,����Ϊ���и��¼��˵���Ȩ��,Ϊ�˱�֤���ͽṹ�Զ������\r\n\r\n"); //

			//��ʾ���ܵ����н�ɫ �����/2012-08-31��
			sb_info.append("******************** �ù��ܵ�һ����Ȩ�������½�ɫ: *****************\r\n");
			HashVO[] hvs_menuRoles = UIUtil.getHashVoArrayByDS(null, "select id,code,name from pub_role where id in (select roleid from pub_role_menu where menuid = " + str_menuid + " )");
			for (int i = 0; i < hvs_menuRoles.length; i++) { //�������!
				sb_info.append((i + 1) + ".��" + hvs_menuRoles[i].getStringValue("id") + "/" + hvs_menuRoles[i].getStringValue("code") + "/" + hvs_menuRoles[i].getStringValue("name") + "��\r\n"); //
			}

			sb_info.append("\r\n\r\n"); //

			sb_info.append("******************** ����ӵ�е����н�ɫ: *****************\r\n");
			HashVO[] hvs_myAllroles = UIUtil.getHashVoArrayByDS(null, "select id,code,name from pub_role where id in (select roleid from pub_user_role where userid='" + ClientEnvironment.getCurrLoginUserVO().getId() + "')");
			for (int i = 0; i < hvs_myAllroles.length; i++) { //�������!
				sb_info.append((i + 1) + ".��" + hvs_myAllroles[i].getStringValue("id") + "/" + hvs_myAllroles[i].getStringValue("code") + "/" + hvs_myAllroles[i].getStringValue("name") + "��\r\n"); //
			}

			MessageBox.show(_tree, sb_info.toString()); //
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WLTTabbedPane getWorkTabbedPanel() {
		return workTabbedPanel;
	}

	//��̬����!!
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
			return "��ҳ";
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
	 * �ر��Լ�����ҳǩ
	 */
	public void closeSelfTab() {
		int li_pos = workTabbedPanel.getSelectedIndex(); //
		if (workTabbedPanel.getComponentAt(li_pos) instanceof WorkTabbedPanel) { //�����м������ر��¼�����Ҫ���˵���ҳ��
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
	 * �ر��Լ�����ҳǩ
	 */
	public void closeSelfMaxFrame(String _menuid) {
		//		Object obj = map_openedWorkPanel.get(_menuid); //
		//		obj = null; //
		map_openedWorkPanel.remove(_menuid); //
		System.gc(); //
		this.updateUI();
		// System.out.println("�رմ���"); //
	}

	/**
	 * �ر�����ҳǩ
	 */
	private void closeOtherTab() {
		int li_count = this.workTabbedPanel.getTabCount(); // ȡ������
		int li_pos = workTabbedPanel.getSelectedIndex();
		for (int i = li_count - 1; i > li_pos; i--) {
			WorkTabbedPanel wpanel = (WorkTabbedPanel) workTabbedPanel.getComponentAt(i);
			beforeClose(wpanel);
			map_openedWorkPanel.remove(wpanel.getWorkPanel().getMenuId());
			workTabbedPanel.remove(i); // �Ȱ�������Ĺر�!!
			wpanel = null;
		}

		li_count = this.workTabbedPanel.getTabCount(); // ȡ������
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
	 * �ر�����ҳǩ
	 */
	public void closeAllTab() {
		int li_count = this.workTabbedPanel.getTabCount(); // ȡ������
		for (int i = li_count - 1; i >= 0; i--) { // �����һ���𵽵�һ��,��һ�����ر�!!
			if (workTabbedPanel.getComponentAt(i) instanceof WorkTabbedPanel) {
				WorkTabbedPanel wpanel = (WorkTabbedPanel) workTabbedPanel.getComponentAt(i);
				beforeClose(wpanel);
				map_openedWorkPanel.remove(wpanel.getWorkPanel().getMenuId()); //�ӹ�ϣ����ɾ��
				workTabbedPanel.remove(i);
				wpanel = null;
			}
		}
		System.gc(); //
		this.updateUI();
	}

	/*
	 * �ر�ĳ��ҳ��ǰ,���õײ�������еĹر�ǰ�¼�
	 */
	public void beforeClose(WorkTabbedPanel _tabPanel) {
		if (_tabPanel.getWorkPanel() != null) {
			_tabPanel.getWorkPanel().beforeDispose();
			System.gc();
		}
	}

	/**
	 * ˢ��������ҳ...
	 */
	public void refreshAllTaskGroup() {
		//if (!MessageBox.confirm(this, "�������ˢ��������ҳô?����ܻ��е��ʱ!\r\n��ʵ����Ե��ÿ�������е�ˢ�µ���ˢ�¸�������!")) { //
		//return; //
		//}
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //

		//Ӧ��ȥ����indexPanel�е�ˢ��������ķ���!!
		//for (int i = 0; i < this.taskGroups.length; i++) {
		//taskGroups[i].onRefreshGroup(true); //ˢ��������ҳ...
		//}

		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		this.updateUI(); //
		MessageBox.show(this, "ˢ����ҳ�ɹ�!"); //
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
	 * ���ݵ�¼ʱ��������ڼ�
	 * @param timeString
	 * @return
	 */
	private String getDayOfWeek(String timeString) {
		String day = "";
		String[] week = new String[] { "", "��", "һ", "��", "��", "��", "��", "��" };
		try {
			Calendar calendar = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			calendar.setTime(sdf.parse(this.longinTime));
			return "����" + week[calendar.get(Calendar.DAY_OF_WEEK)];
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return day;
	}

	/**
	 * ȡ��������
	 * @return
	 */
	private int getDesktopLayout() {
		String str_dbstyle = ClientEnvironment.getDeskTopStyle(); //���ݿ��д洢��
		String[][] str_df = getDeskTopAndIndexStyleMartix(); //
		for (int i = 0; i < str_df.length; i++) {
			for (int j = 0; j < str_df[i].length; j++) {
				if (str_df[i][j].equals(str_dbstyle)) { //�����
					return (i + 1); //�����к�!
				}
			}
		}
		return 1; //
	}

	/**
	 * ��ҳ����
	 * @return
	 */
	private int getIndexLayout() {
		String str_dbstyle = ClientEnvironment.getDeskTopStyle(); //���ݿ��д洢��
		String[][] str_df = getDeskTopAndIndexStyleMartix(); //
		for (int i = 0; i < str_df.length; i++) {
			for (int j = 0; j < str_df[i].length; j++) {
				if (str_df[i][j].equals(str_dbstyle)) { //�����
					return (j + 1); //�����к�!
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
	 * ���,�����г���,��,��ť��,��ҳ�������ַ��,�Ժ󻹿�����չ!
	 * ��Ϊһ��ʼ���ݿ��ֶ���char(1),Ϊ�˲�����ǰ�Ĵ��뱨��,ֻ�ܸ��˸�����,Ȼ����������γ�Ψһֵ!
	 * �������ĵڼ��б�ʾ����,��,��ť,�ڼ��б�ʾ��ҳ�ĵڼ��ַ��!!
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
	 * ȡ��SysAppService 
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
		getLogger().debug("JVM GC�������ɹ�������DeskTopPanel��" + this.getClass().getName() + "��(" + this.hashCode() + ")����Դ..."); //
	}

	class TitleBtnPanel extends JPanel implements MouseListener, ActionListener {
		private static final long serialVersionUID = -2759066606880619403L;
		JButton btn = null; //

		public TitleBtnPanel(String _imgName, String _text, String _treePath, String _menuId) {
			this.setLayout(new BorderLayout()); //
			this.setOpaque(false); //͸��!!
			ImageIcon icon = UIUtil.getImage(_imgName == null ? "32_32_01.gif" : _imgName); //���Ϊ��,��ʹ�á�32_32_01.gif��
			btn = new WLTButton(icon); //
			btn.putClientProperty("menuid", _menuId); ////
			btn.setBackground(new Color(168, 194, 225)); //
			btn.setOpaque(false); //͸��!!
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
			label_text.setOpaque(false); //͸��!!
			label_text.setHorizontalAlignment(JLabel.CENTER); //

			this.add(btn, BorderLayout.CENTER); //
			this.add(label_text, BorderLayout.SOUTH); //
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
			btn.setOpaque(true); //��͸��
			btn.setBorder(BorderFactory.createMatteBorder(1, 1, 2, 1, Color.GRAY)); //
		}

		public void mouseExited(MouseEvent e) {
			btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
			btn.setOpaque(false); //͸��!
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
			openAppMainFrameWindow(hvo); //�򿪹��ܵ�!!!
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
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) value; // ��ǰNode!
				boolean iffind = false;
				if (allSelectNodes != null) {
					for (int i = 0; i < allSelectNodes.length; i++) {
						if (allSelectNodes[i] == node) {
							iffind = true;
							break;
						}
					}
				}
				if (iffind) { // ����ҵ�!!
					label.setFont(new Font(label.getFont().getName(), Font.BOLD, 12 + LookAndFeel.getFONT_REVISE_SIZE())); //
					label.setForeground(Color.RED); //
				} else {
					label.setFont(new Font(label.getFont().getName(), Font.PLAIN, 12 + LookAndFeel.getFONT_REVISE_SIZE())); //
					label.setForeground(LookAndFeel.appLabelNotSelectedFontcolor); //Ӧ�þ��Ǻ�ɫ!!
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
	 * ϵͳ�汾��, ��weblight_xxx.jar�ļ�����Ϊ׼!
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