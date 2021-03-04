package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

public class GetLoginUser_HeadFhDept extends PostfixMathCommand {
	public GetLoginUser_HeadFhDept() {
		numberOfParameters = 0;
	}

	public void run(Stack inStack) throws ParseException {
		try {
			checkStack(inStack); //
			String str_result = (String) ClientEnvironment.getInstance().getLoginUserID();
			if (str_result == null) {
				str_result = "";
			} else {
				String sql = "select deptid    from  v_pub_user_post_1  where  userid=" + str_result + "";
				HashVO[] hashvo = UIUtil.getHashVoArrayByDS(null, "select * from pub_corp_dept where id='" + UIUtil.getStringValueByDS(null, sql) + "'");
				String corptype=hashvo[0].getStringValue("corptype");
				
				if (corptype.substring(0, 2).equals("总行")||corptype.equals("事业部")||corptype.equals("事业部_下属机构")) {
						inStack.push("总行");
				} else {

					inStack.push(UIUtil.getStringValueByDS(null, "select name from  pub_corp_dept where id=(select bl_fengh  from   pub_corp_dept where id='" + UIUtil.getStringValueByDS(null, sql) + "')"));
				}
			}

		} catch (Throwable ex) {
			ex.printStackTrace();
		}

	}
}
