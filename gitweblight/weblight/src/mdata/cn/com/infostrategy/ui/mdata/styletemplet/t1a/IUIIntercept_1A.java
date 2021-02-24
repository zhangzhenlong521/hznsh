/**************************************************************************
 * $RCSfile: IUIIntercept_1A.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t1a;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;

/**
 * 主子表的前端Intercept!!
 * @author user
 *
 */
public interface IUIIntercept_1A {
	public void beforeInsert(BillCardPanel _billCardPanel,BillVO _dealVO) throws Exception;

	public void afterInsert(BillCardPanel _billCardPanel, BillVO _returnVO) throws Exception;

	public void beforeUpdate(BillCardPanel _billCardPanel,BillVO _dealVO) throws Exception;

	public void afterUpdate(BillCardPanel _billCardPanel, BillVO _returnVO) throws Exception;

	public void beforeDelete(BillCardPanel _billCardPanel,BillVO _dealVO) throws Exception;

	public void afterDelete(BillCardPanel _billCardPanel) throws Exception;
}
