/**************************************************************************
 * $RCSfile: DMO_08.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.bs.mdata.styletemplet.t08;


import java.util.Vector;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.mdata.AggBillVO;
import cn.com.infostrategy.to.mdata.BillVO;

public class DMO_08 extends AbstractDMO{

	/**
	 * ����!!!
	 * @param _dsName
	 * @param _bsInterceptName
	 * @param _aggVO
	 * @return
	 * @throws Exception
	 */
	public AggBillVO dealInsert(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception {
		IBSIntercept_08 bsIntercept = getIntercept(_bsInterceptName); // ����BS��������

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

	//ɾ��
	public void dealDelete(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception {
		IBSIntercept_08 bsIntercept = getIntercept(_bsInterceptName); // ����BS��������

		// ɾ��ǰ����!!
		if (bsIntercept != null) {
			bsIntercept.dealCommitBeforeDelete(_dsName, _aggVO);
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
		v_sqls.add(_aggVO.getParentVO().getDeleteSQL()); //��������������SQL

		ServerEnvironment.getCommDMO().executeBatchByDS(_dsName, v_sqls); //�����������ݿ�!!!���ĵش�!!!

		// ɾ��������!!
		if (bsIntercept != null) {
			bsIntercept.dealCommitAfterDelete(_dsName, _aggVO);
		}
	}

	//�޸�
	public AggBillVO dealUpdate(String _dsName, String _bsInterceptName, AggBillVO _aggVO) throws Exception {
		IBSIntercept_08 bsIntercept = getIntercept(_bsInterceptName); // ����BS��������

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

		// ����ȥ�޸�!!!!
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
	private IBSIntercept_08 getIntercept(String _interceptName) throws Exception {
		if (_interceptName != null&&!_interceptName.equals( "" )) {
			return (IBSIntercept_08) Class.forName(_interceptName).newInstance();
		} else {
			return null;
		}
	}

}
/**************************************************************************
 * $RCSfile: DMO_08.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 *
 * $Log: DMO_08.java,v $
 * Revision 1.4  2012/09/14 09:22:57  xch123
 * �ʴ��ֳ�����ͳһ�޸�
 *
 * Revision 1.1  2012/08/28 09:40:52  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:48  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:04  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:54  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:32:56  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:02:04  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:46  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:23  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:24  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:04  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:13  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:20  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/11/13 05:57:59  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/09/25 03:25:04  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:27  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:38  xch
 * *** empty log message ***
 *
 * Revision 1.9  2007/03/15 07:00:31  shxch
 * *** empty log message ***
 *
 * Revision 1.8  2007/03/09 01:17:20  sunxf
 * *** empty log message ***
 *
 * Revision 1.7  2007/03/08 10:59:49  shxch
 * *** empty log message ***
 *
 * Revision 1.6  2007/03/08 10:14:04  shxch
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/02 05:02:51  shxch
 * *** empty log message ***
 *
 * Revision 1.4  2007/02/05 04:40:43  lujian
 * *** empty log message ***
 *
 * Revision 1.3  2007/02/02 08:52:25  lujian
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:32:03  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
