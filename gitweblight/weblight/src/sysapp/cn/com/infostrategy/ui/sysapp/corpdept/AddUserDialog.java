package cn.com.infostrategy.ui.sysapp.corpdept;

import java.awt.Container;
import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardDialog;

/**
 * 增加用户
 * @author xch
 *
 */
public class AddUserDialog extends BillCardDialog {

	private String str_deptid = null;
	private String str_postid = null;

	private String returnNewID = null; //

	private HashMap menuMap = null;

	private String pwdwidth = null;

	/**
	 * 新增人员
	 * @param _parent
	 * @param _postid
	 */
	public AddUserDialog(Container _parent, String _deptid, String _postid) {
		this(_parent, _deptid, _postid, null);
	}

	public AddUserDialog(Container _parent, String _deptid, String _postid, HashMap _menuMap) {
		super(_parent, "新增人员", "PUB_USER_CODE1", 630, 380, WLTConstants.BILLDATAEDITSTATE_INSERT); //设置窗口大小，与修改人员窗口大小一致【李春娟/2012-04-18】
		this.str_deptid = _deptid;
		this.str_postid = _postid; //
		this.menuMap = _menuMap;
		if (menuMap != null && menuMap.containsKey("宽度")) {
			pwdwidth = (String) menuMap.get("宽度");
		} else {
			pwdwidth = new TBUtil().getSysOptionStringValue("初始化人员密码宽度", null);
		}
		if (pwdwidth != null && !pwdwidth.equals("") && !pwdwidth.equals("0")) {
			this.getCardPanel().getCompentByKey("PWD").getLabel().setText("*密码(" + pwdwidth + ")位");
		}
		String deptname = "";
		try {
			deptname = UIUtil.getStringValueByDS(null, "select NAME from pub_corp_dept where id='" + _deptid + "' ");//以前用的DISPATCHNAME 字段，很疑惑，很多项目没有设置这个字段，故改为NAME【李春娟/2014-02-24】
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.getCardPanel().setValueAt("PK_DEPT", new RefItemVO(_deptid, "", deptname));//邮储纠结新增时没有机构名称
		btn_save.setVisible(false); //
	}

	/*
	 * 在初始化用户时候 可以做密码长度校验。太平提出！
	 */
	private boolean checkPwd() {
		String sr_newpwd = this.getCardPanel().getBillVO().getStringValue("PWD");
		if (pwdwidth != null && pwdwidth.contains("-")) {
			String[] str_pwdlength = pwdwidth.split("-");
			if (sr_newpwd.length() < Integer.parseInt(str_pwdlength[0]) || sr_newpwd.length() > Integer.parseInt(str_pwdlength[1])) { //10-16位之间
				MessageBox.show(this, "新密码长度必须在" + pwdwidth + "位之间,请修改!"); //
				return false;
			}

		} else if (pwdwidth != null && sr_newpwd.length() != Integer.parseInt(pwdwidth) && !pwdwidth.equals("0")) {
			MessageBox.show(this, "新密码长度必须为" + pwdwidth + "位,请修改!"); //
			return false;
		}
		return true;
	}

	public void onConfirm() {
		try {
			if (!billcardPanel.checkValidate()) {
				return;
			}

			String str_userid = billcardPanel.getRealValueAt("id"); //
			String str_usercode = billcardPanel.getRealValueAt("code"); //人员编码,最好校验一下该人员编码
			if (pwdwidth != null) {
				if (!checkPwd()) {
					return;
				}
			}
			//校验该人员是否已存在!!!
			HashVO[] hvos = UIUtil.getHashVoArrayByDS(null, "select count(code) c1 from pub_user where code='" + str_usercode + "'"); //
			int li_count = hvos[0].getIntegerValue("c1").intValue(); //
			if (li_count > 0) {
				MessageBox.show(null, "系统中已经存在一个编码叫【" + str_usercode + "】的用户,不能重复录入,请使用导入操作将其导入本机构!"); //
				return;
			}

			//校验该人员在该机构,该岗位是否存在!!
			String str_insertsql = billcardPanel.getUpdateDataSQL(); //
			//增加对pub_user pk_dept 的同步处理 Gwang 2012-4-17
			if(this.getCardPanel().getRealValueAt("PK_DEPT") != null && !"".equals(this.getCardPanel().getRealValueAt("PK_DEPT"))) {
				str_deptid = this.getCardPanel().getRealValueAt("PK_DEPT");
			}
			String sql = "update pub_user set pk_dept = " + str_deptid + " where id = " + str_userid;
			String str_newpk = UIUtil.getSequenceNextValByDS(null, "S_PUB_USER_POST"); //
			UIUtil.executeBatchByDS(null, new String[] { str_insertsql, sql, getSQL_User_Post(str_newpk, str_userid, str_deptid, str_postid) }); ////
			returnNewID = str_newpk;
			this.setCloseType(1);
			this.dispose(); //
		} catch (Exception e) {
			MessageBox.showException(this, e);
		} //
	}

	private String getSQL_User_Post(String _newPK, String _userid, String _deptid, String _postid) throws Exception {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("insert into pub_user_post");
		sb_sql.append("(");
		sb_sql.append("id,"); //id  (id)
		sb_sql.append("userid,"); //userid  (userid)
		sb_sql.append("postid,"); //postid  (postid)
		sb_sql.append("userdept,"); //userdept  (userdept)
		sb_sql.append("isdefault"); //isdefault  (isdefault)
		sb_sql.append(")");
		sb_sql.append(" values ");
		sb_sql.append("(");
		sb_sql.append(_newPK + ","); //id  (id)
		sb_sql.append("'" + _userid + "',"); //userid  (userid)
		sb_sql.append(_postid == null ? "null," : ("'" + _postid + "',")); //postid  (postid)
		sb_sql.append("'" + _deptid + "',"); //userdept  (userdept)
		sb_sql.append("'Y'"); //isdefault  (isdefault)
		sb_sql.append(")"); //
		return sb_sql.toString(); //
	}

	public String getReturnNewID() {
		return returnNewID; //
	}
}
