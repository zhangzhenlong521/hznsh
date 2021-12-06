package com.pushworld.ipushgrc.ui.HR.p060;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
/**
 * 
 * @author longlonggo521
 *  岗位变化统计
 */
public class PostChangeCountWorkPanel extends MultiLevelReportDataBuilderAdapter {
	TBUtil tbutil = new TBUtil();
	CommDMO dmo = new CommDMO();

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		String deptid = (String) condition.get("deptid");//机构
		String username = (String) condition.get("username"); //姓名
		String date = (String) condition.get("date");//时间
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("select id id, deptid 机构,username 姓名,date 调动时间  from hr_recordpost where 1=1");

		//主管部门
		if (deptid != null && !deptid.equals("")) {
			String [] str=getSplit(deptid);
			sql_sb.append(" and deptid in('"+deptid+"')");
		}

		//制度分类查询条件
		if (username != null && !username.equals("")) {
			sql_sb.append(" and username in ('" + username+ "')");
		}

		//业务类型查询条件,树型多选!!
		if (date != null && !date.equals("")) { //业务类型
			if(date.contains("年") || date.contains("月")){
				sql_sb.append(" and (date>='"+date.replace(";", "").replace("年", "-").replace("月","-01")+"' and date<='"+dateJytian(date)+" 24:00:00')");
				
			}else{
				sql_sb.append(" and (date>='"+date.replace(";", "")+"' and date<='"+date.replace(";","")+" 24:00:00')");
			}
					
		}

		sql_sb.append(" order by date"); //如果不按时间排序，统计出来的季度是乱的
		HashVO[] vos = dmo.getHashVoArrayByDS(null, sql_sb.toString());

