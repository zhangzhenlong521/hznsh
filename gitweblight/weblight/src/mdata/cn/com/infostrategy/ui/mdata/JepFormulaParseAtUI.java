/**************************************************************************
 * $RCSfile: JepFormulaParseAtUI.java,v $  $Revision: 1.9 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.jepfunctions.AddItemValueChangedListener;
import cn.com.infostrategy.to.mdata.jepfunctions.DirectLinkRef;
import cn.com.infostrategy.to.mdata.jepfunctions.ExecWLTAction;
import cn.com.infostrategy.to.mdata.jepfunctions.GetCellCurrRowColItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.GetCellEditingItemNumberValue;
import cn.com.infostrategy.to.mdata.jepfunctions.GetCellItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.GetCellSumValueByKeys;
import cn.com.infostrategy.to.mdata.jepfunctions.GetCellSumValueByRowCol;
import cn.com.infostrategy.to.mdata.jepfunctions.GetClientEnvironmentPut;
import cn.com.infostrategy.to.mdata.jepfunctions.GetComBoxItemVO;
import cn.com.infostrategy.to.mdata.jepfunctions.GetEditState;
import cn.com.infostrategy.to.mdata.jepfunctions.GetFormatChildListItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.GetFormatChildTreeItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.GetItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.GetMultiRefName;
import cn.com.infostrategy.to.mdata.jepfunctions.GetNotNullItemCount;
import cn.com.infostrategy.to.mdata.jepfunctions.GetRefCode;
import cn.com.infostrategy.to.mdata.jepfunctions.GetRefItemVO;
import cn.com.infostrategy.to.mdata.jepfunctions.GetRefName;
import cn.com.infostrategy.to.mdata.jepfunctions.GetStringItemVO;
import cn.com.infostrategy.to.mdata.jepfunctions.GetTreeItemLevelValue;
import cn.com.infostrategy.to.mdata.jepfunctions.GetWFInfo;
import cn.com.infostrategy.to.mdata.jepfunctions.GetYearDateRefItemVO;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.to.mdata.jepfunctions.ResetAllItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.ResetCardGroupValue;
import cn.com.infostrategy.to.mdata.jepfunctions.ResetItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.SetCardGroupExpand;
import cn.com.infostrategy.to.mdata.jepfunctions.SetCardGroupVisiable;
import cn.com.infostrategy.to.mdata.jepfunctions.SetCardRowVisiable;
import cn.com.infostrategy.to.mdata.jepfunctions.SetCellItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.SetClientEnvironmentPut;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemBackGround;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemEditable;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemForeGround;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemIsmustinput;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemLabel;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemValue;
import cn.com.infostrategy.to.mdata.jepfunctions.SetItemVisiable;
import cn.com.infostrategy.to.mdata.jepfunctions.SetRefDefine;
import cn.com.infostrategy.to.mdata.jepfunctions.SetRefItemCode;
import cn.com.infostrategy.to.mdata.jepfunctions.SetRefItemName;
import cn.com.infostrategy.to.mdata.jepfunctions.ShowMsg;
import cn.com.infostrategy.to.mdata.jepfunctions.ShowNewCardDialog;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.formatcomp.AddBillSelectListenerParse;
import cn.com.infostrategy.ui.mdata.formatcomp.AddSelectEventBindRefresh;
import cn.com.infostrategy.ui.mdata.formatcomp.AddSelectQuickQueryRefresh;
import cn.com.infostrategy.ui.mdata.formatcomp.ClearTable;
import cn.com.infostrategy.ui.mdata.formatcomp.GetFormatItemPanel;
import cn.com.infostrategy.ui.mdata.formatcomp.GetRegFormulaParse;
import cn.com.infostrategy.ui.mdata.formatcomp.GetSelectedBillVOItemValueParse;
import cn.com.infostrategy.ui.mdata.formatcomp.GetUserRole;
import cn.com.infostrategy.ui.mdata.formatcomp.QueryAllDataParse;
import cn.com.infostrategy.ui.mdata.formatcomp.QueryDataByConditionParse;
import cn.com.infostrategy.ui.mdata.formatcomp.SetBillPanelBtnVisiableParse;
import cn.com.infostrategy.ui.mdata.formatcomp.SetBtnBarVisiableParse;
import cn.com.infostrategy.ui.mdata.formatcomp.SetDefaultRefItemVOReturnFromParse;
import cn.com.infostrategy.ui.mdata.formatcomp.SetEveryBtnBarIsVisiable;
import cn.com.infostrategy.ui.mdata.formatcomp.SetListQQAfterIsVisiable;
import cn.com.infostrategy.ui.mdata.formatcomp.SetListQQIsVisiableParse;
import cn.com.infostrategy.ui.mdata.formatcomp.SetListQQVisiableParse;
import cn.com.infostrategy.ui.mdata.formatcomp.SetQQAfterClearTable;
import cn.com.infostrategy.ui.mdata.formatcomp.SetSelfBtnBarIsVisiable;
import cn.com.infostrategy.ui.mdata.formatcomp.SetSelfListQQAfterIsVisiable;
import cn.com.infostrategy.ui.mdata.jepfunctions.OpenBillListDialog;
import cn.com.infostrategy.ui.mdata.styletemplet.config.SetSelfListQQIsVisiable;
import cn.com.infostrategy.ui.report.BillCellPanel;

/**
 * UI端的公式解析器
 * @author xch
 *
 */
