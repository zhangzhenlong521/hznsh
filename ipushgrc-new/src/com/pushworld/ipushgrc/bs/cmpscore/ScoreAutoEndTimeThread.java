package com.pushworld.ipushgrc.bs.cmpscore;

import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
/**
 *  �絽��δ���и������룬���Զ���Ϊ�Ѹ���
 */
public class ScoreAutoEndTimeThread  implements WLTJobIFC{

	CommDMO commdmo = new CommDMO();
	public String run() throws Exception {	
		String date =new TBUtil().getCurrDate();
		//����֪ͨ״̬�µġ����ҽ�ֹʱ���Ѿ��ȵ�ǰʱ��С�� Υ�����֪ͨ��״̬�Զ��޸�Ϊ�Ѹ��顣
		// ����״̬�������ö��۷ֵ�ֵ��ΪӦ�۷�
		
			List list_sql = new ArrayList();
			String _sql = "select id,totalscore from cmp_score_record where sendstate = '2' and applyendtime <'" + date + "'" ;
			HashVO[] vos = commdmo.getHashVoArrayByDS(null, _sql);
			for( HashVO vo : vos ){
				//1.���ȼ����ܷ�
				if("".equals(vo.getStringValue("totalscore")) || vo.getStringValue("totalscore") == null){  //���֮ǰû�й��۷�
					list_sql.add("update cmp_score_record set totalscore = scorelost where id = '"+vo.getStringValue("id")+"'");
				}else{
					list_sql.add("update cmp_score_record set totalscore = totalscore + scorelost where id = '"+vo.getStringValue("id")+"'");
				}
				//2.����״̬
				list_sql.add("update cmp_score_record set sendstate='4' ,resultscore = scorelost where id = '"+vo.getStringValue("id")+"'");
			}
			commdmo.executeBatchByDS(null, list_sql);
		
		return "���븴�鵽�ڣ��Զ�����Ϊ�Ѹ��飡";
	}

}