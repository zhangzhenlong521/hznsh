package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillOfficePanel;

/**
 * 关键类!!就是整个系统多页签打开任意一个功能点时,将每个AbstractworkPanel包装一下的面板,所谓包装就是上面加一个导航说明栏,即当前位置：...->...->,右边还有帮助关闭等!!
 * 右边工作区中每个页签中的Panel,该Panel其实就是一个"当前位置的说明"加上一个WorkTabbedPanel
 * @author xch
 * 
 */
public class WorkTabbedPanel extends JPanel {

	private static final long serialVersionUID = -7388887270490543022L;
	private String str_menuid; //
	private String subject = null; //
	private String title = null; //
	private Icon icon;
	private WLTButton btn_help, btn_closeme; //
	private AbstractWorkPanel workPanel = null;
	private DeskTopPanel deskTopPanel = null;
	private WLTTabbedPane matherTabbedPane = null; // 母体
	private JFrame matherFrame = null; // 母体Frame
	private TBUtil tbutil = null;
	boolean maxed = false;
	boolean northPanelVisible = true;
	private JLabel label; //显示当前路径的label
	private JPanel back;

	public WorkTabbedPanel(String _menuid, String _subject, String _title, Icon _icon, AbstractWorkPanel _workPanel, Boolean _isextend, Integer _height, boolean haveNothPanel) {
		northPanelVisible = haveNothPanel;
		this.str_menuid = _menuid; //
		this.subject = _subject;
		this.title = _title;
		this.icon = _icon; //
		this.workPanel = _workPanel;
		this.setLayout(new BorderLayout());
		this.setBorder(BorderFactory.createEmptyBorder()); //
		JPanel panel_content = new JPanel(new BorderLayout(0, 0)); //
		if (northPanelVisible) {
			panel_content.add(getNorthPanel(), BorderLayout.NORTH);
		}
		if (_isextend != null && _isextend.booleanValue()) {
			_workPanel.setBorder(BorderFactory.createEmptyBorder()); //
			JPanel panel_sss = new JPanel(new BorderLayout()); //
			panel_sss.setPreferredSize(new Dimension(700, (_height == null ? 700 : _height.intValue()))); //
			panel_sss.add(_workPanel); //
			panel_sss.setBorder(BorderFactory.createEmptyBorder()); //
			JScrollPane scrollPanel = new JScrollPane(panel_sss);
			scrollPanel.getVerticalScrollBar().setPreferredSize(new Dimension(12, 100)); //
			scrollPanel.setBorder(BorderFactory.createEmptyBorder()); //
			panel_content.add(scrollPanel, BorderLayout.CENTER);
		} else {
			panel_content.add(_workPanel, BorderLayout.CENTER);
		}

		panel_content.setPreferredSize(new Dimension(0, 0)); //
		JScrollPane scrollPanel = new JScrollPane(panel_content);
		scrollPanel.getViewport().setPreferredSize(new Dimension(0, 0)); //
		scrollPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); //
		scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER); //
		this.add(scrollPanel); //
	}

	public WorkTabbedPanel(String _menuid, String _subject, String _title, Icon _icon, AbstractWorkPanel _workPanel, Boolean _isextend, Integer _height) {
		this(_menuid, _subject, _title, _icon, _workPanel, _isextend, _height, true);
	}

	public AbstractWorkPanel getWorkPanel() {
		return workPanel;
	}

	public JComponent getBtn_closeMe() {
		return back;
	}

	/**
	 * 就是上面一行说明!!!
	 * @return
	 */
	private JPanel getNorthPanel() {
		label = new JLabel(title);
		label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		label.setOpaque(false);
		// label.setForeground(new java.awt.Color(254, 115, 17)); //桔黄色
		label.setForeground(LookAndFeel.appLabelHighlightFontcolor);
		if (ClientEnvironment.isAdmin()) {
			label.setToolTipText("类名:" + workPanel.getClass().getName()); //
		}

		//帮助按钮!!!
		btn_help = new WLTButton(UIUtil.getImage("office_030.gif")); //
		btn_help.setBorder(BorderFactory.createEmptyBorder()); //
		btn_help.setRightBtnShowInfo(false); //
		btn_help.setFocusable(false);
		btn_help.setCursor(new Cursor(Cursor.HAND_CURSOR)); //手形图标效果感觉更好些!【xch/2012-02-27】
		btn_help.setPreferredSize(new Dimension(17, 17)); //
		if (ClientEnvironment.isAdmin()) {
			btn_help.setToolTipText("查看帮助!Shift+点击可以查看加载类名,Ctrl+点击可查看功能点配置"); //
		} else {
			btn_help.setToolTipText("查看帮助"); //普通用户登录提示,根据经验客户肯定不会接受上面一堆提示!! xch(2012-02-23)
		}
		btn_help.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.isShiftDown()) {
					MessageBox.show(btn_help, "实际加载的类名:[" + workPanel.getClass().getName() + "]"); //
				} else if (e.isControlDown() && ClientEnvironment.isAdmin()) {//管理员身份登陆才可配置 [郝明2012-12-05]
					showMenuInfo(); //
				} else {
					onHelp();
				}
			}
		});

		//关闭按钮!!!
		//		btn_closeme = new WLTButton(UIUtil.getImage("closewin.gif")); // closewin.gif
		btn_closeme = new WLTButton(new ImageIcon(TBUtil.getTBUtil().getImageScale(UIUtil.getImage("zt_031.gif").getImage(), 13, 13))); // closewin.gif
		btn_closeme.setCursor(new Cursor(Cursor.HAND_CURSOR));
		//		private ImageIcon closeImg = new ImageIcon(TBUtil.getTBUtil().getImageScale(UIUtil.getImage("zt_031.gif").getImage(), 13, 13));
		btn_closeme.setFocusable(false);
		btn_closeme.setPreferredSize(new Dimension(17, 17)); //		
		btn_closeme.setToolTipText("关闭本页"); //
		btn_closeme.setRightBtnShowInfo(false); //右边不显示默认的info
		btn_closeme.addCustActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeMe(); //
			}
		});

		JPanel panel_east = new JPanel(new FlowLayout(FlowLayout.RIGHT, 1, 0)); //
		panel_east.setOpaque(false);
		panel_east.setBackground(LookAndFeel.systabbedPanelSelectedBackground); // 背景
		panel_east.add(btn_help);
		//		panel_east.add(btn_closeme);
		panel_east.setPreferredSize(new Dimension(100, 19)); //
		btn_help.setVisible(true); //
		//		btn_closeme.setVisible(true); //

		WLTPanel panel = new WLTPanel(BackGroundDrawingUtil.VERTICAL_TOP_TO_BOTTOM, LookAndFeel.systabbedPanelSelectedBackground, false); // 颜色设成系统页签选中时的颜色!!!这样就自然形成一体,而不是以前的那种像是断开来的感觉!!!

		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(2000, 19)); //
		panel.add(label, BorderLayout.CENTER);
		panel.add(panel_east, BorderLayout.EAST); //
		panel.setBorder(BorderFactory.createEmptyBorder()); //
		back = getItemPanel("返 回", new Color(9, 70, 160));
		back.setCursor(new Cursor(Cursor.HAND_CURSOR));
		back.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				back.putClientProperty("focus", "N");
				back.updateUI();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				back.putClientProperty("focus", "Y");
				back.updateUI();
			}
		});
		back.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		back.setPreferredSize(new Dimension(65, 19));
		panel.add(back, BorderLayout.WEST);
		return panel;
	}

	//得到返回的按钮
	private JPanel getItemPanel(final String _text, final Color _color) {
		JPanel panel = new JPanel(new BorderLayout()) {
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				String focus = (String) this.getClientProperty("focus");
				GeneralPath path = new GeneralPath();
				path.moveTo(15, 1);
				path.lineTo(getWidth() - 1, 1);
				path.lineTo(getWidth() - 1, getHeight() - 1);
				path.lineTo(15, getHeight() - 1);
				path.lineTo(3, getHeight() / 2);

				path.lineTo(15, 1);
				Color color = null;
				Color color_end = null;
				if ("Y".equals(focus)) {
					color = new Color(167, 191, 136);
					color_end = new Color(197,214,158);
				} else {
					color = new Color(157, 181, 126);
					color_end = new Color(187,204,138);
				}
				LinearGradientPaint paint = new LinearGradientPaint(0, this.getHeight() / 2, 0, getHeight(), new float[] { 0.0f, 0.01f, 1f }, new Color[] {color_end, color,color_end});
				g2d.setPaint(paint);
				g2d.fill(path);
				g2d.draw(path);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				g2d.setColor(new Color(9, 70, 160));
				g2d.drawString(_text, getWidth() / 2 - 8, getHeight() - 5);
			}
		};
		return panel;
	}

	protected void showMenuInfo() {
		MenuConfigDialog dialog = new MenuConfigDialog(this, str_menuid); //
		dialog.btn_save.setEnabled(false);
		dialog.btn_confirm.setEnabled(false); //
		dialog.setVisible(true); //
	}

	public DeskTopPanel getDeskTopPanel() {
		return deskTopPanel;
	}

	public void setDeskTopPanel(DeskTopPanel deskTopPanel) {
		this.deskTopPanel = deskTopPanel;
	}

	public WorkTabbedPanel deepClone() {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(buf);
			out.writeObject(this);
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
			return (WorkTabbedPanel) in.readObject();
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	private TBUtil getTBUtil() {
		if (tbutil == null) {
			tbutil = new TBUtil();
		}
		return tbutil;
	}

	/**
	 * 点击帮助查看!!
	 */
	private void onHelp() {
		try {
			if (ClientEnvironment.isAdmin()) {
				//功能点主类追加 setHelp()方法,返回String帮助信息 【杨科/2013-02-26】
				//以前的逻辑是: A 在各RegisterMenu.xml中找功能点主类,若有,找help属性值,若有,找该包下help.txt,若有,找help属性值相应帮助信息
				//            B pub_menu表找word帮助文件名
				//            远程调用会同时返回A B值
				String helpinfo = null;
				Method[] method = workPanel.getClass().getMethods();
				for(int i=0;i<method.length;i++){
					if(method[i].getName().equals("setHelp")){
						helpinfo = (String) method[i].invoke(workPanel, null);
					}
				}
				
				if(helpinfo!=null&&!helpinfo.trim().equals("")){
					MessageBox.show(this, helpinfo.toString());
					return;
				}
			}
			
			String str_thisClsName = workPanel.getClass().getName(); //这个加载类的名称!!!
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			String[] str_helpInfo = service.getMenuHelpInfo(str_menuid, str_thisClsName); //帮助信息,后来增加的功能,即先从help文件中找关键帮助,后再找word文件!!【xch/2012-02-27】
			String str_helpText = str_helpInfo[0]; //
			if (str_helpText != null) {
				//Container _parentContainer, Object _msg, String _title, int _msgType, Object[] _options, int _initValue, int _width, int _height, boolean _isCanCopyText
				int li_return = MessageBox.showOptionDialog(this, str_helpText, "快速帮助查看", WLTConstants.MESSAGE_QUESTION, new String[] { " 确 定 ", "查看Word帮助" }, 0, 800, 350, false, false); //
				if (li_return == 1) { //如果点击的【查看Word帮助】,则再次调用打开Word文件!!!
					openWordFileHelp(str_helpInfo[1]); //
				}
			} else { //如果根本就没有定义help信息,则兼容以前的机制,直接打开Word!!
				openWordFileHelp(str_helpInfo[1]); //打开Word帮助文件!!!
			}
		} catch (Exception e1) {
			MessageBox.showException(this, e1); //
		}
	}

	/**
	 * 查看帮助增加直接帮助文本机制后,为了封装方便,将打开Word文件弄成一个方法!这样代码结构更合理！【xch/2012-02-27】
	 * @param filename
	 */
	private void openWordFileHelp(String filename) {
		if (filename == null || filename.trim().equals("")) { //
			MessageBox.show(this, "该功能点没有定认Word帮助文档,请在菜单配置中定义!");
			return; //
		}
		/***
		 * Gwang 2012-09-17修改
		 * 将系统中的帮助文档固定放在webroot的help目录中[E:\TomCat5.5.23\webapps\icam\help\doc] 
		 * 否则还要手动复制过去增加系统部署时的复杂度, 特别是在产品安装盘时
		 */
		String helpPath = System.getProperty("APP_DEPLOYPATH") + "help/doc/";

		String openType = getTBUtil().getSysOptionStringValue("帮助文档是否可编辑", "N"); //系统默认点击帮助文档为千航打开，不可编辑。
		if ("N".equals(openType)) {
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
			officeVO.setSubdir(helpPath);
			officeVO.setAbsoluteSeverDir(true);
			final BillOfficePanel pane = new BillOfficePanel(filename, officeVO); //使用Office面板打开,因为后来遇到需
			BillDialog dialog = new BillDialog(this) { //关闭对话框时，关千航控件。
				public boolean beforeWindowClosed() {
					pane.getWebBrowser().executeScript("swingCall('closedoc');");
					return true;
				}
			};
			dialog.getContentPane().add(pane);
			dialog.maxToScreenSizeBy1280AndLocationCenter();
			dialog.setTitle(subject + "-帮助文档");
			dialog.setVisible(true);
		} else {
			UIUtil.openRemoteServerFile("realdir", helpPath + filename); // //			
		}

	}

	private void closeMe() {
		try {
			workPanel.beforeDispose(); // 调用一下工作面板的关闭前的操作,比如有时需要停止掉一些线程!!!
			getDeskTopPanel().closeSelfTab(); //
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WLTTabbedPane getMatherTabbedPane() {
		return matherTabbedPane;
	}

	public void setMatherTabbedPane(WLTTabbedPane matherTabbedPane) {
		this.matherTabbedPane = matherTabbedPane;
	}

	public boolean isMaxed() {
		return maxed;
	}

	public JFrame getMatherFrame() {
		return matherFrame;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String _title) {
		title = _title;
		label.setText(title);
	}
}
