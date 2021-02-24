package cn.com.infostrategy.ui.common;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.mdata.AbstractShiftLgIfc;
import cn.com.infostrategy.ui.mdata.AbstractShiftPanel;

/**
 * ģ��JTabbedPane����һ���������,ֻ����������һ����ť
 * @author xch
 *
 */
public class WLTTabbedPane extends JPanel implements AbstractShiftLgIfc {

	private static final long serialVersionUID = 1L; //

	private JPanel btnPanel = new JPanel(); //
	private JLabel tablabel = null; //
	private JPanel toftPanel = new JPanel(); //������
	private CardLayout cardLayout = new CardLayout(); //��Ƭ����

	private Color color_clicked = null;

	private Color color_notclicked = null; //
	private Color color_notClickedForeGround = new Color(100, 100, 100);

	private int li_count = 0; //
	private ArrayList al_btns = new ArrayList(); //
	private Vector vc_pages = new Vector(); //

	private int selectedIndex = 0;

	private Vector vc_mouseListeners = new Vector(); //
	private Vector vc_changeListeners = new Vector(); //

	private int tabPlacement = 1; //Tab�������滹������?

	private boolean canClose = false; //�Ƿ���Թر�,��
	private int select = 0;

	private AbstractShiftPanel sp = null;

	private WLTButton btn_more = null; //���ఴť
	private boolean isSimple = false;
	private boolean allMustShow = false;

	public WLTTabbedPane() {
		this(LookAndFeel.apptabbedPanelSelectedBackground, LookAndFeel.apptabbedPanelNotSelectedBackground, JTabbedPane.TOP); //
	}

	public WLTTabbedPane(boolean _canClose) {
		this(LookAndFeel.apptabbedPanelSelectedBackground, LookAndFeel.apptabbedPanelNotSelectedBackground, JTabbedPane.TOP); //
		canClose = _canClose;
	}

	public WLTTabbedPane(int _tabPlacement) {
		this(LookAndFeel.apptabbedPanelSelectedBackground, LookAndFeel.apptabbedPanelNotSelectedBackground, _tabPlacement); //
	}

	public WLTTabbedPane(Color _bgColor, Color _notSelectedbgColor) {
		this(_bgColor, _notSelectedbgColor, JTabbedPane.TOP); //
	}

