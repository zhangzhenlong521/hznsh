package cn.com.infostrategy.ui.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * 模仿OutLook的抽屉风格的控件!!!
 * @author xch
 *
 */
public class WLTOutlookBar extends JPanel implements ActionListener {

	private List tabList = new ArrayList(); //记录有几个页签! 存的是字符串!!
	private List tabBtnList = new ArrayList();
	private HashMap pages = new HashMap();

	private JPanel panel = null;
	private String selectedTabKey = null;

	private boolean isCanMax = true; //是否可以满屏!!
	private boolean isMaxMode = false; //是否是满屏模式,即最大化只显示某一个页签抽屉!!!
	private Font btnFont = LookAndFeel.font; //
	private int textAlign = SwingConstants.LEFT; //
	private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR); //
	private TBUtil tbUtil = new TBUtil(); //

	public WLTOutlookBar() {
		super();
		this.setLayout(new BorderLayout());
	}

	public void addTab(final String title, ImageIcon icon, JComponent c) {
		addTab(title, icon, c, null);
	}

	public void addTab(final String title, ImageIcon icon, JComponent c, String tip) {
		if (selectedTabKey == null) {
			selectedTabKey = title;
		}
		tabList.add(title); //
		pages.put(title, c);
		tabBtnList.add(createButton(title, icon, tabBtnList.size()));
		repaintAllBtns();
	}

	/**
	 * 创建按钮
	 * @param title
	 * @param icon
	 * @param _pos
	 * @return
	 */
	private JButton createButton(final String title, ImageIcon icon, int _pos) {
		int li_dirtype = BackGroundDrawingUtil.HORIZONTAL_RIGHT_TO_LEFT; //
		if ("上下".equals(tbUtil.getSysOptionStringValue("首页抽屉按钮渐变方向", null))) { //有的客户喜欢上下渐变！
			li_dirtype = BackGroundDrawingUtil.VERTICAL_TOP_TO_BOTTOM2; //
		}
		WLTButton btn = new WLTButton(title, icon, li_dirtype); // BackGroundDrawingUtil.
		btn.setPreferredSize(new Dimension(0, 27)); //
		btn.setBackground(LookAndFeel.desktop_OutLookBarBg);//桌面左边抽屉工具条的背景颜色，刘旋飞提出!!
		btn.setHorizontalAlignment(textAlign); //
		btn.setFont(btnFont); //
		btn.setCursor(handCursor); //
		btn.setRightBtnShowInfo(false); //默认右键会弹出菜单,显示按钮相关信息,但这里不需要!否则客户肯定会不喜欢!【xch/2012-02-27】
		btn.addCustActionListener(this); //
		return btn;
	}

	/**
	 * 切换某一个组
	 */
	public void switchTab(JButton _btn) {
		Boolean isExpand = (Boolean) _btn.getClientProperty("isexpand"); //是否展开!
		setAllBtnClientProp("isexpand", false); //所有都收缩!!!
		setAllBtnClientProp("isRightMax", false); //所有都收缩!!!
		if (isExpand == null || !isExpand.booleanValue()) { //这样能实现再次点击时会收缩进去!!!
			_btn.putClientProperty("isexpand", Boolean.TRUE); //设置这个为展开!
		}
		selectedTabKey = _btn.getText(); //
		repaintAllBtns(); //重绘!!!
		this.revalidate(); //
		this.repaint(); //
	}

	//最大化某一个抽屉!即有时抽屉非常多时,会有点撑不开的感觉! 非常需要将其他抽屉都隐藏掉,只打开某一个抽屉!!
	public void maxOneTab(JButton _btn) {
		setAllBtnClientProp("isexpand", false); //所有都收缩!!!
		_btn.putClientProperty("isexpand", Boolean.TRUE); //设置这个为展开!
		_btn.putClientProperty("isRightMax", Boolean.TRUE); //设置这个为展开!
		repaintOneBtn(_btn); //
		this.revalidate(); //
		this.repaint(); //
	}

	public void actionPerformed(ActionEvent _event) {
		int li_modid = _event.getModifiers(); //
		boolean isCtrlDown = (li_modid == 18 ? true : false); //是否按了Ctrl键??
		JButton btn = (JButton) _event.getSource(); //取得按钮!!
		if (isCanMax) { //如果支持满屏!!!
			if (isMaxMode) { //如果已是满屏模式,则直接是返回!不管是否按了Ctrl
				btn.putClientProperty("isexpand", Boolean.FALSE); //设置这个为展开!
				switchTab(btn); //
				isMaxMode = false; //
			} else {
				if (isCtrlDown) { //如果按了Ctrl键!!
					maxOneTab(btn); //满屏!!!
					isMaxMode = true; //
				} else {
					switchTab(btn); //
				}
			}
		} else {
			switchTab(btn); //
		}

	}

	//设置所有按钮的属性
	private void setAllBtnClientProp(String _key, boolean _isExpand) {
		for (int i = 0; i < tabBtnList.size(); i++) { //遍历所有按钮
			JButton btn = (JButton) tabBtnList.get(i); //按钮!!!
			btn.putClientProperty(_key, new Boolean(_isExpand)); //
		}
	}

	//只画某一个按钮!!!
	private void repaintOneBtn(JButton _btn) {
		int li_dirtype = WLTPanel.INCLINE_NW_TO_SE; //
		panel = WLTPanel.createDefaultPanel(new BorderLayout(), li_dirtype); //
		_btn.setCursor(handCursor); //
		_btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY), BorderFactory.createEmptyBorder(2, 6, 2, 2)));
		_btn.setForeground(Color.RED); //
		_btn.setToolTipText("点击收缩原样"); //
		panel.add(_btn, BorderLayout.NORTH); //
		panel.add((JComponent) pages.get(_btn.getText()), BorderLayout.CENTER); //

		this.removeAll(); //
		this.setLayout(new BorderLayout()); //
		this.add(panel, BorderLayout.CENTER); //
	}

	public void setFontAt(int _index, Font _font) {
		if (_index >= tabBtnList.size()) {
			return;
		}
		JButton btn = (JButton) tabBtnList.get(_index); //
		btn.setFont(_font); //
	}

	public void setFontAll(Font _font) {
		this.btnFont = _font; //
		if (tabBtnList != null && tabBtnList.size() > 0) {
			for (int i = 0; i < tabBtnList.size(); i++) {
				JButton btn = (JButton) tabBtnList.get(i); //
				btn.setFont(btnFont); //
			}
		}
	}

	public void setTextAlign(int _align) {
		this.textAlign = _align; //
	}

	/**
	 * 重新绘制页签,关键逻辑与机关都在这!!!
	 */
	private void repaintAllBtns() {
		int li_dirtype = WLTPanel.INCLINE_NW_TO_SE; //
		panel = WLTPanel.createDefaultPanel(new BorderLayout(), li_dirtype);
		this.removeAll(); //
		this.setLayout(new BorderLayout()); //
		this.add(panel, BorderLayout.CENTER); //
		Color color = new Color(55, 55, 55); //
		for (int i = 0; i < tabList.size(); i++) { //遍历所有按钮
			JPanel itemPanel = WLTPanel.createDefaultPanel(new BorderLayout(), li_dirtype);
			JButton btn = (JButton) tabBtnList.get(i); //按钮!!!
			btn.setCursor(handCursor); //
			if (isCanMax) {
				btn.setToolTipText("按Ctrl键点击可放大"); //
			}
			int li_line = (i == 0 ? 1 : 0);
			btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(li_line, 1, 1, 1, Color.GRAY), BorderFactory.createEmptyBorder(2, 6, 2, 2)));
			itemPanel.add(btn, BorderLayout.NORTH); //在上面加入按钮!!!
			panel.add(itemPanel, BorderLayout.NORTH);

			Boolean isExpand = (Boolean) btn.getClientProperty("isexpand"); //是否展开!
			String tabTitle = (tabList.get(i)==null)?"":tabList.get(i).toString(); //  袁江晓更改，解决库清空后用admin登录不进去的bug
			if (isExpand != null && isExpand.booleanValue() && tabTitle.equals(selectedTabKey)) { //如果就是点中的,并且就是要求展开的!这样能实现再次点击时会收缩进去!!!
				btn.setForeground(Color.RED); //
				panel.add((JComponent) pages.get(tabTitle), BorderLayout.CENTER); //加入其对应的树
				JPanel newPanel = WLTPanel.createDefaultPanel(new BorderLayout(), li_dirtype);
				panel.add(newPanel, BorderLayout.SOUTH);
				panel = newPanel;
			} else {
				btn.setForeground(color); //
				JPanel newPanel = WLTPanel.createDefaultPanel(new BorderLayout(), li_dirtype); //
				panel.add(newPanel, BorderLayout.CENTER); //重新赋值,形成递归效果!!!
				panel = newPanel; //
			}
		}
	}

	public int getTabCount() {
		return tabList.size();
	}

	public void setSelectedIndex(int index) {
		setAllBtnClientProp("isexpand", false); //
		JButton btn = (JButton) tabBtnList.get(index); ////按钮!!!
		btn.putClientProperty("isexpand", true); ////
		selectedTabKey = tabList.get(index).toString(); ////
		repaintAllBtns();
	}

	public JComponent getSelectedComponent() {
		return (JComponent) pages.get(selectedTabKey);
	}

	public void removeTabAt(int index) {
		pages.remove(tabList.get(index)); //
		tabBtnList.remove(index); //
		tabList.remove(index); //
		repaintAllBtns();
	}

	public boolean isCanMax() {
		return isCanMax;
	}

	public void setCanMax(boolean isCanMax) {
		this.isCanMax = isCanMax;
	}

}
