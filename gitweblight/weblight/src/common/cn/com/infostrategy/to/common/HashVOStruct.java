/**************************************************************************
 * $RCSfile: HashVOStruct.java,v $  $Revision: 1.9 $  $Date: 2012/09/14 09:17:31 $
 **************************************************************************/

package cn.com.infostrategy.to.common;

import java.io.Serializable;
import java.util.HashMap;

/**
 * ����ͷ�����HashVO[]����!!!
 * @author xch
 *
 */
public class HashVOStruct implements Serializable {

	private static final long serialVersionUID = 7758843430724165925L;

	private String tablename = null; //����һ��ȡ����,ֻȡ��һ�е�,��֧�ִӶ����ȡ��,����д��ͼ!!
	private String pkN = null;//��������
	private String pkC = null;//��������
	private HashMap index = null;//���� �����������ݿ��Ĵ������ʱ�����
	private String[] headerName = null;
	private int[] headerType = null;
	private String[] headerTypeName = null; //
	private int[] headerLength = null; //�ֶεĿ��
	private int[] precision = null; //��ȷ��
	private int[] scale = null; //С�����λ

	private HashVO[] hashVOs = null; //
	private int totalRecordCount = 0; //ʵ�ʵļ�¼��,��Ϊ�������ڷ�ҳ�Ĺ���,����hashVOs.length��totalRecordCount�������ϸ�һ�µ�,������Ҫ���������!!!
	private String fromSQL = null;  //���ĸ�SQL���ɵ�!

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

	public HashVO[] getHashVOs() {
		return hashVOs;
	}

	public void setHashVOs(HashVO[] hashVOs) {
		this.hashVOs = hashVOs;
	}

	public boolean containsItemKey(String _itemKey) {
		if (headerName != null) {
			for (int i = 0; i < headerName.length; i++) { //���������Ϊ��...
				if (headerName[i].equalsIgnoreCase(_itemKey)) {
					return true;
				}
			}
		}
		return false;
	}

	public String getPkN() {
		return pkN;
	}

	public void setPkN(String pkN) {
		this.pkN = pkN;
	}

	public String getPkC() {
		return pkC;
	}

	public void setPkC(String pkC) {
		this.pkC = pkC;
	}

	public HashMap getIndex() {
		return index;
	}

	//����������Ϣ!!!
	public void setIndex(HashMap index) {
		this.index = index;
	}

	//ʵ�ʼ�¼��!
	public int getTotalRecordCount() {
		return totalRecordCount;
	}

	public void setTotalRecordCount(int totalRecordCount) {
		this.totalRecordCount = totalRecordCount;
	}

	public String getFromSQL() {
		return fromSQL;
	}

	public void setFromSQL(String fromSQL) {
		this.fromSQL = fromSQL;
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
