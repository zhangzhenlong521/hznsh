package cn.com.pushworld.salary.ui.target;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 选择考核岗位， 可按机构选， 也可以按高管/中层选择 返回岗位ID及岗位名称， 多个！
 * 
 * @author Gwang 2013-7-16 下午05:56:21
 */
public class ChooseUserRefDialog extends AbstractRefDialog implements BillTreeSelectListener, ActionListener {

	private BillTreePanel billTree_corp = null;
	private BillListPanel billList_user = null;
	private BillListPanel billList_user_shopcar = null;
	private WLTButton btn_addShopCarUser, btn_addAllShopCarUser, btn_delShopCarUser, btn_delAllShopCarUser;
	private WLTButton btn_showUser1, btn_showUser2, btn_showUser3; // 按系统参数显示[人员选择参照_岗位归类]
	private WLTButton btn_confirm, btn_cancel;
	private RefItemVO returnRefItemVO = null;
	private String depttempletCode = "PUB_CORP_DEPT_CODE1";
	private String usertemplet = "PUB_USER_POST_DEFAULT";
	private String returnType = null; // 返回人员， 返回岗位
	private String selectedID; // 已选择的ID, 弹出窗口传入
	private String[] stationKinds = null;
	private String isSingle = "N";

	public ChooseUserRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		if (refItemVO != null) {
			this.selectedID = refItemVO.getId();
		}
	}

	public ChooseUserRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String returnType) {
		super(_parent, _title, refItemVO, panel);
		this.returnType = returnType;
		if (refItemVO != null) {
			this.selectedID = refItemVO.getId();
		}
	}

	/**
	 * 单选
	 */
	public ChooseUserRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel, String returnType, String isSingle) {
		super(_parent, _title, refItemVO, panel);
		this.isSingle = isSingle;
		this.returnType = returnType;
		if (refItemVO != null) {
			this.selectedID = refItemVO.getId();
		}
	}

	public void initialize() {
		try {
			billTree_corp = new BillTreePanel(depttempletCode);
			billTree_corp.queryDataByCondition("1=1");
			billTree_corp.addBillTreeSelectListener(this);

			String stationKind = TBUtil.getTBUtil().getSysOptionStringValue("人员选择参照_岗位归类", "高管;中层;行长");
			stationKinds = stationKind.split(";");

			billList_user = new BillListPanel(usertemplet);
			billList_user.setQuickQueryPanelVisiable(false);
			billList_user.getBillListBtnPanel().setVisible(true);
			btn_showUser1 = new WLTButton(stationKinds[0], "office_015.gif");
			btn_showUser2 = new WLTButton(stationKinds[1], "office_084.gif");
			btn_showUser3 = new WLTButton(stationKinds[2], "office_108.gif");
			btn_showUser1.addActionListener(this);
			btn_showUser2.addActionListener(this);
			btn_showUser3.addActionListener(this);
			billList_user.addBatchBillListButton(new WLTButton[] { btn_showUser1, btn_showUser2, btn_showUser3 });
			billList_user.repaintBillListButton();

			billList_user_shopcar = new BillListPanel(billList_user.getTempletVO().deepClone());
			billList_user_shopcar.setTitleLabelText("已选择的人员");
			billList_user_shopcar.setQuickQueryPanelVisiable(false);
			btn_addShopCarUser = new WLTButton("添加", "add.gif");
			btn_delShopCarUser = new WLTButton("删除", "del.gif");
			btn_addAllShopCarUser = new WLTButton("全部添加", "office_163.gif");
			btn_delAllShopCarUser = new WLTButton("全部删除", "office_165.gif");
			btn_addShopCarUser.setToolTipText("将上方选中的人员添加进来");
			btn_delShopCarUser.setToolTipText("将下方已选择的人员删除掉");
			btn_addShopCarUser.addActionListener(this);
			btn_delShopCarUser.addActionListener(this);
			btn_addAllShopCarUser.addActionListener(this);
			btn_delAllShopCarUser.addActionListener(this);
			billList_user_shopcar.addBatchBillListButton(new WLTButton[] { btn_addShopCarUser, btn_delShopCarUser, btn_addAllShopCarUser, btn_delAllShopCarUser });
			billList_user_shopcar.repaintBillListButton();
			// 设置已选择的数据
			String sqlIn = TBUtil.getTBUtil().getInCondition(this.selectedID);
			String sql = "";
			if (this.returnType != null && this.returnType.equals("返回岗位")) {
				sql = " postid in (" + sqlIn + ")";

			} else {
				sql = " userid in (" + sqlIn + ") and isdefault = 'Y'";
			}
			WLTSplitPane splitPanel = null;
			if (isSingle.equalsIgnoreCase("Y")) {
				billList_user.QueryDataByCondition(sql);
				billList_user.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTree_corp, billList_user);
			} else {
				billList_user_shopcar.QueryDataByCondition(sql);
				WLTSplitPane splitPanel_2 = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, billList_user, billList_user_shopcar);
				splitPanel_2.setDividerLocation(this.getHeight() / 2);
				splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTree_corp, splitPanel_2);
			}
			splitPanel.setDividerLocation(250);
			this.getContentPane().add(splitPanel, BorderLayout.CENTER);
			this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
			this.setTitle("");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this);
		panel.add(btn_confirm);
		panel.add(btn_cancel);
		return panel;
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		BillVO billVO_tree = billTree_corp.getSelectedVO();
		if (billVO_tree != null) {
			billList_user.QueryDataByCondition(" deptid = '" + billVO_tree.getStringValue("id") + "'");
		}
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_confirm) {
			onConfirm();
		} else if (_event.getSource() == btn_cancel) {
			onCancel();
		} else if (_event.getSource() == btn_addShopCarUser) {
			onAddShopCarUser();
		} else if (_event.getSource() == btn_delShopCarUser) {
			onDelShopCarUser();
		} else if (_event.getSource() == btn_addAllShopCarUser) {
			onAddAllShopCarUser();
		} else if (_event.getSource() == btn_delAllShopCarUser) {
			onDelAllShopCarUser();
		} else if (_event.getSource() == btn_showUser1) {
			onShowUser1();
		} else if (_event.getSource() == btn_showUser2) {
			onShowUser2();
		} else if (_event.getSource() == btn_showUser3) {
			onShowUser3();
		}
	}

	/**
	 * 
	 */
	private void onShowUser1() {
		this.doShowCheckUser(stationKinds[0]);
	}

	/**
	 * 
	 */
	private void onShowUser2() {
		this.doShowCheckUser(stationKinds[1]);
	}

	/**
	 * 
	 */
	private void onShowUser3() {
		this.doShowCheckUser(stationKinds[2]);
	}

	private void doShowCheckUser(String type) {
		// String sql = "select * from sal_target_check_user_list where status =
		// '有效' and type = '" + type + "’";
		String sql =
		// "select * from v_pub_user_post_1 " +
		// " where postid in (select id from pub_post where stationkind =
		// '"+type+"')" +
		// " order by linkcode";
		"SELECT a.* " + "FROM v_pub_user_post_1 a " + "LEFT JOIN pub_post b ON a.postid = b.id " + "WHERE a.isdefault = 'Y' AND b.stationkind = '" + type + "' " + "ORDER BY linkcode, usercode";
		billList_user.QueryData(sql);

	}

	private void onConfirm() {
		// 根据参数决定返回人员或岗位
		String idCol = "userid";
		String nameCol = "username";
		if (this.returnType != null && this.returnType.equals("返回岗位")) {
			idCol = "postid";
			nameCol = "postname";
		}
		if (isSingle.equalsIgnoreCase("Y")) {
			BillVO billVO = billList_user.getSelectedBillVO();
			if (billVO == null) {
				MessageBox.show(this, "请选择一个人员!");
				return;
			}
			returnRefItemVO = new RefItemVO(billVO.getStringValue(idCol), "", billVO.getStringValue(nameCol));
		} else {
			BillVO[] selVOS = null;
			selVOS = billList_user_shopcar.getAllBillVOs();
			if (selVOS == null || selVOS.length == 0) {
				MessageBox.show(this, "请选择一个人员!");
				return;
			}

			StringBuilder ids = new StringBuilder(";");
			StringBuilder names = new StringBuilder("");
			for (int i = 0; i < selVOS.length; i++) {
				ids.append(selVOS[i].getStringValue(idCol) + ";");
				names.append(selVOS[i].getStringValue(nameCol) + ";");
			}
			returnRefItemVO = new RefItemVO(ids.toString(), "", names.toString());
		}
		this.setCloseType(BillDialog.CONFIRM);
		this.dispose();
	}

	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL);
		this.dispose();
	}

	private void onAddShopCarUser() {
		BillVO[] billVOs = billList_user.getSelectedBillVOs();
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "请从上方人员列表中选择一个或多个人员进行此操作!");
			return;
		}
		onAddShopCarUserByBillVOs(billVOs);
	}

	/**
	 * 全部添加
	 */
	private void onAddAllShopCarUser() {
		BillVO[] billVOs = billList_user.getAllBillVOs();
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "没有可以添加的人员,无法进行此操作!");
			return;
		}
		onAddShopCarUserByBillVOs(billVOs);
	}

	private void onAddShopCarUserByBillVOs(BillVO[] _billVOs) {
		HashSet hst = new HashSet();
		BillVO[] billVOs_shopCardUser = billList_user_shopcar.getAllBillVOs();
		if (billVOs_shopCardUser != null && billVOs_shopCardUser.length > 0) {
			for (int i = 0; i < billVOs_shopCardUser.length; i++) {
				String str_userid = billVOs_shopCardUser[i].getStringValue("userid");
				if (str_userid != null) {
					hst.add(str_userid);
				}
			}
		}
		StringBuilder sb_reduplicate_user = new StringBuilder();
		for (int i = 0; i < _billVOs.length; i++) {
			String str_userid = _billVOs[i].getStringValue("userid");
			String str_usercode = _billVOs[i].getStringValue("usercode");
			String str_username = _billVOs[i].getStringValue("username");
			if (hst.contains(str_userid)) {
				sb_reduplicate_user.append("【" + str_usercode + "/" + str_username + "】");
			}
		}
		String str_reduplicate_user = sb_reduplicate_user.toString();
		if (!str_reduplicate_user.equals("")) {
			MessageBox.show(this, "用户" + str_reduplicate_user + "已经添加过了,不能重复添加,请重新选择!");
			return;
		}
		billList_user_shopcar.addBillVOs(_billVOs);
	}

	private void onDelShopCarUser() {
		BillVO[] billVOs = billList_user_shopcar.getSelectedBillVOs();
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "请从下方人员列表中选择一个或多个人员进行此操作!");
			return;
		}
		if (!MessageBox.confirm(this, "您确定要删除选中的人员吗?")) {
			return;
		}
		billList_user_shopcar.removeSelectedRows();
	}

	private void onDelAllShopCarUser() {
		int li_count = billList_user_shopcar.getRowCount();
		if (li_count <= 0) {
			MessageBox.show(this, "已选择的人员为空,无法进行此操作!");
			return;
		}
		if (!MessageBox.confirm(this, "您确定要删除所有已选择的人员么?")) {
			return;
		}
		billList_user_shopcar.clearTable();
	}

	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

}
