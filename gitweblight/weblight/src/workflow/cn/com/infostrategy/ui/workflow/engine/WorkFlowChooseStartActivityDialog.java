package cn.com.infostrategy.ui.workflow.engine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.workflow.design.WorkFlowDesignWPanel;

/**
 * ѡ��һ�����̽�������
 * @author xch
 *
 */
public class WorkFlowChooseStartActivityDialog extends BillDialog implements ActionListener {

	private ActivityVO[] allActivityVOS = null; //
	private String wfprocessid = null; //
	private WorkFlowDesignWPanel graphPanel = null;
	private WLTButton btn_confirm, btn_cancel;
	private int closeType = -1;

	private ActivityVO returnActivityVO = null; //

	public WorkFlowChooseStartActivityDialog(Container _parent, ActivityVO[] _activityVOs) {
		super(_parent, "���ݼ��㷢�ֶ�����������Ļ���,��ѡ������һ��������", 1000, 700); //
		this.allActivityVOS = _activityVOs; //
		this.wfprocessid = "" + _activityVOs[0].getProcessid(); //
		initialize();
	}

	private void initialize() {
		this.setLayout(new BorderLayout()); //
		graphPanel = new WorkFlowDesignWPanel(false); //
		graphPanel.setToolBarVisiable(false);
		graphPanel.loadGraphByID(wfprocessid); //
		graphPanel.lockGroupAndOnlyDoSelect();//ѡ����������ʱ�������϶�����
		String[] str_activityIds = new String[allActivityVOS.length]; //
		for (int i = 0; i < str_activityIds.length; i++) {
			str_activityIds[i] = "" + allActivityVOS[i].getId(); //
		}

		graphPanel.lightCell(str_activityIds, Color.BLUE); //
		this.add(graphPanel, BorderLayout.CENTER); //
		this.add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		panel.setBackground(Color.WHITE); //
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");

		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	public void onConfirm() {
		ActivityVO chooseActivityVO = graphPanel.getSelectedActivityVO(); //
		if (chooseActivityVO == null) {
			MessageBox.show(this, "����ѡ��һ������������!"); //
			return;
		}

		int li_choose = -1; //
		for (int i = 0; i < allActivityVOS.length; i++) {
			if (allActivityVOS[i].getId().intValue() == chooseActivityVO.getId().intValue()) {
				li_choose = i; //
			}
		}

		if (li_choose < 0) {
			MessageBox.show(this, "ֻ��ѡ����ɫ��Ľ���������!!"); //
			return;
		}
		returnActivityVO = allActivityVOS[li_choose]; //
		closeType = BillDialog.CONFIRM;
		this.dispose(); //
	}

	/**
	 * 
	 * @return
	 */
	public ActivityVO getReturnActivityVO() {
		return returnActivityVO; //
	}

	public void onCancel() {
		closeType = BillDialog.CANCEL;
		this.dispose();
	}

	public int getCloseType() {
		return closeType;
	}

}
