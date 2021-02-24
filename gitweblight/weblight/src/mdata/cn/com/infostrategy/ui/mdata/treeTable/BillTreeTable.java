package cn.com.infostrategy.ui.mdata.treeTable;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import com.l2fprod.common.swing.renderer.DefaultCellRenderer;

import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.ImageIconFactory;

/**
 * 树表控件。
 */
public class BillTreeTable extends JTable {

	private static final long serialVersionUID = 1L;
	private BillTreeTableCellRenderer tree; //单元格绘制
	private DefaultTreeTableModel treetableModel;
	private Pub_Templet_1VO templetVO;
	private Pub_Templet_1_ItemVO[] templetItemVOs;
	private BillTreeTableSelectionModel selectionModel = new BillTreeTableSelectionModel();//选择模式

	public BillTreeTable(DefaultTreeTableModel _treeTableModel, TableColumnModel _columnModel) {
		super();
		treetableModel = _treeTableModel;
		templetVO = treetableModel.getTempletVO();
		templetItemVOs = templetVO.getItemVos();
		tree = new BillTreeTableCellRenderer(this, _treeTableModel);//
		DefaultTreeTableModelAdapter tableModel = new DefaultTreeTableModelAdapter(_treeTableModel, tree);
		super.setModel(tableModel);//设置数据构造
		setDefaultRenderer(String.class, new DefaultCellRenderer());//
		setDefaultRenderer(StringItemVO.class, new DefaultCellRenderer());//
		tree.setSelectionModel(selectionModel);

		((BasicTreeUI) tree.getUI()).setCollapsedIcon(ImageIconFactory.getCollapsedIcon()); //
		((BasicTreeUI) tree.getUI()).setExpandedIcon(ImageIconFactory.getExpandedIcon()); //

		setSelectionModel(selectionModel.getListSelectionModel()); //设置模式 
		setShowGrid(true);

		setIntercellSpacing(new Dimension(1, 1));
		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		new TableRowResizer(this);
		setRowHeight(30);
	}

	public JTree getJTree() {
		return tree;
	}

	protected boolean isTreeHandleEventType(MouseEvent e) {
		switch (e.getID()) {
		case MouseEvent.MOUSE_CLICKED:
		case MouseEvent.MOUSE_PRESSED:
		case MouseEvent.MOUSE_RELEASED:
			return !e.isPopupTrigger();
		}
		return false;
	}

	protected int getTreeHandleWidth() {
		if (tree.getUI() instanceof BasicTreeUI) {
			BasicTreeUI ui = (BasicTreeUI) tree.getUI();
			return ui.getLeftChildIndent() + ui.getRightChildIndent();
		} else {
			return -1;
		}
	}

	public TreePath getPathForRow(int row) {
		return tree.getPathForRow(row);
	}

	public int getRowHeight(int i) {
		if (i >= 0) {
			TreePath treepath = getPathForRow(i);
			if (treepath != null) {
				BillTreeTableDefaultMutableTreeNode node = (BillTreeTableDefaultMutableTreeNode) getPathForRow(i).getLastPathComponent();
				if (node != null) {
					if (node.getNodeHeight() > 0) {
						return node.getNodeHeight();
					} else {
						super.getRowHeight();
					}
				}
			}
		}
		return super.getRowHeight(i);
	}

	public void setRowHeight(int rowHeight) {
		if (rowHeight > 0) {
			super.setRowHeight(rowHeight);
			if (tree != null && tree.getRowHeight() != rowHeight) {
				tree.setRowHeight(getRowHeight());
			}
		}
	}

	public void setRowHeight(int _row, int rowHeight) {
		if (rowHeight > 0) {
			super.setRowHeight(_row, rowHeight);
			tree.setRowHeight(_row, rowHeight);
		}
	}

	public void setOnlyCellRowHeight(int _row, int rowHeight) {
		if (rowHeight > 0) {
			super.setRowHeight(_row, rowHeight);
		}
	}

	public void expandAll(boolean expand) {
		tree.expandAll(expand);
	}

	class BillTreeTableSelectionModel extends DefaultTreeSelectionModel {
		public ListSelectionModel getListSelectionModel() {
			return listSelectionModel;
		}
	}

	class TableRowResizer extends MouseInputAdapter {
		public Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);

		private int mouseYOffset, resizingRow;
		private Cursor otherCursor = resizeCursor;
		private JTable tmptable;

		public TableRowResizer(JTable table) {
			this.tmptable = table;
			table.addMouseListener(this);
			table.addMouseMotionListener(this);
		}

		private int getResizingRow(Point p) {
			return getResizingRow(p, tmptable.rowAtPoint(p));
		}

		private int getResizingRow(Point p, int row) {
			if (row == -1) {
				return -1;
			}

			int col = tmptable.columnAtPoint(p);
			if (col == -1)
				return -1;
			Rectangle r = tmptable.getCellRect(row, col, true); // 表格中对应行列的范围区域
			r.grow(0, -3); // 将网格的范围减少3个像素，即网线上下3个像素内，光标就会变
			if (r.contains(p)) // 如果对应的行列范围包含鼠标的范围
				return -1;
			int midPoint = r.y + r.height / 2;
			int rowIndex = (p.y < midPoint) ? row - 1 : row;
			return rowIndex;
		}

		public void mousePressed(MouseEvent e) {
			Point p = e.getPoint();
			resizingRow = getResizingRow(p);
			mouseYOffset = p.y - tmptable.getRowHeight(resizingRow);
		}

		private void swapCursor() {
			Cursor tmp = tmptable.getCursor();
			tmptable.setCursor(otherCursor);
			otherCursor = tmp;
		}

		public void mouseMoved(MouseEvent e) {
			int li_row = getResizingRow(e.getPoint());
			if (li_row >= 0 != (tmptable.getCursor() == resizeCursor)) {
				swapCursor();
			}
		}

		public void mouseDragged(MouseEvent e) {
			int mouseY = e.getY();
			if (resizingRow >= 0) {
				int newHeight = mouseY - mouseYOffset;
				if (newHeight > 0) {
					if (e.isControlDown()) {
						int li_rowcount = tmptable.getRowCount();
						for (int i = 0; i < li_rowcount; i++) {
							tmptable.setRowHeight(i, newHeight); // 设置新的行高..
						}
					} else {
						tmptable.setRowHeight(resizingRow, newHeight); // 设置新的行高..
						tmptable.revalidate();
						tmptable.repaint();
					}
				}
			}
		}
	}
}
