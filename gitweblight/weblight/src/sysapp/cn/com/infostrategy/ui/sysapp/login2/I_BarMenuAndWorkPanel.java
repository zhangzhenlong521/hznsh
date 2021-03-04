package cn.com.infostrategy.ui.sysapp.login2;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTMenuItemUI;
import cn.com.infostrategy.ui.common.WLTMenuUI;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane.CloseTabedPaneBtn;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;
import cn.com.infostrategy.ui.sysapp.login.LoginUtil;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;
import cn.com.infostrategy.ui.sysapp.login.WLTFrame;
import cn.com.infostrategy.ui.sysapp.login.WorkTabbedPanel;
import cn.com.infostrategy.ui.workflow.pbom.BillBomPanel;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.sysapp.login2.I_BarMenuAndWorkPanel 
 * @Description: 这个类是根据菜单ID，得到一个网页风格导航菜单。其逻辑在DeskTopPanel都可找到。
 * DeskTopPanel太复杂 里面只有一个WorkTabedPane。只有独立出来才能保证同一个系统中有多个办公区。bom图链接中跳转到多个办公区
 * @author haoming
 * @date Mar 14, 2013 11:22:16 AM
 *  
*/
public class I_BarMenuAndWorkPanel extends JPanel implements ActionListener {
	private static Color fanse = new Color(9, 70, 160);//菜单条的背景颜色
	private String menuID;
	private BillBomPanel bomPanel;
	private WLTTabbedPane workTabbedPanel;
	private HashMap map_openedWorkPanel;//记录已经打开过的信息
	private TBUtil tbUtil = new TBUtil();
	private JMenuItem menuItem_1_closethis, menuItem_2_closeothers, menuItem_3_closeall; //右键菜单
	private DefaultMutableTreeNode allTreeRootNode;
	private HashMap buttonPopmenuMap = null; //存储按钮栏风格情况下弹出菜单!

	/**
	 * 
	 * @param _menuID 菜单ID
	 * @param _bomPanel bom图
	 * @param _all 所有菜单.
	 */
	public I_BarMenuAndWorkPanel(String _menuID, BillBomPanel _bomPanel, DefaultMutableTreeNode _all) {
		menuID = _menuID;
		bomPanel = _bomPanel;
		allTreeRootNode = _all;
		init();
	}

	private void init() {
		workTabbedPanel = new WLTTabbedPane(true);
		map_openedWorkPanel = new HashMap();
		this.setLayout(new BorderLayout());
		this.setOpaque(false);
		JPanel toppanel = new JPanel(new BorderLayout()); //菜单条
		toppanel.setOpaque(false);
		if (bomPanel != null) {
			JPanel back = getItemPanel(-2, "", "返回", "", fanse, false);
			JComponent[] panels = bomPanel.getCardPanelAllComponent();
			String parentname = (String) panels[panels.length - 1].getClientProperty("parentname");
			back.setToolTipText("上一层：" + parentname);
			back.setCursor(new Cursor(Cursor.HAND_CURSOR));
			back.addMouseListener(new MouseAdapter() {
				public void mouseReleased(MouseEvent e) {
					if (bomPanel != null) {
						JComponent btn = (JComponent) e.getSource();
						if (!(e.getX() > 0 && e.getX() < btn.getWidth() && e.getY() > 0 && e.getY() < btn.getHeight())) {
							return;
						}
						bomPanel.onBack();
						map_openedWorkPanel = new HashMap();
						DeskTopPanel.getDeskTopPanel().expandTopBtnPane(true);
						System.gc();
					}
				}

				public void mouseEntered(MouseEvent e) {
					((JPanel) e.getSource()).putClientProperty("focus", "Y");
					((JPanel) e.getSource()).repaint();
				}

				public void mouseExited(MouseEvent e) {
					((JPanel) e.getSource()).putClientProperty("focus", "N");
					((JPanel) e.getSource()).repaint();
				}
			});
			back.setPreferredSize(new Dimension(80, 35));
			toppanel.add(back, BorderLayout.WEST);
		}
		toppanel.add(getButtonMenuBarPanel(menuID), BorderLayout.CENTER);
		this.add(toppanel, BorderLayout.NORTH);
		workTabbedPanel.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
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
		this.add(workTabbedPanel, BorderLayout.CENTER);
	}