		ReportDMO reportDMO = new ReportDMO(); 
//		//处理主管部门
		reportDMO.addOneFieldFromOtherTree(vos, "机构", "机构", "select id,name,parentid from pub_corp_dept", 2, false, 2); //加上第1层
//
//		//处理业务类型第1层与第2层!!
//		reportDMO.addOneFieldFromOtherTree(vos, "学历", "学历", "select degree,degree from v_sal_personinfo", 1, false, 1); //加上第1层
//
//		//处理内控分类之第一层与第二层
//		reportDMO.addOneFieldFromOtherTree(vos, "岗位归类", "岗位归类", "select stationkind from v_sal_personinfo", 1, false, 1); //加上第1层
//		//产品分类之第一层与第二层
//		reportDMO.addOneFieldFromOtherTree(vos, "职称", "职称", "select posttitle from v_sal_personinfo", 1, false, 1); //加上第1层
//		new ReportUtil().leftOuterJoin_YSMDFromDateTime(vos, "发布日期", "发布日期", "季"); //将发而日期折算成季度!
		return vos;
	}

	public String[] getGroupFieldNames() {
		return new String[] {"机构","姓名","调动时间"};
	}

	public String[] getSumFiledNames() {
		return new String[] { "数量" };
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		return getBeforehandGroupType();
	}

	@Override
	/**
	 * 钻取明细时的模板编码!!
	 */
	public String getDrillTempletCode() throws Exception {
		return "HR_RECORDPOST_CODE1"; //
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		return getBeforehandGroupType();
	}

	/**
	 * 雷达图
	 */
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Splider() {
		ArrayList al = new ArrayList(); //
		al.add(getBeforehandGroupType("机构_姓名", "数量", BeforeHandGroupTypeVO.COUNT));
		al.add(getBeforehandGroupType("姓名_调动时间", "数量", BeforeHandGroupTypeVO.COUNT));
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}

	/**
	 * 表格与图表!!!
	 * @return
	 */
	public BeforeHandGroupTypeVO[] getBeforehandGroupType() {
		ArrayList al = new ArrayList(); //
		al.add(getBeforehandGroupType("机构_姓名", "数量", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("姓名_调动时间", "数量", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("机构_调动时间", "数量", BeforeHandGroupTypeVO.COUNT)); //
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}

	public BeforeHandGroupTypeVO getBeforehandGroupType(String _name, String typeName, String _type) {
		String[] names = _name.split("_");
		BeforeHandGroupTypeVO typeVo = new BeforeHandGroupTypeVO();
		typeVo.setName(_name);
		typeVo.setRowHeaderGroupFields(new String[] { names[0] });
		typeVo.setColHeaderGroupFields(new String[] { names[1] });
		typeVo.setComputeGroupFields(new String[][] { { typeName, _type } });
		return typeVo;
	}

	public String getMultiOrCondition(String key, String _condition) {
		StringBuffer sb_sql = new StringBuffer();
		String[] tempid = tbutil.split(_condition, ";"); // str_realvalue.split(";");
		if (tempid != null && tempid.length > 0) {
			sb_sql.append(" and (");
			for (int j = 0; j < tempid.length; j++) {
				sb_sql.append(key + " like '%;" + tempid[j] + ";%'"); // 
				if (j != tempid.length - 1) { //
					sb_sql.append(" or ");
				}
			}
			sb_sql.append(") "); //
		}
		return sb_sql.toString();
	}

	public HashMap getGroupFieldOrderConfig() {
		HashMap map = new HashMap();
		try {
			map.put("机构", dmo.getStringArrayFirstColByDS(null, "select name from pub_corp_dept"));
			map.put("姓名", dmo.getStringArrayFirstColByDS(null, "select name from pub_user"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	public String [] getSplit(String str){
		String [] split=str.split(";");
		return split;
		
	}
	/**
	 * 
	 * @return
	 * 日期当月最后一天
	 */
	public String dateJytian(String str) {
		String [] str1=str.replace(";","").replace("年","-").replace("月","").split("-");
		String str2=null;
		if(str1.length>2){
			Calendar cl=setCalendar(Integer.parseInt(str1[0]),Integer.parseInt(str1[1]),Integer.parseInt(str1[2]));
			Calendar c2=getBeforeDay(cl);
			str2 = new SimpleDateFormat("yyyy-MM-dd").format(c2.getTime());	
		}else{
			Calendar c=Calendar.getInstance();
			  int MaxDay=c.getActualMaximum(Calendar.DAY_OF_MONTH);
			  //按你的要求设置时间
			  c.set( Integer.parseInt(str1[0]), Integer.parseInt(str1[1]),01, 23, 59, 59);
		      int day = c.get(Calendar.DATE);  
		      c.set(Calendar.DATE, day-1);
			  //按格式输出
			  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			  str2 = sdf.format(c.getTime()); //上月最后一天
		}

		return str2;

	}
	public static void main(String[] args) {
		  Calendar c=Calendar.getInstance();
		  int MaxDay=c.getActualMaximum(Calendar.DAY_OF_MONTH);
		  //按你的要求设置时间
		  c.set( 2017, 11,01, 23, 59, 59);
	      int day = c.get(Calendar.DATE);  
	      c.set(Calendar.DATE, day-1);
		  //按格式输出
		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
		  String gtime = sdf.format(c.getTime()); //上月最后一天
		  System.out.println(">>>>>>>>>>>>>>>>>"+gtime);
	}
	 /** 
     * 设置时间 
     * @param year 
     * @param month 
     * @param date 
     * @return 
     */  
    public  Calendar setCalendar(int year,int month,int date){  
        Calendar cl = Calendar.getInstance();  
        cl.set(year, month-1, date);  
        return cl;  
    }  
      
    /** 
     * 获取当前时间的前一天时间 
     * @param cl 
     * @return 
     */  
    private  Calendar getBeforeDay(Calendar cl){  
        //使用roll方法进行向前回滚  
        //cl.roll(Calendar.DATE, -1);  
        //使用set方法直接进行设置  
        int day = cl.get(Calendar.DATE);  
        cl.set(Calendar.DATE, day-1);
        return cl;  
    }  
      
    /** 
     * 获取当前时间的后一天时间 
     * @param cl 
     * @return 
     */  
    private  Calendar getAfterDay(Calendar cl){  
        //使用roll方法进行回滚到后一天的时间  
        //cl.roll(Calendar.DATE, 1);  
        //使用set方法直接设置时间值  
        int day = cl.get(Calendar.DATE);  
        cl.set(Calendar.DATE, day+1);  
        return cl;  
    }  
      
    /** 
     * 打印时间 
     * @param cl 
     */  
    public  void printCalendar(Calendar cl){  
        int year = cl.get(Calendar.YEAR);  
        int month = cl.get(Calendar.MONTH)+1;  
        int day = cl.get(Calendar.DATE);  
        System.out.println(year+"-"+month+"-"+day);  
    }  

}


