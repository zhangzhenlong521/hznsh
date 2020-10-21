package cn.com.pushworld.wn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class JDFKMhuizong extends AbstractWorkPanel implements ActionListener {

	private BillListPanel listPanel = null;
	private UIUtil uiUtil = new UIUtil();
	private WLTButton updateButton, removeButton, submitButton;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("WN_JDFKMHZB_02_ZPY");
		submitButton = new WLTButton("提交");
		submitButton.addActionListener(this);
		updateButton = new WLTButton("修改");
		updateButton.addActionListener(this);
		removeButton = new WLTButton("删除");
		removeButton.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { updateButton,
				removeButton, submitButton });
//		listPanel.getQuickQueryPanel().addBillQuickActionListener(this);// 获取到快速查询事件
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == submitButton) {//
			submitData();
		} else if (e.getSource() == removeButton) {
			removeData();
		} else if (e.getSource() == updateButton) {
			updateData();
//		} else if (listPanel.getQuickQueryPanel() == e.getSource()) {
//			QuickQuery();
		}
	}

	// 重写快速查询
	private void QuickQuery() {
		
		 String condition="1=1 "+listPanel.getQuickQueryPanel().getQuerySQLCondition(); 
		 String GY_Name=ClientEnvironment.getInstance().getLoginUserCode();
		 //获取到当前用户的code
		 System.out.println("code:"+GY_Name);
		 condition=condition+" and GY_ID='"+GY_Name+"'";
		  System.out.println(condition);
		  listPanel.QueryDataByCondition(condition);
		 
	}

	// 修改数据
	private void updateData() {
		try {
			BillVO[] selected = listPanel.getSelectedBillVOs();
			if (selected.length != 1) {
				MessageBox.show(listPanel, "请选中一条数据进行修改");
				return;
			}
			BillVO billvo = selected[0];// 获取到用户选中的数据
			String state = billvo.getStringValue("state");
			if (state.equals("未提交") || state.equals("已退回")) {
				BillCardPanel cardpanel = new BillCardPanel(
						"WN_JDFKMHZB_02_ZPY");
				cardpanel.setBillVO(billvo);
				BillCardDialog dialog = new BillCardDialog(listPanel, "编辑",
						cardpanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
				dialog.setVisible(true);
				listPanel.refreshCurrSelectedRow();
			} else {
				MessageBox.show("当前数据状态为【" + state + "】不允许修改");
			}
			listPanel.refreshCurrData();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 删除数据
	private void removeData() {
		try {
			BillVO[] selected = listPanel.getSelectedBillVOs();
			if (selected.length <= 0) {
				MessageBox.show(listPanel, "请选择一条或多条记录再执行此操作;");
				return;
			}
			String notId = "";
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < selected.length; i++) {
				String state = selected[i].getStringValue("state");
				String id = selected[i].getStringValue("id");
				if (!state.equals("未提交")) {
					if (notId.isEmpty()) {
						notId = notId + id;
					} else {
						notId = notId + " " + id;
					}
				} else {
					String sql = "delete from WN_JDFKMHZB_02 where id='" + id
							+ "'";
					list.add(sql);
				}
			}
			if (list.size() > 0) {
				int check = MessageBox.showOptionDialog(this, "确定删除数据吗？", "提示",
						new String[] { "确定", "取消" }, 1);
				if (check == 0) {
					uiUtil.executeBatchByDS(null, list);
				} else {
					return;
				}
			}
			if (!"".equals(notId.trim())) {
				MessageBox.show("id为" + notId + "的数据状态已改变，不可删除！！！");
			}
			listPanel.refreshCurrData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 提交事件
	private void submitData() {
		try {
			// 获取选中数据
			BillVO[] selected = listPanel.getSelectedBillVOs();
			if (selected.length <= 0) {
				MessageBox.show(listPanel, "请选择一条或多条记录在执行此操作;");
				return;
			}
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < selected.length; i++) {
				String selectedId = selected[i].getStringValue("ID");
				String state = selected[i].getStringValue("state");
				String JFFSE = selected[i].getStringValue("JFFSE");
				String JFTJZJ = selected[i].getStringValue("JFTJZJ");
				String DFFSE = selected[i].getStringValue("DFFSE");
				String DFTJZJ = selected[i].getStringValue("DFTJZJ");
				String updateSQL = "";
				if (state.equals("未提交") || state.equals("已退回")) {
					if (Double.parseDouble(JFFSE) < Double.parseDouble(JFTJZJ)
							|| Double.parseDouble(DFFSE) < Double
									.parseDouble(DFTJZJ)) {
						MessageBox.show(this, "您选择提交的记录中存在借方发生额小于借方调剂资金或者贷方发生额小于贷方调剂资金的数据，请重新填写后提交！");
					} else {
						updateSQL = "update WN_JDFKMHZB_02 set state='已提交' where id='"
								+ selectedId + "'";
						list.add(updateSQL);
					}
				}
				System.out.println(list.size());
				if (list.size() >= 5000) {
					UIUtil.executeBatchByDS(null, list);
					list.clear();
				}
			}
			if (list.size() >= 0) {
				UIUtil.executeBatchByDS(null, list);
			} else {
				MessageBox.show("当前选中数据已经提交,请重新选择");
			}
			listPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
