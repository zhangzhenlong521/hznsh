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
 * ��ҳ��ݷ�ʽ���! ���Ƿ���Noted��IPhoneһ���ڴ�ť!!
 * @author xch
 *
 */
public class IndexShortCutPanel extends JPanel implements MouseListener, AdjustmentListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private String[] str_firstLevels = null; //
	private HashVO[] allNodeVOs = null; //����Ҷ�ӽ�������!!!

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

	//ָ��������ɫ!!!
	public IndexShortCutPanel(HashVO[] _allHVs, boolean _isCardLayout) {
		if (_isCardLayout) { //����Ǹ���ģ��ֲ��,���Ӳ���!
			this.allNodeVOs = _allHVs; //
			initialize(); //
		} else {
			this.setLayout(new BorderLayout()); //
			this.add(getOneLevelAllBtnPanel(_allHVs, colors[new Random().nextInt(colors.length)], null), BorderLayout.CENTER); //
		}
	}

	//����ҳ��!!ʹ�ò�!!
	private void initialize() {
		this.setLayout(new BorderLayout()); //
		str_firstLevels = getFirstLevelNodeNames(); //��һ���嵥!

		WLTTabbedPane tabbed = new WLTTabbedPane(); //
		myShortCutPanel = new JPanel(new BorderLayout()); //
		myShortCutPanel.add(getMyShortCutPanel(str_firstLevels)); //
		tabbed.addTab("�ҵĳ��ù���", UIUtil.getImage("office_110.gif"), myShortCutPanel); //

		JPanel allMenuPanel = new JPanel(new BorderLayout()); //
		allMenuPanel.add(getNorthBtnPanel(str_firstLevels), BorderLayout.NORTH); //
		toftPanel = new JPanel(); //
		cardLayout = new CardLayout(); //
		toftPanel.setLayout(cardLayout); //

		//ѭ��������������!
		for (int i = 0; i < str_firstLevels.length; i++) { //����!!!
			int li_colorIndex = i % colors.length; //�ڼ���,ÿ5����һ����ɫ!!
			JPanel itemPanel = getOneLevelAllBtnPanel(str_firstLevels[i], colors[li_colorIndex]); //
			toftPanel.add(itemPanel, str_firstLevels[i]); //
		}
		allMenuPanel.add(toftPanel, BorderLayout.CENTER); //

		tabbed.addTab("�ҵ����й���", UIUtil.getImage("office_083.gif"), allMenuPanel); //
		this.add(tabbed); //

		//�����Ҽ������Ĳ˵�!!
		popMenu = new JPopupMenu(); //
		menuItem_add = new JMenuItem("��������ÿ�"); //
		menuItem_remove = new JMenuItem("�ӳ��ÿ����Ƴ�"); //

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
	 * �����ҵĳ��ù��ܿ����!
	 * @param _firstLevelNames
	 * @return
	 */
	private JPanel getMyShortCutPanel(String[] _firstLevelNames) {
		//�ȴ����ݿ����ҵ��ҵ���������!!
		try {
			String str_loginUserId = ClientEnvironment.getCurrLoginUserVO().getId(); //

			//���ҳ��ҵ����н�ɫ!!
			String[] str_myallroles = UIUtil.getStringArrayFirstColByDS(null, "select rolecode from v_pub_user_role_1 where userid='" + str_loginUserId + "'"); //
			String[] str_commrole = new String[] { "һ���û�", "һ��Ա��", "����Ա��", "�����û�" }; //
			String[] str_span = new String[str_myallroles.length + str_commrole.length]; //
			System.arraycopy(str_myallroles, 0, str_span, 0, str_myallroles.length); //
			System.arraycopy(str_commrole, 0, str_span, str_myallroles.length, str_commrole.length); //����!!

			//ȥ��ݷ�ʽ�����ҵ��ҵ����п�ݷ�ʽ!!������ɫƥ������Աֱ��ƥ���!!
			HashVO[] hvs_allMenu = UIUtil.getHashVoArrayByDS(null, "select * from pub_user_shortcut where (shorttype='��ɫ' and userid in (" + tbUtil.getInCondition(str_span) + ")) or ((shorttype='��Ա' or shorttype is null ) and userid='" + str_loginUserId + "')");

			ArrayList al_matchedVO = new ArrayList(); //��Ȼ����һЩ��ݷ�ʽ,������ÿ�ݷ�ʽ���ҵ�Ŀǰ��Χ֮��!!������������㲻Ҫ��,���ǿ���Ȩ�޷����䶯,�����������Ŀ�ݷ�ʽ,����ȴû��Ȩ�ޣ����ܵ㶼����������!����Ҫ������һ������,����׳!
			for (int i = 0; i < allNodeVOs.length; i++) {
				boolean isMatch = false; //
				for (int j = 0; j < hvs_allMenu.length; j++) {
					if (hvs_allMenu[j].getStringValue("menuid").equals(allNodeVOs[i].getStringValue("id"))) {
						isMatch = true; //
						break; //
					}
				}
				if (isMatch) {
					al_matchedVO.add(allNodeVOs[i]); //����
				}
			}
			HashVO[] hvs = (HashVO[]) al_matchedVO.toArray(new HashVO[0]); // 
			return getOneLevelAllBtnPanel(hvs, null, _firstLevelNames); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			JPanel panel = new JPanel(new FlowLayout()); //
			panel.add(new JLabel("�����ҵĳ��ù���ʱ�����쳣:<" + ex.getClass().getName() + ">" + ex.getMessage())); //
			return panel; //
		}

	}

	/**
	 * ȡ��ĳ��һ��ģ���µ����а�ť!!
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
	 * ȡ��ĳһ��,�������а�ťŪ����!
	 * @param _hvs
	 * @param _bgcolor
	 * @param _firstlevelNames
	 * @return
	 */
	private JPanel getOneLevelAllBtnPanel(HashVO[] _hvs, Color _bgcolor, String[] _firstlevelNames) {
		JPanel contentPanel = new JPanel(); //
		contentPanel.setOpaque(false); //͸��!!
		//contentPanel.setBackground(Color.WHITE);  //
		contentPanel.setLayout(null); //
		int li_onewidth = 85; //ÿ����ť�Ŀ��,����˾���ԭ���İ�ť̫��!!
		int li_oneheight = 55; //ÿ����ť�ĸ߶�
		int li_onecount = 7; //һ�м�����ť
		int li_x_space = 45; //����ÿ����ť֮��ľ���!
		int li_y_space = 30; //����ÿ����ť֮��ľ���!
		int li_x = 0, li_y = 0; //
		boolean isShortCut = (_bgcolor == null ? true : false); //�Ƿ��ǳ��ù���,�����,����ʾ,�Ҽ������˵�����һ��!

		for (int i = 0; i < _hvs.length; i++) {
			String str_id = _hvs[i].getStringValue("id"); //
			String str_name = _hvs[i].getStringValue("name"); //
			String str_path = _hvs[i].getStringValue("$TreePath"); //
			String str_imgName = _hvs[i].getStringValue("icon"); //
			if (str_imgName == null) {
				str_imgName = "office_054.gif"; //
			}
			int li_div = i / li_onecount; //��,һ�а�8����ť!!
			int li_mod = i % li_onecount; //����
			//li_x = 10 + li_mod * 100 + (li_mod * 12); //
			//li_y = 10 + li_div * 70 + (li_div * 12); //
			li_x = 20 + li_mod * (li_onewidth + li_x_space); //
			li_y = 20 + li_div * (li_oneheight + li_y_space); //

			Color itemBgColor = _bgcolor; //
			if (itemBgColor == null) {
				if (_firstlevelNames != null) {
					for (int j = 0; j < _firstlevelNames.length; j++) { //�������е�һ��!
						if (str_path.startsWith(_firstlevelNames[j])) {
							itemBgColor = colors[j % colors.length]; //// 
							break; //
						}
					}
				}
			}

			JPanel itemPanel = getItemPanel(str_id, str_imgName, str_name, (i + 1) + "." + str_path, itemBgColor, isShortCut); //ȡ��ĳһ����ť�����!!!//( +
			itemPanel.setBounds(li_x, li_y, li_onewidth, li_oneheight); //
			contentPanel.add(itemPanel); //
		}
		int li_all_width = (li_onewidth + li_x_space) * li_onecount + 50; //
		int li_all_height = li_y + li_oneheight + li_y_space + 50; //
		contentPanel.setPreferredSize(new Dimension(li_all_width, li_all_height)); //
		//contentPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE)); //����debugʱ������Ч����

		JPanel containerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); //��ǰ�İ�ť���ǿ���,�о�����,�ĳɾ���,����Ҫһ���������
		//containerPanel.setBorder(BorderFactory.createLineBorder(Color.RED)); //����debugʱ������Ч����
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

	//ȡ�õ�һ��ģ�������!!!
	private String[] getFirstLevelNodeNames() {
		LinkedHashSet hst = new LinkedHashSet(); //
		for (int i = 0; i < allNodeVOs.length; i++) {
			String str_path = allNodeVOs[i].getStringValue("$TreePath"); //
			String[] str_levels = tbUtil.split(str_path, "��"); //
			hst.add(str_levels[0].trim()); //
		}
		return (String[]) hst.toArray(new String[0]); //
	}

	/**
	 * ÿһ����ť
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
		panel.putClientProperty("�Ƿ��ǳ��ù���", _isShortCut ? "Y" : "N"); //
		if (_isShortCut) {
			panel.setToolTipText("<html>" + str_path + "<br><font color=\"FF0000\">�Ҽ����������Ƴ�������!</font></html>"); //
		} else {
			panel.setToolTipText("<html>" + str_path + "<br><font color=\"FF0000\">�Ҽ�����������ӡ�����!</font></html>"); //
		}
		panel.addMouseListener(this); //

		ImageIcon imgIcon = UIUtil.getImage(_imgName); //
		//imgIcon = new ImageIcon(tbUtil.getImageScale(imgIcon.getImage(), 25, 25));  //��ͼƬ�����32*32,��ΪСͼƬ���ÿ�??�������͸��Ч��û��!!!������ʱ������,�Ժ�Ӧ���������취!!
		JLabel imgLabel = new MyAlphaLabel(imgIcon); //ͼƬ!!!
		panel.add(imgLabel, BorderLayout.CENTER); //����!!
		JLabel textLabel = new JLabel(_text, JLabel.CENTER); //
		textLabel.setForeground(textColor); //
		panel.add(textLabel, BorderLayout.SOUTH); ////

		panel.putClientProperty("ImgLabel", imgLabel); //�󶨵�Label
		return panel; //
	}

	public void mouseClicked(MouseEvent _event) {
		JPanel itemPanel = (JPanel) _event.getSource(); //
		String str_menuid = (String) itemPanel.getClientProperty("menuid"); //
		String str_path = (String) itemPanel.getClientProperty("$TreePath"); //

		String str_isShortCut = (String) itemPanel.getClientProperty("�Ƿ��ǳ��ù���"); //
		if (_event.getButton() == MouseEvent.BUTTON3) { //�����������Ҽ�!����ʾ�����˵�!!
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
		if (_event.getSource() == menuItem_add) { //����Ǽ���
			String str_menuId = (String) menuItem_add.getClientProperty("menuid"); //
			String str_treePath = (String) menuItem_add.getClientProperty("$TreePath"); //
			onAddToMyShortCut(str_menuId, str_treePath); //���뵽�ҵĳ��ù�����!!
		} else if (_event.getSource() == menuItem_remove) { //������Ƴ�?
			String str_menuId = (String) menuItem_remove.getClientProperty("menuid"); //
			String str_treePath = (String) menuItem_remove.getClientProperty("$TreePath"); //
			onRemoveFromMyShortCut(str_menuId, str_treePath); //���ҵĳ��ù������Ƴ�!!
		}
	}

	//���뵽�ҵĳ��ù�����!!
	private void onAddToMyShortCut(String _menuId, String _treePath) {
		try {
			String str_loginUserId = ClientEnvironment.getCurrLoginUserVO().getId(); //
			String str_count = UIUtil.getStringValueByDS(null, "select count(*) c1 from pub_user_shortcut where userid='" + str_loginUserId + "' and menuid='" + _menuId + "' and (shorttype='��Ա' or shorttype is null)"); //
			if (Integer.parseInt(str_count) > 0) {
				MessageBox.show(this, "�ù����Ѿ������ҵĳ��ù�������,û�б�Ҫ�ظ����!"); //
				return; //
			}
			InsertSQLBuilder isql = new InsertSQLBuilder("pub_user_shortcut"); //
			isql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_PUB_USER_SHORTCUT")); //����
			isql.putFieldValue("userid", str_loginUserId); //
			isql.putFieldValue("menuid", _menuId); //
			isql.putFieldValue("shorttype", "��Ա"); //
			UIUtil.executeUpdateByDS(null, isql); //

			myShortCutPanel.removeAll(); //
			myShortCutPanel.setLayout(new BorderLayout()); //
			myShortCutPanel.add(getMyShortCutPanel(str_firstLevels)); //�ȼ������й���!!
			myShortCutPanel.updateUI(); //
			MessageBox.show(this, "�����ܵ㡾" + _treePath + "��������ҵĳ��ù����гɹ�!"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	//���ҵĳ��ù������Ƴ�!!
	private void onRemoveFromMyShortCut(String _menuId, String _treePath) {
		try {
			if (!MessageBox.confirm(this, "���Ƿ�����뽫�ù��ܵ㡾" + _treePath + "���ӳ��ù��ܿ����Ƴ���?")) {
				return;
			}
			String str_loginUserId = ClientEnvironment.getCurrLoginUserVO().getId(); //
			String str_count = UIUtil.getStringValueByDS(null, "select count(*) c1 from pub_user_shortcut where userid='" + str_loginUserId + "' and menuid='" + _menuId + "' and (shorttype='��Ա' or shorttype is null)"); //
			if (Integer.parseInt(str_count) <= 0) { //���û�ҵ��ҵ�,��˵�������ɫ������!
				MessageBox.show(this, "��������ĳ����ɫ�ĳ��ù���(�����þ��иý�ɫ),ֻ�й���Ա�����Ƴ�!"); //
				return; //
			}
			UIUtil.executeUpdateByDS(null, "delete from pub_user_shortcut where userid='" + str_loginUserId + "' and menuid='" + _menuId + "' and (shorttype='��Ա' or shorttype is null)"); //
			myShortCutPanel.removeAll(); //
			myShortCutPanel.setLayout(new BorderLayout()); //
			myShortCutPanel.add(getMyShortCutPanel(str_firstLevels)); //�ȼ������й���!!
			myShortCutPanel.updateUI(); //
			MessageBox.show(this, "���ҵĳ��ù������Ƴ��ɹ�!!\r\n����ע��,�������Ա������Ϊĳ����ɫ�ĳ��ù���,�����־����иý�ɫ,����Ȼ�����!!\r\n������Ա���õ�[��ɫ�ೣ�ù���]�ǲ��ܱ�һ���û��Ƴ���!"); //
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
				Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.65f); //����ʱ͸��!
				g2d.setComposite(alphaComp);
				super.paint(g2d);
			} else {
				Graphics2D g2d = (Graphics2D) g;
				Composite alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f); //��͸��!!!!
				g2d.setComposite(alphaComp);
				super.paint(g2d);
			}
		}

	}

}
