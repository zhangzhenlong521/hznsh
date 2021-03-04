package cn.com.infostrategy.ui.sysapp.job;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

// Ԭ����  20170411 ����
public class PubJobWKPanel extends AbstractWorkPanel implements ActionListener {

	private static final long serialVersionUID = -1381725904698235431L;
	private WLTButton btn_state, btn_restart, btn_stop, btn_view;

	private BillListPanel billList = null;

	public void initialize() {
		billList = new BillListPanel("pub_job_CODE1");
		btn_state = new WLTButton("�鿴����״̬");
		btn_restart = new WLTButton("��������");
		btn_stop = new WLTButton("ֹͣ����");
		btn_view = new WLTButton("������ʽ");
		billList.addBatchBillListButton(new WLTButton[] { btn_view, btn_state, btn_restart, btn_stop });
		btn_state.addActionListener(this);
		btn_restart.addActionListener(this);
		btn_stop.addActionListener(this);
		btn_view.addActionListener(this);
		billList.repaintBillListButton();
		this.add(billList); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_state) {
			lookJobState();
		} else if (e.getSource() == btn_restart) {
			startJob();
		} else if (e.getSource() == btn_stop) {
			stopJob();
		} else if (e.getSource() == btn_view) {
			onShowFormat();
		}
	}

	private void onShowFormat() {
		StringBuffer sb = new StringBuffer("������ʽ���£�\r\n");
		sb.append("ÿ��ÿ��10���ӵ���,���磺ÿʱ;10;\r\n");
		sb.append("ÿ��5��50����,���磺ÿ��;05:50;\r\n");
		sb.append("ÿ����13��50����,���磺ÿ��;3;13:50;\r\n");
		sb.append("ÿ��10��13��50����,���磺ÿ��;10;13:50;\r\n");
		sb.append("ÿ���360��13��50����,ÿ��;360;13:50;\r\n");
		sb.append("ÿ���ȵ�һ���µڶ����8��40����,ÿ��;1;2;08:40;\r\n");
		MessageBox.show(billList, sb.toString());
	}

	private void lookJobState() {
		BillVO billVO = billList.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String jobName = billVO.getStringValue("NAME"); //
		try {
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.getCommonService();
			String str_result = service.lookJobState(jobName);
			MessageBox.show(this, str_result); //
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private void startJob() {
		BillVO billVO = billList.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String jobName = billVO.getStringValue("NAME"); //
		if (JOptionPane.showConfirmDialog(this, "��ȷ����������JOB��" + jobName + "����?", "��ʾ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
			try {
				FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.getCommonService();
				String str_result = service.startJob(jobName);
				MessageBox.show(this, str_result); //
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

	private void stopJob() {
		BillVO billVO = billList.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String jobName = billVO.getStringValue("NAME"); //
		int option = JOptionPane.showConfirmDialog(this, "��ȷ��ֹͣJOB��" + jobName + "����?", "��ʾ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (option == JOptionPane.YES_OPTION) {
			try {
				FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.getCommonService();
				String str_result = service.stopJob(jobName);
				MessageBox.show(this, str_result);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
	}

}