public class JepFormulaParseAtUI extends JepFormulaParse {

	protected byte getJepType() {
		return WLTConstants.JEPTYPE_UI;
	}

	public JepFormulaParseAtUI() {
		initNormalFunction(); //调用父类常用的计算函数!!
	}

	public JepFormulaParseAtUI(boolean _isStandard) {
		if (_isStandard) {
			initStandardFunction(); //只加载标准化函数!即JEP自带的最基本的函数!
		} else {
			initNormalFunction(); //调用父类常用的计算函数!!
		}
	}

	/**
	 * 可以操作控件的函数,即除了常用函数外，还加上控件操作函数!!!很有用，也是亮点所在!!!
	 * 控件连动是UI中最难的地方，也是工作量最大的地方!!!解决这个问题也就大大提高了开发效率，目前国内都没有很好的这方面的设计!!也许WebLight在这方面将是做得最好的!!
	 * @param _panel,
	 * @param _row,行号，因为列表是有行的，所有存在行号的问题!!!!!对于卡片来说这个参数是不启效果的!!
	 */
	public JepFormulaParseAtUI(BillPanel _panel) {
		initNormalFunction(); //调用父类常用的计算函数!!

		//下面8个函数的定义与BS端一样,但实现类方法与在BS端不一样,因为BS端没有Panel..而是传入HashMap!!!
		//8个常用的控件操作函数，其中6个容易实现!!这些参数中的ItemKey以后会有更复杂的变化，比如点操作，或前辍加上是哪个组件的索引，从而实现组件连动!!!!
		//这三个公式,卡片与列表都可有!!
		parser.addFunction("getColValue2", new GetItemValue(_panel)); //取得表中某一个字段的值!!

		parser.addFunction("getItemValue", new GetItemValue(_panel)); //取得某一项的值!优先实现!!其中参数可以带点,表示可以取到下拉框或参照的的任意一项内容!!!!
		parser.addFunction("getRefName", new GetRefName(_panel)); //取得某一项的值!优先实现!!其中参数可以带点,表示可以取到下拉框或参照的的任意一项内容!!!!
		parser.addFunction("getRefCode", new GetRefCode(_panel)); //取得某一项的值!优先实现!!其中参数可以带点,表示可以取到下拉框或参照的的任意一项内容!!!!
		parser.addFunction("getMultiRefName", new GetMultiRefName(_panel)); //设置某一项的值!优先实现!!!!!
		parser.addFunction("AddItemValueChangedListener", new AddItemValueChangedListener(_panel)); //gaofeng 加入灵活控制界面元素的公式
		parser.addFunction("getStringItemVO", new GetStringItemVO(_panel)); //取得一个文本框数据
		parser.addFunction("getComBoxItemVO", new GetComBoxItemVO(_panel)); //取得一个下拉框数据
		parser.addFunction("getRefItemVO", new GetRefItemVO(_panel)); //取得一个参照数据
		parser.addFunction("getYearDateRefItemVO", new GetYearDateRefItemVO(_panel)); //取得一个参照数据【李春娟/2016-03-20】

		parser.addFunction("getNotNullItemCount", new GetNotNullItemCount(_panel)); //取得一个参照数据

		parser.addFunction("setItemValue", new SetItemValue(_panel)); //设置某一项的值!优先实现!!!!!
		parser.addFunction("setItemLabel", new SetItemLabel(_panel)); //设置某一项的列名!优先实现!!!!!

		parser.addFunction("resetAllItemValue", new ResetAllItemValue(_panel)); //清空某一项的值!!优先实现!!!!
		parser.addFunction("resetItemValue", new ResetItemValue(_panel)); //清空某一项的值!!优先实现!!!!
		parser.addFunction("resetCardGroupValue", new ResetCardGroupValue(_panel)); //清空某一项的值!!优先实现!!!!
		parser.addFunction("getClientEnvironmentPut", new GetClientEnvironmentPut()); // 加入getLoginCode()函数
		parser.addFunction("setClientEnvironmentPut", new SetClientEnvironmentPut()); // 加入getLoginCode()函数

		//下面公式应只限于卡片
		parser.addFunction("SetItemIsmustinput", new SetItemIsmustinput(_panel));
		parser.addFunction("setItemEditable", new SetItemEditable(_panel)); //设置某一项是否可编辑!优先实现!!
		parser.addFunction("setItemVisiable", new SetItemVisiable(_panel)); //设置某一项是否可编辑!优先实现!!

		parser.addFunction("setItemForeGround", new SetItemForeGround(_panel)); //设置某一项的的前景颜色!!
		parser.addFunction("setItemBackGround", new SetItemBackGround(_panel)); //设置某一项的的背景颜色!只支持列表【李春娟/2014-11-13】

		parser.addFunction("setCardRowVisiable", new SetCardRowVisiable(_panel)); //设置卡片中某一行的所有控件都显示/隐藏
		parser.addFunction("setCardGroupVisiable", new SetCardGroupVisiable(_panel)); //设置某一项是否可编辑!优先实现!!
		parser.addFunction("setCardGroupExpand", new SetCardGroupExpand(_panel)); //设置某一项是否可编辑!优先实现!!
		parser.addFunction("getEditState", new GetEditState(_panel)); //获得卡片的编辑状态【李春娟/2013-05-09】
		
		//parser.addFunction("reloadItemValue", new ReloadItemValue(_panel, _row)); //重新加载数据,比如一个下拉框中的数据!!这个还要吗?

		//设置参照Code
		parser.addFunction("setRefItemCode", new SetRefItemCode(_panel)); //设置参照编码
		parser.addFunction("setRefItemName", new SetRefItemName(_panel)); //设置参照编码
		parser.addFunction("setRefDefine", new SetRefDefine(_panel)); //设置参照编码

		//取得控件中的值!
		parser.addFunction("getTreeItemLevelValue", new GetTreeItemLevelValue(_panel)); //设置某一项的值!优先实现!!!!!
		parser.addFunction("getFormatChildListItemValue", new GetFormatChildListItemValue(_panel)); //定义注册参照
		parser.addFunction("getFormatChildTreeItemValue", new GetFormatChildTreeItemValue(_panel)); //定义注册参照

		//设置网格中的值
		parser.addFunction("setCellItemValue", new SetCellItemValue(_panel)); //设置某一项的值!优先实现!!!!!
		parser.addFunction("getCellItemValue", new GetCellItemValue(_panel, false)); //取得某一项的值!优先实现!!!!!
		parser.addFunction("getCellItemNumberValue", new GetCellItemValue(_panel, true)); //取得某一项的值!优先实现!!!!!

		//与工作流相关的公式,比如根据表单中的值,取得流程实例,然后取得流程的相关信息,比如当前流程编码,当前环节编码,当前流程处理人,流程历史处理人!!!
		parser.addFunction("getWFInfo", new GetWFInfo(_panel, true)); //取得工作流相关
		parser.addFunction("showMsg", new ShowMsg(_panel));
	}

