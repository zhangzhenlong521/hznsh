package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.mdata.CommUCDefineVO;

/**
 * ͨ�õ�ȡ�ÿؼ�����!!!��ǰ��һ�����Ĺ�ʽ��VO,����������չ������!!!���һ����������!!
 * ����Ū��һ��ͨ�õĹ�ʽ��VO,ͨ��VO��CommUCDefineVO
 * @author xch
 *
 */
public class GetCommUC extends PostfixMathCommand {
	public GetCommUC() {
		numberOfParameters = -1;
	}

	public void run(Stack _inStack) throws ParseException {
		try {
			checkStack(_inStack); //
			Object[] str_pars = new String[curNumberOfParameters]; //��������б�!Ӧ����һ��,��λ������ż��!
			for (int i = str_pars.length - 1; i >= 0; i--) { //�����ú�������!!
				str_pars[i] = _inStack.pop(); //
			}
			CommUCDefineVO commUCVO = new CommUCDefineVO(); //�����ؼ�����VO
			commUCVO.setTypeName((String) str_pars[0]); //��һ���϶��ǿؼ�����,����ķֱ��ǲ����������ֵ!!
			//��������в���,����������!!!
			for (int i = 0; i < (str_pars.length - 1) / 2; i++) { //
				String str_key = (String) str_pars[i * 2 + 1]; //��Ϊ��һ��������,����Ҫ��1
				String str_value = (String) str_pars[i * 2 + 2]; //��Ϊ��һ��������,�����ǵڶ���,����Ҫ��2
				commUCVO.setConfValue(str_key, str_value); //����ֵ!!
			}
			_inStack.push(commUCVO); //���ؿؼ�����VO!!!
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
}
