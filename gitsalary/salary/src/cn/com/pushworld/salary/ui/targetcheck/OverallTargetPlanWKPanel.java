package cn.com.pushworld.salary.ui.targetcheck;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ȫ��ָ��ƻ��ƶ�
 * 
 * @author haoming create by 2013-7-5
 */
public class OverallTargetPlanWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 3380514784981375319L;
	private BillListPanel plan_listPanel;
	private WLTButton btn_createPlan, btn_editPlan, btn_start, btn_watch;

	public void initialize() {
		plan_listPanel = new BillListPanel("SAL_TARGET_CHECK_PLAN_CODE1");
		btn_createPlan = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT, "�����ƻ�");
		btn_editPlan = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT, "�޸�");
		btn_start = new WLTButton("����");
		btn_start.addActionListener(this);
		btn_watch = new WLTButton("Ԥ��");
		plan_listPanel.addBatchBillListButton(new WLTButton[] { btn_createPlan, btn_editPlan, btn_start, btn_watch });
		plan_listPanel.repaintBillListButton();
		this.add(plan_listPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_start) {
			onStart();
		}
	}

	/*
	 * �Ժ�Ѵ��߼��ŵ���������ִ�С�
	 */
	private void onStart() {
		BillVO billvo = plan_listPanel.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String planId = billvo.getStringValue("id"); // �õ��ƻ���ID
		String targetIds = billvo.getStringValue("targetlist"); // �õ��ƻ��������趨����Ҫ���ġ�
		String checkdate = billvo.getStringValue("checkdate"); // ���μƻ����ڡ����¶ȡ�
		try {
			// �����ȸ��ݲ��Ž�������
			HashVO deptTargetVos[] = UIUtil.getHashVoArrayByDS(null, "select *from sal_target_list where parentid in(" + TBUtil.getTBUtil().getInCondition(targetIds) + ")");
			LinkedHashMap<String, List> dept_targetListMap = new LinkedHashMap<String, List>(); // ���ŵ�����ָ��
			for (int i = 0; i < deptTargetVos.length; i++) {
				String deptID = deptTargetVos[i].getStringValue("deptid");
				if (deptID == null || deptID.equals("")) { // Ӧ��û�п�ֵŶ��
					continue;
				}
				List list = null;
				if (dept_targetListMap.containsKey(deptID)) { // �������
					list = dept_targetListMap.get(deptID);
				} else {// ������
					list = new ArrayList();
				}
				list.add(deptTargetVos[i]);
				dept_targetListMap.put(deptID, list);// �Ѳ��ŵ�ָ�꼯�Ϸŵ������С�
			}
			if (dept_targetListMap.size() == 0) {
				MessageBox.show(this, "�����ƶ��ļƻ�ָ�꣬û�б��ֽ⡣");
				return;
			}
			Iterator it = dept_targetListMap.entrySet().iterator();
			List sqlList = new ArrayList();
			while (it.hasNext()) { // �������ŵ�����
				Entry entry = (Entry) it.next();
				String deptID = (String) entry.getKey();
				List<HashVO> deptTargetVo = (List) entry.getValue();
				InsertSQLBuilder insertSql = new InsertSQLBuilder("sal_dept_check");
				insertSql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_sal_dept_check"));
				insertSql.putFieldValue("planid", planId);
				insertSql.putFieldValue("checkdate", checkdate);
				insertSql.putFieldValue("deptid", deptID);
				StringBuffer targetSB = new StringBuffer();
				for (int i = 0; i < deptTargetVo.size(); i++) {
					targetSB.append(";" + deptTargetVo.get(i).getStringValue("id"));
				}
				targetSB.append(";");
				insertSql.putFieldValue("targetlist", targetSB.toString());
				sqlList.add(insertSql);
			}
			UpdateSQLBuilder updatesql = new UpdateSQLBuilder("SAL_TARGET_CHECK_PLAN");
			updatesql.putFieldValue("status", "������"); // ���¼ƻ�״̬��
			updatesql.setWhereCondition(" id = " + planId);
			sqlList.add(updatesql);
			UIUtil.executeBatchByDS(null, sqlList);
			plan_listPanel.refreshCurrSelectedRow();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// ûһ��ָ�꣬��Ҫ�����ֱ��в���ȫ������
	public InsertSQLBuilder getDeptcheckScoreSql(HashVO _targetVO, HashVO _planVO) throws Exception {
		InsertSQLBuilder sql = new InsertSQLBuilder("sal_dept_check_score");
		sql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "s_sal_dept_check_score"));
		sql.putFieldValue("planid", UIUtil.getSequenceNextValByDS(null, "s_sal_dept_check_score"));
		sql.putFieldValue("checkid", UIUtil.getSequenceNextValByDS(null, "s_sal_dept_check_score"));
		sql.putFieldValue("checkdate", UIUtil.getSequenceNextValByDS(null, "s_sal_dept_check_score"));
		sql.putFieldValue("checkdept", UIUtil.getSequenceNextValByDS(null, "s_sal_dept_check_score"));
		sql.putFieldValue("targetid", UIUtil.getSequenceNextValByDS(null, "s_sal_dept_check_score"));
		sql.putFieldValue("targetname", UIUtil.getSequenceNextValByDS(null, "s_sal_dept_check_score"));
		sql.putFieldValue("targetdefine", UIUtil.getSequenceNextValByDS(null, "s_sal_dept_check_score"));
		sql.putFieldValue("evalstandard", UIUtil.getSequenceNextValByDS(null, "s_sal_dept_check_score"));
		sql.putFieldValue("weights", UIUtil.getSequenceNextValByDS(null, "s_sal_dept_check_score"));
		sql.putFieldValue("scoreuser", UIUtil.getSequenceNextValByDS(null, "s_sal_dept_check_score"));
		sql.putFieldValue("status", "δ����");
		return sql;
	}

}