	public JepFormulaParseAtUI(BillPanel _panel, WLTButton _btn) {
		this(_panel);
		parser.addFunction("execWLTAction", new ExecWLTAction(_panel, _btn)); //执行按钮动作
		// 按钮函数，根据参数获得不同的展示
		parser.addFunction("showNewCardDialog", new ShowNewCardDialog(_panel, _btn)); //执行按钮动作
		parser.addFunction("openBillListDialog", new OpenBillListDialog(_panel, _btn)); //执行按钮动作
	}

	public JepFormulaParseAtUI(BillPanel _panel, WLTButton _btn, BillFormatPanel _billformatpanel, String _regformula1, String _regformula2, String _regformula3) {
		this(_panel);
		parser.addFunction("execWLTAction", new ExecWLTAction(_panel, _btn)); //执行按钮动作
		//注册所有函数
		parser.addFunction("getCard", new GetFormatItemPanel(_billformatpanel, GetFormatItemPanel.ITEMTYPE_CARD)); //
		parser.addFunction("getList", new GetFormatItemPanel(_billformatpanel, GetFormatItemPanel.ITEMTYPE_LIST)); //
		parser.addFunction("getTree", new GetFormatItemPanel(_billformatpanel, GetFormatItemPanel.ITEMTYPE_TREE)); //
		parser.addFunction("getCell", new GetFormatItemPanel(_billformatpanel, GetFormatItemPanel.ITEMTYPE_CELL)); //

		parser.addFunction("getSelectedBillVOItemValue", new GetSelectedBillVOItemValueParse(_billformatpanel)); //事件绑定函数
		parser.addFunction("addSelectEventBindRefresh", new AddSelectEventBindRefresh(_billformatpanel)); //事件绑定函数
		parser.addFunction("addSelectQuickQueryRefresh", new AddSelectQuickQueryRefresh(_billformatpanel)); //事件绑定函数

		parser.addFunction("queryAllData", new QueryAllDataParse(_billformatpanel)); //事件绑定函数
		parser.addFunction("getUserRole", new GetUserRole()); //事件绑定函数

		parser.addFunction("isSelected", new IsSelected(_billformatpanel)); //事件绑定函数

		parser.addFunction("queryDataByCondition", new QueryDataByConditionParse(_billformatpanel)); //事件绑定函数
		parser.addFunction("setEveryBtnBarIsVisiable", new SetEveryBtnBarIsVisiable(_billformatpanel)); //事件绑定函数
		parser.addFunction("setSelfBtnBarIsVisiable", new SetSelfBtnBarIsVisiable(_billformatpanel)); //事件绑定函数
		parser.addFunction("setBtnBarVisiable", new SetBtnBarVisiableParse(_billformatpanel)); //设置按钮是否全部显示/隐藏
		parser.addFunction("setBillPanelBtnVisiable", new SetBillPanelBtnVisiableParse(_billformatpanel)); //调置某个按钮是否显示

		parser.addFunction("setListQQVisiable", new SetListQQVisiableParse(_billformatpanel)); //事件绑定函数
		parser.addFunction("clearTable", new ClearTable(_billformatpanel)); //事件绑定函数
		parser.addFunction("setSelfListQQIsVisiable", new SetSelfListQQIsVisiable(_billformatpanel)); //事件绑定函数
		parser.addFunction("setListQQIsVisiable", new SetListQQIsVisiableParse(_billformatpanel)); //事件绑定函数
		parser.addFunction("setDefaultRefItemVOReturnFrom", new SetDefaultRefItemVOReturnFromParse(_billformatpanel)); //事件绑定函数
		parser.addFunction("setListQQAfterIsVisiable", new SetListQQAfterIsVisiable(_billformatpanel)); //事件绑定函数
		parser.addFunction("setSelfListQQAfterIsVisiable", new SetSelfListQQAfterIsVisiable(_billformatpanel)); //事件绑定函数
		parser.addFunction("setQQAfterClearTable", new SetQQAfterClearTable(_billformatpanel)); //事件绑定函数

		//注册事件绑定函数
		parser.addFunction("addBillSelectListener", new AddBillSelectListenerParse(_billformatpanel)); //列表或树,选择变化时触发的事件,最有用的公式!!!

		parser.addFunction("getRegFormula", new GetRegFormulaParse(_billformatpanel, _regformula1, _regformula2, _regformula3)); //列表或树,选择变化时触发的事件,最有用的公式!!!

		// 按钮函数，根据参数获得不同的展示
		parser.addFunction("showNewCardDialog", new ShowNewCardDialog(_panel, _btn)); //执行按钮动作

		// 按钮公式，直接弹出参照，用于关联两个事物
		parser.addFunction("DirectLinkRef", new DirectLinkRef(_panel, _btn)); //执行按钮动作

		parser.addFunction("openBillListDialog", new OpenBillListDialog(_panel, _btn)); //执行按钮动作

	}

