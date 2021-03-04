package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.JTextComponent;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.ImageIconFactory;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.IconFactory;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.mdata.hmui.I_ButtonUI 
 * @Description: 按钮UI
 * @author haoming
 * @date Mar 28, 2013 6:39:21 PM
 *  
*/
public class I_ButtonUI extends BasicButtonUI {
	private Color focusColor = new Color(250, 250, 250, 200); //焦点虚线颜色
	private Color unenableColor = new Color(208, 208, 208);
	private static I_ButtonUI btnUI = null;

	private Insets insets;
	private BasicStroke stroke = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, new float[] { 2.0f }, 0.0f);
	public final static String DEFAULT_LARGE_BUTTON = "LARGE_BUTTON";//大按钮风格
	public final static String CUSTOM_BORDER = "CUSTOM_BORDER"; //用了本UI。会强制把线去掉。如果自定义线

	public static ComponentUI createUI(JComponent c) {
		((AbstractButton) c).setBorderPainted(false);
		if (btnUI == null) {
			btnUI = new I_ButtonUI();
		}
		return btnUI;
	}

	private Icon rightIcon = null; //

	/**
	 * 
	 */
	public I_ButtonUI() {
		this(BackGroundDrawingUtil.VERTICAL_TOP_TO_BOTTOM); //默认是
	}

	public I_ButtonUI(int _directType) {
		this(_directType, Color.WHITE);
	}

	/**
	 * @param int   _direct   指定绘制的方向
	 * @param Color _colorEnd 指定颜色的变化目标（从原本的背景色渐变到参数指定的颜色）
	 */
	public I_ButtonUI(int _directType, Color _colorEnd) {
		super();
	}

	protected void installDefaults(AbstractButton b) {
		super.installDefaults(b);
		if (b.isContentAreaFilled()) {
			b.setOpaque(false);
			b.setBorder(new I_BtnBorder());
			insets = b.getInsets();
			int textwidth = TBUtil.getTBUtil().getStrWidth(b.getText());
			if (b.getIcon() != null) {
				textwidth += b.getIcon().getIconWidth();
			}
			textwidth += 20;
			b.setPreferredSize(new Dimension(textwidth, 23)); //设置系统按钮默认的宽度。
		}
	}

	@Override
	protected void uninstallDefaults(AbstractButton b) {
		super.uninstallDefaults(b);
		insets = null;
	}

	public void paint(Graphics g, JComponent c) {
		Graphics2D g2d = (Graphics2D) g.create();
		AbstractButton button = (AbstractButton) c;
		if (!button.isContentAreaFilled()) {
			super.paint(g, c);
			return;
		}
		if (c.getBorder() instanceof EmptyBorder && !c.isOpaque()) {
			super.paint(g, c);
			return;
		}
		if (c.getClientProperty("CUSTOM_BORDER") == null && !(c.getBorder() instanceof I_BtnBorder)) { //如果默认不是UI中的border，并且没有自定义边框。
			c.setBorder(new I_BtnBorder(c.getInsets()));
		}
		if (button.getText() != null && button.isOpaque()) {
			button.setOpaque(false);
		}
		ButtonModel model = button.getModel();
		//用c.getVisibleRect();会有问题，如果查询面板查询按钮靠右显示，调节窗口大小，
		//出现按钮和查询条件重叠的话，按钮上会有部分是透明的（如果重叠的越多，透明长度越长），说明用显示区域得到的位置不准确！【李春娟/2012-03-02】
		int dx = 0;
		int dy = 0;
		int dw = c.getWidth();
		int dh = c.getHeight();

		if (insets != null) {
			dx += insets.left;
			dy += insets.top;
			dw -= insets.left + insets.right;
			dh -= insets.top + insets.bottom;
		}
		Object o_width = button.getClientProperty("JButton.RefTextField"); //如果是参照的按钮,参照按钮会把后半个背景画出来。 所有后面有x起始位置为-5
		Object o_arrow = button.getClientProperty("JButton.ArrawButton");
		if (o_width != null) { //参照中的按钮
			JTextComponent filed = (JTextComponent) o_width;
			if (filed.hasFocus() && (filed.isEditable() || filed.getBackground() == LookAndFeel.inputbgcolor_enable || filed.getForeground() == LookAndFeel.inputbgcolor_disable) || ("Y".equals(filed.getClientProperty("JTextField.DrawFocusBorder")))) {
				IconFactory.getInstance().getTextFieldBgFocused().draw(g, -5, 0, dw + 7, dh + 2);
			} else if (!model.isEnabled()) { //如果是下拉框按钮并且不可以编辑
				IconFactory.getInstance().getTextFieldBgDisabled().draw(g, -5, 0, dw + 7, dh + 2);
			} else {
				IconFactory.getInstance().getTextFieldBgNormal().draw(g, -5, 0, dw + 7, dh + 2);
			}
			dy++;
			dh -= 2;
		}
		if (!model.isEnabled() && o_arrow == null && o_width == null && (!TBUtil.getTBUtil().isEmpty(button.getText()))) { //不可编辑
			LAFUtil.drawButtonBackground(g2d, dx, dy, dw, dh, 10, unenableColor, 1.0f);
		} else if (o_arrow == null) {//可编辑，不是下拉框
			if ((button.getText() != null && !button.getText().equals(""))) { //
				if (model.isArmed() || model.isPressed()) { //按下去的状态!!
					IconFactory.getInstance().getButtonIcon_PressedOrange().draw(g2d, dx, dy, dw, dh);
				} else {
					g2d.setComposite(AlphaComposite.SrcOver.derive(0.90f));
					if (model.isRollover() || button.hasFocus()) { // 鼠标移动上来
						g2d.setComposite(AlphaComposite.SrcOver.derive(1.0f));
						LAFUtil.drawButtonBackground(g2d, dx, dy, dw, dh, 10, c.getBackground().darker(), 1.0f);
					} else {
						LAFUtil.drawButtonBackground(g2d, dx, dy, dw, dh, 10, c.getBackground(), 1.0f);
					}
				}
			} else if (o_width == null) { //普通的只有图标的按钮,鼠标移动上去时，画边框
				if (model.isRollover()) {
					g2d.setComposite(AlphaComposite.SrcOver.derive(0.5f));
					IconFactory.getInstance().getTextFieldBgNormal().draw(g2d, 0, 0, c.getWidth(), c.getHeight());
				}
			}
		}

		g2d.setComposite(AlphaComposite.SrcOver.derive(1.0f));
		super.paint(g, c); //父类画图!!!
		//在首页的多抽屉页签中有个效果就是在满屏时需要在右边显示一个图片
		if (button.getClientProperty("isRightMax") != null && ((Boolean) button.getClientProperty("isRightMax"))) {
			if (rightIcon == null) {
				rightIcon = ImageIconFactory.getDownEntityArrowIcon(Color.GRAY); //
			}
			rightIcon.paintIcon(c, g, button.getWidth() - 20, 8); //
		}
		g2d.dispose();
	}

	@Override
	protected void paintFocus(Graphics g, AbstractButton button, Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
		super.paintFocus(g, button, viewRect, textRect, iconRect);
		Graphics2D g2d = (Graphics2D) g.create();
		g.setColor(new Color(255, 255, 255, 50));
		if (button.getClientProperty(DEFAULT_LARGE_BUTTON) != null && button.hasFocus()) {
			LAFUtil.setAntiAliasing(g2d, true);
			g2d.setStroke(stroke);
			g2d.setColor(focusColor);
			g2d.drawRect((int) viewRect.getX() + 2, (int) viewRect.getY() + 2, (int) viewRect.getWidth() - 5, (int) viewRect.getHeight() - 5);
			LAFUtil.setAntiAliasing(g2d, false);
		}
		g2d.dispose();
	}

	protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
		AbstractButton b = (AbstractButton) c;
		ButtonModel model = b.getModel();
		if (model.isEnabled() && model.isPressed()) {
			g.setColor(b.getForeground());
			textRect.setLocation((int) textRect.getX() + 1, (int) textRect.getY() + 1); //按下的效果。
		}
		super.paintText(g, c, textRect, text);
	}

	@Override
	protected void paintIcon(Graphics g, JComponent c, Rectangle iconRect) {
		AbstractButton b = (AbstractButton) c;
		ButtonModel model = b.getModel();
		if (model.isEnabled() && model.isPressed()) {
			g.setColor(b.getForeground());
			iconRect.setLocation((int) iconRect.getX() + 1, (int) iconRect.getY() + 1); //按下的效果。
		}
		super.paintIcon(g, c, iconRect);
	}

	@Override
	public Dimension getMaximumSize(JComponent c) {
		Dimension mindim = super.getMinimumSize(c);
		if (mindim.getHeight() < 20) { //小于20就出现按钮异型。
			mindim.setSize(mindim.getWidth(), 20);
		}
		return mindim;
	}
}

class I_BtnBorder extends AbstractBorder {
	Insets inset = new Insets(1, 1, 1, 1);

	public I_BtnBorder() {
		super();
	}

	public I_BtnBorder(Insets _insets) {
		inset = _insets;
	}

	public Insets getBorderInsets(Component c) {
		AbstractButton b = (AbstractButton) c;
		if (b.getClientProperty("JButton.RefTextField") != null || b.getClientProperty("JButton.ArrawButton") != null) {
			inset.set(1, 0, 1, 2);
		}
		return inset;
	};
}
