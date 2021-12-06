package com.pushworld.ipushgrc.ui.wfrisk;

import java.util.HashMap;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.formatcomp.BillFormatDialog;

public class ChooseDeptOrPostDialog {

	private static final long serialVersionUID = 1L;

	public HashMap show(HashMap _hashmap) {

		if (_hashmap == null) {
			_hashmap = new HashMap();
		}

		BillFormatDialog billdialog = new BillFormatDialog(null, "添加机构和岗位", "wf_addDeptAndPost");
		billdialog.setVisible(true);
		BillVO deptbillvo = billdialog.getBillformatPanel().getBillTreePanel().getSelectedVO();
		BillVO[] postbillvos = billdialog.getBillformatPanel().getBillListPanel().getSelectedBillVOs();
		_hashmap.put("deptbillvo", deptbillvo);
		_hashmap.put("postbillvos", postbillvos);
		if (billdialog.getCloseType() != 1 || deptbillvo == null) {
			return null;
		}
		return _hashmap;
	}
}
