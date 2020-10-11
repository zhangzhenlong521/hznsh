/**************************************************************************
 * $RCSfile: BillListPanel.java,v $  $Revision: 1.170 $  $Date: 2013/02/28 06:14:42 $
 **************************************************************************/

package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.PanelUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.apache.log4j.Logger;
import org.jdesktop.jdic.desktop.Desktop;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillItemVO;
import cn.com.infostrategy.to.mdata.BillListDataNumberComparator;
import cn.com.infostrategy.to.mdata.BillListDataStrComparator;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1ParVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.jepfunctions.JepFormulaParse;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.AbstractBillListQueryCallback;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.DataTransport;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.ImageIconFactory;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.MultiLineToolTip;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTMenuItemUI;
import cn.com.infostrategy.ui.common.WLTMenuUI;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTPanelUI;
import cn.com.infostrategy.ui.common.WLTScrollPanel;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.common.WeiduSearchButton;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ChildTable;
import cn.com.infostrategy.ui.mdata.cardcomp.RecordShowDialog;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_File;
import cn.com.infostrategy.ui.mdata.combine.CombineTable;
import cn.com.infostrategy.ui.mdata.listcomp.IBillCellEditor;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_Button;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_CheckBox;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_ComboBox;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_FileChoose;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_Label;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_Ref;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_RowNumber;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_StylePad;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_TextArea;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_TextField;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellRender_Button;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellRender_CheckBox;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellRender_FileChoose;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellRender_JLabel;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellRender_JTextArea;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellRender_RowNumber;
import cn.com.infostrategy.ui.mdata.listcomp.QuickSearchDialog;
import cn.com.infostrategy.ui.mdata.listcomp.ResetOrderConditionDialog;
import cn.com.infostrategy.ui.mdata.listcomp.ShowColumnDataAsPieChartDialog;
import cn.com.infostrategy.ui.mdata.listcomp.ShowHideSortTableColumnDialog;
import cn.com.infostrategy.ui.mdata.querycomp.QuickPutValueDialog;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;
import cn.com.infostrategy.ui.report.BillHtmlPanel;
import cn.com.infostrategy.ui.report.cellcompent.ExcelUtil;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;
import cn.com.infostrategy.ui.workflow.engine.WorkflowDealBtnPanel;

public class BillListPanel extends BillPanel {

	private static final long serialVersionUID = 8051676743971788439L;

	private String str_templetcode = null;
	public Pub_Templet_1VO templetVO = null;
	private Pub_Templet_1_ItemVO[] templetItemVOs = null;

	private JPanel unTitlePanel = null; //除了标题栏之外的其他所有内容的面板!实际上就是三个(快速查询,工具条栏,表格) 
	private JPanel centerPanel = null; //真正的内容面板!!

	private JPanel titlePanel = null; //标题面板
	private JLabel label_temname = null; //

	private boolean isHiddenUntitlePanelBtnvVisiable = false; //
	private boolean isToolBarPanelDownRight = false; //是否工具条突出显示! 中铁建项目上UI要求就是工具条是突出光亮显示的!!!

	private WLTButton btn_shuntitle, btn_shqq = null; //显示/隐藏快速查询面板的按钮..
	private ImageIcon icon_showqq = null;
	private ImageIcon icon_hiddenqq = null;

	private Vector v_listbtnListener = new Vector(); // 自定义按钮监听事件
	private Vector v_listHtmlHrefListener = new Vector(); // 自定义按钮监听事件
	private BillQueryPanel quickQueryPanel = null;
	private JScrollPane scrollPanel_main = null; //

	private JTable table = null; //
	private JViewport jv = null; //
	private JTable rowHeaderTable = null;
	private TableColumn rowNumberColumn = null; //

	private int li_rowHeight = 22; //
	private DefaultTableColumnModel rowHeaderColumnModel = null;
	private DefaultTableColumnModel columnModel = null;
	private TableColumn[] allTableColumns = null;
	private BillListModel tableModel = null;

	private String str_dataFilterCustCondition = null; //
	private String str_dataFilterCustOrCondition = null; //
	private String str_orderCustCondition = null; //

	private String str_realsql = null; // 实际的SQL
	private String str_calltrackmsg = null; //调用的日志!!!

	private String str_dataPolicyInfo = null; //数据权限的说明
	private String str_dataPolicySQL = null; //数据权限的说明

	private Object[][] all_realValueData = null; //当前数据!!!

	private int li_currpage = 1; //当前页!!!
	private int li_onepagerecords = 50; //每页显示多少,默认是50!!! 改为50 Gwang 2013-10-17
	private int li_TotalRecordCount = 0; //所有记录数!!!
	private JComboBox jComboBox_onePageRecords = null; // 每页有多少条记录

	private static int datalength = 0;

	private int li_mouse_x, li_mouse_y = 0;
	boolean bo_tableislockcolumn = false; //
	private Vector v_lockedcolumns = new Vector();
	private boolean showPopMenu = true;

	private TBUtil tBUtil = null; // 转换工具!!
	private AbstractWorkPanel loadedWorkPanel = null; //加载该列表的抽象工作面板
	private BillFormatPanel loaderBillFormatPanel = null; //加载该列表的公式面板
	private CardCPanel_ChildTable loaderChildTable = null; //加载该列表的引用子表

	private JButton firstPageButton, pageupButton, pagedownButton, lastPageButton, goToPageButton = null;

	private boolean bo_isShowPageNavigation = false; // 是否显示分页导航条..
	private boolean bo_isShowOperatorNavigation = false;

	public JTextField goToPageTextField = null;
	public JLabel label_pagedesc = null;

	public String str_rownumberMark = "_RECORD_ROW_NUMBER";
	public String str_rownumberMarkName = " "; // 行号的名称

	public boolean bo_ifProgramIsEditing = false; // 是否程序真正编辑

	public int[] searchcolumn = null;
	public String str_currsortcolumnkey = null;
	public int li_currsorttype = 0;
	private Vector v_deleted_row = new Vector();

	private int templete_codecolumn_index = -1;
	private JPanel toolbarPanel = null; //
	protected JPanel toolbarPanel_content = null;

	private BillButtonPanel billListBtnPanel = null; // 按钮面板
	private WorkflowDealBtnPanel wf_btnPanel = null; // 工作流板

	protected JPanel customerNavigationJPanel = new JPanel();
	private JPanel pagePanel = null;

	private AbstractListCustomerButtonBarPanel listCustPanel = null;

	private Vector v_listeners = new Vector(); // 反射注册的事件监听者!!!
	private Vector v_selectedListeners = new Vector(); // 反射注册的事件监听者!!!
	private Vector v_checkedAllListeners = new Vector();// 监听序号的勾选框 袁江晓 20131029 添加
	private Vector v_afterqueryListeners = new Vector(); // 反射注册的事件监听者!!!,查询后进行的处理
	private Vector v_MouseDoubleClickListeners = new Vector(); // 反射注册的事件监听者!!!,查询后进行的处理

	private boolean is_admin = false;

	private Hashtable ht_groupcolumn = new Hashtable();
	private int li_rowno_width = 37; // 行号宽度,设为0可以去掉,37
	private HashMap hm_initeditable = new HashMap(); //

	private JMenuItem menuItem_showuitruedata, menuItem_showdbtruedata, menuItem_showCard, menuItem_dirdelrecord, menuItem_dirupdaterecord, menuItem_dirdelworkflowinfo; //直接删除与修改记录
	private JPopupMenu rightPopMenu = null; // 右击表格中右键时弹出的菜单
	private ActionListener recordPopMenuAction = null; //

	private JPopupMenu popmenu_header = null; // 点击表头中右键时弹出的菜单
	private JMenu menu_table_templetmodify, menu_excel, menu_db; //
	private JMenuItem item_column_lock, item_column_search, item_column_quickputvalue, item_column_quickstrike, item_column_weidusearch, item_column_quicksavecurrwidth, item_column_quicksaverowheight, item_table_setLineVisiable, menu_table_exportprint, menu_table_exporthtml, item_column_lookhelp, item_column_piechart, item_table_showhidecolumn, item_table_resetOrderCons,
			item_table_templetmodify_1, item_table_templetmodify_2, item_state;
	private JMenuItem menu_db_addflow4_database, menu_db_addflow4_templet, menu_db_addflow13_templet, menu_transportToDataSource, item_table_showsql, menu_db_autoBuildData;
	private ActionListener headerPopMenuAction = null; //

	public boolean isCanShowCardInfo = true; //是否显示卡片信息
	public boolean isHeaderCanSort = true; //表头双击是否可以排序
	private boolean isRowNumberChecked = false;//  表头是否是勾选框的样式!即以前的选择不同,大量客户喜欢勾选的样式,南京油运项目中就曾遇到说工作流处理选择人时Ctrl键不好!!

	private FrameWorkMetaDataServiceIfc metaDataService = null; //元数据远程服务.
	private String str_tableToolTip = "提示:按住Ctrl键点击,可多选或取消已选记录!"; //总是提醒这个,因为有许多人不知道如何多选或取消表格中已选数据!!!想来想去,最省事的就是永远来这个提醒!!

	private Logger logger = WLTLogger.getLogger(BillListPanel.class); //
	private java.awt.Dimension oldDimension = null; //
	private HashMap linkChildTempletVOMap = null; //存储引用子表模板VO的map,因为后来在取数据时,需要取出引用子表中的数据,而对一个列表来说,如果选择多条数据,则不要每一条数据都要计算一下引用子表的模板VO(否则性能太慢),所以搞一个Map

	private String isRefreshParent = "-1"; //袁江晓添加  主要用来表示如果该billlist为父页面，如果关闭子页面是否需要刷新该billlist,不设置则刷新，如果设置为1则不刷新
	private boolean canRefreshParent = false; //袁江晓 20130509修改 
	private boolean combine_mark = true; //合并跳过批量初始化数据标记 【杨科/2013-07-23】 
	private ArrayList<Integer> combineColumns = null; //合并列 【杨科/2013-07-23】 
	private int lock_pos = -1; //合并锁定标记 【杨科/2013-07-23】 
	private ListCellEditor_RowNumber editor = null;// 袁江晓 20131029 添加 主要获得是否选中勾选框
	private AbstractBillListQueryCallback listQueryCallback;
	private ExcelUtil excel = new ExcelUtil();
	private int [] color;//zzl 修改标题颜色

	private int count;

	public static int getDatalength() {
		return datalength;
	}

	public static void setDatalength(int datalength) {
		BillListPanel.datalength = datalength;
	}

	public BillListPanel() {
	}

	public BillListPanel(String _templetcode) {
		this(_templetcode, true); //
	}

	/**
	 * 使用模板编码创建!!
	 * @param _templetcode
	 * @param _isAutoInitialize 是否自动初始始化页面!即立即调用initialize()方法! 因为经常有需要在构建界面之前修改一些属性! 但这样就需要手工调用initialize了!
	 */
	public BillListPanel(String _templetcode, boolean _isAutoInitialize) {
		if (_templetcode.indexOf(".") > 0) { // 如果是个类名,即编码中有个".",我们则认为是个类名!!则直接反射调用
			try {
				init((AbstractTMO) Class.forName(_templetcode).newInstance(), _isAutoInitialize);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		} else { // 如果是真正的编码!
			init(_templetcode, _isAutoInitialize);
		}
	}

	public BillListPanel(String _templetcode, boolean _isAutoInitialize, boolean _isToolBarPanelDownRight) {
		this.isToolBarPanelDownRight = _isToolBarPanelDownRight; //是否工具条光亮显示,中铁的UI效果!!临时加上这个构造方法,实际上单独一个列表效果是可以的,但如果是在一个复杂界面中有几个列表则不行!!!
		if (_templetcode.indexOf(".") > 0) { // 如果是个类名,即编码中有个".",我们则认为是个类名!!则直接反射调用
			try {
				init((AbstractTMO) Class.forName(_templetcode).newInstance(), _isAutoInitialize);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		} else { // 如果是真正的编码!
			init(_templetcode, _isAutoInitialize);
		}
	}

	/**
	 * 极限根据一个SQL语句就能生成一个列表
	 * 
	 * @param _daName
	 * @param _sql
	 */
	public BillListPanel(String _dsanme, String _sql) {
		try {
			HashVOStruct hashVOStructs = UIUtil.getHashVoStructByDS(_dsanme, _sql); // 当场取数据!!!
			String[] allcolumns = hashVOStructs.getHeaderName(); // 所有的列名
			HashVO[] hvs = hashVOStructs.getHashVOs(); // 所有的数据

			templetVO = UIUtil.getPub_Templet_1VO(getTMO(allcolumns, 125)); // 元原模板VO
			templetItemVOs = templetVO.getItemVos(); // 各项
			str_templetcode = templetVO.getTempletcode();
			initialize();
			putValue(hvs);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	public BillListPanel(HashVOStruct _hashVOStructs) {
		this(_hashVOStructs, 125);//
	}

	/**
	 * 
	 * @param zzl  最后列颜色
	 */
	public void setTableCellRenderer(TableCellRenderer tempCellRenderer) {
		if (null != tempCellRenderer) {
			TableColumnModel tcm = getTable().getColumnModel();
			for (int i = tcm.getColumnCount() - 1, n = tcm.getColumnCount(); i < n; i++) {
				TableColumn tc = tcm.getColumn(i);
				tc.setCellRenderer(tempCellRenderer);
			}
		}
	}

	/**
	 * 
	 * @param zzl  全行颜色
	 */
	public void setTableCellRendererHE(TableCellRenderer tempCellRenderer) {
		if (null != tempCellRenderer) {
			TableColumnModel tcm = getTable().getColumnModel();
			for (int i = 0; i < tcm.getColumnCount(); i++) {
				TableColumn tc = tcm.getColumn(i);
				tc.setCellRenderer(tempCellRenderer);
			}
		}
	}

	/**
	 * 
	 * @param zzl
	 * 可以传一个数字进去  让第几列显示颜色
	 */
	public void setTableCellRenderer(TableCellRenderer tempCellRenderer, int a) {
		if (null != tempCellRenderer) {
			TableColumnModel tcm = getTable().getColumnModel();
			for (int i = 0; i < tcm.getColumnCount(); i++) {
				TableColumn tc = tcm.getColumn(a);
				tc.setCellRenderer(tempCellRenderer);
			}
		}
	}

	/**
	 * 极限根据一个SQL语句就能生成一个列表
	 * 
	 * @param _daName
	 * @param _sql
	 */
	public BillListPanel(HashVOStruct _hashVOStructs, int _width) {
		try {
			String[] allcolumns = _hashVOStructs.getHeaderName(); // 所有的列名
			HashVO[] hvs = _hashVOStructs.getHashVOs(); // 所有的数据
			templetVO = UIUtil.getPub_Templet_1VO(getTMO(allcolumns, _width)); // 元原模板VO
			templetItemVOs = templetVO.getItemVos(); // 各项
			str_templetcode = templetVO.getTempletcode();
			initialize();
			putValue(hvs);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	public BillListPanel(HashVO[] _hvs) {
		this((String) null, _hvs); //
	}

	//直接使用HashVO[]构造,最极端
	public BillListPanel(String _templetName, HashVO[] _hvs) {
		try {
			int[] li_colWidths = new int[] { 85 }; //
			String[] allcolumns = null; //
			if (_hvs == null || _hvs.length <= 0) {
				allcolumns = new String[] { "直接使用HashVO[]构造,而数据为空" }; //
				li_colWidths = new int[] { 350 }; //
			} else {
				allcolumns = _hvs[0].getKeys(); //
				int[] li_widths = new int[allcolumns.length]; //智能根据数据的宽度计算列宽!!
				for (int j = 0; j < li_widths.length; j++) {
					li_widths[j] = 85; //
				}
				for (int j = 0; j < allcolumns.length; j++) {
					for (int i = 0; i < _hvs.length; i++) {
						String str_text = _hvs[i].getStringValue(allcolumns[j]); //
						int li_w = getTBUtil().getStrUnicodeLength(str_text) * 8; //
						li_w = (li_w > 300 ? 300 : li_w); //上限是300
						if (li_w > li_widths[j]) {
							li_widths[j] = li_w; //
						}
					}
				}
				li_colWidths = li_widths; //
			}
			templetVO = UIUtil.getPub_Templet_1VO(getTMO(allcolumns, li_colWidths, _templetName)); // 元原模板VO
			templetItemVOs = templetVO.getItemVos(); // 各项
			str_templetcode = templetVO.getTempletcode();
			initialize();
			putValue(_hvs);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * 根据直接生成的抽象模板VO创建页面!!!,即配置数据不存在 pub_teplet_1表与pub_teplet_1_item表中的!!!
	 * 
	 * @param _abstractTempletVO
	 */
	public BillListPanel(AbstractTMO _abstractTempletVO) {
		init(_abstractTempletVO, true);
	}

	/**
	 * 根据直接生成的抽象模板VO创建页面!!!,即配置数据不存在 pub_teplet_1表与pub_teplet_1_item表中的!!!
	 * 
	 * @param _abstractTempletVO
	 */
	public BillListPanel(AbstractTMO _abstractTempletVO, boolean _isAutoInit) {
		init(_abstractTempletVO, _isAutoInit);
	}

	public BillListPanel(ServerTMODefine _serverTMO) {
		this(_serverTMO, true); //
	}

	/**
	 * 直接根据ServerTMO创建
	 * @param _serverTMO
	 */
	public BillListPanel(ServerTMODefine _serverTMO, boolean _isAutoInit) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_serverTMO); //
			templetItemVOs = templetVO.getItemVos(); // 各项
			str_templetcode = templetVO.getTempletcode();
			if (_isAutoInit) {
				initialize();
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	public BillListPanel(Pub_Templet_1VO _templetVO) {
		this(_templetVO, true);
	}

	public BillListPanel(Pub_Templet_1VO _templetVO, boolean _isAutoInitialize) {
		templetVO = _templetVO;
		templetItemVOs = templetVO.getItemVos(); // 各项
		str_templetcode = templetVO.getTempletcode();
		if (_isAutoInitialize) { //是否自动初始始化
			initialize();
		}
	}

	private void init(String _templetcode, boolean _isAutoInitialize) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_templetcode);
			templetItemVOs = templetVO.getItemVos(); // 各项
			str_templetcode = templetVO.getTempletcode();
			if (_isAutoInitialize) {
				initialize();
			}
		} catch (Exception e) {
			MessageBox.showException(this, e);
		} // 取得单据模板VO
	}

	private void init(AbstractTMO _abstractTempletVO, boolean _isAutoInitialize) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_abstractTempletVO);
			templetItemVOs = templetVO.getItemVos(); // 各项
			str_templetcode = _abstractTempletVO.getPub_templet_1Data().getStringValue("templetcode"); //
			if (_isAutoInitialize) {
				initialize(); //
			}
		} catch (Exception e) {
			MessageBox.showException(this, e);
		} //	
	}

	/**
	 * 根据直接生成的抽象模板VO创建页面!!!,即配置数据不存在 pub_teplet_1表与pub_teplet_1_item表中的!!!
	 * @param _abstractTempletVO
	 */
	public void initialize() {
		DeskTopPanel.setSelectInfo("");//每次初始化清空下选中单元格状态信息[YangQing/2013-09-25]
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getListiscombine() && i > 0) { //列表是否合并 【杨科/2013-07-26】 
				if (combineColumns == null) {
					combineColumns = new ArrayList<Integer>();
				}
				combineColumns.add(i - 1);
			}
			hm_initeditable.put(templetItemVOs[i].getItemkey(), templetItemVOs[i].getListiseditable()); //是否强编辑!!先置起来!!
		}

		this.removeAll(); //
		this.setLayout(new BorderLayout());
		this.setBackground(LookAndFeel.defaultShadeColor1); //统统是系统默认颜色!!
		this.setUI(new WLTPanelUI(BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE, Color.WHITE, false)); //
		//this.setOpaque(false); //
		if (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) {
			this.is_admin = true;
		}

		this.bo_isShowPageNavigation = templetVO.getIsshowlistpagebar().booleanValue(); // 是否显示分页导航条
		this.bo_isShowOperatorNavigation = templetVO.getIsshowlistopebar().booleanValue(); // 是否显示数据库操作导航条

		TitledBorder border = BorderFactory.createTitledBorder("[" + templetVO.getTempletname() + "]");
		border.setTitleFont(new Font("宋体", Font.PLAIN, 12 + LookAndFeel.getFONT_REVISE_SIZE()));
		border.setTitleColor(Color.BLUE); //

		scrollPanel_main = new JScrollPane(); //
		scrollPanel_main.setOpaque(false); //透明!!
		scrollPanel_main.getViewport().setOpaque(false); //透明!!!
		scrollPanel_main.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);//孙富君
		scrollPanel_main.getViewport().add(getTable()); // 实际内容的表格!!

		rowHeaderColumnModel = new DefaultTableColumnModel();
		rowHeaderColumnModel.addColumn(getRowNumberColumn()); //加入行号列!这里处理了行号!!!

		rowHeaderTable = new ResizableTable(getTableModel(), rowHeaderColumnModel); // 创建新的表..
		rowHeaderTable.setGridColor(LookAndFeel.tableHeadLineClolr); //
		rowHeaderTable.setRowHeight(li_rowHeight + LookAndFeel.getFONT_REVISE_SIZE()); //
		rowHeaderTable.setRowSelectionAllowed(true);

		rowHeaderTable.setColumnSelectionAllowed(false);
		rowHeaderTable.getTableHeader().setReorderingAllowed(false);
		rowHeaderTable.getTableHeader().setResizingAllowed(false);
		LockTableHeaderRender lockTHR = new LockTableHeaderRender(this, rowHeaderTable);
		rowHeaderTable.getTableHeader().setDefaultRenderer(lockTHR); //
		rowHeaderTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		rowHeaderTable.setSelectionModel(getTable().getSelectionModel()); //

		jv = new JViewport(); //
		jv.setView(rowHeaderTable);
		jv.setOpaque(false);
		LookAndFeel.installProperty(jv, "opaque", Boolean.FALSE);
		int li_height = new Double(rowHeaderTable.getMaximumSize().getHeight()).intValue();
		jv.setPreferredSize(new Dimension(li_rowno_width, li_height)); //
		scrollPanel_main.setRowHeader(jv); //关键设置,即将行号表放在滚动框的左边!!

		if ("DB".equals(this.getTempletVO().getBuildFromType())) { //有三种类型:XML2,DB,CLASS
			rowHeaderTable.getColumnModel().getColumn(0).setHeaderValue("-");
		} else if ("CLASS".equals(this.getTempletVO().getBuildFromType())) {
			rowHeaderTable.getColumnModel().getColumn(0).setHeaderValue("=");
		}

