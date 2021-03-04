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
 * �����������������!�ؼ���
 * 
 * @author xch
 * 
 */
public class WorkFlowDesignWPanel extends JPanel implements GraphSelectionListener, KeyListener, BillPropEditListener {

	private static final long serialVersionUID = -7539768612311088686L;

	private JPanel toolBarPanel = null; //����Ĺ�����
	private JButton btn_new, btn_open, btn_save; //

	private JPanel mainGraphPanel = null;//
	private JPanel panel_west = null;
	private JToolBar toolBar2 = null;

	private JGraph graph = null; //
	private DefaultGraphCell titleCell;
	private boolean showtoolbar = true; //�Ƿ���ʾ������
	private boolean showtoolbox = true; //�Ƿ���ʾ������,��ǰ�����õ�showtoolbar�����߼�������ʾ�����䣬��Щ���壬���������˲���
	private MyGraphUI myGraphUI = null;

	private GraphUndoManager undoManager = null; // ����

	private int cellNumber = 0; // ��¼һ�������˼������.	
	private WLTButton btn_processprop = null;
	private WLTButton btn_toolbox1; // btn_toolbox2, btn_toolbox3 = null;	//������,���̷���,���շ���

	private JSplitPane splitPanel = null;
	private Vector vector_cellSelectedListener = new Vector(); // ѡ�����仯�¼�
	private Vector vector_celldoubleclickListener = new Vector(); //

	protected CardLayout cardlayout_1 = null; // ��Ƭ����,�ǳ��ؼ�!
	private JPanel toftPanel_1 = null; // ��Ƭ���ֵĵײ����!!!
	private BillPropPanel processPropPanel = null; // ��ʾ�������Ե����!!
	private Color deptGroupBgcolor = new Color(232, 238, 247); //200, 214, 234
	private Color linecolor = new Color(70, 119, 191); //
	private Color startCellBgcolor = new Color(225, 255, 225); //
	private Color endCellBgcolor = new Color(225, 255, 225);
	private Color normalCellBgcolor = new Color(232, 238, 247);

	protected CardLayout cardlayout_2 = null; // ��Ƭ����,�ǳ��ؼ�!
	private JPanel toftPanel_2 = null; // ��Ƭ���ֵĵײ����!!!
	private BillPropPanel activityBillPropPanel = null; // �����������
	private BillPropPanel transitionBillPropPanel = null; // �����������

	// private BillPropPanel processAnalysisBillPropPanel = null; //���̷���
	// private BillPropPanel riskAnalysisBillPropPanel = null; //���շ���

	private JButton btn_adddept, btn_addstation, btn_insert_0, btn_insert_1, btn_insert_2, btn_insert_3, btn_insert_4, btn_insert_5, btn_insert_6, btn_insert_7, btn_insert_8, btn_samex, btn_samey, btn_samew, btn_sameh, btn_zoom, btn_zoomin, btn_zoomout, btn_undo, btn_redo, btn_delete, btn_help, btn_importbackimg;
	private JCheckBox checkBox_lockgroup, checkBox_showorder = null;//sunfujun/20120621/��Ϊ��Щʱ��������ϵ�ļ������������⣬����һ����ť���Կ��ٲ鿴ÿ�����ڵ�����_wg
	private ProcessVO currentProcessVO = null; // ��ǰ����VO..

	//��������� �����/2012-11-12��
	private JScrollPane staffScrollPanel = null;
	private JViewport staffXViewport, staffYViewport = null;
	private DefaultGraphCell dlineCell;

	// private Vector v_deptgroups = new Vector(); //���ż���..
	// private Vector v_stationgroups = new Vector(); //�׶μ���..

	private int groupcount = 0; //
	private int stationcount = 0; //
	public int li_opentype = 0;

	private DefaultGraphCell mouseRightClickedCell; // ����Ҽ����ʱѡ�еĲ˵�
	private DefaultGraphCell stationBackGroundCell = null; //�׶εı�������!!

	private boolean isEditChanged = false; //�ж������Ƿ�仯

	private String processId = "";
	private String processCode = "";
	private String processName = "";
	private String sr_type = "";

	private boolean isDragging = false; //�Ƿ����������ڵĹ�����....
	private Point draggingPoint = null; //
	private ArrayList al_draggingPortXY = new ArrayList(); //

	private JToolBar toolBar = new JToolBar("abc", JToolBar.HORIZONTAL); // ������
	private Vector v_cellDeleteListeners = new Vector(); // ɾ�����̶����¼�

	private Object[] copyCach = null;//����
	private ArrayList addActivityIds = new ArrayList();//�����Ļ��ڣ����������水ť�ˣ������
	private boolean iscopy = false;
	private boolean showpreviewdept = true;//��Ϊũ��һͼ������û��ģ�������ӱ�������ע���ֵ䡢ƽ̨�������ñ�ȣ������ڱ����������˸����ԣ������������ж��Ƿ���ʾԤ�貿��
	private int stationheight = 10;//ũ�������ÿ�������׶εļ��Ҫ��������
	private boolean isCanSetCellColor = true;//ũ��������༭���Ž׶μ�����ʱ��Ҫ����������ɫ�ͱ�����ɫ����ֹ����ͼ���༭�Ļ�����ڵģ������ô˲���
	private String groupName = "����";//����������ƣ��еĿͻ��в��ţ��еĽ�ְ�ܣ�ũ�У��������ô˲���
	private boolean undoRedoVisible = true;//�������ָ���ť�Ƿ�ɼ���������ť����bug�����Խ�����������ظð�ť
	private String helpInfo = null;//�ͻ�����˵����̫���ף���Ҫ�Զ���һЩ������Ϣ��ע����Ϣ
	private boolean gridVisible = true;//���������Ƿ�ɼ���Ĭ�Ͽɼ�
	private boolean isHaveLoaded = false;//����ͼ������֮��Ž���undo�¼����ռ�
	private int lineType[] = new int[] { 2, 1, 4, 5, 7, 8, 9, 0 }; //�����ߵĶ������ͣ�Ϊ�˺ͱ༭����������һ�¡���Ū��ô��˳�򡣾�������ֺ���鿴GraphConstants�еĳ�����ARROW��ͷ�ģ�

	private TBUtil tbUtil = null; //

	/**
	 * Ĭ��ҳ��
	 */
	public WorkFlowDesignWPanel() {
		li_opentype = 1;
		init(); // ��ʼ��ҳ��
	}

	public WorkFlowDesignWPanel(boolean _showtoolbox) {
		li_opentype = 2;
		showtoolbox = _showtoolbox; //
		init(); // ��ʼ��ҳ��
	}

	/**
	 * �鿴һ������ģ��!!
	 * 
	 * @param _processId
	 */
	public WorkFlowDesignWPanel(String _processCode) {
		li_opentype = 2;
		init(); // ��ʼ��ҳ��
		loadGraphByCode(_processCode); //
	}

	/**
	 * �鿴һ������ģ��!!
	 * 
	 * @param _processId
	 */
	public WorkFlowDesignWPanel(String _processCode, boolean _showtoolbox) {
		li_opentype = 2;
		showtoolbox = _showtoolbox; //

		init(); // ��ʼ��ҳ��
		loadGraphByCode(_processCode); //
	}

	public WorkFlowDesignWPanel(boolean _showtoolbox, String _processId, String _processCode, String _processName, String _type) {

		processId = _processId;
		processCode = _processCode;
		processName = _processName;
		li_opentype = 2;
		showtoolbox = _showtoolbox; //
		sr_type = _type;
		init(); // ��ʼ��ҳ��
	}

	/**
	 * ��ʼ��ҳ��!!������
	 */
	public void init() {
		this.setLayout(new BorderLayout()); // ���ò�����!
		if (showtoolbox) {//�ж��Ƿ���ʾ�����䣬�����ʾ�Ļ�����Ҫ������ߵ��������Ժ��ұ߹����䣨�������Ժ��������ԣ�
			if (this.showtoolbar) {//�ж��Ƿ���ʾ����Ĺ�����
				this.add(getNorthToolBar(), BorderLayout.NORTH); // �Ϸ��ǹ�����
			}
			this.add(getWestPanel(), BorderLayout.WEST); // �������������
			splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			splitPanel.setDividerLocation(9999); //
			splitPanel.setDividerSize(5);
			splitPanel.setResizeWeight(1D); // ��Ļ���ʱ,�ұߵĴ�С����!
			splitPanel.setLeftComponent(getMainGraphPanel()); //
			splitPanel.setRightComponent(getEastPanel()); //
			this.add(splitPanel, BorderLayout.CENTER); // �м���Graph�༭��
			this.add(getEastToolBox(), BorderLayout.EAST); // �ұ��ǻ������Ժ��������ԵĹ�����
		} else {
			if (this.showtoolbar) {//�ж��Ƿ���ʾ����Ĺ�����
				this.add(getNorthToolBar(), BorderLayout.NORTH);
			}
			this.add(getMainGraphPanel(), BorderLayout.CENTER); //			
		}
		setGridVisible(true);//���ñ������ӿɼ�
	}

	public void loadGraphByProcessVO(ProcessVO _vo) {
		currentProcessVO = _vo; //
		if (currentProcessVO != null) {
			openMainGraph(currentProcessVO, 2); // ����һ������VO,��һ��ͼ��!!
		}
	}

