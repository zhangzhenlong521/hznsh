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
 * �״�ͼ��֩��ͼ��
 * 
 * @author hm
 * 
 */
public class BillRadarPanel extends JPanel {
	private String title = "";
	private boolean webFilled = true; // �Ƿ�����״�ͼͳ������
	private HashVO[] hvs_data; // ����
	private String rowName; // 
	private String colName; //
	private String computeGroupFields; // ͳ����

	private boolean isShowTotalColumn = true;
	private boolean isDemo; // �Ƿ���ģ������
	private boolean haveTable; // �Ƿ���JTableչʾ����
	private JPanel spiderPanel;
	private ChartPanel chartPanel;
	private JFreeChart jfreechart;
	private JTable jTable_1;
	private Font titleFont = new Font("����", Font.BOLD, 18);

	/**
	 * demo ֱ���ܴ���һ��Demo
	 * 
	 * @param _title
	 */
	public BillRadarPanel(String _title) {
		this(_title, true, true);
	}

	/**
	 * @param _title
	 *            ����
	 * @param _hvs_data
	 *            ����
	 * @param rowNames
	 *            �������������Ϊ ÿ��������ࡣ
	 * @param colNames
	 *            �������������Ϊ �״�ͼ����
	 * @param _computeGroupFields
	 * @param _haveTable
	 *            �Ƿ���table���
	 */
	public BillRadarPanel(String _title, HashVO[] _hvs_data, String[] rowNames, String[] colNames, String _computeGroupFields, boolean _haveTable) {
		this(_title, _hvs_data, rowNames, colNames, _computeGroupFields, _haveTable, true, false,true);
	}
	public BillRadarPanel(String _title, HashVO[] _hvs_data, String[] rowNames, String[] colNames, String _computeGroupFields, boolean _haveTable,boolean _isShowTotalColumn) {
		this(_title, _hvs_data, rowNames, colNames, _computeGroupFields, _haveTable, true, false,_isShowTotalColumn);
	}

	/**
	 * @param _title
	 *            ����
	 * @param _hvs_data
	 *            ����
	 * @param rowNames
	 *            �������������Ϊ ÿ��������ࡣ
	 * @param colNames
	 *            �������������Ϊ �״�ͼ����
	 * @param _computeGroupFields
	 * @param _haveTable
	 *            �Ƿ���table���
	 * @param _isDemo
	 *            �Ƿ���ģ��
	 */
	public BillRadarPanel(String _title, HashVO[] _hvs_data, String[] rowNames, String[] colNames, String _computeGroupFields, boolean _haveTable, boolean _webFilled, boolean _isDemo) {
		this(_title, _hvs_data, rowNames, colNames, _computeGroupFields, _haveTable, true, false,true);   // ���ܺϼƵ�table
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
	 *            ��Ŀ
	 * @param _webFilled
	 *            �Ƿ��������
	 */
	public BillRadarPanel(String _title, boolean _webFilled, boolean _isDemo) {
		this("�״�ͼģ��", null, new String[] { "" }, new String[] { "" }, "", false, true, true);
	}

	// ���� JfreeChart
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
		MySpiderWebPlot spiderwebplot = new MySpiderWebPlot(dataset); // ���Զ�����״�ͼ������
		spiderwebplot.setLabelFont(new Font("����", Font.BOLD, 14));
		spiderwebplot.setWebFilled(webFilled); // �Ƿ��������
		spiderwebplot.setAxisLinePaint(Color.BLACK); // ���õ�������ɫ
		JFreeChart jfreechart = new JFreeChart(title, titleFont, spiderwebplot, false);
		spiderwebplot.setBackgroundImageAlignment(MySpiderWebPlot.MINIMUM_HEIGHT_TO_DRAW);
		LegendTitle legendtitle = new LegendTitle(spiderwebplot);
		legendtitle.setPosition(RectangleEdge.BOTTOM);
		jfreechart.addSubtitle(legendtitle);
		this.jfreechart = jfreechart;
	}

