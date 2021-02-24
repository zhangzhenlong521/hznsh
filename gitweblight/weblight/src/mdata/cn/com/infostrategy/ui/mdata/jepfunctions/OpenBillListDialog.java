package cn.com.infostrategy.ui.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/**
 * 直接弹出一个列表对话框!!!,一共4个参数,第一个是模板编码,第2个是打开列表窗口时的初始化查询条件,第3个打开时窗口大小,第4个是默认高度!
 * @author xch
 *
 */
public class OpenBillListDialog extends PostfixMathCommand {

	private BillPanel billPanel = null;
	private WLTButton wltButton = null;

	private TBUtil tBUtil = null;

	public OpenBillListDialog(BillPanel _billPanel, WLTButton _btn) {
		numberOfParameters = -1; //四个条件,第一个是模板编码,第2个SQL过滤条件,第三个是宽度与高度
		this.billPanel = _billPanel;
		this.wltButton = _btn; //

	}

	public void run(Stack inStack) throws ParseException {
		if (this.curNumberOfParameters != 4) {
			System.err.println("公式OpenBillListDialog的参数个数不对,必须是4个请检查!!"); //
			return;
		}
		try {
			BillVO billVO = null;
			if (billPanel instanceof BillCardPanel) {
				billVO = ((BillCardPanel) billPanel).getBillVO(); //
			} else if (billPanel instanceof BillListPanel) {
				billVO = ((BillListPanel) billPanel).getSelectedBillVO(); //
			} else if (billPanel instanceof BillTreePanel) {
				billVO = ((BillTreePanel) billPanel).getSelectedVO(); //
			}

			if (billVO == null) {
				MessageBox.showSelectOne(billPanel); //
				return;
			}

			checkStack(inStack);

			Object param_1 = inStack.pop();
			Object param_2 = inStack.pop();
			Object param_3 = inStack.pop();
			Object param_4 = inStack.pop();

			String str_templetCode = (String) param_4;
			String str_sqlFilterCon = (String) param_3;
			int li_width = Integer.parseInt((String) param_2);
			int li_height = Integer.parseInt((String) param_1);

			if (str_sqlFilterCon != null && !str_sqlFilterCon.trim().equals("")) {
				String[] str_items = getTBUtil().getFormulaMacPars(str_sqlFilterCon, "${", "}"); //
				for (int i = 0; i < str_items.length; i++) {
					str_sqlFilterCon = getTBUtil().replaceAll(str_sqlFilterCon, "${" + str_items[i] + "}", billVO.getStringValue(str_items[i], "")); //
				}
			}
			BillListDialog billListDialog = new BillListDialog(billPanel, wltButton.getText(), str_templetCode, str_sqlFilterCon, li_width, li_height, false); //
			billListDialog.getBilllistPanel().setQuickQueryPanelVisiable(false); //快速查询框必须隐藏掉!!否则可以再次查询就不对了!!!
			billListDialog.getBilllistPanel().setItemEditable(false); //
			billListDialog.setVisible(true);
		} catch (Exception _ex) {
			getTBUtil().printStackTrace(_ex); //
		}

	}

	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil(); //
		}
		return tBUtil;
	}
}
