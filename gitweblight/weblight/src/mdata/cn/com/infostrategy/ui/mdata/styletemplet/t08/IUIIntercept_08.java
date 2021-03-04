/**************************************************************************
 * $RCSfile: IUIIntercept_08.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t08;

import cn.com.infostrategy.to.mdata.AggBillVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ���ӱ��ǰ��Intercept!!
 * @author user
 *
 */
public interface IUIIntercept_08 {

	//����ɾ��ǰ���Ķ���
	public void actionBeforeDelete_parent(BillListPanel _parentbilllistPanel, int _delerow) throws Exception;

	//�ӱ�ɾ��ǰ���Ķ���
	public void actionBeforeDelete_child(BillListPanel _parentbilllistPanel, int _delerow) throws Exception;

	//�������������Ķ���,����ʱ�����ǿ�Ƭ
	public void actionAfterInsert_parent(BillCardPanel _billcardPanel) throws Exception;

	//�ӱ����������Ķ���,����ʱ�ӱ����б�!!
	public void actionAfterInsert_child(BillListPanel _billListPanel, int _newrow) throws Exception;

	//�����޸ĺ����Ķ���,�޸�ʱ�����ǿ�Ƭ!!!
	public void actionAfterUpdate_parent(BillCardPanel _billcardPanel, String _itemkey) throws Exception; //

	//�ӱ��޸ĺ����Ķ���!!
	public void actionAfterUpdate_child(BillListPanel _billListPanel, String _itemkey, int _updatedrow) throws Exception; //

	//����ǰ����
	public void dealCommitBeforeInsert(cn.com.infostrategy.ui.mdata.styletemplet.t08.AbstractStyleWorkPanel_08 _frame, AggBillVO _insertobjs) throws Exception; //

	//��������
	public void dealCommitAfterInsert(cn.com.infostrategy.ui.mdata.styletemplet.t08.AbstractStyleWorkPanel_08 _frame, AggBillVO _insertobjs); //

	//ɾ��ǰ����
	public void dealCommitBeforeDelete(cn.com.infostrategy.ui.mdata.styletemplet.t08.AbstractStyleWorkPanel_08 _frame, AggBillVO _insertobjs) throws Exception; //

	//ɾ������
	public void dealCommitAfterDelete(cn.com.infostrategy.ui.mdata.styletemplet.t08.AbstractStyleWorkPanel_08 _frame, AggBillVO _insertobjs); //

	//�޸�ǰ����
	public void dealCommitBeforeUpdate(cn.com.infostrategy.ui.mdata.styletemplet.t08.AbstractStyleWorkPanel_08 _frame, AggBillVO _insertobjs) throws Exception; //

	//�޸ĺ���
	public void dealCommitAfterUpdate(cn.com.infostrategy.ui.mdata.styletemplet.t08.AbstractStyleWorkPanel_08 _frame, AggBillVO _insertobjs); //

}
/**************************************************************************
 * $RCSfile: IUIIntercept_08.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 *
 * $Log: IUIIntercept_08.java,v $
 * Revision 1.4  2012/09/14 09:22:58  xch123
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
 * Revision 1.3  2010/02/08 11:02:03  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:13:06  xuchanghua
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
 * Revision 1.1  2007/09/21 02:28:48  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:37  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/05 09:59:14  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:48:33  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/