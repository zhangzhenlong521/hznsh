package cn.com.infostrategy.ui.mdata.formatcomp;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * 刷新事件绑定公式,异常有用,大大提高效率!!
 * @author xch
 *
 */
public class AddSelectEventBindRefresh extends PostfixMathCommand {
	private BillFormatPanel formatpanel = null; //

	public AddSelectEventBindRefresh(BillFormatPanel _billcellpanel) {
		this.formatpanel = _billcellpanel; //
		numberOfParameters = 4; //
	}

	public void run(Stack inStack) throws ParseException {
		Object par_passive_fieldname = inStack.pop(); //被动刷新的表的外键
		Object par_active_fieldname = inStack.pop(); //主动刷新的表的主键

		final Object par_passive_panel = inStack.pop(); //
		Object par_active_panel = inStack.pop(); //
		if (par_passive_panel == null || par_active_panel == null) {
			return;
		}

		String str_passive_fieldname_1 = (String) par_passive_fieldname;
		String str_active_fieldname_1 = (String) par_active_fieldname; //

		final String str_passive_fieldname = str_passive_fieldname_1;
		final String str_active_fieldname = str_active_fieldname_1; //

		if (par_active_panel instanceof BillListPanel) { //如果是表型面板
			BillListPanel billlist = (BillListPanel) par_active_panel;
			billlist.addBillListSelectListener(new BillListSelectListener() {
				public void onBillListSelectChanged(BillListSelectionEvent _event) {
					BillVO billVO = _event.getCurrSelectedVO();
					if (billVO != null) {
						refreshPassive(par_passive_panel, str_passive_fieldname, billVO.getStringValue(str_active_fieldname)); //
					}
				}
			});
		} else if (par_active_panel instanceof BillTreePanel) { //如果是树型面板!!!
			BillTreePanel billTree = (BillTreePanel) par_active_panel;
			billTree.addBillTreeSelectListener(new BillTreeSelectListener() {
				public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
					BillVO billVO = _event.getCurrSelectedVO();
					if (billVO != null) {
						refreshPassive(par_passive_panel, str_passive_fieldname, billVO.getStringValue(str_active_fieldname)); //
					}
				}
			});
		}

	}

	/**
	 * 刷新被动方
	 * @param par_passive_panel
	 * @param _passive_fieldname
	 * @param _active_fieldvalue
	 */
	protected void refreshPassive(Object par_passive_panel, String _passive_fieldname, String _active_fieldvalue) {
		if (par_passive_panel instanceof BillListPanel) {
			BillListPanel billlistPanel = (BillListPanel) par_passive_panel;

			if (_active_fieldvalue == null || _active_fieldvalue.trim().equals("")) {
				billlistPanel.QueryDataByCondition(_passive_fieldname + "=" + _active_fieldvalue + ""); //
			} else {
				billlistPanel.QueryDataByCondition(_passive_fieldname + "='" + _active_fieldvalue + "'"); //
			}

		} else if (par_passive_panel instanceof BillCardPanel) {
			BillCardPanel billcardPanel = (BillCardPanel) par_passive_panel;
			if (_active_fieldvalue == null || _active_fieldvalue.trim().equals("")) {
				billcardPanel.queryDataByCondition(_passive_fieldname + "=" + _active_fieldvalue + ""); //
			} else {
				billcardPanel.queryDataByCondition(_passive_fieldname + "='" + _active_fieldvalue + "'"); //
			}

		} else if (par_passive_panel instanceof BillTreePanel) {
			BillTreePanel billtreePanel = (BillTreePanel) par_passive_panel;

			if (_active_fieldvalue == null || _active_fieldvalue.trim().equals("")) {
				billtreePanel.queryDataByCondition(_passive_fieldname + "=" + _active_fieldvalue + ""); //
			} else {
				billtreePanel.queryDataByCondition(_passive_fieldname + "='" + _active_fieldvalue + "'"); //
			}

		}
	}
}
