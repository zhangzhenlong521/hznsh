package cn.com.infostrategy.to.report;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Ԥ�õķ����ѯ����!!!
 * ������ʱ��Щ�쵼�����Զ���ѡ������,��Ҫ��һ��Ԥ�����úõĲ�ѯ����
 * @author xch
 *
 */
public class BeforeHandGroupTypeVO implements Serializable {

	private static final long serialVersionUID = 7338244805785588645L;

	public static int GRID = 1; //�������
	public static int CHART = 1; //ͼ������

	public static String SUM = "sum"; //
	public static String COUNT = "count"; //
	public static String MAX = "max"; //
	public static String MIN = "min"; //
	public static String AVG = "avg"; //
	public static String INIT= "init";//Ԭ����20130306 ���  ��ʼֵ

	private String name = null; //����,��������������ʾ������
	private String type = "GRID"; //��������,��GRID,CHART��������!!Ĭ�����GRID,��Ϊ�����������ǲ���,�������ָ���쳣!��xch/2012-08-06��

	private String[] rowHeaderGroupFields = null; //��ͷ
	private String[] colHeaderGroupFields = null; //��ͷ
	private String[][] computeGroupFields = null; //ͳ�Ƶ���,��ָ�������е�ֵ��ʲô,֮�����Ƕ�ά,����Ϊ��һ��ָ���������ڶ��п�����countҲ������sum��avg����,�����count����,���һ�е�ֵ�������,����{"����",BeforeHandGroupTypeVO.COUNT},{"��ʧ���",BeforeHandGroupTypeVO.SUM}

	private boolean isTotal = true; //�Ƿ����ܼ�!Ĭ�����е�,���û���ܼ�,��С��Ҳ����Ч��!

	private boolean isRowGroupTiled = false; //��ͷ�Ƿ�ƽ��?
	private boolean isRowGroupSubTotal = true; //��ͷ�Ƿ���С��?
	private boolean isColGroupTiled = false; //��ͷ�Ƿ�ƽ��?
	private boolean isColGroupSubTotal = true; //��ͷ�Ƿ���С��?

	private boolean isSortByCpValue = false; //�Ƿ���ݽ������?����ʱά�ȵ����в��ǰ�ά�ȱ���ά�ȱ���ָ����˳��,���ǰ�����С����!�����а�ĸ���!

	private HashMap filterGroupValueMap = null; //Ĭ�϶�ĳ�����ά��ֵ���й���!��ǰ̨��ѯ�������ݺ�,��Ҫ��ĳ��ά�ȵ�ĳ��ֵ�ɵ�!����ͳ�ƻ���ʱ,��Ҫ��1����ȥ��! ������Ԥ��ʱ,ֻ����ʾ��ǰ����(��󼾶�)!
	private HashMap secondHashVOComputeMap = null; //���μ����HashVO����Ķ���,�����˺���ȫ�����ٴζ�HashVO������������,�ʴ��о���Ԥ������,���Ƚ���ͬ�Ȼ��ȼ���,Ȼ���������������������ٴμ���

	private HashMap rowHeaderFormulaGroupMap = null; //������ͷ�ϵ����������У���Ĺ�ʽ��,key��ԭ��������,value�Ƕ�ά����!��ά������N��3��,ÿ�б�ʾһ���µ���,��һ�б�ʾ�µ�����,�ڶ��б�ʾ��ʽ,����������չ����

	public BeforeHandGroupTypeVO() {
	}

	public BeforeHandGroupTypeVO(String _name) {
		this.name = _name;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getRowHeaderGroupFields() {
		return rowHeaderGroupFields;
	}

	public void setRowHeaderGroupFields(String[] rowHeaderGroupFields) {
		this.rowHeaderGroupFields = rowHeaderGroupFields;
	}

	public String[] getColHeaderGroupFields() {
		return colHeaderGroupFields;
	}

	public void setColHeaderGroupFields(String[] colHeaderGroupFields) {
		this.colHeaderGroupFields = colHeaderGroupFields;
	}

	public String[][] getComputeGroupFields() {
		return computeGroupFields;
	}

	public void setComputeGroupFields(String[][] computeGroupFields) {
		this.computeGroupFields = computeGroupFields;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isRowGroupTiled() {
		return isRowGroupTiled;
	}

	public void setRowGroupTiled(boolean isRowGroupTiled) {
		this.isRowGroupTiled = isRowGroupTiled;
	}

	public boolean isRowGroupSubTotal() {
		return isRowGroupSubTotal;
	}

	public void setRowGroupSubTotal(boolean isRowGroupSubTotal) {
		this.isRowGroupSubTotal = isRowGroupSubTotal;
	}

	public boolean isColGroupTiled() {
		return isColGroupTiled;
	}

	public void setColGroupTiled(boolean isColGroupTiled) {
		this.isColGroupTiled = isColGroupTiled;
	}

	public boolean isColGroupSubTotal() {
		return isColGroupSubTotal;
	}

	public void setColGroupSubTotal(boolean isColGroupSubTotal) {
		this.isColGroupSubTotal = isColGroupSubTotal;
	}

	public HashMap getRowHeaderFormulaGroupMap() {
		return rowHeaderFormulaGroupMap;
	}

	public void setRowHeaderFormulaGroupMap(HashMap rowHeaderFormulaGroupMap) {
		this.rowHeaderFormulaGroupMap = rowHeaderFormulaGroupMap;
	}

	public boolean isSortByCpValue() {
		return isSortByCpValue;
	}

	public void setSortByCpValue(boolean isSortByCpValue) {
		this.isSortByCpValue = isSortByCpValue;
	}

	public boolean isTotal() {
		return isTotal;
	}

	public void setTotal(boolean isTotal) {
		this.isTotal = isTotal;
	}

	public HashMap getFilterGroupValueMap() {
		return filterGroupValueMap;
	}

	public void setFilterGroupValueMap(HashMap filterGroupValueMap) {
		this.filterGroupValueMap = filterGroupValueMap;
	}

	/**
	 * �������HashVO���ݸ�ʽ�Ķ���!!
	 * ���ڵ�һ�����ά�����ʱ,����������ά��ֵ���ж��ι���,�����˺��ٴζԽ�������а��������!
	 * ��ǰǿ����ͬʱ�����ֽ��,һ�������磬һ���Ƕ�ά��HashVO,���κα����ٱ仯,������������Ȼ���Ǹ���ά��! ��Ȼ���������ά��HashVO[],Ȼ��Ϳ��Զ������ά������ж��μ���!
	 * ���ּ�������ٴ�����,�ٴκϲ�������
	 * ���ò�����:
	 * ���ڼ�λ����������=2
	 * ���ʱ���ص���= new String[]{"�����¶�"}
	 * @return
	 */
	public HashMap getSecondHashVOComputeMap() {
		return secondHashVOComputeMap;
	}

	public void setSecondHashVOComputeMap(HashMap secondHashVOComputeMap) {
		this.secondHashVOComputeMap = secondHashVOComputeMap;
	}

}
