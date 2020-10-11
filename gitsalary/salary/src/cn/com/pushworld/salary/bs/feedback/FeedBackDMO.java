package cn.com.pushworld.salary.bs.feedback;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.pushworld.salary.to.SalaryTBUtil;

public class FeedBackDMO extends AbstractDMO {
	private CommDMO dmo = new CommDMO();

	public List getFeedbackDataHashvo(String sql, int rowArea[]) throws Exception {
		HashVOStruct struct = dmo.getHashVoStructByDS(null, sql, false, false, rowArea); //查询主记录内容。
		HashVO hvo[] = struct.getHashVOs();
		String ids_condition = new SalaryTBUtil().getInCondition(hvo, "id");
		if (ids_condition != null) {
			HashVO vos[] = dmo.getHashVoArrayByDS(null, "select t1.* ,t3.name username,t2.sex from sal_feedback t1 left join v_sal_personinfo t2  on t1.createuser = t2.id left join pub_user  t3 on t1.createuser= t3.id where t1.groupid in(" + ids_condition + ") order by t1.createtime desc");
			DefaultMutableTreeNode nodes[] = new DefaultMutableTreeNode[vos.length];
			DefaultMutableTreeNode rootnode = new DefaultMutableTreeNode();
			HashMap<String, DefaultMutableTreeNode> map = new HashMap<String, DefaultMutableTreeNode>();
			for (int i = 0; i < vos.length; i++) {
				nodes[i] = new DefaultMutableTreeNode(vos[i]);
				map.put(vos[i].getStringValue("id"), nodes[i]);
			}
			for (int i = 0; i < nodes.length; i++) {
				String parentid = vos[i].getStringValue("parentid");
				if (map.containsKey(parentid)) {
					DefaultMutableTreeNode p_node = map.get(parentid);
					p_node.add(nodes[i]);
				} else {
					rootnode.add(nodes[i]);
				}
			}

			ArrayList<HashVO[]> ask_answer = new ArrayList<HashVO[]>();

			int askcount = rootnode.getChildCount();
			for (int i = 0; i < askcount; i++) {
				DefaultMutableTreeNode child = (DefaultMutableTreeNode) rootnode.getChildAt(i);
				DefaultMutableTreeNode ask_nodes[] = getOneNodeAllChildrenNodes(child);
				List<HashVO> l = new ArrayList<HashVO>();
				for (int j = 0; j < ask_nodes.length; j++) {
					HashVO vo = (HashVO) ask_nodes[j].getUserObject();
					l.add(vo);
				}
				ask_answer.add(l.toArray(new HashVO[0]));
			}
			return ask_answer;
		}
		return null;
	}

	private DefaultMutableTreeNode[] getOneNodeAllChildrenNodes(TreeNode _node) {
		ArrayList vector = new ArrayList();
		visitAllNodes(vector, _node); //
		DefaultMutableTreeNode[] allNodes = (DefaultMutableTreeNode[]) vector.toArray(new DefaultMutableTreeNode[0]); //遍历所有结点..
		return allNodes; //
	}

	private void visitAllNodes(ArrayList _vector, TreeNode node) {
		_vector.add(node); // 加入该结点
		if (node.getChildCount() >= 0) { //如果有子结点,则遍历每个子结点，递归调用本方法
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // 找到该儿子!!
				visitAllNodes(_vector, childNode); // 继续查找该儿子
			}
		}
	}
}
