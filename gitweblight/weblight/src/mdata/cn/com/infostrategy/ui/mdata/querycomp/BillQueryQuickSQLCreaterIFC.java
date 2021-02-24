package cn.com.infostrategy.ui.mdata.querycomp;

import cn.com.infostrategy.ui.mdata.BillQueryPanel;

/**
 * 查询面板的SQL生成器.
 * @author xch
 *
 */
public interface BillQueryQuickSQLCreaterIFC {

	/**
	 * 取得快速查询面板的SQL.
	 * @param _queryPanel
	 * @return
	 */
	public String getQuickSQL(BillQueryPanel _queryPanel); //
	
	
}
