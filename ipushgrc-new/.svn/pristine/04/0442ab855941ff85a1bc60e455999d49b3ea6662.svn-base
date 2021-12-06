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
 * 违规事件编辑!!!
 * 
 * @author xch
 * 
 */
public class CmpEventEditWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private BillListPanel billList; //
	private WLTButton btn_insert, btn_update, btn_delete;
	private TBUtil tbutil = new TBUtil();
	private String userName = ClientEnvironment.getCurrLoginUserVO().getName(); // 登陆人员名称
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
		billList.repaintBillListButton(); // 刷新按钮!!!
		this.add(billList); //
		//增加删除限制，由平台参数配置，必须在billlist.repaintBillListButton()之后设置，否则不起作用【李春娟/2015-12-30】
		if (btn_delete != null &&TBUtil.getTBUtil().getSysOptionBooleanValue("是否只有系统管理员可删除", false) && (!"admin".equals(ClientEnvironment.getCurrLoginUserVO().getCode()) || !new TBUtil().isExistInArray("总行系统管理员", ClientEnvironment.getCurrLoginUserVO().getAllRoleCodes()))) {
			btn_delete.setToolTipText("只有系统管理员可删除");
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

	//出于权限考虑不应该在这删除, 应该先去删除方案, 再回来删除事件! Gwang 
	//	/**
	//	 * 同时删除违规事件和整改方案
	//	 */
	//	private void deleteAll() {
	//		BillVO selectVO = billList.getSelectedBillVO();
	//		if (selectVO == null) {
	//			MessageBox.showSelectOne(this);
	//			return;
	//		}
	//		if (!MessageBox.confirm(this, "确定删除此条记录以及相关信息吗?")) {
	//			return;
	//		}
	//		List<String> deleteSQL = new ArrayList<String>();
	//		if (selectVO.getStringValue("wardcust") != null && !selectVO.getStringValue("wardcust").equals("")) {
	//			String inCondition = tbutil.getInCondition(selectVO.getStringValue("wardcust"));
	//			deleteSQL.add("delete from cmp_wardevent_cust where id in (" + inCondition + ")"); // 删除涉及的客户子表数据
	//		}
	//		if (selectVO.getStringValue("warduser") != null && !selectVO.getStringValue("warduser").equals("")) {
	//			String inCondition = tbutil.getInCondition(selectVO.getStringValue("warduser"));
	//			deleteSQL.add("delete from cmp_wardevent_user where id in (" + inCondition + ")"); // 删除涉及的员工子表数据
	//		}
	//		String eventid = selectVO.getStringValue("id");
	//		deleteSQL.add("delete from cmp_event where id = '" + eventid + "'");//删除违规事件
	//		String sql = "delete from CMP_EVENT_ADJUSTSTEP where PROJECTID = (select id from CMP_EVENT_ADJUSTPROJECT where eventid="+eventid+")";//删除正改措施
	//		String sql1 = "delete from CMP_EVENT_TRACK where PROJECTID = (select id from CMP_EVENT_ADJUSTPROJECT where eventid="+eventid+")";//删除整改方案的跟踪信息
	////		deleteSQL.add("delete from CMP_EVENT_ADJUSTPROJECT where eventid=" + eventid);//删除整改方案
	//		deleteSQL.add(sql);
	//		deleteSQL.add(sql1);
	//		if (deleteSQL.size() > 0) {
	//			try {
	//				UIUtil.executeBatchByDS(null, deleteSQL);
	//				UIUtil.executeUpdateByDS(null, "delete from CMP_EVENT_ADJUSTPROJECT where eventid=" + eventid);//删除整改方案
	//				billList.removeSelectedRow();
	//			} catch (Exception e) {
	//				e.printStackTrace();
	//			}
	//		}
	//	}

	/*
	 * 新增
	 */
	public void onInsert() {
		BillCardPanel cardPanel = new BillCardPanel(tempetCode);
		cardPanel.insertRow();
		cardPanel.setGroupVisiable("整改情况", false);
		cardPanel.setGroupVisiable("整改状态", false);
		cardPanel.setEditableByInsertInit();
		BillCardDialog dialog = new BillCardDialog(this, "新增", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardPanel.setEditable("refrisks", false);
		dialog.setVisible(true);
		BillVO vo = cardPanel.getBillVO();
		if (dialog.getCloseType() != 1) { // 如果数据没有保存 那么需要删除掉子表中的数据
			List deleteSQL = new ArrayList();
			if (vo.getStringValue("wardcust") != null && !vo.getStringValue("wardcust").equals("")) {
				String inCondition = tbutil.getInCondition(vo.getStringValue("wardcust"));
				deleteSQL.add("delete from cmp_wardevent_cust where id in (" + inCondition + ")"); // 删除涉及的客户子表数据
			}
			if (vo.getStringValue("warduser") != null && !vo.getStringValue("warduser").equals("")) {
				String inCondition = tbutil.getInCondition(vo.getStringValue("warduser"));
				deleteSQL.add("delete from cmp_wardevent_user where id in (" + inCondition + ")"); // 删除涉及的员工子表数据
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
				/* * 插入风险评估申请表 * */
				//将billList传参过去,来用于“风险评估申请”对话框的父窗口，以避免某些问题，详见GeneralInsertIntoRiskEval类[YangQing/2013-09-18]
				new GeneralInsertIntoRiskEval(billList, cardPanel, cardPanel.getTempletVO().getTablename(), "录入");
			}

		}

	}

	/*
	 * 更新
	 */
	public void onUpdate() {
		BillVO selectVO = billList.getSelectedBillVO();
		if (selectVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (isHasAdjustCase(selectVO)) {//有了整改方案
			MessageBox.show(this, "此事件已经有了整改方案，不可修改！");
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(tempetCode);
		cardPanel.setBillVO(selectVO);
		cardPanel.setEditableByEditInit(); // 设置为编辑
		BillCardDialog dialog = new BillCardDialog(this, "修改", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE); // 创建对话框
		if (selectVO.getStringValue("cmp_cmpfile_id") == null || selectVO.getStringValue("cmp_cmpfile_id").equals("")) {
			cardPanel.setEditable("refrisks", false); // 如果流程文件为空，那么风险点不可编辑
		}
		dialog.setVisible(true);
		if (dialog.getCloseType() != 1) {
			return;
		} else {
			new GeneralInsertIntoRiskEval(billList, cardPanel, cardPanel.getTempletVO().getTablename(), "修改");
		}
		billList.refreshCurrSelectedRow();
	}

	/*
	 * 删除
	 */
	public void onDelete() {
		BillVO selectVO = billList.getSelectedBillVO();
		if (selectVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (isHasAdjustCase(selectVO)) {//有了整改方案
			MessageBox.show(this, "此事件已经有了整改方案，不可删除！");
			return;
		}
		if (!MessageBox.confirmDel(this)) {
			return;
		}
		List deleteSQL = new ArrayList();
		if (selectVO.getStringValue("wardcust") != null && !selectVO.getStringValue("wardcust").equals("")) {
			String inCondition = tbutil.getInCondition(selectVO.getStringValue("wardcust"));
			deleteSQL.add("delete from cmp_wardevent_cust where id in (" + inCondition + ")"); // 删除涉及的客户子表数据
		}
		if (selectVO.getStringValue("warduser") != null && !selectVO.getStringValue("warduser").equals("")) {
			String inCondition = tbutil.getInCondition(selectVO.getStringValue("warduser"));
			deleteSQL.add("delete from cmp_wardevent_user where id in (" + inCondition + ")"); // 删除涉及的员工子表数据
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
	 * 查看一个违规事件是否有整改方案
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
