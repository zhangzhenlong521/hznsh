package cn.com.infostrategy.bs.mdata.dataaccessfuns;

import cn.com.infostrategy.bs.mdata.DataAccessFunctionIFC;

/**
 * 取得登录人员的所属机构
 * @author xch
 *
 */
public class GetLoginUserCorp implements DataAccessFunctionIFC {

	private String str_CorpId = null; //人员所属机构id

	/**
	 * 构造方法
	 */
	public GetLoginUserCorp() {
		//在构造方法中先设好值,然后取时直接返回
		//先取得登录人员的id,然后去数据库中查询,然后赋给类变量即可
		str_CorpId = "1234";
	}

	public String getFunValue(String[] _pars) {
		return str_CorpId;
	}

}
