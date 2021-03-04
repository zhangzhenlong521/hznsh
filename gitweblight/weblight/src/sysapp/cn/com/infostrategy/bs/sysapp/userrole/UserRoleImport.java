package cn.com.infostrategy.bs.sysapp.userrole;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;

/**
 * ��Ա��ɫ���� �����/2012-09-07��
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
				hs_dept.put(hvs_dept[i].getStringValue("$parentpathnamelink1"), hvs_dept[i].getStringValue("id")); //��ȡ�����㼶
			}

			HashVO[] hvs_role = getCommDMO().getHashVoArrayByDS(null, " select id,code from pub_role ");
			HashMap hs_pub_role = new HashMap();
			for (int i = 0; i < hvs_role.length; i++) {
				hs_pub_role.put(hvs_role[i].getStringValue("code"), hvs_role[i].getStringValue("id")); //���ҽ�ɫid
			}

			HashVO[] hvs_users = getCommDMO().getHashVoArrayByDS(null, " select id,����,һ����������,������������,��������,ϵͳ��ɫ from pub_temp_users ");

			ArrayList list_sqls = new ArrayList();
			for (int i = 0; i < hvs_users.length; i++) {
				String username = hvs_users[i].getStringValue("����");

				String deptname = hvs_users[i].getStringValue("һ����������");
				String twodeptname = hvs_users[i].getStringValue("������������");
				if (!twodeptname.substring(twodeptname.length() - 4, twodeptname.length()).equals("���б���")) {
					deptname += "-" + twodeptname;
				}
				deptname += "-" + hvs_users[i].getStringValue("��������");

				String deptmark = "ƥ��ʧ��";
				if (hs_dept.containsKey(deptname)) {
					deptmark = "ƥ��ɹ�";
				}

				String role = "";
				String rolemark = "ƥ��ʧ��";

				String roletemp = (String) hvs_users[i].getStringValue("ϵͳ��ɫ");
				if (!(roletemp == null || roletemp.equals(""))) {
					String[] roles = ((String) hvs_users[i].getStringValue("ϵͳ��ɫ")).split(";");
					int mark = 0;
					for (int j = 0; j < roles.length; j++) {
						if (!(roles[j] == null || roles[j].equals(""))) {
							role += roles[j] + ";";
							mark++;
						}
					}
					if (mark == roles.length) {
						rolemark = "ƥ��ɹ�";
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
				hs_dept.put(hvs_dept[i].getStringValue("$parentpathnamelink1"), hvs_dept[i].getStringValue("id")); //��ȡ�����㼶
			}

			HashVO[] hvs_role = getCommDMO().getHashVoArrayByDS(null, " select id,code from pub_role ");
			HashMap hs_role = new HashMap();
			for (int i = 0; i < hvs_role.length; i++) {
				hs_role.put(hvs_role[i].getStringValue("code"), hvs_role[i].getStringValue("id")); //���ҽ�ɫid
			}

			HashVO[] hvs_deptid_usename = getCommDMO().getHashVoArrayByDS(null, " select a.userdept,b.name from pub_user_post a, pub_user b where a.userid=b.id ");
			HashMap hs_deptid_usename = new HashMap();
			for (int i = 0; i < hvs_deptid_usename.length; i++) {
				hs_deptid_usename.put(hvs_deptid_usename[i].getStringValue("userdept") + "-" + hvs_deptid_usename[i].getStringValue("name"), ""); //��ȡ�����㼶
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
				if (!(deptmark != null && rolesmark != null && deptmark.equals("ƥ��ɹ�") && deptmark.equals("ƥ��ɹ�"))) {
					list_sqls.add("update pub_temp_users set temp='����ʧ��' where id=" + hvs_users[i].getStringValue("id"));
					continue;
				}

				if (hs_deptid_usename.containsKey((String) hs_dept.get(hvs_users[i].getStringValue("deptname")) + "-" + hvs_users[i].getStringValue("����"))) {
					list_sqls.add("update pub_temp_users set temp='�û��Ѵ���,�����ظ�����' where id=" + hvs_users[i].getStringValue("id"));
					continue;
				}

				String newid = getCommDMO().getSequenceNextValByDS(null, "s_pub_user");
				list_sqls.add(new InsertSQLBuilder("pub_user", new String[][] { { "id", newid }, { "NAME", hvs_users[i].getStringValue("����") }, { "SEX", hvs_users[i].getStringValue("�Ա�") }, { "IDCARDNO", hvs_users[i].getStringValue("���֤��") }, { "STAFF", hvs_users[i].getStringValue("��λ") }, { "POST", hvs_users[i].getStringValue("ְ��") }, { "BIRTHYEAR", hvs_users[i].getStringValue("��������") },
						{ "DEGREEN", hvs_users[i].getStringValue("���ѧ��") }, { "SPECIALTY", hvs_users[i].getStringValue("רҵ") }, { "TELEPHONE", hvs_users[i].getStringValue("��ϵ�绰") }, { "MOBILE", hvs_users[i].getStringValue("�ֻ�") }, { "CODE3", hvs_users[i].getStringValue("��Ա��") } }).getSQL());

				list_sqls.add("delete from pub_temp_users where id=" + hvs_users[i].getStringValue("id"));

				//������Ա����
				String postnewid = getCommDMO().getSequenceNextValByDS(null, "s_pub_user_post");
				String userdept = (String) hs_dept.get(hvs_users[i].getStringValue("deptname"));
				if (userdept != null && !(userdept.equals(""))) {
					list_sqls.add(new InsertSQLBuilder("pub_user_post", new String[][] { { "id", postnewid }, { "userid", newid }, { "userdept", userdept } }).getSQL());
				}

				//������Ա��ɫ
				String[] roles = hvs_users[i].getStringValue("rolesname").split(";");
				for (int j = 0; j < roles.length; j++) {
					if (roles[j] != null && !(roles[j].equals(""))) {
						String rolenewid = getCommDMO().getSequenceNextValByDS(null, "s_pub_user_role");
						//���������⣬int�������ֵΪ2147483647����ǰ��pub_user_role��id����ǰ׺20000������û���⣬�����������Դ���ߵ���������ѭ�����ʸ�Ϊ����ǰ׺�����/2016-02-29��
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
