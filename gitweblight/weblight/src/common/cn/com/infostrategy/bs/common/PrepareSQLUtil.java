package cn.com.infostrategy.bs.common;

import java.util.ArrayList;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * ��������Ŀ�пͻ��Ƽ���Ҫ������SQL�����Ǳ����滻Ԥ����ķ�ʽ����ʹ��PrepareStatement,����������һһ������!!!
 * �ù��߾����������SQL,Ӳ������м�������,��ʵ������������Щ�ǽ������˵�,����where�����о���and/or��!
 * �����ÿ�ζ��ӵ��ô�����SQL������б��򿪷������ַǳ�������!! ������ǰ���еĴ��붼Ҫ��,������̫��!
 * �������Կ��ǣ���Ϊ������ǿ��Խ����ɹ��ģ�Ϊ����߿���Ч�ʣ����Ի���ͳһʹ�ø��෴�����!!
 * ����ƽ̨�����в������ؾ����Ƿ����øý�������������ݿͻ��ֲ��ϸ�Ҫ����ô��(�������ǵ�һ��),�����������ɱ�����͵�!
 * @author xch
 *
 */
public class PrepareSQLUtil {

	TBUtil tbUtil = new TBUtil(); //

	/**
	 * ����һ��SQL,ת����prepare�ı����滻Ԥ�������ʽ!!!
	 * @param _sql
	 * @return ���صľ���һ��ArrayList,���е�һ������ת����Ĵ��ʺŵ�SQL,��������������滻�ı���!!!
	 */
	public ArrayList prepareSQL(String _sql) {
		String str_lowersql = _sql.toLowerCase().trim(); //
		if (str_lowersql.startsWith("select ")) { //�����select��ͷ��!
			return prepareSelectSQL(_sql); //
		} else if (str_lowersql.startsWith("update ")) {
			return prepareUpdateSQL(_sql);
		} else if (str_lowersql.startsWith("delete ")) {
			return prepareDeleteSQL(_sql);
		} else if (str_lowersql.startsWith("insert ")) {
			return prepareInsertSQL(_sql);
		} else {
			return null;
		}
	}

	/**
	 * ����Select���!!
	 * @param _sql
	 * @return
	 */
	public ArrayList prepareSelectSQL(String _sql) {
		return prepareSelectOrDeleteSQL(_sql); //
	}

