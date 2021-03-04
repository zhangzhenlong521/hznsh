package cn.com.pushworld.salary.bs.report_;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

/**
 * 通过hms原始数据 rcv配置数据
 * 返回最终结果集HashMap getResult()
 * 具体思路是:
 * A 遍历hms原始数据获取 上表头末级节点数组 左表头末级节点数组 ReportTitleTree对象初始数据
 * B 遍历hms原始数据 根据 上表头末级节点数组 左表头末级节点数组 获取 末级节点交叉结果集 即hms数组索引集
 * C 根据 末级节点交叉结果集 即hms数组索引集 进行数量 求和等计算
 * D 小计计算是根据ReportTitleTree对象中hm_sub小计标记进行相应运算
 */
public class ReportCrossEngine {
	private HashMap<String, String>[] hms = null; // 初始数据
	private ReportConfigVO rcv = null; // 配置数据
	private HashMap hm_cache = new HashMap(); // 缓存集
	private HashMap<String, Integer> hm_null = new HashMap<String, Integer>(); //空值标记
	
	public ReportCrossEngine(HashMap<String, String>[] _hms, ReportConfigVO _rcv){
		this.hms = _hms;
		this.rcv = _rcv;
	}
	
    private Object getResultByType(String _type){
    	if(_type.equals("titleleft_tree")||_type.equals("titleleft_final")){
    		if(!hm_cache.containsKey("titleleft_tree")||!hm_cache.containsKey("titleleft_final")){
    			setTitleResult(rcv.getLefttitle(), "titleleft"); // 左表头
    		}
    	}else if(_type.equals("titletop_tree")||_type.equals("titletop_final")){
    		if(!hm_cache.containsKey("titletop_tree")||!hm_cache.containsKey("titletop_final")){
    			setTitleResult(rcv.getToptitle(), "titletop"); // 上表头
    		}
    	}else if(_type.equals("cross_result")&&!hm_cache.containsKey("cross_result")){
    		setCrossResult(); // 末级节点交叉结果集
    	}else if(_type.equals("cross_num")&&!hm_cache.containsKey("cross_num")){
    		setCrossResult_num(); // 末级节点交叉结果集_数量
    	}else if(_type.equals("cross_num_tot_left")&&!hm_cache.containsKey("cross_num_tot_left")){
    		setCrossResult_num_tot("left"); // 末级节点交叉结果集_数量_合计_left
    	}else if(_type.equals("cross_num_tot_top")&&!hm_cache.containsKey("cross_num_tot_top")){
    		setCrossResult_num_tot("top"); // 末级节点交叉结果集_数量_合计_top
    	}else if(_type.equals("cross_num_avg_left")&&!hm_cache.containsKey("cross_num_avg_left")){
    		setCrossResult_num_avg("left", false); // 末级节点交叉结果集_数量_平均_left
    	}else if(_type.equals("cross_num_avg_top")&&!hm_cache.containsKey("cross_num_avg_top")){
    		setCrossResult_num_avg("top", false); // 末级节点交叉结果集_数量_平均_top
    	}else if(_type.equals("cross_num_avg_left_null")&&!hm_cache.containsKey("cross_num_avg_left_null")){
    		setCrossResult_num_avg("left", true); // 末级节点交叉结果集_数量_平均_left
    	}else if(_type.equals("cross_num_avg_top_null")&&!hm_cache.containsKey("cross_num_avg_top_null")){
    		setCrossResult_num_avg("top", true); // 末级节点交叉结果集_数量_平均_top
    	}
    	
    	return hm_cache.get(_type);
    }
    
