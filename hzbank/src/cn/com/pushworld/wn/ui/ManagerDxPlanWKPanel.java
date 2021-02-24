package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class ManagerDxPlanWKPanel extends AbstractWorkPanel implements
		ActionListener {

	private BillListPanel listPanel;
	private WLTButton btn_start, btn_end, btn_delete;
	private String str;

	// private HashVO[] hashVos =null;

	@Override
	public void initialize() {
		// try {
		// hashVos=UIUtil.getHashVoArrayByDS(null,
		// "select * from sal_person_check_list where 1=1  and (type='61')  and (1=1)  order by seq");
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		listPanel = new BillListPanel("WN_MANAGERPLAN_TABLE_Q01_ZPY");
		btn_start = new WLTButton("��ʼ���");
		btn_start.addActionListener(this);
		btn_end = new WLTButton("�������");
		btn_end.addActionListener(this);
		btn_delete = new WLTButton("ɾ��");
		listPanel.addBatchBillListButton(new WLTButton[] { btn_delete,
				btn_start, btn_end });
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_delete) {// ɾ������
			deletePlan();
		} else if (e.getSource() == btn_start) {// ��ʼ���
			startGradeScore();
		} else if (e.getSource() == btn_end) {// ��������
			endGradeScore();
		}
	}

	private void endGradeScore() {
		try {
			// ������ǰ���˼ƻ�
			BillVO selectedBillVO = listPanel.getSelectedBillVO();
			final String id = selectedBillVO.getStringValue("id");
			String state = selectedBillVO.getStringValue("state");// ��ȡ����ǰѡ�мƻ���״̬
			if ("���ֽ���".equals(state)) {
				MessageBox.show(this, "��ǰ���˼ƻ��Ѿ�����,����Ҫ�ظ�����");
				return;
			}
			// �жϵ�ǰ�����Ƿ����
			String count = UIUtil
					.getStringValueByDS(
							null,
							"select count(*) num from WN_MANAGERDX_TABLE where state='������' and PFUSERNAME is null");
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			if (Double.parseDouble(count) > 0) {// ��ʾ���ڴ������δ�������
				if (MessageBox.confirm("����˼ƻ��д��ڿͻ�����������δ��ɣ��Ƿ���������ּƻ���")) {
					new SplashWindow(this, new AbstractAction() {
						@Override
						public void actionPerformed(ActionEvent e) {
							str = service.endManagerDXscore(id);
						}
					});
				} else {
					return;
				}
			} else {// ֱ�ӽ���
				new SplashWindow(this, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						str = service.endManagerDXscore(id);
					}
				});
			}
			UIUtil.executeUpdateByDS(null,
					"update wn_managerplan_table set state='���ֽ���' where id='"
							+ id + "'");
			listPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void startGradeScore() {// Ϊÿ���ͻ��������ɿ��˼ƻ�
		try {
			// ��ȡ����ǰѡ�мƻ�
			BillVO selectedBillVO = listPanel.getSelectedBillVO();// ��ȡ����ǰѡ�мƻ�
			final String id = selectedBillVO.getStringValue("id");// ��ȡ����ǰ���˼ƻ���ID
			String state = selectedBillVO.getStringValue("state");// ��ȡ���ƻ�״̬
			if (!"δ����".equals(state)) {
				MessageBox.show(
						this,
						"��ǰ�ͻ������Կ��˼ƻ���"
								+ selectedBillVO.getStringValue("planname")
								+ "��״̬Ϊ��" + state + "��,�޷��ٴ����ɣ�����");
				return;
			}
			String countString = UIUtil.getStringValueByDS(null,
					"select count(*) from wn_managerplan_table where id!='"
							+ id + "' and state='������'");
			Integer count = Integer.parseInt(countString);
			if (count > 0) {
				HashVO[] plans = UIUtil.getHashVoArrayByDS(null,
						"select * from wn_managerplan_table where state='������'");
				StringBuilder buffer = new StringBuilder();
				for (int i = 0; i < plans.length; i++) {
					buffer.append(plans[i].getStringValue("planname"));
				}
				MessageBox.show(this, "���ڿͻ������Կ��˼ƻ���" + buffer
						+ "��������δ���������Ƚ��������ƻ�");
				return;
			}
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					str = service.gradeManagerDXscore(id);
				}
			});
			MessageBox.show(this, str);
			UIUtil.executeUpdateByDS(null,
					"update wn_managerplan_table set state='������' where id='"
							+ id + "'");
			listPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deletePlan() {// ɾ���ƻ�
		try {
			BillVO[] billVos = listPanel.getSelectedBillVOs();
			if (billVos.length <= 0) {
				MessageBox.show(this, "��ѡ��һ�����ݽ��в���");
			}
			StringBuffer str = new StringBuffer();
			List<String> _sqllist = new ArrayList<String>();
			String state = "";
			String planname = "";
			String id = "";
			String _sql = "";
			for (int i = 0; i < billVos.length; i++) {
				state = billVos[i].getStringValue("state");// ��ȡ����ǰ���˼ƻ���״̬
				planname = billVos[i].getStringValue("planname");
				id = billVos[i].getStringValue("id");
				if (!"δ����".equals(state)) {
					str.append(planname + " ");
				}
				_sql = "delete from wn_managerplan_table where id='" + id + "'";
				_sqllist.add(_sql);
			}
			UIUtil.executeBatchByDS(null, _sqllist);// ɾ������
			if (str.length() > 0) {
				MessageBox.show(this, "����ɾ�����,�ƻ�����Ϊ��" + str + "���޷�ɾ����");
			} else {
				MessageBox.show(this, "����ɾ�����");
			}
			listPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}