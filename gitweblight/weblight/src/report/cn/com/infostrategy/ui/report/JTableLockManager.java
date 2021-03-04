package cn.com.infostrategy.ui.report;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;

import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;

/**
 * ������������һ��ʵ�ֱ���������� �����ַ���һ����BILLLISTPANEL�ķ�ʽ һ�־��������
 */
public class JTableLockManager extends JTableHeader {
	private JTable table;
	private JScrollPane scrollPane;
	private int col = -1;
	private int row = -1;
	private FixedMouseListenter mouseListener;
	private int division = 0; // �����Ŀ��
	private int divisionrow = 0; // �����ĸ߶�
	private FixedColumnsDelegate fixedColumns = null;
	private FixedRowsDelegate fixedRows = null;
	private FixedCornerDelegate fixedCorner = null;
	private int tableheadheight, scrollpanelrowheadwidth = 0;
	private TableHierarchyListener tableHierarchy = null;

	public JTableLockManager(JTable table, JScrollPane scrollPane) {
		super(table.getTableHeader().getColumnModel());
		this.table = table;
		this.scrollPane = scrollPane;
		init();
	}

	public boolean isShow() {
		return this.isShowing();
	}

	private void init() {
		tableheadheight = table.getTableHeader().getBounds().height; // ��ͷ�ĸ߶�
		if (tableheadheight == 0 && scrollPane.getColumnHeader() != null) {
			tableheadheight = scrollPane.getColumnHeader().getBounds().height;
		}
		if (scrollpanelrowheadwidth == 0 && scrollPane.getRowHeader() != null) { // ��ͷ�ĳ���
			scrollpanelrowheadwidth = scrollPane.getRowHeader().getBounds().width;
		}
		mouseListener = new FixedMouseListenter();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setTableHeader(this);
		JViewport jv2 = new JViewport();
		jv2.setOpaque(false);
		jv2.setView(this);
		scrollPane.setColumnHeader(jv2);
		this.addMouseListener(mouseListener);
		table.addMouseListener(mouseListener);
		scrollPane.addComponentListener(new ScrolPaneComponentListener()); // ����û�ã���ִ��
		scrollPane.updateUI();
		// table.setFillsViewportHeight(true); �����������������ȥ����
	}

	public void paint(Graphics g) {
		super.paint(g);
		if (col >= 0) {
			int division = mouseListener.getDivision();
			if (division > 0) { // �����������
				Rectangle r = getVisibleRect();
				BufferedImage image = new BufferedImage(division, r.height, BufferedImage.TYPE_INT_ARGB);
				Graphics g2 = image.getGraphics();
				g2.setClip(0, 0, division, r.height);
				g2.setColor(Color.WHITE);
				g2.fillRect(0, 0, division, r.height);
				super.paint(g2); // ����������еı�ͷ����ͼƬ��
				g.drawImage(image, r.x, r.y, division, r.height, null); // ��ͼƬ������ͷ��
				g2.dispose();
			}
		}
	}

	public void unLock() {
		JLayeredPane pane = table.getRootPane().getLayeredPane();
		if (fixedColumns != null) {
			fixedColumns.setVisible(false);
			pane.remove(fixedColumns);
		}
		if (fixedRows != null) {
			fixedRows.setVisible(false);
			pane.remove(fixedRows);
		}
		if (fixedCorner != null) {
			fixedCorner.setVisible(false);
			pane.remove(fixedCorner);
		}
		table.removeHierarchyListener(tableHierarchy);
		mouseListener.added = false;
		col = -1;
		row = -1;
		division = 0;
		divisionrow = 0;
	}

	public int getFixCol() {
		return col;
	}

	public void setFixRowCol(int fixRow, int fixCol) {
		this.col = fixCol;
		this.row = fixRow;
		this.repaint();
		mouseListener.freeze();
	}

	public FixedColumnsDelegate getFixedColumnsDelegate() {
		if (fixedColumns == null) {
			fixedColumns = new FixedColumnsDelegate();
		}
		return fixedColumns;
	}

	public FixedRowsDelegate getFixedRowsDelegate() {
		if (fixedRows == null) {
			fixedRows = new FixedRowsDelegate();
		}
		return fixedRows;
	}

	public FixedCornerDelegate getFixedCornerDelegate() {
		if (fixedCorner == null) {
			fixedCorner = new FixedCornerDelegate();
		}
		return fixedCorner;
	}

