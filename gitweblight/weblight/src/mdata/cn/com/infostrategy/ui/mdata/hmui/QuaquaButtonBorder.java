/*
 * @(#)QuaquaButtonBorder.java  1.4.1  2007-12-18
 *
 * Copyright (c) 2005-2007 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;

import cn.com.infostrategy.ui.mdata.hmui.util.Images;


/**
 * QuaquaButtonBorder.
 * This border uses client properties and font sizes of a JComponent to
 * determine which style the border shall have.
 * For some styles, the JComponent should honour size constrictions.
 * <p>
 * The following values of the client property <code>Quaqua.Button.style</code>
 * are supported:
 * <ul>
 * <li><code>push</code> Rounded push button. Maximum height of the JComponent
 * shall be constrained to its preferred height.</li>
 * <li><code>square</code> Square button. No size constraints.</li>
 * <li><code>placard</code> Placard button. No size constraints.</li>
 * <li><code>colorWell</code> Square button with color area in the center.
 * No size constraints.</li>
 * <li><code>bevel</code> Rounded Bevel button. No size constraints.</li>
 * <li><code>toggle</code> Toggle button. Maximum height of the JComponent
 * shall be constrained to its preferred height.</li>
 * <li><code>toggleWest</code> West Toggle button. Maximum height of the JComponent
 * shall be constrained to its preferred height.</li>
 * <li><code>toggleEast</code> East Toggle button. Maximum height of the JComponent
 * shall be constrained to its preferred height.</li>
 * <li><code>toggleCenter</code> Center Toggle button. Maximum height of the JComponent
 * shall be constrained to its preferred height.</li>
 * <li><code>toolBar</code> ToolBar button. No size constraints.</li>
 * <li><code>toolBarTab</code> ToolBar Tab button. No size constraints.</li>
 * <li><code>toolBarRollover</code> ToolBar button with rollover effect. No size constraints.</li>
 * </ul>
 * If the <code>Quaqua.Button.style</code> property is missing, then the
 * following values of the client property <code>JButton.buttonType</code>
 * are supported:
 * <ul>
 * <li><code>text</code> Rounded push button. Maximum height of the JComponent
 * shall be constrained to its preferred height.</li>
 * <li><code>toolBar</code> Square button. No size constraints.</li>
 * <li><code>icon</code> Rounded Bevel button. No size constraints.</li>
 * </ul>
 *
 * @author  Werner Randelshofer
 * @version 1.5 2007-12-18 Fixed visual margins. 
 * <br>1.4 2006-12-24 by Karl von Randow: Use Images class to create artwork.
 * <br>1.2 2005-01-04 "placard" style added. Tweaked visual margin of
 * regular push button and of small push button.
 * <br>1.1 2005-09-06 Create all borders lazily.
 * <br>1.1 2005-09-04 "help" style added.
 * <br>1.0.2 2005-05-06 Fixed "toolbar" property.
 * <br>1.0  29 March 2005  Created.
 */
public class QuaquaButtonBorder implements Border, UIResource {
    // Shared borders
    private static Border regularPushButtonBorder;
    private static Border toolBarBorder;
    private String defaultStyle;

    /** Creates a new instance. */
    public QuaquaButtonBorder(String defaultStyle) {
        this.defaultStyle = defaultStyle;
    }

    public Border getBorder(JComponent c) {
        Border b = null;
        String style = getStyle(c);

        b = getRegularPushButtonBorder();
        if (b == null) {
            throw new InternalError(style);
        }
        return b;
    }

    private Border getRegularPushButtonBorder() {
        if (regularPushButtonBorder == null) {
            Insets borderInsets = new Insets(3, 8, 3, 8);
            BufferedImage[] imageFrames = Images.split(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/Button.default.png")),
                    12, true);
            Border[] borderFrames = new Border[12];
            for (int i = 0; i < 12; i++) {
                borderFrames[i] = QuaquaBorderFactory.create(
                        imageFrames[i],
                        new Insets(11, 13, 13, 13),
                        borderInsets,
                        true);
            }
            ButtonStateBorder buttonStateBorder = new ButtonStateBorder(
                    Images.split(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/Button.borders.png")),
                    10, true),
                    new Insets(11, 13, 13, 13),
                    borderInsets,
                    true);

            buttonStateBorder.setBorder(
                    ButtonStateBorder.DEFAULT,
                    new AnimatedBorder(borderFrames, 100));

            regularPushButtonBorder = new CompoundBorder(
                    //new VisualMargin(2, 3, 2, 3),
                    new VisualMargin(0, 0, 0, 0),
                    new CompoundBorder(
                    new EmptyBorder(-2, -4, -2, -4),
                    new OverlayBorder(
                    buttonStateBorder,
                    new FocusBorder(
                    QuaquaBorderFactory.create(
                    Images.createImage(QuaquaButtonBorder.class.getResource("images/Button.focusRing.png")),
                    new Insets(12, 13, 12, 13),
                    borderInsets,
                    false)))));
        }
        return regularPushButtonBorder;
    }


