package cn.com.pushworld.salary.ui.targetcheck;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * ȫ�мƻ����ú�ά�����Ŷ�Ӧ��ָ�ꡣ����һ���ɹ���Ա�����������ض���ɫ��
 * 
 * @author haoming create by 2013-7-5
 */
public class DeptTargetCheckSetWKPanel extends AbstractWorkPanel implements ActionListener, BillTreeSelectListener {
	private static final long serialVersionUID = -6342742052574757603L;
	private BillTreePanel deptTreePanel;
	private WLTSplitPane left_right_splitPane;
	private BillListPanel checklistPanel, targetListPanel; // ���˲����б������˲���ָ���б�
	private WLTButton btn_check_dept_import, btn_check_dept_reomve, btn_score_import, btn_score_remove, btn_target_import, btn_target_remove;
	private WLTTabbedPane tabedPane = new WLTTabbedPane();

	@Override
	public void initialize() {
		deptTreePanel = new BillTreePanel("PUB_CORP_DEPT_SELF");
		deptTreePanel.addBillTreeSelectListener(this);
		deptTreePanel.queryDataByCondition(null);

		targetListPanel = new BillListPanel("SAL_TARGET_LIST_CODE1"); // �ò���Ҫ���˵�ָ�ꡣ����ָ�����ز��źͿ����ˣ��Զ����� �����˼�¼
		btn_target_import = new WLTButton("��ӿ���ָ��");
		btn_target_remove = new WLTButton("�ӿ������Ƴ�");
		btn_target_import.addActionListener(this);
		btn_target_remove.addActionListener(this);
		targetListPanel.addBatchBillListButton(new WLTButton[] { btn_target_import, btn_target_remove });
		targetListPanel.repaintBillListButton();

		checklistPanel = new BillListPanel("PUB_CORP_DEPT_SELF"); // �������� ,ͨ��score��ȥ��������Щ���˻�����distinct��
		btn_check_dept_import = new WLTButton("��ӿ�������");
		btn_check_dept_reomve = new WLTButton("�Ƴ���������");
		btn_check_dept_import.addActionListener(this);
		btn_check_dept_reomve.addActionListener(this);
		checklistPanel.addBatchBillListButton(new WLTButton[] { btn_check_dept_import, btn_check_dept_reomve });
		checklistPanel.repaintBillListButton();

		BillListPanel scoreListPanel = new BillListPanel("SAL_DEPT_CHECK_SCORE_CODE1"); // ����ָ��
		btn_score_import = new WLTButton("���ָ��");
		btn_score_remove = new WLTButton("�Ƴ�ָ��");
		scoreListPanel.addBatchBillListButton(new WLTButton[] { btn_score_import, btn_score_remove });
		scoreListPanel.repaintBillListButton();
		WLTSplitPane up_down_split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, checklistPanel, scoreListPanel);
		tabedPane.addTab("���ο���ָ��", targetListPanel);
		tabedPane.addTab("����Ȩ���趨", up_down_split);
		left_right_splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, deptTreePanel, tabedPane);
		this.add(left_right_splitPane);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_check_dept_import) {
			onDeptImport();
		} else if (e.getSource() == btn_check_dept_reomve) {

		} else if (e.getSource() == btn_target_import) { // ����ָ�굼�뵽�������۳��С�
			onImportTargetToPond();
		} else if (e.getSource() == btn_target_remove) { // ����ָ�굼�뵽�������۳��С�
			onRemoveTargetToPond();
		}
	}

	private void onDeptImport() {
		BillVO checkedDeptVO = deptTreePanel.getSelectedVO();// �����˵Ļ���
		if (checkedDeptVO == null) {
			MessageBox.show(this, "�������ѡ�񱻿��˲���!");
			return;
		}
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent event) {
		BillVO checkedDeptVO = event.getCurrSelectedVO(); // �����˻���
		if (checkedDeptVO == null) {
			return;
		}

		if (tabedPane.getSelectedIndex() == 0) {// ָ��ҳ��
			try {
				HashVO vos[] = UIUtil.getHashVoArrayByDS(null, "select *from sal_dept_check where deptid = '" + checkedDeptVO.getPkValue() + "'");
				if (vos.length > 0) {
					String targets = vos[0].getStringValue("targetlist");
					targetListPanel.QueryDataByCondition(" id in(" + TBUtil.getTBUtil().getInCondition(targets) + ")");
				} else {
					targetListPanel.removeAllRows();
				}
				return;
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else { // ���½ṹ��ָ��Ȩ�ؽ��档
			try {
				// ���˻���
				HashVO checkdept[] = UIUtil.getHashVoArrayByDS(null, "select distinct(scoredeptid) from SAL_DEPT_CHECK_SCORE where checkdept='" + checkedDeptVO.getPkValue() + "'");
				List ids = new ArrayList<String>();
				for (int i = 0; i < checkdept.length; i++) {
					ids.add(checkdept[i].getStringValue("scoredeptid"));
				}
				checklistPanel.QueryDataByCondition(" id in(" + TBUtil.getTBUtil().getInCondition(ids) + ")");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	// ����ָ�굽���ο������У�
	private void onImportTargetToPond() {
		BillVO checkedDeptVO = deptTreePanel.getSelectedVO();// �����˵Ļ���
		if (checkedDeptVO == null) {
			MessageBox.show(this, "�������ѡ�񱻿��˲���!");
			return;
		}
		BillVO allBillVO[] = targetListPanel.getAllBillVOs();
		BillListDialog listDialog = new BillListDialog(this, "ѡ��أز���ָ��", "SAL_TARGET_LIST_CODE1",
				" deptid = " + checkedDeptVO.getPkValue() + " and id not in(" + getInCondition(allBillVO, "id") + ")", 800, 600);
		listDialog.getBilllistPanel().setRowNumberChecked(true);
		listDialog.setVisible(true);
		BillVO retTargetVos[] = listDialog.getReturnBillVOs();
		if (listDialog.getCloseType() == 1 && retTargetVos.length > 0) {
			targetListPanel.addBillVOs(retTargetVos);
			List<String> oldList = getBillVosItemList(allBillVO, "id"); // �ɼ�¼
			List<String> newList = getBillVosItemList(retTargetVos, "id");
			oldList.addAll(newList);
			String targetIDs = getMutilValueStr(oldList); // �õ���ǰ��������ָ�괮��
			UpdateSQLBuilder updateSql = new UpdateSQLBuilder("sal_dept_check");
			updateSql.putFieldValue("targetlist", targetIDs);
			updateSql.setTableName(" deptid = '" + checkedDeptVO.getPkValue() + "' ");
			try {
				UIUtil.executeUpdateByDS(null, updateSql);
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// �ӱ��ο��˳����Ƴ��ü�¼����Ҫ�жϣ��Ƿ���������
	private void onRemoveTargetToPond() {
		// ��Ҫ���ơ�

	}

	// ����billVO��ĳ�еõ�in
	private String getInCondition(BillVO billvo[], String _item) {
		return TBUtil.getTBUtil().getInCondition(getBillVosItemList(billvo, _item));
	}

	// �õ�һ��BIllVOĳ��ֵ�ļ��ϡ�
	private List getBillVosItemList(BillVO[] _vos, String _item) {
		List l = new ArrayList();
		for (int i = 0; i < _vos.length; i++) {
			l.add(_vos[i].getStringValue(_item));
		}
		return l;
	}

	// �õ����ֵ�洢��ʽ;1;3;4;5;
	private String getMutilValueStr(List<String> _list) {
		if (_list == null || _list.size() == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < _list.size(); i++) {
			String id = _list.get(i);
			if (id != null && !id.equals("")) {
				sb.append(";" + id);
			}
		}
		if (sb.length() > 0) {
			sb.append(";"); // ��β
			return sb.toString();
		}
		return "";
	}
}
