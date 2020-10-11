package cn.com.infostrategy.bs.mdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.Queue;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;

/**
 * 解析自动生成数据配置信息，自动插入数据。大于500条 每500条执行一次。
 * 
 * @author hm
 * 
 */
public class BuildDemoDataUtil {
	private HashMap valueMap = new HashMap(); // 存放加载Demo所有数据的缓存
	private HashMap reflectMap = new HashMap(); // 存放引用的映射关系
	private CommDMO comm;
	private Queue qu = new Queue(); //存放流水号。

	public BuildDemoDataUtil(String _saveTable, BillVO _vos[], int _num) throws Exception {
		insertDemoDataToDB(_saveTable, _vos, _num);
	}

	/*
	 * 插入数据，500一执行。
	 */
	public void insertDemoDataToDB(String _saveTable, BillVO _vos[], int _num) throws Exception {
		List sqlList = getBuildDemoSqlList(_saveTable, _vos, _num);
		int size = sqlList.size();
		if (size > 500) {
			int index = 0;
			while (true) {
				if (size / 500 > index) {
					getCommDMO().executeBatchByDS(null, sqlList.subList(index * 500, (index + 1) * 500), false);
				} else {
					getCommDMO().executeBatchByDS(null, sqlList.subList(index * 500, size), false);
					break;
				}
				index++;
			}
		} else {
			getCommDMO().executeBatchByDS(null, sqlList, false);
		}
	}

	private List getBuildDemoSqlList(String _saveTable, BillVO _vos[], int _num) throws Exception {
		loadDemoData(_vos, _num); //加载所有数据。
		List sqlList = new ArrayList();
		Random random = new Random(); //随即对象
		for (int i = 0; i < _num; i++) {
			InsertSQLBuilder insertSQL = new InsertSQLBuilder(_saveTable);
			String autonoStr = null;
			for (int j = 0; j < _vos.length; j++) {
				BillVO vo = _vos[j];
				String itemkey = (String) vo.getStringValue("itemkey");
				String type = vo.getStringValue("type");
				String value = vo.getStringValue("value");
				boolean autono = false;
				if (vo.getStringValue("autono") != null) {
					autono = vo.getStringValue("autono").equals("N") ? false : true;
				}
				if ("序列".equals(type)) {
					insertSQL.putFieldValue(itemkey, -(i + 1)); //序列全为负数。
				} else if ("单值".equals(type) && value != null && !value.equals("")) {
					if (value.contains("select")) {
						HashVO[] vos = (HashVO[]) valueMap.get(itemkey);
						if (vos == null) {
							throw new Exception("键" + itemkey + "没有可随即的值,请查看sql语句" + value + "配置是否正确");
						}
						int rad = random.nextInt(vos.length);
						if (autono) {
							if (autonoStr == null) {
								autonoStr = getAutoNo();
							}
							insertSQL.putFieldValue(itemkey, vos[rad].getStringValue(0) + "_" + autonoStr);
						} else {
							insertSQL.putFieldValue(itemkey, vos[rad].getStringValue(0));
						}
						if (reflectMap.containsKey(itemkey)) {
							String item_2 = (String) reflectMap.get(itemkey);
							insertSQL.putFieldValue(item_2, vos[rad].getStringValue(1));
						}
					} else if (value.contains("$")) { // 证明是冗余字段。比如说机构名称，人员名称。
						continue;
					} else {
						String[] str = (String[]) valueMap.get(itemkey);
						int rad = random.nextInt(str.length);
						if (autono) {
							if (autonoStr == null) {
								autonoStr = getAutoNo();
							}
							insertSQL.putFieldValue(itemkey, str[rad] + "_" + autonoStr);
						} else {
							insertSQL.putFieldValue(itemkey, str[rad]);
						}
					}
				} else if ("多值".equals(type) && value != null && !value.equals("")) {
					if (value.contains("select")) {
						HashVO[] vos = (HashVO[]) valueMap.get(itemkey);
						Vector vc = new Vector();
						int count = vos.length; // 数据库中总共的数据条数
						int c_1 = random.nextInt(count > 5 ? 5 : count) + 1; // 多选几个
						// 1-5
						Vector num = new Vector(); // 存放选取数组位置，如果选过一次就remove,加快随即速度
						for (int k = 0; k < c_1; k++) {
							num.add(k);
						}
						StringBuffer sb_value = new StringBuffer();
						StringBuffer sb_value_2 = new StringBuffer();
						if (reflectMap.containsKey(itemkey)) {
							while (true) {
								int a_1 = random.nextInt(num.size()); // 取得一个随即位置
								int v_1 = (Integer) num.get(a_1); // 取出随即位置的内容;
								num.remove(a_1); // 把这个位置的内容移除
								if (!vc.contains(v_1)) {
									if (sb_value.length() == 0) {
										sb_value.append(";" + vos[v_1].getStringValue(0) + ";");
										sb_value_2.append(";" + vos[v_1].getStringValue(1) + ";");
									} else {
										sb_value.append(vos[v_1].getStringValue(0) + ";");
										sb_value_2.append(vos[v_1].getStringValue(1) + ";");
									}
									vc.add(v_1);
								}
								if (vc.size() == c_1) { // 如果随即够停止
									break;
								}
							}
						} else {
							while (true) {
								int a_1 = random.nextInt(num.size()); // 取得一个随即位置
								int v_1 = (Integer) num.get(a_1); // 取出随即位置的内容;
								num.remove(a_1); // 把这个位置的内容移除
								if (!vc.contains(v_1)) {
									if (sb_value.length() == 0) {
										sb_value.append(";" + vos[v_1].getStringValue(0) + ";");
									} else {
										sb_value.append(vos[v_1].getStringValue(0) + ";");
									}
									vc.add(v_1);
								}
								if (vc.size() == c_1) { // 如果随即够停止
									break;
								}
							}
						}
						insertSQL.putFieldValue(itemkey, sb_value.toString());
						if (reflectMap.containsKey(itemkey) && sb_value_2.length() > 0) {
							insertSQL.putFieldValue(reflectMap.get(itemkey).toString(), sb_value_2.toString());
						}
					} else if (value.contains("$")) { // 证明是冗余字段。比如说机构名称，人员名称。
						continue;
					} else {
						String[] str = (String[]) valueMap.get(itemkey);
						Vector vc = new Vector();
						int count = str.length; // 数据库中总共的数据条数
						int c_1 = random.nextInt(count > 5 ? 5 : count) + 1; // 多选几个
						// 1-5
						Vector num = new Vector(); // 存放选取数组位置，如果选过一次就remove,加快随即速度
						for (int k = 0; k < c_1; k++) {
							num.add(k);
						}
						StringBuffer sb_value = new StringBuffer();
						while (true) {
							int a_1 = random.nextInt(num.size()); // 取得一个随即位置
							int v_1 = (Integer) num.get(a_1); // 取出随即位置的内容;
							num.remove(a_1); // 把这个位置的内容移除
							if (!vc.contains(v_1)) {
								if (sb_value.length() == 0) {
									sb_value.append(";" + str[v_1] + ";");
								} else {
									sb_value.append(str[v_1] + ";");
								}
								vc.add(v_1);
							}
							if (vc.size() == c_1) { // 如果随即够停止
								break;
							}
						}
						insertSQL.putFieldValue(itemkey, sb_value.toString());
					}
				}
			}
			sqlList.add(insertSQL.getSQL());
		}
		return sqlList;
	}

