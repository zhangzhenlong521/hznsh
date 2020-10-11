package cn.com.infostrategy.bs.workflow;

import java.util.Comparator;

import cn.com.infostrategy.to.workflow.design.ActivityVO;

/**
 * 工作流中的环节排序器!! 好由上至下排序!
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

		int li_y_between = li_y_2 - li_y_1; //Y的差矩!!
		if (li_y_between > 5 || li_y_between < -5) { //如果相差很大,则直接返回!
			if (li_y_between > 0) { //如果是2在1的下面,则返回正数,
				return -1; //
			} else {
				return 1; //
			}
		} else { //如果Y相差不大!,则判断X位置,如果X相差极大,则以X的说了算!!
			int li_x_between = li_x_2 - li_x_1; //X的差矩!!
			if (li_x_between > 5 || li_x_between < -5) { //如果X相差极大
				if (li_x_between > 0) { //如果是2在1的下面,则返回正数,
					return -1; //
				} else {
					return 1; //
				}
			} else { //如果Y与X相差都不大!
				if (li_y_1 == li_y_2) {
					return li_x_1 - li_x_2; //
				} else {
					return li_y_1 - li_y_2; //
				}
			}
		}
	}

}
