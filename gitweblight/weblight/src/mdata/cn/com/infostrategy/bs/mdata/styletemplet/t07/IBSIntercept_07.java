/**************************************************************************
 * $RCSfile: IBSIntercept_07.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.bs.mdata.styletemplet.t07;

import cn.com.infostrategy.to.mdata.BillVO;

public interface IBSIntercept_07 {

	//新增前处理
	public void dealCommitBeforeInsert(String _dsName, BillVO _insertobjs) throws Exception; //

	//新增后处理
	public void dealCommitAfterInsert(String _dsName, BillVO _insertobjs) throws Exception; //

	//删除前处理
	public void dealCommitBeforeDelete(String _dsName, BillVO _deleteobjs) throws Exception; //

	//删除后处理
	public void dealCommitAfterDelete(String _dsName, BillVO _deleteobjs) throws Exception; //

	//修改前处理
	public void dealCommitBeforeUpdate(String _dsName, BillVO _updateobjs) throws Exception; //

	//修改后处理
	public void dealCommitAfterUpdate(String _dsName, BillVO _updateobjs) throws Exception; //

}
/**************************************************************************
 * $RCSfile: IBSIntercept_07.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 *
 * $Log: IBSIntercept_07.java,v $
 * Revision 1.4  2012/09/14 09:22:58  xch123
 * 邮储现场回来统一修改
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
 * Revision 1.3  2010/02/08 11:01:58  sunfujun
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
 * Revision 1.3  2007/09/20 05:08:36  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/08 10:53:18  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:32:02  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
