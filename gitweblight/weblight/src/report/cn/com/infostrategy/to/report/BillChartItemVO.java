package cn.com.infostrategy.to.report;

import java.io.Serializable;
import java.util.HashMap;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * ͼ������е�һ��Ԫ�ص�VO,֮�����������������Ϊ��ǰֻ��һ��Double,Ȼ���кϼ����кϼƶ��ǽ����ۼӲ���!
 * ����������е�������ƽ�����Ļ�,���кϼƽ����ۼӲ������߼��ǲ��Ե�,�����ݸ��ӵ�ֵ���޷����㣬����֪�������е�ֵ�����������ģ���Ҫ֪��sum��count��ֵ
 * ������ʱ�ͱ���洢һ�����������һ���򵥵�doubleֵ��,����������������!!��sum��count��ֵ�����ȥ,Ȼ���ӓ������Ӌ���!!
 * ������кϼƲ�����ǰ̨���㣬��Ͳ���Ҫ�����������ˣ������ṩһ��API�ں�̨�����(��Ҳ����֪���������ĸ��ֵ)������˵Ҳ����ܻ��и��õİ취!!
 * ��ʱ����ô����!!Ȼ���ṩһ�������밴ť���Բ鿴ÿ�����ӵļ�����̣�����У���Ƿ������ȷ����
 * @author xch
 *
 */
public class BillChartItemVO implements Serializable {

	private static final long serialVersionUID = -7869530510926563582L;

	public static int TOTAL = 1; //�ܺ�����,��С��ֻ�Ǽ򵥵�����Ͳ���
	public static int RATE = 2; //��������,��С�����ø����sumValue�ĺ��ٳ��Ը����countValue�ĺ�,����ƽ�������㷨.
	private int valueType = TOTAL; //��ֵ������,Ĭ����TOTAL����,��ֱ����͵�����

	private double sumValue = 0; //
	private double countValue = 0; //
	private double value = 0; //

	private boolean isPercent = false; //�Ƿ��ǰٷֱȵ�����,�����,���ڱ������ʾʱ������һ���ٷֺ�%,Ĭ���ǲ����ٷֺŵ�!
	private String infoMsg = null; //��¼��μ����,���������,���������ȥʱ����ʾ����!

	private HashMap custMap = new HashMap(); //���Է�һ���Զ������

	/**
	 * ֱ����ƽ����ֵ,��ֱ��������͵Ĺ��췽��
	 * @param _avgValue
	 */
	public BillChartItemVO(double _value) {
		this(_value, false); //
	}

	public BillChartItemVO(double _value, boolean _isPercent) {
		this.valueType = TOTAL; //�����Ǳ�������
		this.value = _value; //
		this.isPercent = _isPercent; //
	}

	/**
	 * ֻ��sum��Count,Avg��ʱ����
	 * @param _sumValue
	 * @param _countValue
	 */
	public BillChartItemVO(double _sumValue, double _countValue) {
		this(_sumValue, _countValue, false);
	}

	public BillChartItemVO(double _sumValue, double _countValue, boolean _isPercent) {
		this.valueType = RATE; //�����Ǳ�������
		this.sumValue = _sumValue; //
		this.countValue = _countValue; //
		this.isPercent = _isPercent; //
		if (_countValue != 0) {
			this.value = sumValue / countValue;
		}
	}

	/**
	 * Value����������ͽ���
	 * @param _sumValue
	 * @param _countValue
	 * @param _avgvalue
	 */
	public BillChartItemVO(double _sumValue, double _countValue, double _value) {
		this(_sumValue, _countValue, _value, false); //
	}

	public BillChartItemVO(double _sumValue, double _countValue, double _value, boolean _isPercent) {
		this.valueType = RATE; //�����Ǳ�������
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
		return Double.valueOf(TBUtil.getTBUtil().getDoubleToString(value)); //ͼ�ϵ�ֵ�����е�ֵ��Ϊ���ȶ��Բ���,�����ṩ�÷�����֤���߶Ե���!!
	}

	public boolean isPercent() {
		return isPercent;
	}

	public void setPercent(boolean isPercent) {
		this.isPercent = isPercent;
	}

	/**
	 * �����Զ������
	 * @param _key
	 * @param _value
	 */
	public void setCustProperty(Object _key, Object _value) {
		this.custMap.put(_key, _value); //
	}

	/**
	 * ȡ���Զ������.
	 * @param _key
	 * @return
	 */
	public Object getCustProperty(Object _key) {
		return custMap.get(_key); //
	}

	public String getInfoMsg() {
		if (this.infoMsg != null) { //��������ֶ����ù�,��ֱ�ӷ���!
			return this.infoMsg;
		} else {
			if (isPercent && value != 0) {
				this.infoMsg = "(" + sumValue + "/" + countValue + ")%=" + TBUtil.getTBUtil().getDoubleToString(value) + "%"; //������ϸ�������,�Ա����
			} else {
				this.infoMsg = sumValue + "/" + countValue + "=" + TBUtil.getTBUtil().getDoubleToString(value); //������ϸ�������,�Ա����
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
