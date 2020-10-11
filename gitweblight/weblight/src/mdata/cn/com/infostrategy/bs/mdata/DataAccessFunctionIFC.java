package cn.com.infostrategy.bs.mdata;

/**
 * 数据权限之公式接口,所有的公式或自定义类都必须实现该接口!比如：
 * getCorpFH({createcorp});  //根据机构id计算出所属分行
 * getLoginUserBLFH();  //计算出登录人员所属分行
 * isSupperAdmin();  //是否超级管理员
 * getLoginUserCorp();  //计算得到登录人员的所属机构
 * getCorpByUserID({createuserid});  //根据人员ID作为参数计算得到其所属机构
 * isZHorFH(); //是总行还是分行
 * 
 * 参数中带{}的说明是宏代码，即需要在运行时拿实现数据的对应字段进行替换的!!
 * 所有接口的实现类,应该注意在构造方法中将所有数据都预先从数据库中取出来，然后只在接口方法中进行内存计算，这样性能才高！
 * 如果一个函数没有一个参数，则应该直接在构造方法中都计算好，然后在接口方法中直接返回这个类变量才对！
 * @author xch
 */
public interface DataAccessFunctionIFC {

	public static String[][] regFunctions = new String[][] { //所有静态函数,即在条件定义中使用,或资源公式定义的值定义中使用!!!
	{ "getLoginUserCorp()", "cn.com.infostrategy.bs.mdata.dataaccessfuns.GetLoginUserCorp", "取得登录人员所属机构" }, //
			{ "getLoginUserBelongFHCorp()", "", "" }, //取得登录人员所属分行的id
			{ "getLoginUserIsZHorFH()", "" }, //判断登录人员是总行还是分行,返回【总行/分行】
			{ "getLoginUserRoleCodes()", "" }, //取得登录人员所有角色编码,返回【合规管理员;法律管理员;反洗钱管理员...】
			{ "getLoginUserPostCodes()", "" }, //取得登录人所所属的岗位
			{ "isLoginUserInRoleCodes(\"分行合规管理员\",\"分行法律管理员\")", }, //登录人员是否具有哪些角色,返回【是/否】
			{ "isLoginUserInPostCodes(\"法律岗\",\"反洗钱岗\",\"分行行长\")", }, //登录人员是否具有哪些岗位
			{ "getBelongFHByCorpID({CreateCorp})", "cn.com.infostrategy.bs.mdata.dataaccessfuns.GetBelongFHByCorpID", "根据机构id取得其所属分行" }, //根据机构id取得其所属分行
			{ "getCorpIDByUserID({CreateUser})", "", "根据人员id取得其所属机构id" }, //根据人员id取得其所属机构id
			{ "getBelongFHByUserID({CreateUser})", "" }, //根据人员id取得其所属机构的所属分行的id
			{ "getAllRoleCodesByUserID({CreateUser})", "" }, //根据人员id取得其所具有的所有角色编码,以分号隔开
			{ "getAllPostCodesByUserID({CreateUser})", "" }, //根据人员id取得其所具有的所有岗位编码,以分号隔开
			{ "getColValueByItemId(\"pub_comboboxdict\",\"name\",\"id\",{filetype},\"type='体系文件类型'\")", "" }, //根据id等字段去一个表中取得另一个字段的值!!
	};

	/**
	 * 取得计算的值
	 * @param _pars 参数,按道理只要一个参数即可,但考虑到扩展,支持数组!即许多功能可以使用一个函数或类搞定,而不需要写多个类,维护起来也方便!!!
	 * @return
	 */
	public String getFunValue(String[] _pars); //////

}
