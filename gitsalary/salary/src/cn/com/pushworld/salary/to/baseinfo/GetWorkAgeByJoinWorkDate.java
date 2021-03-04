package cn.com.pushworld.salary.to.baseinfo;

import cn.com.infostrategy.to.mdata.jepfunctions.IClassJepFormulaParseIFC;

public class GetWorkAgeByJoinWorkDate implements IClassJepFormulaParseIFC {

	public String getForMulaValue(String[] pars) throws Exception {
		String date = pars[0];
		FormulaTool tool = new FormulaTool();
		return tool.getWorkAgeByWorkDate(date) + "";
	}

}
