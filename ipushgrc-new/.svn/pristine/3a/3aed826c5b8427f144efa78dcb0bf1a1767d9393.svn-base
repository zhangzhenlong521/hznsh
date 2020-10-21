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

import com.pushworld.ipushgrc.ui.law.p020.LawQueryWKPanel;

/**
 * 法规相关
 * @author scy copy from hm
 *
 */
public class LawSearchBtnAction implements WLTActionListener, ActionListener {
	private WLTButton btn_select, btn_cancel;
	private BillDialog dialog;
	private BillCardPanel cardPanel;
	private LawQueryWKPanel lawPanel;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		if (WLTConstants.BILLDATAEDITSTATE_INIT.equalsIgnoreCase(cardPanel.getEditState())) {
			MessageBox.show(cardPanel, "浏览界面，不可导入！");//【李春娟/2016-09-05】
			return;
		}
		lawPanel = new LawQueryWKPanel();
		lawPanel.setLayout(new BorderLayout());
		lawPanel.initialize();
		dialog = new BillDialog(_event.getBillPanelFrom(), "在系统中搜索法规", 850, 450);//修改窗口提示【李春娟/2016-09-30】
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(lawPanel, BorderLayout.CENTER);
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
			BillListPanel listPanel = lawPanel.getlistPanel();
			BillVO vos[] = listPanel.getSelectedBillVOs();
			if (vos.length == 0) {
				MessageBox.show(cardPanel, "请至少选择一条记录");
				return;
			}
			StringBuffer re_sb = new StringBuffer(); //返回的数据
			for (int i = 0; i < vos.length; i++) {
				String code = vos[i].getStringValue("dispatch_code"); //发布号 
				String name = vos[i].getStringValue("lawname"); //制度名称
				name = TBUtil.getTBUtil().getLawOrRuleName(name); //加书名号。
				code = TBUtil.getTBUtil().getLawOrRuleCode(code);
				if (re_sb.length() > 0) {
					re_sb.append("\n");
				}
				re_sb.append(name);
				re_sb.append(code);
			}
			StringItemVO obj = (StringItemVO) cardPanel.getValueAt("tag_law");
			if (obj != null && !"".equals(obj.toString())) { //如果不为空
				if ("《》()".equals(obj.toString()) || "格式为:《制度名称》(制度文号)换行".equals(obj.toString())) {
					cardPanel.setValueAt("tag_law", new StringItemVO(re_sb.toString()));
				} else {
					cardPanel.setValueAt("tag_law", new StringItemVO(obj + "\n" + re_sb.toString()));
				}
			} else {
				cardPanel.setValueAt("tag_law", new StringItemVO(re_sb.toString()));
			}
			dialog.dispose();
		} else if (e.getSource() == btn_cancel) {
			dialog.dispose();
		}
	}
}
