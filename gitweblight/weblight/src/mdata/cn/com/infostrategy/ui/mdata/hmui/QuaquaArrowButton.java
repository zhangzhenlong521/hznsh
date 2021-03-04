package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.SwingConstants;

public class QuaquaArrowButton extends JButton implements SwingConstants {
    private JScrollBar scrollbar;
    
    public QuaquaArrowButton(JScrollBar scrollbar) {
        this.scrollbar = scrollbar;
        setRequestFocusEnabled(false);
        setOpaque(false);
    }
    
    public void paint(Graphics g) {
        return;
    }
    /*
    public Dimension getPreferredSize() {
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            if (scrollbar.getFont().getSize() <= 11) {
                return new Dimension(11, 12);
            } else {
                return new Dimension(15, 16);
            }
        } else {
            if (scrollbar.getFont().getSize() <= 11) {
                return new Dimension(12, 11);
            } else {
                return new Dimension(16, 15);
            }
        }
    }
    
    public Dimension getMinimumSize() {
        return new Dimension(5, 5);
    }
    
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }*/
    
    public boolean isFocusTraversable() {
        return false;
    }
}
