package com.pushworld.ipushgrc.ui.cmpscore.p020;

import cn.com.infostrategy.ui.mdata.styletemplet.t03.AbstractStyleWorkPanel_03;

public class CmpScoreStandDefineWKPanel extends AbstractStyleWorkPanel_03{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2229463340742417056L;

	@Override
	public String getTempletcode() {
		return "CMP_SCORESTAND_CODE1";
	}
	
	public void afterInitialize(){
		this.getBillTreePanel().addBillTreeMovedListener(this);
	}

}
