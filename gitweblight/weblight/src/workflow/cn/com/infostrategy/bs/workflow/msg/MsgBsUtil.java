package cn.com.infostrategy.bs.workflow.msg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.ServerCacheDataFactory;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.workflow.engine.MsgVO;

public class MsgBsUtil {
	private TBUtil tbUtil = null;
	private CommDMO commDMO = null;
	private HashMap corpid_parentid = null;

	/**
	 * 获取未读消息数量
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public HashMap getMsgCountMap(HashMap _parMap) throws Exception {
		return getMsgCountMap(_parMap, -1);
	}

	public HashMap getMsgCountMap(HashMap _parMap, int c) throws Exception {
		HashMap retrunMap = new HashMap();
		String str_loginUserId = "";
		if (_parMap.get("userid") != null) {
			str_loginUserId = (String) _parMap.get("userid");
		} else {
			str_loginUserId = new WLTInitContext().getCurrSession().getLoginUserId();
		}
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
		String[] allroleids = null;
		if (_parMap.get("roleids") != null) {
			allroleids = (String[]) _parMap.get("roleids");
		} else {
			allroleids = getCommDMO().getStringArrayFirstColByDS(null, " select roleid from pub_user_role where userid = '" + str_loginUserId + "' ");
		}
		int unread = 0;
		int readed = 0;
		int sended = 0;

		String unrsql = getUnreadSQL(str_loginUserId, allcorp, allroleids);
		String unr = getCommDMO().getStringValueByDS(null, " select count(id) from pub_msgcenter where " + unrsql);
		unread = Integer.parseInt(unr);
		if (c == 0) {
			retrunMap.put("未读消息", unread);
			retrunMap.put("未读SQL", unrsql);
			return retrunMap;
		}
		String redsql = getReadedSQL(str_loginUserId);
		String red = getCommDMO().getStringValueByDS(null, "select count(id) from pub_msgcenter where" + redsql);
		readed = Integer.parseInt(red);
		if (c == 1) {
			retrunMap.put("已读消息", readed);
			retrunMap.put("已读SQL", redsql);
			return retrunMap;
		}
		String sedsql = getSendedSQL(str_loginUserId);
		String sed = getCommDMO().getStringValueByDS(null, "select count(id) from pub_msgcenter where" + sedsql);
		sended = Integer.parseInt(sed);
		if (c == 2) {
			retrunMap.put("已发消息", sended);
			retrunMap.put("已发SQL", sedsql);
			return retrunMap;
		}
		retrunMap.put("未读消息", unread);
		retrunMap.put("未读SQL", unrsql);
		retrunMap.put("已读消息", readed);
		retrunMap.put("已读SQL", redsql);
		retrunMap.put("已发消息", sended);
		retrunMap.put("已发SQL", sedsql);
		return retrunMap;
	}

	/**
	 * 通过用ID获取该用户所属的所有机构，比如直接所属部门、所属行，即我
	 * 属于某部门，同时我也属于该部门的分行
	 * @param userid
	 * @return
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
		return corpid_parentid; //
	}

	private String getReadedSQL(String userid) {
//		StringBuffer sb = new StringBuffer(" id in (select msgid from pub_msgcenter_readed where userid='" + userid + "' and isdelete='N' )  ");
		StringBuffer sb = new StringBuffer(" exists (select msgid from pub_msgcenter_readed where userid='" + userid + "' and isdelete='N' and msgid=pub_msgcenter.id )  ");//改成exists语句性能会有所提高，加上加载公式修改后那是相当快/sunfujun/20121122
		return sb.toString();
	}

	private String getSendedSQL(String userid) {
		StringBuffer sb = new StringBuffer(" wf_state='审核通过' and isdelete='N' and sender = '" + userid + "' ");
		return sb.toString();
	}

	private String getUnreadSQL(String userid, String[] corpids, String[] roleids) {
		StringBuffer sb = new StringBuffer();
		sb.append(" wf_state='审核通过' and functiontype='sysmsg' and (receiver like '%;" + userid + ";%' ");
		if (corpids != null && corpids.length > 0) {
			sb.append(" or ( ");
			for (int i = 0; i < corpids.length; i++) {
				if (i == 0) {
					sb.append("  recvcorp like '%;" + corpids[i] + ";%' ");
				} else {
					sb.append(" or recvcorp like '%;" + corpids[i] + ";%' ");
				}
			}
			sb.append(" ) ");
		}
		if (roleids != null && roleids.length > 0) {
			sb.append(" or ( ");
			for (int i = 0; i < roleids.length; i++) {
				if (i == 0) {
					sb.append("  recvrole like '%;" + roleids[i] + ";%' ");
				} else {
					sb.append(" or recvrole like '%;" + roleids[i] + ";%' ");
				}
			}
			sb.append(" ) ");
		}

//		sb.append(" ) and id not in (select msgid from pub_msgcenter_readed where userid='" + userid + "' ) ");
		sb.append(" ) and not exists (select id from pub_msgcenter_readed where userid='" + userid + "' and msgid=pub_msgcenter.id)  ");//改成exists语句性能会有所提高/sunfujun/20121122

		return sb.toString();
	}

	private CommDMO getCommDMO() {
		if (commDMO == null) {
			commDMO = new CommDMO();
		}
		return commDMO;
	}

	private TBUtil getTBUtil() {
		if (tbUtil == null) {
			tbUtil = new TBUtil();
		}
		return tbUtil;
	}

	/**
	 * 发送消息的逻辑
	 * @return
	 * @throws Exception 
	 */
	public HashMap sendMsg(HashMap param) throws Exception {
		if (param.get("vo") != null) {
			BillVO vo = (BillVO) param.get("vo");
			String msgType = vo.getStringValue("msgtype");
			if (msgType != null && !"".equals(msgType)) {
				String sendclass = getCommDMO().getStringValueByDS(null, "select sendimpl from pub_msgcenter_rule where name='" + msgType + "' ");
				if (sendclass != null && !"".equals(sendclass)) {
					Object obj = Class.forName(sendclass).newInstance();
					if (obj instanceof MsgSendExtFunctionIFC) {
						((MsgSendExtFunctionIFC) obj).send(vo);
						return null;
					}
				}
			}
			//如果沒有实现逻辑
			getCommDMO().executeBatchByDS(null, new String[] { vo.getInsertSQL() });
		}
		return null;
	}

