package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Container;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;

/**
 * 模板选择
 * @author haoming
 * create by 2015-10-29
 */
public class MetaTempletQueryDialog extends BillDialog {
	MetaTempletQueryPanel panel = new MetaTempletQueryPanel();
	
	public MetaTempletQueryDialog(Container _parent) {
		super(_parent);
		this.setLayout(new BorderLayout());
		panel.initialize();
		this.add(panel, BorderLayout.CENTER);
		addOptionButtonPanel(new String[] { "确定", "返回" });
	}

	public String getSelectTempletCode() {
		BillVO bvo = panel.getSelectBillVO();
		if (bvo != null) {
			return panel.getSelectBillVO().getStringValue("TEMPLETCODE");
		}
		return null;
	}

	public String getSelectTempletFrom() {
		BillVO bvo = panel.getSelectBillVO();
		if (bvo != null) {
			return panel.getSelectBillVO().getStringValue("savetype");
		}
		return null;
	}

	class MetaTempletQueryPanel extends MetaTempletConfigPanel {
		@Override
		public boolean isSelectPanel() {
			return true;
		}
	}
}
