package cn.com.infostrategy.ui.common;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.Scrollable;
import javax.swing.border.Border;

/**
 * 滚动条不占实际空间的滚动条，浮动。
 * @author hm
 *
 */
public class I_MyJScrollPane extends JScrollPane {
	public I_MyJScrollPane() {
		this(null, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}

	public I_MyJScrollPane(Component view, int vsbPolicy, int hsbPolicy) {
		setLayout(new I_ScrollPaneLayout());
		setVerticalScrollBarPolicy(vsbPolicy);
		setHorizontalScrollBarPolicy(hsbPolicy);
		setHorizontalScrollBar(createHorizontalScrollBar());
		setViewport(createViewport()); //把添加bar和view的添加顺序修改
		if (view != null) {
			setViewportView(view);
		}
		setOpaque(false);
		updateUI();
		if (!this.getComponentOrientation().isLeftToRight()) {
			viewport.setViewPosition(new Point(Integer.MAX_VALUE, 0));
		}
		this.getVerticalScrollBar().setUnitIncrement(15);
		this.getHorizontalScrollBar().setUnitIncrement(15);
	}

	public I_MyJScrollPane(Component view) {
		this(view, VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_AS_NEEDED);
	}
}

class I_MyJscrollPaneLayout implements LayoutManager2 {
	Component c;
	JScrollBar bar;

	public void addLayoutComponent(Component comp, Object constraints) {
		if (comp instanceof JScrollBar) {
			bar = (JScrollBar) comp;
		} else {
			c = comp;
		}
	}

	public float getLayoutAlignmentX(Container target) {
		System.out.println("2");
		return 0;
	}

	public float getLayoutAlignmentY(Container target) {
		// TODO Auto-generated method stub
		System.out.println("3");
		return 0;
	}

	public void invalidateLayout(Container target) {
		System.out.println("" + target.getInsets().left);
		System.out.println(target.getInsets().right);
		c.setPreferredSize(new Dimension(target.getInsets().right - target.getInsets().left, target.getHeight()));
		System.out.println("4");

	}

	public Dimension maximumLayoutSize(Container target) {
		// TODO Auto-generated method stub
		System.out.println("5");
		return null;
	}

	public void addLayoutComponent(String name, Component comp) {
		// TODO Auto-generated method stub
		System.out.println("6");
	}

	public void layoutContainer(Container target) {
		I_MyJScrollPane myScrollPanel = (I_MyJScrollPane) target;
		Rectangle vsbR = new Rectangle(0, 0, 0, 0);

		Rectangle extentSize = new Rectangle(0, 0, 0, 0);

		Rectangle availR = myScrollPanel.getBounds();

		boolean isEmpty = (availR.width < 0 || availR.height < 0);
		boolean vsbNeeded;
		Dimension viewPrefSize = (c != null) ? c.getPreferredSize() : new Dimension(0, 0);
		if (isEmpty) {
			vsbNeeded = false;
		} else { // vsbPolicy == VERTICAL_SCROLLBAR_AS_NEEDED
			vsbNeeded = (viewPrefSize.height > extentSize.height);
		}

		bar.setBounds(target.getWidth() - 15, 0, 15, target.getHeight());
		c.setBounds(0, 0, target.getWidth(), target.getHeight());
		System.out.println("7");
	}

	public Dimension minimumLayoutSize(Container parent) {
		// TODO Auto-generated method stub
		System.out.println("8");
		return new Dimension(4, 4);
	}

	public Dimension preferredLayoutSize(Container target) {
		return null;
	}

