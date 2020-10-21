package cn.com.pushworld.wn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sun.mail.handlers.message_rfc822;

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

public class CsbShangbaoHuizong extends AbstractWorkPanel implements
		ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BillListPanel list_01;
	private WLTButton btn_submit, btn_edit, btn_del;
	private String id;
	private String deptId=ClientEnvironment.getInstance().getLoginUserDeptId();
	@Override
	public void initialize() {
		id = ClientEnvironment.getCurrLoginUserVO().getId();
		list_01 = new BillListPanel("WN_CSBHZ_01_CODE1");
		btn_submit = new WLTButton("提交");
		btn_submit.addActionListener(this);
		list_01.addBatchBillListButton(new WLTButton[] { btn_submit, btn_del,
				btn_edit });
		list_01.repaintBillListButton();
		btn_del = list_01.getBillListBtn("$列表直接删除");
		btn_del.addActionListener(this);
		btn_edit = list_01.getBillListBtn("$列表弹出编辑");
		btn_edit.addActionListener(this);
		list_01.setVisible(true);
//		list_01.getQuickQueryPanel().addBillQuickActionListener(this);// 获取到快速查询事件
		this.add(list_01);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_submit) {
			onSubmit();
		} else if (obj == btn_del) {
			onDel();
		} else if (obj == btn_edit) {
			onEdit();
//		} else if (list_01.getQuickQueryPanel() == e.getSource()) {
//			QuickQuery();
		}
	}

	private void QuickQuery() {
		String condition = " 1=1 "
				+ list_01.getQuickQueryPanel().getQuerySQLCondition();// 获取到查询条件
		String GY_Name = ClientEnvironment.getInstance().getLoginUserCode();// 获取到当前柜员的号
		condition = condition + " and  GY_ID='" + GY_Name + "'";
		System.out.println(condition);
		list_01.QueryDataByCondition(condition);
	}

	private void onEdit() {
		BillVO billvo = list_01.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.show(list_01, "请选择一条记录再进行此操作.");
			return;
		}
		String status = billvo.getStringValue("state");
		if (status.equals("已提交")) {
			MessageBox.show(list_01, "此数据已经提交不可修改！");
			return;
		} else {
			BillCardPanel cardpanel = new BillCardPanel("WN_CSBHZ_01_CODE1");
			cardpanel.setBillVO(billvo);
			BillCardDialog dialog = new BillCardDialog(list_01, "编辑",
					cardpanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
			dialog.setVisible(true);
			list_01.refreshCurrSelectedRow();
		}

	}

	private void onDel() {
		try {
			BillVO billvo = list_01.getSelectedBillVO();
			BillVO[] selected = list_01.getSelectedBillVOs();
			if (selected.length <= 0) {
				MessageBox.show(list_01, "请选择一条记录再进行此操作.");
				return;
			}
			String notId = "";
			List<String> list = new ArrayList<String>();
			for (int i = 0; i < selected.length; i++) {
				String state = selected[i].getStringValue("state");
				String id = selected[i].getStringValue("ID");
				if ("已退回".equals(state.trim()) || "未提交".equals(state.trim())) {
					String sql = "delete from WN_CSBHZ_01 where id='" + id
							+ "'";
					list.add(sql);
				} else {
					if ("".equals(notId.trim())) {
						notId = notId + id;
					} else {
						notId = notId + " " + id;
					}
				}
			}
			if (list.size() >= 1) {
				int check = MessageBox.showOptionDialog(this, "确定删除数据吗？", "提示",
						new String[] { "确定", "取消" }, 1);
				if (check == 0) {
					UIUtil.executeBatchByDS(null, list);
				} else {
					return;
				}
			}
			if (notId != null & !"".equals(notId)) {
				MessageBox.show(list_01, "您选中的数据中存在已经提交或者已经审核的数据，无法删除！！！");
			}
			list_01.refreshCurrData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// String status = billvo.getStringValue("state");
		// if (status.equals("已提交")) {
		// MessageBox.show(list_01, "此数据已经提交不可删除！");
		// return;
		// } else {
		// String creater = billvo.getStringValue("GY_ID");
		// if (!id.equals(creater)) {
		// MessageBox.show(list_01, "非本人创建的记录无权进行删除.");
		// return;
		// }
		// int k = MessageBox.showConfirmDialog(list_01, "您确定要删除吗?");
		// if (k == 0) {
		// try {
		// UIUtil.executeUpdateByDS(null, "delete from WN_CSBHZ_01 where id=" +
		// billvo.getPkValue());
		// list_01.removeSelectedRow();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }

	}

	private void onSubmit() {
		try {
			BillVO billvo = list_01.getSelectedBillVO();
			if (billvo == null || billvo.equals("")) {
				MessageBox.show(list_01, "请选择一条记录再进行此操作.");
				return;
			}
			String state = billvo.getStringValue("state");
			if (state.equals("未提交") || state.equals("已退回")) {
				int k = MessageBox.showConfirmDialog(list_01, "您确定要提交给委派会计吗?");
				if (k != 0) {
					return;
				}
				String date = new SimpleDateFormat("yyyy-MM-dd")
						.format(new Date());
				UIUtil.executeUpdateByDS(null,
						"update WN_CSBHZ_01 set state='已提交' where id =" + billvo.getPkValue());
				list_01.refreshCurrSelectedRow();
				MessageBox.show(list_01, "提交成功!");
			} else if (state.equals("已提交")) {
				MessageBox.show(list_01, "该数据无法再次提交！");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
