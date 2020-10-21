package com.pushworld.ipushgrc.ui.cmpevent.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.to.common.TBUtil;
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

import com.pushworld.ipushgrc.ui.cmpcheck.p030.GeneralInsertIntoRiskEval;

/**
 * �ɹ������༭!!!
 * 
 * @author xch
 * 
 */
public class CmpWardEditWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel billList; //
	private WLTButton btn_insert, btn_update, btn_delete;
	private TBUtil tbutil = new TBUtil();
	private BillCardPanel cardPanel = null;

	private String userName = ClientEnvironment.getCurrLoginUserVO().getName(); // ��½��Ա����

	@Override
	public void initialize() {
		billList = new BillListPanel("CMP_WARD_CODE1"); //
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); //
		btn_insert.addActionListener(this);
		btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //
		btn_update.addActionListener(this);
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
		btn_delete.addActionListener(this);
		WLTButton btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //
		billList.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_list }); //
		billList.repaintBillListButton(); // ˢ�°�ť!!!
		this.add(billList); //
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_insert) {
			onInsert();
		} else if (obj == btn_update) {
			onUpdate();
		} else if (obj == btn_delete) {
			onDelete();
		}
	}

	/*
	 * ����
	 */
	public void onInsert() {
		cardPanel = new BillCardPanel("CMP_WARD_CODE1");
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		BillCardDialog dialog = new BillCardDialog(this, "����", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);

		cardPanel.setEditable("refrisks", false);
		dialog.setVisible(true);
		BillVO vo = cardPanel.getBillVO();
		if (dialog.getCloseType() != 1) { // �������û�б��� ��ô��Ҫɾ�����ӱ��е�����
			List deleteSQL = new ArrayList();
			if (vo.getStringValue("wardcust") != null && !vo.getStringValue("wardcust").equals("")) {
				String inCondition = tbutil.getInCondition(vo.getStringValue("wardcust"));
				deleteSQL.add("delete from cmp_wardevent_cust where id in (" + inCondition + ")"); // ɾ���漰�Ŀͻ��ӱ�����
			}
			if (vo.getStringValue("warduser") != null && !vo.getStringValue("warduser").equals("")) {
				String inCondition = tbutil.getInCondition(vo.getStringValue("warduser"));
				deleteSQL.add("delete from cmp_wardevent_user where id in (" + inCondition + ")"); // ɾ���漰��Ա���ӱ�����
			}
			if (deleteSQL.size() > 0) {
				try {
					UIUtil.executeBatchByDS(null, deleteSQL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			int rownum = billList.getRowCount();
			billList.insertEmptyRow(rownum);
			billList.setBillVOAt(rownum, vo);
			billList.setSelectedRow(rownum);
			/* * ���������������� * */
			//��billList���ι�ȥ,�����ڡ������������롱�Ի���ĸ����ڣ��Ա���ĳЩ���⣬���GeneralInsertIntoRiskEval��[YangQing/2013-09-18]
			new GeneralInsertIntoRiskEval(billList, cardPanel, cardPanel.getTempletVO().getTablename(), "¼��");
		}
	}

	/*
	 * ����
	 */
	public void onUpdate() {
		BillVO selectVO = billList.getSelectedBillVO();
		if (selectVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel("CMP_WARD_CODE1");
		cardPanel.setBillVO(selectVO);
		cardPanel.setEditableByEditInit();
		BillCardDialog dialog = new BillCardDialog(this, "�޸�", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		if (selectVO.getStringValue("cmp_cmpfile_id") == null || selectVO.getStringValue("cmp_cmpfile_id").equals("")) {
			cardPanel.setEditable("refrisks", false);
		}
		dialog.setVisible(true);
		if (dialog.getCloseType() != 1) {
			return;
		} else {
			//��billList���ι�ȥ,�����ڡ������������롱�Ի���ĸ����ڣ��Ա���ĳЩ���⣬���GeneralInsertIntoRiskEval��[YangQing/2013-09-18]
			new GeneralInsertIntoRiskEval(billList, cardPanel, cardPanel.getTempletVO().getTablename(), "�޸�");
		}
		billList.refreshCurrSelectedRow();
	}

	/*
	 * ɾ��
	 */
	public void onDelete() {
		BillVO selectVO = billList.getSelectedBillVO();
		if (selectVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (!MessageBox.confirmDel(this)) {
			return;
		}
		List deleteSQL = new ArrayList();
		if (selectVO.getStringValue("wardcust") != null && !selectVO.getStringValue("wardcust").equals("")) {
			String inCondition = tbutil.getInCondition(selectVO.getStringValue("wardcust"));
			deleteSQL.add("delete from cmp_wardevent_cust where id in (" + inCondition + ")"); // ɾ���漰�Ŀͻ��ӱ�����
		}
		if (selectVO.getStringValue("warduser") != null && !selectVO.getStringValue("warduser").equals("")) {
			String inCondition = tbutil.getInCondition(selectVO.getStringValue("warduser"));
			deleteSQL.add("delete from cmp_wardevent_user where id in (" + inCondition + ")"); // ɾ���漰��Ա���ӱ�����
		}
		deleteSQL.add("delete from cmp_ward where id = '" + selectVO.getStringValue("id") + "'");
		if (deleteSQL.size() > 0) {
			try {
				UIUtil.executeBatchByDS(null, deleteSQL);
				billList.removeSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
