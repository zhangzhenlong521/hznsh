package com.pushworld.ipushgrc.ui.cmpevent.p030;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
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
 * Υ���¼��༭!!!
 * 
 * @author xch
 * 
 */
public class CmpEventEditWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private BillListPanel billList; //
	private WLTButton btn_insert, btn_update, btn_delete;
	private TBUtil tbutil = new TBUtil();
	private String userName = ClientEnvironment.getCurrLoginUserVO().getName(); // ��½��Ա����
	private String tempetCode;

	public void initialize() {
		currInit("CMP_EVENT_CODE1");
	}

	public void currInit(String _templetCode) {
		tempetCode = _templetCode;
		billList = new BillListPanel(tempetCode); //
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); //
		btn_insert.addActionListener(this);
		btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //
		btn_update.addActionListener(this);
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
		btn_delete.addActionListener(this);
		WLTButton btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //z
		billList.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_list }); //
		billList.repaintBillListButton(); // ˢ�°�ť!!!
		this.add(billList); //
		//����ɾ�����ƣ���ƽ̨�������ã�������billlist.repaintBillListButton()֮�����ã����������á����/2015-12-30��
		if (btn_delete != null &&TBUtil.getTBUtil().getSysOptionBooleanValue("�Ƿ�ֻ��ϵͳ����Ա��ɾ��", false) && (!"admin".equals(ClientEnvironment.getCurrLoginUserVO().getCode()) || !new TBUtil().isExistInArray("����ϵͳ����Ա", ClientEnvironment.getCurrLoginUserVO().getAllRoleCodes()))) {
			btn_delete.setToolTipText("ֻ��ϵͳ����Ա��ɾ��");
			btn_delete.setEnabled(false);
		}
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

	//����Ȩ�޿��ǲ�Ӧ������ɾ��, Ӧ����ȥɾ������, �ٻ���ɾ���¼�! Gwang 
	//	/**
	//	 * ͬʱɾ��Υ���¼������ķ���
	//	 */
	//	private void deleteAll() {
	//		BillVO selectVO = billList.getSelectedBillVO();
	//		if (selectVO == null) {
	//			MessageBox.showSelectOne(this);
	//			return;
	//		}
	//		if (!MessageBox.confirm(this, "ȷ��ɾ��������¼�Լ������Ϣ��?")) {
	//			return;
	//		}
	//		List<String> deleteSQL = new ArrayList<String>();
	//		if (selectVO.getStringValue("wardcust") != null && !selectVO.getStringValue("wardcust").equals("")) {
	//			String inCondition = tbutil.getInCondition(selectVO.getStringValue("wardcust"));
	//			deleteSQL.add("delete from cmp_wardevent_cust where id in (" + inCondition + ")"); // ɾ���漰�Ŀͻ��ӱ�����
	//		}
	//		if (selectVO.getStringValue("warduser") != null && !selectVO.getStringValue("warduser").equals("")) {
	//			String inCondition = tbutil.getInCondition(selectVO.getStringValue("warduser"));
	//			deleteSQL.add("delete from cmp_wardevent_user where id in (" + inCondition + ")"); // ɾ���漰��Ա���ӱ�����
	//		}
	//		String eventid = selectVO.getStringValue("id");
	//		deleteSQL.add("delete from cmp_event where id = '" + eventid + "'");//ɾ��Υ���¼�
	//		String sql = "delete from CMP_EVENT_ADJUSTSTEP where PROJECTID = (select id from CMP_EVENT_ADJUSTPROJECT where eventid="+eventid+")";//ɾ�����Ĵ�ʩ
	//		String sql1 = "delete from CMP_EVENT_TRACK where PROJECTID = (select id from CMP_EVENT_ADJUSTPROJECT where eventid="+eventid+")";//ɾ�����ķ����ĸ�����Ϣ
	////		deleteSQL.add("delete from CMP_EVENT_ADJUSTPROJECT where eventid=" + eventid);//ɾ�����ķ���
	//		deleteSQL.add(sql);
	//		deleteSQL.add(sql1);
	//		if (deleteSQL.size() > 0) {
	//			try {
	//				UIUtil.executeBatchByDS(null, deleteSQL);
	//				UIUtil.executeUpdateByDS(null, "delete from CMP_EVENT_ADJUSTPROJECT where eventid=" + eventid);//ɾ�����ķ���
	//				billList.removeSelectedRow();
	//			} catch (Exception e) {
	//				e.printStackTrace();
	//			}
	//		}
	//	}

	/*
	 * ����
	 */
	public void onInsert() {
		BillCardPanel cardPanel = new BillCardPanel(tempetCode);
		cardPanel.insertRow();
		cardPanel.setGroupVisiable("�������", false);
		cardPanel.setGroupVisiable("����״̬", false);
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
			if ("Y".equalsIgnoreCase(vo.getStringValue("ifsubmit"))) {
				/* * ���������������� * */
				//��billList���ι�ȥ,�����ڡ������������롱�Ի���ĸ����ڣ��Ա���ĳЩ���⣬���GeneralInsertIntoRiskEval��[YangQing/2013-09-18]
				new GeneralInsertIntoRiskEval(billList, cardPanel, cardPanel.getTempletVO().getTablename(), "¼��");
			}

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
		if (isHasAdjustCase(selectVO)) {//�������ķ���
			MessageBox.show(this, "���¼��Ѿ��������ķ����������޸ģ�");
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(tempetCode);
		cardPanel.setBillVO(selectVO);
		cardPanel.setEditableByEditInit(); // ����Ϊ�༭
		BillCardDialog dialog = new BillCardDialog(this, "�޸�", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE); // �����Ի���
		if (selectVO.getStringValue("cmp_cmpfile_id") == null || selectVO.getStringValue("cmp_cmpfile_id").equals("")) {
			cardPanel.setEditable("refrisks", false); // ��������ļ�Ϊ�գ���ô���յ㲻�ɱ༭
		}
		dialog.setVisible(true);
		if (dialog.getCloseType() != 1) {
			return;
		} else {
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
		if (isHasAdjustCase(selectVO)) {//�������ķ���
			MessageBox.show(this, "���¼��Ѿ��������ķ���������ɾ����");
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
		deleteSQL.add("delete from cmp_event where id = '" + selectVO.getStringValue("id") + "'");
		if (deleteSQL.size() > 0) {
			try {
				UIUtil.executeBatchByDS(null, deleteSQL);
				billList.removeSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * �鿴һ��Υ���¼��Ƿ������ķ���
	 * @param vo
	 * @return
	 */
	private boolean isHasAdjustCase(BillVO vo) {
		if (vo == null)
			return false;
		String eventid = vo.getStringValue("id");
		String sql = "select * from CMP_EVENT_ADJUSTPROJECT where eventid =" + eventid;
		HashVO[] vos = null;
		try {
			vos = UIUtil.getHashVoArrayByDS(null, sql);
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (vos == null || vos.length == 0)
			return false;
		else
			return true;

	}
}
