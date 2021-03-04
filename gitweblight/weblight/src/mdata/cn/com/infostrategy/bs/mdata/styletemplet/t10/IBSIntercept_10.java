package cn.com.infostrategy.bs.mdata.styletemplet.t10;

import cn.com.infostrategy.to.mdata.AggBillVO;

public interface IBSIntercept_10 {

	//����ǰ����
	public void dealCommitBeforeInsert(String _dsName, AggBillVO _insertobjs) throws Exception; //

	//��������
	public void dealCommitAfterInsert(String _dsName, AggBillVO _insertobjs) throws Exception; //

	//ɾ��ǰ����
	public void dealCommitBeforeDelete(String _dsName, AggBillVO _insertobjs) throws Exception; //

	//ɾ������
	public void dealCommitAfterDelete(String _dsName, AggBillVO _insertobjs) throws Exception; //

	//�޸�ǰ����
	public void dealCommitBeforeUpdate(String _dsName, AggBillVO _insertobjs) throws Exception; //

	//�޸ĺ���
	public void dealCommitAfterUpdate(String _dsName, AggBillVO _insertobjs) throws Exception; //

}
