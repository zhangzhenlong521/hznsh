package cn.com.infostrategy.to.common;

import java.util.Comparator;

/**
 * �Ƚ��ַ����ıȽ���,��Ϊjdk�ڱȽ�����ʱ���󲻶�,��Ҫת����ISO-8859-1����!!
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
		if (orders == null) { //���û��ָ��Ԥ�õ���������
			if (o1 == null) {
				if (o2 != null) {
					return -1;
				} else {
					return 0;
				}
			} else {
				if (o2 != null) {
					try {
						String str_1 = new String(((String) o1).getBytes("GBK"), "ISO-8859-1"); //תһ��
						String str_2 = new String(((String) o2).getBytes("GBK"), "ISO-8859-1"); //תһ��
						return str_1.compareTo(str_2); //
					} catch (Exception e) {
						e.printStackTrace(); //
						return 0;
					}
				} else {
					return 1;
				}
			}
		} else { //���ָ����Ԥ������
			String str_1 = (String) o1;
			String str_2 = (String) o2;
			int li_pos_1 = findItemInArray(str_1, this.orders); //��λ��
			int li_pos_2 = findItemInArray(str_2, this.orders); //��λ��
			return li_pos_1 - li_pos_2;
		}
	}

	/**
	 * ��һ��ֵ��һ�������е�λ��
	 * @param _item
	 * @param _array
	 * @return
	 */
	private int findItemInArray(String _item, String[] _array) {
		if (_item == null) {
			return -1; //����ǿ�ֵ,����Զ���ڵ�һλ
		}
		for (int i = 0; i < _array.length; i++) {
			if (_item.equals(_array[i])) {
				return i; //
			}
		}
		return 99999; //�����ֵû������������г��֣������ر���ֵ����Զ���ں������!!�����ֵʲô��!!
	}
}
