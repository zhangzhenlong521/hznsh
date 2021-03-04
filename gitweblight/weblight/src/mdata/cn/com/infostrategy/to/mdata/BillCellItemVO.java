package cn.com.infostrategy.to.mdata;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * BillCellPanel中一个格子中存储的数据
 * @author xch
 *
 */
public class BillCellItemVO implements Serializable, Cloneable {

	private static final long serialVersionUID = 3378255126526311412L;

	private int cellrow;
	private int cellcol;

	private String cellkey; //唯一标识
	private String cellvalue; //实际的值
	private String celldesc; //格子定义说明..
	private String cellhelp; //帮助说明
	private String celltype; //类型
	private String iseditable; //是否可编辑
	private String ishtmlhref; //是否是html超链接显示的方式(取值Y/N),因为在许多报表输出的地方,需要一种以html超链接(即有个下划线)的方式显示,然后点击后可以弹出展示一个新窗口,即实现钻取效果,而以前的问题在于许多人不知道那里可以点击!!!

	private String rowheight; //行高
	private String colwidth = "80"; //列宽

	private String loadformula; //加载公式
	private String editformula; //编辑公式
	private String validateformula; //校验公式...
	private String avgformula; // 民生用   计算平均分的公式   gaofeng

	//颜色
	private String foreground; //
	private String background;

	//字体参数
	private String fonttype;
	private String fontstyle;
	private String fontsize;

	private int halign = 1; //左右排序,1-靠左;2-靠中;3-靠右
	private int valign = 1; //上下排序,1-靠顶;2-靠中;3-靠下

	private String span; //

	private HashMap custMap = new HashMap(); //可以放一个自定义对象

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
	 * 设置合并
	 * @param span,格式是"3,2"，表示向下跨3行,向右跨2列,如果不跨则是"1,1"
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

	//赋值是【Y】【N】两种
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
	 * 设置自定义对象
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
	 * 取得自定义对象.
	 * @param _key
	 * @return
	 */
	public Object getCustProperty(Object _key) {
		return custMap.get(_key); //
	}

	//返回整个自己定义Map
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
