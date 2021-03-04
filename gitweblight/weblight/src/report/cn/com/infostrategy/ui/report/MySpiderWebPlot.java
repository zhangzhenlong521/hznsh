package cn.com.infostrategy.ui.report;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import org.jfreechart.chart.entity.CategoryItemEntity;
import org.jfreechart.chart.entity.EntityCollection;
import org.jfreechart.chart.plot.PlotRenderingInfo;
import org.jfreechart.chart.plot.PlotState;
import org.jfreechart.chart.plot.SpiderWebPlot;
import org.jfreechart.data.category.CategoryDataset;
import org.jfreechart.util.TableOrder;

/**
 *  绘制蜘蛛网
 * 
 * @author hm
 * 
 */
public class MySpiderWebPlot extends SpiderWebPlot {
	private int ticks = DEFAULT_TICKS;
	private static final int DEFAULT_TICKS = 5;
	private NumberFormat format = NumberFormat.getInstance();
	private static final double PERPENDICULAR = 90;
	private static final double TICK_SCALE = 0.01; // 刻度 标示线的长短。
	private int valueLabelGap = DEFAULT_GAP;
	private static final int DEFAULT_GAP = 10;
	private static final double THRESHOLD = 15;
	private boolean webFilled;

	public MySpiderWebPlot(CategoryDataset createCategoryDataset) {
		super(createCategoryDataset);
		webFilled = true;
	}

	public void draw(Graphics2D g2, Rectangle2D area, Point2D anchor, PlotState parentState, PlotRenderingInfo info) {
		super.draw(g2, area, anchor, parentState, info);
	} //橙色

	Color color[] = new Color[] { new Color(255, 0, 0, 128), new Color(254, 115, 17, 128), new Color(255, 255, 0, 128), new Color(0, 255, 0, 128), new Color(0, 255, 255, 128) };

