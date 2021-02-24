/**************************************************************************
 * $RCSfile: IUIIntercept_08.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t08;

import cn.com.infostrategy.to.mdata.AggBillVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 主子表的前端Intercept!!
 * @author user
 *
 */
public interface IUIIntercept_08 {

	//主表删除前做的动作
	public void actionBeforeDelete_parent(BillListPanel _parentbilllistPanel, int _delerow) throws Exception;

	//子表删除前做的动作
	public void actionBeforeDelete_child(BillListPanel _parentbilllistPanel, int _delerow) throws Exception;

	//主表新增后做的动作,新增时主表是卡片
	public void actionAfterInsert_parent(BillCardPanel _billcardPanel) throws Exception;

	//子表新增后做的动作,新增时子表是列表!!
	public void actionAfterInsert_child(BillListPanel _billListPanel, int _newrow) throws Exception;

	//主表修改后做的动作,修改时主表是卡片!!!
	public void actionAfterUpdate_parent(BillCardPanel _billcardPanel, String _itemkey) throws Exception; //

	//子表修改后做的动作!!
	public void actionAfterUpdate_child(BillListPanel _billListPanel, String _itemkey, int _updatedrow) throws Exception; //

	//新增前处理
	public void dealCommitBeforeInsert(cn.com.infostrategy.ui.mdata.styletemplet.t08.AbstractStyleWorkPanel_08 _frame, AggBillVO _insertobjs) throws Exception; //

	//新增后处理
	public void dealCommitAfterInsert(cn.com.infostrategy.ui.mdata.styletemplet.t08.AbstractStyleWorkPanel_08 _frame, AggBillVO _insertobjs); //

	//删除前处理
	public void dealCommitBeforeDelete(cn.com.infostrategy.ui.mdata.styletemplet.t08.AbstractStyleWorkPanel_08 _frame, AggBillVO _insertobjs) throws Exception; //

	//删除后处理
	public void dealCommitAfterDelete(cn.com.infostrategy.ui.mdata.styletemplet.t08.AbstractStyleWorkPanel_08 _frame, AggBillVO _insertobjs); //

	//修改前处理
	public void dealCommitBeforeUpdate(cn.com.infostrategy.ui.mdata.styletemplet.t08.AbstractStyleWorkPanel_08 _frame, AggBillVO _insertobjs) throws Exception; //

	//修改后处理
	public void dealCommitAfterUpdate(cn.com.infostrategy.ui.mdata.styletemplet.t08.AbstractStyleWorkPanel_08 _frame, AggBillVO _insertobjs); //

}
/**************************************************************************
 * $RCSfile: IUIIntercept_08.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 *
 * $Log: IUIIntercept_08.java,v $
 * Revision 1.4  2012/09/14 09:22:58  xch123
 * 邮储现场回来统一修改
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