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
 * ģ��OutLook�ĳ�����Ŀؼ�!!!
 * @author xch
 *
 */
public class WLTOutlookBar extends JPanel implements ActionListener {

	private List tabList = new ArrayList(); //��¼�м���ҳǩ! ������ַ���!!
	private List tabBtnList = new ArrayList();
	private HashMap pages = new HashMap();

	private JPanel panel = null;
	private String selectedTabKey = null;

	private boolean isCanMax = true; //�Ƿ��������!!
	private boolean isMaxMode = false; //�Ƿ�������ģʽ,�����ֻ��ʾĳһ��ҳǩ����!!!
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
	 * ������ť
	 * @param title
	 * @param icon
	 * @param _pos
	 * @return
	 */
	private JButton createButton(final String title, ImageIcon icon, int _pos) {
		int li_dirtype = BackGroundDrawingUtil.HORIZONTAL_RIGHT_TO_LEFT; //
		if ("����".equals(tbUtil.getSysOptionStringValue("��ҳ���밴ť���䷽��", null))) { //�еĿͻ�ϲ�����½��䣡
			li_dirtype = BackGroundDrawingUtil.VERTICAL_TOP_TO_BOTTOM2; //
		}
		WLTButton btn = new WLTButton(title, icon, li_dirtype); // BackGroundDrawingUtil.
		btn.setPreferredSize(new Dimension(0, 27)); //
		btn.setBackground(LookAndFeel.desktop_OutLookBarBg);//������߳��빤�����ı�����ɫ�����������!!
		btn.setHorizontalAlignment(textAlign); //
		btn.setFont(btnFont); //
		btn.setCursor(handCursor); //
		btn.setRightBtnShowInfo(false); //Ĭ���Ҽ��ᵯ���˵�,��ʾ��ť�����Ϣ,�����ﲻ��Ҫ!����ͻ��϶��᲻ϲ��!��xch/2012-02-27��
		btn.addCustActionListener(this); //
		return btn;
	}

	/**
	 * �л�ĳһ����
	 */
	public void switchTab(JButton _btn) {
		Boolean isExpand = (Boolean) _btn.getClientProperty("isexpand"); //�Ƿ�չ��!
		setAllBtnClientProp("isexpand", false); //���ж�����!!!
		setAllBtnClientProp("isRightMax", false); //���ж�����!!!
		if (isExpand == null || !isExpand.booleanValue()) { //������ʵ���ٴε��ʱ��������ȥ!!!
			_btn.putClientProperty("isexpand", Boolean.TRUE); //�������Ϊչ��!
		}
		selectedTabKey = _btn.getText(); //
		repaintAllBtns(); //�ػ�!!!
		this.revalidate(); //
		this.repaint(); //
	}

	//���ĳһ������!����ʱ����ǳ���ʱ,���е�Ų����ĸо�! �ǳ���Ҫ���������붼���ص�,ֻ��ĳһ������!!
	public void maxOneTab(JButton _btn) {
		setAllBtnClientProp("isexpand", false); //���ж�����!!!
		_btn.putClientProperty("isexpand", Boolean.TRUE); //�������Ϊչ��!
		_btn.putClientProperty("isRightMax", Boolean.TRUE); //�������Ϊչ��!
		repaintOneBtn(_btn); //
		this.revalidate(); //
		this.repaint(); //
	}

	public void actionPerformed(ActionEvent _event) {
		int li_modid = _event.getModifiers(); //
		boolean isCtrlDown = (li_modid == 18 ? true : false); //�Ƿ���Ctrl��??
		JButton btn = (JButton) _event.getSource(); //ȡ�ð�ť!!
		if (isCanMax) { //���֧������!!!
			if (isMaxMode) { //�����������ģʽ,��ֱ���Ƿ���!�����Ƿ���Ctrl
				btn.putClientProperty("isexpand", Boolean.FALSE); //�������Ϊչ��!
				switchTab(btn); //
				isMaxMode = false; //
			} else {
				if (isCtrlDown) { //�������Ctrl��!!
					maxOneTab(btn); //����!!!
					isMaxMode = true; //
				} else {
					switchTab(btn); //
				}
			}
		} else {
			switchTab(btn); //
		}

	}

