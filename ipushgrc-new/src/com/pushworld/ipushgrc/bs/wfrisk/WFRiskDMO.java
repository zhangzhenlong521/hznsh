package com.pushworld.ipushgrc.bs.wfrisk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Random;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.DataPolicyDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.workflow.design.RiskVO;

import com.pushworld.ipushgrc.to.WordTBUtil;

/**
 * 流程与风险模块的DMO
 * 
 * @author xch
 * 
 */
public class WFRiskDMO extends AbstractDMO {
	/**
	 * 根据流程编码、流程文件、流程文件id新增流程，并将流程id返回
	 * 
	 * @param _code
	 *            流程编码
	 * @param _name
	 *            流程名称
	 * @param _cmpfileid
	 *            流程文件id
	 * @throws Exception
	 */
	TBUtil tbUtil = new TBUtil(); //
	CommDMO dmo = new CommDMO();

	/**
	 * 新增一个流程，一图两表中先新增流程再修改名称，默认流程名：cmpfilename + "_流程"+该流程文件的流程数
	 * @param _code
	 * @param _name
	 * @param _userdef01  流程所属部门
	 * @param _cmpfileid
	 * @return
	 * @throws Exception
	 */
	public String insertOneWf(String _code, String _name, String _userdef01, String _cmpfileid) throws Exception {
		InsertSQLBuilder isql_insert = new InsertSQLBuilder("pub_wf_process"); // 
		String str_id = dmo.getSequenceNextValByDS(null, "S_PUB_WF_PROCESS"); //
		isql_insert.putFieldValue("id", str_id);
		isql_insert.putFieldValue("code", _code);
		isql_insert.putFieldValue("name", _name);
		isql_insert.putFieldValue("userdef01", _userdef01);//流程所属机构【李春娟/2012-03-14】
		isql_insert.putFieldValue("cmpfileid", _cmpfileid);
		try {
			String seq = _name.substring(_name.lastIndexOf("_流程") + 3, _name.length());
			if (Integer.parseInt(seq) < 10) {//为了排序，将小于10的排序值前加0,如果这里转换成数字报错的话，捕获异常，但sql照常执行！只是存不上排序字段【李春娟/2012-03-23】
				seq = "0" + seq;
			}
			isql_insert.putFieldValue("userdef04", seq);//流程的排序字段
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (_cmpfileid != null && !"".equals(_cmpfileid)) {//如果该流程关联流程文件，则设置流程类型为“体系流程”，与导入visio的逻辑一致【李春娟/2012-05-24】
			isql_insert.putFieldValue("wftype", "体系流程");
		}
		dmo.executeUpdateByDS(null, isql_insert.getSQL());
		return str_id;
	}

	/**
	 * 根据流程文件id删除一个流程文件并级联删除流程、流程相关和环节相关的信息，以及流程文件历史版本和历史版本内容
	 * 
	 * @param _cmpfileid
	 *            流程文件id
	 * @throws Exception
	 */
	public void deleteCmpFileById(String _cmpfileid) throws Exception {
		ArrayList sql_deletefile = new ArrayList();
		String[] processids = dmo.getStringArrayFirstColByDS(null, "select id from pub_wf_process where cmpfileid =" + _cmpfileid);
		if (processids != null && processids.length > 0) {
			String str_processids = tbUtil.getInCondition(processids);
			String str_userDefinedCls = tbUtil.getSysOptionStringValue("自定义一图两表按钮面板类", null);//如果系统中有自定义一图两表按钮面板类，说明流程相关和环节相关里有可能增加了内容，这时需要获得删除时的sql语句【李春娟/2012-05-14】
			if (str_userDefinedCls != null && !str_userDefinedCls.trim().equals("")) {// 如果配置了自定义一图两表按钮面板类
				HashMap hashmap = new HashMap();
				hashmap.put("wfprocess_ids", str_processids);// 将所有流程id传入
				hashmap = tbUtil.reflectCallCommMethod(str_userDefinedCls + ".getDeleteCmpFileSqls()", hashmap);
				if (hashmap != null) {
					if (hashmap.get("deleteCmpFileSqls") != null) {
						String[] sqls = (String[]) hashmap.get("deleteCmpFileSqls");
						if (sqls != null && sqls.length > 0) {
							for (int i = 0; i < sqls.length; i++) {
								sql_deletefile.add(sqls[i]);
							}
						}
					}
				}
			}

			//以前根据参数配置是否显示来删除，太麻烦，直接将所有sql执行一下即可【李春娟/2014-10-10】
			sql_deletefile.add("delete from cmp_cmpfile_law        where cmpfile_id =" + _cmpfileid); // 流程和环节的相关法规
			sql_deletefile.add("delete from cmp_cmpfile_rule       where cmpfile_id =" + _cmpfileid); // 流程和环节的相关制度
			sql_deletefile.add("delete from cmp_cmpfile_checkpoint where cmpfile_id =" + _cmpfileid); // 流程和环节的检查要点
			sql_deletefile.add("delete from cmp_cmpfile_punish 	   where cmpfile_id =" + _cmpfileid); //流程和环节的相关罚则
			sql_deletefile.add("delete from cmp_cmpfile_wfdesc     where cmpfile_id =" + _cmpfileid); // 流程概述
			sql_deletefile.add("delete from cmp_cmpfile_refwf      where cmpfile_id =" + _cmpfileid); // 流程的相关流程
			sql_deletefile.add("delete from cmp_cmpfile_wfopereq   where cmpfile_id =" + _cmpfileid); // 环节的操作要求
			sql_deletefile.add("delete from cmp_risk               where cmpfile_id =" + _cmpfileid); // 环节的风险点

			sql_deletefile.add("delete from pub_wf_activity        where processid in(" + str_processids + ")");// 环节
			sql_deletefile.add("delete from pub_wf_group           where processid in(" + str_processids + ")");// 阶段和部门
			sql_deletefile.add("delete from pub_wf_transition      where processid in(" + str_processids + ")");// 环节连线
			sql_deletefile.add("delete from pub_wf_process  where cmpfileid =" + _cmpfileid); // 删除流程表
		}
		//下面三条sql不能放到上面的if判断里，否则无流程图的流程文件无法删除了【李春娟/2014-10-30】
		sql_deletefile.add("delete from cmp_cmpfile_hist         where cmpfile_id =" + _cmpfileid); // 删除流程文件历史版本
		sql_deletefile.add("delete from cmp_cmpfile_histcontent  where cmpfile_id =" + _cmpfileid); // 删除流程文件历史版本内容
		sql_deletefile.add("delete from cmp_cmpfile         where id =" + _cmpfileid); // 最后删除流程文件
		dmo.executeBatchByDS(null, sql_deletefile);
	}

	/**
	 * 根据流程id删除一个流程并级联删除流程相关和环节相关的信息
	 * 
	 * @param _wfid
	 *            流程id
	 * @throws Exception
	 */
	public void deleteWfById(String _wfid) throws Exception {
		ArrayList sql_deletewf = new ArrayList();
		String str_userDefinedCls = tbUtil.getSysOptionStringValue("自定义一图两表按钮面板类", null);//如果系统中有自定义一图两表按钮面板类，说明流程相关和环节相关里有可能增加了内容，这时需要获得删除时的sql语句【李春娟/2012-05-14】
		if (str_userDefinedCls != null && !str_userDefinedCls.trim().equals("")) {// 如果配置了自定义一图两表按钮面板类
			HashMap hashmap = new HashMap();
			hashmap.put("wfprocess_id", _wfid);// 将单个流程id传入
			hashmap = tbUtil.reflectCallCommMethod(str_userDefinedCls + ".getDeleteWfSqls()", hashmap);
			if (hashmap != null) {
				if (hashmap.get("deleteWfSqls") != null) {
					String[] sqls = (String[]) hashmap.get("deleteWfSqls");
					if (sqls != null && sqls.length > 0) {
						for (int i = 0; i < sqls.length; i++) {
							sql_deletewf.add(sqls[i]);
						}
					}
				}
			}
		}

		sql_deletewf.add("delete from cmp_cmpfile_law        where wfprocess_id =" + _wfid); // 流程和环节的相关法规
		sql_deletewf.add("delete from cmp_cmpfile_rule       where wfprocess_id =" + _wfid); // 流程和环节的相关制度
		sql_deletewf.add("delete from cmp_cmpfile_checkpoint where wfprocess_id =" + _wfid); // 流程和环节的检查要点
		sql_deletewf.add("delete from cmp_cmpfile_punish 	 where wfprocess_id =" + _wfid); //流程和环节的相关罚则
		sql_deletewf.add("delete from cmp_cmpfile_wfdesc     where wfprocess_id =" + _wfid); // 流程概述
		sql_deletewf.add("delete from cmp_cmpfile_refwf      where wfprocess_id =" + _wfid); // 流程的相关流程
		sql_deletewf.add("delete from cmp_cmpfile_wfopereq   where wfprocess_id =" + _wfid); // 环节的操作要求
		sql_deletewf.add("delete from cmp_risk               where wfprocess_id =" + _wfid); // 环节的风险点

		sql_deletewf.add("delete from pub_wf_activity        where processid =" + _wfid);// 环节
		sql_deletewf.add("delete from pub_wf_group           where processid =" + _wfid);// 阶段和部门
		sql_deletewf.add("delete from pub_wf_transition      where processid =" + _wfid);// 环节连线
		sql_deletewf.add("delete from pub_wf_process         where id =" + _wfid); // 最后删除流程表
		dmo.executeBatchByDS(null, sql_deletewf);
	}

	/**
	 * 根据流程id取得流程相关信息的条数
	 * 
	 * @param _wfid
	 *            流程id
	 * @return
	 * @throws Exception
	 */
	public String[] getRelationCountByWfId(String _wfid) throws Exception {
		String show_wfref = tbUtil.getSysOptionStringValue("体系流程_流程相关", null);
		String[] show_wfrefs = tbUtil.split(show_wfref, ";");
		if (show_wfrefs == null) { //如果没有这个参数,则这里会报一个空指什异常!! xch 2012-02-14
			show_wfrefs = new String[] { "Y", "Y", "Y", "Y", "Y", "Y", "Y" };//去掉了相关案苑【李春娟/2014-09-22】
		} else if (show_wfrefs.length < 7) {
			if (show_wfref.endsWith(";")) {//多加几个配置，防止长度不够而报异常【李春娟/2014-10-21】
				show_wfref = show_wfref + "Y;Y;Y;Y;Y;Y;Y";
			} else {
				show_wfref = show_wfref + ";Y;Y;Y;Y;Y;Y;Y";
			}
			show_wfrefs = tbUtil.split(show_wfref, ";");
		}
		StringBuffer sb_sql = new StringBuffer();
		if ("Y".equals(show_wfrefs[0])) {
			sb_sql.append("select count(id),1 as id from cmp_cmpfile_wfdesc where wfprocess_id=" + _wfid);// 流程的流程概述
		} else {
			sb_sql.append("select 0,1 as id from wltdual");
		}
		if ("Y".equals(show_wfrefs[1])) {
			sb_sql.append(" union all select count(id),2 as id from cmp_cmpfile_law where relationtype='流程' and wfprocess_id=" + _wfid);// 流程的相关法规
		} else {
			sb_sql.append(" union all select 0,2 as id from wltdual");
		}
		if ("Y".equals(show_wfrefs[2])) {
			sb_sql.append(" union all select count(id),3 as id from cmp_cmpfile_rule where relationtype='流程' and wfprocess_id=" + _wfid);// 流程的相关制度
		} else {
			sb_sql.append(" union all select 0,3 as id from wltdual");
		}
		if ("Y".equals(show_wfrefs[3])) {
			sb_sql.append(" union all select count(id),4 as id from cmp_cmpfile_checkpoint where relationtype='流程' and wfprocess_id=" + _wfid);// 流程的检查要点
		} else {
			sb_sql.append(" union all select 0,4 as id from wltdual");
		}
		if ("Y".equals(show_wfrefs[4])) {
			sb_sql.append(" union all select count(id),5 as id from cmp_cmpfile_punish where relationtype='流程' and wfprocess_id=" + _wfid);//流程的相关罚则
		} else {
			sb_sql.append(" union all select 0,5 as id from wltdual");
		}
		//		if ("Y".equals(show_wfrefs[5])) {
		//			sb_sql.append(" union all select 0,6 as id from wltdual");
		//			// sb_sql.append(" union all select count(id) from
		//			// cmp_cmpfile_punish where relationtype='流程' and wfprocess_id=" +
		//			// _wfid);//流程的相关案苑
		//		} else {
		//			sb_sql.append(" union all select 0,6 as id from wltdual");
		//		}

		if (show_wfrefs.length == 8) {
			if ("Y".equals(show_wfrefs[6])) {
				sb_sql.append(" union all select count(id),6 as id from cmp_cmpfile_refwf where wfprocess_id=" + _wfid);// 流程的相关流程
			} else {
				sb_sql.append(" union all select 0,6 as id from wltdual");
			}

			if ("Y".equals(show_wfrefs[7])) {
				sb_sql.append(" union all select count(id),7 as id from cmp_risk where riskreftype='流程' and wfprocess_id=" + _wfid);// 流程的相关风险点
			} else {
				sb_sql.append(" union all select 0,7 as id from wltdual");
			}
		} else {
			if ("Y".equals(show_wfrefs[5])) {
				sb_sql.append(" union all select count(id),6 as id from cmp_cmpfile_refwf where wfprocess_id=" + _wfid);// 流程的相关流程
			} else {
				sb_sql.append(" union all select 0,6 as id from wltdual");
			}

			if ("Y".equals(show_wfrefs[6])) {
				sb_sql.append(" union all select count(id),7 as id from cmp_risk where riskreftype='流程' and wfprocess_id=" + _wfid);// 流程的相关风险点
			} else {
				sb_sql.append(" union all select 0,7 as id from wltdual");
			}

		}
		sb_sql.append(" order by id");
		return dmo.getStringArrayFirstColByDS(null, sb_sql.toString());
	}

	/**
	 * 根据环节id取得环节相关信息的条数
	 * 
	 * @param _activityid
	 *            环节id
	 * @return
	 * @throws Exception
	 */
	public String[] getRelationCountByActivityId(String _activityid) throws Exception {
		String str_sql = getRelationCountSQLByActivityId(_activityid);
		return dmo.getStringArrayFirstColByDS(null, str_sql);
	}

	/**
	 * 根据环节id取得环节相关信息的条数的sql语句
	 * 
	 * @param _activityid
	 *            环节id
	 * @return
	 * @throws Exception
	 */
	private String getRelationCountSQLByActivityId(String _activityid) throws Exception {
		String show_activityref = tbUtil.getSysOptionStringValue("体系流程_环节相关", null);
		String[] show_activityrefs = tbUtil.split(show_activityref, ";");
		if (show_activityrefs == null) { //如果没有这个参数,则这里会报一个空指什异常!! xch 2012-02-14
			show_activityrefs = new String[] { "Y", "Y", "Y", "Y", "Y", "Y" };
		} else if (show_activityrefs.length < 6) {
			if (show_activityref.endsWith(";")) {//多加几个配置，防止长度不够而报异常【李春娟/2014-10-21】
				show_activityref = show_activityref + "Y;Y;Y;Y;Y;Y";
			} else {
				show_activityref = show_activityref + ";Y;Y;Y;Y;Y;Y";
			}
			show_activityrefs = tbUtil.split(show_activityref, ";");
		}
		StringBuffer sb_sql = new StringBuffer();
		if ("Y".equals(show_activityrefs[0])) {
			sb_sql.append("select count(id),1 as id from cmp_cmpfile_wfopereq where wfactivity_id=" + _activityid);// 环节的操作要求
		} else {
			sb_sql.append("select 0,1 as id from wltdual");
		}
		if ("Y".equals(show_activityrefs[1])) {
			sb_sql.append(" union all select count(id),2 as id from cmp_cmpfile_law where wfactivity_id=" + _activityid);// 环节的相关法规
		} else {
			sb_sql.append(" union all select 0,2 as id from wltdual");
		}
		if ("Y".equals(show_activityrefs[2])) {
			sb_sql.append(" union all select count(id),3 as id from cmp_cmpfile_rule where wfactivity_id=" + _activityid);// 环节的相关制度
		} else {
			sb_sql.append(" union all select 0,3 as id from wltdual");
		}
		if ("Y".equals(show_activityrefs[3])) {
			sb_sql.append(" union all select count(id),4 as id from cmp_cmpfile_checkpoint where wfactivity_id=" + _activityid);// 环节的检查要点
		} else {
			sb_sql.append(" union all select 0,4 as id from wltdual");
		}
		if ("Y".equals(show_activityrefs[4])) {
			sb_sql.append(" union all select count(id),5 as id from cmp_cmpfile_punish where wfactivity_id=" + _activityid);//环节的相关罚则
		} else {
			sb_sql.append(" union all select 0,5 as id from wltdual");
		}
		if ("Y".equals(show_activityrefs[5])) {
			sb_sql.append(" union all select count(id),6 as id from cmp_risk where riskreftype='环节' and wfactivity_id=" + _activityid);// 环节的风险点
		} else {
			sb_sql.append(" union all select 0,6 as id from wltdual");
		}
		sb_sql.append(" order by id");
		return sb_sql.toString();
	}

	/**
	 * 根据流程id取得所有环节的环节相关信息的条数
	 * 
	 * @param _processid
	 *            流程id
	 * @return
	 * @throws Exception
	 */
	public HashMap getRelationCountMap(String _processid) throws Exception {
		String[] activityids = dmo.getStringArrayFirstColByDS(null, "select id from pub_wf_activity where processid =" + _processid);
		if (activityids == null || activityids.length == 0) {
			return null;
		}
		HashMap map = new HashMap();
		for (int i = 0; i < activityids.length; i++) {
			String str_sql = getRelationCountSQLByActivityId(activityids[i]);
			map.put(activityids[i], dmo.getStringArrayFirstColByDS(null, str_sql));
		}
		return map;
	}

	/**
	 * 根据多个环节id删除环节的相关信息
	 * 
	 * @param _activityids
	 *            多个环节id
	 * @return
	 * @throws Exception
	 */
	public void deleteActivityRelationByActivityIds(String _activityids) throws Exception {
		ArrayList sql_deleteActivities = new ArrayList();
		String str_userDefinedCls = tbUtil.getSysOptionStringValue("自定义一图两表按钮面板类", null);//如果系统中有自定义一图两表按钮面板类，说明流程相关和环节相关里有可能增加了内容，这时需要获得删除时的sql语句【李春娟/2012-05-14】
		if (str_userDefinedCls != null && !str_userDefinedCls.trim().equals("")) {// 如果配置了自定义一图两表按钮面板类
			HashMap hashmap = new HashMap();
			hashmap.put("wfactivity_ids", _activityids);// 将所有环节id传入
			hashmap = tbUtil.reflectCallCommMethod(str_userDefinedCls + ".getDeleteActivitySqls()", hashmap);
			if (hashmap != null) {
				if (hashmap.get("deleteActivitySqls") != null) {
					String[] sqls = (String[]) hashmap.get("deleteActivitySqls");
					if (sqls != null && sqls.length > 0) {
						for (int i = 0; i < sqls.length; i++) {
							sql_deleteActivities.add(sqls[i]);
						}
					}
				}
			}
		}
		String show_activityref = tbUtil.getSysOptionStringValue("体系流程_环节相关", null);
		String[] show_activityrefs = tbUtil.split(show_activityref, ";");
		if (show_activityrefs == null) { //如果没有这个参数,则这里会报一个空指什异常!! xch 2012-02-14
			show_activityrefs = new String[6];
		}

		sql_deleteActivities.add("delete from cmp_cmpfile_wfopereq   where wfactivity_id in(" + _activityids + ")"); // 环节的操作要求
		sql_deleteActivities.add("delete from cmp_cmpfile_law        where wfactivity_id in(" + _activityids + ")");// 环节的相关法规
		sql_deleteActivities.add("delete from cmp_cmpfile_rule       where wfactivity_id in(" + _activityids + ")");// 环节的相关制度
		sql_deleteActivities.add("delete from cmp_cmpfile_checkpoint where wfactivity_id in(" + _activityids + ")");// 环节的检查要点
		sql_deleteActivities.add("delete from cmp_cmpfile_punish 	 where wfactivity_id in(" + _activityids + ")");//环节的相关罚则
		sql_deleteActivities.add("delete from cmp_risk               where wfactivity_id in(" + _activityids + ")"); // 环节的风险点
		dmo.executeBatchByDS(null, sql_deleteActivities);
	}

	/**
	 * 服务器端word预览，
	 * @param _cmpfileid
	 * @param _wfmap
	 * @return
	 * @throws Exception
	 */
	public String getServerCmpfilePath(String _cmpfileid, HashMap _wfmap) throws Exception {
		TBUtil tbutil = new TBUtil();
		String stringUrl = tbutil.getSysOptionStringValue("生成word需要访问的Servlet", null);
		if (stringUrl != null && !"".equals(stringUrl) && !"N".equalsIgnoreCase(stringUrl)) {//配置了访问另外一个Servlet
			String wfpath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile/word";//创建临时目录C:/WebPushTemp/officecompfile/word
			System.out.println("获取的转发给的servlet：" + stringUrl);
			URL url = new URL(stringUrl);
			URLConnection connection = url.openConnection();//打开url
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(true);

			//发送map到servlet,为了简单起见把compfileid也通过map发送过去，对方接受到以后再将compfileid这个key值remove掉，
			//因为如果将compfileid添加在url后面发送过去，对方那边总是报错，说是什么编码的原因
			ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
			if (_wfmap != null) {
				_wfmap.put("comfileid", _cmpfileid);
			}
			oos.writeObject(_wfmap);
			oos.flush();
			oos.close();
			//接受servlet生成的word
			//通过Map的形式，将文件名称、文件内容(字节数组)等发送过来，接收到以后再将文件内容的字节数组一次性写入到文件中即可
			ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
			HashMap<String, Object> recivedMap = (HashMap<String, Object>) ois.readObject();
			ois.close();

			wfpath = (String) recivedMap.get("name");//得到文件名
			//将读入的字节数组一次性写出到word文件中
			FileOutputStream fos = new FileOutputStream(wfpath);
			fos.write((byte[]) recivedMap.get("bytes"));
			fos.flush();
			fos.close();

			String returnName = wfpath.substring(wfpath.indexOf("/word"), wfpath.length());
			System.out.println(">>>>>>>>>>>>>>>>>>>?????????????????" + wfpath + "-returnName:" + returnName);
			return returnName;
		} else {
			System.out.println("不进行转发");
			WFRiskWordBuilder wordBuilder = new WFRiskWordBuilder(_cmpfileid, _wfmap); //		
			byte[] wfBytes = wordBuilder.getDocContextBytes(true); // 从李春娟提供的生成Word的方法中取得Word格式的二进制流,只有流程说明部分
			String wfpath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile/word";//创建临时目录C:/WebPushTemp/officecompfile/word
			File file = new File(wfpath);
			if (!file.exists()) {//如果服务器端没有该文件夹，则创建之
				file.mkdir();
			}
			wfpath = wfpath + "/wf_" + System.currentTimeMillis() + ".doc";// 流程说明word的完整路径，如C:/WebPushTemp/officecompfile/word/258_1.2.doc
			File wffile = new File(wfpath);
			FileOutputStream output = new FileOutputStream(wffile);
			output.write(wfBytes);
			output.close();

			HashVO[] reffileVO = dmo.getHashVoArrayByDS(null, "select * from cmp_cmpfile where id = '" + _cmpfileid + "'");
			String reffilepath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile" + "/" + reffileVO[0].getStringValue("reffile");//正文的完整路径
			reffilepath = copyFile(reffilepath, reffileVO[0].getStringValue("cmpfilename", "cmpfile") + ".doc"); // 复制正文，返回正文副本的完整路径
			WordTBUtil wordutil = new WordTBUtil();
			HashMap textmap = new HashMap();//要替换的文本map,如key="$发布日期$",value="2011-05-24"
			textmap.put("$文件名称$", reffileVO[0].getStringValue("cmpfilename", ""));
			textmap.put("$编码$", reffileVO[0].getStringValue("cmpfilecode", ""));
			textmap.put("$编制单位$", convertStr(reffileVO[0].getStringValue("blcorpname", "")));
			textmap.put("$发布日期$", tbUtil.getCurrDate() + "    ");
			textmap.put("$相关文件$", reffileVO[0].getStringValue("item_addenda", ""));//新增了几个替换【李春娟/2014-09-22】
			textmap.put("$相关表单$", reffileVO[0].getStringValue("item_formids", ""));
			wordutil.mergeOrReplaceFile(wfpath, reffilepath, "$一图两表$", textmap, _cmpfileid);//合并文件和替换文本

			//最后将流程说明word和正文副本word删除
			if (wffile.exists()) {
				wffile.delete();
			}
			File[] allfile = file.listFiles();
			for (int i = 0; i < allfile.length; i++) {//删除不是今天的冗余文件
				String filename = allfile[i].getName();
				if (filename != null && !filename.startsWith(tbUtil.getCurrDate(false, false)) && !filename.startsWith("wf_")) {
					allfile[i].delete();
				}
			}
			String fileName = reffilepath.substring(reffilepath.indexOf("/word"), reffilepath.length());
			System.out.println("\\\\\\\\\\\\\\\\:" + fileName + "\n" + reffilepath);
			return fileName;
		}
	}

	/**
	 * 在服务器端重新发布所有状态为【有效】的流程文件，将历史版本都删除，然后版本号设置为1.0，主要适用于系统升级后，以前的历史版本和现在的生成方法不同的情况。
	 * 本方法进行了优化，按传入值的流程文件id来进行发布【李春娟/2015-08-07】
	 * @param _overwrite 是否覆盖最新版本,如果覆盖，则最新版本号不变，如果不覆盖则新增一个版本号，或重置为1.0版本号
	 * @throws Exception
	 */
	public HashMap publishAllCmpFile(HashMap _wfmap) throws Exception {
		HashMap hashmap = new HashMap();
		if (_wfmap != null && _wfmap.size() > 0) {
			String[] ids = (String[]) _wfmap.keySet().toArray(new String[0]);
			String cmpfileids = TBUtil.getTBUtil().getInCondition(ids);
			dmo.executeBatchByDS(null, new String[] { "delete from cmp_cmpfile_hist where cmpfile_id in(" + cmpfileids + ")", "delete from cmp_cmpfile_histcontent where cmpfile_id in(" + cmpfileids + ")" });
			String[][] cmpfiles = dmo.getStringArrayByDS(null, "select id,cmpfilename from cmp_cmpfile where id in(" + cmpfileids + ")");//以前只发布有效的，有些问题，应该按传值来判断【李春娟/2015-08-07】
			for (int i = 0; i < cmpfiles.length; i++) {
				if (_wfmap.containsKey(cmpfiles[i][0])) {
					try {
						publishCmpFile(cmpfiles[i][0], cmpfiles[i][1], "1.0", (HashMap) _wfmap.get(cmpfiles[i][0]), true, false);
					} catch (FileNotFoundException e) {
						hashmap.put(cmpfiles[i][0], cmpfiles[i][1]);
					}
				}
			}
		}

		return hashmap;
	}

	/**
	 * 发布某个流程文件,发布的同时，更新版本号，并在文件历史表和文件历史内容表中存入当前流程和流程文件的信息。
	 * word格式只存正文（平台参数:"流程文件是否由正文生成word"="Y","JACOB工作方式"="0"([查看正文]正文和流程图合并方式 0-不启用, 1-客户端, 2-服务器端)）
	 * 
	 * @param _cmpfileid     流程文件id
	 * @param _cmpfilename   流程文件名称
	 * @param _newversionno  新的版本号	 
	 * @param _wfmap         流程图二进制流map
	 * @param _overwrite     如果数据库中已有相同版本的历史记录，是否需要覆盖
	 * @throws Exception
	 */
	public void publishCmpFile(String _cmpfileid, String _cmpfilename, String _newversionno, HashMap _wfmap, boolean _overwrite) throws Exception {
		if (_overwrite) {//如果需要覆盖，则删除同版本号的历史记录
			dmo.executeBatchByDS(null, new String[] { "delete from cmp_cmpfile_hist where cmpfile_id=" + _cmpfileid + " and cmpfile_versionno=" + _newversionno, "delete from cmp_cmpfile_histcontent where cmpfile_id=" + _cmpfileid + " and cmpfile_versionno=" + _newversionno });
		}
		String pushlishdate = tbUtil.getCurrDate(); // 当前日期!
		// 往历史表中写一条数据,即记录下该历史版本!!!
		InsertSQLBuilder isql_insert = new InsertSQLBuilder("cmp_cmpfile_hist"); // 流程文件历史表中新增记录
		String str_cmpfile_histid = dmo.getSequenceNextValByDS(null, "S_CMP_CMPFILE_HIST"); // 体系文件历史id
		isql_insert.putFieldValue("id", str_cmpfile_histid);
		isql_insert.putFieldValue("cmpfile_id", _cmpfileid);// 对应的流程文件id
		isql_insert.putFieldValue("cmpfile_name", _cmpfilename);// 文件名称
		isql_insert.putFieldValue("cmpfile_publishdate", pushlishdate);// 发布日期
		isql_insert.putFieldValue("cmpfile_versionno", _newversionno);// 新的版本号

		if (_newversionno.contains(".") && _newversionno.substring(_newversionno.indexOf("."), _newversionno.length()).length() == 3) {//如果该版本号是小版本号，即小数点后有两位数字，则不需要修改文件的状态及版本号
			dmo.executeBatchByDS(null, new String[] { isql_insert.getSQL() });//执行更新和新增历史记录
		} else {
			String updatesql = "update cmp_cmpfile set filestate='3',publishdate='" + pushlishdate + "',versionno=" + _newversionno + " where id=" + _cmpfileid; // 修改本体系文件的状态!!!	
			dmo.executeBatchByDS(null, new String[] { updatesql, isql_insert.getSQL() });//执行更新和新增历史记录
		}

		HashVO[] reffileVO = dmo.getHashVoArrayByDS(null, "select * from cmp_cmpfile where id = '" + _cmpfileid + "'");
		String reffilepath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile" + "/" + reffileVO[0].getStringValue("reffile");//服务器端正文的完整路径
		InputStream input = new FileInputStream(reffilepath);
		byte docBytes[] = tbUtil.readFromInputStreamToBytes(input);
		insertCmpFileByDoc(_cmpfileid, str_cmpfile_histid, _newversionno, docBytes);//存储doc格式的流程文件
		insertCmpFileByHtml(_cmpfileid, str_cmpfile_histid, _newversionno, _wfmap);//存储html格式的流程文件
	}

	/**
	 * 发布某个流程文件,发布的同时，更新版本号，并在文件历史表和文件历史内容表中存入当前流程和流程文件的信息。
	 * 如果参数_showreffile为true，word格式合并正文和流程说明部分，并且是在服务器端合并
	 * （平台参数:"流程文件是否由正文生成word"="Y","JACOB工作方式"="2"([查看正文]正文和流程图合并方式 0-不启用, 1-客户端, 2-服务器端)）；
	 * 如果参数_showreffile为false，说明系统不使用正文，则用itext直接由文件子项（目的、适用范围等）写文档
	 * 
	 * @param _cmpfileid    流程文件id
	 * @param _cmpfilename  流程文件名称
	 * @param _newversionno 新的版本号	 
	 * @param _wfmap        流程图二进制流map
	 * @param _showreffile  是否有正文
	 * @param _overwrite    如果数据库中已有相同版本的历史记录，是否需要覆盖
	 * @throws Exception
	 */
	public void publishCmpFile(String _cmpfileid, String _cmpfilename, String _newversionno, HashMap _wfmap, boolean _showreffile, boolean _overwrite) throws Exception {
		if (_overwrite) {//如果需要覆盖，则删除同版本号的历史记录
			dmo.executeBatchByDS(null, new String[] { "delete from cmp_cmpfile_hist where cmpfile_id=" + _cmpfileid + " and cmpfile_versionno=" + _newversionno, "delete from cmp_cmpfile_histcontent where cmpfile_id=" + _cmpfileid + " and cmpfile_versionno=" + _newversionno });
		}
		String pushlishdate = tbUtil.getCurrDate(); // 当前日期!
		// 往历史表中写一条数据,即记录下该历史版本!!!
		InsertSQLBuilder isql_insert = new InsertSQLBuilder("cmp_cmpfile_hist"); // 流程文件历史表中新增记录
		String str_cmpfile_histid = dmo.getSequenceNextValByDS(null, "S_CMP_CMPFILE_HIST"); // 体系文件历史id
		isql_insert.putFieldValue("id", str_cmpfile_histid);
		isql_insert.putFieldValue("cmpfile_id", _cmpfileid);// 对应的流程文件id
		isql_insert.putFieldValue("cmpfile_name", _cmpfilename);// 文件名称
		isql_insert.putFieldValue("cmpfile_publishdate", pushlishdate);// 发布日期
		isql_insert.putFieldValue("cmpfile_versionno", _newversionno);// 新的版本号

		if (_newversionno.contains(".") && _newversionno.substring(_newversionno.indexOf("."), _newversionno.length()).length() == 3) {//如果该版本号是小版本号，即小数点后有两位数字，则不需要修改文件的状态及版本号
			dmo.executeBatchByDS(null, new String[] { isql_insert.getSQL() });//执行更新和新增历史记录
		} else {
			String updatesql = "update cmp_cmpfile set filestate='3',publishdate='" + pushlishdate + "',versionno=" + _newversionno + " where id=" + _cmpfileid; // 修改本体系文件的状态!!!	
			dmo.executeBatchByDS(null, new String[] { updatesql, isql_insert.getSQL() });//执行更新和新增历史记录
		}

		// 存储历史版本的内容!!cmp_cmpfile_histcontent
		// 以前的历史处理是将一批表都复制了一遍,非常麻烦与不稳定,而且对历史信息进行统计的情况并不多!
		// 所以新的机制是将数据生成html报表存下来,直接存在cmp_cmpfile_histcontent表中!
		// 至于统计某段时间的风险点的历史迁移情况,则通过对风险点表的日志记录来实现!

		//_showreffile默认有正文，这里要跟文件维护时的默认值保持一致
		WFRiskWordBuilder wordBuilder = new WFRiskWordBuilder(_cmpfileid, _wfmap); //
		if (_showreffile) {//如果有正文，则保存正文的内容，否则保存文件子项，如目的、适用范围等富文本框的内容
			//如果系统使用正文，则要将正文和流程说明部分合并，并将正文中文件名称，编码，编制单位，发布日期，生效日期等替换，
			//因iText没有合并和替换的功能，所以采用jacob合并word或替换word文本，
			//首先将流程说明部分（包括流程图和环节信息表格）使用iText技术生成word文档，
			//然后将流程文件的正文复制一份，将正文副本和流程说明文档合并，并将正文的副本中有特殊标识的替换掉，
			//然后将正文副本，也就是最完整的文件以流的形式保存到数据库中
			//最后删除流程说明和正文副本两个临时文件
			byte[] wfBytes = wordBuilder.getDocContextBytes(true); // 从李春娟提供的生成Word的方法中取得Word格式的二进制流,只有流程说明部分
			String wfpath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile/word";//创建临时目录C:/WebPushTemp/officecompfile/word
			File wffile = new File(wfpath);
			if (!wffile.exists()) {//如果服务器端没有该文件夹，则创建之
				wffile.mkdir();
			}

			wfpath = wfpath + "/wf_" + System.currentTimeMillis() + ".doc";// 流程说明word的完整路径，如C:/WebPushTemp/officecompfile/word/258_1.2.doc
			wffile = new File(wfpath);
			FileOutputStream output = new FileOutputStream(wffile);
			output.write(wfBytes);
			output.close();

			HashVO[] reffileVO = dmo.getHashVoArrayByDS(null, "select * from cmp_cmpfile where id = '" + _cmpfileid + "'");
			String reffilepath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile" + "/" + reffileVO[0].getStringValue("reffile");//正文的完整路径
			reffilepath = copyFile(reffilepath, null); // 复制正文，返回正文副本的完整路径
			WordTBUtil wordutil = new WordTBUtil();
			HashMap textmap = new HashMap();//要替换的文本map,如key="$发布日期$",value="2011-05-24"
			textmap.put("$文件名称$", reffileVO[0].getStringValue("cmpfilename", ""));
			textmap.put("$编码$", reffileVO[0].getStringValue("cmpfilecode", ""));
			textmap.put("$编制单位$", convertStr(reffileVO[0].getStringValue("blcorpname", "")));
			textmap.put("$发布日期$", tbUtil.getCurrDate() + "    ");
			textmap.put("$相关文件$", reffileVO[0].getStringValue("item_addenda", ""));//新增了几个替换【李春娟/2014-09-22】
			textmap.put("$相关表单$", reffileVO[0].getStringValue("item_formids", ""));
			wordutil.mergeOrReplaceFile(wfpath, reffilepath, "$一图两表$", textmap, _cmpfileid);//合并文件和替换文本
			InputStream input = new FileInputStream(reffilepath);

			byte[] docBytes = tbUtil.readFromInputStreamToBytes(input);
			insertCmpFileByDoc(_cmpfileid, str_cmpfile_histid, _newversionno, docBytes);//存储doc格式的流程文件

			//最后将流程说明word和正文副本word删除
			File reffile = new File(reffilepath);
			if (wffile.exists()) {
				wffile.delete();
			}
			if (reffile.exists()) {
				reffile.delete();
			}
		} else {
			//如果系统不使用正文，则用iText技术，存储Word文件
			byte[] docBytes = wordBuilder.getDocContextBytes(); //从李春娟提供的生成Word的方法中取得Word格式的二进制流
			insertCmpFileByDoc(_cmpfileid, str_cmpfile_histid, _newversionno, docBytes);//存储doc格式的流程文件
		}
		insertCmpFileByHtml(_cmpfileid, str_cmpfile_histid, _newversionno, _wfmap);//存储html格式的流程文件p
	}

	/**
	 * 如果系统有正文并且在客户端合并正文和流程说明部分，就得在合并前将当前版本信息添加到历史记录中，这样在合并时修改记录表中才会有当前版本
	 * 这里判断是否需要覆盖同版本历史记录的而不是在发布时判断，因为在客户端发布才会使用这个方法，并且这个方法是产生了新版本号的一条历史记录（不包括内容）
	 * 
	 * @param _cmpfileid    流程文件id
	 * @param _cmpfilename  流程文件名称
	 * @param _newversionno 流程文件要发布的版本号
	 * @param _overwrite    如果数据库中已有相同版本的历史记录，是否需要覆盖
	 * @return
	 * @throws Exception
	 */
	public String addCmpfileHist(String _cmpfileid, String _cmpfilename, String _newversionno, boolean _overwrite) throws Exception {
		if (_overwrite) {//如果需要覆盖，则删除同版本号的历史记录
			dmo.executeBatchByDS(null, new String[] { "delete from cmp_cmpfile_hist where cmpfile_id=" + _cmpfileid + " and cmpfile_versionno=" + _newversionno, "delete from cmp_cmpfile_histcontent where cmpfile_id=" + _cmpfileid + " and cmpfile_versionno=" + _newversionno });
		}
		String pushlishdate = tbUtil.getCurrDate(); // 当前日期!
		// 往历史表中写一条数据,即记录下该历史版本!!!
		InsertSQLBuilder isql_insert = new InsertSQLBuilder("cmp_cmpfile_hist"); // 流程文件历史表中新增记录
		String str_cmpfile_histid = dmo.getSequenceNextValByDS(null, "S_CMP_CMPFILE_HIST"); // 体系文件历史id
		isql_insert.putFieldValue("id", str_cmpfile_histid);
		isql_insert.putFieldValue("cmpfile_id", _cmpfileid);// 对应的流程文件id
		isql_insert.putFieldValue("cmpfile_name", _cmpfilename);// 文件名称
		isql_insert.putFieldValue("cmpfile_publishdate", pushlishdate);// 发布日期
		isql_insert.putFieldValue("cmpfile_versionno", _newversionno);// 新的版本号
		dmo.executeUpdateByDS(null, isql_insert);//执行更新版本和新增历史记录
		return str_cmpfile_histid;
	}

	/**
	 * 发布某个流程文件,件历史发布的同时，更新版本号，并在文件历史表和文内容表中存入当前流程和流程文件的信息。
	 * 因itext不能实现正文和流程说明部分合并，故用jacob实现，而jacob必须在装有word的环境下执行，而有时服务器端（Linux系统）是不能满足的，
	 * 故在客户端先合并好，生成要保存的压缩后的64位码，然后传到服务器端进行其他操作。
	 * word格式合并正文和流程说明部分，并且是在客户端合并（平台参数:"流程文件是否由正文生成word"="Y","JACOB工作方式"="1"([查看正文]正文和流程图合并方式 0-不启用, 1-客户端, 2-服务器端)）
	 * 
	 * 这是唯一一个不用判断是否需要覆盖同版本历史记录的发布方法，因为在客户端发布才会使用这个方法，而在此之前会调用新增新版本号的历史记录（不包括内容）时，会判断是否需要覆盖同版本号的历史记录（包括内容），
	 * 如果在这里判断的话，会将本版本号的历史记录（不包括内容）给删除掉了
	 * @param _cmpfileid       流程文件id
	 * @param _cmpfilename     流程文件名称
	 * @param _newversionno    新的版本号
	 * @param _cmpfile_histid  历史记录表id，在正文和流程说明部分的合并前要把发布后的版本存入历史记录表中，这样在发布的word文档中的修改记录才会有当前版本
	 * @param _wfmap           流程图二进制流map
	 * @param _doc64code       客户端合并好的word版本流程文件
	 * @throws Exception
	 */
	public void publishCmpFile(String _cmpfileid, String _cmpfilename, String _newversionno, String _cmpfile_histid, HashMap _wfmap, String _doc64code) throws Exception {
		if (!_newversionno.contains(".") || _newversionno.substring(_newversionno.indexOf("."), _newversionno.length()).length() < 3) {//如果该版本号是小版本号，即小数点后有两位数字，则不需要修改文件的状态及版本号
			String updatesql = "update cmp_cmpfile set filestate='3',publishdate='" + tbUtil.getCurrDate() + "',versionno=" + _newversionno + " where id=" + _cmpfileid; // 修改本体系文件的状态!!!	
			dmo.executeBatchByDS(null, new String[] { updatesql });//执行更新和新增历史记录
		}

		// 存储历史版本的内容!!cmp_cmpfile_histcontent
		insertCmpFileHistContent(_cmpfileid, _cmpfile_histid, _newversionno, _doc64code, "DOC"); //实际存储!已经是压缩过的不需要再压缩了
		insertCmpFileByHtml(_cmpfileid, _cmpfile_histid, _newversionno, _wfmap);//存储html格式的流程文件
	}

	private String copyFile(String _oldFilePath, String _newFilename) throws Exception {
		File file = new File(_oldFilePath);
		InputStream input = new FileInputStream(file);
		byte[] by = tbUtil.readFromInputStreamToBytes(input);
		if (_newFilename == null || _newFilename.trim().equals("")) {
			_newFilename = _oldFilePath.substring(_oldFilePath.lastIndexOf("/") + 1);
		}
		//需要处理一下，因为文件名中不能包括以下特殊符号：\/:*?"<>|
		_newFilename = tbUtil.replaceAll(_newFilename, "\\", "＼");
		_newFilename = tbUtil.replaceAll(_newFilename, "/", "／");
		_newFilename = tbUtil.replaceAll(_newFilename, ":", "：");
		_newFilename = tbUtil.replaceAll(_newFilename, "*", "×");
		_newFilename = tbUtil.replaceAll(_newFilename, "?", "？");
		_newFilename = tbUtil.replaceAll(_newFilename, "\"", "“");
		_newFilename = tbUtil.replaceAll(_newFilename, "<", "＜");
		_newFilename = tbUtil.replaceAll(_newFilename, ">", "＞");
		_newFilename = tbUtil.replaceAll(_newFilename, "|", "│");//注意这两个竖线是不一样的哦
		_newFilename = tbUtil.replaceAll(_newFilename, " ", "");
		Random rand = new Random();
		_newFilename = new TBUtil().getCurrTime(false, false) + rand.nextInt(100) + "_" + _newFilename;//这里增加了一个随机数的设置，因为如果两个人同一时间进行word预览，这时word文档就会锁住，这时必须在服务器端选择一下，或结束进程才可以再次word预览【李春娟/2012-10-29】
		String newFilePath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile/word/" + _newFilename;
		FileOutputStream output = new FileOutputStream(newFilePath);
		output.write(by);
		input.close();
		output.close();
		return newFilePath;
	}

	/**
	 * 流程文件发布时，存储doc格式的流程文件
	 * @param _cmpfileid
	 * @param _cmpfile_histid
	 * @param _newversionno
	 * @param _docBytes
	 * @throws Exception
	 */
	private void insertCmpFileByDoc(String _cmpfileid, String _cmpfile_histid, String _newversionno, byte[] _docBytes) throws Exception {
		byte[] zipDocBytes = tbUtil.compressBytes(_docBytes); //压缩一下!!
		String str_doc_64code = tbUtil.convertBytesTo64Code(zipDocBytes); //转成64位编码!!!
		insertCmpFileHistContent(_cmpfileid, _cmpfile_histid, _newversionno, str_doc_64code, "DOC"); //实际存储!!!
	}

	/**
	 * 流程文件发布时，存储html格式的流程文件
	 * @param _cmpfileid
	 * @param _cmpfile_histid
	 * @param _newversionno
	 * @param _wfmap
	 * @throws Exception
	 */
	private void insertCmpFileByHtml(String _cmpfileid, String _cmpfile_histid, String _newversionno, HashMap _wfmap) throws Exception {
		WFRiskHtmlBuilder htmlBuilder = new WFRiskHtmlBuilder(); //
		String str_html = htmlBuilder.getHtmlContentByHist(_cmpfileid, _cmpfile_histid, 0); // 从李春娟提供方法中取得html内容!!!
		// 在调用图片的地方用使用<img
		// src="./WebDispatchServlet?cls=com.pushworld.ipushgrc.bs.wfrisk.WFRiskHistImageDispatchWeb&cmpfilehistid=%CMPFILE_HISTID%&imgname=IMAGE_1">
		byte[] htmlBytes = str_html.getBytes("UTF-8"); // 转成byte[],因为想压缩一下!
		byte[] zipHtmlBytes = tbUtil.compressBytes(htmlBytes); // 压缩一下!!
		String str_html_64code = tbUtil.convertBytesTo64Code(zipHtmlBytes); // 转成64位编码!!!
		insertCmpFileHistContent(_cmpfileid, _cmpfile_histid, _newversionno, str_html_64code, "HTML"); // 实际存储!!

		// 存储Html中的图片,然后在浏览时通过Servlet调用数据库中的内容进行渲染!!
		String[] str_imgkeys = (String[]) _wfmap.keySet().toArray(new String[0]); //
		for (int i = 0; i < str_imgkeys.length; i++) {
			byte[] imgBytes = (byte[]) _wfmap.get(str_imgkeys[i]); // 这是个是已经压缩过的,所以不需要再次压缩!!!
			String str_img64code = tbUtil.convertBytesTo64Code(imgBytes); // 转64位码
			insertCmpFileHistContent(_cmpfileid, _cmpfile_histid, _newversionno, str_img64code, "IMAGE_" + str_imgkeys[i]); // 保存到数据库!!
		}
	}

	/**
	 * 将一段内容插入数据库
	 * 
	 * @param _cmpfileid
	 * @param str_cmpfile_histid
	 * @param _newversionno
	 * @param _64code
	 * @param _contentname
	 * @throws Exception
	 */
	private void insertCmpFileHistContent(String _cmpfileid, String str_cmpfile_histid, String _newversionno, String _64code, String _contentname) throws Exception {
		ArrayList al_doc_64codesplit = tbUtil.split(_64code, 10, 2000); //
		for (int i = 0; i < al_doc_64codesplit.size(); i++) {
			String[] str_rowdata = (String[]) al_doc_64codesplit.get(i); // 一行的数据!!!
			String str_histcontent_id = null; //
			for (int j = 0; j < str_rowdata.length; j++) { // 遍历各列
				if (j == 0) { // 如果是第一列则做insert操作,从第2列起做update操作,这是为了防止SQL太长,执行不过!!!
					str_histcontent_id = dmo.getSequenceNextValByDS(null, "S_CMP_CMPFILE_HISTCONTENT"); // 历史内容id
					InsertSQLBuilder isql_insert = new InsertSQLBuilder("cmp_cmpfile_histcontent"); // 流程文件历史内容表中新增记录
					isql_insert.putFieldValue("id", str_histcontent_id); // 主键
					isql_insert.putFieldValue("cmpfile_id", _cmpfileid); // 体系文件id
					isql_insert.putFieldValue("cmpfile_histid", str_cmpfile_histid); // 某个历史版本的id
					isql_insert.putFieldValue("cmpfile_versionno", _newversionno); // 版本号!
					isql_insert.putFieldValue("contentname", _contentname); // 内容名称是DOC,即表示是Word格式!
					isql_insert.putFieldValue("seq", (i + 1)); // 序号!!
					isql_insert.putFieldValue("doc" + j, str_rowdata[j]); // 设置值!!!
					dmo.executeUpdateByDS(null, isql_insert.getSQL(), false); // 直接插入数据库!!!
				} else {
					UpdateSQLBuilder isql_update = new UpdateSQLBuilder("cmp_cmpfile_histcontent", "id='" + str_histcontent_id + "'"); // 从第2列开始就做update操作!
					isql_update.putFieldValue("doc" + j, str_rowdata[j]); // 设置值
					dmo.executeUpdateByDS(null, isql_update.getSQL(), false); // 直接插入数据库!!!
				}
			}
		}
	}

	/**
	 * 取提Word文件内容!!!
	 * 
	 * @param _cmpfileid 流程文件id
	 * @param _wfmap     流程图片的内容!!
	 * @param _onlywf    是否只有流程说明部分!!
	 * @return
	 * @throws Exception
	 */
	public byte[] getDocContextBytes(String _cmpfileid, HashMap _wfmap, boolean _onlywf) throws Exception {
		return new WFRiskWordBuilder(_cmpfileid, _wfmap).getDocContextBytes(_onlywf);
	}

	/**
	 * 查看流程后判断是否要记录日志
	 * 
	 * @param _cmpfileid
	 *            流程文件id
	 * @param _userid
	 *            用户id
	 * @param _clicktime
	 *            查看时间
	 * @return
	 * @throws Exception
	 */
	public boolean clickCmpFile(String _cmpfileid, String _userid, String _clicktime) throws Exception {
		String clicktime = dmo.getStringValueByDS(null, "select max(clicktime) from cmp_cmpfile_clicklog where cmpfile_id=" + _cmpfileid + " and userid=" + _userid);
		if (clicktime == null || "".equals(clicktime)) {
			return true;
		}
		if (clicktime.length() < 14) { //如果原有系统插入的时间只到天。没有到具体时间。返回true。[郝明2012-02-28]
			return true;
		}
		if (clicktime.substring(0, 14).equals(_clicktime.substring(0, 14))) {
			return false;
		}
		return true;
	}

	/**
	 * 删除流程文件的一个历史版本的所有记录
	 * @param _cmpfile_histid 历史版本id
	 * @throws Exception
	 */
	public void deleteCmpFileHistById(String _cmpfile_histid) throws Exception {
		dmo.executeBatchByDS(null, new String[] { "delete from cmp_cmpfile_hist where id =" + _cmpfile_histid, "delete from cmp_cmpfile_histcontent where cmpfile_histid=" + _cmpfile_histid });
	}

	/**
	 * 删除流程文件的所有历史版本的所有记录，并且更新文件发布日期和版本号为空,状态为“编辑中”
	 * @param _cmpfileid 流程文件id
	 * @throws Exception
	 */
	public void deleteAllCmpFileHistByCmpfileId(String _cmpfileid) throws Exception {
		ArrayList sqllist = new ArrayList();
		sqllist.add("delete from cmp_cmpfile_histcontent where cmpfile_id=" + _cmpfileid);
		sqllist.add("delete from cmp_cmpfile_hist where cmpfile_id =" + _cmpfileid);
		sqllist.add("update cmp_cmpfile  set publishdate=null,versionno=null,filestate='1'  where id =" + _cmpfileid);
		dmo.executeBatchByDS(null, sqllist);
	}

	/**
	 * 一个BOM图中的所有热点的RiskVO
	 * @param _bomtype  "RISK"、"PROCESS"、"CMPFILE"
	 * @param _datatype  "BLCORPNAME"、"ICTYPENAME"
	 * @param _alldatas  BOM图所有热点值，只有机构才需要设置
	 * @param _isSelfCorp  是否查询本机构
	 * @return
	 * @throws Exception
	 */
	public Hashtable getHashtableRiskVO(String _bomtype, String _datatype, ArrayList _alldatas, boolean _isSelfCorp) throws Exception {
		String contion = "";
		if (_isSelfCorp) {
			String str_loginUserId = new WLTInitContext().getCurrSession().getLoginUserId(); //
			String[] deptids = new DataPolicyDMO().getDataPolicyCondition(str_loginUserId, "机构查询策略(本机构)", 1, "blcorpid", null);
			if (deptids == null || deptids.length < 4) {//如果登录人员无所属机构，会报异常，故修改之【李春娟/2014-12-25】
				contion = " and 1=2 ";
			} else {
				String currUserDataPolicyDeptids = deptids[3];
				contion = " and blcorpid in(" + tbUtil.getInCondition(currUserDataPolicyDeptids) + ") ";
			}
		}
		Hashtable ht_data = new Hashtable();
		if ("RISK".equalsIgnoreCase(_bomtype)) {
			if ("BLCORPNAME".equalsIgnoreCase(_datatype)) {
				for (int i = 0; i < _alldatas.size(); i++) {
					if (_alldatas.get(i) == null) {//必须设置一下，否则如果为空，ht_data.get(null) 会报空指针异常【李春娟/2014-08-25】
						continue;
					}
					HashVO[] hashvo = dmo.getHashVoArrayByDS(null, "select risk_rank,count(risk_id) c1 from v_risk_process_file where risk_rank is not null and filestate='3' and blcorpname like '%" + _alldatas.get(i) + "' " + contion + " group by risk_rank");
					RiskVO riskVO = new RiskVO();
					for (int j = 0; j < hashvo.length; j++) {
						String rank = hashvo[j].getStringValue("risk_rank");
						if (rank.equals("极大风险") || rank.equals("高风险")) {
							riskVO.setLevel1RiskCount(riskVO.getLevel1RiskCount() + Integer.parseInt(hashvo[j].getStringValue("c1")));
						} else if (rank.equals("极小风险") || rank.equals("低风险")) {
							riskVO.setLevel3RiskCount(riskVO.getLevel3RiskCount() + Integer.parseInt(hashvo[j].getStringValue("c1")));
						} else if (rank.equals("中等风险")) {
							riskVO.setLevel2RiskCount(riskVO.getLevel2RiskCount() + Integer.parseInt(hashvo[j].getStringValue("c1")));
						}
					}
					ht_data.put(_alldatas.get(i), riskVO);
				}
			} else if ("ICTYPENAME".equalsIgnoreCase(_datatype)) {
				HashVO[] hashvo = dmo.getHashVoArrayByDS(null, "select risk_rank,count(risk_id) c1,ictypename from v_risk_process_file where risk_rank is not null and filestate='3' " + contion + " group by risk_rank,ictypename");
				for (int i = 0; i < hashvo.length; i++) {
					if (hashvo[i].getStringValue("ictypename") == null) {//必须设置一下，否则如果为空，ht_data.get(null) 会报空指针异常【李春娟/2014-08-25】
						continue;
					}
					RiskVO riskVO = null;
					if (ht_data.containsKey(hashvo[i].getStringValue("ictypename"))) {
						riskVO = (RiskVO) ht_data.get(hashvo[i].getStringValue("ictypename"));
					} else {
						riskVO = new RiskVO();
					}
					String rank = hashvo[i].getStringValue("risk_rank");
					if (rank.equals("极大风险") || rank.equals("高风险")) {
						riskVO.setLevel1RiskCount(riskVO.getLevel1RiskCount() + Integer.parseInt(hashvo[i].getStringValue("c1")));
					} else if (rank.equals("极小风险") || rank.equals("低风险")) {
						riskVO.setLevel3RiskCount(riskVO.getLevel3RiskCount() + Integer.parseInt(hashvo[i].getStringValue("c1")));
					} else if (rank.equals("中等风险")) {
						riskVO.setLevel2RiskCount(riskVO.getLevel2RiskCount() + Integer.parseInt(hashvo[i].getStringValue("c1")));
					}
					ht_data.put(hashvo[i].getStringValue("ictypename"), riskVO);
				}
			}
		} else if ("PROCESS".equalsIgnoreCase(_bomtype)) {
			if ("BLCORPNAME".equalsIgnoreCase(_datatype)) {
				for (int i = 0; i < _alldatas.size(); i++) {
					if (_alldatas.get(i) == null) {//必须设置一下，否则如果为空，ht_data.get(null) 会报空指针异常【李春娟/2014-08-25】
						continue;
					}
					String[] str_process = dmo.getStringArrayFirstColByDS(null, "select count(id) c1 from cmp_cmpfile where filestate='3' and blcorpname like '%" + _alldatas.get(i) + "' " + contion + " union all select count(wfprocess_id) c1 from v_process_file where filestate='3' and blcorpname like '%" + _alldatas.get(i) + "' " + contion);
					RiskVO riskVO = new RiskVO();
					riskVO.setLevel2RiskCount(Integer.parseInt(str_process[0]));
					riskVO.setLevel3RiskCount(Integer.parseInt(str_process[1]));
					ht_data.put(_alldatas.get(i), riskVO);
				}
			} else if ("ICTYPENAME".equalsIgnoreCase(_datatype)) {
				HashVO[] hashvo = dmo.getHashVoArrayByDS(null, "select count(id) c1,ictypename from cmp_cmpfile where filestate='3' " + contion + " group by ictypename");
				for (int i = 0; i < hashvo.length; i++) {
					if (hashvo[i].getStringValue("ictypename") == null) {//必须设置一下，否则如果为空，ht_data.get(null) 会报空指针异常【李春娟/2014-08-25】
						continue;
					}
					RiskVO riskVO = new RiskVO();
					riskVO.setLevel2RiskCount(Integer.parseInt(hashvo[i].getStringValue("c1")));
					ht_data.put(hashvo[i].getStringValue("ictypename"), riskVO);
				}

				hashvo = dmo.getHashVoArrayByDS(null, "select count(wfprocess_id) c1,ictypename from v_process_file where filestate='3' " + contion + " group by ictypename");
				for (int i = 0; i < hashvo.length; i++) {
					if (hashvo[i].getStringValue("ictypename") == null) {//必须设置一下，否则如果为空，ht_data.get(null) 会报空指针异常【李春娟/2014-08-25】
						continue;
					}
					RiskVO riskVO = (RiskVO) ht_data.get(hashvo[i].getStringValue("ictypename"));
					if (riskVO == null) {
						continue;
					}
					riskVO.setLevel3RiskCount(Integer.parseInt(hashvo[i].getStringValue("c1")));
					ht_data.put(hashvo[i].getStringValue("ictypename"), riskVO);
				}
			}
		}
		return ht_data;
	}

	/**
	 * 将短的字符串后面加空格，变成不小于某长度的字符串
	 * @param _oldstr
	 * @return
	 */
	private String convertStr(String _oldstr) {
		if (_oldstr == null || "".equals(_oldstr)) {
			return "              ";
		}
		int i = _oldstr.getBytes().length;
		StringBuffer sb_str = new StringBuffer(_oldstr);
		for (; i < 14; i++) {
			sb_str.append(" ");
		}
		return sb_str.toString();
	}

	/**
	 * 在服务器端创建流程文件最新版本的word文档，在上传office控件下的word目录中，以历史版本主表id为文档名称【李春娟/2015-02-11】
	 * @param _cmpfileid
	 * @return				返回文件名称，如123.doc
	 * @throws Exception
	 */
	public String createCmpfileByHistWord(String _cmpfileid) throws Exception {
		String str_cmpfilehistid = dmo.getStringValueByDS(null, "select id from cmp_cmpfile_hist where cmpfile_id =" + _cmpfileid + " order by cmpfile_versionno desc");
		if (str_cmpfilehistid == null || "".endsWith(str_cmpfilehistid)) {
			return null;
		}
		HashVO[] hvs = dmo.getHashVoArrayByDS(null, "select * from cmp_cmpfile_histcontent where cmpfile_histid='" + str_cmpfilehistid + "' and contentname='DOC' order by seq"); //
		StringBuilder sb_doc = new StringBuilder(); //
		String str_itemValue = null; //
		for (int i = 0; i < hvs.length; i++) {
			for (int j = 0; j < 10; j++) {
				str_itemValue = hvs[i].getStringValue("doc" + j); //
				if (str_itemValue == null || str_itemValue.trim().equals("")) {
					break; //
				} else {
					sb_doc.append(str_itemValue.trim()); //拼接!!!
				}
			}
		}
		String str_64code = sb_doc.toString(); //
		byte[] bytes = tbUtil.convert64CodeToBytes(str_64code); //
		byte[] unZipedBytes = tbUtil.decompressBytes(bytes); //解压!!这就是Word的实际内容,用于输出!!!
		//以历史版本主表id作为文件名，必定不重复
		String newFilePath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile/word/" + str_cmpfilehistid + ".doc";
		File file = new File(newFilePath);
		if (!file.exists()) {
			String FilePath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile/word";
			File word = new File(FilePath);
			if (!word.exists()) {//需要判断一下是否有word路径【李春娟/2015-02-11】
				word.mkdir();
			}
			FileOutputStream output = new FileOutputStream(newFilePath);
			output.write(unZipedBytes);
			output.close();
		}
		return str_cmpfilehistid + ".doc";
	}
	
	public boolean outputAllCmpFileByHisWord() throws Exception{
		
		String outputPath = ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/officecompfile/word/";
		
		String sql = 
			"select id,cmpfile_name,cmpfile_versionno from cmp_cmpfile_hist as a " +
			"	where cmpfile_versionno=(select max(cmpfile_versionno) from cmp_cmpfile_hist as b " +
			"	where a.cmpfile_id = b.cmpfile_id) and id = 923";
		HashVO[] cmpFileHisVOs = dmo.getHashVoArrayByDS(null, sql);
		
		for(HashVO cmpFileHisVO : cmpFileHisVOs){
			String cmpFileHisID = cmpFileHisVO.getStringValue("id");
			String cmpFileName = cmpFileHisVO.getStringValue("cmpfile_name");
			HashVO[] hvs = dmo.getHashVoArrayByDS(null, "select * from cmp_cmpfile_histcontent where cmpfile_histid='" + cmpFileHisID + "' and contentname='DOC' order by seq"); //
			StringBuilder sb_doc = new StringBuilder(); //
			String str_itemValue = null; //
			for (int i = 0; i < hvs.length; i++) {
				for (int j = 0; j < 10; j++) {
					str_itemValue = hvs[i].getStringValue("doc" + j); //
					if (str_itemValue == null || str_itemValue.trim().equals("")) {
						break; //
					} else {
						sb_doc.append(str_itemValue.trim()); //拼接!!!
					}
				}
			}
			String str_64code = sb_doc.toString();
			byte[] bytes = tbUtil.convert64CodeToBytes(str_64code);
			byte[] unZipedBytes = tbUtil.decompressBytes(bytes); //解压!!这就是Word的实际内容,用于输出!!!
			
			String outputFile = outputPath + cmpFileName + ".doc";
			File file = new File(outputFile);
			if (!file.exists()) {
				FileOutputStream output = new FileOutputStream(outputFile);
				output.write(unZipedBytes);
				output.close();
			}
		}
		return true;
	}
}
