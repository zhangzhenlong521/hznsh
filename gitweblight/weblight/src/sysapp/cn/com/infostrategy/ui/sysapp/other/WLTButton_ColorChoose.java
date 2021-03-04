package cn.com.infostrategy.ui.sysapp.other;

import java.awt.Color;

import javax.swing.JColorChooser;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

public class WLTButton_ColorChoose implements WLTActionListener {

    public void actionPerformed(WLTActionEvent _event) throws Exception {
        BillListPanel list = (BillListPanel)_event.getBillPanelFrom();
        if (list.getSelectedBillVO() == null) {
            //throw new WLTAppException("请选择一个注册按钮!");
            MessageBox.show("请选择一个注册按钮!");
            return;
        }
        BillVO current_vo = list.getSelectedBillVO();
        String str_currColor = current_vo.getStringValue(list.getSelectedColumnItemKey());

        //判断，如果单元格内容不是颜色，提示用户 
        if (!str_currColor.matches("\\d{1,3},\\s*\\d{1,3},\\s*\\d{1,3}")) { 
            MessageBox.show("当前选择的单元格内容不是描述颜色的字符串！");
            return;
        }
        
        //弹出颜色选择对话框,获得用户选择的颜色
        Color color_chosen = JColorChooser.showDialog(list, "颜色选择", getColorByEachRGB(str_currColor));
        if(color_chosen == null) {
            //user cacel color choosing, do nothing
            return;
        }
        
        //将颜色字符串写入当前选择的单元格
        list.setValueAt(
            getRGBColorString(color_chosen), 
            list.getTable().getSelectedRow(), 
            list.getSelectedColumnItemKey()
            );
        
    }
    
    /**
     * 
     * @param _color, it's format as "int,int,int"
     * @return
     */
    private Color getColorByEachRGB(String _color) {
        String[] color_RGB = _color.replaceAll("\\s*", "").split(",");
        return new Color(
                Integer.valueOf(color_RGB[0]).intValue(),//color_red
                Integer.valueOf(color_RGB[1]).intValue(),//color_green
                Integer.valueOf(color_RGB[2]).intValue() //color_blue
                );
    }
    
    /**
     * 
     * @param _color
     * @return return a RGB color string format with "int,int,int"
     */
    private String getRGBColorString(Color _color) {
        int i_color_RGB = _color.getRGB();
        int i_color_R = 0xFF & (i_color_RGB>>16);
        int i_color_G = 0xFF & (i_color_RGB>>8);
        int i_color_B = 0xFF & (i_color_RGB);
        return i_color_R + "," + i_color_G + "," + i_color_B;
    }

}
