package cn.com.infostrategy.bs.mdata;

import java.util.HashMap;

/**
 * ��ѯ���(BillQueryPanel)���Զ���SQL�ഴ���е��е�SQL������!!
 * @author Administrator
 *
 */
public interface IBillQueryPanelSQLCreateIFC {

	/**
	 * ������������SQL����,����һ��SQL������һ�㶼�� "id in ('123','345','342')"������,������" id in (select ids from v_pub_sqlins where bno=20)"
	 * @param _itemValue
	 * @param _otherConditions
	 * @param _otherSQL
	 * @return
	 * @throws Exception
	 */
	public String getSQLByCondition(String _itemKey, String _itemValue, HashMap _allItemValues, HashMap _allItemSQLs, String _wholeSQL) throws Exception;
}
