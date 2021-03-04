package cn.com.infostrategy.to.mdata;

import java.io.Serializable;

public class BillCellVO implements Serializable {

	private static final long serialVersionUID = 6202527480266853131L;

	private String id = null; //主键
	private String templetcode = null; //模板编码
	private String templetname = null; //模板名称
	private String billNo = null; //单据编码
	private String descr = null;  //备注说明

	private int rowlength; //总共有几行
	private int collength; //总共有几列
	private String seq;

	private BillCellItemVO[][] cellItemVOs = null; //每个格子的实际数据
	
	

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getTempletcode() {
		return templetcode;
	}

	public void setTempletcode(String templetcode) {
		this.templetcode = templetcode;
	}

	public String getTempletname() {
		return templetname;
	}

	public void setTempletname(String templetname) {
		this.templetname = templetname;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public int getRowlength() {
		return rowlength;
	}

	public void setRowlength(int rowlength) {
		this.rowlength = rowlength;
	}

	public int getCollength() {
		return collength;
	}

	public void setCollength(int collength) {
		this.collength = collength;
	}

	public BillCellItemVO[][] getCellItemVOs() {
		return cellItemVOs;
	}

	public void setCellItemVOs(BillCellItemVO[][] cellItemVOs) {
		this.cellItemVOs = cellItemVOs;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

}
