package cn.com.infostrategy.to.report;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 预置的分组查询条件!!!
 * 就是有时有些领导不会自定义选择条件,需要搞一个预先设置好的查询分组
 * @author xch
 *
 */
public class BeforeHandGroupTypeVO implements Serializable {

	private static final long serialVersionUID = 7338244805785588645L;

	public static int GRID = 1; //表格类型
	public static int CHART = 1; //图表类型

	public static String SUM = "sum"; //
	public static String COUNT = "count"; //
	public static String MAX = "max"; //
	public static String MIN = "min"; //
	public static String AVG = "avg"; //
	public static String INIT= "init";//袁江晓20130306 添加  初始值

	private String name = null; //名称,即在下拉框中显示的名称
	private String type = "GRID"; //报表类型,有GRID,CHART两种类型!!默认设成GRID,因为经常有人忘记不设,结果报空指针异常!【xch/2012-08-06】

	private String[] rowHeaderGroupFields = null; //行头
	private String[] colHeaderGroupFields = null; //列头
	private String[][] computeGroupFields = null; //统计的列,即指定格子中的值是什么,之所以是二维,是因为第一列指定列名，第二列可以是count也可能是sum或avg计算,如果是count计算,则第一列的值是随意的,比如{"数量",BeforeHandGroupTypeVO.COUNT},{"损失金额",BeforeHandGroupTypeVO.SUM}

	private boolean isTotal = true; //是否有总计!默认是有的,如果没有总计,则小计也不起效果!

	private boolean isRowGroupTiled = false; //行头是否平铺?
	private boolean isRowGroupSubTotal = true; //行头是否有小计?
	private boolean isColGroupTiled = false; //列头是否平铺?
	private boolean isColGroupSubTotal = true; //列头是否有小计?

	private boolean isSortByCpValue = false; //是否根据金额排序?即有时维度的排列不是按维度本身按维度本身指定的顺序,而是按金额大小排序!即排行榜的概念!

	private HashMap filterGroupValueMap = null; //默认对某个组的维度值进行过滤!即前台查询所有数据后,需要将某个维度的某个值干掉!比如统计环比时,需要将1季度去掉! 或者是预警时,只想显示当前季度(最大季度)!
	private HashMap secondHashVOComputeMap = null; //二次计算对HashVO处理的定义,即过滤后完全可能再次对HashVO进行排序计算等,邮储中就有预警功能,是先进行同比环比计算,然后按照连续增长次数进行再次计算

	private HashMap rowHeaderFormulaGroupMap = null; //定义行头上的组可以增加校报的公式组,key是原来的组名,value是二维数组!二维数组是N行3列,每行表示一个新的组,第一列表示新的组名,第二列表示公式,第三列是扩展属性

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
	 * 定义输出HashVO数据格式的定义!!
	 * 即在第一次输出维度组合时,经常遇到对维度值进行二次过滤,而过滤后再次对结果集进行按金额排序!
	 * 即前强计算同时有两种结果,一种是网络，一种是二维的HashVO,即任何报表再变化,本质上数据依然还是个二维表! 依然存在这个二维表HashVO[],然后就可以对这个二维数组进行二次计算!
	 * 这种计算包括再次排序,再次合并。。。
	 * 常用参数有:
	 * 按第几位计算列排序=2
	 * 输出时隐藏的列= new String[]{"发生月度"}
	 * @return
	 */
	public HashMap getSecondHashVOComputeMap() {
		return secondHashVOComputeMap;
	}

	public void setSecondHashVOComputeMap(HashMap secondHashVOComputeMap) {
		this.secondHashVOComputeMap = secondHashVOComputeMap;
	}

}
