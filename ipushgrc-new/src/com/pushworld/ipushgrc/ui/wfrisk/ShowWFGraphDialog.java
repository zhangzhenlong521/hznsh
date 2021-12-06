package com.pushworld.ipushgrc.ui.wfrisk;

import java.awt.BorderLayout;
import java.awt.Container;

import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.AbstractHyperLinkDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;

import com.pushworld.ipushgrc.ui.wfrisk.p010.WFGraphEditPanel;

/**
 * 流程文件发布与废止的模板中 查看流程的链接事件
 * @author lcj
 *
 */
public class ShowWFGraphDialog extends AbstractHyperLinkDialog {

	public ShowWFGraphDialog(Container _parent) {
		super(_parent);
	}

	@Override
	public void initialize() {
		this.setSize(1000, 700); //
		this.getContentPane().setLayout(new BorderLayout());
		BillCardPanel cardpanel = (BillCardPanel) this.getBillPanel();
		BillVO billVO = cardpanel.getBillVO();
		String cmpfileid = billVO.getStringValue("id");
		String cmpfilename = billVO.getStringValue("cmpfilename");
		String[][] processes = null;
		try {
			processes = UIUtil.getStringArrayByDS(null, "select id,code,name from pub_wf_process where cmpfileid =" + cmpfileid + " order by userdef04,id");
		} catch (Exception e) {
			MessageBox.showException(cardpanel, e);
		}// 流程文件的所有流程
		if (processes == null || processes.length == 0) {// 判断流程文件是否有流程
			throw new WLTAppException("该文件没有流程!");
		}
		WFGraphEditPanel graphPanel = new WFGraphEditPanel(this, cmpfileid, cmpfilename, processes, false); //
		graphPanel.showLevel(processes[0][0]);
		this.getContentPane().add(graphPanel); //
	}
}
