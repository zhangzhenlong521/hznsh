/*
 * @(#)QuaquaUtilities.java  4.1.1  2008-01-24
 *
 * Copyright (c) 2003-2008 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */
package cn.com.infostrategy.ui.mdata.hmui.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.text.View;

/**
 * Utility class for the Quaqua LAF.
 *
 * @author Werner Randelshofer, Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * @version 4.1 2007-11-10 Added method adjustFocus and shouldIgnore from
 * SwingUtilities2.
 * <br>4.0 2007-11-01 Rewrote method isFocused for Java 1.4 or higher. 
 * <br>3.6 2007-08-01 Method isFocused did not always work as expected.
 * <br>3.5 2007-03-04 Added support for client property
 * "Quaqua.Component.cellRendererFor".
 * <br>3.4 2007-02-27 Added method getUIOfType.
 * <br>3.3 2007-02-10 Method installProperty added.
 * <br>3.2 2007-01-18 Changed method isFocused to return true, if the
 * component is focusOwner or permanentFocusOwner.
 * <br>3.1 2006-09-04 Added method compositeRequestFocus.
 * <br>3.0.5 2006-08-20 Method endGraphics must not set
 * KEY_TEXT_ANTIALIASING to null.
 * <br>3.0.4 2006-02-19 Catch Throwable in method setWindowAlpha instead
 * of catching NoSuchMethodException.
 * <br>3.0.3 2006-01-08 Don't set Window alpha, when running on
 * Java 1.4.2_05 on Mac OS X 10.3.5. Because this only has the effect of turning
 * the background color of the Window to white.
 * <br>3.0.2 2005-12-10 Method isOnActiveWindow() did not reliably
 * return true.
 * <br>3.0.1 2005-11-12 Fixed NPE in method repaint border.
 * <br>3.0 2005-09-24 Removed all reflection helper methods. Moved Sheet
 * helper methods out into class Sheets.
 * <br>2.6 2005-09-17 Method isOnFocusedWindow returns true, if
 * the window returns false on "getFocusableWindowState".
 * <br>2.5 2005-03-13 Renamed method isFrameActive to isOnActiveFrame.
 * <br>2.4 2004-12-28 Method createBufferdImage added. Method
 * isOnActiveWindow() renamed to isFrameActive().
 * <br>2.3 2004-12-14 Method getUI added.
 * <br>2.2.1 2004-12-01 Methods setDragEnabled and getDragEnabled never
 * worked because the attempted to get method objects on the wrong class.
 * <br>2.2 2004-09-19 Refined algorithm of method isFrameActive.
 * <br>2.1 2004-07-04 Methods repaintBorder, beginFont, endFont and
 * isFocused added.
 * <br>2.0 2004-04-27 Renamed from QuaquaGraphicUtils to QuaquaUtilities.
 * Added method isFrameActive(Component).
 * <br>1.1.1 2003-10-08 Diagnostic output to System.out removed.
 * <br>1.1 2003-10-05 Methods getModifiersText and getModifiersUnicode
 * added.
 * <br>1.0 2003-07-19 Created.
 */
public class QuaquaUtilities extends BasicGraphicsUtils implements SwingConstants {

    private final static boolean DEBUG = false;
    /**
     * This is set to false, if we fail to install properties
     * directly.
     */
    private static boolean canInstallProperty = true;

    /** Prevent instance creation. */
    private QuaquaUtilities() {
    }

    /*
     * Convenience function for determining ComponentOrientation.  Helps us
     * avoid having Munge directives throughout the code.
     */
    public static boolean isLeftToRight(Component c) {
        return c.getComponentOrientation().isLeftToRight();
    }

