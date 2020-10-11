package cn.com.pushworld.salary.bs.indexpage;

import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.pushworld.salary.to.SalaryFomulaParseUtil;

public class DeptCunKuanReport implements DeskTopNewsDataBuilderIFC {

	public HashVO[] getNewData(String loginUserCode) throws Exception {
		HashVO[] hvo = new CommDMO().getHashVoArrayByDS(null, "select '���ֵ' ����,checkdate ���� ,reportvalue ֵ from sal_dept_check_score where targetid = 28 ");
		//����Ŀ��ֵ
		SalaryFomulaParseUtil util = new SalaryFomulaParseUtil();
		HashVO basevo = new HashVO();
		List<HashVO> l = new ArrayList<HashVO>();
		for (int i = 0; i < hvo.length; i++) {
			l.add(hvo[i]);
			basevo.setAttributeValue("checkdate", hvo[i].getStringValue("����"));
			Object obj = util.onExecute(util.getFoctorHashVO("������Ŀ��ֵ_ȫ��"), basevo, new StringBuffer());
			HashVO vo = new HashVO();
			vo.setAttributeValue("����", "Ŀ��ֵ");
			vo.setAttributeValue("����", hvo[i].getStringValue("����"));
			vo.setAttributeValue("ֵ", obj == null ? "0" : String.valueOf(obj));
			l.add(vo);
		}
//		HashVO yue_2 = new HashVO();
//		yue_2.setAttributeValue("����", "���ֵ");
//		yue_2.setAttributeValue("����", "2014-02");
//		yue_2.setAttributeValue("ֵ", "28010.2");
//
//		HashVO yue_2_1 = new HashVO();
//		yue_2_1.setAttributeValue("����", "Ŀ��ֵ");
//		yue_2_1.setAttributeValue("����", "2014-02");
//		yue_2_1.setAttributeValue("ֵ", "70500");
//		l.add(yue_2);
//		l.add(yue_2_1);
		HashVO lastvo[] = l.toArray(new HashVO[0]);
		TBUtil.getTBUtil().sortHashVOs(lastvo, new String[][] { { "����", "N", "N" } });
		return lastvo;
	}
}
