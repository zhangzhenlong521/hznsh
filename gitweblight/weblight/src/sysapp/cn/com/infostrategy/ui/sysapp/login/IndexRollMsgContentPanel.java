package cn.com.infostrategy.ui.sysapp.login;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import cn.com.infostrategy.to.common.CommonDate;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTHrefLabel;
import cn.com.infostrategy.ui.mdata.BillCardDialog;

/**
 * ��������ʵ�����ݿ�!
 * @author xch
 *
 */
public class IndexRollMsgContentPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;

	private HashVO[] hvs = null; //
	private boolean isTimerScroll = false; ///
	private WLTHrefLabel[] labels = null; //��5����������!!��Լ����ռ�!!
	private javax.swing.Timer timer = null; //

	/**
	 * ���췽��
	 */
	public IndexRollMsgContentPanel(HashVO[] _hvs, boolean _isTimeScroll, boolean _isMore) {
		this.hvs = _hvs; //
		this.isTimerScroll = _isTimeScroll; //
		if (_isMore) {
			labels = new WLTHrefLabel[hvs == null ? 0 : hvs.length];
		} else {
			labels = new WLTHrefLabel[8];
		}
		initialize(); //
	}

	/**
	 * ��ʼ��ҳ��!!
	 */
	private void initialize() {
		this.setOpaque(false); //͸��!!!
		this.setLayout(null); //
		Color color1 = LookAndFeel.desktop_Foreground; //new Color(2, 68, 152)
		for (int i = 0; i < labels.length; i++) {
			if (hvs != null && i < hvs.length) {
				String str_createtime = hvs[i].getStringValue("createtime", ""); //
				if (!str_createtime.equals("")) {
					if (str_createtime.length() > 10) {
						str_createtime = str_createtime.substring(0, 10); //
					}
					str_createtime = " [" + str_createtime + "]"; //
				}
				String str_text = "��" + hvs[i].getStringValue("title") + str_createtime; //
				labels[i] = new WLTHrefLabel(str_text); //
				labels[i].setToolTipText(str_text); //
				labels[i].putClientProperty("msgid", hvs[i].getStringValue("id")); //

				//������ҳϵͳ������ϢnewͼƬ��ʾ���� �����/2012-11-05��
				if (!str_createtime.equals("")) {
					int sysMsgDay = new TBUtil().getSysOptionIntegerValue("ϵͳ������Ϣ��ʾ����", 2);
					CommonDate cd = new CommonDate(new Date());
					if (cd.getDaysAfter(new CommonDate(hvs[i].getStringValue("createtime", ""), false)) < sysMsgDay) {
						labels[i].setIcon(UIUtil.getImage("new.gif")); //
						labels[i].setHorizontalTextPosition(SwingConstants.LEADING); //
					}
				} else {
					if (i < 2) {
						labels[i].setIcon(UIUtil.getImage("new.gif")); //
						labels[i].setHorizontalTextPosition(SwingConstants.LEADING); //
					}
				}
			} else {
				labels[i] = new WLTHrefLabel("��"); //
			}
			labels[i].setForeground(color1); //������ɫ
			labels[i].addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						onClicked((JLabel) e.getSource());
					}  catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} //
				}
			}); //
			labels[i].addMouseEnterListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (timer != null) {
						timer.stop(); //
					}
				}
			}); //

			labels[i].addMouseExitListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (timer != null) {
						timer.start(); //
					}
				}
			}); //

			labels[i].setBounds(0, i * 25, 360, 25); //
			this.add(labels[i]); //
		}

		this.setPreferredSize(new Dimension(400, labels.length * 25 + 30)); //
		if (isTimerScroll) {
			timer = new Timer(37, this); //ÿ��35������һ��ˢ��!!30ms�ٶ��е����
			timer.start(); //
			ClientEnvironment.clientTimerMap.add(timer); //
		}
	}

	/**
	 * ÿ��һ��ʱ��ˢ��ʱ,�����߼�!������λ��!
	 */
	public void actionPerformed(ActionEvent e) {
		for (JLabel lab : labels) {
			int li_newY = (int) lab.getBounds().getY() - 1; //ȡ��ĳ��Label��Yλ��ȥ��3������!!
			if (li_newY < -35) { //�������ͷ�����,����������Ϊ���·�!!
				li_newY = 25 * labels.length; //
			}
			lab.setBounds(0, li_newY, 360, 25); //
		}
	}

	public void refresh(HashVO[] vo) {
		this.hvs = vo;
		timer.stop();
		this.removeAll();
		labels = new WLTHrefLabel[8];
		initialize(); //
	}


	protected void onClicked(JLabel _label) {  //�ɵķ���
		
		String str_id = (String) _label.getClientProperty("msgid"); //
		if (str_id == null) {
			return;
		}
		BillCardDialog dialog = new BillCardDialog(this, "PUB_SYSBOARD_CODE1"); //
		String tempName = dialog.getCardPanel().getTempletVO().getTempletname();
		dialog.setTitle(tempName);
		dialog.getCardPanel().setVisiable("msgtype", false);
		dialog.getCardPanel().setVisiable("seq", false);
		dialog.getCardPanel().setVisiable("iseffect", false);
		dialog.getCardPanel().queryDataByCondition("id='" + str_id + "'"); //
		dialog.setVisible(true); //
		}

//	protected void onClicked(JLabel label) throws WLTRemoteException, Exception { // 2016-01-28 Ԭ���� ��� ϵͳ������Ϣ�ĵ��ҳ����ʾ77
//		String str_id = (String) label.getClientProperty("msgid");
//		String sql = "select contentmsg from pub_sysboard where id="+str_id;
//		String str_url = UIUtil.getStringValueByDS(null, sql);
//		try {
//			Runtime.getRuntime().exec("explorer.exe \"" + str_url + "\"");
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
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

}
