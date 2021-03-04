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
 * ģ��JTabbedPane����һ���������,ֻ����������һ����ť
 * @author xch
 *
 */
public class WLTRadioPane extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L; //
	private JPanel btnPanel = null; //
	private JPanel toftPanel = null; //new JPanel(); //������
	private CardLayout cardLayout = new CardLayout(); //��Ƭ����
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
		btnPanel = new WLTPanel(WLTPanel.VERTICAL_TOP_TO_BOTTOM, _bgColor, false); //��ɫ����������!
		btnPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0)); //
		btnPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 0)); ////
		toftPanel = WLTPanel.createDefaultPanel(); //
		toftPanel.setLayout(cardLayout); //��β���
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
	 * ����һ��ҳǩ..
	 * @param _title
	 */
	public void addTab(String _title, String _key, String _img, Component _component) {
		li_count++; //
		JRadioButton btn = null; //
		if (_img == null) { //���ûͼƬ
			btn = new JRadioButton(_title);
			btn.setOpaque(false);
			btnPanel.add(btn); //�����ͼƬ,��ĳɰ�û��,�������Labe���ֵĵ�����!!!
		} else { //�����ͼƬ,��������Label
			btn = new JRadioButton();
			btn.setOpaque(false);
			btnPanel.add(btn); //�����ͼƬ,��ĳɰ�û��,�������Labe���ֵĵ�����!!!
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
		btn.setFocusable(true); //����и�������Ӻÿ���,�ؼ��ǿ���֧�ּ��̲���!!!!
		if (li_count == 0 && isAutoSelectFirst) { //��ҳ������ӭ��ʾʱ�Ͳ��Զ�ѡ��!
			btn.setSelected(true); ///
		}
		btn.putClientProperty("title", _title); //
		btn.putClientProperty("pos", new Integer(li_count)); //
		if (_key != null) {
			btn.putClientProperty("key", _key); //
		}
		btn.addActionListener(this); //
		al_btns.add(btn); //
		toftPanel.add(_component, _title); //����ĳ����

		//btnPanel.repaint(); //
	}

	/**
	 * ����keyȡ��ĳһ��ҳ��
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
	 * �����ť����..
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
	 * ȡ�����а�ť
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

	//�Ƿ�ĳ����ť��ѡ��?
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
	 * ���ӱ仯�����¼�
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