	//�������а�ť������
	private void setAllBtnClientProp(String _key, boolean _isExpand) {
		for (int i = 0; i < tabBtnList.size(); i++) { //�������а�ť
			JButton btn = (JButton) tabBtnList.get(i); //��ť!!!
			btn.putClientProperty(_key, new Boolean(_isExpand)); //
		}
	}

	//ֻ��ĳһ����ť!!!
	private void repaintOneBtn(JButton _btn) {
		int li_dirtype = WLTPanel.INCLINE_NW_TO_SE; //
		panel = WLTPanel.createDefaultPanel(new BorderLayout(), li_dirtype); //
		_btn.setCursor(handCursor); //
		_btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.GRAY), BorderFactory.createEmptyBorder(2, 6, 2, 2)));
		_btn.setForeground(Color.RED); //
		_btn.setToolTipText("�������ԭ��"); //
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
	 * ���»���ҳǩ,�ؼ��߼�����ض�����!!!
	 */
	private void repaintAllBtns() {
		int li_dirtype = WLTPanel.INCLINE_NW_TO_SE; //
		panel = WLTPanel.createDefaultPanel(new BorderLayout(), li_dirtype);
		this.removeAll(); //
		this.setLayout(new BorderLayout()); //
		this.add(panel, BorderLayout.CENTER); //
		Color color = new Color(55, 55, 55); //
		for (int i = 0; i < tabList.size(); i++) { //�������а�ť
			JPanel itemPanel = WLTPanel.createDefaultPanel(new BorderLayout(), li_dirtype);
			JButton btn = (JButton) tabBtnList.get(i); //��ť!!!
			btn.setCursor(handCursor); //
			if (isCanMax) {
				btn.setToolTipText("��Ctrl������ɷŴ�"); //
			}
			int li_line = (i == 0 ? 1 : 0);
			btn.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(li_line, 1, 1, 1, Color.GRAY), BorderFactory.createEmptyBorder(2, 6, 2, 2)));
			itemPanel.add(btn, BorderLayout.NORTH); //��������밴ť!!!
			panel.add(itemPanel, BorderLayout.NORTH);

			Boolean isExpand = (Boolean) btn.getClientProperty("isexpand"); //�Ƿ�չ��!
			String tabTitle = (tabList.get(i)==null)?"":tabList.get(i).toString(); //  Ԭ�������ģ��������պ���admin��¼����ȥ��bug
			if (isExpand != null && isExpand.booleanValue() && tabTitle.equals(selectedTabKey)) { //������ǵ��е�,���Ҿ���Ҫ��չ����!������ʵ���ٴε��ʱ��������ȥ!!!
				btn.setForeground(Color.RED); //
				panel.add((JComponent) pages.get(tabTitle), BorderLayout.CENTER); //�������Ӧ����
				JPanel newPanel = WLTPanel.createDefaultPanel(new BorderLayout(), li_dirtype);
				panel.add(newPanel, BorderLayout.SOUTH);
				panel = newPanel;
			} else {
				btn.setForeground(color); //
				JPanel newPanel = WLTPanel.createDefaultPanel(new BorderLayout(), li_dirtype); //
				panel.add(newPanel, BorderLayout.CENTER); //���¸�ֵ,�γɵݹ�Ч��!!!
				panel = newPanel; //
			}
		}
	}

	public int getTabCount() {
		return tabList.size();
	}

	public void setSelectedIndex(int index) {
		setAllBtnClientProp("isexpand", false); //
		JButton btn = (JButton) tabBtnList.get(index); ////��ť!!!
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
