package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.sysapp.login.DeskTopNewGroupVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPanelUI;

/**
 * 首页! 关键类!!! 以前是分组方式,但不够好看,新的机制是仿照网页,有头版图片,有滚动新闻! 每个分组框改成左上角有一个图片!显得像个网页!!!
 * 
 * @author xch
 * 
 */
public class IndexPanel extends JPanel implements ItemListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DeskTopNewGroupVO[] allTaskVOs = null; // 所有的数据
	private DeskTopPanel deskTopPanel = null; //
	private TBUtil tbUtil = new TBUtil(); //

	public IndexPanel(DeskTopPanel _deskTopPanel) {
		try {
			this.deskTopPanel = _deskTopPanel; //
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			allTaskVOs = service.getDeskTopNewGroupVOs(ClientEnvironment.getCurrLoginUserVO().getCode()); // 先取得所有数据!!!
			if (tbUtil.getSysOptionBooleanValue("公告信息是否启用大BOM图", false)) {//默认为fasle  20180709 袁江晓修改 之前提交的有问题
				initialize2();
			} else {
				initialize();
			}
			//
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 构造页面!!!  此为冯帅新添加的类型，但是尽量不要更改旧的逻辑
	 */
	private void initialize2() throws Exception {
		JPanel realContentPanel = new JPanel(); //
		realContentPanel.setFocusable(true); // 可点击,否则按上下键不能滚动
		realContentPanel.setOpaque(false); // 透明!!!
		realContentPanel.setLayout(null); // //绝对值总局!以前层层嵌套的并不好,因为有的强行拖宽效果并不好!
		// 而且最后导致了滚动条效果出了问题!。.setLayout(null);
		int li_space = 20; // 各个组之间的纵向间距,以前是10,总觉得堆在一起!
		int li_maxheigth = 0; //
		int li_yy_start = 0; //
		// 是否显示系统消息,即有最上面两个框(滚动图片框与滚动新闻框)
		boolean isShowSysMsg = tbUtil.getSysOptionBooleanValue("是否显示滚动消息", true);
		if (isShowSysMsg) { // 如果有滚动图片与滚动新闻,则在最上面显示两个滚动框,一个是滚动图片,一个是滚动新闻,这些消息都在平台表中配置的!!
			// 滚动图片
			IndexRollImagePanel rollImgPanel = new IndexRollImagePanel(); // 滚动图片面板
			rollImgPanel.setBounds(565, 0, 550, 225); //
			realContentPanel.add(rollImgPanel); //

			// 滚动文字!
			IndexRollMsgPanel rollMsgPanel = new IndexRollMsgPanel(); // 滚动文字
			// rollMsgPanel.setBorder(BorderFactory.createLineBorder(new
			// Color(220, 220, 220), 1)); //
			rollMsgPanel.setBounds(565, 225, 550, 225); //
			realContentPanel.add(rollMsgPanel); //

			li_yy_start = 450 + 10; //
		}

		// 遍历所有的数据!!!要区分[半列/全列]
		int li_xx_start = 0; // X坐标的起始位置!!
		boolean isFirstHalft = false; // 记录该循环中的面板是否是第一个半列
		for (int i = 0; i < allTaskVOs.length; i++) { // 遍历!各个任务
			String str_dataType = allTaskVOs[i].getDefineVO().getDatatype(); // 数据类型!!
			String str_viewCols = allTaskVOs[i].getDefineVO().getViewcols(); // 显示多少列!!!
			String str_capimg = allTaskVOs[i].getDefineVO().getCapimg(); // 帽子图片!
			String templetcode = allTaskVOs[i].getDefineVO().getTempletcode();
			String custclass = allTaskVOs[i].getDefineVO().getDatabuildername(); // 自定义类【zzl  2017/3/31】

			int li_width = 0; // 宽度
			if ("全列".equals(str_viewCols)) {
				li_width = 1100; //
			} else { // 半列!!
				li_width = 550; //
			}
			JPanel itemPanel = null; //
			if (templetcode != null && str_dataType.endsWith("Bom图") && custclass != null) {//zzl[2017/3/29]
				itemPanel = new IndexCustPanel(deskTopPanel, allTaskVOs[i]);
				//				itemPanel = new IndexRiskOfMapWorkPanel();
				itemPanel.setBounds(0, 0, 550, 458);
			} else {
				if (str_dataType != null && str_dataType.endsWith("图")) {
					itemPanel = new IndexChartPanel(deskTopPanel, allTaskVOs[i]); // 如果是图形的!
				} else if (str_dataType != null && str_dataType.endsWith("板")) { // 首页追加自定义面板
					// 【杨科/2012-11-12】
					String str_clasName = allTaskVOs[i].getDefineVO().getDatabuildername();
					if (str_clasName != null && !str_clasName.equals("")) {
						try {
							itemPanel = (JPanel) Class.forName(str_clasName).newInstance();
						} catch (Exception e) {
							continue;
						}
					}
				} else {
					itemPanel = new IndexItemTaskPanel(deskTopPanel, allTaskVOs[i], li_width); // 如果是文字的!
				}
				// itemPanel.setBorder(BorderFactory.createLineBorder(new
				// Color(220, 220, 220), 1)); //
				itemPanel.setBounds(li_xx_start, li_yy_start, li_width, 210); //
			}
			if (li_xx_start == 0 && li_width == 550) { //
				isFirstHalft = true; //
			} else {
				isFirstHalft = false; //
			}
			if (str_capimg != null && !str_capimg.trim().equals("")) { // 如果有帽子!则强行另起一行先画上帽子,然后再强行另起一行!
				if (i != 0) {
					li_yy_start = li_yy_start + 210 + li_space; // 先另起一行!

				}
				JLabel labelpp = null; //
				ImageIcon capImg = UIUtil.getImageFromServer("/images/" + str_capimg); // .getImage()
				if (capImg != null) { // 如果找到,则将其大小强行填满为735*80
					if (capImg.getIconWidth() != 735 || capImg.getIconHeight() != 90) { // 如果美工做的图片大小不是我想要的735*90,则进行拉伸处理!
						capImg = new ImageIcon(tbUtil.getImageScale(capImg.getImage(), 735, 90)); //
					}
					labelpp = new JLabel(capImg); //
				} else {
					labelpp = new JLabel("加载图片[%WebRoot%/images/" + str_capimg + "]失败,可能没有该图片[735*90]!"); //
				}
				labelpp.setToolTipText(str_capimg + "[735*90]"); //
				labelpp.setBounds(0, li_yy_start, 735, 90); // 图片高90,因这sohu,sina,163首页图片都是90
				realContentPanel.add(labelpp); //

				li_xx_start = 0; //
				li_yy_start = li_yy_start + 90 + li_space; // 图片高80
			} else {
				// X与Y坐标位置!!!
				if (i != 0) {
					if ("全列".equals(str_viewCols)) { // 如果是全列!
						li_xx_start = 0; //
						li_yy_start = li_yy_start + 210; //
					} else { // 如果是半列
						if (isFirstHalft) { // 真巧前面一个就是第一个半列,则是本条是第二个半列!
							li_xx_start = 560; // X是375,Y不变
						} else { // 也许前面是全列,或者是第二个半列,则我是另起一行!
							li_xx_start = 0; //
							li_yy_start = li_yy_start + 210; //
						}
					}
				}
			}

			realContentPanel.add(itemPanel); //
			li_maxheigth = li_yy_start + li_space; // 最高的地方
		}

		// 用户登录数!!!
		JLabel label_loginCount = new JLabel(getLoginCount()); //
		label_loginCount.setToolTipText("通过系统参数[首页底部说明]设置"); //
		label_loginCount.setForeground(new Color(2, 68, 152)); //
		label_loginCount.setBounds(465, li_maxheigth, 300, 20); // X的起始位置需要根据文字的实际多少反算!!
		li_maxheigth = li_maxheigth + 20; //
		realContentPanel.add(label_loginCount); //

		// 首页去版权所有 【杨科/2012-11-20】
		String year = UIUtil.getCurrDate().substring(0, 4);
		String copyright = "版权所有·" + System.getProperty("LICENSEDTO") + "  Copyright 1997-" + year + " all rights reserved";
		boolean isHaveCopyright = tbUtil.getSysOptionBooleanValue("是否显示Copyright", true);
		if (!isHaveCopyright) {
			copyright = "                  版权所有·" + System.getProperty("LICENSEDTO"); // 加空格为居中
		}

		// 版权所有
		JLabel label_copyright = new JLabel(copyright); //
		label_copyright.setForeground(new Color(2, 68, 152)); //
		label_copyright.setBounds(400, li_maxheigth, 600, 20); // X的起始位置需要根据文字的实际多少反算!!
		li_maxheigth = li_maxheigth + 20; //
		realContentPanel.add(label_copyright); //

		// 右边的图片栏,中铁建的王部长等客户就喜欢这个!
		int li_allWidth = 800; //
		String str_rightbarConf = tbUtil.getSysOptionStringValue("首页右边的图片", null); //
		if (str_rightbarConf != null && !str_rightbarConf.trim().equals("")) {
			JPanel rightPanel = getRightImageBarPanel(str_rightbarConf); //
			int li_rightWidth = (int) rightPanel.getPreferredSize().getWidth(); //
			int li_rightHeight = (int) rightPanel.getPreferredSize().getHeight(); //
			rightPanel.setBounds(750, 0, li_rightWidth, li_rightHeight); // 正好宽度是1000,即如果不要左边的菜单树,正好匹配1024的分辨率!!
			// 如果是1280的分辨率,则正好加上左边的菜单树,即是最完美的宽度设置!!
			realContentPanel.add(rightPanel); //
			if (li_rightHeight > li_maxheigth) {
				li_maxheigth = li_rightHeight;
			}
			li_allWidth = 750 + li_rightWidth + 5; //
		}

		if (tbUtil.getSysOptionBooleanValue("公告信息是否启用大BOM图", true)) {
			realContentPanel.setPreferredSize(new Dimension(1150, li_maxheigth + 30)); // 宽度与高度!!!
		} else {
			realContentPanel.setPreferredSize(new Dimension(li_allWidth, li_maxheigth + 30)); //宽度与高度!!!
		}

		// 总体布局!!!
		JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); //
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0)); //
		contentPanel.setOpaque(false); // 透明!!!
		contentPanel.setFocusable(true); // 可点击,否则按上下键不能滚动!!
		contentPanel.add(realContentPanel); //

		JPopupMenu popMenu = new JPopupMenu(); //
		JMenuItem menuItem = new JMenuItem("刷新首页", UIUtil.getImage("office_191.gif")); //
		menuItem.addActionListener(this); //
		popMenu.add(menuItem); //
		contentPanel.setComponentPopupMenu(popMenu); //

		JScrollPane scrollPanel = new JScrollPane(contentPanel); //
		scrollPanel.setFocusable(true); //
		scrollPanel.getVerticalScrollBar().setUnitIncrement(20); // 滚动时快点!!
		scrollPanel.setOpaque(false); //
		scrollPanel.getViewport().setOpaque(false); // 透明!!!

		this.setBackground(LookAndFeel.desktop_Background); // //
		this.setLayout(new BorderLayout()); //
		this.setUI(new WLTPanelUI(WLTPanel.HORIZONTAL_FROM_MIDDLE, false)); //
		this.add(scrollPanel, BorderLayout.CENTER); //

	}

	/**
	 * 构造页面!!!
	 */
	private void initialize() throws Exception {
		JPanel realContentPanel = new JPanel(); //
		realContentPanel.setFocusable(true); //可点击,否则按上下键不能滚动
		realContentPanel.setOpaque(false); //透明!!!
		realContentPanel.setLayout(null); ////绝对值总局!以前层层嵌套的并不好,因为有的强行拖宽效果并不好! 而且最后导致了滚动条效果出了问题!

		int li_space = 15; //各个组之间的纵向间距,以前是10,总觉得堆在一起!
		int li_maxheigth = 0; //
		int li_yy_start = 0; //
		//是否显示系统消息,即有最上面两个框(滚动图片框与滚动新闻框)
		boolean isShowSysMsg = tbUtil.getSysOptionBooleanValue("是否显示滚动消息", true);
		if (isShowSysMsg) { //如果有滚动图片与滚动新闻,则在最上面显示两个滚动框,一个是滚动图片,一个是滚动新闻,这些消息都在平台表中配置的!!
			//滚动图片
			IndexRollImagePanel rollImgPanel = new IndexRollImagePanel(); //滚动图片面板
			rollImgPanel.setBounds(0, 0, 360, 185); //
			realContentPanel.add(rollImgPanel); //

			//滚动文字!
			IndexRollMsgPanel rollMsgPanel = new IndexRollMsgPanel(); //滚动文字
			//			rollMsgPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1)); //
			rollMsgPanel.setBounds(375, 0, 360, 185); //
			realContentPanel.add(rollMsgPanel); //

			li_yy_start = 185 + 10; //
		}

		//遍历所有的数据!!!要区分[半列/全列]
		int li_xx_start = 0; //X坐标的起始位置!!
		boolean isFirstHalft = false; //记录该循环中的面板是否是第一个半列
		for (int i = 0; i < allTaskVOs.length; i++) { //遍历!各个任务
			String str_dataType = allTaskVOs[i].getDefineVO().getDatatype(); //数据类型!!
			String str_viewCols = allTaskVOs[i].getDefineVO().getViewcols(); //显示多少列!!!
			String str_capimg = allTaskVOs[i].getDefineVO().getCapimg(); //帽子图片!
			String str_descr = allTaskVOs[i].getDefineVO().getDescr(); //备注
			if (str_capimg != null && !str_capimg.trim().equals("")) { //如果有帽子!则强行另起一行先画上帽子,然后再强行另起一行!
				if (i != 0) {
					li_yy_start = li_yy_start + 210 + li_space; //先另起一行!
				}
				JLabel labelpp = null; //
				ImageIcon capImg = UIUtil.getImageFromServer("/images/" + str_capimg); //.getImage()
				if (capImg != null) { //如果找到,则将其大小强行填满为735*80
					if (capImg.getIconWidth() != 735 || capImg.getIconHeight() != 90) { //如果美工做的图片大小不是我想要的735*90,则进行拉伸处理!
						capImg = new ImageIcon(tbUtil.getImageScale(capImg.getImage(), 735, 90)); //
					}
					labelpp = new JLabel(capImg); //
				} else {
					labelpp = new JLabel("加载图片[%WebRoot%/images/" + str_capimg + "]失败,可能没有该图片[735*90]!"); //
				}
				labelpp.setToolTipText(str_capimg + "[735*90]"); //
				labelpp.setBounds(0, li_yy_start, 735, 90); //图片高90,因这sohu,sina,163首页图片都是90
				realContentPanel.add(labelpp); //

				li_xx_start = 0; //
				li_yy_start = li_yy_start + 90 + li_space; //图片高80
			} else {
				//X与Y坐标位置!!!
				if (i != 0) {
					if ("全列".equals(str_viewCols)) { //如果是全列!
						li_xx_start = 0; //
						li_yy_start = li_yy_start + 210 + li_space; //
					} else { //如果是半列
						if (isFirstHalft) { //真巧前面一个就是第一个半列,则是本条是第二个半列!
							li_xx_start = 375; //X是375,Y不变
						} else { //也许前面是全列,或者是第二个半列,则我是另起一行!
							li_xx_start = 0; //
							li_yy_start = li_yy_start + 210 + li_space; //
						}
					}
				}
			}

			int li_width = 0; //宽度
			if ("全列".equals(str_viewCols)) {
				li_width = 735; //
			} else { //半列!!
				li_width = 360; //
			}

			JPanel itemPanel = null; //
			if (str_dataType != null && str_dataType.endsWith("图")) {
				itemPanel = new IndexChartPanel(deskTopPanel, allTaskVOs[i]); //如果是图形的!
			} else if (str_dataType != null && str_dataType.endsWith("板")) { //首页追加自定义面板 【杨科/2012-11-12】
				String str_clasName = allTaskVOs[i].getDefineVO().getDatabuildername();
				if (str_clasName != null && !str_clasName.equals("")) {
					try {
						itemPanel = (JPanel) Class.forName(str_clasName).newInstance();
					} catch (Exception e) {
						continue;
					}
				}
			} else if(str_dataType != null && str_dataType.endsWith("滚动")){//zzl [2019-7-8],绩效系统信息提醒为空故写成滚动信息，看着不是那么单调
				itemPanel=new IndexChartPanel().getIndexGDPanel(itemPanel, allTaskVOs[i]); //zzl [2019-7-8] 如果是左右滚动
			}else{
				itemPanel = new IndexItemTaskPanel(deskTopPanel, allTaskVOs[i], li_width); //如果是文字的!
			}
			//			itemPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1)); //
			itemPanel.setBounds(li_xx_start, li_yy_start, li_width, 210); //
			if (li_xx_start == 0 && li_width == 360) { //
				isFirstHalft = true; //
			} else {
				isFirstHalft = false; //
			}

			realContentPanel.add(itemPanel); //
			li_maxheigth = li_yy_start + 210 + li_space; //最高的地方
		}

		//用户登录数!!!
		JLabel label_loginCount = new JLabel(getLoginCount()); //
		label_loginCount.setToolTipText("通过系统参数[首页底部说明]设置"); //
		label_loginCount.setForeground(new Color(2, 68, 152)); //
		label_loginCount.setBounds(265, li_maxheigth, 300, 20); //X的起始位置需要根据文字的实际多少反算!!
		li_maxheigth = li_maxheigth + 20; //
		realContentPanel.add(label_loginCount); //

		//首页去版权所有 【杨科/2012-11-20】
		String year = UIUtil.getCurrDate().substring(0, 4);
		String copyright = "版权所有·" + System.getProperty("LICENSEDTO") + "  Copyright 1997-" + year + " all rights reserved";
		boolean isHaveCopyright = tbUtil.getSysOptionBooleanValue("是否显示Copyright", true);
		if (!isHaveCopyright) {
			copyright = "                  版权所有·" + System.getProperty("LICENSEDTO"); //加空格为居中
		}

		//版权所有
		JLabel label_copyright = new JLabel(copyright); //
		label_copyright.setForeground(new Color(2, 68, 152)); //
		label_copyright.setBounds(200, li_maxheigth, 600, 20); //X的起始位置需要根据文字的实际多少反算!!
		li_maxheigth = li_maxheigth + 20; //
		realContentPanel.add(label_copyright); //

		//右边的图片栏,中铁建的王部长等客户就喜欢这个!
		int li_allWidth = 800; //
		String str_rightbarConf = tbUtil.getSysOptionStringValue("首页右边的图片", null); //
		if (str_rightbarConf != null && !str_rightbarConf.trim().equals("")) {
			JPanel rightPanel = getRightImageBarPanel(str_rightbarConf); //
			int li_rightWidth = (int) rightPanel.getPreferredSize().getWidth(); //
			int li_rightHeight = (int) rightPanel.getPreferredSize().getHeight(); //
			rightPanel.setBounds(750, 0, li_rightWidth, li_rightHeight); //正好宽度是1000,即如果不要左边的菜单树,正好匹配1024的分辨率!! 如果是1280的分辨率,则正好加上左边的菜单树,即是最完美的宽度设置!!
			realContentPanel.add(rightPanel); //
			if (li_rightHeight > li_maxheigth) {
				li_maxheigth = li_rightHeight;
			}
			li_allWidth = 750 + li_rightWidth + 5; //
		}
		realContentPanel.setPreferredSize(new Dimension(li_allWidth, li_maxheigth + 30)); //宽度与高度!!!

		//总体布局!!!
		JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); //
		contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0)); //
		contentPanel.setOpaque(false); //透明!!!
		contentPanel.setFocusable(true); //可点击,否则按上下键不能滚动!!
		contentPanel.add(realContentPanel); //

		JPopupMenu popMenu = new JPopupMenu(); //
		JMenuItem menuItem = new JMenuItem("刷新首页", UIUtil.getImage("office_191.gif")); //
		menuItem.addActionListener(this); //
		popMenu.add(menuItem); //
		contentPanel.setComponentPopupMenu(popMenu); //

		JScrollPane scrollPanel = new JScrollPane(contentPanel); //
		scrollPanel.setFocusable(true); //
		scrollPanel.getVerticalScrollBar().setUnitIncrement(20); //滚动时快点!!
		scrollPanel.setOpaque(false); //
		scrollPanel.getViewport().setOpaque(false); //透明!!!

		this.setBackground(LookAndFeel.desktop_Background); ////
		this.setLayout(new BorderLayout()); //
		this.setUI(new WLTPanelUI(WLTPanel.HORIZONTAL_FROM_MIDDLE, false)); //
		this.add(scrollPanel, BorderLayout.CENTER); //

	}

	public void actionPerformed(ActionEvent e) {
		deskTopPanel.refreshAllTaskGroup(); //
	}

	/**
	 * 有许多客户就是喜欢右边有些图片栏从上至下排列,感觉很像个网站!! 比中铁建和王部长就要求!
	 * 
	 * @return
	 */
	private JPanel getRightImageBarPanel(String _conf) {
		HashMap map = tbUtil.convertStrToMapByExpress(_conf, ";", "="); //
		JPanel panel = new JPanel(); //
		panel.setOpaque(false); // 透明!!
		panel.setLayout(null); //

		int li_imgwidth = 200, li_imgheight = 80; //
		String[] str_keys = (String[]) map.keySet().toArray(new String[0]); //
		int li_y = 0; //
		for (int i = 0; i < str_keys.length; i++) { //
			ImageIcon img = UIUtil.getImageFromServer("/images/" + str_keys[i]); //
			JButton btn = new JButton(); // 宽*高=250*100
			if (img == null) {
				btn.setText("加载图片[" + str_keys[i] + "]失败!"); //
			} else {
				// 强行改下大小
				if (img.getIconWidth() != li_imgwidth || img.getIconHeight() != li_imgheight) { // 如果美工做的图片大小不是指定大小,则强行拉伸一下!
					img = new ImageIcon(tbUtil.getImageScale(img.getImage(), li_imgwidth, li_imgheight)); //
				}
				btn.setIcon(img); //
			}
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onClickedRightBarImg((JButton) e.getSource()); //
				}

			}); //
			btn.setBorder(BorderFactory.createEmptyBorder()); //
			btn.setMargin(new Insets(0, 0, 0, 0)); //
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); //

			if (ClientEnvironment.isAdmin()) {
				if (i == 0) {
					btn.setToolTipText("<html>" + str_keys[i] + "=" + map.get(str_keys[i]) + "<br>通过系统参数[首页右边的图片]设置!<br>格式是[rg1.gif=451;rg2.gif=562;],key是图片名称,value是菜单id<br>图片必须放在WebRoot/images/目录下</html>"); //
				} else { //
					btn.setToolTipText(str_keys[i] + "=" + map.get(str_keys[i])); //
				}
			}

			btn.setBounds(0, li_y, li_imgwidth, li_imgheight); //
			btn.putClientProperty("menuid", (String) map.get(str_keys[i])); //
			panel.add(btn); //
			li_y = li_y + li_imgheight + 10; //
		}

		li_y = li_y + 10; //
		JComboBox comBox = new JComboBox(); //
		if (ClientEnvironment.isAdmin()) {
			comBox.setToolTipText("通过系统参数[首页相关链接]设置!"); //
		}
		comBox.setFocusable(false); //
		comBox.addItem(new ComBoxItemVO("相关链接", null, "相关链接")); //

		String str_links = tbUtil.getSysOptionStringValue("首页相关链接", ""); //
		if (!str_links.trim().equals("")) {
			HashMap linkmap = tbUtil.parseStrAsMap(str_links); //
			String[] str_linkkeys = (String[]) linkmap.keySet().toArray(new String[0]); //
			for (int i = 0; i < str_linkkeys.length; i++) {
				comBox.addItem(new ComBoxItemVO((String) linkmap.get(str_linkkeys[i]), null, str_linkkeys[i])); //
			}
		}
		comBox.setBounds(20, li_y, 180, 25); //
		comBox.addItemListener(this); //
		panel.add(comBox); //
		li_y = li_y + 30; //

		panel.setPreferredSize(new Dimension(li_imgwidth, li_y)); //
		return panel; //
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getStateChange() != ItemEvent.SELECTED) {
			return;
		}
		JComboBox comBox = (JComboBox) e.getSource(); //
		if (comBox.getSelectedIndex() == 0) {
			return; //
		}

		ComBoxItemVO itemVO = (ComBoxItemVO) comBox.getSelectedItem(); //
		String str_url = itemVO.getId(); //
		try {
			Runtime.getRuntime().exec("explorer.exe " + str_url); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 点击右边图片按钮,就是相当于打开一个功能菜单
	 * 
	 * @param _btn
	 */
	protected void onClickedRightBarImg(JButton _btn) {
		try {
			_btn.setCursor(new Cursor(Cursor.WAIT_CURSOR)); // 点击时图示要变成等待效果!!
			String str_menuid = (String) _btn.getClientProperty("menuid"); //
			deskTopPanel.openAppMainFrameWindowById(str_menuid); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		} finally {
			_btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
		}
	}

	private String getLoginCount() throws Exception {
		try {
			if ("Y".equalsIgnoreCase(System.getProperty("ISLOADRUNDERCALL"))) { //
				return ""; //
			}
			String str_text = tbUtil.getSysOptionStringValue("首页底部说明", "您是第【${TotalCount}】位访问者,共登录【${UserCount}】次"); //
			if (str_text.indexOf("${TotalCount}") >= 0 || str_text.indexOf("${UserCount}") >= 0) { // 如果有这两个数字
				SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
				String str_totalCount = service.setTotalLoginCount(); // 这两次远程访问要合并成一次!!!
				String str_userCount = service.setUserLoginCount(); //
				str_text = tbUtil.replaceAll(str_text, "${TotalCount}", str_totalCount); //
				str_text = tbUtil.replaceAll(str_text, "${UserCount}", str_userCount); //
			}

			return str_text; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return ""; //
		}
	}

}
