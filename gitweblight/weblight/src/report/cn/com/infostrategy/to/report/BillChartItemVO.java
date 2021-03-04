package cn.com.infostrategy.to.report;

import java.io.Serializable;
import java.util.HashMap;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * 图表格子中的一个元素的VO,之所以提出这个概念，是因为以前只存一个Double,然后行合计与列合计都是进行累加操作!
 * 但如果格子中的数据是平均数的话,则行合计进行累加操作的逻辑是不对的,而根据格子的值又无法计算，必须知道格子中的值是如何算出来的，即要知道sum与count的值
 * 所以这时就必须存储一个对象而不是一个简单的double值了,所以提出了这个对象!!将sum与count的值都存进去,然后合訐是重新計算的!!
 * 但如果行合计不是在前台计算，则就不需要提出这个概念了，比如提供一个API在后台计算好(但也必须知道分子与分母的值)，所以说也许可能还有更好的办法!!
 * 暂时先这么处理!!然后提供一个方法与按钮可以查看每个格子的计算过程！！以校验是否计算正确！！
 * @author xch
 *
 */
public class BillChartItemVO implements Serializable {

	private static final long serialVersionUID = -7869530510926563582L;

	public static int TOTAL = 1; //总和类型,即小计只是简单的做求和操作
	public static int RATE = 2; //比例类型,即小计是拿各项的sumValue的和再除以各项的countValue的和,即算平均数的算法.
	private int valueType = TOTAL; //数值的类型,默认是TOTAL类型,即直接求和的类型

	private double sumValue = 0; //
	private double countValue = 0; //
	private double value = 0; //

	private boolean isPercent = false; //是否是百分比的数字,如果是,则在表格中显示时后面会多一个百分号%,默认是不带百分号的!
	private String infoMsg = null; //记录如何计算的,即计算过程,在鼠标移上去时会显示出来!

	private HashMap custMap = new HashMap(); //可以放一个自定义对象

	/**
	 * 直接送平均数值,即直接求和类型的构造方法
	 * @param _avgValue
	 */
	public BillChartItemVO(double _value) {
		this(_value, false); //
	}

	public BillChartItemVO(double _value, boolean _isPercent) {
		this.valueType = TOTAL; //类型是比例类型
		this.value = _value; //
		this.isPercent = _isPercent; //
	}

	/**
	 * 只送sum与Count,Avg及时计算
	 * @param _sumValue
	 * @param _countValue
	 */
	public BillChartItemVO(double _sumValue, double _countValue) {
		this(_sumValue, _countValue, false);
	}

	public BillChartItemVO(double _sumValue, double _countValue, boolean _isPercent) {
		this.valueType = RATE; //类型是比例类型
		this.sumValue = _sumValue; //
		this.countValue = _countValue; //
		this.isPercent = _isPercent; //
		if (_countValue != 0) {
			this.value = sumValue / countValue;
		}
	}

	/**
	 * Value在外面计算送进来
	 * @param _sumValue
	 * @param _countValue
	 * @param _avgvalue
	 */
	public BillChartItemVO(double _sumValue, double _countValue, double _value) {
		this(_sumValue, _countValue, _value, false); //
	}

	public BillChartItemVO(double _sumValue, double _countValue, double _value, boolean _isPercent) {
		this.valueType = RATE; //类型是比例类型
		this.sumValue = _sumValue; //
		this.countValue = _countValue; //
		this.value = _value; //
		this.isPercent = _isPercent; //
	}

	public int getValueType() {
		return this.valueType;
	}

	public void setValueType(int _valueType) {
		this.valueType = _valueType; //
	}

	public double getSumValue() {
		return sumValue;
	}

	public void setSumValue(double _sumValue) {
		this.sumValue = _sumValue;
	}

	public double getCountValue() {
		return countValue;
	}

	public void setCountValue(double _countValue) {
		this.countValue = _countValue;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double _value) {
		this.value = _value;
	}

	public double getScaleValue() {
		return Double.valueOf(TBUtil.getTBUtil().getDoubleToString(value)); //图上的值与表格中的值因为精度而对不上,所以提供该方法保证两者对得上!!
	}

	public boolean isPercent() {
		return isPercent;
	}

	public void setPercent(boolean isPercent) {
		this.isPercent = isPercent;
	}

	/**
	 * 设置自定义对象
	 * @param _key
	 * @param _value
	 */
	public void setCustProperty(Object _key, Object _value) {
		this.custMap.put(_key, _value); //
	}

	/**
	 * 取得自定义对象.
	 * @param _key
	 * @return
	 */
	public Object getCustProperty(Object _key) {
		return custMap.get(_key); //
	}

	public String getInfoMsg() {
		if (this.infoMsg != null) { //如果曾经手动设置过,则直接返回!
			return this.infoMsg;
		} else {
			if (isPercent && value != 0) {
				this.infoMsg = "(" + sumValue + "/" + countValue + ")%=" + TBUtil.getTBUtil().getDoubleToString(value) + "%"; //返回详细计算过程,以便跟踪
			} else {
				this.infoMsg = sumValue + "/" + countValue + "=" + TBUtil.getTBUtil().getDoubleToString(value); //返回详细计算过程,以便跟踪
			}
			return this.infoMsg;
		}
	}

	public void setInfoMsg(String infoMsg) {
		this.infoMsg = infoMsg;
	}

	@Override
	public String toString() {
		if (isPercent && value != 0) {
			return TBUtil.getTBUtil().getDoubleToString(value) + "%"; //
		} else {
			return TBUtil.getTBUtil().getDoubleToString(value); //
		}
	}

}
