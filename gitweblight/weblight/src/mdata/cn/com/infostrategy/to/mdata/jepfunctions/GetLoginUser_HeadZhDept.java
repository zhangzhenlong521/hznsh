package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

public class GetLoginUser_HeadZhDept extends PostfixMathCommand {
	public GetLoginUser_HeadZhDept() {
		numberOfParameters = 0;
	}

	public void run(Stack inStack) throws ParseException {
		try {
			checkStack(inStack); //
			String str_result = (String) ClientEnvironment.getInstance().getCurrLoginUserVO().getBlDeptId();
			if (str_result == null) {
				str_result = "";
			} else {

				HashVO[] hashvo = UIUtil.getHashVoArrayByDS(null, "select linkcode from pub_corp_dept where id='" + str_result + "'");
				if (hashvo[0].getStringValue("linkcode").length() == 12) {//支行/分行部门
					HashVO[] hashvo1 = UIUtil.getHashVoArrayByDS(null, "select name from pub_corp_dept where linkcode='" + hashvo[0].getStringValue("linkcode").substring(0, 12) + "'");

					inStack.push(hashvo1[0].getStringValue("name"));
				} else if (hashvo[0].getStringValue("linkcode").length() > 12) {//支行部门
					HashVO[] hashvo1 = UIUtil.getHashVoArrayByDS(null, "select name from pub_corp_dept where linkcode='" + hashvo[0].getStringValue("linkcode").substring(0, 12) + "'");

					inStack.push(hashvo1[0].getStringValue("name"));

				}
			}

		} catch (Throwable ex) {
			ex.printStackTrace();
		}

	}
}
