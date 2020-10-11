package cn.com.pushworld.salary.ui.targetcheck;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ButtonDefineVO;
import cn.com.infostrategy.to.mdata.DeleteSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
/**
 * ���ż�Ч�ƻ��ƶ�
 * @author Administrator
 *
 */
public class DeptTargetSetWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillListPanel plan_list = null;
	private BillCellPanel bc = null;
	private WLTButton create_plan, edit_plan, del_plan, watch_plan, submit_plan, target_set = null;
	private WLTSplitPane wsp = null;
	public void initialize() {
		plan_list = new BillListPanel("SAL_TARGET_PLAN_CODE1");
//		target_list = new BillListPanel("SAL_TARGET_PLAN_ITEM_CODE1");
		initButton();
//		wsp = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, plan_list, getCellPanel());
//		wsp.setDividerLocation(250);
		this.add(plan_list, BorderLayout.CENTER);
	}
	
	public JPanel getCellPanel() {
		JPanel jp = new JPanel();
		jp.setLayout(new BorderLayout());
		bc = new BillCellPanel();
		bc.setToolBarVisiable(false);
		bc.setAllowShowPopMenu(false);
		bc.setEditable(false);
		bc.setOpaque(false);
		jp.add(bc, BorderLayout.CENTER);
		return jp;
	}
	
	public void initButton() {
		ButtonDefineVO btnDefineVo = new ButtonDefineVO("�ƶ��ƻ�");
		btnDefineVo.setBtntype(WLTButton.LIST_POPINSERT);
		btnDefineVo.setBtntext("�ƶ��ƻ�");
		btnDefineVo.setBtnimg("zt_045.gif");
		btnDefineVo.setBtndescr("�ƶ��ƻ�");
		create_plan = new WLTButton(btnDefineVo);
		target_set = new WLTButton("ָ������", "folder_star.png");
		target_set.setToolTipText("ѡ�мƻ�����ָ������");
		edit_plan = new WLTButton("�޸ļƻ�", "cog_edit.png");
		del_plan = new WLTButton("ɾ���ƻ�", "del.gif");
		submit_plan = new WLTButton("���üƻ�", "zt_028.gif");
		watch_plan = new WLTButton("�ƻ�����", "zt_010.gif");
		del_plan.addActionListener(this);
		submit_plan.addActionListener(this);
		watch_plan.addActionListener(this);
		target_set.addActionListener(this);
		edit_plan.addActionListener(this);
		plan_list.addBatchBillListButton(new WLTButton[]{create_plan, target_set, edit_plan, del_plan, watch_plan, submit_plan});
		plan_list.repaintBillListButton();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == target_set) {
			onTargetSet();
		} else if (e.getSource() == del_plan) {
			onDelPlan();
		} else if (e.getSource() == submit_plan) {
			onSubmitPlan();
		} else if (e.getSource() == watch_plan) {
			onWatchPlan();
		} else if (e.getSource() == edit_plan) {
			onEdit();
		}
	}

	private void onWatchPlan() {
		if (checkSelectOne()) {
			try {
				BillVO vo = plan_list.getSelectedBillVO();
				HashMap _parMap = new HashMap();
				_parMap.put("planid", vo.getStringValue("id"));
				HashMap rtn = UIUtil.commMethod("cn.com.pushworld.salary.bs.targetcheck.TargetCheckTool", "getTargetCheckPlanCellVO", _parMap);
				BillCellVO vo_ = (BillCellVO)rtn.get("vo");
				BillCellPanel cp = new BillCellPanel();
				cp.loadBillCellData(vo_);
				cp.setToolBarVisiable(false);
				cp.setAllowShowPopMenu(false);
				cp.setEditable(false);
				cp.setOpaque(false);
				BillDialog bd = new BillDialog(plan_list);
				bd.setTitle("�ƻ�����");
				bd.addConfirmButtonPanel(2);
				bd.add(cp, BorderLayout.CENTER);
				bd.setSize(800, 600);
				bd.locationToCenterPosition();
				bd.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void onEdit() {
		if (checkSelectOne()) {
			BillVO billVO = plan_list.getSelectedBillVO();
//			if ("������".equals(billVO.getStringValue("status"))) {
//				MessageBox.show(plan_list, "�üƻ��Ѿ�����,�޷�ִ�д˲���!");
//				return;
//			}
			BillCardPanel cardPanel = new BillCardPanel(plan_list.templetVO);
			cardPanel.setBillVO(billVO);
			BillCardDialog dialog = new BillCardDialog(plan_list, plan_list.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
			dialog.setVisible(true);
			if (dialog.getCloseType() == 1) {
				plan_list.setBillVOAt(plan_list.getSelectedRow(), dialog.getBillVO(), false); //
				plan_list.setRowStatusAs(plan_list.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			}
		}
	}

	private void onSubmitPlan() {
		if (checkSelectOne()) {
			BillVO vo = plan_list.getSelectedBillVO();
//			if ("������".equals(vo.getStringValue("status"))) {
//				MessageBox.show(plan_list, "�üƻ��Ѿ�����,����ִ�д˲���!");
//				return;
//			}
			if (MessageBox.showConfirmDialog(plan_list, "���ú��޷�����ָ�������Լ�ɾ������,��ȷ�����øüƻ���?", "����", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				UpdateSQLBuilder usb = new UpdateSQLBuilder(plan_list.getTempletVO().getSavedtablename());
				usb.putFieldValue("status", "������");
				usb.setWhereCondition(vo.getPkName() + "=" + vo.getPkValue());
				try {
					UIUtil.executeUpdateByDS(null, usb);
					MessageBox.show(plan_list, "�����ɹ�!");
					plan_list.refreshCurrSelectedRow();
				} catch (Exception e) {
					e.printStackTrace();
					MessageBox.show(plan_list, "����ʧ��!���ٴγ��Ի������Ա��ϵ!");
				}
			}
		}
	}

	private void onDelPlan() {
		if (checkSelectOne()) {
			BillVO vo = plan_list.getSelectedBillVO();
//			if ("������".equals(vo.getStringValue("status"))) {
//				MessageBox.show(plan_list, "�üƻ��Ѿ�����,�޷�ִ�д˲���!");
//				return;
//			}
			if (MessageBox.showConfirmDialog(plan_list, "��ȷ��ɾ���üƻ���?", "����", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				DeleteSQLBuilder dsb = new DeleteSQLBuilder(plan_list.getTempletVO().getSavedtablename());
				dsb.setWhereCondition(vo.getPkName() + "=" + vo.getPkValue());
				DeleteSQLBuilder dsb2 = new DeleteSQLBuilder("sal_target_plan_item");
				dsb2.setWhereCondition("planid=" + vo.getPkValue());
				DeleteSQLBuilder dsb3 = new DeleteSQLBuilder("sal_target_plan_score");
				dsb3.setWhereCondition("planitemid in (select id from sal_target_plan_item where planid=" + vo.getPkValue() + ")");
				try {
					UIUtil.executeBatchByDS(null, new String[]{dsb.getSQL(), dsb2.getSQL(), dsb3.getSQL()});
					MessageBox.show(plan_list, "�����ɹ�!");
					plan_list.removeSelectedRow();
				} catch (Exception e) {
					e.printStackTrace();
					MessageBox.show(plan_list, "����ʧ��!���ٴγ��Ի������Ա��ϵ!");
				}
			}
		}
	}

	private void onTargetSet() {
		if (checkSelectOne()) {
//			BillVO vo = plan_list.getSelectedBillVO();
//			if ("������".equals(vo.getStringValue("status"))) {
//				MessageBox.show(plan_list, "�üƻ��Ѿ�����,�޷�ִ�д˲���!");
//				return;
//			}
			DeptTargetSetDialog dtsd = new DeptTargetSetDialog(plan_list, plan_list.getSelectedBillVO());
			dtsd.setVisible(true);
		}
	}
	
	private boolean checkSelectOne() {
		BillVO vo = plan_list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(plan_list);
			return false;
		}
		return true;
	}

}
