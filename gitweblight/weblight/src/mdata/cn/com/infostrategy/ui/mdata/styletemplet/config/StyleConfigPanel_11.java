/**************************************************************************
 * $RCSfile: StyleConfigPanel_11.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.config;

import java.awt.BorderLayout;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractTempletRefPars;
import cn.com.infostrategy.ui.sysapp.registmenu.RegistMenuTreePanel;

public class StyleConfigPanel_11 extends AbstractTempletRefPars {

	private static final long serialVersionUID = -6147862007359706108L;

	private RegistMenuTreePanel regMenuPanel = null; //

	private HashVO returnHVO = null; //

	public StyleConfigPanel_11(String _text) {
		this.setLayout(new BorderLayout());
		regMenuPanel = new RegistMenuTreePanel(); //
		regMenuPanel.initialize(); //
		this.add(regMenuPanel); //
	}

	public VectorMap getParameters() {
		VectorMap map = new VectorMap(); //		
		map.put("menuname", returnHVO.getStringValue("menuname") + ";"); //
		map.put("xmlfile", returnHVO.getStringValue("xmlfile")); //
		return map;
	}

	public void stopEdit() {
		JTree tree = regMenuPanel.getJTree(); //
		TreePath selPath = tree.getSelectionPath(); //
		if (selPath == null) {
			throw new WLTAppException("请选择一个末(叶子)结点"); //
		}
		DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) selPath.getLastPathComponent(); //
		if (!selNode.isLeaf()) { //如果不是叶子结点
			throw new WLTAppException("请选择一个末(叶子)结点"); //
		}
		returnHVO = (HashVO) selNode.getUserObject(); //
		String str_command = returnHVO.getStringValue("command"); //
		if (str_command == null || str_command.trim().equals("")) {
			throw new WLTAppException("选中的功能点的路径为空!"); //
		}
	}

	protected String bsInformation() {
		return null;
	}

	protected String uiInformation() {
		return null;
	}

}
