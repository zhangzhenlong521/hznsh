/**************************************************************************
 * $RCSfile: WorkFlowEditFrame.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:18:02 $
 **************************************************************************/
package cn.com.infostrategy.ui.workflow.design;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;

import org.jgraph.JGraph;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.BasicMarqueeHandler;
import org.jgraph.graph.CellHandle;
import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.Edge;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphContext;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;
import org.jgraph.graph.GraphUndoManager;
import org.jgraph.graph.Port;
import org.jgraph.graph.PortView;

import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.to.workflow.design.ProcessVO;
import cn.com.infostrategy.to.workflow.design.TransitionVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.workflow.WorkFlowServiceIfc;

public class WorkFlowEditFrame extends JFrame implements GraphSelectionListener, KeyListener {

	private static final long serialVersionUID = 6119029864084558630L;

	public static final int STAUS_INIT = 1;

	public static final int STAUS_CREATE = 2;

	public static final int STAUS_SAVED = 3;

	// JGraph instance
	protected JGraph graph;

	JFrame parentFrame = null;

	// Undo Manager
	protected GraphUndoManager undoManager;

	//All kinds of buttons
	//--文件管理按钮
	protected JButton btn_new, btn_open, btn_save, btn_config = null;

	//--图形控制按钮
	protected JButton btn_zoom, btn_zoomIn, btn_zoomOut;

	//--图形编辑按钮
	protected JButton btn_insert, btn_start, btn_end;

	//--图形编辑控制
	protected Action undo, redo, remove, group, ungroup, tofront, toback;

	//编辑按钮
	protected Action cut, copy, paste;

	// cell count that gets put in cell label
	protected int cellCount = -1;

	protected boolean hasStartActivity = false;

	protected boolean hasEndActivity = false;

	protected int edgecount = -1;

	// Status Bar
	protected StatusBarGraphListener statusBar;

	public static boolean isProcessConfig = false;

	private ProcessVO currentProcessVO = null;

	private JPanel graphpanel = null;

	JToolBar toolbar = null, sysbar = null;

	String str_processID = null; //当前内存环节ID!!

	//private WorkFlowTemplateIFC workflowTemplate = null; //和当前平台流程对应的流程模版

	//	private BillVO workflowTemplateVO=null;

	public WorkFlowEditFrame() {
		this.setTitle("工作流编辑"); //..
		this.setSize(1000, 700); //

		//createJGraphEnvironment();
		populateContentPane();
		this.updateButtonStatus(1);
		this.setVisible(true); //
	}

	public void createJGraphEnvironment() {
		graph = createGraph();
		graph.setMarqueeHandler(createMarqueeHandler());
		undoManager = new GraphUndoManager() {
			private static final long serialVersionUID = 2625332172251917315L;

			public void undoableEditHappened(UndoableEditEvent e) {
				super.undoableEditHappened(e);
				updateHistoryButtons();
			}
		};
		installListeners(graph);
	}

	// Hook for subclassers
	protected void populateContentPane() {
		//设置标题
		//		setTitle("工作流编辑器 - (空)");
		//设置大小 --??

		//设置布局管理器
		//		getContentPane().
		setLayout(new BorderLayout());
		//加入系统工具栏
		//getContentPane().
		add(createSysBar(false), BorderLayout.NORTH);
		//加入绘图形工具栏
		//getContentPane().
		add(createToolBar(true), BorderLayout.WEST);

		//中间的图形工作区
		graphpanel = new JPanel();
		graphpanel.setLayout(new BorderLayout());
		//getContentPane().
		add(new JScrollPane(graphpanel), BorderLayout.CENTER);

		//底部的状态栏
		statusBar = createStatusBar();
		//getContentPane().
		add(statusBar, BorderLayout.SOUTH);
	}

	// Hook for subclassers
	protected JGraph createGraph() {
		JGraph graph = new MyGraph(new MyModel());
		graph.getGraphLayoutCache().setFactory(new DefaultCellViewFactory() {

			private static final long serialVersionUID = -1537355545325666349L;

			protected EdgeView createEdgeView(Object cell) {
				return new EdgeView(cell) {
					private static final long serialVersionUID = -5392753398154600008L;

					public CellHandle getHandle(GraphContext context) {
						return new MyEdgeHandle(this, context);
					}
				};
			}
		});

		return graph;
	}

	// Hook for subclassers
	protected void installListeners(JGraph graph) {
		graph.getModel().addUndoableEditListener(undoManager);
		graph.getSelectionModel().addGraphSelectionListener(this);
		graph.addKeyListener(this);
		graph.getModel().addGraphModelListener(statusBar);
		graph.addMouseListener(new MyMouseListener());
	}

	// Hook for subclassers
	protected BasicMarqueeHandler createMarqueeHandler() {
		return new MyMarqueeHandler();
	}

	// Hook for subclassers
	protected void addActivity() {
		cellCount = cellCount + 1;
		//DefaultGraphCell cell = new DefaultGraphCell("环节" + cellCount);

		int li_x = 50 + 70 * cellCount;
		int li_y = 50 + 40 * cellCount;

		DefaultGraphCell cell = createVertex("环节" + cellCount, li_x, li_y, 60, 35, new Color(193, 235, 255), Color.BLACK, true, "gxlu.nova.framework.workflow.ui.JGraphEllipseView");

		ActivityVO activityVO = new ActivityVO();
		activityVO.setCode("WFA" + cellCount); //代码
		activityVO.setWfname("环节" + cellCount); //流程图中的显示名称
		activityVO.setUiname("环节" + cellCount); //单据中显示的名称
		activityVO.setX(new Integer(li_x)); //X
		activityVO.setY(new Integer(li_y)); //Y

		activityVO.setDesc(""); //描述

		cell.getAttributes().put("linked", activityVO); //

		Object selectedActivity = null; //workflowTemplate.getSelectedActivityTemplate();
		if (selectedActivity != null)
			cell.getAttributes().put("activity", selectedActivity);

		// Add one Floating Port
		cell.addPort(); //
		graph.getGraphLayoutCache().insert(cell);
	}

