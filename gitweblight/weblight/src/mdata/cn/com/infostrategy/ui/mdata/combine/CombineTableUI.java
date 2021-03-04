package cn.com.infostrategy.ui.mdata.combine;

import javax.swing.table.*; 
import javax.swing.plaf.basic.*; 
import java.awt.*; 
import javax.swing.*; 

/** 
 * ºÏ²¢UI 
 * @author ¡¾Ñî¿Æ/2013-07-23¡¿ 
 */

public class CombineTableUI extends BasicTableUI { 
  
    public void paint(Graphics g, JComponent c) { 
        Rectangle r = g.getClipBounds(); 
        rendererPane.removeAll(); 
  
        int firstCol = table.columnAtPoint(new Point(r.x, 0)); 
        int lastCol = table.columnAtPoint(new Point(r.x + r.width, 0)); 
        // -1 is a flag that the ending point is outside the table 
        if (lastCol < 0) { 
            lastCol = table.getColumnCount() - 1; 
        } 
        for (int i = firstCol; i <= lastCol; i++) { 
            paintCol(i, g); 
        } 
  
        paintGrid(g, 0, table.getRowCount() - 1, 0, table.getColumnCount() - 1); 
    } 
  
    private void paintCol(int col, Graphics g) { 
        Rectangle r = g.getClipBounds(); 
        for (int i = 0; i < table.getRowCount(); i++) { 
            Rectangle r1 = table.getCellRect(i, col, true); 
            if (r1.intersects(r)) // at least a part is visible 
            { 
                int sk = ((CombineTable) table).combineData.visibleCell(i, col); 
                paintCell(sk, col, g, r1); 
                // increment the column counter 
                i += ((CombineTable) table).combineData.span(sk, col) - 1; 
            } 
        } 
    } 
  
    private void paintCell(int row, int column, Graphics g, Rectangle area) { 
        int verticalMargin = table.getRowMargin(); 
        int horizontalMargin = table.getColumnModel().getColumnMargin(); 
  
        area.setBounds(area.x + horizontalMargin / 2, area.y + verticalMargin / 2, area.width - horizontalMargin, area.height - verticalMargin); 
  
        if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column) { 
            Component component = table.getEditorComponent(); 
            component.setBounds(area); 
            component.validate(); 
        } else { 
            TableCellRenderer renderer = table.getCellRenderer(row, column); 
            Component component = table.prepareRenderer(renderer, row, column); 
            if (component.getParent() == null) { 
                rendererPane.add(component); 
            } 
            rendererPane.paintComponent(g, component, table, area.x, area.y, 
                    area.width, area.height, true); 
        } 
    } 
  
    private void paintGrid(Graphics g, int rMin, int rMax, int cMin, int cMax) { 
        g.setColor(table.getGridColor()); 
  
        Rectangle minCell = table.getCellRect(rMin, cMin, true); 
        Rectangle maxCell = table.getCellRect(rMax, cMax, true); 
        Rectangle damagedArea = minCell.union(maxCell); 
  
        if (table.getShowHorizontalLines()) { 
            CombineData cMap = ((CombineTable) table).combineData; 
            for (int row = rMin; row <= rMax; row++) { 
                for (int column = cMin; column <= cMax; column++) { 
                    Rectangle cellRect = table.getCellRect(row, column, true); 
  
                    if (cMap.combineColumns.contains(column)) { 
                        int visibleCell = cMap.visibleCell(row, column); 
                        int span = cMap.span(row, column); 
                        if (span > 1 && row < visibleCell + span - 1) { 
                        } else { 
                            g.drawLine(cellRect.x, cellRect.y + cellRect.height - 1, cellRect.x + cellRect.width - 1, cellRect.y + cellRect.height - 1); 
                        } 
                    } else { 
                        g.drawLine(cellRect.x, cellRect.y + cellRect.height - 1, cellRect.x + cellRect.width - 1, cellRect.y + cellRect.height - 1); 
                    } 
                } 
            } 
        } 
        if (table.getShowVerticalLines()) { 
            TableColumnModel cm = table.getColumnModel(); 
            int tableHeight = damagedArea.y + damagedArea.height; 
            int x; 
            if (table.getComponentOrientation().isLeftToRight()) { 
                x = damagedArea.x; 
                for (int column = cMin; column <= cMax; column++) { 
                    int w = cm.getColumn(column).getWidth(); 
                    x += w; 
                    g.drawLine(x - 1, 0, x - 1, tableHeight - 1); 
                } 
            } else { 
                x = damagedArea.x; 
                for (int column = cMax; column >= cMin; column--) { 
                    int w = cm.getColumn(column).getWidth(); 
                    x += w; 
                    g.drawLine(x - 1, 0, x - 1, tableHeight - 1); 
                } 
            } 
        } 
    } 
}