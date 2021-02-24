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
		//HashVO[] hashvo = commDMO.getHashVoArrayByDS(null, " select writedeptname as ��������,rank as ���յȼ�,avg(a.id) as ���� from cmp_wf_riskpoint a,cmp_cmpfile b  where b.id=a.cmpfile_id and rank is not null and writedeptname is not null  group by writedeptname,rank  ");
		HashVO[] hashvo = commDMO.getHashVoArrayByDS(null, " select writedeptname as ��������,rank as ���յȼ�,a.id as ����,a.cmpfile_code as ��ϵ�ļ�����,a.cmpfile_name as ��ϵ�ļ����� from cmp_wf_riskpoint a,cmp_cmpfile b  where b.id=a.cmpfile_id and rank is not null and writedeptname is not null");
		return hashvo;
	}

	@Override
	public String getColHeadName() {
		return "���յȼ�";
	}

	@Override
	public String getRowHeadName() {
		return "��������";
	}

	@Override
	public String getComputeItemName() {
		// TODO Auto-generated method stub
		return "����";
	}

	@Override
	public String getComputeType() {
		// TODO Auto-generated method stub
		return StyleReport_3_BuildDataIFC.SUM;
	}
	
}