    private Object getResultByType(String _type, String _column){
        if(_type.equals("cross_value")&&!hm_cache.containsKey("cross_value_"+_column)){
    		setCrossResultColumn_value(_column); // 某列 末级节点交叉结果集_原值
    	}else if(_type.equals("cross_sum")&&!hm_cache.containsKey("cross_sum_"+_column)){
    		setCrossResultColumn_sum(_column); // 某列 末级节点交叉结果集_求和
    	}else if(_type.equals("cross_sum_tot_left")&&!hm_cache.containsKey("cross_sum_tot_left_"+_column)){
    		setCrossResultColumn_sum_tot("left", _column); // 某列 末级节点交叉结果集_求和_合计_left
    	}else if(_type.equals("cross_sum_tot_top")&&!hm_cache.containsKey("cross_sum_tot_top_"+_column)){
    		setCrossResultColumn_sum_tot("top", _column); // 某列 末级节点交叉结果集_求和_合计_top
    	}else if(_type.equals("cross_sum_avg_left")&&!hm_cache.containsKey("cross_sum_avg_left_"+_column)){
    		setCrossResultColumn_sum_avg("left", _column, false); // 某列 末级节点交叉结果集_求和_平均_left
    	}else if(_type.equals("cross_sum_avg_top")&&!hm_cache.containsKey("cross_sum_avg_top_"+_column)){
    		setCrossResultColumn_sum_avg("top", _column, false); // 某列 末级节点交叉结果集_求和_平均_top
    	}else if(_type.equals("cross_sum_avg_left_null")&&!hm_cache.containsKey("cross_sum_avg_left_null_"+_column)){
    		setCrossResultColumn_sum_avg("left", _column, true); // 某列 末级节点交叉结果集_求和_平均_left
    	}else if(_type.equals("cross_sum_avg_top_null")&&!hm_cache.containsKey("cross_sum_avg_top_null_"+_column)){
    		setCrossResultColumn_sum_avg("top", _column, true); // 某列 末级节点交叉结果集_求和_平均_top
    	}
    	
    	return hm_cache.get(_type+"_"+_column);
    }
	
	private String[] getTitleLeftFinal(){
		return (String[])getResultByType("titleleft_final");
	}
	
	private HashMap<String, ReportTitleVO> getTitleLefttree(){
		return (HashMap<String, ReportTitleVO>)getResultByType("titleleft_tree");
	}
	
	private String[] getTitleTopFinal(){
		return (String[])getResultByType("titletop_final");
	}
	
	private HashMap<String, ReportTitleVO> getTitleToptree(){
		return (HashMap<String, ReportTitleVO>)getResultByType("titletop_tree");
	}
	
    /**
	 * 表头基础数据
	 */
	private void setTitleResult(String[] _title, String _type){	
		HashMap<String, ReportTitleVO> hm = new HashMap<String, ReportTitleVO>();
		Set<String> finalset = new HashSet<String>(); // 末级节点
		
		hm.put("#", new ReportTitleVO());
		if(_title!=null&&_title.length>0){
			for(int i=0; i<hms.length; i++){
				if(hms[i]==null){
					continue;
				}
				
				String pname = "";
				for(int t=0; t<_title.length; t++){
					String name = hms[i].get(_title[t]);
					int level = t+1;
					pname += "*"+name; // 加*
					
					if(hm.containsKey(pname)){
						if(level<_title.length){ // 追加子节点
							hm.get(pname).childsetAdd(hms[i].get(_title[level]));	
						}
					}else{
						ReportTitleVO rtv = new ReportTitleVO();
						rtv.setName(name);
						rtv.setLevel(level);
						if(level==_title.length){
							rtv.setChild(true);
							finalset.add(pname);
						}
						if(level<_title.length){ // 追加子节点
							rtv.childsetAdd(hms[i].get(_title[level]));	
						}
				
						hm.put(pname, rtv);
						
						if(t==0){
							hm.get("#").childsetAdd(name);	
						}
					}
				}
			}
		}else{
			hm.get("#").setChild(true);
		}

		if(_type.equals("titleleft")){
			hm_cache.put("titleleft_tree", hm); 
			hm_cache.put("titleleft_final", (String[])finalset.toArray(new String[finalset.size()])); // 末级节点数据集
		}else if(_type.equals("titletop")){
			hm_cache.put("titletop_tree", hm); 
			hm_cache.put("titletop_final", (String[])finalset.toArray(new String[finalset.size()])); // 末级节点数据集
		}
	}
	
	private HashMap<String, String[]> getCrossResult(){
		return (HashMap<String, String[]>)getResultByType("cross_result");
	}
	
