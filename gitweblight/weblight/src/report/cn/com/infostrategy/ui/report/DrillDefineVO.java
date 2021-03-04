package cn.com.infostrategy.ui.report;

import java.io.Serializable;
import java.util.HashMap;

/**
 * ��ȡ�������,��������ȡʱ,����ѡ���������ָ������ȡ���
 * 
 * @author xch
 *
 */
public class DrillDefineVO implements Serializable {

	public static int ROWGROUP = 1; //��ȡ����
	public static int COLGROUP = 2; //��ȡ����
	public static int CELL = 3; //��ȡ�ĸ���

	private int drillType = 0; //��ȡ������,ȡֵӦ����������̬�����е�����һ��!
	private String[] drillRowGroupName = null; //����ȡ��ͷ��ʱ,���������ֵ,����ȡ����ʱ,������ֵ,������ֵ
	private String[] drillColGroupName = null; //����ȡ��ͷ��ʱ,���������ֵ,����ȡ����ʱ,������ֵ,������ֵ
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