	/**
	 * ͨ������ֱ�Ӵ�����Ŀǰ���ڰ���tableͳ�Ʊ���л�ѡȡ�����ã�
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
		MySpiderWebPlot spiderwebplot = new MySpiderWebPlot(dataset); // ���Զ�����״�ͼ������
		spiderwebplot.setLabelFont(new Font("����", Font.BOLD, 14));
		spiderwebplot.setWebFilled(webFilled); // �Ƿ��������
		spiderwebplot.setAxisLinePaint(Color.BLACK); // ���õ�������ɫ
		JFreeChart jfreechart = new JFreeChart(title, titleFont, spiderwebplot, false);
		spiderwebplot.setBackgroundImageAlignment(MySpiderWebPlot.MINIMUM_HEIGHT_TO_DRAW);
		LegendTitle legendtitle = new LegendTitle(spiderwebplot);
		legendtitle.setPosition(RectangleEdge.BOTTOM);
		jfreechart.addSubtitle(legendtitle);
		this.jfreechart = jfreechart;
	}

	// ����һ��ChartPanel���
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
			this.add(new JPanel().add(new JLabel("û�в鵽һ������!!!")));
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
		str_realColumns[0] = (_xType + "�� " + _yType + "<" + _zLabel + ">"); //
		for (int i = 0; i < _months.length; i++) {
			str_realColumns[i + 1] = _months[i]; //
		}
		str_realColumns[str_realColumns.length - 1] = "�кϼ�"; //

		Object[][] realCellData = new Object[_prodtypes.length + 1][_months.length + 2]; // �����ʵ�ʴ洢��ֵ...
		for (int i = 0; i < _prodtypes.length; i++) {
			realCellData[i][0] = _prodtypes[i];
		}
		realCellData[_prodtypes.length][0] = "�кϼ�"; //

		// ʵ������
		boolean isRateValueType = false; // ���������Ƿ��Ǳ�������???
		boolean isPercent = false; // �Ƿ��ǰٷֱ�
		for (int i = 0; i < _data.length; i++) {
			for (int j = 0; j < _data[i].length; j++) {
				if (_data[i][j] == null) {
					realCellData[i][j + 1] = new BillChartItemVO(0); // ���Ϊ��,��0����,�Ƿ���Ҫ��0�������д�����!!!!!?????
				} else {
					realCellData[i][j + 1] = _data[i][j];
					if (_data[i][j].getValueType() == BillChartItemVO.RATE) { // ֻҪ��һ���Ǳ�������,�������������;��Ǳ�����,��Ϊ�п�����Щ����Ϊȡ����ֵ����ΪTotal����
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
		// �кϼ�
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
					if (isRateValueType) { // ����Ǳ�������
						if (tempItemVO.getValueType() == 2 && tempItemVO.getCountValue() != 0) { // ֻ�������Ǳ������͵���countֵ��Ϊ0��!!���ۼ�,��Ϊ�е���Ϊ�Ҳ���ֵ��������Total
							ld_item_sum = ld_item_sum + ld_temp_sum; // �ܺ�,����
							ld_item_count = ld_item_count + ld_temp_count; // ������
							ld_all_sum = ld_all_sum + ld_temp_sum; //
							ld_all_count = ld_all_count + ld_temp_count; //
						}
					} else {
						ld_item_value = ld_item_value + ld_temp_value; //
						// ld_all_value = ld_all_value + ld_temp_value; //�ܼ��ۼ�
					}
				}
			}
			if (isRateValueType) {
				realCellData[_prodtypes.length][i + 1] = new BillChartItemVO(ld_item_sum, ld_item_count, isPercent); //	
			} else {
				realCellData[_prodtypes.length][i + 1] = new BillChartItemVO(ld_item_value, isPercent); //	
			}
		}

		// �кϼ�
		for (int i = 0; i < _prodtypes.length; i++) {
			double ld_item_sum = 0; //
			double ld_item_count = 0; //
			double ld_item_value = 0; //
			for (int j = 0; j < _months.length; j++) { // ����
				if (realCellData[i][j + 1] != null) { //
					BillChartItemVO tempItemVO = (BillChartItemVO) realCellData[i][j + 1]; //
					double ld_temp_sum = tempItemVO.getSumValue();
					double ld_temp_count = tempItemVO.getCountValue();
					double ld_temp_value = tempItemVO.getValue(); //
					if (isRateValueType) { // ����Ǳ�������
						if (tempItemVO.getValueType() == 2 && tempItemVO.getCountValue() != 0) { // ֻ�������Ǳ������͵���countֵ��Ϊ0��!!���ۼ�,��Ϊ�е���Ϊ�Ҳ���ֵ��������Total
							ld_item_sum = ld_item_sum + ld_temp_sum; // �ܺ�,����
							ld_item_count = ld_item_count + ld_temp_count; // ������
							ld_all_sum = ld_all_sum + ld_temp_sum; //
							ld_all_count = ld_all_count + ld_temp_count; //
						}
					} else {
						ld_item_value = ld_item_value + ld_temp_value; //
						ld_all_value = ld_all_value + ld_temp_value; // �ܼ��ۼ�
					}
				}
			}
			if (isRateValueType) { // ����Ǳ�������
				realCellData[i][_months.length + 1] = new BillChartItemVO(ld_item_sum, ld_item_count, isPercent); //	
			} else {
				realCellData[i][_months.length + 1] = new BillChartItemVO(ld_item_value, isPercent); //	
			}
		}

		// �����½ǵ��ܼ�
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
			table_temp.getColumnModel().removeColumn(table_temp.getColumn("�кϼ�"));
			((DefaultTableModel)table_temp.getModel()).removeRow(table_temp.getRowCount()-1);
		}
		table_temp.getTableHeader().setReorderingAllowed(false); // ��֮�䲻���˹�����
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
		if (li_allwidths < 666) { // Ϊ������,���С��700�����������������֤�����ı���������Զ�Ʒ����棬�����Ƕ���һ������!!
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
					onDataTableSelectChanged(_type, "�б仯"); //
				}
			}
		});

		// table_temp.getColumnModel().getSelectionModel().addListSelectionListener(new
		// ListSelectionListener() {
		// public void valueChanged(ListSelectionEvent e) {
		// if (!e.getValueIsAdjusting()) {
		// onDataTableSelectChanged(_type, "�б仯"); //
		// }
		// }
		// });

		table_temp.putClientProperty("X_Type", _xType); // ��X/Yά��ֱ�Ӵ��ڱ���������,Ȼ�����X/Yת��ʱ�ܻ��!!
		table_temp.putClientProperty("Y_Type", _yType); //
		table_temp.putClientProperty("Z_Type", _zLabel); //
		return table_temp; //
	}

	private void onDataTableSelectChanged(int _type, String _reason) {
		spliderFromTable(); // �״�ͼ�ı��ѡ��仯.
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
	 * �ӱ��1��������ͼ...
	 */

