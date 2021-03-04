package cn.com.infostrategy.ui.report;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.mdata.JepFormulaParseAtUI;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_BigArea;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_RowNumber;
import cn.com.infostrategy.ui.report.cellcompent.AttributiveCellEditor;
import cn.com.infostrategy.ui.report.cellcompent.AttributiveCellRenderer;
import cn.com.infostrategy.ui.report.cellcompent.AttributiveCellTableModel;
import cn.com.infostrategy.ui.report.cellcompent.CellAttribute;
import cn.com.infostrategy.ui.report.cellcompent.CellSpan;
import cn.com.infostrategy.ui.report.cellcompent.CellTMO;
import cn.com.infostrategy.ui.report.cellcompent.MultiSpanCellTable;
import cn.com.infostrategy.ui.report.cellcompent.OpenCellTempletDialog;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;

/**
 * 像Excel一样的一个设计器,可以保存一个二维的网格数据与风格 一个包括所有功能:合并/分割;改字体;改颜色的例子
 * 
 * @author xch
 * 
 */
public class BillCellPanel extends BillPanel implements ActionListener {

	private static final long serialVersionUID = -8764511718458388278L;

	// 控件类型
	public static String ITEMTYPE_LABEL = "LABEL"; // 纯文本
	public static String ITEMTYPE_TEXT = "TEXT"; // 纯文本
	public static String ITEMTYPE_NUMBERTEXT = "NUMBERTEXT"; // 数字框..
	public static String ITEMTYPE_TEXTAREA = "TEXTAREA"; // 多行文本框
	public static String ITEMTYPE_CHECKBOX = "CHECKBOX"; // 勾选框
	public static String ITEMTYPE_COMBOBOX = "COMBOBOX"; // 下拉框
	public static String ITEMTYPE_DATE = "DATE"; // 日期
	public static String ITEMTYPE_DATETIME = "DATETIME"; // 时间

	private BillCellVO billCellVO = null; //
	private BillCellItemVO[][] billCellItemVos = null;

	private String templetcode = null; // 模板编码
	private String templetname = null; // 模板名称
	private String billNo = null; // 单据编码
	private String descr = null; // 备注

	private AttributiveCellTableModel tablemodel = null; // 数据模型对象..
	private CellAttribute cellAtt = null;

	private JScrollPane scroll = null; //
	private MultiSpanCellTable table = null; // 表格对象,最核心的对象.

	private JTable rowHeaderTable = null;
	private JToolBar toolbar = null;
	private JButton btn_new, btn_save, btn_open, btn_exportsql = null;
	private JButton btn_combine, btn_split = null;

	private JButton btn_forecolor, btn_backcolor; //
	private JComboBox combox_fonttype, combox_fontstyle, combox_fontsize, combox_celltype;

	private FrameWorkMetaDataServiceIfc metaservice = null;

	private JButton btn_left, btn_center, btn_right, btn_top, btn_middle, btn_bottom; //
	private JCheckBox checkbox_showCellKey = null; //

	private JMenuItem popMenuItem_insertRow, popMenuItem_insertCol, popMenuItem_delRow, popMenuItem_delCol, popMenuItem_cleardata, popMenuItem_loc, popMenuItem_unloc;
	private JMenuItem popMenuItem_setTooltipText, popMenuItem_setCellEditable, popMenuItem_setCellKey, item_AutoSetCellKey, popMenuItem_setCellEditFormula, popMenuItem_setCellValidateFormula, popMenuItem_setCellIsHtmlHref, popMenuItem_setCellDesc, popMenuItem_showCellProp; //
	private boolean isAllowShowPopMenu = true; //是否允许弹出右键菜单

	private JPanel mainPanel = null; // 主面板

	private JTextField textfield_status = null; //

	private boolean bo_isshowline = true; //
	private boolean bo_isshowtoolbar = true; //
	private boolean bo_isshowheader = true; //是否显示行头与列头!!

	private JPopupMenu popMenu = null; //
	private JPopupMenu custPopMenu = null; //

	private JepFormulaParseAtUI formulaParse = null; //

	private Vector vc_HtmlHrefListrners = new Vector(); //点击超链的事件

	private boolean ifSetRowHeight = true;//是否设置行高 因为jtable设置行高的时候会重绘 有性能问题

	/**
	 * 武坤萌更新代码 以下是初始化EXCEL文件的相关属性
	 */
	private JButton btn_exportExcel = null; // 导出Excel按扭
	private JButton btn_inputExcel = null; // 导入Excel按扭
	private JButton btn_exportXML = null; // 导出XML
	private JButton btn_importXML = null; // 导入XML
	private HashVO[] excelHash = null;
	private HashVO[] cellHash = null;
	private JTextArea textArea = null;
	private BillDialog dialog = null;
	// private String defaultPath = "C:/";
	private String fileExistsError = "该文件已经存在，是否覆盖？"; // 文件存在错误提示信息
	private String defaultSheetName = "SheetName"; // 缺省的sheet名称
	private String successMessage = "保存成功！\n文件路径为："; // 成功提示信息
	private String billInitException = "BillCellItemVO初始化异常"; // 异常信息提示
	private String cellSpanError = "单元格合并返回值错误！"; // 单元格合并返回值错误信息
	private String lookAtErrorInformation = "请在后台查看错误信息！"; // 错误信息查看提示
	private String messageTempletcodeNull = "模版为空，请选择一个模版！"; // 空模版错误信息
	private String fileNotExists = "指定文件不存在！"; // 文件不存在错误信息提示

	private HSSFRow row = null; // 定义行
	private HSSFCell cell = null; // 定义列

	private int currMouseOverRow = -1, currMouseOverCol = -1; //当前鼠标移上去时的行号
	private JTableLockManager jtm = null;
	private boolean isLocked = false;

	/**
	 * 默认
	 */
	public BillCellPanel() {
		this(null, null, null);
	}

	/**
	 * 
	 * @param _templetCode
	 *            模板编码
	 */
	public BillCellPanel(String _templetCode) {
		this(_templetCode, null, null);
	}

	//直接根据一个HashVO构造一个CellPanel
	public BillCellPanel(HashVO[] _hvs) {
		String[] str_keys = _hvs[0].getKeys(); //
		BillCellVO cellVO = new BillCellVO(); //
		cellVO.setRowlength(_hvs.length + 1); //
		cellVO.setCollength(str_keys.length); //
		BillCellItemVO[][] itemVOs = new BillCellItemVO[_hvs.length + 1][str_keys.length]; //
		for (int j = 0; j < str_keys.length; j++) {
			itemVOs[0][j] = new BillCellItemVO(); //
			itemVOs[0][j].setCelltype(BillCellPanel.ITEMTYPE_TEXTAREA); //
			itemVOs[0][j].setCellvalue(str_keys[j]); //
			itemVOs[0][j].setBackground("255,215,255"); //
			itemVOs[0][j].setRowheight("32");
			itemVOs[0][j].setColwidth("100"); //
			itemVOs[0][j].setHalign(2); //
			itemVOs[0][j].setValign(2); //
		}

		for (int i = 1; i < itemVOs.length; i++) {
			for (int j = 0; j < itemVOs[0].length; j++) {
				itemVOs[i][j] = new BillCellItemVO(); //
				itemVOs[i][j].setCelltype(BillCellPanel.ITEMTYPE_TEXTAREA); //
				itemVOs[i][j].setCellvalue(_hvs[i - 1].getStringValue(str_keys[j])); //
				itemVOs[i][j].setRowheight("25");
				itemVOs[i][j].setColwidth("100"); //
				itemVOs[i][j].setValign(2); //

				String str_bgcolor = (String) _hvs[i - 1].getUserObject("bgcolor@" + str_keys[j]); //
				if (str_bgcolor != null && !str_bgcolor.trim().equals("")) { //如果不为空!
					itemVOs[i][j].setBackground(str_bgcolor); //
				}
			}
		}
		cellVO.setCellItemVOs(itemVOs); //
		initialize(cellVO); //
	}

	/**
	 * 直接根据BillCEllVO生成一个平台的Excel表格
	 * 
	 * @param _cellVO
	 */
	public BillCellPanel(BillCellVO _cellVO) {
		initialize(_cellVO); //
	}

	public BillCellPanel(BillCellVO _cellVO, boolean _showline, boolean _isShowHeader) {
		this.bo_isshowline = _showline;
		this.bo_isshowheader = _isShowHeader; //
		initialize(_cellVO); //
	}

	/**
	 * 
	 * @param _templetCode
	 *            模板编码
	 */
	public BillCellPanel(String _templetCode, boolean _showline) {
		this(_templetCode, null, null, _showline);
	}

	/**
	 * 
	 * @param _templetCode
	 *            模板编码
	 */
	public BillCellPanel(String _templetCode, boolean _showline, boolean _showtoolbar) {
		this(_templetCode, null, null, _showline, _showtoolbar);
	}

	public BillCellPanel(String _templetCode, boolean _showline, boolean _showtoolbar, boolean _isShowHeader) {
		this(_templetCode, null, null, _showline, _showtoolbar, _isShowHeader);
	}

	/**
	 * 
	 * @param _templetCode
	 *            模板编码
	 */
	public BillCellPanel(String _templetCode, boolean _showline, boolean _showtoolbar, int _row, int _col) {
		this(_templetCode, null, null, _showline, _showtoolbar, _row, _col);
	}

	public BillCellPanel(String _templetCode, String _billNo, String _descr) {
		this(_templetCode, _billNo, _descr, true, true);
	}

	public BillCellPanel(String _templetCode, String _billNo, String _descr, boolean _showline) {
		this(_templetCode, _billNo, _descr, _showline, true);
	}

	public BillCellPanel(String _templetCode, String _billNo, String _descr, boolean _showline, boolean _showtoolbar) {
		this(_templetCode, _billNo, _descr, _showline, _showtoolbar, true);
	}

	public BillCellPanel(String _templetCode, String _billNo, String _descr, boolean _showline, boolean _showtoolbar, boolean _isShowHeader) {
		this.templetcode = _templetCode; //
		this.billNo = _billNo; //
		this.descr = _descr; //
		this.bo_isshowline = _showline;
		this.bo_isshowtoolbar = _showtoolbar; //
		this.bo_isshowheader = _isShowHeader; //
		initialize(8, 8); //
	}

	public BillCellPanel(String _templetCode, String _billNo, String _descr, boolean _showline, boolean _showtoolbar, int _row, int _col) {
		this.templetcode = _templetCode; //
		this.billNo = _billNo; //
		this.descr = _descr; //
		this.bo_isshowline = _showline;
		this.bo_isshowtoolbar = _showtoolbar; //
		initialize(_row, _col); //
	}

	private void initialize(int _row, int _col) {
		this.setLayout(new BorderLayout());
		this.setOpaque(false); //透明!!!
		JPanel panel_root = new JPanel(); // 根面板..
		panel_root.setOpaque(false); //这是透明的!
		panel_root.setLayout(new BorderLayout()); //

		if (bo_isshowtoolbar) {
			panel_root.add(getTitleBtnBar(), BorderLayout.NORTH); // 加入按钮栏
		}else{
			panel_root.add(getTitleBtnBar(1), BorderLayout.NORTH); // 加入按钮栏
		}

		mainPanel = new JPanel(); //
		if (templetcode != null) {
			try {
				this.billCellVO = getMetaService().getBillCellVO(templetcode, billNo, descr); //
				initTable(billCellVO.getRowlength(), billCellVO.getRowlength()); //
				loadBillCellData(this.billCellVO); //
			} catch (Exception ex) {
				MessageBox.showException(this, ex); //
			}
		} else {
			this.billCellVO = new BillCellVO(); //
			initTable(_row, _col); //
		}

		panel_root.add(mainPanel, BorderLayout.CENTER); // 加入按钮栏..
		textfield_status = new JTextField(); //
		textfield_status.setEditable(false); //

		if (bo_isshowtoolbar) {
			panel_root.add(textfield_status, BorderLayout.SOUTH); // 加入状态栏..
		}

		// if (bo_isshowtoolbar) {
		// this.add(new JScrollPanel(panel_root)); //
		// } else {
		// this.add(panel_root); //
		// }

		this.add(panel_root); //
		initPopMenu();
	}

	private void initialize(BillCellVO _cellVO) {
		try {
			this.setLayout(new BorderLayout());
			mainPanel = new JPanel(new BorderLayout()); //
			this.add(mainPanel); //
			loadBillCellData(_cellVO); //
			initPopMenu(); // 初始化右键菜单
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 初始化表格.
	 * 
	 * @param _rowcount
	 * @param _colcount
	 */
	private void initTable(int _rowcount, int _colcount) {
		mainPanel.removeAll(); //
		mainPanel.setLayout(new BorderLayout()); //
		mainPanel.setOpaque(false); //透明!!

		tablemodel = new AttributiveCellTableModel(_rowcount, _colcount); // 创建一个表格
		tablemodel.addTableModelListener(new BillCellTableModelEditListener()); // 增加值变化事件
		cellAtt = tablemodel.getCellAttribute(); //

		table = new MultiSpanCellTable(tablemodel);
		isLocked = false;
		if (checkbox_showCellKey != null) {
			table.setShowCellKey(this.checkbox_showCellKey.isSelected());
		}
		if (!bo_isshowline || !bo_isshowheader) {
			table.setTableHeader(null); //
			if (!bo_isshowline) {
				table.setGridColor(Color.WHITE); //
			}
		}

		table.putClientProperty("isshowline", new Boolean(this.bo_isshowline)); // /
		table.setRowHeight(21); //
		if (table.getTableHeader() != null) {
			table.getTableHeader().setOpaque(false);
		}
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(true);
		table.setCellSelectionEnabled(true);
		table.setDefaultRenderer(Object.class, new AttributiveCellRenderer()); // 默认绘制器
		table.setDefaultEditor(Object.class, new AttributiveCellEditor(table, this)); //

		//表格的鼠标监听事件,搞成一个,会少生成一个内部类!!
		MouseAdapter tableMouseLn = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					onClickedTable(e.getSource(), e.getPoint(), e.isControlDown(), e.isShiftDown(), e.isAltDown()); //
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					showPopMenu(e.getSource(), e.getPoint()); //
				}
			}

			//鼠标在格子上移过的时候
			public void mouseMoved(MouseEvent e) {
				Point point = e.getPoint(); //
				onMouseMoveFromTable(point); //
			}

			public void mouseReleased(MouseEvent e) {
				selectedInfo();//单击列表单元格时，下方状态栏显示信息[YangQing/2013-09-25]
			}
		};
		table.addMouseListener(tableMouseLn); //监听!
		table.addMouseMotionListener(tableMouseLn); //移动监听!

		scroll = new JScrollPane(table); //
		if (!bo_isshowline) { //白色!! 如果不显示线,则将
			scroll.getViewport().setBackground(Color.WHITE); //
		}

		//table.getTableHeader().setOpaque(false);
		JViewport jv2 = new JViewport();
		jv2.setOpaque(false); //一定要设一下，否则上边总有个白条
		jv2.setView(table.getTableHeader());
		scroll.setColumnHeader(jv2); //

		if (!bo_isshowheader) { //如果不显示表头,即是像html白板上做报表一样,则设成透明的,而且是没有边框的
			scroll.setOpaque(false); //透明!!
			scroll.getViewport().setOpaque(false); //透明!!
			scroll.setBorder(BorderFactory.createEmptyBorder()); //空白边框!!
			table.setBorder(BorderFactory.createLineBorder(table.getGridColor(), 1)); //createMatteBorder(1, 1, 1, 1, table.getGridColor())); //表格边框!!!
		} else { //如果有表头
			table.setBorder(BorderFactory.createEmptyBorder()); //
		}

