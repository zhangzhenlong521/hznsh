package cn.com.infostrategy.ui.mdata.styletemplet;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.AggBillVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.WLTRemoteCallServiceIfc;

public interface StyleTempletServiceIfc extends WLTRemoteCallServiceIfc {

	//风格模板1的系列处理!!
	public HashMap style01_dealCommit(String _dsName, String _bsInterceptName, BillVO[] _insertVOs, BillVO[] _deleteVOs, BillVO[] _updateVOs) throws Exception;

	//风格模板2的系列处理!!
	public BillVO style02_dealInsert(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception;

	public BillVO style02_dealUpdate(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception;

	public void style02_dealDelete(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception;

	//	风格模板3的系列处理!!
	public BillVO style03_dealInsert(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception;

	public BillVO style03_dealUpdate(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception;

	public void style03_dealDelete(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception;

	public void style03_dealCascadeDelete(String _dsName, String _bsInterceptName, HashVO[] _deleteobj, String tablename, String field) throws Exception;

	//风格模板4的系列处理!!
	public HashMap style04_dealCommit(String _dsName, String _bsInterceptName, BillVO[] _insertVOs, BillVO[] _deleteVOs, BillVO[] _updateVOs) throws Exception;

	//	风格模板5的系列处理!!
	public BillVO style05_dealInsert(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception;

	public BillVO style05_dealUpdate(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception;

	public void style05_dealDelete(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception;

	//风格模板6的系列处理!!
	public HashMap style06_dealCommit(String _dsName, String _bsInterceptName, BillVO[] _insertVOs, BillVO[] _deleteVOs, BillVO[] _updateVOs) throws Exception;

	//风格模板7的系列处理!!
	public BillVO style07_dealInsert(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception;

	public BillVO style07_dealUpdate(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception;

	public void style07_dealDelete(String _dsName, String _bsInterceptName, BillVO _vo) throws Exception;

	//风格模板8系列处理
	public AggBillVO style08_dealInsert(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception;

	public AggBillVO style08_dealUpdate(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception;

	public void style08_dealDelete(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception;

	//风格模板9系列处理
	public AggBillVO style09_dealInsert(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception;

	public AggBillVO style09_dealUpdate(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception;

	public void style09_dealDelete(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception;

}