    /**
     * Draw a string with the graphics <code>g</code> at location
     * (<code>x</code>, <code>y</code>)
     * just like <code>g.drawString</code> would.
     * The character at index <code>underlinedIndex</code>
     * in text will be underlined. If <code>index</code> is beyond the
     * bounds of <code>text</code> (including < 0), nothing will be
     * underlined.
     *
     * @param g Graphics to draw with
     * @param text String to draw
     * @param underlinedIndex Index of character in text to underline
     * @param x x coordinate to draw at
     * @param y y coordinate to draw at
     * @since 1.4
     */
    public static void drawStringUnderlineCharAt(Graphics g, String text,
            int underlinedIndex, int x, int y) {
        g.drawString(text, x, y);
        if (underlinedIndex >= 0 && underlinedIndex < text.length()) {
            FontMetrics fm = g.getFontMetrics();
            int underlineRectX = x + fm.stringWidth(text.substring(0, underlinedIndex));
            int underlineRectY = y;
            int underlineRectWidth = fm.charWidth(text.charAt(underlinedIndex));
            int underlineRectHeight = 1;
            g.fillRect(underlineRectX, underlineRectY + fm.getDescent() - 1,
                    underlineRectWidth, underlineRectHeight);
        }
    }

    /**
     * Returns index of the first occurrence of <code>mnemonic</code>
     * within string <code>text</code>. Matching algorithm is not
     * case-sensitive.
     *
     * @param text The text to search through, may be null
     * @param mnemonic The mnemonic to find the character for.
     * @return index into the string if exists, otherwise -1
     */
    static int findDisplayedMnemonicIndex(String text, int mnemonic) {
        if (text == null || mnemonic == '\0') {
            return -1;
        }

        char uc = Character.toUpperCase((char) mnemonic);
        char lc = Character.toLowerCase((char) mnemonic);

        int uci = text.indexOf(uc);
        int lci = text.indexOf(lc);

        if (uci == -1) {
            return lci;
        } else if (lci == -1) {
            return uci;
        } else {
            return (lci < uci) ? lci : uci;
        }
    }

    /**
     * Returns true if the component is on a Dialog or a Frame, which is active,
     * or if it is on a Window, which is focused.
     * Always returns true, if the component has no parent window.
     */
    public static boolean isOnActiveWindow(Component c) {
        // In the RootPaneUI, we set a client property on the whole component
        // tree, if the ancestor Frame gets activated or deactivated.
        if (c instanceof JComponent) {
            Boolean value = (Boolean) ((JComponent) c).getClientProperty("Frame.active");
            // Unfortunately, the value is not always reliable.
            // Therefore we can only do a short circuit, if the value is true.
            if (value != null && value.booleanValue()) {
                return true;
            //return value.booleanValue();
            }
        }

        Window window = SwingUtilities.getWindowAncestor(c);
        boolean isOnActiveWindow;
        if (window == null) {
            isOnActiveWindow = true;
        } else if ((window instanceof Frame) || (window instanceof Dialog)) {
            isOnActiveWindow = window.isActive();
        } else {
            if (window.getFocusableWindowState()) {
                isOnActiveWindow = window.isFocused();
            } else {
                isOnActiveWindow = true;
            }
        }

        // In case the activation property is true, we fix the value of the
        // client property, so that we can do a short circuit next time.
        if (isOnActiveWindow && (c instanceof JComponent)) {
            ((JComponent) c).putClientProperty("Frame.active", new Boolean(isOnActiveWindow));
        }
        return isOnActiveWindow;
    }

    /**
     * Returns a Mac OS X specific String describing the modifier key(s),
     * such as "Shift", or "Ctrl+Shift".
     *
     * @return string a text description of the combination of modifier
     *                keys that were held down during the event
     */
    public static String getKeyModifiersText(int modifiers, boolean leftToRight) {
        return getKeyModifiersUnicode(modifiers, leftToRight);
    }

