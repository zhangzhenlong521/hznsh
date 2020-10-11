package cn.com.infostrategy.to.common;

import java.util.Comparator;

/**
 * HashVO�ıȽ���,�������ж���бȽ�,�����Ƿ���,֧���Ƿ�������!
 * new String[][]{{"seq","N","Y"}}  //��HashVO[]�е�seq�ֶ���������,����������...
 * @author xch
 *
 */
public class TableDataStructComparator implements Comparator {

	private String[] str_headNames = null;
	private String[][] str_sortColumns = null; //

	public TableDataStructComparator(String[] _headNames, String[][] _sortColumns) {
		this.str_headNames = _headNames; //��ͷ
		this.str_sortColumns = _sortColumns; //
	}

	public int compare(Object o1, Object o2) {
		String[] bodyRowData_1 = (String[]) o1; //
		String[] bodyRowData_2 = (String[]) o2; //
		int[] li_compareResult = new int[str_sortColumns.length]; //
		for (int i = 0; i < str_sortColumns.length; i++) {
			int li_index = getIndex(str_sortColumns[i][0]); //
			if (li_index < 0) {
				li_compareResult[i] = 0; //���û�ҵ�λ��(��û����ֵ),���ʾ���!!!
				continue; //������һѭ��
			}

			if (str_sortColumns[i][2].equals("Y")) { //���������
				double ld_1 = ((bodyRowData_1[li_index] == null || bodyRowData_1[li_index].trim().equals("")) ? -99999 : Double.valueOf(bodyRowData_1[li_index])); //��ֵ��-99999����,��������ǰ��
				double ld_2 = ((bodyRowData_2[li_index] == null || bodyRowData_2[li_index].trim().equals("")) ? -99999 : Double.valueOf(bodyRowData_2[li_index])); //��ֵ��-99999����,��������ǰ��
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
					String str_1 = (bodyRowData_1[li_index] == null ? "" : bodyRowData_1[li_index]); //��ֵ���մ�����,��������ǰ��
					String str_2 = (bodyRowData_2[li_index] == null ? "" : bodyRowData_2[li_index]); //��ֵ���մ�����,��������ǰ��
					str_1 = new String(str_1.getBytes("GBK"), "ISO-8859-1"); //תһ��
					str_2 = new String(str_2.getBytes("GBK"), "ISO-8859-1"); //תһ��
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
			if (li_compareResult[i] != 0) { //�ӵ�һ����ʼ,��������,����������!!
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
