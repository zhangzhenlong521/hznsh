package com.pushworld.ipushgrc.bs.score;

import java.util.ArrayList;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.RemindIfc;
import cn.com.infostrategy.to.common.HashVO;

public class ScoreRemindData implements RemindIfc {

	/**
	 * 主页面上方登录时间后面的提示内容【李春娟/2013-06-06】
	 */
	public ArrayList getRemindDatas(String userId) throws Exception {
		if (userId == null || "".equals(userId.trim())) {
			return null;
		}
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select t1.*,t2.name as creatorname from v_score_user t1 left join pub_user t2 on t1.creator=t2.id where t1.state='未复议' and t1.effectdate is not null and t1.USERID=" + userId + " order by t1.effectdate desc");
		ArrayList list = new ArrayList();
		if (hvs == null || hvs.length == 0) {
			return list;
		}
		for (int i = 0; i < hvs.length; i++) {
			//String publishdate = hvs[i].getStringValue("publishdate");
			//String date = publishdate.substring(0, 4) + "年" + publishdate.substring(5, 7) + "月" + publishdate.substring(8) + "日";
			//hvs[i].setAttributeValue("showtitle", date + "违规积分" + hvs[i].getStringValue("score") + "分的认定通知书");

			String effectdate = hvs[i].getStringValue("effectdate");
			String date = effectdate.substring(0, 4) + "年" + effectdate.substring(5, 7) + "月" + effectdate.substring(8) + "日";
			list.add("您有一条违规积分将于" + date + "生效");
		}
		return list;
	}

}
