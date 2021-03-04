package cn.com.infostrategy.ui.workflow.design;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.jdesktop.jdic.desktop.Desktop;
import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellHandle;
import org.jgraph.graph.ConnectionSet;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.Edge;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphUndoManager;
import org.jgraph.graph.ParentMap;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortView;
import org.jgraph.graph.DefaultGraphModel.GraphModelEdit;
import org.jgraph.plaf.basic.BasicGraphUI;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.workflow.design.ActivityBean;
import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.to.workflow.design.GroupVO;
import cn.com.infostrategy.to.workflow.design.ProcessBean;
import cn.com.infostrategy.to.workflow.design.ProcessVO;
import cn.com.infostrategy.to.workflow.design.RiskVO;
import cn.com.infostrategy.to.workflow.design.TransitionBean;
import cn.com.infostrategy.to.workflow.design.TransitionVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPropEditEvent;
import cn.com.infostrategy.ui.mdata.BillPropEditListener;
import cn.com.infostrategy.ui.mdata.BillPropPanel;
import cn.com.infostrategy.ui.workflow.WorkFlowServiceIfc;

/**
 * 工作流设计器主界面!关键类
 * 
 * @author xch
 * 
 */
public class WorkFlowDesignWPanel extends JPanel implements GraphSelectionListener, KeyListener, BillPropEditListener {

	private static final long serialVersionUID = -7539768612311088686L;

	private JPanel toolBarPanel = null; //上面的工具条
	private JButton btn_new, btn_open, btn_save; //

	private JPanel mainGraphPanel = null;//
	private JPanel panel_west = null;
	private JToolBar toolBar2 = null;

	private JGraph graph = null; //
	private DefaultGraphCell titleCell;
	private boolean showtoolbar = true; //是否显示工具条
	private boolean showtoolbox = true; //是否显示工具箱,以前都是用的showtoolbar，但逻辑上是显示工具箱，有些歧义，所以新增此参数
	private MyGraphUI myGraphUI = null;

	private GraphUndoManager undoManager = null; // 后退

	private int cellNumber = 0; // 记录一共增加了几个结点.	
	private WLTButton btn_processprop = null;
	private WLTButton btn_toolbox1; // btn_toolbox2, btn_toolbox3 = null;	//工具箱,流程分析,风险分析

	private JSplitPane splitPanel = null;
	private Vector vector_cellSelectedListener = new Vector(); // 选择发生变化事件
	private Vector vector_celldoubleclickListener = new Vector(); //

	protected CardLayout cardlayout_1 = null; // 卡片布局,非常关键!
	private JPanel toftPanel_1 = null; // 卡片布局的底层面板!!!
	private BillPropPanel processPropPanel = null; // 显示流程属性的面板!!
	private Color deptGroupBgcolor = new Color(232, 238, 247); //200, 214, 234
	private Color linecolor = new Color(70, 119, 191); //
	private Color startCellBgcolor = new Color(225, 255, 225); //
	private Color endCellBgcolor = new Color(225, 255, 225);
	private Color normalCellBgcolor = new Color(232, 238, 247);

	protected CardLayout cardlayout_2 = null; // 卡片布局,非常关键!
	private JPanel toftPanel_2 = null; // 卡片布局的底层面板!!!
	private BillPropPanel activityBillPropPanel = null; // 环节属性面板
	private BillPropPanel transitionBillPropPanel = null; // 连线属性面板

	// private BillPropPanel processAnalysisBillPropPanel = null; //流程分析
	// private BillPropPanel riskAnalysisBillPropPanel = null; //风险分析

	private JButton btn_adddept, btn_addstation, btn_insert_0, btn_insert_1, btn_insert_2, btn_insert_3, btn_insert_4, btn_insert_5, btn_insert_6, btn_insert_7, btn_insert_8, btn_samex, btn_samey, btn_samew, btn_sameh, btn_zoom, btn_zoomin, btn_zoomout, btn_undo, btn_redo, btn_delete, btn_help, btn_importbackimg;
	private JCheckBox checkBox_lockgroup, checkBox_showorder = null;//sunfujun/20120621/因为有些时候生成体系文件排序老有问题，增加一个按钮可以快速查看每个环节的排序_wg
	private ProcessVO currentProcessVO = null; // 当前流程VO..

	//工作流标尺 【杨科/2012-11-12】
	private JScrollPane staffScrollPanel = null;
	private JViewport staffXViewport, staffYViewport = null;
	private DefaultGraphCell dlineCell;

	// private Vector v_deptgroups = new Vector(); //部门集合..
	// private Vector v_stationgroups = new Vector(); //阶段集合..

	private int groupcount = 0; //
	private int stationcount = 0; //
	public int li_opentype = 0;

	private DefaultGraphCell mouseRightClickedCell; // 鼠标右键点击时选中的菜单
	private DefaultGraphCell stationBackGroundCell = null; //阶段的背景环节!!

	private boolean isEditChanged = false; //判断内容是否变化

	private String processId = "";
	private String processCode = "";
	private String processName = "";
	private String sr_type = "";

	private boolean isDragging = false; //是否处于正在拖在的过程中....
	private Point draggingPoint = null; //
	private ArrayList al_draggingPortXY = new ArrayList(); //

	private JToolBar toolBar = new JToolBar("abc", JToolBar.HORIZONTAL); // 工具条
	private Vector v_cellDeleteListeners = new Vector(); // 删除流程对象事件

	private Object[] copyCach = null;//复制
	private ArrayList addActivityIds = new ArrayList();//新增的环节，如果点击保存按钮了，将清空
	private boolean iscopy = false;
	private boolean showpreviewdept = true;//因为农行一图两表中没有模板主表子表、下拉框注册字典、平台参数配置表等，所以在本类中增加了个属性，根据属性来判断是否显示预设部门
	private int stationheight = 10;//农行提出，每次新增阶段的间隔要可以设置
	private boolean isCanSetCellColor = true;//农行提出，编辑部门阶段及环节时不要出现字体颜色和背景颜色，防止流程图被编辑的花里胡哨的，故设置此参数
	private String groupName = "部门";//纵向矩阵名称，有的客户叫部门，有的叫职能（农行），故设置此参数
	private boolean undoRedoVisible = true;//撤销及恢复按钮是否可见，因撤销按钮存在bug，所以建议可设置隐藏该按钮
	private String helpInfo = null;//客户觉得说明不太明白，需要自定义一些帮助信息或注意信息
	private boolean gridVisible = true;//背景格子是否可见，默认可见
	private boolean isHaveLoaded = false;//流程图加载完之后才进行undo事件的收集
	private int lineType[] = new int[] { 2, 1, 4, 5, 7, 8, 9, 0 }; //定义线的顶端类型，为了和编辑线中下拉框一致。故弄这么个顺序。具体的数字含义查看GraphConstants中的常量（ARROW开头的）

	private TBUtil tbUtil = null; //

	/**
	 * 默认页面
	 */
	public WorkFlowDesignWPanel() {
		li_opentype = 1;
		init(); // 初始化页面
	}

	public WorkFlowDesignWPanel(boolean _showtoolbox) {
		li_opentype = 2;
		showtoolbox = _showtoolbox; //
		init(); // 初始化页面
	}

	/**
	 * 查看一个流程模板!!
	 * 
	 * @param _processId
	 */
	public WorkFlowDesignWPanel(String _processCode) {
		li_opentype = 2;
		init(); // 初始化页面
		loadGraphByCode(_processCode); //
	}

	/**
	 * 查看一个流程模板!!
	 * 
	 * @param _processId
	 */
	public WorkFlowDesignWPanel(String _processCode, boolean _showtoolbox) {
		li_opentype = 2;
		showtoolbox = _showtoolbox; //

		init(); // 初始化页面
		loadGraphByCode(_processCode); //
	}

	public WorkFlowDesignWPanel(boolean _showtoolbox, String _processId, String _processCode, String _processName, String _type) {

		processId = _processId;
		processCode = _processCode;
		processName = _processName;
		li_opentype = 2;
		showtoolbox = _showtoolbox; //
		sr_type = _type;
		init(); // 初始化页面
	}

	/**
	 * 初始化页面!!即布局
	 */
	public void init() {
		this.setLayout(new BorderLayout()); // 设置布局类!
		if (showtoolbox) {//判断是否显示工具箱，如果显示的话，需要加上左边的流程属性和右边工具箱（环节属性和连线属性）
			if (this.showtoolbar) {//判断是否显示上面的工具条
				this.add(getNorthToolBar(), BorderLayout.NORTH); // 上方是工具条
			}
			this.add(getWestPanel(), BorderLayout.WEST); // 左边是流程属性
			splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			splitPanel.setDividerLocation(9999); //
			splitPanel.setDividerSize(5);
			splitPanel.setResizeWeight(1D); // 屏幕变大时,右边的大小不变!
			splitPanel.setLeftComponent(getMainGraphPanel()); //
			splitPanel.setRightComponent(getEastPanel()); //
			this.add(splitPanel, BorderLayout.CENTER); // 中间是Graph编辑器
			this.add(getEastToolBox(), BorderLayout.EAST); // 右边是环节属性和连线属性的工具箱
		} else {
			if (this.showtoolbar) {//判断是否显示上面的工具条
				this.add(getNorthToolBar(), BorderLayout.NORTH);
			}
			this.add(getMainGraphPanel(), BorderLayout.CENTER); //			
		}
		setGridVisible(true);//设置背景格子可见
	}

	public void loadGraphByProcessVO(ProcessVO _vo) {
		currentProcessVO = _vo; //
		if (currentProcessVO != null) {
			openMainGraph(currentProcessVO, 2); // 根据一个对象VO,打开一个图形!!
		}
	}

