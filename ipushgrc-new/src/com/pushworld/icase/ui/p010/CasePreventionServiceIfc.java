package com.pushworld.icase.ui.p010;

import cn.com.infostrategy.ui.common.WLTRemoteCallServiceIfc;

public interface CasePreventionServiceIfc extends WLTRemoteCallServiceIfc {
	public boolean submitInvestigationPlan(String planId, String deptCorpType, String channels) throws Exception;
}
