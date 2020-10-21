package com.pushworld.ipushlbs.ui.lawyermanage.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * ��ʦ��Ϣά��
 * 
 * @author yinliang
 * @since 2012.01.17
 */
public class LawyerManageWKPanel extends AbstractWorkPanel implements BillTreeSelectListener, ActionListener {

	private static final long serialVersionUID = 1L;
	private WLTSplitPane split; // ��ҳ�����
	private BillTreePanel billTree_dept = null; // ������!!
	private BillCardPanel billCard_dept = null; // ������Ƭ
	private BillListPanel billList_lawyer = null; // ��ʦ��Ϣ�б�
	private WLTButton btn_insert, btn_update, btn_delete, btn_save, lawyer_insert; // ��,ɾ,��
	private BillVO billvo;
	int flag = 0; // ������ʾ��������ӻ����޸İ�ť 1Ϊ���ӣ�2Ϊ�޸�

	@Override
	public void initialize() {
		billTree_dept = new BillTreePanel("LBS_LAWYER_DEPT_CODE1"); // ��������
		billTree_dept.setDragable(true);

		// ���Ӹ�����ť
		btn_insert = new WLTButton("����");
		btn_update = new WLTButton("�޸�");
		btn_delete = new WLTButton("ɾ��");
		btn_insert.addActionListener(this);
		btn_update.addActionListener(this);
		btn_delete.addActionListener(this);
		billTree_dept.addBatchBillTreeButton(new WLTButton[] { btn_insert, btn_update, btn_delete });
		billTree_dept.repaintBillTreeButton();

		billCard_dept = new BillCardPanel("LBS_LAWYER_DEPT_CODE1"); // �Ҳ��ϲ��ֻ�����Ϣ
		btn_save = new WLTButton("����");
		btn_save.addActionListener(this);
		billCard_dept.addBillCardButton(btn_save);
		billCard_dept.repaintBillCardButton();
		btn_save.setEnabled(false);

		billList_lawyer = new BillListPanel("LBS_LAWYER_INFO_CODE1");// �Ҳ��²�����ʦ��Ϣ
		lawyer_insert = billList_lawyer.getBillListBtn("$�б�������");
		lawyer_insert.addActionListener(this);

		billTree_dept.queryDataByCondition(null); // ��ѯ��������,Ҫ��Ȩ�޹���!!
		billTree_dept.addBillTreeSelectListener(this); // ˢ���¼�����!!

		WLTSplitPane split_1 = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billCard_dept, billList_lawyer); // �Ҳ����·ֲ�
		split_1.setDividerLocation(260);

