package cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.JEP;
import org.nfunk.jep.ParseException;

import cn.com.infostrategy.to.common.HashVO;

/**
 * ����һ��HashVO���ֶ��󡣿��Դ���һ�ű�����Ҳ���Դ���������sql��
 * @author haoming create by 2013-6-24
 * 
 *         ÿ�δ���һ��SalaryFomulaParseUtil����ʵ������
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
		Object[] str_pars = new String[curNumberOfParameters]; // ��������б�!Ӧ����һ��,��λ������ż��!
		for (int i = str_pars.length - 1; i >= 0; i--) { // �����ú�������!!
			str_pars[i] = _inStack.pop(); //
		}
		if (str_pars.length == 0) {
			throw new ParseException("�봫�����");
		} else {
			String tableNameOrSql = ""; // ֧�ֱ�������select-Sql
			String condition = "";
			tableNameOrSql = str_pars[0].toString();
			if (str_pars.length > 1 && str_pars[1] != null && !"null".equals(str_pars[1].toString())) {
				condition = str_pars[1].toString();
			}
			StringBuffer selectSql = new StringBuffer();
			if (tableNameOrSql.toLowerCase().contains("select ")) { // �����ѯ������
				selectSql.append(tableNameOrSql);
			} else {// ���������ƴ��Sql
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
				throw new ParseException("����" + selectSql.toString() + "����������");
			}
			cacheMap_sqlData.put(selectSql.toString(), getVos);
			_inStack.push(getVos); // ��������ѣ��η�����ֻ�ܷ���һ��HashVO��

		}
	}
}
