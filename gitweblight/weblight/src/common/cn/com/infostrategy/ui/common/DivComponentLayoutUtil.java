package cn.com.infostrategy.ui.common;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/**
 * 给一个面板做层布局。本质是加载起layoutpane上。
 * 需要根据该面板的显示状态来设置div控件的添加与移除.
 * 自己在layoutpane上添加东西，如果不移除，可能会出现内存不释放问题。
 * @author haoming
 * create by 2013-7-26
 */
public final class DivComponentLayoutUtil implements Serializable {
	private static final long serialVersionUID = -7436687046719873368L;

	private HashMap<JComponent, DivPaneBean> divMap = new HashMap<JComponent, DivPaneBean>();

	private PanelHierarchyListener hierarchy = new PanelHierarchyListener();

	private PanelComponentListener componentListener = new PanelComponentListener();

	private PanelHierarchyBoundListener boundListener = new PanelHierarchyBoundListener();

	/*
					 * 
					 */
	private DivComponentLayoutUtil() {
	};

	private static DivComponentLayoutUtil util = new DivComponentLayoutUtil();

	public static DivComponentLayoutUtil getInstanse() {
		return util;
	}

	public void addComponentOnDiv(JComponent _parent, JComponent _divComponent, Rectangle _rect, DivComponentCustomResetRectListener _custonResetRect) {
		addComponentOnDiv(_parent, _divComponent, _rect, _custonResetRect, JLayeredPane.DRAG_LAYER);
	}

