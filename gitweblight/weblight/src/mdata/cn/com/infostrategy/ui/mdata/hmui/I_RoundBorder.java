package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;
import javax.swing.plaf.UIResource;
import javax.swing.text.JTextComponent;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.mdata.hmui.I_RoundBorder 
 * @Description: ±ßÏß
 * @author haoming
 * @date Mar 20, 2013 2:18:01 PM
 *  
*/
public class I_RoundBorder extends LineBorder implements UIResource {
	public static final Color defaultLineColor = new Color(188, 188, 188);

	protected int arcWidth = 0;

	public I_RoundBorder() {
		this(defaultLineColor, 1);
	}

	public I_RoundBorder(Color color) {
		this(color, 1);
	}

	public I_RoundBorder(int thickness) {
		this(defaultLineColor, thickness);
	}

	public I_RoundBorder(Color color, int thickness) {
		super(color, thickness);
	}

	public Insets getBorderInsets(Component c) {
		return getBorderInsets(c, new Insets(0, 0, 0, 0));
	}

	public Insets getBorderInsets(Component c, Insets insets) {
		Insets margin = null;

		if ((c instanceof AbstractButton)) {
			margin = ((AbstractButton) c).getMargin();
		} else if ((c instanceof JToolBar)) {
			margin = ((JToolBar) c).getMargin();
		} else if ((c instanceof JTextComponent)) {
			margin = ((JTextComponent) c).getMargin();
		}
		insets.top = ((margin != null ? margin.top : 0) + this.thickness);
		insets.left = ((margin != null ? margin.left : 0) + this.thickness);
		insets.bottom = ((margin != null ? margin.bottom : 0) + this.thickness);
		insets.right = ((margin != null ? margin.right : 0) + this.thickness);

		return insets;
	}

	public void paintBorder(Component c, Graphics _g, int x, int y, int width, int height) {
		Graphics2D g2d = (Graphics2D) _g.create();
		Color oldColor = g2d.getColor();

		((Graphics2D) g2d).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Component cp = c.getParent();

		g2d.setColor(this.lineColor);

		for (int i = 0; i < this.thickness; i++) {
			g2d.drawRoundRect(x + i, y + i, width - i - i - 1, height - i - i - 1, this.arcWidth, this.arcWidth);
			if (this.thickness > 1) {
				g2d.setColor(LAFUtil.getColor(g2d.getColor(), 70, 70, 70, -50));
			}
		}
		g2d.dispose();
	}

	public I_RoundBorder setLineColor(Color c) {
		this.lineColor = c;
		return this;
	}

	public Color getLineColor() {
		return this.lineColor;
	}

	public I_RoundBorder setThickness(int t) {
		this.thickness = t;
		return this;
	}

	public Object clone() {
		I_RoundBorder bb = new I_RoundBorder(getLineColor(), getThickness());
		return bb;
	}

	public int getArcWidth() {
		return this.arcWidth;
	}

	public I_RoundBorder setArcWidth(int arcWidth) {
		this.arcWidth = arcWidth;
		return this;
	}

	public static class UIResource extends I_RoundBorder implements javax.swing.plaf.UIResource {

		public UIResource() {
			super();
		}
	}

}