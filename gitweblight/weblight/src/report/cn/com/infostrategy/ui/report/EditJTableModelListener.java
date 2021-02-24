package cn.com.infostrategy.ui.report;

import java.util.List;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import cn.com.infostrategy.to.mdata.BillCellItemVO;

public class EditJTableModelListener implements TableModelListener {

	private BillCellPanel billCell = null;
	private List list = null;
	private String[] temp = null;
	private BillCellItemVO billCellItem = null;
	public EditJTableModelListener(BillCellPanel billCell,List list){
		this.billCell = billCell;
		this.list = list;
	}
	public void tableChanged(TableModelEvent e) {
		int number = 0;
		if(list == null || list.size() == 0){
			return;
		}
		switch (e.getType()){
		
		case TableModelEvent.INSERT:
			for(int i = 0;i < list.size();i++){
				temp = (String[])list.get(i);
				if(temp == null || temp.length == 0){
					continue;
				}
				if(temp.length == 1){
					billCellItem = billCell.getBillCellItemVOAt(temp[0]);
//					设置该项不可编辑
				}
				for(int j = 1;j < temp.length; j++){
					billCellItem = billCell.getBillCellItemVOAt(temp[j]);
					try{
						if(billCellItem != null){
							if(!checkString(billCellItem.getCellvalue()).equals("")){
								number = number + Integer.parseInt(checkString(billCellItem.getCellvalue()));
								billCellItem.setCellvalue(checkString(billCellItem.getCellvalue().trim()));
								billCellItem.setForeground("0,0,0");
							}
						}
					}catch(Exception ex){
						billCellItem.setForeground("255,0,0");
						continue;
					}
				}
				billCellItem = billCell.getBillCellItemVOAt(temp[0]);
				billCellItem.setCellvalue(String.valueOf(number));
				number = 0;
			}
			billCell.getTable().repaint();
			break;
			
		case TableModelEvent.UPDATE:
			for(int i = 0;i < list.size();i++){
				temp = (String[])list.get(i);
				if(temp == null || temp.length == 0){
					continue;
				}
				if(temp.length == 1){
					billCellItem = billCell.getBillCellItemVOAt(temp[0]);
//					设置该项不可编辑
				}
				for(int j = 1;j < temp.length; j++){
					billCellItem = billCell.getBillCellItemVOAt(temp[j]);
					try{
						if(billCellItem != null){
							if(!checkString(billCellItem.getCellvalue()).equals("")){
								number = number + Integer.parseInt(checkString(billCellItem.getCellvalue()));
								billCellItem.setCellvalue(checkString(billCellItem.getCellvalue().trim()));
								billCellItem.setForeground("0,0,0");
							}
						}
					}catch(Exception ex){
						billCellItem.setForeground("255,0,0");
						continue;
					}
				}
				billCellItem = billCell.getBillCellItemVOAt(temp[0]);
				billCellItem.setCellvalue(String.valueOf(number));
				number = 0;
			}
			billCell.getTable().repaint();
			break;
			
		case TableModelEvent.DELETE:
			for(int i = 0;i < list.size();i++){
				temp = (String[])list.get(i);
				if(temp == null || temp.length == 0){
					continue;
				}
				if(temp.length == 1){
					billCellItem = billCell.getBillCellItemVOAt(temp[0]);
//					设置该项不可编辑
				}
				for(int j = 1;j < temp.length; j++){
					billCellItem = billCell.getBillCellItemVOAt(temp[j]);
					try{
						if(billCellItem != null){
							if(!checkString(billCellItem.getCellvalue()).equals("")){
								number = number + Integer.parseInt(checkString(billCellItem.getCellvalue()));
								billCellItem.setCellvalue(checkString(billCellItem.getCellvalue().trim()));
								billCellItem.setForeground("0,0,0");
							}
						}
					}catch(Exception ex){
						billCellItem.setForeground("255,0,0");
						continue;
					}
				}
				billCellItem = billCell.getBillCellItemVOAt(temp[0]);
				billCellItem.setCellvalue(String.valueOf(number));
				number = 0;
			}
			billCell.getTable().repaint();
			break;
		}
        billCell.getTable().repaint();
	}
	private String checkString(String input){
		if(input == null){
			input="";
		}
		String output = null;
		output = input.replaceAll("\r", "");
		output = output.replaceAll("\n", "");
		output = output.replaceAll(" ", "");
		return output.trim();
	}
//	int firstRow = e.getFirstRow();
//    int lastRow = e.getLastRow();
//    int mColIndex = e.getColumn();
//    switch (e.getType()) {
//      case TableModelEvent.INSERT:
//    	  System.out.println("The inserted rows are in the range ["+firstRow+", "+lastRow+"] ");
//        for (int r=firstRow; r<=lastRow; r++) {
//        	System.out.println("Row "+r+" was inserted");
//        }
//        break;
//      case TableModelEvent.UPDATE:
//        if (firstRow == TableModelEvent.HEADER_ROW) {
//            if (mColIndex == TableModelEvent.ALL_COLUMNS) {
//            	System.out.println("A column was added");
//            } else {
//            	System.out.println("Column "+mColIndex+" in header changed");
//            }
//        } else {
//        	int number = 0;
//            for (int r=12; r<=17; r++) {
//            	System.out.println("Row "+r+" was changed");
//            
//                if (mColIndex == TableModelEvent.ALL_COLUMNS) {
//                	System.out.println("All columns in the range of rows have changed");
//                } else {
//                	
//                	System.out.println("Column "+mColIndex+" changed");
//                	
//                	billCell.getTable().setRowHeight(20);
//                	if(billCell.getBillCellItemVOAt(r, 3).getCellvalue() == null || billCell.getBillCellItemVOAt(r, 3).getCellvalue().equals("")){
//                		number = number + 0;
//                	}else{
//                		number = number + Integer.parseInt(billCell.getBillCellItemVOAt(r, 3).getCellvalue());
//                	}
//                }
//            }
//            BillCellItemVO temp =  billCell.getBillCellItemVOAt(11, 3);
//            temp.setCellvalue(String.valueOf(number));
//        }
//        break;
//      case TableModelEvent.DELETE:
//    	  System.out.println("["+firstRow+", "+lastRow+"] changed");
//        for (int r=firstRow; r<=lastRow; r++) {
//        	 System.out.println("Row r was deleted");
//        }
//        break;
//    }
//    billCell.getTable().repaint();
}
