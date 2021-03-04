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

// 袁江晓  20170411 更改
public class PubJobWKPanel extends AbstractWorkPanel implements ActionListener {

	private static final long serialVersionUID = -1381725904698235431L;
	private WLTButton btn_state, btn_restart, btn_stop, btn_view;

	private BillListPanel billList = null;

	public void initialize() {
		billList = new BillListPanel("pub_job_CODE1");
		btn_state = new WLTButton("查看任务状态");
		btn_restart = new WLTButton("启动任务");
		btn_stop = new WLTButton("停止任务");
		btn_view = new WLTButton("参数格式");
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
		StringBuffer sb = new StringBuffer("参数格式如下：\r\n");
		sb.append("每天每隔10分钟调用,比如：每时;10;\r\n");
		sb.append("每天5点50调用,比如：每天;05:50;\r\n");
		sb.append("每周三13点50调用,比如：每周;3;13:50;\r\n");
		sb.append("每月10号13点50调用,比如：每月;10;13:50;\r\n");
		sb.append("每年第360天13点50调用,每年;360;13:50;\r\n");
		sb.append("每季度第一个月第二天的8点40调用,每季;1;2;08:40;\r\n");
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
		if (JOptionPane.showConfirmDialog(this, "您确定重新启动JOB【" + jobName + "】吗?", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
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
		int option = JOptionPane.showConfirmDialog(this, "您确定停止JOB【" + jobName + "】吗?", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
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
