package com.pushworld.ipushgrc.bs.score.p010;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;

/**
 * 首页数据构造器-违规积分-按认定日期倒序【李春娟/2013-06-05】
 * @author lcj
 *
 */
public class ScoreDataBuilder implements DeskTopNewsDataBuilderIFC {

	public HashVO[] getNewData(String _userCode) throws Exception {
		String userid = new WLTInitContext().getCurrSession().getLoginUserId();
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select t1.*,t2.name as creatorname from v_score_user t1 left join pub_user t2 on t1.creator=t2.id where t1.state='未复议' and t1.effectdate is not null and t1.USERID=" + userid + " order by t1.effectdate desc");

		for (int i = 0; i < hvs.length; i++) {
			//String publishdate = hvs[i].getStringValue("publishdate");
			//String date = publishdate.substring(0, 4) + "年" + publishdate.substring(5, 7) + "月" + publishdate.substring(8) + "日";
			//hvs[i].setAttributeValue("showtitle", date + "违规积分" + hvs[i].getStringValue("score") + "分的认定通知书");

			String effectdate = hvs[i].getStringValue("effectdate");
			String date = effectdate.substring(0, 4) + "年" + effectdate.substring(5, 7) + "月" + effectdate.substring(8) + "日";
			hvs[i].setAttributeValue("showtitle", "您有一条违规积分将于" + date + "生效");

			hvs[i].setToStringFieldName("showtitle"); // 标题!!!
		}
		return hvs;
	}
}
