package cn.com.infostrategy.to.common;

import java.util.Comparator;

/**
 * HashVO的比较器,可以运行多个列比较,运行是否倒序,支持是否是数字!
 * new String[][]{{"seq","N","Y"}}  //对HashVO[]中的seq字段升序排序,而且是数字...第二个列表示是否是倒序,第三个列表示是否是数字!!!
 * @author xch
 *
 */
public class HashVOComparator implements Comparator {

	private String[][] str_sortColumns = null; //
	private String[] orders = null;
	private String itemKey;

	public HashVOComparator(String itemKey, String[] orders) {
		this.orders = orders;
		this.itemKey = itemKey;
	}

	public HashVOComparator(String[][] _sortColumns) {
		this.str_sortColumns = _sortColumns;
	}

	public HashVOComparator(String[][] _sortColumns, String itemKey, String[] orders) {
		this.orders = orders;
		this.itemKey = itemKey;
		this.str_sortColumns = _sortColumns;
	}

	public int compare(Object o1, Object o2) {
		HashVO hvo_1 = (HashVO) o1;
		HashVO hvo_2 = (HashVO) o2;
		if (itemKey != null && !itemKey.equals("")) {
			String str_1 = hvo_1.getStringValue(itemKey, ""); //空值当空串处理,即会排在前面
			String str_2 = hvo_2.getStringValue(itemKey, ""); //空值当空串处理,即会排在前面
			int li_pos_1 = findItemInArray(str_1, this.orders); //找位置
			int li_pos_2 = findItemInArray(str_2, this.orders); //找位置
			return li_pos_1 - li_pos_2;

		}

		int[] li_compareResult = new int[str_sortColumns.length]; //
		for (int i = 0; i < str_sortColumns.length; i++) {
			if (str_sortColumns[i][2].equals("Y")) { //如果是数字
				try {
					String str_1 = hvo_1.getStringValue(str_sortColumns[i][0], "-99999"); //空值当空串处理,即会排在前面
					String str_2 = hvo_2.getStringValue(str_sortColumns[i][0], "-99999"); //空值当空串处理,即会排在前面
					str_1 = str_1.trim();
					str_2 = str_2.trim();
					if (str_1.equals("")) {
						str_1 = "-99999"; //
					}
					if (str_2.equals("")) {
						str_2 = "-99999"; //
					}
					if (str_1.endsWith("%")) { //考虑有百分号的情况，在报表统计中有需要对百分比进行排序【xch/2012-08-27】
						str_1 = str_1.substring(0, str_1.length() - 1); //
					}
					if (str_2.endsWith("%")) {
						str_2 = str_2.substring(0, str_2.length() - 1); //
					}

					Double ld_1 = Double.parseDouble(str_1); //空值当-99999处理,即会排在前面
					Double ld_2 = Double.parseDouble(str_2); //空值当-99999处理,即会排在前面
					if (ld_1 == ld_2) {
						li_compareResult[i] = 0;
					} else {
						if (ld_1 > ld_2) {
							li_compareResult[i] = 1;
						} else {
							li_compareResult[i] = -1;
						}
					}
				} catch (Exception ex) {
					System.err.println(ex.getMessage()); //
					li_compareResult[i] = 0; //
				}
			} else {
				//如果第三列有分号,即认为是个数组,然后就按数组指定的顺序排列,而不是英文字母顺序排列！！
				//if()

				try {
					if (itemKey != null && !itemKey.equals("") && str_sortColumns[i][0].equals(itemKey)) {
						String str_1 = hvo_1.getStringValue(str_sortColumns[i][0], ""); //空值当空串处理,即会排在前面
						String str_2 = hvo_2.getStringValue(str_sortColumns[i][0], ""); //空值当空串处理,即会排在前面
						int li_pos_1 = findItemInArray(str_1, this.orders); //找位置
						int li_pos_2 = findItemInArray(str_2, this.orders); //找位置
						li_compareResult[i] = li_pos_1 - li_pos_2;
					} else {
						String str_1 = hvo_1.getStringValue(str_sortColumns[i][0], ""); //空值当空串处理,即会排在前面
						String str_2 = hvo_2.getStringValue(str_sortColumns[i][0], ""); //空值当空串处理,即会排在前面
						str_1 = new String(str_1.getBytes("GBK"), "ISO-8859-1"); //转一下
						str_2 = new String(str_2.getBytes("GBK"), "ISO-8859-1"); //转一下
						li_compareResult[i] = str_1.compareTo(str_2);
					}

					//
				} catch (Exception ex) {
					System.err.println(ex.getMessage()); //
					li_compareResult[i] = 0; //
				}
			}

			if (str_sortColumns[i][1].equalsIgnoreCase("Y")) { //是否倒序
				li_compareResult[i] = 0 - li_compareResult[i]; //
			}

			if (li_compareResult[i] != 0) {
				return li_compareResult[i]; //如果已经比较出来了,则直接返回!!!即没必要继续下一个比较了!!,否则进行下一个的比较!!
			}
		}

		return 0; //返回0
	}

	/**
	 * 找一个值在一个数组中的位置
	 * @param _item
	 * @param _array
	 * @return
	 */
	private int findItemInArray(String _item, String[] _array) {
		if (_item == null) {
			return -1; //如果是空值,则永远放在第一位
		}
		for (int i = 0; i < _array.length; i++) {
			if (_item.equals(_array[i])) {
				return i; //
			}
		}
		return 99999; //如果该值没有在排序的项中出现，则搞个特别大的值，永远放在后面出现!!比如空值什么的!!
	}
}
