package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

public class GetLoginUser_Dept  extends PostfixMathCommand{
	public GetLoginUser_Dept() {
		numberOfParameters = 0;
	}
	
	public void run(Stack inStack) throws ParseException {
		try {
			checkStack(inStack); //
			String str_id = (String) ClientEnvironment.getInstance().getLoginUserID();
			HashVO[] hashvo1 = UIUtil.getHashVoArrayByDS(null, "select *  from  pub_user_post where userid='" + str_id + "'");
			String str_result=hashvo1[0].getStringValue("userdept");
			if (str_result == null) {
				str_result = "";
			} else {
				HashVO[] hashvo = UIUtil.getHashVoArrayByDS(null, "select *  from pub_corp_dept where id='" + str_result + "'");
				String corptype=hashvo[0].getStringValue("corptype");
				if(corptype.substring(0, 2).equals("总行")){// 总行部门及事业部//事业部分部_下属机构
					inStack.push("总行");
				}else if(corptype.substring(0, 2).equals("分行")||corptype.equals("支行")||corptype.equals("支行_下属机构")||corptype.equals("事业部分部")||corptype.equals("事业部分部_下属机构")) {
					inStack.push(hashvo[0].getStringValue("bl_fengh_name"));
				}else if(corptype.equals("事业部")||corptype.equals("事业部_下属机构")){
					inStack.push(hashvo[0].getStringValue("bl_shiyb_name"));
				}
				
			
			}

		} catch (Throwable ex) {
			ex.printStackTrace();
		}

	}
}
