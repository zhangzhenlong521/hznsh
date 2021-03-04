/*
 * @(#)AnimatedBorder.java  1.0.1  2005-06-25
 *
 * Copyright (c) 2005 Werner Randelshofer
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;

import javax.swing.border.Border;
/**
 * AnimatedBorder takes an array of borders and a delay value, to draw an
 * animated border.
 *
 * @author  Werner Randelshofer
 * @version 1.0.1 2005-06-25 Return a new instance of Insets in method getBorderInsets.
 * <br>1.0  13 March 2005  Created.
 */
public class AnimatedBorder implements Border {
    private final static java.util.Timer timer = new java.util.Timer(true);
    /**
     * In this HashSet we store all components that are scheduled for repainting.
     */
    private HashSet scheduledComponents = new HashSet();
    /**
     * Animation borders.
     * All borders must have the same insets.
     */
    private Border[] borders;
    /**
     * Delay time between borders.
     */
    private long delay;
    
    /** Creates a new instance. */
    public AnimatedBorder(Border[] borders, long delay) {
        this.borders = borders;
        this.delay = delay;
    }
    
    public Insets getBorderInsets(Component c) {
        return (Insets) borders[0].getBorderInsets(c).clone();
    }
    
    public boolean isBorderOpaque() {
        return borders[0].isBorderOpaque();
    }
    
    public void paintBorder(final Component c, Graphics g, int x, int y, int width, int height) {
        long animTime = System.currentTimeMillis() % (borders.length * delay);
        int frame = (int) (animTime / delay);

        borders[frame].paintBorder(c, g, x, y, width, height);
        
        
        long sleep = delay - animTime % delay;
        if (sleep == 0) sleep = delay;
        
        if (! scheduledComponents.contains(c)) {
            scheduledComponents.add(c);
            javax.swing.Timer timer = new javax.swing.Timer((int) sleep, new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    scheduledComponents.remove(c);
                    c.repaint();
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
}