	/**
	 * �������е�JLabel
	 */
	private class FixedColumnsDelegate extends JLabel {
		public void paintComponent(Graphics g) {
			if (isShow()) { // �����ͷ����ʾ��֤�����Ǵ��ŵ�ǰ���� �Ż�
				if (division > 0) { // �����������
					Rectangle r = table.getBounds();
					Rectangle visibleRect = table.getVisibleRect();
					Graphics g4 = null;
					BufferedImage image4 = new BufferedImage(division, r.height, BufferedImage.TYPE_INT_RGB); // ʹ��rgb.argb��֧��͸�����������⡣
					g4 = image4.getGraphics();
					g4.setClip(0, visibleRect.y, division, visibleRect.height);
					g4.fillRect(0, 0, division, visibleRect.height); // �����ӿ�ʼ�������е�ͼ����image4��
					table.paint(g4);
					//g.drawImage(image4, 0, 0, division, r.height, 0, visibleRect.y, division, visibleRect.y + r.height, null);
					g.drawImage(image4, 0, r.y, null);
					// x����ʼ����0 ˵���������ҹ������仯
					g4.dispose();
				}
			}
		}
	}

	/**
	 * �����е�JLabel
	 */
	private class FixedRowsDelegate extends JLabel {
		public void paintComponent(Graphics g) {
			if (isShow()) {
				if (divisionrow > 0) {
					Rectangle r = table.getBounds();
					Rectangle visibleRect = table.getVisibleRect();
					BufferedImage image2 = new BufferedImage(r.width, divisionrow, BufferedImage.TYPE_INT_RGB); //��������buffer.��table���һ��
					Graphics g3 = image2.getGraphics();
					g3.setClip(visibleRect.x, 0, visibleRect.width, divisionrow);
					g3.fillRect(0, 0, visibleRect.width, divisionrow);
					table.paint(g3);
					//g.drawImage(image2, 0, 0, r.width, divisionrow, visibleRect.x, 0, visibleRect.x + r.width, divisionrow, null);
					g.drawImage(image2, r.x, 0, null);
					g3.dispose();
				}
			}
		}
	}

	/**
	 * ��ȫ�����Ľ����JLabel
	 */
	private class FixedCornerDelegate extends JLabel {
		public void paintComponent(Graphics g) {
			if (isShow()) {
				if (divisionrow > 0 && division > 0) {
					BufferedImage image2 = new BufferedImage(division, divisionrow, BufferedImage.TYPE_INT_RGB);
					Graphics g3 = image2.getGraphics();
					g3.setClip(0, 0, division, divisionrow);
					g3.setColor(Color.RED);
					g3.fillRect(0, 0, division, divisionrow);
					table.paint(g3);
					g.drawImage(image2, 0, 0, division, divisionrow, 0, 0, division, divisionrow, null);
					g3.dispose();
				}
			}
		}
	}

	private class FixedMouseListenter extends MouseAdapter {

		private boolean added;

		public FixedMouseListenter() {
		}

		public void mouseReleased(MouseEvent e) {
			doMosuseAction();
		}

		public void mouseMoved(MouseEvent e) {
			doMosuseAction();
		}

		private void doMosuseAction() {
			freeze();
		}

		public void freeze() {
			JLayeredPane pane = table.getRootPane().getLayeredPane();
			setBoundsOnFrozen(); // ����3��lable��λ�ô�С
			if (!added) {
				pane.add(getFixedCornerDelegate(), JLayeredPane.PALETTE_LAYER);
				pane.add(getFixedRowsDelegate(), JLayeredPane.PALETTE_LAYER);
				pane.add(getFixedColumnsDelegate(), JLayeredPane.PALETTE_LAYER);
				added = true;
				tableHierarchy = new TableHierarchyListener();
				table.addHierarchyListener(tableHierarchy);
			}
			getFixedCornerDelegate().setVisible(true);
			getFixedRowsDelegate().setVisible(true);
			getFixedColumnsDelegate().setVisible(true);

		}

		// �������ʾλ��
		public void setBoundsOnFrozen() {
			if (col >= 0 && row < 0) { // ֻ������
				setBoundsOnFrozenColumns();
			} else if (col < 0 && row >= 0) { // ֻ������
				setBoundsOnFrozenRows();
			} else if (col >= 0 && row >= 0) { // ����������������
				setBoundsOnFrozenRowsAndColumns();
			}
		}

		public void setBoundsOnFrozenRowsAndColumns() {
			if (scrollPane.isShowing()) {
				setBoundsOnFrozenColumns();
				setBoundsOnFrozenRows();
				JLayeredPane pane = table.getRootPane().getLayeredPane();
				Point p = scrollPane.getLocationOnScreen();
				SwingUtilities.convertPointFromScreen(p, pane);
				int headerHeight = tableheadheight;
				if (scrollpanelrowheadwidth <= 0)
					scrollpanelrowheadwidth = 0;
				p.x += scrollpanelrowheadwidth;
				getFixedCornerDelegate().setBounds(p.x, p.y + headerHeight, division, divisionrow);
			}
		}

