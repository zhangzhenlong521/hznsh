package com.pushworld.ipushlbs.ui.lawreport.p030;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

/**
 * 主诉案件--多维报表--组织数据
 * 
 * @author wupeng
 * 
 */
public class LawCaseMultiReportQueryData extends MultiLevelReportDataBuilderAdapter {

	@SuppressWarnings("unchecked")
	@Override
	public HashVO[] buildReportData(HashMap condition) throws Exception {
		ReportUtil util = new ReportUtil();
		TBUtil tb = new TBUtil();
		StringBuffer sb = new StringBuffer("Select Law.Sendproceeding 案件数量,Law.Casetype 风险分类, pro.Name 涉及业务类型, Dept.Name 诉讼主体,dict.Name 诉讼类型,law.CREATEDATE 年份,law.CREATEDATE 季度"
				+ "  From Lbs_Case_Self Law, Pub_Corp_Dept Dept,pub_comboboxdict pro,pub_comboboxdict dict"
				+ "  Where Dept.Id = Law.Lawsuitmain and pro.id=law.BUSITYPENAME and pro.type='案件_业务类型' and dict.id=law.lawsuittype and dict.type='案件_诉讼类型'  ");

		if (condition != null) {
			if (condition.get("LAWSUITMAIN") != null && !condition.get("LAWSUITMAIN").equals(""))
				sb.append("  and lawsuitmain in ").append(this.getInConditionWtihQuote((String) condition.get("LAWSUITMAIN")));

			if (condition.get("ENDTYPE") != null && !condition.get("ENDTYPE").equals(""))
				sb.append(" and endtype in ").append(this.getInConditionWtihQuote((String) condition.get("ENDTYPE")));
			else
				sb.append("  and endtype != '").append("未审批").append("'");

			if (condition.get("CREATEDATE") != null && !condition.get("CREATEDATE").equals("")) {
				String[] dates = tb.convertComp_dateTimeFormat((String) condition.get("CREATEDATE")).split(";");
				sb.append(" and law.CREATEDATE >='").append(dates[0]).append("' and law.CREATEDATE <='").append(dates[1]).append("'");
			}
		}
		HashVO[] vos = new CommDMO().getHashVoArrayByDS(null, sb.toString());
		util.leftOuterJoin_YSMDFromDateTime(vos, "年份", "年份", "年");
		util.leftOuterJoin_YSMDFromDateTime(vos, "季度", "季度", "季");
		return vos;
	}

	@Override
	public String[] getGroupFieldNames() {

		return new String[] { "涉及业务类型" };
	}

	@Override
	public String[] getSumFiledNames() {

		return new String[] { "案件数量" , "数量"};
	}

	@Override
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		BeforeHandGroupTypeVO[] vos = new BeforeHandGroupTypeVO[5];
		vos[0] = new BeforeHandGroupTypeVO("涉及业务类型-风险分类");
		vos[0].setColHeaderGroupFields(new String[] { "涉及业务类型" });
		vos[0].setRowHeaderGroupFields(new String[] { "风险分类" });
		vos[0].setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });

		vos[1] = new BeforeHandGroupTypeVO("诉讼主体-诉讼类型");
		vos[1].setColHeaderGroupFields(new String[] { "诉讼主体" });
		vos[1].setRowHeaderGroupFields(new String[] { "诉讼类型" });
		vos[1].setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });

		vos[2] = new BeforeHandGroupTypeVO("涉及业务类型-诉讼主体");
		vos[2].setColHeaderGroupFields(new String[] { "诉讼主体" });// y轴
		vos[2].setRowHeaderGroupFields(new String[] { "涉及业务类型" });// x轴
		vos[2].setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });

		vos[3] = new BeforeHandGroupTypeVO("案件数量-季报");
		vos[3].setColHeaderGroupFields(new String[] { "诉讼主体" });
		vos[3].setRowHeaderGroupFields(new String[] { "季度" });
		vos[3].setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });

		vos[4] = new BeforeHandGroupTypeVO("案件数量-年报");
		vos[4].setColHeaderGroupFields(new String[] { "诉讼主体" });
		vos[4].setRowHeaderGroupFields(new String[] { "年份" });
		vos[4].setComputeGroupFields(new String[][] { { "案件数量", BeforeHandGroupTypeVO.COUNT } });

		return vos;
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
