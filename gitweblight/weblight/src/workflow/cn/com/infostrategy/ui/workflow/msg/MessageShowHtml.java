package cn.com.infostrategy.ui.workflow.msg;

import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 收藏夹传阅红头文件关联类接口 【杨科/2012-12-14】
 */

public interface MessageShowHtml {

	public boolean onBillListHtml(BillListPanel billListPanel, String pvalue, String templetcode);
	
}
