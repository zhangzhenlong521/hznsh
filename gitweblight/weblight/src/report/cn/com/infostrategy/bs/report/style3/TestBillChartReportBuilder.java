package cn.com.infostrategy.bs.report.style3;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

public class TestBillChartReportBuilder extends AbstractStyleReport_3_BuildDataAdapter {

	@Override
	public HashVO[] buildDataByCondition(HashMap _condition, CurrLoginUserVO userVO) throws Exception {
		TBUtil tbUtil = new TBUtil();
		String depart = (String) _condition.get("dept");

		CommDMO commDMO = new CommDMO();
		//HashVO[] hashvo = commDMO.getHashVoArrayByDS(null, " select writedeptname as 机构名称,rank as 风险等级,avg(a.id) as 数量 from cmp_wf_riskpoint a,cmp_cmpfile b  where b.id=a.cmpfile_id and rank is not null and writedeptname is not null  group by writedeptname,rank  ");
		HashVO[] hashvo = commDMO.getHashVoArrayByDS(null, " select writedeptname as 机构名称,rank as 风险等级,a.id as 数量,a.cmpfile_code as 体系文件编码,a.cmpfile_name as 体系文件名称 from cmp_wf_riskpoint a,cmp_cmpfile b  where b.id=a.cmpfile_id and rank is not null and writedeptname is not null");
		return hashvo;
	}

	@Override
	public String getColHeadName() {
		return "风险等级";
	}

	@Override
	public String getRowHeadName() {
		return "机构名称";
	}

	@Override
	public String getComputeItemName() {
		// TODO Auto-generated method stub
		return "数量";
	}

	@Override
	public String getComputeType() {
		// TODO Auto-generated method stub
		return StyleReport_3_BuildDataIFC.SUM;
	}
	
}
