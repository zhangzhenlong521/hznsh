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
 * ��ֽ��棬�޸ķ����Ժ�ִ�й�ʽ��
 * 
 * @author haoming create by 2013-7-10
 */
public class UserCheckTargetValueChangeLinstener implements ItemValueListener {

	public void process(BillPanel panel) {
		BillVO currEditVO = null;
		String currEditItem = "";
		if (panel instanceof BillCardPanel) { // ����ǿ�Ƭִ�дη�����Ŀǰ�������⡣ �ò�����ǰ�༭���ֶ�
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
		if ("checkdratio".equalsIgnoreCase(currEditItem)) {// �õ���ǰ�༭���ֶ�,����ǿ۷��ֶ�
			String checkdratio = currEditVO.getStringValue("checkdratio"); // �۷ֱ���
			String weights = currEditVO.getStringValue("weights"); // Ȩ�ط�ֵ
			float f_weights = Float.parseFloat(weights); // Ȩ�ط�ֵ
			if (checkdratio == null || checkdratio.equals("")) {
				updateSql.putFieldValue("checkscore", "");
			} else {
				float f_checkratio = Float.parseFloat(checkdratio); // �۷ֱ���
				float lastScore = f_weights * (100 - f_checkratio) / 100;
				updateSql.putFieldValue("checkscore", lastScore + "");
			}
			updateSql.putFieldValue("scoredeptname", ClientEnvironment.getLoginUserDeptName());
			updateSql.putFieldValue("checkdratio", checkdratio);
			updateSql.putFieldValue("status", "���ύ");
		} else if ("recheckdratio".equalsIgnoreCase(currEditItem)) { // �� �޸Ŀ۷��ֶ�
			String checkdratio = currEditVO.getStringValue("recheckdratio"); // Ҫ�޸ĵ��Ŀ۷ֱ���
			updateSql.putFieldValue("recheckdratio", checkdratio);
		}
		try {
			List sqllist = new ArrayList();
			sqllist.add(updateSql);
			UIUtil.executeBatchByDS(null, sqllist, true, false);
			if (panel instanceof BillListPanel) {
				((BillListPanel) panel).refreshCurrSelectedRow(); // �޸ĳɹ���ˢ��
				DeptTargetScoredWKPanel scoreWKPanel = (DeptTargetScoredWKPanel) panel.getClientProperty("this"); //ȡ��ִ��
				if (scoreWKPanel != null) { //�����ж��Ƿ�Ϊ�ա���Ϊ�����������б���Ҳ���д˶���
					scoreWKPanel.autoCheckIFCanCommit(); //�޸�һ���ж����Ƿ�����ύ��
				}
			}
		} catch (Exception e) {
			if (e.getMessage().contains("connect")) {
				MessageBox.showWarn(panel, "���ĵ���������ܳ�������.���鱾������.");
			} else {
				MessageBox.showException(panel, e);
			}
			e.printStackTrace();
		}

	}
}
