package cn.com.infostrategy.ui.mdata.formatcomp;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillFormatPanel;

public class GetFormatItemPanel extends PostfixMathCommand {
	public static int ITEMTYPE_CARD = 1; //
	public static int ITEMTYPE_LIST = 2; //
	public static int ITEMTYPE_TREE = 3; //
	public static int ITEMTYPE_CELL = 4; //

	private BillFormatPanel formatpanel = null; //
	private int itempaneltype = -1; //

	public GetFormatItemPanel(BillFormatPanel _billformatpanel, int _type) {
		this.formatpanel = _billformatpanel; //
		this.itempaneltype = _type; //
		numberOfParameters = -1; //
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		if (curNumberOfParameters != 1) {
			System.err.println("函数" + getFunName() + "参数个数不对,必须只能有一个参数!");
		}

		Object param_1 = inStack.pop(); //types[]
		String str_templetcode = (String) param_1; //模板编码

		Object objreturn = null;
		if (itempaneltype == ITEMTYPE_CARD) {
			if (str_templetcode.equals("")) {
				objreturn = formatpanel.getBillCardPanel();
			} else {
				objreturn = formatpanel.getBillCardPanelByTempletCode(str_templetcode);
			}
		} else if (itempaneltype == ITEMTYPE_LIST) {
			if (str_templetcode.equals("")) {
				objreturn = formatpanel.getBillListPanel(); //
			} else {
				objreturn = formatpanel.getBillListPanelByTempletCode(str_templetcode);
			}
		} else if (itempaneltype == ITEMTYPE_TREE) {
			if (str_templetcode.equals("")) {
				objreturn = formatpanel.getBillTreePanel(); //
			} else {
				objreturn = formatpanel.getBillTreePanelByTempletCode(str_templetcode);
			}
		} else if (itempaneltype == ITEMTYPE_CELL) {
			if (str_templetcode.equals("")) {
				objreturn = formatpanel.getBillCellPanel();
			} else {
				objreturn = formatpanel.getBillCellPanelByTempletCode(str_templetcode); //
			}
		}

		inStack.push(objreturn); //
	}

	private String getFunName() {
		if (itempaneltype == ITEMTYPE_CARD) {
			return "getCard()";
		} else if (itempaneltype == ITEMTYPE_LIST) {
			return "getList()";
		} else if (itempaneltype == ITEMTYPE_TREE) {
			return "getTree()";
		} else {
			return "getCell()";
		}
	}
}
