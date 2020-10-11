package cn.com.pushworld.salary.to.baseinfo;

import cn.com.infostrategy.to.mdata.jepfunctions.IClassJepFormulaParseIFC;

public class GetAgeByCardID implements IClassJepFormulaParseIFC {

	public String getForMulaValue(String[] pars) throws Exception {
		String idcard = pars[0];
		FormulaTool tool = new FormulaTool();
		return tool.getAgeByIdCard(idcard) + "";
	}

}
