package cn.com.pushworld.wn.ui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions.IF;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class CsbShangbaoPF extends AbstractWorkPanel implements ActionListener,BillListHtmlHrefListener {

	private BillListPanel listPanel = null;
	private JComboBox comboBox = null;//复选框
	private WLTButton updateButton, vertifyButton, vertifyBatchButton,
			backBatchButton;

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == updateButton) {// 修改
			updateData();
		} else if (e.getSource() == vertifyButton) {// 批复
			vertifyData();
		} else if (e.getSource() == vertifyBatchButton) {// 批量审核
			vertifyBatchData();
		} else if (e.getSource() == backBatchButton) {// 批量退回
			backBatchData();
		}else if (e.getSource() == listPanel.getQuickQueryPanel()) {
			QuickQuery();
		}
	}

	private void QuickQuery() {// 查询出的结果只能是柜员已经提交 已退回 已批复
		try {
			String condition = "1=1 "
					+ listPanel.getQuickQueryPanel().getQuerySQLCondition();
			String state = "未提交";
			condition = condition + " and STATE !='" + state + "'";
			listPanel.QueryDataByCondition(condition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateData() {// 修改数据
		try {
			BillVO billvo = listPanel.getSelectedBillVO();
			if (billvo == null) {
				MessageBox.show(listPanel, "请选中一条数据！！！");
				return;
			}
			String state = billvo.getStringValue("state");// 获取到选中数据的状态
			if ("已审核".equals(state) || "已退回".equals(state)) {
				BillCardPanel cardpanel = new BillCardPanel(
						"WN_CSBHZ_01_ZPY_Q01");
				cardpanel.setBillVO(billvo);
				BillCardDialog dialog = new BillCardDialog(listPanel, "编辑",
						cardpanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
				dialog.setVisible(true);
				cardpanel.setEditable("STATE", false);// 设置修改时，不能修改状态
				listPanel.refreshCurrSelectedRow();
			} else {
				MessageBox.show(this, "当前数据未批复,无法修改");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void vertifyData() {
		try {
			BillVO billvo = listPanel.getSelectedBillVO();
			if (billvo == null) {
				MessageBox.show(this, "请选中一条数据！！！");
				return;
			}
			String state = billvo.getStringValue("state");// 获取到选中数据的状态
			if ("已提交".equals(state.trim())) {
				BillCardPanel cardpanel = new BillCardPanel(
						"WN_CSBHZ_01_ZPY_Q01");
				cardpanel.setBillVO(billvo);
				BillCardDialog dialog = new BillCardDialog(listPanel, "编辑",
						cardpanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
				dialog.setVisible(true);
				listPanel.refreshCurrSelectedRow();
			} else {
				MessageBox.show(this, "当前数据状态是【" + state + "】,无法批复");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void vertifyBatchData() {
		try {
			BillVO[] billvos = listPanel.getCheckedBillVOs();
			if (billvos == null || billvos.length == 0) {
				MessageBox.show(this, "请选中一条数据！！！");
				return;
			}
			String ids = "";
			String notIds = "";
			String ratifyPerson = ClientEnvironment.getInstance()
					.getLoginUserName();
			String ratifyTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());
			for (int i = 0; i < billvos.length; i++) {
				String state = billvos[i].getStringValue("state");
				String id = billvos[i].getStringValue("id");
				if ("已审核".equals(state) || "已退回".equals(state)) {
					if ("".equals(notIds)) {
						notIds = notIds + id;
					} else {
						notIds = notIds + " " + id;
					}
				} else if ("已提交".equals(state)) {
					if ("".equals(ids)) {
						ids = ids + "'" + id + "'";
					} else {
						ids = ids + ",'" + id + "'";
					}
				}
			}
			if (notIds != null && !"".equals(notIds.trim())) {
				MessageBox.show(this, "选中数据中存在【已审核】或【已退回】数据");
				return;
			} else {
				Frame frame = new Frame();
				String inputValue = JOptionPane.showInputDialog("请输入理由:");
				UIUtil.executeUpdateByDS(null,
						"update WN_CSBHZ_01 set ratifyReason='" + inputValue
								+ "',ratifyPerson='" + ratifyPerson
								+ "',ratifyTime='" + ratifyTime
								+ "',state='已审核' where id in (" + ids + ")");
				listPanel.refreshData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void backBatchData() {
		try {
			BillVO[] billvos = listPanel.getCheckedBillVOs();
			if (billvos == null || billvos.length == 0) {
				MessageBox.show(this, "请选中一条数据！！！");
				return;
			}
			String ids = "";
			String notIds = "";
			String ratifyPerson = ClientEnvironment.getInstance()
					.getLoginUserName();
			String ratifyTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());
			for (int i = 0; i < billvos.length; i++) {
				String state = billvos[i].getStringValue("state");
				String id = billvos[i].getStringValue("id");
				if ("已审核".equals(state) || "已退回".equals(state)) {
					if ("".equals(notIds)) {
						notIds = notIds + id;
					} else {
						notIds = notIds + " " + id;
					}
				} else if ("已提交".equals(state)) {
					if ("".equals(ids)) {
						ids = ids + "'" + id + "'";
					} else {
						ids = ids + ",'" + id + "'";
					}
				}
			}
			if (notIds != null && !"".equals(notIds.trim())) {
				MessageBox.show(this, "选中数据中存在【已审核】或【已退回】数据");
				return;
			} else {
				Frame frame = new Frame();
				String inputValue = JOptionPane.showInputDialog("请输入理由:");
				UIUtil.executeUpdateByDS(null,
						"update WN_CSBHZ_01 set ratifyReason='" + inputValue
								+ "',ratifyPerson='" + ratifyPerson
								+ "',ratifyTime='" + ratifyTime
								+ "',state='已退回' where id in (" + ids + ")");
				listPanel.refreshData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void initialize() {
		listPanel = new BillListPanel("WN_CSBHZ_01_ZPY_Q01");
		updateButton = new WLTButton("修改");
		vertifyButton = new WLTButton("批复");
		vertifyBatchButton = new WLTButton("批量审核");
		backBatchButton = new WLTButton("批量退回");
		updateButton.addActionListener(this);
		vertifyButton.addActionListener(this);
		vertifyBatchButton.addActionListener(this);
		backBatchButton.addActionListener(this);
		listPanel.setRowNumberChecked(true);//设置启动
		listPanel.addBillListHtmlHrefListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { updateButton,
				vertifyButton, vertifyBatchButton, backBatchButton });
		listPanel.repaintBillListButton();
		listPanel.getQuickQueryPanel().addBillQuickActionListener(this);
		this.add(listPanel);
	}

	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}