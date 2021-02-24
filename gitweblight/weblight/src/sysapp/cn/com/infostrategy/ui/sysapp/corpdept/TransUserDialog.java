package cn.com.infostrategy.ui.sysapp.corpdept;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * 迁移用户从一个机构到另一个机构!!
 * @author xch
 *
 */
public class TransUserDialog extends BillDialog implements BillTreeSelectListener, ActionListener {

	private static final long serialVersionUID = 4906171141417382771L; //

	private BillTreePanel billTreePanel_dept; //机构树
	private BillListPanel billListPanel_post; //岗位表

	protected WLTButton btn_confirm, btn_cancel;

	private int li_granuType = -1; //粒度类型(共3种类型),1-总行,分行,事业部;2-总行,总行部门,事业部,事业部分部,分行(最常用的),支行;3-所有
	private boolean isFilterByLoginUser = false;

	private String str_user_post_id = null; //
	private String str_fromDeptId, str_fromDeptName, str_userid, str_userName; //
	private String str_toDeptId, str_toDeptName; //

	private int closeType = -1; //

	/**
	 * 将一个用户迁移到另一个机构
	 * @param _parent
	 * @param _userid
	 */
	public TransUserDialog(Container _parent, String _user_post_id, String _fromdeptid, String _fromDeptName, String _userid, String _userName, int _granuTyp, boolean _isFilter) {
		super(_parent, "选择机构/岗位", 800, 500); //
		this.li_granuType = _granuTyp;
		this.isFilterByLoginUser = _isFilter; //
		this.str_user_post_id = _user_post_id;
		this.str_fromDeptId = _fromdeptid;
		this.str_fromDeptName = _fromDeptName;
		this.str_userid = _userid;
		this.str_userName = _userName; //
		this.initialize();
	}

	private void initialize() {
		billTreePanel_dept = new CorpDeptBillTreePanel(li_granuType, isFilterByLoginUser); //
		billListPanel_post = new BillListPanel("PUB_POST_CODE1"); //
		billListPanel_post.setAllBillListBtnVisiable(false); //隐藏所有按钮

		WLTSplitPane splitPanel = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel_dept, billListPanel_post); //
		splitPanel.setDividerLocation(175); //

		billTreePanel_dept.queryDataByCondition(null); //
		billTreePanel_dept.addBillTreeSelectListener(this); //
		this.getContentPane().setLayout(new BorderLayout()); //

		this.getContentPane().add(splitPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		str_toDeptId = _event.getCurrSelectedVO().getStringValue("id");
		str_toDeptName = _event.getCurrSelectedVO().getStringValue("name"); //机构名称
		billListPanel_post.queryDataByCondition("deptid='" + str_toDeptId + "'", "seq"); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");

		btn_confirm.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		panel.add(btn_confirm);
		panel.add(btn_cancel);
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { //
			onConfirm();
		} else if (e.getSource() == btn_cancel) { //
			onCancel(); //
		}
	}

	private void onConfirm() {
		try {
			if (str_toDeptId == null) {
				MessageBox.show(this, "请选择一个机构!"); //
				return; //
			}

			String str_toPostid = null;
			String str_toPostname = null;
			BillVO billVO_Post = billListPanel_post.getSelectedBillVO(); //
			if (billVO_Post == null) {
				if (JOptionPane.showConfirmDialog(this, "确定要把【" + str_userName + "】从机构【" + str_fromDeptName + "】迁移到:\r\n机构【" + str_toDeptName + "】岗位【未指定】中来吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}
			} else {
				str_toPostid = billVO_Post.getStringValue("id"); //岗位ID
				str_toPostname = billVO_Post.getStringValue("name"); //岗位Name
				if (JOptionPane.showConfirmDialog(this, "确定要把【" + str_userName + "】从机构【" + str_fromDeptName + "】迁移到:\r\n机构【" + str_toDeptName + "】岗位【" + str_toPostname + "】中来吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
					return;
				}
			}

			String str_delSQL = "delete from pub_user_post where id='" + str_user_post_id + "'"; //这里有问题！
			String str_newpk = UIUtil.getSequenceNextValByDS(null, "S_PUB_USER_POST"); //
			UIUtil.executeBatchByDS(null, new String[] { str_delSQL, getSQL_User_Post(str_newpk, str_userid, str_toDeptId, str_toPostid), getSQL_User_Dept(str_userid, str_toDeptId), getSQL_User_role(str_userid, str_toDeptId) }); ////
			closeType = 1; //
			this.dispose(); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void onCancel() {
		closeType = 2; //
		this.dispose(); //
	}
	/**
	 * 
	 * @param str_newpk
	 * @param str_userid
	 * @param str_toDeptId2
	 * @param str_toPostid
	 * @return 记录岗位调动的sql zzl【2017-10-30】
	 */
	private String getSQL_record_Post(String str_newpk, String deptid,
			String deptname, String postid, String postname, String userid,
			String usercode, String username, String date) {
		InsertSQLBuilder isql = new InsertSQLBuilder("hr_recordpost");
		isql.putFieldValue("id", str_newpk);
		isql.putFieldValue("deptid", deptid);
		isql.putFieldValue("deptname", deptname);
		isql.putFieldValue("postid", postid);
		isql.putFieldValue("postname", postname);
		isql.putFieldValue("userid", userid);
		isql.putFieldValue("usercode", usercode);
		isql.putFieldValue("username", username);
		isql.putFieldValue("date", date);
		return isql.toString();
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

	//更新原有的机构属性   gaofeng
	private String getSQL_User_Dept(String _userid, String _deptid) throws Exception {
		StringBuilder sb_sql = new StringBuilder();
		sb_sql.append("update pub_user set pk_dept=");
		sb_sql.append(_deptid);
		sb_sql.append(" where id=");
		sb_sql.append(_userid);
		return sb_sql.toString();
	}

	//更新pub_user_role 中机构 lcj
	private String getSQL_User_role(String _userid, String _deptid) throws Exception {
		StringBuilder sb_sql = new StringBuilder();
		sb_sql.append("update pub_user_role set userdept=");
		sb_sql.append(_deptid);
		sb_sql.append(" where userid=");
		sb_sql.append(_userid);
		sb_sql.append(" and userdept=");
		sb_sql.append(this.str_fromDeptId);
		return sb_sql.toString();
	}

	public int getCloseType() {
		return closeType;
	}

	public void setCloseType(int closeType) {
		this.closeType = closeType;
	}

}
