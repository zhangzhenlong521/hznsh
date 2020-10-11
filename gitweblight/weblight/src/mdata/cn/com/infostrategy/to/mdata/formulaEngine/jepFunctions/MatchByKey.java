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
 * 简单匹配功能,主要是根据一个key,value 串儿,然后匹配传入的值,返回相同key对应的value.仅支持字符串
 * 在对象因子配置如：匹配("匹配入参",[人员信息.学历],"匹配源",[学历集合],"匹配字段","name","返回字段","返回值类型","").
 * 
 * 1、匹配入参的值基本字符串
 * 2、匹配源可以值HashVO数组，也可以是Map.
 * 3、如果是Map就不需要后面的匹配字段和返回字段;如果是HashVO[]需要制定匹配和返回哪个字段。
 * 4、 返回类型一般是必须返回数字时用到，如果在其他公式中包含匹配公式，可能会涉及到返回值类型.仅是一个因子直接定义匹配，有主属性定义过类型.
 * 
 * 
 * 实现CallbackEvaluationI的接口可以从node中获取内容.更方便。
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
			Node cnode = node.jjtGetChild(j); //获取到节点
			obj[j] = evaluatori.eval(cnode);
		}
		HashMap<Object, Object> allconditioninfo = new HashMap<Object, Object>(); //所有的配置信息
		for (int i = 0; i < obj.length; i++) {
			Object str_key = obj[i]; //因为第一个是类型,所以要加1
			Object str_value = obj[i + 1]; //因为第一个是类型,并且是第二个,所以要加2
			allconditioninfo.put(str_key, str_value);
			i++;
		}
		String keyvalue = String.valueOf(allconditioninfo.get("匹配入参"));//必须传入
		String keyitem = (String) allconditioninfo.get("匹配字段"); //默认是0位置
		Object compare = allconditioninfo.get("匹配源");
		String returnitem = (String) allconditioninfo.get("返回字段");
		String returnValueType = (String) allconditioninfo.get("返回值类型");
		String currValue = keyvalue;
		Object compareValue = compare;
		if (compareValue == null) { //如果就1一个值说明是在输入条件中传入的集合对象。
			compareValue = getConditionData();
			if (compareValue == null) {
				compareValue = getInputData();
			}
		}
		Object rtvalue = null;
		if (compareValue instanceof String && compareValue.toString().contains("=")) { // 说明是例如:par1=你好,par2=我看行
			HashMap map = tbutil.convertStrToMapByExpress(compareValue.toString());
			rtvalue = map.get(currValue); // 判断后得到的值
			if (rtvalue == null) {
				if (childCount == 3) { //有默认值
					rtvalue = obj[2];
				}
			}
		} else if (compareValue instanceof HashVO[]) { // 传入一个两个字段的hashvo数组
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
			if (returnValueType != null && !"字符串".equals(returnValueType)) {
				if (rtvalue != null && !"".equals(rtvalue)) {
					if ("整数".equals(returnValueType.trim())) {
						BigDecimal big = new BigDecimal(String.valueOf(obj).trim()).setScale(0, BigDecimal.ROUND_HALF_UP);
						rtvalue = big.intValue();
					} else {
						String digit = returnValueType.substring(0, returnValueType.indexOf("位"));
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
