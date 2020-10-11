package cn.com.pushworld.salary.bs.report_;

import java.util.Comparator;
import java.util.HashMap;

/**
 * 数组排序 参照另一个数组顺序 比较器
 */

public class StringComparator implements Comparator<String>{
	private HashMap<String, Integer> hm = new HashMap<String, Integer>();
	private int arrayslen = 0;
	
	public StringComparator(String[] _orderarrays, int _arrayslen){
		for (int i = 0; i < _orderarrays.length; i++) {
			this.hm.put(_orderarrays[i], i);
		}
		this.arrayslen = _arrayslen;
	}

	public int compare(String str0, String str1) {
		int str0_index = getOrderarraysIndex(str0);
		int str1_index = getOrderarraysIndex(str1);
		if(str0_index>str1_index){
			return 1;
		}else if(str0_index<str1_index){
			return -1;
		}else{
			return 0;	
		}
	}
	
	private int getOrderarraysIndex(String str){
		if(hm.containsKey(str)){
			return hm.get(str);
		}else{
			return this.arrayslen;
		}
	}

}
