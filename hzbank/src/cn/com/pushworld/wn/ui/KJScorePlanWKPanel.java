package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.pushworld.wn.bs.WnSalaryServiceImpl;

public class KJScorePlanWKPanel extends AbstractWorkPanel implements
		ActionListener {

	private BillListPanel listPanel;
	private WLTButton btn_delete, btn_grade, btn_end;
	private String str;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("WN_KJDXPLAN_TABLE_Q01_ZPY");
		btn_delete = new WLTButton("ɾ��");
		btn_delete.addActionListener(this);
		btn_grade = new WLTButton("��ʼ����");
		btn_grade.addActionListener(this);
		btn_end = new WLTButton("��������");
		btn_end.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_delete,
				btn_grade, btn_end });
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_delete) {// ɾ����ּƻ�
			try {
				BillVO vo = listPanel.getSelectedBillVO();
				if (vo == null) {
					MessageBox.show(this, "��ѡ��һ�мƻ����в���");
					return;
				}
				String state = vo.getStringValue("state");
				if (!"δ����".equals(state)) {
					MessageBox.show(this,
							"��ǰί�ɻ�ƿ��˼ƻ���" + vo.getStringValue("PLANNAME")
									+ "����״̬Ϊ��" + state + "�����޷�ɾ��");
					return;
				}
				UIUtil.executeUpdateByDS(
						null,
						"delete from wn_kjdxplan_table where id='"
								+ vo.getStringValue("id") + "'");
				listPanel.refreshData();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} else if (e.getSource() == btn_grade) {// ���ɴ�ּƻ�
			try {
				BillVO vo = listPanel.getSelectedBillVO();
				final String id = vo.getStringValue("id");
				if (vo == null) {
					MessageBox.show(this, "��ѡ��һ�п��˼ƻ�����");
					return;
				}
				String state = vo.getStringValue("state");// ��ȡ����ǰ״̬
				if (!"δ����".equals(state)) {
					MessageBox.show(this,
							"��ǰ���˼ƻ�Ϊ��" + vo.getStringValue("PLANNAME")
									+ "����״̬Ϊ��" + state + "��,�޷��ٴ����ɡ�");
					return;
				}
				// �ж��Ƿ����������ּƻ�����������
				HashVO[] gradingScore = UIUtil.getHashVoArrayByDS(null,
						"select * from wn_kjdxplan_table where state='������'");
				if (gradingScore.length > 0) {
					MessageBox.show(this, "�����������˼ƻ����������У����Ƚ����������˼ƻ�");
					return;
				}
				final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
				new SplashWindow(this, new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						str = service.getKJDXScore(id);
					}
				});
				UIUtil.executeUpdateByDSPS(null,
						"update wn_kjdxplan_table set state='������' where id='"
								+ id + "'");
				MessageBox.show(this, str);
				listPanel.refreshData();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} else if (e.getSource() == btn_end) {// ������ǰ���˼ƻ�
			try {
				BillVO vo = listPanel.getSelectedBillVO();
				final String id = vo.getStringValue("id");
				if (vo == null) {
					MessageBox.show(this, "��ѡ��һ�����˼ƻ����в���");
					return;
				}
				String state = vo.getStringValue("state");
				if (!"������".equals(state)) {
					MessageBox.show(this,
							"��ǰ���˼ƻ���" + vo.getStringValue("PLANNAME")
									+ "����״̬Ϊ��" + state + "��,�޷�����");
					return;
				}
				HashVO[] scoreNotEnd = UIUtil.getHashVoArrayByDS(null,
						"select * from wn_kjscore_table where  state='������'");
				final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
				if (scoreNotEnd.length > 0) {
					if (MessageBox
							.confirm("�ڵ�ǰ���˼ƻ��У�������δ�������ֵ�ί�ɻ�ƣ��Ƿ�����Ҫǿ�ƽ����أ�")) {// ȷ��ǿ�ƽ���
						new SplashWindow(this, new AbstractAction() {
							@Override
							public void actionPerformed(ActionEvent e) {
								str = service.getKJDXEnd(id);
							}
						});
					} else {
						return;
					}
				} else {
					new SplashWindow(this, new AbstractAction() {
						@Override
						public void actionPerformed(ActionEvent e) {
							str = service.getKJDXEnd(id);
						}
					});
				}
				UIUtil.executeUpdateByDS(null,
						"update wn_kjdxplan_table set state='���ֽ���' where id="
								+ id + "");
				MessageBox.show(this, str);
				listPanel.refreshData();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}