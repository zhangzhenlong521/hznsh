package cn.com.infostrategy.bs.mdata.styletemplet.t10;

import cn.com.infostrategy.to.mdata.AggBillVO;

public interface IBSIntercept_10 {

	//新增前处理
	public void dealCommitBeforeInsert(String _dsName, AggBillVO _insertobjs) throws Exception; //

	//新增后处理
	public void dealCommitAfterInsert(String _dsName, AggBillVO _insertobjs) throws Exception; //

	//删除前处理
	public void dealCommitBeforeDelete(String _dsName, AggBillVO _insertobjs) throws Exception; //

	//删除后处理
	public void dealCommitAfterDelete(String _dsName, AggBillVO _insertobjs) throws Exception; //

	//修改前处理
	public void dealCommitBeforeUpdate(String _dsName, AggBillVO _insertobjs) throws Exception; //

	//修改后处理
	public void dealCommitAfterUpdate(String _dsName, AggBillVO _insertobjs) throws Exception; //

}
