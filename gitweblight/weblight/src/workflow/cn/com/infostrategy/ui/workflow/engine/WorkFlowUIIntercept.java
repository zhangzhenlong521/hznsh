package cn.com.infostrategy.ui.workflow.engine;

//工作流UI端的拦截器!!
public interface WorkFlowUIIntercept {

	/**
	 * 在打开工作流处理界面后可以进行的逻辑处理
	 * 通过WorkFlowProcessPanel对象的getBillCardPanel(),getHistoryBillListPanel()可以得到对应的卡片面板!然后再对卡片面板上的相关控件进行逻辑处理!
	 * 比如：设置哪些控件显示,哪些控件不可编辑等!
	 */
	public void afterOpenWFProcessPanel(cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel _processPanel) throws Exception;
}
