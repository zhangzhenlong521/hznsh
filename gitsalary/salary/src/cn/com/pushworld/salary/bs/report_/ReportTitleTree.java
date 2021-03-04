package cn.com.pushworld.salary.bs.report_;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * 通过hm_left hm_top rcv配置数据构建ReportTitleTree对象
 * 返回报表结果集hm_result 末级节点al_left al_top 再次计算标记hm_again 
 * 左表头长度left_len 上表头长度top_len 行数rows 列数cols
 */
public class ReportTitleTree {
	private BaseUtil baseutil = new BaseUtil();
	private HashMap<String, ReportTitleVO> hm_left = new HashMap<String, ReportTitleVO>();
	private HashMap<String, ReportTitleVO> hm_top = new HashMap<String, ReportTitleVO>();
	private ReportConfigVO rcv = new ReportConfigVO(); // 配置数据
	private HashMap hm_result = new HashMap(); // 结果集
	private HashMap hm_result_name = new HashMap(); // 结果集 重命名
	private HashMap hm_combine = new HashMap(); // 合并集
	private ArrayList<String> al_left = new ArrayList<String>(); // 末级节点_left
	private ArrayList<String> al_top = new ArrayList<String>(); // 末级节点_top
	private HashMap hm_again = new HashMap(); // 再次计算标记
	private HashMap<String, Integer> hm_sub = new HashMap<String, Integer>(); // 小计标记
	private int left_len = 0; // 左表头长度
	private int top_len = 0; // 上表头长度
	private int rows = 0; // 行数
	private int cols = 0; // 列数

	public ReportTitleTree(HashMap<String, ReportTitleVO> _hm_left, HashMap<String, ReportTitleVO> _hm_top, ReportConfigVO _rcv){
		this.hm_left = _hm_left;
		this.hm_top = _hm_top;
		this.rcv = _rcv;
		
		dealTitleLen(); //表头初始长度
		
		if(rcv.isFront_topagain()){ //合计在前
			dealTitleLeftAgain(); //合计 平均等
			enumTitleLeftTree("#"); //递归处理左表头
		}else{
			enumTitleLeftTree("#"); //递归处理左表头
			dealTitleLeftAgain(); //合计 平均等
		}
		
		if(rcv.getCross().length>1){
			dealTitleTop();
		}else{
			if(rcv.isFront_topagain()){ //合计在前
				dealTitleTopAgain(rcv.getCrossvos()[0]); //合计 平均等
				enumTitleTopTree("#", rcv.getCrossvos()[0].getCname()); //递归处理上表头
			}else{
				enumTitleTopTree("#", rcv.getCrossvos()[0].getCname()); //递归处理上表头
				dealTitleTopAgain(rcv.getCrossvos()[0]); //合计 平均等
			}	
		}
		
		if(!rcv.isCombine_left()){
			fillCombine_Left(); //左表头不合并单元格
		}
		
		if(hm_result_name.size()>0){
			renameResult(); //重命名
		}
	}
	
    /**
	 * 左/上 表头初始长度
	 */
	private void dealTitleLen(){
		String[] lefttitle = rcv.getLefttitle();
		if(lefttitle.length>0){
			for(int i=0; i<lefttitle.length; i++){
				al_top.add(lefttitle[i]);
				cols++;
			}
		}
		
		String[] toptitle = rcv.getToptitle();
		if(toptitle.length>0){
			for(int i=0; i<toptitle.length; i++){
				al_left.add(toptitle[i]);
				rows++;
			}
		}
		
		if(rcv.getCross().length>1){
			al_left.add("");
			rows++;
		}
		
		if(lefttitle.length>0){
			for(int i=0; i<lefttitle.length; i++){
				hm_result.put("0_"+i, lefttitle[i]);
				hm_combine.put("0_"+i, rows+",1");
			}
		}
		
		left_len = cols;
		top_len = rows;
	}
	
    /**
	 * 上表头 多cross处理
	 */
	private void dealTitleTop(){
		ReportConfigCrossVO[] crossvos = rcv.getCrossvos();
		
		int combine = rcv.getLefttitle().length;
		for(int i=0; i<crossvos.length; i++){
			String cname = crossvos[i].getCname();
			String x_y = "0_"+cols;
			
			String cname_name = crossvos[i].getCname_name();
			if(cname_name.equals("数量_求和")){
				hm_result.put(x_y, "数量");
			}else{
				hm_result.put(x_y, cname_name);
			}
			
			if(rcv.isFront_topagain()){
				dealTitleTopAgain(crossvos[i]);
				enumTitleTopTree("#", cname);
			}else{
				enumTitleTopTree("#", cname);
				dealTitleTopAgain(crossvos[i]);
			}	
			hm_combine.put(x_y, "1,"+(cols-combine));
			combine = cols;
		}
	}
	
