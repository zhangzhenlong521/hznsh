package cn.com.infostrategy.ui.mdata;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * 页面切换效果面板
 *
 */
public class AbstractShiftPanel extends JPanel {
	private Image image1 = null;
	private Image image2 = null;
	private int direct = 3; // 0从右向左转，1从左向右转，2从上向下转，3从下向上转
	private double imagewidth, imagecurrwidth = 0;
	private double imageheight, imagecurry = 0;
	private double imageendheight = 0; // 规定图片消失的时候边的最大y坐标
	private double changell = 0; // 变动比例 即长度变化1 y坐标变化多少
	private double speed = 85;
	private Timer timer = null;
	private LayoutManager lm = null;
	public static Vector timerlist = new Vector();
	private AbstractShiftLgIfc ifc = null;

	public AbstractShiftPanel(Image _fromimage1, Image _toimage2) {
		this(null, _fromimage1, _toimage2, (int) (Math.random() * 4));
	}

	/**
	 * 
	 * @param _fromimage1
	 * @param _toimage2
	 * @param _direct 方向0，1，2，3
	 */
	public AbstractShiftPanel(AbstractShiftLgIfc c, Image _fromimage1, Image _toimage2, int _direct) {
		this.image1 = _fromimage1;
		this.image2 = _toimage2;
		this.direct = _direct;
		this.ifc = c;
		if (image1 != null && image2 != null) {
			imagewidth = image1.getWidth(null);
			imageheight = image1.getHeight(null);

			imageendheight = imageheight / 3.0d;
			changell = imageendheight / imagewidth;
			imagecurrwidth = imagewidth;
			speed = imagewidth / 10;
			if (direct == 1) {
				Image temp = null;
				temp = image2;
				image2 = image1;
				image1 = temp;
				imagecurrwidth = 0;
				imagecurry = imageendheight;
			}

			if (direct > 1) {
				imageendheight = imagewidth / 3.0d;
				changell = imageendheight / imageheight;
				imagecurrwidth = imageheight;
				if (direct == 3) {
					Image temp = null;
					temp = image2;
					image2 = image1;
					image1 = temp;
					imagecurrwidth = 0;
					imagecurry = imageendheight;
				}
				speed = imageheight / 10;
			}
			timer = new Timer(20, new TimerListener());
			for (int i = 0; i < timerlist.size(); i++) {
				((Timer) timerlist.get(i)).stop();
			}
			timer.start();
			timerlist.add(timer);
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g = (Graphics2D) g;
		if (image1 != null) {
			Color old = g.getColor();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, image1.getWidth(null), image1.getHeight(null));
			g.setColor(old);
			paintAAA(g, direct);
		}
	}

	public void paintAAA(Graphics g, int _direct) {
		if (direct == 0 || direct == 1) {
			paintLeftRight(g, direct);
		} else if (direct == 2 || direct == 3) {
			paintUpDown(g, direct);
		}
	}

	public void paintUpDown(Graphics g, int _direct) {
		int imagecurrwidth_ = (int)Math.rint(imagecurrwidth);
		for (int i = 0; i < imageheight; i++) {
			if (i < imagecurrwidth_) {
				g.drawImage(image1, getDY1(i, imagecurry, imagecurrwidth_), i, getDY2(i + 1, imagecurry, imagecurrwidth_, imagewidth), i + 1, 0, getX(i, imageheight, imagecurrwidth_), (int) Math.rint(imagewidth), getX(i + 1, imageheight, imagecurrwidth_), null);
			} else {
				g.drawImage(image2, getSY1(i - imagecurrwidth_, imageendheight - imagecurry, imageheight - imagecurrwidth_), i, getSY2(i + 1 - imagecurrwidth_, imageendheight - imagecurry, imageheight - imagecurrwidth_, imagewidth), i + 1, 0, getX(i
						- imagecurrwidth_, imageheight, imageheight - imagecurrwidth_), (int) Math.rint(imagewidth), getX(i + 1 - imagecurrwidth_, imageheight, imageheight - imagecurrwidth_), null);
			}
		}
		if (_direct == 2) {
			if (imagecurrwidth > 0) {
				imagecurrwidth = imagecurrwidth - speed;
				imagecurry = imagecurry + speed * changell;
				if (imagecurrwidth <= 0) {
					imagecurrwidth = 0;
					imagecurry = imageendheight;
				}
			} else {
				timer.stop();
				image1 = null;
				image2 = null;
				if (ifc != null) {
					ifc.afterShift();
				}
			}
		} else if (_direct == 3) {
			if (imagecurrwidth < imageheight) {
				imagecurrwidth = imagecurrwidth + speed;
				imagecurry = imagecurry - speed * changell;
				if (imagecurrwidth >= imageheight) {
					imagecurrwidth = imageheight;
					imagecurry = 0;
				}
			} else {
				timer.stop();
				image1 = null;
				image2 = null;
				if (ifc != null) {
					ifc.afterShift();
				}
			}
		}
		g.dispose();
		g = null;
		System.gc();
	}

