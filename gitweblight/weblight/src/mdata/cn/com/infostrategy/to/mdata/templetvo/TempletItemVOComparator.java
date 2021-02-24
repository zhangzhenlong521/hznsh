package cn.com.infostrategy.to.mdata.templetvo;

import java.util.Comparator;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;

/**
 * 比如两个模板VO的顺序,在邮储项目中遇到客户对我们工作处理意见查看列表的顺序有不同意见,竟然喜欢步骤在最后一步!
 * 如果弄成多个模板定义显然没必要,正确的办法是将重新进行排序!
 * @author xch
 *
 */
public class TempletItemVOComparator implements Comparator {

	private String[] str_sortColumns = null; //

	public TempletItemVOComparator(String[] _sortColumns) {
		this.str_sortColumns = _sortColumns;
	}

	public int compare(Object o1, Object o2) {
		Pub_Templet_1_ItemVO hvo_1 = (Pub_Templet_1_ItemVO) o1;
		Pub_Templet_1_ItemVO hvo_2 = (Pub_Templet_1_ItemVO) o2;

		String str_name1 = hvo_1.getItemname(); //
		String str_name2 = hvo_2.getItemname(); //

		int li_pos_1 = 99999; //
		for (int i = 0; i < str_sortColumns.length; i++) {
			if (str_name1.equals(str_sortColumns[i])) {
				li_pos_1 = i; //
				break; //
			}
		}

		int li_pos_2 = 99999; //
		for (int i = 0; i < str_sortColumns.length; i++) {
			if (str_name2.equals(str_sortColumns[i])) {
				li_pos_2 = i; //
				break; //
			}
		}

		return li_pos_1 - li_pos_2; //返回0
	}
}
