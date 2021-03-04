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
 * �����Զ���������������Ϣ���Զ��������ݡ�����500�� ÿ500��ִ��һ�Ρ�
 * 
 * @author hm
 * 
 */
public class BuildDemoDataUtil {
	private HashMap valueMap = new HashMap(); // ��ż���Demo�������ݵĻ���
	private HashMap reflectMap = new HashMap(); // ������õ�ӳ���ϵ
	private CommDMO comm;
	private Queue qu = new Queue(); //�����ˮ�š�

	public BuildDemoDataUtil(String _saveTable, BillVO _vos[], int _num) throws Exception {
		insertDemoDataToDB(_saveTable, _vos, _num);
	}

	/*
	 * �������ݣ�500һִ�С�
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
		loadDemoData(_vos, _num); //�����������ݡ�
		List sqlList = new ArrayList();
		Random random = new Random(); //�漴����
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
				if ("����".equals(type)) {
					insertSQL.putFieldValue(itemkey, -(i + 1)); //����ȫΪ������
				} else if ("��ֵ".equals(type) && value != null && !value.equals("")) {
					if (value.contains("select")) {
						HashVO[] vos = (HashVO[]) valueMap.get(itemkey);
						if (vos == null) {
							throw new Exception("��" + itemkey + "û�п��漴��ֵ,��鿴sql���" + value + "�����Ƿ���ȷ");
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
					} else if (value.contains("$")) { // ֤���������ֶΡ�����˵�������ƣ���Ա���ơ�
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
				} else if ("��ֵ".equals(type) && value != null && !value.equals("")) {
					if (value.contains("select")) {
						HashVO[] vos = (HashVO[]) valueMap.get(itemkey);
						Vector vc = new Vector();
						int count = vos.length; // ���ݿ����ܹ�����������
						int c_1 = random.nextInt(count > 5 ? 5 : count) + 1; // ��ѡ����
						// 1-5
						Vector num = new Vector(); // ���ѡȡ����λ�ã����ѡ��һ�ξ�remove,�ӿ��漴�ٶ�
						for (int k = 0; k < c_1; k++) {
							num.add(k);
						}
						StringBuffer sb_value = new StringBuffer();
						StringBuffer sb_value_2 = new StringBuffer();
						if (reflectMap.containsKey(itemkey)) {
							while (true) {
								int a_1 = random.nextInt(num.size()); // ȡ��һ���漴λ��
								int v_1 = (Integer) num.get(a_1); // ȡ���漴λ�õ�����;
								num.remove(a_1); // �����λ�õ������Ƴ�
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
								if (vc.size() == c_1) { // ����漴��ֹͣ
									break;
								}
							}
						} else {
							while (true) {
								int a_1 = random.nextInt(num.size()); // ȡ��һ���漴λ��
								int v_1 = (Integer) num.get(a_1); // ȡ���漴λ�õ�����;
								num.remove(a_1); // �����λ�õ������Ƴ�
								if (!vc.contains(v_1)) {
									if (sb_value.length() == 0) {
										sb_value.append(";" + vos[v_1].getStringValue(0) + ";");
									} else {
										sb_value.append(vos[v_1].getStringValue(0) + ";");
									}
									vc.add(v_1);
								}
								if (vc.size() == c_1) { // ����漴��ֹͣ
									break;
								}
							}
						}
						insertSQL.putFieldValue(itemkey, sb_value.toString());
						if (reflectMap.containsKey(itemkey) && sb_value_2.length() > 0) {
							insertSQL.putFieldValue(reflectMap.get(itemkey).toString(), sb_value_2.toString());
						}
					} else if (value.contains("$")) { // ֤���������ֶΡ�����˵�������ƣ���Ա���ơ�
						continue;
					} else {
						String[] str = (String[]) valueMap.get(itemkey);
						Vector vc = new Vector();
						int count = str.length; // ���ݿ����ܹ�����������
						int c_1 = random.nextInt(count > 5 ? 5 : count) + 1; // ��ѡ����
						// 1-5
						Vector num = new Vector(); // ���ѡȡ����λ�ã����ѡ��һ�ξ�remove,�ӿ��漴�ٶ�
						for (int k = 0; k < c_1; k++) {
							num.add(k);
						}
						StringBuffer sb_value = new StringBuffer();
						while (true) {
							int a_1 = random.nextInt(num.size()); // ȡ��һ���漴λ��
							int v_1 = (Integer) num.get(a_1); // ȡ���漴λ�õ�����;
							num.remove(a_1); // �����λ�õ������Ƴ�
							if (!vc.contains(v_1)) {
								if (sb_value.length() == 0) {
									sb_value.append(";" + str[v_1] + ";");
								} else {
									sb_value.append(str[v_1] + ";");
								}
								vc.add(v_1);
							}
							if (vc.size() == c_1) { // ����漴��ֹͣ
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
	 * ����������Ϣһ���԰�Demo���ݷ��뻺����
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
			if ("��ֵ".equals(type) && value != null && !value.equals("")) {
				if (autono && qu.size() == 0) { //��ʼ�� һ�ξ͵��ˣ�û��Ҫ������ˮ�ֶζ���һ����
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
				} else if (value.startsWith("$")) { // ֤���������ֶΡ�����˵�������ƣ���Ա���ơ�
					reflectMap.put(value.substring(1), itemkey);
					continue;
				} else {
					String[] values = value.split(";");
					valueMap.put(itemkey, values);
				}
			} else if ("��ֵ".equals(type) && value != null && !value.equals("")) {
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
