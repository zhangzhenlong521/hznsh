/**************************************************************************
 * $RCSfile: IUIIntercept_01.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t01;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ���ģ��1��UI��������
 * @author xch
 *
 */
public interface IUIIntercept_01 {

	//������ʼ��..
	public void afterInitialize(AbstractStyleWorkPanel_01 _styleWorkPanel) throws Exception;

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
/**************************************************************************
 * $RCSfile: IUIIntercept_01.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 *
 * $Log: IUIIntercept_01.java,v $
 * Revision 1.4  2012/09/14 09:22:58  xch123
 * �ʴ��ֳ�����ͳһ�޸�
 *
 * Revision 1.1  2012/08/28 09:41:01  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:48  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:17  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:06  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:18  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:02:03  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:13:01  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/06/11 07:34:42  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:57  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:33  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:27  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:24  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:34  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:46  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:25  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:48:33  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
