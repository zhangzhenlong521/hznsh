package com.pushworld.ipushgrc.ui.wfrisk.p080;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JLabel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 体系文件查询权限设置!! 与平台的数据权限如何集成??
 * @author xch
 *
 */
public class CmpFileQueryPermitWKPanel extends AbstractWorkPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private BillListPanel billList = null; //

	private WLTButton btn_insert, btn_update, btn_delete, btn_list; //增,删,改
	private WLTButton btn_moveup, btn_movedown, btn_save, btn_refreah; //
	private String str_datapolicy_id = null; //

	@Override
	public void initialize() {
		try {
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, "select * from pub_datapolicy where name='流程文件查询策略'"); ////
			if (hvs == null || hvs.length <= 0) {
				this.add(new JLabel("没有定义名为【流程文件查询策略】的数据权限策略,请定义之!")); //
				return; //
			}
			str_datapolicy_id = hvs[0].getStringValue("id"); //策略id
			billList = new BillListPanel("PUB_DATAPOLICY_B_CODE1"); //
			billList.QueryDataByCondition("datapolicy_id='" + str_datapolicy_id + "'"); ////
			billList.getTitleLabel().setText("流程文件查询策略"); //
			btn_insert = new WLTButton("新增"); //
			btn_insert.addActionListener(this); //
			btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //修改
			btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //删除
			btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //浏览
			btn_moveup = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP); //
			btn_movedown = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN); //
			btn_save = WLTButton.createButtonByType(WLTButton.LIST_SAVE, "保存顺序"); //
			btn_refreah = new WLTButton("刷新"); //
			btn_refreah.addActionListener(this); //
			billList.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_list, btn_moveup, btn_movedown, btn_save, btn_refreah }); //
			billList.repaintBillListButton(); //
			this.add(billList); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_insert) {
			onInsert(); //
		} else if (e.getSource() == btn_refreah) {
			onRefresh(); //
		}
	}

	private void onInsert() {
		HashMap defaultValueMap = new HashMap(); //
		defaultValueMap.put("datapolicy_id", str_datapolicy_id); ////
		billList.doInsert(defaultValueMap); //做插入操作!!
	}

	private void onRefresh() {
		billList.QueryDataByCondition("datapolicy_id='" + str_datapolicy_id + "'"); ////
	}
}
