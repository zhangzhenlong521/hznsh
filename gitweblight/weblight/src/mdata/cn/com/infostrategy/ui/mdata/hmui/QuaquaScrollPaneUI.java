package cn.com.infostrategy.ui.mdata.hmui;

/*
 *
 * Copyright (c) 2004-2005 Werner Randelshofer
 * Staldenmattweg 2, Immensee, CH-6405, Switzerland.
 * All rights reserved.
 *
 * The copyright of this software is owned by Werner Randelshofer. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Werner Randelshofer. For details see accompanying license terms. 
 */


import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicScrollPaneUI;

/**
 * QuaquaScrollPaneUI.
 *
 * @author  Werner Randelshofer
 * @version 1.2.2 2005-11-26 Retrieve default opaqueness from UIManager.
 * <br>1.2.1 2005-09-17 Don't fill background if non-opaque.
 * <br>1.2 2005-08-25 Installs a QuaquaScrollPaneLayout to the scroll
 * pane to avoid overlapping of scroll bars with the grow-box of a Frame or
 * Dialog.
 * <br>1.1 2005-07-17 Adapted to changes in interface VisuallyLayoutable.
 * <br>1.0  June 23, 2004  Created.
 */
public class QuaquaScrollPaneUI extends BasicScrollPaneUI{
    //private HierarchyListener hierarchyListener;
    
    /** Creates a new instance. */
    public QuaquaScrollPaneUI() {
    }
    
    public static ComponentUI createUI(JComponent c) {
        return new QuaquaScrollPaneUI();
    }
    
    public void installUI(JComponent c) {
        super.installUI(c);
        QuaquaUtilities.installProperty(c, "opaque", UIManager.get("ScrollPane.opaque"));
        c.setOpaque(false);
        //c.setOpaque(QuaquaManager.getBoolean("ScrollPane.opaque"));
//        Methods.invokeIfExists(c, "setFocusable", QuaquaManager.getBoolean("ScrollPane.focusable"));
    }
    protected PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler(super.createPropertyChangeListener());
    }
    protected void uninstallDefaults(JScrollPane scrollpane) {
        super.uninstallDefaults(scrollpane);
        if (scrollpane.getLayout() instanceof UIResource) {
            ScrollPaneLayout layout = new ScrollPaneLayout.UIResource();
            scrollpane.setLayout(layout);
            layout.syncWithScrollPane(scrollpane);
        }
    }
    
    /*
    protected HierarchyListener createHierarchyListener(JScrollPane c) {
        // FIXME: The ComponentActivationHandler repaints the _whole_ JScrollPane.
        // This is inefficient. We only need the border area of the JScrollPane
        // to be repainted.
        return new ComponentActivationHandler(c);
    }*/
    /*
    protected void installListeners(JScrollPane c) {
        hierarchyListener = createHierarchyListener(c);
        if (hierarchyListener != null) {
            c.addHierarchyListener(hierarchyListener);
        }
        super.installListeners(c);
    }
     
    protected void uninstallListeners(JScrollPane c) {
        if (hierarchyListener != null) {
            c.removeHierarchyListener(hierarchyListener);
            hierarchyListener = null;
        }
        super.uninstallListeners(c);
    }*/
    public Insets getVisualMargin(Component c) {
        Insets margin = (Insets) ((JComponent) c).getClientProperty("Quaqua.Component.visualMargin");
        if (margin == null) margin = UIManager.getInsets("Component.visualMargin");
        return (margin == null) ? new Insets(0, 0, 0 ,0) : margin;
    }
    public void update(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            g.setColor(c.getBackground());
            Insets margin = getVisualMargin(c);
            g.fillRect(margin.left, margin.top, c.getWidth() - margin.left - margin.right, c.getHeight() - margin.top - margin.bottom);
            paint(g, c);
        }
    }
    
    public int getBaseline(JComponent c, int width, int height) {
        return -1;
    }
    
    /**
     * PropertyChangeListener for the ScrollBars.
     */
    private class PropertyChangeHandler implements PropertyChangeListener {
        PropertyChangeListener target;
        public PropertyChangeHandler(PropertyChangeListener target) {
            this.target = target;
        }
        // Listens for changes in the model property and reinstalls the
        // horizontal/vertical PropertyChangeListeners.
        public void propertyChange(PropertyChangeEvent e) {
            String propertyName = e.getPropertyName();
            Object source = e.getSource();
            
            if ("Frame.active".equals(propertyName)) {
                QuaquaUtilities.repaintBorder((JComponent) source);
            }
            target.propertyChange(e);
        }
    }
}