package cn.com.infostrategy.ui.sysapp.bug;

import javax.swing.JLabel;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

/**
 * Bug����ͳ��! ��ҵ��Ŀ����������һ���򵥵�bug������,ʹ�ù��̻��Ǿ��úܷ����!!
 * ��Ϊ�Ժ������Ŀ�ж�������Bug���������,ʹ�õ�������Bug����ϵͳ���ǲ�������! ���Ըɴ��Լ���һ�������õ�!!! 
 * FORMATFORMULA=getMultiReport("BUG_REPORT_SHOW","cn.com.cib.bs.common.BugAnalyseReport")>
 * @author xch
 *
 */
public class PubBugStatisWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		this.add(new JLabel("PubBugStatisWKPanel")); //

	}

}
