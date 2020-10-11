package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.metal.MetalButtonUI;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.ImageIconFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.report.BillHtmlPanel;

/**
 * ��ҳ����ͼƬ��!�������Ͻǵ�4��ͼƬ�л����л�ȥ�ĵ����!!
 * @author xch
 *
 */
public class IndexRollImagePanel extends JPanel implements ActionListener {

	private HashVO[] hvs = null; //��������!!

	private String[] str_msgid = new String[4]; //
	private String[] str_titles = new String[4]; //�ĸ�����!!
	private Icon[] images = new Icon[4]; //4��ͼƬ!

	private JLabel imageLabel = null; //
	private JLabel textLabel = null; //
	private JButton[] btns = null; //

	private int li_currimgindex = 1; //��ǰͼƬ������,���ڼ�¼ͼƬ�л�ʱ��!!!
	private Timer timer = null; //
	private int li_delaycount = 0; //
	private TBUtil tbUtil = new TBUtil(); //

	/**
	 * ���췽��!!
	 */
	public IndexRollImagePanel() {
		try {
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			this.hvs = service.getSysBoardRollImage(); //ȡ��ͼƬ����!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		initialize(); //
	}

	/**
	 * ����ҳ��!!!
	 */
	private void initialize() {
		if (hvs == null || hvs.length <= 0) {
			for (int i = 0; i < 4; i++) { //
				str_titles[i] = "û�ж�������"; //
				images[i] = ImageIconFactory.getTextIcon("û�ж���ͼƬ!"); //
			}
		} else {
			for (int i = 0; i < 4; i++) { //
				if (i < hvs.length) { //�����
					str_msgid[i] = hvs[i].getStringValue("id"); //
					str_titles[i] = hvs[i].getStringValue("title"); //
					String str_64code = hvs[i].getStringValue("image_64code"); //image_64code
					if (str_64code != null && !str_64code.equals("")) {
						byte[] bytes = tbUtil.convert64CodeToBytes(str_64code); //
						ImageIcon imgIcon = new ImageIcon(bytes, "ͼƬ"); //�ȴ���ͼƬ
						images[i] = new ImageIcon(tbUtil.getImageScale(imgIcon.getImage(), 550, 240), "ͼƬ"); //�����ų�320*150
					} else {
						images[i] = ImageIconFactory.getTextIcon("û�ж����ͼƬ!"); //
					}
				} else {
					str_titles[i] = "û�ж�������"; //
					images[i] = ImageIconFactory.getTextIcon("û�ж���ͼƬ!"); //
				}
			}
		}

		this.setLayout(new BorderLayout()); //
		this.setOpaque(false); //͸��!!
		this.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1)); //
		imageLabel = new JLabel(images[0]); //
		imageLabel.setToolTipText(str_titles[0]); //
		imageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
		imageLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
//				onClicked(); //
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				timer.stop(); //
			}

			@Override
			public void mouseExited(MouseEvent e) {
				timer.start(); //
			}
		}); //

		this.add(imageLabel, BorderLayout.CENTER); //
		this.add(getSouthPanel(), BorderLayout.SOUTH); //
		timer = new Timer(2000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switchImage(); //
			}
		}); //
		timer.start(); //
		ClientEnvironment.clientTimerMap.add(timer); //
	}

	protected void onClicked() {
		try {
			imageLabel.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			String str_id = str_msgid[li_currimgindex - 1]; //
			if (str_id == null) {
				return; //
			}
			BillCardDialog dialog = new BillCardDialog(this, "PUB_SYSBOARD_CODE1"); //
			String tempName = dialog.getCardPanel().getTempletVO().getTempletname();
			dialog.setTitle(tempName);
			dialog.getCardPanel().setVisiable("msgtype", false);
			dialog.getCardPanel().setVisiable("seq", false);
			dialog.getCardPanel().setVisiable("iseffect", false);
			dialog.getCardPanel().queryDataByCondition("id='" + str_id + "'"); //
			dialog.setVisible(true); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		} finally {
			imageLabel.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
		}
	}

	
	// 2016-01-28  ͼƬ��ϢҲ�����Ƶ��޸�
