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
	//--�ļ�����ť
	protected JButton btn_new, btn_open, btn_save, btn_config = null;

	//--ͼ�ο��ư�ť
	protected JButton btn_zoom, btn_zoomIn, btn_zoomOut;

	//--ͼ�α༭��ť
	protected JButton btn_insert, btn_start, btn_end;

	//--ͼ�α༭����
	protected Action undo, redo, remove, group, ungroup, tofront, toback;

	//�༭��ť
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

	String str_processID = null; //��ǰ�ڴ滷��ID!!

	//private WorkFlowTemplateIFC workflowTemplate = null; //�͵�ǰƽ̨���̶�Ӧ������ģ��

	//	private BillVO workflowTemplateVO=null;

	public WorkFlowEditFrame() {
		this.setTitle("�������༭"); //..
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
		//���ñ���
		//		setTitle("�������༭�� - (��)");
		//���ô�С --??

		//���ò��ֹ�����
		//		getContentPane().
		setLayout(new BorderLayout());
		//����ϵͳ������
		//getContentPane().
		add(createSysBar(false), BorderLayout.NORTH);
		//�����ͼ�ι�����
		//getContentPane().
		add(createToolBar(true), BorderLayout.WEST);

		//�м��ͼ�ι�����
		graphpanel = new JPanel();
		graphpanel.setLayout(new BorderLayout());
		//getContentPane().
		add(new JScrollPane(graphpanel), BorderLayout.CENTER);

		//�ײ���״̬��
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
		//DefaultGraphCell cell = new DefaultGraphCell("����" + cellCount);

		int li_x = 50 + 70 * cellCount;
		int li_y = 50 + 40 * cellCount;

		DefaultGraphCell cell = createVertex("����" + cellCount, li_x, li_y, 60, 35, new Color(193, 235, 255), Color.BLACK, true, "gxlu.nova.framework.workflow.ui.JGraphEllipseView");

		ActivityVO activityVO = new ActivityVO();
		activityVO.setCode("WFA" + cellCount); //����
		activityVO.setWfname("����" + cellCount); //����ͼ�е���ʾ����
		activityVO.setUiname("����" + cellCount); //��������ʾ������
		activityVO.setX(new Integer(li_x)); //X
		activityVO.setY(new Integer(li_y)); //Y

		activityVO.setDesc(""); //����

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
			transitionVO.setWfname("ת��" + edgecount);
			transitionVO.setUiname("ת��" + edgecount);

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
		DefaultEdge edge = new DefaultEdge("ת��" + edgecount);
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
					if (p.getParent().toString().equals("��ʼ")) {
						graph.repaint();
						return;
					}
				}
				if (source instanceof DefaultPort) {
					DefaultPort p = (DefaultPort) source;
					if (p.getParent().toString().equals("����")) {
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
	 * ����һ����ʼ���
	 */
	private void addStart() {
		if (hasStartActivity)
			return;
		hasStartActivity = true;
		int x = 10;
		int y = 10;
		DefaultGraphCell cell = createVertex("��ʼ", x, y, 45, 45, new Color(193, 235, 255), Color.BLACK, true, "gxlu.nova.framework.workflow.ui.JGraphEllipseView");
		ImageIcon startIcon = UIUtil.getImage("workflow/start.gif");
		GraphConstants.setIcon(cell.getAttributes(), startIcon);

		ActivityVO activityVO = new ActivityVO();
		activityVO.setCode("START"); //
		activityVO.setWfname("��ʼ"); //
		activityVO.setUiname("��ʼ"); //
		activityVO.setX(new Integer(x));
		activityVO.setY(new Integer(y));

		activityVO.setDesc("");

		cell.getAttributes().put("linked", activityVO);

		graph.getGraphLayoutCache().insert(cell); // �������

		if (!undo.isEnabled())
			undo.setEnabled(true);
	}

	/**
	 * ����һ���������
	 */
	private void addEnd() {
		if (hasEndActivity)
			return;
		hasEndActivity = true;
		int x = 450;
		int y = 400;

		DefaultGraphCell cell = createVertex("����", x, y, 45, 45, Color.ORANGE, Color.BLACK, true, "gxlu.nova.framework.workflow.ui.JGraphEllipseView");
		ImageIcon startIcon = UIUtil.getImage("workflow/end.gif");
		GraphConstants.setIcon(cell.getAttributes(), startIcon);

		ActivityVO activityVO = new ActivityVO();
		activityVO.setCode("END"); //
		activityVO.setWfname("����"); //
		activityVO.setUiname("����"); //
		activityVO.setX(new Integer(x));
		activityVO.setY(new Integer(y));

		activityVO.setDesc("");

		cell.getAttributes().put("linked", activityVO);

		graph.getGraphLayoutCache().insert(cell); // �������

		if (!undo.isEnabled())
			undo.setEnabled(true);
	}

	/**
	 * <b>��ˮƽ������</b>
	 * �������鹤����:
	 * 1."�½�","��","����","����������Ϣ"
	 * 2."�ָ�ԭʼ��С","�Ŵ�","��С"
	 * 3."����","ճ��","����","ɾ��"
	 * @return ������
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
		btn_new.setToolTipText("�½�");
		btn_new.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onNew();
			}

		});
		toolbar.add(btn_new);

		ImageIcon openIcon = UIUtil.getImage("workflow/open.gif");
		btn_open = new JButton(openIcon);
		btn_open.setToolTipText("����һ��������");
		btn_open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOpen();
			}

		});
		toolbar.add(btn_open);

		ImageIcon saveIcon = UIUtil.getImage("workflow/save.gif");
		btn_save = new JButton(saveIcon);
		btn_save.setToolTipText("����");
		btn_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSave();
			}
		});
		toolbar.add(btn_save);

		ImageIcon configPIcon = UIUtil.getImage("workflow/config.gif");
		btn_config = new JButton(configPIcon);
		btn_config.setToolTipText("����������Ϣ");
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
		btn_zoom.setToolTipText("�ָ�ԭʼ��С");
		btn_zoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graph.setScale(1.0);
			}
		});
		toolbar.add(btn_zoom);

		//Zoom In
		ImageIcon zoomInIcon = UIUtil.getImage("workflow/zoomin.gif");
		btn_zoomIn = new JButton(zoomInIcon);
		btn_zoomIn.setToolTipText("��С");
		btn_zoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				graph.setScale(1.2 * graph.getScale());
			}
		});
		toolbar.add(btn_zoomIn);

		// Zoom Out
		ImageIcon zoomOutIcon = UIUtil.getImage("workflow/zoomout.gif");
		btn_zoomOut = new JButton(zoomOutIcon);
		btn_zoomOut.setToolTipText("�Ŵ�");
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
	 * <b>����ֱ������</b>
	 * �������鹤����:
	 * 1."��ӿ�ʼ���","��ӽ������","��������"
	 * 2."�ָ�","����"
	 * 3."�ŵ���ǰ��","�ŵ������"
	 * 4."����","ȡ������"
	 * @return ������
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
		btn_start.setToolTipText("��ӿ�ʼ���");
		btn_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addStart();
			}
		});
		toolbar.add(btn_start);

		ImageIcon endIcon = UIUtil.getImage("workflow/end.gif");
		btn_end = new JButton(endIcon);
		btn_end.setToolTipText("��ӽ������");
		btn_end.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addEnd();
			}
		});
		toolbar.add(btn_end);

		ImageIcon insertIcon = UIUtil.getImage("workflow/insert.gif");
		btn_insert = new JButton(insertIcon);
		btn_insert.setToolTipText("��������");
		btn_insert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addActivity(); //���ӻ���
				if (!undo.isEnabled())
					undo.setEnabled(true);
			}
		});
		toolbar.add(btn_insert);

		// Undo
		toolbar.addSeparator();
		ImageIcon undoIcon = UIUtil.getImage("workflow/undo.gif");
		//		JButton btn_undo = new JButton(undoIcon);
		//		btn_undo.setToolTipText("����");
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
	 * @return ����JGraph����
	 */
	public JGraph getGraph() {
		return graph;
	}

	/**
	 * @param graph
	 * 			���ñ�ʾ��ǰ��JGraph����
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
		//			//this.setTitle("�������༭�� - " + currentProcessVO.getName() + "(" + currentProcessVO.getCode() + ")");
		//			try {
		//				str_processID = UIUtil.getSequenceNextValByDS(null, "s_pub_wf_process");
		//			} catch (WLTRemoteException e) {
		//				e.printStackTrace();
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			} //ȡ������ID
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
			JOptionPane.showMessageDialog(WorkFlowEditFrame.this, "�޷��������Ϣ", "����", JOptionPane.OK_OPTION);
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

		//���¶���
		for (int i = 0; i < rootCount; i++) {
			Object obj = model.getRootAt(i);
			if (obj instanceof DefaultGraphCell) {

				DefaultGraphCell cell = (DefaultGraphCell) obj;
				Object linkedObject = cell.getAttributes().get("linked");

				if (linkedObject instanceof ActivityVO) {
					Rectangle2D point2D = GraphConstants.getBounds(cell.getAttributes()); //ȡ������λ��
					int x = new Double(point2D.getX()).intValue();
					int y = new Double(point2D.getY()).intValue();
					//int id = this.getNewActivityID();

					ActivityVO activityVO = (ActivityVO) linkedObject; //����ת��
					//activityVO.setId(id);
					//activityVO.setJoincount(getVertexInCount(cell));
					//activityVO.setSplitcount(getVertexOutCount(cell));
					activityVO.setX(new Integer(x));
					activityVO.setY(new Integer(y));
					activityVOList.add(activityVO);
				}
			}
		}

		//���±�
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

		//Լ�����
		if (checkBeforeSave() == false)
			return;

		System.out.println("");

		currentProcessVO.setActivityVOs((ActivityVO[]) activityVOList.toArray(new ActivityVO[0]));
		currentProcessVO.setTransitionVOs((TransitionVO[]) transitionVOList.toArray(new TransitionVO[0]));

		try {
			WorkFlowServiceIfc wfservice = (WorkFlowServiceIfc) RemoteServiceFactory.getInstance().lookUpService(WorkFlowServiceIfc.class); //Remote Call
			wfservice.saveWFProcess(currentProcessVO, str_processID);

			//			workflowTemplate.saveWorkFlowTemplate(currentProcessVO); //���湤����ģ��

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
		//			//this.setTitle("�������༭�� - " + currentProcessVO.getName() + "(" + currentProcessVO.getCode() + ")");
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
			//this.setTitle("�������༭�� - " + currentProcessVO.getName() + "(" + currentProcessVO.getCode() + ")");

			//workflowTemplate.loadWorkFlowTemplate(currentProcessVO.getId() + ""); //������ģ��

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
	 * ������ͼ�ĵط�
	 * @param _worktickno
	 * @param _processid
	 * @param _tabpos
	 * @return
	 */
	private void openMainGraph(ProcessVO processVO) {
		TransitionVO[] tansitionVOs = processVO.getTransitionVOs(); //�õ���������

		ActivityVO[] activityVOs = processVO.getActivityVOs();

		Hashtable cellMap = new Hashtable();

		//����������ͨ���
		for (int i = 0; i < activityVOs.length; i++) {
			DefaultGraphCell cell = null;

			if (activityVOs[i].getCode().equalsIgnoreCase("START")) {
				cell = createVertex(activityVOs[i].getWfname(), activityVOs[i].getX().intValue(), activityVOs[i].getY().intValue(), 45, 45, new Color(193, 235, 255), Color.BLACK, true, "gxlu.nova.framework.workflow.ui.JGraphEllipseView");
				ImageIcon startIcon = UIUtil.getImage("workflow/start.gif");
				GraphConstants.setIcon(cell.getAttributes(), startIcon);
				cell.getAttributes().put("linked", activityVOs[i]);
				//GraphConstants.setGradientColor(cell.getAttributes(), new Color(102, 255, 102));
				graph.getGraphLayoutCache().insert(cell); //�������
			} else if (activityVOs[i].getCode().equalsIgnoreCase("END")) {
				cell = createVertex(activityVOs[i].getWfname(), activityVOs[i].getX().intValue(), activityVOs[i].getY().intValue(), 45, 45, Color.ORANGE, Color.BLACK, true, "gxlu.nova.framework.workflow.ui.JGraphEllipseView");
				ImageIcon endIcon = UIUtil.getImage("workflow/end.gif");
				GraphConstants.setIcon(cell.getAttributes(), endIcon);
				cell.getAttributes().put("linked", activityVOs[i]);
				//GraphConstants.setGradientColor(cell.getAttributes(), new Color(102, 255, 102));
				graph.getGraphLayoutCache().insert(cell); //�������
			} else {
				cell = createVertex(activityVOs[i].getWfname(), activityVOs[i].getX().intValue(), activityVOs[i].getY().intValue(), unicode_length(activityVOs[i].getWfname()) * 6 + 30, 35, new Color(193, 235, 255), Color.BLACK, true, "gxlu.nova.framework.workflow.ui.JGraphEllipseView");
				cell.getAttributes().put("linked", activityVOs[i]);
				graph.getGraphLayoutCache().insert(cell); //�������
			}
			cellMap.put(activityVOs[i].getCode() + "", cell);
		}

		//ȥ����ʼ�ͽ����ڵ�
		cellCount = activityVOs.length - 3;

		//������������
		for (int i = 0; i < tansitionVOs.length; i++) {
			DefaultEdge edge = new DefaultEdge(tansitionVOs[i].getWfname());
			this.setEdgeAttributes(edge.getAttributes());
			edge.getAttributes().put("linked", tansitionVOs[i]);

			//			String fromId = tansitionVOs[i].getFromactivity(); //��...
			//			String toId = tansitionVOs[i].getToactivity(); //ȥ...
			//
			//			DefaultGraphCell fromcell = (DefaultGraphCell) cellMap.get(fromId + "");
			//			DefaultGraphCell tocell = (DefaultGraphCell) cellMap.get(toId + "");
			//
			//			if (fromcell != null && tocell != null) {
			//				edge.setSource(fromcell.getChildAt(0)); //��ʼλ��
			//				edge.setTarget(tocell.getChildAt(0)); //����λ��
			//			}
			//
			//			GraphConstants.setEndFill(edge.getAttributes(), true);
			//			GraphConstants.setLineEnd(edge.getAttributes(), GraphConstants.ARROW_SIMPLE);
			//
			//			//��������·��
			//			List pointLs = tansitionVOs[i].getPoints(); //�Ѿ�������ʼ,�������
			//			GraphConstants.setPoints(edge.getAttributes(), this.convertToPoint2DList(pointLs));
			//
			//			edgeRender(edge);
			//
			//			graph.getGraphLayoutCache().insert(edge); //�������
		}

		edgecount = tansitionVOs.length;
		hasStartActivity = true;
		hasEndActivity = true;

		graph.getGraphLayoutCache().reload();
		graph.clearSelection(); //		
		graph.updateUI();
	}

	/**
	 * �õ����еĿ�ʼ���
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
	 * �õ��������
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
	 * �õ����е��м���
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
	 * ����һ�����ƵĽ��
	 */
	public DefaultGraphCell createVertex(Object name, double x, double y, double w, double h, Color bg, Color fore, boolean raised, String viewClass) {
		DefaultGraphCell cell = new DefaultGraphCell(name);

		if (viewClass != null) {
			GPCellViewFactory.setViewClass(cell.getAttributes(), viewClass); //
		}

		GraphConstants.setBounds(cell.getAttributes(), new Rectangle2D.Double(x, y, w, h)); //
		if (bg != null) {
			GraphConstants.setBackground(cell.getAttributes(), bg);
			GraphConstants.setFont(cell.getAttributes(), new Font("����", Font.PLAIN, 12));
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
	 * ���°�ť״̬
	 */
	public void updateButtonStatus(int sysStatus) {

		//1-ϵͳ��ʼ��, 2-�½�, 3-����
		if (sysStatus == 1) { //ϵͳ��ʼ��

			//--�ļ����ư�ť
			btn_new.setEnabled(true);
			btn_save.setEnabled(false);
			btn_open.setEnabled(true);
			btn_config.setEnabled(false);

			//--ͼ�ο��ư�ť
			btn_zoom.setEnabled(false);
			btn_zoomIn.setEnabled(false);
			btn_zoomOut.setEnabled(false);

			//--ͼ�α༭��ť
			btn_insert.setEnabled(false);
			btn_start.setEnabled(false);
			btn_end.setEnabled(false);

			//--ͼ�α༭����
			undo.setEnabled(false);
			redo.setEnabled(false);
			remove.setEnabled(false);
			group.setEnabled(false);
			ungroup.setEnabled(false);
			tofront.setEnabled(false);
			toback.setEnabled(false);

			//�༭��ť
			cut.setEnabled(false);
			copy.setEnabled(false);
			paste.setEnabled(false);
		} else if (sysStatus == 2) { //�½�

			//--�ļ����ư�ť
			btn_new.setEnabled(true);
			btn_save.setEnabled(true);
			btn_open.setEnabled(true);
			btn_config.setEnabled(false);

			//--ͼ�ο��ư�ť
			btn_zoom.setEnabled(true);
			btn_zoomIn.setEnabled(true);
			btn_zoomOut.setEnabled(true);

			//--ͼ�α༭��ť
			btn_insert.setEnabled(true);
			btn_start.setEnabled(true);
			btn_end.setEnabled(true);

			//--ͼ�α༭����
			undo.setEnabled(false);
			redo.setEnabled(false);
			remove.setEnabled(false);
			group.setEnabled(false);
			ungroup.setEnabled(false);
			tofront.setEnabled(false);
			toback.setEnabled(false);

			//�༭��ť
			cut.setEnabled(false);
			copy.setEnabled(false);
			paste.setEnabled(false);
		} else if (sysStatus == 3) { //����
			btn_save.setEnabled(true);
		}
	}

	/**
	 * ���±��������Ķ�����Ϣ
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
	 * �õ��Ӹö�����������еı�
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
	 * �õ��ӽ���ö�������еı�
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
	 * �õ�����ĳ���
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
	 * �õ���������
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
	 * ɾ��ѡ�е�һ���������
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
				//���ɾ�����Ƕ���,��Ҫ�ռ������ıߵ���Ϣ,����ر�һ��ɾ��!
				if (((!(cells[i] instanceof DefaultEdge)) && !(cells[i] instanceof DefaultPort)) && (cells[i] instanceof DefaultGraphCell)) {
					ActivityVO vertexVO = (ActivityVO) ((DefaultGraphCell) cells[i]).getAttributes().get("linked");
					for (int j = 0; j < totalCount; j++) {
						DefaultGraphCell cell = (DefaultGraphCell) graph.getModel().getRootAt(j);
						if (cell instanceof DefaultEdge) {
							ActivityVO sourceVO = (ActivityVO) getEdgeSource((DefaultEdge) cell).getAttributes().get("linked");
							ActivityVO targetVO = (ActivityVO) getEdgeTarget((DefaultEdge) cell).getAttributes().get("linked");
							if (sourceVO.getCode().equals(vertexVO.getCode())) { //����ߵ���ʼ����Ϊ��ɾ������
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
				} else if (cells[i] instanceof DefaultEdge) { //���ɾ�����Ǳ�,��Ҫ�Ѻͱ�������Ķ���ĳ��Ⱥ����ֵ�ֱ��һ!
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
	 * ����ǰ���кϷ���У���飬��У������ͼ�μ�У��
	 * @return
	 */
	private boolean checkBeforeSave() {
		GraphModel model = graph.getModel();
		int cellCount = model.getRootCount();

		ArrayList vertexList = new ArrayList();
		ActivityVO[] vertexes = null;
		ArrayList edgeList = new ArrayList();

		ArrayList errMsg = new ArrayList();

		//�õ����еĶ���ͱ�
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

		//�õ���ʼ���,���������м���
		ActivityVO[] startVertex = getStartActivityVO(vertexes);
		ActivityVO[] endVertex = getEndActivityVO(vertexes);
		ActivityVO[] middleVertex = getActivityVO(vertexes);

		//1.��鿪ʼ,�����ڵ�
		if (startVertex == null || startVertex.length == 0)
			errMsg.add("ȱ�ٿ�ʼ���!\n");

		if (endVertex == null || endVertex.length == 0)
			errMsg.add("ȱ�ٽ������!\n");

		//2.��鿪ʼ�ڵ��Ƿ�������,�Ƿ������
		//		if (startVertex != null && startVertex.length != 0 && startVertex[0].getJoinCount() > 0)
		//			errMsg.add("��ʼ��㲻��������!\n");
		//		if (startVertex != null && startVertex.length != 0 && startVertex[0].getSplitCount() <= 0)
		//			errMsg.add("��ʼ�����������!\n");

		//3.�������ڵ��Ƿ������
		//		if (endVertex != null && endVertex.length != 0 && endVertex[0].getSplitCount() > 0)
		//			errMsg.add("������㲻�������!\n");
		//		if (endVertex != null && endVertex.length != 0 && endVertex[0].getJoinCount() <= 0)
		//			errMsg.add("����������������!\n");

		//4.����Ƿ���ڹ����ڵ�
		//		if (middleVertex == null || middleVertex.length == 0) {
		//			errMsg.add("�������м���!\n");
		//		} else {
		//			for (int i = 0; i < middleVertex.length; i++) {
		//				if (middleVertex[i].getJoinCount() <= 0 && middleVertex[i].getSplitCount() <= 0)
		//					errMsg.add("���\"" + middleVertex[i].getWfname() + "\"����Ϊ�������!\n");
		//				else {
		//					if (middleVertex[i].getJoinCount() <= 0) {
		//						errMsg.add("���\"" + middleVertex[i].getWfname() + "\"����û������!\n");
		//					} else if (middleVertex[i].getSplitCount() <= 0)
		//						errMsg.add("���\"" + middleVertex[i].getWfname() + "\"����û�����!\n");
		//				}
		//			}
		//		}

		if (errMsg.size() != 0) {
			StringBuffer msg = new StringBuffer("�������:\n");
			for (int i = 0; i < errMsg.size(); i++) {
				msg.append((i + 1) + "." + errMsg.get(i));
			}
			JOptionPane.showMessageDialog(this, msg.toString());

			return false;
		}

		return true;
	}

	/**
	 * �����ƶ����ַ���,������ֽڳ���,����ַ������и������ַ�,��ô���ĳ��Ⱦ���2
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
	 * ���ݶ������Ⱥͳ��ȸ���������������ʾ��ʽ
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
	 * ����edge���˵Ľ�����Ⱥͳ��ȸ���edge��ʼ�ͽ����˵����ʾ��ʽ
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
	 * ��List�е�double[]Ԫ��ת��Ϊ��Ӧ��Point2D.Double����
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
	 * ��List�е�Point2D.DoubleԪ��ת��Ϊ��Ӧ��double[]����
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
	 * ��������
	 * @author Administrator
	 *
	 */
	private class MyMouseListener extends java.awt.event.MouseAdapter {

		public void mouseClicked(MouseEvent e) {
			/**
			 * ���˫��������Ӧ�����Ա༭����
			 */
			//			if (e.getClickCount() == 2) {
			//				Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());
			//				if (cell != null) {
			//					if (cell instanceof DefaultEdge) {
			//						new TransitionPropertiesEditDialog(WorkFlowEditFrame.this, "ת��", (DefaultEdge) cell, graph);
			//						graph.getGraphLayoutCache().reload();
			//						graph.updateUI();
			//
			//					} else if (cell instanceof DefaultGraphCell) {
			//						//new ActivityPropertiesEditDialog(WorkFlowEditFrame.this, "����", (DefaultGraphCell) cell, graph);
			//						graph.getGraphLayoutCache().reload();
			//						graph.updateUI();
			//					}
			//				}
			//			}
			/**
			 * ���������ˢ�½�����ڵ�λ��
			 */
			if (e.getClickCount() == 1) {
				Object cell = graph.getFirstCellForLocation(e.getX(), e.getY());

				if (cell instanceof DefaultGraphCell) {
					Rectangle2D point2D = GraphConstants.getBounds(((DefaultGraphCell) cell).getAttributes()); //ȡ������λ��
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
			leftSideStatus = new JLabel("��ӭʹ�ù�����Ѷ����������ƽ̨");

			Runtime runtime = Runtime.getRuntime();
			int freeMemory = (int) (runtime.freeMemory() / 1024);
			int totalMemory = (int) (runtime.totalMemory() / 1024);
			int usedMemory = (totalMemory - freeMemory);
			String str = "�����ڴ�/�����ڴ�:" + usedMemory + "/" + totalMemory + "K";

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
			String str = "�����ڴ�/�����ڴ�:" + usedMemory + "/" + totalMemory + "K";
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
 * �ʴ��ֳ�����һ�����
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
