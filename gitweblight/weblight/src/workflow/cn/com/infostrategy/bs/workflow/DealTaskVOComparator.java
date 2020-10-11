package cn.com.infostrategy.bs.workflow;

import java.util.Comparator;

import cn.com.infostrategy.to.workflow.engine.DealTaskVO;

public class DealTaskVOComparator implements Comparator {

	public int compare(Object _obj1, Object _obj2) {
		try {
			DealTaskVO taksVO1 = (DealTaskVO) _obj1;
			DealTaskVO taksVO2 = (DealTaskVO) _obj2;
			String str_corpName1 = null;
			String str_corpName2 = null;

			if (taksVO1 != null) {
				str_corpName1 = taksVO1.getParticipantUserDeptName();
				if (str_corpName1 != null) {
					str_corpName1 = new String(str_corpName1.getBytes("GBK"), "ISO-8859-1"); //转一下
				}
			}
			if (taksVO2 != null) {
				str_corpName2 = taksVO2.getParticipantUserDeptName();
				if (str_corpName2 != null) {
					str_corpName2 = new String(str_corpName2.getBytes("GBK"), "ISO-8859-1"); //转一下
				}
			}
			str_corpName1 = (str_corpName1 == null ? "" : str_corpName1); //必须处理空值,在民生中发生有的机构名称为空时就产生空指针异常!!!
			str_corpName2 = (str_corpName2 == null ? "" : str_corpName2); //
			return str_corpName1.compareTo(str_corpName2); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return 0; //
		}
	}

}
