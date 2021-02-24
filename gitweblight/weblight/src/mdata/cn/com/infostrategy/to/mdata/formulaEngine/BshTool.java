package cn.com.infostrategy.to.mdata.formulaEngine;

import java.util.Hashtable;

import bsh.Interpreter;
import cn.com.infostrategy.to.common.WLTLogger;

public class BshTool {

	/**
	 * �̶������Ϊ:inputParam
	 * ������Ϊ:outputParam
	 * @param formula
	 * @param inputParamValue
	 * @return
	 */
	public Object getValueByBshFormula(String formula, Object inputParamValue) throws Exception {
		Hashtable inputParam = new Hashtable();
		if (inputParamValue != null) {
			inputParam.put("inputParam", inputParamValue);
		}
		return getValueByBshFormula(formula, inputParam, "outputParam");
	}

	/**
	 * ���Զ����������
	 * ��������
	 * @param formula
	 * @param inputParam
	 * @param outputParamName
	 * @return
	 */
	public Object getValueByBshFormula(String formula, Hashtable inputParam, String outputParamName) throws Exception {
		try {
			Interpreter parm = new Interpreter();
			if (inputParam != null && inputParam.size() > 0) {
				String[] inputparanName = (String[]) inputParam.keySet().toArray(new String[0]);
				for (int i = 0; i < inputparanName.length; i++) {
					parm.set(inputparanName[i], inputParam.get(inputparanName[i]));
				}
			}
			Object a = parm.eval(formula);
			if (outputParamName != null && !"".equals(outputParamName.trim())) {
				return parm.get(outputParamName);
			} else {
				return a;
			}
		} catch (Exception e) {
			WLTLogger.getLogger(BshTool.class).error("bsh��ʽִ�г��ִ���", e);
			throw e;
		}
	}

}
