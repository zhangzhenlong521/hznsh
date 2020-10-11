/*
 * @(#)QuaquaIconFactory.java  4.0.1 2007-09-08
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
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.plaf.IconUIResource;

import cn.com.infostrategy.ui.mdata.hmui.util.ButtonFocusIcon;
import cn.com.infostrategy.ui.mdata.hmui.util.ButtonStateIcon;
import cn.com.infostrategy.ui.mdata.hmui.util.FrameButtonStateIcon;
import cn.com.infostrategy.ui.mdata.hmui.util.ShiftedIcon;
import cn.com.infostrategy.ui.mdata.hmui.util.SliderThumbIcon;
import cn.com.infostrategy.ui.mdata.hmui.util.SwingWorker;


/**
 * QuaquaIconFactory.
 *
 * @author  Werner Randelshofer, Christopher Atlan
 * @version 4.0.1 2007-09-08 Option pane icons were empty when Quaqua ran
 * in a restriced Java WebStart sandbox. 
 * <br>4.0 2007-04-28 Removed Java-Cocoa code.
 * <br>3.2.1 2007-02-23 Don't pop an autorelease pool if its identifier is 0.
 * <br>3.2 2007-01-05 Issue #1: Changed LazyOptionPaneIcon to load image
 * asynchronously before paintIcon is invoked.
 * <br>3.1 2006-12-24 by Karl von Randow: Use Images class to create artwork.
 * <br>3.0.2 2006-11-01 Use Graphics2D.drawImage() to scale application
 * image icon instead of using Image.getScaledInstance().
 * <br>3.0.1 2006-05-14 Application icon was unnecessarily created multiple
 * times.
 * <br>3.0 2006-05-12 Added support for file icon images. Renamed some
 * methods.
 * <br>2.1 2006-02-14 Added method createFrameButtonStateIcon.
 * <br>2.0 2006-02-12 Added methods createApplicationIcon, compose,
 * createOptionPaneIcon. These methods were contributed by Christopher Atlan.
 * <br>1.0 December 4, 2005 Created.
 */
public class QuaquaIconFactory {
    
    /**
     * Lazy option pane icon.
     * The creation of an option pane icon is a potentially slow operation.
     * This icon class will load the icon image in a worker thread and paint it,
     * when it is ready.
     */
    private static class LazyOptionPaneIcon implements Icon {
        private ImageIcon realIcon;
        private int messageType;
        private SwingWorker worker;
        
        public LazyOptionPaneIcon(final int messageType) {
            this.messageType = messageType;
            worker = new SwingWorker() {
                public Object construct() {
                        return null;
                }
            };
            worker.start();
        }
        
        public int getIconHeight() {
            return 64;
        }
        
        public int getIconWidth() {
            return 64;
        }
        
        public void paintIcon(final Component c, Graphics g, int x, int y) {
            if (realIcon == null) {
                realIcon = (ImageIcon) worker.get();
            }
            if (realIcon != null) {
                realIcon.paintIcon(c, g, x, y);
            }
        }
    }
    
    /**
     * Prevent instance creation.
     */
    private QuaquaIconFactory() {
    }
    
    public static URL getResource(String location) {
        URL url = QuaquaIconFactory.class.getResource(location);
        if (url == null) {
            throw new InternalError("image resource missing: "+location);
        }
        return url;
    }
    
    public static Image createImage(String location) {
        return createImage(QuaquaIconFactory.class, location);
    }
    public static Image createImage(Class baseClass, String location) {
        return Images.createImage(baseClass.getResource(location));
    }
    public static Image createBufferedImage(String location) {
        return Images.toBufferedImage(createImage(location));
    }
    
    public static Icon[] createIcons(String location, int count, boolean horizontal) {
        Icon[] icons = new Icon[count];
        
        BufferedImage[] images = Images.split(
                (Image) createImage(location),
                count, horizontal
                );
        
        for (int i=0; i < count; i++) {
            icons[i] = new IconUIResource(new ImageIcon(images[i]));
        }
        return icons;
    }
    
    public static Icon createIcon(String location, int count, boolean horizontal, int index) {
        return createIcons(location, count, horizontal)[index];
    }
    
    
    public static Icon createButtonStateIcon(String location, int states) {
        return new ButtonStateIcon(
                (Image) createImage(location),
                states, true
                );
    }
    public static Icon createButtonStateIcon(String location, int states, Point shift) {
        return new ShiftedIcon(
                new ButtonStateIcon(
                (Image) createImage(location),
                states, true
                ),
                shift
                );
    }
    public static Icon createButtonStateIcon(String location, int states, Rectangle shift) {
        return new ShiftedIcon(
                new ButtonStateIcon(
                (Image) createImage(location),
                states, true
                ),
                shift
                );
    }
    public static Icon createFrameButtonStateIcon(String location, int states) {
        return new FrameButtonStateIcon(
                (Image) createImage(location),
                states, true
                );
    }
    
    
    public static Icon createButtonFocusIcon(String location, int states) {
        return new ButtonFocusIcon(
                (Image) createImage(location),
                states, true
                );
    }
    
    public static Icon createSliderThumbIcon(String location) {
        return new SliderThumbIcon(createImage(location), 6, true);
    }
    
    public static Icon createIcon(Class baseClass, String location) {
        return new ImageIcon(createImage(baseClass, location));
    }
    public static Icon createIcon(Class baseClass, String location, Point shift) {
        return new ShiftedIcon(
                new ImageIcon(createImage(baseClass, location)),
                shift
                );
    }
    public static Icon createIcon(Class baseClass, String location, Rectangle shiftAndSize) {
        return new ShiftedIcon(
                new ImageIcon(createImage(baseClass, location)),
                shiftAndSize
                );
    }
    
    public static Icon createOptionPaneIcon(int messageType) {
        return new LazyOptionPaneIcon(messageType);
    }
    
    
    /**
     * Gets the application image. This is a buffered image of size 128x128.
     * If the Cocoa Java bridge and the ImageIO API is present, this will get
     * the image from the OS X application bundle.
     * In all other cases this will return a default application image.
     */
}
