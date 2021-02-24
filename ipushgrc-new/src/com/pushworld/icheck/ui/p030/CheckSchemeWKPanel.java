package com.pushworld.icheck.ui.p030;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.report.cellcompent.ExcelUtil;

import com.pushworld.ipushgrc.ui.icheck.word.WordExport;

/**
 * 检查方案维护 (CK_SCHEME)【李春娟/2016-08-10】
 * 方案状态：未执行，执行中，已结束
 * 
 */
public class CheckSchemeWKPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener, BillListMouseDoubleClickedListener {

	private static final long serialVersionUID = -1514385022019530158L;
	private BillListPanel billList_scheme = null;
	private WLTButton btn_insertFromPlan, btn_insert, btn_edit, btn_delete, btn_export, btn_start, btn_end;
	private String status1 = "未执行", status2 = "执行中", status3 = "已结束";//检查方案状态
	private BillVO vo = null;
	private static String id=null;
	private WLTButton btn_new=null;
	@Override
	public void initialize() {
		billList_scheme = new BillListPanel("CK_SCHEME_LCJ_E01");

		String type = this.getMenuConfMapValueAsStr("网络版", "Y");
		if ("N".equalsIgnoreCase(type)) {//单机版只显示【结束】按钮【李春娟/2016-09-12】
			btn_end = new WLTButton("结束");
			btn_end.addActionListener(this);
			billList_scheme.addBatchBillListButton(new WLTButton[] { btn_end });
		} else {
			btn_insertFromPlan = new WLTButton("从计划创建");
//			btn_insert = new WLTButton("直接创建");
			btn_edit = new WLTButton("修改");
			btn_delete = new WLTButton("删除");
			btn_export = new WLTButton("导出方案");
			btn_start = new WLTButton("开始");
			btn_end = new WLTButton("结束");
			btn_new=new WLTButton("新增检查人员");

			btn_insertFromPlan.addActionListener(this);
//			btn_insert.addActionListener(this);
			btn_edit.addActionListener(this);
			btn_delete.addActionListener(this);
			btn_export.addActionListener(this);
			btn_start.addActionListener(this);
			btn_end.addActionListener(this);
			btn_new.addActionListener(this);
			billList_scheme.addBatchBillListButton(new WLTButton[] { btn_insertFromPlan, btn_edit, btn_delete, btn_export, btn_start, btn_end ,btn_new});
		}

		billList_scheme.repaintBillListButton();
		billList_scheme.addBillListSelectListener(this);
		billList_scheme.addBillListMouseDoubleClickedListener(this);
		this.add(billList_scheme);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_insertFromPlan) {
			onInsertFromPlan();
		} else if (obj == btn_insert) {
			onInsert();
		} else if (obj == btn_edit) {
			onEdit();
		} else if (obj == btn_delete) {
			onDelete();
		} else if (obj == btn_export) {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					onExport();
				}
			});
		} else if (obj == btn_start) {
			onStart();
		} else if (obj == btn_end) {
			onEnd();
		}else if(obj == btn_new){
			onNew();
		}
	}
