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
 * �������ò˵����ɫʱ��ѡ��Ի���!
 * @author Administrator
 *
 */
public class RoleMenuMatrixChooseDialog extends BillDialog implements BillTreeSelectListener {

	private BillTreePanel menuTree = null; //
	private BillListPanel roleList = null; //

	public RoleMenuMatrixChooseDialog(java.awt.Container _parent) {
		super(_parent, "ѡ��ĩ���˵����鿴���ɫ����", 1000, 700); //
		initialize(); //��ʼ��ҳ��
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
		this.addConfirmButtonPanel(2); //ֻ��ʾ��ȡ������ť�����/2015-12-15��
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

	//ѡ��˵�ʱ����ѯ�Ĳ˵����������Щ��ɫ�����/2015-12-15��
	public void onBillTreeSelectChanged(BillTreeSelectionEvent event) {
		if (menuTree.getSelectedNode() == null || menuTree.getSelectedNode().isRoot()) {
			roleList.removeAllRows();
		} else if (menuTree.getSelectedVO() != null) {
			String menuid = menuTree.getSelectedVO().getStringValue("id");
			roleList.QueryDataByCondition("id in(select roleid from pub_role_menu where menuid='" + menuid + "')");
		}

	}

}