    /**
	 * 末级节点交叉结果集
	 */
	private void setCrossResult(){
		HashMap<String, String> hm = new HashMap<String, String>();
		String[] lefttitle = rcv.getLefttitle();
		String[] toptitle = rcv.getToptitle();
		for(int i=0; i<hms.length; i++){
			if(hms[i]==null){
				continue;
			}
			
			StringBuffer sb = new StringBuffer();
			for(int t=0; t<lefttitle.length; t++){
				sb.append("*"); 
				sb.append(hms[i].get(lefttitle[t])); 
			}
			sb.append("_"); 
			for(int t=0; t<toptitle.length; t++){
				sb.append("*"); 
				sb.append(hms[i].get(toptitle[t])); 
			}
			
			String str = sb.toString();
			if(hm.containsKey(str)){
				hm.put(str, hm.get(str)+";"+i);
			}else{
				hm.put(str, Integer.toString(i));
			}
		}
		
		HashMap<String, String[]> hm_cross = new HashMap<String, String[]>();
		
/*		String[] leftfinalarray = getTitleLeftFinal();
		String[] topfinalarray = getTitleTopFinal();
		for(int i=0; i<leftfinalarray.length; i++){
			for(int j=0; j<topfinalarray.length; j++){
				String str = leftfinalarray[i]+"_"+topfinalarray[j];
				hm_cross.put(str, hm.get(str).split(";"));
			}
		}*/
		
		Iterator iterator = hm.entrySet().iterator();
        while(iterator.hasNext()){
            Entry<String, String> entry = (Entry) iterator.next();
			hm_cross.put(entry.getKey(), entry.getValue().split(";"));
        }

        hm_cache.put("cross_result", hm_cross); 
	}
	
	private HashMap<String, Integer> getCrossResult_num(){
		return (HashMap<String, Integer>)getResultByType("cross_num");
	}
	
    /**
	 * 末级节点交叉结果集_数量
	 */
	private void setCrossResult_num(){
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		
		Iterator iterator = getCrossResult().entrySet().iterator();
        while(iterator.hasNext()){
            Entry<String, String[]> entry = (Entry) iterator.next();
            hm.put(entry.getKey(), entry.getValue().length);
        }
        
        hm_cache.put("cross_num", hm); 
	}
	
	private HashMap<String, Integer> getCrossResult_num_tot_left(){
		return (HashMap<String, Integer>)getResultByType("cross_num_tot_left");
	}
	
	private HashMap<String, Integer> getCrossResult_num_tot_top(){
		return (HashMap<String, Integer>)getResultByType("cross_num_tot_top");
	}
	
    /**
	 * 末级节点交叉结果集_数量_合计
	 */
	private void setCrossResult_num_tot(String _type){
		HashMap<String, Integer> hm = new HashMap<String, Integer>();
		
        String[] leftfinal = getTitleLeftFinal();
		String[] topfinal = getTitleTopFinal();
		HashMap<String, Integer> hm_cross_num = getCrossResult_num();
		
		if(_type.equals("left")){
			for(int i=0; i<leftfinal.length; i++){
				int total = 0;
				for(int j=0; j<topfinal.length; j++){
					if(hm_cross_num.containsKey(leftfinal[i]+"_"+topfinal[j])){
						total += hm_cross_num.get(leftfinal[i]+"_"+topfinal[j]);
					}
				}
				hm.put("num_tot_"+leftfinal[i], total);
			}
			hm_cache.put("cross_num_tot_left", hm); 
		}else if(_type.equals("top")){
			for(int i=0; i<topfinal.length; i++){
				int total = 0;
				for(int j=0; j<leftfinal.length; j++){
					if(hm_cross_num.containsKey(leftfinal[j]+"_"+topfinal[i])){
						total += hm_cross_num.get(leftfinal[j]+"_"+topfinal[i]);
					}	
				}
				hm.put("num_tot_"+topfinal[i], total);
			}
			hm_cache.put("cross_num_tot_top", hm); 
		}
	}
	
	private HashMap<String, Double> getCrossResult_num_avg_left(boolean _isnull){
		if(_isnull){
			return (HashMap<String, Double>)getResultByType("cross_num_avg_left_null");
		}else{
			return (HashMap<String, Double>)getResultByType("cross_num_avg_left");
		}
	}
	
	private HashMap<String, Double> getCrossResult_num_avg_top(boolean _isnull){
		if(_isnull){
			return (HashMap<String, Double>)getResultByType("cross_num_avg_top_null");
		}else{
			return (HashMap<String, Double>)getResultByType("cross_num_avg_top");
		}
	}
	
