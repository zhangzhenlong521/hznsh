package com.pushworld.ipushgrc.ui.icheck.p010;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

import com.pushworld.ipushgrc.ui.rule.p020.RuleQueryWKPanel;

/**
 * ������¼����棬����ƶ��и�����
 * @author hm
 *
 */
public class RuleSearchBtnAction implements WLTActionListener, ActionListener {
	private WLTButton btn_select, btn_cancel;
	private BillDialog dialog;
	private BillCardPanel cardPanel;
	private RuleQueryWKPanel rulePanel;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(cardPanel.getEditState())) {
			MessageBox.show(cardPanel, "������棬���ɵ��룡");//�����/2016-09-05��
			return;
		}
		rulePanel = new RuleQueryWKPanel(); //ֱ�ӵ��úϹ��ҳ�档
		rulePanel.setLayout(new BorderLayout());
		rulePanel.initialize();
		dialog = new BillDialog(_event.getBillPanelFrom(), "��ϵͳ�������ƶ�", 850, 450);//�޸Ĵ�����ʾ�����/2016-09-30��
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(rulePanel, BorderLayout.CENTER);
		dialog.locationToCenterPosition();
		WLTPanel btnPanel = (WLTPanel) WLTPanel.createDefaultPanel(new FlowLayout());
		btn_select = new WLTButton("ѡ��", "office_175.gif");
		btn_select.addActionListener(this);
		btn_cancel = new WLTButton("����", "office_078.gif");
		btn_cancel.addActionListener(this);
		btnPanel.add(btn_select);
		btnPanel.add(btn_cancel);
		dialog.getContentPane().add(btnPanel, BorderLayout.SOUTH);
		dialog.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_select) {
			BillListPanel listPanel = rulePanel.getBillList_rule();
			BillVO vos[] = listPanel.getSelectedBillVOs();
			if (vos.length == 0) {
				MessageBox.show(cardPanel, "������ѡ��һ����¼");
				return;
			}
			StringBuffer re_sb = new StringBuffer(); //���ص�����
			for (int i = 0; i < vos.length; i++) {
				String code = vos[i].getStringValue("rulecode");
				String name = vos[i].getStringValue("rulename");
				name = TBUtil.getTBUtil().getLawOrRuleName(name); //�������š�
				code = TBUtil.getTBUtil().getLawOrRuleCode(code);
				if (re_sb.length() > 0) {
					re_sb.append("\n");
				}
				re_sb.append(name);
				re_sb.append(code);
			}
			StringItemVO obj = (StringItemVO) cardPanel.getValueAt("rules_id");
			if (obj != null && !"".equals(obj.toString())) { //�����Ϊ��
				if ("����()".equals(obj.toString()) || "��ʽΪ:���ƶ����ơ�(�ƶ��ĺ�)����".equals(obj.toString())) {
					cardPanel.setValueAt("tag_rule", new StringItemVO(re_sb.toString()));
				} else {
					cardPanel.setValueAt("tag_rule", new StringItemVO(obj + "\n" + re_sb.toString()));
				}
			} else {
				cardPanel.setValueAt("tag_rule", new StringItemVO(re_sb.toString()));
			}
			dialog.dispose();
		} else if (e.getSource() == btn_cancel) {
			dialog.dispose();
		}
	}
}