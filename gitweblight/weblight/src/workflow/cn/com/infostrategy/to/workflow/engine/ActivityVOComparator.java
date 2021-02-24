package cn.com.infostrategy.to.workflow.engine;

import java.util.Comparator;

/**
 * �Ƚ��� 
 * @author xch
 *
 */
public class ActivityVOComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		DealActivityVO vo_1 = (DealActivityVO) o1;
		DealActivityVO vo_2 = (DealActivityVO) o2;
		if (vo_1.getSortIndex() == vo_2.getSortIndex()) { //������,��Ƚϱ���
			return vo_1.getActivityCode().compareTo(vo_2.getActivityCode()); //���ݻ��ڱ�������!!!
		} else { //�������,��ֱ�Ӹ��������ֶ�!!
			return vo_1.getSortIndex() - vo_2.getSortIndex(); //
		}
	}

}
