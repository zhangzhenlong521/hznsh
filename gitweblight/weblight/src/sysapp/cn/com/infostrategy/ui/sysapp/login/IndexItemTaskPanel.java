package cn.com.infostrategy.ui.sysapp.login;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.CommonDate;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.sysapp.login.DeskTopNewGroupVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTHrefLabel;
import cn.com.infostrategy.ui.common.WLTHtmlButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.IconFactory;

/**
 * ��ҳĳһ��������������!!!
 * ��400*205
 * @author xch
 *
 */
public class IndexItemTaskPanel extends JPanel implements ActionListener, MouseListener {

	private static final long serialVersionUID = 1L;
	private DeskTopPanel deskTopPanel = null; //
	private DeskTopNewGroupVO taskVO = null; //
	private WLTHrefLabel[] labels = null; //
	private JButton btn_refresh, btn_more; //
	private int allWidth = 360; //
	private boolean isMore = false; //
	private boolean isRefresh = true; //
	private TBUtil tbUtil = new TBUtil(); //

	public IndexItemTaskPanel(DeskTopPanel _deskTopPanel, DeskTopNewGroupVO _taskVO) {
		this(_deskTopPanel, _taskVO, 360); //
	}

	public IndexItemTaskPanel(DeskTopPanel _deskTopPanel, DeskTopNewGroupVO _taskVO, int _width) {
		this.deskTopPanel = _deskTopPanel; //
		this.taskVO = _taskVO; //
		this.allWidth = _width; //
		initialize(null); //
	}

	public IndexItemTaskPanel(DeskTopPanel _deskTopPanel, DeskTopNewGroupVO _taskVO, int _width, boolean _isMore) {
		this.deskTopPanel = _deskTopPanel; //
		this.taskVO = _taskVO; //
		this.allWidth = _width; //
		this.isMore = _isMore; //
		initialize(null); //
	}

	public IndexItemTaskPanel(DeskTopPanel _deskTopPanel, DeskTopNewGroupVO _taskVO, int _width, boolean _isMore, boolean _isLazyLoad) {
		this.deskTopPanel = _deskTopPanel; //
		this.taskVO = _taskVO; //
		this.allWidth = _width; //
		this.isMore = _isMore; //
		initialize(new Boolean(_isLazyLoad)); //
	}

