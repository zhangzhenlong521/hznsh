package com.pushworld.icheck.ui.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

import com.pushworld.ipushgrc.ui.icheck.word.WordExport;

/**
 * ���ƻ�ά�� ���Ǽ��ƻ�(CK_PLAN)�ĵ��б�ά�������/2016-08-10��
 * �ƻ�״̬��δִ�С�ִ���С��ѽ���
 * 
 */

public class CheckPlanWKPanel extends AbstractWorkPanel implements ActionListener,BillListSelectListener {
	private BillListPanel billList_plan = null;
	private WLTButton btn_insert, btn_edit, btn_delete, btn_export, btn_start, btn_end;
	private String status1 = "δִ��", status2 = "ִ����", status3 = "�ѽ���";//���ƻ�״̬
	private BillVO vo=null;

	@Override
	public void initialize() {
		billList_plan = new BillListPanel("CK_PLAN_LCJ_E01");
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); // ����
		btn_edit = new WLTButton("�޸�");
		btn_delete = new WLTButton("ɾ��");
		btn_export = new WLTButton("����ȷ����");
		btn_start = new WLTButton("��ʼ");
		btn_end = new WLTButton("����");
		btn_edit.addActionListener(this);
		btn_delete.addActionListener(this);
		btn_export.addActionListener(this);
		btn_start.addActionListener(this);
		btn_end.addActionListener(this);

		billList_plan.addBatchBillListButton(new WLTButton[] { btn_insert, btn_edit, btn_delete, btn_export, btn_start, btn_end });
		billList_plan.repaintBillListButton();
		billList_plan.addBillListSelectListener(this);
		
