package cn.com.infostrategy.ui.workflow;

import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.sysapp.login.DeskTopNewGroupDefineVO;
import cn.com.infostrategy.ui.common.BillFrame;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;
import cn.com.infostrategy.ui.sysapp.login.IndexItemTaskPanel;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessFrame;

/**
 * 页面点击工作流任务时处理的
 * @author xch
 *
 */
public class DeskTopTaskGroupAction extends AbstractAction {
	private static final long serialVersionUID = 1L;

	private java.awt.Container clickedFromCompent = null;
	private DeskTopPanel deskTopPanel = null;//
	private DeskTopNewGroupDefineVO defineVO = null;
	private int index = -1; //

	private HashVO hvoData = null;
	long ll_1 = 0; //

	public void actionPerformed(ActionEvent e) {
		ll_1 = System.currentTimeMillis();
		try {
			TBUtil tbUtil = new TBUtil(); //
			clickedFromCompent = (java.awt.Container) e.getSource(); //
			index = (Integer) this.getValue("Index"); //
			defineVO = (DeskTopNewGroupDefineVO) this.getValue("DeskTopNewsDefineVO"); //
			deskTopPanel = (DeskTopPanel) this.getValue("DeskTopPanel"); //
			hvoData = (HashVO) this.getValue("DeskTopNewsDataVO"); //

			String str_task_id = hvoData.getStringValue("id"); //任务id
			String str_prdealpool_id = hvoData.getStringValue("prdealpoolid"); //流程任务id
			String str_prinstance_id = hvoData.getStringValue("prinstanceid"); //流程实例id
			String str_parentinstanceid = hvoData.getStringValue("parentinstanceid"); //父流程实例id
			String str_rootinstanceid = hvoData.getStringValue("rootinstanceid"); //根流程实例id

			String str_templetCode = hvoData.getStringValue("templetcode"); //
			String str_pkvalue = hvoData.getStringValue("pkvalue"); //

			WorkFlowServiceIfc wfService = (WorkFlowServiceIfc) UIUtil.lookUpRemoteService(WorkFlowServiceIfc.class); //
			HashVO hvo_judge_dealpool = wfService.judgeTaskDeal(str_prinstance_id, str_prdealpool_id, ClientEnvironment.getInstance().getLoginUserID()); //验证!!
			boolean isOnlyView = !(hvo_judge_dealpool.getBooleanValue("任务是否有效", true)); //
			String str_unEffectReason = hvo_judge_dealpool.getStringValue("原因说明"); //
			BillCardPanel cardPanel = new BillCardPanel(str_templetCode); //
			cardPanel.queryData("select *  from  " + cardPanel.getTempletVO().getTablename() + "  where " + cardPanel.getTempletVO().getPkname() + "='" + str_pkvalue + "'"); //从模板查询表里取值

			String str_sql_1 = "update pub_task_deal set islookat='Y',lookattime='" + tbUtil.getCurrTime() + "' where id='" + str_task_id + "' and islookat!='Y'"; //先置为查看过了!!
			String str_sql_2 = "update pub_wf_dealpool set isreceive='Y',receivetime='" + UIUtil.getServerCurrTime() + "' where id='" + tbUtil.getNullCondition(str_prdealpool_id) + "' and (isreceive is null or isreceive='N')"; //如果没接收,则立即表示接收
			UIUtil.executeBatchByDS(null, new String[] { str_sql_1, str_sql_2 }); //
			String str_title = "流程处理taskid=" + str_task_id + "/dealpoolid=" + str_prdealpool_id + "/prinstanceid=" + str_prinstance_id + "/parentinstanceid=" + (str_parentinstanceid == null ? "" : str_parentinstanceid) + "/rootinstanceid=" + (str_rootinstanceid == null ? "" : str_rootinstanceid);
			WorkFlowProcessFrame frame = new WorkFlowProcessFrame((Container) e.getSource(), str_title, cardPanel, null, str_task_id, str_prdealpool_id, str_prinstance_id, isOnlyView, str_unEffectReason); ////
			frame.setVisible(true); //
			frame.addWindowCloseListener(new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					afterClose((BillFrame) e.getSource()); //
				}
			});
			frame.toFront(); //
			//如果是确定关闭,则立即删除该条信息
		} catch (Exception _ex) {
			MessageBox.showException((Container) e.getSource(), _ex); //
		}
	}

	/**
	 * 关闭以后...
	 * @param _frame
	 */
	protected void afterClose(BillFrame _frame) {
		try {
			if (_frame.getCloseType() == 1) {
				long ll_2 = System.currentTimeMillis();
				SysAppServiceIfc service = (SysAppServiceIfc) RemoteServiceFactory.getInstance().lookUpService(SysAppServiceIfc.class); //定义远程服务
				ClientEnvironment clientEnv = ClientEnvironment.getInstance();
				service.addClickedMenuLog(clientEnv.getLoginUserCode(), clientEnv.getLoginUserName(), clientEnv.getCurrLoginUserVO().getBlDeptId(), clientEnv.getCurrLoginUserVO().getBlDeptName(), "$处理首页工作流任务", "" + hvoData, (ll_2 - ll_1) + "毫秒"); //增加点击菜单日志..
			}
		} catch (Exception exx) {
			exx.printStackTrace(); //
		} finally {
			refreshUI(); //一定要刷新页面!
		}
	}

	private void refreshUI() {
		if (clickedFromCompent instanceof IndexItemTaskPanel) { //刷新页面!
			((IndexItemTaskPanel) clickedFromCompent).onRefreshGroup(true); ///刷新!!!
		}
	}

}
