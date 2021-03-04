package cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions;

import java.util.HashMap;

import org.nfunk.jep.EvaluatorI;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.CallbackEvaluationI;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.formulaEngine.SalaryFomulaParseUtil;

/**
 * 根据传入值构造一个map。
 * 
 * 此方法目前支持两种写法。
 * 一：传入三个参数.createMap(一条sql或者一个对象因子,"key字段"，"value字段")
 * 1、第一个参数可以是一条标准的sql也可以是[全部人员集合]这样的对象型因子.
 * 2、后两个参数用来设定map的key和value。
 * 
 * 二：传入两个参数.createMap(一条sql或者一个对象因子,"key字段")
 * 该种写法默认把查出来的HashVO 按照 key缓存到map中.
 * @author haoming
 * create by 2013-8-20
 */
public class createMap extends AbstractPostfixMathCommand implements CallbackEvaluationI {
	public createMap(JEP jepParse, Object wholeObjData, SalaryFomulaParseUtil salaryParseUtil, StringBuffer rtSb) {
		super(jepParse, wholeObjData, salaryParseUtil, rtSb);
	}

	public Object evaluate(Node node, EvaluatorI e) throws ParseException {
		int index = node.jjtGetNumChildren();
		Object obj[] = new Object[index];
		for (int j = 0; j < index; j++) {
			Node cnode = node.jjtGetChild(j); //获取到节点
			obj[j] = e.eval(cnode);
		}
		if (index < 2) {
			throw new ParseException("调用createMap函数必须有三个参数,传入对象，key");
		}
		HashVO[] vos = null;
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (obj[0] instanceof HashVO[]) {
			vos = (HashVO[]) obj[0];
		} else if (obj[0] instanceof String) {
			try {
				if (String.valueOf(obj[0]).contains("select ")) {//传入的是sql
					vos = salaryTBUtil.getHashVoArrayByDS(null, String.valueOf(obj[0]));
				} else {
					String tableName = (String) obj[0];
					String keyitem = (String) obj[1];
					String valueitem = (String) obj[2];
					String sql = "select " + keyitem + "," + valueitem + " from " + tableName;
					vos = salaryTBUtil.getHashVoArrayByDS(null, sql);
				}
			} catch (Exception ex) {
				throw new ParseException("createMap函数执行sql出错");
			}
		} else {
			throw new ParseException("调用createMap函数第一个入参类型不支持");
		}
		String keyitem = (String) obj[1];
		if (obj.length == 2) {//如果只传入了源数据或者sql。传入了key。那么说明要一个关键值缓存HashVO对象
			for (int i = 0; i < vos.length; i++) {
				map.put(vos[i].getStringValue(keyitem), vos[i]);
			}
		} else if (obj.length == 3) { //三个参数是把key和value缓存了。
			String valueitem = (String) obj[2];
			for (int i = 0; i < vos.length; i++) {
				map.put(vos[i].getStringValue(keyitem), vos[i].getStringValue(valueitem));
			}
		}
		return map;
	}
}
