package com.pushworld.ipushgrc.bs.cmpreport.p110;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.workflow.WFIntercept2IFC;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

public class CmpReportStartIntercept implements WFIntercept2IFC {

	public void afterAction(WFParVO callVO, String _loginuserid,
			BillVO _billvo, String _dealtype) throws Exception {
		String id = _billvo.getStringValue("id");
		String sql = "update "+_billvo.getSaveTableName()+" set state='ЩѓХњжа' where id = "+id ;
		new CommDMO().executeBatchByDS(null, new String[]{sql});

	}

	public void beforeAction(WFParVO callVO, String _loginuserid,
			BillVO _billvo, String _dealtype) throws Exception {

	}

}
