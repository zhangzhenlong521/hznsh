package cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions;

import org.nfunk.jep.JEP;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.mdata.formulaEngine.SalaryFomulaParseUtil;
import cn.com.infostrategy.to.mdata.formulaEngine.SalaryTBUtil;

/**
 * 
 * @author haoming create by 2013-6-24
 * 
 *         每次创建一个SalaryFomulaParseUtil都会实例化他的所有实现类,而且值实例化一次。
 */
public abstract class AbstractPostfixMathCommand extends PostfixMathCommand {
	public JEP jep; // 把jep公式传入，可以根据jep获取设定的变量。
	public Object wholeObjData; // 全局变量，是数据一开始传入执行的值
	public Object inputData; // 数据来源对象
	public Object conditionData;// 数据条件对象
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
