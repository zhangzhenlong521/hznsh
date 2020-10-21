package cn.com.pushworld.wn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.text.html.StyleSheet.ListPainter;

import com.ibm.db2.jcc.a.v;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;

/**
 * @author zzl
 * 
 *         2019-3-27-����06:26:03 ��Ա����������ּƻ�
 */
public class GYServerKHPlan extends AbstractWorkPanel implements
		ActionListener, BillListSelectListener {
	private BillListPanel list = null;
	private WLTButton btn_ks, btn_end, btn_delete = null;
	private String str = null;
	private CommDMO dmo = new CommDMO();

	@Override
	public void initialize() {
		list = new BillListPanel("WN_GYKHPLAN_CODE1");
		btn_ks = new WLTButton("��ʼ���");
		btn_end = new WLTButton("�������");
		btn_delete = new WLTButton("ɾ��");
		btn_ks.addActionListener(this);
		btn_end.addActionListener(this);
		btn_delete.addActionListener(this);
		list.addBillListButton(btn_delete);
		list.addBillListButton(btn_ks);
		list.addBillListButton(btn_end);
		list.repaintBillListButton();
		list.addBillListSelectListener(this);
		this.add(list);
	}

	@Override
	public void actionPerformed(ActionEvent act) {
		if (act.getSource() == btn_ks) {
			gradeScore();
		} else if (act.getSource() == btn_end) {
			gradeEnd();
		} else if (act.getSource() == btn_delete) {
			deleteScore();
		}
	}

	private void deleteScore() {// ɾ�����ּƻ�
		try {
			BillVO vo = list.getSelectedBillVO();
			if (vo == null) {
				MessageBox.show(this, "��ѡ��һ�����˼ƻ�����ɾ��");
				return;
			}
			// ��ȡ����ǰ���˼ƻ���״̬
			String state = UIUtil.getStringValueByDS(
					null,
					"select state from WN_GYKHPLAN where id='"
							+ vo.getStringValue("ID") + "'");
			if (!"δ����".equals(state)) {
				MessageBox.show(this, "��ǰ���ּƻ���" + vo.getStringValue("PLANNAME")
						+ "����״̬Ϊ��" + state + "��,�޷�ɾ��");
				return;
			}
			UIUtil.executeUpdateByDS(
					null,
					"delete from WN_GYKHPLAN  where ID='"
							+ vo.getStringValue("id") + "'");
			list.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBillListSelectChanged(BillListSelectionEvent ect) {
		BillVO vo = list.getSelectedBillVO();
		if (vo.getStringValue("state").equals("������")) {
			btn_ks.setEnabled(false);
			btn_end.setEnabled(true);
		} else if (vo.getStringValue("state").equals("���ֽ���")) {
			btn_ks.setEnabled(false);
			btn_end.setEnabled(false);
		} else {
			btn_ks.setEnabled(true);
			btn_end.setEnabled(true);
		}
	}

	public void gradeScore(int q) {
		try {
			final BillVO vo = list.getSelectedBillVO();
			HashVO[] vos = UIUtil
					.getHashVoArrayByDS(null, "select * from "
							+ list.getTempletVO().getTablename()
							+ " where state='������'");
			if (vos.length > 0) {
				MessageBox.show(this, "�������еĿ��˼ƻ������Ƚ��������еĿ��˼ƻ���Ȼ�����½����˼ƻ�");
				return;
			}
			HashVO[] timeVos = UIUtil.getHashVoArrayByDS(null, "select * from "
					+ list.getTempletVO().getTablename() + " where PLANNAME='"
					+ vo.getStringValue("PLANTIME") + "'");
			if (timeVos.length > 0) {
				MessageBox.show(this, "����ʱ��Ϊ��" + vo.getStringValue("PLANTIME")
						+ "���ƻ��Ѿ����ڣ�");
				return;
			}
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					str = service.getSqlInsert(vo.getStringValue("PLANTIME"));
					UpdateSQLBuilder update = new UpdateSQLBuilder(list
							.getTempletVO().getTablename());
					update.setWhereCondition("id='" + vo.getStringValue("id")
							+ "'");
					update.putFieldValue("state", "������");
					try {
						UIUtil.executeUpdateByDS(null, update.getSQL());
						list.refreshCurrSelectedRow();
					} catch (WLTRemoteException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			list.refreshData();
			MessageBox.show(this, str);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void gradeScore() {
		try {
			// BillVO selectedBillVO = list.getSelectedBillVO();
			// if("���ֽ���".equals(selectedBillVO.getStringValue("state"))){
			// MessageBox.show(this,"��ǰ���˼ƻ��Ѿ�����");
			// }
			// ��ȡ����ǰ�����
			BillVO vo = list.getSelectedBillVO();
			if (vo == null) {
				MessageBox.show(this, "��ѡ��һ�����ݽ��в���");
				return;
			}
			String id = vo.getStringValue("ID");
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent event) {
					String date = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss")
							.format(new Date());
					str = service.getSqlInsert(date);
				}
			});
			UIUtil.executeUpdateByDS(null,
					"UPDATE WN_GYKHPLAN SET STATE='������' WHERE ID='" + id + "'");
			MessageBox.show(this, str);
			list.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void gradeEnd() {
		try {// �������:1.�޸�״̬;2.�����ܷ�
			BillVO selectedBillVO = list.getSelectedBillVO();
			if (selectedBillVO == null) {
				MessageBox.show(this, "��ѡ��һ�����˼ƻ����в���");
				return;
			}
			String pftime=UIUtil.getStringValueByDS(null,"select max(pftime) from WN_GYPF_TABLE") ;
			final HashVO[] vo = UIUtil
					.getHashVoArrayByDS(
							null,
							"SELECT distinct(USERCODE) AS USERCODE,STATE,PFTIME,FHRESULT FROM WN_GYPF_TABLE WHERE STATE='������'  OR FHRESULT<>'����ͨ��' AND PFTIME='"+pftime+"'");
			String result = UIUtil
					.getStringValueByDS(
							null,
							"SELECT COUNT(*) FROM WN_GYPF_TABLE WHERE STATE='������' OR FHRESULT IS NULL OR FHRESULT<>'����ͨ��' AND PFTIME='"+pftime+"'");
			int count = Integer.parseInt(result);

			int sumcount = 0;
			if (count > 0) {
				sumcount = MessageBox.showOptionDialog(this,
						"��ǰ���˼ƻ��д����Ƿ�������δ��������δ���˵Ĺ�Ա���Ƿ������ǰ���˼ƻ�", "��ʾ",
						new String[] { "��", "��" }, 0);
			}
			if (sumcount != 0) {
				new SplashWindow(this, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent actionevent) {
						for (int i = 0; i < vo.length; i++) {
							HashVO v = vo[i];
							gradeEndEveryOne(v.getStringValue("USERCODE"));
						}
					}
				});
				MessageBox.show(this, "��ǰ���˼ƻ������ɹ�");
				UIUtil.executeUpdateByDS(null,
						"update WN_GYKHPLAN set  state='���ֽ���' where id='"
								+ selectedBillVO.getStringValue("ID") + "'");
			} else {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void gradeEnd(int w) {
		try {
			BillVO vo = list.getSelectedBillVO();
			HashVO[] vos = UIUtil.getHashVoArrayByDS(
					null,
					"select * from WN_GYPF_TABLE where 1=1 and PFTIME='"
							+ vo.getStringValue("PLANTIME")
							+ "' and KOUOFEN is null");
			if (vos.length > 0) {
				if (MessageBox.confirm(this, "��δ��������֣�ȷ��Ҫǿ�ƽ�����")) {
					UpdateSQLBuilder update = new UpdateSQLBuilder(list
							.getTempletVO().getTablename());
					update.setWhereCondition("id='" + vo.getStringValue("id")
							+ "'");
					update.putFieldValue("state", "���ֽ���");
					UIUtil.executeUpdateByDS(null, update.getSQL());
					UpdateSQLBuilder update2 = new UpdateSQLBuilder(
							"WN_GYPF_TABLE");
					update2.setWhereCondition("PFTIME='"
							+ vo.getStringValue("PLANTIME") + "'");
					update2.putFieldValue("state", " ���ֽ���");
					UIUtil.executeUpdateByDS(null, update2.getSQL());
					list.refreshData();
					MessageBox.show(this, "������ֳɹ�");
				}
			} else {
				UpdateSQLBuilder update = new UpdateSQLBuilder(list
						.getTempletVO().getTablename());
				update.setWhereCondition("id='" + vo.getStringValue("id") + "'");
				update.putFieldValue("state", "���ֽ���");
				UIUtil.executeUpdateByDS(null, update.getSQL());
				UpdateSQLBuilder update2 = new UpdateSQLBuilder("WN_GYPF_TABLE");
				update2.setWhereCondition("PFTIME='"
						+ vo.getStringValue("PLANTIME") + "'");
				update2.putFieldValue("state", "���ֽ���");
				UIUtil.executeUpdateByDS(null, update2.getSQL());
				list.refreshData();
				MessageBox.show(this, "������ֳɹ�");
			}
		} catch (Exception e) {
			MessageBox.show(this, "�������ʧ��");
			e.printStackTrace();
		}
	}

	public void gradeEndEveryOne(String usercode) {
		try {
			Double KOUOFEN = 0.0;
			Double result = 0.0;
			String pftime=UIUtil.getStringValueByDS(null, "select max(pftime) from WN_GYPF_TABLE where usercode='"+usercode+"'")  ;
			UpdateSQLBuilder update = new UpdateSQLBuilder(
					"wnSalaryDb.WN_GYPF_TABLE");
			HashVO[] vo = UIUtil.getHashVoArrayByDS(null,
					"select * from wnSalaryDb.WN_GYPF_TABLE where usercode='"
							+ usercode + "' and  (FHRESULT<>'����ͨ��' OR FHRESULT IS null) and pftime='"+pftime+"'   Order by ID");
			List list = new ArrayList<String>();
			
			for (int i = 0; i < vo.length - 1; i++) {
				String koufenValue = vo[i].getStringValue("KOUFEN");
				String defenValue = vo[i].getStringValue("FENZHI");
				if (koufenValue == null || "".equals(koufenValue)) {
					koufenValue = defenValue;
				}
				if (Double.parseDouble(koufenValue) > Double
						.parseDouble(defenValue)) {
					koufenValue = defenValue;
				}
				KOUOFEN = Double.parseDouble(koufenValue);
				result = result + KOUOFEN;
				update.setWhereCondition("id='" + vo[i].getStringValue("id")
						+ "' and pftime='"+pftime+"'");
				update.putFieldValue("KOUOFEN", KOUOFEN);
				update.putFieldValue("state", "���ֽ���");
				list.add(update.getSQL());
			}
			double sumfen = 100.00 - result;
			String sumSQL = "update wnSalaryDb.WN_GYPF_TABLE set KOUOFEN='"
					+ sumfen + "',state='���ֽ���' where usercode='" + usercode
					+ "' and xiangmu='�ܷ�' and pftime='"+pftime+"'";
			list.add(sumSQL);
			if (list.size() > 0) {
				UIUtil.executeBatchByDS(null, list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