	public void removeLayoutComponent(Component comp) {
		// TODO Auto-generated method stub
		System.out.println("8");
	}

}

/**
 * 
 * @author hm
 *
 */
class I_ScrollPaneLayout extends ScrollPaneLayout {
	public void layoutContainer(Container parent) {
		JScrollPane scrollPane = (JScrollPane) parent;
		vsbPolicy = scrollPane.getVerticalScrollBarPolicy();
		hsbPolicy = scrollPane.getHorizontalScrollBarPolicy();

		Rectangle availR = scrollPane.getBounds();
		availR.x = availR.y = 0;

		Insets insets = parent.getInsets();
		availR.x = insets.left;
		availR.y = insets.top;
		availR.width -= insets.left + insets.right;
		availR.height -= insets.top + insets.bottom;
		boolean leftToRight = true;

		Rectangle colHeadR = new Rectangle(0, availR.y, 0, 0);

		if ((colHead != null) && (colHead.isVisible())) {
			int colHeadHeight = Math.min(availR.height, colHead.getPreferredSize().height);
			colHeadR.height = colHeadHeight;
			availR.y += colHeadHeight;
			availR.height -= colHeadHeight;
		}

		Rectangle rowHeadR = new Rectangle(0, 0, 0, 0);

		if ((rowHead != null) && (rowHead.isVisible())) {
			int rowHeadWidth = Math.min(availR.width, rowHead.getPreferredSize().width);
			rowHeadR.width = rowHeadWidth;
			availR.width -= rowHeadWidth;
			if (leftToRight) {
				rowHeadR.x = availR.x;
				availR.x += rowHeadWidth;
			} else {
				rowHeadR.x = availR.x + availR.width;
			}
		}

		Border viewportBorder = scrollPane.getViewportBorder();
		Insets vpbInsets;
		if (viewportBorder != null) {
			vpbInsets = viewportBorder.getBorderInsets(parent);
			availR.x += vpbInsets.left;
			availR.y += vpbInsets.top;
			availR.width -= vpbInsets.left + vpbInsets.right;
			availR.height -= vpbInsets.top + vpbInsets.bottom;
		} else {
			vpbInsets = new Insets(0, 0, 0, 0);
		}

		Component view = (viewport != null) ? viewport.getView() : null;
		Dimension viewPrefSize = (view != null) ? view.getPreferredSize() : new Dimension(0, 0);

		Dimension extentSize = (viewport != null) ? viewport.toViewCoordinates(availR.getSize()) : new Dimension(0, 0);

		boolean viewTracksViewportWidth = false;
		boolean viewTracksViewportHeight = false;
		boolean isEmpty = (availR.width < 0 || availR.height < 0);
		Scrollable sv;
		if (!isEmpty && view instanceof Scrollable) {
			sv = (Scrollable) view;
			viewTracksViewportWidth = sv.getScrollableTracksViewportWidth();
			viewTracksViewportHeight = sv.getScrollableTracksViewportHeight();
		} else {
			sv = null;
		}

		Rectangle vsbR = new Rectangle(0, availR.y - vpbInsets.top, 0, 0);

		boolean vsbNeeded;
		if (isEmpty) {
			vsbNeeded = false;
		} else if (vsbPolicy == VERTICAL_SCROLLBAR_ALWAYS) {
			vsbNeeded = true;
		} else if (vsbPolicy == VERTICAL_SCROLLBAR_NEVER) {
			vsbNeeded = false;
		} else { // vsbPolicy == VERTICAL_SCROLLBAR_AS_NEEDED
			vsbNeeded = !viewTracksViewportHeight && (viewPrefSize.height > extentSize.height);
		}

		if ((vsb != null) && vsbNeeded) { //纵向滚动条显示
			adjustForVSB(true, availR, vsbR, vpbInsets, leftToRight);
//			extentSize = viewport.toViewCoordinates(availR.getSize());
		}
		Rectangle hsbR = new Rectangle(availR.x - vpbInsets.left, 0, 0, 0);
		boolean hsbNeeded;
		if (isEmpty) {
			hsbNeeded = false;
		} else if (hsbPolicy == HORIZONTAL_SCROLLBAR_ALWAYS) {
			hsbNeeded = true;
		} else if (hsbPolicy == HORIZONTAL_SCROLLBAR_NEVER) {
			hsbNeeded = false;
		} else { // hsbPolicy == HORIZONTAL_SCROLLBAR_AS_NEEDED
			hsbNeeded = !viewTracksViewportWidth && (viewPrefSize.width > extentSize.width);
		}

		if ((hsb != null) && hsbNeeded) {
			adjustForHSB(true, availR, hsbR, vpbInsets);
			if ((vsb != null) && !vsbNeeded && (vsbPolicy != VERTICAL_SCROLLBAR_NEVER)) {

//								extentSize = viewport.toViewCoordinates(availR.getSize());
				vsbNeeded = viewPrefSize.height > extentSize.height;
				extentSize.setSize(extentSize.getWidth(), viewPrefSize.height);
				if (vsbNeeded) {
					adjustForVSB(true, availR, vsbR, vpbInsets, leftToRight);
				}
			}
		}

		if (viewport != null) {
		}
		vsbR.height = availR.height + vpbInsets.top + vpbInsets.bottom;
		hsbR.width = availR.width + vpbInsets.left + vpbInsets.right;
		rowHeadR.height = availR.height + vpbInsets.top + vpbInsets.bottom;
		rowHeadR.y = availR.y - vpbInsets.top;
		colHeadR.width = availR.width + vpbInsets.left + vpbInsets.right;
		colHeadR.x = availR.x - vpbInsets.left;

		if (rowHead != null) {
			rowHead.setBounds(rowHeadR);
		}

		if (colHead != null) {
			colHead.setBounds(colHeadR);
		}
		int vsbWidth = Math.max(0, Math.min(vsb.getPreferredSize().width, availR.width));
		if (vsb != null) {
			if (vsbNeeded) {
				vsb.setVisible(true);
				availR.width += vsbWidth;
				vsb.setBounds(vsbR);
			} else {
				vsb.setVisible(false);
			}
		}

		if (hsb != null) {
			if (hsbNeeded) {
				hsb.setVisible(true);
				availR.height += vsbWidth;
				hsb.setBounds(hsbR);
			} else {
				hsb.setVisible(false);
			}
		}
		viewport.setBounds(availR);
	}

	//调整纵向大小。
	private void adjustForVSB(boolean wantsVSB, Rectangle available, Rectangle vsbR, Insets vpbInsets, boolean leftToRight) {
		int oldWidth = vsbR.width;
		if (wantsVSB) {
			int vsbWidth = Math.max(0, Math.min(vsb.getPreferredSize().width, available.width));

			available.width -= vsbWidth;
			vsbR.width = vsbWidth;

			if (leftToRight) {
				vsbR.x = available.x + available.width + vpbInsets.right;
			} else {
				vsbR.x = available.x - vpbInsets.left;
				available.x += vsbWidth;
			}
		} else {
			available.width += oldWidth;
		}
	}

	private void adjustForHSB(boolean wantsHSB, Rectangle available, Rectangle hsbR, Insets vpbInsets) {
		int oldHeight = hsbR.height;
		if (wantsHSB) {
			int hsbHeight = Math.max(0, Math.min(available.height, hsb.getPreferredSize().height));

			available.height -= hsbHeight;
			hsbR.y = available.y + available.height + vpbInsets.bottom;
			hsbR.height = hsbHeight;
		} else {
			available.height += oldHeight;
		}
	}
}
