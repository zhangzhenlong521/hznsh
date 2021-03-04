package cn.com.infostrategy.ui.sysapp.login2;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.AbstractBorder;

import cn.com.infostrategy.ui.common.LookAndFeel;

/**
 * 系统消息、我的工作区、待办...按钮组。
 * @author hm
 *
 */
public class I_IndexButtonGroupPanel extends JPanel implements ActionListener {
	private List list = new ArrayList();
	private boolean running = false;
	private MouseAdapter adapter;
	private Timer timer;
	private boolean focus = false;

	public boolean isFocus() {
		return focus;
	}

	public void setFocus(boolean focus) {
		this.focus = focus;
	}

	public I_IndexButtonGroupPanel() {
		this.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
		this.setBackground(new Color(65, 65, 65, 180));
		adapter = new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				super.mouseEntered(e);
				if (!focus) {
					focus = true;
				}
				if (timer != null && !timer.isRunning()) {
					timer.start();
				}
			}

			public void mouseExited(MouseEvent e) {
				super.mouseExited(e);
				e.getComponent();
				if (timer != null && !timer.isRunning()) {
					//					timer.start();
				}
			}
		};
		this.setBorder(new AbstractBorder() {
			public Insets getBorderInsets(Component c) {
				return new Insets(10, 0, 5, 0);
			}

			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
				Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(LookAndFeel.compBorderLineColor);
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.drawRoundRect(1, 1, width - 2, height - 2, 10, 10);
			}
		});
		//		this.addMouseListener(adapter);
		timer = new Timer(10, this);
		//		this.setPreferredSize(new Dimension(this.getWidth(), 20));
		//		setUI(new WLTPanelUI());
	}

	//t当前帧数，b是开始帧数， c变化量 d变化时间长短 ,加速
	private double getX(double t, double b, double c, double d) {
		return c * (t /= d) * Math.pow(t, 4) + b;
	}

	public Component add(Component comp) {
		list.add(comp);
		return super.add(comp);
	}
	
	int index = 0;

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == timer) {
			int height = (int) getX(index, 20, 80, 30);
			if (height >= 80) {
				timer.stop();
				index = 0;
			}
			this.setPreferredSize(new Dimension(this.getWidth(), height));
			this.setSize(this.getWidth(), height);
			this.repaint();
			index++;
		}
	}
}