		scrollPanel_main.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeaderTable.getTableHeader());
		scrollPanel_main.setBorder(BorderFactory.createEmptyBorder()); //

		JViewport jv2 = new JViewport();
		jv2.setOpaque(false); //一定要设一下，否则上边总有个白条
		jv2.setView(getTable().getTableHeader());
		scrollPanel_main.setColumnHeader(jv2); //

		//临时面板,将快速查询与工具条组装
		JPanel temp_panel_1 = new JPanel(new BorderLayout()); //表格与上面的工具条!!
		temp_panel_1.setOpaque(false); //透明!!!!
		temp_panel_1.add(getToolbarPanel(), BorderLayout.NORTH); //
		temp_panel_1.add(scrollPanel_main, BorderLayout.CENTER); //

		if (LookAndFeel.getBoleanValue("列表分页条是否在下面", false)) { //许多用户都提出分页要放在下面!所以要加个参数,默认是放在上面的!
			if (bo_isShowPageNavigation) { //如果的确有分页条
				JPanel page_downtmp_panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); //
				page_downtmp_panel.setOpaque(false); //透明!
				page_downtmp_panel.setMinimumSize(new Dimension(100, 26)); //
				page_downtmp_panel.add(getPagePanel()); //
				page_downtmp_panel.add(getPageDescLabel()); //
				temp_panel_1.add(page_downtmp_panel, BorderLayout.SOUTH); //分页
			} else { //如果根本就没有分页条,则也不加!!

			}
		}

		unTitlePanel = new JPanel(new BorderLayout()); //查询框与上面的(表格与工具条)
		unTitlePanel.setOpaque(false); //透明!!!!
		unTitlePanel.add(temp_panel_1, BorderLayout.CENTER); //

		centerPanel = new JPanel(new BorderLayout()); //这个面板一定要,因为可能需要重绘等需求!!或者隐藏之!!
		centerPanel.setOpaque(false); //透明!!!!
		centerPanel.add(getTitlePanel(), BorderLayout.NORTH); //

		centerPanel.add(unTitlePanel, BorderLayout.CENTER); //
		this.add(centerPanel, BorderLayout.CENTER); //

		if (templetVO.getIsshowlistquickquery() && !templetVO.getIscollapsequickquery()) { //如果显示并且不是收缩的,则加载之!
			unTitlePanel.add(getQuickQueryPanel(), BorderLayout.NORTH); //
		}

		// 初始化弹出菜单
		rightPopMenu = new JPopupMenu(); //
		menuItem_showuitruedata = new JMenuItem("查看UI中实际数据", UIUtil.getImage("office_086.gif")); //
		menuItem_showdbtruedata = new JMenuItem("查看DB中实际数据", UIUtil.getImage("office_009.gif")); //
		menuItem_showCard = new JMenuItem("卡片浏览", UIUtil.getImage("office_065.gif")); //
		menuItem_dirdelrecord = new JMenuItem("直接删除", UIUtil.getImage("del.gif")); //直接删除
		menuItem_dirupdaterecord = new JMenuItem("直接修改", UIUtil.getImage("office_026.gif")); //直接修改记录字段值
		menuItem_dirdelworkflowinfo = new JMenuItem("清除工作流数据", UIUtil.getImage("office_144.gif")); //直接清空工作流数据

		recordPopMenuAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == menuItem_showuitruedata) {
					showUIRecord();
				} else if (e.getSource() == menuItem_showdbtruedata) {
					showDBRecord();
				} else if (e.getSource() == menuItem_showCard) {
					cardShowInfo();
				} else if (e.getSource() == menuItem_dirdelrecord) {
					dirDeleteRecord();
				} else if (e.getSource() == menuItem_dirupdaterecord) {
					dirUpdateRecord();
				} else if (e.getSource() == menuItem_dirdelworkflowinfo) {
					dirDelWorkFlowInfo();
				}
			}
		};
		menuItem_showuitruedata.addActionListener(recordPopMenuAction); //
		menuItem_showdbtruedata.addActionListener(recordPopMenuAction); //
		menuItem_showCard.addActionListener(recordPopMenuAction); //
		menuItem_dirdelrecord.addActionListener(recordPopMenuAction); //
		menuItem_dirupdaterecord.addActionListener(recordPopMenuAction); //
		menuItem_dirdelworkflowinfo.addActionListener(recordPopMenuAction); //

		rightPopMenu.add(menuItem_showCard);

		if (is_admin) {
			rightPopMenu.add(menuItem_showuitruedata);
			rightPopMenu.add(menuItem_showdbtruedata);
			rightPopMenu.add(menuItem_dirdelrecord);
			rightPopMenu.add(menuItem_dirupdaterecord);
			rightPopMenu.add(menuItem_dirdelworkflowinfo);
		}

		this.setPreferredSize(new Dimension(975, 125)); //

		//打开时直接查询数据前20条数据
		if (templetVO != null && templetVO.getAutoLoads() != 0) {
			autoLoadPartData(); //自动加载数据
		}

		if (combineColumns != null) { //列表合并 【杨科/2013-07-26】 
			setCombineColumns(combineColumns);
		}
	}

	//自动加载部分数据
	private void autoLoadPartData() {
		String str_dsName = getDataSourceName(); //数据源名称
		String str_autoLoadSql = getAutoLoadSQL(); //自动加载时的SQL语句,它与查询时的SQL条件还不一样!!!比如自动加载时只想查状态为未处理的或本人录入的(即最关心的即时数据而非历史数据)!!但查询时可以查询全部!!!
		queryDataByDS(str_dsName, str_autoLoadSql); //查询数据,而且可以取前多少条数据
	}

	/**
	 * 设置是否不透明...
	 * @param _opaque
	 */
	public void setBillListOpaque(boolean _opaque) {
		this.setOpaque(_opaque); //
		if (_opaque) {//不透明
			this.setUI(new WLTPanelUI(BackGroundDrawingUtil.INCLINE_NW_TO_SE));
		} else { //透明
			this.setUI((PanelUI) UIManager.getUI(this));
		}

	}


	public void setBillListTitleName(String _text) {
		getTitlePanel().setVisible(true); //
		label_temname.setText("※" + _text); //
	}

	/**
	 * zzl 修改标题颜色
	 * @param _text
	 */
	public void setBillListTitleName(String _text,int a,int b,int c) {
		getTitlePanel().setVisible(true); //
		label_temname.setText("※" + _text); //
		label_temname.setForeground(new Color(a, b, c)); //106, 106, 255 149, 149, 255
	}


	private AbstractListCustomerButtonBarPanel getListCustPanel() {
		if (listCustPanel != null) {
			return listCustPanel;
		}

		if (this.templetVO.getListcustpanel() == null || this.templetVO.getListcustpanel().trim().equals("")) {
			return null;
		}

		try {
			listCustPanel = (AbstractListCustomerButtonBarPanel) Class.forName(this.templetVO.getListcustpanel()).newInstance();
			listCustPanel.setBillListPanel(this);
			listCustPanel.initialize();
			return listCustPanel;
		} catch (Exception e) {
			MessageBox.showException(this, e);
			return null;
		}
	}

	/**
	 * 
	 * @param _text
	 * @return
	 */
	public WLTButton getBillListBtn(String _code) { //
		WLTButton btn_tmp = getBillListBtnPanel().getButtonByCode(_code); //
		try {
			if (btn_tmp == null) {
				throw new WLTAppException("模板[" + this.str_templetcode + "]中没有定义编码为[" + _code + "]的列表按钮,为了能够成功初始化页面,系统生成了一个不可见的按钮!"); //
			} else {
				return btn_tmp; //
			}
		} catch (Exception e) {
			MessageBox.showException(this, e);
			return new WLTButton("不存在的按钮[" + _code + "]"); //为了防止因null异常而不能显示界面,创建一个临时按钮
		}
	}

	public void setBillListBtnVisiable(String _text, boolean _visiable) {
		getBillListBtnPanel().setBillListBtnVisiable(_text, _visiable);
	}

	public void setAllBillListBtnVisiable(boolean _visiable) {
		getBillListBtnPanel().setAllBillListBtnVisiable(_visiable);
	}

	public JPanel getToolbarPanel() {
		if (toolbarPanel != null) {
			return toolbarPanel;
		}
		//boolean isToolBarPanelDownRight = LookAndFeel.getBoleanValue("列表工具条是否突出", false); //中铁项目因为UI强烈要求与EAC统一!!而中铁的UI中的列表工具条是突出显示的! 即不透明的单独的一条!
		if (isToolBarPanelDownRight) { //如果是工具条单独光亮显示!!!
			toolbarPanel_content = new WLTPanel(WLTPanel.VERTICAL_LIGHT2, new FlowLayout(FlowLayout.LEFT, 1, 1), LookAndFeel.table_toolBarBgcolor, false); //
		} else {
			toolbarPanel_content = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1)); //
			toolbarPanel_content.setOpaque(false); //透明!!!
		}

		toolbarPanel_content.add(getBillListBtnPanel(false)); //先加入按钮!!!没有滚动框
		customerNavigationJPanel.setLayout(new BorderLayout()); //
		customerNavigationJPanel.setOpaque(false); //
		toolbarPanel_content.add(customerNavigationJPanel); //可以添加控件的自由导航面板
		if (getListCustPanel() != null) { //如果有自定义面板!!
			toolbarPanel_content.add(getListCustPanel()); //再见加入模板中定义的按钮
		}

		boolean isPageBtnPanelWrap = templetVO.getIslistpagebarwrap(); //分页条是否另起一行!!
		if (!bo_isShowPageNavigation) { //如果不显示分页,则不管其他条件,永远放在上面的后面!!
			toolbarPanel_content.add(getPageDescLabel()); //再加入分页说明!!!这个永远是要的!!
		} else {
			if (!LookAndFeel.getBoleanValue("列表分页条是否在下面", false) && !isPageBtnPanelWrap) { //如果分页条不是在下面,即在上面,则才加入分页条!! 因为许多用户都提出分页要放在下面!
				toolbarPanel_content.add(getPagePanel()); //再加入分页条!如果指定了不显示分页,则默认是隐藏的!!!!	
				toolbarPanel_content.add(getPageDescLabel()); //再加入分页说明!!!这个永远是要的!!
			}
		}

		WLTScrollPanel scrollPanel = new WLTScrollPanel(toolbarPanel_content);
		scrollPanel.setOpaque(false);
		toolbarPanel = new WLTPanel(new BorderLayout(0, 0)); //
		toolbarPanel.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0)); //
		toolbarPanel.setOpaque(false);
		toolbarPanel.add(scrollPanel, BorderLayout.CENTER); //

		if (!LookAndFeel.getBoleanValue("列表分页条是否在下面", false) && isPageBtnPanelWrap && bo_isShowPageNavigation) { //如果分页条在上面,且另起一行!!
			JPanel pageBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 1)); //
			pageBtn.setOpaque(false); //透明!!
			pageBtn.add(getPagePanel()); //
			pageBtn.add(getPageDescLabel()); //
			toolbarPanel.add(pageBtn, BorderLayout.SOUTH); //
		}
		return toolbarPanel; //
	}

	/**
	 * 设置工具栏的背景颜色..
	 * @param _bg
	 */
	public void setToolBarPanelBackground(Color _bg) {
		if (toolbarPanel != null) {
			toolbarPanel.setBackground(_bg);
			toolbarPanel_content.setBackground(_bg);
		}
		getMainScrollPane().setOpaque(false);
		getMainScrollPane().setBackground(_bg);
		getMainScrollPane().getViewport().setBackground(_bg);
		getMainScrollPane().getRowHeader().setBackground(_bg); //
		table.getTableHeader().setBackground(_bg); //
	}

	/**
	 * 增加工作流面板...
	 */
	public void addWorkFlowDealPanel() {
		wf_btnPanel = new WorkflowDealBtnPanel(this); // 工作流面板按钮
		customerNavigationJPanel.add(wf_btnPanel); //

		customerNavigationJPanel.repaint(); //
		toolbarPanel_content.repaint(); //
	}

	/**
	 * 在按钮栏上增加自定义面板!!
	 * @param _custPanel
	 */
	public void addCustBtnPanel(JPanel _custPanel) {
		customerNavigationJPanel.add(_custPanel); //
		customerNavigationJPanel.repaint(); //
		toolbarPanel_content.repaint(); //
	}

	/**
	 * 取得工作面板
	 * 
	 * @return
	 */
	public WorkflowDealBtnPanel getWorkflowDealBtnPanel() {
		return wf_btnPanel;
	}

	/**
	 * 设置工具栏面板是否显示.,直接调用setVisiable会出现灰屏现象.
	 */
	public void setToolbarVisiable(boolean _visiable) {
		if (_visiable) {
			getToolbarPanel().setPreferredSize(new Dimension((int) getToolbarPanel().getPreferredSize().getWidth(), 22)); //
		} else {
			getToolbarPanel().setPreferredSize(new Dimension((int) getToolbarPanel().getPreferredSize().getWidth(), 0)); //	
		}
	}

	/**
	 * 返回标题面板!!
	 * @return
	 */
	public JPanel getTitlePanel() {
		if (titlePanel != null) {
			return titlePanel;
		}

		titlePanel = new JPanel(new BorderLayout(3, 0)); //
		titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		titlePanel.setOpaque(false); //透明
		titlePanel.add(getTitleLabel(), BorderLayout.CENTER); //		

		//显示/隐藏非常标题栏之外的其他所有面板!在工作流处理需要!!
		if (isHiddenUntitlePanelBtnvVisiable) { //如果定义是显示这个按钮,则显示! 默认是不显示的!!
			btn_shuntitle = new WLTButton(LookAndFeel.treeExpandedIcon); //
			btn_shuntitle.setPreferredSize(new Dimension(16, 16)); //
			btn_shuntitle.setOpaque(false); //
			btn_shuntitle.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
			btn_shuntitle.setToolTipText("隐藏/显示表格数据"); //
			btn_shuntitle.setBorder(BorderFactory.createEmptyBorder()); //
			btn_shuntitle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showOrHiddenUnTitlePanel(); //显示或隐藏快速查询面板!!
				}
			}); //
			titlePanel.add(btn_shuntitle, BorderLayout.WEST); //
		}

		//隐藏/显示快速查询!!!
		icon_showqq = UIUtil.getImage("office_136.gif"); //收缩图标
		//icon_hiddenqq = UIUtil.getImage("office_160.gif"); //展开图标
		icon_hiddenqq = new ImageIcon(getTBUtil().getImageRotate(icon_showqq.getImage(), 180)); //180度!
		btn_shqq = new WLTButton(icon_showqq); //
		btn_shqq.setPreferredSize(new Dimension(16, 16)); //
		btn_shqq.setOpaque(false);
		btn_shqq.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
		btn_shqq.setToolTipText("隐藏/显示查询面板"); //
		btn_shqq.setBorder(BorderFactory.createEmptyBorder()); //
		btn_shqq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showOrHiddenQuickQueryPanel(); //显示或隐藏快速查询面板!!
			}
		}); //
		titlePanel.add(btn_shqq, BorderLayout.EAST); //

		//加入标题!
		if (templetVO.getIsshowlistquickquery()) { //如果指定显示快速查询!!!
			btn_shqq.setVisible(true);
			if (templetVO.getIscollapsequickquery()) { //如果是权收起来的话
				btn_shqq.setIcon(icon_hiddenqq); //
			} else {
				btn_shqq.setIcon(icon_showqq); //
			}
		} else {
			btn_shqq.setVisible(false);
		}

		if (getTitleLabel().getText().equals("") && !btn_shqq.isVisible()) {
			titlePanel.setVisible(false); //
		}

		return titlePanel;
	}

	/**
	 * 返回标题Label!!!
	 * @return
	 */
	public JLabel getTitleLabel() {
		if (label_temname != null) {
			return label_temname;
		}

		label_temname = new JLabel("", JLabel.LEFT); //
		if (templetVO.getTempletname() != null && !templetVO.getTempletname().trim().equals("")) {
			label_temname.setText("※" + templetVO.getTempletname() + ""); //
		}
		label_temname.setFont(new Font("System", Font.PLAIN, 12 + LookAndFeel.getFONT_REVISE_SIZE())); //
		label_temname.setForeground(new Color(149, 149, 255)); //106, 106, 255 149, 149, 255
		label_temname.setOpaque(false);
		return label_temname;
	}

	/**
	 * 设置标题文字!!
	 * @param _text
	 */
	public void setTitleLabelText(String _text) {
		JLabel label = getTitleLabel(); //
		if (label != null) { //如果不为空!!
			if (_text != null && _text.startsWith("※")) { //如果已有
				label.setText(_text); //
			} else {
				label.setText("※" + _text); //
			}
		}
	}

	public void setTableToolTipText(String _text) {
		str_tableToolTip = _text;
	}

	//显示/隐藏除标题栏之外的所有内容! 在工作流处理界面中用到! 即意见太多时需要有个组!可以隐藏意见表
	private void showOrHiddenUnTitlePanel() {
		if (oldDimension == null) {
			oldDimension = this.getPreferredSize(); //
		}
		if (unTitlePanel.isVisible()) {
			unTitlePanel.setVisible(false); //
			this.setPreferredSize(new Dimension((int) oldDimension.getWidth(), 25)); //
			btn_shuntitle.setIcon(LookAndFeel.treeCollapsedIcon); //  
		} else {
			unTitlePanel.setVisible(true); //
			this.setPreferredSize(oldDimension); //
			btn_shuntitle.setIcon(LookAndFeel.treeExpandedIcon); //  
		}
	}

	private void showOrHiddenQuickQueryPanel() {
		//刘旋飞提出如果列表打开时查询面板就是隐藏的。那么点击一次此按钮按说应该显示，但由于quickQueryPanel刚被实例化，且visible=true。所以第一次点击做的操作是把面板隐藏。。【郝明2012-02-29】
		if (quickQueryPanel == null) { //如果默认隐藏查询面板，quickQueryPanel并没有被实例
			setBillQueryPanelVisible(true); //设置展开
			unTitlePanel.validate();
			return;
		}
		BillQueryPanel queryPanel = getQuickQueryPanel(); //
		if (queryPanel.isVisible()) {
			setBillQueryPanelVisible(false); //
		} else {
			setBillQueryPanelVisible(true); //
		}
	}

	/**
	 * 设置查询面板是否显示!
	 * @param _visible
	 */
	public void setBillQueryPanelVisible(boolean _visible) {
		BillQueryPanel queryPanel = getQuickQueryPanel(); //
		if (_visible) {
			queryPanel.setVisible(true);
			btn_shqq.setIcon(icon_showqq);
		} else {
			queryPanel.setVisible(false);
			btn_shqq.setIcon(icon_hiddenqq); //
		}
	}

	public BillButtonPanel getBillListBtnPanel() {
		return getBillListBtnPanel(false); //
	}

	/**
	 * 列表本身可以定义一些快速按钮
	 * 
	 * @return
	 */
	private BillButtonPanel getBillListBtnPanel(boolean _scrollable) {
		if (billListBtnPanel != null) {
			return billListBtnPanel;
		}

		billListBtnPanel = new BillButtonPanel(this.templetVO.getListcustbtns(), this, _scrollable); //
		billListBtnPanel.setOpaque(false);
		billListBtnPanel.paintButton(); //绘制按钮
		return billListBtnPanel;
	}

	/**
	 * 增加一个按钮.加完后必须调用一下repaintBillListButton()重绘一下才能看到效果!!!
	 * @param _btn
	 */
	public void addBillListButton(WLTButton _btn) {
		_btn.setBillPanelFrom(this); //先绑定好自己
		billListBtnPanel.addButton(_btn); //
	}

	/**
	 * 增加一批按钮
	 * @param _btns
	 */
	public void addBatchBillListButton(WLTButton[] _btns) {
		for (int i = 0; i < _btns.length; i++) {
			if (_btns[i] != null) {//南昌银行项目以前的代码发现添加了未定义的按钮，这里报空指针异常，菜单打不开，故判断一下【李春娟/2016-06-24】
				_btns[i].setBillPanelFrom(this); //先绑定好自己
			}
		}
		billListBtnPanel.addBatchButton(_btns);
	}

	/**
	 * 增加一个按钮.
	 * @param _btn
	 */
	public void insertBillListButton(WLTButton _btn) {
		_btn.setBillPanelFrom(this); //先绑定好自己
		billListBtnPanel.insertButton(_btn); //
	}

	/**
	 * 插入一批按钮.
	 * @param _btn
	 */
	public void insertBatchBillListButton(WLTButton[] _btns) {
		for (int i = 0; i < _btns.length; i++) {
			_btns[i].setBillPanelFrom(this); //先绑定好自己
		}
		billListBtnPanel.insertBatchButton(_btns); //
	}

	/**
	 * 重新绘制按钮
	 */
	public void repaintBillListButton() {
		billListBtnPanel.paintButton(); //
	}

	public void addCustButton(JButton _btn) {
		billListBtnPanel.addCustButton(_btn); //
	}

	public void addBillListButtonActinoListener(BillListButtonActinoListener _listener) {
		v_listbtnListener.add(_listener); //
	}

	public void addBillListHtmlHrefListener(BillListHtmlHrefListener _listener) {
		v_listHtmlHrefListener.add(_listener); //
	}

	private void onListHtmlHrefClick(int _row, int _col, boolean _isshiftdown) {
		try {
			this.table.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			TableColumn column = table.getColumnModel().getColumn(_col); //
			String str_itemkey = (String) column.getIdentifier(); //
			if ("按钮".equals(getItemTypeByItemKey(str_itemkey))) { //按钮不处理!因为按钮本身有逻辑!!
				return;
			}
			if (v_listHtmlHrefListener.size() == 0) { //如果没有一个监听者则默认打开卡片样式
				if (getTempletItemVO(str_itemkey).getUCDfVO() != null) {
					String linkedKey = getTempletItemVO(str_itemkey).getUCDfVO().getConfValue("html连接key");
					//sunfujun/20120831/经常有这种需求表单中最重要的是一个附件，在列表上一点击表单名称就打开附件_邮储
					if (linkedKey != null && !"".equals(linkedKey)) {
						if ("文件选择框".equals(getItemTypeByItemKey(linkedKey))) {
							RefDialog_File refdialog_file = new RefDialog_File(this, getItemNameByItemKey(linkedKey), (RefItemVO) getValueAt(_row, linkedKey), this, getTempletItemVO(linkedKey).getUCDfVO());
							refdialog_file.setPubTempletItemVO(getTempletItemVO(linkedKey)); //
							refdialog_file.initialize();
							refdialog_file.setVisible(true);
							return;
						} else if ("Office控件".equals(getItemTypeByItemKey(linkedKey))) {
							String str_serverDir = "/officecompfile";
							CommUCDefineVO commUCDfVO = getTempletItemVO(linkedKey).getUCDfVO();
							if (commUCDfVO != null) {
								str_serverDir = commUCDfVO.getConfValue("存储目录", "/officecompfile"); //
							}
							RefItemVO currRefItemVO = (RefItemVO) getValueAt(_row, linkedKey);
							if (currRefItemVO != null && currRefItemVO.getId() != null && !getTBUtil().isEndWithInArray(currRefItemVO.getId(), new String[] { ".doc", "docx", ".xls", ".wps" }, true)) {
								if (str_serverDir.equals("/officecompfile")) {
									UIUtil.openRemoteServerFile("office", currRefItemVO.getId());
								} else {
									UIUtil.openRemoteServerFile("serverdir", str_serverDir + "/" + currRefItemVO.getId());
								}
							} else {
								BillOfficeDialog bod = new BillOfficeDialog(this, currRefItemVO.getId(), false, false, str_serverDir);
								bod.setVisible(true);
							}
							return;
						}
					}
				}
				cardShowInfo(); //卡片显示
			} else {
				for (int i = 0; i < v_listHtmlHrefListener.size(); i++) {
					BillListHtmlHrefListener listener = (BillListHtmlHrefListener) v_listHtmlHrefListener.get(i); // 超链接监听器
					listener.onBillListHtmlHrefClicked(new BillListHtmlHrefEvent(str_itemkey, _row, _col, _isshiftdown, this)); // 触发方法
				}
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		} finally {
			this.table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
	}

	/**
	 * 设置列表的翻页功能
	 * @return
	 */
	public JPanel getPagePanel() {
		if (pagePanel != null) {
			return pagePanel;
		}
		jComboBox_onePageRecords = new JComboBox();
		jComboBox_onePageRecords.setLightWeightPopupEnabled(false); //
		jComboBox_onePageRecords.setEditable(true); //
		jComboBox_onePageRecords.setOpaque(false); //
		jComboBox_onePageRecords.setFocusable(false); //
		jComboBox_onePageRecords.setForeground(Color.GRAY); //

		jComboBox_onePageRecords.setToolTipText("每页显示的记录数");
		jComboBox_onePageRecords.addItem("10");
		jComboBox_onePageRecords.addItem("20");
		jComboBox_onePageRecords.addItem("50");
		jComboBox_onePageRecords.addItem("100");
		jComboBox_onePageRecords.addItem("200");
		jComboBox_onePageRecords.addItem("500");
		jComboBox_onePageRecords.addItem("1000"); //这个应该是常用的上限!!!
		jComboBox_onePageRecords.addItem("2000");
		jComboBox_onePageRecords.addItem("10000");//以前是全部,但这样会造成数据量太大时系统崩掉!且后台本身的2000限制,所以干脆搞成2000算了!!
		jComboBox_onePageRecords.setSelectedIndex(2); //默认是50
		jComboBox_onePageRecords.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					li_onepagerecords = Integer.parseInt((String) jComboBox_onePageRecords.getSelectedItem()); //【xch/2012-04-27】重置每页数量!兴业等许多客户觉得得立即变化不好!
					//关键是有“傻鸟”上来就选每页2000,结果有性能问题!!即系统要有智能提醒以防止误操作的性能问题!!否则不知道的人上来狂点就会系统死掉!!系统要有这种“保险”设计！
					String str_msg = "每页将显示[" + li_onepagerecords + "]条数据,请重新查询!"; //
					if (li_onepagerecords >= 500) {
						str_msg = str_msg + "\r\n友情提示:每页显示数量太多会导致性能下降, 请慎重操作!";
						MessageBox.show(jComboBox_onePageRecords, str_msg); //
					} else {
						if (ClientEnvironment.getInstance().get("不再提醒-列表每页显示记录数") == null) { //如果指定了不要再提醒!
							if (MessageBox.showOptionDialog(jComboBox_onePageRecords, str_msg, "提示", new String[] { "确 定", "不再提醒" }) == 1) {
								ClientEnvironment.getInstance().put("不再提醒-列表每页显示记录数", "Y"); //
							}
						}
					}
				}
			}
		});

		JTextField cmb_textField = ((JTextField) ((JComponent) jComboBox_onePageRecords.getEditor().getEditorComponent())); ////
		cmb_textField.setEditable(false); //
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //自定义UI中已经去掉border。
			cmb_textField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
		}
		cmb_textField.setForeground(LookAndFeel.inputforecolor_enable); //
		cmb_textField.setBackground(LookAndFeel.inputbgcolor_enable); //

		pagePanel = new JPanel();
		pagePanel.setOpaque(false); //透明
		pagePanel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		pagePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
		pagePanel.setBorder(BorderFactory.createEmptyBorder(2, 1, 0, 0)); //

		boolean isPageBtnISPic = LookAndFeel.getBoleanValue("分页按钮是否是图片", false); //以前民生项目中刘红林死活要所有按钮都是文字的,包括分页按钮!! 但中铁项目中肖主任又死活要求UI统一,不得已只能搞个参数!! 默认还是有字的!!
		boolean isBtnOpaque = true; //
		Border btnBorder = null; //
		if (isPageBtnISPic) { //如果是图片按钮!则使用中铁的图片! 但如果再来一个客户要求分页按钮又不一样,是不是又要另搞一批图片名称的参数??
			firstPageButton = new WLTButton(UIUtil.getImage("zt_page_1.gif")); //UIUtil.getImage("pageFirst.gif")  "首页"
			pageupButton = new WLTButton(UIUtil.getImage("zt_page_2.gif")); //UIUtil.getImage("pageUp2.gif") "上页"
			pagedownButton = new WLTButton(UIUtil.getImage("zt_page_3.gif")); //UIUtil.getImage("pageDown2.gif") "下页"
			lastPageButton = new WLTButton(UIUtil.getImage("zt_page_4.gif")); //UIUtil.getImage("pageLast.gif") "尾页"
			isBtnOpaque = false; //
			btnBorder = BorderFactory.createEmptyBorder(); //空边框!!
		} else { //文字按钮!!
			firstPageButton = new WLTButton("首页"); //
			pageupButton = new WLTButton("上页"); //
			pagedownButton = new WLTButton("下页"); //
			lastPageButton = new WLTButton("尾页"); //
			firstPageButton.setPreferredSize(new Dimension(37, 18));
			pageupButton.setPreferredSize(new Dimension(37, 18));
			pagedownButton.setPreferredSize(new Dimension(37, 18));
			lastPageButton.setPreferredSize(new Dimension(37, 18));
			firstPageButton.setBackground(LookAndFeel.systembgcolor);
			pageupButton.setBackground(LookAndFeel.systembgcolor);
			pagedownButton.setBackground(LookAndFeel.systembgcolor);
			lastPageButton.setBackground(LookAndFeel.systembgcolor);
			isBtnOpaque = true; //
			btnBorder = BorderFactory.createLineBorder(LookAndFeel.compBorderLineColor, 1); //带线的边框!!!
		}
		firstPageButton.setToolTipText("第一页"); //
		pageupButton.setToolTipText("上一页"); //
		pagedownButton.setToolTipText("下一页"); //
		lastPageButton.setToolTipText("最后一页"); //

		firstPageButton.setOpaque(isBtnOpaque); //必须是透明!!
		pageupButton.setOpaque(isBtnOpaque); //
		pagedownButton.setOpaque(isBtnOpaque); //
		lastPageButton.setOpaque(isBtnOpaque); //

		firstPageButton.setBorder(btnBorder); //
		pageupButton.setBorder(btnBorder); //
		pagedownButton.setBorder(btnBorder); //
		lastPageButton.setBorder(btnBorder); //

		Font btnFont = new Font("宋体", Font.PLAIN, 12); //
		firstPageButton.setFont(btnFont); //
		pageupButton.setFont(btnFont); //
		pagedownButton.setFont(btnFont); //
		lastPageButton.setFont(btnFont); //

		goToPageTextField = new JTextField("1");
		goToPageTextField.setForeground(Color.GRAY); //
		goToPageTextField.setHorizontalAlignment(SwingConstants.CENTER);

		goToPageButton = new WLTButton("转"); //UIUtil.getImage("gotopage.gif")
		goToPageButton.setToolTipText("直接跳转到某一页"); //
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //自定义UI中已经去掉border。
			goToPageTextField.setBorder(BorderFactory.createLineBorder(LookAndFeel.compBorderLineColor, 1));
			goToPageButton.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
		}
		goToPageButton.setFont(LookAndFeel.font); //

		goToPageButton.setBackground(LookAndFeel.systembgcolor);
		//		goToPageButton.setForeground(Color.GRAY); //

		JPanel panel_jump = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); //
		panel_jump.setOpaque(false);
		panel_jump.add(goToPageTextField); //
		panel_jump.add(goToPageButton); //

		jComboBox_onePageRecords.setPreferredSize(new Dimension(60, 18));
		goToPageTextField.setPreferredSize(new Dimension(18, 18)); //

		goToPageButton.setPreferredSize(new Dimension(25, 18));
		ActionListener pageButtonAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					BillListPanel.this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
					if (e.getSource() == firstPageButton) {
						onGoToFirstPage(); // 跳到首页
					} else if (e.getSource() == pageupButton) {
						onGoToPreviousPage(); //前一页
					} else if (e.getSource() == pagedownButton) {
						onGoToNextPage(); //后一页
					} else if (e.getSource() == lastPageButton) {
						onGoToLastPage(); //最后一页
					} else if (e.getSource() == goToPageButton) {
						onGoToPage(); //最后一页
					}
				} catch (Exception ex) {
					MessageBox.showException(BillListPanel.this, ex);
				} finally {
					BillListPanel.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
				}
			}
		};

		firstPageButton.addActionListener(pageButtonAction); //
		pageupButton.addActionListener(pageButtonAction); //
		pagedownButton.addActionListener(pageButtonAction); //
		lastPageButton.addActionListener(pageButtonAction); //
		goToPageButton.addActionListener(pageButtonAction); //

		pagePanel.add(firstPageButton);
		pagePanel.add(pageupButton);
		pagePanel.add(pagedownButton);
		pagePanel.add(lastPageButton);
		pagePanel.add(jComboBox_onePageRecords);
		pagePanel.add(panel_jump);

		if (!bo_isShowPageNavigation) { //如果不指定分页
			pagePanel.setVisible(false); // 分页
		}
		return pagePanel;
	}

	//太平项目升级平台添加
	public JComboBox getjComboBox_onePageRecords() {
		return jComboBox_onePageRecords;
	}

	//太平项目升级平台添加
	public void setjComboBox_onePageRecords(JComboBox jComboBox_onePageRecords) {
		this.jComboBox_onePageRecords = jComboBox_onePageRecords;
	}

	public JLabel getPageDescLabel() {
		if (label_pagedesc == null) { //唯一一个显示排序的Label。[郝明2012-08-03]
			label_pagedesc = new JLabel("", SwingConstants.LEFT); //
			label_pagedesc.setForeground(new Color(69, 69, 69)); //
			label_pagedesc.setMinimumSize(new Dimension(0, 22));
		}
		return label_pagedesc;
	}

	/**
	 * 设置所有Item中的某一项为某值
	 * 
	 * @param _itemkey
	 * @param _obj
	 */
	public void setAllItemValue(String _itemkey, Object _obj) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			templetItemVOs[i].setAttributeValue(_itemkey, _obj);
		}
	}

	// 设置是否可以编辑!!
	public void setItemEditable(String _itemkey, boolean _ifedit) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase(_itemkey)) {
				if (_ifedit) {
					templetItemVOs[i].setAttributeValue("listiseditable", "1"); //
				} else {
					templetItemVOs[i].setAttributeValue("listiseditable", "4"); //
				}
				break;
			}
		}
	}

	// 设置是否可以编辑!!
	public void setItemEditable(boolean _ifedit) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (_ifedit) {
				templetItemVOs[i].setAttributeValue("listiseditable", "1"); //
			} else {
				templetItemVOs[i].setAttributeValue("listiseditable", "4"); //
			}
		}
	}

	// 设置是否可以编辑!!
	public void setItemEditableByInit() {
		// 设置各控件!!!
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			templetItemVOs[i].setAttributeValue("listiseditable", hm_initeditable.get(templetItemVOs[i].getItemkey())); //
		} //
	}

	/**
	 * 设置选择模式
	 * <ul>
	 * <li> <code>ListSelectionModel.SINGLE_SELECTION</code> 只能选一行
	 * <li> <code>ListSelectionModel.SINGLE_INTERVAL_SELECTION</code> 可以选一段
	 * <li> <code>ListSelectionModel.MULTIPLE_INTERVAL_SELECTION</code> 可以选多行
	 * </ul>
	 * 
	 */
	public void setSelectionMode(int _model) {
		getTable().getSelectionModel().setSelectionMode(_model);
	}

	public JTable getTable() {
		if (table == null) {
			table = new HtmlHrefTable(getTableModel(), getColumnModel()); // 创建表格
			table.setRowHeight(li_rowHeight + LookAndFeel.getFONT_REVISE_SIZE()); //
			table.setGridColor(LookAndFeel.tableHeadLineClolr); //网格线的颜色!!
			table.setIntercellSpacing(new Dimension(1, 1)); //
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						selectedChanged(); // 行选择变化
					}
				}
			}); //

			new TableAdapter(table);
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) { // 如果是左键
						if (e.getClickCount() == 2) {
							mouseDoubleClicked(e); //
						}
					} else if (e.getButton() == MouseEvent.BUTTON3) {
						showPopMenu(e, is_admin);
					}
				}

				//鼠标选择完松开时触发，此事件可含括所有选择情况，包括按Ctrl键。
				public void mouseReleased(MouseEvent e) {
					selectedInfo();//单击列表单元格时，下方状态栏显示信息[YangQing/2013-09-25]
				}
			});

			table.getTableHeader().setFont(LookAndFeel.font_b); //粗体
			table.getTableHeader().setOpaque(false);
			if (templetVO.getListheaderisgroup().booleanValue()) { // 列表的表头是否支持分组
				if (isGroupHeader()) { // 如果需要分组,则进行表头合并..
					groupTableHeader(table, getGroupTitles(), this.templetVO); // 如果需要合并表头,则做合并表头处理
				} else {
					table.getTableHeader().setDefaultRenderer(new SortHeaderRenderer(this.templetVO, this.is_admin)); //
					table.getTableHeader().setPreferredSize(new Dimension(20000, templetVO.getListheadheight())); //
				}
			} else {
				table.getTableHeader().setDefaultRenderer(new SortHeaderRenderer(this.templetVO, this.is_admin)); //
				table.getTableHeader().setPreferredSize(new Dimension(20000, templetVO.getListheadheight())); //
			}

			table.getTableHeader().addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent evt) {
					if (evt.getClickCount() == 2) { //
						if (isHeaderCanSort) { //有的表格不允许双击排序,否则会把数据弄乱了!
							sortByColumn(evt.getPoint());
						}
					} else if (evt.getButton() == MouseEvent.BUTTON3) { //如果是右键!
						if (showPopMenu) { //有的地方的表格不需要弹出功能!比如报表
							showPopMenu(evt.getComponent(), evt.getX(), evt.getY()); // 弹出菜单
						}

					}
				}
			});
		}
		return table;
	}

	private boolean isGroupHeader() {
		if (templetVO != null) {
			for (int i = 0; i < templetItemVOs.length; i++) {
				if (templetItemVOs[i].isListisshowable().booleanValue() && templetItemVOs[i].getGrouptitle() != null && !templetItemVOs[i].getGrouptitle().trim().equals("")) {
					return true;
				}
			}
		}
		return false;
	}

	private String[][] getGroupTitles() {
		Vector v_return = new Vector();
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].isListisshowable().booleanValue() && templetItemVOs[i].getGrouptitle() != null && !templetItemVOs[i].getGrouptitle().trim().equals("")) {
				v_return.add(new String[] { templetItemVOs[i].getItemkey(), templetItemVOs[i].getGrouptitle() }); // itemkey,grouptltle
			}
		}

		String[][] str_data = new String[v_return.size()][2];
		for (int i = 0; i < str_data.length; i++) {
			str_data[i] = (String[]) v_return.get(i);
		}
		return str_data;
	}

	/**
	 * 重新设置表头,将说明中的内容替换成实际值...
	 */
	public void reGroupHeader(Hashtable _ht) {
		try {
			if (isGroupHeader()) {
				BillListGroupableTableHeader header = (BillListGroupableTableHeader) table.getTableHeader(); //
				Hashtable ht_colconvert = header.getColValueConverter(); //
				String[] str_keys = (String[]) ht_colconvert.keySet().toArray(new String[0]); // 得到所有key数组
				for (int i = 0; i < str_keys.length; i++) {
					String str_value = str_keys[i]; //
					String[] str_items = getTBUtil().getFormulaMacPars(str_value); //
					for (int j = 0; j < str_items.length; j++) {
						str_value = getTBUtil().replaceAll(str_value, "{" + str_items[j] + "}", "" + _ht.get(str_items[j]));
					}
					ht_colconvert.put(str_keys[i], str_value); //
				}
				header.repaint(); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		}
	}

	private void groupTableHeader(JTable _table, String[][] _data, Pub_Templet_1VO _Templet_1VO) {
		TableColumnModel cm = _table.getColumnModel(); // 得到表的 ColumnModel 对象
		BillListGroupableTableHeader header = new BillListGroupableTableHeader(cm, _Templet_1VO); // 创建表头对象
		for (int i = 0; i < _data.length; i++) { //
			if (!_data[i][1].equals("")) {
				String[] str_items = getTBUtil().split(_data[i][1], "."); // 对
				for (int j = str_items.length - 1; j >= 0; j--) {
					String str_groupname = str_items[j]; //
					if (j == str_items.length - 1) // 如果是最后一位,则加入该列
					{
						getColumnGroup(str_groupname).add(getTableColumn(_data[i][0])); // 加入列
					} else {
						if (str_items.length > 1) {
							if (!getColumnGroup(str_groupname).containsGroup(getColumnGroup(str_items[j + 1]))) { // 如果不包括下一个组则加入下一组,即比如第2组已包括第三组了
								getColumnGroup(str_groupname).add(getColumnGroup(str_items[j + 1])); //
							}
						}
					}

					if (j == 0) { // 如果是第一位!!
						if (!header.containsGroup(getColumnGroup(str_groupname))) { // 如果不包括该组,则加入该组!!
							header.addColumnGroup(getColumnGroup(str_groupname)); //
						}
					}
				}
			}
		}
		_table.setTableHeader(header); // 设置表头对象
	}

	private BillListGroupColumn getColumnGroup(String _groupName) {
		if (ht_groupcolumn.containsKey(_groupName)) {
			return (BillListGroupColumn) ht_groupcolumn.get(_groupName); //
		} else {
			BillListGroupColumn cg = new BillListGroupColumn(_groupName); //
			ht_groupcolumn.put(_groupName, cg); //
			return cg; //
		}
	}

	private void selectedChanged() {
		int li_row = this.getSelectedRow();
		if (li_row < 0) {
			return;
		}
		BillVO billVO = getBillVO(li_row); //
		for (int i = 0; i < v_selectedListeners.size(); i++) {
			BillListSelectListener listener = (BillListSelectListener) v_selectedListeners.get(i);
			listener.onBillListSelectChanged(new BillListSelectionEvent(li_row, billVO, this)); //
		}
	}

	/**
	 * 单击列表单元格时，下方状态栏显示信息方法
	 * @author YangQing/2013-09-25
	 */
	private void selectedInfo() {
		int[] sel_row = this.getTable().getSelectedRows();//得到所选中的行号
		int[] sel_column = this.getTable().getSelectedColumns();//得到所选中的列号
		String[][] selectCelValue = new String[sel_row.length][sel_column.length];//存放选中单元格的值
		for (int i = 0; i < sel_row.length; i++) {
			for (int y = 0; y < sel_column.length; y++) {
				Object o_value = this.getTable().getValueAt(sel_row[i], sel_column[y]);
				String value = o_value == null ? "" : o_value.toString();
				selectCelValue[i][y] = value;
			}
		}

		Pattern pattern = Pattern.compile("^\\+?\\d*\\.?\\d+$|^-?\\d*\\.?\\d+$");//匹配正负整数、小数
		String celinfo = "";//底下状态栏要显示的字符串
		double sum = 0;//数值总和
		int numvalue_count = 0;//数值个数
		boolean allPersent = true; //默认
		for (int i = 0; i < selectCelValue.length; i++) {
			for (int y = 0; y < selectCelValue[i].length; y++) {
				String str_selValue = selectCelValue[i][y];
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
		}
		if (numvalue_count <= 1) { //如果数字一个以下 郝明20130926
			celinfo = "◆ 计数：" + (sel_row.length * sel_column.length) + " ◆";
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
			celinfo = "◆ 计数：" + (sel_row.length * sel_column.length) + "  数值个数：" + (numvalue_count) + "  求和：" + d_f.format(lastSum) + (allPersent ? "%" : "") + "  平均值" + (ydy ? "≈" : ":") + avgvalue + (allPersent ? "%" : "") + " ◆";
		}
		DeskTopPanel.setSelectInfo(celinfo);//显示
	}

	/**
	 * 弹出菜单
	 * 
	 * @param _compent
	 * @param _x
	 * @param _y
	 */
	private void showPopMenu(Component _compent, final int _x, final int _y) {
		if (popmenu_header != null) { // 如果已经创建弹出菜单.
			if (this.bo_tableislockcolumn) { //如果已锁定了
				item_column_lock.setText("解锁"); //
				item_column_lock.setIcon(UIUtil.getImage("office_082.gif")); //
			} else {
				item_column_lock.setText("锁定"); //
				item_column_lock.setIcon(UIUtil.getImage("office_166.gif")); //
			}
			li_mouse_x = _x;
			li_mouse_y = _y;
			popmenu_header.show(_compent, _x, _y); //
			return; //直接返回
		}
		popmenu_header = new JPopupMenu();

		int li_menuwidth = 138; //
		ArrayList al_menus = new ArrayList(); //之所以搞个ArrayList加入,是因为菜单太多了,后面加入监听事件都要一行行的写,不能循环加入!
		//菜单清单1
		item_column_lock = createPopMenuItem(al_menus, "锁定", "office_166.gif", false, li_menuwidth); // 锁定,使用一个方法包一下,为了让设置大小,UI,颜色,等只要写一遍!!!
		item_column_search = createPopMenuItem(al_menus, "结果中查找", "office_115.gif", false, li_menuwidth); //过滤/定位
		item_table_showhidecolumn = createPopMenuItem(al_menus, "隐藏/显示列", "office_123.gif", false, li_menuwidth); //
		item_table_resetOrderCons = createPopMenuItem(al_menus, "设置排序条件", "office_173.gif", false, li_menuwidth); //
		item_column_quickputvalue = createPopMenuItem(al_menus, "快速赋值", "office_096.gif", false, li_menuwidth); // 快速赋值
		item_column_quickstrike = createPopMenuItem(al_menus, "快速穿透查询", "office_163.gif", false, li_menuwidth); //快速穿透查询
		item_column_quicksavecurrwidth = createPopMenuItem(al_menus, "保存宽度与顺序", "office_076.gif", false, li_menuwidth); // 保存宽茺与顺序
		item_column_quicksaverowheight = createPopMenuItem(al_menus, "保存行高", "office_125.gif", false, li_menuwidth); //保存行高
		item_table_setLineVisiable = createPopMenuItem(al_menus, "表格线隐藏/显示", "office_070.gif", false, li_menuwidth); //以前这搞了个图片特别大,结果使得其他图片与字之间总有个间距,滚动时还抖动一下!找了好久才知道是图片太大的原因!!
		item_column_weidusearch = createPopMenuItem(al_menus, "多维展示", "office_104.gif", false, li_menuwidth); //

		menu_excel = (JMenu) createPopMenuItem(al_menus, "导出Excel/Html", "office_170.gif", true, li_menuwidth); //
		menu_table_exportprint = createPopMenuItem(al_menus, "导出Excel", null, false, 80); //
		menu_table_exporthtml = createPopMenuItem(al_menus, "导出Html", null, false, 80);
		menu_excel.add(menu_table_exportprint); //
		menu_excel.add(menu_table_exporthtml);
		item_column_piechart = createPopMenuItem(al_menus, "饼图", "office_006.gif", false, li_menuwidth); // 饼图
		item_column_lookhelp = createPopMenuItem(al_menus, "查看帮助", "office_037.gif", false, li_menuwidth); // 帮助

		menu_table_templetmodify = (JMenu) createPopMenuItem(al_menus, "快速模板编辑", "office_127.gif", true, li_menuwidth); //是个光盘图标,让人印象深刻!!
		item_table_templetmodify_1 = createPopMenuItem(al_menus, "1.编辑选中列", null, false, 100); ////
		item_table_templetmodify_2 = createPopMenuItem(al_menus, "2.编辑整个模板", null, false, 100); ////
		menu_table_templetmodify.add(item_table_templetmodify_1); //
		menu_table_templetmodify.add(item_table_templetmodify_2); //
		menu_db = (JMenu) createPopMenuItem(al_menus, "物理数据处理", "office_147.gif", true, li_menuwidth);
		menu_db_addflow4_database = createPopMenuItem(al_menus, "在数据库中增加工作流4个字段", null, false, 180);
		menu_db_addflow4_templet = createPopMenuItem(al_menus, "在模板中增加工作流4个字段", null, false, 180);
		menu_db_addflow13_templet = createPopMenuItem(al_menus, "在模板中增加流程监控13个字段", null, false, 180); //为了在待办任务,已办任务,我的流程中取得流程监控信息,需要各不相同的12个字段!
		menu_transportToDataSource = createPopMenuItem(al_menus, "导出模版到数据源", null, false, 180);
		menu_db_autoBuildData = createPopMenuItem(al_menus, "自动插入Demo数据", null, false, 180);
		menu_db.add(menu_db_addflow4_database);
		menu_db.add(menu_db_addflow4_templet);
		menu_db.add(menu_db_addflow13_templet); //12个流程监控字段!
		menu_db.add(menu_transportToDataSource);
		menu_db.add(menu_db_autoBuildData);
		item_table_showsql = createPopMenuItem(al_menus, "查看配置与监控", "office_162.gif", false, li_menuwidth); //
		item_state = createPopMenuItem(al_menus, "查看子项状态", "office_011.gif", false, li_menuwidth);
		//加入控件!
		popmenu_header.add(item_column_search); //过滤
		popmenu_header.add(item_column_lock); //锁定
		popmenu_header.add(item_table_showhidecolumn); //隐藏/显示列
		popmenu_header.add(item_table_resetOrderCons); //重置排序条件!!
		popmenu_header.add(item_column_piechart); //某一列数据生成饼图
		if (is_admin) {
			popmenu_header.add(item_column_quickputvalue); //快速赋值!!
		}
		popmenu_header.add(item_column_lookhelp); //查看某一列帮助说明!!
		popmenu_header.addSeparator(); // 分隔条!!!该分隔条上面的是针对某一列的,下面的是针对整个表格的!!!
		if (is_admin) {
			popmenu_header.add(item_column_quickstrike); //快速穿透
			popmenu_header.add(item_column_quicksaverowheight); // 保存行高....
			popmenu_header.add(item_column_quicksavecurrwidth); // 保存列宽与顺序!!直接修改了数据库的模块，故只能管理员修改【李春娟/2016-04-26】
		}
		popmenu_header.add(item_column_weidusearch); //多维展示,孙富君根据我的要求做的功能!!
		popmenu_header.add(item_table_setLineVisiable); //表格线是否显示与隐藏..

		if (is_admin) { //管理员才加入
			popmenu_header.add(menu_excel); //
			popmenu_header.add(menu_table_templetmodify); // 快速模板编辑
			popmenu_header.add(menu_db); // 快速数据库存处理
			popmenu_header.add(item_state);//查看子项状态即以前用五角星和加减号来表示的意思，统统搞到这里来看，红色保留
		} else if (getTBUtil().getSysOptionBooleanValue("列表是否可以导出", true)) {//机场财务要求不允许导出，故增加该参数【李春娟/2014-01-13】
			popmenu_header.add(menu_excel); //
		}
		popmenu_header.add(item_table_showsql); // 快速模板编辑
		if (this.bo_tableislockcolumn) { //如果已锁定了
			item_column_lock.setText("解锁"); //
			item_column_lock.setIcon(UIUtil.getImage("office_082.gif")); //
		} else {
			item_column_lock.setText("锁定"); //
			item_column_lock.setIcon(UIUtil.getImage("office_166.gif")); //
		}

		headerPopMenuAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == item_column_lock) {
					if (item_column_lock.getText().equals("锁定")) {
						lockColumn();
					} else if (item_column_lock.getText().equals("解锁")) {
						unlockColumn();
					}
				} else if (e.getSource() == item_column_quickputvalue) {
					quickPutValue();
				} else if (e.getSource() == item_column_quickstrike) { //快速穿透查询!!!
					quickStrikeQuery();
				} else if (e.getSource() == item_column_quicksavecurrwidth) {
					quickSaveCurrWidth();
				} else if (e.getSource() == item_column_quicksaverowheight) {
					quickSaveCurrRowHeight();
				} else if (e.getSource() == item_table_setLineVisiable) {
					setTableGridLineVisiable(); //
				} else if (e.getSource() == item_column_weidusearch) {
					weiduSearch(); //
				} else if (e.getSource() == menu_table_exportprint) {
					exportToExcel();
				} else if (e.getSource() == menu_table_exporthtml) {
					exportToHtml();
				} else if (e.getSource() == item_column_piechart) {
					showPieChart();
				} else if (e.getSource() == item_column_lookhelp) {
					onLookHelp();
				} else if (e.getSource() == item_column_search) {
					quickSearch();
				} else if (e.getSource() == item_table_showhidecolumn) {
					reShowHideTableColumn();
				} else if (e.getSource() == item_table_resetOrderCons) {
					onResetOrderCons();
				} else if (e.getSource() == item_table_templetmodify_1) {
					modifyTemplet();
				} else if (e.getSource() == item_table_templetmodify_2) {
					modifyTemplet2(templetVO.getTempletcode());
				} else if (e.getSource() == menu_db_addflow4_database) {
					menu_db_flow_modifyTemplet_database(_x, _y);
				} else if (e.getSource() == menu_db_addflow4_templet) { //在模板中增加工作流的4个字段!!
					menu_db_flow_modifyTemplet(_x, _y);
				} else if (e.getSource() == menu_db_addflow13_templet) { //在模板中增加流程监控的12个字段!
					menu_db_addflow13Field_templet(_x, _y); //增加12个关键字段!
				} else if (e.getSource() == item_table_showsql) {
					showSQL();
				} else if (e.getSource() == item_state) {
					showItemState();
				} else if (e.getSource() == menu_transportToDataSource) {
					menu_transportToDataSource();
				} else if (e.getSource() == menu_db_autoBuildData) { //自动插入demo数据
					menu_db_AutoBuildData();
				}
			}

		};

		//循环加入监听事件!!!
		for (int i = 0; i < al_menus.size(); i++) {
			JMenuItem itemMenu = (JMenuItem) al_menus.get(i); //
			if (!(itemMenu instanceof JMenu)) { //如果不是目录!!
				itemMenu.addActionListener(headerPopMenuAction); //
			}
		}
		li_mouse_x = _x;
		li_mouse_y = _y;
		popmenu_header.show(_compent, _x, _y); //
	}

	//创建菜单!!!将一些方法只要写一次!
	private JMenuItem createPopMenuItem(ArrayList _list, String _text, String _iconImg, boolean _isMenu, int _width) {
		JMenuItem _menuItem = null; //
		if (_isMenu) {
			_menuItem = new JMenu(_text); //
			((JMenu) _menuItem).setUI(new WLTMenuUI());
		} else {
			_menuItem = new JMenuItem(_text); //
			_menuItem.setUI(new WLTMenuItemUI());
		}
		_menuItem.setOpaque(true); //不透明!!
		_menuItem.setBackground(LookAndFeel.defaultShadeColor1); //
		_menuItem.setPreferredSize(new Dimension(_width, 20)); //设置大小
		_menuItem.setToolTipText(_menuItem.getText()); //
		if (_iconImg != null) {
			_menuItem.setIcon(UIUtil.getImage(_iconImg)); // 
		}
		_list.add(_menuItem); //
		return _menuItem; //
	}

	private void showPopMenu2(Component _compent, int _x, int _y) {
		JPopupMenu popmenu_header2 = new JPopupMenu();
		JMenuItem item_column_unlock = new JMenuItem("解锁", UIUtil.getImage("office_082.gif")); // 解锁
		item_column_unlock.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				unlockColumn();
			}
		});
		popmenu_header2.add(item_column_unlock); //
		popmenu_header2.show(_compent, _x, _y); //
	}

	private void weiduSearch() {
		WLTActionEvent ee = new WLTActionEvent((Object) item_column_weidusearch, this);
		try {
			new WeiduSearchButton().actionPerformed(ee);
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}

	public void hidePopMenu() {
		this.showPopMenu = false;
	}

	public Object getValueAtTable(int _row, int _column) {
		return getTable().getValueAt(_row, _column); //
	}

	public Object getValueAt(int _row, int _column) {
		return getTableModel().getValueAt(_row, _column); //
	}

	public Object getValueAt(int _row, String _key) {
		return getTableModel().getValueAt(_row, _key);
	}

	public Object[] getValueAtRow(int _row) {
		return getTableModel().getValueAtRow(_row);
	}

	public HashMap getValueAtRowWithHashMap(int _row) {
		return getTableModel().getValueAtRowWithHashMap(_row);
	}

	public VectorMap getValueAtRowWithVectorMap(int _row) {
		return getTableModel().getValueAtRowWithVectorMap(_row);
	}

	public VectorMap getValueAtModelWithVectorMap(int _row) {
		return getTableModel().getValueAtModelWithVectorMap(_row);
	}

	public VectorMap getSavedValueAtModelWithVectorMap(int _row) {
		return getTableModel().getSavedValueAtModelWithVectorMap(_row);
	}

	public Object[][] getValueAtAll() {
		return getTableModel().getValueAtAll();
	}

	public String getViewValueAtData(int _row, int _col) {
		Object obj = all_realValueData[_row][_col];
		return getObjectViewValue(obj);
	}

	public String getViewValueAtModel(int _row, int _col) {
		Object obj = getValueAtTable(_row, _col);
		return getObjectViewValue(obj);
	}

	public String getRealValueAtTable(int _row, int _column) {
		Object obj = getValueAtTable(_row, _column);
		return getObjectRealValue(obj);
	}

	public String getRealValueAtModel(int _row, int _column) {
		return getTableModel().getRealValueAtModel(_row, _column);
	}

	public String getRealValueAtModel(int _row, String _key) {
		return getTableModel().getRealValueAtModel(_row, _key);
	}

	public String[][] getRealValueAtModel() {
		return getTableModel().getRealValueAtModel();
	}

	public void setValueAt(Object _obj, int _row, int _column) {
		this.getTableModel().setValueAt(_obj, _row, _column);
	}

	public void setValueAt(Object _obj, int _row, String _key) {
		this.getTableModel().setValueAt(_obj, _row, _key); //
	}

	public void setRealValueAt(String _value, int _row, String _key) {
		this.getTableModel().setRealValueAt(_value, _row, _key); //
	}

	//设置某一项的前景颜色
	public void setItemForeGroundColor(String _foreGround, int _row, String _itemKey) {
		BillItemVO billItemVO = (BillItemVO) this.getTableModel().getValueAt(_row, _itemKey); //
		if (billItemVO != null) {
			billItemVO.setForeGroundColor(_foreGround); //
		}
	}

	/**
	 * 设置列表背景颜色【李春娟/2014-11-13】
	 * @param _backGround
	 * @param _row
	 */
	public void setItemBackGroundColor(String _backGround, int _row, String _itemKey) {
		BillItemVO billItemVO = (BillItemVO) this.getTableModel().getValueAt(_row, _itemKey); //
		if (billItemVO != null) {
			billItemVO.setBackGroundColor(_backGround); //
		}
	}

	public void setItemBackGroundColor(Color _backGround, int _row) {
		getTable().putClientProperty("$rowbackground_" + _row, _backGround); //
		if (rowHeaderTable != null) { //行头也设下
			rowHeaderTable.putClientProperty("$rowbackground_" + _row, _backGround); //
		}
	}

	public void clearItemBackGroundColor() {
		for (int i = 0; i < 200; i++) {
			getTable().putClientProperty("$rowbackground_" + i, null); //
			if (rowHeaderTable != null) { //行头也设下
				rowHeaderTable.putClientProperty("$rowbackground_" + i, null); //
			}
		}
	}

	public void setBillVOAt(int _row, BillVO _billVO) {
		setBillVOAt(_row, _billVO, true); //
	}

	public void setBillVOAt(int _row, BillVO _billVO, boolean _isExecEditFormula) {
		if (!_isExecEditFormula) {
			bo_ifProgramIsEditing = true;
		}
		if (_billVO == null) {
			return;
		}
		String[] str_keys = _billVO.getKeys();
		for (int i = 0; i < str_keys.length; i++) {
			setValueAt(_billVO.getObject(str_keys[i]), _row, str_keys[i]);
		}
		if (!_isExecEditFormula) {
			bo_ifProgramIsEditing = false;
		}
	}

	// 清空某一行数据!!
	public void reset(int _row) {
		String[] str_keys = this.getTempletVO().getItemKeys();
		for (int i = 0; i < str_keys.length; i++) {
			setValueAt(null, _row, str_keys[i]);
		}
	}

	/**
	 * 在末尾加入一个空行
	 * 
	 * @return
	 */
	public int addEmptyRow() {
		return addEmptyRow(true);
	}

	public int addEmptyRow(boolean _autoSel) {
		return addEmptyRow(_autoSel, false); //
	}

	/**
	 * 在末尾加入一个空行
	 * 
	 * @return
	 */
	public int addEmptyRow(boolean _autoSel, boolean _isAddSelect) {
		int li_row = this.getTableModel().addEmptyRow(); //
		if (_autoSel) {
			if (_isAddSelect) {
				this.addSelectedRow(li_row); //
			} else {
				this.setSelectedRow(li_row); //
			}
		}
		return li_row; //
	}

	public int addRowWithDefaultValue() {
		int li_newrow = this.getTableModel().addEmptyRow(); //
		this.setSelectedRow(li_newrow); //
		VectorMap defaultvalue = getDefaultValue(li_newrow);
		Object[][] value = defaultvalue.getAllData();
		for (int i = 0; i < value.length; i++) {
			this.setRealValueAt("" + value[i][1], li_newrow, (String) value[i][0]);
		}
		return li_newrow;
	}

	public boolean moveUpRow() {
		stopEditing();
		int[] li_currRows = getTable().getSelectedRows();
		int sign_stop = -1;//stop sign of move up
		for (int i = 0; i < li_currRows.length; i++) {
			int li_currRow = li_currRows[i];
			if (li_currRow < 0) {
				return false;
			}
			if (li_currRow > 0 && (li_currRow - 1) != sign_stop) {
				bo_ifProgramIsEditing = true;
				getTableModel().moveRow(li_currRow, li_currRow, li_currRow - 1);
				getTable().removeRowSelectionInterval(li_currRow, li_currRow);
				getTable().addRowSelectionInterval(li_currRow - 1, li_currRow - 1);
				bo_ifProgramIsEditing = false;
				sign_stop = li_currRow - 1;
			} else {
				sign_stop = li_currRow;
			}
		}
		return true;
	}

	public boolean moveDownRow() {
		stopEditing();

		int[] li_currRows = getTable().getSelectedRows();
		int sign_stop = getTableModel().getRowCount();//stop sign of move up
		for (int i = li_currRows.length - 1; i >= 0; i--) {
			int li_currRow = li_currRows[i];
			if (li_currRow < 0) {
				return false;
			}
			if (li_currRow >= 0 && (li_currRow + 1) != sign_stop) {
				bo_ifProgramIsEditing = true;
				getTableModel().moveRow(li_currRow, li_currRow, li_currRow + 1);
				getTable().removeRowSelectionInterval(li_currRow, li_currRow);
				getTable().addRowSelectionInterval(li_currRow + 1, li_currRow + 1);
				bo_ifProgramIsEditing = false;
				sign_stop = li_currRow + 1;
			} else {
				sign_stop = li_currRow;
			}
		}
		return true;
	}

	public int newRow() {
		return newRow(true); //
	}

	public int newRow(boolean _isAutoSelect) {
		return newRow(_isAutoSelect, true); //
	}

	public int newRow(boolean _isAutoSelect, boolean _isExecDefaultFormula) {
		stopEditing();
		int li_currrow = getTable().getSelectedRow(); // 取得当前行
		int li_newrow = -1;
		if (li_currrow < 0) { // 如果没有选中行,则在末尾加
			li_newrow = insertEmptyRow(0); // 在第一行末尾加入
		} else {
			if (li_currrow == getTable().getRowCount() - 1) {
				li_newrow = addEmptyRow(_isAutoSelect); // 在末尾加入
			} else {
				li_newrow = insertEmptyRow(li_currrow + 1); // 插入
			}
		}

		if (li_newrow >= 0) {
			setValueAt(new RowNumberItemVO("INSERT", li_newrow), li_newrow, this.str_rownumberMark); //
			if (templetVO.getPksequencename() != null) {
				try {
					String str_newid = UIUtil.getSequenceNextValByDS(null, templetVO.getPksequencename());
					String str_pkfieldname = templetVO.getPkname();
					if (str_pkfieldname != null) {
						setValueAt(new StringItemVO(str_newid), li_newrow, str_pkfieldname); // 设置主键值
					}
				} catch (Exception e) {
					MessageBox.showException(this, e);
				}
			}
			if (_isExecDefaultFormula) { // 设置默认值..
				VectorMap defaultvalue = getDefaultValue(li_newrow);
				Object[][] value = defaultvalue.getAllData();
				for (int i = 0; i < value.length; i++) {
					this.setValueAt(value[i][1], li_newrow, (String) value[i][0]);//以前用的方法setRealValueAt，不能设置参照对象，故修改之【李春娟/2014-10-10】
					//this.setRealValueAt("" + value[i][1], li_newrow, (String) value[i][0]);
				}
			}
			Rectangle rect = getTable().getCellRect(li_newrow, 0, true);
			getTable().scrollRectToVisible(rect);
			if (_isAutoSelect) {
				getTable().setRowSelectionInterval(li_newrow, li_newrow);
			}
		}
		return li_newrow;
	}

	public void removeRow(int _row) {
		this.setRowStatusAs(_row, WLTConstants.BILLDATAEDITSTATE_DELETE);
		BillVO billVO = getBillVO(_row); //
		v_deleted_row.add(billVO); //
		getTableModel().removeRow(_row); //
	}

	public void removeAllDeleteSQLS() {
		v_deleted_row.clear();
	}

	public void removeAllRows() {
		for (int i = getTable().getRowCount() - 1; i >= 0; i--) {
			removeRow(i);
		}
	}

	public void removeRow() {
		stopEditing();
		int li_currrow = getTable().getSelectedRow(); // 取得当前行
		if (li_currrow < 0) {
			MessageBox.showSelectOne(this);
			return;
		}
		this.setRowStatusAs(li_currrow, WLTConstants.BILLDATAEDITSTATE_DELETE);
		BillVO billVO = getBillVO(li_currrow); //
		v_deleted_row.add(billVO); //
		getTableModel().removeRow(li_currrow); //
	}

	public void removeRow(boolean quiet) {
		stopEditing();
		int li_currrow = getTable().getSelectedRow(); // 取得当前行
		if (li_currrow < 0) {
			if (!quiet)
				MessageBox.showSelectOne(this);
			return;
		}
		this.setRowStatusAs(li_currrow, WLTConstants.BILLDATAEDITSTATE_DELETE);
		BillVO billVO = getBillVO(li_currrow); //
		v_deleted_row.add(billVO); //
		getTableModel().removeRow(li_currrow); //
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

	/**
	 * 判断列表是否编辑过，如果编辑过可能需要提示用户进行保存【李春娟/2016-04-13】
	 * @return
	 */
	public boolean isEdited() {
		this.stopEditing(); // 结束编辑
		if (v_deleted_row.size() > 0) {//删除的记录
			return true;
		}
		//新增或修改的记录
		for (int i = 0; i < getTableModel().getRowCount(); i++) {
			String str_type = ((RowNumberItemVO) getTableModel().getValueAt(i, str_rownumberMark)).getState(); //
			if (str_type.equals("INSERT")) { //如果是新增状态!
				return true;
			} else if (str_type.equals("UPDATE")) { //如果是修改状态!
				return true;
			}
		}
		return false;
	}

	public String[] getDeleteRowValues() {
		return (String[]) v_deleted_row.toArray(new String[0]);
	}

	//计算三种类型的SQL[删,增,改]
	public String[] getOperatorSQLs() {
		Vector v_all = new Vector();
		v_all.addAll(getDeleteSQLVector());
		for (int i = 0; i < getTableModel().getRowCount(); i++) {
			String str_type = ((RowNumberItemVO) getTableModel().getValueAt(i, str_rownumberMark)).getState(); //
			if (str_type.equals("INSERT")) { //如果是新增状态!
				v_all.add(getInsertSQL(i));
			} else if (str_type.equals("UPDATE")) { //如果是修改状态!
				v_all.add(getUpdateSQL(i));
			} else if (str_type.equals("INIT")) { //如果是初始化状态!
			} else { //
			}
		}
		return (String[]) v_all.toArray(new String[0]); //
	}

	public void executeDeleteOperationOnly() {
		this.stopEditing(); // 结束编辑
		String[] delsqls = getDeleteSQLs();
		v_deleted_row.clear();
		try {
			UIUtil.executeBatchByDS(str_templetcode, delsqls); // 有问题!!
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	public String[] clearDeleteSQLs() {
		String[] delsqls = getDeleteSQLs();
		this.v_deleted_row.clear();
		return delsqls;
	}

	public Integer[] getNotInitRowNums() {
		ArrayList list = new ArrayList();
		for (int i = 0; i < getTableModel().getRowCount(); i++) {
			String str_type = ((RowNumberItemVO) getTableModel().getValueAt(i, str_rownumberMark)).getState();
			if (str_type.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_INIT)) {
				continue;
			}
			list.add(new Integer(i));
		}
		return (Integer[]) list.toArray(new Integer[0]);

	}

	public String[] getDeleteSQLs() {
		return (String[]) getDeleteSQLVector().toArray(new String[0]); //
	}

	public Vector getDeleteSQLVector() {
		Vector v_return = new Vector();
		String str_del_sql = "";
		for (int i = 0; i < v_deleted_row.size(); i++) {
			BillVO billVO = (BillVO) v_deleted_row.get(i);
			String str_pkvalue = ((StringItemVO) billVO.getObject(templetVO.getPkname())).getStringValue(); // //
			str_del_sql = "delete from " + templetVO.getSavedtablename() + " where " + templetVO.getPkname() + "='" + str_pkvalue + "'"; //
			v_return.add(str_del_sql);
		}
		return v_return;
	}

	public int insertEmptyRow(int _row) {
		int i = this.getTableModel().insertEmptyRow(_row);
		String rowheight = templetVO.getListrowheight();
		//templetVO.getListrowheight()获取的可能是22,22 故追加,判断 【杨科/2013-06-19】
		if (rowheight != null && !rowheight.equals("") && !rowheight.contains(",")) {
			int height = Integer.parseInt(templetVO.getListrowheight());
			if (height > 0) {
				rowHeaderTable.setRowHeight(i, height);
				table.setRowHeight(i, height);
			}
		}
		return i;
	}

	public void addRow(Object[] _objs) {
		this.getTableModel().addRow(_objs);
	}

	public void addRow(HashMap _map) {
		this.getTableModel().addRow(_map); //
	}

	public void addRow(VectorMap _map) {
		this.getTableModel().addRow(_map); //
	}

	public void addRow(BillVO _billVO) {
		this.getTableModel().addRow(_billVO); //
	}

	public void addBillVOs(BillVO[] _billVOs) {
		if (_billVOs == null) {
			return; //
		}
		for (int i = 0; i < _billVOs.length; i++) {
			addRow(_billVOs[i]); //
		}
	}

	public void insertRow(int _row, Object[] _objs) {
		this.getTableModel().insertRow(_row, _objs); //
	}

	public void insertRow(int _row, BillVO _billVO) {
		this.getTableModel().insertRow(_row, _billVO); //
	}

	public BillVO getBillVO(int _row) {
		return getBillVO(_row, false); //
	}

	/**
	 * 
	 * @param _row
	 * @param _isGetLinkChildData  是否取同时计算出"引用子表"的数据! 后来在工作导出时需要计算出"引用子表"数据!【xch/2012-11-13】
	 * @return
	 */
	public BillVO getBillVO(int _row, boolean _isGetLinkChildData) {
		stopEditing(); //先结束编辑模式,否则取不到值
		BillVO vo = new BillVO();
		vo.setTempletCode(templetVO.getTempletcode()); //
		vo.setTempletName(templetVO.getTempletname()); //
		vo.setQueryTableName(templetVO.getTablename());
		vo.setSaveTableName(templetVO.getSavedtablename()); //
		vo.setPkName(templetVO.getPkname()); // 主键字段名
		vo.setSequenceName(templetVO.getPksequencename()); // 序列名
		vo.setToStringFieldName(templetVO.getTostringkey()); //设置ToString的字段名!!!

		int li_length = templetItemVOs.length;

		// 所有ItemKey
		String[] all_Keys = new String[li_length + 1]; //
		all_Keys[0] = this.str_rownumberMark; // 行号
		for (int i = 1; i < all_Keys.length; i++) {
			all_Keys[i] = this.templetVO.getItemKeys()[i - 1];
		}

		// 所有的名称
		String[] all_Names = new String[li_length + 1]; //
		all_Names[0] = str_rownumberMarkName; // 行号
		for (int i = 1; i < all_Names.length; i++) {
			all_Names[i] = this.templetVO.getItemNames()[i - 1];
		}

		String[] all_Types = new String[li_length + 1]; //
		all_Types[0] = str_rownumberMarkName; // 行号
		for (int i = 1; i < all_Types.length; i++) {
			all_Types[i] = this.templetVO.getItemTypes()[i - 1];
		}

		String[] all_ColumnTypes = new String[li_length + 1]; //
		all_ColumnTypes[0] = "NUMBER"; // 行号
		for (int i = 1; i < all_ColumnTypes.length; i++) {
			all_ColumnTypes[i] = this.templetItemVOs[i - 1].getSavedcolumndatatype(); //
		}

		boolean[] bo_isNeedSaves = new boolean[li_length + 1];
		bo_isNeedSaves[0] = false; // 行号
		for (int i = 1; i < bo_isNeedSaves.length; i++) {
			bo_isNeedSaves[i] = this.templetItemVOs[i - 1].isNeedSave();
		}

		vo.setKeys(all_Keys); //
		vo.setNames(all_Names); //
		vo.setItemType(all_Types); // 控件类型!!
		vo.setColumnType(all_ColumnTypes); // 数据库类型!!
		vo.setNeedSaves(bo_isNeedSaves); // 是否需要保存!!
		vo.setDatas(this.getValueAtRow(_row)); // 设置真正的数据!!

		//如果需要同时计算出"引用子表"数据,则根据引用子表的定义再去查询一下引用子表的数据,并设将数据设置在这个BillVO中..
		if (_isGetLinkChildData) {
			try {
				for (int i = 0; i < all_Types.length; i++) {
					if (all_Types[i].equals(WLTConstants.COMP_LINKCHILD)) { //如果是引用子表!
						String str_itemkey = all_Keys[i]; //
						//根据itemkey得到子表中的数据!
						CommUCDefineVO defvo = templetVO.getItemVo(str_itemkey).getUCDfVO(); //
						String str_temptCode = defvo.getConfValue("模板编码"); //
						String str_foreignkey = defvo.getConfValue("关联外键字段名"); //
						String str_thisPkValue = vo.getPkValue(); //主键值

						if (linkChildTempletVOMap == null) {
							linkChildTempletVOMap = new HashMap(); //
						}
						Pub_Templet_1VO temptVO = (Pub_Templet_1VO) linkChildTempletVOMap.get(str_temptCode); //
						if (temptVO == null) {
							temptVO = UIUtil.getPub_Templet_1VO(str_temptCode); //
							linkChildTempletVOMap.put(str_temptCode, temptVO); //
						}
						String str_linkChildSql = "select * from " + temptVO.getTablename() + " where " + str_foreignkey + "='" + str_thisPkValue + "'"; //
						BillVO[] childVOs = UIUtil.getBillVOsByDS(null, str_linkChildSql, temptVO); //
						System.out.println("子表共【" + childVOs.length + "】条数据!"); //
						vo.setLinkChildBillVOs(str_itemkey, childVOs); //设置!
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}
		return vo;
	}

	//取得选中行的数据!
	public String[][] getSelectedRowValue() {
		int li_selectedRow = this.getTable().getSelectedRow(); //
		if (li_selectedRow < 0) {
			return null;
		}
		Pub_Templet_1_ItemVO[] itemVOs = getTempletItemVOs(); //
		ArrayList al_values = new ArrayList(); //
		for (int i = 0; i < itemVOs.length; i++) {
			String str_realValue = getRealValueAtModel(li_selectedRow, itemVOs[i].getItemkey()); //
			if (str_realValue != null && !str_realValue.trim().equals("")) { //如果不为空!
				al_values.add(new String[] { itemVOs[i].getItemkey(), str_realValue }); //
			}
		}
		String[][] str_return = new String[al_values.size()][2]; //
		for (int i = 0; i < str_return.length; i++) {
			str_return[i] = (String[]) al_values.get(i); //
		}
		return str_return; //
	}

	/**
	 * 取得选中行的BillVO
	 * 
	 * @return
	 */
	public BillVO getSelectedBillVO() {
		//此处改为默认不考虑勾选情况,原因如下:
		//1.以勾选方式选择数据, 一般是用于进行多条数据同时处理时的需求, 而"浏览","编辑","删除"等针对单一数据的操作则应该采用直接选择的方式.
		// 否则, 若采用勾选方式, 勾选了第一行(RowNum=1), 现在想浏览或编辑或删除第2行(RowNum=2)时, 首先应该取消第1行的勾选状态, 然后再勾选第2行, 这样的操作就不合理了.
		//2.如果默认为考虑勾选情况，则会出现双击不同记录，最后看到的都是同一条数据！
		//3.一般情况是没有勾选框的列表，有勾选框的情况毕竟是少数
		//同理，修改的方法有getSelectedBillVOs()，getSelectedRow()，getSelectedRows()【李春娟/2012-03-01】
		return getSelectedBillVO(false, false); //
	}

	public BillVO getSelectedBillVO(boolean _isThinkChecked) {
		return getSelectedBillVO(_isThinkChecked, false); //
	}

	public BillVO getSelectedBillVO(boolean _isThinkChecked, boolean _isGetLinkChildData) {
		if (_isThinkChecked && this.isRowNumberChecked) { //如果考虑勾选情况,并且确实是勾选情况
			BillVO[] vos = getCheckedBillVOs(_isGetLinkChildData);
			if (vos.length == 0) {//这里需要判断一下，是否没有勾选，否则报数组越界异常【李春娟/2012-03-01】
				return null;
			}
			return vos[0]; //
		} else {
			int li_selectedRow = this.getTable().getSelectedRow(); //
			if (li_selectedRow < 0) {
				return null;
			}
			return this.getBillVO(li_selectedRow, _isGetLinkChildData); //
		}
	}

	/**
	 * 取得所有选中的行的数据VO
	 * 
	 * @return
	 */
	public BillVO[] getSelectedBillVOs() {
		//此处改为默认不考虑勾选情况,原因如下:
		//1.以勾选方式选择数据, 一般是用于进行多条数据同时处理时的需求, 而"浏览","编辑","删除"等针对单一数据的操作则应该采用直接选择的方式.
		// 否则, 若采用勾选方式, 勾选了第一行(RowNum=1), 现在想浏览或编辑或删除第2行(RowNum=2)时, 首先应该取消第1行的勾选状态, 然后再勾选第2行, 这样的操作就不合理了.
		//2.如果默认为考虑勾选情况，则会出现双击不同记录，最后看到的都是同一条数据！
		//3.一般情况是没有勾选框的列表，有勾选框的情况毕竟是少数
		//同理，修改的方法有getSelectedBillVO()，getSelectedRow()，getSelectedRows()【李春娟/2012-03-01】
		return getSelectedBillVOs(false, false); //
	}

	//后来有了勾选框的效果,为了兼容及又可以选择原来的数据,加了个参数!!
	public BillVO[] getSelectedBillVOs(boolean _isThinkChecked) {
		return getSelectedBillVOs(_isThinkChecked, false); //
	}

	//后来有了勾选框的效果,为了兼容及又可以选择原来的数据,加了个参数!!
	public BillVO[] getSelectedBillVOs(boolean _isThinkChecked, boolean _isGetLinkChildData) {
		if (_isThinkChecked && this.isRowNumberChecked) { //如果考虑勾选情况,并且确实是勾选情况
			return getCheckedBillVOs(_isGetLinkChildData); //
		} else {
			int[] li_selectedRows = this.getTable().getSelectedRows();
			BillVO[] vos = new BillVO[li_selectedRows.length];
			for (int i = 0; i < vos.length; i++) {
				vos[i] = this.getBillVO(li_selectedRows[i], _isGetLinkChildData);
			}
			return vos; //
		}
	}

	/**取得所有勾选中的BillVO
	 * @return
	 */
	public BillVO[] getCheckedBillVOs() {
		return getCheckedBillVOs(false); //
	}

	/**取得所有勾选中的BillVO
	 * @return
	 */
	public BillVO[] getCheckedBillVOs(boolean _isGetLinkChildData) {
		int[] li_checkedRows = this.getCheckedRows(); //
		BillVO[] vos = new BillVO[li_checkedRows.length];
		for (int i = 0; i < vos.length; i++) {
			vos[i] = this.getBillVO(li_checkedRows[i], _isGetLinkChildData);
		}
		return vos; //
	}

	// 得到删除的BillVO[]
	public BillVO[] getDeletedBillVOs() {
		return (BillVO[]) v_deleted_row.toArray(new BillVO[0]);
	}

	// 得到所有新增的BillVO
	public BillVO[] getInsertedBillVOs() {
		ArrayList insertvos = new ArrayList();
		for (int i = 0; i < this.getTable().getRowCount(); i++) {
			if (this.getRowNumberEditState(i).equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_INSERT))
				insertvos.add(this.getBillVO(i));
		}
		return (BillVO[]) insertvos.toArray(new BillVO[0]);
	}

	// 得到所有修改过的BillVO
	public BillVO[] getUpdatedBillVOs() {
		ArrayList updatevos = new ArrayList();
		for (int i = 0; i < this.getTable().getRowCount(); i++) {
			if (this.getRowNumberEditState(i).equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_UPDATE))
				updatevos.add(this.getBillVO(i));
		}
		return (BillVO[]) updatevos.toArray(new BillVO[0]);
	}

	/**
	 * 取得所有BillVO
	 * 
	 * @return
	 */
	public BillVO[] getBillVOs() {
		ArrayList _temp = new ArrayList();
		_temp.addAll(v_deleted_row);
		this.clearDeleteBillVOs();
		for (int i = 0; i < this.getTable().getRowCount(); i++) {
			_temp.add(this.getBillVO(i));
		}
		return (BillVO[]) _temp.toArray(new BillVO[0]);
	}

	/**
	 * 取得所有BillVO
	 * 
	 * @return
	 */
	public BillVO[] getAllBillVOs() {
		int li_rowCount = this.getTable().getRowCount();
		BillVO[] vos = new BillVO[li_rowCount];
		for (int i = 0; i < li_rowCount; i++) {
			vos[i] = this.getBillVO(i);
		}
		return vos;
	}

	private String getObjectRealValue(Object _obj) {
		if (_obj == null) {
			return null;
		}
		if (_obj instanceof String) {
			return (String) _obj;
		} else if (_obj instanceof ComBoxItemVO) {
			ComBoxItemVO vo = (ComBoxItemVO) _obj;
			return vo.getId();
		} else if (_obj instanceof RefItemVO) {
			RefItemVO vo = (RefItemVO) _obj;
			return vo.getId();
		} else {
			return _obj.toString();
		}
	}

	private String getObjectViewValue(Object _obj) {
		if (_obj == null) {
			return null;
		}
		if (_obj instanceof String) {
			return (String) _obj;
		} else if (_obj instanceof ComBoxItemVO) {
			ComBoxItemVO vo = (ComBoxItemVO) _obj;
			return vo.getName();
		} else if (_obj instanceof RefItemVO) {
			RefItemVO vo = (RefItemVO) _obj;
			return vo.getName();
		} else {
			return _obj.toString();
		}
	}

	public int findColumnIndex(String _key) {
		int li_columncount = this.getColumnModel().getColumnCount();
		for (int i = 0; i < li_columncount; i++) {
			TableColumn column = this.getColumnModel().getColumn(i);
			if (((String) column.getIdentifier()).equalsIgnoreCase(_key)) {
				return i;
			}
		}
		return -1;
	}

	public TableColumn findColumn(String _key) {
		int li_columncount = this.getColumnModel().getColumnCount();
		for (int i = 0; i < li_columncount; i++) {
			TableColumn column = this.getColumnModel().getColumn(i);
			if (((String) column.getIdentifier()).equalsIgnoreCase(_key)) {
				return column; //
			}
		}
		return null;
	}

	/**
	 * 插入一行，并将该行状态设为init
	 * 
	 * @param _row
	 * @param _values
	 */
	public void insertRowWithInitStatus(int _row, HashMap _values) {
		if (_row < 0) {
			this.addEmptyRow(); //
			int li_rowcount = this.getTable().getModel().getRowCount();
			Set keys = _values.keySet();
			for (Iterator it = keys.iterator(); it.hasNext();) {
				String key = (String) it.next();
				if (key.equalsIgnoreCase("VERSION") && this.getTableModel().findModelIndex(key) < 0) {
					continue;
				} else
					this.setValueAt(_values.get(key), li_rowcount - 1, key);
			}
			if (li_rowcount - 1 >= 0) {
				Rectangle rect = this.getTable().getCellRect(li_rowcount - 1, 0, true);
				this.getTable().scrollRectToVisible(rect);
				this.getTable().setRowSelectionInterval(li_rowcount - 1, li_rowcount - 1);
			}
			bo_ifProgramIsEditing = true;
			this.setValueAt(new RowNumberItemVO(WLTConstants.BILLDATAEDITSTATE_INIT, li_rowcount - 1), li_rowcount - 1, "_RECORD_ROW_NUMBER");
		} else {
			this.insertEmptyRow(_row + 1);
			Set keys = _values.keySet();
			for (Iterator it = keys.iterator(); it.hasNext();) {
				String key = (String) it.next();
				if (key.equalsIgnoreCase("VERSION") && this.getTableModel().findModelIndex(key) < 0) {
					continue;
				} else
					this.setValueAt(_values.get(key), _row + 1, key);
			}
			this.getTable().setRowSelectionInterval(_row + 1, _row + 1);
			bo_ifProgramIsEditing = true;
			this.setValueAt(new RowNumberItemVO(WLTConstants.BILLDATAEDITSTATE_INIT, _row + 1), _row + 1, "_RECORD_ROW_NUMBER");

		}
		bo_ifProgramIsEditing = false;
	}

	/**
	 * 设置所有行的状态
	 * 
	 * @param status
	 */
	public void setAllRowStatusAs(String status) {
		bo_ifProgramIsEditing = true;
		for (int i = 0; i < table.getRowCount(); i++) {
			RowNumberItemVO rowNumVO = getRowNumberVO(i); //
			if (rowNumVO == null) {
				rowNumVO = new RowNumberItemVO(WLTConstants.BILLDATAEDITSTATE_INIT, i); //
			}
			if (status.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_INIT)) {
				rowNumVO.setState(WLTConstants.BILLDATAEDITSTATE_INIT); //
				//this.setValueAt(new RowNumberItemVO(WLTConstants.BILLDATAEDITSTATE_INIT, i), i, "_RECORD_ROW_NUMBER");
			} else if (status.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
				rowNumVO.setState(WLTConstants.BILLDATAEDITSTATE_INSERT); //
				//this.setValueAt(new RowNumberItemVO(WLTConstants.BILLDATAEDITSTATE_INSERT, i), i, "_RECORD_ROW_NUMBER");
			} else if (status.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
				rowNumVO.setState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
				//this.setValueAt(new RowNumberItemVO(WLTConstants.BILLDATAEDITSTATE_UPDATE, i), i, "_RECORD_ROW_NUMBER");
			}
		}
		bo_ifProgramIsEditing = false;
	}

	/**
	 * 设置某一行的状态
	 * 
	 * @param _row
	 * @param status
	 */
	public void setRowStatusAs(int _row, String status) {
		bo_ifProgramIsEditing = true;
		if (status.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_INIT))
			this.setValueAt(new RowNumberItemVO(WLTConstants.BILLDATAEDITSTATE_INIT, _row), _row, "_RECORD_ROW_NUMBER");
		else if (status.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_INSERT))
			this.setValueAt(new RowNumberItemVO(WLTConstants.BILLDATAEDITSTATE_INSERT, _row), _row, "_RECORD_ROW_NUMBER");
		else if (status.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_UPDATE))
			this.setValueAt(new RowNumberItemVO(WLTConstants.BILLDATAEDITSTATE_UPDATE, _row), _row, "_RECORD_ROW_NUMBER");
		else if (status.equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_DELETE))
			this.setValueAt(new RowNumberItemVO(WLTConstants.BILLDATAEDITSTATE_DELETE, _row), _row, "_RECORD_ROW_NUMBER");
		bo_ifProgramIsEditing = false;
	}

	public TableColumnModel getColumnModel() {
		if (columnModel != null) {
			return columnModel;
		}

		columnModel = new DefaultTableColumnModel(); // 创建列模式
		//袁江晓 20130531 保存自定义列  更改
		//1.首先取得自定义显示的列
		String cols = "";
		boolean isrelodcol = false;
		if (CustomizeColumnMap.getCustomizeBean(templetVO.getTempletcode()) == null) {//先获得本地缓存
			isrelodcol = false;
		} else {
			isrelodcol = true;
			cols = CustomizeColumnMap.getCustomizeBean(templetVO.getTempletcode()).get_text();//结果格式为LAW_FILE_CODE;S_CHECK_DEPT;S_CHECK_PEOPLE;MATTER_TYPE;state2;
		}
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getListisshowable().booleanValue()) { // 如果该列是列表情况显示!!!,则加入该列
				if (isrelodcol) {//如果本地有记录则需要加载本地保存的缓存
					if (cols.indexOf(templetItemVOs[i].getItemkey()) > -1) {//如果存在
						columnModel.addColumn(getTableColumns()[i]);
					}
				} else {
					columnModel.addColumn(getTableColumns()[i]);
				}
			}
		}
		return columnModel;
	}

	/**
	 * 取得表格中某一列的列名
	 * @param _colIndex
	 * @return
	 */
	public String getColumnName(int _colIndex) {
		String itemKey = (String) this.getTable().getColumnModel().getColumn(_colIndex).getIdentifier();
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase(itemKey)) {
				return templetItemVOs[i].getItemname();
			}
		}
		return null;
	}

	public int getRowCount() {
		return this.getTableModel().getRowCount();
	}

	private TableColumn getRowNumberColumn() {
		if (rowNumberColumn != null) {
			return rowNumberColumn;
		}
		ListCellRender_RowNumber render = new ListCellRender_RowNumber(); // 创建列绘制器..
		render.setBillList(this); //绑定!!
		ListCellEditor_RowNumber editor = new ListCellEditor_RowNumber();
		editor.setBillList(this); //绑定!!
		this.editor = editor;//袁江晓 20131029 添加
		rowNumberColumn = new TableColumn(0, li_rowno_width, render, editor); // 创建列,对应第一列数据,行号宽
		if (ClientEnvironment.getInstance().isEngligh()) {
			rowNumberColumn.setHeaderValue("No."); //
		} else {
			rowNumberColumn.setHeaderValue(str_rownumberMarkName); //
		}
		rowNumberColumn.setIdentifier(this.str_rownumberMark);
		return rowNumberColumn;
	}

	/**
	 * 创建所有的列
	 * 
	 * @return
	 */
	private TableColumn[] getTableColumns() {
		if (this.allTableColumns != null) {
			return allTableColumns;
		}
		allTableColumns = new TableColumn[templetItemVOs.length]; // 列的总数等于模板中的总数!
		for (int i = 0; i < allTableColumns.length; i++) {
			String str_key = templetItemVOs[i].getItemkey(); // key
			String str_type = templetItemVOs[i].getItemtype(); // 类型
			int li_width = templetItemVOs[i].getListwidth().intValue(); // 宽度
			if (!TBUtil.isEmpty(templetItemVOs[i].getItemname())) {
				li_width += LookAndFeel.getFONT_REVISE_SIZE() * (templetItemVOs[i].getItemname().length() + (templetItemVOs[i].getIsmustinput() ? 4 : 0));
			}
			if (str_key.equals("TEMPLETCODE")) {
				templete_codecolumn_index = i;
			}

			TableCellEditor cellEditor = null;
			TableCellRenderer cellRender = null;
			if (str_type.equals(WLTConstants.COMP_LABEL)) {
				if (templetVO.getIslistautorowheight()) { //如果是自动撑高,则强行使用TextArea显示!因为这样撑高后才会有效果,否则虽然撑高了,但还是一行,后面跟了个[...],没达到效果!【xch/2012-05-04】
					cellRender = new ListCellRender_JTextArea(templetItemVOs[i]); // 多行文本显示器
				} else {
					cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
				}
				cellEditor = new ListCellEditor_Label(templetItemVOs[i]); // 标签
			} else if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || // 文本框
					str_type.equals(WLTConstants.COMP_NUMBERFIELD) || // 数字框
					str_type.equals(WLTConstants.COMP_PASSWORDFIELD) || // 密码框
					str_type.equals(WLTConstants.COMP_REGULAR)//[zzl]正则表达式
			) {
				if (templetVO.getIslistautorowheight()) {
					cellRender = new ListCellRender_JTextArea(templetItemVOs[i]); // 多行文本显示器
				} else {
					cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
				}
				JTextField textField = null;
				if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || str_type.equals(WLTConstants.COMP_REGULAR)) {//[zzl]正则表达式
					textField = new JTextField();
				} else if (str_type.equals(WLTConstants.COMP_NUMBERFIELD)) // 数字框
				{
					textField = new JFormattedTextField();
					CommUCDefineVO ucvo = templetItemVOs[i].getUCDfVO();
					textField.setDocument(new NumberFormatdocument(ucvo)); //定义数字框只能输入数字,输入字母不让键入!【李春娟/2013-06-05】
				} else if (str_type.equals(WLTConstants.COMP_PASSWORDFIELD)) { // 密码框
					textField = new JPasswordField();
				}
				textField.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
				cellEditor = new ListCellEditor_TextField(textField, templetItemVOs[i]);
			} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { // 下拉框
				if (templetVO.getIslistautorowheight()) { //如果是自动撑高,【xch/2012-05-04】
					cellRender = new ListCellRender_JTextArea(templetItemVOs[i]); // 多行文本显示器
				} else {
					cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
				}
				JComboBox comBox = new JComboBox(); //
				if (templetItemVOs[i].getComBoxItemVos() != null) {
					comBox.addItem(new ComBoxItemVO("", null, "")); //
					for (int kk = 0; kk < templetItemVOs[i].getComBoxItemVos().length; kk++) {
						if (templetItemVOs[i].getComBoxItemVos()[kk] != null) {
							comBox.addItem(templetItemVOs[i].getComBoxItemVos()[kk]); //先初始化控件!!
						}
					}
				}
				cellEditor = new ListCellEditor_ComboBox(comBox, templetItemVOs[i]);
			} else if (str_type.equals(WLTConstants.COMP_TEXTAREA)) { // 多行文本框
				cellRender = new ListCellRender_JTextArea(templetItemVOs[i]); // 多行文本显示器
				cellEditor = new ListCellEditor_TextArea(templetItemVOs[i]); // 多行文本框编辑器
			} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || // 表型参照1
					str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || // 树型参照1
					str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || // 多选参照1
					str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || // 自定义参照
					str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //
					str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //
					str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || // 
					str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || // 注册参照
					str_type.equals(WLTConstants.COMP_DATE) || // 日历
					str_type.equals(WLTConstants.COMP_DATETIME) || // 时间
					str_type.equals(WLTConstants.COMP_COLOR) || // 颜色选择框
					str_type.equals(WLTConstants.COMP_BIGAREA) || // 大文本框
					str_type.equals(WLTConstants.COMP_PICTURE) || // 图标选择框
					str_type.equals(WLTConstants.COMP_LINKCHILD) || // 图标选择框
					str_type.equals(WLTConstants.COMP_IMPORTCHILD) || // 导入子表
					str_type.equals(WLTConstants.COMP_OFFICE) // Office控件
			) { // 如果是各种参照,现在包括7种参照
				if (templetVO.getIslistautorowheight()) { //如果是自动撑高,【xch/2012-05-04】!!
					cellRender = new ListCellRender_JTextArea(templetItemVOs[i]); //多行文本显示器!!
				} else {
					cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
				}
				cellEditor = new ListCellEditor_Ref(templetItemVOs[i]);
			} else if (str_type.equals(WLTConstants.COMP_CHECKBOX)) { // 勾选框
				cellRender = new ListCellRender_CheckBox();
				cellEditor = new ListCellEditor_CheckBox(new JCheckBox(), templetItemVOs[i]);
			} else if (str_type.equals(WLTConstants.COMP_BUTTON)) { // 如果是按钮
				cellRender = new ListCellRender_Button(templetItemVOs[i]); // 按钮显示器
				cellEditor = new ListCellEditor_Button(templetItemVOs[i]); // 多行文本框编辑器
			} else if (str_type.equals(WLTConstants.COMP_FILECHOOSE)) { // 如果是文件选择框
				cellRender = new ListCellRender_FileChoose(templetItemVOs[i]); // 按钮显示器
				cellEditor = new ListCellEditor_FileChoose(templetItemVOs[i]); // 文件选择框
			} else if (str_type.equals(WLTConstants.COMP_STYLEAREA)) { //富文本框
				cellRender = new ListCellRender_JTextArea(templetItemVOs[i], true); //使用多行文本框显示!!但提示有尾部宏代码
				cellEditor = new ListCellEditor_StylePad(templetItemVOs[i], this); //
			} else if (str_type.equals(WLTConstants.COMP_SELFDESC)) {
				if (templetItemVOs[i].getUCDfVO() != null) {
					String str_render_cls = templetItemVOs[i].getUCDfVO().getConfValue("列表渲染器"); //
					if (str_render_cls != null && !str_render_cls.trim().equals("")) { //如果不为空,则反射创建!!
						try {
							cellRender = (TableCellRenderer) Class.forName(str_render_cls).getConstructor(new Class[] { Pub_Templet_1_ItemVO.class }).newInstance(new Object[] { templetItemVOs[i] });
						} catch (Exception e) {
							e.printStackTrace();
							cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //创建失败的话,则使用默认,保证页面能出来!
						}
					} else {
						cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
					}
					String str_editor_cls = templetItemVOs[i].getUCDfVO().getConfValue("列表编辑器"); //
					if (str_editor_cls != null && !str_editor_cls.trim().equals("")) {
						try {
							cellEditor = (TableCellEditor) Class.forName(str_editor_cls).getConstructor(new Class[] { Pub_Templet_1_ItemVO.class }).newInstance(new Object[] { templetItemVOs[i] });
						} catch (Exception e) {
							e.printStackTrace();
							cellEditor = new ListCellEditor_TextField(new JTextField(), templetItemVOs[i]);
						}
					} else {
						cellEditor = new ListCellEditor_TextField(new JTextField(), templetItemVOs[i]);
					}
				} else {
					cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
					cellEditor = new ListCellEditor_TextField(new JTextField(), templetItemVOs[i]);
				}
			} else {
				cellRender = null; //
				cellEditor = null;
			}

			allTableColumns[i] = new TableColumn(i + 1, li_width, cellRender, cellEditor); // 创建列
			allTableColumns[i].setIdentifier(templetItemVOs[i].getItemkey()); // 唯一性标识
			if (ClientEnvironment.getInstance().isEngligh()) {
				allTableColumns[i].setHeaderValue(templetItemVOs[i].getItemname_e()); // 列的标题
			} else {
				allTableColumns[i].setHeaderValue(templetItemVOs[i].getItemname()); // 列的标题
			}

		}
		return allTableColumns;
	}

	/**
	 * 创建数据Model,数据Model也是与模板一一对应的!!!!
	 * 
	 * @return
	 */
	public BillListModel getTableModel() {
		if (tableModel != null) {
			return tableModel;
		}

		int li_columncount = templetItemVOs.length;
		Object[][] data = new Object[0][li_columncount + 1]; //
		String[] columns = templetVO.getItemKeys(); // //
		String[] new_columns = new String[columns.length + 1];
		new_columns[0] = str_rownumberMark; //
		for (int i = 0; i < columns.length; i++) {
			new_columns[i + 1] = columns[i];
		}

		tableModel = new BillListModel(data, new_columns, this.templetVO); // 创建数据Model
		tableModel.setBillListPanel(this); //
		tableModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				int lastRow = e.getLastRow(); //
				int mColIndex = e.getColumn(); //
				switch (e.getType()) {
				case TableModelEvent.INSERT:
					refreshCombineData(); //INSERT DELETE UPDATE tableModel数据变化 合并刷新 【杨科/2013-07-23】 
					break;
				case TableModelEvent.DELETE:
					refreshCombineData();
					break;
				case TableModelEvent.UPDATE:
					refreshCombineData();
					onChanged(lastRow, mColIndex);
					break;
				}
			}
		});

		return tableModel; //
	}

	public void setScrollable(boolean _bo) {
		if (_bo) {
			scrollPanel_main.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED); //
			scrollPanel_main.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED); //
		} else {
			scrollPanel_main.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER); //
			scrollPanel_main.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); //
		}
	}

	/**
	 * 表格数据发生变化
	 * 
	 * @param _row
	 * @param _col
	 */
	private synchronized void onChanged(int _row, int _col) {
		if (bo_ifProgramIsEditing) {
			return;
		}
		if (1 == 1) {
			//return;
		}
		if (_col == 0)// 如果是修改了状态，则不做处理.否则在findModelItemKey(_col)中会溢出.
			return;
		Object obj = getValueAt(_row, _col); // 变化的数据!!如果没变化则不做
		if (obj != null && obj instanceof BillItemVO) {
			BillItemVO itemVO = (BillItemVO) obj;
			if (!itemVO.isValueChanged()) {
				return;
			}
		}

		bo_ifProgramIsEditing = true; // 设置程序真在编辑
		setRowNumberState(_row); // 设置行号状态为处理修改状态
		execEditFormula(_row, _col);// 执行加载公式
		String str_itemkey = findModelItemKey(_col); // 变化的Key
		for (int i = 0; i < v_listeners.size(); i++) {
			BillListEditListener listener = (BillListEditListener) v_listeners.get(i);
			listener.onBillListValueChanged(new BillListEditEvent(str_itemkey, obj, _row, this)); //
		}
		bo_ifProgramIsEditing = false; // 程序真在编辑结束!!
	}

	/**
	 * 注册事件
	 * 
	 * @param _listener
	 */
	public void addBillListEditListener(BillListEditListener _listener) {
		v_listeners.add(_listener); //
	}

	/**
	 * 注册事件
	 * 
	 * @param _listener
	 */
	public void addBillListSelectListener(BillListSelectListener _listener) {
		v_selectedListeners.add(_listener); //
	}

	public void addBillListCheckedAllListener(BillListCheckedAllListener _listener) {
		v_checkedAllListeners.add(_listener); //
	}

	public void addBillListCheckedListener(BillListCheckedListener _listener) {
		editor.getV_checkedListeners().add(_listener); //
	}

	public void addBillListMouseDoubleClickedListener(BillListMouseDoubleClickedListener _listener) {
		v_MouseDoubleClickListeners.add(_listener); //
	}

	/**
	 * 注册事件
	 * 
	 * @param _listener
	 */
	public void addBillListAfterQueryListener(BillListAfterQueryListener _listener) {
		v_afterqueryListeners.add(_listener); //
	}

	private synchronized void setRowNumberState(int _row) {
		RowNumberItemVO valueVO = (RowNumberItemVO) getValueAt(_row, str_rownumberMark);
		if (valueVO == null)
			valueVO = new RowNumberItemVO(WLTConstants.BILLDATAEDITSTATE_INIT, _row);
		String str_oldstate = valueVO.getState(); //
		if (str_oldstate != null && (str_oldstate.equals("INSERT") || str_oldstate.equals("UPDATE"))) {
		} else {
			valueVO.setState(RowNumberItemVO.UPDATE);
			setValueAt(valueVO, _row, str_rownumberMark);
		}
	}

	public void execViewFormula() {

	}

	/**
	 * 执行编辑公式..
	 * 
	 * @param _row
	 */
	public void execEditFormula(int _row, int _col) {
		if (_col != 0) {
			String str_editFormuladesc = this.getTempletItemVOs()[_col - 1].getEditformula();
			if (str_editFormuladesc != null && !str_editFormuladesc.trim().equals("")) {
				String[] str_editformulas = getTBUtil().split1(str_editFormuladesc, ";");
				for (int i = 0; i < str_editformulas.length; i++) {// 遍历执行所有公式
					dealEditFormula(str_editformulas[i], _row); //
				}
			} // 遍历公式结束!!!
		}
	}

	private int findItemPos(Pub_Templet_1_ItemVO[] itemVos, String _itemkey) {
		for (int i = 0; i < itemVos.length; i++) {
			if (itemVos[i].getItemkey().equalsIgnoreCase(_itemkey)) {
				return i;
			}
		}
		return -1;
	}

	private String findItemType(Pub_Templet_1_ItemVO[] itemVos, String _itemkey) {
		for (int i = 0; i < itemVos.length; i++) {
			if (itemVos[i].getItemkey().equalsIgnoreCase(_itemkey)) {
				return itemVos[i].getItemtype();
			}
		}
		return null;
	}

	private int findListIsHtmlHref(String _itemkey) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase(_itemkey)) {
				if (templetItemVOs[i].getItemtype().equals(WLTConstants.COMP_BUTTON) || templetItemVOs[i].getItemtype().equals(WLTConstants.COMP_FILECHOOSE)) { // 如果是按钮则强行返回
					return 1;
				} else {
					if (templetItemVOs[i].getListishtmlhref()) {
						return 2; // 是否是超链
					}
				}
			}
		}
		return -1; // 默认不是超链
	}

	private VectorMap getDefaultValue(int _newrow) {// 目前直接的字符串或者{Date(10)},{Date(19)}
		VectorMap result = new VectorMap();
		for (int i = 0; i < templetItemVOs.length; i++) {
			Pub_Templet_1_ItemVO tempitem = templetItemVOs[i];
			String formula = tempitem.getDefaultvalueformula();
			if (formula != null && !formula.equals("\"\"") && !formula.equals("")) {
				formula = formula.trim(); //
				// formula = UIUtil.replaceAll(formula, " ", "");
				try {
					Vector v_itemkey = getTBUtil().findItemKey(formula);
					for (int j = 0; j < v_itemkey.size(); j++) {
						formula = UIUtil.replaceAll(formula, "{" + v_itemkey.get(j) + "}", "" + ClientEnvironment.getInstance().get(v_itemkey.get(j)));
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				System.out.println("执行默认值公式:[" + formula + "]");
				JepFormulaParseAtUI jepFormulaParseAtUI = new JepFormulaParseAtUI(this); //
				Object obj = jepFormulaParseAtUI.execFormula(formula);
				if (obj == null) {
					result.put(tempitem.getItemkey(), ""); //
				} else {
					result.put(tempitem.getItemkey(), obj); //以前的代码obj转成String了，如果是参照，id和name值都为name就有问题了，故修改之【李春娟/2014-10-10】
				}
			}
		}
		return result;
	}

	/**
	 * 真正执行某一个编辑公式..使用JEP去执行!!
	 * 
	 * @param _formula
	 */
	private void dealEditFormula(String _formula, int _row) {
		try {
			JepFormulaParse jepParse = new JepFormulaParseAtUI(this);
			jepParse.execFormula(_formula); // 使用JEP执行公式!!!!
			System.out.println("成功执行编辑公式项:[" + _formula + "]"); //
		} catch (Exception ex) {
			System.out.println("失败执行编辑公式项:[" + _formula + "]");
			ex.printStackTrace(); //
		}
	}

	public void setSelectedRowCode(String _code) {
		int li_rowcount = table.getModel().getRowCount();
		String temp_str;
		for (int i = 0; i < li_rowcount; i++) {
			temp_str = (String) this.table.getValueAt(i, this.templete_codecolumn_index);
			if (temp_str.equals(_code)) {
				setSelectedRow(i);
				return;
			}
		}
	}

	public void setSelectedRow(int _row) {
		if (_row >= 0) {
			if (table.getRowCount() > 0) {
				Rectangle rect = table.getCellRect(_row, 0, true);
				table.scrollRectToVisible(rect);
				table.setRowSelectionInterval(_row, _row);
			}
		}
	}

	public void addSelectedRow(int _row) {
		if (_row >= 0) {
			if (table.getRowCount() > 0) {
				Rectangle rect = table.getCellRect(_row, 0, true);
				table.scrollRectToVisible(rect);
				table.addRowSelectionInterval(_row, _row);
			}
		}
	}

	//设置第几行选中!!
	public void setCheckedRow(int _row, boolean _checked) {
		if (_row >= 0) {
			if (table.getRowCount() > 0) {
				RowNumberItemVO numberVO = getRowNumberVO(_row); //
				if (numberVO != null) {
					numberVO.setChecked(_checked); //
					this.rowHeaderTable.repaint(); //
					this.rowHeaderTable.revalidate(); //
					Rectangle rect = table.getCellRect(_row, 0, true);
					table.scrollRectToVisible(rect);
				}
			}
		}
	}

	/**
	 * 置顶
	 */
	public void moveToTop() {
		if (table.getRowCount() > 0) {
			Rectangle rect = table.getCellRect(0, 0, true);
			rect.y = (int) (rect.getY() - li_rowHeight);
			table.scrollRectToVisible(rect);
		}
	}

	/**
	 * 清除选择
	 */
	public void clearSelection() {
		table.clearSelection(); //
	}

	public void selectAll() {
		table.selectAll();
	}

	/**
	 * 找到某一行,根据itemkey与value,比如 code=003
	 * 
	 * @param _itemkey
	 * @param _value
	 * @return
	 */
	public int findRow(String _itemkey, String _value) {
		for (int i = 0; i < this.getRowCount(); i++) {
			String str_realvalue = getRealValueAtModel(i, _itemkey); //
			if (str_realvalue != null && str_realvalue.equals(_value)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 得到数据源名称
	 * 
	 * @return
	 */
	public String getDataSourceName() {
		return getTableModel().getDataSourceName(); //
	}

	/**
	 * 得到数据权限过滤条件
	 * 
	 * @return
	 */
	public String getDataconstraint() {
		if (templetVO.getDataconstraint() == null || templetVO.getDataconstraint().trim().equals("null") || templetVO.getDataconstraint().trim().equals("")) {
			return null; // 默认数据源
		} else {
			return "" + new JepFormulaParseAtUI(null).execFormula(templetVO.getDataconstraint()); //
		}
	}

	public String getAutoLoadConstraint() {
		if (templetVO.getAutoloadconstraint() == null || templetVO.getAutoloadconstraint().trim().equals("null") || templetVO.getAutoloadconstraint().trim().equals("")) {
			return null; // 默认数据源
		} else {
			return "" + new JepFormulaParseAtUI(null).execFormula(templetVO.getAutoloadconstraint()); //
		}
	}

	public String getTableAllItem() {
		StringBuffer sb_item = new StringBuffer();
		int itemlength = templetItemVOs.length;
		for (int i = 0; i < itemlength; i++) {
			if (templetItemVOs[i].isNeedSave()) {
				if (sb_item.length() == 0) {
					sb_item.append(" " + templetItemVOs[i].getItemkey());
				} else {
					sb_item.append("," + templetItemVOs[i].getItemkey());
				}
			}
		}
		sb_item.append(" ");
		if (sb_item.toString().trim().length() == 0) {
			return "*";
		}
		return sb_item.toString();
	}

	public String getAutoLoadSQL() {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select * from " + templetVO.getTablename() + " where 1=1 "); //  
		String str_autocon = getAutoLoadConstraint(); //
		if (str_autocon != null) {
			sb_sql.append(" and (" + str_autocon + ")"); //
		}

		String str_dataPolicySQL = getDataPolicyCondition(); //数据权限策略!!
		if (str_dataPolicySQL != null) {
			sb_sql.append(" and (" + str_dataPolicySQL + ")"); ////
		}

		String str_orderCon = templetVO.getOrdercondition(); //排序条件!!
		if (str_orderCon != null && !str_orderCon.trim().equals("")) { //如果有排序条件! 则加上排序条件! 以前一度自动加载时没有排序条件!
			sb_sql.append(" order by " + str_orderCon); //
		}
		return sb_sql.toString(); //
	}

	/**
	 * 用户自定义过滤条件
	 * 
	 * @return
	 */
	public String getDataFilterCustCondition() {
		return str_dataFilterCustCondition;
	}

	// 根据条件取数!!!
	public void QueryDataByCondition(final String _wherecondition) {
		queryDataByDS(getDataSourceName(), getSQL(_wherecondition, this.templetVO.getOrdercondition()));
	}

	// 根据条件取数!!
	public void queryDataByCondition(final String _wherecondition, String _ordercondition) {
		queryDataByDS(getDataSourceName(), getSQL(_wherecondition, _ordercondition)); //
	}

	// 根据条件取数!!!
	public BillVO[] queryBillVOsByCondition(final String _wherecondition) throws Exception {
		return UIUtil.getBillVOsByDS(getDataSourceName(), getSQL(_wherecondition), this.templetVO); //
	}

	public void queryDataByDSAndCondition(String _datasourcename, final String _wherecondition) {
		queryDataByDS(_datasourcename, getSQL(_wherecondition, this.templetVO.getOrdercondition())); //
	}

	public String getOrderCondition() {
		return this.templetVO.getOrdercondition(); //
	}

	/**
	 * 过滤条件
	 * 
	 * @param _filtercondition
	 */
	public void setDataFilterCustCondition(String _filtercondition) {
		str_dataFilterCustCondition = _filtercondition; //
	}

	public void setDataFilterCustOrCondition(String _filtercondition) {
		str_dataFilterCustOrCondition = _filtercondition; //
	}

	// 设置排序条件
	public void setOrderCustCondition(String _orderCondition) {
		str_orderCustCondition = _orderCondition; //
	}

	public String getSQL(String _condition) {
		return getSQL(_condition, null); //
	}

	public String getSQL(String _condition, String _ordercondition) {
		String str_return = "select * from " + templetVO.getTablename() + " where 1=1 "; //

		if (str_dataFilterCustCondition != null) { //如果API定义了过滤条件,则直接用它!!!!
			str_return = str_return + " and (" + str_dataFilterCustCondition + ") ";
		} else if (str_dataFilterCustOrCondition != null) {
			str_return = str_return + " or (" + str_dataFilterCustOrCondition + ") ";
		} else {
			String str_constraintFilterCondition = getDataconstraint();
			if (str_constraintFilterCondition != null) {
				str_return = str_return + " and (" + str_constraintFilterCondition + ") ";
			}
		}

		// 再把传入的参数加上
		if (_condition != null) {
			str_return = str_return + " and (" + _condition + ") ";
		}

		String str_dataPolicySQL = getDataPolicyCondition(); //资源配置!!!

		if (str_dataPolicySQL != null) {
			String str_andor = this.templetVO.getDataSqlAndOrCondition(); //有and与or两种关系! 默认是and/sunfujun/20121119
			if (str_andor.equalsIgnoreCase("or")) { //如果是or的关系,则找到原来的where位置
				int li_pos_where = str_return.indexOf(" where "); //
				String str_1 = str_return.substring(0, li_pos_where + 7); //
				String str_2 = str_return.substring(li_pos_where + 7, str_return.length()); //
				if (str_2.trim().equals("1=1")) {
					str_2 = "1=2"; //
				}
				str_return = str_1 + " ( " + str_2 + " ) or (" + str_dataPolicySQL + ")"; //
				//str_return = str_return + " " + str_andor + " (" + str_dataPolicySQL + ") ";
			} else {
				str_return = str_return + " " + str_andor + " (" + str_dataPolicySQL + ") ";
			}
		}

		if (_ordercondition != null && !_ordercondition.trim().equals("")) {
			str_return = str_return + " order by " + _ordercondition; //
		}
		return str_return; //
	}

	/**
	 * 数据权限策略生成的SQL!!最复杂,最强大的地方!!!
	 * @return
	 */
	public String getDataPolicyCondition() {
		if (this.templetVO.getDatapolicy() != null && !this.templetVO.getDatapolicy().trim().equals("")) { //如果定义数据权限定义!!则加上数据权限过滤!!
			try {
				String[] str_datapolicyCondition = getMetaDataService().getDataPolicyCondition(ClientEnvironment.getCurrLoginUserVO().getId(), this.templetVO.getDatapolicy(), this.templetVO.getDatapolicymap()); //
				str_dataPolicyInfo = str_datapolicyCondition[0]; //
				str_dataPolicySQL = str_datapolicyCondition[1]; //				
				return str_dataPolicySQL;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 根据真正的SQL取数!!
	 * 
	 * @param _sql
	 */
	public void QueryData(final String _sql) {
		queryDataByDS(getDataSourceName(), _sql);
	}

	public void queryDataByDS(final String _datasourcename, final String _sql) {
		queryDataByDS(_datasourcename, _sql, false); //
	}

	/**
	 * 真正查询数据的方法!!!!!
	 * @param _datasourcename
	 * @param _sql
	 * @param _isVewPartData
	 */
	public void queryDataByDS(final String _datasourcename, final String _sql, boolean _isRegHVOinRowNumberVO) {
		if (templetVO.getIsshowlistpagebar()) { //如果显示分页栏!!!
			li_currpage = 1; //设置当前页
			int[] li_rowsArea = new int[] { 1, li_onepagerecords }; //1-20
			queryDataByDS(_datasourcename, _sql, li_rowsArea, _isRegHVOinRowNumberVO); //查第一页
		} else {
			queryDataByDS(_datasourcename, _sql, null, _isRegHVOinRowNumberVO); //
		}
	}

	/**
	 * 查询一定范围内的数据!!
	 * @param _datasourcename
	 * @param _sql
	 * @param _rowArea
	 */
	public void queryDataByDS(final String _datasourcename, final String _sql, int[] _rowArea, boolean _isRegHVOinRowNumberVO) {
		this.stopEditing(); //
		this.str_realsql = _sql; //
		li_currsorttype = 0; //
		str_currsortcolumnkey = null; //
		clearTable(); //
		try {
			Pub_Templet_1ParVO parTempletVO = templetVO.getParPub_Templet_1VO(); //重建一个参数VO,即在服务器端许多信息是不需要的!为了减少网络网络传输,必须缩小参数大小! 这个非常重要!!
			str_calltrackmsg = null; //
			all_realValueData = getMetaDataService().getBillListDataByDS(_datasourcename, _sql, parTempletVO, true, _rowArea, _isRegHVOinRowNumberVO); //是否只显示部分数据,是否执行加载公式
			str_calltrackmsg = ClientEnvironment.getCurrCallSessionTrackMsgAndClear(); ////取得当前会话调用时服务器端执行的日志!! 如果这里能取到值,必须在服务器端调用initContext.addCurrSessionForClientTrackMsg("日志内容....");  //
			putValue(all_realValueData); //设置数据! 在有等待框时如果数据量又很大,屏幕会闪!
			if (all_realValueData != null && all_realValueData.length > 0) {
				RowNumberItemVO rowNumVO = (RowNumberItemVO) all_realValueData[0][0]; //
				li_TotalRecordCount = rowNumVO.getTotalRecordCount(); //
			} else {
				li_TotalRecordCount = 0; //
			}
			resetPageInfo(); //重置页号说明,即总共N条,N页,当前第几页
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex);
		}

		//// 设置所有行高
		if (templetVO.getIslistautorowheight()) { // 如果是自动设置行高!!
			autoSetRowHeight(); // 自动设置行高
		} else {
			String str_rowheight = templetVO.getListrowheight(); //
			if (str_rowheight != null && !str_rowheight.trim().equals("")) {
				String[] rowHeights = getTBUtil().split(str_rowheight, ","); //
				int li_rowcounts = table.getRowCount(); //
				for (int i = 0; i < rowHeights.length; i++) {
					if (i < li_rowcounts) {
						table.setRowHeight(i, Integer.parseInt(rowHeights[i]));
						rowHeaderTable.setRowHeight(i, Integer.parseInt(rowHeights[i]) + LookAndFeel.getFONT_REVISE_SIZE());
					} else {
						break;
					}
				}
			}
		}

		resetHeight(); // 重置所有行高
		autoSetTitleCheckedOrNot();//自动设置表头勾选框是否选中【李春娟/2014-05-06】

		for (int i = 0; i < v_afterqueryListeners.size(); i++) {
			BillListAfterQueryListener listener = (BillListAfterQueryListener) v_afterqueryListeners.get(i); //
			listener.onBillListAfterQuery(new BillListAfterQueryEvent(this)); //
		}
		if (null != listQueryCallback) {
			listQueryCallback.queryCallback();
		}

		//scrollPanel_main.invalidate(); //这一步与下一步一定要做否则，快点滚动时屏幕会花!
		//scrollPanel_main.repaint(); //
		centerPanel.revalidate(); //
		centerPanel.repaint(); //
	}

	/**
	 * 直接根据一个HashVO[]来查询数据!!!
	 * 按道理这种情况下分页条是没有意思的!
	 * @param _hashVOs
	 */
	public void queryDataByHashVOs(HashVO[] _hashVOs) {
		try {
			this.stopEditing(); //停止编辑
			clearTable(); //清空数据
			Pub_Templet_1ParVO parTempletVO = templetVO.getParPub_Templet_1VO(); //重建一个参数VO,即在服务器端许多信息是不需要的!为了减少网络网络传输,必须缩小参数大小! 这个非常重要!!
			all_realValueData = getMetaDataService().getBillListDataByHashVOs(parTempletVO, _hashVOs); //
			putValue(all_realValueData); //设置数据! 在有等待框时如果数据量又很大,屏幕会闪!
			centerPanel.revalidate(); //
			centerPanel.repaint(); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex);
		}
	}

	/**
	 * 结果中找 不再查数据库比较好 因为有些重写的查询事件不知道是怎么写的 就应该在现有的数据里面用代码实现
	 * 先改成这样以后可以修改 应该具体控件类型具体写逻辑
	 * 比如日历是一个时间区间你需要写不同的逻辑，多选，单选等都不同。现在只是完全匹配
	 */
	public void queryDataByResult(HashMap queryMap) {
		if (all_realValueData != null) {
			this.stopEditing(); //
			clearTable(); //
			List all_all_realValueData_List = new ArrayList();
			if (queryMap != null && queryMap.keySet().size() > 0) {
				HashMap indexK = new HashMap();
				String[] keys = (String[]) queryMap.keySet().toArray(new String[0]);
				if (keys != null) {
					for (int j = 0; j < keys.length; j++) {
						if (keys[j].startsWith("obj_")) {
							continue;
						}
						indexK.put(keys[j], (findItemPos(this.templetItemVOs, keys[j]) + 1) + "");
					}
				}
				for (int i = 0; i < all_realValueData.length; i++) {
					boolean ifok = true;
					for (int j = 0; j < keys.length; j++) {
						if (keys[j].startsWith("obj_") || indexK.get(keys[j]) == null || "-1".equals(indexK.get(keys[j])) || queryMap.get(keys[j]) == null || "".equals(queryMap.get(keys[j]))) {
							continue;
						}
						Object oo = all_realValueData[i][Integer.parseInt(indexK.get(keys[j]).toString())];
						if (oo != null) {
							String realvalue = "";
							if (oo instanceof RefItemVO) {
								realvalue = ((RefItemVO) oo).getId();

							} else if (oo instanceof ComBoxItemVO) {
								realvalue = ((ComBoxItemVO) oo).getId();
							} else if (oo instanceof StringItemVO) {
								realvalue = ((StringItemVO) oo).getStringValue();
							} else if (oo instanceof String) {
								realvalue = oo.toString();
							}
							//System.out.println(keys[j]);
							//System.out.println(indexK.get(keys[j]));
							//System.out.println(queryMap.get(keys[j]));
							//System.out.println(realvalue);
							if (!queryMap.get(keys[j]).equals(realvalue)) {
								ifok = false;
								break;
							}
						}

					}
					if (ifok) {
						all_all_realValueData_List.add(all_realValueData[i]);
					}
				}
			}
			all_realValueData = (Object[][]) all_all_realValueData_List.toArray(new Object[0][0]);
			try {
				for (int i = 0; i < all_realValueData.length; i++) {
					((RowNumberItemVO) all_realValueData[i][0]).setRecordIndex(i); //设置行号,因为后台可能经过了排序,在前台做更好!否则凡是利用知号做缓存同步的会有问题!!会影响分页等效果!!这个过程耗时极短!!可以忽略不记!!
				}
				if (jComboBox_onePageRecords.getSelectedItem().equals("全部")) {
					li_onepagerecords = all_realValueData.length;
				} else {
					//li_onepagerecords = Integer.parseInt(jComboBox.getSelectedItem().toString());  //
					li_onepagerecords = Integer.parseInt(jComboBox_onePageRecords.getEditor().getItem().toString());
				}
				putValue(all_realValueData); //设置数据
			} catch (Exception _ex) {
				MessageBox.showException(this, _ex);
			}

			// 设置所有行高
			if (templetVO.getIslistautorowheight()) { // 如果是自动设置行高!!
				autoSetRowHeight(); // 自动设置行高
			} else {
				String str_rowheight = templetVO.getListrowheight(); //
				if (str_rowheight != null && !str_rowheight.trim().equals("")) {
					String[] rowHeights = getTBUtil().split(str_rowheight, ","); //
					int li_rowcounts = table.getRowCount(); //
					for (int i = 0; i < rowHeights.length; i++) {
						if (i < li_rowcounts) {
							table.setRowHeight(i, Integer.parseInt(rowHeights[i]));
							rowHeaderTable.setRowHeight(i, Integer.parseInt(rowHeights[i]) + LookAndFeel.getFONT_REVISE_SIZE());
						} else {
							break;
						}
					}
				}
			}

			resetHeight(); // 重置所有行高
			autoSetTitleCheckedOrNot();//自动设置表头勾选框是否选中【李春娟/2014-05-06】

			for (int i = 0; i < v_afterqueryListeners.size(); i++) {
				BillListAfterQueryListener listener = (BillListAfterQueryListener) v_afterqueryListeners.get(i); //
				listener.onBillListAfterQuery(new BillListAfterQueryEvent(this)); //
			}

			Object pppp = all_realValueData; //
			centerPanel.revalidate();
			centerPanel.repaint(); //
		}
	}

	private void resetHeight() {
		int li_rows = this.getRowCount(); //
		int li_height = 85; //原来是79
		for (int i = 0; i < li_rows; i++) {
			li_height = li_height + this.getTable().getRowHeight(i) + 1; //
		}

		if (li_height < 100) {
			li_height = 100; //
		}

		if (li_height > 500) {
			li_height = 500; //
		}

		int li_width = (int) this.getPreferredSize().getWidth(); //
		this.setPreferredSize(new Dimension(li_width, li_height)); //
		//this.updateUI(); //这个会调用repaint重画整个区域，导致渐变效果无效
	}

	public void setListQueryCallback(AbstractBillListQueryCallback myAbstractBillListQueryCallback) {
		this.listQueryCallback = myAbstractBillListQueryCallback;
	}

	public AbstractBillListQueryCallback getListQueryCallback() {
		return listQueryCallback;
	}

	public void refreshData() {
		if (str_realsql != null) {
			QueryData(str_realsql); //
		} else {//应该没啥问题工作流发起时会调这个方法，如果一点击菜单进入界面没有点一下查询或者设置成自动查询在发起后列表不会刷新所以/sunfujun/20120904
			if (getQuickQueryPanel() != null) {
				getQuickQueryPanel().onQuickQuery(false);
			} else {
				QueryDataByCondition(" 1=1 ");
			}
		}
	}

	public void refreshData(boolean _isRegHVOinRowNumberVO) {
		if (str_realsql != null) {
			queryDataByDS(null, str_realsql, _isRegHVOinRowNumberVO); //
		} else {//应该没啥问题工作流发起时会调这个方法，如果一点击菜单进入界面没有点一下查询或者设置成自动查询在发起后列表不会刷新所以/sunfujun/20120904
			if (getQuickQueryPanel() != null) {
				getQuickQueryPanel().onQuickQuery(false, _isRegHVOinRowNumberVO);
			} else {
				QueryDataByCondition(" 1=1 ");
			}
		}
	}

	public void refreshCurrSelectedRow() {
		int li_selrow = this.getSelectedRow(); //取得当前选中的行!
		if (li_selrow < 0) {
			return;
		}
		try {
			RowNumberItemVO rowNoVO = (RowNumberItemVO) getValueAt(li_selrow, 0); //
			//int li_rowindex = rowNoVO.getRecordIndex(); // 
			String str_pkvalue = this.getRealValueAtModel(li_selrow, this.getTempletVO().getPkname()); //取得主键的值

			if (str_pkvalue != null) {
				//int li_index = findModelIndex(str_pkvalue); //
				Pub_Templet_1ParVO parTempletVO = templetVO.getParPub_Templet_1VO(); //重建一个参数VO,即在服务器端许多信息是不需要的!为了减少网络网络传输,必须缩小参数大小! 这个非常重要!!
				Object[][] objs = getMetaDataService().getBillListDataByDS(getDataSourceName(), "select * from " + templetVO.getTablename() + " where " + templetVO.getPkname() + "='" + str_pkvalue + "'", parTempletVO, true, null); //直接取某一条数据!!
				if (objs.length > 0) {
					//((RowNumberItemVO) objs[0][0]).setRecordIndex(li_rowindex); //行号必须是原来的,因为存在分页的问题!!
					bo_ifProgramIsEditing = true; //不想触发编辑事件!!
					for (int i = 0; i < objs[0].length; i++) {
						this.setValueAt(objs[0][i], li_selrow, i); //为当前行设置各列的值!!!
					}
					bo_ifProgramIsEditing = false; //恢复触发事件
					//all_realValueData[li_rowindex] = objs[0]; //更新缓存中数据

				}
			}
			if (null != listQueryCallback) {
				listQueryCallback.queryCallback();
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		} finally {
			bo_ifProgramIsEditing = false; //恢复触发事件
		}
	}

	public void refreshData(Object[][] _data) {
		all_realValueData = _data;
		putValue(all_realValueData); //
	}

	/**
	 * 自动设置表头勾选框是否选中!!【李春娟/2014-05-06】
	 */
	public void autoSetTitleCheckedOrNot() {
		if (isRowNumberChecked) {//如果列表中记录全部选中，则表头勾选框才选中【李春娟/2014-05-06】
			if (this.getRowCount() > 0) {
				boolean isallchecked = true;//判断列表中数据是否全部勾选了
				for (int i = 0; i < getRowCount(); i++) {
					RowNumberItemVO rowVO = getRowNumberVO(i); //
					if (rowVO != null && !rowVO.isChecked()) {
						isallchecked = false;
						break;
					}
				}
				if (isallchecked) {
					setTitleChecked(true);
				} else {
					setTitleChecked(false);
				}
			} else {
				setTitleChecked(false);
			}
		}
	}

	/**
	 * 自动设置行高!!
	 */
	public void autoSetRowHeight() {
		autoSetRowHeight(-1); //
	}

	/**
	 * 自动设置行高,但有一个上限,防止出现一个很变态的行狂高!!
	 * 
	 * @param _maxheight
	 */
	public void autoSetRowHeight(int _maxheight) {
		int li_rowcount = this.getRowCount(); //
		if (li_rowcount > 0) {
			for (int i = 0; i < li_rowcount; i++) {
				int li_height = getRowMaxHeight(i); ////取得某一行中行高..
				if (_maxheight <= 0) {
					table.setRowHeight(i, li_height + LookAndFeel.getFONT_REVISE_SIZE());
					rowHeaderTable.setRowHeight(i, li_height + LookAndFeel.getFONT_REVISE_SIZE());
				} else {
					if (li_height > _maxheight) {
						table.setRowHeight(i, _maxheight + LookAndFeel.getFONT_REVISE_SIZE());
						rowHeaderTable.setRowHeight(i, _maxheight + LookAndFeel.getFONT_REVISE_SIZE());
					} else {
						table.setRowHeight(i, li_height + LookAndFeel.getFONT_REVISE_SIZE());
						rowHeaderTable.setRowHeight(i, li_height + LookAndFeel.getFONT_REVISE_SIZE());
					}
				}
			}
		}
	}

	/**
	 * 取得总行高...
	 * 
	 * @return
	 */
	public int getAllRowHeight() {
		int li_rowcount = this.getRowCount(); //
		if (li_rowcount > 0) {
			int li_allheight = 0;
			for (int i = 0; i < li_rowcount; i++) {
				int li_rowheight = getTable().getRowHeight(i);
				li_allheight = li_allheight + li_rowheight; //
			}
			return li_allheight; //
		} else {
			return 0;
		}
	}

	/**
	 * 取得某一行的行高.
	 * @param _row
	 * @return
	 */
	private int getRowMaxHeight(int _row) {
		int li_colcount = getTable().getColumnCount(); //
		int li_initrows = 1;
		for (int i = 0; i < li_colcount; i++) {
			TableColumn column = getTable().getColumnModel().getColumn(i); //
			String str_itemkey = (String) column.getIdentifier(); //
			if (findItemType(this.templetItemVOs, str_itemkey).equals(WLTConstants.COMP_TEXTAREA)) {// 如果是多行文本框
				Object str_value = getValueAt(_row, str_itemkey); //
				if (str_value != null) {
					int li_colwidth = column.getPreferredWidth(); //宽度(多少像素)
					int li_worllength = getWordLength(str_value.toString()); //所有字符的长度(像素)，如果输入了换行则获得的行数就不准了！！！
					int li_rows = li_worllength / li_colwidth + 1; //计算总共有多少行,这里漏掉了+1，也就是需要上取整，李春娟修改！
					if (li_rows > li_initrows) {
						li_initrows = li_rows; //
					}
				}
			}
		}

		if (li_initrows > 10) { //超过10行的只显示10行,否则会出现超高的,不好看
			li_initrows = 10;
		} else if (li_initrows == 1) {
			return li_rowHeight;
		}
		return li_initrows * (li_rowHeight - 1);//多行文本框换行后，一行的高度要比22像素小些，如果在加上html链接，行高就更小了？李春娟修改！
	}

	private int getUnicodeLength(String s) { // 这个方法是指送入一个字符串,算出其字节长度,如果字符串中有个中文字符,那么他的长度就算2
		char c;
		int j = 0;
		boolean bo_1 = false;
		if (s == null || s.length() == 0) {
			return 0;
		}
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) >= 0x100) {
				bo_1 = true;
				j = j + 2;
			} else {
				j = j + 1;
			}
		}
		return j / 2; //
	}

	private int getWordLength(String s) {
		return SwingUtilities.computeStringWidth(Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font), s); //
	}

	public void setPageScrollable(boolean _enable) {
		bo_isShowPageNavigation = _enable;
		putValue(this.all_realValueData); //
	}

	public boolean getPageScrollable() {
		return bo_isShowPageNavigation;
	}

	public void refreshCurrData() {
		if (this.all_realValueData != null) {
			putValue(this.all_realValueData); //
		}
	}

	/**
	 * 送入纯粹的数据,即没有行号的
	 * @param _objs
	 */
	public void putSolidValue(Object[][] _objs) {
		if (_objs != null && _objs.length > 0) {
			Object[][] newObjs = new Object[_objs.length][_objs[0].length + 1];
			for (int i = 0; i < newObjs.length; i++) {
				newObjs[i][0] = new RowNumberItemVO(WLTConstants.BILLDATAEDITSTATE_INIT, i); //
				System.arraycopy(_objs[i], 0, newObjs[i], 1, _objs[i].length);
			}
			putValue(newObjs); //
		} else {
			clearTable();
		}
	}

	/**
	 * 向列表中塞入二维数组,一种最快的赋值方法
	 * @param _objs
	 */
	public void putValue(Object[][] _objs) {
		clearTable();
		java.util.Vector v_allRows = new java.util.Vector(); //
		if (_objs != null && _objs.length > 0) {
			for (int i = 0; i < _objs.length; i++) { //各行
				java.util.Vector v_row = new java.util.Vector(); //一行的数据!!!
				for (int j = 0; j < _objs[i].length; j++) {
					Object objj = null; //
					if (_objs[i][j] != null && (_objs[i][j] instanceof String)) {
						objj = new StringItemVO((String) _objs[i][j]);
					} else {
						objj = _objs[i][j]; //
					}
					v_row.add(objj); //
				}
				v_allRows.add(v_row); //
			}
		}
		DefaultTableModel model = (DefaultTableModel) getTable().getModel(); //
		model.getDataVector().addAll(v_allRows); //这样一次性插入更快! 否则屏幕会闪!!!以前是一行行的插入的!!
		model.fireTableRowsInserted(0, v_allRows.size() - 1); //
		getTable().invalidate(); //
		getTable().repaint(); //
		rowHeaderTable.invalidate(); //
		rowHeaderTable.repaint(); //
		scrollPanel_main.invalidate(); //这一步与下一步一定要做否则，快点滚动时屏幕会花!
		scrollPanel_main.repaint(); //
	}

	/**
	 * 将一个HashVO数组插入表格,这个HashVO数组不包括行号,所以第一列要自己加入行号
	 * 
	 * @param _hvos
	 */
	public void putValue(HashVO[] _hvos) {
		if (_hvos == null) {
			return;
		}
		clearTable();
		combine_mark = false;
		String[] str_itemkeys = this.templetVO.getItemKeys(); // 得到所有key
		String[] str_itemtype = this.templetVO.getItemTypes(); //
		all_realValueData = new Object[_hvos.length][str_itemkeys.length + 1]; //
		for (int i = 0; i < _hvos.length; i++) { // 所有数据!
			int li_row = this.addEmptyRow(false); //
			RowNumberItemVO rowNumVO = new RowNumberItemVO(); //
			rowNumVO.setState(WLTConstants.BILLDATAEDITSTATE_INIT); //
			all_realValueData[li_row][0] = rowNumVO; //

			for (int j = 0; j < str_itemkeys.length; j++) {
				String str_value = _hvos[i].getStringValue(str_itemkeys[j]); //
				if (str_value != null) {
					Object obj_value = null; //
					if (str_itemtype[j].equals(WLTConstants.COMP_TEXTFIELD) || //
							str_itemtype[j].equals(WLTConstants.COMP_NUMBERFIELD) || //
							str_itemtype[j].equals(WLTConstants.COMP_TEXTAREA) || //
							str_itemtype[j].equals(WLTConstants.COMP_CHECKBOX) //
					) {
						obj_value = new StringItemVO(str_value); //
					} else if (str_itemtype[j].equals(WLTConstants.COMP_COMBOBOX)) {
						obj_value = new ComBoxItemVO(str_value, null, str_value); //
					} else if (str_itemtype[j].equals(WLTConstants.COMP_REFPANEL) || //
							str_itemtype[j].equals(WLTConstants.COMP_REFPANEL) || //
							str_itemtype[j].equals(WLTConstants.COMP_REFPANEL_CUST) || //
							str_itemtype[j].equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //
							str_itemtype[j].equals(WLTConstants.COMP_REFPANEL_MULTI) || //
							str_itemtype[j].equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //
							str_itemtype[j].equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //
							str_itemtype[j].equals(WLTConstants.COMP_REFPANEL_TREE) || //
							str_itemtype[j].equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //
							str_itemtype[j].equals(WLTConstants.COMP_EXCEL) || //
							str_itemtype[j].equals(WLTConstants.COMP_FILECHOOSE) //增加附件类型【李春娟/2016-11-16】
					) {
						obj_value = new RefItemVO(str_value, null, str_value); //
					} else {
						obj_value = new StringItemVO(str_value); //
					}

					this.setValueAt(obj_value, li_row, str_itemkeys[j]); //
					all_realValueData[li_row][j + 1] = obj_value; //
				}
			}
		}
		combine_mark = true;
		refreshCombineData(); //combine_mark标记 批量追加后 tableModel数据变化 合并刷新 【杨科/2013-07-23】 
	}

	public Object[][] getAllValue() {
		return this.all_realValueData;
	}

	public Object[][] currPageValue() {
		return this.all_realValueData;
	}

	private void onGoToFirstPage() {
		li_currpage = 1; //
		goToPage(li_currpage);
	}

	//前一页
	private void onGoToPreviousPage() {
		if (li_currpage == 1) {
			MessageBox.show(this, "已经是第一页了!"); //
			return;
		}
		goToPage(li_currpage - 1);
	}

	private void onGoToNextPage() {
		if (li_currpage >= getTotalPages()) {
			MessageBox.show(this, "已经是最后一页了!"); //
			return;
		}
		goToPage(li_currpage + 1);
	}

	private void onGoToLastPage() {
		goToPage(getTotalPages()); //
	}

	private void onGoToPage() {
		int page = 0;
		try {
			page = Integer.parseInt(goToPageTextField.getText()); //
		} catch (Exception ex) {
			MessageBox.show(this, "请在跳转页数录入数字!");
			return;
		}
		goToPage(page);
	}

	/**
	 * 跳至某一页!
	 * @param _lipage
	 */
	public void goToPage(int _lipage) {
		goToPage(_lipage, true);
	}

	/**
	 * 跳至某一页!
	 * @param _lipage   跳转到第几页
	 * @param _msg      如果找不到是否提示，因调用该方法后有可能会自定义提示信息，所以这里增加个参数。李春娟2012-02-22增加该方法
	 */
	public void goToPage(int _lipage, boolean _msg) {
		int li_maxPages = getTotalPages(); //
		if (_lipage < 1 || _lipage > li_maxPages) {
			if (_msg) {
				MessageBox.show(this, "第[" + _lipage + "]页不存在!"); //
			}
			return;
		}
		li_currpage = _lipage; //设置当前页号!!!
		int[] li_rowsArea = null; //
		li_rowsArea = new int[] { (_lipage - 1) * li_onepagerecords + 1, _lipage * li_onepagerecords }; //1(1-20),2(21-40)
		queryDataByDS(this.getDataSourceName(), this.str_realsql, li_rowsArea, false); //查第一页
	}

	//取前当前面的所有记录数! 有客户提出行号不是1-20,而是加上前面的所有!
	public int getFrontAllRecords() {
		return (li_currpage - 1) * li_onepagerecords; //
	}

	/**
	 * 取得总计的页数!!!
	 * @return
	 */
	private int getTotalPages() {
		int li_pages = li_TotalRecordCount / li_onepagerecords; //计算总共有多少页!!!
		if (li_TotalRecordCount % li_onepagerecords != 0) {
			li_pages = li_pages + 1;
		}
		return li_pages;
	}

	/**
	 * 设置分页栏上的说明!!
	 */
	public void resetPageInfo() {
		StringBuilder sb_text = new StringBuilder(); //
		if (this.templetVO.getIsshowlistpagebar()) { //如果显示分页栏
			sb_text.append("共" + li_TotalRecordCount + "条," + "当前第" + li_currpage + "/" + getTotalPages() + "页"); //
		} else { //如果不显示分页
			sb_text.append("共" + li_TotalRecordCount + "条"); //
		}
		if (getTBUtil().getSysOptionBooleanValue("列表分页条是否有排序说明", true)) { //中铁的肖主任的UI标准没有排序条件!一定要去掉!!!实际是有更好!
			String[] str_orderInfo = getOrderConsInfo(); //
			if (!"无".equals(str_orderInfo[0])) {//如果没有排序，就不要显示"排序:无"了，如果列表按钮很多又有分页或者是引用子表感觉很乱【李春娟/2012-03-27】
				sb_text.append(",排序:"); //
				sb_text.append(str_orderInfo[0]); //
				label_pagedesc.setToolTipText("排序条件:" + str_orderInfo[1]); //
			}
		}
		label_pagedesc.setText(sb_text.toString()); //
	}

	//取得排序条件
	private String[] getOrderConsInfo() {
		String str_info = getOrderCondition(); //
		if (str_info == null || str_info.trim().equals("")) {
			return new String[] { "无", "无" };
		}
		String[] str_items = getTBUtil().split(str_info, ","); //
		StringBuilder str_infodesc = new StringBuilder(); //本身的说明,由于有时条件太多,不能显示所有,所以只显示第一个!!
		StringBuilder str_infotip = new StringBuilder(); //鼠标移上去的ToolTip说明!!
		for (int i = 0; i < str_items.length; i++) {
			String str_item = str_items[i].trim(); //
			int li_pos = str_item.indexOf(" "); //看有没有空格??
			String str_key = null; //
			boolean isDesc = false; //
			if (li_pos < 0) {
				str_key = str_item; //
			} else {
				str_key = str_item.substring(0, str_item.indexOf(" ")).trim(); //
				String str_isdesc = str_item.substring(str_item.indexOf(" "), str_item.length()).trim(); //
				if ("desc".equalsIgnoreCase(str_isdesc)) { //如果是desc
					isDesc = true; //
				}
			}
			String str_name = str_key; //
			Pub_Templet_1_ItemVO itemVO = getTempletItemVO(str_key); //
			if (itemVO != null) {
				str_name = itemVO.getItemname(); //
			}
			if (i == 0) {
				str_infodesc.append(str_name + (isDesc ? "↓" : "↑")); //
				if (str_items.length > 1) {
					str_infodesc.append("..."); //
				}
			}
			str_infotip.append(str_name + (isDesc ? "↓" : "↑")); //
			if (i != str_items.length - 1) {
				str_infotip.append(","); //
			}
		}
		return new String[] { str_infodesc.toString(), str_infotip.toString() }; //
	}

	/**
	 * 取得某一页数据
	 * 
	 * @param _lipage
	 * @return
	 */
	public Object[][] getOnePageData(int _lipage) {
		if (all_realValueData == null)
			return null;
		int li_allcount = all_realValueData.length; // 86
		int li_begin_pos = li_onepagerecords * (_lipage - 1); // 25*(1-1) = 0
		if (li_begin_pos > li_allcount - 1) {
			return null;
		}
		int li_end_pos = li_begin_pos + li_onepagerecords - 1; // 0+25-1=24
		if (li_end_pos >= li_allcount - 1) {
			li_end_pos = li_allcount - 1;
		}
		int li_rocords = li_end_pos - li_begin_pos + 1; //
		Object[][] return_objs = new Object[li_rocords][all_realValueData[0].length];
		int li_cycle = 0;
		for (int i = li_begin_pos; i <= li_end_pos; i++) {
			return_objs[li_cycle] = all_realValueData[i];
			li_cycle++;
		}
		return return_objs;
	}

	/**
	 * 取得参照说明中的SQL语句!!!
	 * 
	 * @param _allrefdesc
	 * @return
	 */
	public String getRefDescSQL(String _allrefdesc) {
		String str_type = null;
		String str_sql = null;
		int li_pos = _allrefdesc.indexOf(":"); //
		if (li_pos < 0) {
			str_type = "TABLE";
		} else {
			str_type = _allrefdesc.substring(0, li_pos).toUpperCase(); //
		}

		if (str_type.equalsIgnoreCase("TABLE")) {
			if (li_pos < 0) {
				str_sql = _allrefdesc;
			} else {
				str_sql = _allrefdesc.substring(li_pos + 1, _allrefdesc.length()); //
			}
		} else if (str_type.equalsIgnoreCase("TREE")) {
			_allrefdesc = _allrefdesc.trim(); // 截去空格
			String str_remain = _allrefdesc.substring(li_pos + 1, _allrefdesc.length()); //
			int li_pos_tree_1 = str_remain.indexOf(";"); //
			str_sql = str_remain.substring(0, li_pos_tree_1); // SQL语句
		} else if (str_type.equalsIgnoreCase("CUST")) {
		}
		return str_sql;
	}

	private int findModelIndex(String _key) {
		if (_key.equalsIgnoreCase(str_rownumberMark)) {
			return 0;
		}

		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase(_key)) {
				return i + 1;
			}
		}
		return -1;
	}

	private String findModelItemKey(int _pos) {
		return templetItemVOs[_pos - 1].getItemkey();
	}

	public String getSelectedColumnItemKey() {
		int li_column = this.getTable().getSelectedColumn(); //选中的列..
		if (li_column < 0) {
			return null; //
		}
		return (String) this.getTable().getColumnModel().getColumn(li_column).getIdentifier();
	}

	/**
	 * 清空表,但不操作数据库！以前这里有个极其严重的Bug,即以前是一行行的去删除!在数量很多时非常慢!!
	 * 要命的是当前面有个等待框时,会报大量数组溢出!!! 曾经很长时间都没搞明白这是为什么!! 后来在兴业项目中终于发现了这个问题(2010-12-24)
	 * 解决的办法是使用下面代码中最新的方法为删除数据!!! 这样删除极快!!
	 */
	public void clearTable() {
		getTable().clearSelection();
		DefaultTableModel model = (DefaultTableModel) getTable().getModel(); //
		if (model.getRowCount() <= 0) {
			return;
		}
		model.getDataVector().removeAllElements(); //一下子清除所有!
		model.fireTableRowsDeleted(0, model.getRowCount()); //
		getTable().invalidate(); //
		getTable().repaint();
		rowHeaderTable.invalidate(); //
		rowHeaderTable.repaint(); //
		scrollPanel_main.invalidate(); //这一步与下一步一定要做否则，快点滚动时屏幕会花!
		scrollPanel_main.repaint(); //
	}

	//返回所有需要加密的字段清单!!!
	public String[] getiEncryptKeys() {
		ArrayList al_keys = null; //
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getIsencrypt()) { //如果需要加密,则加入
				if (al_keys == null) {
					al_keys = new ArrayList();
				}
				al_keys.add(templetItemVOs[i].getItemkey()); //加入!
			}
		}
		if (al_keys == null) {
			return null; //
		}
		return (String[]) al_keys.toArray(new String[0]); //
	}

	/**
	 * 新增的SQL!
	 * 
	 * @return
	 */
	public String getInsertSQL(int _row) {
		return this.getBillVO(_row).getInsertSQL(getiEncryptKeys());
	}

	/**
	 * 删除的SQL!
	 * 
	 * @return
	 */
	public String getDeleteSQL(int _row) {
		return this.getBillVO(_row).getDeleteSQL();
	}

	/**
	 * 得到某一条记录的update SQL
	 * 
	 * @param _row
	 * @return
	 */
	public String getUpdateSQL(int _row) {
		return this.getBillVO(_row).getUpdateSQL(getiEncryptKeys()); //需要加密的字段要处理!
	}

	/**
	 * 得到所有的updateSQL
	 * 
	 * @return
	 */
	public String[] getUpdateSQLs() {
		int li_count = getTable().getRowCount();
		String[] str_sqls = new String[li_count];
		for (int i = 0; i < li_count; i++) {
			str_sqls[i] = getUpdateSQL(i);
		}
		return str_sqls;
	}

	public String[] getInsertSQLs() {
		int li_count = getTable().getRowCount();
		String[] str_sqls = new String[li_count];
		for (int i = 0; i < li_count; i++) {
			str_sqls[i] = getInsertSQL(i);
		}
		return str_sqls;
	}

	public String[] getUpdateSQLWithStatus() {
		int li_count = getTable().getRowCount();
		ArrayList sql = new ArrayList();
		for (int i = 0; i < li_count; i++) {
			if (this.getValueAt(i, "_RECORD_ROW_NUMBER").equals("UPDATE"))
				sql.add(getUpdateSQL(i));
		}
		return (String[]) sql.toArray(new String[0]);
	}

	public String[] getInsertSQLWithStatus() {
		int li_count = getTable().getRowCount();
		ArrayList sql = new ArrayList();
		for (int i = 0; i < li_count; i++) {
			if (this.getValueAt(i, "_RECORD_ROW_NUMBER").equals("INSERT"))
				sql.add(getInsertSQL(i));
		}
		return (String[]) sql.toArray(new String[0]);
	}

	public ArrayList getRowNumsWithStatus(String status) {
		int li_count = getTable().getRowCount();
		ArrayList temp = new ArrayList();
		for (int i = 0; i < li_count; i++) {
			if (this.getValueAt(i, "_RECORD_ROW_NUMBER").equals(status))
				temp.add(new Integer(i));
		}
		return temp;
	}

	public RowNumberItemVO getRowNumberVO(int _row) {
		return (RowNumberItemVO) getValueAt(_row, this.str_rownumberMark); //
	}

	public String getRowNumberEditState(int _row) {
		return ((RowNumberItemVO) getValueAt(_row, this.str_rownumberMark)).getState(); //
	}

	public String getEditState(int _row) {
		return getRowNumberEditState(_row); //
	}

	//获得当前选中行勾选框是否选中
	public boolean isSelectedRowChecked() {
		JComponent rowNumberCompent = this.editor.getCompent();
		boolean state = false;
		if (rowNumberCompent instanceof JCheckBox) {//如果是勾选框
			state = ((JCheckBox) rowNumberCompent).isSelected();//是否选中
		}
		return state;
	}

	//获得表头勾选框是否选中
	public boolean isTitleChecked() {
		if (isRowNumberChecked) {
			JCheckBox titleCheckBox = ((LockTableHeaderRender) rowHeaderTable.getTableHeader().getDefaultRenderer()).getCheckbox();
			if (titleCheckBox != null && titleCheckBox.isSelected()) {
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * 自动设置标题选中的勾选框是否选中，不执行公式
	 * 袁江晓 20131029 添加
	 * @param isChecked
	 */
	public void setTitleChecked(boolean isChecked) {
		if (isRowNumberChecked) {
			JCheckBox titleCheckBox = ((LockTableHeaderRender) rowHeaderTable.getTableHeader().getDefaultRenderer()).getCheckbox();
			if (titleCheckBox != null) {
				titleCheckBox.setSelected(isChecked);
				this.repaint();
				this.revalidate();
			}
		}
	}

	/**
	 * 给表头的选中添加事件
	 * 袁江晓 20131029 添加
	 */
	public void checkedTitleChanged() {
		for (int i = 0; i < v_checkedAllListeners.size(); i++) {
			BillListCheckedAllListener listener = (BillListCheckedAllListener) v_checkedAllListeners.get(i);
			listener.onBillListCheckedAll(new BillListCheckedAllEvent(this));
		}
	}

	public int getSelectedRow() {
		//此处改为默认不考虑勾选情况,原因如下:
		//1.以勾选方式选择数据, 一般是用于进行多条数据同时处理时的需求, 而"浏览","编辑","删除"等针对单一数据的操作则应该采用直接选择的方式.
		// 否则, 若采用勾选方式, 勾选了第一行(RowNum=1), 现在想浏览或编辑或删除第2行(RowNum=2)时, 首先应该取消第1行的勾选状态, 然后再勾选第2行, 这样的操作就不合理了.
		//2.如果默认为考虑勾选情况，则会出现双击不同记录，最后看到的都是同一条数据！
		//3.一般情况是没有勾选框的列表，有勾选框的情况毕竟是少数
		//同理，修改的方法有getSelectedBillVO()，getSelectedBillVOs()，getSelectedRows()【李春娟/2012-03-01】
		return getSelectedRow(false); //
	}

	public int getSelectedRow(boolean _isThinkChecked) {
		if (_isThinkChecked && this.isRowNumberChecked) {
			int[] li_rows = getCheckedRows(); //
			if (li_rows.length > 0) {
				return li_rows[0];
			} else {
				return -1; //
			}
		} else {
			return getTable().getSelectedRow();
		}
	}

	public int[] getSelectedRows() {
		//此处改为默认不考虑勾选情况,原因如下:
		//1.以勾选方式选择数据, 一般是用于进行多条数据同时处理时的需求, 而"浏览","编辑","删除"等针对单一数据的操作则应该采用直接选择的方式.
		// 否则, 若采用勾选方式, 勾选了第一行(RowNum=1), 现在想浏览或编辑或删除第2行(RowNum=2)时, 首先应该取消第1行的勾选状态, 然后再勾选第2行, 这样的操作就不合理了.
		//2.如果默认为考虑勾选情况，则会出现双击不同记录，最后看到的都是同一条数据！
		//3.一般情况是没有勾选框的列表，有勾选框的情况毕竟是少数
		//同理，修改的方法有getSelectedBillVO()，getSelectedBillVOs()，getSelectedRow()【李春娟/2012-03-01】
		return getSelectedRows(false); //
	}

	public int[] getSelectedRows(boolean _isThinkChecked) {
		if (_isThinkChecked && this.isRowNumberChecked) {
			return getCheckedRows(); //
		} else {
			return getTable().getSelectedRows();
		}
	}

	public int[] getCheckedRows() {
		ArrayList al_rows = new ArrayList(); //
		for (int i = 0; i < getRowCount(); i++) {
			RowNumberItemVO rowVO = getRowNumberVO(i); //
			if (rowVO != null && rowVO.isChecked()) {
				al_rows.add(i); //
			}
		}
		int[] li_returns = new int[al_rows.size()]; //
		for (int i = 0; i < li_returns.length; i++) {
			li_returns[i] = (Integer) al_rows.get(i); //
		}
		return li_returns; //
	}

	/**
	 * 删除选中的行
	 * 
	 * @param _rowindex
	 */
	public void removeSelectedRows() {
		int[] li_rows = getTable().getSelectedRows(); //
		removeRows(li_rows); //
	}

	public void removeSelectedRow() {
		int li_row = getTable().getSelectedRow(); //
		removeRows(new int[] { li_row }); //
	}

	public void removeRows(int[] _rowindex) {
		if (_rowindex == null || _rowindex.length == 0)
			return;
		DefaultTableModel model = (DefaultTableModel) getTable().getModel();
		Arrays.sort(_rowindex);
		for (int i = _rowindex.length - 1; i >= 0; i--) {
			this.setRowStatusAs(_rowindex[i], WLTConstants.BILLDATAEDITSTATE_DELETE);
			BillVO billVO = getBillVO(_rowindex[i]); //
			v_deleted_row.add(billVO); //
			model.removeRow(_rowindex[i]);
		}
	}

	/**
	 * 重新设置隐藏/显示列.
	 */
	public void reShowHideTableColumn() {
		String clickedItemKey = null; //
		TableColumn col = this.getClickedTableColumn();
		if (col != null) {
			clickedItemKey = (String) col.getIdentifier(); //点击的列名..
		}

		ArrayList allTempletDefineShows = new ArrayList(); //模板中定义需要显示的列!!!
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getListisshowable() != null && templetItemVOs[i].getListisshowable().booleanValue()) { //如果定义是显示的
				String str_itemkey = templetItemVOs[i].getItemkey(); //
				String str_itemname = templetItemVOs[i].getItemname(); //
				boolean bo_isvisiable = isVisible(str_itemkey); //
				if (v_lockedcolumns.contains(str_itemkey)) {
					bo_isvisiable = true; //
				}

				String[] str_item = new String[] { str_itemkey, str_itemname, bo_isvisiable ? "Y" : "N" }; //
				allTempletDefineShows.add(str_item); //
			}
		}

		String[][] str_alldata = new String[allTempletDefineShows.size()][3]; //
		for (int i = 0; i < str_alldata.length; i++) {
			str_alldata[i] = (String[]) allTempletDefineShows.get(i); //
		}

		ShowHideSortTableColumnDialog dialog = new ShowHideSortTableColumnDialog(this, "重新设置隐藏列", 325, 325, str_alldata, clickedItemKey); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			//袁江晓 20130530修改  主要针对设置的显示列、隐藏列 需要将此内容存储在本地			
			String[][] str_returnData = dialog.getReturnData(); //
			if (str_returnData != null) {
				for (int i = 0; i < str_returnData.length; i++) {
					setItemVisible(str_returnData[i][0], str_returnData[i][2].equals("Y") ? true : false); //
				}
				StringBuffer sb = new StringBuffer();//列之间用分号隔开，只存需要显示的
				for (int i = 0; i < str_returnData.length; i++) {
					setItemVisible(str_returnData[i][0], str_returnData[i][2].equals("Y") ? true : false); //
					if (str_returnData[i][2].equals("Y")) {
						sb.append(str_returnData[i][0]).append(";");
					}
				}
				CustomizeColumnMap.putValue(templetVO.getTempletcode(), sb.toString());// 写入cache
				CustomizeColumnMap.writeQuickSearchHisToCache();//将cache放入本地
			}
		}
	}

	/**
	 * 设置某一列是否显示!!!
	 * 
	 * @param _itemKey
	 * @param _visible
	 */
	public void setItemVisible(String _itemKey, boolean _visible) {
		if (_visible) {
			if (isVisible(_itemKey)) { // 如果本身就显示着,则什么都不干
			} else {
				showColumn(_itemKey); // 如果本来不显示,则显示之
			}
		} else {
			if (isVisible(_itemKey)) { // 如果本身显示着,则隐藏之
				hiddenColumn(_itemKey); //
			} else { // 如果本身就隐藏着，则什么都不干
			}
		}
	}

	public void setItemWidth(String _itemKey, int _width) {
		TableColumn column = findColumn(_itemKey);
		if (column != null) {
			column.setPreferredWidth(_width); //设置新的宽度
		}
	}

	/**
	 * 批量设置是否显示列!!
	 * 
	 * @param _itemKeys
	 * @param _visible
	 */
	public void setItemsVisible(String[] _itemKeys, boolean _visible) {
		for (int i = 0; i < _itemKeys.length; i++) {
			setItemVisible(_itemKeys[i], _visible); //
		}
	}

	/**
	 * 设置所有列是否显示或隐藏!
	 * 
	 * @param _visible
	 */
	public void setItemsAllVisible(boolean _visible) {
		String[] str_keys = getTempletVO().getItemKeys(); //
		setItemsVisible(str_keys, _visible); //
	}

	/**
	 * 隐藏某一列
	 * 
	 * @param _itemKey
	 */
	private void hiddenColumn(String _itemKey) {
		int li_count = getTable().getColumnModel().getColumnCount(); // 表中显示的所有列
		for (int i = 0; i < li_count; i++) { // 遍历
			TableColumn column = getTable().getColumnModel().getColumn(i);
			if (column.getIdentifier().toString().equalsIgnoreCase(_itemKey)) { // 如果某一列等于入参
				getTable().getColumnModel().removeColumn(column); // 删除某一列
				return; //
			}
		}
	}

	/**
	 * 隐藏某一列
	 * 
	 * @param _itemKey
	 */
	private void showColumn(String _itemKey) {
		int li_modelindex = findModelIndex(_itemKey); //
		if (li_modelindex >= 0) { // 如果找到了
			int li_itemindex = li_modelindex - 1; // 行号不算
			for (int i = li_itemindex + 1; i < templetItemVOs.length; i++) {
				if (isVisible(templetItemVOs[i].getItemkey())) { // 如果我后面的某一列是显示着的
					insertColumn(templetItemVOs[i].getItemkey(), _itemKey); // 如果找到该列就插在该列前面
					return; // 直接返回
				}
			}
			this.getColumnModel().addColumn(getTableColumn(_itemKey)); // 否则就直接插入在最后面!
		}
	}

	/**
	 * 判断某一列是否显示!!
	 * 
	 * @param _itemKey
	 * @return
	 */
	public boolean isVisible(String _itemKey) {
		int li_count = getTable().getColumnModel().getColumnCount(); // 表中显示的所有列
		for (int i = 0; i < li_count; i++) { // 遍历
			if (getTable().getColumnModel().getColumn(i).getIdentifier().toString().equalsIgnoreCase(_itemKey)) { // 如果某一列等于入参
				return true; //
			}
		}
		return false; //
	}

	private void clearAllColumn() {
		int li_columncount = getColumnModel().getColumnCount();
		for (int i = 0; i < li_columncount; i++) {
			getColumnModel().removeColumn(getColumnModel().getColumn(0));
		}
		for (int i = 0; i < templetItemVOs.length; i++) {
			templetItemVOs[i].setListisshowable(Boolean.FALSE);
		}
	}

	/**
	 * 重置排序条件!!
	 */
	private void onResetOrderCons() {
		String[] str_allViewColumns = this.templetVO.getRealViewColumns(); //所有可以匹配查询条件的列!!
		String[][] str_allViewColumnNames = new String[str_allViewColumns.length][2]; //
		for (int i = 0; i < str_allViewColumns.length; i++) {
			str_allViewColumnNames[i][0] = str_allViewColumns[i]; //第一列!!!
			Pub_Templet_1_ItemVO itemVO = getTempletItemVO(str_allViewColumns[i]); //
			if (itemVO != null) { //如果找到了
				str_allViewColumnNames[i][1] = itemVO.getItemname(); //名称
			}
		}
		String str_orderCondition = templetVO.getOrdercondition(); //实际的排序条件
		String[] str_filterkeys = (String[]) v_lockedcolumns.toArray(new String[0]); //锁定的列!!!
		ResetOrderConditionDialog dialog = new ResetOrderConditionDialog(this, str_allViewColumnNames, str_orderCondition); //
		dialog.setVisible(true);//显示!!!
		if (dialog.getCloseType() == 1) {
			String str_newCons = dialog.getReturnCons(); //
			this.templetVO.setOrdercondition(str_newCons); //重新设置内存中的排序条件!!!
			resetPageInfo(); //重新设置说明!!!
			MessageBox.show(this, "重新设置排序条件成功,请点击查询按钮重新查询!"); //
		}
	}

	//第一种编辑
	private void modifyTemplet() {
		String clicked_itemkey = (String) (getColumnModel().getColumn(getClickedColumnPos()).getIdentifier()); //
		try {
			new MetaDataUIUtil().modifyTemplet(this, this.getTempletVO().getBuildFromType(), this.getTempletVO().getBuildFromInfo(), this.getTempletVO().getTempletcode(), this.getTempletVO().getTempletname(), false, clicked_itemkey);
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
			return; //
		}
	}

	//第二种编辑!!!
	private void modifyTemplet2(String templetname) {
		try {
			new MetaDataUIUtil().modifyTemplet(this, this.getTempletVO().getBuildFromType(), this.getTempletVO().getBuildFromInfo(), this.getTempletVO().getTempletcode(), this.getTempletVO().getTempletname(), false, null);
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
			return; //
		}
	}

	//在数据库中增加工作流的4个字段!
	private void menu_db_flow_modifyTemplet_database(int _x, int _y) {
		try {
			String str_tablename = this.templetVO.getSavedtablename(); //保存的表名
			String str_sql_1 = "alter table " + str_tablename + " add billtype varchar(100)"; //
			String str_sql_2 = "alter table " + str_tablename + " add busitype varchar(100)"; //
			String str_sql_3 = "alter table " + str_tablename + " add wfprinstanceid decimal"; //
			String str_sql_4 = "alter table " + str_tablename + " add create_userid decimal"; //
			//UIUtil.executeBatchByDS(null, new String[] { str_sql_1, str_sql_2, str_sql_3, str_sql_4 }); //
			MessageBox.show("请手动执行以下SQL(根据不同的数据库修改数字类型):\r\n" + str_sql_1 + ";\r\n" + str_sql_2 + ";\r\n" + str_sql_3 + ";\r\n" + str_sql_4 + ";\r\n");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, e.getMessage());
		}
	}

	//在模板中增加工作流的四个字段
	private void menu_db_flow_modifyTemplet(int _x, int _y) {
		try {
			StringBuilder sb_text = new StringBuilder(); //
			sb_text.append("请在模板中手工增加以下4个字段:\r\n"); //
			sb_text.append("billtype 文本框,根据需要指定默认值\r\n"); //
			sb_text.append("busitype 文本框,根据需要指定默认值\r\n"); //
			sb_text.append("wfprinstanceid 文本框\r\n"); //
			sb_text.append("create_userid 数字框,默认值是登录人员id\r\n"); //
			MessageBox.show(sb_text.toString()); //
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, e.getMessage());
		}
	}

	//在模板中增加流程监控的13个字段
	private void menu_db_addflow13Field_templet(int _x, int _y) {
		if (!checkIsCanConfigTemplet(true)) { //看是否可编辑配置
			return;
		}
		try {
			String[][] str_items = new String[][] { { "task_curractivityname", "当前步骤", "N" }, //任务的待办箱与已办箱中都需要!
					{ "task_creatername", "提交人", "N" }, { "task_createtime", "提交时间", "N" }, { "task_createrdealmsg", "提交意见", "N" }, //待办箱
					{ "task_realdealusername", "实际处理人", "N" }, { "task_dealtime", "处理时间", "N" }, { "task_dealmsg", "处理意见", "N" }, //已办箱
					{ "prins_curractivityname", "当前步骤", "Y" }, { "prins_lastsubmitername", "最后处理人", "N" }, { "prins_lastsubmittime", "最后处理时间", "N" }, { "prins_lastsubmitmsg", "最后处理人意见", "N" }, { "prins_mylastsubmittime", "我的最后处理时间", "Y" }, { "prins_mylastsubmitmsg", "我的最后处理意见", "Y" } //从单据出发,直接关联流程实例的相关字段!
			}; ////

			StringBuilder sb_msg = new StringBuilder(); //
			for (int i = 0; i < str_items.length; i++) {
				sb_msg.append(str_items[i][0] + "/" + str_items[i][1] + "\n"); //
			}
			if (JOptionPane.showConfirmDialog(this, "您确定要在模板中增加12个流程监控字段吗?这将增加以下字段:\r\n" + sb_msg.toString(), "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			String str_retult = UIUtil.getMetaDataService().insertTempletItem(this.templetVO.getPk_pub_templet_1(), str_items); //
			MessageBox.show("导入成功!结果:" + str_retult); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}

	}

	private void menu_transportToDataSource() {
		try {
			DataTransport.createDataTransport(this).transportXMLTempletDialog(new String[] { this.str_templetcode });
		} catch (WLTRemoteException e) {
			e.printStackTrace();
			MessageBox.show(this, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, e.getMessage());
		}
	}

	/**
	 * 判断是否可以配置模板,即如果是Class类型的,则不可配置,如果是XML类型的,则需要复制到DB中进行配置!! 如果是DB的,则分：如果XML中也有,则可以将XML中搞过来比较,也可以直接删除自己而使用XML中的! 如果XML中没有则是以前的逻辑!! 
	 * @return
	 */
	private boolean checkIsCanConfigTemplet(boolean _isquiet) {
		return checkIsCanConfigTemplet(_isquiet, null);
	}

	private boolean checkIsCanConfigTemplet(boolean _isquiet, String _colKey) {
		try {
			String str_buildFromType = templetVO.getBuildFromType(); //创建的类型!!
			String str_buildFromInfo = templetVO.getBuildFromInfo(); //创建的信息!!
			String str_templetCode = templetVO.getTempletcode(); //模板编码
			String str_templetName = templetVO.getTempletname();
			return new MetaDataUIUtil().checkTempletIsCanConfig(this, str_buildFromType, str_buildFromInfo, str_templetCode, str_templetName, _isquiet, _colKey); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
			return false; //
		}
	}

	/*
	 * 自动生成Demo数据
	 */
	public void menu_db_AutoBuildData() {
		new AutoBuildDataDialog(this, templetVO, templetItemVOs);
	}

	/**
	 * 重新加载页面
	 */
	public void reload() {
		reload(this.str_templetcode); //
	}

	/**
	 * 重新加载页面
	 */
	public void reload(String _templetcode) {
		allTableColumns = null;
		tableModel = null; //
		columnModel = null; //
		table = null; //
		toolbarPanel = null; //
		pagePanel = null;
		init(_templetcode, true); //
	}

	private int getClickedColumnPos() {
		Point point = new Point(li_mouse_x, li_mouse_y);
		int li_pos = getTable().getTableHeader().columnAtPoint(point);
		return li_pos;
	}

	private TableColumn getClickedTableColumn() {
		int li_pos = getClickedColumnPos();
		if (li_pos >= 0) {
			return getColumnModel().getColumn(li_pos);
		} else {
			return null; //
		}
	}

	private void lockColumn() {
		lockColumn(getClickedColumnPos());
	}

	public void setUnEditable() {
		setAllItemValue("listiseditable", WLTConstants.BILLCOMPENTEDITABLE_NONE);
	}

	private void lockColumn(int _pos) {
		v_lockedcolumns.removeAllElements(); // 清空数组
		String[] str_keys = new String[_pos + 1];
		int li_newwidth = 0;
		for (int i = 0; i <= _pos; i++) {
			TableColumn tableColumn = getColumnModel().getColumn(i);
			li_newwidth = li_newwidth + tableColumn.getWidth();
			str_keys[i] = (String) tableColumn.getIdentifier();
			v_lockedcolumns.add(str_keys[i]);
			int li_modexindex = findModelIndex(str_keys[i]);
			rowHeaderColumnModel.addColumn(getTableColumns()[li_modexindex - 1]);
		}
		ListSelectionModel lsm_table = rowHeaderTable.getSelectionModel();
		rowHeaderTable = new ResizableTable(getTableModel(), rowHeaderColumnModel); // 创建新的表..
		rowHeaderTable.setGridColor(LookAndFeel.tableHeadLineClolr); //
		rowHeaderTable.setRowHeight(li_rowHeight); //
		rowHeaderTable.setRowSelectionAllowed(true);
		rowHeaderTable.setColumnSelectionAllowed(false);
		rowHeaderTable.getTableHeader().setReorderingAllowed(false);
		rowHeaderTable.getTableHeader().setResizingAllowed(false);
		LockTableHeaderRender lockTHR = new LockTableHeaderRender(this, rowHeaderTable);
		rowHeaderTable.getTableHeader().setDefaultRenderer(lockTHR); //
		rowHeaderTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		rowHeaderTable.setSelectionModel(lsm_table);
		rowHeaderTable.setOpaque(false); //
		for (int i = 0; i < rowHeaderTable.getRowCount(); i++) {
			rowHeaderTable.setRowHeight(i, table.getRowHeight(i)); //
		}
		rowHeaderTable.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				Point point = new Point(evt.getX(), evt.getY());
				int li_pos = rowHeaderTable.getTableHeader().columnAtPoint(point);
				if (li_pos != 0) {
					showPopMenu2(evt.getComponent(), evt.getX(), evt.getY()); // 弹出菜单
				}
			}

		});

		if (getTable().getSelectedRow() >= 0) {
			rowHeaderTable.setRowSelectionInterval(getTable().getSelectedRow(), getTable().getSelectedRow());
		}

		rowHeaderTable.setSelectionModel(getTable().getSelectionModel()); // 解决解锁后事件消失的问题!!
		jv = new JViewport();
		jv.setOpaque(false); //
		jv.setView(rowHeaderTable);
		int li_height = new Double(rowHeaderTable.getMaximumSize().getHeight()).intValue();
		jv.setPreferredSize(new Dimension(li_newwidth + li_rowno_width, li_height));
		scrollPanel_main.setRowHeader(jv);
		scrollPanel_main.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeaderTable.getTableHeader());
		scrollPanel_main.updateUI(); //
		for (int i = 0; i <= _pos; i++) {
			this.deleteColumn(str_keys[i]);
		}
		bo_tableislockcolumn = true;

		refreshCombineData_header(_pos); //锁定 合并刷新 【杨科/2013-07-23】 
	}

	private void unlockColumn() {
		int li_count = rowHeaderTable.getColumnModel().getColumnCount();
		for (int i = li_count - 1; i >= 1; i--) {
			TableColumn column = rowHeaderTable.getColumnModel().getColumn(i); //
			String str_key = (String) column.getIdentifier();
			this.insertColumn(0, str_key);
		}

		for (int i = li_count - 1; i >= 1; i--) {
			TableColumn column = rowHeaderTable.getColumnModel().getColumn(i); //
			rowHeaderColumnModel.removeColumn(column);
		}

		for (int i = 0; i < rowHeaderTable.getRowCount(); i++) {
			rowHeaderTable.setRowHeight(i, table.getRowHeight(i)); //
		}
		int li_height = new Double(rowHeaderTable.getMaximumSize().getHeight()).intValue();
		scrollPanel_main.getRowHeader().setPreferredSize(new Dimension(li_rowno_width, li_height));
		scrollPanel_main.updateUI(); //
		v_lockedcolumns.removeAllElements(); // 清空数组
		bo_tableislockcolumn = false;

		refreshCombineData(); //解锁 合并刷新 【杨科/2013-07-23】 
	}

	public JScrollPane getMainScrollPane() {
		return scrollPanel_main;
	}

	public JViewport getMainScrollPaneViewPort() {
		return scrollPanel_main.getViewport();
	}

	private void showPieChart() {
		int li_clickedColumnpos = getClickedColumnPos();
		TableColumn column = getClickedTableColumn();
		// String str_key = (String) column.getIdentifier();
		String str_name = (String) column.getHeaderValue();

		ShowColumnDataAsPieChartDialog dialog = new ShowColumnDataAsPieChartDialog(this, str_name, getColumnValues(li_clickedColumnpos));
		dialog.setVisible(true);
	}

	//查看帮助..
	private void onLookHelp() {
		int li_clickedColumnpos = getClickedColumnPos();
		TableColumn column = getClickedTableColumn();
		String str_key = (String) column.getIdentifier();
		Pub_Templet_1_ItemVO itemVO = getTempletItemVO(str_key); //
		String str_tiptext = itemVO.getItemtiptext(); //
		if (str_tiptext == null || str_tiptext.trim().equals("")) {
			str_tiptext = itemVO.getItemname(); //

		}
		if (str_tiptext == null) {
			str_tiptext = "";
		}
		JEditorPane editPanel = new JEditorPane("text/html", str_tiptext); //
		editPanel.setFont(LookAndFeel.font); //
		editPanel.setEditable(false); //
		editPanel.setBackground(LookAndFeel.systembgcolor); //
		BillDialog dialog = new BillDialog(this, "查看【" + itemVO.getItemname() + "】帮助", 800, 200); //窗口搞大点【xch/2012-03-07】
		dialog.getContentPane().add(new JScrollPane(editPanel)); ////
		dialog.setVisible(true); //
	}

	public String[] getColumnValues(int _index) {
		int li_rowcount = getTable().getRowCount();
		String[] str_data = new String[li_rowcount];
		for (int i = 0; i < li_rowcount; i++) {
			str_data[i] = String.valueOf(getValueAtTable(i, _index));
		}
		return str_data;
	}

	/**
	 * 在某一位置插入一列,
	 * 
	 * @param _columnindex
	 *            想要插入的表中列的位置
	 * @param _modelindex
	 *            插入数据的哪一列
	 */
	public void insertColumn(int _columnindex, int _modelindex) {
		TableColumn column = getTableColumns()[_modelindex]; // 找到那一列
		this.getColumnModel().addColumn(column);
		this.getColumnModel().moveColumn(table.getColumnCount() - 1, _columnindex); // 移动列顺序
	}

	/**
	 * 在某一位置插入一列,
	 * 
	 * @param _columnindex
	 *            想要插入的表中列的位置
	 * @param _modelindex
	 *            插入数据的哪一列
	 */
	public void insertColumn(int _columnindex, String _modelkey) {
		int li_modelindex = findModelIndex(_modelkey); //
		TableColumn column = getTableColumns()[li_modelindex - 1]; // 找到那一列
		this.getColumnModel().addColumn(column);
		this.getColumnModel().moveColumn(table.getColumnCount() - 1, _columnindex);
	}

	/**
	 * 在某一旬的前面插入一列
	 * 
	 * @param _columnkey
	 *            某一列
	 * @param _modelkey
	 *            需要插入的列!!
	 */
	public void insertColumn(String _columnkey, String _modelkey) {
		int li_columnindex = findColumnIndex(_columnkey); // 列中的位置
		TableColumn column = getTableColumn(_modelkey); // 找到那一列
		this.getColumnModel().addColumn(column); // 先在最后一列增加一列
		this.getColumnModel().moveColumn(table.getColumnCount() - 1, li_columnindex); // 将最后一列移到相应的位置
	}

	private TableColumn getTableColumn(String _itemKey) {
		for (int i = 0; i < getTableColumns().length; i++) {
			if (getTableColumns()[i].getIdentifier().toString().equalsIgnoreCase(_itemKey)) {
				return getTableColumns()[i];
			}
		}
		return null;
	}

	/**
	 * 删除某一列,显示的列
	 * 
	 * @param _index
	 */
	public void deleteColumn(int _index) {
		TableColumn column = this.getColumnModel().getColumn(_index);
		this.getColumnModel().removeColumn(column);
	}

	/**
	 * 删除某一列,根据key
	 * 
	 * @param _key
	 */
	public void deleteColumn(String _key) {
		hiddenColumn(_key); //
	}

	/**
	 * 显示列表数据!!
	 */
	public void mouseDoubleClicked(MouseEvent e) {
		int li_row = this.getSelectedRow(); //
		if (li_row < 0) {
			return;
		}
		BillVO billVO = this.getBillVO(li_row); //
		if (v_MouseDoubleClickListeners.size() > 0) { //如果有有监听了双击事件
			for (int i = 0; i < v_MouseDoubleClickListeners.size(); i++) {
				BillListMouseDoubleClickedListener listener = (BillListMouseDoubleClickedListener) v_MouseDoubleClickListeners.get(i); //
				BillListMouseDoubleClickedEvent event = new BillListMouseDoubleClickedEvent(li_row, billVO, this); //
				listener.onMouseDoubleClicked(event); //
			}
			return; //
		}

		if (!isCanShowCardInfo) {
			return;
		}

		int li_column = this.getTable().getSelectedColumn(); // 选中的列
		//啥也没选
		if (li_column < 0) {
			return;
		}

		String itemkey = (String) this.getTable().getColumnModel().getColumn(li_column).getIdentifier(); //
		Pub_Templet_1_ItemVO itemVO = getTempletItemVO(itemkey); //
		String itemtype = itemVO.getItemtype(); //
		Boolean bo_ishtml = itemVO.getListishtmlhref(); //
		if (bo_ishtml || itemtype.equals(WLTConstants.COMP_BUTTON) || itemtype.equals(WLTConstants.COMP_FILECHOOSE)) { // 如果HTML超链接显示，或者是按钮，或者是文件选择框,则返回，不做事件处理
			return; //
		}

		int li_mouselistenercount = table.getMouseListeners().length; //
		if (li_mouselistenercount > 5) {
			return; // 如果有两个以上的监听者则不做
		}

		boolean isShowCard = new TBUtil().getSysOptionBooleanValue("双击列表是否弹出卡片", true); //默认是弹出
		if (isShowCard) {
			cardShowInfo();
		}
		//cardShowInfo(); //卡片显示!!大量客户提出双击弹出卡片并不好,有点觉得莫名其妙,有点甚至作出Bug提出!所以还是去掉为妙!因为可以通过点击右键选择【卡片浏览】实现该效果!!
	}

	public void setCanShowCardInfo(boolean _bo) {
		isCanShowCardInfo = _bo; //
	}

	public void cardShowInfo() {
		BillCardPanel cardPanel = new BillCardPanel(this.templetVO); //
		BillVO billVO = this.getSelectedBillVO(); //
		cardPanel.setBillVO(billVO); //
		if (this.getBillListBtn("comm_listselect2") != null && "浏览".equals(this.getBillListBtn("comm_listselect2").getName())) {
			Pub_Templet_1_ItemVO[] templetItemVOs = cardPanel.getTempletVO().getItemVos();
			for (int i = 0; i < templetItemVOs.length; i++) { //遍历所有控件!!
				final String str_itemkey = templetItemVOs[i].getItemkey();
				String str_type = templetItemVOs[i].getItemtype();
				if (str_type.equals(WLTConstants.COMP_LABEL)) { //Label
				} else if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || str_type.equals(WLTConstants.COMP_NUMBERFIELD) || str_type.equals(WLTConstants.COMP_PASSWORDFIELD)) { //文本框,数字框,密码框
					cardPanel.execEditFormula(str_itemkey);
				} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { //下拉框
					cardPanel.execEditFormula(str_itemkey);
				} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || //表型参照1
						str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || //树型参照1
						str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //多选参照1
						str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || //自定义参照
						str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //列表模板参照
						str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //树型模板参照
						str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //注册样板参照
						str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //注册参照
						str_type.equals(WLTConstants.COMP_DATE) || //日历
						str_type.equals(WLTConstants.COMP_DATETIME) || //时间
						str_type.equals(WLTConstants.COMP_BIGAREA) || //大文本框
						//str_type.equals(WLTConstants.COMP_FILECHOOSE) || //文件选择框!!!（以前也是当一个参照处理,但后来无数处用户要求将其拿到主页面上来,以符合大量网页系统的效果,少点一次,）
						str_type.equals(WLTConstants.COMP_COLOR) || //颜色选择框
						str_type.equals(WLTConstants.COMP_CALCULATE) || //计算器
						str_type.equals(WLTConstants.COMP_PICTURE) || //图片选择框
						str_type.equals(WLTConstants.COMP_EXCEL) || //EXCEL
						str_type.equals(WLTConstants.COMP_OFFICE) //office
				) { //如果是各种参照
					cardPanel.execEditFormula(str_itemkey);
				} else if (str_type.equals(WLTConstants.COMP_TEXTAREA)) { //多行文本框
					cardPanel.execEditFormula(str_itemkey);
				} else if (str_type.equals(WLTConstants.COMP_BUTTON)) { //按钮
					cardPanel.execEditFormula(str_itemkey);
				} else if (str_type.equals(WLTConstants.COMP_CHECKBOX)) { //勾选框
					cardPanel.execEditFormula(str_itemkey);
				} else {
					continue; //
				}
			}
		}
		String str_recordName = billVO.toString(); //
		BillCardFrame frame = new BillCardFrame(this, this.templetVO.getTempletname() + "[" + str_recordName + "]", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT, true); //
		frame.setVisible(true); //
	}

	/**
	 * 直接删除记录.
	 */
	private void dirDeleteRecord() {
		int[] li_rows = this.getSelectedRows(); //
		if (li_rows.length == 0) {
			MessageBox.showSelectOne(this);
			return;
		}

		String str_tableName = this.getTempletVO().getSavedtablename(); //
		String str_pkName = this.getTempletVO().getPkname(); //
		if (str_tableName == null || str_tableName.trim().equals("") || str_pkName == null || str_pkName.trim().equals("")) {
			MessageBox.show(this, "存储的表名【" + str_tableName + "】与主键字段名【" + str_pkName + "】都不能为空！");
			return;
		}

		String[] str_sqls = new String[li_rows.length]; //
		for (int i = 0; i < li_rows.length; i++) {
			String str_pkValue = getRealValueAtModel(li_rows[i], str_pkName); //
			if (str_pkValue == null || str_pkValue.trim().equals("")) {
				MessageBox.show(this, "当前记录的主键字段上的值为空,不能进行此操作！");
				return;
			}
			str_sqls[i] = "delete from " + str_tableName.trim() + " where " + str_pkName + "='" + str_pkValue + "'";
		}

		if (JOptionPane.showConfirmDialog(this, "你是否真的想删除选中的记录吗？\r\n这将执行SQL【" + str_sqls[0] + "】等共【" + str_sqls.length + "】条记录\r\n请确认当前记录的确来自于保存表中，同时注意是否还有其他关联关系需要处理！\r\n当前的查询SQL是：\r\n" + this.str_realsql + "", "提示", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			try {
				UIUtil.executeBatchByDS(getDataSourceName(), str_sqls);
				this.removeSelectedRows(); //从页面上删除
				MessageBox.show(this, "删除当前记录成功，共删除【" + str_sqls.length + "】条记录！");
			} catch (Exception _ex) {
				MessageBox.showException(this, _ex);
			}
		}
	}

	//直接修改记录,在实施过程中快速修改数据
	private void dirUpdateRecord() {
		try {
			int li_selectedRow = this.getSelectedRow(); //
			if (li_selectedRow < 0) {
				MessageBox.showSelectOne(this);
				return;
			}
			String str_itemKey = getSelectedColumnItemKey(); //
			if (str_itemKey == null) {
				MessageBox.show(this, "请选择一列执行此操作!"); ////
				return;
			}

			String str_tableName = this.getTempletVO().getSavedtablename(); //
			String str_pkName = this.getTempletVO().getPkname(); //
			if (str_tableName == null || str_tableName.trim().equals("") || str_pkName == null || str_pkName.trim().equals("")) {
				MessageBox.show(this, "存储的表名【" + str_tableName + "】与主键字段名【" + str_pkName + "】都不能为空！");
				return;
			}
			String str_pkValue = getRealValueAtModel(li_selectedRow, str_pkName); //主键字段名
			String str_selValue = getRealValueAtModel(li_selectedRow, str_itemKey); //选中格子的值!!

			JPanel panel = new JPanel(); //
			panel.setLayout(null); //
			JLabel label = new JLabel("表[" + str_tableName + "]中字段[" + str_itemKey + "]新值:"); //
			JTextArea textArea = new WLTTextArea(str_selValue); //
			textArea.setFont(LookAndFeel.font);
			JScrollPane scrollPanel = new JScrollPane(textArea); //
			JTextField textField = new JTextField(); //
			textField.setEditable(false); //
			textField.setBackground(Color.LIGHT_GRAY); //
			textField.setOpaque(false); //
			textField.setBorder(BorderFactory.createEmptyBorder()); ////

			label.setBounds(0, 5, 400, 20); //
			scrollPanel.setBounds(0, 25, 400, 100); //
			textField.setBounds(0, 125, 400, 20); //

			panel.add(label); //
			panel.add(scrollPanel); //
			panel.add(textField); //

			panel.setPreferredSize(new Dimension(400, 145)); ////

			textField.setText("update " + str_tableName + " set " + str_itemKey + "='{新值}' where " + str_pkName + "='" + str_pkValue + "'"); //
			if (JOptionPane.showConfirmDialog(this, panel, "您确定要直接修改该行记录的[" + str_itemKey + "]值吗?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			String str_newValue = textArea.getText(); //
			String str_sql = "update " + str_tableName + " set " + str_itemKey + "='" + str_newValue + "' where " + str_pkName + "='" + str_pkValue + "'"; //
			int li_rows = UIUtil.executeUpdateByDS(null, str_sql); //
			MessageBox.show(this, "修改数据成功!共更新了【" + li_rows + "】条记录!"); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex);
		}
	}

	/**
	 * 直接清空工作流相关信息!
	 */
	private void dirDelWorkFlowInfo() {
		try {
			int li_row = this.getSelectedRow(); //
			if (li_row < 0) {
				MessageBox.showSelectOne(this);
				return;
			}

			BillVO billVO = this.getSelectedBillVO(); //
			if (!billVO.containsKey("wfprinstanceid")) { //
				MessageBox.show(this, "模板没有字段[wfprinstanceid],说明不是走工作流的表,不能执行此操作!");
				return;
			}

			String str_prinstanceid = billVO.getStringValue("wfprinstanceid"); //流程实例
			if (str_prinstanceid == null || str_prinstanceid.trim().equals("")) {
				MessageBox.show(this, "字段[wfprinstanceid]值为空,说明流程还没启动,没有必要执行此操作!");
				return;
			}
			String str_tabName = billVO.getSaveTableName().toLowerCase(); //保存表名
			String str_pkName = billVO.getPkName().toLowerCase(); //主键字段名!
			String str_pkValue = billVO.getPkValue(); //主键字段值!

			String str_sql_1 = "delete from pub_wf_prinstance where rootinstanceid='" + str_prinstanceid + "'";
			String str_sql_2 = "delete from pub_wf_dealpool   where rootinstanceid='" + str_prinstanceid + "'";
			String str_sql_3 = "delete from pub_task_deal     where rootinstanceid='" + str_prinstanceid + "'";
			String str_sql_4 = "delete from pub_task_off      where rootinstanceid='" + str_prinstanceid + "'";
			String str_sql_5 = "update " + str_tabName + " set wfprinstanceid=null where " + str_pkName + "='" + str_pkValue + "'"; //

			String str_msg = "您确定要执行此操作吗?这将清除该记录所有走过的工作流信息,会执行以下5条SQL:\n" + str_sql_1 + ";\n" + str_sql_2 + ";\n" + str_sql_3 + ";\n" + str_sql_4 + ";\n" + str_sql_5 + ";\n请确认您是否真的想做此事!";

			JPanel panel = new JPanel(); //
			panel.setLayout(null); //
			JTextArea textArea = new JTextArea(str_msg); //
			textArea.setFont(LookAndFeel.font); //
			textArea.setEditable(false); //
			JScrollPane scrollPanel = new JScrollPane(textArea); //
			scrollPanel.setBounds(0, 5, 475, 125); //
			panel.add(scrollPanel); //
			panel.setPreferredSize(new Dimension(475, 130)); ////
			if (JOptionPane.showConfirmDialog(this, panel, "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}

			UIUtil.executeBatchByDS(null, new String[] { str_sql_1, str_sql_2, str_sql_3, str_sql_4, str_sql_5 }); //
			this.setValueAt(null, li_row, "wfprinstanceid"); //清空数据
			MessageBox.show(this, "清空工作流相关数据成功,这条单据已回到初始录入状态!!\n现在拟稿人可以在草稿箱查询到该记录,然后重新提交!"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}

	}

	/**
	 * 显示数据库中存的实际数据!!!
	 * 
	 */
	private void showPopMenu(MouseEvent e, boolean _isadmin) {
		//table.clearSelection();  //
		Point p = e.getPoint();
		int row = table.rowAtPoint(p);
		int col = table.columnAtPoint(p); //
		if (table.getSelectedRows().length <= 1) { //如果没有选中行,或选中的行只有一个,则重置选中的行,否则如果已选中多行，则不做处理!
			table.setRowSelectionInterval(row, row); //
		}

		if (col >= 0) {
			table.setColumnSelectionInterval(col, col); //设置选中的列,为了右键修改值使用!
		}

		if (!isCanShowCardInfo) {
			menuItem_showCard.setEnabled(false); //
		}
		rightPopMenu.show(table, e.getX(), e.getY()); //
	}

	/**
	 * 显示页面上的实际数据
	 */
	public void showUIRecord() {
		int li_row = getSelectedRow();
		if (li_row < 0) {
			return;
		}

		String[] str_tabcolumnnames = new String[] { "ItemKey", "ItemName", "类型", "实际值类型", "实际值内容" }; //
		String[] str_itemkeys = this.templetVO.getItemKeys(); //
		String[] str_itemnames = this.templetVO.getItemNames(); //
		String[] str_itemtypes = this.templetVO.getItemTypes(); //
		String[] str_valuetype = new String[str_itemkeys.length]; //
		String[] str_valuedata = new String[str_itemkeys.length]; //
		for (int i = 0; i < str_itemkeys.length; i++) {
			Object obj = this.getValueAt(li_row, str_itemkeys[i]); // /
			if (obj != null) {
				str_valuetype[i] = obj.getClass().getName(); //
				if (obj instanceof ComBoxItemVO) {
					ComBoxItemVO itemVO = (ComBoxItemVO) obj; //
					str_valuedata[i] = "id=[" + itemVO.getId() + "],code=[" + itemVO.getCode() + "],name=[" + itemVO.getName() + "]"; //
				} else if (obj instanceof RefItemVO) {
					RefItemVO itemVO = (RefItemVO) obj; //
					str_valuedata[i] = "id=[" + itemVO.getId() + "],code=[" + itemVO.getCode() + "],name=[" + itemVO.getName() + "]"; //
				} else {
					str_valuedata[i] = obj.toString(); //
				}
			}
		}

		String[][] str_tabdata = new String[str_itemkeys.length][5]; //
		for (int i = 0; i < str_itemkeys.length; i++) {
			str_tabdata[i][0] = str_itemkeys[i];
			str_tabdata[i][1] = str_itemnames[i];
			str_tabdata[i][2] = str_itemtypes[i];
			str_tabdata[i][3] = str_valuetype[i];
			str_tabdata[i][4] = str_valuedata[i];
		}

		JTable tmptable = new JTable(str_tabdata, str_tabcolumnnames); //
		tmptable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //

		tmptable.getColumnModel().getColumn(0).setPreferredWidth(100);
		tmptable.getColumnModel().getColumn(1).setPreferredWidth(100);
		tmptable.getColumnModel().getColumn(2).setPreferredWidth(100);
		tmptable.getColumnModel().getColumn(3).setPreferredWidth(255);
		tmptable.getColumnModel().getColumn(4).setPreferredWidth(150);

		JPanel contentPanel = new JPanel(new BorderLayout()); //
		//RowNumber数据!!
		RowNumberItemVO rowNumberVO = getRowNumberVO(li_row); //
		HashVO rowHashVO = (rowNumberVO == null ? null : rowNumberVO.getRecordHVO()); //行号中的HashVO
		if (rowHashVO != null) {
			JTabbedPane tabbedPane = new JTabbedPane(); //
			tabbedPane.addTab("记录中内容", UIUtil.getImage("office_042.gif"), new JScrollPane(tmptable)); //

			//构建行号中内容的表格!!!
			String[] str_rowKeys = rowHashVO.getKeys(); //
			String[][] str_rowNumData = new String[str_rowKeys.length][2]; //
			for (int i = 0; i < str_rowKeys.length; i++) {
				str_rowNumData[i][0] = str_rowKeys[i]; //
				str_rowNumData[i][1] = rowHashVO.getStringValue(str_rowKeys[i], ""); //
			}
			JTable rowDataTable = new JTable(str_rowNumData, new String[] { "Key", "Value" }); //
			rowDataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //
			rowDataTable.getColumnModel().getColumn(0).setPreferredWidth(250);
			rowDataTable.getColumnModel().getColumn(1).setPreferredWidth(350);
			tabbedPane.addTab("行号中的内容", UIUtil.getImage("office_157.gif"), new JScrollPane(rowDataTable)); //
			contentPanel.add(tabbedPane, BorderLayout.CENTER); //
		} else {
			contentPanel.add(new JScrollPane(tmptable), BorderLayout.CENTER); //
		}
		JLabel label_warn = new JLabel("★★★如果发现某个字段数据为空,则千万要注意可能是从某个视图查询造成的!"); //
		label_warn.setBackground(Color.RED); //
		label_warn.setForeground(Color.WHITE); //
		label_warn.setOpaque(true); //
		contentPanel.add(label_warn, BorderLayout.NORTH); //

		JLabel label_warn2 = new JLabel("★★★如果感觉某个字段数据不对,则注意使用另一个【查看DB实际数据】功能进行比较!"); //
		label_warn2.setBackground(Color.RED); //
		label_warn2.setForeground(Color.WHITE); //
		label_warn2.setOpaque(true); //
		contentPanel.add(label_warn2, BorderLayout.SOUTH); //

		BillDialog dialog = new BillDialog(this, "查看UI实际数据", 800, 350); //
		dialog.getContentPane().add(contentPanel); //
		dialog.setVisible(true); //
	}

	public void showDBRecord() {
		int li_row = getSelectedRow();
		if (li_row < 0) {
			return;
		}
		if (templetVO.getTablename() == null || templetVO.getTablename().trim().equals("")) {
			MessageBox.show(this, "模板中没有定义查询表名,不能查看"); //
			return; //
		}
		if (templetVO.getPkname() == null || templetVO.getPkname().length() <= 0) {
			try {
				String[] allValues = new String[templetVO.getRealViewItemVOs().length];
				for (int i = 0; i < templetVO.getRealViewItemVOs().length; i++) {
					allValues[i] = String.valueOf(getRealValueAtModel(li_row, templetVO.getItemKeys()[i]));
				}
				new RecordShowDialog(this, templetVO.getTablename(), templetVO.getItemKeys(), allValues);
			} catch (Exception _ex) {
				MessageBox.showException(this, _ex); //
			}
		} else {
			try {
				String pk_value = "" + this.getValueAt(li_row, templetVO.getPkname()); //
				new RecordShowDialog(this, templetVO.getTablename(), templetVO.getPkname(), pk_value);
			} catch (Exception _ex) {
				MessageBox.showException(this, _ex); //
			}
		}
	}

	/**
	 * 快速定位...
	 */
	public void quickSearch() {
		int li_colIndex = getClickedColumnPos(); //
		TableColumn col = this.getClickedTableColumn(); //点击的列
		String clickedItemName = getColumnName(li_colIndex); //
		int li_rowCount = getTable().getRowCount(); //
		int li_colCount = getTable().getColumnCount(); //
		String[][] str_data = new String[li_rowCount][li_colCount]; //
		for (int i = 0; i < li_rowCount; i++) {
			for (int j = 0; j < li_colCount; j++) {
				str_data[i][j] = (getTable().getValueAt(i, j) == null ? "" : String.valueOf(getTable().getValueAt(i, j)));
			}
		}

		String[] str_colNames = new String[li_colCount]; //所有列名
		int[] li_width = new int[li_colCount]; //所有列宽
		for (int j = 0; j < li_colCount; j++) {
			str_colNames[j] = getColumnName(j); //
			li_width[j] = getTable().getColumnModel().getColumn(j).getPreferredWidth(); //
		}

		QuickSearchDialog dialog = new QuickSearchDialog(this, li_colIndex, clickedItemName, str_colNames, li_width, str_data); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			clearSelection(); //先清空选择!!!
			int[] li_allSelectedRows = dialog.getSelectedRows();
			for (int i = 0; i < li_allSelectedRows.length; i++) {
				table.addRowSelectionInterval(li_allSelectedRows[i], li_allSelectedRows[i]); //选中
			}
			Rectangle rect = table.getCellRect(li_allSelectedRows[0], 0, true);
			table.scrollRectToVisible(rect); //
		}
	}

	/**
	 * 设置是否显示分页.
	 * 
	 * @param _isshow
	 */
	public void setPagePanelVisible(boolean _isshow) {
		if (pagePanel == null)
			return;
		this.pagePanel.setVisible(_isshow);
		//this.repaint();  //
	}

	public int[] getSearchcolumn() {
		return searchcolumn;
	}

	public void setSearchcolumn(int[] _searchcolumn) {
		searchcolumn = _searchcolumn;
	}

	/**
	 * 双击某一列时对某一列进行排序!!!!
	 * @param _point
	 */
	private void sortByColumn(Point _point) {
		int li_rowcount = this.getTableModel().getRowCount(); //
		all_realValueData = new Object[li_rowcount][this.templetItemVOs.length + 1]; //
		for (int i = 0; i < li_rowcount; i++) {
			all_realValueData[i][0] = getRowNumberVO(i); //行号
			for (int j = 0; j < templetItemVOs.length; j++) {
				all_realValueData[i][j + 1] = this.getValueAt(i, templetItemVOs[j].getItemkey()); //
			}
		}
		if (this.all_realValueData.length == 0) {
			return;
		}
		int li_pos = getTable().getTableHeader().columnAtPoint(_point);
		TableColumn column = getTable().getColumnModel().getColumn(li_pos);
		String str_key = (String) column.getIdentifier(); //
		if (str_currsortcolumnkey != null && !str_key.equals(str_currsortcolumnkey)) {
			li_currsorttype = 0;
		}
		int li_modelindex = findModelIndex(str_key); //
		boolean isInt = templetItemVOs[li_modelindex - 1].getItemtype().equalsIgnoreCase(WLTConstants.COMP_NUMBERFIELD); //是否是数字框
		if (li_currsorttype == 0 || li_currsorttype == -1) {
			sortObjs(this.all_realValueData, li_modelindex, isInt, false); // 升序
			putValue(all_realValueData);
			li_currsorttype = 1;
		} else {
			sortObjs(this.all_realValueData, li_modelindex, isInt, true); // 升序
			putValue(all_realValueData);
			li_currsorttype = -1;
		}
		str_currsortcolumnkey = str_key; //
		table.getTableHeader().resizeAndRepaint(); //
		table.getTableHeader().invalidate(); //
	}

	public Pub_Templet_1_ItemVO[] getTempletItemVOs() {
		return templetItemVOs;
	}

	public void setTempletItemVOs(Pub_Templet_1_ItemVO[] templetItemVOs) {
		this.templetItemVOs = templetItemVOs;
	}

	public Pub_Templet_1VO getTempletVO() {
		return templetVO;
	}

	public void setTempletVO(Pub_Templet_1VO templetVO) {
		this.templetVO = templetVO;
	}

	public String getStr_realsql() {
		return str_realsql;
	}

	public void setValueAtRow(int _row, BillVO _vo) {
		for (int i = 0; i < _vo.getKeys().length; i++) {
			this.setValueAt(_vo.getDatas()[i], _row, _vo.getKeys()[i]);
		}
	}

	public void setValueAtRow(int _row, HashVO _vo) {
		for (int i = 0; i < _vo.getKeys().length; i++) {
			this.setValueAt(_vo.getStringValue(_vo.getKeys()[i]), _row, _vo.getKeys()[i]); //
		}
	}

	public void clearDeleteBillVOs() {
		this.v_deleted_row.clear();
	}

	/**
	 * 将当前页面上的数据进行保存 即根据不同行上的编辑状态，生成不同的SQL进行处理
	 * 这里返回是否保存成功，以便其他地方调用后有不同的提示【李春娟/2013-06-05】
	 */
	public boolean saveData() {
		this.stopEditing(); // 结束编辑
		String[] str_sql = getOperatorSQLs(); //
		try {
			UIUtil.executeBatchByDS(getDataSourceName(), str_sql); // 有问题!!
			this.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT); //
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
			return false;
		}
	}

	public void showSQL() {
		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append("客户端提交的初始SQL:\r\n【" + (getStr_realsql() == null ? "" : getStr_realsql()) + "】\r\n"); //

		sb_sql.append("数据权限执行的情况:\r\n【" + this.str_dataPolicyInfo + "】\r\n"); //权限策略计算说明
		sb_sql.append("数据权限执行的结果:\r\n【" + this.str_dataPolicySQL + "】\r\n"); //权限策略计算SQL

		sb_sql.append("服务器端实际执行的过程日志:\r\n【" + (str_calltrackmsg == null ? "" : str_calltrackmsg) + "】\r\n"); //

		sb_sql.append("\r\n****************** 主表配置信息 ****************\r\n");
		sb_sql.append("模板编码【" + templetVO.getTempletcode() + "】,模板名称【" + templetVO.getTempletname() + "】\r\n");
		sb_sql.append("创建方式【" + templetVO.getBuildFromType() + "】,创建信息【" + templetVO.getBuildFromInfo() + "】\r\n");
		sb_sql.append("queryTable【" + (templetVO.getTablename() == null ? "" : templetVO.getTablename()) + "】,savedTable【" + templetVO.getSavedtablename() + "】,DataSourceName【" + templetVO.getDatasourcename() + "】\r\n");
		sb_sql.append("启用的数据权限策略【" + templetVO.getDatapolicy() + "】,数据权限策略【" + templetVO.getDatapolicymap() + "】\r\n");

		String clicked_itemkey = (String) (getColumnModel().getColumn(getClickedColumnPos()).getIdentifier()); //
		Pub_Templet_1_ItemVO itemVO = getTempletItemVO(clicked_itemkey); //
		sb_sql.append("\r\n********** 子项配置信息 **********\r\n");
		sb_sql.append("ItemKey=【" + (itemVO.getItemkey() == null ? "" : itemVO.getItemkey()) + "】\r\n"); //
		sb_sql.append("ItemName=【" + (itemVO.getItemname() == null ? "" : itemVO.getItemname()) + "】\r\n"); //
		sb_sql.append("ItemType=【" + (itemVO.getItemtype() == null ? "" : itemVO.getItemtype()) + "】\r\n"); //
		sb_sql.append("Comboxdesc=【" + (itemVO.getComboxdesc() == null ? "" : itemVO.getComboxdesc()) + "】\r\n"); //
		sb_sql.append("RefDesc=【" + (itemVO.getRefdesc() == null ? "" : itemVO.getRefdesc()) + "】\r\n"); //
		sb_sql.append("LoadFormula=【" + (itemVO.getLoadformula() == null ? "" : itemVO.getLoadformula()) + "】\r\n"); //
		sb_sql.append("Editformula=【" + (itemVO.getEditformula() == null ? "" : itemVO.getEditformula()) + "】\r\n"); //
		sb_sql.append("Defaultvalueformula=【" + (itemVO.getDefaultvalueformula() == null ? "" : itemVO.getDefaultvalueformula()) + "】\r\n"); //
		MessageBox.showTextArea(this, sb_sql.toString());
	}

	private void showItemState() {
		new MetaDataUIUtil().showItemState(this, this.templetItemVOs);
	}

	/**
	 * 快速赋值,锁定一列,迅速将这一列的值赋同一值
	 */

	private void quickPutValue() {
		TableColumn col = this.getClickedTableColumn();
		int pos = -1;
		String key = (String) col.getIdentifier();
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (key.equals(templetItemVOs[i].getItemkey())) {
				pos = i;
				break;
			}
		}
		if (pos != -1) {
			QuickPutValueDialog dialog = new QuickPutValueDialog(this, "快速赋值", templetItemVOs[pos], true);
			if (!dialog.isModified())
				return;
			Object value = dialog.getValue();
			if (value == null || value.equals("") || (value instanceof RefItemVO) && ((RefItemVO) value).getId().equals("")) {
				if (JOptionPane.showConfirmDialog(this, "确定要将为列赋空值吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_NO_OPTION) {
					return;
				}
			}
			int[] li_selectedrows = table.getSelectedRows();
			if (li_selectedrows.length == 0) { // 如果一行没选择,则表示操作所有的行
				for (int i = 0; i < table.getRowCount(); i++) {
					((BillListModel) table.getModel()).setValueAt(value, i, key);
					if (!this.getEditState(i).equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) { // 如果不是新增状态
						this.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE);
					}
				}
			} else { // 如果有选择,则只对选择的行处理
				for (int i = 0; i < li_selectedrows.length; i++) {
					((BillListModel) table.getModel()).setValueAt(value, li_selectedrows[i], key);
					if (!this.getEditState(li_selectedrows[i]).equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) { // 如果不是新增状态
						this.setRowStatusAs(li_selectedrows[i], WLTConstants.BILLDATAEDITSTATE_UPDATE);
					}
				}
			}
		}
	}

	/**
	 * 快速穿透查询,有时因为数据权限过滤,导致在演示时无法出来数据,结果效果不好,所以提供一个方法事以直接不做任何过滤查询所有数据!!
	 */
	private void quickStrikeQuery() {
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
		try {
			String str_sql = "select * from " + this.templetVO.getTablename() + " where '穿透查询'='穿透查询'";
			queryDataByDS(null, str_sql); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
	}

	/**
	 * 快速保存当前位置与宽度
	 */
	private void quickSaveCurrWidth() {
		if (!checkIsCanConfigTemplet(true)) { //看是否可编辑配置
			return;
		}
		int li_allcolumncount = getTable().getColumnModel().getColumnCount(); //
		String[] str_pkid = new String[li_allcolumncount];
		String[] ste_currKeys = new String[li_allcolumncount]; //
		int[] li_currwidth = new int[li_allcolumncount];
		int[] li_oldpos = new int[li_allcolumncount];
		for (int i = 0; i < li_allcolumncount; i++) {
			TableColumn tc = getTable().getColumnModel().getColumn(i); //
			String itemkey = (String) tc.getIdentifier();
			ste_currKeys[i] = itemkey; //
			Pub_Templet_1_ItemVO itemVO = getTempletItemVO(itemkey); //
			str_pkid[i] = itemVO.getId(); //
			li_currwidth[i] = tc.getWidth(); //
			li_oldpos[i] = itemVO.getShoworder().intValue(); // 以前各个列的顺序!!
		}

		Arrays.sort(li_oldpos); // 重新排序
		String[] str_sqls = new String[li_allcolumncount];
		for (int i = 0; i < li_allcolumncount; i++) {
			str_sqls[i] = "update pub_templet_1_item set listwidth='" + li_currwidth[i] + "',showorder='" + li_oldpos[i] + "' where pk_pub_templet_1_item='" + str_pkid[i] + "'";
		}

		try {
			UIUtil.executeBatchByDS(null, str_sqls);
			MessageBox.show(this, UIUtil.getLanguage("保存当前各列的宽度与位置顺序成功!!"), WLTConstants.MESSAGE_INFO); //
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, UIUtil.getLanguage("保存当前位置与顺序失败,原因:" + e.getMessage())); //
		} //
	}

	private void quickSaveCurrRowHeight() {
		if (!checkIsCanConfigTemplet(true)) { //看是否可编辑配置
			return;
		}
		int li_roucount = this.getRowCount(); //
		if (li_roucount > 0) {
			StringBuffer sb_rowheight = new StringBuffer(""); //
			for (int i = 0; i < li_roucount; i++) {
				int li_rowheight = this.getTable().getRowHeight(i); // //
				sb_rowheight.append(li_rowheight + ","); // //
			}

			try {
				UIUtil.executeUpdateByDS(null, "update pub_templet_1 set listrowheight='" + sb_rowheight.toString() + "' where pk_pub_templet_1='" + this.templetVO.getPk_pub_templet_1() + "'"); //
				templetVO.setListrowheight(sb_rowheight.toString()); //
				MessageBox.show(this, UIUtil.getLanguage("保存行高成功!"), WLTConstants.MESSAGE_INFO); //
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.show(this, UIUtil.getLanguage("保存当前位置与顺序失败,原因:" + e.getMessage())); //
			} //
		}
	}

	/**
	 * 设置表格线是否显示与隐藏
	 */
	private void setTableGridLineVisiable() {
		JCheckBox box_rowLine = new JCheckBox("横向线是否显示"); //
		JCheckBox box_colLine = new JCheckBox("纵向线是否显示"); //		
		box_rowLine.setFocusable(false);
		box_colLine.setFocusable(false);
		box_rowLine.setSelected(table.getShowHorizontalLines()); //
		box_colLine.setSelected(table.getShowVerticalLines()); //
		box_rowLine.setBounds(5, 5, 120, 20); //
		box_colLine.setBounds(125, 5, 120, 20); //
		JPanel panel = new JPanel(); //
		panel.setOpaque(false); //透明!
		panel.setLayout(null); //
		panel.add(box_rowLine); //
		panel.add(box_colLine); //
		panel.setPreferredSize(new Dimension(250, 25)); //
		if (JOptionPane.showConfirmDialog(this, panel, "你是否真的想重置表格线是否显示?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			this.table.setShowHorizontalLines(box_rowLine.isSelected()); //在BasicTableUI中的方法paintGrid()是调用了该参数,然后决定如何绘图!! 现在有了奇偶行颜色不一致后就会产生问题了!也就是要重构paintGrid方法了,但该方法是private的!! 
			this.table.setShowVerticalLines(box_colLine.isSelected()); //
		}
	}

	private Object[][] getValueWithoutID(Object[][] _obj) {
		Object[][] obj = new Object[_obj.length][_obj[0].length];
		for (int i = 0; i < _obj.length; i++) {
			for (int j = 0; j < _obj[i].length - 1; j++) {
				obj[i][j] = _obj[i][j + 1];
			}
		}
		return obj;
	}

	/**
	 * 将表格中的数据层次出成Excel!
	 */
	public void exportToExcel() {
		//提示一把,是页面数据还是model中数据!!
		//		int li_result = JOptionPane.showOptionDialog(this, "点击按钮“页面显示列数据”只导出当前页上显示列的数据。\r\n点击按钮“所有列的数据”导出当前页所有(包括隐藏列)的数据。", "提示", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "页面显示列数据", "所有列的数据", "取消" }, "页面显示列数据"); //
		//		if (li_result == JOptionPane.CANCEL_OPTION) {
		//			return;
		//		}
		//这个提示太绕了, 一般人都看不懂! 都以为在问是否导出全部数据, 甚至有个用户被折磨了半小时, 索性改了! Gwang 2013-09-03
		//一般普通用户只想要显示列数据, 所以这个选项只针对管理员. 改为面向所的用户
		Boolean flg = TBUtil.getTBUtil().getSysOptionBooleanValue("excel导出是否导出全部数据", false);//zzl[2019-1-8 无锡减值系统希望导出当前的全部数据]
		int li_result = JOptionPane.YES_OPTION; //默认只导显示列数据
		//if (ClientEnvironment.getInstance().isAdmin()) { //管理员身份登陆
		if (flg) {
			li_result = JOptionPane.showOptionDialog(this, "请选择导出方式", "提示", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "显示列", "所有列", "全部数据", "取消" }, "显示列"); //
		} else {
			li_result = JOptionPane.showOptionDialog(this, "请选择导出方式", "提示", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "显示列", "所有列", "取消" }, "显示列"); //
		}
		if (flg) {
			if (li_result == JOptionPane.QUESTION_MESSAGE || li_result == -1) {//zzl [2019-1-8]修改bug 点击×号会弹出文件选择框
				return;
			}
		} else {
			if (li_result == JOptionPane.CANCEL_OPTION || li_result == -1) {
				return;
			}
		}
		//}

		String[][] str_values = null; //

		if (li_result == JOptionPane.YES_OPTION) {
			int li_colCount = table.getColumnCount(); //
			str_values = new String[table.getRowCount() + 1][li_colCount];
			for (int i = 0; i < li_colCount; i++) {
				str_values[0][i] = getColumnName(i); //
			}
			for (int i = 1; i < str_values.length; i++) {
				for (int j = 0; j < str_values[i].length; j++) {
					Object obj = table.getValueAt(i - 1, j); //
					if (obj == null) {
						str_values[i][j] = "";
					} else {
						str_values[i][j] = obj.toString();
					}
				}
			}
		} else if (li_result == JOptionPane.NO_OPTION) {
			Pub_Templet_1_ItemVO[] itemvos = getTempletItemVOs();
			int count = 0;
			System.out.println(itemvos.length);
			for (int i = 0; i < itemvos.length; i++) {
				if (itemvos[i].getListisexport() != null && itemvos[i].getListisexport().booleanValue()) {
					count++;
				}
			}
			str_values = new String[table.getRowCount() + 1][count]; //
			String str_modelkey = null;
			String str_modelName = null;
			String[] str_keys = new String[count];
			count = 0;
			for (int i = 0; i < itemvos.length; i++) {
				if (itemvos[i].getListisexport() != null && itemvos[i].getListisexport().booleanValue()) {
					str_modelkey = itemvos[i].getItemkey();
					str_modelName = itemvos[i].getItemname();
					if (str_modelName == null || str_modelName.trim().equals("")) {
						str_values[0][count] = str_modelkey; //
					} else {
						str_values[0][count] = str_modelName; //
					}
					str_keys[count] = str_modelkey;
					count++;
				}
			}
			for (int i = 1; i < str_values.length; i++) {
				for (int j = 0; j < str_keys.length; j++) {
					Object obj = getValueAt(i - 1, str_keys[j]); //
					if (obj == null) {
						str_values[i][j] = "";
					} else {
						str_values[i][j] = obj.toString();
					}
				}
			}
		} else if (li_result == JOptionPane.CANCEL_OPTION) {//zzl[2019-1-7 可以导出全部数据的功能]
			try {
				String sql = this.str_realsql;//张珍龙增加的【导出全部数据】需求，以前用的getQuickQueryPanel().getQuerySQL(); ，如果代码中加了查询监听这里获取的查询条件就会有问题，故优化之【李春娟/2019-03-14】
				if (this.str_realsql == null) {
					MessageBox.show(this,"请先查询再导出。");
					return;
				}
				Pub_Templet_1_ItemVO[] itemvos = getTempletItemVOs();
				StringBuffer sb = new StringBuffer();
				StringBuffer titlesb = new StringBuffer();
				for (int i = 0; i < itemvos.length; i++) {
					if (itemvos[i].getListisexport() != null && itemvos[i].getListisexport().booleanValue()) {
						sb.append(itemvos[i].getItemkey() + ",");//zzl[2019-1-7]得到导出的列
						titlesb.append(itemvos[i].getItemname() + ",");//zzl[2019-1-7]得到导出的列表头
					}
				}
				String[] title = titlesb.toString().substring(0, titlesb.toString().lastIndexOf(",")).split(",");
				sql = sql.replace("*", " " + sb.toString().substring(0, sb.toString().lastIndexOf(",")) + " ");
				String[][] date = UIUtil.getStringArrayByDS(null, sql);
				str_values = new String[date.length + 1][title.length];
				for (int i = 0; i < str_values.length; i++) {
					for (int j = 0; j < str_values[i].length; j++) {
						if (i == 0) {
							str_values[i][j] = title[j].toString();//zzl [2019-1-7 excel表头]
						} else {
							str_values[i][j] = date[i - 1][j].toString();
						}
					}
				}
			} catch (WLTRemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				} else {
					String filename = file.getName();
					return filename.endsWith(".xls");
				}
			}

			public String getDescription() {
				return "*.xls";
			}
		});

		try {
			File f = new File(new File(ClientEnvironment.str_downLoadFileDir + this.templetVO.getTempletname() + (str_values.length < 10000 ? ".xls" : ".xlsx")).getCanonicalPath());
			chooser.setSelectedFile(f);
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

		int li_rewult = chooser.showSaveDialog(this);
		if (li_rewult == JFileChooser.APPROVE_OPTION) {
			File curFile = chooser.getSelectedFile(); //
			if (curFile != null) {
				String str_fullpathname = curFile.getAbsolutePath(); //
				String str_return = null;
				if (str_values != null && str_values.length < 10000) { //10000可能有问题
					str_return = new cn.com.infostrategy.ui.report.cellcompent.ExcelUtil().setDataToExcelFile(str_values, str_fullpathname); //
				} else {
					str_return = new cn.com.infostrategy.ui.report.cellcompent.ExcelUtil().setDataToExcelFile_xlsx(str_values, str_fullpathname); //					
				}
				if (JOptionPane.showConfirmDialog(this, str_return + "\r\n你是否想立即打开该文件?", "提示", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					try {
						Desktop.open(curFile); //
					} catch (Exception ex) {
						ex.printStackTrace(); //
						try {
							Runtime.getRuntime().exec("explorer.exe \"" + str_fullpathname + "\"");
						} catch (Exception exx) {
							exx.printStackTrace(); //
						}
					}
				}

			}
		}
	}

	/**
	 * 将表格中的数据层次出成Html
	 * 2009-04-14
	 */
	public void exportToHtml() {
		StringBuffer strbf_html = new StringBuffer("<html>\r\n").append("<head>\r\n") //
				//generate html content
				.append("<title>BillList导出</title>\r\n")//
				.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n")//
				//.append("<LINK href=\"./applet/exportHTML.css\" type=text/css rel=stylesheet />\r\n") //
				.append("<style type=\"text/css\">\r\n") //
				.append("<!--\r\n") //
				.append("#list{border:1px solid;border-collapse: collapse;table-layout: fixed;}\r\n").append("#list th{background: #EEEEEE;font-weight: normal}\r\n").append("#list th,td{border:1px solid;font-size:12px;}\r\n").append("-->\r\n") //
				.append("</style>\r\n") //
				.append("</head>\r\n\r\n").append("<body>\r\n\r\n").append(getHtmlTableText()).append("</body>\r\n</html>");
		//popup dialog include the html
		BillDialog dialog = new BillDialog(this, this.getTempletVO().getTempletname(), this.getWidth(), this.getHeight());
		dialog.setLayout(new BorderLayout());
		BillHtmlPanel htmlPanel = new BillHtmlPanel();
		dialog.getContentPane().add(htmlPanel, BorderLayout.CENTER);
		htmlPanel.loadHtml(strbf_html.toString());
		dialog.setVisible(true);
	}

	/**
	 * excel导入，可以根据导出excel表头模板导入数据【李春娟/2018-07-26】
	 * 在张珍龙的导入代码基础上进行优化：
	 * 无数据提醒；查询部门和人员id不放在for循环里；获得主键值一次取50个，减少远程调用；去掉空行；列名判断是否是机构，用endsWith
	 * 以前是管理员身份都可以在表头右击导入，但后来考虑权限太大了，故保留该方法，哪个模块需要时加按钮调用即可【李春娟/2018-08-11】
	 * 同高总平台中cn.com.infostrategy.ui.mdata.listcomp.click2.BillListPanelClick2Action.importByExcel()
	 */
	public void importByExcel() {
		JFileChooser chooser = new JFileChooser();
		try {
			int flag = chooser.showOpenDialog(this);
			if (flag == 1) {
				return;
			}
			File file = chooser.getSelectedFile();
			if (file == null) {
				return;
			}
			final String data[][] = new cn.com.infostrategy.ui.report.cellcompent.ExcelUtil().getExcelFileData(file.getAbsolutePath());
			if (data == null || data.length == 1) {//如果获取文件失败或只有个列名，则提示
				MessageBox.show(this, "该文件无数据!");
				return;
			}
			Pub_Templet_1_ItemVO[] vos = this.getTempletItemVOs();
			HashMap<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < vos.length; i++) {
				map.put(vos[i].getItemname(), vos[i].getItemkey().toLowerCase());
			}

			HashMap deptnameMap = new HashMap();//这里用HashMap主要是为了去重复
			HashMap usernameMap = new HashMap();
			//现将机构名称和人员名称放到HashMap中，全部查询出来
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data[i].length; j++) {
					if (data[0][j].trim().endsWith("机构") || data[0][j].trim().endsWith("部门") || data[0][j].trim().endsWith("单位") || data[0][j].trim().endsWith("公司")) {//这里以后需要优化，到底用contains还是endsWith?【李春娟/2018-07-26】
						if (data[i][j] != null && !data[i][j].equals("")) {
							deptnameMap.put(data[i][j].trim(), data[i][j].trim());//前后去空格，中间空格可能会有用，故暂不去除【李春娟/2018-07-30】
						}
					} else if (data[0][j].trim().endsWith("人")) {
						if (data[i][j] != null && !data[i][j].equals("")) {
							usernameMap.put(data[i][j].trim(), data[i][j].trim());//前后去空格，中间空格可能会有用，故暂不去除【李春娟/2018-07-30】
						}
					}
				}

			}
			//更新修改列
			String tablename = this.getTempletVO().getSavedtablename();//保存表名
			String pksequencename = this.getTempletVO().getPksequencename();//主键序列名，这里模板必须配置正确，否则会主键冲突
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkCommServiceIfc.class);
			Long[] ll_newIds = service.getSequenceNextValByDS(null, pksequencename, new Integer(50));//客户端默认取10个缓存起来，这里一次取50个，减少远程调用
			int idcount = 0;
			//因机构或部门可能会有重复，故进行优化，机构可以导入机构路径，比如中国太平-太平再保险-太平再香港总公司-办公室【李春娟/2018-09-07】
			HashMap deptLinkMap = new HashMap();//机构全路径，包括第一层
			HashMap deptLink1Map = new HashMap();//机构路径，不包括第一层
			HashVO[] hvos_dept = UIUtil.getHashVoArrayAsTreeStructByDS(null, " select * from pub_corp_dept ", "id", "parentid", null, null);
			for (int i = 0; i < hvos_dept.length; i++) {
				String id = hvos_dept[i].getStringValue("id");
				String parentpathnamelink = hvos_dept[i].getStringValue("$parentpathnamelink");
				if (parentpathnamelink != null && !"".equals(parentpathnamelink)) {
					deptLinkMap.put(parentpathnamelink, id);
				}
				String parentpathnamelink1 = hvos_dept[i].getStringValue("$parentpathnamelink1");
				if (parentpathnamelink1 != null && !"".equals(parentpathnamelink1)) {
					deptLink1Map.put(parentpathnamelink1, id);
				}
			}

			HashMap deptidMap = UIUtil.getHashMapBySQLByDS(null, "select name ,id from pub_corp_dept where name in(" + TBUtil.getTBUtil().getInCondition((String[]) deptnameMap.keySet().toArray(new String[0])) + ")");
			HashMap useridMap = UIUtil.getHashMapBySQLByDS(null, "select name ,id from pub_user where name in(" + TBUtil.getTBUtil().getInCondition((String[]) usernameMap.keySet().toArray(new String[0])) + ")");
			TableDataStruct tableStruct = UIUtil.getTableDataStructByDS(null, "select * from " + tablename + " where 1=2");
			String[] headerName = tableStruct.getHeaderName();//取得保存表所有列，后面判断表中有的字段才进行保存，否则会报错
			HashMap headerMap = new HashMap();
			for (int i = 0; i < headerName.length; i++) {
				if (headerName[i] != null) {
					headerMap.put(headerName[i].toLowerCase(), headerName[i].toLowerCase());
				}
			}
			InsertSQLBuilder insert = new InsertSQLBuilder(tablename);
			ArrayList _sqllist = new ArrayList();
			for (int i = 1; i < data.length; i++) {//从第2行开始遍历，默认第一行为列名
				boolean isnotnull = false;
				for (int j = 0; j < data[i].length; j++) {
					String colname = data[0][j].trim();
					if (headerMap.containsKey(map.get(colname))) {//数据库中有该字段才参与保存
						//判断是否为空，如果整行为空，则不插入
						if (data[i][j] != null && !data[i][j].trim().equals("")) {
							String value = data[i][j].trim();
							isnotnull = true;
							if (colname.endsWith("机构") || colname.endsWith("部门") || colname.endsWith("单位") || colname.endsWith("公司")) {//这里以后需要优化，到底用contains还是endsWith?【李春娟/2018-07-26】
								if (deptLinkMap.containsKey(value)) {//先看是否匹配全路径【李春娟/2018-09-07】
									insert.putFieldValue(map.get(colname), (String) deptLinkMap.get(value));
								} else if (deptLink1Map.containsKey(value)) {//再看是否匹配去掉第一层的路径
									insert.putFieldValue(map.get(colname), (String) deptLink1Map.get(value));
								} else {//最后看是否匹配一个机构名称
									insert.putFieldValue(map.get(colname), (String) deptidMap.get(value));
								}
							} else if (colname.endsWith("人")) {
								insert.putFieldValue(map.get(colname), (String) useridMap.get(value));
							} else {
								insert.putFieldValue(map.get(colname), value);
							}
						} else {
							insert.putFieldValue(map.get(colname), "");
						}
					}
				}
				if (isnotnull) {
					if (idcount == 50) {
						idcount = 0;
						ll_newIds = service.getSequenceNextValByDS(null, pksequencename, new Integer(50));
					}
					insert.putFieldValue("id", ll_newIds[idcount]);
					idcount++;
					_sqllist.add(insert.getSQL());
					System.out.println(insert.getSQL());//打印出所有sql
				}
			}
			UIUtil.executeBatchByDS(null, _sqllist);
			this.refreshData();
			MessageBox.show(this, "导入成功!");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
	}

	public String getHtmlTableText() {
		int li_row_count = table.getRowCount() + 1; // +1 is table head
		int li_col_count = table.getColumnCount();
		StringBuffer strbf_html = new StringBuffer("<table id=\"list\" cellpadding=\"3\" cellspacing=\"1\">\r\n");
		//generate table head
		strbf_html.append("<tr>\r\n");
		for (int i = 0; i < li_col_count; i++) {
			String columnName = table.getColumnName(i);
			strbf_html.append("<th width=").append(table.getColumn(columnName).getWidth()).append(">").append(getColumnName(i)).append("</th>\r\n");
		}
		strbf_html.append("</tr>\r\n");

		//generate table body
		for (int i = 1; i < li_row_count; i++) {
			strbf_html.append("<tr>\r\n");
			for (int j = 0; j < li_col_count; j++) {
				strbf_html.append("<td>");
				Object obj = table.getValueAt(i - 1, j); //
				if (obj != null) {
					String str_itemkey = table.getColumnName(j);
					String str_itemType = templetVO.getItemVo(str_itemkey).getItemtype();
					//要根据cell内容的类型决定生成的html内容
					if (str_itemType.equals(WLTConstants.COMP_OFFICE) || str_itemType.equals(WLTConstants.COMP_FILECHOOSE)) {
						strbf_html.append(getComponentToHrefHtml(this.getBillVO(j), obj.toString(), str_itemkey, str_itemType));
					} else {
						strbf_html.append(obj.toString());
					}
				}
				strbf_html.append("</td>\r\n");
			}
			strbf_html.append("</tr>\r\n");
		}

		//match the non-complete html content
		strbf_html.append("</table>\r\n");
		return strbf_html.toString();
	}

	private void onInsert(String[][] excelData) throws Exception {

		for (int m = 2; m < excelData.length; m++) {
			int j = newRow();
			for (int i = 0; i < excelData[0].length; i++) {
				if (excelData[m][i] != null) {
					setValueAt(new StringItemVO(excelData[m][i]), j, excelData[0][i]);

				} else if (excelData[0][i] != null) {
					setValueAt(new StringItemVO(""), j, excelData[0][i]);
				}
			}
			saveData();
		}

	}

	/**
	 * 做新增操作!
	 */
	public void doInsert(HashMap _defaultValue) {
		BillCardPanel cardPanel = new BillCardPanel(this.templetVO); //创建一个卡片面板
		cardPanel.setLoaderBillFormatPanel(this.getLoaderBillFormatPanel()); //将列表的BillFormatPanel的句柄传给卡片
		cardPanel.insertRow(); //卡片新增一行!
		if (_defaultValue != null) {
			String[] str_keys = (String[]) _defaultValue.keySet().toArray(new String[0]); //
			for (int i = 0; i < str_keys.length; i++) { ////
				Object valueObj = _defaultValue.get(str_keys[i]); ////
				if (valueObj != null) {
					if (valueObj instanceof String) {
						cardPanel.setValueAt(str_keys[i], new StringItemVO((String) valueObj)); //
					} else if (valueObj instanceof StringItemVO) {
						cardPanel.setValueAt(str_keys[i], (StringItemVO) valueObj); //
					} else if (valueObj instanceof ComBoxItemVO) {
						cardPanel.setValueAt(str_keys[i], (ComBoxItemVO) valueObj); //
					} else if (valueObj instanceof RefItemVO) {
						cardPanel.setValueAt(str_keys[i], (RefItemVO) valueObj); //
					} else {
						cardPanel.setValueAt(str_keys[i], new StringItemVO(valueObj.toString())); ////
					}
				}
			}
		}
		cardPanel.setEditableByInsertInit(); //设置卡片编辑状态为新增时的设置
		BillCardDialog dialog = new BillCardDialog(this, this.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //弹出卡片新增框
		dialog.setVisible(true); //显示卡片窗口
		if (dialog.getCloseType() == 1) { //如是是点击确定返回!将则卡片中的数据赋给列表!
			int li_newrow = newRow(false); //
			this.setBillVOAt(li_newrow, dialog.getBillVO(), false);
			this.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
			this.setSelectedRow(li_newrow); //
		}

	}

	//做删除操作
	public void doDelete(boolean _isQuiet) {
		try {
			int li_selRow = this.getSelectedRow();
			if (li_selRow < 0) {
				MessageBox.showSelectOne(this);
				return;
			}
			if (!_isQuiet) { //如果不是安静模式则还提示一把!
				if (!MessageBox.confirmDel(this)) {
					return; //
				}
			}

			String str_tableName = this.templetVO.getSavedtablename(); //
			if (str_tableName == null || str_tableName.trim().equals("")) {
				MessageBox.show(this, "模板中定义的保存表名为空,不能进行删除操作!"); //
				return;
			}
			String str_pkName = this.templetVO.getPkname(); //主键名
			if (str_pkName == null || str_pkName.trim().equals("")) {
				MessageBox.show(this, "主键字段名为空,不能进行删除操作!"); //
				return;
			}

			BillVO billVO = this.getSelectedBillVO(); //
			String str_pkValue = billVO.getStringValue(this.templetVO.getPkname()); //主键值
			String str_sql = "delete from " + str_tableName + " where " + str_pkName + "='" + str_pkValue + "'"; //主SQL,在实际执行时是在最后一条执行!!!

			//删除相关联的数据!!!
			String[][] str_selRowValue = this.getSelectedRowValue(); //当前选中行的数据!!
			if (str_selRowValue != null && str_selRowValue.length > 0) { //如果有值!!!
				cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc service = (cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc) UIUtil.lookUpRemoteService(cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc.class); //
				service.doCascadeDeleteSQL(str_tableName, str_selRowValue, str_sql, true); //级联删除所有关联数据!!!
			}

			//强制追加级联删除 【杨科/2013-03-26】
			if (getTBUtil().getSysOptionBooleanValue("列表删除是否启用级联删除", false)) {
				UIUtil.executeBatchByDS(getDataSourceName(), getDeleteSQL(billVO));
			}

			this.removeRow(li_selRow); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
		if (all_realValueData != null && all_realValueData.length > 0) { //增加校验，否则删除后会报错 Gwang 2016-12-19
			RowNumberItemVO rowNumVO = (RowNumberItemVO) all_realValueData[0][0]; //
			li_TotalRecordCount = rowNumVO.getTotalRecordCount(); //
			System.out.println(">>>>>>>>>>删除>>>>>>>>>>" + li_TotalRecordCount);
		}
	}

	//获取级联删除sqls 【杨科/2013-03-26】
	public ArrayList getDeleteSQL(BillVO billVO) throws Exception {
		ArrayList al_sqls = new ArrayList();
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			String str_type = templetItemVOs[i].getItemtype();
			if (str_type.equals(WLTConstants.COMP_LINKCHILD)) {
				CommUCDefineVO uCDfVO = templetItemVOs[i].getUCDfVO();
				if (uCDfVO != null && uCDfVO.getConfValue("模板编码") != null && uCDfVO.getConfValue("主键字段名") != null) {
					Pub_Templet_1VO templetVO_Child = UIUtil.getPub_Templet_1VO(uCDfVO.getConfValue("模板编码"));
					String ids = billVO.getStringValue(templetItemVOs[i].getItemkey());
					if (ids != null && ids.endsWith(";")) {
						ids = ids.substring(0, ids.lastIndexOf(";")).replace(";", ",");
						String str_sql = "delete from " + templetVO_Child.getSavedtablename() + " where " + uCDfVO.getConfValue("主键字段名") + " in (" + ids + ")";
						al_sqls.add(str_sql);
					}
				}
			}
		}
		return al_sqls;
	}

	/**
	 * 做编辑处理.
	 */
	public void doEdit() {
		BillVO billVO = this.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		BillCardPanel cardPanel = new BillCardPanel(this.templetVO);
		cardPanel.setLoaderBillFormatPanel(this.getLoaderBillFormatPanel());
		cardPanel.setBillVO(billVO); //

		BillCardDialog dialog = new BillCardDialog(this, this.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			if (this.getSelectedRow() == -1) {//袁江晓 20130415添加，主要解决刷新模板时候可能出现的错
			} else {
				this.setBillVOAt(this.getSelectedRow(), dialog.getBillVO());
				this.setRowStatusAs(this.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			}
		}
	}

	//实际排序
	public void sortObjs(Object[][] _objs, int _pos, boolean _isNumber, boolean _isdesc) {
		//long ll_1 = System.currentTimeMillis(); //
		if (_isNumber) {
			Arrays.sort(_objs, new BillListDataNumberComparator(_pos, _isdesc));
		} else {
			Arrays.sort(_objs, new BillListDataStrComparator(_pos, _isdesc));
		}
		//long ll_2 = System.currentTimeMillis(); //
		//logger.debug("前台排序耗时【" + (ll_2 - ll_1) + "】毫秒");
	}

	public String replaceAll(String str_par, String old_item, String new_item) {
		String str_return = "";
		String str_remain = str_par;
		boolean bo_1 = true;
		while (bo_1) {
			int li_pos = str_remain.indexOf(old_item);
			if (li_pos < 0) {
				break;
			} // 如果找不到,则返回
			String str_prefix = str_remain.substring(0, li_pos);
			str_return = str_return + str_prefix + new_item; // 将结果字符串加上原来前辍
			str_remain = str_remain.substring(li_pos + old_item.length(), str_remain.length());
		}
		str_return = str_return + str_remain; // 将剩余的加上
		return str_return;
	}

	public JPanel getCustomerNavigationJPanel() {
		customerNavigationJPanel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		return customerNavigationJPanel;
	}

	public void setCustomerNavigationJPanel(JPanel _custchildPanel) {
		customerNavigationJPanel.removeAll(); //
		customerNavigationJPanel.setLayout(new BorderLayout()); //
		customerNavigationJPanel.setPreferredSize(_custchildPanel.getPreferredSize()); //
		customerNavigationJPanel.add(_custchildPanel); //
		customerNavigationJPanel.updateUI(); //
	}

	public void setCustomerNavigationJPanelVisible(boolean _visible) {
		if (customerNavigationJPanel != null) {
			customerNavigationJPanel.setVisible(_visible); //
		}
	}

	public Pub_Templet_1_ItemVO getTempletItemVO(String key) {
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equalsIgnoreCase(key))
				return templetItemVOs[i];
		}
		return null;
	}

	public String getItemNameByItemKey(String _itemKey) {
		Pub_Templet_1_ItemVO itemVO = getTempletItemVO(_itemKey); //
		if (itemVO != null) {
			return itemVO.getItemname(); //
		}
		return null;
	}

	public String getItemTypeByItemKey(String _itemKey) {
		Pub_Templet_1_ItemVO itemVO = getTempletItemVO(_itemKey); //
		if (itemVO != null) {
			return itemVO.getItemtype(); //
		}
		return null;
	}

	public boolean containsItemKey(String _itemKey) {
		return this.getTableModel().containsItemKey(_itemKey); //
	}

	/**
	 * 校验
	 * 
	 * @return
	 */
	public boolean checkValidate() {
		return (checkIsNullValidate() && checkItemLengthValidate()); // 先只处理非空校验,以后还会增加校验公式!!
	}

	/**
	 * 非空和警告校验,
	 * 
	 * @return
	 */
	private boolean checkIsNullValidate() {
		this.stopEditing();
		String[] str_keys = this.getTempletVO().getItemKeys(); // 所有的key
		String[] str_names = this.getTempletVO().getItemNames(); // 所有的Name
		boolean[] bo_isMustInputs = this.getTempletVO().getItemIsMustInputs(); // 是否必输入
		int li_rowcount = getTableModel().getRowCount();
		StringBuffer showmsg = new StringBuffer();
		for (int i = 0; i < li_rowcount; i++) {
			for (int j = 0; j < str_keys.length; j++) {
				if (bo_isMustInputs[j]) { // 如果是必输入项!!
					Object obj = getValueAt(i, str_keys[j]); //
					if (obj == null) {
						showmsg.append("第[" + (i + 1) + "]行数据,[" + str_names[j] + "]不能为空!\r\n"); //
					} else {
						if (obj instanceof String) { // ..
							String new_name = (String) obj;
							if (new_name.trim().equals("")) {
								showmsg.append("第[" + (i + 1) + "]行数据,[" + str_names[j] + "]不能为空!\r\n"); //
							}
						} else if (obj instanceof StringItemVO) {
							StringItemVO new_name = (StringItemVO) obj;
							if (new_name.getStringValue().trim().equals("")) {
								showmsg.append("第[" + (i + 1) + "]行数据,[" + str_names[j] + "]不能为空!\r\n"); //
							}
						} else if (obj instanceof RefItemVO) {//以前只判断了String、StringItemVO对象，导致参照和下拉框未判断，故增加【李春娟/2018-11-22】
							RefItemVO new_name = (RefItemVO) obj;
							if (new_name.getId() == null || new_name.getId().trim().equals("")) {
								showmsg.append("第[" + (i + 1) + "]行数据,[" + str_names[j] + "]不能为空!\r\n"); //
							}
						} else if (obj instanceof ComBoxItemVO) {
							ComBoxItemVO new_name = (ComBoxItemVO) obj;
							if (new_name.getId() == null || new_name.getId().trim().equals("")) {
								showmsg.append("第[" + (i + 1) + "]行数据,[" + str_names[j] + "]不能为空!\r\n"); //
							}
						}

					}
				}
			}
		}
		String str_showmsg = showmsg.toString();
		if ("".equals(str_showmsg)) {
			boolean[] bo_isWarnInputs = this.getTempletVO().getItemIsWarnInputs(); //是否警告项	
			for (int i = 0; i < li_rowcount; i++) {
				for (int j = 0; j < str_keys.length; j++) {
					if (bo_isWarnInputs[j]) { // 如果是警告项!!
						Object obj = getValueAt(i, str_keys[j]); //
						if (obj == null) {
							showmsg.append("第[" + (i + 1) + "]行数据,[" + str_names[j] + "]为警告项!\r\n"); //
						} else {
							if (obj instanceof String) { // ..
								String new_name = (String) obj;
								if (new_name.trim().equals("")) {
									showmsg.append("第[" + (i + 1) + "]行数据,[" + str_names[j] + "]为警告项!\r\n"); //
								}
							} else if (obj instanceof StringItemVO) {
								StringItemVO new_name = (StringItemVO) obj;
								if (new_name.getStringValue().trim().equals("")) {
									showmsg.append("第[" + (i + 1) + "]行数据,[" + str_names[j] + "]为警告项!\r\n"); //
								}
							}
						}
					}
				}
			}
			if ("".equals(showmsg.toString())) {
				return true;
			}
			showmsg.append("\r\n 是否继续保存？");
			int option = MessageBox.showConfirmDialog(this, showmsg.toString(), "提示", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				return true;
			} else {
				return false;
			}
		}
		str_showmsg = showmsg.substring(0, showmsg.length() - 2);
		MessageBox.showTextArea(this, str_showmsg);
		return false;
	}

	/**
	 * 长度校验,
	 * @return
	 */
	private boolean checkItemLengthValidate() {
		this.stopEditing();
		String[] str_keys = this.getTempletVO().getItemKeys(); //所有的key
		String[] str_names = this.getTempletVO().getItemNames(); //所有的Name
		int[] str_lengths = this.getTempletVO().getItemLengths(); //所有的Name
		int li_rowcount = getTableModel().getRowCount();
		StringBuffer showmsg = new StringBuffer();
		String zfj = getTBUtil().getSysOptionStringValue("数据库字符集", "GBK");//以前只判断GBK，如果是UTF-8，一个汉字占三个字节，故校验可能通过，但保存数据库报错，故设置该参数【李春娟/2016-04-26】
		for (int i = 0; i < li_rowcount; i++) {
			for (int j = 0; j < str_keys.length; j++) {
				Object obj = getValueAt(i, str_keys[j]); //
				boolean ifsave = getTempletItemVO(str_keys[j]).getIssave();//只有参与保存的才做长度校验【李春娟/2015-06-18】
				if (obj != null && ifsave) {
					int length = 0;
					boolean checkSuc = true;
					if (obj instanceof StringItemVO) {
						if (str_lengths[j] != -1) {
							StringItemVO new_name = (StringItemVO) obj;
							try {
								// 在这里值得提一下: 万一根据GBK编码格式来判断出现错误/异常, 请使用下列方法:
								// getTBUtil().getStrUnicodeLength(String s)来根据UNICODE编码格式查询字符串的字节长度
								length = new_name.getStringValue().getBytes(zfj).length;
								if (length > str_lengths[j]) {
									checkSuc = false;
								}
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}
					} else if (obj instanceof RefItemVO) {
						if (str_lengths[j] != -1) {
							RefItemVO new_name = (RefItemVO) obj;
							try {
								if (new_name.getId() != null) {
									length = new_name.getId().getBytes(zfj).length;
									if (length > str_lengths[j]) {
										checkSuc = false;
									}
								}
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}
					} else if (obj instanceof ComBoxItemVO) {
						if (str_lengths[j] != -1) {
							ComBoxItemVO new_name = (ComBoxItemVO) obj;
							try {
								if (new_name.getId() != null) {
									length = new_name.getId().getBytes(zfj).length;
									if (length > str_lengths[j]) {
										checkSuc = false;
									}
								}
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
						}
					} else if (obj instanceof String) { // ..
						String new_name = (String) obj;
						try {
							length = new_name.getBytes(zfj).length;
							if (length > str_lengths[j]) {
								checkSuc = false;
							}
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					if (!checkSuc) {
						showmsg.append("第[" + (i + 1) + "]行数据,[" + getTBUtil().getTrimSwapLineStr(str_names[j]) + "]的数据长度大于数据库定义长度(" + str_lengths[j] + ")!\r\n"); //
					}
				}
			}
		}
		String str_showmsg = showmsg.toString();
		if ("".equals(str_showmsg)) {
			return true;
		}
		str_showmsg = showmsg.substring(0, showmsg.length() - 2);
		MessageBox.showTextArea(this, str_showmsg);
		return false;
	}

	private boolean checkIsUniqueValidate() {
		return false;
	}

	/**
	 * 光标停在某一列!!
	 * 
	 * @param _row
	 * @param _itemKey
	 */
	private void focusOn(int _row, String _itemKey) {
		int li_colIndex = findColumnIndex(_itemKey); // 如果这一列是隐藏的,则返回-1
		if (li_colIndex < 0) {
			return;
		}

		this.getTable().editCellAt(_row, li_colIndex); //
		IBillCellEditor cellEditor = (IBillCellEditor) this.getTable().getCellEditor(_row, li_colIndex); // 取得编辑器!!
		JComponent obj_comp = cellEditor.getCompent(); // 以得控件!!
		if (obj_comp instanceof JTextField) { // 如果文本框!!
			JTextField new_obj = (JTextField) obj_comp;
			new_obj.requestFocus();
			new_obj.requestFocusInWindow(); //
		} else if (obj_comp instanceof JComboBox) { // 如果是下拉框!!
			JComboBox new_obj = (JComboBox) obj_comp;
			new_obj.requestFocus();
			new_obj.requestFocusInWindow(); //
		} else if (obj_comp instanceof JCheckBox) { // 如果是勾选框
			JCheckBox new_obj = (JCheckBox) obj_comp;
			new_obj.requestFocus();
			new_obj.requestFocusInWindow(); //
		} else if (obj_comp instanceof AbstractWLTCompentPanel) { // 如果是INovaCompent
			AbstractWLTCompentPanel new_obj = (AbstractWLTCompentPanel) obj_comp;
			new_obj.focus();
		} else {
		}
		this.updateUI(); //
	}

	private AbstractTMO getTMO(String[] _allcolumns, int _width) {
		return getTMO(_allcolumns, new int[] { _width }, null); //
	}

	/**
	 * 创建TMO
	 * 
	 * @param _allcolumns
	 * @return
	 */
	private AbstractTMO getTMO(String[] _allcolumns, int[] _widths, String _templetName) {
		HashVO parentVO = new HashVO(); //
		parentVO.setAttributeValue("templetcode", "Test"); // 模版编码,请勿随便修改
		parentVO.setAttributeValue("templetname", _templetName); // 模版编码,请勿随便修改
		HashVO[] childVOs = new HashVO[_allcolumns.length];
		int count = 0;
		for (int i = 0; i < childVOs.length; i++) {
			childVOs[i] = new HashVO();
			childVOs[i].setAttributeValue("itemkey", _allcolumns[i]); //
			childVOs[i].setAttributeValue("itemname", _allcolumns[i]); //
			childVOs[i].setAttributeValue("itemtype", WLTConstants.COMP_TEXTAREA); // 都是文本框
			int li_width = _widths[0]; //
			if (i < _widths.length) { //如果没越界,则用对应位置上的
				li_width = _widths[i]; //
			}
			childVOs[i].setAttributeValue("listwidth", "" + li_width); //以前是125,太小,搞大点!!!以后应该根据实际内容大小自动匹配! 即与表格参照一样!!
			if (_allcolumns[i].endsWith("#")) {
				childVOs[i].setAttributeValue("listisshowable", "N"); //
			} else {
				childVOs[i].setAttributeValue("listisshowable", "Y"); //
				childVOs[i].setAttributeValue("iswrap", "Y");
			}
			childVOs[i].setAttributeValue("listiseditable", "4"); //
			childVOs[i].setAttributeValue("cardwidth", "400*60");
		}

		AbstractTMO tmo = new DefaultTMO(parentVO, childVOs); // 创建元原模板数据
		return tmo;
	}

	/**
	 * 取得加载这个billListPanel的Frame,比如是各种风格模板!!
	 * 
	 * @return
	 */
	public AbstractStyleWorkPanel getLoadedWrokPanel() {
		return (AbstractStyleWorkPanel) loadedWorkPanel;
	}

	/**
	 * 取得加载这个billListPanel的Frame,比如是各种风格模板!!
	 * 
	 * @return
	 */
	public AbstractWorkPanel getLoadedPanel() {
		return loadedWorkPanel;
	}

	/**
	 * 设置加载这个billListPanel的工作面板,比如是各种风格模板的工作面板!!
	 * 
	 * @param loadedFrame
	 */
	public void setLoadedWorkPanel(AbstractWorkPanel _loadedWorkPanel) {
		this.loadedWorkPanel = _loadedWorkPanel;
	}

	public BillFormatPanel getLoaderBillFormatPanel() {
		return loaderBillFormatPanel;
	}

	public void setLoaderBillFormatPanel(BillFormatPanel loaderBillFormatPanel) {
		this.loaderBillFormatPanel = loaderBillFormatPanel;
	}

	public Vector getV_listbtnListener() {
		return v_listbtnListener;
	}

	/**
	 * 引用子表
	 * @param _ChildTable
	 */
	public void setLoaderChildTable(CardCPanel_ChildTable _ChildTable) {
		loaderChildTable = _ChildTable; //
	}

	public CardCPanel_ChildTable getLoaderChildTable() {
		return loaderChildTable; //
	}

	/**
	 * 取得工具类
	 * 
	 * @return
	 */
	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil; //
	}

	private FrameWorkMetaDataServiceIfc getMetaDataService() {
		if (metaDataService == null) {
			try {
				metaDataService = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); ////
			} catch (Exception e) {
				e.printStackTrace();
				return null; //
			}
		}
		return metaDataService; //
	}

	public void setQuickQueryPanelVisiable(boolean _visiable) {
		getQuickQueryPanel().setVisible(_visiable); //
		btn_shqq.setVisible(_visiable); //那个展开按钮也要设下
	}

	public BillQueryPanel getQuickQueryPanel() {
		if (quickQueryPanel != null) {
			return quickQueryPanel;
		}
		quickQueryPanel = new BillQueryPanel(this); // 快速查询面板!
		quickQueryPanel.setOpaque(false); //
		unTitlePanel.add(quickQueryPanel, BorderLayout.NORTH); //如果模板中配置不显示，在初始化也不加载，但如果代码中设置需要显示的话，要加入到unTitlePanel面板中，李春娟修改
		return quickQueryPanel;
	}

	public void setQuickQueryPanelItemValue(String _itemKey, Object _obj) {
		getQuickQueryPanel().setCompentObjectValue(_itemKey, _obj);
	}

	public void setQuickQueryPanelItemRealValue(String _itemKey, String _value) {
		getQuickQueryPanel().setRealValueAt(_itemKey, _value); //
	}

	public boolean isHeaderCanSort() {
		return isHeaderCanSort;
	}

	public void setHeaderCanSort(boolean isHeaderCanSort) {
		this.isHeaderCanSort = isHeaderCanSort;
	}

	public boolean isRowNumberChecked() {
		return isRowNumberChecked;
	}

	public void setRowNumberChecked(boolean isRowNumberChecked) {
		this.isRowNumberChecked = isRowNumberChecked;
		if (isRowNumberChecked) { //如果是勾选中的!
			li_rowno_width = 44;
		} else {
			li_rowno_width = 37;
		}
		if (jv != null) {
			jv.setPreferredSize(new Dimension(li_rowno_width, jv.getPreferredSize().height)); //
		}
		if (rowNumberColumn != null) {
			rowNumberColumn.setPreferredWidth(li_rowno_width); //
		}
		this.rowHeaderTable.repaint(); //
		this.rowHeaderTable.revalidate(); //
	}

	public int getLi_currpage() {
		return li_currpage;
	}

	public JLabel getLabel_pagedesc() {
		return label_pagedesc;
	}

	public void setHiddenUntitlePanelBtnvVisiable(boolean isHiddenUntitlePanelBtnvVisiable) {
		this.isHiddenUntitlePanelBtnvVisiable = isHiddenUntitlePanelBtnvVisiable;
	}

	public Vector getAfterUueryListeners() {
		return v_afterqueryListeners;
	}

	class TableAdapter implements ActionListener {

		JTable jt_table;

		public TableAdapter(JTable _table) {
			jt_table = _table;
			KeyStroke ks_copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
			KeyStroke ks_enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, ActionEvent.META_MASK);
			jt_table.registerKeyboardAction(this, "Copy", ks_copy, JComponent.WHEN_FOCUSED); //

		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Copy")) {
				int li_row = jt_table.getSelectedRow();
				int li_col = jt_table.getSelectedColumn();

				if (li_row >= 0 && li_col >= 0) {
					String str_value = "";
					Object obj = jt_table.getValueAt(li_row, li_col);
					if (obj != null) {
						str_value = obj.toString();
						StringSelection ss = new StringSelection(str_value);
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
					}
				}
			}

		}
	}

	/**
	 * 表头双击排序时显示不同图标!!
	 */
	class SortHeaderRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 2924685007982535240L;

		Pub_Templet_1VO parentVO = null;

		Pub_Templet_1_ItemVO[] childVOs = null;

		boolean isadmin = true;

		public SortHeaderRenderer(Pub_Templet_1VO _templetVO, boolean isadmin) {
			this.parentVO = _templetVO;
			this.childVOs = _templetVO.getItemVos();
			this.isadmin = isadmin;
		}

		public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean isSelected, boolean hasFocus, int _row, int _column) {
			JLabel label2 = (JLabel) super.getTableCellRendererComponent(jtable, obj, isSelected, hasFocus, _row, _column); //
			WLTLabel label = new WLTLabel(label2.getText(), WLTPanel.VERTICAL_TOP_TO_BOTTOM2); //
			label.setOpaque(true); //不透明
			label.setHorizontalAlignment(SwingConstants.CENTER); //居中
			label.setForeground(LookAndFeel.table_headFontColor);
			label.setBackground(LookAndFeel.tableheaderbgcolor);

			TableColumn column = jtable.getColumnModel().getColumn(_column);
			String str_key = (String) column.getIdentifier(); //
			String str_text = (obj != null ? obj.toString() : ""); //
			Pub_Templet_1_ItemVO itemVO = parentVO.getItemVo(str_key); //
			String str_tooltiptext = itemVO.getItemtiptext(); //
			if (str_tooltiptext == null || str_tooltiptext.trim().equals("")) { ////
				str_tooltiptext = str_text;
			}
			boolean _ismustinput = false;
			this.setHorizontalAlignment(SwingConstants.CENTER); //
			if (itemVO == null) {
				label.setForeground(java.awt.Color.RED); //
				label.setToolTipText("没有找到对应的ItemKey"); //
			} else {
				if ("Y".equals(itemVO.getIsmustinput2())) {
					_ismustinput = true;
				}
				if (itemVO.isNeedSave() && !itemVO.isCanSave()) { //
					label.setForeground(java.awt.Color.RED); //
				} else {
					label.setForeground(LookAndFeel.table_headFontColor); //表头字体颜色
				}
			}

			if (jtable != null) {
				JTableHeader jtableheader = jtable.getTableHeader();
				if (jtableheader != null) {
					String fg_bgcolor = itemVO.getShowbgcolor();
					if (fg_bgcolor == null || fg_bgcolor.trim().equals("")) {
						label.setBackground(LookAndFeel.tableheaderbgcolor);
					} else {
						String[] fg_bgcolors = fg_bgcolor.split(",");
						if (fg_bgcolors.length == 1) { //只设置前景色，背景色默认
							label.setForeground(getTBUtil().getColor(fg_bgcolors[0])); //设置前景色，即字体颜色
							label.setBackground(LookAndFeel.tableheaderbgcolor); //设置默认背景色
						} else if (fg_bgcolors.length > 1) {
							label.setForeground(getTBUtil().getColor(fg_bgcolors[0])); //设置前景色，即字体颜色
							label.setBackground(getTBUtil().getColor(fg_bgcolors[1])); //设置背景色
						}
					}
					label.setFont(jtableheader.getFont());
				}
			}
			//sunfujun/20120720/解决表头红星问题
			itemVO.isViewColumn();
			String str_realText = getTBUtil().getLableRealText(str_text, ClientEnvironment.getInstance().isAdmin(), _ismustinput && isHeadShowAsterisk(), itemVO.isViewColumn(), itemVO.isNeedSave(), isHeadShowAsterisk()); //
			label.setText(str_realText); ////
			label.setToolTipText(str_tooltiptext); //
			if (_ismustinput && isHeadShowAsterisk()) {//邮储项目中绝大多数字段都是必输项，表头都会有红星（必输项），提出表头不要显示红星，故增加此过滤【李春娟/2012-06-18】
				label.addStrItemColor("*", Color.RED); //
			}
			if (str_key.equals(str_currsortcolumnkey)) {
				if (li_currsorttype == 1) {
					label.setIcon(ImageIconFactory.getUpArrowIcon());
				} else if (li_currsorttype == -1) {
					label.setIcon(ImageIconFactory.getDownArrowIcon());
				} else {
					label.setIcon(null);
				}
			} else {
				label.setIcon(null);
			}
			//label.setForeground(LookAndFeel.table_headFontColor);
			label.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, LookAndFeel.tableHeadLineClolr)); //
			return label;
		}
	}

	//原来老孙在类变量中直接取值的!根据所有书上的介绍,不要在类变量的定义中去调一个函数! 极不好的习惯!
	private boolean isHeadShowAsterisk() {
		return getTBUtil().getSysOptionBooleanValue("列表表头是否显示必输项提示", true); //这行代码本身就是调用缓存的,所以没有性能问题!
	}

	/**
	 * 锁定列的表格的列头的绘制器..
	 * @author xch
	 *
	 */
	class LockTableHeaderRender extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private BillListPanel billList = null;

		public JCheckBox getCheckbox() {
			return checkbox;
		}

		private JTable table = null;
		private JCheckBox checkbox = null;

		public LockTableHeaderRender() {

		}

		public LockTableHeaderRender(BillListPanel _billList, JTable jtable) {
			this.billList = _billList;
			this.table = jtable;
			checkbox = new JCheckBox();
			jtable.getTableHeader().addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() > 0) {
						if (billList != null && billList.isRowNumberChecked) {
							int selectColumn = table.getTableHeader().columnAtPoint(e.getPoint());
							if (selectColumn == 0) {
								boolean value = !checkbox.isSelected();
								checkbox.setSelected(value);
								if (table.getRowCount() > 0) {
									for (int i = 0; i < table.getRowCount(); i++) {
										RowNumberItemVO numberVO = billList.getRowNumberVO(i);
										if (numberVO != null) {
											numberVO.setChecked(value);
										}
									}
									billList.repaint();
									billList.revalidate();
								}
								checkedTitleChanged();
							}
						}
					}
				}
			});
		}

		public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean isSelected, boolean hasFocus, int _row, int _column) {
			JLabel label2 = (JLabel) super.getTableCellRendererComponent(jtable, obj, isSelected, hasFocus, _row, _column); //
			JComponent compent = null;
			if (_column == 0) {
				if (billList == null || !billList.isRowNumberChecked()) { //如果是Label
					compent = new WLTLabel(label2.getText(), WLTPanel.VERTICAL_TOP_TO_BOTTOM2);
					((WLTLabel) compent).setHorizontalAlignment(SwingConstants.CENTER);
					((WLTLabel) compent).setFont(LookAndFeel.font_b); //
				} else {
					compent = checkbox;
					((JCheckBox) compent).setText(" " + label2.getText());
					compent.setFocusable(true);
					compent.setPreferredSize(new Dimension(12, 12));
					((JCheckBox) compent).setBorderPainted(true);
					((JCheckBox) compent).setMargin(new Insets(0, 0, 0, 0));
					((JCheckBox) compent).setFont(LookAndFeel.font_b);
				}
				compent.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, LookAndFeel.tableHeadLineClolr));
			} else {
				compent = new WLTLabel(label2.getText(), WLTPanel.VERTICAL_TOP_TO_BOTTOM2);
				((WLTLabel) compent).setHorizontalAlignment(SwingConstants.CENTER);
				((WLTLabel) compent).setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, LookAndFeel.tableHeadLineClolr)); //
				((WLTLabel) compent).setFont(LookAndFeel.font_b); //
			}
			compent.setForeground(LookAndFeel.table_headFontColor);
			compent.setBackground(LookAndFeel.tableheaderbgcolor);
			return compent; //
		}

		public BillListPanel getBillList() {
			return billList;
		}

		public void setBillList(BillListPanel billList) {
			this.billList = billList;
		}

	}

	class IntegerDoucument extends PlainDocument {
		private static final long serialVersionUID = -6061518182446972408L;

		public void insertString(int offset, String s, AttributeSet attributeSet) throws BadLocationException {
			char[] charofs = s.toCharArray();// 查看字符串的每个字符是否是数字
			boolean isNum = true;
			for (int i = 0; i < charofs.length; i++) {
				if ("-0123456789.".indexOf(charofs[i]) < 0) {
					isNum = false;
					break;
				}
			}
			if (isNum) { // 如果是数字就插入!!
				if (offset == 0 && (s.equals("."))) {
					Toolkit.getDefaultToolkit().beep(); // 放个声音!!
					return; //
				} else {
					super.insertString(offset, s, attributeSet);
				}
			} else {
				Toolkit.getDefaultToolkit().beep(); // 放个声音!!
				return; // 直接返回不插入了!!
			}
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
			if (li_row >= 0 != (tmptable.getCursor() == resizeCursor)) {
				swapCursor();
			}
		}

		public void mouseDragged(MouseEvent e) {
			int mouseY = e.getY();
			if (resizingRow >= 0) {
				int newHeight = mouseY - mouseYOffset;
				if (newHeight > 0) {
					if (e.isControlDown()) {
						int li_rowcount = tmptable.getRowCount();
						for (int i = 0; i < li_rowcount; i++) {
							tmptable.setRowHeight(i, newHeight); // 设置新的行高..
							table.setRowHeight(i, newHeight); // 设置主表中的行高
						}
					} else {
						tmptable.setRowHeight(resizingRow, newHeight); // 设置新的行高..
						table.setRowHeight(resizingRow, newHeight); // 设置主表中的行高
					}
				}
			}
		}
	}

	class HtmlHrefCursor extends MouseInputAdapter {
		//public Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		private int mouseYOffset, resizingRow;
		//private Cursor otherCursor = resizeCursor;
		private JTable tmptable;
		private int li_toolTipLine = 25; //

		public HtmlHrefCursor(JTable table) {
			this.tmptable = table;
			table.addMouseListener(this);
			table.addMouseMotionListener(this);
		}

		public void mouseMoved(MouseEvent e) {
			Point point = e.getPoint(); //
			// int li_row = getResizingRow(e.getPoint());
			int li_col = tmptable.columnAtPoint(point);
			int li_row = tmptable.rowAtPoint(point); //
			if (li_col < 0 || li_row < 0) {
				tmptable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				return;
			}

			TableColumn column = tmptable.getColumnModel().getColumn(li_col); //
			int li_colwidth = column.getPreferredWidth(); // 列的宽度...
			String str_itemkey = (String) column.getIdentifier(); //
			int bo_islisthtmlhref = findListIsHtmlHref(str_itemkey); // 是否是Html

			Object obj = tmptable.getValueAt(li_row, li_col);
			//tmptable.setToolTipText(null); //
			if (obj != null && obj.toString() != null && !obj.toString().equals("")) {
				String str_strtext = "" + obj; ////
				int li_textwidth = getTBUtil().getStrWidth(tmptable.getFont(), str_strtext) + 15; ////
				if (li_textwidth > li_colwidth) { //如果字的宽度已超过了列宽,说明有字看不见了,所以要设下ToolTip
					int li_textlength = str_strtext.length(); //
					if (li_textlength > li_toolTipLine) { //如果超过字数!!!
						StringBuilder sb_toolTip = new StringBuilder(); //
						for (int i = 0; i < li_textlength / li_toolTipLine; i++) {
							sb_toolTip.append(str_strtext.substring(i * li_toolTipLine, (i + 1) * li_toolTipLine) + "\n"); ////
						}
						if (li_textlength % li_toolTipLine != 0) { //如果有余!!则补上剩下的!!!
							sb_toolTip.append(str_strtext.substring((li_textlength / li_toolTipLine) * li_toolTipLine, li_textlength) + "\n"); ////
						}
						tmptable.setToolTipText(sb_toolTip.toString() + str_tableToolTip); ////
					} else { //如果小于15个字则直接显示一行
						tmptable.setToolTipText(str_strtext + "\n" + str_tableToolTip); //
					}
				} else {
					tmptable.setToolTipText(str_tableToolTip); //
				}
			} else {
				tmptable.setToolTipText(str_tableToolTip); //
			}

			if (bo_islisthtmlhref < 0) { // 如果不是HtmlHref,则立即返回
				tmptable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				return;
			}

			if (obj == null || obj.toString().equals("")) {
				tmptable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); // 如果值为空,则立即返回...
				return;
			}

			Rectangle reg = tmptable.getCellRect(li_row, li_col, true);
			String str_value = obj.toString(); //

			int li_length = SwingUtilities.computeStringWidth(Toolkit.getDefaultToolkit().getFontMetrics(tmptable.getFont()), str_value); // 取得字长
			if ((point.getX() - reg.getX()) > li_length || (point.getX() - reg.getX()) > li_colwidth) {
				tmptable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); //
			} else {
				if (tmptable.getCursor().getType() != Cursor.WAIT_CURSOR) {
					tmptable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); //
				}
			}
		}

		public void mouseClicked(MouseEvent e) {
			if (tmptable.getCursor().getType() == Cursor.HAND_CURSOR) { // 首先必须是手型
				if (e.getClickCount() == 1 && e.getButton() == e.BUTTON1) {
					tmptable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); // 先置为默认
					Point point = e.getPoint(); //
					int li_col = tmptable.columnAtPoint(point);
					int li_row = tmptable.rowAtPoint(point); //
					if (li_col < 0 || li_row < 0) {
						return;
					}

					onListHtmlHrefClick(li_row, li_col, e.isShiftDown()); //
				}
			}
		}

		private int getUnicodeLength(String s) // 这个方法是指送入一个字符串,算出其字节长度,如果字符串中有个中文字符,那么他的长度就算2
		{
			char c;
			int j = 0;
			boolean bo_1 = false;
			if (s == null || s.length() == 0) {
				return 0;
			}
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) >= 0x100) {
					bo_1 = true;
					j = j + 2;
				} else {
					j = j + 1;
				}
			}
			return j;
		}

	}

	//获取CombineTable 【杨科/2013-07-23】 
	public CombineTable getCombineTable() {
		return (CombineTable) getTable();
	}

	//设置合并列 【杨科/2013-07-23】 
	public void setCombineColumns(ArrayList<Integer> combineCols) {
		this.combineColumns = combineCols;
		getCombineTable().setCombineColumns(getTableModel(), combineColumns, 2);
		if (combineColumns != null) {
			getCombineTable().setCellSelectionEnabled(true); //单元格选择 【杨科/2013-07-26】 
		}
	}

	//合并刷新 【杨科/2013-07-23】 
	public void refreshCombineData() {
		if (bo_tableislockcolumn) { //锁定 【杨科/2013-07-23】 
			refreshCombineData_header(lock_pos);
			return;
		}

		lock_pos = -1;
		if (combine_mark && combineColumns != null && getTableModel().getRealValueAtModel() != null) {
			getCombineTable().setCombineColumns(getTableModel(), combineColumns, 2);
		}
	}

	//锁定 合并刷新 【杨科/2013-07-23】 
	public void refreshCombineData_header(int pos) {
		if (combine_mark && combineColumns != null && getTableModel().getRealValueAtModel() != null) {
			ArrayList<Integer> tableCombineColumns = new ArrayList<Integer>();
			ArrayList<Integer> headerCombineColumns = new ArrayList<Integer>();
			for (Integer column : combineColumns) {
				if (column <= pos) {
					headerCombineColumns.add(column + 1);
				} else {
					tableCombineColumns.add(column - pos - 1); //行隐藏
				}
			}

			getCombineTable().setCombineColumns(getTableModel(), tableCombineColumns, 2 + pos + 1);
			((CombineTable) rowHeaderTable).setCombineColumns(getTableModel(), headerCombineColumns, 1); //header
			lock_pos = pos;
		}
	}

	class HtmlHrefTable extends CombineTable { //继承CombineTable 合并 【杨科/2013-07-23】 
		private static final long serialVersionUID = 1L;

		public HtmlHrefTable(BillListModel _tableModel, TableColumnModel _columnModel) {
			super(_tableModel, _columnModel); //
			new HtmlHrefCursor(this); //
		}

		public JToolTip createToolTip() {
			MultiLineToolTip tip = new MultiLineToolTip();
			tip.setComponent(this);
			return tip;
		}

		public void changeSelection(int row, int column, boolean toggle, boolean extend) {
			super.changeSelection(row, column, toggle, extend);
			repaint();
			if (combineColumns != null) { //同步序号列 【杨科/2013-07-26】 
				int span = this.combineData.span(row, column);
				rowHeaderTable.setRowSelectionInterval(row, row + span - 1);
				rowHeaderTable.repaint();
			}
		}

	}

	class ResizableTable extends CombineTable { //继承CombineTable 合并 【杨科/2013-07-23】 
		private static final long serialVersionUID = 1L;
		protected MouseInputAdapter rowResizer, columnResizer = null;

		public ResizableTable(TableModel dm) {
			super(dm);
			new TableRowResizer(this);
		}

		public ResizableTable(Object[][] strings, Object[] strings2) {
			super(strings, strings2);//
			new TableRowResizer(this); //
		}

		public ResizableTable(BillListModel tableModel, DefaultTableColumnModel rowHeaderColumnModel) {
			super(tableModel, rowHeaderColumnModel); //
			new TableRowResizer(this); //
		}

		public void changeSelection(int row, int column, boolean toggle, boolean extend) {
			if (getCursor().getType() == Cursor.N_RESIZE_CURSOR)
				return;
			super.changeSelection(row, column, toggle, extend);
			if (combineColumns != null) { //同步表格 【杨科/2013-07-26】 
				getCombineTable().repaint();
			}
		}
	}

	public int getLi_TotalRecordCount() {
		return li_TotalRecordCount;
	}

	public void setLi_TotalRecordCount(int liTotalRecordCount) {
		li_TotalRecordCount = liTotalRecordCount;
	}

	public int getLi_onepagerecords() {
		return li_onepagerecords;
	}

	public String getIsRefreshParent() {
		return isRefreshParent;
	}

	public void setIsRefreshParent(String isRefreshParent) {
		this.isRefreshParent = isRefreshParent;
	}

	public boolean isCanRefreshParent() {
		return canRefreshParent;
	}

	public void setCanRefreshParent(boolean canRefreshParent) {
		this.canRefreshParent = canRefreshParent;
	}

}