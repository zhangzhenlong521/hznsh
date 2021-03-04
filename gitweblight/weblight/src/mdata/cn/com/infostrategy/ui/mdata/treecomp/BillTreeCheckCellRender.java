package cn.com.infostrategy.ui.mdata.treecomp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillTreeDefaultMutableTreeNode;
import cn.com.infostrategy.to.mdata.templetvo.BillTreeNodeVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * ģ�Ϳؼ��Ŀ��Թ�ѡѡ���Render
 * @author xch
 *
 */
public class BillTreeCheckCellRender extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 3831972479955851782L;

	private boolean checked = false; //
	private JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0)); //
	private JCheckBox check = new JCheckBox("", false); //
	private JLabel label = new JLabel(); //

	private TreePath path;
	private DefaultMutableTreeNode[] allNodes = null;
	private String leafimg, parentimg = null;

	/**
	 * ���췽��
	 * @param _path
	 */
	public BillTreeCheckCellRender(boolean _ischecked) {
		this.checked = _ischecked; //
		if (checked) {
			panel.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 1));
			check.setBorder(BorderFactory.createEmptyBorder()); //
			check.setPreferredSize(new Dimension(17, 17)); //
			label.setEnabled(true); //
			label.setOpaque(false); //
			panel.add(check);
			panel.add(label);
		}
	}
	
	public BillTreeCheckCellRender(String _leafimg, String _parentimg) {
		this.leafimg = _leafimg;
		this.parentimg = _parentimg;
	}

	public void setSelectPath(TreePath _path) {
		this.path = null; //
		this.allNodes = null; //

		this.path = _path; //
		if (path != null) {
			allNodes = (DefaultMutableTreeNode[]) Arrays.asList(path.getPath()).toArray(new DefaultMutableTreeNode[0]);
		}
	}

	/**
	 * ȡ�ÿؼ�
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		if (checked) {
			return getCheckedPanel(tree, value, selected, expanded, leaf, row, hasFocus); //��ѡ��!!!
		} else {
			JLabel oldLabel = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus); //
			JLabel superlabel = new JLabel(oldLabel.getText());
			if (leaf) {
				superlabel.setIcon(UIUtil.getImage(leafimg == null ? "blank.gif" : leafimg)); //ĩ�����һ����ɫ����ǩͼ��.
			} else {
				superlabel.setIcon(UIUtil.getImage(parentimg == null ? "office_030.gif" : parentimg)); //��һ��ǩ�ʵ�ͼ��.
			}
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value; //
			if (node.isRoot() && tree.getClientProperty("MyRootIconImg") != null) {
				superlabel.setIcon(UIUtil.getImage((String) tree.getClientProperty("MyRootIconImg")));
			}

			boolean isVirtualCorpNode = false; //�Ƿ��ǻ���������!! 
			if (node != null && (node.getUserObject() instanceof BillTreeNodeVO)) { //�����㲻Ϊ��,����TreeNodeVO
				BillTreeNodeVO nodeVO = (BillTreeNodeVO) node.getUserObject(); //
				if (nodeVO.getIconName() != null && !nodeVO.getIconName().trim().equals("")) { //�������ͼ�����Ʋ�Ϊ��!
					superlabel.setIcon(UIUtil.getImage(nodeVO.getIconName())); //�Զ���ͼ��
				}
				isVirtualCorpNode = nodeVO.isVirtualNode(); //
			} else if (node != null && (node.getUserObject() instanceof HashVO)) {//����xmlע�Ṧ�ܵ㣬Ҳ��ʾͼ��
				HashVO vo = (HashVO) node.getUserObject();
				String icon = vo.getStringValue("icon");
				if (icon != null && !icon.equals("")) {
					superlabel.setIcon(UIUtil.getImage(icon));
				}
			}

			if (isVirtualCorpNode) { //�����������!!
				superlabel.setFont(LookAndFeel.font_i); //��������..
			} else {
				superlabel.setFont(LookAndFeel.font); //��������..
			}

			boolean iffind = false;
			if (allNodes != null) {
				for (int i = 0; i < allNodes.length; i++) {
					if (allNodes[i] == node) {
						iffind = true;
						break;
					}
				}
			}

			if (iffind) { // ����ҵ�!!
				superlabel.setForeground(Color.RED);
			} else {
				//superlabel.setForeground(LookAndFeel.systemLabelFontcolor);
				superlabel.setForeground(LookAndFeel.appLabelNotSelectedFontcolor);
			}
			//
			//superlabel.setText((node == null || node.getUserObject() == null) ? "" : node.getUserObject().toString()); //
			if (selected) {
				superlabel.setOpaque(true);
				superlabel.setBackground(Color.YELLOW);
				superlabel.setForeground(Color.RED); //
			} else {
				superlabel.setOpaque(false);
				//superlabel.setBackground(UIManager.getColor("Tree.textBackground"));
				//superlabel.setForeground(UIManager.getColor("Tree.notSelectionForeground"));
			}
			if (hasFocus) {
				//superlabel.setOpaque(true);
				//superlabel.setBackground(UIManager.getColor("Tree.selectionBackground"));
			}
			return superlabel; //
		}
	}

	/**
	 * ��ѡ�������
	 */
	public Component getCheckedPanel(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		try {
			check.setOpaque(false); //
			label.setOpaque(false); //

			BillTreeDefaultMutableTreeNode node = (BillTreeDefaultMutableTreeNode) value; //
			check.setSelected(node.isChecked());

			boolean iffind = false;
			if (allNodes != null) {
				for (int i = 0; i < allNodes.length; i++) {
					if (allNodes[i] == node) {
						iffind = true;
						break;
					}
				}
			}

			if (iffind) { // ����ҵ�!!
				label.setForeground(Color.red);
			} else {
				label.setForeground(LookAndFeel.systemLabelFontcolor); //
			}

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

			label.setText(node == null ? "" : node.getUserObject().toString()); //
			if (selected) {
				panel.setOpaque(true);
				panel.setBackground(Color.YELLOW);
				label.setForeground(Color.red);
			} else {
				panel.setOpaque(false);
				//label.setBackground(UIManager.getColor("Tree.textBackground"));
			}

			//			if (hasFocus) {
			//				System.out.println("���[" + value + "]");
			//				panel.setOpaque(true);
			//				panel.setBackground(LookAndFeel.appLabelSelectedFontcolor);
			//
			//				label.setOpaque(true);
			//				label.setBackground(LookAndFeel.appLabelSelectedFontcolor);
			//				label.setForeground(Color.red);
			//			}

			return panel; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}
}
