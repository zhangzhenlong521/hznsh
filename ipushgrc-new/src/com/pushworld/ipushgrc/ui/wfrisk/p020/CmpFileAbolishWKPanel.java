package com.pushworld.ipushgrc.ui.wfrisk.p020;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.wfrisk.CmpFileHistoryViewDialog;
import com.pushworld.ipushgrc.ui.wfrisk.CmpfileAndWFGraphDialog;
import com.pushworld.ipushgrc.ui.wfrisk.PublishNewCmpFileVersionDialog;
import com.pushworld.ipushgrc.ui.wfrisk.WFRiskUIUtil;

/**
 * 体系文件废止/发布! 
 * A.即一些客户可能有客种需求:编辑与发布人员不是同一种角色! 即一般员工负责编辑,管理员负责发布! 则需要单独有一个发布的功能点!!
 * B.如果不是走工作流,则可以直接选择一个接收人员指定某个人进行发布!
 * @author xch
 *
 */
public class CmpFileAbolishWKPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener {
	private BillListPanel billList_cmpfile; // 流程文件列表!

	private WLTButton btn_wf_process, btn_looktempfile, btn_looktempfile2; //
	private WLTButton btn_publish, btn_abolish; //发布,废止!!
	private boolean showreffile = TBUtil.getTBUtil().getSysOptionBooleanValue("流程文件是否由正文生成word", true);

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		String templetcode = TBUtil.getTBUtil().getSysOptionHashItemStringValue("各功能中流程文件的模板", "发布", "CMP_CMPFILE_CODE3");//该模板有走流程时最后环节出现的新版本号newversionno和是否同意isapprove 字段【李春娟/2012-07-13】
		billList_cmpfile = new BillListPanel(templetcode); //
		//billList_cmpfile.setDataFilterCustCondition("filestate in('1','3')");//只查看编辑中和有效的,张越霞强烈建议可以显示申请中的，为了可以监控，但如果没参与申请流程，则处理意见不显示
		btn_looktempfile = new WLTButton("Word预览");
		btn_looktempfile.setToolTipText("临时版本Word预览");
		btn_looktempfile2 = new WLTButton("Html预览");
		btn_looktempfile2.setToolTipText("临时版本Html预览");
		btn_looktempfile.addActionListener(this);
		btn_looktempfile2.addActionListener(this);
		if (TBUtil.getTBUtil().getSysOptionBooleanValue("流程文件发布是否走流程", false)) {//如果流程文件发布走流程（默认不走流程）
			btn_wf_process = WLTButton.createButtonByType(WLTButton.LIST_WORKFLOWSTART_MONITOR); //流程发起/监控
			btn_wf_process.addActionListener(this);
			billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_wf_process, btn_looktempfile, btn_looktempfile2 });// 流程文件添加按钮
		} else {
			btn_publish = new WLTButton("发布");
			btn_abolish = new WLTButton("废止");
			btn_publish.addActionListener(this);
			btn_abolish.addActionListener(this);
			billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_publish, btn_abolish, btn_looktempfile, btn_looktempfile2 });// 流程文件添加按钮
		}
		billList_cmpfile.repaintBillListButton();// 必须重新绘制按钮
		billList_cmpfile.addBillListHtmlHrefListener(this);
		this.add(billList_cmpfile); //
	}

	/**
	 * 列表按钮的点击事件
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_publish) {//【发布】
			onPublishFile();
		} else if (e.getSource() == btn_abolish) {//【废止】
			onAbolishFile();
		} else if (e.getSource() == btn_wf_process) {//【流程发起/监控】
			onDealProcess();
		} else if (e.getSource() == btn_looktempfile) {//【Word预览】
			onLookTempFileByWord();
		} else if (e.getSource() == btn_looktempfile2) {//【Html预览】
			onLookTempFileByHtml();
		}
	}

	/**
	 * 列表链接事件，一个是文件名称的链接，一个是历史版本的链接，因该页面按钮太多，所以将文件名称和历史版本设成链接查看，
	 * 为了和后面流程文件地图等功能点一致，文件名称链接打开是查看文件和流程的dialog，而不采用查看正文的方式。
	 */
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if ("cmpfilename".equals(_event.getItemkey())) {
			onLookFileAndWf();
		} else {
			onShowHist();
		}
	}

	/**
	 * 流程处理的逻辑，编辑中的文件必定是要发布，这时就得判断如果按正文生成word，但又没编写正文，则不允许发布.
	 * 如果该文件已有流程实例，并且已走完，并且当前登录人是实例的创建人，则清空流程，重新发起流程。
	 * 这种情况发生在上次流程处理结果为领导不同意发布或不同意废止，因编写人要查看处理意见，进行相应的修改，故在流程结束时不能清空流程处理记录!!!\
	 * 而需要编写人重新点击流程处理时清空!!!必须是编写人(也就是流程发起人)
	 */
	private void onDealProcess() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_cmpfile.refreshCurrSelectedRow();
		billVO = billList_cmpfile.getSelectedBillVO();
		String cmpfileid = billVO.getStringValue("id");
		String filestate = billVO.getStringValue("filestate");//1- 编辑中, 2- 发布申请中, 3- 有效, 4- 废止申请中, 5- 失效
		String wfprinstanceid = billVO.getStringValue("wfprinstanceid");
		if (wfprinstanceid == null) {//如果是发起流程
			if (!onChangeFilestate(billVO, filestate)) {//判断是否可流程处理，如果可以同时修改文件状态！
				return;
			}
			new cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory().dealAction("deal", billList_cmpfile, null); //发起流程!
			try {//一点击流程发起按钮即使取消了流程发布、流程状态也改变了。故做修改确定发布流程了才改变文件状态  【zzl】2016/1/28
				String id=UIUtil.getStringValueByDS(null, "select wfprinstanceid from cmp_cmpfile where id="+cmpfileid+"");
				if(!id.equals("") && filestate.equals("1")){
					UIUtil.executeUpdateByDS(null, "update cmp_cmpfile set filestate='2' where id="+cmpfileid+"");
					billList_cmpfile.reload();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				//判断当前登录人是否是发起人，如果是，并且该流程已结束，则应该删除流程所有信息，重新发起流程，否则只是监控流程
				String creater = UIUtil.getStringValueByDS(null, "select creater from pub_wf_prinstance where status='END' and id=" + wfprinstanceid);
				if (ClientEnvironment.getCurrLoginUserVO().getId().equals(creater) && MessageBox.showConfirmDialog(billList_cmpfile, "该流程已结束,但未通过审批,是否要重新发起流程(重新发起将删除所有审批信息)?") == JOptionPane.YES_OPTION) {
					String[] sqls = new String[] { "delete from pub_wf_prinstance where rootinstanceid=" + wfprinstanceid, "delete from pub_wf_dealpool where rootinstanceid=" + wfprinstanceid, "delete from pub_task_deal where rootinstanceid=" + wfprinstanceid, "delete from pub_task_off where rootinstanceid=" + wfprinstanceid, "update cmp_cmpfile set wfprinstanceid=null where id=" + cmpfileid };
					UIUtil.executeBatchByDS(null, sqls);//删除流程实例、处理记录、待办记录、已办记录，并将该文件实例id设为空
					if (!onChangeFilestate(billVO, filestate)) {//判断是否可流程处理，如果可以同时修改文件状态！
						return;
					}
					billList_cmpfile.setRealValueAt(null, billList_cmpfile.getSelectedRow(), "wfprinstanceid");//必须设置一下，不能用billList_cmpfile.refreshCurrSelectedRow()，因为onChangeFilestate()方法中已改变文件状态，但不能保存
					new cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory().dealAction("deal", billList_cmpfile, null); //处理动作!
				} else {//如果流程没有结束，则只能流程监控
					cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog wfMonitorDialog = new cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog(billList_cmpfile, wfprinstanceid, billVO); //
					wfMonitorDialog.setMaxWindowMenuBar();
					wfMonitorDialog.setVisible(true); //
				}
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
		}
		billList_cmpfile.refreshCurrSelectedRow();
	}

	/**
	 * 发起流程之前需要修改流程文件的文件状态
	 * @param _billvo    流程文件billvo
	 * @param _filestate 文件状态
	 * @return           是否可以流程处理
	 */
	private boolean onChangeFilestate(BillVO _billvo, String _filestate) {
		if ("2".equals(_filestate) || "4".equals(_filestate) || "5".equals(_filestate)) {//[发布申请中]、[废止申请中]和[废止]的文件不能进行其他操作
			MessageBox.show(this, "该文件的状态为[" + _billvo.getStringViewValue("filestate") + "],不能进行此操作!");
		} else if ("3".equals(_filestate)) {//[有效]的文件只能走废止流程
			if ("编辑中".equals(_billvo.getStringValue("riskstate"))) {
				MessageBox.show(this, "该风险状态为[编辑中],不能进行此操作,请通知风险评估人员结束编辑!"); //
				return false;
			}
			if (MessageBox.showConfirmDialog(this, "该操作将启动[废止]流程, 是否继续?") == JOptionPane.YES_OPTION) {
				billList_cmpfile.setRealValueAt("4", billList_cmpfile.getSelectedRow(), "filestate");
				return true;
			}
		} else {//[编辑中]的文件要走发布流程,如果流程文件由正文生成word，并且正文为空，即没有编写正文则不允许发布
			if ("编辑中".equals(_billvo.getStringValue("riskstate"))) {
				MessageBox.show(this, "该风险状态为[编辑中],不能进行此操作,请通知风险评估人员结束编辑!"); //
				return false;
			}
			if (MessageBox.showConfirmDialog(this, "该操作将启动[发布]流程, 是否继续?") == JOptionPane.YES_OPTION) {
				String reffilename = _billvo.getStringValue("reffile", "");
				if (showreffile && "".equals(reffilename)) {
					MessageBox.show(this, "该文件正文未编写,不能发布!"); //
					return false;
				}
//				billList_cmpfile.setRealValueAt("2", billList_cmpfile.getSelectedRow(), "filestate");即使是点击取消文件状态也改变
				return true;
			}
		}
		return false;
	}

	/**
	 * 发布新版本!!!
	 */
	private void onPublishFile() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_cmpfile.refreshCurrSelectedRow();//刷新选中行，以防别人发布了版本，又重复发布了
		billVO = billList_cmpfile.getSelectedBillVO();//重新获得billvo
		if ("5".equals(billVO.getStringValue("filestate"))) {
			MessageBox.show(this, "该文件已处于[" + billVO.getStringViewValue("filestate") + "]状态，不能发布!"); //
			return;
		}
		if ("编辑中".equals(billVO.getStringValue("riskstate"))) {
			MessageBox.show(this, "该风险状态为[编辑中],不能进行此操作,请通知风险评估人员结束编辑!"); //
			return;
		}
		if ("3".equals(billVO.getStringValue("filestate")) && !MessageBox.confirm(this, "该流程文件状态已是[有效], 是否需要重新发布?")) {//如果本身就是有效的则提示是否重新发布【李春娟/2012-03-08】
			return;
		}
		final String cmpfileid = billVO.getStringValue("id");
		final String cmpfilename = billVO.getStringValue("cmpfilename");
		String str_oldversion = billVO.getStringValue("versionno"); //旧的版本号
		final String reffilename = billVO.getStringValue("reffile", "");
		//如果流程文件由正文生成word，并且正文为空，即没有编写正文则不允许发布
		if (showreffile && "".equals(reffilename)) {
			MessageBox.show(this, "该文件正文未编写,不能发布!"); //
			return;
		}

		String str_newversionno = null; //
		if (str_oldversion == null || "".equals(str_oldversion)) { //如果没有版本号，第一次发布默认设为“1.0”
			str_newversionno = "1.0";
		} else {
			if (!str_oldversion.contains(".")) {
				str_oldversion = str_oldversion + ".0";
			}
			PublishNewCmpFileVersionDialog dialog = new PublishNewCmpFileVersionDialog(this, str_oldversion); //
			dialog.setVisible(true); //
			if (dialog.getCloseType() == 1) {
				str_newversionno = dialog.getReturnNewVersion(); //取得返回的版本号!!
			}
		}
		if (str_newversionno != null) { //如果有新版本!!
			final String newversionno = str_newversionno;
			new SplashWindow(this, "正在发布,请等待...", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					new WFRiskUIUtil().dealpublish(CmpFileAbolishWKPanel.this, cmpfileid, cmpfilename, showreffile, newversionno);
				}
			});
			billList_cmpfile.refreshCurrSelectedRow(); //刷新
		}
	}

	/**
	 * 废止某个流程文件!
	 */
	private void onAbolishFile() {
		try {
			BillVO billVO = billList_cmpfile.getSelectedBillVO();
			if (billVO == null) {
				MessageBox.showSelectOne(this);
				return;
			}
			if ("5".equals(billVO.getStringValue("filestate"))) {
				MessageBox.show(this, "该文件已处于[" + billVO.getStringViewValue("filestate") + "]状态，请勿重复操作!"); //
				return;
			}
			if ("编辑中".equals(billVO.getStringValue("riskstate"))) {
				MessageBox.show(this, "该风险状态为[编辑中],不能进行此操作,请通知风险评估人员结束编辑!"); //
				return;
			}
			if (!MessageBox.confirm(this, "流程文件废止后将只能查看,无法再重新维护,\r\n除非后台修改数据库,是否继续?")) { //【李春娟/2012-03-08】
				return; //
			}
			UIUtil.executeBatchByDS(null, new String[] { "update cmp_cmpfile set filestate='5' where id=" + billVO.getStringValue("id"), "update cmp_risk_editlog set filestate='5' where cmpfile_id =" + billVO.getStringValue("id") });
			billList_cmpfile.refreshCurrSelectedRow(); //
			MessageBox.show(this, "废止成功!"); //
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * 文件名称-链接，查看流程文件及其所有流程
	 */
	private void onLookFileAndWf() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请选择一个流程文件!"); //
			return;
		}
		String tabcount = this.getMenuConfMapValueAsStr("文件查看页签数", "0");//流程维护和发布废止两个功能可配置文件查看页签数量，比平台参数的优先级要高。默认值为0，表示该菜单参数弃权，根据平台参数判断【李春娟/2015-02-11】
		CmpfileAndWFGraphDialog dialog = new CmpfileAndWFGraphDialog(this, "查看文件和流程", billVO.getStringValue("id"), tabcount);
		dialog.setVisible(true);
	}

	/**
	 * 历史版本-链接的逻辑
	 */
	private void onShowHist() {
		try {
			BillVO billVO = billList_cmpfile.getSelectedBillVO();
			if (billVO == null) {
				MessageBox.showSelectOne(this);
				return;
			}
			String versionno = billVO.getStringValue("versionno");//只根据版本号是否为空判断即可，因为如果发布过的文件肯定有版本号，并且如果可以删除历史版本的话，当前版本肯定是要保留的
			if (versionno == null) {
				MessageBox.show(this, "该文件未发布过，没有历史版本!"); //
				return;
			}
			String str_cmpfileid = billVO.getStringValue("id"); //
			CmpFileHistoryViewDialog dialog = new CmpFileHistoryViewDialog(this, "文件[" + billVO.getStringValue("cmpfilename") + "]的历史版本", str_cmpfileid, false); //
			dialog.setVisible(true); //
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * 【Word预览】的逻辑
	 */
	private void onLookTempFileByWord() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String str_cmpfileid = billVO.getStringValue("id"); //
		try {
			new WFRiskUIUtil().openOneFileAsWord(this, str_cmpfileid);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	/**
	 * 【Html预览】的逻辑
	 */
	private void onLookTempFileByHtml() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "请选择一个流程文件!"); //
			return;
		}
		String str_cmpfileID = billVO.getStringValue("id"); //
		int htmlStyle = 0;
		if (getMenuConfMapValueAsStr("htmlStyle") != null) {
			htmlStyle = Integer.parseInt(getMenuConfMapValueAsStr("htmlStyle"));
		}
		try {
			new WFRiskUIUtil().openOneFileAsHTML(billList_cmpfile, str_cmpfileID, htmlStyle); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}
}