	private void spliderFromTable() {
		int li_selectedRowCount = jTable_1.getSelectedRowCount(); //
		int li_selectedColCount = jTable_1.getSelectedColumnCount(); //

		if (li_selectedRowCount == 0 || li_selectedColCount == 0) { // ���һ��ûѡ,�����ʼ��,�����ѡ��ʱ��Ĭ�ϻ�������.
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
			if (jTable_1.getRowSelectionAllowed() && !jTable_1.getColumnSelectionAllowed()) { // ��ѡ��
				str_xLabels = getXLabelsFromTable(jTable_1); //
				str_yLabels = getYLabelsFromTable(jTable_1); //
				ld_datas = getDoubleDataFromTable(jTable_1); //
			} else if (!jTable_1.getRowSelectionAllowed() && jTable_1.getColumnSelectionAllowed()) { // ��ѡ��
				str_xLabels = getYLabelsFromTable(jTable_1); //
				str_yLabels = getXLabelsFromTable(jTable_1); //
				ld_datas = convertData(getDoubleDataFromTable(jTable_1)); //
			} else if (jTable_1.getRowSelectionAllowed() && jTable_1.getColumnSelectionAllowed()) { // ����ѡ��
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

	// �ӱ����ȡ��ͼ���ʵ�ʶ�ά����..
	private double[][] getDoubleDataFromTable(JTable _table) {
		if (_table.getRowSelectionAllowed() && !_table.getColumnSelectionAllowed()) { // ֻѡ����
			int[] li_rows = _table.getSelectedRows(); // ��������
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
		} else if (!_table.getRowSelectionAllowed() && _table.getColumnSelectionAllowed()) { // ֻѡ����
			int li_rows = _table.getRowCount() - 1; //
			int[] li_selCols = _table.getSelectedColumns(); //
			if (li_selCols.length > 0 && li_selCols[0] == 0) {
				int[] li_selCols_new = new int[li_selCols.length - 1]; //
				System.arraycopy(li_selCols, 1, li_selCols_new, 0, li_selCols_new.length);
				li_selCols = li_selCols_new; // ���¸�ֵ
			}
			double[][] ld_data = new double[li_rows][li_selCols.length]; //
			for (int i = 0; i < li_rows; i++) {
				for (int j = 0; j < li_selCols.length; j++) {
					BillChartItemVO chartItemVO = (BillChartItemVO) _table.getValueAt(i, li_selCols[j]); // //
					ld_data[i][j] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue());
				}
			}
			return ld_data; //
		} else if (_table.getRowSelectionAllowed() && _table.getColumnSelectionAllowed()) { // ͬʱѡ��������
			int[] li_rows = _table.getSelectedRows(); // ��������
			int[] li_selCols = _table.getSelectedColumns(); //
			if (li_selCols.length > 0 && li_selCols[0] == 0) {
				int[] li_selCols_new = new int[li_selCols.length - 1]; //
				System.arraycopy(li_selCols, 1, li_selCols_new, 0, li_selCols_new.length);
				li_selCols = li_selCols_new; // ���¸�ֵ
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

	// ȡ��ͼ���һ���ϵ����ݣ�����ͼ���X����
	private String[] getXLabelsFromTable(JTable _table) {
		if (!_table.getRowSelectionAllowed() && _table.getColumnSelectionAllowed()) { // �����ֻ��ѡ����,ֻ����Ҫȥ�����һ��
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

	// ȡ��ͼ���ͷ�ı�ǩ,����ͼ���Y����
	private String[] getYLabelsFromTable(JTable _table) {
		if (_table.getRowSelectionAllowed() && !_table.getColumnSelectionAllowed()) { // �����ֻ��ѡ����,��Ҫȥ����һ�������һ��
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
			int[] li_cols = _table.getSelectedColumns(); // ѡ�е���
			ArrayList al_cols = new ArrayList(); //
			for (int i = 0; i < li_cols.length; i++) {
				if (li_cols[i] != 0) { // ��һ�в���
					al_cols.add(_table.getColumnName(li_cols[i])); // ѡ�е�����
				}
			}
			return (String[]) al_cols.toArray(new String[0]); //
		}
	}

	public void createSpiderChartDemo() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		String group1 = "�ش����";
		dataset.addValue(65, group1, "����ҵ��");
		dataset.addValue(70, group1, "��˾ҵ��");
		dataset.addValue(34, group1, "����ҵ��");
		dataset.addValue(25, group1, "�м�ҵ��");
		dataset.addValue(55, group1, "����ҵ��");
		dataset.addValue(15, group1, "���ÿ�ҵ��");
		dataset.addValue(56, group1, "���ҵ��");
		dataset.addValue(8, group1, "������Դ");
		dataset.addValue(120, group1, "�ƻ�����");
		dataset.addValue(80, group1, "��ƽ���");
		dataset.addValue(78, group1, "���ڱ���");
		dataset.addValue(85, group1, "�º�ල");
		String group2 = "��С����";
		dataset.addValue(55, group2, "����ҵ��");
		dataset.addValue(70, group2, "��˾ҵ��");
		dataset.addValue(44, group2, "����ҵ��");
		dataset.addValue(55, group2, "�м�ҵ��");
		dataset.addValue(55, group2, "����ҵ��");
		dataset.addValue(15, group2, "���ÿ�ҵ��");
		dataset.addValue(56, group2, "���ҵ��");
		dataset.addValue(8, group2, "������Դ");
		dataset.addValue(100, group2, "�ƻ�����");
		dataset.addValue(64, group2, "��ƽ���");
		dataset.addValue(81, group2, "���ڱ���");
		dataset.addValue(55, group2, "�º�ල");
		MySpiderWebPlot spiderwebplot = new MySpiderWebPlot(dataset);
		spiderwebplot.setWebFilled(webFilled);
		spiderwebplot.setAxisLinePaint(Color.BLACK); // ���õ�������ɫ
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
			if (column == 0) { // ��һ���ֿ���
				label.setHorizontalAlignment(SwingConstants.LEFT);
			} else {
				label.setHorizontalAlignment(SwingConstants.RIGHT);
			}

			if (value != null && value instanceof BillChartItemVO) {
				if (((BillChartItemVO) value).getValueType() == BillChartItemVO.RATE) { // ֻ���Ǳ������͵Ĳ���ʾ��μ����
					label.setToolTipText(((BillChartItemVO) value).getInfoMsg()); //
				}
			}

			boolean bo_rowAllowSelected = _table.getRowSelectionAllowed(); //
			boolean bo_colAllowSelected = _table.getColumnSelectionAllowed(); //

			label.setOpaque(true); // //
			if (column == 0) { // ��һ��
				label.setBackground(new Color(240, 240, 240)); // ������ɫ!!!
			} else {
				if (bo_rowAllowSelected && !bo_colAllowSelected) { // ֻѡ����
					if (column == _table.getColumnCount() - 1 && isShowTotalColumn) { // ��������һ��.
						label.setBackground(new Color(254, 254, 181)); // ������ɫ!!!
					} else {
						if (isSelected) {
							label.setBackground(_table.getSelectionBackground()); // //
						} else {
							if (row == _table.getRowCount() - 1) {
								label.setBackground(new Color(254, 254, 181)); // ������ɫ!!!
							} else {
								label.setBackground(Color.WHITE);
							}
						}
					}
				} else if (!bo_rowAllowSelected && bo_colAllowSelected) { // ֻѡ����
					if (row == _table.getRowCount() - 1) { // ��������һ��
						label.setBackground(new Color(254, 254, 181)); // ������ɫ!!!
					} else {
						if (isSelected) {
							label.setBackground(_table.getSelectionBackground()); // //
						} else {
							if (column == _table.getColumnCount() - 1 && isShowTotalColumn) {
								label.setBackground(new Color(254, 254, 181)); // ������ɫ!!!
							} else {
								label.setBackground(Color.WHITE);
							}
						}
					}
				} else if (bo_rowAllowSelected && bo_colAllowSelected) { // ���ж�ѡ��
					if (isSelected) {
						label.setBackground(_table.getSelectionBackground()); // //
					} else {
						if (row == _table.getRowCount() - 1 || (column == _table.getColumnCount() - 1 && isShowTotalColumn)) {
							label.setBackground(new Color(254, 254, 181)); // ������ɫ!!!
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
	 * ����excel  ������2012-5-24��
	 */
	/**
	 * ����excel
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
			f = new File(new File("C:\\��������.xls").getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		chooser.setSelectedFile(f);
		int li_rewult = chooser.showSaveDialog(this);
		if (li_rewult == JFileChooser.APPROVE_OPTION) {
			File curFile = chooser.getSelectedFile(); //
			if (curFile != null) {
				createExcelFile(curFile.getAbsolutePath()); //
				MessageBox.show(this, "����Excel�ɹ�!"); //
			}
		}
	}
	
	//��������excel�ļ�    ������2012-5-24��
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
		
		sheet=wb.createSheet("��������");
		HSSFPatriarch pat=sheet.createDrawingPatriarch();
		
		//������ͼƬ
		byte[] image=this.getChartImageAsBtyeArray(chartPanel, true);
		
		HSSFClientAnchor anchor=new HSSFClientAnchor(0,0,255,255,(short)0,0,(short)15,15);//ǰ�ĸ������в���ȷ�����塢����������ֵͼƬ���Ͻ��ڵڼ��еڼ��У������������ֵͼƬ�߶�Ϊ���м���
		pat.createPicture(anchor,wb.addPicture(image, HSSFWorkbook.PICTURE_TYPE_JPEG));
		
		//����������
		String[][] datas=this.getChartTableDatas();
		for(int i = 0;i < datas.length;i++){
			row=sheet.createRow(i+20);
			for( int j = 0; j < datas[i].length;j++){
				cell=row.createCell(j);
				cell.setCellStyle(setBorder);//���ñ߿�
				cell.setCellValue(datas[i][j]);
			}
		}
		
		//д�뵽excel
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
	
	//���ֽ��������ʽ�õ�Component��ͼƬ  isJpg,�Ƿ�ĵ�jpg��ʽ��ͼƬ������õ�bmp��ʽ��ͼƬ     ������2012-5-24��
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
	
	//�õ�ͼ������·��ı�������,������ͷ����     ������2012-5-24��
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
	 * ����html  ������2012-5-24��
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
				File f = new File(new File("C:\\��������.html").getCanonicalPath());
				chooser.setSelectedFile(f);
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}

			int li_rewult = chooser.showSaveDialog(this);
			if (li_rewult == JFileChooser.APPROVE_OPTION) {
				File curFile = chooser.getSelectedFile(); //
				if (curFile != null) {
					PrintWriter writer = new PrintWriter(new FileOutputStream(curFile), false); //
					writer.println(this.getHtmlContentByString(curFile.getParent()).toString()); // �����Html
					writer.flush();
					writer.close();
					MessageBox.show(this, "����Html�ļ��ɹ�!!"); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * �õ�html���ַ�������  ������2012-5-24��
	 * @param filePath  ��html���ڵ�Ŀ¼·��
	 * @return
	 */
	private StringBuffer getHtmlContentByString(String filePath){
		StringBuffer returnSb=new StringBuffer();
		returnSb.append("<html><head><title>��������</title>\r\n");
		returnSb.append("<style type=\"text/css\">\r\n");
		returnSb.append(".style_1 {\r\n");
		returnSb.append(" font-size: 12px; color: #333333; font-family: ����\r\n");
		returnSb.append("}\r\n");
		returnSb.append("</style>\r\n");
		returnSb.append("</head><body>\r\n");
		
		JTable table=jTable_1;
		JPanel pict_panel=this.chartPanel;
		
		String imageDirectory="����html����ͼƬ\\";
		String imageName=imageDirectory + System.currentTimeMillis()+".jpg";
		
		File imagePath=new File(filePath);
		if(imagePath.isDirectory()){
			File temp=new File(imagePath+"\\"+imageDirectory);
			if(!temp.exists()){//���Ŀ¼�����ڣ�����Ŀ¼
				temp.mkdirs();
			}else{//��������ļ�����ʽ����,ɾ����,Ȼ�󴴽�Ŀ¼
				if(temp.isFile()){
					temp.delete();
					temp.mkdirs();
				}
			}
			this.createHtmlPicture(imagePath+"\\"+imageName, pict_panel, true);//����ͼƬ
		}
		returnSb.append("<img  src=\"").append(imageName).append("\" alt=\"").append("����ͼƬ").append("\" />\r\n");
		returnSb.append("<table align=\"left\" style=\" margin-left: 0px; margin-right: 0px; margin-top: 3px; margin-bottom: 8px\" cellSpacing=\"1\" cellPadding=\"5\" bgColor=\"#999999\" border=\"0\" bordercolor=\"#CCCCCC\">\r\n");
		//�õ�table�����ݣ�������ͷ��Ϣ
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
	 * ����ͼƬ  ������2012-5-24��
	 * @param path  ͼƬ�ľ���·��,ȫ�ļ���
	 * @param comp
	 * @param isJpg
	 * @return
	 */
	public void createHtmlPicture(String path,Component comp,boolean isJpg){
		File file=new File(path);
		if(file.isDirectory()){
			MessageBox.show(this,"ͼƬ·����Ŀ¼�����ܴ���ͼƬ!");
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
