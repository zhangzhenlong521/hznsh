package cn.com.infostrategy.ui.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jfreechart.chart.ChartPanel;
import org.jfreechart.chart.JFreeChart;
import org.jfreechart.chart.title.LegendTitle;
import org.jfreechart.data.category.DefaultCategoryDataset;
import org.jfreechart.ui.RectangleEdge;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.report.BillChartItemVO;
import cn.com.infostrategy.to.report.BillChartVO;
import cn.com.infostrategy.to.report.ReportUtil;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTSplitPane;

/**
 * 雷达图（蜘蛛图）
 * 
 * @author hm
 * 
 */
public class BillRadarPanel extends JPanel {
	private String title = "";
	private boolean webFilled = true; // 是否填充雷达图统计区域
	private HashVO[] hvs_data; // 数据
	private String rowName; // 
	private String colName; //
	private String computeGroupFields; // 统计列

	private boolean isShowTotalColumn = true;
	private boolean isDemo; // 是否是模型数据
	private boolean haveTable; // 是否用JTable展示数据
	private JPanel spiderPanel;
	private ChartPanel chartPanel;
	private JFreeChart jfreechart;
	private JTable jTable_1;
	private Font titleFont = new Font("宋体", Font.BOLD, 18);

	/**
	 * demo 直接能创建一个Demo
	 * 
	 * @param _title
	 */
	public BillRadarPanel(String _title) {
		this(_title, true, true);
	}

	/**
	 * @param _title
	 *            标题
	 * @param _hvs_data
	 *            数据
	 * @param rowNames
	 *            传入的行名称作为 每个区域分类。
	 * @param colNames
	 *            传入的列名称作为 雷达图条线
	 * @param _computeGroupFields
	 * @param _haveTable
	 *            是否有table面板
	 */
	public BillRadarPanel(String _title, HashVO[] _hvs_data, String[] rowNames, String[] colNames, String _computeGroupFields, boolean _haveTable) {
		this(_title, _hvs_data, rowNames, colNames, _computeGroupFields, _haveTable, true, false,true);
	}
	public BillRadarPanel(String _title, HashVO[] _hvs_data, String[] rowNames, String[] colNames, String _computeGroupFields, boolean _haveTable,boolean _isShowTotalColumn) {
		this(_title, _hvs_data, rowNames, colNames, _computeGroupFields, _haveTable, true, false,_isShowTotalColumn);
	}

	/**
	 * @param _title
	 *            标题
	 * @param _hvs_data
	 *            数据
	 * @param rowNames
	 *            传入的行名称作为 每个区域分类。
	 * @param colNames
	 *            传入的列名称作为 雷达图条线
	 * @param _computeGroupFields
	 * @param _haveTable
	 *            是否有table面板
	 * @param _isDemo
	 *            是否是模型
	 */
	public BillRadarPanel(String _title, HashVO[] _hvs_data, String[] rowNames, String[] colNames, String _computeGroupFields, boolean _haveTable, boolean _webFilled, boolean _isDemo) {
		this(_title, _hvs_data, rowNames, colNames, _computeGroupFields, _haveTable, true, false,true);   // 有总合计的table
	}

	public BillRadarPanel(String _title, HashVO[] _hvs_data, String[] rowNames, String[] colNames, String _computeGroupFields, boolean _haveTable, boolean _webFilled, boolean _isDemo,boolean  _isShowTotalColumn){
			this.title = _title;
			this.hvs_data = _hvs_data;
			rowName = rowNames[0];
			colName = colNames[0];
			computeGroupFields = _computeGroupFields;
			haveTable = _haveTable;
			webFilled = _webFilled;
			isDemo = _isDemo;
			setShowTotalColumn(_isShowTotalColumn);
			initialize();
	}
	/**
	 * 
	 * @param _title
	 *            题目
	 * @param _webFilled
	 *            是否填充区域
	 */
	public BillRadarPanel(String _title, boolean _webFilled, boolean _isDemo) {
		this("雷达图模型", null, new String[] { "" }, new String[] { "" }, "", false, true, true);
	}

