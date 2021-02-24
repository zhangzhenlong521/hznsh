package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

public class GetLoginUser_BranchDept extends PostfixMathCommand {
	public GetLoginUser_BranchDept() {
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
				if (hashvo[0].getStringValue("linkcode").substring(0, 8).equals("00010001")) {
					inStack.push("");
				} else {
					HashVO[] hashvo1 = UIUtil.getHashVoArrayByDS(null, "select name from pub_corp_dept where linkcode='" + hashvo[0].getStringValue("linkcode").substring(0, 8) + "'");

					inStack.push(hashvo1[0].getStringValue("name").substring(0, 2));
				}
			}

		} catch (Throwable ex) {
			ex.printStackTrace();
		}

	}
}
