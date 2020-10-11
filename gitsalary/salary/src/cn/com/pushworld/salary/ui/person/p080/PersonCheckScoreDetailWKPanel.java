package cn.com.pushworld.salary.ui.person.p080;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * 员工考核明细 查询
 * 
 * @author Administrator
 * 
 */
public class PersonCheckScoreDetailWKPanel extends AbstractWorkPanel {
	private BillListPanel bl = null;

	public void initialize() {
		bl = new BillListPanel("SAL_PERSON_CHECK_SCORE_CODE1");
		bl.setDataFilterCustCondition("scoretype='手动打分'");
		//设置日期默认值为当前考核日期  Gwang 2013-08-21
		QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel)bl.getQuickQueryPanel().getCompentByKey("checkdate");
		String checkDate = new SalaryUIUtil().getCheckDate();
		dateRef.setValue(checkDate);
		this.add(bl);
	}

}
