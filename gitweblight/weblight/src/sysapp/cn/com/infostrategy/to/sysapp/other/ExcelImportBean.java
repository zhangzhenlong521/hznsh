package cn.com.infostrategy.to.sysapp.other;

import java.io.Serializable;

import cn.com.infostrategy.to.common.HashVO;

public class ExcelImportBean implements Serializable {
	private int row;
	private String col;
	private String msg;
	public final static int WARN = 0;
	public final static int ERROR = -1;
	private int type; //¾¯¸æ£¬´íÎó

	public ExcelImportBean(int _row, String _col, String _msg, int _type) {
		this.row = _row;
		this.col = _col;
		this.msg = _msg;
		this.type = _type;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public String getCol() {
		return col;
	}

	public void setCol(String col) {
		this.col = col;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public HashVO getHashVO() {
		HashVO vo = new HashVO();
		vo.setAttributeValue("row", row);
		vo.setAttributeValue("col", col);
		vo.setAttributeValue("msg", msg);
		vo.setAttributeValue("type", type);
		return vo;
	}
}