	// Insert a new Edge between source and target
	public void connect(Port source, Port target) {
		// Construct Edge with no label

		DefaultEdge edge = createDefaultEdge();
		if (graph.getModel().acceptsSource(edge, source) && graph.getModel().acceptsTarget(edge, target)) {

			ActivityVO sourceActivityVO = (ActivityVO) ((DefaultGraphCell) graph.getModel().getParent(source)).getAttributes().get("linked");
			ActivityVO targetActivityVO = (ActivityVO) ((DefaultGraphCell) graph.getModel().getParent(target)).getAttributes().get("linked");

			TransitionVO transitionVO = new TransitionVO();
			transitionVO.setCode("WFT" + edgecount);
			transitionVO.setWfname("转移" + edgecount);
			transitionVO.setUiname("转移" + edgecount);

			List points = new ArrayList();
			points.add(new double[] { sourceActivityVO.getX().doubleValue(), sourceActivityVO.getY().doubleValue() });
			points.add(new double[] { targetActivityVO.getX().doubleValue(), targetActivityVO.getY().doubleValue() });
			transitionVO.setPoints(points);
			GraphConstants.setPoints(edge.getAttributes(), convertToPoint2DList(points));

			//			transitionVO.setFromactivity(sourceActivityVO.getCode());
			//			transitionVO.setToactivity(targetActivityVO.getCode());
			transitionVO.setCondition("");
			edge.getAttributes().put("linked", transitionVO);

			GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_SIMPLE); //

			graph.getGraphLayoutCache().insertEdge(edge, source, target);

			vertexRender(getEdgeSource(edge));
			vertexRender(getEdgeTarget(edge));

			graph.getGraphLayoutCache().reload();
		}
	}

	// Hook for subclassers
	protected DefaultEdge createDefaultEdge() {
		edgecount++;
		DefaultEdge edge = new DefaultEdge("转移" + edgecount);
		setEdgeAttributes(edge.getAttributes());
		return edge;
	}

	// Hook for subclassers
	public void setEdgeAttributes(AttributeMap map) {
		GraphConstants.setLineEnd(map, GraphConstants.ARROW_SIMPLE);
		GraphConstants.setLineColor(map, new Color(30, 30, 210));
		Point2D point = new Point2D.Double(GraphConstants.PERMILLE / 2, 0);
		GraphConstants.setLabelPosition(map, point);
	}

	// Create a Group that Contains the Cells
	public void group(Object[] cells) {
		cells = graph.order(cells);
		if (cells != null && cells.length > 0) {
			DefaultGraphCell group = createGroupCell();
			graph.getGraphLayoutCache().insertGroup(group, cells);
		}
	}

	// Hook for subclassers
	protected DefaultGraphCell createGroupCell() {
		return new DefaultGraphCell();
	}

	protected int getCellCount(JGraph graph) {
		Object[] cells = graph.getDescendants(graph.getRoots());
		return cells.length;
	}

	// Ungroup the Groups in Cells and Select the Children
	public void ungroup(Object[] cells) {
		graph.getGraphLayoutCache().ungroup(cells);
	}

	// Determines if a Cell is a Group
	public boolean isGroup(Object cell) {
		// Map the Cell to its View
		CellView view = graph.getGraphLayoutCache().getMapping(cell, false);
		if (view != null)
			return !view.isLeaf();
		return false;
	}

	// Brings the Specified Cells to Front
	public void toFront(Object[] c) {
		graph.getGraphLayoutCache().toFront(c);
	}

	// Sends the Specified Cells to Back
	public void toBack(Object[] c) {
		graph.getGraphLayoutCache().toBack(c);
	}

	// Undo the last Change to the Model or the View
	public void undo() {
		try {
			undoManager.undo(graph.getGraphLayoutCache());
		} catch (Exception ex) {
			System.err.println(ex);
		} finally {
			updateHistoryButtons();
		}
	}

	// Redo the last Change to the Model or the View
	public void redo() {
		try {
			undoManager.redo(graph.getGraphLayoutCache());
		} catch (Exception ex) {
			System.err.println(ex);
		} finally {
			updateHistoryButtons();
		}
	}

	// Update Undo/Redo Button State based on Undo Manager
	protected void updateHistoryButtons() {
		undo.setEnabled(undoManager.canUndo(graph.getGraphLayoutCache()));
		redo.setEnabled(undoManager.canRedo(graph.getGraphLayoutCache()));
	}

	// From GraphSelectionListener Interface
	public void valueChanged(GraphSelectionEvent e) {
		group.setEnabled(graph.getSelectionCount() > 1);
		boolean enabled = !graph.isSelectionEmpty();
		remove.setEnabled(enabled);
		ungroup.setEnabled(enabled);
		tofront.setEnabled(enabled);
		toback.setEnabled(enabled);
		copy.setEnabled(enabled);
		cut.setEnabled(enabled);
	}

	//
	// KeyListener for Delete KeyStroke
	public void keyReleased(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		// Listen for Delete Key Press
		if (e.getKeyCode() == KeyEvent.VK_DELETE) {
			// Execute Remove Action on Delete Key Press
			remove.actionPerformed(null);
		}
	}

	// Custom Graph
	public static class MyGraph extends JGraph {
		private static final long serialVersionUID = 680977975113635585L;

		public MyGraph(GraphModel model) {
			this(model, null);
		}

		public MyGraph(GraphModel model, GraphLayoutCache cache) {
			super(model, cache);
			setPortsVisible(true);
			setGridEnabled(true);
			setGridSize(6);
			setTolerance(2);
			setInvokesStopCellEditing(true);
			setCloneable(true);
			setJumpToDefaultPort(true);
			setEditable(false);
		}

	}

	// Custom Edge Handle
	private static class MyEdgeHandle extends EdgeView.EdgeHandle {

		private static final long serialVersionUID = 6214042328576496289L;

		/**
		 * @param edge
		 * @param ctx
		 */
		public MyEdgeHandle(EdgeView edge, GraphContext ctx) {
			super(edge, ctx);
		}

		// Override Superclass Method
		public boolean isAddPointEvent(MouseEvent event) {
			//return event.isShiftDown();
			return super.isAddPointEvent(event);
		}

		// Override Superclass Method
		public boolean isRemovePointEvent(MouseEvent event) {
			//return event.isShiftDown();
			return super.isRemovePointEvent(event);
		}

	}

	//
	// Custom Model
	private static class MyModel extends DefaultGraphModel {

		private static final long serialVersionUID = -8229609908515476259L;

		// Override Superclass Method
		public boolean acceptsSource(Object edge, Object port) {
			return (((Edge) edge).getTarget() != port);
		}

		// Override Superclass Method
		public boolean acceptsTarget(Object edge, Object port) {
			return (((Edge) edge).getSource() != port);
		}
	}

	// Custom MarqueeHandler
	private class MyMarqueeHandler extends BasicMarqueeHandler {
		protected Point2D start, current;

		protected PortView port, firstPort;

		public boolean isForceMarqueeEvent(MouseEvent e) {
			if (e.isShiftDown())
				return false;

			if (SwingUtilities.isRightMouseButton(e)) {
				return true;
			}

			port = getSourcePortAt(e.getPoint());
			if (port != null && graph.isPortsVisible())
				return true;
			return super.isForceMarqueeEvent(e);
		}

		//Display PopupMenu or Remember Start Location and First Port
		public void mousePressed(final MouseEvent e) {

			if (SwingUtilities.isRightMouseButton(e)) {
				super.mousePressed(e);
			} else if (port != null && graph.isPortsVisible()) {
				start = graph.toScreen(port.getLocation());
				firstPort = port;
			} else {
				super.mousePressed(e);
			}
		}

		// Find Port under Mouse and Repaint Connector
		public void mouseDragged(MouseEvent e) {
			if (start != null) {
				Graphics g = graph.getGraphics();
				PortView newPort = getTargetPortAt(e.getPoint());
				if (newPort == null || newPort != port) {
					paintConnector(Color.black, graph.getBackground(), g);
					port = newPort;
					if (port != null)
						current = graph.toScreen(port.getLocation());
					else
						current = graph.snap(e.getPoint());
					paintConnector(graph.getBackground(), Color.black, g);
				}
			}

			super.mouseDragged(e);
		}

		public PortView getSourcePortAt(Point2D point) {
			graph.setJumpToDefaultPort(false);
			PortView result;
			try {
				result = graph.getPortViewAt(point.getX(), point.getY());
			} finally {
				graph.setJumpToDefaultPort(true);
			}
			return result;
		}

		// Find a Cell at point and Return its first Port as a PortView
		protected PortView getTargetPortAt(Point2D point) {
			return graph.getPortViewAt(point.getX(), point.getY());
		}

		// Connect the First Port and the Current Port in the Graph or Repaint
		public void mouseReleased(MouseEvent e) {
			if (e != null && port != null && firstPort != null && firstPort != port) {
				Port source = (Port) firstPort.getCell();
				Port target = (Port) port.getCell();
				if (target instanceof DefaultPort) {
					DefaultPort p = (DefaultPort) target;
					if (p.getParent().toString().equals("开始")) {
						graph.repaint();
						return;
					}
				}
				if (source instanceof DefaultPort) {
					DefaultPort p = (DefaultPort) source;
					if (p.getParent().toString().equals("结束")) {
						graph.repaint();
						return;
					}
				}
				connect(source, target);
				e.consume();
				super.mouseReleased(e);
			} else
				graph.repaint();
			firstPort = port = null;
			start = current = null;
			super.mouseReleased(e);
		}

		// Show Special Cursor if Over Port
		public void mouseMoved(MouseEvent e) {
			if (e != null && getSourcePortAt(e.getPoint()) != null && graph.isPortsVisible()) {
				graph.setCursor(new Cursor(Cursor.HAND_CURSOR));
				e.consume();
			} else
				super.mouseMoved(e);
		}

		// Use Xor-Mode on Graphics to Paint Connector
		protected void paintConnector(Color fg, Color bg, Graphics g) {
			g.setColor(fg);
			g.setXORMode(bg);
			paintPort(graph.getGraphics());
			if (firstPort != null && start != null && current != null)
				g.drawLine((int) start.getX(), (int) start.getY(), (int) current.getX(), (int) current.getY());
		}

		// Use the Preview Flag to Draw a Highlighted Port
		protected void paintPort(Graphics g) {
			if (port != null) {
				boolean o = (GraphConstants.getOffset(port.getAllAttributes()) != null);
				Rectangle2D r = (o) ? port.getBounds() : port.getParentView().getBounds();
				r = graph.toScreen((Rectangle2D) r.clone());
				r.setFrame(r.getX() - 3, r.getY() - 3, r.getWidth() + 6, r.getHeight() + 6);
				graph.getUI().paintCell(g, port, r, true);
			}
		}

	}

	/**
	 * 增加一个开始结点
	 */
	private void addStart() {
		if (hasStartActivity)
			return;
		hasStartActivity = true;
		int x = 10;
		int y = 10;
		DefaultGraphCell cell = createVertex("开始", x, y, 45, 45, new Color(193, 235, 255), Color.BLACK, true, "gxlu.nova.framework.workflow.ui.JGraphEllipseView");
		ImageIcon startIcon = UIUtil.getImage("workflow/start.gif");
		GraphConstants.setIcon(cell.getAttributes(), startIcon);

		ActivityVO activityVO = new ActivityVO();
		activityVO.setCode("START"); //
		activityVO.setWfname("开始"); //
		activityVO.setUiname("开始"); //
		activityVO.setX(new Integer(x));
		activityVO.setY(new Integer(y));

		activityVO.setDesc("");

		cell.getAttributes().put("linked", activityVO);

		graph.getGraphLayoutCache().insert(cell); // 加入面板

		if (!undo.isEnabled())
			undo.setEnabled(true);
	}

	/**
	 * 增加一个结束结点
	 */
	private void addEnd() {
		if (hasEndActivity)
			return;
		hasEndActivity = true;
		int x = 450;
		int y = 400;

		DefaultGraphCell cell = createVertex("结束", x, y, 45, 45, Color.ORANGE, Color.BLACK, true, "gxlu.nova.framework.workflow.ui.JGraphEllipseView");
		ImageIcon startIcon = UIUtil.getImage("workflow/end.gif");
		GraphConstants.setIcon(cell.getAttributes(), startIcon);

		ActivityVO activityVO = new ActivityVO();
		activityVO.setCode("END"); //
		activityVO.setWfname("结束"); //
		activityVO.setUiname("结束"); //
		activityVO.setX(new Integer(x));
		activityVO.setY(new Integer(y));

		activityVO.setDesc("");

		cell.getAttributes().put("linked", activityVO);

		graph.getGraphLayoutCache().insert(cell); // 加入面板

		if (!undo.isEnabled())
			undo.setEnabled(true);
	}

	/**
	 * <b>创水平工具栏</b>
	 * 包括三组工具栏:
	 * 1."新建","打开","保存","设置流程信息"
	 * 2."恢复原始大小","放大","缩小"
	 * 3."复制","粘贴","剪切","删除"
	 * @return 工具栏
	 */
	private JToolBar createSysBar(boolean isVertical) {
		JToolBar toolbar;
		if (isVertical == true)
			toolbar = new JToolBar(JToolBar.VERTICAL);
		else
			toolbar = new JToolBar(JToolBar.HORIZONTAL);

		toolbar.setFloatable(false);
		toolbar.addSeparator();
		toolbar.addSeparator();
		toolbar.addSeparator();
		toolbar.addSeparator();

		// Group
		ImageIcon newIcon = UIUtil.getImage("workflow/new.gif");
		btn_new = new JButton(newIcon);
		btn_new.setToolTipText("新建");
		btn_new.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onNew();
			}

		});
		toolbar.add(btn_new);

		ImageIcon openIcon = UIUtil.getImage("workflow/open.gif");
		btn_open = new JButton(openIcon);
		btn_open.setToolTipText("加载一个工作流");
		btn_open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOpen();
			}

		});
		toolbar.add(btn_open);

		ImageIcon saveIcon = UIUtil.getImage("workflow/save.gif");
		btn_save = new JButton(saveIcon);
		btn_save.setToolTipText("保存");
		btn_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSave();
			}
		});
		toolbar.add(btn_save);

		ImageIcon configPIcon = UIUtil.getImage("workflow/config.gif");
		btn_config = new JButton(configPIcon);
		btn_config.setToolTipText("设置流程信息");
		btn_config.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//wfprocessinfo = new WFProcessInfo(pkg, process, false, null, parentFrame, null);
			}
		});
		toolbar.add(btn_config);

		toolbar.addSeparator();

		//zoom
		ImageIcon zoomIcon = UIUtil.getImage("workflow/zoom.gif");
		btn_zoom = new JButton(zoomIcon);
		btn_zoom.setToolTipText("恢复原始大小");
		btn_zoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graph.setScale(1.0);
			}
		});
		toolbar.add(btn_zoom);

		//Zoom In
		ImageIcon zoomInIcon = UIUtil.getImage("workflow/zoomin.gif");
		btn_zoomIn = new JButton(zoomInIcon);
		btn_zoomIn.setToolTipText("缩小");
		btn_zoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graph.setScale(1.2 * graph.getScale());
			}
		});
		toolbar.add(btn_zoomIn);

		// Zoom Out
		ImageIcon zoomOutIcon = UIUtil.getImage("workflow/zoomout.gif");
		btn_zoomOut = new JButton(zoomOutIcon);
		btn_zoomOut.setToolTipText("放大");
		btn_zoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graph.setScale(graph.getScale() / 1.2);
			}
		});
		toolbar.add(btn_zoomOut);

		toolbar.addSeparator();
		Action action;

		// Copy
		action = javax.swing.TransferHandler.getCopyAction();
		action.putValue(Action.SMALL_ICON, UIUtil.getImage("workflow/copy.gif"));
		toolbar.add(copy = new EventRedirector(action));

		// Paste
		action = javax.swing.TransferHandler.getPasteAction();
		action.putValue(Action.SMALL_ICON, UIUtil.getImage("workflow/paste.gif"));
		toolbar.add(paste = new EventRedirector(action));

		// Cut
		action = javax.swing.TransferHandler.getCutAction();
		action.putValue(Action.SMALL_ICON, UIUtil.getImage("workflow/cut.gif"));
		toolbar.add(cut = new EventRedirector(action));

		// Remove
		ImageIcon removeIcon = UIUtil.getImage("workflow/delete.gif");
		remove = new AbstractAction("", removeIcon) {
			private static final long serialVersionUID = 488254019573537005L;

			public void actionPerformed(ActionEvent e) {
				deleteVertex();
			}
		};
		remove.setEnabled(false);
		toolbar.add(remove);

		return toolbar;
	}

	/**
	 * <b>创垂直工具栏</b>
	 * 包括四组工具栏:
	 * 1."添加开始结点","添加结束结点","新增环节"
	 * 2."恢复","重做"
	 * 3."放到最前面","放到最后面"
	 * 4."分组","取消分组"
	 * @return 工具栏
	 */
	public JToolBar createToolBar(boolean isVertical) {
		JToolBar toolbar;
		if (isVertical == true)
			toolbar = new JToolBar(JToolBar.VERTICAL);
		else
			toolbar = new JToolBar(JToolBar.HORIZONTAL);

		toolbar.setFloatable(false);

		// Insert
		ImageIcon startIcon = UIUtil.getImage("workflow/start.gif");
		btn_start = new JButton(startIcon);
		btn_start.setToolTipText("添加开始结点");
		btn_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addStart();
			}
		});
		toolbar.add(btn_start);

		ImageIcon endIcon = UIUtil.getImage("workflow/end.gif");
		btn_end = new JButton(endIcon);
		btn_end.setToolTipText("添加结束结点");
		btn_end.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addEnd();
			}
		});
		toolbar.add(btn_end);

		ImageIcon insertIcon = UIUtil.getImage("workflow/insert.gif");
		btn_insert = new JButton(insertIcon);
		btn_insert.setToolTipText("新增环节");
		btn_insert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addActivity(); //增加环节
				if (!undo.isEnabled())
					undo.setEnabled(true);
			}
		});
		toolbar.add(btn_insert);

		// Undo
		toolbar.addSeparator();
		ImageIcon undoIcon = UIUtil.getImage("workflow/undo.gif");
		//		JButton btn_undo = new JButton(undoIcon);
		//		btn_undo.setToolTipText("后退");
		undo = new AbstractAction("", undoIcon) {
			private static final long serialVersionUID = 7363030588038515315L;

			public void actionPerformed(ActionEvent e) {
				undo();
				if (!undo.isEnabled())
					undo.setEnabled(true);
			}
		};
		undo.setEnabled(false);
		//		btn_undo.addActionListener(undo);
		toolbar.add(undo);

		// Redo
		ImageIcon redoIcon = UIUtil.getImage("workflow/redo.gif");
		redo = new AbstractAction("", redoIcon) {
			private static final long serialVersionUID = 3328812331893437483L;

			public void actionPerformed(ActionEvent e) {
				redo();
			}
		};
		redo.setEnabled(false);
		toolbar.add(redo);

		// To Front
		toolbar.addSeparator();
		ImageIcon toFrontIcon = UIUtil.getImage("workflow/tofront.gif");
		tofront = new AbstractAction("", toFrontIcon) {
			private static final long serialVersionUID = -5200250616157676710L;

			public void actionPerformed(ActionEvent e) {
				if (!graph.isSelectionEmpty())
					toFront(graph.getSelectionCells());
			}
		};
		tofront.setEnabled(false);
		toolbar.add(tofront);

		// To Back
		ImageIcon toBackIcon = UIUtil.getImage("workflow/toback.gif");
		toback = new AbstractAction("", toBackIcon) {

			private static final long serialVersionUID = 2136276563216829073L;

			public void actionPerformed(ActionEvent e) {
				if (!graph.isSelectionEmpty())
					toBack(graph.getSelectionCells());
			}
		};
		toback.setEnabled(false);
		toolbar.add(toback);

		// Zoom Std
		toolbar.addSeparator();
		ImageIcon groupIcon = UIUtil.getImage("workflow/group.gif");
		group = new AbstractAction("", groupIcon) {
			private static final long serialVersionUID = 8457932412048908829L;

			public void actionPerformed(ActionEvent e) {
				group(graph.getSelectionCells());
			}
		};
		group.setEnabled(false);
		toolbar.add(group);

		// Ungroup
		ImageIcon ungroupIcon = UIUtil.getImage("workflow/ungroup.gif");
		ungroup = new AbstractAction("", ungroupIcon) {

			private static final long serialVersionUID = 3771820321542397787L;

			public void actionPerformed(ActionEvent e) {
				ungroup(graph.getSelectionCells());
			}
		};
		ungroup.setEnabled(false);
		toolbar.add(ungroup);

		return toolbar;
	}

	/**
	 * @return 返回JGraph对象
	 */
	public JGraph getGraph() {
		return graph;
	}

	/**
	 * @param graph
	 * 			设置表示当前的JGraph对象
	 */
	public void setGraph(JGraph graph) {
		this.graph = graph;
	}

	/**
	 * Create a status bar
	 */
	protected StatusBarGraphListener createStatusBar() {
		return new EdStatusBar();
	}

	/**
	 * 
	 * @return a String representing the version of this application
	 */
	protected String getVersion() {
		return JGraph.VERSION;
	}

	public void onNew() {
		CreateWorkFlowDialog dialog = new CreateWorkFlowDialog(this);
		//		dialog.setVisible(true);
		//
		//		currentProcessVO = dialog.getProcessVO(); ////
		//
		//		//workflowTemplateVO = workflowTemplate.getWorkFlowTemplateVO(dialog.getWorkFlowTemplatePanel());
		//
		//		if (currentProcessVO != null) {
		//			//this.setTitle("工作流编辑器 - " + currentProcessVO.getName() + "(" + currentProcessVO.getCode() + ")");
		//			try {
		//				str_processID = UIUtil.getSequenceNextValByDS(null, "s_pub_wf_process");
		//			} catch (WLTRemoteException e) {
		//				e.printStackTrace();
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			} //取得流程ID
		//
		//			currentProcessVO.setId(Integer.parseInt(str_processID));
		//			hasStartActivity = false;
		//			hasEndActivity = false;
		//			edgecount = -1;
		//			cellCount = -1;
		//
		//			createJGraphEnvironment();
		//			graphpanel.removeAll();
		//			graphpanel.add(graph, BorderLayout.CENTER);
		//			graphpanel.updateUI();
		//
		//			updateButtonStatus(2);
		//		}
	}

	public void onSave() {

		if (graph.getRoots().length == 0) {
			JOptionPane.showMessageDialog(WorkFlowEditFrame.this, "无法保存空信息", "错误", JOptionPane.OK_OPTION);
			return;
		}

		saveGraph();
		updateButtonStatus(3);

	}

	private void saveGraph() {

		GraphModel model = graph.getModel();
		int rootCount = model.getRootCount();

		ArrayList activityVOList = new ArrayList();
		ArrayList transitionVOList = new ArrayList();

		System.out.println("count:" + rootCount);

		//更新顶点
		for (int i = 0; i < rootCount; i++) {
			Object obj = model.getRootAt(i);
			if (obj instanceof DefaultGraphCell) {

				DefaultGraphCell cell = (DefaultGraphCell) obj;
				Object linkedObject = cell.getAttributes().get("linked");

				if (linkedObject instanceof ActivityVO) {
					Rectangle2D point2D = GraphConstants.getBounds(cell.getAttributes()); //取得坐标位置
					int x = new Double(point2D.getX()).intValue();
					int y = new Double(point2D.getY()).intValue();
					//int id = this.getNewActivityID();

					ActivityVO activityVO = (ActivityVO) linkedObject; //类型转换
					//activityVO.setId(id);
					//activityVO.setJoincount(getVertexInCount(cell));
					//activityVO.setSplitcount(getVertexOutCount(cell));
					activityVO.setX(new Integer(x));
					activityVO.setY(new Integer(y));
					activityVOList.add(activityVO);
				}
			}
		}

		//更新边
		for (int i = 0; i < rootCount; i++) {
			Object obj = model.getRootAt(i);
			if (obj instanceof DefaultGraphCell) {
				DefaultGraphCell cell = (DefaultGraphCell) obj;
				Object linkedObject = cell.getAttributes().get("linked");

				if (linkedObject instanceof TransitionVO) {
					updateConnectValue((DefaultEdge) cell);

					TransitionVO transitionVO = (TransitionVO) linkedObject;

					transitionVOList.add(transitionVO);
				}
			}
		}

		//约束检查
		if (checkBeforeSave() == false)
			return;

		System.out.println("");

		currentProcessVO.setActivityVOs((ActivityVO[]) activityVOList.toArray(new ActivityVO[0]));
		currentProcessVO.setTransitionVOs((TransitionVO[]) transitionVOList.toArray(new TransitionVO[0]));

		try {
			WorkFlowServiceIfc wfservice = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class); //Remote Call
			wfservice.saveWFProcess(currentProcessVO, str_processID);

			//			workflowTemplate.saveWorkFlowTemplate(currentProcessVO); //保存工作流模版

			MessageBox.show(this, "Saved successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
		}
	}

	public void onOpen() {
		OpenProcessDialog dialog = new OpenProcessDialog(this);
		//		dialog.setVisible(true); //
		//		str_processID = dialog.getProcessID();
		//		if (str_processID == null) {
		//			return;
		//		}
		//		try {
		//			WorkFlowServiceIfc wfservice = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class); //
		//			currentProcessVO = wfservice.getWFProcess(str_processID); //
		//			//this.setTitle("工作流编辑器 - " + currentProcessVO.getName() + "(" + currentProcessVO.getCode() + ")");
		//			createJGraphEnvironment();
		//			graphpanel.removeAll();
		//			graphpanel.add(graph, BorderLayout.CENTER);
		//			openMainGraph(currentProcessVO);
		//
		//			updateButtonStatus(2);
		//			graphpanel.updateUI();
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}
	}

	public void clearPanel() {
		graphpanel.removeAll();
		graphpanel.updateUI();
		updateButtonStatus(1);
	}

	public void onOpen(String str_processID) {
		//		ChooseProcessDialog dialog = new ChooseProcessDialog(this);
		//		dialog.setVisible(true); //
		//		str_processID = dialog.getProcessID();
		if (str_processID == null) {
			return;
		}
		try {
			WorkFlowServiceIfc wfservice = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class);
			currentProcessVO = wfservice.getWFProcessByWFCode(str_processID); //
			//this.setTitle("工作流编辑器 - " + currentProcessVO.getName() + "(" + currentProcessVO.getCode() + ")");

			//workflowTemplate.loadWorkFlowTemplate(currentProcessVO.getId() + ""); //打开流程模版

			createJGraphEnvironment();
			graphpanel.removeAll();
			graphpanel.add(graph, BorderLayout.CENTER);
			openMainGraph(currentProcessVO);

			updateButtonStatus(2);
			graphpanel.updateUI();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 真正画图的地方
	 * @param _worktickno
	 * @param _processid
	 * @param _tabpos
	 * @return
	 */
	private void openMainGraph(ProcessVO processVO) {
		TransitionVO[] tansitionVOs = processVO.getTransitionVOs(); //得到所有连接

		ActivityVO[] activityVOs = processVO.getActivityVOs();

		Hashtable cellMap = new Hashtable();

		//增加所有普通结点
		for (int i = 0; i < activityVOs.length; i++) {
			DefaultGraphCell cell = null;

			if (activityVOs[i].getCode().equalsIgnoreCase("START")) {
				cell = createVertex(activityVOs[i].getWfname(), activityVOs[i].getX().intValue(), activityVOs[i].getY().intValue(), 45, 45, new Color(193, 235, 255), Color.BLACK, true, "gxlu.nova.framework.workflow.ui.JGraphEllipseView");
				ImageIcon startIcon = UIUtil.getImage("workflow/start.gif");
				GraphConstants.setIcon(cell.getAttributes(), startIcon);
				cell.getAttributes().put("linked", activityVOs[i]);
				//GraphConstants.setGradientColor(cell.getAttributes(), new Color(102, 255, 102));
				graph.getGraphLayoutCache().insert(cell); //加入面板
			} else if (activityVOs[i].getCode().equalsIgnoreCase("END")) {
				cell = createVertex(activityVOs[i].getWfname(), activityVOs[i].getX().intValue(), activityVOs[i].getY().intValue(), 45, 45, Color.ORANGE, Color.BLACK, true, "gxlu.nova.framework.workflow.ui.JGraphEllipseView");
				ImageIcon endIcon = UIUtil.getImage("workflow/end.gif");
				GraphConstants.setIcon(cell.getAttributes(), endIcon);
				cell.getAttributes().put("linked", activityVOs[i]);
				//GraphConstants.setGradientColor(cell.getAttributes(), new Color(102, 255, 102));
				graph.getGraphLayoutCache().insert(cell); //加入面板
			} else {
				cell = createVertex(activityVOs[i].getWfname(), activityVOs[i].getX().intValue(), activityVOs[i].getY().intValue(), unicode_length(activityVOs[i].getWfname()) * 6 + 30, 35, new Color(193, 235, 255), Color.BLACK, true, "gxlu.nova.framework.workflow.ui.JGraphEllipseView");
				cell.getAttributes().put("linked", activityVOs[i]);
				graph.getGraphLayoutCache().insert(cell); //加入面板
			}
			cellMap.put(activityVOs[i].getCode() + "", cell);
		}

		//去掉开始和结束节点
		cellCount = activityVOs.length - 3;

		//增加所有连线
		for (int i = 0; i < tansitionVOs.length; i++) {
			DefaultEdge edge = new DefaultEdge(tansitionVOs[i].getWfname());
			this.setEdgeAttributes(edge.getAttributes());
			edge.getAttributes().put("linked", tansitionVOs[i]);

			//			String fromId = tansitionVOs[i].getFromactivity(); //从...
			//			String toId = tansitionVOs[i].getToactivity(); //去...
			//
			//			DefaultGraphCell fromcell = (DefaultGraphCell) cellMap.get(fromId + "");
			//			DefaultGraphCell tocell = (DefaultGraphCell) cellMap.get(toId + "");
			//
			//			if (fromcell != null && tocell != null) {
			//				edge.setSource(fromcell.getChildAt(0)); //起始位置
			//				edge.setTarget(tocell.getChildAt(0)); //结束位置
			//			}
			//
			//			GraphConstants.setEndFill(edge.getAttributes(), true);
			//			GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_SIMPLE);
			//
			//			//设置连线路径
			//			List pointLs = tansitionVOs[i].getPoints(); //已经包含起始,结束结点
			//			GraphConstants.setPoints(edge.getAttributes(), this.convertToPoint2DList(pointLs));
			//
			//			edgeRender(edge);
			//
			//			graph.getGraphLayoutCache().insert(edge); //加入面板
		}

		edgecount = tansitionVOs.length;
		hasStartActivity = true;
		hasEndActivity = true;

		graph.getGraphLayoutCache().reload();
		graph.clearSelection(); //		
		graph.updateUI();
	}

	/**
	 * 得到所有的开始结点
	 */
	private ActivityVO[] getStartActivityVO(ActivityVO[] activityVO) {
		ArrayList activityVOList = new ArrayList();

		for (int i = 0; i < activityVO.length; i++) {
			ActivityVO _vo = activityVO[i];
			if (_vo.getCode().equalsIgnoreCase("START"))
				activityVOList.add(_vo);
		}

		return (ActivityVO[]) activityVOList.toArray(new ActivityVO[0]);
	}

	/**
	 * 得到结束结点
	 * @param activityVO
	 * @return
	 */
	private ActivityVO[] getEndActivityVO(ActivityVO[] activityVO) {
		ArrayList activityVOList = new ArrayList();

		for (int i = 0; i < activityVO.length; i++) {
			ActivityVO _vo = activityVO[i];
			if (_vo.getCode().equalsIgnoreCase("END"))
				activityVOList.add(_vo);
		}

		return (ActivityVO[]) activityVOList.toArray(new ActivityVO[0]);
	}

	/**
	 * 得到所有的中间结点
	 */
	private ActivityVO[] getActivityVO(ActivityVO[] activityVO) {
		ArrayList activityVOList = new ArrayList();

		for (int i = 0; i < activityVO.length; i++) {
			ActivityVO _vo = activityVO[i];
			if (!_vo.getCode().equalsIgnoreCase("START") && !_vo.getCode().equalsIgnoreCase("END"))
				activityVOList.add(_vo);
		}

		return (ActivityVO[]) activityVOList.toArray(new ActivityVO[0]);
	}

	/**
	 * 创建一个定制的结点
	 */
	public DefaultGraphCell createVertex(Object name, double x, double y, double w, double h, Color bg, Color fore, boolean raised, String viewClass) {
		DefaultGraphCell cell = new DefaultGraphCell(name);

		if (viewClass != null) {
			GPCellViewFactory.setViewClass(cell.getAttributes(), viewClass); //
		}

		GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(x, y, w, h)); //
		if (bg != null) {
			GraphConstants.setBackground(cell.getAttributes(), bg);
			GraphConstants.setFont(cell.getAttributes(), new Font("宋体", Font.PLAIN, 12));
			GraphConstants.setOpaque(cell.getAttributes(), true);
		}

		if (fore != null) {
			GraphConstants.setForeground(cell.getAttributes(), fore);
		}

		// Set raised border
		if (raised) {
			GraphConstants.setBorder(cell.getAttributes(), BorderFactory.createRaisedBevelBorder());
		} else {
			GraphConstants.setBorderColor(cell.getAttributes(), Color.black);
		}

		GraphConstants.setGradientColor(cell.getAttributes(), new Color(102, 255, 102));

		cell.addPort();
		return cell;
	}

	/**
	 * 更新按钮状态
	 */
	public void updateButtonStatus(int sysStatus) {

		//1-系统初始化, 2-新建, 3-保存
		if (sysStatus == 1) { //系统初始化

			//--文件控制按钮
			btn_new.setEnabled(true);
			btn_save.setEnabled(false);
			btn_open.setEnabled(true);
			btn_config.setEnabled(false);

			//--图形控制按钮
			btn_zoom.setEnabled(false);
			btn_zoomIn.setEnabled(false);
			btn_zoomOut.setEnabled(false);

			//--图形编辑按钮
			btn_insert.setEnabled(false);
			btn_start.setEnabled(false);
			btn_end.setEnabled(false);

			//--图形编辑控制
			undo.setEnabled(false);
			redo.setEnabled(false);
			remove.setEnabled(false);
			group.setEnabled(false);
			ungroup.setEnabled(false);
			tofront.setEnabled(false);
			toback.setEnabled(false);

			//编辑按钮
			cut.setEnabled(false);
			copy.setEnabled(false);
			paste.setEnabled(false);
		} else if (sysStatus == 2) { //新建

			//--文件控制按钮
			btn_new.setEnabled(true);
			btn_save.setEnabled(true);
			btn_open.setEnabled(true);
			btn_config.setEnabled(false);

			//--图形控制按钮
			btn_zoom.setEnabled(true);
			btn_zoomIn.setEnabled(true);
			btn_zoomOut.setEnabled(true);

			//--图形编辑按钮
			btn_insert.setEnabled(true);
			btn_start.setEnabled(true);
			btn_end.setEnabled(true);

			//--图形编辑控制
			undo.setEnabled(false);
			redo.setEnabled(false);
			remove.setEnabled(false);
			group.setEnabled(false);
			ungroup.setEnabled(false);
			tofront.setEnabled(false);
			toback.setEnabled(false);

			//编辑按钮
			cut.setEnabled(false);
			copy.setEnabled(false);
			paste.setEnabled(false);
		} else if (sysStatus == 3) { //保存
			btn_save.setEnabled(true);
		}
	}

	/**
	 * 更新边所包含的对象信息
	 * @param edge
	 */
	private void updateConnectValue(DefaultEdge edge) {
		TransitionVO vo_trans = (TransitionVO) edge.getAttributes().get("linked");
		ActivityVO source = (ActivityVO) getEdgeSource(edge).getAttributes().get("linked");
		ActivityVO target = (ActivityVO) getEdgeTarget(edge).getAttributes().get("linked");

		List points = GraphConstants.getPoints(edge.getAttributes());
		vo_trans.setPoints(convertToArrayList(points));

		//		vo_trans.setFromactivity(source.getCode());
		//		vo_trans.setToactivity(target.getCode());
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
	 * 得到从该顶点出发的所有的边
	 * @param vertex
	 * @return
	 */
	private DefaultEdge[] getVertexOutEdges(DefaultGraphCell vertex) {
		ArrayList edgeList = new ArrayList();

		ActivityVO activityVO = (ActivityVO) vertex.getAttributes().get("linked");

		GraphModel model = graph.getModel();
		int totalCount = model.getRootCount();

		for (int i = 0; i < totalCount; i++) {
			DefaultGraphCell cell = (DefaultGraphCell) model.getRootAt(i);
			if (cell instanceof DefaultEdge) {
				TransitionVO edgeVo = (TransitionVO) cell.getAttributes().get("linked");
				if (edgeVo.getFromactivity().equals(activityVO.getCode())) {
					edgeList.add(cell);
				}
			}
		}

		return (DefaultEdge[]) edgeList.toArray(new DefaultEdge[0]);
	}

	/**
	 * 得到从进入该顶点的所有的边
	 * @param vertex
	 * @return
	 */
	private DefaultEdge[] getVertexInEdges(DefaultGraphCell vertex) {
		ArrayList edgeList = new ArrayList();

		ActivityVO activityVO = (ActivityVO) vertex.getAttributes().get("linked");

		GraphModel model = graph.getModel();
		int totalCount = model.getRootCount();

		for (int i = 0; i < totalCount; i++) {
			DefaultGraphCell cell = (DefaultGraphCell) model.getRootAt(i);
			if (cell instanceof DefaultEdge) {
				TransitionVO edgeVo = (TransitionVO) cell.getAttributes().get("linked");
				if (edgeVo.getToactivity().equals(activityVO.getCode())) {
					edgeList.add(cell);
				}
			}
		}
		return (DefaultEdge[]) edgeList.toArray(new DefaultEdge[0]);
	}

	/**
	 * 得到顶点的出度
	 * @param vertex
	 * @return
	 */
	private int getVertexInCount(DefaultGraphCell vertex) {
		int count = 0;

		GraphModel model = graph.getModel();
		int totalCount = model.getRootCount();

		for (int i = 0; i < totalCount; i++) {
			DefaultGraphCell cell = (DefaultGraphCell) graph.getModel().getRootAt(i);
			if (cell instanceof DefaultEdge) {
				DefaultGraphCell target = getEdgeTarget((DefaultEdge) cell);
				if (((ActivityVO) target.getAttributes().get("linked")).getCode().equals(((ActivityVO) vertex.getAttributes().get("linked")).getCode())) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * 得到顶点的入度
	 * @param vertex
	 * @return
	 */
	private int getVertexOutCount(DefaultGraphCell vertex) {
		int count = 0;
		;

		GraphModel model = graph.getModel();
		int totalCount = model.getRootCount();

		for (int i = 0; i < totalCount; i++) {
			DefaultGraphCell cell = (DefaultGraphCell) graph.getModel().getRootAt(i);
			if (cell instanceof DefaultEdge) {
				DefaultGraphCell source = getEdgeSource((DefaultEdge) cell);
				if (((ActivityVO) source.getAttributes().get("linked")).getCode().equals(((ActivityVO) vertex.getAttributes().get("linked")).getCode())) {
					count++;
				}
			}
		}
		return count;
	}

	/**
	 * 删除选中的一个或多个结点
	 */
	private void deleteVertex() {
		ArrayList addiationalDeleteCells = new ArrayList();
		ArrayList readyToRerenderCells = new ArrayList();
		if (!graph.isSelectionEmpty()) {
			Object[] cells = graph.getSelectionCells();

			cells = graph.getDescendants(cells);

			GraphModel model = graph.getModel();
			int totalCount = model.getRootCount();

			for (int i = 0; i < cells.length; i++) {
				//如果删除的是顶点,需要收集关联的边的信息,把相关边一起删除!
				if (((!(cells[i] instanceof DefaultEdge)) && !(cells[i] instanceof DefaultPort)) && (cells[i] instanceof DefaultGraphCell)) {
					ActivityVO vertexVO = (ActivityVO) ((DefaultGraphCell) cells[i]).getAttributes().get("linked");
					for (int j = 0; j < totalCount; j++) {
						DefaultGraphCell cell = (DefaultGraphCell) graph.getModel().getRootAt(j);
						if (cell instanceof DefaultEdge) {
							ActivityVO sourceVO = (ActivityVO) getEdgeSource((DefaultEdge) cell).getAttributes().get("linked");
							ActivityVO targetVO = (ActivityVO) getEdgeTarget((DefaultEdge) cell).getAttributes().get("linked");
							if (sourceVO.getCode().equals(vertexVO.getCode())) { //如果边的起始顶点为待删除顶点
								addiationalDeleteCells.add(cell);
								readyToRerenderCells.add(getEdgeTarget((DefaultEdge) cell));
							} else if (targetVO.getCode().equals(vertexVO.getCode())) {
								addiationalDeleteCells.add(cell);
								readyToRerenderCells.add(getEdgeSource((DefaultEdge) cell));
							}
						}
					}

					if (vertexVO.getCode().equalsIgnoreCase("START"))
						this.hasStartActivity = false;
					else if (vertexVO.getCode().equalsIgnoreCase("END"))
						this.hasEndActivity = false;
				} else if (cells[i] instanceof DefaultEdge) { //如果删除的是边,需要把和边相关联的顶点的出度和入度值分别减一!
					DefaultGraphCell sourceCell = getEdgeSource((DefaultEdge) cells[i]);
					ActivityVO sourceVO = (ActivityVO) sourceCell.getAttributes().get("linked");
					//sourceVO.setSplitcount(sourceVO.getSplitCount() - 1);
					readyToRerenderCells.add(sourceCell);
					DefaultGraphCell targetCell = getEdgeTarget((DefaultEdge) cells[i]);
					ActivityVO targetVO = (ActivityVO) targetCell.getAttributes().get("linked");
					//targetVO.setJoincount(targetVO.getJoinCount() - 1);

					readyToRerenderCells.add(targetCell);
				}
			}

			graph.getModel().remove(cells);
			graph.getModel().remove((Object[]) addiationalDeleteCells.toArray(new Object[0]));

			for (int i = 0; i < readyToRerenderCells.size(); i++) {
				Object cell = readyToRerenderCells.get(i);
				if (cell instanceof DefaultEdge) {
					edgeRender((DefaultEdge) cell);
				} else if (cell instanceof DefaultGraphCell) {
					vertexRender((DefaultGraphCell) cell);
				}
			}

			graph.getGraphLayoutCache().reload();
			if (!undo.isEnabled())
				undo.setEnabled(true);
		}
	}

	/**
	 * 保存前进行合法性校验检查，该校验属于图形级校验
	 * @return
	 */
	private boolean checkBeforeSave() {
		GraphModel model = graph.getModel();
		int cellCount = model.getRootCount();

		ArrayList vertexList = new ArrayList();
		ActivityVO[] vertexes = null;
		ArrayList edgeList = new ArrayList();

		ArrayList errMsg = new ArrayList();

		//得到所有的顶点和边
		for (int i = 0; i < cellCount; i++) {
			Object cell = model.getRootAt(i);
			if (cell instanceof DefaultEdge) {
				TransitionVO edge = (TransitionVO) ((DefaultEdge) cell).getAttributes().get("linked");
				edgeList.add(edge);
			} else if (cell instanceof DefaultGraphCell) {
				ActivityVO vertex = (ActivityVO) ((DefaultGraphCell) cell).getAttributes().get("linked");
				vertexList.add(vertex);
			}
		}

		vertexes = (ActivityVO[]) vertexList.toArray(new ActivityVO[0]);

		vertexList = null;
		edgeList = null;

		//得到开始结点,结束结点和中间结点
		ActivityVO[] startVertex = getStartActivityVO(vertexes);
		ActivityVO[] endVertex = getEndActivityVO(vertexes);
		ActivityVO[] middleVertex = getActivityVO(vertexes);

		//1.检查开始,结束节点
		if (startVertex == null || startVertex.length == 0)
			errMsg.add("缺少开始结点!\n");

		if (endVertex == null || endVertex.length == 0)
			errMsg.add("缺少结束结点!\n");

		//2.检查开始节点是否有输入,是否有输出
		//		if (startVertex != null && startVertex.length != 0 && startVertex[0].getJoinCount() > 0)
		//			errMsg.add("开始结点不能有输入!\n");
		//		if (startVertex != null && startVertex.length != 0 && startVertex[0].getSplitCount() <= 0)
		//			errMsg.add("开始结点必须有输出!\n");

		//3.检查结束节点是否有输出
		//		if (endVertex != null && endVertex.length != 0 && endVertex[0].getSplitCount() > 0)
		//			errMsg.add("结束结点不能有输出!\n");
		//		if (endVertex != null && endVertex.length != 0 && endVertex[0].getJoinCount() <= 0)
		//			errMsg.add("结束结点必须有输入!\n");

		//4.检查是否存在孤立节点
		//		if (middleVertex == null || middleVertex.length == 0) {
		//			errMsg.add("不存在中间结点!\n");
		//		} else {
		//			for (int i = 0; i < middleVertex.length; i++) {
		//				if (middleVertex[i].getJoinCount() <= 0 && middleVertex[i].getSplitCount() <= 0)
		//					errMsg.add("结点\"" + middleVertex[i].getWfname() + "\"不能为孤立结点!\n");
		//				else {
		//					if (middleVertex[i].getJoinCount() <= 0) {
		//						errMsg.add("结点\"" + middleVertex[i].getWfname() + "\"不能没有输入!\n");
		//					} else if (middleVertex[i].getSplitCount() <= 0)
		//						errMsg.add("结点\"" + middleVertex[i].getWfname() + "\"不能没有输出!\n");
		//				}
		//			}
		//		}

		if (errMsg.size() != 0) {
			StringBuffer msg = new StringBuffer("保存出错:\n");
			for (int i = 0; i < errMsg.size(); i++) {
				msg.append((i + 1) + "." + errMsg.get(i));
			}
			JOptionPane.showMessageDialog(this, msg.toString());

			return false;
		}

		return true;
	}

	/**
	 * 根据制定的字符串,算出其字节长度,如果字符串中有个中文字符,那么他的长度就算2
	 * @param s
	 * @return
	 */
	private int unicode_length(String s) {
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
		if (bo_1)
			j = j - 1;
		return j;
	}

	/**
	 * 根据顶点的入度和出度更改相连的连线显示方式
	 * @param vertex
	 */
	private void vertexRender(DefaultGraphCell vertex) {

		DefaultEdge[] inEdges = getVertexInEdges(vertex);
		DefaultEdge[] outEdges = getVertexOutEdges(vertex);

		if (inEdges != null) {
			for (int i = 0; i < inEdges.length; i++) {
				edgeRender(inEdges[i]);
			}
		}

		if (outEdges != null) {
			for (int i = 0; i < outEdges.length; i++) {
				edgeRender(outEdges[i]);
			}
		}
	}

	/**
	 * 根据edge两端的结点的入度和出度更改edge开始和结束端点的显示方式
	 * @param edge
	 */
	private void edgeRender(DefaultEdge edge) {
		ActivityVO sourceVertexVO = (ActivityVO) getEdgeSource(edge).getAttributes().get("linked");
		ActivityVO targetVertexVO = (ActivityVO) getEdgeTarget(edge).getAttributes().get("linked");

		//		if (sourceVertexVO.getSplitCount() == 1) {
		//			GraphConstants.setLineBegin(edge.getAttributes(), GraphConstants.ARROW_NONE);
		//		} else if (sourceVertexVO.getSplitCount() > 1) {
		//			if (sourceVertexVO.getSplityType() != null && sourceVertexVO.getSplityType().equalsIgnoreCase("XOR")) {
		//				GraphConstants.setLineBegin(edge.getAttributes(), GraphConstants.ARROW_CIRCLE);
		//				GraphConstants.setBeginFill(edge.getAttributes(), false);
		//			} else if (sourceVertexVO.getSplityType() != null && sourceVertexVO.getSplityType().equalsIgnoreCase("AND")) {
		//				GraphConstants.setLineBegin(edge.getAttributes(), GraphConstants.ARROW_CIRCLE);
		//				GraphConstants.setBeginFill(edge.getAttributes(), true);
		//			}
		//		}
		//
		//		if (targetVertexVO.getJoinCount() == 1) {
		//			GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_SIMPLE);
		//		} else if (targetVertexVO.getJoinCount() > 1) {
		//			if (targetVertexVO.getJoinType() != null && targetVertexVO.getJoinType().equalsIgnoreCase("XOR")) {
		//				GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_TECHNICAL);
		//				GraphConstants.setEndFill(edge.getAttributes(), false);
		//			} else if (targetVertexVO.getJoinType() != null && targetVertexVO.getJoinType().equalsIgnoreCase("AND")) {
		//				GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_TECHNICAL);
		//				GraphConstants.setEndFill(edge.getAttributes(), true);
		//			}
		//		}
	}

	/**
	 * 把List中的double[]元素转换为对应的Point2D.Double类型
	 * @param list
	 * @return
	 */
	private List convertToPoint2DList(List list) {
		List _list = new ArrayList();
		for (int i = 0; i < list.size(); i++) {
			double[] elem = (double[]) list.get(i);
			Point2D.Double _elem = new Point2D.Double(elem[0], elem[1]);
			_list.add(_elem);
		}

		return _list;
	}

	/**
	 * 把List中的Point2D.Double元素转换为对应的double[]类型
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

	/**
	 * 鼠标监听器
	 * @author Administrator
	 *
	 */
	private class MyMouseListener extends java.awt.event.MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			/**
			 * 如果双击，打开相应的属性编辑窗口
			 */
			//			if (e.getClickCount() == 2) {
			//				Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());
			//				if (cell != null) {
			//					if (cell instanceof DefaultEdge) {
			//						new TransitionPropertiesEditDialog(WorkFlowEditFrame.this, "转移", (DefaultEdge) cell, graph);
			//						graph.getGraphLayoutCache().reload();
			//						graph.updateUI();
			//
			//					} else if (cell instanceof DefaultGraphCell) {
			//						//new ActivityPropertiesEditDialog(WorkFlowEditFrame.this, "环节", (DefaultGraphCell) cell, graph);
			//						graph.getGraphLayoutCache().reload();
			//						graph.updateUI();
			//					}
			//				}
			//			}
			/**
			 * 如果单击，刷新结点所在的位置
			 */
			if (e.getClickCount() == 1) {
				Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());

				if (cell instanceof DefaultGraphCell) {
					Rectangle2D point2D = GraphConstants.getBounds(((DefaultGraphCell) cell).getAttributes()); //取得坐标位置
					if (point2D != null) {
						int x = new Double(point2D.getX()).intValue();
						int y = new Double(point2D.getY()).intValue();
						((ActivityVO) ((DefaultGraphCell) cell).getAttributes().get("linked")).setX(new Integer(x));
						((ActivityVO) ((DefaultGraphCell) cell).getAttributes().get("linked")).setY(new Integer(y));
					}
				}
				//				clearSelectedActivityTemplate
			}
		}
	}

	private class StatusBarGraphListener extends JPanel implements GraphModelListener {
		private static final long serialVersionUID = 8804516095608420463L;

		/**
		 * Graph Model change event
		 */
		public void graphChanged(GraphModelEvent e) {
			updateStatusBar();
		}

		protected void updateStatusBar() {

		}
	}

	private class EdStatusBar extends StatusBarGraphListener {
		private static final long serialVersionUID = -5664450060695652205L;

		protected JLabel leftSideStatus;

		/**
		 * contains the scale for the current graph
		 */
		protected JLabel rightSideStatus;

		/**
		 * Constructor for GPStatusBar.
		 * 
		 */
		public EdStatusBar() {
			super();
			// Add this as graph model change listener
			setLayout(new BorderLayout());
			leftSideStatus = new JLabel("欢迎使用国信朗讯工作流配置平台");

			Runtime runtime = Runtime.getRuntime();
			int freeMemory = (int) (runtime.freeMemory() / 1024);
			int totalMemory = (int) (runtime.totalMemory() / 1024);
			int usedMemory = (totalMemory - freeMemory);
			String str = "已用内存/所有内存:" + usedMemory + "/" + totalMemory + "K";

			rightSideStatus = new JLabel(str);
			leftSideStatus.setBorder(BorderFactory.createLoweredBevelBorder());
			rightSideStatus.setBorder(BorderFactory.createLoweredBevelBorder());
			add(leftSideStatus, BorderLayout.CENTER);
			add(rightSideStatus, BorderLayout.EAST);
		}

		protected void updateStatusBar() {
			Runtime runtime = Runtime.getRuntime();
			int freeMemory = (int) (runtime.freeMemory() / 1024);
			int totalMemory = (int) (runtime.totalMemory() / 1024);
			int usedMemory = (totalMemory - freeMemory);
			String str = "已用内存/所有内存:" + usedMemory + "/" + totalMemory + "K";
			rightSideStatus.setText(str);
		}

		/**
		 * @return Returns the leftSideStatus.
		 */
		public JLabel getLeftSideStatus() {
			return leftSideStatus;
		}

		/**
		 * @param leftSideStatus
		 *            The leftSideStatus to set.
		 */
		public void setLeftSideStatus(JLabel leftSideStatus) {
			this.leftSideStatus = leftSideStatus;
		}

		/**
		 * @return Returns the rightSideStatus.
		 */
		public JLabel getRightSideStatus() {
			return rightSideStatus;
		}

		/**
		 * @param rightSideStatus
		 *            The rightSideStatus to set.
		 */
		public void setRightSideStatus(JLabel rightSideStatus) {
			this.rightSideStatus = rightSideStatus;
		}
	}

	// This will change the source of the actionevent to graph.
	public class EventRedirector extends AbstractAction {
		private static final long serialVersionUID = 2116272676035838629L;

		protected Action action;

		// Construct the "Wrapper" Action
		public EventRedirector(Action a) {
			super("", (ImageIcon) a.getValue(Action.SMALL_ICON));
			this.action = a;
		}

		// Redirect the Actionevent
		public void actionPerformed(ActionEvent e) {
			e = new ActionEvent(graph, e.getID(), e.getActionCommand(), e.getModifiers());
			action.actionPerformed(e);
		}
	}

	//	public Object getWorkFlowTemplateVO(){
	//		return workflowTemplateVO;
	//	}
	//	
	public ProcessVO getProcessVO() {
		return this.currentProcessVO;
	}

	public Object[] getActivitiyTemplateVOs() {
		ArrayList list = new ArrayList();
		int count = graph.getModel().getRootCount();
		for (int i = 0; i < count; i++) {
			Object obj = graph.getModel().getRootAt(i);
			list.add(obj);
		}

		return (Object[]) list.toArray(new Object[0]);
	}
}
/**************************************************************************
 * $RCSfile: WorkFlowEditFrame.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:18:02 $
 *
 * $Log: WorkFlowEditFrame.java,v $
 * Revision 1.4  2012/09/14 09:18:02  xch123
 * 邮储现场回来一起更新
 *
 * Revision 1.1  2012/08/28 09:41:17  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:32:17  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:52  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:35  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:34:05  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:54  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:13:35  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:11:45  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:53  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:48:01  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/03/16 05:41:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:43  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:47  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/09/23 08:06:08  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/23 08:03:03  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:29:19  xch
 * *** empty log message ***
 *
 * Revision 1.4  2007/09/20 05:08:33  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/04/05 08:30:48  sunxb
 * *** empty log message ***
 *
 * Revision 1.2  2007/04/05 07:59:40  sunxf
 * *** empty log message ***
 *
 * Revision 1.1  2007/04/05 05:08:38  sunxb
 * *** empty log message ***
 *
 * Revision 1.1  2007/04/03 01:47:44  sunxb
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/28 09:22:09  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/03/28 03:51:02  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/03/28 03:01:18  shxch
 * *** empty log message ***
 *
 * Revision 1.30  2007/03/27 09:26:16  sunxb
 * *** empty log message ***
 *
 * Revision 1.29  2007/03/27 07:44:13  sunxb
 * *** empty log message ***
 *
 * Revision 1.28  2007/03/26 05:54:32  sunxb
 * *** empty log message ***
 *
 * Revision 1.27  2007/03/23 09:07:27  sunxb
 * *** empty log message ***
 *
 * Revision 1.26  2007/03/23 09:06:33  sunxb
 * *** empty log message ***
 *
 * Revision 1.25  2007/03/23 09:03:45  sunxb
 * *** empty log message ***
 *
 * Revision 1.24  2007/03/23 08:10:42  shxch
 * *** empty log message ***
 *
 * Revision 1.22  2007/03/23 05:35:03  shxch
 * *** empty log message ***
 *
 * Revision 1.21  2007/03/23 03:22:26  sunxb
 * *** empty log message ***
 *
 * Revision 1.20  2007/03/22 08:32:24  sunxb
 * *** empty log message ***
 *
 * Revision 1.19  2007/03/22 07:54:04  sunxb
 * *** empty log message ***
 *
 * Revision 1.18  2007/03/22 07:49:23  sunxb
 * *** empty log message ***
 *
 * Revision 1.17  2007/03/22 07:39:04  sunxb
 * *** empty log message ***
 *
 * Revision 1.16  2007/03/22 07:26:24  sunxb
 * *** empty log message ***
 *
 * Revision 1.15  2007/03/22 07:03:19  sunxb
 * *** empty log message ***
 *
 * Revision 1.14  2007/03/21 08:09:03  sunxb
 * *** empty log message ***
 *
 * Revision 1.13  2007/03/21 03:16:42  sunxb
 * *** empty log message ***
 *
 * Revision 1.12  2007/03/20 09:38:51  shxch
 * *** empty log message ***
 *
 * Revision 1.11  2007/03/20 09:16:54  sunxb
 * *** empty log message ***
 *
 * Revision 1.10  2007/03/20 07:15:56  sunxb
 * *** empty log message ***
 *
 * Revision 1.9  2007/03/20 01:28:06  sunxb
 * *** empty log message ***
 *
 * Revision 1.8  2007/03/19 09:20:23  sunxb
 * *** empty log message ***
 *
 * Revision 1.7  2007/03/16 10:25:15  sunxb
 * *** empty log message ***
 *
 * Revision 1.6  2007/03/16 05:18:30  sunxb
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/15 01:45:29  sunxb
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/14 08:07:56  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/14 07:27:36  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/03/14 07:22:55  sunxb
 * *** empty log message ***
 *
 * Revision 1.1  2007/03/07 01:59:13  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 03:53:26  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
