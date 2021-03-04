package cn.com.infostrategy.ui.mdata.treeTable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

public class CheckTreeTableManager extends MouseAdapter implements TreeSelectionListener {

	private CheckTreeSelectionModel selectionModel;
	private BillTreeTable treetable;
	private JTree tree;
	int hotspot = new JCheckBox().getPreferredSize().width;

	public CheckTreeTableManager(BillTreeTable _treeTable) {
		this.treetable = _treeTable;
		this.tree = (JTree) treetable.getCellRenderer(0, 0);
		tree.getSelectionModel();
		selectionModel = new CheckTreeSelectionModel(tree.getModel());
		treetable.addMouseListener(this);
		selectionModel.addTreeSelectionListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent me) {
		TreePath path = tree.getPathForLocation(me.getX(), me.getY());
		if (path == null) {
			return;
		}
		if (me.getX() > tree.getPathBounds(path).x + hotspot) {
			return;
		}
		selectionModel.removeTreeSelectionListener(this);
		BillTreeTableDefaultMutableTreeNode node = (BillTreeTableDefaultMutableTreeNode) path.getLastPathComponent();
		boolean selected = node.isChecked();
		node.setIschecked(!selected);
		try {
			if (selected) {
				selectionModel.removeSelectionPath(path);
			} else {
				selectionModel.addSelectionPath(path);
			}
		} finally {
			selectionModel.addTreeSelectionListener(this);
			treetable.revalidate();
			treetable.repaint();
		}
	}

	public CheckTreeSelectionModel getSelectionModel() {
		return selectionModel;
	}

	public void valueChanged(TreeSelectionEvent e) {
	}
}