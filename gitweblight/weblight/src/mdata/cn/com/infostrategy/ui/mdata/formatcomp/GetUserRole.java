package cn.com.infostrategy.ui.mdata.formatcomp;

/**
 * 得到用户角色
 */
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

public class GetUserRole extends PostfixMathCommand {

	public GetUserRole() {

		numberOfParameters = 0; //
	}

	public void run(Stack inStack) throws ParseException {

		String str_userid = ClientEnvironment.getInstance().getLoginUserID();
		try {
			checkStack(inStack); //
			HashVO[] hashvo = UIUtil.getHashVoArrayByDS(null, "Select prole.code as pro from pub_user puser,pub_user_role ur,pub_role prole where puser.id=ur.userid and prole.id=ur.roleid And puser.Id='" + str_userid + "'");
			if (hashvo.length == 1) {
				inStack.push(hashvo[0].getStringValue("pro")); //
			} else {
				String roles = "";
				for (int i = 0; i < hashvo.length; i++) {
					roles = roles + hashvo[i].getStringValue("pro") + ",";
				}
				inStack.push(roles); //

			}

		} catch (WLTRemoteException e) {

			e.printStackTrace();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}
}
