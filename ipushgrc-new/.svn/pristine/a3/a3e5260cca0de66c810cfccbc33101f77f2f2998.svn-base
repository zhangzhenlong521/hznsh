package com.pushworld.ipushgrc.ui.wfrisk.p010;

import cn.com.infostrategy.to.mdata.jepfunctions.IClassJepFormulaParseIFC;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * 流程文件正文保存后，关闭窗口时需要更新本条记录的正文字段，否则，客户经常忘记点击卡片中的确定，导致正文路径丢失【李春娟/2012-10-29】
 * @author lcj
 *
 */
public class UpdateCmpFileReffileJEPImpl implements IClassJepFormulaParseIFC {
	public String getForMulaValue(String[] _pars) throws Exception {
		if (_pars != null && _pars.length > 0) {
			if (_pars[0] == null || _pars[0].equals("")) {
				return "";
			}
			UIUtil.executeUpdateByDS(null, "update cmp_cmpfile set reffile='" + _pars[1] + "' where id=" + _pars[0]);
		}
		return "";
	}

}
