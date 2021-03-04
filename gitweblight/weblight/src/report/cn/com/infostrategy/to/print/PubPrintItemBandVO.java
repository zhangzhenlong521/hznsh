package cn.com.infostrategy.to.print;

import java.io.Serializable;

/**
 * ��ӡģ����ϸ�����
 * ����ǿ�Ƭʽ��ӡ,��ֻ��һ������.Ŀǰһ�����ݲ�֧�ַ�ҳ����.
 * ������б�ʽ��ӡ,������ж���,�ҷ�ҳ.
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

	private String fonttype = null; //��������
	private int fontsize = 10; //�����С
	private int fontstyle = 0; //������,����/б��,��

	private int halign = 1; //����λ��
	private int valign = 1; //����λ��
	private int layer = 100; //��
	
	
	private String foreground = null;  //
	private String background = null;  //

	private boolean showBorder = false; //�Ƿ���ʾ�߿�..
	private boolean showBaseline = false; //�Ƿ���ʾ����..

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
