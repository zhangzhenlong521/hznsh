package cn.com.infostrategy.ui.workflow.engine;

//������UI�˵�������!!
public interface WorkFlowUIIntercept {

	/**
	 * �ڴ򿪹���������������Խ��е��߼�����
	 * ͨ��WorkFlowProcessPanel�����getBillCardPanel(),getHistoryBillListPanel()���Եõ���Ӧ�Ŀ�Ƭ���!Ȼ���ٶԿ�Ƭ����ϵ���ؿؼ������߼�����!
	 * ���磺������Щ�ؼ���ʾ,��Щ�ؼ����ɱ༭��!
	 */
	public void afterOpenWFProcessPanel(cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel _processPanel) throws Exception;
}
