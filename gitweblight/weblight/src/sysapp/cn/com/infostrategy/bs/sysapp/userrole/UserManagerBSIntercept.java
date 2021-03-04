package cn.com.infostrategy.bs.sysapp.userrole;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.mdata.styletemplet.t02.IBSIntercept_02;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.mdata.BillVO;

public class UserManagerBSIntercept implements IBSIntercept_02 {

	private CommDMO dmo = null;

	public void dealCommitAfterDelete(String _dsName, BillVO _deleteobjs) throws Exception {
		String str_userid = _deleteobjs.getPkValue(); //
		String str_1 = "delete from pub_user_role where userid='" + str_userid + "'"; //
		String str_2 = "delete from pub_user_menu where userid='" + str_userid + "'"; //
		getDMO().executeBatchByDS(_dsName, new String[] { str_1, str_2 }); //
	}

	public void dealCommitAfterInsert(String _dsName, BillVO _insertobjs) throws Exception {

	}

	public void dealCommitAfterUpdate(String _dsName, BillVO _updateobjs) throws Exception {

	}

	public void dealCommitBeforeDelete(String _dsName, BillVO _deleteobjs) throws Exception {
		String str_userid = _deleteobjs.getPkValue(); //
		if(str_userid.equalsIgnoreCase(getDMO().getCurrSession().getLoginUserId())){
			throw new WLTAppException("当前用户不能删除自己！");
		}
		
	}

	public void dealCommitBeforeInsert(String _dsName, BillVO _insertobjs) throws Exception {

	}

	public void dealCommitBeforeUpdate(String _dsName, BillVO _updateobjs) throws Exception {

	}

	private CommDMO getDMO() {
		if (dmo == null) {
			dmo = ServerEnvironment.getCommDMO();
		}
		return dmo;
	}
}
