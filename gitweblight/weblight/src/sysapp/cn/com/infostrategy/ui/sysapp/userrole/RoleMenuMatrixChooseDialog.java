package cn.com.infostrategy.ui.sysapp.userrole;

import java.awt.BorderLayout;

import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * 交叉配置菜单与角色时的选择对话框!
 * @author Administrator
 *
 */
public class RoleMenuMatrixChooseDialog extends BillDialog implements BillTreeSelectListener {

	private BillTreePanel menuTree = null; //
	private BillListPanel roleList = null; //

	public RoleMenuMatrixChooseDialog(java.awt.Container _parent) {
		super(_parent, "选择末级菜单，查看其角色配置", 1000, 700); //
		initialize(); //初始化页面
	}

	/**
	 * 
	 */
	private void initialize() {
		menuTree = new BillTreePanel(new cn.com.infostrategy.to.sysapp.login.TMO_Pub_Menu()); //
		menuTree.queryDataByCondition(null); //
		roleList = new BillListPanel(new ServerTMODefine("cn.com.infostrategy.bs.sysapp.servertmo.TMO_Pub_Role")); ////
		roleList.setQuickQueryPanelVisiable(false);
		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, menuTree, roleList); //
		menuTree.addBillTreeSelectListener(this);
		this.getContentPane().add(split, BorderLayout.CENTER); //
		this.addConfirmButtonPanel(2); //只显示【取消】按钮【李春娟/2015-12-15】
	}

	/**
	* 
	* @return
	*/
	public String[] getChooseMenuIds() {
		return null;
	}

	public String[] getChooseRoleIds() {
		return null;
	}

	//选择菜单时，查询改菜单分配给了哪些角色【李春娟/2015-12-15】
	public void onBillTreeSelectChanged(BillTreeSelectionEvent event) {
		if (menuTree.getSelectedNode() == null || menuTree.getSelectedNode().isRoot()) {
			roleList.removeAllRows();
		} else if (menuTree.getSelectedVO() != null) {
			String menuid = menuTree.getSelectedVO().getStringValue("id");
			roleList.QueryDataByCondition("id in(select roleid from pub_role_menu where menuid='" + menuid + "')");
		}

	}

}
