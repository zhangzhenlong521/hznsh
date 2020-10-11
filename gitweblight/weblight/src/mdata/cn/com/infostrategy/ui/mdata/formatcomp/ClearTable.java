package cn.com.infostrategy.ui.mdata.formatcomp;

/**
 * ģ��ѡ���¼������һ��ģ��
 */
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

public class ClearTable extends PostfixMathCommand {
	private BillFormatPanel formatpanel = null; //

	public ClearTable(BillFormatPanel _billcellpanel) {
		this.formatpanel = _billcellpanel; //
		numberOfParameters = 1; //
	}

	public void run(Stack inStack) throws ParseException {
		Object billPanel = inStack.pop(); //��Ҫ��յ�ģ��
		if (billPanel instanceof BillListPanel) { //����Ǳ������
			BillListPanel billlist = (BillListPanel) billPanel;
			billlist.clearTable(); //
		} else if (billPanel instanceof BillTreePanel) { //������������!!!
			BillTreePanel billTree = (BillTreePanel) billPanel;
			billTree.clearTree(); //
		} else if (billPanel instanceof BillCardPanel) { //������������!!!
			BillCardPanel bilCard = (BillCardPanel) billPanel;
			bilCard.clear(); //
		}
	}
}
