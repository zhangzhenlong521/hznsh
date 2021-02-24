package com.pushworld.ipushlbs.ui.constactsign.p010;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowUIIntercept2;

/**
 * �����̽��������endtype��ֵ
 * @author p17
 *
 */
public class FormatDealWFIncp implements WorkFlowUIIntercept2{

	public void afterWorkFlowEnd(WorkFlowProcessPanel panel, BillVO billvo)
			throws Exception {

		String prinstanceId=panel.getPrinstanceId();
		String getEndType="select ENDTYPE from pub_wf_prinstance where id= "+prinstanceId;
		String endType=UIUtil.getStringValueByDS(null, getEndType);//�õ�����ʵ���Ľ���״̬
		
		String updateSql="update "+billvo.getSaveTableName()+" set endtype = '"+endType +"' where id = "+billvo.getStringValue("id");
		UIUtil.executeUpdateByDS(null, updateSql);//������ʵ����Ľ���״̬����洢��
		
//		try{
//			String sql="update "+billvo.getSaveTableName()+" set print = '��' where id = "+billvo.getStringValue("id");
//			UIUtil.executeUpdateByDS(null, sql);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
	}

}