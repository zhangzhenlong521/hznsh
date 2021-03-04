package cn.com.infostrategy.to.mdata;

import java.util.Comparator;

/**
 * 列表查询时需要进行排序,这是数字的比较器.
 * @author xch
 *
 */
public class BillListDataNumberComparator implements Comparator {

	private int li_index = -1; //
	private boolean isdesc = false; //

	public BillListDataNumberComparator(int _index, boolean _desc) {
		li_index = _index;
		isdesc = _desc;
	}

	/**
	 * 实际比较
	 */
	public int compare(Object _obj1, Object _obj2) {
		try {
			Object[] rowData1 = (Object[]) _obj1;
			Object[] rowData2 = (Object[]) _obj2;
			Object itemData1 = rowData1[li_index];
			Object itemData2 = rowData2[li_index];
			double ld_1 = (itemData1 == null ? 0 : Double.parseDouble(itemData1.toString()));
			double ld_2 = (itemData2 == null ? 0 : Double.parseDouble(itemData2.toString()));
			
			//Bug: 如果 (ld_1 - ld_2) = 0.x, int一把就是0了 Gwang 2013-09-07
//			if (!isdesc) { //升序
//				return (int) (ld_1 - ld_2);
//			} else { //降序
//				return (int) (ld_2 - ld_1);
//			}
			
			boolean ret = false;
			if (!isdesc) { // 升序
				ret = (ld_1 > ld_2);
			} else { // 降序
				ret = (ld_2 > ld_1);
			}
			return (ret ? 1 : 0); 
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return 0;
		}
	}

}
