package cn.com.infostrategy.bs.mdata;

import java.util.HashMap;

/**
 * 查询面板(BillQueryPanel)中自定义SQL类创建中的中的SQL构造器!!
 * @author Administrator
 *
 */
public interface IBillQueryPanelSQLCreateIFC {

	/**
	 * 根据条件创建SQL条件,返回一个SQL条件，一般都是 "id in ('123','345','342')"的样子,或者是" id in (select ids from v_pub_sqlins where bno=20)"
	 * @param _itemValue
	 * @param _otherConditions
	 * @param _otherSQL
	 * @return
	 * @throws Exception
	 */
	public String getSQLByCondition(String _itemKey, String _itemValue, HashMap _allItemValues, HashMap _allItemSQLs, String _wholeSQL) throws Exception;
}
