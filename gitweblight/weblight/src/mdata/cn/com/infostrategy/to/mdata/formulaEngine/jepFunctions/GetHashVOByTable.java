package cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;

import cn.com.infostrategy.to.common.HashVO;

/**
 * 创建一个HashVO数字对象。可以传入一张表明，也可以传入完整的sql。
 * @author haoming create by 2013-6-24
 * 
 *         每次创建一个SalaryFomulaParseUtil都会实例化。
 */
public class GetHashVOByTable extends AbstractPostfixMathCommand {

	private HashMap cacheMap_sqlData = new HashMap();

	public GetHashVOByTable() {
		this(null, null);
	}

	public GetHashVOByTable(JEP _jep, Object _wholeInputData) {
		super(_jep, _wholeInputData);
	}

	@Override
	public void run(Stack _inStack) throws ParseException {
		checkStack(_inStack); //
		Object[] str_pars = new String[curNumberOfParameters]; // 构造参数列表!应该是一对,即位数和是偶数!
		for (int i = str_pars.length - 1; i >= 0; i--) { // 倒叙获得函数参数!!
			str_pars[i] = _inStack.pop(); //
		}
		if (str_pars.length == 0) {
			throw new ParseException("请传入表名");
		} else {
			String tableNameOrSql = ""; // 支持表名或者select-Sql
			String condition = "";
			tableNameOrSql = str_pars[0].toString();
			if (str_pars.length > 1 && str_pars[1] != null && !"null".equals(str_pars[1].toString())) {
				condition = str_pars[1].toString();
			}
			StringBuffer selectSql = new StringBuffer();
			if (tableNameOrSql.toLowerCase().contains("select ")) { // 传入查询条件。
				selectSql.append(tableNameOrSql);
			} else {// 传入表名，拼接Sql
				selectSql.append("select * from " + tableNameOrSql + "");
				if (condition != null && !condition.equals("")) {
					selectSql.append(" where " + condition);
				}
			}
			if (cacheMap_sqlData.containsKey(selectSql.toString())) {
				_inStack.push((HashVO[]) cacheMap_sqlData.get(selectSql.toString()));
				return;
			}
			HashVO[] getVos = null;
			try {
				getVos = salaryTBUtil.getHashVoArrayByDS(null, selectSql.toString());
			} catch (Exception e) {
				e.printStackTrace();
				throw new ParseException("◆◆" + selectSql.toString() + "语句有误◆◆");
			}
			cacheMap_sqlData.put(selectSql.toString(), getVos);
			_inStack.push(getVos); // 这儿定死把，次方法就只能返回一个HashVO。

		}
	}
}
