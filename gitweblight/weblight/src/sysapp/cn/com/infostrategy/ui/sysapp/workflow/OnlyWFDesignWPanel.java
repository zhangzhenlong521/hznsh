package cn.com.infostrategy.ui.sysapp.workflow;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.workflow.design.WorkFlowDesignWPanel;

/**
 * ��ʱ�ͻ�Ҫ��ֻ�鿴��������ά������,����������ͼ����ʾ!!
 * @author xch
 *
 */
public class OnlyWFDesignWPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		WorkFlowDesignWPanel wfPanel = new WorkFlowDesignWPanel() {
			@Override
			public String getWFProcessCondition() {
				return "wftype is null or wftype='������'"; //ֻ�ܲ鿴��������,����ϵ�ļ�ʲô�Ķ����ܴ���
			}

		}; //
		this.add(wfPanel); //
	}

}
