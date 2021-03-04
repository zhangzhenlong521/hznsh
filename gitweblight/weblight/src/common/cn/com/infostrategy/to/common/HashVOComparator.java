package cn.com.infostrategy.to.common;

import java.util.Comparator;

/**
 * HashVO�ıȽ���,�������ж���бȽ�,�����Ƿ���,֧���Ƿ�������!
 * new String[][]{{"seq","N","Y"}}  //��HashVO[]�е�seq�ֶ���������,����������...�ڶ����б�ʾ�Ƿ��ǵ���,�������б�ʾ�Ƿ�������!!!
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
			String str_1 = hvo_1.getStringValue(itemKey, ""); //��ֵ���մ�����,��������ǰ��
			String str_2 = hvo_2.getStringValue(itemKey, ""); //��ֵ���մ�����,��������ǰ��
			int li_pos_1 = findItemInArray(str_1, this.orders); //��λ��
			int li_pos_2 = findItemInArray(str_2, this.orders); //��λ��
			return li_pos_1 - li_pos_2;

		}

		int[] li_compareResult = new int[str_sortColumns.length]; //
		for (int i = 0; i < str_sortColumns.length; i++) {
			if (str_sortColumns[i][2].equals("Y")) { //���������
				try {
					String str_1 = hvo_1.getStringValue(str_sortColumns[i][0], "-99999"); //��ֵ���մ�����,��������ǰ��
					String str_2 = hvo_2.getStringValue(str_sortColumns[i][0], "-99999"); //��ֵ���մ�����,��������ǰ��
					str_1 = str_1.trim();
					str_2 = str_2.trim();
					if (str_1.equals("")) {
						str_1 = "-99999"; //
					}
					if (str_2.equals("")) {
						str_2 = "-99999"; //
					}
					if (str_1.endsWith("%")) { //�����аٷֺŵ�������ڱ���ͳ��������Ҫ�԰ٷֱȽ�������xch/2012-08-27��
						str_1 = str_1.substring(0, str_1.length() - 1); //
					}
					if (str_2.endsWith("%")) {
						str_2 = str_2.substring(0, str_2.length() - 1); //
					}

					Double ld_1 = Double.parseDouble(str_1); //��ֵ��-99999����,��������ǰ��
					Double ld_2 = Double.parseDouble(str_2); //��ֵ��-99999����,��������ǰ��
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
				//����������зֺ�,����Ϊ�Ǹ�����,Ȼ��Ͱ�����ָ����˳������,������Ӣ����ĸ˳�����У���
				//if()

				try {
					if (itemKey != null && !itemKey.equals("") && str_sortColumns[i][0].equals(itemKey)) {
						String str_1 = hvo_1.getStringValue(str_sortColumns[i][0], ""); //��ֵ���մ�����,��������ǰ��
						String str_2 = hvo_2.getStringValue(str_sortColumns[i][0], ""); //��ֵ���մ�����,��������ǰ��
						int li_pos_1 = findItemInArray(str_1, this.orders); //��λ��
						int li_pos_2 = findItemInArray(str_2, this.orders); //��λ��
						li_compareResult[i] = li_pos_1 - li_pos_2;
					} else {
						String str_1 = hvo_1.getStringValue(str_sortColumns[i][0], ""); //��ֵ���մ�����,��������ǰ��
						String str_2 = hvo_2.getStringValue(str_sortColumns[i][0], ""); //��ֵ���մ�����,��������ǰ��
						str_1 = new String(str_1.getBytes("GBK"), "ISO-8859-1"); //תһ��
						str_2 = new String(str_2.getBytes("GBK"), "ISO-8859-1"); //תһ��
						li_compareResult[i] = str_1.compareTo(str_2);
					}

					//
				} catch (Exception ex) {
					System.err.println(ex.getMessage()); //
					li_compareResult[i] = 0; //
				}
			}

			if (str_sortColumns[i][1].equalsIgnoreCase("Y")) { //�Ƿ���
				li_compareResult[i] = 0 - li_compareResult[i]; //
			}

			if (li_compareResult[i] != 0) {
				return li_compareResult[i]; //����Ѿ��Ƚϳ�����,��ֱ�ӷ���!!!��û��Ҫ������һ���Ƚ���!!,���������һ���ıȽ�!!
			}
		}

		return 0; //����0
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
