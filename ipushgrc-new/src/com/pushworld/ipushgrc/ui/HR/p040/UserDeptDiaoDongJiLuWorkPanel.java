package com.pushworld.ipushgrc.ui.HR.p040;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
/**
 * 
 * @author longlonggo521
 * 员工岗位调动记录【2017-10-30】
 */
public class UserDeptDiaoDongJiLuWorkPanel extends AbstractWorkPanel{
	BillListPanel list=null;

	@Override
	public void initialize() {
		list=new BillListPanel("HR_RECORDPOST_CODE1");
		this.add(list);
		
	}

}
