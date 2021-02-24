package cn.com.infostrategy.ui.sysapp.corpdept;

import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/**
 * 机构树的BillTreePanel,新的机构权限模型!!!以后将成为水平产品的标准!!!!!
 * @author xch
 *
 */
public class CorpDeptBillTreePanel extends BillTreePanel {

	private static final long serialVersionUID = 1L; //

	private int li_granuType = -1; //粒度类型(共3种类型),1-总行,分行,事业部;2-总行,总行部门,事业部,事业部分部,分行(最常用的),支行;3-所有

	private boolean isFilterByLoginUser = false;

	private boolean custConditionIsNull = false; //

	public CorpDeptBillTreePanel() {
		super("pub_corp_dept_CODE1"); //机构树
	}

	/**
	 * 根据粒度与是否权限过滤,生成不同形式的树!!
	 * @param _granuType
	 * @param _isFilterByLoginUser
	 */
	public CorpDeptBillTreePanel(int _granuType, boolean _isFilterByLoginUser) {
		super("pub_corp_dept_Ref"); //机构树
		this.li_granuType = _granuType; //
		this.isFilterByLoginUser = _isFilterByLoginUser; //
	}

	/**
	 * 根据粒度与是否权限过滤,生成不同形式的树!!
	 * @param _granuType
	 * @param _isFilterByLoginUser
	 */
	public CorpDeptBillTreePanel(String _templetCode, int _granuType, boolean _isFilterByLoginUser) {
		super(_templetCode); //机构树
		this.li_granuType = _granuType; //
		this.isFilterByLoginUser = _isFilterByLoginUser; //
	}

	@Override
	/**
	 * 自定义过滤条件，如果为空,则可以显示"所有范围"按钮，这样就无需要为corpdept in ()拼一个老长的SQL,性能会高许多
	 */
	public String getCustCondition() {
		String str_granuCondition = null; ////
		if (li_granuType == 1) { ////如果是第一种粒度模式
			str_granuCondition = "corptype in ('总行','总行部门','分行','事业部')"; //
		} else if (li_granuType == 2) { ////如果是第二种粒度模式
			str_granuCondition = "corptype in ('总行','总行部门','事业部','事业部分部','分行','分行部门','支行')"; //..
		} else if (li_granuType == 3) { //如果是3,则为空,为空就表示查出所有的!!
			str_granuCondition = null; //
		} else if (li_granuType == 4) {
			str_granuCondition = "corptype in ('总行','一级分行')";
		}

		String str_filterCondition = null; //权限过滤,即总行看所有,本分行看本分行的!
		//如果需要进行权限过滤..
		if (isFilterByLoginUser) { //
			str_filterCondition = UIUtil.getLoginUserBLDeptCondition(); //
		}

		String str_allcondition = null; //
		if (str_granuCondition == null) { //如果无粒度过滤
			if (str_filterCondition == null) { //如果两者都为空,则不做任何处理!!
			} else {
				str_allcondition = str_filterCondition; //
			}
		} else {
			if (str_filterCondition == null) {
				str_allcondition = str_granuCondition; //
			} else {
				str_allcondition = str_granuCondition + " and " + str_filterCondition; //
			}
		}
		if (str_allcondition == null) {
			custConditionIsNull = true; //为空
		} else {
			custConditionIsNull = false; //
		}
		return str_allcondition; //
	}

	public boolean isCustConditionIsNull() {
		return custConditionIsNull;
	}

}
