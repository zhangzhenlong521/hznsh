package cn.com.infostrategy.ui.common;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 模仿JTabbedPane做的一个布局面板,只不过上面是一批按钮
 * @author xch
 *
 */
public class WLTRadioPane extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L; //
	private JPanel btnPanel = null; //
	private JPanel toftPanel = null; //new JPanel(); //层次面板
	private CardLayout cardLayout = new CardLayout(); //卡片布局
	private ButtonGroup btnGroup = new ButtonGroup();

	private int li_count = -1; //
	private ArrayList al_btns = new ArrayList(); //
	private Vector vc_changeListeners = new Vector(); //

	private boolean isAutoSelectFirst = true; //

	public WLTRadioPane() {
		this(LookAndFeel.defaultShadeColor1); //
	}

	public WLTRadioPane(Color _bgColor) {
		this.setLayout(new BorderLayout()); //
		btnPanel = new WLTPanel(WLTPanel.VERTICAL_TOP_TO_BOTTOM, _bgColor, false); //颜色后来改了下!
		btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); //
		btnPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 0)); ////
		toftPanel = WLTPanel.createDefaultPanel(); //
		toftPanel.setLayout(cardLayout); //层次布局
		toftPanel.setBorder(BorderFactory.createEmptyBorder()); //
		this.add(btnPanel, BorderLayout.NORTH); //
		this.add(toftPanel, BorderLayout.CENTER); //
	}

	public void addTab(String _title, Component _component) {
		addTab(_title, null, _component);
	}

	public void addTab(String _title, String _img, Component _component) {
		addTab(_title, null, null, _component);
	}

	/**
	 * 增加一个页签..
	 * @param _title
	 */
	public void addTab(String _title, String _key, String _img, Component _component) {
		li_count++; //
		JRadioButton btn = null; //
		if (_img == null) { //如果没图片
			btn = new JRadioButton(_title);
			btn.setOpaque(false);
			btnPanel.add(btn); //如果有图片,则改成按没字,后面跟个Labe有字的的样子!!!
		} else { //如果有图片,则后面跟个Label
			btn = new JRadioButton();
			btn.setOpaque(false);
			btnPanel.add(btn); //如果有图片,则改成按没字,后面跟个Labe有字的的样子!!!
			JLabel label = new JLabel(_title, UIUtil.getImage(_img), JLabel.LEFT); //
			label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 3)); //
			label.putClientProperty("bindbtn", btn); //
			label.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					JLabel label = (JLabel) e.getSource(); //
					label.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
					JRadioButton bindbtn = (JRadioButton) label.getClientProperty("bindbtn"); //
					bindbtn.setSelected(true); //
					onClicked(bindbtn); //
					label.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
				}

			}); //
			btnPanel.add(label); //
		}
		btnGroup.add(btn); //
		btn.setFocusable(true); //设成有个框的样子好看点,关键是可以支持键盘操作!!!!
		if (li_count == 0 && isAutoSelectFirst) { //首页遇到欢迎提示时就不自动选择!
			btn.setSelected(true); ///
		}
		btn.putClientProperty("title", _title); //
		btn.putClientProperty("pos", new Integer(li_count)); //
		if (_key != null) {
			btn.putClientProperty("key", _key); //
		}
		btn.addActionListener(this); //
		al_btns.add(btn); //
		toftPanel.add(_component, _title); //加入某个层

		//btnPanel.repaint(); //
	}

	/**
	 * 根据key取得某一个页面
	 * @param _title
	 * @return
	 */
	public Component getTabCompent(int _index) {
		return toftPanel.getComponent(_index); //
	}

	public Component getTabCompent(String _key) {
		return toftPanel.getComponent(getIndexByKey(_key)); //
	}

	private int getIndexByKey(String _key) {
		for (int i = 0; i < al_btns.size(); i++) {
			JRadioButton btn = (JRadioButton) al_btns.get(i); //
			String str_key = (String) btn.getClientProperty("key"); //
			if (_key.equals(str_key)) {
				return i; //
			}
		}
		return 0; //
	}

	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if (btnPanel != null) {
			btnPanel.setBackground(bg);
		}
		JRadioButton[] btns = getButtons();
		if (btns != null) {
			for (int i = 0; i < btns.length; i++) {
				//btns[i].setOpaque(true); //
				btns[i].setBackground(bg);
			}
		}
	}

	/**
	 * 点击按钮动作..
	 */
	public void actionPerformed(ActionEvent e) {
		JRadioButton btn = (JRadioButton) e.getSource(); //
		btn.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
		onClicked(btn); //
		btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
	}

	public void onClicked(JRadioButton _bindbtn) {
		String str_title = (String) _bindbtn.getClientProperty("title"); //
		cardLayout.show(toftPanel, str_title); //
		for (int i = 0; i < vc_changeListeners.size(); i++) {
			ChangeListener listener = (ChangeListener) vc_changeListeners.get(i); //
			listener.stateChanged(new ChangeEvent(this)); //
		}
	}

	public void showOneTab(String _title) {
		getRadioButton(_title).setSelected(true);
		cardLayout.show(toftPanel, _title); //
	}

	public void onClicked(String _title) {
		JRadioButton btn = getRadioButton(_title); //
		if (btn != null) {
			btn.setSelected(true); //
			onClicked(btn); //
		}
	}

	/**
	 * 取得所有按钮
	 * @return
	 */
	public JRadioButton[] getButtons() {
		if (al_btns == null) {
			return null;
		}
		JRadioButton[] btns = new JRadioButton[al_btns.size()]; //
		for (int i = 0; i < al_btns.size(); i++) {
			btns[i] = (JRadioButton) al_btns.get(i);
		}
		return btns;
	}

	public JRadioButton getRadioButton(String _title) {
		JRadioButton[] btns = getButtons(); //
		for (int i = 0; i < btns.length; i++) {
			if (_title.equals(btns[i].getClientProperty("title"))) {
				return btns[i];
			}
		}
		return null; //
	}

	//是否某个按钮已选中?
	public boolean isSellectedOneItem() {
		JRadioButton[] btns = getButtons(); //
		for (int i = 0; i < btns.length; i++) {
			if (btns[i].isSelected()) {
				return true;
			}
		}
		return false; //
	}

	public void clearAllRadionSelected() {
		JRadioButton[] btns = getButtons(); //
		if (btns != null && btns.length > 0) {
			for (int i = 0; i < btns.length; i++) {
				btns[i].setSelected(false); //
			}
		}
	}

	public void setBtnPanelVisible(boolean _visiable) {
		btnPanel.setVisible(_visiable); //
	}

	public boolean isBtnPanelVisible() {
		return btnPanel.isVisible(); //
	}

	/**
	 * 增加变化监听事件
	 * @param l
	 */
	public void addChangeListener(ChangeListener l) {
		vc_changeListeners.add(l); //
	}

	public int getSelectIndex() {
		for (Enumeration<AbstractButton> btns = btnGroup.getElements(); btns.hasMoreElements();) {
			AbstractButton btn = btns.nextElement();
			if (btn.isSelected()) {
				return (Integer) btn.getClientProperty("pos");
			}
		}
		return -1;
	}

	public String getSelectTitle() {
		for (Enumeration<AbstractButton> btns = btnGroup.getElements(); btns.hasMoreElements();) {
			AbstractButton btn = btns.nextElement();
			if (btn.isSelected()) {
				return (String) btn.getClientProperty("title");
			}
		}
		return null;
	}

	public String getSelectKey() {
		for (Enumeration<AbstractButton> btns = btnGroup.getElements(); btns.hasMoreElements();) {
			AbstractButton btn = btns.nextElement();
			if (btn.isSelected()) {
				return (String) btn.getClientProperty("key"); //
			}
		}
		return null;
	}

	public boolean isAutoSelectFirst() {
		return isAutoSelectFirst;
	}

	public void setAutoSelectFirst(boolean isAutoSelectFirst) {
		this.isAutoSelectFirst = isAutoSelectFirst;
	}

}
