package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.LinkedHashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * �����в�������żλ�ֱ�Ϊkey��value���뵽һ����ϣ����! ����һ��HashMap
 * ע��,�����صĲ����ַ��������������!! �Ժ��������Ĳ�����Ӧ��ʹ��ͳһ�����ܵķ���������!!!
 * @author xch
 *
 */
public class GetParAsMap extends PostfixMathCommand {
	public GetParAsMap() {
		numberOfParameters = -1;
	}

	public void run(Stack _inStack) throws ParseException {
		try {
			checkStack(_inStack); //
			Object[] str_pars = new String[curNumberOfParameters]; //��������б�!Ӧ����һ��,��λ������ż��!
			for (int i = str_pars.length - 1; i >= 0; i--) { //�����ú�������!!
				str_pars[i] = _inStack.pop(); //
			}
			LinkedHashMap returnMap = new LinkedHashMap(); //
			for (int i = 0; i < str_pars.length / 2; i++) {  //
				String str_key = (String) str_pars[i * 2]; //
				Object obj_value = str_pars[i * 2 + 1]; //
				returnMap.put(str_key, obj_value); //
			}
			_inStack.push(returnMap); //������ȥ!!!���ص��Ǹ���ϣ��!!!
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

	}
}
