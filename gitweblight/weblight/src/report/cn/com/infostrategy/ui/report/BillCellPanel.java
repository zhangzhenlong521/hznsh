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
 * ��Excelһ����һ�������,���Ա���һ����ά�������������� һ���������й���:�ϲ�/�ָ�;������;����ɫ������
 * 
 * @author xch
 * 
 */
public class BillCellPanel extends BillPanel implements ActionListener {

	private static final long serialVersionUID = -8764511718458388278L;

	// �ؼ�����
	public static String ITEMTYPE_LABEL = "LABEL"; // ���ı�
	public static String ITEMTYPE_TEXT = "TEXT"; // ���ı�
	public static String ITEMTYPE_NUMBERTEXT = "NUMBERTEXT"; // ���ֿ�..
	public static String ITEMTYPE_TEXTAREA = "TEXTAREA"; // �����ı���
	public static String ITEMTYPE_CHECKBOX = "CHECKBOX"; // ��ѡ��
	public static String ITEMTYPE_COMBOBOX = "COMBOBOX"; // ������
	public static String ITEMTYPE_DATE = "DATE"; // ����
	public static String ITEMTYPE_DATETIME = "DATETIME"; // ʱ��

	private BillCellVO billCellVO = null; //
	private BillCellItemVO[][] billCellItemVos = null;

	private String templetcode = null; // ģ�����
	private String templetname = null; // ģ������
	private String billNo = null; // ���ݱ���
	private String descr = null; // ��ע

	private AttributiveCellTableModel tablemodel = null; // ����ģ�Ͷ���..
	private CellAttribute cellAtt = null;

	private JScrollPane scroll = null; //
	private MultiSpanCellTable table = null; // ������,����ĵĶ���.

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
	private boolean isAllowShowPopMenu = true; //�Ƿ��������Ҽ��˵�

	private JPanel mainPanel = null; // �����

	private JTextField textfield_status = null; //

	private boolean bo_isshowline = true; //
	private boolean bo_isshowtoolbar = true; //
	private boolean bo_isshowheader = true; //�Ƿ���ʾ��ͷ����ͷ!!

	private JPopupMenu popMenu = null; //
	private JPopupMenu custPopMenu = null; //

	private JepFormulaParseAtUI formulaParse = null; //

	private Vector vc_HtmlHrefListrners = new Vector(); //����������¼�

	private boolean ifSetRowHeight = true;//�Ƿ������и� ��Ϊjtable�����иߵ�ʱ����ػ� ����������

	/**
	 * �����ȸ��´��� �����ǳ�ʼ��EXCEL�ļ����������
	 */
	private JButton btn_exportExcel = null; // ����Excel��Ť
	private JButton btn_inputExcel = null; // ����Excel��Ť
	private JButton btn_exportXML = null; // ����XML
	private JButton btn_importXML = null; // ����XML
	private HashVO[] excelHash = null;
	private HashVO[] cellHash = null;
	private JTextArea textArea = null;
	private BillDialog dialog = null;
	// private String defaultPath = "C:/";
	private String fileExistsError = "���ļ��Ѿ����ڣ��Ƿ񸲸ǣ�"; // �ļ����ڴ�����ʾ��Ϣ
	private String defaultSheetName = "SheetName"; // ȱʡ��sheet����
	private String successMessage = "����ɹ���\n�ļ�·��Ϊ��"; // �ɹ���ʾ��Ϣ
	private String billInitException = "BillCellItemVO��ʼ���쳣"; // �쳣��Ϣ��ʾ
	private String cellSpanError = "��Ԫ��ϲ�����ֵ����"; // ��Ԫ��ϲ�����ֵ������Ϣ
	private String lookAtErrorInformation = "���ں�̨�鿴������Ϣ��"; // ������Ϣ�鿴��ʾ
	private String messageTempletcodeNull = "ģ��Ϊ�գ���ѡ��һ��ģ�棡"; // ��ģ�������Ϣ
	private String fileNotExists = "ָ���ļ������ڣ�"; // �ļ������ڴ�����Ϣ��ʾ

	private HSSFRow row = null; // ������
	private HSSFCell cell = null; // ������

	private int currMouseOverRow = -1, currMouseOverCol = -1; //��ǰ�������ȥʱ���к�
	private JTableLockManager jtm = null;
	private boolean isLocked = false;

	/**
	 * Ĭ��
	 */
	public BillCellPanel() {
		this(null, null, null);
	}

	/**
	 * 
	 * @param _templetCode
	 *            ģ�����
	 */
	public BillCellPanel(String _templetCode) {
		this(_templetCode, null, null);
	}

