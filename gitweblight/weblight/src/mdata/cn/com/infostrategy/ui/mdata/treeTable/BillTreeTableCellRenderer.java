package cn.com.infostrategy.ui.mdata.treeTable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.plaf.metal.MetalTreeUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import cn.com.infostrategy.to.mdata.BillTreeDefaultMutableTreeNode;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.treecomp.TristateCheckBox;

/**
 * 树表单元格渲染器
 * 
 * @author haoming
 * create by 2015-7-14
 */
public class BillTreeTableCellRenderer extends JTree implements TableCellRenderer {
	protected int visibleRow;

	private BillTreeTable treeTable;
	private Dimension intercellSpacing = new Dimension(1, 1);
	private JPanel treeBackPanel = new JPanel(new BorderLayout()); //很重要。单双行颜色

	public BillTreeTableCellRenderer(BillTreeTable _treeTable, TreeModel model) {
		super(model);
		this.treeTable = _treeTable;
		MyTreeCellRender render = new MyTreeCellRender(true);
		TreeCellEditor editor = new DefaultCellEditor(new TristateCheckBox());
		setCellEditor(editor);
		setCellRenderer(render);
		setRootVisible(false); //  
		setShowsRootHandles(false); //  
		this.putClientProperty("JTree.lineStyle", "None"); //不显示连接线
		setBackground(Color.BLUE);
		setOpaque(false);
	}

	public synchronized void setRowHeight(int _row, int _height) {
		super.setRowHeight(-1);
		TreePath treePath = treeTable.getJTree().getPathForRow(_row);
		if (treePath != null) {
			BillTreeTableDefaultMutableTreeNode node = (BillTreeTableDefaultMutableTreeNode) treePath.getLastPathComponent();
			node.setNodeHeight(_height);
		}
		refreshCellRenderer();
	}

	//性能慢,
	public void refreshCellRenderer() {
		MyTreeCellRender render = new MyTreeCellRender(true);
		setCellRenderer(render); //必须重新设置render才好使。
		treeTable.revalidate();
		treeTable.repaint();
		this.revalidate();
		this.repaint();
	}

	public BillTreeTable getTreeTable() {
		return treeTable;
	}

	public int getRowHeight2() {
		return getRowHeight2(visibleRow);
	}

	protected synchronized int getRowHeight2(int _row) {
		if (treeTable == null) {
			return super.getRowHeight();
		}
		TreePath treePath = this.getPathForRow(_row);
		if (treePath == null) {
			return -1;
		}
		BillTreeTableDefaultMutableTreeNode node = (BillTreeTableDefaultMutableTreeNode) treePath.getLastPathComponent();
		if (node.getNodeHeight() < 0 && treeTable.getJTree() != null) {
			return treeTable.getRowHeight(_row);
		}
		return node.getNodeHeight();
	}

	class TreeUI extends MetalTreeUI {

	}

	public void setBounds(int x, int y, int w, int h) {
		super.setBounds(x, 0, w, treeTable.getHeight());
	}

	public void paint(Graphics g) {
		Rectangle oldClip = g.getClipBounds();
		Rectangle clip;
		clip = oldClip.intersection(new Rectangle(0, 0, getWidth() - intercellSpacing.width, getRowHeight2() - intercellSpacing.height));
		g.setClip(clip);
		g.translate(0, -getTranslateHeight());
		super.paint(g);
		g.translate(0, getTranslateHeight());
		g.setClip(oldClip);
	}

	private HashMap<Integer, Integer> translateCache = new HashMap<Integer, Integer>(); //用来缓存位置，提高性能

	//获取绘制偏移量。当前单元格位置为前面所有单元格高度和
	private int getTranslateHeight() {
		int rowHeight = 0;
		//		int height = getTranslateCache(visibleRow - 1); //获取上一个单元格的偏移量
		//		if (height >= 0) { //如果上一个单元格的偏移量>0
		//			height = height + getRowHeight2(visibleRow - 1); //偏移量+上一个单元格高度
		//			translateCache.put(visibleRow, height); //缓存
		//			return height;
		//		}
		for (int i = 0; i < visibleRow; i++) { //
			rowHeight += getRowHeight2(i);
		}
		//		translateCache.put(visibleRow, rowHeight);
		return rowHeight;
	}

	private int getTranslateCache(int _row) {
		if (translateCache.containsKey(_row)) {
			return translateCache.get(_row);
		}
		return -1;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected)
			setBackground(table.getSelectionBackground());
		else
			setBackground(table.getBackground());
		visibleRow = row;
		treeBackPanel.setOpaque(true);
		if (isSelected) {
			treeBackPanel.setBackground(treeTable.getSelectionBackground());
			treeBackPanel.setForeground(Color.RED); //
		} else {
			if (row % 2 == 0) {
				treeBackPanel.setBackground(LookAndFeel.table_bgcolor_odd); //
			} else {
				treeBackPanel.setBackground(LookAndFeel.tablebgcolor); //
			}
		}

		treeBackPanel.add(this, BorderLayout.CENTER);
		return treeBackPanel;
	}

	public void expandAll(boolean expand) {
		expandAll(this, expand);
	}

	private void expandAll(JTree tree, boolean expand) {
		TreeNode root = (TreeNode) tree.getModel().getRoot();
		expandAll(tree, new TreePath(root), expand);
	}

	private void expandAll(JTree tree, TreePath parent, boolean expand) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}
		if (expand) {
			tree.expandPath(parent);
		} else {
			if (!node.isRoot()) {
				tree.collapsePath(parent);
			}
		}
	}

	class MyTreeCellRender extends DefaultTreeCellRenderer {
		private JPanel renderer;
		private boolean ischeck = false; //是否是勾选的树
		private TristateCheckBox checkbox;

		public MyTreeCellRender(boolean _ischeck) {
			super();
			renderer = new JPanel(new BorderLayout(3, 0));
			renderer.setOpaque(false);
			ischeck = _ischeck;
			if (ischeck) {
				checkbox = new TristateCheckBox();
				checkbox.setEnabled(true);
				checkbox.setOpaque(false);
				checkbox.setPreferredSize(new Dimension(16, 16));
			}
		}

		public Component getTreeCellRendererComponent(JTree jtree, Object obj, boolean flag, boolean flag1, boolean flag2, int i, boolean flag3) {
			if (i == -1) {
				return super.getTreeCellRendererComponent(jtree, obj, flag, flag1, flag2, i, flag3);
			}
			JLabel oldLabel = (JLabel) super.getTreeCellRendererComponent(jtree, obj, flag, flag1, flag2, i, flag3);
			int width = (int) getPreferredSize().getWidth();
			int height = getRowHeight2(i);
			if (selected) {
				oldLabel.setOpaque(true);
				oldLabel.setBackground(treeTable.getSelectionBackground());
				oldLabel.setForeground(Color.RED); //
			} else {
				oldLabel.setOpaque(true);
				if (i % 2 == 0) {
					oldLabel.setBackground(LookAndFeel.table_bgcolor_odd); //
				} else {
					oldLabel.setBackground(LookAndFeel.tablebgcolor); //
				}
			}
			if (ischeck) {
				BillTreeTableDefaultMutableTreeNode node = (BillTreeTableDefaultMutableTreeNode) obj;
				renderer.add(checkbox, BorderLayout.WEST);
				checkbox.setSelected(node.isChecked());
				oldLabel.setIcon(null);
			}
			renderer.add(oldLabel, BorderLayout.CENTER);
			renderer.setPreferredSize(new Dimension(width, height));
			return renderer;
		}
	}
}
