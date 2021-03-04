package cn.com.pushworld.salary.ui.target.p040;

import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.mdata.jepfunctions.ItemValueListener;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 打分界面，修改分数以后执行公式。
 * 
 * @author haoming create by 2013-7-10
 */
public class UserCheckTargetValueChangeLinstener implements ItemValueListener {

	public void process(BillPanel panel) {
		BillVO currEditVO = null;
		String currEditItem = "";
		if (panel instanceof BillCardPanel) { // 如果是卡片执行次方法，目前会有问题。 得不到当前编辑的字段
			BillCardPanel cardPanel = (BillCardPanel) panel;
			currEditVO = cardPanel.getBillVO();
		} else {
			BillListPanel listPane = (BillListPanel) panel;
			currEditVO = listPane.getSelectedBillVO();
			currEditItem = listPane.getSelectedColumnItemKey();
		}
		String id = currEditVO.getStringValue("id");
		UpdateSQLBuilder updateSql = new UpdateSQLBuilder("sal_dept_check_score");
		updateSql.setWhereCondition(" id = " + id);
		if ("checkdratio".equalsIgnoreCase(currEditItem)) {// 得到当前编辑的字段,如果是扣分字段
			String checkdratio = currEditVO.getStringValue("checkdratio"); // 扣分比例
			String weights = currEditVO.getStringValue("weights"); // 权重分值
			float f_weights = Float.parseFloat(weights); // 权重分值
			if (checkdratio == null || checkdratio.equals("")) {
				updateSql.putFieldValue("checkscore", "");
			} else {
				float f_checkratio = Float.parseFloat(checkdratio); // 扣分比例
				float lastScore = f_weights * (100 - f_checkratio) / 100;
				updateSql.putFieldValue("checkscore", lastScore + "");
			}
			updateSql.putFieldValue("scoredeptname", ClientEnvironment.getLoginUserDeptName());
			updateSql.putFieldValue("checkdratio", checkdratio);
			updateSql.putFieldValue("status", "待提交");
		} else if ("recheckdratio".equalsIgnoreCase(currEditItem)) { // 是 修改扣分字段
			String checkdratio = currEditVO.getStringValue("recheckdratio"); // 要修改到的扣分比例
			updateSql.putFieldValue("recheckdratio", checkdratio);
		}
		try {
			List sqllist = new ArrayList();
			sqllist.add(updateSql);
			UIUtil.executeBatchByDS(null, sqllist, true, false);
			if (panel instanceof BillListPanel) {
				((BillListPanel) panel).refreshCurrSelectedRow(); // 修改成功后，刷新
				DeptTargetScoredWKPanel scoreWKPanel = (DeptTargetScoredWKPanel) panel.getClientProperty("this"); //取到执行
				if (scoreWKPanel != null) { //必须判断是否为空。因为工作流处理列表中也会有此动作
					scoreWKPanel.autoCheckIFCanCommit(); //修改一次判断下是否可以提交。
				}
			}
		} catch (Exception e) {
			if (e.getMessage().contains("connect")) {
				MessageBox.showWarn(panel, "您的电脑网络可能出现问题.请检查本地连接.");
			} else {
				MessageBox.showException(panel, e);
			}
			e.printStackTrace();
		}

	}
}
