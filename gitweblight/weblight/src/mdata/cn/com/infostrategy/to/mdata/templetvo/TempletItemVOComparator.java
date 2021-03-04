package cn.com.infostrategy.to.mdata.templetvo;

import java.util.Comparator;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;

/**
 * ��������ģ��VO��˳��,���ʴ���Ŀ�������ͻ������ǹ�����������鿴�б��˳���в�ͬ���,��Ȼϲ�����������һ��!
 * ���Ū�ɶ��ģ�嶨����Ȼû��Ҫ,��ȷ�İ취�ǽ����½�������!
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

		return li_pos_1 - li_pos_2; //����0
	}
}