	public void addComponentOnDiv(JComponent _parent, JComponent _divComponent, Rectangle _rect, DivComponentCustomResetRectListener _custonResetRect, Integer layout) {
		if (_parent == null) {
			return;
		}
		if (divMap.containsKey(_parent)) { //如过已经做过添加div操作。
			DivPaneBean bean = (DivPaneBean) divMap.get(_parent);
			bean.setComponent(_divComponent, _rect);
		} else {
			try {
				DivPaneBean bean = new DivPaneBean(_parent);
				bean.setResetRectListener(_custonResetRect);
				bean.setComponent(_divComponent, _rect);
				divMap.put(_parent, bean);
				_parent.addHierarchyBoundsListener(boundListener);
				_parent.addHierarchyListener(hierarchy);
				_parent.addComponentListener(componentListener);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		JLayeredPane pane = _parent.getRootPane().getLayeredPane();
		Point thisPoint = _parent.getLocationOnScreen(); //获取当前页面的位置
		SwingUtilities.convertPointFromScreen(thisPoint, pane); //取到当前面板相对layeredpane的位置.
		Rectangle newRect = (Rectangle) _rect.clone();
		newRect.setLocation(thisPoint.x + _rect.x, thisPoint.y + _rect.y);
		_divComponent.setBounds(newRect);
		pane.add(_divComponent, layout);
	}

	public void addComponentOnDiv(JComponent _parent, JComponent _divComponent, Rectangle _rect, int _delay) {

	}

	/*
	 * 一般不需要手动去控制
	 */
	public void removeComponentOnDiv(JComponent _parent, JComponent _divComponent) {
		if (_divComponent == null) {
			return;
		}
		DivPaneBean bean = divMap.get(_parent);
		if (bean != null) {
			bean.getLayeredPane().remove(_divComponent);
			bean.getDivComponent().remove(_divComponent);
			bean.getLayeredPane().repaint();
			bean.getLayeredPane().updateUI();
		}
	}

	private synchronized void resetComponentDivLocation(JComponent _parentComponent) {
		if (!_parentComponent.isShowing()) {
			return;
		}
		DivPaneBean bean = divMap.get(_parentComponent); //获取层对象
		Point thisPoint = _parentComponent.getLocationOnScreen(); //获取当前页面的位置
		SwingUtilities.convertPointFromScreen(thisPoint, bean.getLayeredPane()); //取到当前面板相对layeredpane的位置.
		HashMap<JComponent, Object> allDiv = bean.getDivComponent(); //所有层面板 
		for (Iterator iterator = allDiv.entrySet().iterator(); iterator.hasNext();) {
			Entry<JComponent, Object> jComponent = (Entry<JComponent, Object>) iterator.next();
			if (bean.getResetRectListener() != null) {
				bean.getResetRectListener().resetComponentDivLocation(jComponent.getKey());
			} else {
				Rectangle rect = (Rectangle) jComponent.getValue();
				jComponent.getKey().setLocation(thisPoint.x + rect.x, thisPoint.y + rect.y);
			}
		}
	}

	//有一些面板内部嵌套好多面板后，内部的移动，层变换事件不一定全部会执行，导出出现不绘制。故添加此事件。
	class PanelHierarchyBoundListener implements HierarchyBoundsListener {
		public void ancestorMoved(HierarchyEvent hierarchyevent) {
			resetComponentDivLocation((JComponent) hierarchyevent.getComponent());
		}

		public void ancestorResized(HierarchyEvent hierarchyevent) {
			resetComponentDivLocation((JComponent) hierarchyevent.getComponent());
		}

	}

	class PanelComponentListener extends ComponentAdapter {

		public void componentMoved(ComponentEvent e) {
			resetComponentDivLocation((JComponent) e.getComponent());
		}

		public void componentResized(ComponentEvent e) {
			resetComponentDivLocation((JComponent) e.getComponent());
		}

	}

	class PanelHierarchyListener implements HierarchyListener {
		public void hierarchyChanged(HierarchyEvent hierarchyevent) {
			if (hierarchyevent.getComponent().isShowing()) {//如果显示着
				JComponent actionPane = (JComponent) hierarchyevent.getComponent(); //得到
				DivPaneBean bean = divMap.get(hierarchyevent.getComponent());
				JLayeredPane pane = bean.getLayeredPane();
				HashMap<JComponent, Object> allDiv = bean.getDivComponent();
				for (Iterator iterator = allDiv.keySet().iterator(); iterator.hasNext();) {
					JComponent divComponent = (JComponent) iterator.next();
					if (divComponent != null) {
						pane.add(divComponent);
					}
				}
				resetComponentDivLocation(actionPane);
			} else {
				DivPaneBean bean = divMap.get(hierarchyevent.getComponent());
				JComponent actionPane = (JComponent) hierarchyevent.getComponent(); //得到
				JLayeredPane pane = bean.getLayeredPane();
				HashMap<JComponent, Object> allDiv = bean.getDivComponent();
				for (Iterator iterator = allDiv.keySet().iterator(); iterator.hasNext();) {
					JComponent divComponent = (JComponent) iterator.next();
					if (divComponent != null) {
						pane.remove(divComponent);
					}
				}
				if (actionPane.getRootPane() == null) { //说明次面板被释放了。或者关闭了。
					divMap.remove(actionPane);
				}
			}
		}
	}

	public interface DivComponentCustomResetRectListener {
		public void resetComponentDivLocation(JComponent _divComponent);
	}
}

class DivPaneBean {
	private JLayeredPane layeredPane;
	private DivComponentLayoutUtil.DivComponentCustomResetRectListener resetRectListener;
	private JComponent _parentComponent;

	public JComponent get_parentComponent() {
		return _parentComponent;
	}

	public DivComponentLayoutUtil.DivComponentCustomResetRectListener getResetRectListener() {
		return resetRectListener;
	}

	public void setResetRectListener(DivComponentLayoutUtil.DivComponentCustomResetRectListener resetRectListener) {
		this.resetRectListener = resetRectListener;
	}

	public JLayeredPane getLayeredPane() {
		return layeredPane;
	}

	public HashMap<JComponent, Object> getDivComponent() {
		return divComponent;
	}

	private HashMap<JComponent, Object> divComponent = new HashMap<JComponent, Object>(); //存放层面板

	public DivPaneBean(JComponent _parent) throws Exception {
		if (_parent == null) {
			throw new Exception("创建DivPaneBean对象，传入的面板为null");
		}
		JRootPane rootP = _parent.getRootPane();
		if (rootP == null) {
			throw new Exception("创建DivPaneBean对象,传入的面板找不到JRootPane,极有可能是创建JComponent后直接添加Div(找不到容器),如果是在初始化JComponent时添加Div,建议开新线程执行.");
		}
		_parentComponent = _parent;
		layeredPane = rootP.getLayeredPane();
	}

	public void setComponent(JComponent _component, Rectangle _rect) {
		divComponent.put(_component, _rect);
	}

	protected void finalize() throws Throwable {
		System.out.println("DivPaneBean被释放");
		super.finalize();
	}
}