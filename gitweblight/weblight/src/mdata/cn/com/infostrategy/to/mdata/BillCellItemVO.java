package cn.com.infostrategy.to.mdata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * BillCellPanel��һ�������д洢������
 * @author xch
 *
 */
public class BillCellItemVO implements Serializable, Cloneable {

	private static final long serialVersionUID = 3378255126526311412L;

	private int cellrow;
	private int cellcol;

	private String cellkey; //Ψһ��ʶ
	private String cellvalue; //ʵ�ʵ�ֵ
	private String celldesc; //���Ӷ���˵��..
	private String cellhelp; //����˵��
	private String celltype; //����
	private String iseditable; //�Ƿ�ɱ༭
	private String ishtmlhref; //�Ƿ���html��������ʾ�ķ�ʽ(ȡֵY/N),��Ϊ����౨������ĵط�,��Ҫһ����html������(���и��»���)�ķ�ʽ��ʾ,Ȼ��������Ե���չʾһ���´���,��ʵ����ȡЧ��,����ǰ��������������˲�֪��������Ե��!!!

	private String rowheight; //�и�
	private String colwidth = "80"; //�п�

	private String loadformula; //���ع�ʽ
	private String editformula; //�༭��ʽ
	private String validateformula; //У�鹫ʽ...
	private String avgformula; // ������   ����ƽ���ֵĹ�ʽ   gaofeng

	//��ɫ
	private String foreground; //
	private String background;

	//�������
	private String fonttype;
	private String fontstyle;
	private String fontsize;

	private int halign = 1; //��������,1-����;2-����;3-����
	private int valign = 1; //��������,1-����;2-����;3-����

	private String span; //

	private HashMap custMap = new HashMap(); //���Է�һ���Զ������

	private boolean itemTypechanged = false; //

	public String getAvgformula() {
		return avgformula;
	}

	public void setAvgformula(String avgformula) {
		this.avgformula = avgformula;
	}

	public int getCellrow() {
		return cellrow;
	}

	public void setCellrow(int cellrow) {
		this.cellrow = cellrow;
	}

	public int getCellcol() {
		return cellcol;
	}

	public void setCellcol(int cellcol) {
		this.cellcol = cellcol;
	}

	public String getCellvalue() {
		return cellvalue;
	}

	public void setCellvalue(String cellvalue) {
		this.cellvalue = cellvalue;
	}

	public String getCellhelp() {
		return cellhelp;
	}

	public void setCellhelp(String cellhelp) {
		this.cellhelp = cellhelp;
	}

	public String getCelltype() {
		return celltype;
	}

	public void setCelltype(String celltype) {
		this.celltype = celltype;
	}

	public String getRowheight() {
		return rowheight;
	}

	public void setRowheight(String rowheight) {
		this.rowheight = rowheight;
	}

	public String getColwidth() {
		return colwidth;
	}

	public void setColwidth(String colwidth) {
		this.colwidth = colwidth;
	}

	public String getForeground() {
		return foreground;
	}

	public void setForeground(String foreground) {
		this.foreground = foreground;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getFonttype() {
		return fonttype;
	}

	public void setFonttype(String fonttype) {
		this.fonttype = fonttype;
	}

	public String getFontstyle() {
		return fontstyle;
	}

	public void setFontstyle(String fontstyle) {
		this.fontstyle = fontstyle;
	}

	public String getFontsize() {
		return fontsize;
	}

	public void setFontsize(String fontsize) {
		this.fontsize = fontsize;
	}

	public String getSpan() {
		return span;
	}

	/**
	 * ���úϲ�
	 * @param span,��ʽ��"3,2"����ʾ���¿�3��,���ҿ�2��,�����������"1,1"
	 */
	public void setSpan(String span) {
		this.span = span;
	}

	public String getIseditable() {
		return iseditable;
	}

	public void setIseditable(String iseditable) {
		this.iseditable = iseditable;
	}

	public String getIshtmlhref() {
		return ishtmlhref;
	}

	//��ֵ�ǡ�Y����N������
	public void setIshtmlhref(String ishtmlhref) {
		this.ishtmlhref = ishtmlhref;
	}

	public String toString() {
		return this.cellvalue; //
	}

	public String getCellkey() {
		return cellkey;
	}

	public void setCellkey(String cellkey) {
		this.cellkey = cellkey;
	}

	public String getCelldesc() {
		return celldesc;
	}

	public void setCelldesc(String celldesc) {
		this.celldesc = celldesc;
	}

	public boolean isItemTypechanged() {
		return itemTypechanged;
	}

	public void setItemTypechanged(boolean itemTypechanged) {
		this.itemTypechanged = itemTypechanged;
	}

	public int getHalign() {
		return halign;
	}

	public void setHalign(int haligh) {
		this.halign = haligh;
	}

	public int getValign() {
		return valign;
	}

	public void setValign(int valign) {
		this.valign = valign;
	}

	/**
	 * �����Զ������
	 * @param _key
	 * @param _value
	 */
	public void setCustProperty(Object _key, Object _value) {
		this.custMap.put(_key, _value); //
	}

	public void setCustProperty(Map _map) {
		this.custMap.putAll(_map); //
	}

	/**
	 * ȡ���Զ������.
	 * @param _key
	 * @return
	 */
	public Object getCustProperty(Object _key) {
		return custMap.get(_key); //
	}

	//���������Լ�����Map
	public HashMap getCustPropMap() {
		return custMap; //
	}

	public String getLoadformula() {
		return loadformula;
	}

	public void setLoadformula(String loadformula) {
		this.loadformula = loadformula;
	}

	public String getEditformula() {
		return editformula;
	}

	public void setEditformula(String editformula) {
		this.editformula = editformula;
	}

	public String getValidateformula() {
		return validateformula;
	}

	public void setValidateformula(String validateformula) {
		this.validateformula = validateformula;
	}

	public BillCellItemVO deepClone() {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(buf);
			out.writeObject(this);
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
			return (BillCellItemVO) in.readObject();
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

}
