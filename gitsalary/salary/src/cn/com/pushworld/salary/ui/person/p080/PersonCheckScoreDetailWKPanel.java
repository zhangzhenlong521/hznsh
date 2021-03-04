package cn.com.pushworld.salary.ui.person.p080;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * Ա��������ϸ ��ѯ
 * 
 * @author Administrator
 * 
 */
public class PersonCheckScoreDetailWKPanel extends AbstractWorkPanel {
	private BillListPanel bl = null;

	public void initialize() {
		bl = new BillListPanel("SAL_PERSON_CHECK_SCORE_CODE1");
		bl.setDataFilterCustCondition("scoretype='�ֶ����'");
		//��������Ĭ��ֵΪ��ǰ��������  Gwang 2013-08-21
		QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel)bl.getQuickQueryPanel().getCompentByKey("checkdate");
		String checkDate = new SalaryUIUtil().getCheckDate();
		dateRef.setValue(checkDate);
		this.add(bl);
	}

}
