package cn.com.infostrategy.ui.mdata.formatcomp;

import java.util.Vector;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.config.SetSelfListQQIsVisiable;

/**
 * 格式面板
 * 
 * @author xch
 *
 */
public class FormatEventBindFormulaParse extends JepFormulaParse {
	private BillFormatPanel formatpanel = null;
	private String str_regformula1, str_regformula2, str_regformula3 = null;

	//JEP parser = new JEP();

	public FormatEventBindFormulaParse(BillFormatPanel _panel) {
		this(_panel, null, null, null); //
	}

	public FormatEventBindFormulaParse(BillFormatPanel _panel, String _regformula1, String _regformula2, String _regformula3) {
		this.formatpanel = _panel; //
		this.str_regformula1 = _regformula1; //
		this.str_regformula2 = _regformula2; //
		this.str_regformula3 = _regformula3; //

		initNormalFunction(); //先注册父类的所有通用函数!!

		//注册所有函数
		parser.addFunction("getCard", new GetFormatItemPanel(this.formatpanel, GetFormatItemPanel.ITEMTYPE_CARD)); //
		parser.addFunction("getList", new GetFormatItemPanel(this.formatpanel, GetFormatItemPanel.ITEMTYPE_LIST)); //
		parser.addFunction("getTree", new GetFormatItemPanel(this.formatpanel, GetFormatItemPanel.ITEMTYPE_TREE)); //
		parser.addFunction("getCell", new GetFormatItemPanel(this.formatpanel, GetFormatItemPanel.ITEMTYPE_CELL)); //

		parser.addFunction("getSelectedBillVOItemValue", new GetSelectedBillVOItemValueParse(this.formatpanel)); //事件绑定函数
		parser.addFunction("addSelectEventBindRefresh", new AddSelectEventBindRefresh(this.formatpanel)); //事件绑定函数
		parser.addFunction("addSelectQuickQueryRefresh", new AddSelectQuickQueryRefresh(this.formatpanel)); //事件绑定函数

		parser.addFunction("queryAllData", new QueryAllDataParse(this.formatpanel)); //事件绑定函数
		parser.addFunction("getUserRole", new GetUserRole()); //事件绑定函数

		parser.addFunction("queryDataByCondition", new QueryDataByConditionParse(this.formatpanel)); //事件绑定函数
		parser.addFunction("setEveryBtnBarIsVisiable", new SetEveryBtnBarIsVisiable(this.formatpanel)); //事件绑定函数
		parser.addFunction("setSelfBtnBarIsVisiable", new SetSelfBtnBarIsVisiable(this.formatpanel)); //事件绑定函数
		parser.addFunction("setBtnBarVisiable", new SetBtnBarVisiableParse(this.formatpanel)); //设置按钮是否全部显示/隐藏
		parser.addFunction("setBillPanelBtnVisiable", new SetBillPanelBtnVisiableParse(this.formatpanel)); //调置某个按钮是否显示

		parser.addFunction("setListQQVisiable", new SetListQQVisiableParse(this.formatpanel)); //事件绑定函数
		parser.addFunction("clearTable", new ClearTable(this.formatpanel)); //事件绑定函数
		parser.addFunction("setSelfListQQIsVisiable", new SetSelfListQQIsVisiable(this.formatpanel)); //事件绑定函数
		parser.addFunction("setListQQIsVisiable", new SetListQQIsVisiableParse(this.formatpanel)); //事件绑定函数
		parser.addFunction("setDefaultRefItemVOReturnFrom", new SetDefaultRefItemVOReturnFromParse(this.formatpanel)); //事件绑定函数
		parser.addFunction("setListQQAfterIsVisiable", new SetListQQAfterIsVisiable(this.formatpanel)); //事件绑定函数
		parser.addFunction("setSelfListQQAfterIsVisiable", new SetSelfListQQAfterIsVisiable(this.formatpanel)); //事件绑定函数
		parser.addFunction("setQQAfterClearTable", new SetQQAfterClearTable(this.formatpanel)); //事件绑定函数

		//注册事件绑定函数
		parser.addFunction("addBillSelectListener", new AddBillSelectListenerParse(this.formatpanel)); //列表或树,选择变化时触发的事件,最有用的公式!!!

		parser.addFunction("getRegFormula", new GetRegFormulaParse(this.formatpanel, str_regformula1, str_regformula2, str_regformula3)); //列表或树,选择变化时触发的事件,最有用的公式!!!
	}

	/**
	 * 按照函数名，函数说明，举例的方式加入Vector
	 * 
	 * @return
	 */

	public static Vector getFunctionDetail() {
		Vector vec_functions = new Vector(); //
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
		vec_functions.add(new String[] { "clearTable(getList(\"PUB_POST_CODE1\"));", "清空某一个面板中的数据,包括列表,树,卡片", "" });
		vec_functions.add(new String[] { "setListQQVisiable(getList(\"pub_corp_dept_CODE1\"),\"false\");", "设置列表的快速查询面板是否显示", "" });
		vec_functions.add(new String[] { "setSelfListQQIsVisiable(getList(\"pub_corp_dept_CODE1\"),\"自定义按钮\",\"false\");", "设置自定义按钮在快速查询后是否有功能", "" });
		vec_functions.add(new String[] { "setListQQIsVisiable(getList(\"pub_corp_dept_CODE1\"),\"新增\",\"false\",\"编辑\",\"false\",\"删除\",\"false\",\"查看\",\"false\");", "设置通用按钮在快速查询后是否有功能", "" });
		vec_functions.add(new String[] { "setListQQAfterIsVisiable(getList(\"pub_corp_dept_CODE1\"),getList(\"pub_corp_dept_CODE1\"),\"新增\",\"false\",\"编辑\",\"false\",\"删除\",\"false\",\"查看\",\"false\");", "在一个模板快速查询后设置另一个模板的通用按钮是否有功能", "" });
		vec_functions.add(new String[] { "setSelfListQQAfterIsVisiable(getList(\"pub_corp_dept_CODE1\"),getList(\"pub_corp_dept_CODE1\"),\"自定义按钮\",\"false\");", "在一个模板快速查询后设置另一个模板的自定义按钮是否有功能", "" });
		vec_functions.add(new String[] { "setQQAfterClearTable(getList(\"pub_corp_dept_CODE1\"),getList(\"pub_corp_dept_CODE1\"));", "在一个模板快速查询后清空另一个模板的值", "" });
		vec_functions.add(new String[] { "setDefaultRefItemVOReturnFrom(getList(\"pub_corp_dept_CODE1\"),\"id\",\"name\");", "设置默认参照返回", "" });

		vec_functions.add(new String[] { "getRegFormula(\"1\")", "取得注册公式第几个", "" });

		return vec_functions;
	}

	@Override
	protected byte getJepType() {
		return WLTConstants.JEPTYPE_UI; //
	}

}
