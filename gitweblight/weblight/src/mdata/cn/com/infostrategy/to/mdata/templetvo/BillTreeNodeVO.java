package cn.com.infostrategy.to.mdata.templetvo;

import java.io.Serializable;

/**
 * BillTreePanelÿ�����ʵ�ʴ洢�Ķ���,��ǰ�洢����BillVO,������������������,���Ըĳɸö�����
 * ����RowNo�Ƕ�Ӧ��realStrData�еĵڼ���,��Text����ʵ����ʾ������
 * @author xch
 */
public class BillTreeNodeVO implements Serializable {
	private static final long serialVersionUID = 1L;
	private int rowNo = -1; //�к�,����Ӧ��realStrData����ĵڼ���
	private String text = ""; //ʵ����ʾ���ı�
	private String iconName = null; //���ͼ�������,��ʱ��Ҫָ��ͼ������
	private boolean isVirtualNode = false; //�Ƿ���������?

	public BillTreeNodeVO(int _rowNo, String _text) {
		this.rowNo = _rowNo;
		this.text = _text;
	}

	public BillTreeNodeVO(int _rowNo, String _text, String _iconName) {
		this.rowNo = _rowNo; //�к�
		this.text = _text; //�ı�
		this.iconName = _iconName; //ͼ����
	}

	public BillTreeNodeVO(int _rowNo, String _text, String _iconName, boolean _isVirtualNode) {
		this.rowNo = _rowNo; //�к�
		this.text = _text; //�ı�
		this.iconName = _iconName; //ͼ����
		this.isVirtualNode = _isVirtualNode; //�Ƿ���������??
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

	//ȡ��ͼ������
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
		return getText(); //��ʾ�ı�,�����ع�,��֤����ʾ�������ǶԵ�
	}

}
