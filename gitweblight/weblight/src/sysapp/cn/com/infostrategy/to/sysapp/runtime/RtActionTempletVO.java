package cn.com.infostrategy.to.sysapp.runtime;

import java.io.Serializable;

//��̬�ű�ִ�е�ģ�嶨��
public class RtActionTempletVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String templetName = null; //ģ������,Ҳͬʱ�Ǹ��������!!!
	private String superClassType = null; //extends/implements
	private String packageSubfix = null; //���ĺ�׺,����cn.com.weblight.runtime.�����ټ���,һ�������: ui/bs
	private String classPrefix = null; //������ǰ�,�ؼ�,�����������,����BSFilter!!!!
	private String[] imported = null; //���������!!!
	private String functiondoc = null; //������˵��!!
	private String functionName = null; //������ͷ!!
	private String[] functionContent = null; //����������!! 

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
