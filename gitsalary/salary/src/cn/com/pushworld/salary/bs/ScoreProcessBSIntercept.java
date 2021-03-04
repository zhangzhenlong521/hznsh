package cn.com.pushworld.salary.bs;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.workflow.WorkFlowEngineBSIntercept;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTHashMap;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

/**
 * @author haoming
 * create by 2013-8-24
 */
public class ScoreProcessBSIntercept extends WorkFlowEngineBSIntercept {
	@Override
	public void afterWorkFlowEnd(String billType, String busiType, WFParVO secondCallVO, String loginuserid, BillVO billVO, String dealtype, WLTHashMap parMap) throws Exception {
		String itemids = billVO.getStringValue("itemids");
		String updateSql = "";
		if ("非正常结束".equals(dealtype)) {
			updateSql = "update sal_dept_check_score set status='已提交' where id in(" + TBUtil.getTBUtil().getInCondition(itemids) + ")";
		} else {
			updateSql = "update sal_dept_check_score set checkdratio = recheckdratio,checkscore = weights*(100-recheckdratio)/100,status='已提交' where id in(" + TBUtil.getTBUtil().getInCondition(itemids) + ")";
		}
		CommDMO commdmo = new CommDMO();
		commdmo.executeUpdateByDS(null, updateSql);
	}
}
