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
 * ��Ϣ���Ĵ��� �����/2012-11-28��
 */

public class MessageBSUtil {
	private CommDMO commDMO = null;
	private HashMap corpid_parentid = null;

	/**
	 * ͨ����ID��ȡ���û����������л���������ֱ���������š�������
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

	//��ȡ���ķ�����map
	public HashMap getPassReadClassCountMap(HashMap _parMap) throws Exception {
		HashMap retrunMap = new HashMap();

		String wheresql = (String) _parMap.get("����SQL");
		if (wheresql == null || wheresql.equals("")) {
			HashMap hs = getPassReadSQL(_parMap);
			_parMap.put("����SQL", hs.get("����SQL"));
			_parMap.put("����SQL", hs.get("����SQL"));
			_parMap.put("����SQL", hs.get("����SQL"));
		}

		String[] keys = { "��ͨ������Ϣ", "��ͨ������Ϣ", "��ͨ������Ϣ", "������������Ϣ", "������������Ϣ", "������������Ϣ" };
		String[] msgtypes = { "��ͨ������Ϣ", "��ͨ������Ϣ", "��ͨ������Ϣ", "������������Ϣ", "������������Ϣ", "������������Ϣ" };
		String[] sqls = { "����SQL", "����SQL", "����SQL", "����SQL", "����SQL", "����SQL" };
		for (int i = 0; i < keys.length; i++) {
			retrunMap.put(keys[i], getClassCountBySQL(" select templetcode, templetname, count(*) cou from pub_message where msgtype='" + msgtypes[i] + "' and " + _parMap.get(sqls[i]) + " group by templetcode, templetname "));
		}
		retrunMap.put("����SQL", _parMap.get("����SQL"));
		retrunMap.put("����SQL", _parMap.get("����SQL"));
		retrunMap.put("����SQL", _parMap.get("����SQL"));
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

	//��ȡ����-����-����SQL
	public HashMap getPassReadSQL(HashMap _parMap) throws Exception {
		StringBuffer sql_unread = new StringBuffer();
		StringBuffer sql_readed = new StringBuffer();
		StringBuffer sql_send = new StringBuffer();

		//��ȡ�û�id
		String str_loginUserId = "";
		if (_parMap.get("userid") != null) {
			str_loginUserId = (String) _parMap.get("userid");
		} else {
			str_loginUserId = new WLTInitContext().getCurrSession().getLoginUserId();
		}

		//��ȡ�û�����
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

		//��ȡ�û���ɫ��
		String[] allroleids = null;
		if (_parMap.get("roleids") != null) {
			allroleids = (String[]) _parMap.get("roleids");
		} else {
			allroleids = getCommDMO().getStringArrayFirstColByDS(null, " select roleid from pub_user_role where userid = '" + str_loginUserId + "' ");
		}

		//����sql
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

		//����sql
		sql_readed.append(" isdelete='N' ");
		sql_readed.append(" and exists (select id from pub_message_readed a where a.msgid = pub_message.id and userid = '" + str_loginUserId + "') and isdelete='N' ");

		//����sql
		sql_send.append(" isdelete='N' ");
		sql_send.append(" and sender = '" + str_loginUserId + "' ");

		HashMap retrunMap = new HashMap();
		retrunMap.put("����SQL", sql_unread.toString());
		retrunMap.put("����SQL", sql_readed.toString());
		retrunMap.put("����SQL", sql_send.toString());

		return retrunMap;
	}

}
