package cn.com.infostrategy.ui.workflow;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * ������UI�˵Ĺ�����!
 * @author Administrator
 *
 */
public class WorkFlowUITool {

	private HashVO hvoCurrActivityInfo = null; //��ǰ������Ϣ����dealpool$��������һ��������Ϣ������ 

	private WorkFlowUITool() {
	}

	public String getPrinstanceId() {
		return hvoCurrActivityInfo.getStringValue("prinstance$id"); //
	}

	/**
	 * ���������еõ���ǰ�����뵱ǰ�������Ϣ
	 * @param _hvoCurrActivityInfo
	 */
	public WorkFlowUITool(HashVO _hvoCurrActivityInfo) {
		hvoCurrActivityInfo = _hvoCurrActivityInfo; //
	}

	public boolean isSelfCycleInnerThisTask() {
		return hvoCurrActivityInfo.getBooleanValue("dealpool$isselfcycleclick", false); //
	}

	/**
	 * ȡ�õ�ǰ�����������ѭ��ʱ,��Ӧ��С����ͼ�еĽ�ɫ���ƣ�����ʵҲ����ͼ�еĻ������ƣ�����
	 * @return
	 */
	public String getCurrSelfCycleBindRoleCode() {
		return hvoCurrActivityInfo.getStringValue("dealpool$selfcycle_torolecode"); //
	}

	/**
	 * ȡ�õ�ǰ�����������ѭ��ʱ,��Ӧ��С����ͼ�еĽ�ɫ���ƣ�����ʵҲ����ͼ�еĻ������ƣ�����
	 * @return
	 */
	public String getCurrSelfCycleBindRoleName() {
		String str_name = hvoCurrActivityInfo.getStringValue("dealpool$selfcycle_torolename"); //
		if (str_name == null) {
			return null;
		}
		str_name = trimAndReplace(str_name); //
		return str_name; //
	}

	/**
	 * ��ǰ������С����ͼ���Ǵ��ĸ�"�ڻ���"����!!!
	 * @return
	 */
	public String getCurrSelfCycleBindFromRoleCode() {
		return hvoCurrActivityInfo.getStringValue("dealpool$selfcycle_fromrolecode"); //
	}

	/**
	 * ��ǰ������С����ͼ���Ǵ��ĸ�"�ڻ���"����!!!
	 * @return
	 */
	public String getCurrSelfCycleBindFromRoleName() {
		String str_name = hvoCurrActivityInfo.getStringValue("dealpool$selfcycle_fromrolename"); //
		if (str_name == null) {
			return null;
		}
		str_name = trimAndReplace(str_name); //
		return str_name; //
	}

	//�������ϵĻ��з���ȥ��!
	private String trimAndReplace(String _str) {
		_str = _str.trim(); //
		_str = TBUtil.getTBUtil().replaceAll(_str, "\r", ""); //
		_str = TBUtil.getTBUtil().replaceAll(_str, "\n", ""); //
		_str = TBUtil.getTBUtil().replaceAll(_str, "��", "("); //
		_str = TBUtil.getTBUtil().replaceAll(_str, "��", ")"); //
		_str = TBUtil.getTBUtil().replaceAll(_str, " ", ""); //
		return _str;
	}

	/**
	 * ��ǰ���ڱ���!!!
	 * @return
	 */
	public String getCurrActivityCode() {
		return hvoCurrActivityInfo.getStringValue("code"); //
	}

	/**
	 * ��ǰ��������!!
	 * @return
	 */
	public String getCurrActivityName() {
		return hvoCurrActivityInfo.getStringValue("wfname"); //
	}

	/**
	 * ȡ�õ�ǰ�����Ǵ��ĸ����߹����ģ�����
	 * @return
	 */
	public String getCurrTaskFromTransitionCode() {
		return null; //
	}

	/**
	 * ȡ�õ�ǰ�����Ǵ��ĸ����ڹ����ģ�����
	 * @return
	 */
	public String getCurrTaskFromActivityCode() {
		return null; //
	}

	/**
	 * ȡ�õ�ǰ�����Ǵ��ĸ����ڹ����ģ�����
	 * @return
	 */
	public String getCurrTaskFromActivityName() {
		return null; //
	}

	/**
	 * �����ĳ�����߹��߹����Σ�����
	 * @param _transitionCode
	 * @return
	 * @throws Exception
	 */
	public int getOneTransitionThrowCount(String _transitionCode) throws Exception {
		String str_sql = "select count(*) c1 from pub_wf_dealpool t1,pub_wf_transition t2 where t1.transition=t2.id and t1.prinstanceid='" + getPrinstanceId() + "' and t2.code='" + _transitionCode + "'"; //
		String str_count = UIUtil.getStringValueByDS(null, str_sql); //
		int li_count = Integer.parseInt(str_count); //
		return li_count; //
	}

}