	/**
	 * ����ҳ��!!!
	 */
	private void initialize(Boolean _isLazyLoad) {
		this.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); //
		boolean isLazyLoad = false; //
		if (_isLazyLoad == null) { //���û����ȷָ��,��ʹ�ò�������!!
			isLazyLoad = "Y".equalsIgnoreCase(taskVO.getDefineVO().getIsLazyLoad()); //�Ƿ���װ��??
		} else { //�����ȷָ��,��ʹ����ȷָ����!!
			isLazyLoad = _isLazyLoad.booleanValue(); //
		}
		if (isLazyLoad) { //�������װ��,�����߳�!
			this.setBackground(LookAndFeel.defaultShadeColor1); //new Color(226, 236, 246)
			this.setLayout(new BorderLayout()); //
			this.setOpaque(false); //͸��
			this.add(new JLabel("���ڼ�������....", UIUtil.getImage("office_006.gif"), SwingConstants.CENTER), BorderLayout.CENTER); //����ʾ���ڼ�������
			this.add(getTitlePanel(taskVO.getDefineVO().getTitle(), taskVO.getDefineVO().getImgicon(), taskVO.getDefineVO().getTitlecolor(), false), BorderLayout.NORTH); //
			new Timer().schedule(new TimerTask() { //��һ���߳�,ȥ����������!!
						@Override
						public void run() { //�¿�һ���̼߳���ҳ��!
							lazyLoad(); //��װ��!!
						}
					}, 0); //���೤ʱ���ʼִ��!! 
		} else {
			loadRealPanel(); //
		}
	}

	/**
	 * ��װ���߼�!����Ҫȡ������,Ȼ��ҳ��!!
	 */
	private void lazyLoad() {
		try {
			String str_clasName = taskVO.getDefineVO().getDatabuildername(); //���ݼ�������!

			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			HashVO[] hvs = service.getDeskTopNewGroupVOData(str_clasName, ClientEnvironment.getCurrLoginUserVO().getCode(), taskVO.getDefineVO()); //������ܺ�ʱ!!

			//׷��������Ч�����жϡ����/2012-07-30��
			if ((taskVO.getDefineVO().getDatatype() == null || taskVO.getDefineVO().getDatatype().equals("����")) && (hvs != null)) {
				String expfield = (String) taskVO.getDefineVO().getOtherconfig().get("��Ч����ƥ���ֶ�");
				int expdate = 0;
				ArrayList al_hvs = new ArrayList();

				try {
					expdate = Integer.parseInt((String) taskVO.getDefineVO().getOtherconfig().get("��Ч����"));
				} catch (RuntimeException e) {
					expdate = 0;
				}

				if (expfield != null && !expfield.trim().equals("") && expdate > 0) {
					CommonDate cd = new CommonDate(new Date());
					for (int i = 0; i < hvs.length; i++) {
						if (hvs[i].getDateValue(expfield) != null && cd.getDaysAfter(new CommonDate(hvs[i].getDateValue(expfield))) < expdate) {
							al_hvs.add(hvs[i]);
						}
					}
					hvs = (HashVO[]) al_hvs.toArray(new HashVO[0]);
				}
			} //yangke			

			this.taskVO.setDataVOs(hvs);
			this.removeAll(); //�Ȳ���ҳ������������!!
			loadRealPanel(); //ʵ�ʼ���!
			this.updateUI(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

	}

	/**
	 * ����ʵ��ҳ��!!
	 */
	private void loadRealPanel() {
		isRefresh = tbUtil.getSysOptionBooleanValue("��ҳ�������Ƿ���ˢ�°�ť", true); //������������Ȼ��ϲ��ˢ��
		String str_title = taskVO.getDefineVO().getTitle(); //
		String str_imgName = taskVO.getDefineVO().getImgicon(); //
		String str_logoimg = taskVO.getDefineVO().getLogoimg(); //
		String str_titleColor = taskVO.getDefineVO().getTitlecolor(); //
		//str_imgName = (str_imgName == null ? "office_066.gif" : str_imgName);
		//str_logoimg = (str_logoimg == null ? "90_60_01.gif" : str_logoimg);

		this.setBackground(LookAndFeel.defaultShadeColor1); //new Color(226, 236, 246)
		this.setLayout(null); //
		this.setOpaque(false); //͸��

		//������ͼƬ....
		int li_y = 0; //
		JPanel titlePanel = getTitlePanel(str_title, str_imgName, str_titleColor, true); //�������!!
		titlePanel.setBounds(1, 1, allWidth - 2, 30); //
		this.add(titlePanel); //
		li_y = li_y + 33; //

		//��ͼƬ...
		if (str_logoimg != null && !str_logoimg.trim().equals("")) {
			JLabel img1 = new JLabel(UIUtil.getImage(str_logoimg)); //
			img1.setBounds(5, li_y, 90, 60); //
			this.add(img1); //
		}

		HashVO[] hvs = taskVO.getDataVOs(); //���ݶ���!!
		if (isMore) { //����Ǹ���!!
			labels = new WLTHrefLabel[hvs == null ? 0 : hvs.length]; //
		} else {
			labels = new WLTHrefLabel[7]; //?
		}

		//��������������
		Color color1 = LookAndFeel.desktop_Foreground; //new Color(2, 68, 152); //������ɫ,¦ƽ˵������Ŀ�пͻ�ҲҪ������!!
		for (int i = 0; i < labels.length; i++) {
			if (hvs != null && i < hvs.length) { //��������������
				labels[i] = new WLTHrefLabel("��" + hvs[i] == null ? "" : hvs[i].toString()); //
				labels[i].setToolTipText("��" + hvs[i] == null ? "" : hvs[i].toString()); //

				//������ҳnewͼƬ��ʾ���� �����/2012-11-05��
				String newfield = (String) taskVO.getDefineVO().getOtherconfig().get("����Ϣ��ʾ����ƥ���ֶ�");
				int newday = 10;
				try {
					newday = Integer.parseInt((String) taskVO.getDefineVO().getOtherconfig().get("����Ϣ��ʾ����"));
				} catch (RuntimeException e) {
					newday = 10;
				}

				if (!(newfield == null || newfield.trim().equals(""))) {
					CommonDate cd = new CommonDate(new Date());
					if (hvs[i].getDateValue(newfield) != null && cd.getDaysAfter(new CommonDate(hvs[i].getDateValue(newfield))) < newday) {
						labels[i].setIcon(UIUtil.getImage("new.gif"));
						labels[i].setHorizontalTextPosition(SwingConstants.LEADING);
					}
				} else {
					if (i < taskVO.getDefineVO().getNewcount()) { //ǰ���������и�newͼ��!!��ʾ�����µ���Ϣ!!!
						labels[i].setIcon(UIUtil.getImage("new.gif")); //
						labels[i].setHorizontalTextPosition(SwingConstants.LEADING); //
					}
				}

				labels[i].putClientProperty("HashVO", hvs[i]); //
				labels[i].putClientProperty("index", i); //
			} else {
				labels[i] = new WLTHrefLabel("��"); //
			}
			labels[i].setOpaque(false); //
			labels[i].setFont(LookAndFeel.font); //
			labels[i].setForeground(color1); //
			if (str_logoimg != null && !str_logoimg.trim().equals("")) { //����д�ͼ��,��ǰ���������ұߵ�!
				labels[i].setBounds(((i < 3) ? 100 : 8), li_y, ((i < 3) ? (allWidth - 90) : allWidth) - 15, 25); //
			} else { //���û�д�ͼƬ!
				labels[i].setBounds(8, li_y, allWidth - 15, 25); //
			}
			//			labels[i].addActionListener(this); //
			labels[i].addMouseListener(this); //�Ҽ����Բ鿴HashVO����!!
			this.add(labels[i]); //
			li_y = li_y + 25;
		}
		this.setPreferredSize(new Dimension(allWidth, li_y)); //
	}

	//�����������!
	private JPanel getTitlePanel(String _title, String _imgName, String _titleColor, boolean _isHaveRightBtn) {
		_imgName = (_imgName == null ? "office_066.gif" : _imgName);
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
		panel.setOpaque(false);
		JLabel label_title = new JLabel(" " + _title, UIUtil.getImage(_imgName), JLabel.LEFT); //
		label_title.setOpaque(false);
		label_title.setFont(LookAndFeel.desktop_GroupTitleFont); //����������Ҫ����ҳ��������ִܴ�!���˲�����������Ӱ��,�ָ��˸�����!!
		if (_titleColor != null && !_titleColor.trim().equals("")) { //�����������ɫ!!
			int li_pos = _titleColor.indexOf("@"); //����û��@����������?��Ϊ������Ŀ���е���ı�����ɫ��������һ��!
			if (_titleColor.indexOf("@") > 0) { //�����@����
				label_title.setForeground(tbUtil.getColor(_titleColor.substring(0, li_pos), Color.BLACK)); //ǰ����ɫ!!
				panel.setBackground(tbUtil.getColor(_titleColor.substring(li_pos + 1, _titleColor.length()), Color.BLACK)); //������ɫ!!��Ϊlabel��͸����,����Ҫ����panel��!!
			} else { //���û�ж���,��Ĭ��ʹ�ú�ɫ!
				label_title.setForeground(tbUtil.getColor(_titleColor, Color.BLACK)); //ֻ��ǰ��!
			}
		}
		panel.add(label_title, BorderLayout.CENTER); //
		if (_isHaveRightBtn && !isMore) {
			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); //
			btnPanel.setBorder(BorderFactory.createEmptyBorder(1, 15, 1, 25));
			btnPanel.setOpaque(false); //
			btn_refresh = new WLTHtmlButton("ˢ��"); //
			btn_more = new WLTHtmlButton("����"); //
			btn_refresh.addActionListener(this); //
			btn_more.addActionListener(this); //
			if (isRefresh) {
				btnPanel.add(btn_refresh); //
			}
			btnPanel.add(btn_more); //
			panel.add(btnPanel, BorderLayout.EAST); //
		}
		panel.setPreferredSize(new Dimension(-1, 30)); //
		return panel; //
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_refresh) {
			onRefreshGroup(false); //ˢ��!!
		} else if (_event.getSource() == btn_more) {
			onMore(); //
		}
	}

	/**
	 * ���ĳ����Ŀ!!
	 * @param _hrefLabel
	 */
	private void onClicked(WLTHrefLabel _hrefLabel) {
		try {
			_hrefLabel.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			HashVO dataVO = (HashVO) _hrefLabel.getClientProperty("HashVO"); //
			if (dataVO == null) {
				return;
			}

			int li_index = (Integer) _hrefLabel.getClientProperty("index"); //
			String str_actionName = taskVO.getDefineVO().getActionname(); //
			if (str_actionName != null && !str_actionName.trim().equals("")) { // ����������Զ����Action,�Լ����߼�������,���繤����.
				AbstractAction action = (AbstractAction) Class.forName(str_actionName).newInstance(); //
				action.putValue("DeskTopNewsDefineVO", taskVO.getDefineVO()); //
				action.putValue("DeskTopNewsDataVO", dataVO); //
				action.putValue("Index", new Integer(li_index)); //
				action.putValue("DeskTopPanel", this.deskTopPanel); //
				action.putValue("IndexItemTaskPanel", this); //
				action.actionPerformed(new ActionEvent(this, 1, "ClickTaskGroup")); // ִ��!!!!
			} else { //
				String str_templetCode = taskVO.getDefineVO().getTempletcode(); //ģ�����!
				if (str_templetCode != null && !str_templetCode.equals("")) {
					BillCardPanel cardPanel = new BillCardPanel(str_templetCode); //
					if (dataVO.getStringValue("id") != null) {
						System.out.println("����ֵ��=[" + dataVO.getStringValue("id") + "]"); //
						cardPanel.queryDataByCondition("id='" + dataVO.getStringValue("id") + "'"); //
					}
					BillCardDialog dialog = new BillCardDialog(this, taskVO.getDefineVO().getTitle(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT); //
					dialog.setVisible(true); // 
				} else {
					System.out.println("û�ж����Զ���Actiob,Ҳû�ж���ģ�����!");
				}
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		} finally {
			_hrefLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
		}
	}

	/**
	 * ˢ��
	 */
	public void onRefreshGroup(boolean _isQuiet) {
		try {
			if (btn_refresh != null) {//������൯����ʱ�����жϻ��д���
				btn_refresh.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			}
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			if (isMore) {
				this.taskVO = service.getDeskTopNewGroupVOs(ClientEnvironment.getCurrLoginUserVO().getCode(), this.taskVO.getDefineVO().getTitle(), true); //����!!
			} else {
				this.taskVO = service.getDeskTopNewGroupVOs(ClientEnvironment.getCurrLoginUserVO().getCode(), this.taskVO.getDefineVO().getTitle(), false); //����!!
			}//BUG
			this.removeAll(); //
			this.setLayout(null); //
			this.loadRealPanel(); //
			this.updateUI(); //
		} catch (Exception ex) {
			if (_isQuiet) {
				ex.printStackTrace();
			} else {
				MessageBox.showException(this, ex); //
			}
		} finally {
			if (btn_refresh != null) {
				btn_refresh.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
			}
		}
	}

	/**
	 * �������!!!
	 */
	private void onMore() {
		try {
			btn_more.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			DeskTopNewGroupVO newGroupVO = service.getDeskTopNewGroupVOs(ClientEnvironment.getCurrLoginUserVO().getCode(), taskVO.getDefineVO().getTitle(), true); //����ǲ�����װ����Ƶ�!��Ϊû��Ҫ!!
			IndexItemTaskPanel newTaskPanel = new IndexItemTaskPanel(this.deskTopPanel, newGroupVO, 600, true, false); //

			JScrollPane scroll = new JScrollPane(newTaskPanel); //
			JScrollBar scrollbar = scroll.getVerticalScrollBar();
			scrollbar.setUnitIncrement(10); //��ǰ��ҳ�������¹�����̫����[����2012-05-31]
			scroll.setOpaque(false); ////

			scroll.getViewport().setOpaque(false); ////
			JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
			contentPanel.add(scroll); //
			BillDialog dialog = new BillDialog(this, "��������", 700, 600); //
			dialog.getContentPane().add(contentPanel); //
			dialog.setVisible(true); //
			onRefreshGroup(true); //ˢ��!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		} finally {
			btn_more.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
		}
	}

	/**
	 * �Ҽ���¼���Բ鿴�󶨵�ʵ������,����Ŀ�����з�����������!!!
	 */
	public void mouseClicked(MouseEvent _event) {
		WLTHrefLabel label = (WLTHrefLabel) _event.getSource(); //
		String str_actionName = taskVO.getDefineVO().getActionname(); //
		String str_dataBuilderName = taskVO.getDefineVO().getDatabuildername(); //

		StringBuffer sb_text = new StringBuffer(); //
		sb_text.append("���ݹ���������=��" + str_dataBuilderName + "��\r\n"); //
		sb_text.append("�ͻ��˵������������=��" + str_actionName + "��\r\n"); //
		sb_text.append("�ü�¼�󶨵�HashVOʵ��ֵ��\r\n"); //
		if (_event.getButton() == MouseEvent.BUTTON3) {
			HashVO dataVO = (HashVO) label.getClientProperty("HashVO"); //
			if (dataVO == null) {
				sb_text.append("û�а�һ��HashVO����!!\r\n"); //
				MessageBox.show(this, sb_text.toString()); //
			} else {
				String[] str_keys = dataVO.getKeys(); //
				for (int i = 0; i < str_keys.length; i++) {
					sb_text.append("��" + str_keys[i] + "��=��" + dataVO.getStringValue(str_keys[i], "") + "��\r\n"); //
				}
				MessageBox.show(this, sb_text.toString()); //
			}
		} else {
			onClicked(label);
		}
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	protected void paintBorder(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
		IconFactory.getInstance().getPanelQueryItem_BG().draw(g, 0, 0, getWidth(), getHeight());
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}
}
