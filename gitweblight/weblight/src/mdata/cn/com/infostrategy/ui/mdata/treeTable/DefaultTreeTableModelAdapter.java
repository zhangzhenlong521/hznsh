package cn.com.infostrategy.ui.mdata.treeTable;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import cn.com.infostrategy.to.mdata.BillVO;

/**
 * 表格数据构造器
 * @author haoming
 * create by 2015-7-3
 */
public class DefaultTreeTableModelAdapter extends AbstractTableModel {
	private BillTreeTableCellRenderer tree;
	private DefaultTreeTableModel treeTableModel;

	public DefaultTreeTableModelAdapter(DefaultTreeTableModel _treeTableModel, JTree _tree) {
		this.treeTableModel = _treeTableModel;
		tree = (BillTreeTableCellRenderer) _tree;
		tree.addTreeExpansionListener(new TreeExpansionListener() {
			public void treeExpanded(TreeExpansionEvent event) {
				fireTableDataChanged();
				resetAllRowHeight();
			}

			public void treeCollapsed(TreeExpansionEvent event) {
				fireTableDataChanged();
				resetAllRowHeight();
			}
		});
	}

	//很重要，展开节点后，根据节点高度，重新设置表格高度。目前没有发现更好的办法。
	public void resetAllRowHeight() {
		int i = getRowCount();
		long l = System.currentTimeMillis();
		for (int j = 0; j < i; j++) {
			tree.getTreeTable().setOnlyCellRowHeight(j, getRowHeight(j));
		}
		tree.refreshCellRenderer(); //最后刷新树render。
	}

	public String getColumnName(int column) {
		return treeTableModel.getColumnName(column);
	}

	public int getColumnCount() {
		return treeTableModel.getColumnCount();
	}

	public int getRowCount() {
		return tree.getRowCount();
	}

	public Class<?> getColumnClass(int column) {
		return treeTableModel.getColumnClass(column);
	}

	public Object getValueAt(int row, int column) {
		return treeTableModel.getValueAt(nodeForRow(row), column);
	}

	public BillVO getBillVOAt(int row) {
		return treeTableModel.getValueAt(nodeForRow(row));

	}

	public Object[] getValueAtRow(int row) {
		int columnCount = treeTableModel.getColumnCount();
		Object obj[] = new Object[columnCount];
		for (int i = 0; i < columnCount; i++) {
			obj[i] = getValueAt(row, i);
		}
		return obj;
	}

	public int getRowHeight(int _row) {
		BillTreeTableDefaultMutableTreeNode node = (BillTreeTableDefaultMutableTreeNode) nodeForRow(_row);
		return node.getNodeHeight();
	}

	public boolean isCellEditable(int row, int column) {
		return true;
	}

	public void setValueAt(Object value, int row, int column) { //表格更新事件可以再此调用。
		super.setValueAt(value, row, column);
		treeTableModel.setValueAt(value, (DefaultMutableTreeNode) nodeForRow(row), column);
	}

	protected Object nodeForRow(int row) {
		TreePath treePath = tree.getPathForRow(row);
		if (treePath == null) {
			return null;
		}
		return treePath.getLastPathComponent();
	}
}