//	protected void onClicked() {
//		String str_id = str_msgid[li_currimgindex - 1]; //
//		if (str_id == null) {
//			return; //
//		}
//		BillHtmlPanel billHTMLPanel = new BillHtmlPanel();
//		HashMap hashMap = new HashMap();
//		hashMap.put("id", str_id);
//		hashMap.put("table_main", "PUB_SYSBOARD");
//		hashMap.put("url", System.getProperty("CALLURL"));
//		billHTMLPanel.loadhtml("cn.com.infostrategy.bs.sysapp.login.NewsPrimaryKeyFileShow", hashMap);
//		BillDialog dialog = new BillDialog(this, "ϵͳ��Ϣ", 1000, Toolkit.getDefaultToolkit().getScreenSize().height - 30);
//		dialog.setLayout(new BorderLayout());
//		dialog.add(billHTMLPanel);
//		dialog.setVisible(true);
//	}
	
	private ButtonUI metalButtonUI  = new MetalButtonUI();
	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new BorderLayout()); //
		panel.setOpaque(false); //͸��
		textLabel = new JLabel(str_titles[0]); //
		textLabel.setFont(new Font("����", Font.BOLD, 12)); //
		textLabel.setForeground(new Color(2, 68, 152)); //
		panel.add(textLabel, BorderLayout.CENTER); //

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 0)); //
		btns = new JButton[4]; //
		for (int i = 0; i < 4; i++) {
			btns[i] = new JButton("" + (i + 1)); // 
			btns[i].setUI(metalButtonUI);
			btns[i].setBorder(BorderFactory.createEmptyBorder()); //
			btns[i].setMargin(new Insets(0, 0, 0, 0)); //
			if (i == 0) {
				btns[i].setBackground(Color.RED); //
			} else {
				btns[i].setBackground(Color.BLACK); //
			}
			btns[i].setToolTipText(str_titles[i]); //btns
			btns[i].setOpaque(true);
			btns[i].setFocusable(false); //
			btns[i].setCursor(new Cursor(Cursor.HAND_CURSOR)); //
			btns[i].setForeground(Color.WHITE); //
			btns[i].setPreferredSize(new Dimension(16, 16)); //
			btns[i].addActionListener(this); //
			btnPanel.add(btns[i]); //
		}
		panel.add(btnPanel, BorderLayout.EAST); //
		return panel; //
	}

	public void actionPerformed(ActionEvent e) {
		li_delaycount = 3; //ָ����Timer����5�ο�ת,�������ܿ���!!!!
		resetAllBtnBackGround(); //
		JButton btn = (JButton) e.getSource(); //
		btn.setBackground(Color.RED); //
		btn.setForeground(Color.WHITE); //
		String str_text = btn.getText(); //
		int li_index = Integer.parseInt(str_text); //
		imageLabel.setIcon(images[li_index - 1]); //
		imageLabel.setToolTipText(str_titles[li_index - 1]); //
		textLabel.setText(str_titles[li_index - 1]); //
		li_currimgindex = li_index; //
	}

	/**
	 * �л�ͼƬ
	 */
	protected void switchImage() {
		//System.out.println("Timer������һȦ.........[" + this.hashCode() + "]"); //
		if (li_delaycount > 0) {
			li_delaycount--;
			return;
		}
		li_currimgindex = li_currimgindex + 1; //
		if (li_currimgindex > 4) {
			li_currimgindex = 1; //
		}

		resetAllBtnBackGround(); //
		btns[li_currimgindex - 1].setBackground(Color.RED); //
		btns[li_currimgindex - 1].setForeground(Color.WHITE); //
		imageLabel.setIcon(images[li_currimgindex - 1]); //
		imageLabel.setToolTipText(str_titles[li_currimgindex - 1]); //
		textLabel.setText(str_titles[li_currimgindex - 1]); //
	}

	private void resetAllBtnBackGround() {
		for (int i = 0; i < 4; i++) {
			btns[i].setBackground(Color.BLACK); //
			btns[i].setForeground(Color.WHITE); //
		}
	}

}
