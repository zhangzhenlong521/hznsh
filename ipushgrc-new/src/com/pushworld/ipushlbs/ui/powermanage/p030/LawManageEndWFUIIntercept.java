package com.pushworld.ipushlbs.ui.powermanage.p030;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowUIIntercept2;

/**
 * ��Ȩ�������̽���ʱִ��
 * 
 * @author yinliang
 * @since 2011.12.14
 * 
 */
public class LawManageEndWFUIIntercept implements WorkFlowUIIntercept2 {

	public void afterWorkFlowEnd(WorkFlowProcessPanel panel, BillVO billvo) throws Exception {
		// ͨ����ǰ��Ϣ������ID
		// ȡ�õ�ǰ���̵�����״̬�Լ��Ƿ��������������������һ����status�϶���end�ˡ�����endtypeҲ������Ϊ�գ��϶�ֻ�����������ͷ�����������������ˡ�����
		String sql_flow = "select endtype from pub_wf_prinstance where Id = " + billvo.getStringValue("wfprinstanceid");
		String flow_endtype = UIUtil.getStringValueByDS(null, sql_flow);
		String sql_flowstate = "";
		if (flow_endtype.equals("��������")) // ������������
			sql_flowstate = "update " + billvo.getSaveTableName() + " set flowstate = '����ͨ��' where id = " + billvo.getStringValue("id");
		else
			// δ��������
			sql_flowstate = "update " + billvo.getSaveTableName() + " set flowstate = '����δͨ��' where id = " + billvo.getStringValue("id");
		UIUtil.executeBatchByDS(null, new String[] { sql_flowstate });
	}
}
