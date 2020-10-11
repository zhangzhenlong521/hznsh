package cn.com.infostrategy.to.sysapp.login;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ĳһ�ַ��ģ��Ķ������.
 * @author xch
 *
 */
public class StyleTempletDefineVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name = null; //����
	private String descr = null; //����
	private String defaultClassName = null; //Ĭ��ʵ���������
	private ArrayList al_formula = new ArrayList(); //���й�ʽ

	/**
	 * Ĭ�Ϲ��췽��
	 */
	public StyleTempletDefineVO() {
	}

	/**
	 * �ڶ��ֹ��췽��
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
	 * ���ӿ���
	 */
	public void putFormulaBlankLine() {
		al_formula.add(new String[] { "" }); //
	}

	/**
	 * ȡ��ʵ�ʵĹ�ʽ�﷨.
	 * @return
	 */
	public String getFormulaDefine() {
		StringBuffer sb_formula = new StringBuffer(); //
		sb_formula.append("\"���ģ������\",\"" + this.name + "\",\r\n"); //
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