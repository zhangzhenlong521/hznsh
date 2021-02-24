package com.pushworld.ipushgrc.bs.score.p010;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;

/**
 * ��ҳ���ݹ�����-Υ�����-���϶����ڵ������/2013-06-05��
 * @author lcj
 *
 */
public class ScoreDataBuilder implements DeskTopNewsDataBuilderIFC {

	public HashVO[] getNewData(String _userCode) throws Exception {
		String userid = new WLTInitContext().getCurrSession().getLoginUserId();
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select t1.*,t2.name as creatorname from v_score_user t1 left join pub_user t2 on t1.creator=t2.id where t1.state='δ����' and t1.effectdate is not null and t1.USERID=" + userid + " order by t1.effectdate desc");

		for (int i = 0; i < hvs.length; i++) {
			//String publishdate = hvs[i].getStringValue("publishdate");
			//String date = publishdate.substring(0, 4) + "��" + publishdate.substring(5, 7) + "��" + publishdate.substring(8) + "��";
			//hvs[i].setAttributeValue("showtitle", date + "Υ�����" + hvs[i].getStringValue("score") + "�ֵ��϶�֪ͨ��");

			String effectdate = hvs[i].getStringValue("effectdate");
			String date = effectdate.substring(0, 4) + "��" + effectdate.substring(5, 7) + "��" + effectdate.substring(8) + "��";
			hvs[i].setAttributeValue("showtitle", "����һ��Υ����ֽ���" + date + "��Ч");

			hvs[i].setToStringFieldName("showtitle"); // ����!!!
		}
		return hvs;
	}
}
