package cn.com.infostrategy.ui.mdata.cardcomp;

import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * Office控件生成书签替换,返回一个HashMap
 * @author xch
 *
 */
public interface RefDialog_Office_BookMarkCreaterIFC {

	/**
	 * 
	 * @param _billPanel
	 * @return
	 */
	public OfficeCompentControlVO createBookMarkReplaceMap(BillPanel _billPanel);
}
