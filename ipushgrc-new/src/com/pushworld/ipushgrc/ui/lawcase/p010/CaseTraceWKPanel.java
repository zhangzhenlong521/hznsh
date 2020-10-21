package com.pushworld.ipushgrc.ui.lawcase.p010;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class CaseTraceWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillListPanel billList = null; //
	WLTButton arbitratebtn = new WLTButton("仲裁");
	WLTButton firstjudgebtn = new WLTButton("一审");
	WLTButton firstagainjudgebtn = new WLTButton("一审再审");
	WLTButton repeatejudgebtn = new WLTButton("重审");
	WLTButton secondjudgebtn = new WLTButton("二审");
	WLTButton secondagainjudgebtn = new WLTButton("二审再审");

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billList = new BillListPanel("LBS_CASE_CODE1"); //
		billList.setDataFilterCustCondition("REPLYSTATE=3");
		billList.setItemVisible("REPLYSTATE", false);
		billList.addBatchBillListButton(new WLTButton[] {arbitratebtn, firstjudgebtn, firstagainjudgebtn, repeatejudgebtn, secondjudgebtn, secondagainjudgebtn });
		arbitratebtn.addActionListener(this);
		firstjudgebtn.addActionListener(this);
		firstagainjudgebtn.addActionListener(this);
		repeatejudgebtn.addActionListener(this);
		secondjudgebtn.addActionListener(this);
		secondagainjudgebtn.addActionListener(this);
		billList.repaintBillListButton();
		this.add(billList, BorderLayout.CENTER); //
	}

	public void actionPerformed(ActionEvent e) {
		String caseid = getSeclectedCaseId();
		if (caseid == null)
			return;
		if (e.getSource() == arbitratebtn) {
			arbitrateEdit(caseid);
		} else if (e.getSource() == firstjudgebtn) {
			judgeEdit(caseid, "一审");

		} else if (e.getSource() == firstagainjudgebtn) {
			judgeEdit(caseid, "一审再审");
		} else if (e.getSource() == repeatejudgebtn) {
			judgeEdit(caseid, "重审");
		} else if (e.getSource() == secondjudgebtn) {
			judgeEdit(caseid, "二审");
		} else if (e.getSource() == secondagainjudgebtn) {
			judgeEdit(caseid, "二审再审");
		}

	}

	private String getSeclectedCaseId() {
		BillVO selbillvo = billList.getSelectedBillVO();
		if (selbillvo == null) {
			MessageBox.show(this, "请先选择一个案件！");
			return null;
		} else {
			String case_id = selbillvo.getStringValue("id");
			return case_id;
		}
	}
	
	/**
	 * 仲裁
	 * @param caseid
	 */
	private void arbitrateEdit(String caseid) {
		String[][] data = null;
		try {
			data = UIUtil.getStringArrayByDS(null, "select *  from lbs_arbitrate where ACCUSECASE_ID="+caseid);
		} catch (WLTRemoteException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String type="仲裁";
		BillCardPanel panel = new BillCardPanel("LBS_ARBITRATE_CODE1");
		BillCardDialog dia = null;
		if (data.length == 0) {
			panel.insertRow();
			panel.setValueAt("ACCUSECASE_ID", new StringItemVO(caseid));
			panel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
			dia = new BillCardDialog(this, type + "录入", panel, WLTConstants.BILLDATAEDITSTATE_INSERT);
			dia.setVisible(true);
		} else {
			panel.queryDataByCondition("ACCUSECASE_ID=" + caseid);
			panel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
			dia = new BillCardDialog(this, type + "维护", panel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
			dia.setVisible(true);
		}
		if (dia.getCloseType() == 1) {//更新案件的判决结果
			BillVO billvo = dia.getBillVO();
			String carddate = billvo.getStringValue("judgedate");
			String oriJudgeDate = null;
			try {
				oriJudgeDate = UIUtil.getStringValueByDS(null, "select judgedate from LBS_CASE where id=" + caseid);
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (oriJudgeDate == null || oriJudgeDate.trim().equals("") || carddate.compareTo(oriJudgeDate) > 0) {
				String newState = "仲裁";
				String newJudgeDate = null;
				String newJudgeResult = null;
				String newJudgeResultDesc = null;
				String newJudgeResultFile = null;
				newJudgeDate = carddate;
				String newBankSaving = null;
				String newMovablePro = null;
				String newUnMovablePro = null;
				String newOtherPro = null;
				RefItemVO refitem=billvo.getRefItemVOValue("RESULTFILE");
				if(refitem!=null){
				newJudgeResultFile=refitem.getId();
				}
				newJudgeResult = billvo.getStringValue("RESULT", "");
				newJudgeResultDesc = billvo.getStringValue("RESULTDESC", "");
				newBankSaving = billvo.getStringValue("SAVEBANKSAVING","");
				newMovablePro = billvo.getStringValue("MOVEPROPERTY","");
				newUnMovablePro = billvo.getStringValue("UNMOVEPROPERTY","");
				newOtherPro = billvo.getStringValue("OTHERPROPERTY","");
				try {
					StringBuffer sb = new StringBuffer();
					sb.append("update LBS_CASE  set  STATE='" + newState);
					sb.append("',judgedate='" + newJudgeDate);
					sb.append("',JUDGERESULT='" + newJudgeResult);
					sb.append("',JUDGERESULTDESC='" + newJudgeResultDesc);
					if(newJudgeResultFile!=null){
						sb.append("',JUDGERESULTFILE='"+newJudgeResultFile);
					}
					sb.append("',SAVEBANKSAVING='"+newBankSaving);
					sb.append("',MOVEPROPERTY='"+newMovablePro);
					sb.append("',UNMOVEPROPERTY='"+newUnMovablePro);
					sb.append("',OTHERPROPERTY='"+newOtherPro);
					sb.append("' where id=" + caseid);
					UIUtil.executeBatchByDS(null, new String[] { sb.toString() });
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			billList.refreshData();
		}
	}
	private void judgeEdit(String caseid, String type) {
		String[][] data = null;
		try {
			data = UIUtil.getStringArrayByDS(null, "select *  from lbs_judge where ACCUSECASE_ID=" + caseid + "and type='" + type + "'");
		} catch (WLTRemoteException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		BillCardDialog dia =null;
		if (data.length == 0) {
			BillCardPanel panel = new BillCardPanel("LBS_JUDGE_CODE1");
			panel.insertRow();
			panel.setValueAt("type", new StringItemVO(type));
			panel.setValueAt("ACCUSECASE_ID", new StringItemVO(caseid));
			panel.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
			dia= new BillCardDialog(this, type + "录入", panel, WLTConstants.BILLDATAEDITSTATE_INSERT);
			dia.setVisible(true);
		} else {
			BillCardPanel panel = new BillCardPanel("LBS_JUDGE_CODE1");
			panel.queryDataByCondition("ACCUSECASE_ID=" + caseid + "and type='" + type + "'");
			panel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
			dia = new BillCardDialog(this, type + "维护", panel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
			dia.setVisible(true);
		}
		
		if (dia.getCloseType() == 1) {//更新案件的判决结果
			BillVO billvo = dia.getBillVO();
			String carddate = billvo.getStringValue("judgedate");
			String oriJudgeDate = null;
			try {
				oriJudgeDate = UIUtil.getStringValueByDS(null, "select judgedate from LBS_CASE where id=" + caseid);
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (oriJudgeDate == null || oriJudgeDate.trim().equals("")||carddate.compareTo(oriJudgeDate)>0) {
				String newState=null;
				String newJudgeDate = null;
				String newJudgeResult = null;
				String newJudgeResultDesc = null;
				String newJudgeResultFile = null;
				String newBankSaving = null;
				String newMovablePro = null;
				String newUnMovablePro = null;
				String newOtherPro = null;
				newState=billvo.getStringValue("type","");
				newJudgeDate = carddate;
				RefItemVO refitem=billvo.getRefItemVOValue("JUDGERESULTFILE");
				if(refitem!=null){
				newJudgeResultFile=refitem.getId();
				}
				newJudgeResult = billvo.getStringValue("JUDGERESULT","");
				newJudgeResultDesc = billvo.getStringValue("JUDGERESULTDESC","");
				newBankSaving = billvo.getStringValue("SAVEBANKSAVING","");
				newMovablePro = billvo.getStringValue("MOVEPROPERTY","");
				newUnMovablePro = billvo.getStringValue("UNMOVEPROPERTY","");
				newOtherPro = billvo.getStringValue("OTHERPROPERTY","");
				try {
					StringBuffer sb=new StringBuffer();
					sb.append("update LBS_CASE  set  STATE='"+newState);
					sb.append("',judgedate='"+newJudgeDate);
					sb.append("',JUDGERESULT='"+newJudgeResult);
					sb.append("',JUDGERESULTDESC='"+newJudgeResultDesc);
					sb.append("',SAVEBANKSAVING='"+newBankSaving);
					if(newJudgeResultFile!=null){
					sb.append("',JUDGERESULTFILE='"+newJudgeResultFile);
					}
					sb.append("',MOVEPROPERTY='"+newMovablePro);
					sb.append("',UNMOVEPROPERTY='"+newUnMovablePro);
					sb.append("',OTHERPROPERTY='"+newOtherPro);
					sb.append("' where id="+caseid);
					UIUtil.executeBatchByDS(null, new String[] { sb.toString() });
				} catch (Exception e) {
					e.printStackTrace();
				}
				billList.refreshData();
			}
		}
	}
}
