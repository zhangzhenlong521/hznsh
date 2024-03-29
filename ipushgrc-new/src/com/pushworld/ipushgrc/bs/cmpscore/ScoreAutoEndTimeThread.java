package com.pushworld.ipushgrc.bs.cmpscore;

import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
/**
 *  如到期未进行复议申请，则自动变为已复议
 */
public class ScoreAutoEndTimeThread  implements WLTJobIFC{

	CommDMO commdmo = new CommDMO();
	public String run() throws Exception {	
		String date =new TBUtil().getCurrDate();
		//将已通知状态下的、并且截止时间已经比当前时间小的 违规积分通知的状态自动修改为已复议。
		// 更新状态，并将裁定扣分的值定为应扣分
		
			List list_sql = new ArrayList();
			String _sql = "select id,totalscore from cmp_score_record where sendstate = '2' and applyendtime <'" + date + "'" ;
			HashVO[] vos = commdmo.getHashVoArrayByDS(null, _sql);
			for( HashVO vo : vos ){
				//1.首先计算总分
				if("".equals(vo.getStringValue("totalscore")) || vo.getStringValue("totalscore") == null){  //如果之前没有过扣分
					list_sql.add("update cmp_score_record set totalscore = scorelost where id = '"+vo.getStringValue("id")+"'");
				}else{
					list_sql.add("update cmp_score_record set totalscore = totalscore + scorelost where id = '"+vo.getStringValue("id")+"'");
				}
				//2.更新状态
				list_sql.add("update cmp_score_record set sendstate='4' ,resultscore = scorelost where id = '"+vo.getStringValue("id")+"'");
			}
			commdmo.executeBatchByDS(null, list_sql);
		
		return "申请复议到期，自动更新为已复议！";
	}

}
