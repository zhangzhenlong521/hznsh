package com.pushworld.ipushgrc.ui.wfrisk.p010;

import cn.com.infostrategy.to.mdata.jepfunctions.IClassJepFormulaParseIFC;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * �����ļ����ı���󣬹رմ���ʱ��Ҫ���±�����¼�������ֶΣ����򣬿ͻ��������ǵ����Ƭ�е�ȷ������������·����ʧ�����/2012-10-29��
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
