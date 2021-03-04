package cn.com.infostrategy.ui.mdata.treeTable;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;

/**
 * 列表树形机构数据构造器。
 * @author haoming
 * create by 2015-7-3
 */
public class DefaultTreeTableModel extends DefaultTreeModel {
	private static final int CHANGED = 0;
	private static final int INSERTED = 1;
	private static final int REMOVED = 2;
	private static final int STRUCTURE_CHANGED = 3;
	private Pub_Templet_1VO templetVO;

	public Pub_Templet_1VO getTempletVO() {
		return templetVO;
	}

	private Pub_Templet_1_ItemVO[] templetItemVOs;
	private TBUtil tBUtil;

	protected static String[] columnNames;
	protected static String[] columnKeys;

	protected static Class<?>[] columnTypes;

	public DefaultTreeTableModel(TreeNode root, Pub_Templet_1VO _tempvo) {
		super(root);
		templetVO = _tempvo;
		templetItemVOs = templetVO.getItemVos();
		initialize();
	}

	private void initialize() {
		columnNames = templetVO.getItemNames();
		List visibleItem = new ArrayList();
		List itemType = new ArrayList();
		List itemkey = new ArrayList();
		itemType.add(DefaultTreeTableModel.class);
		visibleItem.add("结构");
		itemkey.add("$treetableshowitem");

		for (int i = 0; i < templetItemVOs.length; i++) {
			//不能是主键,列表显示
			if (templetItemVOs[i].isListisshowable() && !templetItemVOs[i].getItemkey().equals(templetVO.getPkname())) {
				itemType.add(String.class);
				visibleItem.add(templetItemVOs[i].getItemname());
				itemkey.add(templetItemVOs[i].getItemkey());
			}
		}
		columnTypes = (Class[]) itemType.toArray(new Class[0]);
		columnNames = (String[]) visibleItem.toArray(new String[0]);
		columnKeys = (String[]) itemkey.toArray(new String[0]);
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public String getColumnName(int column) {
		return columnNames[column];
	}

	public Class<?> getColumnClass(int column) {
		return columnTypes[column];
	}

	public String getColumnKey(int column) {
		return columnKeys[column];
	}

	public Object getValueAt(Object node, int column) {
		Object ob = ((DefaultMutableTreeNode) node).getUserObject();
		if (ob instanceof HashVO) {
			HashVO bvo = (HashVO) ob;
			return bvo.getStringValue(columnKeys[column]);
		} else if (ob instanceof String) {
			return String.valueOf(ob);
		} else if (ob instanceof BillVO) {
			return ((BillVO) ob).getObject(columnKeys[column]);
		}
		return "null";
	}

	public BillVO getValueAt(Object _node) {
		Object ob = ((DefaultMutableTreeNode) _node).getUserObject();
		if (ob instanceof BillVO) {
			return (BillVO) ob;
		}
		return null;
	}

	public void setValueAt(Object value, DefaultMutableTreeNode node, int column) {
		BillVO bvo = (BillVO) node.getUserObject();
		bvo.setObject(columnKeys[column], value);
	}

	public int getNodeHeight(BillTreeTableDefaultMutableTreeNode node) {
		return node.getNodeHeight();
	}

	public void setNodeHeight(BillTreeTableDefaultMutableTreeNode node, int _height) {
		node.setNodeHeight(_height);
	}

	private void fireTreeNode(int changeType, Object source, Object[] path, int[] childIndices, Object[] children) {
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = new TreeModelEvent(source, path, childIndices, children);
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {

				switch (changeType) {
				case CHANGED:
					((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
					break;
				case INSERTED:
					((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
					break;
				case REMOVED:
					((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
					break;
				case STRUCTURE_CHANGED:
					((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
					break;
				default:
					break;
				}

			}
		}
	}

	protected void fireTreeNodesChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
		fireTreeNode(CHANGED, source, path, childIndices, children);
	}

	protected void fireTreeNodesInserted(Object source, Object[] path, int[] childIndices, Object[] children) {
		fireTreeNode(INSERTED, source, path, childIndices, children);
	}

	protected void fireTreeNodesRemoved(Object source, Object[] path, int[] childIndices, Object[] children) {
		fireTreeNode(REMOVED, source, path, childIndices, children);
	}

	protected void fireTreeStructureChanged(Object source, Object[] path, int[] childIndices, Object[] children) {
		fireTreeNode(STRUCTURE_CHANGED, source, path, childIndices, children);
	}

	/**
	 * 得到数据源名称
	 * @return
	 */
	public String getDataSourceName() {
		if (templetVO.getDatasourcename() == null || templetVO.getDatasourcename().trim().equals("null") || templetVO.getDatasourcename().trim().equals("")) {
			return ClientEnvironment.getInstance().getDefaultDataSourceName(); // 默认数据源
		} else {
			return getTBUtil().convertDataSourceName(ClientEnvironment.getInstance().getCurrSessionVO(), templetVO.getDatasourcename()); // 算出数据源!!
		}
	}

	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil; //
	}
}
