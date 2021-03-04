package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.WLTPanel;

/**
 * 一个水平布局的控件,即将各个控件水平布局,而且会自动增宽!
 * 其思想就是利用BorderLayout递归叠加,构成页面!!!
 * @author xch
 * 
 * From 2009-06-22
 * 1. 不再继承class JPanel，改为继承class WLTPanel
 * 2. 该类中包含的所有容器，其背景皆设为透明的
 *
 */
public class HFlowLayoutPanel extends WLTPanel {

    private static final long serialVersionUID = 1L;
    private javax.swing.JComponent[] compents = null; //
    private HashMap hm_compents = new HashMap(); //

    public HFlowLayoutPanel(java.util.List _list) {
        super();
        compents = new javax.swing.JComponent[_list.size()]; //
        for (int i = 0; i < _list.size(); i++) {
            compents[i] = (javax.swing.JComponent) _list.get(i); //
        }
        initialize(); //
    }

    public HFlowLayoutPanel(ArrayList _list) {
        super();
        compents = new javax.swing.JComponent[_list.size()]; //
        for (int i = 0; i < _list.size(); i++) {
            compents[i] = (javax.swing.JComponent) _list.get(i); //
        }
        this.initialize(); //
    }
    
    public HFlowLayoutPanel(ArrayList _list, Color _bgcolor) {
        super(BackGroundDrawingUtil.SINGLE_COLOR_UNCHANGE, _bgcolor);
        compents = new javax.swing.JComponent[_list.size()]; //
        for (int i = 0; i < _list.size(); i++) {
            compents[i] = (javax.swing.JComponent) _list.get(i); //
        }
        this.initialize(); //
    }

    public HFlowLayoutPanel(ArrayList _list, int _shadeType, Color _bgcolor) {
        super(_shadeType, _bgcolor);
        compents = new javax.swing.JComponent[_list.size()]; //
        for (int i = 0; i < _list.size(); i++) {
            compents[i] = (javax.swing.JComponent) _list.get(i); //
        }
        this.initialize(); //
    }

    /**
     *
     */
    public HFlowLayoutPanel(javax.swing.JComponent[] _compents) {
        super();
        this.compents = _compents; //
        initialize(); //
    }
    
    public HFlowLayoutPanel(javax.swing.JComponent[] _compents, Color _bgcolor) {
        super(BackGroundDrawingUtil.SINGLE_COLOR_UNCHANGE, _bgcolor);
        this.setBackground(_bgcolor); //
        this.compents = _compents; //
        initialize(); //
    }

    public HFlowLayoutPanel(javax.swing.JComponent[] _compents, int _shadeType, Color _bgcolor) {
        super(_shadeType, _bgcolor);
        this.setBackground(_bgcolor); //
        this.compents = _compents; //
        initialize(); //
    }

    private void initialize() {
        this.setLayout(new BorderLayout(0, 0)); //

        JPanel[] panels = new JPanel[compents.length]; //
        for (int i = 0; i < panels.length; i++) {
            panels[i] = new WLTPanel(new BorderLayout()); //
        }

        for (int i = compents.length - 1; i >= 0; i--) { //从最后一个往前加!!
            if (i == compents.length - 1) { //如果是最后一个,则在CENTER中加入一个空面板!!!
                JPanel panel = new WLTPanel(new BorderLayout()); //
                panel.setOpaque(false);//--isOpaque
                panel.add(compents[i], BorderLayout.NORTH); //
                panels[i].add(panel, BorderLayout.WEST); ////
                JPanel freePanel = new JPanel();
                freePanel.setOpaque(false);//--isOpaque
                freePanel.setPreferredSize(new Dimension(0, 0)); //
                panels[i].add(freePanel, BorderLayout.CENTER); //
            } else {
                JPanel panel = new WLTPanel(new BorderLayout()); //先要用一个临时面板将该控件放在上方,这样,就不会自动变大!!!
                panel.setOpaque(false);//--isOpaque
                panel.add(compents[i], BorderLayout.NORTH); //
                panels[i].add(panel, BorderLayout.WEST); ////
                panels[i].add(panels[i + 1], BorderLayout.CENTER); //
            }
        }

        if (panels.length > 0) {
            this.add(panels[0]); //
        }
    }

    /**
     * 取得该行所有的控件!!!
     * @return
     */
    public javax.swing.JComponent[] getAllCompents() {
        return compents;
    }
}
