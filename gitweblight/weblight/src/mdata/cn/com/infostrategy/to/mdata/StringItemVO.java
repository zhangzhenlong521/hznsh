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
	 * 判断两者是否相等..
	 */
	public boolean equals(Object obj) {
		if (obj == null) {
			if (this.stringValue == null || this.stringValue.equals("")) {
				return true; //如果目标是空,而本人的id为空或空字符串,则认为是相同的!!
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
