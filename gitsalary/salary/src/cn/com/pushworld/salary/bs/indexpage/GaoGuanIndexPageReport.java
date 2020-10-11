package cn.com.pushworld.salary.bs.indexpage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC2;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.pushworld.salary.to.SalaryFomulaParseUtil;

public class GaoGuanIndexPageReport implements DeskTopNewsDataBuilderIFC2 {

	public HashVO[] getNewData(String loginUserCode, HashMap param) throws Exception {
		String targetName = "";
		String targettype = "";
		String calcType = "";
		String mubiao = "";
		if (param != null) {
			String targetName_1 = (String) param.get("ָ������"); //��������
			if (!TBUtil.isEmpty(targetName_1)) {
				targetName = targetName_1;
			}
			String targettype_1 = (String) param.get("ָ������"); //���Ŷ���,Ա������ 
			if (!TBUtil.isEmpty(targettype_1)) {
				targettype = targettype_1;
			}
			String calcType_1 = (String) param.get("��������"); //��� ��ƽ�� ���ֵ ��Сֵ
			if (!TBUtil.isEmpty(calcType_1)) {
				calcType = calcType_1;
			}
			String mubiao_1 = (String) param.get("Ŀ������");
			if (!TBUtil.isEmpty(mubiao_1)) {
				mubiao = mubiao_1;
			}
		}
		CommDMO dmo = new CommDMO();
		HashVO hvo[] = null;
		if ("���Ŷ���".equals(targettype) || "����".equals(targettype)) {
			hvo = dmo.getHashVoArrayByDS(null, "select '���ֵ' ����,t1.checkdate ����,t1.reportvalue ֵ from sal_dept_check_score t1 left join sal_target_list t2 on t1.targetid = t2.id left join sal_target_check_log t3 on t1.logid = t3.id where t2.name='" + targetName + "' order by t3.checkdate");
		} else if ("Ա������".equals(targettype) || "Ա��".equals(targettype)) {

		}
		if (TBUtil.isEmpty(mubiao)) {
			return hvo;
		}
		List<HashVO> l = new ArrayList<HashVO>();
		SalaryFomulaParseUtil util = new SalaryFomulaParseUtil();
		HashVO basevo = new HashVO();
		for (int i = 0; i < hvo.length; i++) {
			l.add(hvo[i]);
			basevo.setAttributeValue("checkdate", hvo[i].getStringValue("����"));
			Object obj = util.onExecute(util.getFoctorHashVO(mubiao), basevo, new StringBuffer());
			HashVO vo = new HashVO();
			vo.setAttributeValue("����", "Ŀ��ֵ");
			vo.setAttributeValue("����", hvo[i].getStringValue("����"));
			vo.setAttributeValue("ֵ", obj == null ? "0" : String.valueOf(obj));
			l.add(vo);
		}

		return l.toArray(new HashVO[0]);
	}

}
