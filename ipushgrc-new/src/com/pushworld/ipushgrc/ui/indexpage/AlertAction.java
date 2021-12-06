package com.pushworld.ipushgrc.ui.indexpage;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.sysapp.login.DeskTopNewGroupVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;
import cn.com.infostrategy.ui.sysapp.login.IndexItemTaskPanel;

public class AlertAction extends AbstractAction {

	private String alertid = null;
	private String linkurl = null;
	private DeskTopPanel desktoppanel = null;
	private HashVO datavo = null;
	private DeskTopNewGroupVO groupvo = null;

	public void actionPerformed(ActionEvent e) {
		try {
			IndexItemTaskPanel itemtaskPanel = (IndexItemTaskPanel) e.getSource();
			TBUtil tbutil = new TBUtil();
			datavo = (HashVO) this.getValue("DeskTopNewsDataVO");
			desktoppanel = (DeskTopPanel) this.getValue("DeskTopPanel");
			alertid = datavo.getStringValue("id");
			linkurl = datavo.getStringValue("linkurl");
			if (linkurl == null || linkurl.equals("")) {
				//���Ϊ�� ʲô������
			}else if (linkurl.contains(".")) {//����·�� 
				AbstractWorkPanel wk = (AbstractWorkPanel) Class.forName(linkurl).newInstance();
				if (wk == null) {
					MessageBox.show(desktoppanel, "ģ���෴�����ʧ�ܣ�");
					return;
				}
				wk.setLayout(new BorderLayout()); //������Ҫ����
				wk.initialize();
				Component com = wk.getComponent(0); // �õ�BillListPanel
				BillListPanel listPanel = null;
				if (com instanceof BillListPanel) {
					listPanel = (BillListPanel) com;
					WLTButton btn = listPanel.getBillListBtn("����������");
					if (btn != null) {
						btn.setVisible(false);
					}
					listPanel.getTempletVO().setDatapolicy(null);
					listPanel.getTempletVO().setDatapolicymap(null);
					listPanel.QueryDataByCondition(" id in (" + tbutil.getInCondition(datavo.getStringValue("dataids")) + ")"); // ������ʾ����Ψһ
					listPanel.setDataFilterCustCondition(" id in ( " + tbutil.getInCondition(datavo.getStringValue("dataids")) + ")");// ������ʾ����Ψһ
				}
				BillDialog dialog = new BillDialog(desktoppanel);
				dialog.setLayout(new BorderLayout());
				dialog.getContentPane().setLayout(new BorderLayout());
				dialog.add(wk, BorderLayout.CENTER);
				dialog.setSize(800, 400);
				dialog.locationToCenterPosition();
				dialog.setVisible(true);
			} else { //��һ��ģ��
				if (datavo.getStringValue("dataids").contains(";")) {
					BillListDialog dialog = new BillListDialog(desktoppanel, "�鿴", linkurl);
					BillListPanel listpanel = dialog.getBilllistPanel();
					listpanel.QueryDataByCondition(listpanel.getTempletVO().getPkname() + " in (" + tbutil.getInCondition(datavo.getStringValue("dataids")) + ")");
					listpanel.setDataFilterCustCondition(listpanel.getTempletVO().getPkname() + " in (" + tbutil.getInCondition(datavo.getStringValue("dataids")) + ")");
					dialog.locationToCenterPosition();
					dialog.setVisible(true);
				} else {
					BillCardDialog dialog = new BillCardDialog(desktoppanel, linkurl);
					BillCardPanel cardPanel = dialog.getBillcardPanel();
					cardPanel.queryDataByCondition(cardPanel.getTempletVO().getPkname() + " = " + datavo.getStringValue("dataids"));
					dialog.setVisible(true);
				}
			}
			if (MessageBox.confirm(desktoppanel, "������ʾ����Ϣ����?")) {
				InsertSQLBuilder insertSQl = new InsertSQLBuilder("msg_alert_looked");
				insertSQl.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "s_msg_alert_looked"));
				insertSQl.putFieldValue("alertid", alertid);
				insertSQl.putFieldValue("lookeduser", ClientEnvironment.getCurrSessionVO().getLoginUserName());
				insertSQl.putFieldValue("lookdate", UIUtil.getServerCurrDate());
				UIUtil.executeBatchByDS(null, new String[] { insertSQl.getSQL() });
				itemtaskPanel.onRefreshGroup(true);
			}
		} catch (Exception e1) {
			MessageBox.show((Container) e.getSource(), e1.getMessage());
		}
	}

};
