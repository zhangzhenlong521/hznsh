package cn.com.infostrategy.ui.mdata.treeTable;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeCellRenderer;

/**
 * ±í¸ñ±à¼­Æ÷
 * @author haoming
 * create by 2015-7-7
 */
public class BillTreeTableCellEditor extends DefaultCellEditor implements TableCellEditor {

	private JTree tree; //
	private JTable table;
	private JPanel renderer;

	private JLabel treeCellLabel;

	public BillTreeTableCellEditor(JTree _tree, JTable table, boolean _ischeck) {
		super(new TreeTableTextField());
		this.tree = _tree;
		this.table = table;
		renderer = new JPanel(new BorderLayout(3, 0));
		renderer.setOpaque(false);
		treeCellLabel = new JLabel();
	}

	//
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
		Component component = super.getTableCellEditorComponent(table, value, isSelected, r, c);
		initEditorOffset(table, r, c, isSelected);
		return component;
	}

	protected void initEditorOffset(JTable table, int row, int column, boolean isSelected) {
		if (tree == null)
			return;
		Object node = tree.getPathForRow(row).getLastPathComponent();
		boolean leaf = tree.getModel().isLeaf(node);
		boolean expanded = tree.isExpanded(row);
		TreeCellRenderer tcr = tree.getCellRenderer();
		Component editorComponent = tcr.getTreeCellRendererComponent(tree, node, isSelected, expanded, leaf, row, false);
		((TreeTableTextField) getComponent()).init(row, column, table, tree, editorComponent);
	}

	//
	public boolean isCellEditable(EventObject e) {

		if (e instanceof MouseEvent) {
			int colunm1 = 0;
			MouseEvent me = (MouseEvent) e;
			int doubleClick = 2;
			MouseEvent newME = new MouseEvent(tree, me.getID(), me.getWhen(), me.getModifiers(), me.getX() - table.getCellRect(0, colunm1, true).x, me.getY(), doubleClick, me.isPopupTrigger());
			if (me instanceof MouseEvent) {
				if (((MouseEvent) me).getClickCount() >= 2) {
					return true;
				} else if (((MouseEvent) e).getClickCount() >= clickCountToStart) {
					return true;
				} else {
					tree.dispatchEvent(newME);
				}
				return false;
			}
		}
		return false;
	}

	public Object getCellEditorValue() {
		return null;
	}

	@Override
	public Component getTreeCellEditorComponent(JTree jtree, Object obj, boolean flag, boolean flag1, boolean flag2, int i) {
		return super.getTreeCellEditorComponent(jtree, obj, flag, flag1, flag2, i);
	}

	static class TreeTableTextField extends JTextField {
		private int iconWidth;

		void init(int row, int column, JTable table, JTree tree, Component editorComponent) {
			this.column = column;
			this.row = row;
			this.table = table;
			this.tree = tree;
			updateIconWidth(editorComponent);
			setComponentOrientation(table.getComponentOrientation());
		}

		/**
		 * @param treeComponent
		 */
		private void updateIconWidth(Component treeComponent) {
			iconWidth = 0;
			if (!(treeComponent instanceof JLabel))
				return;
			Icon icon = ((JLabel) treeComponent).getIcon();
			if (icon != null) {
				iconWidth = icon.getIconWidth() + ((JLabel) treeComponent).getIconTextGap();
			}

		}

		private int column;
		private int row;
		private JTable table;
		private JTree tree;

		/**
		 * {@inheritDoc} <p>
		 * 
		 * Overridden to place the textfield in the node content boundaries, 
		 * leaving the icon to the renderer. <p>
		 * 
		 * PENDING JW: insets?
		 * 
		 */
		@SuppressWarnings("deprecation")
		@Override
		public void reshape(int x, int y, int width, int height) {
			// Allows precise positioning of text field in the tree cell.
			// following three lines didn't work out
			//Border border = this.getBorder(); // get this text field's border
			//Insets insets = border == null ? null : border.getBorderInsets(this);
			//int newOffset = offset - (insets == null ? 0 : insets.left);

			Rectangle cellRect = table.getCellRect(0, column, false);
			Rectangle nodeRect = tree.getRowBounds(row);
			nodeRect.width -= iconWidth;
			if (table.getComponentOrientation().isLeftToRight()) {
				int nodeStart = cellRect.x + nodeRect.x + iconWidth;
				int nodeEnd = cellRect.x + cellRect.width;
				super.reshape(nodeStart, y, nodeEnd - nodeStart, height);
				//                int newOffset = nodeLeftX - getInsets().left;
				//                super.reshape(x + newOffset, y, width - newOffset, height);
			} else {
				int nodeRightX = nodeRect.x + nodeRect.width;
				nodeRect.x = 0; //Math.max(0, nodeRect.x);
				// ignore the parameter
				width = nodeRightX - nodeRect.x;
				super.reshape(cellRect.x + nodeRect.x, y, width, height);
			}
		}

	}
}
