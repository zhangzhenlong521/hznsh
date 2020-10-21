package com.pushworld.ipushlbs.ui.lawreport.p020;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

/**
 * 非格式合同打印统计
 * 
 * @author yinliang
 * @since 2011.12.19
 */
public class UnFormatPrint2 extends MultiLevelReportDataBuilderAdapter {

	@Override
	public HashVO[] buildReportData(HashMap _condition) throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append("Select de.Name 机构名称,ls.Name 合同名称,ld.Type 审查类型,ld.pdate 打印日期,ld.pcount 打印张数 " + "From lbs_dept_print_contact ld ,pub_corp_dept de , lbs_unstdfile ls "
				+ "Where ld.dept_id = de.Id And ld.contact_id = ls.Id and ld.type = '2'" // 直接写死了，不好。。
		);
		if (_condition.get("DEALDOC_NAME") != null && !_condition.get("DEALDOC_NAME").equals(""))
			sb.append(" and ld.contact_id in " +  getInConditionWtihQuote((String) _condition.get("DEALDOC_NAME")));
		if (_condition.get("SENDORG") != null && !_condition.get("SENDORG").equals(""))
			sb.append(" and ld.dept_id in " +getInConditionWtihQuote( (String) _condition.get("SENDORG")));
		sb.append(" order by 机构名称,打印日期");
		HashVO[] hashvo = new CommDMO().getHashVoArrayByDS(null, sb.toString());
		new ReportUtil().leftOuterJoin_YSMDFromDateTime(hashvo, "打印时间", "打印日期", "季");
		return hashvo;
	}

	// 组区域
	@Override
	public String[] getGroupFieldNames() {
		return new String[] { "机构名称", "合同名称", "审查类型", "打印日期" };
	}

	// 结果区域
	@Override
	public String[] getSumFiledNames() {
		return new String[] { "合同名称", "打印张数" };
	}

	// 网格预置统计类型
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		BeforeHandGroupTypeVO[] beforeHandGroupTypeVOs = new BeforeHandGroupTypeVO[2];
		// 按合同性质
		beforeHandGroupTypeVOs[0] = new BeforeHandGroupTypeVO("机构名称-合同名称");
		beforeHandGroupTypeVOs[0].setRowHeaderGroupFields(new String[] { "机构名称" });
		beforeHandGroupTypeVOs[0].setColHeaderGroupFields(new String[] { "合同名称" });
		beforeHandGroupTypeVOs[0].setComputeGroupFields(new String[][] { { "打印张数", BeforeHandGroupTypeVO.SUM } });
		// 按季度
		beforeHandGroupTypeVOs[1] = new BeforeHandGroupTypeVO("合同名称-季度");
		beforeHandGroupTypeVOs[1].setColHeaderGroupFields(new String[] { "合同名称" });
		beforeHandGroupTypeVOs[1].setRowHeaderGroupFields(new String[] { "打印时间" });
		beforeHandGroupTypeVOs[1].setComputeGroupFields(new String[][] { { "打印张数", BeforeHandGroupTypeVO.SUM } });

		return beforeHandGroupTypeVOs;
	}

	// 图标类型统计类型
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		BeforeHandGroupTypeVO[] beforeHandGroupTypeVOs = new BeforeHandGroupTypeVO[2];
		// 按合同性质
		beforeHandGroupTypeVOs[0] = new BeforeHandGroupTypeVO("机构名称-合同名称");
		beforeHandGroupTypeVOs[0].setRowHeaderGroupFields(new String[] { "机构名称" });
		beforeHandGroupTypeVOs[0].setColHeaderGroupFields(new String[] { "合同名称" });
		beforeHandGroupTypeVOs[0].setComputeGroupFields(new String[][] { { "打印张数", BeforeHandGroupTypeVO.SUM } });
		// 按季度
		beforeHandGroupTypeVOs[1] = new BeforeHandGroupTypeVO("合同名称-季度");
		beforeHandGroupTypeVOs[1].setRowHeaderGroupFields(new String[] { "合同名称" });
		beforeHandGroupTypeVOs[1].setColHeaderGroupFields(new String[] { "打印时间" });
		beforeHandGroupTypeVOs[1].setComputeGroupFields(new String[][] { { "打印张数", BeforeHandGroupTypeVO.SUM } });
		return beforeHandGroupTypeVOs;
	}

	// 饼状
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Splider() {
		BeforeHandGroupTypeVO[] beforeHandGroupTypeVOs = new BeforeHandGroupTypeVO[2];
		// 按合同性质
		beforeHandGroupTypeVOs[0] = new BeforeHandGroupTypeVO("机构名称-合同名称");
		beforeHandGroupTypeVOs[0].setRowHeaderGroupFields(new String[] { "机构名称" });
		beforeHandGroupTypeVOs[0].setColHeaderGroupFields(new String[] { "合同名称" });
		beforeHandGroupTypeVOs[0].setComputeGroupFields(new String[][] { { "打印张数", BeforeHandGroupTypeVO.SUM } });
		// 按季度
		beforeHandGroupTypeVOs[1] = new BeforeHandGroupTypeVO("合同名称-季度");
		beforeHandGroupTypeVOs[1].setRowHeaderGroupFields(new String[] { "合同名称" });
		beforeHandGroupTypeVOs[1].setColHeaderGroupFields(new String[] { "打印时间" });
		beforeHandGroupTypeVOs[1].setComputeGroupFields(new String[][] { { "打印张数", BeforeHandGroupTypeVO.SUM } });
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