	/*
	 * 更具配置信息一次性把Demo数据放入缓存中
	 */
	private void loadDemoData(BillVO _vos[], int num) {
		for (int j = 0; j < _vos.length; j++) {
			BillVO vo = _vos[j];
			String itemkey = (String) vo.getStringValue("itemkey");
			String type = vo.getStringValue("type");
			String value = vo.getStringValue("value");
			boolean autono = false;
			if (vo.getStringValue("autono") != null) {
				autono = vo.getStringValue("autono").equals("N") ? false : true;
			}
			if ("单值".equals(type) && value != null && !value.equals("")) {
				if (autono && qu.size() == 0) { //初始化 一次就得了，没必要所有流水字段都搞一个。
					for (int i = 1; i <= num; i++) {
						if (i < 10) {
							qu.push("00000" + i);
						} else if (i < 100) {
							qu.push("0000" + i);
						} else if (i < 1000) {
							qu.push("000" + i);
						} else if (i < 10000) {
							qu.push("00" + i);
						} else {
							qu.push("0" + i);
						}
					}
				}
				if (value.contains("select")) {
					try {
						HashVO vos[] = getCommDMO().getHashVoArrayByDS(null, value, 100);
						valueMap.put(itemkey, vos);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (value.startsWith("$")) { // 证明是冗余字段。比如说机构名称，人员名称。
					reflectMap.put(value.substring(1), itemkey);
					continue;
				} else {
					String[] values = value.split(";");
					valueMap.put(itemkey, values);
				}
			} else if ("多值".equals(type) && value != null && !value.equals("")) {
				if (value.contains("select")) {
					try {
						HashVO vos[] = getCommDMO().getHashVoArrayByDS(null, value, 100);
						valueMap.put(itemkey, vos);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (value.startsWith("$")) {
					reflectMap.put(value.substring(1), itemkey);
					continue;
				} else {
					String[] values = value.split(";");
					valueMap.put(itemkey, values);
				}
			}
		}
	}

	public String getAutoNo() {
		String str = "";
		try {
			if (qu.size() > 0) {
				str = (String) qu.pop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public CommDMO getCommDMO() {
		if (comm == null) {
			comm = new CommDMO();
		}
		return comm;
	}
}
