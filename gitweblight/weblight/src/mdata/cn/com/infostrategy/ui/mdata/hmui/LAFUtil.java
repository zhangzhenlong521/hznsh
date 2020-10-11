package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.PrintGraphics;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.print.PrinterGraphics;

import javax.swing.JComponent;
import javax.swing.UIManager;

import sun.print.ProxyPrintGraphics;
import sun.swing.PrintColorUIResource;
import sun.swing.SwingUtilities2;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.mdata.hmui.LAFUtil 
 * @Description: Æ¤·ô¹¤¾ßÀà¡£
 * @author haoming
 * @date Mar 19, 2013 4:27:58 PM
 *  
*/
public class LAFUtil {
	//
	public static boolean getBoolean(String key) {
		Object value = UIManager.get(key);
		return (value instanceof Boolean) ? ((Boolean) value).booleanValue() : false;
	}

	//ÉèÖÃ¿¹¾â³Ý
	public static void setAntiAliasing(Graphics2D g2, boolean antiAliasing) {
		if (antiAliasing)
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		else
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
	}

	public static void fillTextureRoundRec(Graphics2D g2d, Color baseColor, int x, int y, int w, int h, int arc, int colorDelta) {
		Graphics2D g2 = (Graphics2D) g2d.create();
		setAntiAliasing(g2, true);
		GradientPaint gp = new GradientPaint(x, y, getColor(baseColor, colorDelta, colorDelta, colorDelta), x, y + h, baseColor);
		g2.setPaint(gp);
		g2.fillRoundRect(x, y, w, h, arc, arc);
		g2.dispose();
	}

	public static Color getColor(Color basic, int r, int g, int b) {
		return new Color(getColorInt(basic.getRed() + r), getColorInt(basic.getGreen() + g), getColorInt(basic.getBlue() + b), getColorInt(basic.getAlpha()));
	}

	public static Color getColor(Color basic, int r, int g, int b, int a) {
		return new Color(getColorInt(basic.getRed() + r), getColorInt(basic.getGreen() + g), getColorInt(basic.getBlue() + b), getColorInt(basic.getAlpha() + a));
	}

	public static int getColorInt(int rgb) {
		return rgb >= 0 ? rgb <= 255 ? rgb : 255 : '\0';
	}

	public static void drawRoundRect(Graphics2D _g2d, int _x, int _y, int _width, int _height, int _arc, Color _color, float stroke) {
		setAntiAliasing(_g2d, true);
		stroke = stroke <= 0 ? 1 : stroke;
		_g2d.setStroke(new BasicStroke(stroke));
		_g2d.setColor(_color);
		_g2d.drawRoundRect(_x, _y, _width, _height, _arc, _arc);
		setAntiAliasing(_g2d, false);
	}

	public static void getTextFieldBgNormal(Graphics2D _g2d, Rectangle _rec) {
		drawRoundRect(_g2d, (int) _rec.getX(), (int) _rec.getY(), (int) _rec.getWidth(), (int) _rec.getHeight() - 1, 15, Color.DARK_GRAY, 1);
	}

	public static void getTextFieldBgFocused(Graphics2D _g2d, Rectangle _rec) {
		drawRoundRect(_g2d, (int) _rec.getX(), (int) _rec.getY(), (int) _rec.getWidth(), (int) _rec.getHeight() - 1, 15, Color.BLUE, 2);
	}

	public static void getTextFieldBgDisabled(Graphics2D _g2d, Rectangle _rec) {
		drawRoundRect(_g2d, (int) _rec.getX(), (int) _rec.getY(), (int) _rec.getWidth(), (int) _rec.getHeight() - 1, 15, Color.GRAY, 1);
	}

	private static Color color_alpha = new Color(250, 250, 250, 100); //·¢¹âµÄÍ¸Ã÷°×É«