		/**
		 * ֻ������ ��ֻ��Ҫ��һ��ͼƬ
		 */
		public void setBoundsOnFrozenRows() {
			if (scrollPane.isShowing()) {
				divisionrow = table.getCellRect(row, 1, true).y + table.getCellRect(row, 1, true).height;
				JLayeredPane pane = table.getRootPane().getLayeredPane();
				Point p = scrollPane.getLocationOnScreen();
				SwingUtilities.convertPointFromScreen(p, pane);
				int headerHeight = tableheadheight;
				int hScrollWidth = (scrollPane.getHorizontalScrollBar().isVisible()) ? scrollPane.getHorizontalScrollBar().getBounds().width : table.getBounds().width;
				if (scrollpanelrowheadwidth <= 0)
					scrollpanelrowheadwidth = 0;
				p.x += scrollpanelrowheadwidth;
				getFixedRowsDelegate().setBounds(p.x, p.y + headerHeight, hScrollWidth, divisionrow);
			}
		}

		/**
		 * ֻ������ ��ֻ��Ҫ��һ��ͼƬ
		 */
		public void setBoundsOnFrozenColumns() {
			if (scrollPane.isShowing()) {
				division = table.getCellRect(1, col, true).x + table.getCellRect(1, col, true).width;
				int limit = scrollPane.getBounds().width - scrollPane.getVerticalScrollBar().getBounds().width - 2;
				division = Math.min(division, limit);
				JLayeredPane pane = table.getRootPane().getLayeredPane();
				Point p = scrollPane.getLocationOnScreen();
				SwingUtilities.convertPointFromScreen(p, pane);
				Rectangle scrollPaneBounds = scrollPane.getBounds();
				int headerHeight = tableheadheight;
				int hScrollHeight = (scrollPane.getHorizontalScrollBar().isVisible()) ? scrollPane.getHorizontalScrollBar().getBounds().height : 0;
				if (scrollpanelrowheadwidth <= 0)
					scrollpanelrowheadwidth = 0;
				p.x += scrollpanelrowheadwidth;
				getFixedColumnsDelegate().setBounds(p.x, p.y + headerHeight, division, scrollPaneBounds.height - headerHeight - hScrollHeight - 1);
			}
		}

		public int getDivision() {
			return division;
		}
	}

	private class TableHierarchyListener implements HierarchyListener {
		public void hierarchyChanged(HierarchyEvent e) {
			if (!e.getComponent().isShowing()) { // �������ʾֱ�Ӵ�layerpane����
				if (DeskTopPanel.getDeskTopPanel().getRootPane() == null) {//���������ʱ��ע��ϵͳ���δ���ִ��
					return;
				}
				JLayeredPane pane = DeskTopPanel.getDeskTopPanel().getRootPane().getLayeredPane(); // ����ѧ�������dialog�������Ĵ�desktoppanel�͵ò����ˡ�
				if (fixedColumns != null) { // �������ʾ�ʹ�layeredpane�����ߣ�����رմ�ҳ��ʱ�������ͷš�
					pane.remove(fixedColumns);
				}
				if (fixedRows != null) {
					pane.remove(fixedRows);
				}
				if (fixedCorner != null) {
					pane.remove(fixedCorner);
				}
			} else {
				JLayeredPane pane = table.getRootPane().getLayeredPane();
				pane.add(getFixedCornerDelegate(), JLayeredPane.PALETTE_LAYER);
				pane.add(getFixedRowsDelegate(), JLayeredPane.PALETTE_LAYER);
				pane.add(getFixedColumnsDelegate(), JLayeredPane.PALETTE_LAYER);
				mouseListener.freeze();
			}
		}
	}

	private class ScrolPaneComponentListener implements ComponentListener {

		public void componentHidden(ComponentEvent e) {
			displayMessage(e.getComponent().getClass().getName() + " --- Hidden");

		}

		public void componentMoved(ComponentEvent e) {
			displayMessage(e.getComponent().getClass().getName() + " --- Moved");
		}

		public void componentResized(ComponentEvent e) {
			displayMessage(e.getComponent().getClass().getName() + " --- Resized");
			freeze();

		}

		public void componentShown(ComponentEvent e) {
			displayMessage(e.getComponent().getClass().getName() + " --- Shown");
		}

		private void displayMessage(String msg) {
		}

		private void freeze() {
			mouseListener.freeze();
		}
	}

	public JTable getTable() {
		return table;
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
}
