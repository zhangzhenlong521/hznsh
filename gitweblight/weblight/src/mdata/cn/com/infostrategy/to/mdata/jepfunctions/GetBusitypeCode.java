package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

public class GetBusitypeCode extends PostfixMathCommand {
	public GetBusitypeCode() {
		numberOfParameters = 3;
	}

	public void run(Stack inStack) throws ParseException {
		try {
			checkStack(inStack); //

			Object param_1 = inStack.pop();
			String str_param1 = (String) param_1; //
			Object param_2 = inStack.pop();
			String str_param2 = (String) param_2; //
			Object param_3 = inStack.pop();
			String str_param3 = (String) param_3; //
			String number = "";
			String str_result = (String) ClientEnvironment.getInstance().getLoginUserID();
			if (str_result == null) {
				str_result = "";
			} else {
				String sql = "select deptid    from  v_pub_user_post_1  where  userid=" + str_result + "";
				HashVO[] hashvo = UIUtil.getHashVoArrayByDS(null, "select * from pub_corp_dept where id='" + UIUtil.getStringValueByDS(null, sql) + "'");
				String corptype = hashvo[0].getStringValue("corptype");
				if (corptype.substring(0, 2).equals("总行") || corptype.equals("事业部") || corptype.equals("事业部_下属机构")) {
					number = UIUtil.getStringValueByDS(null, "select number from cmb_fhbanksequence where bankname='总行'");
				} else {
					String deptname = UIUtil.getStringValueByDS(null, "select name from  pub_corp_dept where id=(select bl_fengh  from   pub_corp_dept where id='" + UIUtil.getStringValueByDS(null, sql) + "')");
					number = UIUtil.getStringValueByDS(null, "select number from cmb_fhbanksequence where bankname='" + deptname + "'");
				}
			}
			inStack.push(str_param3 +str_param2 + "("+str_param1+")" + number);

		} catch (Throwable ex) {
			ex.printStackTrace();
		}

	}
}
