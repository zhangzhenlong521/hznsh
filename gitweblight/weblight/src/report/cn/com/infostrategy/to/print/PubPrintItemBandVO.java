package cn.com.infostrategy.to.print;

import java.io.Serializable;

/**
 * 打印模板明细项对象
 * 如果是卡片式打印,则只有一条数据.目前一行数据不支持分页处理.
 * 如果是列表式打印,则可能有多条,且分页.
 * @author xch
 *
 */
public class PubPrintItemBandVO implements Serializable {

	private static final long serialVersionUID = 774326255270948475L;

	private String itemkey = null; //ItemKey
	private String itemname = null; //ItemName
	private double x = 0; //
	private double y = 0; //

	private int width = 200;
	private int height = 20;

	private String fonttype = null; //字体类型
	private int fontsize = 10; //字体大小
	private int fontstyle = 0; //字体风格,粗体/斜体,等

	private int halign = 1; //左右位置
	private int valign = 1; //上下位置
	private int layer = 100; //层
	
	
	private String foreground = null;  //
	private String background = null;  //

	private boolean showBorder = false; //是否显示边框..
	private boolean showBaseline = false; //是否显示底线..

	public String getItemkey() {
		return itemkey;
	}

	public void setItemkey(String itemkey) {
		this.itemkey = itemkey;
	}

	public String getItemname() {
		return itemname;
	}

	public void setItemname(String itemname) {
		this.itemname = itemname;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getFonttype() {
		return fonttype;
	}

	public void setFonttype(String fonttype) {
		this.fonttype = fonttype;
	}

	public int getFontsize() {
		return fontsize;
	}

	public void setFontsize(int fontsize) {
		this.fontsize = fontsize;
	}

	public int getFontstyle() {
		return fontstyle;
	}

	public void setFontstyle(int fontstyle) {
		this.fontstyle = fontstyle;
	}

	public int getHalign() {
		return halign;
	}

	public void setHalign(int halign) {
		this.halign = halign;
	}

	public int getValign() {
		return valign;
	}

	public void setValign(int valign) {
		this.valign = valign;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public boolean isShowBorder() {
		return showBorder;
	}

	public void setShowBorder(boolean showBorder) {
		this.showBorder = showBorder;
	}

	public boolean isShowBaseline() {
		return showBaseline;
	}

	public void setShowBaseline(boolean showBaseline) {
		this.showBaseline = showBaseline;
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

}
