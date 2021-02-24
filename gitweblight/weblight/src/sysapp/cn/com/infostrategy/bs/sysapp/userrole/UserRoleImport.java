package cn.com.infostrategy.bs.sysapp.userrole;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;

/**
 * 人员角色关联 【杨科/2012-09-07】
 */

public class UserRoleImport {
	private CommDMO commDMO = null;

	public HashMap urLike(HashMap hm) throws Exception {
		HashMap rhm = new HashMap();

		urLike();

		return rhm;
	}

	public HashMap urJoin(HashMap hm) throws Exception {
		HashMap rhm = new HashMap();

		urJoin();

		return rhm;
	}

	private void urLike() {
		try {
			HashVO[] hvs_dept = getCommDMO().getHashVoArrayAsTreeStructByDS(null, " select * from pub_corp_dept", "id", "parentid", null, null);
			HashMap hs_dept = new HashMap();
			for (int i = 0; i < hvs_dept.length; i++) {
				hs_dept.put(hvs_dept[i].getStringValue("$parentpathnamelink1"), hvs_dept[i].getStringValue("id")); //获取机构层级
			}

			HashVO[] hvs_role = getCommDMO().getHashVoArrayByDS(null, " select id,code from pub_role ");
			HashMap hs_pub_role = new HashMap();
			for (int i = 0; i < hvs_role.length; i++) {
				hs_pub_role.put(hvs_role[i].getStringValue("code"), hvs_role[i].getStringValue("id")); //反找角色id
			}

			HashVO[] hvs_users = getCommDMO().getHashVoArrayByDS(null, " select id,姓名,一级分行名称,二级分行名称,部门名称,系统角色 from pub_temp_users ");

			ArrayList list_sqls = new ArrayList();
			for (int i = 0; i < hvs_users.length; i++) {
				String username = hvs_users[i].getStringValue("姓名");

				String deptname = hvs_users[i].getStringValue("一级分行名称");
				String twodeptname = hvs_users[i].getStringValue("二级分行名称");
				if (!twodeptname.substring(twodeptname.length() - 4, twodeptname.length()).equals("分行本部")) {
					deptname += "-" + twodeptname;
				}
				deptname += "-" + hvs_users[i].getStringValue("部门名称");

				String deptmark = "匹配失败";
				if (hs_dept.containsKey(deptname)) {
					deptmark = "匹配成功";
				}

				String role = "";
				String rolemark = "匹配失败";

				String roletemp = (String) hvs_users[i].getStringValue("系统角色");
				if (!(roletemp == null || roletemp.equals(""))) {
					String[] roles = ((String) hvs_users[i].getStringValue("系统角色")).split(";");
					int mark = 0;
					for (int j = 0; j < roles.length; j++) {
						if (!(roles[j] == null || roles[j].equals(""))) {
							role += roles[j] + ";";
							mark++;
						}
					}
					if (mark == roles.length) {
						rolemark = "匹配成功";
					}
				}

				list_sqls.add("update pub_temp_users set username='" + username + "', deptname='" + deptname + "', deptmark='" + deptmark + "', rolesname='" + role + "', rolesmark='" + rolemark + "' where id=" + hvs_users[i].getStringValue("id"));
			}
			getCommDMO().executeBatchByDS(null, list_sqls);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void urJoin() {
		try {
			HashVO[] hvs_dept = getCommDMO().getHashVoArrayAsTreeStructByDS(null, " select * from pub_corp_dept", "id", "parentid", null, null);
			HashMap hs_dept = new HashMap();
			for (int i = 0; i < hvs_dept.length; i++) {
				hs_dept.put(hvs_dept[i].getStringValue("$parentpathnamelink1"), hvs_dept[i].getStringValue("id")); //获取机构层级
			}

			HashVO[] hvs_role = getCommDMO().getHashVoArrayByDS(null, " select id,code from pub_role ");
			HashMap hs_role = new HashMap();
			for (int i = 0; i < hvs_role.length; i++) {
				hs_role.put(hvs_role[i].getStringValue("code"), hvs_role[i].getStringValue("id")); //反找角色id
			}

			HashVO[] hvs_deptid_usename = getCommDMO().getHashVoArrayByDS(null, " select a.userdept,b.name from pub_user_post a, pub_user b where a.userid=b.id ");
			HashMap hs_deptid_usename = new HashMap();
			for (int i = 0; i < hvs_deptid_usename.length; i++) {
				hs_deptid_usename.put(hvs_deptid_usename[i].getStringValue("userdept") + "-" + hvs_deptid_usename[i].getStringValue("name"), ""); //获取机构层级
			}

			HashVO[] hvs_users = getCommDMO().getHashVoArrayByDS(null, " select * from pub_temp_users ");

			if (hvs_users.length > 0) {
				String username = hvs_users[0].getStringValue("username");
				if (username == null || username.equals("")) {
					urLike();
					hvs_users = getCommDMO().getHashVoArrayByDS(null, " select * from pub_temp_users ");
				}
			}

			ArrayList list_sqls = new ArrayList();
			for (int i = 0; i < hvs_users.length; i++) {
				String deptmark = hvs_users[i].getStringValue("deptmark");
				String rolesmark = hvs_users[i].getStringValue("rolesmark");
				if (!(deptmark != null && rolesmark != null && deptmark.equals("匹配成功") && deptmark.equals("匹配成功"))) {
					list_sqls.add("update pub_temp_users set temp='导入失败' where id=" + hvs_users[i].getStringValue("id"));
					continue;
				}

				if (hs_deptid_usename.containsKey((String) hs_dept.get(hvs_users[i].getStringValue("deptname")) + "-" + hvs_users[i].getStringValue("姓名"))) {
					list_sqls.add("update pub_temp_users set temp='用户已存在,不能重复导入' where id=" + hvs_users[i].getStringValue("id"));
					continue;
				}

				String newid = getCommDMO().getSequenceNextValByDS(null, "s_pub_user");
				list_sqls.add(new InsertSQLBuilder("pub_user", new String[][] { { "id", newid }, { "NAME", hvs_users[i].getStringValue("姓名") }, { "SEX", hvs_users[i].getStringValue("性别") }, { "IDCARDNO", hvs_users[i].getStringValue("身份证号") }, { "STAFF", hvs_users[i].getStringValue("岗位") }, { "POST", hvs_users[i].getStringValue("职务") }, { "BIRTHYEAR", hvs_users[i].getStringValue("出生日期") },
						{ "DEGREEN", hvs_users[i].getStringValue("最高学历") }, { "SPECIALTY", hvs_users[i].getStringValue("专业") }, { "TELEPHONE", hvs_users[i].getStringValue("联系电话") }, { "MOBILE", hvs_users[i].getStringValue("手机") }, { "CODE3", hvs_users[i].getStringValue("柜员号") } }).getSQL());

				list_sqls.add("delete from pub_temp_users where id=" + hvs_users[i].getStringValue("id"));

				//关联人员机构
				String postnewid = getCommDMO().getSequenceNextValByDS(null, "s_pub_user_post");
				String userdept = (String) hs_dept.get(hvs_users[i].getStringValue("deptname"));
				if (userdept != null && !(userdept.equals(""))) {
					list_sqls.add(new InsertSQLBuilder("pub_user_post", new String[][] { { "id", postnewid }, { "userid", newid }, { "userdept", userdept } }).getSQL());
				}

				//关联人员角色
				String[] roles = hvs_users[i].getStringValue("rolesname").split(";");
				for (int j = 0; j < roles.length; j++) {
					if (roles[j] != null && !(roles[j].equals(""))) {
						String rolenewid = getCommDMO().getSequenceNextValByDS(null, "s_pub_user_role");
						//这里有问题，int类型最大值为2147483647，以前在pub_user_role的id加了前缀20000，导入没问题，但如果用数据源工具导出，会死循环，故改为不加前缀【李春娟/2016-02-29】
						list_sqls.add(new InsertSQLBuilder("pub_user_role", new String[][] { { "id", rolenewid }, { "userid", newid }, { "roleid", (String) hs_role.get(roles[j]) } }).getSQL());
					}
				}
			}
			getCommDMO().executeBatchByDS(null, list_sqls);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private CommDMO getCommDMO() {
		if (commDMO == null) {
			commDMO = new CommDMO();
		}
		return commDMO;
	}

}
