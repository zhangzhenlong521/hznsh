package cn.com.infostrategy.ui.mdata.styletemplet.t20;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;

/**
 * 工作流的风格模板! 该模板的最大特点是可以配置"草稿箱","待办箱","已办箱"的显示与否！
 * 想把工作流做成一个非常易用的通用模板,以后配置一个单据模板编码就可以了。
 * 要成功开发工作流,有以下几项严格约定，必须遵守：
 * 1.表中必须有以下4个字段：create_userid   decimal,billtype  varchar(30),busitype  varchar(30),wfprinstanceid   decimal,
 * 2.模板必须设置列表查询框显示,否则页面会报错
 * 3.
 * @author lcj
 */
public abstract class AbstractStyleWorkPanel_20 extends AbstractStyleWorkPanel {

	private Pub_Templet_1VO pubTempletVO = null; //模板定义VO
	private BillListPanel billList_1, billList_2, billList_3; //三外列表
	private boolean isBillList_1Visable, isBillList_2Visable, isBillList_3Visable; //设置三个列表是否显示

	public AbstractStyleWorkPanel_20() {

	}

	public void initialize() {
		super.initialize(); //
		try {
			this.setLayout(new BorderLayout());
			pubTempletVO = UIUtil.getPub_Templet_1VO(getBillTempletCode()); //为了提高性能只需要一个模板定义VO就可以了!!!
			pubTempletVO.setIsshowlistquickquery(Boolean.TRUE); //显示快速查询

			String str_userid = ClientEnvironment.getInstance().getLoginUserID(); //登录人员主键
			String str_tablename = pubTempletVO.getSavedtablename(); //保存的表名...

			JTabbedPane pane = new JTabbedPane(JTabbedPane.LEFT); //
			if (getBillList_1Visable()) {
				billList_1 = new BillListPanel(pubTempletVO); //
				billList_1.addWorkFlowDealPanel(); //增加工作流面板
				billList_1.getWorkflowDealBtnPanel().hiddenAllBtns();
				billList_1.getWorkflowDealBtnPanel().setInsertBtnVisiable(true);
				billList_1.getWorkflowDealBtnPanel().setUpdateBtnVisiable(true);
				billList_1.getWorkflowDealBtnPanel().setDeleteBtnVisiable(true);
				billList_1.getWorkflowDealBtnPanel().setProcessBtnVisiable(true);
				billList_1.getWorkflowDealBtnPanel().setProcessBtnVisiable(true);
				//billList_1.QueryDataByCondition(" id<100");
				billList_1.setDataFilterCustCondition("create_userid = '" + str_userid + "' and wfprinstanceid is null"); //制单人等于我,且没有启动流程的!
				pane.addTab(getTabTitle_1(), billList_1); //
			}
			if (getBillList_2Visable()) {
				billList_2 = new BillListPanel(pubTempletVO); //
				billList_2.addWorkFlowDealPanel(); //
				billList_2.getWorkflowDealBtnPanel().hiddenAllBtns();
				if (new TBUtil().getSysOptionBooleanValue("工作流处理按钮的面板中是否显示接收按钮", true)) {
					billList_2.getWorkflowDealBtnPanel().setReceiveBtnVisiable(true);
				}
				billList_2.getWorkflowDealBtnPanel().setProcessBtnVisiable(true);
				billList_2.getWorkflowDealBtnPanel().setMonitorBtnVisiable(true);
				billList_2.getWorkflowDealBtnPanel().setExportBtnVisiable(true); //
				//billList_2.getWorkflowDealBtnPanel().setDeleteBtnVisiable(true);
				billList_2.setDataFilterCustCondition("wfprinstanceid in (select t1.id from pub_wf_prinstance t1,pub_wf_dealpool t2 where t1.id=t2.prinstanceid and t1.billtablename='" + str_tablename + "' and t2.participant_user='" + ClientEnvironment.getInstance().getLoginUserID()
						+ "' and t2.issubmit='N' and t2.isprocess='N')"); //待办箱,已提交但还未被处理
				//billList_2.setAllBillListBtnVisiable(true); //
				//billList_2.QueryDataByCondition(null);	//待办箱数据自动加载！！  因为模板中有权限过滤，也得加上
				pane.addTab(getTabTitle_2(), billList_2); //
			}
			if (getBillList_3Visable()) {
				billList_3 = new BillListPanel(pubTempletVO); //
				billList_3.addWorkFlowDealPanel(); //
				billList_3.getWorkflowDealBtnPanel().hiddenAllBtns();
				billList_3.getWorkflowDealBtnPanel().setCancelBtnVisiable(true);
				billList_3.getWorkflowDealBtnPanel().setMonitorBtnVisiable(true);
				billList_3.getWorkflowDealBtnPanel().setExportBtnVisiable(true); //
				billList_3.setDataFilterCustCondition("wfprinstanceid in (select id from pub_wf_prinstance where billtablename='" + str_tablename + "' and submiterhist like '%;" + ClientEnvironment.getInstance().getLoginUserID() + ";%')"); //已办箱
				billList_3.setAllBillListBtnVisiable(false); //		
				pane.addTab(getTabTitle_3(), billList_3); //
			}
			this.add(pane, BorderLayout.CENTER);
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private String getTabTitle_1() {
		return "<html><table><tr><td>&nbsp;<b><font color=#65B1AB size=4>草&nbsp;稿&nbsp;箱</font></b>&nbsp;</td></tr></table></html>"; //
	}

	private String getTabTitle_2() {
		return "<html><table><tr><td>&nbsp;<b><font color=#65B1AB size=4>待&nbsp;办&nbsp;箱</font></b>&nbsp;</td></tr></table></html>"; //
	}

	private String getTabTitle_3() {
		return "<html><table><tr><td>&nbsp;<b><font color=#65B1AB size=4>已&nbsp;办&nbsp;箱</font></b>&nbsp;</td></tr></table></html>"; //
	}

	//定义单据模板编码
	public abstract String getBillTempletCode(); //

	//配置"草稿箱","待办箱","已办箱"的显示与否！
	public abstract boolean getBillList_1Visable();

	public abstract boolean getBillList_2Visable();

	public abstract boolean getBillList_3Visable();
}