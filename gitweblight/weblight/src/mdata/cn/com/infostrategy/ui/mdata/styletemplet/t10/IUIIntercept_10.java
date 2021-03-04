/**************************************************************************
 * $RCSfile: IUIIntercept_10.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t10;

import cn.com.infostrategy.to.mdata.AggBillVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 主子表的前端Intercept!!
 * @author user
 *
 */
public interface IUIIntercept_10 {

	//主表删除前做的动作
	public void actionBeforeDelete_parent(BillListPanel _parentbilllistPanel, int _delerow) throws Exception;

	//子表删除前做的动作,_tabIndex是子表各页签的顺序,从1开始,分别是1,2,3,4,5
	public void actionBeforeDelete_child(int _tabIndex, BillListPanel _childbilllistPanel, int _delerow) throws Exception;

	//主表新增后做的动作,新增时主表是卡片
	public void actionAfterInsert_parent(BillCardPanel _billcardPanel) throws Exception;

	//子表新增后做的动作,新增时子表是列表!!,_tabIndex是子表各页签的顺序,从1开始,分别是1,2,3,4,5
	public void actionAfterInsert_child(int _tabIndex, BillListPanel _billListPanel, int _newrow) throws Exception;

	//主表修改后做的动作,修改时主表是卡片!!!
	public void actionAfterUpdate_parent(BillCardPanel _billcardPanel, String _itemkey) throws Exception; //

	//子表修改后做的动作!!,_tabIndex是子表各页签的顺序,从1开始,分别是1,2,3,4,5
	public void actionAfterUpdate_child(int _tabIndex, BillListPanel _billListPanel, String _itemkey, int _updatedrow) throws Exception; //

	//新增前处理
	public void dealCommitBeforeInsert(cn.com.infostrategy.ui.mdata.styletemplet.t09.AbstractStyleWorkPanel_09 _frame, AggBillVO _insertobjs) throws Exception; //

	//新增后处理
	public void dealCommitAfterInsert(cn.com.infostrategy.ui.mdata.styletemplet.t09.AbstractStyleWorkPanel_09 _frame, AggBillVO _insertobjs); //

	//删除前处理
	public void dealCommitBeforeDelete(cn.com.infostrategy.ui.mdata.styletemplet.t09.AbstractStyleWorkPanel_09 _frame, AggBillVO _insertobjs) throws Exception; //

	//删除后处理
	public void dealCommitAfterDelete(cn.com.infostrategy.ui.mdata.styletemplet.t09.AbstractStyleWorkPanel_09 _frame, AggBillVO _insertobjs); //

	//修改前处理
	public void dealCommitBeforeUpdate(cn.com.infostrategy.ui.mdata.styletemplet.t09.AbstractStyleWorkPanel_09 _frame, AggBillVO _insertobjs) throws Exception; //

	//修改后处理
	public void dealCommitAfterUpdate(cn.com.infostrategy.ui.mdata.styletemplet.t09.AbstractStyleWorkPanel_09 _frame, AggBillVO _insertobjs); //

}
