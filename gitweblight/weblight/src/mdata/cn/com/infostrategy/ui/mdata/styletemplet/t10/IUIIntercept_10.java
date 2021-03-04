/**************************************************************************
 * $RCSfile: IUIIntercept_10.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t10;

import cn.com.infostrategy.to.mdata.AggBillVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ���ӱ��ǰ��Intercept!!
 * @author user
 *
 */
public interface IUIIntercept_10 {

	//����ɾ��ǰ���Ķ���
	public void actionBeforeDelete_parent(BillListPanel _parentbilllistPanel, int _delerow) throws Exception;

	//�ӱ�ɾ��ǰ���Ķ���,_tabIndex���ӱ��ҳǩ��˳��,��1��ʼ,�ֱ���1,2,3,4,5
	public void actionBeforeDelete_child(int _tabIndex, BillListPanel _childbilllistPanel, int _delerow) throws Exception;

	//�������������Ķ���,����ʱ�����ǿ�Ƭ
	public void actionAfterInsert_parent(BillCardPanel _billcardPanel) throws Exception;

	//�ӱ����������Ķ���,����ʱ�ӱ����б�!!,_tabIndex���ӱ��ҳǩ��˳��,��1��ʼ,�ֱ���1,2,3,4,5
	public void actionAfterInsert_child(int _tabIndex, BillListPanel _billListPanel, int _newrow) throws Exception;

	//�����޸ĺ����Ķ���,�޸�ʱ�����ǿ�Ƭ!!!
	public void actionAfterUpdate_parent(BillCardPanel _billcardPanel, String _itemkey) throws Exception; //

	//�ӱ��޸ĺ����Ķ���!!,_tabIndex���ӱ��ҳǩ��˳��,��1��ʼ,�ֱ���1,2,3,4,5
	public void actionAfterUpdate_child(int _tabIndex, BillListPanel _billListPanel, String _itemkey, int _updatedrow) throws Exception; //

	//����ǰ����
	public void dealCommitBeforeInsert(cn.com.infostrategy.ui.mdata.styletemplet.t09.AbstractStyleWorkPanel_09 _frame, AggBillVO _insertobjs) throws Exception; //

	//��������
	public void dealCommitAfterInsert(cn.com.infostrategy.ui.mdata.styletemplet.t09.AbstractStyleWorkPanel_09 _frame, AggBillVO _insertobjs); //

	//ɾ��ǰ����
	public void dealCommitBeforeDelete(cn.com.infostrategy.ui.mdata.styletemplet.t09.AbstractStyleWorkPanel_09 _frame, AggBillVO _insertobjs) throws Exception; //

	//ɾ������
	public void dealCommitAfterDelete(cn.com.infostrategy.ui.mdata.styletemplet.t09.AbstractStyleWorkPanel_09 _frame, AggBillVO _insertobjs); //

	//�޸�ǰ����
	public void dealCommitBeforeUpdate(cn.com.infostrategy.ui.mdata.styletemplet.t09.AbstractStyleWorkPanel_09 _frame, AggBillVO _insertobjs) throws Exception; //

	//�޸ĺ���
	public void dealCommitAfterUpdate(cn.com.infostrategy.ui.mdata.styletemplet.t09.AbstractStyleWorkPanel_09 _frame, AggBillVO _insertobjs); //

}
