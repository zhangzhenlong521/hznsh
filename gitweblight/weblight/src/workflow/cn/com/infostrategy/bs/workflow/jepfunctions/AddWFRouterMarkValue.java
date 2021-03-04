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
 * ����������������һ��·�ɱ��!!
 * ������ʵ������,ֱ����·�ɱ�Ǻ�������!
 * @author xch
 *
 */
public class AddWFRouterMarkValue extends PostfixMathCommand {

	private String str_prinstanceid = null; //
	private String str_dealpoolid = null; //d

	/**
	 * ���췽��..
	 * @param _dealpoolid 
	 * @param _prinstanceid 
	 * @param _callvo 
	 */
	public AddWFRouterMarkValue(WFParVO _callvo, String _prinstanceid, String _dealpoolid) {
		numberOfParameters = 2;
		str_prinstanceid = _prinstanceid; //
		str_dealpoolid = _dealpoolid; //
	}

	/**
	 * ִ�й�ʽ.
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
				str_mark = str_mark + str_key + "=" + str_value + ";"; //
				new CommDMO().executeUpdateByDS(null, "update pub_wf_prinstance set routemark='" + str_mark + "' where id='" + str_prinstanceid + "'"); //ִ���µ�·�ɱ��
			}
		} catch (Exception e) {
			e.printStackTrace(); //
			inStack.push(new WLTAppException("��������ִ��setWFRouterMarkValue(\"" + str_key + "\",\"" + str_value + "\")��ʽ�����쳣!!")); //
		}
	}
}
