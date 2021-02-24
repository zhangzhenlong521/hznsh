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
				if(corptype.substring(0, 2).equals("����")){// ���в��ż���ҵ��//��ҵ���ֲ�_��������
					inStack.push("����");
				}else if(corptype.substring(0, 2).equals("����")||corptype.equals("֧��")||corptype.equals("֧��_��������")||corptype.equals("��ҵ���ֲ�")||corptype.equals("��ҵ���ֲ�_��������")) {
					inStack.push(hashvo[0].getStringValue("bl_fengh_name"));
				}else if(corptype.equals("��ҵ��")||corptype.equals("��ҵ��_��������")){
					inStack.push(hashvo[0].getStringValue("bl_shiyb_name"));
				}
				
			
			}

		} catch (Throwable ex) {
			ex.printStackTrace();
		}

	}
}