    static String getKeyModifiersUnicode(int modifiers, boolean leftToRight) {
        char[] cs = new char[4];
        int count = 0;
        if (leftToRight) {
            if ((modifiers & InputEvent.CTRL_MASK) != 0) {
                cs[count++] = '\u2303';
            } // Unicode: UP ARROWHEAD
            if ((modifiers & (InputEvent.ALT_MASK | InputEvent.ALT_GRAPH_MASK)) != 0) {
                cs[count++] = '\u2325';
            } // Unicode: OPTION KEY
            if ((modifiers & InputEvent.SHIFT_MASK) != 0) {
                cs[count++] = '\u21e7';
            } // Unicode: UPWARDS WHITE ARROW
            if ((modifiers & InputEvent.META_MASK) != 0) {
                cs[count++] = '\u2318';
            } // Unicode: PLACE OF INTEREST SIGN
        } else {
            if ((modifiers & InputEvent.META_MASK) != 0) {
                cs[count++] = '\u2318';
            } // Unicode: PLACE OF INTEREST SIGN
            if ((modifiers & InputEvent.SHIFT_MASK) != 0) {
                cs[count++] = '\u21e7';
            } // Unicode: UPWARDS WHITE ARROW
            if ((modifiers & (InputEvent.ALT_MASK | InputEvent.ALT_GRAPH_MASK)) != 0) {
                cs[count++] = '\u2325';
            } // Unicode: OPTION KEY
            if ((modifiers & InputEvent.CTRL_MASK) != 0) {
                cs[count++] = '\u2303';
            } // Unicode: UP ARROWHEAD
        }
        return new String(cs, 0, count);
    }

    public static void repaintBorder(JComponent component) {
        JComponent c = component;
        Border border = null;
        Container container = component.getParent();
        if (container instanceof JViewport) {
            c = (JComponent) container.getParent();
            if (c != null) {
                border = c.getBorder();
            }
        }
        if (border == null) {
            border = component.getBorder();
            c = component;
        }
        if (border != null && c != null) {
            int w = c.getWidth();
            int h = c.getHeight();
            Insets insets = c.getInsets();
            c.repaint(0, 0, w, insets.top);
            c.repaint(0, insets.top, insets.left, h - insets.bottom - insets.top);
            c.repaint(0, h - insets.bottom, w, insets.bottom);
            c.repaint(w - insets.right, insets.top, insets.right, h - insets.bottom - insets.top);
        }
    }

    public static final Object beginGraphics(Graphics2D graphics2d) {
        Object object = graphics2d.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);

