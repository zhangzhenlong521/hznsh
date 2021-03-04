package cn.com.infostrategy.bs.common;

import java.util.ArrayList;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * 中外运项目中客户科技部要求所有SQL必须是变量替换预编译的方式，即使用PrepareStatement,所以做了这一一个工具!!!
 * 该工具就是逆向解析SQL,硬计算出有几个变量,但实际上理论上有些是解析不了的,比如where条件中就有and/or等!
 * 但如果每次都从调用处构造SQL与变量列表，则开发起来又非常不方便!! 而且以前所有的代码都要改,工作量太大!
 * 所以折衷考虑，因为大多数是可以解析成功的，为了提高开发效率，所以还是统一使用该类反向解析!!
 * 另外平台本身还有参数开关决定是否启用该解析，而大多数据客户又不严格要求这么做(中外运是第一个),所以这样做成本是最低的!
 * @author xch
 *
 */
public class PrepareSQLUtil {

	TBUtil tbUtil = new TBUtil(); //

	/**
	 * 解析一个SQL,转换成prepare的变量替换预编译的形式!!!
	 * @param _sql
	 * @return 返回的就是一个ArrayList,其中第一个就是转换后的带问号的SQL,后面的则依次是替换的变量!!!
	 */
	public ArrayList prepareSQL(String _sql) {
		String str_lowersql = _sql.toLowerCase().trim(); //
		if (str_lowersql.startsWith("select ")) { //如果是select开头的!
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
	 * 解析Select语句!!
	 * @param _sql
	 * @return
	 */
	public ArrayList prepareSelectSQL(String _sql) {
		return prepareSelectOrDeleteSQL(_sql); //
	}

	/**
	 * 解析Insert语句!
	 * @param _sql
	 * @return
	 */
	public ArrayList prepareInsertSQL(String _sql) {
		try {
			int li_pos_values = _sql.toLowerCase().indexOf("values"); //先找到values的地方!!
			if (li_pos_values < 0) { //如果没有values的则不做处理!!!比如insert pub_user select * from ...等语法
				return null; //
			}
			String str_sql_1 = _sql.substring(0, li_pos_values); //values前面的
			String str_sql_2 = _sql.substring(li_pos_values + 6, _sql.length()).trim(); //values后面的
			StringBuilder sb_allNewSQL = new StringBuilder(); //

			sb_allNewSQL.append(str_sql_1); //
			sb_allNewSQL.append(" values ("); // 
			if (str_sql_2.endsWith(";")) {
				str_sql_2 = str_sql_2.substring(0, str_sql_2.length() - 1); //
			}

			ArrayList al_ValueList = new ArrayList(); //存储变量的内容!!
			String str_valuesSQL = splitInsertValueSQL(str_sql_2.substring(1, str_sql_2.length() - 1), new StringBuilder(), al_ValueList); //
			sb_allNewSQL.append(str_valuesSQL); //加上右括号!
			sb_allNewSQL.append(")"); //加上右括号!

			al_ValueList.add(0, sb_allNewSQL.toString()); //
			return al_ValueList; //
		} catch (Exception ex) {
			System.err.println("Prepare解析SQL[" + _sql + "]发生异常[" + ex.getClass().getName() + ":" + ex.getMessage() + "]"); //
			//ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * 解析Update语句
	 * @param _sql
	 * @return
	 */
	public ArrayList prepareUpdateSQL(String _sql) {
		try {
			int li_pos_where = _sql.toLowerCase().indexOf(" where "); //先找到where的地方!!
			if (li_pos_where < 0) { //如果没有where的则不做处理!!!比如直接设置某个值!
				return null; //
			}
			String str_sql_1 = _sql.substring(0, li_pos_where); //前面的
			String str_sql_2 = _sql.substring(li_pos_where, _sql.length()).trim(); //后面的
			int li_pos_set = str_sql_1.toLowerCase().indexOf(" set "); //找Set

			StringBuilder sb_allNewSQL = new StringBuilder(); //
			sb_allNewSQL.append(str_sql_1.substring(0, li_pos_set).trim() + " set "); //

			//where前的内容
			ArrayList al_ValueList = new ArrayList(); //存储变量的内容!!
			String str_setNewSQL = splitSetSQL(str_sql_1.substring(li_pos_set + 5, str_sql_1.length()).trim(), new StringBuilder(), al_ValueList); //
			sb_allNewSQL.append(str_setNewSQL); //拼接上set条件!!

			//where后的内容
			sb_allNewSQL.append(" where "); //加上where条件!!

			String str_andwhere = str_sql_2.trim().substring(5, str_sql_2.length()).trim(); //
			String str_whereConsSQL = splitWhereCondition(str_andwhere, al_ValueList); //解析where条件
			sb_allNewSQL.append(str_whereConsSQL); //拼接上where转换后的

			al_ValueList.add(0, sb_allNewSQL.toString()); //最前面的第一个就是转换后的SQL
			return al_ValueList; //
		} catch (Exception ex) {
			System.err.println("Prepare解析SQL[" + _sql + "]发生异常[" + ex.getClass().getName() + ":" + ex.getMessage() + "]"); //
			//ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * 解析Delete语句!
	 * @param _sql
	 * @return
	 */
	public ArrayList prepareDeleteSQL(String _sql) {
		return prepareSelectOrDeleteSQL(_sql); //
	}

	/**
	 * select与delete的原理一样,即只需处理where后面的条件,所以包装成了一个方法!!!
	 * @param _sql
	 * @return
	 */
	private ArrayList prepareSelectOrDeleteSQL(String _sql) {
		try {
			int li_pos_where = _sql.toLowerCase().indexOf(" where "); //先找到where的地方!!
			if (li_pos_where < 0) { //如果没有where的则不做处理!!!比如直接设置某个值!
				return null; //
			}
			String str_sql_1 = _sql.substring(0, li_pos_where); //前面的
			String str_sql_2 = _sql.substring(li_pos_where, _sql.length()).trim(); //后面的

			StringBuilder sb_allNewSQL = new StringBuilder(); //
			ArrayList al_ValueList = new ArrayList(); //存储变量的内容!!
			sb_allNewSQL.append(str_sql_1); //
			sb_allNewSQL.append(" where "); //加上where条件!!

			String str_andwhere = str_sql_2.trim().substring(5, str_sql_2.length()).trim(); //
			String str_whereConsSQL = splitWhereCondition(str_andwhere, al_ValueList); //解析where条件
			sb_allNewSQL.append(str_whereConsSQL); //拼接上where转换后的

			al_ValueList.add(0, sb_allNewSQL.toString()); //最前面的第一个就是转换后的SQL
			return al_ValueList; //
		} catch (Exception ex) {
			System.err.println("Prepare解析SQL[" + _sql + "]发生异常[" + ex.getClass().getName() + ":" + ex.getMessage() + "]"); //
			ex.printStackTrace(); //
			return null; //
		}
	}

	/**
	 * 分割Insert Values后面的内容!!
	 * @param _sql
	 * @param _newSQL
	 * @param _list
	 * @return
	 */
	private String splitInsertValueSQL(String _sql, StringBuilder _newSQL, ArrayList _list) {
		boolean isStrValue = _sql.startsWith("'"); //看是否是字符串开始!
		if (isStrValue) { //如果是单引号!!则寻找下一个单引号!!!
			int li_nextpos = findPos(0, _sql.substring(1, _sql.length())); //下一个单引号的位置!!
			String str_item = _sql.substring(1, li_nextpos + 1); //这一段的内容!!
			if ((li_nextpos + 1) < _sql.length() - 1) { //如果不是结尾,则要递归继续查找!!
				_newSQL.append("?,"); //
				str_item = tbUtil.replaceAll(str_item, "''", "'"); //如果有两个单引号的则要反过来变成一个!
				_list.add(str_item); //
				String str_newsubfix = _sql.substring(li_nextpos + 2, _sql.length()); //
				str_newsubfix = str_newsubfix.trim(); //去空格!!
				str_newsubfix = str_newsubfix.substring(1, str_newsubfix.length()); //后面的!!!
				return splitInsertValueSQL(str_newsubfix, _newSQL, _list); //递归调用
			} else { //如果是结尾的!!
				_newSQL.append("?"); //
				str_item = tbUtil.replaceAll(str_item, "''", "'"); //如果有两个单引号的则要反过来变成一个!
				_list.add(str_item); //
				return _newSQL.toString(); //
			}
		} else { //如果不是单引号开头,则寻找下一个逗号!!
			int li_pos_2 = _sql.indexOf(","); //计算下一个逗号位置,这里可能有隐患,比如values中有函数(比如:to_char(sysdate,'YYYY-MM-DD')),所以以后需要再搞一个算法!准备找到下一个逗号!
			if (li_pos_2 > 0) { //如果有逗号,则递归调用!!!
				String str_item = _sql.substring(0, li_pos_2).trim(); //这一段的内容,因为是数字所以需要做trim截去空格!!!
				_newSQL.append("?,"); //新的SQL!
				if (str_item.equalsIgnoreCase("null")) {
					_list.add(null); //
				} else {
					_list.add(new Double(str_item)); //
				}
				String str_newsubfix = _sql.substring(li_pos_2 + 1, _sql.length()).trim(); //
				return splitInsertValueSQL(str_newsubfix, _newSQL, _list); //递归调用
			} else { //如果没逗号,则说明结束了
				String str_item = _sql.substring(0, _sql.length()).trim(); //这一段的内容!!
				_newSQL.append("?"); //新的SQL!!!
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
	 * 处理update中的set部分的内容!!
	 * @param _sql
	 * @param _newSQL
	 * @param _list
	 */
	private String splitSetSQL(String _sql, StringBuilder _newSQL, ArrayList _list) {
		int li_pos = _sql.indexOf("="); //
		String str_1 = _sql.substring(0, li_pos).trim(); //字段名!!
		String str_2 = _sql.substring(li_pos + 1, _sql.length()); //
		str_2 = str_2.trim(); //去掉空格!!!
		if (str_2.startsWith("'")) { //如果是单引号开头,则说明是字符串
			int li_pos_1 = findPos(0, str_2.substring(1, str_2.length())); //寻找位置
			String str_item = str_2.substring(1, li_pos_1 + 1); //这一段的内容!!
			//System.out.println("这一段的字段名[" + str_1 + "],内容[" + str_item + "]"); //
			if ((li_pos_1 + 1) < str_2.length() - 1) { //如果不是结尾的,即后面还有!则递归继续查找!!!
				_newSQL.append(str_1 + "=?,"); //新的SQL!!!
				str_item = tbUtil.replaceAll(str_item, "''", "'"); //如果有两个单引号的则要反过来变成一个!
				_list.add(str_item); //加入
				String str_newsubfix = str_2.substring(li_pos_1 + 2, str_2.length()); //
				str_newsubfix = str_newsubfix.trim(); //去空格!!
				str_newsubfix = str_newsubfix.substring(1, str_newsubfix.length()); //后面的!!!
				return splitSetSQL(str_newsubfix, _newSQL, _list); //递归调用
			} else {
				_newSQL.append(str_1 + "=?"); //新的SQL!!!
				str_item = tbUtil.replaceAll(str_item, "''", "'"); //如果有两个单引号的则要反过来变成一个!
				_list.add(str_item); //加入
				return _newSQL.toString(); //
			}
		} else { //说明是数字!!
			int li_pos_2 = str_2.indexOf(","); //看有没有逗号!
			String str_item = null; //
			if (li_pos_2 > 0) { //如果有逗号!
				str_item = str_2.substring(0, li_pos_2).trim(); //这一段的内容!!
				//System.out.println("这一段的字段名[" + str_1 + "],内容[" + str_item + "]"); //
				_newSQL.append(str_1 + "=?,"); //新的SQL!
				if (str_item.equalsIgnoreCase("null")) {
					_list.add(null); //
				} else {
					_list.add(new Double(str_item)); //数字!!
				}
				String str_newsubfix = str_2.substring(li_pos_2 + 1, str_2.length()).trim(); //
				return splitSetSQL(str_newsubfix, _newSQL, _list); //递归继续查找!!
			} else { //如果没有逗号,即后面还有!
				str_item = str_2.substring(0, str_2.length()).trim(); //这一段的内容!!
				//System.out.println("这一段的字段名[" + str_1 + "],内容[" + str_item + "]"); //
				_newSQL.append(str_1 + "=?"); //新的SQL!
				if (str_item.equalsIgnoreCase("null")) {
					_list.add(null); //
				} else {
					_list.add(new Double(str_item)); //数字!!
				}
				return _newSQL.toString(); //
			}
		}
	}

	//递归算法,不停的寻找到真正的第一个单引号!!!
	private int findPos(int _oldLen, String _sql) {
		int li_pos = _sql.indexOf("'"); //找到第一个单引号!!
		//System.out.println("位置=[" + li_pos + "],合并后是[" + (_oldLen + li_pos) + "]"); //
		if (li_pos != _sql.length() - 1) { //如果不是最后一个!
			String str_next = _sql.substring(li_pos + 1, li_pos + 2); //
			//System.out.println("下一个字符是[" + str_next + "]"); //
			if (str_next.equals("'")) { //如果下一个字符又是单引号，则继续查找
				//System.out.println("下一个字符是单引号[" + str_next + "]"); //
				String str_newSQL = _sql.substring(li_pos + 2, _sql.length()); //
				//System.out.println("新的SQL[" + str_newSQL + "]"); //
				return findPos(_oldLen + li_pos + 2, str_newSQL); //
			} else {
				//System.out.println("下一个字符不是单引号!!!"); //
				return _oldLen + li_pos; //
			}
		} else {
			return _oldLen + li_pos; //
		}
	}

	/**
	 * 处理where条件,非常关键的处理,因为除了insert外，其他三种都有where条件处理的需求
	 * 现在的思路是根据and/or为分隔,得到一个个的单个条件(between 可能有问题),然后分别处理!
	 * 该算法有漏洞,如果某个条件中就有and or的内容,则可能报错!!!
	 * @param _sql
	 * @param _listValue
	 * @return
	 */
	private String splitWhereCondition(String _sql, ArrayList _listValue) {
		int li_groupby = _sql.toLowerCase().indexOf("group by"); //分组
		int li_orderby = _sql.toLowerCase().indexOf("order by"); //排序
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
		splitSQLByAndOr(_sql, al_items); //根据and与or分隔
		StringBuilder sb_cons = new StringBuilder(); //
		for (int i = 0; i < al_items.size(); i++) {
			//System.out.println("第[" + i + "]项=[" + al_items.get(i) + "]"); //
			String str_item = (String) al_items.get(i); //
			if (str_item.trim().equalsIgnoreCase("and") || str_item.trim().equalsIgnoreCase("or")) { //如果是连接串,则直接加入!
				sb_cons.append(str_item); //
				continue; //
			}
			//某一个原子条件,即=,>,<,like in,等....
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
				sb_cons.append(str_item); //直接加入,不做处理!
			} else {
				String str_1 = str_item.substring(0, li_realpos); ////
				String str_2 = str_item.substring(li_realpos + str_cmp.length(), str_item.length()); ////
				if (str_cmp.equals(" in")) { //in条件!!如果in的条件中就有括号，则该算法行不通!!
					sb_cons.append(str_1); //
					sb_cons.append(str_cmp); //
					int li_pos_a = str_2.indexOf("("); //
					int li_pos_b = str_2.indexOf(")"); //
					String str_convalue = str_2.substring(li_pos_a + 1, li_pos_b); //
					String[] str_values = tbUtil.split(str_convalue, ","); //分隔!!!
					sb_cons.append("("); //
					for (int k = 0; k < str_values.length; k++) {
						sb_cons.append("?"); //拼接!!
						if (k != str_values.length - 1) {
							sb_cons.append(","); //拼接!!
						}
						str_values[k] = str_values[k].trim(); //去空格!!
						if (str_values[k].startsWith("'")) { //如果是单引号
							_listValue.add(str_values[k].substring(1, str_values[k].length() - 1)); //
						} else {
							_listValue.add(new Double(str_values[k])); //
						}
					}
					sb_cons.append(")"); //
					sb_cons.append(str_2.substring(li_pos_b + 1, str_2.length())); //加上后面的剩余，比如有括号等!!
				} else if (str_cmp.equals(" like ")) { //like条件
					sb_cons.append(str_1); //
					sb_cons.append(str_cmp); //
					String str_remain = str_2.substring(str_2.indexOf("'") + 1, str_2.length()); //
					String str_listvalue = str_remain.substring(0, str_remain.lastIndexOf("'")); //这里可能
					String str_leftappend = str_remain.substring(str_remain.lastIndexOf("'") + 1, str_remain.length()); //
					sb_cons.append("?"); //拼接!![like$" + str_remain + "]
					sb_cons.append(str_leftappend); //加上后面的剩余，比如有括号等!!
					str_listvalue = tbUtil.replaceAll(str_listvalue, "''", "'"); //
					_listValue.add(str_listvalue); //
				} else { //>,=。。。。
					if (str_2.indexOf("'") >= 0) { //如果是字符串
						sb_cons.append(str_1); //
						sb_cons.append(str_cmp); //
						String str_remain = str_2.substring(str_2.indexOf("'") + 1, str_2.length()); //
						String str_listvalue = str_remain.substring(0, str_remain.lastIndexOf("'")); //这里可能
						String str_leftappend = str_remain.substring(str_remain.lastIndexOf("'") + 1, str_remain.length()); //
						sb_cons.append("?"); //拼接!!
						sb_cons.append(str_leftappend); //加上后面的剩余，比如有括号等!!
						_listValue.add(str_listvalue); //
					} else { //如果是数字,则需要考虑括号!!
						int li_gh_right = str_2.indexOf(")"); //
						if (li_gh_right > 0) { //如果有右括号!
							String str_numitem = str_2.substring(0, li_gh_right).trim(); //
							if (tbUtil.isStrAllNunbers(str_numitem)) { //如果直的是数字才做,否则就是关联字段!!!
								sb_cons.append(str_1); //
								sb_cons.append(str_cmp); //
								sb_cons.append("?"); //拼接!!
								sb_cons.append(str_2.substring(li_gh_right, str_2.length())); //
								_listValue.add(new Double(str_numitem)); //
							} else {
								sb_cons.append(str_item); //直接加入,不做处理!
							}
						} else {
							String str_numitem = str_2.substring(0, str_2.length()).trim(); //
							if (tbUtil.isStrAllNunbers(str_numitem)) { //如果直的是数字才做,否则就是关联字段!!!
								sb_cons.append(str_1); //
								sb_cons.append(str_cmp); //
								sb_cons.append("?"); //拼接!!
								_listValue.add(new Double(str_numitem)); //
							} else {
								sb_cons.append(str_item); //直接加入,不做处理!
							}
						}
					}
				}
			}

		}
		if (str_group_order_by != null) {
			sb_cons.append(" " + str_group_order_by); //添加!!!
		}
		return sb_cons.toString(); //
	}

	/**
	 * 根据 and / or来分割SQL
	 * @param _sql
	 * @param _alItems
	 */
	private void splitSQLByAndOr(String _sql, ArrayList _alItems) {
		int li_pos_and = _sql.toLowerCase().indexOf(" and "); //and位置
		int li_pos_or = _sql.toLowerCase().indexOf(" or "); //and位置
		if (li_pos_and < 0 && li_pos_or < 0) { //如果没有就返回!!
			_alItems.add(_sql); //加入
			return; //
		}
		boolean isAnd = true; //
		if (li_pos_and > 0 && li_pos_or > 0) { //如果都有,则找小的!
			isAnd = (li_pos_and < li_pos_or ? true : false); //找小的!!!
		} else {
			if (li_pos_and > 0) { //如果是and
				isAnd = true; //
			} else {
				isAnd = false; //
			}
		}
		if (isAnd) { //如果是and条件!!
			_alItems.add(_sql.substring(0, li_pos_and)); //
			_alItems.add(_sql.substring(li_pos_and, li_pos_and + 5)); //
			String str_remain = _sql.substring(li_pos_and + 5, _sql.length()); //
			splitSQLByAndOr(str_remain, _alItems); //
		} else { //or条件!!
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
		String str_initsql = "update PUB_USER SET code='bb''  d,d'',dd,sex=''YY''hh',name='BBB=G,HJ,HK',sex = 'W' ,age= 28 , tmp= 9 where SENAME='S_PUB_THREADMONITOR' AND (CURRVALUE=254140 or id>'100') and (date between '2006' and 2007) and name like '%合规%' and  id in (25,158) order by id,name"; //
		//String str_initsql = "Delete from  PUB_USER where (SENAME='S_PUB_THREADMONITOR') AND (CURRVALUE=254140 or id>'100')  and name like '%合规%') and  id in ('15','16')) and pk is null"; //
		//String str_initsql = "select * from  PUB_USER where (SENAME='S_PUB_THREADMONITOR') AND (CURRVALUE=254140 or id>'100')  and name like '%合规%') and  id in ('15','16')) and pk is null"; //
		//String str_initsql = "insert into pub_user (id,code,name) values ('253594','9001','2011-08-30 10:13:42','20110830090643','8','12','20','20','1','1','1','0','0','0','939','0','1','0','0','0','0','0','2','1','2')"; //
		System.out.println("原始SQL[" + str_initsql + "]"); //
		ArrayList al_return = new PrepareSQLUtil().prepareSQL(str_initsql); //
		long ll_2 = System.currentTimeMillis();
		System.out.println("解析耗时[" + (ll_2 - ll_1) + "]"); //
		for (int i = 0; i < al_return.size(); i++) {
			System.out.println("第[" + i + "]项=[" + al_return.get(i) + "]"); //
		}
	}

}