	public void paintLeftRight(Graphics g, int _direct) {
		int imagecurrwidth_ = (int)Math.rint(imagecurrwidth);
		for (int i = 0; i < imagewidth - 1; i++) {
			if (i < imagecurrwidth_) {
				g.drawImage(image1, i, getDY1(i, imagecurry, imagecurrwidth), i + 1, getDY2(i + 1, imagecurry, imagecurrwidth, imageheight), getX(i, imagewidth, imagecurrwidth_), 0, getX(i + 1, imagewidth, imagecurrwidth_), (int) Math.rint(imageheight), null);
			} else {
//				g.drawImage(image2, i, getSY1(i - (int) Math.rint(imagecurrwidth), imageendheight - imagecurry, imagewidth - imagecurrwidth), i + 1, getSY2(i + 1 - (int) Math.rint(imagecurrwidth), imageendheight - imagecurry, imagewidth - imagecurrwidth, imageheight), getX(i
//						- (int) Math.rint(imagecurrwidth), imagewidth, imagewidth - imagecurrwidth), 0, getX(i + 1 - (int) Math.rint(imagecurrwidth), imagewidth, imagewidth - imagecurrwidth), (int) Math.rint(imageheight), null);
				g.drawImage(image2, i, getSY1(i - imagecurrwidth_, imageendheight - imagecurry, imagewidth - imagecurrwidth), i + 1, getSY2(i + 1 - imagecurrwidth_, imageendheight - imagecurry, imagewidth - imagecurrwidth, imageheight), getX(i
						- imagecurrwidth_, imagewidth, imagewidth - imagecurrwidth), 0, getX(i + 1 - imagecurrwidth_, imagewidth, imagewidth - imagecurrwidth), (int) Math.rint(imageheight), null);
			}
		}
		if (_direct == 0) {
			if (imagecurrwidth > 0) {
				imagecurrwidth = imagecurrwidth - speed;
				imagecurry = imagecurry + speed * changell;
				if (imagecurrwidth <= 0) {
					imagecurrwidth = 0;
					imagecurry = imageendheight;
				}
			} else {
				timer.stop();
				image1 = null;
				image2 = null;
				if (ifc != null) {
					ifc.afterShift();
				}
			}
		} else if (_direct == 1) {
			if (imagecurrwidth < imagewidth) {
				imagecurrwidth = imagecurrwidth + speed;
				imagecurry = imagecurry - speed * changell;
				if (imagecurrwidth >= imagewidth) {
					imagecurrwidth = imagewidth;
					imagecurry = 0;
				}
			} else {
				timer.stop();
				image1 = null;
				image2 = null;
				if (ifc != null) {
					ifc.afterShift();
				}
			}
		}
		g.dispose();
		g = null;
		System.gc();
	}

	public int getDY1(int i, double imagecurry, double imagecurrwidth) {
		if (imagecurrwidth <= 0) {
			return 0;
		} else {
			return (int) Math.rint((imagecurry - (double) i * imagecurry / imagecurrwidth));
		}
	}

	public int getDY2(int i, double imagecurry, double imagecurrwidth, double imageheight) {
		return (int) Math.rint(imageheight) - getDY1(i, imagecurry, imagecurrwidth);
	}

	public int getSY1(int i, double imagecurry, double imagecurrwidth) {
		if (imagecurrwidth <= 0) {
			return 0;
		} else {
			return (int) Math.rint(((double) i) * imagecurry / imagecurrwidth);
		}
	}

	public int getSY2(int i, double imagecurry, double imagecurrwidth, double imageheight) {
		return (int) Math.rint(imageheight) - getSY1(i, imagecurry, imagecurrwidth);
	}

	public int getX(int i, double imagewidth, double imagecurrwidth) {
		if (imagecurrwidth <= 0) {
			return (int) Math.rint(imagewidth);
		} else {
			return (int) Math.rint(((double) i) / imagecurrwidth * imagewidth);
		}
	}

	class TimerListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			repaint();
		}
	}

	public Timer getTimer() {
		return timer;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}
}
