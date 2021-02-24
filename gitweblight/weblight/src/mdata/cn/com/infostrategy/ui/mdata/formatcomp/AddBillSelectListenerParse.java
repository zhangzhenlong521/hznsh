package cn.com.infostrategy.ui.mdata.formatcomp;

import java.util.Stack;
import java.util.Vector;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * 刷新事件绑定公式,异常有用,大大提高效率!!
 * @author xch
 *
 */
public class AddBillSelectListenerParse extends PostfixMathCommand {
	private BillFormatPanel formatpanel = null; //

	public AddBillSelectListenerParse(BillFormatPanel _billcellpanel) {
		this.formatpanel = _billcellpanel; //
		numberOfParameters = -1; //参数不确定
	}

	public void run(Stack inStack) throws ParseException {
		try {
			checkStack(inStack); //
			Vector v_pars = new Vector(); //
			for (int i = 0; i < curNumberOfParameters; i++) {
				v_pars.add(inStack.pop()); //
			}

			final Object[] obj_pars = new Object[v_pars.size()]; //
			int li_pos = 0; //
			for (int i = v_pars.size() - 1; i >= 0; i--) {
				obj_pars[li_pos] = v_pars.get(i); //
				li_pos++;
			}

			Object obj_billPanel = (BillPanel) obj_pars[0]; //最后获得是哪个模板框
			if (obj_billPanel instanceof BillListPanel) {
				BillListPanel billListPanel = (BillListPanel) obj_billPanel; //
				billListPanel.addBillListSelectListener(new BillListSelectListener() {
					public void onBillListSelectChanged(BillListSelectionEvent _event) {
						onBillSelectChanged(obj_pars); //
					}
				});
			} else if (obj_billPanel instanceof BillTreePanel) {
				BillTreePanel billTreePanel = (BillTreePanel) obj_billPanel; //
				billTreePanel.addBillTreeSelectListener(new BillTreeSelectListener() {
					public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
						onBillSelectChanged(obj_pars);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * 选择发生变化!!
	 */
	protected void onBillSelectChanged(Object[] obj_pars) {
		FormatEventBindFormulaParse formulaParse = new FormatEventBindFormulaParse(formatpanel); //
		TBUtil tbUtil = new TBUtil();
		for (int i = 1; i < obj_pars.length; i++) {
			String str_formula = (String) obj_pars[i];
			String[] str_formulas = tbUtil.split1(str_formula, ";"); //
			for (int j = 0; j < str_formulas.length; j++) {
				System.out.println("AddBillSelectListenerParse[" + this + "]动态执行公式[" + str_formulas[j] + "]"); //
				formulaParse.execFormula(str_formulas[j]); //
			}
		}
	}

}
