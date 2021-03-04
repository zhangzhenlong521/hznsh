package cn.com.pushworld.salary.ui.baseinfo;

import cn.com.infostrategy.to.mdata.jepfunctions.ItemValueListener;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.pushworld.salary.to.baseinfo.FormulaTool;

public class IdCardFormula implements ItemValueListener {

	public void process(BillPanel panel) {
		BillCardPanel bc = (BillCardPanel) panel;
		FormulaTool tool = new FormulaTool();
		String idcard = bc.getRealValueAt("cardid");
		if (idcard == null || "".equals(idcard)) {
			// MessageBox.show(bc, "身份证号不能为空!", 5, null);
			return;
		}
		if (!tool.isValidatedAllIdcard(idcard)) {
			bc.reset("cardid");
			MessageBox.show(bc, "身份证号不合法!", 5, null);
			return;
		}
		try {
			String sex = tool.getSexByIdCard(idcard);
			bc.setRealValueAt("sex", sex);
			// 获取出生日期
			bc.setRealValueAt("birthday", tool.getBirthDayByIdCard(idcard));
			// 获取年龄
			bc.setRealValueAt("age", tool.getAgeByIdCard(idcard) + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