        /*
        AffineTransform tx = graphics2d.getTransform();
        AffineTransform savedTransform = (AffineTransform) tx.clone();
        tx.scale(0.9,0.9);
        graphics2d.setTransform(tx);
         */
        graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //if (true) return savedTransform;
        return object;
    }

    public static final void endGraphics(Graphics2D graphics2d, Object oldHints) {
        /*
        if (true) {
        graphics2d.setTransform((AffineTransform) oldHints);
        return;
        }*/

        if (oldHints != null) {
            graphics2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    oldHints);
        }
    }

    /**
     * Returns true, if the specified component is focus owner or permanent
     * focus owner and if the component is on an the active window.
     */
    public static final boolean isFocused(Component component) {
        // When a component is used as a cell renderer, the focus can
        // not be determined from the component itself.
        if (component instanceof JComponent) {
            if (((JComponent) component).getClientProperty("Quaqua.Component.cellRendererFor") != null) {
                component = (Component) ((JComponent) component).getClientProperty("Quaqua.Component.cellRendererFor");
            }
        }

        //---
        try {
            boolean isFocusOwner = ((Boolean) Methods.invoke(component, "isFocusOwner")).booleanValue();

            Window ancestor = SwingUtilities.getWindowAncestor(component);
            Object kfm = Methods.invokeStatic("java.awt.KeyboardFocusManager", "getCurrentKeyboardFocusManager");

            return isFocusOwner ||
                    component == Methods.invoke(kfm, "getPermanentFocusOwner") &&
                    ancestor != null &&
                    Methods.invokeGetter(ancestor, "isFocused", false);
        } catch (NoSuchMethodException e) {
            return component.hasFocus();
        }


    }

    static boolean isHeadless() {
        return Methods.invokeStaticGetter(GraphicsEnvironment.class, "isHeadless", false);
    }

    public static int getLeftSideBearing(Font f, String string) {
        return ((Integer) Methods.invokeStatic(
                "com.sun.java.swing.SwingUtilities2", "getLeftSideBearing",
                new Class[]{Font.class, String.class}, new Object[]{f, string},
                new Integer(0))).intValue();
    }

    /**
     * Invoked when the user attempts an invalid operation,
     * such as pasting into an uneditable <code>JTextField</code>
     * that has focus. The default implementation beeps. Subclasses
     * that wish different behavior should override this and provide
     * the additional feedback.
     *
     * @param component Component the error occured in, may be null
     *			indicating the error condition is not directly
     *			associated with a <code>Component</code>.
     */
    static void provideErrorFeedback(Component component) {
        Toolkit toolkit = null;
        if (component != null) {
            toolkit = component.getToolkit();
        } else {
            toolkit = Toolkit.getDefaultToolkit();
        }
        toolkit.beep();
    } // provideErrorFeedback()

    public static BufferedImage createBufferedImage(URL location) {
        Image image = Toolkit.getDefaultToolkit().createImage(location);
        BufferedImage buf;
        if (image instanceof BufferedImage) {
            buf = (BufferedImage) image;
        } else {
            loadImage(image);
            //buf = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
            buf = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(image.getWidth(null), image.getHeight(null), Transparency.OPAQUE);

            Graphics g = buf.getGraphics();
            g.drawImage(image, 0, 0, null);
            g.dispose();
            image.flush();
        }
        return buf;
    }

    public static TexturePaint createTexturePaint(URL location) {
        BufferedImage texture = createBufferedImage(location);
        TexturePaint paint = new TexturePaint(texture, new Rectangle(0, 0, texture.getWidth(), texture.getHeight()));
        return paint;
    }

    /**
     * Loads the image, returning only when the image is loaded.
     * @param image the image
     */
    private static void loadImage(Image image) {
        Component component = new Component() {
        };
        MediaTracker tracker = new MediaTracker(component);
        synchronized (tracker) {
            int id = 0;

            tracker.addImage(image, id);
            try {
                tracker.waitForID(id, 0);
            } catch (InterruptedException e) {
                if (DEBUG) {
                    System.out.println("INTERRUPTED while loading Image");
                }
            }
            int loadStatus = tracker.statusID(id, false);
            tracker.removeImage(image, id);
        }
    }

    /**
     * Compute and return the location of the icons origin, the
     * location of origin of the text baseline, and a possibly clipped
     * version of the compound labels string.  Locations are computed
     * relative to the viewR rectangle.
     * The JComponents orientation (LEADING/TRAILING) will also be taken
     * into account and translated into LEFT/RIGHT values accordingly.
     */
    public static String layoutCompoundLabel(JComponent c,
            FontMetrics fm,
            String text,
            Icon icon,
            int verticalAlignment,
            int horizontalAlignment,
            int verticalTextPosition,
            int horizontalTextPosition,
            Rectangle viewR,
            Rectangle iconR,
            Rectangle textR,
            int textIconGap) {
        boolean orientationIsLeftToRight = true;
        int hAlign = horizontalAlignment;
        int hTextPos = horizontalTextPosition;

        if (c != null) {
            if (!(c.getComponentOrientation().isLeftToRight())) {
                orientationIsLeftToRight = false;
            }
        }

        // Translate LEADING/TRAILING values in horizontalAlignment
        // to LEFT/RIGHT values depending on the components orientation
        switch (horizontalAlignment) {
            case LEADING:
                hAlign = (orientationIsLeftToRight) ? LEFT : RIGHT;
                break;
            case TRAILING:
                hAlign = (orientationIsLeftToRight) ? RIGHT : LEFT;
                break;
        }

        // Translate LEADING/TRAILING values in horizontalTextPosition
        // to LEFT/RIGHT values depending on the components orientation
        switch (horizontalTextPosition) {
            case LEADING:
                hTextPos = (orientationIsLeftToRight) ? LEFT : RIGHT;
                break;
            case TRAILING:
                hTextPos = (orientationIsLeftToRight) ? RIGHT : LEFT;
                break;
        }

        return layoutCompoundLabelImpl(c,
                fm,
                text,
                icon,
                verticalAlignment,
                hAlign,
                verticalTextPosition,
                hTextPos,
                viewR,
                iconR,
                textR,
                textIconGap);
    }

    /**
     * Compute and return the location of the icons origin, the
     * location of origin of the text baseline, and a possibly clipped
     * version of the compound labels string.  Locations are computed
     * relative to the viewR rectangle.
     * This layoutCompoundLabel() does not know how to handle LEADING/TRAILING
     * values in horizontalTextPosition (they will default to RIGHT) and in
     * horizontalAlignment (they will default to CENTER).
     * Use the other version of layoutCompoundLabel() instead.
     */
    public static String layoutCompoundLabel(
            FontMetrics fm,
            String text,
            Icon icon,
            int verticalAlignment,
            int horizontalAlignment,
            int verticalTextPosition,
            int horizontalTextPosition,
            Rectangle viewR,
            Rectangle iconR,
            Rectangle textR,
            int textIconGap) {
        return layoutCompoundLabelImpl(null, fm, text, icon,
                verticalAlignment,
                horizontalAlignment,
                verticalTextPosition,
                horizontalTextPosition,
                viewR, iconR, textR, textIconGap);
    }

    /**
     * Compute and return the location of the icons origin, the
     * location of origin of the text baseline, and a possibly clipped
     * version of the compound labels string.  Locations are computed
     * relative to the viewR rectangle.
     * This layoutCompoundLabel() does not know how to handle LEADING/TRAILING
     * values in horizontalTextPosition (they will default to RIGHT) and in
     * horizontalAlignment (they will default to CENTER).
     * Use the other version of layoutCompoundLabel() instead.
     *
     * This is the same as SwingUtilities.layoutCompoundLabelImpl, except for
     * the algorithm for clipping the text. If a text is too long, "..." are
     * inserted at the middle of the text instead of at the end.
     */
    private static String layoutCompoundLabelImpl(
            JComponent c,
            FontMetrics fm,
            String text,
            Icon icon,
            int verticalAlignment,
            int horizontalAlignment,
            int verticalTextPosition,
            int horizontalTextPosition,
            Rectangle viewR,
            Rectangle iconR,
            Rectangle textR,
            int textIconGap) {
        /* Initialize the icon bounds rectangle iconR.
         */

        if (icon != null) {
            iconR.width = icon.getIconWidth();
            iconR.height = icon.getIconHeight();
        } else {
            iconR.width = iconR.height = 0;
        }

        /* Initialize the text bounds rectangle textR.  If a null
         * or and empty String was specified we substitute "" here
         * and use 0,0,0,0 for textR.
         */

        boolean textIsEmpty = (text == null) || text.equals("");
        int lsb = 0;

        View v = null;
        if (textIsEmpty) {
            textR.width = textR.height = 0;
            text = "";
        } else {
            v = (c != null) ? (View) c.getClientProperty("html") : null;
            if (v != null) {
                textR.width = (int) v.getPreferredSpan(View.X_AXIS);
                textR.height = (int) v.getPreferredSpan(View.Y_AXIS);
            } else {
                textR.width = SwingUtilities.computeStringWidth(fm, text);

                lsb = getLeftSideBearing(fm.getFont(), text);
                if (lsb < 0) {
                    // If lsb is negative, add it to the width, the
                    // text bounds will later be adjusted accordingly.
                    textR.width -= lsb;
                }
                textR.height = fm.getHeight();
            }
        }

        /* Unless both text and icon are non-null, we effectively ignore
         * the value of textIconGap.  The code that follows uses the
         * value of gap instead of textIconGap.
         */

        int gap = (textIsEmpty || (icon == null)) ? 0 : textIconGap;

        if (!textIsEmpty) {

            /* If the label text string is too wide to fit within the available
             * space "..." and as many characters as will fit will be
             * displayed instead.
             */

            int availTextWidth;

            if (horizontalTextPosition == CENTER) {
                availTextWidth = viewR.width;
            } else {
                availTextWidth = viewR.width - (iconR.width + gap);
            }


            if (textR.width > availTextWidth) {
                if (v != null) {
                    textR.width = availTextWidth;
                } else {
                    String clipString = "...";
                    int totalWidth = SwingUtilities.computeStringWidth(fm, clipString);
                    int nChars;
                    int len = text.length();
                    for (nChars = 0; nChars < len; nChars++) {
                        int charIndex = (nChars % 2 == 0) ? nChars / 2 : len - 1 - nChars / 2;
                        totalWidth += fm.charWidth(text.charAt(charIndex));
                        if (totalWidth > availTextWidth) {
                            break;
                        }
                    }
                    text = text.substring(0, nChars / 2) + clipString + text.substring(len - nChars / 2);
                    textR.width = SwingUtilities.computeStringWidth(fm, text);
                }
            }
        }


        /* Compute textR.x,y given the verticalTextPosition and
         * horizontalTextPosition properties
         */

        if (verticalTextPosition == TOP) {
            if (horizontalTextPosition != CENTER) {
                textR.y = 0;
            } else {
                textR.y = -(textR.height + gap);
            }
        } else if (verticalTextPosition == CENTER) {
            textR.y = (iconR.height / 2) - (textR.height / 2);
        } else { // (verticalTextPosition == BOTTOM)
            if (horizontalTextPosition != CENTER) {
                textR.y = iconR.height - textR.height;
            } else {
                textR.y = (iconR.height + gap);
            }
        }

        if (horizontalTextPosition == LEFT) {
            textR.x = -(textR.width + gap);
        } else if (horizontalTextPosition == CENTER) {
            textR.x = (iconR.width / 2) - (textR.width / 2);
        } else { // (horizontalTextPosition == RIGHT)
            textR.x = (iconR.width + gap);
        }

        /* labelR is the rectangle that contains iconR and textR.
         * Move it to its proper position given the labelAlignment
         * properties.
         *
         * To avoid actually allocating a Rectangle, Rectangle.union
         * has been inlined below.
         */
        int labelR_x = Math.min(iconR.x, textR.x);
        int labelR_width = Math.max(iconR.x + iconR.width,
                textR.x + textR.width) - labelR_x;
        int labelR_y = Math.min(iconR.y, textR.y);
        int labelR_height = Math.max(iconR.y + iconR.height,
                textR.y + textR.height) - labelR_y;

        int dx, dy;

        if (verticalAlignment == TOP) {
            dy = viewR.y - labelR_y;
        } else if (verticalAlignment == CENTER) {
            dy = (viewR.y + (viewR.height / 2)) - (labelR_y + (labelR_height / 2));
        } else { // (verticalAlignment == BOTTOM)
            dy = (viewR.y + viewR.height) - (labelR_y + labelR_height);
        }

        if (horizontalAlignment == LEFT) {
            dx = viewR.x - labelR_x;
        } else if (horizontalAlignment == RIGHT) {
            dx = (viewR.x + viewR.width) - (labelR_x + labelR_width);
        } else { // (horizontalAlignment == CENTER)
            dx = (viewR.x + (viewR.width / 2)) -
                    (labelR_x + (labelR_width / 2));
        }

        /* Translate textR and glypyR by dx,dy.
         */

        textR.x += dx;
        textR.y += dy;

        iconR.x += dx;
        iconR.y += dy;

        if (lsb < 0) {
            // lsb is negative. We previously adjusted the bounds by lsb,
            // we now need to shift the x location so that the text is
            // drawn at the right location. The result is textR does not
            // line up with the actual bounds (on the left side), but we will
            // have provided enough space for the text.
            textR.width += lsb;
            textR.x -= lsb;
        }

        return text;
    }

    public static void configureGraphics(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    /**
     * Uses some unsupported (dangerous) API calls on the native peers to make
     * a window translucent. If the API is not found, this method leaves the
     * window opaque.
     *
     * @param w The Window.
     * @param value The alpha channel for the window.
     */
    static final void setWindowAlpha(Window w, int value) {
        if (w == null) {
            return;
        }

        // Java 1.4.2_05 does not support window alpha.
        // Setting window alpha only sets the background color of the window
        // to white.

        if (w.getPeer() == null) {
            w.pack();
        }
        java.awt.peer.ComponentPeer peer = w.getPeer();
        try {
            // Alpha API for Apple's Java 1.4 + 1.5 on Mac OS X 10.4 Tiger.
            Methods.invoke(peer, "setAlpha", (float) (value / 255f));
        } catch (Throwable e) {
            // Alpha API for Apple's Java 1.3.
        }
    }

    /** Copied from BasicLookAndFeel.
     */
    public static Component compositeRequestFocus(Component component) {
        try {
            if (component instanceof Container) {
                Container container = (Container) component;
                if (Methods.invokeGetter(container, "isFocusCycleRoot", false)) {

                    Object policy = Methods.invokeGetter(container, "getFocusTraversalPolicy", null);
                    Component comp = (Component) Methods.invoke(policy, "getDefaultComponent", Container.class, container);
                    if (comp != null) {
                        comp.requestFocus();
                        return comp;
                    }
                }
                Container rootAncestor = (Container) Methods.invokeGetter(container, "getFocusCycleRootAncestor", null);
                if (rootAncestor != null) {
                    Object policy = Methods.invokeGetter(rootAncestor, "getFocusTraversalPolicy", null);
                    Component comp = (Component) Methods.invoke(policy, "getComponentAfter",
                            new Class[]{Container.class, Component.class},
                            new Object[]{rootAncestor, container});

                    if (comp != null && SwingUtilities.isDescendingFrom(comp, container)) {
                        comp.requestFocus();
                        return comp;
                    }
                }
            }
        } catch (NoSuchMethodException e) {
        // ignore
        }
        if (Methods.invokeGetter(component, "isFocusable", true)) {
            component.requestFocus();
            return component;
        }
        return null;
    }

    /**
     * Convenience method for installing a property with the specified name
     * and value on a component if that property has not already been set
     * by the client program.  This method is intended to be used by
     * UI delegate instances that need to specify a default value for a
     * property of primitive type (boolean, int, ..), but do not wish
     * to override a value set by the client.  Since primitive property
     * values cannot be wrapped with the UIResource marker, this method
     * uses private state to determine whether the property has been set
     * by the client.
     * @throws IllegalArgumentException if the specified property is not
     *         one which can be set using this method
     * @throws ClassCastException may be thrown if the property value
     *         specified does not match the property's type
     * @throws NullPointerException may be thrown if c or propertyValue is null
     * @param c the target component for installing the property
     * @param propertyName String containing the name of the property to be set
     */
    public static void installProperty(JComponent c,
            String propertyName, Object value) {
        if (canInstallProperty) {
            //LookAndFeel.installProperty(c, propertyName, value);
            try {
                Methods.invokeStatic(LookAndFeel.class, "setUIProperty",
                        new Class[]{JComponent.class, String.class, Object.class},
                        new Object[]{c, propertyName, value});
                return;
            } catch (NoSuchMethodException e) {
                // System.err.println("Warning: QuaquaUtilities failed to installProperty "+propertyName);
                canInstallProperty = false;
            }
        }
        if (propertyName == "opaque") {
            c.setOpaque(((Boolean) value).booleanValue());
        } else if (propertyName == "autoscrolls") {
            c.setAutoscrolls(((Boolean) value).booleanValue());
        /*
        } else if (propertyName == "focusTraversalKeysForward") {
        c.setFocusTraversalKeys(KeyboardFocusManager.
        FORWARD_TRAVERSAL_KEYS,
        (java.util.Set)value);*/
        /*
        } else if (propertyName == "focusTraversalKeysBackward") {
        c.setFocusTraversalKeys(KeyboardFocusManager.
        BACKWARD_TRAVERSAL_KEYS,
        (java.util.Set)value);
         */
        } else {
            throw new IllegalArgumentException("property \"" +
                    propertyName + "\" cannot be set using this method");
        }
    }

    /**
     * Returns the ui that is of type <code>klass</code>, or null if
     * one can not be found.
     */
    public static Object getUIOfType(ComponentUI ui, Class klass) {
        if (klass.isInstance(ui)) {
            return ui;
        }
        return null;
    }

    public static void adjustFocus(JComponent tree) {
        //SwingUtilities2.adjustFocus(tree);
        try {
            Methods.invokeStatic("com.sun.java.swing.SwingUtilities2", "adjustFocus", JComponent.class, tree);
        } catch (NoSuchMethodException ex) {
            tree.requestFocusInWindow();
        }
    }

    static boolean shouldIgnore(MouseEvent e, JComponent tree) {
        //return QuaquaUtilities2.shouldIgnore(e, tree);
        try {
            return ((Boolean) Methods.invokeStatic("com.sun.java.swing.SwingUtilities2", "shouldIgnore",
                    new Class[]{MouseEvent.class, JComponent.class},
                    new Object[]{e, tree})).booleanValue();
        } catch (NoSuchMethodException ex) {
            return false;
        }
    }
}
