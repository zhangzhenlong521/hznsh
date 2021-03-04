package cn.com.infostrategy.to.mdata;

import java.util.Comparator;

/**
 * 列表查询时需要进行排序,这是数字的比较器.
 * @author xch
 *
 */
public class BillListDataComparator implements Comparator {

	private String[][] str_sortCols = null; //排序的列,一共3列!!第1列是表示坐标,即第几位;第2列表示是否是倒序;第3列表示是否是数字!!!!比如{"3","Y","N"}

	public BillListDataComparator(String[][] _sortCols) {
		this.str_sortCols = _sortCols; //
	}

	/**
	 * 实际比较
	 */
	public int compare(Object _obj1, Object _obj2) {
		try {
			Object[] rowData1 = (Object[]) _obj1;
			Object[] rowData2 = (Object[]) _obj2;
			for (int i = 0; i < str_sortCols.length; i++) { //遍历各个排序列,即如果第一个相等则比较第二个!!!!
				int li_index = Integer.parseInt(str_sortCols[i][0]); //取第几列
				boolean isDesc = (str_sortCols[i][1].equalsIgnoreCase("Y") ? true : false); //是否倒序,
				boolean isNumber = (str_sortCols[i][2].equalsIgnoreCase("Y") ? true : false); //是否是数字
				Object itemData1 = rowData1[li_index];
				Object itemData2 = rowData2[li_index];
				int li_compareResult = 0; //比较值..
				if (isNumber) { //如果是数字,则转换成数字比较
					double ld_1 = (itemData1 == null ? 0 : Double.parseDouble(itemData1.toString()));
					double ld_2 = (itemData2 == null ? 0 : Double.parseDouble(itemData2.toString()));
					li_compareResult = (int) (ld_1 - ld_2);
				} else { //如果是字符串,则转换成字符串比较
					String str_1 = (itemData1 == null ? "" : itemData1.toString());
					String str_2 = (itemData2 == null ? "" : itemData2.toString());
					String newStr_1 = new String(str_1.getBytes("GBK"), "ISO-8859-1"); //必须转换一下才能达到按汉语拼音排序的效果
					String newStr_2 = new String(str_2.getBytes("GBK"), "ISO-8859-1"); //必须转换一下才能达到按汉语拼音排序的效果
					li_compareResult = newStr_1.compareTo(newStr_2);
				}

				if (isDesc) { //如果是倒序,就倒一下
					li_compareResult = 0 - li_compareResult;
				}

				if (li_compareResult != 0) { //如果不相等则立即返回,即不再进行第二个列的比较了!!!否则继续比较!
					return li_compareResult; //
				}
			}

			return 0; //如果每一个都相等,则认为真正相等,直接返回!!
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return 0;
		}
	}

}
