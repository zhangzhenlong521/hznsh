package cn.com.pushworld.salary.ui.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * 更新数据库中的人员姓名
 * 
 * @author [张营闯/2014-05-04]
 */
public class RandomUserName {
	private static GenerrateNameFacotory nameFacotory = null;
	private HashMap userMap = null;// 存放用户信息
	private List<String> list = new LinkedList<String>();// 存放随机生成的姓名
	private Set<String> set = null;// 存放userMap的key值
	private List codelist = new ArrayList();// 用户code

	public void changeUserName() throws WLTRemoteException, Exception {
		getUserCode();
		changeName();
		changeSqlName();

	}

	// 取得用户的code
	private void getUserCode() throws WLTRemoteException, Exception {
		userMap = UIUtil.getHashMapBySQLByDS(null, "select id,name from pub_user where code not like 'admin'");
		nameFacotory = new GenerrateNameFacotory();
		set = userMap.keySet();
		for (String userid : set) {
			codelist.add(userid);
		}
	}

	// 更換用戶信息中的姓名
	private void changeName() {
		while (list.size() < userMap.size()) {
			String name = nameFacotory.generateName();
			if (!list.contains(name)) {
				list.add(name);
				userMap.put(codelist.get(list.size() - 1), name);
			}
		}
	}

	// 更新数据库中的相关数据
	private void changeSqlName() throws Exception {
		List sqllist = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			String sql = "update pub_user set name ='" + userMap.get(codelist.get(i)) + "' where id = '" + codelist.get(i) + "'";
			String sql_1 = "update sal_personinfo set name ='" + userMap.get(codelist.get(i)) + "' where id= '" + codelist.get(i) + "'";
			String sql_2 = "update sal_person_check_score set checkedusername ='" + userMap.get(codelist.get(i)) + "' where checkeduser= '" + codelist.get(i) + "'";
			String sql_3 = "update sal_person_check_target_score set checkedusername ='" + userMap.get(codelist.get(i)) + "' where checkeduser= '" + codelist.get(i) + "'";
			String sql_4 = "update sal_target_check_user_list set username ='" + userMap.get(codelist.get(i)) + "' where userid= '" + codelist.get(i) + "'";
			String sql_5 = "update sal_person_postduty_score set scoreusername ='" + userMap.get(codelist.get(i)) + "' where scoreuser= '" + codelist.get(i) + "'";
			String sql_6 = "update sal_person_postduty_score set checkedusername='" + userMap.get(codelist.get(i)) + "' where checkeduser = '" + codelist.get(i) + "'";
			String sql_7 = "update sal_person_check_result set checkedusername='" + userMap.get(codelist.get(i)) + "' where checkeduserid = '" + codelist.get(i) + "'";
			String sql_8 = "update sal_person_fund_account set username='" + userMap.get(codelist.get(i)) + "' where userid = '" + codelist.get(i) + "'";

			sqllist.add(sql);
			sqllist.add(sql_1);
			sqllist.add(sql_2);
			sqllist.add(sql_3);
			sqllist.add(sql_4);
			sqllist.add(sql_5);
			sqllist.add(sql_6);
			sqllist.add(sql_7);
			sqllist.add(sql_8);
		}
		UIUtil.executeBatchByDS(null, sqllist);
	}
}
