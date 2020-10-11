/**************************************************************************
 * $RCSfile: IUIIntercept_2B.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t2b;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 主子表的前端Intercept!!
 * @author user
 *
 */
public interface IUIIntercept_2B {
	//删除前做的动作
	public void actionBeforeDelete(BillListPanel _billlistPanel, int _delerow) throws Exception;

	//新增前做的动作
	public void actionBeforeInsert(BillListPanel _billlistPanel, int _newrow) throws Exception;

	//修改前做的动作
	public void actionBeforeUpdate(BillListPanel _billlistPanel, int _updatedrow, String _itemkey) throws Exception; //

	//提交前做的校验处理!!!!参数分别是新增的,删除的,修改的数据!!!
	public void dealBeforeCommit(BillListPanel _billlistPanel, BillVO[] _insertobjs, BillVO[] _deleteobjs, BillVO[] _updateobjs) throws Exception; //

	//提交后的后续处理..
	public void dealAfterCommit(BillListPanel _billlistPanel, BillVO[] _insertobjs, BillVO[] _deleteobjs, BillVO[] _updateobjs) throws Exception; //

}
