package cn.com.infostrategy.to.sysapp.login;

import java.io.Serializable;
import java.util.Vector;

/**
 * 定义风格模板的类,扩展新的风格模板时只需要修改这个类的配置,然后再新增一个类即可,变得非常简便
 * @author xch
 *
 */
public class StyleTempletDefineBuilder implements Serializable {
	private static final long serialVersionUID = 1L;

	private Vector vc_vos = new Vector(); //存储所有定义类的集合

	/**
	 * 构造方法,即初始化所有模板定义...
	 * 新增一个模板时只要做两件事:1-在该方法中注册一个新模板定义;2-以注册的类名创建一个类,在类中可以通过getStyleFormulaValue("$TempletCode")取得参数值.
	 * 其中平台自动帮你做了几件事:
	 * 1.点击菜单时自动创建类的实例
	 * 2.自动将参数送到面板实例中去
	 * 3.在面板中通过公式可以自动取得参数值,包括字符串与字符数组。
	 */
	public StyleTempletDefineBuilder() {
		StyleTempletDefineVO stdo = null; //

		stdo = new StyleTempletDefineVO("风格模板1", "单列表", "cn.com.infostrategy.ui.mdata.styletemplet.t01.DefaultStyleWorkPanel_01"); //
		stdo.putFormula("$TempletCode", "PUB_USER_CODE1"); //
		vc_vos.add(stdo); //

		stdo = new StyleTempletDefineVO("风格模板2", "单列表/卡", "cn.com.infostrategy.ui.mdata.styletemplet.t02.DefaultStyleWorkPanel_02"); //
		stdo.putFormula("$TempletCode", "PUB_USER_CODE1"); //
		stdo.putFormula("UIIntercept", "cn.com.pushworld.Intercept"); //
		vc_vos.add(stdo); //

		stdo = new StyleTempletDefineVO("风格模板3", "单表树/卡", "cn.com.infostrategy.ui.mdata.styletemplet.t03.DefaultStyleWorkPanel_03"); //
		stdo.putFormula("$TempletCode", "pub_corp_dept_CODE1"); //
		vc_vos.add(stdo); //

		stdo = new StyleTempletDefineVO("风格模板4", "双表树列", "cn.com.infostrategy.ui.mdata.styletemplet.t04.DefaultStyleWorkPanel_04"); //
		stdo.putFormula("$TreeTempletCode", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$TreeJoinField", "id"); //
		stdo.putFormula("$TableTempletCode", "pub_user_post_CODE1"); //
		stdo.putFormula("$TableJoinField", "deptid"); //
		stdo.putFormulaBlankLine(); //
		stdo.putFormula("$CustAfterInitClass", "cn.com.pushworld.ui.AfterInitialize"); //
		vc_vos.add(stdo); //

		stdo = new StyleTempletDefineVO("风格模板5", "双表树列/卡", "cn.com.infostrategy.ui.mdata.styletemplet.t05.DefaultStyleWorkPanel_05"); //
		stdo.putFormula("$TreeTempletCode", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$TreeJoinField", "id"); //
		stdo.putFormula("$TableTempletCode", "pub_user_post_CODE1"); //
		stdo.putFormula("$TableJoinField", "deptid"); //
		stdo.putFormulaBlankLine(); //
		stdo.putFormula("$CustPanel", "cn.com.pushworld.ui.TestCustPanel"); //
		stdo.putFormula("$UIIntercept", "cn.com.pushworld.ui.TestUIIntercept"); //
		stdo.putFormula("$BSIntercept", "cn.com.pushworld.bs.TestBSIntercept"); //
		stdo.putFormula("$IsShowSysButton", "Y"); //
		vc_vos.add(stdo); //

		stdo = new StyleTempletDefineVO("风格模板6", "双表列列", "cn.com.infostrategy.ui.mdata.styletemplet.t06.DefaultStyleWorkPanel_06"); //
		stdo.putFormula("$ParentTempletCode", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$ParentJoinField", "id"); //
		stdo.putFormula("$ChildTempletCode", "pub_user_post_CODE1"); //
		stdo.putFormula("$ChildJoinField", "deptid"); //
		stdo.putFormulaBlankLine(); //
		stdo.putFormula("$CustPanel", "cn.com.pushworld.ui.TestCustPanel"); //
		stdo.putFormula("$UIIntercept", "cn.com.pushworld.ui.TestUIIntercept"); //
		stdo.putFormula("$BSIntercept", "cn.com.pushworld.bs.TestBSIntercept"); //
		stdo.putFormula("$IsShowSysButton", "Y"); //
		stdo.putFormula("$Layout", "上下"); //
		vc_vos.add(stdo); //

		stdo = new StyleTempletDefineVO("风格模板7", "双表列列/卡", "cn.com.infostrategy.ui.mdata.styletemplet.t07.DefaultStyleWorkPanel_07"); //
		stdo.putFormula("$ParentTempletCode", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$ParentJoinField", "id"); //
		stdo.putFormula("$ChildTempletCode", "pub_user_post_CODE1"); //
		stdo.putFormula("$ChildJoinField", "deptid"); //
		stdo.putFormulaBlankLine(); //
		stdo.putFormula("$CustPanel", "cn.com.pushworld.ui.TestCustPanel"); //
		stdo.putFormula("$UIIntercept", "cn.com.pushworld.ui.TestUIIntercept"); //
		stdo.putFormula("$BSIntercept", "cn.com.pushworld.bs.TestBSIntercept"); //
		stdo.putFormula("$IsShowSysButton", "Y"); //
		stdo.putFormula("$Layout", "上下"); //
		vc_vos.add(stdo); //

		stdo = new StyleTempletDefineVO("风格模板8", "主子表", "cn.com.infostrategy.ui.mdata.styletemplet.t08.DefaultStyleWorkPanel_08"); //
		stdo.putFormula("$ParentTempletCode", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$ParentJoinField", "id"); //
		stdo.putFormula("$ChildTempletCode", "pub_user_post_CODE1"); //
		stdo.putFormula("$ChildJoinField", "deptid"); //
		stdo.putFormulaBlankLine(); //
		stdo.putFormula("$CustPanel", "cn.com.pushworld.ui.TestCustPanel"); //
		stdo.putFormula("$UIIntercept", "cn.com.pushworld.ui.TestUIIntercept"); //
		stdo.putFormula("$BSIntercept", "cn.com.pushworld.bs.TestBSIntercept"); //
		stdo.putFormula("$IsShowSysButton", "Y"); //
		vc_vos.add(stdo); //

		stdo = new StyleTempletDefineVO("风格模板9", "多子表", "cn.com.infostrategy.ui.mdata.styletemplet.t09.DefaultStyleWorkPanel_09"); //
		stdo.putFormula("$ParentTempletCode", "PUB_USER_CODE1"); //
		stdo.putFormula("$ParentJoinField", "id"); //
		stdo.putFormula("$ChildTempletCode", new String[] { "pub_user_post_CODE1", "pub_user_role_CODE1", "pub_user_menu_CODE1" }); //
		stdo.putFormula("$ChildJoinField", new String[] { "deptid", "roleid", "menuid" }); //
		stdo.putFormulaBlankLine(); //
		stdo.putFormula("$CustPanel", "cn.com.pushworld.ui.TestCustPanel"); //
		stdo.putFormula("$UIIntercept", "cn.com.pushworld.ui.TestUIIntercept"); //
		stdo.putFormula("$BSIntercept", "cn.com.pushworld.bs.TestBSIntercept"); //
		stdo.putFormula("$IsShowSysButton", "Y"); //
		vc_vos.add(stdo); //		

		stdo = new StyleTempletDefineVO("风格模板10", "主子孙表", "cn.com.infostrategy.ui.mdata.styletemplet.t10.DefaultStyleWorkPanel_10"); //
		stdo.putFormula("$ParentTempletCode", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$ParentJoinField", "id"); //
		stdo.putFormula("$ChildTempletCode", "pub_user_post_CODE1"); //
		stdo.putFormula("$ChildJoinField", "deptid"); //
		stdo.putFormulaBlankLine(); //
		stdo.putFormula("$CustPanel", "cn.com.pushworld.ui.TestCustPanel"); //
		stdo.putFormula("$UIIntercept", "cn.com.pushworld.ui.TestUIIntercept"); //
		stdo.putFormula("$BSIntercept", "cn.com.pushworld.bs.TestBSIntercept"); //
		stdo.putFormula("$IsShowSysButton", "Y"); //
		vc_vos.add(stdo); //	

		//根据操作的对象个数,引申出的各种变种!!!
		stdo = new StyleTempletDefineVO("风格模板1A", "两个页签,第一个是卡片第二个是列表", "cn.com.infostrategy.ui.mdata.styletemplet.t1a.DefaultStyleWorkPanel_1A"); //
		stdo.putFormula("$TempletCode", "pub_corp_dept_CODE1"); //
		vc_vos.add(stdo); //	

		stdo = new StyleTempletDefineVO("风格模板2A", "通过一个对象的列出卡片弹出去维护另一个对象", "cn.com.infostrategy.ui.mdata.styletemplet.t2a.DefaultStyleWorkPanel_2A"); //
		stdo.putFormula("$TempletCode1", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$TempletCode2", "pub_corp_dept_CODE1"); //
		vc_vos.add(stdo); //	

		stdo = new StyleTempletDefineVO("风格模板2B", "通过一个对象弹出列表去维护另一个对象", "cn.com.infostrategy.ui.mdata.styletemplet.t2b.DefaultStyleWorkPanel_2B"); //
		stdo.putFormula("$TempletCode1", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$TempletCode2", "pub_corp_dept_CODE1"); //
		vc_vos.add(stdo); //	

		stdo = new StyleTempletDefineVO("风格模板3A", "三个列表,选择A刷新B,选择B刷新C", "cn.com.infostrategy.ui.mdata.styletemplet.t3a.DefaultStyleWorkPanel_3A"); //
		stdo.putFormula("$TempletCode1", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$TempletCode2", "pub_corp_dept_CODE1"); //
		stdo.putFormula("$TempletCode3", "pub_corp_dept_CODE1"); //
		vc_vos.add(stdo); //	

		stdo = new StyleTempletDefineVO("工作流风格1", "三页签方式", "cn.com.infostrategy.ui.mdata.styletemplet.wf1.DfWFStyle1WKPanel"); //
		stdo.putFormula("$TempletCode", "pub_corp_dept_CODE1"); //
		vc_vos.add(stdo); //	
		
		stdo = new StyleTempletDefineVO("工作流风格2", "三箱按钮在列表上", "cn.com.infostrategy.ui.mdata.styletemplet.wf2.DfWFStyle2WKPanel"); //
		stdo.putFormula("$TempletCode", "pub_corp_dept_CODE1"); //
		vc_vos.add(stdo); //	
		
	}

	public StyleTempletDefineVO[] getAllStyleTempletDefineVOs() {
		return (StyleTempletDefineVO[]) vc_vos.toArray(new StyleTempletDefineVO[0]);
	}

	/**
	 * 取得所有风格模板的名称..
	 * @return
	 */
	public String[] gettAllStyleTempletNames() {
		StyleTempletDefineVO[] stdos = getAllStyleTempletDefineVOs(); //
		String[] str_returns = new String[stdos.length]; //
		for (int i = 0; i < str_returns.length; i++) {
			str_returns[i] = stdos[i].getName(); //
		}
		return str_returns;
	}

	/**
	 * 根据名称取得某一种风格模板的实现类名.
	 * @param _name
	 * @return
	 */
	public String getDefaultClassName(String _name) {
		for (int i = 0; i < vc_vos.size(); i++) {
			StyleTempletDefineVO stvo = (StyleTempletDefineVO) vc_vos.get(i); //
			if (stvo.getName().equals(_name)) {
				return stvo.getDefaultClassName(); //
			}
		}
		return null;
	}
}