	/**
	 * BillCellPanel的公式解析器
	 * @param _cellPanel
	 * @param _row
	 * @param _col
	 */
	public JepFormulaParseAtUI(BillCellPanel _cellPanel, int _row, int _col) {
		this(_cellPanel);
		parser.addFunction("getCellCurrRowItemValue", new GetCellCurrRowColItemValue(_cellPanel, _row, _col, 1, false)); //取得网络中同一行中某一列上的值!!
		parser.addFunction("getCellCurrRowItemNumberValue", new GetCellCurrRowColItemValue(_cellPanel, _row, _col, 1, true)); //取得网络中同一行中某一列上的值,转换成数字!!
		parser.addFunction("getCellCurrColItemValue", new GetCellCurrRowColItemValue(_cellPanel, _row, _col, 2, false)); //取得网络中同一列中某一行上的值!!
		parser.addFunction("getCellCurrColItemNumberValue", new GetCellCurrRowColItemValue(_cellPanel, _row, _col, 2, true)); //取得网络中同一列中某一行上的值,转换成数字!!
		parser.addFunction("getCellSumValueByRowCol", new GetCellSumValueByRowCol(_cellPanel, _row, _col)); //取得某一项的值!优先实现!!!!!
		parser.addFunction("getCellSumValueByKeys", new GetCellSumValueByKeys(_cellPanel, _row, _col)); //取得某一项的值!优先实现!!!!!
	}

	/**
	 * BillCellPanel的公式解析器
	 * @param _cellPanel
	 * @param _editingValue
	 * @param _row
	 * @param _col
	 */
	public JepFormulaParseAtUI(BillCellPanel _cellPanel, String _editingValue, int _row, int _col) {
		this(_cellPanel, _row, _col);
		parser.addFunction("getCellEditingItemNumberValue", new GetCellEditingItemNumberValue(_cellPanel, _editingValue)); //取得网络中正在编辑的格子中的值..
	}

}
