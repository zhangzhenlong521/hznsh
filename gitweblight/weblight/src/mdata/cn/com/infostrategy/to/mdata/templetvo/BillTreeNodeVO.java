package cn.com.infostrategy.to.mdata.templetvo;

import java.io.Serializable;

/**
 * BillTreePanel每个结点实际存储的对象,以前存储的是BillVO,但后来发生性能问题,所以改成该对象了
 * 其中RowNo是对应于realStrData中的第几行,而Text则是实际显示的数据
 * @author xch
 */
public class BillTreeNodeVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private int rowNo = -1; //行号,它对应于realStrData数组的第几行
	private String text = ""; //实际显示的文本
	private String iconName = null; //结点图标的名称,有时需要指定图标名称
	private boolean isVirtualNode = false; //是否是虚拟结点?

	public BillTreeNodeVO(int _rowNo, String _text) {
		this.rowNo = _rowNo;
		this.text = _text;
	}

	public BillTreeNodeVO(int _rowNo, String _text, String _iconName) {
		this.rowNo = _rowNo; //行号
		this.text = _text; //文本
		this.iconName = _iconName; //图标名
	}

	public BillTreeNodeVO(int _rowNo, String _text, String _iconName, boolean _isVirtualNode) {
		this.rowNo = _rowNo; //行号
		this.text = _text; //文本
		this.iconName = _iconName; //图标名
		this.isVirtualNode = _isVirtualNode; //是否是虚拟结点??
	}

	public int getRowNo() {
		return rowNo;
	}

	public void setRowNo(int rowNo) {
		this.rowNo = rowNo;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	//取得图标名称
	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	public boolean isVirtualNode() {
		return isVirtualNode;
	}

	public void setVirtualNode(boolean isVirtualNode) {
		this.isVirtualNode = isVirtualNode;
	}

	@Override
	public String toString() {
		return getText(); //显示文本,必须重构,保证树显示和数据是对的
	}

}
