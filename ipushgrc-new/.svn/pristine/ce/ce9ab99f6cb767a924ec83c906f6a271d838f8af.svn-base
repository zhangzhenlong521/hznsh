package com.pushworld.ipushgrc.ui.cmpreport.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.keywordreplace.TemplateToWordUIUtil;

/**
 * 合规综合报查询!!! 权限如何
 * 就是对合规报告(cmp_report)的单列表
 * @author xch
 *
 */
public class CmpReportQueryWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel billList = null; //
	WLTButton export_btn = null;

	@Override
	public void initialize() {
		billList = new BillListPanel("CMP_REPORT_CODE1"); //
		export_btn = new WLTButton("导出Word");
		//		export_btn.addActionListener(this);
		//		billList.addBillListButton(export_btn);
		boolean wf = TBUtil.getTBUtil().getSysOptionBooleanValue("合规报告是否走工作流", true);//项目实施时，综合报告直接将结果提交，故增加该配置【loj/2015-05-21】
		if (!wf) {
			billList.setItemVisible("state", false);//如果不走工作流，则隐藏状态【loj/2015-05-21】
		}
		billList.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });//该列表以前没有按钮，很突兀，故增加浏览按钮【李春娟/2012-03-23】
		billList.repaintBillListButton();
		this.add(billList); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == export_btn) {
			TemplateToWordUIUtil tem_word_util = new TemplateToWordUIUtil();
			BillVO[] checkItemVOs = billList.getSelectedBillVOs();
			if (checkItemVOs == null || checkItemVOs.length == 0) {
				MessageBox.showSelectOne(this);
				return;
			}
			String[] fileNames = new String[checkItemVOs.length];
			for (int i = 0; i < fileNames.length; i++) {
				fileNames[i] = checkItemVOs[i].getStringValue("reportname");
			}
			try {
				tem_word_util.createWordByOneListPanel("CMP_REPORT_CODE1", checkItemVOs, fileNames, this);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
