package cn.com.pushworld.salary.bs.indexpage;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC2;
import cn.com.infostrategy.to.common.HashVO;

public class BuLiangDaiKuanLVReport implements DeskTopNewsDataBuilderIFC2 {

	public HashVO[] getNewData(String loginUserCode, HashMap _map) throws Exception {
		HashVO[] hvo = new CommDMO().getHashVoArrayByDS(null, "select '����������' ����,'��һ����' ���� ,reportvalue ֵ from sal_dept_check_score t1 left join sal_target_check_log t2 on t1.logid = t2.id  where t1.targetid = 256 order by t2.checkdate");
		HashVO rtvo = new HashVO();
		rtvo.setAttributeValue("����", "����������"); //
		rtvo.setAttributeValue("X��", "ʵ��ֵ(%)"); //
		rtvo.setAttributeValue("ʵ��ֵ", hvo[hvo.length - 1].getStringValue("ֵ")); //
		rtvo.setAttributeValue("��Сֵ", 0d); //
		rtvo.setAttributeValue("����ֵ", 5d); //
		rtvo.setAttributeValue("����ֵ", 8d); //
		rtvo.setAttributeValue("���ֵ", 15d); //

		HashVO rtvo_1 = new HashVO();
		rtvo_1.setAttributeValue("����", "��������ҵ���ռ��"); //
		rtvo_1.setAttributeValue("X��", "3��(%)"); //
		rtvo_1.setAttributeValue("ʵ��ֵ", 30.2); //
		rtvo_1.setAttributeValue("��Сֵ", 0d); //
		rtvo_1.setAttributeValue("����ֵ", 60); //
		rtvo_1.setAttributeValue("����ֵ", 20d); //
		rtvo_1.setAttributeValue("���ֵ", 100d); //

		return new HashVO[] { rtvo, rtvo_1 };
	}

}
