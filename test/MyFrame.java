import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputListener;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JTabbedPane;

public class MyFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MyFrame frame = new MyFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MyFrame() {
		setUndecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		//给面板添加边框，边框添加 放大缩小功能
		contentPane.setBorder(new Border(Color.black, 1, this));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

//		JScrollPane scrollPane = new JScrollPane();
//		tabbedPane.addTab("New tab", null, scrollPane, null);
//
//		JTree tree = new JTree();
//		scrollPane.setViewportView(tree);
	}
	
	//主要代码
	class Border extends LineBorder implements MouseInputListener {
		private static final long serialVersionUID = 1L;

		private JFrame frame;
		private int delta;

		private Point sp;
		private Point cp;
		private int width;
		private int height;

		private boolean top, bottom, left, right, topLeft, topRight,
				bottomLeft, bottomRight;

		public Border(Color color, int delta, JFrame frame) {
			super(color, delta);
			this.delta = delta;
			this.frame = frame;

			addMouseMotionListener(this);
			addMouseListener(this);
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			Point dp = e.getLocationOnScreen();
			// 拖动时的组件原点
			int ox = dp.x - cp.x;
			int oy = dp.y - cp.y;

			// 静止的 原点
			int x = sp.x - cp.x;
			int y = sp.y - cp.y;

			int h = height;
			int w = width;

			if (top) {
				ox = x;
				h = height + (-dp.y + sp.y);
			} else if (bottom) {
				oy = y;
				ox = x;
				h = height + (dp.y - sp.y);
			} else if (left) {
				oy = y;
				w = width + (-dp.x + sp.x);
			} else if (right) {
				oy = y;
				ox = x;
				w = width + (dp.x - sp.x);
			} else if (topLeft) {
				h = height + (-dp.y + sp.y);
				w = width + (-dp.x + sp.x);
			} else if (topRight) {
				ox = x;
				h = height + (-dp.y + sp.y);
				w = width + (dp.x - sp.x);
			} else if (bottomLeft) {
				oy = y;
				h = height + (-dp.y + sp.y);
				w = width + (dp.x - sp.x);
			} else if (bottomRight) {
				ox = x;
				oy = y;
				h = height + (dp.y - sp.y);
				w = width + (+dp.x - sp.x);
			}
			frame.setLocation(ox, oy);
			frame.setSize(w, h);
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			sp = arg0.getLocationOnScreen();
			cp = arg0.getPoint();
			width = frame.getWidth();
			height = frame.getHeight();

			top = cp.x > delta && cp.x < width - delta && cp.y <= delta;
			bottom = cp.x > delta && cp.x < width - delta
					&& cp.y >= height - delta;
			left = cp.x <= delta && cp.y > delta && cp.y < height - delta;
			right = cp.x >= width - delta && cp.y > delta
					&& cp.y < height - delta;

			topLeft = cp.x <= delta && cp.y <= delta;
			topRight = cp.x >= width - delta && cp.y <= delta;

			bottomLeft = cp.x <= delta && cp.y >= height - delta;
			bottomRight = cp.x >= width - delta && cp.y >= height - delta;

			if (top) {
				frame.setCursor(Cursor
						.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				return;
			} else if (bottom) {
				frame.setCursor(Cursor
						.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			} else if (left) {
				frame.setCursor(Cursor
						.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			} else if (right) {
				frame.setCursor(Cursor
						.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			} else if (topLeft) {
				frame.setCursor(Cursor
						.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
			} else if (topRight) {
				frame.setCursor(Cursor
						.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
			} else if (bottomLeft) {
				frame.setCursor(Cursor
						.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
			} else if (bottomRight) {
				frame.setCursor(Cursor
						.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
			}
		}

		@Override
		public void mouseClicked(MouseEvent arg0) {
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			frame.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		@Override
		public void mousePressed(MouseEvent arg0) {
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}

	}

}
