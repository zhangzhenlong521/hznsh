/**************************************************************************
 * $RCSfile: IBSIntercept_08.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.bs.mdata.styletemplet.t08;

import cn.com.infostrategy.to.mdata.AggBillVO;

public interface IBSIntercept_08 {

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
/**************************************************************************
 * $RCSfile: IBSIntercept_08.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 *
 * $Log: IBSIntercept_08.java,v $
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
 * Revision 1.1  2007/09/21 02:28:27  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:38  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/08 10:14:04  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:32:03  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
