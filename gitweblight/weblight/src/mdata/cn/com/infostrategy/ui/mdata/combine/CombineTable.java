package cn.com.infostrategy.ui.mdata.combine;

import javax.swing.*; 

import java.awt.*; 
import java.util.ArrayList;

import javax.swing.table.TableColumn; 
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel; 

import cn.com.infostrategy.ui.mdata.BillListModel;

/** 
 * 合并基类 
 * @author 【杨科/2013-07-23】 
 */

public class CombineTable extends JTable { 
  
    public CombineData combineData = null; 
	
	public CombineTable(TableModel _tableModel) {
		super(_tableModel);
        
        setCombineColumns( new String[0][0], null);
		
		super.setUI(new CombineTableUI()); 
	}
	
	public CombineTable(Object[][] strings, Object[] strings2) {
		super(strings, strings2);
        
        setCombineColumns(strings, null);
		
		super.setUI(new CombineTableUI()); 
	}
	
	public CombineTable(BillListModel _tableModel, TableColumnModel _columnModel) {
		super(_tableModel, _columnModel);
        
        setCombineColumns(_tableModel, null);
		
		super.setUI(new CombineTableUI()); 
	}
    
    public void setCombineColumns(Object[][] strings, ArrayList<Integer> combineCols) { 
    	this.combineData = new CombineData(strings, combineCols); 
        
        for (Integer column : combineData.combineColumns) { 
            TableColumn tableColumn = super.columnModel.getColumn(column); 
            tableColumn.setCellRenderer(new CombineColumnRender()); 
        } 
    }
    
    public void setCombineColumns(BillListModel _tableModel, ArrayList<Integer> combineCols) { 
    	this.combineData = new CombineData(_tableModel.getRealValueAtModel(), combineCols); 
        
        for (Integer column : combineData.combineColumns) { 
            TableColumn tableColumn = super.columnModel.getColumn(column); 
            tableColumn.setCellRenderer(new CombineColumnRender()); 
        } 
    }
    
    public void setCombineColumns(BillListModel _tableModel, ArrayList<Integer> combineCols, int datamark) { 
    	this.combineData = new CombineData(_tableModel.getRealValueAtModel(), combineCols, datamark); 
        
        for (Integer column : combineData.combineColumns) { 
            TableColumn tableColumn = super.columnModel.getColumn(column); 
            tableColumn.setCellRenderer(new CombineColumnRender()); 
        } 
    }
  
    public Rectangle getCellRect(int row, int column, boolean includeSpacing) { 
        // required because getCellRect is used in JTable constructor 
        if (combineData == null) { 
            return super.getCellRect(row, column, includeSpacing); 
        } 
        
        if(combineData != null && row > combineData.getdatasLen()-1){
        	return super.getCellRect(row, column, includeSpacing); 
        }
        
        // add widths of all spanned logical cells 
        int sk = combineData.visibleCell(row, column); 
        Rectangle rect1 = super.getCellRect(sk, column, includeSpacing); 
        if (combineData.span(sk, column) != 1) { 
            for (int i = 1; i < combineData.span(sk, column); i++) { 
                rect1.height += this.getRowHeight(sk + i); 
            } 
        } 
        return rect1; 
    } 
  
    public int rowAtPoint(Point p) { 
        int column = super.columnAtPoint(p); 
        // -1 is returned by columnAtPoint if the point is not in the table 
        if (column < 0) { 
            return column; 
        } 
        int row = super.rowAtPoint(p); 
        return combineData.visibleCell(row, column); 
    } 
  
    public boolean isCellEditable(int row, int column) { 
        if (combineData.combineColumns.contains(column)) { 
            return false; 
        } 
        return super.isCellEditable(row, column); 
    } 
  
    public boolean isCellSelected(int row, int column) { 
/*        if (combineData.combineColumns.contains(column)) { 
            return false; 
        } */
        return super.isCellSelected(row, column); 
    } 
}
