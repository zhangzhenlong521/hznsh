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
			// MessageBox.show(bc, "���֤�Ų���Ϊ��!", 5, null);
			return;
		}
		if (!tool.isValidatedAllIdcard(idcard)) {
			bc.reset("cardid");
			MessageBox.show(bc, "���֤�Ų��Ϸ�!", 5, null);
			return;
		}
		try {
			String sex = tool.getSexByIdCard(idcard);
			bc.setRealValueAt("sex", sex);
			// ��ȡ��������
			bc.setRealValueAt("birthday", tool.getBirthDayByIdCard(idcard));
			// ��ȡ����
			bc.setRealValueAt("age", tool.getAgeByIdCard(idcard) + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
