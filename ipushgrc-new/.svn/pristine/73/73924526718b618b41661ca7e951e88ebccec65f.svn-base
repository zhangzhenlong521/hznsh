package com.pushworld.ipushlbs.bs.casemanage;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.workflow.WorkFlowEngineBSIntercept;
import cn.com.infostrategy.to.common.WLTHashMap;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

public class CaseBSWFIntercept extends WorkFlowEngineBSIntercept {

	/**
	 * 整个流程结束后执行的逻辑,只有注册在流程中该方法才会被调用！！
	 * @param _billType
	 * @param _busiType
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	public void afterWorkFlowEnd(String _billType, String _busiType, WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype, WLTHashMap _parMap) throws Exception {
		//起诉和被诉案件，以前设置status='END'，后来发现案件跟踪时才会改变status状态，故修改之【李春娟/2015-08-11】
		String sql = "update " + _billVO.getSaveTableName() + " set endtype ='" + _dealtype + "' where id='" + _billVO.getRealValue("id") + "'";
		CommDMO comm = new CommDMO();
		comm.executeUpdateByDS(null, sql);
	}

}