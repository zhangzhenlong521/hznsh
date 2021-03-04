package cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions;

import org.nfunk.jep.EvaluatorI;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.CallbackEvaluationI;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTLogger;

/*
 * 1���ù�ʽ����ֱ�Ӷ�HashVO�����е�ĳһ���ֶε�ֵ������ƽ��
 * 2��ֱ�Ӵ������֣���ƽ������double����
 *  ��ƽ����
 */
public class Avg extends AbstractPostfixMathCommand implements CallbackEvaluationI {

	public Avg(JEP jepParse) {
		super(jepParse);
	}

	public Object evaluate(Node node, EvaluatorI evaluatori) throws ParseException {
		int index = node.jjtGetNumChildren();
		Object obj[] = new Object[index];
		for (int j = 0; j < index; j++) {
			Node cnode = node.jjtGetChild(j); //��ȡ���ڵ�
			obj[j] = evaluatori.eval(cnode);
		}
		double f = 0;
		if (obj.length > 0 && obj[0] instanceof HashVO[]) {
			if (obj.length != 2) {
				throw new ParseException("��avg��ʽ��HashVO������м��㣬��Ҫ����������飬�����ֶε���������");
			}
			String vagkey = (String) obj[1];
			HashVO vos[] = (HashVO[]) obj[0];
			for (int i = 0; i < vos.length; i++) {
				f += vos[i].getDoubleValue(vagkey, 0d);
			}
			if (vos.length == 0) {
				return 0;
			} else {
				f = f / vos.length;
			}
		} else {

			for (int i = 0; i < obj.length; i++) {
				try {
					f += Double.parseDouble(obj[i] + "");
				} catch (Exception ex) {
					WLTLogger.getLogger(Avg.class).error("����ֵת����Float����" + Avg.class.getName(), ex);
				}
			}
			if (obj.length == 0) {
				f = 0;
			} else {
				f = f / obj.length;
			}
		}

		return f;

	}

}
