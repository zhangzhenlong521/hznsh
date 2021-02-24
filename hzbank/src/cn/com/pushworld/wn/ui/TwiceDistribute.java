package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class TwiceDistribute extends AbstractWorkPanel implements
		ActionListener {

	// <<<<<<< HEAD
	// private CommDMO dmo = new CommDMO();
	// private BillListPanel billListPanel;
	// private WLTButton btn_submit, btn_edit,btn_select;
	// private String time;
	// @Override
	// public void actionPerformed(ActionEvent arg0) {
	// InsertSQLBuilder insert = new InsertSQLBuilder("wn_twicedistribute");
	// time = billListPanel.getQuickQueryPanel().getCompentRealValue("time");
	// List list = new ArrayList<String>();
	// try {
	// String[][] infor = dmo.getStringArrayByDS(null,
	// "select deptname,username,usercode from v_pub_user_post_1");
	// for(int i = 0; i < infor.length;i++){
	// String id = dmo.getSequenceNextValByDS(null,"wn_twicedistribute");
	// insert.putFieldValue("id", id);
	// insert.putFieldValue("WDMC", infor[i][0]);
	// insert.putFieldValue("name", infor[i][1]);
	// insert.putFieldValue("code", infor[i][2]);
	// insert.putFieldValue("time", time);
	// list.add(insert.getSQL());
	// }
	// dmo.executeBatchByDS(null, list);
	//
	// } catch (Exception e) {
	//
	// e.printStackTrace();
	// =======
	private UIUtil uiutil = new UIUtil();
	private BillListPanel billListPanel = null;
	private WLTButton btn_submit, btn_edit, btn_select;
	private String WDMC = ClientEnvironment.getInstance()
			.getLoginUserDeptName();

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_submit) {
			try {
				onSubmit();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == btn_edit) {
			onEdit();
		} else if (e.getSource() == btn_select) {
			onSelect();
		} else if (e.getSource() == billListPanel.getQuickQueryPanel()) {
			String status = "草稿";
			InsertSQLBuilder insert = new InsertSQLBuilder("wn_twicedistribute");
			String time = billListPanel.getQuickQueryPanel()
					.getCompentRealValue("time");
			List list = new ArrayList<String>();
			try {
				String[][] chaxun = uiutil.getStringArrayByDS(null,
						"select * from wn_twicedistribute where time='" + time
								+ "'");
				if (chaxun.length <= 0) {
					try {
						String[][] infor = uiutil
								.getStringArrayByDS(null,
										"select deptname,username,usercode from v_pub_user_post_1");
						for (int i = 0; i < infor.length; i++) {
							String id = UIUtil.getSequenceNextValByDS(null,
									"wn_twicedistribute");
							insert.putFieldValue("id", id);
							insert.putFieldValue("WDMC", infor[i][0]);
							insert.putFieldValue("name", infor[i][1]);
							insert.putFieldValue("code", infor[i][2]);
							insert.putFieldValue("time", time);
							insert.putFieldValue("money", 0);
							insert.putFieldValue("status", status);
							list.add(insert.getSQL());
						}
						uiutil.executeBatchByDS(null, list);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			Query();
		}
	}

	public void onSelect() {
		BillVO billvo = billListPanel.getSelectedBillVO();
		if (billvo == null || billvo.equals("")) {
			MessageBox.show("请选择一条记录再执行此操作！");
			return;
		} else {
			BillCardPanel cardPanel = new BillCardPanel(
					"WN_TWICEDISTRIBUTE_CODE1");
			BillCardDialog cardDialog = new BillCardDialog(billListPanel, "查看",
					cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
			cardPanel.setBillVO(billvo);
			cardDialog.setVisible(true);
		}

	}

	public void onEdit() {
		BillVO billvo = billListPanel.getSelectedBillVO();
		if (billvo == null || billvo.equals("")) {
			MessageBox.show("请选择一条记录再执行此操作！");
			return;
		}
		String status = billvo.getStringValue("status");
		if (status.equals("已提交")) {
			MessageBox.show("该记录已提交无法修改");
			return;
		} else {
			BillCardPanel cardpanel = new BillCardPanel(
					"WN_TWICEDISTRIBUTE_CODE1");
			cardpanel.setBillVO(billvo);
			BillCardDialog dialog = new BillCardDialog(billListPanel, "编辑",
					cardpanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
			dialog.setVisible(true);
			cardpanel.setEditable("status", false);// 设置修改时，不能修改状态
			billListPanel.refreshCurrSelectedRow();
		}

	}

	public void onSubmit() throws Exception {
		BillVO billvo = billListPanel.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.show("请选择一条记录再执行此操作！");
			return;
		} else {
			String id = billvo.getStringValue("id");
			uiutil.executeUpdateByDS(null,
					"update wn_twicedistribute set status='已提交' where id='"
							+ id + "'");
			billListPanel.refreshData();
			// >>>>>>> 5893a9e34fae888803f17ebc95aff84e07f5e9fc
		}

	}

	// <<<<<<< HEAD
	// @Override
	// public void initialize() {
	// BillListPanel billListPanel = new
	// BillListPanel("WN_TWICEDISTRIBUTE_CODE1");
	// btn_submit = new WLTButton("提交");
	// btn_submit.addActionListener(this);
	// billListPanel.addBatchBillListButton(new WLTButton[] { btn_submit,
	// btn_select, btn_edit });
	// billListPanel.repaintBillListButton();
	// btn_edit = billListPanel.getBillListBtn("$列表弹出编辑");
	// btn_edit.addActionListener(this);
	// btn_select = billListPanel.getBillListBtn("$列表查看");
	// btn_select.addActionListener(this);
	// =======
	public void Query() {
		String name1;
		String code1;
		String time1;
		String name = billListPanel.getQuickQueryPanel().getCompentRealValue(
				"name");
		String code = billListPanel.getQuickQueryPanel().getCompentRealValue(
				"code");
		String time = billListPanel.getQuickQueryPanel().getCompentRealValue(
				"time");
		if (name.equals("")) {
			name1 = "1=1";
		} else {
			name1 = "name='" + name + "'";
		}
		if (code.equals("")) {
			code1 = " and 1=1";
		} else {
			code1 = " and code='" + code + "'";
		}
		if (time.equals("")) {
			time1 = "and 1=1";
		} else {
			time1 = " and time='" + time + "'";
		}
		String sqlCondition = name1 + code1 + time1;
		String sql = "select * from wn_twicedistribute where " + sqlCondition
				+ " and WDMC = '" + WDMC + "'";
		billListPanel.QueryData(sql);

	}

	@Override
	public void initialize() {
		billListPanel = new BillListPanel("WN_TWICEDISTRIBUTE_CODE1");
		btn_submit = new WLTButton("提交");
		btn_submit.addActionListener(this);
		btn_edit = new WLTButton("修改");
		btn_edit.addActionListener(this);
		btn_select = new WLTButton("浏览");
		btn_select.addActionListener(this);
		billListPanel.addBatchBillListButton(new WLTButton[] { btn_submit,
				btn_select, btn_edit });
		billListPanel.repaintBillListButton();
		billListPanel.setVisible(true);
		billListPanel.getQuickQueryPanel().addBillQuickActionListener(this);// 获取到快速查询事件
		this.add(billListPanel);
	}

}
