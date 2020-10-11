package cn.com.infostrategy.bs.workflow.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

/**
 * 在流程引擎中设置路由标记!!
 * @author xch
 *
 */
public class GetWFRouterMarkCount extends PostfixMathCommand {

	private String str_prinstanceid = null; //

	/**
	 * 构造方法..
	 * @param _dealpoolid 
	 * @param _prinstanceid 
	 * @param _callvo. 
	 */
	public GetWFRouterMarkCount(WFParVO _callvo, String _prinstanceid, String _dealpoolid) {
		numberOfParameters = 1;
		str_prinstanceid = _prinstanceid; //
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //
		String str_key = (String) inStack.pop(); //
		String str_sql = "select routemark from pub_wf_prinstance where id='" + str_prinstanceid + "'"; //找最后一个路由标记名为对应名的标记值
		try {
			TBUtil tbutil = new TBUtil(); //
			HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, str_sql); //
			int li_count = 0; //
			if (hvs != null && hvs.length > 0) {
				String str_routemark = hvs[0].getStringValue("routemark"); //取得路由标记!!
				if (str_routemark != null && !str_routemark.trim().equals("")) { //如果路由标记不为空!
					String[] str_allmarks = tbutil.split(str_routemark, ";"); //以分号分隔!算出有几对
					if (str_allmarks != null && str_allmarks.length > 0) { //
						for (int i = 0; i < str_allmarks.length; i++) { //
							String[] str_keyvalues = tbutil.split(str_allmarks[i], "="); //以=为分隔,找出key与value.
							if (str_keyvalues[0].equalsIgnoreCase(str_key)) {
								li_count++;
							}
						}
					}
				}
			}
			inStack.push(new Double(li_count)); //
		} catch (Exception e) {
			e.printStackTrace(); //
			inStack.push(new WLTAppException("在连线上执行getWFRouterMarkValue(\"" + str_key + "\")公式发生异常!!")); //
		}
	}
}
