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
 * �����û�
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
	 * ������Ա
	 * @param _parent
	 * @param _postid
	 */
	public AddUserDialog(Container _parent, String _deptid, String _postid) {
		this(_parent, _deptid, _postid, null);
	}

	public AddUserDialog(Container _parent, String _deptid, String _postid, HashMap _menuMap) {
		super(_parent, "������Ա", "PUB_USER_CODE1", 630, 380, WLTConstants.BILLDATAEDITSTATE_INSERT); //���ô��ڴ�С�����޸���Ա���ڴ�Сһ�¡����/2012-04-18��
		this.str_deptid = _deptid;
		this.str_postid = _postid; //
		this.menuMap = _menuMap;
		if (menuMap != null && menuMap.containsKey("���")) {
			pwdwidth = (String) menuMap.get("���");
		} else {
			pwdwidth = new TBUtil().getSysOptionStringValue("��ʼ����Ա������", null);
		}
		if (pwdwidth != null && !pwdwidth.equals("") && !pwdwidth.equals("0")) {
			this.getCardPanel().getCompentByKey("PWD").getLabel().setText("*����(" + pwdwidth + ")λ");
		}
		String deptname = "";
		try {
			deptname = UIUtil.getStringValueByDS(null, "select NAME from pub_corp_dept where id='" + _deptid + "' ");//��ǰ�õ�DISPATCHNAME �ֶΣ����ɻ󣬺ܶ���Ŀû����������ֶΣ��ʸ�ΪNAME�����/2014-02-24��
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.getCardPanel().setValueAt("PK_DEPT", new RefItemVO(_deptid, "", deptname));//�ʴ���������ʱû�л�������
		btn_save.setVisible(false); //
	}

	/*
	 * �ڳ�ʼ���û�ʱ�� ���������볤��У�顣̫ƽ�����
	 */
	private boolean checkPwd() {
		String sr_newpwd = this.getCardPanel().getBillVO().getStringValue("PWD");
		if (pwdwidth != null && pwdwidth.contains("-")) {
			String[] str_pwdlength = pwdwidth.split("-");
			if (sr_newpwd.length() < Integer.parseInt(str_pwdlength[0]) || sr_newpwd.length() > Integer.parseInt(str_pwdlength[1])) { //10-16λ֮��
				MessageBox.show(this, "�����볤�ȱ�����" + pwdwidth + "λ֮��,���޸�!"); //
				return false;
			}

		} else if (pwdwidth != null && sr_newpwd.length() != Integer.parseInt(pwdwidth) && !pwdwidth.equals("0")) {
			MessageBox.show(this, "�����볤�ȱ���Ϊ" + pwdwidth + "λ,���޸�!"); //
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
			String str_usercode = billcardPanel.getRealValueAt("code"); //��Ա����,���У��һ�¸���Ա����
			if (pwdwidth != null) {
				if (!checkPwd()) {
					return;
				}
			}
			//У�����Ա�Ƿ��Ѵ���!!!
			HashVO[] hvos = UIUtil.getHashVoArrayByDS(null, "select count(code) c1 from pub_user where code='" + str_usercode + "'"); //
			int li_count = hvos[0].getIntegerValue("c1").intValue(); //
			if (li_count > 0) {
				MessageBox.show(null, "ϵͳ���Ѿ�����һ������С�" + str_usercode + "�����û�,�����ظ�¼��,��ʹ�õ���������䵼�뱾����!"); //
				return;
			}

			//У�����Ա�ڸû���,�ø�λ�Ƿ����!!
			String str_insertsql = billcardPanel.getUpdateDataSQL(); //
			//���Ӷ�pub_user pk_dept ��ͬ������ Gwang 2012-4-17
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
