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
 * �ؼ���!!��������ϵͳ��ҳǩ������һ�����ܵ�ʱ,��ÿ��AbstractworkPanel��װһ�µ����,��ν��װ���������һ������˵����,����ǰλ�ã�...->...->,�ұ߻��а����رյ�!!
 * �ұ߹�������ÿ��ҳǩ�е�Panel,��Panel��ʵ����һ��"��ǰλ�õ�˵��"����һ��WorkTabbedPanel
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
	private WLTTabbedPane matherTabbedPane = null; // ĸ��
	private JFrame matherFrame = null; // ĸ��Frame
	private TBUtil tbutil = null;
	boolean maxed = false;
	boolean northPanelVisible = true;
	private JLabel label; //��ʾ��ǰ·����label
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
	 * ��������һ��˵��!!!
	 * @return
	 */
	private JPanel getNorthPanel() {
		label = new JLabel(title);
		label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		label.setOpaque(false);
		// label.setForeground(new java.awt.Color(254, 115, 17)); //�ۻ�ɫ
		label.setForeground(LookAndFeel.appLabelHighlightFontcolor);
		if (ClientEnvironment.isAdmin()) {
			label.setToolTipText("����:" + workPanel.getClass().getName()); //
		}

		//������ť!!!
		btn_help = new WLTButton(UIUtil.getImage("office_030.gif")); //
		btn_help.setBorder(BorderFactory.createEmptyBorder()); //
		btn_help.setRightBtnShowInfo(false); //
		btn_help.setFocusable(false);
		btn_help.setCursor(new Cursor(Cursor.HAND_CURSOR)); //����ͼ��Ч���о�����Щ!��xch/2012-02-27��
		btn_help.setPreferredSize(new Dimension(17, 17)); //
		if (ClientEnvironment.isAdmin()) {
			btn_help.setToolTipText("�鿴����!Shift+������Բ鿴��������,Ctrl+����ɲ鿴���ܵ�����"); //
		} else {
			btn_help.setToolTipText("�鿴����"); //��ͨ�û���¼��ʾ,���ݾ���ͻ��϶������������һ����ʾ!! xch(2012-02-23)
		}
		btn_help.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.isShiftDown()) {
					MessageBox.show(btn_help, "ʵ�ʼ��ص�����:[" + workPanel.getClass().getName() + "]"); //
				} else if (e.isControlDown() && ClientEnvironment.isAdmin()) {//����Ա��ݵ�½�ſ����� [����2012-12-05]
					showMenuInfo(); //
				} else {
					onHelp();
				}
			}
		});

		//�رհ�ť!!!
		//		btn_closeme = new WLTButton(UIUtil.getImage("closewin.gif")); // closewin.gif
		btn_closeme = new WLTButton(new ImageIcon(TBUtil.getTBUtil().getImageScale(UIUtil.getImage("zt_031.gif").getImage(), 13, 13))); // closewin.gif
		btn_closeme.setCursor(new Cursor(Cursor.HAND_CURSOR));
		//		private ImageIcon closeImg = new ImageIcon(TBUtil.getTBUtil().getImageScale(UIUtil.getImage("zt_031.gif").getImage(), 13, 13));
		btn_closeme.setFocusable(false);
		btn_closeme.setPreferredSize(new Dimension(17, 17)); //		
		btn_closeme.setToolTipText("�رձ�ҳ"); //
		btn_closeme.setRightBtnShowInfo(false); //�ұ߲���ʾĬ�ϵ�info
		btn_closeme.addCustActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeMe(); //
			}
		});

		JPanel panel_east = new JPanel(new FlowLayout(FlowLayout.RIGHT, 1, 0)); //
		panel_east.setOpaque(false);
		panel_east.setBackground(LookAndFeel.systabbedPanelSelectedBackground); // ����
		panel_east.add(btn_help);
		//		panel_east.add(btn_closeme);
		panel_east.setPreferredSize(new Dimension(100, 19)); //
		btn_help.setVisible(true); //
		//		btn_closeme.setVisible(true); //

		WLTPanel panel = new WLTPanel(BackGroundDrawingUtil.VERTICAL_TOP_TO_BOTTOM, LookAndFeel.systabbedPanelSelectedBackground, false); // ��ɫ���ϵͳҳǩѡ��ʱ����ɫ!!!��������Ȼ�γ�һ��,��������ǰ���������ǶϿ����ĸо�!!!

		panel.setLayout(new BorderLayout());
		panel.setPreferredSize(new Dimension(2000, 19)); //
		panel.add(label, BorderLayout.CENTER);
		panel.add(panel_east, BorderLayout.EAST); //
		panel.setBorder(BorderFactory.createEmptyBorder()); //
		back = getItemPanel("�� ��", new Color(9, 70, 160));
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

	//�õ����صİ�ť
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
	 * ��������鿴!!
	 */
	private void onHelp() {
		try {
			if (ClientEnvironment.isAdmin()) {
				//���ܵ�����׷�� setHelp()����,����String������Ϣ �����/2013-02-26��
				//��ǰ���߼���: A �ڸ�RegisterMenu.xml���ҹ��ܵ�����,����,��help����ֵ,����,�Ҹð���help.txt,����,��help����ֵ��Ӧ������Ϣ
				//            B pub_menu����word�����ļ���
				//            Զ�̵��û�ͬʱ����A Bֵ
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
			
			String str_thisClsName = workPanel.getClass().getName(); //��������������!!!
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			String[] str_helpInfo = service.getMenuHelpInfo(str_menuid, str_thisClsName); //������Ϣ,�������ӵĹ���,���ȴ�help�ļ����ҹؼ�����,������word�ļ�!!��xch/2012-02-27��
			String str_helpText = str_helpInfo[0]; //
			if (str_helpText != null) {
				//Container _parentContainer, Object _msg, String _title, int _msgType, Object[] _options, int _initValue, int _width, int _height, boolean _isCanCopyText
				int li_return = MessageBox.showOptionDialog(this, str_helpText, "���ٰ����鿴", WLTConstants.MESSAGE_QUESTION, new String[] { " ȷ �� ", "�鿴Word����" }, 0, 800, 350, false, false); //
				if (li_return == 1) { //�������ġ��鿴Word������,���ٴε��ô�Word�ļ�!!!
					openWordFileHelp(str_helpInfo[1]); //
				}
			} else { //���������û�ж���help��Ϣ,�������ǰ�Ļ���,ֱ�Ӵ�Word!!
				openWordFileHelp(str_helpInfo[1]); //��Word�����ļ�!!!
			}
		} catch (Exception e1) {
			MessageBox.showException(this, e1); //
		}
	}

	/**
	 * �鿴��������ֱ�Ӱ����ı����ƺ�,Ϊ�˷�װ����,����Word�ļ�Ū��һ������!��������ṹ��������xch/2012-02-27��
	 * @param filename
	 */
	private void openWordFileHelp(String filename) {
		if (filename == null || filename.trim().equals("")) { //
			MessageBox.show(this, "�ù��ܵ�û�ж���Word�����ĵ�,���ڲ˵������ж���!");
			return; //
		}
		/***
		 * Gwang 2012-09-17�޸�
		 * ��ϵͳ�еİ����ĵ��̶�����webroot��helpĿ¼��[E:\TomCat5.5.23\webapps\icam\help\doc] 
		 * ����Ҫ�ֶ����ƹ�ȥ����ϵͳ����ʱ�ĸ��Ӷ�, �ر����ڲ�Ʒ��װ��ʱ
		 */
		String helpPath = System.getProperty("APP_DEPLOYPATH") + "help/doc/";

		String openType = getTBUtil().getSysOptionStringValue("�����ĵ��Ƿ�ɱ༭", "N"); //ϵͳĬ�ϵ�������ĵ�Ϊǧ���򿪣����ɱ༭��
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
			officeVO.setIfShowResult(false); //����ʾ���������ʾ��
			officeVO.setIfselfdesc(true); //�ؼ�
			officeVO.setSubdir(helpPath);
			officeVO.setAbsoluteSeverDir(true);
			final BillOfficePanel pane = new BillOfficePanel(filename, officeVO); //ʹ��Office����,��Ϊ����������
			BillDialog dialog = new BillDialog(this) { //�رնԻ���ʱ����ǧ���ؼ���
				public boolean beforeWindowClosed() {
					pane.getWebBrowser().executeScript("swingCall('closedoc');");
					return true;
				}
			};
			dialog.getContentPane().add(pane);
			dialog.maxToScreenSizeBy1280AndLocationCenter();
			dialog.setTitle(subject + "-�����ĵ�");
			dialog.setVisible(true);
		} else {
			UIUtil.openRemoteServerFile("realdir", helpPath + filename); // //			
		}

	}

	private void closeMe() {
		try {
			workPanel.beforeDispose(); // ����һ�¹������Ĺر�ǰ�Ĳ���,������ʱ��Ҫֹͣ��һЩ�߳�!!!
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
