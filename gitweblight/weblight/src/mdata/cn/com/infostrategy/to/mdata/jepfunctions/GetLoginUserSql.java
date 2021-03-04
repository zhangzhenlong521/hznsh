package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.ArrayList;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;

public class GetLoginUserSql extends PostfixMathCommand {
	private int callType = -1;
	private String column = null;
	private String type = "";
	private String btype = "corp";

	public GetLoginUserSql(int _callType, String type) {
		numberOfParameters = -1;
		this.callType = _callType;
		this.btype = type;
	}

	public void run(Stack inStack) throws ParseException {
		try {
			checkStack(inStack);
			if (curNumberOfParameters == 1) {
				column = (String) inStack.pop();
			} else if (curNumberOfParameters == 2) {
				type = (String) inStack.pop();
				column = (String) inStack.pop();
			}
			String str_value = null;
			if ("role".equals(btype)) {
				str_value = getAllRoleSql();
			} else if ("corp".equals(btype)) {
				str_value = getAllDeptSql();
			}
			if (str_value == null) {
				str_value = ""; //
			}
			inStack.push(str_value);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

	private void getMyBlDeptIds(CommDMO _dmo, ArrayList _list, String deptid) {
		if (deptid != null && !"".equals(deptid) && !"null".equals(deptid)) {
			_list.add(deptid);
			try {
				String str_parentid = _dmo.getStringValueByDS(null,
						"select parentid from pub_corp_dept where id='"
								+ deptid + "'");
				if (str_parentid != null && !str_parentid.trim().equals("")
						&& !str_parentid.trim().equals("null")) {
					getMyBlDeptIds(_dmo, _list, str_parentid);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void getMyBlDeptIds(ArrayList _list, String deptid) {
		if (deptid != null && !"".equals(deptid) && !"null".equals(deptid)) {
			_list.add(deptid);
			try {
				String str_parentid = UIUtil.getStringValueByDS(null,
						"select parentid from pub_corp_dept where id='"
								+ deptid + "'");
				if (str_parentid != null && !str_parentid.trim().equals("")
						&& !str_parentid.trim().equals("null")) {
					getMyBlDeptIds(_list, str_parentid);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String getAllDeptSql() {
		StringBuffer deptsql = new StringBuffer("");
		ArrayList depts = new ArrayList();
		if (callType == WLTConstants.JEPTYPE_UI) {
			getMyBlDeptIds(depts, ClientEnvironment.getCurrLoginUserVO()
					.getBlDeptId());
		} else {
			getMyBlDeptIds(new CommDMO(), depts, new WLTInitContext()
					.getCurrSession().getLoginUserPKDept());// 经查看代码确认这就是登录的机构
		}
		if (depts != null && depts.size() > 0) {
			deptsql.append(" ( ");
			for (int i = 0; i < depts.size(); i++) {
				if (i == 0) {
					if ("likeor".equalsIgnoreCase(type) || "".equals(type)
							|| "null".equals(type)) {
						deptsql.append(" " + column + " like '%;"
								+ depts.get(i) + ";%' ");
					} else {
						deptsql.append(" " + column + " column = '"
								+ depts.get(i) + "' ");
					}
				} else {
					if ("likeor".equalsIgnoreCase(type) || "".equals(type)
							|| "null".equals(type)) {
						deptsql.append(" or " + column + " like '%;"
								+ depts.get(i) + ";%' ");
					} else {
						deptsql.append(" or " + column + " = '" + depts.get(i)
								+ "' ");
					}
				}
			}
			deptsql.append(" ) ");
		} else {
			deptsql.append(" 1=2 ");
		}
		return deptsql.toString();
	}

	public String getAllRoleSql() {
		StringBuffer rolesql = new StringBuffer("");
		String[] roles = null;
		if (callType == WLTConstants.JEPTYPE_UI) {
			roles = ClientEnvironment.getCurrLoginUserVO().getAllRoleIds();
		} else {
			String loginuserid = new WLTInitContext().getCurrSession()
					.getLoginUserId();
			try {
				roles = new CommDMO()
						.getStringArrayFirstColByDS(
								null,
								"select t3.id from pub_user t1,pub_user_role t2,pub_role t3 where t1.id=t2.userid and t2.roleid=t3.id and t1.id='"
										+ loginuserid + "'");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (roles != null && roles.length > 0) {
			rolesql.append(" ( ");
			for (int i = 0; i < roles.length; i++) {
				if (i == 0) {
					if ("likeor".equalsIgnoreCase(type) || "".equals(type)
							|| "null".equals(type)) {
						rolesql.append(" " + column + " like '%;" + roles[i]
								+ ";%' ");
					} else {
						rolesql.append(" " + column + " = '" + roles[i] + "' ");
					}
				} else {
					if ("likeor".equalsIgnoreCase(type) || "".equals(type)
							|| "null".equals(type)) {
						rolesql.append(" or " + column + " like '%;" + roles[i]
								+ ";%' ");
					} else {
						rolesql.append(" or " + column + " = '" + roles[i]
								+ "' ");
					}
				}
			}
			rolesql.append(" ) ");
		} else {
			rolesql.append(" 1=2 ");
		}
		return rolesql.toString();
	}
}
