package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import bsh.This;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.pushworld.wn.bs.WnSalaryServiceImpl;

public class GydxdfWKPanel extends AbstractWorkPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private BillListPanel listPanel;
	private WLTButton btn_ks, btn_end, btn_delete;// ��ʼ��֣��������
	private String result;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("WN_GYDXPLAN_ZPY_Q01");
		btn_ks = new WLTButton("��ʼ���");
		btn_ks.addActionListener(this);
		btn_end = new WLTButton("�������");
		btn_end.addActionListener(this);
		btn_delete = new WLTButton("ɾ��");
		btn_delete.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_delete, btn_ks,
				btn_end });
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_ks) {// ��ʼ���ˣ����ɴ�ּƻ�
			gradeScore();
		} else if (e.getSource() == btn_end) {// ��������
			gradeEnd();
		} else if (e.getSource() == btn_delete) {
			deleteRow();
		}
	}

	private void deleteRow() {// ɾ���У�����������ɾ��
		try {
			BillVO[] selected = listPanel.getSelectedBillVOs();
			if (selected.length < 0) {
				MessageBox.show(this, "��ѡ�����ݽ��в���������");
				return;
			}
			StringBuilder str = new StringBuilder();
			List<String> _sqlList = new ArrayList<String>();
			for (int i = 0; i < selected.length; i++) {// ɾ��
				String planname = selected[i].getStringValue("planname");
				String state = selected[i].getStringValue("state");// ��ȡ����ǰ�ƻ�״̬
				String id = selected[i].getStringValue("id");
				if (!"δ����".equals(state)) {
					str.append(planname + " ");
				} else {
					String sql = "delete from WN_GYDXPLAN where id='" + id
							+ "'";
					_sqlList.add(sql);
				}
			}
			UIUtil.executeBatchByDS(null, _sqlList);
			if (str == null || str.toString().isEmpty() || str.length() == 0) {
				MessageBox.show(this, "ɾ���������");
			} else {
				MessageBox.show(this, "����ɾ�����,�ƻ�����Ϊ��" + str + "���޷�ɾ����");
			}
			listPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void gradeScore() {// ��ʼ����,Ϊÿ����Ա���ɶ��Կ��˼ƻ�
		try {
			BillVO selectedBillVO = listPanel.getSelectedBillVO();// ��ȡ����ǰѡ�мƻ�
			final String id = selectedBillVO.getStringValue("id");// ��ȡ����ǰѡ�мƻ���ID
			String state = selectedBillVO.getStringValue("state");// ��ȡ���ƻ�״̬
			if (!"δ����".equals(state)) {// ��ȡ����ǰ״̬
				MessageBox.show(
						this,
						"��ǰ��Ա���Կ��˼ƻ���"
								+ selectedBillVO.getStringValue("planname")
								+ "��״̬Ϊ��" + state + "��,�޷��ٴ����ɣ�����");
				return;
			}
			// �����ɿ��˼ƻ�֮ǰ���ж��Ƿ�����������˼ƻ�״̬�Ƿ����
			String countString = UIUtil.getStringValueByDS(null,
					"select count(*) from WN_GYDXPLAN where id!='" + id
							+ "' and state='������'");
			Integer count = Integer.parseInt(countString);
			if (count != 0) {
				HashVO[] plans = UIUtil.getHashVoArrayByDS(null,
						"select * from WN_GYDXPLAN where state='������'");
				StringBuffer buffer = new StringBuffer("");
				for (int i = 0; i < plans.length; i++) {
					buffer.append(plans[i].getStringValue("planname"));
				}
				MessageBox.show(this, "���ڹ�Ա���Կ��˼ƻ���" + buffer
						+ "��������δ���������Ƚ��������ƻ�");
				return;
			}
			// �жϵ�ǰ���˼ƻ��Ƿ�ȫ����������Լ��������
			String countString2 = UIUtil
					.getStringValueByDS(null,
							"SELECT COUNT(*) FROM wn_gydx_table WHERE state='������' OR fhresult IS NULL");
			Integer count2 = Integer.parseInt(countString2);
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			// if(count2>0){
			// int sumcount=MessageBox.showOptionDialog(this,
			// "��ǰ���˼ƻ��д����Ƿ�������δ��������δ���˵Ĺ�Ա���Ƿ������ǰ���˼ƻ�","��ʾ", new String[] { "��",
			// "��" },1);
			// if(sumcount!=0){
			// new SplashWindow(this, new AbstractAction() {
			// @Override
			// public void actionPerformed(ActionEvent e) {
			// result=service.gradeDXscore(id);
			// }
			// });
			// listPanel.refreshData();
			// }else{
			// return;
			// }
			// }else{
			// new SplashWindow(this, new AbstractAction() {
			// @Override
			//
			// });
			// listPanel.refreshData();
			// }
			//
			//
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					result = service.gradeDXscore(id);
				}
			});
			listPanel.refreshData();
			MessageBox.show(this, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void gradeEnd() {// ��������
		try {
			BillVO selectedBillVO = listPanel.getSelectedBillVO();// ��ȡ����ǰ���˼ƻ�
			if (selectedBillVO == null) {
				MessageBox.show(this, "��ѡ��һ�����ݽ���");
				return;
			}
			// ��ȡ����ǰ�ƻ�״̬
			String state = selectedBillVO.getStringValue("state");// ��ȡ��ǰ�ƻ���״̬
			String planName = selectedBillVO.getStringValue("planname");// ��ȡ����ǰ�ƻ���״̬
			final String planid = selectedBillVO.getStringValue("id");// ��ȡ����ǰ�ƻ���ID
			if (!"������".equals(state)) {
				MessageBox.show(this, "��ǰ���˼ƻ���" + planName + "��״̬Ϊ��" + state
						+ "��,�޷�������");
				return;
			}
			// ���û�ѡ��ȷ������ʱ,�ǵ����û�ȷ��һ��
			String count = UIUtil
					.getStringValueByDS(null,
							"select count(*) from wn_gydx_table where state='������' or fhresult='����δͨ��'");
			if (Integer.parseInt(count) > 0) {
				if (MessageBox.confirm(this, "��δ��������֣�ȷ��Ҫǿ�ƽ�����")) {
					// ������ǰ���˼ƻ�
					final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
							.lookUpRemoteService(WnSalaryServiceIfc.class);
					new SplashWindow(this, new AbstractAction() {
						@Override
						public void actionPerformed(ActionEvent e) {
							result = service.gradeDXEnd(planid);
						}
					});
				} else {
					return;
				}
			} else {
				// ������ǰ���˼ƻ�
				final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
				new SplashWindow(this, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						result = service.gradeDXEnd(planid);
					}
				});
			}
			String sql = "update  WN_GYDXPLAN  set STATE='���ֽ���' where id='"
					+ planid + "'";
			UIUtil.executeUpdateByDS(null, sql);
			MessageBox.show(this, result);
			listPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ----->��ʱ����ʼ����ִ�мƻ�
	public void gradeScore(int num) {
		try {
			// Ϊ�������˼ƻ����ɼƻ�ID
			String maxId = UIUtil.getStringValueByDS(null,
					"select max(ID) from WN_GYDX_TABLE");
			if (maxId == null || maxId.isEmpty()) {
				maxId = "1";
			} else {
				maxId = String.valueOf((Integer.parseInt(maxId) + 1));
			}
			WnSalaryServiceIfc service = new WnSalaryServiceImpl();
			String str = service.gradeDXscore(maxId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ------>��ʱ�����������ִ�мƻ�
	public void gradeEnd(int num) {// ���Կ��˼ƻ�����
		try {
			String countStr = UIUtil
					.getStringValueByDS(null,
							"select count(*) from WN_GYDX_TABLE where state='������' or  fhresult is null ");
			if (Integer.parseInt(countStr) <= 0) {
				return;
			} else {
				String planId = UIUtil
						.getStringValueByDS(null,
								"select id from WN_GYDX_TABLE where state='������' or fhresult  IS null");
				WnSalaryServiceIfc service = new WnSalaryServiceImpl();
				String str = service.gradeDXEnd(planId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
