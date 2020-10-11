package cn.com.infostrategy.ui.mdata.combine;

import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.List; 

import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;

/** 
 * �ϲ����ݶ��� 
 * @author �����/2013-07-23�� 
 */

public class CombineData { 
  
    public ArrayList<Integer> combineColumns = new ArrayList<Integer>(); //���ڱ�����Ҫ�ϲ����к� 
    private Object[][] datas = new String[0][0]; //table�����ݣ���������ϲ��ĵ�Ԫ�� 
    private ArrayList<HashMap<Integer, Integer>> rowPoss; 
    private ArrayList<HashMap<Integer, Integer>> rowCounts; 
    private int datamark = 0; //���BillListModel��������
  
    public CombineData(Object[][] datas, List<Integer> combineColumns) { 
        if(datas!=null&&combineColumns!=null){
        	this.datas = datas; 
            for (Integer column : combineColumns) { 
                if (column < 0) { 
                    continue; 
                } 
                this.combineColumns.add(column); 
            }
        	
        	process(); 
        }
    } 
    
    public CombineData(Object[][] datas, List<Integer> combineColumns, int datamark) { 
        if(datas!=null&&combineColumns!=null){
        	this.datas = datas; 
        	this.datamark = datamark; 
            for (Integer column : combineColumns) { 
                if (column < 0) { 
                    continue; 
                } 
                this.combineColumns.add(column); 
            }
        	
        	process(); 
        }
    }
  
    private void process() { 
        rowPoss = new ArrayList<HashMap<Integer, Integer>>(); 
        rowCounts = new ArrayList<HashMap<Integer, Integer>>(); 
  
        for (Integer integer : combineColumns) { 
            HashMap<Integer, Integer> rowPos = new HashMap<Integer, Integer>(); 
            HashMap<Integer, Integer> rowCount = new HashMap<Integer, Integer>(); 
  
            String pre = ""; 
            int count = 0; 
            int start = 0; 
            for (int i = 0; i < datas.length; i++) { 
            	Object[] data = datas[i];
            	String viewvalue = getObjectValue(data[integer+datamark]); //������������
                if (pre.equals(viewvalue)) { 
                    count++; 
                } else { 
                    rowCount.put(start, count); 
                    pre = viewvalue; 
                    count = 1; 
                    start = i; 
                } 
                rowPos.put(i, start); 
            } 
            rowCount.put(start, count); 
  
            rowPoss.add(rowPos); 
            rowCounts.add(rowCount); 
        } 
    } 
  
    /** 
     * ����table��row��column�е�Ԫ���������� 
     */
    public int span(int row, int column) { 
    	if(row<0){ //���������Ϊ-1
    		return 1; 
    	}
    	
        int index = combineColumns.lastIndexOf(column); 
        if (index != -1) { 
            return rowCounts.get(index).get(rowPoss.get(index).get(row)); 
        } else { 
            return 1; 
        } 
    } 
  
    /** 
     * ����table��row��column�е�Ԫ�����ڵĺϲ���Ԫ�����ʼ��λ�� 
     */
    public int visibleCell(int row, int column) { 
        int index = combineColumns.lastIndexOf(column); 
        if ((index != -1) && row > -1) { 
            return rowPoss.get(index).get(row); 
        } else { 
            return row; 
        } 
    } 
    
	private String getObjectValue(Object _obj) {
		String realvalue = "";
		if (_obj != null) {
			if (_obj instanceof String) {
				realvalue = _obj.toString();
			}
			return realvalue;
		}
		return "";
	}
	
    public int getdatasLen(){
    	return datas.length;
    }
}
