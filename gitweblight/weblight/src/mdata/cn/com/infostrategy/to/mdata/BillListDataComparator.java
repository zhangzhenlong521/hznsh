package cn.com.infostrategy.to.mdata;

import java.util.Comparator;

/**
 * �б��ѯʱ��Ҫ��������,�������ֵıȽ���.
 * @author xch
 *
 */
public class BillListDataComparator implements Comparator {

	private String[][] str_sortCols = null; //�������,һ��3��!!��1���Ǳ�ʾ����,���ڼ�λ;��2�б�ʾ�Ƿ��ǵ���;��3�б�ʾ�Ƿ�������!!!!����{"3","Y","N"}

	public BillListDataComparator(String[][] _sortCols) {
		this.str_sortCols = _sortCols; //
	}

	/**
	 * ʵ�ʱȽ�
	 */
	public int compare(Object _obj1, Object _obj2) {
		try {
			Object[] rowData1 = (Object[]) _obj1;
			Object[] rowData2 = (Object[]) _obj2;
			for (int i = 0; i < str_sortCols.length; i++) { //��������������,�������һ�������Ƚϵڶ���!!!!
				int li_index = Integer.parseInt(str_sortCols[i][0]); //ȡ�ڼ���
				boolean isDesc = (str_sortCols[i][1].equalsIgnoreCase("Y") ? true : false); //�Ƿ���,
				boolean isNumber = (str_sortCols[i][2].equalsIgnoreCase("Y") ? true : false); //�Ƿ�������
				Object itemData1 = rowData1[li_index];
				Object itemData2 = rowData2[li_index];
				int li_compareResult = 0; //�Ƚ�ֵ..
				if (isNumber) { //���������,��ת�������ֱȽ�
					double ld_1 = (itemData1 == null ? 0 : Double.parseDouble(itemData1.toString()));
					double ld_2 = (itemData2 == null ? 0 : Double.parseDouble(itemData2.toString()));
					li_compareResult = (int) (ld_1 - ld_2);
				} else { //������ַ���,��ת�����ַ����Ƚ�
					String str_1 = (itemData1 == null ? "" : itemData1.toString());
					String str_2 = (itemData2 == null ? "" : itemData2.toString());
					String newStr_1 = new String(str_1.getBytes("GBK"), "ISO-8859-1"); //����ת��һ�²��ܴﵽ������ƴ�������Ч��
					String newStr_2 = new String(str_2.getBytes("GBK"), "ISO-8859-1"); //����ת��һ�²��ܴﵽ������ƴ�������Ч��
					li_compareResult = newStr_1.compareTo(newStr_2);
				}

				if (isDesc) { //����ǵ���,�͵�һ��
					li_compareResult = 0 - li_compareResult;
				}

				if (li_compareResult != 0) { //������������������,�����ٽ��еڶ����еıȽ���!!!��������Ƚ�!
					return li_compareResult; //
				}
			}

			return 0; //���ÿһ�������,����Ϊ�������,ֱ�ӷ���!!
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return 0;
		}
	}

}
