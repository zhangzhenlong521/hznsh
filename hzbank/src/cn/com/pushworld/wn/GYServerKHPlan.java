package cn.com.pushworld.wn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.text.html.StyleSheet.ListPainter;

import com.ibm.db2.jcc.a.v;

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
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;

/**
 * @author zzl
 * 
 *         2019-3-27-下午06:26:03 柜员服务质量打分计划
 */
public class GYServerKHPlan extends AbstractWorkPanel implements
		ActionListener, BillListSelectListener {
	private BillListPanel list = null;
	private WLTButton btn_ks, btn_end, btn_delete = null;
	private String str = null;
	private CommDMO dmo = new CommDMO();

	@Override
	public void initialize() {
		list = new BillListPanel("WN_GYKHPLAN_CODE1");
		btn_ks = new WLTButton("开始打分");
		btn_end = new WLTButton("结束打分");
		btn_delete = new WLTButton("删除");
		btn_ks.addActionListener(this);
		btn_end.addActionListener(this);
		btn_delete.addActionListener(this);
		list.addBillListButton(btn_delete);
		list.addBillListButton(btn_ks);
		list.addBillListButton(btn_end);
		list.repaintBillListButton();
		list.addBillListSelectListener(this);
		this.add(list);
	}

	@Override
	public void actionPerformed(ActionEvent act) {
		if (act.getSource() == btn_ks) {
			gradeScore();
		} else if (act.getSource() == btn_end) {
			gradeEnd();
		} else if (act.getSource() == btn_delete) {
			deleteScore();
		}
	}

	private void deleteScore() {// 删除评分计划
		try {
			BillVO vo = list.getSelectedBillVO();
			if (vo == null) {
				MessageBox.show(this, "请选中一条考核计划进行删除");
				return;
			}
			// 获取到当前考核计划的状态
			String state = UIUtil.getStringValueByDS(
					null,
					"select state from WN_GYKHPLAN where id='"
							+ vo.getStringValue("ID") + "'");
			if (!"未评分".equals(state)) {
				MessageBox.show(this, "当前评分计划【" + vo.getStringValue("PLANNAME")
						+ "】的状态为【" + state + "】,无法删除");
				return;
			}
			UIUtil.executeUpdateByDS(
					null,
					"delete from WN_GYKHPLAN  where ID='"
							+ vo.getStringValue("id") + "'");
			list.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBillListSelectChanged(BillListSelectionEvent ect) {
		BillVO vo = list.getSelectedBillVO();
		if (vo.getStringValue("state").equals("评分中")) {
			btn_ks.setEnabled(false);
			btn_end.setEnabled(true);
		} else if (vo.getStringValue("state").equals("评分结束")) {
			btn_ks.setEnabled(false);
			btn_end.setEnabled(false);
		} else {
			btn_ks.setEnabled(true);
			btn_end.setEnabled(true);
		}
	}

	public void gradeScore(int q) {
		try {
			final BillVO vo = list.getSelectedBillVO();
			HashVO[] vos = UIUtil
					.getHashVoArrayByDS(null, "select * from "
							+ list.getTempletVO().getTablename()
							+ " where state='评分中'");
			if (vos.length > 0) {
				MessageBox.show(this, "有评分中的考核计划，请先结束评分中的考核计划，然后在新建考核计划");
				return;
			}
			HashVO[] timeVos = UIUtil.getHashVoArrayByDS(null, "select * from "
					+ list.getTempletVO().getTablename() + " where PLANNAME='"
					+ vo.getStringValue("PLANTIME") + "'");
			if (timeVos.length > 0) {
				MessageBox.show(this, "考核时间为【" + vo.getStringValue("PLANTIME")
						+ "】计划已经存在！");
				return;
			}
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					str = service.getSqlInsert(vo.getStringValue("PLANTIME"));
					UpdateSQLBuilder update = new UpdateSQLBuilder(list
							.getTempletVO().getTablename());
					update.setWhereCondition("id='" + vo.getStringValue("id")
							+ "'");
					update.putFieldValue("state", "评分中");
					try {
						UIUtil.executeUpdateByDS(null, update.getSQL());
						list.refreshCurrSelectedRow();
					} catch (WLTRemoteException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			list.refreshData();
			MessageBox.show(this, str);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void gradeScore() {
		try {
			// BillVO selectedBillVO = list.getSelectedBillVO();
			// if("评分结束".equals(selectedBillVO.getStringValue("state"))){
			// MessageBox.show(this,"当前考核计划已经结束");
			// }
			// 获取到当前打分项
			BillVO vo = list.getSelectedBillVO();
			if (vo == null) {
				MessageBox.show(this, "请选中一行数据进行操作");
				return;
			}
			String id = vo.getStringValue("ID");
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent event) {
					String date = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss")
							.format(new Date());
					str = service.getSqlInsert(date);
				}
			});
			UIUtil.executeUpdateByDS(null,
					"UPDATE WN_GYKHPLAN SET STATE='评分中' WHERE ID='" + id + "'");
			MessageBox.show(this, str);
			list.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void gradeEnd() {
		try {// 结束打分:1.修改状态;2.计算总分
			BillVO selectedBillVO = list.getSelectedBillVO();
			if (selectedBillVO == null) {
				MessageBox.show(this, "请选中一条考核计划进行操作");
				return;
			}
			String pftime=UIUtil.getStringValueByDS(null,"select max(pftime) from WN_GYPF_TABLE") ;
			final HashVO[] vo = UIUtil
					.getHashVoArrayByDS(
							null,
							"SELECT distinct(USERCODE) AS USERCODE,STATE,PFTIME,FHRESULT FROM WN_GYPF_TABLE WHERE STATE='评分中'  OR FHRESULT<>'复核通过' AND PFTIME='"+pftime+"'");
			String result = UIUtil
					.getStringValueByDS(
							null,
							"SELECT COUNT(*) FROM WN_GYPF_TABLE WHERE STATE='评分中' OR FHRESULT IS NULL OR FHRESULT<>'复核通过' AND PFTIME='"+pftime+"'");
			int count = Integer.parseInt(result);

			int sumcount = 0;
			if (count > 0) {
				sumcount = MessageBox.showOptionDialog(this,
						"当前考核计划中存在是否评分尚未结束或尚未复核的柜员，是否结束当前考核计划", "提示",
						new String[] { "否", "是" }, 0);
			}
			if (sumcount != 0) {
				new SplashWindow(this, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent actionevent) {
						for (int i = 0; i < vo.length; i++) {
							HashVO v = vo[i];
							gradeEndEveryOne(v.getStringValue("USERCODE"));
						}
					}
				});
				MessageBox.show(this, "当前考核计划结束成功");
				UIUtil.executeUpdateByDS(null,
						"update WN_GYKHPLAN set  state='评分结束' where id='"
								+ selectedBillVO.getStringValue("ID") + "'");
			} else {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void gradeEnd(int w) {
		try {
			BillVO vo = list.getSelectedBillVO();
			HashVO[] vos = UIUtil.getHashVoArrayByDS(
					null,
					"select * from WN_GYPF_TABLE where 1=1 and PFTIME='"
							+ vo.getStringValue("PLANTIME")
							+ "' and KOUOFEN is null");
			if (vos.length > 0) {
				if (MessageBox.confirm(this, "有未打完的评分，确定要强制结束吗？")) {
					UpdateSQLBuilder update = new UpdateSQLBuilder(list
							.getTempletVO().getTablename());
					update.setWhereCondition("id='" + vo.getStringValue("id")
							+ "'");
					update.putFieldValue("state", "评分结束");
					UIUtil.executeUpdateByDS(null, update.getSQL());
					UpdateSQLBuilder update2 = new UpdateSQLBuilder(
							"WN_GYPF_TABLE");
					update2.setWhereCondition("PFTIME='"
							+ vo.getStringValue("PLANTIME") + "'");
					update2.putFieldValue("state", " 评分结束");
					UIUtil.executeUpdateByDS(null, update2.getSQL());
					list.refreshData();
					MessageBox.show(this, "结束打分成功");
				}
			} else {
				UpdateSQLBuilder update = new UpdateSQLBuilder(list
						.getTempletVO().getTablename());
				update.setWhereCondition("id='" + vo.getStringValue("id") + "'");
				update.putFieldValue("state", "评分结束");
				UIUtil.executeUpdateByDS(null, update.getSQL());
				UpdateSQLBuilder update2 = new UpdateSQLBuilder("WN_GYPF_TABLE");
				update2.setWhereCondition("PFTIME='"
						+ vo.getStringValue("PLANTIME") + "'");
				update2.putFieldValue("state", "评分结束");
				UIUtil.executeUpdateByDS(null, update2.getSQL());
				list.refreshData();
				MessageBox.show(this, "结束打分成功");
			}
		} catch (Exception e) {
			MessageBox.show(this, "结束打分失败");
			e.printStackTrace();
		}
	}

	public void gradeEndEveryOne(String usercode) {
		try {
			Double KOUOFEN = 0.0;
			Double result = 0.0;
			String pftime=UIUtil.getStringValueByDS(null, "select max(pftime) from WN_GYPF_TABLE where usercode='"+usercode+"'")  ;
			UpdateSQLBuilder update = new UpdateSQLBuilder(
					"wnSalaryDb.WN_GYPF_TABLE");
			HashVO[] vo = UIUtil.getHashVoArrayByDS(null,
					"select * from wnSalaryDb.WN_GYPF_TABLE where usercode='"
							+ usercode + "' and  (FHRESULT<>'复核通过' OR FHRESULT IS null) and pftime='"+pftime+"'   Order by ID");
			List list = new ArrayList<String>();
			
			for (int i = 0; i < vo.length - 1; i++) {
				String koufenValue = vo[i].getStringValue("KOUFEN");
				String defenValue = vo[i].getStringValue("FENZHI");
				if (koufenValue == null || "".equals(koufenValue)) {
					koufenValue = defenValue;
				}
				if (Double.parseDouble(koufenValue) > Double
						.parseDouble(defenValue)) {
					koufenValue = defenValue;
				}
				KOUOFEN = Double.parseDouble(koufenValue);
				result = result + KOUOFEN;
				update.setWhereCondition("id='" + vo[i].getStringValue("id")
						+ "' and pftime='"+pftime+"'");
				update.putFieldValue("KOUOFEN", KOUOFEN);
				update.putFieldValue("state", "评分结束");
				list.add(update.getSQL());
			}
			double sumfen = 100.00 - result;
			String sumSQL = "update wnSalaryDb.WN_GYPF_TABLE set KOUOFEN='"
					+ sumfen + "',state='评分结束' where usercode='" + usercode
					+ "' and xiangmu='总分' and pftime='"+pftime+"'";
			list.add(sumSQL);
			if (list.size() > 0) {
				UIUtil.executeBatchByDS(null, list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
