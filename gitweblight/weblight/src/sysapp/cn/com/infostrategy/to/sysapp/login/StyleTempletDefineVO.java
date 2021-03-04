package cn.com.infostrategy.to.sysapp.login;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 某一种风格模板的定义对象.
 * @author xch
 *
 */
public class StyleTempletDefineVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name = null; //名称
	private String descr = null; //描述
	private String defaultClassName = null; //默认实现类的类名
	private ArrayList al_formula = new ArrayList(); //所有公式

	/**
	 * 默认构造方法
	 */
	public StyleTempletDefineVO() {
	}

	/**
	 * 第二种构造方法
	 * @param _name
	 * @param _descr
	 * @param _className
	 */
	public StyleTempletDefineVO(String _name, String _descr, String _className) {
		this.name = _name;
		this.descr = _descr; //
		this.defaultClassName = _className; //
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getDefaultClassName() {
		return defaultClassName;
	}

	public void setDefaultClassName(String defaultClassName) {
		this.defaultClassName = defaultClassName;
	}

	public void putFormula(String _key, String _value) {
		al_formula.add(new String[] { _key, _value }); //
	}

	public void putFormula(String _key, String[] _values) {
		String[] str_fms = new String[_values.length + 1]; //
		str_fms[0] = _key;
		for (int i = 1; i < str_fms.length; i++) {
			str_fms[i] = _values[i - 1]; //
		}
		al_formula.add(str_fms); //
	}

	/**
	 * 增加空行
	 */
	public void putFormulaBlankLine() {
		al_formula.add(new String[] { "" }); //
	}

	/**
	 * 取得实际的公式语法.
	 * @return
	 */
	public String getFormulaDefine() {
		StringBuffer sb_formula = new StringBuffer(); //
		sb_formula.append("\"风格模板类型\",\"" + this.name + "\",\r\n"); //
		for (int i = 0; i < al_formula.size(); i++) {
			String[] str_pars = (String[]) al_formula.get(i); //
			if (str_pars.length == 1) {
				sb_formula.append(str_pars[0] + "\r\n"); //
			} else {
				sb_formula.append("\"" + str_pars[0] + "\",\"" + str_pars[1] + "\",\r\n"); //
			}
		}
		return sb_formula.toString();
	}

}