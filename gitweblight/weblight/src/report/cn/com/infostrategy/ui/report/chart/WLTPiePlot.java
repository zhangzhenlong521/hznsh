package cn.com.infostrategy.ui.report.chart;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;

/**
 * 民生刘红林强烈要求,饼图中一些比例特别小的就不需要显示文字了!因为否则会堆在一起没法看,而只需要显示大比例的图! 所以需要重构PiePlot!! 费老大劲了 ！！！！
 * @author xch
 *
 */
public class WLTPiePlot extends PiePlot {

	private static final long serialVersionUID = 1L;

	/** The default percent label formatter. */
	public static final NumberFormat DEFAULT_PERCENT_FORMATTER = NumberFormat.getPercentInstance();

	/** The default value label formatter. */
	public static final NumberFormat DEFAULT_VALUE_FORMATTER = NumberFormat.getNumberInstance();

	// 设置一个最大百分比 一般section的概率超过这个百分比则显示,否则不显示. 如果所有的section都小于这个百分比则隔三个随机显示
	private static final double THRESHOLDPERCENT = 0.03;

	// 选20多种颜色. 避免有的颜色与显示背景冲突,导致看不清楚图表中的字
	Color[] colors = new Color[] { Color.BLUE, new Color(64, 0, 0), Color.RED, Color.GREEN, Color.MAGENTA, new Color(191, 191, 0), new Color(64, 64, 128), new Color(0, 128, 128), new Color(64, 64, 64), new Color(128, 128, 128), new Color(0, 0, 128), new Color(64, 191, 128), new Color(64, 64, 0),
			new Color(191, 0, 0), new Color(0, 0, 0), new Color(128, 32, 32), new Color(0, 64, 0), new Color(255, 128, 64), new Color(117, 112, 207), new Color(183, 129, 190), new Color(86, 233, 210), new Color(112, 85, 234), }; //

