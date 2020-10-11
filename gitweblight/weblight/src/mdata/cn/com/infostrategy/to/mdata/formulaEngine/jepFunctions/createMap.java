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
 * ���ݴ���ֵ����һ��map��
 * 
 * �˷���Ŀǰ֧������д����
 * һ��������������.createMap(һ��sql����һ����������,"key�ֶ�"��"value�ֶ�")
 * 1����һ������������һ����׼��sqlҲ������[ȫ����Ա����]�����Ķ���������.
 * 2�����������������趨map��key��value��
 * 
 * ����������������.createMap(һ��sql����һ����������,"key�ֶ�")
 * ����д��Ĭ�ϰѲ������HashVO ���� key���浽map��.
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
			Node cnode = node.jjtGetChild(j); //��ȡ���ڵ�
			obj[j] = e.eval(cnode);
		}
		if (index < 2) {
			throw new ParseException("����createMap������������������,�������key");
		}
		HashVO[] vos = null;
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (obj[0] instanceof HashVO[]) {
			vos = (HashVO[]) obj[0];
		} else if (obj[0] instanceof String) {
			try {
				if (String.valueOf(obj[0]).contains("select ")) {//�������sql
					vos = salaryTBUtil.getHashVoArrayByDS(null, String.valueOf(obj[0]));
				} else {
					String tableName = (String) obj[0];
					String keyitem = (String) obj[1];
					String valueitem = (String) obj[2];
					String sql = "select " + keyitem + "," + valueitem + " from " + tableName;
					vos = salaryTBUtil.getHashVoArrayByDS(null, sql);
				}
			} catch (Exception ex) {
				throw new ParseException("createMap����ִ��sql����");
			}
		} else {
			throw new ParseException("����createMap������һ��������Ͳ�֧��");
		}
		String keyitem = (String) obj[1];
		if (obj.length == 2) {//���ֻ������Դ���ݻ���sql��������key����ô˵��Ҫһ���ؼ�ֵ����HashVO����
			for (int i = 0; i < vos.length; i++) {
				map.put(vos[i].getStringValue(keyitem), vos[i]);
			}
		} else if (obj.length == 3) { //���������ǰ�key��value�����ˡ�
			String valueitem = (String) obj[2];
			for (int i = 0; i < vos.length; i++) {
				map.put(vos[i].getStringValue(keyitem), vos[i].getStringValue(valueitem));
			}
		}
		return map;
	}
}