	public WLTTabbedPane(Color _bgColor, Color _notSelectedbgColor, int _tabPlacement) {
		color_clicked = _bgColor.darker();
		color_notclicked = _notSelectedbgColor; //
		this.tabPlacement = _tabPlacement; //tab��λ��
		this.setLayout(new BorderLayout()); //
		this.setOpaque(false); //͸��!!
		btnPanel.setOpaque(false); //͸��!!!
		btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); //ˮƽ����
		if (tabPlacement == JTabbedPane.BOTTOM) {
			btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0)); ////
		} else {
			btnPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 0, 0)); ////
		}
		toftPanel.setLayout(cardLayout); //��β���
		toftPanel.setOpaque(false);
		toftPanel.setBorder(BorderFactory.createEmptyBorder()); //
		JPanel panel_north = new JPanel(new BorderLayout(0, 0)); //
		panel_north.setOpaque(false); //͸��!!!
		panel_north.add(btnPanel, BorderLayout.NORTH); //

		tablabel = new JLabel();
		tablabel.setOpaque(false);
		tablabel.setPreferredSize(new Dimension(100, 1)); //�����������ϸһЩ
		tablabel.setBackground(color_clicked); //
		tablabel.setOpaque(true); //��͸��,�������ͻ��Ч��!!
		panel_north.add(tablabel, BorderLayout.CENTER); ////

		if (_tabPlacement == JTabbedPane.TOP) {
			this.add(panel_north, BorderLayout.NORTH); //
		} else if (_tabPlacement == JTabbedPane.BOTTOM) {
			this.add(panel_north, BorderLayout.SOUTH); //
		} else {
			this.add(panel_north, BorderLayout.NORTH); //
		}
		this.add(toftPanel, BorderLayout.CENTER); //

		//btnPanel׷�ӿ�ȱ仯�¼� ����btnPanel
		btnPanel.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) {

			}

			public void componentMoved(ComponentEvent e) {

			}

			public void componentResized(ComponentEvent e) {
				dealmore(); //���ఴť����
				btnPanel.updateUI();
			}

			public void componentShown(ComponentEvent e) {

			}
		});
	}

	public String getTitleAt(int i) {
		if (al_btns.get(i) instanceof CloseTabedPaneBtn) {
			return ((CloseTabedPaneBtn) al_btns.get(i)).getText();
		}
		return "";
	}

	//ҳǩ����Ƿ����ʾ��ƽ��
	public void isSimpleStyle(boolean _simpleStyle) {
		isSimple = _simpleStyle;
	}

	//����ȫ����ʾ,��������Щ�����ȡ��������ȣ��������а�ť���ء�
	public void isAllMustShow(boolean _flag) {
		allMustShow = _flag;
	}

	public void addTab(String _title, Icon _icon, JComponent _component) {
		CloseTabedPaneBtn btn = null;
		int li_shadeType = BackGroundDrawingUtil.VERTICAL_BOTTOM_TO_TOP; //����ķ�ʽ
		if (tabPlacement == JTabbedPane.BOTTOM) {
			li_shadeType = BackGroundDrawingUtil.VERTICAL_TOP_TO_BOTTOM2; //
		}
		btn = new CloseTabedPaneBtn(_title, (ImageIcon) _icon, this.getTabCount(), this, canClose);
		btn.setUI((ButtonUI) BasicButtonUI.createUI(this));
		btn.setOpaque(false);

		btn.setFocusable(false); //
		int li_topline = 1, li_bottomline = 0;
		if (tabPlacement == JTabbedPane.BOTTOM) {
			li_topline = 0;
			li_bottomline = 1;
		}
		if (li_count == 0) {
			btn.setBorder(BorderFactory.createMatteBorder(li_topline, 1, li_bottomline, 1, new Color(120, 120, 120))); ////
			btn.setBackground(color_clicked); //
			btn.setForeground(LookAndFeel.systemLabelFontcolor); //
		} else {
			btn.setBorder(BorderFactory.createMatteBorder(li_topline, 0, li_bottomline, 1, new Color(120, 120, 120))); ////
			btn.setBackground(color_notclicked); //
			btn.setForeground(Color.GRAY); //
		}
		if (_icon != null) {
			btn.setBorder(BorderFactory.createEmptyBorder()); ////
		} else {
			//			btn.setPreferredSize(new Dimension((int) btn.getPreferredSize().getWidth(), 24)); //���ø߶�
		}
		String str_clientinfo = li_count + "_" + _title; //
		btn.putClientProperty("ClientProperty", str_clientinfo); //
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent _event) {
				defaultActioned(_event); //
			}
		});
		btn.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				defaultMouseClicked(e); //
			}
		}); //
		btnPanel.add(btn); //
		al_btns.add(btn); //

		_component.putClientProperty("ClientProperty", str_clientinfo); //
		toftPanel.add(_component, li_count + "_" + _title); //����ĳ����
		vc_pages.add(_component); //
		li_count++; //
		btn.init();
		btn.repaint();
		if (li_count == 1) {
			if (al_btns.get(selectedIndex) instanceof CloseTabedPaneBtn) {
				((CloseTabedPaneBtn) al_btns.get(selectedIndex)).setFocus(true);
			}
		}
		//setSelectedComponent(_component); //
		if (!allMustShow && li_count >= 5) {
			dealmore(); //׷��tab ���ఴť����
		}
		btnPanel.updateUI(); //
	}

	//���ఴť���� ׷�� ɾ��tab �����/2013-07-17��
	private void dealmore() {
		double btnpanel_len = btnPanel.getWidth(); //btnPanel����
		if (al_btns.size() > 1) {
			if (btn_more == null) {
				btn_more = new WLTButton("" + al_btns.size(), "zt_073.gif");
				btn_more.setBackground(btnPanel.getBackground());
				btn_more.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, btnPanel.getBackground()));
				btn_more.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showHiddenTabMenu(btn_more); //��ʾ����tab
					}
				});
			}

			double btn_visible_len = ((WLTButton) al_btns.get(0)).getPreferredSize().getWidth(); //��ҳ��Զ��ʾ ׷��
			btn_visible_len += btn_more.getPreferredSize().getWidth(); //����Ԥ��׷��

			int btn_visible_mark = 0; //��ʼ���ر��
			for (int i = al_btns.size() - 1; i > 0; i--) { //���ű���
				WLTButton btn = (WLTButton) al_btns.get(i);
				if (btn_visible_mark > 0) { //�����ر�� ����֮
					if (btn.isVisible()) {
						btn.setVisible(false);
					}
				} else {
					btn_visible_len += btn.getPreferredSize().getWidth(); //��ʾ��ť�ĳ��� ׷��
					if (btn_visible_len < btnpanel_len) { //��ʾ��ť����������ܳ� ��ʾ ��֮ ���� ��׷�����ر��
						if (!btn.isVisible()) {
							btn.setVisible(true);
						}
					} else {
						btn_visible_mark = i;
						if (btn.isVisible()) {
							btn.setVisible(false);
						}
					}
				}
			}

			btnPanel.remove(btn_more); //������remove ��֤btn_more�����
			if (btn_visible_mark > 0) {
				btn_more.setText("" + btn_visible_mark); //�������� �����ر��
				btn_more.setToolTipText("" + btn_visible_mark);
				btnPanel.add(btn_more);
			}
		}
	}

	//���ఴť���� ѡ��tab �����/2013-07-17��
	private void selectmore(int btn_select) {
		if (getJButtonAt(btn_select).isVisible()) { //����ʾ ��return
			return;
		}

		double btnpanel_len = btnPanel.getWidth();
		if (al_btns.size() > 1) {
			if (btn_more == null) {
				btn_more = new WLTButton("" + al_btns.size(), "zt_073.gif");
				btn_more.setBackground(btnPanel.getBackground());
				btn_more.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, btnPanel.getBackground()));
				btn_more.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						showHiddenTabMenu(btn_more); //��ʾ����tab
					}
				});
			}

			double btn_visible_len = ((WLTButton) al_btns.get(0)).getPreferredSize().getWidth(); //��ҳ��Զ��ʾ ׷��
			btn_visible_len += btn_more.getPreferredSize().getWidth(); //����Ԥ��׷��
			btn_visible_len += getJButtonAt(btn_select).getPreferredSize().getWidth(); //ѡ��tabԤ��׷��

			int btn_visible_mark = 0; //��ʼ���ر��
			for (int i = al_btns.size() - 1; i > 0; i--) {
				WLTButton btn = (WLTButton) al_btns.get(i);

				if (i == btn_select) { //����ѡ��tab
					btn.setVisible(true);
					continue;
				}

				if (btn_visible_mark > 0) { //�����ر�� ����֮
					if (btn.isVisible()) {
						btn.setVisible(false);
					}
				} else {
					btn_visible_len += btn.getPreferredSize().getWidth(); //��ʾ��ť�ĳ��� ׷��
					if (btn_visible_len < btnpanel_len) { //��ʾ��ť����������ܳ� ��ʾ ��֮ ���� ��׷�����ر��
						if (!btn.isVisible()) {
							btn.setVisible(true);
						}
					} else {
						btn_visible_mark = i;
						if (btn.isVisible()) {
							btn.setVisible(false);
						}
					}
				}
			}

			btnPanel.remove(btn_more); //������remove ��֤btn_more�����
			if (btn_visible_mark > 0) {
				if (btn_visible_mark > btn_select) { //�����ر�Ǵ���ѡ��tab ����-1
					btn_visible_mark--;
				}
				btn_more.setText("" + btn_visible_mark); //�������� �����ر��
				btn_more.setToolTipText("" + btn_visible_mark);
				btnPanel.add(btn_more);
			}
		}
	}

	/**
	 * �ع���Mouse�¼�!!
	 */
	public synchronized void addMouseListener(MouseListener _listener) {
		vc_mouseListeners.add(_listener); //
	}

	/**
	 * ���ӱ仯�����¼�
	 * @param l
	 */
	public void addChangeListener(ChangeListener l) {
		vc_changeListeners.add(l); //
	}

	/**
	 * ����һ��ҳǩ
	 * @param _title
	 */
	public void addTab(String _title, JComponent _component) {
		addTab(_title, null, _component);
	}

	private void defaultActioned(ActionEvent _event) {
		JComponent btn = (JComponent) _event.getSource(); //
		String str_clientinfo = (String) btn.getClientProperty("ClientProperty"); //
		int li_index = indexOfComponent(getJCompentByClientInfo(str_clientinfo)); //
		setSelectedIndex(li_index); //��������лᴥ��ѡ��仯�¼�!!

	}

	/**
	 * Ĭ��Mouse�¼�����!!!
	 * @param e
	 */
	private void defaultMouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3 || e.getButton() == MouseEvent.BUTTON2) { //ֻ���Ҽ��Ŵ���ѡ���¼�,��Ϊ�������ActionListener������!! ��������м�, ��ΪҪʵ�ֹر��Լ� Gwang 2013/2/28		
			JComponent btn = (JComponent) e.getSource(); //
			String str_clientinfo = (String) btn.getClientProperty("ClientProperty"); //
			int li_index = indexOfComponent(getJCompentByClientInfo(str_clientinfo)); //
			setSelectedIndex(li_index); //��������лᴥ��ѡ��仯�¼�!!
		}

		//ѭ������ʵ��Mouse�¼�����!!
		for (int i = 0; i < vc_mouseListeners.size(); i++) {
			MouseListener listeners = (MouseListener) vc_mouseListeners.get(i); //
			listeners.mouseClicked(e); //
			listeners.mouseReleased(e);
		}
	}

	/**
	 * ����ĳһ��ҳǩ����ɫ
	 * @param _index
	 * @param _foreground
	 */
	public void setForegroundAt(int _index, Color _foreground) {
		JComponent btn = (JComponent) al_btns.get(_index); //
		btn.setForeground(_foreground); //
	}

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

	private void addShiftEffect(int toli_index) {
		try {
			if (toli_index == this.getSelectedIndex()) {
				return;
			}
			int isup = toli_index > this.getSelectedIndex() ? 0 : 1;
			if (getComponentAt(toli_index).getBounds().height > 0) {
				sp = new AbstractShiftPanel(this, TBUtil.getTBUtil().getCompentImage(getComponentAt(this.getSelectedIndex())), TBUtil.getTBUtil().getCompentImage(getComponentAt(toli_index)), isup);
				toftPanel.add(sp, "sp");
				cardLayout.show(toftPanel, "sp");
			} else {
				setSelectedIndex_(toli_index);
			}
		} catch (Exception e) {
			setSelectedIndex_(toli_index);
			e.printStackTrace();
		}
	}

	/**
	 * ѡ��ĳһ��
	 * @param _index
	 */
	public void setSelectedIndex(int _index) {
		if (TBUtil.getTBUtil().getSysOptionBooleanValue("ƽ̨ҳ���л��Ƿ������", false)) {
			select = _index;
			addShiftEffect(_index);
		} else {
			setSelectedIndex_(_index);
		}
	}

	public void setSelectedIndex_(int _index) {
		if (_index < 0) {
			return;
		}
		JComponent c = getComponentAt(_index); //
		c.setOpaque(false);
		String str_clientInfo = (String) c.getClientProperty("ClientProperty"); //
		cardLayout.show(toftPanel, str_clientInfo); //

		//���ð�ť
		setAllButtonColor(color_notclicked, Color.GRAY); //
		JComponent selectedButton = getJButtonByClientInfo(str_clientInfo); //
		selectedButton.setBackground(color_clicked); //
		selectedButton.setForeground(LookAndFeel.systemLabelFontcolor); //
		if (al_btns.size() - 1 >= selectedIndex) {
			if (al_btns.get(selectedIndex) instanceof CloseTabedPaneBtn) {
				((CloseTabedPaneBtn) al_btns.get(selectedIndex)).setFocus(false);
			}
		}
		if (al_btns.get(_index) instanceof CloseTabedPaneBtn) {
			((CloseTabedPaneBtn) al_btns.get(_index)).setFocus(true);
		}

		selectedIndex = _index; //

		//����ѡ���¼�!!
		for (int i = 0; i < vc_changeListeners.size(); i++) {
			ChangeListener listener = (ChangeListener) vc_changeListeners.get(i); //
			listener.stateChanged(new ChangeEvent(this)); //
		}

		selectmore(_index); //ѡ��tab ���ఴť����
	}

	/**
	 * ɾ���ڼ�ҳ.
	 */
	public void remove(int _index) {
		removeTabAt(_index);
	}

	public void removeTabAt(int _index) {
		JComponent btn = getJButtonAt(_index); //
		JComponent compent = getComponentAt(_index);
		al_btns.remove(_index); //
		vc_pages.remove(_index); //
		if (canClose) {
			for (int i = 0; i < al_btns.size(); i++) {
				CloseTabedPaneBtn closeBtn = (CloseTabedPaneBtn) al_btns.get(i);
				closeBtn.setIndex(i);
			}
		}
		int li_previousIndex = _index - 1; //
		int li_nextIndex = _index; //
		if (li_nextIndex >= getTabCount()) {
			if (li_previousIndex == 0) {
				setSelectedIndex_(0); //
			} else if (li_previousIndex > 0) {
				setSelectedIndex_(li_previousIndex); //
			}
		} else {
			setSelectedIndex_(li_nextIndex); //
		}

		btnPanel.remove(btn); //
		cardLayout.removeLayoutComponent(compent); //
		toftPanel.remove(compent); //����ǧ���ܶ�!!!�������ɴ�ĳ��AbstractWorkPanel�����Զ��������������!����ڴ�Խ��Խ��!
		compent = null; //
		if (allMustShow) {
			dealmore(); //ɾ��tab ���ఴť����
		}
		btnPanel.updateUI(); //
		toftPanel.updateUI();
	}

	public String getClientInfo(int _index) {
		JComponent component = getComponentAt(_index); //
		String str_clientInfo = (String) component.getClientProperty("ClientProperty"); //
		return str_clientInfo; //
	}

	public JComponent getJButtonByClientInfo(String _clientInfo) {
		for (int i = 0; i < al_btns.size(); i++) {
			JComponent btn = (JComponent) al_btns.get(i);
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
	 * �ж�ĳ��ҳǩ��λ��
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
	 * ȡ������ҳǩ������
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
	 * ȡ��ĳһҳ�Ŀؼ�
	 * @param index
	 * @return
	 */
	public JComponent getComponentAt(int index) {
		return (JComponent) vc_pages.get(index); //
	}

	public JButton getJButtonAt(int index) {
		return (JButton) al_btns.get(index); //
	}

	public int getTabIndexByName(String _text) {
		int index = -1;
		int tabCount = this.getTabCount();
		for (int i = 0; i < tabCount; i++) {
			JButton btn = this.getJButtonAt(i);
			String t = btn.getText();
			if (_text.equals(t)) {
				index = i;
			}
		}
		return index;
	}

	/**
	 * �������а�ť�ı�����ɫ!!!
	 * @param _color
	 */
	private void setAllButtonColor(Color _color, Color _foreGround) {
		for (int i = 0; i < al_btns.size(); i++) {
			JComponent btn = (JComponent) al_btns.get(i); //
			btn.setBackground(_color); //
			btn.setForeground(_foreGround); //
		}
	}

	public class CloseTabedPaneBtn extends WLTButton implements MouseListener, ComponentListener {
		/**
		 * 
		 * @param _text ��ʾ����
		 * @param _icon ͼ��
		 * @param index ���б��
		 */
		private ImageIcon icon;
		private int index;
		private boolean focus;
		private ImageIcon closeImg = new ImageIcon(TBUtil.getTBUtil().getImageScale(UIUtil.getImage("zt_031.gif").getImage(), 13, 13));
		private ImageIcon closeImg_clicked = new ImageIcon(TBUtil.getTBUtil().getImageScale(UIUtil.getImage("del.gif").getImage(), 13, 13));
		private WLTTabbedPane tabedPanel;
		private boolean mouseEnter; //�������
		private boolean showCloseBtn; //�Ƿ���ʾclose��ť
		private Color noFocusBorderColor = new Color(151, 161, 166);
		private GeneralPath path;//����·��
		private GradientPaint paint; //��Ⱦ��
		private boolean closebtnfocus; //�Ƿ����ڹرհ�ťλ��
		private Dimension textSize = null;

		public CloseTabedPaneBtn(String _text, ImageIcon _icon, int _index, WLTTabbedPane _tabedPanel, boolean _showCloseBtn) {
			super("");
			this.setToolTipText(null);
			setText(_text);
			icon = _icon;
			index = _index;
			tabedPanel = _tabedPanel;
			mouseEnter = false;
			showCloseBtn = _showCloseBtn;
		}

		public void setIndex(int _index) {
			index = _index;
		}

		private void init() {
			int width = 0;
			if (icon != null) {
				width += icon.getIconWidth();
			}
			if (getText() != null && getClientProperty(BasicHTML.propertyKey) == null) {
				width += TBUtil.getTBUtil().getStrWidth(getText());
			} else {
				textSize = new Dimension();
				textSize = getUI().getPreferredSize(this);
				width = (int) textSize.getWidth();
			}
			if (isSimple) {
				width += showCloseBtn ? 35 : 20;
			} else {
				width += showCloseBtn ? 35 : 30;
			}
			this.setPreferredSize(new Dimension(width, 24));
			this.setLayout(null);
			this.setOpaque(false);
			this.addComponentListener(this);
		}

		public void setFocus(boolean _focus) {
			if (Boolean.toString(focus).equals(Boolean.toString(_focus))) {
				return;
			}
			focus = _focus;
			int width = 0;
			if (icon != null) {
				width += icon.getIconWidth();
			}
			if (getText() != null && getClientProperty(BasicHTML.propertyKey) == null) {
				width += TBUtil.getTBUtil().getStrWidth(getText());
			} else {
				textSize = new Dimension();
				textSize = getUI().getPreferredSize(this);
				width = (int) textSize.getWidth();
			}
			if (isSimple) {
				width += showCloseBtn ? 35 : 20;
			} else {
				width += showCloseBtn ? 35 : 30;
				width += _focus ? 15 : 0;
			}
			this.setSize(width, 24);
			this.setPreferredSize(new Dimension(width, 24));
			this.repaint();
		}

		//�򻯻���
		public void paintSimpleStyle(Graphics g) {
			int selectIndex = tabedPanel.getSelectedIndex();//�õ���ǰѡ���ҳǩ
			Graphics2D g2d = (Graphics2D) g;
			path = new GeneralPath(GeneralPath.WIND_NON_ZERO);
			boolean ishid = false;
			BasicStroke stroke = new BasicStroke(2);
			if (index < getTabCount() - 1) {
				ishid = ((CloseTabedPaneBtn) tabedPanel.getJButtonAt(index + 1)).getIsHidden();
			}
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (focus) {
				if (showCloseBtn) {
					if ((model.isArmed() || model.isPressed()) && closebtnfocus) {
						g2d.drawImage(closeImg_clicked.getImage(), getWidth() - 38, 5, null);
					} else {
						g2d.drawImage(closeImg.getImage(), getWidth() - 38, 5, null);
					}

				}
				g2d.setColor(Color.BLUE);
				g2d.setStroke(stroke);
				g2d.drawLine(3, this.getHeight() - 1, this.getWidth() - 3, this.getHeight() - 1);
			} else {
				if (mouseEnter && showCloseBtn)//������������
					if ((model.isArmed() || model.isPressed()) && closebtnfocus) {
						g2d.drawImage(closeImg_clicked.getImage(), getWidth() - 22, 5, null);
					} else {
						g2d.drawImage(closeImg.getImage(), getWidth() - 22, 5, null);
					}
			}
			g2d.setColor(getBackground().darker());
			if (icon != null) {
				g2d.drawImage(icon.getImage(), 4, 4, null);
			}
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			if (getText() != null) {
				View v = (View) this.getClientProperty(BasicHTML.propertyKey);
				if (v != null) {
					v.paint(g2d, new Rectangle((icon != null ? icon.getIconWidth() : 4) + 8, 4, (int) textSize.getWidth(), (int) textSize.getHeight()));
				} else {
					g2d.setColor(Color.BLACK);
					g2d.drawString(getText(), (icon != null ? icon.getIconWidth() : 4) + 8, 16);
				}
			}
		}

		public void paint(Graphics g) {
			if (isSimple) {
				paintSimpleStyle(g);
				return;
			}
			//			super.paint(g);
			int selectIndex = tabedPanel.getSelectedIndex();//�õ���ǰѡ���ҳǩ
			Graphics2D g2d = (Graphics2D) g;
			path = new GeneralPath(GeneralPath.WIND_NON_ZERO);
			boolean ishid = false;
			if (index < getTabCount() - 1) {
				ishid = ((CloseTabedPaneBtn) tabedPanel.getJButtonAt(index + 1)).getIsHidden();
			}
			if (focus) { //����н���
				path.moveTo(4, 0);
				path.lineTo(0, 4);
				path.lineTo(0, this.getHeight() - 1);
				path.lineTo(this.getWidth(), this.getHeight() - 1);
				path.curveTo(this.getWidth() - 15, this.getHeight() - 3, this.getWidth() - 15, 3, this.getWidth() - 30, 0);
				path.lineTo(4, 0);
			} else {
				if (selectIndex < index) { //�����ǰѡ���ҳǩ�ڴ�ҳǩǰ,��ô������߿�
					path.moveTo(0, this.getHeight() - 1);
					int x = this.getWidth() - 1;
					path.lineTo(x, this.getHeight() - 1);
					path.lineTo(x, 4);
					path.lineTo(x - 4, 0);
					path.lineTo(0, 0);
					if (tabedPanel.getTabCount() - 1 != index && !ishid) {
						g2d.setColor(noFocusBorderColor);
						g2d.drawLine(this.getWidth() - 5, 0, this.getWidth(), 0);
					}
				} else { //�����ҳǩ��ѡ���ҳǩ���档
					if (ishid) { //�������������ˡ���ô�������ť���ұ߻�������
						path.moveTo(this.getWidth() - 1, this.getHeight());
						path.lineTo(this.getWidth() - 1, 4);
						path.lineTo(this.getWidth() - 4, 0);
					} else {
						path.moveTo(this.getWidth(), 0);
					}
					path.lineTo(4, 0);
					path.lineTo(0, 4);
					path.lineTo(0, this.getHeight() - 1);
					path.lineTo(this.getWidth(), this.getHeight() - 1);
				}
			}
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			if (focus) {
				paint = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), getBackground().darker());
				g2d.setPaint(paint);
				g2d.fill(path);
				if (showCloseBtn) {
					if ((model.isArmed() || model.isPressed()) && closebtnfocus) {
						g2d.drawImage(closeImg_clicked.getImage(), getWidth() - 38, 5, null);
					} else {
						g2d.drawImage(closeImg.getImage(), getWidth() - 38, 5, null);
					}

				}
				//������滹��ҳǩ�� ��Ҫ�ڴ˰�ť�ϲ���������ļ���
				if (tabedPanel.getTabCount() - 1 > index && !ishid) {
					g2d.setColor(noFocusBorderColor);
					g2d.drawLine(this.getWidth() - 30, 0, this.getWidth(), 0);
				}
			} else {
				if (mouseEnter && showCloseBtn)//������������
					if ((model.isArmed() || model.isPressed()) && closebtnfocus) {
						g2d.drawImage(closeImg_clicked.getImage(), getWidth() - 22, 5, null);
					} else {
						g2d.drawImage(closeImg.getImage(), getWidth() - 22, 5, null);
					}
			}
			g2d.setColor(getBackground().darker());
			g2d.draw(path);
			if (index != 0) {
				g2d.setColor(noFocusBorderColor);
				g2d.drawLine(0, 0, 4, 0);
			}
			if (icon != null) {
				g2d.drawImage(icon.getImage(), 4, 4, null);
			}
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			if (getText() != null) {
				View v = (View) this.getClientProperty(BasicHTML.propertyKey);
				if (v != null) {
					v.paint(g2d, new Rectangle((icon != null ? icon.getIconWidth() : 4) + 8, 4, (int) textSize.getWidth(), (int) textSize.getHeight()));
				} else {
					g2d.setColor(Color.BLACK);
					g2d.drawString(getText(), (icon != null ? icon.getIconWidth() : 4) + 8, 16);
				}
			}

		}

		public void mouseClicked(MouseEvent e) {

		}

		public void mouseEntered(MouseEvent e) {
			mouseEnter = true;
			this.repaint();
		}

		public void mouseExited(MouseEvent e) {
			mouseEnter = false;
			this.repaint();
		}

		public void mousePressed(MouseEvent e) {
			CloseTabedPaneBtn btn = (CloseTabedPaneBtn) e.getSource();
			int selectIndex = tabedPanel.getSelectedIndex();
			if (selectIndex == index && btn.canClose() && (e.getX() >= btn.getWidth() - 38 && e.getX() <= btn.getWidth() - 24) && (e.getY() >= 4 && e.getY() <= 17)) {
				closebtnfocus = true;
				return;
			} else if (selectIndex != index && btn.canClose() && (e.getX() >= btn.getWidth() - 22 && e.getX() <= btn.getWidth() - 7) && (e.getY() >= 4 && e.getY() <= 17)) {
				closebtnfocus = true;
				return;
			}
			closebtnfocus = false;
		}

		public void mouseReleased(MouseEvent e) {

		}

		public void setCanClose(boolean _flag) {
			showCloseBtn = _flag;
			repaint();
		}

		public boolean canClose() {
			return showCloseBtn;
		}

		private boolean isHidden = false; //�Ƿ��Ǳ��ƶ������صġ�

		public boolean getIsHidden() {
			return isHidden;
		}

		public void componentHidden(ComponentEvent e) {

		}

		public void componentMoved(ComponentEvent e) {
			if (getY() > 5) {
				if (!isHidden) {
					//�л�����Ҫˢ��ǰ��һ����ť.����ʾ�����أ����ߴ���->��ʾ
					CloseTabedPaneBtn btn = (CloseTabedPaneBtn) getJButtonAt(index - 1);
					btn.repaint();
				}
				isHidden = true;
			} else {
				if (isHidden) {
					if (index > 0) {
						CloseTabedPaneBtn btn = (CloseTabedPaneBtn) getJButtonAt(index - 1);
						btn.repaint();
					}
				}
				isHidden = false;
			}
		}

		public void componentResized(ComponentEvent e) {
			// TODO Auto-generated method stub

		}

		public void componentShown(ComponentEvent e) {
			// TODO Auto-generated method stub

		}

	}

	public void afterShift() {
		setSelectedIndex_(select);
		if (sp != null) {
			cardLayout.removeLayoutComponent(sp);
			toftPanel.remove(sp);
			sp = null;
			System.gc();
		}
	}

	//����TabMenu �����/2013-07-25��
	private void showHiddenTabMenu(WLTButton _btn) {
		JPopupMenu morepopup = new JPopupMenu();

		for (int i = 0; i < al_btns.size(); i++) {
			WLTButton btn = (WLTButton) al_btns.get(i);
			if (!btn.isVisible()) {
				JMenuItem menuItem = new JMenuItem(UIUtil.getLanguage("��" + btn.getText()));
				menuItem.putClientProperty("index", i);
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						JMenuItem menuItem = (JMenuItem) e.getSource();
						int li_index = (Integer) menuItem.getClientProperty("index");
						setSelectedIndex(li_index); //ѡ��tab
					}
				});
				morepopup.add(menuItem);
			}
		}

		morepopup.show(_btn, 0, _btn.getHeight());
	}
}