/**
 * zzl[]新增检查人员
 */
	private void onNew() {
		BillVO vo=billList_scheme.getSelectedBillVO();
		BillCardDialog log=new BillCardDialog(this,"新增检查人员","CK_SCHEME_ZZL_E01_1",800,400);
		log.getBillcardPanel().queryDataByCondition("id="+vo.getStringValue("id"));
		log.setVisible(true);
	}

	/**
	 * 从计划创建方案
	 */
	private void onInsertFromPlan() {
		BillListDialog billlistDialog = new BillListDialog(this, "选择一个计划，点击下一步", "CK_PLAN_LCJ_E01");
		billlistDialog.getBtn_confirm().setText("下一步");
		billlistDialog.getBilllistPanel().setDataFilterCustCondition("status='" + status2 + "'");
		billlistDialog.getBilllistPanel().QueryDataByCondition(null);
		billlistDialog.getBilllistPanel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		billlistDialog.getBilllistPanel().addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		billlistDialog.getBilllistPanel().repaintBillListButton();
		billlistDialog.setVisible(true);
		if (billlistDialog.getCloseType() != 1) {
			return;
		}
		BillVO planVo = billlistDialog.getReturnBillVOs()[0];
		BillCardPanel schemePanel = new BillCardPanel(billList_scheme.getTempletVO());//方案基本信息
		schemePanel.insertRow(); //卡片新增一行!
		schemePanel.setEditableByInsertInit();
		schemePanel.setValueAt("PLANID", new RefItemVO(planVo.getStringValue("id"), "", planVo.getStringValue("planname")));
		schemePanel.setValueAt("CODE", new StringItemVO(planVo.getStringValue("CODE") + "-"));//方案编码
		schemePanel.setValueAt("PLANNAME", new StringItemVO(planVo.getStringValue("PLANNAME")));//检查项目名称
		schemePanel.setValueAt("NAME", new StringItemVO(planVo.getStringValue("PLANNAME")));//方案名称
		schemePanel.setValueAt("GOAL", new StringItemVO(planVo.getStringValue("GOAL")));
		schemePanel.setValueAt("CKSCOPE", new StringItemVO(planVo.getStringValue("CKSCOPE")));

		schemePanel.setRealValueAt("CHECKDEPT", planVo.getStringValue("CHECKDEPT"));//检查提请部门
		schemePanel.setRealValueAt("PLANTYPE", planVo.getStringValue("PLANTYPE"));//计划类型
		schemePanel.setValueAt("PLANBEGINDATE", planVo.getStringValue("PLANBEGINDATE"));//计划开始日期
		schemePanel.setValueAt("PLANENDDATE", planVo.getStringValue("PLANENDDATE"));//计划结束日期

		String currPlanid = planVo.getStringValue("id");
		String currSchemeid = schemePanel.getRealValueAt("id");

		CheckSchemeEditDialog editDialog = new CheckSchemeEditDialog(billList_scheme, schemePanel, 0, currPlanid, currSchemeid);
		editDialog.setVisible(true);
	}

	/**
	 * 直接创建方案
	 */
	private void onInsert() {
		BillCardPanel schemePanel = new BillCardPanel(billList_scheme.getTempletVO());//方案基本信息
		schemePanel.insertRow(); //卡片新增一行!
		schemePanel.setEditableByInsertInit();
		schemePanel.setVisiable("planid", false);
		String currPlanid = "";//设置当前计划
		String currSchemeid = schemePanel.getRealValueAt("id");//设置当前方案

		CheckSchemeEditDialog editDialog = new CheckSchemeEditDialog(billList_scheme, schemePanel, 0, currPlanid, currSchemeid);
		editDialog.setVisible(true);
	}

	/**
	 * 修改方案，如果是系统管理员身份，则允许修改，否则只能修改未确认的方案
	 */
	private void onEdit() {
		BillVO selectedRow = billList_scheme.getSelectedBillVO();
		if (selectedRow == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String status = selectedRow.getStringValue("status");
		if (status1.equals(status)) {//如果是未执行的才可修改，因执行中或已结束的方案已经生成了实施记录，故修改也没什么意义，管理员也不要修改了
			BillCardPanel schemePanel = new BillCardPanel(billList_scheme.getTempletVO());//方案基本信息
			schemePanel.setBillVO(selectedRow);
			schemePanel.setEditableByEditInit();
			schemePanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
			String currPlanid = selectedRow.getStringValue("planid");//设置当前计划
			String currSchemeid = selectedRow.getStringValue("id");//设置当前方案

			CheckSchemeEditDialog editDialog = new CheckSchemeEditDialog(billList_scheme, schemePanel, 1, currPlanid, currSchemeid);
			editDialog.setVisible(true);
		} else {
			MessageBox.show(this, "该方案" + status + "，不可修改。");
		}
	}

	/**
	 * 删除方案，删除前需要判断方案状态，如果是系统管理员身份，则允许删除，否则只能删除未执行的方案
	 */
	private void onDelete() {
		BillVO selectedRow = billList_scheme.getSelectedBillVO();
		if (selectedRow == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_scheme.refreshCurrSelectedRow();//先刷新一下再取状态
		selectedRow = billList_scheme.getSelectedBillVO();
		String id = selectedRow.getStringValue("id");
		String status = selectedRow.getStringValue("status");
		try {
			if (!status1.equals(status)) {//如果不是未执行状态，则只允许管理员身份删除，包括删除相关信息 【李春娟/2016-08-25】
				if (ClientEnvironment.getInstance().isAdmin()) {
					if (!MessageBox.confirm(this, "该操作将删除本方案所有信息（包括方案、底稿、问题等），是否继续？")) {
						return;
					}
				} else {
					MessageBox.show(this, "该方案" + status + "，不可删除。");
					return;
				}
			} else if (!MessageBox.confirmDel(this)) {
				return;
			}
			ArrayList sqlList = new ArrayList();
			sqlList.add("delete from CK_SCHEME where id=" + id);//方案
			sqlList.add("delete from CK_MEMBER_WORK where schemeid=" + id);//检查组成员及分工
			sqlList.add("delete from ck_manuscript_design where schemeid=" + id);//底稿设计
			sqlList.add("delete from ck_scheme_impl where schemeid=" + id);//检查实施表
			sqlList.add("delete from ck_scheme_implement where schemeid=" + id);//检查实施表
			sqlList.add("delete from ck_problem_info where schemeid=" + id);//问题表
			//以后这里还涉及删除整改方案及整改跟踪等信息
			UIUtil.executeBatchByDS(null, sqlList);
			billList_scheme.removeSelectedRow();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 导出方案确认书
	 * [zzl]
	 */
	private void onExport() {
		if (vo == null) {
			MessageBox.show(this, "请选择一条数据");
			return;
		}
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("请选择要保存到的目录");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		int result = fc.showSaveDialog(this);
		if (result != JFileChooser.APPROVE_OPTION) { // 如果不是确定的
			return;
		}
		String savePath = fc.getSelectedFile().getPath();
		Map<Object, Object> dataMap = new HashMap<Object, Object>();
		try {
			String DeptName = UIUtil.getStringValueByDS(null, "select name from pub_corp_dept where id=1");
			String planName = UIUtil.getStringValueByDS(null, "select PLANTYPE from CK_PLAN where id='" + vo.getStringValue("planid") + "'");
			if (planName.equals("合规检查")) {
				planName = "合规";
			}
			if (planName.equals("审计检查")) {
				planName = "审计";
			}
			if (planName.equals("自律检查")) {
				planName = "自律";
			}
			dataMap.put("title", DeptName + planName + "检查方案");
			dataMap.put("titletime", vo.getStringValue("CREATEDATE"));
			dataMap.put("code", vo.getStringValue("CODE"));
			dataMap.put("projectnmae", vo.getStringValue("NAME"));
			HashVO [] accvo=UIUtil.getHashVoArrayByDS(null,"select * from ck_manuscript_design where schemeid ='"+vo.getStringValue("id")+"'");
			Map<Object,Object> accmap=new HashMap<Object, Object>();
			for(int i=0;i<accvo.length;i++){
				String tag_law=accvo[i].getStringValue("tag_law");
				String tag_rule=accvo[i].getStringValue("tag_rule");
				String tag_flow=accvo[i].getStringValue("tag_flow");
				if(tag_law!=null){
				accmap.put(tag_law, tag_law);
				}
				if(tag_rule!=null){
				accmap.put(tag_rule, tag_rule);
				}
				if(tag_flow!=null){
				accmap.put(tag_flow, tag_flow);	
				}
			}
			StringBuffer accsb=new StringBuffer();
			for (Object s : accmap.keySet()) {
				accsb.append(s);
			}
			UpdateSQLBuilder update=new UpdateSQLBuilder("CK_SCHEME");
			update.setWhereCondition("id=" + vo.getStringValue("id"));
			update.putFieldValue("CHECKGIST", accsb.toString());
			UIUtil.executeUpdateByDS(null, update.getSQL());
			billList_scheme.refreshCurrSelectedRow();
			dataMap.put("according",accsb);
			dataMap.put("purpose", vo.getStringValue("GOAL"));
			dataMap.put("scope", vo.getStringValue("CKSCOPE"));
			dataMap.put("leader", vo.getStringViewValue("LEADER"));
			dataMap.put("leaderP", vo.getStringViewValue("REFEREE"));//副组长，现在考虑到信贷检查弄成多选【李春娟/2016-09-23】
			String ids = vo.getStringValue("MEMBERWORK");
			ids = ids.replace(";", ",");
			ids = ids.substring(0, ids.length() - 1);
			HashVO[] hv = UIUtil.getHashVoArrayByDS(null, "select * from CK_MEMBER_WORK where id in(" + ids + ")");
			StringBuffer sb = new StringBuffer();
			int a = 0;
			for (int i = 0; i < hv.length; i++) {
				a = a + 1;
				sb.append("第" + a + "小组" + "\n");
				String zzname = UIUtil.getStringValueByDS(null, "select name from pub_user where id='" + hv[i].getStringValue("LEADER") + "'");
				sb.append("组长:" + zzname + "\r");
				String[] names = UIUtil.getStringArrayFirstColByDS(null, "select name from pub_user where id in(" + filterid(hv[i].getStringValue("TEAMUSERS")) + ")");
				StringBuffer sb2 = new StringBuffer();
				sb2 = splitName(names);
				sb.append("组员:" + sb2 + "\r");
				String[] depts = UIUtil.getStringArrayFirstColByDS(null, "select name from pub_corp_dept where id in(" + filterid(hv[i].getStringValue("CHECKEDDEPT")) + ")");
				StringBuffer sb3 = new StringBuffer();
				sb3 = splitName(depts);
				sb.append("负责检查" + sb3 + "\n");
			}
			dataMap.put("names", sb);
			dataMap.put("time", vo.getStringValue("SITETIME"));
			dataMap.put("methods", vo.getStringValue("CONTENTMETHOD"));
			savePath = savePath + "\\" + dataMap.get("title") + ".doc";
			WordExport mdoc = new WordExport();
			mdoc.createDoc(dataMap, "WordPlanModel", savePath);
			MessageBox.show(this, "导出成功");
		} catch (Exception a) {
			a.printStackTrace();
		}

	}

	/**
	 * 开始实施方案
	 */
	private void onStart() {
		BillVO billvo = billList_scheme.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String status = billvo.getStringValue("status");
		if (status == null || "".equals(status) || status1.equals(status) || (ClientEnvironment.isAdmin() && status3.equals(status))) {//新增管理员身份可将结束状态的方案重新开始，防止有未检查完的记录，需要重新开启方案【李春娟/2016-09-12】
			try {
				String schemeid = billvo.getStringValue("id");
				String memberwork = billvo.getStringValue("MEMBERWORK");
				String count = UIUtil.getStringValueByDS(null, "select count(*) from ck_scheme_impl where schemeid=" + schemeid);
				if (count != null && !count.equals("0")) {
					MessageBox.show(this, "该方案已有实施记录，不重新生成。");
					UIUtil.executeUpdateByDS(null, "update CK_SCHEME set status='" + status2 + "' where id =" + billvo.getStringValue("id"));
					billList_scheme.refreshCurrSelectedRow();
					return;
				}
				final String[][] works = UIUtil.getStringArrayByDS(null, "select id,leader,teamusers,checkeddept from CK_MEMBER_WORK where schemeid=" + schemeid);
				if (works == null || works.length == 0) {
					MessageBox.show(this, "请修改方案，添加检查组成员及分工再开始。");
					return;
				}
				final String[] manuscriptids = UIUtil.getStringArrayFirstColByDS(null, "select id from ck_manuscript_design where schemeid = " + schemeid);
				if (manuscriptids == null || manuscriptids.length == 0) {
					MessageBox.show(this, "请修改方案，添加底稿设计再开始。");
					return;
				}

				final ArrayList sqlList = new ArrayList();
				final InsertSQLBuilder isqlBuilder1 = new InsertSQLBuilder("ck_scheme_impl");
				final InsertSQLBuilder isqlBuilder = new InsertSQLBuilder("ck_scheme_implement");
				//检查实施子表设置检查计划、检查方案等关联关系和冗余信息
				isqlBuilder1.putFieldValue("schemeid", schemeid);
				isqlBuilder1.putFieldValue("planid", billvo.getStringValue("planid", ""));
				isqlBuilder1.putFieldValue("planname", billvo.getStringValue("planname", ""));
				isqlBuilder1.putFieldValue("code", billvo.getStringValue("code", ""));
				isqlBuilder1.putFieldValue("schemetype", billvo.getStringValue("schemetype", ""));
				isqlBuilder1.putFieldValue("name", billvo.getStringValue("name", ""));
				isqlBuilder1.putFieldValue("leader", billvo.getStringValue("leader", ""));
				isqlBuilder1.putFieldValue("referee", billvo.getStringValue("referee", ""));
				isqlBuilder1.putFieldValue("checkgist", billvo.getStringValue("checkgist", ""));
				isqlBuilder1.putFieldValue("goal", billvo.getStringValue("goal", ""));
				isqlBuilder1.putFieldValue("ckscope", billvo.getStringValue("ckscope", ""));
				isqlBuilder1.putFieldValue("sitetime", billvo.getStringValue("sitetime", ""));
				isqlBuilder1.putFieldValue("contentmethod", billvo.getStringValue("contentmethod", ""));
				isqlBuilder1.putFieldValue("status", status1);//设置实施状态"未执行"，在检查实施中逐条录入信息后会变为"执行中"，方案结束时变为"已结束"【李春娟/2016-09-29】
				isqlBuilder1.putFieldValue("createdate", billvo.getStringValue("createdate", ""));//这里如果设置方案开始的时间点，前面又是方案信息，感觉怪怪的，故冗余方案信息【李春娟/2016-09-26】
				isqlBuilder1.putFieldValue("creater", billvo.getStringValue("creater", ""));
				isqlBuilder1.putFieldValue("createdept", billvo.getStringValue("createdept", ""));
				isqlBuilder1.putFieldValue("expstatus", "未导出");//导出状态
				//检查实施子表设置检查计划、检查方案关系
				isqlBuilder.putFieldValue("planid", billvo.getStringValue("planid", ""));
				isqlBuilder.putFieldValue("schemeid", schemeid);
				String SCHEMETYPE = billvo.getStringValue("SCHEMETYPE");//检查类型：信贷检查、票据检查
				if (SCHEMETYPE != null && ("信贷检查".equals(SCHEMETYPE) || "票据检查".equals(SCHEMETYPE))) {//信贷或票据检查必须导入样本库【李春娟/2016-10-14】
					JFileChooser chooser = new JFileChooser();
					chooser.setDialogTitle("请导入样本库");
					FileNameExtensionFilter filter = new FileNameExtensionFilter("(*.xls,*.xlxs)", "xls", "xlsx");
					chooser.addChoosableFileFilter(filter);
					int flag = chooser.showOpenDialog(this);
					if (flag == 1) {//直接关闭窗口，未选择文件
						return;
					}
					File file = chooser.getSelectedFile();
					if (file == null) {
						return;
					}
					final String[][] datas = new ExcelUtil().getExcelFileData(file.getPath());
					//客户名称、客户编号、借据编号、贷款方式、贷款品种、放款日期、到期日期、借据金额、借据余额、贷款期限(月)、
					//执行利率(%)、表内欠息、表外欠息、五级形态、经办人、经办机构、结息方式、贷款用途
					if (datas == null || datas.length < 2) {
						MessageBox.show(this, "该文件无可用数据!");
						return;
					}
					new SplashWindow(this, new AbstractAction() {//导入数据量大时时间比较长，故增加一个滚动条【李春娟/2016-10-14】
								public void actionPerformed(ActionEvent e) {
									try {
										HashMap deptMap = new HashMap();
										for (int m = 1; m < datas.length; m++) {//从第二行开始遍历，第一行是列头
											String deptname = datas[m][15];//经办机构
											if (deptname != null && !"".equals(deptname.trim())) {
												deptname = deptname.trim();
												ArrayList deptList = null;
												if (deptMap.containsKey(deptname)) {
													deptList = (ArrayList) deptMap.get(deptname);//记录那些机构在哪几行
												} else {
													deptList = new ArrayList();
												}
												deptList.add(m);
												deptMap.put(deptname, deptList);
											}
										}
										String[] deptnames = (String[]) deptMap.keySet().toArray(new String[0]);
										//查询经办机构的主键id
										HashMap deptIdNameMap = UIUtil.getHashMapBySQLByDS(null, "select id,name from pub_corp_dept where name in(" + TBUtil.getTBUtil().getInCondition(deptnames) + ")");//钢哥建议用shortname，但为了和合规产品统一，暂时先用name【李春娟/2016-09-30】
										for (int i = 0; i < works.length; i++) {//检查组成员及分工
											String checkeddept = works[i][3];
											String[] checkeddepts = TBUtil.getTBUtil().split(checkeddept, ";");

											//检查实施主表设置检查成员
											isqlBuilder1.putFieldValue("memberid", works[i][0]);
											isqlBuilder1.putFieldValue("leader2", works[i][1]);
											isqlBuilder1.putFieldValue("teamusers", works[i][2]);
											//检查实施子表设置检查成员
											isqlBuilder.putFieldValue("memberid", works[i][0]);
											isqlBuilder.putFieldValue("leader2", works[i][1]);
											isqlBuilder.putFieldValue("teamusers", works[i][2]);

											for (int j = 0; j < checkeddepts.length; j++) {//小组中的被检查机构
												checkeddept = checkeddepts[j];//单个被检查机构
												if (deptIdNameMap.containsKey(checkeddept)) {//如果导入的样本表中有该被检查机构记录，则按记录内容生成主子表
													ArrayList deptList = (ArrayList) deptMap.get(deptIdNameMap.get(checkeddept));//记录那些机构在哪几行
													for (int k = 0; k < deptList.size(); k++) {//该被检查机构样本数据的行号
														//检查实施主表设置主键、被检查机构
														String implid = UIUtil.getSequenceNextValByDS(null, "S_CK_SCHEME_IMPL");
														isqlBuilder1.putFieldValue("id", implid);
														isqlBuilder1.putFieldValue("deptid", checkeddept);
														//设置导入数据
														for (int n = 1; n < 19; n++) {
															isqlBuilder1.putFieldValue("c" + n, datas[(Integer) deptList.get(k)][n - 1]);
														}
														sqlList.add(isqlBuilder1.getSQL());
														//检查实施子表设置主表关联关系、被检查机构
														isqlBuilder.putFieldValue("implid", implid);
														isqlBuilder.putFieldValue("deptid", checkeddept);//被检查机构

														for (int j2 = 0; j2 < manuscriptids.length; j2++) {
															isqlBuilder.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_CK_SCHEME_IMPLEMENT"));
															isqlBuilder.putFieldValue("parentid", manuscriptids[j2]);//检查底稿id
															sqlList.add(isqlBuilder.getSQL());
														}
													}
												} else {//如果样本中没有该被检查机构，则按默认生成
													//检查实施主表设置主键、被检查机构
													String implid = UIUtil.getSequenceNextValByDS(null, "S_CK_SCHEME_IMPL");

													isqlBuilder1.putFieldValue("id", implid);
													isqlBuilder1.putFieldValue("deptid", checkeddept);
													sqlList.add(isqlBuilder1.getSQL());
													//检查实施子表设置主表关联关系、被检查机构
													isqlBuilder.putFieldValue("implid", implid);
													isqlBuilder.putFieldValue("deptid", checkeddept);//被检查机构

													for (int j2 = 0; j2 < manuscriptids.length; j2++) {
														isqlBuilder.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_CK_SCHEME_IMPLEMENT"));
														isqlBuilder.putFieldValue("parentid", manuscriptids[j2]);//检查底稿id
														sqlList.add(isqlBuilder.getSQL());
													}
												}
											}
										}
									} catch (WLTRemoteException e1) {
										e1.printStackTrace();
									} catch (Exception e1) {
										e1.printStackTrace();
									}
								}
							}, 600, 130, 300, 300, false); // 
				} else {//如果不是信贷检查或票据检查，则每个单位建一个检查实施主表
					for (int i = 0; i < works.length; i++) {//检查组成员及分工
						String checkeddept = works[i][3];
						String[] checkeddepts = TBUtil.getTBUtil().split(checkeddept, ";");

						//检查实施主表设置检查成员
						isqlBuilder1.putFieldValue("memberid", works[i][0]);
						isqlBuilder1.putFieldValue("leader2", works[i][1]);
						isqlBuilder1.putFieldValue("teamusers", works[i][2]);
						//检查实施子表设置检查成员
						isqlBuilder.putFieldValue("memberid", works[i][0]);
						isqlBuilder.putFieldValue("leader2", works[i][1]);
						isqlBuilder.putFieldValue("teamusers", works[i][2]);

						for (int j = 0; j < checkeddepts.length; j++) {//小组中的被检查机构
							checkeddept = checkeddepts[j];//单个被检查机构
							//检查实施主表设置主键、被检查机构
							String implid = UIUtil.getSequenceNextValByDS(null, "S_CK_SCHEME_IMPL");
							isqlBuilder1.putFieldValue("id", implid);
							isqlBuilder1.putFieldValue("deptid", checkeddept);
							sqlList.add(isqlBuilder1.getSQL());
							//检查实施子表设置主表关联关系、被检查机构
							isqlBuilder.putFieldValue("implid", implid);
							isqlBuilder.putFieldValue("deptid", checkeddept);//被检查机构

							for (int j2 = 0; j2 < manuscriptids.length; j2++) {
								isqlBuilder.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_CK_SCHEME_IMPLEMENT"));
								isqlBuilder.putFieldValue("parentid", manuscriptids[j2]);//检查底稿id
								sqlList.add(isqlBuilder.getSQL());
							}
						}
					}
				}
				sqlList.add("update CK_SCHEME set status='" + status2 + "' where id =" + schemeid);
				UIUtil.executeBatchByDS(null, sqlList);
				billList_scheme.refreshCurrSelectedRow();
				MessageBox.show(this, "操作成功！");
			} catch (Exception e) {
				e.printStackTrace();
				MessageBox.showException(this, e);
			}
		} else {
			MessageBox.show(this, "该方案" + status + "，无需再开始执行。");
		}
	}

	/**
	 * 结束方案
	 */
	private void onEnd() {
		BillVO billvo = billList_scheme.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_scheme.refreshCurrSelectedRow();//刷新一下，防止查询后有人改过状态了
		billvo = billList_scheme.getSelectedBillVO();
		String status = billvo.getStringValue("status");
		if (status3.equals(status)) {
			MessageBox.show(this, "该方案" + status + "，无需再结束。");
			return;
		} else if (!status2.equals(status)) {
			MessageBox.show(this, "只有" + status2 + "的方案可进行此操作。");
			return;
		}
		//这里需要判断是否有未实施的检查底稿，如有，则提示
		String schemeid = billvo.getStringValue("id");
		try {
			String[][] impls = UIUtil.getStringArrayByDS(null, "select t2.name username,t3.name deptname from ck_scheme_impl t1 left join pub_user t2 on t1.leader2=t2.id left join pub_corp_dept t3 on t1.deptid = t3.id where t1.schemeid='" + schemeid + "' and t1.status!='已结束' group by t1.leader2 ,t1.deptid  order by t1.leader2");
			if (impls != null && impls.length > 0) {//如果数据库中查询有未做完的检查机构，则提示【李春娟/2016-09-12】
				StringBuffer sb = new StringBuffer("以下单位未检查完毕：\n");
				String tempname = "";
				for (int i = 0; i < impls.length; i++) {
					if (tempname.equals(impls[i][0])) {
						sb.append("、" + impls[i][1]);
					} else {
						tempname = impls[i][0];
						if (i != 0) {
							sb.append("\n");
						}
						sb.append(impls[i][0] + "组：" + impls[i][1]);
					}
				}
				if (MessageBox.confirm(this, sb.toString() + "\n结束后无法再进行检查实施，是否继续？")) {
					UIUtil.executeBatchByDS(null, new String[] { "update CK_SCHEME set status='" + status3 + "' where id =" + schemeid, "update ck_scheme_impl set status='" + status3 + "' where schemeid =" + schemeid, });//【李春娟/2016-09-29】
					billList_scheme.refreshCurrSelectedRow();
					MessageBox.show(this, "操作成功！");
				}
			}

		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent e) {
		vo = e.getBillListPanel().getSelectedBillVO();
	}

	/**
	 * 列表双击浏览，需要显示底稿信息【李春娟/2016-09-03】
	 */
	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent event) {
		BillVO selectedRow = event.getCurrSelectedVO();
		if (selectedRow == null) {
			return;
		}
		BillCardPanel schemePanel = new BillCardPanel(billList_scheme.getTempletVO());//方案基本信息
		schemePanel.setBillVO(selectedRow);
		schemePanel.setEditable(false);
		schemePanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);

		String currPlanid = selectedRow.getStringValue("planid");//设置当前计划
		String currSchemeid = selectedRow.getStringValue("id");//设置当前方案

		CheckSchemeEditDialog editDialog = new CheckSchemeEditDialog(billList_scheme, schemePanel, 2, currPlanid, currSchemeid);
		editDialog.setVisible(true);
	}

	private String filterid(String ids) {
		ids = ids.replace(";", ",");
		ids = ids.substring(1, ids.length() - 1);
		return ids;

	}

	private StringBuffer splitName(String[] name) {
		StringBuffer sbname = new StringBuffer();
		for (int i = 0; i < name.length; i++) {
			sbname.append(name[i].toString());
			sbname.append(",");
		}
		return sbname;
	}
	public static String getPlanId(){
		return id;
		
	}

}
