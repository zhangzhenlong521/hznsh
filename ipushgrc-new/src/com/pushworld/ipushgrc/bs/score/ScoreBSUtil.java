package com.pushworld.ipushgrc.bs.score;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.mdata.MetaDataDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.report.ReportExportWord;

import com.pushworld.ipushgrc.to.score.ScoreWordTBUtil;

/**
 * 违规积分模块客户端工具【李春娟/2013-05-17】
 * @author lcj
 *
 */
public class ScoreBSUtil {
	private TBUtil tbUtil = new TBUtil();
	private ScoreWordTBUtil wordutil = new ScoreWordTBUtil();//word合并及替换工具
	private CommDMO commDMO = new CommDMO();
	private String viewtype = TBUtil.getTBUtil().getSysOptionStringValue("违规积分通知单查看方式", "WORD");
	public void createPunishword(String id, String userid) throws Exception {
		MetaDataDMO dataDMO = new MetaDataDMO();
		String cmpfilename = "";//文件名
		Pub_Templet_1VO templet_1VO = dataDMO.getPub_Templet_1VO("SCORE_USER_LCJ_Q02");//子模板
		Pub_Templet_1_ItemVO[] itemVO = templet_1VO.getItemVos();
		HashVO[] userhashVO = commDMO.getHashVoArrayByDS(null, "select * from v_score_user where id ='" + id + "' and userid = '" + userid + "'");
		if(!"WORD".equalsIgnoreCase(viewtype)){
			return;
		}
		HashVO[] hashVO = commDMO.getHashVoArrayByDS(null, "select ATTACHFILE from SCORE_TEMPLET where TEMPLETTYPE ='惩罚通知书'");//取得认定通知单模板
		if (hashVO != null && hashVO.length > 0) {
			cmpfilename = hashVO[0].getStringValue("ATTACHFILE");
			if (cmpfilename == null || cmpfilename.trim().equals("")) {//这里需要判断一下，否则会报错，找不到惩罚通知书就不发了，流程继续【李春娟/2013-09-24】
				return;
			} else if (cmpfilename.contains(";")) {
				cmpfilename = cmpfilename.substring(0, cmpfilename.indexOf(";"));//当有多个模板时，默认取第一个模板
			}
		}
		String username = null;
		try {
			username = commDMO.getStringValueByDS(null, "select name from pub_user where id = '" + userid + "'");//取得违规人员姓名
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		HashMap textmap = new HashMap();//要替换的文本map,如key="$发布日期$",value="2011-05-24"
		for (int j = 0; j < itemVO.length; j++) {
			if (!itemVO[j].getItemname().equals("违规人")) {
				textmap.put("$" + itemVO[j].getItemname() + "$", userhashVO[0].getStringValue(itemVO[j].getItemkey()));
			} else {
				textmap.put("$" + itemVO[j].getItemname() + "$", username);
			}
		}
		String reffilepath = "";//为了记录相关人员的认定通知单的文件名
		reffilepath = copyFile(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/upload" + cmpfilename, userid, id);
		wordutil.replaceScoreFile(reffilepath, textmap);//替换文本
		reffilepath = reffilepath.substring(reffilepath.lastIndexOf("/"), reffilepath.length());

		UpdateSQLBuilder updateSQLBuilder = new UpdateSQLBuilder("SCORE_USER");//将改文件的地址传给相关记录的相应字段
		updateSQLBuilder.putFieldValue("punishfilepath", "/punish" + reffilepath);
		updateSQLBuilder.setWhereCondition(" userid = '" + userid + "' and id ='" + id + "'");
		String sql = updateSQLBuilder.getSQL();
		commDMO.executeUpdateByDS(null, sql);
	}

	/**
	 * 复制文件逻辑处理
	 * */
	private String copyFile(String _oldFilePath, String _newFilename, String id) throws Exception {
		String str_bscodecache = ServerEnvironment.getProperty("WLTUPLOADFILEDIR");
		String wfpath = str_bscodecache + "/upload/punish";//创建服务器端文件存放地址
		File wffile = new File(wfpath);
		if (!wffile.exists()) {//如果没有该文件夹，则创建之
			wffile.mkdirs();
		}
		File file = new File(_oldFilePath);
		InputStream input = new FileInputStream(file);
		byte[] by = tbUtil.readFromInputStreamToBytes(input);
		_newFilename = "N" + commDMO.getSequenceNextValByDS(null, "S_PUB_FILEUPLOAD") + "_" + id + "_" + _newFilename;//新的文件名
		String newFilePath = wffile + "/" + _newFilename + ".doc";
		FileOutputStream output = new FileOutputStream(newFilePath);
		output.write(by);
		input.close();
		output.close();
		return newFilePath;
	}

	/**
	 * 新的违规积分直接生效功能【李春娟/2014-11-04】
	 * @param _id 积分登记主表（SCORE_REGISTER）主键
	 * @throws Exception
	 */
	public void effectScoreByRegisterId(String _id) throws Exception {
		effectScoreBySqlCondition(" registerid=" + _id);
		//设置违规积分登记主表的认定状态和认定日期
		ArrayList sqllist = new ArrayList();
		sqllist.add("update score_register set state='已认定',publishdate='" + tbUtil.getCurrDate() + "' where id=" + _id);
		commDMO.executeBatchByDS(null, sqllist);
	}

	/**
	 * 新的违规积分直接生效功能【李春娟/2014-11-04】
	 * @param _id 积分登记主表（SCORE_REGISTER）主键
	 * @throws Exception
	 */
	public void effectScoreBySqlCondition(String _sqlCon) throws Exception {
		String currdate = tbUtil.getCurrDate();
		String _sql = "select id,userid,score from v_score_user where " + _sqlCon + " order by publishdate,id";//未复议状态，并且未生效的，并且生效日期小于或等于当前日期的【李春娟/2013-06-03】
		HashVO[] vos = commDMO.getHashVoArrayByDS(null, _sql);
		String[][] punishs = commDMO.getStringArrayByDS(null, "Select score,punish from SCORE_PUNISH where score is not null order by score");
		for (HashVO vo : vos) {
			//下面更新总违规积分、总减免积分、总积分 逻辑在类 com.pushworld.ipushgrc.bs.score.p060.ReApplyWFBSIntercept 中也用到，如果改动，请一并修改【李春娟/2013-05-17】
			String str_wgscore = commDMO.getStringValueByDS(null, "select sum(finalscore) from v_score_user where userid = '" + vo.getStringValue("userid") + "' and EFFECTDATE like '" + currdate.substring(0, 4) + "%' and state='已生效'");
			//取得此时本年度该人员的总减免积分
			String str_jmscore = commDMO.getStringValueByDS(null, "select sum(REALSCORE) from score_reduce where userid = '" + vo.getStringValue("userid") + "' and EXAMINEDATE like '" + currdate.substring(0, 4) + "%'");
			String str_score = vo.getStringValue("score");//本条积分的应计分值
			float wgscore = 0;//当前总违规积分
			if (str_wgscore != null && !str_wgscore.equals("")) {
				wgscore = Float.parseFloat(str_wgscore);
			}
			float score = 0;
			if (str_score != null && !str_score.equals("")) {
				score = Float.parseFloat(str_score);
			}
			wgscore += score;//这里增加的应计分值，积分审核结束是用的审核分值 rescore

			float jmscore = 0;//当前总减免积分
			if (str_jmscore != null && !str_jmscore.equals("")) {
				jmscore = Float.parseFloat(str_jmscore);
			}
			float totalscore = wgscore - jmscore;

			String punishtype = "";
			if (punishs != null && punishs.length > 0) {
				float minscore = Float.parseFloat(punishs[0][0]);
				if (totalscore >= minscore) {
					for (int i = 0; i < punishs.length; i++) {
						if (i < punishs.length - 1) {
							float smallscore = Float.parseFloat(punishs[i][0]);
							float bigscore = Float.parseFloat(punishs[i + 1][0]);
							if (totalscore >= smallscore && totalscore < bigscore) {
								punishtype = punishs[i][1];
								break;
							}
						} else {
							punishtype = punishs[i][1];
						}
					}
				}
			}
			StringBuffer sb_sql = new StringBuffer("update score_user set wgscore=");
			sb_sql.append(wgscore);
			sb_sql.append(",jmscore=");
			sb_sql.append(jmscore);
			sb_sql.append(",totalscore=");//本条的积分也要算在总违规积分中
			sb_sql.append(totalscore);
			if (punishtype != null && !punishtype.trim().equals("")) {
				sb_sql.append(",punishtype='");
				sb_sql.append(punishtype);
				sb_sql.append("' ");
			}
			sb_sql.append(",finalscore=score,finalmoney=money ,effectdate='");//这里还是需要重新设置一下生效日期，有可能有几天服务器未开启，故有些积分有可能超过了预定的生效日期【李春娟/2013-06-03】
			sb_sql.append(currdate);
			sb_sql.append("',state='已生效' where id=");
			sb_sql.append(vo.getStringValue("id"));
			commDMO.executeUpdateByDS(null, sb_sql.toString());//这里每次都需要执行一下，否则如果一天内有多次积分，多次积分的总违规积分，总积分 就都一样了【李春娟/2013-05-17】
			if (punishtype != null && !punishtype.trim().equals("")) {
				new ScoreBSUtil().createPunishword(vo.getStringValue("id"), vo.getStringValue("userid"));
			}
		}
	}

	//
	public HashMap publishEffectScore(HashMap hashmap) throws Exception {
		HashMap rt = new LinkedHashMap();
		ReportExportWord word = new ReportExportWord();
		Iterator it = hashmap.entrySet().iterator();
		String str_beginDir = ServerEnvironment.getProperty("WLTUPLOADFILEDIR"); //
		str_beginDir = str_beginDir + "/" + WLTConstants.UPLOAD_DIRECTORY; //默认是上传到参数指定的目录!
		List sqllist = new ArrayList();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			String id = (String) entry.getKey();
			BillCellVO cellvo = (BillCellVO) entry.getValue();
			String fliename = "N" + commDMO.getSequenceNextValByDS(null, "S_PUB_FILEUPLOAD") + "_" + id;
			word.exportWordFile(cellvo, str_beginDir + "/score/", fliename);
			UpdateSQLBuilder up = new UpdateSQLBuilder("SCORE_USER");
			up.setWhereCondition(" id = " + id);
			up.putFieldValue("publishfilepath", "/score/" + fliename + ".doc");
			sqllist.add(up);
		}
		commDMO.executeBatchByDS(null, sqllist);
		return rt;
	}
}
