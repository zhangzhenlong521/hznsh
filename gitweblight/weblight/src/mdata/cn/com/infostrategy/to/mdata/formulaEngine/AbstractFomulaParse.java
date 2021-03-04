package cn.com.infostrategy.to.mdata.formulaEngine;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/*
 * 所有类型解析工具必须继承改抽象类。解析有系统对象、文本、数字、Excel、代码等...
 */
public abstract class AbstractFomulaParse {
	protected TBUtil tbutil = new TBUtil();
	public SalaryTBUtil salaryTBUtil = new SalaryTBUtil(); //工具内部的查询数据库已经做了客户端服务器端判断.
	
	/**
	 *  
	 * @param util parseUtil里有很多缓存数据，必须传入.
	 * @param _factorHashVO 因子对象
	 * @param _baseDataHashVO 传入的 基础数据
	 * @param _level 执行层级
	 * @param rtStr 拼接执行过程内容
	 * @return
	 * @throws Exception
	 */
	public abstract Object parse(SalaryFomulaParseUtil util, HashVO _factorHashVO, HashVO _baseDataHashVO, int _level, StringBuffer rtStr) throws Exception;

}
