package com.pushworld.ipushgrc.ui.cmpscore.p020;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowUIIntercept2;
/**
 * 违规积分复议流程结束拦截器
 * @author yinliang
 * @since 2011.12.26
 */
public class ScoreEndWFUIIntercept implements WorkFlowUIIntercept2{

	
	public void afterWorkFlowEnd(WorkFlowProcessPanel panel, BillVO billvo)
			throws Exception {
		//通过当前信息的流程ID 取得当前流程的运行状态以及是否正常结束情况，来到这一步，status肯定是end了。。。endtype也不可能为空，肯定只有正常结束和非正常结束两种情况了。。。
		String sql_flow = "select endtype from pub_wf_prinstance where Id = " + billvo.getStringValue("wfprinstanceid") ;
		String flow_endtype = UIUtil.getStringValueByDS(null, sql_flow);
		String sql_flowstate = "" ;
		String id = billvo.getStringValue("id") ;
		if(flow_endtype.equals("正常结束")) //流程正常结束,状态变为等待裁定
			sql_flowstate = "update "+ billvo.getSaveTableName() +" set sendstate = '5' where id = "+id;  
		else{ //未正常结束，表示没批准，直接进入到已复议中
			sql_flowstate = "update "+ billvo.getSaveTableName() +" set sendstate = '4'," +
					"resultscore = scorelost where id = "+billvo.getStringValue("id");
			 //计算总分
			String totalscore = billvo.getStringValue("totalscore");
				if("".equals(totalscore) || totalscore == null){  //如果之前没有过扣分
					UIUtil.executeUpdateByDS(null,"update cmp_score_record set totalscore = scorelost where id = "+id );
				}else{
					UIUtil.executeUpdateByDS(null,"update cmp_score_record set totalscore = totalscore + scorelost where id = "+id );
				}
			}
		UIUtil.executeUpdateByDS(null,sql_flowstate );
		}
}
