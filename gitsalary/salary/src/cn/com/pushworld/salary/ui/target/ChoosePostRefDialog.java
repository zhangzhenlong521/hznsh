package cn.com.pushworld.salary.ui.target;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryEvent;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryListener;
import cn.com.infostrategy.ui.mdata.BillListCheckedAllEvent;
import cn.com.infostrategy.ui.mdata.BillListCheckedAllListener;
import cn.com.infostrategy.ui.mdata.BillListCheckedEvent;
import cn.com.infostrategy.ui.mdata.BillListCheckedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * 岗位价值评估，选择被评岗位， 可按机构选， 也可以按高管/中层选择
 * 返回多个岗位ID及岗位名称！【李春娟/2013-10-24】
 * @author lcj
 */
public class ChoosePostRefDialog extends AbstractRefDialog implements BillTreeSelectListener, ActionListener, BillListCheckedAllListener, BillListCheckedListener, BillListAfterQueryListener {

	private BillTreePanel billTree_corp = null;
	private BillListPanel billList_post = null;
	private WLTButton btn_addAllShopCarUser, btn_delAllShopCarUser, btn_showAll;
	private WLTButton btn_confirm, btn_cancel;
	private RefItemVO returnRefItemVO = null;
	private String depttemplet = "PUB_CORP_DEPT_CODE1";
	private String posttemplet = "PUB_POST_LCJ_Q01";
	private String selectedID; //已选择的ID, 弹出窗口传入
	private HashMap postMap = new HashMap();

