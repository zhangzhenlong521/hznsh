package cn.com.infostrategy.ui.report;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 钻取定义对象,即进行钻取时,根据选择情况返回指定的钻取情况
 * 
 * @author xch
 *
 */
public class DrillDefineVO implements Serializable {

	public static int ROWGROUP = 1; //钻取的行
	public static int COLGROUP = 2; //钻取的行
	public static int CELL = 3; //钻取的格子

	private int drillType = 0; //钻取的类型,取值应该是三个静态变量中的任意一个!
	private String[] drillRowGroupName = null; //当钻取行头组时,该项必须有值,当钻取格子时,可能有值,可能无值
	private String[] drillColGroupName = null; //当钻取列头组时,该项必须有值,当钻取格子时,可能有值,可能无值
	private HashMap[] drillConditionMap = null; //
	private String[][] computeFunAndFields = null;  //

	public int getDrillType() {
		return drillType;
	}

	public void setDrillType(int drillType) {
		this.drillType = drillType;
	}

	public String[] getDrillRowGroupName() {
		return drillRowGroupName;
	}

	public void setDrillRowGroupName(String[] drillRowGroupName) {
		this.drillRowGroupName = drillRowGroupName;
	}

	public String[] getDrillColGroupName() {
		return drillColGroupName;
	}

	public void setDrillColGroupName(String[] drillColGroupName) {
		this.drillColGroupName = drillColGroupName;
	}

	public HashMap[] getDrillConditionMap() {
		return drillConditionMap;
	}

	public void setDrillConditionMap(HashMap[] drillConditionMap) {
		this.drillConditionMap = drillConditionMap;
	}

	public String[][] getComputeFunAndFields() {
		return computeFunAndFields;
	}

	public void setComputeFunAndFields(String[][] computeFunAndFields) {
		this.computeFunAndFields = computeFunAndFields;
	}

}