    /**
	 * enum左表头
	 */
	private void enumTitleLeftTree(String _pname){
		ReportTitleVO rtv = hm_left.get(_pname);
		
		if(_pname.equals("#")){
			String[] childs = rtv.getChildsetArrays();
			
			//排序
			String[] orders = rcv.getLeftvos()[0].getOrders();
			if(orders.length>0){
				baseutil.sortArraysByOrderarrays(childs, orders);
			}
			
			for(int i=0; i<childs.length; i++){
				enumTitleLeftTree("*"+childs[i]);
			}
		}else{
			String x_y = rows+"_"+(rtv.getLevel()-1);
			hm_result.put(x_y, rtv.getName());
			
			if(rtv.isChild()){
				al_left.add(_pname);
				rows++;	
			}else{
				String[] childs = rtv.getChildsetArrays();
				int rows_mark = rows;
				
				//排序
				String[] orders = rcv.getLeftvos()[rtv.getLevel()].getOrders();
				if(orders.length>0){
					baseutil.sortArraysByOrderarrays(childs, orders);
				}
				
				for(int i=0; i<childs.length; i++){
					enumTitleLeftTree(_pname+"*"+childs[i]);
				}
				
				hm_combine.put(x_y, (rows-rows_mark)+",1");
				
				//小计计算追加
				String[] subagain = rcv.getLeftvos()[rtv.getLevel()-1].getSubagain();
				String[] subagain_name = rcv.getLeftvos()[rtv.getLevel()-1].getSubagain_name();
		        if(subagain.length>0){
		        	for(int i=0; i<subagain.length; i++){
						String x_y_sub = rows+"_"+(rtv.getLevel()-1);
						hm_result.put(x_y_sub, subagain[i]);
						hm_result_name.put(x_y_sub, subagain_name[i]);
						
						hm_combine.put(x_y_sub, "1,"+(getLeft_len()-rtv.getLevel()+1));
						
						hm_sub.put(rows+"_", rows-rows_mark);
						
						//左表头 不合并 标记
						for(int j=rtv.getLevel()-1; j<getLeft_len(); j++){
							hm_sub.put(rows+"_"+j, 0);
	                    }
						
						al_left.add(subagain[i]);
						rows++;	
		        	}
		        }
			}
		}
	}
	
    /**
	 * enum上表头
	 */
	private void enumTitleTopTree(String _pname, String _cname){
		ReportTitleVO rtv = hm_top.get(_pname);
		
    	int start = 1;
    	if(rcv.getCross().length>1){
    		start = 0;
    	}
    	
    	if(_pname.equals("#")){
			String[] childs = rtv.getChildsetArrays();
			
			//排序
			String[] orders = rcv.getTopvos()[0].getOrders();
			if(orders.length>0){
				baseutil.sortArraysByOrderarrays(childs, orders);
			}
			
			for(int i=0; i<childs.length; i++){
				enumTitleTopTree("*"+childs[i], _cname);
			}
    	}else{
			String x_y = (rtv.getLevel()-start)+"_"+cols;
			hm_result.put(x_y, rtv.getName());

			if(rtv.isChild()){
				hm_again.put(cols, _cname);
				al_top.add(_pname);
				cols++;
			}else{
				String[] childs = rtv.getChildsetArrays();
				int cols_mark = cols;
				
				//排序
				String[] orders = rcv.getTopvos()[rtv.getLevel()].getOrders();
				if(orders.length>0){
					baseutil.sortArraysByOrderarrays(childs, orders);
				}
				
				for(int i=0; i<childs.length; i++){
					enumTitleTopTree(_pname+"*"+childs[i], _cname);
				}
				
				hm_combine.put(x_y, "1,"+(cols-cols_mark));
				
				//小计计算追加
				String[] subagain = rcv.getTopvos()[rtv.getLevel()-1].getSubagain();
				String[] subagain_name = rcv.getTopvos()[rtv.getLevel()-1].getSubagain_name();
		        if(subagain.length>0){
		        	for(int i=0; i<subagain.length; i++){
						String x_y_sub = (rtv.getLevel()-start)+"_"+cols;
						hm_result.put(x_y_sub, subagain[i]);
						hm_result_name.put(x_y_sub, subagain_name[i]);
						
						hm_combine.put(x_y_sub, (getTop_len()-rtv.getLevel()+start)+",1");
						hm_again.put(cols, _cname);
						
						hm_sub.put("_"+cols, cols-cols_mark);
						
						al_top.add(subagain[i]);
						cols++;	
		        	}
		        }
			}
    	}
	}
	
