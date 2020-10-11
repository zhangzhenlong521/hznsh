package cn.com.infostrategy.to.mdata;

import java.io.Serializable;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * 按钮定义对象!!!非常重要，系统的功能权限今天基本都是通过控制按钮的是否显示来实现的!
 * @author xch
 *
 */
public class ButtonDefineVO implements Serializable {

	private static final long serialVersionUID = -8303469708727598530L;

	private String code = null; //编码,唯一性
	private String btntype = null; //按钮类型!!!关键,以后权限配置可以通过类型来设置
	private String btntext = null; //按钮文字
	private String btntooltiptext = null; //按钮提示
	private String btnpars = null; //按钮参数...

	private String clickingformula = null; //点击前的公式
	private String clickedformula = null; //点击后的公式
	private String allowposts = null; //允许的岗位编码

	private String allowroles = null; //允许的角色编码
	private String allowroletype = null; //允许的角色类型,有白名单与黑名单之分!!

	private String allowusers = null; //允许的人员主键
	private String allowusertype = null; //允许的人员类型,有白名单与黑名单之分!!

	private String allowifcbyinit = null; //初始化时的校验器,在BS端执行的!!
	private String allowifcbyclick = null; //选择变化或点击时的校验器,在UI端执行的!!

	private boolean isAllowed = true; //是否允许,默认是true
	private String allowResult = ""; //权限计算的结果
	private String btnimg = null; //按钮图片名
	private String btndescr = null; //按钮备注说明

	private boolean isRegisterBtn = false; //是否是注册的按钮,即在数据库中存在的,默认是false

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

	//增加新的结果!!!
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
	 * 是否是注册按钮
	 * @return
	 */
	public boolean isRegisterBtn() {
		return isRegisterBtn;
	}

	/**
	 * 设置是否是注册按钮
	 * @param isRegisterBtn
	 */
	public void setRegisterBtn(boolean isRegisterBtn) {
		this.isRegisterBtn = isRegisterBtn;
	}

	/**
	 * 取得所有允许岗位编码
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
	 * 取得所有允许角色的编码
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
	 * 取得所有允许人员的主键
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
		return "按钮定义说明:code=[" + this.code + "],btntext=[" + this.btntext + "],btntype=[" + this.btntype + "],allowposts=[" + this.allowposts + "],allowroles=[" + this.allowroles + "],allowusers=[" + this.allowusers + "]";
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
