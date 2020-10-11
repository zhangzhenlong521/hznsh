/**************************************************************************
 * $RCSfile: DMO_02.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/

package cn.com.infostrategy.bs.mdata.styletemplet.t02;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.mdata.BillVO;

public class DMO_02 extends AbstractDMO {

	public DMO_02() {
	}

	public BillVO dealInsert(String _dsName, String _bsInterceptName, BillVO _insertobj) throws Exception {
		IBSIntercept_02 bsIntercept = getIntercept(_bsInterceptName); // 拦截器

		// 新增前拦截!!
		if (bsIntercept != null) {
			bsIntercept.dealCommitBeforeInsert(_dsName, _insertobj);
		}

		// 直正去新增!!!!!!!!!!!!!!
		String str_sql = _insertobj.getInsertSQL(); // 实际的SQL
		ServerEnvironment.getCommDMO().executeUpdateByDS(_dsName, str_sql); //真正提交数据库!

		// 新增后拦截!!
		if (bsIntercept != null) {
			bsIntercept.dealCommitAfterInsert(_dsName, _insertobj);
		}

		return _insertobj;
	}

	public void dealDelete(String _dsName, String _bsInterceptName, BillVO _deleteobj) throws Exception {
		IBSIntercept_02 bsIntercept = getIntercept(_bsInterceptName); // 拦截器

		// 删除前拦截!!
		if (bsIntercept != null) {
			bsIntercept.dealCommitBeforeDelete(_bsInterceptName, _deleteobj);
		}

		// 真正去删除
		String str_sql = _deleteobj.getDeleteSQL(); // 有?的SQL
		ServerEnvironment.getCommDMO().executeUpdateByDS(_dsName, str_sql); //真正提交数据库!

		// 删除后拦截!!
		if (bsIntercept != null) {
			bsIntercept.dealCommitAfterDelete(_dsName, _deleteobj);
		}
	}

	public BillVO dealUpdate(String _dsName, String _bsInterceptName, BillVO _updateobj) throws Exception {
		IBSIntercept_02 bsIntercept = getIntercept(_bsInterceptName); // 拦截器

		if (bsIntercept != null) {
			bsIntercept.dealCommitBeforeUpdate(_dsName, _updateobj);
		}

		// 乐观锁检查,,如果需要有乐观锁处理!!!
		if (_updateobj.isDealVersion()) {
			Double ld_version = _updateobj.getVersion(); // 取得当前版本号!!
			Double ld_currVersion = getVersion(_dsName, _updateobj); //
			if (ld_version != null && ld_currVersion != null && !ld_version.equals(ld_currVersion)) {
				throw new Exception("所操作数据已经被其他用户修改，请刷新数据后再进行操作！");
			}
		}

		String str_sql = _updateobj.getUpdateSQL();
		ServerEnvironment.getCommDMO().executeUpdateByDS(_dsName, str_sql); //真正提交数据库!

		if (bsIntercept != null) {
			bsIntercept.dealCommitAfterUpdate(_dsName, _updateobj);
		}

		return _updateobj;
	}

	/**
	 * 取得当前版本
	 * @param _dsName
	 * @param _updateobj
	 * @return
	 */
	private Double getVersion(String _dsName, BillVO _updateobj) {
		StringBuffer str_sql = new StringBuffer();
		str_sql.append("select nvl(version,1) from " + _updateobj.getSaveTableName() + " where" + _updateobj.getUpdateWhereCondition());
		String[][] data = null;
		try {
			data = ServerEnvironment.getCommDMO().getStringArrayByDS(_dsName, str_sql.toString());
			return new Double(data[0][0]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 取得服务器端拦截器类
	 * @param _interceptName
	 * @return
	 * @throws Exception
	 */
	private IBSIntercept_02 getIntercept(String _interceptName) throws Exception {
		if (_interceptName != null && !_interceptName.equals("")) {
			return (IBSIntercept_02) Class.forName(_interceptName).newInstance();
		} else {
			return null;
		}
	}
}
/*******************************************************************************
 * $RCSfile: DMO_02.java,v $ $Revision: 1.4 $ $Date: 2012/09/14 09:22:57 $
 * 
 * $Log: DMO_02.java,v $
 * Revision 1.4  2012/09/14 09:22:57  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:51  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:46  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:04  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:53  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:32:55  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:02:02  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:46  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:22  xuchanghua
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
 * Revision 1.2  2008/01/17 02:48:19  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/11/13 05:58:01  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/09/25 03:25:05  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:26  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:30  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/15 07:00:32  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/03/09 01:14:46  sunxf
 * *** empty log message ***
 *
 * Revision 1.1  2007/03/08 10:42:29  shxch
 * *** empty log message ***
 *
 * Revision 1.7  2007/03/08 10:40:41  shxch
 * *** empty log message ***
 *
 * Revision 1.6  2007/03/08 08:24:34  shxch
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/02 05:02:51  shxch
 * *** empty log message ***
 *
 * Revision 1.4  2007/02/05 04:40:43  lujian
 * *** empty log message ***
 * Revision 1.3 2007/02/02 08:52:25 lujian *** empty log
 * message ***
 * 
 * Revision 1.2 2007/01/30 04:32:03 lujian *** empty log message ***
 * 
 * 
 ******************************************************************************/
