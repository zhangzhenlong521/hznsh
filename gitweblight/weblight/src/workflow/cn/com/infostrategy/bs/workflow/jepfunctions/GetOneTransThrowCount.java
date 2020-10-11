package cn.com.infostrategy.bs.workflow.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

/**
 * ����������������һ��·�ɱ��!!
 * ������ʵ������,ֱ����·�ɱ�Ǻ�������!
 * @author xch
 *
 */
public class GetOneTransThrowCount extends PostfixMathCommand {

	private String str_prinstanceid = null; //
	private String str_dealpoolid = null; //

	/**
	 * ���췽��..
	 * @param _dealpoolid 
	 * @param _prinstanceid 
	 * @param _callvo 
	 */
	public GetOneTransThrowCount(WFParVO _callvo, String _prinstanceid, String _dealpoolid) {
		numberOfParameters = 1;
		str_prinstanceid = _prinstanceid; //
		str_dealpoolid = _dealpoolid; //
	}

	/**
	 * ִ�й�ʽ.
	 */
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //
		String str_tansCode = (String) inStack.pop(); //���߱���!!!
		try {
			String str_prinstanceId = this.str_prinstanceid; //����ʵ��Id
			String str_sql = "select count(*) c1 from pub_wf_dealpool t1,pub_wf_transition t2 where t1.transition=t2.id and t1.prinstanceid='" + str_prinstanceId + "' and t2.code='" + str_tansCode + "'"; //
			String str_count = new CommDMO().getStringValueByDS(null, str_sql); //������Ϊ���߱��벻�Ե����Ҳ�֧
			int li_count = Integer.parseInt(str_count); //
			System.out.println("ִ�������ϵĹ�ʽ,��������[]���߹��Ĵ���=[" + li_count + "]"); //
			inStack.push(new Integer(li_count)); //
		} catch (Exception e) {
			e.printStackTrace(); //
			inStack.push(new Integer(0)); //
			//inStack.push(new WLTAppException("��������ִ��setWFRouterMarkValue(\"" + str_key + "\",\"" + str_value + "\")��ʽ�����쳣!!")); //
		}
	}
}
