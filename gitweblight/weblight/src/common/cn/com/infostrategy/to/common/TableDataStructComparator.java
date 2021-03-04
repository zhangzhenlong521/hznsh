package cn.com.infostrategy.to.common;

import java.util.Comparator;

/**
 * HashVO的比较器,可以运行多个列比较,运行是否倒序,支持是否是数字!
 * new String[][]{{"seq","N","Y"}}  //对HashVO[]中的seq字段升序排序,而且是数字...
 * @author xch
 *
 */
public class TableDataStructComparator implements Comparator {

	private String[] str_headNames = null;
	private String[][] str_sortColumns = null; //

	public TableDataStructComparator(String[] _headNames, String[][] _sortColumns) {
		this.str_headNames = _headNames; //列头
		this.str_sortColumns = _sortColumns; //
	}

	public int compare(Object o1, Object o2) {
		String[] bodyRowData_1 = (String[]) o1; //
		String[] bodyRowData_2 = (String[]) o2; //
		int[] li_compareResult = new int[str_sortColumns.length]; //
		for (int i = 0; i < str_sortColumns.length; i++) {
			int li_index = getIndex(str_sortColumns[i][0]); //
			if (li_index < 0) {
				li_compareResult[i] = 0; //如果没找到位置(即没这项值),则表示相等!!!
				continue; //继续下一循环
			}

			if (str_sortColumns[i][2].equals("Y")) { //如果是数字
				double ld_1 = ((bodyRowData_1[li_index] == null || bodyRowData_1[li_index].trim().equals("")) ? -99999 : Double.valueOf(bodyRowData_1[li_index])); //空值当-99999处理,即会排在前面
				double ld_2 = ((bodyRowData_2[li_index] == null || bodyRowData_2[li_index].trim().equals("")) ? -99999 : Double.valueOf(bodyRowData_2[li_index])); //空值当-99999处理,即会排在前面
				if (ld_1 == ld_2) {
					li_compareResult[i] = 0;
				} else {
					if (ld_1 > ld_2) {
						li_compareResult[i] = 1;
					} else {
						li_compareResult[i] = -1;
					}
				}
			} else {
				try {
					String str_1 = (bodyRowData_1[li_index] == null ? "" : bodyRowData_1[li_index]); //空值当空串处理,即会排在前面
					String str_2 = (bodyRowData_2[li_index] == null ? "" : bodyRowData_2[li_index]); //空值当空串处理,即会排在前面
					str_1 = new String(str_1.getBytes("GBK"), "ISO-8859-1"); //转一下
					str_2 = new String(str_2.getBytes("GBK"), "ISO-8859-1"); //转一下
					li_compareResult[i] = str_1.compareTo(str_2); //
				} catch (Exception ex) {
					ex.printStackTrace(); //
					li_compareResult[i] = 0; //
				}
			}

			if (str_sortColumns[i][1].equalsIgnoreCase("Y")) {
				li_compareResult[i] = 0 - li_compareResult[i]; //
			}
		}

		for (int i = 0; i < li_compareResult.length; i++) { //
			if (li_compareResult[i] != 0) { //从第一个开始,如果不相等,则立即返回!!
				return li_compareResult[i];
			}
		}
		return 0;
	}

	private int getIndex(String _sortItemName) {
		for (int i = 0; i < str_headNames.length; i++) {
			if (str_headNames[i].equalsIgnoreCase(_sortItemName)) {
				return i;
			}
		}
		return -1;
	}
}
