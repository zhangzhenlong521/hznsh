package com.pushworld.ipushgrc.bs.indexpage;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
/**
 * ��ҳ��Ϣ���ѡ�2012-07-20
 * ��ҵ�����ݲ�����ɺ󣬸�msg_alert���ű����һ�����ݡ�descr������ҳ��ʾ��ʾ��Щ���ݣ�billtype�Ǹ��������ͣ�ģ�飩��busitype������ν�Ķ�������
 * linkurl��������һ��AbstractWorkPanel�����࣬Ҳ��������һ��ģ����롣dataids����ʾ���ݵ�id
 * receivrole���ܽ�ɫid�� ;201;105;��Ҫ��ô���á�receivdept�ǽ��ܽ�ɫ��
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
		//�ƶȵ����ܻ������ƶȹ���Ա ���Կ�������������
		StringBuffer sql = new StringBuffer(" select t1.* from msg_alert t1,rule_rule t2 where ( t2.blcorp in("+tbutil.getInCondition(depts)+") and t1.billtype='�ƶ�����' and t1.dataids=t2.id and t1.id not in(select alertid from msg_alert_looked) ");
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
		sql.append(") and id not in(select alertid from msg_alert_looked) and billtype!='�ƶ�����'");
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
