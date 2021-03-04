/**************************************************************************
 * $RCSfile: IBSIntercept_3A.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.bs.mdata.styletemplet.t3a;

import cn.com.infostrategy.to.mdata.BillVO;

public interface IBSIntercept_3A {
	/**δʹ��
	 * 
	 * @param _dsName
	 * @param _insertobjs
	 * @throws Exception
	 */
	//����ǰ���� 
	public void dealCommitBeforeInsertB(String _dsName, BillVO _insertobjs) throws Exception; //
	/**δʹ��
	 * 
	 * @param _dsName
	 * @param _insertobjs
	 * @throws Exception
	 */
	//��������δʹ��
	public void dealCommitAfterInsertB(String _dsName, BillVO _insertobjs) throws Exception; //
	/**δʹ��
	 * 
	 * @param _dsName
	 * @param _insertobjs
	 * @throws Exception
	 */
	//ɾ��ǰ����δʹ��
	public void dealCommitBeforeDeleteB(String _dsName, BillVO _deleteobjs) throws Exception; //
	/**δʹ��
	 * 
	 * @param _dsName
	 * @param _insertobjs
	 * @throws Exception
	 */
	//ɾ������δʹ��
	public void dealCommitAfterDeleteB(String _dsName, BillVO _deleteobjs) throws Exception; //
	/**δʹ��
	 * 
	 * @param _dsName
	 * @param _insertobjs
	 * @throws Exception
	 */
	//�޸�ǰ����δʹ��
	public void dealCommitBeforeUpdateB(String _dsName, BillVO _updateobjs) throws Exception; //
	/**δʹ��
	 * 
	 * @param _dsName
	 * @param _insertobjs
	 * @throws Exception
	 */
	//�޸ĺ���δʹ��
	public void dealCommitAfterUpdateB(String _dsName, BillVO _updateobjs) throws Exception; //
	/**δʹ��
	 * 
	 * @param _dsName
	 * @param _insertobjs
	 * @throws Exception
	 */
	//����ǰ����δʹ��
	public void dealCommitBeforeInsertC(String _dsName, BillVO _insertobjs) throws Exception; //
	/**δʹ��
	 * 
	 * @param _dsName
	 * @param _insertobjs
	 * @throws Exception
	 */
	//��������δʹ��
	public void dealCommitAfterInsertC(String _dsName, BillVO _insertobjs) throws Exception; //
	/**δʹ��
	 * 
	 * @param _dsName
	 * @param _insertobjs
	 * @throws Exception
	 */
	//ɾ��ǰ����δʹ��
	public void dealCommitBeforeDeleteC(String _dsName, BillVO _deleteobjs) throws Exception; //
	/**δʹ��
	 * 
	 * @param _dsName
	 * @param _insertobjs
	 * @throws Exception
	 */
	//ɾ������δʹ��
	public void dealCommitAfterDeleteC(String _dsName, BillVO _deleteobjs) throws Exception; //
	/**δʹ��
	 * 
	 * @param _dsName
	 * @param _insertobjs
	 * @throws Exception
	 */
	//�޸�ǰ����δʹ��
	public void dealCommitBeforeUpdateC(String _dsName, BillVO _updateobjs) throws Exception; //
	/**δʹ��
	 * 
	 * @param _dsName
	 * @param _insertobjs
	 * @throws Exception
	 */
	//�޸ĺ���δʹ��
	public void dealCommitAfterUpdateC(String _dsName, BillVO _updateobjs) throws Exception; //
	/**************************************************************************
	 * $RCSfile: IBSIntercept_3A.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
	 *
	 * $Log: IBSIntercept_3A.java,v $
	 * Revision 1.4  2012/09/14 09:22:58  xch123
	 * �ʴ��ֳ�����ͳһ�޸�
	 *
	 * Revision 1.1  2012/08/28 09:40:52  Administrator
	 * *** empty log message ***
	 *
	 * Revision 1.3  2011/10/10 06:31:48  wanggang
	 * restore
	 *
	 * Revision 1.1  2011/04/02 11:43:59  xch123
	 * *** empty log message ***
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
	 * Revision 1.6  2010/02/08 11:02:04  sunfujun
	 * *** empty log message ***
	 *
	 * Revision 1.4  2010/02/08 05:20:42  sunfujun
	 * *** empty log message ***
	 *
	 * Revision 1.3  2010/02/08 05:19:40  sunfujun
	 * *** empty log message ***
	 *
	 * Revision 1.2  2010/02/08 05:07:08  sunfujun
	 * *** empty log message ***
	 *
	 * Revision 1.1  2010/02/03 05:23:07  lichunjuan
	 * *** empty log message ***
	 *
	 * Revision 1.1  2010/02/02 05:18:32  lcj
	 * *** empty log message ***
	 *
	 **************************************************************************/
}
