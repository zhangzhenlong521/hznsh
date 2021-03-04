/*
 * @(#)MutableColorUIResource.java  1.0  November 10, 2007
 *
 * Copyright (c) 2007 Werner Randelshofer
 * Staldenmattweg 2, CH-6405 Immensee, Switzerland
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */

package cn.com.infostrategy.ui.mdata.hmui.util;

import java.awt.Color;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

import javax.swing.plaf.UIResource;

/**
 * A ColorUIResource which can change its color.
 *
 * @author Werner Randelshofer
 * @version 1.0 November 10, 2007 Created.
 */
public class MutableColorUIResource extends Color implements UIResource {
    private int argb;
    
    /** Creates a new instance. */
    public MutableColorUIResource(int rgb) {
        this(rgb, false);
    }
    public MutableColorUIResource(int argb, boolean hasAlpha) {
        super((hasAlpha) ? argb : 0xff000000 | argb, true);
        this.argb = argb;
    }
    
    public void setColor(Color newValue) {
        setRGB(newValue.getRGB());
    }
    
    public void setRGB(int newValue) {
        argb = newValue;
    }
    
    public int getRGB() {
        return argb;
    }
    
    public PaintContext createContext(ColorModel cm, Rectangle r, Rectangle2D r2d, AffineTransform xform, RenderingHints hints) {
        return new Color(argb, true).createContext(cm, r, r2d, xform, hints);
    }
}
