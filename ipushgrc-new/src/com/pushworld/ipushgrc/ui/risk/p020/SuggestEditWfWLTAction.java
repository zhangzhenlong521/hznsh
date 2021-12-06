package com.pushworld.ipushgrc.ui.risk.p020;

import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.ui.common.BillFrame;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillLevelPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_TextArea;

import com.pushworld.ipushgrc.ui.wfrisk.WFGraphEditFrame;
import com.pushworld.ipushgrc.ui.wfrisk.p010.WFGraphEditItemPanel;

/**
 * ������������-�����޸ĵ����̼����ڵ�ѡ��
 * @author lcj
 *
 */
public class SuggestEditWfWLTAction implements WLTActionListener {
	private BillCardPanel cardPanel = null;
	private BillVO billVO = null;
	private String[][] processes = null;// �����ļ�����������
	private WFGraphEditFrame graphFrame = null;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		billVO = cardPanel.getBillVO();
		String cmpfile_id = billVO.getStringValue("cmpfile_id");
		if (cmpfile_id == null || "".equals(cmpfile_id)) {
			MessageBox.show(cardPanel, "����ѡ��һ�������ļ�!");
			return;
		}

		processes = UIUtil.getStringArrayByDS(null, "select id,code,name from pub_wf_process where cmpfileid =" + cmpfile_id + " order by userdef04,id");
		if (processes == null || processes.length == 0) {
			MessageBox.show(cardPanel, "�������ļ�û�����̣����ܲ鿴!");
			return;
		}
		String cmpfilename = billVO.getStringViewValue("cmpfile_id");
		CardCPanel_TextArea textarea = (CardCPanel_TextArea) cardPanel.getCompentByKey("sug_adjwfact");
		if (textarea.getArea().isEditable()) {
			graphFrame = new WFGraphEditFrame(cardPanel, "��ѡ��һ�������޸ĵĻ���", 800, 700, cmpfile_id, cmpfilename, processes, false); //
			graphFrame.getBtn_cancel().setText("ȷ��");
			graphFrame.getBtn_cancel().setToolTipText("ȷ��");
			graphFrame.addWindowCloseListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					afterClose(); //
				}
			});
		} else {
			graphFrame = new WFGraphEditFrame(cardPanel, "�鿴����", 800, 700, cmpfile_id, cmpfilename, processes, false); //
		}
		graphFrame.setVisible(true);//ͬʱ��ʾ���̱༭����
	}

	protected void afterClose() {
		if (graphFrame.getCloseType() != BillFrame.CANCEL) {//����ǵ�����رա���ť�˳��Ĳ�ִ���߼������/2013-04-28��
			return;
		}
		BillLevelPanel levelpanel = graphFrame.getGraphPanel().getBillLevelPanel();
		ArrayList list_id = new ArrayList();
		for (int i = 0; i < processes.length; i++) {
			WFGraphEditItemPanel itempanel = (WFGraphEditItemPanel) levelpanel.getLevelPanel(processes[i][0]);
			if (itempanel == null || itempanel.getWorkFlowPanel() == null) {
				continue;
			}
			ActivityVO activityvo = itempanel.getWorkFlowPanel().getSelectedActivityVO();
			if (activityvo != null) {
				list_id.add(activityvo.getId() + "");
			}
		}
		if (list_id.size() == 0) {
			return;
		}
		try {
			HashVO[] activityvos = UIUtil.getHashVoArrayByDS(null, "select pro.name,act.belongdeptgroup,act.belongstationgroup,act.wfname from pub_wf_activity act left join pub_wf_process pro on act.processid = pro.id where act.id in(" + new TBUtil().getInCondition(list_id) + ")");
			String sug_adjwfact = billVO.getStringValue("sug_adjwfact");
			StringBuffer sb_desc = new StringBuffer();
			for (int i = 0; i < activityvos.length; i++) {
				if (i == 0 && (sug_adjwfact != null && !"".equals(sug_adjwfact.trim()))) {
					sb_desc.append(sug_adjwfact);
					if (!sug_adjwfact.endsWith("\n")) {
						sb_desc.append("\n");
					}
				}
				sb_desc.append("�����޸�����[");
				sb_desc.append(activityvos[i].getStringValue("name"));
				sb_desc.append("]��");
				String dept = activityvos[i].getStringValue("belongdeptgroup");
				if (dept != null) {
					sb_desc.append("����Ϊ[");
					sb_desc.append(dept.replace("\n", ""));
					sb_desc.append("]");
				}
				String station = activityvos[i].getStringValue("belongstationgroup");
				if (station != null) {
					sb_desc.append("�׶�Ϊ[");
					sb_desc.append(station.replace("\n", ""));
					sb_desc.append("]");
				}
				sb_desc.append("�Ļ���[");
				sb_desc.append(activityvos[i].getStringValue("wfname").replace("\n", ""));
				sb_desc.append("]");
				sb_desc.append("\n");
			}
			if (sb_desc.length() > 0) {
				cardPanel.setRealValueAt("sug_adjwfact", sb_desc.toString());
			}
		} catch (Exception e1) {
			MessageBox.showException(cardPanel, e1);
		}
	}
}