    /**
	 * 末级节点交叉结果集_数量_平均
	 */
	private void setCrossResult_num_avg(String _type, boolean _isnull){
		HashMap<String, Double> hm = new HashMap<String, Double>();
		
		if(_type.equals("left")){
			String[] leftfinal = getTitleLeftFinal();
			HashMap<String, Integer> hm_left = getCrossResult_num_tot_left();
			int topfinal_len = getTitleTopFinal().length;
			
			for(int i=0; i<leftfinal.length; i++){
				int num_null = 0;
				if(_isnull&&hm_null.containsKey("数量_"+leftfinal[i])){
					num_null = hm_null.get("数量_"+leftfinal[i]);
				}
				
				double avg = MathUtil.div((double)hm_left.get("num_tot_"+leftfinal[i]), topfinal_len-num_null, 2);
				hm.put("num_avg_"+leftfinal[i], avg);
			}
			
			if(_isnull){
				hm_cache.put("cross_num_avg_left_null", hm); 
			}else{
				hm_cache.put("cross_num_avg_left", hm); 
			}
		}else if(_type.equals("top")){
			String[] topfinal = getTitleTopFinal();
			HashMap<String, Integer> hm_top = getCrossResult_num_tot_top();
			int leftfinal_len = getTitleLeftFinal().length;
			
			for(int i=0; i<topfinal.length; i++){
				int num_null = 0;
				if(_isnull&&hm_null.containsKey("数量_"+topfinal[i])){
					num_null = hm_null.get("数量_"+topfinal[i]);
				}
				
				double avg = MathUtil.div((double)hm_top.get("num_tot_"+topfinal[i]), leftfinal_len-num_null, 2);
				hm.put("num_avg_"+topfinal[i], avg);
			}
			
			if(_isnull){
				hm_cache.put("cross_num_avg_top_null", hm); 
			}else{
				hm_cache.put("cross_num_avg_top", hm); 
			}
		}
	}

	private HashMap<String, String> getCrossResultColumn_value(String _column){
		return (HashMap<String, String>)getResultByType("cross_value", _column);
	}
	
    /**
	 * 某列 末级节点交叉结果集_原值
	 */
	private void setCrossResultColumn_value(String _column){
		HashMap<String, String> hm = new HashMap<String, String>();
		
		Iterator iterator = getCrossResult().entrySet().iterator();
        while(iterator.hasNext()){
            Entry<String, String[]> entry = (Entry) iterator.next();
            hm.put(entry.getKey(), getValueByIndexs(entry.getValue(), _column));
        }
       
        hm_cache.put("cross_value_"+_column, hm); 
	}
	
	private String getValueByIndexs(String[] _strs, String _column){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<_strs.length; i++){
			String value = hms[Integer.parseInt(_strs[i])].get(_column);
			if(value!=null){
				sb.append(value);
				sb.append(";");
			}
		}
		
