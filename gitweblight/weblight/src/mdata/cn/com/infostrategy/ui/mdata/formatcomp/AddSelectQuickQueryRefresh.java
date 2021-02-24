package cn.com.infostrategy.ui.mdata.formatcomp;

/**
 * 刷新快速查询事件绑定公式,异常有用,大大提高效率!!
 * @author wj
 *
 */
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

public class AddSelectQuickQueryRefresh extends PostfixMathCommand {
	private BillFormatPanel formatpanel = null; //

	public AddSelectQuickQueryRefresh(BillFormatPanel _billcellpanel) {

		this.formatpanel = _billcellpanel; //
		numberOfParameters = 5; //
	}

	public void run(Stack inStack) throws ParseException {
		Object par_passive_listid = inStack.pop(); //列表外键
		Object par_passive_fieldname = inStack.pop(); //name
		Object par_active_id = inStack.pop(); //id

		final Object par_passive_panel = inStack.pop(); //列表
		Object par_active_panel = inStack.pop(); //需要选择变化的(树型或列表)

		if (par_passive_panel == null || par_active_panel == null) {
			return;
		}
		final String str_passive_listid = (String) par_passive_listid;
		final String str_passive_fieldname = (String) par_passive_fieldname;
		final String str_active_id = (String) par_active_id; //

		if (par_active_panel instanceof BillListPanel) { //如果是表型面板
			BillListPanel billlist = (BillListPanel) par_active_panel;
			billlist.addBillListSelectListener(new BillListSelectListener() {
				public void onBillListSelectChanged(BillListSelectionEvent _event) {
					BillVO billVO = _event.getCurrSelectedVO();
					if (billVO != null) {
						refreshPassive(par_passive_panel, billVO.getStringValue(str_passive_fieldname), billVO.getStringValue(str_active_id), str_passive_listid); //
					}
				}
			});
		} else if (par_active_panel instanceof BillTreePanel) { //如果是树型面板!!!
			BillTreePanel billTree = (BillTreePanel) par_active_panel;
			billTree.addBillTreeSelectListener(new BillTreeSelectListener() {
				public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
					BillVO billVO = _event.getCurrSelectedVO();
					if (billVO != null) {
						refreshPassive(par_passive_panel, billVO.getStringValue(str_passive_fieldname), billVO.getStringValue(str_active_id), str_passive_listid); //
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
	protected void refreshPassive(Object par_passive_panel, String _active_fieldvalue, String sid, String listid) {
		if (par_passive_panel instanceof BillListPanel) {
			BillListPanel billlistPanel = (BillListPanel) par_passive_panel;
			billlistPanel.setQuickQueryPanelItemValue(listid, new RefItemVO(sid, _active_fieldvalue, _active_fieldvalue));
		}
	}
}
