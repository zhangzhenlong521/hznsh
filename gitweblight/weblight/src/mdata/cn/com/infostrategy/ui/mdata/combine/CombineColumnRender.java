package cn.com.infostrategy.ui.mdata.combine;

import java.awt.Component; 
import javax.swing.JTable; 
import javax.swing.table.DefaultTableCellRenderer; 
  
/** 
 * ������Ҫ�ϲ����еĵ�Ԫ���ܱ�ѡ�� ���ܾ۽� 
 * @author �����/2013-07-23�� 
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
