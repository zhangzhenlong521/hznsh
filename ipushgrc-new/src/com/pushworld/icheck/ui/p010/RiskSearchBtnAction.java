package com.pushworld.icheck.ui.p010;

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

/**
 * 在逐条录入界面，相关制度有个所有
 * @author hm
 *
 */
public class RiskSearchBtnAction implements WLTActionListener, ActionListener {
	private WLTButton btn_select, btn_cancel;
	private BillDialog dialog;
	private BillCardPanel cardPanel;
	private BillListPanel riskPanel = null;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(cardPanel.getEditState())) {
			MessageBox.show(cardPanel, "浏览界面，不可导入！");//【李春娟/2016-09-05】
			return;
		}
		riskPanel = new BillListPanel("CMP_RISK_SCY_Q01");
		riskPanel.setLayout(new BorderLayout());
		riskPanel.initialize();
		dialog = new BillDialog(_event.getBillPanelFrom(), "在系统中搜索风险点", 850, 450);//修改窗口提示【李春娟/2016-09-30】
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(riskPanel, BorderLayout.CENTER);
		dialog.locationToCenterPosition();
		WLTPanel btnPanel = (WLTPanel) WLTPanel.createDefaultPanel(new FlowLayout());
		btn_select = new WLTButton("选择", "office_175.gif");
		btn_select.addActionListener(this);
		btn_cancel = new WLTButton("返回", "office_078.gif");
		btn_cancel.addActionListener(this);
		btnPanel.add(btn_select);
		btnPanel.add(btn_cancel);
		dialog.getContentPane().add(btnPanel, BorderLayout.SOUTH);
		dialog.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_select) {
			BillVO vos[] = riskPanel.getSelectedBillVOs();
			if (vos.length == 0) {
				MessageBox.show(cardPanel, "请至少选择一条记录");
				return;
			}
			StringBuffer re_sb = new StringBuffer(); //返回的数据
			StringBuffer re_sb_contrl = new StringBuffer(); //返回的数据
			for (int i = 0; i < vos.length; i++) {
				
				String code = vos[i].getStringValue("riskcode");
				String name = vos[i].getStringValue("riskname");

				String ctrlfnstype = vos[i].getRefItemVOValue("ctrlfnstype").getName();
				String ctrlfn3 = vos[i].getStringValue("ctrlfn3");
				
				name = TBUtil.getTBUtil().getLawOrRuleName(name); //加书名号。
				code = TBUtil.getTBUtil().getLawOrRuleCode(code);
				
				if(null!=ctrlfnstype)
				ctrlfnstype = TBUtil.getTBUtil().getLawOrRuleName(ctrlfnstype); //加书名号。
				if(null!=ctrlfn3)
				ctrlfn3 = TBUtil.getTBUtil().getLawOrRuleCode(ctrlfn3);

				if (re_sb.length() > 0) {
					re_sb.append("\n");
				}
				re_sb.append(name);
				re_sb.append(code);

				if (re_sb_contrl.length() > 0) {
					re_sb_contrl.append("\n");
				}
				if(null!=ctrlfnstype)
				re_sb_contrl.append(ctrlfnstype);
				if(null!=ctrlfn3)
				re_sb_contrl.append(ctrlfn3);
			}
			StringItemVO obj = (StringItemVO) cardPanel.getValueAt("tag_risk");
			if (obj != null && !"".equals(obj.toString())) { //如果不为空
				if ("《》()".equals(obj.toString()) || "格式为:《制度名称》(制度文号)换行".equals(obj.toString())) {
					cardPanel.setValueAt("tag_risk", new StringItemVO(re_sb.toString()));
				} else {
					cardPanel.setValueAt("tag_risk", new StringItemVO(obj + "\n" + re_sb.toString()));
				}
			} else {
				cardPanel.setValueAt("tag_risk", new StringItemVO(re_sb.toString()));
			}
			
			//相关控制
			if(!" ".equals(re_sb_contrl.toString())&& null!=re_sb_contrl){
				StringItemVO obj_contrl = (StringItemVO) cardPanel.getValueAt("tag_ctrldict");
				if (obj_contrl != null && !"".equals(obj_contrl.toString())) { //如果不为空
					if ("《》()".equals(obj_contrl.toString()) || "格式为:《制度名称》(制度文号)换行".equals(obj_contrl.toString())) {
						cardPanel.setValueAt("tag_ctrldict", new StringItemVO(re_sb_contrl.toString()));
					} else {
						cardPanel.setValueAt("tag_ctrldict", new StringItemVO(obj_contrl + "\n" + re_sb_contrl.toString()));
					}
				} else {
					cardPanel.setValueAt("tag_ctrldict", new StringItemVO(re_sb_contrl.toString()));
				}
			}
			
			dialog.dispose();
		} else if (e.getSource() == btn_cancel) {
			dialog.dispose();
		}
	}
}
