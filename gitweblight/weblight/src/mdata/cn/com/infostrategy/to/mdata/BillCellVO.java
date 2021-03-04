package cn.com.infostrategy.to.mdata;

import java.io.Serializable;

public class BillCellVO implements Serializable {

	private static final long serialVersionUID = 6202527480266853131L;

	private String id = null; //����
	private String templetcode = null; //ģ�����
	private String templetname = null; //ģ������
	private String billNo = null; //���ݱ���
	private String descr = null;  //��ע˵��

	private int rowlength; //�ܹ��м���
	private int collength; //�ܹ��м���
	private String seq;

	private BillCellItemVO[][] cellItemVOs = null; //ÿ�����ӵ�ʵ������
	
	

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
