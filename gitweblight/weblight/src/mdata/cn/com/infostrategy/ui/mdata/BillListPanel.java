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

	private JPanel unTitlePanel = null; //���˱�����֮��������������ݵ����!ʵ���Ͼ�������(���ٲ�ѯ,��������,���) 
	private JPanel centerPanel = null; //�������������!!

	private JPanel titlePanel = null; //�������
	private JLabel label_temname = null; //

	private boolean isHiddenUntitlePanelBtnvVisiable = false; //
	private boolean isToolBarPanelDownRight = false; //�Ƿ񹤾���ͻ����ʾ! ��������Ŀ��UIҪ����ǹ�������ͻ��������ʾ��!!!

	private WLTButton btn_shuntitle, btn_shqq = null; //��ʾ/���ؿ��ٲ�ѯ���İ�ť..
	private ImageIcon icon_showqq = null;
	private ImageIcon icon_hiddenqq = null;

	private Vector v_listbtnListener = new Vector(); // �Զ��尴ť�����¼�
	private Vector v_listHtmlHrefListener = new Vector(); // �Զ��尴ť�����¼�
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

	private String str_realsql = null; // ʵ�ʵ�SQL
	private String str_calltrackmsg = null; //���õ���־!!!

	private String str_dataPolicyInfo = null; //����Ȩ�޵�˵��
	private String str_dataPolicySQL = null; //����Ȩ�޵�˵��

	private Object[][] all_realValueData = null; //��ǰ����!!!

	private int li_currpage = 1; //��ǰҳ!!!
	private int li_onepagerecords = 50; //ÿҳ��ʾ����,Ĭ����50!!! ��Ϊ50 Gwang 2013-10-17
	private int li_TotalRecordCount = 0; //���м�¼��!!!
	private JComboBox jComboBox_onePageRecords = null; // ÿҳ�ж�������¼

	private static int datalength = 0;

	private int li_mouse_x, li_mouse_y = 0;
	boolean bo_tableislockcolumn = false; //
	private Vector v_lockedcolumns = new Vector();
	private boolean showPopMenu = true;

	private TBUtil tBUtil = null; // ת������!!
	private AbstractWorkPanel loadedWorkPanel = null; //���ظ��б�ĳ��������
	private BillFormatPanel loaderBillFormatPanel = null; //���ظ��б�Ĺ�ʽ���
	private CardCPanel_ChildTable loaderChildTable = null; //���ظ��б�������ӱ�

	private JButton firstPageButton, pageupButton, pagedownButton, lastPageButton, goToPageButton = null;

	private boolean bo_isShowPageNavigation = false; // �Ƿ���ʾ��ҳ������..
	private boolean bo_isShowOperatorNavigation = false;

	public JTextField goToPageTextField = null;
	public JLabel label_pagedesc = null;

	public String str_rownumberMark = "_RECORD_ROW_NUMBER";
	public String str_rownumberMarkName = " "; // �кŵ�����

	public boolean bo_ifProgramIsEditing = false; // �Ƿ���������༭

	public int[] searchcolumn = null;
	public String str_currsortcolumnkey = null;
	public int li_currsorttype = 0;
	private Vector v_deleted_row = new Vector();

	private int templete_codecolumn_index = -1;
	private JPanel toolbarPanel = null; //
	protected JPanel toolbarPanel_content = null;

	private BillButtonPanel billListBtnPanel = null; // ��ť���
	private WorkflowDealBtnPanel wf_btnPanel = null; // ��������

	protected JPanel customerNavigationJPanel = new JPanel();
	private JPanel pagePanel = null;

	private AbstractListCustomerButtonBarPanel listCustPanel = null;

	private Vector v_listeners = new Vector(); // ����ע����¼�������!!!
	private Vector v_selectedListeners = new Vector(); // ����ע����¼�������!!!
	private Vector v_checkedAllListeners = new Vector();// ������ŵĹ�ѡ�� Ԭ���� 20131029 ���
	private Vector v_afterqueryListeners = new Vector(); // ����ע����¼�������!!!,��ѯ����еĴ���
	private Vector v_MouseDoubleClickListeners = new Vector(); // ����ע����¼�������!!!,��ѯ����еĴ���

	private boolean is_admin = false;

	private Hashtable ht_groupcolumn = new Hashtable();
	private int li_rowno_width = 37; // �кſ��,��Ϊ0����ȥ��,37
	private HashMap hm_initeditable = new HashMap(); //

	private JMenuItem menuItem_showuitruedata, menuItem_showdbtruedata, menuItem_showCard, menuItem_dirdelrecord, menuItem_dirupdaterecord, menuItem_dirdelworkflowinfo; //ֱ��ɾ�����޸ļ�¼
	private JPopupMenu rightPopMenu = null; // �һ�������Ҽ�ʱ�����Ĳ˵�
	private ActionListener recordPopMenuAction = null; //

	private JPopupMenu popmenu_header = null; // �����ͷ���Ҽ�ʱ�����Ĳ˵�
	private JMenu menu_table_templetmodify, menu_excel, menu_db; //
	private JMenuItem item_column_lock, item_column_search, item_column_quickputvalue, item_column_quickstrike, item_column_weidusearch, item_column_quicksavecurrwidth, item_column_quicksaverowheight, item_table_setLineVisiable, menu_table_exportprint, menu_table_exporthtml, item_column_lookhelp, item_column_piechart, item_table_showhidecolumn, item_table_resetOrderCons,
			item_table_templetmodify_1, item_table_templetmodify_2, item_state;
	private JMenuItem menu_db_addflow4_database, menu_db_addflow4_templet, menu_db_addflow13_templet, menu_transportToDataSource, item_table_showsql, menu_db_autoBuildData;
	private ActionListener headerPopMenuAction = null; //

	public boolean isCanShowCardInfo = true; //�Ƿ���ʾ��Ƭ��Ϣ
	public boolean isHeaderCanSort = true; //��ͷ˫���Ƿ��������
	private boolean isRowNumberChecked = false;//  ��ͷ�Ƿ��ǹ�ѡ�����ʽ!����ǰ��ѡ��ͬ,�����ͻ�ϲ����ѡ����ʽ,�Ͼ�������Ŀ�о�������˵����������ѡ����ʱCtrl������!!

	private FrameWorkMetaDataServiceIfc metaDataService = null; //Ԫ����Զ�̷���.
	private String str_tableToolTip = "��ʾ:��סCtrl�����,�ɶ�ѡ��ȡ����ѡ��¼!"; //�����������,��Ϊ������˲�֪����ζ�ѡ��ȡ���������ѡ����!!!������ȥ,��ʡ�µľ�����Զ���������!!

	private Logger logger = WLTLogger.getLogger(BillListPanel.class); //
	private java.awt.Dimension oldDimension = null; //
	private HashMap linkChildTempletVOMap = null; //�洢�����ӱ�ģ��VO��map,��Ϊ������ȡ����ʱ,��Ҫȡ�������ӱ��е�����,����һ���б���˵,���ѡ���������,��Ҫÿһ�����ݶ�Ҫ����һ�������ӱ��ģ��VO(��������̫��),���Ը�һ��Map

	private String isRefreshParent = "-1"; //Ԭ�������  ��Ҫ������ʾ�����billlistΪ��ҳ�棬����ر���ҳ���Ƿ���Ҫˢ�¸�billlist,��������ˢ�£��������Ϊ1��ˢ��
	private boolean canRefreshParent = false; //Ԭ���� 20130509�޸� 
	private boolean combine_mark = true; //�ϲ�����������ʼ�����ݱ�� �����/2013-07-23�� 
	private ArrayList<Integer> combineColumns = null; //�ϲ��� �����/2013-07-23�� 
	private int lock_pos = -1; //�ϲ�������� �����/2013-07-23�� 
	private ListCellEditor_RowNumber editor = null;// Ԭ���� 20131029 ��� ��Ҫ����Ƿ�ѡ�й�ѡ��
	private AbstractBillListQueryCallback listQueryCallback;
	private ExcelUtil excel = new ExcelUtil();
	private int [] color;//zzl �޸ı�����ɫ

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
	 * ʹ��ģ����봴��!!
	 * @param _templetcode
	 * @param _isAutoInitialize �Ƿ��Զ���ʼʼ��ҳ��!����������initialize()����! ��Ϊ��������Ҫ�ڹ�������֮ǰ�޸�һЩ����! ����������Ҫ�ֹ�����initialize��!
	 */
	public BillListPanel(String _templetcode, boolean _isAutoInitialize) {
		if (_templetcode.indexOf(".") > 0) { // ����Ǹ�����,���������и�".",��������Ϊ�Ǹ�����!!��ֱ�ӷ������
			try {
				init((AbstractTMO) Class.forName(_templetcode).newInstance(), _isAutoInitialize);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		} else { // ����������ı���!
			init(_templetcode, _isAutoInitialize);
		}
	}

	public BillListPanel(String _templetcode, boolean _isAutoInitialize, boolean _isToolBarPanelDownRight) {
		this.isToolBarPanelDownRight = _isToolBarPanelDownRight; //�Ƿ񹤾���������ʾ,������UIЧ��!!��ʱ����������췽��,ʵ���ϵ���һ���б�Ч���ǿ��Ե�,���������һ�����ӽ������м����б�����!!!
		if (_templetcode.indexOf(".") > 0) { // ����Ǹ�����,���������и�".",��������Ϊ�Ǹ�����!!��ֱ�ӷ������
			try {
				init((AbstractTMO) Class.forName(_templetcode).newInstance(), _isAutoInitialize);
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		} else { // ����������ı���!
			init(_templetcode, _isAutoInitialize);
		}
	}

	/**
	 * ���޸���һ��SQL����������һ���б�
	 * 
	 * @param _daName
	 * @param _sql
	 */
	public BillListPanel(String _dsanme, String _sql) {
		try {
			HashVOStruct hashVOStructs = UIUtil.getHashVoStructByDS(_dsanme, _sql); // ����ȡ����!!!
			String[] allcolumns = hashVOStructs.getHeaderName(); // ���е�����
			HashVO[] hvs = hashVOStructs.getHashVOs(); // ���е�����

			templetVO = UIUtil.getPub_Templet_1VO(getTMO(allcolumns, 125)); // Ԫԭģ��VO
			templetItemVOs = templetVO.getItemVos(); // ����
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
	 * @param zzl  �������ɫ
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
	 * @param zzl  ȫ����ɫ
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
	 * ���Դ�һ�����ֽ�ȥ  �õڼ�����ʾ��ɫ
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
	 * ���޸���һ��SQL����������һ���б�
	 * 
	 * @param _daName
	 * @param _sql
	 */
	public BillListPanel(HashVOStruct _hashVOStructs, int _width) {
		try {
			String[] allcolumns = _hashVOStructs.getHeaderName(); // ���е�����
			HashVO[] hvs = _hashVOStructs.getHashVOs(); // ���е�����
			templetVO = UIUtil.getPub_Templet_1VO(getTMO(allcolumns, _width)); // Ԫԭģ��VO
			templetItemVOs = templetVO.getItemVos(); // ����
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

	//ֱ��ʹ��HashVO[]����,���
	public BillListPanel(String _templetName, HashVO[] _hvs) {
		try {
			int[] li_colWidths = new int[] { 85 }; //
			String[] allcolumns = null; //
			if (_hvs == null || _hvs.length <= 0) {
				allcolumns = new String[] { "ֱ��ʹ��HashVO[]����,������Ϊ��" }; //
				li_colWidths = new int[] { 350 }; //
			} else {
				allcolumns = _hvs[0].getKeys(); //
				int[] li_widths = new int[allcolumns.length]; //���ܸ������ݵĿ�ȼ����п�!!
				for (int j = 0; j < li_widths.length; j++) {
					li_widths[j] = 85; //
				}
				for (int j = 0; j < allcolumns.length; j++) {
					for (int i = 0; i < _hvs.length; i++) {
						String str_text = _hvs[i].getStringValue(allcolumns[j]); //
						int li_w = getTBUtil().getStrUnicodeLength(str_text) * 8; //
						li_w = (li_w > 300 ? 300 : li_w); //������300
						if (li_w > li_widths[j]) {
							li_widths[j] = li_w; //
						}
					}
				}
				li_colWidths = li_widths; //
			}
			templetVO = UIUtil.getPub_Templet_1VO(getTMO(allcolumns, li_colWidths, _templetName)); // Ԫԭģ��VO
			templetItemVOs = templetVO.getItemVos(); // ����
			str_templetcode = templetVO.getTempletcode();
			initialize();
			putValue(_hvs);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * ����ֱ�����ɵĳ���ģ��VO����ҳ��!!!,���������ݲ����� pub_teplet_1����pub_teplet_1_item���е�!!!
	 * 
	 * @param _abstractTempletVO
	 */
	public BillListPanel(AbstractTMO _abstractTempletVO) {
		init(_abstractTempletVO, true);
	}

	/**
	 * ����ֱ�����ɵĳ���ģ��VO����ҳ��!!!,���������ݲ����� pub_teplet_1����pub_teplet_1_item���е�!!!
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
	 * ֱ�Ӹ���ServerTMO����
	 * @param _serverTMO
	 */
	public BillListPanel(ServerTMODefine _serverTMO, boolean _isAutoInit) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_serverTMO); //
			templetItemVOs = templetVO.getItemVos(); // ����
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
		templetItemVOs = templetVO.getItemVos(); // ����
		str_templetcode = templetVO.getTempletcode();
		if (_isAutoInitialize) { //�Ƿ��Զ���ʼʼ��
			initialize();
		}
	}

	private void init(String _templetcode, boolean _isAutoInitialize) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_templetcode);
			templetItemVOs = templetVO.getItemVos(); // ����
			str_templetcode = templetVO.getTempletcode();
			if (_isAutoInitialize) {
				initialize();
			}
		} catch (Exception e) {
			MessageBox.showException(this, e);
		} // ȡ�õ���ģ��VO
	}

	private void init(AbstractTMO _abstractTempletVO, boolean _isAutoInitialize) {
		try {
			templetVO = UIUtil.getPub_Templet_1VO(_abstractTempletVO);
			templetItemVOs = templetVO.getItemVos(); // ����
			str_templetcode = _abstractTempletVO.getPub_templet_1Data().getStringValue("templetcode"); //
			if (_isAutoInitialize) {
				initialize(); //
			}
		} catch (Exception e) {
			MessageBox.showException(this, e);
		} //	
	}

	/**
	 * ����ֱ�����ɵĳ���ģ��VO����ҳ��!!!,���������ݲ����� pub_teplet_1����pub_teplet_1_item���е�!!!
	 * @param _abstractTempletVO
	 */
	public void initialize() {
		DeskTopPanel.setSelectInfo("");//ÿ�γ�ʼ�������ѡ�е�Ԫ��״̬��Ϣ[YangQing/2013-09-25]
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getListiscombine() && i > 0) { //�б��Ƿ�ϲ� �����/2013-07-26�� 
				if (combineColumns == null) {
					combineColumns = new ArrayList<Integer>();
				}
				combineColumns.add(i - 1);
			}
			hm_initeditable.put(templetItemVOs[i].getItemkey(), templetItemVOs[i].getListiseditable()); //�Ƿ�ǿ�༭!!��������!!
		}

		this.removeAll(); //
		this.setLayout(new BorderLayout());
		this.setBackground(LookAndFeel.defaultShadeColor1); //ͳͳ��ϵͳĬ����ɫ!!
		this.setUI(new WLTPanelUI(BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE, Color.WHITE, false)); //
		//this.setOpaque(false); //
		if (ClientEnvironment.getInstance().getLoginModel() == ClientEnvironment.LOGINMODEL_ADMIN) {
			this.is_admin = true;
		}

		this.bo_isShowPageNavigation = templetVO.getIsshowlistpagebar().booleanValue(); // �Ƿ���ʾ��ҳ������
		this.bo_isShowOperatorNavigation = templetVO.getIsshowlistopebar().booleanValue(); // �Ƿ���ʾ���ݿ����������

		TitledBorder border = BorderFactory.createTitledBorder("[" + templetVO.getTempletname() + "]");
		border.setTitleFont(new Font("����", Font.PLAIN, 12 + LookAndFeel.getFONT_REVISE_SIZE()));
		border.setTitleColor(Color.BLUE); //

		scrollPanel_main = new JScrollPane(); //
		scrollPanel_main.setOpaque(false); //͸��!!
		scrollPanel_main.getViewport().setOpaque(false); //͸��!!!
		scrollPanel_main.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);//�︻��
		scrollPanel_main.getViewport().add(getTable()); // ʵ�����ݵı��!!

		rowHeaderColumnModel = new DefaultTableColumnModel();
		rowHeaderColumnModel.addColumn(getRowNumberColumn()); //�����к���!���ﴦ�����к�!!!

		rowHeaderTable = new ResizableTable(getTableModel(), rowHeaderColumnModel); // �����µı�..
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
		scrollPanel_main.setRowHeader(jv); //�ؼ�����,�����кű���ڹ���������!!

		if ("DB".equals(this.getTempletVO().getBuildFromType())) { //����������:XML2,DB,CLASS
			rowHeaderTable.getColumnModel().getColumn(0).setHeaderValue("-");
		} else if ("CLASS".equals(this.getTempletVO().getBuildFromType())) {
			rowHeaderTable.getColumnModel().getColumn(0).setHeaderValue("=");
		}

		scrollPanel_main.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowHeaderTable.getTableHeader());
		scrollPanel_main.setBorder(BorderFactory.createEmptyBorder()); //

		JViewport jv2 = new JViewport();
		jv2.setOpaque(false); //һ��Ҫ��һ�£������ϱ����и�����
		jv2.setView(getTable().getTableHeader());
		scrollPanel_main.setColumnHeader(jv2); //

		//��ʱ���,�����ٲ�ѯ�빤������װ
		JPanel temp_panel_1 = new JPanel(new BorderLayout()); //���������Ĺ�����!!
		temp_panel_1.setOpaque(false); //͸��!!!!
		temp_panel_1.add(getToolbarPanel(), BorderLayout.NORTH); //
		temp_panel_1.add(scrollPanel_main, BorderLayout.CENTER); //

		if (LookAndFeel.getBoleanValue("�б��ҳ���Ƿ�������", false)) { //����û��������ҳҪ��������!����Ҫ�Ӹ�����,Ĭ���Ƿ��������!
			if (bo_isShowPageNavigation) { //�����ȷ�з�ҳ��
				JPanel page_downtmp_panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); //
				page_downtmp_panel.setOpaque(false); //͸��!
				page_downtmp_panel.setMinimumSize(new Dimension(100, 26)); //
				page_downtmp_panel.add(getPagePanel()); //
				page_downtmp_panel.add(getPageDescLabel()); //
				temp_panel_1.add(page_downtmp_panel, BorderLayout.SOUTH); //��ҳ
			} else { //���������û�з�ҳ��,��Ҳ����!!

			}
		}

		unTitlePanel = new JPanel(new BorderLayout()); //��ѯ���������(����빤����)
		unTitlePanel.setOpaque(false); //͸��!!!!
		unTitlePanel.add(temp_panel_1, BorderLayout.CENTER); //

		centerPanel = new JPanel(new BorderLayout()); //������һ��Ҫ,��Ϊ������Ҫ�ػ������!!��������֮!!
		centerPanel.setOpaque(false); //͸��!!!!
		centerPanel.add(getTitlePanel(), BorderLayout.NORTH); //

		centerPanel.add(unTitlePanel, BorderLayout.CENTER); //
		this.add(centerPanel, BorderLayout.CENTER); //

		if (templetVO.getIsshowlistquickquery() && !templetVO.getIscollapsequickquery()) { //�����ʾ���Ҳ���������,�����֮!
			unTitlePanel.add(getQuickQueryPanel(), BorderLayout.NORTH); //
		}

		// ��ʼ�������˵�
		rightPopMenu = new JPopupMenu(); //
		menuItem_showuitruedata = new JMenuItem("�鿴UI��ʵ������", UIUtil.getImage("office_086.gif")); //
		menuItem_showdbtruedata = new JMenuItem("�鿴DB��ʵ������", UIUtil.getImage("office_009.gif")); //
		menuItem_showCard = new JMenuItem("��Ƭ���", UIUtil.getImage("office_065.gif")); //
		menuItem_dirdelrecord = new JMenuItem("ֱ��ɾ��", UIUtil.getImage("del.gif")); //ֱ��ɾ��
		menuItem_dirupdaterecord = new JMenuItem("ֱ���޸�", UIUtil.getImage("office_026.gif")); //ֱ���޸ļ�¼�ֶ�ֵ
		menuItem_dirdelworkflowinfo = new JMenuItem("�������������", UIUtil.getImage("office_144.gif")); //ֱ����չ���������

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

		//��ʱֱ�Ӳ�ѯ����ǰ20������
		if (templetVO != null && templetVO.getAutoLoads() != 0) {
			autoLoadPartData(); //�Զ���������
		}

		if (combineColumns != null) { //�б�ϲ� �����/2013-07-26�� 
			setCombineColumns(combineColumns);
		}
	}

	//�Զ����ز�������
	private void autoLoadPartData() {
		String str_dsName = getDataSourceName(); //����Դ����
		String str_autoLoadSql = getAutoLoadSQL(); //�Զ�����ʱ��SQL���,�����ѯʱ��SQL��������һ��!!!�����Զ�����ʱֻ���״̬Ϊδ����Ļ���¼���(������ĵļ�ʱ���ݶ�����ʷ����)!!����ѯʱ���Բ�ѯȫ��!!!
		queryDataByDS(str_dsName, str_autoLoadSql); //��ѯ����,���ҿ���ȡǰ����������
	}

	/**
	 * �����Ƿ�͸��...
	 * @param _opaque
	 */
	public void setBillListOpaque(boolean _opaque) {
		this.setOpaque(_opaque); //
		if (_opaque) {//��͸��
			this.setUI(new WLTPanelUI(BackGroundDrawingUtil.INCLINE_NW_TO_SE));
		} else { //͸��
			this.setUI((PanelUI) UIManager.getUI(this));
		}

	}


	public void setBillListTitleName(String _text) {
		getTitlePanel().setVisible(true); //
		label_temname.setText("��" + _text); //
	}

	/**
	 * zzl �޸ı�����ɫ
	 * @param _text
	 */
	public void setBillListTitleName(String _text,int a,int b,int c) {
		getTitlePanel().setVisible(true); //
		label_temname.setText("��" + _text); //
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
				throw new WLTAppException("ģ��[" + this.str_templetcode + "]��û�ж������Ϊ[" + _code + "]���б�ť,Ϊ���ܹ��ɹ���ʼ��ҳ��,ϵͳ������һ�����ɼ��İ�ť!"); //
			} else {
				return btn_tmp; //
			}
		} catch (Exception e) {
			MessageBox.showException(this, e);
			return new WLTButton("�����ڵİ�ť[" + _code + "]"); //Ϊ�˷�ֹ��null�쳣��������ʾ����,����һ����ʱ��ť
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
		//boolean isToolBarPanelDownRight = LookAndFeel.getBoleanValue("�б������Ƿ�ͻ��", false); //������Ŀ��ΪUIǿ��Ҫ����EACͳһ!!��������UI�е��б�������ͻ����ʾ��! ����͸���ĵ�����һ��!
		if (isToolBarPanelDownRight) { //����ǹ���������������ʾ!!!
			toolbarPanel_content = new WLTPanel(WLTPanel.VERTICAL_LIGHT2, new FlowLayout(FlowLayout.LEFT, 1, 1), LookAndFeel.table_toolBarBgcolor, false); //
		} else {
			toolbarPanel_content = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1)); //
			toolbarPanel_content.setOpaque(false); //͸��!!!
		}

		toolbarPanel_content.add(getBillListBtnPanel(false)); //�ȼ��밴ť!!!û�й�����
		customerNavigationJPanel.setLayout(new BorderLayout()); //
		customerNavigationJPanel.setOpaque(false); //
		toolbarPanel_content.add(customerNavigationJPanel); //������ӿؼ������ɵ������
		if (getListCustPanel() != null) { //������Զ������!!
			toolbarPanel_content.add(getListCustPanel()); //�ټ�����ģ���ж���İ�ť
		}

		boolean isPageBtnPanelWrap = templetVO.getIslistpagebarwrap(); //��ҳ���Ƿ�����һ��!!
		if (!bo_isShowPageNavigation) { //�������ʾ��ҳ,�򲻹���������,��Զ��������ĺ���!!
			toolbarPanel_content.add(getPageDescLabel()); //�ټ����ҳ˵��!!!�����Զ��Ҫ��!!
		} else {
			if (!LookAndFeel.getBoleanValue("�б��ҳ���Ƿ�������", false) && !isPageBtnPanelWrap) { //�����ҳ������������,��������,��ż����ҳ��!! ��Ϊ����û��������ҳҪ��������!
				toolbarPanel_content.add(getPagePanel()); //�ټ����ҳ��!���ָ���˲���ʾ��ҳ,��Ĭ�������ص�!!!!	
				toolbarPanel_content.add(getPageDescLabel()); //�ټ����ҳ˵��!!!�����Զ��Ҫ��!!
			}
		}

		WLTScrollPanel scrollPanel = new WLTScrollPanel(toolbarPanel_content);
		scrollPanel.setOpaque(false);
		toolbarPanel = new WLTPanel(new BorderLayout(0, 0)); //
		toolbarPanel.setBorder(BorderFactory.createEmptyBorder(1, 0, 1, 0)); //
		toolbarPanel.setOpaque(false);
		toolbarPanel.add(scrollPanel, BorderLayout.CENTER); //

		if (!LookAndFeel.getBoleanValue("�б��ҳ���Ƿ�������", false) && isPageBtnPanelWrap && bo_isShowPageNavigation) { //�����ҳ��������,������һ��!!
			JPanel pageBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 1)); //
			pageBtn.setOpaque(false); //͸��!!
			pageBtn.add(getPagePanel()); //
			pageBtn.add(getPageDescLabel()); //
			toolbarPanel.add(pageBtn, BorderLayout.SOUTH); //
		}
		return toolbarPanel; //
	}

	/**
	 * ���ù������ı�����ɫ..
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
	 * ���ӹ��������...
	 */
	public void addWorkFlowDealPanel() {
		wf_btnPanel = new WorkflowDealBtnPanel(this); // ��������尴ť
		customerNavigationJPanel.add(wf_btnPanel); //

		customerNavigationJPanel.repaint(); //
		toolbarPanel_content.repaint(); //
	}

	/**
	 * �ڰ�ť���������Զ������!!
	 * @param _custPanel
	 */
	public void addCustBtnPanel(JPanel _custPanel) {
		customerNavigationJPanel.add(_custPanel); //
		customerNavigationJPanel.repaint(); //
		toolbarPanel_content.repaint(); //
	}

	/**
	 * ȡ�ù������
	 * 
	 * @return
	 */
	public WorkflowDealBtnPanel getWorkflowDealBtnPanel() {
		return wf_btnPanel;
	}

	/**
	 * ���ù���������Ƿ���ʾ.,ֱ�ӵ���setVisiable����ֻ�������.
	 */
	public void setToolbarVisiable(boolean _visiable) {
		if (_visiable) {
			getToolbarPanel().setPreferredSize(new Dimension((int) getToolbarPanel().getPreferredSize().getWidth(), 22)); //
		} else {
			getToolbarPanel().setPreferredSize(new Dimension((int) getToolbarPanel().getPreferredSize().getWidth(), 0)); //	
		}
	}

	/**
	 * ���ر������!!
	 * @return
	 */
	public JPanel getTitlePanel() {
		if (titlePanel != null) {
			return titlePanel;
		}

		titlePanel = new JPanel(new BorderLayout(3, 0)); //
		titlePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		titlePanel.setOpaque(false); //͸��
		titlePanel.add(getTitleLabel(), BorderLayout.CENTER); //		

		//��ʾ/���طǳ�������֮��������������!�ڹ�����������Ҫ!!
		if (isHiddenUntitlePanelBtnvVisiable) { //�����������ʾ�����ť,����ʾ! Ĭ���ǲ���ʾ��!!
			btn_shuntitle = new WLTButton(LookAndFeel.treeExpandedIcon); //
			btn_shuntitle.setPreferredSize(new Dimension(16, 16)); //
			btn_shuntitle.setOpaque(false); //
			btn_shuntitle.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
			btn_shuntitle.setToolTipText("����/��ʾ�������"); //
			btn_shuntitle.setBorder(BorderFactory.createEmptyBorder()); //
			btn_shuntitle.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showOrHiddenUnTitlePanel(); //��ʾ�����ؿ��ٲ�ѯ���!!
				}
			}); //
			titlePanel.add(btn_shuntitle, BorderLayout.WEST); //
		}

		//����/��ʾ���ٲ�ѯ!!!
		icon_showqq = UIUtil.getImage("office_136.gif"); //����ͼ��
		//icon_hiddenqq = UIUtil.getImage("office_160.gif"); //չ��ͼ��
		icon_hiddenqq = new ImageIcon(getTBUtil().getImageRotate(icon_showqq.getImage(), 180)); //180��!
		btn_shqq = new WLTButton(icon_showqq); //
		btn_shqq.setPreferredSize(new Dimension(16, 16)); //
		btn_shqq.setOpaque(false);
		btn_shqq.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
		btn_shqq.setToolTipText("����/��ʾ��ѯ���"); //
		btn_shqq.setBorder(BorderFactory.createEmptyBorder()); //
		btn_shqq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showOrHiddenQuickQueryPanel(); //��ʾ�����ؿ��ٲ�ѯ���!!
			}
		}); //
		titlePanel.add(btn_shqq, BorderLayout.EAST); //

		//�������!
		if (templetVO.getIsshowlistquickquery()) { //���ָ����ʾ���ٲ�ѯ!!!
			btn_shqq.setVisible(true);
			if (templetVO.getIscollapsequickquery()) { //�����Ȩ�������Ļ�
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
	 * ���ر���Label!!!
	 * @return
	 */
	public JLabel getTitleLabel() {
		if (label_temname != null) {
			return label_temname;
		}

		label_temname = new JLabel("", JLabel.LEFT); //
		if (templetVO.getTempletname() != null && !templetVO.getTempletname().trim().equals("")) {
			label_temname.setText("��" + templetVO.getTempletname() + ""); //
		}
		label_temname.setFont(new Font("System", Font.PLAIN, 12 + LookAndFeel.getFONT_REVISE_SIZE())); //
		label_temname.setForeground(new Color(149, 149, 255)); //106, 106, 255 149, 149, 255
		label_temname.setOpaque(false);
		return label_temname;
	}

	/**
	 * ���ñ�������!!
	 * @param _text
	 */
	public void setTitleLabelText(String _text) {
		JLabel label = getTitleLabel(); //
		if (label != null) { //�����Ϊ��!!
			if (_text != null && _text.startsWith("��")) { //�������
				label.setText(_text); //
			} else {
				label.setText("��" + _text); //
			}
		}
	}

	public void setTableToolTipText(String _text) {
		str_tableToolTip = _text;
	}

	//��ʾ/���س�������֮�����������! �ڹ���������������õ�! �����̫��ʱ��Ҫ�и���!�������������
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
		//�������������б��ʱ��ѯ���������صġ���ô���һ�δ˰�ť��˵Ӧ����ʾ��������quickQueryPanel�ձ�ʵ��������visible=true�����Ե�һ�ε�����Ĳ����ǰ�������ء���������2012-02-29��
		if (quickQueryPanel == null) { //���Ĭ�����ز�ѯ��壬quickQueryPanel��û�б�ʵ��
			setBillQueryPanelVisible(true); //����չ��
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
	 * ���ò�ѯ����Ƿ���ʾ!
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
	 * �б�����Զ���һЩ���ٰ�ť
	 * 
	 * @return
	 */
	private BillButtonPanel getBillListBtnPanel(boolean _scrollable) {
		if (billListBtnPanel != null) {
			return billListBtnPanel;
		}

		billListBtnPanel = new BillButtonPanel(this.templetVO.getListcustbtns(), this, _scrollable); //
		billListBtnPanel.setOpaque(false);
		billListBtnPanel.paintButton(); //���ư�ť
		return billListBtnPanel;
	}

	/**
	 * ����һ����ť.�����������һ��repaintBillListButton()�ػ�һ�²��ܿ���Ч��!!!
	 * @param _btn
	 */
	public void addBillListButton(WLTButton _btn) {
		_btn.setBillPanelFrom(this); //�Ȱ󶨺��Լ�
		billListBtnPanel.addButton(_btn); //
	}

	/**
	 * ����һ����ť
	 * @param _btns
	 */
	public void addBatchBillListButton(WLTButton[] _btns) {
		for (int i = 0; i < _btns.length; i++) {
			if (_btns[i] != null) {//�ϲ�������Ŀ��ǰ�Ĵ��뷢�������δ����İ�ť�����ﱨ��ָ���쳣���˵��򲻿������ж�һ�¡����/2016-06-24��
				_btns[i].setBillPanelFrom(this); //�Ȱ󶨺��Լ�
			}
		}
		billListBtnPanel.addBatchButton(_btns);
	}

	/**
	 * ����һ����ť.
	 * @param _btn
	 */
	public void insertBillListButton(WLTButton _btn) {
		_btn.setBillPanelFrom(this); //�Ȱ󶨺��Լ�
		billListBtnPanel.insertButton(_btn); //
	}

	/**
	 * ����һ����ť.
	 * @param _btn
	 */
	public void insertBatchBillListButton(WLTButton[] _btns) {
		for (int i = 0; i < _btns.length; i++) {
			_btns[i].setBillPanelFrom(this); //�Ȱ󶨺��Լ�
		}
		billListBtnPanel.insertBatchButton(_btns); //
	}

	/**
	 * ���»��ư�ť
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
			if ("��ť".equals(getItemTypeByItemKey(str_itemkey))) { //��ť������!��Ϊ��ť�������߼�!!
				return;
			}
			if (v_listHtmlHrefListener.size() == 0) { //���û��һ����������Ĭ�ϴ򿪿�Ƭ��ʽ
				if (getTempletItemVO(str_itemkey).getUCDfVO() != null) {
					String linkedKey = getTempletItemVO(str_itemkey).getUCDfVO().getConfValue("html����key");
					//sunfujun/20120831/���������������������Ҫ����һ�����������б���һ��������ƾʹ򿪸���_�ʴ�
					if (linkedKey != null && !"".equals(linkedKey)) {
						if ("�ļ�ѡ���".equals(getItemTypeByItemKey(linkedKey))) {
							RefDialog_File refdialog_file = new RefDialog_File(this, getItemNameByItemKey(linkedKey), (RefItemVO) getValueAt(_row, linkedKey), this, getTempletItemVO(linkedKey).getUCDfVO());
							refdialog_file.setPubTempletItemVO(getTempletItemVO(linkedKey)); //
							refdialog_file.initialize();
							refdialog_file.setVisible(true);
							return;
						} else if ("Office�ؼ�".equals(getItemTypeByItemKey(linkedKey))) {
							String str_serverDir = "/officecompfile";
							CommUCDefineVO commUCDfVO = getTempletItemVO(linkedKey).getUCDfVO();
							if (commUCDfVO != null) {
								str_serverDir = commUCDfVO.getConfValue("�洢Ŀ¼", "/officecompfile"); //
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
				cardShowInfo(); //��Ƭ��ʾ
			} else {
				for (int i = 0; i < v_listHtmlHrefListener.size(); i++) {
					BillListHtmlHrefListener listener = (BillListHtmlHrefListener) v_listHtmlHrefListener.get(i); // �����Ӽ�����
					listener.onBillListHtmlHrefClicked(new BillListHtmlHrefEvent(str_itemkey, _row, _col, _isshiftdown, this)); // ��������
				}
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		} finally {
			this.table.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
	}

	/**
	 * �����б�ķ�ҳ����
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

		jComboBox_onePageRecords.setToolTipText("ÿҳ��ʾ�ļ�¼��");
		jComboBox_onePageRecords.addItem("10");
		jComboBox_onePageRecords.addItem("20");
		jComboBox_onePageRecords.addItem("50");
		jComboBox_onePageRecords.addItem("100");
		jComboBox_onePageRecords.addItem("200");
		jComboBox_onePageRecords.addItem("500");
		jComboBox_onePageRecords.addItem("1000"); //���Ӧ���ǳ��õ�����!!!
		jComboBox_onePageRecords.addItem("2000");
		jComboBox_onePageRecords.addItem("10000");//��ǰ��ȫ��,�����������������̫��ʱϵͳ����!�Һ�̨�����2000����,���Ըɴ���2000����!!
		jComboBox_onePageRecords.setSelectedIndex(2); //Ĭ����50
		jComboBox_onePageRecords.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					li_onepagerecords = Integer.parseInt((String) jComboBox_onePageRecords.getSelectedItem()); //��xch/2012-04-27������ÿҳ����!��ҵ�����ͻ����õ������仯����!
					//�ؼ����С�ɵ��������ѡÿҳ2000,�������������!!��ϵͳҪ�����������Է�ֹ���������������!!����֪�������������ͻ�ϵͳ����!!ϵͳҪ�����֡����ա���ƣ�
					String str_msg = "ÿҳ����ʾ[" + li_onepagerecords + "]������,�����²�ѯ!"; //
					if (li_onepagerecords >= 500) {
						str_msg = str_msg + "\r\n������ʾ:ÿҳ��ʾ����̫��ᵼ�������½�, �����ز���!";
						MessageBox.show(jComboBox_onePageRecords, str_msg); //
					} else {
						if (ClientEnvironment.getInstance().get("��������-�б�ÿҳ��ʾ��¼��") == null) { //���ָ���˲�Ҫ������!
							if (MessageBox.showOptionDialog(jComboBox_onePageRecords, str_msg, "��ʾ", new String[] { "ȷ ��", "��������" }) == 1) {
								ClientEnvironment.getInstance().put("��������-�б�ÿҳ��ʾ��¼��", "Y"); //
							}
						}
					}
				}
			}
		});

		JTextField cmb_textField = ((JTextField) ((JComponent) jComboBox_onePageRecords.getEditor().getEditorComponent())); ////
		cmb_textField.setEditable(false); //
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //�Զ���UI���Ѿ�ȥ��border��
			cmb_textField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
		}
		cmb_textField.setForeground(LookAndFeel.inputforecolor_enable); //
		cmb_textField.setBackground(LookAndFeel.inputbgcolor_enable); //

		pagePanel = new JPanel();
		pagePanel.setOpaque(false); //͸��
		pagePanel.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		pagePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
		pagePanel.setBorder(BorderFactory.createEmptyBorder(2, 1, 0, 0)); //

		boolean isPageBtnISPic = LookAndFeel.getBoleanValue("��ҳ��ť�Ƿ���ͼƬ", false); //��ǰ������Ŀ������������Ҫ���а�ť�������ֵ�,������ҳ��ť!! ��������Ŀ��Ф����������Ҫ��UIͳһ,������ֻ�ܸ������!! Ĭ�ϻ������ֵ�!!
		boolean isBtnOpaque = true; //
		Border btnBorder = null; //
		if (isPageBtnISPic) { //�����ͼƬ��ť!��ʹ��������ͼƬ! ���������һ���ͻ�Ҫ���ҳ��ť�ֲ�һ��,�ǲ�����Ҫ���һ��ͼƬ���ƵĲ���??
			firstPageButton = new WLTButton(UIUtil.getImage("zt_page_1.gif")); //UIUtil.getImage("pageFirst.gif")  "��ҳ"
			pageupButton = new WLTButton(UIUtil.getImage("zt_page_2.gif")); //UIUtil.getImage("pageUp2.gif") "��ҳ"
			pagedownButton = new WLTButton(UIUtil.getImage("zt_page_3.gif")); //UIUtil.getImage("pageDown2.gif") "��ҳ"
			lastPageButton = new WLTButton(UIUtil.getImage("zt_page_4.gif")); //UIUtil.getImage("pageLast.gif") "βҳ"
			isBtnOpaque = false; //
			btnBorder = BorderFactory.createEmptyBorder(); //�ձ߿�!!
		} else { //���ְ�ť!!
			firstPageButton = new WLTButton("��ҳ"); //
			pageupButton = new WLTButton("��ҳ"); //
			pagedownButton = new WLTButton("��ҳ"); //
			lastPageButton = new WLTButton("βҳ"); //
			firstPageButton.setPreferredSize(new Dimension(37, 18));
			pageupButton.setPreferredSize(new Dimension(37, 18));
			pagedownButton.setPreferredSize(new Dimension(37, 18));
			lastPageButton.setPreferredSize(new Dimension(37, 18));
			firstPageButton.setBackground(LookAndFeel.systembgcolor);
			pageupButton.setBackground(LookAndFeel.systembgcolor);
			pagedownButton.setBackground(LookAndFeel.systembgcolor);
			lastPageButton.setBackground(LookAndFeel.systembgcolor);
			isBtnOpaque = true; //
			btnBorder = BorderFactory.createLineBorder(LookAndFeel.compBorderLineColor, 1); //���ߵı߿�!!!
		}
		firstPageButton.setToolTipText("��һҳ"); //
		pageupButton.setToolTipText("��һҳ"); //
		pagedownButton.setToolTipText("��һҳ"); //
		lastPageButton.setToolTipText("���һҳ"); //

		firstPageButton.setOpaque(isBtnOpaque); //������͸��!!
		pageupButton.setOpaque(isBtnOpaque); //
		pagedownButton.setOpaque(isBtnOpaque); //
		lastPageButton.setOpaque(isBtnOpaque); //

		firstPageButton.setBorder(btnBorder); //
		pageupButton.setBorder(btnBorder); //
		pagedownButton.setBorder(btnBorder); //
		lastPageButton.setBorder(btnBorder); //

		Font btnFont = new Font("����", Font.PLAIN, 12); //
		firstPageButton.setFont(btnFont); //
		pageupButton.setFont(btnFont); //
		pagedownButton.setFont(btnFont); //
		lastPageButton.setFont(btnFont); //

		goToPageTextField = new JTextField("1");
		goToPageTextField.setForeground(Color.GRAY); //
		goToPageTextField.setHorizontalAlignment(SwingConstants.CENTER);

		goToPageButton = new WLTButton("ת"); //UIUtil.getImage("gotopage.gif")
		goToPageButton.setToolTipText("ֱ����ת��ĳһҳ"); //
		if (!"WebPushUIByHm".equalsIgnoreCase(UIManager.getLookAndFeel().getID())) { //�Զ���UI���Ѿ�ȥ��border��
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
						onGoToFirstPage(); // ������ҳ
					} else if (e.getSource() == pageupButton) {
						onGoToPreviousPage(); //ǰһҳ
					} else if (e.getSource() == pagedownButton) {
						onGoToNextPage(); //��һҳ
					} else if (e.getSource() == lastPageButton) {
						onGoToLastPage(); //���һҳ
					} else if (e.getSource() == goToPageButton) {
						onGoToPage(); //���һҳ
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

		if (!bo_isShowPageNavigation) { //�����ָ����ҳ
			pagePanel.setVisible(false); // ��ҳ
		}
		return pagePanel;
	}

	//̫ƽ��Ŀ����ƽ̨���
	public JComboBox getjComboBox_onePageRecords() {
		return jComboBox_onePageRecords;
	}

	//̫ƽ��Ŀ����ƽ̨���
	public void setjComboBox_onePageRecords(JComboBox jComboBox_onePageRecords) {
		this.jComboBox_onePageRecords = jComboBox_onePageRecords;
	}

	public JLabel getPageDescLabel() {
		if (label_pagedesc == null) { //Ψһһ����ʾ�����Label��[����2012-08-03]
			label_pagedesc = new JLabel("", SwingConstants.LEFT); //
			label_pagedesc.setForeground(new Color(69, 69, 69)); //
			label_pagedesc.setMinimumSize(new Dimension(0, 22));
		}
		return label_pagedesc;
	}

	/**
	 * ��������Item�е�ĳһ��Ϊĳֵ
	 * 
	 * @param _itemkey
	 * @param _obj
	 */
	public void setAllItemValue(String _itemkey, Object _obj) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			templetItemVOs[i].setAttributeValue(_itemkey, _obj);
		}
	}

	// �����Ƿ���Ա༭!!
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

	// �����Ƿ���Ա༭!!
	public void setItemEditable(boolean _ifedit) {
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (_ifedit) {
				templetItemVOs[i].setAttributeValue("listiseditable", "1"); //
			} else {
				templetItemVOs[i].setAttributeValue("listiseditable", "4"); //
			}
		}
	}

	// �����Ƿ���Ա༭!!
	public void setItemEditableByInit() {
		// ���ø��ؼ�!!!
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			templetItemVOs[i].setAttributeValue("listiseditable", hm_initeditable.get(templetItemVOs[i].getItemkey())); //
		} //
	}

	/**
	 * ����ѡ��ģʽ
	 * <ul>
	 * <li> <code>ListSelectionModel.SINGLE_SELECTION</code> ֻ��ѡһ��
	 * <li> <code>ListSelectionModel.SINGLE_INTERVAL_SELECTION</code> ����ѡһ��
	 * <li> <code>ListSelectionModel.MULTIPLE_INTERVAL_SELECTION</code> ����ѡ����
	 * </ul>
	 * 
	 */
	public void setSelectionMode(int _model) {
		getTable().getSelectionModel().setSelectionMode(_model);
	}

	public JTable getTable() {
		if (table == null) {
			table = new HtmlHrefTable(getTableModel(), getColumnModel()); // �������
			table.setRowHeight(li_rowHeight + LookAndFeel.getFONT_REVISE_SIZE()); //
			table.setGridColor(LookAndFeel.tableHeadLineClolr); //�����ߵ���ɫ!!
			table.setIntercellSpacing(new Dimension(1, 1)); //
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						selectedChanged(); // ��ѡ��仯
					}
				}
			}); //

			new TableAdapter(table);
			table.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1) { // ��������
						if (e.getClickCount() == 2) {
							mouseDoubleClicked(e); //
						}
					} else if (e.getButton() == MouseEvent.BUTTON3) {
						showPopMenu(e, is_admin);
					}
				}

				//���ѡ�����ɿ�ʱ���������¼��ɺ�������ѡ�������������Ctrl����
				public void mouseReleased(MouseEvent e) {
					selectedInfo();//�����б�Ԫ��ʱ���·�״̬����ʾ��Ϣ[YangQing/2013-09-25]
				}
			});

			table.getTableHeader().setFont(LookAndFeel.font_b); //����
			table.getTableHeader().setOpaque(false);
			if (templetVO.getListheaderisgroup().booleanValue()) { // �б�ı�ͷ�Ƿ�֧�ַ���
				if (isGroupHeader()) { // �����Ҫ����,����б�ͷ�ϲ�..
					groupTableHeader(table, getGroupTitles(), this.templetVO); // �����Ҫ�ϲ���ͷ,�����ϲ���ͷ����
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
						if (isHeaderCanSort) { //�еı������˫������,����������Ū����!
							sortByColumn(evt.getPoint());
						}
					} else if (evt.getButton() == MouseEvent.BUTTON3) { //������Ҽ�!
						if (showPopMenu) { //�еĵط��ı����Ҫ��������!���籨��
							showPopMenu(evt.getComponent(), evt.getX(), evt.getY()); // �����˵�
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
	 * �������ñ�ͷ,��˵���е������滻��ʵ��ֵ...
	 */
	public void reGroupHeader(Hashtable _ht) {
		try {
			if (isGroupHeader()) {
				BillListGroupableTableHeader header = (BillListGroupableTableHeader) table.getTableHeader(); //
				Hashtable ht_colconvert = header.getColValueConverter(); //
				String[] str_keys = (String[]) ht_colconvert.keySet().toArray(new String[0]); // �õ�����key����
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
		TableColumnModel cm = _table.getColumnModel(); // �õ���� ColumnModel ����
		BillListGroupableTableHeader header = new BillListGroupableTableHeader(cm, _Templet_1VO); // ������ͷ����
		for (int i = 0; i < _data.length; i++) { //
			if (!_data[i][1].equals("")) {
				String[] str_items = getTBUtil().split(_data[i][1], "."); // ��
				for (int j = str_items.length - 1; j >= 0; j--) {
					String str_groupname = str_items[j]; //
					if (j == str_items.length - 1) // ��������һλ,��������
					{
						getColumnGroup(str_groupname).add(getTableColumn(_data[i][0])); // ������
					} else {
						if (str_items.length > 1) {
							if (!getColumnGroup(str_groupname).containsGroup(getColumnGroup(str_items[j + 1]))) { // �����������һ�����������һ��,�������2���Ѱ�����������
								getColumnGroup(str_groupname).add(getColumnGroup(str_items[j + 1])); //
							}
						}
					}

					if (j == 0) { // ����ǵ�һλ!!
						if (!header.containsGroup(getColumnGroup(str_groupname))) { // �������������,��������!!
							header.addColumnGroup(getColumnGroup(str_groupname)); //
						}
					}
				}
			}
		}
		_table.setTableHeader(header); // ���ñ�ͷ����
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
	 * �����б�Ԫ��ʱ���·�״̬����ʾ��Ϣ����
	 * @author YangQing/2013-09-25
	 */
	private void selectedInfo() {
		int[] sel_row = this.getTable().getSelectedRows();//�õ���ѡ�е��к�
		int[] sel_column = this.getTable().getSelectedColumns();//�õ���ѡ�е��к�
		String[][] selectCelValue = new String[sel_row.length][sel_column.length];//���ѡ�е�Ԫ���ֵ
		for (int i = 0; i < sel_row.length; i++) {
			for (int y = 0; y < sel_column.length; y++) {
				Object o_value = this.getTable().getValueAt(sel_row[i], sel_column[y]);
				String value = o_value == null ? "" : o_value.toString();
				selectCelValue[i][y] = value;
			}
		}

		Pattern pattern = Pattern.compile("^\\+?\\d*\\.?\\d+$|^-?\\d*\\.?\\d+$");//ƥ������������С��
		String celinfo = "";//����״̬��Ҫ��ʾ���ַ���
		double sum = 0;//��ֵ�ܺ�
		int numvalue_count = 0;//��ֵ����
		boolean allPersent = true; //Ĭ��
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
					sum += dle_selvalue;//��������֣����ۼ��ϣ���͡���ƽ��ֵ
					numvalue_count++;
					if (!isper) {
						allPersent = false;
					}
				}
			}
		}
		if (numvalue_count <= 1) { //�������һ������ ����20130926
			celinfo = "�� ������" + (sel_row.length * sel_column.length) + " ��";
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
			celinfo = "�� ������" + (sel_row.length * sel_column.length) + "  ��ֵ������" + (numvalue_count) + "  ��ͣ�" + d_f.format(lastSum) + (allPersent ? "%" : "") + "  ƽ��ֵ" + (ydy ? "��" : ":") + avgvalue + (allPersent ? "%" : "") + " ��";
		}
		DeskTopPanel.setSelectInfo(celinfo);//��ʾ
	}

	/**
	 * �����˵�
	 * 
	 * @param _compent
	 * @param _x
	 * @param _y
	 */
	private void showPopMenu(Component _compent, final int _x, final int _y) {
		if (popmenu_header != null) { // ����Ѿ����������˵�.
			if (this.bo_tableislockcolumn) { //�����������
				item_column_lock.setText("����"); //
				item_column_lock.setIcon(UIUtil.getImage("office_082.gif")); //
			} else {
				item_column_lock.setText("����"); //
				item_column_lock.setIcon(UIUtil.getImage("office_166.gif")); //
			}
			li_mouse_x = _x;
			li_mouse_y = _y;
			popmenu_header.show(_compent, _x, _y); //
			return; //ֱ�ӷ���
		}
		popmenu_header = new JPopupMenu();

		int li_menuwidth = 138; //
		ArrayList al_menus = new ArrayList(); //֮���Ը��ArrayList����,����Ϊ�˵�̫����,�����������¼���Ҫһ���е�д,����ѭ������!
		//�˵��嵥1
		item_column_lock = createPopMenuItem(al_menus, "����", "office_166.gif", false, li_menuwidth); // ����,ʹ��һ��������һ��,Ϊ�������ô�С,UI,��ɫ,��ֻҪдһ��!!!
		item_column_search = createPopMenuItem(al_menus, "����в���", "office_115.gif", false, li_menuwidth); //����/��λ
		item_table_showhidecolumn = createPopMenuItem(al_menus, "����/��ʾ��", "office_123.gif", false, li_menuwidth); //
		item_table_resetOrderCons = createPopMenuItem(al_menus, "������������", "office_173.gif", false, li_menuwidth); //
		item_column_quickputvalue = createPopMenuItem(al_menus, "���ٸ�ֵ", "office_096.gif", false, li_menuwidth); // ���ٸ�ֵ
		item_column_quickstrike = createPopMenuItem(al_menus, "���ٴ�͸��ѯ", "office_163.gif", false, li_menuwidth); //���ٴ�͸��ѯ
		item_column_quicksavecurrwidth = createPopMenuItem(al_menus, "��������˳��", "office_076.gif", false, li_menuwidth); // ���������˳��
		item_column_quicksaverowheight = createPopMenuItem(al_menus, "�����и�", "office_125.gif", false, li_menuwidth); //�����и�
		item_table_setLineVisiable = createPopMenuItem(al_menus, "���������/��ʾ", "office_070.gif", false, li_menuwidth); //��ǰ����˸�ͼƬ�ر��,���ʹ������ͼƬ����֮�����и����,����ʱ������һ��!���˺þò�֪����ͼƬ̫���ԭ��!!
		item_column_weidusearch = createPopMenuItem(al_menus, "��άչʾ", "office_104.gif", false, li_menuwidth); //

		menu_excel = (JMenu) createPopMenuItem(al_menus, "����Excel/Html", "office_170.gif", true, li_menuwidth); //
		menu_table_exportprint = createPopMenuItem(al_menus, "����Excel", null, false, 80); //
		menu_table_exporthtml = createPopMenuItem(al_menus, "����Html", null, false, 80);
		menu_excel.add(menu_table_exportprint); //
		menu_excel.add(menu_table_exporthtml);
		item_column_piechart = createPopMenuItem(al_menus, "��ͼ", "office_006.gif", false, li_menuwidth); // ��ͼ
		item_column_lookhelp = createPopMenuItem(al_menus, "�鿴����", "office_037.gif", false, li_menuwidth); // ����

		menu_table_templetmodify = (JMenu) createPopMenuItem(al_menus, "����ģ��༭", "office_127.gif", true, li_menuwidth); //�Ǹ�����ͼ��,����ӡ�����!!
		item_table_templetmodify_1 = createPopMenuItem(al_menus, "1.�༭ѡ����", null, false, 100); ////
		item_table_templetmodify_2 = createPopMenuItem(al_menus, "2.�༭����ģ��", null, false, 100); ////
		menu_table_templetmodify.add(item_table_templetmodify_1); //
		menu_table_templetmodify.add(item_table_templetmodify_2); //
		menu_db = (JMenu) createPopMenuItem(al_menus, "�������ݴ���", "office_147.gif", true, li_menuwidth);
		menu_db_addflow4_database = createPopMenuItem(al_menus, "�����ݿ������ӹ�����4���ֶ�", null, false, 180);
		menu_db_addflow4_templet = createPopMenuItem(al_menus, "��ģ�������ӹ�����4���ֶ�", null, false, 180);
		menu_db_addflow13_templet = createPopMenuItem(al_menus, "��ģ�����������̼��13���ֶ�", null, false, 180); //Ϊ���ڴ�������,�Ѱ�����,�ҵ�������ȡ�����̼����Ϣ,��Ҫ������ͬ��12���ֶ�!
		menu_transportToDataSource = createPopMenuItem(al_menus, "����ģ�浽����Դ", null, false, 180);
		menu_db_autoBuildData = createPopMenuItem(al_menus, "�Զ�����Demo����", null, false, 180);
		menu_db.add(menu_db_addflow4_database);
		menu_db.add(menu_db_addflow4_templet);
		menu_db.add(menu_db_addflow13_templet); //12�����̼���ֶ�!
		menu_db.add(menu_transportToDataSource);
		menu_db.add(menu_db_autoBuildData);
		item_table_showsql = createPopMenuItem(al_menus, "�鿴��������", "office_162.gif", false, li_menuwidth); //
		item_state = createPopMenuItem(al_menus, "�鿴����״̬", "office_011.gif", false, li_menuwidth);
		//����ؼ�!
		popmenu_header.add(item_column_search); //����
		popmenu_header.add(item_column_lock); //����
		popmenu_header.add(item_table_showhidecolumn); //����/��ʾ��
		popmenu_header.add(item_table_resetOrderCons); //������������!!
		popmenu_header.add(item_column_piechart); //ĳһ���������ɱ�ͼ
		if (is_admin) {
			popmenu_header.add(item_column_quickputvalue); //���ٸ�ֵ!!
		}
		popmenu_header.add(item_column_lookhelp); //�鿴ĳһ�а���˵��!!
		popmenu_header.addSeparator(); // �ָ���!!!�÷ָ�������������ĳһ�е�,������������������!!!
		if (is_admin) {
			popmenu_header.add(item_column_quickstrike); //���ٴ�͸
			popmenu_header.add(item_column_quicksaverowheight); // �����и�....
			popmenu_header.add(item_column_quicksavecurrwidth); // �����п���˳��!!ֱ���޸������ݿ��ģ�飬��ֻ�ܹ���Ա�޸ġ����/2016-04-26��
		}
		popmenu_header.add(item_column_weidusearch); //��άչʾ,�︻�������ҵ�Ҫ�����Ĺ���!!
		popmenu_header.add(item_table_setLineVisiable); //������Ƿ���ʾ������..

		if (is_admin) { //����Ա�ż���
			popmenu_header.add(menu_excel); //
			popmenu_header.add(menu_table_templetmodify); // ����ģ��༭
			popmenu_header.add(menu_db); // �������ݿ�洦��
			popmenu_header.add(item_state);//�鿴����״̬����ǰ������ǺͼӼ�������ʾ����˼��ͳͳ�㵽������������ɫ����
		} else if (getTBUtil().getSysOptionBooleanValue("�б��Ƿ���Ե���", true)) {//��������Ҫ���������������Ӹò��������/2014-01-13��
			popmenu_header.add(menu_excel); //
		}
		popmenu_header.add(item_table_showsql); // ����ģ��༭
		if (this.bo_tableislockcolumn) { //�����������
			item_column_lock.setText("����"); //
			item_column_lock.setIcon(UIUtil.getImage("office_082.gif")); //
		} else {
			item_column_lock.setText("����"); //
			item_column_lock.setIcon(UIUtil.getImage("office_166.gif")); //
		}

		headerPopMenuAction = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == item_column_lock) {
					if (item_column_lock.getText().equals("����")) {
						lockColumn();
					} else if (item_column_lock.getText().equals("����")) {
						unlockColumn();
					}
				} else if (e.getSource() == item_column_quickputvalue) {
					quickPutValue();
				} else if (e.getSource() == item_column_quickstrike) { //���ٴ�͸��ѯ!!!
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
				} else if (e.getSource() == menu_db_addflow4_templet) { //��ģ�������ӹ�������4���ֶ�!!
					menu_db_flow_modifyTemplet(_x, _y);
				} else if (e.getSource() == menu_db_addflow13_templet) { //��ģ�����������̼�ص�12���ֶ�!
					menu_db_addflow13Field_templet(_x, _y); //����12���ؼ��ֶ�!
				} else if (e.getSource() == item_table_showsql) {
					showSQL();
				} else if (e.getSource() == item_state) {
					showItemState();
				} else if (e.getSource() == menu_transportToDataSource) {
					menu_transportToDataSource();
				} else if (e.getSource() == menu_db_autoBuildData) { //�Զ�����demo����
					menu_db_AutoBuildData();
				}
			}

		};

		//ѭ����������¼�!!!
		for (int i = 0; i < al_menus.size(); i++) {
			JMenuItem itemMenu = (JMenuItem) al_menus.get(i); //
			if (!(itemMenu instanceof JMenu)) { //�������Ŀ¼!!
				itemMenu.addActionListener(headerPopMenuAction); //
			}
		}
		li_mouse_x = _x;
		li_mouse_y = _y;
		popmenu_header.show(_compent, _x, _y); //
	}

	//�����˵�!!!��һЩ����ֻҪдһ��!
	private JMenuItem createPopMenuItem(ArrayList _list, String _text, String _iconImg, boolean _isMenu, int _width) {
		JMenuItem _menuItem = null; //
		if (_isMenu) {
			_menuItem = new JMenu(_text); //
			((JMenu) _menuItem).setUI(new WLTMenuUI());
		} else {
			_menuItem = new JMenuItem(_text); //
			_menuItem.setUI(new WLTMenuItemUI());
		}
		_menuItem.setOpaque(true); //��͸��!!
		_menuItem.setBackground(LookAndFeel.defaultShadeColor1); //
		_menuItem.setPreferredSize(new Dimension(_width, 20)); //���ô�С
		_menuItem.setToolTipText(_menuItem.getText()); //
		if (_iconImg != null) {
			_menuItem.setIcon(UIUtil.getImage(_iconImg)); // 
		}
		_list.add(_menuItem); //
		return _menuItem; //
	}

	private void showPopMenu2(Component _compent, int _x, int _y) {
		JPopupMenu popmenu_header2 = new JPopupMenu();
		JMenuItem item_column_unlock = new JMenuItem("����", UIUtil.getImage("office_082.gif")); // ����
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

	//����ĳһ���ǰ����ɫ
	public void setItemForeGroundColor(String _foreGround, int _row, String _itemKey) {
		BillItemVO billItemVO = (BillItemVO) this.getTableModel().getValueAt(_row, _itemKey); //
		if (billItemVO != null) {
			billItemVO.setForeGroundColor(_foreGround); //
		}
	}

	/**
	 * �����б�����ɫ�����/2014-11-13��
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
		if (rowHeaderTable != null) { //��ͷҲ����
			rowHeaderTable.putClientProperty("$rowbackground_" + _row, _backGround); //
		}
	}

	public void clearItemBackGroundColor() {
		for (int i = 0; i < 200; i++) {
			getTable().putClientProperty("$rowbackground_" + i, null); //
			if (rowHeaderTable != null) { //��ͷҲ����
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

	// ���ĳһ������!!
	public void reset(int _row) {
		String[] str_keys = this.getTempletVO().getItemKeys();
		for (int i = 0; i < str_keys.length; i++) {
			setValueAt(null, _row, str_keys[i]);
		}
	}

	/**
	 * ��ĩβ����һ������
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
	 * ��ĩβ����һ������
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
		int li_currrow = getTable().getSelectedRow(); // ȡ�õ�ǰ��
		int li_newrow = -1;
		if (li_currrow < 0) { // ���û��ѡ����,����ĩβ��
			li_newrow = insertEmptyRow(0); // �ڵ�һ��ĩβ����
		} else {
			if (li_currrow == getTable().getRowCount() - 1) {
				li_newrow = addEmptyRow(_isAutoSelect); // ��ĩβ����
			} else {
				li_newrow = insertEmptyRow(li_currrow + 1); // ����
			}
		}

		if (li_newrow >= 0) {
			setValueAt(new RowNumberItemVO("INSERT", li_newrow), li_newrow, this.str_rownumberMark); //
			if (templetVO.getPksequencename() != null) {
				try {
					String str_newid = UIUtil.getSequenceNextValByDS(null, templetVO.getPksequencename());
					String str_pkfieldname = templetVO.getPkname();
					if (str_pkfieldname != null) {
						setValueAt(new StringItemVO(str_newid), li_newrow, str_pkfieldname); // ��������ֵ
					}
				} catch (Exception e) {
					MessageBox.showException(this, e);
				}
			}
			if (_isExecDefaultFormula) { // ����Ĭ��ֵ..
				VectorMap defaultvalue = getDefaultValue(li_newrow);
				Object[][] value = defaultvalue.getAllData();
				for (int i = 0; i < value.length; i++) {
					this.setValueAt(value[i][1], li_newrow, (String) value[i][0]);//��ǰ�õķ���setRealValueAt���������ò��ն��󣬹��޸�֮�����/2014-10-10��
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
		int li_currrow = getTable().getSelectedRow(); // ȡ�õ�ǰ��
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
		int li_currrow = getTable().getSelectedRow(); // ȡ�õ�ǰ��
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
	 * �ж��б��Ƿ�༭��������༭��������Ҫ��ʾ�û����б��桾���/2016-04-13��
	 * @return
	 */
	public boolean isEdited() {
		this.stopEditing(); // �����༭
		if (v_deleted_row.size() > 0) {//ɾ���ļ�¼
			return true;
		}
		//�������޸ĵļ�¼
		for (int i = 0; i < getTableModel().getRowCount(); i++) {
			String str_type = ((RowNumberItemVO) getTableModel().getValueAt(i, str_rownumberMark)).getState(); //
			if (str_type.equals("INSERT")) { //���������״̬!
				return true;
			} else if (str_type.equals("UPDATE")) { //������޸�״̬!
				return true;
			}
		}
		return false;
	}

	public String[] getDeleteRowValues() {
		return (String[]) v_deleted_row.toArray(new String[0]);
	}

	//�����������͵�SQL[ɾ,��,��]
	public String[] getOperatorSQLs() {
		Vector v_all = new Vector();
		v_all.addAll(getDeleteSQLVector());
		for (int i = 0; i < getTableModel().getRowCount(); i++) {
			String str_type = ((RowNumberItemVO) getTableModel().getValueAt(i, str_rownumberMark)).getState(); //
			if (str_type.equals("INSERT")) { //���������״̬!
				v_all.add(getInsertSQL(i));
			} else if (str_type.equals("UPDATE")) { //������޸�״̬!
				v_all.add(getUpdateSQL(i));
			} else if (str_type.equals("INIT")) { //����ǳ�ʼ��״̬!
			} else { //
			}
		}
		return (String[]) v_all.toArray(new String[0]); //
	}

	public void executeDeleteOperationOnly() {
		this.stopEditing(); // �����༭
		String[] delsqls = getDeleteSQLs();
		v_deleted_row.clear();
		try {
			UIUtil.executeBatchByDS(str_templetcode, delsqls); // ������!!
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
		//templetVO.getListrowheight()��ȡ�Ŀ�����22,22 ��׷��,�ж� �����/2013-06-19��
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
	 * @param _isGetLinkChildData  �Ƿ�ȡͬʱ�����"�����ӱ�"������! �����ڹ�������ʱ��Ҫ�����"�����ӱ�"����!��xch/2012-11-13��
	 * @return
	 */
	public BillVO getBillVO(int _row, boolean _isGetLinkChildData) {
		stopEditing(); //�Ƚ����༭ģʽ,����ȡ����ֵ
		BillVO vo = new BillVO();
		vo.setTempletCode(templetVO.getTempletcode()); //
		vo.setTempletName(templetVO.getTempletname()); //
		vo.setQueryTableName(templetVO.getTablename());
		vo.setSaveTableName(templetVO.getSavedtablename()); //
		vo.setPkName(templetVO.getPkname()); // �����ֶ���
		vo.setSequenceName(templetVO.getPksequencename()); // ������
		vo.setToStringFieldName(templetVO.getTostringkey()); //����ToString���ֶ���!!!

		int li_length = templetItemVOs.length;

		// ����ItemKey
		String[] all_Keys = new String[li_length + 1]; //
		all_Keys[0] = this.str_rownumberMark; // �к�
		for (int i = 1; i < all_Keys.length; i++) {
			all_Keys[i] = this.templetVO.getItemKeys()[i - 1];
		}

		// ���е�����
		String[] all_Names = new String[li_length + 1]; //
		all_Names[0] = str_rownumberMarkName; // �к�
		for (int i = 1; i < all_Names.length; i++) {
			all_Names[i] = this.templetVO.getItemNames()[i - 1];
		}

		String[] all_Types = new String[li_length + 1]; //
		all_Types[0] = str_rownumberMarkName; // �к�
		for (int i = 1; i < all_Types.length; i++) {
			all_Types[i] = this.templetVO.getItemTypes()[i - 1];
		}

		String[] all_ColumnTypes = new String[li_length + 1]; //
		all_ColumnTypes[0] = "NUMBER"; // �к�
		for (int i = 1; i < all_ColumnTypes.length; i++) {
			all_ColumnTypes[i] = this.templetItemVOs[i - 1].getSavedcolumndatatype(); //
		}

		boolean[] bo_isNeedSaves = new boolean[li_length + 1];
		bo_isNeedSaves[0] = false; // �к�
		for (int i = 1; i < bo_isNeedSaves.length; i++) {
			bo_isNeedSaves[i] = this.templetItemVOs[i - 1].isNeedSave();
		}

		vo.setKeys(all_Keys); //
		vo.setNames(all_Names); //
		vo.setItemType(all_Types); // �ؼ�����!!
		vo.setColumnType(all_ColumnTypes); // ���ݿ�����!!
		vo.setNeedSaves(bo_isNeedSaves); // �Ƿ���Ҫ����!!
		vo.setDatas(this.getValueAtRow(_row)); // ��������������!!

		//�����Ҫͬʱ�����"�����ӱ�"����,����������ӱ�Ķ�����ȥ��ѯһ�������ӱ������,���轫�������������BillVO��..
		if (_isGetLinkChildData) {
			try {
				for (int i = 0; i < all_Types.length; i++) {
					if (all_Types[i].equals(WLTConstants.COMP_LINKCHILD)) { //����������ӱ�!
						String str_itemkey = all_Keys[i]; //
						//����itemkey�õ��ӱ��е�����!
						CommUCDefineVO defvo = templetVO.getItemVo(str_itemkey).getUCDfVO(); //
						String str_temptCode = defvo.getConfValue("ģ�����"); //
						String str_foreignkey = defvo.getConfValue("��������ֶ���"); //
						String str_thisPkValue = vo.getPkValue(); //����ֵ

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
						System.out.println("�ӱ���" + childVOs.length + "��������!"); //
						vo.setLinkChildBillVOs(str_itemkey, childVOs); //����!
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}
		return vo;
	}

	//ȡ��ѡ���е�����!
	public String[][] getSelectedRowValue() {
		int li_selectedRow = this.getTable().getSelectedRow(); //
		if (li_selectedRow < 0) {
			return null;
		}
		Pub_Templet_1_ItemVO[] itemVOs = getTempletItemVOs(); //
		ArrayList al_values = new ArrayList(); //
		for (int i = 0; i < itemVOs.length; i++) {
			String str_realValue = getRealValueAtModel(li_selectedRow, itemVOs[i].getItemkey()); //
			if (str_realValue != null && !str_realValue.trim().equals("")) { //�����Ϊ��!
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
	 * ȡ��ѡ���е�BillVO
	 * 
	 * @return
	 */
	public BillVO getSelectedBillVO() {
		//�˴���ΪĬ�ϲ����ǹ�ѡ���,ԭ������:
		//1.�Թ�ѡ��ʽѡ������, һ�������ڽ��ж�������ͬʱ����ʱ������, ��"���","�༭","ɾ��"����Ե�һ���ݵĲ�����Ӧ�ò���ֱ��ѡ��ķ�ʽ.
		// ����, �����ù�ѡ��ʽ, ��ѡ�˵�һ��(RowNum=1), �����������༭��ɾ����2��(RowNum=2)ʱ, ����Ӧ��ȡ����1�еĹ�ѡ״̬, Ȼ���ٹ�ѡ��2��, �����Ĳ����Ͳ�������.
		//2.���Ĭ��Ϊ���ǹ�ѡ�����������˫����ͬ��¼����󿴵��Ķ���ͬһ�����ݣ�
		//3.һ�������û�й�ѡ����б��й�ѡ�������Ͼ�������
		//ͬ���޸ĵķ�����getSelectedBillVOs()��getSelectedRow()��getSelectedRows()�����/2012-03-01��
		return getSelectedBillVO(false, false); //
	}

	public BillVO getSelectedBillVO(boolean _isThinkChecked) {
		return getSelectedBillVO(_isThinkChecked, false); //
	}

	public BillVO getSelectedBillVO(boolean _isThinkChecked, boolean _isGetLinkChildData) {
		if (_isThinkChecked && this.isRowNumberChecked) { //������ǹ�ѡ���,����ȷʵ�ǹ�ѡ���
			BillVO[] vos = getCheckedBillVOs(_isGetLinkChildData);
			if (vos.length == 0) {//������Ҫ�ж�һ�£��Ƿ�û�й�ѡ����������Խ���쳣�����/2012-03-01��
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
	 * ȡ������ѡ�е��е�����VO
	 * 
	 * @return
	 */
	public BillVO[] getSelectedBillVOs() {
		//�˴���ΪĬ�ϲ����ǹ�ѡ���,ԭ������:
		//1.�Թ�ѡ��ʽѡ������, һ�������ڽ��ж�������ͬʱ����ʱ������, ��"���","�༭","ɾ��"����Ե�һ���ݵĲ�����Ӧ�ò���ֱ��ѡ��ķ�ʽ.
		// ����, �����ù�ѡ��ʽ, ��ѡ�˵�һ��(RowNum=1), �����������༭��ɾ����2��(RowNum=2)ʱ, ����Ӧ��ȡ����1�еĹ�ѡ״̬, Ȼ���ٹ�ѡ��2��, �����Ĳ����Ͳ�������.
		//2.���Ĭ��Ϊ���ǹ�ѡ�����������˫����ͬ��¼����󿴵��Ķ���ͬһ�����ݣ�
		//3.һ�������û�й�ѡ����б��й�ѡ�������Ͼ�������
		//ͬ���޸ĵķ�����getSelectedBillVO()��getSelectedRow()��getSelectedRows()�����/2012-03-01��
		return getSelectedBillVOs(false, false); //
	}

	//�������˹�ѡ���Ч��,Ϊ�˼��ݼ��ֿ���ѡ��ԭ��������,���˸�����!!
	public BillVO[] getSelectedBillVOs(boolean _isThinkChecked) {
		return getSelectedBillVOs(_isThinkChecked, false); //
	}

	//�������˹�ѡ���Ч��,Ϊ�˼��ݼ��ֿ���ѡ��ԭ��������,���˸�����!!
	public BillVO[] getSelectedBillVOs(boolean _isThinkChecked, boolean _isGetLinkChildData) {
		if (_isThinkChecked && this.isRowNumberChecked) { //������ǹ�ѡ���,����ȷʵ�ǹ�ѡ���
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

	/**ȡ�����й�ѡ�е�BillVO
	 * @return
	 */
	public BillVO[] getCheckedBillVOs() {
		return getCheckedBillVOs(false); //
	}

	/**ȡ�����й�ѡ�е�BillVO
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

	// �õ�ɾ����BillVO[]
	public BillVO[] getDeletedBillVOs() {
		return (BillVO[]) v_deleted_row.toArray(new BillVO[0]);
	}

	// �õ�����������BillVO
	public BillVO[] getInsertedBillVOs() {
		ArrayList insertvos = new ArrayList();
		for (int i = 0; i < this.getTable().getRowCount(); i++) {
			if (this.getRowNumberEditState(i).equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_INSERT))
				insertvos.add(this.getBillVO(i));
		}
		return (BillVO[]) insertvos.toArray(new BillVO[0]);
	}

	// �õ������޸Ĺ���BillVO
	public BillVO[] getUpdatedBillVOs() {
		ArrayList updatevos = new ArrayList();
		for (int i = 0; i < this.getTable().getRowCount(); i++) {
			if (this.getRowNumberEditState(i).equalsIgnoreCase(WLTConstants.BILLDATAEDITSTATE_UPDATE))
				updatevos.add(this.getBillVO(i));
		}
		return (BillVO[]) updatevos.toArray(new BillVO[0]);
	}

	/**
	 * ȡ������BillVO
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
	 * ȡ������BillVO
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
	 * ����һ�У���������״̬��Ϊinit
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
	 * ���������е�״̬
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
	 * ����ĳһ�е�״̬
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

		columnModel = new DefaultTableColumnModel(); // ������ģʽ
		//Ԭ���� 20130531 �����Զ�����  ����
		//1.����ȡ���Զ�����ʾ����
		String cols = "";
		boolean isrelodcol = false;
		if (CustomizeColumnMap.getCustomizeBean(templetVO.getTempletcode()) == null) {//�Ȼ�ñ��ػ���
			isrelodcol = false;
		} else {
			isrelodcol = true;
			cols = CustomizeColumnMap.getCustomizeBean(templetVO.getTempletcode()).get_text();//�����ʽΪLAW_FILE_CODE;S_CHECK_DEPT;S_CHECK_PEOPLE;MATTER_TYPE;state2;
		}
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getListisshowable().booleanValue()) { // ����������б������ʾ!!!,��������
				if (isrelodcol) {//��������м�¼����Ҫ���ر��ر���Ļ���
					if (cols.indexOf(templetItemVOs[i].getItemkey()) > -1) {//�������
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
	 * ȡ�ñ����ĳһ�е�����
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
		ListCellRender_RowNumber render = new ListCellRender_RowNumber(); // �����л�����..
		render.setBillList(this); //��!!
		ListCellEditor_RowNumber editor = new ListCellEditor_RowNumber();
		editor.setBillList(this); //��!!
		this.editor = editor;//Ԭ���� 20131029 ���
		rowNumberColumn = new TableColumn(0, li_rowno_width, render, editor); // ������,��Ӧ��һ������,�кſ�
		if (ClientEnvironment.getInstance().isEngligh()) {
			rowNumberColumn.setHeaderValue("No."); //
		} else {
			rowNumberColumn.setHeaderValue(str_rownumberMarkName); //
		}
		rowNumberColumn.setIdentifier(this.str_rownumberMark);
		return rowNumberColumn;
	}

	/**
	 * �������е���
	 * 
	 * @return
	 */
	private TableColumn[] getTableColumns() {
		if (this.allTableColumns != null) {
			return allTableColumns;
		}
		allTableColumns = new TableColumn[templetItemVOs.length]; // �е���������ģ���е�����!
		for (int i = 0; i < allTableColumns.length; i++) {
			String str_key = templetItemVOs[i].getItemkey(); // key
			String str_type = templetItemVOs[i].getItemtype(); // ����
			int li_width = templetItemVOs[i].getListwidth().intValue(); // ���
			if (!TBUtil.isEmpty(templetItemVOs[i].getItemname())) {
				li_width += LookAndFeel.getFONT_REVISE_SIZE() * (templetItemVOs[i].getItemname().length() + (templetItemVOs[i].getIsmustinput() ? 4 : 0));
			}
			if (str_key.equals("TEMPLETCODE")) {
				templete_codecolumn_index = i;
			}

			TableCellEditor cellEditor = null;
			TableCellRenderer cellRender = null;
			if (str_type.equals(WLTConstants.COMP_LABEL)) {
				if (templetVO.getIslistautorowheight()) { //������Զ��Ÿ�,��ǿ��ʹ��TextArea��ʾ!��Ϊ�����Ÿߺ�Ż���Ч��,������Ȼ�Ÿ���,������һ��,������˸�[...],û�ﵽЧ��!��xch/2012-05-04��
					cellRender = new ListCellRender_JTextArea(templetItemVOs[i]); // �����ı���ʾ��
				} else {
					cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
				}
				cellEditor = new ListCellEditor_Label(templetItemVOs[i]); // ��ǩ
			} else if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || // �ı���
					str_type.equals(WLTConstants.COMP_NUMBERFIELD) || // ���ֿ�
					str_type.equals(WLTConstants.COMP_PASSWORDFIELD) || // �����
					str_type.equals(WLTConstants.COMP_REGULAR)//[zzl]������ʽ
			) {
				if (templetVO.getIslistautorowheight()) {
					cellRender = new ListCellRender_JTextArea(templetItemVOs[i]); // �����ı���ʾ��
				} else {
					cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
				}
				JTextField textField = null;
				if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || str_type.equals(WLTConstants.COMP_REGULAR)) {//[zzl]������ʽ
					textField = new JTextField();
				} else if (str_type.equals(WLTConstants.COMP_NUMBERFIELD)) // ���ֿ�
				{
					textField = new JFormattedTextField();
					CommUCDefineVO ucvo = templetItemVOs[i].getUCDfVO();
					textField.setDocument(new NumberFormatdocument(ucvo)); //�������ֿ�ֻ����������,������ĸ���ü���!�����/2013-06-05��
				} else if (str_type.equals(WLTConstants.COMP_PASSWORDFIELD)) { // �����
					textField = new JPasswordField();
				}
				textField.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
				cellEditor = new ListCellEditor_TextField(textField, templetItemVOs[i]);
			} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { // ������
				if (templetVO.getIslistautorowheight()) { //������Զ��Ÿ�,��xch/2012-05-04��
					cellRender = new ListCellRender_JTextArea(templetItemVOs[i]); // �����ı���ʾ��
				} else {
					cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
				}
				JComboBox comBox = new JComboBox(); //
				if (templetItemVOs[i].getComBoxItemVos() != null) {
					comBox.addItem(new ComBoxItemVO("", null, "")); //
					for (int kk = 0; kk < templetItemVOs[i].getComBoxItemVos().length; kk++) {
						if (templetItemVOs[i].getComBoxItemVos()[kk] != null) {
							comBox.addItem(templetItemVOs[i].getComBoxItemVos()[kk]); //�ȳ�ʼ���ؼ�!!
						}
					}
				}
				cellEditor = new ListCellEditor_ComboBox(comBox, templetItemVOs[i]);
			} else if (str_type.equals(WLTConstants.COMP_TEXTAREA)) { // �����ı���
				cellRender = new ListCellRender_JTextArea(templetItemVOs[i]); // �����ı���ʾ��
				cellEditor = new ListCellEditor_TextArea(templetItemVOs[i]); // �����ı���༭��
			} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || // ���Ͳ���1
					str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || // ���Ͳ���1
					str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || // ��ѡ����1
					str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || // �Զ������
					str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //
					str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //
					str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || // 
					str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || // ע�����
					str_type.equals(WLTConstants.COMP_DATE) || // ����
					str_type.equals(WLTConstants.COMP_DATETIME) || // ʱ��
					str_type.equals(WLTConstants.COMP_COLOR) || // ��ɫѡ���
					str_type.equals(WLTConstants.COMP_BIGAREA) || // ���ı���
					str_type.equals(WLTConstants.COMP_PICTURE) || // ͼ��ѡ���
					str_type.equals(WLTConstants.COMP_LINKCHILD) || // ͼ��ѡ���
					str_type.equals(WLTConstants.COMP_IMPORTCHILD) || // �����ӱ�
					str_type.equals(WLTConstants.COMP_OFFICE) // Office�ؼ�
			) { // ����Ǹ��ֲ���,���ڰ���7�ֲ���
				if (templetVO.getIslistautorowheight()) { //������Զ��Ÿ�,��xch/2012-05-04��!!
					cellRender = new ListCellRender_JTextArea(templetItemVOs[i]); //�����ı���ʾ��!!
				} else {
					cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
				}
				cellEditor = new ListCellEditor_Ref(templetItemVOs[i]);
			} else if (str_type.equals(WLTConstants.COMP_CHECKBOX)) { // ��ѡ��
				cellRender = new ListCellRender_CheckBox();
				cellEditor = new ListCellEditor_CheckBox(new JCheckBox(), templetItemVOs[i]);
			} else if (str_type.equals(WLTConstants.COMP_BUTTON)) { // ����ǰ�ť
				cellRender = new ListCellRender_Button(templetItemVOs[i]); // ��ť��ʾ��
				cellEditor = new ListCellEditor_Button(templetItemVOs[i]); // �����ı���༭��
			} else if (str_type.equals(WLTConstants.COMP_FILECHOOSE)) { // ������ļ�ѡ���
				cellRender = new ListCellRender_FileChoose(templetItemVOs[i]); // ��ť��ʾ��
				cellEditor = new ListCellEditor_FileChoose(templetItemVOs[i]); // �ļ�ѡ���
			} else if (str_type.equals(WLTConstants.COMP_STYLEAREA)) { //���ı���
				cellRender = new ListCellRender_JTextArea(templetItemVOs[i], true); //ʹ�ö����ı�����ʾ!!����ʾ��β�������
				cellEditor = new ListCellEditor_StylePad(templetItemVOs[i], this); //
			} else if (str_type.equals(WLTConstants.COMP_SELFDESC)) {
				if (templetItemVOs[i].getUCDfVO() != null) {
					String str_render_cls = templetItemVOs[i].getUCDfVO().getConfValue("�б���Ⱦ��"); //
					if (str_render_cls != null && !str_render_cls.trim().equals("")) { //�����Ϊ��,���䴴��!!
						try {
							cellRender = (TableCellRenderer) Class.forName(str_render_cls).getConstructor(new Class[] { Pub_Templet_1_ItemVO.class }).newInstance(new Object[] { templetItemVOs[i] });
						} catch (Exception e) {
							e.printStackTrace();
							cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //����ʧ�ܵĻ�,��ʹ��Ĭ��,��֤ҳ���ܳ���!
						}
					} else {
						cellRender = new ListCellRender_JLabel(templetItemVOs[i]); //
					}
					String str_editor_cls = templetItemVOs[i].getUCDfVO().getConfValue("�б�༭��"); //
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

			allTableColumns[i] = new TableColumn(i + 1, li_width, cellRender, cellEditor); // ������
			allTableColumns[i].setIdentifier(templetItemVOs[i].getItemkey()); // Ψһ�Ա�ʶ
			if (ClientEnvironment.getInstance().isEngligh()) {
				allTableColumns[i].setHeaderValue(templetItemVOs[i].getItemname_e()); // �еı���
			} else {
				allTableColumns[i].setHeaderValue(templetItemVOs[i].getItemname()); // �еı���
			}

		}
		return allTableColumns;
	}

	/**
	 * ��������Model,����ModelҲ����ģ��һһ��Ӧ��!!!!
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

		tableModel = new BillListModel(data, new_columns, this.templetVO); // ��������Model
		tableModel.setBillListPanel(this); //
		tableModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				int lastRow = e.getLastRow(); //
				int mColIndex = e.getColumn(); //
				switch (e.getType()) {
				case TableModelEvent.INSERT:
					refreshCombineData(); //INSERT DELETE UPDATE tableModel���ݱ仯 �ϲ�ˢ�� �����/2013-07-23�� 
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
	 * ������ݷ����仯
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
		if (_col == 0)// ������޸���״̬����������.������findModelItemKey(_col)�л����.
			return;
		Object obj = getValueAt(_row, _col); // �仯������!!���û�仯����
		if (obj != null && obj instanceof BillItemVO) {
			BillItemVO itemVO = (BillItemVO) obj;
			if (!itemVO.isValueChanged()) {
				return;
			}
		}

		bo_ifProgramIsEditing = true; // ���ó������ڱ༭
		setRowNumberState(_row); // �����к�״̬Ϊ�����޸�״̬
		execEditFormula(_row, _col);// ִ�м��ع�ʽ
		String str_itemkey = findModelItemKey(_col); // �仯��Key
		for (int i = 0; i < v_listeners.size(); i++) {
			BillListEditListener listener = (BillListEditListener) v_listeners.get(i);
			listener.onBillListValueChanged(new BillListEditEvent(str_itemkey, obj, _row, this)); //
		}
		bo_ifProgramIsEditing = false; // �������ڱ༭����!!
	}

	/**
	 * ע���¼�
	 * 
	 * @param _listener
	 */
	public void addBillListEditListener(BillListEditListener _listener) {
		v_listeners.add(_listener); //
	}

	/**
	 * ע���¼�
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
	 * ע���¼�
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
	 * ִ�б༭��ʽ..
	 * 
	 * @param _row
	 */
	public void execEditFormula(int _row, int _col) {
		if (_col != 0) {
			String str_editFormuladesc = this.getTempletItemVOs()[_col - 1].getEditformula();
			if (str_editFormuladesc != null && !str_editFormuladesc.trim().equals("")) {
				String[] str_editformulas = getTBUtil().split1(str_editFormuladesc, ";");
				for (int i = 0; i < str_editformulas.length; i++) {// ����ִ�����й�ʽ
					dealEditFormula(str_editformulas[i], _row); //
				}
			} // ������ʽ����!!!
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
				if (templetItemVOs[i].getItemtype().equals(WLTConstants.COMP_BUTTON) || templetItemVOs[i].getItemtype().equals(WLTConstants.COMP_FILECHOOSE)) { // ����ǰ�ť��ǿ�з���
					return 1;
				} else {
					if (templetItemVOs[i].getListishtmlhref()) {
						return 2; // �Ƿ��ǳ���
					}
				}
			}
		}
		return -1; // Ĭ�ϲ��ǳ���
	}

	private VectorMap getDefaultValue(int _newrow) {// Ŀǰֱ�ӵ��ַ�������{Date(10)},{Date(19)}
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
				System.out.println("ִ��Ĭ��ֵ��ʽ:[" + formula + "]");
				JepFormulaParseAtUI jepFormulaParseAtUI = new JepFormulaParseAtUI(this); //
				Object obj = jepFormulaParseAtUI.execFormula(formula);
				if (obj == null) {
					result.put(tempitem.getItemkey(), ""); //
				} else {
					result.put(tempitem.getItemkey(), obj); //��ǰ�Ĵ���objת��String�ˣ�����ǲ��գ�id��nameֵ��Ϊname���������ˣ����޸�֮�����/2014-10-10��
				}
			}
		}
		return result;
	}

	/**
	 * ����ִ��ĳһ���༭��ʽ..ʹ��JEPȥִ��!!
	 * 
	 * @param _formula
	 */
	private void dealEditFormula(String _formula, int _row) {
		try {
			JepFormulaParse jepParse = new JepFormulaParseAtUI(this);
			jepParse.execFormula(_formula); // ʹ��JEPִ�й�ʽ!!!!
			System.out.println("�ɹ�ִ�б༭��ʽ��:[" + _formula + "]"); //
		} catch (Exception ex) {
			System.out.println("ʧ��ִ�б༭��ʽ��:[" + _formula + "]");
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

	//���õڼ���ѡ��!!
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
	 * �ö�
	 */
	public void moveToTop() {
		if (table.getRowCount() > 0) {
			Rectangle rect = table.getCellRect(0, 0, true);
			rect.y = (int) (rect.getY() - li_rowHeight);
			table.scrollRectToVisible(rect);
		}
	}

	/**
	 * ���ѡ��
	 */
	public void clearSelection() {
		table.clearSelection(); //
	}

	public void selectAll() {
		table.selectAll();
	}

	/**
	 * �ҵ�ĳһ��,����itemkey��value,���� code=003
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
	 * �õ�����Դ����
	 * 
	 * @return
	 */
	public String getDataSourceName() {
		return getTableModel().getDataSourceName(); //
	}

	/**
	 * �õ�����Ȩ�޹�������
	 * 
	 * @return
	 */
	public String getDataconstraint() {
		if (templetVO.getDataconstraint() == null || templetVO.getDataconstraint().trim().equals("null") || templetVO.getDataconstraint().trim().equals("")) {
			return null; // Ĭ������Դ
		} else {
			return "" + new JepFormulaParseAtUI(null).execFormula(templetVO.getDataconstraint()); //
		}
	}

	public String getAutoLoadConstraint() {
		if (templetVO.getAutoloadconstraint() == null || templetVO.getAutoloadconstraint().trim().equals("null") || templetVO.getAutoloadconstraint().trim().equals("")) {
			return null; // Ĭ������Դ
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

		String str_dataPolicySQL = getDataPolicyCondition(); //����Ȩ�޲���!!
		if (str_dataPolicySQL != null) {
			sb_sql.append(" and (" + str_dataPolicySQL + ")"); ////
		}

		String str_orderCon = templetVO.getOrdercondition(); //��������!!
		if (str_orderCon != null && !str_orderCon.trim().equals("")) { //�������������! �������������! ��ǰһ���Զ�����ʱû����������!
			sb_sql.append(" order by " + str_orderCon); //
		}
		return sb_sql.toString(); //
	}

	/**
	 * �û��Զ����������
	 * 
	 * @return
	 */
	public String getDataFilterCustCondition() {
		return str_dataFilterCustCondition;
	}

	// ��������ȡ��!!!
	public void QueryDataByCondition(final String _wherecondition) {
		queryDataByDS(getDataSourceName(), getSQL(_wherecondition, this.templetVO.getOrdercondition()));
	}

	// ��������ȡ��!!
	public void queryDataByCondition(final String _wherecondition, String _ordercondition) {
		queryDataByDS(getDataSourceName(), getSQL(_wherecondition, _ordercondition)); //
	}

	// ��������ȡ��!!!
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
	 * ��������
	 * 
	 * @param _filtercondition
	 */
	public void setDataFilterCustCondition(String _filtercondition) {
		str_dataFilterCustCondition = _filtercondition; //
	}

	public void setDataFilterCustOrCondition(String _filtercondition) {
		str_dataFilterCustOrCondition = _filtercondition; //
	}

	// ������������
	public void setOrderCustCondition(String _orderCondition) {
		str_orderCustCondition = _orderCondition; //
	}

	public String getSQL(String _condition) {
		return getSQL(_condition, null); //
	}

	public String getSQL(String _condition, String _ordercondition) {
		String str_return = "select * from " + templetVO.getTablename() + " where 1=1 "; //

		if (str_dataFilterCustCondition != null) { //���API�����˹�������,��ֱ������!!!!
			str_return = str_return + " and (" + str_dataFilterCustCondition + ") ";
		} else if (str_dataFilterCustOrCondition != null) {
			str_return = str_return + " or (" + str_dataFilterCustOrCondition + ") ";
		} else {
			String str_constraintFilterCondition = getDataconstraint();
			if (str_constraintFilterCondition != null) {
				str_return = str_return + " and (" + str_constraintFilterCondition + ") ";
			}
		}

		// �ٰѴ���Ĳ�������
		if (_condition != null) {
			str_return = str_return + " and (" + _condition + ") ";
		}

		String str_dataPolicySQL = getDataPolicyCondition(); //��Դ����!!!

		if (str_dataPolicySQL != null) {
			String str_andor = this.templetVO.getDataSqlAndOrCondition(); //��and��or���ֹ�ϵ! Ĭ����and/sunfujun/20121119
			if (str_andor.equalsIgnoreCase("or")) { //�����or�Ĺ�ϵ,���ҵ�ԭ����whereλ��
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
	 * ����Ȩ�޲������ɵ�SQL!!���,��ǿ��ĵط�!!!
	 * @return
	 */
	public String getDataPolicyCondition() {
		if (this.templetVO.getDatapolicy() != null && !this.templetVO.getDatapolicy().trim().equals("")) { //�����������Ȩ�޶���!!���������Ȩ�޹���!!
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
	 * ����������SQLȡ��!!
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
	 * ������ѯ���ݵķ���!!!!!
	 * @param _datasourcename
	 * @param _sql
	 * @param _isVewPartData
	 */
	public void queryDataByDS(final String _datasourcename, final String _sql, boolean _isRegHVOinRowNumberVO) {
		if (templetVO.getIsshowlistpagebar()) { //�����ʾ��ҳ��!!!
			li_currpage = 1; //���õ�ǰҳ
			int[] li_rowsArea = new int[] { 1, li_onepagerecords }; //1-20
			queryDataByDS(_datasourcename, _sql, li_rowsArea, _isRegHVOinRowNumberVO); //���һҳ
		} else {
			queryDataByDS(_datasourcename, _sql, null, _isRegHVOinRowNumberVO); //
		}
	}

	/**
	 * ��ѯһ����Χ�ڵ�����!!
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
			Pub_Templet_1ParVO parTempletVO = templetVO.getParPub_Templet_1VO(); //�ؽ�һ������VO,���ڷ������������Ϣ�ǲ���Ҫ��!Ϊ�˼����������紫��,������С������С! ����ǳ���Ҫ!!
			str_calltrackmsg = null; //
			all_realValueData = getMetaDataService().getBillListDataByDS(_datasourcename, _sql, parTempletVO, true, _rowArea, _isRegHVOinRowNumberVO); //�Ƿ�ֻ��ʾ��������,�Ƿ�ִ�м��ع�ʽ
			str_calltrackmsg = ClientEnvironment.getCurrCallSessionTrackMsgAndClear(); ////ȡ�õ�ǰ�Ự����ʱ��������ִ�е���־!! ���������ȡ��ֵ,�����ڷ������˵���initContext.addCurrSessionForClientTrackMsg("��־����....");  //
			putValue(all_realValueData); //��������! ���еȴ���ʱ����������ֺܴ�,��Ļ����!
			if (all_realValueData != null && all_realValueData.length > 0) {
				RowNumberItemVO rowNumVO = (RowNumberItemVO) all_realValueData[0][0]; //
				li_TotalRecordCount = rowNumVO.getTotalRecordCount(); //
			} else {
				li_TotalRecordCount = 0; //
			}
			resetPageInfo(); //����ҳ��˵��,���ܹ�N��,Nҳ,��ǰ�ڼ�ҳ
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex);
		}

		//// ���������и�
		if (templetVO.getIslistautorowheight()) { // ������Զ������и�!!
			autoSetRowHeight(); // �Զ������и�
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

		resetHeight(); // ���������и�
		autoSetTitleCheckedOrNot();//�Զ����ñ�ͷ��ѡ���Ƿ�ѡ�С����/2014-05-06��

		for (int i = 0; i < v_afterqueryListeners.size(); i++) {
			BillListAfterQueryListener listener = (BillListAfterQueryListener) v_afterqueryListeners.get(i); //
			listener.onBillListAfterQuery(new BillListAfterQueryEvent(this)); //
		}
		if (null != listQueryCallback) {
			listQueryCallback.queryCallback();
		}

		//scrollPanel_main.invalidate(); //��һ������һ��һ��Ҫ�����򣬿�����ʱ��Ļ�Ứ!
		//scrollPanel_main.repaint(); //
		centerPanel.revalidate(); //
		centerPanel.repaint(); //
	}

	/**
	 * ֱ�Ӹ���һ��HashVO[]����ѯ����!!!
	 * ��������������·�ҳ����û����˼��!
	 * @param _hashVOs
	 */
	public void queryDataByHashVOs(HashVO[] _hashVOs) {
		try {
			this.stopEditing(); //ֹͣ�༭
			clearTable(); //�������
			Pub_Templet_1ParVO parTempletVO = templetVO.getParPub_Templet_1VO(); //�ؽ�һ������VO,���ڷ������������Ϣ�ǲ���Ҫ��!Ϊ�˼����������紫��,������С������С! ����ǳ���Ҫ!!
			all_realValueData = getMetaDataService().getBillListDataByHashVOs(parTempletVO, _hashVOs); //
			putValue(all_realValueData); //��������! ���еȴ���ʱ����������ֺܴ�,��Ļ����!
			centerPanel.revalidate(); //
			centerPanel.repaint(); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex);
		}
	}

	/**
	 * ������� ���ٲ����ݿ�ȽϺ� ��Ϊ��Щ��д�Ĳ�ѯ�¼���֪������ôд�� ��Ӧ�������е����������ô���ʵ��
	 * �ȸĳ������Ժ�����޸� Ӧ�þ���ؼ����;���д�߼�
	 * ����������һ��ʱ����������Ҫд��ͬ���߼�����ѡ����ѡ�ȶ���ͬ������ֻ����ȫƥ��
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
					((RowNumberItemVO) all_realValueData[i][0]).setRecordIndex(i); //�����к�,��Ϊ��̨���ܾ���������,��ǰ̨������!����������֪��������ͬ���Ļ�������!!��Ӱ���ҳ��Ч��!!������̺�ʱ����!!���Ժ��Բ���!!
				}
				if (jComboBox_onePageRecords.getSelectedItem().equals("ȫ��")) {
					li_onepagerecords = all_realValueData.length;
				} else {
					//li_onepagerecords = Integer.parseInt(jComboBox.getSelectedItem().toString());  //
					li_onepagerecords = Integer.parseInt(jComboBox_onePageRecords.getEditor().getItem().toString());
				}
				putValue(all_realValueData); //��������
			} catch (Exception _ex) {
				MessageBox.showException(this, _ex);
			}

			// ���������и�
			if (templetVO.getIslistautorowheight()) { // ������Զ������и�!!
				autoSetRowHeight(); // �Զ������и�
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

			resetHeight(); // ���������и�
			autoSetTitleCheckedOrNot();//�Զ����ñ�ͷ��ѡ���Ƿ�ѡ�С����/2014-05-06��

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
		int li_height = 85; //ԭ����79
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
		//this.updateUI(); //��������repaint�ػ��������򣬵��½���Ч����Ч
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
		} else {//Ӧ��ûɶ���⹤��������ʱ���������������һ����˵��������û�е�һ�²�ѯ�������ó��Զ���ѯ�ڷ�����б���ˢ������/sunfujun/20120904
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
		} else {//Ӧ��ûɶ���⹤��������ʱ���������������һ����˵��������û�е�һ�²�ѯ�������ó��Զ���ѯ�ڷ�����б���ˢ������/sunfujun/20120904
			if (getQuickQueryPanel() != null) {
				getQuickQueryPanel().onQuickQuery(false, _isRegHVOinRowNumberVO);
			} else {
				QueryDataByCondition(" 1=1 ");
			}
		}
	}

	public void refreshCurrSelectedRow() {
		int li_selrow = this.getSelectedRow(); //ȡ�õ�ǰѡ�е���!
		if (li_selrow < 0) {
			return;
		}
		try {
			RowNumberItemVO rowNoVO = (RowNumberItemVO) getValueAt(li_selrow, 0); //
			//int li_rowindex = rowNoVO.getRecordIndex(); // 
			String str_pkvalue = this.getRealValueAtModel(li_selrow, this.getTempletVO().getPkname()); //ȡ��������ֵ

			if (str_pkvalue != null) {
				//int li_index = findModelIndex(str_pkvalue); //
				Pub_Templet_1ParVO parTempletVO = templetVO.getParPub_Templet_1VO(); //�ؽ�һ������VO,���ڷ������������Ϣ�ǲ���Ҫ��!Ϊ�˼����������紫��,������С������С! ����ǳ���Ҫ!!
				Object[][] objs = getMetaDataService().getBillListDataByDS(getDataSourceName(), "select * from " + templetVO.getTablename() + " where " + templetVO.getPkname() + "='" + str_pkvalue + "'", parTempletVO, true, null); //ֱ��ȡĳһ������!!
				if (objs.length > 0) {
					//((RowNumberItemVO) objs[0][0]).setRecordIndex(li_rowindex); //�кű�����ԭ����,��Ϊ���ڷ�ҳ������!!
					bo_ifProgramIsEditing = true; //���봥���༭�¼�!!
					for (int i = 0; i < objs[0].length; i++) {
						this.setValueAt(objs[0][i], li_selrow, i); //Ϊ��ǰ�����ø��е�ֵ!!!
					}
					bo_ifProgramIsEditing = false; //�ָ������¼�
					//all_realValueData[li_rowindex] = objs[0]; //���»���������

				}
			}
			if (null != listQueryCallback) {
				listQueryCallback.queryCallback();
			}
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		} finally {
			bo_ifProgramIsEditing = false; //�ָ������¼�
		}
	}

	public void refreshData(Object[][] _data) {
		all_realValueData = _data;
		putValue(all_realValueData); //
	}

	/**
	 * �Զ����ñ�ͷ��ѡ���Ƿ�ѡ��!!�����/2014-05-06��
	 */
	public void autoSetTitleCheckedOrNot() {
		if (isRowNumberChecked) {//����б��м�¼ȫ��ѡ�У����ͷ��ѡ���ѡ�С����/2014-05-06��
			if (this.getRowCount() > 0) {
				boolean isallchecked = true;//�ж��б��������Ƿ�ȫ����ѡ��
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
	 * �Զ������и�!!
	 */
	public void autoSetRowHeight() {
		autoSetRowHeight(-1); //
	}

	/**
	 * �Զ������и�,����һ������,��ֹ����һ���ܱ�̬���п��!!
	 * 
	 * @param _maxheight
	 */
	public void autoSetRowHeight(int _maxheight) {
		int li_rowcount = this.getRowCount(); //
		if (li_rowcount > 0) {
			for (int i = 0; i < li_rowcount; i++) {
				int li_height = getRowMaxHeight(i); ////ȡ��ĳһ�����и�..
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
	 * ȡ�����и�...
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
	 * ȡ��ĳһ�е��и�.
	 * @param _row
	 * @return
	 */
	private int getRowMaxHeight(int _row) {
		int li_colcount = getTable().getColumnCount(); //
		int li_initrows = 1;
		for (int i = 0; i < li_colcount; i++) {
			TableColumn column = getTable().getColumnModel().getColumn(i); //
			String str_itemkey = (String) column.getIdentifier(); //
			if (findItemType(this.templetItemVOs, str_itemkey).equals(WLTConstants.COMP_TEXTAREA)) {// ����Ƕ����ı���
				Object str_value = getValueAt(_row, str_itemkey); //
				if (str_value != null) {
					int li_colwidth = column.getPreferredWidth(); //���(��������)
					int li_worllength = getWordLength(str_value.toString()); //�����ַ��ĳ���(����)����������˻������õ������Ͳ�׼�ˣ�����
					int li_rows = li_worllength / li_colwidth + 1; //�����ܹ��ж�����,����©����+1��Ҳ������Ҫ��ȡ��������޸ģ�
					if (li_rows > li_initrows) {
						li_initrows = li_rows; //
					}
				}
			}
		}

		if (li_initrows > 10) { //����10�е�ֻ��ʾ10��,�������ֳ��ߵ�,���ÿ�
			li_initrows = 10;
		} else if (li_initrows == 1) {
			return li_rowHeight;
		}
		return li_initrows * (li_rowHeight - 1);//�����ı����к�һ�еĸ߶�Ҫ��22����СЩ������ڼ���html���ӣ��и߾͸�С�ˣ�����޸ģ�
	}

	private int getUnicodeLength(String s) { // ���������ָ����һ���ַ���,������ֽڳ���,����ַ������и������ַ�,��ô���ĳ��Ⱦ���2
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
	 * ���봿�������,��û���кŵ�
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
	 * ���б��������ά����,һ�����ĸ�ֵ����
	 * @param _objs
	 */
	public void putValue(Object[][] _objs) {
		clearTable();
		java.util.Vector v_allRows = new java.util.Vector(); //
		if (_objs != null && _objs.length > 0) {
			for (int i = 0; i < _objs.length; i++) { //����
				java.util.Vector v_row = new java.util.Vector(); //һ�е�����!!!
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
		model.getDataVector().addAll(v_allRows); //����һ���Բ������! ������Ļ����!!!��ǰ��һ���еĲ����!!
		model.fireTableRowsInserted(0, v_allRows.size() - 1); //
		getTable().invalidate(); //
		getTable().repaint(); //
		rowHeaderTable.invalidate(); //
		rowHeaderTable.repaint(); //
		scrollPanel_main.invalidate(); //��һ������һ��һ��Ҫ�����򣬿�����ʱ��Ļ�Ứ!
		scrollPanel_main.repaint(); //
	}

	/**
	 * ��һ��HashVO���������,���HashVO���鲻�����к�,���Ե�һ��Ҫ�Լ������к�
	 * 
	 * @param _hvos
	 */
	public void putValue(HashVO[] _hvos) {
		if (_hvos == null) {
			return;
		}
		clearTable();
		combine_mark = false;
		String[] str_itemkeys = this.templetVO.getItemKeys(); // �õ�����key
		String[] str_itemtype = this.templetVO.getItemTypes(); //
		all_realValueData = new Object[_hvos.length][str_itemkeys.length + 1]; //
		for (int i = 0; i < _hvos.length; i++) { // ��������!
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
							str_itemtype[j].equals(WLTConstants.COMP_FILECHOOSE) //���Ӹ������͡����/2016-11-16��
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
		refreshCombineData(); //combine_mark��� ����׷�Ӻ� tableModel���ݱ仯 �ϲ�ˢ�� �����/2013-07-23�� 
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

	//ǰһҳ
	private void onGoToPreviousPage() {
		if (li_currpage == 1) {
			MessageBox.show(this, "�Ѿ��ǵ�һҳ��!"); //
			return;
		}
		goToPage(li_currpage - 1);
	}

	private void onGoToNextPage() {
		if (li_currpage >= getTotalPages()) {
			MessageBox.show(this, "�Ѿ������һҳ��!"); //
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
			MessageBox.show(this, "������תҳ��¼������!");
			return;
		}
		goToPage(page);
	}

	/**
	 * ����ĳһҳ!
	 * @param _lipage
	 */
	public void goToPage(int _lipage) {
		goToPage(_lipage, true);
	}

	/**
	 * ����ĳһҳ!
	 * @param _lipage   ��ת���ڼ�ҳ
	 * @param _msg      ����Ҳ����Ƿ���ʾ������ø÷������п��ܻ��Զ�����ʾ��Ϣ�������������Ӹ����������2012-02-22���Ӹ÷���
	 */
	public void goToPage(int _lipage, boolean _msg) {
		int li_maxPages = getTotalPages(); //
		if (_lipage < 1 || _lipage > li_maxPages) {
			if (_msg) {
				MessageBox.show(this, "��[" + _lipage + "]ҳ������!"); //
			}
			return;
		}
		li_currpage = _lipage; //���õ�ǰҳ��!!!
		int[] li_rowsArea = null; //
		li_rowsArea = new int[] { (_lipage - 1) * li_onepagerecords + 1, _lipage * li_onepagerecords }; //1(1-20),2(21-40)
		queryDataByDS(this.getDataSourceName(), this.str_realsql, li_rowsArea, false); //���һҳ
	}

	//ȡǰ��ǰ������м�¼��! �пͻ�����кŲ���1-20,���Ǽ���ǰ�������!
	public int getFrontAllRecords() {
		return (li_currpage - 1) * li_onepagerecords; //
	}

	/**
	 * ȡ���ܼƵ�ҳ��!!!
	 * @return
	 */
	private int getTotalPages() {
		int li_pages = li_TotalRecordCount / li_onepagerecords; //�����ܹ��ж���ҳ!!!
		if (li_TotalRecordCount % li_onepagerecords != 0) {
			li_pages = li_pages + 1;
		}
		return li_pages;
	}

	/**
	 * ���÷�ҳ���ϵ�˵��!!
	 */
	public void resetPageInfo() {
		StringBuilder sb_text = new StringBuilder(); //
		if (this.templetVO.getIsshowlistpagebar()) { //�����ʾ��ҳ��
			sb_text.append("��" + li_TotalRecordCount + "��," + "��ǰ��" + li_currpage + "/" + getTotalPages() + "ҳ"); //
		} else { //�������ʾ��ҳ
			sb_text.append("��" + li_TotalRecordCount + "��"); //
		}
		if (getTBUtil().getSysOptionBooleanValue("�б��ҳ���Ƿ�������˵��", true)) { //������Ф���ε�UI��׼û����������!һ��Ҫȥ��!!!ʵ�����и���!
			String[] str_orderInfo = getOrderConsInfo(); //
			if (!"��".equals(str_orderInfo[0])) {//���û�����򣬾Ͳ�Ҫ��ʾ"����:��"�ˣ�����б�ť�ܶ����з�ҳ�����������ӱ�о����ҡ����/2012-03-27��
				sb_text.append(",����:"); //
				sb_text.append(str_orderInfo[0]); //
				label_pagedesc.setToolTipText("��������:" + str_orderInfo[1]); //
			}
		}
		label_pagedesc.setText(sb_text.toString()); //
	}

	//ȡ����������
	private String[] getOrderConsInfo() {
		String str_info = getOrderCondition(); //
		if (str_info == null || str_info.trim().equals("")) {
			return new String[] { "��", "��" };
		}
		String[] str_items = getTBUtil().split(str_info, ","); //
		StringBuilder str_infodesc = new StringBuilder(); //�����˵��,������ʱ����̫��,������ʾ����,����ֻ��ʾ��һ��!!
		StringBuilder str_infotip = new StringBuilder(); //�������ȥ��ToolTip˵��!!
		for (int i = 0; i < str_items.length; i++) {
			String str_item = str_items[i].trim(); //
			int li_pos = str_item.indexOf(" "); //����û�пո�??
			String str_key = null; //
			boolean isDesc = false; //
			if (li_pos < 0) {
				str_key = str_item; //
			} else {
				str_key = str_item.substring(0, str_item.indexOf(" ")).trim(); //
				String str_isdesc = str_item.substring(str_item.indexOf(" "), str_item.length()).trim(); //
				if ("desc".equalsIgnoreCase(str_isdesc)) { //�����desc
					isDesc = true; //
				}
			}
			String str_name = str_key; //
			Pub_Templet_1_ItemVO itemVO = getTempletItemVO(str_key); //
			if (itemVO != null) {
				str_name = itemVO.getItemname(); //
			}
			if (i == 0) {
				str_infodesc.append(str_name + (isDesc ? "��" : "��")); //
				if (str_items.length > 1) {
					str_infodesc.append("..."); //
				}
			}
			str_infotip.append(str_name + (isDesc ? "��" : "��")); //
			if (i != str_items.length - 1) {
				str_infotip.append(","); //
			}
		}
		return new String[] { str_infodesc.toString(), str_infotip.toString() }; //
	}

	/**
	 * ȡ��ĳһҳ����
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
	 * ȡ�ò���˵���е�SQL���!!!
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
			_allrefdesc = _allrefdesc.trim(); // ��ȥ�ո�
			String str_remain = _allrefdesc.substring(li_pos + 1, _allrefdesc.length()); //
			int li_pos_tree_1 = str_remain.indexOf(";"); //
			str_sql = str_remain.substring(0, li_pos_tree_1); // SQL���
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
		int li_column = this.getTable().getSelectedColumn(); //ѡ�е���..
		if (li_column < 0) {
			return null; //
		}
		return (String) this.getTable().getColumnModel().getColumn(li_column).getIdentifier();
	}

	/**
	 * ��ձ�,�����������ݿ⣡��ǰ�����и��������ص�Bug,����ǰ��һ���е�ȥɾ��!�������ܶ�ʱ�ǳ���!!
	 * Ҫ�����ǵ�ǰ���и��ȴ���ʱ,�ᱨ�����������!!! �����ܳ�ʱ�䶼û����������Ϊʲô!! ��������ҵ��Ŀ�����ڷ������������(2010-12-24)
	 * ����İ취��ʹ��������������µķ���Ϊɾ������!!! ����ɾ������!!
	 */
	public void clearTable() {
		getTable().clearSelection();
		DefaultTableModel model = (DefaultTableModel) getTable().getModel(); //
		if (model.getRowCount() <= 0) {
			return;
		}
		model.getDataVector().removeAllElements(); //һ�����������!
		model.fireTableRowsDeleted(0, model.getRowCount()); //
		getTable().invalidate(); //
		getTable().repaint();
		rowHeaderTable.invalidate(); //
		rowHeaderTable.repaint(); //
		scrollPanel_main.invalidate(); //��һ������һ��һ��Ҫ�����򣬿�����ʱ��Ļ�Ứ!
		scrollPanel_main.repaint(); //
	}

	//����������Ҫ���ܵ��ֶ��嵥!!!
	public String[] getiEncryptKeys() {
		ArrayList al_keys = null; //
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			if (templetItemVOs[i].getIsencrypt()) { //�����Ҫ����,�����
				if (al_keys == null) {
					al_keys = new ArrayList();
				}
				al_keys.add(templetItemVOs[i].getItemkey()); //����!
			}
		}
		if (al_keys == null) {
			return null; //
		}
		return (String[]) al_keys.toArray(new String[0]); //
	}

	/**
	 * ������SQL!
	 * 
	 * @return
	 */
	public String getInsertSQL(int _row) {
		return this.getBillVO(_row).getInsertSQL(getiEncryptKeys());
	}

	/**
	 * ɾ����SQL!
	 * 
	 * @return
	 */
	public String getDeleteSQL(int _row) {
		return this.getBillVO(_row).getDeleteSQL();
	}

	/**
	 * �õ�ĳһ����¼��update SQL
	 * 
	 * @param _row
	 * @return
	 */
	public String getUpdateSQL(int _row) {
		return this.getBillVO(_row).getUpdateSQL(getiEncryptKeys()); //��Ҫ���ܵ��ֶ�Ҫ����!
	}

	/**
	 * �õ����е�updateSQL
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

	//��õ�ǰѡ���й�ѡ���Ƿ�ѡ��
	public boolean isSelectedRowChecked() {
		JComponent rowNumberCompent = this.editor.getCompent();
		boolean state = false;
		if (rowNumberCompent instanceof JCheckBox) {//����ǹ�ѡ��
			state = ((JCheckBox) rowNumberCompent).isSelected();//�Ƿ�ѡ��
		}
		return state;
	}

	//��ñ�ͷ��ѡ���Ƿ�ѡ��
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
	 * �Զ����ñ���ѡ�еĹ�ѡ���Ƿ�ѡ�У���ִ�й�ʽ
	 * Ԭ���� 20131029 ���
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
	 * ����ͷ��ѡ������¼�
	 * Ԭ���� 20131029 ���
	 */
	public void checkedTitleChanged() {
		for (int i = 0; i < v_checkedAllListeners.size(); i++) {
			BillListCheckedAllListener listener = (BillListCheckedAllListener) v_checkedAllListeners.get(i);
			listener.onBillListCheckedAll(new BillListCheckedAllEvent(this));
		}
	}

	public int getSelectedRow() {
		//�˴���ΪĬ�ϲ����ǹ�ѡ���,ԭ������:
		//1.�Թ�ѡ��ʽѡ������, һ�������ڽ��ж�������ͬʱ����ʱ������, ��"���","�༭","ɾ��"����Ե�һ���ݵĲ�����Ӧ�ò���ֱ��ѡ��ķ�ʽ.
		// ����, �����ù�ѡ��ʽ, ��ѡ�˵�һ��(RowNum=1), �����������༭��ɾ����2��(RowNum=2)ʱ, ����Ӧ��ȡ����1�еĹ�ѡ״̬, Ȼ���ٹ�ѡ��2��, �����Ĳ����Ͳ�������.
		//2.���Ĭ��Ϊ���ǹ�ѡ�����������˫����ͬ��¼����󿴵��Ķ���ͬһ�����ݣ�
		//3.һ�������û�й�ѡ����б��й�ѡ�������Ͼ�������
		//ͬ���޸ĵķ�����getSelectedBillVO()��getSelectedBillVOs()��getSelectedRows()�����/2012-03-01��
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
		//�˴���ΪĬ�ϲ����ǹ�ѡ���,ԭ������:
		//1.�Թ�ѡ��ʽѡ������, һ�������ڽ��ж�������ͬʱ����ʱ������, ��"���","�༭","ɾ��"����Ե�һ���ݵĲ�����Ӧ�ò���ֱ��ѡ��ķ�ʽ.
		// ����, �����ù�ѡ��ʽ, ��ѡ�˵�һ��(RowNum=1), �����������༭��ɾ����2��(RowNum=2)ʱ, ����Ӧ��ȡ����1�еĹ�ѡ״̬, Ȼ���ٹ�ѡ��2��, �����Ĳ����Ͳ�������.
		//2.���Ĭ��Ϊ���ǹ�ѡ�����������˫����ͬ��¼����󿴵��Ķ���ͬһ�����ݣ�
		//3.һ�������û�й�ѡ����б��й�ѡ�������Ͼ�������
		//ͬ���޸ĵķ�����getSelectedBillVO()��getSelectedBillVOs()��getSelectedRow()�����/2012-03-01��
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
	 * ɾ��ѡ�е���
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
	 * ������������/��ʾ��.
	 */
	public void reShowHideTableColumn() {
		String clickedItemKey = null; //
		TableColumn col = this.getClickedTableColumn();
		if (col != null) {
			clickedItemKey = (String) col.getIdentifier(); //���������..
		}

		ArrayList allTempletDefineShows = new ArrayList(); //ģ���ж�����Ҫ��ʾ����!!!
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getListisshowable() != null && templetItemVOs[i].getListisshowable().booleanValue()) { //�����������ʾ��
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

		ShowHideSortTableColumnDialog dialog = new ShowHideSortTableColumnDialog(this, "��������������", 325, 325, str_alldata, clickedItemKey); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			//Ԭ���� 20130530�޸�  ��Ҫ������õ���ʾ�С������� ��Ҫ�������ݴ洢�ڱ���			
			String[][] str_returnData = dialog.getReturnData(); //
			if (str_returnData != null) {
				for (int i = 0; i < str_returnData.length; i++) {
					setItemVisible(str_returnData[i][0], str_returnData[i][2].equals("Y") ? true : false); //
				}
				StringBuffer sb = new StringBuffer();//��֮���÷ֺŸ�����ֻ����Ҫ��ʾ��
				for (int i = 0; i < str_returnData.length; i++) {
					setItemVisible(str_returnData[i][0], str_returnData[i][2].equals("Y") ? true : false); //
					if (str_returnData[i][2].equals("Y")) {
						sb.append(str_returnData[i][0]).append(";");
					}
				}
				CustomizeColumnMap.putValue(templetVO.getTempletcode(), sb.toString());// д��cache
				CustomizeColumnMap.writeQuickSearchHisToCache();//��cache���뱾��
			}
		}
	}

	/**
	 * ����ĳһ���Ƿ���ʾ!!!
	 * 
	 * @param _itemKey
	 * @param _visible
	 */
	public void setItemVisible(String _itemKey, boolean _visible) {
		if (_visible) {
			if (isVisible(_itemKey)) { // ����������ʾ��,��ʲô������
			} else {
				showColumn(_itemKey); // �����������ʾ,����ʾ֮
			}
		} else {
			if (isVisible(_itemKey)) { // ���������ʾ��,������֮
				hiddenColumn(_itemKey); //
			} else { // �������������ţ���ʲô������
			}
		}
	}

	public void setItemWidth(String _itemKey, int _width) {
		TableColumn column = findColumn(_itemKey);
		if (column != null) {
			column.setPreferredWidth(_width); //�����µĿ��
		}
	}

	/**
	 * ���������Ƿ���ʾ��!!
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
	 * �����������Ƿ���ʾ������!
	 * 
	 * @param _visible
	 */
	public void setItemsAllVisible(boolean _visible) {
		String[] str_keys = getTempletVO().getItemKeys(); //
		setItemsVisible(str_keys, _visible); //
	}

	/**
	 * ����ĳһ��
	 * 
	 * @param _itemKey
	 */
	private void hiddenColumn(String _itemKey) {
		int li_count = getTable().getColumnModel().getColumnCount(); // ������ʾ��������
		for (int i = 0; i < li_count; i++) { // ����
			TableColumn column = getTable().getColumnModel().getColumn(i);
			if (column.getIdentifier().toString().equalsIgnoreCase(_itemKey)) { // ���ĳһ�е������
				getTable().getColumnModel().removeColumn(column); // ɾ��ĳһ��
				return; //
			}
		}
	}

	/**
	 * ����ĳһ��
	 * 
	 * @param _itemKey
	 */
	private void showColumn(String _itemKey) {
		int li_modelindex = findModelIndex(_itemKey); //
		if (li_modelindex >= 0) { // ����ҵ���
			int li_itemindex = li_modelindex - 1; // �кŲ���
			for (int i = li_itemindex + 1; i < templetItemVOs.length; i++) {
				if (isVisible(templetItemVOs[i].getItemkey())) { // ����Һ����ĳһ������ʾ�ŵ�
					insertColumn(templetItemVOs[i].getItemkey(), _itemKey); // ����ҵ����оͲ��ڸ���ǰ��
					return; // ֱ�ӷ���
				}
			}
			this.getColumnModel().addColumn(getTableColumn(_itemKey)); // �����ֱ�Ӳ����������!
		}
	}

	/**
	 * �ж�ĳһ���Ƿ���ʾ!!
	 * 
	 * @param _itemKey
	 * @return
	 */
	public boolean isVisible(String _itemKey) {
		int li_count = getTable().getColumnModel().getColumnCount(); // ������ʾ��������
		for (int i = 0; i < li_count; i++) { // ����
			if (getTable().getColumnModel().getColumn(i).getIdentifier().toString().equalsIgnoreCase(_itemKey)) { // ���ĳһ�е������
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
	 * ������������!!
	 */
	private void onResetOrderCons() {
		String[] str_allViewColumns = this.templetVO.getRealViewColumns(); //���п���ƥ���ѯ��������!!
		String[][] str_allViewColumnNames = new String[str_allViewColumns.length][2]; //
		for (int i = 0; i < str_allViewColumns.length; i++) {
			str_allViewColumnNames[i][0] = str_allViewColumns[i]; //��һ��!!!
			Pub_Templet_1_ItemVO itemVO = getTempletItemVO(str_allViewColumns[i]); //
			if (itemVO != null) { //����ҵ���
				str_allViewColumnNames[i][1] = itemVO.getItemname(); //����
			}
		}
		String str_orderCondition = templetVO.getOrdercondition(); //ʵ�ʵ���������
		String[] str_filterkeys = (String[]) v_lockedcolumns.toArray(new String[0]); //��������!!!
		ResetOrderConditionDialog dialog = new ResetOrderConditionDialog(this, str_allViewColumnNames, str_orderCondition); //
		dialog.setVisible(true);//��ʾ!!!
		if (dialog.getCloseType() == 1) {
			String str_newCons = dialog.getReturnCons(); //
			this.templetVO.setOrdercondition(str_newCons); //���������ڴ��е���������!!!
			resetPageInfo(); //��������˵��!!!
			MessageBox.show(this, "�����������������ɹ�,������ѯ��ť���²�ѯ!"); //
		}
	}

	//��һ�ֱ༭
	private void modifyTemplet() {
		String clicked_itemkey = (String) (getColumnModel().getColumn(getClickedColumnPos()).getIdentifier()); //
		try {
			new MetaDataUIUtil().modifyTemplet(this, this.getTempletVO().getBuildFromType(), this.getTempletVO().getBuildFromInfo(), this.getTempletVO().getTempletcode(), this.getTempletVO().getTempletname(), false, clicked_itemkey);
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
			return; //
		}
	}

	//�ڶ��ֱ༭!!!
	private void modifyTemplet2(String templetname) {
		try {
			new MetaDataUIUtil().modifyTemplet(this, this.getTempletVO().getBuildFromType(), this.getTempletVO().getBuildFromInfo(), this.getTempletVO().getTempletcode(), this.getTempletVO().getTempletname(), false, null);
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
			return; //
		}
	}

	//�����ݿ������ӹ�������4���ֶ�!
	private void menu_db_flow_modifyTemplet_database(int _x, int _y) {
		try {
			String str_tablename = this.templetVO.getSavedtablename(); //����ı���
			String str_sql_1 = "alter table " + str_tablename + " add billtype varchar(100)"; //
			String str_sql_2 = "alter table " + str_tablename + " add busitype varchar(100)"; //
			String str_sql_3 = "alter table " + str_tablename + " add wfprinstanceid decimal"; //
			String str_sql_4 = "alter table " + str_tablename + " add create_userid decimal"; //
			//UIUtil.executeBatchByDS(null, new String[] { str_sql_1, str_sql_2, str_sql_3, str_sql_4 }); //
			MessageBox.show("���ֶ�ִ������SQL(���ݲ�ͬ�����ݿ��޸���������):\r\n" + str_sql_1 + ";\r\n" + str_sql_2 + ";\r\n" + str_sql_3 + ";\r\n" + str_sql_4 + ";\r\n");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, e.getMessage());
		}
	}

	//��ģ�������ӹ��������ĸ��ֶ�
	private void menu_db_flow_modifyTemplet(int _x, int _y) {
		try {
			StringBuilder sb_text = new StringBuilder(); //
			sb_text.append("����ģ�����ֹ���������4���ֶ�:\r\n"); //
			sb_text.append("billtype �ı���,������Ҫָ��Ĭ��ֵ\r\n"); //
			sb_text.append("busitype �ı���,������Ҫָ��Ĭ��ֵ\r\n"); //
			sb_text.append("wfprinstanceid �ı���\r\n"); //
			sb_text.append("create_userid ���ֿ�,Ĭ��ֵ�ǵ�¼��Աid\r\n"); //
			MessageBox.show(sb_text.toString()); //
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, e.getMessage());
		}
	}

	//��ģ�����������̼�ص�13���ֶ�
	private void menu_db_addflow13Field_templet(int _x, int _y) {
		if (!checkIsCanConfigTemplet(true)) { //���Ƿ�ɱ༭����
			return;
		}
		try {
			String[][] str_items = new String[][] { { "task_curractivityname", "��ǰ����", "N" }, //����Ĵ��������Ѱ����ж���Ҫ!
					{ "task_creatername", "�ύ��", "N" }, { "task_createtime", "�ύʱ��", "N" }, { "task_createrdealmsg", "�ύ���", "N" }, //������
					{ "task_realdealusername", "ʵ�ʴ�����", "N" }, { "task_dealtime", "����ʱ��", "N" }, { "task_dealmsg", "�������", "N" }, //�Ѱ���
					{ "prins_curractivityname", "��ǰ����", "Y" }, { "prins_lastsubmitername", "�������", "N" }, { "prins_lastsubmittime", "�����ʱ��", "N" }, { "prins_lastsubmitmsg", "����������", "N" }, { "prins_mylastsubmittime", "�ҵ������ʱ��", "Y" }, { "prins_mylastsubmitmsg", "�ҵ���������", "Y" } //�ӵ��ݳ���,ֱ�ӹ�������ʵ��������ֶ�!
			}; ////

			StringBuilder sb_msg = new StringBuilder(); //
			for (int i = 0; i < str_items.length; i++) {
				sb_msg.append(str_items[i][0] + "/" + str_items[i][1] + "\n"); //
			}
			if (JOptionPane.showConfirmDialog(this, "��ȷ��Ҫ��ģ��������12�����̼���ֶ���?�⽫���������ֶ�:\r\n" + sb_msg.toString(), "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			String str_retult = UIUtil.getMetaDataService().insertTempletItem(this.templetVO.getPk_pub_templet_1(), str_items); //
			MessageBox.show("����ɹ�!���:" + str_retult); //
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
	 * �ж��Ƿ��������ģ��,�������Class���͵�,�򲻿�����,�����XML���͵�,����Ҫ���Ƶ�DB�н�������!! �����DB��,��֣����XML��Ҳ��,����Խ�XML�и�����Ƚ�,Ҳ����ֱ��ɾ���Լ���ʹ��XML�е�! ���XML��û��������ǰ���߼�!! 
	 * @return
	 */
	private boolean checkIsCanConfigTemplet(boolean _isquiet) {
		return checkIsCanConfigTemplet(_isquiet, null);
	}

	private boolean checkIsCanConfigTemplet(boolean _isquiet, String _colKey) {
		try {
			String str_buildFromType = templetVO.getBuildFromType(); //����������!!
			String str_buildFromInfo = templetVO.getBuildFromInfo(); //��������Ϣ!!
			String str_templetCode = templetVO.getTempletcode(); //ģ�����
			String str_templetName = templetVO.getTempletname();
			return new MetaDataUIUtil().checkTempletIsCanConfig(this, str_buildFromType, str_buildFromInfo, str_templetCode, str_templetName, _isquiet, _colKey); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
			return false; //
		}
	}

	/*
	 * �Զ�����Demo����
	 */
	public void menu_db_AutoBuildData() {
		new AutoBuildDataDialog(this, templetVO, templetItemVOs);
	}

	/**
	 * ���¼���ҳ��
	 */
	public void reload() {
		reload(this.str_templetcode); //
	}

	/**
	 * ���¼���ҳ��
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
		v_lockedcolumns.removeAllElements(); // �������
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
		rowHeaderTable = new ResizableTable(getTableModel(), rowHeaderColumnModel); // �����µı�..
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
					showPopMenu2(evt.getComponent(), evt.getX(), evt.getY()); // �����˵�
				}
			}

		});

		if (getTable().getSelectedRow() >= 0) {
			rowHeaderTable.setRowSelectionInterval(getTable().getSelectedRow(), getTable().getSelectedRow());
		}

		rowHeaderTable.setSelectionModel(getTable().getSelectionModel()); // ����������¼���ʧ������!!
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

		refreshCombineData_header(_pos); //���� �ϲ�ˢ�� �����/2013-07-23�� 
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
		v_lockedcolumns.removeAllElements(); // �������
		bo_tableislockcolumn = false;

		refreshCombineData(); //���� �ϲ�ˢ�� �����/2013-07-23�� 
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

	//�鿴����..
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
		BillDialog dialog = new BillDialog(this, "�鿴��" + itemVO.getItemname() + "������", 800, 200); //���ڸ��㡾xch/2012-03-07��
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
	 * ��ĳһλ�ò���һ��,
	 * 
	 * @param _columnindex
	 *            ��Ҫ����ı����е�λ��
	 * @param _modelindex
	 *            �������ݵ���һ��
	 */
	public void insertColumn(int _columnindex, int _modelindex) {
		TableColumn column = getTableColumns()[_modelindex]; // �ҵ���һ��
		this.getColumnModel().addColumn(column);
		this.getColumnModel().moveColumn(table.getColumnCount() - 1, _columnindex); // �ƶ���˳��
	}

	/**
	 * ��ĳһλ�ò���һ��,
	 * 
	 * @param _columnindex
	 *            ��Ҫ����ı����е�λ��
	 * @param _modelindex
	 *            �������ݵ���һ��
	 */
	public void insertColumn(int _columnindex, String _modelkey) {
		int li_modelindex = findModelIndex(_modelkey); //
		TableColumn column = getTableColumns()[li_modelindex - 1]; // �ҵ���һ��
		this.getColumnModel().addColumn(column);
		this.getColumnModel().moveColumn(table.getColumnCount() - 1, _columnindex);
	}

	/**
	 * ��ĳһѮ��ǰ�����һ��
	 * 
	 * @param _columnkey
	 *            ĳһ��
	 * @param _modelkey
	 *            ��Ҫ�������!!
	 */
	public void insertColumn(String _columnkey, String _modelkey) {
		int li_columnindex = findColumnIndex(_columnkey); // ���е�λ��
		TableColumn column = getTableColumn(_modelkey); // �ҵ���һ��
		this.getColumnModel().addColumn(column); // �������һ������һ��
		this.getColumnModel().moveColumn(table.getColumnCount() - 1, li_columnindex); // �����һ���Ƶ���Ӧ��λ��
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
	 * ɾ��ĳһ��,��ʾ����
	 * 
	 * @param _index
	 */
	public void deleteColumn(int _index) {
		TableColumn column = this.getColumnModel().getColumn(_index);
		this.getColumnModel().removeColumn(column);
	}

	/**
	 * ɾ��ĳһ��,����key
	 * 
	 * @param _key
	 */
	public void deleteColumn(String _key) {
		hiddenColumn(_key); //
	}

	/**
	 * ��ʾ�б�����!!
	 */
	public void mouseDoubleClicked(MouseEvent e) {
		int li_row = this.getSelectedRow(); //
		if (li_row < 0) {
			return;
		}
		BillVO billVO = this.getBillVO(li_row); //
		if (v_MouseDoubleClickListeners.size() > 0) { //������м�����˫���¼�
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

		int li_column = this.getTable().getSelectedColumn(); // ѡ�е���
		//ɶҲûѡ
		if (li_column < 0) {
			return;
		}

		String itemkey = (String) this.getTable().getColumnModel().getColumn(li_column).getIdentifier(); //
		Pub_Templet_1_ItemVO itemVO = getTempletItemVO(itemkey); //
		String itemtype = itemVO.getItemtype(); //
		Boolean bo_ishtml = itemVO.getListishtmlhref(); //
		if (bo_ishtml || itemtype.equals(WLTConstants.COMP_BUTTON) || itemtype.equals(WLTConstants.COMP_FILECHOOSE)) { // ���HTML��������ʾ�������ǰ�ť���������ļ�ѡ���,�򷵻أ������¼�����
			return; //
		}

		int li_mouselistenercount = table.getMouseListeners().length; //
		if (li_mouselistenercount > 5) {
			return; // ������������ϵļ���������
		}

		boolean isShowCard = new TBUtil().getSysOptionBooleanValue("˫���б��Ƿ񵯳���Ƭ", true); //Ĭ���ǵ���
		if (isShowCard) {
			cardShowInfo();
		}
		//cardShowInfo(); //��Ƭ��ʾ!!�����ͻ����˫��������Ƭ������,�е����Ī������,�е���������Bug���!���Ի���ȥ��Ϊ��!��Ϊ����ͨ������Ҽ�ѡ�񡾿�Ƭ�����ʵ�ָ�Ч��!!
	}

	public void setCanShowCardInfo(boolean _bo) {
		isCanShowCardInfo = _bo; //
	}

	public void cardShowInfo() {
		BillCardPanel cardPanel = new BillCardPanel(this.templetVO); //
		BillVO billVO = this.getSelectedBillVO(); //
		cardPanel.setBillVO(billVO); //
		if (this.getBillListBtn("comm_listselect2") != null && "���".equals(this.getBillListBtn("comm_listselect2").getName())) {
			Pub_Templet_1_ItemVO[] templetItemVOs = cardPanel.getTempletVO().getItemVos();
			for (int i = 0; i < templetItemVOs.length; i++) { //�������пؼ�!!
				final String str_itemkey = templetItemVOs[i].getItemkey();
				String str_type = templetItemVOs[i].getItemtype();
				if (str_type.equals(WLTConstants.COMP_LABEL)) { //Label
				} else if (str_type.equals(WLTConstants.COMP_TEXTFIELD) || str_type.equals(WLTConstants.COMP_NUMBERFIELD) || str_type.equals(WLTConstants.COMP_PASSWORDFIELD)) { //�ı���,���ֿ�,�����
					cardPanel.execEditFormula(str_itemkey);
				} else if (str_type.equals(WLTConstants.COMP_COMBOBOX)) { //������
					cardPanel.execEditFormula(str_itemkey);
				} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || //���Ͳ���1
						str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || //���Ͳ���1
						str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //��ѡ����1
						str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || //�Զ������
						str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //�б�ģ�����
						str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //����ģ�����
						str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //ע���������
						str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //ע�����
						str_type.equals(WLTConstants.COMP_DATE) || //����
						str_type.equals(WLTConstants.COMP_DATETIME) || //ʱ��
						str_type.equals(WLTConstants.COMP_BIGAREA) || //���ı���
						//str_type.equals(WLTConstants.COMP_FILECHOOSE) || //�ļ�ѡ���!!!����ǰҲ�ǵ�һ�����մ���,�������������û�Ҫ�����õ���ҳ������,�Է��ϴ�����ҳϵͳ��Ч��,�ٵ�һ��,��
						str_type.equals(WLTConstants.COMP_COLOR) || //��ɫѡ���
						str_type.equals(WLTConstants.COMP_CALCULATE) || //������
						str_type.equals(WLTConstants.COMP_PICTURE) || //ͼƬѡ���
						str_type.equals(WLTConstants.COMP_EXCEL) || //EXCEL
						str_type.equals(WLTConstants.COMP_OFFICE) //office
				) { //����Ǹ��ֲ���
					cardPanel.execEditFormula(str_itemkey);
				} else if (str_type.equals(WLTConstants.COMP_TEXTAREA)) { //�����ı���
					cardPanel.execEditFormula(str_itemkey);
				} else if (str_type.equals(WLTConstants.COMP_BUTTON)) { //��ť
					cardPanel.execEditFormula(str_itemkey);
				} else if (str_type.equals(WLTConstants.COMP_CHECKBOX)) { //��ѡ��
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
	 * ֱ��ɾ����¼.
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
			MessageBox.show(this, "�洢�ı�����" + str_tableName + "���������ֶ�����" + str_pkName + "��������Ϊ�գ�");
			return;
		}

		String[] str_sqls = new String[li_rows.length]; //
		for (int i = 0; i < li_rows.length; i++) {
			String str_pkValue = getRealValueAtModel(li_rows[i], str_pkName); //
			if (str_pkValue == null || str_pkValue.trim().equals("")) {
				MessageBox.show(this, "��ǰ��¼�������ֶ��ϵ�ֵΪ��,���ܽ��д˲�����");
				return;
			}
			str_sqls[i] = "delete from " + str_tableName.trim() + " where " + str_pkName + "='" + str_pkValue + "'";
		}

		if (JOptionPane.showConfirmDialog(this, "���Ƿ������ɾ��ѡ�еļ�¼��\r\n�⽫ִ��SQL��" + str_sqls[0] + "���ȹ���" + str_sqls.length + "������¼\r\n��ȷ�ϵ�ǰ��¼��ȷ�����ڱ�����У�ͬʱע���Ƿ�������������ϵ��Ҫ����\r\n��ǰ�Ĳ�ѯSQL�ǣ�\r\n" + this.str_realsql + "", "��ʾ", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			try {
				UIUtil.executeBatchByDS(getDataSourceName(), str_sqls);
				this.removeSelectedRows(); //��ҳ����ɾ��
				MessageBox.show(this, "ɾ����ǰ��¼�ɹ�����ɾ����" + str_sqls.length + "������¼��");
			} catch (Exception _ex) {
				MessageBox.showException(this, _ex);
			}
		}
	}

	//ֱ���޸ļ�¼,��ʵʩ�����п����޸�����
	private void dirUpdateRecord() {
		try {
			int li_selectedRow = this.getSelectedRow(); //
			if (li_selectedRow < 0) {
				MessageBox.showSelectOne(this);
				return;
			}
			String str_itemKey = getSelectedColumnItemKey(); //
			if (str_itemKey == null) {
				MessageBox.show(this, "��ѡ��һ��ִ�д˲���!"); ////
				return;
			}

			String str_tableName = this.getTempletVO().getSavedtablename(); //
			String str_pkName = this.getTempletVO().getPkname(); //
			if (str_tableName == null || str_tableName.trim().equals("") || str_pkName == null || str_pkName.trim().equals("")) {
				MessageBox.show(this, "�洢�ı�����" + str_tableName + "���������ֶ�����" + str_pkName + "��������Ϊ�գ�");
				return;
			}
			String str_pkValue = getRealValueAtModel(li_selectedRow, str_pkName); //�����ֶ���
			String str_selValue = getRealValueAtModel(li_selectedRow, str_itemKey); //ѡ�и��ӵ�ֵ!!

			JPanel panel = new JPanel(); //
			panel.setLayout(null); //
			JLabel label = new JLabel("��[" + str_tableName + "]���ֶ�[" + str_itemKey + "]��ֵ:"); //
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

			textField.setText("update " + str_tableName + " set " + str_itemKey + "='{��ֵ}' where " + str_pkName + "='" + str_pkValue + "'"); //
			if (JOptionPane.showConfirmDialog(this, panel, "��ȷ��Ҫֱ���޸ĸ��м�¼��[" + str_itemKey + "]ֵ��?", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			String str_newValue = textArea.getText(); //
			String str_sql = "update " + str_tableName + " set " + str_itemKey + "='" + str_newValue + "' where " + str_pkName + "='" + str_pkValue + "'"; //
			int li_rows = UIUtil.executeUpdateByDS(null, str_sql); //
			MessageBox.show(this, "�޸����ݳɹ�!�������ˡ�" + li_rows + "������¼!"); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex);
		}
	}

	/**
	 * ֱ����չ����������Ϣ!
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
				MessageBox.show(this, "ģ��û���ֶ�[wfprinstanceid],˵�������߹������ı�,����ִ�д˲���!");
				return;
			}

			String str_prinstanceid = billVO.getStringValue("wfprinstanceid"); //����ʵ��
			if (str_prinstanceid == null || str_prinstanceid.trim().equals("")) {
				MessageBox.show(this, "�ֶ�[wfprinstanceid]ֵΪ��,˵�����̻�û����,û�б�Ҫִ�д˲���!");
				return;
			}
			String str_tabName = billVO.getSaveTableName().toLowerCase(); //�������
			String str_pkName = billVO.getPkName().toLowerCase(); //�����ֶ���!
			String str_pkValue = billVO.getPkValue(); //�����ֶ�ֵ!

			String str_sql_1 = "delete from pub_wf_prinstance where rootinstanceid='" + str_prinstanceid + "'";
			String str_sql_2 = "delete from pub_wf_dealpool   where rootinstanceid='" + str_prinstanceid + "'";
			String str_sql_3 = "delete from pub_task_deal     where rootinstanceid='" + str_prinstanceid + "'";
			String str_sql_4 = "delete from pub_task_off      where rootinstanceid='" + str_prinstanceid + "'";
			String str_sql_5 = "update " + str_tabName + " set wfprinstanceid=null where " + str_pkName + "='" + str_pkValue + "'"; //

			String str_msg = "��ȷ��Ҫִ�д˲�����?�⽫����ü�¼�����߹��Ĺ�������Ϣ,��ִ������5��SQL:\n" + str_sql_1 + ";\n" + str_sql_2 + ";\n" + str_sql_3 + ";\n" + str_sql_4 + ";\n" + str_sql_5 + ";\n��ȷ�����Ƿ������������!";

			JPanel panel = new JPanel(); //
			panel.setLayout(null); //
			JTextArea textArea = new JTextArea(str_msg); //
			textArea.setFont(LookAndFeel.font); //
			textArea.setEditable(false); //
			JScrollPane scrollPanel = new JScrollPane(textArea); //
			scrollPanel.setBounds(0, 5, 475, 125); //
			panel.add(scrollPanel); //
			panel.setPreferredSize(new Dimension(475, 130)); ////
			if (JOptionPane.showConfirmDialog(this, panel, "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}

			UIUtil.executeBatchByDS(null, new String[] { str_sql_1, str_sql_2, str_sql_3, str_sql_4, str_sql_5 }); //
			this.setValueAt(null, li_row, "wfprinstanceid"); //�������
			MessageBox.show(this, "��չ�����������ݳɹ�,���������ѻص���ʼ¼��״̬!!\n��������˿����ڲݸ����ѯ���ü�¼,Ȼ�������ύ!"); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}

	}

	/**
	 * ��ʾ���ݿ��д��ʵ������!!!
	 * 
	 */
	private void showPopMenu(MouseEvent e, boolean _isadmin) {
		//table.clearSelection();  //
		Point p = e.getPoint();
		int row = table.rowAtPoint(p);
		int col = table.columnAtPoint(p); //
		if (table.getSelectedRows().length <= 1) { //���û��ѡ����,��ѡ�е���ֻ��һ��,������ѡ�е���,���������ѡ�ж��У���������!
			table.setRowSelectionInterval(row, row); //
		}

		if (col >= 0) {
			table.setColumnSelectionInterval(col, col); //����ѡ�е���,Ϊ���Ҽ��޸�ֵʹ��!
		}

		if (!isCanShowCardInfo) {
			menuItem_showCard.setEnabled(false); //
		}
		rightPopMenu.show(table, e.getX(), e.getY()); //
	}

	/**
	 * ��ʾҳ���ϵ�ʵ������
	 */
	public void showUIRecord() {
		int li_row = getSelectedRow();
		if (li_row < 0) {
			return;
		}

		String[] str_tabcolumnnames = new String[] { "ItemKey", "ItemName", "����", "ʵ��ֵ����", "ʵ��ֵ����" }; //
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
		//RowNumber����!!
		RowNumberItemVO rowNumberVO = getRowNumberVO(li_row); //
		HashVO rowHashVO = (rowNumberVO == null ? null : rowNumberVO.getRecordHVO()); //�к��е�HashVO
		if (rowHashVO != null) {
			JTabbedPane tabbedPane = new JTabbedPane(); //
			tabbedPane.addTab("��¼������", UIUtil.getImage("office_042.gif"), new JScrollPane(tmptable)); //

			//�����к������ݵı��!!!
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
			tabbedPane.addTab("�к��е�����", UIUtil.getImage("office_157.gif"), new JScrollPane(rowDataTable)); //
			contentPanel.add(tabbedPane, BorderLayout.CENTER); //
		} else {
			contentPanel.add(new JScrollPane(tmptable), BorderLayout.CENTER); //
		}
		JLabel label_warn = new JLabel("�����������ĳ���ֶ�����Ϊ��,��ǧ��Ҫע������Ǵ�ĳ����ͼ��ѯ��ɵ�!"); //
		label_warn.setBackground(Color.RED); //
		label_warn.setForeground(Color.WHITE); //
		label_warn.setOpaque(true); //
		contentPanel.add(label_warn, BorderLayout.NORTH); //

		JLabel label_warn2 = new JLabel("��������о�ĳ���ֶ����ݲ���,��ע��ʹ����һ�����鿴DBʵ�����ݡ����ܽ��бȽ�!"); //
		label_warn2.setBackground(Color.RED); //
		label_warn2.setForeground(Color.WHITE); //
		label_warn2.setOpaque(true); //
		contentPanel.add(label_warn2, BorderLayout.SOUTH); //

		BillDialog dialog = new BillDialog(this, "�鿴UIʵ������", 800, 350); //
		dialog.getContentPane().add(contentPanel); //
		dialog.setVisible(true); //
	}

	public void showDBRecord() {
		int li_row = getSelectedRow();
		if (li_row < 0) {
			return;
		}
		if (templetVO.getTablename() == null || templetVO.getTablename().trim().equals("")) {
			MessageBox.show(this, "ģ����û�ж����ѯ����,���ܲ鿴"); //
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
	 * ���ٶ�λ...
	 */
	public void quickSearch() {
		int li_colIndex = getClickedColumnPos(); //
		TableColumn col = this.getClickedTableColumn(); //�������
		String clickedItemName = getColumnName(li_colIndex); //
		int li_rowCount = getTable().getRowCount(); //
		int li_colCount = getTable().getColumnCount(); //
		String[][] str_data = new String[li_rowCount][li_colCount]; //
		for (int i = 0; i < li_rowCount; i++) {
			for (int j = 0; j < li_colCount; j++) {
				str_data[i][j] = (getTable().getValueAt(i, j) == null ? "" : String.valueOf(getTable().getValueAt(i, j)));
			}
		}

		String[] str_colNames = new String[li_colCount]; //��������
		int[] li_width = new int[li_colCount]; //�����п�
		for (int j = 0; j < li_colCount; j++) {
			str_colNames[j] = getColumnName(j); //
			li_width[j] = getTable().getColumnModel().getColumn(j).getPreferredWidth(); //
		}

		QuickSearchDialog dialog = new QuickSearchDialog(this, li_colIndex, clickedItemName, str_colNames, li_width, str_data); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			clearSelection(); //�����ѡ��!!!
			int[] li_allSelectedRows = dialog.getSelectedRows();
			for (int i = 0; i < li_allSelectedRows.length; i++) {
				table.addRowSelectionInterval(li_allSelectedRows[i], li_allSelectedRows[i]); //ѡ��
			}
			Rectangle rect = table.getCellRect(li_allSelectedRows[0], 0, true);
			table.scrollRectToVisible(rect); //
		}
	}

	/**
	 * �����Ƿ���ʾ��ҳ.
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
	 * ˫��ĳһ��ʱ��ĳһ�н�������!!!!
	 * @param _point
	 */
	private void sortByColumn(Point _point) {
		int li_rowcount = this.getTableModel().getRowCount(); //
		all_realValueData = new Object[li_rowcount][this.templetItemVOs.length + 1]; //
		for (int i = 0; i < li_rowcount; i++) {
			all_realValueData[i][0] = getRowNumberVO(i); //�к�
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
		boolean isInt = templetItemVOs[li_modelindex - 1].getItemtype().equalsIgnoreCase(WLTConstants.COMP_NUMBERFIELD); //�Ƿ������ֿ�
		if (li_currsorttype == 0 || li_currsorttype == -1) {
			sortObjs(this.all_realValueData, li_modelindex, isInt, false); // ����
			putValue(all_realValueData);
			li_currsorttype = 1;
		} else {
			sortObjs(this.all_realValueData, li_modelindex, isInt, true); // ����
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
	 * ����ǰҳ���ϵ����ݽ��б��� �����ݲ�ͬ���ϵı༭״̬�����ɲ�ͬ��SQL���д���
	 * ���ﷵ���Ƿ񱣴�ɹ����Ա������ط����ú��в�ͬ����ʾ�����/2013-06-05��
	 */
	public boolean saveData() {
		this.stopEditing(); // �����༭
		String[] str_sql = getOperatorSQLs(); //
		try {
			UIUtil.executeBatchByDS(getDataSourceName(), str_sql); // ������!!
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
		sb_sql.append("�ͻ����ύ�ĳ�ʼSQL:\r\n��" + (getStr_realsql() == null ? "" : getStr_realsql()) + "��\r\n"); //

		sb_sql.append("����Ȩ��ִ�е����:\r\n��" + this.str_dataPolicyInfo + "��\r\n"); //Ȩ�޲��Լ���˵��
		sb_sql.append("����Ȩ��ִ�еĽ��:\r\n��" + this.str_dataPolicySQL + "��\r\n"); //Ȩ�޲��Լ���SQL

		sb_sql.append("��������ʵ��ִ�еĹ�����־:\r\n��" + (str_calltrackmsg == null ? "" : str_calltrackmsg) + "��\r\n"); //

		sb_sql.append("\r\n****************** ����������Ϣ ****************\r\n");
		sb_sql.append("ģ����롾" + templetVO.getTempletcode() + "��,ģ�����ơ�" + templetVO.getTempletname() + "��\r\n");
		sb_sql.append("������ʽ��" + templetVO.getBuildFromType() + "��,������Ϣ��" + templetVO.getBuildFromInfo() + "��\r\n");
		sb_sql.append("queryTable��" + (templetVO.getTablename() == null ? "" : templetVO.getTablename()) + "��,savedTable��" + templetVO.getSavedtablename() + "��,DataSourceName��" + templetVO.getDatasourcename() + "��\r\n");
		sb_sql.append("���õ�����Ȩ�޲��ԡ�" + templetVO.getDatapolicy() + "��,����Ȩ�޲��ԡ�" + templetVO.getDatapolicymap() + "��\r\n");

		String clicked_itemkey = (String) (getColumnModel().getColumn(getClickedColumnPos()).getIdentifier()); //
		Pub_Templet_1_ItemVO itemVO = getTempletItemVO(clicked_itemkey); //
		sb_sql.append("\r\n********** ����������Ϣ **********\r\n");
		sb_sql.append("ItemKey=��" + (itemVO.getItemkey() == null ? "" : itemVO.getItemkey()) + "��\r\n"); //
		sb_sql.append("ItemName=��" + (itemVO.getItemname() == null ? "" : itemVO.getItemname()) + "��\r\n"); //
		sb_sql.append("ItemType=��" + (itemVO.getItemtype() == null ? "" : itemVO.getItemtype()) + "��\r\n"); //
		sb_sql.append("Comboxdesc=��" + (itemVO.getComboxdesc() == null ? "" : itemVO.getComboxdesc()) + "��\r\n"); //
		sb_sql.append("RefDesc=��" + (itemVO.getRefdesc() == null ? "" : itemVO.getRefdesc()) + "��\r\n"); //
		sb_sql.append("LoadFormula=��" + (itemVO.getLoadformula() == null ? "" : itemVO.getLoadformula()) + "��\r\n"); //
		sb_sql.append("Editformula=��" + (itemVO.getEditformula() == null ? "" : itemVO.getEditformula()) + "��\r\n"); //
		sb_sql.append("Defaultvalueformula=��" + (itemVO.getDefaultvalueformula() == null ? "" : itemVO.getDefaultvalueformula()) + "��\r\n"); //
		MessageBox.showTextArea(this, sb_sql.toString());
	}

	private void showItemState() {
		new MetaDataUIUtil().showItemState(this, this.templetItemVOs);
	}

	/**
	 * ���ٸ�ֵ,����һ��,Ѹ�ٽ���һ�е�ֵ��ͬһֵ
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
			QuickPutValueDialog dialog = new QuickPutValueDialog(this, "���ٸ�ֵ", templetItemVOs[pos], true);
			if (!dialog.isModified())
				return;
			Object value = dialog.getValue();
			if (value == null || value.equals("") || (value instanceof RefItemVO) && ((RefItemVO) value).getId().equals("")) {
				if (JOptionPane.showConfirmDialog(this, "ȷ��Ҫ��Ϊ�и���ֵ��?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_NO_OPTION) {
					return;
				}
			}
			int[] li_selectedrows = table.getSelectedRows();
			if (li_selectedrows.length == 0) { // ���һ��ûѡ��,���ʾ�������е���
				for (int i = 0; i < table.getRowCount(); i++) {
					((BillListModel) table.getModel()).setValueAt(value, i, key);
					if (!this.getEditState(i).equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) { // �����������״̬
						this.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE);
					}
				}
			} else { // �����ѡ��,��ֻ��ѡ����д���
				for (int i = 0; i < li_selectedrows.length; i++) {
					((BillListModel) table.getModel()).setValueAt(value, li_selectedrows[i], key);
					if (!this.getEditState(li_selectedrows[i]).equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) { // �����������״̬
						this.setRowStatusAs(li_selectedrows[i], WLTConstants.BILLDATAEDITSTATE_UPDATE);
					}
				}
			}
		}
	}

	/**
	 * ���ٴ�͸��ѯ,��ʱ��Ϊ����Ȩ�޹���,��������ʾʱ�޷���������,���Ч������,�����ṩһ����������ֱ�Ӳ����κι��˲�ѯ��������!!
	 */
	private void quickStrikeQuery() {
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
		try {
			String str_sql = "select * from " + this.templetVO.getTablename() + " where '��͸��ѯ'='��͸��ѯ'";
			queryDataByDS(null, str_sql); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
	}

	/**
	 * ���ٱ��浱ǰλ������
	 */
	private void quickSaveCurrWidth() {
		if (!checkIsCanConfigTemplet(true)) { //���Ƿ�ɱ༭����
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
			li_oldpos[i] = itemVO.getShoworder().intValue(); // ��ǰ�����е�˳��!!
		}

		Arrays.sort(li_oldpos); // ��������
		String[] str_sqls = new String[li_allcolumncount];
		for (int i = 0; i < li_allcolumncount; i++) {
			str_sqls[i] = "update pub_templet_1_item set listwidth='" + li_currwidth[i] + "',showorder='" + li_oldpos[i] + "' where pk_pub_templet_1_item='" + str_pkid[i] + "'";
		}

		try {
			UIUtil.executeBatchByDS(null, str_sqls);
			MessageBox.show(this, UIUtil.getLanguage("���浱ǰ���еĿ����λ��˳��ɹ�!!"), WLTConstants.MESSAGE_INFO); //
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, UIUtil.getLanguage("���浱ǰλ����˳��ʧ��,ԭ��:" + e.getMessage())); //
		} //
	}

	private void quickSaveCurrRowHeight() {
		if (!checkIsCanConfigTemplet(true)) { //���Ƿ�ɱ༭����
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
				MessageBox.show(this, UIUtil.getLanguage("�����и߳ɹ�!"), WLTConstants.MESSAGE_INFO); //
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.show(this, UIUtil.getLanguage("���浱ǰλ����˳��ʧ��,ԭ��:" + e.getMessage())); //
			} //
		}
	}

	/**
	 * ���ñ�����Ƿ���ʾ������
	 */
	private void setTableGridLineVisiable() {
		JCheckBox box_rowLine = new JCheckBox("�������Ƿ���ʾ"); //
		JCheckBox box_colLine = new JCheckBox("�������Ƿ���ʾ"); //		
		box_rowLine.setFocusable(false);
		box_colLine.setFocusable(false);
		box_rowLine.setSelected(table.getShowHorizontalLines()); //
		box_colLine.setSelected(table.getShowVerticalLines()); //
		box_rowLine.setBounds(5, 5, 120, 20); //
		box_colLine.setBounds(125, 5, 120, 20); //
		JPanel panel = new JPanel(); //
		panel.setOpaque(false); //͸��!
		panel.setLayout(null); //
		panel.add(box_rowLine); //
		panel.add(box_colLine); //
		panel.setPreferredSize(new Dimension(250, 25)); //
		if (JOptionPane.showConfirmDialog(this, panel, "���Ƿ���������ñ�����Ƿ���ʾ?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			this.table.setShowHorizontalLines(box_rowLine.isSelected()); //��BasicTableUI�еķ���paintGrid()�ǵ����˸ò���,Ȼ�������λ�ͼ!! ����������ż����ɫ��һ�º�ͻ����������!Ҳ����Ҫ�ع�paintGrid������,���÷�����private��!! 
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
	 * ������е����ݲ�γ���Excel!
	 */
	public void exportToExcel() {
		//��ʾһ��,��ҳ�����ݻ���model������!!
		//		int li_result = JOptionPane.showOptionDialog(this, "�����ť��ҳ����ʾ�����ݡ�ֻ������ǰҳ����ʾ�е����ݡ�\r\n�����ť�������е����ݡ�������ǰҳ����(����������)�����ݡ�", "��ʾ", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "ҳ����ʾ������", "�����е�����", "ȡ��" }, "ҳ����ʾ������"); //
		//		if (li_result == JOptionPane.CANCEL_OPTION) {
		//			return;
		//		}
		//�����ʾ̫����, һ���˶�������! ����Ϊ�����Ƿ񵼳�ȫ������, �����и��û�����ĥ�˰�Сʱ, ���Ը���! Gwang 2013-09-03
		//һ����ͨ�û�ֻ��Ҫ��ʾ������, �������ѡ��ֻ��Թ���Ա. ��Ϊ���������û�
		Boolean flg = TBUtil.getTBUtil().getSysOptionBooleanValue("excel�����Ƿ񵼳�ȫ������", false);//zzl[2019-1-8 ������ֵϵͳϣ��������ǰ��ȫ������]
		int li_result = JOptionPane.YES_OPTION; //Ĭ��ֻ����ʾ������
		//if (ClientEnvironment.getInstance().isAdmin()) { //����Ա��ݵ�½
		if (flg) {
			li_result = JOptionPane.showOptionDialog(this, "��ѡ�񵼳���ʽ", "��ʾ", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "��ʾ��", "������", "ȫ������", "ȡ��" }, "��ʾ��"); //
		} else {
			li_result = JOptionPane.showOptionDialog(this, "��ѡ�񵼳���ʽ", "��ʾ", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "��ʾ��", "������", "ȡ��" }, "��ʾ��"); //
		}
		if (flg) {
			if (li_result == JOptionPane.QUESTION_MESSAGE || li_result == -1) {//zzl [2019-1-8]�޸�bug ������Żᵯ���ļ�ѡ���
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
		} else if (li_result == JOptionPane.CANCEL_OPTION) {//zzl[2019-1-7 ���Ե���ȫ�����ݵĹ���]
			try {
				String sql = this.str_realsql;//���������ӵġ�����ȫ�����ݡ�������ǰ�õ�getQuickQueryPanel().getQuerySQL(); ����������м��˲�ѯ���������ȡ�Ĳ�ѯ�����ͻ������⣬���Ż�֮�����/2019-03-14��
				if (this.str_realsql == null) {
					MessageBox.show(this,"���Ȳ�ѯ�ٵ�����");
					return;
				}
				Pub_Templet_1_ItemVO[] itemvos = getTempletItemVOs();
				StringBuffer sb = new StringBuffer();
				StringBuffer titlesb = new StringBuffer();
				for (int i = 0; i < itemvos.length; i++) {
					if (itemvos[i].getListisexport() != null && itemvos[i].getListisexport().booleanValue()) {
						sb.append(itemvos[i].getItemkey() + ",");//zzl[2019-1-7]�õ���������
						titlesb.append(itemvos[i].getItemname() + ",");//zzl[2019-1-7]�õ��������б�ͷ
					}
				}
				String[] title = titlesb.toString().substring(0, titlesb.toString().lastIndexOf(",")).split(",");
				sql = sql.replace("*", " " + sb.toString().substring(0, sb.toString().lastIndexOf(",")) + " ");
				String[][] date = UIUtil.getStringArrayByDS(null, sql);
				str_values = new String[date.length + 1][title.length];
				for (int i = 0; i < str_values.length; i++) {
					for (int j = 0; j < str_values[i].length; j++) {
						if (i == 0) {
							str_values[i][j] = title[j].toString();//zzl [2019-1-7 excel��ͷ]
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
				if (str_values != null && str_values.length < 10000) { //10000����������
					str_return = new cn.com.infostrategy.ui.report.cellcompent.ExcelUtil().setDataToExcelFile(str_values, str_fullpathname); //
				} else {
					str_return = new cn.com.infostrategy.ui.report.cellcompent.ExcelUtil().setDataToExcelFile_xlsx(str_values, str_fullpathname); //					
				}
				if (JOptionPane.showConfirmDialog(this, str_return + "\r\n���Ƿ��������򿪸��ļ�?", "��ʾ", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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
	 * ������е����ݲ�γ���Html
	 * 2009-04-14
	 */
	public void exportToHtml() {
		StringBuffer strbf_html = new StringBuffer("<html>\r\n").append("<head>\r\n") //
				//generate html content
				.append("<title>BillList����</title>\r\n")//
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
	 * excel���룬���Ը��ݵ���excel��ͷģ�嵼�����ݡ����/2018-07-26��
	 * ���������ĵ����������Ͻ����Ż���
	 * ���������ѣ���ѯ���ź���Աid������forѭ����������ֵһ��ȡ50��������Զ�̵��ã�ȥ�����У������ж��Ƿ��ǻ�������endsWith
	 * ��ǰ�ǹ���Ա��ݶ������ڱ�ͷ�һ����룬����������Ȩ��̫���ˣ��ʱ����÷������ĸ�ģ����Ҫʱ�Ӱ�ť���ü��ɡ����/2018-08-11��
	 * ͬ����ƽ̨��cn.com.infostrategy.ui.mdata.listcomp.click2.BillListPanelClick2Action.importByExcel()
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
			if (data == null || data.length == 1) {//�����ȡ�ļ�ʧ�ܻ�ֻ�и�����������ʾ
				MessageBox.show(this, "���ļ�������!");
				return;
			}
			Pub_Templet_1_ItemVO[] vos = this.getTempletItemVOs();
			HashMap<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < vos.length; i++) {
				map.put(vos[i].getItemname(), vos[i].getItemkey().toLowerCase());
			}

			HashMap deptnameMap = new HashMap();//������HashMap��Ҫ��Ϊ��ȥ�ظ�
			HashMap usernameMap = new HashMap();
			//�ֽ��������ƺ���Ա���Ʒŵ�HashMap�У�ȫ����ѯ����
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data[i].length; j++) {
					if (data[0][j].trim().endsWith("����") || data[0][j].trim().endsWith("����") || data[0][j].trim().endsWith("��λ") || data[0][j].trim().endsWith("��˾")) {//�����Ժ���Ҫ�Ż���������contains����endsWith?�����/2018-07-26��
						if (data[i][j] != null && !data[i][j].equals("")) {
							deptnameMap.put(data[i][j].trim(), data[i][j].trim());//ǰ��ȥ�ո��м�ո���ܻ����ã����ݲ�ȥ�������/2018-07-30��
						}
					} else if (data[0][j].trim().endsWith("��")) {
						if (data[i][j] != null && !data[i][j].equals("")) {
							usernameMap.put(data[i][j].trim(), data[i][j].trim());//ǰ��ȥ�ո��м�ո���ܻ����ã����ݲ�ȥ�������/2018-07-30��
						}
					}
				}

			}
			//�����޸���
			String tablename = this.getTempletVO().getSavedtablename();//�������
			String pksequencename = this.getTempletVO().getPksequencename();//����������������ģ�����������ȷ�������������ͻ
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkCommServiceIfc.class);
			Long[] ll_newIds = service.getSequenceNextValByDS(null, pksequencename, new Integer(50));//�ͻ���Ĭ��ȡ10����������������һ��ȡ50��������Զ�̵���
			int idcount = 0;
			//��������ſ��ܻ����ظ����ʽ����Ż����������Ե������·���������й�̫ƽ-̫ƽ�ٱ���-̫ƽ������ܹ�˾-�칫�ҡ����/2018-09-07��
			HashMap deptLinkMap = new HashMap();//����ȫ·����������һ��
			HashMap deptLink1Map = new HashMap();//����·������������һ��
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
			String[] headerName = tableStruct.getHeaderName();//ȡ�ñ���������У������жϱ����е��ֶβŽ��б��棬����ᱨ��
			HashMap headerMap = new HashMap();
			for (int i = 0; i < headerName.length; i++) {
				if (headerName[i] != null) {
					headerMap.put(headerName[i].toLowerCase(), headerName[i].toLowerCase());
				}
			}
			InsertSQLBuilder insert = new InsertSQLBuilder(tablename);
			ArrayList _sqllist = new ArrayList();
			for (int i = 1; i < data.length; i++) {//�ӵ�2�п�ʼ������Ĭ�ϵ�һ��Ϊ����
				boolean isnotnull = false;
				for (int j = 0; j < data[i].length; j++) {
					String colname = data[0][j].trim();
					if (headerMap.containsKey(map.get(colname))) {//���ݿ����и��ֶβŲ��뱣��
						//�ж��Ƿ�Ϊ�գ��������Ϊ�գ��򲻲���
						if (data[i][j] != null && !data[i][j].trim().equals("")) {
							String value = data[i][j].trim();
							isnotnull = true;
							if (colname.endsWith("����") || colname.endsWith("����") || colname.endsWith("��λ") || colname.endsWith("��˾")) {//�����Ժ���Ҫ�Ż���������contains����endsWith?�����/2018-07-26��
								if (deptLinkMap.containsKey(value)) {//�ȿ��Ƿ�ƥ��ȫ·�������/2018-09-07��
									insert.putFieldValue(map.get(colname), (String) deptLinkMap.get(value));
								} else if (deptLink1Map.containsKey(value)) {//�ٿ��Ƿ�ƥ��ȥ����һ���·��
									insert.putFieldValue(map.get(colname), (String) deptLink1Map.get(value));
								} else {//����Ƿ�ƥ��һ����������
									insert.putFieldValue(map.get(colname), (String) deptidMap.get(value));
								}
							} else if (colname.endsWith("��")) {
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
					System.out.println(insert.getSQL());//��ӡ������sql
				}
			}
			UIUtil.executeBatchByDS(null, _sqllist);
			this.refreshData();
			MessageBox.show(this, "����ɹ�!");
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
					//Ҫ����cell���ݵ����;������ɵ�html����
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
	 * ����������!
	 */
	public void doInsert(HashMap _defaultValue) {
		BillCardPanel cardPanel = new BillCardPanel(this.templetVO); //����һ����Ƭ���
		cardPanel.setLoaderBillFormatPanel(this.getLoaderBillFormatPanel()); //���б��BillFormatPanel�ľ��������Ƭ
		cardPanel.insertRow(); //��Ƭ����һ��!
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
		cardPanel.setEditableByInsertInit(); //���ÿ�Ƭ�༭״̬Ϊ����ʱ������
		BillCardDialog dialog = new BillCardDialog(this, this.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); //������Ƭ������
		dialog.setVisible(true); //��ʾ��Ƭ����
		if (dialog.getCloseType() == 1) { //�����ǵ��ȷ������!����Ƭ�е����ݸ����б�!
			int li_newrow = newRow(false); //
			this.setBillVOAt(li_newrow, dialog.getBillVO(), false);
			this.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //�����б���е�����Ϊ��ʼ��״̬.
			this.setSelectedRow(li_newrow); //
		}

	}

	//��ɾ������
	public void doDelete(boolean _isQuiet) {
		try {
			int li_selRow = this.getSelectedRow();
			if (li_selRow < 0) {
				MessageBox.showSelectOne(this);
				return;
			}
			if (!_isQuiet) { //������ǰ���ģʽ����ʾһ��!
				if (!MessageBox.confirmDel(this)) {
					return; //
				}
			}

			String str_tableName = this.templetVO.getSavedtablename(); //
			if (str_tableName == null || str_tableName.trim().equals("")) {
				MessageBox.show(this, "ģ���ж���ı������Ϊ��,���ܽ���ɾ������!"); //
				return;
			}
			String str_pkName = this.templetVO.getPkname(); //������
			if (str_pkName == null || str_pkName.trim().equals("")) {
				MessageBox.show(this, "�����ֶ���Ϊ��,���ܽ���ɾ������!"); //
				return;
			}

			BillVO billVO = this.getSelectedBillVO(); //
			String str_pkValue = billVO.getStringValue(this.templetVO.getPkname()); //����ֵ
			String str_sql = "delete from " + str_tableName + " where " + str_pkName + "='" + str_pkValue + "'"; //��SQL,��ʵ��ִ��ʱ�������һ��ִ��!!!

			//ɾ�������������!!!
			String[][] str_selRowValue = this.getSelectedRowValue(); //��ǰѡ���е�����!!
			if (str_selRowValue != null && str_selRowValue.length > 0) { //�����ֵ!!!
				cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc service = (cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc) UIUtil.lookUpRemoteService(cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc.class); //
				service.doCascadeDeleteSQL(str_tableName, str_selRowValue, str_sql, true); //����ɾ�����й�������!!!
			}

			//ǿ��׷�Ӽ���ɾ�� �����/2013-03-26��
			if (getTBUtil().getSysOptionBooleanValue("�б�ɾ���Ƿ����ü���ɾ��", false)) {
				UIUtil.executeBatchByDS(getDataSourceName(), getDeleteSQL(billVO));
			}

			this.removeRow(li_selRow); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
		if (all_realValueData != null && all_realValueData.length > 0) { //����У�飬����ɾ����ᱨ�� Gwang 2016-12-19
			RowNumberItemVO rowNumVO = (RowNumberItemVO) all_realValueData[0][0]; //
			li_TotalRecordCount = rowNumVO.getTotalRecordCount(); //
			System.out.println(">>>>>>>>>>ɾ��>>>>>>>>>>" + li_TotalRecordCount);
		}
	}

	//��ȡ����ɾ��sqls �����/2013-03-26��
	public ArrayList getDeleteSQL(BillVO billVO) throws Exception {
		ArrayList al_sqls = new ArrayList();
		for (int i = 0; i < this.templetItemVOs.length; i++) {
			String str_type = templetItemVOs[i].getItemtype();
			if (str_type.equals(WLTConstants.COMP_LINKCHILD)) {
				CommUCDefineVO uCDfVO = templetItemVOs[i].getUCDfVO();
				if (uCDfVO != null && uCDfVO.getConfValue("ģ�����") != null && uCDfVO.getConfValue("�����ֶ���") != null) {
					Pub_Templet_1VO templetVO_Child = UIUtil.getPub_Templet_1VO(uCDfVO.getConfValue("ģ�����"));
					String ids = billVO.getStringValue(templetItemVOs[i].getItemkey());
					if (ids != null && ids.endsWith(";")) {
						ids = ids.substring(0, ids.lastIndexOf(";")).replace(";", ",");
						String str_sql = "delete from " + templetVO_Child.getSavedtablename() + " where " + uCDfVO.getConfValue("�����ֶ���") + " in (" + ids + ")";
						al_sqls.add(str_sql);
					}
				}
			}
		}
		return al_sqls;
	}

	/**
	 * ���༭����.
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
			if (this.getSelectedRow() == -1) {//Ԭ���� 20130415��ӣ���Ҫ���ˢ��ģ��ʱ����ܳ��ֵĴ�
			} else {
				this.setBillVOAt(this.getSelectedRow(), dialog.getBillVO());
				this.setRowStatusAs(this.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			}
		}
	}

	//ʵ������
	public void sortObjs(Object[][] _objs, int _pos, boolean _isNumber, boolean _isdesc) {
		//long ll_1 = System.currentTimeMillis(); //
		if (_isNumber) {
			Arrays.sort(_objs, new BillListDataNumberComparator(_pos, _isdesc));
		} else {
			Arrays.sort(_objs, new BillListDataStrComparator(_pos, _isdesc));
		}
		//long ll_2 = System.currentTimeMillis(); //
		//logger.debug("ǰ̨�����ʱ��" + (ll_2 - ll_1) + "������");
	}

	public String replaceAll(String str_par, String old_item, String new_item) {
		String str_return = "";
		String str_remain = str_par;
		boolean bo_1 = true;
		while (bo_1) {
			int li_pos = str_remain.indexOf(old_item);
			if (li_pos < 0) {
				break;
			} // ����Ҳ���,�򷵻�
			String str_prefix = str_remain.substring(0, li_pos);
			str_return = str_return + str_prefix + new_item; // ������ַ�������ԭ��ǰ�
			str_remain = str_remain.substring(li_pos + old_item.length(), str_remain.length());
		}
		str_return = str_return + str_remain; // ��ʣ��ļ���
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
	 * У��
	 * 
	 * @return
	 */
	public boolean checkValidate() {
		return (checkIsNullValidate() && checkItemLengthValidate()); // ��ֻ����ǿ�У��,�Ժ󻹻�����У�鹫ʽ!!
	}

	/**
	 * �ǿպ;���У��,
	 * 
	 * @return
	 */
	private boolean checkIsNullValidate() {
		this.stopEditing();
		String[] str_keys = this.getTempletVO().getItemKeys(); // ���е�key
		String[] str_names = this.getTempletVO().getItemNames(); // ���е�Name
		boolean[] bo_isMustInputs = this.getTempletVO().getItemIsMustInputs(); // �Ƿ������
		int li_rowcount = getTableModel().getRowCount();
		StringBuffer showmsg = new StringBuffer();
		for (int i = 0; i < li_rowcount; i++) {
			for (int j = 0; j < str_keys.length; j++) {
				if (bo_isMustInputs[j]) { // ����Ǳ�������!!
					Object obj = getValueAt(i, str_keys[j]); //
					if (obj == null) {
						showmsg.append("��[" + (i + 1) + "]������,[" + str_names[j] + "]����Ϊ��!\r\n"); //
					} else {
						if (obj instanceof String) { // ..
							String new_name = (String) obj;
							if (new_name.trim().equals("")) {
								showmsg.append("��[" + (i + 1) + "]������,[" + str_names[j] + "]����Ϊ��!\r\n"); //
							}
						} else if (obj instanceof StringItemVO) {
							StringItemVO new_name = (StringItemVO) obj;
							if (new_name.getStringValue().trim().equals("")) {
								showmsg.append("��[" + (i + 1) + "]������,[" + str_names[j] + "]����Ϊ��!\r\n"); //
							}
						} else if (obj instanceof RefItemVO) {//��ǰֻ�ж���String��StringItemVO���󣬵��²��պ�������δ�жϣ������ӡ����/2018-11-22��
							RefItemVO new_name = (RefItemVO) obj;
							if (new_name.getId() == null || new_name.getId().trim().equals("")) {
								showmsg.append("��[" + (i + 1) + "]������,[" + str_names[j] + "]����Ϊ��!\r\n"); //
							}
						} else if (obj instanceof ComBoxItemVO) {
							ComBoxItemVO new_name = (ComBoxItemVO) obj;
							if (new_name.getId() == null || new_name.getId().trim().equals("")) {
								showmsg.append("��[" + (i + 1) + "]������,[" + str_names[j] + "]����Ϊ��!\r\n"); //
							}
						}

					}
				}
			}
		}
		String str_showmsg = showmsg.toString();
		if ("".equals(str_showmsg)) {
			boolean[] bo_isWarnInputs = this.getTempletVO().getItemIsWarnInputs(); //�Ƿ񾯸���	
			for (int i = 0; i < li_rowcount; i++) {
				for (int j = 0; j < str_keys.length; j++) {
					if (bo_isWarnInputs[j]) { // ����Ǿ�����!!
						Object obj = getValueAt(i, str_keys[j]); //
						if (obj == null) {
							showmsg.append("��[" + (i + 1) + "]������,[" + str_names[j] + "]Ϊ������!\r\n"); //
						} else {
							if (obj instanceof String) { // ..
								String new_name = (String) obj;
								if (new_name.trim().equals("")) {
									showmsg.append("��[" + (i + 1) + "]������,[" + str_names[j] + "]Ϊ������!\r\n"); //
								}
							} else if (obj instanceof StringItemVO) {
								StringItemVO new_name = (StringItemVO) obj;
								if (new_name.getStringValue().trim().equals("")) {
									showmsg.append("��[" + (i + 1) + "]������,[" + str_names[j] + "]Ϊ������!\r\n"); //
								}
							}
						}
					}
				}
			}
			if ("".equals(showmsg.toString())) {
				return true;
			}
			showmsg.append("\r\n �Ƿ�������棿");
			int option = MessageBox.showConfirmDialog(this, showmsg.toString(), "��ʾ", JOptionPane.YES_NO_OPTION);
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
	 * ����У��,
	 * @return
	 */
	private boolean checkItemLengthValidate() {
		this.stopEditing();
		String[] str_keys = this.getTempletVO().getItemKeys(); //���е�key
		String[] str_names = this.getTempletVO().getItemNames(); //���е�Name
		int[] str_lengths = this.getTempletVO().getItemLengths(); //���е�Name
		int li_rowcount = getTableModel().getRowCount();
		StringBuffer showmsg = new StringBuffer();
		String zfj = getTBUtil().getSysOptionStringValue("���ݿ��ַ���", "GBK");//��ǰֻ�ж�GBK�������UTF-8��һ������ռ�����ֽڣ���У�����ͨ�������������ݿⱨ�������øò��������/2016-04-26��
		for (int i = 0; i < li_rowcount; i++) {
			for (int j = 0; j < str_keys.length; j++) {
				Object obj = getValueAt(i, str_keys[j]); //
				boolean ifsave = getTempletItemVO(str_keys[j]).getIssave();//ֻ�в��뱣��Ĳ�������У�顾���/2015-06-18��
				if (obj != null && ifsave) {
					int length = 0;
					boolean checkSuc = true;
					if (obj instanceof StringItemVO) {
						if (str_lengths[j] != -1) {
							StringItemVO new_name = (StringItemVO) obj;
							try {
								// ������ֵ����һ��: ��һ����GBK�����ʽ���жϳ��ִ���/�쳣, ��ʹ�����з���:
								// getTBUtil().getStrUnicodeLength(String s)������UNICODE�����ʽ��ѯ�ַ������ֽڳ���
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
						showmsg.append("��[" + (i + 1) + "]������,[" + getTBUtil().getTrimSwapLineStr(str_names[j]) + "]�����ݳ��ȴ������ݿⶨ�峤��(" + str_lengths[j] + ")!\r\n"); //
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
	 * ���ͣ��ĳһ��!!
	 * 
	 * @param _row
	 * @param _itemKey
	 */
	private void focusOn(int _row, String _itemKey) {
		int li_colIndex = findColumnIndex(_itemKey); // �����һ�������ص�,�򷵻�-1
		if (li_colIndex < 0) {
			return;
		}

		this.getTable().editCellAt(_row, li_colIndex); //
		IBillCellEditor cellEditor = (IBillCellEditor) this.getTable().getCellEditor(_row, li_colIndex); // ȡ�ñ༭��!!
		JComponent obj_comp = cellEditor.getCompent(); // �Եÿؼ�!!
		if (obj_comp instanceof JTextField) { // ����ı���!!
			JTextField new_obj = (JTextField) obj_comp;
			new_obj.requestFocus();
			new_obj.requestFocusInWindow(); //
		} else if (obj_comp instanceof JComboBox) { // �����������!!
			JComboBox new_obj = (JComboBox) obj_comp;
			new_obj.requestFocus();
			new_obj.requestFocusInWindow(); //
		} else if (obj_comp instanceof JCheckBox) { // ����ǹ�ѡ��
			JCheckBox new_obj = (JCheckBox) obj_comp;
			new_obj.requestFocus();
			new_obj.requestFocusInWindow(); //
		} else if (obj_comp instanceof AbstractWLTCompentPanel) { // �����INovaCompent
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
	 * ����TMO
	 * 
	 * @param _allcolumns
	 * @return
	 */
	private AbstractTMO getTMO(String[] _allcolumns, int[] _widths, String _templetName) {
		HashVO parentVO = new HashVO(); //
		parentVO.setAttributeValue("templetcode", "Test"); // ģ�����,��������޸�
		parentVO.setAttributeValue("templetname", _templetName); // ģ�����,��������޸�
		HashVO[] childVOs = new HashVO[_allcolumns.length];
		int count = 0;
		for (int i = 0; i < childVOs.length; i++) {
			childVOs[i] = new HashVO();
			childVOs[i].setAttributeValue("itemkey", _allcolumns[i]); //
			childVOs[i].setAttributeValue("itemname", _allcolumns[i]); //
			childVOs[i].setAttributeValue("itemtype", WLTConstants.COMP_TEXTAREA); // �����ı���
			int li_width = _widths[0]; //
			if (i < _widths.length) { //���ûԽ��,���ö�Ӧλ���ϵ�
				li_width = _widths[i]; //
			}
			childVOs[i].setAttributeValue("listwidth", "" + li_width); //��ǰ��125,̫С,����!!!�Ժ�Ӧ�ø���ʵ�����ݴ�С�Զ�ƥ��! ���������һ��!!
			if (_allcolumns[i].endsWith("#")) {
				childVOs[i].setAttributeValue("listisshowable", "N"); //
			} else {
				childVOs[i].setAttributeValue("listisshowable", "Y"); //
				childVOs[i].setAttributeValue("iswrap", "Y");
			}
			childVOs[i].setAttributeValue("listiseditable", "4"); //
			childVOs[i].setAttributeValue("cardwidth", "400*60");
		}

		AbstractTMO tmo = new DefaultTMO(parentVO, childVOs); // ����Ԫԭģ������
		return tmo;
	}

	/**
	 * ȡ�ü������billListPanel��Frame,�����Ǹ��ַ��ģ��!!
	 * 
	 * @return
	 */
	public AbstractStyleWorkPanel getLoadedWrokPanel() {
		return (AbstractStyleWorkPanel) loadedWorkPanel;
	}

	/**
	 * ȡ�ü������billListPanel��Frame,�����Ǹ��ַ��ģ��!!
	 * 
	 * @return
	 */
	public AbstractWorkPanel getLoadedPanel() {
		return loadedWorkPanel;
	}

	/**
	 * ���ü������billListPanel�Ĺ������,�����Ǹ��ַ��ģ��Ĺ������!!
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
	 * �����ӱ�
	 * @param _ChildTable
	 */
	public void setLoaderChildTable(CardCPanel_ChildTable _ChildTable) {
		loaderChildTable = _ChildTable; //
	}

	public CardCPanel_ChildTable getLoaderChildTable() {
		return loaderChildTable; //
	}

	/**
	 * ȡ�ù�����
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
		btn_shqq.setVisible(_visiable); //�Ǹ�չ����ťҲҪ����
	}

	public BillQueryPanel getQuickQueryPanel() {
		if (quickQueryPanel != null) {
			return quickQueryPanel;
		}
		quickQueryPanel = new BillQueryPanel(this); // ���ٲ�ѯ���!
		quickQueryPanel.setOpaque(false); //
		unTitlePanel.add(quickQueryPanel, BorderLayout.NORTH); //���ģ�������ò���ʾ���ڳ�ʼ��Ҳ�����أ������������������Ҫ��ʾ�Ļ���Ҫ���뵽unTitlePanel����У�����޸�
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
		if (isRowNumberChecked) { //����ǹ�ѡ�е�!
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
	 * ��ͷ˫������ʱ��ʾ��ͬͼ��!!
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
			label.setOpaque(true); //��͸��
			label.setHorizontalAlignment(SwingConstants.CENTER); //����
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
				label.setToolTipText("û���ҵ���Ӧ��ItemKey"); //
			} else {
				if ("Y".equals(itemVO.getIsmustinput2())) {
					_ismustinput = true;
				}
				if (itemVO.isNeedSave() && !itemVO.isCanSave()) { //
					label.setForeground(java.awt.Color.RED); //
				} else {
					label.setForeground(LookAndFeel.table_headFontColor); //��ͷ������ɫ
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
						if (fg_bgcolors.length == 1) { //ֻ����ǰ��ɫ������ɫĬ��
							label.setForeground(getTBUtil().getColor(fg_bgcolors[0])); //����ǰ��ɫ����������ɫ
							label.setBackground(LookAndFeel.tableheaderbgcolor); //����Ĭ�ϱ���ɫ
						} else if (fg_bgcolors.length > 1) {
							label.setForeground(getTBUtil().getColor(fg_bgcolors[0])); //����ǰ��ɫ����������ɫ
							label.setBackground(getTBUtil().getColor(fg_bgcolors[1])); //���ñ���ɫ
						}
					}
					label.setFont(jtableheader.getFont());
				}
			}
			//sunfujun/20120720/�����ͷ��������
			itemVO.isViewColumn();
			String str_realText = getTBUtil().getLableRealText(str_text, ClientEnvironment.getInstance().isAdmin(), _ismustinput && isHeadShowAsterisk(), itemVO.isViewColumn(), itemVO.isNeedSave(), isHeadShowAsterisk()); //
			label.setText(str_realText); ////
			label.setToolTipText(str_tooltiptext); //
			if (_ismustinput && isHeadShowAsterisk()) {//�ʴ���Ŀ�о�������ֶζ��Ǳ������ͷ�����к��ǣ�������������ͷ��Ҫ��ʾ���ǣ������Ӵ˹��ˡ����/2012-06-18��
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

	//ԭ���������������ֱ��ȡֵ��!�����������ϵĽ���,��Ҫ��������Ķ�����ȥ��һ������! �����õ�ϰ��!
	private boolean isHeadShowAsterisk() {
		return getTBUtil().getSysOptionBooleanValue("�б��ͷ�Ƿ���ʾ��������ʾ", true); //���д��뱾����ǵ��û����,����û����������!
	}

	/**
	 * �����еı�����ͷ�Ļ�����..
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
				if (billList == null || !billList.isRowNumberChecked()) { //�����Label
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
			char[] charofs = s.toCharArray();// �鿴�ַ�����ÿ���ַ��Ƿ�������
			boolean isNum = true;
			for (int i = 0; i < charofs.length; i++) {
				if ("-0123456789.".indexOf(charofs[i]) < 0) {
					isNum = false;
					break;
				}
			}
			if (isNum) { // ��������־Ͳ���!!
				if (offset == 0 && (s.equals("."))) {
					Toolkit.getDefaultToolkit().beep(); // �Ÿ�����!!
					return; //
				} else {
					super.insertString(offset, s, attributeSet);
				}
			} else {
				Toolkit.getDefaultToolkit().beep(); // �Ÿ�����!!
				return; // ֱ�ӷ��ز�������!!
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
							tmptable.setRowHeight(i, newHeight); // �����µ��и�..
							table.setRowHeight(i, newHeight); // ���������е��и�
						}
					} else {
						tmptable.setRowHeight(resizingRow, newHeight); // �����µ��и�..
						table.setRowHeight(resizingRow, newHeight); // ���������е��и�
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
			int li_colwidth = column.getPreferredWidth(); // �еĿ��...
			String str_itemkey = (String) column.getIdentifier(); //
			int bo_islisthtmlhref = findListIsHtmlHref(str_itemkey); // �Ƿ���Html

			Object obj = tmptable.getValueAt(li_row, li_col);
			//tmptable.setToolTipText(null); //
			if (obj != null && obj.toString() != null && !obj.toString().equals("")) {
				String str_strtext = "" + obj; ////
				int li_textwidth = getTBUtil().getStrWidth(tmptable.getFont(), str_strtext) + 15; ////
				if (li_textwidth > li_colwidth) { //����ֵĿ���ѳ������п�,˵�����ֿ�������,����Ҫ����ToolTip
					int li_textlength = str_strtext.length(); //
					if (li_textlength > li_toolTipLine) { //�����������!!!
						StringBuilder sb_toolTip = new StringBuilder(); //
						for (int i = 0; i < li_textlength / li_toolTipLine; i++) {
							sb_toolTip.append(str_strtext.substring(i * li_toolTipLine, (i + 1) * li_toolTipLine) + "\n"); ////
						}
						if (li_textlength % li_toolTipLine != 0) { //�������!!����ʣ�µ�!!!
							sb_toolTip.append(str_strtext.substring((li_textlength / li_toolTipLine) * li_toolTipLine, li_textlength) + "\n"); ////
						}
						tmptable.setToolTipText(sb_toolTip.toString() + str_tableToolTip); ////
					} else { //���С��15������ֱ����ʾһ��
						tmptable.setToolTipText(str_strtext + "\n" + str_tableToolTip); //
					}
				} else {
					tmptable.setToolTipText(str_tableToolTip); //
				}
			} else {
				tmptable.setToolTipText(str_tableToolTip); //
			}

			if (bo_islisthtmlhref < 0) { // �������HtmlHref,����������
				tmptable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				return;
			}

			if (obj == null || obj.toString().equals("")) {
				tmptable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); // ���ֵΪ��,����������...
				return;
			}

			Rectangle reg = tmptable.getCellRect(li_row, li_col, true);
			String str_value = obj.toString(); //

			int li_length = SwingUtilities.computeStringWidth(Toolkit.getDefaultToolkit().getFontMetrics(tmptable.getFont()), str_value); // ȡ���ֳ�
			if ((point.getX() - reg.getX()) > li_length || (point.getX() - reg.getX()) > li_colwidth) {
				tmptable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); //
			} else {
				if (tmptable.getCursor().getType() != Cursor.WAIT_CURSOR) {
					tmptable.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); //
				}
			}
		}

		public void mouseClicked(MouseEvent e) {
			if (tmptable.getCursor().getType() == Cursor.HAND_CURSOR) { // ���ȱ���������
				if (e.getClickCount() == 1 && e.getButton() == e.BUTTON1) {
					tmptable.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)); // ����ΪĬ��
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

		private int getUnicodeLength(String s) // ���������ָ����һ���ַ���,������ֽڳ���,����ַ������и������ַ�,��ô���ĳ��Ⱦ���2
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

	//��ȡCombineTable �����/2013-07-23�� 
	public CombineTable getCombineTable() {
		return (CombineTable) getTable();
	}

	//���úϲ��� �����/2013-07-23�� 
	public void setCombineColumns(ArrayList<Integer> combineCols) {
		this.combineColumns = combineCols;
		getCombineTable().setCombineColumns(getTableModel(), combineColumns, 2);
		if (combineColumns != null) {
			getCombineTable().setCellSelectionEnabled(true); //��Ԫ��ѡ�� �����/2013-07-26�� 
		}
	}

	//�ϲ�ˢ�� �����/2013-07-23�� 
	public void refreshCombineData() {
		if (bo_tableislockcolumn) { //���� �����/2013-07-23�� 
			refreshCombineData_header(lock_pos);
			return;
		}

		lock_pos = -1;
		if (combine_mark && combineColumns != null && getTableModel().getRealValueAtModel() != null) {
			getCombineTable().setCombineColumns(getTableModel(), combineColumns, 2);
		}
	}

	//���� �ϲ�ˢ�� �����/2013-07-23�� 
	public void refreshCombineData_header(int pos) {
		if (combine_mark && combineColumns != null && getTableModel().getRealValueAtModel() != null) {
			ArrayList<Integer> tableCombineColumns = new ArrayList<Integer>();
			ArrayList<Integer> headerCombineColumns = new ArrayList<Integer>();
			for (Integer column : combineColumns) {
				if (column <= pos) {
					headerCombineColumns.add(column + 1);
				} else {
					tableCombineColumns.add(column - pos - 1); //������
				}
			}

			getCombineTable().setCombineColumns(getTableModel(), tableCombineColumns, 2 + pos + 1);
			((CombineTable) rowHeaderTable).setCombineColumns(getTableModel(), headerCombineColumns, 1); //header
			lock_pos = pos;
		}
	}

	class HtmlHrefTable extends CombineTable { //�̳�CombineTable �ϲ� �����/2013-07-23�� 
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
			if (combineColumns != null) { //ͬ������� �����/2013-07-26�� 
				int span = this.combineData.span(row, column);
				rowHeaderTable.setRowSelectionInterval(row, row + span - 1);
				rowHeaderTable.repaint();
			}
		}

	}

	class ResizableTable extends CombineTable { //�̳�CombineTable �ϲ� �����/2013-07-23�� 
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
			if (combineColumns != null) { //ͬ����� �����/2013-07-26�� 
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