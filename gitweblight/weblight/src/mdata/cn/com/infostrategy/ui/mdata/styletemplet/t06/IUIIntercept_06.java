/**************************************************************************
 * $RCSfile: IUIIntercept_06.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t06;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public interface IUIIntercept_06 {

	//ɾ��ǰ���Ķ���
	public void actionBeforeDelete(BillListPanel _billlistPanel, int _delerow) throws Exception;

	//���������Ķ���
	public void actionAfterInsert(BillListPanel _billlistPanel, int _newrow) throws Exception;

	//�޸ĺ����Ķ���
	public void actionAfterUpdate(BillListPanel _billlistPanel, int _updatedrow, String _itemkey) throws Exception;  //
	
	

	//�ύǰ����У�鴦��!!!!�����ֱ���������,ɾ����,�޸ĵ�����!!!
	public void dealBeforeCommit(BillListPanel _billlistPanel, BillVO[] _insertobjs, BillVO[] _deleteobjs, BillVO[] _updateobjs) throws Exception; //

	//�ύ��ĺ�������..
	public void dealAfterCommit(BillListPanel _billlistPanel, BillVO[] _insertobjs, BillVO[] _deleteobjs, BillVO[] _updateobjs) throws Exception; //

}
/**************************************************************************
 * $RCSfile: IUIIntercept_06.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 *
 * $Log: IUIIntercept_06.java,v $
 * Revision 1.4  2012/09/14 09:22:57  xch123
 * �ʴ��ֳ�����ͳһ�޸�
 *
 * Revision 1.1  2012/08/28 09:41:02  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:46  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:17  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:06  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:19  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:02:00  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:13:05  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:11:00  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:34  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:28  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:25  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:35  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:47  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:32  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:48:32  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/