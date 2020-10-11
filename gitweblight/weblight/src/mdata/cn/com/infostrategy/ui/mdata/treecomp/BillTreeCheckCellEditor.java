package cn.com.infostrategy.ui.mdata.treecomp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

import cn.com.infostrategy.to.mdata.BillTreeDefaultMutableTreeNode;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/**
 * ����ѡ������ͱ༭��..
 * @author xch
 *
 */
public class BillTreeCheckCellEditor extends DefaultTreeCellEditor {

	private BillTreePanel billTreePanel = null; //
	private JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0)); //
	private JCheckBox check = new JCheckBox("", false);; //
	private JLabel label = new JLabel(); //

	private JTree tree = null; //
	private BillTreeDefaultMutableTreeNode node = null; //

	public BillTreeCheckCellEditor(JTree tree, DefaultTreeCellRenderer renderer, BillTreePanel _billTreePanel) {
		super(tree, renderer);
		this.billTreePanel = _billTreePanel; //
		check.setPreferredSize(new Dimension(17, 17)); //
		check.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked(e.getModifiers() == 17); //
			}
		});

		panel.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 1));
		check.setBorder(BorderFactory.createEmptyBorder()); //

		label.setOpaque(true); //
		label.setBackground(UIManager.getColor("Tree.selectionBackground"));
		panel.add(check);
		panel.add(label);
	}

	public Component getTreeCellEditorComponent(JTree _tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		check.setOpaque(false); //
		label.setOpaque(false); //

		panel.setOpaque(true);
		panel.setBackground(Color.YELLOW);
		check.setBackground(Color.YELLOW); //
		check.setForeground(Color.YELLOW);
		label.setForeground(Color.red);

		tree = _tree; //
		node = (BillTreeDefaultMutableTreeNode) value; //
		check.setSelected(node.isChecked()); //

		boolean isVirtualCorpNode = false; //�Ƿ��ǻ���������!! 
		if (node != null && (node.getUserObject() instanceof BillTreeNodeVO)) { //�����㲻Ϊ��,����TreeNodeVO
			BillTreeNodeVO nodeVO = (BillTreeNodeVO) node.getUserObject(); //
			if (nodeVO.isVirtualNode()) {
				isVirtualCorpNode = true; //
			}
		}
		if (isVirtualCorpNode) {
			label.setFont(LookAndFeel.font_i); //����!!!
		} else {
			label.setFont(LookAndFeel.font); //����!!!
		}

		label.setText(value == null ? "" : node.getUserObject().toString()); //
		return panel; //
	}

	public boolean isCellEditable(EventObject _event) {
		if (_event instanceof MouseEvent) {
			return ((MouseEvent) _event).getClickCount() >= 1;
		} else {
			return true;
		}
	}

	/**
	 * �����ѡ��...
	 */
	private void onClicked(boolean _isShiftDown) {
		boolean bo_selected = check.isSelected(); //�ȱ���
		node.setChecked(bo_selected); //��ǰ���,����ݹ���������ӽ��!
		boolean islinkedCheck = billTreePanel.isDefaultLinkedCheck(); //Ĭ���Ƿ�����!!
		boolean iscascade = false; //
		if (islinkedCheck) { //�������
			if (!_isShiftDown) { //û�а�shift
				iscascade = true; //
			}
		} else { //���������
			if (_isShiftDown) {
				iscascade = true; //
			}
		}
		if (iscascade) {
			Vector v_nodes = new Vector(); //
			visitAllNodes(v_nodes, node); //���������ӽ��,������..
			for (int i = 0; i < v_nodes.size(); i++) {
				BillTreeDefaultMutableTreeNode child = (BillTreeDefaultMutableTreeNode) v_nodes.get(i); //
				child.setChecked(bo_selected); //
			}
		}

		if (tree != null) {
			tree.repaint(); //
			billTreePanel.onCheckEditChange(node, bo_selected); //
		}
	}

	/**
	 * ȡ��ֵ,���༭����ʱ���ؼ��е�ֵͨ���÷�������..
	 */
	@Override
	public Object getCellEditorValue() {
		node.setChecked(check.isSelected()); //
		return node; //
	}

	private void visitAllNodes(Vector _vector, TreeNode node) {
		_vector.add(node); // ����ý��
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // �ҵ��ö���!!
				visitAllNodes(_vector, childNode); // �������Ҹö���
			}
		}
	}

}