		this.add(billList_plan);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_edit) {
			onEdit();
		} else if (obj == btn_delete) {
			onDelete();
		} else if (obj == btn_export) {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					onExport();
				}
			});
			
		} else if (obj == btn_start) {
			onStart();
		} else if (obj == btn_end) {
			onEnd();
		}
	}

	/**
	 * �޸ļƻ�
	 */
	private void onEdit() {
		BillVO billvo = billList_plan.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String status = billvo.getStringValue("status");
		//δִ�л� ִ���в����ǹ���Ա���ݣ�����޸�
		if (status == null || "".equals(status) || status1.equals(status) || (status2.equals(status) && ClientEnvironment.getInstance().isAdmin())) {
			billList_plan.doEdit();
		} else {
			MessageBox.show(this, "�üƻ�" + status + ",�����޸�.");
		}
	}

	/**
	 * ɾ���ƻ�
	 * ���ǵ����ߺ���ܻ��в������ݣ�����������Ա�����û�ɾ����ִ�еļƻ�����������Ϣ
	 */
	private void onDelete() {
		BillVO billvo = billList_plan.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String status = billvo.getStringValue("status");
		if (status == null || "".equals(status) || status1.equals(status)) {
			billList_plan.doDelete(false);
		} else if (ClientEnvironment.getInstance().isAdmin()) {//�Ƿ����Ա����
			try {
				String[] schemes = UIUtil.getStringArrayFirstColByDS(null, "select name from CK_SCHEME where planid =" + billvo.getStringValue("id"));
				if (schemes == null || schemes.length == 0) {
					billList_plan.doDelete(false);
				} else {
					StringBuffer sb = new StringBuffer("���·����ѹ����üƻ���\n");
					for (int i = 0; i < schemes.length; i++) {
						sb.append((i + 1) + "��" + schemes[i] + "\n");
					}
					sb.append("�Ƿ�ȷ��ɾ����");
					if (MessageBox.confirm(this, sb.toString())) {//ɾ���ƻ����Ȳ�ɾ�������������Ϣ�ˡ����/2016-08-25��
						billList_plan.doDelete(true);
					}
				}

			} catch (WLTRemoteException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			//			if (MessageBox.showConfirmDialog(this, "�ò�����ɾ���üƻ������������������Ϣ,�Ƿ����ɾ��?") == JOptionPane.YES_OPTION) {
			//				ArrayList sqlList = new ArrayList();
			//				sqlList.add("delete from CK_PLAN where id =" + billvo.getStringValue("id"));
			//				sqlList.add("delete from CK_SCHEME where planid =" + billvo.getStringValue("id"));
			//				sqlList.add("delete from CK_MEMBER_WORK where planid =" + billvo.getStringValue("id"));//������Ҫ�׸���ơ�ɾ�����⡢���ĵȼ�¼��
			//				try {
			//					UIUtil.executeBatchByDS(null, sqlList);
			//					billList_plan.removeSelectedRow();
			//				} catch (Exception e) {
			//					e.printStackTrace();
			//					MessageBox.showException(this, e);
			//				}
			//			}
		} else {
			MessageBox.show(this, "�üƻ�" + status + ",����ɾ��.");
		}
	}

	/**
	 * ����ȷ����
	 * [zzl]
	 * 
	 */
	private void onExport() {
		if (vo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("��ѡ��Ҫ���浽��Ŀ¼");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		int result = fc.showSaveDialog(this);
		if (result != JFileChooser.APPROVE_OPTION) { // �������ȷ����
			return;
		}
				String savePath = fc.getSelectedFile().getPath();
				Map<Object, Object> dataMap = new HashMap<Object, Object>();
				try {
					String DeptName=UIUtil.getStringValueByDS(null, "select name from pub_corp_dept where id=1");
						dataMap.put("title",DeptName+ vo.getStringValue("PLANTYPE")+ "��Ŀȷ����");
						dataMap.put("titleTime", vo.getStringValue("CREATEDATE"));
						dataMap.put("code", vo.getStringValue("CODE"));
						dataMap.put("name", vo.getStringValue("PLANNAME"));
						String deptname = UIUtil.getStringValueByDS(null,"select name from pub_corp_dept where id="+ vo.getStringValue("CHECKDEPT")+ "");
						dataMap.put("dept", deptname);
						String[] time = vo.getStringValue("PLANBEGINDATE").split("-");
						String[] overtime = vo.getStringValue("PLANENDDATE").split("-");
						StringBuffer sb = new StringBuffer();
						sb.append(time[0].toString()+"��"+time[1].toString()+"��"+time[2].toString()+"����");	
						sb.append(overtime[0].toString()+"��"+overtime[1].toString()+"��"+overtime[2].toString()+"��");	
						dataMap.put("time", sb);
						String id=filterid(vo.getStringValue("CHECKEDDEPT"));
						String [] dtname=UIUtil.getStringArrayFirstColByDS(null,"select name from pub_corp_dept where id in("+id+")");
						StringBuffer ckdept=splitName(dtname);
						dataMap.put("ckdept", ckdept);						
						String usercode=filterid(vo.getStringValue("TEAMUSERS"));
						String [] uscode=UIUtil.getStringArrayFirstColByDS(null,"select name from pub_user where id in("+usercode+")");
						StringBuffer ckname=splitName(uscode);
						dataMap.put("ckname", ckname);
						dataMap.put("purpose", vo.getStringValue("GOAL"));
						dataMap.put("scope", vo.getStringValue("CKSCOPE"));
					savePath=savePath+"\\"+dataMap.get("title")+".doc";
					WordExport mdoc = new WordExport();
					mdoc.createDoc(dataMap,"WordConfirmModel", savePath);
					MessageBox.show(this,"�����ɹ�");
				} catch (Exception a) {
					a.printStackTrace();
				}
	

	}

	/**
	 * ��ʼ�ƻ��߼�
	 */
	private void onStart() {
		BillVO billvo = billList_plan.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String status = billvo.getStringValue("status");
		if (status == null || "".equals(status) || status1.equals(status)) {
			try {
				UIUtil.executeUpdateByDS(null, "update CK_PLAN set status='" + status2 + "' where id =" + billvo.getStringValue("id"));
				billList_plan.refreshCurrSelectedRow();
				MessageBox.show(this, "�����ɹ�!");
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.showException(this, e);
			}
		} else {
			MessageBox.show(this, "�üƻ�" + status + ",�����ٿ�ʼִ��.");
		}
	}

	/**
	 * �����ƻ��߼�
	 */
	private void onEnd() {
		BillVO billvo = billList_plan.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		//������Ҫ�ж��Ƿ���δ�����ļ�鷽�������У�����ʾ
		String status = billvo.getStringValue("status");
		if (status == null || "".equals(status) || status2.equals(status)) {
			try {
				//δִ�С��ѽ�����ִ����
				String[][] names = UIUtil.getStringArrayByDS(null, "select status,name from CK_SCHEME where planid =" + billvo.getStringValue("id") + " order by status");
				if (names == null || names.length == 0) {//
					if (!MessageBox.confirm(this, "�üƻ�û�м�鷽��,�Ƿ����?")) {
						return;
					}
				} else {
					StringBuffer sb_1 = new StringBuffer();
					StringBuffer sb_2 = new StringBuffer();
					int count1 = 1;
					int count2 = 1;
					for (int i = 0; i < names.length; i++) {
						if (status1.equals(names[i][0])) {
							if (sb_1.length() > 0) {
								sb_1.append("   " + count1 + "��" + names[i][1] + "\n");
							} else {
								sb_1.append("��üƻ���" + status1 + "�ļ�鷽����\n");
								sb_1.append("   " + count1 + "��" + names[i][1] + "\n");
							}
							count1++;
						} else if (status2.equals(names[i][0])) {
							if (sb_2.length() > 0) {
								sb_2.append("   " + count2 + "��" + names[i][1] + "\n");
							} else {
								sb_2.append("��üƻ���" + status2 + "�ļ�鷽����\n");
								sb_2.append("   " + count2 + "��" + names[i][1] + "\n");
							}
						}
					}
					sb_1 = sb_1.append(sb_2);
					if (sb_1.length() > 0) {
						MessageBox.show(this, sb_1 + "   ����ֱ�ӽ���!");
						return;
					}
				}
				UIUtil.executeUpdateByDS(null, "update CK_PLAN set status='" + status3 + "' where id =" + billvo.getStringValue("id"));
				billList_plan.refreshCurrSelectedRow();
				MessageBox.show(this, "�����ɹ�!");
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.showException(this, e);
			}
		} else {
			MessageBox.show(this, "�üƻ�" + status + ",�����ٽ���.");
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent e) {
		vo=e.getBillListPanel().getSelectedBillVO();	
	}
	private String filterid(String ids){
		ids = ids.replace(";", ",");
	    ids = ids.substring(1, ids.length() - 1);
	   return ids;
	   
	}
	private StringBuffer splitName(String[] name){
		StringBuffer sbname = new StringBuffer();
		for(int i=0;i<name.length;i++){
			sbname.append(name[i].toString());
			sbname.append(",");
		}
		return sbname;	   
	}
}