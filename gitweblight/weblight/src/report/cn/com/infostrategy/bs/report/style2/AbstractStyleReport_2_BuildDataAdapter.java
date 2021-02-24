package cn.com.infostrategy.bs.report.style2;

import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

public abstract class AbstractStyleReport_2_BuildDataAdapter implements StyleReport_2_BuildDataIFC {

	public HashVO[] buildDataByCondition(HashMap _condition, CurrLoginUserVO userVO) throws Exception {
		return null;
	}

	/**
	 * 报表标题
	 * @return
	 */
	public String getTitle() {
		return null;
	}

	/**
	 * 排序的列
	 * @return 返回一个行三列的数组,其中第一列是列名,必须与返回的HashVO中的列名品配,第二列是升序还是倒序(Y=升序,N=倒序),第三列是指明是否是数字("Y"/"N")
	 */
	public String[][] getSortColumns() {
		return null; //
	}

	/**
	 * 需要相同合并的列
	 * @return
	 */
	public String[] getSpanColumns() {
		return null; //
	}

}
