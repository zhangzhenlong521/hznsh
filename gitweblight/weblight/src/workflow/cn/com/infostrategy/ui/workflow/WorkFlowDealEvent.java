package cn.com.infostrategy.ui.workflow;

import java.util.EventObject;

/**
 * ���̴����¼�,�������������,��Ҫ֪ͨ�������ˢ�µ�! ������ҳ����������!
 * @author Administrator
 *
 */
public class WorkFlowDealEvent extends EventObject {

	private static final long serialVersionUID = 5065941700069269833L;
	private int dealType = -1; //
	private String prInstanceId = null; //ʵ��id
	private String prDealPoolId = null; //��������id

	public WorkFlowDealEvent(Object _source, int _dealType, String _prinstanceId, String _prdealPoolId) {
		super(_source);
		this.dealType = _dealType; //
		this.prInstanceId = _prinstanceId; //����ʵ��id
		this.prDealPoolId = _prdealPoolId; //��������id
	}

	public int getDealType() {
		return dealType;
	}

	public String getPrInstanceId() {
		return prInstanceId;
	}

	public String getPrDealPoolId() {
		return prDealPoolId;
	}

}
