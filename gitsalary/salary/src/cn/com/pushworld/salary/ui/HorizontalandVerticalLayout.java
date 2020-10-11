package cn.com.pushworld.salary.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.io.Serializable;
/**
 * 水平居中布局
 * @author haoming
 * create by 2014-1-9
 */
public class HorizontalandVerticalLayout implements LayoutManager2,Serializable {
	private static final long serialVersionUID = -7422827760499919014L;
	private Component com = null;
	public void addLayoutComponent(Component component, Object obj) {
		
	}

	public float getLayoutAlignmentX(Container container) {
		return 0;
	}

	public float getLayoutAlignmentY(Container container) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void invalidateLayout(Container container) {
		// TODO Auto-generated method stub

	}

	public Dimension maximumLayoutSize(Container container) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addLayoutComponent(String s, Component component) {
		// TODO Auto-generated method stub

	}

	public void layoutContainer(Container container) {
		synchronized (container.getTreeLock()) {
			int w = container.getWidth();
			int h = container.getHeight();
			com = container.getComponent(0);
			int cw = com.getWidth();
			int ch = com.getHeight();
			int x = (w-cw)/2;
			int y =(h-ch)/2;
			com.setBounds(x, y, cw, ch);
		}

	}

	public Dimension minimumLayoutSize(Container container) {
		// TODO Auto-generated method stub
		return new Dimension(0, 0);
	}

	public Dimension preferredLayoutSize(Container container) {
		// TODO Auto-generated method stub
		return new Dimension(0, 0);
	}

	public void removeLayoutComponent(Component component) {
		// TODO Auto-generated method stub

	}

}
