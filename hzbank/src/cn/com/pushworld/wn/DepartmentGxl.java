package cn.com.pushworld.wn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;


public class DepartmentGxl{
	/**
	 * 部门定量指标存款贡献率、收入贡献率、综合贡献度
	 */
	
	
	private CommDMO dmo = new CommDMO();
	private HashVO[] vo=null;
	private HashMap<String, String> map = null;


	
	public HashMap<String, String> getCkgxl(String year,String monthColum,String LastMonth){
		HashMap<String, String> ckgxl = new HashMap<String, String>();
		try {
//			map = dmo.getHashMapBySQLByDS(null, "select name,code from pub_corp_dept where corpdistinct='涉农网点'");
			vo = dmo.getHashVoArrayByDS(null,"select code,name from pub_corp_dept where corpdistinct='涉农网点'");
			HashMap<String, String> ckSds = dmo.getHashMapBySQLByDS(
					null,
					"select a.name,b.F from pub_corp_dept a,excel_tab_9 b where substr(b.A,12,length(b.A))=a.name and  b.year||'-'||b.month='"+LastMonth+"'");
			HashMap<String, String> ckSdrw = dmo.getHashMapBySQLByDS(
					null,
					"select a,"+monthColum+" from excel_tab_33 where year='"+year+"'");
			for(int i = 0;i < vo.length;i++){
				int a,b;//a=存款时点数较年初  b=存款时点数较年初任务
				a=Integer.parseInt(ckSds.get(vo[i].getStringValue("name")).toString());
				b=Integer.parseInt(ckSdrw.get(vo[i].getStringValue("name")).toString());
				ckgxl.put(vo[i].getStringValue("name"), String.valueOf(a / b));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ckgxl;
	}
	
	
	public HashMap<String, String> getSrgxl(String year,String monthColum,String LastMonth){
		HashMap<String, String> srgxl = new HashMap<String, String>();
		try {
//			vo = dmo.getHashVoArrayByDS(
//					null,"select name,code from pub_corp_dept where corpdistinct='涉农网点'");
			HashMap<String, String> srSds = dmo.getHashMapBySQLByDS(
					null,
					"select a.name,b.c from pub_corp_dept a,excel_tab_94 b where substr(b.A,12,length(b.A))=a.name and  b.year||'-'||b.month='"+LastMonth+"'");
			HashMap<String, String> srSdrw = dmo.getHashMapBySQLByDS(
					null,
					"select a,"+monthColum+" from excel_tab_93 where year='"+year+"'");
			for(int i = 0;i < vo.length;i++){
				int a,b;//a=利息时点数较年初  b=存款时点数较年初任务
				a=Integer.parseInt(srSds.get(vo[i].getStringValue("name")).toString());
				b=Integer.parseInt(srSdrw.get(vo[i].getStringValue("name")).toString());
				srgxl.put(vo[i].getStringValue("name"), String.valueOf(a / b));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return srgxl;
	}
	
	public HashMap<String, String> getZhgxd(HashMap<String, String> ckgxl, HashMap<String, String> srgxl,String lastMonth){
		HashMap<String, String> zhgxd = new HashMap<String, String>();
		InsertSQLBuilder insert = new InsertSQLBuilder("wn_zhgxd");
		List list = new ArrayList<String>();
		try {
//			vo = dmo.getHashVoArrayByDS(
//					null,"select name,code from pub_corp_dept where corpdistinct='涉农网点'");
			for(int i = 0; i<vo.length;i++){
				int a,b;//a存款时点比例 b利息时点比例
				a=Integer.parseInt(ckgxl.get(vo[i].getStringValue("name")).toString());
				b=Integer.parseInt(srgxl.get(vo[i].getStringValue("name")).toString());
				zhgxd.put(vo[i].getStringValue("name"), String.valueOf(a + b));
			}
			for (String str : zhgxd.keySet()) {
				insert.putFieldValue("deptname", str);
				insert.putFieldValue("zhgxl", zhgxd.get(str));
				insert.putFieldValue("createtime",lastMonth );
				list.add(insert.getSQL());
			}
			dmo.executeBatchByDS(null, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return zhgxd;
		
	}
	
	
	
	
	
	
	
	



	
	
}