	//jfreechart-1.0.14 可以转动,不需要重构饼图的渲染器 故注释 但不删除 留做参考 【杨科/2013-06-09】
	
/*	public WLTPiePlot(PieDataset dataset) {
		super(dataset);
		setSectionLabelType(PiePlot.NAME_AND_PERCENT_LABELS);
		setNoDataMessage("No data available");
		setSectionLabelGap(0.15d); //设置默认的section label与饼图的距离
	}

	//重写画饼图的方法
	protected void drawPie(Graphics2D g2, Rectangle2D plotArea, PlotRenderingInfo info, int pieIndex, PieDataset data, String label) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// adjust the plot area by the interior spacing value
		double gapHorizontal = plotArea.getWidth() * getInteriorGap();
		double gapVertical = plotArea.getHeight() * getInteriorGap();
		double pieX = plotArea.getX() + gapHorizontal / 2;
		double pieY = plotArea.getY() + gapVertical / 2;
		double pieW = plotArea.getWidth() - gapHorizontal;
		double pieH = plotArea.getHeight() - gapVertical;

		// make the pie area a square if the pie chart is to be circular...
		if (isCircular()) {
			double min = Math.min(pieW, pieH) / 2;
			pieX = (pieX + pieX + pieW) / 2 - min;
			pieY = (pieY + pieY + pieH) / 2 - min;
			pieW = 2 * min;
			pieH = 2 * min;
		}

		Rectangle2D explodedPieArea = new Rectangle2D.Double(pieX, pieY, pieW, pieH);
		double explodeHorizontal = (1 - getRadius()) * pieW;
		double explodeVertical = (1 - getRadius()) * pieH;
		Rectangle2D pieArea = new Rectangle2D.Double(pieX + explodeHorizontal / 2, pieY + explodeVertical / 2, pieW - explodeHorizontal, pieH - explodeVertical);

		// plot the data (unless the dataset is null)...
		if ((data != null) && (data.getKeys().size() > 0)) {

			// get a list of categories...
			List keys = data.getKeys();
			// compute the total value of the data series skipping over the negative values
			double totalValue = DatasetUtilities.calculatePieDatasetTotal(data);

			// For each positive value in the dataseries, compute and draw the corresponding arc.
			double runningTotal = 0;
			int section = 0;
			Iterator iterator = keys.iterator();
			while (iterator.hasNext()) {
				Comparable currentKey = (Comparable) iterator.next();
				Number dataValue = data.getValue(currentKey);
				Double ddataValue = (Double) dataValue;

				if (dataValue != null) {
					double value = dataValue.doubleValue();
					if (value > 0) {

						// draw the pie section...
						double angle1 = getStartAngle() + (getDirection().getFactor() * (runningTotal * 360) / totalValue);
						double angle2 = getStartAngle() + (getDirection().getFactor() * (runningTotal + value) * 360 / totalValue);
						runningTotal += value;

						double angle = (angle2 - angle1);
						if (Math.abs(angle) > this.getMinimumArcAngleToDraw()) {
							Rectangle2D arcBounds = getArcBounds(pieArea, explodedPieArea, angle1, angle2 - angle1, getExplodePercent(section));
							Arc2D.Double arc = new Arc2D.Double(arcBounds, angle1, angle2 - angle1, Arc2D.PIE);

							Paint p = new RadialGradientPaint(arcBounds, new float[] { 0f, 0.75f, 1f }, new Color[] { colors[section % colors.length], colors[section % colors.length].darker(), colors[section % colors.length].darker().darker() }, CycleMethod.NO_CYCLE);
							Paint outlinePaint = getOutlinePaint(section);
							Stroke outlineStroke = getOutlineStroke(section);
							g2.setPaint(p);
							g2.fill(arc);
							g2.setStroke(outlineStroke);
							g2.setPaint(outlinePaint);
							g2.draw(arc);

							// add a tooltip for the pie section...
							if (info != null) {
								EntityCollection entities = info.getOwner().getEntityCollection();
								if (entities != null) {
									String tip = null;
									if (this.getItemLabelGenerator() != null) {
										tip = this.getItemLabelGenerator().generateToolTip(data, currentKey, pieIndex);
									}
									String url = null;
									if (this.getURLGenerator() != null) {
										url = this.getURLGenerator().generateURL(data, currentKey, pieIndex);
									}
									PieSectionEntity entity = new PieSectionEntity(arc, getDataset(), pieIndex, section, currentKey, tip, url);
									entities.addEntity(entity);
								}
							}
						}

						// then draw the label...
						if (this.getSectionLabelType() != NO_LABELS) {

							//如果所有的概率都太小, 则隔3个一显示
							if (isAllValueTooSmall(data)) {
								if (section % 3 == 0) {
									drawLabel(g2, pieArea, explodedPieArea, data, value, section, angle1, angle2 - angle1);
								}
							} else if (isDrawMyLabelText(ddataValue, totalValue)) {//根据概率选择的显示标签
								drawLabel(g2, pieArea, explodedPieArea, data, value, section, angle1, angle2 - angle1);
							}
						}

					}
				}
				section = section + 1;
			}

			// draw the series label
			if (label != null) {
				g2.setPaint(getSeriesLabelPaint());
				g2.setFont(getSeriesLabelFont());

				Rectangle2D bounds = g2.getFontMetrics().getStringBounds(label, g2);
				double labelX = pieX + (pieW / 2) - (bounds.getWidth() / 2);
				double labelY = pieY + pieH + 2 * bounds.getHeight();
				g2.drawString(label, (int) labelX, (int) labelY);
			}
		} else {
			drawNoDataMessage(g2, plotArea);
		}
	}

	//判断section的概率是否小于门限概率
	private boolean isDrawMyLabelText(double dataValue, double totalValue) {
		if (dataValue / totalValue <= THRESHOLDPERCENT)
			return false;
		else
			return true;
	}

	//判断是不是所有的section的概率都小于门限概率
	private boolean isAllValueTooSmall(PieDataset data) {

		// get a list of categories...
		List keys = data.getKeys();
		// compute the total value of the data series skipping over the negative values
		double totalValue = DatasetUtilities.calculatePieDatasetTotal(data);

		Iterator iterator = keys.iterator();
		int counter = 0;
		while (iterator.hasNext()) {
			Comparable currentKey = (Comparable) iterator.next();
			Number dataValue = data.getValue(currentKey);
			Double ddataValue = (Double) dataValue;

			if (ddataValue / totalValue <= THRESHOLDPERCENT)
				counter++;
		}

		if (counter == keys.size())
			return true;

		else
			return false;
	}

	//重写画label的方法
	protected void drawLabel(Graphics2D g2, Rectangle2D pieArea, Rectangle2D explodedPieArea, PieDataset data, double value, int section, double startAngle, double extent) {
		// handle label drawing...
		FontRenderContext frc = g2.getFontRenderContext();
		String label = "";
		if (getSectionLabelType() == NAME_LABELS) {
			label = data.getKey(section).toString();
		} else if (this.getSectionLabelType() == VALUE_LABELS) {
			label = DEFAULT_VALUE_FORMATTER.format(value);
		} else if (this.getSectionLabelType() == PERCENT_LABELS) {
			label = DEFAULT_PERCENT_FORMATTER.format(extent / 360 * this.getDirection().getFactor());
		} else if (this.getSectionLabelType() == NAME_AND_VALUE_LABELS) {
			label = data.getKey(section).toString() + " (" + DEFAULT_VALUE_FORMATTER.format(value) + ")";
		} else if (this.getSectionLabelType() == NAME_AND_PERCENT_LABELS) {
			label = data.getKey(section).toString() + " (" + DEFAULT_PERCENT_FORMATTER.format(extent / 360 * this.getDirection().getFactor()) + ")";
		} else if (this.getSectionLabelType() == VALUE_AND_PERCENT_LABELS) {
			label = DEFAULT_VALUE_FORMATTER.format(value) + " (" + DEFAULT_PERCENT_FORMATTER.format(extent / 360 * this.getDirection().getFactor()) + ")";
		}
		FontMetrics fm = g2.getFontMetrics(this.getSectionPaint());
		Rectangle2D labelBounds = fm.getStringBounds(label, g2);
		LineMetrics lm = this.getSectionPaint().getLineMetrics(label, frc);
		double ascent = lm.getAscent();

		根据section的大小改变sectionLabelGap, 因为距离改变不明显,暂不使用
		
		double totalValue = DatasetUtilities.getPieDatasetTotal(data);
		
		double sectionPersent= value/totalValue;
		double variableSectionLabelGap=0.01*sectionPersent*sectionPersent+0.19d*sectionPersent+0.1d ;
		//double variableSectionLabelGap=0.2d*sectionPersent+0.1d ;
		setSectionLabelGap(variableSectionLabelGap);
		

		Point2D labelLocation = calculateLabelLocation(labelBounds, ascent, pieArea, explodedPieArea, startAngle, extent, getExplodePercent(section));

		Composite saveComposite = g2.getComposite();
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g2.setPaint(this.getLabelPaint());
		g2.setPaint(this.getSectionPaint());
		Paint itemColor = getPaint(section);
		g2.setPaint(itemColor); //
		if (label.length() >= 6) {//如果标签的字长大于或者等于6 换行显示

			String label1 = label.substring(0, 3);
			String label2 = label.substring(3, label.length());

			g2.drawString(label1, (float) labelLocation.getX(), (float) labelLocation.getY());
			g2.drawString(label2, (float) labelLocation.getX(), (float) labelLocation.getY() + 10);

		} else {//小于6个则只画一行
			g2.drawString(label, (float) labelLocation.getX(), (float) labelLocation.getY());
		}

		g2.setComposite(saveComposite);

	}

	// 重写getPaint方法,获取预定的颜色
	public Paint getPaint(int section) {
		return colors[section % colors.length];
	}*/

}