	public ChoosePostRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		if (refItemVO != null) {
			this.selectedID = refItemVO.getId();
		}
	}

	public void initialize() {
		try {
			billTree_corp = new BillTreePanel(depttemplet);
			billTree_corp.setMoveUpDownBtnVisiable(false);
			billTree_corp.queryDataByCondition("1=1");
			billTree_corp.addBillTreeSelectListener(this);

			billList_post = new BillListPanel(posttemplet);
			billList_post.setTitleLabelText("");
			billList_post.setRowNumberChecked(true);

			billList_post.QueryData("select p.* from pub_post p left join pub_corp_dept d on d.id=p.deptid where p.id in(" + TBUtil.getTBUtil().getInCondition(this.selectedID) + ") order by d.linkcode,p.seq,p.code");
			for (int i = 0; i < billList_post.getRowCount(); i++) {
				billList_post.setCheckedRow(i, true);
				postMap.put(billList_post.getRealValueAtModel(i, "id"), billList_post.getRealValueAtModel(i, "deptid"));
			}
			if (postMap.size() > 0) {
				billList_post.setTitleChecked(true);
			}
			billList_post.addBillListAfterQueryListener(this);
			billList_post.addBillListCheckedAllListener(this);
			billList_post.addBillListCheckedListener(this);

			btn_addAllShopCarUser = new WLTButton("添加所有岗位", "office_163.gif");
			btn_delAllShopCarUser = new WLTButton("清空已选", "office_165.gif");
			btn_showAll = new WLTButton("查看已选(" + postMap.size() + ")", "refsearch.gif");
			btn_showAll.setForeground(Color.RED);
			btn_addAllShopCarUser.setToolTipText("添加系统中所有岗位");
			btn_delAllShopCarUser.setToolTipText("清空所有已选的岗位");
			btn_addAllShopCarUser.addActionListener(this);
			btn_delAllShopCarUser.addActionListener(this);
			btn_showAll.addActionListener(this);
			billList_post.addBatchBillListButton(new WLTButton[] { btn_addAllShopCarUser, btn_delAllShopCarUser, btn_showAll });
			billList_post.repaintBillListButton();

			WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTree_corp, billList_post);
			splitPanel.setDividerLocation(230);
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

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_confirm) {
			onConfirm();
		} else if (_event.getSource() == btn_cancel) {
			onCancel();
		} else if (_event.getSource() == btn_addAllShopCarUser) {
			onAddAllShopCarUser();
		} else if (_event.getSource() == btn_delAllShopCarUser) {
			onDelAllShopCarUser();
		} else if (_event.getSource() == btn_showAll) {
			onShowAll();
		}
	}

	private void onConfirm() {
		if (postMap.size() == 0) {
			MessageBox.show(this, "请勾选一个或多个岗位!");
			return;
		}
		String[] return_keys = (String[]) postMap.keySet().toArray(new String[0]);
		try {
			//按顺序取出数据，并加入
			String[][] posts = UIUtil.getStringArrayByDS(null, "select p.id,p.name from pub_post p left join pub_corp_dept d on d.id=p.deptid where p.id in(" + TBUtil.getTBUtil().getInCondition(return_keys) + ") order by d.linkcode,p.seq,p.code");
			StringBuilder ids = new StringBuilder(";");
			StringBuilder names = new StringBuilder("");

			for (int i = 0; i < posts.length; i++) {
				ids.append(posts[i][0] + ";");
				names.append(posts[i][1] + ";");
			}
			returnRefItemVO = new RefItemVO(ids.toString(), "", names.toString());
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.setCloseType(BillDialog.CONFIRM);
		this.dispose();
	}

	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL);
		this.dispose();
	}

	/**
	 * 全部添加
	 */
	private void onAddAllShopCarUser() {
		if (!MessageBox.confirm(this, "您确定要添加系统中所有的岗位吗?")) {
			return;
		}
		try {
			postMap = UIUtil.getHashMapBySQLByDS(null, "select id,deptid from pub_post where deptid in(select id from pub_corp_dept)");
			int rowcount = billList_post.getRowCount();
			for (int i = 0; i < rowcount; i++) {
				billList_post.setCheckedRow(i, true);
			}
			btn_showAll.setText("查看已选(" + postMap.size() + ")");
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void onDelAllShopCarUser() {
		if (postMap.size() <= 0) {
			MessageBox.show(this, "尚未选择岗位,无需进行此操作!");
			return;
		}
		if (!MessageBox.confirm(this, "您确定要清空所有已选择的岗位吗?")) {
			return;
		}
		billList_post.clearTable();
		postMap.clear();
		btn_showAll.setText("查看已选(0)");
	}

	private void onShowAll() {
		if (postMap.size() <= 0) {
			MessageBox.show(this, "尚未选择岗位!");
			return;
		}
		String[] return_keys = (String[]) postMap.keySet().toArray(new String[0]);
		billList_post.QueryData("select p.* from pub_post p left join pub_corp_dept d on d.id=p.deptid where p.id in(" + TBUtil.getTBUtil().getInCondition(return_keys) + ") order by d.linkcode,p.seq,p.code");
		int rowcount = billList_post.getRowCount();
		for (int i = 0; i < rowcount; i++) {
			billList_post.setCheckedRow(i, true);
		}

	}

	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		BillVO billVO_tree = billTree_corp.getSelectedVO();
		if (billVO_tree != null) {
			billList_post.QueryDataByCondition(" deptid = '" + billVO_tree.getStringValue("id") + "'");
			onBillListAfterQuery(null);
		}
	}

	public void onBillListCheckedAll(BillListCheckedAllEvent _event) {
		if (billList_post.isTitleChecked()) {
			for (int i = 0; i < billList_post.getRowCount(); i++) {
				postMap.put(billList_post.getRealValueAtModel(i, "id"), billList_post.getRealValueAtModel(i, "deptid"));
			}
		} else {
			for (int i = 0; i < billList_post.getRowCount(); i++) {
				postMap.remove(billList_post.getRealValueAtModel(i, "id"));
			}
		}
		btn_showAll.setText("查看已选(" + postMap.size() + ")");
	}

	public void onBillListChecked(BillListCheckedEvent _event) {
		int row = _event.getCheckedRow();
		if (billList_post.isSelectedRowChecked()) {
			postMap.put(billList_post.getRealValueAtModel(row, "id"), billList_post.getRealValueAtModel(row, "deptid"));
		} else {
			postMap.remove(billList_post.getRealValueAtModel(row, "id"));
		}
		btn_showAll.setText("查看已选(" + postMap.size() + ")");
	}

	public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
		boolean isAllChecked = true;
		for (int i = 0; i < billList_post.getRowCount(); i++) {
			if (postMap.containsKey(billList_post.getRealValueAtModel(i, "id"))) {
				billList_post.setCheckedRow(i, true);
			} else {
				isAllChecked = false;
			}
		}
		if (billList_post.getRowCount() > 0 && isAllChecked) {
			billList_post.setTitleChecked(true);
		} else {
			billList_post.setTitleChecked(false);
		}
		btn_showAll.setText("查看已选(" + postMap.size() + ")");
	}
}
