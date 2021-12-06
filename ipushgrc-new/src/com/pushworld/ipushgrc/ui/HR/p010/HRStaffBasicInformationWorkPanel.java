package com.pushworld.ipushgrc.ui.HR.p010;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
/**
 * 
 * @author zzl[2017-10-26]
 * 员工基本信息维护
 * 
 *
 */
public class HRStaffBasicInformationWorkPanel  extends AbstractWorkPanel{
	BillListPanel list=null;

	@Override
	public void initialize() {
		list=new BillListPanel("SAL_PERSONINFO_ZZL_CODE2");	
		this.add(list);
	}

}
 