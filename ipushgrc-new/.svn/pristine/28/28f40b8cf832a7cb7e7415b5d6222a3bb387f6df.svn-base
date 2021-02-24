package com.pushworld.ipushgrc.bs.indexpage;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
/**
 * 首页消息提醒。2012-07-20
 * 在业务数据操作完成后，给msg_alert这张表插入一条数据。descr就是首页显示提示哪些内容，billtype是该数据类型（模块）。busitype（无所谓的东西。）
 * linkurl可以配置一个AbstractWorkPanel的子类，也可以配置一个模板编码。dataids是提示数据的id
 * receivrole接受角色id串 ;201;105;需要这么配置。receivdept是接受角色。
 * @author hm
 *
 */
public class AlertDateBuilder implements DeskTopNewsDataBuilderIFC {
	private TBUtil tbutil = new TBUtil();
	public HashVO[] getNewData(String userCode) throws Exception {
		String userid = new WLTInitContext().getCurrSession().getLoginUserId();
		 CommDMO commdmo = new CommDMO();
		String [] roles =commdmo.getStringArrayFirstColByDS(null, "select roleid from pub_user_role where userid = "+userid);
		String [] depts = commdmo.getStringArrayFirstColByDS(null, "select deptid from v_pub_user_post_1 where userid = "+userid);
		StringBuffer con = new StringBuffer();
		for (int i = 0; i < roles.length; i++) {
			if(i == 0){
				con.append(" receivrole like '%"+ roles[i] +"%' ");	
			}else{
				con.append(" or receivrole like '%"+ roles[i] +"%' ");	
			}
		}
		//制度的主管机构的制度管理员 可以看到的评价提醒
		StringBuffer sql = new StringBuffer(" select t1.* from msg_alert t1,rule_rule t2 where ( t2.blcorp in("+tbutil.getInCondition(depts)+") and t1.billtype='制度评价' and t1.dataids=t2.id and t1.id not in(select alertid from msg_alert_looked) ");
		if(con.length()!=0){
			sql.append( " and (" + con +" ) " ); 
		}
		sql.append(" ) "); 
		sql.append(" union all select * from msg_alert where 1=1 and " );
		sql.append(" (receivuser = " +userid +" ");
		String likeRoleCondition = getOrCondition("receivrole",roles);
		String likeDeptCondition = getOrCondition("receivdept",depts);
		
		
		if(con.length() != 0){
			sql.append(" or (("+likeRoleCondition+") and receivdept is null )");
			sql.append(" or (("+likeRoleCondition+") and ("+likeDeptCondition+"))");
			sql.append(" or (("+likeDeptCondition+") and receivrole is null)");
		}
		sql.append(") and id not in(select alertid from msg_alert_looked) and billtype!='制度评价'");
		HashVO[] vos = new CommDMO().getHashVoArrayByDS(null, sql.toString());
		if(vos !=null){
			for(int i = 0 ; i < vos.length;i++){
				vos[i].setToStringFieldName("descr");
			}
		}
		return vos;
	}
	private String getOrCondition(String _column,String [] str){
		StringBuffer s = new StringBuffer();
		if(str == null || str.length == 0){
			return "1=2";
		}
		for (int i = 0; i < str.length; i++) {
			if(s.length() == 0){
				s.append(" "+ _column + " like '%;"+str[i]+";%'");
			}else{
				s.append(" or "+ _column + " like  '%;"+str[i]+";%'");
			}
		}
		return s.toString();
	}
}
