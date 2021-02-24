package cn.com.infostrategy.ui.sysapp.login2;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicPanelUI;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;

/**
 * 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.sysapp.login2.I_WLTTabbedPane 
 * @Description: 首页顶端动态切换导航面板。实现原有WLTTabPanel。
 * @author haoming
 * @date Mar 12, 2013 11:36:30 AM
 *
 */
public class I_WLTTabbedPane extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L; //

	private WLTPanel btnPanel; //
	private JLabel tablabel = null; //
	private JPanel toftPanel = new JPanel(); //层次面板
	private CardLayout cardLayout = new CardLayout(); //卡片布局

	private Color color_clicked = null;

	private Color color_notclicked = null; //
	private Color splitMoveLabelBackColor = new Color(19, 124, 172);
	private int li_count = 0; //
	private ArrayList al_btns = new ArrayList(); //
	private Vector vc_pages = new Vector(); //

	private int selectedIndex = 0;

	private Vector vc_mouseListeners = new Vector(); //
	private Vector vc_changeListeners = new Vector(); //
	private boolean show = true;
	private JSplitPane splitpane = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT); //整体面板，上下分割，上面是按钮，下面是办公区。
	private Timer focusMoveTimer;
	private int splitDividerLocation = 95;//默认split的大小
	private JPopupMenu pop;
	private JPanel topMainPane;
	private long s; //这个变量用来记录 动态切换一次消耗的时间起始点(勿删)
	private final int btnPanel_3d_width = 250;
	private boolean clickExpand = false; // 点击收起，默认
	private JLabel label;

	public I_WLTTabbedPane() {
		this(LookAndFeel.apptabbedPanelSelectedBackground, LookAndFeel.apptabbedPanelNotSelectedBackground); //
	}

	public I_WLTTabbedPane(int _tabHeight) {
		this(LookAndFeel.apptabbedPanelSelectedBackground, LookAndFeel.apptabbedPanelNotSelectedBackground); //
	}

	public I_WLTTabbedPane(Color _bgColor, final Color _notSelectedbgColor) {
		topMainPane = new JPanel();
		topMainPane.setBackground(_bgColor);
		topMainPane.setLayout(new BorderLayout());
		color_clicked = _bgColor;
		color_notclicked = _notSelectedbgColor; //
		this.setLayout(new BorderLayout()); //
		btnPanel = new WLTPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
		btnPanel.setBackground(_bgColor);
		btnPanel.setUI(new BasicPanelUI() {
			public void paint(Graphics g, JComponent c) {
				//立体的按钮背景板
				GeneralPath path = new GeneralPath();
				path.moveTo(c.getWidth() / 2 - btnPanel_3d_width, c.getHeight() / 2);
				path.lineTo(c.getWidth() / 2 + btnPanel_3d_width, c.getHeight() / 2);
				path.lineTo(c.getWidth() / 2 + btnPanel_3d_width + 40, c.getHeight() - 2);
				path.lineTo(c.getWidth() / 2 - btnPanel_3d_width - 40, c.getHeight() - 2);
				path.lineTo(c.getWidth() / 2 - btnPanel_3d_width, c.getHeight() / 2);
				GradientPaint paint = new GradientPaint(0, c.getHeight() / 2, Color.WHITE, 0, c.getHeight(), Color.LIGHT_GRAY);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setPaint(paint);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.fill(path);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

			}
		});
		btnPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0)); ////
		toftPanel.setLayout(cardLayout); //层次布局
		toftPanel.setBorder(BorderFactory.createEmptyBorder()); //
		JPanel panel_north = new JPanel(new BorderLayout()); //
		panel_north.setOpaque(false); //透明!!!
		label = new JLabel() { //收起的双横线label
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(splitMoveLabelBackColor);
				g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
				GradientPaint paint = new GradientPaint(0, 2, Color.WHITE, 0, 5, Color.DARK_GRAY);
				g2d.setPaint(paint);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.fillRoundRect((int) this.getSize().getWidth() / 2 - 18, 1, 36, 4, 2, 2);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			}
		};
		timer = new Timer(10, this);
		MouseAdapter mouse = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (timer != null) {
					if (show) {
						clickExpand = true;
					} else {
						clickExpand = false;
					}
					if (canMovingPaint()) { //判断是否可动态切换
						s = System.currentTimeMillis(); //设置启动缓冲效果初始时间
						timer.start();
					} else {
						if (show) {//直接显示
							splitpane.setDividerLocation(0);
							show = false;
						} else if (!clickExpand) {
							splitpane.setDividerLocation(splitDividerLocation);
							show = true;
						}
					}
				}
			}
		};
		//		label.setOpaque(false);
		label.addMouseListener(mouse);
		label.setBackground(Color.BLUE);
		label.setPreferredSize(new Dimension(-1, 7));
		panel_north.add(label, BorderLayout.NORTH);
		panel_north.add(toftPanel, BorderLayout.CENTER); ////
		topMainPane.add(btnPanel, BorderLayout.CENTER);
		topMainPane.setMinimumSize(new Dimension(0, 0));
		btnPanel.setMinimumSize(new Dimension(0, 0));
		splitpane.setTopComponent(topMainPane);
		splitpane.setBottomComponent(panel_north);
		splitpane.setDividerSize(0);
		splitpane.setDividerLocation(splitDividerLocation);
		splitpane.setOpaque(false);
		focusMoveTimer = new Timer(10, this);
		if (ClientEnvironment.isAdmin()) {
			topMainPane.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 1 && e.getButton() == MouseEvent.BUTTON3) {
						showConfigPopMenu(e);
					}
				}
			});
		}
		this.add(splitpane); //
	}

	public void setBtnPanelCloseForever() {
		splitpane.setDividerLocation(0);
		label.setEnabled(false);
		label.setVisible(false);
		clickExpand = true;
		show = false;
	}

	private void showConfigPopMenu(MouseEvent e) {
		JPopupMenu pop = new JPopupMenu();
		JMenuItem item = new JMenuItem("配置");
		item.addActionListener(this);
		pop.add(item);
		pop.show(this, e.getX(), e.getY());
	}

	public JPanel getTopMainPanel() {
		return topMainPane;
	}

	//加速
	private double getX(double t, double b, double c, double d) {
		return c * (t /= d) * Math.pow(t, 4) + b;
	}

	//减速
	private double getX2(double t, double b, double c, double d) {
		if ((t /= d / 2) < 1)
			return c / 2 * Math.pow(t, 3) + b;
		return c / 2 * ((t -= 2) * t * t + 2) + b;
	}

	private double getX3(double t, double b, double c, double d) {
		double s = 1.8;
		return c * (t /= d) * t * ((s + 1) * t - s) + b;
	}

	int index = 0;

	public synchronized void expandPane() {
		if (!show && !clickExpand) {
			if (canMovingPaint()) {
				s = System.currentTimeMillis();
				timer.start();
			} else if (!clickExpand) {
				splitpane.setDividerLocation(splitDividerLocation);
				show = true;
			}
		}
	}

	public synchronized void unexpandPane() {
		if (show) {
			if (canMovingPaint()) {
				s = System.currentTimeMillis();
				timer.start();
			} else {
				splitpane.setDividerLocation(0);
				show = false;
			}
		}
	}

	public boolean getShowState() {
		return show;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == timer && !show) {
			int height = (int) getX2(index, 0, splitDividerLocation, 20);
			if (height >= splitDividerLocation) {
				timer.stop();
				if (System.currentTimeMillis() - s > 1200) {
					resetCanPaintCache();
				}
				index = 0;
				show = true;
			}
			splitpane.setDividerLocation(height);
			index++;
		} else if (e.getSource() == timer) {
			int height = (int) getX2(index, 0, splitDividerLocation, 20);
			if (height >= splitDividerLocation) {
				timer.stop();
				if (System.currentTimeMillis() - s > 1200) {
					resetCanPaintCache();
				}
				index = 0;
				show = false;
			}
			splitpane.setDividerLocation(splitDividerLocation - height);
			index++;
		} else if (e.getSource() == focusMoveTimer) {
			this.repaint(0, 0, DeskTopPanel.getDeskTopPanel().getWidth(), topMainPane.getHeight());
			focusindex++;
		} else if (e.getSource() instanceof JMenuItem) {
			BillListDialog listDialog = new BillListDialog(this, "首页按钮风格配置", "PUB_DESKTOP_BTN_CODE1");
			listDialog.setVisible(true);
		}
	}

	int focusindex = 0;

	public void addTab(String _title, ImageIcon _icon, JComponent _component) {
		addTab(_title, _icon, _component, _icon.getIconWidth(), _icon.getIconHeight() + 14);
	}

	public void addTab(String _title, ImageIcon _icon, JComponent _component, int width, int height) {
		I_WLTLButton2 btn = null;
		int li_shadeType = BackGroundDrawingUtil.VERTICAL_BOTTOM_TO_TOP; //渐变的方式
		if (_icon == null) {
			btn = new I_WLTLButton2(_title, new ImageIcon(new TBUtil().getImageScale(UIUtil.getImage("title_1_3.png").getImage(), 60, 60))); //创建一个按钮
		} else {
			btn = new I_WLTLButton2(_title, _icon); //创建一个按钮
		}
		btn.setFocusable(false); //
		btn.setPreferredSize(new Dimension(width + 13, height + 5));
		String str_clientinfo = li_count + "_" + _title; //
		btn.putClientProperty("ClientProperty", str_clientinfo); //
		btn.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				defaultMouseClicked(e); //
			}
		}); //
		btnPanel.add(btn); //
		al_btns.add(btn); //

		_component.putClientProperty("ClientProperty", str_clientinfo); //
		toftPanel.add(_component, li_count + "_" + _title); //加入某个层
		vc_pages.add(_component); //
		li_count++; //
	}

	/**
	 * 重构了Mouse事件!!
	 */
	public synchronized void addMouseListener(MouseListener _listener) {
		vc_mouseListeners.add(_listener); //
	}

	/**
	 * 增加变化监听事件
	 * @param l
	 */
	public void addChangeListener(ChangeListener l) {
		vc_changeListeners.add(l); //
	}

	/**
	 * 增加一个页签
	 * @param _title
	 */
	public void addTab(String _title, JComponent _component) {
		addTab(_title, null, _component);
	}

	/**
	 * 默认Mouse事件监听!!!
	 * @param e
	 */
	private void defaultMouseClicked(MouseEvent e) {
		//先处理默认的
		I_WLTLButton2 btn = (I_WLTLButton2) e.getSource(); //
		if (!(e.getX() > 0 && e.getX() < btn.getWidth() && e.getY() > 0 && e.getY() < btn.getHeight())) {
			return;
		}
		String str_clientinfo = (String) btn.getClientProperty("ClientProperty"); //
		int li_index = indexOfComponent(getJCompentByClientInfo(str_clientinfo)); //
		if (e.getClickCount() == 1) {
			setSelectedIndex(li_index, true); //这个方法中会触发选择变化事件!!
		} else {
			setSelectedIndex(li_index, false); //这个方法中会触发选择变化事件!!
		}

		//循环触发实际Mouse事件监听!!
		for (int i = 0; i < vc_mouseListeners.size(); i++) {
			MouseListener listeners = (MouseListener) vc_mouseListeners.get(i); //
			listeners.mouseClicked(e); //
		}
	}

	/**
	 * 设置某一个页签的颜色
	 * @param _index
	 * @param _foreground
	 */

	public void setSelectedComponent(JComponent c) {
		int li_index = indexOfComponent(c); //
		setSelectedIndex(li_index); //
	}

	public void setSelectedComponentByClientInfo(String _clientInfo) {
		int li_index = indexOfComponent(getJCompentByClientInfo(_clientInfo)); //
		if (li_index >= 0 && li_index != getSelectedIndex()) {
			setSelectedIndex(li_index); //
		}
	}

	/**
	 * 选中某一列
	 * @param _index
	 */
	Timer timer = new Timer(10, this);
	private int forward_index = -1;
	private int forward_x;
	private int curr_x;
	I_WLTLButton2 selectedButton;

	public void setSelectedIndex(int _index) {
		this.setSelectedIndex(_index, true);
	}

	long d;

	/*
	 * dynamic是否动态。
	 */
	public void setSelectedIndex(int _index, boolean dynamic) {
		JComponent c = getComponentAt(_index); //
		String str_clientInfo = (String) c.getClientProperty("ClientProperty"); //
		//设置按钮
		if (forward_index == -1) {
			forward_index = _index;
			cardLayout.show(toftPanel, str_clientInfo); //
		} else {
			forward_index = selectedIndex;
		}
		JComponent c_1 = getComponentAt(forward_index); //
		String str = (String) c_1.getClientProperty("ClientProperty"); //
		I_WLTLButton2 s = getJButtonByClientInfo(str); //
		forward_x = s.getX();
		selectedIndex = _index; //
		setAllButtonColor(color_notclicked, Color.GRAY); //

		selectedButton = getJButtonByClientInfo(str_clientInfo); //
		if (forward_index == selectedIndex) {
			selectedButton.setPressed(true);
		}
		curr_x = selectedButton.getX();
		if (dynamic) {
			if (focusMoveTimer.isRunning()) {//如果连续点击。
				focusMoveTimer.stop();//先停止
				focusindex = 0;//重置
				System.gc();
			}
			if (forward_index != selectedIndex) {
				d = System.currentTimeMillis();
				focusMoveTimer.start();
			}
		} else {
			selectedButton.setPressed(true);
			cardLayout.show(toftPanel, str_clientInfo); //
		}
		selectedButton.setBackground(color_clicked); //
		selectedButton.setForeground(LookAndFeel.systemLabelFontcolor); //
		//触发选择事件!!
		for (int i = 0; i < vc_changeListeners.size(); i++) {
			ChangeListener listener = (ChangeListener) vc_changeListeners.get(i); //
			listener.stateChanged(new ChangeEvent(this)); //
		}
	}

	//	原有的直接画方框。
	//	Color color = new Color(4, 163, 200, 2);
	Color color1 = new Color(4, 163, 255, 70);
	Color color2 = new Color(4, 163, 255, 150);

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(I_WLTLButton2.displace_h, I_WLTLButton2.displace_v);
		GradientPaint paint = new GradientPaint(0, 0, color1, 0, selectedButton.getHeight(), color2);
		g2d.setPaint(paint);
		g2d.setStroke(new BasicStroke(2));
		if (forward_index == selectedIndex) {
			return;
		}
		if (!focusMoveTimer.isRunning()) {
			return;
		}

		int x = (int) getX2(focusindex, 0, curr_x - forward_x, 18);
		if (focusindex == 8) { //增加到20的时候切换显示页面。
			JComponent c = getComponentAt(selectedIndex); //
			String str_clientInfo = (String) c.getClientProperty("ClientProperty"); //
			cardLayout.show(toftPanel, str_clientInfo); //
		}
		if (Math.abs(x) >= Math.abs(curr_x - forward_x)) {
			focusindex = 0;
			g2d.fillRoundRect(curr_x, selectedButton.getY(), selectedButton.getIcon().getIconWidth() - 2, selectedButton.getIcon().getIconHeight() - 16, 10, 10);
			focusMoveTimer.stop();
			selectedButton.setPressed(true);
			return;
		}
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		//		g2d.setComposite(AlphaComposite.DstOver);
		//		g2d.fillRoundRect(forward_x + x, selectedButton.getY(), selectedButton.getWidth() - 2, selectedButton.getHeight() - 2, 10, 10);
		g2d.fillRoundRect(forward_x + x, selectedButton.getY(), selectedButton.getIcon().getIconWidth() - 2, selectedButton.getIcon().getIconHeight(), 10, 10);
		int s_x1 = forward_x + x + (selectedButton.getIcon().getIconWidth()) / 2 - 8;
		int s_y1 = selectedButton.getY() + selectedButton.getIcon().getIconHeight() - 16;
		g2d.setColor(Color.WHITE);
		g2d.translate(0, 16);
		g2d.fillPolygon(new int[] { s_x1, s_x1 + 8, s_x1 + 16 }, new int[] { s_y1, s_y1 - 8, s_y1 }, 3); //画白色△
	}

	/**
	 * 删除第几页.
	 */
	public void remove(int _index) {
		removeTabAt(_index);
	}

	public void removeTabAt(int _index) {
		I_WLTLButton2 btn = getJButtonAt(_index); //
		JComponent compent = getComponentAt(_index);
		al_btns.remove(_index); //
		vc_pages.remove(_index); //

		int li_previousIndex = _index - 1; //
		int li_nextIndex = _index; //
		if (li_nextIndex >= getTabCount()) {
			if (li_previousIndex <= 0) {
				setSelectedIndex(0); //
			} else {
				setSelectedIndex(li_previousIndex); //
			}
		} else {
			setSelectedIndex(li_nextIndex); //
		}

		btnPanel.remove(btn); //
		cardLayout.removeLayoutComponent(compent); //
		toftPanel.remove(compent); //这行千万不能丢!!!否则会造成打开某个AbstractWorkPanel后就永远不能垃圾回收了!造成内存越跑越高!
		compent = null; //
		btnPanel.updateUI(); //
		toftPanel.updateUI();
	}

	public String getClientInfo(int _index) {
		JComponent component = getComponentAt(_index); //
		String str_clientInfo = (String) component.getClientProperty("ClientProperty"); //
		return str_clientInfo; //
	}

	public I_WLTLButton2 getJButtonByClientInfo(String _clientInfo) {
		for (int i = 0; i < al_btns.size(); i++) {
			I_WLTLButton2 btn = (I_WLTLButton2) al_btns.get(i);
			String str_clientInfo = (String) btn.getClientProperty("ClientProperty"); //
			if (str_clientInfo.equals(_clientInfo)) {
				return btn;
			}
		}
		return null; //
	}

	public JComponent getJCompentByClientInfo(String _clientInfo) {
		int li_count = getTabCount(); //
		for (int i = 0; i < li_count; i++) {
			JComponent component = (JComponent) vc_pages.get(i); //
			String str_clientInfo = (String) component.getClientProperty("ClientProperty"); //
			if (str_clientInfo.equals(_clientInfo)) {
				return component;
			}
		}
		return null;
	}

	/**
	 * 判断某个页签的位置
	 * @param _component
	 * @return
	 */
	public int indexOfComponent(JComponent _component) {
		int li_count = getTabCount(); //
		for (int i = 0; i < li_count; i++) {
			JComponent comp = (JComponent) vc_pages.get(i); //
			if (_component == comp) {
				return i;
			}
		}
		return -1;
	}

	public int getSelectedIndex() {
		return selectedIndex; //
	}

	/**
	 * 取得所有页签的数量
	 * @return
	 */
	public int getTabCount() {
		return vc_pages.size();
	}

	public void setBtnPanelVisible(boolean _visiable) {
		btnPanel.setVisible(_visiable); //
		tablabel.setVisible(_visiable);
	}

	public boolean isBtnPanelVisible() {
		return btnPanel.isVisible(); //
	}

	/**
	 * 取得某一页的控件
	 * @param index
	 * @return
	 */
	public JComponent getComponentAt(int index) {
		return (JComponent) vc_pages.get(index); //
	}

	public I_WLTLButton2 getJButtonAt(int index) {
		return (I_WLTLButton2) al_btns.get(index); //
	}

	/**
	 * 设置所有按钮的背景颜色!!!
	 * @param _color
	 */
	private void setAllButtonColor(Color _color, Color _foreGround) {
		for (int i = 0; i < al_btns.size(); i++) {
			I_WLTLButton2 btn = (I_WLTLButton2) al_btns.get(i); //
			btn.setPressed(false);
		}
	}

	/*
	 * 得到整体的Jframe的窗口状态，最大化的窗口下，绘制会很慢,卡。
	 * 主要是1024*768分辨率有问题
	 */
	private boolean windowMaxState() {
		JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(DeskTopPanel.getDeskTopPanel());
		if (mainFrame.getExtendedState() == 6) { //最大化
			if (Toolkit.getDefaultToolkit().getScreenSize().getWidth() == 1024 && Toolkit.getDefaultToolkit().getScreenSize().getHeight() == 768) {
				return true;
			}
			return false;
		}
		return false;
	}

	/*
	 * 重置是否动态切换缓存。
	 * 值为1=非全屏窗口会卡顿
	 * 值为2=全屏窗口卡顿
	 */
	private void resetCanPaintCache() {
		Object obj = ClientEnvironment.getInstance().get("movingpaint");
		int i = 0;
		if (obj != null) {
			i = (Integer) obj;
		}
		if (i == 0) {
			if (windowMaxState()) {
				ClientEnvironment.getInstance().put("movingpaint", 2);
			} else {
				ClientEnvironment.getInstance().put("movingpaint", 1);
			}
		} else {
			if (i == 1) {//任何状态都卡
				return;
			} else if (!windowMaxState()) {
				ClientEnvironment.getInstance().put("movingpaint", 1);
			}
		}
	}

	private boolean canMovingPaint() {
		Object obj = ClientEnvironment.getInstance().get("movingpaint");
		if (obj == null) {
			return true;
		}
		int i = (Integer) obj;
		if (i == 1) {
			return false;
		} else if (i == 2) { //全屏会卡
			if (windowMaxState()) {//处于全屏状态
				return false;
			} else {//非全屏状态
				return true;
			}
		} else {
			return true;
		}
	}
}
