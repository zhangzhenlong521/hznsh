package cn.com.infostrategy.ui.sysapp.login;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTHtmlButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.IconFactory;

/**
 * ��ҳ�������ſ�!!!
 * @author xch
 *
 */
public class IndexRollMsgPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JButton btn_more, btn_fresh; //
	private IndexRollMsgContentPanel msgpanel = null;

	public IndexRollMsgPanel() {
		this.setLayout(new BorderLayout()); //
		this.setOpaque(false); //
		this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));
		String name = TBUtil.getTBUtil().getSysOptionStringValue("ϵͳ��Ϣ����", "");
		if(name == null|| name.equals("")){
			this.add(getTitlePanel("ϵͳ��Ϣ", "site.gif"), BorderLayout.NORTH); //
		}else {
			this.add(getTitlePanel(name, "site.gif"), BorderLayout.NORTH); //
		}
		
		msgpanel = new IndexRollMsgContentPanel(getRollMsgHvs(true), true, false);
		this.add(msgpanel, BorderLayout.CENTER); //���������
	}

	private JPanel getTitlePanel(String _title, String _imgName) {
		boolean isRefresh = new TBUtil().getSysOptionBooleanValue("��ҳ�������Ƿ���ˢ�°�ť", true); //������������Ȼ��ϲ��ˢ��
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
		JLabel label_title = new JLabel(" " + _title, UIUtil.getImage(_imgName), JLabel.LEFT); //
		label_title.setFont(new Font("����", Font.BOLD, 12)); //
		panel.add(label_title, BorderLayout.WEST); //

		JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); //
		btnPanel.setBorder(BorderFactory.createEmptyBorder(1, 15, 1, 25));
		btnPanel.setOpaque(false); //
		btn_fresh = new WLTHtmlButton("ˢ��");
		System.out.println("ˢ�°�ť��� ��"+btn_fresh.getWidth());
		btn_fresh.addActionListener(this);
		btn_more = new WLTHtmlButton("����"); //
		btn_more.addActionListener(this); //
		if (isRefresh) {
			btnPanel.add(btn_fresh);
		}
		btnPanel.add(btn_more); //
		panel.add(btnPanel, BorderLayout.EAST); //
		panel.setPreferredSize(new Dimension(-1, 30)); //
		return panel; //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_more) {
			onMore(); //
		} else { //ˢ��
			onRefresh();
		}
	}

	private void onMore() {
		try {
			btn_more.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			IndexRollMsgContentPanel msgPanel = new IndexRollMsgContentPanel(getRollMsgHvs(false), false, true); //
			JScrollPane scroll = new JScrollPane(msgPanel); //
			scroll.setOpaque(false); ////
			scroll.getViewport().setOpaque(false); ////
			JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
			contentPanel.add(scroll); //
			BillDialog dialog = new BillDialog(this, "������Ϣ", 700, 600); //
			dialog.getContentPane().add(contentPanel); //
			dialog.setVisible(true); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		} finally {
			btn_more.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
		}
	}

	private void onRefresh() {
		msgpanel.refresh(getRollMsgHvs(true));
	}

	private HashVO[] getRollMsgHvs(boolean _istrim) {
		try {
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			return service.getSysBoardRollMsg(_istrim); //������Զ�̷���Ҫ�ϲ���һ��!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}
	protected void paintBorder(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
		IconFactory.getInstance().getPanelQueryItem_BG().draw(g, 0, 0, getWidth(), getHeight());
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}
}
