package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import com.sun.mail.handlers.message_rfc822;

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

//���Ŷ���ָ����
public class BMqualitative extends AbstractWorkPanel implements ActionListener,
		BillListSelectListener {

	private BillListPanel listPanel;
	private WLTButton btn_ks, btn_end = null;
	private String str = null;
	private CommDMO dmo = new CommDMO();

	@Override
	public void initialize() {
		// WN_JDFKMHZB_02_Q02_ZPY
		listPanel = new BillListPanel("WN_BMPFPLAN_Q01_ZPY");
		btn_ks = new WLTButton("��ʼ����");
		btn_ks.addActionListener(this);
		btn_end = new WLTButton("��������");
		btn_end.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_ks, btn_end });
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_ks) {// ��ʼ���
			gradeScore();
		} else if (e.getSource() == btn_end) {// �������
			gradeEnd();
		}
	}

	private void gradeEnd() {// 1.����ǰ״̬ȫ������;2.����ÿ������ÿһ��������յ÷�
		try {// ��������ÿһ���Ȼ��ȷ��ÿһ������
			BillVO selectedBillVO = listPanel.getSelectedBillVO();
			if (selectedBillVO == null) {
				MessageBox.show("��ѡ��һ�мƻ����в���");
				return;
			}
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			new SplashWindow(this, new AbstractAction() {

				@Override
				public void actionPerformed(ActionEvent e) {
					str = service.gradeBMScoreEnd();
				}
			});
			MessageBox.show(this, str);
			listPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ��ʼ��֣�Ϊÿ��ָ�����ɴ�ּƻ�
	 */
	private void gradeScore() {
		try {
			// �жϵ�ǰ���ƻ����Ƿ���ڴ����
			final BillVO vo = listPanel.getSelectedBillVO();
			if (vo == null) {
				MessageBox.show(this, "��ѡ��һ��״̬Ϊ��δ���ɡ��ļƻ����в���������");
				return;
			}
			final String id = vo.getStringValue("id");// ��ȡ��ǰ�ƻ���id
			HashVO[] hashvo = UIUtil.getHashVoArrayByDS(null,
					"select * from wn_BMPF_table where planid='" + id + "' ");
			if (hashvo.length > 0) {// �������ظ����ɼƻ�
				MessageBox.show(this, "��ǰѡ�мƻ���" + vo.getStringValue("PLANNAME")
						+ "���Ѿ�����");
				return;
			}
			HashVO[] vos = UIUtil.getHashVoArrayByDS(null,
					"select * from WN_BMPFPLAN where state='������' and id!='"
							+ id + "'");
			if (vos.length > 0) {//
				MessageBox.show(this, "������δ�����Ĳ��ſ��˼ƻ�,���Ƚ���������");
				return;
			}
			final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
					.lookUpRemoteService(WnSalaryServiceIfc.class);
			new SplashWindow(this, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					str = service.getBMsql(id);
					UpdateSQLBuilder update = new UpdateSQLBuilder(listPanel
							.getTempletVO().getTablename());
					update.setWhereCondition("id='" + vo.getStringValue("id")
							+ "'");
					update.putFieldValue("state", "������");
					try {
						UIUtil.executeUpdateByDS(null, update.getSQL());
						listPanel.refreshCurrSelectedRow();
						listPanel.refreshData();
					} catch (WLTRemoteException e1) {
						e1.printStackTrace();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBillListSelectChanged(BillListSelectionEvent event) {
		BillVO vo = listPanel.getSelectedBillVO();
		String state = vo.getStringValue("state");
		if ("������".equals(state)) {
			btn_ks.setEnabled(false);
		} else if ("���ֽ���".equals(state)) {
			btn_ks.setEnabled(false);
			btn_end.setEnabled(false);
		} else {
			btn_ks.setEnabled(true);
			btn_end.setEnabled(true);
		}
	}
}