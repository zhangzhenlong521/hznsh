package cn.com.infostrategy.bs.workflow.msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.ServerCacheDataFactory;
import cn.com.infostrategy.to.common.HashVO;

/**
 * 消息中心传阅 【杨科/2012-11-28】
 */

public class MessageBSUtil {
	private CommDMO commDMO = null;
	private HashMap corpid_parentid = null;

	/**
	 * 通过用ID获取该用户所属的所有机构，比如直接所属部门、所属行
	 */
	private void getAllBLCorpByUserID(String corpid, List corplist) {
		corplist.add(corpid);
		if (getAllCorpsCacheMap().get(corpid) != null && !"".equals(getAllCorpsCacheMap().get(corpid)) && getAllCorpsCacheMap().containsKey(getAllCorpsCacheMap().get(corpid))) {
			getAllBLCorpByUserID((String) getAllCorpsCacheMap().get(corpid), corplist);
		}
	}

	public HashMap getAllCorpsCacheMap() {
		if (corpid_parentid == null) {
			HashVO[] hvs_allCorps = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate();
			corpid_parentid = new HashMap();
			for (int i = 0; i < hvs_allCorps.length; i++) {
				corpid_parentid.put(hvs_allCorps[i].getStringValue("id"), hvs_allCorps[i].getStringValue("parentid"));
			}
		}
		return corpid_parentid;
	}

	private CommDMO getCommDMO() {
		if (commDMO == null) {
			commDMO = new CommDMO();
		}
		return commDMO;
	}

	//获取传阅分类树map
	public HashMap getPassReadClassCountMap(HashMap _parMap) throws Exception {
		HashMap retrunMap = new HashMap();

		String wheresql = (String) _parMap.get("待阅SQL");
		if (wheresql == null || wheresql.equals("")) {
			HashMap hs = getPassReadSQL(_parMap);
			_parMap.put("待阅SQL", hs.get("待阅SQL"));
			_parMap.put("已阅SQL", hs.get("已阅SQL"));
			_parMap.put("传阅SQL", hs.get("传阅SQL"));
		}

		String[] keys = { "普通待阅消息", "普通已阅消息", "普通传阅消息", "工作流待阅消息", "工作流已阅消息", "工作流传阅消息" };
		String[] msgtypes = { "普通传阅消息", "普通传阅消息", "普通传阅消息", "工作流传阅消息", "工作流传阅消息", "工作流传阅消息" };
		String[] sqls = { "待阅SQL", "已阅SQL", "传阅SQL", "待阅SQL", "已阅SQL", "传阅SQL" };
		for (int i = 0; i < keys.length; i++) {
			retrunMap.put(keys[i], getClassCountBySQL(" select templetcode, templetname, count(*) cou from pub_message where msgtype='" + msgtypes[i] + "' and " + _parMap.get(sqls[i]) + " group by templetcode, templetname "));
		}
		retrunMap.put("待阅SQL", _parMap.get("待阅SQL"));
		retrunMap.put("已阅SQL", _parMap.get("已阅SQL"));
		retrunMap.put("传阅SQL", _parMap.get("传阅SQL"));
		return retrunMap;
	}

	private HashMap getClassCountBySQL(String _sql) throws Exception {
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, _sql);
		if (hvs != null && hvs.length > 0) {
			LinkedHashMap returnMap = new LinkedHashMap();
			for (int i = 0; i < hvs.length; i++) {
				String templetcode = hvs[i].getStringValue("templetcode");
				String templetname = hvs[i].getStringValue("templetname");
				String cou = hvs[i].getStringValue("cou");
				returnMap.put(templetcode, new String[] { templetname, cou });
			}
			return returnMap;
		}
		return null;
	}

	//获取待阅-已阅-传阅SQL
	public HashMap getPassReadSQL(HashMap _parMap) throws Exception {
		StringBuffer sql_unread = new StringBuffer();
		StringBuffer sql_readed = new StringBuffer();
		StringBuffer sql_send = new StringBuffer();

		//获取用户id
		String str_loginUserId = "";
		if (_parMap.get("userid") != null) {
			str_loginUserId = (String) _parMap.get("userid");
		} else {
			str_loginUserId = new WLTInitContext().getCurrSession().getLoginUserId();
		}

		//获取用户部门
		String corpid = null;
		String[] allcorp = null;
		if (_parMap.get("corpid") != null) {
			corpid = (String) _parMap.get("corpid");
			List a = new ArrayList();
			getAllBLCorpByUserID(corpid, a);
			allcorp = (String[]) a.toArray(new String[0]);
		} else {
			HashVO[] vos = getCommDMO().getHashVoArrayByDS(null, " select userdept,isdefault from pub_user_post where userid = '" + str_loginUserId + "' ");
			if (vos != null && vos.length > 0) {
				for (int i = 0; i < vos.length; i++) {
					if ("Y".equals(vos[i].getStringValue("isdefault"))) {
						corpid = vos[i].getStringValue("userdept");
						break;
					}
				}
				if (corpid == null) {
					corpid = vos[0].getStringValue("userdept");
				}
				List a = new ArrayList();
				getAllBLCorpByUserID(corpid, a);
				allcorp = (String[]) a.toArray(new String[0]);
			}
		}

		//获取用户角色组
		String[] allroleids = null;
		if (_parMap.get("roleids") != null) {
			allroleids = (String[]) _parMap.get("roleids");
		} else {
			allroleids = getCommDMO().getStringArrayFirstColByDS(null, " select roleid from pub_user_role where userid = '" + str_loginUserId + "' ");
		}

		//待阅sql
		sql_unread.append(" isdelete='N' ");
		sql_unread.append(" and (receiver like '%;" + str_loginUserId + ";%' ");
		if (allcorp != null && allcorp.length > 0) {
			sql_unread.append(" or ( ");
			for (int i = 0; i < allcorp.length; i++) {
				if (i == 0) {
					sql_unread.append(" recvcorp like '%;" + allcorp[i] + ";%' ");
				} else {
					sql_unread.append(" or recvcorp like '%;" + allcorp[i] + ";%' ");
				}
			}
			sql_unread.append(" ) ");
		}
		if (allroleids != null && allroleids.length > 0) {
			sql_unread.append(" or ( ");
			for (int i = 0; i < allroleids.length; i++) {
				if (i == 0) {
					sql_unread.append(" recvrole like '%;" + allroleids[i] + ";%' ");
				} else {
					sql_unread.append(" or recvrole like '%;" + allroleids[i] + ";%' ");
				}
			}
			sql_unread.append(" ) ");
		}
		sql_unread.append(" ) ");
		sql_unread.append(" and not exists (select id from pub_message_readed a where a.msgid = pub_message.id and userid = '" + str_loginUserId + "') ");

		//已阅sql
		sql_readed.append(" isdelete='N' ");
		sql_readed.append(" and exists (select id from pub_message_readed a where a.msgid = pub_message.id and userid = '" + str_loginUserId + "') and isdelete='N' ");

		//传阅sql
		sql_send.append(" isdelete='N' ");
		sql_send.append(" and sender = '" + str_loginUserId + "' ");

		HashMap retrunMap = new HashMap();
		retrunMap.put("待阅SQL", sql_unread.toString());
		retrunMap.put("已阅SQL", sql_readed.toString());
		retrunMap.put("传阅SQL", sql_send.toString());

		return retrunMap;
	}

}
