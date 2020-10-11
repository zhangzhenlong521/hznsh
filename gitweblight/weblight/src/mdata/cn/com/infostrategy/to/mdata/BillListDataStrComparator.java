package cn.com.infostrategy.to.mdata;

import java.util.Comparator;

/**
 * 列表查询时需要进行排序,这是字符串的比较器.
 * @author xch
 *
 */
public class BillListDataStrComparator implements Comparator {

	private int li_index = -1; //
	private boolean isdesc = false; //

	/**
	 * 构造方法.
	 * @param _index
	 * @param desc
	 */
	public BillListDataStrComparator(int _index, boolean _desc) {
		li_index = _index;
		isdesc = _desc;
	}

	/**
	 * 比较器
	 */
	public int compare(Object _obj1, Object _obj2) {
		try {
			Object[] rowData1 = (Object[]) _obj1;
			Object[] rowData2 = (Object[]) _obj2;
			Object itemData1 = rowData1[li_index];
			Object itemData2 = rowData2[li_index];
			String str_1 = (itemData1 == null ? "" : itemData1.toString());
			String str_2 = (itemData2 == null ? "" : itemData2.toString());

			String newStr_1 = new String(str_1.getBytes("GBK"), "ISO-8859-1"); //必须转换一下才能达到按汉语拼音排序的效果
			String newStr_2 = new String(str_2.getBytes("GBK"), "ISO-8859-1"); //必须转换一下才能达到按汉语拼音排序的效果

			if (!isdesc) { //升序
				return newStr_1.compareTo(newStr_2);
			} else { //降序
				return newStr_2.compareTo(newStr_1);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage()); //
			return 0;
		}
	}
}
