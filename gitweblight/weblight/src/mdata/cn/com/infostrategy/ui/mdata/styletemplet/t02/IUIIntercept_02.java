/**************************************************************************
 * $RCSfile: IUIIntercept_02.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t02;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public interface IUIIntercept_02 {

	//��ʼ��������һ��������
	public abstract void afterInitialize(AbstractStyleWorkPanel_02 _styleWorkPanel) throws Exception;

	//ɾ��ǰ���Ķ���
	public abstract void actionBeforeDelete(BillListPanel _billlistPanel, int _delerow) throws Exception;

	//���������Ķ���
	public abstract void actionAfterInsert(BillCardPanel _billCardPanel) throws Exception;
	
	//�޸�ǰ���Ķ���!
	public abstract void actionBeforeUpdate(BillCardPanel _billCardPanel) throws Exception;

	//�޸ĺ����Ķ���,���ڴ����༭��ʽ������!
	public abstract void actionAfterUpdate(BillCardPanel _billCardPanel, String _itemkey) throws Exception;

	//�����ύǰ����,һ����У��
	public abstract void dealCommitBeforeInsert(cn.com.infostrategy.ui.mdata.styletemplet.t02.AbstractStyleWorkPanel_02 _frame, BillVO _insertobjs) throws Exception; //

	//�����ύ����,һ��������ҳ��ؼ�!!������һ������ں�̨�������ӵ���������֪����ֵ!!!
	public abstract void dealCommitAfterInsert(cn.com.infostrategy.ui.mdata.styletemplet.t02.AbstractStyleWorkPanel_02 _frame, BillVO _insertobjs); //

	//ɾ���ύǰ����,һ����У��
	public abstract void dealCommitBeforeDelete(cn.com.infostrategy.ui.mdata.styletemplet.t02.AbstractStyleWorkPanel_02 _frame, BillVO _deleteobjs) throws Exception; //

	//ɾ���ύ����,һ��������ҳ��ؼ�!!
	public abstract void dealCommitAfterDelete(cn.com.infostrategy.ui.mdata.styletemplet.t02.AbstractStyleWorkPanel_02 _frame, BillVO _deleteobjs); //

	//�޸��ύǰ����,һ����У��
	public abstract void dealCommitBeforeUpdate(cn.com.infostrategy.ui.mdata.styletemplet.t02.AbstractStyleWorkPanel_02 _frame, BillVO _updateobjs) throws Exception; //

	//�޸��ύ����,һ��������ҳ��ؼ�!!
	public abstract void dealCommitAfterUpdate(cn.com.infostrategy.ui.mdata.styletemplet.t02.AbstractStyleWorkPanel_02 _frame, BillVO _updateobjs); //

}
/**************************************************************************
 * $RCSfile: IUIIntercept_02.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 *
 * $Log: IUIIntercept_02.java,v $
 * Revision 1.4  2012/09/14 09:22:58  xch123
 * �ʴ��ֳ�����ͳһ�޸�
 *
 * Revision 1.1  2012/08/28 09:41:01  Administrator
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
 * Revision 1.1  2010/04/08 04:33:18  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2010/02/08 11:02:04  sunfujun
 * *** empty log message ***
 *
 * Revision 1.2  2010/01/04 11:56:40  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:13:01  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2009/06/11 07:14:38  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/06/08 02:06:49  xuchanghua
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
 * Revision 1.3  2007/09/20 05:08:13  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/05 09:59:14  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:48:30  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
