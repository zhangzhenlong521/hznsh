package cn.com.pushworld.salary.ui.person.p050;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * 个人评分的进度统计
 * @author Administrator
 */
public class PersonCheckProcessWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private DefaultStyleReportPanel_2 dr = null;
	private BillQueryPanel bq = null;

	private String exportFileName = "个人考核进度_";

	private WLTButton auto_score = new WLTButton("协助他人打分", UIUtil.getImage("user_edit.png"));
	private WLTButton submit = new WLTButton("协助提交", UIUtil.getImage("zt_071.gif"));
	private String input_value_range = TBUtil.getTBUtil().getSysOptionStringValue("薪酬模块人员打分区间", "0-10");

	public void initialize() {
		dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_PERSON", "");
		bq = dr.getBillQueryPanel();
		auto_score.addActionListener(this);
		dr.getPanel_btn().add(auto_score);
		dr.getPanel_btn().add(submit);
		submit.addActionListener(this);
		//设置日期默认值为当前考核日期  Gwang 2013-08-21
		QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) bq.getCompentByKey("checkdate");
		String checkDate = new SalaryUIUtil().getCheckDate();
		dateRef.setValue(checkDate);

		//设置导出文件名称 Gwang 2013-08-31
		dr.setReportExpName(exportFileName);

		dr.getBillCellPanel().setEditable(false);
		bq.addBillQuickActionListener(this);
		this.add(dr, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == auto_score) {
			autoScore();
		} else if (e.getSource() == confirm) {
			onClickAutoScore();
		} else if (e.getSource() == submit) {
			onAutoSubmit();
		} else {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					onQuery();
				}
			});
		}
	}

	private void onAutoSubmit() {
		String checkdate = bq.getRealValueAt("checkdate");
		try {
			String logid = UIUtil.getStringValueByDS(null, "select id from sal_target_check_log where checkdate='" + checkdate + "' ");
			if (logid == null) {
				return;
			}
			BillListPanel listpanel = new BillListPanel(null, "select t2.scoreuser userid ,t1.name 姓名,t1.deptname 机构 from v_sal_personinfo t1 left join sal_person_check_score t2 on t1.id = t2.scoreuser where t2.scoretype='手动打分' and (t2.status is null or t2.status!='已提交') and t2.logid =" + logid + "  group by t2.scoreuser having count(t2.id)>0 ");
			listpanel.setItemVisible("userid", false);
			listpanel.setRowNumberChecked(true);
			BillListDialog dialog = new BillListDialog(this, "请选择需要协助提交的人员", listpanel, 400, 600, false);
			dialog.setVisible(true);
			BillVO vos[] = listpanel.getCheckedBillVOs();
			if (vos.length > 0) {
				String userids = new SalaryTBUtil().getInCondition(vos, "userid");
				UIUtil.executeUpdateByDS(null, "update sal_person_check_score set status='已提交' where logid = " + logid + " and scoreuser in(" + userids + ") and scoretype='手动打分' and checkscore is not null and checkscore !=''");
				MessageBox.show(this,"已协助提交，请重新查询!");
			}
		} catch (Exception ex) {

		}
	}

	//点击自动打分按钮
	private void onClickAutoScore() {
		String text = field.getText();
		if (TBUtil.isEmpty(text)) {
			MessageBox.show(dialog, "请输入自动打分分数");
			return;
		}
		if (!TBUtil.getTBUtil().isStrAllNunbers(text)) {
			MessageBox.show(dialog, "请输入整数");
			return;
		}
		try {
			int num = Integer.parseInt(text);
			int range[] = getInputNumRange();
			if (num < range[0] || num > range[1]) {// 应该不会存在
				MessageBox.show(dialog, "请输入[" + input_value_range + "]的整数.");
				return;
			}

			BillVO vos[] = listpanel.getSelectedBillVOs(true);
			if (vos.length == 0) {
				MessageBox.show(this, "请勾选要自动打分的人员.");
				return;
			}
			StringBuffer bf = new StringBuffer();
			StringBuffer userid = new StringBuffer();
			for (int i = 0; i < vos.length; i++) {
				bf.append(vos[i].getStringValue("username", "") + ",");
				userid.append(vos[i].getStringValue("userid", "") + ",");
			}
			if (MessageBox.confirm(this, "确定为评分人[" + bf.substring(0, bf.length() - 1) + "]未完成的员工评议指标打[" + num + "]分吗?")) {
				String checkdate = bq.getRealValueAt("checkdate");
				String logid = UIUtil.getStringValueByDS(null, "select id from sal_target_check_log where checkdate='" + checkdate + "' ");
				if (logid == null) {
					return;
				}
				String currdate = UIUtil.getServerCurrDate();
				String sql = "update sal_person_check_score set descr='管理员自动打分',lasteditdate='" + currdate + "',status='已提交',checkscore=" + num + " where (checkscore is null or checkscore='' ) and targettype='员工定性指标' and logid =" + logid + " and scoretype='手动打分' and scoreuser in(" + userid.substring(0, userid.length() - 1) + ")";
				UIUtil.executeUpdateByDS(null, sql);
				MessageBox.show(this, "打分完成,请重新查询.");
				dialog.dispose();
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(dialog, "请输入整数");
		}
	}

	private void onQuery() {
		try {
			if (bq.checkValidate()) {
				SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
				dr.getBillCellPanel().loadBillCellData(ifc.getPersonCheckProcessVO(bq.getRealValueAt("checkdate")));
				dr.getBillCellPanel().setEditable(false);
				if (dr.getBillCellPanel().getRowCount() > 2) {
					dr.getBillCellPanel().setLockedCell(2, 1); //锁定表头 杨科 2013-10-10
				}

				//设置导出文件名称 Gwang 2013-08-31
				dr.setReportExpName(exportFileName + bq.getRealValueAt("checkdate"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, "发生异常请与管理员联系!");
		}
	}

	private JTextField field = new JTextField();
	private WLTButton confirm = new WLTButton("自动打分");
	private BillListPanel listpanel = new BillListPanel(new MyTMO());
	private BillDialog dialog;

	private void autoScore() {
		String checkdate = bq.getRealValueAt("checkdate");
		try {
			String logid = UIUtil.getStringValueByDS(null, "select id from sal_target_check_log where checkdate='" + checkdate + "' ");
			if (logid == null) {
				return;
			}
			HashVO vos[] = UIUtil.getHashVoArrayByDS(null, "select t2.scoreuser userid,t1.name username,t1.deptname,count(t2.id) totle,count(t3.id) stotle from v_sal_personinfo t1 left join sal_person_check_score t2 on t1.id = t2.scoreuser left join sal_person_check_score t3 on t3.id = t2.id and (t3.checkscore is not null and t3.checkscore !='') where t2.scoretype='手动打分' and t2.logid = '" + logid
					+ "'   group by t2.scoreuser having (count(t2.id)-count(t3.id))>0");
			for (int i = 0; i < vos.length; i++) {
				vos[i].setAttributeValue("curr", vos[i].getStringValue("stotle") + "/" + vos[i].getStringValue("totle"));
			}
			if (vos.length == 0) {
				MessageBox.show(this, "本次打分都已完成.");
				return;
			}
			listpanel.setRowNumberChecked(true);
			listpanel.queryDataByHashVOs(vos);
			for (int i = 0; i < vos.length; i++) {
				listpanel.setCheckedRow(i, true);
			}
			dialog = new BillDialog(this, "协助他人打分", 400, 400);
			WLTLabel label = new WLTLabel("<html><a color='red'>此功能将协助未能参加打分的人员或未完成的部分自动打分.勾选评议人员,输入整数分数,点击自动打分</a>");
			dialog.setLayout(new BorderLayout());
			dialog.add(label, BorderLayout.NORTH);
			dialog.add(listpanel, BorderLayout.CENTER);
			JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			JLabel label_info = new WLTLabel("请输入分数:");
			field.setPreferredSize(new Dimension(80, 20));
			southPanel.add(label_info);
			southPanel.add(field);
			southPanel.add(confirm);
			confirm.addActionListener(this);
			dialog.add(southPanel, BorderLayout.SOUTH);
			dialog.setVisible(true);
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 得到可以录入值的范围,两个长度的数组.
	public int[] getInputNumRange() {
		String[] values = TBUtil.getTBUtil().split(input_value_range, "-");
		int begin = 0;
		int end = 10;
		try {
			if (values.length == 2) {
				begin = Integer.parseInt(values[0]);
				end = Integer.parseInt(values[1]);
				if (begin >= end) { // 如果值前后顺序有问题。
					begin = 0;
					end = 10;
				}
			}
		} catch (Exception _ex) {

		}
		return new int[] { begin, end };
	}

	class MyTMO extends AbstractTMO {

		public HashVO getPub_templet_1Data() {
			HashVO vo = new HashVO(); //
			vo.setAttributeValue("templetcode", "C"); // 模版编码,请勿随便修改
			vo.setAttributeValue("templetname", "员工评议未完成人员"); // 模板名称
			vo.setAttributeValue("templetname_e", ""); // 模板名称
			vo.setAttributeValue("tablename", "WLTDUAL"); // 查询数据的表(视图)名
			vo.setAttributeValue("pkname", "ID"); // 主键名
			vo.setAttributeValue("pksequencename", "S_SAL_PERSON_CHECK_SCORE"); // 序列名
			vo.setAttributeValue("savedtablename", "SAL_PERSON_CHECK_SCORE"); // 保存数据的表名
			vo.setAttributeValue("CardWidth", "577"); // 卡片宽度
			vo.setAttributeValue("Isshowlistpagebar", "N"); // 列表是否显示分页栏
			vo.setAttributeValue("Isshowlistopebar", "N"); // 列表是否显示操作按钮栏
			vo.setAttributeValue("listcustpanel", null); // 列表自定义面板
			vo.setAttributeValue("cardcustpanel", null); // 卡片自定义面板

			vo.setAttributeValue("TREEPK", "id"); // 列表是否显示操作按钮栏
			vo.setAttributeValue("TREEPARENTPK", ""); // 列表是否显示操作按钮栏
			vo.setAttributeValue("Treeviewfield", ""); // 列表是否显示操作按钮栏
			vo.setAttributeValue("Treeisshowtoolbar", "Y"); // 树型显示工具栏
			vo.setAttributeValue("Treeisonlyone", "N"); // 树型显示工具栏
			vo.setAttributeValue("Treeseqfield", "seq"); // 列表是否显示操作按钮栏
			vo.setAttributeValue("Treeisshowroot", "Y"); // 列表是否显示操作按钮栏
			return vo;
		}

		@Override
		public HashVO[] getPub_templet_1_itemData() {
			List itemvoList = new ArrayList();

			HashVO itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "userid"); // 唯一标识,用于取数与保存 //指标ID
			itemVO.setAttributeValue("itemname", "主键"); // 显示名称
			itemVO.setAttributeValue("itemname_e", "Id"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "Y"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "145"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "N"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "1"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemvoList.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "username"); // 唯一标识,用于取数与保存 //指标ID
			itemVO.setAttributeValue("itemname", "评分人"); // 显示名称
			itemVO.setAttributeValue("itemname_e", "Id"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "80"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemvoList.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "deptname"); // 唯一标识,用于取数与保存 //指标ID
			itemVO.setAttributeValue("itemname", "部门"); // 显示名称
			itemVO.setAttributeValue("itemname_e", "Id"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "80"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemvoList.add(itemVO);

			itemVO = new HashVO();
			itemVO.setAttributeValue("itemkey", "curr"); // 唯一标识,用于取数与保存 //指标ID
			itemVO.setAttributeValue("itemname", "进度"); // 显示名称
			itemVO.setAttributeValue("itemname_e", "Id"); // 显示名称
			itemVO.setAttributeValue("itemtype", "文本框"); // 控件类型
			itemVO.setAttributeValue("comboxdesc", null); // 下拉框定义
			itemVO.setAttributeValue("refdesc", null); // 参照定义
			itemVO.setAttributeValue("issave", "N"); // 是否参与保存(Y,N)
			itemVO.setAttributeValue("isdefaultquery", "2"); // 1-快速查询;2-通用查询;3-不参与查询
			itemVO.setAttributeValue("ismustinput", "N"); // 是否必输项(Y,N)
			itemVO.setAttributeValue("loadformula", null); // 加载公式
			itemVO.setAttributeValue("editformula", null); // 编辑公式
			itemVO.setAttributeValue("defaultvalueformula", null); // 默认值公式
			itemVO.setAttributeValue("listwidth", "80"); // 列表是宽度
			itemVO.setAttributeValue("cardwidth", "150"); // 卡片时宽度
			itemVO.setAttributeValue("listisshowable", "Y"); // 列表时是否显示(Y,N)
			itemVO.setAttributeValue("listiseditable", "4"); // 列表时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("cardisshowable", "N"); // 卡片时是否显示(Y,N)
			itemVO.setAttributeValue("cardiseditable", "1"); // 卡片时是否可编辑,1-全部可编辑,2-仅新增可编辑,3-仅修改可编辑;4-全部禁用
			itemVO.setAttributeValue("iswrap", "N");
			itemvoList.add(itemVO);
			return (HashVO[]) itemvoList.toArray(new HashVO[0]);
		}

	}
}