package cn.com.infostrategy.ui.report.chart;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.ui.TextAnchor;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.report.BillChartItemVO;
import cn.com.infostrategy.to.report.BillChartVO;
import cn.com.infostrategy.to.report.WordFileUtil;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPopupButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.NumberFormatdocument;
import cn.com.infostrategy.ui.report.ReportUIUtil;

/**
 * ͼ�����..
 * 
 * @author xch...
 * 
 */
public class BillChartPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -1175130133212277989L;

	private TBUtil tbUtil = new TBUtil(); //

	// �л�ͼ������
	private JRadioButton radioButton_1, radioButton_2, radioButton_3;
	private WLTPopupButton popBtn_deal; //���ô���!
	private JMenuItem menuItem_rowData, menuItem_colData, menuItem_rowColData; //�����ݣ�������,������
	private JMenuItem menuItem_layout_sx, menuItem_layout_zy; //�������Ҳ���!
	private JMenuItem menuItem_2d, menuItem_3d; //2D��3D��ʾ!
	private JMenuItem menuItem_maxWindow, menuItem_split; //ͼ����!

	private WLTPopupButton popBtn_export; //
	private JMenuItem menuItem_exportWord, menuItem_exportExcel; //

	// ����ͼ����ʾ����

	private boolean isCanReDrawChart = true; //�Ƿ�ɾ��ػ棬����API�޸�������ֵʱ�����ػ�!

	//ͼ������壬��Ƭ����
	private JPanel toftPanel = new JPanel(); //
	private CardLayout cardLayout = new CardLayout(); //
	private WLTSplitPane splitPane_1 = null; //
	private WLTSplitPane splitPane_2 = null; //
	private WLTSplitPane splitPane_3 = null; //

	private JTable jTable_1;
	private JTable jTable_2;
	private JTable jTable_3;

	private JFreeChart chart_bar = null;
	private JFreeChart chart_line = null;
	private JFreeChart chart_pie = null;

	private ChartPanel chartPanel_bar = null;
	private ChartPanel chartPanel_line = null;
	private ChartPanel chartPanel_pie = null;

	private JPanel barPanel = new JPanel(); //
	private JPanel linePanel = new JPanel(); //
	private JPanel piePanel = new JPanel(); //

	private String[] str_months;
	private String[] str_prodtypes;
	private BillChartItemVO[][] str_data;
	private String str_YTypes;
	private String str_XTypes;

	private boolean isBar3D = false, isLine3D = false, isPie3D = false;

	//����ͼ�������,�����������Щ������ȥ��������ֱ�Ӵ������������ȥ����Ϊ�и�X/Yת�Ṧ��ʹ����Щ��������������Դ���!
	//�������������ͼ����Զ���X/Yת��,������Щ�����Ե�û��̫��������!!
	//���Ժ�����ʵ��˫����ͷĳһ�л���ĳһ�еĵ�һ�У����Ծʹ��������е�ֵ��������,�Ӷ�ʵ�ֿͻ�һֱ��������ܣ���
	//������ʵ��һ���Ҽ��������ֶ���Ա�ͷ����ͷ�����ݽ�������,����ѧ�������յȼ��ȣ��Ǹ���ĳ���ض�����Ȼ˳������
	//������ʵ��һ�����ĳһ�����ʵ��һ���������򱨱�Ĺ��ܣ����Ǳ�ͷ�������ǵ�1��,��2��,��3����������е�������ԭ���ı�ͷ���ƣ����ֱ���ĺô��ǿ���ͬʱ��ʾ����ϸ������򣬲�������ֻ��ͬʱ��ʾһ��!

	private String title; //����
	private String str_ZLabels = "����"; // �������ƣ�һ������str_data����ֵ������
	private Color[] serialsColors = null; // ���е���ɫ����������û��
	private boolean isShowTotalColumn = true;

	private String str_builderClassName = null; //
	private HashMap queryConditionMap = null; //
	private String[] allCanGroupFields = null; //

	private String str_drillActionClass = null; //�ͻ���ʵ����ȥ��ʵ���ݵ���·����[����2012-06-08]
	private String str_drillTempletCode = null; //�ͻ�����ȡ��ϸʱ����һ������ģ��??

	private WLTButton expHtmlButton = null; //����html��ť   //Ԭ����20120908�޸ģ��ʴ���Ŀ�´�Ҫ��������ťƽ�̣�ȥ��֮ǰ��������������ͳ��ҳ�治һ��
	private WLTButton expWordButton = null; //����html��ť  //Ԭ����20120908�޸ģ��ʴ���Ŀ�´�Ҫ��������ťƽ�̣�ȥ��֮ǰ��������������ͳ��ҳ�治һ��
	private WLTButton expExcelButton = null; //����html��ť  //Ԭ����20120908�޸ģ��ʴ���Ŀ�´�Ҫ��������ťƽ�̣�ȥ��֮ǰ��������������ͳ��ҳ�治һ��

	private Color[] beautifulColors = new Color[] { new Color(254, 128, 0), new Color(0, 128, 128), new Color(128, 0, 128), //
			new Color(254, 0, 254), new Color(0, 254, 254), new Color(128, 0, 254), new Color(0, 128, 0), new Color(254, 254, 128), new Color(128, 0, 128), new Color(64, 0, 128), new Color(128, 128, 0), new Color(128, 128, 192), new Color(254, 128, 192), new Color(0, 128, 254), new Color(128, 64, 0), //
			new Color(225, 213, 218), new Color(74, 63, 228), new Color(200, 234, 210), new Color(224, 242, 26), new Color(231, 192, 229), new Color(98, 69, 77), new Color(192, 188, 226), new Color(73, 129, 90), new Color(241, 244, 210), //
			new Color(225, 39, 214), new Color(195, 229, 226), new Color(169, 143, 80), new Color(225, 48, 94), new Color(100, 99, 108), new Color(42, 231, 99), new Color(153, 159, 87), new Color(96, 69, 94), new Color(74, 143, 138), new Color(244, 178, 18) }; //һϵ�кÿ�����ɫ,һ��25��,���ν�������

	//jfreechart-1.0.14 ����������� �����/2013-06-09��
	{
		StandardChartTheme theme = new StandardChartTheme("unicode");

		theme.setExtraLargeFont(new Font("SimSun", Font.PLAIN, 20));
		theme.setLargeFont(new Font("SimSun", Font.PLAIN, 14));
		theme.setRegularFont(new Font("SimSun", Font.PLAIN, 12));
		theme.setSmallFont(new Font("SimSun", Font.PLAIN, 10));

		ChartFactory.setChartTheme(theme);
	}

	////Ĭ�Ϲ��췽������....
	private BillChartPanel() {
	}

	public BillChartPanel(String _title, String[] _serials, double[] _data) {
		distinctedArray(_serials); //
		this.setLayout(new BorderLayout());
		chart_pie = createPieChart(_title, _serials, _data, 1, "row"); //
		ChartPanel chartPanel = new ChartPanel(chart_pie); //
		chartPanel.setMouseWheelEnabled(true); //ͼ��ת�� �������� �����/2013-06-09��
		this.add(chartPanel); //
	}

	/**
	 * ͨ��ChartVO����
	 * 
	 * @param _title
	 * @param _xtypes
	 * @param _ytypes
	 * @param _chartVO
	 */
	public BillChartPanel(String _title, String _xtypes, String _ytypes, BillChartVO _chartVO) {
		this(getRealTitle(_title), _xtypes, _ytypes, getRealZLabel(_title), _chartVO.getXSerial(), _chartVO.getYSerial(), _chartVO.getDataVO(), null);
	}

	public BillChartPanel(String _title, String _xtypes, String _ytypes, BillChartVO _chartVO, boolean isShowTotalColumn) {
		this(getRealTitle(_title), _xtypes, _ytypes, getRealZLabel(_title), _chartVO.getXSerial(), _chartVO.getYSerial(), _chartVO.getDataVO(), null, isShowTotalColumn);
	}

	public BillChartPanel(String _title, String _xtypes, String _ytypes, String _zLabel, BillChartVO _chartVO) {
		this(getRealTitle(_title), _xtypes, _ytypes, _zLabel, _chartVO.getXSerial(), _chartVO.getYSerial(), _chartVO.getDataVO(), null);
	}

	public BillChartPanel(String _title, String _xtypes, String _ytypes, String[] _prodtypes, String[] _months) {
		this(getRealTitle(_title), _xtypes, _ytypes, getRealZLabel(_title), _prodtypes, _months, getBillChartItemVOs(_prodtypes, _months, null), null); //
	}

	public BillChartPanel(String _title, String _xtypes, String _ytypes, String[] _prodtypes, String[] _months, double[][] _data) {
		this(getRealTitle(_title), _xtypes, _ytypes, getRealZLabel(_title), _prodtypes, _months, getBillChartItemVOs(_prodtypes, _months, _data), null); //
	}

	public BillChartPanel(String _title, String _xtypes, String _ytypes, String _zLabel, String[] _prodtypes, String[] _months, double[][] _data) {
		this(getRealTitle(_title), _xtypes, _ytypes, _zLabel, _prodtypes, _months, getBillChartItemVOs(_prodtypes, _months, _data), null); //
	}

	public BillChartPanel(String _title, String _xtypes, String _ytypes, String[] _prodtypes, String[] _months, BillChartItemVO[][] _chartItemVOs) {
		this(getRealTitle(_title), _xtypes, _ytypes, getRealZLabel(_title), _prodtypes, _months, _chartItemVOs, null); //
	}

	public BillChartPanel(String _title, String _xtypes, String _ytypes, String[] _prodtypes, String[] _months, double[][] _data, Color[] _colors) {
		this(getRealTitle(_title), _xtypes, _ytypes, getRealZLabel(_title), _prodtypes, _months, getBillChartItemVOs(_prodtypes, _months, _data), _colors); //
	}

	public BillChartPanel(String _title, String _xtypes, String _ytypes, String _zLabel, String[] _prodtypes, String[] _months, double[][] _data, Color[] _colors) {
		this(getRealTitle(_title), _xtypes, _ytypes, _zLabel, _prodtypes, _months, getBillChartItemVOs(_prodtypes, _months, _data), _colors); //
	}

	public BillChartPanel(String _title, String _xtypes, String _ytypes, String[] _prodtypes, String[] _months, BillChartItemVO[][] _chartItemVOs, Color[] _colors) {
		this(getRealTitle(_title), _xtypes, _ytypes, getRealZLabel(_title), _prodtypes, _months, _chartItemVOs, _colors); //
	}

	public BillChartPanel(String _title, String _xtypes, String _ytypes, String _zLabel, String[] _prodtypes, String[] _months, BillChartItemVO[][] _chartItemVOs, Color[] _colors) {
		distinctedArray(_prodtypes); //
		distinctedArray(_months); //
		serialsColors = _colors; //
		initialize(_title, _xtypes, _ytypes, _zLabel, _prodtypes, _months, _chartItemVOs); //
	}

	public BillChartPanel(String _title, String _xtypes, String _ytypes, String _zLabel, String[] _prodtypes, String[] _months, BillChartItemVO[][] _chartItemVOs, Color[] _colors, boolean isShowTotalColumn) {
		distinctedArray(_prodtypes); //
		distinctedArray(_months); //
		serialsColors = _colors; //
		this.isShowTotalColumn = isShowTotalColumn;
		initialize(_title, _xtypes, _ytypes, _zLabel, _prodtypes, _months, _chartItemVOs); //
	}

	//��̬����!!
	public static BillChartPanel getInstance() {
		return new BillChartPanel(); //
	}

	private static String getRealTitle(String _title) {
		if (_title.indexOf("<") > 0) {
			return _title.substring(0, _title.indexOf("<")); //
		} else {
			return _title;
		}
	}

	private static String getRealZLabel(String _title) {
		if (_title.indexOf("<") > 0) {
			return _title.substring(_title.indexOf("<") + 1, _title.length() - 1); //
		} else {
			return "����";
		}
	}

	private static BillChartItemVO[][] getBillChartItemVOs(String[] _prodtypes, String[] _months, double[][] _doubleValues) {
		BillChartItemVO[][] chartItemVOs = null;
		if (_doubleValues == null) {
			chartItemVOs = new BillChartItemVO[_prodtypes.length][_months.length]; //
			Random random = new Random(); //
			for (int i = 0; i < _prodtypes.length; i++) {
				for (int j = 0; j < _months.length; j++) {
					chartItemVOs[i][j] = new BillChartItemVO(2 + random.nextInt(20)); //
				}
			}
		} else {
			chartItemVOs = new BillChartItemVO[_doubleValues.length][_doubleValues[0].length]; //
			for (int i = 0; i < chartItemVOs.length; i++) {
				for (int j = 0; j < chartItemVOs[i].length; j++) {
					chartItemVOs[i][j] = new BillChartItemVO(_doubleValues[i][j]); ////
				}
			}
		}
		return chartItemVOs;
	}

	private void convertXY() {
		BillChartItemVO[][] data = new BillChartItemVO[str_months.length][str_prodtypes.length]; //
		for (int i = 0; i < str_months.length; i++) {
			for (int j = 0; j < str_prodtypes.length; j++) {
				if (str_data[j][i] == null) {
					//data[i][j].setValue(0.0);
				} else {
					data[i][j] = str_data[j][i];
				}//
			}
		}
		this.setMember(this.title, this.str_YTypes, this.str_XTypes, this.str_months, this.str_prodtypes, data); //
	}

	//kl modify
	private void setMember(String title2, String str_YTypes2, String str_XTypes2, String[] str_months2, String[] str_prodtypes2, BillChartItemVO[][] data) {
		this.str_YTypes = str_XTypes2;
		this.str_XTypes = str_YTypes2;
		this.str_months = str_prodtypes2;
		this.str_prodtypes = str_months2;
		this.str_data = data;
		distinctedArray(str_prodtypes); //Ϊ�˷�ֹ�ظ�,���ظ�����������ϰ���������������..
		distinctedArray(str_months); //Ϊ�˷�ֹ�ظ�,���ظ�����������ϰ���������������..
		this.add(getMainPanel(str_XTypes, str_YTypes, str_prodtypes, str_months, str_data), BorderLayout.CENTER);
		generateBarChartFromTable1("��ʼ��..."); //
		generateLineChartFromTable2("��ʼ��");
		generatePieChartFromTable3("��ʼ��..."); //
		radioButton_1.setSelected(true); //
	}

	/**
	 * ��ʼ��
	 * 
	 * @param _title
	 * @param _xtypes
	 * @param _ytypes
	 * @param _prodtypes
	 * @param _months
	 * @param _data
	 */
	private void initialize(String _title, String _xtypes, String _ytypes, String _zLabel, String[] _prodtypes, String[] _months, BillChartItemVO[][] _data) {
		this.title = _title;
		this.str_ZLabels = _zLabel; //

		this.str_months = _months;
		this.str_data = _data;
		this.str_prodtypes = _prodtypes;
		this.str_YTypes = _ytypes;
		this.str_XTypes = _xtypes;
		initColor();
		distinctedArray(_prodtypes); //Ϊ�˷�ֹ�ظ�,���ظ�����������ϰ���������������..
		distinctedArray(_months); //Ϊ�˷�ֹ�ظ�,���ظ�����������ϰ���������������..
		this.removeAll(); //
		this.setLayout(new BorderLayout());
		this.add(getNorthPanel(), BorderLayout.NORTH);
		this.add(getMainPanel(_xtypes, _ytypes, _prodtypes, _months, _data), BorderLayout.CENTER);
		generateBarChartFromTable1("��ʼ��..."); //
		generateLineChartFromTable2("��ʼ��");
		generatePieChartFromTable3("��ʼ��..."); //
	}

	//���ж�������!
	public void actionPerformed(ActionEvent e) {
		Object objSource = e.getSource(); //
		if (objSource == radioButton_1) {
			cardLayout.show(toftPanel, "bar"); //
		} else if (objSource == radioButton_2) {
			cardLayout.show(toftPanel, "line"); //
		} else if (objSource == radioButton_3) {
			cardLayout.show(toftPanel, "pie"); //
		} else if (objSource == menuItem_rowData || objSource == menuItem_colData || objSource == menuItem_rowColData) { //����������е�ѡ��
			int li_type = getChartTypeRadioSelected(); //
			JTable delTable = null; //
			if (li_type == 1) {
				delTable = jTable_1;
			} else if (li_type == 2) {
				delTable = jTable_2;
			} else if (li_type == 3) {
				delTable = jTable_3;
			}

			if (objSource == menuItem_rowData) { //������!!
				delTable.setRowSelectionAllowed(true);
				delTable.setColumnSelectionAllowed(false);
			} else if (objSource == menuItem_colData) { //������!!
				delTable.setRowSelectionAllowed(false);
				delTable.setColumnSelectionAllowed(true);
			} else if (objSource == menuItem_rowColData) { //������!!
				delTable.setRowSelectionAllowed(true);
				delTable.setColumnSelectionAllowed(true);
			}

			if (li_type == 1) {
				generateBarChartFromTable1("����ѡ��ģ���л�");
			} else if (li_type == 2) {
				generateLineChartFromTable2("����ѡ��ģ���л�");
			} else if (li_type == 3) {
				generatePieChartFromTable3("����ѡ��ģ���л�");
			}
		} else if (objSource == menuItem_layout_sx || objSource == menuItem_layout_zy) {
			int li_type = getChartTypeRadioSelected(); //
			WLTSplitPane dealSplitPanel = null; //
			if (li_type == 1) {
				dealSplitPanel = splitPane_1;
			} else if (li_type == 2) {
				dealSplitPanel = splitPane_2;
			} else if (li_type == 3) {
				dealSplitPanel = splitPane_3;
			}
			if (objSource == menuItem_layout_sx) {
				dealSplitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
			} else if (objSource == menuItem_layout_zy) {
				dealSplitPanel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			}
		} else if (objSource == menuItem_2d || objSource == menuItem_3d) {
			int li_type = getChartTypeRadioSelected(); //
			if (li_type == 1) {
				if (objSource == menuItem_2d) {
					generateBarChartFromTable1("ͼ��2/3ά�л�", false); //�ػ�
				} else if (objSource == menuItem_3d) {
					generateBarChartFromTable1("ͼ��2/3ά�л�", true); //�ػ�
				}
			} else if (li_type == 3) { //��ͼ
				if (objSource == menuItem_2d) {
					generatePieChartFromTable3("ͼ��2/3ά�л�", false); //�ػ�
				} else if (objSource == menuItem_3d) {
					generatePieChartFromTable3("ͼ��2/3ά�л�", true); //�ػ�
				}
			} else {
				if (objSource == menuItem_2d) {
					generateLineChartFromTable2("ͼ��2/3ά�л�", false); //�ػ�
				} else if (objSource == menuItem_3d) {
					generateLineChartFromTable2("ͼ��2/3ά�л�", true); //�ػ�
				}
				/*				if (objSource == menuItem_3d) {
									MessageBox.show(this, "ֻ������ͼ���ͼ3άЧ��,����ͼû��!"); //
								}*/
			}
		} else if (objSource == menuItem_split) {
			chartSplit(); //
		} else if (objSource == menuItem_maxWindow) { //ͼ��Ŵ�
			chartZoom(); //
		} else if (objSource == popBtn_deal.getButton()) { //XYת��
			convertXY(); //
		} else if (objSource == expWordButton) {//����HtmlԬ����20120908�޸�
			exportWordFile(); //
		} else if (objSource == expExcelButton) {//����HtmlԬ����20120908�޸�
			exportExcel(); //
		} else if (objSource == expHtmlButton) { //����HtmlԬ����20120908�޸�
			exportHTML(); //
			//exportMHT(); //���Ҳ����,��ͼƬ����ѹ����̫������!
		}
	}

	/**
	 * ����һ�Ű�ť...
	 * @return
	 */
	private JPanel getNorthPanel() {
		JPanel jPanel_north = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.LEFT)); //

		//����ͼ�ΰ�ť!
		radioButton_1 = new JRadioButton("����ͼ", true);
		radioButton_2 = new JRadioButton("����ͼ");
		radioButton_3 = new JRadioButton("����ͼ");
		radioButton_1.setOpaque(false); //
		radioButton_2.setOpaque(false); //
		radioButton_3.setOpaque(false); //
		ButtonGroup group = new ButtonGroup();
		group.add(radioButton_1);
		group.add(radioButton_2);
		group.add(radioButton_3);
		radioButton_1.addActionListener(this); //
		radioButton_2.addActionListener(this); //
		radioButton_3.addActionListener(this); //
		jPanel_north.add(radioButton_1); //
		jPanel_north.add(radioButton_2); //
		jPanel_north.add(radioButton_3); //

		//����������
		Dimension dimSize = new Dimension(80, 20); // 
		menuItem_rowData = new JMenuItem("������"); //
		menuItem_colData = new JMenuItem("������"); //
		menuItem_rowColData = new JMenuItem("������"); //

		menuItem_layout_sx = new JMenuItem("���²���"); //
		menuItem_layout_zy = new JMenuItem("���Ҳ���"); //

		menuItem_2d = new JMenuItem("2άЧ��"); //
		menuItem_3d = new JMenuItem("3άЧ��"); //

		menuItem_maxWindow = new JMenuItem("ͼ��Ŵ�"); //
		menuItem_split = new JMenuItem("ͼ����"); //

		menuItem_rowData.setPreferredSize(dimSize); //
		menuItem_colData.setPreferredSize(dimSize); //
		menuItem_rowColData.setPreferredSize(dimSize); //
		menuItem_layout_sx.setPreferredSize(dimSize); //
		menuItem_layout_zy.setPreferredSize(dimSize); //
		menuItem_2d.setPreferredSize(dimSize); //
		menuItem_3d.setPreferredSize(dimSize); //
		menuItem_maxWindow.setPreferredSize(dimSize); //
		menuItem_split.setPreferredSize(dimSize); //

		menuItem_rowData.addActionListener(this); //
		menuItem_colData.addActionListener(this); //
		menuItem_rowColData.addActionListener(this); //

		menuItem_layout_sx.addActionListener(this); //
		menuItem_layout_zy.addActionListener(this); //

		menuItem_2d.addActionListener(this); //
		menuItem_3d.addActionListener(this); //

		menuItem_maxWindow.addActionListener(this); //
		menuItem_split.addActionListener(this); //

		JPopupMenu popMenu_deal = new JPopupMenu(); //
		popMenu_deal.add(menuItem_rowData); //
		popMenu_deal.add(menuItem_colData); //
		popMenu_deal.add(menuItem_rowColData); //

		popMenu_deal.addSeparator(); //
		popMenu_deal.add(menuItem_layout_sx); //
		popMenu_deal.add(menuItem_layout_zy); //

		popMenu_deal.addSeparator(); //
		popMenu_deal.add(menuItem_2d); //
		popMenu_deal.add(menuItem_3d); //

		popMenu_deal.addSeparator(); //
		popMenu_deal.add(menuItem_maxWindow); //
		popMenu_deal.add(menuItem_split); //

		popBtn_deal = new WLTPopupButton(WLTPopupButton.TYPE_WITH_RIGHT_TOGGLE, "X/Yת��", UIUtil.getImage("office_169.gif"), popMenu_deal); //
		popBtn_deal.setPreferredSize(new Dimension(95, 23)); // ���С
		popBtn_deal.getButton().setBackground(LookAndFeel.btnbgcolor); //systembgcolor
		popBtn_deal.getPopButton().setBackground(LookAndFeel.btnbgcolor); //
		popBtn_deal.getButton().addActionListener(this); //

		jPanel_north.add(popBtn_deal); //

		//֮ǰ�߼����õ���������  ���ʴ���ĿҪ�󻻳�button������񱨱�ҳ�汣��һ��
		/*		//����
				menuItem_exportWord = new JMenuItem("����Word"); //
				menuItem_exportExcel = new JMenuItem("����Excel"); //

				menuItem_exportWord.setPreferredSize(dimSize); //
				menuItem_exportExcel.setPreferredSize(dimSize); //

				menuItem_exportWord.addActionListener(this); //
				menuItem_exportExcel.addActionListener(this); //

				JPopupMenu popMenu_export = new JPopupMenu(); //
				popMenu_export.add(menuItem_exportWord); //
				popMenu_export.add(menuItem_exportExcel); //

				popBtn_export = new WLTPopupButton(WLTPopupButton.TYPE_WITH_RIGHT_TOGGLE, "����", UIUtil.getImage("office_192.gif"), popMenu_export); //
				popBtn_export.setPreferredSize(new Dimension(95, 23)); // ���С
				popBtn_export.getButton().setBackground(LookAndFeel.btnbgcolor); //systembgcolor
				popBtn_export.getPopButton().setBackground(LookAndFeel.btnbgcolor); //
				popBtn_export.getButton().addActionListener(this); //
				//jPanel_north.add(popBtn_export); //
		*/
		expHtmlButton = new WLTButton("����Html", "office_192.gif");
		expWordButton = new WLTButton("����Word", "office_124.gif");
		expExcelButton = new WLTButton("����Excel", "office_170.gif");
		expHtmlButton.addActionListener(this);
		expWordButton.addActionListener(this);
		expExcelButton.addActionListener(this);

		jPanel_north.add(expHtmlButton);
		jPanel_north.add(expWordButton);
		jPanel_north.add(expExcelButton);

		return jPanel_north;
	}

	public int getChartTypeRadioSelected() {
		if (radioButton_1.isSelected()) {
			return 1;
		} else if (radioButton_2.isSelected()) {
			return 2;
		} else if (radioButton_3.isSelected()) {
			return 3;
		} else {
			return 0;
		}
	}

	/**
	 * �õ�ѡ�е��к�
	 * 
	 * @return
	 */
	public int getSelectedRows() {
		if (radioButton_1.isSelected()) {
			return jTable_1.getSelectedRow();
		} else if (radioButton_2.isSelected()) {
			return jTable_2.getSelectedRow();
		} else if (radioButton_3.isSelected()) {
			return jTable_3.getSelectedRow();
		}
		return 0;
	}

	/**
	 * �õ�ѡ�е��к�
	 * 
	 * @return
	 */
	public int getSelectedColumn() {
		if (radioButton_1.isSelected()) {
			return jTable_1.getSelectedColumn();
		} else if (radioButton_2.isSelected()) {
			return jTable_2.getSelectedColumn();
		} else if (radioButton_3.isSelected()) {
			return jTable_3.getSelectedColumn();
		}
		return 0;
	}

	private JPanel getMainPanel(String _xtypes, String _ytypes, String[] _prodtypes, String[] _months, BillChartItemVO[][] _data) {
		toftPanel.setLayout(cardLayout); //
		toftPanel.removeAll(); //
		toftPanel.add("bar", getPanel_1(this.title, _xtypes, _ytypes, this.str_ZLabels, _prodtypes, _months, _data)); // ����
		toftPanel.add("line", getPanel_2(this.title, _xtypes, _ytypes, this.str_ZLabels, _prodtypes, _months, _data)); // ����
		toftPanel.add("pie", getPanel_3(this.title, _xtypes, _ytypes, this.str_ZLabels, _prodtypes, _months, _data)); // ��ͼ..
		return toftPanel;
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
		if (!isShowTotalColumn) {
			str_labels = new String[li_colCount - 1]; //
		}

		for (int i = 0; i < str_labels.length; i++) {
			str_labels[i] = "" + _table.getColumnModel().getColumn(i + 1).getHeaderValue(); //
		}

		return str_labels;
	}

	//�Ŵ�ͼ��
	private void chartZoom() {
		ChartPanel tempChartPanel = null; //
		if (radioButton_1.isSelected()) {
			tempChartPanel = chartPanel_bar;
		} else if (radioButton_2.isSelected()) {
			tempChartPanel = chartPanel_line;
		} else if (radioButton_3.isSelected()) {
			tempChartPanel = chartPanel_pie;
		}

		JFreeChart chart = null;
		try {
			chart = (JFreeChart) tempChartPanel.getChart().clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return;
		}
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setMouseWheelEnabled(true); //ͼ��ת�� �������� �����/2013-06-09��
		JScrollPane scrollPane = new JScrollPane(chartPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(100);

		JSlider sliderBar = new JSlider(SwingConstants.HORIZONTAL, 0, 100, 0);
		ChangeListener changeListener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int width = 0;
				int height = 0;
				int min_width = 0;
				int min_height = 0;
				int horizonalStepLength = 48;
				int veticalStepLength = 28;
				if (width == 0 && height == 0) {
					Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
					min_width = d.width;
					min_height = d.height;
				}

				JSlider slider = (JSlider) e.getSource();
				int adjust = slider.getValue();
				width = min_width + horizonalStepLength * adjust;
				height = min_height + veticalStepLength * adjust;
				chartPanel.setPreferredSize(new Dimension(width, height));
				chartPanel.updateUI();
			}
		};

		sliderBar.addChangeListener(changeListener); //

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(sliderBar, BorderLayout.NORTH);

		JFrame frame = new JFrame("ͼ��Ŵ���ʾ");
		frame.add(panel);
		frame.setSize(Toolkit.getDefaultToolkit().getScreenSize());
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
	}

	/**
	 * Ϊ�˴�ӡЧ�����������ͼ��
	 */
	private void chartSplit() {
		int li_selected = getChartTypeRadioSelected(); //
		String str_xType = null; //
		String str_yType = null; //
		String str_zType = null; //
		String[] str_prodTypes = null; //
		String[] str_months = null; //
		BillChartItemVO[][] chartItemVOs = null; //
		if (li_selected == 1) {
			str_xType = (String) jTable_1.getClientProperty("X_Type"); //
			str_yType = (String) jTable_1.getClientProperty("Y_Type"); //
			str_zType = (String) jTable_1.getClientProperty("Z_Type"); //
			str_prodTypes = getAllRow1Names(this.jTable_1); //
			str_months = getAllHeadNames(jTable_1); //
			chartItemVOs = getAllDataFromTable(jTable_1); //
		} else if (li_selected == 2) {
			str_xType = (String) jTable_2.getClientProperty("X_Type"); //
			str_yType = (String) jTable_2.getClientProperty("Y_Type"); //
			str_zType = (String) jTable_2.getClientProperty("Z_Type"); //
			str_prodTypes = getAllRow1Names(this.jTable_2); //
			str_months = getAllHeadNames(jTable_2); //
			chartItemVOs = getAllDataFromTable(jTable_2); //
		} else if (li_selected == 3) {
			str_xType = (String) jTable_3.getClientProperty("X_Type"); //
			str_yType = (String) jTable_3.getClientProperty("Y_Type"); //
			str_zType = (String) jTable_3.getClientProperty("Z_Type"); //
			str_prodTypes = getAllRow1Names(this.jTable_3); //
			str_months = getAllHeadNames(jTable_3); //
			chartItemVOs = getAllDataFromTable(jTable_3); //
		}

		int maxRowCount = str_prodTypes.length; //
		int maxColCount = str_months.length; //

		// �������öԻ���
		JPanel setRowGroupPanel = new WLTPanel(new FlowLayout(FlowLayout.LEFT));
		setRowGroupPanel.add(new JLabel("��������������"));
		JTextField rowCountEachGroupText = new JTextField(5);// String.valueOf(maxRowCount),
		rowCountEachGroupText.setHorizontalAlignment(SwingConstants.RIGHT);
		rowCountEachGroupText.setDocument(new NumberFormatdocument());
		rowCountEachGroupText.setText(String.valueOf(maxRowCount));
		setRowGroupPanel.add(rowCountEachGroupText);

		JPanel setColGroupPanel = new WLTPanel(new FlowLayout(FlowLayout.LEFT));
		setColGroupPanel.add(new JLabel("��������������"));
		JTextField colCountEachGroupText = new JTextField(5);// String.valueOf(maxColCount),
		colCountEachGroupText.setHorizontalAlignment(SwingConstants.RIGHT);
		colCountEachGroupText.setDocument(new NumberFormatdocument());
		colCountEachGroupText.setText(String.valueOf(maxColCount));
		setColGroupPanel.add(colCountEachGroupText);

		Box vBox = Box.createVerticalBox();
		vBox.add(setRowGroupPanel);
		vBox.add(setColGroupPanel);

		int returnValue = JOptionPane.showConfirmDialog(this, vBox, "���÷�������", JOptionPane.OK_CANCEL_OPTION);
		if (returnValue != JOptionPane.YES_OPTION) {
			return;
		}

		// ���ú��ˣ�������
		int rowCountEachGroup = Integer.parseInt(rowCountEachGroupText.getText());
		int colCountEachGroup = Integer.parseInt(colCountEachGroupText.getText());

		// To defend input wrong number.
		if (rowCountEachGroup < 0 || rowCountEachGroup > maxRowCount) {
			MessageBox.showWarn(this, "����д�ķ���������" + rowCountEachGroup + "����ȷ��ȡֵ����Ӧ����[1," + maxRowCount + "]֮��");
			return;
		}
		if (colCountEachGroup < 0 || colCountEachGroup > maxColCount) {
			MessageBox.showWarn(this, "����д�ķ���������" + colCountEachGroup + "����ȷ��ȡֵ����Ӧ����[1," + maxColCount + "]֮��");
			return;
		}

		int rowGroupCount = maxRowCount % rowCountEachGroup == 0 ? maxRowCount / rowCountEachGroup : maxRowCount / rowCountEachGroup + 1;
		int colGroupCount = maxColCount % colCountEachGroup == 0 ? maxColCount / colCountEachGroup : maxColCount / colCountEachGroup + 1;

		WLTTabbedPane tabbedPane = new WLTTabbedPane();
		String[] xGroups = null; // new ArrayList<String>();
		String[] yGroups = null; // new ArrayList<String>();
		BillChartItemVO[][] data = null;
		int realRowBegin = 0;
		int realColBegin = 0;
		for (int i = 0; i < rowGroupCount; i++) {
			realRowBegin = i * rowCountEachGroup;
			// Ҫ��ֳ���������
			if (i != rowGroupCount - 1) {
				xGroups = new String[rowCountEachGroup];
			} else {// ������һ�����飬��ֹԽ��
				int xGroupSize = str_prodTypes.length - realRowBegin;
				xGroups = new String[xGroupSize];
			}

			for (int rowIndex = 0; rowIndex < xGroups.length; rowIndex++) {
				xGroups[rowIndex] = str_prodTypes[realRowBegin + rowIndex];
			}

			for (int j = 0; j < colGroupCount; j++) {
				realColBegin = j * colCountEachGroup;
				// Ҫ��ֳ���������
				if (j != colGroupCount - 1) {
					yGroups = new String[colCountEachGroup];
				} else {// ������һ�����飬��ֹԽ��
					int yGroupSize = str_months.length - realColBegin;
					yGroups = new String[yGroupSize];
				}

				for (int colIndex = 0; colIndex < yGroups.length; colIndex++) {
					yGroups[colIndex] = str_months[realColBegin + colIndex];
				}

				data = new BillChartItemVO[xGroups.length][yGroups.length];
				for (int rowIndex = 0; rowIndex < xGroups.length; rowIndex++) {
					for (int colIndex = 0; colIndex < yGroups.length; colIndex++) {
						data[rowIndex][colIndex] = chartItemVOs[realRowBegin + rowIndex][realColBegin + colIndex];
					}
				}

				JPanel chartPanel = new BillChartPanel(title, str_xType, str_yType, xGroups, yGroups, data); //
				tabbedPane.addTab("����" + (i * colGroupCount + j + 1), chartPanel);
			}
		}

		JFrame chartSplitFrame = new JFrame("ͼ����");
		chartSplitFrame.getContentPane().add(tabbedPane);
		chartSplitFrame.setLocation(300, 0);
		chartSplitFrame.setSize(1000, 800);
		chartSplitFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		chartSplitFrame.setVisible(true);
	}

	/**
	 * ����һ�л����е���һ���µ�BillChartPanel
	 * 
	 * @param _type
	 *            ���ͣ��л����У�
	 * @param _columnOrRow
	 *            (ѡ����л�����)
	 * @return ����64λ�ַ���
	 */
	public JFreeChart createOneColumnOrRowChart(int _type, int _sumType) {
		JFreeChart newChart = null;
		if (_type == 1) {
			String str_xType = (String) jTable_1.getClientProperty("X_Type"); //
			String str_yType = (String) jTable_1.getClientProperty("Y_Type"); //
			String str_zType = (String) jTable_1.getClientProperty("Z_Type"); //
			if (_sumType == 1) { //�кϼ�
				String[] str_labels = getAllHeadNames(jTable_1); //
				double[][] ld_data = new double[1][str_labels.length]; //
				for (int i = 0; i < str_labels.length; i++) {
					BillChartItemVO chartItemVO = (BillChartItemVO) jTable_1.getValueAt(jTable_1.getRowCount() - 1, i + 1); //
					ld_data[0][i] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue()); //
				}
				newChart = createBarChart(this.title, str_xType, str_zType, new String[] { "�кϼ�" }, str_labels, ld_data, false, null);
			} else {
				String[] str_labels = getAllRow1Names(jTable_1); //
				double[][] ld_data = new double[1][str_labels.length]; //
				for (int i = 0; i < str_labels.length; i++) {
					BillChartItemVO chartItemVO = (BillChartItemVO) jTable_1.getValueAt(i, jTable_1.getColumnCount() - 1); //
					ld_data[0][i] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue()); //
				}
				newChart = createBarChart(this.title, str_yType, str_zType, new String[] { "�кϼ�" }, str_labels, ld_data, false, null);
			}
		} else if (_type == 2) {
			String str_xType = (String) jTable_2.getClientProperty("X_Type"); //
			String str_yType = (String) jTable_2.getClientProperty("Y_Type"); //
			String str_zType = (String) jTable_2.getClientProperty("Z_Type"); //
			if (_sumType == 1) {
				String[] str_labels = getAllHeadNames(jTable_2); //
				double[][] ld_data = new double[1][str_labels.length]; //
				for (int i = 0; i < str_labels.length; i++) {
					BillChartItemVO chartItemVO = (BillChartItemVO) jTable_2.getValueAt(jTable_2.getRowCount() - 1, i + 1); //
					ld_data[0][i] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue()); //
				}
				newChart = createLineChart(this.title, str_xType, str_zType, new String[] { "�кϼ�" }, str_labels, ld_data, isLine3D, null);
			} else {
				String[] str_labels = getAllRow1Names(jTable_2); //
				double[][] ld_data = new double[1][str_labels.length]; //
				for (int i = 0; i < str_labels.length; i++) {
					BillChartItemVO chartItemVO = (BillChartItemVO) jTable_2.getValueAt(i, jTable_2.getColumnCount() - 1); //
					ld_data[0][i] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue()); //
				}
				newChart = createLineChart(this.title, str_yType, str_zType, new String[] { "�кϼ�" }, str_labels, ld_data, isLine3D, null);
			}
		} else if (_type == 3) {
			String str_xType = (String) jTable_3.getClientProperty("X_Type"); //
			String str_yType = (String) jTable_3.getClientProperty("Y_Type"); //
			String str_zType = (String) jTable_3.getClientProperty("Z_Type"); //
			if (_sumType == 1) {
				String[] str_labels = getAllHeadNames(jTable_3); //
				double[] ld_data = new double[str_labels.length]; //
				for (int i = 0; i < str_labels.length; i++) {
					BillChartItemVO chartItemVO = (BillChartItemVO) jTable_3.getValueAt(jTable_3.getRowCount() - 1, i + 1); //
					ld_data[i] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue()); ////
				}
				newChart = createPieChart("ͼ��", str_labels, ld_data, 0, null);
			} else {
				String[] str_labels = getAllRow1Names(jTable_3); //
				double[] ld_data = new double[str_labels.length]; //
				for (int i = 0; i < str_labels.length; i++) {
					BillChartItemVO chartItemVO = (BillChartItemVO) jTable_3.getValueAt(i, jTable_3.getColumnCount() - 1); //
					ld_data[i] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue()); ////
				}
				newChart = createPieChart("ͼ��", str_labels, ld_data, 0, null);
			}
		}
		return newChart;
	}

	/**
	 * ȡ�����ݱ��..
	 * @param _type
	 * @return
	 */
	private JTable getDataTable(final int _type, String _xType, String _yType, String _zLabel, String[] _prodtypes, String[] _months, BillChartItemVO[][] _data) {
		String[] str_realColumns = new String[_months.length + 2];
		str_realColumns[0] = (_yType + "�� " + _xType + "<" + _zLabel + ">"); //
		for (int i = 0; i < _months.length; i++) {
			str_realColumns[i + 1] = _months[i]; //
		}
		str_realColumns[str_realColumns.length - 1] = "�кϼ�"; //

		Object[][] realCellData = new Object[_prodtypes.length + 1][_months.length + 2]; //�����ʵ�ʴ洢��ֵ...
		for (int i = 0; i < _prodtypes.length; i++) {
			realCellData[i][0] = _prodtypes[i];
		}
		realCellData[_prodtypes.length][0] = "�кϼ�"; //

		//ʵ������
		boolean isRateValueType = false; //���������Ƿ��Ǳ�������???
		boolean isPercent = false; //�Ƿ��ǰٷֱ�
		for (int i = 0; i < _data.length; i++) {
			for (int j = 0; j < _data[i].length; j++) {
				if (_data[i][j] == null) {
					realCellData[i][j + 1] = new BillChartItemVO(0); //���Ϊ��,��0����,�Ƿ���Ҫ��0�������д�����!!!!!?????
				} else {
					realCellData[i][j + 1] = _data[i][j];
					if (_data[i][j].getValueType() == BillChartItemVO.RATE) { //ֻҪ��һ���Ǳ�������,�������������;��Ǳ�����,��Ϊ�п�����Щ����Ϊȡ����ֵ����ΪTotal����
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
		//�кϼ�
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
					if (isRateValueType) { //����Ǳ�������
						if (tempItemVO.getValueType() == 2 && tempItemVO.getCountValue() != 0) { //ֻ�������Ǳ������͵���countֵ��Ϊ0��!!���ۼ�,��Ϊ�е���Ϊ�Ҳ���ֵ��������Total
							ld_item_sum = ld_item_sum + ld_temp_sum; //�ܺ�,����
							ld_item_count = ld_item_count + ld_temp_count; //������
							ld_all_sum = ld_all_sum + ld_temp_sum; //
							ld_all_count = ld_all_count + ld_temp_count; //
						}
					} else {
						ld_item_value = ld_item_value + ld_temp_value; //
						//ld_all_value = ld_all_value + ld_temp_value; //�ܼ��ۼ�
					}
				}
			}
			if (isRateValueType) {
				realCellData[_prodtypes.length][i + 1] = new BillChartItemVO(ld_item_sum, ld_item_count, isPercent); //	
			} else {
				realCellData[_prodtypes.length][i + 1] = new BillChartItemVO(ld_item_value, isPercent); //	
			}
		}

		//�кϼ�
		for (int i = 0; i < _prodtypes.length; i++) {
			double ld_item_sum = 0; //
			double ld_item_count = 0; //
			double ld_item_value = 0; //
			for (int j = 0; j < _months.length; j++) { //����
				if (realCellData[i][j + 1] != null) { //
					BillChartItemVO tempItemVO = (BillChartItemVO) realCellData[i][j + 1]; //
					double ld_temp_sum = tempItemVO.getSumValue();
					double ld_temp_count = tempItemVO.getCountValue();
					double ld_temp_value = tempItemVO.getValue(); //
					if (isRateValueType) { //����Ǳ�������
						if (tempItemVO.getValueType() == 2 && tempItemVO.getCountValue() != 0) { //ֻ�������Ǳ������͵���countֵ��Ϊ0��!!���ۼ�,��Ϊ�е���Ϊ�Ҳ���ֵ��������Total
							ld_item_sum = ld_item_sum + ld_temp_sum; //�ܺ�,����
							ld_item_count = ld_item_count + ld_temp_count; //������
							ld_all_sum = ld_all_sum + ld_temp_sum; //
							ld_all_count = ld_all_count + ld_temp_count; //
						}
					} else {
						ld_item_value = ld_item_value + ld_temp_value; //
						ld_all_value = ld_all_value + ld_temp_value; //�ܼ��ۼ�
					}
				}
			}
			if (isRateValueType) { //����Ǳ�������
				realCellData[i][_months.length + 1] = new BillChartItemVO(ld_item_sum, ld_item_count, isPercent); //	
			} else {
				realCellData[i][_months.length + 1] = new BillChartItemVO(ld_item_value, isPercent); //	
			}
		}

		//�����½ǵ��ܼ�
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

		JTable table_temp = new JTable(model); //Ҫ����BillListPanelô?
		if (!isShowTotalColumn()) {
			table_temp.getColumnModel().removeColumn(table_temp.getColumn("�кϼ�"));
		}
		table_temp.getTableHeader().setReorderingAllowed(false); //��֮�䲻���˹�����
		table_temp.getTableHeader().setPreferredSize(new Dimension(20000, 27)); //Ԭ����20121009�޸ģ�֮ǰΪ-1���ͷΪ��ͷֻ��Ӧ��Ļ����϶����ͷ���϶�
		table_temp.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //
		table_temp.setRowSelectionAllowed(true); //
		table_temp.setColumnSelectionAllowed(false); //
		table_temp.setRowHeight(23); //
		table_temp.setToolTipText("˫�����ݸ�ʽ���Բ鿴��ϸ!"); //

		MyCellRender cellRender = new MyCellRender(); //
		int[] li_colWidths = new int[table_temp.getColumnCount()]; //
		int li_allwidths = 0; //
		for (int i = 0; i < table_temp.getColumnCount(); i++) { //������!
			table_temp.getColumnModel().getColumn(i).setCellRenderer(cellRender); //
			li_colWidths[i] = tbUtil.getStrWidth(table_temp.getTableHeader().getFont(), table_temp.getColumnName(i)) + 15; //
			li_colWidths[i] = (li_colWidths[i] > 200 ? 200 : li_colWidths[i]); //
			li_allwidths = li_allwidths + li_colWidths[i];
		}
		if (li_allwidths < 666) { //Ϊ������,���С��700�����������������֤�����ı���������Զ�Ʒ����棬�����Ƕ���һ������!!
			int li_addValue = (666 - li_allwidths) / li_colWidths.length; //
			for (int i = 0; i < li_colWidths.length; i++) {
				li_colWidths[i] = li_colWidths[i] + li_addValue;
			}
		}
		for (int i = 0; i < li_colWidths.length; i++) {
			table_temp.getColumnModel().getColumn(i).setPreferredWidth(li_colWidths[i]); //
		}

		//��ѡ��仯!
		table_temp.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					onDataTableSelectChanged(_type, "�б仯"); //
				}
			}
		});

		//��ѡ��仯!
		table_temp.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					onDataTableSelectChanged(_type, "�б仯"); //
				}
			}
		});

		//�������¼�
		//�����������¼�,���һ��,��������һ���ڲ���!!
		MouseAdapter tableMouseLn = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				Point point = e.getPoint(); //
				JTable table = (JTable) e.getSource();
				int li_row = table.rowAtPoint(point);//
				int li_column = table.columnAtPoint(point); //
				int li_selectedRowCout = table.getSelectedRowCount();
				int li_selectedColCout = table.getSelectedColumnCount(); //
				if (li_row >= 0 && li_column >= 0 && li_selectedRowCout <= 1 && li_selectedColCout <= 1) {
					table.setRowSelectionInterval(li_row, li_row);
					table.setColumnSelectionInterval(li_column, li_column); //
				}
				if ((e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) || e.getButton() == MouseEvent.BUTTON3) {
					onClickedTable(table, point, e.isControlDown(), e.isShiftDown()); //
				}
			}

			//����ڸ������ƹ���ʱ��
			public void mouseMoved(MouseEvent _event) {
				onMouseMoveFromTable((JTable) _event.getSource(), _event.getPoint()); //�ӱ���ϻ���!!
			}
		};
		table_temp.addMouseListener(tableMouseLn); //����!
		table_temp.addMouseMotionListener(tableMouseLn); //�ƶ�����!

		table_temp.putClientProperty("X_Type", _xType); //��X/Yά��ֱ�Ӵ��ڱ���������,Ȼ�����X/Yת��ʱ�ܻ��!!
		table_temp.putClientProperty("Y_Type", _yType); //
		table_temp.putClientProperty("Z_Type", _zLabel); //

		return table_temp; //
	}

	/**
	 * �������¼�!!
	 * @param source
	 * @param point
	 * @param controlDown
	 * @param shiftDown
	 */
	protected void onClickedTable(JTable _table, Point _point, boolean controlDown, boolean shiftDown) {
		if (this.str_builderClassName == null) {
			return;
		}
		try {
			int li_selRow = _table.getSelectedRow(); // ѡ�е���
			int li_selCol = _table.getSelectedColumn(); // ѡ�е���
			int li_rowCount = _table.getRowCount(); //�кϲ�!
			int li_colCount = _table.getColumnCount(); //�кϲ�!
			if (li_selRow < 0 || li_selCol < 0) {
				MessageBox.show(this, "��ѡ�����ݽ�����ȡ����!"); //
				return; //
			}
			if (li_selCol == 0 || li_selRow == li_rowCount - 1 || li_selCol == li_colCount - 1) { //�кϼ�
				return; //
			}
			BillChartItemVO cellItemVO = (BillChartItemVO) _table.getValueAt(li_selRow, li_selCol); //
			String str_ids = (String) cellItemVO.getCustProperty("#value"); //
			new ReportUIUtil().onDrillDetail(_table, str_ids, null, this.str_builderClassName, this.queryConditionMap, this.allCanGroupFields, this.str_drillActionClass, this.str_drillTempletCode); //
		} catch (Exception _ex) {
			MessageBox.showException(_table, _ex);
		}
	}

	/**
	 * ����ڱ���ϻ���!!
	 * @param point
	 */
	protected void onMouseMoveFromTable(JTable _table, Point _point) {
		_table.putClientProperty("MouseMovingRowCol", null); //���
		if (this.str_builderClassName == null) {
			return; //
		}
		int li_row = _table.rowAtPoint(_point); //�õ����������
		int li_col = _table.columnAtPoint(_point); //�õ����������
		int li_rowCount = _table.getRowCount(); //�кϲ�!
		int li_colCount = _table.getColumnCount(); //�кϲ�!
		if (li_row >= 0 && li_col >= 0) { ////
			boolean isHand = true; //�Ƿ�Ҫ����
			if (li_col == 0 || li_row == li_rowCount - 1 || li_col == li_colCount - 1) { //�кϼ�
				isHand = false; //
			}
			if (isHand) { //���������,�򽫹�����������!!!
				_table.putClientProperty("MouseMovingRowCol", li_row + "," + li_col); //
				_table.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
			} else {
				_table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
			}
		}
		_table.revalidate();
		_table.repaint();
	}

	/**
	 * ���ѡ��仯.. //Ĭ��ѡ�к�������Ҫ����仯����ʱ�ȿ��Ŵ˷���.����2014-5-7
	 * @param _type
	 */
	public void onDataTableSelectChanged(int _type, String _reason) {
		if (_type == 1) {
			generateBarChartFromTable1(_reason); //����ͼ�еı��ѡ��仯.
		} else if (_type == 2) {
			generateLineChartFromTable2(_reason); //����ͼ�б��ѡ�����仯.
		} else if (_type == 3) {
			generatePieChartFromTable3(_reason); //��ͼ�б��ѡ�����仯.
		}
	}

	/**
	 * 
	 * @param _title
	 * @param _month
	 * @param _value
	 * @param _prodtypes
	 * @param _months
	 * @param _data
	 * @return
	 */
	private JPanel getPanel_1(String _title, String _xType, String _yType, String _zLabel, String[] _prodtypes, String[] _months, BillChartItemVO[][] _data) {
		jTable_1 = getDataTable(1, _xType, _yType, _zLabel, _prodtypes, _months, _data); ////
		jTable_1.getTableHeader().setOpaque(false);

		JScrollPane scrollPanel = new JScrollPane(jTable_1); //

		JViewport jv2 = new JViewport();
		jv2.setOpaque(false); //һ��Ҫ��һ�£������ϱ����и�����
		jv2.setView(jTable_1.getTableHeader());
		scrollPanel.setColumnHeader(jv2); //
		scrollPanel.setOpaque(false);
		scrollPanel.getViewport().setOpaque(false); //

		barPanel.setLayout(new BorderLayout());

		JPanel tableContentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		tableContentPanel.add(scrollPanel); //

		splitPane_1 = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, barPanel, tableContentPanel); //
		splitPane_1.setDividerLocation(275); //
		splitPane_1.setDividerSize(10);
		splitPane_1.setOneTouchExpandable(true);

		JPanel panel_1 = new JPanel(new BorderLayout()); //
		panel_1.add(splitPane_1, BorderLayout.CENTER); //
		return panel_1; //
	}

	/**
	 * 
	 * @param _title
	 * @param _month
	 * @param _value
	 * @param _prodtypes
	 * @param _months
	 * @param _data
	 * @return
	 */
	private JPanel getPanel_2(String _title, String _xType, String _yType, String _zLabel, String[] _prodtypes, String[] _months, BillChartItemVO[][] _data) {
		jTable_2 = getDataTable(2, _xType, _yType, _zLabel, _prodtypes, _months, _data); ////
		jTable_2.getTableHeader().setOpaque(false);

		JScrollPane scrollPanel = new JScrollPane(jTable_2); //

		JViewport jv2 = new JViewport();
		jv2.setOpaque(false); //һ��Ҫ��һ�£������ϱ����и�����
		jv2.setView(jTable_2.getTableHeader());
		scrollPanel.setColumnHeader(jv2); //
		scrollPanel.setOpaque(false);
		scrollPanel.getViewport().setOpaque(false); //

		linePanel.setLayout(new BorderLayout());

		JPanel tableContentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		tableContentPanel.add(scrollPanel); //

		splitPane_2 = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, linePanel, tableContentPanel); //
		splitPane_2.setDividerLocation(275); //
		splitPane_2.setDividerSize(10);
		splitPane_2.setOneTouchExpandable(true);

		JPanel panel_2 = new JPanel(new BorderLayout()); //
		panel_2.add(splitPane_2, BorderLayout.CENTER); //
		return panel_2; //
	}

	/**
	 * 
	 * @param _title
	 * @param _month
	 * @param _value
	 * @param _prodtypes
	 * @param _months
	 * @param _data
	 * @return
	 */
	private JPanel getPanel_3(String _title, String _xType, String _yType, String _zLabel, String[] _prodtypes, String[] _months, BillChartItemVO[][] _data) {
		jTable_3 = getDataTable(3, _xType, _yType, _zLabel, _prodtypes, _months, _data); ////
		jTable_3.getTableHeader().setOpaque(false);

		JScrollPane scrollPanel = new JScrollPane(jTable_3); //
		JViewport jv2 = new JViewport();
		jv2.setOpaque(false); //һ��Ҫ��һ�£������ϱ����и�����
		jv2.setView(jTable_3.getTableHeader());
		scrollPanel.setColumnHeader(jv2); //
		scrollPanel.setOpaque(false);
		scrollPanel.getViewport().setOpaque(false); //

		piePanel.setLayout(new BorderLayout());

		JPanel tableContentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		tableContentPanel.add(scrollPanel); //

		splitPane_3 = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, piePanel, tableContentPanel); //
		splitPane_3.setDividerLocation(275); //
		splitPane_3.setDividerSize(10);
		splitPane_3.setOneTouchExpandable(true);

		JPanel panel_3 = new JPanel(new BorderLayout()); //
		panel_3.add(splitPane_3, BorderLayout.CENTER); //
		return panel_3; //
	}

	private void generateBarChartFromTable1(String _reason) {
		generateBarChartFromTable1(_reason, isBar3D); //
	}

	/**
	 * �ӱ��1��������ͼ...
	 */
	private void generateBarChartFromTable1(String _reason, boolean _is3D) {
		if (!isCanReDrawChart) {
			return;
		}
		int li_selectedRowCount = jTable_1.getSelectedRowCount(); //
		int li_selectedColCount = jTable_1.getSelectedColumnCount(); //
		String str_xType = (String) jTable_1.getClientProperty("X_Type"); //
		String str_yType = (String) jTable_1.getClientProperty("Y_Type"); //
		String str_zType = (String) jTable_1.getClientProperty("Z_Type"); //

		isBar3D = _is3D; //
		//System.out.println("��Ϊ[" + _reason + "]�ػ�����ͼ,ѡ����������[" + li_selectedRowCount + "," + li_selectedColCount + "]"); ////
		if (li_selectedRowCount == 0 || li_selectedColCount == 0) { //���һ��ûѡ,�����ʼ��,�����ѡ��ʱ��Ĭ�ϻ�������.
			String[] str_xLabels = getAllRow1Names(jTable_1);
			String[] str_yLabels = getAllHeadNames(jTable_1); // 
			double[][] ld_data = new double[str_xLabels.length][str_yLabels.length]; //
			for (int i = 0; i < ld_data.length; i++) {
				for (int j = 0; j < ld_data[i].length; j++) {
					BillChartItemVO chartItemVO = (BillChartItemVO) jTable_1.getValueAt(i, j + 1); //
					ld_data[i][j] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue());
				}
			}
			chart_bar = createBarChart(this.title, str_xType, str_zType, str_xLabels, str_yLabels, ld_data, isBar3D, null);
			chartPanel_bar = new ChartPanel(chart_bar); //
			//chartPanel_bar.setMouseWheelEnabled(true); //ͼ��ת�� �������� �����/2013-06-09��
			barPanel.removeAll();
			barPanel.setLayout(new BorderLayout()); //
			barPanel.add(chartPanel_bar); //
			barPanel.updateUI(); //
		} else { //����ʵ���������
			String[] str_xLabels = null; //
			String[] str_yLabels = null;
			double[][] ld_datas = null;
			if (jTable_1.getRowSelectionAllowed() && !jTable_1.getColumnSelectionAllowed()) { //��ѡ��
				str_xLabels = getXLabelsFromTable(jTable_1); //
				str_yLabels = getYLabelsFromTable(jTable_1); //
				ld_datas = getDoubleDataFromTable(jTable_1); //
			} else if (!jTable_1.getRowSelectionAllowed() && jTable_1.getColumnSelectionAllowed()) { //��ѡ��
				str_xLabels = getYLabelsFromTable(jTable_1); //
				str_yLabels = getXLabelsFromTable(jTable_1); //
				ld_datas = convertData(getDoubleDataFromTable(jTable_1)); //
			} else if (jTable_1.getRowSelectionAllowed() && jTable_1.getColumnSelectionAllowed()) { //����ѡ��
				str_xLabels = getXLabelsFromTable(jTable_1); //
				str_yLabels = getYLabelsFromTable(jTable_1); //
				ld_datas = getDoubleDataFromTable(jTable_1); //
			}

			if (ld_datas != null) {
				chart_bar = createBarChart(this.title, str_xType, str_zType, str_xLabels, str_yLabels, ld_datas, isBar3D, null);
				chartPanel_bar = new ChartPanel(chart_bar); //
				//chartPanel_bar.setMouseWheelEnabled(true); //ͼ��ת�� �������� �����/2013-06-09��
				barPanel.removeAll();
				barPanel.setLayout(new BorderLayout()); //
				barPanel.add(chartPanel_bar); //
				barPanel.updateUI(); //
			}
		}
	}

	/**
	 * �ӱ��1��������ͼ...
	 */
	public void generateBarChartFromTable11(int[] rownumbers, boolean _is3D) {
		if (!isCanReDrawChart) {
			return;
		}
		String str_xType = (String) jTable_1.getClientProperty("X_Type"); //
		String str_zType = (String) jTable_1.getClientProperty("Z_Type"); //

		isBar3D = _is3D; //
		String[] str_xLabels = null; //
		String[] str_yLabels = null;
		double[][] ld_datas = null;
		if (jTable_1.getRowSelectionAllowed() && !jTable_1.getColumnSelectionAllowed()) { //��ѡ��
			str_xLabels = getXLabelsFromTable(jTable_1); //
			str_yLabels = getYLabelsFromTable(jTable_1); //
			ld_datas = getDoubleDataFromTable(jTable_1); //
		} else if (!jTable_1.getRowSelectionAllowed() && jTable_1.getColumnSelectionAllowed()) { //��ѡ��
			str_xLabels = getYLabelsFromTable(jTable_1); //
			str_yLabels = getXLabelsFromTable(jTable_1); //
			ld_datas = convertData(getDoubleDataFromTable(jTable_1)); //
		} else if (jTable_1.getRowSelectionAllowed() && jTable_1.getColumnSelectionAllowed()) { //����ѡ��
			str_xLabels = getXLabelsFromTable(jTable_1); //
			str_yLabels = getYLabelsFromTable(jTable_1); //
			ld_datas = getDoubleDataFromTable(jTable_1); //
		}

		if (ld_datas != null) {
			chart_bar = createBarChart(this.title, str_xType, str_zType, str_xLabels, str_yLabels, ld_datas, isBar3D, null);
			chartPanel_bar = new ChartPanel(chart_bar); //
			//chartPanel_bar.setMouseWheelEnabled(true); //ͼ��ת�� �������� �����/2013-06-09��
			barPanel.removeAll();
			barPanel.setLayout(new BorderLayout()); //
			barPanel.add(chartPanel_bar); //
			barPanel.updateUI(); //
		}
	}

	//׷������ͼ3άЧ�� �����/2013-06-13��
	private void generateLineChartFromTable2(String _reason) {
		generateLineChartFromTable2(_reason, isLine3D); //
	}

	/**
	 * ����ͼѡ��仯ʱ..
	 */
	private void generateLineChartFromTable2(String _reason, boolean _is3D) {
		if (!isCanReDrawChart) {
			return;
		}
		int li_selectedRowCount = jTable_2.getSelectedRowCount(); //
		int li_selectedColCount = jTable_2.getSelectedColumnCount(); //
		String str_xType = (String) jTable_2.getClientProperty("X_Type"); //
		String str_yType = (String) jTable_2.getClientProperty("Y_Type"); //
		String str_zType = (String) jTable_2.getClientProperty("Z_Type"); //

		isLine3D = _is3D;
		//System.out.println("��Ϊ[" + _reason + "]�ػ�����ͼ,ѡ����������[" + li_selectedRowCount + "," + li_selectedColCount + "]"); ////
		if (li_selectedRowCount == 0 || li_selectedColCount == 0) { //���һ����ûѡ,��Ĭ��ʹ�õ�һ�����ݻ���
			String[] str_xLabels = getAllRow1Names(jTable_2);
			String[] str_yLabels = getAllHeadNames(jTable_2); // 
			double[][] ld_data = new double[str_xLabels.length][str_yLabels.length]; //
			for (int i = 0; i < ld_data.length; i++) {
				for (int j = 0; j < ld_data[i].length; j++) {
					BillChartItemVO chartItemVO = (BillChartItemVO) jTable_2.getValueAt(i, j + 1); //
					ld_data[i][j] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue()); //
				}
			}
			chart_line = createLineChart(this.title, str_xType, str_zType, str_xLabels, str_yLabels, ld_data, isLine3D, null);
			chartPanel_line = new ChartPanel(chart_line); //
			//chartPanel_line.setMouseWheelEnabled(true); //ͼ��ת�� �������� �����/2013-06-09��
			linePanel.removeAll();
			linePanel.setLayout(new BorderLayout()); //
			linePanel.add(chartPanel_line); //
			linePanel.updateUI(); //
		} else {
			String[] str_xLabels = null; //
			String[] str_yLabels = null;
			double[][] ld_datas = null;
			if (jTable_2.getRowSelectionAllowed() && !jTable_2.getColumnSelectionAllowed()) { //��ѡ��
				str_xLabels = getXLabelsFromTable(jTable_2); //
				str_yLabels = getYLabelsFromTable(jTable_2); //
				ld_datas = getDoubleDataFromTable(jTable_2); //
			} else if (!jTable_2.getRowSelectionAllowed() && jTable_2.getColumnSelectionAllowed()) { //��ѡ��
				str_xLabels = getYLabelsFromTable(jTable_2); //
				str_yLabels = getXLabelsFromTable(jTable_2); //
				ld_datas = convertData(getDoubleDataFromTable(jTable_2)); //
			} else if (jTable_2.getRowSelectionAllowed() && jTable_2.getColumnSelectionAllowed()) { //����ѡ��
				str_xLabels = getXLabelsFromTable(jTable_2); //
				str_yLabels = getYLabelsFromTable(jTable_2); //
				ld_datas = getDoubleDataFromTable(jTable_2); //
			}
			if (ld_datas != null) {
				chart_line = createLineChart(this.title, str_xType, str_zType, str_xLabels, str_yLabels, ld_datas, isLine3D, null);
				chartPanel_line = new ChartPanel(chart_line); //
				//chartPanel_line.setMouseWheelEnabled(true); //ͼ��ת�� �������� �����/2013-06-09��
				linePanel.removeAll();
				linePanel.setLayout(new BorderLayout()); //
				linePanel.add(chartPanel_line); //
				linePanel.updateUI(); //
			}
		}
	}

	private void generatePieChartFromTable3(String _reason) {
		generatePieChartFromTable3(_reason, isPie3D); //
	}

	/**
	 * �ӱ��3������ͼ���ػ�.
	 */
	private void generatePieChartFromTable3(String _reason, boolean _is3D) {
		if (!isCanReDrawChart) {
			return;
		}
		int li_selectedRowCount = jTable_3.getSelectedRowCount(); //
		int li_selectedColCount = jTable_3.getSelectedColumnCount(); //

		isPie3D = _is3D; //
		//System.out.println("��Ϊ[" + _reason + "]�ػ��ͼ,ѡ����������[" + li_selectedRowCount + "," + li_selectedColCount + "]"); ////
		if (li_selectedRowCount == 0 || li_selectedColCount == 0) { //���һ����ûѡ,��Ĭ��ʹ�õ�һ�����ݻ���
			String[] str_Labels = getAllHeadNames(jTable_1); // 
			double[] ld_data = new double[str_Labels.length];
			for (int i = 0; i < ld_data.length; i++) {
				BillChartItemVO chartItemVO = (BillChartItemVO) jTable_3.getValueAt(0, i + 1); //
				ld_data[i] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue());
			}

			this.chart_pie = createPieChart("ͼ��", str_Labels, ld_data, isPie3D ? 1 : 0, null);
			this.chartPanel_pie = new ChartPanel(chart_pie); //
			this.chartPanel_pie.setMouseWheelEnabled(true); //ͼ��ת�� �������� �����/2013-06-09��
			this.piePanel.removeAll();
			this.piePanel.setLayout(new BorderLayout()); //
			this.piePanel.add(chartPanel_pie); //
			this.piePanel.updateUI(); //
		} else { //�������ѡ���������
			String[] str_Labels = null; //
			double[] ld_datas = null;
			if (jTable_3.getRowSelectionAllowed() && !jTable_3.getColumnSelectionAllowed()) { //��ѡ��
				int li_selectedRow = jTable_3.getSelectedRow(); //
				if (li_selectedRow >= 0) {
					str_Labels = getYLabelsFromTable(jTable_3); //
					ld_datas = new double[str_Labels.length]; //
					for (int i = 0; i < ld_datas.length; i++) {
						BillChartItemVO chartItemVO = (BillChartItemVO) jTable_3.getValueAt(li_selectedRow, i + 1); //
						ld_datas[i] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue()); //
					}
				}
			} else if (!jTable_3.getRowSelectionAllowed() && jTable_3.getColumnSelectionAllowed()) { //��ѡ��
				int li_selectedCol = jTable_3.getSelectedColumn(); //
				if (li_selectedCol > 0) {
					str_Labels = getXLabelsFromTable(jTable_3); //
					ld_datas = new double[str_Labels.length]; //
					for (int i = 0; i < ld_datas.length; i++) {
						BillChartItemVO chartItemVO = (BillChartItemVO) jTable_3.getValueAt(i, li_selectedCol); //
						ld_datas[i] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue());
					}
				}
			} else if (jTable_3.getRowSelectionAllowed() && jTable_3.getColumnSelectionAllowed()) { //����ѡ��
				int[] li_allSelRows = jTable_3.getSelectedRows(); //����ѡ�е���
				int[] li_allSelCols = jTable_3.getSelectedColumns(); //����ѡ�е���
				if (li_allSelRows.length == 0 || li_allSelCols.length == 0) {
					return; //
				}
				if (li_allSelCols[0] == 0) {
					int[] li_newSelCols = new int[li_allSelCols.length - 1]; //
					System.arraycopy(li_allSelCols, 1, li_newSelCols, 0, li_newSelCols.length); //
					li_allSelCols = li_newSelCols; //
				}

				str_Labels = new String[li_allSelRows.length * li_allSelCols.length]; ////
				ld_datas = new double[li_allSelRows.length * li_allSelCols.length]; //
				int li_count = 0; //

				String str_rowlabel = null;
				String str_collabel = null;
				BillChartItemVO chartItemVO = null;
				for (int i = 0; i < li_allSelRows.length; i++) {
					for (int j = 0; j < li_allSelCols.length; j++) {
						str_rowlabel = "" + jTable_3.getValueAt(li_allSelRows[i], 0);
						str_collabel = jTable_3.getColumnName(li_allSelCols[j]);
						str_Labels[li_count] = str_rowlabel + "_" + str_collabel; ////
						chartItemVO = (BillChartItemVO) jTable_3.getValueAt(li_allSelRows[i], li_allSelCols[j]); //
						ld_datas[li_count] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue());
						li_count++;
					}
				}
			}

			if (ld_datas != null) {
				this.chart_pie = createPieChart("ͼ��", str_Labels, ld_datas, isPie3D ? 1 : 0, null);
				this.chartPanel_pie = new ChartPanel(chart_pie); //
				this.chartPanel_pie.setMouseWheelEnabled(true); //ͼ��ת�� �������� �����/2013-06-09��
				this.piePanel.removeAll();
				this.piePanel.setLayout(new BorderLayout()); //
				this.piePanel.add(chartPanel_pie); //
				this.piePanel.updateUI(); //
			}
		}
	}

	//ȡ��ͼ���һ���ϵ����ݣ�����ͼ���X����
	private String[] getXLabelsFromTable(JTable _table) {
		if (!_table.getRowSelectionAllowed() && _table.getColumnSelectionAllowed()) { //�����ֻ��ѡ����,ֻ����Ҫȥ�����һ��
			int li_allRows = _table.getRowCount(); //
			String[] str_items = new String[li_allRows - 1]; //
			for (int i = 0; i < str_items.length; i++) {
				str_items[i] = "" + _table.getValueAt(i, 0); //
			}
			return str_items; //
		} else {
			int[] li_rows = _table.getSelectedRows(); //
			String[] str_items = new String[li_rows.length]; ////
			for (int i = 0; i < str_items.length; i++) {
				str_items[i] = "" + _table.getValueAt(li_rows[i], 0); //
			}
			return str_items;
		}
	}

	//ȡ��ͼ���ͷ�ı�ǩ,����ͼ���Y����
	private String[] getYLabelsFromTable(JTable _table) {
		if (_table.getRowSelectionAllowed() && !_table.getColumnSelectionAllowed()) { //�����ֻ��ѡ����,��Ҫȥ����һ�������һ��
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
			int[] li_cols = _table.getSelectedColumns(); //ѡ�е���
			ArrayList al_cols = new ArrayList(); //
			for (int i = 0; i < li_cols.length; i++) {
				if (li_cols[i] != 0) { //��һ�в���
					al_cols.add(_table.getColumnName(li_cols[i])); //ѡ�е�����
				}
			}
			return (String[]) al_cols.toArray(new String[0]); //
		}
	}

	//�ӱ����ȡ��ͼ���ʵ�ʶ�ά����..
	private double[][] getDoubleDataFromTable(JTable _table) {
		if (_table.getRowSelectionAllowed() && !_table.getColumnSelectionAllowed()) { //ֻѡ����
			int[] li_rows = _table.getSelectedRows(); //��������
			int li_cols = _table.getColumnCount() - 2; //
			if (!isShowTotalColumn()) {
				li_cols = li_cols + 1;
			}
			double[][] ld_data = new double[li_rows.length][li_cols]; //
			for (int i = 0; i < li_rows.length; i++) {
				for (int j = 0; j < li_cols; j++) {
					BillChartItemVO chartItemVO = (BillChartItemVO) _table.getValueAt(li_rows[i], j + 1); //
					ld_data[i][j] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue()); ////
				}
			}
			return ld_data;
		} else if (!_table.getRowSelectionAllowed() && _table.getColumnSelectionAllowed()) { //ֻѡ����
			int li_rows = _table.getRowCount() - 1; //
			int[] li_selCols = _table.getSelectedColumns(); //
			if (li_selCols.length > 0 && li_selCols[0] == 0) {
				int[] li_selCols_new = new int[li_selCols.length - 1]; //
				System.arraycopy(li_selCols, 1, li_selCols_new, 0, li_selCols_new.length);
				li_selCols = li_selCols_new; //���¸�ֵ
			}
			double[][] ld_data = new double[li_rows][li_selCols.length]; //
			for (int i = 0; i < li_rows; i++) {
				for (int j = 0; j < li_selCols.length; j++) {
					BillChartItemVO chartItemVO = (BillChartItemVO) _table.getValueAt(i, li_selCols[j]); ////
					ld_data[i][j] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue());
				}
			}
			return ld_data; //
		} else if (_table.getRowSelectionAllowed() && _table.getColumnSelectionAllowed()) { //ͬʱѡ��������
			int[] li_rows = _table.getSelectedRows(); //��������
			int[] li_selCols = _table.getSelectedColumns(); //
			if (li_selCols.length > 0 && li_selCols[0] == 0) {
				int[] li_selCols_new = new int[li_selCols.length - 1]; //
				System.arraycopy(li_selCols, 1, li_selCols_new, 0, li_selCols_new.length);
				li_selCols = li_selCols_new; //���¸�ֵ
			}
			double[][] ld_data = new double[li_rows.length][li_selCols.length]; //
			for (int i = 0; i < li_rows.length; i++) {
				for (int j = 0; j < li_selCols.length; j++) {
					BillChartItemVO chartItemVO = (BillChartItemVO) _table.getValueAt(li_rows[i], li_selCols[j]); ////
					ld_data[i][j] = (chartItemVO == null ? 0 : chartItemVO.getScaleValue()); //
				}
			}
			return ld_data; //
		} else {
			return null;
		}
	}

	//��һ�������ȡ����������,����������BillChartItemVO
	public BillChartItemVO[][] getAllDataFromTable(JTable _table) {
		BillChartItemVO[][] chartItemVOs = new BillChartItemVO[_table.getRowCount() - 1][_table.getColumnCount() - 2]; //��Ҫȥ�����һ�е��кϼ�,��Ҫȥ����һ��˵�������һ�е��кϼ�
		for (int i = 0; i < chartItemVOs.length; i++) {
			for (int j = 0; j < chartItemVOs[i].length; j++) {
				chartItemVOs[i][j] = (BillChartItemVO) _table.getValueAt(i, j + 1); //
			}
		}
		return chartItemVOs;
	}

	private void distinctedArray(String[] _array) {
		for (int i = 0; i < _array.length; i++) {
			for (int j = 0; j < _array.length; j++) {
				if (i != j) {
					if (_array[j] != null && _array[j].equals(_array[i])) {
						_array[j] = _array[j] + j; //
					}
				}
			}
		}
	}

	private String addEmptyLine(String _str) {
		if (_str.length() <= 3) {
			return _str;
		} else {
			String str_return = ""; //
			int li_count = _str.length() / 3;
			for (int i = 0; i < li_count; i++) {
				str_return = str_return + _str.substring(i * 3, i * 3 + 3) + "\n"; //
			}
			if (_str.length() > li_count * 3) {
				str_return = str_return + _str.substring(li_count * 3, _str.length()); //
			}

			return str_return; //
		}
	}

	private String[] convertStrAddLine(String[] _items) {
		String[] str_return = new String[_items.length]; //
		for (int i = 0; i < str_return.length; i++) {
			str_return[i] = addEmptyLine(_items[i]); //
		}
		return str_return; //
	}

	/**
	 * ����ͼ
	 * 
	 * @param _title
	 * @param _title_x
	 * @param _title_y
	 * @param _serials_types
	 * @param _serials_x
	 * @param _data
	 * @return
	 */
	public JFreeChart createBarChart(String _title, String _title_x, String _title_y, String[] _serials_types, String[] _serials_x, double[][] _data, boolean _is3D, Color[] _colors) {
		JFreeChart temp_barChart = null;
		try {
			//jfreechart-1.0.14 ����Ҫ�ӻ��� �����/2013-06-09��
			//String[] str_serials_types_convert = convertStrAddLine(_serials_types); //�ӻ���,����ͼ���ֻ����һ��
			//String[] str_serials_x_convert = convertStrAddLine(_serials_x); //
			DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
			for (int i = 0; i < _serials_types.length; i++) {
				for (int j = 0; j < _serials_x.length; j++) {
					defaultcategorydataset.addValue(_data[i][j], _serials_types[i], _serials_x[j]);
				}
			}

			if (_is3D) {
				temp_barChart = ChartFactory.createBarChart3D(_title, _title_x, _title_y, defaultcategorydataset, PlotOrientation.VERTICAL, true, true, false);
			} else {
				temp_barChart = ChartFactory.createBarChart(_title, _title_x, _title_y, defaultcategorydataset, PlotOrientation.VERTICAL, true, true, false);
				CategoryPlot plot = temp_barChart.getCategoryPlot();
				NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
				rangeAxis.setUpperMargin(0.2);//Ԭ������2012-8-30����ӣ���������������������ߵľ��룬������������ʾ�����忴�������Խ����������ã�����������Ŀ��� 0.1��Щ�ط�����������_�ϲ�/sunfujun/20121208
				rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
				BarRenderer renderer = (BarRenderer) plot.getRenderer();
				renderer.setDrawBarOutline(false);
				int count = _serials_types.length;
				for (int i = 0; i < count; i++) {
					renderer.setSeriesPaint(i, new GradientPaint(0f, 0.0F, beautifulColors[(i) % 34], 0.0f, WIDTH, beautifulColors[(i) % 34].darker().darker()));
				}
				renderer.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.CENTER_HORIZONTAL));
				plot.setRenderer(renderer);
			}
			temp_barChart.setBackgroundPaint(Color.white); //
			if (defaultcategorydataset.getRowCount() == 1) { //���ֻ��һ������
				if (_is3D) {
					setChartAsCustRender3D(temp_barChart);
				} else {
					setChartAsCustRender(temp_barChart); //�Զ���Render
				}
			} else {
				CategoryPlot categoryplot = temp_barChart.getCategoryPlot();
				BarRenderer barrenderer = (BarRenderer) categoryplot.getRenderer();
				barrenderer.setBaseItemLabelsVisible(true); //
				barrenderer.setDrawBarOutline(false);
				barrenderer.setBaseItemLabelsVisible(true);
				barrenderer.setMaximumBarWidth(0.10000000000000001D);
				if (_colors != null) {
					for (int p = 0; p < _colors.length; p++) {
						if (_colors[p] != null) {
							barrenderer.setSeriesPaint(p, _colors[p]); //
						}
					}
				}
			}

			//׷������������ �����/2013-06-09��
			CategoryPlot categoryplot = temp_barChart.getCategoryPlot();
			CategoryItemRenderer localCategoryItemRenderer = categoryplot.getRenderer();
			localCategoryItemRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
			localCategoryItemRenderer.setSeriesItemLabelsVisible(0, Boolean.TRUE);

			if (_serials_x.length > 8) {
				//������б �����/2013-06-09��
				CategoryAxis domainAxis = categoryplot.getDomainAxis();
				domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
			}

			//���ñ���ɫ������ɫ �����/2013-06-09��
			categoryplot.setBackgroundPaint(Color.white);
			categoryplot.setRangeGridlinePaint(Color.lightGray);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return temp_barChart;
	}

	/**
	 * Ԭ���� 20140121 ���  ��Ҫ�������б����Զ������״ͼ��ʾ Ϊ�˽�ʡ�ռ䣬����ʡȥ��һЩ��״ͼ����
	 * @param _title
	 * @param _title_x
	 * @param _title_y
	 * @param _serials_types
	 * @param _serials_x
	 * @param isVertical
	 * @param _data
	 * @param _is3D
	 * @param _colors
	 * @return
	 */
	public JFreeChart createBarChart2(String _title, String _title_x, String _title_y, String[] _serials_types, String[] _serials_x, boolean isVertical, double[][] _data, boolean _is3D, Color[] _colors) {
		JFreeChart temp_barChart = null;
		_title = "";
		_title_x = "";
		_title_y = "";
		try {
			String[] str_serials_types_convert = convertStrAddLine(_serials_types); //�ӻ���,����ͼ���ֻ����һ��
			String[] str_serials_x_convert = convertStrAddLine(_serials_x); //
			DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
			for (int i = 0; i < str_serials_types_convert.length; i++) {
				for (int j = 0; j < str_serials_x_convert.length; j++) {
					defaultcategorydataset.addValue(_data[i][j], str_serials_types_convert[i], str_serials_x_convert[j]);
				}
			}
			if (_is3D) {
				if (isVertical) {
					temp_barChart = ChartFactory.createBarChart3D(_title, _title_x, _title_y, defaultcategorydataset, PlotOrientation.VERTICAL, false, false, false);
				} else {
					temp_barChart = ChartFactory.createBarChart3D(_title, _title_x, _title_y, defaultcategorydataset, PlotOrientation.HORIZONTAL, false, false, false);
				}
			} else {
				if (isVertical) {
					temp_barChart = ChartFactory.createBarChart(_title, _title_x, _title_y, defaultcategorydataset, PlotOrientation.VERTICAL, false, false, false);
				} else {
					temp_barChart = ChartFactory.createBarChart(_title, _title_x, _title_y, defaultcategorydataset, PlotOrientation.HORIZONTAL, false, false, false);
				}
				CategoryPlot categoryplot = temp_barChart.getCategoryPlot();
				categoryplot.setOutlinePaint(Color.WHITE);//���ñ߿�  �ޱ߿�
				categoryplot.setBackgroundPaint(Color.WHITE);//��������ʾ���ı���ɫ

				categoryplot.setAxisOffset(new RectangleInsets(-10, -10, 0, 0));//�˾�����ϣ���������ƫ��
				//categoryplot.setBackgroundPaint(Color.RED);  //��������������ɫ  
				//categoryplot.setDomainGridlinePaint(Color.pink);  //�������������ɫ  
				//categoryplot.setRangeGridlinePaint(Color.pink); 
				CategoryAxis domainAxis = categoryplot.getDomainAxis();
				domainAxis.setCategoryMargin(0.1);//
				Axis axis1 = categoryplot.getDomainAxis();
				axis1.setVisible(false);//���ú���������Ƿ���ʾ
				//Y�S�������w��С���ɫ 
				Axis axis2 = categoryplot.getRangeAxis(); //��������Ŀ̶��Ƿ���ʾ
				axis2.setVisible(false);//��������Ŀ̶��Ƿ���ʾ
				/* 
				 //��������Ŀ̶ȴ�С
				 NumberAxis numberAxis = (NumberAxis) categoryplot.getRangeAxis();
				 numberAxis.setVisible(true);
				 numberAxis.setAutoTickUnitSelection(false);
				 double unit=50d;//���ÿ̶ȵĳ���
				 NumberTickUnit ntu= new NumberTickUnit(unit);
				 numberAxis.setTickUnit(ntu);
				 // numberAxis.setTickMarksVisible(false);
				 numberAxis.setAutoTickUnitSelection(true); */
				CustomRenderer renderer = new CustomRenderer(_colors);
				//���������λ��
				ItemLabelPosition itemLabelPosition = new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 0);
				renderer.setPositiveItemLabelPosition(itemLabelPosition);
				renderer.setNegativeItemLabelPosition(itemLabelPosition);
				renderer.setMaximumBarWidth(0.2);
				renderer.setShadowVisible(false);//���������Ƿ�����Ӱ
				Font labelFont_value = new Font("SansSerif", Font.TRUETYPE_FONT, 50); //������״ͼ�ϵ�����
				renderer.setItemLabelFont(labelFont_value);

				renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
				renderer.setSeriesItemLabelsVisible(0, true);//������ֵ��ʾ
				renderer.setMaximumBarWidth(0.40000000000000001D);//�������������
				renderer.setBaseOutlinePaint(Color.BLACK); //�������ӵı߿�
				renderer.setDrawBarOutline(true); //������ʾ
				renderer.setMinimumBarLength(0.2);// �������Ӹ߶�  
				renderer.setBaseOutlinePaint(Color.BLACK);// �������ӱ߿���ɫ  
				renderer.setDrawBarOutline(true);// �������ӱ߿�ɼ�  
				renderer.setItemMargin(0.5);// ����ÿ��������������ƽ������֮�����  
				renderer.setIncludeBaseInRange(true);// ��ʾÿ��������ֵ�����޸ĸ���ֵ����������  
				NumberFormat n = new DecimalFormat("########.###");
				renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", n));//ȥ�������еĶ���
				renderer.setBaseItemLabelsVisible(true);
				categoryplot.setRenderer(renderer);
			}
			temp_barChart.setBackgroundPaint(Color.white); //
		} catch (ArrayIndexOutOfBoundsException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return temp_barChart;
	}

	//Ԭ���� 20140121 ��� ��Ҫ�����Զ������ɫ
	class CustomRenderer extends BarRenderer {
		private static final long serialVersionUID = 784630226449158436L;
		private Paint[] colors;
		Color[] colorValues = { Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.ORANGE, Color.PINK };

		public CustomRenderer(Color[] color) {
			if (color != null) {
				this.colorValues = color;
			}
			colors = new Paint[colorValues.length];
			for (int i = 0; i < colorValues.length; i++) {
				colors[i] = colorValues[i];
			}
		}

		//ÿ�������Գ�ʼ������ɫ������ѭ  
		public Paint getItemPaint(int i, int j) {
			return colors[j % colors.length];
		}
	}

	/**
	 * ����ͼ
	 * 
	 * @param _title
	 * @param _title_x
	 * @param _title_y
	 * @param _serials_types
	 * @param _serials_x
	 * @param _data
	 * @return
	 */
	public JFreeChart createLineChart(String _title, String _title_x, String _title_y, String[] _serials_types, String[] _serials_x, double[][] _data, boolean _is3D, Color[] _colors) {
		JFreeChart temp_barChart = null;
		try {
			//jfreechart-1.0.14 ����Ҫ�ӻ��� �����/2013-06-09��
			//String[] str_serials_types_convert = convertStrAddLine(_serials_types); //�ӻ���,����ͼ���ֻ����һ��
			//String[] str_serials_x_convert = convertStrAddLine(_serials_x); //
			DefaultCategoryDataset defaultcategorydataset = new DefaultCategoryDataset();
			for (int i = 0; i < _serials_types.length; i++) {
				for (int j = 0; j < _serials_x.length; j++) {
					defaultcategorydataset.addValue(_data[i][j], _serials_types[i], _serials_x[j]);
				}
			}

			if (_is3D) {
				temp_barChart = ChartFactory.createLineChart3D(_title, _title_x, _title_y, defaultcategorydataset, PlotOrientation.VERTICAL, true, true, false);
			} else {
				temp_barChart = ChartFactory.createLineChart(_title, _title_x, _title_y, defaultcategorydataset, PlotOrientation.VERTICAL, true, true, false);
			}
			//temp_barChart = ChartFactory.createLineChart(_title, _title_x, _title_y, defaultcategorydataset, PlotOrientation.VERTICAL, true, true, false);
			temp_barChart.setBackgroundPaint(Color.white);
			if (_serials_x.length <= 31) { // �����һ�����ڵ�����ʾ����,ʱ��������ճ��һ�����������
				/*			    StandardLegend standardlegend = (StandardLegend) temp_barChart.getLegend();
								standardlegend.setDisplaySeriesShapes(true);
								standardlegend.setShapeScaleX(1.5D);
								standardlegend.setShapeScaleY(1.5D);
								standardlegend.setDisplaySeriesLines(true);*/

				CategoryPlot categoryplot = temp_barChart.getCategoryPlot();
				NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
				numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
				numberaxis.setAutoRangeIncludesZero(true);
				numberaxis.setUpperMargin(0.20000000000000001D);
				numberaxis.setLabelAngle(1.5707963267948966D);
				LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer) categoryplot.getRenderer();
				lineandshaperenderer.setDrawOutlines(true);
				//���ֻ������ǰ3�����߷�ʽ 3������ȫ��ʵ�� ��ע�� ͳһ��ʵ�� �����/2013-06-09��
				/*				lineandshaperenderer.setSeriesStroke(0, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 10F, 6F }, 0.0F));
								lineandshaperenderer.setSeriesStroke(1, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 6F, 6F }, 0.0F));
								lineandshaperenderer.setSeriesStroke(2, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 2.0F, 6F }, 0.0F));*/
				lineandshaperenderer.setBaseItemLabelsVisible(true);
				lineandshaperenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition());
				lineandshaperenderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition());

				if (_colors != null) {
					for (int p = 0; p < _colors.length; p++) {
						if (_colors[p] != null) {
							lineandshaperenderer.setSeriesPaint(p, _colors[p]);
						}
					}
				} else {
					if (defaultcategorydataset.getRowCount() == 1) {
						lineandshaperenderer.setSeriesPaint(0, getRandomColor());
					}
				}
			}

			CategoryPlot categoryplot = temp_barChart.getCategoryPlot();
			LineAndShapeRenderer localLineAndShapeRenderer = (LineAndShapeRenderer) categoryplot.getRenderer();
			for (int i = 0; i < _serials_types.length; i++) {
				//׷�����ϵ���״ �����/2013-06-09��
				localLineAndShapeRenderer.setSeriesShapesVisible(i, true);
			}

			//׷�ӵ������� �����/2013-06-09��
			localLineAndShapeRenderer.setBaseShapesVisible(true);
			localLineAndShapeRenderer.setBaseItemLabelsVisible(true);
			localLineAndShapeRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());

			if (_serials_x.length > 8) {
				//������б �����/2013-06-09��
				CategoryAxis domainAxis = categoryplot.getDomainAxis();
				domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
			}

			//���ñ���ɫ������ɫ �����/2013-06-09�� 
			categoryplot.setBackgroundPaint(Color.white);
			categoryplot.setRangeGridlinePaint(Color.lightGray);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return temp_barChart;
	}

	/**
	 * ������ͼ
	 * 
	 * @param _title
	 * @param _serials
	 * @param _datas
	 * @return
	 */
	public JFreeChart createPieChart(String _title, String[] _serials, double[] _datas, int type, String _selected) {// type=0Ϊ2ά.����Ϊ3ά
		//jfreechart-1.0.14 ����Ҫ�ӻ��� �����/2013-06-09��
		//String[] str_serials_x_convert = convertStrAddLine(_serials); //
		DefaultPieDataset dataset = new DefaultPieDataset();
		for (int i = 0; i < _serials.length; i++) {
			dataset.setValue(_serials[i], new Double(_datas[i])); //
		}
		if (type == 0) {
			//chart_pie = myCreatePieChart(_title, dataset, true, true, false);
			chart_pie = ChartFactory.createPieChart(_title, dataset, true, true, false);
		} else {
			chart_pie = ChartFactory.createPieChart3D(_title, (PieDataset) dataset, true, true, false);
		}
		chart_pie.setBackgroundPaint(Color.WHITE);
		PiePlot plot = (PiePlot) chart_pie.getPlot();
		plot.setLabelPaint(PiePlot.DEFAULT_LABEL_OUTLINE_PAINT);
		plot.setNoDataMessage("No data available");
		//plot.setLabelGenerator((PieSectionLabelGenerator) new StandardCategoryItemLabelGenerator()); //
		//׷�Ӱٷֱ� �����/2013-06-09��
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({2})"));
		if (_selected != null && _selected.equals("column") && this.serialsColors != null) {
			for (int p = 0; p < serialsColors.length; p++) { //
				if (serialsColors[p] != null) {
					plot.setSectionPaint(p, new GradientPaint(0.0f, 0.0F, beautifulColors[(p) % 34], 0.0f, WIDTH, beautifulColors[(p) % 34].darker().darker()));
				}
			}
		}

		//���ñ���ɫ �����/2013-06-09��
		plot.setBackgroundPaint(Color.white);

		return chart_pie;
	}

	//jfreechart-1.0.14 ����ת��,����Ҫ�ع���ͼ����Ⱦ�� �����/2013-06-09��
	/*	private JFreeChart myCreatePieChart(String title, PieDataset data, boolean legend, boolean tooltips, boolean urls) {
			PiePlot plot = new WLTPiePlot(data); //�ع���ͼ����Ⱦ��,�����Ŀ̫��ʱ��������ص���һ�������!!
			plot.setInsets(new Insets(0, 5, 5, 5));
			PieItemLabelGenerator labelGenerator = null;
			if (tooltips) {
				labelGenerator = new StandardPieItemLabelGenerator(); //
			}
			PieURLGenerator urlGenerator = null;
			if (urls) {
				urlGenerator = new StandardPieURLGenerator(); //
			}
			plot.setItemLabelGenerator(labelGenerator); //
			plot.setURLGenerator(urlGenerator); //
			JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, plot, legend);
			return chart; //
		}*/

	/**
	 * �Զ�������ɫ.Render..
	 * @param _chart
	 */
	private void setChartAsCustRender(JFreeChart _chart) {
		PlotOrientation orientation = _chart.getCategoryPlot().getOrientation();
		int li_colcount = _chart.getCategoryPlot().getDataset().getColumnCount(); //
		BarRenderer barRender = new CustomBarRenderer(getAllColumnColors(li_colcount)); //
		barRender.setSeriesPaint(0, new GradientPaint(0.0f, 0.0F, beautifulColors[(0) % 34], 0.0f, WIDTH, beautifulColors[(0) % 34].darker().darker()));
		if (orientation == PlotOrientation.HORIZONTAL) {
			ItemLabelPosition position1 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE3, TextAnchor.CENTER_LEFT, TextAnchor.CENTER, 0.0);
			barRender.setBasePositiveItemLabelPosition(position1);
			ItemLabelPosition position2 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE9, TextAnchor.CENTER_RIGHT, TextAnchor.CENTER, 0.0);
			barRender.setBaseNegativeItemLabelPosition(position2);
		} else if (orientation == PlotOrientation.VERTICAL) {
			ItemLabelPosition position1 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER, TextAnchor.CENTER, 0.0);
			barRender.setBasePositiveItemLabelPosition(position1);
			ItemLabelPosition position2 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER, TextAnchor.CENTER, 0.0);
			barRender.setBaseNegativeItemLabelPosition(position2);
		}
		Shape shape = new RoundRectangle2D.Float(1, 1, 10, 20, 12, 12);
		barRender.setBaseShape(shape, true);
		barRender.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator()); //
		barRender.setBaseItemLabelsVisible(true); //
		barRender.setBaseItemLabelsVisible(true); //
		barRender.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.CENTER_HORIZONTAL));
		barRender.setMaximumBarWidth(0.08000000000000001D);
		_chart.getCategoryPlot().setRenderer(barRender); //

	}

	//������ά����ͼ�Ļ�����
	private void setChartAsCustRender3D(JFreeChart _chart) {
		int li_colcount = _chart.getCategoryPlot().getDataset().getColumnCount(); //
		BarRenderer3D barRender = new CustomBarRenderer3D(getAllColumnColors(li_colcount)); //
		barRender.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		barRender.setBaseItemLabelsVisible(true); //
		barRender.setBaseItemLabelsVisible(true); //
		barRender.setMaximumBarWidth(0.03000000000000001D); //
		_chart.getCategoryPlot().setRenderer(barRender); //
	}

	//ȡ�����е���ɫ
	private Color[] getAllColumnColors(int _colCount) {
		if (_colCount < beautifulColors.length) { //����Ѷ���ĺÿ�����ɫ�ѹ���,��ֱ�ӷ���
			for (int i = 0; i < beautifulColors.length; i++) {
				beautifulColors[i] = new Color(beautifulColors[i].getRed(), beautifulColors[i].getGreen(), beautifulColors[i].getBlue(), 128);
			}
			return beautifulColors;
		} else {
			int li_added = _colCount - beautifulColors.length; //Ԥ������ɫ����,��Ҫ�Ӽ���
			Color[] addedColors = new Color[li_added]; //
			Random ranDom = new Random(); //
			for (int i = 0; i < addedColors.length; i++) {
				int li_r = ranDom.nextInt(254);
				int li_g = ranDom.nextInt(254);
				int li_b = ranDom.nextInt(254);
				addedColors[i] = new Color(li_r, li_g, li_b); //
			}
			Color[] returnColors = new Color[beautifulColors.length + li_added]; //
			System.arraycopy(beautifulColors, 0, returnColors, 0, beautifulColors.length); //
			System.arraycopy(addedColors, 0, returnColors, beautifulColors.length, addedColors.length); //
			return returnColors;
		}

	}

	private Color getRandomColor() {
		return beautifulColors[new Random().nextInt(beautifulColors.length)]; //
	}

	/**
	 * ��������ķ���
	 * 
	 * @param _title
	 * @param _xtitles
	 * @param _ytitles
	 * @param _table
	 * @param _isconvert
	 * @return
	 */
	public static BillChartPanel getChartPanelFromTable(String _title, String _xtitles, String _ytitles, JTable _table, boolean _isconvert, VectorMap _sortcolor) {
		// ���������ı� ���� ���� ������
		int li_rowcount = _table.getRowCount(); //
		int colcount = _table.getColumnCount(); //
		String[] str_months = new String[colcount - 1]; //
		String[] str_proctypes = new String[li_rowcount]; //

		for (int i = 0; i < str_months.length; i++) {
			str_months[i] = _table.getColumnModel().getColumn(i + 1).getHeaderValue().toString(); //
		}

		for (int i = 0; i < str_proctypes.length; i++) {
			str_proctypes[i] = "" + _table.getValueAt(i, 0);
		}
		double[][] ld_data = new double[li_rowcount][colcount - 1]; //
		for (int i = 0; i < ld_data.length; i++) {
			for (int j = 0; j < ld_data[i].length; j++) {
				String str_value = "" + _table.getValueAt(i, j + 1); //
				if (str_value.equals("") || str_value.equals("null")) {
					ld_data[i][j] = 0; //
				} else {
					ld_data[i][j] = Double.parseDouble(str_value); //
				}
			}
		}

		BillChartPanel chartPanel = null; //

		String[] tmp_products = null; //
		String[] tmp_months = null;
		double[][] tmp_data = null; //
		if (!_isconvert) { // �����ת��X/Y
			tmp_products = str_proctypes; //
			tmp_months = str_months; //
			tmp_data = ld_data; //
		} else {
			tmp_products = str_months; //
			tmp_months = str_proctypes; //
			tmp_data = convertData(ld_data); //
		}

		String[] str_newproctypes = null;
		double[][] ld_newdata = null; //
		Color[] newColor = null;
		if (_sortcolor != null) {
			// tmp_products = {"�ͷ���","��С����","�еȷ���","�߷���"}
			// "�߷���", "�еȷ���", "�ͷ���", "��С����"
			// new String[] { "�߷���", "�еȷ���", "�ͷ���", "��С����" }); //
			String[] sortedString = _sortcolor.getKeysAsString(); //
			int[] li_newindex = getResortIndex(tmp_products, sortedString);
			str_newproctypes = new String[tmp_products.length]; //
			ld_newdata = new double[tmp_data.length][tmp_data[0].length]; //
			for (int i = 0; i < li_newindex.length; i++) {
				str_newproctypes[li_newindex[i]] = tmp_products[i]; //
				ld_newdata[li_newindex[i]] = tmp_data[i]; //
			}

			newColor = new Color[str_newproctypes.length];
			for (int i = 0; i < str_newproctypes.length; i++) {
				newColor[i] = (Color) _sortcolor.get(str_newproctypes[i]); //
			}
		} else {
			str_newproctypes = tmp_products; //
			ld_newdata = tmp_data; //
		}
		chartPanel = new BillChartPanel(_title, _xtitles, _ytitles, str_newproctypes, tmp_months, ld_newdata, newColor); //
		return chartPanel; //
	}

	private static int[] getResortIndex(String[] _truedata, String[] _sortdata) {
		String[] exitsArr = new String[_sortdata.length];

		Vector notExitsArr = new Vector();

		for (int i = 0; i < _truedata.length; i++) {
			for (int j = 0; j < _sortdata.length; j++) {
				if (_truedata[i].equals(_sortdata[j])) {
					exitsArr[j] = _truedata[i];
					break;
				} else {
					if (j == _sortdata.length - 1) {
						notExitsArr.add(_truedata[i]);
					}
				}
			}
		}

		Vector finals = new Vector();
		for (int i = 0; i < exitsArr.length; i++) {
			if (exitsArr[i] != null) {
				finals.add(exitsArr[i]);
			}
		}

		for (int i = 0; i < notExitsArr.size(); i++) {
			if (notExitsArr.get(i) != null) {
				finals.add(notExitsArr.get(i));
			}
		}

		int[] re_int = new int[_truedata.length];
		for (int i = 0; i < _truedata.length; i++) {
			for (int j = 0; j < finals.size(); j++) {
				if (finals.get(j) != null) {
					String s = (String) finals.get(j);
					if (_truedata[i].equals(s)) {
						re_int[i] = j;
					}
				}
			}
		}
		return re_int;
	}

	public static BillChartPanel getChartPanelBySQL(String _title, String _xtitles, String _ytitles, String _sql, boolean _isconvert) {
		return getChartPanelBySQL(_title, _xtitles, _ytitles, _sql, _isconvert, null); //
	}

	public static BillChartPanel getChartPanelBySQL(String _title, String _xtitles, String _ytitles, String _sql, boolean _isconvert, VectorMap vm) {
		try {
			HashVO[] hvs = UIUtil.getHashVoArrayByDS(null, _sql);
			if (hvs.length <= 0) {
				JTable atable = new JTable(new String[][] { { "����Ӧ���", "0" } }, new String[] { "", "û������" }); //
				return getChartPanelFromTable("û������", "û������", "û������", atable, false, vm); //
			}
			HashMap rows = new HashMap(); //
			HashMap columns = new HashMap(); //

			Hashtable ht_datas = new Hashtable(); //
			for (int i = 0; i < hvs.length; i++) {
				rows.put(hvs[i].getStringValue(0), hvs[i].getStringValue(0)); //
				columns.put(String.valueOf(hvs[i].getStringValue(1)), hvs[i].getStringValue(1)); //
				ht_datas.put(hvs[i].getStringValue(0) + "_" + hvs[i].getStringValue(1), hvs[i].getStringValue(2)); //
			}

			String[] rowheaders = (String[]) rows.keySet().toArray(new String[0]); //
			String[] columnnames = (String[]) columns.keySet().toArray(new String[0]); //

			String[] columnnames_2 = new String[columnnames.length + 1]; //
			columnnames_2[0] = ""; //
			System.arraycopy(columnnames, 0, columnnames_2, 1, columnnames.length); //

			String[][] ld_data = new String[rowheaders.length][columnnames.length + 1]; //
			for (int i = 0; i < ld_data.length; i++) {
				ld_data[i][0] = rowheaders[i];
				for (int j = 0; j < columnnames.length; j++) {
					Object obj = ht_datas.get(rowheaders[i] + "_" + columnnames[j]); //
					if (obj != null) {
						ld_data[i][j + 1] = "" + obj; //
					}
				}
			}

			JTable table = new JTable(ld_data, columnnames_2); //
			return getChartPanelFromTable(_title, _xtitles, _ytitles, table, _isconvert, vm); //
		} catch (Exception e) {
			e.printStackTrace();
		} //

		return null;
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

	private String[][] convertStrArray(String[][] _oldData) {
		String[][] returndata = new String[_oldData[0].length][_oldData.length];
		for (int i = 0; i < _oldData.length; i++) {
			for (int j = 0; j < _oldData[i].length; j++) {
				returndata[j][i] = _oldData[i][j];
			}
		}
		return returndata;
	}

	/**
	 * ����Html,ͼƬ��������һ��Ŀ¼!!
	 */
	public void exportHTML() {
		try {
			String str_imgName = System.currentTimeMillis() + ".jpg";
			String str_text = getHtmlContentByString(str_imgName).toString(); //
			String str_fileName = UIUtil.saveFile(this, "html", "ͼ������", str_text.getBytes("GBK")); //
			if (str_fileName != null) { //
				str_fileName = TBUtil.getTBUtil().replaceAll(str_fileName, "\\", "/");
				String str_path = str_fileName.substring(0, str_fileName.lastIndexOf("/")); //
				str_path = str_path + "/����html����ͼƬ";
				File fileimagePath = new File(str_path);
				if (!fileimagePath.exists()) {
					fileimagePath.mkdirs(); //
				}
				JPanel pict_panel = null;
				int style = this.getChartTypeRadioSelected();
				if (1 == style) {
					pict_panel = this.barPanel;
				} else if (2 == style) {
					pict_panel = this.linePanel;
				} else if (3 == style) {
					pict_panel = this.piePanel;
				}

				byte[] bytes = TBUtil.getTBUtil().getCompentBytes(pict_panel); //
				FileOutputStream fout = new FileOutputStream(str_path + "/" + str_imgName);
				fout.write(bytes); //
				fout.close(); //
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
	private StringBuffer getHtmlContentByString(String _imgName) {
		StringBuffer returnSb = new StringBuffer();
		returnSb.append("<html><head><title>��������</title>\r\n");
		returnSb.append("<style type=\"text/css\">\r\n");
		returnSb.append(".style_1 {\r\n");
		returnSb.append(" font-size: 12px; color: #333333; font-family: ����\r\n");
		returnSb.append("}\r\n");
		returnSb.append("</style>\r\n");
		returnSb.append("</head><body>\r\n");

		JTable table = null;
		int style = this.getChartTypeRadioSelected();
		if (1 == style) {
			table = jTable_1;
		} else if (2 == style) {
			table = jTable_2;
		} else if (3 == style) {
			table = jTable_3;
		}
		returnSb.append("<img  src=\"./����html����ͼƬ/").append(_imgName).append("\" alt=\"").append("����ͼƬ").append("\" />\r\n");
		returnSb.append("<table align=\"left\" style=\" margin-left: 0px; margin-right: 0px; margin-top: 3px; margin-bottom: 8px\" cellSpacing=\"1\" cellPadding=\"5\" bgColor=\"#999999\" border=\"0\" bordercolor=\"#CCCCCC\">\r\n");
		//�õ�table�����ݣ�������ͷ��Ϣ
		String[][] datas = this.getChartTableDatas(table);
		Color co = null;
		TBUtil tbutil = new TBUtil();
		int width = 20;
		for (int i = 0; i < datas.length; i++) {
			returnSb.append("<tr height=\"").append(table.getRowHeight()).append("\">\r\n");
			for (int j = 0; j < datas[i].length; j++) {
				if (i != 0) {
					co = table.getCellRenderer(i - 1, j).getTableCellRendererComponent(table, null, false, false, i - 1, j).getBackground();
				} else {
					co = table.getCellRenderer(i, j).getTableCellRendererComponent(table, null, false, false, i, j).getBackground();
				}
				width = table.getColumnModel().getColumn(j).getWidth();
				StringBuffer bgcolor = new StringBuffer();
				bgcolor.append("#");
				bgcolor.append(tbutil.getHexString(co.getRed()));
				bgcolor.append(tbutil.getHexString(co.getGreen()));
				bgcolor.append(tbutil.getHexString(co.getBlue()));
				returnSb.append("<td  bgcolor=\"").append(bgcolor).append("\" class=\"style_1\" >").append(datas[i][j]).append("</td>\r\n");
				//				returnSb.append("<td width=\""+width+"\""+" bgcolor=\"").append(bgcolor).append("\" class=\"style_1\" >").append(datas[i][j]).append("</td>\r\n");
			}
			returnSb.append("</tr>\r\n");
		}
		returnSb.append("</body>\r\n</html>");
		return returnSb;
	}

	/**
	 * ����excel  ������2012-5-24��
	 * wp
	 */
	public void exportExcel() {
		try {
			UIUtil.saveFile(this, "xls", "��������", createExcelFileData()); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//��������excel�ļ�  ������2012-5-24��
	private byte[] createExcelFileData() throws Exception {
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = null;
		HSSFRow row = null;
		HSSFCell cell = null;

		HSSFCellStyle cellStyle = wb.createCellStyle();
		cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//����
		sheet = wb.createSheet("��������");
		HSSFPatriarch pat = sheet.createDrawingPatriarch();

		//����excel��ͼƬ
		byte[] image = null;

		int chartype = this.getChartTypeRadioSelected();
		JTable table = null;
		if (chartype == 1) {
			table = jTable_1;
			image = this.getChartImageAsBtyeArray(barPanel, true);

		} else if (chartype == 2) {
			table = jTable_2;
			image = this.getChartImageAsBtyeArray(linePanel, true);
		} else if (chartype == 3) {
			table = jTable_3;
			image = this.getChartImageAsBtyeArray(this.piePanel, true);
		}
		//����excel������
		String[][] datas = this.getChartTableDatas(table);

		//����ͼƬ
		HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 255, 255, (short) 0, 0, (short) 15, 15);//ǰ�ĸ������в���ȷ�����塢����������ֵͼƬ���Ͻ��ڵڼ��еڼ��У������������ֵͼƬ�߶�Ϊ���м���
		pat.createPicture(anchor, wb.addPicture(image, HSSFWorkbook.PICTURE_TYPE_JPEG));

		//����������
		for (int i = 0; i < datas.length; i++) {
			row = sheet.createRow(i + 20);
			for (int j = 0; j < datas[i].length; j++) {
				if (i == 0) {
					//					sheet.setColumnWidth(j,table.getColumnModel().getColumn(j).getWidth());//�����п�ò�Ʋ���
				}
				cell = row.createCell(j);
				cell.setCellStyle(cellStyle);//���ñ߿�
				cell.setCellValue(datas[i][j]);
			}
		}

		ByteArrayOutputStream bout = new ByteArrayOutputStream(); //
		wb.write(bout);
		byte[] bytes = bout.toByteArray(); //
		bout.close();
		return bytes; //
	}

	//���ֽ��������ʽ�õ�Component��ͼƬ  isJpg,�Ƿ�ĵ�jpg��ʽ��ͼƬ������õ�bmp��ʽ��ͼƬ  ������2012-5-24��
	private byte[] getChartImageAsBtyeArray(Component comp, boolean isJpg) throws Exception {
		Rectangle rect = comp.getBounds();
		return TBUtil.getTBUtil().getCompentBytes(comp, rect.width, rect.height, !isJpg); //��������С��ȡ��ͼƬ[����2012-11-27]
	}

	//�õ�ͼ������·��ı�������,������ͷ����   ������2012-5-24��
	private String[][] getChartTableDatas(JTable jtable) {
		int rowcount = jtable.getRowCount() + 1;
		int columnCount = jtable.getColumnCount();
		String[][] datas = new String[rowcount][columnCount];

		for (int i = 0; i < rowcount; i++) {
			for (int j = 0; j < columnCount; j++) {
				if (i == 0) {
					datas[i][j] = jtable.getColumnName(j);
				} else {
					if (jtable.getValueAt(i - 1, j) != null)
						datas[i][j] = jtable.getValueAt(i - 1, j).toString();
					else
						datas[i][j] = "";
				}
			}
		}

		return datas;
	}

	/**
	 * ����Word�ļ�!!
	 */
	public void exportWordFile() {
		try {
			try {
				int li_type = getChartTypeRadioSelected();
				String str_xml = null; //
				if (li_type == 1) {
					str_xml = getWordXmlForSumchart(1, "��״ͼ��"); //
				} else if (li_type == 2) {
					str_xml = getWordXmlForSumchart(2, "����ͼ��"); //
				} else if (li_type == 3) {
					str_xml = getWordXmlForSumchart(3, "��״ͼ��"); //
				}
				UIUtil.saveFile(this, "doc", "��������", str_xml.getBytes("GBK")); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * ��ͼ��ת����Word��XML��ʽ����..
	 * 
	 * @param _type
	 * @return
	 */
	public String getWordXML(String _title, int _type) {
		if (_type == 1) {
			return getWord(_title, jTable_1, chart_bar);
		} else if (_type == 2) {
			return getWord(_title, jTable_2, chart_line);
		} else if (_type == 3) {
			return getWord(_title, jTable_3, chart_pie);
		}
		return null;
	}

	public String getWordXmlForSumchart(int _type, String _title) {
		return getWordXmlForSumchart(new int[] { _type }, _title); //
	}

	public String getWordXmlForSumchart(int[] _types, String _title) {
		return getWordXmlForSumchart(_types, _title, null); //
	}

	public String getWordXmlForSumchart(int[] _types, String _title, String _type) {
		WordFileUtil wordFileUtil = new WordFileUtil();
		StringBuffer sb_text = new StringBuffer(); //
		sb_text.append(wordFileUtil.getWordFileBegin()); //
		if (_title != null) {
			sb_text.append(wordFileUtil.getWordTitle(_title)); //
		}
		if (_type == null) {
			_type = "3";
		}

		if ("1".equals(_type) || "3".equals(_type)) {
			for (int i = 0; i < _types.length; i++) {
				JFreeChart chart_1 = createOneColumnOrRowChart(_types[i], 1); //
				sb_text.append(wordFileUtil.getImageXml(imageToBaseCode(chart_1), 400, 200, false)); //
				sb_text.append(wordFileUtil.getWordTitle("")); //
			}
		}
		if ("2".equals(_type) || "3".equals(_type)) {
			for (int i = 0; i < _types.length; i++) {
				JFreeChart chart_2 = createOneColumnOrRowChart(_types[i], 2); //
				sb_text.append(wordFileUtil.getImageXml(imageToBaseCode(chart_2), 400, 200, false)); //
				sb_text.append(wordFileUtil.getWordTitle("")); //
			}
		}
		String[][] tableData = getTableToArray(jTable_1);
		if (tableData != null && tableData.length > 0 && tableData[0] != null && tableData[0].length > 0) {
			//ȡ����һ�е�һ��
			String strTitle = tableData[0][0];
			sb_text.append(getTableTitle(strTitle));
		}
		sb_text.append(getTableWordXML2(tableData)); ////
		sb_text.append(wordFileUtil.getWordFileEnd(true)); //
		return sb_text.toString();
	}

	public String getWordXmlForSumchartOnlyContent(int _type, String _title) {
		return getWordXmlForSumchartOnlyContent(new int[] { _type }, _title, null); //
	}

	public String getWordXmlForSumchartOnlyContent(int[] _types, String _title, String _type) {
		WordFileUtil wordFileUtil = new WordFileUtil(); //
		StringBuffer sb_text = new StringBuffer(); //
		if (_title != null) {
			sb_text.append(wordFileUtil.getWordTitle(_title)); //
		}
		if (_type == null) {
			_type = "3";
		}
		if ("1".equals(_type) || "3".equals(_type)) {
			for (int i = 0; i < _types.length; i++) {
				JFreeChart chart_1 = createOneColumnOrRowChart(_types[i], 1); //
				sb_text.append(wordFileUtil.getImageXml(imageToBaseCode(chart_1), 400, 200, false)); //
				sb_text.append(wordFileUtil.getWordTitle("")); //
			}
		}
		if ("2".equals(_type) || "3".equals(_type)) {
			for (int i = 0; i < _types.length; i++) {
				JFreeChart chart_2 = createOneColumnOrRowChart(_types[i], 2); //
				sb_text.append(wordFileUtil.getImageXml(imageToBaseCode(chart_2), 400, 200, false)); //
				sb_text.append(wordFileUtil.getWordTitle("")); //
			}
		}
		String[][] tableData = getTableToArray(jTable_1);
		if (tableData != null && tableData.length > 0 && tableData[0] != null && tableData[0].length > 0) {
			//ȡ����һ�е�һ��
			String strTitle = tableData[0][0];
			sb_text.append(getTableTitle(strTitle));
		}

		sb_text.append(getTableWordXML2(tableData)); //
		sb_text.append(wordFileUtil.getWordTitle("")); //
		return sb_text.toString();
	}

	/**
	 * �õ�word�ļ�
	 * 
	 * @param jTable
	 *            ���ݱ��
	 * @param jFreeChart
	 * @return
	 */
	private String getWord(String _title, JTable jTable, JFreeChart jFreeChart) {
		WordFileUtil wordFileUtil = new WordFileUtil();
		String result = null; //
		String[][] inputData = getTableToArray(jTable);
		String str_wordtitle = "";
		if (_title != null) {
			str_wordtitle = wordFileUtil.getWordTitle(_title);
		}
		result = wordFileUtil.getWordFileBegin() + str_wordtitle + wordFileUtil.getImageXml(imageToBaseCode(jFreeChart), 400, 200, false) + getTableWordXML(inputData) + wordFileUtil.getWordFileEnd();
		return result;
	}

	/**
	 * ���������ת��Ϊ��ά����
	 * 
	 * @param jTable
	 * @return String
	 */
	private String[][] getTableToArray(JTable jTable) {
		int li_colcount = jTable.getColumnCount(); //
		String[][] outputData = new String[jTable.getRowCount() + 1][li_colcount];
		for (int i = 0; i < li_colcount; i++) {
			outputData[0][i] = jTable.getColumnName(i); //
		}

		for (int i = 1; i < outputData.length; i++) {
			for (int j = 0; j < outputData[i].length; j++) {
				outputData[i][j] = "" + jTable.getValueAt(i - 1, j); ////
			}
		}
		return outputData;
	}

	/**
	 * �õ��������
	 * 
	 * @param inputData
	 * @return
	 */
	private String getTableWordXML(String[][] _oldTableData) {
		TBUtil tbUtil = new TBUtil();
		boolean isChangeRowCol = tbUtil.getSysOptionBooleanValue("�����Ƿ�ת����", false);
		if (_oldTableData == null || _oldTableData.length == 0) {
			return "";
		}
		String[][] str_newData = null; //
		if (_oldTableData.length > _oldTableData[0].length || !isChangeRowCol) { //��������������ݶ�!��Ҫת��
			str_newData = _oldTableData;
		} else if (isChangeRowCol) {
			str_newData = convertStrArray(_oldTableData); //
		}
		return new WordFileUtil().getTableXml(str_newData, 2160, "center"); //
	}

	/**
	 * �޸�ԭ����,��һ�е�һ�в����   gaofeng
	 * @param _oldTableData
	 * @return
	 */
	private String getTableWordXML2(String[][] _oldTableData) {
		TBUtil tbUtil = new TBUtil();
		boolean isChangeRowCol = tbUtil.getSysOptionBooleanValue("�����Ƿ�ת����", false);
		if (_oldTableData == null || _oldTableData.length == 0) {
			return "";
		}
		String[][] str_newData = null; //
		if (_oldTableData.length > _oldTableData[0].length || !isChangeRowCol) { //��������������ݶ�!��Ҫת��
			str_newData = _oldTableData;
		} else if (isChangeRowCol) {
			str_newData = convertStrArray(_oldTableData); //
		}
		return new WordFileUtil().getTableXml(str_newData, 2160, "center"); //
	}

	private String getTableTitle(String strcontext) {
		StringBuffer sb = new StringBuffer();
		sb.append("<w:p>");
		sb.append("<w:pPr>");
		sb.append("<w:rPr>");
		sb.append("<w:rFonts w:hint=\"fareast\"/>");
		sb.append("</w:rPr>");
		sb.append("</w:pPr>");
		sb.append("<w:r>");
		sb.append("<w:rPr>");
		sb.append("<w:rFonts w:hint=\"fareast\"/>");
		sb.append("<wx:font wx:val=\"����\"/>");
		sb.append("</w:rPr>");
		sb.append("<w:t>");
		if (strcontext.indexOf(">") >= 0 || strcontext.indexOf("<") >= 0) {
			sb.append("<![CDATA[" + strcontext + "]]>");
		} else {
			sb.append(strcontext);
		}

		sb.append("</w:t>");
		sb.append("</w:r>");
		sb.append("</w:p>");
		return sb.toString();
	}

	/**
	 * �õ�ͼƬ��64λ��
	 * 
	 * @param jFreeChart
	 * @return
	 */
	private String imageToBaseCode(JFreeChart jFreeChart) {
		try {
			ByteArrayOutputStream output = null;
			BufferedImage image = null;
			byte[] outputByte = null;
			output = new ByteArrayOutputStream();
			image = jFreeChart.createBufferedImage(700, 350, 1, null); //����ͼƬ��������ɫ���� jfreechart-1.0.14������ �����/2013-06-13��
			ImageIO.write(image, "jpeg", output);
			outputByte = output.toByteArray();
			return new String(org.jfreechart.xml.util.Base64.encode(outputByte)); //jfreechart-1.0.14�����޸���·��jfreeΪjfreechart �����/2013-06-09��
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * ����Mht�ļ�,�ǳ���Ҫ��һ�ּ���,��������Ҫ��MHT�ĸ�ʽ�������ӵ���������!! �ʼ���ʵҲ����������!!
	 */
	public void exportMHT() {
		try {
			int li_type = getChartTypeRadioSelected();
			JFreeChart chart = null; //
			if (li_type == 1) {
				chart = chart_bar;
			} else if (li_type == 2) {
				chart = chart_line;
			} else if (li_type == 3) {
				chart = chart_pie;
			}
			String str_mht = getMhtBeginHtml() + getMhtContentHtml(getTableToArray(jTable_1), "400", "700") + getMhtImageHtml(imageToBaseCode(chart));
			UIUtil.saveFile(this, "mht", "��������", str_mht.getBytes("GBK")); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//mht��ͷ!
	private String getMhtBeginHtml() {
		StringBuffer begin = new StringBuffer();
		begin.append("From: <�� Microsoft Internet Explorer 5 ����>\r\n");
		begin.append("Subject: export\r\n");
		begin.append("Date: Tue, 16 Sep 2008 13:53:06 +0800\r\n");
		begin.append("MIME-Version: 1.0\r\n");
		begin.append("Content-Type: multipart/related;\r\n");
		begin.append("  type=\"text/html\";\r\n");
		begin.append("  boundary=\"----=_NextPart_000_0000_01C91815.A55B3E50\"\r\n");
		begin.append("X-MimeOLE: Produced By Microsoft MimeOLE V6.00.2900.3198\r\n");
		begin.append("\r\n");
		begin.append("This is a multi-part message in MIME format.\r\n");
		begin.append("\r\n");
		begin.append("------=_NextPart_000_0000_01C91815.A55B3E50\r\n");
		begin.append("Content-Type: text/html;\r\n");
		begin.append("  charset=\"gb2312\"\r\n");
		begin.append("Content-Transfer-Encoding: quoted-printable\r\n");
		begin.append("Content-Location: =?gb2312?B?ZmlsZTovL0Q6XM7StcTOxLW1XNfAw+ZcYWFhYS5odG1s?=\r\n");
		begin.append("\r\n");
		begin.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\r\n");
		return begin.toString();
	}

	//mht�ļ��е�Html
	private String getMhtContentHtml(String[][] input, String _height, String _width) {
		StringBuffer returnHtml = new StringBuffer();
		returnHtml.append("<HTML><HEAD><TITLE>export</TITLE>\r\n");
		returnHtml.append("<META http-equiv=3DContent-Type content=3D\"text/html; charset=3Dgb2312\">\r\n");
		returnHtml.append("<STYLE type=3Dtext/css>.style_1 {\r\n");
		returnHtml.append("  FONT-SIZE: 12px; COLOR: #333333; LINE-HEIGHT: 18px; FONT-FAMILY: =\r\n");
		returnHtml.append("=CB=CE=CC=E5");
		returnHtml.append("}\r\n");
		returnHtml.append("</STYLE>\r\n");
		returnHtml.append("<META content=3D\"MSHTML 6.00.2900.3268\" name=3DGENERATOR>\r\n");
		returnHtml.append("</HEAD><BODY>\r\n");
		//returnHtml.append("<IMG src=3D\"file:///c:/image.jpg\">=20\r\n");
		returnHtml.append("<IMG height=3D" + _height + " src=3D\"file:///c:/image.jpg\" width=3D" + _width + ">=20\r\n");
		returnHtml.append("<TABLE style=3D\"MARGIN: 3px 0px 8px\" borderColor=3D#cccccc cellSpacing=3D1=20 cellPadding=3D5 align=3Dleft bgColor=3D#999999 border=3D0>\r\n");
		returnHtml.append("<TBODY>");
		for (int i = 0; i < input.length; i++) {
			returnHtml.append("<TR height=3D20>\r\n");
			for (int j = 0; j < input[i].length; j++) {
				returnHtml.append("<TD class=3Dstyle_1 vAlign=3Dcenter align=3Dmiddle width=3D100=20 bgColor=3D#ffffff>" + input[i][j] + "</TD>\r\n");
			}
			returnHtml.append("</TR>\r\n");
		}
		returnHtml.append("</TBODY>");
		returnHtml.append("</TABLE>\r\n");
		returnHtml.append("</HTML>\r\n");
		returnHtml.append("\r\n");
		return returnHtml.toString();
	}

	//mht��ͼƬ��ʵ������!
	private String getMhtImageHtml(String img) {
		StringBuffer image = new StringBuffer();
		image.append("------=_NextPart_000_0000_01C91815.A55B3E50\r\n");
		image.append("Content-Type: image/jpeg\r\n");
		image.append("Content-Transfer-Encoding: base64\r\n");
		image.append("Content-Location: file:///c:/image.jpg\r\n");
		image.append("\r\n");
		image.append(img + "\r\n");
		image.append("\r\n");
		image.append("------------=_NextPart_000_0000_01C91815.A55B3E50--\r\n");
		return image.toString();
	}

	public void setStr_builderClassName(String str_builderClassName) {
		this.str_builderClassName = str_builderClassName;
	}

	public void setQueryConditionMap(HashMap queryConditionMap) {
		this.queryConditionMap = queryConditionMap;
	}

	public void setAllCanGroupFields(String[] allCanGroupFields) {
		this.allCanGroupFields = allCanGroupFields;
	}

	public void setStr_drillActionClass(String str_drillActionClass) {
		this.str_drillActionClass = str_drillActionClass;
	}

	public void setStr_drillTempletCode(String str_drillTempletCode) {
		this.str_drillTempletCode = str_drillTempletCode;
	}

	public JTable getJTable_3() {
		return jTable_3;
	}

	public void setJTable_3(JTable table_3) {
		jTable_3 = table_3;
	}

	public JTable getJTable_2() {
		return jTable_2;
	}

	public void setJTable_2(JTable table_2) {
		jTable_2 = table_2;
	}

	public JTable getJTable_1() {
		return jTable_1;
	}

	public void setJTable_1(JTable table_1) {
		jTable_1 = table_1;
	}

	public boolean isShowTotalColumn() {
		return isShowTotalColumn;
	}

	public void setShowTotalColumn(boolean newvalue) {
		this.isShowTotalColumn = newvalue;
	}

	//��ʼ��beautifulColor��ɫ���顣
	public void initColor() {
		int max = 253;
		int min = 41;
		int single = (max - min) / 2 + 3; //������Ծֵ
		int r = max;
		int g = min;
		int b = min;
		List colorList = new ArrayList();
		while (1 == 1) {
			Color color = new Color(r, g, b);
			colorList.add(color);
			if (colorList.size() > 36) {
				break;
			}
			if (g < max && r >= max && b == min) {
				g += single;
				if (g >= 255) {
					g = max;
				}
			} else if (g >= max && r > min && b <= min) {
				r -= single;
				if (r <= 0) {
					r = min;
				}
			} else if (g >= max && r <= min && b < max) {
				b += single;
				if (b >= 255) {
					b = max;
				}
			} else if (g > min && r <= min && b >= max) {
				g -= single;
				if (g <= 0) {
					g = min;
				}
			} else if (g <= min && r < max && b >= max) {
				r += single;
				if (r >= 255) {
					r = max;
				}
			} else {
				max = max - 42;
				min = min + 18;
				single = (max - min) / 2 + 2;
				r = max;
				g = min;
				b = min;
			}
		}
		beautifulColors = (Color[]) colorList.toArray(new Color[0]);
	}

	class MyCellRender extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable _table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel) super.getTableCellRendererComponent(_table, value, isSelected, hasFocus, row, column); //
			boolean isMovingOnMe = false; //�Ƿ����������ˣ�
			String str_MouseMovingRowCol = (String) _table.getClientProperty("MouseMovingRowCol"); //
			if (str_MouseMovingRowCol != null) {
				int li_row = Integer.parseInt(str_MouseMovingRowCol.substring(0, str_MouseMovingRowCol.indexOf(","))); //
				int li_col = Integer.parseInt(str_MouseMovingRowCol.substring(str_MouseMovingRowCol.indexOf(",") + 1, str_MouseMovingRowCol.length())); //
				if (row == li_row && column == li_col) { //������ϵ�������,����ǵ�ǰ������!!
					isMovingOnMe = true; //
				}
			}

			label.setForeground(Color.BLACK); //
			if (isMovingOnMe && value != null) { //���������ϵ�����,����ֵ,�����»���!
				label.setText("<html><u>" + value.toString() + "</u></html>"); //
				label.setForeground(Color.BLUE); //
			}

			if (column == 0) { //��һ���ֿ���
				label.setHorizontalAlignment(SwingConstants.LEFT);
			} else {
				label.setHorizontalAlignment(SwingConstants.RIGHT);
			}

			if (value != null && value instanceof BillChartItemVO) {
				if (((BillChartItemVO) value).getValueType() == BillChartItemVO.RATE) { //ֻ���Ǳ������͵Ĳ���ʾ��μ����
					label.setToolTipText(((BillChartItemVO) value).getInfoMsg()); //
				}
			}

			boolean bo_rowAllowSelected = _table.getRowSelectionAllowed(); //
			boolean bo_colAllowSelected = _table.getColumnSelectionAllowed(); //

			label.setOpaque(true); ////
			if (column == 0) { //��һ��
				label.setBackground(new Color(240, 240, 240)); //������ɫ!!!
			} else {
				if (bo_rowAllowSelected && !bo_colAllowSelected) { //ֻѡ����
					if (column == _table.getColumnCount() - 1 && isShowTotalColumn) { //��������һ��.
						label.setBackground(new Color(254, 254, 181)); //������ɫ!!!
					} else {
						if (isSelected) {
							label.setBackground(_table.getSelectionBackground()); ////
						} else {
							if (row == _table.getRowCount() - 1) {
								label.setBackground(new Color(254, 254, 181)); //������ɫ!!!
							} else {
								label.setBackground(Color.WHITE);
							}
						}
					}
				} else if (!bo_rowAllowSelected && bo_colAllowSelected) { //ֻѡ����
					if (row == _table.getRowCount() - 1) { //��������һ��
						label.setBackground(new Color(254, 254, 181)); //������ɫ!!!
					} else {
						if (isSelected) {
							label.setBackground(_table.getSelectionBackground()); ////
						} else {
							if (column == _table.getColumnCount() - 1 && isShowTotalColumn) {
								label.setBackground(new Color(254, 254, 181)); //������ɫ!!!
							} else {
								label.setBackground(Color.WHITE);
							}
						}
					}
				} else if (bo_rowAllowSelected && bo_colAllowSelected) { //���ж�ѡ��
					if (isSelected) {
						label.setBackground(_table.getSelectionBackground()); ////
					} else {
						if (row == _table.getRowCount() - 1 || (column == _table.getColumnCount() - 1 && isShowTotalColumn)) {
							label.setBackground(new Color(254, 254, 181)); //������ɫ!!!
						} else {
							label.setBackground(Color.WHITE);
						}
					}
				}
			}
			return label; //
		}
	}

	class CustomBarRenderer extends BarRenderer {
		private Paint colors[];

		public CustomBarRenderer(Paint[] apaint) {
			colors = new GradientPaint[apaint.length];
			Color[] color = (Color[]) apaint;
			for (int i = 0; i < color.length; i++) {
				colors[i] = new GradientPaint(0, 0, color[i].brighter().brighter(), 0, 0, color[i].darker());
			}
		}

		public Paint getItemPaint(int i, int j) {
			return colors[j];
		}

		public void setSeriesPaint(int series, Paint paint) {
			setSeriesPaint(series, paint, true);
		}

	}

	class CustomBarRenderer3D extends BarRenderer3D {
		private Paint colors[];

		public CustomBarRenderer3D(Paint[] apaint) {
			colors = apaint;
		}

		public Paint getItemPaint(int i, int j) {
			return colors[j];
		}
	}

}