    /**
	 * 左表头 再次计算
	 */
	private void dealTitleLeftAgain(){
		String[] strs = rcv.getTopagain();
		String[] strs_name = rcv.getTopagain_name();
        if(strs.length>0){
        	for(int i=0; i<strs.length; i++){
        		String x_y = rows+"_0";
    			hm_result.put(x_y, strs[i]);
    			hm_result_name.put(x_y, strs_name[i]);
    			hm_combine.put(x_y, "1,"+rcv.getLefttitle().length);
    			hm_sub.put(rows+"_", -1);
    			al_left.add(strs[i]);
    			rows++;
        	}
        }
	}
	
    /**
	 * 上表头 再次计算
	 */
	private void dealTitleTopAgain(ReportConfigCrossVO _rccvo){
		String[] strs = _rccvo.getLeftagain();
		String[] strs_name = _rccvo.getLeftagain_name();
        if(strs.length>0){
        	int start = 0;
        	if(rcv.getCross().length>1){
        		start = 1;
        	}
        	
        	for(int i=0; i<strs.length; i++){
        		String x_y = start+"_"+cols;
    			hm_result.put(x_y, strs[i]);
    			hm_result_name.put(x_y, strs_name[i]);
    			hm_combine.put(x_y, rcv.getToptitle().length+",1");
    			hm_again.put(cols, _rccvo.getCname());
    			hm_sub.put("_"+cols, -1);
    			al_top.add(strs[i]);
    			cols++;
        	}
        }
	}
	
    /**
	 * 左表头 填充合并  
	 */
	private void fillCombine_Left(){
		if(getLeft_len()==1){
			return;
		}
		
		String[] strs = rcv.getTopagain(); 
        if(strs.length>0){ // 再次计算跳过填充
    	    for(int x=getTop_len(); x<getLeft().length; x++){
    	    	for(int y=0; y<getLeft_len()-1; y++){
    	    		String value_xy = (String)hm_result.get(x+"_"+y);
    	    		int mark = 0;
    	    		if(y==0&&value_xy!=null){
    	    			for(int i=0; i<strs.length; i++){
    	    				if(value_xy.equals(strs[i])){
    	    					mark = 1;
    	    					break;
    	    				}
    	    			}
    	    			
    	    			if(mark>0){
    	    				continue;
    	    			}
    	    		}
    	    		
    	    		if(!hm_sub.containsKey(x+"_"+y)){
        	    		if(hm_result.containsKey(x+"_"+y)){
        	    			hm_combine.remove(x+"_"+y);	
        	    		}
        	    		
            			String value_last = (String) hm_result.get((x-1)+"_"+y);
            			String value = (String) hm_result.get(x+"_"+y);
            			if(value==null&&value_last!=null){
            				hm_result.put(x+"_"+y, value_last);
            			}
    	    		}
    	    	}
    		}
        }else{
    	    for(int x=getTop_len(); x<getLeft().length; x++){
    	    	for(int y=0; y<getLeft_len()-1; y++){
    	    		if(!hm_sub.containsKey(x+"_"+y)){
        	    		if(hm_result.containsKey(x+"_"+y)){
        	    			hm_combine.remove(x+"_"+y);	
        	    		}
        	    		
            			String value_last = (String) hm_result.get((x-1)+"_"+y);
            			String value = (String) hm_result.get(x+"_"+y);
            			if(value==null&&value_last!=null){
            				hm_result.put(x+"_"+y, value_last);
            			}
    	    		}
    	    	}
    		}
        }
	}
	
	private void renameResult(){
		Iterator iterator = hm_result_name.entrySet().iterator();
        while(iterator.hasNext()){
            Entry<String, String> entry = (Entry) iterator.next();
            hm_result.put(entry.getKey(), entry.getValue());
        }
	}

	public HashMap getResult() {
		return hm_result;
	}
	public HashMap getCombine() {
		return hm_combine;
	}
	public String[] getLeft() {
		return (String[])al_left.toArray(new String[al_left.size()]);
	}
	public String[] getTop() {
		return (String[])al_top.toArray(new String[al_top.size()]);
	}
	public HashMap getAgain() {
		return hm_again;
	}
	public HashMap<String, Integer> getSub() {
		return hm_sub;
	}
	public int getLeft_len() {
		return left_len;
	}
	public int getTop_len() {
		return top_len;
	}
	public int getRows() {
		return rows;
	}
	public int getCols() {
		return cols;
	}
}
