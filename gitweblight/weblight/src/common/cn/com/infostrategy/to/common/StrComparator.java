package cn.com.infostrategy.to.common;

import java.util.Comparator;

/**
 * 比较字符串的比较器,因为jdk在比较中文时好象不对,需要转换成ISO-8859-1才行!!
 * @author xch
 *
 */
public class StrComparator implements Comparator {

	private String[] orders = null;

	public StrComparator() {

	}

	public StrComparator(String[] _orders) {
		orders = _orders;
	}

	public int compare(Object o1, Object o2) {
		if (orders == null) { //如果没有指定预置的排序条件
			if (o1 == null) {
				if (o2 != null) {
					return -1;
				} else {
					return 0;
				}
			} else {
				if (o2 != null) {
					try {
						String str_1 = new String(((String) o1).getBytes("GBK"), "ISO-8859-1"); //转一下
						String str_2 = new String(((String) o2).getBytes("GBK"), "ISO-8859-1"); //转一下
						return str_1.compareTo(str_2); //
					} catch (Exception e) {
						e.printStackTrace(); //
						return 0;
					}
				} else {
					return 1;
				}
			}
		} else { //如果指定了预设条件
			String str_1 = (String) o1;
			String str_2 = (String) o2;
			int li_pos_1 = findItemInArray(str_1, this.orders); //找位置
			int li_pos_2 = findItemInArray(str_2, this.orders); //找位置
			return li_pos_1 - li_pos_2;
		}
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
