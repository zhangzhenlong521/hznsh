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
 * Υ�����ģ��ͻ��˹��ߡ����/2013-05-17��
 * @author lcj
 *
 */
public class ScoreBSUtil {
	private TBUtil tbUtil = new TBUtil();
	private ScoreWordTBUtil wordutil = new ScoreWordTBUtil();//word�ϲ����滻����
	private CommDMO commDMO = new CommDMO();
	private String viewtype = TBUtil.getTBUtil().getSysOptionStringValue("Υ�����֪ͨ���鿴��ʽ", "WORD");
	public void createPunishword(String id, String userid) throws Exception {
		MetaDataDMO dataDMO = new MetaDataDMO();
		String cmpfilename = "";//�ļ���
		Pub_Templet_1VO templet_1VO = dataDMO.getPub_Templet_1VO("SCORE_USER_LCJ_Q02");//��ģ��
		Pub_Templet_1_ItemVO[] itemVO = templet_1VO.getItemVos();
		HashVO[] userhashVO = commDMO.getHashVoArrayByDS(null, "select * from v_score_user where id ='" + id + "' and userid = '" + userid + "'");
		if(!"WORD".equalsIgnoreCase(viewtype)){
			return;
		}
		HashVO[] hashVO = commDMO.getHashVoArrayByDS(null, "select ATTACHFILE from SCORE_TEMPLET where TEMPLETTYPE ='�ͷ�֪ͨ��'");//ȡ���϶�֪ͨ��ģ��
		if (hashVO != null && hashVO.length > 0) {
			cmpfilename = hashVO[0].getStringValue("ATTACHFILE");
			if (cmpfilename == null || cmpfilename.trim().equals("")) {//������Ҫ�ж�һ�£�����ᱨ���Ҳ����ͷ�֪ͨ��Ͳ����ˣ����̼��������/2013-09-24��
				return;
			} else if (cmpfilename.contains(";")) {
				cmpfilename = cmpfilename.substring(0, cmpfilename.indexOf(";"));//���ж��ģ��ʱ��Ĭ��ȡ��һ��ģ��
			}
		}
		String username = null;
		try {
			username = commDMO.getStringValueByDS(null, "select name from pub_user where id = '" + userid + "'");//ȡ��Υ����Ա����
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		HashMap textmap = new HashMap();//Ҫ�滻���ı�map,��key="$��������$",value="2011-05-24"
		for (int j = 0; j < itemVO.length; j++) {
			if (!itemVO[j].getItemname().equals("Υ����")) {
				textmap.put("$" + itemVO[j].getItemname() + "$", userhashVO[0].getStringValue(itemVO[j].getItemkey()));
			} else {
				textmap.put("$" + itemVO[j].getItemname() + "$", username);
			}
		}
		String reffilepath = "";//Ϊ�˼�¼�����Ա���϶�֪ͨ�����ļ���
		reffilepath = copyFile(ServerEnvironment.getProperty("WLTUPLOADFILEDIR") + "/upload" + cmpfilename, userid, id);
		wordutil.replaceScoreFile(reffilepath, textmap);//�滻�ı�
		reffilepath = reffilepath.substring(reffilepath.lastIndexOf("/"), reffilepath.length());

		UpdateSQLBuilder updateSQLBuilder = new UpdateSQLBuilder("SCORE_USER");//�����ļ��ĵ�ַ������ؼ�¼����Ӧ�ֶ�
		updateSQLBuilder.putFieldValue("punishfilepath", "/punish" + reffilepath);
		updateSQLBuilder.setWhereCondition(" userid = '" + userid + "' and id ='" + id + "'");
		String sql = updateSQLBuilder.getSQL();
		commDMO.executeUpdateByDS(null, sql);
	}

	/**
	 * �����ļ��߼�����
	 * */
	private String copyFile(String _oldFilePath, String _newFilename, String id) throws Exception {
		String str_bscodecache = ServerEnvironment.getProperty("WLTUPLOADFILEDIR");
		String wfpath = str_bscodecache + "/upload/punish";//�������������ļ���ŵ�ַ
		File wffile = new File(wfpath);
		if (!wffile.exists()) {//���û�и��ļ��У��򴴽�֮
			wffile.mkdirs();
		}
		File file = new File(_oldFilePath);
		InputStream input = new FileInputStream(file);
		byte[] by = tbUtil.readFromInputStreamToBytes(input);
		_newFilename = "N" + commDMO.getSequenceNextValByDS(null, "S_PUB_FILEUPLOAD") + "_" + id + "_" + _newFilename;//�µ��ļ���
		String newFilePath = wffile + "/" + _newFilename + ".doc";
		FileOutputStream output = new FileOutputStream(newFilePath);
		output.write(by);
		input.close();
		output.close();
		return newFilePath;
	}

	/**
	 * �µ�Υ�����ֱ����Ч���ܡ����/2014-11-04��
	 * @param _id ���ֵǼ�����SCORE_REGISTER������
	 * @throws Exception
	 */
	public void effectScoreByRegisterId(String _id) throws Exception {
		effectScoreBySqlCondition(" registerid=" + _id);
		//����Υ����ֵǼ�������϶�״̬���϶�����
		ArrayList sqllist = new ArrayList();
		sqllist.add("update score_register set state='���϶�',publishdate='" + tbUtil.getCurrDate() + "' where id=" + _id);
		commDMO.executeBatchByDS(null, sqllist);
	}

	/**
	 * �µ�Υ�����ֱ����Ч���ܡ����/2014-11-04��
	 * @param _id ���ֵǼ�����SCORE_REGISTER������
	 * @throws Exception
	 */
	public void effectScoreBySqlCondition(String _sqlCon) throws Exception {
		String currdate = tbUtil.getCurrDate();
		String _sql = "select id,userid,score from v_score_user where " + _sqlCon + " order by publishdate,id";//δ����״̬������δ��Ч�ģ�������Ч����С�ڻ���ڵ�ǰ���ڵġ����/2013-06-03��
		HashVO[] vos = commDMO.getHashVoArrayByDS(null, _sql);
		String[][] punishs = commDMO.getStringArrayByDS(null, "Select score,punish from SCORE_PUNISH where score is not null order by score");
		for (HashVO vo : vos) {
			//���������Υ����֡��ܼ�����֡��ܻ��� �߼����� com.pushworld.ipushgrc.bs.score.p060.ReApplyWFBSIntercept ��Ҳ�õ�������Ķ�����һ���޸ġ����/2013-05-17��
			String str_wgscore = commDMO.getStringValueByDS(null, "select sum(finalscore) from v_score_user where userid = '" + vo.getStringValue("userid") + "' and EFFECTDATE like '" + currdate.substring(0, 4) + "%' and state='����Ч'");
			//ȡ�ô�ʱ����ȸ���Ա���ܼ������
			String str_jmscore = commDMO.getStringValueByDS(null, "select sum(REALSCORE) from score_reduce where userid = '" + vo.getStringValue("userid") + "' and EXAMINEDATE like '" + currdate.substring(0, 4) + "%'");
			String str_score = vo.getStringValue("score");//�������ֵ�Ӧ�Ʒ�ֵ
			float wgscore = 0;//��ǰ��Υ�����
			if (str_wgscore != null && !str_wgscore.equals("")) {
				wgscore = Float.parseFloat(str_wgscore);
			}
			float score = 0;
			if (str_score != null && !str_score.equals("")) {
				score = Float.parseFloat(str_score);
			}
			wgscore += score;//�������ӵ�Ӧ�Ʒ�ֵ��������˽������õ���˷�ֵ rescore

			float jmscore = 0;//��ǰ�ܼ������
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
			sb_sql.append(",totalscore=");//�����Ļ���ҲҪ������Υ�������
			sb_sql.append(totalscore);
			if (punishtype != null && !punishtype.trim().equals("")) {
				sb_sql.append(",punishtype='");
				sb_sql.append(punishtype);
				sb_sql.append("' ");
			}
			sb_sql.append(",finalscore=score,finalmoney=money ,effectdate='");//���ﻹ����Ҫ��������һ����Ч���ڣ��п����м��������δ����������Щ�����п��ܳ�����Ԥ������Ч���ڡ����/2013-06-03��
			sb_sql.append(currdate);
			sb_sql.append("',state='����Ч' where id=");
			sb_sql.append(vo.getStringValue("id"));
			commDMO.executeUpdateByDS(null, sb_sql.toString());//����ÿ�ζ���Ҫִ��һ�£��������һ�����ж�λ��֣���λ��ֵ���Υ����֣��ܻ��� �Ͷ�һ���ˡ����/2013-05-17��
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
		str_beginDir = str_beginDir + "/" + WLTConstants.UPLOAD_DIRECTORY; //Ĭ�����ϴ�������ָ����Ŀ¼!
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
