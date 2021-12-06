package com.pushworld.ipushgrc.ui.wfrisk.p110;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.wfrisk.CmpFileHistoryViewDialog;
import com.pushworld.ipushgrc.ui.wfrisk.CmpfileAndWFGraphDialog;

/**
 * 历史版本维护，删除流程文件的历史版本（最新版本不可删除）
 * @author lcj
 *
 */
public class WFAndRiskVersionWKPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener {
	private BillListPanel billList_cmpfile = null; //
	private WLTButton btn_version = null;

	@Override
	public void initialize() {
		String templetcode = TBUtil.getTBUtil().getSysOptionHashItemStringValue("各功能中流程文件的模板", "版本管理", "CMP_CMPFILE_CODE3");//以前是同维护模板，但云南城投企业内控，客户内控部统一来版本管理，故修改之【李春娟/2015-01-05】
		billList_cmpfile = new BillListPanel(templetcode);
		if (billList_cmpfile.getTempletItemVO("versionno") != null) {
			billList_cmpfile.getTempletItemVO("versionno").setListishtmlhref(false);//设置不显示链接
		}
		billList_cmpfile.setDataFilterCustCondition("versionno is not null");//只查询到有版本号的文件
		btn_version = new WLTButton("历史版本");
		btn_version.addActionListener(this);
		billList_cmpfile.addBillListButton(btn_version);
		billList_cmpfile.repaintBillListButton();
		billList_cmpfile.addBillListHtmlHrefListener(this);
		this.add(billList_cmpfile); //
	}

	public void actionPerformed(ActionEvent e) {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String cmpfileid = billVO.getStringValue("id");
		CmpFileHistoryViewDialog dialog = new CmpFileHistoryViewDialog(this, "文件[" + billVO.getStringValue("cmpfilename") + "]的历史版本", cmpfileid, true); //
		dialog.setVisible(true); //
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		onLookFileAndWf();
	}

	/**
	 * 文件名称-链接，查看流程文件及其所有流程
	 */
	private void onLookFileAndWf() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请选择一个流程文件!"); //
			return;
		}
		CmpfileAndWFGraphDialog dialog = new CmpfileAndWFGraphDialog(this, "查看文件和流程", billVO.getStringValue("id"));
		dialog.setVisible(true);
	}
}
