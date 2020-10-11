package cn.com.infostrategy.to.mdata;

public class StringItemVO extends BillItemVO {

	private static final long serialVersionUID = 8237251557102339336L;

	private String stringValue = null;  //

	public StringItemVO() {
	}

	public StringItemVO(String _value) {
		this.stringValue = _value;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	/**
	 * �ж������Ƿ����..
	 */
	public boolean equals(Object obj) {
		if (obj == null) {
			if (this.stringValue == null || this.stringValue.equals("")) {
				return true; //���Ŀ���ǿ�,�����˵�idΪ�ջ���ַ���,����Ϊ����ͬ��!!
			}
		}

		if (!(obj instanceof StringItemVO)) { //
			return false; //
		} else if ((((StringItemVO) obj).getStringValue() == null || ((StringItemVO) obj).getStringValue().equals("")) && (this.stringValue == null || this.stringValue.equals(""))) {
			return true;
		} else {
			return ((StringItemVO) obj).getStringValue().equals(this.stringValue);
		}

	}

	public String toString() {
		if (stringValue == null) {
			return "";
		} else {
			return stringValue;
		}
	}

}
