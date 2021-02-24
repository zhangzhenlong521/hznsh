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
 * 左右各一个按钮,中间是是个滚动框,当要出现动框时,左右按钮就自动出现了
 * 点击左右按钮时会滚动scrollPanel
 * @author xch
 *
 */
public class WLTScrollPanel extends JPanel {
	private java.awt.Container contentPanel = null; //实际内容

	private JScrollPane scrollPanel = null; //
	private JButton btn_moveright, btn_moveleft; //

	public WLTScrollPanel(java.awt.Container _content) {
		super();
		this.contentPanel = _content;
		initialize();
	}

	/**
	 * 初始化页面..
	 */
	private void initialize() {
		this.setLayout(new BorderLayout(0, 0)); //
		this.setOpaque(false); //

		scrollPanel = new JScrollPane(contentPanel); //

		//面板透明处理
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
		ImageIcon rightbtn_img = UIUtil.getImage("right_btn.gif"); //中铁提供的一个图片!
		if (rightbtn_img != null) {
			btn_moveright = new WLTButton("", rightbtn_img); //如果有该图片则使用图片!
		} else {
			btn_moveright = new WLTButton(">>"); //>>
			btn_moveright.setPreferredSize(new Dimension(17, 16)); //
		}
		btn_moveright.setOpaque(false); //
		btn_moveright.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		btn_moveright.setForeground(Color.BLUE);
		btn_moveright.setBorder(BorderFactory.createEmptyBorder());

		btn_moveleft = null; //
		ImageIcon leftbtn_img = UIUtil.getImage("left_btn.gif"); //中铁提供的一个图片!
		if (leftbtn_img != null) {
			btn_moveleft = new WLTButton("", leftbtn_img); //如果有该图片则使用图片!
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
		btn_moveleft.setVisible(false); //默认左右按钮不显示。 郝明2013-5-30,否则感觉面板哆嗦。
		btn_moveright.setVisible(false);
		this.add(btn_moveleft, BorderLayout.WEST); //向左的按钮
		this.add(scrollPanel, BorderLayout.CENTER); //中间放置按钮的滚动框
		this.add(btn_moveright, BorderLayout.EAST); //向右的按钮
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
	 * 重写此方法，把bar的所有事件都干掉！否则如果设置WLTScrollPanel.setVisible为false的话,cpu就会疯转。不停的调用componentHidden,componentShown。hm[2012-10-22].
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
