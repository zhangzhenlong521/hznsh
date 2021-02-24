package com.pushworld.ipushgrc.ui.score.p090;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.report.BillCellPanel;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;

/**
 * 
 * Copyright Pushine
 * @ClassName: com.pushworld.ipushgrc.ui.score.p090.DeptScoreStaticWKPanel 
 * @Description: 内容
 * @author haoming
 * @date Oct 13, 2014 11:52:15 AM
 *
 */
public class DeptScoreStaticWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillQueryPanel querypanel = new BillQueryPanel("SCORE_DEPT_HM_CODE1");
	IPushGRCServiceIfc ifc = null;
	BillCellPanel cellpanel;
	private WLTButton btn_export = new WLTButton("导出(Excel)");
	String sql_deptids;

	public void initialize() {
		this.setLayout(new BorderLayout());
		try {
			querypanel.addBillQuickActionListener(this);
			HashMap queryConsMap = querypanel.getQuickQueryConditionAsMap(true, false); //先算出查询条件,如果有必须项的查询条件而没输入,则直接返回!

			//增加违规积分之单位积分统计查询策略，可通过平台参数配置【李春娟/2015-02-02】
			String str_userId = ClientEnvironment.getInstance().getLoginUserID();
			String str_policyName = TBUtil.getTBUtil().getSysOptionStringValue("单位积分统计查询策略", "通用策略(本单位)");
			if (str_policyName != null && !"".equals(str_policyName.trim())) {//如果配置不为空，则增加过滤，否则不需要查询策略
				FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class);
				String[] str_depts = service.getDataPolicyCondition(str_userId, str_policyName, 1, "id", null); // 
				if (str_depts != null && str_depts.length >= 2 && str_depts[1] != null) {
					String[] deptids = UIUtil.getStringArrayFirstColByDS(null, "select id from pub_corp_dept where " + str_depts[1]);//查出所有本单位的机构
					if (deptids != null && deptids.length > 0) {
						StringBuffer sb_dept = new StringBuffer(";");
						for (int i = 0; i < deptids.length; i++) {
							sb_dept.append(deptids[i]);
							sb_dept.append(";");
						}
						sql_deptids = sb_dept.toString();
						queryConsMap.put("单位负责人角色名称", getMenuConfMapValueAsStr("单位负责人角色名称","总行部门负责人;支行负责人"));
						queryConsMap.put("DEPTID", sql_deptids);
					}
				}
			}

			ifc = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			cellpanel = new BillCellPanel(ifc.getDeptScoreCellVO(queryConsMap));
			WLTPanel btnpanel = new WLTPanel(new FlowLayout(FlowLayout.LEFT));
			btnpanel.add(btn_export);
			btn_export.addActionListener(this);
			WLTPanel southPanel = new WLTPanel(new BorderLayout());
			this.add(querypanel, BorderLayout.NORTH);
			southPanel.add(btnpanel, BorderLayout.NORTH);
			southPanel.add(cellpanel, BorderLayout.CENTER);
			this.add(southPanel, BorderLayout.CENTER);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_export) {
			onExportExcel();
		} else {
			HashMap queryConsMap = querypanel.getQuickQueryConditionAsMap(true, false); //先算出查询条件,如果有必须项的查询条件而没输入,则直接返回!
			queryConsMap.putAll(getMenuConfMap());
			try {
				if (sql_deptids != null) {
					queryConsMap.put("DEPTID", sql_deptids);
				}
				queryConsMap.put("单位负责人角色名称", getMenuConfMapValueAsStr("单位负责人角色名称","总行部门负责人;支行负责人"));
				BillCellVO cellvo = ifc.getDeptScoreCellVO(queryConsMap);
				cellpanel.loadBillCellData(cellvo);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}

	private void onExportExcel() {
		cellpanel.exportExcel();
	}

}
