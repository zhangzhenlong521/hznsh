package cn.com.infostrategy.ui.sysapp.login;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.GradientPaintTransformType;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.StandardGradientPaintTransformer;
import org.jfree.ui.TextAnchor;
import org.jfreechart.chart.plot.dial.DialCap;
import org.jfreechart.chart.plot.dial.DialTextAnnotation;
import org.jfreechart.chart.plot.dial.DialValueIndicator;
import org.jfreechart.chart.plot.dial.StandardDialRange;
import org.jfreechart.chart.plot.dial.StandardDialScale;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.report.BillChartRangVO;
import cn.com.infostrategy.to.sysapp.login.DeskTopNewGroupVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.BillFrame;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTHtmlButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.IconFactory;
import cn.com.infostrategy.ui.sysapp.login.index.IndexPanelCustCommIfc;
import cn.com.infostrategy.ui.workflow.pbom.BillBomPanel;
import cn.com.infostrategy.ui.workflow.pbom.BomItemPanel;

/**
 * ��ҳ��Ϣ���ĳ���ʹ������˵����,Ҳ����֧��ͼƬ!!! ��ֱ�ӽ�����ͼ,����ͼ,��ͼ������ҳ!!!
 * ����ܹؼ�,��ǰ��ҳ������ԭ�������Ϊȱ��ͼƬ�����Ч����,û��ͼƬҲ����Ҫԭ��! ����ͼ�ξͻ�ʹϵͳ�Եñ�����ḻ�ö�!!!
 * @author xch
 *
 */
