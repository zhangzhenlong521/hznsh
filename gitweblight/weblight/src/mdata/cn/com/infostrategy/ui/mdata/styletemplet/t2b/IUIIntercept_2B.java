/**************************************************************************
 * $RCSfile: IUIIntercept_2B.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t2b;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ���ӱ��ǰ��Intercept!!
 * @author user
 *
 */
public interface IUIIntercept_2B {
	//ɾ��ǰ���Ķ���
	public void actionBeforeDelete(BillListPanel _billlistPanel, int _delerow) throws Exception;

	//����ǰ���Ķ���
	public void actionBeforeInsert(BillListPanel _billlistPanel, int _newrow) throws Exception;

	//�޸�ǰ���Ķ���
	public void actionBeforeUpdate(BillListPanel _billlistPanel, int _updatedrow, String _itemkey) throws Exception; //

	//�ύǰ����У�鴦��!!!!�����ֱ���������,ɾ����,�޸ĵ�����!!!
	public void dealBeforeCommit(BillListPanel _billlistPanel, BillVO[] _insertobjs, BillVO[] _deleteobjs, BillVO[] _updateobjs) throws Exception; //

	//�ύ��ĺ�������..
	public void dealAfterCommit(BillListPanel _billlistPanel, BillVO[] _insertobjs, BillVO[] _deleteobjs, BillVO[] _updateobjs) throws Exception; //

}
