package cn.com.pushworld.salary.ui.posteval.p070;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ��λ��ֵ�����÷���ϸ��ѯ�����/2013-11-06��
 * @author lcj
 *
 */
public class PostEvalScoreDetailWKPanel extends AbstractWorkPanel {
	private BillListPanel listPanel;

	public void initialize() {
		listPanel = new BillListPanel("SAL_POST_EVAL_SCORE_LCJ_Q01");
		this.add(listPanel);
	}
}
