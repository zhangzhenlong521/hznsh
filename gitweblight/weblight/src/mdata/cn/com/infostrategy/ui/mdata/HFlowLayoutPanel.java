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
 * һ��ˮƽ���ֵĿؼ�,���������ؼ�ˮƽ����,���һ��Զ�����!
 * ��˼���������BorderLayout�ݹ����,����ҳ��!!!
 * @author xch
 * 
 * From 2009-06-22
 * 1. ���ټ̳�class JPanel����Ϊ�̳�class WLTPanel
 * 2. �����а����������������䱳������Ϊ͸����
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

        for (int i = compents.length - 1; i >= 0; i--) { //�����һ����ǰ��!!
            if (i == compents.length - 1) { //��������һ��,����CENTER�м���һ�������!!!
                JPanel panel = new WLTPanel(new BorderLayout()); //
                panel.setOpaque(false);//--isOpaque
                panel.add(compents[i], BorderLayout.NORTH); //
                panels[i].add(panel, BorderLayout.WEST); ////
                JPanel freePanel = new JPanel();
                freePanel.setOpaque(false);//--isOpaque
                freePanel.setPreferredSize(new Dimension(0, 0)); //
                panels[i].add(freePanel, BorderLayout.CENTER); //
            } else {
                JPanel panel = new WLTPanel(new BorderLayout()); //��Ҫ��һ����ʱ��彫�ÿؼ������Ϸ�,����,�Ͳ����Զ����!!!
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
     * ȡ�ø������еĿؼ�!!!
     * @return
     */
    public javax.swing.JComponent[] getAllCompents() {
        return compents;
    }
}
