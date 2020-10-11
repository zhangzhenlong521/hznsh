package cn.com.pushworld.salary.bs.indexpage;

import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.pushworld.salary.to.SalaryFomulaParseUtil;

public class DeptDaiKuanReport implements DeskTopNewsDataBuilderIFC {

	public HashVO[] getNewData(String loginUserCode) throws Exception {
		HashVO[] hvo = new CommDMO().getHashVoArrayByDS(null, "select '完成值' 名称,checkdate 日期 ,reportvalue 值 from sal_dept_check_score where targetid = 251");
		//计算目标值
		SalaryFomulaParseUtil util = new SalaryFomulaParseUtil();
		HashVO basevo = new HashVO();
		List<HashVO> l = new ArrayList<HashVO>();
		for (int i = 0; i < hvo.length; i++) {
			l.add(hvo[i]);
			basevo.setAttributeValue("checkdate", hvo[i].getStringValue("日期"));
			Object obj = util.onExecute(util.getFoctorHashVO("对公贷款年初余额_全行"), basevo, new StringBuffer());
			HashVO vo = new HashVO();
			vo.setAttributeValue("名称", "目标值");
			vo.setAttributeValue("日期", hvo[i].getStringValue("日期"));
			vo.setAttributeValue("值", obj == null ? "0" : String.valueOf(obj));
			l.add(vo);
		}
//		HashVO yue_2 = new HashVO();
//		yue_2.setAttributeValue("名称", "完成值");
//		yue_2.setAttributeValue("日期", "2014-02");
//		yue_2.setAttributeValue("值", "25011.6");
//
//		HashVO yue_2_1 = new HashVO();
//		yue_2_1.setAttributeValue("名称", "目标值");
//		yue_2_1.setAttributeValue("日期", "2014-02");
//		yue_2_1.setAttributeValue("值", "68851");
//		l.add(yue_2);
//		l.add(yue_2_1);
		HashVO lastvo[] = l.toArray(new HashVO[0]);
		TBUtil.getTBUtil().sortHashVOs(lastvo, new String[][] { { "日期", "N", "N" } });
		return lastvo;
	}

}
