package cn.com.infostrategy.bs.sysapp.userrole;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;

/**
 * 角色绑定菜单工具类
 * @author yangke
 * @date 2012-08-27
 */

public class RoleAuthBSUtil {
	private CommDMO commDMO = null;

	public HashMap getCellData(HashMap hm) throws Exception { //获取表格数据 【杨科/2012-08-27】
		HashVO[] menuhvs = getMenuData();
		HashVO[] rolehvs = getRoleData();
		HashMap authhs = getAuthHashMap();
		String[][] celldata = new String[menuhvs.length+1][rolehvs.length+1];
		
		for(int i=0; i<menuhvs.length; i++){
			for(int j=0; j<rolehvs.length; j++){
				if(i==0){
					celldata[0][0]="【角色】→\r\n（授权）↘\r\n【功能点】↓";
					celldata[0][j+1]=rolehvs[j].getStringValue("name");
				}
				
				if(j==0){
					celldata[i+1][0]=menuhvs[i].getStringValue("$parentpathnamelink");
				}
				
				if(authhs.containsKey(menuhvs[i].getStringValue("id")+"-"+rolehvs[j].getStringValue("id"))){
					celldata[i+1][j+1]="Y-"+menuhvs[i].getStringValue("id")+"-"+rolehvs[j].getStringValue("id");
				}else{
					celldata[i+1][j+1]="N-"+menuhvs[i].getStringValue("id")+"-"+rolehvs[j].getStringValue("id");
				}
			}			
		}
		
		HashMap rhm = new HashMap();
		rhm.put("celldata", celldata);

		return rhm;
	}
	
	public HashVO[] getMenuData() throws Exception{
		ArrayList al = new ArrayList();
		HashVO[] hvs = getCommDMO().getHashVoArrayAsTreeStructByDS(null, "select * from vi_menu", "id", "parentmenuid", null, null);
		
		for(int i=0; i<hvs.length; i++){
			if(hvs[i].getStringValue("$isleaf")!=null&&hvs[i].getStringValue("$isleaf").equals("Y")){
				al.add(hvs[i]);
			}
		}
		
		return (HashVO[]) al.toArray(new HashVO[0]);
	}
	
	public HashVO[] getRoleData() throws Exception{
		return getCommDMO().getHashVoArrayByDS(null, "select id,code,name,roletype from pub_role");
	}
	
	public HashVO[] getAuthData() throws Exception{
		return getCommDMO().getHashVoArrayByDS(null, "select id,roleid,menuid from pub_role_menu");
	}
	
	public HashMap getAuthHashMap() throws Exception{
		HashMap hs = new HashMap();
		HashVO[] authhvs = getAuthData();
		
		for(int i=0; i<authhvs.length; i++){
			hs.put(authhvs[i].getStringValue("menuid")+"-"+authhvs[i].getStringValue("roleid"), "Y");
		}
		
		return hs;
	}
	
	private CommDMO getCommDMO() {
		if (commDMO == null) {
			commDMO = new CommDMO();
		}
		return commDMO;
	}
	
}