		split = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTree_dept, split_1); // ȫ�����ҷֲ�
		this.add(split); //
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		billvo = billTree_dept.getSelectedVO();
		if (billvo == null) { // ����Ǹ��ڵ㣬��û����Ϣ��
			billCard_dept.clear();
			billCard_dept.setEditable(false);
			billList_lawyer.clearTable();
			return;
		}
		//���水ť������
		btn_save.setEnabled(false);
		// ˢ�»�����Ƭ
		billCard_dept.setBillVO(billvo);
		billCard_dept.setEditable(false);
		// ˢ����ʦ�б�
		billList_lawyer.clearTable();
		billList_lawyer.QueryDataByCondition("PROXY_DEPT='" + billvo.getStringValue("id") + "'");// ������ʦ��
		billList_lawyer.refreshData();

	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_insert) // ����������
			onInsert();
		else if (obj == btn_update) // ���»�����Ϣ
			onUpdate();
		else if (obj == btn_save) // ���������Ϣ
			onSave();
		else if (obj == btn_delete) // ɾ��������Ϣ
			onDelete();
		else if (obj == lawyer_insert) // ������ʦ��Ϣ
			onInsertLawyer();
	}

	// ������ʦ��Ϣ
	private void onInsertLawyer() {
		BillVO billvo = billTree_dept.getSelectedVO();
		if (billvo == null) {
			MessageBox.show(split, "��ѡ��һ��������");
			return;
		}
		// ׼��������ʦ��������Ƭ
		BillCardPanel billcard = new BillCardPanel("LBS_LAWYER_INFO_CODE1");
		billcard.insertRow();
		billcard.setValueAt("PROXY_DEPT", new StringItemVO(billvo.getStringValue("id"))); // ��������ID����ȥ
		BillCardDialog dialog = new BillCardDialog(split, "��ʦ����", billcard, WLTConstants.BILLDATAEDITSTATE_INSERT);
		dialog.setVisible(true);
		if (dialog.getCloseType() == 1) { // �����ǵ��ȷ������,�����ݴ����б�
			int li_newrow = billList_lawyer.newRow(false); //
			billList_lawyer.setBillVOAt(li_newrow, dialog.getBillVO(), false);
			billList_lawyer.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); // �����б���е�����Ϊ��ʼ��״̬.
			billList_lawyer.setSelectedRow(li_newrow);
		}
	}

	// ɾ��������Ϣ
	private void onDelete() {
		BillVO billvo = billTree_dept.getSelectedVO();
		if (billvo == null) {
			MessageBox.show(split, "��ѡ��һ��������");
			return;
		}
		if (MessageBox.showConfirmDialog(split, "ɾ������ͬʱ��ɾ����ʦ��Ϣ��ȷ��ɾ����?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return; //
		}
		// ɾ��sql
		String str_sql = "delete from " + billvo.getSaveTableName() + " where id = " + billvo.getStringValue("id"); //
		String str_sql2 = "delete from LBS_LAWYER_INFO where proxy_dept = '" + billvo.getStringValue("id") + "'";
		List<String> list = new ArrayList<String>();
		list.add(str_sql);
		list.add(str_sql2);
		try {
			UIUtil.executeBatchByDS(null, list);
			billCard_dept.clear(); // ��ջ�����Ƭ��Ϣ
			billList_lawyer.clearTable(); // �����ʦ����Ϣ
			billCard_dept.setEditable(false); // ����Ϊ�����޸�
		} catch (Exception e) {
			e.printStackTrace();
		} //
		billTree_dept.refreshTree();
		billTree_dept.updateUI();
	}

	// ������Ϣ����
	private void onSave() {
		String editState = billCard_dept.getEditState();
		try {
			if ("".equals(billCard_dept.getValueAt("NAME").toString()) || billCard_dept.getValueAt("NAME") == null || "".equals(billCard_dept.getValueAt("REG_ADDRESS").toString()) || billCard_dept.getValueAt("REG_ADDRESS") == null || "".equals(billCard_dept.getValueAt("CON_ADDRESS").toString())
					|| billCard_dept.getValueAt("CON_ADDRESS") == null) {
				MessageBox.show(split, "��������ڿ�ֵ��");
				return;
			}
			if (editState.equals("INIT")) { // ����ǳ�ʼ
				MessageBox.show(split, "��δ�����޸ģ����ɱ��棡");
				return;
			} else if (editState.equals("INSERT")) { // ����ǲ���\�޸�����
				billCard_dept.updateData(); // ��������,������½���insert��������޸���update
				// �Զ��ġ���
				billCard_dept.setEditable(false);
				billTree_dept.addNode(billCard_dept.getBillVO());
			} else if (editState.equals("UPDATE")) {
				billCard_dept.updateData(); // ��������,������½���insert��������޸���update
				// �Զ��ġ���
				billCard_dept.setEditable(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ��������
	private void onUpdate() {
		BillVO billvo = billTree_dept.getSelectedVO();
		if (billvo == null) {
			MessageBox.show(split, "��ѡ��һ��������");
			return;
		}
		btn_save.setEnabled(true);
		billCard_dept.setEditState("UPDATE");
		billCard_dept.setEditable(true);
	}

	// �����½�
	private void onInsert() {
		btn_save.setEnabled(true);
		BillVO billvo = billTree_dept.getSelectedVO();
		billCard_dept.clear();
		billCard_dept.insertRow();
		if (billvo == null) // ���ѡ����Ǹ��ڵ����û��ѡ��
			billCard_dept.setValueAt("parentid", null);
		else
			billCard_dept.setValueAt("parentid", new StringItemVO(billvo.getStringValue("id")));
		billCard_dept.setEditableByInsertInit();
	}

}