	/**
	 * 加载图形
	 * 
	 * @param _code
	 */
	public void loadGraphByCode(String _code) {
		try {
			WorkFlowServiceIfc wfservice = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class); //
			currentProcessVO = wfservice.getWFProcessByWFCode(_code); //
			if (currentProcessVO != null) {
				openMainGraph(currentProcessVO); // 根据一个对象VO,打开一个图形!!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void loadGraphByID(String _processid) {
		loadGraphByID(null, _processid);
	}

	public void loadGraphByID(String _datasourcename, String _processid) {
		loadGraphByID(_datasourcename, _processid, 1);
	}

	public void loadGraphByID(String _datasourcename, String _processid, int _type) {
		try {
			if (_processid == null) {
				return;
			}
			WorkFlowServiceIfc wfservice = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class); //
			currentProcessVO = wfservice.getWFProcessByWFID(_datasourcename, _processid); //
			if (currentProcessVO != null) {
				openMainGraph(currentProcessVO, _type); // 根据一个对象VO,打开一个图形!!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void loadHistoryGraphByID(String _processid) {
		try {
			WorkFlowServiceIfc wfservice = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class); //
			currentProcessVO = wfservice.getHistoryWFProcessByWFID(_processid); //
			if (currentProcessVO != null) {
				openHistoryMainGraph(currentProcessVO); // 根据一个对象VO,打开一个图形!!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void setToolBarVisiable(boolean _visiable) {
		this.setShowtoolbar(_visiable);
	}

	//点击工具条上的按钮!!!
	private void onClickToolBarBtn(ActionEvent _event) {
		if (_event.getSource() == btn_new) {
			onCreateWfProcess();
		} else if (_event.getSource() == btn_open) {
			onOpenWfProcess();
		} else if (_event.getSource() == btn_save) {
			onSaveWfProcess();
		} else if (_event.getSource() == btn_adddept) {
			onAddDept();
		} else if (_event.getSource() == btn_addstation) {
			onAddstation();
		} else if (_event.getSource() == btn_insert_0) {
			onAddDeptAndPost(); //增加机构与岗位
		} else if (_event.getSource() == btn_zoom) {
			onZoom();
		} else if (_event.getSource() == btn_zoomin) {
			onZoomin();
		} else if (_event.getSource() == btn_zoomout) {
			onZoomout();
		} else if (_event.getSource() == btn_undo) {
			onUndo();
		} else if (_event.getSource() == btn_redo) {
			onRedo();
		} else if (_event.getSource() == btn_delete) {
			onDelCell();
		} else if (_event.getSource() == btn_help) {
			onShowHelpInfo();
		} else if (_event.getSource() == btn_samex) {
			cellSameX();
		} else if (_event.getSource() == btn_samey) {
			cellSameY();
		} else if (_event.getSource() == btn_samew) {
			cellSameW();
		} else if (_event.getSource() == btn_sameh) {
			cellSameH();
		} else if (_event.getSource() == btn_importbackimg) {
			onImportBackImg();
		}
	}

	/**
	 * 上方的工具条
	 * 
	 * @return
	 */
	private JPanel getNorthToolBar() {
		if (toolBarPanel != null) {
			return toolBarPanel;
		}
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
		toolBar.setFloatable(false); //
		toolBar.setRollover(true); //
		toolBar.setBorderPainted(true); //

		btn_new = new JButton(new ToolBarBtnAction("", UIUtil.getImage("workflow/new.gif"))); //
		btn_open = new JButton(new ToolBarBtnAction("", UIUtil.getImage("workflow/open.gif"))); //
		btn_save = new JButton(new ToolBarBtnAction("", UIUtil.getImage("workflow/save.gif"))); //
		btn_new.setToolTipText("新建流程");
		btn_open.setToolTipText("打开流程");
		btn_save.setToolTipText("保存");

		toolBar.add(btn_new); //
		toolBar.add(btn_open); //
		toolBar.add(btn_save); //

		toolBar.addSeparator(new Dimension(10, 10)); //

		btn_adddept = toolBar.add(new ToolBarBtnAction("AddDept", UIUtil.getImage("workflow/add_dept.gif"))); //  UIUtil.getImage("workflow/add_down.gif")));  //增加部门
		btn_addstation = toolBar.add(new ToolBarBtnAction("AddStation", UIUtil.getImage("workflow/add_station.gif"))); //UIUtil.getImage("workflow/connecton.gif"))); // 增加阶段
		btn_adddept.setToolTipText("增加" + groupName);
		btn_addstation.setToolTipText("增加阶段");

		btn_insert_0 = toolBar.add(new ToolBarBtnAction("机构岗位", UIUtil.getImage("workflow/add_dept_post.gif")));// UIUtil.getImage("workflow/spli2.gif"))); // 增加部门
		btn_insert_0.setToolTipText("机构岗位"); //

		btn_insert_1 = new MyDraggableButton(UIUtil.getImage("workflow/cellview_1.gif"));
		btn_insert_1.setPreferredSize(new Dimension(20, 20)); //
		btn_insert_1.setToolTipText("环节"); //

		btn_insert_2 = new MyDraggableButton(UIUtil.getImage("workflow/cellview_2.gif"));
		btn_insert_2.setPreferredSize(new Dimension(20, 20)); //
		btn_insert_2.setToolTipText("进程"); //

		btn_insert_3 = new MyDraggableButton(UIUtil.getImage("workflow/cellview_3.gif"));
		btn_insert_3.setPreferredSize(new Dimension(20, 20)); //
		btn_insert_3.setToolTipText("判断"); //

		btn_insert_4 = new MyDraggableButton(UIUtil.getImage("workflow/cellview_4.gif"));
		btn_insert_4.setPreferredSize(new Dimension(20, 20)); //
		btn_insert_4.setToolTipText("标注"); //

		btn_insert_5 = new MyDraggableButton(UIUtil.getImage("workflow/cellview_5.gif"));
		btn_insert_5.setPreferredSize(new Dimension(20, 20)); //
		btn_insert_5.setToolTipText("特殊环节"); //

		btn_insert_6 = new MyDraggableButton(UIUtil.getImage("workflow/cellview_6.gif"));
		btn_insert_6.setPreferredSize(new Dimension(20, 20)); //
		btn_insert_6.setToolTipText("数据"); //

		btn_insert_7 = new MyDraggableButton(UIUtil.getImage("workflow/cellview_7.gif"));
		btn_insert_7.setPreferredSize(new Dimension(20, 20)); //
		btn_insert_7.setToolTipText("文档"); //

		btn_insert_8 = new MyDraggableButton(UIUtil.getImage("pic2.gif"));
		btn_insert_8.setPreferredSize(new Dimension(20, 20)); //
		btn_insert_8.setToolTipText("图片环节"); //

		btn_adddept.setFocusPainted(false);//去掉按钮点击后图片上显示的框框
		btn_addstation.setFocusPainted(false);
		btn_insert_0.setFocusPainted(false);
		btn_insert_1.setFocusPainted(false);
		btn_insert_2.setFocusPainted(false);
		btn_insert_3.setFocusPainted(false);
		btn_insert_4.setFocusPainted(false);
		btn_insert_5.setFocusPainted(false);
		btn_insert_6.setFocusPainted(false);
		btn_insert_7.setFocusPainted(false);
		btn_insert_8.setFocusPainted(false);

		toolBar.add(btn_insert_0); //
		toolBar.add(btn_insert_1); //
		toolBar.add(btn_insert_2); //
		toolBar.add(btn_insert_3); //
		toolBar.add(btn_insert_4); //
		toolBar.add(btn_insert_5); //
		toolBar.add(btn_insert_6); //
		toolBar.add(btn_insert_7); //
		toolBar.add(btn_insert_8); //

		btn_samex = toolBar.add(new ToolBarBtnAction("samex", UIUtil.getImage("workflow/align_left.gif"))); //
		btn_samey = toolBar.add(new ToolBarBtnAction("samey", UIUtil.getImage("workflow/valign_top.gif"))); //
		btn_samew = toolBar.add(new ToolBarBtnAction("samew", UIUtil.getImage("workflow/same_width.gif"))); //
		btn_sameh = toolBar.add(new ToolBarBtnAction("sameh", UIUtil.getImage("workflow/same_height.gif"))); //
		btn_zoom = toolBar.add(new ToolBarBtnAction("Zoom", UIUtil.getImage("workflow/zoom.gif"))); //
		btn_zoomin = toolBar.add(new ToolBarBtnAction("Zoomin", UIUtil.getImage("workflow/zoomin.gif"))); //
		btn_zoomout = toolBar.add(new ToolBarBtnAction("Zoomout", UIUtil.getImage("workflow/zoomout.gif"))); //
		btn_undo = toolBar.add(new ToolBarBtnAction("Undo", UIUtil.getImage("workflow/undo.gif"))); //
		btn_redo = toolBar.add(new ToolBarBtnAction("Redo", UIUtil.getImage("workflow/redo.gif"))); //
		btn_delete = toolBar.add(new ToolBarBtnAction("Delete", UIUtil.getImage("workflow/delete.gif")));
		btn_help = toolBar.add(new ToolBarBtnAction("Help", UIUtil.getImage("workflow/help.gif")));
		btn_importbackimg = toolBar.add(new ToolBarBtnAction("importbackimg", UIUtil.getImage("zt_049.gif")));
		btn_samex.setToolTipText("纵向对齐");
		btn_samey.setToolTipText("横向对齐");
		btn_samew.setToolTipText("同长度");
		btn_sameh.setToolTipText("同高度");
		btn_zoom.setToolTipText("恢复大小");
		btn_zoomin.setToolTipText("放大");
		btn_zoomout.setToolTipText("缩小");
		btn_undo.setToolTipText("撤销");
		btn_redo.setToolTipText("恢复");
		btn_delete.setToolTipText("删除");
		btn_help.setToolTipText("帮助说明");
		btn_importbackimg.setToolTipText("上传背景图片");

		btn_samex.setFocusPainted(false);
		btn_samey.setFocusPainted(false);
		btn_samew.setFocusPainted(false);
		btn_sameh.setFocusPainted(false);
		btn_zoom.setFocusPainted(false);
		btn_zoomin.setFocusPainted(false);
		btn_zoomout.setFocusPainted(false);
		btn_undo.setFocusPainted(false);
		btn_redo.setFocusPainted(false);
		btn_delete.setFocusPainted(false);
		btn_help.setFocusPainted(false);
		btn_importbackimg.setFocusPainted(false);
		checkBox_lockgroup = new JCheckBox("锁定底层", false); //
		checkBox_lockgroup.setOpaque(false); //透明!以前感觉一下有点突出
		checkBox_lockgroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onLockGroup(); //
			}
		});
		toolBar.add(checkBox_lockgroup);
		checkBox_showorder = new JCheckBox("显示排序", false);
		checkBox_showorder.setOpaque(false);
		checkBox_showorder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onShowOrder();
			}
		});
		toolBar.add(checkBox_showorder);

		JLabel l1 = new JLabel();
		l1.setText("Ο");
		JButton flow_copy = new JButton("流程导入");
		flow_copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCopyFlow(1);
			}
		});
		if (sr_type.equals("管理规定") || sr_type.equals("程序文件")) {
			toolBar.add(l1);
			toolBar.add(flow_copy); //
			//toolBar.add(flow_copy2); //
		}

		// btn_start.setEnabled(false);
		// btn_end.setEnabled(false);
		btn_adddept.setEnabled(false); // 增加部门.
		btn_addstation.setEnabled(false); // 增加阶段.

		btn_insert_0.setEnabled(false);
		btn_insert_1.setEnabled(false);
		btn_insert_2.setEnabled(false);
		btn_insert_3.setEnabled(false);
		btn_insert_4.setEnabled(false);
		btn_insert_5.setEnabled(false);
		btn_insert_6.setEnabled(false);
		btn_insert_7.setEnabled(false);
		btn_insert_8.setEnabled(false);

		btn_samex.setEnabled(false);
		btn_samey.setEnabled(false);
		btn_samew.setEnabled(false);
		btn_sameh.setEnabled(false);
		btn_zoom.setEnabled(false);
		btn_zoomin.setEnabled(false);
		btn_zoomout.setEnabled(false);
		btn_undo.setEnabled(false);
		btn_redo.setEnabled(false); //
		btn_delete.setEnabled(false); //

		JScrollPane scrollPanel = new JScrollPane(toolBar); //
		scrollPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER); //
		toolBarPanel = new JPanel(new BorderLayout()); //
		toolBarPanel.add(scrollPanel); //
		return toolBarPanel;
	}

	public JToolBar getWFPanelToolBar() {
		return toolBar;
	}

	/**
	 * 
	 * 复制流程功能
	 */
	protected void onCopyFlow(int type) {
		HashVO hvo = new HashVO(); //
		hvo.setAttributeValue("flowid", processId);
		hvo.setAttributeValue("flowcode", processCode);
		hvo.setAttributeValue("flowname", processName);
		hvo.setAttributeValue("type", sr_type);
		Dialog_FlowCopy dialog = new Dialog_FlowCopy(this, "流程导入", 1024, 600, hvo, 1);
		dialog.setVisible(true);
		if (dialog.getCloseType() == 1) {
			loadGraphByCode(processCode); //
		}
	}

	public void setAllBtnEnable() {
		setToolBarVisiable(true);

		btn_save.setEnabled(true); // 保存
		btn_new.setVisible(false);
		btn_open.setVisible(false); //

		btn_adddept.setEnabled(true); // 增加部门.
		btn_addstation.setEnabled(true); // 增加阶段.

		btn_insert_0.setEnabled(true);
		btn_insert_1.setEnabled(true);
		btn_insert_2.setEnabled(true);
		btn_insert_3.setEnabled(true);
		btn_insert_4.setEnabled(true);
		btn_insert_5.setEnabled(true);
		btn_insert_6.setEnabled(true);
		btn_insert_7.setEnabled(true);
		btn_insert_8.setEnabled(true);

		btn_samex.setEnabled(true);
		btn_samey.setEnabled(true);
		btn_samew.setEnabled(true);
		btn_sameh.setEnabled(true);
		btn_zoom.setEnabled(true);
		btn_zoomin.setEnabled(true);
		btn_zoomout.setEnabled(true);
		btn_undo.setEnabled(true);
		btn_redo.setEnabled(true); //
		btn_delete.setEnabled(true); //
	}

	public void setAllBtnDisable() {
		if (toolBarPanel == null) {//如果本来就不显示工具条则直接返回
			return;
		}

		btn_save.setEnabled(false); // 保存
		btn_new.setVisible(false);
		btn_open.setVisible(false); //

		btn_adddept.setEnabled(false); // 增加部门.
		btn_addstation.setEnabled(false); // 增加阶段.

		btn_insert_0.setEnabled(false);
		btn_insert_1.setEnabled(false);
		btn_insert_2.setEnabled(false);
		btn_insert_3.setEnabled(false);
		btn_insert_4.setEnabled(false);
		btn_insert_5.setEnabled(false);
		btn_insert_6.setEnabled(false);
		btn_insert_7.setEnabled(false);
		btn_insert_8.setEnabled(false);

		btn_samex.setEnabled(false);
		btn_samey.setEnabled(false);
		btn_samew.setEnabled(false);
		btn_sameh.setEnabled(false);
		btn_zoom.setEnabled(false);
		btn_zoomin.setEnabled(false);
		btn_zoomout.setEnabled(false);
		btn_undo.setEnabled(false);
		btn_redo.setEnabled(false); //
		btn_delete.setEnabled(false); //
	}

	private JPanel getMainGraphPanel() {
		if (mainGraphPanel != null) {
			return mainGraphPanel;
		}
		// 创建Graph对象,非常关键!
		graph = new MyGraph(new MyModel());
		graph.getGraphLayoutCache().setFactory(new GPCellViewFactory()); // //

		graph.setMarqueeHandler(new MyMarqueeHandler());
		graph.getSelectionModel().addGraphSelectionListener(this);
		graph.addKeyListener(this); //
		graph.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == e.BUTTON3) { // 如果点击的是右键
					showPopMenu(e.getX(), e.getY()); //
				} else if (e.getButton() == e.BUTTON1 && e.getClickCount() == 2) { //
					onDoubleClickCell(e.getX(), e.getY()); //
				}
			}
		}); //

		graph.setFont(LookAndFeel.font); //
		graph.setBackground(Color.WHITE); //
		myGraphUI = new MyGraphUI(); // 
		graph.setUI(myGraphUI); //

		undoManager = new GraphUndoManager() {
			private static final long serialVersionUID = 2625332172251917315L; //

			public void undoableEditHappened(UndoableEditEvent e) {
				if (undoRedoVisible && isHaveLoaded) {
					if (e.getEdit() instanceof GraphModelEdit) {//只将模型变化的行为作为undo的对象同时阶段背景的变化不纳入undo的范围【sunfujun,20120411,修正undo的bug——王宁】
						try {
							Object[] a = ((GraphModelEdit) e.getEdit()).getInserted();
							if (a == null) {
								a = ((GraphModelEdit) e.getEdit()).getRemoved();
							}
							if (a != null && a.length > 0) {
								if (((DefaultGraphCell) a[0]).getUserObject() instanceof GroupVO && "STATIONBACK".equals(((GroupVO) ((DefaultGraphCell) a[0]).getUserObject()).getGrouptype())) {
								} else {
									super.undoableEditHappened(e);
								}
							} else {
								Map attrs = ((GraphModelEdit) e.getEdit()).getAttributes();
								if (attrs != null && attrs.size() > 0) {
									a = ((DefaultGraphCell[]) attrs.keySet().toArray(new DefaultGraphCell[0]));
									if (attrs.size() == 1 && ((DefaultGraphCell) a[0]).getUserObject() instanceof GroupVO && "DEPT".equals(((GroupVO) ((DefaultGraphCell) a[0]).getUserObject()).getGrouptype())) {
										if (getDeptCells().length == 1) {
											super.undoableEditHappened(e);
										}
									} else {
										super.undoableEditHappened(e);
									}
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							super.undoableEditHappened(e);
						}
					}
				}
			}
		};
		graph.getModel().addUndoableEditListener(undoManager);

		//工作流标尺 X轴 Y轴 XY轴监听器 【杨科/2012-11-12】
		StaffXPanel staffXPanel = new StaffXPanel();
		staffXPanel.setPreferredSize(new Dimension(2000, 15));
		StaffYPanel staffYPanel = new StaffYPanel();
		staffYPanel.setPreferredSize(new Dimension(20, 2000));

		staffXPanel.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {

			}

			public void mouseEntered(MouseEvent e) {

			}

			public void mouseExited(MouseEvent e) {

			}

			public void mousePressed(MouseEvent e) {
				addDLine(e.getX(), 0, 1, 2000);
			}

			public void mouseReleased(MouseEvent e) {
				delDLine();
			}

		});

		staffXPanel.addMouseMotionListener(new MouseMotionListener() {

			public void mouseDragged(MouseEvent e) {
				//delDLine();
				//addDLine(e.getX(), 0, 1, 2000);

				GraphConstants.setBounds(dlineCell.getAttributes(), new Rectangle2D.Double(e.getX(), 0, 1, 2000));
				getGraph().getGraphLayoutCache().toFront(new Object[] { dlineCell });
				//graph.getGraphLayoutCache().insert(dlineCell);
			}

			public void mouseMoved(MouseEvent e) {

			}

		});

		staffYPanel.addMouseListener(new MouseListener() {

			public void mouseClicked(MouseEvent e) {

			}

			public void mouseEntered(MouseEvent e) {

			}

			public void mouseExited(MouseEvent e) {

			}

			public void mousePressed(MouseEvent e) {
				addDLine(0, e.getY(), 2000, 1);
			}

			public void mouseReleased(MouseEvent e) {
				delDLine();
			}

		});

		staffYPanel.addMouseMotionListener(new MouseMotionListener() {

			public void mouseDragged(MouseEvent e) {
				//delDLine();
				//addDLine(0, e.getY(), 2000, 1);

				GraphConstants.setBounds(dlineCell.getAttributes(), new Rectangle2D.Double(0, e.getY(), 2000, 1));
				getGraph().getGraphLayoutCache().toFront(new Object[] { dlineCell });
				//graph.getGraphLayoutCache().insert(dlineCell);
			}

			public void mouseMoved(MouseEvent e) {

			}

		});

		//工作流标尺 【杨科/2012-11-12】
		staffScrollPanel = new JScrollPane(graph);
		staffScrollPanel.setBorder(BorderFactory.createEmptyBorder());
		staffXViewport = new JViewport();
		staffXViewport.setView(staffXPanel);
		staffYViewport = new JViewport();
		staffYViewport.setView(staffYPanel);

		mainGraphPanel = new JPanel(); // 右边的面板,包括弹出式属性面板与坚着的按钮栏!!!
		mainGraphPanel.setLayout(new BorderLayout()); //
		mainGraphPanel.add(staffScrollPanel);
		return mainGraphPanel; //
	}

	//工作流标尺 添加虚线 【杨科/2012-11-12】
	public void addDLine(int x, int y, int w, int h) {
		dlineCell = getCell("XY", Color.BLACK, "cn.com.infostrategy.ui.workflow.design.CellView_DLine");

		GraphConstants.setBounds(dlineCell.getAttributes(), new Rectangle2D.Double(x, y, w, h));
		GraphConstants.setHorizontalAlignment(dlineCell.getAttributes(), SwingConstants.LEFT);
		GraphConstants.setSelectable(dlineCell.getAttributes(), false);
		GraphConstants.setMoveable(dlineCell.getAttributes(), true);
		GraphConstants.setBorder(dlineCell.getAttributes(), BorderFactory.createEmptyBorder());

		graph.getGraphLayoutCache().insert(dlineCell);
	}

	//工作流标尺 删除虚线 【杨科/2012-11-12】
	public void delDLine() {
		graph.getModel().remove(new Object[] { dlineCell });
	}

	//工作流标尺是否显示 true显示 false不显示 【杨科/2012-11-12】
	public void showStaff(boolean isShow) {
		if (isShow) {
			staffScrollPanel.setColumnHeader(staffXViewport);
			staffScrollPanel.setRowHeader(staffYViewport);
		} else {
			staffScrollPanel.setColumnHeader(null);
			staffScrollPanel.setRowHeader(null);
		}
	}

	private JPanel getWestPanel() {
		if (panel_west != null) {
			return panel_west;
		}
		panel_west = new JPanel(); // 右边的面板,包括弹出式属性面板与坚着的按钮栏!!!
		panel_west.setLayout(new BorderLayout()); //

		btn_processprop = new WLTButton(UIUtil.getImage("processprop.gif"));
		btn_processprop.setBackground(Color.lightGray); //
		btn_processprop.setName("1");
		btn_processprop.setBorder(BorderFactory.createLineBorder(Color.black, 1)); //
		btn_processprop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClickedProcessProp(); //
			}
		});

		JToolBar toolBar = new JToolBar("abc", JToolBar.VERTICAL); // 工具条
		toolBar.setPreferredSize(new Dimension(25, 100));
		toolBar.setMargin(new Insets(0, 1, 0, 1)); //
		toolBar.setFloatable(false); //
		toolBar.add(btn_processprop); //

		cardlayout_1 = new CardLayout(); //
		toftPanel_1 = new JPanel(cardlayout_1); //
		toftPanel_1.add(new JLabel("未选择可用信息"), "blank"); // 加入连线属性面板
		toftPanel_1.add(getProcessPropPanel(), "process"); // 加入流程属性面板
		toftPanel_1.setPreferredSize(new Dimension(175, 800)); // 设置默认大小!!!
		toftPanel_1.setVisible(false); // 先隐藏属性面板!!!

		panel_west.add(toolBar, BorderLayout.WEST); //
		panel_west.add(toftPanel_1, BorderLayout.CENTER); //
		return panel_west; //
	}

	private JPanel getEastPanel() {
		if (toftPanel_2 != null) {
			return toftPanel_2;
		}
		cardlayout_2 = new CardLayout(); //
		toftPanel_2 = new JPanel(cardlayout_2); //
		toftPanel_2.add(new JLabel("未选择可用信息"), "blank"); // 加入连线属性面板
		toftPanel_2.add(getActivityPropPanel(), "activity"); // 加入环节属性面板
		toftPanel_2.add(getTransitionPropPanel(), "transition"); // 加入连线属性面板
		toftPanel_2.setPreferredSize(new Dimension(203, 100)); // 设置默认大小!!!
		toftPanel_2.setVisible(false); // 先隐藏属性面板!!!
		return toftPanel_2; //
	}

	/**
	 * 环节及连线工具箱
	 * 
	 * @return
	 */
	private JToolBar getEastToolBox() {
		if (toolBar2 != null) {
			return toolBar2;
		}
		btn_toolbox1 = new WLTButton(UIUtil.getImage("toolbox1.gif")); // 工具箱
		btn_toolbox1.setBackground(Color.lightGray); //
		btn_toolbox1.setName("1");
		btn_toolbox1.setBorder(BorderFactory.createLineBorder(Color.black, 1)); //
		btn_toolbox1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked_1(); //
			}
		});

		toolBar2 = new JToolBar("abc", JToolBar.VERTICAL); // 工具条
		toolBar2.setPreferredSize(new Dimension(25, 500));
		toolBar2.setMargin(new Insets(5, 1, 5, 1)); //
		toolBar2.setFloatable(false); //

		toolBar2.add(btn_toolbox1); // 工具箱

		return toolBar2;
	}

	public JGraph getGraph() {
		return graph;
	}

	public TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil;
		}
		tbUtil = new TBUtil();
		return tbUtil; //
	}

	/**
	 * 流程属性面板..
	 * 
	 * @return
	 */
	private BillPropPanel getProcessPropPanel() {
		if (processPropPanel == null) {
			processPropPanel = new BillPropPanel("pub_wf_process_CODE1"); // 流程属性面板
			processPropPanel.addBillPropEditListener(this);
		}
		return processPropPanel;
	}

	/**
	 * 环节属性面板..
	 * 
	 * @return //
	 */
	private BillPropPanel getActivityPropPanel() {
		if (activityBillPropPanel == null) {
			activityBillPropPanel = new BillPropPanel("pub_wf_activity_CODE1"); // 环节属性面板,"pub_wf_activity_CODE1");  //new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_wf_activity_CODE1.xml")
			activityBillPropPanel.addBillPropEditListener(this);
		}
		return activityBillPropPanel;
	}

	/**
	 * 连线属性面板..
	 * 
	 * @return
	 */
	private BillPropPanel getTransitionPropPanel() {
		if (transitionBillPropPanel == null) {
			transitionBillPropPanel = new BillPropPanel("pub_wf_transition_CODE1"); // 连线属性面板!!!
			transitionBillPropPanel.addBillPropEditListener(this); //
		}
		return transitionBillPropPanel;
	}

	/**
	 * 点击了流程属性面板..
	 */
	private void onClickedProcessProp() {
		if (btn_processprop.getName().equals("1")) {
			btn_processprop.setBorder(BorderFactory.createLoweredBevelBorder()); //
			toftPanel_1.setVisible(true); //
			btn_processprop.setName("2");
			switchToPropessPropPanel();
		} else {
			btn_processprop.setBorder(BorderFactory.createLineBorder(Color.black, 1)); //
			toftPanel_1.setVisible(false); //
			btn_processprop.setName("1");
		}
	}

	public void onClicked_1() {
		if (btn_toolbox1.getName().equals("1")) {
			btn_toolbox1.setBorder(BorderFactory.createLoweredBevelBorder()); //
			toftPanel_2.setVisible(true); //
			int li_width = ClientEnvironment.getInstance().getAppletWidth() - 450;
			splitPanel.setDividerLocation(li_width); //
			btn_toolbox1.setName("2");
		} else {
			btn_toolbox1.setBorder(BorderFactory.createLineBorder(Color.black, 1)); //
			toftPanel_2.setVisible(false); //
			splitPanel.setDividerLocation(9999); //
			btn_toolbox1.setName("1");
		}
	}

	private void switchToPropessPropPanel() {
		if (cardlayout_1 != null) {
			cardlayout_1.show(toftPanel_1, "process"); //
		}
	}

	private void switchToActivityPropPanel() {
		if (cardlayout_2 != null) {
			cardlayout_2.show(toftPanel_2, "activity"); //
		}
	}

	private void switchToTransitionPropPanel() {
		if (cardlayout_2 != null) {
			cardlayout_2.show(toftPanel_2, "transition"); //
		}
	}

	private void switchToBlankPropPanel() {
		if (cardlayout_2 != null) {
			cardlayout_2.show(toftPanel_2, "blank"); //
		}
	}

	/**
	 * 创建一个流程
	 */
	private void onCreateWfProcess() {
		onCreateWfProcess(true, null, null);
	}

	/**
	 * 创建一个流程
	 */
	public void onCreateWfProcess(boolean _showDialog, String _code, String _name) {
		String code = _code;
		String name = _name;
		if (_showDialog) {
			CreateWorkFlowDialog dialog = new CreateWorkFlowDialog(this);
			dialog.setVisible(true); //
			System.out.println("关闭类型:" + dialog.getCloseType());
			if (dialog.getCloseType() == 1) {
				code = dialog.getProcessCode();
				name = dialog.getProcessName();
			} else {
				return;
			}
		}

		clearEmptyGraph(); // 清空屏幕!!关键!!
		String str_newID = null;
		try {
			str_newID = UIUtil.getSequenceNextValByDS(null, "s_pub_wf_process");
		} catch (Exception e) {
			e.printStackTrace();
		} //
		currentProcessVO = new ProcessVO(); //
		currentProcessVO.setId(str_newID); //
		currentProcessVO.setCode(code); // 流程编码
		currentProcessVO.setName(name); // 流程名称!!
		currentProcessVO.setWftype("工作流");//一般只有流程设计中有新增流程的按钮，并且该流程图不会关联流程文件，一般是为了业务需要跑的工作流【李春娟/2012-05-24】

		//重置流程属性 标题 【杨科/2013-03-27】
		ProcessBean bean = (ProcessBean) getProcessPropPanel().getBeanInstance(); //
		copyProcessDataFromVoToBean(currentProcessVO, bean); //
		getProcessPropPanel().reload(); // 重新将Bean中的数据加载到控件中去显示!!

		//先加入标题,有的客户可能不需要这个标题,以后可以考虑做成一个系统参数!!
		String str_title = "流程名称:" + this.getCurrentProcessVO().getName(); //"流程编码:" + this.getCurrentProcessVO().getCode() + ",
		if (ClientEnvironment.isAdmin()) {
			str_title = "流程名称:" + this.getCurrentProcessVO().getCode() + "/" + this.getCurrentProcessVO().getName(); //因为有大量的名称重复,所以在admin=Y时,显示编码!!!
		}
		titleCell = createVertex(str_title, 5, 5, 500, 20, Color.WHITE, Color.BLUE, new Font("System", Font.PLAIN, 12), true, null);
		titleCell.removeAllChildren(); //
		GraphConstants.setHorizontalAlignment(titleCell.getAttributes(), SwingConstants.LEFT); //
		GraphConstants.setSelectable(titleCell.getAttributes(), false); //
		GraphConstants.setMoveable(titleCell.getAttributes(), false);
		GraphConstants.setBorder(titleCell.getAttributes(), BorderFactory.createEmptyBorder()); //
		GraphConstants.setFont(titleCell.getAttributes(), new Font("宋体", Font.PLAIN, 12)); //
		graph.getGraphLayoutCache().insert(titleCell); // 加入面板

		// btn_start.setEnabled(true);
		// btn_end.setEnabled(true);
		btn_adddept.setEnabled(true); //
		btn_addstation.setEnabled(true); //

		btn_insert_0.setEnabled(true);
		btn_insert_1.setEnabled(true);
		btn_insert_2.setEnabled(true);
		btn_insert_3.setEnabled(true);
		btn_insert_4.setEnabled(true);
		btn_insert_5.setEnabled(true);
		btn_insert_6.setEnabled(true);
		btn_insert_7.setEnabled(true);
		btn_insert_8.setEnabled(true);

		btn_samex.setEnabled(true);
		btn_samey.setEnabled(true);
		btn_samew.setEnabled(true);
		btn_sameh.setEnabled(true);
		btn_zoom.setEnabled(true);
		btn_zoomin.setEnabled(true);
		btn_zoomout.setEnabled(true);
		btn_undo.setEnabled(true);
		btn_redo.setEnabled(true); //
		btn_delete.setEnabled(true); //

		onInsertStartCell(); // 插入起始结点!
		onInsertEndCell(); // 插入结束结点!!
		graph.clearSelection(); //

		groupcount = 0;
		stationcount = 0; //
	}

	/**
	 * 清空屏幕上所有对象,以备新的操作!!
	 */
	public void clearEmptyGraph() {
		graph.removeAll(); //
		int li_rootcount = graph.getModel().getRootCount();
		Object[] roots = new Object[li_rootcount];
		for (int i = 0; i < li_rootcount; i++) {
			roots[i] = graph.getModel().getRootAt(i);
		}

		Object[] cells = graph.getDescendants(roots);
		graph.getModel().remove(cells);
		graph.getGraphLayoutCache().reload(); // 一定要执行,否则会出现多余的框!!
		graph.setUI(new MyGraphUI()); // 重新设置UI
		setGridVisible(true);//设置背景格子可见
		graph.invalidate(); //

		this.stationBackGroundCell = null; //置为空!!!
		switchToBlankPropPanel(); // 属性框也置空!!
	}

	public String getWFProcessCondition() {
		return null; //
	}

	/**
	 * 选择一个工作流!!
	 */
	private void onOpenWfProcess() {
		String str_condition = getWFProcessCondition(); //
		OpenProcessDialog dialog = new OpenProcessDialog(this, str_condition);
		dialog.getBillListPanel().setDataFilterCustCondition(str_condition); //
		dialog.loadData(); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			String str_processID = dialog.getProcessID();//以前用的编码，但不能保证编码是唯一的，故需要改为流程id【李春娟/2012-05-22】
			if (str_processID != null) {
				loadGraphByID(str_processID); //
			}
		}
	}

	public void openMainGraph(ProcessVO _processVO) {
		openMainGraph(_processVO, 1); // //
	}

	public void openHistoryMainGraph(ProcessVO _processVO) {
		openHistoryMainGraph(_processVO, 1); // //
	}

	//打开一个流程图...
	public void openMainGraph(ProcessVO processVO, int _type) {
		this.isHaveLoaded = false;
		this.currentProcessVO = processVO; //
		clearEmptyGraph(); // 清空屏幕!!!关键

		boolean silockwheninit = new TBUtil().getSysOptionBooleanValue("流程图加载时是否默认锁定底层", false);
		if (checkBox_lockgroup != null) {
			if (silockwheninit) {
				checkBox_lockgroup.setSelected(true); //
			} else {
				checkBox_lockgroup.setSelected(false); //	
			}
		}
		checkBox_showorder.setSelected(false);
		GroupVO[] groupVOs = processVO.getGroupVOs(); //
		TransitionVO[] tansitionVOs = processVO.getTransitionVOs(); // 得到所有连接
		ActivityVO[] activityVOs = processVO.getActivityVOs(); //
		Hashtable cellMap = new Hashtable();

		//先加入标题,有的客户可能不需要这个标题,以后可以考虑做成一个系统参数!!
		String str_title = "流程名称:" + this.getCurrentProcessVO().getName(); //"流程编码:" + this.getCurrentProcessVO().getCode() + ",
		if (ClientEnvironment.isAdmin()) {
			str_title = "流程名称:" + this.getCurrentProcessVO().getCode() + "/" + this.getCurrentProcessVO().getName(); //因为有大量的名称重复,所以在admin=Y时,显示编码!!!
		}
		titleCell = createVertex(str_title, 5, 5, 500, 20, Color.WHITE, Color.BLUE, new Font("System", Font.PLAIN, 12), true, null);
		titleCell.removeAllChildren(); //
		GraphConstants.setHorizontalAlignment(titleCell.getAttributes(), SwingConstants.LEFT); //
		GraphConstants.setSelectable(titleCell.getAttributes(), false); //
		GraphConstants.setMoveable(titleCell.getAttributes(), false);
		GraphConstants.setBorder(titleCell.getAttributes(), BorderFactory.createEmptyBorder()); //
		GraphConstants.setFont(titleCell.getAttributes(), new Font("宋体", Font.PLAIN, 12)); //
		graph.getGraphLayoutCache().insert(titleCell); // 加入面板

		// 加入各个组..
		boolean isHaveStation = false; //
		for (int i = 0; i < groupVOs.length; i++) {
			if (groupVOs[i].getGrouptype().equals("DEPT")) { //岳耀彪曾经在蝉城现场发现顺序有乱,原因以前这里有段重置X位置的代码,而DB2中查询时没带order 条件,结果出来的顺序并不是插入的先后顺序,然后这段代码逻辑就会产生问题!!
				addDeptGroup(groupVOs[i]); //
			} else if (groupVOs[i].getGrouptype().equals("STATION")) {
				addStationGroup(groupVOs[i]); //
				isHaveStation = true; //
			}
		}

		//加入阶段组的背景!
		if (isHaveStation) { //如果有阶段了,才加入了阶段的背景的矩阵!
			addStationBackGroundActivity(); //
		}
		cellNumber = activityVOs.length;
		// 增加所有普通结点
		for (int i = 0; i < activityVOs.length; i++) {
			DefaultGraphCell cell = null;
			if (activityVOs[i].getActivitytype().equalsIgnoreCase("START")) { // 开始结点
				String fonttype = "宋体";
				int fontsize = 12;
				Color foreGround = Color.BLACK;
				Color backGround = this.startCellBgcolor; //
				if (activityVOs[i].getFonttype() != null && !activityVOs[i].getFonttype().trim().equals("")) {
					fonttype = activityVOs[i].getFonttype(); //
				}
				if (activityVOs[i].getFontsize() != null) {
					fontsize = activityVOs[i].getFontsize().intValue(); //
				}
				if (activityVOs[i].getForeground() != null && !activityVOs[i].getForeground().trim().equals("")) { //
					foreGround = convertStrToColor(activityVOs[i].getForeground()); //
				}
				if (activityVOs[i].getBackground() != null && !activityVOs[i].getBackground().trim().equals("")) { //
					backGround = convertStrToColor(activityVOs[i].getBackground()); //
				}

				cell = createVertex(activityVOs[i], activityVOs[i].getX().intValue(), activityVOs[i].getY().intValue(), activityVOs[i].getWidth().intValue(), activityVOs[i].getHeight().intValue(), backGround, foreGround, new Font(fonttype, Font.PLAIN, fontsize), true, null);
				ImageIcon startIcon = UIUtil.getImage("workflow/start.gif");
				GraphConstants.setIcon(cell.getAttributes(), startIcon);
				graph.getGraphLayoutCache().insert(cell); // 加入面板
			} else if (activityVOs[i].getActivitytype().equalsIgnoreCase("END")) { // 结束结点
				String fonttype = "宋体";
				int fontsize = 12;
				Color foreGround = Color.BLACK;
				Color backGround = this.endCellBgcolor; //
				if (activityVOs[i].getFonttype() != null && !activityVOs[i].getFonttype().trim().equals("")) {
					fonttype = activityVOs[i].getFonttype(); //
				}
				if (activityVOs[i].getFontsize() != null) {
					fontsize = activityVOs[i].getFontsize().intValue(); //
				}
				if (activityVOs[i].getForeground() != null && !activityVOs[i].getForeground().trim().equals("")) { //
					foreGround = convertStrToColor(activityVOs[i].getForeground()); //
				}
				if (activityVOs[i].getBackground() != null && !activityVOs[i].getBackground().trim().equals("")) { //
					backGround = convertStrToColor(activityVOs[i].getBackground()); //
				}

				cell = createVertex(activityVOs[i], activityVOs[i].getX().intValue(), activityVOs[i].getY().intValue(), activityVOs[i].getWidth().intValue(), activityVOs[i].getHeight().intValue(), backGround, foreGround, new Font(fonttype, Font.PLAIN, fontsize), true, null);
				ImageIcon endIcon = UIUtil.getImage("workflow/end.gif");
				GraphConstants.setIcon(cell.getAttributes(), endIcon);
				graph.getGraphLayoutCache().insert(cell); // 加入面板
			} else { // 普通结点
				String fonttype = "宋体";
				int fontsize = 12;
				Color foreGround = Color.BLACK; //
				Color backGround = normalCellBgcolor; //
				if (activityVOs[i].getFonttype() != null && !activityVOs[i].getFonttype().trim().equals("")) {
					fonttype = activityVOs[i].getFonttype(); //
				}
				if (activityVOs[i].getFontsize() != null) {
					fontsize = activityVOs[i].getFontsize().intValue(); //
				}
				if (activityVOs[i].getForeground() != null && !activityVOs[i].getForeground().trim().equals("")) { //
					foreGround = convertStrToColor(activityVOs[i].getForeground()); //
				}
				if (activityVOs[i].getBackground() != null && !activityVOs[i].getBackground().trim().equals("")) { //
					backGround = convertStrToColor(activityVOs[i].getBackground()); //
				}

				String str_viewtypename = "cn.com.infostrategy.ui.workflow.design.CellView_" + activityVOs[i].getViewtype(); //
				cell = createVertex(activityVOs[i], activityVOs[i].getX().intValue(), activityVOs[i].getY().intValue(), activityVOs[i].getWidth().intValue(), activityVOs[i].getHeight().intValue(), backGround, foreGround, new Font(fonttype, Font.PLAIN, fontsize), true, str_viewtypename);
				graph.getGraphLayoutCache().insert(cell); // 加入面板
			}
			graph.getGraphLayoutCache().toFront(new DefaultGraphCell[] { cell }); // 放在前层
			cellMap.put(activityVOs[i].getId() + "", cell); //
		}

		// 增加所有连线
		for (int i = 0; i < tansitionVOs.length; i++) {
			DefaultEdge edge = new DefaultEdge(tansitionVOs[i]); //
			// this.setEdgeAttributes(edge.getAttributes()); //

			Integer fromId = tansitionVOs[i].getFromactivity(); // 从...
			Integer toId = tansitionVOs[i].getToactivity(); // 去...

			DefaultGraphCell fromcell = (DefaultGraphCell) cellMap.get(fromId + "");
			DefaultGraphCell tocell = (DefaultGraphCell) cellMap.get(toId + "");

			if (fromcell != null && tocell != null) {
				edge.setSource(fromcell.getChildAt(0)); // 起始位置
				edge.setTarget(tocell.getChildAt(0)); // 结束位置
			}

			GraphConstants.setLabelAlongEdge(edge.getAttributes(), true); //如果为True则字上斜着的贴着线
			GraphConstants.setEndFill(edge.getAttributes(), true);//连线箭头填充
			//GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_TECHNICAL);//连线箭头为三角
			GraphConstants.setLineEnd(edge.getAttributes(), tansitionVOs[i].getLineType());//【sunfujun/20120426/增加箭头类型的配置_王钢】
			if (tansitionVOs[i].isSingle()) {
				GraphConstants.setLineBegin(edge.getAttributes(), GraphConstants.ARROW_NONE);
				GraphConstants.setBeginFill(edge.getAttributes(), false);
			} else {
				GraphConstants.setLineBegin(edge.getAttributes(), tansitionVOs[i].getLineType());
				GraphConstants.setBeginFill(edge.getAttributes(), true);
			}
			GraphConstants.setLineWidth(edge.getAttributes(), 1); //

			if (tansitionVOs[i].getUiname() != null && tansitionVOs[i].getUiname().contains(",")) {
				String[] points = tansitionVOs[i].getUiname().split(",");
				GraphConstants.setLabelPosition(edge.getAttributes(), new Point(Integer.parseInt(points[0]), Integer.parseInt(points[1])));
			}

			// 设置字体与连线颜色
			String str_fonttype = "System"; //
			int li_fontsize = 12; //
			Color foreGround = Color.BLACK;
			Color backGround = linecolor; //

			if (tansitionVOs[i].getFonttype() != null && !tansitionVOs[i].getFonttype().trim().equals("")) {
				str_fonttype = tansitionVOs[i].getFonttype(); //
			}

			if (tansitionVOs[i].getFontsize() != null) {
				li_fontsize = tansitionVOs[i].getFontsize().intValue(); //
			}

			if (tansitionVOs[i].getForeground() != null && !tansitionVOs[i].getForeground().trim().equals("")) {
				foreGround = convertStrToColor(tansitionVOs[i].getForeground()); //
			}

			if (tansitionVOs[i].getBackground() != null && !tansitionVOs[i].getBackground().trim().equals("")) {
				backGround = convertStrToColor(tansitionVOs[i].getBackground()); //
			}

			GraphConstants.setFont(edge.getAttributes(), new Font(str_fonttype, Font.PLAIN, li_fontsize)); //
			GraphConstants.setForeground(edge.getAttributes(), foreGround);//
			GraphConstants.setLineColor(edge.getAttributes(), backGround);//

			rePaintLineStyle(edge); // 重新设置线的颜色!!

			// 设置连线路径
			List pointLs = tansitionVOs[i].getPoints(); // 已经包含起始,结束结点
			GraphConstants.setPoints(edge.getAttributes(), this.convertToPoint2DList(pointLs));

			// edgeRender(edge);

			graph.getGraphLayoutCache().insert(edge); // 加入面板
			graph.getGraphLayoutCache().toFront(new DefaultGraphCell[] { edge }); // 放在前层
		}

		graph.clearSelection(); //

		if (_type == 1) {
			ProcessBean bean = (ProcessBean) getProcessPropPanel().getBeanInstance(); //
			copyProcessDataFromVoToBean(processVO, bean); //
			getProcessPropPanel().reload(); // 重新将Bean中的数据加载到控件中去显示!!
		}

		// 所有按钮有效
		if (li_opentype == 1) {
			btn_adddept.setEnabled(true);
			btn_addstation.setEnabled(true);
			btn_insert_0.setEnabled(true);
			btn_insert_1.setEnabled(true);
			btn_insert_2.setEnabled(true);
			btn_insert_3.setEnabled(true);
			btn_insert_4.setEnabled(true);
			btn_insert_5.setEnabled(true); //
			btn_insert_6.setEnabled(true); //
			btn_insert_7.setEnabled(true);
			btn_insert_8.setEnabled(true);

			btn_samex.setEnabled(true);
			btn_samey.setEnabled(true);
			btn_samew.setEnabled(true);
			btn_sameh.setEnabled(true);
			btn_zoom.setEnabled(true);
			btn_zoomin.setEnabled(true);
			btn_zoomout.setEnabled(true);
			btn_undo.setEnabled(true);
			btn_redo.setEnabled(true); //
			btn_delete.setEnabled(true); //
		}

		graph.setUI(new MyGraphUI()); //
		graph.invalidate(); //

		if (!showtoolbox) {
			onLockGroup(); //
		}
		graph.getGraphLayoutCache().reload(); //关键!但一定要放在下面的三行代码之前!
		//reSortStation(null);//不要重置部门高度
		reSortDept(null);
		reSetAllLayer(true);
		isEditChanged = false; //因为刚打开,所以并不认为是修改过的!
		this.isHaveLoaded = true;
	}

	/**
	 * 真正画图的地方
	 * 
	 * @param _worktickno
	 * @param _processid
	 * @param _tabpos
	 * @param _type
	 *            1-client,2-server
	 * @return
	 */
	public void openHistoryMainGraph(ProcessVO processVO, int _type) {
		openMainGraph(processVO, _type); //以前不知道是谁竟然把整个方法都复制了一遍,不是为什么要那么做!!!
	}

	/**
	 * 将颜色转换成字符串,以逗号将R,G,B相隔
	 * 
	 * @param _color
	 * @return
	 */
	private String convertColorToStr(Color _color) {
		return _color.getRed() + "," + _color.getGreen() + "," + _color.getBlue(); //
	}

	/**
	 * 将字符串转换成颜色
	 * 
	 * @param _str
	 * @return
	 */
	private Color convertStrToColor(String _str) {
		String[] items = _str.split(","); //
		return new Color(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]));
	}

	/**
	 * 把List中的double[]元素转换为对应的Point2D.Double类型
	 * 
	 * @param list
	 * @return
	 */
	private List convertToPoint2DList(List list) {
		List _list = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			double[] elem = (double[]) list.get(i);
			Point2D.Double _elem = new Point2D.Double(elem[0], elem[1]); //
			_list.add(_elem);
		}

		return _list;
	}

	/**
	 * 保存一个工作流!!
	 */
	public void onSaveWfProcess() {
		onSaveWfProcess(true);
	}

	/**
	 * 保存一个工作流
	 * @param _isShowMessage  是否显示提示信息
	 */
	public void onSaveWfProcess(boolean _isShowMessage) {
		try {
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			long ll_1 = System.currentTimeMillis(); //
			if (currentProcessVO == null) {
				MessageBox.show(this, "当前没有数据,无法保存!"); //
				return;
			}
			//马宁提出，这里不要提示，否则不知道是否需要保存【李春娟/2012-05-09】
			//			if (!isEditChanged) {
			//				if (JOptionPane.showConfirmDialog(this, "数据没有发生变化,你想重新保存之吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			//					return;
			//				}
			//			}
			if (showtoolbox) {//如果显示工具条，才可以展开环节和连线属性，才有必要停止编辑
				getActivityPropPanel().stopEditing(); // 结束环节框编辑..
				getTransitionPropPanel().stopEditing(); // 结束连线框编辑..
			}

			GraphModel model = graph.getModel();
			int rootCount = model.getRootCount(); // 所有结点的数目
			ArrayList activityVOList = new ArrayList(); //
			ArrayList transitionVOList = new ArrayList(); //
			ArrayList groupVOList = new ArrayList(); //

			// 保存环节
			HashMap hs_code = new HashMap(); //
			for (int i = 0; i < rootCount; i++) {
				Object obj = model.getRootAt(i);
				if (obj instanceof DefaultGraphCell) {
					DefaultGraphCell cell = (DefaultGraphCell) obj; //
					Object linkedObject = cell.getUserObject();
					if (linkedObject instanceof ActivityVO) {
						Rectangle2D point2D = GraphConstants.getBounds(cell.getAttributes()); // 取得坐标位置
						int x = new Double(point2D.getX()).intValue();
						int y = new Double(point2D.getY()).intValue();
						int width = new Double(point2D.getWidth()).intValue(); // 宽度
						int height = new Double(point2D.getHeight()).intValue(); // 高度

						ActivityVO activityVO = (ActivityVO) linkedObject; // 类型转换
						boolean isCanSaveWhenCodeSame = new TBUtil().getSysOptionBooleanValue("环节编码相等时是否允许保存", true); //农行里没有平台配置表，并且只能编辑流程图，对环节属性不能修改，所以暂时改为允许保存
						if (hs_code.containsKey(activityVO.getCode()) && !isCanSaveWhenCodeSame) {
							String str_msg = "环节【" + activityVO.getWfname() + "】与环节【" + hs_code.get(activityVO.getCode()) + "】的编码都是【" + activityVO.getCode() + "】,不允许存在相同的环节编码,退出保存操作!";
							str_msg = new TBUtil().getTrimSwapLineStr(str_msg); //
							MessageBox.show(this, str_msg); //
							return; //
						} else {
							hs_code.put(activityVO.getCode(), activityVO.getWfname()); //
						}
						// activityVO.setJoincount(getVertexInCount(cell));
						// activityVO.setSplitcount(getVertexOutCount(cell));
						activityVO.setX(new Integer(x));
						activityVO.setY(new Integer(y));
						activityVO.setWidth(new Integer(width)); //
						activityVO.setHeight(new Integer(height)); //
						activityVO.setBelongdeptgroup(null); //
						activityVO.setBelongstationgroup(null); //
						activityVOList.add(activityVO);
					}
				}
			}

			// 保存连线..
			for (int i = 0; i < rootCount; i++) {
				Object obj = model.getRootAt(i);
				if (obj instanceof DefaultGraphCell) {
					DefaultGraphCell cell = (DefaultGraphCell) obj;
					Object linkedObject = cell.getUserObject();

					if (linkedObject instanceof TransitionVO) {
						updateConnectValue((DefaultEdge) cell);
						TransitionVO transitionVO = (TransitionVO) linkedObject;
						transitionVOList.add(transitionVO);
					}
				}
			}

			// 保存部门组..
			DefaultGraphCell[] deptCells = getDeptCells(); //
			for (int i = 0; i < deptCells.length; i++) {
				DefaultGraphCell cell = deptCells[i];
				Object linkedObject = cell.getUserObject(); // 取用用户对象
				if (linkedObject instanceof GroupVO) { // 如果是组
					Rectangle2D itempoint = graph.getCellBounds(cell); // 取得该组的位置与大小
					GroupVO groupVO = (GroupVO) linkedObject; //
					groupVO.setX(new Integer((int) itempoint.getX())); //
					groupVO.setY(new Integer((int) itempoint.getY())); //
					groupVO.setWidth(new Integer((int) itempoint.getWidth())); //
					groupVO.setHeight(new Integer((int) itempoint.getHeight())); //
					groupVOList.add(groupVO); //
				}
			}

			// 保存阶段组..
			DefaultGraphCell[] stationCells = getStationCells(); //
			for (int i = 0; i < stationCells.length; i++) {
				DefaultGraphCell cell = stationCells[i]; //
				Object linkedObject = cell.getUserObject(); // 取用用户对象
				if (linkedObject instanceof GroupVO) { // 如果是组
					Rectangle2D itempoint = graph.getCellBounds(cell); // 取得该组的位置与大小
					GroupVO groupVO = (GroupVO) linkedObject; //
					groupVO.setX(new Integer((int) itempoint.getX())); //
					groupVO.setY(new Integer((int) itempoint.getY())); //
					groupVO.setWidth(new Integer((int) itempoint.getWidth())); //
					groupVO.setHeight(new Integer((int) itempoint.getHeight())); //
					groupVOList.add(groupVO); //
				}
			}

			// 约束检查，比如不能有断线等
			ActivityVO[] activityVOs = (ActivityVO[]) activityVOList.toArray(new ActivityVO[0]); //
			TransitionVO[] transitionVOs = (TransitionVO[]) transitionVOList.toArray(new TransitionVO[0]); //
			GroupVO[] groupVOs = (GroupVO[]) groupVOList.toArray(new GroupVO[0]); //
			setActivityBelongGroup(activityVOs, groupVOs); // 在Activity上绑定BelongGroup

			currentProcessVO.setActivityVOs(activityVOs); //
			currentProcessVO.setTransitionVOs(transitionVOs); //
			currentProcessVO.setGroupVOs(groupVOs); //

			WorkFlowServiceIfc wfservice = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class); // Remote
			wfservice.saveWFProcess(currentProcessVO, "" + currentProcessVO.getId()); //
			isEditChanged = false; //保存后表示不变化了
			addActivityIds.clear();//清空新增环节id
			if (_isShowMessage) {
				long ll_2 = System.currentTimeMillis(); //
				MessageBox.show(this, "保存成功,共耗时[" + (ll_2 - ll_1) + "]毫秒!"); //
			}
		} catch (Exception e) {
			e.printStackTrace(); //
			MessageBox.showException(this, e); //
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
	}

	/**
	 * 为环节设置对应的部门矩阵与阶段矩阵名
	 * 
	 * @param activityVOs
	 * @param groupVOs
	 */
	private void setActivityBelongGroup(ActivityVO[] activityVOs, GroupVO[] groupVOs) {
		if (groupVOs == null || groupVOs.length == 0) {
			return;
		}

		for (int i = 0; i < activityVOs.length; i++) { // 遍历各个环节
			for (int j = 0; j < groupVOs.length; j++) { // 遍历各个组
				if (groupVOs[j].getGrouptype().equals("DEPT")) { // 如果是机构组,即坚着的
					if ((activityVOs[i].getX() + (activityVOs[i].getWidth() / 2) >= groupVOs[j].getX()) //X轴上,环节的左边必须在该组的左边的右方
							&& (activityVOs[i].getX() + (activityVOs[i].getWidth() / 2) < groupVOs[j].getX() + groupVOs[j].getWidth()) //X轴上,环节的左边必须在组的右边的左边
							&& (activityVOs[i].getY() < 30 + groupVOs[j].getHeight()) //
					) { // 环节的X坐标大于组的坐标.
						activityVOs[i].setBelongdeptgroup(groupVOs[j].getWfname()); // 直到找完所有的纵向组
					}
				}
			}

			for (int j = 0; j < groupVOs.length; j++) { // 遍历各个组
				if (groupVOs[j].getGrouptype().equals("STATION")) { // 如果是横着的
					if (activityVOs[i].getY() + (activityVOs[i].getHeight() / 2) < groupVOs[j].getY()) {
						activityVOs[i].setBelongstationgroup(groupVOs[j].getWfname()); // 只要找到一个横向组就行了
						break;
					}
				}
			}
		}
	}

	/**
	 * 光亮显示一个结点
	 */
	public void lightCell(String[][] str_id) {
		unlightAllCell();
		unlightAllLine(); //

		for (int i = 0; i < str_id.length; i++) {
			DefaultGraphCell cell = findCellById(str_id[i][0]);
			if (cell != null) {
				if (str_id[i][1].equals("Y")) {
					GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createLineBorder(Color.RED, 3)); //当前
				} else if (str_id[i][1].equals("From")) { //来的环节
					GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createLineBorder(Color.MAGENTA, 3)); //来源是
				} else if (str_id[i][1].equals("To")) { //去的环节
					GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createLineBorder(Color.BLUE, 3)); //去的是绿的
				} else {
					GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createLineBorder(Color.MAGENTA, 3)); //
				}
			}

			//			if (i > 0) {
			//				String str_fromId = str_id[i - 1][0]; //来源环节
			//				String str_toId = str_id[i][0]; //目标环节
			//				DefaultEdge edge = findLineById(str_fromId, str_toId); //找到线,然后光亮显示!
			//				if (edge != null) {
			//					GraphConstants.setLineColor(edge.getAttributes(), Color.MAGENTA);
			//					GraphConstants.setLineWidth(edge.getAttributes(), 1); //
			//				}
			//			}
		}

		graph.getGraphLayoutCache().reload(); //
		// graph.updateUI();
		// graph.setUI(new MyGraphUI()); // 重新设置UI
		// graph.invalidate(); //
		graph.repaint(); //
	}

	public void lightLine(String[][] _cellIds) {
		for (int i = 0; i < _cellIds.length; i++) {
			String str_fromId = _cellIds[i][0];
			String str_toId = _cellIds[i][1];
			DefaultEdge edge = findLineById(str_fromId, str_toId); //找到线,然后光亮显示!
			if (edge != null) {
				GraphConstants.setLineColor(edge.getAttributes(), Color.MAGENTA);
				GraphConstants.setLineWidth(edge.getAttributes(), 1); //
			}
		}
		graph.getGraphLayoutCache().reload(); //
		graph.repaint(); //
	}

	/**
	 * 光亮显示一个结点
	 */
	public void lightCell(String[] _activityid, Color _color) {
		unlightAllCell();
		unlightAllLine(); //

		for (int i = 0; i < _activityid.length; i++) {
			DefaultGraphCell cell = findCellById(_activityid[i]);
			if (cell != null) {
				GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createLineBorder(_color, 1)); //
			}

		}
		graph.getGraphLayoutCache().reload(); //
		graph.repaint(); //
	}

	public void unlightAllCell() {
		GraphModel model = graph.getModel();
		int rootCount = model.getRootCount(); // 所有结点的数目
		for (int i = 0; i < rootCount; i++) {
			Object obj = model.getRootAt(i);
			if (obj instanceof DefaultGraphCell) {
				DefaultGraphCell cell = (DefaultGraphCell) obj; //
				Object linkedObject = cell.getUserObject();
				if (linkedObject instanceof ActivityVO) {
					GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createRaisedBevelBorder()); //
				}
			}
		}
	}

	/**
	 * 灰掉所有的线
	 */
	public void unlightAllLine() {
		GraphModel model = graph.getModel();
		int rootCount = model.getRootCount(); // 所有结点的数目
		for (int i = 0; i < rootCount; i++) {
			Object obj = model.getRootAt(i);
			if (obj instanceof DefaultEdge) {
				DefaultEdge edge = (DefaultEdge) obj; //
				GraphConstants.setLineColor(edge.getAttributes(), Color.BLACK);
				GraphConstants.setLineWidth(edge.getAttributes(), 1);
			}
		}
	}

	private DefaultGraphCell findCellById(String _id) {
		GraphModel model = graph.getModel();
		int rootCount = model.getRootCount(); // 所有结点的数目
		for (int i = 0; i < rootCount; i++) {
			Object obj = model.getRootAt(i);
			if (obj instanceof DefaultGraphCell) {
				DefaultGraphCell cell = (DefaultGraphCell) obj; //
				Object linkedObject = cell.getUserObject();
				if (linkedObject instanceof ActivityVO) {
					ActivityVO activityVO = (ActivityVO) linkedObject; // 类型转换
					if (("" + activityVO.getId()).equals(_id)) {
						return cell;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 为某个结点增加热点
	 * 
	 * @param _id
	 * @param _riskVO
	 */
	public void setCellAddRisk(String _id, RiskVO _riskVO) {
		DefaultGraphCell cell = findCellById(_id); //
		if (cell == null) {
			return;
		}
		ActivityVO acvo = (ActivityVO) cell.getUserObject();
		acvo.setRiskVO(_riskVO); //
		graph.repaint(); //
	}

	private DefaultEdge findLineById(String _fromId, String _toID) {
		GraphModel model = graph.getModel();
		int rootCount = model.getRootCount(); // 所有结点的数目
		for (int i = 0; i < rootCount; i++) {
			Object obj = model.getRootAt(i);
			if (obj instanceof DefaultEdge) {
				DefaultEdge edge = (DefaultEdge) obj; //
				DefaultGraphCell fromcell = getEdgeSource(edge);
				DefaultGraphCell tocell = getEdgeTarget(edge);
				if (fromcell != null && tocell != null) {
					ActivityVO fromActivityVO = (ActivityVO) fromcell.getUserObject();
					ActivityVO toActivityVO = (ActivityVO) tocell.getUserObject();
					if (("" + fromActivityVO.getId()).equals(_fromId) && ("" + toActivityVO.getId()).equals(_toID)) {
						return edge;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 更新边所包含的对象信息
	 * 
	 * @param edge
	 */
	private void updateConnectValue(DefaultEdge edge) {
		TransitionVO vo_trans = (TransitionVO) edge.getUserObject();
		List points = GraphConstants.getPoints(edge.getAttributes());
		if (points != null) {
			vo_trans.setPoints(convertToArrayList(points));
		}

		if (vo_trans.getWfname() != null && !vo_trans.getWfname().trim().equals("")) {
			Point2D point = GraphConstants.getLabelPosition(edge.getAttributes());
			if (point != null) {
				//System.out.println(">>>>>>>>>" + (point.getX() + "").substring(0, (point.getX() + "").indexOf(".")) + "," + point.getY());
				vo_trans.setUiname((point.getX() + "").substring(0, (point.getX() + "").indexOf(".")) + "," + (point.getY() + "").substring(0, (point.getY() + "").indexOf(".")));
			}
		}
		DefaultGraphCell source_cell = getEdgeSource(edge);
		if (source_cell != null) {
			ActivityVO source_vo = (ActivityVO) source_cell.getUserObject();
			if (source_vo != null) {
				vo_trans.setFromactivity(source_vo.getId());
			}
		}

		DefaultGraphCell target_cell = getEdgeTarget(edge);
		if (target_cell != null) {
			ActivityVO target_vo = (ActivityVO) target_cell.getUserObject();
			if (target_vo != null) {
				vo_trans.setToactivity(target_vo.getId());
			}
		}
	}

	/**
	 * 得到连接边的起始结点
	 */
	private DefaultGraphCell getEdgeSource(DefaultEdge edge) {
		DefaultGraphCell cell = (DefaultGraphCell) graph.getModel().getParent(edge.getSource());
		return cell;
	}

	/**
	 * 得到连接边的终止结点
	 */
	private DefaultGraphCell getEdgeTarget(DefaultEdge edge) {
		DefaultGraphCell cell = (DefaultGraphCell) graph.getModel().getParent(edge.getTarget());
		return cell;
	}

	/**
	 * 把List中的Point2D.Double元素转换为对应的double[]类型
	 * 
	 * @param list
	 * @return
	 */
	private List convertToArrayList(List list) {
		List _list = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			Point2D.Double elem = (Point2D.Double) list.get(i);
			double[] _elem = new double[2];
			_elem[0] = elem.getX();
			_elem[1] = elem.getY();
			_list.add(_elem);
		}

		return _list;
	}

	private Integer getNewActivityID() {
		String str_newID = null;
		try {
			str_newID = UIUtil.getSequenceNextValByDS(null, "s_pub_wf_activity");
		} catch (Exception e) {
			e.printStackTrace();
		} //
		return new Integer(str_newID);
	}

	/**
	 * 取得一个新连线的主键
	 * 
	 * @return
	 */
	private Integer getNewTransitionID() {
		String str_newID = null;
		try {
			str_newID = UIUtil.getSequenceNextValByDS(null, "s_pub_wf_transition");
		} catch (Exception e) {
			e.printStackTrace();
		} //
		return new Integer(str_newID);
	}

	private void onInsertStartCell() {
		int x = 90;
		int y = 60;

		ActivityVO activityVO = new ActivityVO();
		activityVO.setActivitytype("START"); //
		Integer activityid = getNewActivityID();
		activityVO.setId(activityid);//以前这里又重新获得一下id，activityVO.setId(getNewActivityID())，bug!!!【李春娟/2012-05-22】
		addActivityIds.add(activityid + "");
		activityVO.setCode("START"); //
		activityVO.setWfname("开始"); //
		activityVO.setUiname("开始"); //
		activityVO.setX(new Integer(x));
		activityVO.setY(new Integer(y));
		activityVO.setApprovemodel("1"); // 1表示抢占
		activityVO.setAutocommit("N"); // 不自动提交
		activityVO.setShowparticimode("1"); //
		activityVO.setDesc("");
		activityVO.setViewtype(1); //
		activityVO.setFonttype("System");//需要设置一下，否则复制流程图时会报错【李春娟/2014-10-21】
		activityVO.setFontsize(12);
		activityVO.setForeground("0,0,0");
		activityVO.setBackground("225,255,225");

		DefaultGraphCell cell = createVertex(activityVO, x, y, 40, 40, this.startCellBgcolor, Color.BLACK, new Font("System", Font.PLAIN, 12), true, null);
		GraphConstants.setIcon(cell.getAttributes(), UIUtil.getImage("workflow/start.gif"));
		getGraph().getGraphLayoutCache().insert(cell); // 加入面板
		getGraph().getGraphLayoutCache().toFront(new DefaultGraphCell[] { cell }); //
		isEditChanged = true; //表示已经变化了
	}

	private void onInsertEndCell() {
		int x = 300;
		int y = 350;

		ActivityVO activityVO = new ActivityVO();
		activityVO.setActivitytype("END"); //
		Integer activityid = getNewActivityID();
		activityVO.setId(activityid);
		addActivityIds.add(activityid + "");
		activityVO.setCode("END"); //
		activityVO.setWfname("结束"); //
		activityVO.setUiname("结束"); //
		activityVO.setX(new Integer(x));
		activityVO.setY(new Integer(y));
		activityVO.setApprovemodel("1"); // 1表示抢占
		activityVO.setAutocommit("N"); // 不自动提交
		activityVO.setShowparticimode("1"); //
		activityVO.setDesc("");
		activityVO.setViewtype(1); //
		activityVO.setFonttype("System");//需要设置一下，否则复制流程图时会报错【李春娟/2014-10-21】
		activityVO.setFontsize(12);
		activityVO.setForeground("0,0,0");
		activityVO.setBackground("225,255,225");

		DefaultGraphCell cell = createVertex(activityVO, x, y, 40, 40, this.endCellBgcolor, Color.BLACK, new Font("System", Font.PLAIN, 12), true, null);
		GraphConstants.setIcon(cell.getAttributes(), UIUtil.getImage("workflow/end.gif"));

		getGraph().getGraphLayoutCache().insert(cell); // 加入面板
		getGraph().getGraphLayoutCache().toFront(new DefaultGraphCell[] { cell }); //
		isEditChanged = true; //表示已经变化了
	}

	/**
	 * 重新定义各个部门的位置与大小..
	 * 所有拖动或移动时触发的入口!!!
	 * @param _groupCell
	 */
	private void resetMyPosAndSize(DefaultGraphCell _groupCell) {
		Rectangle2D itempoint_sel = graph.getCellBounds(_groupCell); // 取得选中结点的位置与大小
		Object userobj = _groupCell.getUserObject();
		if (userobj instanceof ActivityVO) { //如果移动的是普通环节
			reSetAllLayer(false); //
		} else if (userobj instanceof GroupVO) {
			GroupVO gvo = (GroupVO) _groupCell.getUserObject();
			String type = gvo.getGrouptype();
			if (type.equals("DEPT")) {
				reSortDept(itempoint_sel); // 重排部门组..
			} else if (type.equals("STATION")) {
				reSortStation(itempoint_sel); // 重排阶段组..
			}
			reSetAllLayer(true); //
		}
		isEditChanged = true; //表示已经变化了
	}

	public void reSetAllLayer(boolean _isReComputeHeight) {
		if (_isReComputeHeight && stationBackGroundCell != null) {
			GraphConstants.setBounds(stationBackGroundCell.getAttributes(), new Rectangle2D.Double(10, 30, 61, getMaxDeptHeight())); //
		}
		getGraph().getGraphLayoutCache().toBack(getStationCells()); //
		getGraph().getGraphLayoutCache().toBack(getDeptCells()); //
		if (stationBackGroundCell != null) {
			getGraph().getGraphLayoutCache().toBack(new DefaultGraphCell[] { stationBackGroundCell }); //
		} else {
			graph.getGraphLayoutCache().toBack(new Object[] { titleCell });//需要设置一下，否则重叠的环节顺序就错了【李春娟/2012-05-22】
		}
	}

	/**
	 * 重排各个部门
	 * 
	 * @param _point_sel
	 */
	private void reSortDept(Rectangle2D _point_sel) {//由于阶段的文字是画在阶段帽子上，所以改变高度的时候设定不能越过最下面的阶段【sunfujun/20120413】
		DefaultGraphCell[] deptCells = getDeptCells(); //
		double ld_x = 70; //
		double li_firstheight = 0; //
		double laststation_y = 0; //
		DefaultGraphCell[] stationCells = getStationCells();
		if (stationCells != null && stationCells.length > 0) {
			laststation_y = getMaxStationPos() + 10;
		}
		for (int k = 0; k < deptCells.length; k++) { // 遍历排序的集合
			DefaultGraphCell itemcell = deptCells[k]; //
			Rectangle2D itempoint = graph.getCellBounds(itemcell); // 取得该部门的位置
			double ld_width = itempoint.getWidth(); // 宽度
			if (k == 0) {
				if (laststation_y > 0) {
					li_firstheight = (laststation_y - 30) > itempoint.getHeight() ? (laststation_y - 30) : itempoint.getHeight();
				} else {
					li_firstheight = itempoint.getHeight();
				}
			}
			if (_point_sel != null) {
				moveGrouptoNewPoint(itemcell, ld_x, 30, ld_width, (laststation_y - 30) > _point_sel.getHeight() ? (laststation_y - 30) : _point_sel.getHeight()); //
			} else {
				moveGrouptoNewPoint(itemcell, ld_x, 30, ld_width, li_firstheight); //
			}
			ld_x = ld_x + ld_width - 1; //
		}
		if (deptCells == null || deptCells.length == 0) {//如果部门都没有了将阶段也都删掉！
			graph.getModel().remove(this.getStationCells());
			graph.getGraphLayoutCache().toBack(new Object[] { titleCell });//需要设置一下，否则重叠的环节顺序就错了【李春娟/2012-05-22】
			graph.getGraphLayoutCache().reload();
		} else {
			graph.getGraphLayoutCache().reload();//必须先reload一把
			moveStationToMaxDept(); // 移动线至最大部门的右边..
		}
		isEditChanged = true; //表示已经变化了
	}

	/**
	 * 移动一个机构组至新位置
	 * 
	 * @param _x
	 * @param _y
	 * @param _width
	 * @param _height
	 */
	private void moveGrouptoNewPoint(DefaultGraphCell _group, double _x, double _y, double _width, double _height) {
		DefaultGraphCell cell = (DefaultGraphCell) _group;
		GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(_x, _y, _width, _height)); //
		isEditChanged = true; //表示已经变化了
	}

	/**
	 * 移动所有线的位置至最右边部门的大小
	 */
	private void moveStationToMaxDept() {
		double ld_max_x_width = getMaxDeptRightPos(); // 取得所有部门最右边的位置(x+width)
		double li_x = 10;
		if (ld_max_x_width > 0) {
			// 将所有的阶段线的宽度改成与最右边部门的右边线对齐!!!
			DefaultGraphCell[] stationCells = getStationCells(); //
			for (int i = 0; i < stationCells.length; i++) {
				DefaultGraphCell itemcell = stationCells[i]; // 取得其中的Cell
				Rectangle2D itempoint = graph.getCellBounds(itemcell); // 取得该部门的位置
				GraphConstants.setBounds(itemcell.getAttributes(), new Rectangle2D.Double(li_x, itempoint.getY() < 90 ? 90 : itempoint.getY(), ld_max_x_width - li_x, itempoint.getHeight())); //调整阶段高度，默认在部门标头下面【李春娟/2012-04-17】
			}
			graph.getGraphLayoutCache().reload(); //
			isEditChanged = true; //表示已经变化了
		}
	}

	/**
	 * 将所有部门都改成一样大...
	 */
	private void resizeAllDeptSample(double _height) {
		DefaultGraphCell[] deptCells = getDeptCells(); //
		for (int i = 0; i < deptCells.length; i++) {
			DefaultGraphCell itemcell = deptCells[i]; //
			Rectangle2D itempoint = graph.getCellBounds(itemcell); // 取得该部门的位置
			double ld_x = itempoint.getX(); //
			double ld_y = itempoint.getY(); //
			double ld_width = itempoint.getWidth(); // 宽度

			DefaultGraphCell obj_bottem = (DefaultGraphCell) itemcell;
			GraphConstants.setBounds(obj_bottem.getAttributes(), new Rectangle2D.Double(ld_x, ld_y, ld_width, _height)); //
		}
		graph.getGraphLayoutCache().reload(); //
		isEditChanged = true; //表示已经变化了
	}

	/**
	 * 重新排序
	 * 
	 * @param _itempoint_sel
	 * 
	 */
	private void reSortStation(Rectangle2D _itempoint_sel) {
		if (getMaxDeptHeight() < getMaxStationPos() + 10) {
			resizeAllDeptSample(getMaxStationPos() + 10); //调整高度【李春娟/2012-04-17】
		}
		moveStationToMaxDept(); // 移动所有的线到部门的右边
	}

	/**
	 * 取得所有部门中最边的部门的右边线坐标
	 * 
	 * @return
	 */
	private double getMaxDeptRightPos() {
		double ld_max_x = 0; //
		double ld_max_x_width = 0; //
		DefaultGraphCell[] deptCells = getDeptCells(); //
		if (deptCells != null && deptCells.length > 0) {
			Rectangle2D itempoint = graph.getCellBounds(deptCells[deptCells.length - 1]); // 取得该部门的位置
			ld_max_x = itempoint.getX();
			ld_max_x_width = ld_max_x + itempoint.getWidth();
		}
		return ld_max_x_width;
	}

	/**
	 * 取得最大的阶段的位置,即最下面的阶段的位置..
	 * 
	 * @return
	 */
	private double getMaxStationPos() {
		double ld_y = 0; //
		DefaultGraphCell[] stationCells = getStationCells(); //
		if (stationCells != null && stationCells.length > 0) {
			Rectangle2D itempoint_right = graph.getCellBounds(stationCells[stationCells.length - 1]); // 取得该部门的位置
			ld_y = itempoint_right.getY();
		}

		DefaultGraphCell[] activityCells = getActivityCells(); //
		if (activityCells != null && activityCells.length > 0) {
			Rectangle2D itempoint_right = graph.getCellBounds(activityCells[activityCells.length - 1]); // 取得该部门的位置
			double ld_tmp_y = itempoint_right.getY();
			if (ld_tmp_y > ld_y) {
				ld_y = ld_tmp_y;
			}
		}

		return ld_y;
	}

	//取得部门的最大宽度!
	public int getMaxDeptHeight() {
		DefaultGraphCell[] cells = getDeptCells(); //
		int li_maxHeight = 0; //
		for (int i = 0; i < cells.length; i++) {
			Rectangle2D rc2d = graph.getCellBounds(cells[i]);
			int li_height = (int) rc2d.getHeight(); //
			if (li_height > li_maxHeight) {
				li_maxHeight = li_height;
			}
		}
		return li_maxHeight + 4; //不知道为什么一定要加4个像素,一定是因为空余边界的原因造成的! 但不管怎么样,这样就对齐了!!
	}

	/**
	 * 取得所有阶段的组
	 * 
	 * @return
	 */
	public DefaultGraphCell[] getActivityCells() {
		GraphModel model = graph.getModel();
		int rootCount = model.getRootCount(); // 所有结点的数目
		ArrayList<DefaultGraphCell> list = new ArrayList<DefaultGraphCell>();
		for (int i = 0; i < rootCount; i++) {
			Object obj = model.getRootAt(i);
			if (obj instanceof DefaultGraphCell) {
				DefaultGraphCell cell = (DefaultGraphCell) obj; //
				Object linkedObject = cell.getUserObject();
				if (linkedObject instanceof ActivityVO) {
					Rectangle2D itempoint = graph.getCellBounds(cell); // 取得该组的位置
					double they = itempoint.getY();
					int index = 0;
					for (DefaultGraphCell now : list) {
						if (graph.getCellBounds(now).getY() >= they)
							break;
						index++;
					}
					list.add(index, cell);
				}
			}
		}
		return (DefaultGraphCell[]) list.toArray(new DefaultGraphCell[0]);
	}

	/**
	 * 取得所有环节VO
	 * @return
	 */
	public ActivityVO[] getActivityVOs() {
		GraphModel model = graph.getModel();
		int rootCount = model.getRootCount(); // 所有结点的数目
		ArrayList<ActivityVO> list = new ArrayList<ActivityVO>();
		for (int i = 0; i < rootCount; i++) {
			Object obj = model.getRootAt(i);
			if (obj instanceof DefaultGraphCell) {
				DefaultGraphCell cell = (DefaultGraphCell) obj; //
				Object linkedObject = cell.getUserObject();
				if (linkedObject instanceof ActivityVO) {
					ActivityVO linkedVO = (ActivityVO) linkedObject; //				
					list.add(linkedVO);
				}
			}
		}
		return (ActivityVO[]) list.toArray(new ActivityVO[0]); //
	}

	private int[] getNewDeptXandHeight() {
		int[] li_x_height = new int[] { 70, 420 }; //
		DefaultGraphCell[] deptCells = getDeptCells(); //
		if (deptCells != null && deptCells.length > 0) {
			Rectangle2D itempoint = graph.getCellBounds(deptCells[deptCells.length - 1]); // 取得该组的位置
			li_x_height[0] = (int) (itempoint.getX() + itempoint.getWidth() - 1); //取得最后一个组的位置
			li_x_height[1] = (int) itempoint.getHeight(); //
		}
		return li_x_height; //
	}

	/**
	 * 在底层增加部门
	 */
	private void onAddDept() {
		String str_deptname = "";
		String str_option = new TBUtil().getSysOptionStringValue("自定义流程图增加部门按钮的点击事件", null);
		if (str_option == null || str_option.equals("")) {
			//因为农行一图两表中没有模板主表子表、下拉框注册字典、平台参数配置表等，所以在本类中增加了个属性，根据属性来判断是否显示预设部门。
			ChoosePhaseDialog dialog = new ChoosePhaseDialog(this, "选择" + groupName, groupName + "名称", "常用流程图" + groupName, this.showpreviewdept); //
			dialog.setVisible(true); //
			if (dialog.getCloseType() != 1) {
				return; //
			}
			str_deptname = dialog.getPhaseName(); //
		} else {
			HashMap hashmap = new TBUtil().reflectCallCommMethod(str_option, null);
			if (hashmap == null) {
				return;
			}
			str_deptname = (String) hashmap.get("deptname");
		}
		int[] li_x_height = getNewDeptXandHeight(); //
		dealAddDept(str_deptname, null, li_x_height[0], 100, li_x_height[1]); //
		reSortDept(null); //重排机构
		reSetAllLayer(false); //设置机构和阶段在底层
	}

	/**
	 *  根据名称新增一个部门矩阵!!
	 * @param _deptName 部门名称!!
	 * @param _x  X坐标位置!
	 * @param _width 宽度
	 * @param _height 高度
	 */
	private GroupVO dealAddDept(String _deptName, String _posts, int _x, int _width, int _height) {
		GroupVO groupVO = new GroupVO(); //
		groupVO.setGrouptype("DEPT"); // 类型是部门
		groupVO.setCode(_deptName); //
		groupVO.setWfname(_deptName); //
		groupVO.setPosts(_posts);

		int li_y = 30; //y的位置不变!!
		groupVO.setX(new Integer(_x)); //
		groupVO.setY(new Integer(li_y)); //
		groupVO.setWidth(new Integer(_width)); //
		groupVO.setHeight(new Integer(_height)); //

		// 颜色与字体
		groupVO.setFonttype("System"); //
		groupVO.setFontsize(12);
		groupVO.setForeground("0,0,0"); //
		groupVO.setBackground(convertColorToStr(deptGroupBgcolor)); //

		addDeptGroup(groupVO); //
		groupcount++;
		return groupVO;
	}

	/**
	 * 增加部门框图(从左到右排列的)..
	 * 
	 * @return
	 */
	public void addDeptGroup(GroupVO _groupVO) {
		int li_x = _groupVO.getX().intValue(); //
		int li_y = _groupVO.getY().intValue(); //
		int li_width = _groupVO.getWidth().intValue(); //
		int li_height = _groupVO.getHeight().intValue(); //
		DefaultGraphCell cell1 = createVertex(_groupVO, li_x, li_y, li_width, li_height, this.deptGroupBgcolor, Color.BLACK, new Font("System", Font.PLAIN, 12), true, "cn.com.infostrategy.ui.workflow.design.CellView_Dept", false); //创建一个环节,宽75,高45!!!
		// 设置字体与颜色
		String str_fonttype = "System"; //
		int li_size = 12; //
		Color foreGround = Color.BLACK; //
		Color backGround = this.deptGroupBgcolor; //
		if (_groupVO.getFonttype() != null) {
			str_fonttype = _groupVO.getFonttype(); //
		}

		if (_groupVO.getFontsize() != null) {
			li_size = _groupVO.getFontsize(); //
		}

		if (_groupVO.getForeground() != null) {
			foreGround = convertStrToColor(_groupVO.getForeground()); //
		}

		if (_groupVO.getBackground() != null) {
			backGround = convertStrToColor(_groupVO.getBackground()); //
		}

		// 设置字体与颜色.
		GraphConstants.setFont(cell1.getAttributes(), new Font(str_fonttype, Font.PLAIN, li_size)); //
		GraphConstants.setForeground(cell1.getAttributes(), foreGround); //
		GraphConstants.setBackground(cell1.getAttributes(), backGround); //

		GraphConstants.setBounds(cell1.getAttributes(), new Rectangle2D.Double(li_x, li_y, li_width, li_height)); //
		GraphConstants.setMoveable(cell1.getAttributes(), true);
		GraphConstants.setSelectable(cell1.getAttributes(), true);
		graph.getGraphLayoutCache().insert(cell1); // 先加入组
		graph.getGraphLayoutCache().toBack(new DefaultGraphCell[] { cell1 }); // 放在后层
		isEditChanged = true; //表示已经变化了
	}

	/**
	 * 增加阶段框图(从上到下排列)..
	 */
	private void onAddstation() {
		DefaultGraphCell[] deptcells = this.getDeptCells();
		if (deptcells == null || deptcells.length == 0) { //如果没有部门框框就不允许画阶段
			MessageBox.show(this, "请先增加" + groupName + "!");
			return;
		}
		ChoosePhaseDialog dialog = new ChoosePhaseDialog(this, "选择阶段", "阶段名称", "常用流程图阶段", this.showpreviewdept); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() != 1) {
			return; //
		}

		String str_phasename = dialog.getPhaseName(); //
		GroupVO groupVO = new GroupVO(); //
		groupVO.setGrouptype("STATION"); // 类型是部门
		groupVO.setCode(str_phasename); //
		groupVO.setWfname(str_phasename); //

		int li_x = 10; //
		int li_y = 200 + this.stationcount * stationheight;//第一个阶段y位置为200,为了保证一次增加多个不完全重叠，要加上this.stationcount * stationheight
		int li_width = (int) getMaxDeptRightPos();
		int li_height = 40; //
		groupVO.setX(new Integer(li_x)); //
		groupVO.setY(new Integer(li_y)); //
		groupVO.setWidth(new Integer(li_width - li_x)); //
		groupVO.setHeight(new Integer(li_height));
		groupVO.setFonttype("System"); //
		groupVO.setFontsize(12);
		groupVO.setForeground("0,0,0"); //
		groupVO.setBackground("0,0,0"); //		

		addStationGroup(groupVO); //
		if (this.stationBackGroundCell == null) { //如果还没有画阶段的背景,则立即画上!!
			addStationBackGroundActivity(); //
		} else {
			getGraph().getGraphLayoutCache().toBack(new Object[] { stationBackGroundCell }); //一定要在背后!
		}
		reSortStation(null);//农行提出要重置机构高度
		reSetAllLayer(true); //
	}

	/**
	 * 增加机构与岗位,航天科工项目中提出,部门与岗位要一起加入,就是模拟迪博的一图两表,将岗位放在第二行,机构放在第一行!
	 * 原理就是先插入一个部门组,然后紧贴着部门组创建三个岗位!!
	 */
	private void onAddDeptAndPost() {
		BillVO deptbillvo = null;
		BillVO[] postbillvos = null;
		String str_option = new TBUtil().getSysOptionStringValue("自定义流程图增加部门和岗位按钮的点击事件", null);
		if (str_option == null) {
			return;
		} else {
			HashMap hashmap = new TBUtil().reflectCallCommMethod(str_option, null);
			if (hashmap == null) {
				return;
			}
			deptbillvo = (BillVO) hashmap.get("deptbillvo");
			postbillvos = (BillVO[]) hashmap.get("postbillvos");
		}
		String deptname = deptbillvo.getStringValue("name");
		Object obj = getSelectedDeptCell();
		if (obj != null) { //如果选中了机构,就在该机构上增加岗位，不再增加机构
			DefaultGraphCell cell = (DefaultGraphCell) obj;
			if (postbillvos == null || postbillvos.length == 0) {
				return;
			}
			GroupVO groupvo_old = (GroupVO) cell.getUserObject();
			GroupVO groupvo_new = new GroupVO();
			try {
				BeanUtils.copyProperties(groupvo_new, groupvo_old);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			String str_posts = (groupvo_new.getPosts() == null ? "" : groupvo_new.getPosts() + ";");
			for (int i = 0; i < postbillvos.length; i++) {
				str_posts = str_posts + postbillvos[i].getStringValue("name") + ";";
			}
			if (str_posts.length() > 0) {
				groupvo_new.setPosts(str_posts.substring(0, str_posts.length() - 1));
			}

			/**
			 * 【sunfujun/20120417/undo bug修改】
			 */

			createUndoEvent(cell, groupvo_new);
			cell.setUserObject(groupvo_new);
			resetMyPosAndSize(cell);
		} else {//如果没有选中机构，就新增机构和岗位
			String str_posts = "";
			if (postbillvos != null && postbillvos.length > 0) {
				for (int i = 0; i < postbillvos.length; i++) {
					str_posts = str_posts + postbillvos[i].getStringValue("name") + ";";
				}
			}
			int[] li_x_height = getNewDeptXandHeight(); //取得新有机构组的X坐标与高度!
			if (str_posts.length() > 0) {
				str_posts = str_posts.substring(0, str_posts.length() - 1);
			}
			dealAddDept(deptname, str_posts, li_x_height[0], 100, li_x_height[1]); //
			reSortDept(null); //重排机构
			reSetAllLayer(false); //设置机构和阶段在底层
		}
	}

	public void createUndoEvent(DefaultGraphCell cell, Object newValue) {//【sunfujun/20120419/undo问题优化】
		Map attr = (Map) cell.getAttributes().clone();
		GraphConstants.setValue(attr, newValue);
		Map attrs = new HashMap();
		attrs.put(cell, attr);
		attrs.put(new DefaultGraphCell(), new HashMap());
		graph.getGraphLayoutCache().edit(attrs, null, null, null);
	}

	/**
	 * 取得纵向部门的矩阵..
	 * 
	 * @return
	 */
	public void addStationGroup(GroupVO _groupVO) {
		int li_x = _groupVO.getX().intValue(); //
		int li_y = _groupVO.getY().intValue(); //
		int li_width = _groupVO.getWidth().intValue(); //
		int li_height = _groupVO.getHeight().intValue(); //

		DefaultGraphCell cell1 = getCell(_groupVO.getWfname(), Color.BLACK, "cn.com.infostrategy.ui.workflow.design.CellView_Station"); // 定义一个组的名称

		GraphConstants.setBounds(cell1.getAttributes(), new Rectangle2D.Double(li_x, li_y, li_width, li_height)); // 定义大小
		GraphConstants.setEditable(cell1.getAttributes(), true);
		GraphConstants.setSelectable(cell1.getAttributes(), true); //
		GraphConstants.setMoveable(cell1.getAttributes(), true); //
		GraphConstants.setFont(cell1.getAttributes(), new Font(_groupVO.getFonttype(), Font.PLAIN, _groupVO.getFontsize())); // 设置字体

		String[] str_color = _groupVO.getForeground().split(",");
		GraphConstants.setForeground(cell1.getAttributes(), new Color(Integer.parseInt(str_color[0]), Integer.parseInt(str_color[1]), Integer.parseInt(str_color[2])));// 设置前景颜色.
		str_color = _groupVO.getBackground().split(",");
		GraphConstants.setBackground(cell1.getAttributes(), new Color(Integer.parseInt(str_color[0]), Integer.parseInt(str_color[1]), Integer.parseInt(str_color[2])));//设置后景颜色
		GraphConstants.setBorder(cell1.getAttributes(), BorderFactory.createLineBorder(Color.BLACK, 0)); // 边框
		cell1.setUserObject(_groupVO);
		graph.getGraphLayoutCache().insert(cell1); //
		graph.getGraphLayoutCache().toBack(new DefaultGraphCell[] { cell1 }); // 放在后层
		isEditChanged = true; //表示已经变化了
		stationcount++; //
	}

	//增加阶段的背景框!即那个整流罩!!!
	private void addStationBackGroundActivity() {
		int li_x = 10; //X位置是固定的
		int li_y = 30; //Y位置也是固定的
		int li_width = 61; //宽度也是固定的
		int li_height = getMaxDeptHeight(); //
		String str_cellviewname = "cn.com.infostrategy.ui.workflow.design.CellView_StationBackCell"; // 显示的图标
		stationBackGroundCell = createVertex(null, li_x, li_y, li_width, li_height, deptGroupBgcolor, Color.BLACK, new Font("宋体", Font.PLAIN, 12), true, str_cellviewname, false); //
		stationBackGroundCell.getAttributes().put("工作流编辑器", this); //

		GroupVO groupVO = new GroupVO(); //
		groupVO.setGrouptype("STATIONBACK"); // 类型是部门
		groupVO.setCode(null); //
		groupVO.setWfname(null); //

		groupVO.setX(li_x); //
		groupVO.setY(li_y); //
		groupVO.setWidth(li_width); //
		groupVO.setHeight(li_height); //

		// 颜色与字体
		groupVO.setFonttype("System"); //
		groupVO.setFontsize(12);
		groupVO.setForeground("0,0,0"); //
		groupVO.setBackground(convertColorToStr(deptGroupBgcolor)); //
		stationBackGroundCell.setUserObject(groupVO); //

		GraphConstants.setBorder(stationBackGroundCell.getAttributes(), BorderFactory.createLineBorder(Color.BLACK, 1));
		GraphConstants.setSelectable(stationBackGroundCell.getAttributes(), false); //
		getGraph().getGraphLayoutCache().insert(stationBackGroundCell); // 加入面板

		getGraph().getGraphLayoutCache().toBack(new Object[] { stationBackGroundCell }); //一定要在背后!
	}

	//在删除阶段时,每次都计算下剩下的阶段,如果没有了,则自动删除阶段的背景

	private void delStationBackGroundActivityAsAuto() {//【sunfujun,20120411,修正undo的bug——王宁】
		updateStationBackGroundActivityAsAuto();
	}

	private void updateStationBackGroundActivityAsAuto() {//【sunfujun,20120411,修正undo的bug——王宁】
		DefaultGraphCell[] stationCells = getStationCells(); //
		if (stationCells == null || stationCells.length == 0) {
			this.stationcount = 0;
			if (stationBackGroundCell != null) {
				graph.getModel().remove(new Object[] { stationBackGroundCell }); //
				stationBackGroundCell = null; //一定要置为空,否则重新加载时会有问题!
			}
			graph.getGraphLayoutCache().toBack(new Object[] { titleCell });//需要设置一下，否则重叠的环节顺序就错了【李春娟/2012-05-22】
		} else {
			this.stationcount = stationCells.length;
			if (this.stationBackGroundCell != null) { //如果还没有
				getGraph().getGraphLayoutCache().toBack(new Object[] { stationBackGroundCell }); //一定要在背后!
			} else {
				addStationBackGroundActivity();
			}
		}
	}

	/**
	 * 取得所有部门的组,从左至右排列...
	 * 
	 * @return
	 */
	public DefaultGraphCell[] getDeptCells() {
		return getDeptCells(null);
	}

	public DefaultGraphCell[] getDeptCells(Map attrs) {
		GraphModel model = graph.getModel();
		int rootCount = model.getRootCount(); // 所有结点的数目
		ArrayList<DefaultGraphCell> list = new ArrayList<DefaultGraphCell>();
		for (int i = 0; i < rootCount; i++) {
			Object obj = model.getRootAt(i);
			if (obj instanceof DefaultGraphCell) {
				DefaultGraphCell cell = (DefaultGraphCell) obj; //
				Object linkedObject = cell.getUserObject();
				if (linkedObject instanceof GroupVO) {
					GroupVO gvo = (GroupVO) linkedObject; //
					if (gvo.getGrouptype().equals("DEPT")) { // 如果类型是部门的
						Rectangle2D itempoint = graph.getCellBounds(cell); // 取得该组的位置
						double thex = itempoint.getX();
						int index = 0;
						for (DefaultGraphCell now : list) {
							if (graph.getCellBounds(now).getX() >= thex)
								break;
							index++;
						}
						list.add(index, cell);
						if (attrs != null && !attrs.containsKey(cell)) {//【sunfujun/20120417/undobug修改】
							GroupVO gvo_new = new GroupVO();
							try {
								BeanUtils.copyProperties(gvo_new, gvo);
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							Map attr = (Map) cell.getAttributes().clone();
							GraphConstants.setValue(attr, gvo_new);
							attrs.put(cell, attr);
							attrs.put(new DefaultGraphCell(), new HashMap());
							cell.setUserObject(gvo_new);
						}
					}
				}
			}
		}
		return (DefaultGraphCell[]) list.toArray(new DefaultGraphCell[0]);
	}

	/**
	 * 取得所有阶段的组
	 * 
	 * @return
	 */
	public DefaultGraphCell[] getStationCells() {
		return getStationCells(null);
	}

	public DefaultGraphCell[] getStationCells(Map attrs) {
		GraphModel model = graph.getModel();
		int rootCount = model.getRootCount(); // 所有结点的数目
		ArrayList<DefaultGraphCell> list = new ArrayList<DefaultGraphCell>();
		for (int i = 0; i < rootCount; i++) {
			Object obj = model.getRootAt(i);
			if (obj instanceof DefaultGraphCell) {
				DefaultGraphCell cell = (DefaultGraphCell) obj; //
				Object linkedObject = cell.getUserObject();
				if (linkedObject instanceof GroupVO) {
					GroupVO gvo = (GroupVO) linkedObject; //
					if (gvo.getGrouptype().equals("STATION")) { // 如果类型是部门的
						Rectangle2D itempoint = graph.getCellBounds(cell); // 取得该组的位置
						double they = itempoint.getY();
						int index = 0;
						for (DefaultGraphCell now : list) {
							if (graph.getCellBounds(now).getY() >= they)
								break;
							index++;
						}
						list.add(index, cell);
						if (attrs != null && !attrs.containsKey(cell))
							attrs.put(cell, cell.getAttributes());
					}
				}
			}
		}
		return (DefaultGraphCell[]) list.toArray(new DefaultGraphCell[0]);
	}

	//取得所有阶段的属性,在画阶段的背景时需要!!!
	public String[][] getStationCellsInfo() {
		DefaultGraphCell[] cells = getStationCells(); //
		if (cells == null || cells.length == 0) {
			return null; //
		}
		String[][] str_return = new String[cells.length][6]; //
		for (int i = 0; i < cells.length; i++) {
			Rectangle2D rc2d = graph.getCellBounds(cells[i]);
			int li_y = (int) rc2d.getY(); //Y上的位置!
			int li_height = (int) rc2d.getHeight(); //高度!
			GroupVO groupvo = (GroupVO) cells[i].getUserObject();
			str_return[i] = new String[] { groupvo.getWfname(), "" + li_y, "" + li_height, groupvo.getFonttype(), "" + groupvo.getFontsize(), groupvo.getForeground() }; //
		}
		return str_return; //
	}

	public DefaultGraphCell getCell(Object _userObject, Color _color, String _viewclass) {
		// Construct Vertex with no Label

		DefaultGraphCell cell = new DefaultGraphCell(_userObject == null ? "" : _userObject); //
		if (_viewclass != null) {
			GPCellViewFactory.setViewClass(cell.getAttributes(), _viewclass);
		}

		// Add one Floating Port
		// cell.addPort();

		// Add a Bounds Attribute to the Map
		// GraphConstants.setBounds(cell.getAttributes(), new
		// Rectangle2D.Double(point.getX(), point.getY(), 0, 0));

		// Make sure the cell is resized on insert
		// GraphConstants.setResize(cell.getAttributes(), true);
		// Add a nice looking gradient background

		// GraphConstants.setGradientColor(cell.getAttributes(), _color);

		// GraphConstants.setMoveable(cell.getAttributes(), false); //
		GraphConstants.setSelectable(cell.getAttributes(), false); //
		GraphConstants.setResize(cell.getAttributes(), false); //

		// Add a Border Color Attribute to the Map
		GraphConstants.setBorderColor(cell.getAttributes(), Color.black);
		// Add a White Background
		GraphConstants.setBackground(cell.getAttributes(), _color);

		// GraphConstants.setOpaque(cell.getAttributes(), true);

		// Make Vertex Opaque
		if (_userObject != null) {
			GraphConstants.setOpaque(cell.getAttributes(), true);
		} else {
			GraphConstants.setOpaque(cell.getAttributes(), false);
		}
		// Insert the Vertex (including child port and attributes)
		return cell; //
		// graph.getGraphLayoutCache().insert(vertex);
	}

	/**
	 * 锁定底层!!
	 */
	public void onLockGroup() {
		boolean bo_lock = false;
		if (checkBox_lockgroup != null) {
			if (checkBox_lockgroup.isSelected()) {
				bo_lock = false;
			} else {
				bo_lock = true;
			}
		}
		dealGroupSelectable(bo_lock); ////
	}

	//锁定底层
	public void lockGroup() {
		dealGroupSelectable(false); //
	}

	//解锁底层
	public void unLockGroup() {
		dealGroupSelectable(true); //
	}

	//锁定底层,并且只能做选择操作!适用于浏览状态查看时!需要根据连线是否可移动屏蔽右键的编辑删除复制等操作，而以前弄两个方法根本没必要，并且都没有屏蔽右键操作
	public void lockGroupAndOnlyDoSelect() {
		lockGroup(); //锁定底层
		getGraph().setEdgeLabelsMovable(false); //
		getGraph().setMoveable(false); //设置不能移动框框
		getGraph().setSelectionEnabled(true); //
		KeyListener[] keyListeners = getGraph().getKeyListeners(); //删除所有快捷键,因为有可能会做Delete键!!
		for (int i = 0; i < keyListeners.length; i++) {
			getGraph().removeKeyListener(keyListeners[i]); //
		}
	}

	private void dealGroupSelectable(boolean _locked) {
		DefaultGraphCell[] cells = getDeptCells(); // 取得所有部门组
		if (cells != null && cells.length > 0) {
			for (int i = 0; i < cells.length; i++) {
				GraphConstants.setSelectable(cells[i].getAttributes(), _locked); //
				GraphConstants.setMoveable(cells[i].getAttributes(), _locked); //
			}
			graph.getGraphLayoutCache().toBack(cells); // 放在后层
		}

		DefaultGraphCell[] cells_station = getStationCells(); // 取得所有阶段
		if (cells_station != null && cells_station.length > 0) {
			for (int i = 0; i < cells_station.length; i++) {
				GraphConstants.setSelectable(cells_station[i].getAttributes(), _locked); //
				GraphConstants.setMoveable(cells_station[i].getAttributes(), _locked); //
			}
			graph.getGraphLayoutCache().toBack(cells_station); // 放在后层
		}

		graph.clearSelection(); //
		//graph.getGraphLayoutCache().reload(); // 一定要执行,否则会出现多余的框!!?? 测试没发现多余的框，倒是发现一个bug，如果点击锁定底层则有的连线跑到环节（有重叠环节的时候）后面去了，故不执行此句【李春娟/2012-05-21】

		if (this.stationBackGroundCell != null) { //如果还没有
			getGraph().getGraphLayoutCache().toBack(new Object[] { stationBackGroundCell }); //一定要在背后!
		}
	}

	/**
	 * 环节
	 */
	public void updateCellText(DefaultGraphCell _cell) {
		int li_x = (int) GraphConstants.getBounds(_cell.getAttributes()).getX(); //
		int li_y = (int) GraphConstants.getBounds(_cell.getAttributes()).getY(); //
		Point point = new Point(li_x, li_y);
		SwingUtilities.convertPointToScreen(point, graph); //

		Font cellFont = GraphConstants.getFont(_cell.getAttributes()); //
		Color cellForecolor = GraphConstants.getForeground(_cell.getAttributes()); //
		Color cellBackcolor = GraphConstants.getBackground(_cell.getAttributes()); //

		ActivityVO acvo_old = (ActivityVO) _cell.getUserObject(); //
		CellConfigDialog dialog = new CellConfigDialog(this, "修改环节", (int) point.getX(), (int) point.getY(), cellFont, cellForecolor, cellBackcolor, acvo_old.getWfname(), acvo_old.getUiname(), acvo_old.getViewtype(), acvo_old.getImgstr(), null, "ACTIVITY");
		dialog.setCanSetCellColor(this.isCanSetCellColor);
		dialog.setLocation(400, 200);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			String str_text = dialog.getContentStr(); //
			ActivityVO acvo_new = new ActivityVO();
			try {
				BeanUtils.copyProperties(acvo_new, acvo_old);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			acvo_new.setWfname(str_text); //
			acvo_new.setUiname(dialog.getSeq()); //
			acvo_new.setFonttype(dialog.getFontType()); //
			acvo_new.setFontsize(dialog.getFontSize()); //
			acvo_new.setImgstr(dialog.getImgstr());
			acvo_new.setForeground(convertColorToStr(dialog.getForeGround())); //
			acvo_new.setBackground(convertColorToStr(dialog.getBackGround())); //
			acvo_new.setActivitytype("NORMAL");
			// 修改环节类型
			int viewtype = dialog.getViewType();
			acvo_new.setViewtype(viewtype);

			/**
			 * 使得普通环节的编辑支持undo（不包括环节类型）【sunfujun/20120416/undo bug修改】
			 */
			createUndoEvent(_cell, acvo_new);

			// 设置字体
			GraphConstants.setFont(_cell.getAttributes(), new Font(dialog.getFontType(), Font.PLAIN, dialog.getFontSize())); //

			// 设置前景颜色.
			GraphConstants.setForeground(_cell.getAttributes(), dialog.getForeGround());//

			// 设置后景颜色
			GraphConstants.setBackground(_cell.getAttributes(), dialog.getBackGround());//

			String str_viewtypename = "cn.com.infostrategy.ui.workflow.design.CellView_" + viewtype; //
			GPCellViewFactory.setViewClass(_cell.getAttributes(), str_viewtypename);
			getGraph().setSelectionCell(_cell); //必须得有这句，否则移动环节时环节类型不变，再移动部门，环节类型又变回去了
			if (viewtype == 8) {//图片环节不需要边框只有流程监控时候可以有
				GraphConstants.setBorder(_cell.getAttributes(), BorderFactory.createEmptyBorder());
			} else {
				GraphConstants.setBorder(_cell.getAttributes(), BorderFactory.createLineBorder(Color.BLACK, 1));
			}

			graph.getGraphLayoutCache().reload(); //
			graph.repaint(); //
			isEditChanged = true; //表示已经变化了
		}
	}

	/**
	 * 修改连线名称
	 */
	public void updateTransitionText(DefaultGraphCell _cell) {
		DefaultEdge edge = (DefaultEdge) _cell; //
		DefaultPort port_source = (DefaultPort) edge.getSource(); // 连线源环节
		DefaultGraphCell cell_source = (DefaultGraphCell) port_source.getParent(); //
		int li_x = (int) GraphConstants.getBounds(cell_source.getAttributes()).getX(); //
		int li_y = (int) GraphConstants.getBounds(cell_source.getAttributes()).getY(); //

		Point point = new Point(li_x, li_y);
		SwingUtilities.convertPointToScreen(point, graph); //
		Font cellFont = GraphConstants.getFont(_cell.getAttributes()); //
		Color cellForecolor = GraphConstants.getForeground(_cell.getAttributes()); //
		Color cellBackcolor = GraphConstants.getLineColor(_cell.getAttributes()); //
		TransitionVO transitionVO_old = (TransitionVO) _cell.getUserObject(); //
		int lineType = GraphConstants.getLineEnd(_cell.getAttributes());
		boolean isSingle = (GraphConstants.getLineBegin(_cell.getAttributes()) == 0 && !GraphConstants.isBeginFill(_cell.getAttributes())) ? true : false;
		CellConfigDialog dialog = new CellConfigDialog(this, "修改连线", (int) point.getX(), (int) point.getY(), cellFont, cellForecolor, cellBackcolor, transitionVO_old.getWfname(), null, 0, null, null, "TRANSITION", lineType, isSingle); //
		dialog.setCanSetCellColor(this.isCanSetCellColor);
		dialog.setLocation(400, 200);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			String str_text = dialog.getContentStr(); //
			TransitionVO transitionVO_new = new TransitionVO();
			try {
				BeanUtils.copyProperties(transitionVO_new, transitionVO_old);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			transitionVO_new.setWfname(str_text); //
			transitionVO_new.setFonttype(dialog.getFontType()); //
			transitionVO_new.setFontsize(dialog.getFontSize()); //
			transitionVO_new.setForeground(convertColorToStr(dialog.getForeGround())); //
			transitionVO_new.setBackground(convertColorToStr(dialog.getBackGround())); //
			transitionVO_new.setSingle(dialog.isSingle());
			transitionVO_new.setLineType(dialog.getLineType());
			/**
			 * 使得连线的编辑支持undo【sunfujun/20120416/undo bug修改】
			 */
			createUndoEvent(_cell, transitionVO_new);
			// 设置字体
			GraphConstants.setFont(_cell.getAttributes(), new Font(dialog.getFontType(), Font.PLAIN, dialog.getFontSize())); //

			// 设置前景颜色.
			GraphConstants.setForeground(_cell.getAttributes(), dialog.getForeGround());//

			// 设置连线颜色
			GraphConstants.setLineColor(_cell.getAttributes(), dialog.getBackGround());//

			//字体不会出现歪斜现象！
			//GraphConstants.setBounds(edge.getAttributes(), new Rectangle2D.Double(500,500,50,50));
			String charOld = transitionVO_old.getWfname();//袁江晓   20120918修改 先获得之前的连线上是否有字体，如果有则不改变位置，如果没有则设置添加字体的位置
			GraphConstants.setAutoSize(edge.getAttributes(), true);
			;//袁江晓   20120918修改 设置自动大小
			if (null == charOld || (null != charOld && charOld.trim().equals(""))) {
				GraphConstants.setLabelPosition(edge.getAttributes(), new Point2D.Double(20, 10));//袁江晓   20120918修改 设置标签的位置
			}
			GraphConstants.setLabelAlongEdge(edge.getAttributes(), true); //如果为True则字上斜着的贴着线
			GraphConstants.setLineEnd(edge.getAttributes(), transitionVO_new.getLineType());
			GraphConstants.setEndFill(edge.getAttributes(), true);//【sunfujun/20120426/增加箭头类型的配置_王钢】
			if (transitionVO_new.isSingle()) {
				GraphConstants.setLineBegin(edge.getAttributes(), GraphConstants.ARROW_NONE);
				GraphConstants.setBeginFill(edge.getAttributes(), false);
			} else {
				GraphConstants.setLineBegin(edge.getAttributes(), transitionVO_new.getLineType());
				GraphConstants.setBeginFill(edge.getAttributes(), true);
			}
			graph.getGraphLayoutCache().reload(); //
			graph.repaint(); //
			isEditChanged = true; //表示已经变化了
		}
	}

	/** 
	* @param _cell
	*/
	public void updateGroupDeptCellText(DefaultGraphCell _cell) {
		GroupVO groupVO_old = (GroupVO) _cell.getUserObject(); //

		int li_x = (int) GraphConstants.getBounds(_cell.getAttributes()).getX(); //
		int li_y = (int) GraphConstants.getBounds(_cell.getAttributes()).getY(); //

		Point point = new Point(li_x, li_y);
		SwingUtilities.convertPointToScreen(point, graph); //

		Font cellFont = GraphConstants.getFont(_cell.getAttributes()); //
		Color cellForecolor = GraphConstants.getForeground(_cell.getAttributes()); //
		Color cellBackcolor = GraphConstants.getBackground(_cell.getAttributes()); //

		CellConfigDialog dialog = new CellConfigDialog(this, "修改" + groupName, (int) point.getX(), (int) point.getY(), cellFont, cellForecolor, cellBackcolor, groupVO_old.getWfname(), null, 0, null, groupVO_old.getPosts(), "DEPT"); //
		dialog.setCanSetCellColor(this.isCanSetCellColor);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			String str_newtext = dialog.getContentStr(); //
			GroupVO groupVO_new = new GroupVO();
			try {
				BeanUtils.copyProperties(groupVO_new, groupVO_old);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			groupVO_new.setFonttype(dialog.getFontType()); //
			groupVO_new.setFontsize(dialog.getFontSize()); //
			groupVO_new.setForeground(convertColorToStr(dialog.getForeGround())); //
			groupVO_new.setBackground(convertColorToStr(dialog.getBackGround())); //	
			groupVO_new.setWfname(str_newtext); //
			groupVO_new.setPosts(dialog.getPostsStr());

			/**
			 * 使得部门的编辑支持undo【sunfujun/20120416/undo bug修改】
			 */
			createUndoEvent(_cell, groupVO_new);
			_cell.setUserObject(groupVO_new);

			GraphConstants.setFont(_cell.getAttributes(), new Font(dialog.getFontType(), Font.PLAIN, dialog.getFontSize())); // 设置字体
			GraphConstants.setForeground(_cell.getAttributes(), dialog.getForeGround());// 设置前景颜色.
			GraphConstants.setBackground(_cell.getAttributes(), dialog.getBackGround());//设置后景颜色

			graph.getGraphLayoutCache().reload(); //
			graph.repaint(); //
			isEditChanged = true; // 表示已经变化了
		}
	}

	/**
	* 
	* @param _cell
	*/
	public void updateGroupCellText(DefaultGraphCell _cell) {
		GroupVO groupVO_old = (GroupVO) _cell.getUserObject(); //

		int li_x = (int) GraphConstants.getBounds(_cell.getAttributes()).getX(); //
		int li_y = (int) GraphConstants.getBounds(_cell.getAttributes()).getY(); //

		Point point = new Point(li_x, li_y);
		SwingUtilities.convertPointToScreen(point, graph); //

		Font cellFont = GraphConstants.getFont(_cell.getAttributes()); //
		Color cellForecolor = GraphConstants.getForeground(_cell.getAttributes()); //
		Color cellBackcolor = GraphConstants.getBackground(_cell.getAttributes()); //

		CellConfigDialog dialog = new CellConfigDialog(this, "修改阶段", (int) point.getX(), (int) point.getY(), cellFont, cellForecolor, cellBackcolor, groupVO_old.getWfname(), null, 0, null, null, "STATION"); //
		dialog.setCanSetCellColor(this.isCanSetCellColor);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			String str_newtext = dialog.getContentStr(); //
			GroupVO groupVO_new = new GroupVO();
			try {
				BeanUtils.copyProperties(groupVO_new, groupVO_old);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			groupVO_new.setWfname(str_newtext); //
			groupVO_new.setFonttype(dialog.getFontType()); //
			groupVO_new.setFontsize(dialog.getFontSize()); //
			groupVO_new.setForeground(convertColorToStr(dialog.getForeGround())); //
			groupVO_new.setBackground(convertColorToStr(dialog.getBackGround())); //	

			/**
			 * 使得阶段的编辑支持undo【sunfujun/20120416/undo bug修改】
			 */
			createUndoEvent(_cell, groupVO_new);
			_cell.setUserObject(groupVO_new);

			GraphConstants.setFont(_cell.getAttributes(), new Font(dialog.getFontType(), Font.PLAIN, dialog.getFontSize())); // 设置字体
			GraphConstants.setForeground(_cell.getAttributes(), dialog.getForeGround());// 设置前景颜色.
			GraphConstants.setBackground(_cell.getAttributes(), dialog.getBackGround());//设置后景颜色

			graph.getGraphLayoutCache().reload(); //
			graph.repaint(); //
			isEditChanged = true; // 表示已经变化了
		}
	}

	public ProcessVO getCurrentProcessVO() {
		return currentProcessVO;
	}

	public boolean isEditChanged() {
		return isEditChanged;
	}

	/**
	 * 增加一个结点
	 */
	private void onInsertNormalCell(int _type, double _x, double _y) {
		cellNumber = cellNumber + 1;
		String str_code = "WFA" + cellNumber; //
		String str_wfname = "环节" + cellNumber; //
		String str_uiname = "" + cellNumber; //
		dealInsertNormalCell(_type, _x, _y, 70, 45, str_code, str_wfname, str_uiname); //创建一个环节!!
	}

	/**
	 * 根据名称插入一个环节!!
	 * @param _type
	 * @param _x
	 * @param _y
	 * @param _code
	 * @param _name
	 */
	private void dealInsertNormalCell(int _type, double _x, double _y, int _width, int _height, String _code, String _wfname, String _uiname) {
		ActivityVO activityVO = new ActivityVO();
		Integer activityid = getNewActivityID();
		activityVO.setId(activityid); //关键!! 取得环节的主键,目前的原理是在前台直接生成环节主键!!! 这样在后台保存就会快许多!!! 而且不会重复!! 缺点是第一个环节可能会有点慢!!!
		addActivityIds.add(activityid + "");
		activityVO.setActivitytype("NORMAL"); // 环节类型
		activityVO.setCode(_code); // 代码
		activityVO.setWfname(_wfname); // 流程图中的显示名称
		activityVO.setUiname(_uiname); // 排序的序号
		activityVO.setX(new Integer((int) _x)); // X
		activityVO.setY(new Integer((int) _y)); // Y
		activityVO.setApprovemodel("1"); // 1表示抢占
		activityVO.setAutocommit("N"); // 不自动提交
		activityVO.setIsassignapprover("N"); // 是否需指定审批人!!
		activityVO.setShowparticimode("1"); //
		activityVO.setDesc(""); // 描述
		activityVO.setViewtype(new Integer(_type)); //
		activityVO.setWidth(_width);
		activityVO.setHeight(_height);
		activityVO.setFonttype("System"); //
		activityVO.setFontsize(12);
		activityVO.setForeground("0,0,0"); //
		activityVO.setBackground("232,238,247"); //			
		activityVO.setImgstr("workflow/start.gif");
		String str_cellviewname = "cn.com.infostrategy.ui.workflow.design.CellView_" + _type; // 显示的图标
		DefaultGraphCell cell = createVertex(activityVO, _x - 30, _y - 17, _width, _height, normalCellBgcolor, Color.BLACK, new Font("System", Font.PLAIN, 12), true, str_cellviewname); //创建一个环节,宽75,高45!!!
		getGraph().getGraphLayoutCache().insert(cell); // 加入面板
		getGraph().getGraphLayoutCache().toFront(new Object[] { cell }); //
		getGraph().setSelectionCell(cell); // //
		isEditChanged = true; //表示已经变化了
	}

	/**
	 * 复制粘贴一个环节
	 * @param _type
	 * @param _x
	 * @param _y
	 * @param _width
	 * @param _height
	 * @param _code
	 * @param _name
	 */
	private DefaultGraphCell dealPasteNormalCell(DefaultGraphCell cell, int _x, int _y) {

		try {
			ActivityVO _activityVO = (ActivityVO) cell.getUserObject();
			ActivityVO activityVO = (ActivityVO) new TBUtil().deepClone(_activityVO);
			activityVO.setRiskVO(null);//不复制风险点
			Integer activityid = getNewActivityID();
			activityVO.setId(activityid);//以前这里又重新获得一下id，activityVO.setId(getNewActivityID())，bug!!!【李春娟/2012-05-22】
			addActivityIds.add(activityid + "");
			activityVO.setX(_x);
			_activityVO.setY(_y);
			String str_cellviewname = "cn.com.infostrategy.ui.workflow.design.CellView_" + activityVO.getViewtype(); // 显示的图标
			DefaultGraphCell cellnew = createVertex(activityVO, _x, _y, GraphConstants.getBounds(cell.getAttributes()).getWidth(), GraphConstants.getBounds(cell.getAttributes()).getHeight(), GraphConstants.getBackground(cell.getAttributes()), GraphConstants.getForeground(cell.getAttributes()), GraphConstants.getFont(cell.getAttributes()), true, str_cellviewname); //创建一个环节,宽75,高45!!!
			getGraph().getGraphLayoutCache().insert(cellnew); // 加入面板
			getGraph().getGraphLayoutCache().toFront(new Object[] { cellnew }); //
			isEditChanged = true; //表示已经变化了
			return cellnew;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void addWorkFlowDoubleClickCellListener(WorkFlowDoubleClickCellListener _listeer) {
		vector_celldoubleclickListener.add(_listeer); //
	}

	/**
	 * 增加选择监听事件..
	 * 
	 * @param _listeer
	 */
	public void addWorkFlowCellSelectedListener(WorkFlowCellSelectedListener _listeer) {
		vector_cellSelectedListener.add(_listeer); //
	}

	/**
	 * 双击某个结点...
	 * 
	 * @param _x
	 * @param _y
	 */
	private void onDoubleClickCell(int _x, int _y) {
		Object obj = graph.getSelectionCell(); //
		if (obj != null) {
			if (obj instanceof DefaultGraphCell) {
				DefaultGraphCell cell = (DefaultGraphCell) obj;
				Object userobj = cell.getUserObject(); //
				for (int i = 0; i < vector_celldoubleclickListener.size(); i++) {
					WorkFlowDoubleClickCellListener listener = (WorkFlowDoubleClickCellListener) vector_celldoubleclickListener.get(i); //
					listener.onWorkFlowDoubleClickCell(new WorkFlowDoubleClickCellEvent(this, (ActivityVO) userobj));
				}
			}
		}
	}

	/**
	 * 右键弹出菜单...
	 * @param _x
	 * @param _y
	 */
	private void showPopMenu(int _x, int _y) {
		if (isDragging) {
			return; //
		}
		final int x = _x;
		final int y = _y;
		Object obj = graph.getSelectionCell(); //
		if (obj == null && stationBackGroundCell != null) {//如果是点击了阶段名称，阶段框是默认不选中的，需要触发选中事件，然后进行编辑
			Rectangle2D stationpoint = graph.getCellBounds(stationBackGroundCell);
			if (x >= stationpoint.getX() && x <= stationpoint.getX() + stationpoint.getWidth() && y >= stationpoint.getY() && y <= stationpoint.getY() + stationpoint.getHeight()) {
				DefaultGraphCell[] stationCells = getStationCells(); //
				for (int i = 0; i < stationCells.length; i++) {
					DefaultGraphCell itemcell = stationCells[i]; // 取得其中的Cell
					Rectangle2D itempoint = graph.getCellBounds(itemcell); // 取得该部门的位置
					if (itempoint.getY() >= y) {
						graph.setSelectionCell(itemcell);
						break;
					}
				}
			}
		}
		obj = graph.getSelectionCell(); //
		JPopupMenu popMenu = new JPopupMenu(); //

		if (obj != null) {
			if (!graph.getEdgeLabelsMovable()) {//如果选中了控件，但连线根本不能移动（即流程图不可编辑）则直接返回
				return;
			}
			if (obj instanceof DefaultGraphCell) {
				final DefaultGraphCell cell = (DefaultGraphCell) obj;
				Object userobj = cell.getUserObject(); //
				JMenuItem item_0 = new JMenuItem("编辑"); //
				item_0.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						editCellConfig(); //
					}
				});
				JMenuItem item_1 = new JMenuItem("删除"); //
				item_1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onDelCell(); //
					}
				});
				popMenu.add(item_0); //
				popMenu.add(item_1); //
				if (userobj instanceof ActivityVO) {
					JMenuItem item_2 = new JMenuItem("复制"); //
					item_2.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							copyCells(); //
						}
					}); //
					popMenu.add(item_2); //
				}
			}
		} else {
			JMenu menu_export = new JMenu("导出流程图"); //
			final JMenuItem menuitem_export_jpg = new JMenuItem("JPG图片格式"); //
			final JMenuItem menuitem_export_bmp = new JMenuItem("BMP图片格式(高清)"); //
			final JMenuItem menuitem_export_word = new JMenuItem("Word文件格式"); //
			final JMenuItem menuitem_export_conf = new JMenuItem("导出流程关键配置"); //
			final JMenuItem menu_showimg = new JMenuItem("显示背景图"); //
			final JMenuItem menu_hiddenimg = new JMenuItem("隐藏背景图"); //
			menu_showimg.setToolTipText("上传一个流程图片作为背景后,可以像\"描红\"一样快速画图!"); //

			menu_export.add(menuitem_export_jpg); //jpg文件
			menu_export.add(menuitem_export_bmp); //bmp文件
			menu_export.add(menuitem_export_word); //
			menu_export.add(menuitem_export_conf); //

			ActionListener aclt = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (e.getSource() == menuitem_export_jpg) {
						exportAsJpg(); //
					} else if (e.getSource() == menuitem_export_bmp) {
						exportAsBmp(); //
					} else if (e.getSource() == menuitem_export_word) {
						exportAsWord(); //
					} else if (e.getSource() == menuitem_export_conf) {
						exportConf(); //
					} else if (e.getSource() == menu_showimg) {
						onShowImg(); //
					} else if (e.getSource() == menu_hiddenimg) {
						onHiddenImg(); //
					}
				}
			};

			menuitem_export_jpg.addActionListener(aclt);
			menuitem_export_bmp.addActionListener(aclt);
			menuitem_export_word.addActionListener(aclt);
			menuitem_export_conf.addActionListener(aclt);
			menu_showimg.addActionListener(aclt);
			menu_hiddenimg.addActionListener(aclt);

			popMenu.add(menu_export); //
			popMenu.add(menu_showimg); //
			popMenu.add(menu_hiddenimg); //
		}

		if (this.iscopy) {//如果按了复制，但未粘贴
			JMenuItem item_3 = new JMenuItem("粘贴"); //
			item_3.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					paseCells(x, y, true);
				}
			});
			popMenu.add(item_3); //
		}

		popMenu.show(graph, _x, _y); //
	}

	/**
	 * 将参与者,是否自循环,条件等关键信息一口气输出,然后可以集中审查!
	 */
	public void exportConf() {
		try {
			if (currentProcessVO == null || currentProcessVO.getId() == null) {
				MessageBox.show(this, "流程流程ID为空,可能还没有保存数据!请第二个保存一下再导出!");
				return;
			}
			String str_processID = this.currentProcessVO.getId(); //
			String str_sql_1 = "select t1.id,t1.code 环节编码,t1.wfname 环节名称,t1.activitytype 类型,t1.isselfcycle 是否自循环,t2.code 自循流程图,t1.approvemodel 审批模式,t1.participate_corp 参与机构,t1.participate_group 参与角色,t1.participate_dynamic 动态参与者,t1.intercept1 UI拦截器,t1.intercept2 BS拦截器 from pub_wf_activity t1 left join pub_wf_process t2 on t1.selfcyclerolemap=t2.id where t1.processid=" + str_processID
					+ " order by t1.y,t1.x"; //
			String str_sql_2 = "select t1.id,t1.code 连线编码,t2.code 来源环节编码,t2.wfname 来源环节名称,t3.code 目标环节编码,t3.wfname 目标环节名称,t1.conditions 通过条件,t1.intercept 通过时执行动作 from pub_wf_transition t1 left join pub_wf_activity t2 on t1.fromactivity=t2.id left join pub_wf_activity t3 on t1.toactivity=t3.id where t1.processid='" + str_processID + "'"; //
			HashVOStruct hvs_1 = UIUtil.getHashVoStructByDS(null, str_sql_1); //
			HashVOStruct hvs_2 = UIUtil.getHashVoStructByDS(null, str_sql_2); // 
			BillListPanel list_1 = new BillListPanel(hvs_1, 105);
			BillListPanel list_2 = new BillListPanel(hvs_2, 105);
			BillCardPanel card = new BillCardPanel(new DefaultTMO("流程信息", new String[][] { { "id", "400" }, { "code", "400" }, { "name", "400" },// 
					{ "wftype", "400" }, { "wfeguiintercept", "400" }, { "wfegbsintercept", "400" }, // 
					{ "userdef01", "400" }, { "userdef02", "400" }, { "userdef03", "400" }, { "userdef04", "400" } })); //
			card.queryData("select * from pub_wf_process where id='" + str_processID + "'"); //

			JTabbedPane tabb = new JTabbedPane(); //
			tabb.addTab("环节信息", list_1); //
			tabb.addTab("连线信息", list_2); //
			tabb.addTab("流程信息", card); //

			Object obj = getGraph().getSelectionCell();
			if (obj != null) {
				if (obj instanceof DefaultEdge) { //如果是连线
					DefaultEdge cell = (DefaultEdge) obj;
					Object userobj = cell.getUserObject(); //
					if (userobj instanceof TransitionVO) {
						TransitionVO tnsVO = (TransitionVO) userobj; //
						String str_tnsId = "" + tnsVO.getId(); //
						for (int i = 0; i < list_2.getRowCount(); i++) {
							if (list_2.getRealValueAtModel(i, "id").equals(str_tnsId)) {
								list_2.setSelectedRow(i); //
								break; //
							}
						}
						tabb.setSelectedIndex(1); //
					}
				} else if (obj instanceof DefaultGraphCell) { //如果是环节
					DefaultGraphCell cell = (DefaultGraphCell) obj;
					Object userobj = cell.getUserObject(); //
					if (userobj instanceof ActivityVO) {
						ActivityVO actVO = (ActivityVO) userobj; //
						String str_actId = "" + actVO.getId(); //
						for (int i = 0; i < list_1.getRowCount(); i++) {
							if (list_1.getRealValueAtModel(i, "id").equals(str_actId)) {
								list_1.setSelectedRow(i); //
								break; //
							}
						}
						tabb.setSelectedIndex(0); //
					}
				}
			}

			BillDialog dialog = new BillDialog(this, "流程配置信息", 1000, 600);
			dialog.getContentPane().add(tabb); //
			dialog.setVisible(true); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * 显示背景图片【李春娟/2012-04-12】
	 */
	private void onShowImg() {
		Object obj = graph.getClientProperty("backimg");
		if (obj == null) {
			if (MessageBox.confirm(this, "未上传背景图片,是否上传?")) {
				onImportBackImg();
			}
		} else {
			graph.setBackgroundImage((ImageIcon) obj);
			graph.repaint();
		}
	}

	/**
	 * 隐藏背景图片【李春娟/2012-04-12】
	 */
	private void onHiddenImg() {
		graph.setBackgroundImage(null);
		graph.repaint();
		reSetAllLayer(false);//需要设置一下，否则阶段会跑到后面去【李春娟/2012-04-18】
	}

	/**
	 * 上传背景图片，背景图片只是作为标准流程进行参考描红，不参与保存【李春娟/2012-04-12】
	 */
	private void onImportBackImg() {
		try {
			JFileChooser fc = new JFileChooser(new File(ClientEnvironment.str_downLoadFileDir));
			int li_result = fc.showOpenDialog(this);
			if (li_result != JFileChooser.APPROVE_OPTION) {
				return;
			}
			File selFile = fc.getSelectedFile();
			String str_fileName = selFile.getName(); //
			if (!(str_fileName.toLowerCase().endsWith(".jpg") || str_fileName.toLowerCase().endsWith(".gif") || str_fileName.toLowerCase().endsWith(".png") || str_fileName.toLowerCase().endsWith(".bmp"))) {
				MessageBox.show(this, "只能上传[jpg/gif/png/bmp]四种格式的图片!"); //
				return; //
			}
			BufferedImage baseImg = ImageIO.read(selFile);
			Graphics2D graph_g2 = (Graphics2D) baseImg.getGraphics();
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			graph_g2.setComposite(ac);
			graph_g2.scale(graph.getScale(), graph.getScale());//不这样搞环节不能放大与缩小
			graph_g2.setColor(Color.WHITE); //
			graph_g2.fillRect(0, 0, baseImg.getWidth(), baseImg.getHeight());
			graph_g2.dispose(); //
			ImageIcon icon = new ImageIcon(baseImg);
			graph.putClientProperty("backimg", icon);
			graph.setBackgroundImage(icon);
			graph.repaint();
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	//保存成jpg文件,压缩的,但文件小!
	private void exportAsJpg() {
		saveAsPicture(false); //
	}

	//保存成bmp文件!
	private void exportAsBmp() {
		saveAsPicture(true); //
	}

	/**
	 * 导出成图片
	 * @param _isBmp
	 */
	private void saveAsPicture(boolean _isBmp) {
		boolean isExportTitle = getTBUtil().getSysOptionBooleanValue("工作流导出是否有标题", true); //内控系统遇到一些客户要求导出时不要标题!
		graph.setGridVisible(false); //导出时把背景的网点去掉,即是干净的纯白背景
		if (!isExportTitle) { //如果不要导出标题,则将标题颜色改成白色即可!
			setTitleCellForeground(Color.WHITE);
		}
		try {
			String str_fileType = (_isBmp ? "bmp" : "jpg"); //
			String str_filename = ClientEnvironment.getInstance().getClientCodeCachePath() + "\\" + "graph_" + System.currentTimeMillis() + "." + str_fileType; //
			int li_width = (int) graph.getPreferredSize().getWidth() + 15;
			int li_height = (int) graph.getPreferredSize().getHeight() + 15;

			byte[] bytes = tbUtil.getCompentBytes(graph, li_width, li_height, _isBmp);
			tbUtil.writeBytesToOutputStream(new FileOutputStream(new File(str_filename), false), bytes); // 写文件!!
			Desktop.open(new File(str_filename)); //打开图片
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		} finally {
			graph.setGridVisible(true); //
			if (!isExportTitle) { //隐藏的标题颜色再恢复回来!
				setTitleCellForeground(Color.BLUE); //
			}
		}
	}

	/**
	 * 导出Word..
	 */
	private void exportAsWord() {
		boolean isExportTitle = getTBUtil().getSysOptionBooleanValue("工作流导出是否有标题", true); //内控系统遇到一些客户要求导出时不要标题!
		graph.setGridVisible(false); //导出时把背景的网点去掉,即是干净的纯白背景
		if (!isExportTitle) { //如果不要导出标题,则将标题颜色改成白色即可!
			setTitleCellForeground(Color.WHITE);
		}

		String str_filename = ClientEnvironment.getInstance().getClientCodeCachePath() + "" + "graph_" + System.currentTimeMillis() + ".doc"; //
		try {
			int li_width = (int) graph.getPreferredSize().getWidth() + 15;
			int li_height = (int) graph.getPreferredSize().getHeight() + 15;
			getTBUtil().saveCompentAsWordFile(graph, str_filename, li_width, li_height); //应该还要搞个表格在后面的才好!即将各个环节的名称清单搞成一个表
			try {
				Desktop.open(new File(str_filename)); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
				Runtime.getRuntime().exec("explorer.exe \"" + str_filename + "\"");
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		} finally {
			graph.setGridVisible(true); //
			if (!isExportTitle) { //隐藏的标题颜色再恢复回来!
				setTitleCellForeground(Color.BLUE); //
			}
		}
	}

	//导出流程文件正文是在其他类中生成图片，故这里需要提供设置标题颜色的方法【李春娟/2012-11-16】
	public void setTitleCellForeground(Color _color) {
		if (titleCell != null) {
			GraphConstants.setForeground(titleCell.getAttributes(), _color); //
			graph.getGraphLayoutCache().reload();
		}
	}

	public void onZoom() {
		graph.setScale(1.0);
	}

	public void onZoomin() {
		graph.setScale(graph.getScale() * 5 / 4); //
	}

	public void onZoomout() {
		graph.setScale(graph.getScale() * 4 / 5); //
	}

	// Undo the last Change to the Model or the View
	public void onUndo() {
		try {
			undoManager.undo(graph.getGraphLayoutCache());
			updateStationBackGroundActivityAsAuto();//【sunfujun,20120411,修正undobug】
			moveStationToMaxDept();
			reSetAllLayer(true);
			isEditChanged = true; //表示已经变化了
		} catch (Exception ex) {
			System.err.println(ex);
		} finally {
			// updateHistoryButtons();
		}
	}

	// Redo the last Change to the Model or the View
	public void onRedo() {
		try {
			undoManager.redo(graph.getGraphLayoutCache());
			updateStationBackGroundActivityAsAuto();//【sunfujun,20120411,修正undobug】
			moveStationToMaxDept();
			reSetAllLayer(true);
			isEditChanged = true; //表示已经变化了
		} catch (Exception ex) {
			System.err.println(ex);
		} finally {
			// updateHistoryButtons();
		}
	}

	public void onShowOrder() {
		if (checkBox_showorder != null) {
			boolean bo_shod = false;
			if (checkBox_showorder.isSelected()) {
				bo_shod = true;
			} else {
				bo_shod = false;
			}
			doShowOrder(bo_shod);
		}
	}

	//台前客户希望在导出流程文件正文时的流程图环节左上角的序号显示出来，故新增此方法【李春娟/2013-08-14】
	public void doShowOrder(boolean _visible) {
		Object[] cells = getActivityCells();
		for (int i = 0; i < cells.length; i++) {
			((DefaultGraphCell) cells[i]).getAttributes().put("isshoworder", _visible);
		}
		graph.repaint();
	}

	/**
	 * 删除某一个结点
	 */
	private void onDelCell() {
		if (!graph.isSelectionEmpty()) {
			Object[] cells = graph.getSelectionCells();
			cells = graph.getDescendants(cells); //找到递归的!
			Object linkedObject = null;
			for (int i = 0; i < cells.length; i++) {
				linkedObject = ((DefaultGraphCell) cells[i]).getUserObject();
				if (linkedObject instanceof ActivityVO) {
					if (MessageBox.showConfirmDialog(this, "您确定要删除该环节吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {//流程图中删除环节前来个警告
						return;
					} else {
						break;
					}
				}
			}
			//graph.getModel().remove(cells); //如果是环节,则需要同时删除环节关联的所有连线!!
			Object[] transitionCells = getAllActivtyRefTransition(cells);//得到选中环节关联的连线
			//if (transitionCells != null) {//删除连线
			//graph.getModel().remove(transitionCells);
			//}
			Object[] allDeleteCells = ArrayUtils.addAll(cells, transitionCells);
			Map attrs = new HashMap();
			getDeptCells(attrs);
			ConnectionSet connectionset = ConnectionSet.create(graph.getModel(), allDeleteCells, true);
			ParentMap parentmap = ParentMap.create(graph.getModel(), allDeleteCells, true, false);
			((DefaultGraphModel) graph.getModel()).edit(null, allDeleteCells, attrs, connectionset, parentmap, null);//写成一个undo事件【sunfujun/20120417/undobug】
			if (this.v_cellDeleteListeners != null && this.v_cellDeleteListeners.size() > 0) {
				for (int i = 0; i < v_cellDeleteListeners.size(); i++) {
					WorkFlowCellDeleteListener listener = (WorkFlowCellDeleteListener) v_cellDeleteListeners.get(i);
					listener.onWorkFlowCellDelete(new WorkFlowCellDeleteEvent(cells));
				}
			}
		}
		reSortDept(null); //重排部门,因为可能删除的是中间的部门!!
		if (stationBackGroundCell != null) {
			GraphConstants.setBounds(stationBackGroundCell.getAttributes(), new Rectangle2D.Double(10, 30, 61, getMaxDeptHeight())); //
		}
		delStationBackGroundActivityAsAuto(); //如果阶段都删除光了,则还要自动计算一下是否要删除阶段的背景!!
		isEditChanged = true; //表示已经变化了
	}

	/**
	 * 得到环节cells所关联的所有连线
	 * @param cells
	 * @return
	 */
	private Object[] getAllActivtyRefTransition(Object[] cells) {
		HashMap transitionVOMap = new HashMap();
		Object linkedObject = null;
		DefaultGraphCell cell = null;
		StringBuffer sb = new StringBuffer();
		//找到所有选中环节的ID
		for (int i = 0; i < cells.length; i++) {
			linkedObject = ((DefaultGraphCell) cells[i]).getUserObject();
			if (linkedObject instanceof ActivityVO) {
				sb.append("';" + ((ActivityVO) linkedObject).getId() + ";',");
			}
		}
		//找到所有关联选中环节的连线
		GraphModel model = graph.getModel();
		int rootCount = model.getRootCount(); // 所有结点的数目
		for (int i = 0; i < rootCount; i++) {
			Object obj = model.getRootAt(i);
			if (obj instanceof DefaultGraphCell) {
				cell = (DefaultGraphCell) obj;
				linkedObject = cell.getUserObject();
				if (linkedObject instanceof TransitionVO) {
					updateConnectValue((DefaultEdge) cell);
					TransitionVO transitionVO = (TransitionVO) linkedObject;
					if (sb.indexOf("';" + transitionVO.getFromactivity() + ";'") >= 0 || sb.indexOf("';" + transitionVO.getToactivity() + ";'") >= 0)
						transitionVOMap.put(i, cell);
				}
			}
		}

		if (transitionVOMap != null && transitionVOMap.size() > 0) {
			return (Object[]) transitionVOMap.values().toArray(new Object[0]);
		}

		return null;
	}

	private void onShowHelpInfo() {
		if (this.helpInfo != null) {
			MessageBox.showTextArea(this, helpInfo); //
			return;
		}
		StringBuilder sb_help = new StringBuilder(); //
		sb_help.append("1.在一个环节上按住Ctrl键往外拖动,就可以画线!\r\n"); //
		sb_help.append("2.按住Shift键在一个连线上点击,就会增加一个折点!\n"); //
		sb_help.append("3.在拖动画线过程中,敲击X键,会自动增加折点!\n"); //
		sb_help.append("4.在文档环节后增加图片环节按钮，拖动可以创建图片环节!\n"); //
		sb_help.append("5.增加环节的复制、粘贴功能!\n"); //
		sb_help.append("6.增加纵向对齐、横向对齐、同高、同长4个操作环节按钮!"); //
		MessageBox.showTextArea(this, sb_help.toString()); //
	}

	/**
	 * 编辑环节属性
	 */
	private void editCellConfig() {
		if (graph.getSelectionCell() != null && graph.getSelectionCell() instanceof DefaultGraphCell) {
			mouseRightClickedCell = (DefaultGraphCell) graph.getSelectionCell(); //
			if (mouseRightClickedCell != null) { // 如果右键选中的环节不为空!!
				Object userobj = mouseRightClickedCell.getUserObject(); //
				if (userobj instanceof ActivityVO) {
					updateCellText(mouseRightClickedCell); //
				} else if (userobj instanceof TransitionVO) {
					updateTransitionText(mouseRightClickedCell); //
				} else if (userobj instanceof GroupVO) {
					GroupVO gvo = (GroupVO) userobj; //
					if (gvo.getGrouptype().equals("DEPT")) { // 如果类型是部门的
						updateGroupDeptCellText(mouseRightClickedCell); //部门不可编辑!!! 这是航天科工的需求!! 不可以影响其他项目!!
					} else if (gvo.getGrouptype().equals("STATION")) { //如果是阶段
						updateGroupCellText(mouseRightClickedCell); //
					}
				} else {
					JOptionPane.showMessageDialog(this, "选中的是未知的数据类型,既不是环节也不是连线!"); //
				}
				reSetAllLayer(false); //
			}
		}
	}

	/**
	 * 复制
	 */
	private void copyCells() {
		this.iscopy = true;
		copyCach = this.graph.getSelectionCells();
	}

	/**
	 * 粘贴
	 * @param _x
	 * @param _y
	 */
	private void paseCells(int _x, int _y, boolean _isAbsolute) {
		if (copyCach != null && copyCach.length > 0 && this.iscopy) {
			DefaultGraphCell cell = null;
			Object linkobj = null;
			int newx = 10000;
			int newy = 10000;
			DefaultGraphCell newActivityCell = null;
			List selectT = new ArrayList();
			ArrayList copecell = new ArrayList();
			Rectangle2D point2D = null; // 取得坐标位置
			if (_isAbsolute) {//是否是鼠标点击的绝对位置（右击选择粘贴）
				for (int i = 0; i < copyCach.length; i++) {//
					if (copyCach[i] instanceof DefaultGraphCell) {
						cell = (DefaultGraphCell) copyCach[i];
						linkobj = cell.getUserObject();
						if (linkobj instanceof ActivityVO) {
							point2D = GraphConstants.getBounds(cell.getAttributes());
							if (newx > new Double(point2D.getX()).intValue()) {
								newx = new Double(point2D.getX()).intValue();//取得选中环节中最小的环节x坐标
							}
							if (newy > new Double(point2D.getY()).intValue()) {
								newy = new Double(point2D.getY()).intValue();//取得选中环节中最小的环节y坐标
							}
						}
					}
				}
				_x = _x - newx;
				_y = _y - newy;
			} else {
				for (int i = 0; i < copyCach.length; i++) {// 
					if (copyCach[i] instanceof DefaultGraphCell) {
						cell = (DefaultGraphCell) copyCach[i];
						linkobj = cell.getUserObject();
						if (linkobj instanceof ActivityVO) {
							point2D = GraphConstants.getBounds(cell.getAttributes());
							_x = new Double(point2D.getWidth()).intValue() / 2 + 2;//向右下方移动第一个环节的宽度高度的一半
							_y = new Double(point2D.getHeight()).intValue() / 2 + 2;
							break;
						}
					}
				}
			}

			for (int i = 0; i < copyCach.length; i++) {//先画环节和部门 
				if (copyCach[i] instanceof DefaultGraphCell) {
					cell = (DefaultGraphCell) copyCach[i];
					linkobj = cell.getUserObject();
					if (linkobj instanceof ActivityVO) {
						point2D = GraphConstants.getBounds(cell.getAttributes());
						newx = new Double(point2D.getX()).intValue() + _x;
						newy = new Double(point2D.getY()).intValue() + _y;
						if (newy < 5) {
							newy = 5;
						}
						if (newx < 5) {
							newx = 5;
						}
						newActivityCell = dealPasteNormalCell(cell, newx, newy);
						copecell.add(newActivityCell);
					} else if (linkobj instanceof GroupVO) {

					} else if (linkobj instanceof TransitionVO) {
						selectT.add(cell);
					}
				}
			}
			getGraph().setSelectionCells(copecell.toArray());
			this.iscopy = false;
		}
	}

	/**
	 * cell调整函数同X坐标
	 */
	private void cellSameX() {
		Object[] selectCells = this.graph.getSelectionCells();//得到选中的单元
		if (selectCells != null && selectCells.length > 1) {//最少2个才能比较
			if (selectCells[0] instanceof DefaultGraphCell) {
				if (((DefaultGraphCell) selectCells[0]).getUserObject() instanceof ActivityVO) {
					graph.getGraphLayoutCache().edit(getAttrMap(selectCells));
					Rectangle2D rec2d = GraphConstants.getBounds(((DefaultGraphCell) selectCells[0]).getAttributes());
					double x0 = rec2d.getX();
					for (int i = 1; i < selectCells.length; i++) {
						if (((DefaultGraphCell) selectCells[i]).getUserObject() instanceof ActivityVO) {
							rec2d = GraphConstants.getBounds(((DefaultGraphCell) selectCells[i]).getAttributes());
							GraphConstants.setBounds(((DefaultGraphCell) selectCells[i]).getAttributes(), new Rectangle2D.Double(x0, rec2d.getY(), rec2d.getWidth(), rec2d.getHeight()));
						}
					}
					getGraph().getGraphLayoutCache().toFront(new Object[] { selectCells }); //
				}
			}
		}
	}

	private Map getAttrMap(Object[] selectCells) {
		Map attrs = new HashMap();
		for (int i = 1; i < selectCells.length; i++) {
			attrs.put(selectCells[i], ((DefaultGraphCell) selectCells[i]).getAttributes());
		}
		return attrs;
	}

	/**
	 * cell调整函数同Y坐标
	 */
	private void cellSameY() {
		Object[] selectCells = this.graph.getSelectionCells();//得到选中的单元
		if (selectCells != null && selectCells.length > 1) {//最少2个才能比较
			if (selectCells[0] instanceof DefaultGraphCell) {
				if (((DefaultGraphCell) selectCells[0]).getUserObject() instanceof ActivityVO) {
					graph.getGraphLayoutCache().edit(getAttrMap(selectCells));
					Rectangle2D rec2d = GraphConstants.getBounds(((DefaultGraphCell) selectCells[0]).getAttributes());
					double y = rec2d.getY();
					for (int i = 1; i < selectCells.length; i++) {
						if (((DefaultGraphCell) selectCells[i]).getUserObject() instanceof ActivityVO) {
							rec2d = GraphConstants.getBounds(((DefaultGraphCell) selectCells[i]).getAttributes());
							GraphConstants.setBounds(((DefaultGraphCell) selectCells[i]).getAttributes(), new Rectangle2D.Double(rec2d.getX(), y, rec2d.getWidth(), rec2d.getHeight()));
						}
					}
					getGraph().getGraphLayoutCache().toFront(new Object[] { selectCells }); //
				}
			}
		}
	}

	/**
	 * cell调整函数同高度
	 */
	private void cellSameH() {
		Object[] selectCells = this.graph.getSelectionCells();//得到选中的单元
		if (selectCells != null && selectCells.length > 1) {//最少2个才能比较
			if (selectCells[0] instanceof DefaultGraphCell) {
				if (((DefaultGraphCell) selectCells[0]).getUserObject() instanceof ActivityVO) {
					graph.getGraphLayoutCache().edit(getAttrMap(selectCells));
					Rectangle2D rec2d = GraphConstants.getBounds(((DefaultGraphCell) selectCells[0]).getAttributes());
					double h = rec2d.getHeight();
					for (int i = 1; i < selectCells.length; i++) {
						if (((DefaultGraphCell) selectCells[i]).getUserObject() instanceof ActivityVO) {
							rec2d = GraphConstants.getBounds(((DefaultGraphCell) selectCells[i]).getAttributes());
							GraphConstants.setBounds(((DefaultGraphCell) selectCells[i]).getAttributes(), new Rectangle2D.Double(rec2d.getX(), rec2d.getY(), rec2d.getWidth(), h));
						}
					}
					getGraph().getGraphLayoutCache().toFront(new Object[] { selectCells }); //
				}
			}
		}
	}

	/**
	 * cell调整函数同宽度
	 */
	private void cellSameW() {
		Object[] selectCells = this.graph.getSelectionCells();//得到选中的单元
		if (selectCells != null && selectCells.length > 1) {//最少2个才能比较
			if (selectCells[0] instanceof DefaultGraphCell) {
				if (((DefaultGraphCell) selectCells[0]).getUserObject() instanceof ActivityVO) {
					graph.getGraphLayoutCache().edit(getAttrMap(selectCells));
					Rectangle2D rec2d = GraphConstants.getBounds(((DefaultGraphCell) selectCells[0]).getAttributes());
					double w = rec2d.getWidth();
					for (int i = 1; i < selectCells.length; i++) {
						if (((DefaultGraphCell) selectCells[i]).getUserObject() instanceof ActivityVO) {
							rec2d = GraphConstants.getBounds(((DefaultGraphCell) selectCells[i]).getAttributes());
							GraphConstants.setBounds(((DefaultGraphCell) selectCells[i]).getAttributes(), new Rectangle2D.Double(rec2d.getX(), rec2d.getY(), w, rec2d.getHeight()));
						}
					}
					getGraph().getGraphLayoutCache().toFront(new Object[] { selectCells }); //
				}
			}
		}
	}

	//根据按的是左右键来获得下一个线箭头类型。[郝明2012-04-28]
	private int getLineType(int now, int type) {
		for (int i = 0; i < lineType.length; i++) {
			if (type == 0 && lineType[i] == now) {
				if (i == 0) {
					return lineType[7];
				} else {
					return lineType[i - 1];
				}
			} else if (type == 1 && lineType[i] == now) {
				if (i == 7) {
					return lineType[0];
				} else {
					return lineType[i + 1];
				}
			}
		}
		return now;
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			onDelCell(); //
		} else if (e.getKeyCode() >= KeyEvent.VK_LEFT && e.getKeyCode() <= KeyEvent.VK_DOWN) {
			Object[] objs = graph.getSelectionCells();
			for (int i = 0; i < objs.length; i++) {
				Object obj = objs[i]; //
				if (obj instanceof DefaultGraphCell) { // 如果是默认结点
					DefaultGraphCell cell = (DefaultGraphCell) obj; //
					if (objs.length == 1 && cell instanceof DefaultEdge) { //如果选择的是线，按左右方向键会改变其箭头形状。[郝明2012-04-28]
						//如果是
						TransitionVO vo = (TransitionVO) cell.getUserObject();
						TransitionVO newVo = new TransitionVO();
						try {
							BeanUtils.copyProperties(newVo, vo);
						} catch (IllegalAccessException e1) {
							e1.printStackTrace();
						} catch (InvocationTargetException e1) {
							e1.printStackTrace();
						}
						int type = vo.getLineType();
						if (e.getKeyCode() == KeyEvent.VK_LEFT) {
							int newType = getLineType(type, 0);
							newVo.setLineType(newType);
						} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
							int newType = getLineType(type, 1);
							newVo.setLineType(newType);
						}
						createUndoEvent(cell, newVo);
						GraphConstants.setLineEnd(cell.getAttributes(), newVo.getLineType());
						graph.getGraphLayoutCache().reload(); //
						reSetAllLayer(true); //
						graph.repaint();
						isEditChanged = true; //表示已经变化了
					}
					if (cell.getChildCount() == 0)//李强修改
						continue;
					Rectangle2D rec = GraphConstants.getBounds(cell.getAttributes()); //
					if (rec != null) {
						double ld_x = rec.getX();
						double ld_y = rec.getY();
						double ld_x_center = rec.getCenterX();
						double ld_y_center = rec.getCenterY();
						//因环节连线时，有时候怎么动，线都不是直的，故将变化单位量度改为1，以前为2!
						if (e.getKeyCode() == KeyEvent.VK_LEFT) {
							ld_x = ld_x - 1;
							ld_x_center = ld_x_center - 1;
						} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
							ld_x = ld_x + 1;
							ld_x_center = ld_x_center + 1;
						} else if (e.getKeyCode() == KeyEvent.VK_UP) {
							ld_y = ld_y - 1;
							ld_y_center = ld_y_center - 1;
						} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
							ld_y = ld_y + 1;
							ld_y_center = ld_y_center + 1;
						}

						Rectangle2D newRec = new Rectangle2D.Double(ld_x_center, ld_y_center, rec.getWidth(), rec.getHeight());
						GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(ld_x, ld_y, rec.getWidth(), rec.getHeight())); //
						if (cell.getFirstChild() instanceof DefaultPort) {
							DefaultPort port = (DefaultPort) cell.getFirstChild();
							GraphConstants.setBounds(port.getAttributes(), newRec); //
						}
						graph.getGraphLayoutCache().reload(); //
						reSetAllLayer(true); //
						graph.repaint();
						isEditChanged = true; //表示已经变化了
					}
					e.consume();//【sfj/20120301/防止用方向键移动环节时，滚动条的滚动】
				}
			}
		} else if (e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown()) {
			onUndo(); //
		} else if (e.getKeyCode() == KeyEvent.VK_Y && e.isControlDown()) {
			onRedo(); //
		} else if (e.getKeyCode() == KeyEvent.VK_F2) {
			graph.stopEditing(); //
			Object obj = graph.getSelectionCell();
			if (obj != null && obj instanceof DefaultGraphCell) {
				this.mouseRightClickedCell = (DefaultGraphCell) obj; //
				editCellConfig(); //
			} else {
				this.mouseRightClickedCell = null;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_X) {
			if (isDragging) {
				int li_x = (int) draggingPoint.getX(); //
				int li_y = (int) draggingPoint.getY(); //
				al_draggingPortXY.add(new int[] { li_x, li_y }); //
				graph.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR)); //
				try {
					Thread.currentThread().sleep(100); //等会,否则太快,没感觉!!!
				} catch (Exception ex) {
					ex.printStackTrace(); //
				}
				graph.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
			}
		} else if (e.getKeyCode() == KeyEvent.VK_C) {
			graph.stopEditing(); //
			Object obj = graph.getSelectionCell();
			if (obj != null && obj instanceof DefaultGraphCell) {
				this.mouseRightClickedCell = (DefaultGraphCell) obj; //
				copyCells();
			} else {
				this.mouseRightClickedCell = null;
			}
		} else if (e.getKeyCode() == KeyEvent.VK_V) {
			graph.stopEditing(); //
			if (this.iscopy) {
				paseCells(0, 0, false);
			}
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	/**
	 * 图形中选择结点变化
	 */
	public void valueChanged(GraphSelectionEvent e) {
		Object obj = e.getCell(); //
		if (obj instanceof DefaultGraphCell) {
			DefaultGraphCell cell = (DefaultGraphCell) obj; //
			Object linkedObject = cell.getUserObject(); //
			if (linkedObject instanceof ActivityVO) {
				ActivityVO vo = (ActivityVO) linkedObject;
				if (this.showtoolbox) {//判断是否显示工具箱
					ActivityBean bean = (ActivityBean) getActivityPropPanel().getBeanInstance(); //
					copyActivityDataFromVoToBean(vo, bean);
					getActivityPropPanel().reload(); //
					switchToActivityPropPanel();
				}
				callAllSelectListener(vo); //增加环节选择事件
			} else if (linkedObject instanceof GroupVO) {
				if (this.showtoolbox) {//判断是否显示工具箱
					switchToBlankPropPanel();
				}
				callAllSelectListener(null); //以前一直监控不到选择部门的事件，这里设置一下，即可在事件方法中判断到底选中的是环节还是部门
			} else if (this.showtoolbox) {//需要先判断是否显示工具箱，并且判断点击的是否是连线
				if (linkedObject instanceof TransitionVO) {
					TransitionVO vo = (TransitionVO) linkedObject;
					TransitionBean bean = (TransitionBean) getTransitionPropPanel().getBeanInstance(); //
					copyTransitionDataFromVoToBean(vo, bean);
					getTransitionPropPanel().reload(); //
					switchToTransitionPropPanel();
				} else {
					switchToBlankPropPanel();
				}
			}
		} else if (this.showtoolbox) {//如果显示工具箱才执行
			switchToBlankPropPanel();
		}
	}

	/**
	 * 选择发生变化...
	 * 
	 * @param _vo
	 */
	private void callAllSelectListener(ActivityVO _vo) {
		for (int i = 0; i < vector_cellSelectedListener.size(); i++) {
			WorkFlowCellSelectedListener listener = (WorkFlowCellSelectedListener) vector_cellSelectedListener.get(i); //
			WorkFlowCellSelectedEvent event = new WorkFlowCellSelectedEvent(this, _vo); //
			listener.onWorkFlowCellSelected(event); // //

		}
	}

	/**
	 * 编辑右边的属性框中的内容值发生变化
	 */
	public void onBillPropValueChanged(BillPropEditEvent _evt) {
		if (_evt.getSource() == getProcessPropPanel()) {
			if (currentProcessVO != null) {
				ProcessBean bean = (ProcessBean) _evt.getBeanInstance();
				copyProcessDataFromBeanToVo(bean, this.currentProcessVO);
			}
		} else if (_evt.getSource() == getActivityPropPanel()) {
			DefaultGraphCell cell = getSelectedActivityCell();
			if (cell != null) {
				ActivityBean bean = (ActivityBean) _evt.getBeanInstance();
				ActivityVO vo = (ActivityVO) cell.getUserObject();
				copyActivityDataFromBeanToVo(bean, vo); // 将bean中的数据赋值到VO中去!!!
				graph.getGraphLayoutCache().reload(); // 一定要执行,否则会出现多余的框!!
				// graph.updateUI(); //
				// graph.setUI(new MyGraphUI()); // 重新设置UI
				graph.repaint();
				graph.invalidate(); //
				// graph.invalidate(); //
			}

		} else if (_evt.getSource() == getTransitionPropPanel()) {
			DefaultEdge transition = getSelectedTransition();
			if (transition != null) {
				TransitionBean bean = (TransitionBean) _evt.getBeanInstance(); //
				TransitionVO vo = (TransitionVO) transition.getUserObject(); //
				copyTransitionDataFromBeanToVo(bean, vo); // 将bean中的数据赋值到VO中去!!!
				rePaintLineStyle(transition); // 重新画一下颜色!
				graph.getGraphLayoutCache().reload(); // 一定要执行,否则会出现多余的框!!
				// graph.updateUI(); //
				// graph.setUI(new MyGraphUI()); // 重新设置UI
				graph.repaint();
				graph.invalidate(); //
			}
		} else { // 
			//			DefaultEdge transition = getSelectedTransition();
			//			if (transition != null) {
			//				TransitionBean bean = (TransitionBean) _evt.getBeanInstance(); //
			//				TransitionVO vo = (TransitionVO) transition.getUserObject(); //
			//				copyTransitionDataFromBeanToVo(bean, vo); // 将bean中的数据赋值到VO中去!!!
			//				rePaintLineStyle(transition); // 重新画一下颜色!
			//				graph.getGraphLayoutCache().reload(); // 一定要执行,否则会出现多余的框!!
			//				// graph.updateUI(); //
			//				// graph.setUI(new MyGraphUI()); // 重新设置UI
			//				graph.repaint();
			//				graph.invalidate(); //
			//			}
		}
	}

	/**
	 * 重新绘制颜色
	 * 
	 * @param _edge
	 */
	private void rePaintLineStyle(DefaultEdge _edge) {
		TransitionVO vo = (TransitionVO) _edge.getUserObject(); //
		if ("SUBMIT".equalsIgnoreCase(vo.getDealtype())) {
			// GraphConstants.setLineColor(_edge.getAttributes(), new
			// java.awt.Color(99, 99, 99)); //
			GraphConstants.setDashPattern(_edge.getAttributes(), new float[] { 10, 0 }); //
			GraphConstants.setDashOffset(_edge.getAttributes(), 0);
		} else if ("REJECT".equalsIgnoreCase(vo.getDealtype())) {
			GraphConstants.setLineColor(_edge.getAttributes(), java.awt.Color.MAGENTA); //
			GraphConstants.setDashPattern(_edge.getAttributes(), new float[] { 5, 2, 2, 2 }); //
			GraphConstants.setDashOffset(_edge.getAttributes(), 5);
		} else {
			// GraphConstants.setLineColor(_edge.getAttributes(),
			// java.awt.Color.BLACK); //
			GraphConstants.setDashPattern(_edge.getAttributes(), new float[] { 10, 0 }); //
			GraphConstants.setDashOffset(_edge.getAttributes(), 5);
		}
	}

	/**
	 * 将流程Bean中的数据复制到VO中去!!!在左边属性编辑框中编辑某个数据后触发
	 * 
	 * @param _bean
	 * @param _vo
	 */
	private void copyProcessDataFromBeanToVo(ProcessBean _bean, ProcessVO _vo) {
		_vo.setCode(_bean.getCode()); //
		_vo.setName(_bean.getName()); //
		_vo.setWfeguiintercept(_bean.getWfeguiintercept()); //UI端统一的拦截器!!
		_vo.setWfegbsintercept(_bean.getWfegbsintercept()); //BS端统一的拦截器!!

		_vo.setUserdef01(_bean.getUserdef01()); //
		_vo.setUserdef02(_bean.getUserdef02()); //
		_vo.setUserdef03(_bean.getUserdef03()); //
		_vo.setUserdef04(_bean.getUserdef04()); //自定义项4
	}

	/**
	 * 将流程VO中的数据复制到Bean中去!!!在左边属性编辑框中编辑某个数据后触发
	 * 
	 * @param _bean
	 * @param _vo
	 */
	private void copyProcessDataFromVoToBean(ProcessVO _vo, ProcessBean _bean) {
		_bean.setCode(_vo.getCode()); //
		_bean.setName(_vo.getName()); //
		_bean.setWfeguiintercept(_vo.getWfeguiintercept()); //UI端统一的拦截器!!
		_bean.setWfegbsintercept(_vo.getWfegbsintercept()); //BS端统一的拦截器!!

		_bean.setUserdef01(_vo.getUserdef01()); //
		_bean.setUserdef02(_vo.getUserdef02()); //
		_bean.setUserdef03(_vo.getUserdef03()); //
		_bean.setUserdef04(_vo.getUserdef04()); //自定义项4
	}

	/**
	 * 将环节Bean中的数据复制到VO中去!!!在右边属性编辑框中编辑某个数据后触发
	 * 
	 * @param _bean
	 * @param _vo
	 */
	private void copyActivityDataFromBeanToVo(ActivityBean _bean, ActivityVO _vo) {
		_vo.setProcessid(_bean.getProcessid()); //
		_vo.setActivitytype(_bean.getActivitytype()); //
		_vo.setCode(_bean.getCode()); //
		_vo.setUiname(_bean.getUiname()); //
		_vo.setWfname(_bean.getWfname()); //

		if (_bean.getCanhalfstart() != null && _bean.getCanhalfstart().booleanValue()) {
			_vo.setCanhalfstart("Y"); //
		} else {
			_vo.setCanhalfstart("N"); //
		}

		if (_bean.getIsHideTransSend() != null && _bean.getIsHideTransSend().booleanValue()) {
			_vo.setIsHideTransSend("Y"); //
		} else {
			_vo.setIsHideTransSend("N"); //
		}

		_vo.setHalfstartrole(_bean.getHalfstartrole() == null ? null : _bean.getHalfstartrole().getId()); //
		_vo.setHalfstartdepttype(_bean.getHalfstartdepttype() == null ? null : _bean.getHalfstartdepttype().getId()); // 可以启动的机构类型.. gaofeng

		if (_bean.getCanhalfend() != null && _bean.getCanhalfend().booleanValue()) {
			_vo.setCanhalfend("Y"); //
		} else {
			_vo.setCanhalfend("N"); //
		}

		if (_bean.getCanselfaddparticipate() != null && _bean.getCanselfaddparticipate().booleanValue()) {
			_vo.setCanselfaddparticipate("Y"); //
		} else {
			_vo.setCanselfaddparticipate("N"); //
		}

		if (_bean.getAutocommit() != null && _bean.getAutocommit().booleanValue()) {
			_vo.setAutocommit("Y"); //
		} else {
			_vo.setAutocommit("N"); //
		}

		if (_bean.getIscanback() != null && _bean.getIscanback().booleanValue()) {
			_vo.setIscanback("Y"); //
		} else {
			_vo.setIscanback("N"); //
		}

		if (_bean.getIsassignapprover() != null && _bean.getIsassignapprover().booleanValue()) {
			_vo.setIsassignapprover("Y"); //
		} else {
			_vo.setIsassignapprover("N"); //
		}

		if (_bean.getIsneedmsg() != null && _bean.getIsneedmsg().booleanValue()) {
			_vo.setIsneedmsg("Y"); //
		} else {
			_vo.setIsneedmsg("N"); //
		}

		if (_bean.getIsneedreport() != null && _bean.getIsneedreport().booleanValue()) {
			_vo.setIsneedreport("Y"); //
		} else {
			_vo.setIsneedreport("N"); //
		}

		if (_bean.getIsselfcycle() != null && _bean.getIsselfcycle().booleanValue()) { //是否可以自循环!
			_vo.setIsselfcycle("Y"); //
		} else {
			_vo.setIsselfcycle("N"); //
		}

		_vo.setSelfcyclerolemap(_bean.getSelfcyclerolemap()); //自循环对应的角色映射

		_vo.setIscanlookidea(_bean.getIscanlookidea()); //
		_vo.setApprovemodel(_bean.getApprovemodel());
		_vo.setApprovenumber(_bean.getApprovenumber()); //
		_vo.setChildwfcode(_bean.getChildwfcode()); //设置子流程编码!!

		_vo.setShowparticimode(_bean.getShowparticimode()); //
		_vo.setParticipate_user(_bean.getParticipate_user() == null ? null : _bean.getParticipate_user().getId()); // 用户,加;
		_vo.setParticipate_group(_bean.getParticipate_group() == null ? null : _bean.getParticipate_group().getId()); // 参与组,加";"

		_vo.setParticipate_corp(_bean.getParticipate_corp()); //机构范围定义,实际上是根据公式计算出来的

		_vo.setCcto_user(_bean.getCcto_user() == null ? null : _bean.getCcto_user().getId()); //抄送人员!!!
		_vo.setCcto_corp(_bean.getCcto_corp()); //抄送机构!!!
		_vo.setCcto_role(_bean.getCcto_role() == null ? null : _bean.getCcto_role().getId()); //抄送角色!!!

		_vo.setParticipate_dynamic(_bean.getParticipate_dynamic() == null ? null : _bean.getParticipate_dynamic().getId()); ////
		_vo.setMessageformat(_bean.getMessageformat());
		_vo.setMessagereceiver(addPrefix(_bean.getMessagereceiver()));

		_vo.setIntercept1(_bean.getIntercept1()); // 拦截器1
		_vo.setIntercept2(_bean.getIntercept2()); // 拦截器2

		_vo.setUserdef01(_bean.getUserdef01()); //
		_vo.setUserdef02(_bean.getUserdef02()); //
		_vo.setUserdef03(_bean.getUserdef03()); //
		_vo.setUserdef04(_bean.getUserdef04()); //
		_vo.setExtconfmap(_bean.getExtconfmap());
		isEditChanged = true; //
	}

	/**
	 * 将环节VO中的数据复制到Bean中去,在图中选择某个结点时触发
	 * 
	 * @param _bean
	 * @param _vo
	 */
	private void copyActivityDataFromVoToBean(ActivityVO _vo, ActivityBean _bean) {
		_bean.setProcessid(_vo.getProcessid()); //
		_bean.setActivitytype(_vo.getActivitytype()); //
		_bean.setCode(_vo.getCode()); //
		_bean.setUiname(_vo.getUiname()); //
		_bean.setWfname(_vo.getWfname()); //

		if (_vo.getCanhalfstart() != null && _vo.getCanhalfstart().equals("Y")) {
			_bean.setCanhalfstart(Boolean.TRUE); //
		} else {
			_bean.setCanhalfstart(Boolean.FALSE); //
		}

		if (_vo.getIsHideTransSend() != null && _vo.getIsHideTransSend().equals("Y")) {
			_bean.setIsHideTransSend(Boolean.TRUE);
		} else {
			_bean.setIsHideTransSend(Boolean.FALSE);
		}

		_bean.setHalfstartrole(_vo.getHalfstartrole() == null ? null : (new RefItemVO(_vo.getHalfstartrole(), null, _vo.getHalfstartrole()))); //中途启动的角色!!
		_bean.setHalfstartdepttype(_vo.getHalfstartdepttype() == null ? null : (new RefItemVO(_vo.getHalfstartdepttype(), null, _vo.getHalfstartdepttype()))); //中途启动的机构类型!!  gaofeng

		if (_vo.getCanhalfend() != null && _vo.getCanhalfend().equals("Y")) {
			_bean.setCanhalfend(Boolean.TRUE); //
		} else {
			_bean.setCanhalfend(Boolean.FALSE); //
		}

		if (_vo.getCanselfaddparticipate() != null && _vo.getCanselfaddparticipate().equals("Y")) {
			_bean.setCanselfaddparticipate(Boolean.TRUE); //
		} else {
			_bean.setCanselfaddparticipate(Boolean.FALSE); //
		}

		if (_vo.getAutocommit() != null && _vo.getAutocommit().equals("Y")) {
			_bean.setAutocommit(Boolean.TRUE); //
		} else {
			_bean.setAutocommit(Boolean.FALSE); //
		}

		if (_vo.getIscanback() != null && _vo.getIscanback().equals("Y")) {
			_bean.setIscanback(Boolean.TRUE); //
		} else {
			_bean.setIscanback(Boolean.FALSE); //
		}

		if (_vo.getIsneedmsg() != null && _vo.getIsneedmsg().equals("Y")) {
			_bean.setIsneedmsg(Boolean.TRUE); //
		} else {
			_bean.setIsneedmsg(Boolean.FALSE); //
		}
		if (_vo.getIsneedreport() != null && _vo.getIsneedreport().equals("Y")) {
			_bean.setIsneedreport(Boolean.TRUE); //
		} else {
			_bean.setIsneedreport(Boolean.FALSE); //
		}

		_bean.setIscanlookidea(_vo.getIscanlookidea());
		if (_vo.getIsassignapprover() != null && _vo.getIsassignapprover().equals("Y")) {
			_bean.setIsassignapprover(Boolean.TRUE); //
		} else {
			_bean.setIsassignapprover(Boolean.FALSE); //
		}

		if (_vo.getIsselfcycle() != null && _vo.getIsselfcycle().equals("Y")) { //是否自循环!
			_bean.setIsselfcycle(Boolean.TRUE); //
		} else {
			_bean.setIsselfcycle(Boolean.FALSE); //
		}

		_bean.setSelfcyclerolemap(_vo.getSelfcyclerolemap()); //自循环的角色映射!

		_bean.setApprovemodel(_vo.getApprovemodel());
		_bean.setApprovenumber(_vo.getApprovenumber());
		_bean.setChildwfcode(_vo.getChildwfcode()); //设置子流程编码!!!

		_bean.setShowparticimode(_vo.getShowparticimode()); // 显示参与者模式.
		_bean.setParticipate_user(_vo.getParticipate_user() == null ? null : (new RefItemVO(_vo.getParticipate_user(), null, _vo.getParticipate_user()))); // 设置参与者,去掉前辍
		_bean.setParticipate_group(_vo.getParticipate_group() == null ? null : (new RefItemVO(_vo.getParticipate_group(), null, _vo.getParticipate_group()))); //静态角色/组

		_bean.setParticipate_corp(_vo.getParticipate_corp()); //设置参与的机构!!

		_bean.setCcto_user(_vo.getCcto_user() == null ? null : (new RefItemVO(_vo.getCcto_user(), null, _vo.getCcto_user()))); //抄送人员!
		_bean.setCcto_corp(_vo.getCcto_corp()); //抄送机构!
		_bean.setCcto_role(_vo.getCcto_role() == null ? null : (new RefItemVO(_vo.getCcto_role(), null, _vo.getCcto_role()))); //抄送角色!

		_bean.setParticipate_dynamic(_vo.getParticipate_dynamic() == null ? null : (new RefItemVO(_vo.getParticipate_dynamic(), null, _vo.getParticipate_dynamic()))); //

		_bean.setMessageformat(_vo.getMessageformat());
		_bean.setMessagereceiver(delPrefix(_vo.getMessagereceiver()));

		_bean.setIntercept1(_vo.getIntercept1()); //
		_bean.setIntercept2(_vo.getIntercept2()); //

		_bean.setUserdef01(_vo.getUserdef01()); //
		_bean.setUserdef02(_vo.getUserdef02()); //
		_bean.setUserdef03(_vo.getUserdef03()); //
		_bean.setUserdef04(_vo.getUserdef04()); //
		_bean.setExtconfmap(_vo.getExtconfmap());
	}

	private String addPrefix(String _parstr) {
		if (_parstr != null && !_parstr.trim().equals("")) {
			_parstr = ";" + _parstr;
		}
		return _parstr;
	}

	private String delPrefix(String _parstr) {
		if (_parstr != null && _parstr.startsWith(";")) {
			_parstr = _parstr.substring(1, _parstr.length());
		}
		return _parstr;
	}

	/**
	 * 将连线Bean中的数据复制到VO中去,在右边属性编辑框中编辑某个数据后触发
	 * 
	 * @param _bean
	 * @param _vo
	 */
	private void copyTransitionDataFromBeanToVo(TransitionBean _bean, TransitionVO _vo) {
		_vo.setCode(_bean.getCode()); //
		_vo.setUiname(_bean.getUiname()); //
		_vo.setWfname(_bean.getWfname()); //
		_vo.setDealtype(_bean.getDealtype()); // 处理类型,即有提交与拒绝之分
		_vo.setMailsubject(_bean.getMailsubject()); // 邮件主题
		_vo.setMailcontent(_bean.getMailcontent()); // 邮件内容
		_vo.setFromactivity(_bean.getFromactivity()); //
		_vo.setToactivity(_bean.getToactivity()); //

		_vo.setCondition(_bean.getConditions()); //
		_vo.setReasoncodesql(_bean.getReasoncodesql()); //
		_vo.setIntercept(_bean.getIntercept()); //

		isEditChanged = true; //
	}

	/**
	 * 将连线VO中的数据复制到Bean中去,在图中选择某个连线时触发
	 * 
	 * @param _bean
	 * @param _vo
	 */
	private void copyTransitionDataFromVoToBean(TransitionVO _vo, TransitionBean _bean) {
		_bean.setCode(_vo.getCode()); //
		_bean.setUiname(_vo.getUiname()); //
		_bean.setWfname(_vo.getWfname()); //
		_bean.setDealtype(_vo.getDealtype()); //
		_bean.setMailsubject(_vo.getMailsubject()); // 邮件主题
		_bean.setMailcontent(_vo.getMailcontent()); // 邮件内容
		_bean.setFromactivity(_vo.getFromactivity()); //
		_bean.setToactivity(_vo.getToactivity()); //
		_bean.setConditions(_vo.getCondition()); //
		_bean.setReasoncodesql(_vo.getReasoncodesql()); // //
		_bean.setIntercept(_vo.getIntercept()); // 拦截器
	}

	private DefaultEdge getSelectedTransition() {
		Object obj = getGraph().getSelectionCell();
		if (obj == null) {
			return null;
		}
		if (obj instanceof DefaultEdge) {
			DefaultEdge cell = (DefaultEdge) obj;
			if (cell.getUserObject() instanceof TransitionVO) {
				return cell; //
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private DefaultGraphCell getSelectedActivityCell() {
		Object obj = getGraph().getSelectionCell();
		if (obj == null) {
			return null;
		}
		if (obj instanceof DefaultGraphCell) {
			DefaultGraphCell cell = (DefaultGraphCell) obj;
			if (cell.getUserObject() instanceof ActivityVO) {
				return cell; //
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 高老师提出，需要获得选中的所有环节的信息，故增加该方法【李春娟/2012-10-25】
	 * @return
	 */
	private DefaultGraphCell[] getSelectedActivityCells() {
		Object[] obj = getGraph().getSelectionCells();
		if (obj == null || obj.length == 0) {
			return null;
		}
		ArrayList arrayList = new ArrayList();
		for (int i = 0; i < obj.length; i++) {
			if (obj[i] instanceof DefaultGraphCell) {
				DefaultGraphCell cell = (DefaultGraphCell) obj[i];
				if (cell.getUserObject() instanceof ActivityVO) {//判断是否是环节，如果是才加入【李春娟/2012-10-25】
					arrayList.add(cell);
				}
			}
		}
		if (arrayList.size() == 0) {//如果没有找到直接返回【李春娟/2012-10-25】
			return null;
		}
		return (DefaultGraphCell[]) arrayList.toArray(new DefaultGraphCell[0]);
	}

	private DefaultGraphCell getSelectedDeptCell() {
		Object obj = getGraph().getSelectionCell();
		if (obj == null) {
			return null;
		}
		if (obj instanceof DefaultGraphCell) {
			DefaultGraphCell cell = (DefaultGraphCell) obj;
			if (cell.getUserObject() instanceof GroupVO) {
				GroupVO groupvo = (GroupVO) cell.getUserObject();
				if ("DEPT".equalsIgnoreCase(groupvo.getGrouptype())) {
					return cell; //
				}
			}
			return null;
		}
		return null;
	}

	public void addWorkWolkFlowCellDeleteListener(WorkFlowCellDeleteListener _listener) {
		this.v_cellDeleteListeners.add(_listener);
	}

	/**
	 * 取得选中的结点数据对象
	 * 
	 * @return
	 */
	public ActivityVO getSelectedActivityVO() {
		DefaultGraphCell cell = getSelectedActivityCell(); //
		if (cell == null) {
			return null;
		}
		if (cell.getUserObject() instanceof ActivityVO) {
			return (ActivityVO) cell.getUserObject();
		}

		return null;
	}

	/**
	 * 高老师提出，需要获得选中的所有环节的信息，故增加该方法【李春娟/2012-10-25】
	 * 
	 * @return
	 */
	public ActivityVO[] getSelectedActivityVOs() {
		DefaultGraphCell[] cell = getSelectedActivityCells(); //
		if (cell == null || cell.length == 0) {
			return null;
		}
		ActivityVO[] actvos = new ActivityVO[cell.length];
		for (int i = 0; i < cell.length; i++) {
			actvos[i] = (ActivityVO) cell[i].getUserObject();
		}
		return actvos;
	}

	public DefaultGraphCell createVertex(Object name, double x, double y, double w, double h, Color bg, Color fore, Font _font, boolean raised, String viewClass) {
		return createVertex(name, x, y, w, h, bg, fore, _font, raised, viewClass, true); //
	}

	/**
	 * 创建一个定制的结点
	 */
	public DefaultGraphCell createVertex(Object name, double x, double y, double w, double h, Color bg, Color fore, Font _font, boolean raised, String viewClass, boolean _isaddPort) {
		DefaultGraphCell cell = new DefaultGraphCell(name);
		if (viewClass != null) {
			GPCellViewFactory.setViewClass(cell.getAttributes(), viewClass);
		}

		GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(x, y, w, h)); //
		if (bg != null) {
			GraphConstants.setBackground(cell.getAttributes(), bg);
			GraphConstants.setOpaque(cell.getAttributes(), true);
		}

		if (fore != null) {
			GraphConstants.setForeground(cell.getAttributes(), fore);
		}

		if (_font != null) {
			GraphConstants.setFont(cell.getAttributes(), _font); //
		}

		// Set raised border
		// if (raised) {
		GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createLineBorder(Color.BLACK, 1));
		if (viewClass != null && viewClass.endsWith("8")) {//图片环节不需要边框只有流程监控时候可以有
			GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createEmptyBorder());
		}
		// } else {
		// GraphConstants.setBorderColor(cell.getAttributes(), Color.black);
		// }

		// GraphConstants.setGradientColor(cell.getAttributes(), new Color(102,
		// 255, 102));
		// GraphConstants.setEditable(cell.getAttributes(), true); //

		if (_isaddPort) {
			cell.addPort(); // 增加一个连接点,必须做这一步,否则就不能连接,加入这个后,会在图形的中央多一个黄色的小方框!!这就是Port
		}
		if (checkBox_showorder != null) {
			cell.getAttributes().put("isshoworder", checkBox_showorder.isSelected());
		}
		return cell;
	}

	public JPopupMenu createPopupMenu(final Point pt, final Object cell) {
		JPopupMenu menu = new JPopupMenu();
		if (cell != null) {
			// Edit
			menu.add(new AbstractAction("Edit") {
				public void actionPerformed(ActionEvent e) {
					graph.startEditingAtCell(cell);
				}
			});
		}
		// Remove
		if (!graph.isSelectionEmpty()) {
			menu.addSeparator();
			menu.add(new AbstractAction("Remove") {
				public void actionPerformed(ActionEvent e) {
					// remove.actionPerformed(e);
				}
			});
		}
		menu.addSeparator();
		// Insert
		menu.add(new AbstractAction("Insert") {
			public void actionPerformed(ActionEvent ev) {
				// insert(pt);
			}
		});
		return menu;
	}

	// Hook for subclassers
	protected DefaultEdge createDefaultEdge() {
		return new DefaultEdge();
	}

	// Insert a new Edge between source and target
	public void connect(Port source, Port target, List _list) {
		// Construct Edge with no label

		// if (graph.getModel().acceptsSource(edge, source) &&
		// graph.getModel().acceptsTarget(edge, target)) {
		// Create a Map thath holds the attributes for the edge

		ActivityVO sourceActivityVO = (ActivityVO) ((DefaultGraphCell) graph.getModel().getParent(source)).getUserObject();
		ActivityVO targetActivityVO = (ActivityVO) ((DefaultGraphCell) graph.getModel().getParent(target)).getUserObject();

		TransitionVO transitionVO = new TransitionVO();
		transitionVO.setId(getNewTransitionID()); // 先创建好主键,关键,以前的机制不是这样的
		transitionVO.setCode("WFT"); //
		transitionVO.setWfname(""); //
		transitionVO.setUiname(""); //
		transitionVO.setDealtype("SUBMIT"); //

		transitionVO.setFonttype("System"); //
		transitionVO.setFontsize(12);
		transitionVO.setForeground("0,0,0"); //
		transitionVO.setBackground(convertColorToStr(linecolor)); //

		List points = new ArrayList();
		points.add(new double[] { sourceActivityVO.getX().doubleValue(), sourceActivityVO.getY().doubleValue() }); //
		if (_list != null && _list.size() > 0) {
			for (int i = 0; i < al_draggingPortXY.size(); i++) {
				int[] li_x_y = (int[]) al_draggingPortXY.get(i); //
				points.add(new double[] { li_x_y[0], li_x_y[1] }); //
			}
		}
		points.add(new double[] { targetActivityVO.getX().doubleValue(), targetActivityVO.getY().doubleValue() }); //
		transitionVO.setPoints(points); // 设置折点!!!!

		// GraphConstants.setPoints(edge.getAttributes(),
		// convertToPoint2DList(points));
		transitionVO.setFromactivity(sourceActivityVO.getId());
		transitionVO.setToactivity(targetActivityVO.getId());
		transitionVO.setCondition(""); //

		DefaultEdge edge = new DefaultEdge(transitionVO); // 创建连线
		edge.setSource(source); // 起始位置
		edge.setTarget(target); // 结束位置

		GraphConstants.setLabelAlongEdge(edge.getAttributes(), true); //如果为True则字上斜着的贴着线
		GraphConstants.setEndFill(edge.getAttributes(), true);//连线箭头填充
		//GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_TECHNICAL);//连线箭头为三角
		GraphConstants.setLineEnd(edge.getAttributes(), transitionVO.getLineType()); //【sunfujun/20120426/增加箭头类型的配置_王钢】
		if (transitionVO.isSingle()) {
			GraphConstants.setLineBegin(edge.getAttributes(), GraphConstants.ARROW_NONE);
			GraphConstants.setBeginFill(edge.getAttributes(), false);
		} else {
			GraphConstants.setLineBegin(edge.getAttributes(), transitionVO.getLineType());
			GraphConstants.setBeginFill(edge.getAttributes(), true);
		}
		GraphConstants.setFont(edge.getAttributes(), LookAndFeel.font); //
		GraphConstants.setLineWidth(edge.getAttributes(), 1); //
		GraphConstants.setLineColor(edge.getAttributes(), linecolor);

		GraphConstants.setDisconnectable(edge.getAttributes(), false); // 永远粘住,不能断开!!
		GraphConstants.setPoints(edge.getAttributes(), convertToPoint2DList(points)); //加上折点!!!非常关键,即根据内存中记录的位置!

		// Insert the Edge and its Attributes
		graph.getGraphLayoutCache().insert(edge);//
		//graph.getGraphLayoutCache().insertEdge(edge, source, target);//
		isEditChanged = true; //
		// }
	}

	//按钮点击事件!!
	class ToolBarBtnAction extends AbstractAction {
		ToolBarBtnAction(String _text, Icon _icon) {
			super(_text, _icon); //
		}

		public void actionPerformed(ActionEvent e) {
			onClickToolBarBtn(e); //
		}
	}

	class MyDraggableButton extends JButton implements DragGestureListener, DragSourceListener, DragSourceMotionListener {//【sfj/2012-02-29/修改拖动按钮增加环节导致的性能问题】【sfj/2012-03-01/优化拖动的环节有点闪】
		DragSource dragSource;
		private BufferedImage baseImg = null; //每次拖动时的整个画面的照相

		public MyDraggableButton(Icon _icon) {
			super(_icon); //
			try {
				dragSource = new DragSource();
				dragSource.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
				dragSource.addDragSourceMotionListener(this);
			} catch (Exception e) {
				e.printStackTrace(); //
			}
		}

		public void dragGestureRecognized(DragGestureEvent evt) {
			Transferable t = new StringSelection("aString");
			dragSource.startDrag(evt, Cursor.getDefaultCursor(), t, this);

			Rectangle visRect = graph.getBounds();
			baseImg = new BufferedImage((int) visRect.getWidth(), (int) visRect.getHeight(), BufferedImage.TYPE_INT_RGB);

			if (!(evt.getComponent() instanceof JButton)) {
				return;
			}
			JButton comp = (JButton) evt.getComponent();
			if (!comp.isEnabled()) {
				return;
			}
			comp.setEnabled(false);//去掉按下按钮的效果
			comp.setEnabled(true);
			Point point = comp.getLocation();
			SwingUtilities.convertPointFromScreen(point, graph);//【sfj/2012-02-23/放大导致的拖动问题】
			double li_x = (point.getX()) / graph.getScale(); //
			double li_y = (point.getY()) / graph.getScale(); //
			if (comp == btn_insert_1) {
				onInsertNormalCell(1, li_x, li_y);
			} else if (comp == btn_insert_2) {
				onInsertNormalCell(2, li_x, li_y);
			} else if (comp == btn_insert_3) {
				onInsertNormalCell(3, li_x, li_y);
			} else if (comp == btn_insert_4) {
				onInsertNormalCell(4, li_x, li_y);
			} else if (comp == btn_insert_5) {
				onInsertNormalCell(5, li_x, li_y);
			} else if (comp == btn_insert_6) {
				onInsertNormalCell(6, li_x, li_y);
			} else if (comp == btn_insert_7) {
				onInsertNormalCell(7, li_x, li_y);
			} else if (comp == btn_insert_8) {
				onInsertNormalCell(8, li_x, li_y);
			}
		}

		public void dragEnter(DragSourceDragEvent evt) {
			System.out.println("dragEnter"); //
		}

		public void dragOver(DragSourceDragEvent evt) {
			System.out.println("dragOver"); //
		}

		public void dragExit(DragSourceEvent evt) {
			System.out.println("dragExit"); //
		}

		public void dropActionChanged(DragSourceDragEvent evt) {
			System.out.println("dropActionChanged"); //
		}

		public void dragDropEnd(DragSourceDropEvent evt) {
			DefaultGraphCell cell = WorkFlowDesignWPanel.this.getSelectedActivityCell();
			AttributeMap map = cell.getAttributes();
			Point point = new Point();
			point.x = (int) evt.getX();
			point.y = (int) evt.getY();
			SwingUtilities.convertPointFromScreen(point, graph);
			point.x = (int) (point.x - 35 * graph.getScale());
			point.y = (int) (point.y - 23 * graph.getScale());
			Rectangle2D rec = (Rectangle2D) map.get("bounds");
			GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(point.getX() / graph.getScale() < 0 ? 0 : point.getX() / graph.getScale(), point.getY() / graph.getScale() < 0 ? 0 : point.getY() / graph.getScale(), rec.getWidth(), rec.getHeight())); //
			getGraph().getGraphLayoutCache().toFront(new DefaultGraphCell[] { cell });//【sfj/2012-02-29/直接这样搞就不会出现动环节的时候会跑到部门和阶段后面】
		}

		public void dragMouseMoved(DragSourceDragEvent dsde) {
			DefaultGraphCell cell = WorkFlowDesignWPanel.this.getSelectedActivityCell();
			AttributeMap map = cell.getAttributes();
			Rectangle2D rec = (Rectangle2D) map.get("bounds");
			Point point = dsde.getLocation();
			SwingUtilities.convertPointFromScreen(point, graph);
			Graphics2D graph_g2 = (Graphics2D) graph.getGraphics();
			BasicGraphUI graphui = (BasicGraphUI) graph.getUI(); //
			Graphics2D baseImg_g2 = baseImg.createGraphics();
			graphui.paint(baseImg_g2, graph);//将流程图画到图片上
			baseImg_g2.scale(graph.getScale(), graph.getScale());//不这样搞环节不能放大与缩小
			graphui.paintCell(baseImg_g2, graph.getGraphLayoutCache().getMapping(cell, true), new Rectangle2D.Double(point.getX() / graph.getScale() - 0.5 * rec.getWidth(), point.getY() / graph.getScale() - 0.5 * rec.getHeight(), 70, 45), false);//将拖动的环节画到图片上
			baseImg_g2.dispose();
			graph_g2.drawImage(baseImg, 0, 0, null);//将画好的图片画到流程图上，就不会闪了
			graph_g2.dispose(); //
		}
	}

	class MyGraph extends JGraph {
		XCHDropTargetListener xchListener = null;

		public MyGraph(GraphModel model) {
			this(model, null);
			// this.setDropTarget(); //
			xchListener = new XCHDropTargetListener(); //
			// new DropTarget(this, xchListener); //
			// this.setDropTarget(new DropTarget(new JLabel(), xchListener));
		}

		public MyGraph(GraphModel model, GraphLayoutCache cache) {
			super(model, cache);
			setGridVisible(true); //
			setPortsScaled(false); //
			setPortsVisible(true);
			setGridEnabled(true);
			setGridSize(6);
			setTolerance(2);
			setInvokesStopCellEditing(true);
			setCloneable(false); // 不允许复制!
			setEditable(false); //
			setJumpToDefaultPort(true);

			this.addMouseMotionListener(new MouseMotionListener() {

				public void mouseDragged(MouseEvent e) {
					DefaultGraphCell cell = (DefaultGraphCell) graph.getSelectionCell();
					if (cell != null) {
						AttributeMap map = cell.getAttributes();
						Rectangle2D rec = (Rectangle2D) map.get("bounds");
						if (rec != null) {
							GraphConstants.setBounds(map, new Rectangle2D.Double(rec.getX(), rec.getY(), rec.getWidth(), rec.getHeight()));
						}
					}
				}

				public void mouseMoved(MouseEvent e) {
				}
			});

		}
	}

	class XCHDropTargetListener implements DropTargetListener {
		public XCHDropTargetListener() {

		}

		public void dragEnter(DropTargetDragEvent evt) {
			// Called when the user is dragging and enters this drop target.
			// System.out.println("DropTargetDragEvent");
		}

		public void dragOver(DropTargetDragEvent evt) {
			// Called when the user is dragging and moves over this drop target.
			// System.out.println("dragOver");
		}

		public void dragExit(DropTargetEvent evt) {
			// Called when the user is dragging and leaves this drop target.
			// System.out.println("DropTargetEvent");
		}

		public void dropActionChanged(DropTargetDragEvent evt) {
			// Called when the user changes the drag action between copy or
			// move.
			// System.out.println("DropTargetDragEvent");
		}

		public void drop(DropTargetDropEvent evt) {
			// Called when the user finishes or cancels the drag operation.
			// System.out.println("drop"); //
			// System.out.println("DropTargetDropEvent");
		}
	}

	// A Custom Model that does not allow Self-References
	public class MyModel extends DefaultGraphModel {
		// Override Superclass Method
		public boolean acceptsSource(Object edge, Object port) {
			// Source only Valid if not Equal Target
			return (((Edge) edge).getTarget() != port);
		}

		// Override Superclass Method
		public boolean acceptsTarget(Object edge, Object port) {
			// Target only Valid if not Equal Source
			return (((Edge) edge).getSource() != port);
		}
	}

	/**
	 * 最重要也最复杂的一个类!!!
	 * 画线,连线的逻辑都在这里面!!!
	 * @author xch
	 */
	public class MyMarqueeHandler extends BasicMarqueeHandler {

		// Holds the Start and the Current Point
		protected Point2D startPoint, currentPoint; // 位置,记住开始位置与当前位置

		// Holds the First and the Current Port
		protected PortView firstPort, currentPort; //

		private double li_lastX, li_lastY = -1;

		private DefaultGraphCell firstCell, secondCell = null; // 两个结点!!

		// // Override to Gain Control (for PopupMenu and ConnectMode)
		public boolean isForceMarqueeEvent(MouseEvent e) {
			//System.out.println("调用了isForceMarqueeEvent...."); //
			// if (e.isShiftDown())
			// return false;
			// // If Right Mouse Button we want to Display the PopupMenu
			// if (SwingUtilities.isRightMouseButton(e))
			// // Return Immediately
			// return true;
			// // Find and Remember Port
			// port = getSourcePortAt(e.getPoint());
			// //取得一个连接点!!!非常关键,但后来发现getSourcePortAt()这个方法有时好象有问题,即按住拖动时,死活出不了线! 那是因为那个Port在中间,很难捕获到!!!
			// // If Port Found and in ConnectMode (=Ports Visible)
			// if (port != null && graph.isPortsVisible())
			// return true;
			// // Else Call Superclass
			// return super.isForceMarqueeEvent(e);

			if (e.isControlDown()) { // 如果是按住了Ctrl键
				//				currentPort = null; //
				//				Object obj = graph.getFirstCellForLocation(e.getPoint().getX(), e.getPoint().getY()); //
				//				if (obj != null && obj instanceof DefaultGraphCell) {
				//					DefaultGraphCell cell = (DefaultGraphCell) obj; //
				//					currentPort = graph.getDefaultPortForCell(cell); //
				//				}
				currentPort = getTargetPortAt(e.getPoint()); // 以前使用getSourcePortAt()时好象总有问题,因为这个方法定义了如果有多个连接点时不能拖线!!!而getTargetPortAt()则总是找第一个Port,所以总是成功!!!!
				if (currentPort != null) { // 如果成功找到,则返回True
					graph.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
					return true;
				} else {
					graph.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
					return false;
				}
			} else { // 当不按住Ctrl键时,根本不触发mousePressed事件!
				currentPort = null;
				graph.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
				return false;
			}
		}

		/**
		 * 当鼠标键往下按时!!!!
		 */
		// Display PopupMenu or Remember Start Location and First Port
		public void mousePressed(final MouseEvent e) {
			// If Right Mouse Button
			if (SwingUtilities.isRightMouseButton(e)) { // 如果是右键
				super.mousePressed(e);
			} else if (currentPort != null && graph.isPortsVisible()) {
				startPoint = graph.toScreen(currentPort.getLocation()); // Remember
				// System.out.println("找到启动点位置" + startPoint + ",并成功为startPoint绑定值!!"); //Start Location
				firstPort = currentPort; // Remember First Port
			} else {
				super.mousePressed(e); // Call Superclass
			}
		}

		// // Find Port under Mouse and Repaint Connector
		/**
		 * 拖动时!!!
		 */
		public void mouseDragged(MouseEvent e) {
			//System.out.println("拖动..."); //
			// If remembered Start Point is Valid
			if (startPoint != null) {
				isDragging = true; //
				if (graph.getCursor().getType() != Cursor.HAND_CURSOR) { //)setCursor(new Cursor(Cursor.MOVE_CURSOR)
					graph.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
				}
				draggingPoint = e.getPoint(); //
				// Fetch Graphics from Graph
				Graphics g = graph.getGraphics();
				// Reset Remembered Port
				PortView newPort = getTargetPortAt(e.getPoint()); // 新的连接点为当前鼠标移动到的位置!!!
				// Do not flicker (repaint only on real changes)
				if (newPort == null || newPort != currentPort) {
					// Xor-Paint the old Connector (Hide old Connector)
					paintConnector(Color.black, graph.getBackground(), g); // 重绘连线,不能少,否则旧的线会去不掉!

					// If Port was found then Point to Port Location
					currentPort = newPort;
					if (currentPort != null) {
						currentPoint = graph.toScreen(currentPort.getLocation()); // 如果发现新的连接点,则将currentPoint指定为新的连接点的位置
						// Else If no Port was found then Point to Mouse Location
					} else {
						currentPoint = graph.snap(e.getPoint()); // 如果没发现新的连接点,则将currentPoint指定为当前移动到的位置!!
					}

					//Xor-Paint the new Connector...
					paintConnector(graph.getBackground(), Color.black, g); // 重绘连线!!!
				} else {
					if (newPort == currentPort) { //不能自连接了???
						// System.out.println("新的连接点等于当前连接点!!");
						// //一旦移上目标环节,则再移动时不做任何处理!!
					}
				}
				isEditChanged = true; //
			}
			// Call Superclass
			super.mouseDragged(e);
		}

		// // Connect the First Port and the Current Port in the Graph or
		// Repaint
		public void mouseReleased(MouseEvent e) {
			//System.out.println("松开鼠标了..."); //
			// If Valid Event, Current and First Port
			if (e != null && currentPort != null && firstPort != null && firstPort != currentPort) { // 当释放时,发现两个点都有,则将两个点连起来!!!
				// Then Establish Connection
				connect((Port) firstPort.getCell(), (Port) currentPort.getCell(), al_draggingPortXY); //将两个结点连起来!!!!!至关重要!!!
				e.consume();
				// Else Repaint the Graph
			} else {
				graph.repaint();
			}
			isEditChanged = true; //
			// Reset Global Vars
			firstPort = null;
			currentPort = null;
			startPoint = null;
			currentPoint = null;
			isDragging = false; //
			al_draggingPortXY.clear(); //
			graph.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
			// Call Superclass
			super.mouseReleased(e);
		}

		// Show Special Cursor if Over Port
		public void mouseMoved(MouseEvent e) {
			//System.out.println("移动...");  //
			graph.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
			//			PortView tempPortView = getTargetPortAt(e.getPoint()); // 以前使用getSourcePortAt()时好象总有问题,因为这个方法定义了如果有多个连接点时不能拖线!!!而getTargetPortAt()则总是找第一个Port,所以总是成功!!!!
			//			if (tempPortView != null) { // 如果成功找到,则返回True
			//				System.out.println("移动找到了...");  //
			//				graph.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
			//			} else {
			//				graph.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
			//			}

			// if (e != null && getSourcePortAt(e.getPoint()) != null &&
			// graph.isPortsVisible()) {
			// graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
			// e.consume();
			// } else {
			// super.mouseMoved(e);
			// }
		}

		// Use Xor-Mode on Graphics to Paint Connector
		protected void paintConnector(Color fg, Color bg, Graphics g) {
			// Set Foreground
			g.setColor(fg);
			// Set Xor-Mode Color
			g.setXORMode(bg);
			// Highlight the Current Port
			paintPort(graph.getGraphics()); //

			// If Valid First Port, Start and Current Point
			if (firstPort != null && startPoint != null && currentPoint != null) {
				// Then Draw A Line From Start to Current Point
				if (al_draggingPortXY.size() == 0) { //如果没打断点
					g.drawLine((int) startPoint.getX(), (int) startPoint.getY(), (int) currentPoint.getX(), (int) currentPoint.getY()); //就是画一个线!!! 可以考虑算法,自动画折线!!
				} else {
					int[] li_xy_1 = (int[]) al_draggingPortXY.get(0); //
					g.drawLine((int) startPoint.getX(), (int) startPoint.getY(), li_xy_1[0], li_xy_1[1]); //就是画一个线!!! 可以考虑算法,自动画折线!!

					for (int i = 1; i < al_draggingPortXY.size(); i++) {
						int[] li_xy_a = (int[]) al_draggingPortXY.get(i - 1); //
						int[] li_xy_b = (int[]) al_draggingPortXY.get(i); //
						g.drawLine(li_xy_a[0], li_xy_a[1], li_xy_b[0], li_xy_b[1]); //就是画一个线!!! 可以考虑算法,自动画折线!!
					}

					int[] li_xy_2 = (int[]) al_draggingPortXY.get(al_draggingPortXY.size() - 1); //
					g.drawLine(li_xy_2[0], li_xy_2[1], (int) currentPoint.getX(), (int) currentPoint.getY()); //就是画一个线!!! 可以考虑算法,自动画折线!
				}
				isEditChanged = true; //
			}
		}

		// Use the Preview Flag to Draw a Highlighted Port
		protected void paintPort(Graphics g) {
			// If Current Port is Valid
			if (currentPort != null) {
				// If Not Floating Port...
				boolean o = (GraphConstants.getOffset(currentPort.getAllAttributes()) != null);
				// ...Then use Parent's Bounds
				Rectangle2D r = (o) ? currentPort.getBounds() : currentPort.getParentView().getBounds();
				// Scale from Model to Screen
				r = graph.toScreen((Rectangle2D) r.clone());
				// Add Space For the Highlight Border
				r.setFrame(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r.getHeight() + 6); //将目标框周围多个淡淡的边框,以表示碰到了!!!
				// Paint Port in Preview (=Highlight) Mode
				graph.getUI().paintCell(g, currentPort, r, true);
				//if (currentPort != firstPort) {
				graph.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
				//}
				isEditChanged = true; //
			} else {
				graph.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
			}
		}

		public PortView getSourcePortAt(Point2D point) {
			// Disable jumping
			graph.setJumpToDefaultPort(false);
			PortView result;
			try {
				// Find a Port View in Model Coordinates and Remember
				result = graph.getPortViewAt(point.getX(), point.getY());
			} finally {
				graph.setJumpToDefaultPort(true);
			}
			return result;
		}

		// Find a Cell at point and Return its first Port as a PortView
		protected PortView getTargetPortAt(Point2D point) {
			// Find a Port View in Model Coordinates and Remembe
			return graph.getPortViewAt(point.getX(), point.getY());
		}
	}

	class MyGraphUI extends BasicGraphUI {

		public MyGraphUI() {
			super();
		}

		public CellHandle createHandle(GraphContext context) {
			try {
				return new RootHandle(context) {
					private static final long serialVersionUID = -7540850313657538962L;

					private boolean isDragging = false; //
					private boolean isDraggGroup = false; //
					private boolean isDraggDept = false;
					private boolean isDraggStation = false;

					@Override
					protected void startDragging(MouseEvent event) {
						//System.out.println("startDragging"); //
						super.startDragging(event);
						isEditChanged = true; //
					}

					@Override
					public void mouseDragged(MouseEvent event) {
						super.mouseDragged(event);
						isEditChanged = true; //
						if (!isDragging) { //如果第一次就记录下是否是拖动状态,这样下面就不会再做了,提高性能!!
							isDragging = true; //记录表示正处于拖动中的状态!!!
							Object[] objs = graph.getSelectionCells();
							for (Object selobj : objs) {
								if (selobj instanceof DefaultGraphCell) {
									DefaultGraphCell cell = (DefaultGraphCell) selobj; //
									if (cell.getUserObject() instanceof GroupVO) { //
										if (isDraggDept && isDraggDept) {
											return;
										}
										isDraggGroup = true; //如果发现拖动的组,则记录下!
										if ("DEPT".equals(((GroupVO) cell.getUserObject()).getGrouptype())) {
											isDraggDept = true;
										}
										if ("STATION".equals(((GroupVO) cell.getUserObject()).getGrouptype())) {
											isDraggStation = true;
										}
									}
								}
							}
						}
					}

					@Override
					public void mouseMoved(MouseEvent _event) {
						super.mouseMoved(_event); //
						//isEditChanged = true; //
					}

					@Override
					public void overlay(Graphics g) {
						super.overlay(g);
						isEditChanged = true; //
						//System.out.println("overlay"); //
					}

					@Override
					public void mousePressed(MouseEvent event) {
						super.mousePressed(event);
					}

					@Override
					public void mouseReleased(MouseEvent event) {//【sunfujun/20120419/优化undo问题】
						isDragging = false; //
						if (isDraggGroup) { //如果拖动的组!!!
							Map attributes = new HashMap();
							if (isDraggDept) {
								attributes.put(new DefaultGraphCell(), new HashMap());
								getDeptCells(attributes);
							}
							if (isDraggStation) {
								getStationCells(attributes);
							}
							graph.getGraphLayoutCache().edit(attributes, null, null, null);
							super.mouseReleased(event);
							Object firstobj = graph.getEditingCell(); //
							if (firstobj != null && firstobj instanceof DefaultGraphCell)
								resetMyPosAndSize((DefaultGraphCell) firstobj); //
							Object[] objs = graph.getSelectionCells();
							for (Object selobj : objs)
								resetMyPosAndSize((DefaultGraphCell) selobj); //
						} else {
							super.mouseReleased(event);
						}
						isDraggGroup = false;
						isDraggDept = false;
						isDraggStation = false;
						if (event.getButton() == MouseEvent.BUTTON3) { // 如果点击的是右键
							event.consume(); //必须调一下这个,否则第二次右击键 时会跑到后面的矩阵中去!
						}
						isEditChanged = true; //
					}

				};
			} catch (NullPointerException e) {
				// e.printStackTrace(); //
				// ignore for now...
				return null;
			}
		}
	}

	/**
	 * 用户VO
	 * 
	 * @author xch
	 * 
	 */
	class USERVO extends AbstractTMO {
		private static final long serialVersionUID = 8057184541083294474L;

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "USER_VO"); // 模版编码,请勿随便修改
			vo.setAttributeValue("templetname", "用户"); // 模板名称
			vo.setAttributeValue("tablename", "pub_user"); // 查询数据的表(视图)名
			vo.setAttributeValue("pkname", null); // 主键名
			vo.setAttributeValue("pksequencename", null); // 序列名
			vo.setAttributeValue("savedtablename", null); // 保存数据的表名
			vo.setAttributeValue("listcustpanel", null); // 列表自定义面板
			vo.setAttributeValue("cardcustpanel", null); // 卡片自定义面板
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "CODE"); // 唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "编码"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "100"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "NAME"); // 唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "姓名"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "100"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}
	}

	/**
	 * 用户VO
	 * 
	 * @author xch
	 * 
	 */
	class ROLEVO extends AbstractTMO {
		private static final long serialVersionUID = 8057184541083294474L;

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "ROLE_VO"); // 模版编码,请勿随便修改
			vo.setAttributeValue("templetname", "角色"); // 模板名称
			vo.setAttributeValue("tablename", "pub_role"); // 查询数据的表(视图)名
			vo.setAttributeValue("pkname", null); // 主键名
			vo.setAttributeValue("pksequencename", null); // 序列名
			vo.setAttributeValue("savedtablename", null); // 保存数据的表名
			vo.setAttributeValue("listcustpanel", null); // 列表自定义面板
			vo.setAttributeValue("cardcustpanel", null); // 卡片自定义面板
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "CODE"); // 唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "编码"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "100"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "NAME"); // 唯一标识,用于取数与保存
			itemVO.setAttributeValue("itemname", "名称"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "100"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "Y"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}
	}

	public JCheckBox getCheckBox_lockgroup() {
		return checkBox_lockgroup;
	}

	public void setCheckBox_lockgroup(JCheckBox checkBox_lockgroup) {
		this.checkBox_lockgroup = checkBox_lockgroup;
	}

	public JButton getBtn_save() {
		return btn_save;
	}

	public void setBtn_save(JButton btn_save) {
		this.btn_save = btn_save;
	}

	/**
	 * 获得增加机构和岗位的按钮
	 * @return
	 */
	public JButton getBtn_insert_0() {
		return btn_insert_0;
	}

	public ArrayList getAddActivityIds() {
		return addActivityIds;
	}

	public JButton addBarBtn(String _text, Icon _icon) {
		JButton btn_adddept = toolBar.add(new ToolBarBtnAction(_text, _icon));
		return btn_adddept;

	}

	public void setShowpreviewdept(boolean showpreviewdept) {
		this.showpreviewdept = showpreviewdept;
	}

	/**
	 * 设置是否显示上面的工具条
	 * @param _showtoolbox
	 */
	public void setShowtoolbar(boolean _showtoolbar) {
		if (this.showtoolbar == _showtoolbar) {
			return;
		}

		this.showtoolbar = _showtoolbar;
		if (showtoolbar) {//如果设置为显示工具条
			if (toolBarPanel == null) {//构造时没有显示工具条，则需要加入工具条
				this.add(getNorthToolBar(), BorderLayout.NORTH); // 上方是工具条
			}
			getNorthToolBar().setVisible(true); //
		} else if (toolBarPanel != null) {//如果设置为不显示工具条，并且本身已经显示着，则需要隐藏
			getNorthToolBar().setVisible(false); //
		}
	}

	/**
	 * 设置是否显示流程属性和工具箱
	 * @param _showtoolbox
	 */
	public void setShowtoolbox(boolean _showtoolbox) {
		if (this.showtoolbox == _showtoolbox) {
			return;
		}

		this.showtoolbox = _showtoolbox;

		if (showtoolbox) {//判断是否显示工具箱，如果显示的话，需要加上左边的流程属性和右边工具箱（环节属性和连线属性）
			this.add(getWestPanel(), BorderLayout.WEST); // 左边是流程属性
			splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			splitPanel.setDividerLocation(9999); //
			splitPanel.setDividerSize(5);
			splitPanel.setResizeWeight(1D); // 屏幕变大时,右边的大小不变!
			splitPanel.setLeftComponent(getMainGraphPanel()); //
			splitPanel.setRightComponent(getEastPanel()); //
			this.add(splitPanel, BorderLayout.CENTER); // 中间是Graph编辑器
			this.add(getEastToolBox(), BorderLayout.EAST); // 右边是环节属性和连线属性的工具箱
		} else {
			this.add(getMainGraphPanel(), BorderLayout.CENTER); //
			if (panel_west != null) {//先判断左边的流程属性是否存在
				panel_west.setVisible(false);
			}
			if (toftPanel_2 != null) {//右边环节及连线卡片布局的底层面板
				toftPanel_2.setVisible(false);
			}
			if (toolBar2 != null) {//环节及连线工具箱
				toolBar2.setVisible(false);
			}
		}
	}

	public void setStationheight(int _stationheight) {
		this.stationheight = _stationheight;
	}

	public boolean isCanSetCellColor() {
		return isCanSetCellColor;
	}

	public void setCanSetCellColor(boolean _isCanSetCellColor) {
		this.isCanSetCellColor = _isCanSetCellColor;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String _groupName) {
		this.groupName = _groupName;
		btn_adddept.setToolTipText("增加" + groupName);//这里必须设置一下
	}

	public boolean isUndoRedoVisible() {
		return undoRedoVisible;
	}

	public void setUndoRedoVisible(boolean _undoRedoVisible) {
		this.undoRedoVisible = _undoRedoVisible;
		this.btn_undo.setVisible(undoRedoVisible);
		this.btn_redo.setVisible(undoRedoVisible);
	}

	public String getHelpInfo() {
		return helpInfo;
	}

	public void setHelpInfo(String _helpInfo) {
		this.helpInfo = _helpInfo;
	}

	public boolean isGridVisible() {
		return gridVisible;
	}

	public void setGridVisible(boolean _gridVisible) {
		this.gridVisible = _gridVisible;
		graph.setGridVisible(gridVisible);
	}

	public void setProcesscode(String _processcode) {
		this.currentProcessVO.setCode(_processcode);//必须设置一下，否则流程保存后又变为修改前的编码了【李春娟/2012-03-19】
	}

	/**
	 * 添加修改流程名称的方法，同时注意流程图上的流程名称也要修改掉。【李春娟/2012-03-19】
	 * @param _processname
	 */
	public void setProcessname(String _processname) {
		this.currentProcessVO.setName(_processname);//必须设置一下，否则流程保存后又变为修改前的名称了
		String str_title = "流程名称:" + _processname;
		titleCell.setUserObject(str_title);
		graph.getGraphLayoutCache().insert(titleCell); //这里需要将流程图上面的流程名称也修改掉！
	}

	/**
	 * 获得选择发生变化事件【李春娟/2012-04-11】
	 */
	public Vector getVector_cellSelectedListener() {
		return vector_cellSelectedListener;
	}
}
