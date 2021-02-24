package cn.com.infostrategy.ui.sysapp.login;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTMenuItemUI;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;

/**
 * 首页快捷方式面板! 就是仿照Noted与IPhone一样摆大按钮!!
 * @author xch
 *
 */
public class IndexShortCutPanel extends JPanel implements MouseListener, AdjustmentListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private String[] str_firstLevels = null; //
	private HashVO[] allNodeVOs = null; //所有叶子结点的数据!!!

	private JPanel myShortCutPanel = null; //
	private JPanel toftPanel = null; //
	private CardLayout cardLayout = null; //

	private Border border1 = BorderFactory.createLineBorder(Color.GRAY, 1); //
	private Border border2 = BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLineBorder(Color.RED, 1)); //; //
	private Cursor cursor = new Cursor(Cursor.HAND_CURSOR); //
	private Cursor cursor_wait = new Cursor(Cursor.WAIT_CURSOR); //
	private Color textColor = new Color(60, 60, 60); //

	private Color[] colors = new Color[] { new Color(60, 120, 160), new Color(63, 36, 117), new Color(120, 40, 50), new Color(50, 80, 50), new Color(80, 80, 80), new Color(155, 50, 0), new Color(20, 105, 107), new Color(75, 93, 75), new Color(75, 48, 132), new Color(76, 93, 142),
			new Color(144, 29, 67), new Color(101, 40, 34) }; //
	private TBUtil tbUtil = new TBUtil(); //

	private JPopupMenu popMenu = null; //
	private JMenuItem menuItem_add, menuItem_remove; //

	public IndexShortCutPanel(HashVO[] _allLeafNodeHvs) {
		this(_allLeafNodeHvs, true); //
	}

	//指定背景颜色!!!
	public IndexShortCutPanel(HashVO[] _allHVs, boolean _isCardLayout) {
		if (_isCardLayout) { //如果是根据模板分层的,则复杂布局!
			this.allNodeVOs = _allHVs; //
			initialize(); //
		} else {
			this.setLayout(new BorderLayout()); //
			this.add(getOneLevelAllBtnPanel(_allHVs, colors[new Random().nextInt(colors.length)], null), BorderLayout.CENTER); //
		}
	}

	//构造页面!!使用层!!
	private void initialize() {
		this.setLayout(new BorderLayout()); //
		str_firstLevels = getFirstLevelNodeNames(); //第一层清单!

		WLTTabbedPane tabbed = new WLTTabbedPane(); //
		myShortCutPanel = new JPanel(new BorderLayout()); //
		myShortCutPanel.add(getMyShortCutPanel(str_firstLevels)); //
		tabbed.addTab("我的常用功能", UIUtil.getImage("office_110.gif"), myShortCutPanel); //

		JPanel allMenuPanel = new JPanel(new BorderLayout()); //
		allMenuPanel.add(getNorthBtnPanel(str_firstLevels), BorderLayout.NORTH); //
		toftPanel = new JPanel(); //
		cardLayout = new CardLayout(); //
		toftPanel.setLayout(cardLayout); //

		//循环加入各层的数据!
		for (int i = 0; i < str_firstLevels.length; i++) { //遍历!!!
			int li_colorIndex = i % colors.length; //第几行,每5行是一组颜色!!
			JPanel itemPanel = getOneLevelAllBtnPanel(str_firstLevels[i], colors[li_colorIndex]); //
			toftPanel.add(itemPanel, str_firstLevels[i]); //
		}
		allMenuPanel.add(toftPanel, BorderLayout.CENTER); //

		tabbed.addTab("我的所有功能", UIUtil.getImage("office_083.gif"), allMenuPanel); //
		this.add(tabbed); //

		//创建右键弹出的菜单!!
		popMenu = new JPopupMenu(); //
		menuItem_add = new JMenuItem("添加至常用库"); //
		menuItem_remove = new JMenuItem("从常用库中移除"); //

		menuItem_add.setUI(new WLTMenuItemUI()); //
		menuItem_add.setOpaque(true); //
		menuItem_add.setBackground(LookAndFeel.defaultShadeColor1);
		menuItem_add.setPreferredSize(new Dimension(120, 25)); //
		menuItem_add.setIcon(UIUtil.getImage("office_199.gif"));

		menuItem_remove.setUI(new WLTMenuItemUI()); //
		menuItem_remove.setOpaque(true); //
		menuItem_remove.setBackground(LookAndFeel.defaultShadeColor1);
		menuItem_remove.setPreferredSize(new Dimension(120, 25)); //
		menuItem_remove.setIcon(UIUtil.getImage("office_144.gif"));

		menuItem_add.addActionListener(this); //
		menuItem_remove.addActionListener(this); //

		popMenu.add(menuItem_add); //
		popMenu.add(menuItem_remove); //
	}

	/**
	 * 构造我的常用功能库面板!
	 * @param _firstLevelNames
	 * @return
	 */
	private JPanel getMyShortCutPanel(String[] _firstLevelNames) {
		//先从数据库中找到我的所有数据!!
		try {
			String str_loginUserId = ClientEnvironment.getCurrLoginUserVO().getId(); //

			//先找出我的所有角色!!
			String[] str_myallroles = UIUtil.getStringArrayFirstColByDS(null, "select rolecode from v_pub_user_role_1 where userid='" + str_loginUserId + "'"); //
			String[] str_commrole = new String[] { "一般用户", "一般员工", "所有员工", "所有用户" }; //
			String[] str_span = new String[str_myallroles.length + str_commrole.length]; //
			System.arraycopy(str_myallroles, 0, str_span, 0, str_myallroles.length); //
			System.arraycopy(str_commrole, 0, str_span, str_myallroles.length, str_commrole.length); //拷贝!!

			//去快捷方式表中找到我的所有快捷方式!!包括角色匹配与人员直接匹配的!!
			HashVO[] hvs_allMenu = UIUtil.getHashVoArrayByDS(null, "select * from pub_user_shortcut where (shorttype='角色' and userid in (" + tbUtil.getInCondition(str_span) + ")) or ((shorttype='人员' or shorttype is null ) and userid='" + str_loginUserId + "')");

			ArrayList al_matchedVO = new ArrayList(); //虽然我有一些快捷方式,但必须该快捷方式在我的目前范围之内!!理论上这个计算不要做,但是可能权限发生变动,导致我曾经的快捷方式,现在却没有权限，或功能点都根本不在了!所以要有这样一个计算,更健壮!
			for (int i = 0; i < allNodeVOs.length; i++) {
				boolean isMatch = false; //
				for (int j = 0; j < hvs_allMenu.length; j++) {
					if (hvs_allMenu[j].getStringValue("menuid").equals(allNodeVOs[i].getStringValue("id"))) {
						isMatch = true; //
						break; //
					}
				}
				if (isMatch) {
					al_matchedVO.add(allNodeVOs[i]); //加入
				}
			}
			HashVO[] hvs = (HashVO[]) al_matchedVO.toArray(new HashVO[0]); // 
			return getOneLevelAllBtnPanel(hvs, null, _firstLevelNames); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			JPanel panel = new JPanel(new FlowLayout()); //
			panel.add(new JLabel("加载我的常用功能时发生异常:<" + ex.getClass().getName() + ">" + ex.getMessage())); //
			return panel; //
		}

	}

	/**
	 * 取得某个一级模板下的所有按钮!!
	 * @param _levelName
	 * @return
	 */
	private JPanel getOneLevelAllBtnPanel(String _levelName, Color _bgcolor) {
		ArrayList al_hvs = new ArrayList(); //
		for (int i = 0; i < allNodeVOs.length; i++) {
			String str_name = allNodeVOs[i].getStringValue("$TreePath"); //
			if (str_name.startsWith(_levelName)) {
				al_hvs.add(allNodeVOs[i]); //
			}
		}
		HashVO[] hvs = (HashVO[]) al_hvs.toArray(new HashVO[0]); ////
		return getOneLevelAllBtnPanel(hvs, _bgcolor, null); //
	}

	/**
	 * 取得某一层,即将所有按钮弄出来!
	 * @param _hvs
	 * @param _bgcolor
	 * @param _firstlevelNames
	 * @return
	 */
	private JPanel getOneLevelAllBtnPanel(HashVO[] _hvs, Color _bgcolor, String[] _firstlevelNames) {
		JPanel contentPanel = new JPanel(); //
		contentPanel.setOpaque(false); //透明!!
		//contentPanel.setBackground(Color.WHITE);  //
		contentPanel.setLayout(null); //
		int li_onewidth = 85; //每个按钮的宽度,许多人觉得原来的按钮太大!!
		int li_oneheight = 55; //每个按钮的高度
		int li_onecount = 7; //一行几个按钮
		int li_x_space = 45; //横向每个按钮之间的距离!
		int li_y_space = 30; //纵向每个按钮之间的距离!
		int li_x = 0, li_y = 0; //
		boolean isShortCut = (_bgcolor == null ? true : false); //是否是常用功能,如果是,则提示,右键弹出菜单都不一样!

		for (int i = 0; i < _hvs.length; i++) {
			String str_id = _hvs[i].getStringValue("id"); //
			String str_name = _hvs[i].getStringValue("name"); //
			String str_path = _hvs[i].getStringValue("$TreePath"); //
			String str_imgName = _hvs[i].getStringValue("icon"); //
			if (str_imgName == null) {
				str_imgName = "office_054.gif"; //
			}
			int li_div = i / li_onecount; //商,一行摆8个按钮!!
			int li_mod = i % li_onecount; //余数
			//li_x = 10 + li_mod * 100 + (li_mod * 12); //
			//li_y = 10 + li_div * 70 + (li_div * 12); //
			li_x = 20 + li_mod * (li_onewidth + li_x_space); //
			li_y = 20 + li_div * (li_oneheight + li_y_space); //

			Color itemBgColor = _bgcolor; //
			if (itemBgColor == null) {
				if (_firstlevelNames != null) {
					for (int j = 0; j < _firstlevelNames.length; j++) { //遍历所有第一列!
						if (str_path.startsWith(_firstlevelNames[j])) {
							itemBgColor = colors[j % colors.length]; //// 
							break; //
						}
					}
				}
			}

			JPanel itemPanel = getItemPanel(str_id, str_imgName, str_name, (i + 1) + "." + str_path, itemBgColor, isShortCut); //取得某一个按钮的面板!!!//( +
			itemPanel.setBounds(li_x, li_y, li_onewidth, li_oneheight); //
			contentPanel.add(itemPanel); //
		}
		int li_all_width = (li_onewidth + li_x_space) * li_onecount + 50; //
		int li_all_height = li_y + li_oneheight + li_y_space + 50; //
		contentPanel.setPreferredSize(new Dimension(li_all_width, li_all_height)); //
		//contentPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE)); //用于debug时看布局效果用

		JPanel containerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); //以前的按钮总是靠左,感觉不好,改成居中,就需要一个容器面板
		//containerPanel.setBorder(BorderFactory.createLineBorder(Color.RED)); //用于debug时看布局效果用
		containerPanel.setOpaque(false); //
		containerPanel.add(contentPanel); //
		containerPanel.setPreferredSize(new Dimension(li_all_width + 10, li_all_height + 10)); //

		JScrollPane scroll = new JScrollPane(containerPanel); //
		scroll.getVerticalScrollBar().setUnitIncrement(50); //
		scroll.getVerticalScrollBar().addAdjustmentListener(this); //
		scroll.setOpaque(false);//
		scroll.getViewport().setOpaque(false); //

		JPanel returnPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		returnPanel.add(scroll); //
		return returnPanel; //
	}

	private JPanel getNorthBtnPanel(String[] str_firstLevels) {
		ActionListener listener = new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onShowOneLevel((JButton) e.getSource()); //
			}
		};
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.LEFT, 3, 3)); //
		//panel.add(new WLTButton(""));  //
		panel.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 0)); //
		for (int i = 0; i < str_firstLevels.length; i++) {
			WLTButton btn = new WLTButton(str_firstLevels[i], WLTPanel.VERTICAL_LIGHT2); //
			btn.setPreferredSize(new Dimension((int) btn.getPreferredSize().getWidth(), 28)); //
			int li_index = i % colors.length; //
			btn.setBackground(colors[li_index]); //
			btn.setForeground(new Color(230, 230, 230)); //
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
			btn.addActionListener(listener); //
			panel.add(btn); //
		}
		return panel; //
	}

	protected void onShowOneLevel(JButton _btn) {
		String str_btntext = _btn.getText(); //
		if (popMenu != null) {
			popMenu.setVisible(false); //
		}
		cardLayout.show(toftPanel, str_btntext); //
	}

	//取得第一层模块的名称!!!
	private String[] getFirstLevelNodeNames() {
		LinkedHashSet hst = new LinkedHashSet(); //
		for (int i = 0; i < allNodeVOs.length; i++) {
			String str_path = allNodeVOs[i].getStringValue("$TreePath"); //
			String[] str_levels = tbUtil.split(str_path, "→"); //
			hst.add(str_levels[0].trim()); //
		}
		return (String[]) hst.toArray(new String[0]); //
	}

	/**
	 * 每一个按钮
	 * @param _id
	 * @param _imgName
	 * @param _text
	 * @return
	 */
	private JPanel getItemPanel(String _id, String _imgName, String _text, String _path, Color _color, boolean _isShortCut) {
		JPanel panel = new WLTPanel(WLTPanel.VERTICAL_LIGHT2, new BorderLayout(), _color, false); //VERTICAL_TOP_TO_BOTTOM
		panel.setBorder(border1); //
		panel.setCursor(cursor); //
		panel.putClientProperty("menuid", _id); //
		String str_path = tbUtil.replaceAll(_path, " ", ""); //
		panel.putClientProperty("$TreePath", str_path); //
		panel.putClientProperty("是否是常用功能", _isShortCut ? "Y" : "N"); //
		if (_isShortCut) {
			panel.setToolTipText("<html>" + str_path + "<br><font color=\"FF0000\">右键可以做【移除】操作!</font></html>"); //
		} else {
			panel.setToolTipText("<html>" + str_path + "<br><font color=\"FF0000\">右键可以做【添加】操作!</font></html>"); //
		}
		panel.addMouseListener(this); //

		ImageIcon imgIcon = UIUtil.getImage(_imgName); //
		//imgIcon = new ImageIcon(tbUtil.getImageScale(imgIcon.getImage(), 25, 25));  //将图片拉伸成32*32,因为小图片不好看??但拉伸后透明效果没了!!!所以暂时不处理,以后应该想其他办法!!
		JLabel imgLabel = new MyAlphaLabel(imgIcon); //图片!!!
		panel.add(imgLabel, BorderLayout.CENTER); //居中!!
		JLabel textLabel = new JLabel(_text, JLabel.CENTER); //
		textLabel.setForeground(textColor); //
		panel.add(textLabel, BorderLayout.SOUTH); ////

		panel.putClientProperty("ImgLabel", imgLabel); //绑定的Label
		return panel; //
	}

	public void mouseClicked(MouseEvent _event) {
		JPanel itemPanel = (JPanel) _event.getSource(); //
		String str_menuid = (String) itemPanel.getClientProperty("menuid"); //
		String str_path = (String) itemPanel.getClientProperty("$TreePath"); //

		String str_isShortCut = (String) itemPanel.getClientProperty("是否是常用功能"); //
		if (_event.getButton() == MouseEvent.BUTTON3) { //如果点击的是右键!则显示弹出菜单!!
			if ("Y".equals(str_isShortCut)) {
				menuItem_add.setVisible(false); //
				menuItem_remove.setVisible(true); //

				menuItem_remove.putClientProperty("menuid", str_menuid); //
				menuItem_remove.putClientProperty("$TreePath", str_path); //
			} else {
				menuItem_add.setVisible(true); //
				menuItem_remove.setVisible(false); //
				menuItem_add.putClientProperty("menuid", str_menuid); //
				menuItem_add.putClientProperty("$TreePath", str_path); //
			}
			popMenu.show(itemPanel, _event.getX(), _event.getY()); //
		} else {
			itemPanel.setCursor(cursor_wait); //
			DeskTopPanel.getDeskTopPanel().openAppMainFrameWindowById(str_menuid); //
		}
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == menuItem_add) { //如果是加入
			String str_menuId = (String) menuItem_add.getClientProperty("menuid"); //
			String str_treePath = (String) menuItem_add.getClientProperty("$TreePath"); //
			onAddToMyShortCut(str_menuId, str_treePath); //加入到我的常用功能中!!
		} else if (_event.getSource() == menuItem_remove) { //如果是移除?
			String str_menuId = (String) menuItem_remove.getClientProperty("menuid"); //
			String str_treePath = (String) menuItem_remove.getClientProperty("$TreePath"); //
			onRemoveFromMyShortCut(str_menuId, str_treePath); //从我的常用功能中移除!!
		}
	}

	//加入到我的常用功能中!!
	private void onAddToMyShortCut(String _menuId, String _treePath) {
		try {
			String str_loginUserId = ClientEnvironment.getCurrLoginUserVO().getId(); //
			String str_count = UIUtil.getStringValueByDS(null, "select count(*) c1 from pub_user_shortcut where userid='" + str_loginUserId + "' and menuid='" + _menuId + "' and (shorttype='人员' or shorttype is null)"); //
			if (Integer.parseInt(str_count) > 0) {
				MessageBox.show(this, "该功能已经加入我的常用功能中了,没有必要重复添加!"); //
				return; //
			}
			InsertSQLBuilder isql = new InsertSQLBuilder("pub_user_shortcut"); //
			isql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_PUB_USER_SHORTCUT")); //主键
			isql.putFieldValue("userid", str_loginUserId); //
			isql.putFieldValue("menuid", _menuId); //
			isql.putFieldValue("shorttype", "人员"); //
			UIUtil.executeUpdateByDS(null, isql); //

			myShortCutPanel.removeAll(); //
			myShortCutPanel.setLayout(new BorderLayout()); //
			myShortCutPanel.add(getMyShortCutPanel(str_firstLevels)); //先加入所有功能!!
			myShortCutPanel.updateUI(); //
			MessageBox.show(this, "将功能点【" + _treePath + "】添加至我的常用功能中成功!"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	//从我的常用功能中移除!!
	private void onRemoveFromMyShortCut(String _menuId, String _treePath) {
		try {
			if (!MessageBox.confirm(this, "你是否真的想将该功能点【" + _treePath + "】从常用功能库中移除吗?")) {
				return;
			}
			String str_loginUserId = ClientEnvironment.getCurrLoginUserVO().getId(); //
			String str_count = UIUtil.getStringValueByDS(null, "select count(*) c1 from pub_user_shortcut where userid='" + str_loginUserId + "' and menuid='" + _menuId + "' and (shorttype='人员' or shorttype is null)"); //
			if (Integer.parseInt(str_count) <= 0) { //如果没找到我的,则说明是与角色关联的!
				MessageBox.show(this, "这是属于某个角色的常用功能(你正好具有该角色),只有管理员才能移除!"); //
				return; //
			}
			UIUtil.executeUpdateByDS(null, "delete from pub_user_shortcut where userid='" + str_loginUserId + "' and menuid='" + _menuId + "' and (shorttype='人员' or shorttype is null)"); //
			myShortCutPanel.removeAll(); //
			myShortCutPanel.setLayout(new BorderLayout()); //
			myShortCutPanel.add(getMyShortCutPanel(str_firstLevels)); //先加入所有功能!!
			myShortCutPanel.updateUI(); //
			MessageBox.show(this, "从我的常用功能中移除成功!!\r\n但请注意,如果管理员将其作为某个角色的常用功能,而你又具体有该角色,则仍然会出现!!\r\n即管理员设置的[角色类常用功能]是不能被一般用户移除的!"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	public void mouseEntered(MouseEvent e) {
		JPanel panel = (JPanel) e.getSource(); //
		panel.setCursor(cursor); //
		panel.setBorder(border2); //

		JLabel label = (JLabel) panel.getClientProperty("ImgLabel"); //
		label.putClientProperty("isEnter", "Y"); //
	}

	public void mouseExited(MouseEvent e) {
		JPanel panel = (JPanel) e.getSource(); //
		panel.setBorder(border1); //

		JLabel label = (JLabel) panel.getClientProperty("ImgLabel"); //
		label.putClientProperty("isEnter", "N"); //
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void adjustmentValueChanged(AdjustmentEvent e) {
		this.revalidate(); //
		this.repaint(); //
	}

	class MyAlphaLabel extends JLabel {

		private static final long serialVersionUID = -3607779567980034624L;

		public MyAlphaLabel(Icon image) {
			super(image);
		}

		@Override
		public void paint(Graphics g) {
			String str_isEnter = (String) this.getClientProperty("isEnter"); //
			if ("Y".equals(str_isEnter)) {
				Graphics2D g2d = (Graphics2D) g;
				Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f); //进入时透明!
				g2d.setComposite(alphaComp);
				super.paint(g2d);
			} else {
				Graphics2D g2d = (Graphics2D) g;
				Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f); //不透明!!!!
				g2d.setComposite(alphaComp);
				super.paint(g2d);
			}
		}

	}

}
