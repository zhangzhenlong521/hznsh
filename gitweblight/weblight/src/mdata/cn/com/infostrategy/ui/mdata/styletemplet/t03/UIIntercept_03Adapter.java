package cn.com.infostrategy.ui.mdata.styletemplet.t03;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;

/**
 * ���ģ��3��UI����������������
 * @author xch
 *
 */
public class UIIntercept_03Adapter implements IUIIntercept_03 {

	//������ʼ��...
	public void afterInitialize(AbstractStyleWorkPanel_03 workPanel) throws Exception {
	}

	public void actionAfterInsert(BillCardPanel cardPanel) throws Exception {
	}

	public void actionAfterUpdate(BillCardPanel cardPanel, String _itemkey) throws Exception {
	}

	public void actionBeforeDelete(BillCardPanel cardPanel) throws Exception {
	}

	public void dealCommitAfterDelete(AbstractStyleWorkPanel_03 _frame, BillVO _deleteobjs) {
	}

	public void dealCommitAfterInsert(AbstractStyleWorkPanel_03 _frame, BillVO _insertobjs) {
	}

	public void dealCommitAfterUpdate(AbstractStyleWorkPanel_03 _frame, BillVO _updateobjs) {
	}

	public void dealCommitBeforeDelete(AbstractStyleWorkPanel_03 _frame, BillVO _deleteobjs) throws Exception {
	}

	public void dealCommitBeforeInsert(AbstractStyleWorkPanel_03 _frame, BillVO _insertobjs) throws Exception {
	}

	public void dealCommitBeforeUpdate(AbstractStyleWorkPanel_03 _frame, BillVO _updateobjs) throws Exception {
	}

}