	public WLTTabbedPane getWorkTabbedPanel() {
		return workTabbedPanel;
	}

	/**
	 * 关闭自己本身页签
	 */
	public void closeSelfTab() {
		int li_pos = workTabbedPanel.getSelectedIndex(); //
		CloseTabedPaneBtn btn = (CloseTabedPaneBtn) getWorkTabbedPanel().getJButtonAt(li_pos);
		if(!btn.canClose()){
			return;
		}
		WorkTabbedPanel wpanel = (WorkTabbedPanel) workTabbedPanel.getComponentAt(li_pos); // 
		map_openedWorkPanel.remove(wpanel.getWorkPanel().getMenuId());
		workTabbedPanel.remove(li_pos); //
		wpanel = null;
		System.gc(); //
		this.updateUI();
	}

	JPopupMenu indexPopMenu;

	//多页签右键的弹出菜单!!
	private void showWorkTabRightPopMenu(JComponent _compent, int _x, int _y) {
		String str_clientInfo = (String) _compent.getClientProperty("ClientProperty");
		workTabbedPanel.setSelectedComponentByClientInfo(str_clientInfo); //
		if (indexPopMenu == null) {
			indexPopMenu = new JPopupMenu(); //创建之!!!

			menuItem_1_closethis = new JMenuItem("关闭本页签", UIUtil.getImage("closewin.gif")); //
			menuItem_2_closeothers = new JMenuItem("关闭其他页签", UIUtil.getImage("clear.gif")); //
			menuItem_3_closeall = new JMenuItem("关闭所有页签", UIUtil.getImage("office_143.gif")); //

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
		} else {
			menuItem_1_closethis.setVisible(true); //关闭自己
			menuItem_2_closeothers.setVisible(true); //关闭其他
			menuItem_3_closeall.setVisible(true); //关闭所有!!
		}
		indexPopMenu.show(_compent, _x, _y); //
	}

	//得到一个中间渐变的面板.
	public JPanel getItemPanel(final int _id, String _imgName, String _text, String _path, final Color _color, boolean _isShortCut) {
		JPanel panel = new WLTPanel(WLTPanel.VERTICAL_LIGHT2, new BorderLayout(), _color, false) {
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				String focus = (String) this.getClientProperty("focus");
				if (_id >= 0 && "Y".equals(focus)) {
					super.paintComponent(g);
					g2d.setColor(new Color(10, 10, 10, 100));
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2d.fillRoundRect(4, 0, this.getWidth() - 6, this.getHeight(), 30, 30);
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				} else {
					super.paintComponent(g);
				}
				if (_id > 0) { //画按钮间的竖线
					g2d.setColor(Color.DARK_GRAY);
					g2d.setStroke(new BasicStroke(1));
					g2d.drawLine(1, 10, 1, this.getHeight() - 10);
				}
				if (_id == -2) {//back按钮
					GeneralPath path = new GeneralPath();
					path.moveTo(15, 6);
					path.lineTo(65, 6);
					path.lineTo(65, 28);
					path.lineTo(15, 28);
					path.lineTo(3, 17f);
					path.lineTo(15, 6);
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					if ("Y".equals(focus)) {
						g2d.setColor(new Color(255, 255, 200, 200));
					} else {
						g2d.setColor(new Color(255, 255, 100, 150));
					}
					g2d.draw(path);
					g2d.fill(path);
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				}
			}
		}; //VERTICAL_TOP_TO_BOTTOM
		panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		JLabel textLabel = new JLabel(_text, JLabel.CENTER); //
		if (_id == -2) {
			textLabel.setForeground(fanse); //
		} else {
			textLabel.setForeground(new Color(224, 224, 224)); //
		}
		textLabel.setFont(LookAndFeel.font_big);

