package cn.com.infostrategy.ui.mdata.styletemplet.t01;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ���ģ��1��UI����������������,ʵ�ʵ�����������̳���
 * ǿ�ҽ��鲻Ҫֱ��ʵ�ֽӿ�,��Ϊ�ӿ��Ժ����ʱ������䶯�������µķ���,���ֱ�Ӽ̳��ڽӿ�,�����֡�ǣһ������ȫ����״�����ܲ��ã�����
 * @author xch
 *
 */
public class UIIntercept_01Adapter implements IUIIntercept_01 {

	//������ʼ��..
	public void afterInitialize(AbstractStyleWorkPanel_01 _styleWorkPanel) throws Exception {
	}

	public void actionBeforeDelete(BillListPanel panel, int _delerow) throws Exception {
	}

	public void actionBeforeInsert(BillListPanel panel, int _newrow) throws Exception {
	}

	public void actionBeforeUpdate(BillListPanel panel, int _updatedrow, String _itemkey) throws Exception {
	}

	public void dealAfterCommit(BillListPanel panel, BillVO[] _insertobjs, BillVO[] _deleteobjs, BillVO[] _updateobjs) throws Exception {
	}

	public void dealBeforeCommit(BillListPanel panel, BillVO[] _insertobjs, BillVO[] _deleteobjs, BillVO[] _updateobjs) throws Exception {
	}

}
