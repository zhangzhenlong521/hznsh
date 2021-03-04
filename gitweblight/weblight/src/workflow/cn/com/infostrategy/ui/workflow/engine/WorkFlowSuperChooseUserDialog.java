package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.Box;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/**
 * @author sfj
 * 首先要有一个数据权限来过滤机构然后再根据机构和角色来过滤出人员
 * 开始是想用扩展机构类型的由于邮储的角色已经区分了业务和风险所以不用了
 * 邮储希望总行看所有
 * 一分看本一分和总行
 * 二分好本二分和一分部门和总行
 * 。。。
 * 即看上下级不看兄弟
 * 这个由数据权限来定义再合适不过
 * 感觉就是按角色来选择
 */
public class WorkFlowSuperChooseUserDialog extends BillDialog implements ActionListener {
	private WLTButton btn_confirm, btn_cancel, btn_search;
	private BillListPanel userList = null;
	private BillTreePanel corpTypeTree, roleTree, appendCorpTypeTree = null;
	private String corpsql = null;
	private BillVO[] vos = null;
	public WorkFlowSuperChooseUserDialog(Container _parent) {
		super(_parent, "高级查找", 900, 600);
		this.initialize();
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		this.add(getMainPanel(), BorderLayout.CENTER);
		this.add(getSourthPanel(), BorderLayout.SOUTH);
	}

	/**
	 * 主面板
	 * @return
	 */
	private JPanel getMainPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new BorderLayout());
		try {
			Box mainbox = Box.createHorizontalBox();
			userList = new BillListPanel(new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_user_1.xml"));
			userList.setRowNumberChecked(true);
			corpTypeTree = new BillTreePanel(new DefaultTMO("机构类型", new String[][]{{"机构类型", "100"}}));
			corpTypeTree.reSetTreeChecked(true);
			corpTypeTree.getTempletVO().setTreeviewfield("机构类型");
			corpTypeTree.queryData(" select name 机构类型 from pub_comboboxdict where type in ('机构分类','机构类型') and id not like '$%' order by seq ");
			corpTypeTree.getBillTreeBtnPanel().setVisible(false);
			corpTypeTree.getQuickLocatePanel().setVisible(false);
			roleTree = new BillTreePanel(new DefaultTMO("角色", new String[][]{{"角色", "100"}}));
			roleTree.reSetTreeChecked(true);
			roleTree.getTempletVO().setTreeviewfield("角色");
			roleTree.queryData(" select name 角色 from pub_role order by name desc ");
			roleTree.getBillTreeBtnPanel().setVisible(false);
			roleTree.getQuickLocatePanel().setVisible(false);
			btn_search = WLTButton.createButtonByType(WLTButton.COMM, "查找");
			btn_search.addActionListener(this);
			WLTSplitPane wlsp = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, corpTypeTree, roleTree);
			wlsp.setDividerLocation(200);
			Box btn = Box.createHorizontalBox();
			btn.add(btn_search);
			btn.add(userList);
			boolean isshowcorptype = TBUtil.getTBUtil().getSysOptionBooleanValue("工作流高级查找是否显示机构类型条件", false);
			if (isshowcorptype) {
				WLTSplitPane wlsp2 = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, wlsp, btn);
				panel.add(wlsp2, BorderLayout.CENTER);
			} else {
				WLTSplitPane wlsp2 = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, roleTree, btn);
				panel.add(wlsp2, BorderLayout.CENTER);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return panel;
	}
 
	private JPanel getSourthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_cancel.addActionListener(this);
		btn_confirm.addActionListener(this);
		panel.add(btn_confirm);
		panel.add(btn_cancel);
		return panel;
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_confirm) {
			onConfirm();
		} else if (_event.getSource() == btn_cancel) {
			onCancel();
		} else if (_event.getSource() == btn_search) {
			onSearch();
		}
	}
	
	public void onSearch() {
		BillVO[] corpTypeVos= corpTypeTree.getCheckedVOs();
//		if (corpTypeVos == null || corpTypeVos.length < 1 || (corpTypeVos.length == 1 && corpTypeVos[0] == null)) {
//			MessageBox.show(btn_search, "请选择机构类型进行过滤!");
//			return;
//		}
		BillVO[] roleVos= roleTree.getCheckedVOs();
		if (roleVos == null || roleVos.length < 1 || (roleVos.length == 1 && roleVos[0] == null)) {
			MessageBox.show(btn_search, "请选择角色进行过滤!");
			return;
		}
		
		List corptype = new ArrayList();
		List role = new ArrayList();
		if (corpTypeVos != null) {
			for (int i = 0 ; i < corpTypeVos.length; i ++ ) {
				if (corpTypeVos[i] != null) {
					corptype.add(corpTypeVos[i].toString());
				}
			}
		}
		
		if (roleVos != null) {
			for (int i = 0 ; i < roleVos.length; i ++ ) {
				if (roleVos[i] != null) {
					role.add(roleVos[i].toString());
				}
			}
		}
		
		try {
			HashMap parMap = new HashMap();
			parMap.put("corptype", corptype);
			parMap.put("role", role);
			HashMap returnMap = UIUtil.commMethod("cn.com.infostrategy.bs.workflow.WorkFlowBSUtil", "getUserCorpAndUsersBycorpTypeAndRole", parMap);
			HashVO[] hvs = (HashVO[]) returnMap.get("users");
			putUserDataByHashVO(hvs);
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}
	
	private void putUserDataByHashVO(HashVO[] _hvs) {
		userList.clearTable(); //先清空右边表格!!
		if (_hvs != null && _hvs.length > 0) {
			for (int i = 0; i < _hvs.length; i++) {
				int _newRow = userList.newRow(false); //
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("userid")), _newRow, "userid"); //
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("usercode")), _newRow, "usercode"); //
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("username")), _newRow, "username"); //
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("userroleid")), _newRow, "userroleid"); //
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("userrolename")), _newRow, "userrolename"); //
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("userdept")), _newRow, "userdept"); //机构id
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("userdeptname")), _newRow, "userdeptname"); //机构名称
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("accruserid")), _newRow, "accruserid"); //授权人id
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("accrusercode")), _newRow, "accrusercode"); //授权人编码
				userList.setValueAt(new StringItemVO(_hvs[i].getStringValue("accrusername")), _newRow, "accrusername"); //授权人名称!!

			}
			userList.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
		}
	}
	
	public void onConfirm() {
		BillVO[] vos = userList.getCheckedBillVOs();
		if (vos == null || vos.length < 1) {
			MessageBox.show(userList, "请勾选需要选择的人员!");
			return;
		}
		this.vos = vos;
		closeType = 1;
		this.dispose();
	}

	public void onCancel() {
		closeType = 2;
		this.dispose();
	}

	public int getCloseType() {
		return closeType;
	}

	public BillVO[] getVos() {
		return vos;
	}
	
}
