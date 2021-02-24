package cn.com.infostrategy.bs.workflow;

import java.util.Comparator;

import cn.com.infostrategy.to.workflow.design.ActivityVO;

/**
 * �������еĻ���������!! ��������������!
 * @author xch
 *
 */
public class ActivityVOComparator implements Comparator {

	public int compare(Object _obj1, Object _obj2) {
		ActivityVO vo1 = (ActivityVO) _obj1; //
		ActivityVO vo2 = (ActivityVO) _obj2; //

		int li_x_1 = vo1.getX(); //
		int li_y_1 = vo1.getY(); //
		
		int li_x_2 = vo2.getX(); //
		int li_y_2 = vo2.getY(); //

		int li_y_between = li_y_2 - li_y_1; //Y�Ĳ��!!
		if (li_y_between > 5 || li_y_between < -5) { //������ܴ�,��ֱ�ӷ���!
			if (li_y_between > 0) { //�����2��1������,�򷵻�����,
				return -1; //
			} else {
				return 1; //
			}
		} else { //���Y����!,���ж�Xλ��,���X����,����X��˵����!!
			int li_x_between = li_x_2 - li_x_1; //X�Ĳ��!!
			if (li_x_between > 5 || li_x_between < -5) { //���X����
				if (li_x_between > 0) { //�����2��1������,�򷵻�����,
					return -1; //
				} else {
					return 1; //
				}
			} else { //���Y��X������!
				if (li_y_1 == li_y_2) {
					return li_x_1 - li_x_2; //
				} else {
					return li_y_1 - li_y_2; //
				}
			}
		}
	}

}
