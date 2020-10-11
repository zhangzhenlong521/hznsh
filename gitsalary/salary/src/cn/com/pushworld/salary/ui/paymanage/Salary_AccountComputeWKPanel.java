package cn.com.pushworld.salary.ui.paymanage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * ���ʼ���
 * ѡ��ģ�����¶����ɹ��ʱ�
 * ���ʱ���ģ���Լ����Ӷ���������
 * ����ͨ�����ʱ�Ҫ֪������Щ���Լ�
 * ���ӵ������Ϣ��һ��
 * @author Administrator
 *
 */
public class Salary_AccountComputeWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillListPanel salary_bill_list = null; //���ʵ��б�
	private WLTButton create_sa_bill, open_sa_bill, del_sa_bill, submit_sa_bill,submit_sa_end = null;
	private BillCardDialog dialog = null;
	private SalaryServiceIfc service;
	private String checkdate=null;

	public void initialize() {
		salary_bill_list = new BillListPanel("SAL_SALARYBILL_CODE1");
		addBillListButton();
		this.add(salary_bill_list, BorderLayout.CENTER);
	}

	public void addBillListButton() {
		create_sa_bill = new WLTButton("���ɹ��ʵ�", "zt_028.gif");
		open_sa_bill = new WLTButton("�鿴���ʵ�", "zt_010.gif");
		del_sa_bill = new WLTButton("ɾ�����ʵ�", "zt_031.gif");
		submit_sa_bill = new WLTButton("���Ź��ʵ�", "office_082.gif");
		submit_sa_end = new WLTButton("�رչ��ʵ�", "office_166.gif");
		create_sa_bill.addActionListener(this);
		open_sa_bill.addActionListener(this);
		del_sa_bill.addActionListener(this);
		submit_sa_bill.addActionListener(this);
		submit_sa_end.addActionListener(this);
		salary_bill_list.addBatchBillListButton(new WLTButton[] { create_sa_bill, open_sa_bill, del_sa_bill, submit_sa_bill,submit_sa_end });
		salary_bill_list.repaintBillListButton();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == create_sa_bill) {
			onCreate();
		} else if (e.getSource() == open_sa_bill) {
			onOpen();
		} else if (e.getSource() == del_sa_bill) {
			onDel();
		} else if (e.getSource() == submit_sa_bill) {
			onSubmit();
		}else if(e.getSource() == submit_sa_end){
			onend();
		}
	}

	public void onCreate() {
		final BillListDialog bld = new BillListDialog(this, "ѡ��������", "SAL_ACCOUNT_SET_CODE1", 800, 600);
		bld.setVisible(true);
		if (bld.getCloseType() == BillListDialog.CONFIRM) {
			BillCardPanel cardPanel = new BillCardPanel(salary_bill_list.templetVO);
			cardPanel.insertRow();
			cardPanel.setEditableByInsertInit();
			String checkDate = new SalaryUIUtil().getCheckDate();
			cardPanel.setValueAt("monthly", checkDate);
			final String accountid = bld.getReturnBillVOs()[0].getPkValue();
			final String name = bld.getBilllistPanel().getSelectedBillVO().getStringValue("name");
			cardPanel.setValueAt("sal_account_setid", new RefItemVO(accountid, "", bld.getReturnBillVOs()[0].getRealValue("name")));
			dialog = new BillCardDialog(this, salary_bill_list.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT) {
				public void onConfirm() {
					checkdate = billcardPanel.getBillVO().getStringValue("monthly");
					if(name.contains("����")){//zzl �����Ļ������ʲ��ü���
						new SplashWindow(this, "���ʵ�������,���Ժ�...", new AbstractAction() {
							public void actionPerformed(final ActionEvent arg0) {
								Timer timer = new Timer();
								try {
									timer.schedule(new TimerTask() {
										SplashWindow w = (SplashWindow) arg0.getSource();

										public void run() { // ��Ҫ���߳��������Ƿ��Ѿ������ݼ��ؽ����ˡ�
											String schedule = null;
											Object obj[] = null;
											try {
												obj = (Object[]) getService().getRemoteActionSchedule("", "���ʼ���");
											} catch (Exception e) {
												e.printStackTrace();
												this.cancel();
											}
											if (obj != null) {
												schedule = (String) obj[0];
												w.setWaitInfo(schedule);
											} else {
												w.setWaitInfo("ϵͳ����Ŭ��������...");
											}
										}
									}, 20, 1000);

									// ���빤�ʱ������
									List sql = new ArrayList();
									sql.add(billcardPanel.getInsertSQL());
									// ���빤�ʱ������ӵĹ�ϵ��
									BillVO vo = billcardPanel.getBillVO();
									SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
									ifc.onCreateSalaryBill(sql, vo.getPkValue(), accountid, checkdate,vo);
									timer.cancel();
									billVO = billcardPanel.getBillVO();
									closeType = 1;
									dialog.dispose();
								} catch (Exception e) {
									e.printStackTrace();
									timer.cancel();
									MessageBox.show(billcardPanel, "���ɹ��ʵ������쳣���ٴγ��Ի������Ա��ϵ!");
									closeType = -1;
									return;
								}
							}
						}, false);
		
					}else{
						billcardPanel.stopEditing();
						if (!billcardPanel.newCheckValidate("submit")) {
							return;
						}
						try {
							String status = UIUtil.getStringValueByDS(null, "select status from sal_target_check_log where checkdate='" + checkdate + "'");
							if (!"���˽���".equals(status)) {
								MessageBox.show(dialog, "���Ƚ���" + checkdate + "�·ݵĿ���!");
								return;
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					    new SplashWindow(this, "���ʵ�������,���Ժ�...", new AbstractAction() {
						public void actionPerformed(final ActionEvent arg0) {
							Timer timer = new Timer();
							try {
								timer.schedule(new TimerTask() {
									SplashWindow w = (SplashWindow) arg0.getSource();

									public void run() { // ��Ҫ���߳��������Ƿ��Ѿ������ݼ��ؽ����ˡ�
										String schedule = null;
										Object obj[] = null;
										try {
											obj = (Object[]) getService().getRemoteActionSchedule("", "���ʼ���");
										} catch (Exception e) {
											e.printStackTrace();
											this.cancel();
										}
										if (obj != null) {
											schedule = (String) obj[0];
											w.setWaitInfo(schedule);
										} else {
											w.setWaitInfo("ϵͳ����Ŭ��������...");
										}
									}
								}, 20, 1000);

								// ���빤�ʱ������
								List sql = new ArrayList();
								sql.add(billcardPanel.getInsertSQL());
								// ���빤�ʱ������ӵĹ�ϵ��
								BillVO vo = billcardPanel.getBillVO();
								SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
								//zzl [2020-5-18] ��������ʵ�
								ifc.onCreateSalaryBill(sql, vo.getPkValue(), accountid, checkdate,vo);
								timer.cancel();
								billVO = billcardPanel.getBillVO();
								closeType = 1;
								dialog.dispose();
							} catch (Exception e) {
								e.printStackTrace();
								timer.cancel();
								MessageBox.show(billcardPanel, "���ɹ��ʵ������쳣���ٴγ��Ի������Ա��ϵ!");
								closeType = -1;
								return;
							}
						}
					}, false);
				}
				}
			};
			if (dialog.getBtn_save() != null) {
				dialog.getBtn_save().setVisible(false);
			}
			dialog.setVisible(true);
			if (dialog.getCloseType() == 1) {
				int li_newrow = salary_bill_list.newRow(false);
				salary_bill_list.setBillVOAt(li_newrow, dialog.getBillVO(), false);
				salary_bill_list.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT);
				salary_bill_list.setSelectedRow(li_newrow);
				MessageBox.show(salary_bill_list, "�����ɹ�!");
			}
		}
	}

	public void onOpen() {
		BillVO vo = salary_bill_list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(this, "��ѡ��һ����¼�����д˲���!");
			return;
		}
		//zzl[2020-5-18] �����ʵ�չʾ
		String wgtype;
		try{
			HashVO[] modelVos=UIUtil.getHashVoArrayByDS(null,"select * from sal_account_set where id='"+vo.getStringValue("sal_account_setid")+"'");
			wgtype=modelVos[0].getStringValue("name");
			if(wgtype.contains("����")){
				WGSalaryBillDetailDialog sbd = new WGSalaryBillDetailDialog(this, vo);
				sbd.setVisible(true);
			}else{
				SalaryBillDetailDialog sbd = new SalaryBillDetailDialog(this, vo);
				sbd.setVisible(true);
			}
		}catch (Exception e){
            e.printStackTrace();
		}
	}

	public void onDel() {
		try {
			int li_selRow = salary_bill_list.getSelectedRow();
			if (li_selRow < 0) {
				MessageBox.showSelectOne(salary_bill_list);
				return;
			}
			if (!MessageBox.confirmDel(salary_bill_list)) {
				return;
			}
			String str_tableName = salary_bill_list.templetVO.getSavedtablename();
			if (str_tableName == null || str_tableName.trim().equals("")) {
				MessageBox.show(this, "ģ���ж���ı������Ϊ��,���ܽ���ɾ������!");
				return;
			}
			String str_pkName = salary_bill_list.templetVO.getPkname();
			if (str_pkName == null || str_pkName.trim().equals("")) {
				MessageBox.show(this, "�����ֶ���Ϊ��,���ܽ���ɾ������!");
				return;
			}

			BillVO billVO = salary_bill_list.getSelectedBillVO(); //
			String str_pkValue = billVO.getStringValue(salary_bill_list.templetVO.getPkname());
			String str_sql = "delete from " + str_tableName + " where " + str_pkName + "='" + str_pkValue + "'";
			String str_sql2 = "delete from sal_salarybill_detail where salarybillid='" + str_pkValue + "'";
			String str_sql3 = "delete from sal_salarybill_factor where salarybillid='" + str_pkValue + "'";
			UIUtil.executeBatchByDS(null, new String[] { str_sql, str_sql2, str_sql3 });
			salary_bill_list.removeRow(li_selRow);
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex);
		}
	}

	public void onSubmit() {
		int li_selRow = salary_bill_list.getSelectedRow();
		if (li_selRow < 0) {
			MessageBox.showSelectOne(salary_bill_list);
			return;
		}
		if (MessageBox.showConfirmDialog(salary_bill_list, "���Ź��ʵ���Ա�����ڸ������Ľ��и��˹��ʵ���ѯ!��ȷ�Ͽ�����?", "����", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			UpdateSQLBuilder usb = new UpdateSQLBuilder(salary_bill_list.getTempletVO().getSavedtablename());
			usb.putFieldValue("state", "�ѿ���");
			usb.setWhereCondition("id=" + salary_bill_list.getSelectedBillVO().getPkValue());
			try {
				UIUtil.executeBatchByDS(null, new String[] { usb.getSQL() });
				MessageBox.show(this, "�����ɹ�!");
				salary_bill_list.refreshCurrSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.show(this, "����ʧ�����ٴγ��Ի������Ա��ϵ!");
			}
		}
	}

	/**
	 * zzl
	 */
	public void onend() {
		int li_selRow = salary_bill_list.getSelectedRow();
		if (li_selRow < 0) {
			MessageBox.showSelectOne(salary_bill_list);
			return;
		}
		if (MessageBox.showConfirmDialog(salary_bill_list, "��ȷ���رչ��ʵ���?", "����", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			UpdateSQLBuilder usb = new UpdateSQLBuilder(salary_bill_list.getTempletVO().getSavedtablename());
			usb.putFieldValue("state", "δ����");
			usb.setWhereCondition("id=" + salary_bill_list.getSelectedBillVO().getPkValue());
			try {
				UIUtil.executeBatchByDS(null, new String[] { usb.getSQL() });
				MessageBox.show(this, "�����ɹ�!");
				salary_bill_list.refreshCurrSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.show(this, "����ʧ�����ٴγ��Ի������Ա��ϵ!");
			}
		}
	}

	private SalaryServiceIfc getService() {
		if (service == null) {
			try {
				service = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return service;
	}
}
