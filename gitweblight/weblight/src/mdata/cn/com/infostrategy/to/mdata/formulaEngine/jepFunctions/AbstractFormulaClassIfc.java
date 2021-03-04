package cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.formulaEngine.SalaryFomulaParseUtil;

/**
 * 代码执行一个固定类.
 * @author haoming
 * create by 2013-9-16
 */
public interface AbstractFormulaClassIfc {
	/**
	 * 
	 * @param _factorVO 目前没有用到。可以传入null
	 * @param _baseHashVO 传入的基本对象，很重要。
	 * @param _parseUtil 传入的parseutil。
	 * @param _actionDescr 执行的描述情况
	 * @return 真正执行完的值
	 * @throws Exception
	 * 此方法虽然后返回值，但是目前该返回值没有放到数据库中，需要代码中自己写update语句去执行。
	 * 返回值在演算的时候用到。
	 */
	public void onExecute(HashVO _factorVO, HashVO _baseHashVO, SalaryFomulaParseUtil _parseUtil) throws Exception;
}
