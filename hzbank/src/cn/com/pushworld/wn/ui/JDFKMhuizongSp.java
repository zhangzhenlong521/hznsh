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

/**
 * 
 * @author zzl
 * 
 *         2019-3-28-下午05:01:24 借贷方科目审批
 */
public class JDFKMhuizongSp extends AbstractWorkPanel implements ActionListener,BillListHtmlHrefListener{

	private BillListPanel list = null;
	private JComboBox comboBox = null;//复选框
	private WLTButton updateButton, verifyButton, vertiybatchButton,
			backbatchButton;

	@Override
	public void initialize() {
		list = new BillListPanel("WN_JDFKMHZB_02_Q02_ZPY");
		updateButton = new WLTButton("修改");
		updateButton.addActionListener(this);
		verifyButton = new WLTButton("批复");
		verifyButton.addActionListener(this);
		vertiybatchButton = new WLTButton("批量审核");
		vertiybatchButton.addActionListener(this);
		backbatchButton = new WLTButton("批量退回");
		backbatchButton.addActionListener(this);
		list.addBatchBillListButton(new WLTButton[] { updateButton,
				verifyButton, vertiybatchButton, backbatchButton });
		list.getQuickQueryPanel().addBillQuickActionListener(this);// 获取到快速查询事件
		list.setRowNumberChecked(true);//设置启动
		list.addBillListHtmlHrefListener(this);
		list.repaintBillListButton();
		this.add(list);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == updateButton) {// 修改，只允许修改批复理由
			updateReason();
		} else if (e.getSource() == verifyButton) {// 批复按钮
			verifyData();
		} else if (list.getQuickQueryPanel() == e.getSource()) {
			QuickQuery();
		} else if (e.getSource() == vertiybatchButton) {
			vertiybatchData();
		} else if (e.getSource() == backbatchButton) {
			backbatchData();
		}
	}

	private void vertiybatchData() {
		try {
			BillVO[] billvos = list.getCheckedBillVOs();
			if (billvos == null || billvos.length == 0) {
				MessageBox.show(list, "请选中一条数据！！！");
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
						"update WN_JDFKMHZB_02 set ratifyReason='" + inputValue
								+ "',ratifyPerson='" + ratifyPerson
								+ "',ratifyTime='" + ratifyTime
								+ "',state='已审核' where id in (" + ids + ")");
				list.refreshData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void backbatchData() {// 批量退回
		try {
			BillVO[] billvos = list.getCheckedBillVOs();
			if (billvos == null || billvos.length == 0) {
				MessageBox.show(list, "请选中一条数据！！！");
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
						"update WN_JDFKMHZB_02 set ratifyReason='" + inputValue
								+ "',ratifyPerson='" + ratifyPerson
								+ "',ratifyTime='" + ratifyTime
								+ "',state='已退回' where id in (" + ids + ")");
				list.refreshData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void QuickQuery() {// 查询出的数据只能是柜员已经提交，已退回或者已审核的;
		try {
			// 获取到当前登录人的机构
			String dept = ClientEnvironment.getInstance().getLoginUserDeptId();
			String code = UIUtil.getStringValueByDS(null,
					"SELECT code FROM pub_corp_dept WHERE ID='" + dept + "'");
			String condition = "1=1 "
					+ list.getQuickQueryPanel().getQuerySQLCondition();
			String state = "未提交";
			condition = condition + " and STATE !='" + state + "' and JG_ID='"
					+ code + "'";
			list.QueryDataByCondition(condition);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// private void backData() {
	// BillVO billvo = list.getSelectedBillVO();//获取到当前选中数据
	// if (billvo == null) {
	// MessageBox.show(this, "请选中一条数据！！！");
	// return;
	// }
	// String state = billvo.getStringValue("state");//获取到选中数据的状态
	// if (state.equals("已提交")) {//在已提交的状态下可以退回数据
	// BillCardPanel cardpanel = new BillCardPanel("WN_JDFKMHZB_02_Q02_ZPY");
	// cardpanel.setBillVO(billvo);
	// BillCardDialog dialog = new BillCardDialog(list, "编辑", cardpanel,
	// WLTConstants.BILLDATAEDITSTATE_UPDATE);
	// dialog.setVisible(true);
	// list.refreshCurrSelectedRow();
	// } else {
	// MessageBox.show(this, "当前数据状态是【" + state + "】,无法退回");
	// }
	//
	// }

	private void verifyData() {// 批复数据
		BillVO billvo = list.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.show(list, "请选中一条数据！！！");
			return;
		}
		String state = billvo.getStringValue("state");// 获取到选中数据的状态
		if ("已提交".equals(state.trim())) {
			BillCardPanel cardpanel = new BillCardPanel(
					"WN_JDFKMHZB_02_Q02_ZPY");
			cardpanel.setBillVO(billvo);
			BillCardDialog dialog = new BillCardDialog(list, "编辑", cardpanel,
					WLTConstants.BILLDATAEDITSTATE_UPDATE);
			dialog.setVisible(true);
			list.refreshCurrSelectedRow();
		} else {
			MessageBox.show(this, "当前数据状态是【" + state + "】,无法批复");
		}
	}

	private void updateReason() {
		BillVO billvo = list.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.show(list, "请选中一条数据！！！");
			return;
		}
		String state = billvo.getStringValue("state");// 获取到选中数据的状态
		if ("已审核".equals(state) || "已退回".equals(state)) {
			BillCardPanel cardpanel = new BillCardPanel(
					"WN_JDFKMHZB_02_Q02_ZPY");
			cardpanel.setBillVO(billvo);
			BillCardDialog dialog = new BillCardDialog(list, "编辑", cardpanel,
					WLTConstants.BILLDATAEDITSTATE_UPDATE);
			cardpanel.setEditable("STATE", false);
			dialog.setVisible(true);
			list.refreshCurrSelectedRow();
		} else {
			MessageBox.show(list, "当前数据未批复,无法修改");
		}

	}

	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent arg0) {
		// TODO Auto-generated method stub
		
	}



}
