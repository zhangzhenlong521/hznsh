package cn.com.infostrategy.ui.mdata.styletemplet.t02;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 风格模板2的UI端拦截器的适配器
 * @author xch
 *
 */
public class UIIntercept_02Adapter implements IUIIntercept_02 {

	public void afterInitialize(AbstractStyleWorkPanel_02 _styleWorkPanel) throws Exception {
	}

	public void actionAfterInsert(BillCardPanel cardPanel) throws Exception {
	}

	public void actionAfterUpdate(BillCardPanel cardPanel, String _itemkey) throws Exception {
	}

	public void actionBeforeDelete(BillListPanel panel, int _delerow) throws Exception {
	}

	public void dealCommitAfterDelete(AbstractStyleWorkPanel_02 _frame, BillVO _deleteobjs) {
	}

	public void dealCommitAfterInsert(AbstractStyleWorkPanel_02 _frame, BillVO _insertobjs) {
	}

	public void dealCommitAfterUpdate(AbstractStyleWorkPanel_02 _frame, BillVO _updateobjs) {
	}

	public void dealCommitBeforeDelete(AbstractStyleWorkPanel_02 _frame, BillVO _deleteobjs) throws Exception {
	}

	public void dealCommitBeforeInsert(AbstractStyleWorkPanel_02 _frame, BillVO _insertobjs) throws Exception {
	}

	public void dealCommitBeforeUpdate(AbstractStyleWorkPanel_02 _frame, BillVO _updateobjs) throws Exception {
	}

	public void actionBeforeUpdate(BillCardPanel cardPanel) throws Exception {
	}

}