public class IndexChartPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private DeskTopPanel deskTopPanel = null; //
	private DeskTopNewGroupVO taskVO = null; //
	private String chartType = null; //
	private String templetcode = null;

	private JButton btn_refresh, btn_more; //
	private MouseListener mouseListener = null; //
	private boolean isRefresh = true; //
	private TBUtil tbUtil = new TBUtil(); //
	private  int x = 730;
	private JPanel text=null;
	private javax.swing.Timer timer = null; //

	private Color[] beautifulColors = new Color[] { // 
	new Color(255, 200, 200), new Color(255, 255, 217), new Color(166, 166, 255), new Color(254, 0, 254), new Color(0, 254, 254), new Color(128, 0, 254), new Color(0, 128, 0), // 
			new Color(254, 254, 128), new Color(128, 0, 128), new Color(64, 0, 128), new Color(128, 128, 0), new Color(128, 128, 192), new Color(254, 128, 192), new Color(0, 128, 254), //
			new Color(128, 64, 0), new Color(225, 213, 218), new Color(74, 63, 228), new Color(200, 234, 210), new Color(224, 242, 26), new Color(231, 192, 229), new Color(98, 69, 77), // 
			new Color(192, 188, 226), new Color(73, 129, 90), new Color(241, 244, 210), new Color(225, 39, 214), new Color(195, 229, 226), new Color(169, 143, 80), new Color(225, 48, 94), // 
			new Color(100, 99, 108), new Color(42, 231, 99), new Color(153, 159, 87), new Color(96, 69, 94), new Color(74, 143, 138), new Color(244, 178, 18) }; //һϵ�кÿ�����ɫ,һ��25��,���ν�������

	//jfreechart-1.0.14 ����������� �����/2013-06-09��
	{
		StandardChartTheme theme = new StandardChartTheme("unicode");

		theme.setExtraLargeFont(new Font("SimSun", Font.PLAIN, 20));
		theme.setLargeFont(new Font("SimSun", Font.PLAIN, 14));
		theme.setRegularFont(new Font("SimSun", Font.PLAIN, 12));
		theme.setSmallFont(new Font("SimSun", Font.PLAIN, 10));

		ChartFactory.setChartTheme(theme);
	}

	public IndexChartPanel(DeskTopPanel _deskTopPanel, DeskTopNewGroupVO _taskVO) {
		this.deskTopPanel = _deskTopPanel; //
		this.taskVO = _taskVO; //
		this.chartType = taskVO.getDefineVO().getDatatype(); //
		this.templetcode=taskVO.getDefineVO().getTempletcode();
		mouseListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onClicked(e.getSource()); //
			}
		};
		initialize(); //
	}

	public IndexChartPanel() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private void initialize() {
		boolean isLazyLoad = "Y".equalsIgnoreCase(taskVO.getDefineVO().getIsLazyLoad()); //�Ƿ���װ��??
		if (isLazyLoad) { //�������װ��!
			this.setBackground(LookAndFeel.defaultShadeColor1); //new Color(226, 236, 246)
			this.setLayout(new BorderLayout()); //
			this.setOpaque(false); //͸��
			this.add(new JLabel("���ڼ�������....", UIUtil.getImage("office_006.gif"), SwingConstants.CENTER), BorderLayout.CENTER); //����ʾ���ڼ�������
			this.add(getTitlePanel(taskVO.getDefineVO().getTitle(), taskVO.getDefineVO().getImgicon(), taskVO.getDefineVO().getTitlecolor(), false), BorderLayout.NORTH); //
			new Timer().schedule(new TimerTask() { //��һ���߳�,ȥ����������!!
						@Override
						public void run() { //�¿�һ���̼߳���ҳ��!
							lazyLoad(); //��װ��!!
						}
					}, 0); //���೤ʱ���ʼִ��!!
		} else {
			loadRealPanel(); //
		}
	}

	/**
	 * ��װ���߼�!����Ҫȡ������,Ȼ��ҳ��!!
	 */
	private void lazyLoad() {
		try {
			String str_clasName = taskVO.getDefineVO().getDatabuildername(); //���ݼ�������!
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			HashVO[] hvs = service.getDeskTopNewGroupVOData(str_clasName, ClientEnvironment.getCurrLoginUserVO().getCode(), taskVO.getDefineVO()); //������ܺ�ʱ!!
			this.taskVO.setDataVOs(hvs); //
			this.removeAll(); //�Ȳ���ҳ������������!!
			loadRealPanel(); //ʵ�ʼ���!
			this.updateUI(); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ʵ�ʼ���ҳ��!
	 */
	private void loadRealPanel() {
		try {
			isRefresh = tbUtil.getSysOptionBooleanValue("��ҳ�������Ƿ���ˢ�°�ť", true); //������������Ȼ��ϲ��ˢ��
			this.setLayout(new BorderLayout()); //
			this.setOpaque(false); //͸��!!
			String str_title = taskVO.getDefineVO().getTitle(); //
			String str_imgName = taskVO.getDefineVO().getImgicon(); //
			String str_titleColor = taskVO.getDefineVO().getTitlecolor(); //��ɫ
			this.add(getTitlePanel(str_title, str_imgName, str_titleColor, true), BorderLayout.NORTH); //
			JPanel tmpPanel = new JPanel(new BorderLayout());
			tmpPanel.setOpaque(false);
			tmpPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));
			if (chartType.equals("����ͼ")) {
				tmpPanel.add(getBarChartPanel(), BorderLayout.CENTER); //
			} else if (chartType.equals("��������ͼ")) {
				tmpPanel.add(getBarChartPanel_H(), BorderLayout.CENTER); //
			} else if (chartType.equals("����ͼ")) {
				tmpPanel.add(getLineChartPanel(), BorderLayout.CENTER); //
			} else if (chartType.equals("��ɫ������ͼ")) {
				//��ɫ������ͼƽ̨�ṩʾ�� ��Ŀ����ʵ�����ʹ���Զ���Panelʵ�� �����/2013-06-14��	
				tmpPanel.add(getLineChartPanel_R(null), BorderLayout.CENTER); //
			} else if (chartType.equals("��ͼ")) {
				tmpPanel.add(getPieChartPanel(), BorderLayout.CENTER); //
			} else if (chartType.equals("�Ǳ���ͼ")) {
				tmpPanel = new JPanel(new FlowLayout());
				tmpPanel.setOpaque(false);
				HashVO[] hvs = taskVO.getDataVOs(); //���ɵ�����
				if (hvs == null || hvs.length <= 0) { //��Demo����
					JPanel diaPlotPanel_1 = getDialPlotPanel("���յ�", "����", 45, 0, 30, 80, 100, new Color(255, 255, 176), "���з��յ������"); //
					JPanel diaPlotPanel_2 = getDialPlotPanel("�����¼�", "���·���", 75, 0, 30, 80, 100, new Color(255, 163, 83), "�����ȷ����ķ����¼�����"); //
					Dimension size = new Dimension(165, 160);
					diaPlotPanel_1.setPreferredSize(size);
					diaPlotPanel_2.setPreferredSize(size);
					tmpPanel.add(diaPlotPanel_1); //
					tmpPanel.add(diaPlotPanel_2); //
				} else {
					if (hvs.length == 1) {
						JPanel diaPlotPanel_1 = getDialPlotPanelByHashVO(hvs[0]); //
						Dimension size = new Dimension(165, 160);
						diaPlotPanel_1.setPreferredSize(size);
						tmpPanel.add(diaPlotPanel_1); //
					} else {
						JPanel diaPlotPanel_1 = getDialPlotPanelByHashVO(hvs[0]); //��һ���Ǳ���!!
						JPanel diaPlotPanel_2 = getDialPlotPanelByHashVO(hvs[1]); //�ڶ����Ǳ���!!!
						Dimension size = new Dimension(165, 160);
						diaPlotPanel_1.setPreferredSize(size);
						diaPlotPanel_2.setPreferredSize(size);
						tmpPanel.add(diaPlotPanel_1); //
						tmpPanel.add(diaPlotPanel_2); //
					}
				}
			} else if (chartType.equals("Bomͼ")) {//��ҳ������Ϣ����ʾbomͼ�����ϳ�Ͷ���Ҫ�����/2015-04-10��
				HashVO[] hvs = taskVO.getDataVOs(); //���ɵ�����
				String bomcode = "smeom";//����Ĭ�ϵ�bomͼ����
				if (hvs != null && hvs.length > 0) {
					bomcode = hvs[0].getStringValue("bomcode");//����ʵ�ʵ�bomͼ����
				}
				BillBomPanel bomPanel = new BillBomPanel(templetcode);
				if (!ClientEnvironment.isAdmin()) {
					bomPanel.setEditable(false);//���ɱ༭
				}
//				bomPanel.putClientProperty("BOMTYPE", "RISK");
//				Hashtable _htriskVO=new Hashtable();
//				_htriskVO.put("�Ŵ��ʲ�����", "12,12,12");
//				bomPanel.setRiskVO(_htriskVO);
				tmpPanel.add(bomPanel, BorderLayout.CENTER); //
			} else if (chartType.equals("����ͼ")) {
				//Ϊ�˼���̫ƽ�ɴ��룬���ڴ˴�����һ������ʵ�ֽӿڡ��������õ�λ�÷ŵ��������������Ҳû�к��ʵĵط��š�
				tmpPanel = new JPanel(new FlowLayout());
				tmpPanel.setOpaque(false);
				String classpath = taskVO.getDefineVO().getDatabuildername();
				if (!tbUtil.isEmpty(classpath)) {
					try {
						Object obj = Class.forName(classpath.trim()).newInstance();
						if (obj instanceof IndexPanelCustCommIfc) {
							IndexPanelCustCommIfc impl = (IndexPanelCustCommIfc) obj;
							impl.init(tmpPanel, deskTopPanel, taskVO);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			this.add(tmpPanel, BorderLayout.CENTER); //
			this.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
		} catch (Exception ex) {
			this.setLayout(new BorderLayout()); //
			this.add(new JLabel("����ͼ��ʧ��,��������̨�鿴��־!")); //
		}
	}

	private JPanel getDialPlotPanelByHashVO(HashVO _hvo) {
		String str_title = _hvo.getStringValue("����", "����"); //
		String str_x = _hvo.getStringValue("X��", "X��"); //
		double li_realValue = _hvo.getDoubleValue("ʵ��ֵ", (double) 50); //
		double li_value1 = _hvo.getDoubleValue("��Сֵ", (double) 0); //
		double li_value2 = _hvo.getDoubleValue("����ֵ", (double) 30); //
		double li_value3 = _hvo.getDoubleValue("����ֵ", (double) 60); //
		double li_value4 = _hvo.getDoubleValue("���ֵ", (double) 100); //
		String str_color = _hvo.getStringValue("����ɫ", "FFA953"); //
		String str_tip = _hvo.getStringValue("��ʾ", ""); //
		Color bgColor = tbUtil.getColor(str_color); //
		JPanel diaPlotPanel = getDialPlotPanel(str_title, str_x, li_realValue, li_value1, li_value2, li_value3, li_value4, bgColor, str_tip); //
		return diaPlotPanel; //
	}

	//�����������!!
	private JPanel getTitlePanel(String _title, String _imgName, String _titleColor, boolean _isHaveRightBtn) {
		_imgName = (_imgName == null ? "office_066.gif" : _imgName); //ͼƬ
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		panel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
		JLabel label_title = new JLabel(" " + _title, UIUtil.getImage(_imgName), JLabel.LEFT); //
		label_title.setFont(LookAndFeel.desktop_GroupTitleFont); //����������Ҫ����ҳ��������ִܴ�!���˲�����������Ӱ��,�ָ��˸�����!!
		if (_titleColor != null && !_titleColor.trim().equals("")) { //�����������ɫ!!
			int li_pos = _titleColor.indexOf("@"); //����û��@����������?��Ϊ������Ŀ���е���ı�����ɫ��������һ��!
			if (_titleColor.indexOf("@") > 0) { //�����@����
				label_title.setForeground(tbUtil.getColor(_titleColor.substring(0, li_pos), Color.BLACK)); //ǰ����ɫ!!
				panel.setBackground(tbUtil.getColor(_titleColor.substring(li_pos + 1, _titleColor.length()), Color.BLACK)); //������ɫ!!��Ϊlabel��͸����,����Ҫ����panel��!!
			} else { //���û�ж���,��Ĭ��ʹ�ú�ɫ!
				label_title.setForeground(tbUtil.getColor(_titleColor, Color.BLACK)); //ֻ��ǰ��!
			}
		}
		panel.add(label_title, BorderLayout.WEST); //

		if (_isHaveRightBtn) {
			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0)); //
			btnPanel.setBorder(BorderFactory.createEmptyBorder(1, 15, 1, 25));
			btnPanel.setOpaque(false); //
			btn_refresh = new WLTHtmlButton("ˢ��"); //
			btn_more = new WLTHtmlButton("����"); //
			btn_refresh.addActionListener(this); //
			btn_more.addActionListener(this); //
			if (isRefresh) {
				btnPanel.add(btn_refresh); //
			}
			btnPanel.add(btn_more); //
			panel.add(btnPanel, BorderLayout.EAST); //
		}
		panel.setPreferredSize(new Dimension(-1, 30)); //
		return panel; //
	}

	/**
	 * ����ͼ
	 * @return
	 */
	private JPanel getBarChartPanel() {
		String[] str_x = null; //;
		String[] str_y = null; //
		double[][] ld_value = null; //
		HashVO[] hvs = taskVO.getDataVOs(); //���ɵ�����
		if (hvs == null || hvs.length == 0) { //���û������ʹ��Demo����!!
			Object[] objs = getBarOrLineDemoData(); //
			str_x = (String[]) objs[0];
			str_y = (String[]) objs[1];
			ld_value = (double[][]) objs[2];
		} else { //
			Object[] objs = getBarOrLineData(hvs); //
			str_x = (String[]) objs[0];
			str_y = (String[]) objs[1];
			ld_value = (double[][]) objs[2]; //
		}
		DefaultCategoryDataset dataSet = createCategoryDataset(str_x, str_y, ld_value); //

		HashMap config = taskVO.getDefineVO().getOtherconfig();
		String X_name = "X��";
		String Y_name = "Y��";

		if (config.containsKey("X��")) {
			X_name = (String) config.get("X��");
		}
		if (config.containsKey("Y��")) {
			Y_name = (String) config.get("Y��");
		}
		org.jfree.chart.JFreeChart chart = ChartFactory.createBarChart("", X_name, Y_name, dataSet, PlotOrientation.VERTICAL, true, true, false); ////
		chart.setBackgroundPaint(Color.white); //
		if (dataSet.getRowCount() == 1) { //���ֻ��һ������
			setChartAsCustRender(chart); //
		} else {
			CategoryPlot plot = chart.getCategoryPlot();
			NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setUpperMargin(0.2);
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			BarRenderer renderer = (BarRenderer) plot.getRenderer();
			renderer.setBaseItemLabelsVisible(true); //
			renderer.setDrawBarOutline(false);
			renderer.setBaseItemLabelsVisible(true);
			renderer.setMaximumBarWidth(0.10000000000000001D);
			int count = str_x.length;
			for (int i = 0; i < count; i++) {
				renderer.setSeriesPaint(i, new GradientPaint(0f, 0.0F, beautifulColors[(i) % beautifulColors.length], 0.0f, WIDTH, beautifulColors[(i) % beautifulColors.length].darker().darker())); //
			}
			renderer.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.CENTER_HORIZONTAL));
			plot.setRenderer(renderer);

		}

		//׷������������ �����/2013-06-09��
		CategoryPlot categoryplot = chart.getCategoryPlot();
		CategoryItemRenderer localCategoryItemRenderer = categoryplot.getRenderer();
		localCategoryItemRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		localCategoryItemRenderer.setSeriesItemLabelsVisible(0, Boolean.TRUE);

		if (str_y.length > 8) {
			//������б �����/2013-06-09��
			CategoryAxis domainAxis = categoryplot.getDomainAxis();
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
		}

		//���ñ���ɫ������ɫ �����/2013-06-09��
		categoryplot.setBackgroundPaint(Color.white);
		categoryplot.setRangeGridlinePaint(Color.lightGray);

		ChartPanel charPanel = new ChartPanel(chart); //
		//charPanel.setMouseWheelEnabled(true); //ͼ��ת�� �������� �����/2013-06-09��
		charPanel.putClientProperty("ChartType", "����ͼ"); //
		charPanel.putClientProperty("Title", taskVO.getDefineVO().getTitle()); //
		charPanel.putClientProperty("SeriesX", str_x); //
		charPanel.putClientProperty("SeriesY", str_y); //
		charPanel.putClientProperty("Data", ld_value); //
		charPanel.addMouseListener(mouseListener); //
		return charPanel; //
	}

	//��������ͼ �����/2013-06-14��
	private JPanel getBarChartPanel_H() {
		String[] str_x = null; //;
		String[] str_y = null; //
		double[][] ld_value = null; //
		HashVO[] hvs = taskVO.getDataVOs(); //���ɵ�����
		if (hvs == null || hvs.length == 0) { //���û������ʹ��Demo����!!
			Object[] objs = getBarOrLineDemoData(); //
			str_x = (String[]) objs[0];
			str_y = (String[]) objs[1];
			ld_value = (double[][]) objs[2];
		} else { //
			Object[] objs = getBarOrLineData(hvs); //
			str_x = (String[]) objs[0];
			str_y = (String[]) objs[1];
			ld_value = (double[][]) objs[2]; //
		}

		HashMap config = taskVO.getDefineVO().getOtherconfig();
		DefaultCategoryDataset dataSet = createCategoryDataset(str_x, str_y, ld_value); //

		String X_name = "X��";
		String Y_name = "Y��";

		if (config.containsKey("X��")) {
			X_name = (String) config.get("X��");
		}
		if (config.containsKey("Y��")) {
			Y_name = (String) config.get("Y��");
		}

		org.jfree.chart.JFreeChart chart = ChartFactory.createBarChart("", X_name, Y_name, dataSet, PlotOrientation.HORIZONTAL, true, true, false); ////
		chart.setBackgroundPaint(Color.white); //
		if (dataSet.getRowCount() == 1) { //���ֻ��һ������
			setChartAsCustRender(chart); //
		} else {
			CategoryPlot plot = chart.getCategoryPlot();
			NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setUpperMargin(0.2);
			rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			BarRenderer renderer = (BarRenderer) plot.getRenderer();
			renderer.setBaseItemLabelsVisible(true); //
			renderer.setDrawBarOutline(false);
			renderer.setBaseItemLabelsVisible(true);
			renderer.setMaximumBarWidth(0.10000000000000001D);
			int count = str_x.length;
			for (int i = 0; i < count; i++) {
				renderer.setSeriesPaint(i, new GradientPaint(0f, 0.0F, beautifulColors[(i) % beautifulColors.length], 0.0f, WIDTH, beautifulColors[(i) % beautifulColors.length].darker().darker())); //
			}
			renderer.setGradientPaintTransformer(new StandardGradientPaintTransformer(GradientPaintTransformType.CENTER_HORIZONTAL));
			plot.setRenderer(renderer);

		}

		//׷������������ �����/2013-06-09��
		CategoryPlot categoryplot = chart.getCategoryPlot();
		CategoryItemRenderer localCategoryItemRenderer = categoryplot.getRenderer();
		localCategoryItemRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		localCategoryItemRenderer.setSeriesItemLabelsVisible(0, Boolean.TRUE);

		if (str_y.length > 8) {
			//������б �����/2013-06-09��
			CategoryAxis domainAxis = categoryplot.getDomainAxis();
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
		}

		//���ñ���ɫ������ɫ �����/2013-06-09��
		categoryplot.setBackgroundPaint(Color.white);
		categoryplot.setRangeGridlinePaint(Color.lightGray);

		//����xy�� �����/2013-06-14��
		//categoryplot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);

		ChartPanel charPanel = new ChartPanel(chart); //
		//charPanel.setMouseWheelEnabled(true); //ͼ��ת�� �������� �����/2013-06-09��
		charPanel.putClientProperty("ChartType", "����ͼ"); //
		charPanel.putClientProperty("Title", taskVO.getDefineVO().getTitle()); //
		charPanel.putClientProperty("SeriesX", str_x); //
		charPanel.putClientProperty("SeriesY", str_y); //
		charPanel.putClientProperty("Data", ld_value); //
		charPanel.addMouseListener(mouseListener); //
		return charPanel; //
	}

	/**
	 * ����ͼ
	 * @return
	 */
	private JPanel getLineChartPanel() {
		String[] str_x = null; //;
		String[] str_y = null; //
		double[][] ld_value = null; //
		HashVO[] hvs = taskVO.getDataVOs(); //���ɵ�����
		if (ClientEnvironment.isAdmin() && (hvs == null || hvs.length == 0)) { //���û������ʹ��Demo����!!
			Object[] objs = getBarOrLineDemoData(); //
			str_x = (String[]) objs[0];
			str_y = (String[]) objs[1];
			ld_value = (double[][]) objs[2];
		} else { //
			Object[] objs = getBarOrLineData(hvs); //
			str_x = (String[]) objs[0];
			str_y = (String[]) objs[1];
			ld_value = (double[][]) objs[2]; //
		}
		DefaultCategoryDataset dataSet = createCategoryDataset(str_x, str_y, ld_value); //

		HashMap config = taskVO.getDefineVO().getOtherconfig();
		String X_name = "X��";
		String Y_name = "Y��";

		if (config.containsKey("X��")) {
			X_name = (String) config.get("X��");
		}
		if (config.containsKey("Y��")) {
			Y_name = (String) config.get("Y��");
		}

		org.jfree.chart.JFreeChart chart = ChartFactory.createLineChart("", X_name, Y_name, dataSet, PlotOrientation.VERTICAL, true, true, false);
		chart.setBackgroundPaint(Color.white); //

		/*		StandardLegend standardlegend = (StandardLegend) chart.getLegend();
				standardlegend.setDisplaySeriesShapes(true);
				standardlegend.setShapeScaleX(1.5D);
				standardlegend.setShapeScaleY(1.5D);
				standardlegend.setDisplaySeriesLines(true);*/
		CategoryPlot categoryplot = chart.getCategoryPlot();
		NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
		numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		numberaxis.setAutoRangeIncludesZero(true);
		numberaxis.setUpperMargin(0.20000000000000001D);
		numberaxis.setLabelAngle(1.5707963267948966D);
		LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer) categoryplot.getRenderer();
		lineandshaperenderer.setDrawOutlines(true);
		//���ֻ������ǰ3�����߷�ʽ 3������ȫ��ʵ�� ��ע�� ͳһ��ʵ�� �����/2013-06-09��
		/*		lineandshaperenderer.setSeriesStroke(0, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 10F, 6F }, 0.0F));
				lineandshaperenderer.setSeriesStroke(1, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 6F, 6F }, 0.0F));
				lineandshaperenderer.setSeriesStroke(2, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 2.0F, 6F }, 0.0F));*/
		lineandshaperenderer.setBaseItemLabelsVisible(true);
		lineandshaperenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition());
		lineandshaperenderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition());

		LineAndShapeRenderer localLineAndShapeRenderer = (LineAndShapeRenderer) categoryplot.getRenderer();
		for (int i = 0; i < str_x.length; i++) {
			//׷�����ϵ���״ �����/2013-06-09��
			localLineAndShapeRenderer.setSeriesShapesVisible(i, true);
		}

		//׷�ӵ������� �����/2013-06-09��
		localLineAndShapeRenderer.setBaseShapesVisible(true);
		localLineAndShapeRenderer.setBaseItemLabelsVisible(true);
		localLineAndShapeRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());

		if (str_y.length > 8) {
			//������б �����/2013-06-09��
			CategoryAxis domainAxis = categoryplot.getDomainAxis();
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
		}

		//���ñ���ɫ������ɫ �����/2013-06-09��
		categoryplot.setBackgroundPaint(Color.white);
		categoryplot.setRangeGridlinePaint(Color.lightGray);

		ChartPanel charPanel = new ChartPanel(chart); //
		//charPanel.setMouseWheelEnabled(true); //ͼ��ת�� �������� �����/2013-06-09��
		charPanel.putClientProperty("ChartType", "����ͼ"); //
		charPanel.putClientProperty("Title", taskVO.getDefineVO().getTitle()); //
		charPanel.putClientProperty("SeriesX", str_x); //
		charPanel.putClientProperty("SeriesY", str_y); //
		charPanel.putClientProperty("Data", ld_value); ///
		charPanel.addMouseListener(mouseListener); //
		return charPanel; //
	}

	//��ɫ������ͼ �����/2013-06-14��
	private JPanel getLineChartPanel_R(BillChartRangVO[] rangVO) {
		String[] str_x = null; //;
		String[] str_y = null; //
		double[][] ld_value = null; //
		HashVO[] hvs = taskVO.getDataVOs(); //���ɵ�����
		if (hvs == null || hvs.length == 0) { //���û������ʹ��Demo����!!
			Object[] objs = getBarOrLineDemoData(); //
			str_x = (String[]) objs[0];
			str_y = (String[]) objs[1];
			ld_value = (double[][]) objs[2];
		} else { //
			Object[] objs = getBarOrLineData(hvs); //
			str_x = (String[]) objs[0];
			str_y = (String[]) objs[1];
			ld_value = (double[][]) objs[2]; //
		}
		DefaultCategoryDataset dataSet = createCategoryDataset(str_x, str_y, ld_value); //
		HashMap config = taskVO.getDefineVO().getOtherconfig();

		String X_name = "X��";
		String Y_name = "Y��";

		if (config.containsKey("X��")) {
			X_name = (String) config.get("X��");
		}
		if (config.containsKey("Y��")) {
			Y_name = (String) config.get("Y��");
		}
		org.jfree.chart.JFreeChart chart = ChartFactory.createLineChart("", X_name, Y_name, dataSet, PlotOrientation.VERTICAL, true, true, false);
		chart.setBackgroundPaint(Color.white); //

		/*		StandardLegend standardlegend = (StandardLegend) chart.getLegend();
				standardlegend.setDisplaySeriesShapes(true);
				standardlegend.setShapeScaleX(1.5D);
				standardlegend.setShapeScaleY(1.5D);
				standardlegend.setDisplaySeriesLines(true);*/
		CategoryPlot categoryplot = chart.getCategoryPlot();
		NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
		numberaxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		numberaxis.setAutoRangeIncludesZero(true);
		numberaxis.setUpperMargin(0.20000000000000001D);
		numberaxis.setLabelAngle(1.5707963267948966D);
		LineAndShapeRenderer lineandshaperenderer = (LineAndShapeRenderer) categoryplot.getRenderer();
		lineandshaperenderer.setDrawOutlines(true);
		//���ֻ������ǰ3�����߷�ʽ 3������ȫ��ʵ�� ��ע�� ͳһ��ʵ�� �����/2013-06-09��
		/*		lineandshaperenderer.setSeriesStroke(0, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 10F, 6F }, 0.0F));
				lineandshaperenderer.setSeriesStroke(1, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 6F, 6F }, 0.0F));
				lineandshaperenderer.setSeriesStroke(2, new BasicStroke(2.0F, 1, 1, 1.0F, new float[] { 2.0F, 6F }, 0.0F));*/
		lineandshaperenderer.setBaseItemLabelsVisible(true);
		lineandshaperenderer.setBasePositiveItemLabelPosition(new ItemLabelPosition());
		lineandshaperenderer.setBaseNegativeItemLabelPosition(new ItemLabelPosition());

		LineAndShapeRenderer localLineAndShapeRenderer = (LineAndShapeRenderer) categoryplot.getRenderer();
		for (int i = 0; i < str_x.length; i++) {
			//׷�����ϵ���״ �����/2013-06-09��
			localLineAndShapeRenderer.setSeriesShapesVisible(i, true);
		}

		//׷�ӵ������� �����/2013-06-09��
		localLineAndShapeRenderer.setBaseShapesVisible(true);
		localLineAndShapeRenderer.setBaseItemLabelsVisible(true);
		localLineAndShapeRenderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());

		if (str_y.length > 8) {
			//������б �����/2013-06-09��
			CategoryAxis domainAxis = categoryplot.getDomainAxis();
			domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
		}

		//���ñ���ɫ������ɫ �����/2013-06-09��
		categoryplot.setBackgroundPaint(Color.white);
		categoryplot.setRangeGridlinePaint(Color.lightGray);

		//��ɫ������ �����/2013-06-14��
		if (rangVO == null) {
			rangVO = new BillChartRangVO[3];
			rangVO[0] = new BillChartRangVO(0, 50, "�ͷ���", Color.green);
			rangVO[1] = new BillChartRangVO(50, 80, "�з���", Color.yellow);
			rangVO[2] = new BillChartRangVO(80, 100, "�߷���", Color.red);
		}

		//������ɫ�� �����/2013-06-14��
		for (int i = 0; i < rangVO.length; i++) {
			BillChartRangVO rv = rangVO[i];
			IntervalMarker localIntervalMarker = new IntervalMarker(rv.getStart_border(), rv.getEnd_border()); //����
			localIntervalMarker.setLabel(rv.getRang_text()); //����
			localIntervalMarker.setLabelAnchor(RectangleAnchor.LEFT);
			localIntervalMarker.setLabelTextAnchor(TextAnchor.CENTER_LEFT);
			localIntervalMarker.setPaint(rv.getColor()); //��ɫ
			categoryplot.addRangeMarker(localIntervalMarker, Layer.BACKGROUND);
		}

		ChartPanel charPanel = new ChartPanel(chart); //
		//charPanel.setMouseWheelEnabled(true); //ͼ��ת�� �������� �����/2013-06-09��
		charPanel.putClientProperty("ChartType", "����ͼ"); //
		charPanel.putClientProperty("Title", taskVO.getDefineVO().getTitle()); //
		charPanel.putClientProperty("SeriesX", str_x); //
		charPanel.putClientProperty("SeriesY", str_y); //
		charPanel.putClientProperty("Data", ld_value); ///
		charPanel.addMouseListener(mouseListener); //
		return charPanel; //
	}

	/**
	 * ��ͼ
	 * @return
	 */
	private JPanel getPieChartPanel() {
		String[] str_x = null; //
		double[][] ld_value = null; //
		HashVO[] hvs = taskVO.getDataVOs(); //���ɵ�����
		if (hvs == null || hvs.length == 0) { //���û������ʹ��Demo����!!
			Object[] objs = getPieDemoData(); //
			str_x = (String[]) objs[0];
			ld_value = (double[][]) objs[1];
		} else { //
			Object[] objs = getPieData(hvs); //��ñ�ͼ������!!!
			str_x = (String[]) objs[0];
			ld_value = (double[][]) objs[1];
		}
		DefaultPieDataset dataset = createPieDataset(str_x, ld_value); //
		org.jfree.chart.JFreeChart chart = ChartFactory.createPieChart3D("", dataset, true, true, false);
		chart.setBackgroundPaint(Color.WHITE);
		PiePlot plot = (PiePlot) chart.getPlot();
		plot.setLabelPaint(PiePlot.DEFAULT_LABEL_OUTLINE_PAINT);
		plot.setNoDataMessage("No data available");
		//((CategoryItemRenderer) plot).setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator()); //
		//׷�Ӱٷֱ� �����/2013-06-09��
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} ({2})"));
		for (int p = 0; p < str_x.length; p++) {
			plot.setSectionPaint(p, new GradientPaint(0.0f, 0.0F, beautifulColors[p], 0.0f, WIDTH, beautifulColors[p])); //
		}

		//���ñ���ɫ �����/2013-06-09��
		plot.setBackgroundPaint(Color.white);

		ChartPanel charPanel = new ChartPanel(chart); //
		charPanel.setMouseWheelEnabled(true); //ͼ��ת�� �������� �����/2013-06-09��
		charPanel.putClientProperty("ChartType", "��ͼ"); //
		charPanel.putClientProperty("Title", taskVO.getDefineVO().getTitle()); //
		charPanel.putClientProperty("SeriesX", new String[] { "��ֵ" }); //
		charPanel.putClientProperty("SeriesY", str_x); //
		charPanel.putClientProperty("Data", ld_value); ///
		charPanel.addMouseListener(mouseListener); //
		return charPanel; //
	}

	//��Ϊ����ͼ������ͼ��������һ����,���Ը��һ������,�Ա�����!!!
	private Object[] getBarOrLineData(HashVO[] hvs) {
		LinkedHashSet xSet = new LinkedHashSet(); //
		LinkedHashSet ySet = new LinkedHashSet(); //
		HashMap dataMap = new HashMap(); //
		for (int i = 0; i < hvs.length; i++) {
			String str_key_1 = hvs[i].getStringValue(0); //
			String str_key_2 = hvs[i].getStringValue(1); //
			double ld_count = convertDoubleValue(hvs[i].getStringValue(2)); //
			xSet.add(str_key_1); //
			ySet.add(str_key_2); //

			String str_key_span = "#" + str_key_1 + "#" + str_key_2 + "#"; //���ڴ���ֵ�ı��!!!
			if (!dataMap.containsKey(str_key_span)) {
				dataMap.put(str_key_span, ld_count); //
			} else {
				double oldCount = (Double) dataMap.get(str_key_span); //
				ld_count = ld_count + oldCount; //
				dataMap.put(str_key_span, ld_count); //��������!!!
			}
		}

		String[] str_x = (String[]) xSet.toArray(new String[0]); //
		String[] str_y = (String[]) ySet.toArray(new String[0]); //
		double[][] ld_values = new double[str_x.length][str_y.length]; //
		for (int i = 0; i < str_x.length; i++) { //
			for (int j = 0; j < str_y.length; j++) { //
				String str_key_span = "#" + str_x[i] + "#" + str_y[j] + "#"; //���ڴ���ֵ�ı��!!!
				Double ld_count = (Double) dataMap.get(str_key_span); ////
				ld_values[i][j] = (ld_count == null ? 0 : ld_count); //
			}
		}
		return new Object[] { str_x, str_y, ld_values }; //
	}

	//ȡ�ñ�ͼ����
	private Object[] getPieData(HashVO[] hvs) {
		LinkedHashSet xSet = new LinkedHashSet(); //
		HashMap dataMap = new HashMap(); //
		for (int i = 0; i < hvs.length; i++) {
			String str_key_1 = hvs[i].getStringValue(0); //
			double ld_count = convertDoubleValue(hvs[i].getStringValue(2)); //ͬ��ȡ��3��,�������Ա�֤������ͼֱ�����ͼ����!
			xSet.add(str_key_1); //
			if (!dataMap.containsKey(str_key_1)) {
				dataMap.put(str_key_1, ld_count); //
			} else {
				double oldCount = (Double) dataMap.get(str_key_1); //
				ld_count = ld_count + oldCount; //
				dataMap.put(str_key_1, ld_count); //��������!!!
			}
		}

		String[] str_x = (String[]) xSet.toArray(new String[0]); //
		double[][] ld_values = new double[1][str_x.length]; //
		for (int i = 0; i < str_x.length; i++) {
			Double ld_count = (Double) dataMap.get(str_x[i]); ////
			ld_values[0][i] = (ld_count == null ? 0 : ld_count); //
		}
		return new Object[] { str_x, ld_values }; //
	}

	//����Demo����!!!
	private Object[] getBarOrLineDemoData() {
		String[] str_x = new String[] { "�߷���", "�еȷ���", "�ͷ���" }; //
		String[] str_y = new String[] { "2008��", "2009��", "2010��", "2011��" }; //
		Random ranDom = new Random(); //
		double[][] ld_values = new double[str_x.length][str_y.length]; //
		for (int i = 0; i < str_x.length; i++) {
			for (int j = 0; j < str_y.length; j++) {
				ld_values[i][j] = ranDom.nextInt(100); //
			}
		}
		return new Object[] { str_x, str_y, ld_values }; //
	}

	//��ͼ��Demo����!!!
	private Object[] getPieDemoData() {
		String[] str_x = new String[] { "2008��", "2009��", "2010��", "2011��", "2012��" }; //
		double[][] ld_values = new double[1][str_x.length]; //
		Random ranDom = new Random(); //
		for (int i = 0; i < str_x.length; i++) {
			ld_values[0][i] = ranDom.nextInt(100); //
		}
		return new Object[] { str_x, ld_values }; //
	}

	private DefaultCategoryDataset createCategoryDataset(String[] str_x, String[] str_y, double[][] ld_value) {
		DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
		for (int i = 0; i < str_x.length; i++) {
			for (int j = 0; j < str_y.length; j++) {
				dataSet.addValue(ld_value[i][j], str_x[i], str_y[j]);
			}
		}
		return dataSet; //
	}

	private DefaultPieDataset createPieDataset(String[] str_x, double[][] ld_value) {
		DefaultPieDataset dataSet = new DefaultPieDataset();
		for (int i = 0; i < str_x.length; i++) {
			if (str_x[i] == null)
				continue;
			dataSet.setValue(str_x[i], ld_value[0][i]); //
		}
		return dataSet; //
	}

	private double convertDoubleValue(String _text) {
		if (_text == null || _text.trim().equals("")) { //
			return 0; //
		}
		boolean isNumber = tbUtil.isStrAllNunbers(_text); //
		if (isNumber) {
			return Double.parseDouble(_text); //
		} else {
			return 0; //
		}
	}

	/**
	 * �Զ�������ɫ.Render..
	 * @param _chart
	 */
	private void setChartAsCustRender(JFreeChart _chart) {
		PlotOrientation orientation = _chart.getCategoryPlot().getOrientation();
		int li_colcount = _chart.getCategoryPlot().getDataset().getColumnCount(); //
		BarRenderer barRender = new CustomBarRenderer(getAllColumnColors(li_colcount)); //
		barRender.setSeriesPaint(0, new GradientPaint(0.0f, 0.0F, beautifulColors[(0) % 38], 0.0f, WIDTH, beautifulColors[(0) % 38].darker().darker()));
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

	private org.jfreechart.chart.ChartPanel getDialPlotPanel(String _topName, String _bottomName, double _realValue, double _value1, double _value2, double _value3, double _value4, Color _bgColor, String _tip) {
		boolean _type = true;
		if (_value2 > _value3) {
			_type = false;
			double _zj = _value2;
			_value2 = _value3;
			_value3 = _zj;
		}
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

		StandardDialRange standarddialrange = new StandardDialRange(_value3, maxValue, _type ? Color.red : Color.green);//�ж�Ԥ��ֵ�Ƿ��������ֵ�����/2012-03-15��
		standarddialrange.setInnerRadius(0.58999999999999997D);
		standarddialrange.setOuterRadius(0.58999999999999997D);
		dialplot.addLayer(standarddialrange);

		StandardDialRange standarddialrange1 = new StandardDialRange(_value2, _value3, Color.orange);
		standarddialrange1.setInnerRadius(0.58999999999999997D);
		standarddialrange1.setOuterRadius(0.58999999999999997D);
		dialplot.addLayer(standarddialrange1);

		StandardDialRange standarddialrange2 = new StandardDialRange(_value1, _value2, _type ? Color.green : Color.red);//�ж�Ԥ��ֵ�Ƿ��������ֵ�����/2012-03-15��
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
		double[][] ld_value = new double[][] { { _realValue, _value1, _value2, _value3, _value4 } };
		org.jfreechart.chart.ChartPanel charPanel = new org.jfreechart.chart.ChartPanel(jfreechart);
		charPanel.setToolTipText(_tip); //
		charPanel.putClientProperty("ChartType", "�Ǳ���ͼ"); //
		charPanel.putClientProperty("Title", taskVO.getDefineVO().getTitle()); //
		charPanel.putClientProperty("SeriesX", new String[] { "��ֵ" }); //
		if (_type) {
			charPanel.putClientProperty("SeriesY", new String[] { "ʵ��ֵ", "��Сֵ", "����ֵ", "����ֵ", "���ֵ" }); //
		} else {
			charPanel.putClientProperty("SeriesY", new String[] { "ʵ��ֵ", "��Сֵ", "����ֵ", "����ֵ", "���ֵ" }); //	
		}
		charPanel.putClientProperty("Data", ld_value); //
		charPanel.addMouseListener(mouseListener); //
		return charPanel; //
	}

	private double getDefaultMaxNum(double _currValue, double _minValue) {
		return (double) 30 * ((int) _currValue / 30 + 1) + _minValue;
	}

	/**
	 * �������!!!
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_refresh) {
			onRefresh(); //ˢ��
		} else if (e.getSource() == btn_more) {
			onMore(); //ˢ��
		}
	}

	private void onRefresh() {
		try {
			btn_refresh.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			SysAppServiceIfc service = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			this.taskVO = service.getDeskTopNewGroupVOs(ClientEnvironment.getCurrLoginUserVO().getCode(), this.taskVO.getDefineVO().getTitle(), false); //����!!
			this.removeAll(); //
			this.loadRealPanel(); //
			this.updateUI(); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		} finally {
			btn_refresh.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
		}
	}

	private void onMore() {
		try {
			btn_more.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			String str_linkMenu = taskVO.getDefineVO().getLinkmenu(); //
			if (str_linkMenu == null) {
				MessageBox.show(this, "��Ҫ������ת���ĸ����ܵ�!"); //
				return;
			}
			final WorkTabbedPanel panel = cn.com.infostrategy.ui.sysapp.login.DeskTopPanel.deskTopPanel.getOpenAppMainPanel(str_linkMenu, null);
			String str_dbstyle = ClientEnvironment.getDeskTopStyle(); //���ݿ��д洢��
			String[][] str_df = getDeskTopAndIndexStyleMartix(); //
			int index = 1;
			for (int i = 0; i < str_df.length; i++) {
				for (int j = 0; j < str_df[i].length; j++) {
					if (str_df[i][j].equals(str_dbstyle)) { //�����
						index = i + 1; //�����к�!
						break;
					}
				}
			}
			if (index == 3 || index == 4) {
				BillFrame frame = new BillFrame(this, 1024, 800);
				frame.getContentPane().add(panel);
				frame.setVisible(true);
			} else {
				DeskTopPanel.deskTopPanel.openAppMainFrameWindowById(str_linkMenu); //
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		} finally {
			btn_more.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
		}
	}

	private String[][] getDeskTopAndIndexStyleMartix() {
		return new String[][] { //
		{ "A", "B", "C", "D" }, //
				{ "a", "b", "c", "d" }, //
				{ "1", "2", "3", "4" }, //char A1
				{ "H", "I", "G", "K" }, //
				{ "O", "P", "Q", "R" } }; //
	}

	public void onClicked(Object _evtSource) {
		try {
			JPanel chartPanel = (JPanel) _evtSource; //
			String str_chartType = (String) chartPanel.getClientProperty("ChartType"); //
			String str_title = (String) chartPanel.getClientProperty("Title"); //
			String[] str_x = (String[]) chartPanel.getClientProperty("SeriesX"); //
			String[] str_y = (String[]) chartPanel.getClientProperty("SeriesY"); //
			double[][] ld_data = (double[][]) chartPanel.getClientProperty("Data"); //

			int li_dialogwidth = 900; //
			int li_dialoghheight = 600; //
			int li_splitdivlocation = 400; //
			if ("�Ǳ���ͼ".equals(str_chartType)) {
				li_dialogwidth = 500; //
				li_dialoghheight = 700; //
				li_splitdivlocation = 500; //
			}
			OpenChartDialog openDialog = null; //
			if (chartPanel instanceof org.jfree.chart.ChartPanel) {
				org.jfree.chart.JFreeChart chart = ((org.jfree.chart.ChartPanel) chartPanel).getChart(); //
				org.jfree.chart.JFreeChart cloneChart = (org.jfree.chart.JFreeChart) chart.clone(); //
				if (!"�Ǳ���ͼ".equals(str_chartType)) {
					cloneChart.setTitle(str_title); //
				}
				org.jfree.chart.ChartPanel newChartPanel = new org.jfree.chart.ChartPanel(cloneChart); //
				newChartPanel.setMouseWheelEnabled(true); //ͼ��ת�� �������� �����/2013-06-09��
				openDialog = new OpenChartDialog(this, newChartPanel, str_title, str_x, str_y, ld_data, li_dialogwidth, li_dialoghheight, li_splitdivlocation); //
			} else if (chartPanel instanceof org.jfreechart.chart.ChartPanel) {
				org.jfreechart.chart.JFreeChart chart = ((org.jfreechart.chart.ChartPanel) chartPanel).getChart(); //
				org.jfreechart.chart.JFreeChart cloneChart = (org.jfreechart.chart.JFreeChart) chart.clone(); //
				if (!"�Ǳ���ͼ".equals(str_chartType)) {
					cloneChart.setTitle(str_title); //
				}
				org.jfreechart.chart.ChartPanel newChartPanel = new org.jfreechart.chart.ChartPanel(cloneChart); //
				newChartPanel.setMouseWheelEnabled(true); //ͼ��ת�� �������� �����/2013-06-09��
				openDialog = new OpenChartDialog(this, newChartPanel, str_title, str_x, str_y, ld_data, li_dialogwidth, li_dialoghheight, li_splitdivlocation); //
			}
			openDialog.setVisible(true); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	class CustomBarRenderer extends BarRenderer {
		private static final long serialVersionUID = 1L;
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
			setSeriesFillPaint(series, paint, true);
		}
	}

	/**
	 * ���ͼ��ʱ�Ŵ���ʾͼ��!!!
	 * @author xch
	 *
	 */
	class OpenChartDialog extends BillDialog {

		private static final long serialVersionUID = 1L; ////

		private OpenChartDialog(java.awt.Container _parent, org.jfree.chart.ChartPanel _newChartPanel, String _title, String[] _x, String[] _y, double[][] _data, int _width, int _dialogwidth, int _splitdivlocation) {
			super(_parent, _title, _width, _dialogwidth); //
			initialize(_newChartPanel, _title, _x, _y, _data, _splitdivlocation); //
		}

		private OpenChartDialog(java.awt.Container _parent, org.jfreechart.chart.ChartPanel _newChartPanel, String _title, String[] _x, String[] _y, double[][] _data, int _width, int _dialogwidth, int _splitdivlocation) {
			super(_parent, _title, _width, _dialogwidth); //
			initialize(_newChartPanel, _title, _x, _y, _data, _splitdivlocation); //
		}

		private void initialize(JPanel _newChartPanel, String _title, String[] _x, String[] _y, double[][] _data, int _splitdivlocation) {
			try {
				String[][] str_data = new String[_data.length][_y.length + 1]; //
				for (int i = 0; i < str_data.length; i++) {
					str_data[i][0] = _x[i]; //
					for (int j = 1; j < str_data[i].length; j++) {
						str_data[i][j] = "" + _data[i][j - 1];
					}
				}
				String[] str_new_y = new String[_y.length + 1]; //
				str_new_y[0] = ""; //
				System.arraycopy(_y, 0, str_new_y, 1, _y.length); //����!!!
				JTable table = new JTable(str_data, str_new_y); //
				table.setOpaque(false); //
				table.getTableHeader().setOpaque(false); //
				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //
				for (int i = 0; i < table.getColumnCount(); i++) {
					if (i == 0) {
						table.getColumnModel().getColumn(i).setPreferredWidth(60); //
					} else {
						table.getColumnModel().getColumn(i).setPreferredWidth(80); //
					}
				}
				table.getTableHeader().setOpaque(false); //͸��!!!
				JScrollPane scroll = new JScrollPane(table); //
				scroll.setOpaque(false);
				scroll.getViewport().setOpaque(false); //

				JViewport jv2 = new JViewport();
				jv2.setOpaque(false); //һ��Ҫ��һ�£������ϱ����и�����
				jv2.setView(table.getTableHeader());
				scroll.setColumnHeader(jv2); //

				WLTSplitPane split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, _newChartPanel, scroll); //
				split.setDividerLocation(_splitdivlocation); //
				split.setOpaque(false); //
				JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
				contentPanel.add(split); //

				this.getContentPane().add(contentPanel); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}

	}

	protected void paintBorder(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
		IconFactory.getInstance().getPanelQueryItem_BG().draw(g, 0, 0, getWidth(), getHeight());
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
	}
	/**
	 * zzl ���ҹ���������
	 * @return
	 */
	public JPanel getIndexGDPanel(JPanel _deskTopPanel, DeskTopNewGroupVO _taskVO){
		final String str=_taskVO.getDefineVO().getDescr();
		text = new JPanel(new BorderLayout()){
			private static final long serialVersionUID = 7633828902009421639L;
			@Override
			public void paint(Graphics g) {
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(200, 210, getWidth(), getHeight());
				g.setColor(Color.blue);
				Font font = new Font("����", Font.PLAIN, 50);
				g.setFont(font);
				FontMetrics fm = g.getFontMetrics();
				int strWidth = fm.stringWidth(str);							// �õ��ַ����Ŀ��
				int width = text.getWidth();								// �õ���ʾ���Ŀ��
				if(x < 0 && Math.abs(x) > width+strWidth){
					x = 730;
				}
				g.drawString(str, x, 80);
//				g.drawString(str, strWidth + 10+ x + str.length() , 80);
			}
			@Override
			public void update(Graphics g) {
				paint(g);
			}
		};
		text.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); //
//		text.setPreferredSize(new Dimension(660, 45));
		text.setBackground(LookAndFeel.defaultShadeColor1); //new Color(226, 236, 246)
		text.setLayout(new BorderLayout()); //
		text.setOpaque(false); //͸��
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(true){
						// �ı���ʾ��λ��
						x = x - 4;
						text.updateUI();
						repaint();
						Thread.sleep(50);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
		return text;
	}
}
