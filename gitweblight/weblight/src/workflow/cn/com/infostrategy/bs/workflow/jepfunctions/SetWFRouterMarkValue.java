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
 * 从流程实例中找,如果找到则修改之,如果没找到则不做!这一点与AddWFRouterMarkValue不同!!!
 * @author xch
 *
 */
public class SetWFRouterMarkValue extends PostfixMathCommand {

	private String str_prinstanceid = null; //
	private String str_dealpoolid = null; //

	/**
	 * 构造方法..
	 * @param _dealpoolid 
	 * @param _prinstanceid 
	 * @param _callvo 
	 */
	public SetWFRouterMarkValue(WFParVO _callvo, String _prinstanceid, String _dealpoolid) {
		numberOfParameters = 2;
		str_prinstanceid = _prinstanceid; //
		str_dealpoolid = _dealpoolid; //
	}

	/**
	 * 执行公式.
	 */
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //
		String str_value = (String) inStack.pop(); //value
		String str_key = (String) inStack.pop(); //key

		try {
			TBUtil tbutil = new TBUtil(); //
			CommDMO dmo = new CommDMO(); //
			HashVO[] hvs = dmo.getHashVoArrayByDS(null, "select routemark from pub_wf_prinstance where id='" + str_prinstanceid + "'");
			if (hvs.length > 0) {
				String str_mark = hvs[0].getStringValue("routemark"); //
				if (str_mark == null) {
					str_mark = ""; //
				}

				String[] str_allmarks = tbutil.split(str_mark, ";"); //以分号分隔!算出有几对
				boolean bo_iffind = false; //
				StringBuffer sb_newmark = new StringBuffer(""); //
				if (str_allmarks != null && str_allmarks.length > 0) { //如果有数
					String[][] str_data = new String[str_allmarks.length][2];
					for (int i = 0; i < str_allmarks.length; i++) { //
						String[] str_keyvalues = tbutil.split(str_allmarks[i], "="); //以=为分隔,找出key与value.
						str_data[i][0] = str_keyvalues[0]; //
						str_data[i][1] = str_keyvalues[1]; //
					}

					for (int i = 0; i < str_data.length; i++) { //遍历所有记录数!
						if (str_data[i][0].equalsIgnoreCase(str_key)) {
							str_data[i][1] = str_value; //
							bo_iffind = true; //设置为true
						}
					}

					for (int i = 0; i < str_data.length; i++) { //遍历所有记录数!
						sb_newmark.append(str_data[i][0] + "=" + str_data[i][1] + ";"); //
					}
				}

				//如果没找到则加上
				if (!bo_iffind) {
					sb_newmark.append(str_key + "=" + str_value + ";"); //
				}
				new CommDMO().executeUpdateByDS(null, "update pub_wf_prinstance set routemark='" + sb_newmark + "' where id='" + str_prinstanceid + "'"); //执行新的路由标记
			}
		} catch (Exception e) {
			e.printStackTrace(); //
			inStack.push(new WLTAppException("在连线上执行setWFRouterMarkValue(\"" + str_key + "\",\"" + str_value + "\")公式发生异常!!")); //
		}
	}
}