    /**
     * Returns the default button margin for the specified component.
     *
     * FIXME: We should not create a new Insets instance on each method call.
     */
    public Insets getDefaultMargin(JComponent c) {
        Insets margin = null;
        String style = getStyle(c);
        boolean isSmall = c.getFont().getSize() <= 11;

        // Explicitly chosen styles
        if (style.equals("text") || style.equals("push")) {
            if (isSmall) {
                margin = new Insets(1, 4, 1, 4);
            } else {
                margin = new Insets(1, 8, 2, 8);
            }
        } else if (style.equals("toolBar")) {
            margin = new Insets(0, 0, 0, 0);
        } else if (style.equals("toolBarRollover")) {
            margin = new Insets(0, 0, 0, 0);
        } else if (style.equals("toolBarTab")) {
            margin = new Insets(0, 0, 0, 0);
        } else if (style.equals("square")) {
            if (isSmall) {
                margin = new Insets(1, 6, 1, 6);
            } else {
                margin = new Insets(1, 6, 2, 6);
            }
        } else if (style.equals("placard")) {
            if (isSmall) {
                margin = new Insets(1, 6, 1, 6);
            } else {
                margin = new Insets(1, 6, 2, 6);
            }
        } else if (style.equals("colorWell")) {
            if (isSmall) {
                margin = new Insets(1, 6, 1, 6);
            } else {
                margin = new Insets(1, 6, 2, 6);
            }
        } else if (style.equals("icon") || style.equals("bevel")) {
            if (isSmall) {
                margin = new Insets(1, 6, 1, 6);
            } else {
                margin = new Insets(1, 6, 2, 6);
            }
        } else if (style.equals("toggle")) {
            if (isSmall) {
                margin = new Insets(1, 5, 1, 5);
            } else {
                margin = new Insets(1, 5, 2, 5);
            }
        } else if (style.equals("toggleEast")) {
            if (isSmall) {
                margin = new Insets(1, 5, 1, 5);
            } else {
                margin = new Insets(1, 5, 2, 5);
            }
        } else if (style.equals("toggleCenter")) {
            if (isSmall) {
                margin = new Insets(1, 5, 1, 5);
            } else {
                margin = new Insets(1, 5, 2, 5);
            }
        } else if (style.equals("toggleWest")) {
            if (isSmall) {
                margin = new Insets(1, 5, 1, 5);
            } else {
                margin = new Insets(1, 5, 2, 5);
            }
        } else if (style.equals("help")) {
            margin = new Insets(0, 0, 0, 0);

        // Implicit styles
        } else if (c.getParent() instanceof JToolBar) {
            margin = new Insets(0, 0, 0, 0);
        } else {
            if (isSmall) {
                margin = new Insets(1, 4, 1, 4);
            } else {
                margin = new Insets(1, 8, 2, 8);
            }
        }
        return margin;
    }

    public boolean isFixedHeight(JComponent c) {
        String style = getStyle(c).toLowerCase();
        return style.equals("text") || style.equals("push") || style.startsWith("toggle");
    }

    protected String getStyle(JComponent c) {
        String style = (String) c.getClientProperty("Quaqua.Button.style");
        if (style == null) {
            style = (String) c.getClientProperty("JButton.buttonType");
        }
        if (style == null) {
            style = defaultStyle;
        }
        return style;
    }

    /**
     * Returns true, if this border has a visual cue for the pressed
     * state of the button.
     * If the border has no visual cue, then the ButtonUI has to provide
     * it by some other means.
     */
    public boolean hasPressedCue(JComponent c) {
        Border b = getBorder(c);
        return b != toolBarBorder;
    }

    public Insets getVisualMargin(Component c) {
        Border b = getBorder((JComponent) c);
        Insets visualMargin = new Insets(0, 0, 0, 0);

        if (b instanceof VisualMargin) {
            visualMargin = ((VisualMargin) b).getVisualMargin(c);
        } else if (b instanceof CompoundBorder) {
            b = ((CompoundBorder) b).getOutsideBorder();
            if (b instanceof VisualMargin) {
                visualMargin = ((VisualMargin) b).getVisualMargin(c);
            }
        }
        return visualMargin;
    }

    /**
     * Returns true, if this border has a visual cue for the disabled
     * state of the button.
     * If the border has no visual cue, then the ButtonUI has to provide
     * it by some other means.
     * /
     * public boolean hasDisabledCue(JComponent c) {
     * return false;
     * }*/
    public Insets getBorderInsets(Component c) {
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            Insets insets = (Insets) jc.getClientProperty("Quaqua.Border.insets");
            if (insets != null) {
                return (Insets) insets.clone();
            }
        }

        boolean isBorderPainted = true;
        if (c instanceof AbstractButton) {
            isBorderPainted = ((AbstractButton) c).isBorderPainted();
        }
        Insets insets;
        if (!isBorderPainted) {
            insets = (Insets) UIManager.getInsets("Component.visualMargin").clone();
        } else {
            insets = getBorder((JComponent) c).getBorderInsets(c);
            if (c instanceof AbstractButton) {
                AbstractButton b = (AbstractButton) c;
                Insets margin = b.getMargin();
                if (margin == null || (margin instanceof UIResource)) {
                    margin = getDefaultMargin((JComponent) c);
                }
                if (margin != null) {
                    insets.top += margin.top;
                    insets.left += margin.left;
                    insets.bottom += margin.bottom;
                    insets.right += margin.right;
                }
            }
        }
        return insets;
    }

    public boolean isBorderOpaque() {
        return false;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        getBorder((JComponent) c).paintBorder(c, g, x, y, width, height);
    }
}
