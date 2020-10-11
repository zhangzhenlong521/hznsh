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
 * ����������������·�ɱ��!!
 * @author xch
 *
 */
public class GetWFRouterMarkCount extends PostfixMathCommand {

	private String str_prinstanceid = null; //

	/**
	 * ���췽��..
	 * @param _dealpoolid 
	 * @param _prinstanceid 
	 * @param _callvo. 
	 */
	public GetWFRouterMarkCount(WFParVO _callvo, String _prinstanceid, String _dealpoolid) {
		numberOfParameters = 1;
		str_prinstanceid = _prinstanceid; //
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //
		String str_key = (String) inStack.pop(); //
		String str_sql = "select routemark from pub_wf_prinstance where id='" + str_prinstanceid + "'"; //�����һ��·�ɱ����Ϊ��Ӧ���ı��ֵ
		try {
			TBUtil tbutil = new TBUtil(); //
			HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, str_sql); //
			int li_count = 0; //
			if (hvs != null && hvs.length > 0) {
				String str_routemark = hvs[0].getStringValue("routemark"); //ȡ��·�ɱ��!!
				if (str_routemark != null && !str_routemark.trim().equals("")) { //���·�ɱ�ǲ�Ϊ��!
					String[] str_allmarks = tbutil.split(str_routemark, ";"); //�Էֺŷָ�!����м���
					if (str_allmarks != null && str_allmarks.length > 0) { //
						for (int i = 0; i < str_allmarks.length; i++) { //
							String[] str_keyvalues = tbutil.split(str_allmarks[i], "="); //��=Ϊ�ָ�,�ҳ�key��value.
							if (str_keyvalues[0].equalsIgnoreCase(str_key)) {
								li_count++;
							}
						}
					}
				}
			}
			inStack.push(new Double(li_count)); //
		} catch (Exception e) {
			e.printStackTrace(); //
			inStack.push(new WLTAppException("��������ִ��getWFRouterMarkValue(\"" + str_key + "\")��ʽ�����쳣!!")); //
		}
	}
}
