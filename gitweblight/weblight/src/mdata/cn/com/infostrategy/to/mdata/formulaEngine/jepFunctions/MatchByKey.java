package cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions;

import java.math.BigDecimal;
import java.util.HashMap;

import org.nfunk.jep.EvaluatorI;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.CallbackEvaluationI;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;

/**
 * ��ƥ�书��,��Ҫ�Ǹ���һ��key,value ����,Ȼ��ƥ�䴫���ֵ,������ͬkey��Ӧ��value.��֧���ַ���
 * �ڶ������������磺ƥ��("ƥ�����",[��Ա��Ϣ.ѧ��],"ƥ��Դ",[ѧ������],"ƥ���ֶ�","name","�����ֶ�","����ֵ����","").
 * 
 * 1��ƥ����ε�ֵ�����ַ���
 * 2��ƥ��Դ����ֵHashVO���飬Ҳ������Map.
 * 3�������Map�Ͳ���Ҫ�����ƥ���ֶκͷ����ֶ�;�����HashVO[]��Ҫ�ƶ�ƥ��ͷ����ĸ��ֶΡ�
 * 4�� ��������һ���Ǳ��뷵������ʱ�õ��������������ʽ�а���ƥ�乫ʽ�����ܻ��漰������ֵ����.����һ������ֱ�Ӷ���ƥ�䣬�������Զ��������.
 * 
 * 
 * ʵ��CallbackEvaluationI�Ľӿڿ��Դ�node�л�ȡ����.�����㡣
 * @author haoming
 * create by 2013-8-12
 */
public class MatchByKey extends AbstractPostfixMathCommand implements CallbackEvaluationI {

	private TBUtil tbutil = TBUtil.getTBUtil();

	public MatchByKey(JEP _jep) {
		super(_jep);
	}

	public Object evaluate(Node node, EvaluatorI evaluatori) throws ParseException {
		int childCount = node.jjtGetNumChildren();
		Object obj[] = new Object[childCount];
		for (int j = 0; j < childCount; j++) {
			Node cnode = node.jjtGetChild(j); //��ȡ���ڵ�
			obj[j] = evaluatori.eval(cnode);
		}
		HashMap<Object, Object> allconditioninfo = new HashMap<Object, Object>(); //���е�������Ϣ
		for (int i = 0; i < obj.length; i++) {
			Object str_key = obj[i]; //��Ϊ��һ��������,����Ҫ��1
			Object str_value = obj[i + 1]; //��Ϊ��һ��������,�����ǵڶ���,����Ҫ��2
			allconditioninfo.put(str_key, str_value);
			i++;
		}
		String keyvalue = String.valueOf(allconditioninfo.get("ƥ�����"));//���봫��
		String keyitem = (String) allconditioninfo.get("ƥ���ֶ�"); //Ĭ����0λ��
		Object compare = allconditioninfo.get("ƥ��Դ");
		String returnitem = (String) allconditioninfo.get("�����ֶ�");
		String returnValueType = (String) allconditioninfo.get("����ֵ����");
		String currValue = keyvalue;
		Object compareValue = compare;
		if (compareValue == null) { //�����1һ��ֵ˵���������������д���ļ��϶���
			compareValue = getConditionData();
			if (compareValue == null) {
				compareValue = getInputData();
			}
		}
		Object rtvalue = null;
		if (compareValue instanceof String && compareValue.toString().contains("=")) { // ˵��������:par1=���,par2=�ҿ���
			HashMap map = tbutil.convertStrToMapByExpress(compareValue.toString());
			rtvalue = map.get(currValue); // �жϺ�õ���ֵ
			if (rtvalue == null) {
				if (childCount == 3) { //��Ĭ��ֵ
					rtvalue = obj[2];
				}
			}
		} else if (compareValue instanceof HashVO[]) { // ����һ�������ֶε�hashvo����
			HashVO vos[] = (HashVO[]) compareValue;
			for (int i = 0; i < vos.length; i++) {
				String key = null;
				if (keyitem != null && !keyitem.trim().equals("")) {
					key = vos[i].getStringValue(keyitem);
				} else {
					key = vos[i].getStringValue(0);
				}
				if (key != null && key.equals(currValue)) {
					if (returnitem != null && !returnitem.trim().equals("")) {
						rtvalue = vos[i].getStringValue(returnitem);
					} else {
						rtvalue = vos[i].getStringValue(1);
					}
					break;
				}
			}
		} else if (compareValue instanceof HashMap) {
			HashMap map = (HashMap) compareValue;
			rtvalue = map.get(currValue);
		}
		if (rtvalue == null) {
			rtvalue = "";
		}
		try {
			if (returnValueType != null && !"�ַ���".equals(returnValueType)) {
				if (rtvalue != null && !"".equals(rtvalue)) {
					if ("����".equals(returnValueType.trim())) {
						BigDecimal big = new BigDecimal(String.valueOf(obj).trim()).setScale(0, BigDecimal.ROUND_HALF_UP);
						rtvalue = big.intValue();
					} else {
						String digit = returnValueType.substring(0, returnValueType.indexOf("λ"));
						rtvalue = new BigDecimal(String.valueOf(obj).trim()).setScale(Integer.parseInt(digit), BigDecimal.ROUND_HALF_UP).doubleValue();
					}
				} else {
					rtvalue = 0;
				}
			}

		} catch (Exception ex) {
			try {
				rtvalue = Float.parseFloat(String.valueOf(rtvalue));
			} catch (Exception ex2) {
				WLTLogger.getLogger(MatchByKey.class).error(rtvalue, ex2);
			}
		}
		return rtvalue;
	}
}
