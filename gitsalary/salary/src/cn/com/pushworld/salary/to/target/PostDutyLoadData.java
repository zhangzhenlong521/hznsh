package cn.com.pushworld.salary.to.target;

import java.util.HashMap;

import cn.com.infostrategy.bs.sysapp.ServerCacheDataFactory;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.jepfunctions.IClassJepFormulaParseIFC;
import cn.com.pushworld.salary.to.SalaryTBUtil;

/**
 * 
 * 岗位评议指标 被考核岗位加载公式调用类。
 * @author haoming
 * create by 2016-1-26
 */
public class PostDutyLoadData implements IClassJepFormulaParseIFC {
	private HashMap dept;
	private SalaryTBUtil stbutil = new SalaryTBUtil();
	private HashVO allPostDutyChild[];
	private TBUtil tbutil = new TBUtil();

	public String getForMulaValue(String[] pars) throws Exception {
		HashVO[] hvs_corpCache = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate(); //处到机构缓存
		dept = tbutil.getHashMapFromHashVOs(hvs_corpCache, "id", "name"); //将id,name抽出来作为一个哈希表,以便查询快速!!
		allPostDutyChild = stbutil.getHashVoArrayByDS(null, "select * from sal_post_duty_check_post where targetid='" + pars[0] + "'");
		HashMap<String, String> posttype = stbutil.getHashMapBySQLByDS(null, "select code,name from PUB_COMBOBOXDICT where type ='薪酬_岗位归类'");
		String targetID = pars[0];
		StringBuffer str = new StringBuffer();
		if (targetID != null) {
			for (int i = 0; i < allPostDutyChild.length; i++) {
				if (targetID.equals(allPostDutyChild[i].getStringValue("targetid"))) {
					String checkeddepts = allPostDutyChild[i].getStringValue("checkeddepts");
					if (!tbutil.isEmpty(checkeddepts)) {
						String depts[] = tbutil.split(checkeddepts, ";");
						StringBuffer deptstr = new StringBuffer();
						for (int j = 0; j < depts.length; j++) {
							String deptname = (String) dept.get(depts[j]);
							deptstr.append(deptname + "&");
						}
						if (deptstr.length() > 0) {
							str.append(deptstr.substring(0, deptstr.length() - 1) + "->");
						}
					}
					String posttypeids[] = tbutil.split(allPostDutyChild[i].getStringValue("postid", ""), ";");
					for (int j = 0; j < posttypeids.length; j++) {
						if (posttype.containsKey(posttypeids[j])) {
							str.append(";" + posttype.get(posttypeids[j]) + ";");
						} else {
							str.append(";【找不到】;");
						}
					}
				}
			}
		}
		return str.toString();
	}
}
