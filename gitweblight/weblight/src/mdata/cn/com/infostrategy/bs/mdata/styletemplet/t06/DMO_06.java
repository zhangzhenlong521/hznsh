/**************************************************************************
 * $RCSfile: DMO_06.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/

package cn.com.infostrategy.bs.mdata.styletemplet.t06;


import java.util.HashMap;
import java.util.Vector;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.mdata.BillVO;

public class DMO_06 extends AbstractDMO{

	public DMO_06() {

	}

	public HashMap dealCommit(String _dsName, String _bsInterceptName, BillVO[] _insertobjs, BillVO[] _deleteobjs, BillVO[] _updateobjs) throws Exception {
		IBSIntercept_06 bsIntercept = getIntercept(_bsInterceptName); // 创建BS端拦截器

		// 新增前拦截!!
		if (bsIntercept != null) {
			bsIntercept.dealBeforeCommit(_dsName, _insertobjs, _deleteobjs, _updateobjs);
		}

		Vector v_sqls = new Vector(); //
		if (_deleteobjs != null && _deleteobjs.length > 0) {
			for (int i = 0; i < _deleteobjs.length; i++) {
				String str_delSQL = _deleteobjs[i].getDeleteSQL();
				v_sqls.add(str_delSQL);
			}
		}

		if (_insertobjs != null && _insertobjs.length > 0) {
			for (int i = 0; i < _insertobjs.length; i++) {
				String str_insertSQL = _insertobjs[i].getInsertSQL();
				v_sqls.add(str_insertSQL);
			}
		}

		if (_updateobjs != null && _updateobjs.length > 0) {
			for (int i = 0; i < _updateobjs.length; i++) {
				//				// 乐观锁检查,,如果需要有乐观锁处理!!!
				//				if (_updateobjs[i].isDealVersion()) {
				//					Double ld_version = _updateobjs[i].getVersion(); // 取得当前版本号!!
				//					if (ld_version != null && getVersion(_updateobjs[i]) != null && !ld_version.equals(getVersion(_updateobjs[i]))) {
				//						throw new Exception("所操作数据已经被其他用户修改，请刷新数据后再进行操作！");
				//					}
				//				}
				String str_updateSQL = _updateobjs[i].getUpdateSQL();
				v_sqls.add(str_updateSQL);
			}
		}

		ServerEnvironment.getCommDMO().executeBatchByDS(_dsName, v_sqls); //真正提交数据库,核心地带!!

		// 新增后拦截!!
		if (bsIntercept != null) {
			bsIntercept.dealAfterCommit(_dsName, _insertobjs, _deleteobjs, _updateobjs);
		}

		HashMap BillVOMap = new HashMap(); //
		BillVOMap.put("INSERT", _insertobjs); //
		BillVOMap.put("DELETE", _deleteobjs); //
		BillVOMap.put("UPDATE", _updateobjs); //

		return BillVOMap;
	}

	private Double getVersion(String _dsName, BillVO _updateobj) {
		StringBuffer str_sql = new StringBuffer();
		str_sql.append("SELECT nvl(version,1) FROM " + _updateobj.getSaveTableName() + " WHERE" + _updateobj.getUpdateWhereCondition() + "");
		TableDataStruct tabledata = null;
		String[][] data = null;
		try {
			System.out.println("查询数据库Version:" + str_sql.toString());
			tabledata = ServerEnvironment.getCommDMO().getTableDataStructByDS(null, str_sql.toString());
			data = tabledata.getBodyData();
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
	private IBSIntercept_06 getIntercept(String _interceptName) throws Exception {
		if (_interceptName != null&&!_interceptName.equals( "" )) {
			return (IBSIntercept_06) Class.forName(_interceptName).newInstance();
		} else {
			return null;
		}
	}

}
/*******************************************************************************
 * $RCSfile: DMO_06.java,v $ $Revision: 1.4 $ $Date: 2012/09/14 09:22:58 $
 * 
 * $Log: DMO_06.java,v $
 * Revision 1.4  2012/09/14 09:22:58  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:52  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:47  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:04  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:53  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:32:56  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:02:03  sunfujun
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
 * Revision 1.2  2008/01/17 02:48:19  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/11/13 05:58:00  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/09/25 03:25:07  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:27  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:38  xch
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/15 07:00:31  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/09 01:17:19  sunxf
 * *** empty log message ***
 *
 * Revision 1.2  2007/03/08 11:03:01  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/03/08 10:42:29  shxch
 * *** empty log message ***
 *
 * Revision 1.6  2007/03/08 10:40:41  shxch
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/02 05:02:51  shxch
 * *** empty log message ***
 *
 * Revision 1.4  2007/02/05 04:40:44  lujian
 * *** empty log message ***
 * Revision 1.3 2007/02/02 08:52:25 lujian *** empty log
 * message ***
 * 
 * Revision 1.2 2007/01/30 04:32:02 lujian *** empty log message ***
 * 
 * 
 ******************************************************************************/
