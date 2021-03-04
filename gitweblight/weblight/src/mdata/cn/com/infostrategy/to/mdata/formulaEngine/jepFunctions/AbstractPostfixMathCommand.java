package cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions;

import org.nfunk.jep.JEP;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.mdata.formulaEngine.SalaryFomulaParseUtil;
import cn.com.infostrategy.to.mdata.formulaEngine.SalaryTBUtil;

/**
 * 
 * @author haoming create by 2013-6-24
 * 
 *         ÿ�δ���һ��SalaryFomulaParseUtil����ʵ������������ʵ����,����ֵʵ����һ�Ρ�
 */
public abstract class AbstractPostfixMathCommand extends PostfixMathCommand {
	public JEP jep; // ��jep��ʽ���룬���Ը���jep��ȡ�趨�ı�����
	public Object wholeObjData; // ȫ�ֱ�����������һ��ʼ����ִ�е�ֵ
	public Object inputData; // ������Դ����
	public Object conditionData;// ������������
	public SalaryFomulaParseUtil salaryParseUtil;
	public StringBuffer rtsb;
	public SalaryTBUtil salaryTBUtil = new SalaryTBUtil();

	public Object getInputData() {
		return inputData;
	}

	public void setInputData(Object inputData) {
		this.inputData = inputData;
	}

	public Object getConditionData() {
		return conditionData;
	}

	public void setConditionData(Object conditionData) {
		this.conditionData = conditionData;
	}

	public AbstractPostfixMathCommand(JEP _jepParse) {
		this(_jepParse, null);
	};

	public AbstractPostfixMathCommand(Object _wholeObjData) {
		this(null, _wholeObjData);
	}

	public AbstractPostfixMathCommand(JEP _jepParse, Object _wholeObjData) {
		jep = _jepParse;
		wholeObjData = _wholeObjData;
		numberOfParameters = -1;
	}

	public AbstractPostfixMathCommand(JEP _jepParse, Object _wholeObjData, SalaryFomulaParseUtil _salaryParseUtil, StringBuffer _rtSb) {
		jep = _jepParse;
		numberOfParameters = -1;
		wholeObjData = _wholeObjData;
		this.salaryParseUtil = _salaryParseUtil;
		rtsb = _rtSb;
	}

	public StringBuffer getRtsb() {
		return rtsb;
	}

	public void setRtsb(StringBuffer rtsb) {
		this.rtsb = rtsb;
	}

}
