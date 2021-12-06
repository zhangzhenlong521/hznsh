package com.pushworld.ipushgrc.bs.score.p060;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.workflow.WorkFlowEngineBSIntercept;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTHashMap;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

import com.pushworld.ipushgrc.bs.score.ScoreBSUtil;

/**
 * Υ����ּ�������͸�������BS�����������������/2013-05-13��
 * ��������Ӧ�Ļ���UI������������com.pushworld.ipushgrc.ui.score.p060.ReApplyWFUIIntercept
 * @author lcj
 *
 */
public class ReApplyWFBSIntercept extends WorkFlowEngineBSIntercept {
	/**
	 * ĳ��һ��ִ�к�Ĵ���,ֻ��ע���ڻ�����,�÷����Żᱻ���ã���
	 * @param _billType
	 * @param _busiType
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	public void afterActivityAction(String _billType, String _busiType, WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype, WLTHashMap _parMap) throws Exception {
		if (_busiType.contains("����")) {//����Ǽ�������
			if ("δ���".equals(_billVO.getStringValue("state"))) {
				String date = TBUtil.getTBUtil().getCurrDate();
				new CommDMO().executeUpdateByDS(null, "update score_reduce set state = '�����',applydate='" + date + "' where id = " + _billVO.getStringValue("id"));
			}
		} else if (_busiType.contains("����")) {//����Ǹ�������
			if ("δ����".equals(_billVO.getStringValue("state"))) {
				new CommDMO().executeUpdateByDS(null, "update score_user set state='������' where id=" + _billVO.getStringValue("id"));
			}
		}
	}

	/**
	 * �������̽�����ִ�е��߼�,ֻ��ע���������и÷����Żᱻ���ã���
	 * @param _billType
	 * @param _busiType
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	public void afterWorkFlowEnd(String _billType, String _busiType, WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype, WLTHashMap _parMap) throws Exception {
		String loginUserId = new WLTInitContext().getCurrSession().getLoginUserId();
		CommDMO commDMO = new CommDMO();
		String loginUserDeptId = commDMO.getStringValueByDS(null, "select userdept from pub_user_post where userid='" + loginUserId + "' and isdefault='Y'"); //
		String currdate = new TBUtil().getCurrDate();
		if (_busiType.contains("����")) {//����Ǽ�������
			commDMO.executeUpdateByDS(null, "update score_reduce set state='�����',examineuser=" + loginUserId + ",examinedept=" + loginUserDeptId + ", examinedate='" + currdate + "' where id=" + _billVO.getStringValue("id"));
		} else if (_busiType.contains("����")) {//����Ǹ�������
			//���������Υ����֡��ܼ�����֡��ܻ��� �߼����� com.pushworld.ipushgrc.bs.score.ScoreBSUtil.effectScoreBySqlContion() ��Ҳ�õ�������Ķ�����һ���޸ġ����/2013-05-17��
			String str_wgscore = commDMO.getStringValueByDS(null, "select sum(finalscore) from v_score_user where userid = '" + _billVO.getStringValue("USERID") + "' and EFFECTDATE like '" + currdate.substring(0, 4) + "%' and state='����Ч'");
			String str_jmscore = commDMO.getStringValueByDS(null, "select sum(REALSCORE) from score_reduce where userid = '" + _billVO.getStringValue("USERID") + "' and EXAMINEDATE like '" + currdate.substring(0, 4) + "%'");
			String str_rescore = _billVO.getStringValue("rescore");//�������ֵ���˷�ֵ
			float wgscore = 0;
			if (str_wgscore != null && !str_wgscore.equals("")) {
				wgscore = Float.parseFloat(str_wgscore);
			}
			float rescore = 0;
			if (str_rescore != null && !str_rescore.equals("")) {
				rescore = Float.parseFloat(str_rescore);
			}
			wgscore += rescore;//�������ӵĸ����ֵ�������Զ���Чʱ���õ�Ӧ�Ʒ�ֵ score
			float jmscore = 0;
			if (str_jmscore != null && !str_jmscore.equals("")) {
				jmscore = Float.parseFloat(str_jmscore);
			}
			float totalscore = wgscore - jmscore;
			String[][] punishs = commDMO.getStringArrayByDS(null, "Select score,punish from SCORE_PUNISH where score is not null order by score");
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
			sb_sql.append(",totalscore=");//�����ĸ������ҲҪ������Υ�������
			sb_sql.append(totalscore);
			if (punishtype != null && !punishtype.trim().equals("")) {
				sb_sql.append(",punishtype='");
				sb_sql.append(punishtype);
				sb_sql.append("' ");
			}
			sb_sql.append(",finalscore=rescore,finalmoney=remoney,effectdate='");
			sb_sql.append(currdate);
			sb_sql.append("',state='����Ч',reuserid=");
			sb_sql.append(loginUserId);
			sb_sql.append(",redeptid=");
			sb_sql.append(loginUserDeptId);
			sb_sql.append(",redate='");
			sb_sql.append(currdate);
			sb_sql.append("' where id=");
			sb_sql.append(_billVO.getStringValue("id"));
			commDMO.executeUpdateByDS(null, sb_sql.toString());
			if (punishtype != null && !punishtype.trim().equals("")) {
				new ScoreBSUtil().createPunishword(_billVO.getStringValue("id"), _billVO.getStringValue("USERID"));
			}
		}
	}
}
