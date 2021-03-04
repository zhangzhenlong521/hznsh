package cn.com.infostrategy.bs.workflow;

import cn.com.infostrategy.bs.workflow.jepfunctions.AddWFRouterMarkValue;
import cn.com.infostrategy.bs.workflow.jepfunctions.GetOneTransThrowCount;
import cn.com.infostrategy.bs.workflow.jepfunctions.GetWFBillItemValue;
import cn.com.infostrategy.bs.workflow.jepfunctions.GetWFRouterMarkCount;
import cn.com.infostrategy.bs.workflow.jepfunctions.GetWFRouterMarkValue;
import cn.com.infostrategy.bs.workflow.jepfunctions.SetWFRouterMarkValue;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.jepfunctions.GetParAsMap;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

public class JepFormulaParseAtWorkFlow extends JepFormulaParse {

	@Override
	protected byte getJepType() {
		return WLTConstants.JEPTYPE_BS;
	}

	/**
	 * 构造方法
	 * @param __billvo 
	 * @param callVO 
	 */
	public JepFormulaParseAtWorkFlow(WFParVO _callVO, String _prinstanceid, String _dealpoolid, BillVO _billvo) {
		initNormalFunction(); //常用的计算函数!!!
		parser.addFunction("setWFRouterMarkValue", new SetWFRouterMarkValue(_callVO, _prinstanceid, _dealpoolid)); //设置工作流引擎的路由标记!
		parser.addFunction("addWFRouterMarkValue", new AddWFRouterMarkValue(_callVO, _prinstanceid, _dealpoolid)); //增加一个路由标记!
		parser.addFunction("getWFRouterMarkValue", new GetWFRouterMarkValue(_callVO, _prinstanceid, _dealpoolid)); //设置工作流引擎的路由标记!
		parser.addFunction("getWFRouterMarkCount", new GetWFRouterMarkCount(_callVO, _prinstanceid, _dealpoolid)); //取得某个路由标记的个数!!
		parser.addFunction("getWFBillItemValue", new GetWFBillItemValue(_callVO, _prinstanceid, _dealpoolid, _billvo)); //设置工作流引擎的路由标记!
		parser.addFunction("getOneTransThrowCount", new GetOneTransThrowCount(_callVO, _prinstanceid, _dealpoolid)); //设置工作流引擎的路由标记!
		parser.addFunction("setExtConfMap", new GetParAsMap()); //设置扩展参数
	}
}