	public static void drawButtonBackground(Graphics2D _g2d, int _x, int _y, int _width, int _height, int _arc, Color _color, float stroke) {
		setAntiAliasing(_g2d, true);
		GradientPaint paint = new GradientPaint(0, 0, _color, 0, (int) (_height * 1.5), Color.WHITE);
		//Ìî³äÔ²½Ç±³¾°
		_g2d.setPaint(paint);
		_g2d.fillRoundRect(_x, _y, _width, _height, _arc, _arc);

		//Ìî³ä·¢¹â		

		_g2d.setColor(color_alpha);
		_g2d.fillRoundRect(_x + 3, _y + 2, _width - 6, 10, 5, 10);

		//»­±ß¿ò
		stroke = stroke <= 0 ? 1 : stroke;
		_g2d.setStroke(new BasicStroke(stroke));
		_g2d.setColor(_color.darker());
		_g2d.drawRoundRect(_x, _y, _width, _height, _arc, _arc);

		setAntiAliasing(_g2d, false);
	}

	public abstract interface TextComponentFocusActionIfc {
		public abstract void switchBgToNomal();

		public abstract void switchBgToFocused();
	}

	public static class I_SwingUtilities2{
		public static I_SwingUtilities2 swingUtil = new I_SwingUtilities2();
		public Graphics2D getGraphics2D(Graphics g) {
			if (g instanceof Graphics2D)
				return (Graphics2D) g;
			if (g instanceof ProxyPrintGraphics)
				return (Graphics2D) (Graphics2D) ((ProxyPrintGraphics) g).getGraphics();
			else
				return null;
		}

		boolean isPrinting(Graphics g) {
			return (g instanceof PrinterGraphics) || (g instanceof PrintGraphics);
		}

		public void drawString(JComponent jcomponent, Graphics g, String s, int i, int j) {
			if (s == null || s.length() <= 0)
				return;
			if (isPrinting(g)) {
				Graphics2D graphics2d = getGraphics2D(g);
				if (graphics2d != null) {
					float f = (float) graphics2d.getFont().getStringBounds(s, DEFAULT_FRC).getWidth();
					TextLayout textlayout = new TextLayout(s, graphics2d.getFont(), graphics2d.getFontRenderContext());
					textlayout = textlayout.getJustifiedLayout(f);
					Color color = graphics2d.getColor();
					if (color instanceof PrintColorUIResource)
						graphics2d.setColor(((PrintColorUIResource) color).getPrintColor());
					graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
					textlayout.draw(graphics2d, i, j);
					graphics2d.setColor(color);
					return;
				}
			}
			g.drawString(s, i, j);
		}

		public final FontRenderContext DEFAULT_FRC = new FontRenderContext(null, false, false);

		public void drawStringUnderlineCharAt(JComponent jcomponent, Graphics g, String s, int i, int j, int k) {
			if (s == null || s.length() <= 0)
				return;
			drawString(jcomponent, g, s, j, k);
			int l = s.length();
			if (i >= 0 && i < l) {
				int i1 = k;
				int j1 = 1;
				int k1 = 0;
				int l1 = 0;
				boolean flag = isPrinting(g);
				boolean flag1 = flag;
				if (!flag1) {
					FontMetrics fontmetrics = g.getFontMetrics();
					k1 = j + SwingUtilities2.stringWidth(jcomponent, fontmetrics, s.substring(0, i));
					l1 = fontmetrics.charWidth(s.charAt(i));
				} else {
					Graphics2D graphics2d = getGraphics2D(g);
					if (graphics2d != null) {
						TextLayout textlayout = new TextLayout(s, graphics2d.getFont(), graphics2d.getFontRenderContext());
						if (flag) {
							float f = (float) graphics2d.getFont().getStringBounds(s, DEFAULT_FRC).getWidth();
							textlayout = textlayout.getJustifiedLayout(f);
						}
						TextHitInfo texthitinfo = TextHitInfo.leading(i);
						TextHitInfo texthitinfo1 = TextHitInfo.trailing(i);
						Shape shape = textlayout.getVisualHighlightShape(texthitinfo, texthitinfo1);
						Rectangle rectangle = shape.getBounds();
						k1 = j + rectangle.x;
						l1 = rectangle.width;
					}
				}
				g.fillRect(k1, i1 + 1, l1, j1);
			}
		}
	}
}
