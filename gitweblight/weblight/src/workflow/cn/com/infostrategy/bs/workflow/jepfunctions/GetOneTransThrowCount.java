package cn.com.infostrategy.bs.workflow.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

/**
 * 在流程引擎中增加一个路由标记!!
 * 从流程实例中找,直接在路由标记后面增加!
 * @author xch
 *
 */
public class GetOneTransThrowCount extends PostfixMathCommand {

	private String str_prinstanceid = null; //
	private String str_dealpoolid = null; //

	/**
	 * 构造方法..
	 * @param _dealpoolid 
	 * @param _prinstanceid 
	 * @param _callvo 
	 */
	public GetOneTransThrowCount(WFParVO _callvo, String _prinstanceid, String _dealpoolid) {
		numberOfParameters = 1;
		str_prinstanceid = _prinstanceid; //
		str_dealpoolid = _dealpoolid; //
	}

	/**
	 * 执行公式.
	 */
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //
		String str_tansCode = (String) inStack.pop(); //连线编码!!!
		try {
			String str_prinstanceId = this.str_prinstanceid; //流程实例Id
			String str_sql = "select count(*) c1 from pub_wf_dealpool t1,pub_wf_transition t2 where t1.transition=t2.id and t1.prinstanceid='" + str_prinstanceId + "' and t2.code='" + str_tansCode + "'"; //
			String str_count = new CommDMO().getStringValueByDS(null, str_sql); //可能因为连线编码不对导致找不支
			int li_count = Integer.parseInt(str_count); //
			System.out.println("执行连线上的公式,计算连线[]上走过的次数=[" + li_count + "]"); //
			inStack.push(new Integer(li_count)); //
		} catch (Exception e) {
			e.printStackTrace(); //
			inStack.push(new Integer(0)); //
			//inStack.push(new WLTAppException("在连线上执行setWFRouterMarkValue(\"" + str_key + "\",\"" + str_value + "\")公式发生异常!!")); //
		}
	}
}