		String result = sb.toString();
		if(result.endsWith(";")){
			return result.substring(0, result.length()-1);
		}
		return "";
	}
	
	private HashMap<String, Double> getCrossResultColumn_sum(String _column){
		return (HashMap<String, Double>)getResultByType("cross_sum", _column);
	}
	
    /**
	 * 某列 末级节点交叉结果集_求和
	 */
	private void setCrossResultColumn_sum(String _column){
		HashMap<String, Double> hm = new HashMap<String, Double>();
		
		Iterator iterator = getCrossResult().entrySet().iterator();
        while(iterator.hasNext()){
            Entry<String, String[]> entry = (Entry) iterator.next();
            hm.put(entry.getKey(), getSumByIndexs(entry.getValue(), _column));
        }
       
        hm_cache.put("cross_sum_"+_column, hm); 
	}
	
	private double getSumByIndexs(String[] _strs, String _column){
		double sum = 0.0;
		for(int i=0; i<_strs.length; i++){
			String value = hms[Integer.parseInt(_strs[i])].get(_column);
			sum = MathUtil.add(sum, getDoubleValue(value, 0.0));
		}
		
		return sum;
	}
	
	private double getDoubleValue(String _value, double _nvl){
		try {
			_nvl = Double.parseDouble(_value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return _nvl;
	}
	
	private HashMap<String, Double> getCrossResultColumn_sum_tot_left(String _column){
		return (HashMap<String, Double>)getResultByType("cross_sum_tot_left", _column);
	}
	
	private HashMap<String, Double> getCrossResultColumn_sum_tot_top(String _column){
		return (HashMap<String, Double>)getResultByType("cross_sum_tot_top", _column);
	}
	
    /**
	 * 某列 末级节点交叉结果集_求和_合计
	 */
	private void setCrossResultColumn_sum_tot(String _type, String _column){
		HashMap<String, Double> hm = new HashMap<String, Double>();
		
        String[] leftfinal = getTitleLeftFinal();
		String[] topfinal = getTitleTopFinal();
		HashMap<String, Double> hm_cross_sum = getCrossResultColumn_sum(_column);
		
		if(_type.equals("left")){
			for(int i=0; i<leftfinal.length; i++){
				double total = 0.0;
				for(int j=0; j<topfinal.length; j++){
					if(hm_cross_sum.containsKey(leftfinal[i]+"_"+topfinal[j])){
						total = MathUtil.add(total, hm_cross_sum.get(leftfinal[i]+"_"+topfinal[j]));
					}	
				}
				hm.put("sum_tot_"+leftfinal[i], total);
			}
			hm_cache.put("cross_sum_tot_left_"+_column, hm); 
		}else if(_type.equals("top")){
			for(int i=0; i<topfinal.length; i++){
				double total = 0.0;
				for(int j=0; j<leftfinal.length; j++){
					if(hm_cross_sum.containsKey(leftfinal[j]+"_"+topfinal[i])){
						total = MathUtil.add(total, hm_cross_sum.get(leftfinal[j]+"_"+topfinal[i]));
					}
				}
				hm.put("sum_tot_"+topfinal[i], total);
			}
			hm_cache.put("cross_sum_tot_top_"+_column, hm); 
		}
	}
	
	private HashMap<String, Double> getCrossResultColumn_sum_avg_left(String _column, boolean _isnull){
		if(_isnull){
			return (HashMap<String, Double>)getResultByType("cross_sum_avg_left_null", _column);
		}else{
			return (HashMap<String, Double>)getResultByType("cross_sum_avg_left", _column);
		}
	}
	
	private HashMap<String, Double> getCrossResultColumn_sum_avg_top(String _column, boolean _isnull){
		if(_isnull){
			return (HashMap<String, Double>)getResultByType("cross_sum_avg_top_null", _column);
		}else{
			return (HashMap<String, Double>)getResultByType("cross_sum_avg_top", _column);
		}
	}
	
    /**
	 * 某列 末级节点交叉结果集_求和_平均
	 */
	private void setCrossResultColumn_sum_avg(String _type, String _column, boolean _isnull){
		HashMap<String, Double> hm = new HashMap<String, Double>();
		
		if(_type.equals("left")){
			String[] leftfinal = getTitleLeftFinal();
			HashMap<String, Double> hm_left = getCrossResultColumn_sum_tot_left(_column);
			int topfinal_len = getTitleTopFinal().length;
			
			for(int i=0; i<leftfinal.length; i++){
				int num_null = 0;
				if(_isnull&&hm_null.containsKey(_column+"_"+leftfinal[i])){
					num_null = hm_null.get(_column+"_"+leftfinal[i]);
				}
				
				double avg = MathUtil.div((double)hm_left.get("sum_tot_"+leftfinal[i]), topfinal_len-num_null, 2);
				hm.put("sum_avg_"+leftfinal[i], avg);
			}
			
			if(_isnull){
				hm_cache.put("cross_sum_avg_left_null_"+_column, hm); 
			}else{
				hm_cache.put("cross_sum_avg_left_"+_column, hm); 
			}
		}else if(_type.equals("top")){
			String[] topfinal = getTitleTopFinal();
			HashMap<String, Double> hm_top = getCrossResultColumn_sum_tot_top(_column);
			int leftfinal_len = getTitleLeftFinal().length;
			
			for(int i=0; i<topfinal.length; i++){
				int num_null = 0;
				if(_isnull&&hm_null.containsKey(_column+"_"+topfinal[i])){
					num_null = hm_null.get(_column+"_"+topfinal[i]);
				}
				
				double avg = MathUtil.div((double)hm_top.get("sum_tot_"+topfinal[i]), leftfinal_len-num_null, 2);
				hm.put("sum_avg_"+topfinal[i], avg);
			}
			
			if(_isnull){
				hm_cache.put("cross_sum_avg_top_null_"+_column, hm); 
			}else{
				hm_cache.put("cross_sum_avg_top_"+_column, hm); 
			}
		}
	}
	
    /**
	 * 小合计 左表头 数量
	 */
	private int getLeftSub_num(int x, int y, HashMap hm_result, HashMap<String, Integer> hm_sub){
		int sub = 0;
		int sub_len  = hm_sub.get(x+"_");
		for(int i=0;i<sub_len;i++){
			int x_last = x-i-1;
			if(!hm_sub.containsKey(x_last+"_")&&hm_result.get(x_last+"_"+y)!=null){
				sub += (Integer)hm_result.get(x_last+"_"+y);
			}
		}
		
		return sub;
	}
	
    /**
	 * 小合计 上表头 数量
	 */
	private int getTopSub_num(int x, int y, HashMap hm_result, HashMap<String, Integer> hm_sub){
		int sub = 0;
		int sub_len  = hm_sub.get("_"+y);
		for(int i=0;i<sub_len;i++){
			int y_last = y-i-1;
			if(!hm_sub.containsKey("_"+y_last)&&hm_result.get(x+"_"+y_last)!=null){
				sub += (Integer)hm_result.get(x+"_"+y_last);
			}
		}
		
		return sub;
	}
	
    /**
	 * 小合计 左表头 求和
	 */
	private double getLeftSub_sum(int x, int y, HashMap hm_result, HashMap<String, Integer> hm_sub){
		double sub = 0.0;
		int sub_len  = hm_sub.get(x+"_");
		for(int i=0;i<sub_len;i++){
			int x_last = x-i-1;
			if(!hm_sub.containsKey(x_last+"_")&&hm_result.get(x_last+"_"+y)!=null){
				sub = MathUtil.add(sub, (Double)hm_result.get(x_last+"_"+y));
			}
		}
		
		return sub;
	}
	
    /**
	 * 小合计 上表头 求和
	 */
	private double getTopSub_sum(int x, int y, HashMap hm_result, HashMap<String, Integer> hm_sub){
		double sub = 0.0;
		int sub_len  = hm_sub.get("_"+y);
		for(int i=0;i<sub_len;i++){
			int y_last = y-i-1;
			if(!hm_sub.containsKey("_"+y_last)&&hm_result.get(x+"_"+y_last)!=null){
				sub = MathUtil.add(sub, (Double)hm_result.get(x+"_"+y_last));
			}
		}
		
		return sub;
	}
	
    /**
	 * 小平均 左表头
	 */
	private double getLeftSub_avg(int x, int y, HashMap hm_result, HashMap<String, Integer> hm_sub, String type, boolean isnull){
		int avg = 0;
		int sub_len  = hm_sub.get(x+"_"); 
		for(int i=0;i<sub_len;i++){
			int x_last = x-i-1;
			if(!hm_sub.containsKey(x_last+"_")){
				avg++;
			}
		}
		
		int num_null = 0;
		if(isnull){
			num_null = getLeftSub_num_null(x, y, hm_result, hm_sub);
		}
		
		if((avg-num_null)==0){
			return 0.0;
		}
		
		if(type.equals("数量")){
			return MathUtil.div((double)getLeftSub_num(x, y, hm_result, hm_sub), avg-num_null, 2);
		}
		
		return MathUtil.div((double)getLeftSub_sum(x, y, hm_result, hm_sub), avg-num_null, 2);
	}
	
    /**
	 * 小平均 上表头
	 */
	private double getTopSub_avg(int x, int y, HashMap hm_result, HashMap<String, Integer> hm_sub, String type, boolean isnull){
		int avg = 0;
		int sub_len  = hm_sub.get("_"+y);
		for(int i=0;i<sub_len;i++){
			int y_last = y-i-1;
			if(!hm_sub.containsKey("_"+y_last)){
				avg++;
			}
		}
		
		int num_null = 0;
		if(isnull){
			num_null = getTopSub_num_null(x, y, hm_result, hm_sub);
		}
		
		if((avg-num_null)==0){
			return 0.0;
		}
		
		if(type.equals("数量")){
			return MathUtil.div((double)getTopSub_num(x, y, hm_result, hm_sub), avg-num_null, 2);
		}
		
		return MathUtil.div((double)getTopSub_sum(x, y, hm_result, hm_sub), avg-num_null, 2);
	}
	
    /**
	 * 左表头 空值数量
	 */
	private int getLeftSub_num_null(int x, int y, HashMap hm_result, HashMap<String, Integer> hm_sub){
		int num_null = 0;
		int sub_len  = hm_sub.get(x+"_");
		for(int i=0;i<sub_len;i++){
			int x_last = x-i-1;
			if(!hm_sub.containsKey(x_last+"_")&&hm_result.get(x_last+"_"+y)==null){
				num_null++;
			}
		}
		
		return num_null;
	}
	
    /**
	 * 上表头 空值数量
	 */
	private int getTopSub_num_null(int x, int y, HashMap hm_result, HashMap<String, Integer> hm_sub){
		int num_null = 0;
		int sub_len  = hm_sub.get("_"+y);
		for(int i=0;i<sub_len;i++){
			int y_last = y-i-1;
			if(!hm_sub.containsKey("_"+y_last)&&hm_result.get(x+"_"+y_last)==null){
				num_null++;
			}
		}
		
		return num_null;
	}
	
    /**
	 * 标记空值
	 */
	private void setHm_null(String str){
		if(hm_null.containsKey(str)){
			hm_null.put(str, hm_null.get(str)+1);
		}else{
			hm_null.put(str, 1);
		}
	}
	
    /**
	 * 报表 结果集
	 */
	public HashMap getResult(){		
		ReportTitleTree rtt = new ReportTitleTree(getTitleLefttree(),getTitleToptree(),rcv);
		
		String[] left = rtt.getLeft();
		String[] top = rtt.getTop();
		HashMap hm_result = rtt.getResult();
		HashMap hm_again = rtt.getAgain();
		HashMap<String, Integer> hm_sub = rtt.getSub();
		
		for(int x=rtt.getTop_len(); x<left.length; x++){
			for(int y=rtt.getLeft_len(); y<top.length; y++){
				String[] agains = ((String)hm_again.get(y)).split("_");
				String cname = agains[0];
				String calculate = agains[1];
				
				if(calculate.equals("求和")){
					if(left[x].startsWith("*")){
						if(top[y].startsWith("*")){
							if(cname.equals("数量")){
								hm_result.put(x+"_"+y, getCrossResult_num().get(left[x]+"_"+top[y]));
							}else{
								hm_result.put(x+"_"+y, getCrossResultColumn_sum(cname).get(left[x]+"_"+top[y]));
							}
							
							if(hm_result.get(x+"_"+y)==null){
								setHm_null(cname+"_"+left[x]);
								setHm_null(cname+"_"+top[y]);
							}
						}else{
							if(top[y].equals("合计")){
								if(cname.equals("数量")){
									hm_result.put(x+"_"+y, getCrossResult_num_tot_left().get("num_tot_"+left[x]));
								}else{
									hm_result.put(x+"_"+y, getCrossResultColumn_sum_tot_left(cname).get("sum_tot_"+left[x]));
								}	
							}else if(top[y].equals("平均")){
								if(cname.equals("数量")){
									hm_result.put(x+"_"+y, getCrossResult_num_avg_left(false).get("num_avg_"+left[x]));
								}else{
									hm_result.put(x+"_"+y, getCrossResultColumn_sum_avg_left(cname, false).get("sum_avg_"+left[x]));
								}	
							}else if(top[y].equals("小合计")){
								if(hm_sub.get(x+"_")!=null){
									continue;
								}
								
								if(cname.equals("数量")){
									hm_result.put(x+"_"+y, getTopSub_num(x, y, hm_result, hm_sub));
								}else{
									hm_result.put(x+"_"+y, getTopSub_sum(x, y, hm_result, hm_sub));
								}
							}else if(top[y].equals("小平均")||top[y].equals("小平均空")){
								if(hm_sub.get(x+"_")!=null){
									continue;
								}
								
								boolean isnull = false;
								if(top[y].equals("小平均空")){
									isnull = true;
								}
								
								hm_result.put(x+"_"+y, getTopSub_avg(x, y, hm_result, hm_sub, cname, isnull));
							}
						}
					}else if(left[x].equals("合计")){
						if(top[y].startsWith("*")){
							if(cname.equals("数量")){
								hm_result.put(x+"_"+y, getCrossResult_num_tot_top().get("num_tot_"+top[y]));
							}else{
								hm_result.put(x+"_"+y, getCrossResultColumn_sum_tot_top(cname).get("sum_tot_"+top[y]));
							}
						}
					}else if(left[x].equals("平均")){
						if(top[y].startsWith("*")){
							if(cname.equals("数量")){
								hm_result.put(x+"_"+y, getCrossResult_num_avg_top(false).get("num_avg_"+top[y]));
							}else{
								hm_result.put(x+"_"+y, getCrossResultColumn_sum_avg_top(cname, false).get("sum_avg_"+top[y]));
							}	
						}
					}else if(left[x].equals("小合计")){
						if(hm_sub.get("_"+y)!=null){
							continue;
						}
						
						if(cname.equals("数量")){
							hm_result.put(x+"_"+y, getLeftSub_num(x, y, hm_result, hm_sub));
						}else{
							hm_result.put(x+"_"+y, getLeftSub_sum(x, y, hm_result, hm_sub));
						}
					}else if(left[x].equals("小平均")||left[x].equals("小平均空")){
						if(hm_sub.get("_"+y)!=null){
							continue;
						}
						
						boolean isnull = false;
						if(left[x].equals("小平均空")){
							isnull = true;
						}
						
						hm_result.put(x+"_"+y, getLeftSub_avg(x, y, hm_result, hm_sub, cname, isnull));
					}
				}else if(calculate.equals("原值")){
					if(left[x].startsWith("*")){
						if(top[y].startsWith("*")){
							hm_result.put(x+"_"+y, getCrossResultColumn_value(cname).get(left[x]+"_"+top[y]));
						}
					}
				}
			}
		}
		
        // 去空计算
		for(int x=rtt.getTop_len(); x<left.length; x++){
			for(int y=rtt.getLeft_len(); y<top.length; y++){
				String[] agains = ((String)hm_again.get(y)).split("_");
				String cname = agains[0];
				String calculate = agains[1];
				
				if(calculate.equals("求和")){
					if(left[x].startsWith("*")){
						if(!top[y].startsWith("*")){
							if(top[y].equals("平均空")){							
								if(cname.equals("数量")){
									hm_result.put(x+"_"+y, getCrossResult_num_avg_left(true).get("num_avg_"+left[x]));
								}else{
									hm_result.put(x+"_"+y, getCrossResultColumn_sum_avg_left(cname, true).get("sum_avg_"+left[x]));
								}	
							}
						}
					}else if(left[x].equals("平均空")){
						if(top[y].startsWith("*")){
							if(cname.equals("数量")){
								hm_result.put(x+"_"+y, getCrossResult_num_avg_top(true).get("num_avg_"+top[y]));
							}else{
								hm_result.put(x+"_"+y, getCrossResultColumn_sum_avg_top(cname, true).get("sum_avg_"+top[y]));
							}	
						}
					}	
				}
			}
		}
		
		HashMap result = new HashMap();
		result.put("result", hm_result);
		result.put("combine", rtt.getCombine());
		result.put("rows", rtt.getRows());
		result.put("cols", rtt.getCols());
		result.put("lefttitle_len", rtt.getLeft_len());
		result.put("toptitle_len", rtt.getTop_len());
		
		return result;
	}
	
    /**
	 * HTML结果集
	 */
	public String getReportHtml(HashMap _hm){
		HashMap result = (HashMap)_hm.get("result");
		HashMap combine = (HashMap)_hm.get("combine");
		int rows = (Integer)_hm.get("rows");
		int cols = (Integer)_hm.get("cols");
		int lefttitle_len = (Integer)_hm.get("lefttitle_len");
		int toptitle_len = (Integer)_hm.get("toptitle_len");
		
		StringBuffer sb = new StringBuffer();
		sb.append("<html><head><title></title>");
		sb.append("<style type=\"text/css\">");
		sb.append(".td_border {border-color:#888888;border-style:solid;border-top-width:1px;border-bottom-width:1px;border-left-width:1px;border-right-width:1px;font-size:12px;}");
		sb.append("</style>");
		sb.append("</head><body>");
		sb.append("<table style=\"border-collapse:collapse;\" border=\"0\" cellpadding=\"5\">");
		for (int x = 0; x < rows; x++) {
			sb.append("<tr>");
			for (int y = 0; y < cols; y++) {
				if(x<toptitle_len&&y<lefttitle_len){
					sb.append("<td class=\"td_border\">");
					sb.append(result.get(x+"_"+y));
					sb.append("</td>");
					continue;
				}
				
				if(x<toptitle_len||y<lefttitle_len){
					if(combine.containsKey(x+"_"+y)){
						String[] spans = ((String)combine.get(x+"_"+y)).split(",");
						if(spans[0].equals("1")){
							sb.append("<td class=\"td_border\" align=\"center\" bgcolor=\"#84C1FF\" colspan=\""+spans[1]+"\">");
							sb.append(result.get(x+"_"+y));
							sb.append("</td>");		
						}else{
							sb.append("<td class=\"td_border\" align=\"center\" bgcolor=\"#84C1FF\" rowspan=\""+spans[0]+"\">");
							sb.append(result.get(x+"_"+y));
							sb.append("</td>");	
						}
					}else{
						if(result.get(x+"_"+y)!=null){
							sb.append("<td class=\"td_border\" bgcolor=\"#84C1FF\" align=\"center\">");
							sb.append(result.get(x+"_"+y));
							sb.append("</td>");	
						}
					}
				}else{
					sb.append("<td class=\"td_border\" align=\"right\">");
					if(result.get(x+"_"+y)==null){
						sb.append("");
					}else{
						sb.append(result.get(x+"_"+y));	
					}
					sb.append("</td>");
				}
			}
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("</body></html>");
		
		return sb.toString();
	}
	
}
