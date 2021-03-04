package cn.com.infostrategy.bs.sysapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.collections.map.LinkedMap;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;

public class RolesAndMenuRelationDMO extends AbstractDMO {
	CommDMO dmo = new CommDMO();
	TBUtil tbutil = new TBUtil();

	public BillCellVO getRolesAndMenuRelation() throws Exception {
		HashVO menus[] = dmo.getHashVoArrayAsTreeStructByDS(null, "select * from pub_menu", "id", "parentmenuid", "seq", "");

		HashMap<String, String> allmenu = new HashMap();
		String webpushmenuid = "";
		for (int i = 0; i < menus.length; i++) {
			allmenu.put(menus[i].getStringValue("id"), menus[i].getStringValue("name"));
			if ("平台配置".equals(menus[i].getStringValue("name"))) {
				webpushmenuid = menus[i].getStringValue("id");
			}
		}

		HashMap<String, HashVO> last = new HashMap<String, HashVO>();
		LinkedMap parent = new LinkedMap();
		List lastlist = new ArrayList<HashVO>();
		for (int i = 0; i < menus.length; i++) {
			String parentids = menus[i].getStringValue("$parentpathids");
			if (parentids != null && !tbutil.isEmpty(webpushmenuid) && parentids.contains(";" + webpushmenuid + ";")) {
				continue;
			}
			String isleaf = menus[i].getStringValue("$isleaf");
			if ("Y".equals(isleaf)) { //如果是根节点
				lastlist.add(menus[i]);
				String parentmenu = menus[i].getStringValue("parentmenuid");
				if (parent.containsKey(parentmenu)) {
					List childs = (List) parent.get(parentmenu);
					childs.add(menus[i]);
					parent.put(parentmenu, childs);
				} else {
					List childs = new ArrayList();
					childs.add(menus[i]);
					parent.put(parentmenu, childs);
				}
			}
		}
		List billcellitem = new ArrayList<BillCellItemVO[]>();

		List firstrow = new ArrayList<BillCellItemVO>();
		BillCellItemVO headcol1 = new BillCellItemVO();
		headcol1.setCellvalue("模块名称");
		firstrow.add(headcol1);

		BillCellItemVO headcol2 = new BillCellItemVO();
		headcol2.setCellvalue("功能点");
		firstrow.add(headcol2);

		HashVO rolesMap[] = dmo.getHashVoArrayByDS(null, "select id,name from PUB_ROLE ");

		for (int i = 0; i < rolesMap.length; i++) {
			String rolename = (String) rolesMap[i].getStringValue("name");
			BillCellItemVO headcol_auto = new BillCellItemVO();
			headcol_auto.setCellvalue(rolename);
			firstrow.add(headcol_auto);
		}
		billcellitem.add((BillCellItemVO[]) firstrow.toArray(new BillCellItemVO[0]));
		HashMap<String, String> menuid_roles_map = getMenuRoles();
		//第二行
		Iterator menu_rolels = parent.entrySet().iterator();
		while (menu_rolels.hasNext()) {
			Entry entry = (Entry) menu_rolels.next();
			String menuid = (String) entry.getKey();
			List menu_childs = (List) entry.getValue();

			for (int i = 0; i < menu_childs.size(); i++) {
				List oneRow = new ArrayList<BillCellItemVO>();
				BillCellItemVO currrowcol1 = new BillCellItemVO();
				currrowcol1.setCellvalue(allmenu.get(menuid));
				oneRow.add(currrowcol1);
				HashVO leafMenu = (HashVO) menu_childs.get(i); // 叶子功能点
				BillCellItemVO currrowcol2 = new BillCellItemVO();
				currrowcol2.setCellvalue(leafMenu.getStringValue("name"));
				oneRow.add(currrowcol2);
				String menu_rols_str = menuid_roles_map.get(leafMenu.getStringValue("id"));
				for (int m = 0; m < rolesMap.length; m++) {
					String roleid = (String) rolesMap[m].getStringValue("id");
					BillCellItemVO headcol = new BillCellItemVO();
					if (menu_rols_str != null && menu_rols_str.contains(";" + roleid + ";")) {
						headcol.setCellvalue("●");
					}
					oneRow.add(headcol);
				}
				billcellitem.add((BillCellItemVO[]) oneRow.toArray(new BillCellItemVO[0]));
			}
		}
		BillCellItemVO[][] cellvos = (BillCellItemVO[][]) billcellitem.toArray(new BillCellItemVO[0][0]);
		formatSpan(cellvos, new int[] { 0 });
		BillCellVO cellvo = new BillCellVO();
		cellvo.setCollength(2 + rolesMap.length);
		cellvo.setRowlength(billcellitem.size());
		cellvo.setCellItemVOs(cellvos);
		return cellvo;
	}

	public HashMap getMenuRoles() {
		try {
			HashVO vos[] = dmo.getHashVoArrayByDS(null, "select menuid,roleid from pub_role_menu");
			HashMap<String, String> map = new HashMap();
			for (int i = 0; i < vos.length; i++) {
				String menuid = vos[i].getStringValue("menuid");
				if (map.containsKey(menuid)) {
					String m = map.get(menuid);
					m += vos[i].getStringValue("roleid") + ";";
					map.put(menuid, m);
				} else {
					String m = ";" + vos[i].getStringValue("roleid") + ";";
					map.put(menuid, m);
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将单元格相同内容的合并
	 * 
	 * @param cellItemVOs
	 * @param _spanColumns
	 *            那几列需要处理
	 */
	public void formatSpan(BillCellItemVO[][] cellItemVOs, int[] _spanColumns) {
		if (_spanColumns != null) {
			HashMap temp = new HashMap();
			for (int i = 0; i < _spanColumns.length; i++) {
				int li_pos = _spanColumns[i];
				if (li_pos >= 0) {
					int li_spancount = 1;
					int li_spanbeginpos = 1;
					for (int k = 2; k < cellItemVOs.length; k++) {
						String str_value = cellItemVOs[k][li_pos].getCellvalue();
						// 合并的列颜色一样
						cellItemVOs[k][li_pos].setBackground("234,240,248");
						String str_value_front = cellItemVOs[k - 1][li_pos].getCellvalue();
						if (tbutil.compareTwoString(str_value_front, str_value)) {
							if (i >= 1) {
								String str_value0 = cellItemVOs[k][_spanColumns[i - 1]].getCellvalue();
								String str_value_front0 = cellItemVOs[k - 1][_spanColumns[i - 1]].getCellvalue();
								if (tbutil.compareTwoString(str_value0, str_value_front0)) {
									li_spancount++;
								} else {
									cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
									li_spancount = 1;
									li_spanbeginpos = k;
								}
							} else {
								li_spancount++;
							}

						} else {
							cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
							li_spancount = 1;
							li_spanbeginpos = k;
						}
					}
					cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
				}
			}
		}
	}

}
