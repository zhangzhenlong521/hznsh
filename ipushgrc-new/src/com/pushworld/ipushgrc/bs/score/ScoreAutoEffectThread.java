package com.pushworld.ipushgrc.bs.score;

import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.to.common.TBUtil;

/**
 *  违规积分如到期未进行复议申请，则自动生效【李春娟/2013-05-17】
 *  在菜单 平台配置-》系统设置-》系统任务定义 中进行配置
 */
public class ScoreAutoEffectThread implements WLTJobIFC {

	public String run() throws Exception {
		new ScoreBSUtil().effectScoreBySqlCondition(" state = '未复议' and effectdate<='" + TBUtil.getTBUtil().getCurrDate() + "' ");//【李春娟/2014-11-04】
		return "违规积分申请复议到期,自动生效!";
	}
}
