/**************************************************************************
 * $RCSfile: JepFormulaParse.java,v $  $Revision: 1.21 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Vector;

import org.nfunk.jep.JEP;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.mdata.SystemOutPrintln;
import cn.com.infostrategy.ui.mdata.ThrowException;
import cn.com.infostrategy.ui.mdata.formatcomp.GetUserRole;

/**
 * 公式解析器的父类!!也是抽象类!!!在这里定义大家共用的注册的一些方法!!
 * 
 * @author xch
 * 
 */
public abstract class JepFormulaParse {

	protected JEP parser = new JEP();

	protected abstract byte getJepType(); // Jep的类型!!!

	//标准化的函数,即只要最基本的公式计算!在万维报表的警界值计算中需要用到!
	protected void initStandardFunction() {
		parser.addStandardFunctions(); // 增加所有标准函数!!
		parser.addStandardConstants(); // 增加所有变量!!!
	}

	/**
	 * 标准的常用的计算函数!!
	 */
	protected void initNormalFunction() {
		parser.addStandardFunctions(); // 增加所有标准函数!!
		parser.addStandardConstants(); // 增加所有变量!!!

		// 注册所有函数
		parser.addFunction("getColValue", new GetColValue(getJepType())); // 加入getColValue()函数
		parser.addFunction("getSQLValue", new GetSQLValue(getJepType())); // 加入getColValue()函数

		parser.addFunction("getTreePathColValue", new GetTreePathTableName(getJepType())); // 加入getColValue()函数

		parser.addFunction("getColValueByDS", new GetColValueByDS(getJepType())); // 加入getColValue()函数,根据数据源取数!!!!
		parser.addFunction("getFnValue", new GetFnValue(getJepType())); // 获得存储函数值,很有用的!!!
		parser.addFunction("getClassValue", new GetClassValue(getJepType())); // 通过反射类生成数据
		parser.addFunction("DecideMessageBox", new DecideMessageBox()); // 加入getLoginCode()函数

		parser.addFunction("getLoginUserId", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_ID)); // 加入getLoginCode()函数
		parser.addFunction("getLoginUserCode", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_CODE)); // 加入getLoginCode()函数
		parser.addFunction("getLoginUserName", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_NAME)); // 加入getLoginName()函数
		parser.addFunction("getLoginUserDeptQSql", new GetLoginUserSql(getJepType(), "corp"));
		parser.addFunction("getLoginUserRoleQSql", new GetLoginUserSql(getJepType(), "role"));
		parser.addFunction("getLoginUserDeptId", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPTID)); // 登录人员所属部门主键
		parser.addFunction("getLoginUserDeptName", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPTNAME)); // 登录人员所属机构名称
		parser.addFunction("getLoginUserDeptType", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_CORPTYPE)); // 登录人员所属部门名称

		parser.addFunction("getCorpIdByType", new GetCorpIdByType(getJepType())); //根据机构类型取得机构ID,比如类型为总行
		parser.addFunction("getLoginUserParentCorpItemValueByType", new GetLoginUserParentCorpItemValueByType(getJepType())); //取得登录人员的所属机构的所有上级机构中机构类型为指定类型的机构!!	

		parser.addFunction("getBusitypeCode", new GetBusitypeCode()); // 得到编号规则
		//某些地方用到以下方法，但注释了，就跑不通了，先不注释了，以后有好的办法再改！
		parser.addFunction("getLoginUser_DeptId", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPTID)); // 登录人员所属部门主键
		parser.addFunction("getLoginUser_DeptCode", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPTCODE)); // 登录人员所属部门编码
		parser.addFunction("getLoginUser_DeptName", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPTNAME)); // 登录人员所属部门名称
		parser.addFunction("getLoginUser_DeptType", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_CORPTYPE)); // 登录人员所属部门名称

		parser.addFunction("getLoginUser_Dept_BLZHBM", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLZHBM)); // 登录人员所属机构之所属总行部门
		parser.addFunction("getLoginUser_Dept_BLZHBM_NAME", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLZHBM_NAME)); // 登录人员所属机构之所属总行部门

		parser.addFunction("getLoginUser_Dept_FENGH", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLFENGH)); // 登录人员所属机构之所属分行
		parser.addFunction("getLoginUser_Dept_FENGH_NAME", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLFENGH_NAME)); // 登录人员所属机构之所属分行

		parser.addFunction("getLoginUser_Dept_FENGHBM", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLFENGHBM)); // 登录人员所属机构之所属分行部门
		parser.addFunction("getLoginUser_Dept_FENGHBM_NAME", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLFENGHBM_NAME)); // 登录人员所属机构之所属分行部门

		parser.addFunction("getLoginUser_Dept_ZHIH", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLZHIH)); // 登录人员所属机构之所属支行
		parser.addFunction("getLoginUser_Dept_ZHIH_NAME", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLZHIH_NAME)); // 登录人员所属机构之所属支行

		parser.addFunction("getLoginUser_Dept_SHIYB", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLSHIYB)); // 登录人员所属机构之所属事业部
		parser.addFunction("getLoginUser_Dept_SHIYB_NAME", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLSHIYB_NAME)); // 登录人员所属机构之所属事业部

		parser.addFunction("getLoginUser_Dept_SHIYBFB", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLSHIYBFB)); // 登录人员所属机构之所属事业部分部
		parser.addFunction("getLoginUser_Dept_SHIYBFB_NAME", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_DEPT_BLSHIYBFB_NAME)); // 登录人员所属机构之所属事业部分部

		parser.addFunction("getLoginUser_PostId", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_POSTID)); // 登录人员所属岗位ID
		parser.addFunction("getLoginUser_PostCode", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_POSTCODE)); // 登录人员所属岗位Code
		parser.addFunction("getLoginUser_PostName", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_POSTNAME)); // 登录人员所属岗位Name

		parser.addFunction("getLoginUserCorpId", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_CORPID)); //所属部门之所属机构【李春娟/2012-06-06】
		parser.addFunction("getLoginUserCorpName", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_CORPNAME)); //所属部门之所属机构名称 Gwang 2016-08-30
		
		parser.addFunction("getLoginUserBelongDept", new GetLoginUserBelongDept()); // 登录人员所属部门为总行还是分行
		parser.addFunction("getLoginUser_HeadFhDept", new GetLoginUser_HeadFhDept()); // 得到分行名称
		parser.addFunction("getLoginUser_HeadZhDept", new GetLoginUser_HeadZhDept()); // 得到支行名称或分行部门名称
		parser.addFunction("getLoginUser_BranchDept", new GetLoginUser_BranchDept()); // 得到支行名称前两位 总行返回空字符

		parser.addFunction("getLoginUser_Dept", new GetLoginUser_Dept()); // 
		///////////////////////////////

		parser.addFunction("getLoginUserDeptLinkCode", new GetLoginUserDeptLinkCode()); // 登录人员所属部门层级编码!!!至关重要!!
		parser.addFunction("getLoginUserPostCode", new GetLoginUserPostCode()); // 登录人员所属岗位编码
		//parser.addFunction("getLoginUserInfo", new GetLoginUserInfo(getJepType(), GetLoginUserInfo.LOGINUSER_NAME)); // 加入getLoginUserInfo()函

		parser.addFunction("getSequence", new GetSequence()); // 得到序列函数
		parser.addFunction("getFillZeroNum", new GetFillZeroNum()); // 得到补零后的值
		parser.addFunction("getUserRole", new GetUserRole()); // 得到补零后的值
		parser.addFunction("getDateDifference", new GetDateDifference()); //得到两个时间差

		parser.addFunction("getCurrDBDate", new GetCurrentDBDate(getJepType())); // 加入getCurrDate()函数
		parser.addFunction("getCurrDBTime", new GetCurrentDBTime(getJepType())); // 加入getCurrDate()函数
		parser.addFunction("getCurrDate", new GetCurrentTime(GetCurrentTime.DATE)); // 加入getCurrDate()函数
		parser.addFunction("getCurrTime", new GetCurrentTime(GetCurrentTime.TIME)); // 加入getCurrTime()函数
		parser.addFunction("getYearByDateTime", new GetTimePart(GetTimePart.YEAY)); //根据时间取得年度
		parser.addFunction("getSeasonByDateTime", new GetTimePart(GetTimePart.SEASON)); //根据时间取得季度
		parser.addFunction("getMonthByDateTime", new GetTimePart(GetTimePart.MONTH)); //根据时间取得月度
		parser.addFunction("getDateByDateTime", new GetTimePart(GetTimePart.DATE)); //根据时间取得日期
		parser.addFunction("afterDate", new AfterDate()); // 加入getCurrDate()函数
		parser.addFunction("toString", new ToString()); // 加入toString函数
		parser.addFunction("toNumber", new ToNumber()); // 加入toNumber函数
		parser.addFunction("getMapStrItemValue", new GetMapStrItemValue()); //从哈希表类型的字符串中取得对应key的value
		parser.addFunction("indexOf", new IndexOf()); // 检索给定字符串中给定的字符或字符串
		parser.addFunction("subString", new SubString());// 截取字符串
		parser.addFunction("fillBlank", new FillBlank());//填充空格
		parser.addFunction("getCaseValue", new GetCaseValue());//为了省去if/else的麻烦,一条搞定!!
		parser.addFunction("length", new Length());// 截取字符串
		parser.addFunction("replaceall", new ReplaceAll());// 替换字符串中的内容
		parser.addFunction("replaceallandsubString", new ReplaceAllAndSubString());// 替换字符串中的内容并截取字符串
		parser.addFunction("getRandom", new GetRandom()); //取得随机数 日期+随机十位数
		parser.addFunction("getParAsMap", new GetParAsMap()); //取得随机数 日期+随机十位数

		parser.addFunction("throwException", new ThrowException()); // 事件绑定函数
		parser.addFunction("systemOutPrintln", new SystemOutPrintln()); //打印一段话  
		parser.addFunction("getDecimalFormatNumber", new GetDecimalFormatNumber()); //设置小数的格式
	}

	/**
	 * 真正的执行公式!!
	 * 
	 * @param _expr,真正的可执行的语法!!
	 * @return
	 */
	public final Object execFormula(String _expr) {
		try {
			parser.parseExpression(_expr); // 执行公式
			return parser.getValueAsObject(); //
		} catch (Throwable ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * 按照函数名，函数说明，举例的方式加入Vector
	 * 
	 * @return
	 */
	public static Vector getFunctionDetail() {
		Vector vec_functions = new Vector(); //
		vec_functions.add(new String[] { "getColValue(\"tablename\",\"fieldname\",\"con_field\",\"con_value\")", "根据tablename,con_field,con_value，查询相应的数据，返回fielname的value", "getColValue(\"pub_menu\",\"LOCALNAME\",\"ID\",\"22\") = \"客户查询\"" });
		vec_functions.add(new String[] { "getColValue2(\"tablename\",\"fieldname\",\"con_field\",\"itemKey\")",
				"根据参数拼成select {fieldname} from {tablename} where {con_field}=getItemValue({itemKey}),它与getColValue的区别在于最后的参数是itemkey的名称而不是值,\"getColValue2(\"pub_user\",\"name\",\"id\",\"createuser\") = \"张三\"" });
		vec_functions.add(new String[] { "getSQLValue(\"数组\",\"select * from pub_user\",\"null\")", "根据SQL语句，查询相应的数据，返回字符串或者拼接字符串，第一个参数如果是数组则返回拼接字符串，如果是字符串则返回一个字符串,最后一个参数如果是null则执行第二个参数的SQL语句，如果不是，则需要把第二个参数拼成in的条件，第三个参数就是in条件里面的内容，注意不要加()",
				"getSQLValue(\"字符串\",\"select * from pub_user\",\"null\") = \"客户查询\"" });

		vec_functions
				.add(new String[] { "GetColValueByDS(\"datasourcename\",\"tablename\",\"fieldname\",\"con_field\",\"con_value\")", "从指定数据源中,根据tablename,con_field,con_value，查询相应的数据，返回fielname的value", "GetColValueByDS(\"datasource_default\",\"pub_menu\",\"LOCALNAME\",\"ID\",\"22\") = \"客户查询\"" });

		vec_functions.add(new String[] { "getFnValue(\"fnname\",\"pa_1\",……)", "获得存储函数的执行结果，函数的参数个数不确定，返回结果为String", "" });

		vec_functions.add(new String[] { "if(1==1,\"aaa\",\"bbb\")", "根据条件返回", "if(1==1,\"aaa\",\"bbb\"),则返回\"aaa\"" });
		vec_functions.add(new String[] { "DecideMessageBox(\"列表\",\"templetcode\",\"请选择一个人员\")", "根据模板名判断是否需要弹出警告框,一般用于新增等", "DecideMessageBox(\"列表\",\"pub_user\",\"请选择一个人员\")" });
		vec_functions.add(new String[] { "showMsg(\"提示语\")", "弹出一句话", "showMsg(\"aaa\")" });
		vec_functions.add(new String[] { "isSelected(getList(\"pub_corp_dept\"))", "判断当前面板是否被选中，返回Ture或False", "" });
		vec_functions.add(new String[] { "throwException(\"请选择一个人员\")", "抛出异常", "" });
		vec_functions.add(new String[] { "systemOutPrintln(\"打印一段话\")", "打印一段话", "" });

		vec_functions.add(new String[] { "getClassValue(\"cn.com.infostrategy.to.mdata.jepfunctions.IClassJepFormulaParseIFC\",\"aa\",\"bb\")", "通过反射类生成", "cn.com.infostrategy.to.mdata.jepfunctions.IClassJepFormulaParseIFC\",\"aa\",\"bb\")" });
		vec_functions.add(new String[] { "getClientEnvironmentPut(\"string\")", "得到客户端PUT的值,返回为字符串格式", "\")" });

		vec_functions.add(new String[] { "getLoginUserId()", "返回当前登录用户ID", "getLoginUserId()= \"1\"" });
		vec_functions.add(new String[] { "getLoginUserCode()", "返回当前登录用户CODE", "getLoginUserCode()= \"ADMIN\"" });
		vec_functions.add(new String[] { "getLoginUserName()", "返回当前登录用户", "getLoginUserName()= \"ADMIN\"" });

		vec_functions.add(new String[] { "getLoginUserDeptId()", "登录用户所属机构ID", "getLoginUser_DeptId()= \"126\"" }); //
		vec_functions.add(new String[] { "getLoginUserDeptName()", "登录用户所属机构名称", "getLoginUser_DeptName()= \"南京分行\"" }); //
		vec_functions.add(new String[] { "getLoginUserDeptType()", "登录用户所属机构类型", "getLoginUser_DeptType()= \"分行\"" }); //

		vec_functions.add(new String[] { "getLoginUserCorpId()", "登录用户所属部门之所属机构ID", "getLoginUserCorpId()= \"126\"" }); //
		vec_functions.add(new String[] { "getLoginUserCorpName()", "登录用户所属部门之所属机构Name", "getLoginUserCorpName()= \"集团公司\"" }); //
		vec_functions.add(new String[] { "getLoginUserDeptQSql()", "登录用户所属部门路径拼成likeor的sql", "getLoginUserDeptQSql(\"receivercorps\",\"likeor\")" }); //
		vec_functions.add(new String[] { "getLoginUserRoleQSql()", "登录用户所属角色拼成likeor的sql", "getLoginUserRoleQSql(\"receiverroles\",\"likeor\")" }); //

		vec_functions.add(new String[] { "getCorpIdByType(\"总行\")", "根据机构类型取得机构id", "getCorpIdByType(\"总行\")= \"12356\"" }); //
		vec_functions.add(new String[] { "getLoginUserParentCorpItemValueByType(\"一级分行/总行部门\",\"总行\",\"name\")", "取得登录人员所有上级机构中类型为指定类型的机构,以/区分表示先找到谁就是谁,第二个参数表示跨全行找,第三个是返回列", "getLoginUserParentCorpIdByType(\"一级分行/总行部门\",\"总行\",\"name\")= \"上海分行\"" }); //

		vec_functions.add(new String[] { "getBusitypeCode(\"getLoginUser_HeadFhDept()\",\"getYearByDateTime(getCurrDate())\",\"合审\")", "通过3个参数得到编码", "getBusitypeCode(\"getLoginUser_HeadFhDept()\",\"getYearByDateTime(getCurrDate())\",\"合审\")" });

		//		vec_functions.add(new String[] { "getLoginUser_Dept_BLZHBM()", "登录用户所属机构之所属总行部门ID", "getLoginUser_Dept_BLZHBM()= \"126\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_Dept_BLZHBM_NAME()", "登录用户所属机构之所属总行部门名称", "getLoginUser_Dept_BLZHBM_NAME()= \"总行法律合规部\"" }); //
		//
		//		vec_functions.add(new String[] { "getLoginUser_Dept_FENGH()", "登录用户所属机构之所属分行ID", "getLoginUser_Dept_FENGH()= \"3463\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_Dept_FENGH_NAME()", "登录用户所属机构之所属分行名称", "getLoginUser_Dept_FENGH_NAME()= \"上海分行\"" }); //
		//
		//		vec_functions.add(new String[] { "getLoginUser_Dept_FENGHBM()", "登录用户所属机构之所属分行部门ID", "getLoginUser_Dept_FENGHBM()= \"45622\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_Dept_FENGHBM_NAME()", "登录用户所属机构之所属分行部门名称", "getLoginUserDeptId()= \"上海分行计划财务部\"" }); //
		//
		//		vec_functions.add(new String[] { "getLoginUser_Dept_ZHIH()", "登录用户所属机构之所属支行ID", "getLoginUser_Dept_ZHIH()= \"32786\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_Dept_ZHIH_NAME()", "登录用户所属机构之所属支行名称", "getLoginUser_Dept_ZHIH_NAME()= \"上海分行徐汇区支行\"" }); //
		//
		//		vec_functions.add(new String[] { "getLoginUser_Dept_SHIYB()", "登录用户所属机构之所属事业部ID", "getLoginUser_Dept_SHIYB()= \"345\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_Dept_SHIYB_NAME()", "登录用户所属机构之所属事业部名称", "getLoginUser_Dept_SHIYB_NAME()= \"工商金融事业部\"" }); //
		//
		//		vec_functions.add(new String[] { "getLoginUser_Dept_SHIYBFB()", "登录用户所属机构之所属事业部分部ID", "getLoginUser_Dept_SHIYBFB()= \"126\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_Dept_SHIYBFB_NAME()", "登录用户所属机构之所属事业部分部名称", "getLoginUser_Dept_SHIYBFB_NAME()= \"工商金融事业部上海分部\"" }); //
		//
		//		vec_functions.add(new String[] { "getLoginUser_PostId()", "返回当前登录用户所属部门主键", "getLoginUserDeptId()= \"126\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_PostCode()", "返回当前登录用户所属部门主键", "getLoginUserDeptId()= \"126\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_PostName()", "返回当前登录用户所属部门主键", "getLoginUserDeptId()= \"126\"" }); //
		//
		//		vec_functions.add(new String[] { "getLoginUserDeptId()", "返回当前登录用户所属部门主键", "getLoginUserDeptId()= \"126\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_Dept()", "返回当前登录用户所属机构行", "getLoginUser_Dept()= \"成都分行\"" }); //
		//
		//		vec_functions.add(new String[] { "getLoginUserBelongDept()", "返回当前登录用户所属部门为总行还是分行", "getLoginUserBelongDept()= \"总行\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_HeadFhDept()", "返回当前登录用户的所属头分行或总行的名字", "getLoginUser_HeadFhDept()= \"总行\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_HeadZhDept()", "返回当前登录用户的所属支行的名字", "getLoginUser_HeadZhDept()= \"XXXX支行\"" }); //
		//		vec_functions.add(new String[] { "getLoginUser_BranchDept()", "返回当前登录用户所属机构行前两位", "getLoginUser_BranchDept()= \"成都\"" }); //
		//		vec_functions.add(new String[] { "getLoginUserDeptLinkCode()", "返回当前登录用户所属部门编码", "getLoginUserDeptLinkCode()= \"00010003\"" });
		//		vec_functions.add(new String[] { "getLoginUserPostCode()", "返回当前登录用户岗位编码", "getLoginUserPostCode() = \"BPS/B/LG/M010\"" });
		//		vec_functions.add(new String[] { "getUserRole()", "根据登陆人取得相当的角色", "getLoginUserInfo(\"userdef01\")= \"aa\"" });

//		vec_functions.add(new String[] { "getLoginUserInfo(\"userdef01\")", "返回当前登录用户某一列的值", "getLoginUserInfo(\"userdef01\")= \"aa\"" });
		vec_functions.add(new String[] { "getTreePathColValue(\"表名\",\"返回字段名\",\"树形结构勾连的id字段\",\"树形结构勾连的parentid字段\",\"过滤字段(比如id)\",\"过滤条件(比如12345)\",\"返回路径链名=Y/是否截掉第一层=N\");", "从一个树型结构中根据id的值返回其父亲路径中所有结点的name值拼在一起!",
				"getTreePathColValue(\"pub_corp_dept\",\"name\",\"id\",\"parentid\",\"id\",\"12345\",\"返回路径链名=Y/是否截掉第一层=N\");" });
		vec_functions.add(new String[] { "getTreePathColValue(\"表名\",\"返回字段名\",\"树形结构勾连的id字段\",\"树形结构勾连的parentid字段\",\"过滤字段(比如id)\",\"过滤条件(比如12345)\",\"返回路径链名=Y/是否截掉第一层=N\",\"截掉的字段名称=corptype/截掉的字段值=条件1,条件2\");", "从一个树型结构中根据id的值返回其父亲路径中所有结点的name值拼在一起!",
		"getTreePathColValue(\"pub_corp_dept\",\"name\",\"id\",\"parentid\",\"id\",getItemValue(\"deptid\"),\"返回路径链名=Y/是否截掉第一层=Y\",\"截掉的字段名称=corptype/截掉的字段值=总行部门处室,一级分行部门处室\");" });
		vec_functions.add(new String[] { "getDateDifference(\"firsttime\",\"secondtime\")", "返回两个时间的差,返回数字", "getDateDifference(\"2007-01-05\",\"2007-01-06\")= 1" });

		vec_functions.add(new String[] { "getCurrDate()", "返回系统当前日期", "getCurrDate()= \"2007-01-05\"" });
		vec_functions.add(new String[] { "getCurrTime()", "返回系统当前时间", "getCurrTime()= \"2007-01-05 10:05:33\"" });

		vec_functions.add(new String[] { "getYearByDateTime()", "返回某个时间的年度", "getYearByDateTime(\"2008-10-05\")= \"2008\"" });
		vec_functions.add(new String[] { "getSeasonByDateTime()", "返回某个时间的季度", "getSeasonByDateTime(\"2008-10-05\")= \"2008年4季度\"" });
		vec_functions.add(new String[] { "getMonthByDateTime()", "返回某个时间的月份", "getMonthByDateTime(\"2008-10-05\")= \"2008年12月\"" });
		vec_functions.add(new String[] { "getDateByDateTime()", "返回某个时间的日期", "getDateByDateTime(\"2008-10-05\")= \"2008-10-05\"" });

		vec_functions.add(new String[] { "getCurrDBDate()", "返回服务器端当前日期", "getCurrDBDate()= \"2007-01-05\"" });
		vec_functions.add(new String[] { "getCurrDBTime()", "返回服务器端当前时间", "getCurrDBTime()= \"2007-01-05 10:05:33\"" });
		vec_functions.add(new String[] { "getSequence(\"sequencename\",\"digit\")", "返回当前序列的值", "getSequence(\"sequencename\",\"3\")= \"001\"" });
		vec_functions.add(new String[] { "getFillZeroNum(\"number\",\"digit\")", "返回补零后的值", "getFillZeroNum(\"number\",\"3\")= \"001\"" });

		vec_functions.add(new String[] { "afterDate(\"oldDate\",5)", "返回指定时间后几天", "afterDate(\"2007-05-01\",5)= \"2007-05-05\"" });
		vec_functions.add(new String[] { "toString(Object)", "把传入的参数object转化为String", "toString(123) = \"123\"" });
		vec_functions.add(new String[] { "getMapStrItemValue(\"par1=10;par2=21;par3=aa;\",\"par1\")", "从一个哈希表类型的字符串中取得对应key的值", "getMapStrItemValue(getItemValue(\"mylastsubmittime\"),getLoginUserId()) = \"123\"" });

		vec_functions.add(new String[] { "getCaseValue(\"RUN\",\"RUN,审批中,END,流程已结束,null,流程未启动\")", "省去以前的if/else配置的麻烦,一条搞定", "getCaseValue(\"RUN\",\"RUN,审批中,END,流程已结束,null,流程未启动\")" });
		vec_functions.add(new String[] { "toNumber(\"par\")", "把传入的参数object转化为Number", "toNumber(\"70\") = 123" });
		vec_functions.add(new String[] { "indexOf(\"string\",\"indexvalue\")", "检索string中indexvalue的位置", "indexOf(\"abcdf\",\"c\") = 2" });
		vec_functions.add(new String[] { "length(\"abcde\")", "统计一个字符串的宽度", "length(\"abcde\")=5" });

		vec_functions.add(new String[] { "subString(\"string\",beginindex,endindex)", "截取string的beginindex-endindex之间的字符串", "subString(\"abcdefg\",1,5) = \"bcde\"" });
		vec_functions.add(new String[] { "fillBlank(\"反洗钱法\")", "将一个字符串分割后在中间填充一个空格", "fillBlank(\"反洗钱法\")=反 洗 钱 法 " });
		vec_functions.add(new String[] { "replaceall(\"string\",\"regexstring\",\"replacestring\")", "将string中所有的regexstring替换为replacestring", "replaceall(\"abcd abdg\",\"ab\",\"11\") = \"11cd 11dg\"" });
		vec_functions.add(new String[] { "replaceallandsubString(\"string\",\";\",\",\")", "将string中所有的regexstring替换为replacestring，并且切去该字符串的最后一位", "replaceall(\"aba,bcd,\",\";\",\",\") = \"aba;bcd\"" });

		vec_functions.add(new String[] { "getRandom()", "取得随机数 日期+随机十位数", "getRandom()= \"2008-1234567890\"" });
		vec_functions.add(new String[] { "getParAsMap(\"是否显示根结点\",\"N\",\"是否只允许选择叶子结点\",\"Y\")", "将参数与奇偶位为key与value,返回一个哈希表", "getParAsMap(\"是否显示根结点\",\"Y\",\"是否只允许选择叶子结点\",\"N\")=一个HashMap对象!" });

		// 所有控件操作函数!!!非常有用的很!!
		vec_functions.add(new String[] { "getItemValue(\"itemKey\")", "取得页面上某一项的值", "getItemValue(\"code\") = \"A001\"" });
		vec_functions.add(new String[] { "setItemValue(\"itemKey\",\"newvalue\")", "设置页面上某一项的值", "setItemValue(\"code\",\"A001\")" });
		vec_functions.add(new String[] { "setItemLabel(\"itemKey\",\"newvalue\")", "设置页面上某一项的列名", "setItemLabel(\"code\",\"A001\")" });

		// 设置参照

		vec_functions.add(new String[] { "getRefName(\"itemKey\")", "得到某个参照的Name值", "getRefName(\"userid\")" });
		vec_functions.add(new String[] { "getRefCode(\"itemKey\")", "得到某个参照的Code值", "getRefCode(\"userid\")" });
		vec_functions.add(new String[] { "getTreeItemLevelValue(\"itemkey\",\"level\")", "得到自定义或树形参照第N层的值", "getTreeItemLevelValue(\"itemkey\",\"level\")=AAA" });

		vec_functions.add(new String[] { "getMultiRefName(\"pub_user\",\"name\",\"code\",getItemValue(\"usercode\"))", "得到多选参照的Name值", "getMultiRefName(\"pub_user\",\"name\",\"code\",getItemValue(\"usercode\"))" });

		vec_functions.add(new String[] { "setRefItemCode(\"itemKey\",\"refcode\")", "设置某个参照的Code值", "setItemValue(\"userid\",\"A001\")" });
		vec_functions.add(new String[] { "setRefItemName(\"itemKey\",\"refname\")", "设置某个参照的Name值", "setItemValue(\"userid\",\"张三\")" });
		vec_functions.add(new String[] { "setRefDefine(\"itemKey\",\"refdefine\")", "设置某个参照的定义", "setItemValue(\"userid\",\"getRegFormatRef(\"部门\",\"\")" });

		vec_functions.add(new String[] { "getStringItemVO(\"张三\")", "返回一个StringItemVO对象", "getStringItemVO(\"张三\")" });
		vec_functions.add(new String[] { "getComBoxItemVO(\"3\",\"003\",\"本科\")", "返回一个ComBoxItemVO对象", "getComBoxItemVO(\"3\",\"003\",\"本科\")" });
		vec_functions.add(new String[] { "getRefItemVO(\"3\",\"003\",\"本科\")", "返回一个RefItemVO对象", "getRefItemVO(\"3\",\"003\",\"本科\")" });
		//统计或查询经常会出现默认查询当前年度，故需要在查询默认条件中设置该RefItemVO，普通的RefItemVO没有HashVO，即不包括查询语句，故新增公式【李春娟/2016-03-20】
		vec_functions.add(new String[] { "getYearDateRefItemVO(\"2016\")", "返回一个日历RefItemVO对象", "getRefItemVO(\"2016年;\",\"年;\",\"2016年;\")" });

		vec_functions.add(new String[] { "resetAllItemValue()", "清空所有值", "resetAllItemValue()" });
		vec_functions.add(new String[] { "resetItemValue(\"itemKey\")", "清空页面上某一项的值", "resetItemValue(\"code\")" });
		vec_functions.add(new String[] { "resetCardGroupValue(\"grouptitle1,grouptitle2\")", "清空页面上一组或多组的值", "resetCardGroupValue(\"其他,基本信息\")" });
		vec_functions.add(new String[] { "setItemEditable(\"itemKey\",\"true\")", "设置页面上某一项是否可编辑", "setItemEditable(\"code\",\"true\")" });
		vec_functions.add(new String[] { "setItemForeGround(\"itemKey\",\"0000FF\")", "设置某一项的前景颜色", "setItemForeGround(\"name\",\"FFFF00\")" });
		vec_functions.add(new String[] { "setItemBackGround(\"itemKey\",\"0000FF\")", "设置某一项的背景颜色,只支持列表设置", "setItemBackGround(\"name\",\"FFFF00\")" });

		vec_functions.add(new String[] { "SetItemIsmustinput(\"itemKey\",\"true\")", "设置页面上某一项是否可必输项", "setItemEditable(\"code\",\"true\")" });
		vec_functions.add(new String[] { "setItemVisiable(\"itemKey\",\"true\")", "设置页面上某一项是否可显示", "setItemVisiable(\"code\",\"true\")" });
		vec_functions.add(new String[] { "setItemVisiable(\"itemKey\",\"true\")", "设置页面上某一项是否可显示", "setItemVisiable(\"code\",\"true\")" });
		vec_functions.add(new String[] { "setCardRowVisiable(\"itemKey\",\"true\")", "设置卡片页面上某一行的所有控件是否可显示", "setCardRowVisiable(\"code\",\"true\")" });
		vec_functions.add(new String[] { "setCardGroupVisiable(\"grouptitle\",\"true\")", "设置卡片页面上某一个组的所有控件是否可显示", "setCardGroupVisiable(\"基本信息\",\"true\")" });
		vec_functions.add(new String[] { "setCardGroupExpand(\"grouptitle\",\"true\")", "设置卡片页面上某一个组是否展开", "setCardGroupExpand(\"基本信息\",\"true\")" });
		vec_functions.add(new String[] { "getEditState()", "获得卡片的编辑状态", "getEditState()" });//【李春娟/2013-05-09】
		
		vec_functions.add(new String[] { "getFormatChildListItemValue(\"Pub_user_code1\",\"id\")", "取得页面上某个列表中的某一项的值,只有该页面是由Billformat构成才行!", "getFormatChildListItemValue(\"Pub_user_code1\") = \"id\"" });
		vec_functions.add(new String[] { "getFormatChildTreeItemValue(\"Pub_user_code1\",\"id\")", "取得页面上某个树中的某一项的值,只有该页面是由Billformat构成才行!", "getFormatChildTreeItemValue(\"Pub_user_code1\") = \"id\"" });
		vec_functions.add(new String[] { "execWLTAction(\"cn.com.pushworld.TestWLTAction\")", "执行某一个公式", "execWLTAction(\"cn.com.pushworld.TestWLTAction\")" });
		vec_functions.add(new String[] { "openBillListDialog(\"PUB_USER_CODE_1\",\"id in (select userid from pub_user_role where roleid ='${id}')\",\"500\",\"300\")", "直接打开一个列表查询框,过滤条件支持${}宏代码替换,即可以拿主表中选中行的某一列值替换",
				"openBillListDialog(\"PUB_USER_CODE_1\",\"id in (select userid from pub_user_role where roleid='${ID}')\",\"500\",\"300\")" });

		// 后来增加的事件公式!!!
		vec_functions.add(new String[] { "setBtnBarVisiable(getList(\"PUB_POST_CODE1\"),\"false\");", "设置某个模板的通用操作按钮是否显示", "" });
		vec_functions.add(new String[] { "setBillPanelBtnVisiable(getList(\"PUB_POST_CODE1\"),\"评估\",\"false\");", "设置按钮是否显示", "" });
		vec_functions.add(new String[] { "getSelectedBillVOItemValue(getList(\"PUB_POST_CODE1\"),\"name\")", "取得某个面板上某个数据", "" });
		vec_functions.add(new String[] { "addBillSelectListener(getList(\"PUB_POST_CODE1\"),\"\"queryAllData(getList(\"PUB_POST_CODE1\")\")\");", "监听事件,再次调用公式,即反射调用公式!", "" });
		vec_functions.add(new String[] { "getUserRole();", "通过登陆人ID得到他的角色", "" }); //
		vec_functions.add(new String[] { "queryAllData(getList(\"PUB_POST_CODE1\"));", "默认查询所有数据", "" }); //
		vec_functions.add(new String[] { "queryDataByCondition(getList(\"PUB_POST_CODE1\"),\"code='001'\");", "根据条件查询数据", "" });
		vec_functions.add(new String[] { "addSelectEventBindRefresh(getTree(\"pub_corp_dept_CODE1\"),getList(\"PUB_POST_CODE1\"),\"id\",\"code\");", "选择刷新事件,通过一个的模板刷新另一个模板,ID为主键，code为显示名字", "addSelectEventBindRefresh(getTree(\"pub_corp_dept_CODE1\"),getList(\"PUB_POST_CODE1\"),\"id\",\"code\"" });
		vec_functions.add(new String[] { "addSelectQuickQueryRefresh(getTree(\"pub_corp_dept_CODE1\"),getList(\"PUB_POST_CODE1\"),\"id\",\"name\",\"listid\");", "刷新快速查询事件，通过一个模板刷新另一个模板的快速查询面板",
				"addSelectQuickQueryRefresh(getTree(\"pub_corp_dept_CODE1\"),getList(\"PUB_POST_CODE1\"),\"id\",\"name\",\"listid\")" });
		vec_functions.add(new String[] { "setEveryBtnBarIsVisiable(getTree(\"PUB_POST_CODE1\"),getList(\"PUB_POST_CODE1\"),\"新增\",\"false\",\"编辑\",\"false\",\"删除\",\"false\",\"查看\",\"false\");", "一个模板刷新时，设置另一个模板的默认操作按钮是否有功能", "" });
		vec_functions.add(new String[] { "setSelfBtnBarIsVisiable(getTree(\"pub_corp_dept_CODE1\"),getList(\"PUB_POST_CODE1\"),\"自定义按钮\",\"false\");", "一个模板刷新时，设置另一个模板的自定义操作按钮是否有功能", "" });
		vec_functions.add(new String[] { "clearTable(getTree(\"PUB_POST_CODE1\"),getList(\"PUB_POST_CODE1\"));", "当某个第一个模板发生变化时清空另一个模板的值", "" });
		vec_functions.add(new String[] { "setListQQVisiable(getList(\"pub_corp_dept_CODE1\"),\"false\");", "设置列表的快速查询面板是否显示", "" });
		vec_functions.add(new String[] { "setSelfListQQIsVisiable(getList(\"pub_corp_dept_CODE1\"),\"自定义按钮\",\"false\");", "设置自定义按钮在快速查询后是否有功能", "" });
		vec_functions.add(new String[] { "setListQQIsVisiable(getList(\"pub_corp_dept_CODE1\"),\"新增\",\"false\",\"编辑\",\"false\",\"删除\",\"false\",\"查看\",\"false\");", "设置通用按钮在快速查询后是否有功能", "" });
		vec_functions.add(new String[] { "setListQQAfterIsVisiable(getList(\"pub_corp_dept_CODE1\"),getList(\"pub_corp_dept_CODE1\"),\"新增\",\"false\",\"编辑\",\"false\",\"删除\",\"false\",\"查看\",\"false\");", "在一个模板快速查询后设置另一个模板的通用按钮是否有功能", "" });
		vec_functions.add(new String[] { "setSelfListQQAfterIsVisiable(getList(\"pub_corp_dept_CODE1\"),getList(\"pub_corp_dept_CODE1\"),\"自定义按钮\",\"false\");", "在一个模板快速查询后设置另一个模板的自定义按钮是否有功能", "" });
		vec_functions.add(new String[] { "setQQAfterClearTable(getList(\"pub_corp_dept_CODE1\"),getList(\"pub_corp_dept_CODE1\"));", "在一个模板快速查询后清空另一个模板的值", "" });
		vec_functions.add(new String[] { "setDefaultRefItemVOReturnFrom(getList(\"pub_corp_dept_CODE1\"),\"id\",\"name\");", "设置默认参照返回", "" });

		//Excel面板公式
		vec_functions.add(new String[] { "setCellItemValue(\"2-D\",\"Y\");", "设置某个Cell的值", "" }); //
		vec_functions.add(new String[] { "getCellItemValue(\"2-D\");", "得得某一个Cell的值", "" }); //
		vec_functions.add(new String[] { "getCellItemNumberValue(\"2-D\");", "得得某一个Cell的值,并转换为数字", "" }); //
		vec_functions.add(new String[] { "getCellSumValueByRowCol(\"2\",\"3\",\"4\",\"5\");", "取得某一段的值", "" }); //

		vec_functions.add(new String[] { "getCellEditingItemNumberValue();", "取得当前编辑的格子的数字值", "" }); //
		vec_functions.add(new String[] { "getCellCurrRowItemValue(\"2\");", "取得当前行第2列的值", "" }); //
		vec_functions.add(new String[] { "getCellCurrRowItemNumberValue(\"2\");", "取得当前行第2列的值,并转换成数字", "" }); //
		vec_functions.add(new String[] { "getCellCurrColItemValue(\"2\");", "取得当前列第2行的值", "" }); //
		vec_functions.add(new String[] { "getCellCurrColItemNumberValue(\"2\");", "取得当前列第2行的值,并转换成数字", "" }); //

		//工作流引擎公式!!
		vec_functions.add(new String[] { "getWFInfo(\"当前流程编码;当前环节编码;当前处理人编码;最后提交人编码;历史处理人编码\");", "取得工作流相关信息", "" }); //
		vec_functions.add(new String[] { "setWFRouterMarkValue(\"xchapprove\",\"Y\");", "在工作流引擎中设置路由标记", "" }); //
		vec_functions.add(new String[] { "addWFRouterMarkValue(\"xchapprove\",\"Y\");", "在工作流引擎中增加路由标记", "" }); //
		vec_functions.add(new String[] { "getWFRouterMarkValue(\"xchapprove\");", "取得工作流引擎中设置路由标记", "" }); //
		vec_functions.add(new String[] { "getWFRouterMarkCount(\"xchapprove\");", "取得某个路由标记个数", "" }); //
		vec_functions.add(new String[] { "getWFBillItemValue(\"casetype\");", "取得工作流引擎中对应的单据中的值", "" }); //

		//新增公式
		vec_functions.add(new String[] { "DirectLinkRef(\"field2\");", "在table中新增关联记录，field2的值通过参照选择", "" }); //
		vec_functions.add(new String[] { "ShowNewCardDialog(\"templetcode\");", "根据模版编码显示浏览卡片", "" }); //
		vec_functions.add(new String[] { "getDecimalFormatNumber(\"\",\"\");", "将数字转换成一定的格式，第一个参数为格式，第二个是数字", "getDecimalFormatNumber(\"#,###.00\",\"12345.6\")=\"12,345.60\"" }); //

		return vec_functions;
	}

	public static JepFormulaParse createUIJepParse() {
		return createJepParseByType(WLTConstants.JEPTYPE_UI); //
	}

	public static JepFormulaParse createBSJepParse() {
		return createJepParseByType(WLTConstants.JEPTYPE_BS); //
	}

	//直接创建一个最基本的解析器! 即既不是JepFormulaParseAtUI也不是JepFormulaParseAtBS,但是UI/BS类型
	public static JepFormulaParse createJepParseByType(final byte _jepType) {
		JepFormulaParse jep = new JepFormulaParse() {
			@Override
			protected byte getJepType() {
				return _jepType;
			}
		};
		jep.initNormalFunction(); //加载一般公式!!
		return jep; //
	}

	public JEP getParser() {
		return parser;
	}
}