	protected void drawLabel(final Graphics2D g2, final Rectangle2D plotArea, final double value, final int cat, final double startAngle, final double extent) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.drawLabel(g2, plotArea, value, cat, startAngle, extent);
		final FontRenderContext frc = g2.getFontRenderContext();
		final double[] transformed = new double[2];
		final double[] transformer = new double[2];
		final Arc2D arc1 = new Arc2D.Double(plotArea, startAngle, 0, Arc2D.OPEN);
		for (int i = 1; i <= ticks; i++) {
			final Point2D point1 = arc1.getEndPoint();
			final double deltaX = plotArea.getCenterX();
			final double deltaY = plotArea.getCenterY();
			double labelX = point1.getX() - deltaX;
			double labelY = point1.getY() - deltaY;
			final double scale = ((double) i / (double) ticks);
			final AffineTransform tx = AffineTransform.getScaleInstance(scale, scale);
			final AffineTransform pointTrans = AffineTransform.getScaleInstance(scale + TICK_SCALE, scale + TICK_SCALE);
			transformer[0] = labelX;
			transformer[1] = labelY;
			pointTrans.transform(transformer, 0, transformed, 0, 1);
			final double pointX = transformed[0] + deltaX;
			final double pointY = transformed[1] + deltaY;
			tx.transform(transformer, 0, transformed, 0, 1);
			labelX = transformed[0] + deltaX;
			labelY = transformed[1] + deltaY;
			double rotated = (PERPENDICULAR);
			AffineTransform rotateTrans = AffineTransform.getRotateInstance(Math.toRadians(rotated), labelX, labelY);
			transformer[0] = pointX;
			transformer[1] = pointY;
			rotateTrans.transform(transformer, 0, transformed, 0, 1);
			final double x1 = transformed[0];
			final double y1 = transformed[1];
			rotated = (-PERPENDICULAR);
			rotateTrans = AffineTransform.getRotateInstance(Math.toRadians(rotated), labelX, labelY);
			rotateTrans.transform(transformer, 0, transformed, 0, 1);
			final Composite saveComposite = g2.getComposite();
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			g2.draw(new Line2D.Double(transformed[0], transformed[1], x1, y1));
			if (startAngle == this.getStartAngle()) {
				//画刻度圆
				g2.setPaint(color[i % color.length]);
				double arcR = deltaY - y1;
				Arc2D arc = new Arc2D.Double(deltaX - arcR, deltaY - arcR, arcR * 2, arcR * 2, 0, 360, Arc2D.OPEN);
				g2.draw(arc);

				final String label = format.format(((double) i / (double) ticks) * this.getMaxValue());
				final LineMetrics lm = getLabelFont().getLineMetrics(label, frc);
				final double ascent = lm.getAscent();
				if (Math.abs(labelX - plotArea.getCenterX()) < THRESHOLD) {
					labelX += valueLabelGap;
					labelY += ascent / (float) 2;
				} else if (Math.abs(labelY - plotArea.getCenterY()) < THRESHOLD) {
					labelY += valueLabelGap;
				} else if (labelX >= plotArea.getCenterX()) {
					if (labelY < plotArea.getCenterY()) {
						labelX += valueLabelGap;
						labelY += valueLabelGap;
					} else {
						labelX -= valueLabelGap;
						labelY += valueLabelGap;
					}
				} else {
					if (labelY > plotArea.getCenterY()) {
						labelX -= valueLabelGap;
						labelY -= valueLabelGap;
					} else {
						labelX += valueLabelGap;
						labelY -= valueLabelGap;
					}
				}
				g2.setPaint(getLabelPaint());
				g2.setFont(DEFAULT_LABEL_FONT);
				g2.drawString(label, (float) labelX, (float) labelY);
				g2.setFont(getLabelFont());
			}
			g2.setComposite(saveComposite);
		}
	}

	/**
	 * 重写画雷达区域
	 */
	protected void drawRadarPoly(Graphics2D g2, Rectangle2D plotArea, Point2D centre, PlotRenderingInfo info, int series, int catCount, double headH, double headW) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Polygon polygon = new Polygon();
		EntityCollection entities = null;
		if (info != null)
			entities = info.getOwner().getEntityCollection();
		for (int cat = 0; cat < catCount; cat++) {
			Number dataValue = getPlotValue(series, cat);
			if (dataValue != null) {
				double value = dataValue.doubleValue();
				if (value >= 0.0D) {
					double angle = getStartAngle() + (getDirection().getFactor() * (double) cat * 360D) / (double) catCount;
					Point2D point = getWebPoint(plotArea, angle, value / getMaxValue());
					polygon.addPoint((int) point.getX(), (int) point.getY());
					Paint paint = getSeriesPaint(series);
					Paint outlinePaint = getSeriesOutlinePaint(series);
					Stroke outlineStroke = getSeriesOutlineStroke(series);
					Ellipse2D head = new java.awt.geom.Ellipse2D.Double(point.getX() - headW / 2D, point.getY() - headH / 2D, headW, headH);
					g2.setPaint(paint);
					g2.fill(head);
					g2.setStroke(outlineStroke);
					g2.setPaint(outlinePaint);
					g2.draw(head);
					if (entities != null) {
						int row = 0;
						int col = 0;
						if (getDataExtractOrder() == TableOrder.BY_ROW) {
							row = series;
							col = cat;
						} else {
							row = cat;
							col = series;
						}
						String tip = null;
						if (getToolTipGenerator() != null)
							tip = getToolTipGenerator().generateToolTip(getDataset(), row, col);
						String url = null;
						if (getURLGenerator() != null)
							url = getURLGenerator().generateURL(getDataset(), row, col);
						Shape area = new Rectangle((int) (point.getX() - headW), (int) (point.getY() - headH), (int) (headW * 2D), (int) (headH * 2D));
						CategoryItemEntity entity = new CategoryItemEntity(area, tip, url, getDataset(), getDataset().getRowKey(row), getDataset().getColumnKey(col));
						entities.add(entity);
					}
				}
			}
		}

		Paint paint = getSeriesPaint(series);
		g2.setPaint(paint);
		g2.setStroke(getSeriesOutlineStroke(series));
		g2.draw(polygon);
		if (webFilled) {
			g2.setComposite(AlphaComposite.getInstance(3, 0.6F));
			g2.fill(polygon);
			g2.setComposite(AlphaComposite.getInstance(3, getForegroundAlpha()));
		}
	}

	public void setWebFilled(boolean webFilled) {
		this.webFilled = webFilled;
		fireChangeEvent();
	}

}