		JScrollBar newScrollBar = new JScrollBar(); //
		newScrollBar.setUnitIncrement(20); //
		scroll.setVerticalScrollBar(newScrollBar); //一定要重置一下,否则会出现向下滚不动的情况!!

		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); //
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); //

		//System.out.println("最小值:" + scroll.getVerticalScrollBar().getMinimum() + ",最大值:" + scroll.getVerticalScrollBar().getMaximum() );  //
		//如果是指定显示行头!!!
		if (bo_isshowheader && bo_isshowline) {
			DefaultTableColumnModel rowHeaderColumnModel = new DefaultTableColumnModel();
			rowHeaderColumnModel.addColumn(getRowNumberColumn()); // 加入行号列

			rowHeaderTable = new ResizableTable(tablemodel, rowHeaderColumnModel); // 创建新的表..
			//rowHeaderTable.setOpaque(false);  //透明!!!
			rowHeaderTable.setRowHeight(22);

			rowHeaderTable.setRowSelectionAllowed(true);
			rowHeaderTable.setColumnSelectionAllowed(false);
			rowHeaderTable.setIntercellSpacing(new Dimension(1, 1)); //
			// rowHeaderTable.setBackground(Color.red);
			rowHeaderTable.getTableHeader().setReorderingAllowed(false);
			rowHeaderTable.getTableHeader().setResizingAllowed(false);
			rowHeaderTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			rowHeaderTable.setSelectionModel(table.getSelectionModel());

			JViewport jv = new JViewport();
			jv.setOpaque(false); //透明!!
			jv.setView(rowHeaderTable);
			int li_height = new Double(rowHeaderTable.getMaximumSize().getHeight()).intValue();
			jv.setPreferredSize(new Dimension(37, li_height));

			scroll.setRowHeader(jv);
			scroll.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeaderTable.getTableHeader());
		}

		//((JComponent)scroll.getCorner(JScrollPane.c)).setOpaque(false);  ///
		scroll.updateUI(); //
		mainPanel.add(scroll, BorderLayout.CENTER); ////
		mainPanel.updateUI(); //
	}

	//设置是否透明
	public void setCrystal(boolean _isCrystal) {
		if (_isCrystal) {
			this.setOpaque(false); //
			scroll.setOpaque(false); //透明!!
			scroll.getViewport().setOpaque(false); //透明!!
			//scroll.setBorder(BorderFactory.createEmptyBorder()); //空白边框!!
			//table.setBorder(BorderFactory.createLineBorder(table.getGridColor(), 1)); //createMatteBorder(1, 1, 1, 1, table.getGridColor())); //表格边框!!!
		} else {
			scroll.setOpaque(true); //不透明!!
			scroll.getViewport().setOpaque(true); //不透明!!
			scroll.setBorder(BorderFactory.createEmptyBorder()); //空白边框!!
			table.setBorder(BorderFactory.createEmptyBorder()); //
		}
	}

	//当鼠标在表格上移动时!!!
	private void onMouseMoveFromTable(Point _point) {
		table.setMouseMovingRow(-1); //清空
		table.setMouseMovingCol(-1); //清空
		int li_row = table.rowAtPoint(_point); //得到光标所在行
		int li_col = table.columnAtPoint(_point); //得到光标所在列
		if (li_row >= 0 && li_col >= 0) { ////
			table.setMouseMovingRow(li_row); //设置
			table.setMouseMovingCol(li_col); //设置

			boolean isHand = false; //是否要手形
			currMouseOverRow = li_row; //当前当标的行号!!
			currMouseOverCol = li_col; //当前光标的列号!!
			try {
				BillCellItemVO itemVO = getBillCellItemVOAt(li_row, li_col); ////
				if (itemVO != null && "Y".equals(itemVO.getIshtmlhref()) && !itemVO.getCellvalue().trim().equals("")) { //如果是指定了Html显示
					isHand = true; //!!!
				}
			} catch (Exception ex) {
				System.err.println(ex.getMessage()); //输出异常!!!
			}

			if (isHand) { //如果是手形,则将光标输出成手形!!!
				table.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
			} else {
				table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
			}
		}
		table.revalidate();
		table.repaint();
	}

	private TableColumn getRowNumberColumn() {
		TableCellRenderer render = new RowNumberRender(); // 创建列绘制器..
		ListCellEditor_RowNumber editor = new ListCellEditor_RowNumber();
		TableColumn rowNumberColumn = new TableColumn(0, 37, render, editor); // 创建列,对应第一列数据

		if (ClientEnvironment.getInstance().isEngligh()) {
			rowNumberColumn.setHeaderValue("No."); //
		} else {
			rowNumberColumn.setHeaderValue("序号"); //
		}

		return rowNumberColumn;
	}

	/**
	 *
	 * @return
	 */
	public JToolBar getTitleBtnBar() { //
		if (toolbar == null) {
			JPanel panel_1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0)); //
			JPanel panel_2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0)); //

			toolbar = new JToolBar("Btn", JToolBar.HORIZONTAL); //
			toolbar.setBackground(LookAndFeel.systembgcolor); //
			toolbar.setLayout(new BorderLayout(0, 2)); //
			toolbar.setFloatable(false);
			toolbar.setMargin(new Insets(1, 1, 3, 1));
			toolbar.setRollover(true); //

			btn_new = new WLTButton("新建"); //
			btn_save = new WLTButton("保存"); //
			btn_open = new WLTButton("打开"); //
			btn_exportsql = new WLTButton("导出SQL"); //

			// 添加新功能 武坤萌
			btn_exportExcel = new WLTButton("导出Excel");
			btn_inputExcel = new WLTButton("导入Excel");
			btn_exportXML = new WLTButton("导出XML");
			btn_importXML = new WLTButton("导入XML");

			btn_new.addActionListener(this);
			btn_save.addActionListener(this);
			btn_open.addActionListener(this);
			btn_exportsql.addActionListener(this);
			btn_exportExcel.addActionListener(this);
			btn_inputExcel.addActionListener(this);
			btn_exportXML.addActionListener(this);
			btn_importXML.addActionListener(this);

			panel_1.add(btn_new);
			panel_1.add(btn_save);
			panel_1.add(btn_open);
			if (ClientEnvironment.getInstance().isAdmin()) {
				panel_1.add(btn_exportsql);
				panel_1.add(btn_exportExcel);
				panel_1.add(btn_inputExcel);
				panel_1.add(btn_exportXML);
				panel_1.add(btn_importXML);
			}

			btn_combine = new WLTButton(UIUtil.getImage("office_071.gif")); //
			btn_split = new WLTButton(UIUtil.getImage("office_149.gif")); //
			btn_combine.setToolTipText("合并单元格"); //
			btn_split.setToolTipText("分割单元格"); //
			btn_combine.addActionListener(this);
			btn_split.addActionListener(this);

			panel_2.add(btn_combine);
			panel_2.add(btn_split);

			btn_forecolor = new WLTButton("前景"); //
			btn_backcolor = new WLTButton("背景"); //
			btn_forecolor.addActionListener(this); //
			btn_backcolor.addActionListener(this); //

			panel_2.add(btn_forecolor);
			panel_2.add(btn_backcolor);

			combox_fonttype = new JComboBox(); //
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			String fontNames[] = ge.getAvailableFontFamilyNames();
			for (int i = 0; i < fontNames.length; i++) {
				combox_fonttype.addItem(fontNames[i]); //
			}
			combox_fonttype.setSelectedIndex(0); //
			combox_fonttype.setPreferredSize(new Dimension(125, 20));
			combox_fonttype.addActionListener(this);
			panel_2.add(combox_fonttype);

			combox_fontstyle = new JComboBox(); //
			combox_fontstyle.addItem("PLAIN"); //
			combox_fontstyle.addItem("BOLD"); //
			combox_fontstyle.addItem("ITALIC"); //
			combox_fontstyle.setSelectedIndex(0); //
			combox_fontstyle.setPreferredSize(new Dimension(70, 20));
			combox_fontstyle.addActionListener(this);
			panel_2.add(combox_fontstyle);

			combox_fontsize = new JComboBox(); //
			combox_fontsize.addItem("10"); //
			combox_fontsize.addItem("11"); //
			combox_fontsize.addItem("12"); //
			combox_fontsize.addItem("13"); //
			combox_fontsize.addItem("14"); //
			combox_fontsize.addItem("16"); //
			combox_fontsize.addItem("20"); //
			combox_fontsize.addItem("35"); //
			combox_fontsize.setSelectedIndex(1); //
			combox_fontsize.setPreferredSize(new Dimension(50, 20));
			combox_fontsize.addActionListener(this);
			panel_2.add(combox_fontsize);

			combox_celltype = new JComboBox(); //
			combox_celltype.addItem(ITEMTYPE_TEXT); // 文本框
			combox_celltype.addItem(ITEMTYPE_NUMBERTEXT); // 数字框
			combox_celltype.addItem(ITEMTYPE_TEXTAREA); // 多行文本框
			combox_celltype.addItem(ITEMTYPE_CHECKBOX); // 勾选框
			combox_celltype.addItem(ITEMTYPE_COMBOBOX); // 下拉框
			combox_celltype.addItem(ITEMTYPE_DATE); // 日历.
			combox_celltype.addItem(ITEMTYPE_DATETIME); // 时间.
			combox_celltype.setSelectedIndex(0); //
			combox_celltype.setPreferredSize(new Dimension(85, 20));
			combox_celltype.addActionListener(this);
			panel_2.add(combox_celltype);

			btn_left = new WLTButton(UIUtil.getImage("align_left.gif")); //
			btn_center = new WLTButton(UIUtil.getImage("align_center.gif")); //
			btn_right = new WLTButton(UIUtil.getImage("align_right.gif")); //
			btn_top = new WLTButton(UIUtil.getImage("valign_top.gif")); //
			btn_middle = new WLTButton(UIUtil.getImage("valign_middle.gif")); //
			btn_bottom = new WLTButton(UIUtil.getImage("valign_bottom.gif")); //
			checkbox_showCellKey = new JCheckBox("显示CellKey", false); //
			checkbox_showCellKey.setToolTipText("是否显示CellKey"); //
			btn_left.addActionListener(this);
			btn_center.addActionListener(this);
			btn_right.addActionListener(this);
			btn_top.addActionListener(this);
			btn_middle.addActionListener(this);
			btn_bottom.addActionListener(this);
			checkbox_showCellKey.addActionListener(this); //

			panel_2.add(btn_left);
			panel_2.add(btn_center);
			panel_2.add(btn_right);
			panel_2.add(btn_top);
			panel_2.add(btn_middle);
			panel_2.add(btn_bottom);
			panel_2.add(checkbox_showCellKey);

			toolbar.add(panel_1, BorderLayout.NORTH);
			toolbar.add(panel_2, BorderLayout.SOUTH);
		}
		return toolbar;
	}
	/**
	 * zzl 2020-8-31 根据数字显示按钮可以扩展
	 * @return
	 */
	public JToolBar getTitleBtnBar(int type) { //
		if(type > 0){
			JPanel panel_1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0)); //
			toolbar = new JToolBar("Btn", JToolBar.HORIZONTAL); //
			toolbar.setBackground(LookAndFeel.systembgcolor); //
			toolbar.setLayout(new BorderLayout(0, 2)); //
			toolbar.setFloatable(false);
			toolbar.setMargin(new Insets(1, 1, 3, 1));
			toolbar.setRollover(true); //
			btn_save = new WLTButton("保存"); //
			btn_exportExcel = new WLTButton("导出Excel");
			btn_inputExcel = new WLTButton("导入Excel");
			btn_save.addActionListener(this);
			btn_exportExcel.addActionListener(this);
			btn_inputExcel.addActionListener(this);
			panel_1.add(btn_save);
			panel_1.add(btn_exportExcel);
			panel_1.add(btn_inputExcel);
			toolbar.add(panel_1, BorderLayout.NORTH);
		}else if (toolbar == null) {
			JPanel panel_1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0)); //
			JPanel panel_2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0)); //

			toolbar = new JToolBar("Btn", JToolBar.HORIZONTAL); //
			toolbar.setBackground(LookAndFeel.systembgcolor); //
			toolbar.setLayout(new BorderLayout(0, 2)); //
			toolbar.setFloatable(false);
			toolbar.setMargin(new Insets(1, 1, 3, 1));
			toolbar.setRollover(true); //

			btn_new = new WLTButton("新建"); //
			btn_save = new WLTButton("保存"); //
			btn_open = new WLTButton("打开"); //
			btn_exportsql = new WLTButton("导出SQL"); //

			// 添加新功能 武坤萌
			btn_exportExcel = new WLTButton("导出Excel");
			btn_inputExcel = new WLTButton("导入Excel");
			btn_exportXML = new WLTButton("导出XML");
			btn_importXML = new WLTButton("导入XML");

			btn_new.addActionListener(this);
			btn_save.addActionListener(this);
			btn_open.addActionListener(this);
			btn_exportsql.addActionListener(this);
			btn_exportExcel.addActionListener(this);
			btn_inputExcel.addActionListener(this);
			btn_exportXML.addActionListener(this);
			btn_importXML.addActionListener(this);

			panel_1.add(btn_new);
			panel_1.add(btn_save);
			panel_1.add(btn_open);
			if (ClientEnvironment.getInstance().isAdmin()) {
				panel_1.add(btn_exportsql);
				panel_1.add(btn_exportExcel);
				panel_1.add(btn_inputExcel);
				panel_1.add(btn_exportXML);
				panel_1.add(btn_importXML);
			}

			btn_combine = new WLTButton(UIUtil.getImage("office_071.gif")); //
			btn_split = new WLTButton(UIUtil.getImage("office_149.gif")); //
			btn_combine.setToolTipText("合并单元格"); //
			btn_split.setToolTipText("分割单元格"); //
			btn_combine.addActionListener(this);
			btn_split.addActionListener(this);

			panel_2.add(btn_combine);
			panel_2.add(btn_split);

			btn_forecolor = new WLTButton("前景"); //
			btn_backcolor = new WLTButton("背景"); //
			btn_forecolor.addActionListener(this); //
			btn_backcolor.addActionListener(this); //

			panel_2.add(btn_forecolor);
			panel_2.add(btn_backcolor);

			combox_fonttype = new JComboBox(); //
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			String fontNames[] = ge.getAvailableFontFamilyNames();
			for (int i = 0; i < fontNames.length; i++) {
				combox_fonttype.addItem(fontNames[i]); //
			}
			combox_fonttype.setSelectedIndex(0); //
			combox_fonttype.setPreferredSize(new Dimension(125, 20));
			combox_fonttype.addActionListener(this);
			panel_2.add(combox_fonttype);

			combox_fontstyle = new JComboBox(); //
			combox_fontstyle.addItem("PLAIN"); //
			combox_fontstyle.addItem("BOLD"); //
			combox_fontstyle.addItem("ITALIC"); //
			combox_fontstyle.setSelectedIndex(0); //
			combox_fontstyle.setPreferredSize(new Dimension(70, 20));
			combox_fontstyle.addActionListener(this);
			panel_2.add(combox_fontstyle);

			combox_fontsize = new JComboBox(); //
			combox_fontsize.addItem("10"); //
			combox_fontsize.addItem("11"); //
			combox_fontsize.addItem("12"); //
			combox_fontsize.addItem("13"); //
			combox_fontsize.addItem("14"); //
			combox_fontsize.addItem("16"); //
			combox_fontsize.addItem("20"); //
			combox_fontsize.addItem("35"); //
			combox_fontsize.setSelectedIndex(1); //
			combox_fontsize.setPreferredSize(new Dimension(50, 20));
			combox_fontsize.addActionListener(this);
			panel_2.add(combox_fontsize);

			combox_celltype = new JComboBox(); //
			combox_celltype.addItem(ITEMTYPE_TEXT); // 文本框
			combox_celltype.addItem(ITEMTYPE_NUMBERTEXT); // 数字框
			combox_celltype.addItem(ITEMTYPE_TEXTAREA); // 多行文本框
			combox_celltype.addItem(ITEMTYPE_CHECKBOX); // 勾选框
			combox_celltype.addItem(ITEMTYPE_COMBOBOX); // 下拉框
			combox_celltype.addItem(ITEMTYPE_DATE); // 日历.
			combox_celltype.addItem(ITEMTYPE_DATETIME); // 时间.
			combox_celltype.setSelectedIndex(0); //
			combox_celltype.setPreferredSize(new Dimension(85, 20));
			combox_celltype.addActionListener(this);
			panel_2.add(combox_celltype);

			btn_left = new WLTButton(UIUtil.getImage("align_left.gif")); //
			btn_center = new WLTButton(UIUtil.getImage("align_center.gif")); //
			btn_right = new WLTButton(UIUtil.getImage("align_right.gif")); //
			btn_top = new WLTButton(UIUtil.getImage("valign_top.gif")); //
			btn_middle = new WLTButton(UIUtil.getImage("valign_middle.gif")); //
			btn_bottom = new WLTButton(UIUtil.getImage("valign_bottom.gif")); //
			checkbox_showCellKey = new JCheckBox("显示CellKey", false); //
			checkbox_showCellKey.setToolTipText("是否显示CellKey"); //
			btn_left.addActionListener(this);
			btn_center.addActionListener(this);
			btn_right.addActionListener(this);
			btn_top.addActionListener(this);
			btn_middle.addActionListener(this);
			btn_bottom.addActionListener(this);
			checkbox_showCellKey.addActionListener(this); //

			panel_2.add(btn_left);
			panel_2.add(btn_center);
			panel_2.add(btn_right);
			panel_2.add(btn_top);
			panel_2.add(btn_middle);
			panel_2.add(btn_bottom);
			panel_2.add(checkbox_showCellKey);

			toolbar.add(panel_1, BorderLayout.NORTH);
			toolbar.add(panel_2, BorderLayout.SOUTH);
		}
		return toolbar;
	}
	public void setToolBarVisiable(boolean _visiable) {
		getTitleBtnBar().setVisible(_visiable); //
		textfield_status.setVisible(_visiable); //
	}

	public void setCustPopMenu(JPopupMenu _custPopMenu) {
		this.custPopMenu = _custPopMenu; //
	}

	//	/**
	//	 * 取得颜色下拉框
	//	 * 
	//	 * @return
	//	 */
	//	private JComboBox getColorComboBox(boolean _isfore) {
	//		JComboBox comBox = new JComboBox(); //
	//		comBox.setPreferredSize(new Dimension(60, 20)); //
	//		comBox.setEditable(true); //
	//
	//		ArrayList al_colors = new ArrayList(); //
	//		for (int i = 0; i <= 4; i++) {
	//			for (int j = 0; j <= 4; j++) {
	//				for (int k = 0; k <= 4; k++) {
	//					int li_r = i * 64; //
	//					int li_g = j * 64; //
	//					int li_b = k * 64; //
	//					if (i == 4) {
	//						li_r = li_r - 1;
	//					}
	//					if (j == 4) {
	//						li_g = li_g - 1;
	//					}
	//					if (k == 4) {
	//						li_b = li_b - 1;
	//					}
	//					Color color = new Color(li_r, li_g, li_b); //
	//					al_colors.add(new MyColor(color)); //
	//				}
	//			}
	//		}
	//
	//		for (int i = 0; i < al_colors.size(); i++) {
	//			comBox.addItem((MyColor) al_colors.get(i)); //
	//		}
	//
	//		comBox.setSelectedIndex(0); //
	//		comBox.addActionListener(this); //
	//		comBox.setRenderer(new ComboxCellRender()); //
	//		JTextField cmb_textField = ((JTextField) ((JComponent) comBox.getEditor().getEditorComponent()));
	//		cmb_textField.setEditable(false); //
	//		return comBox; //
	//	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == btn_new) {
				onNew(); //
			} else if (e.getSource() == btn_save) {
				onSave(); //
			} else if (e.getSource() == btn_open) {
				onOpen(); //
			} else if (e.getSource() == btn_exportsql) {
				exportSQL(); // 导出SQL
			} else if (e.getSource() == btn_combine) {
				onCombine(); //
			} else if (e.getSource() == btn_split) {
				onSplit(); //
			} else if (e.getSource() == btn_forecolor) {
				onColorchanged(1); //
			} else if (e.getSource() == btn_backcolor) {
				onColorchanged(2); //
			} else if (e.getSource() == combox_fonttype || e.getSource() == combox_fontstyle || e.getSource() == combox_fontsize) {
				onFontChanged(); //
			} else if (e.getSource() == combox_celltype) {
				onCellTypechanged(); //
			} else if (e.getSource() == btn_left) {
				onHalign(1);
			} else if (e.getSource() == btn_center) {
				onHalign(2);
			} else if (e.getSource() == btn_right) {
				onHalign(3);
			} else if (e.getSource() == btn_top) {
				onValign(1);
			} else if (e.getSource() == btn_middle) {
				onValign(2);
			} else if (e.getSource() == btn_bottom) {
				onValign(3);
			} else if (e.getSource() == checkbox_showCellKey) {
				setIsShowCellKey(checkbox_showCellKey); //
			} else if (e.getSource() == this.popMenuItem_insertRow) {
				onInsertRow(); //
			} else if (e.getSource() == this.popMenuItem_insertCol) {
				onInsertColumn(); //
			} else if (e.getSource() == this.popMenuItem_delRow) {
				onDeleteRow(); //
			} else if (e.getSource() == this.popMenuItem_delCol) {
				onDeleteColumn(); //
			} else if (e.getSource() == this.popMenuItem_cleardata) {
				cleardata(); //
			} else if (e.getSource() == this.popMenuItem_loc) {
				onLock(); //
			} else if (e.getSource() == this.popMenuItem_unloc) {
				onUnLock(); //
			} else if (e.getSource() == this.popMenuItem_setTooltipText) {
				setToolTipText(); //
			} else if (e.getSource() == this.popMenuItem_setCellEditable) {
				setCellEditable(); //
			} else if (e.getSource() == this.popMenuItem_setCellKey) {
				setCellkey(); //
			} else if (e.getSource() == this.item_AutoSetCellKey) {
				autoSetCellKey(); // 自动设置CellKey
			} else if (e.getSource() == this.popMenuItem_setCellEditFormula) {
				setCellEditFormula(); //
			} else if (e.getSource() == this.popMenuItem_setCellIsHtmlHref) { //如果是超链接!
				setCellIsHtmlHref(); //
			} else if (e.getSource() == popMenuItem_setCellValidateFormula) { //校验公式
				setCellValidateFormula(); //设置校验公式
			} else if (e.getSource() == this.popMenuItem_setCellDesc) {
				setCellDesc(); //
			} else if (e.getSource() == popMenuItem_showCellProp) { //显示格子所有属性
				showCellAllProp(); //显示格子所有属性
			} else if (e.getSource() == btn_exportExcel) {
				// 执行EXCEL文件的导出----
				exportExcel();
			} else if (e.getSource() == btn_inputExcel) {
				// 执行EXCEL文件的导入----
				inputExcel();
			} else if (e.getSource() == btn_exportXML) {
				// 执行XML的导出
				exportXML();
			} else if (e.getSource() == btn_importXML) {
				// 执行XML的导出
				importXML();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			MessageBox.showException(this, ex); //
		}
	}

	private void importXML() {
		textArea = new JTextArea();
		dialog = new BillDialog(this, "导入XML", 1000, 700);
		dialog.setLayout(new BorderLayout());
		textArea.setBackground(Color.WHITE);
		textArea.setForeground(Color.BLUE);
		textArea.setFont(new Font("宋体", Font.PLAIN, 12));
		textArea.select(0, 0);

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		WLTButton confirm = new WLTButton("确定");
		WLTButton cancel = new WLTButton("取消");
		confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String input = textArea.getText();
				StringBuffer text = null;
				try {
					if (input == null || input.trim().equals("")) {
						return;
					} else {
						text = new StringBuffer();
						text.append("<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n");
						text.append("<root>\r\n");
						text.append(input + "\r\n");
						text.append("</root>");
						FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
						service.importXML(text.toString());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					return;
				}
				MessageBox.show("导入成功！");
				textArea.setText("");
				dialog.dispose();
			}

		});
		cancel.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				dialog.dispose();
			}

		});
		panel.add(confirm);
		panel.add(cancel);

		dialog.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER); //
		dialog.getContentPane().add(panel, BorderLayout.SOUTH);
		dialog.setVisible(true);
	}

	/**
	 * 导出XML格式文件 武坤萌 2008-11-20
	 */
	private void exportXML() {
		if (templetcode == null) {
			MessageBox.show(this, "请选择一个EXCEL模版!");
			return;
		}
		BillDialog excelToXML = new BillDialog(this, "导出XML", 1000, 700);
		JTextArea show = null;
		try {
			FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
			show = new JTextArea(service.exportXMLForExcel(templetcode));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		show.setBackground(new Color(240, 240, 240));
		show.setForeground(Color.BLACK);
		show.setFont(new Font("宋体", Font.PLAIN, 12));
		show.select(0, 0);
		excelToXML.getContentPane().add(new JScrollPane(show));
		excelToXML.setVisible(true);
	}

	/**
	 * 合并单元格字符串转换为一维数组
	 * 
	 * @param span
	 *            String
	 * @return 整形[2]
	 */
	private int[] toSpan(String span) {
		String[] splitString = null;
		int[] returnResult = new int[] { 1, 1 };
		if (span != null) {
			splitString = span.split(",");
			returnResult[0] = Integer.parseInt(splitString[0].toString());
			returnResult[1] = Integer.parseInt(splitString[1].toString());
		}
		return returnResult;
	}

	/**
	 * 武坤萌 输出页面文件
	 */
	public void exportHtml() {
		exportHtml("报表数据导出"); //
	}

	/**
	 * 导出Html
	 */
	public void exportHtml(String _filename) {
		try {
			byte[] bytes = createHtmlString().getBytes("GBK"); //
			UIUtil.saveFile(this, "html", _filename, bytes); //
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void exportExcel() {
		exportExcel("报表数据导出", "defaultPanel"); //
	}

	public void exportExcel(String _filename) {
		exportExcel(_filename, "defaultPanel"); //
	}

	/**
	 * 导出Exel,带合并效果的!!
	 */
	public void exportExcel(String _filename, String type) {
		try {
			byte[] bytes = null;
			if (type != null && type.trim().equals("StyleReportPanel")) {//如果为风格报表
				bytes = initStyleReportExcelData();
			} else if (type != null && type.trim().equals("BillReportPanel")) { //如果为万维报表
				bytes = initBillReportExcelData();
			} else {
				bytes = initDefaultExcelData();
			}
			UIUtil.saveFile(this, "xls", _filename, bytes); //
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public byte[] getExportExcelBytes(String type) {
		byte[] bytes = null;
		try {
			if (type != null && type.trim().equals("StyleReportPanel")) {//如果为风格报表
				bytes = initStyleReportExcelData();
			} else if (type != null && type.trim().equals("BillReportPanel")) { //如果为万维报表
				bytes = initBillReportExcelData();
			} else {
				bytes = initDefaultExcelData();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bytes;
	}

	//主要是万维报表样式   20130911  袁江晓修改
	private byte[] initBillReportExcelData() throws Exception {
		// 函数内变量初始化
		HSSFWorkbook wb = null;
		HSSFSheet sheet = null;
		int[] cellSpan = null;
		HSSFCellStyle style = null;
		Color color = null;
		HSSFFont font = null;
		String[] colorStrings = null;
		wb = new HSSFWorkbook(); // 打开EXCEL面板
		sheet = wb.createSheet(); // 创建一个
		wb.setSheetName(0, defaultSheetName);
		font = wb.createFont();
		style = wb.createCellStyle();
		// 遍历整个表格，取出数据
		for (int i = 0; i < table.getRowCount(); i++) { // 直接从页面上取
			row = sheet.createRow(i);
			for (int j = 0; j < table.getColumnCount(); j++) {
				cell = row.createCell((short) j); // 创建单元格
				cell.setCellValue(getValueAt(i, j)); // 表格进行赋值
				// 返回单元格的合并数据
				cellSpan = getSpan(i, j);
				// 返回合并单元格错误
				if (cellSpan == null || cellSpan.length == 0 || cellSpan.length != 2) {
					System.out.println(cellSpanError);
					return null;
				}
				if (cellSpan[0] > 1 || cellSpan[1] > 1) {
					initExcelSpan(sheet, i, j, i + cellSpan[0] - 1, j + cellSpan[1] - 1);
				}
				/**
				 * 以下是EXCEL文件的显示风格的初始化： 背景颜色 字体样式 字体颜色 边框颜色 等等....
				 * ----------------------------------------------------------------------------------------------------------
				 */
				if (getCellItemVOAt(i, j) != null) {
					if ((i == 0) && (getCellItemVOAt(i, j).getColwidth() != null)) { //如果是第一行  即为字段名  袁江晓 20130911 更改样式   
						HSSFCellStyle cstyle1 = wb.createCellStyle();
						//						 row.setHeight((short)(Short.valueOf(getCellItemVOAt(i, j).getRowheight()).shortValue() * 12));   //设置高度
						cstyle1.setAlignment((short) getCellItemVOAt(i, j).getHalign());
						cstyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
						cstyle1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); //设置背景颜色
						cstyle1.setFillForegroundColor(HSSFColor.ROSE.index);//设置背景颜色
						// 边框设置
						cstyle1.setBorderTop((short) 1);
						cstyle1.setBorderRight((short) 1);
						cstyle1.setBorderBottom((short) 1);
						cstyle1.setBorderLeft((short) 1);
						cstyle1.setTopBorderColor(HSSFColor.BLACK.index);
						cstyle1.setRightBorderColor(HSSFColor.BLACK.index);
						cstyle1.setBottomBorderColor(HSSFColor.BLACK.index);
						cstyle1.setLeftBorderColor(HSSFColor.BLACK.index);
						cell.setCellStyle(cstyle1);
					} else if ((j == 0) && (getCellItemVOAt(i, j).getColwidth() != null)) { //如果是第一列  即为字段名  袁江晓 20130911 更改样式
						HSSFCellStyle cstyle1 = wb.createCellStyle();
						//						 row.setHeight((short)(Short.valueOf(getCellItemVOAt(i, j).getRowheight()).shortValue() * 12));   //设置高度
						cstyle1.setAlignment((short) getCellItemVOAt(i, j).getHalign());
						cstyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
						cstyle1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); //设置背景颜色
						cstyle1.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);//设置背景颜色
						// 边框设置
						cstyle1.setBorderTop((short) 1);
						cstyle1.setBorderRight((short) 1);
						cstyle1.setBorderBottom((short) 1);
						cstyle1.setBorderLeft((short) 1);
						cstyle1.setTopBorderColor(HSSFColor.BLACK.index);
						cstyle1.setRightBorderColor(HSSFColor.BLACK.index);
						cstyle1.setBottomBorderColor(HSSFColor.BLACK.index);
						cstyle1.setLeftBorderColor(HSSFColor.BLACK.index);
						cell.setCellStyle(cstyle1);
					} else {
						style.setAlignment((short) getCellItemVOAt(i, j).getHalign());
						style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
						// 边框设置
						style.setBorderTop((short) 1);
						style.setBorderRight((short) 1);
						style.setBorderBottom((short) 1);
						style.setBorderLeft((short) 1);
						style.setTopBorderColor(HSSFColor.BLACK.index);
						style.setRightBorderColor(HSSFColor.BLACK.index);
						style.setBottomBorderColor(HSSFColor.BLACK.index);
						style.setLeftBorderColor(HSSFColor.BLACK.index);
						style.setFillForegroundColor(HSSFColor.RED.index);
						cell.setCellStyle(style);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					}
					if (((i == 0) && (j == 0)) && (getCellItemVOAt(i, j).getColwidth() != null)) { //如果是左上角，则为黄色显示
						HSSFCellStyle cstyle2 = wb.createCellStyle();
						;
						sheet.setColumnWidth(j, Short.valueOf(getCellItemVOAt(i, j).getColwidth()).shortValue() * 47);
						row.setHeight((short) (Short.valueOf(getCellItemVOAt(i, j).getRowheight()).shortValue() * 26));
						cstyle2.setWrapText(true);
						HSSFFont cfont = wb.createFont();
						cfont.setColor(HSSFColor.BLACK.index);
						cfont.setFontHeightInPoints((short) 10);// 设置字体大小
						cstyle2.setFont(cfont);
						cstyle2.setBorderTop((short) 1);
						cstyle2.setBorderRight((short) 1);
						cstyle2.setBorderLeft((short) 1);
						cstyle2.setTopBorderColor(HSSFColor.BLACK.index);
						cstyle2.setRightBorderColor(HSSFColor.BLACK.index);
						cstyle2.setLeftBorderColor(HSSFColor.BLACK.index);
						cstyle2.setFillForegroundColor(HSSFColor.RED.index);
						cstyle2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); //设置背景颜色
						cstyle2.setFillForegroundColor(HSSFColor.YELLOW.index);//设置背景颜色
						cell.setCellStyle(cstyle2);
					}
				}
				/**
				 * 以上初始化完毕。
				 */
			}
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream(); //
		wb.write(bout);
		byte[] bytes = bout.toByteArray(); //
		bout.close();
		return bytes; //
	}

	//除了风格报表和万维报表外的报表的导出样式    即兼容以前旧的
	private byte[] initDefaultExcelData() throws Exception {
		// 函数内变量初始化
		HSSFWorkbook wb = null;
		HSSFSheet sheet = null;
		int[] cellSpan = null;
		HSSFCellStyle style = null;
		Color color = null;
		HSSFFont font = null;
		String[] colorStrings = null;
		wb = new HSSFWorkbook(); // 打开EXCEL面板
		sheet = wb.createSheet(); // 创建一个
		wb.setSheetName(0, defaultSheetName);
		font = wb.createFont();
		style = wb.createCellStyle();
		// 遍历整个表格，取出数据
		for (int i = 0; i < table.getRowCount(); i++) { // 直接从页面上取
			row = sheet.createRow(i);
			for (int j = 0; j < table.getColumnCount(); j++) {
				cell = row.createCell((short) j); // 创建单元格
				cell.setCellValue(getValueAt(i, j)); // 表格进行赋值
				// 返回单元格的合并数据
				cellSpan = getSpan(i, j);
				// 返回合并单元格错误
				if (cellSpan == null || cellSpan.length == 0 || cellSpan.length != 2) {
					System.out.println(cellSpanError);
					return null;
				}
				if (cellSpan[0] > 1 || cellSpan[1] > 1) {
					initExcelSpan(sheet, i, j, i + cellSpan[0] - 1, j + cellSpan[1] - 1);
				}
				/**
				 * 以下是EXCEL文件的显示风格的初始化： 背景颜色 字体样式 字体颜色 边框颜色 等等....
				 * ----------------------------------------------------------------------------------------------------------
				 */
				if (getCellItemVOAt(i, j) != null) {
					if (i == 0 && getCellItemVOAt(i, j).getColwidth() != null) {
						sheet.setColumnWidth(j, Short.valueOf(getCellItemVOAt(i, j).getColwidth()) * 37);
					}
					if (j == 0 && getCellItemVOAt(i, j).getRowheight() != null) {
						row.setHeight((short) (Short.valueOf(getCellItemVOAt(i, j).getRowheight()) * 16));
					}
					// 对齐方式
					style.setAlignment((short) getCellItemVOAt(i, j).getHalign());
					style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
					style.setWrapText(true);
					font.setFontName("宋体");
					font.setColor(HSSFColor.BLACK.index);
					if (getCellItemVOAt(i, j).getFontsize() == null) {
						font.setFontHeightInPoints((short) 12);
					} else {
						font.setFontHeightInPoints(Short.valueOf(getCellItemVOAt(i, j).getFontsize()));
					}
					// 边框设置
					style.setBorderTop((short) 1);
					style.setBorderRight((short) 1);
					style.setBorderBottom((short) 1);
					style.setBorderLeft((short) 1);
					style.setTopBorderColor(HSSFColor.BLACK.index);
					style.setRightBorderColor(HSSFColor.BLACK.index);
					style.setBottomBorderColor(HSSFColor.BLACK.index);
					style.setLeftBorderColor(HSSFColor.BLACK.index);
					cell.setCellStyle(style);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				}
				/**
				 * 以上初始化完毕。
				 */
				// ----------------------------------------------------------------------------------------------------
			}
		}

		ByteArrayOutputStream bout = new ByteArrayOutputStream(); //
		wb.write(bout);
		byte[] bytes = bout.toByteArray(); //
		bout.close();
		return bytes; //
	}

	public void convertxy() {
		convertxy(true); //
	}

	/**
	 * 增加表格x/y轴转换逻辑【李春娟/2012-04-12】
	 */
	public void convertxy(boolean _isResetHeight) {
		if (billCellVO == null) {
			return;
		}
		BillCellItemVO[][] billitemvos = billCellVO.getCellItemVOs();
		BillCellItemVO[][] newbillitemvos = new BillCellItemVO[billitemvos[0].length][billitemvos.length]; //
		for (int i = 0; i < billitemvos[0].length; i++) {
			for (int j = 0; j < billitemvos.length; j++) {
				newbillitemvos[i][j] = billitemvos[j][i];
				if (newbillitemvos[i][j] != null) {
					if (_isResetHeight) { //在做万维报表时遇到不想重置行高！【xch/2012-07-19】
						newbillitemvos[i][j].setRowheight("22");//统一设置一下，否则各单元格都会加高
					}
					String span = newbillitemvos[i][j].getSpan();
					if (span != null && span.contains(",")) {
						span = span.substring(span.indexOf(",") + 1) + "," + span.substring(0, span.indexOf(","));
					}
					newbillitemvos[i][j].setSpan(span); //
				}
			}
		}
		billCellVO.setCellItemVOs(newbillitemvos);
		int tmp_row = billCellVO.getRowlength();
		billCellVO.setRowlength(billCellVO.getCollength());
		billCellVO.setCollength(tmp_row);
		try {
			loadBillCellData(billCellVO);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * 创建Html,以前多人写过,都有问题!我重写了下算法!【xch/2012-08-22】
	 * @return
	 * @throws Exception
	 */
	public String createHtmlString() throws Exception {
		BillCellVO cellVO = getBillCellVO(); //
		BillCellItemVO[][] itemVOs = cellVO.getCellItemVOs(); //
		StringBuffer returnHtml = new StringBuffer();
		returnHtml.append("<html><head><title>导出Html</title>\r\n");
		returnHtml.append("<style type=\"text/css\">\r\n");
		returnHtml.append(".style_1 {border-color:#888888 #888888 #888888 #888888;border-style:solid;border-top-width:1px;border-right-width:1px;border-bottom-width:1px;border-left-width:1px;font-size:12px; }\r\n"); //
		returnHtml.append("</style>\r\n");
		returnHtml.append("</head><body>\r\n");
		returnHtml.append("<table style=\"border-collapse:collapse;\" border=\"0\" cellpadding=\"5\">\r\n");

		TBUtil tbUtil = new TBUtil(); //
		String[][] str_data = new String[itemVOs.length][itemVOs[0].length]; //
		String[][] str_bgcolor = new String[itemVOs.length][itemVOs[0].length]; //
		String[][] str_fontcolor = new String[itemVOs.length][itemVOs[0].length]; //
		String[][] str_height = new String[itemVOs.length][itemVOs[0].length]; //
		String[][] str_width = new String[itemVOs.length][itemVOs[0].length]; //
		int[][] li_align = new int[itemVOs.length][itemVOs[0].length]; //
		int[][] li_valign = new int[itemVOs.length][itemVOs[0].length]; //

		HashMap span_xy_map = new HashMap(); //
		ArrayList al_null_xy = new ArrayList(); //
		for (int i = 0; i < itemVOs.length; i++) {
			for (int j = 0; j < itemVOs[i].length; j++) { //
				String str_value = itemVOs[i][j].getCellvalue(); //
				if (str_value == null || str_value.trim().equals("")) {
					str_value = "&nbsp;"; //
				}
				str_data[i][j] = str_value; //赋值
				str_bgcolor[i][j] = itemVOs[i][j].getBackground(); //
				str_fontcolor[i][j] = itemVOs[i][j].getForeground(); //
				str_height[i][j] = itemVOs[i][j].getRowheight(); //行高!
				str_width[i][j] = itemVOs[i][j].getColwidth(); //
				li_align[i][j] = itemVOs[i][j].getHalign(); //
				li_valign[i][j] = itemVOs[i][j].getValign(); //

				//记录合并情况!
				String str_span = itemVOs[i][j].getSpan(); //合并!
				if (str_span != null && !str_span.trim().equals("")) { //如果不为空
					String[] str_xy = tbUtil.split(str_span.trim(), ","); //分割一下!
					int li_rowspan = Integer.parseInt(str_xy[0]); //
					int li_colspan = Integer.parseInt(str_xy[1]); //
					if (li_rowspan >= 1 && li_colspan >= 1) { //必须两个都是大于0
						span_xy_map.put("" + i + "," + j, str_span.trim()); //记下来,key是哪个位置,value是合并情况,最后htm拼rowSpan,colspan就靠这个value
						for (int r = 0; r < li_rowspan; r++) { //所有行合并的数组
							for (int c = 0; c < li_colspan; c++) { //所有行合并的数组
								if (r != 0 || c != 0) {
									al_null_xy.add(new int[] { i + r, j + c }); //加入列表,即最后将这些位置的格子统统置空!
								}
							}
						}
					}
				}

			}
		}

		//根据上面计算的情况,第二次遍历所有,将所有被合并的格子统统置为null
		for (int i = 0; i < al_null_xy.size(); i++) {
			int[] li_null = (int[]) al_null_xy.get(i); //
			str_data[li_null[0]][li_null[1]] = null; //
		}

		for (int i = 0; i < str_data.length; i++) {
			returnHtml.append("<tr>\r\n");
			for (int j = 0; j < str_data[i].length; j++) { //
				if (str_data[i][j] == null) { //如果为空则跳过!正好实现了合并效果
					continue; //
				}
				String str_rowheight = "25"; //
				if (str_height[i][j] != null) {
					str_rowheight = str_height[i][j]; //
				}
				String str_colwidth = "125"; //
				if (str_width[i][j] != null) {
					str_colwidth = str_width[i][j]; //
				}
				String str_value = str_data[i][j];
				str_value = tbUtil.replaceAll(str_value, "→", "→ <br>"); //
				str_value = tbUtil.replaceAll(str_value, "↘", "↘<br>"); //
				str_value = tbUtil.replaceAll(str_value, "↓", "↓ <br>"); //
				if (str_fontcolor[i][j] != null && !str_fontcolor[i][j].trim().equals("")) {
					str_value = "<span style=\"color:" + tbUtil.getHtmlColor(str_fontcolor[i][j]) + "\">" + str_value + "</span>"; //
				}
				if (span_xy_map.containsKey("" + i + "," + j)) { //如果包含
					String str_span = (String) span_xy_map.get("" + i + "," + j); //
					String[] str_span_xy = tbUtil.split(str_span, ","); //分隔
					int li_rowSpan = Integer.parseInt(str_span_xy[0]); //
					int li_colSpan = Integer.parseInt(str_span_xy[1]); //
					String str_align = getAlign(li_align[i][j], 1);
					String str_valign = getAlign(li_valign[i][j], 2);
					returnHtml.append("<td height=\"" + str_rowheight + "\" class=\"style_1\" align=\"" + str_align + "\"  valign=\"" + str_valign + "\" "); //
					if (str_bgcolor[i][j] != null) {
						returnHtml.append(" bgcolor=\"" + tbUtil.getHtmlColor(str_bgcolor[i][j]) + "\""); //
					}
					if (li_rowSpan > 1) { //如果大于1
						returnHtml.append(" rowspan=" + str_span_xy[0]);
					}
					if (li_colSpan > 1) {
						returnHtml.append(" colspan=" + str_span_xy[1]);
					}
					returnHtml.append(">" + str_value + "</td>\r\n");
				} else {
					returnHtml.append("<td height=\"" + str_rowheight + "\" width=\"" + str_colwidth + "\" class=\"style_1\"");
					if (str_bgcolor[i][j] != null) {
						returnHtml.append(" bgcolor=\"" + tbUtil.getHtmlColor(str_bgcolor[i][j]) + "\""); //
					}
					returnHtml.append(">" + str_value + "</td>\r\n");
				}
			}
			returnHtml.append("</tr>\r\n");
		}
		returnHtml.append("</table>\r\n");
		returnHtml.append("</body></html>\r\n");
		return returnHtml.toString();
	}

	private String getAlign(int _align, int _type) {
		if (_type == 1) {
			switch (_align) {
			case 1:
				return "left"; //
			case 2:
				return "center"; //
			case 3:
				return "right"; //
			default:
				return "left"; //
			}
		} else if (_type == 2) {
			switch (_align) {
			case 1:
				return "top"; //
			case 2:
				return "middle"; //
			case 3:
				return "bottom"; //
			default:
				return "top"; //
			}
		} else {
			return "";
		}
	}

	public void createExcel(String fileName) {
		FileOutputStream output = null;
		HSSFWorkbook wb = null;
		HSSFSheet sheet = null;
		int[] cellSpan = null;
		HSSFCellStyle style = null;
		Color color = null;
		HSSFFont font = null;
		String[] colorStrings = null;

		// 处理EXCEL文件的数据
		try {
			output = new FileOutputStream(fileName);
			wb = new HSSFWorkbook(); // 打开EXCEL面板
			sheet = wb.createSheet(defaultSheetName); // 创建一个

			// 遍历整个表格，取出数据
			for (int i = 0; i < this.billCellVO.getRowlength(); i++) {
				row = sheet.createRow(i);
				// 设置行高
				row.setHeight((short) (Short.valueOf(billCellItemVos[i][0].getRowheight()) * 20));
				for (int j = 0; j < this.billCellVO.getCollength(); j++) {

					cell = row.createCell(j); // 创建单元格
					//cell.setEncoding((short) 1); // 解决中文编码问题
					if (i == 0) {
						// 设置列宽
						sheet.setColumnWidth(j, (Short.valueOf(billCellItemVos[0][j].getColwidth()) * 50));
					}
					if (billCellItemVos[i][j].getCellvalue() != null) {
						cell.setCellValue(billCellItemVos[i][j].getCellvalue().toString()); // 表格进行赋值
					} else {
						cell.setCellValue("");
					}

					// 返回单元格的合并数据
					cellSpan = toSpan(billCellItemVos[i][j].getSpan());
					// 返回合并单元格错误
					if (cellSpan == null || cellSpan.length == 0 || cellSpan.length != 2) {

						return;
					}
					if (cellSpan[0] > 1 || cellSpan[1] > 1) {
						initExcelSpan(sheet, i, j, i + cellSpan[0] - 1, j + cellSpan[1] - 1);
					}
					/**
					 * 以下是EXCEL文件的显示风格的初始化： 背景颜色 字体样式 字体颜色 边框颜色 等等....
					 * ----------------------------------------------------------------------------------------------------------
					 */
					if (billCellItemVos[i][j] != null) {
						font = wb.createFont();
						style = wb.createCellStyle();

						if (billCellItemVos[i][j].getBackground() != null) {
							colorStrings = billCellItemVos[i][j].getBackground().split(",");
							if (colorStrings.length != 3) {
								color = new Color(0, 0, 0);
							} else {
								color = new Color(Integer.parseInt(colorStrings[0]), Integer.parseInt(colorStrings[1]), Integer.parseInt(colorStrings[2]));
							}

							// 颜色参数暂时不能用---------------------本程序采用默认的背景颜色
						}

						// 对齐方式
						style.setAlignment((short) billCellItemVos[i][j].getHalign());
						// style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
						style.setVerticalAlignment((short) billCellItemVos[i][j].getValign());

						// 字体类型设置
						if (billCellItemVos[i][j].getFonttype() == null) {
							font.setFontName("黑体");
						} else {
							font.setFontName(billCellItemVos[i][j].getFonttype());
						}

						if (billCellItemVos[i][j].getFontstyle() == null) {
							font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
							font.setColor(HSSFColor.BLACK.index);
						} else {
							font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
							font.setColor(HSSFColor.BLACK.index);
							// font.setColor(Short.valueOf(billCellItemVos[i][j].getFontstyle()));
						}
						if (billCellItemVos[i][j].getFontsize() == null) {
							font.setFontHeightInPoints((short) 12);
						} else {
							font.setFontHeightInPoints(Short.valueOf(billCellItemVos[i][j].getFontsize()));
						}

						if (billCellItemVos[i][j].getBackground() == null) {
							// style.setFillBackgroundColor(HSSFColor.WHITE.index);
							// style.setFillPattern(HSSFCellStyle.NO_FILL);

						} else {
							// style.setFillBackgroundColor(Short.valueOf(billCellItemVos[i][j].getBackground()));
							style.setFillBackgroundColor(HSSFColor.TAN.index);
							style.setFillPattern(HSSFCellStyle.SPARSE_DOTS);
						}

						// style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

						// 边框设置
						style.setBorderTop((short) 1);
						style.setBorderRight((short) 1);
						style.setBorderBottom((short) 1);
						style.setBorderLeft((short) 1);
						style.setTopBorderColor(HSSFColor.YELLOW.index);
						style.setRightBorderColor(HSSFColor.YELLOW.index);
						style.setBottomBorderColor(HSSFColor.YELLOW.index);
						style.setLeftBorderColor(HSSFColor.YELLOW.index);

						// 设置背景颜色

						// 显示风格为大点显示样式
						// style.setFillPattern(HSSFCellStyle.BIG_SPOTS);

						// 设置前景颜色
						// style.setFillForegroundColor(HSSFColor.RED.index);

						style.setFont(font);

						// style.setFillPattern((short)3);
						// style.setTopBorderColor(HSSFColor.YELLOW.index);

						// 单元格风格设置
						cell.setCellStyle(style);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					}

					/**
					 * 以上初始化完毕。
					 */
					// ----------------------------------------------------------------------------------------------------
				}
			}
			wb.write(output);
			output.flush();
			output.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(lookAtErrorInformation);
		}
	}

	/**
	 * 武坤萌 导入Excel文件 传入文件名称 独立出此函数可以添加其他的功能：文件名称的输入、文件路径的输入等等
	 */
	private void inputExcel() {

		try {
			JFileChooser chooser = new JFileChooser();

			// chooser.addChoosableFileFilter(new
			// javax.swing.filechooser.FileFilter() {
			// public boolean accept(File file) {
			// String filename = file.getName();
			// return filename.endsWith(".xls");
			// }
			//
			// public String getDescription() {
			// return "*.*";
			// }
			// });

			try {
				File f = new File(new File(System.getProperty("user.dir")).getCanonicalPath());
				chooser.setSelectedFile(f);
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}

			int li_rewult = chooser.showSaveDialog(this);
			if (li_rewult == JFileChooser.APPROVE_OPTION) {
				readExcel(chooser.getSelectedFile().getPath());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		// File file = null;
		// file = new File("");
		// if (!file.exists()) {
		// MessageBox.show(fileNotExists);
		// return;
		// }
	}

	/**
	 * 本函数功能主要是读取Excel表格的数据，并将相应的数据添加到JTable中去。 本函数涉及到了JTable的初始化以及显示风格的设置。
	 */
	private void readExcel(String fileName) {
		FileInputStream input = null;
		POIFSFileSystem fileSystem = null;
		int rows = 0;
		int columns = 0;
		HSSFRow row = null;
		HSSFCell cell = null;
		BillCellItemVO billCellItem = null;
		Region region = null;
		JTable subTable = null;
		HSSFCellStyle style = null;

		try {
			input = new FileInputStream(fileName); // 构造文件输出流
			fileSystem = new POIFSFileSystem(input); // 打开文件
			HSSFWorkbook wb = new HSSFWorkbook(fileSystem); // 打开面板
			HSSFSheet sheet = wb.getSheetAt(0); // 取得第一个页签
			rows = sheet.getLastRowNum() + 1;
			columns = sheet.getRow(0).getLastCellNum() + 1;

			initTable(rows, columns);

			for (int i = 0; i < rows; i++) {
				row = sheet.getRow(i);
				for (int j = 0; j < columns; j++) {
					cell = row.getCell((short) j);
					if (cell == null) {
						continue;
					}
					style = cell.getCellStyle();
					billCellItem = new BillCellItemVO();
					billCellItem.setHalign(style.getAlignment());
					billCellItem.setValign(style.getVerticalAlignment());
					if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
						billCellItem.setCellvalue(cell.getStringCellValue());
						// style.getFont();

					} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
						billCellItem.setCellvalue(String.valueOf(cell.getNumericCellValue()));
					} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
						billCellItem.setCellvalue(String.valueOf(cell.getBooleanCellValue()));
					} else {
					}
					if (i == 0) {
						table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						table.getColumnModel().getColumn(j).setPreferredWidth((int) sheet.getColumnWidth((short) j) / 37);

						/**
						 * billCellItem.setColwidth(String.valueOf((int)sheet.getColumnWidth((short)j)/37));
						 * sheet.setColumnWidth((short) j, (short)
						 * (Short.valueOf(getCellItemVOAt(i, j).getColwidth()) *
						 * 37)); CellModel columnModel = new TableColumnModel();
						 * table.setColumnModel(columnModel);
						 */
					}
					if (j == 0) {

						/**
						 * 设置行高..............................................
						 */
						table.setRowHeight(i, row.getHeight() / 15);
						// ......................................................
						/**
						 * 设置表头的行高.........................................
						 * 未解决问题-------------JTable的行高怎样设置-------------
						 */
						JTableHeader tableHeader = table.getTableHeader();
						subTable = tableHeader.getTable();
						subTable.setRowHeight(i, row.getHeight() / 16);
						tableHeader.setTable(subTable);
						table.setTableHeader(tableHeader);
						// .........................................设置失败
					}
					table.setValueAtIgnoreEditEvent(billCellItem, i, j);
					table.revalidate();
					table.repaint();
				}

			}

			// 合并单元格，如下：------------------------------------问题：怎样去判断是否是合并的单元格.
			// 所谓索引，如何通过单个索引去判断单元格是否为合并单元格。

			/**
			 * 修正版 单元格合并成功，单元格合并问题解决
			 */
			int[] tableRows = null;
			int[] tableColumns = null;

			for (int j = 0; j < sheet.getNumMergedRegions(); j++) {
				region = sheet.getMergedRegionAt(j);
				if (region != null) {
					// 有待解决的问题
					// 表格内的元素合并规格是什么？
					tableRows = new int[region.getRowTo() - region.getRowFrom() + 1];
					tableColumns = new int[region.getColumnTo() - region.getColumnFrom() + 1];
					tableRows[0] = region.getRowFrom();
					tableColumns[0] = region.getColumnFrom();

					((CellSpan) cellAtt).combine(tableRows, tableColumns); //
				}
				table.revalidate();
				table.repaint();
			}
			table.setVisible(Boolean.TRUE);
			input.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * 本函数功能主要是初始化EXCEL文件的数据 以及对EXCEL文件的风格初始化   主要是风格报表样式   20130911  袁江晓修改，
	 * 郝明要求将内网袁江晓13年12月份修改的内容更新到外网，主要优化表格【李春娟/2014-05-05】
	 */
	private byte[] initStyleReportExcelData() throws Exception {
		// 函数内变量初始化
		HSSFWorkbook wb = null;
		HSSFSheet sheet = null;
		int[] cellSpan = null;
		HSSFCellStyle style = null;
		Color color = null;
		HSSFFont font = null;
		String[] colorStrings = null;
		wb = new HSSFWorkbook(); // 打开EXCEL面板
		sheet = wb.createSheet(); // 创建一个
		wb.setSheetName(0, defaultSheetName);
		HashMap<Short, HSSFFont> hm_font = new HashMap<Short, HSSFFont>();//存放字体的哈希表，主要是因为不能有更多的字体样式 key为字体颜色，value为字体格式【李春娟/2014-05-05】
		// 遍历整个表格，取出数据
		for (int i = 0; i < table.getRowCount(); i++) { // 直接从页面上取
			row = sheet.createRow(i);
			row.setHeight((short) 400);
			for (int j = 0; j < table.getColumnCount(); j++) {
				cell = row.createCell((short) j); // 创建单元格
				cell.setCellValue(getValueAt(i, j)); // 表格进行赋值
				// 返回单元格的合并数据
				cellSpan = getSpan(i, j);
				// 返回合并单元格错误
				if (cellSpan == null || cellSpan.length == 0 || cellSpan.length != 2) {
					System.out.println(cellSpanError);
					return null;
				}
				if (cellSpan[0] > 1 || cellSpan[1] > 1) {
					initExcelSpan(sheet, i, j, i + cellSpan[0] - 1, j + cellSpan[1] - 1);
				}
				if (getCellItemVOAt(i, j) != null) {
					BillCellItemVO itemvo = getCellItemVOAt(i, j);
					if (getCellItemVOAt(i, j).getColwidth() != null) {
						sheet.setColumnWidth(j, Integer.parseInt(getCellItemVOAt(i, j).getColwidth()) * 45);
					}
					style = wb.createCellStyle();
					BillCellItemVO vo = getCellItemVOAt(i, j);
					style.setAlignment((short) getCellItemVOAt(i, j).getHalign());
					style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
					String fgrgb = itemvo.getForeground();
					int[] frgb = new int[3];
					HSSFColor fonthsfcolor = null;
					if (fgrgb != null && fgrgb.indexOf(",") > -1) {
						String[] temp = fgrgb.trim().split(",");
						frgb[0] = Integer.parseInt(temp[0]);
						frgb[1] = Integer.parseInt(temp[1]);
						frgb[2] = Integer.parseInt(temp[2]);
						fonthsfcolor = wb.getCustomPalette().findSimilarColor((byte) frgb[0], (byte) frgb[1], (byte) frgb[2]);//获取字体比较接近的颜色
					}
					if (hm_font.size() == 0) {//不能够创建比较多的字体，否则excel中会报错 所以采取下面方法
						font = wb.createFont();//不能创建比较多的字体，所以适合做缓存
						font.setFontName("宋体");
						if (getCellItemVOAt(i, j).getFontsize() == null) {
							font.setFontHeightInPoints((short) 12);
						} else {
							font.setFontHeightInPoints(Short.valueOf(getCellItemVOAt(i, j).getFontsize()));
						}
						if (fonthsfcolor != null) {
							font.setColor(fonthsfcolor.getIndex());
							hm_font.put(fonthsfcolor.getIndex(), font);
						} else {
							hm_font.put((short) 32767, font);
						}
						style.setFont(font);
					} else {
						short indexNow;
						if (fonthsfcolor == null) {
							indexNow = (short) 32767;//如果颜色没有，则默认为32767
						} else {
							indexNow = fonthsfcolor.getIndex();
						}
						if (hm_font.keySet().contains(indexNow)) {
							style.setFont(hm_font.get(indexNow));
						} else {
							font = wb.createFont();//不能创建比较多的字体，所以适合做缓存
							font.setFontName("宋体");
							if (getCellItemVOAt(i, j).getFontsize() == null) {
								font.setFontHeightInPoints((short) 12);
							} else {
								font.setFontHeightInPoints(Short.valueOf(getCellItemVOAt(i, j).getFontsize()));
							}
							font.setColor(fonthsfcolor.getIndex());
							hm_font.put(fonthsfcolor.getIndex(), font);
							style.setFont(font);
						}
					}
					// 边框设置
					style.setBorderTop((short) 1);
					style.setBorderRight((short) 1);
					style.setBorderBottom((short) 1);
					style.setBorderLeft((short) 1);
					style.setTopBorderColor(HSSFColor.BLACK.index);
					style.setRightBorderColor(HSSFColor.BLACK.index);
					style.setBottomBorderColor(HSSFColor.BLACK.index);
					style.setLeftBorderColor(HSSFColor.BLACK.index);

					String bgrgb = vo.getBackground();
					int[] brgb = new int[3];
					if (bgrgb != null && bgrgb.indexOf(",") > -1) {
						String[] temp = bgrgb.trim().split(",");
						brgb[0] = Integer.parseInt(temp[0]);
						brgb[1] = Integer.parseInt(temp[1]);
						brgb[2] = Integer.parseInt(temp[2]);
						HSSFColor hsfcolor = wb.getCustomPalette().findSimilarColor((byte) brgb[0], (byte) brgb[1], (byte) brgb[2]);
						style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);//设置背景颜色
						style.setFillForegroundColor(hsfcolor.getIndex());//设置背景颜色
					}
					cell.setCellStyle(style);
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
				}
			}
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream(); //
		wb.write(bout);
		byte[] bytes = bout.toByteArray(); //
		bout.close();
		return bytes; //
	}

	/**
	 * 武坤萌 合并单元格函数
	 * 
	 * @param sheet
	 * @param row
	 * @param column
	 * @param row_sum
	 * @param column_sum
	 */
	private void initExcelSpan(HSSFSheet sheet, int row, int column, int row_sum, int column_sum) {
		Region region = new Region(row, (short) column, row_sum, (short) column_sum);
		sheet.addMergedRegion(region);
	}

	/**
	 * 徐长华 添加 武坤萌 修改
	 * 
	 * @param _key
	 * @return
	 */
	public String getValueAt(int _row, int _column) throws Exception {
		if ((BillCellItemVO) table.getValueAt(_row, _column) == null) {
			return "";
		}

		if (((BillCellItemVO) table.getValueAt(_row, _column)).getCellvalue() == null) {
			return "";
		}

		return ((BillCellItemVO) table.getValueAt(_row, _column)).getCellvalue(); //
	}

	/**
	 * 得到某一个格子上的数据!!
	 * 
	 * @param _row
	 * @param _column
	 * @return
	 * @throws Exception
	 */
	public BillCellItemVO getBillCellItemVOAt(int _row, int _column) {
		return (BillCellItemVO) table.getValueAt(_row, _column);
	}

	/**
	 * 通过设定的key值来得到BillCellItemVO
	 * 
	 * @param key
	 * @return BillCellItemVO
	 */
	public BillCellItemVO getBillCellItemVOAt(String key) {
		Vector v_allpos = findPosByKey(key); //
		if (v_allpos.size() > 0) {
			int[] li_rc = (int[]) v_allpos.get(0);
			int li_row = li_rc[0];
			int li_col = li_rc[1];
			BillCellItemVO cellItemVO = (BillCellItemVO) table.getValueAt(li_row, li_col);
			if (cellItemVO == null) {
				return null;
			} else {
				return cellItemVO; //
			}
		}
		return null;
	}

	/**
	 * 徐长华 添加
	 * 
	 * @param _row
	 * @param _column
	 * @return
	 */
	private BillCellItemVO getCellItemVOAt(int _row, int _column) {
		return ((BillCellItemVO) table.getValueAt(_row, _column)); //
	}

	private void exportSQL() {
		try {
			if (templetcode == null) {
				MessageBox.show(this, "当前模板编码为空,不能导出!请先选择一个模板!"); //
				return; //
			}

			JLabel label_1 = new JLabel("数据库类型:", JLabel.RIGHT); //
			label_1.setBounds(5, 5, 80, 20); //

			JRadioButton radio_sqlserver = new JRadioButton("SQLServer", true); //
			JRadioButton radio_oracle = new JRadioButton("Orcale"); //
			ButtonGroup group = new ButtonGroup(); //
			group.add(radio_sqlserver); //
			group.add(radio_oracle); //

			radio_sqlserver.setBounds(85, 5, 100, 20); // //
			radio_oracle.setBounds(185, 5, 80, 20); // //

			JLabel label_2 = new JLabel("SQL显示风格:"); //
			label_2.setBounds(265, 5, 80, 20); //

			JComboBox comBox_sqltype = new JComboBox(); //
			comBox_sqltype.addItem("横向"); //
			comBox_sqltype.addItem("纵向"); //
			comBox_sqltype.setBounds(345, 5, 80, 20); //

			JPanel panel = new JPanel(); //
			panel.setLayout(null); //
			panel.add(label_1); //
			panel.add(radio_sqlserver); //
			panel.add(radio_oracle); //
			panel.add(label_2); //
			panel.add(comBox_sqltype); //
			panel.setPreferredSize(new Dimension(440, 30)); //

			if (JOptionPane.showConfirmDialog(this, panel, "导出配置", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { //
				return;
			}

			String str_dbtype = ""; //
			if (radio_sqlserver.isSelected()) {
				str_dbtype = WLTConstants.SQLSERVER;
			} else if (radio_oracle.isSelected()) {
				str_dbtype = WLTConstants.ORACLE;
			}
			ReportServiceIfc reportService = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class);
			String sb_sqls = reportService.getExportCellTempletSQL(this.templetcode, str_dbtype, (comBox_sqltype.getSelectedItem().toString().equals("横向") ? false : true)); //
			BillDialog dialog = new BillDialog(this, "导出SQL", 1000, 700);
			JTextArea textArea = new JTextArea(sb_sqls.toString()); //
			textArea.setFont(new Font("宋体", Font.PLAIN, 12));
			textArea.select(0, 0); //
			dialog.getContentPane().add(new JScrollPane(textArea)); //
			dialog.setVisible(true); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex);
		}
	}

	/**
	 * 新建一个模板,以前不能写备注【李春娟/2012-04-12】
	 * 
	 * @throws Exception
	 */
	private void onNew() throws Exception {
		CellTMO celltmo = new CellTMO();
		BillCardPanel cardPanel = new BillCardPanel(celltmo); //创建一个卡片面板
		cardPanel.insertRow(); //卡片新增一行!
		cardPanel.setEditableByInsertInit(); //设置卡片编辑状态为新增时的设置
		BillCardDialog dialog = new BillCardDialog(this, "网格模板", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //弹出卡片新增框
		dialog.setVisible(true); //显示卡片窗口
		if (dialog.getCloseType() == 1) { //如是是点击确定返回!将则卡片中的数据赋给列表!
			this.billCellVO = new BillCellVO(); //
			BillVO billvo = dialog.getBillVO();
			billCellVO.setId(billvo.getStringValue("id")); //
			billCellVO.setTempletcode(billvo.getStringValue("templetcode")); //
			billCellVO.setTempletname(billvo.getStringValue("templetname")); //
			billCellVO.setDescr(billvo.getStringValue("descr"));
			initTable(15, 8); //
		}
	}

	private void onSave() throws Exception {
		try {
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			dealSave(); //
		} catch (Exception ex) {
			MessageBox.showException(table, ex); //
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
	}

	/**
	 * 保存..
	 */
	public void dealSave() throws Exception {
		if (this.billCellVO == null) {
			MessageBox.show(this, "BillCellVO is null,Can't Save."); //
			return;
		}

		if (table.getCellEditor() != null) {
			table.getCellEditor().stopCellEditing(); //
		}
		long ll_1 = System.currentTimeMillis(); //
		getBillCellVOFromUI(); // 从页面上取得数据送入对象
		FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkMetaDataServiceIfc.class);
		service.saveBillCellVO(null, this.billCellVO); //
		long ll_2 = System.currentTimeMillis(); //
		MessageBox.show(this, "保存数据成功,共耗时[" + (ll_2 - ll_1) + "]毫秒!");
	}

	/**
	 * 从页面上取得最新数据送入对象
	 */
	private void getBillCellVOFromUI() {
		int li_rows = table.getRowCount(); //
		int li_cols = table.getColumnCount(); //
		this.billCellVO.setRowlength(li_rows); // 总共有几行..
		this.billCellVO.setCollength(li_cols); // 总共有几列..

		BillCellItemVO[][] cellItemVO = new BillCellItemVO[li_rows][li_cols]; //
		for (int i = 0; i < li_rows; i++) {
			for (int j = 0; j < li_cols; j++) {
				if (table.getValueAt(i, j) == null) {
					cellItemVO[i][j] = new BillCellItemVO(); //
				} else {
					cellItemVO[i][j] = (BillCellItemVO) table.getValueAt(i, j); //
				}

				cellItemVO[i][j].setCellrow(i); //
				cellItemVO[i][j].setCellcol(j); //

				cellItemVO[i][j].setRowheight("" + table.getRowHeight(i)); //
				cellItemVO[i][j].setColwidth("" + table.getColumnModel().getColumn(j).getWidth()); //

				int[] li_spans = ((CellSpan) this.cellAtt).getSpan(i, j); //
				cellItemVO[i][j].setSpan(li_spans[0] + "," + li_spans[1]); //
			}
		}

		this.billCellVO.setCellItemVOs(cellItemVO); //
	}

	/**
	 * 得到页面上当前的实际数据..
	 * 
	 * @return
	 */
	public BillCellVO getBillCellVO() {
		getBillCellVOFromUI();
		return this.billCellVO;
	}

	/**
	 * 打开
	 */
	private void onOpen() throws Exception {
		OpenCellTempletDialog dialog = new OpenCellTempletDialog(this); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			String code = dialog.getCellTempletcode(); //
			String name = dialog.getCellTempletname(); //

			if (code != null) {
				loadBillCellData(code); //
			}
			textfield_status.setText("模板编码:[" + code + "],模板名称:[" + name + "]"); //
		}
	}

	public void stopEditing() {
		try {
			if (getTable().getRowCount() >= 0 && getTable().getCellEditor() != null) {
				getTable().getCellEditor().stopCellEditing(); //
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public JTable getTable() {
		return table;
	}

	private void initPopMenu() {
		popMenu = new JPopupMenu(); //
		popMenuItem_insertRow = new JMenuItem("插入行"); //
		popMenuItem_insertCol = new JMenuItem("插入列"); //

		popMenuItem_delRow = new JMenuItem("删除行"); //
		popMenuItem_delCol = new JMenuItem("删除列"); //
		popMenuItem_cleardata = new JMenuItem("清空数据"); //
		popMenuItem_loc = new JMenuItem("冻结窗格"); //
		popMenuItem_unloc = new JMenuItem("取消冻结窗格");
		popMenuItem_setTooltipText = new JMenuItem("设置提示文字"); //
		popMenuItem_setCellEditable = new JMenuItem("设置可编辑"); //
		popMenuItem_setCellKey = new JMenuItem("设置CellKey"); //
		item_AutoSetCellKey = new JMenuItem("自动设置CellKey"); // 自动设置CellKey.
		popMenuItem_setCellEditFormula = new JMenuItem("设置编辑公式"); // 设置编辑公式
		popMenuItem_setCellIsHtmlHref = new JMenuItem("是否Html链接"); //设置是否Html超链接风格
		popMenuItem_setCellValidateFormula = new JMenuItem("设置校验公式"); //设置校验公式
		popMenuItem_setCellDesc = new JMenuItem("Set CellDesc"); //
		popMenuItem_showCellProp = new JMenuItem("显示Cell所有属性");

		popMenuItem_insertRow.setPreferredSize(new Dimension(120, 19)); //
		popMenuItem_insertCol.setPreferredSize(new Dimension(120, 19)); //
		popMenuItem_delRow.setPreferredSize(new Dimension(120, 19)); //
		popMenuItem_delCol.setPreferredSize(new Dimension(120, 19)); //
		popMenuItem_cleardata.setPreferredSize(new Dimension(120, 19)); //
		popMenuItem_loc.setPreferredSize(new Dimension(120, 19));
		popMenuItem_unloc.setPreferredSize(new Dimension(120, 19));
		popMenuItem_setTooltipText.setPreferredSize(new Dimension(120, 19)); //
		popMenuItem_setCellEditable.setPreferredSize(new Dimension(120, 19)); //
		popMenuItem_setCellKey.setPreferredSize(new Dimension(120, 19)); //
		item_AutoSetCellKey.setPreferredSize(new Dimension(120, 19)); //
		popMenuItem_setCellEditFormula.setPreferredSize(new Dimension(120, 19)); //
		popMenuItem_setCellIsHtmlHref.setPreferredSize(new Dimension(120, 19)); //
		popMenuItem_setCellValidateFormula.setPreferredSize(new Dimension(120, 19)); //
		popMenuItem_setCellDesc.setPreferredSize(new Dimension(120, 19)); //
		popMenuItem_showCellProp.setPreferredSize(new Dimension(120, 19)); //

		popMenuItem_insertRow.addActionListener(this); //
		popMenuItem_insertCol.addActionListener(this); //
		popMenuItem_delRow.addActionListener(this); //
		popMenuItem_delCol.addActionListener(this); //
		popMenuItem_cleardata.addActionListener(this); //
		popMenuItem_loc.addActionListener(this); //
		popMenuItem_unloc.addActionListener(this); //
		popMenuItem_setTooltipText.addActionListener(this); //
		popMenuItem_setCellEditable.addActionListener(this); //
		popMenuItem_setCellKey.addActionListener(this); //
		item_AutoSetCellKey.addActionListener(this); //
		popMenuItem_setCellEditFormula.addActionListener(this); //
		popMenuItem_setCellIsHtmlHref.addActionListener(this); //
		popMenuItem_setCellValidateFormula.addActionListener(this); //
		popMenuItem_setCellDesc.addActionListener(this); //
		popMenuItem_showCellProp.addActionListener(this); //

		popMenu.add(popMenuItem_insertRow); //
		popMenu.add(popMenuItem_insertCol); //
		popMenu.addSeparator(); //
		popMenu.add(popMenuItem_delRow); //
		popMenu.add(popMenuItem_delCol); //
		popMenu.add(popMenuItem_cleardata); //
		popMenu.addSeparator(); //
		popMenu.add(popMenuItem_loc); //
		popMenu.add(popMenuItem_unloc); //
		popMenuItem_unloc.setVisible(false);
		popMenu.addSeparator(); //
		popMenu.add(popMenuItem_setTooltipText); //
		popMenu.add(popMenuItem_setCellEditable); //
		popMenu.add(popMenuItem_setCellKey); //
		popMenu.add(item_AutoSetCellKey); //
		popMenu.add(popMenuItem_setCellEditFormula); //
		popMenu.add(popMenuItem_setCellIsHtmlHref); //
		popMenu.add(popMenuItem_setCellValidateFormula); //
		popMenu.add(popMenuItem_setCellDesc); //
		popMenu.add(popMenuItem_showCellProp); //
	}

	/**
	 * 增加网格控件的超链接点击事件!!!
	 * @param _listener
	 */
	public void addBillCellHtmlHrefListener(BillCellHtmlHrefListener _listener) {
		vc_HtmlHrefListrners.add(_listener); //
	}

	/**
	 * 点击了表格
	 */
	private void onClickedTable(Object source, Point _point, boolean _isCtrl, boolean _isShift, boolean _isAlt) {
		int li_selectedRowCout = table.getSelectedRowCount();
		int li_selectedColCout = table.getSelectedColumnCount();
		int li_row = table.rowAtPoint(_point);//
		int li_column = table.columnAtPoint(_point); //
		if (li_row >= 0 && li_column >= 0) { //必须是有效的行与列,即如果点在网线上面则不触发!
			if (li_selectedRowCout <= 1 && li_selectedColCout <= 1) {
				table.setRowSelectionInterval(li_row, li_row);
				table.setColumnSelectionInterval(li_column, li_column); //
			} else {
				return; //
			}
			if (_isCtrl || _isShift) { //如果是按住上Ctrl键或者Shift键,则直接返回！因为有多选几个格子再右键选择【钻取】【饼图查看】等,如果没有这个控制，显得
				return;
			}
			BillCellItemVO itemVO = getBillCellItemVOAt(li_row, li_column); ////取得数据
			if (itemVO == null) {
				return;
			}
			if (!"Y".equals(itemVO.getIshtmlhref())) { //如果不是设置成超链接的,则直接返回!!
				return; //
			}

			//如果监听了超链接事件!!
			if (vc_HtmlHrefListrners.size() > 0) { //只有注册了事件才继续做!!
				try {
					table.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //光标变成等待的
					BillCellHtmlHrefEvent event = new BillCellHtmlHrefEvent(li_row, li_column, itemVO, this); //构建一个事件对象!!
					event.setCtrlDown(_isCtrl); //
					event.setShiftDown(_isShift); //
					event.setAltDown(_isAlt); //
					for (int i = 0; i < vc_HtmlHrefListrners.size(); i++) { //遍历各个监听器!!!
						BillCellHtmlHrefListener listener = (BillCellHtmlHrefListener) vc_HtmlHrefListrners.get(i); //
						listener.onBillCellHtmlHrefClicked(event); ////调用监听器的动作!!
					}
				} catch (Exception _ex) {
					MessageBox.showException(this, _ex);
				} finally {
					table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //光标变回来!!!
				}
			}
		}
	}

	/**
	 *
	 * @param source
	 * @param x
	 * @param y
	 */
	private void showPopMenu(Object source, Point _point) {
		int li_selectedRowCout = table.getSelectedRowCount();
		int li_selectedColCout = table.getSelectedColumnCount();

		int li_row = table.rowAtPoint(_point);//
		int li_column = table.columnAtPoint(_point); //

		if (li_row >= 0 && li_column >= 0 && li_selectedRowCout <= 1 && li_selectedColCout <= 1) {
			table.setRowSelectionInterval(li_row, li_row);
			table.setColumnSelectionInterval(li_column, li_column); //
		}

		// if (!this.getTitleBtnBar().isVisible()) {
		// //return;
		// }

		if (!isAllowShowPopMenu()) {
			return;
		}

		if (this.custPopMenu != null) {
			custPopMenu.show((java.awt.Component) source, (int) _point.getX(), (int) _point.getY()); //
		} else {
			if (popMenuItem_loc != null) {
				popMenuItem_loc.setVisible(!isLocked);
			}
			if (popMenuItem_unloc != null) {
				popMenuItem_unloc.setVisible(isLocked);
			}
			popMenu.show((java.awt.Component) source, (int) _point.getX(), (int) _point.getY()); //
		}
	}

	/**
	 * 插入行操作..
	 */
	private void onInsertRow() {
		try {
			JLabel label = new JLabel("新增行数:", SwingConstants.RIGHT); //
			JTextField textField = new JTextField("1", SwingConstants.RIGHT); //
			JCheckBox checkBox = new JCheckBox("是否在后面增加"); //
			checkBox.setFocusable(false);
			textField.setHorizontalAlignment(JTextField.RIGHT); //
			label.setBounds(5, 10, 60, 20); //
			textField.setBounds(65, 10, 75, 20); //
			checkBox.setBounds(150, 10, 150, 20); //
			JPanel panel = new JPanel(); //
			panel.setLayout(null); //
			panel.add(label); //
			panel.add(textField); //
			panel.add(checkBox); //
			panel.setPreferredSize(new Dimension(300, 30)); //

			if (JOptionPane.showConfirmDialog(this, panel, "你是否真的想插入新行?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}

			int li_insertRows = Integer.parseInt(textField.getText());
			boolean isAddOnTail = checkBox.isSelected(); //
			getBillCellVOFromUI(); //
			int li_row = table.getSelectedRow(); // 指定在哪一行前插入...
			if (li_row < 0) {
				li_row = table.getRowCount() - 1; //
			} else {
				if (isAddOnTail) { //如果是
					li_row = li_row + 1;
				}
			}

			int li_column = table.getSelectedColumn(); // 指定在哪一前插入
			BillCellItemVO[][] oldItemVOs = this.billCellVO.getCellItemVOs(); //
			int li_oldrowcount = table.getRowCount(); // 行总数
			int li_oldcolcount = table.getColumnCount(); // 列总数

			int li_newrowcount = li_oldrowcount + li_insertRows; // 新的行数=旧的行数+插入的行数.
			BillCellItemVO[][] newItemVOs = new BillCellItemVO[li_newrowcount][li_oldcolcount]; //

			for (int i = 0; i < li_row; i++) {
				for (int j = 0; j < li_oldcolcount; j++) {
					if (oldItemVOs[i][j] != null) {
						newItemVOs[i][j] = oldItemVOs[i][j]; //
					}
				}
			}

			for (int i = li_row + li_insertRows; i < li_newrowcount; i++) {
				for (int j = 0; j < li_oldcolcount; j++) {
					if (oldItemVOs[i - li_insertRows][j] != null) {
						newItemVOs[i][j] = oldItemVOs[i - li_insertRows][j]; //
					}
				}
			}

			initTable(li_newrowcount, li_oldcolcount); //
			this.billCellVO.setRowlength(li_newrowcount); //
			this.billCellVO.setCollength(li_oldcolcount); //
			this.billCellVO.setCellItemVOs(newItemVOs); //
			loadBillCellData(this.billCellVO); //

			Rectangle rect = this.table.getCellRect(li_row, li_column, true); //
			table.scrollRectToVisible(rect); // 滚动到对应的行的位置!!!!!!!!!!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private void onInsertColumn() {
		try {
			JLabel label = new JLabel("新增列数:", SwingConstants.RIGHT); //
			JTextField textField = new JTextField("1", SwingConstants.RIGHT); //
			textField.setHorizontalAlignment(JTextField.RIGHT); //
			JCheckBox checkBox = new JCheckBox("是否在后面增加"); //
			checkBox.setFocusable(false);
			label.setBounds(5, 10, 60, 20); //
			textField.setBounds(65, 10, 75, 20); //
			checkBox.setBounds(150, 10, 150, 20); //
			JPanel panel = new JPanel(); //
			panel.setLayout(null); //
			panel.add(label); //
			panel.add(textField); //
			panel.add(checkBox); //
			panel.setPreferredSize(new Dimension(300, 30)); //

			if (JOptionPane.showConfirmDialog(this, panel, "你是否真的想插入新列?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			int li_insertCols = Integer.parseInt(textField.getText());

			boolean isAddOnTail = checkBox.isSelected(); //
			getBillCellVOFromUI(); //
			int li_col = table.getSelectedColumn(); //
			if (li_col < 0) {
				li_col = table.getColumnCount() - 1;
			} else {
				if (isAddOnTail) {
					li_col = li_col + 1;
				}
			}

			int li_row = table.getSelectedRow(); //
			BillCellItemVO[][] oldItemVOs = this.billCellVO.getCellItemVOs(); //
			int li_oldrowcount = table.getRowCount(); // 行总数
			int li_oldcolcount = table.getColumnCount(); // 列总数

			int li_newcolcount = li_oldcolcount + li_insertCols; //
			BillCellItemVO[][] newItemVOs = new BillCellItemVO[li_oldrowcount][li_newcolcount]; //

			// 遍历所有的行..
			for (int i = 0; i < li_oldrowcount; i++) {
				// 处理前面的列
				for (int j = 0; j < li_col; j++) {
					if (oldItemVOs[i][j] != null) {
						newItemVOs[i][j] = oldItemVOs[i][j]; //
					}
				}

				// 处理后面的列
				for (int j = li_col + li_insertCols; j < li_newcolcount; j++) {
					if (oldItemVOs[i][j - li_insertCols] != null) {
						newItemVOs[i][j] = oldItemVOs[i][j - li_insertCols]; //
					}
				}

			}

			initTable(li_oldrowcount, li_newcolcount); //
			this.billCellVO.setRowlength(li_oldrowcount); //
			this.billCellVO.setCollength(li_newcolcount); //
			this.billCellVO.setCellItemVOs(newItemVOs); //
			loadBillCellData(this.billCellVO); //

			Rectangle rect = this.table.getCellRect(li_row, li_col, false);
			table.scrollRectToVisible(rect); // 滚动到对应的行的位置!!!!!!!!!!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 删除行..
	 */
	private void onDeleteRow() {
		try {
			int li_row = table.getSelectedRow(); //
			if (li_row < 0) {
				MessageBox.show(this, "Please select a row.");
				return;
			}

			if (JOptionPane.showConfirmDialog(this, "你是否真的想删除选中的所有行吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}

			int[] li_rows = table.getSelectedRows(); // 选中的所有行数!!!
			int li_column = table.getSelectedColumn(); // 选中的列.

			getBillCellVOFromUI(); //

			int li_delrowcount = 0; //
			for (int i = 0; i < li_rows.length; i++) {
				Object obj = table.getValueAt(li_rows[i], li_column);
				if (obj == null) {
					li_delrowcount = li_delrowcount + 1;
				} else {
					BillCellItemVO itemVO = (BillCellItemVO) obj; //
					String str_span = itemVO.getSpan(); //
					if (str_span == null) {
						li_delrowcount = li_delrowcount + 1; //
					} else {
						int li_spanrow = Integer.parseInt(str_span.split(",")[0]);
						if (li_spanrow > 0) {
							li_delrowcount = li_delrowcount + li_spanrow;
						}
					}
				}
			}

			BillCellItemVO[][] oldItemVOs = this.billCellVO.getCellItemVOs(); //
			int li_oldrowcount = table.getRowCount(); // 行总数
			int li_oldcolcount = table.getColumnCount(); // 列总数

			int li_newrowcount = li_oldrowcount - li_delrowcount; //
			BillCellItemVO[][] newItemVOs = new BillCellItemVO[li_newrowcount][li_oldcolcount]; //

			for (int i = 0; i < li_row; i++) {
				for (int j = 0; j < li_oldcolcount; j++) {
					if (oldItemVOs[i][j] != null) {
						newItemVOs[i][j] = oldItemVOs[i][j]; //
					}
				}
			}

			for (int i = li_row; i < li_newrowcount; i++) {
				for (int j = 0; j < li_oldcolcount; j++) {
					if (oldItemVOs[i + li_delrowcount][j] != null) {
						newItemVOs[i][j] = oldItemVOs[i + li_delrowcount][j]; //
					}
				}
			}

			initTable(li_newrowcount, li_oldcolcount); //
			this.billCellVO.setRowlength(li_newrowcount); //
			this.billCellVO.setCollength(li_oldcolcount); //
			this.billCellVO.setCellItemVOs(newItemVOs); //
			loadBillCellData(this.billCellVO); //

			if (li_row - 1 < 0) {
				Rectangle rect = this.table.getCellRect(0, li_column, true);
				table.scrollRectToVisible(rect); // 滚动到对应的行的位置!!!!!!!!!!!!
			} else {
				Rectangle rect = this.table.getCellRect(li_row - 1, li_column, true);
				table.scrollRectToVisible(rect); // 滚动到对应的行的位置!!!!!!!!!!!!
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 删除列..
	 */
	private void onDeleteColumn() {
		try {
			int li_col = table.getSelectedColumn(); // 选中的列
			if (li_col < 0) {
				MessageBox.show(this, "Please select a column.");
				return;
			}

			if (JOptionPane.showConfirmDialog(this, "你是否真的想删除选中的所有列吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}

			int[] li_cols = table.getSelectedColumns(); // 选中的所有行数!!!
			int li_row = table.getSelectedRow(); //

			getBillCellVOFromUI(); // //

			int li_delcolcount = 0; //
			for (int i = 0; i < li_cols.length; i++) {
				Object obj = table.getValueAt(li_row, li_cols[i]);
				if (obj == null) {
					li_delcolcount = li_delcolcount + 1;
				} else {
					BillCellItemVO itemVO = (BillCellItemVO) obj; //
					String str_span = itemVO.getSpan(); //
					if (str_span == null) {
						li_delcolcount = li_delcolcount + 1; //
					} else {
						int li_spancol = Integer.parseInt(str_span.split(",")[1]);
						if (li_spancol > 0) {
							li_delcolcount = li_delcolcount + li_spancol;
						}
					}
				}
			}

			BillCellItemVO[][] oldItemVOs = this.billCellVO.getCellItemVOs(); //
			int li_oldrowcount = table.getRowCount(); // 行总数
			int li_oldcolcount = table.getColumnCount(); // 列总数

			int li_newcolcount = li_oldcolcount - li_delcolcount; //
			BillCellItemVO[][] newItemVOs = new BillCellItemVO[li_oldrowcount][li_newcolcount]; //

			// 遍历所有的行..
			for (int i = 0; i < li_oldrowcount; i++) {
				// 处理前面的列
				for (int j = 0; j < li_col; j++) {
					if (oldItemVOs[i][j] != null) {
						newItemVOs[i][j] = oldItemVOs[i][j]; //
					}
				}

				// 处理后面的列
				for (int j = li_col; j < li_newcolcount; j++) {
					if (oldItemVOs[i][j + li_delcolcount] != null) {
						newItemVOs[i][j] = oldItemVOs[i][j + li_delcolcount]; //
					}
				}

			}

			initTable(li_oldrowcount, li_newcolcount); //
			this.billCellVO.setRowlength(li_oldrowcount); //
			this.billCellVO.setCollength(li_newcolcount); //
			this.billCellVO.setCellItemVOs(newItemVOs); //
			loadBillCellData(this.billCellVO); //

			if (li_col - 1 < 0) {
				Rectangle rect = this.table.getCellRect(li_row, 0, true);
				table.scrollRectToVisible(rect); // 滚动到对应的行的位置!!!!!!!!!!!!
			} else {
				Rectangle rect = this.table.getCellRect(li_row, li_col - 1, true);
				table.scrollRectToVisible(rect); // 滚动到对应的行的位置!!!!!!!!!!!!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 冻结窗口 仿EXCEL的冻结窗格
	 */
	public boolean onLock() {
		try {
			int li_col = table.getSelectedColumn(); // 选中的列
			int li_row = table.getSelectedRow(); // 选中的行
			if (li_col < 0) {
				MessageBox.show(this, "请选择单元格!");
				return false;
			}
			if (JOptionPane.showConfirmDialog(this, "你是否真的想冻结窗格吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return false;
			}
			getJLM(table, scroll).setFixRowCol(li_row - 1, li_col - 1);
			scroll.updateUI();
			isLocked = true;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}

	/**
	 * 取消冻结窗口 仿EXCEL
	 */
	public boolean onUnLock() {
		try {
			int li_row = table.getSelectedRow(); // 选中的行
			if (li_row < 0) {
				MessageBox.show(this, "请选择单元格!");
				return false;
			}
			if (JOptionPane.showConfirmDialog(this, "你是否真的想取消冻结窗格吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return false;
			}
			getJLM(table, scroll).unLock();
			scroll.updateUI();
			isLocked = false;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}

	/**
	 * 设置Help.
	 */
	private void setToolTipText() {
		int li_row = table.getSelectedRow();
		int li_column = table.getSelectedColumn(); //
		if (li_row < 0 || li_column < 0) {
			MessageBox.show(this, "Please select a row"); //
			return; //
		}

		BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(li_row, li_column); //

		JPanel panel = new JPanel();
		panel.setLayout(null); //
		JLabel label = new JLabel("Help TipText", SwingConstants.RIGHT);
		JTextField textField = new JTextField(); //
		label.setBounds(0, 10, 80, 20); //
		textField.setBounds(85, 10, 120, 20); //
		panel.setPreferredSize(new Dimension(220, 25)); //
		panel.add(label); //
		panel.add(textField); //

		if (itemVO != null && itemVO.getCellhelp() != null) {
			textField.setText(itemVO.getCellhelp()); //
		}
		JOptionPane.showMessageDialog(this, panel, "Set Help", JOptionPane.INFORMATION_MESSAGE); //
		String str_help = textField.getText();

		if (itemVO == null) {
			itemVO = new BillCellItemVO();
		}
		itemVO.setCellhelp(str_help); //
	}

	private void setCellEditable() {
		int li_row = table.getSelectedRow();
		int li_column = table.getSelectedColumn(); //
		if (li_row < 0 || li_column < 0) {
			MessageBox.show(this, "Please select a row"); //
			return; //
		}

		BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(li_row, li_column); //

		JPanel panel = new JPanel();
		panel.setLayout(null); //
		JLabel label = new JLabel("Is can editable", SwingConstants.RIGHT);
		JCheckBox checkBox = new JCheckBox(); //
		label.setBounds(0, 10, 80, 20); //
		checkBox.setBounds(85, 10, 120, 20); //
		checkBox.setPreferredSize(new Dimension(220, 25)); //
		panel.add(label); //
		panel.add(checkBox); //

		if (itemVO == null) {
			checkBox.setSelected(true);
		} else {
			if (itemVO.getIseditable() == null || itemVO.getIseditable().equals("Y")) {
				checkBox.setSelected(true);
			} else {
				checkBox.setSelected(false);
			}
		}

		JOptionPane.showMessageDialog(this, panel, "Set Editable", JOptionPane.INFORMATION_MESSAGE); //

		String str_result = null;
		if (checkBox.isSelected()) {
			str_result = "Y"; //
		} else {
			str_result = "N"; //
		}

		int[] rows = table.getSelectedRows();
		int[] columns = table.getSelectedColumns();
		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO_tmp = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO_tmp == null) {
					itemVO_tmp = new BillCellItemVO(); //
					itemVO_tmp.setIseditable(str_result); //
					table.setValueAtIgnoreEditEvent(itemVO_tmp, i, j); // 重置数据
				} else {
					itemVO_tmp.setIseditable(str_result); //	
				}
			}
		}
	}

	private void setCellIsHtmlHref() {
		int li_row = table.getSelectedRow();
		int li_column = table.getSelectedColumn(); //
		if (li_row < 0 || li_column < 0) {
			MessageBox.show(this, "Please select a row"); //
			return; //
		}
		BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(li_row, li_column); //
		String str_oldIsHtmlHref = null; //
		if (itemVO != null) {
			str_oldIsHtmlHref = itemVO.getIshtmlhref(); // 旧的公式...
		}

		JLabel label = new JLabel("是否Html链接"); //
		label.setHorizontalAlignment(SwingConstants.RIGHT); //
		JCheckBox checkBox = new JCheckBox(); //
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		panel.add(label); ////
		panel.add(checkBox); ////
		if (str_oldIsHtmlHref != null && str_oldIsHtmlHref.equals("Y")) {
			checkBox.setSelected(true); //
		}
		JOptionPane.showMessageDialog(this, panel, "设置是否Html超链接风格", JOptionPane.INFORMATION_MESSAGE); //
		int[] rows = table.getSelectedRows(); //
		int[] columns = table.getSelectedColumns(); //
		for (int i = 0; i < rows.length; i++) {
			for (int j = 0; j < columns.length; j++) {
				setCellItemIshtmlhref(rows[i], columns[j], checkBox.isSelected()); //指定特定行与列
			}
		}
		table.revalidate(); //
		table.repaint(); //
	}

	//在前台设置某行某列是否有超链接效果!!在做报表时需要这个效果!!
	public void setCellItemIshtmlhref(int _row, int _col, boolean _isHtmlHref) {
		BillCellItemVO itemVO_tmp = (BillCellItemVO) table.getValueAt(_row, _col);
		if (itemVO_tmp == null) {
			itemVO_tmp = new BillCellItemVO(); //
			itemVO_tmp.setIshtmlhref(_isHtmlHref ? "Y" : "N"); //
			table.setValueAtIgnoreEditEvent(itemVO_tmp, _row, _col); // 重置数据
		} else {
			itemVO_tmp.setIshtmlhref(_isHtmlHref ? "Y" : "N"); //	
		}
	}

	/**
	 * 设置网格的编辑公式
	 */
	private void setCellEditFormula() {
		int li_row = table.getSelectedRow();
		int li_column = table.getSelectedColumn(); //
		if (li_row < 0 || li_column < 0) {
			MessageBox.show(this, "Please select a row"); //
			return; //
		}

		int[] rows = table.getSelectedRows(); //
		int[] columns = table.getSelectedColumns(); //
		BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(li_row, li_column); //
		String str_oldEditFormula = null; //
		if (itemVO != null) {
			str_oldEditFormula = itemVO.getEditformula(); // 旧的公式...
		}

		RefDialog_BigArea dialog = new RefDialog_BigArea(this, "网格编辑公式", new RefItemVO(str_oldEditFormula, null, str_oldEditFormula), this, null); //
		dialog.initialize(); // 初始化...
		dialog.setVisible(true); //
		if (dialog.getCloseType() != 1) {//这里需要判断一下，如果不是点击确定退出的就直接返回【李春娟/2012-04-12】
			return;
		}
		RefItemVO refItemVO = dialog.getReturnRefItemVO(); //
		String str_newEditFormula = refItemVO.getId(); // 新的编辑公式

		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO_tmp = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO_tmp == null) {
					itemVO_tmp = new BillCellItemVO(); //
					itemVO_tmp.setEditformula(str_newEditFormula); //
					table.setValueAtIgnoreEditEvent(itemVO_tmp, i, j); // 重置数据
				} else {
					itemVO_tmp.setEditformula(str_newEditFormula); //	
				}
			}
		}
	}

	/**
	 * 设置校验公式
	 */
	private void setCellValidateFormula() {
		int li_row = table.getSelectedRow();
		int li_column = table.getSelectedColumn(); //
		if (li_row < 0 || li_column < 0) {
			MessageBox.show(this, "Please select a row"); //
			return; //
		}

		int[] rows = table.getSelectedRows(); //
		int[] columns = table.getSelectedColumns(); //
		BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(li_row, li_column); //
		String str_oldValidateFormula = null; //
		if (itemVO != null) {
			str_oldValidateFormula = itemVO.getValidateformula(); // 旧的公式...
		}

		RefDialog_BigArea dialog = new RefDialog_BigArea(this, "网格校验公式", new RefItemVO(str_oldValidateFormula, null, str_oldValidateFormula), this, null); //
		dialog.initialize(); // 初始化...
		dialog.setVisible(true); //
		if (dialog.getCloseType() != 1) {
			return;
		}
		RefItemVO refItemVO = dialog.getReturnRefItemVO(); //
		String str_validateFormula = refItemVO.getId(); // 新的校验公式..

		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO_tmp = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO_tmp == null) {
					itemVO_tmp = new BillCellItemVO(); //
					itemVO_tmp.setValidateformula(str_validateFormula);
					table.setValueAtIgnoreEditEvent(itemVO_tmp, i, j);
				} else {
					itemVO_tmp.setValidateformula(str_validateFormula);
				}
			}
		}
	}

	/**
	 * 设置Help.
	 */
	private void setCellkey() {
		int li_row = table.getSelectedRow();
		int li_column = table.getSelectedColumn(); //
		if (li_row < 0 || li_column < 0) {
			MessageBox.show(this, "Please select a row"); //
			return; //
		}

		BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(li_row, li_column); //
		JPanel panel = new JPanel();
		panel.setLayout(null); //
		JLabel label = new JLabel("Cell Key", SwingConstants.RIGHT);
		JTextField textField = new JTextField(); //
		label.setBounds(0, 10, 80, 20); //
		textField.setBounds(85, 10, 120, 20); //
		panel.setPreferredSize(new Dimension(220, 25)); //
		panel.add(label); //
		panel.add(textField); //

		if (itemVO != null && itemVO.getCellkey() != null) {
			textField.setText(itemVO.getCellkey()); //
		}

		JOptionPane.showMessageDialog(this, panel, "Set Cell Key", JOptionPane.INFORMATION_MESSAGE); //
		String str_key = textField.getText();

		int[] rows = table.getSelectedRows();
		int[] columns = table.getSelectedColumns();
		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO_tmp = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO_tmp == null) {
					itemVO_tmp = new BillCellItemVO(); //
					itemVO_tmp.setCellkey(str_key); //
					table.setValueAtIgnoreEditEvent(itemVO_tmp, i, j); // 重置数据
				} else {
					itemVO_tmp.setCellkey(str_key); //	
				}
			}
		}
	}

	/**
	 * 自动设置CellKey,即直接拿行号-列号作为Cellkey的值..
	 */
	private void autoSetCellKey() {
		int li_row = table.getSelectedRow();
		int li_column = table.getSelectedColumn(); //
		if (li_row < 0 || li_column < 0) {
			MessageBox.show(this, "Please select a row"); //
			return; //
		}

		if (JOptionPane.showConfirmDialog(this, "你是否真的想自动设置所有CellKey?这将冲掉以前所有设置值!", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}

		int[] rows = table.getSelectedRows(); // 所有选中的行
		int[] columns = table.getSelectedColumns(); // 所有选中的列

		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO_tmp = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO_tmp == null) {
					itemVO_tmp = new BillCellItemVO(); //
					itemVO_tmp.setCellkey((i + 1) + "-" + table.getColumnName(j)); //
					table.setValueAtIgnoreEditEvent(itemVO_tmp, i, j); // 重置数据
				} else {
					itemVO_tmp.setCellkey((i + 1) + "-" + table.getColumnName(j)); //	
				}
			}
		}

		table.revalidate();
		table.repaint();

		MessageBox.show(this, "自动设置所有CellKey成功!");
	}

	/**
	 * 设置Help.
	 */
	private void setCellDesc() {
		int li_row = table.getSelectedRow();
		int li_column = table.getSelectedColumn(); //
		if (li_row < 0 || li_column < 0) {
			MessageBox.show(this, "Please select a row"); //
			return; //
		}

		BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(li_row, li_column); //
		JPanel panel = new JPanel();
		panel.setLayout(null); //
		JLabel label = new JLabel("Cell Key", SwingConstants.RIGHT);
		JTextField textField = new JTextField(); //
		label.setBounds(0, 10, 80, 20); //
		textField.setBounds(85, 10, 120, 20); //
		panel.setPreferredSize(new Dimension(220, 25)); //
		panel.add(label); //
		panel.add(textField); //

		if (itemVO != null && itemVO.getCelldesc() != null) {
			textField.setText(itemVO.getCelldesc()); //
		}

		JOptionPane.showMessageDialog(this, panel, "Set Cell Key", JOptionPane.INFORMATION_MESSAGE); //
		String str_desc = textField.getText();

		int[] rows = table.getSelectedRows();
		int[] columns = table.getSelectedColumns();
		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO_tmp = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO_tmp == null) {
					itemVO_tmp = new BillCellItemVO(); //
					table.setValueAtIgnoreEditEvent(itemVO_tmp, i, j); // 重置数据
				}
				itemVO_tmp.setCelldesc(str_desc); // 设置说明
				itemVO_tmp.setItemTypechanged(true);
			}
		}
	}

	/**
	 * 显示格子所有属性.
	 */
	public void showCellAllProp() {
		try {
			int li_row = table.getSelectedRow();
			int li_column = table.getSelectedColumn(); //
			if (li_row < 0 || li_column < 0) {
				MessageBox.show(this, "请选中数据进行钻取操作!"); //
				return; //
			}
			BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(li_row, li_column); //
			if (itemVO == null) {
				MessageBox.show(this, "选中格子的内容为空!"); //
				return;

			}
			StringBuilder sb_prop = new StringBuilder("Cell的所有属性值:\r\n");
			sb_prop.append("CellKey=[" + itemVO.getCellkey() + "]\r\n");
			sb_prop.append("CellType=[" + itemVO.getCelltype() + "]\r\n");
			sb_prop.append("CellValue=[" + itemVO.getCellvalue() + "]\r\n");
			sb_prop.append("CellRowCol=[" + itemVO.getCellrow() + "," + itemVO.getCellcol() + "]\r\n");
			sb_prop.append("CellWidthHeight=[" + itemVO.getColwidth() + "," + itemVO.getRowheight() + "]\r\n");
			sb_prop.append("CellSpan=[" + itemVO.getSpan() + "]\r\n"); //合并
			sb_prop.append("Loadformula(加载公式)=[" + itemVO.getLoadformula() + "]\r\n");
			sb_prop.append("Editformula(编辑公式)=[" + itemVO.getEditformula() + "]\r\n");
			sb_prop.append("Validateformula(校验公式)=[" + itemVO.getValidateformula() + "]\r\n");

			sb_prop.append("\r\n"); //

			sb_prop.append("自定义Map中的所有值:\r\n"); //
			HashMap custMap = itemVO.getCustPropMap(); //
			Iterator it_keys = custMap.keySet().iterator(); //
			while (it_keys.hasNext()) {
				String str_key = (String) it_keys.next(); //
				Object obj_value = custMap.get(str_key); //
				if (obj_value == null) {
					sb_prop.append("[" + str_key + "]=[null]\r\n"); //
				} else {
					sb_prop.append("[" + str_key + "]=[" + obj_value.getClass().getName() + "][" + obj_value.toString() + "]\r\n"); //
				}
			}
			MessageBox.showTextArea(this, sb_prop.toString()); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 加载数据
	 */
	private void loadBillCellData(String _templetCode) throws Exception {
		loadBillCellData(_templetCode, null, null); //
	}

	private void loadBillCellData(String _templetCode, String _billNo, String _descr) throws Exception {
		loadBillCellData(getMetaService().getBillCellVO(_templetCode, _billNo, _descr)); //
	}

	/**
	 * 通过一个BillNO从实例表中取出数据送入模板中!!!!
	 * 
	 * @param _billno
	 * @throws Exception
	 */
	public boolean loadBillData(String _billno) throws Exception {
		HashVO[] hashvo = getMetaService().getCellLoadDate(this.billCellVO.getId(), _billno);
		if (hashvo.length > 0) {
			for (int i = 0; i < hashvo.length; i++) {
				String value = hashvo[i].getStringValue("cellvalue");
				value = (value == "null" ? "" : value);
				this.setValueAt(hashvo[i].getStringValue("cellkey"), value);
			}
			this.table.repaint();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param _billno
	 * @throws Exception
	 */
	public void SaveBillData(String _billno) throws Exception {
		stopEditing(); //
		HashMap hashmap = new HashMap(); //
		String[] str_keys = getKeys(); //
		for (int i = 0; i < str_keys.length; i++) {
			hashmap.put(str_keys[i], (getValueAt(str_keys[i]) == null ? null : getValueAt(str_keys[i]).trim())); //
		}
		getMetaService().getCellSaveDate(this.billCellVO.getId(), _billno, hashmap); // 调远程方法保存!!
	}

	/**
	 * 通过Billno和key求和 条件必须billno对应的每个key必须一样
	 * 
	 * @param _billno
	 * @param hashmap
	 * @throws Exception
	 */
	public HashMap sumBillData(String[] billno, String[] cellkey) throws Exception {
		// HashVO[] hashvo3 = null;
		// HashMap temp = new HashMap();
		// for (int i = 0; i < billno.length; i++) {
		// hashvo3 = UIUtil.getHashVoArrayByDS(null, "select * from
		// pub_billcelltemplet_data where billno='" + billno[i] + "'");
		// for (int j = 0; j < hashvo3.length; j++) {
		//
		// for (int k = 0; k < cellkey.length; k++) {
		// if (temp.get(hashvo3[j].getStringValue("cellkey")) == null) {
		// if (cellkey[k].equals(hashvo3[j].getStringValue("cellkey"))) {
		// temp.put(cellkey[k], hashvo3[j].getStringValue("cellvalue"));
		//
		// break;
		// }
		// } else {
		// if (cellkey[k].equals(hashvo3[j].getStringValue("cellkey"))) {
		// int value = Integer.parseInt((String) temp.get(cellkey[k])) +
		// Integer.parseInt(hashvo3[j].getStringValue("cellvalue"));
		// temp.put(cellkey[k], Integer.toString(value));
		// break;
		// }
		// }
		//
		// }
		// }
		// }
		// return temp;
		ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //

		return service.sumBillData(billno, cellkey);

		// this.setValueAt("", Integer.toString(temp));
	}

	public void setValueAt(HashMap _map) {
		stopEditing(); //
		String[] str_keys = (String[]) _map.keySet().toArray(new String[0]);
		for (int i = 0; i < str_keys.length; i++) {
			setValueAt(str_keys[i], (String) _map.get(str_keys[i])); //
		}
		table.revalidate();
		table.repaint();
	}

	/**
	 * 单击列表单元格时，下方状态栏显示信息方法
	 * @author YangQing/2013-09-25
	 */
	private void selectedInfo() {
		int[] sel_row = this.getTable().getSelectedRows();//得到所选中的行号
		int[] sel_column = this.getTable().getSelectedColumns();//得到所选中的列号
		String[][] selectCelValue = new String[sel_row.length][sel_column.length];//存放选中单元格的值
		List list = new ArrayList();
		HashSet<String> spaned = new HashSet<String>();//被合并了。
		for (int i = 0; i < sel_row.length; i++) {
			for (int y = 0; y < sel_column.length; y++) {
				if (spaned.contains(sel_row[i] + "_" + sel_column[y])) { //如果被合并了。
					continue;
				}
				BillCellItemVO itemvo = this.getBillCellItemVOAt(sel_row[i], sel_column[y]);
				if (itemvo == null) {
					continue;
				}
				list.add(itemvo.getCellvalue());
				int span[] = getSpan(sel_row[i], sel_column[y]); //
				for (int j = 0; j < span[0]; j++) {
					for (int j2 = 0; j2 < span[1]; j2++) {
						if (j == 0 && j2 == 0) {
							continue;
						}
						spaned.add((j + sel_row[i]) + "_" + (j2 + sel_column[y]));
					}
				}
			}
		}
		Pattern pattern = Pattern.compile("^\\+?\\d*\\.?\\d+$|^-?\\d*\\.?\\d+$");//匹配正负整数、小数
		String celinfo = "";//底下状态栏要显示的字符串
		double sum = 0;//数值总和
		int numvalue_count = 0;//数值个数
		boolean allPersent = true; //默认
		for (int i = 0; i < list.size(); i++) {
			String str_selValue = (String) list.get(i);
			if (str_selValue == null) {
				continue;
			}
			boolean isper = false;
			if (str_selValue.lastIndexOf("%") == (str_selValue.length() - 1) && str_selValue.length() > 1) {
				str_selValue = str_selValue.substring(0, str_selValue.lastIndexOf("%"));
				isper = true;
			}
			Matcher matcher = pattern.matcher(str_selValue);
			if (matcher.find()) {
				double dle_selvalue = Double.parseDouble(str_selValue);
				if (isper) {
					dle_selvalue = dle_selvalue / 100;
				}
				sum += dle_selvalue;//如果是数字，就累加上，算和、算平均值
				numvalue_count++;
				if (!isper) {
					allPersent = false;
				}
			}
		}
		if (numvalue_count <= 1) { //如果数字一个以下 郝明20130926
			celinfo = "◆ 计数：" + (list.size()) + " ◆";
		} else {
			BigDecimal big = new BigDecimal(sum / numvalue_count).setScale(4, BigDecimal.ROUND_HALF_UP);
			boolean ydy = false; // 是否保留 约等于。
			if (sum / numvalue_count != big.doubleValue()) {
				ydy = true;
			}
			java.text.DecimalFormat d_f = new java.text.DecimalFormat("########.####");
			double bigvalue = big.doubleValue();
			String avgvalue = d_f.format(allPersent ? bigvalue * 100f : bigvalue);
			double lastSum = (double) (allPersent ? sum * 100f : sum);
			celinfo = "◆ 计数：" + (list.size()) + "  数值个数：" + (numvalue_count) + "  求和：" + d_f.format(lastSum) + (allPersent ? "%" : "") + "  平均值" + (ydy ? "≈" : ":") + avgvalue + (allPersent ? "%" : "") + " ◆";
		}
		DeskTopPanel.setSelectInfo(celinfo);//显示
	}

	/**
	 * 通过Billno和key求和 条件必须billno对应的每个key必须一样
	 * 
	 * @param _billno
	 * @param hashmap
	 * @throws Exception
	 */
	public HashMap getSumCorpBillDataByContent(String[] billno, String[] cellkey) throws Exception {
		// HashVO[] hashvo3 = null;
		// HashMap temp = new HashMap();
		// for (int i = 0; i < billno.length; i++) {
		// hashvo3 = UIUtil.getHashVoArrayByDS(null, "select * from
		// pub_billcelltemplet_data where billno='" + billno[i] + "'");
		// for (int j = 0; j < hashvo3.length; j++) {
		//
		// for (int k = 0; k < cellkey.length; k++) {
		// if (temp.get(hashvo3[j].getStringValue("cellkey")) == null) {
		// if (cellkey[k].equals(hashvo3[j].getStringValue("cellkey"))) {
		// temp.put(cellkey[k], hashvo3[j].getStringValue("cellvalue"));
		//
		// break;
		// }
		// } else {
		// if (cellkey[k].equals(hashvo3[j].getStringValue("cellkey"))) {
		// int value = Integer.parseInt((String) temp.get(cellkey[k])) +
		// Integer.parseInt(hashvo3[j].getStringValue("cellvalue"));
		// temp.put(cellkey[k], Integer.toString(value));
		// break;
		// }
		// }
		//
		// }
		// }
		// }
		// return temp;
		ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //

		return service.getSumCorpBillDataByContent(billno, cellkey);

		// this.setValueAt("", Integer.toString(temp));
	}

	/**
	 * 加载数据
	 * 
	 * @param _billCellVO
	 * @throws Exception
	 */
	public void loadBillCellData(BillCellVO _billCellVO) throws Exception {
		this.billCellVO = _billCellVO; //
		this.billCellItemVos = _billCellVO.getCellItemVOs(); //
		this.templetcode = _billCellVO.getTempletcode(); // 模板名称
		this.templetname = _billCellVO.getTempletname(); //
		this.billNo = _billCellVO.getBillNo(); //

		initTable(_billCellVO.getRowlength(), _billCellVO.getCollength()); // 初始化表格
		BillCellItemVO[][] cellItemVOs = _billCellVO.getCellItemVOs(); //
		if (cellItemVOs != null && cellItemVOs.length > 0) {
			int[] li_widths = new int[cellItemVOs[0].length]; //
			int[] li_heights = new int[cellItemVOs.length]; //
			int li_maxwidth = 0; //
			int li_maxheight = 0; //
			int li_itemRowHeight = 0;
			int li_itemColWidth = 0;
			//先算出一行中的最大的高度
			if (ifSetRowHeight) {
				for (int i = 0; i < cellItemVOs.length; i++) { //遍历各行
					li_maxheight = 0; //
					for (int j = 0; j < cellItemVOs[i].length; j++) {
						if (cellItemVOs[i][j] != null && cellItemVOs[i][j].getRowheight() != null) {
							li_itemRowHeight = Integer.parseInt(cellItemVOs[i][j].getRowheight()); //
							if (li_itemRowHeight > li_maxheight) {
								li_maxheight = li_itemRowHeight; //
							}
						}
					}
					if (li_maxheight == 0) {
						li_maxheight = 22;
					}
					li_heights[i] = li_maxheight; //
				}
			}

			//算出一列中最大的宽度
			for (int j = 0; j < cellItemVOs[0].length; j++) {
				li_maxwidth = 0; //
				for (int i = 0; i < cellItemVOs.length; i++) { //遍历各行
					if (cellItemVOs[i][j] != null && cellItemVOs[i][j].getColwidth() != null) {
						li_itemColWidth = Integer.parseInt(cellItemVOs[i][j].getColwidth()); //
						if (li_itemColWidth > li_maxwidth) {
							li_maxwidth = li_itemColWidth; //
						}
					}
				}
				if (li_maxwidth == 0) {
					li_maxwidth = 75;
				}
				li_widths[j] = li_maxwidth; //
			}
			BillCellItemVO itemVO = null;
			String str_span = "";
			String[] str_items = null;
			int li_spanrow_length = 0;
			int li_spancol_length = 0;
			int[] li_spanrows = null;
			int[] li_spancols = null;
			for (int i = 0; i < cellItemVOs.length; i++) { //遍历各行
				if (ifSetRowHeight) {
					this.table.setRowHeight(i, li_heights[i]); //行高
					if (rowHeaderTable != null) { //可能没有头!
						this.rowHeaderTable.setRowHeight(i, li_heights[i] + 1); //行高
					}
				}
				for (int j = 0; j < cellItemVOs[i].length; j++) {
					itemVO = cellItemVOs[i][j]; //
					if (itemVO != null) {
						this.table.setValueAtIgnoreEditEvent(itemVO, i, j); //
						if (i == 0) {
							this.table.getColumnModel().getColumn(j).setPreferredWidth(li_widths[j]); //设置列宽,这要要处理一个最大化的问题!!
						}
						str_span = itemVO.getSpan(); //
						if (str_span != null) {
							str_items = str_span.split(","); //
							li_spanrow_length = Integer.parseInt(str_items[0]);
							li_spancol_length = Integer.parseInt(str_items[1]);
							if (li_spanrow_length > 1 || li_spancol_length > 1) { // 如果有一个大于1,说明是合并的..
								li_spanrows = new int[li_spanrow_length]; //
								li_spancols = new int[li_spancol_length]; //
								for (int k = 0; k < li_spanrows.length; k++) {
									li_spanrows[k] = i + k;
								}

								for (int k = 0; k < li_spancols.length; k++) {
									li_spancols[k] = j + k;
								}

								((CellSpan) cellAtt).combine(li_spanrows, li_spancols); //
							}
						}
					}
				}
			}
		}
		table.clearSelection();
		table.revalidate();
		table.repaint();
	}

	public void setValueAt(String _value, int _row, int _column) {
		BillCellItemVO cellItemVO = new BillCellItemVO(); // 
		cellItemVO.setCellvalue(_value); //
		table.setValueAtIgnoreEditEvent(cellItemVO, _row, _column); //
	}

	/**
	 * 对某些key项设值
	 * 
	 * @param _key
	 */
	public void setValueAt(String _key, String _value) {
		// stopEditing(); //
		Vector v_allpos = findPosByKey(_key); //
		for (int i = 0; i < v_allpos.size(); i++) {
			int[] li_rc = (int[]) v_allpos.get(i); //
			int li_row = li_rc[0];
			int li_col = li_rc[1];
			BillCellItemVO cellItemVO = (BillCellItemVO) table.getValueAt(li_row, li_col);
			if (cellItemVO == null) {
				cellItemVO = new BillCellItemVO(); // 
			}
			cellItemVO.setCellvalue(_value); //
		}
		// table.revalidate();
		// table.repaint();
	}

	//清空所有设置了key项的格子数据为空!!!
	public void resetAllKeysCell() {
		String[] str_keys = getKeys(); //
		if (str_keys != null && str_keys.length > 0) {
			for (int i = 0; i < str_keys.length; i++) {
				setValueAt(str_keys[i], null); //
			}
		}
	}

	/**
	 * 对某些key项设值,并触发编辑公式
	 * 
	 * @param _key
	 */
	public void setValueAtTrrigerEditEvent(String _key, String _value) {
		// stopEditing(); //
		Vector v_allpos = findPosByKey(_key); //
		for (int i = 0; i < v_allpos.size(); i++) {
			int[] li_rc = (int[]) v_allpos.get(i); //
			int li_row = li_rc[0];
			int li_col = li_rc[1];
			BillCellItemVO cellItemVO = (BillCellItemVO) table.getValueAt(li_row, li_col);
			if (cellItemVO == null) {
				cellItemVO = new BillCellItemVO(); // 
			}
			cellItemVO.setCellvalue(_value); //
			table.setValueAt(cellItemVO, li_row, li_col); //
		}
	}

	/**
	 * 返回合并行数列数
	 * 
	 * @param _row
	 * @param _column
	 * @return
	 */
	// TODO
	public int[] getSpan(int _row, int _column) {
		return ((CellSpan) cellAtt).getSpan(_row, _column);
	}

	/**
	 * 取得所有行数
	 * 
	 * @return
	 */
	public int getRowCount() {
		return table.getRowCount(); //
	}

	/**
	 * 得到所有列数
	 * 
	 * @return
	 */
	public int getColumnCount() {
		return table.getColumnCount(); //
	}

	/**
	 * 取得整个表格的宽度
	 * 
	 * @return
	 */
	public int getTableAllWidth() {
		int li_allwidth = 0; //
		for (int i = 0; i < getColumnCount(); i++) {
			li_allwidth = li_allwidth + table.getColumnModel().getColumn(i).getPreferredWidth();
		}
		return li_allwidth + 20;
	}

	public int getTableAllHeight() {
		int li_allheight = 0; //
		for (int i = 0; i < getRowCount(); i++) {
			li_allheight = li_allheight + table.getRowHeight(i); //
		}
		return li_allheight + 10;
	}

	/**
	 * 对某些key项设值
	 * 
	 * @param _key
	 */
	public String getValueAt(String _key) {
		Vector v_allpos = findPosByKey(_key); //
		if (v_allpos.size() > 0) {
			int[] li_rc = (int[]) v_allpos.get(0);
			int li_row = li_rc[0];
			int li_col = li_rc[1];
			BillCellItemVO cellItemVO = (BillCellItemVO) table.getValueAt(li_row, li_col);
			if (cellItemVO == null) {
				return null;
			} else {
				return cellItemVO.getCellvalue(); //
			}
		}
		return null;
	}

	/**
	 * 取得所有key,即那些设了key的格子
	 * 
	 * @return
	 */
	public String[] getKeys() {
		HashMap map = new HashMap(); //
		int li_rowcount = table.getRowCount();
		int li_colcount = table.getColumnCount(); //
		for (int i = 0; i < li_rowcount; i++) {
			for (int j = 0; j < li_colcount; j++) {
				BillCellItemVO cellItemVO = (BillCellItemVO) table.getValueAt(i, j);
				if (cellItemVO != null) {
					if (cellItemVO.getCellkey() != null) {
						map.put(cellItemVO.getCellkey(), cellItemVO.getCellkey()); //
					}
				}
			}
		}

		String[] str_keys = (String[]) map.keySet().toArray(new String[0]); //
		return str_keys; //
	}

	/**
	 * 将所有数据返回成一个HashMap,其中的key就是各个格子设的cellkey
	 * 
	 * @return
	 */
	public HashMap getDataAsHashMap() {
		HashMap map = new HashMap(); //
		String[] str_keys = getKeys(); //
		for (int i = 0; i < str_keys.length; i++) {
			map.put(str_keys[i], getValueAt(str_keys[i])); //
		}
		return map; //
	}

	public void setEditable(boolean _editable) {
		table.setEditable(_editable); //
	}

	/**
	 * 对某些key项设值
	 * 
	 * @param _key
	 */
	public void setEditable(String _key, boolean _editable) {
		Vector v_allpos = findPosByKey(_key); //
		for (int i = 0; i < v_allpos.size(); i++) {
			int[] li_rc = (int[]) v_allpos.get(i);
			int li_row = li_rc[0];
			int li_col = li_rc[1];
			BillCellItemVO cellItemVO = (BillCellItemVO) table.getValueAt(li_row, li_col);
			if (cellItemVO == null) {
				cellItemVO = new BillCellItemVO(); // 
			}
			cellItemVO.setIseditable((_editable == true) ? "Y" : "N"); //
		}
	}

	/**
	 * 对某些key项设值
	 * 
	 * @param _key
	 */
	public void setEditableByPrefix(String _key, boolean _editable) {
		Vector v_allpos = findPosByKeyPrefix(_key); //
		for (int i = 0; i < v_allpos.size(); i++) {
			int[] li_rc = (int[]) v_allpos.get(i);
			int li_row = li_rc[0];
			int li_col = li_rc[1];
			BillCellItemVO cellItemVO = (BillCellItemVO) table.getValueAt(li_row, li_col);
			if (cellItemVO == null) {
				cellItemVO = new BillCellItemVO(); // 
			}
			cellItemVO.setIseditable((_editable == true) ? "Y" : "N"); //
		}
	}

	public void clearCell() {
		String[] keys = this.getKeys();
		for (int i = 0; i < keys.length; i++) {
			this.setValueAt(keys[i], "");
		}
		this.updateUI();
	}

	private Vector findPosByKey(String _key) {
		Vector v_allpos = new Vector(); //
		int li_rowcount = table.getRowCount();
		int li_colcount = table.getColumnCount(); //
		for (int i = 0; i < li_rowcount; i++) {
			for (int j = 0; j < li_colcount; j++) {
				BillCellItemVO cellItemVO = (BillCellItemVO) table.getValueAt(i, j);
				if (cellItemVO != null) {
					if (cellItemVO.getCellkey() != null && cellItemVO.getCellkey().equalsIgnoreCase(_key)) {
						v_allpos.add(new int[] { i, j }); //
					}
				}
			}
		}
		return v_allpos; //
	}

	private Vector findPosByKeyPrefix(String _key) {
		Vector v_allpos = new Vector(); //
		int li_rowcount = table.getRowCount();
		int li_colcount = table.getColumnCount(); //
		for (int i = 0; i < li_rowcount; i++) {
			for (int j = 0; j < li_colcount; j++) {
				BillCellItemVO cellItemVO = (BillCellItemVO) table.getValueAt(i, j);
				if (cellItemVO != null) {
					if (cellItemVO.getCellkey() != null && cellItemVO.getCellkey().indexOf(_key) == 0) {
						v_allpos.add(new int[] { i, j }); //
					}
				}
			}
		}
		return v_allpos; //
	}

	/**
	 * 合并处理
	 */
	private void onCombine() {
		int[] rows = table.getSelectedRows();
		int[] columns = table.getSelectedColumns();

		if ((rows == null) || (columns == null))
			return;
		if ((rows.length < 1) || (columns.length < 1))
			return;

		((CellSpan) cellAtt).combine(rows, columns); //
		// table.clearSelection();
		table.revalidate();
		table.repaint();
	}

	/**
	 * 从某个指定的行与列开始,从行方向上向下合并几行,在列方向上向后合并几列!!!
	 * 比如 span(2,3,5,7),即表示从第2行,第3列开始,在行方向上向下合并5行,在列方向上向右合并7列
	 * @param _startRow 从哪一行开始!
	 * @param _startCol 从哪一列开始!
	 * @param _spanRows 行方向上合并几行,如果不合并,应设成1,但设成0也没事,因为做了容错处理!
	 * @param _spanCols 列方向上合并几列,如果不合并,应设成1,但设成0也没事,因为做了容错处理!
	 */
	public void span(int _startRow, int _startCol, int _spanRowAreas, int _spanColAreas) {
		if (_spanRowAreas <= 0) {
			_spanRowAreas = 1;
		}
		if (_spanColAreas <= 0) {
			_spanColAreas = 1;
		}

		int[] spanRows = new int[_spanRowAreas]; //
		for (int i = 0; i < _spanRowAreas; i++) {
			spanRows[i] = _startRow + i; //
		}
		int[] spanCols = new int[_spanColAreas]; //
		for (int i = 0; i < _spanColAreas; i++) {
			spanCols[i] = _startCol + i; //
		}
		span(spanRows, spanCols); //合并!!!
	}

	/**
	 * 对指定的行与列范围进行合并,比如同样针对上面的要求,即从第2行,第3列开始,在行方向上向下合并5行,在列方向上向右合并7列
	 * 则参数应是 span(new int[]{2,3,4,5,6},new int[]{3,4,5,6,7,8,9})
	 * @param _row 
	 * @param _columns
	 */
	public void span(int[] _row, int[] _columns) {
		((CellSpan) cellAtt).combine(_row, _columns); //
		// table.clearSelection();
		table.revalidate();
		table.repaint();
	}

	/**
	 * 分隔处理
	 */
	private void onSplit() {
		int column = table.getSelectedColumn();
		int row = table.getSelectedRow();
		if (row < 0 || column < 0) {
			return;
		}

		((CellSpan) cellAtt).split(row, column);
		// table.clearSelection();
		table.revalidate();
		table.repaint();
	}

	/**
	 * 颜色选择变化
	 */
	private void onColorchanged(int _type) {
		Color initColor = null; //
		String str_title = ""; //
		if (_type == 1) {
			str_title = "前景"; //
			initColor = btn_forecolor.getBackground(); //
		} else if (_type == 2) {
			str_title = "背景"; //
			initColor = btn_backcolor.getBackground(); //
		}

		Color returnColor = JColorChooser.showDialog(this, str_title, initColor); //
		if (returnColor == null) {
			return;
		}
		if (_type == 1) {
			btn_forecolor.setBackground(returnColor); //
		} else if (_type == 2) {
			btn_backcolor.setBackground(returnColor); //
		}

		String str_colorText = returnColor.getRed() + "," + returnColor.getGreen() + "," + returnColor.getBlue(); //
		int[] columns = table.getSelectedColumns();
		int[] rows = table.getSelectedRows();
		if ((rows == null) || (columns == null))
			return;
		if ((rows.length < 1) || (columns.length < 1))
			return;

		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO == null) {
					itemVO = new BillCellItemVO(); //

				}
				if (_type == 1) {
					itemVO.setForeground(str_colorText); //
				} else if (_type == 2) {
					itemVO.setBackground(str_colorText); //
				}
				table.setValueAtIgnoreEditEvent(itemVO, i, j); // 重置数据
			}
		}

		if (table.getCellEditor() != null) {
			table.getCellEditor().stopCellEditing(); //
		}
		// table.clearSelection(); //
		table.revalidate();
		table.repaint();
	}

	/**
	 * 清空数据
	 */
	private void cleardata() {
		int[] columns = table.getSelectedColumns();
		int[] rows = table.getSelectedRows();
		if ((rows == null) || (columns == null))
			return;
		if ((rows.length < 1) || (columns.length < 1))
			return;

		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO == null) {
					itemVO = new BillCellItemVO(); //

				}

				//跳过不可编辑的
				if (!(itemVO.getIseditable() != null && itemVO.getIseditable().equals("N"))) {
					itemVO.setCellvalue("");
				}

				table.setValueAtIgnoreEditEvent(itemVO, i, j); // 重置数据
			}
		}

		if (table.getCellEditor() != null) {
			table.getCellEditor().stopCellEditing();
		}
		table.revalidate();
		table.repaint();
	}

	/**
	 * 字体发生改变
	 */
	private void onFontChanged() {
		this.stopEditing(); //
		int[] columns = table.getSelectedColumns();
		int[] rows = table.getSelectedRows();
		if ((rows == null) || (columns == null))
			return;
		if ((rows.length < 1) || (columns.length < 1))
			return;

		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO == null) {
					itemVO = new BillCellItemVO(); //

				}
				itemVO.setFonttype((String) combox_fonttype.getSelectedItem()); //
				itemVO.setFontstyle("" + combox_fontstyle.getSelectedIndex()); //
				itemVO.setFontsize((String) combox_fontsize.getSelectedItem()); //
				table.setValueAtIgnoreEditEvent(itemVO, i, j); // 重置数据
			}
		}

		// table.clearSelection(); //
		table.revalidate();
		table.repaint();
	}

	/**
	 * 左右排序发生变化!!!
	 */
	private void onHalign(int _halign) {
		this.stopEditing();
		int[] columns = table.getSelectedColumns();
		int[] rows = table.getSelectedRows();
		if ((rows == null) || (columns == null))
			return;
		if ((rows.length < 1) || (columns.length < 1))
			return;

		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO == null) {
					itemVO = new BillCellItemVO(); //

				}
				itemVO.setHalign(_halign); //
				table.setValueAtIgnoreEditEvent(itemVO, i, j); // 重置数据
			}
		}

		// table.clearSelection(); //
		table.revalidate();
		table.repaint();
	}

	/**
	 * 
	 * @param rows
	 * @param columns
	 * @param _halign 排序,一定要注意：1-靠左;2-靠中;3-靠右，不是SwingConst.CENTER
	 */
	public void setHalign(int[] rows, int[] columns, int _halign) {
		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO == null) {
					itemVO = new BillCellItemVO(); //

				}
				itemVO.setHalign(_halign); //
				table.setValueAtIgnoreEditEvent(itemVO, i, j); // 重置数据
			}
		}

		// table.clearSelection(); //
		table.revalidate();
		table.repaint();
	}

	public void setBackground(String _bgcolor, int _i, int _j) {
		BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(_i, _j);
		if (itemVO == null) {
			itemVO = new BillCellItemVO(); //

		}
		itemVO.setBackground(_bgcolor);
		table.setValueAtIgnoreEditEvent(itemVO, _i, _j); // 重置数据

		table.revalidate();
		table.repaint();
	}

	public void setValign(int[] rows, int[] columns, int _valign) {
		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO == null) {
					itemVO = new BillCellItemVO(); //

				}
				itemVO.setValign(_valign); //
				table.setValueAtIgnoreEditEvent(itemVO, i, j); // 重置数据
			}
		}

		// table.clearSelection(); //
		table.revalidate();
		table.repaint();
	}

	/**
	 * 上下排序发生变化!!!
	 */
	private void onValign(int _halign) {
		this.stopEditing();
		int[] columns = table.getSelectedColumns();
		int[] rows = table.getSelectedRows();
		if ((rows == null) || (columns == null))
			return;
		if ((rows.length < 1) || (columns.length < 1))
			return;

		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO == null) {
					itemVO = new BillCellItemVO(); //

				}
				itemVO.setValign(_halign); //
				table.setValueAtIgnoreEditEvent(itemVO, i, j); // 重置数据
			}
		}

		// table.clearSelection(); //
		table.revalidate();
		table.repaint();
	}

	/**
	 * 设置是否显示CellKey
	 * 
	 * @param _checkbox
	 */
	private void setIsShowCellKey(JCheckBox _checkbox) {
		((MultiSpanCellTable) table).setShowCellKey(_checkbox.isSelected()); //
		table.revalidate();
		table.repaint();
	}

	/**
	 * 控件类型发生变化...
	 */
	private void onCellTypechanged() {
		String str_celltype = (String) combox_celltype.getSelectedItem();
		int[] columns = table.getSelectedColumns();
		int[] rows = table.getSelectedRows();
		if ((rows == null) || (columns == null))
			return;
		if ((rows.length < 1) || (columns.length < 1))
			return;

		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO == null) {
					itemVO = new BillCellItemVO(); //
					itemVO.setCelltype(str_celltype); // 设置控件类型....
					itemVO.setItemTypechanged(true); //
					table.setValueAtIgnoreEditEvent(itemVO, i, j); // 重置数据
				} else {
					itemVO.setCelltype(str_celltype); // 设置控件类型....
					itemVO.setItemTypechanged(true); //
				}
			}
		}

		// table.clearSelection(); //
		table.revalidate();
		table.repaint();
	}

	/**
	 * 执行编辑公式..
	 * @param _formula
	 * @param _row
	 * @param _col
	 */
	public void execEditFormula(String _formula, int _row, int _col) {
		formulaParse = new JepFormulaParseAtUI(this, _row, _col); //
		if (_formula != null && !_formula.trim().equals("")) {
			_formula = UIUtil.replaceAll(_formula, "\r", "");
			_formula = UIUtil.replaceAll(_formula, "\n", "");
			String[] str_editorFormulas = _formula.split(";"); //  
			for (int i = 0; i < str_editorFormulas.length; i++) { // 遍历执行所有公式
				formulaParse.execFormula(str_editorFormulas[i]); //
			}
		} // 遍历公式结束!!!

	}

	private FrameWorkMetaDataServiceIfc getMetaService() throws Exception {
		if (metaservice == null) {
			metaservice = (FrameWorkMetaDataServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkMetaDataServiceIfc.class);
		}
		return metaservice;
	}

	class ComboxCellRender extends DefaultListCellRenderer {
		private static final long serialVersionUID = 270627647569903076L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus); //
			Color color = (Color) value; //
			label.setOpaque(true); //
			label.setBackground(color);
			return label;
		}
	}

	public String getTempletcode() {
		return templetcode;
	}

	public void setTempletcode(String templetcode) {
		this.templetcode = templetcode;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public boolean isAllowShowPopMenu() {
		return isAllowShowPopMenu;
	}

	public void setAllowShowPopMenu(boolean _isAllowShowPopMenu) {
		this.isAllowShowPopMenu = _isAllowShowPopMenu;
	}

	/**
	 * 
	 * @author xch
	 * 
	 */
	class MyColor extends Color {

		private static final long serialVersionUID = -2769180621490384291L;

		public MyColor(Color _color) {
			super(_color.getRed(), _color.getGreen(), _color.getBlue()); //
		}

		public String toString() {
			return " "; //
		}
	}

	class RowNumberRender extends JLabel implements TableCellRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
			this.setOpaque(true); //不透明
			this.setBackground(new Color(240, 240, 240)); // //
			this.setHorizontalAlignment(SwingConstants.CENTER); //
			this.setText("" + (rowIndex + 1)); // //
			return this;
		}
	}

	class TableRowResizer extends MouseInputAdapter {
		public Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);

		private int mouseYOffset, resizingRow;
		private Cursor otherCursor = resizeCursor;
		private JTable tmptable;

		public TableRowResizer(JTable table) {
			this.tmptable = table;
			table.addMouseListener(this);
			table.addMouseMotionListener(this);
		}

		private int getResizingRow(Point p) {
			return getResizingRow(p, tmptable.rowAtPoint(p));
		}

		private int getResizingRow(Point p, int row) {
			// System.out.println("行号[" + row + "]"); //
			if (row == -1) {
				return -1;
			}

			int col = tmptable.columnAtPoint(p);
			if (col == -1)
				return -1;
			Rectangle r = tmptable.getCellRect(row, col, true); // 表格中对应行列的范围区域
			r.grow(0, -3); // 将网格的范围减少3个像素，即网线上下3个像素内，光标就会变
			if (r.contains(p)) // 如果对应的行列范围包含鼠标的范围
				return -1;

			int midPoint = r.y + r.height / 2;
			int rowIndex = (p.y < midPoint) ? row - 1 : row;

			return rowIndex;
		}

		public void mousePressed(MouseEvent e) {
			Point p = e.getPoint();

			resizingRow = getResizingRow(p);
			mouseYOffset = p.y - tmptable.getRowHeight(resizingRow);
		}

		private void swapCursor() {
			Cursor tmp = tmptable.getCursor();
			tmptable.setCursor(otherCursor);
			otherCursor = tmp;
		}

		public void mouseMoved(MouseEvent e) {
			int li_row = getResizingRow(e.getPoint());
			// System.out.println("取得行号[" + li_row + "]"); //
			if (li_row >= 0 != (tmptable.getCursor() == resizeCursor)) {
				swapCursor();
			}
		}

		public void mouseDragged(MouseEvent e) {
			int mouseY = e.getY();

			if (resizingRow >= 0) {
				int newHeight = mouseY - mouseYOffset;
				if (newHeight > 1) {
					if (e.isControlDown()) {
						int li_rowcount = tmptable.getRowCount();
						for (int i = 0; i < li_rowcount; i++) {
							tmptable.setRowHeight(i, newHeight); // 设置新的行高..
							table.setRowHeight(i, newHeight - 1); // 设置主表中的行高
						}
					} else {
						tmptable.setRowHeight(resizingRow, newHeight); // 设置新的行高..
						table.setRowHeight(resizingRow, newHeight - 1); // 设置主表中的行高
					}
				}
			}
		}
	}

	class ResizableTable extends JTable {
		protected MouseInputAdapter rowResizer, columnResizer = null;

		public ResizableTable(TableModel dm) {
			super(dm);
			new TableRowResizer(this);
		}

		public ResizableTable(Object[][] strings, Object[] strings2) {
			super(strings, strings2);//
			new TableRowResizer(this); //
		}

		public ResizableTable(DefaultTableModel tableModel, DefaultTableColumnModel rowHeaderColumnModel) {
			super(tableModel, rowHeaderColumnModel); //
			new TableRowResizer(this); //
		}

		// mouse press intended for resize shouldn't change row/col/cell
		// celection
		public void changeSelection(int row, int column, boolean toggle, boolean extend) {
			if (getCursor().getType() == Cursor.N_RESIZE_CURSOR)
				return;
			super.changeSelection(row, column, toggle, extend);
		}
	}

	class BillCellTableModelEditListener implements TableModelListener {
		public BillCellTableModelEditListener() {
		}

		public void tableChanged(TableModelEvent e) {
			if (e.getType() == TableModelEvent.UPDATE) { // 只监听修改事件..
				// int firstRow = e.getFirstRow();
				int lastRow = e.getLastRow();
				int mColIndex = e.getColumn();
				AttributiveCellTableModel tableModel = (AttributiveCellTableModel) e.getSource(); //
				if (table.isTriggerEditListenerEvent()) { // 如果需要触发编辑事件
					BillCellItemVO itemVO = (BillCellItemVO) tableModel.getValueAt(lastRow, mColIndex);
					if (itemVO != null) {
						if (itemVO.getEditformula() != null) {
							execEditFormula(itemVO.getEditformula(), lastRow, mColIndex); // 执行编辑公式.
						}
					}
				}
			}
		}

	}

	public boolean isIfSetRowHeight() {
		return ifSetRowHeight;
	}

	public void setIfSetRowHeight(boolean ifSetRowHeight) {
		this.ifSetRowHeight = ifSetRowHeight;
	}

	public JTableLockManager getJLM(MultiSpanCellTable table, JScrollPane scroll) {
		if (jtm == null) {
			jtm = new JTableLockManager(table, scroll);
		} else {
			if (jtm.getTable() == table && jtm.getScrollPane() == scroll) {
			} else {
				jtm = new JTableLockManager(table, scroll);
			}
		}
		return jtm;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public boolean setLockedCell(final int _row, final int _col) {
		if (_row <= 0 || _row >= table.getRowCount()) {
			MessageBox.show(this, "冻结的行有问题。");
			return false;
		}
		if (_col <= 0 || _col >= table.getColumnCount()) {
			MessageBox.show(this, "冻结的列有问题。");
			return false;
		}
		new Timer().schedule(new TimerTask() {
			public void run() {
				getJLM(table, scroll).setFixRowCol(_row - 1, _col - 1);
				scroll.updateUI();
			}
		}, 500);
		isLocked = true;
		return true;
	}

	public JScrollPane getScrollPanel() {
		return scroll;
	}
}
