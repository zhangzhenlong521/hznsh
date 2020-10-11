/**************************************************************************
 * $RCSfile: IUIIntercept_02.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t02;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public interface IUIIntercept_02 {

	//初始化结点后还有一步可以做
	public abstract void afterInitialize(AbstractStyleWorkPanel_02 _styleWorkPanel) throws Exception;

	//删除前做的动作
	public abstract void actionBeforeDelete(BillListPanel _billlistPanel, int _delerow) throws Exception;

	//新增后做的动作
	public abstract void actionAfterInsert(BillCardPanel _billCardPanel) throws Exception;
	
	//修改前做的动作!
	public abstract void actionBeforeUpdate(BillCardPanel _billCardPanel) throws Exception;

	//修改后做的动作,即在触发编辑公式后做的!
	public abstract void actionAfterUpdate(BillCardPanel _billCardPanel, String _itemkey) throws Exception;

	//新增提交前处理,一般是校验
	public abstract void dealCommitBeforeInsert(cn.com.infostrategy.ui.mdata.styletemplet.t02.AbstractStyleWorkPanel_02 _frame, BillVO _insertobjs) throws Exception; //

	//新增提交后处理,一般是重置页面控件!!比如有一项必须在后台经过复杂的运算后才能知道其值!!!
	public abstract void dealCommitAfterInsert(cn.com.infostrategy.ui.mdata.styletemplet.t02.AbstractStyleWorkPanel_02 _frame, BillVO _insertobjs); //

	//删除提交前处理,一般是校验
	public abstract void dealCommitBeforeDelete(cn.com.infostrategy.ui.mdata.styletemplet.t02.AbstractStyleWorkPanel_02 _frame, BillVO _deleteobjs) throws Exception; //

	//删除提交后处理,一般是重置页面控件!!
	public abstract void dealCommitAfterDelete(cn.com.infostrategy.ui.mdata.styletemplet.t02.AbstractStyleWorkPanel_02 _frame, BillVO _deleteobjs); //

	//修改提交前处理,一般是校验
	public abstract void dealCommitBeforeUpdate(cn.com.infostrategy.ui.mdata.styletemplet.t02.AbstractStyleWorkPanel_02 _frame, BillVO _updateobjs) throws Exception; //

	//修改提交后处理,一般是重置页面控件!!
	public abstract void dealCommitAfterUpdate(cn.com.infostrategy.ui.mdata.styletemplet.t02.AbstractStyleWorkPanel_02 _frame, BillVO _updateobjs); //

}
/**************************************************************************
 * $RCSfile: IUIIntercept_02.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 *
 * $Log: IUIIntercept_02.java,v $
 * Revision 1.4  2012/09/14 09:22:58  xch123
 * 邮储现场回来统一修改
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