	public HashMap commSendMsg(HashMap hm) throws Exception { //模块消息调用UI/BS对应方法 【杨科/2012-08-15】
		HashMap rhm = new HashMap();

		if (hm.get("msgs") != null) {
			ArrayList msgs = (ArrayList) hm.get("msgs");
			commSendMsgBS(msgs);
		}

		return null;
	}

	public boolean commSendMsgBS(ArrayList msgs) throws Exception { //模块消息调用BS方法 【杨科/2012-08-15】
		if (msgs.size() > 0) {
			getCommDMO().executeBatchByDS(null, getSendMsgSqls(msgs));
			return true;
		}
		return false;
	}

	public ArrayList getSendMsgSqls(ArrayList msgs) throws Exception {
		ArrayList al = new ArrayList();
		for (int i = 0; i < msgs.size(); i++) {
			MsgVO mv = (MsgVO) msgs.get(i);
			al.add(new InsertSQLBuilder("pub_msgcenter", new String[][] { { "id", getCommDMO().getSequenceNextValByDS(null, "S_PUB_MSGCENTER") }, { "msgtype", mv.getMsgtype() }, { "receiver", mv.getReceiver() }, { "recvcorp", mv.getRecvcorp() }, { "recvrole", mv.getRecvrole() },
							{ "msgtitle", mv.getMsgtitle() }, { "msgcontent", mv.getMsgcontent() }, { "msgfile", mv.getMsgfile() }, { "sender", mv.getSender() }, { "sendercorp", mv.getSendercorp() }, { "senddate", TBUtil.getTBUtil().getCurrTime() }, { "isdelete", "N" }, { "state", mv.getState() },
							{ "functiontype", "sysmsg" }, {"wf_state", "审核通过"}, {"createtime", mv.getCreatetime()} }).getSQL());
		}
		return al;
	}

}
