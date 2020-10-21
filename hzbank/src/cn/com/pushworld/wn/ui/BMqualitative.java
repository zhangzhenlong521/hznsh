package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import com.sun.mail.handlers.message_rfc822;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

//部门定性指标打分
public class BMqualitative extends AbstractWorkPanel implements ActionListener,
		BillListSelectListener {

	private BillListPanel listPanel;
	private WLTButton btn_ks, btn_end = null;
	private String str = null;
	private CommDMO dmo = new CommDMO();

	@Override
	public void initialize() {
		// WN_JDFKMHZB_02_Q02_ZPY
		listPanel = new BillListPanel("WN_BMPFPLAN_Q01_ZPY");
		btn_ks = new WLTButton("开始考核");
		btn_ks.addActionListener(this);
		btn_end = new WLTButton("结束考核");
		btn_end.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_ks, btn_end });
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_ks) {// 开始打分
			gradeScore();
		} else if (e.getSource() == btn_end) {// 结束打分
			gradeEnd();
		}
	}

	private void gradeEnd() {// 1.将当前状态全部结束;2.计算每个部门每一大项的最终得分
		try {// 首先锁定每一大项，然后确定每一个部门
			BillVO selectedBillVO = listPanel.getSelectedBillVO();
			if (selectedBillVO == null) {
				MessageBox.show("请选中一行计划进行操作");
				return;
			}
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			new SplashWindow(this, new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					str = service.gradeBMScoreEnd();
				}
			});
			MessageBox.show(this, str);
			listPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开始打分，为每个指标生成打分计划
	 */
	private void gradeScore() {
		try {
			// 判断当前考计划中是否存在打分项
			final BillVO vo = listPanel.getSelectedBillVO();
			if (vo == null) {
				MessageBox.show(this, "请选中一条状态为【未生成】的计划进行操作！！！");
				return;
			}
			final String id = vo.getStringValue("id");// 获取当前计划的id
			HashVO[] hashvo = UIUtil.getHashVoArrayByDS(null,
					"select * from wn_BMPF_table where planid='" + id + "' ");
			if (hashvo.length > 0) {// 不允许重复生成计划
				MessageBox.show(this, "当前选中计划【" + vo.getStringValue("PLANNAME")
						+ "】已经存在");
				return;
			}
			HashVO[] vos = UIUtil.getHashVoArrayByDS(null,
					"select * from WN_BMPFPLAN where state='评分中' and id!='"
							+ id + "'");
			if (vos.length > 0) {//
				MessageBox.show(this, "存在尚未结束的部门考核计划,请先结束！！！");
				return;
			}
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					str = service.getBMsql(id);
					UpdateSQLBuilder update = new UpdateSQLBuilder(listPanel
							.getTempletVO().getTablename());
					update.setWhereCondition("id='" + vo.getStringValue("id")
							+ "'");
					update.putFieldValue("state", "评分中");
					try {
						UIUtil.executeUpdateByDS(null, update.getSQL());
						listPanel.refreshCurrSelectedRow();
						listPanel.refreshData();
					} catch (WLTRemoteException e1) {
						e1.printStackTrace();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBillListSelectChanged(BillListSelectionEvent event) {
		BillVO vo = listPanel.getSelectedBillVO();
		String state = vo.getStringValue("state");
		if ("评分中".equals(state)) {
			btn_ks.setEnabled(false);
		} else if ("评分结束".equals(state)) {
			btn_ks.setEnabled(false);
			btn_end.setEnabled(false);
		} else {
			btn_ks.setEnabled(true);
			btn_end.setEnabled(true);
		}
	}
}