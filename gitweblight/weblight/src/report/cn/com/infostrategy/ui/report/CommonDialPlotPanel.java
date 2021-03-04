package cn.com.infostrategy.ui.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import org.jfreechart.chart.ChartPanel;
import org.jfreechart.chart.JFreeChart;
import org.jfreechart.chart.plot.dial.DialBackground;
import org.jfreechart.chart.plot.dial.DialCap;
import org.jfreechart.chart.plot.dial.DialPlot;
import org.jfreechart.chart.plot.dial.DialTextAnnotation;
import org.jfreechart.chart.plot.dial.DialValueIndicator;
import org.jfreechart.chart.plot.dial.StandardDialFrame;
import org.jfreechart.chart.plot.dial.StandardDialRange;
import org.jfreechart.chart.plot.dial.StandardDialScale;
import org.jfreechart.data.general.DefaultValueDataset;
import org.jfreechart.ui.GradientPaintTransformType;
import org.jfreechart.ui.StandardGradientPaintTransformer;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;

/**
 * 仪表盘报表通用实现类
 * 
 * @author Liyj,haoming
 * 
 */
public class CommonDialPlotPanel extends DialPlotPanel {

	DefaultValueDataset dataset1;
	DefaultValueDataset dataset2;

	/**
	 * 单指针仪表盘
	 * 
	 * @param _value
	 *            指针指向的值
	 * @param _maxValue
	 *            表盘最大值
	 * @param _minValue
	 *            表盘最小值
	 * @param _highValue
	 *            高危险度界限值
	 * @param _lowValue
	 *            低危险度界限值
	 * @param _showNumValue
	 *            表盘显示值刻度
	 * @param _topName
	 *            仪表盘顶部标题
	 * @param _lowValue
	 *            仪表盘内底部标题
	 */
	public CommonDialPlotPanel(double _value, double _maxValue, double _minValue, double _highValue, double _lowValue, double _showNumValue, String _topName, String _bottomName) {

		super(new BorderLayout());
		double maxValue = _maxValue;
		double showNumValue = _showNumValue;
		if (_value > maxValue) {
			maxValue = getDefaultMaxNum(_value, _minValue);
			showNumValue = (maxValue - _minValue) / 6;
		}
		dataset1 = new DefaultValueDataset(_value);
		DialPlot dialplot = new DialPlot();
		dialplot.setView(0.0D, 0.0D, 1.0D, 1.0D);
		dialplot.setDataset(0, dataset1);
		StandardDialFrame standarddialframe = new StandardDialFrame();
		standarddialframe.setBackgroundPaint(Color.lightGray);
		standarddialframe.setForegroundPaint(Color.darkGray);
		dialplot.setDialFrame(standarddialframe);
		GradientPaint gradientpaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(170, 170, 220));
		DialBackground dialbackground = new DialBackground(gradientpaint);
		dialbackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
		dialplot.setBackground(dialbackground);
		DialTextAnnotation dialtextannotation = new DialTextAnnotation(_bottomName);
		dialtextannotation.setFont(new Font("Dialog", 1, 14));
		dialtextannotation.setRadius(0.69999999999999996D);
		dialplot.addLayer(dialtextannotation);
		DialValueIndicator dialvalueindicator = new DialValueIndicator(0);
		dialvalueindicator.setFont(new Font("Dialog", 0, 10));
		dialvalueindicator.setOutlinePaint(Color.darkGray);
		dialvalueindicator.setRadius(0.59999999999999998D);
		dialvalueindicator.setAngle(-103D);
		dialplot.addLayer(dialvalueindicator);
		DialValueIndicator dialvalueindicator1 = new DialValueIndicator(1);
		dialvalueindicator1.setFont(new Font("Dialog", 0, 10));
		dialvalueindicator1.setOutlinePaint(Color.red);
		dialvalueindicator1.setRadius(0.59999999999999998D);
		dialvalueindicator1.setAngle(-77D);
		dialplot.addLayer(dialvalueindicator1);
		StandardDialScale standarddialscale = new StandardDialScale(_minValue, maxValue, -120D, -300D, showNumValue, 4);
		standarddialscale.setTickRadius(0.88D);
		standarddialscale.setTickLabelOffset(0.14999999999999999D);
		standarddialscale.setTickLabelFont(new Font("Dialog", 0, 14));
		dialplot.addScale(0, standarddialscale);
		dialplot.mapDatasetToScale(1, 1);

