/**************************************************************************
 * $RCSfile: HashVOStruct.java,v $  $Revision: 1.9 $  $Date: 2012/09/14 09:17:31 $
 **************************************************************************/

package cn.com.infostrategy.to.common;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 带表头表体的HashVO[]对象!!!
 * @author xch
 *
 */
public class HashVOStruct implements Serializable {

	private static final long serialVersionUID = 7758843430724165925L;

	private String tablename = null; //从哪一个取的数,只取第一列的,不支持从多个表取数,除非写视图!!
	private String pkN = null;//主键名称
	private String pkC = null;//主键列名
	private HashMap index = null;//索引 在做导出数据库表的创建语句时候添加
	private String[] headerName = null;
	private int[] headerType = null;
	private String[] headerTypeName = null; //
	private int[] headerLength = null; //字段的宽度
	private int[] precision = null; //精确度
	private int[] scale = null; //小数点后几位

	private HashVO[] hashVOs = null; //
	private int totalRecordCount = 0; //实际的记录数,因为后来存在分页的功能,所以hashVOs.length与totalRecordCount并不是严格一致的,所以需要这个变量了!!!
	private String fromSQL = null;  //从哪个SQL生成的!

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
			for (int i = 0; i < headerName.length; i++) { //如果列名不为空...
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

	//设置索引信息!!!
	public void setIndex(HashMap index) {
		this.index = index;
	}

	//实际记录数!
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
