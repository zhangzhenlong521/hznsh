package cn.com.infostrategy.to.workflow.engine;

import java.util.Comparator;

/**
 * 比较器 
 * @author xch
 *
 */
public class ActivityVOComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		DealActivityVO vo_1 = (DealActivityVO) o1;
		DealActivityVO vo_2 = (DealActivityVO) o2;
		if (vo_1.getSortIndex() == vo_2.getSortIndex()) { //如果相等,则比较编码
			return vo_1.getActivityCode().compareTo(vo_2.getActivityCode()); //根据环节编码排序!!!
		} else { //如果不等,则直接根据排序字段!!
			return vo_1.getSortIndex() - vo_2.getSortIndex(); //
		}
	}

}