		StandardDialRange standarddialrange = new StandardDialRange(_highValue, maxValue, Color.red);
		standarddialrange.setInnerRadius(0.58999999999999997D);
		standarddialrange.setOuterRadius(0.58999999999999997D);
		dialplot.addLayer(standarddialrange);

		StandardDialRange standarddialrange1 = new StandardDialRange(_lowValue, _highValue, Color.orange);
		standarddialrange1.setInnerRadius(0.58999999999999997D);
		standarddialrange1.setOuterRadius(0.58999999999999997D);
		dialplot.addLayer(standarddialrange1);

		StandardDialRange standarddialrange2 = new StandardDialRange(_minValue, _lowValue, Color.green);
		standarddialrange2.setInnerRadius(0.58999999999999997D);
		standarddialrange2.setOuterRadius(0.58999999999999997D);
		dialplot.addLayer(standarddialrange2);

		org.jfreechart.chart.plot.dial.DialPointer.Pointer pointer = new org.jfreechart.chart.plot.dial.DialPointer.Pointer(0);
		dialplot.addPointer(pointer);
		DialCap dialcap = new DialCap();
		dialcap.setRadius(0.10000000000000001D);
		dialplot.setCap(dialcap);
		JFreeChart jfreechart = new JFreeChart(dialplot);
		jfreechart.setTitle(_topName);
		ChartPanel chartpanel = new ChartPanel(jfreechart);
		chartpanel.setPreferredSize(new Dimension(400, 400));
		addChart(jfreechart);
		JPanel jpanel = new JPanel(new GridLayout(2, 2));
		jpanel.add(new JLabel("指针:"));
		add(chartpanel);
	}

	/**
	 * 双指针仪表盘
	 * 
	 * @param _outerValue
	 *            外表盘指针值
	 * @param _innerValue
	 *            内表盘指针值
	 * @param _outerMaxValue
	 *            外表盘最大值
	 * @param _outerMinValue
	 *            外表盘最小值
	 * @param _innerMaxValue
	 *            内表盘最大值
	 * @param _innerMinValue
	 *            内表盘最小值
	 * @param _highValue
	 *            外表盘高危险度界限值
	 * @param _lowValue
	 *            外表盘低危险度界限值
	 * @param _showOuterNumValue
	 *            外表盘显示值刻度
	 * @param _showInnerNumValue
	 *            内表盘显示值刻度
	 * @param _topName
	 *            仪表盘顶部标题
	 * @param _lowValue
	 *            仪表盘内底部标题
	 * @param _type
	 *            仪表盘类型(为满足个性化需求)
	 */
	public CommonDialPlotPanel(double _outerValue, double _innerValue, double _outerMaxValue, double _outerMinValue, double _innerMaxValue, double _innerMinValue, double _highValue, double _lowValue, double _showOuterNumValue, double _showInnerNumValue, String _topName, String _bottomName, String _type) {

		super(new BorderLayout());
		double outerMaxValue = _outerMaxValue;
		double showOuterNumValue = _showOuterNumValue;
		double innerMaxValue = _innerMaxValue;
		double showInnerNumValue = _showInnerNumValue;
		if (_outerValue > outerMaxValue) {
			outerMaxValue = getDefaultMaxNum(_outerValue, _outerMinValue);
			showOuterNumValue = (outerMaxValue - _outerMinValue) / 6;
		}
		if (_innerValue > innerMaxValue) {
			innerMaxValue = getDefaultMaxNum(_innerValue, _innerMinValue);
			showInnerNumValue = (innerMaxValue - _innerMinValue) / 6;
		}
		if ("统一".equalsIgnoreCase(_type) && outerMaxValue != innerMaxValue) {
			if (outerMaxValue > innerMaxValue) {
				innerMaxValue = outerMaxValue;
			} else {
				outerMaxValue = innerMaxValue;
			}
		}
		dataset1 = new DefaultValueDataset(_outerValue);
		dataset2 = new DefaultValueDataset(_innerValue);
		DialPlot dialplot = new DialPlot();
		dialplot.setView(0.0D, 0.0D, 1.0D, 1.0D);
		dialplot.setDataset(0, dataset1);
		dialplot.setDataset(1, dataset2);
		StandardDialFrame standarddialframe = new StandardDialFrame();
		standarddialframe.setBackgroundPaint(Color.lightGray);
		standarddialframe.setForegroundPaint(Color.darkGray);
		dialplot.setDialFrame(standarddialframe);
		GradientPaint gradientpaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), new Color(170, 170, 220));
		DialBackground dialbackground = new DialBackground(gradientpaint);
		dialbackground.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.VERTICAL));
		dialplot.setBackground(dialbackground);
		DialTextAnnotation dialtextannotation = new DialTextAnnotation(_bottomName);
		dialtextannotation.setFont(new Font("Dialog", 1, 14));
		dialtextannotation.setRadius(0.69999999999999996D);
		dialplot.addLayer(dialtextannotation);
		DialValueIndicator dialvalueindicator = new DialValueIndicator(0);
		dialvalueindicator.setFont(new Font("Dialog", 0, 10));
		dialvalueindicator.setOutlinePaint(Color.darkGray);
		dialvalueindicator.setRadius(0.59999999999999998D);
		dialvalueindicator.setAngle(-103D);
		dialplot.addLayer(dialvalueindicator);
		DialValueIndicator dialvalueindicator1 = new DialValueIndicator(1);
		dialvalueindicator1.setFont(new Font("Dialog", 0, 10));
		dialvalueindicator1.setOutlinePaint(Color.red);
		dialvalueindicator1.setRadius(0.59999999999999998D);
		dialvalueindicator1.setAngle(-77D);
		dialplot.addLayer(dialvalueindicator1);
		StandardDialScale standarddialscale = new StandardDialScale(_outerMinValue, outerMaxValue, -120D, -300D, showOuterNumValue, 4);
		standarddialscale.setTickRadius(0.88D);
		standarddialscale.setTickLabelOffset(0.14999999999999999D);
		standarddialscale.setTickLabelFont(new Font("Dialog", 0, 14));
		dialplot.addScale(0, standarddialscale);
		StandardDialScale standarddialscale1 = new StandardDialScale(_innerMinValue, innerMaxValue, -120D, -300D, showInnerNumValue, 4);
		standarddialscale1.setTickRadius(0.5D);
		standarddialscale1.setTickLabelOffset(0.14999999999999999D);
		standarddialscale1.setTickLabelFont(new Font("Dialog", 0, 10));
		standarddialscale1.setMajorTickPaint(Color.red);
		standarddialscale1.setMinorTickPaint(Color.red);
		dialplot.addScale(1, standarddialscale1);
		dialplot.mapDatasetToScale(1, 1);

		StandardDialRange standarddialrange = new StandardDialRange(_highValue, outerMaxValue, Color.red);
		standarddialrange.setInnerRadius(0.58999999999999997D);
		standarddialrange.setOuterRadius(0.58999999999999997D);
		dialplot.addLayer(standarddialrange);

		StandardDialRange standarddialrange1 = new StandardDialRange(_lowValue, _highValue, Color.orange);
		standarddialrange1.setInnerRadius(0.58999999999999997D);
		standarddialrange1.setOuterRadius(0.58999999999999997D);
		dialplot.addLayer(standarddialrange1);

		StandardDialRange standarddialrange2 = new StandardDialRange(_outerMinValue, _lowValue, Color.green);
		standarddialrange2.setInnerRadius(0.58999999999999997D);
		standarddialrange2.setOuterRadius(0.58999999999999997D);
		dialplot.addLayer(standarddialrange2);

		org.jfreechart.chart.plot.dial.DialPointer.Pin pin = new org.jfreechart.chart.plot.dial.DialPointer.Pin(1);
		pin.setRadius(0.55000000000000004D);
		dialplot.addPointer(pin);
		org.jfreechart.chart.plot.dial.DialPointer.Pointer pointer = new org.jfreechart.chart.plot.dial.DialPointer.Pointer(0);
		dialplot.addPointer(pointer);
		DialCap dialcap = new DialCap();
		dialcap.setRadius(0.10000000000000001D);
		dialplot.setCap(dialcap);
		JFreeChart jfreechart = new JFreeChart(dialplot);
		jfreechart.setTitle(_topName);
		ChartPanel chartpanel = new ChartPanel(jfreechart);
		chartpanel.setPreferredSize(new Dimension(400, 400));
		addChart(jfreechart);
		JPanel jpanel = new JPanel(new GridLayout(2, 2));
		jpanel.add(new JLabel("外指针:"));
		jpanel.add(new JLabel("内指针:"));
		add(chartpanel);

	}

	/**
	 * 调整表盘最大刻度
	 * 
	 * @param _value
	 * @return
	 */
	private double getDefaultMaxNum(double _currValue, double _minValue) {
		return (double) 30 * ((int) _currValue / 30 + 1) + _minValue;
	}

	/**
	 * 风险控制率中预警值要小于正常值，但风险失控率中预警值要大于正常值，所以要分情况设置区域颜色，故增加此方法【李春娟/2012-03-15】
	 * @param _hvo
	 * @param _type  预警值是否大于正常值
	 * @return
	 * @throws Exception
	 */

	public static JPanel getDialPlotPanelByHashVO(HashVO _hvo, boolean _type) throws Exception {
		return getDialPlotPanelByHashVO(_hvo, _type, 400, 400, 400);
	}

	public static JPanel getDialPlotPanelByHashVO(HashVO _hvo, boolean _type, int _width, int _height, int _splitdivlocation) throws Exception {
		String str_title = _hvo.getStringValue("标题", "标题"); //
		String str_x = _hvo.getStringValue("X轴", "X轴"); //
		String li_realValue = _hvo.getStringValue("实际值"); //
		double li_value1 = _hvo.getDoubleValue("最小值", (double) 0); //
		double li_value2; //
		double li_value3;
		if (_type) {//如果预警值大于正常值
			li_value2 = _hvo.getDoubleValue("触发值", (double) 30); //正常值
			li_value3 = _hvo.getDoubleValue("法定值", (double) 60); //
		} else {
			li_value2 = _hvo.getDoubleValue("法定值", (double) 30); //
			li_value3 = _hvo.getDoubleValue("触发值", (double) 60); //正常值
		}
		double li_value4 = _hvo.getDoubleValue("最大值", (double) 100); //
		String str_color = _hvo.getStringValue("背景色", "FFA953"); //
		String str_tip = _hvo.getStringValue("提示", _hvo.getStringValue("提示", "")); //
		Color bgColor = new TBUtil().getColor(str_color); //
		Number value = null;
		if (!TBUtil.isEmpty(li_realValue)) {
			value = Float.parseFloat(li_realValue);
		}
		JPanel diaPlotPanel = getDialPlotPanel(str_title, str_x, value, li_value1, li_value2, li_value3, li_value4, bgColor, str_tip, _type); //
		diaPlotPanel.setPreferredSize(new Dimension(_width <= 0 ? 400 : _width, _height <= 0 ? 400 : _height));
		String title = (String) diaPlotPanel.getClientProperty("Title"); //
		String[] x = (String[]) diaPlotPanel.getClientProperty("SeriesX"); //
		String[] y = (String[]) diaPlotPanel.getClientProperty("SeriesY"); //
		String[][] ld_data = (String[][]) diaPlotPanel.getClientProperty("Data"); //
		return getPanel(diaPlotPanel, title, x, y, ld_data, _splitdivlocation <= 0 ? 400 : _splitdivlocation);
	}

	public static JPanel getDialPlotPanelByHashVO(HashVO _hvo) throws Exception {
		return getDialPlotPanelByHashVO(_hvo, true);//默认为预警值大于正常值
	}

	/**
	 * 加表格
	 * @param _newChartPanel
	 * @param _title
	 * @param _x
	 * @param _y
	 * @param _data
	 * @param _splitdivlocation
	 * @return
	 * @throws Exception
	 */
	private static JPanel getPanel(JPanel _newChartPanel, String _title, String[] _x, String[] _y, String[][] _data, int _splitdivlocation) throws Exception {
		String[][] str_data = new String[_data.length][_y.length]; //
		for (int i = 0; i < str_data.length; i++) {
			for (int j = 0; j < str_data[i].length; j++) {
				str_data[i][j] = "" + _data[i][j];
			}
		}
		String[] str_new_y = new String[_y.length]; //
		str_new_y[0] = ""; //
		System.arraycopy(_y, 0, str_new_y, 0, _y.length); // 拷贝!!!
		JTable table = new JTable(str_data, str_new_y); //
		DefaultTableCellRenderer render = new DefaultTableCellRenderer();
		render.setHorizontalAlignment(SwingConstants.CENTER);

		table.getColumn("实际值").setCellRenderer(render);
		table.getColumn("触发值").setCellRenderer(render);
		table.getColumn("法定值").setCellRenderer(render);
		table.setOpaque(false); //
		table.getTableHeader().setOpaque(false); //
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //
		table.setRowHeight(20);
		for (int i = 0; i < table.getColumnCount(); i++) {
			if (i == 0) {
				table.getColumnModel().getColumn(i).setPreferredWidth(65); //
			} else {
				table.getColumnModel().getColumn(i).setPreferredWidth(65); //
			}
		}
		table.getTableHeader().setOpaque(false); // 透明!!!
		JScrollPane scroll = new JScrollPane(table); //
		scroll.setPreferredSize(new Dimension(200, 50));
		JViewport jv2 = new JViewport();
		jv2.setOpaque(false); // 一定要设一下，否则上边总有个白条
		jv2.setView(table.getTableHeader());
		scroll.setColumnHeader(jv2); //
		JPanel jp = new JPanel(new FlowLayout());
		jp.add(_newChartPanel);
		JPanel tablepanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		tablepanel.add(scroll);
		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, jp, tablepanel); //
		split.setDividerSize(1);
		split.setDividerLocation(_splitdivlocation); //
		split.setOpaque(false); //
		JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		contentPanel.add(split, BorderLayout.CENTER); //
		return contentPanel; //
	}

	private static org.jfreechart.chart.ChartPanel getDialPlotPanel(String _topName, String _bottomName, Number _realValue, double _value1, double _value2, double _value3, double _value4, Color _bgColor, String _tip, boolean _type) {
		double maxValue = _value4;
		double showNumValue = (_value4 - _value1) / 5;
		org.jfreechart.data.general.DefaultValueDataset dataset1 = new org.jfreechart.data.general.DefaultValueDataset(_realValue);
		org.jfreechart.chart.plot.dial.DialPlot dialplot = new org.jfreechart.chart.plot.dial.DialPlot();

		dialplot.setView(0.0D, 0.0D, 1.0D, 1.0D);
		dialplot.setDataset(0, dataset1);
		org.jfreechart.chart.plot.dial.StandardDialFrame standarddialframe = new org.jfreechart.chart.plot.dial.StandardDialFrame();
		dialplot.setDialFrame(standarddialframe);
		GradientPaint gradientpaint = new GradientPaint(new Point(), new Color(255, 255, 255), new Point(), _bgColor); // 170,

		org.jfreechart.chart.plot.dial.DialBackground dialbackground = new org.jfreechart.chart.plot.dial.DialBackground(gradientpaint);
		dialbackground.setGradientPaintTransformer(new org.jfreechart.ui.StandardGradientPaintTransformer(org.jfreechart.ui.GradientPaintTransformType.VERTICAL));
		dialplot.setBackground(dialbackground);

		DialTextAnnotation dialtextannotation = new DialTextAnnotation(_bottomName);
		dialtextannotation.setFont(new Font("Dialog", 1, 14));
		dialtextannotation.setRadius(0.62999999999999996D);
		dialplot.addLayer(dialtextannotation);

		DialValueIndicator dialvalueindicator = new DialValueIndicator(0);
		dialvalueindicator.setFont(new Font("Dialog", 0, 10));
		dialvalueindicator.setOutlinePaint(Color.darkGray);
		dialvalueindicator.setRadius(0.49999999999999998D);
		dialvalueindicator.setAngle(-90D);
		dialplot.addLayer(dialvalueindicator);
		StandardDialScale standarddialscale = new StandardDialScale(_value1, maxValue, -120D, -300D, showNumValue, 4);
		standarddialscale.setTickRadius(0.88D);
		standarddialscale.setTickLabelOffset(0.14999999999999999D);
		standarddialscale.setTickLabelFont(new Font("Dialog", 0, 14));
		dialplot.addScale(0, standarddialscale);
		dialplot.mapDatasetToScale(1, 1);

		StandardDialRange standarddialrange = new StandardDialRange(_value3, maxValue, _type ? Color.red : Color.green);//判断预警值是否大于正常值【李春娟/2012-03-15】
		standarddialrange.setInnerRadius(0.58999999999999997D);
		standarddialrange.setOuterRadius(0.58999999999999997D);
		dialplot.addLayer(standarddialrange);

		StandardDialRange standarddialrange1 = new StandardDialRange(_value2, _value3, Color.orange);
		standarddialrange1.setInnerRadius(0.58999999999999997D);
		standarddialrange1.setOuterRadius(0.58999999999999997D);
		dialplot.addLayer(standarddialrange1);

		StandardDialRange standarddialrange2 = new StandardDialRange(_value1, _value2, _type ? Color.green : Color.red);//判断预警值是否大于正常值【李春娟/2012-03-15】
		standarddialrange2.setInnerRadius(0.58999999999999997D);
		standarddialrange2.setOuterRadius(0.58999999999999997D);
		dialplot.addLayer(standarddialrange2);

		org.jfreechart.chart.plot.dial.DialPointer.Pointer pointer = new org.jfreechart.chart.plot.dial.DialPointer.Pointer(0);
		dialplot.addPointer(pointer);
		DialCap dialcap = new DialCap();
		dialcap.setRadius(0.10000000000000001D);
		dialplot.setCap(dialcap);
		org.jfreechart.chart.JFreeChart jfreechart = new org.jfreechart.chart.JFreeChart(dialplot);
		jfreechart.setTitle(_topName);
		String[][] ld_value = new String[][] { { _realValue == null ? "空" : String.valueOf(_realValue), String.valueOf(_value2), String.valueOf(_value3) } };
		org.jfreechart.chart.ChartPanel charPanel = new org.jfreechart.chart.ChartPanel(jfreechart);
		charPanel.setToolTipText(_tip); //
		charPanel.putClientProperty("ChartType", "仪表盘图"); //
		charPanel.putClientProperty("Title", ""); //
		charPanel.putClientProperty("SeriesX", new String[] { "数值" }); //
		if (_type) {//如果预警值大于正常值
			charPanel.putClientProperty("SeriesY", new String[] { "实际值", "触发值", "法定值" }); //
		} else {
			charPanel.putClientProperty("SeriesY", new String[] { "实际值", "法定值", "触发值" }); //
		}
		charPanel.putClientProperty("Data", ld_value); //
		return charPanel; //
	}
}
