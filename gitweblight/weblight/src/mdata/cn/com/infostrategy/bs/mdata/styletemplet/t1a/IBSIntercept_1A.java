/**************************************************************************
 * $RCSfile: IBSIntercept_1A.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.bs.mdata.styletemplet.t1a;


import cn.com.infostrategy.to.mdata.BillVO;

public interface IBSIntercept_1A {

	// 提交前做的校验!!!!参数分别是新增的,删除的,修改的数据!!!
	public void dealBeforeCommit(String _dsName, BillVO[] _insertobjs, BillVO[] _deleteobjs, BillVO[] _updateobjs) throws Exception; //

	// 提交后做的处理!!
	public void dealAfterCommit(String _dsName, BillVO[] _insertobjs, BillVO[] _deleteobjs, BillVO[] _updateobjs) throws Exception; //

}