	//ֱ�Ӹ���һ��HashVO����һ��CellPanel
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
				if (str_bgcolor != null && !str_bgcolor.trim().equals("")) { //�����Ϊ��!
					itemVOs[i][j].setBackground(str_bgcolor); //
				}
			}
		}
		cellVO.setCellItemVOs(itemVOs); //
		initialize(cellVO); //
	}

	/**
	 * ֱ�Ӹ���BillCEllVO����һ��ƽ̨��Excel���
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
	 *            ģ�����
	 */
	public BillCellPanel(String _templetCode, boolean _showline) {
		this(_templetCode, null, null, _showline);
	}

	/**
	 * 
	 * @param _templetCode
	 *            ģ�����
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
	 *            ģ�����
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
		this.setOpaque(false); //͸��!!!
		JPanel panel_root = new JPanel(); // �����..
		panel_root.setOpaque(false); //����͸����!
		panel_root.setLayout(new BorderLayout()); //

		if (bo_isshowtoolbar) {
			panel_root.add(getTitleBtnBar(), BorderLayout.NORTH); // ���밴ť��
		}else{
			panel_root.add(getTitleBtnBar(1), BorderLayout.NORTH); // ���밴ť��
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

		panel_root.add(mainPanel, BorderLayout.CENTER); // ���밴ť��..
		textfield_status = new JTextField(); //
		textfield_status.setEditable(false); //

		if (bo_isshowtoolbar) {
			panel_root.add(textfield_status, BorderLayout.SOUTH); // ����״̬��..
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
			initPopMenu(); // ��ʼ���Ҽ��˵�
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ��ʼ�����.
	 * 
	 * @param _rowcount
	 * @param _colcount
	 */
	private void initTable(int _rowcount, int _colcount) {
		mainPanel.removeAll(); //
		mainPanel.setLayout(new BorderLayout()); //
		mainPanel.setOpaque(false); //͸��!!

		tablemodel = new AttributiveCellTableModel(_rowcount, _colcount); // ����һ�����
		tablemodel.addTableModelListener(new BillCellTableModelEditListener()); // ����ֵ�仯�¼�
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
		table.setDefaultRenderer(Object.class, new AttributiveCellRenderer()); // Ĭ�ϻ�����
		table.setDefaultEditor(Object.class, new AttributiveCellEditor(table, this)); //

		//�����������¼�,���һ��,��������һ���ڲ���!!
		MouseAdapter tableMouseLn = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					onClickedTable(e.getSource(), e.getPoint(), e.isControlDown(), e.isShiftDown(), e.isAltDown()); //
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					showPopMenu(e.getSource(), e.getPoint()); //
				}
			}

			//����ڸ������ƹ���ʱ��
			public void mouseMoved(MouseEvent e) {
				Point point = e.getPoint(); //
				onMouseMoveFromTable(point); //
			}

			public void mouseReleased(MouseEvent e) {
				selectedInfo();//�����б�Ԫ��ʱ���·�״̬����ʾ��Ϣ[YangQing/2013-09-25]
			}
		};
		table.addMouseListener(tableMouseLn); //����!
		table.addMouseMotionListener(tableMouseLn); //�ƶ�����!

		scroll = new JScrollPane(table); //
		if (!bo_isshowline) { //��ɫ!! �������ʾ��,��
			scroll.getViewport().setBackground(Color.WHITE); //
		}

		//table.getTableHeader().setOpaque(false);
		JViewport jv2 = new JViewport();
		jv2.setOpaque(false); //һ��Ҫ��һ�£������ϱ����и�����
		jv2.setView(table.getTableHeader());
		scroll.setColumnHeader(jv2); //

		if (!bo_isshowheader) { //�������ʾ��ͷ,������html�װ���������һ��,�����͸����,������û�б߿��
			scroll.setOpaque(false); //͸��!!
			scroll.getViewport().setOpaque(false); //͸��!!
			scroll.setBorder(BorderFactory.createEmptyBorder()); //�հױ߿�!!
			table.setBorder(BorderFactory.createLineBorder(table.getGridColor(), 1)); //createMatteBorder(1, 1, 1, 1, table.getGridColor())); //���߿�!!!
		} else { //����б�ͷ
			table.setBorder(BorderFactory.createEmptyBorder()); //
		}

		JScrollBar newScrollBar = new JScrollBar(); //
		newScrollBar.setUnitIncrement(20); //
		scroll.setVerticalScrollBar(newScrollBar); //һ��Ҫ����һ��,�����������¹����������!!

		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); //
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); //

		//System.out.println("��Сֵ:" + scroll.getVerticalScrollBar().getMinimum() + ",���ֵ:" + scroll.getVerticalScrollBar().getMaximum() );  //
		//�����ָ����ʾ��ͷ!!!
		if (bo_isshowheader && bo_isshowline) {
			DefaultTableColumnModel rowHeaderColumnModel = new DefaultTableColumnModel();
			rowHeaderColumnModel.addColumn(getRowNumberColumn()); // �����к���

			rowHeaderTable = new ResizableTable(tablemodel, rowHeaderColumnModel); // �����µı�..
			//rowHeaderTable.setOpaque(false);  //͸��!!!
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
			jv.setOpaque(false); //͸��!!
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

	//�����Ƿ�͸��
	public void setCrystal(boolean _isCrystal) {
		if (_isCrystal) {
			this.setOpaque(false); //
			scroll.setOpaque(false); //͸��!!
			scroll.getViewport().setOpaque(false); //͸��!!
			//scroll.setBorder(BorderFactory.createEmptyBorder()); //�հױ߿�!!
			//table.setBorder(BorderFactory.createLineBorder(table.getGridColor(), 1)); //createMatteBorder(1, 1, 1, 1, table.getGridColor())); //���߿�!!!
		} else {
			scroll.setOpaque(true); //��͸��!!
			scroll.getViewport().setOpaque(true); //��͸��!!
			scroll.setBorder(BorderFactory.createEmptyBorder()); //�հױ߿�!!
			table.setBorder(BorderFactory.createEmptyBorder()); //
		}
	}

	//������ڱ�����ƶ�ʱ!!!
	private void onMouseMoveFromTable(Point _point) {
		table.setMouseMovingRow(-1); //���
		table.setMouseMovingCol(-1); //���
		int li_row = table.rowAtPoint(_point); //�õ����������
		int li_col = table.columnAtPoint(_point); //�õ����������
		if (li_row >= 0 && li_col >= 0) { ////
			table.setMouseMovingRow(li_row); //����
			table.setMouseMovingCol(li_col); //����

			boolean isHand = false; //�Ƿ�Ҫ����
			currMouseOverRow = li_row; //��ǰ������к�!!
			currMouseOverCol = li_col; //��ǰ�����к�!!
			try {
				BillCellItemVO itemVO = getBillCellItemVOAt(li_row, li_col); ////
				if (itemVO != null && "Y".equals(itemVO.getIshtmlhref()) && !itemVO.getCellvalue().trim().equals("")) { //�����ָ����Html��ʾ
					isHand = true; //!!!
				}
			} catch (Exception ex) {
				System.err.println(ex.getMessage()); //����쳣!!!
			}

			if (isHand) { //���������,�򽫹�����������!!!
				table.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
			} else {
				table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
			}
		}
		table.revalidate();
		table.repaint();
	}

	private TableColumn getRowNumberColumn() {
		TableCellRenderer render = new RowNumberRender(); // �����л�����..
		ListCellEditor_RowNumber editor = new ListCellEditor_RowNumber();
		TableColumn rowNumberColumn = new TableColumn(0, 37, render, editor); // ������,��Ӧ��һ������

		if (ClientEnvironment.getInstance().isEngligh()) {
			rowNumberColumn.setHeaderValue("No."); //
		} else {
			rowNumberColumn.setHeaderValue("���"); //
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

			btn_new = new WLTButton("�½�"); //
			btn_save = new WLTButton("����"); //
			btn_open = new WLTButton("��"); //
			btn_exportsql = new WLTButton("����SQL"); //

			// ����¹��� ������
			btn_exportExcel = new WLTButton("����Excel");
			btn_inputExcel = new WLTButton("����Excel");
			btn_exportXML = new WLTButton("����XML");
			btn_importXML = new WLTButton("����XML");

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
			btn_combine.setToolTipText("�ϲ���Ԫ��"); //
			btn_split.setToolTipText("�ָԪ��"); //
			btn_combine.addActionListener(this);
			btn_split.addActionListener(this);

			panel_2.add(btn_combine);
			panel_2.add(btn_split);

			btn_forecolor = new WLTButton("ǰ��"); //
			btn_backcolor = new WLTButton("����"); //
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
			combox_celltype.addItem(ITEMTYPE_TEXT); // �ı���
			combox_celltype.addItem(ITEMTYPE_NUMBERTEXT); // ���ֿ�
			combox_celltype.addItem(ITEMTYPE_TEXTAREA); // �����ı���
			combox_celltype.addItem(ITEMTYPE_CHECKBOX); // ��ѡ��
			combox_celltype.addItem(ITEMTYPE_COMBOBOX); // ������
			combox_celltype.addItem(ITEMTYPE_DATE); // ����.
			combox_celltype.addItem(ITEMTYPE_DATETIME); // ʱ��.
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
			checkbox_showCellKey = new JCheckBox("��ʾCellKey", false); //
			checkbox_showCellKey.setToolTipText("�Ƿ���ʾCellKey"); //
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
	 * zzl 2020-8-31 ����������ʾ��ť������չ
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
			btn_save = new WLTButton("����"); //
			btn_exportExcel = new WLTButton("����Excel");
			btn_inputExcel = new WLTButton("����Excel");
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

			btn_new = new WLTButton("�½�"); //
			btn_save = new WLTButton("����"); //
			btn_open = new WLTButton("��"); //
			btn_exportsql = new WLTButton("����SQL"); //

			// ����¹��� ������
			btn_exportExcel = new WLTButton("����Excel");
			btn_inputExcel = new WLTButton("����Excel");
			btn_exportXML = new WLTButton("����XML");
			btn_importXML = new WLTButton("����XML");

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
			btn_combine.setToolTipText("�ϲ���Ԫ��"); //
			btn_split.setToolTipText("�ָԪ��"); //
			btn_combine.addActionListener(this);
			btn_split.addActionListener(this);

			panel_2.add(btn_combine);
			panel_2.add(btn_split);

			btn_forecolor = new WLTButton("ǰ��"); //
			btn_backcolor = new WLTButton("����"); //
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
			combox_celltype.addItem(ITEMTYPE_TEXT); // �ı���
			combox_celltype.addItem(ITEMTYPE_NUMBERTEXT); // ���ֿ�
			combox_celltype.addItem(ITEMTYPE_TEXTAREA); // �����ı���
			combox_celltype.addItem(ITEMTYPE_CHECKBOX); // ��ѡ��
			combox_celltype.addItem(ITEMTYPE_COMBOBOX); // ������
			combox_celltype.addItem(ITEMTYPE_DATE); // ����.
			combox_celltype.addItem(ITEMTYPE_DATETIME); // ʱ��.
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
			checkbox_showCellKey = new JCheckBox("��ʾCellKey", false); //
			checkbox_showCellKey.setToolTipText("�Ƿ���ʾCellKey"); //
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
	//	 * ȡ����ɫ������
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
				exportSQL(); // ����SQL
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
				autoSetCellKey(); // �Զ�����CellKey
			} else if (e.getSource() == this.popMenuItem_setCellEditFormula) {
				setCellEditFormula(); //
			} else if (e.getSource() == this.popMenuItem_setCellIsHtmlHref) { //����ǳ�����!
				setCellIsHtmlHref(); //
			} else if (e.getSource() == popMenuItem_setCellValidateFormula) { //У�鹫ʽ
				setCellValidateFormula(); //����У�鹫ʽ
			} else if (e.getSource() == this.popMenuItem_setCellDesc) {
				setCellDesc(); //
			} else if (e.getSource() == popMenuItem_showCellProp) { //��ʾ������������
				showCellAllProp(); //��ʾ������������
			} else if (e.getSource() == btn_exportExcel) {
				// ִ��EXCEL�ļ��ĵ���----
				exportExcel();
			} else if (e.getSource() == btn_inputExcel) {
				// ִ��EXCEL�ļ��ĵ���----
				inputExcel();
			} else if (e.getSource() == btn_exportXML) {
				// ִ��XML�ĵ���
				exportXML();
			} else if (e.getSource() == btn_importXML) {
				// ִ��XML�ĵ���
				importXML();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			MessageBox.showException(this, ex); //
		}
	}

	private void importXML() {
		textArea = new JTextArea();
		dialog = new BillDialog(this, "����XML", 1000, 700);
		dialog.setLayout(new BorderLayout());
		textArea.setBackground(Color.WHITE);
		textArea.setForeground(Color.BLUE);
		textArea.setFont(new Font("����", Font.PLAIN, 12));
		textArea.select(0, 0);

		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		WLTButton confirm = new WLTButton("ȷ��");
		WLTButton cancel = new WLTButton("ȡ��");
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
				MessageBox.show("����ɹ���");
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
	 * ����XML��ʽ�ļ� ������ 2008-11-20
	 */
	private void exportXML() {
		if (templetcode == null) {
			MessageBox.show(this, "��ѡ��һ��EXCELģ��!");
			return;
		}
		BillDialog excelToXML = new BillDialog(this, "����XML", 1000, 700);
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
		show.setFont(new Font("����", Font.PLAIN, 12));
		show.select(0, 0);
		excelToXML.getContentPane().add(new JScrollPane(show));
		excelToXML.setVisible(true);
	}

	/**
	 * �ϲ���Ԫ���ַ���ת��Ϊһά����
	 * 
	 * @param span
	 *            String
	 * @return ����[2]
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
	 * ������ ���ҳ���ļ�
	 */
	public void exportHtml() {
		exportHtml("�������ݵ���"); //
	}

	/**
	 * ����Html
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
		exportExcel("�������ݵ���", "defaultPanel"); //
	}

	public void exportExcel(String _filename) {
		exportExcel(_filename, "defaultPanel"); //
	}

	/**
	 * ����Exel,���ϲ�Ч����!!
	 */
	public void exportExcel(String _filename, String type) {
		try {
			byte[] bytes = null;
			if (type != null && type.trim().equals("StyleReportPanel")) {//���Ϊ��񱨱�
				bytes = initStyleReportExcelData();
			} else if (type != null && type.trim().equals("BillReportPanel")) { //���Ϊ��ά����
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
			if (type != null && type.trim().equals("StyleReportPanel")) {//���Ϊ��񱨱�
				bytes = initStyleReportExcelData();
			} else if (type != null && type.trim().equals("BillReportPanel")) { //���Ϊ��ά����
				bytes = initBillReportExcelData();
			} else {
				bytes = initDefaultExcelData();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return bytes;
	}

	//��Ҫ����ά������ʽ   20130911  Ԭ�����޸�
	private byte[] initBillReportExcelData() throws Exception {
		// �����ڱ�����ʼ��
		HSSFWorkbook wb = null;
		HSSFSheet sheet = null;
		int[] cellSpan = null;
		HSSFCellStyle style = null;
		Color color = null;
		HSSFFont font = null;
		String[] colorStrings = null;
		wb = new HSSFWorkbook(); // ��EXCEL���
		sheet = wb.createSheet(); // ����һ��
		wb.setSheetName(0, defaultSheetName);
		font = wb.createFont();
		style = wb.createCellStyle();
		// �����������ȡ������
		for (int i = 0; i < table.getRowCount(); i++) { // ֱ�Ӵ�ҳ����ȡ
			row = sheet.createRow(i);
			for (int j = 0; j < table.getColumnCount(); j++) {
				cell = row.createCell((short) j); // ������Ԫ��
				cell.setCellValue(getValueAt(i, j)); // �����и�ֵ
				// ���ص�Ԫ��ĺϲ�����
				cellSpan = getSpan(i, j);
				// ���غϲ���Ԫ�����
				if (cellSpan == null || cellSpan.length == 0 || cellSpan.length != 2) {
					System.out.println(cellSpanError);
					return null;
				}
				if (cellSpan[0] > 1 || cellSpan[1] > 1) {
					initExcelSpan(sheet, i, j, i + cellSpan[0] - 1, j + cellSpan[1] - 1);
				}
				/**
				 * ������EXCEL�ļ�����ʾ���ĳ�ʼ���� ������ɫ ������ʽ ������ɫ �߿���ɫ �ȵ�....
				 * ----------------------------------------------------------------------------------------------------------
				 */
				if (getCellItemVOAt(i, j) != null) {
					if ((i == 0) && (getCellItemVOAt(i, j).getColwidth() != null)) { //����ǵ�һ��  ��Ϊ�ֶ���  Ԭ���� 20130911 ������ʽ   
						HSSFCellStyle cstyle1 = wb.createCellStyle();
						//						 row.setHeight((short)(Short.valueOf(getCellItemVOAt(i, j).getRowheight()).shortValue() * 12));   //���ø߶�
						cstyle1.setAlignment((short) getCellItemVOAt(i, j).getHalign());
						cstyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
						cstyle1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); //���ñ�����ɫ
						cstyle1.setFillForegroundColor(HSSFColor.ROSE.index);//���ñ�����ɫ
						// �߿�����
						cstyle1.setBorderTop((short) 1);
						cstyle1.setBorderRight((short) 1);
						cstyle1.setBorderBottom((short) 1);
						cstyle1.setBorderLeft((short) 1);
						cstyle1.setTopBorderColor(HSSFColor.BLACK.index);
						cstyle1.setRightBorderColor(HSSFColor.BLACK.index);
						cstyle1.setBottomBorderColor(HSSFColor.BLACK.index);
						cstyle1.setLeftBorderColor(HSSFColor.BLACK.index);
						cell.setCellStyle(cstyle1);
					} else if ((j == 0) && (getCellItemVOAt(i, j).getColwidth() != null)) { //����ǵ�һ��  ��Ϊ�ֶ���  Ԭ���� 20130911 ������ʽ
						HSSFCellStyle cstyle1 = wb.createCellStyle();
						//						 row.setHeight((short)(Short.valueOf(getCellItemVOAt(i, j).getRowheight()).shortValue() * 12));   //���ø߶�
						cstyle1.setAlignment((short) getCellItemVOAt(i, j).getHalign());
						cstyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
						cstyle1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); //���ñ�����ɫ
						cstyle1.setFillForegroundColor(HSSFColor.LIGHT_TURQUOISE.index);//���ñ�����ɫ
						// �߿�����
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
						// �߿�����
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
					if (((i == 0) && (j == 0)) && (getCellItemVOAt(i, j).getColwidth() != null)) { //��������Ͻǣ���Ϊ��ɫ��ʾ
						HSSFCellStyle cstyle2 = wb.createCellStyle();
						;
						sheet.setColumnWidth(j, Short.valueOf(getCellItemVOAt(i, j).getColwidth()).shortValue() * 47);
						row.setHeight((short) (Short.valueOf(getCellItemVOAt(i, j).getRowheight()).shortValue() * 26));
						cstyle2.setWrapText(true);
						HSSFFont cfont = wb.createFont();
						cfont.setColor(HSSFColor.BLACK.index);
						cfont.setFontHeightInPoints((short) 10);// ���������С
						cstyle2.setFont(cfont);
						cstyle2.setBorderTop((short) 1);
						cstyle2.setBorderRight((short) 1);
						cstyle2.setBorderLeft((short) 1);
						cstyle2.setTopBorderColor(HSSFColor.BLACK.index);
						cstyle2.setRightBorderColor(HSSFColor.BLACK.index);
						cstyle2.setLeftBorderColor(HSSFColor.BLACK.index);
						cstyle2.setFillForegroundColor(HSSFColor.RED.index);
						cstyle2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); //���ñ�����ɫ
						cstyle2.setFillForegroundColor(HSSFColor.YELLOW.index);//���ñ�����ɫ
						cell.setCellStyle(cstyle2);
					}
				}
				/**
				 * ���ϳ�ʼ����ϡ�
				 */
			}
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream(); //
		wb.write(bout);
		byte[] bytes = bout.toByteArray(); //
		bout.close();
		return bytes; //
	}

	//���˷�񱨱����ά������ı���ĵ�����ʽ    ��������ǰ�ɵ�
	private byte[] initDefaultExcelData() throws Exception {
		// �����ڱ�����ʼ��
		HSSFWorkbook wb = null;
		HSSFSheet sheet = null;
		int[] cellSpan = null;
		HSSFCellStyle style = null;
		Color color = null;
		HSSFFont font = null;
		String[] colorStrings = null;
		wb = new HSSFWorkbook(); // ��EXCEL���
		sheet = wb.createSheet(); // ����һ��
		wb.setSheetName(0, defaultSheetName);
		font = wb.createFont();
		style = wb.createCellStyle();
		// �����������ȡ������
		for (int i = 0; i < table.getRowCount(); i++) { // ֱ�Ӵ�ҳ����ȡ
			row = sheet.createRow(i);
			for (int j = 0; j < table.getColumnCount(); j++) {
				cell = row.createCell((short) j); // ������Ԫ��
				cell.setCellValue(getValueAt(i, j)); // �����и�ֵ
				// ���ص�Ԫ��ĺϲ�����
				cellSpan = getSpan(i, j);
				// ���غϲ���Ԫ�����
				if (cellSpan == null || cellSpan.length == 0 || cellSpan.length != 2) {
					System.out.println(cellSpanError);
					return null;
				}
				if (cellSpan[0] > 1 || cellSpan[1] > 1) {
					initExcelSpan(sheet, i, j, i + cellSpan[0] - 1, j + cellSpan[1] - 1);
				}
				/**
				 * ������EXCEL�ļ�����ʾ���ĳ�ʼ���� ������ɫ ������ʽ ������ɫ �߿���ɫ �ȵ�....
				 * ----------------------------------------------------------------------------------------------------------
				 */
				if (getCellItemVOAt(i, j) != null) {
					if (i == 0 && getCellItemVOAt(i, j).getColwidth() != null) {
						sheet.setColumnWidth(j, Short.valueOf(getCellItemVOAt(i, j).getColwidth()) * 37);
					}
					if (j == 0 && getCellItemVOAt(i, j).getRowheight() != null) {
						row.setHeight((short) (Short.valueOf(getCellItemVOAt(i, j).getRowheight()) * 16));
					}
					// ���뷽ʽ
					style.setAlignment((short) getCellItemVOAt(i, j).getHalign());
					style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
					style.setWrapText(true);
					font.setFontName("����");
					font.setColor(HSSFColor.BLACK.index);
					if (getCellItemVOAt(i, j).getFontsize() == null) {
						font.setFontHeightInPoints((short) 12);
					} else {
						font.setFontHeightInPoints(Short.valueOf(getCellItemVOAt(i, j).getFontsize()));
					}
					// �߿�����
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
				 * ���ϳ�ʼ����ϡ�
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
	 * ���ӱ��x/y��ת���߼������/2012-04-12��
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
					if (_isResetHeight) { //������ά����ʱ�������������иߣ���xch/2012-07-19��
						newbillitemvos[i][j].setRowheight("22");//ͳһ����һ�£��������Ԫ�񶼻�Ӹ�
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
	 * ����Html,��ǰ����д��,��������!����д�����㷨!��xch/2012-08-22��
	 * @return
	 * @throws Exception
	 */
	public String createHtmlString() throws Exception {
		BillCellVO cellVO = getBillCellVO(); //
		BillCellItemVO[][] itemVOs = cellVO.getCellItemVOs(); //
		StringBuffer returnHtml = new StringBuffer();
		returnHtml.append("<html><head><title>����Html</title>\r\n");
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
				str_data[i][j] = str_value; //��ֵ
				str_bgcolor[i][j] = itemVOs[i][j].getBackground(); //
				str_fontcolor[i][j] = itemVOs[i][j].getForeground(); //
				str_height[i][j] = itemVOs[i][j].getRowheight(); //�и�!
				str_width[i][j] = itemVOs[i][j].getColwidth(); //
				li_align[i][j] = itemVOs[i][j].getHalign(); //
				li_valign[i][j] = itemVOs[i][j].getValign(); //

				//��¼�ϲ����!
				String str_span = itemVOs[i][j].getSpan(); //�ϲ�!
				if (str_span != null && !str_span.trim().equals("")) { //�����Ϊ��
					String[] str_xy = tbUtil.split(str_span.trim(), ","); //�ָ�һ��!
					int li_rowspan = Integer.parseInt(str_xy[0]); //
					int li_colspan = Integer.parseInt(str_xy[1]); //
					if (li_rowspan >= 1 && li_colspan >= 1) { //�����������Ǵ���0
						span_xy_map.put("" + i + "," + j, str_span.trim()); //������,key���ĸ�λ��,value�Ǻϲ����,���htmƴrowSpan,colspan�Ϳ����value
						for (int r = 0; r < li_rowspan; r++) { //�����кϲ�������
							for (int c = 0; c < li_colspan; c++) { //�����кϲ�������
								if (r != 0 || c != 0) {
									al_null_xy.add(new int[] { i + r, j + c }); //�����б�,�������Щλ�õĸ���ͳͳ�ÿ�!
								}
							}
						}
					}
				}

			}
		}

		//���������������,�ڶ��α�������,�����б��ϲ��ĸ���ͳͳ��Ϊnull
		for (int i = 0; i < al_null_xy.size(); i++) {
			int[] li_null = (int[]) al_null_xy.get(i); //
			str_data[li_null[0]][li_null[1]] = null; //
		}

		for (int i = 0; i < str_data.length; i++) {
			returnHtml.append("<tr>\r\n");
			for (int j = 0; j < str_data[i].length; j++) { //
				if (str_data[i][j] == null) { //���Ϊ��������!����ʵ���˺ϲ�Ч��
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
				str_value = tbUtil.replaceAll(str_value, "��", "�� <br>"); //
				str_value = tbUtil.replaceAll(str_value, "�K", "�K<br>"); //
				str_value = tbUtil.replaceAll(str_value, "��", "�� <br>"); //
				if (str_fontcolor[i][j] != null && !str_fontcolor[i][j].trim().equals("")) {
					str_value = "<span style=\"color:" + tbUtil.getHtmlColor(str_fontcolor[i][j]) + "\">" + str_value + "</span>"; //
				}
				if (span_xy_map.containsKey("" + i + "," + j)) { //�������
					String str_span = (String) span_xy_map.get("" + i + "," + j); //
					String[] str_span_xy = tbUtil.split(str_span, ","); //�ָ�
					int li_rowSpan = Integer.parseInt(str_span_xy[0]); //
					int li_colSpan = Integer.parseInt(str_span_xy[1]); //
					String str_align = getAlign(li_align[i][j], 1);
					String str_valign = getAlign(li_valign[i][j], 2);
					returnHtml.append("<td height=\"" + str_rowheight + "\" class=\"style_1\" align=\"" + str_align + "\"  valign=\"" + str_valign + "\" "); //
					if (str_bgcolor[i][j] != null) {
						returnHtml.append(" bgcolor=\"" + tbUtil.getHtmlColor(str_bgcolor[i][j]) + "\""); //
					}
					if (li_rowSpan > 1) { //�������1
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

		// ����EXCEL�ļ�������
		try {
			output = new FileOutputStream(fileName);
			wb = new HSSFWorkbook(); // ��EXCEL���
			sheet = wb.createSheet(defaultSheetName); // ����һ��

			// �����������ȡ������
			for (int i = 0; i < this.billCellVO.getRowlength(); i++) {
				row = sheet.createRow(i);
				// �����и�
				row.setHeight((short) (Short.valueOf(billCellItemVos[i][0].getRowheight()) * 20));
				for (int j = 0; j < this.billCellVO.getCollength(); j++) {

					cell = row.createCell(j); // ������Ԫ��
					//cell.setEncoding((short) 1); // ������ı�������
					if (i == 0) {
						// �����п�
						sheet.setColumnWidth(j, (Short.valueOf(billCellItemVos[0][j].getColwidth()) * 50));
					}
					if (billCellItemVos[i][j].getCellvalue() != null) {
						cell.setCellValue(billCellItemVos[i][j].getCellvalue().toString()); // �����и�ֵ
					} else {
						cell.setCellValue("");
					}

					// ���ص�Ԫ��ĺϲ�����
					cellSpan = toSpan(billCellItemVos[i][j].getSpan());
					// ���غϲ���Ԫ�����
					if (cellSpan == null || cellSpan.length == 0 || cellSpan.length != 2) {

						return;
					}
					if (cellSpan[0] > 1 || cellSpan[1] > 1) {
						initExcelSpan(sheet, i, j, i + cellSpan[0] - 1, j + cellSpan[1] - 1);
					}
					/**
					 * ������EXCEL�ļ�����ʾ���ĳ�ʼ���� ������ɫ ������ʽ ������ɫ �߿���ɫ �ȵ�....
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

							// ��ɫ������ʱ������---------------------���������Ĭ�ϵı�����ɫ
						}

						// ���뷽ʽ
						style.setAlignment((short) billCellItemVos[i][j].getHalign());
						// style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
						style.setVerticalAlignment((short) billCellItemVos[i][j].getValign());

						// ������������
						if (billCellItemVos[i][j].getFonttype() == null) {
							font.setFontName("����");
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

						// �߿�����
						style.setBorderTop((short) 1);
						style.setBorderRight((short) 1);
						style.setBorderBottom((short) 1);
						style.setBorderLeft((short) 1);
						style.setTopBorderColor(HSSFColor.YELLOW.index);
						style.setRightBorderColor(HSSFColor.YELLOW.index);
						style.setBottomBorderColor(HSSFColor.YELLOW.index);
						style.setLeftBorderColor(HSSFColor.YELLOW.index);

						// ���ñ�����ɫ

						// ��ʾ���Ϊ�����ʾ��ʽ
						// style.setFillPattern(HSSFCellStyle.BIG_SPOTS);

						// ����ǰ����ɫ
						// style.setFillForegroundColor(HSSFColor.RED.index);

						style.setFont(font);

						// style.setFillPattern((short)3);
						// style.setTopBorderColor(HSSFColor.YELLOW.index);

						// ��Ԫ��������
						cell.setCellStyle(style);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						//cell.setEncoding(HSSFCell.ENCODING_UTF_16);
					}

					/**
					 * ���ϳ�ʼ����ϡ�
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
	 * ������ ����Excel�ļ� �����ļ����� �������˺���������������Ĺ��ܣ��ļ����Ƶ����롢�ļ�·��������ȵ�
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
	 * ������������Ҫ�Ƕ�ȡExcel�������ݣ�������Ӧ��������ӵ�JTable��ȥ�� �������漰����JTable�ĳ�ʼ���Լ���ʾ�������á�
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
			input = new FileInputStream(fileName); // �����ļ������
			fileSystem = new POIFSFileSystem(input); // ���ļ�
			HSSFWorkbook wb = new HSSFWorkbook(fileSystem); // �����
			HSSFSheet sheet = wb.getSheetAt(0); // ȡ�õ�һ��ҳǩ
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
						 * �����и�..............................................
						 */
						table.setRowHeight(i, row.getHeight() / 15);
						// ......................................................
						/**
						 * ���ñ�ͷ���и�.........................................
						 * δ�������-------------JTable���и���������-------------
						 */
						JTableHeader tableHeader = table.getTableHeader();
						subTable = tableHeader.getTable();
						subTable.setRowHeight(i, row.getHeight() / 16);
						tableHeader.setTable(subTable);
						table.setTableHeader(tableHeader);
						// .........................................����ʧ��
					}
					table.setValueAtIgnoreEditEvent(billCellItem, i, j);
					table.revalidate();
					table.repaint();
				}

			}

			// �ϲ���Ԫ�����£�------------------------------------���⣺����ȥ�ж��Ƿ��Ǻϲ��ĵ�Ԫ��.
			// ��ν���������ͨ����������ȥ�жϵ�Ԫ���Ƿ�Ϊ�ϲ���Ԫ��

			/**
			 * ������ ��Ԫ��ϲ��ɹ�����Ԫ��ϲ�������
			 */
			int[] tableRows = null;
			int[] tableColumns = null;

			for (int j = 0; j < sheet.getNumMergedRegions(); j++) {
				region = sheet.getMergedRegionAt(j);
				if (region != null) {
					// �д����������
					// ����ڵ�Ԫ�غϲ������ʲô��
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
	 * ������������Ҫ�ǳ�ʼ��EXCEL�ļ������� �Լ���EXCEL�ļ��ķ���ʼ��   ��Ҫ�Ƿ�񱨱���ʽ   20130911  Ԭ�����޸ģ�
	 * ����Ҫ������Ԭ����13��12�·��޸ĵ����ݸ��µ���������Ҫ�Ż�������/2014-05-05��
	 */
	private byte[] initStyleReportExcelData() throws Exception {
		// �����ڱ�����ʼ��
		HSSFWorkbook wb = null;
		HSSFSheet sheet = null;
		int[] cellSpan = null;
		HSSFCellStyle style = null;
		Color color = null;
		HSSFFont font = null;
		String[] colorStrings = null;
		wb = new HSSFWorkbook(); // ��EXCEL���
		sheet = wb.createSheet(); // ����һ��
		wb.setSheetName(0, defaultSheetName);
		HashMap<Short, HSSFFont> hm_font = new HashMap<Short, HSSFFont>();//�������Ĺ�ϣ����Ҫ����Ϊ�����и����������ʽ keyΪ������ɫ��valueΪ�����ʽ�����/2014-05-05��
		// �����������ȡ������
		for (int i = 0; i < table.getRowCount(); i++) { // ֱ�Ӵ�ҳ����ȡ
			row = sheet.createRow(i);
			row.setHeight((short) 400);
			for (int j = 0; j < table.getColumnCount(); j++) {
				cell = row.createCell((short) j); // ������Ԫ��
				cell.setCellValue(getValueAt(i, j)); // �����и�ֵ
				// ���ص�Ԫ��ĺϲ�����
				cellSpan = getSpan(i, j);
				// ���غϲ���Ԫ�����
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
						fonthsfcolor = wb.getCustomPalette().findSimilarColor((byte) frgb[0], (byte) frgb[1], (byte) frgb[2]);//��ȡ����ȽϽӽ�����ɫ
					}
					if (hm_font.size() == 0) {//���ܹ������Ƚ϶�����壬����excel�лᱨ�� ���Բ�ȡ���淽��
						font = wb.createFont();//���ܴ����Ƚ϶�����壬�����ʺ�������
						font.setFontName("����");
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
							indexNow = (short) 32767;//�����ɫû�У���Ĭ��Ϊ32767
						} else {
							indexNow = fonthsfcolor.getIndex();
						}
						if (hm_font.keySet().contains(indexNow)) {
							style.setFont(hm_font.get(indexNow));
						} else {
							font = wb.createFont();//���ܴ����Ƚ϶�����壬�����ʺ�������
							font.setFontName("����");
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
					// �߿�����
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
						style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);//���ñ�����ɫ
						style.setFillForegroundColor(hsfcolor.getIndex());//���ñ�����ɫ
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
	 * ������ �ϲ���Ԫ����
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
	 * �쳤�� ��� ������ �޸�
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
	 * �õ�ĳһ�������ϵ�����!!
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
	 * ͨ���趨��keyֵ���õ�BillCellItemVO
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
	 * �쳤�� ���
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
				MessageBox.show(this, "��ǰģ�����Ϊ��,���ܵ���!����ѡ��һ��ģ��!"); //
				return; //
			}

			JLabel label_1 = new JLabel("���ݿ�����:", JLabel.RIGHT); //
			label_1.setBounds(5, 5, 80, 20); //

			JRadioButton radio_sqlserver = new JRadioButton("SQLServer", true); //
			JRadioButton radio_oracle = new JRadioButton("Orcale"); //
			ButtonGroup group = new ButtonGroup(); //
			group.add(radio_sqlserver); //
			group.add(radio_oracle); //

			radio_sqlserver.setBounds(85, 5, 100, 20); // //
			radio_oracle.setBounds(185, 5, 80, 20); // //

			JLabel label_2 = new JLabel("SQL��ʾ���:"); //
			label_2.setBounds(265, 5, 80, 20); //

			JComboBox comBox_sqltype = new JComboBox(); //
			comBox_sqltype.addItem("����"); //
			comBox_sqltype.addItem("����"); //
			comBox_sqltype.setBounds(345, 5, 80, 20); //

			JPanel panel = new JPanel(); //
			panel.setLayout(null); //
			panel.add(label_1); //
			panel.add(radio_sqlserver); //
			panel.add(radio_oracle); //
			panel.add(label_2); //
			panel.add(comBox_sqltype); //
			panel.setPreferredSize(new Dimension(440, 30)); //

			if (JOptionPane.showConfirmDialog(this, panel, "��������", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { //
				return;
			}

			String str_dbtype = ""; //
			if (radio_sqlserver.isSelected()) {
				str_dbtype = WLTConstants.SQLSERVER;
			} else if (radio_oracle.isSelected()) {
				str_dbtype = WLTConstants.ORACLE;
			}
			ReportServiceIfc reportService = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class);
			String sb_sqls = reportService.getExportCellTempletSQL(this.templetcode, str_dbtype, (comBox_sqltype.getSelectedItem().toString().equals("����") ? false : true)); //
			BillDialog dialog = new BillDialog(this, "����SQL", 1000, 700);
			JTextArea textArea = new JTextArea(sb_sqls.toString()); //
			textArea.setFont(new Font("����", Font.PLAIN, 12));
			textArea.select(0, 0); //
			dialog.getContentPane().add(new JScrollPane(textArea)); //
			dialog.setVisible(true); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex);
		}
	}

	/**
	 * �½�һ��ģ��,��ǰ����д��ע�����/2012-04-12��
	 * 
	 * @throws Exception
	 */
	private void onNew() throws Exception {
		CellTMO celltmo = new CellTMO();
		BillCardPanel cardPanel = new BillCardPanel(celltmo); //����һ����Ƭ���
		cardPanel.insertRow(); //��Ƭ����һ��!
		cardPanel.setEditableByInsertInit(); //���ÿ�Ƭ�༭״̬Ϊ����ʱ������
		BillCardDialog dialog = new BillCardDialog(this, "����ģ��", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //������Ƭ������
		dialog.setVisible(true); //��ʾ��Ƭ����
		if (dialog.getCloseType() == 1) { //�����ǵ��ȷ������!����Ƭ�е����ݸ����б�!
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
	 * ����..
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
		getBillCellVOFromUI(); // ��ҳ����ȡ�������������
		FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkMetaDataServiceIfc.class);
		service.saveBillCellVO(null, this.billCellVO); //
		long ll_2 = System.currentTimeMillis(); //
		MessageBox.show(this, "�������ݳɹ�,����ʱ[" + (ll_2 - ll_1) + "]����!");
	}

	/**
	 * ��ҳ����ȡ�����������������
	 */
	private void getBillCellVOFromUI() {
		int li_rows = table.getRowCount(); //
		int li_cols = table.getColumnCount(); //
		this.billCellVO.setRowlength(li_rows); // �ܹ��м���..
		this.billCellVO.setCollength(li_cols); // �ܹ��м���..

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
	 * �õ�ҳ���ϵ�ǰ��ʵ������..
	 * 
	 * @return
	 */
	public BillCellVO getBillCellVO() {
		getBillCellVOFromUI();
		return this.billCellVO;
	}

	/**
	 * ��
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
			textfield_status.setText("ģ�����:[" + code + "],ģ������:[" + name + "]"); //
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
		popMenuItem_insertRow = new JMenuItem("������"); //
		popMenuItem_insertCol = new JMenuItem("������"); //

		popMenuItem_delRow = new JMenuItem("ɾ����"); //
		popMenuItem_delCol = new JMenuItem("ɾ����"); //
		popMenuItem_cleardata = new JMenuItem("�������"); //
		popMenuItem_loc = new JMenuItem("���ᴰ��"); //
		popMenuItem_unloc = new JMenuItem("ȡ�����ᴰ��");
		popMenuItem_setTooltipText = new JMenuItem("������ʾ����"); //
		popMenuItem_setCellEditable = new JMenuItem("���ÿɱ༭"); //
		popMenuItem_setCellKey = new JMenuItem("����CellKey"); //
		item_AutoSetCellKey = new JMenuItem("�Զ�����CellKey"); // �Զ�����CellKey.
		popMenuItem_setCellEditFormula = new JMenuItem("���ñ༭��ʽ"); // ���ñ༭��ʽ
		popMenuItem_setCellIsHtmlHref = new JMenuItem("�Ƿ�Html����"); //�����Ƿ�Html�����ӷ��
		popMenuItem_setCellValidateFormula = new JMenuItem("����У�鹫ʽ"); //����У�鹫ʽ
		popMenuItem_setCellDesc = new JMenuItem("Set CellDesc"); //
		popMenuItem_showCellProp = new JMenuItem("��ʾCell��������");

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
	 * ��������ؼ��ĳ����ӵ���¼�!!!
	 * @param _listener
	 */
	public void addBillCellHtmlHrefListener(BillCellHtmlHrefListener _listener) {
		vc_HtmlHrefListrners.add(_listener); //
	}

	/**
	 * ����˱��
	 */
	private void onClickedTable(Object source, Point _point, boolean _isCtrl, boolean _isShift, boolean _isAlt) {
		int li_selectedRowCout = table.getSelectedRowCount();
		int li_selectedColCout = table.getSelectedColumnCount();
		int li_row = table.rowAtPoint(_point);//
		int li_column = table.columnAtPoint(_point); //
		if (li_row >= 0 && li_column >= 0) { //��������Ч��������,������������������򲻴���!
			if (li_selectedRowCout <= 1 && li_selectedColCout <= 1) {
				table.setRowSelectionInterval(li_row, li_row);
				table.setColumnSelectionInterval(li_column, li_column); //
			} else {
				return; //
			}
			if (_isCtrl || _isShift) { //����ǰ�ס��Ctrl������Shift��,��ֱ�ӷ��أ���Ϊ�ж�ѡ�����������Ҽ�ѡ����ȡ������ͼ�鿴����,���û��������ƣ��Ե�
				return;
			}
			BillCellItemVO itemVO = getBillCellItemVOAt(li_row, li_column); ////ȡ������
			if (itemVO == null) {
				return;
			}
			if (!"Y".equals(itemVO.getIshtmlhref())) { //����������óɳ����ӵ�,��ֱ�ӷ���!!
				return; //
			}

			//��������˳������¼�!!
			if (vc_HtmlHrefListrners.size() > 0) { //ֻ��ע�����¼��ż�����!!
				try {
					table.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //����ɵȴ���
					BillCellHtmlHrefEvent event = new BillCellHtmlHrefEvent(li_row, li_column, itemVO, this); //����һ���¼�����!!
					event.setCtrlDown(_isCtrl); //
					event.setShiftDown(_isShift); //
					event.setAltDown(_isAlt); //
					for (int i = 0; i < vc_HtmlHrefListrners.size(); i++) { //��������������!!!
						BillCellHtmlHrefListener listener = (BillCellHtmlHrefListener) vc_HtmlHrefListrners.get(i); //
						listener.onBillCellHtmlHrefClicked(event); ////���ü������Ķ���!!
					}
				} catch (Exception _ex) {
					MessageBox.showException(this, _ex);
				} finally {
					table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //�������!!!
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
	 * �����в���..
	 */
	private void onInsertRow() {
		try {
			JLabel label = new JLabel("��������:", SwingConstants.RIGHT); //
			JTextField textField = new JTextField("1", SwingConstants.RIGHT); //
			JCheckBox checkBox = new JCheckBox("�Ƿ��ں�������"); //
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

			if (JOptionPane.showConfirmDialog(this, panel, "���Ƿ�������������?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}

			int li_insertRows = Integer.parseInt(textField.getText());
			boolean isAddOnTail = checkBox.isSelected(); //
			getBillCellVOFromUI(); //
			int li_row = table.getSelectedRow(); // ָ������һ��ǰ����...
			if (li_row < 0) {
				li_row = table.getRowCount() - 1; //
			} else {
				if (isAddOnTail) { //�����
					li_row = li_row + 1;
				}
			}

			int li_column = table.getSelectedColumn(); // ָ������һǰ����
			BillCellItemVO[][] oldItemVOs = this.billCellVO.getCellItemVOs(); //
			int li_oldrowcount = table.getRowCount(); // ������
			int li_oldcolcount = table.getColumnCount(); // ������

			int li_newrowcount = li_oldrowcount + li_insertRows; // �µ�����=�ɵ�����+���������.
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
			table.scrollRectToVisible(rect); // ��������Ӧ���е�λ��!!!!!!!!!!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private void onInsertColumn() {
		try {
			JLabel label = new JLabel("��������:", SwingConstants.RIGHT); //
			JTextField textField = new JTextField("1", SwingConstants.RIGHT); //
			textField.setHorizontalAlignment(JTextField.RIGHT); //
			JCheckBox checkBox = new JCheckBox("�Ƿ��ں�������"); //
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

			if (JOptionPane.showConfirmDialog(this, panel, "���Ƿ�������������?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
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
			int li_oldrowcount = table.getRowCount(); // ������
			int li_oldcolcount = table.getColumnCount(); // ������

			int li_newcolcount = li_oldcolcount + li_insertCols; //
			BillCellItemVO[][] newItemVOs = new BillCellItemVO[li_oldrowcount][li_newcolcount]; //

			// �������е���..
			for (int i = 0; i < li_oldrowcount; i++) {
				// ����ǰ�����
				for (int j = 0; j < li_col; j++) {
					if (oldItemVOs[i][j] != null) {
						newItemVOs[i][j] = oldItemVOs[i][j]; //
					}
				}

				// ����������
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
			table.scrollRectToVisible(rect); // ��������Ӧ���е�λ��!!!!!!!!!!!!
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ɾ����..
	 */
	private void onDeleteRow() {
		try {
			int li_row = table.getSelectedRow(); //
			if (li_row < 0) {
				MessageBox.show(this, "Please select a row.");
				return;
			}

			if (JOptionPane.showConfirmDialog(this, "���Ƿ������ɾ��ѡ�е���������?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}

			int[] li_rows = table.getSelectedRows(); // ѡ�е���������!!!
			int li_column = table.getSelectedColumn(); // ѡ�е���.

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
			int li_oldrowcount = table.getRowCount(); // ������
			int li_oldcolcount = table.getColumnCount(); // ������

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
				table.scrollRectToVisible(rect); // ��������Ӧ���е�λ��!!!!!!!!!!!!
			} else {
				Rectangle rect = this.table.getCellRect(li_row - 1, li_column, true);
				table.scrollRectToVisible(rect); // ��������Ӧ���е�λ��!!!!!!!!!!!!
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * ɾ����..
	 */
	private void onDeleteColumn() {
		try {
			int li_col = table.getSelectedColumn(); // ѡ�е���
			if (li_col < 0) {
				MessageBox.show(this, "Please select a column.");
				return;
			}

			if (JOptionPane.showConfirmDialog(this, "���Ƿ������ɾ��ѡ�е���������?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}

			int[] li_cols = table.getSelectedColumns(); // ѡ�е���������!!!
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
			int li_oldrowcount = table.getRowCount(); // ������
			int li_oldcolcount = table.getColumnCount(); // ������

			int li_newcolcount = li_oldcolcount - li_delcolcount; //
			BillCellItemVO[][] newItemVOs = new BillCellItemVO[li_oldrowcount][li_newcolcount]; //

			// �������е���..
			for (int i = 0; i < li_oldrowcount; i++) {
				// ����ǰ�����
				for (int j = 0; j < li_col; j++) {
					if (oldItemVOs[i][j] != null) {
						newItemVOs[i][j] = oldItemVOs[i][j]; //
					}
				}

				// ����������
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
				table.scrollRectToVisible(rect); // ��������Ӧ���е�λ��!!!!!!!!!!!!
			} else {
				Rectangle rect = this.table.getCellRect(li_row, li_col - 1, true);
				table.scrollRectToVisible(rect); // ��������Ӧ���е�λ��!!!!!!!!!!!!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ���ᴰ�� ��EXCEL�Ķ��ᴰ��
	 */
	public boolean onLock() {
		try {
			int li_col = table.getSelectedColumn(); // ѡ�е���
			int li_row = table.getSelectedRow(); // ѡ�е���
			if (li_col < 0) {
				MessageBox.show(this, "��ѡ��Ԫ��!");
				return false;
			}
			if (JOptionPane.showConfirmDialog(this, "���Ƿ�����붳�ᴰ����?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
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
	 * ȡ�����ᴰ�� ��EXCEL
	 */
	public boolean onUnLock() {
		try {
			int li_row = table.getSelectedRow(); // ѡ�е���
			if (li_row < 0) {
				MessageBox.show(this, "��ѡ��Ԫ��!");
				return false;
			}
			if (JOptionPane.showConfirmDialog(this, "���Ƿ������ȡ�����ᴰ����?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
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
	 * ����Help.
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
					table.setValueAtIgnoreEditEvent(itemVO_tmp, i, j); // ��������
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
			str_oldIsHtmlHref = itemVO.getIshtmlhref(); // �ɵĹ�ʽ...
		}

		JLabel label = new JLabel("�Ƿ�Html����"); //
		label.setHorizontalAlignment(SwingConstants.RIGHT); //
		JCheckBox checkBox = new JCheckBox(); //
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		panel.add(label); ////
		panel.add(checkBox); ////
		if (str_oldIsHtmlHref != null && str_oldIsHtmlHref.equals("Y")) {
			checkBox.setSelected(true); //
		}
		JOptionPane.showMessageDialog(this, panel, "�����Ƿ�Html�����ӷ��", JOptionPane.INFORMATION_MESSAGE); //
		int[] rows = table.getSelectedRows(); //
		int[] columns = table.getSelectedColumns(); //
		for (int i = 0; i < rows.length; i++) {
			for (int j = 0; j < columns.length; j++) {
				setCellItemIshtmlhref(rows[i], columns[j], checkBox.isSelected()); //ָ���ض�������
			}
		}
		table.revalidate(); //
		table.repaint(); //
	}

	//��ǰ̨����ĳ��ĳ���Ƿ��г�����Ч��!!��������ʱ��Ҫ���Ч��!!
	public void setCellItemIshtmlhref(int _row, int _col, boolean _isHtmlHref) {
		BillCellItemVO itemVO_tmp = (BillCellItemVO) table.getValueAt(_row, _col);
		if (itemVO_tmp == null) {
			itemVO_tmp = new BillCellItemVO(); //
			itemVO_tmp.setIshtmlhref(_isHtmlHref ? "Y" : "N"); //
			table.setValueAtIgnoreEditEvent(itemVO_tmp, _row, _col); // ��������
		} else {
			itemVO_tmp.setIshtmlhref(_isHtmlHref ? "Y" : "N"); //	
		}
	}

	/**
	 * ��������ı༭��ʽ
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
			str_oldEditFormula = itemVO.getEditformula(); // �ɵĹ�ʽ...
		}

		RefDialog_BigArea dialog = new RefDialog_BigArea(this, "����༭��ʽ", new RefItemVO(str_oldEditFormula, null, str_oldEditFormula), this, null); //
		dialog.initialize(); // ��ʼ��...
		dialog.setVisible(true); //
		if (dialog.getCloseType() != 1) {//������Ҫ�ж�һ�£�������ǵ��ȷ���˳��ľ�ֱ�ӷ��ء����/2012-04-12��
			return;
		}
		RefItemVO refItemVO = dialog.getReturnRefItemVO(); //
		String str_newEditFormula = refItemVO.getId(); // �µı༭��ʽ

		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO_tmp = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO_tmp == null) {
					itemVO_tmp = new BillCellItemVO(); //
					itemVO_tmp.setEditformula(str_newEditFormula); //
					table.setValueAtIgnoreEditEvent(itemVO_tmp, i, j); // ��������
				} else {
					itemVO_tmp.setEditformula(str_newEditFormula); //	
				}
			}
		}
	}

	/**
	 * ����У�鹫ʽ
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
			str_oldValidateFormula = itemVO.getValidateformula(); // �ɵĹ�ʽ...
		}

		RefDialog_BigArea dialog = new RefDialog_BigArea(this, "����У�鹫ʽ", new RefItemVO(str_oldValidateFormula, null, str_oldValidateFormula), this, null); //
		dialog.initialize(); // ��ʼ��...
		dialog.setVisible(true); //
		if (dialog.getCloseType() != 1) {
			return;
		}
		RefItemVO refItemVO = dialog.getReturnRefItemVO(); //
		String str_validateFormula = refItemVO.getId(); // �µ�У�鹫ʽ..

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
	 * ����Help.
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
					table.setValueAtIgnoreEditEvent(itemVO_tmp, i, j); // ��������
				} else {
					itemVO_tmp.setCellkey(str_key); //	
				}
			}
		}
	}

	/**
	 * �Զ�����CellKey,��ֱ�����к�-�к���ΪCellkey��ֵ..
	 */
	private void autoSetCellKey() {
		int li_row = table.getSelectedRow();
		int li_column = table.getSelectedColumn(); //
		if (li_row < 0 || li_column < 0) {
			MessageBox.show(this, "Please select a row"); //
			return; //
		}

		if (JOptionPane.showConfirmDialog(this, "���Ƿ�������Զ���������CellKey?�⽫�����ǰ��������ֵ!", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}

		int[] rows = table.getSelectedRows(); // ����ѡ�е���
		int[] columns = table.getSelectedColumns(); // ����ѡ�е���

		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO_tmp = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO_tmp == null) {
					itemVO_tmp = new BillCellItemVO(); //
					itemVO_tmp.setCellkey((i + 1) + "-" + table.getColumnName(j)); //
					table.setValueAtIgnoreEditEvent(itemVO_tmp, i, j); // ��������
				} else {
					itemVO_tmp.setCellkey((i + 1) + "-" + table.getColumnName(j)); //	
				}
			}
		}

		table.revalidate();
		table.repaint();

		MessageBox.show(this, "�Զ���������CellKey�ɹ�!");
	}

	/**
	 * ����Help.
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
					table.setValueAtIgnoreEditEvent(itemVO_tmp, i, j); // ��������
				}
				itemVO_tmp.setCelldesc(str_desc); // ����˵��
				itemVO_tmp.setItemTypechanged(true);
			}
		}
	}

	/**
	 * ��ʾ������������.
	 */
	public void showCellAllProp() {
		try {
			int li_row = table.getSelectedRow();
			int li_column = table.getSelectedColumn(); //
			if (li_row < 0 || li_column < 0) {
				MessageBox.show(this, "��ѡ�����ݽ�����ȡ����!"); //
				return; //
			}
			BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(li_row, li_column); //
			if (itemVO == null) {
				MessageBox.show(this, "ѡ�и��ӵ�����Ϊ��!"); //
				return;

			}
			StringBuilder sb_prop = new StringBuilder("Cell����������ֵ:\r\n");
			sb_prop.append("CellKey=[" + itemVO.getCellkey() + "]\r\n");
			sb_prop.append("CellType=[" + itemVO.getCelltype() + "]\r\n");
			sb_prop.append("CellValue=[" + itemVO.getCellvalue() + "]\r\n");
			sb_prop.append("CellRowCol=[" + itemVO.getCellrow() + "," + itemVO.getCellcol() + "]\r\n");
			sb_prop.append("CellWidthHeight=[" + itemVO.getColwidth() + "," + itemVO.getRowheight() + "]\r\n");
			sb_prop.append("CellSpan=[" + itemVO.getSpan() + "]\r\n"); //�ϲ�
			sb_prop.append("Loadformula(���ع�ʽ)=[" + itemVO.getLoadformula() + "]\r\n");
			sb_prop.append("Editformula(�༭��ʽ)=[" + itemVO.getEditformula() + "]\r\n");
			sb_prop.append("Validateformula(У�鹫ʽ)=[" + itemVO.getValidateformula() + "]\r\n");

			sb_prop.append("\r\n"); //

			sb_prop.append("�Զ���Map�е�����ֵ:\r\n"); //
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
	 * ��������
	 */
	private void loadBillCellData(String _templetCode) throws Exception {
		loadBillCellData(_templetCode, null, null); //
	}

	private void loadBillCellData(String _templetCode, String _billNo, String _descr) throws Exception {
		loadBillCellData(getMetaService().getBillCellVO(_templetCode, _billNo, _descr)); //
	}

	/**
	 * ͨ��һ��BillNO��ʵ������ȡ����������ģ����!!!!
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
		getMetaService().getCellSaveDate(this.billCellVO.getId(), _billno, hashmap); // ��Զ�̷�������!!
	}

	/**
	 * ͨ��Billno��key��� ��������billno��Ӧ��ÿ��key����һ��
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
	 * �����б�Ԫ��ʱ���·�״̬����ʾ��Ϣ����
	 * @author YangQing/2013-09-25
	 */
	private void selectedInfo() {
		int[] sel_row = this.getTable().getSelectedRows();//�õ���ѡ�е��к�
		int[] sel_column = this.getTable().getSelectedColumns();//�õ���ѡ�е��к�
		String[][] selectCelValue = new String[sel_row.length][sel_column.length];//���ѡ�е�Ԫ���ֵ
		List list = new ArrayList();
		HashSet<String> spaned = new HashSet<String>();//���ϲ��ˡ�
		for (int i = 0; i < sel_row.length; i++) {
			for (int y = 0; y < sel_column.length; y++) {
				if (spaned.contains(sel_row[i] + "_" + sel_column[y])) { //������ϲ��ˡ�
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
		Pattern pattern = Pattern.compile("^\\+?\\d*\\.?\\d+$|^-?\\d*\\.?\\d+$");//ƥ������������С��
		String celinfo = "";//����״̬��Ҫ��ʾ���ַ���
		double sum = 0;//��ֵ�ܺ�
		int numvalue_count = 0;//��ֵ����
		boolean allPersent = true; //Ĭ��
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
				sum += dle_selvalue;//��������֣����ۼ��ϣ���͡���ƽ��ֵ
				numvalue_count++;
				if (!isper) {
					allPersent = false;
				}
			}
		}
		if (numvalue_count <= 1) { //�������һ������ ����20130926
			celinfo = "�� ������" + (list.size()) + " ��";
		} else {
			BigDecimal big = new BigDecimal(sum / numvalue_count).setScale(4, BigDecimal.ROUND_HALF_UP);
			boolean ydy = false; // �Ƿ��� Լ���ڡ�
			if (sum / numvalue_count != big.doubleValue()) {
				ydy = true;
			}
			java.text.DecimalFormat d_f = new java.text.DecimalFormat("########.####");
			double bigvalue = big.doubleValue();
			String avgvalue = d_f.format(allPersent ? bigvalue * 100f : bigvalue);
			double lastSum = (double) (allPersent ? sum * 100f : sum);
			celinfo = "�� ������" + (list.size()) + "  ��ֵ������" + (numvalue_count) + "  ��ͣ�" + d_f.format(lastSum) + (allPersent ? "%" : "") + "  ƽ��ֵ" + (ydy ? "��" : ":") + avgvalue + (allPersent ? "%" : "") + " ��";
		}
		DeskTopPanel.setSelectInfo(celinfo);//��ʾ
	}

	/**
	 * ͨ��Billno��key��� ��������billno��Ӧ��ÿ��key����һ��
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
	 * ��������
	 * 
	 * @param _billCellVO
	 * @throws Exception
	 */
	public void loadBillCellData(BillCellVO _billCellVO) throws Exception {
		this.billCellVO = _billCellVO; //
		this.billCellItemVos = _billCellVO.getCellItemVOs(); //
		this.templetcode = _billCellVO.getTempletcode(); // ģ������
		this.templetname = _billCellVO.getTempletname(); //
		this.billNo = _billCellVO.getBillNo(); //

		initTable(_billCellVO.getRowlength(), _billCellVO.getCollength()); // ��ʼ�����
		BillCellItemVO[][] cellItemVOs = _billCellVO.getCellItemVOs(); //
		if (cellItemVOs != null && cellItemVOs.length > 0) {
			int[] li_widths = new int[cellItemVOs[0].length]; //
			int[] li_heights = new int[cellItemVOs.length]; //
			int li_maxwidth = 0; //
			int li_maxheight = 0; //
			int li_itemRowHeight = 0;
			int li_itemColWidth = 0;
			//�����һ���е����ĸ߶�
			if (ifSetRowHeight) {
				for (int i = 0; i < cellItemVOs.length; i++) { //��������
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

			//���һ�������Ŀ��
			for (int j = 0; j < cellItemVOs[0].length; j++) {
				li_maxwidth = 0; //
				for (int i = 0; i < cellItemVOs.length; i++) { //��������
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
			for (int i = 0; i < cellItemVOs.length; i++) { //��������
				if (ifSetRowHeight) {
					this.table.setRowHeight(i, li_heights[i]); //�и�
					if (rowHeaderTable != null) { //����û��ͷ!
						this.rowHeaderTable.setRowHeight(i, li_heights[i] + 1); //�и�
					}
				}
				for (int j = 0; j < cellItemVOs[i].length; j++) {
					itemVO = cellItemVOs[i][j]; //
					if (itemVO != null) {
						this.table.setValueAtIgnoreEditEvent(itemVO, i, j); //
						if (i == 0) {
							this.table.getColumnModel().getColumn(j).setPreferredWidth(li_widths[j]); //�����п�,��ҪҪ����һ����󻯵�����!!
						}
						str_span = itemVO.getSpan(); //
						if (str_span != null) {
							str_items = str_span.split(","); //
							li_spanrow_length = Integer.parseInt(str_items[0]);
							li_spancol_length = Integer.parseInt(str_items[1]);
							if (li_spanrow_length > 1 || li_spancol_length > 1) { // �����һ������1,˵���Ǻϲ���..
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
	 * ��ĳЩkey����ֵ
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

	//�������������key��ĸ�������Ϊ��!!!
	public void resetAllKeysCell() {
		String[] str_keys = getKeys(); //
		if (str_keys != null && str_keys.length > 0) {
			for (int i = 0; i < str_keys.length; i++) {
				setValueAt(str_keys[i], null); //
			}
		}
	}

	/**
	 * ��ĳЩkey����ֵ,�������༭��ʽ
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
	 * ���غϲ���������
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
	 * ȡ����������
	 * 
	 * @return
	 */
	public int getRowCount() {
		return table.getRowCount(); //
	}

	/**
	 * �õ���������
	 * 
	 * @return
	 */
	public int getColumnCount() {
		return table.getColumnCount(); //
	}

	/**
	 * ȡ���������Ŀ��
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
	 * ��ĳЩkey����ֵ
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
	 * ȡ������key,����Щ����key�ĸ���
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
	 * ���������ݷ��س�һ��HashMap,���е�key���Ǹ����������cellkey
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
	 * ��ĳЩkey����ֵ
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
	 * ��ĳЩkey����ֵ
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
	 * �ϲ�����
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
	 * ��ĳ��ָ���������п�ʼ,���з��������ºϲ�����,���з��������ϲ�����!!!
	 * ���� span(2,3,5,7),����ʾ�ӵ�2��,��3�п�ʼ,���з��������ºϲ�5��,���з��������Һϲ�7��
	 * @param _startRow ����һ�п�ʼ!
	 * @param _startCol ����һ�п�ʼ!
	 * @param _spanRows �з����Ϻϲ�����,������ϲ�,Ӧ���1,�����0Ҳû��,��Ϊ�����ݴ���!
	 * @param _spanCols �з����Ϻϲ�����,������ϲ�,Ӧ���1,�����0Ҳû��,��Ϊ�����ݴ���!
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
		span(spanRows, spanCols); //�ϲ�!!!
	}

	/**
	 * ��ָ���������з�Χ���кϲ�,����ͬ����������Ҫ��,���ӵ�2��,��3�п�ʼ,���з��������ºϲ�5��,���з��������Һϲ�7��
	 * �����Ӧ�� span(new int[]{2,3,4,5,6},new int[]{3,4,5,6,7,8,9})
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
	 * �ָ�����
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
	 * ��ɫѡ��仯
	 */
	private void onColorchanged(int _type) {
		Color initColor = null; //
		String str_title = ""; //
		if (_type == 1) {
			str_title = "ǰ��"; //
			initColor = btn_forecolor.getBackground(); //
		} else if (_type == 2) {
			str_title = "����"; //
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
				table.setValueAtIgnoreEditEvent(itemVO, i, j); // ��������
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
	 * �������
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

				//�������ɱ༭��
				if (!(itemVO.getIseditable() != null && itemVO.getIseditable().equals("N"))) {
					itemVO.setCellvalue("");
				}

				table.setValueAtIgnoreEditEvent(itemVO, i, j); // ��������
			}
		}

		if (table.getCellEditor() != null) {
			table.getCellEditor().stopCellEditing();
		}
		table.revalidate();
		table.repaint();
	}

	/**
	 * ���巢���ı�
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
				table.setValueAtIgnoreEditEvent(itemVO, i, j); // ��������
			}
		}

		// table.clearSelection(); //
		table.revalidate();
		table.repaint();
	}

	/**
	 * �����������仯!!!
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
				table.setValueAtIgnoreEditEvent(itemVO, i, j); // ��������
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
	 * @param _halign ����,һ��Ҫע�⣺1-����;2-����;3-���ң�����SwingConst.CENTER
	 */
	public void setHalign(int[] rows, int[] columns, int _halign) {
		for (int i = rows[0]; i <= rows[rows.length - 1]; i++) {
			for (int j = columns[0]; j <= columns[columns.length - 1]; j++) {
				BillCellItemVO itemVO = (BillCellItemVO) table.getValueAt(i, j);
				if (itemVO == null) {
					itemVO = new BillCellItemVO(); //

				}
				itemVO.setHalign(_halign); //
				table.setValueAtIgnoreEditEvent(itemVO, i, j); // ��������
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
		table.setValueAtIgnoreEditEvent(itemVO, _i, _j); // ��������

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
				table.setValueAtIgnoreEditEvent(itemVO, i, j); // ��������
			}
		}

		// table.clearSelection(); //
		table.revalidate();
		table.repaint();
	}

	/**
	 * �����������仯!!!
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
				table.setValueAtIgnoreEditEvent(itemVO, i, j); // ��������
			}
		}

		// table.clearSelection(); //
		table.revalidate();
		table.repaint();
	}

	/**
	 * �����Ƿ���ʾCellKey
	 * 
	 * @param _checkbox
	 */
	private void setIsShowCellKey(JCheckBox _checkbox) {
		((MultiSpanCellTable) table).setShowCellKey(_checkbox.isSelected()); //
		table.revalidate();
		table.repaint();
	}

	/**
	 * �ؼ����ͷ����仯...
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
					itemVO.setCelltype(str_celltype); // ���ÿؼ�����....
					itemVO.setItemTypechanged(true); //
					table.setValueAtIgnoreEditEvent(itemVO, i, j); // ��������
				} else {
					itemVO.setCelltype(str_celltype); // ���ÿؼ�����....
					itemVO.setItemTypechanged(true); //
				}
			}
		}

		// table.clearSelection(); //
		table.revalidate();
		table.repaint();
	}

	/**
	 * ִ�б༭��ʽ..
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
			for (int i = 0; i < str_editorFormulas.length; i++) { // ����ִ�����й�ʽ
				formulaParse.execFormula(str_editorFormulas[i]); //
			}
		} // ������ʽ����!!!

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
			this.setOpaque(true); //��͸��
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
			// System.out.println("�к�[" + row + "]"); //
			if (row == -1) {
				return -1;
			}

			int col = tmptable.columnAtPoint(p);
			if (col == -1)
				return -1;
			Rectangle r = tmptable.getCellRect(row, col, true); // ����ж�Ӧ���еķ�Χ����
			r.grow(0, -3); // ������ķ�Χ����3�����أ�����������3�������ڣ����ͻ��
			if (r.contains(p)) // �����Ӧ�����з�Χ�������ķ�Χ
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
			// System.out.println("ȡ���к�[" + li_row + "]"); //
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
							tmptable.setRowHeight(i, newHeight); // �����µ��и�..
							table.setRowHeight(i, newHeight - 1); // ���������е��и�
						}
					} else {
						tmptable.setRowHeight(resizingRow, newHeight); // �����µ��и�..
						table.setRowHeight(resizingRow, newHeight - 1); // ���������е��и�
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
			if (e.getType() == TableModelEvent.UPDATE) { // ֻ�����޸��¼�..
				// int firstRow = e.getFirstRow();
				int lastRow = e.getLastRow();
				int mColIndex = e.getColumn();
				AttributiveCellTableModel tableModel = (AttributiveCellTableModel) e.getSource(); //
				if (table.isTriggerEditListenerEvent()) { // �����Ҫ�����༭�¼�
					BillCellItemVO itemVO = (BillCellItemVO) tableModel.getValueAt(lastRow, mColIndex);
					if (itemVO != null) {
						if (itemVO.getEditformula() != null) {
							execEditFormula(itemVO.getEditformula(), lastRow, mColIndex); // ִ�б༭��ʽ.
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
			MessageBox.show(this, "������������⡣");
			return false;
		}
		if (_col <= 0 || _col >= table.getColumnCount()) {
			MessageBox.show(this, "������������⡣");
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
