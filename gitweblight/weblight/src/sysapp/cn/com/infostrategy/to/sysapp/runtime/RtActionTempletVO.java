package cn.com.infostrategy.to.sysapp.runtime;

import java.io.Serializable;

//动态脚本执行的模板定义
public class RtActionTempletVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String templetName = null; //模板名称,也同时是父类的名称!!!
	private String superClassType = null; //extends/implements
	private String packageSubfix = null; //包的后缀,即在cn.com.weblight.runtime.后面再加上,一般就两种: ui/bs
	private String classPrefix = null; //类名的前辍,关键,用来区分类别,比如BSFilter!!!!
	private String[] imported = null; //导入的类名!!!
	private String functiondoc = null; //函数的说明!!
	private String functionName = null; //函数开头!!
	private String[] functionContent = null; //函数的内容!! 

	public String getTempletName() {
		return templetName;
	}

	public void setTempletName(String templetName) {
		this.templetName = templetName;
	}

	public String getClassPrefix() {
		return classPrefix;
	}

	public void setClassPrefix(String classPrefix) {
		this.classPrefix = classPrefix;
	}

	public String[] getImported() {
		return imported;
	}

	public void setImported(String[] imported) {
		this.imported = imported;
	}

	public String getFunctiondoc() {
		return functiondoc;
	}

	public void setFunctiondoc(String functiondoc) {
		this.functiondoc = functiondoc;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String _functionName) {
		this.functionName = _functionName;
	}

	public String[] getFunctionContent() {
		return functionContent;
	}

	public void setFunctionContent(String[] functionContent) {
		this.functionContent = functionContent;
	}

	public String getSuperClassType() {
		return superClassType;
	}

	public void setSuperClassType(String superClassType) {
		this.superClassType = superClassType;
	}

	public String getPackageSubfix() {
		return packageSubfix;
	}

	public void setPackageSubfix(String packageSubfix) {
		this.packageSubfix = packageSubfix;
	}

}
