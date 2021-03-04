package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

public class GetLoginUserBelongDept extends PostfixMathCommand {

	public GetLoginUserBelongDept() {
		numberOfParameters = 0;
	}

	public void run(Stack inStack) throws ParseException {
		try {
			checkStack(inStack); //
			String str_result = (String) ClientEnvironment.getInstance().getLoginUserID();
			if (str_result == null) {
				str_result = "";
			} else {

				String sql = "select userdept  from  pub_user_post  where  userid=" + str_result + "";
				HashVO[] hashvo = UIUtil.getHashVoArrayByDS(null, "select corptype  from pub_corp_dept where id='" + UIUtil.getStringValueByDS(null, sql) + "'");
				if(hashvo != null && hashvo.length > 0) {
					String corptype=hashvo[0].getStringValue("corptype");
					
					if (corptype.substring(0, 2).equals("总行")||corptype.equals("事业部")||corptype.equals("事业部_下属机构")) {
							inStack.push("总行");
						
					} else {
						inStack.push("分行");

					}

				}
				
			}

		} catch (Throwable ex) {
			ex.printStackTrace();
		}

	}

}
