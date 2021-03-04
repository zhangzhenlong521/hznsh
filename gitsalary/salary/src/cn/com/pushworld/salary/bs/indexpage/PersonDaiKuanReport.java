package cn.com.pushworld.salary.bs.indexpage;

import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.pushworld.salary.to.SalaryFomulaParseUtil;
/*
 * 个人贷款
 */
public class PersonDaiKuanReport implements DeskTopNewsDataBuilderIFC {

	public HashVO[] getNewData(String loginUserCode) throws Exception {
		HashVO[] hvo = new CommDMO().getHashVoArrayByDS(null, "select '完成值' 名称,checkdate 日期 ,reportvalue 值 from sal_dept_check_score where targetid = 259");
		//计算目标值
		SalaryFomulaParseUtil util = new SalaryFomulaParseUtil();
		HashVO basevo = new HashVO();
		List<HashVO> l = new ArrayList<HashVO>();
		for (int i = 0; i < hvo.length; i++) {
			if(Float.parseFloat(hvo[i].getStringValue("值",""))<0){
				hvo[i].setAttributeValue("值", "27008");
			}
			l.add(hvo[i]);
			basevo.setAttributeValue("checkdate", hvo[i].getStringValue("日期"));
			Object obj = util.onExecute(util.getFoctorHashVO("全行贷款任务目标值"), basevo, new StringBuffer());
			HashVO vo = new HashVO();
			vo.setAttributeValue("名称", "目标值");
			vo.setAttributeValue("日期", hvo[i].getStringValue("日期"));
			vo.setAttributeValue("值", obj == null ? "0" : Float.parseFloat(String.valueOf(obj))/3);
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
//		yue_2_1.setAttributeValue("值", "20266");
//		l.add(yue_2);
//		l.add(yue_2_1);
		HashVO lastvo[] = l.toArray(new HashVO[0]);
		TBUtil.getTBUtil().sortHashVOs(lastvo, new String[][] { { "日期", "N", "N" } });
		return lastvo;
	}

}
