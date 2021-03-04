/**************************************************************************
 * $RCSfile: TableDataStruct.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/

package cn.com.infostrategy.to.common;

import java.io.Serializable;

public class TableDataStruct implements Serializable {

	private static final long serialVersionUID = 7758843430724165925L;

	private String tablename = null; //����һ��ȡ����,ֻȡ��һ�е�,��֧�ִӶ����ȡ��,����д��ͼ!!

	private String[] headerName = null; //
	private int[] headerType = null;
	private String[] headerTypeName = null; //
	private int[] headerLength = null;  //�ֶεĿ��
	private int[] colValueMaxLen = null;  //ÿ�е�������������,�ڱ���������Ҫ�Զ�����,����ʾ���ڴ�С!
	private String[] isNullAble = null; //�ֶ��Ƿ�Ϊ��
	private int[] precision = null; //��ȷ��
	private int[] scale = null; //С�����λ
	
	private String[][] bodyData = null; //

	public String[][] getBodyData() {
		return bodyData;
	}

	public void setBodyData(String[][] table_body) {
		this.bodyData = table_body;
	}

	public String[] getHeaderName() {
		return headerName;
	}

	public void setHeaderName(String[] table_header) {
		this.headerName = table_header;
	}

	public String[] getHeaderTypeName() {
		return headerTypeName;
	}

	public void setHeaderTypeName(String[] _headerTypeName) {
		this.headerTypeName = _headerTypeName;
	}

	public String getTableName() {
		return tablename;
	}

	public void setTableName(String fromtablename) {
		this.tablename = fromtablename;
	}

	public int[] getHeaderType() {
		return headerType;
	}

	public void setHeaderType(int[] headerType) {
		this.headerType = headerType;
	}

	public int[] getHeaderLength() {
		return headerLength;
	}

	public void setHeaderLength(int[] headerLength) {
		this.headerLength = headerLength;
	}

	public String[] getIsNullAble() {
		return isNullAble;
	}

	public void setIsNullAble(String[] isNullAble) {
		this.isNullAble = isNullAble;
	}

	public int[] getColValueMaxLen() {
		return colValueMaxLen;
	}

	public void setColValueMaxLen(int[] colValueMaxLen) {
		this.colValueMaxLen = colValueMaxLen;
	}

	public int[] getPrecision() {
		return precision;
	}

	public void setPrecision(int[] precision) {
		this.precision = precision;
	}

	public int[] getScale() {
		return scale;
	}

	public void setScale(int[] scale) {
		this.scale = scale;
	}

}