		panel.add(textLabel, BorderLayout.CENTER); ////
		return panel; //
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
	 * 取得所有结点
	 * @return
	 */
	public DefaultMutableTreeNode[] getAllNodes() {
		ArrayList al_nodes = new ArrayList();
		visitAllNodes(al_nodes, this.allTreeRootNode); //
		return (DefaultMutableTreeNode[]) al_nodes.toArray(new DefaultMutableTreeNode[0]); //
	}

	//根据MenuID找到菜单节点
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

	private DefaultMutableTreeNode menuNode; //最终显示的菜单根节点。

	//根据菜单ID，构造一个顶端按钮导航条
	private JPanel getButtonMenuBarPanel(String _menuID) {
		JPanel buttonMenuBarPanel = new JPanel();
		buttonMenuBarPanel = getItemPanel(-1, "", "", "", fanse, false); //创建底部渐变面板
		buttonMenuBarPanel.setLayout(new FlowLayout(FlowLayout.CENTER, -1, 0));
		//		buttonMenuBarPanel.setOpaque(false); //透明!!
		menuNode = allTreeRootNode;
		if (_menuID != null) {
			menuNode = findNodeByID(_menuID);
		}
		int li_firstModelCount = menuNode.getChildCount(); //一级模板数量!!
		Cursor cursor = new Cursor(Cursor.HAND_CURSOR); //
		int li_onebtnwid = 900 / li_firstModelCount; //根据屏幕宽度动态计算每个按钮宽度,即为了防止因为权限原因使得按扭太少,而缩在中间不好看!!
		if (li_onebtnwid > 200) {
			li_onebtnwid = 200;
		}
		if (li_onebtnwid < 70) {
			li_onebtnwid = 70;
		}
		for (int i = 0; i < li_firstModelCount; i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) menuNode.getChildAt(i); //
			HashVO hvo = (HashVO) node.getUserObject(); //取得VO数据!!
			if ("Y".equals(hvo.getStringValue("ishid")) && !ClientEnvironment.isAdmin()) {
				continue;
			}
			JPanel btnP = getItemPanel(i, null, hvo.getStringValue("name"), "", fanse, false); //按钮
			buttonMenuBarPanel.add(btnP);
			btnP.setPreferredSize(new Dimension(li_onebtnwid, 35)); //
			btnP.setCursor(cursor); //
			btnP.putClientProperty("bindmenuid", hvo.getStringValue("id")); //绑定菜单ID
			btnP.putClientProperty("bindmenuindex", i); //绑定菜单索引号
			btnP.addMouseListener(new MouseAdapter() {
				public void mouseEntered(MouseEvent e) {
					((JPanel) e.getSource()).putClientProperty("focus", "Y");
					onClickButtonMenu(menuNode, (JPanel) e.getSource()); //点击按钮事件!
					((JPanel) e.getSource()).repaint();
				}

				public void mouseExited(MouseEvent e) {
					((JPanel) e.getSource()).putClientProperty("focus", "N");
					super.mouseExited(e);
					((JPanel) e.getSource()).repaint();
				}

				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					JPanel jp = (JPanel) e.getSource();
					int li_menuindex = (Integer) jp.getClientProperty("bindmenuindex"); //绑定的菜单索引号!!
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) menuNode.getChildAt(li_menuindex); //取得这个结点!
					if (node.getChildCount() == 0) {
						openAppMainFrameWindowById((String) jp.getClientProperty("bindmenuid"));
					}
				}
			});
		}
		return buttonMenuBarPanel; //
	}

	/**
	 * 点击按钮菜单的事件监听,就是动态计算出弹出菜单并显示!!!
	 */
	private void onClickButtonMenu(DefaultMutableTreeNode _menuTreeNode, JPanel _button) {
		Point point = new Point(0, 0); //
		SwingUtilities.convertPointToScreen(point, _button); //取得位置!!
		Dimension dim = _button.getPreferredSize(); //
		String str_menuid = (String) _button.getClientProperty("bindmenuid"); //绑定的菜单Id...bindmenuindex
		if (buttonPopmenuMap != null && buttonPopmenuMap.containsKey(str_menuid)) {
			JPopupMenu popMenu = (JPopupMenu) buttonPopmenuMap.get(str_menuid); //
			popMenu.show(_button, (int) (_button.getWidth() - popMenu.getPreferredSize().getWidth()) / 2, (int) dim.getHeight()); //显示
		} else {
			int li_menuindex = (Integer) _button.getClientProperty("bindmenuindex"); //绑定的菜单索引号!!
			JPopupMenu popMenu = new JPopupMenu(); //
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) _menuTreeNode.getChildAt(li_menuindex); //取得这个结点!
			createPopMenu(node, popMenu, null); //递归算法创建所有子菜单!!!
			if (node.getChildCount() == 0) {
				popMenu.setPreferredSize(new Dimension(0, 0));
			}
			popMenu.show(_button, (int) (_button.getWidth() - popMenu.getPreferredSize().getWidth()) / 2, (int) dim.getHeight()); //显示
			//缓存一下!
			if (buttonPopmenuMap == null) {
				buttonPopmenuMap = new HashMap();
			}
			buttonPopmenuMap.put(str_menuid, popMenu); //
		}
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
				int str_width = tbUtil.getStrWidth(str_name); //得到字宽度
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
				int str_width = tbUtil.getStrWidth(str_name); //得到字宽度
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
	 * 根据菜单结点,取得该结点路径上的所有结点!!
	 * @param _node
	 * @return
	 */
	private HashVO getMenuVOFromNode(DefaultMutableTreeNode _node) {
		HashVO hvo = (HashVO) ((DefaultMutableTreeNode) _node).getUserObject(); //取得绑定的数据!!
		return hvo;
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
		openAppMainFrameWindow(hvo, _par, workTabbedPanel); //执行找开功能点逻辑!!!
	}

	private void openAppMainFrameWindow(HashVO _hvo, HashMap _par, WLTTabbedPane workTabbedPanel) {
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
			workItemPanel.addMenuConfMap(_par);
			if (workItemPanel == null) { //可能是Frame直接打开了!所以返回null!!
				return;
			}
			workItemPanel.setDeskTopPanel(DeskTopPanel.getDeskTopPanel()); //设置一下!
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
				tabbePanel.setDeskTopPanel(DeskTopPanel.getDeskTopPanel()); // //
				tabbePanel.setBackground(new Color(100, 100, 240)); //
				this.workTabbedPanel.addTab(str_title, icon, tabbePanel); // 加入工作面板!!
				this.workTabbedPanel.setSelectedComponent(tabbePanel); //
				tabbePanel.setMatherTabbedPane(workTabbedPanel); //
				map_openedWorkPanel.put(str_menuid, tabbePanel); //
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

	private String realCallMenuName = null;//在打开一个菜单的时候的remotecallcilent里的获取访问的菜单不对

	private SysAppServiceIfc getSysAppService() {
		try {
			return (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
		} catch (Exception ex) {
			ex.printStackTrace();
			return null; //
		}
	}

	Logger logger;

	private Logger getLogger() {
		if (logger == null) {
			logger = WLTLogger.getLogger(DeskTopPanel.class); //
		}
		return logger; //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == menuItem_1_closethis) {
			closeSelfTab();
		} else if (e.getSource() == menuItem_2_closeothers) {
			closeOtherTab(); //
		} else if (e.getSource() == menuItem_3_closeall) {
			closeAllTab();
		}

	}

	/**
	 * 关闭其他页签
	 */
	private void closeOtherTab() {
		int li_count = this.workTabbedPanel.getTabCount(); // 取得总数
		int li_pos = workTabbedPanel.getSelectedIndex();
		for (int i = li_count - 1; i > li_pos; i--) {
			WorkTabbedPanel wpanel = (WorkTabbedPanel) workTabbedPanel.getComponentAt(i);
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
				map_openedWorkPanel.remove(wpanel.getWorkPanel().getMenuId()); //从哈希表中删除
				workTabbedPanel.remove(i);
				wpanel = null;
			}
		}
		System.gc(); //
		this.updateUI();
	}

}
