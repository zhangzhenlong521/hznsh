package com.pushworld.ipushlbs.ui.lawreport.p050;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

/**
 * 多维报表
 * 
 * @author yinliang 2011.12.19
 * 
 */
public class PowerApplyPrint2 extends MultiLevelReportDataBuilderAdapter {

	@Override
	public HashVO[] buildReportData(HashMap _condition) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("Select dept.name 申请机构 ,u.name 申请人,power.code 申请编码,flowstate 审批状态,releasestate 发布情况,power.applydate 申请日期 " + " From lbs_powerapply power,pub_user u,pub_corp_dept dept  "
				+ " where 1=1 " + " and power.applydept = dept.id and power.applier = u.id and flowstate <> '未提交' ");
		if (_condition.get("APPLIER") != null && !_condition.get("APPLIER").equals(""))
			sb.append(" and u.id in" + getInConditionWtihQuote((String) _condition.get("APPLIER")));
		if (_condition.get("TYPE") != null && !_condition.get("TYPE").equals(""))
			sb.append(" and TYPE in" + getInConditionWtihQuote((String) _condition.get("TYPE")));
		if (_condition.get("releasestate") != null && !_condition.get("releasestate").equals(""))
			sb.append(" and releasestate = '" + _condition.get("releasestate") + "'");
		sb.append(" order by 申请机构,申请日期");
		HashVO[] hashvo = new CommDMO().getHashVoArrayByDS(null, sb.toString());
		new ReportUtil().leftOuterJoin_YSMDFromDateTime(hashvo, "申请日期", "申请日期", "季");
		return hashvo;
	}

	@Override
	public String[] getGroupFieldNames() {
		return new String[] { "申请机构", "申请日期", "审批状态", "发布情况" };
	}

	@Override
	public String[] getSumFiledNames() {
		return new String[] { "数量" };
	}

	// 网格预置统计类型
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		BeforeHandGroupTypeVO[] beforeHandGroupTypeVOs = new BeforeHandGroupTypeVO[3];
		// 按申请机构
		beforeHandGroupTypeVOs[0] = new BeforeHandGroupTypeVO("所属机构-发布状态");
		beforeHandGroupTypeVOs[0].setColHeaderGroupFields(new String[] { "申请机构" });
		beforeHandGroupTypeVOs[0].setRowHeaderGroupFields(new String[] { "发布情况" });
		beforeHandGroupTypeVOs[0].setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });
		// 按季度
		beforeHandGroupTypeVOs[1] = new BeforeHandGroupTypeVO("所属机构-季度");
		beforeHandGroupTypeVOs[1].setColHeaderGroupFields(new String[] { "申请机构" });
		beforeHandGroupTypeVOs[1].setRowHeaderGroupFields(new String[] { "申请日期" });
		beforeHandGroupTypeVOs[1].setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });
		// 按审批结果
		beforeHandGroupTypeVOs[2] = new BeforeHandGroupTypeVO("所属机构-审批状态");
		beforeHandGroupTypeVOs[2].setColHeaderGroupFields(new String[] { "申请机构" });
		beforeHandGroupTypeVOs[2].setRowHeaderGroupFields(new String[] { "审批状态" });
		beforeHandGroupTypeVOs[2].setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });

		return beforeHandGroupTypeVOs;
	}

	// 图标类型统计类型
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		BeforeHandGroupTypeVO[] beforeHandGroupTypeVOs = new BeforeHandGroupTypeVO[3];
		// 按申请机构
		beforeHandGroupTypeVOs[0] = new BeforeHandGroupTypeVO("所属机构-发布状态");
		beforeHandGroupTypeVOs[0].setRowHeaderGroupFields(new String[] { "申请机构" });
		beforeHandGroupTypeVOs[0].setColHeaderGroupFields(new String[] { "发布情况" });
		beforeHandGroupTypeVOs[0].setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });
		// 按季度
		beforeHandGroupTypeVOs[1] = new BeforeHandGroupTypeVO("所属机构-季度");
		beforeHandGroupTypeVOs[1].setRowHeaderGroupFields(new String[] { "申请机构" });
		beforeHandGroupTypeVOs[1].setColHeaderGroupFields(new String[] { "申请日期" });
		beforeHandGroupTypeVOs[1].setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });
		// 按审批结果
		beforeHandGroupTypeVOs[2] = new BeforeHandGroupTypeVO("所属机构-审批状态");
		beforeHandGroupTypeVOs[2].setRowHeaderGroupFields(new String[] { "申请机构" });
		beforeHandGroupTypeVOs[2].setColHeaderGroupFields(new String[] { "审批状态" });
		beforeHandGroupTypeVOs[2].setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });
		return beforeHandGroupTypeVOs;
	}

	// 饼状
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Splider() {

		BeforeHandGroupTypeVO[] beforeHandGroupTypeVOs = new BeforeHandGroupTypeVO[3];
		// 按申请机构
		beforeHandGroupTypeVOs[0] = new BeforeHandGroupTypeVO("所属机构-发布状态");
		beforeHandGroupTypeVOs[0].setRowHeaderGroupFields(new String[] { "申请机构" });
		beforeHandGroupTypeVOs[0].setColHeaderGroupFields(new String[] { "发布情况" });
		beforeHandGroupTypeVOs[0].setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });
		// 按季度
		beforeHandGroupTypeVOs[1] = new BeforeHandGroupTypeVO("所属机构-季度");
		beforeHandGroupTypeVOs[1].setRowHeaderGroupFields(new String[] { "申请机构" });
		beforeHandGroupTypeVOs[1].setColHeaderGroupFields(new String[] { "申请日期" });
		beforeHandGroupTypeVOs[1].setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });
		// 按审批结果
		beforeHandGroupTypeVOs[2] = new BeforeHandGroupTypeVO("所属机构-审批状态");
		beforeHandGroupTypeVOs[2].setRowHeaderGroupFields(new String[] { "申请机构" });
		beforeHandGroupTypeVOs[2].setColHeaderGroupFields(new String[] { "审批状态" });
		beforeHandGroupTypeVOs[2].setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });
		return beforeHandGroupTypeVOs;
	}
	
	private String getInConditionWtihQuote(String valuewhitreg){
		String returnStr=null;
		
		if(valuewhitreg!=null&&!valuewhitreg.equals("")){
			if(valuewhitreg.endsWith(";")){
				valuewhitreg=valuewhitreg.substring(0,valuewhitreg.length()-1);
			}
			if(valuewhitreg.startsWith(";")){
				valuewhitreg=valuewhitreg.substring(1,valuewhitreg.length());
			}
			String[] temp=valuewhitreg.split(";");
			returnStr="(";
			for(String str:temp){
				returnStr=returnStr+"'"+str+"',";
			}
			if(returnStr.endsWith(","))
				returnStr=returnStr.substring(0,returnStr.length()-1);
			if(returnStr.startsWith(","))
				returnStr=returnStr.substring(1,returnStr.length());
		}
		returnStr+=")";
		return returnStr;
	}
}
