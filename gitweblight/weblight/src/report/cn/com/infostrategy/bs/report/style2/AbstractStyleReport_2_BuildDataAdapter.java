package cn.com.infostrategy.bs.report.style2;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

public abstract class AbstractStyleReport_2_BuildDataAdapter implements StyleReport_2_BuildDataIFC {

	public HashVO[] buildDataByCondition(HashMap _condition, CurrLoginUserVO userVO) throws Exception {
		return null;
	}

	/**
	 * �������
	 * @return
	 */
	public String getTitle() {
		return null;
	}

	/**
	 * �������
	 * @return ����һ�������е�����,���е�һ��������,�����뷵�ص�HashVO�е�����Ʒ��,�ڶ����������ǵ���(Y=����,N=����),��������ָ���Ƿ�������("Y"/"N")
	 */
	public String[][] getSortColumns() {
		return null; //
	}

	/**
	 * ��Ҫ��ͬ�ϲ�����
	 * @return
	 */
	public String[] getSpanColumns() {
		return null; //
	}

}