	// 创建 JfreeChart
	public void createSpiderChart() {
		if (isDemo) {
			createSpiderChartDemo();
			return;
		}
		if (hvs_data.length == 0) {
			jfreechart = null;
			return;
		}
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < hvs_data.length; i++) {
			String num = hvs_data[i].getStringValue(computeGroupFields);
			dataset.addValue(Double.parseDouble(num == null ? "0" : num), hvs_data[i].getStringValue(rowName, ""), hvs_data[i].getStringValue(colName, ""));
		}
		MySpiderWebPlot spiderwebplot = new MySpiderWebPlot(dataset); // 用自定义的雷达图绘制器
		spiderwebplot.setLabelFont(new Font("宋体", Font.BOLD, 14));
		spiderwebplot.setWebFilled(webFilled); // 是否填充区域
		spiderwebplot.setAxisLinePaint(Color.BLACK); // 设置底线条颜色
		JFreeChart jfreechart = new JFreeChart(title, titleFont, spiderwebplot, false);
		spiderwebplot.setBackgroundImageAlignment(MySpiderWebPlot.MINIMUM_HEIGHT_TO_DRAW);
		LegendTitle legendtitle = new LegendTitle(spiderwebplot);
		legendtitle.setPosition(RectangleEdge.BOTTOM);
		jfreechart.addSubtitle(legendtitle);
		this.jfreechart = jfreechart;
	}

	/**
	 * 通过数据直接创建（目前用于包含table统计表格切换选取数据用）
	 * 
	 * @param str_xLabels
	 * @param str_yLabels
	 * @param ld_datas
	 */
	public void createSpiderChart(String[] str_xLabels, String[] str_yLabels, double[][] ld_datas) {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for (int i = 0; i < ld_datas.length; i++) {
			for (int j = 0; j < str_yLabels.length; j++) {
				dataset.addValue(ld_datas[i][j], str_xLabels[i], str_yLabels[j]);
			}
		}
		MySpiderWebPlot spiderwebplot = new MySpiderWebPlot(dataset); // 用自定义的雷达图绘制器
		spiderwebplot.setLabelFont(new Font("宋体", Font.BOLD, 14));
		spiderwebplot.setWebFilled(webFilled); // 是否填充区域
		spiderwebplot.setAxisLinePaint(Color.BLACK); // 设置底线条颜色
		JFreeChart jfreechart = new JFreeChart(title, titleFont, spiderwebplot, false);
		spiderwebplot.setBackgroundImageAlignment(MySpiderWebPlot.MINIMUM_HEIGHT_TO_DRAW);
		LegendTitle legendtitle = new LegendTitle(spiderwebplot);
		legendtitle.setPosition(RectangleEdge.BOTTOM);
		jfreechart.addSubtitle(legendtitle);
		this.jfreechart = jfreechart;
	}

	// 创建一个ChartPanel面板
	public ChartPanel createSpiderPanel() {
		createSpiderChart();
		if (jfreechart == null) {
			return null;
		}
		ChartPanel chartpanel = new ChartPanel(jfreechart);
		return chartpanel;
	}

	public void initialize() {
		this.setLayout(new BorderLayout());
		spiderPanel = new JPanel(new BorderLayout());
		if (!isDemo && hvs_data.length == 0) {
			this.add(new JPanel().add(new JLabel("没有查到一条数据!!!")));
			return;
		}
		if (haveTable) {
			BillChartVO chartVO = new ReportUtil().convertHashVOToChartVO(hvs_data); //
			jTable_1 = getDataTable(1, rowName, colName, computeGroupFields, chartVO.getXSerial(), chartVO.getYSerial(), chartVO.getDataVO());
			JScrollPane scrollPane = new JScrollPane(jTable_1);
			WLTSplitPane pane = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, spiderPanel, scrollPane);
			pane.setDividerLocation(350);
			spliderFromTable();
			spiderPanel.add(chartPanel);
			this.add(pane);
		} else {
			chartPanel = createSpiderPanel();
			this.add(chartPanel);
		}
	}

	TBUtil tbUtil = new TBUtil();

	private JTable getDataTable(final int _type, String _xType, String _yType, String _zLabel, String[] _prodtypes, String[] _months, BillChartItemVO[][] _data) {
		String[] str_realColumns = new String[_months.length + 2];
		str_realColumns[0] = (_xType + "├ " + _yType + "<" + _zLabel + ">"); //
		for (int i = 0; i < _months.length; i++) {
			str_realColumns[i + 1] = _months[i]; //
		}
		str_realColumns[str_realColumns.length - 1] = "列合计"; //

		Object[][] realCellData = new Object[_prodtypes.length + 1][_months.length + 2]; // 表格中实际存储的值...
		for (int i = 0; i < _prodtypes.length; i++) {
			realCellData[i][0] = _prodtypes[i];
		}
		realCellData[_prodtypes.length][0] = "行合计"; //

		// 实际数据
		boolean isRateValueType = false; // 数据类型是否是比例类型???
		boolean isPercent = false; // 是否是百分比
		for (int i = 0; i < _data.length; i++) {
			for (int j = 0; j < _data[i].length; j++) {
				if (_data[i][j] == null) {
					realCellData[i][j + 1] = new BillChartItemVO(0); // 如果为空,则当0处理,是否需要当0处理还需有待斟酌!!!!!?????
				} else {
					realCellData[i][j + 1] = _data[i][j];
					if (_data[i][j].getValueType() == BillChartItemVO.RATE) { // 只要有一个是比例类型,则整个数据类型就是比例的,因为有可能有些项因为取不到值而作为Total类型
						isRateValueType = true; //
					}
					if (_data[i][j].isPercent()) {
						isPercent = true;
					}
				}
			}
		}

		double ld_all_sum = 0;
		double ld_all_count = 0;
		double ld_all_value = 0; //
		// 行合计
		for (int i = 0; i < _months.length; i++) {
			double ld_item_sum = 0; //
			double ld_item_count = 0; //
			double ld_item_value = 0; //
			for (int j = 0; j < _prodtypes.length; j++) { //
				if (realCellData[j][i + 1] != null) { //
					BillChartItemVO tempItemVO = (BillChartItemVO) realCellData[j][i + 1]; //
					double ld_temp_sum = tempItemVO.getSumValue();
					double ld_temp_count = tempItemVO.getCountValue();
					double ld_temp_value = tempItemVO.getValue(); //
					if (isRateValueType) { // 如果是比例类型
						if (tempItemVO.getValueType() == 2 && tempItemVO.getCountValue() != 0) { // 只有类型是比例类型的且count值不为0的!!才累加,因为有的因为找不到值而类型是Total
							ld_item_sum = ld_item_sum + ld_temp_sum; // 总和,除数
							ld_item_count = ld_item_count + ld_temp_count; // 被除数
							ld_all_sum = ld_all_sum + ld_temp_sum; //
							ld_all_count = ld_all_count + ld_temp_count; //
						}
					} else {
						ld_item_value = ld_item_value + ld_temp_value; //
						// ld_all_value = ld_all_value + ld_temp_value; //总计累加
					}
				}
			}
			if (isRateValueType) {
				realCellData[_prodtypes.length][i + 1] = new BillChartItemVO(ld_item_sum, ld_item_count, isPercent); //	
			} else {
				realCellData[_prodtypes.length][i + 1] = new BillChartItemVO(ld_item_value, isPercent); //	
			}
		}

		// 列合计
		for (int i = 0; i < _prodtypes.length; i++) {
			double ld_item_sum = 0; //
			double ld_item_count = 0; //
			double ld_item_value = 0; //
			for (int j = 0; j < _months.length; j++) { // 各列
				if (realCellData[i][j + 1] != null) { //
					BillChartItemVO tempItemVO = (BillChartItemVO) realCellData[i][j + 1]; //
					double ld_temp_sum = tempItemVO.getSumValue();
					double ld_temp_count = tempItemVO.getCountValue();
					double ld_temp_value = tempItemVO.getValue(); //
					if (isRateValueType) { // 如果是比例类型
						if (tempItemVO.getValueType() == 2 && tempItemVO.getCountValue() != 0) { // 只有类型是比例类型的且count值不为0的!!才累加,因为有的因为找不到值而类型是Total
							ld_item_sum = ld_item_sum + ld_temp_sum; // 总和,除数
							ld_item_count = ld_item_count + ld_temp_count; // 被除数
							ld_all_sum = ld_all_sum + ld_temp_sum; //
							ld_all_count = ld_all_count + ld_temp_count; //
						}
					} else {
						ld_item_value = ld_item_value + ld_temp_value; //
						ld_all_value = ld_all_value + ld_temp_value; // 总计累加
					}
				}
			}
			if (isRateValueType) { // 如果是比例类型
				realCellData[i][_months.length + 1] = new BillChartItemVO(ld_item_sum, ld_item_count, isPercent); //	
			} else {
				realCellData[i][_months.length + 1] = new BillChartItemVO(ld_item_value, isPercent); //	
			}
		}

		// 最右下角的总计
		if (isRateValueType) {
			realCellData[_prodtypes.length][_months.length + 1] = new BillChartItemVO(ld_all_sum, ld_all_count, isPercent); //	
		} else {
			realCellData[_prodtypes.length][_months.length + 1] = new BillChartItemVO(ld_all_value, isPercent); //	
		}

		DefaultTableModel model = new DefaultTableModel(realCellData, str_realColumns) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JTable table_temp = new JTable(model); //
		table_temp = new JTable(model); // BillListPanel
		if (!isShowTotalColumn()){
			table_temp.getColumnModel().removeColumn(table_temp.getColumn("列合计"));
			((DefaultTableModel)table_temp.getModel()).removeRow(table_temp.getRowCount()-1);
		}
		table_temp.getTableHeader().setReorderingAllowed(false); // 列之间不能人工调序
		table_temp.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //
		table_temp.setRowSelectionAllowed(true); //
		table_temp.setColumnSelectionAllowed(false); //
		table_temp.setRowHeight(22); //

		MyCellRender cellRender = new MyCellRender(); //
		int[] li_colWidths = new int[table_temp.getColumnCount()]; //
		int li_allwidths = 0; //
		for (int i = 0; i < table_temp.getColumnCount(); i++) {
			table_temp.getColumnModel().getColumn(i).setCellRenderer(cellRender); //
			li_colWidths[i] = tbUtil.getStrWidth(table_temp.getTableHeader().getFont(), table_temp.getColumnName(i)) + 15; //
			li_colWidths[i] = (li_colWidths[i] > 200 ? 200 : li_colWidths[i]); //
			li_allwidths = li_allwidths + li_colWidths[i];
		}
		if (li_allwidths < 666) { // 为了美观,如果小于700，则计算增加量，保证出来的表格总是能自动品配界面，而不是堆在一个角落!!
			int li_addValue = (666 - li_allwidths) / li_colWidths.length; //
			for (int i = 0; i < li_colWidths.length; i++) {
				li_colWidths[i] = li_colWidths[i] + li_addValue;
			}
		}
		for (int i = 0; i < li_colWidths.length; i++) {
			table_temp.getColumnModel().getColumn(i).setPreferredWidth(li_colWidths[i]); //
		}

		table_temp.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					onDataTableSelectChanged(_type, "行变化"); //
				}
			}
		});

		// table_temp.getColumnModel().getSelectionModel().addListSelectionListener(new
		// ListSelectionListener() {
		// public void valueChanged(ListSelectionEvent e) {
		// if (!e.getValueIsAdjusting()) {
		// onDataTableSelectChanged(_type, "列变化"); //
		// }
		// }
		// });

		table_temp.putClientProperty("X_Type", _xType); // 将X/Y维度直接存在表格的属性中,然后进行X/Y转换时能获得!!
		table_temp.putClientProperty("Y_Type", _yType); //
		table_temp.putClientProperty("Z_Type", _zLabel); //
		return table_temp; //
	}

	private void onDataTableSelectChanged(int _type, String _reason) {
		spliderFromTable(); // 雷达图的表格选择变化.
	}

	private String[] getAllRow1Names(JTable _table) {
		int li_rowCount = _table.getRowCount(); //
		String[] str_labels = new String[li_rowCount - 1]; //
		for (int i = 0; i < str_labels.length; i++) {
			str_labels[i] = "" + _table.getValueAt(i, 0); //
		}
		return str_labels;
	}

	private String[] getAllHeadNames(JTable _table) {
		int li_colCount = _table.getColumnCount(); //
		String[] str_labels = new String[li_colCount - 2]; //

		for (int i = 0; i < str_labels.length; i++) {
			str_labels[i] = "" + _table.getColumnModel().getColumn(i + 1).getHeaderValue(); //
		}

		return str_labels;
	}

	public boolean isShowTotalColumn() {
		return isShowTotalColumn;
	}

	public void setShowTotalColumn(boolean newvalue) {
		this.isShowTotalColumn = newvalue;
	}

	/**
	 * 从表格1绘制柱形图...
	 */

	private void spliderFromTable() {
		int li_selectedRowCount = jTable_1.getSelectedRowCount(); //
		int li_selectedColCount = jTable_1.getSelectedColumnCount(); //

		if (li_selectedRowCount == 0 || li_selectedColCount == 0) { // 如果一个没选,比如初始化,或清空选择时的默认绘制所有.
			String[] str_xLabels = getAllRow1Names(jTable_1);
			String[] str_yLabels = getAllHeadNames(jTable_1); // 
			double[][] ld_data = new double[str_xLabels.length][str_yLabels.length]; //
			for (int i = 0; i < ld_data.length; i++) {
				for (int j = 0; j < ld_data[i].length; j++) {
					BillChartItemVO chartItemVO = (BillChartItemVO) jTable_1.getValueAt(i, j + 1); //
					ld_data[i][j] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue());
				}
			}
			createSpiderChart(str_xLabels, str_yLabels, ld_data);
			spiderPanel.removeAll();
			chartPanel = new ChartPanel(jfreechart);
			spiderPanel.add(chartPanel);
			spiderPanel.updateUI();
		} else {
			String[] str_xLabels = null; //
			String[] str_yLabels = null;
			double[][] ld_datas = null;
			if (jTable_1.getRowSelectionAllowed() && !jTable_1.getColumnSelectionAllowed()) { // 行选择
				str_xLabels = getXLabelsFromTable(jTable_1); //
				str_yLabels = getYLabelsFromTable(jTable_1); //
				ld_datas = getDoubleDataFromTable(jTable_1); //
			} else if (!jTable_1.getRowSelectionAllowed() && jTable_1.getColumnSelectionAllowed()) { // 列选择
				str_xLabels = getYLabelsFromTable(jTable_1); //
				str_yLabels = getXLabelsFromTable(jTable_1); //
				ld_datas = convertData(getDoubleDataFromTable(jTable_1)); //
			} else if (jTable_1.getRowSelectionAllowed() && jTable_1.getColumnSelectionAllowed()) { // 行列选择
				str_xLabels = getXLabelsFromTable(jTable_1); //
				str_yLabels = getYLabelsFromTable(jTable_1); //
				ld_datas = getDoubleDataFromTable(jTable_1); //
			}
			createSpiderChart(str_xLabels, str_yLabels, ld_datas);
			spiderPanel.removeAll();
			chartPanel = new ChartPanel(jfreechart);
			spiderPanel.add(chartPanel);
			spiderPanel.updateUI();
		}
	}

	private static double[][] convertData(double[][] _olddata) {
		double[][] returndata = new double[_olddata[0].length][_olddata.length];
		for (int i = 0; i < _olddata.length; i++) {
			for (int j = 0; j < _olddata[i].length; j++) {
				returndata[j][i] = _olddata[i][j];
			}
		}
		return returndata;
	}

	// 从表格中取得图表的实际二维数据..
	private double[][] getDoubleDataFromTable(JTable _table) {
		if (_table.getRowSelectionAllowed() && !_table.getColumnSelectionAllowed()) { // 只选择行
			int[] li_rows = _table.getSelectedRows(); // 所有行数
			int li_cols = _table.getColumnCount() - 2; //
			if (!isShowTotalColumn()) {
				li_cols = li_cols + 1;
			}
			double[][] ld_data = new double[li_rows.length][li_cols]; //
			for (int i = 0; i < li_rows.length; i++) {
				for (int j = 0; j < li_cols; j++) {
					BillChartItemVO chartItemVO = (BillChartItemVO) _table.getValueAt(li_rows[i], j + 1); //
					ld_data[i][j] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue()); // //
				}
			}
			return ld_data;
		} else if (!_table.getRowSelectionAllowed() && _table.getColumnSelectionAllowed()) { // 只选择列
			int li_rows = _table.getRowCount() - 1; //
			int[] li_selCols = _table.getSelectedColumns(); //
			if (li_selCols.length > 0 && li_selCols[0] == 0) {
				int[] li_selCols_new = new int[li_selCols.length - 1]; //
				System.arraycopy(li_selCols, 1, li_selCols_new, 0, li_selCols_new.length);
				li_selCols = li_selCols_new; // 重新赋值
			}
			double[][] ld_data = new double[li_rows][li_selCols.length]; //
			for (int i = 0; i < li_rows; i++) {
				for (int j = 0; j < li_selCols.length; j++) {
					BillChartItemVO chartItemVO = (BillChartItemVO) _table.getValueAt(i, li_selCols[j]); // //
					ld_data[i][j] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue());
				}
			}
			return ld_data; //
		} else if (_table.getRowSelectionAllowed() && _table.getColumnSelectionAllowed()) { // 同时选择行与列
			int[] li_rows = _table.getSelectedRows(); // 所有行数
			int[] li_selCols = _table.getSelectedColumns(); //
			if (li_selCols.length > 0 && li_selCols[0] == 0) {
				int[] li_selCols_new = new int[li_selCols.length - 1]; //
				System.arraycopy(li_selCols, 1, li_selCols_new, 0, li_selCols_new.length);
				li_selCols = li_selCols_new; // 重新赋值
			}
			double[][] ld_data = new double[li_rows.length][li_selCols.length]; //
			for (int i = 0; i < li_rows.length; i++) {
				for (int j = 0; j < li_selCols.length; j++) {
					BillChartItemVO chartItemVO = (BillChartItemVO) _table.getValueAt(li_rows[i], li_selCols[j]); // //
					ld_data[i][j] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue()); //
				}
			}
			return ld_data; //
		} else {
			return null;
		}
	}

	// 取得图表第一行上的数据，就是图表的X坐标
	private String[] getXLabelsFromTable(JTable _table) {
		if (!_table.getRowSelectionAllowed() && _table.getColumnSelectionAllowed()) { // 如果是只能选择列,只必须要去掉最后一行
			int li_allRows = _table.getRowCount(); //
			String[] str_items = new String[li_allRows - 1]; //
			for (int i = 0; i < str_items.length; i++) {
				str_items[i] = "" + _table.getValueAt(i, 0); //
			}
			return str_items; //
		} else {
			int[] li_rows = _table.getSelectedRows(); //
			String[] str_items = new String[li_rows.length]; // //
			for (int i = 0; i < str_items.length; i++) {
				str_items[i] = "" + _table.getValueAt(li_rows[i], 0); //
			}
			return str_items;
		}
	}

	// 取得图表表头的标签,就是图表的Y坐标
	private String[] getYLabelsFromTable(JTable _table) {
		if (_table.getRowSelectionAllowed() && !_table.getColumnSelectionAllowed()) { // 如果是只能选择行,则要去掉第一列与最后一列
			int li_allCols = _table.getColumnCount(); //
			String[] str_items = new String[li_allCols - 2]; //
			if (!isShowTotalColumn()) {
				str_items = new String[li_allCols - 1]; //
			}
			for (int i = 0; i < str_items.length; i++) {
				str_items[i] = _table.getColumnName(i + 1);
			}
			return str_items; //
		} else {
			int[] li_cols = _table.getSelectedColumns(); // 选中的列
			ArrayList al_cols = new ArrayList(); //
			for (int i = 0; i < li_cols.length; i++) {
				if (li_cols[i] != 0) { // 第一列不算
					al_cols.add(_table.getColumnName(li_cols[i])); // 选中的列名
				}
			}
			return (String[]) al_cols.toArray(new String[0]); //
		}
	}

	public void createSpiderChartDemo() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		String group1 = "重大风险";
		dataset.addValue(65, group1, "个人业务");
		dataset.addValue(70, group1, "公司业务");
		dataset.addValue(34, group1, "授信业务");
		dataset.addValue(25, group1, "中间业务");
		dataset.addValue(55, group1, "国际业务");
		dataset.addValue(15, group1, "信用卡业务");
		dataset.addValue(56, group1, "理财业务");
		dataset.addValue(8, group1, "人力资源");
		dataset.addValue(120, group1, "计划财务");
		dataset.addValue(80, group1, "会计结算");
		dataset.addValue(78, group1, "后勤保安");
		dataset.addValue(85, group1, "事后监督");
		String group2 = "极小风险";
		dataset.addValue(55, group2, "个人业务");
		dataset.addValue(70, group2, "公司业务");
		dataset.addValue(44, group2, "授信业务");
		dataset.addValue(55, group2, "中间业务");
		dataset.addValue(55, group2, "国际业务");
		dataset.addValue(15, group2, "信用卡业务");
		dataset.addValue(56, group2, "理财业务");
		dataset.addValue(8, group2, "人力资源");
		dataset.addValue(100, group2, "计划财务");
		dataset.addValue(64, group2, "会计结算");
		dataset.addValue(81, group2, "后勤保安");
		dataset.addValue(55, group2, "事后监督");
		MySpiderWebPlot spiderwebplot = new MySpiderWebPlot(dataset);
		spiderwebplot.setWebFilled(webFilled);
		spiderwebplot.setAxisLinePaint(Color.BLACK); // 设置底线条颜色
		JFreeChart jfreechart = new JFreeChart(title, this.titleFont, spiderwebplot, false);
		spiderwebplot.setBackgroundImageAlignment(MySpiderWebPlot.MINIMUM_HEIGHT_TO_DRAW);
		LegendTitle legendtitle = new LegendTitle(spiderwebplot);
		legendtitle.setPosition(RectangleEdge.BOTTOM);
		jfreechart.addSubtitle(legendtitle);
		this.jfreechart = jfreechart;
	}

	class MyCellRender extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable _table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel) super.getTableCellRendererComponent(_table, value, isSelected, hasFocus, row, column);
			if (column == 0) { // 第一列字靠左
				label.setHorizontalAlignment(SwingConstants.LEFT);
			} else {
				label.setHorizontalAlignment(SwingConstants.RIGHT);
			}

			if (value != null && value instanceof BillChartItemVO) {
				if (((BillChartItemVO) value).getValueType() == BillChartItemVO.RATE) { // 只有是比例类型的才显示如何计算的
					label.setToolTipText(((BillChartItemVO) value).getInfoMsg()); //
				}
			}

			boolean bo_rowAllowSelected = _table.getRowSelectionAllowed(); //
			boolean bo_colAllowSelected = _table.getColumnSelectionAllowed(); //

			label.setOpaque(true); // //
			if (column == 0) { // 第一列
				label.setBackground(new Color(240, 240, 240)); // 背景颜色!!!
			} else {
				if (bo_rowAllowSelected && !bo_colAllowSelected) { // 只选中行
					if (column == _table.getColumnCount() - 1 && isShowTotalColumn) { // 如果是最后一列.
						label.setBackground(new Color(254, 254, 181)); // 背景颜色!!!
					} else {
						if (isSelected) {
							label.setBackground(_table.getSelectionBackground()); // //
						} else {
							if (row == _table.getRowCount() - 1) {
								label.setBackground(new Color(254, 254, 181)); // 背景颜色!!!
							} else {
								label.setBackground(Color.WHITE);
							}
						}
					}
				} else if (!bo_rowAllowSelected && bo_colAllowSelected) { // 只选中列
					if (row == _table.getRowCount() - 1) { // 如果是最后一行
						label.setBackground(new Color(254, 254, 181)); // 背景颜色!!!
					} else {
						if (isSelected) {
							label.setBackground(_table.getSelectionBackground()); // //
						} else {
							if (column == _table.getColumnCount() - 1 && isShowTotalColumn) {
								label.setBackground(new Color(254, 254, 181)); // 背景颜色!!!
							} else {
								label.setBackground(Color.WHITE);
							}
						}
					}
				} else if (bo_rowAllowSelected && bo_colAllowSelected) { // 行列都选中
					if (isSelected) {
						label.setBackground(_table.getSelectionBackground()); // //
					} else {
						if (row == _table.getRowCount() - 1 || (column == _table.getColumnCount() - 1 && isShowTotalColumn)) {
							label.setBackground(new Color(254, 254, 181)); // 背景颜色!!!
						} else {
							label.setBackground(Color.WHITE);
						}
					}
				}
			}
			return label; //
		}
	}

	public void refresh() {
		updateUI();
	}

	public void setTitleFont(Font titleFont) {
		this.titleFont = titleFont;
	}
	/**
	 * 导出excel  吴鹏【2012-5-24】
	 */
	/**
	 * 导出excel
	 * wp
	 */
	public void exportExcel(){
		JFileChooser chooser = new JFileChooser(); //
		chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(File file) {
				String filename = file.getName();
				return filename.endsWith(".xls");
			}

			public String getDescription() {
				return "*.xls";
			}
		});

		File f=null;
		try {
			f = new File(new File("C:\\报表数据.xls").getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		chooser.setSelectedFile(f);
		int li_rewult = chooser.showSaveDialog(this);
		if (li_rewult == JFileChooser.APPROVE_OPTION) {
			File curFile = chooser.getSelectedFile(); //
			if (curFile != null) {
				createExcelFile(curFile.getAbsolutePath()); //
				MessageBox.show(this, "保存Excel成功!"); //
			}
		}
	}
	
	//真正创建excel文件    吴鹏【2012-5-24】
	private void createExcelFile(String path) {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet=null;
		HSSFRow  row = null;
		HSSFCell cell = null;
		
		HSSFCellStyle setBorder=wb.createCellStyle();
		setBorder.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		setBorder.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		setBorder.setBorderTop(HSSFCellStyle.BORDER_THIN);
		setBorder.setBorderRight(HSSFCellStyle.BORDER_THIN);
		
		sheet=wb.createSheet("报表数据");
		HSSFPatriarch pat=sheet.createDrawingPatriarch();
		
		//插入表格图片
		byte[] image=this.getChartImageAsBtyeArray(chartPanel, true);
		
		HSSFClientAnchor anchor=new HSSFClientAnchor(0,0,255,255,(short)0,0,(short)15,15);//前四个参数尚不明确，第五、六个参数空值图片左上角在第几行第几列，最后俩参数空值图片高度为几行几列
		pat.createPicture(anchor,wb.addPicture(image, HSSFWorkbook.PICTURE_TYPE_JPEG));
		
		//插入表格数据
		String[][] datas=this.getChartTableDatas();
		for(int i = 0;i < datas.length;i++){
			row=sheet.createRow(i+20);
			for( int j = 0; j < datas[i].length;j++){
				cell=row.createCell(j);
				cell.setCellStyle(setBorder);//设置边框
				cell.setCellValue(datas[i][j]);
			}
		}
		
		//写入到excel
		OutputStream out = null;
		try {
			out = new FileOutputStream(path);
			wb.write(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
				try {
					if(out != null)
						out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	//以字节数组的形式得到Component的图片  isJpg,是否的到jpg格式的图片，否则得到bmp格式的图片     吴鹏【2012-5-24】
	private byte[] getChartImageAsBtyeArray(Component comp,boolean isJpg){
		BufferedImage image=null;
		String type=isJpg?"jpeg":"bmp";
		image=(BufferedImage) comp.createImage(comp.getWidth(), comp.getHeight());
		Graphics g=image.getGraphics();
		comp.paint(g);
		g.dispose();
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		try {
			ImageIO.write(image,type , out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}
	
	//得到图表面板下方的表格的数据,包括表头数据     吴鹏【2012-5-24】
	private String[][] getChartTableDatas(){
		int rowcount=jTable_1.getRowCount()+1;
		int columnCount=jTable_1.getColumnCount();
		String[][] datas=new String[rowcount][columnCount];
		
		for(int i=0;i<rowcount;i++){
			for(int j=0;j<columnCount;j++){
				if(i==0){
					datas[i][j]=jTable_1.getColumnName(j);
				}else{
					if(jTable_1.getValueAt(i-1, j)!=null)
						datas[i][j]=jTable_1.getValueAt(i-1, j).toString();
					else
						datas[i][j]="";
				}
			}
		}
		
		return datas;
	}
	
	/**
	 * 导出html  吴鹏【2012-5-24】
	 * wp
	 */
	public void exportHTML(){
		try {
			JFileChooser chooser = new JFileChooser();
			chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
				public boolean accept(File file) {
					String filename = file.getName();
					return filename.endsWith(".html");
				}

				public String getDescription() {
					return "*.html";
				}
			});

			try {
				File f = new File(new File("C:\\报表数据.html").getCanonicalPath());
				chooser.setSelectedFile(f);
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}

			int li_rewult = chooser.showSaveDialog(this);
			if (li_rewult == JFileChooser.APPROVE_OPTION) {
				File curFile = chooser.getSelectedFile(); //
				if (curFile != null) {
					PrintWriter writer = new PrintWriter(new FileOutputStream(curFile), false); //
					writer.println(this.getHtmlContentByString(curFile.getParent()).toString()); // 保存成Html
					writer.flush();
					writer.close();
					MessageBox.show(this, "保存Html文件成功!!"); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * 得到html的字符串内容  吴鹏【2012-5-24】
	 * @param filePath  是html所在的目录路径
	 * @return
	 */
	private StringBuffer getHtmlContentByString(String filePath){
		StringBuffer returnSb=new StringBuffer();
		returnSb.append("<html><head><title>报表数据</title>\r\n");
		returnSb.append("<style type=\"text/css\">\r\n");
		returnSb.append(".style_1 {\r\n");
		returnSb.append(" font-size: 12px; color: #333333; font-family: 宋体\r\n");
		returnSb.append("}\r\n");
		returnSb.append("</style>\r\n");
		returnSb.append("</head><body>\r\n");
		
		JTable table=jTable_1;
		JPanel pict_panel=this.chartPanel;
		
		String imageDirectory="导出html报表图片\\";
		String imageName=imageDirectory + System.currentTimeMillis()+".jpg";
		
		File imagePath=new File(filePath);
		if(imagePath.isDirectory()){
			File temp=new File(imagePath+"\\"+imageDirectory);
			if(!temp.exists()){//如果目录不存在，创建目录
				temp.mkdirs();
			}else{//如果是以文件的形式存在,删除它,然后创建目录
				if(temp.isFile()){
					temp.delete();
					temp.mkdirs();
				}
			}
			this.createHtmlPicture(imagePath+"\\"+imageName, pict_panel, true);//创建图片
		}
		returnSb.append("<img  src=\"").append(imageName).append("\" alt=\"").append("报表图片").append("\" />\r\n");
		returnSb.append("<table align=\"left\" style=\" margin-left: 0px; margin-right: 0px; margin-top: 3px; margin-bottom: 8px\" cellSpacing=\"1\" cellPadding=\"5\" bgColor=\"#999999\" border=\"0\" bordercolor=\"#CCCCCC\">\r\n");
		//得到table的数据，包括表头信息
		String[][] datas=this.getChartTableDatas();
		Color co = null;
		TBUtil tbutil = new TBUtil();
		for(int i=0;i<datas.length;i++){
			returnSb.append("<tr height=\"").append(table.getRowHeight()).append("\">\r\n");
			for(int j=0;j<datas[i].length;j++){
				if(i != 0){
					co = table.getCellRenderer(i-1, j).getTableCellRendererComponent(table, null, false, false, i-1, j).getBackground();
				}
				else{
					co = table.getCellRenderer(i, j).getTableCellRendererComponent(table, null, false, false, i, j).getBackground();
				}
				StringBuffer bgcolor = new StringBuffer();
				bgcolor.append("#");
				bgcolor.append(tbutil.getHexString(co.getRed()));
				bgcolor.append(tbutil.getHexString(co.getGreen()));
				bgcolor.append(tbutil.getHexString(co.getBlue()));
				
				returnSb.append("<td bgcolor=\"").append(bgcolor).append("\" class=\"style_1\" >").append(datas[i][j]).append("</td>\r\n");
			}
			returnSb.append("</tr>\r\n");
		}
		returnSb.append("</body>\r\n</html>");
		return returnSb;
	}
	
	/**
	 * 创建图片  吴鹏【2012-5-24】
	 * @param path  图片的绝对路径,全文件名
	 * @param comp
	 * @param isJpg
	 * @return
	 */
	public void createHtmlPicture(String path,Component comp,boolean isJpg){
		File file=new File(path);
		if(file.isDirectory()){
			MessageBox.show(this,"图片路径是目录，不能创建图片!");
			return;
		}
		file=null;
		String format=isJpg?"jpeg":"bmp";
		
		BufferedImage image=(BufferedImage) comp.createImage(comp.getWidth(), comp.getHeight());
		Graphics g=image.getGraphics();
		comp.paint(g);
		g.dispose();
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		try {
			ImageIO.write(image, format,out);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] image_bytes=out.toByteArray();
		
		ByteArrayInputStream bai=null;
		OutputStream os=null;
		try {
			bai = new ByteArrayInputStream(image_bytes);
			os = new FileOutputStream(path);
			byte[] buffer = new byte[1024*3];
			int i=0;
			while((i=bai.read(buffer))!=-1){
				os.write(buffer);
			}
			image_bytes = null;
			buffer = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try{
				if(bai!=null){
					bai.close();
				}
			}catch(Exception e1){
				e1.printStackTrace();
			}
			try{
				if(os!=null){
					os.close();
				}
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}
	}
	
	
}