	/**
	 * ����Insert���!
	 * @param _sql
	 * @return
	 */
	public ArrayList prepareInsertSQL(String _sql) {
		try {
			int li_pos_values = _sql.toLowerCase().indexOf("values"); //���ҵ�values�ĵط�!!
			if (li_pos_values < 0) { //���û��values����������!!!����insert pub_user select * from ...���﷨
				return null; //
			}
			String str_sql_1 = _sql.substring(0, li_pos_values); //valuesǰ���
			String str_sql_2 = _sql.substring(li_pos_values + 6, _sql.length()).trim(); //values�����
			StringBuilder sb_allNewSQL = new StringBuilder(); //

			sb_allNewSQL.append(str_sql_1); //
			sb_allNewSQL.append(" values ("); // 
			if (str_sql_2.endsWith(";")) {
				str_sql_2 = str_sql_2.substring(0, str_sql_2.length() - 1); //
			}

			ArrayList al_ValueList = new ArrayList(); //�洢����������!!
			String str_valuesSQL = splitInsertValueSQL(str_sql_2.substring(1, str_sql_2.length() - 1), new StringBuilder(), al_ValueList); //
			sb_allNewSQL.append(str_valuesSQL); //����������!
			sb_allNewSQL.append(")"); //����������!

			al_ValueList.add(0, sb_allNewSQL.toString()); //
			return al_ValueList; //
		} catch (Exception ex) {
			System.err.println("Prepare����SQL[" + _sql + "]�����쳣[" + ex.getClass().getName() + ":" + ex.getMessage() + "]"); //
			//ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * ����Update���
	 * @param _sql
	 * @return
	 */
	public ArrayList prepareUpdateSQL(String _sql) {
		try {
			int li_pos_where = _sql.toLowerCase().indexOf(" where "); //���ҵ�where�ĵط�!!
			if (li_pos_where < 0) { //���û��where����������!!!����ֱ������ĳ��ֵ!
				return null; //
			}
			String str_sql_1 = _sql.substring(0, li_pos_where); //ǰ���
			String str_sql_2 = _sql.substring(li_pos_where, _sql.length()).trim(); //�����
			int li_pos_set = str_sql_1.toLowerCase().indexOf(" set "); //��Set

			StringBuilder sb_allNewSQL = new StringBuilder(); //
			sb_allNewSQL.append(str_sql_1.substring(0, li_pos_set).trim() + " set "); //

			//whereǰ������
			ArrayList al_ValueList = new ArrayList(); //�洢����������!!
			String str_setNewSQL = splitSetSQL(str_sql_1.substring(li_pos_set + 5, str_sql_1.length()).trim(), new StringBuilder(), al_ValueList); //
			sb_allNewSQL.append(str_setNewSQL); //ƴ����set����!!

			//where�������
			sb_allNewSQL.append(" where "); //����where����!!

			String str_andwhere = str_sql_2.trim().substring(5, str_sql_2.length()).trim(); //
			String str_whereConsSQL = splitWhereCondition(str_andwhere, al_ValueList); //����where����
			sb_allNewSQL.append(str_whereConsSQL); //ƴ����whereת�����

			al_ValueList.add(0, sb_allNewSQL.toString()); //��ǰ��ĵ�һ������ת�����SQL
			return al_ValueList; //
		} catch (Exception ex) {
			System.err.println("Prepare����SQL[" + _sql + "]�����쳣[" + ex.getClass().getName() + ":" + ex.getMessage() + "]"); //
			//ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * ����Delete���!
	 * @param _sql
	 * @return
	 */
	public ArrayList prepareDeleteSQL(String _sql) {
		return prepareSelectOrDeleteSQL(_sql); //
	}

	/**
	 * select��delete��ԭ��һ��,��ֻ�账��where���������,���԰�װ����һ������!!!
	 * @param _sql
	 * @return
	 */
	private ArrayList prepareSelectOrDeleteSQL(String _sql) {
		try {
			int li_pos_where = _sql.toLowerCase().indexOf(" where "); //���ҵ�where�ĵط�!!
			if (li_pos_where < 0) { //���û��where����������!!!����ֱ������ĳ��ֵ!
				return null; //
			}
			String str_sql_1 = _sql.substring(0, li_pos_where); //ǰ���
			String str_sql_2 = _sql.substring(li_pos_where, _sql.length()).trim(); //�����

			StringBuilder sb_allNewSQL = new StringBuilder(); //
			ArrayList al_ValueList = new ArrayList(); //�洢����������!!
			sb_allNewSQL.append(str_sql_1); //
			sb_allNewSQL.append(" where "); //����where����!!

			String str_andwhere = str_sql_2.trim().substring(5, str_sql_2.length()).trim(); //
			String str_whereConsSQL = splitWhereCondition(str_andwhere, al_ValueList); //����where����
			sb_allNewSQL.append(str_whereConsSQL); //ƴ����whereת�����

			al_ValueList.add(0, sb_allNewSQL.toString()); //��ǰ��ĵ�һ������ת�����SQL
			return al_ValueList; //
		} catch (Exception ex) {
			System.err.println("Prepare����SQL[" + _sql + "]�����쳣[" + ex.getClass().getName() + ":" + ex.getMessage() + "]"); //
			ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * �ָ�Insert Values���������!!
	 * @param _sql
	 * @param _newSQL
	 * @param _list
	 * @return
	 */
	private String splitInsertValueSQL(String _sql, StringBuilder _newSQL, ArrayList _list) {
		boolean isStrValue = _sql.startsWith("'"); //���Ƿ����ַ�����ʼ!
		if (isStrValue) { //����ǵ�����!!��Ѱ����һ��������!!!
			int li_nextpos = findPos(0, _sql.substring(1, _sql.length())); //��һ�������ŵ�λ��!!
			String str_item = _sql.substring(1, li_nextpos + 1); //��һ�ε�����!!
			if ((li_nextpos + 1) < _sql.length() - 1) { //������ǽ�β,��Ҫ�ݹ��������!!
				_newSQL.append("?,"); //
				str_item = tbUtil.replaceAll(str_item, "''", "'"); //��������������ŵ���Ҫ���������һ��!
				_list.add(str_item); //
				String str_newsubfix = _sql.substring(li_nextpos + 2, _sql.length()); //
				str_newsubfix = str_newsubfix.trim(); //ȥ�ո�!!
				str_newsubfix = str_newsubfix.substring(1, str_newsubfix.length()); //�����!!!
				return splitInsertValueSQL(str_newsubfix, _newSQL, _list); //�ݹ����
			} else { //����ǽ�β��!!
				_newSQL.append("?"); //
				str_item = tbUtil.replaceAll(str_item, "''", "'"); //��������������ŵ���Ҫ���������һ��!
				_list.add(str_item); //
				return _newSQL.toString(); //
			}
		} else { //������ǵ����ſ�ͷ,��Ѱ����һ������!!
			int li_pos_2 = _sql.indexOf(","); //������һ������λ��,�������������,����values���к���(����:to_char(sysdate,'YYYY-MM-DD')),�����Ժ���Ҫ�ٸ�һ���㷨!׼���ҵ���һ������!
			if (li_pos_2 > 0) { //����ж���,��ݹ����!!!
				String str_item = _sql.substring(0, li_pos_2).trim(); //��һ�ε�����,��Ϊ������������Ҫ��trim��ȥ�ո�!!!
				_newSQL.append("?,"); //�µ�SQL!
				if (str_item.equalsIgnoreCase("null")) {
					_list.add(null); //
				} else {
					_list.add(new Double(str_item)); //
				}
				String str_newsubfix = _sql.substring(li_pos_2 + 1, _sql.length()).trim(); //
				return splitInsertValueSQL(str_newsubfix, _newSQL, _list); //�ݹ����
			} else { //���û����,��˵��������
				String str_item = _sql.substring(0, _sql.length()).trim(); //��һ�ε�����!!
				_newSQL.append("?"); //�µ�SQL!!!
				if (str_item.equalsIgnoreCase("null")) {
					_list.add(null); //
				} else {
					_list.add(new Double(str_item)); //
				}
				return _newSQL.toString(); //
			}
		}
	}

	/**
	 * ����update�е�set���ֵ�����!!
	 * @param _sql
	 * @param _newSQL
	 * @param _list
	 */
	private String splitSetSQL(String _sql, StringBuilder _newSQL, ArrayList _list) {
		int li_pos = _sql.indexOf("="); //
		String str_1 = _sql.substring(0, li_pos).trim(); //�ֶ���!!
		String str_2 = _sql.substring(li_pos + 1, _sql.length()); //
		str_2 = str_2.trim(); //ȥ���ո�!!!
		if (str_2.startsWith("'")) { //����ǵ����ſ�ͷ,��˵�����ַ���
			int li_pos_1 = findPos(0, str_2.substring(1, str_2.length())); //Ѱ��λ��
			String str_item = str_2.substring(1, li_pos_1 + 1); //��һ�ε�����!!
			//System.out.println("��һ�ε��ֶ���[" + str_1 + "],����[" + str_item + "]"); //
			if ((li_pos_1 + 1) < str_2.length() - 1) { //������ǽ�β��,�����滹��!��ݹ��������!!!
				_newSQL.append(str_1 + "=?,"); //�µ�SQL!!!
				str_item = tbUtil.replaceAll(str_item, "''", "'"); //��������������ŵ���Ҫ���������һ��!
				_list.add(str_item); //����
				String str_newsubfix = str_2.substring(li_pos_1 + 2, str_2.length()); //
				str_newsubfix = str_newsubfix.trim(); //ȥ�ո�!!
				str_newsubfix = str_newsubfix.substring(1, str_newsubfix.length()); //�����!!!
				return splitSetSQL(str_newsubfix, _newSQL, _list); //�ݹ����
			} else {
				_newSQL.append(str_1 + "=?"); //�µ�SQL!!!
				str_item = tbUtil.replaceAll(str_item, "''", "'"); //��������������ŵ���Ҫ���������һ��!
				_list.add(str_item); //����
				return _newSQL.toString(); //
			}
		} else { //˵��������!!
			int li_pos_2 = str_2.indexOf(","); //����û�ж���!
			String str_item = null; //
			if (li_pos_2 > 0) { //����ж���!
				str_item = str_2.substring(0, li_pos_2).trim(); //��һ�ε�����!!
				//System.out.println("��һ�ε��ֶ���[" + str_1 + "],����[" + str_item + "]"); //
				_newSQL.append(str_1 + "=?,"); //�µ�SQL!
				if (str_item.equalsIgnoreCase("null")) {
					_list.add(null); //
				} else {
					_list.add(new Double(str_item)); //����!!
				}
				String str_newsubfix = str_2.substring(li_pos_2 + 1, str_2.length()).trim(); //
				return splitSetSQL(str_newsubfix, _newSQL, _list); //�ݹ��������!!
			} else { //���û�ж���,�����滹��!
				str_item = str_2.substring(0, str_2.length()).trim(); //��һ�ε�����!!
				//System.out.println("��һ�ε��ֶ���[" + str_1 + "],����[" + str_item + "]"); //
				_newSQL.append(str_1 + "=?"); //�µ�SQL!
				if (str_item.equalsIgnoreCase("null")) {
					_list.add(null); //
				} else {
					_list.add(new Double(str_item)); //����!!
				}
				return _newSQL.toString(); //
			}
		}
	}

	//�ݹ��㷨,��ͣ��Ѱ�ҵ������ĵ�һ��������!!!
	private int findPos(int _oldLen, String _sql) {
		int li_pos = _sql.indexOf("'"); //�ҵ���һ��������!!
		//System.out.println("λ��=[" + li_pos + "],�ϲ�����[" + (_oldLen + li_pos) + "]"); //
		if (li_pos != _sql.length() - 1) { //����������һ��!
			String str_next = _sql.substring(li_pos + 1, li_pos + 2); //
			//System.out.println("��һ���ַ���[" + str_next + "]"); //
			if (str_next.equals("'")) { //�����һ���ַ����ǵ����ţ����������
				//System.out.println("��һ���ַ��ǵ�����[" + str_next + "]"); //
				String str_newSQL = _sql.substring(li_pos + 2, _sql.length()); //
				//System.out.println("�µ�SQL[" + str_newSQL + "]"); //
				return findPos(_oldLen + li_pos + 2, str_newSQL); //
			} else {
				//System.out.println("��һ���ַ����ǵ�����!!!"); //
				return _oldLen + li_pos; //
			}
		} else {
			return _oldLen + li_pos; //
		}
	}

	/**
	 * ����where����,�ǳ��ؼ��Ĵ���,��Ϊ����insert�⣬�������ֶ���where�������������
	 * ���ڵ�˼·�Ǹ���and/orΪ�ָ�,�õ�һ�����ĵ�������(between ����������),Ȼ��ֱ���!
	 * ���㷨��©��,���ĳ�������о���and or������,����ܱ���!!!
	 * @param _sql
	 * @param _listValue
	 * @return
	 */
	private String splitWhereCondition(String _sql, ArrayList _listValue) {
		int li_groupby = _sql.toLowerCase().indexOf("group by"); //����
		int li_orderby = _sql.toLowerCase().indexOf("order by"); //����
		String str_group_order_by = null; //
		if (li_groupby > 0) {
			str_group_order_by = _sql.substring(li_groupby, _sql.length()); //
			_sql = _sql.substring(0, li_groupby); //
		} else if (li_orderby > 0) {
			str_group_order_by = _sql.substring(li_orderby, _sql.length()); //
			_sql = _sql.substring(0, li_orderby); //
		}

		String[] str_compare = new String[] { ">=", "<=", "<>", "!=", ">", "<", "=", " in", " like " }; //
		ArrayList al_items = new ArrayList(); //
		splitSQLByAndOr(_sql, al_items); //����and��or�ָ�
		StringBuilder sb_cons = new StringBuilder(); //
		for (int i = 0; i < al_items.size(); i++) {
			//System.out.println("��[" + i + "]��=[" + al_items.get(i) + "]"); //
			String str_item = (String) al_items.get(i); //
			if (str_item.trim().equalsIgnoreCase("and") || str_item.trim().equalsIgnoreCase("or")) { //��������Ӵ�,��ֱ�Ӽ���!
				sb_cons.append(str_item); //
				continue; //
			}
			//ĳһ��ԭ������,��=,>,<,like in,��....
			String str_item_trimed = tbUtil.replaceAll(str_item, " ", ""); //
			str_item_trimed = tbUtil.replaceAll(str_item_trimed, "(", ""); //
			str_item_trimed = tbUtil.replaceAll(str_item_trimed, "(", ""); //
			//			if (str_item_trimed.equals("1=1") || str_item_trimed.equals("1=2") || str_item_trimed.equals("99=99")) {
			//				sb_cons.append(str_item); //
			//				continue; //
			//			}
			int li_pos_trimed = str_item_trimed.indexOf("="); //
			if (li_pos_trimed > 0) {
				String str_item_trimed_1 = str_item_trimed.substring(0, li_pos_trimed); //
				String str_item_trimed_2 = str_item_trimed.substring(li_pos_trimed + 1, str_item_trimed.length()); //
				if (str_item_trimed_1.equals(str_item_trimed_2)) {
					sb_cons.append(str_item); //
					continue; //
				}
			}

			int li_realpos = -1; //
			String str_cmp = null; //
			for (int j = 0; j < str_compare.length; j++) {
				int li_pos = str_item.toLowerCase().indexOf(str_compare[j]); //
				if (li_pos > 0) {
					li_realpos = li_pos; //
					str_cmp = str_compare[j]; //
					break; //
				}
			}
			if (li_realpos < 0) {
				sb_cons.append(str_item); //ֱ�Ӽ���,��������!
			} else {
				String str_1 = str_item.substring(0, li_realpos); ////
				String str_2 = str_item.substring(li_realpos + str_cmp.length(), str_item.length()); ////
				if (str_cmp.equals(" in")) { //in����!!���in�������о������ţ�����㷨�в�ͨ!!
					sb_cons.append(str_1); //
					sb_cons.append(str_cmp); //
					int li_pos_a = str_2.indexOf("("); //
					int li_pos_b = str_2.indexOf(")"); //
					String str_convalue = str_2.substring(li_pos_a + 1, li_pos_b); //
					String[] str_values = tbUtil.split(str_convalue, ","); //�ָ�!!!
					sb_cons.append("("); //
					for (int k = 0; k < str_values.length; k++) {
						sb_cons.append("?"); //ƴ��!!
						if (k != str_values.length - 1) {
							sb_cons.append(","); //ƴ��!!
						}
						str_values[k] = str_values[k].trim(); //ȥ�ո�!!
						if (str_values[k].startsWith("'")) { //����ǵ�����
							_listValue.add(str_values[k].substring(1, str_values[k].length() - 1)); //
						} else {
							_listValue.add(new Double(str_values[k])); //
						}
					}
					sb_cons.append(")"); //
					sb_cons.append(str_2.substring(li_pos_b + 1, str_2.length())); //���Ϻ����ʣ�࣬���������ŵ�!!
				} else if (str_cmp.equals(" like ")) { //like����
					sb_cons.append(str_1); //
					sb_cons.append(str_cmp); //
					String str_remain = str_2.substring(str_2.indexOf("'") + 1, str_2.length()); //
					String str_listvalue = str_remain.substring(0, str_remain.lastIndexOf("'")); //�������
					String str_leftappend = str_remain.substring(str_remain.lastIndexOf("'") + 1, str_remain.length()); //
					sb_cons.append("?"); //ƴ��!![like$" + str_remain + "]
					sb_cons.append(str_leftappend); //���Ϻ����ʣ�࣬���������ŵ�!!
					str_listvalue = tbUtil.replaceAll(str_listvalue, "''", "'"); //
					_listValue.add(str_listvalue); //
				} else { //>,=��������
					if (str_2.indexOf("'") >= 0) { //������ַ���
						sb_cons.append(str_1); //
						sb_cons.append(str_cmp); //
						String str_remain = str_2.substring(str_2.indexOf("'") + 1, str_2.length()); //
						String str_listvalue = str_remain.substring(0, str_remain.lastIndexOf("'")); //�������
						String str_leftappend = str_remain.substring(str_remain.lastIndexOf("'") + 1, str_remain.length()); //
						sb_cons.append("?"); //ƴ��!!
						sb_cons.append(str_leftappend); //���Ϻ����ʣ�࣬���������ŵ�!!
						_listValue.add(str_listvalue); //
					} else { //���������,����Ҫ��������!!
						int li_gh_right = str_2.indexOf(")"); //
						if (li_gh_right > 0) { //�����������!
							String str_numitem = str_2.substring(0, li_gh_right).trim(); //
							if (tbUtil.isStrAllNunbers(str_numitem)) { //���ֱ�������ֲ���,������ǹ����ֶ�!!!
								sb_cons.append(str_1); //
								sb_cons.append(str_cmp); //
								sb_cons.append("?"); //ƴ��!!
								sb_cons.append(str_2.substring(li_gh_right, str_2.length())); //
								_listValue.add(new Double(str_numitem)); //
							} else {
								sb_cons.append(str_item); //ֱ�Ӽ���,��������!
							}
						} else {
							String str_numitem = str_2.substring(0, str_2.length()).trim(); //
							if (tbUtil.isStrAllNunbers(str_numitem)) { //���ֱ�������ֲ���,������ǹ����ֶ�!!!
								sb_cons.append(str_1); //
								sb_cons.append(str_cmp); //
								sb_cons.append("?"); //ƴ��!!
								_listValue.add(new Double(str_numitem)); //
							} else {
								sb_cons.append(str_item); //ֱ�Ӽ���,��������!
							}
						}
					}
				}
			}

		}
		if (str_group_order_by != null) {
			sb_cons.append(" " + str_group_order_by); //���!!!
		}
		return sb_cons.toString(); //
	}

	/**
	 * ���� and / or���ָ�SQL
	 * @param _sql
	 * @param _alItems
	 */
	private void splitSQLByAndOr(String _sql, ArrayList _alItems) {
		int li_pos_and = _sql.toLowerCase().indexOf(" and "); //andλ��
		int li_pos_or = _sql.toLowerCase().indexOf(" or "); //andλ��
		if (li_pos_and < 0 && li_pos_or < 0) { //���û�оͷ���!!
			_alItems.add(_sql); //����
			return; //
		}
		boolean isAnd = true; //
		if (li_pos_and > 0 && li_pos_or > 0) { //�������,����С��!
			isAnd = (li_pos_and < li_pos_or ? true : false); //��С��!!!
		} else {
			if (li_pos_and > 0) { //�����and
				isAnd = true; //
			} else {
				isAnd = false; //
			}
		}
		if (isAnd) { //�����and����!!
			_alItems.add(_sql.substring(0, li_pos_and)); //
			_alItems.add(_sql.substring(li_pos_and, li_pos_and + 5)); //
			String str_remain = _sql.substring(li_pos_and + 5, _sql.length()); //
			splitSQLByAndOr(str_remain, _alItems); //
		} else { //or����!!
			_alItems.add(_sql.substring(0, li_pos_or)); //
			_alItems.add(_sql.substring(li_pos_or, li_pos_or + 4)); //
			String str_remain = _sql.substring(li_pos_or + 4, _sql.length()); //
			splitSQLByAndOr(str_remain, _alItems); //
		}
	}

	/**
	 * where SENAME='S_PUB_THREADMONITOR' and (CURRVALUE=254140 or id>100) and name like '%=''hh''%'
	 * @param args
	 */
	public static void main(String[] args) {
		long ll_1 = System.currentTimeMillis();
		String str_initsql = "update PUB_USER SET code='bb''  d,d'',dd,sex=''YY''hh',name='BBB=G,HJ,HK',sex = 'W' ,age= 28 , tmp= 9 where SENAME='S_PUB_THREADMONITOR' AND (CURRVALUE=254140 or id>'100') and (date between '2006' and 2007) and name like '%�Ϲ�%' and  id in (25,158) order by id,name"; //
		//String str_initsql = "Delete from  PUB_USER where (SENAME='S_PUB_THREADMONITOR') AND (CURRVALUE=254140 or id>'100')  and name like '%�Ϲ�%') and  id in ('15','16')) and pk is null"; //
		//String str_initsql = "select * from  PUB_USER where (SENAME='S_PUB_THREADMONITOR') AND (CURRVALUE=254140 or id>'100')  and name like '%�Ϲ�%') and  id in ('15','16')) and pk is null"; //
		//String str_initsql = "insert into pub_user (id,code,name) values ('253594','9001','2011-08-30 10:13:42','20110830090643','8','12','20','20','1','1','1','0','0','0','939','0','1','0','0','0','0','0','2','1','2')"; //
		System.out.println("ԭʼSQL[" + str_initsql + "]"); //
		ArrayList al_return = new PrepareSQLUtil().prepareSQL(str_initsql); //
		long ll_2 = System.currentTimeMillis();
		System.out.println("������ʱ[" + (ll_2 - ll_1) + "]"); //
		for (int i = 0; i < al_return.size(); i++) {
			System.out.println("��[" + i + "]��=[" + al_return.get(i) + "]"); //
		}
	}

}