	/**
	 * ����ͼ��
	 * 
	 * @param _code
	 */
	public void loadGraphByCode(String _code) {
		try {
			WorkFlowServiceIfc wfservice = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class); //
			currentProcessVO = wfservice.getWFProcessByWFCode(_code); //
			if (currentProcessVO != null) {
				openMainGraph(currentProcessVO); // ����һ������VO,��һ��ͼ��!!
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
				openMainGraph(currentProcessVO, _type); // ����һ������VO,��һ��ͼ��!!
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
				openHistoryMainGraph(currentProcessVO); // ����һ������VO,��һ��ͼ��!!
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void setToolBarVisiable(boolean _visiable) {
		this.setShowtoolbar(_visiable);
	}

	//����������ϵİ�ť!!!
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
			onAddDeptAndPost(); //���ӻ������λ
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
	 * �Ϸ��Ĺ�����
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
		btn_new.setToolTipText("�½�����");
		btn_open.setToolTipText("������");
		btn_save.setToolTipText("����");

		toolBar.add(btn_new); //
		toolBar.add(btn_open); //
		toolBar.add(btn_save); //

		toolBar.addSeparator(new Dimension(10, 10)); //

		btn_adddept = toolBar.add(new ToolBarBtnAction("AddDept", UIUtil.getImage("workflow/add_dept.gif"))); //  UIUtil.getImage("workflow/add_down.gif")));  //���Ӳ���
		btn_addstation = toolBar.add(new ToolBarBtnAction("AddStation", UIUtil.getImage("workflow/add_station.gif"))); //UIUtil.getImage("workflow/connecton.gif"))); // ���ӽ׶�
		btn_adddept.setToolTipText("����" + groupName);
		btn_addstation.setToolTipText("���ӽ׶�");

		btn_insert_0 = toolBar.add(new ToolBarBtnAction("������λ", UIUtil.getImage("workflow/add_dept_post.gif")));// UIUtil.getImage("workflow/spli2.gif"))); // ���Ӳ���
		btn_insert_0.setToolTipText("������λ"); //

		btn_insert_1 = new MyDraggableButton(UIUtil.getImage("workflow/cellview_1.gif"));
		btn_insert_1.setPreferredSize(new Dimension(20, 20)); //
		btn_insert_1.setToolTipText("����"); //

		btn_insert_2 = new MyDraggableButton(UIUtil.getImage("workflow/cellview_2.gif"));
		btn_insert_2.setPreferredSize(new Dimension(20, 20)); //
		btn_insert_2.setToolTipText("����"); //

		btn_insert_3 = new MyDraggableButton(UIUtil.getImage("workflow/cellview_3.gif"));
		btn_insert_3.setPreferredSize(new Dimension(20, 20)); //
		btn_insert_3.setToolTipText("�ж�"); //

		btn_insert_4 = new MyDraggableButton(UIUtil.getImage("workflow/cellview_4.gif"));
		btn_insert_4.setPreferredSize(new Dimension(20, 20)); //
		btn_insert_4.setToolTipText("��ע"); //

		btn_insert_5 = new MyDraggableButton(UIUtil.getImage("workflow/cellview_5.gif"));
		btn_insert_5.setPreferredSize(new Dimension(20, 20)); //
		btn_insert_5.setToolTipText("���⻷��"); //

		btn_insert_6 = new MyDraggableButton(UIUtil.getImage("workflow/cellview_6.gif"));
		btn_insert_6.setPreferredSize(new Dimension(20, 20)); //
		btn_insert_6.setToolTipText("����"); //

		btn_insert_7 = new MyDraggableButton(UIUtil.getImage("workflow/cellview_7.gif"));
		btn_insert_7.setPreferredSize(new Dimension(20, 20)); //
		btn_insert_7.setToolTipText("�ĵ�"); //

		btn_insert_8 = new MyDraggableButton(UIUtil.getImage("pic2.gif"));
		btn_insert_8.setPreferredSize(new Dimension(20, 20)); //
		btn_insert_8.setToolTipText("ͼƬ����"); //

		btn_adddept.setFocusPainted(false);//ȥ����ť�����ͼƬ����ʾ�Ŀ��
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
		btn_samex.setToolTipText("�������");
		btn_samey.setToolTipText("�������");
		btn_samew.setToolTipText("ͬ����");
		btn_sameh.setToolTipText("ͬ�߶�");
		btn_zoom.setToolTipText("�ָ���С");
		btn_zoomin.setToolTipText("�Ŵ�");
		btn_zoomout.setToolTipText("��С");
		btn_undo.setToolTipText("����");
		btn_redo.setToolTipText("�ָ�");
		btn_delete.setToolTipText("ɾ��");
		btn_help.setToolTipText("����˵��");
		btn_importbackimg.setToolTipText("�ϴ�����ͼƬ");

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
		checkBox_lockgroup = new JCheckBox("�����ײ�", false); //
		checkBox_lockgroup.setOpaque(false); //͸��!��ǰ�о�һ���е�ͻ��
		checkBox_lockgroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onLockGroup(); //
			}
		});
		toolBar.add(checkBox_lockgroup);
		checkBox_showorder = new JCheckBox("��ʾ����", false);
		checkBox_showorder.setOpaque(false);
		checkBox_showorder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onShowOrder();
			}
		});
		toolBar.add(checkBox_showorder);

		JLabel l1 = new JLabel();
		l1.setText("��");
		JButton flow_copy = new JButton("���̵���");
		flow_copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCopyFlow(1);
			}
		});
		if (sr_type.equals("����涨") || sr_type.equals("�����ļ�")) {
			toolBar.add(l1);
			toolBar.add(flow_copy); //
			//toolBar.add(flow_copy2); //
		}

		// btn_start.setEnabled(false);
		// btn_end.setEnabled(false);
		btn_adddept.setEnabled(false); // ���Ӳ���.
		btn_addstation.setEnabled(false); // ���ӽ׶�.

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
	 * �������̹���
	 */
	protected void onCopyFlow(int type) {
		HashVO hvo = new HashVO(); //
		hvo.setAttributeValue("flowid", processId);
		hvo.setAttributeValue("flowcode", processCode);
		hvo.setAttributeValue("flowname", processName);
		hvo.setAttributeValue("type", sr_type);
		Dialog_FlowCopy dialog = new Dialog_FlowCopy(this, "���̵���", 1024, 600, hvo, 1);
		dialog.setVisible(true);
		if (dialog.getCloseType() == 1) {
			loadGraphByCode(processCode); //
		}
	}

	public void setAllBtnEnable() {
		setToolBarVisiable(true);

		btn_save.setEnabled(true); // ����
		btn_new.setVisible(false);
		btn_open.setVisible(false); //

		btn_adddept.setEnabled(true); // ���Ӳ���.
		btn_addstation.setEnabled(true); // ���ӽ׶�.

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
		if (toolBarPanel == null) {//��������Ͳ���ʾ��������ֱ�ӷ���
			return;
		}

		btn_save.setEnabled(false); // ����
		btn_new.setVisible(false);
		btn_open.setVisible(false); //

		btn_adddept.setEnabled(false); // ���Ӳ���.
		btn_addstation.setEnabled(false); // ���ӽ׶�.

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
		// ����Graph����,�ǳ��ؼ�!
		graph = new MyGraph(new MyModel());
		graph.getGraphLayoutCache().setFactory(new GPCellViewFactory()); // //

		graph.setMarqueeHandler(new MyMarqueeHandler());
		graph.getSelectionModel().addGraphSelectionListener(this);
		graph.addKeyListener(this); //
		graph.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == e.BUTTON3) { // �����������Ҽ�
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
					if (e.getEdit() instanceof GraphModelEdit) {//ֻ��ģ�ͱ仯����Ϊ��Ϊundo�Ķ���ͬʱ�׶α����ı仯������undo�ķ�Χ��sunfujun,20120411,����undo��bug����������
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

		//��������� X�� Y�� XY������� �����/2012-11-12��
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

		//��������� �����/2012-11-12��
		staffScrollPanel = new JScrollPane(graph);
		staffScrollPanel.setBorder(BorderFactory.createEmptyBorder());
		staffXViewport = new JViewport();
		staffXViewport.setView(staffXPanel);
		staffYViewport = new JViewport();
		staffYViewport.setView(staffYPanel);

		mainGraphPanel = new JPanel(); // �ұߵ����,��������ʽ�����������ŵİ�ť��!!!
		mainGraphPanel.setLayout(new BorderLayout()); //
		mainGraphPanel.add(staffScrollPanel);
		return mainGraphPanel; //
	}

	//��������� ������� �����/2012-11-12��
	public void addDLine(int x, int y, int w, int h) {
		dlineCell = getCell("XY", Color.BLACK, "cn.com.infostrategy.ui.workflow.design.CellView_DLine");

		GraphConstants.setBounds(dlineCell.getAttributes(), new Rectangle2D.Double(x, y, w, h));
		GraphConstants.setHorizontalAlignment(dlineCell.getAttributes(), SwingConstants.LEFT);
		GraphConstants.setSelectable(dlineCell.getAttributes(), false);
		GraphConstants.setMoveable(dlineCell.getAttributes(), true);
		GraphConstants.setBorder(dlineCell.getAttributes(), BorderFactory.createEmptyBorder());

		graph.getGraphLayoutCache().insert(dlineCell);
	}

	//��������� ɾ������ �����/2012-11-12��
	public void delDLine() {
		graph.getModel().remove(new Object[] { dlineCell });
	}

	//����������Ƿ���ʾ true��ʾ false����ʾ �����/2012-11-12��
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
		panel_west = new JPanel(); // �ұߵ����,��������ʽ�����������ŵİ�ť��!!!
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

		JToolBar toolBar = new JToolBar("abc", JToolBar.VERTICAL); // ������
		toolBar.setPreferredSize(new Dimension(25, 100));
		toolBar.setMargin(new Insets(0, 1, 0, 1)); //
		toolBar.setFloatable(false); //
		toolBar.add(btn_processprop); //

		cardlayout_1 = new CardLayout(); //
		toftPanel_1 = new JPanel(cardlayout_1); //
		toftPanel_1.add(new JLabel("δѡ�������Ϣ"), "blank"); // ���������������
		toftPanel_1.add(getProcessPropPanel(), "process"); // ���������������
		toftPanel_1.setPreferredSize(new Dimension(175, 800)); // ����Ĭ�ϴ�С!!!
		toftPanel_1.setVisible(false); // �������������!!!

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
		toftPanel_2.add(new JLabel("δѡ�������Ϣ"), "blank"); // ���������������
		toftPanel_2.add(getActivityPropPanel(), "activity"); // ���뻷���������
		toftPanel_2.add(getTransitionPropPanel(), "transition"); // ���������������
		toftPanel_2.setPreferredSize(new Dimension(203, 100)); // ����Ĭ�ϴ�С!!!
		toftPanel_2.setVisible(false); // �������������!!!
		return toftPanel_2; //
	}

	/**
	 * ���ڼ����߹�����
	 * 
	 * @return
	 */
	private JToolBar getEastToolBox() {
		if (toolBar2 != null) {
			return toolBar2;
		}
		btn_toolbox1 = new WLTButton(UIUtil.getImage("toolbox1.gif")); // ������
		btn_toolbox1.setBackground(Color.lightGray); //
		btn_toolbox1.setName("1");
		btn_toolbox1.setBorder(BorderFactory.createLineBorder(Color.black, 1)); //
		btn_toolbox1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onClicked_1(); //
			}
		});

		toolBar2 = new JToolBar("abc", JToolBar.VERTICAL); // ������
		toolBar2.setPreferredSize(new Dimension(25, 500));
		toolBar2.setMargin(new Insets(5, 1, 5, 1)); //
		toolBar2.setFloatable(false); //

		toolBar2.add(btn_toolbox1); // ������

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
	 * �����������..
	 * 
	 * @return
	 */
	private BillPropPanel getProcessPropPanel() {
		if (processPropPanel == null) {
			processPropPanel = new BillPropPanel("pub_wf_process_CODE1"); // �����������
			processPropPanel.addBillPropEditListener(this);
		}
		return processPropPanel;
	}

	/**
	 * �����������..
	 * 
	 * @return //
	 */
	private BillPropPanel getActivityPropPanel() {
		if (activityBillPropPanel == null) {
			activityBillPropPanel = new BillPropPanel("pub_wf_activity_CODE1"); // �����������,"pub_wf_activity_CODE1");  //new ServerTMODefine("/cn/com/infostrategy/bs/sysapp/install/templetdata/pub_wf_activity_CODE1.xml")
			activityBillPropPanel.addBillPropEditListener(this);
		}
		return activityBillPropPanel;
	}

	/**
	 * �����������..
	 * 
	 * @return
	 */
	private BillPropPanel getTransitionPropPanel() {
		if (transitionBillPropPanel == null) {
			transitionBillPropPanel = new BillPropPanel("pub_wf_transition_CODE1"); // �����������!!!
			transitionBillPropPanel.addBillPropEditListener(this); //
		}
		return transitionBillPropPanel;
	}

	/**
	 * ����������������..
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
	 * ����һ������
	 */
	private void onCreateWfProcess() {
		onCreateWfProcess(true, null, null);
	}

	/**
	 * ����һ������
	 */
	public void onCreateWfProcess(boolean _showDialog, String _code, String _name) {
		String code = _code;
		String name = _name;
		if (_showDialog) {
			CreateWorkFlowDialog dialog = new CreateWorkFlowDialog(this);
			dialog.setVisible(true); //
			System.out.println("�ر�����:" + dialog.getCloseType());
			if (dialog.getCloseType() == 1) {
				code = dialog.getProcessCode();
				name = dialog.getProcessName();
			} else {
				return;
			}
		}

		clearEmptyGraph(); // �����Ļ!!�ؼ�!!
		String str_newID = null;
		try {
			str_newID = UIUtil.getSequenceNextValByDS(null, "s_pub_wf_process");
		} catch (Exception e) {
			e.printStackTrace();
		} //
		currentProcessVO = new ProcessVO(); //
		currentProcessVO.setId(str_newID); //
		currentProcessVO.setCode(code); // ���̱���
		currentProcessVO.setName(name); // ��������!!
		currentProcessVO.setWftype("������");//һ��ֻ��������������������̵İ�ť�����Ҹ�����ͼ������������ļ���һ����Ϊ��ҵ����Ҫ�ܵĹ����������/2012-05-24��

		//������������ ���� �����/2013-03-27��
		ProcessBean bean = (ProcessBean) getProcessPropPanel().getBeanInstance(); //
		copyProcessDataFromVoToBean(currentProcessVO, bean); //
		getProcessPropPanel().reload(); // ���½�Bean�е����ݼ��ص��ؼ���ȥ��ʾ!!

		//�ȼ������,�еĿͻ����ܲ���Ҫ�������,�Ժ���Կ�������һ��ϵͳ����!!
		String str_title = "��������:" + this.getCurrentProcessVO().getName(); //"���̱���:" + this.getCurrentProcessVO().getCode() + ",
		if (ClientEnvironment.isAdmin()) {
			str_title = "��������:" + this.getCurrentProcessVO().getCode() + "/" + this.getCurrentProcessVO().getName(); //��Ϊ�д����������ظ�,������admin=Yʱ,��ʾ����!!!
		}
		titleCell = createVertex(str_title, 5, 5, 500, 20, Color.WHITE, Color.BLUE, new Font("System", Font.PLAIN, 12), true, null);
		titleCell.removeAllChildren(); //
		GraphConstants.setHorizontalAlignment(titleCell.getAttributes(), SwingConstants.LEFT); //
		GraphConstants.setSelectable(titleCell.getAttributes(), false); //
		GraphConstants.setMoveable(titleCell.getAttributes(), false);
		GraphConstants.setBorder(titleCell.getAttributes(), BorderFactory.createEmptyBorder()); //
		GraphConstants.setFont(titleCell.getAttributes(), new Font("����", Font.PLAIN, 12)); //
		graph.getGraphLayoutCache().insert(titleCell); // �������

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

		onInsertStartCell(); // ������ʼ���!
		onInsertEndCell(); // ����������!!
		graph.clearSelection(); //

		groupcount = 0;
		stationcount = 0; //
	}

	/**
	 * �����Ļ�����ж���,�Ա��µĲ���!!
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
		graph.getGraphLayoutCache().reload(); // һ��Ҫִ��,�������ֶ���Ŀ�!!
		graph.setUI(new MyGraphUI()); // ��������UI
		setGridVisible(true);//���ñ������ӿɼ�
		graph.invalidate(); //

		this.stationBackGroundCell = null; //��Ϊ��!!!
		switchToBlankPropPanel(); // ���Կ�Ҳ�ÿ�!!
	}

	public String getWFProcessCondition() {
		return null; //
	}

	/**
	 * ѡ��һ��������!!
	 */
	private void onOpenWfProcess() {
		String str_condition = getWFProcessCondition(); //
		OpenProcessDialog dialog = new OpenProcessDialog(this, str_condition);
		dialog.getBillListPanel().setDataFilterCustCondition(str_condition); //
		dialog.loadData(); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			String str_processID = dialog.getProcessID();//��ǰ�õı��룬�����ܱ�֤������Ψһ�ģ�����Ҫ��Ϊ����id�����/2012-05-22��
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

	//��һ������ͼ...
	public void openMainGraph(ProcessVO processVO, int _type) {
		this.isHaveLoaded = false;
		this.currentProcessVO = processVO; //
		clearEmptyGraph(); // �����Ļ!!!�ؼ�

		boolean silockwheninit = new TBUtil().getSysOptionBooleanValue("����ͼ����ʱ�Ƿ�Ĭ�������ײ�", false);
		if (checkBox_lockgroup != null) {
			if (silockwheninit) {
				checkBox_lockgroup.setSelected(true); //
			} else {
				checkBox_lockgroup.setSelected(false); //	
			}
		}
		checkBox_showorder.setSelected(false);
		GroupVO[] groupVOs = processVO.getGroupVOs(); //
		TransitionVO[] tansitionVOs = processVO.getTransitionVOs(); // �õ���������
		ActivityVO[] activityVOs = processVO.getActivityVOs(); //
		Hashtable cellMap = new Hashtable();

		//�ȼ������,�еĿͻ����ܲ���Ҫ�������,�Ժ���Կ�������һ��ϵͳ����!!
		String str_title = "��������:" + this.getCurrentProcessVO().getName(); //"���̱���:" + this.getCurrentProcessVO().getCode() + ",
		if (ClientEnvironment.isAdmin()) {
			str_title = "��������:" + this.getCurrentProcessVO().getCode() + "/" + this.getCurrentProcessVO().getName(); //��Ϊ�д����������ظ�,������admin=Yʱ,��ʾ����!!!
		}
		titleCell = createVertex(str_title, 5, 5, 500, 20, Color.WHITE, Color.BLUE, new Font("System", Font.PLAIN, 12), true, null);
		titleCell.removeAllChildren(); //
		GraphConstants.setHorizontalAlignment(titleCell.getAttributes(), SwingConstants.LEFT); //
		GraphConstants.setSelectable(titleCell.getAttributes(), false); //
		GraphConstants.setMoveable(titleCell.getAttributes(), false);
		GraphConstants.setBorder(titleCell.getAttributes(), BorderFactory.createEmptyBorder()); //
		GraphConstants.setFont(titleCell.getAttributes(), new Font("����", Font.PLAIN, 12)); //
		graph.getGraphLayoutCache().insert(titleCell); // �������

		// ���������..
		boolean isHaveStation = false; //
		for (int i = 0; i < groupVOs.length; i++) {
			if (groupVOs[i].getGrouptype().equals("DEPT")) { //��ҫ�������ڲ����ֳ�����˳������,ԭ����ǰ�����ж�����Xλ�õĴ���,��DB2�в�ѯʱû��order ����,���������˳�򲢲��ǲ�����Ⱥ�˳��,Ȼ����δ����߼��ͻ��������!!
				addDeptGroup(groupVOs[i]); //
			} else if (groupVOs[i].getGrouptype().equals("STATION")) {
				addStationGroup(groupVOs[i]); //
				isHaveStation = true; //
			}
		}

		//����׶���ı���!
		if (isHaveStation) { //����н׶���,�ż����˽׶εı����ľ���!
			addStationBackGroundActivity(); //
		}
		cellNumber = activityVOs.length;
		// ����������ͨ���
		for (int i = 0; i < activityVOs.length; i++) {
			DefaultGraphCell cell = null;
			if (activityVOs[i].getActivitytype().equalsIgnoreCase("START")) { // ��ʼ���
				String fonttype = "����";
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
				graph.getGraphLayoutCache().insert(cell); // �������
			} else if (activityVOs[i].getActivitytype().equalsIgnoreCase("END")) { // �������
				String fonttype = "����";
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
				graph.getGraphLayoutCache().insert(cell); // �������
			} else { // ��ͨ���
				String fonttype = "����";
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
				graph.getGraphLayoutCache().insert(cell); // �������
			}
			graph.getGraphLayoutCache().toFront(new DefaultGraphCell[] { cell }); // ����ǰ��
			cellMap.put(activityVOs[i].getId() + "", cell); //
		}

		// ������������
		for (int i = 0; i < tansitionVOs.length; i++) {
			DefaultEdge edge = new DefaultEdge(tansitionVOs[i]); //
			// this.setEdgeAttributes(edge.getAttributes()); //

			Integer fromId = tansitionVOs[i].getFromactivity(); // ��...
			Integer toId = tansitionVOs[i].getToactivity(); // ȥ...

			DefaultGraphCell fromcell = (DefaultGraphCell) cellMap.get(fromId + "");
			DefaultGraphCell tocell = (DefaultGraphCell) cellMap.get(toId + "");

			if (fromcell != null && tocell != null) {
				edge.setSource(fromcell.getChildAt(0)); // ��ʼλ��
				edge.setTarget(tocell.getChildAt(0)); // ����λ��
			}

			GraphConstants.setLabelAlongEdge(edge.getAttributes(), true); //���ΪTrue������б�ŵ�������
			GraphConstants.setEndFill(edge.getAttributes(), true);//���߼�ͷ���
			//GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_TECHNICAL);//���߼�ͷΪ����
			GraphConstants.setLineEnd(edge.getAttributes(), tansitionVOs[i].getLineType());//��sunfujun/20120426/���Ӽ�ͷ���͵�����_���֡�
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

			// ����������������ɫ
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

			rePaintLineStyle(edge); // ���������ߵ���ɫ!!

			// ��������·��
			List pointLs = tansitionVOs[i].getPoints(); // �Ѿ�������ʼ,�������
			GraphConstants.setPoints(edge.getAttributes(), this.convertToPoint2DList(pointLs));

			// edgeRender(edge);

			graph.getGraphLayoutCache().insert(edge); // �������
			graph.getGraphLayoutCache().toFront(new DefaultGraphCell[] { edge }); // ����ǰ��
		}

		graph.clearSelection(); //

		if (_type == 1) {
			ProcessBean bean = (ProcessBean) getProcessPropPanel().getBeanInstance(); //
			copyProcessDataFromVoToBean(processVO, bean); //
			getProcessPropPanel().reload(); // ���½�Bean�е����ݼ��ص��ؼ���ȥ��ʾ!!
		}

		// ���а�ť��Ч
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
		graph.getGraphLayoutCache().reload(); //�ؼ�!��һ��Ҫ������������д���֮ǰ!
		//reSortStation(null);//��Ҫ���ò��Ÿ߶�
		reSortDept(null);
		reSetAllLayer(true);
		isEditChanged = false; //��Ϊ�մ�,���Բ�����Ϊ���޸Ĺ���!
		this.isHaveLoaded = true;
	}

	/**
	 * ������ͼ�ĵط�
	 * 
	 * @param _worktickno
	 * @param _processid
	 * @param _tabpos
	 * @param _type
	 *            1-client,2-server
	 * @return
	 */
	public void openHistoryMainGraph(ProcessVO processVO, int _type) {
		openMainGraph(processVO, _type); //��ǰ��֪����˭��Ȼ������������������һ��,����ΪʲôҪ��ô��!!!
	}

	/**
	 * ����ɫת�����ַ���,�Զ��Ž�R,G,B���
	 * 
	 * @param _color
	 * @return
	 */
	private String convertColorToStr(Color _color) {
		return _color.getRed() + "," + _color.getGreen() + "," + _color.getBlue(); //
	}

	/**
	 * ���ַ���ת������ɫ
	 * 
	 * @param _str
	 * @return
	 */
	private Color convertStrToColor(String _str) {
		String[] items = _str.split(","); //
		return new Color(Integer.parseInt(items[0]), Integer.parseInt(items[1]), Integer.parseInt(items[2]));
	}

	/**
	 * ��List�е�double[]Ԫ��ת��Ϊ��Ӧ��Point2D.Double����
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
	 * ����һ��������!!
	 */
	public void onSaveWfProcess() {
		onSaveWfProcess(true);
	}

	/**
	 * ����һ��������
	 * @param _isShowMessage  �Ƿ���ʾ��ʾ��Ϣ
	 */
	public void onSaveWfProcess(boolean _isShowMessage) {
		try {
			this.setCursor(new Cursor(Cursor.WAIT_CURSOR)); //
			long ll_1 = System.currentTimeMillis(); //
			if (currentProcessVO == null) {
				MessageBox.show(this, "��ǰû������,�޷�����!"); //
				return;
			}
			//������������ﲻҪ��ʾ������֪���Ƿ���Ҫ���桾���/2012-05-09��
			//			if (!isEditChanged) {
			//				if (JOptionPane.showConfirmDialog(this, "����û�з����仯,�������±���֮��?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			//					return;
			//				}
			//			}
			if (showtoolbox) {//�����ʾ���������ſ���չ�����ں��������ԣ����б�Ҫֹͣ�༭
				getActivityPropPanel().stopEditing(); // �������ڿ�༭..
				getTransitionPropPanel().stopEditing(); // �������߿�༭..
			}

			GraphModel model = graph.getModel();
			int rootCount = model.getRootCount(); // ���н�����Ŀ
			ArrayList activityVOList = new ArrayList(); //
			ArrayList transitionVOList = new ArrayList(); //
			ArrayList groupVOList = new ArrayList(); //

			// ���滷��
			HashMap hs_code = new HashMap(); //
			for (int i = 0; i < rootCount; i++) {
				Object obj = model.getRootAt(i);
				if (obj instanceof DefaultGraphCell) {
					DefaultGraphCell cell = (DefaultGraphCell) obj; //
					Object linkedObject = cell.getUserObject();
					if (linkedObject instanceof ActivityVO) {
						Rectangle2D point2D = GraphConstants.getBounds(cell.getAttributes()); // ȡ������λ��
						int x = new Double(point2D.getX()).intValue();
						int y = new Double(point2D.getY()).intValue();
						int width = new Double(point2D.getWidth()).intValue(); // ���
						int height = new Double(point2D.getHeight()).intValue(); // �߶�

						ActivityVO activityVO = (ActivityVO) linkedObject; // ����ת��
						boolean isCanSaveWhenCodeSame = new TBUtil().getSysOptionBooleanValue("���ڱ������ʱ�Ƿ�������", true); //ũ����û��ƽ̨���ñ�����ֻ�ܱ༭����ͼ���Ի������Բ����޸ģ�������ʱ��Ϊ������
						if (hs_code.containsKey(activityVO.getCode()) && !isCanSaveWhenCodeSame) {
							String str_msg = "���ڡ�" + activityVO.getWfname() + "���뻷�ڡ�" + hs_code.get(activityVO.getCode()) + "���ı��붼�ǡ�" + activityVO.getCode() + "��,�����������ͬ�Ļ��ڱ���,�˳��������!";
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

			// ��������..
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

			// ���沿����..
			DefaultGraphCell[] deptCells = getDeptCells(); //
			for (int i = 0; i < deptCells.length; i++) {
				DefaultGraphCell cell = deptCells[i];
				Object linkedObject = cell.getUserObject(); // ȡ���û�����
				if (linkedObject instanceof GroupVO) { // �������
					Rectangle2D itempoint = graph.getCellBounds(cell); // ȡ�ø����λ�����С
					GroupVO groupVO = (GroupVO) linkedObject; //
					groupVO.setX(new Integer((int) itempoint.getX())); //
					groupVO.setY(new Integer((int) itempoint.getY())); //
					groupVO.setWidth(new Integer((int) itempoint.getWidth())); //
					groupVO.setHeight(new Integer((int) itempoint.getHeight())); //
					groupVOList.add(groupVO); //
				}
			}

			// ����׶���..
			DefaultGraphCell[] stationCells = getStationCells(); //
			for (int i = 0; i < stationCells.length; i++) {
				DefaultGraphCell cell = stationCells[i]; //
				Object linkedObject = cell.getUserObject(); // ȡ���û�����
				if (linkedObject instanceof GroupVO) { // �������
					Rectangle2D itempoint = graph.getCellBounds(cell); // ȡ�ø����λ�����С
					GroupVO groupVO = (GroupVO) linkedObject; //
					groupVO.setX(new Integer((int) itempoint.getX())); //
					groupVO.setY(new Integer((int) itempoint.getY())); //
					groupVO.setWidth(new Integer((int) itempoint.getWidth())); //
					groupVO.setHeight(new Integer((int) itempoint.getHeight())); //
					groupVOList.add(groupVO); //
				}
			}

			// Լ����飬���粻���ж��ߵ�
			ActivityVO[] activityVOs = (ActivityVO[]) activityVOList.toArray(new ActivityVO[0]); //
			TransitionVO[] transitionVOs = (TransitionVO[]) transitionVOList.toArray(new TransitionVO[0]); //
			GroupVO[] groupVOs = (GroupVO[]) groupVOList.toArray(new GroupVO[0]); //
			setActivityBelongGroup(activityVOs, groupVOs); // ��Activity�ϰ�BelongGroup

			currentProcessVO.setActivityVOs(activityVOs); //
			currentProcessVO.setTransitionVOs(transitionVOs); //
			currentProcessVO.setGroupVOs(groupVOs); //

			WorkFlowServiceIfc wfservice = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class); // Remote
			wfservice.saveWFProcess(currentProcessVO, "" + currentProcessVO.getId()); //
			isEditChanged = false; //������ʾ���仯��
			addActivityIds.clear();//�����������id
			if (_isShowMessage) {
				long ll_2 = System.currentTimeMillis(); //
				MessageBox.show(this, "����ɹ�,����ʱ[" + (ll_2 - ll_1) + "]����!"); //
			}
		} catch (Exception e) {
			e.printStackTrace(); //
			MessageBox.showException(this, e); //
		} finally {
			this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
		}
	}

	/**
	 * Ϊ�������ö�Ӧ�Ĳ��ž�����׶ξ�����
	 * 
	 * @param activityVOs
	 * @param groupVOs
	 */
	private void setActivityBelongGroup(ActivityVO[] activityVOs, GroupVO[] groupVOs) {
		if (groupVOs == null || groupVOs.length == 0) {
			return;
		}

		for (int i = 0; i < activityVOs.length; i++) { // ������������
			for (int j = 0; j < groupVOs.length; j++) { // ����������
				if (groupVOs[j].getGrouptype().equals("DEPT")) { // ����ǻ�����,�����ŵ�
					if ((activityVOs[i].getX() + (activityVOs[i].getWidth() / 2) >= groupVOs[j].getX()) //X����,���ڵ���߱����ڸ������ߵ��ҷ�
							&& (activityVOs[i].getX() + (activityVOs[i].getWidth() / 2) < groupVOs[j].getX() + groupVOs[j].getWidth()) //X����,���ڵ���߱���������ұߵ����
							&& (activityVOs[i].getY() < 30 + groupVOs[j].getHeight()) //
					) { // ���ڵ�X��������������.
						activityVOs[i].setBelongdeptgroup(groupVOs[j].getWfname()); // ֱ���������е�������
					}
				}
			}

			for (int j = 0; j < groupVOs.length; j++) { // ����������
				if (groupVOs[j].getGrouptype().equals("STATION")) { // ����Ǻ��ŵ�
					if (activityVOs[i].getY() + (activityVOs[i].getHeight() / 2) < groupVOs[j].getY()) {
						activityVOs[i].setBelongstationgroup(groupVOs[j].getWfname()); // ֻҪ�ҵ�һ�������������
						break;
					}
				}
			}
		}
	}

	/**
	 * ������ʾһ�����
	 */
	public void lightCell(String[][] str_id) {
		unlightAllCell();
		unlightAllLine(); //

		for (int i = 0; i < str_id.length; i++) {
			DefaultGraphCell cell = findCellById(str_id[i][0]);
			if (cell != null) {
				if (str_id[i][1].equals("Y")) {
					GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createLineBorder(Color.RED, 3)); //��ǰ
				} else if (str_id[i][1].equals("From")) { //���Ļ���
					GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createLineBorder(Color.MAGENTA, 3)); //��Դ��
				} else if (str_id[i][1].equals("To")) { //ȥ�Ļ���
					GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createLineBorder(Color.BLUE, 3)); //ȥ�����̵�
				} else {
					GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createLineBorder(Color.MAGENTA, 3)); //
				}
			}

			//			if (i > 0) {
			//				String str_fromId = str_id[i - 1][0]; //��Դ����
			//				String str_toId = str_id[i][0]; //Ŀ�껷��
			//				DefaultEdge edge = findLineById(str_fromId, str_toId); //�ҵ���,Ȼ�������ʾ!
			//				if (edge != null) {
			//					GraphConstants.setLineColor(edge.getAttributes(), Color.MAGENTA);
			//					GraphConstants.setLineWidth(edge.getAttributes(), 1); //
			//				}
			//			}
		}

		graph.getGraphLayoutCache().reload(); //
		// graph.updateUI();
		// graph.setUI(new MyGraphUI()); // ��������UI
		// graph.invalidate(); //
		graph.repaint(); //
	}

	public void lightLine(String[][] _cellIds) {
		for (int i = 0; i < _cellIds.length; i++) {
			String str_fromId = _cellIds[i][0];
			String str_toId = _cellIds[i][1];
			DefaultEdge edge = findLineById(str_fromId, str_toId); //�ҵ���,Ȼ�������ʾ!
			if (edge != null) {
				GraphConstants.setLineColor(edge.getAttributes(), Color.MAGENTA);
				GraphConstants.setLineWidth(edge.getAttributes(), 1); //
			}
		}
		graph.getGraphLayoutCache().reload(); //
		graph.repaint(); //
	}

	/**
	 * ������ʾһ�����
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
		int rootCount = model.getRootCount(); // ���н�����Ŀ
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
	 * �ҵ����е���
	 */
	public void unlightAllLine() {
		GraphModel model = graph.getModel();
		int rootCount = model.getRootCount(); // ���н�����Ŀ
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
		int rootCount = model.getRootCount(); // ���н�����Ŀ
		for (int i = 0; i < rootCount; i++) {
			Object obj = model.getRootAt(i);
			if (obj instanceof DefaultGraphCell) {
				DefaultGraphCell cell = (DefaultGraphCell) obj; //
				Object linkedObject = cell.getUserObject();
				if (linkedObject instanceof ActivityVO) {
					ActivityVO activityVO = (ActivityVO) linkedObject; // ����ת��
					if (("" + activityVO.getId()).equals(_id)) {
						return cell;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Ϊĳ����������ȵ�
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
		int rootCount = model.getRootCount(); // ���н�����Ŀ
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
	 * ���±��������Ķ�����Ϣ
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
	 * �õ����ӱߵ���ʼ���
	 */
	private DefaultGraphCell getEdgeSource(DefaultEdge edge) {
		DefaultGraphCell cell = (DefaultGraphCell) graph.getModel().getParent(edge.getSource());
		return cell;
	}

	/**
	 * �õ����ӱߵ���ֹ���
	 */
	private DefaultGraphCell getEdgeTarget(DefaultEdge edge) {
		DefaultGraphCell cell = (DefaultGraphCell) graph.getModel().getParent(edge.getTarget());
		return cell;
	}

	/**
	 * ��List�е�Point2D.DoubleԪ��ת��Ϊ��Ӧ��double[]����
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
	 * ȡ��һ�������ߵ�����
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
		activityVO.setId(activityid);//��ǰ���������»��һ��id��activityVO.setId(getNewActivityID())��bug!!!�����/2012-05-22��
		addActivityIds.add(activityid + "");
		activityVO.setCode("START"); //
		activityVO.setWfname("��ʼ"); //
		activityVO.setUiname("��ʼ"); //
		activityVO.setX(new Integer(x));
		activityVO.setY(new Integer(y));
		activityVO.setApprovemodel("1"); // 1��ʾ��ռ
		activityVO.setAutocommit("N"); // ���Զ��ύ
		activityVO.setShowparticimode("1"); //
		activityVO.setDesc("");
		activityVO.setViewtype(1); //
		activityVO.setFonttype("System");//��Ҫ����һ�£�����������ͼʱ�ᱨ�����/2014-10-21��
		activityVO.setFontsize(12);
		activityVO.setForeground("0,0,0");
		activityVO.setBackground("225,255,225");

		DefaultGraphCell cell = createVertex(activityVO, x, y, 40, 40, this.startCellBgcolor, Color.BLACK, new Font("System", Font.PLAIN, 12), true, null);
		GraphConstants.setIcon(cell.getAttributes(), UIUtil.getImage("workflow/start.gif"));
		getGraph().getGraphLayoutCache().insert(cell); // �������
		getGraph().getGraphLayoutCache().toFront(new DefaultGraphCell[] { cell }); //
		isEditChanged = true; //��ʾ�Ѿ��仯��
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
		activityVO.setWfname("����"); //
		activityVO.setUiname("����"); //
		activityVO.setX(new Integer(x));
		activityVO.setY(new Integer(y));
		activityVO.setApprovemodel("1"); // 1��ʾ��ռ
		activityVO.setAutocommit("N"); // ���Զ��ύ
		activityVO.setShowparticimode("1"); //
		activityVO.setDesc("");
		activityVO.setViewtype(1); //
		activityVO.setFonttype("System");//��Ҫ����һ�£�����������ͼʱ�ᱨ�����/2014-10-21��
		activityVO.setFontsize(12);
		activityVO.setForeground("0,0,0");
		activityVO.setBackground("225,255,225");

		DefaultGraphCell cell = createVertex(activityVO, x, y, 40, 40, this.endCellBgcolor, Color.BLACK, new Font("System", Font.PLAIN, 12), true, null);
		GraphConstants.setIcon(cell.getAttributes(), UIUtil.getImage("workflow/end.gif"));

		getGraph().getGraphLayoutCache().insert(cell); // �������
		getGraph().getGraphLayoutCache().toFront(new DefaultGraphCell[] { cell }); //
		isEditChanged = true; //��ʾ�Ѿ��仯��
	}

	/**
	 * ���¶���������ŵ�λ�����С..
	 * �����϶����ƶ�ʱ���������!!!
	 * @param _groupCell
	 */
	private void resetMyPosAndSize(DefaultGraphCell _groupCell) {
		Rectangle2D itempoint_sel = graph.getCellBounds(_groupCell); // ȡ��ѡ�н���λ�����С
		Object userobj = _groupCell.getUserObject();
		if (userobj instanceof ActivityVO) { //����ƶ�������ͨ����
			reSetAllLayer(false); //
		} else if (userobj instanceof GroupVO) {
			GroupVO gvo = (GroupVO) _groupCell.getUserObject();
			String type = gvo.getGrouptype();
			if (type.equals("DEPT")) {
				reSortDept(itempoint_sel); // ���Ų�����..
			} else if (type.equals("STATION")) {
				reSortStation(itempoint_sel); // ���Ž׶���..
			}
			reSetAllLayer(true); //
		}
		isEditChanged = true; //��ʾ�Ѿ��仯��
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
			graph.getGraphLayoutCache().toBack(new Object[] { titleCell });//��Ҫ����һ�£������ص��Ļ���˳��ʹ��ˡ����/2012-05-22��
		}
	}

	/**
	 * ���Ÿ�������
	 * 
	 * @param _point_sel
	 */
	private void reSortDept(Rectangle2D _point_sel) {//���ڽ׶ε������ǻ��ڽ׶�ñ���ϣ����Ըı�߶ȵ�ʱ���趨����Խ��������Ľ׶Ρ�sunfujun/20120413��
		DefaultGraphCell[] deptCells = getDeptCells(); //
		double ld_x = 70; //
		double li_firstheight = 0; //
		double laststation_y = 0; //
		DefaultGraphCell[] stationCells = getStationCells();
		if (stationCells != null && stationCells.length > 0) {
			laststation_y = getMaxStationPos() + 10;
		}
		for (int k = 0; k < deptCells.length; k++) { // ��������ļ���
			DefaultGraphCell itemcell = deptCells[k]; //
			Rectangle2D itempoint = graph.getCellBounds(itemcell); // ȡ�øò��ŵ�λ��
			double ld_width = itempoint.getWidth(); // ���
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
		if (deptCells == null || deptCells.length == 0) {//������Ŷ�û���˽��׶�Ҳ��ɾ����
			graph.getModel().remove(this.getStationCells());
			graph.getGraphLayoutCache().toBack(new Object[] { titleCell });//��Ҫ����һ�£������ص��Ļ���˳��ʹ��ˡ����/2012-05-22��
			graph.getGraphLayoutCache().reload();
		} else {
			graph.getGraphLayoutCache().reload();//������reloadһ��
			moveStationToMaxDept(); // �ƶ���������ŵ��ұ�..
		}
		isEditChanged = true; //��ʾ�Ѿ��仯��
	}

	/**
	 * �ƶ�һ������������λ��
	 * 
	 * @param _x
	 * @param _y
	 * @param _width
	 * @param _height
	 */
	private void moveGrouptoNewPoint(DefaultGraphCell _group, double _x, double _y, double _width, double _height) {
		DefaultGraphCell cell = (DefaultGraphCell) _group;
		GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(_x, _y, _width, _height)); //
		isEditChanged = true; //��ʾ�Ѿ��仯��
	}

	/**
	 * �ƶ������ߵ�λ�������ұ߲��ŵĴ�С
	 */
	private void moveStationToMaxDept() {
		double ld_max_x_width = getMaxDeptRightPos(); // ȡ�����в������ұߵ�λ��(x+width)
		double li_x = 10;
		if (ld_max_x_width > 0) {
			// �����еĽ׶��ߵĿ�ȸĳ������ұ߲��ŵ��ұ��߶���!!!
			DefaultGraphCell[] stationCells = getStationCells(); //
			for (int i = 0; i < stationCells.length; i++) {
				DefaultGraphCell itemcell = stationCells[i]; // ȡ�����е�Cell
				Rectangle2D itempoint = graph.getCellBounds(itemcell); // ȡ�øò��ŵ�λ��
				GraphConstants.setBounds(itemcell.getAttributes(), new Rectangle2D.Double(li_x, itempoint.getY() < 90 ? 90 : itempoint.getY(), ld_max_x_width - li_x, itempoint.getHeight())); //�����׶θ߶ȣ�Ĭ���ڲ��ű�ͷ���桾���/2012-04-17��
			}
			graph.getGraphLayoutCache().reload(); //
			isEditChanged = true; //��ʾ�Ѿ��仯��
		}
	}

	/**
	 * �����в��Ŷ��ĳ�һ����...
	 */
	private void resizeAllDeptSample(double _height) {
		DefaultGraphCell[] deptCells = getDeptCells(); //
		for (int i = 0; i < deptCells.length; i++) {
			DefaultGraphCell itemcell = deptCells[i]; //
			Rectangle2D itempoint = graph.getCellBounds(itemcell); // ȡ�øò��ŵ�λ��
			double ld_x = itempoint.getX(); //
			double ld_y = itempoint.getY(); //
			double ld_width = itempoint.getWidth(); // ���

			DefaultGraphCell obj_bottem = (DefaultGraphCell) itemcell;
			GraphConstants.setBounds(obj_bottem.getAttributes(), new Rectangle2D.Double(ld_x, ld_y, ld_width, _height)); //
		}
		graph.getGraphLayoutCache().reload(); //
		isEditChanged = true; //��ʾ�Ѿ��仯��
	}

	/**
	 * ��������
	 * 
	 * @param _itempoint_sel
	 * 
	 */
	private void reSortStation(Rectangle2D _itempoint_sel) {
		if (getMaxDeptHeight() < getMaxStationPos() + 10) {
			resizeAllDeptSample(getMaxStationPos() + 10); //�����߶ȡ����/2012-04-17��
		}
		moveStationToMaxDept(); // �ƶ����е��ߵ����ŵ��ұ�
	}

	/**
	 * ȡ�����в�������ߵĲ��ŵ��ұ�������
	 * 
	 * @return
	 */
	private double getMaxDeptRightPos() {
		double ld_max_x = 0; //
		double ld_max_x_width = 0; //
		DefaultGraphCell[] deptCells = getDeptCells(); //
		if (deptCells != null && deptCells.length > 0) {
			Rectangle2D itempoint = graph.getCellBounds(deptCells[deptCells.length - 1]); // ȡ�øò��ŵ�λ��
			ld_max_x = itempoint.getX();
			ld_max_x_width = ld_max_x + itempoint.getWidth();
		}
		return ld_max_x_width;
	}

	/**
	 * ȡ�����Ľ׶ε�λ��,��������Ľ׶ε�λ��..
	 * 
	 * @return
	 */
	private double getMaxStationPos() {
		double ld_y = 0; //
		DefaultGraphCell[] stationCells = getStationCells(); //
		if (stationCells != null && stationCells.length > 0) {
			Rectangle2D itempoint_right = graph.getCellBounds(stationCells[stationCells.length - 1]); // ȡ�øò��ŵ�λ��
			ld_y = itempoint_right.getY();
		}

		DefaultGraphCell[] activityCells = getActivityCells(); //
		if (activityCells != null && activityCells.length > 0) {
			Rectangle2D itempoint_right = graph.getCellBounds(activityCells[activityCells.length - 1]); // ȡ�øò��ŵ�λ��
			double ld_tmp_y = itempoint_right.getY();
			if (ld_tmp_y > ld_y) {
				ld_y = ld_tmp_y;
			}
		}

		return ld_y;
	}

	//ȡ�ò��ŵ������!
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
		return li_maxHeight + 4; //��֪��Ϊʲôһ��Ҫ��4������,һ������Ϊ����߽��ԭ����ɵ�! ��������ô��,�����Ͷ�����!!
	}

	/**
	 * ȡ�����н׶ε���
	 * 
	 * @return
	 */
	public DefaultGraphCell[] getActivityCells() {
		GraphModel model = graph.getModel();
		int rootCount = model.getRootCount(); // ���н�����Ŀ
		ArrayList<DefaultGraphCell> list = new ArrayList<DefaultGraphCell>();
		for (int i = 0; i < rootCount; i++) {
			Object obj = model.getRootAt(i);
			if (obj instanceof DefaultGraphCell) {
				DefaultGraphCell cell = (DefaultGraphCell) obj; //
				Object linkedObject = cell.getUserObject();
				if (linkedObject instanceof ActivityVO) {
					Rectangle2D itempoint = graph.getCellBounds(cell); // ȡ�ø����λ��
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
	 * ȡ�����л���VO
	 * @return
	 */
	public ActivityVO[] getActivityVOs() {
		GraphModel model = graph.getModel();
		int rootCount = model.getRootCount(); // ���н�����Ŀ
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
			Rectangle2D itempoint = graph.getCellBounds(deptCells[deptCells.length - 1]); // ȡ�ø����λ��
			li_x_height[0] = (int) (itempoint.getX() + itempoint.getWidth() - 1); //ȡ�����һ�����λ��
			li_x_height[1] = (int) itempoint.getHeight(); //
		}
		return li_x_height; //
	}

	/**
	 * �ڵײ����Ӳ���
	 */
	private void onAddDept() {
		String str_deptname = "";
		String str_option = new TBUtil().getSysOptionStringValue("�Զ�������ͼ���Ӳ��Ű�ť�ĵ���¼�", null);
		if (str_option == null || str_option.equals("")) {
			//��Ϊũ��һͼ������û��ģ�������ӱ�������ע���ֵ䡢ƽ̨�������ñ�ȣ������ڱ����������˸����ԣ������������ж��Ƿ���ʾԤ�貿�š�
			ChoosePhaseDialog dialog = new ChoosePhaseDialog(this, "ѡ��" + groupName, groupName + "����", "��������ͼ" + groupName, this.showpreviewdept); //
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
		reSortDept(null); //���Ż���
		reSetAllLayer(false); //���û����ͽ׶��ڵײ�
	}

	/**
	 *  ������������һ�����ž���!!
	 * @param _deptName ��������!!
	 * @param _x  X����λ��!
	 * @param _width ���
	 * @param _height �߶�
	 */
	private GroupVO dealAddDept(String _deptName, String _posts, int _x, int _width, int _height) {
		GroupVO groupVO = new GroupVO(); //
		groupVO.setGrouptype("DEPT"); // �����ǲ���
		groupVO.setCode(_deptName); //
		groupVO.setWfname(_deptName); //
		groupVO.setPosts(_posts);

		int li_y = 30; //y��λ�ò���!!
		groupVO.setX(new Integer(_x)); //
		groupVO.setY(new Integer(li_y)); //
		groupVO.setWidth(new Integer(_width)); //
		groupVO.setHeight(new Integer(_height)); //

		// ��ɫ������
		groupVO.setFonttype("System"); //
		groupVO.setFontsize(12);
		groupVO.setForeground("0,0,0"); //
		groupVO.setBackground(convertColorToStr(deptGroupBgcolor)); //

		addDeptGroup(groupVO); //
		groupcount++;
		return groupVO;
	}

	/**
	 * ���Ӳ��ſ�ͼ(���������е�)..
	 * 
	 * @return
	 */
	public void addDeptGroup(GroupVO _groupVO) {
		int li_x = _groupVO.getX().intValue(); //
		int li_y = _groupVO.getY().intValue(); //
		int li_width = _groupVO.getWidth().intValue(); //
		int li_height = _groupVO.getHeight().intValue(); //
		DefaultGraphCell cell1 = createVertex(_groupVO, li_x, li_y, li_width, li_height, this.deptGroupBgcolor, Color.BLACK, new Font("System", Font.PLAIN, 12), true, "cn.com.infostrategy.ui.workflow.design.CellView_Dept", false); //����һ������,��75,��45!!!
		// ������������ɫ
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

		// ������������ɫ.
		GraphConstants.setFont(cell1.getAttributes(), new Font(str_fonttype, Font.PLAIN, li_size)); //
		GraphConstants.setForeground(cell1.getAttributes(), foreGround); //
		GraphConstants.setBackground(cell1.getAttributes(), backGround); //

		GraphConstants.setBounds(cell1.getAttributes(), new Rectangle2D.Double(li_x, li_y, li_width, li_height)); //
		GraphConstants.setMoveable(cell1.getAttributes(), true);
		GraphConstants.setSelectable(cell1.getAttributes(), true);
		graph.getGraphLayoutCache().insert(cell1); // �ȼ�����
		graph.getGraphLayoutCache().toBack(new DefaultGraphCell[] { cell1 }); // ���ں��
		isEditChanged = true; //��ʾ�Ѿ��仯��
	}

	/**
	 * ���ӽ׶ο�ͼ(���ϵ�������)..
	 */
	private void onAddstation() {
		DefaultGraphCell[] deptcells = this.getDeptCells();
		if (deptcells == null || deptcells.length == 0) { //���û�в��ſ��Ͳ������׶�
			MessageBox.show(this, "��������" + groupName + "!");
			return;
		}
		ChoosePhaseDialog dialog = new ChoosePhaseDialog(this, "ѡ��׶�", "�׶�����", "��������ͼ�׶�", this.showpreviewdept); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() != 1) {
			return; //
		}

		String str_phasename = dialog.getPhaseName(); //
		GroupVO groupVO = new GroupVO(); //
		groupVO.setGrouptype("STATION"); // �����ǲ���
		groupVO.setCode(str_phasename); //
		groupVO.setWfname(str_phasename); //

		int li_x = 10; //
		int li_y = 200 + this.stationcount * stationheight;//��һ���׶�yλ��Ϊ200,Ϊ�˱�֤һ�����Ӷ������ȫ�ص���Ҫ����this.stationcount * stationheight
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
		if (this.stationBackGroundCell == null) { //�����û�л��׶εı���,����������!!
			addStationBackGroundActivity(); //
		} else {
			getGraph().getGraphLayoutCache().toBack(new Object[] { stationBackGroundCell }); //һ��Ҫ�ڱ���!
		}
		reSortStation(null);//ũ�����Ҫ���û����߶�
		reSetAllLayer(true); //
	}

	/**
	 * ���ӻ������λ,����ƹ���Ŀ�����,�������λҪһ�����,����ģ��ϲ���һͼ����,����λ���ڵڶ���,�������ڵ�һ��!
	 * ԭ������Ȳ���һ��������,Ȼ������Ų����鴴��������λ!!
	 */
	private void onAddDeptAndPost() {
		BillVO deptbillvo = null;
		BillVO[] postbillvos = null;
		String str_option = new TBUtil().getSysOptionStringValue("�Զ�������ͼ���Ӳ��ź͸�λ��ť�ĵ���¼�", null);
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
		if (obj != null) { //���ѡ���˻���,���ڸû��������Ӹ�λ���������ӻ���
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
			 * ��sunfujun/20120417/undo bug�޸ġ�
			 */

			createUndoEvent(cell, groupvo_new);
			cell.setUserObject(groupvo_new);
			resetMyPosAndSize(cell);
		} else {//���û��ѡ�л����������������͸�λ
			String str_posts = "";
			if (postbillvos != null && postbillvos.length > 0) {
				for (int i = 0; i < postbillvos.length; i++) {
					str_posts = str_posts + postbillvos[i].getStringValue("name") + ";";
				}
			}
			int[] li_x_height = getNewDeptXandHeight(); //ȡ�����л������X������߶�!
			if (str_posts.length() > 0) {
				str_posts = str_posts.substring(0, str_posts.length() - 1);
			}
			dealAddDept(deptname, str_posts, li_x_height[0], 100, li_x_height[1]); //
			reSortDept(null); //���Ż���
			reSetAllLayer(false); //���û����ͽ׶��ڵײ�
		}
	}

	public void createUndoEvent(DefaultGraphCell cell, Object newValue) {//��sunfujun/20120419/undo�����Ż���
		Map attr = (Map) cell.getAttributes().clone();
		GraphConstants.setValue(attr, newValue);
		Map attrs = new HashMap();
		attrs.put(cell, attr);
		attrs.put(new DefaultGraphCell(), new HashMap());
		graph.getGraphLayoutCache().edit(attrs, null, null, null);
	}

	/**
	 * ȡ�������ŵľ���..
	 * 
	 * @return
	 */
	public void addStationGroup(GroupVO _groupVO) {
		int li_x = _groupVO.getX().intValue(); //
		int li_y = _groupVO.getY().intValue(); //
		int li_width = _groupVO.getWidth().intValue(); //
		int li_height = _groupVO.getHeight().intValue(); //

		DefaultGraphCell cell1 = getCell(_groupVO.getWfname(), Color.BLACK, "cn.com.infostrategy.ui.workflow.design.CellView_Station"); // ����һ���������

		GraphConstants.setBounds(cell1.getAttributes(), new Rectangle2D.Double(li_x, li_y, li_width, li_height)); // �����С
		GraphConstants.setEditable(cell1.getAttributes(), true);
		GraphConstants.setSelectable(cell1.getAttributes(), true); //
		GraphConstants.setMoveable(cell1.getAttributes(), true); //
		GraphConstants.setFont(cell1.getAttributes(), new Font(_groupVO.getFonttype(), Font.PLAIN, _groupVO.getFontsize())); // ��������

		String[] str_color = _groupVO.getForeground().split(",");
		GraphConstants.setForeground(cell1.getAttributes(), new Color(Integer.parseInt(str_color[0]), Integer.parseInt(str_color[1]), Integer.parseInt(str_color[2])));// ����ǰ����ɫ.
		str_color = _groupVO.getBackground().split(",");
		GraphConstants.setBackground(cell1.getAttributes(), new Color(Integer.parseInt(str_color[0]), Integer.parseInt(str_color[1]), Integer.parseInt(str_color[2])));//���ú���ɫ
		GraphConstants.setBorder(cell1.getAttributes(), BorderFactory.createLineBorder(Color.BLACK, 0)); // �߿�
		cell1.setUserObject(_groupVO);
		graph.getGraphLayoutCache().insert(cell1); //
		graph.getGraphLayoutCache().toBack(new DefaultGraphCell[] { cell1 }); // ���ں��
		isEditChanged = true; //��ʾ�Ѿ��仯��
		stationcount++; //
	}

	//���ӽ׶εı�����!���Ǹ�������!!!
	private void addStationBackGroundActivity() {
		int li_x = 10; //Xλ���ǹ̶���
		int li_y = 30; //Yλ��Ҳ�ǹ̶���
		int li_width = 61; //���Ҳ�ǹ̶���
		int li_height = getMaxDeptHeight(); //
		String str_cellviewname = "cn.com.infostrategy.ui.workflow.design.CellView_StationBackCell"; // ��ʾ��ͼ��
		stationBackGroundCell = createVertex(null, li_x, li_y, li_width, li_height, deptGroupBgcolor, Color.BLACK, new Font("����", Font.PLAIN, 12), true, str_cellviewname, false); //
		stationBackGroundCell.getAttributes().put("�������༭��", this); //

		GroupVO groupVO = new GroupVO(); //
		groupVO.setGrouptype("STATIONBACK"); // �����ǲ���
		groupVO.setCode(null); //
		groupVO.setWfname(null); //

		groupVO.setX(li_x); //
		groupVO.setY(li_y); //
		groupVO.setWidth(li_width); //
		groupVO.setHeight(li_height); //

		// ��ɫ������
		groupVO.setFonttype("System"); //
		groupVO.setFontsize(12);
		groupVO.setForeground("0,0,0"); //
		groupVO.setBackground(convertColorToStr(deptGroupBgcolor)); //
		stationBackGroundCell.setUserObject(groupVO); //

		GraphConstants.setBorder(stationBackGroundCell.getAttributes(), BorderFactory.createLineBorder(Color.BLACK, 1));
		GraphConstants.setSelectable(stationBackGroundCell.getAttributes(), false); //
		getGraph().getGraphLayoutCache().insert(stationBackGroundCell); // �������

		getGraph().getGraphLayoutCache().toBack(new Object[] { stationBackGroundCell }); //һ��Ҫ�ڱ���!
	}

	//��ɾ���׶�ʱ,ÿ�ζ�������ʣ�µĽ׶�,���û����,���Զ�ɾ���׶εı���

	private void delStationBackGroundActivityAsAuto() {//��sunfujun,20120411,����undo��bug����������
		updateStationBackGroundActivityAsAuto();
	}

	private void updateStationBackGroundActivityAsAuto() {//��sunfujun,20120411,����undo��bug����������
		DefaultGraphCell[] stationCells = getStationCells(); //
		if (stationCells == null || stationCells.length == 0) {
			this.stationcount = 0;
			if (stationBackGroundCell != null) {
				graph.getModel().remove(new Object[] { stationBackGroundCell }); //
				stationBackGroundCell = null; //һ��Ҫ��Ϊ��,�������¼���ʱ��������!
			}
			graph.getGraphLayoutCache().toBack(new Object[] { titleCell });//��Ҫ����һ�£������ص��Ļ���˳��ʹ��ˡ����/2012-05-22��
		} else {
			this.stationcount = stationCells.length;
			if (this.stationBackGroundCell != null) { //�����û��
				getGraph().getGraphLayoutCache().toBack(new Object[] { stationBackGroundCell }); //һ��Ҫ�ڱ���!
			} else {
				addStationBackGroundActivity();
			}
		}
	}

	/**
	 * ȡ�����в��ŵ���,������������...
	 * 
	 * @return
	 */
	public DefaultGraphCell[] getDeptCells() {
		return getDeptCells(null);
	}

	public DefaultGraphCell[] getDeptCells(Map attrs) {
		GraphModel model = graph.getModel();
		int rootCount = model.getRootCount(); // ���н�����Ŀ
		ArrayList<DefaultGraphCell> list = new ArrayList<DefaultGraphCell>();
		for (int i = 0; i < rootCount; i++) {
			Object obj = model.getRootAt(i);
			if (obj instanceof DefaultGraphCell) {
				DefaultGraphCell cell = (DefaultGraphCell) obj; //
				Object linkedObject = cell.getUserObject();
				if (linkedObject instanceof GroupVO) {
					GroupVO gvo = (GroupVO) linkedObject; //
					if (gvo.getGrouptype().equals("DEPT")) { // ��������ǲ��ŵ�
						Rectangle2D itempoint = graph.getCellBounds(cell); // ȡ�ø����λ��
						double thex = itempoint.getX();
						int index = 0;
						for (DefaultGraphCell now : list) {
							if (graph.getCellBounds(now).getX() >= thex)
								break;
							index++;
						}
						list.add(index, cell);
						if (attrs != null && !attrs.containsKey(cell)) {//��sunfujun/20120417/undobug�޸ġ�
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
	 * ȡ�����н׶ε���
	 * 
	 * @return
	 */
	public DefaultGraphCell[] getStationCells() {
		return getStationCells(null);
	}

	public DefaultGraphCell[] getStationCells(Map attrs) {
		GraphModel model = graph.getModel();
		int rootCount = model.getRootCount(); // ���н�����Ŀ
		ArrayList<DefaultGraphCell> list = new ArrayList<DefaultGraphCell>();
		for (int i = 0; i < rootCount; i++) {
			Object obj = model.getRootAt(i);
			if (obj instanceof DefaultGraphCell) {
				DefaultGraphCell cell = (DefaultGraphCell) obj; //
				Object linkedObject = cell.getUserObject();
				if (linkedObject instanceof GroupVO) {
					GroupVO gvo = (GroupVO) linkedObject; //
					if (gvo.getGrouptype().equals("STATION")) { // ��������ǲ��ŵ�
						Rectangle2D itempoint = graph.getCellBounds(cell); // ȡ�ø����λ��
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

	//ȡ�����н׶ε�����,�ڻ��׶εı���ʱ��Ҫ!!!
	public String[][] getStationCellsInfo() {
		DefaultGraphCell[] cells = getStationCells(); //
		if (cells == null || cells.length == 0) {
			return null; //
		}
		String[][] str_return = new String[cells.length][6]; //
		for (int i = 0; i < cells.length; i++) {
			Rectangle2D rc2d = graph.getCellBounds(cells[i]);
			int li_y = (int) rc2d.getY(); //Y�ϵ�λ��!
			int li_height = (int) rc2d.getHeight(); //�߶�!
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
	 * �����ײ�!!
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

	//�����ײ�
	public void lockGroup() {
		dealGroupSelectable(false); //
	}

	//�����ײ�
	public void unLockGroup() {
		dealGroupSelectable(true); //
	}

	//�����ײ�,����ֻ����ѡ�����!���������״̬�鿴ʱ!��Ҫ���������Ƿ���ƶ������Ҽ��ı༭ɾ�����ƵȲ���������ǰŪ������������û��Ҫ�����Ҷ�û�������Ҽ�����
	public void lockGroupAndOnlyDoSelect() {
		lockGroup(); //�����ײ�
		getGraph().setEdgeLabelsMovable(false); //
		getGraph().setMoveable(false); //���ò����ƶ����
		getGraph().setSelectionEnabled(true); //
		KeyListener[] keyListeners = getGraph().getKeyListeners(); //ɾ�����п�ݼ�,��Ϊ�п��ܻ���Delete��!!
		for (int i = 0; i < keyListeners.length; i++) {
			getGraph().removeKeyListener(keyListeners[i]); //
		}
	}

	private void dealGroupSelectable(boolean _locked) {
		DefaultGraphCell[] cells = getDeptCells(); // ȡ�����в�����
		if (cells != null && cells.length > 0) {
			for (int i = 0; i < cells.length; i++) {
				GraphConstants.setSelectable(cells[i].getAttributes(), _locked); //
				GraphConstants.setMoveable(cells[i].getAttributes(), _locked); //
			}
			graph.getGraphLayoutCache().toBack(cells); // ���ں��
		}

		DefaultGraphCell[] cells_station = getStationCells(); // ȡ�����н׶�
		if (cells_station != null && cells_station.length > 0) {
			for (int i = 0; i < cells_station.length; i++) {
				GraphConstants.setSelectable(cells_station[i].getAttributes(), _locked); //
				GraphConstants.setMoveable(cells_station[i].getAttributes(), _locked); //
			}
			graph.getGraphLayoutCache().toBack(cells_station); // ���ں��
		}

		graph.clearSelection(); //
		//graph.getGraphLayoutCache().reload(); // һ��Ҫִ��,�������ֶ���Ŀ�!!?? ����û���ֶ���Ŀ򣬵��Ƿ���һ��bug�������������ײ����е������ܵ����ڣ����ص����ڵ�ʱ�򣩺���ȥ�ˣ��ʲ�ִ�д˾䡾���/2012-05-21��

		if (this.stationBackGroundCell != null) { //�����û��
			getGraph().getGraphLayoutCache().toBack(new Object[] { stationBackGroundCell }); //һ��Ҫ�ڱ���!
		}
	}

	/**
	 * ����
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
		CellConfigDialog dialog = new CellConfigDialog(this, "�޸Ļ���", (int) point.getX(), (int) point.getY(), cellFont, cellForecolor, cellBackcolor, acvo_old.getWfname(), acvo_old.getUiname(), acvo_old.getViewtype(), acvo_old.getImgstr(), null, "ACTIVITY");
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
			// �޸Ļ�������
			int viewtype = dialog.getViewType();
			acvo_new.setViewtype(viewtype);

			/**
			 * ʹ����ͨ���ڵı༭֧��undo���������������ͣ���sunfujun/20120416/undo bug�޸ġ�
			 */
			createUndoEvent(_cell, acvo_new);

			// ��������
			GraphConstants.setFont(_cell.getAttributes(), new Font(dialog.getFontType(), Font.PLAIN, dialog.getFontSize())); //

			// ����ǰ����ɫ.
			GraphConstants.setForeground(_cell.getAttributes(), dialog.getForeGround());//

			// ���ú���ɫ
			GraphConstants.setBackground(_cell.getAttributes(), dialog.getBackGround());//

			String str_viewtypename = "cn.com.infostrategy.ui.workflow.design.CellView_" + viewtype; //
			GPCellViewFactory.setViewClass(_cell.getAttributes(), str_viewtypename);
			getGraph().setSelectionCell(_cell); //���������䣬�����ƶ�����ʱ�������Ͳ��䣬���ƶ����ţ����������ֱ��ȥ��
			if (viewtype == 8) {//ͼƬ���ڲ���Ҫ�߿�ֻ�����̼��ʱ�������
				GraphConstants.setBorder(_cell.getAttributes(), BorderFactory.createEmptyBorder());
			} else {
				GraphConstants.setBorder(_cell.getAttributes(), BorderFactory.createLineBorder(Color.BLACK, 1));
			}

			graph.getGraphLayoutCache().reload(); //
			graph.repaint(); //
			isEditChanged = true; //��ʾ�Ѿ��仯��
		}
	}

	/**
	 * �޸���������
	 */
	public void updateTransitionText(DefaultGraphCell _cell) {
		DefaultEdge edge = (DefaultEdge) _cell; //
		DefaultPort port_source = (DefaultPort) edge.getSource(); // ����Դ����
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
		CellConfigDialog dialog = new CellConfigDialog(this, "�޸�����", (int) point.getX(), (int) point.getY(), cellFont, cellForecolor, cellBackcolor, transitionVO_old.getWfname(), null, 0, null, null, "TRANSITION", lineType, isSingle); //
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
			 * ʹ�����ߵı༭֧��undo��sunfujun/20120416/undo bug�޸ġ�
			 */
			createUndoEvent(_cell, transitionVO_new);
			// ��������
			GraphConstants.setFont(_cell.getAttributes(), new Font(dialog.getFontType(), Font.PLAIN, dialog.getFontSize())); //

			// ����ǰ����ɫ.
			GraphConstants.setForeground(_cell.getAttributes(), dialog.getForeGround());//

			// ����������ɫ
			GraphConstants.setLineColor(_cell.getAttributes(), dialog.getBackGround());//

			//���岻�������б����
			//GraphConstants.setBounds(edge.getAttributes(), new Rectangle2D.Double(500,500,50,50));
			String charOld = transitionVO_old.getWfname();//Ԭ����   20120918�޸� �Ȼ��֮ǰ���������Ƿ������壬������򲻸ı�λ�ã����û����������������λ��
			GraphConstants.setAutoSize(edge.getAttributes(), true);
			;//Ԭ����   20120918�޸� �����Զ���С
			if (null == charOld || (null != charOld && charOld.trim().equals(""))) {
				GraphConstants.setLabelPosition(edge.getAttributes(), new Point2D.Double(20, 10));//Ԭ����   20120918�޸� ���ñ�ǩ��λ��
			}
			GraphConstants.setLabelAlongEdge(edge.getAttributes(), true); //���ΪTrue������б�ŵ�������
			GraphConstants.setLineEnd(edge.getAttributes(), transitionVO_new.getLineType());
			GraphConstants.setEndFill(edge.getAttributes(), true);//��sunfujun/20120426/���Ӽ�ͷ���͵�����_���֡�
			if (transitionVO_new.isSingle()) {
				GraphConstants.setLineBegin(edge.getAttributes(), GraphConstants.ARROW_NONE);
				GraphConstants.setBeginFill(edge.getAttributes(), false);
			} else {
				GraphConstants.setLineBegin(edge.getAttributes(), transitionVO_new.getLineType());
				GraphConstants.setBeginFill(edge.getAttributes(), true);
			}
			graph.getGraphLayoutCache().reload(); //
			graph.repaint(); //
			isEditChanged = true; //��ʾ�Ѿ��仯��
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

		CellConfigDialog dialog = new CellConfigDialog(this, "�޸�" + groupName, (int) point.getX(), (int) point.getY(), cellFont, cellForecolor, cellBackcolor, groupVO_old.getWfname(), null, 0, null, groupVO_old.getPosts(), "DEPT"); //
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
			 * ʹ�ò��ŵı༭֧��undo��sunfujun/20120416/undo bug�޸ġ�
			 */
			createUndoEvent(_cell, groupVO_new);
			_cell.setUserObject(groupVO_new);

			GraphConstants.setFont(_cell.getAttributes(), new Font(dialog.getFontType(), Font.PLAIN, dialog.getFontSize())); // ��������
			GraphConstants.setForeground(_cell.getAttributes(), dialog.getForeGround());// ����ǰ����ɫ.
			GraphConstants.setBackground(_cell.getAttributes(), dialog.getBackGround());//���ú���ɫ

			graph.getGraphLayoutCache().reload(); //
			graph.repaint(); //
			isEditChanged = true; // ��ʾ�Ѿ��仯��
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

		CellConfigDialog dialog = new CellConfigDialog(this, "�޸Ľ׶�", (int) point.getX(), (int) point.getY(), cellFont, cellForecolor, cellBackcolor, groupVO_old.getWfname(), null, 0, null, null, "STATION"); //
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
			 * ʹ�ý׶εı༭֧��undo��sunfujun/20120416/undo bug�޸ġ�
			 */
			createUndoEvent(_cell, groupVO_new);
			_cell.setUserObject(groupVO_new);

			GraphConstants.setFont(_cell.getAttributes(), new Font(dialog.getFontType(), Font.PLAIN, dialog.getFontSize())); // ��������
			GraphConstants.setForeground(_cell.getAttributes(), dialog.getForeGround());// ����ǰ����ɫ.
			GraphConstants.setBackground(_cell.getAttributes(), dialog.getBackGround());//���ú���ɫ

			graph.getGraphLayoutCache().reload(); //
			graph.repaint(); //
			isEditChanged = true; // ��ʾ�Ѿ��仯��
		}
	}

	public ProcessVO getCurrentProcessVO() {
		return currentProcessVO;
	}

	public boolean isEditChanged() {
		return isEditChanged;
	}

	/**
	 * ����һ�����
	 */
	private void onInsertNormalCell(int _type, double _x, double _y) {
		cellNumber = cellNumber + 1;
		String str_code = "WFA" + cellNumber; //
		String str_wfname = "����" + cellNumber; //
		String str_uiname = "" + cellNumber; //
		dealInsertNormalCell(_type, _x, _y, 70, 45, str_code, str_wfname, str_uiname); //����һ������!!
	}

	/**
	 * �������Ʋ���һ������!!
	 * @param _type
	 * @param _x
	 * @param _y
	 * @param _code
	 * @param _name
	 */
	private void dealInsertNormalCell(int _type, double _x, double _y, int _width, int _height, String _code, String _wfname, String _uiname) {
		ActivityVO activityVO = new ActivityVO();
		Integer activityid = getNewActivityID();
		activityVO.setId(activityid); //�ؼ�!! ȡ�û��ڵ�����,Ŀǰ��ԭ������ǰֱ̨�����ɻ�������!!! �����ں�̨����ͻ�����!!! ���Ҳ����ظ�!! ȱ���ǵ�һ�����ڿ��ܻ��е���!!!
		addActivityIds.add(activityid + "");
		activityVO.setActivitytype("NORMAL"); // ��������
		activityVO.setCode(_code); // ����
		activityVO.setWfname(_wfname); // ����ͼ�е���ʾ����
		activityVO.setUiname(_uiname); // ��������
		activityVO.setX(new Integer((int) _x)); // X
		activityVO.setY(new Integer((int) _y)); // Y
		activityVO.setApprovemodel("1"); // 1��ʾ��ռ
		activityVO.setAutocommit("N"); // ���Զ��ύ
		activityVO.setIsassignapprover("N"); // �Ƿ���ָ��������!!
		activityVO.setShowparticimode("1"); //
		activityVO.setDesc(""); // ����
		activityVO.setViewtype(new Integer(_type)); //
		activityVO.setWidth(_width);
		activityVO.setHeight(_height);
		activityVO.setFonttype("System"); //
		activityVO.setFontsize(12);
		activityVO.setForeground("0,0,0"); //
		activityVO.setBackground("232,238,247"); //			
		activityVO.setImgstr("workflow/start.gif");
		String str_cellviewname = "cn.com.infostrategy.ui.workflow.design.CellView_" + _type; // ��ʾ��ͼ��
		DefaultGraphCell cell = createVertex(activityVO, _x - 30, _y - 17, _width, _height, normalCellBgcolor, Color.BLACK, new Font("System", Font.PLAIN, 12), true, str_cellviewname); //����һ������,��75,��45!!!
		getGraph().getGraphLayoutCache().insert(cell); // �������
		getGraph().getGraphLayoutCache().toFront(new Object[] { cell }); //
		getGraph().setSelectionCell(cell); // //
		isEditChanged = true; //��ʾ�Ѿ��仯��
	}

	/**
	 * ����ճ��һ������
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
			activityVO.setRiskVO(null);//�����Ʒ��յ�
			Integer activityid = getNewActivityID();
			activityVO.setId(activityid);//��ǰ���������»��һ��id��activityVO.setId(getNewActivityID())��bug!!!�����/2012-05-22��
			addActivityIds.add(activityid + "");
			activityVO.setX(_x);
			_activityVO.setY(_y);
			String str_cellviewname = "cn.com.infostrategy.ui.workflow.design.CellView_" + activityVO.getViewtype(); // ��ʾ��ͼ��
			DefaultGraphCell cellnew = createVertex(activityVO, _x, _y, GraphConstants.getBounds(cell.getAttributes()).getWidth(), GraphConstants.getBounds(cell.getAttributes()).getHeight(), GraphConstants.getBackground(cell.getAttributes()), GraphConstants.getForeground(cell.getAttributes()), GraphConstants.getFont(cell.getAttributes()), true, str_cellviewname); //����һ������,��75,��45!!!
			getGraph().getGraphLayoutCache().insert(cellnew); // �������
			getGraph().getGraphLayoutCache().toFront(new Object[] { cellnew }); //
			isEditChanged = true; //��ʾ�Ѿ��仯��
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
	 * ����ѡ������¼�..
	 * 
	 * @param _listeer
	 */
	public void addWorkFlowCellSelectedListener(WorkFlowCellSelectedListener _listeer) {
		vector_cellSelectedListener.add(_listeer); //
	}

	/**
	 * ˫��ĳ�����...
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
	 * �Ҽ������˵�...
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
		if (obj == null && stationBackGroundCell != null) {//����ǵ���˽׶����ƣ��׶ο���Ĭ�ϲ�ѡ�еģ���Ҫ����ѡ���¼���Ȼ����б༭
			Rectangle2D stationpoint = graph.getCellBounds(stationBackGroundCell);
			if (x >= stationpoint.getX() && x <= stationpoint.getX() + stationpoint.getWidth() && y >= stationpoint.getY() && y <= stationpoint.getY() + stationpoint.getHeight()) {
				DefaultGraphCell[] stationCells = getStationCells(); //
				for (int i = 0; i < stationCells.length; i++) {
					DefaultGraphCell itemcell = stationCells[i]; // ȡ�����е�Cell
					Rectangle2D itempoint = graph.getCellBounds(itemcell); // ȡ�øò��ŵ�λ��
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
			if (!graph.getEdgeLabelsMovable()) {//���ѡ���˿ؼ��������߸��������ƶ���������ͼ���ɱ༭����ֱ�ӷ���
				return;
			}
			if (obj instanceof DefaultGraphCell) {
				final DefaultGraphCell cell = (DefaultGraphCell) obj;
				Object userobj = cell.getUserObject(); //
				JMenuItem item_0 = new JMenuItem("�༭"); //
				item_0.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						editCellConfig(); //
					}
				});
				JMenuItem item_1 = new JMenuItem("ɾ��"); //
				item_1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onDelCell(); //
					}
				});
				popMenu.add(item_0); //
				popMenu.add(item_1); //
				if (userobj instanceof ActivityVO) {
					JMenuItem item_2 = new JMenuItem("����"); //
					item_2.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							copyCells(); //
						}
					}); //
					popMenu.add(item_2); //
				}
			}
		} else {
			JMenu menu_export = new JMenu("��������ͼ"); //
			final JMenuItem menuitem_export_jpg = new JMenuItem("JPGͼƬ��ʽ"); //
			final JMenuItem menuitem_export_bmp = new JMenuItem("BMPͼƬ��ʽ(����)"); //
			final JMenuItem menuitem_export_word = new JMenuItem("Word�ļ���ʽ"); //
			final JMenuItem menuitem_export_conf = new JMenuItem("�������̹ؼ�����"); //
			final JMenuItem menu_showimg = new JMenuItem("��ʾ����ͼ"); //
			final JMenuItem menu_hiddenimg = new JMenuItem("���ر���ͼ"); //
			menu_showimg.setToolTipText("�ϴ�һ������ͼƬ��Ϊ������,������\"���\"һ�����ٻ�ͼ!"); //

			menu_export.add(menuitem_export_jpg); //jpg�ļ�
			menu_export.add(menuitem_export_bmp); //bmp�ļ�
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

		if (this.iscopy) {//������˸��ƣ���δճ��
			JMenuItem item_3 = new JMenuItem("ճ��"); //
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
	 * ��������,�Ƿ���ѭ��,�����ȹؼ���Ϣһ�������,Ȼ����Լ������!
	 */
	public void exportConf() {
		try {
			if (currentProcessVO == null || currentProcessVO.getId() == null) {
				MessageBox.show(this, "��������IDΪ��,���ܻ�û�б�������!��ڶ�������һ���ٵ���!");
				return;
			}
			String str_processID = this.currentProcessVO.getId(); //
			String str_sql_1 = "select t1.id,t1.code ���ڱ���,t1.wfname ��������,t1.activitytype ����,t1.isselfcycle �Ƿ���ѭ��,t2.code ��ѭ����ͼ,t1.approvemodel ����ģʽ,t1.participate_corp �������,t1.participate_group �����ɫ,t1.participate_dynamic ��̬������,t1.intercept1 UI������,t1.intercept2 BS������ from pub_wf_activity t1 left join pub_wf_process t2 on t1.selfcyclerolemap=t2.id where t1.processid=" + str_processID
					+ " order by t1.y,t1.x"; //
			String str_sql_2 = "select t1.id,t1.code ���߱���,t2.code ��Դ���ڱ���,t2.wfname ��Դ��������,t3.code Ŀ�껷�ڱ���,t3.wfname Ŀ�껷������,t1.conditions ͨ������,t1.intercept ͨ��ʱִ�ж��� from pub_wf_transition t1 left join pub_wf_activity t2 on t1.fromactivity=t2.id left join pub_wf_activity t3 on t1.toactivity=t3.id where t1.processid='" + str_processID + "'"; //
			HashVOStruct hvs_1 = UIUtil.getHashVoStructByDS(null, str_sql_1); //
			HashVOStruct hvs_2 = UIUtil.getHashVoStructByDS(null, str_sql_2); // 
			BillListPanel list_1 = new BillListPanel(hvs_1, 105);
			BillListPanel list_2 = new BillListPanel(hvs_2, 105);
			BillCardPanel card = new BillCardPanel(new DefaultTMO("������Ϣ", new String[][] { { "id", "400" }, { "code", "400" }, { "name", "400" },// 
					{ "wftype", "400" }, { "wfeguiintercept", "400" }, { "wfegbsintercept", "400" }, // 
					{ "userdef01", "400" }, { "userdef02", "400" }, { "userdef03", "400" }, { "userdef04", "400" } })); //
			card.queryData("select * from pub_wf_process where id='" + str_processID + "'"); //

			JTabbedPane tabb = new JTabbedPane(); //
			tabb.addTab("������Ϣ", list_1); //
			tabb.addTab("������Ϣ", list_2); //
			tabb.addTab("������Ϣ", card); //

			Object obj = getGraph().getSelectionCell();
			if (obj != null) {
				if (obj instanceof DefaultEdge) { //���������
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
				} else if (obj instanceof DefaultGraphCell) { //����ǻ���
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

			BillDialog dialog = new BillDialog(this, "����������Ϣ", 1000, 600);
			dialog.getContentPane().add(tabb); //
			dialog.setVisible(true); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	/**
	 * ��ʾ����ͼƬ�����/2012-04-12��
	 */
	private void onShowImg() {
		Object obj = graph.getClientProperty("backimg");
		if (obj == null) {
			if (MessageBox.confirm(this, "δ�ϴ�����ͼƬ,�Ƿ��ϴ�?")) {
				onImportBackImg();
			}
		} else {
			graph.setBackgroundImage((ImageIcon) obj);
			graph.repaint();
		}
	}

	/**
	 * ���ر���ͼƬ�����/2012-04-12��
	 */
	private void onHiddenImg() {
		graph.setBackgroundImage(null);
		graph.repaint();
		reSetAllLayer(false);//��Ҫ����һ�£�����׶λ��ܵ�����ȥ�����/2012-04-18��
	}

	/**
	 * �ϴ�����ͼƬ������ͼƬֻ����Ϊ��׼���̽��вο���죬�����뱣�桾���/2012-04-12��
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
				MessageBox.show(this, "ֻ���ϴ�[jpg/gif/png/bmp]���ָ�ʽ��ͼƬ!"); //
				return; //
			}
			BufferedImage baseImg = ImageIO.read(selFile);
			Graphics2D graph_g2 = (Graphics2D) baseImg.getGraphics();
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
			graph_g2.setComposite(ac);
			graph_g2.scale(graph.getScale(), graph.getScale());//�������㻷�ڲ��ܷŴ�����С
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

	//�����jpg�ļ�,ѹ����,���ļ�С!
	private void exportAsJpg() {
		saveAsPicture(false); //
	}

	//�����bmp�ļ�!
	private void exportAsBmp() {
		saveAsPicture(true); //
	}

	/**
	 * ������ͼƬ
	 * @param _isBmp
	 */
	private void saveAsPicture(boolean _isBmp) {
		boolean isExportTitle = getTBUtil().getSysOptionBooleanValue("�����������Ƿ��б���", true); //�ڿ�ϵͳ����һЩ�ͻ�Ҫ�󵼳�ʱ��Ҫ����!
		graph.setGridVisible(false); //����ʱ�ѱ���������ȥ��,���Ǹɾ��Ĵ��ױ���
		if (!isExportTitle) { //�����Ҫ��������,�򽫱�����ɫ�ĳɰ�ɫ����!
			setTitleCellForeground(Color.WHITE);
		}
		try {
			String str_fileType = (_isBmp ? "bmp" : "jpg"); //
			String str_filename = ClientEnvironment.getInstance().getClientCodeCachePath() + "\\" + "graph_" + System.currentTimeMillis() + "." + str_fileType; //
			int li_width = (int) graph.getPreferredSize().getWidth() + 15;
			int li_height = (int) graph.getPreferredSize().getHeight() + 15;

			byte[] bytes = tbUtil.getCompentBytes(graph, li_width, li_height, _isBmp);
			tbUtil.writeBytesToOutputStream(new FileOutputStream(new File(str_filename), false), bytes); // д�ļ�!!
			Desktop.open(new File(str_filename)); //��ͼƬ
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		} finally {
			graph.setGridVisible(true); //
			if (!isExportTitle) { //���صı�����ɫ�ٻָ�����!
				setTitleCellForeground(Color.BLUE); //
			}
		}
	}

	/**
	 * ����Word..
	 */
	private void exportAsWord() {
		boolean isExportTitle = getTBUtil().getSysOptionBooleanValue("�����������Ƿ��б���", true); //�ڿ�ϵͳ����һЩ�ͻ�Ҫ�󵼳�ʱ��Ҫ����!
		graph.setGridVisible(false); //����ʱ�ѱ���������ȥ��,���Ǹɾ��Ĵ��ױ���
		if (!isExportTitle) { //�����Ҫ��������,�򽫱�����ɫ�ĳɰ�ɫ����!
			setTitleCellForeground(Color.WHITE);
		}

		String str_filename = ClientEnvironment.getInstance().getClientCodeCachePath() + "" + "graph_" + System.currentTimeMillis() + ".doc"; //
		try {
			int li_width = (int) graph.getPreferredSize().getWidth() + 15;
			int li_height = (int) graph.getPreferredSize().getHeight() + 15;
			getTBUtil().saveCompentAsWordFile(graph, str_filename, li_width, li_height); //Ӧ�û�Ҫ�������ں���Ĳź�!�����������ڵ������嵥���һ����
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
			if (!isExportTitle) { //���صı�����ɫ�ٻָ�����!
				setTitleCellForeground(Color.BLUE); //
			}
		}
	}

	//���������ļ���������������������ͼƬ����������Ҫ�ṩ���ñ�����ɫ�ķ��������/2012-11-16��
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
			updateStationBackGroundActivityAsAuto();//��sunfujun,20120411,����undobug��
			moveStationToMaxDept();
			reSetAllLayer(true);
			isEditChanged = true; //��ʾ�Ѿ��仯��
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
			updateStationBackGroundActivityAsAuto();//��sunfujun,20120411,����undobug��
			moveStationToMaxDept();
			reSetAllLayer(true);
			isEditChanged = true; //��ʾ�Ѿ��仯��
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

	//̨ǰ�ͻ�ϣ���ڵ��������ļ�����ʱ������ͼ�������Ͻǵ������ʾ�������������˷��������/2013-08-14��
	public void doShowOrder(boolean _visible) {
		Object[] cells = getActivityCells();
		for (int i = 0; i < cells.length; i++) {
			((DefaultGraphCell) cells[i]).getAttributes().put("isshoworder", _visible);
		}
		graph.repaint();
	}

	/**
	 * ɾ��ĳһ�����
	 */
	private void onDelCell() {
		if (!graph.isSelectionEmpty()) {
			Object[] cells = graph.getSelectionCells();
			cells = graph.getDescendants(cells); //�ҵ��ݹ��!
			Object linkedObject = null;
			for (int i = 0; i < cells.length; i++) {
				linkedObject = ((DefaultGraphCell) cells[i]).getUserObject();
				if (linkedObject instanceof ActivityVO) {
					if (MessageBox.showConfirmDialog(this, "��ȷ��Ҫɾ���û�����?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {//����ͼ��ɾ������ǰ��������
						return;
					} else {
						break;
					}
				}
			}
			//graph.getModel().remove(cells); //����ǻ���,����Ҫͬʱɾ�����ڹ�������������!!
			Object[] transitionCells = getAllActivtyRefTransition(cells);//�õ�ѡ�л��ڹ���������
			//if (transitionCells != null) {//ɾ������
			//graph.getModel().remove(transitionCells);
			//}
			Object[] allDeleteCells = ArrayUtils.addAll(cells, transitionCells);
			Map attrs = new HashMap();
			getDeptCells(attrs);
			ConnectionSet connectionset = ConnectionSet.create(graph.getModel(), allDeleteCells, true);
			ParentMap parentmap = ParentMap.create(graph.getModel(), allDeleteCells, true, false);
			((DefaultGraphModel) graph.getModel()).edit(null, allDeleteCells, attrs, connectionset, parentmap, null);//д��һ��undo�¼���sunfujun/20120417/undobug��
			if (this.v_cellDeleteListeners != null && this.v_cellDeleteListeners.size() > 0) {
				for (int i = 0; i < v_cellDeleteListeners.size(); i++) {
					WorkFlowCellDeleteListener listener = (WorkFlowCellDeleteListener) v_cellDeleteListeners.get(i);
					listener.onWorkFlowCellDelete(new WorkFlowCellDeleteEvent(cells));
				}
			}
		}
		reSortDept(null); //���Ų���,��Ϊ����ɾ�������м�Ĳ���!!
		if (stationBackGroundCell != null) {
			GraphConstants.setBounds(stationBackGroundCell.getAttributes(), new Rectangle2D.Double(10, 30, 61, getMaxDeptHeight())); //
		}
		delStationBackGroundActivityAsAuto(); //����׶ζ�ɾ������,��Ҫ�Զ�����һ���Ƿ�Ҫɾ���׶εı���!!
		isEditChanged = true; //��ʾ�Ѿ��仯��
	}

	/**
	 * �õ�����cells����������������
	 * @param cells
	 * @return
	 */
	private Object[] getAllActivtyRefTransition(Object[] cells) {
		HashMap transitionVOMap = new HashMap();
		Object linkedObject = null;
		DefaultGraphCell cell = null;
		StringBuffer sb = new StringBuffer();
		//�ҵ�����ѡ�л��ڵ�ID
		for (int i = 0; i < cells.length; i++) {
			linkedObject = ((DefaultGraphCell) cells[i]).getUserObject();
			if (linkedObject instanceof ActivityVO) {
				sb.append("';" + ((ActivityVO) linkedObject).getId() + ";',");
			}
		}
		//�ҵ����й���ѡ�л��ڵ�����
		GraphModel model = graph.getModel();
		int rootCount = model.getRootCount(); // ���н�����Ŀ
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
		sb_help.append("1.��һ�������ϰ�סCtrl�������϶�,�Ϳ��Ի���!\r\n"); //
		sb_help.append("2.��סShift����һ�������ϵ��,�ͻ�����һ���۵�!\n"); //
		sb_help.append("3.���϶����߹�����,�û�X��,���Զ������۵�!\n"); //
		sb_help.append("4.���ĵ����ں�����ͼƬ���ڰ�ť���϶����Դ���ͼƬ����!\n"); //
		sb_help.append("5.���ӻ��ڵĸ��ơ�ճ������!\n"); //
		sb_help.append("6.����������롢������롢ͬ�ߡ�ͬ��4���������ڰ�ť!"); //
		MessageBox.showTextArea(this, sb_help.toString()); //
	}

	/**
	 * �༭��������
	 */
	private void editCellConfig() {
		if (graph.getSelectionCell() != null && graph.getSelectionCell() instanceof DefaultGraphCell) {
			mouseRightClickedCell = (DefaultGraphCell) graph.getSelectionCell(); //
			if (mouseRightClickedCell != null) { // ����Ҽ�ѡ�еĻ��ڲ�Ϊ��!!
				Object userobj = mouseRightClickedCell.getUserObject(); //
				if (userobj instanceof ActivityVO) {
					updateCellText(mouseRightClickedCell); //
				} else if (userobj instanceof TransitionVO) {
					updateTransitionText(mouseRightClickedCell); //
				} else if (userobj instanceof GroupVO) {
					GroupVO gvo = (GroupVO) userobj; //
					if (gvo.getGrouptype().equals("DEPT")) { // ��������ǲ��ŵ�
						updateGroupDeptCellText(mouseRightClickedCell); //���Ų��ɱ༭!!! ���Ǻ���ƹ�������!! ������Ӱ��������Ŀ!!
					} else if (gvo.getGrouptype().equals("STATION")) { //����ǽ׶�
						updateGroupCellText(mouseRightClickedCell); //
					}
				} else {
					JOptionPane.showMessageDialog(this, "ѡ�е���δ֪����������,�Ȳ��ǻ���Ҳ��������!"); //
				}
				reSetAllLayer(false); //
			}
		}
	}

	/**
	 * ����
	 */
	private void copyCells() {
		this.iscopy = true;
		copyCach = this.graph.getSelectionCells();
	}

	/**
	 * ճ��
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
			Rectangle2D point2D = null; // ȡ������λ��
			if (_isAbsolute) {//�Ƿ���������ľ���λ�ã��һ�ѡ��ճ����
				for (int i = 0; i < copyCach.length; i++) {//
					if (copyCach[i] instanceof DefaultGraphCell) {
						cell = (DefaultGraphCell) copyCach[i];
						linkobj = cell.getUserObject();
						if (linkobj instanceof ActivityVO) {
							point2D = GraphConstants.getBounds(cell.getAttributes());
							if (newx > new Double(point2D.getX()).intValue()) {
								newx = new Double(point2D.getX()).intValue();//ȡ��ѡ�л�������С�Ļ���x����
							}
							if (newy > new Double(point2D.getY()).intValue()) {
								newy = new Double(point2D.getY()).intValue();//ȡ��ѡ�л�������С�Ļ���y����
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
							_x = new Double(point2D.getWidth()).intValue() / 2 + 2;//�����·��ƶ���һ�����ڵĿ�ȸ߶ȵ�һ��
							_y = new Double(point2D.getHeight()).intValue() / 2 + 2;
							break;
						}
					}
				}
			}

			for (int i = 0; i < copyCach.length; i++) {//�Ȼ����ںͲ��� 
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
	 * cell��������ͬX����
	 */
	private void cellSameX() {
		Object[] selectCells = this.graph.getSelectionCells();//�õ�ѡ�еĵ�Ԫ
		if (selectCells != null && selectCells.length > 1) {//����2�����ܱȽ�
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
	 * cell��������ͬY����
	 */
	private void cellSameY() {
		Object[] selectCells = this.graph.getSelectionCells();//�õ�ѡ�еĵ�Ԫ
		if (selectCells != null && selectCells.length > 1) {//����2�����ܱȽ�
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
	 * cell��������ͬ�߶�
	 */
	private void cellSameH() {
		Object[] selectCells = this.graph.getSelectionCells();//�õ�ѡ�еĵ�Ԫ
		if (selectCells != null && selectCells.length > 1) {//����2�����ܱȽ�
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
	 * cell��������ͬ���
	 */
	private void cellSameW() {
		Object[] selectCells = this.graph.getSelectionCells();//�õ�ѡ�еĵ�Ԫ
		if (selectCells != null && selectCells.length > 1) {//����2�����ܱȽ�
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

	//���ݰ��������Ҽ��������һ���߼�ͷ���͡�[����2012-04-28]
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
				if (obj instanceof DefaultGraphCell) { // �����Ĭ�Ͻ��
					DefaultGraphCell cell = (DefaultGraphCell) obj; //
					if (objs.length == 1 && cell instanceof DefaultEdge) { //���ѡ������ߣ������ҷ������ı����ͷ��״��[����2012-04-28]
						//�����
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
						isEditChanged = true; //��ʾ�Ѿ��仯��
					}
					if (cell.getChildCount() == 0)//��ǿ�޸�
						continue;
					Rectangle2D rec = GraphConstants.getBounds(cell.getAttributes()); //
					if (rec != null) {
						double ld_x = rec.getX();
						double ld_y = rec.getY();
						double ld_x_center = rec.getCenterX();
						double ld_y_center = rec.getCenterY();
						//�򻷽�����ʱ����ʱ����ô�����߶�����ֱ�ģ��ʽ��仯��λ���ȸ�Ϊ1����ǰΪ2!
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
						isEditChanged = true; //��ʾ�Ѿ��仯��
					}
					e.consume();//��sfj/20120301/��ֹ�÷�����ƶ�����ʱ���������Ĺ�����
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
					Thread.currentThread().sleep(100); //�Ȼ�,����̫��,û�о�!!!
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
	 * ͼ����ѡ����仯
	 */
	public void valueChanged(GraphSelectionEvent e) {
		Object obj = e.getCell(); //
		if (obj instanceof DefaultGraphCell) {
			DefaultGraphCell cell = (DefaultGraphCell) obj; //
			Object linkedObject = cell.getUserObject(); //
			if (linkedObject instanceof ActivityVO) {
				ActivityVO vo = (ActivityVO) linkedObject;
				if (this.showtoolbox) {//�ж��Ƿ���ʾ������
					ActivityBean bean = (ActivityBean) getActivityPropPanel().getBeanInstance(); //
					copyActivityDataFromVoToBean(vo, bean);
					getActivityPropPanel().reload(); //
					switchToActivityPropPanel();
				}
				callAllSelectListener(vo); //���ӻ���ѡ���¼�
			} else if (linkedObject instanceof GroupVO) {
				if (this.showtoolbox) {//�ж��Ƿ���ʾ������
					switchToBlankPropPanel();
				}
				callAllSelectListener(null); //��ǰһֱ��ز���ѡ���ŵ��¼�����������һ�£��������¼��������жϵ���ѡ�е��ǻ��ڻ��ǲ���
			} else if (this.showtoolbox) {//��Ҫ���ж��Ƿ���ʾ�����䣬�����жϵ�����Ƿ�������
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
		} else if (this.showtoolbox) {//�����ʾ�������ִ��
			switchToBlankPropPanel();
		}
	}

	/**
	 * ѡ�����仯...
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
	 * �༭�ұߵ����Կ��е�����ֵ�����仯
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
				copyActivityDataFromBeanToVo(bean, vo); // ��bean�е����ݸ�ֵ��VO��ȥ!!!
				graph.getGraphLayoutCache().reload(); // һ��Ҫִ��,�������ֶ���Ŀ�!!
				// graph.updateUI(); //
				// graph.setUI(new MyGraphUI()); // ��������UI
				graph.repaint();
				graph.invalidate(); //
				// graph.invalidate(); //
			}

		} else if (_evt.getSource() == getTransitionPropPanel()) {
			DefaultEdge transition = getSelectedTransition();
			if (transition != null) {
				TransitionBean bean = (TransitionBean) _evt.getBeanInstance(); //
				TransitionVO vo = (TransitionVO) transition.getUserObject(); //
				copyTransitionDataFromBeanToVo(bean, vo); // ��bean�е����ݸ�ֵ��VO��ȥ!!!
				rePaintLineStyle(transition); // ���»�һ����ɫ!
				graph.getGraphLayoutCache().reload(); // һ��Ҫִ��,�������ֶ���Ŀ�!!
				// graph.updateUI(); //
				// graph.setUI(new MyGraphUI()); // ��������UI
				graph.repaint();
				graph.invalidate(); //
			}
		} else { // 
			//			DefaultEdge transition = getSelectedTransition();
			//			if (transition != null) {
			//				TransitionBean bean = (TransitionBean) _evt.getBeanInstance(); //
			//				TransitionVO vo = (TransitionVO) transition.getUserObject(); //
			//				copyTransitionDataFromBeanToVo(bean, vo); // ��bean�е����ݸ�ֵ��VO��ȥ!!!
			//				rePaintLineStyle(transition); // ���»�һ����ɫ!
			//				graph.getGraphLayoutCache().reload(); // һ��Ҫִ��,�������ֶ���Ŀ�!!
			//				// graph.updateUI(); //
			//				// graph.setUI(new MyGraphUI()); // ��������UI
			//				graph.repaint();
			//				graph.invalidate(); //
			//			}
		}
	}

	/**
	 * ���»�����ɫ
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
	 * ������Bean�е����ݸ��Ƶ�VO��ȥ!!!��������Ա༭���б༭ĳ�����ݺ󴥷�
	 * 
	 * @param _bean
	 * @param _vo
	 */
	private void copyProcessDataFromBeanToVo(ProcessBean _bean, ProcessVO _vo) {
		_vo.setCode(_bean.getCode()); //
		_vo.setName(_bean.getName()); //
		_vo.setWfeguiintercept(_bean.getWfeguiintercept()); //UI��ͳһ��������!!
		_vo.setWfegbsintercept(_bean.getWfegbsintercept()); //BS��ͳһ��������!!

		_vo.setUserdef01(_bean.getUserdef01()); //
		_vo.setUserdef02(_bean.getUserdef02()); //
		_vo.setUserdef03(_bean.getUserdef03()); //
		_vo.setUserdef04(_bean.getUserdef04()); //�Զ�����4
	}

	/**
	 * ������VO�е����ݸ��Ƶ�Bean��ȥ!!!��������Ա༭���б༭ĳ�����ݺ󴥷�
	 * 
	 * @param _bean
	 * @param _vo
	 */
	private void copyProcessDataFromVoToBean(ProcessVO _vo, ProcessBean _bean) {
		_bean.setCode(_vo.getCode()); //
		_bean.setName(_vo.getName()); //
		_bean.setWfeguiintercept(_vo.getWfeguiintercept()); //UI��ͳһ��������!!
		_bean.setWfegbsintercept(_vo.getWfegbsintercept()); //BS��ͳһ��������!!

		_bean.setUserdef01(_vo.getUserdef01()); //
		_bean.setUserdef02(_vo.getUserdef02()); //
		_bean.setUserdef03(_vo.getUserdef03()); //
		_bean.setUserdef04(_vo.getUserdef04()); //�Զ�����4
	}

	/**
	 * ������Bean�е����ݸ��Ƶ�VO��ȥ!!!���ұ����Ա༭���б༭ĳ�����ݺ󴥷�
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
		_vo.setHalfstartdepttype(_bean.getHalfstartdepttype() == null ? null : _bean.getHalfstartdepttype().getId()); // ���������Ļ�������.. gaofeng

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

		if (_bean.getIsselfcycle() != null && _bean.getIsselfcycle().booleanValue()) { //�Ƿ������ѭ��!
			_vo.setIsselfcycle("Y"); //
		} else {
			_vo.setIsselfcycle("N"); //
		}

		_vo.setSelfcyclerolemap(_bean.getSelfcyclerolemap()); //��ѭ����Ӧ�Ľ�ɫӳ��

		_vo.setIscanlookidea(_bean.getIscanlookidea()); //
		_vo.setApprovemodel(_bean.getApprovemodel());
		_vo.setApprovenumber(_bean.getApprovenumber()); //
		_vo.setChildwfcode(_bean.getChildwfcode()); //���������̱���!!

		_vo.setShowparticimode(_bean.getShowparticimode()); //
		_vo.setParticipate_user(_bean.getParticipate_user() == null ? null : _bean.getParticipate_user().getId()); // �û�,��;
		_vo.setParticipate_group(_bean.getParticipate_group() == null ? null : _bean.getParticipate_group().getId()); // ������,��";"

		_vo.setParticipate_corp(_bean.getParticipate_corp()); //������Χ����,ʵ�����Ǹ��ݹ�ʽ���������

		_vo.setCcto_user(_bean.getCcto_user() == null ? null : _bean.getCcto_user().getId()); //������Ա!!!
		_vo.setCcto_corp(_bean.getCcto_corp()); //���ͻ���!!!
		_vo.setCcto_role(_bean.getCcto_role() == null ? null : _bean.getCcto_role().getId()); //���ͽ�ɫ!!!

		_vo.setParticipate_dynamic(_bean.getParticipate_dynamic() == null ? null : _bean.getParticipate_dynamic().getId()); ////
		_vo.setMessageformat(_bean.getMessageformat());
		_vo.setMessagereceiver(addPrefix(_bean.getMessagereceiver()));

		_vo.setIntercept1(_bean.getIntercept1()); // ������1
		_vo.setIntercept2(_bean.getIntercept2()); // ������2

		_vo.setUserdef01(_bean.getUserdef01()); //
		_vo.setUserdef02(_bean.getUserdef02()); //
		_vo.setUserdef03(_bean.getUserdef03()); //
		_vo.setUserdef04(_bean.getUserdef04()); //
		_vo.setExtconfmap(_bean.getExtconfmap());
		isEditChanged = true; //
	}

	/**
	 * ������VO�е����ݸ��Ƶ�Bean��ȥ,��ͼ��ѡ��ĳ�����ʱ����
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

		_bean.setHalfstartrole(_vo.getHalfstartrole() == null ? null : (new RefItemVO(_vo.getHalfstartrole(), null, _vo.getHalfstartrole()))); //��;�����Ľ�ɫ!!
		_bean.setHalfstartdepttype(_vo.getHalfstartdepttype() == null ? null : (new RefItemVO(_vo.getHalfstartdepttype(), null, _vo.getHalfstartdepttype()))); //��;�����Ļ�������!!  gaofeng

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

		if (_vo.getIsselfcycle() != null && _vo.getIsselfcycle().equals("Y")) { //�Ƿ���ѭ��!
			_bean.setIsselfcycle(Boolean.TRUE); //
		} else {
			_bean.setIsselfcycle(Boolean.FALSE); //
		}

		_bean.setSelfcyclerolemap(_vo.getSelfcyclerolemap()); //��ѭ���Ľ�ɫӳ��!

		_bean.setApprovemodel(_vo.getApprovemodel());
		_bean.setApprovenumber(_vo.getApprovenumber());
		_bean.setChildwfcode(_vo.getChildwfcode()); //���������̱���!!!

		_bean.setShowparticimode(_vo.getShowparticimode()); // ��ʾ������ģʽ.
		_bean.setParticipate_user(_vo.getParticipate_user() == null ? null : (new RefItemVO(_vo.getParticipate_user(), null, _vo.getParticipate_user()))); // ���ò�����,ȥ��ǰ�
		_bean.setParticipate_group(_vo.getParticipate_group() == null ? null : (new RefItemVO(_vo.getParticipate_group(), null, _vo.getParticipate_group()))); //��̬��ɫ/��

		_bean.setParticipate_corp(_vo.getParticipate_corp()); //���ò���Ļ���!!

		_bean.setCcto_user(_vo.getCcto_user() == null ? null : (new RefItemVO(_vo.getCcto_user(), null, _vo.getCcto_user()))); //������Ա!
		_bean.setCcto_corp(_vo.getCcto_corp()); //���ͻ���!
		_bean.setCcto_role(_vo.getCcto_role() == null ? null : (new RefItemVO(_vo.getCcto_role(), null, _vo.getCcto_role()))); //���ͽ�ɫ!

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
	 * ������Bean�е����ݸ��Ƶ�VO��ȥ,���ұ����Ա༭���б༭ĳ�����ݺ󴥷�
	 * 
	 * @param _bean
	 * @param _vo
	 */
	private void copyTransitionDataFromBeanToVo(TransitionBean _bean, TransitionVO _vo) {
		_vo.setCode(_bean.getCode()); //
		_vo.setUiname(_bean.getUiname()); //
		_vo.setWfname(_bean.getWfname()); //
		_vo.setDealtype(_bean.getDealtype()); // ��������,�����ύ��ܾ�֮��
		_vo.setMailsubject(_bean.getMailsubject()); // �ʼ�����
		_vo.setMailcontent(_bean.getMailcontent()); // �ʼ�����
		_vo.setFromactivity(_bean.getFromactivity()); //
		_vo.setToactivity(_bean.getToactivity()); //

		_vo.setCondition(_bean.getConditions()); //
		_vo.setReasoncodesql(_bean.getReasoncodesql()); //
		_vo.setIntercept(_bean.getIntercept()); //

		isEditChanged = true; //
	}

	/**
	 * ������VO�е����ݸ��Ƶ�Bean��ȥ,��ͼ��ѡ��ĳ������ʱ����
	 * 
	 * @param _bean
	 * @param _vo
	 */
	private void copyTransitionDataFromVoToBean(TransitionVO _vo, TransitionBean _bean) {
		_bean.setCode(_vo.getCode()); //
		_bean.setUiname(_vo.getUiname()); //
		_bean.setWfname(_vo.getWfname()); //
		_bean.setDealtype(_vo.getDealtype()); //
		_bean.setMailsubject(_vo.getMailsubject()); // �ʼ�����
		_bean.setMailcontent(_vo.getMailcontent()); // �ʼ�����
		_bean.setFromactivity(_vo.getFromactivity()); //
		_bean.setToactivity(_vo.getToactivity()); //
		_bean.setConditions(_vo.getCondition()); //
		_bean.setReasoncodesql(_vo.getReasoncodesql()); // //
		_bean.setIntercept(_vo.getIntercept()); // ������
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
	 * ����ʦ�������Ҫ���ѡ�е����л��ڵ���Ϣ�������Ӹ÷��������/2012-10-25��
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
				if (cell.getUserObject() instanceof ActivityVO) {//�ж��Ƿ��ǻ��ڣ�����ǲż��롾���/2012-10-25��
					arrayList.add(cell);
				}
			}
		}
		if (arrayList.size() == 0) {//���û���ҵ�ֱ�ӷ��ء����/2012-10-25��
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
	 * ȡ��ѡ�еĽ�����ݶ���
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
	 * ����ʦ�������Ҫ���ѡ�е����л��ڵ���Ϣ�������Ӹ÷��������/2012-10-25��
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
	 * ����һ�����ƵĽ��
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
		if (viewClass != null && viewClass.endsWith("8")) {//ͼƬ���ڲ���Ҫ�߿�ֻ�����̼��ʱ�������
			GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createEmptyBorder());
		}
		// } else {
		// GraphConstants.setBorderColor(cell.getAttributes(), Color.black);
		// }

		// GraphConstants.setGradientColor(cell.getAttributes(), new Color(102,
		// 255, 102));
		// GraphConstants.setEditable(cell.getAttributes(), true); //

		if (_isaddPort) {
			cell.addPort(); // ����һ�����ӵ�,��������һ��,����Ͳ�������,���������,����ͼ�ε������һ����ɫ��С����!!�����Port
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
		transitionVO.setId(getNewTransitionID()); // �ȴ���������,�ؼ�,��ǰ�Ļ��Ʋ���������
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
		transitionVO.setPoints(points); // �����۵�!!!!

		// GraphConstants.setPoints(edge.getAttributes(),
		// convertToPoint2DList(points));
		transitionVO.setFromactivity(sourceActivityVO.getId());
		transitionVO.setToactivity(targetActivityVO.getId());
		transitionVO.setCondition(""); //

		DefaultEdge edge = new DefaultEdge(transitionVO); // ��������
		edge.setSource(source); // ��ʼλ��
		edge.setTarget(target); // ����λ��

		GraphConstants.setLabelAlongEdge(edge.getAttributes(), true); //���ΪTrue������б�ŵ�������
		GraphConstants.setEndFill(edge.getAttributes(), true);//���߼�ͷ���
		//GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_TECHNICAL);//���߼�ͷΪ����
		GraphConstants.setLineEnd(edge.getAttributes(), transitionVO.getLineType()); //��sunfujun/20120426/���Ӽ�ͷ���͵�����_���֡�
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

		GraphConstants.setDisconnectable(edge.getAttributes(), false); // ��Զճס,���ܶϿ�!!
		GraphConstants.setPoints(edge.getAttributes(), convertToPoint2DList(points)); //�����۵�!!!�ǳ��ؼ�,�������ڴ��м�¼��λ��!

		// Insert the Edge and its Attributes
		graph.getGraphLayoutCache().insert(edge);//
		//graph.getGraphLayoutCache().insertEdge(edge, source, target);//
		isEditChanged = true; //
		// }
	}

	//��ť����¼�!!
	class ToolBarBtnAction extends AbstractAction {
		ToolBarBtnAction(String _text, Icon _icon) {
			super(_text, _icon); //
		}

		public void actionPerformed(ActionEvent e) {
			onClickToolBarBtn(e); //
		}
	}

	class MyDraggableButton extends JButton implements DragGestureListener, DragSourceListener, DragSourceMotionListener {//��sfj/2012-02-29/�޸��϶���ť���ӻ��ڵ��µ��������⡿��sfj/2012-03-01/�Ż��϶��Ļ����е�����
		DragSource dragSource;
		private BufferedImage baseImg = null; //ÿ���϶�ʱ���������������

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
			comp.setEnabled(false);//ȥ�����°�ť��Ч��
			comp.setEnabled(true);
			Point point = comp.getLocation();
			SwingUtilities.convertPointFromScreen(point, graph);//��sfj/2012-02-23/�Ŵ��µ��϶����⡿
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
			getGraph().getGraphLayoutCache().toFront(new DefaultGraphCell[] { cell });//��sfj/2012-02-29/ֱ��������Ͳ�����ֶ����ڵ�ʱ����ܵ����źͽ׶κ��桿
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
			graphui.paint(baseImg_g2, graph);//������ͼ����ͼƬ��
			baseImg_g2.scale(graph.getScale(), graph.getScale());//�������㻷�ڲ��ܷŴ�����С
			graphui.paintCell(baseImg_g2, graph.getGraphLayoutCache().getMapping(cell, true), new Rectangle2D.Double(point.getX() / graph.getScale() - 0.5 * rec.getWidth(), point.getY() / graph.getScale() - 0.5 * rec.getHeight(), 70, 45), false);//���϶��Ļ��ڻ���ͼƬ��
			baseImg_g2.dispose();
			graph_g2.drawImage(baseImg, 0, 0, null);//�����õ�ͼƬ��������ͼ�ϣ��Ͳ�������
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
			setCloneable(false); // ��������!
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
	 * ����ҪҲ��ӵ�һ����!!!
	 * ����,���ߵ��߼�����������!!!
	 * @author xch
	 */
	public class MyMarqueeHandler extends BasicMarqueeHandler {

		// Holds the Start and the Current Point
		protected Point2D startPoint, currentPoint; // λ��,��ס��ʼλ���뵱ǰλ��

		// Holds the First and the Current Port
		protected PortView firstPort, currentPort; //

		private double li_lastX, li_lastY = -1;

		private DefaultGraphCell firstCell, secondCell = null; // �������!!

		// // Override to Gain Control (for PopupMenu and ConnectMode)
		public boolean isForceMarqueeEvent(MouseEvent e) {
			//System.out.println("������isForceMarqueeEvent...."); //
			// if (e.isShiftDown())
			// return false;
			// // If Right Mouse Button we want to Display the PopupMenu
			// if (SwingUtilities.isRightMouseButton(e))
			// // Return Immediately
			// return true;
			// // Find and Remember Port
			// port = getSourcePortAt(e.getPoint());
			// //ȡ��һ�����ӵ�!!!�ǳ��ؼ�,����������getSourcePortAt()���������ʱ����������,����ס�϶�ʱ,�����������! ������Ϊ�Ǹ�Port���м�,���Ѳ���!!!
			// // If Port Found and in ConnectMode (=Ports Visible)
			// if (port != null && graph.isPortsVisible())
			// return true;
			// // Else Call Superclass
			// return super.isForceMarqueeEvent(e);

			if (e.isControlDown()) { // ����ǰ�ס��Ctrl��
				//				currentPort = null; //
				//				Object obj = graph.getFirstCellForLocation(e.getPoint().getX(), e.getPoint().getY()); //
				//				if (obj != null && obj instanceof DefaultGraphCell) {
				//					DefaultGraphCell cell = (DefaultGraphCell) obj; //
				//					currentPort = graph.getDefaultPortForCell(cell); //
				//				}
				currentPort = getTargetPortAt(e.getPoint()); // ��ǰʹ��getSourcePortAt()ʱ������������,��Ϊ�����������������ж�����ӵ�ʱ��������!!!��getTargetPortAt()�������ҵ�һ��Port,�������ǳɹ�!!!!
				if (currentPort != null) { // ����ɹ��ҵ�,�򷵻�True
					graph.setCursor(new Cursor(Cursor.HAND_CURSOR)); //
					return true;
				} else {
					graph.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
					return false;
				}
			} else { // ������סCtrl��ʱ,����������mousePressed�¼�!
				currentPort = null;
				graph.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
				return false;
			}
		}

		/**
		 * ���������°�ʱ!!!!
		 */
		// Display PopupMenu or Remember Start Location and First Port
		public void mousePressed(final MouseEvent e) {
			// If Right Mouse Button
			if (SwingUtilities.isRightMouseButton(e)) { // ������Ҽ�
				super.mousePressed(e);
			} else if (currentPort != null && graph.isPortsVisible()) {
				startPoint = graph.toScreen(currentPort.getLocation()); // Remember
				// System.out.println("�ҵ�������λ��" + startPoint + ",���ɹ�ΪstartPoint��ֵ!!"); //Start Location
				firstPort = currentPort; // Remember First Port
			} else {
				super.mousePressed(e); // Call Superclass
			}
		}

		// // Find Port under Mouse and Repaint Connector
		/**
		 * �϶�ʱ!!!
		 */
		public void mouseDragged(MouseEvent e) {
			//System.out.println("�϶�..."); //
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
				PortView newPort = getTargetPortAt(e.getPoint()); // �µ����ӵ�Ϊ��ǰ����ƶ�����λ��!!!
				// Do not flicker (repaint only on real changes)
				if (newPort == null || newPort != currentPort) {
					// Xor-Paint the old Connector (Hide old Connector)
					paintConnector(Color.black, graph.getBackground(), g); // �ػ�����,������,����ɵ��߻�ȥ����!

					// If Port was found then Point to Port Location
					currentPort = newPort;
					if (currentPort != null) {
						currentPoint = graph.toScreen(currentPort.getLocation()); // ��������µ����ӵ�,��currentPointָ��Ϊ�µ����ӵ��λ��
						// Else If no Port was found then Point to Mouse Location
					} else {
						currentPoint = graph.snap(e.getPoint()); // ���û�����µ����ӵ�,��currentPointָ��Ϊ��ǰ�ƶ�����λ��!!
					}

					//Xor-Paint the new Connector...
					paintConnector(graph.getBackground(), Color.black, g); // �ػ�����!!!
				} else {
					if (newPort == currentPort) { //������������???
						// System.out.println("�µ����ӵ���ڵ�ǰ���ӵ�!!");
						// //һ������Ŀ�껷��,�����ƶ�ʱ�����κδ���!!
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
			//System.out.println("�ɿ������..."); //
			// If Valid Event, Current and First Port
			if (e != null && currentPort != null && firstPort != null && firstPort != currentPort) { // ���ͷ�ʱ,���������㶼��,��������������!!!
				// Then Establish Connection
				connect((Port) firstPort.getCell(), (Port) currentPort.getCell(), al_draggingPortXY); //���������������!!!!!������Ҫ!!!
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
			//System.out.println("�ƶ�...");  //
			graph.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); //
			//			PortView tempPortView = getTargetPortAt(e.getPoint()); // ��ǰʹ��getSourcePortAt()ʱ������������,��Ϊ�����������������ж�����ӵ�ʱ��������!!!��getTargetPortAt()�������ҵ�һ��Port,�������ǳɹ�!!!!
			//			if (tempPortView != null) { // ����ɹ��ҵ�,�򷵻�True
			//				System.out.println("�ƶ��ҵ���...");  //
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
				if (al_draggingPortXY.size() == 0) { //���û��ϵ�
					g.drawLine((int) startPoint.getX(), (int) startPoint.getY(), (int) currentPoint.getX(), (int) currentPoint.getY()); //���ǻ�һ����!!! ���Կ����㷨,�Զ�������!!
				} else {
					int[] li_xy_1 = (int[]) al_draggingPortXY.get(0); //
					g.drawLine((int) startPoint.getX(), (int) startPoint.getY(), li_xy_1[0], li_xy_1[1]); //���ǻ�һ����!!! ���Կ����㷨,�Զ�������!!

					for (int i = 1; i < al_draggingPortXY.size(); i++) {
						int[] li_xy_a = (int[]) al_draggingPortXY.get(i - 1); //
						int[] li_xy_b = (int[]) al_draggingPortXY.get(i); //
						g.drawLine(li_xy_a[0], li_xy_a[1], li_xy_b[0], li_xy_b[1]); //���ǻ�һ����!!! ���Կ����㷨,�Զ�������!!
					}

					int[] li_xy_2 = (int[]) al_draggingPortXY.get(al_draggingPortXY.size() - 1); //
					g.drawLine(li_xy_2[0], li_xy_2[1], (int) currentPoint.getX(), (int) currentPoint.getY()); //���ǻ�һ����!!! ���Կ����㷨,�Զ�������!
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
				r.setFrame(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r.getHeight() + 6); //��Ŀ�����Χ��������ı߿�,�Ա�ʾ������!!!
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
						if (!isDragging) { //�����һ�ξͼ�¼���Ƿ����϶�״̬,��������Ͳ���������,�������!!
							isDragging = true; //��¼��ʾ�������϶��е�״̬!!!
							Object[] objs = graph.getSelectionCells();
							for (Object selobj : objs) {
								if (selobj instanceof DefaultGraphCell) {
									DefaultGraphCell cell = (DefaultGraphCell) selobj; //
									if (cell.getUserObject() instanceof GroupVO) { //
										if (isDraggDept && isDraggDept) {
											return;
										}
										isDraggGroup = true; //��������϶�����,���¼��!
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
					public void mouseReleased(MouseEvent event) {//��sunfujun/20120419/�Ż�undo���⡿
						isDragging = false; //
						if (isDraggGroup) { //����϶�����!!!
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
						if (event.getButton() == MouseEvent.BUTTON3) { // �����������Ҽ�
							event.consume(); //�����һ�����,����ڶ����һ��� ʱ���ܵ�����ľ�����ȥ!
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
	 * �û�VO
	 * 
	 * @author xch
	 * 
	 */
	class USERVO extends AbstractTMO {
		private static final long serialVersionUID = 8057184541083294474L;

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "USER_VO"); // ģ�����,��������޸�
			vo.setAttributeValue("templetname", "�û�"); // ģ������
			vo.setAttributeValue("tablename", "pub_user"); // ��ѯ���ݵı�(��ͼ)��
			vo.setAttributeValue("pkname", null); // ������
			vo.setAttributeValue("pksequencename", null); // ������
			vo.setAttributeValue("savedtablename", null); // �������ݵı���
			vo.setAttributeValue("listcustpanel", null); // �б��Զ������
			vo.setAttributeValue("cardcustpanel", null); // ��Ƭ�Զ������
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "CODE"); // Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "����"); // ��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); // ��������
			itemVO.setAttributeValue("refdesc", null); // ���ն���
			itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
			itemVO.setAttributeValue("editformula", null); // �༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "100"); // �б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); // �б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "NAME"); // Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "����"); // ��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); // ��������
			itemVO.setAttributeValue("refdesc", null); // ���ն���
			itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
			itemVO.setAttributeValue("editformula", null); // �༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "100"); // �б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			vector.add(itemVO);

			return (HashVO[]) vector.toArray(new HashVO[0]);
		}
	}

	/**
	 * �û�VO
	 * 
	 * @author xch
	 * 
	 */
	class ROLEVO extends AbstractTMO {
		private static final long serialVersionUID = 8057184541083294474L;

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "ROLE_VO"); // ģ�����,��������޸�
			vo.setAttributeValue("templetname", "��ɫ"); // ģ������
			vo.setAttributeValue("tablename", "pub_role"); // ��ѯ���ݵı�(��ͼ)��
			vo.setAttributeValue("pkname", null); // ������
			vo.setAttributeValue("pksequencename", null); // ������
			vo.setAttributeValue("savedtablename", null); // �������ݵı���
			vo.setAttributeValue("listcustpanel", null); // �б��Զ������
			vo.setAttributeValue("cardcustpanel", null); // ��Ƭ�Զ������
			return vo;
		}

		public HashVO[] getPub_templet_1_itemData() {
			Vector vector = new Vector();
			HashVO itemVO = null;

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "CODE"); // Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "����"); // ��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); // ��������
			itemVO.setAttributeValue("refdesc", null); // ���ն���
			itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
			itemVO.setAttributeValue("editformula", null); // �༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "100"); // �б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "N"); // �б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			vector.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "NAME"); // Ψһ��ʶ,����ȡ���뱣��
			itemVO.setAttributeValue("itemname", "����"); // ��ʾ����
			itemVO.setAttributeValue("itemtype", "�ı���"); // �ؼ�����
			itemVO.setAttributeValue("comboxdesc", null); // ��������
			itemVO.setAttributeValue("refdesc", null); // ���ն���
			itemVO.setAttributeValue("issave", "N"); // �Ƿ���뱣��(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-���ٲ�ѯ;2-ͨ�ò�ѯ;3-�������ѯ
			itemVO.setAttributeValue("ismustinput", "N"); // �Ƿ������(Y,N)
			itemVO.setAttributeValue("loadformula", null); // ���ع�ʽ
			itemVO.setAttributeValue("editformula", null); // �༭��ʽ
			itemVO.setAttributeValue("defaultvalueformula", null); // Ĭ��ֵ��ʽ
			itemVO.setAttributeValue("listwidth", "100"); // �б��ǿ��
			itemVO.setAttributeValue("cardwidth", "150"); // ��Ƭʱ���
			itemVO.setAttributeValue("listisshowable", "Y"); // �б�ʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); // �б�ʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
			itemVO.setAttributeValue("cardisshowable", "Y"); // ��Ƭʱ�Ƿ���ʾ(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // ��Ƭʱ�Ƿ�ɱ༭,1-ȫ���ɱ༭,2-�������ɱ༭,3-���޸Ŀɱ༭;4-ȫ������
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
	 * ������ӻ����͸�λ�İ�ť
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
	 * �����Ƿ���ʾ����Ĺ�����
	 * @param _showtoolbox
	 */
	public void setShowtoolbar(boolean _showtoolbar) {
		if (this.showtoolbar == _showtoolbar) {
			return;
		}

		this.showtoolbar = _showtoolbar;
		if (showtoolbar) {//�������Ϊ��ʾ������
			if (toolBarPanel == null) {//����ʱû����ʾ������������Ҫ���빤����
				this.add(getNorthToolBar(), BorderLayout.NORTH); // �Ϸ��ǹ�����
			}
			getNorthToolBar().setVisible(true); //
		} else if (toolBarPanel != null) {//�������Ϊ����ʾ�����������ұ����Ѿ���ʾ�ţ�����Ҫ����
			getNorthToolBar().setVisible(false); //
		}
	}

	/**
	 * �����Ƿ���ʾ�������Ժ͹�����
	 * @param _showtoolbox
	 */
	public void setShowtoolbox(boolean _showtoolbox) {
		if (this.showtoolbox == _showtoolbox) {
			return;
		}

		this.showtoolbox = _showtoolbox;

		if (showtoolbox) {//�ж��Ƿ���ʾ�����䣬�����ʾ�Ļ�����Ҫ������ߵ��������Ժ��ұ߹����䣨�������Ժ��������ԣ�
			this.add(getWestPanel(), BorderLayout.WEST); // �������������
			splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			splitPanel.setDividerLocation(9999); //
			splitPanel.setDividerSize(5);
			splitPanel.setResizeWeight(1D); // ��Ļ���ʱ,�ұߵĴ�С����!
			splitPanel.setLeftComponent(getMainGraphPanel()); //
			splitPanel.setRightComponent(getEastPanel()); //
			this.add(splitPanel, BorderLayout.CENTER); // �м���Graph�༭��
			this.add(getEastToolBox(), BorderLayout.EAST); // �ұ��ǻ������Ժ��������ԵĹ�����
		} else {
			this.add(getMainGraphPanel(), BorderLayout.CENTER); //
			if (panel_west != null) {//���ж���ߵ����������Ƿ����
				panel_west.setVisible(false);
			}
			if (toftPanel_2 != null) {//�ұ߻��ڼ����߿�Ƭ���ֵĵײ����
				toftPanel_2.setVisible(false);
			}
			if (toolBar2 != null) {//���ڼ����߹�����
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
		btn_adddept.setToolTipText("����" + groupName);//�����������һ��
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
		this.currentProcessVO.setCode(_processcode);//��������һ�£��������̱�����ֱ�Ϊ�޸�ǰ�ı����ˡ����/2012-03-19��
	}

	/**
	 * ����޸��������Ƶķ�����ͬʱע������ͼ�ϵ���������ҲҪ�޸ĵ��������/2012-03-19��
	 * @param _processname
	 */
	public void setProcessname(String _processname) {
		this.currentProcessVO.setName(_processname);//��������һ�£��������̱�����ֱ�Ϊ�޸�ǰ��������
		String str_title = "��������:" + _processname;
		titleCell.setUserObject(str_title);
		graph.getGraphLayoutCache().insert(titleCell); //������Ҫ������ͼ�������������Ҳ�޸ĵ���
	}

	/**
	 * ���ѡ�����仯�¼������/2012-04-11��
	 */
	public Vector getVector_cellSelectedListener() {
		return vector_cellSelectedListener;
	}
}
