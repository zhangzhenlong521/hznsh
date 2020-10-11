package cn.com.infostrategy.ui.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * ���Ҹ�һ����ť,�м����Ǹ�������,��Ҫ���ֶ���ʱ,���Ұ�ť���Զ�������
 * ������Ұ�ťʱ�����scrollPanel
 * @author xch
 *
 */
public class WLTScrollPanel extends JPanel {
	private java.awt.Container contentPanel = null; //ʵ������

	private JScrollPane scrollPanel = null; //
	private JButton btn_moveright, btn_moveleft; //

	public WLTScrollPanel(java.awt.Container _content) {
		super();
		this.contentPanel = _content;
		initialize();
	}

	/**
	 * ��ʼ��ҳ��..
	 */
	private void initialize() {
		this.setLayout(new BorderLayout(0, 0)); //
		this.setOpaque(false); //

		scrollPanel = new JScrollPane(contentPanel); //

		//���͸������
		scrollPanel.setOpaque(false);
		scrollPanel.getViewport().setOpaque(false);

		scrollPanel.setBorder(BorderFactory.createEmptyBorder()); //
		scrollPanel.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPanel.getHorizontalScrollBar().setUnitIncrement(20); //

		scrollPanel.getHorizontalScrollBar().addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent e) {
				btn_moveleft.setVisible(false);
				btn_moveright.setVisible(false); //
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentResized(ComponentEvent e) {
			}

			public void componentShown(ComponentEvent e) {
				btn_moveleft.setVisible(true);
				btn_moveright.setVisible(true); //
			}
		});

		btn_moveright = null;
		ImageIcon rightbtn_img = UIUtil.getImage("right_btn.gif"); //�����ṩ��һ��ͼƬ!
		if (rightbtn_img != null) {
			btn_moveright = new WLTButton("", rightbtn_img); //����и�ͼƬ��ʹ��ͼƬ!
		} else {
			btn_moveright = new WLTButton(">>"); //>>
			btn_moveright.setPreferredSize(new Dimension(17, 16)); //
		}
		btn_moveright.setOpaque(false); //
		btn_moveright.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		btn_moveright.setForeground(Color.BLUE);
		btn_moveright.setBorder(BorderFactory.createEmptyBorder());

		btn_moveleft = null; //
		ImageIcon leftbtn_img = UIUtil.getImage("left_btn.gif"); //�����ṩ��һ��ͼƬ!
		if (leftbtn_img != null) {
			btn_moveleft = new WLTButton("", leftbtn_img); //����и�ͼƬ��ʹ��ͼƬ!
		} else {
			btn_moveleft = new WLTButton("<<"); //<<
			btn_moveleft.setPreferredSize(new Dimension(17, 16)); //
		}
		btn_moveleft.setOpaque(false); //
		btn_moveleft.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		btn_moveleft.setForeground(Color.BLUE);
		btn_moveleft.setBorder(BorderFactory.createEmptyBorder());

		btn_moveright.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onMoveScrollPanelRight(); //
			}
		});

		btn_moveleft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onMoveScrollPanelLeft(); //
			}
		});
		btn_moveleft.setVisible(false); //Ĭ�����Ұ�ť����ʾ�� ����2013-5-30,����о������¡�
		btn_moveright.setVisible(false);
		this.add(btn_moveleft, BorderLayout.WEST); //����İ�ť
		this.add(scrollPanel, BorderLayout.CENTER); //�м���ð�ť�Ĺ�����
		this.add(btn_moveright, BorderLayout.EAST); //���ҵİ�ť
	}

	private void onMoveScrollPanelLeft() {
		int li_oldvalue = scrollPanel.getHorizontalScrollBar().getValue(); //
		int li_newvalue = li_oldvalue - 50; //
		scrollPanel.getHorizontalScrollBar().setValue(li_newvalue); //
	}

	private void onMoveScrollPanelRight() {
		int li_oldvalue = scrollPanel.getHorizontalScrollBar().getValue(); //
		int li_newvalue = li_oldvalue + 50; //
		scrollPanel.getHorizontalScrollBar().setValue(li_newvalue); //
	}
	/**
	 * ��д�˷�������bar�������¼����ɵ��������������WLTScrollPanel.setVisibleΪfalse�Ļ�,cpu�ͻ��ת����ͣ�ĵ���componentHidden,componentShown��hm[2012-10-22].
	 */
	public void setVisible(boolean flag) {
		super.setVisible(flag);
		if(!flag && scrollPanel!=null){
			ComponentListener listener[] = scrollPanel.getHorizontalScrollBar().getComponentListeners();
			if(listener!=null){
				for (int i = 0; i < listener.length; i++) {
					scrollPanel.getHorizontalScrollBar().removeComponentListener(listener[i]);
				}
			}
		}
	}
	

}
