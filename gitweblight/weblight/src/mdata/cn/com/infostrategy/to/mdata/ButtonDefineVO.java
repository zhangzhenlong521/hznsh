package cn.com.infostrategy.to.mdata;

import java.io.Serializable;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * ��ť�������!!!�ǳ���Ҫ��ϵͳ�Ĺ���Ȩ�޽����������ͨ�����ư�ť���Ƿ���ʾ��ʵ�ֵ�!
 * @author xch
 *
 */
public class ButtonDefineVO implements Serializable {

	private static final long serialVersionUID = -8303469708727598530L;

	private String code = null; //����,Ψһ��
	private String btntype = null; //��ť����!!!�ؼ�,�Ժ�Ȩ�����ÿ���ͨ������������
	private String btntext = null; //��ť����
	private String btntooltiptext = null; //��ť��ʾ
	private String btnpars = null; //��ť����...

	private String clickingformula = null; //���ǰ�Ĺ�ʽ
	private String clickedformula = null; //�����Ĺ�ʽ
	private String allowposts = null; //����ĸ�λ����

	private String allowroles = null; //����Ľ�ɫ����
	private String allowroletype = null; //����Ľ�ɫ����,�а������������֮��!!

	private String allowusers = null; //�������Ա����
	private String allowusertype = null; //�������Ա����,�а������������֮��!!

	private String allowifcbyinit = null; //��ʼ��ʱ��У����,��BS��ִ�е�!!
	private String allowifcbyclick = null; //ѡ��仯����ʱ��У����,��UI��ִ�е�!!

	private boolean isAllowed = true; //�Ƿ�����,Ĭ����true
	private String allowResult = ""; //Ȩ�޼���Ľ��
	private String btnimg = null; //��ťͼƬ��
	private String btndescr = null; //��ť��ע˵��

	private boolean isRegisterBtn = false; //�Ƿ���ע��İ�ť,�������ݿ��д��ڵ�,Ĭ����false

	private ButtonDefineVO() {
	}

	public ButtonDefineVO(String _code) {
		this.code = _code; //
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getBtntype() {
		return btntype;
	}

	public void setBtntype(String btntype) {
		this.btntype = btntype;
	}

	public String getBtntext() {
		return btntext;
	}

	public void setBtntext(String btntext) {
		this.btntext = btntext;
	}

	public String getBtntooltiptext() {
		return btntooltiptext;
	}

	public void setBtntooltiptext(String btntooltiptext) {
		this.btntooltiptext = btntooltiptext;
	}

	public String getBtndescr() {
		return btndescr;
	}

	public void setBtndescr(String btndescr) {
		this.btndescr = btndescr;
	}

	public String getClickingformula() {
		return clickingformula;
	}

	public String[] getClickingformulaArray() {
		if (clickingformula == null) {
			return null;
		}
		return TBUtil.getTBUtil().split1(clickingformula, ";"); //
	}

	public void setClickingformula(String clickingformula) {
		this.clickingformula = clickingformula;
	}

	public String getClickedformula() {
		return clickedformula;
	}

	public String[] getClickedformulaArray() {
		if (clickedformula == null) {
			return null;
		}
		return TBUtil.getTBUtil().split1(clickedformula, ";"); //
	}

	public void setClickedformula(String clickedformula) {
		this.clickedformula = clickedformula;
	}

	public String getAllowposts() {
		return allowposts;
	}

	public void setAllowposts(String allowposts) {
		this.allowposts = allowposts;
	}

	public String getAllowroles() {
		return allowroles;
	}

	public void setAllowroles(String allowroles) {
		this.allowroles = allowroles;
	}

	public String getAllowusers() {
		return allowusers;
	}

	public void setAllowusers(String allowusers) {
		this.allowusers = allowusers;
	}

	public String getAllowResult() {
		return allowResult;
	}

	public void setAllowResult(String allowResult) {
		this.allowResult = allowResult;
	}

	//�����µĽ��!!!
	public void appendAllowResult(String _result) {
		this.allowResult = this.allowResult + _result; //
	}

	public String getBtnimg() {
		return btnimg;
	}

	public void setBtnimg(String btnimg) {
		this.btnimg = btnimg;
	}

	/**
	 * �Ƿ���ע�ᰴť
	 * @return
	 */
	public boolean isRegisterBtn() {
		return isRegisterBtn;
	}

	/**
	 * �����Ƿ���ע�ᰴť
	 * @param isRegisterBtn
	 */
	public void setRegisterBtn(boolean isRegisterBtn) {
		this.isRegisterBtn = isRegisterBtn;
	}

	/**
	 * ȡ�����������λ����
	 * @return
	 */
	public String[] getAllowPostArrays() {
		if (allowposts == null) {
			return null;
		}
		String[] str_iitems = allowposts.split(";");
		return str_iitems; //
	}

	/**
	 * ȡ�����������ɫ�ı���
	 * @return
	 */
	public String[] getAllowRoleArrays() {
		if (allowroles == null) {
			return null;
		}
		String[] str_iitems = allowroles.split(";");
		return str_iitems; //
	}

	/**
	 * ȡ������������Ա������
	 * @return
	 */
	public String[] getAllowUserArrays() {
		if (allowusers == null) {
			return null;
		}
		String[] str_iitems = allowusers.split(";");
		return str_iitems; //
	}

	@Override
	public String toString() {
		return "��ť����˵��:code=[" + this.code + "],btntext=[" + this.btntext + "],btntype=[" + this.btntype + "],allowposts=[" + this.allowposts + "],allowroles=[" + this.allowroles + "],allowusers=[" + this.allowusers + "]";
	}

	public String getAllowifcbyinit() {
		return allowifcbyinit;
	}

	public void setAllowifcbyinit(String _allowifcbyinit) {
		this.allowifcbyinit = _allowifcbyinit;
	}

	public String getAllowifcbyclick() {
		return allowifcbyclick;
	}

	public void setAllowifcbyclick(String _allowifcbyclick) {
		this.allowifcbyclick = _allowifcbyclick;
	}

	public String getAllowroletype() {
		return allowroletype;
	}

	public void setAllowroletype(String allowroletype) {
		this.allowroletype = allowroletype;
	}

	public String getAllowusertype() {
		return allowusertype;
	}

	public void setAllowusertype(String allowusertype) {
		this.allowusertype = allowusertype;
	}

	public boolean isAllowed() {
		return isAllowed;
	}

	public void setAllowed(boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public String getBtnpars() {
		return btnpars;
	}

	public void setBtnpars(String btnpars) {
		this.btnpars = btnpars;
	}

}
