package com.pushworld.ipushgrc.ui.wfrisk.p020;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowUIIntercept2;

import com.pushworld.ipushgrc.ui.wfrisk.WFRiskUIUtil;

/**
 * 流程文件发布和废止的流程结束后执行的拦截器。
 * 1.发布流程要显示新版本号和是否同意两个控件，废止流程只需显示是否同意下拉框!
 * 2.如果文件状态为[发布申请中] 并且 勾选了是否同意，则直接发布文件，发布新版本号为 下拉框[newversionno]的值，
 * 如果未勾选是否同意，则将文件状态改为[编辑中]，因为只有[编辑中]的文件才可以申请发布，并提示发布失败！
 * 3.如果文件状态为[废止申请中] 并且 勾选了是否同意，则直接废止文件，
 * 如果未勾选是否同意，则将文件状态改为[有效]，因为只有[有效]的文件才可以申请废止，并提示废止失败！
 * 4.如果不同意发布或废止，则不删除流程记录，以供编写人按照流程的处理意见来修改流程，在重新点流程处理时
 * @author lcj
 *
 */
public class CmpFileAbolishEndWFUIInterceptor implements WorkFlowUIIntercept2 {
	private WorkFlowProcessPanel wfpanel;
	private BillVO billVO;

	public void afterWorkFlowEnd(WorkFlowProcessPanel panel, BillVO billvo) throws Exception {
		this.wfpanel = panel;
		this.billVO = billvo;
		try {
			String filestate = billVO.getStringValue("filestate");//1- 编辑中, 2- 发布申请中, 3- 有效, 4- 废止申请中, 5- 失效
			if ("2".equals(filestate)) {
				onPublishFile();
			} else {
				onAbolisthFile();
			}
		} catch (Exception e1) {
			MessageBox.showException(wfpanel, e1);
		}
	}

	/**
	 * 废止文件!
	 * @throws Exception
	 */
	private void onAbolisthFile() throws Exception {
		final String cmpfileid = billVO.getStringValue("id");
		String str_isapprove = billVO.getStringValue("isapprove");
		if ("Y".equals(str_isapprove)) { //如果同意废止!!
			String wfprinstanceid = billVO.getStringValue("wfprinstanceid");//实例id
			String[] sqls = new String[] { "delete from pub_wf_prinstance where rootinstanceid=" + wfprinstanceid, "delete from pub_wf_dealpool where rootinstanceid=" + wfprinstanceid, "delete from pub_task_deal where rootinstanceid=" + wfprinstanceid,
					"delete from pub_task_off where rootinstanceid=" + wfprinstanceid, "update cmp_cmpfile set wfprinstanceid=null,filestate='5' where id=" + cmpfileid, "update cmp_risk_editlog set filestate='5' where cmpfile_id =" + cmpfileid };
			UIUtil.executeBatchByDS(null, sqls);//删除流程实例、处理记录、待办记录、已办记录，并将该文件实例id设为空，改变文件状态为[废止]
			MessageBox.show(wfpanel, "废止成功!");
		} else {
			//将文件状态[废止申请中]变为[有效]，因为只有[有效]的文件才可以申请废止
			UIUtil.executeUpdateByDS(null, "update cmp_cmpfile set filestate='3' where id=" + cmpfileid);
			MessageBox.show(wfpanel, "废止失败!"); //
		}
	}

	/**
	 * 发布新版本!
	 */
	private void onPublishFile() throws Exception {
		final String cmpfileid = billVO.getStringValue("id");
		final String cmpfilename = billVO.getStringValue("cmpfilename");
		final boolean showreffile = new TBUtil().getSysOptionBooleanValue("流程文件是否由正文生成word", true);
		final String str_newversionno = billVO.getStringValue("newversionno"); //
		String str_isapprove = billVO.getStringValue("isapprove");
		if ("Y".equals(str_isapprove)) { //如果有新版本!!
			new SplashWindow(wfpanel, "正在发布,请等待...", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					new WFRiskUIUtil().dealpublish(wfpanel, cmpfileid, cmpfilename, showreffile, str_newversionno);
				}
			});
			String wfprinstanceid = billVO.getStringValue("wfprinstanceid");//实例id
			String[] sqls = new String[] { "delete from pub_wf_prinstance where rootinstanceid=" + wfprinstanceid, "delete from pub_wf_dealpool where rootinstanceid=" + wfprinstanceid, "delete from pub_task_deal where rootinstanceid=" + wfprinstanceid,
					"delete from pub_task_off where rootinstanceid=" + wfprinstanceid, "update cmp_cmpfile set wfprinstanceid=null where id=" + cmpfileid };
			UIUtil.executeBatchByDS(null, sqls);//删除流程实例、处理记录、待办记录、已办记录，并将该文件实例id设为空，这里不用改变文件状态为[有效]，因为在dealpublish()方法中已设置
		} else {
			//将文件状态[发布申请中]变为[编辑中]
			UIUtil.executeUpdateByDS(null, "update cmp_cmpfile set filestate='1' where id=" + cmpfileid);
			MessageBox.show(wfpanel, "发布新版本失败!"); //
		}
	}
}
