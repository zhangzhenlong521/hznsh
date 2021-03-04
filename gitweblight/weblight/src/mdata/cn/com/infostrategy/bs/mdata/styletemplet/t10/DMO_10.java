package cn.com.infostrategy.bs.mdata.styletemplet.t10;


import java.util.Vector;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.mdata.AggBillVO;
import cn.com.infostrategy.to.mdata.BillVO;

public class DMO_10 extends AbstractDMO{

	public DMO_10() {
	}

	/**
	 * ����!!
	 * @param _dsName
	 * @param _bsInterceptName
	 * @param _aggVO
	 * @return
	 * @throws Exception
	 */
	public AggBillVO dealInsert(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception {
		IBSIntercept_10 bsIntercept = getIntercept(_bsInterceptName);

		// ����ǰ����!!
		if (bsIntercept != null) {
			bsIntercept.dealCommitBeforeInsert(_dsName, _aggVO);
		}

		Vector v_sqls = new Vector(); //
		v_sqls.add(_aggVO.getParentVO().getInsertSQL()); //��������������SQL

		int li_childCount = _aggVO.getChildCount(); // ����ҳǩ����!!!
		for (int i = 0; i < li_childCount; i++) {
			BillVO childInsertVOs[] = _aggVO.getChildInsertVOs(i + 1);
			if (childInsertVOs != null && childInsertVOs.length > 0) { // �����������!!
				for (int j = 0; j < childInsertVOs.length; j++) {
					String str_sqlchild_insert = childInsertVOs[j].getInsertSQL();
					v_sqls.add(str_sqlchild_insert);
				}
			}
		}

		ServerEnvironment.getCommDMO().executeBatchByDS(_dsName, v_sqls); //�����������ݿ�!!!!!���ĵش�!!!

		// ����������!!
		if (bsIntercept != null) {
			bsIntercept.dealCommitAfterInsert(_dsName, _aggVO);
		}
		return _aggVO;
	}

	/**
	 * ɾ��
	 * @param _dsName
	 * @param _bsInterceptName
	 * @param _aggVO
	 * @throws Exception
	 */
	public void dealDelete(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception {
		IBSIntercept_10 bsIntercept = getIntercept(_bsInterceptName);

		// ɾ��ǰ����!!
		if (bsIntercept != null) {
			bsIntercept.dealCommitBeforeDelete(_dsName, _aggVO); //ɾ��ǰ����
		}

		Vector v_sqls = new Vector(); //
		int li_childCount = _aggVO.getChildCount(); //
		for (int i = 0; i < li_childCount; i++) {
			BillVO[] childDeleteVOs = _aggVO.getChildDeleteVOs(i + 1);
			if (childDeleteVOs != null && childDeleteVOs.length > 0) {
				for (int j = 0; j < childDeleteVOs.length; j++) {
					String str_sqlchild_delete = childDeleteVOs[j].getDeleteSQL();
					v_sqls.add(str_sqlchild_delete); //
				}
			}
		}
		v_sqls.add(_aggVO.getParentVO().getDeleteSQL( )); //��������������SQL

		ServerEnvironment.getCommDMO().executeBatchByDS(_dsName, v_sqls); //�����������ݿ�!!!���ĵش�!!!

		// ɾ��������!!
		if (bsIntercept != null) {
			bsIntercept.dealCommitAfterDelete(_dsName, _aggVO); //ɾ��������
		}
	}

	public AggBillVO dealUpdate(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception {
		IBSIntercept_10 bsIntercept = getIntercept(_bsInterceptName);

		// �޸�ǰ����!!
		if (bsIntercept != null) {
			bsIntercept.dealCommitBeforeUpdate(_dsName, _aggVO);
		}

		// �ֹ������,,�����Ҫ���ֹ�������!!!
		if (_aggVO.getParentVO().isDealVersion()) {
			Double ld_version = _aggVO.getParentVO().getVersion(); // ȡ�õ�ǰ�汾��!!
			Double ld_currVersion = getVersion(_dsName, _aggVO); //
			if (ld_version != null && ld_currVersion != null && !ld_version.equals(ld_currVersion)) {
				throw new Exception("�����������Ѿ��������û��޸ģ���ˢ�����ݺ��ٽ��в�����");
			}
		}

		//����ȥ�޸�!!!!
		Vector v_sqls = new Vector(); //
		String str_updateparent_sql = _aggVO.getParentVO().getUpdateSQL(); // ȡ������update��SQL!!!
		v_sqls.add(str_updateparent_sql); //

		//���������ӱ�....!!!
		int li_childCount = _aggVO.getChildCount(); //
		for (int i = 0; i < li_childCount; i++) {
			BillVO[] childInsertVOs = _aggVO.getChildInsertVOs(i + 1);
			BillVO[] childUpdateVOs = _aggVO.getChildUpdateVOs(i + 1);
			BillVO[] childDeleteVOs = _aggVO.getChildDeleteVOs(i + 1);

			if (childDeleteVOs != null) { // ����ɾ��
				for (int j = 0; j < childDeleteVOs.length; j++) {
					String str_sqlchild_delete = childDeleteVOs[j].getDeleteSQL();
					v_sqls.add(str_sqlchild_delete);
				}
			}

			if (childInsertVOs != null) { // ��������
				for (int j = 0; j < childInsertVOs.length; j++) {
					String str_sqlchild_insert = childInsertVOs[j].getInsertSQL();
					v_sqls.add(str_sqlchild_insert);
				}
			}

			if (childUpdateVOs != null) { // ������޸�
				for (int j = 0; j < childUpdateVOs.length; j++) {
					String str_sqlchild_update = childUpdateVOs[j].getUpdateSQL();
					v_sqls.add(str_sqlchild_update);
				}
			}
		}

		ServerEnvironment.getCommDMO().executeBatchByDS(_dsName, v_sqls); //����ȥ�������ݿ�!���ĵش�!!

		// �޸ĺ�����!!
		if (bsIntercept != null) {
			bsIntercept.dealCommitAfterUpdate(_dsName, _aggVO);
		}

		return _aggVO;
	}

	/**
	 * @param _aggvo
	 * @return
	 */
	private Double getVersion(String _dsName, AggBillVO _aggvo) {
		String str_version_sql = "select nvl(version,1) from " + _aggvo.getParentVO().getSaveTableName() + " where " + _aggvo.getParentVO().getUpdateWhereCondition(); //
		String[][] data = null;
		try {
			data = ServerEnvironment.getCommDMO().getStringArrayByDS(_dsName, str_version_sql.toString());
			return new Double(data[0][0]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * ȡ�÷���������������
	 * @param _interceptName
	 * @return
	 * @throws Exception
	 */
	private IBSIntercept_10 getIntercept(String _interceptName) throws Exception {
		if (_interceptName != null&&!_interceptName.equals( "" )) {
			return (IBSIntercept_10) Class.forName(_interceptName).newInstance();
		} else {
			return null;
		}
	}
}
