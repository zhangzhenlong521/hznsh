package cn.com.infostrategy.ui.mdata.combine;

import java.awt.Component; 
import javax.swing.JTable; 
import javax.swing.table.DefaultTableCellRenderer; 
  
/** 
 * 设置需要合并的列的单元格不能被选中 不能聚焦 
 * @author 【杨科/2013-07-23】 
 */

public class CombineColumnRender extends DefaultTableCellRenderer { 
  
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) { 
/*        CombineTable cTable = (CombineTable) table; 
        if (cTable.combineData.combineColumns.contains(column)) { 
            hasFocus = false; 
        }*/ 
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); 
    } 
    
}
