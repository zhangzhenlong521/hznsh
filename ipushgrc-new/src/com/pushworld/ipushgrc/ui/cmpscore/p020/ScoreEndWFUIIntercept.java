package com.pushworld.ipushgrc.ui.cmpscore.p020;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowUIIntercept2;
/**
 * Υ����ָ������̽���������
 * @author yinliang
 * @since 2011.12.26
 */
public class ScoreEndWFUIIntercept implements WorkFlowUIIntercept2{

	
	public void afterWorkFlowEnd(WorkFlowProcessPanel panel, BillVO billvo)
			throws Exception {
		//ͨ����ǰ��Ϣ������ID ȡ�õ�ǰ���̵�����״̬�Լ��Ƿ��������������������һ����status�϶���end�ˡ�����endtypeҲ������Ϊ�գ��϶�ֻ�����������ͷ�����������������ˡ�����
		String sql_flow = "select endtype from pub_wf_prinstance where Id = " + billvo.getStringValue("wfprinstanceid") ;
		String flow_endtype = UIUtil.getStringValueByDS(null, sql_flow);
		String sql_flowstate = "" ;
		String id = billvo.getStringValue("id") ;
		if(flow_endtype.equals("��������")) //������������,״̬��Ϊ�ȴ��ö�
			sql_flowstate = "update "+ billvo.getSaveTableName() +" set sendstate = '5' where id = "+id;  
		else{ //δ������������ʾû��׼��ֱ�ӽ��뵽�Ѹ�����
			sql_flowstate = "update "+ billvo.getSaveTableName() +" set sendstate = '4'," +
					"resultscore = scorelost where id = "+billvo.getStringValue("id");
			 //�����ܷ�
			String totalscore = billvo.getStringValue("totalscore");
				if("".equals(totalscore) || totalscore == null){  //���֮ǰû�й��۷�
					UIUtil.executeUpdateByDS(null,"update cmp_score_record set totalscore = scorelost where id = "+id );
				}else{
					UIUtil.executeUpdateByDS(null,"update cmp_score_record set totalscore = totalscore + scorelost where id = "+id );
				}
			}
		UIUtil.executeUpdateByDS(null,sql_flowstate );
		}
}
