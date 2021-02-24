package cn.com.pushworld.wn;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;

/**
 * 
 * @author zzl
 * 
 *         2019-3-29-上午11:13:41
 *         柜员服务质量评分:一周一次，委派会计给本网点的柜员打分，网点主任审核，本网点的网点主任和委派会计都只能负责本网点的柜员，
 *         联社管理部门可以查看所有的。
 */
public class GYServerKH extends AbstractWorkPanel implements
		BillTreeSelectListener, BillListSelectListener, ActionListener {

	// private CommDMO dmo = new CommDMO();
	private BillTreePanel billTreePanel_Dept = null;// 机构树
	private BillListPanel billListPanel_User_Post = null;// 人员表
	private BillListPanel billListPanel_User_check = null;// 柜员评分
	private WLTSplitPane splitPanel_all = null;
	private String str_currdeptid, str_currdeptname = null; // 当前机构ID及机构名称
	private String panelStyle = "3";
	private BillCellPanel billCellPanel = null;// 柜员评分表
	private WLTSplitPane splitPanel = null;
	private WLTButton btn_save, btn_end, btn_verify;// ZPY【2019-05-20】新增复核功能
	private JPanel panel = new JPanel();
	private String PFUSERNAME = ClientEnvironment.getInstance()
			.getLoginUserName();// 登录人员名称
	private String PFSUERCODE = ClientEnvironment.getInstance()
			.getLoginUserCode();// 登录人员编码
	private String PFUSERDEPT = ClientEnvironment.getInstance()
			.getLoginUserDeptId();// 登录人员机构号
	private HashMap USERCODES = null;
	private HashMap USERTYPE = null;
	private String pfTime = null;
	private String message="";
	
	public void initialize() {
		// 获取到当前登录人的机构code
		try {
			USERCODES = UIUtil.getHashMapBySQLByDS(null,
					"select id,code from PUB_CORP_DEPT");
			USERTYPE = UIUtil
					.getHashMapBySQLByDS(null,
							"select USERCODE,POSTNAME from  WNSALARYDB.V_PUB_USER_POST_1 where ISDEFAULT='Y'");// 对于人员评分表，不同的人看到柜员评分表中的内容
			/*USERTYPE = UIUtil
					.getHashMapBySQLByDS(null,"SELECT USERCODE,ROLENAME FROM V_PUB_USER_ROLE_1 WHERE ROLENAME ='委派会计' OR ROLENAME LIKE '%主任%'");
		*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		billTreePanel_Dept = new BillTreePanel(getCorpTempletCode());//最左侧 机构树:
																// 获取相应的数据
		billListPanel_User_Post = new BillListPanel("V_PUB_USER_POST_zpy");// 右侧上上人员表
		billListPanel_User_Post.addBillListSelectListener(this);
		// billCellPanel = new BillCellPanel("柜员服务考核评价单", null, null, true,
		// false);
		billListPanel_User_check = new BillListPanel("WN_GYPF_TABLE_CODE1");// 右侧人员评分表
		splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT);// 水平拆分
		billTreePanel_Dept.queryDataByCondition("1=1 ");
		billTreePanel_Dept
				.queryDataByCondition("CORPTYPE!='总行' AND CORPTYPE!='集团' AND CORPTYPE!='母行' ");
		splitPanel = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,
				billListPanel_User_Post, billListPanel_User_check);// 上下拆分
		splitPanel.setDividerLocation(200);
		btn_end = new WLTButton("结束评分");
		btn_save = new WLTButton("保存");
		btn_save.addActionListener(this);
		btn_end.addActionListener(this);
		// if ("主任".equals() ||
		// "副主任".equals(USERTYPE.get(PFSUERCODE).toString())) {
		String userType ="";
		if(USERTYPE.get(PFSUERCODE)!=null){
			userType = USERTYPE.get(PFSUERCODE).toString()==null?"":USERTYPE.get(PFSUERCODE).toString();// 获取到当前登录人的身份
		}
//		userType = USERTYPE.get(PFSUERCODE).toString()==null?"":USERTYPE.get(PFSUERCODE).toString();// 获取到当前登录人的身份
		if (userType!=null&& userType.contains("主任")) {
			btn_verify = new WLTButton("评分复核");
			btn_verify.addActionListener(this);
			billListPanel_User_check.addBillListButton(btn_verify);
			billListPanel_User_check.setItemEditable("fhreason", true);
		} else if (userType!=null&&userType.contains("委派会计")) {
			billListPanel_User_check.addBillListButton(btn_save);
			btn_end.setIconTextGap(-50);
			billListPanel_User_check.addBillListButton(btn_end);
			billListPanel_User_check.setItemEditable("fhreason", false);
		}
		billListPanel_User_check.repaintBillListButton();
		billListPanel_User_check.addBillListSelectListener(this);
		String PFDEPTCODE = USERCODES.get(PFUSERDEPT).toString();// 获取到当前的机构code
		/**
		 * 【2019-11-26】
		 * 应客户要求，不参与考核的柜员信息不再此显示
		 */
		String unCheckCode="SELECT CODE FROM V_SAL_PERSONINFO WHERE ISUNCHECK='Y' ";//获取到当前机构不参与考核的人名单 
		billListPanel_User_Post.queryDataByCondition("deptcode='" + PFDEPTCODE
				+ "' and POSTNAME like '%柜员%' and usercode not in ("+unCheckCode+")", "seq,usercode");
		// 获取到当前登录人的机构code
		if ("282006".equals(PFDEPTCODE)||"282007".equals(PFDEPTCODE)) {
			splitPanel_all.add(billTreePanel_Dept);// 如果当前登录人属于运营管理部，则显示机构树
			billTreePanel_Dept.addBillTreeSelectListener(this);
		}
		splitPanel_all.add(splitPanel);
		splitPanel_all.setDividerLocation(180);
		this.add(splitPanel_all);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {//保存按钮
			try {
				List<String> testList=new ArrayList<String>();
				 final	BillVO[] bos = billListPanel_User_check.getBillVOs();//这里获取到的是当前选中柜员的得分
				   for (int i = 0; i < bos.length; i++) {
					if("评分结束".equals(bos[i].getStringValue("state"))){
						testList.add(bos[i].getStringValue("id"));
					}
				   }
				   if(testList.size()==bos.length){
				   MessageBox.show(this,"当前柜员评分已经结束，请勿重复操作！！！");
				   return;
			   }
				final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
				List<String> list=new ArrayList<String>();
				//判断当前选中柜员评分是否结束
			   for (int i = 0; i < bos.length; i++) {
				if("评分结束".equals(bos[i].getStringValue("state"))){
					list.add(bos[i].getStringValue("id"));
				}
			   }
			   if(list.size()==bos.length){
				   MessageBox.show(this,"当前柜员评分已经结束，请勿重复操作！！！");
				   return;
			   }
				new SplashWindow(this, new AbstractAction() {

					@Override
					public void actionPerformed(ActionEvent e) {//重写方法，将方法放到后台执行
						message=service.saveGradeScore(bos);
					}
				});
				if(message.contains("成功")){
					MessageBox.show(this,message);
					billListPanel_User_check.refreshData();
				}else{
					MessageBox.show(this,message);
				}
			

				   
				   //代码废弃，暂时保留
//				List<String> testList=new ArrayList<String>();
//				//判断当前选中柜员评分是否结束
//				BillVO[] bos = billListPanel_User_check.getBillVOs();//这里获取到的是当前选中柜员的得分
//			   for (int i = 0; i < bos.length; i++) {
//				if("评分结束".equals(bos[i].getStringValue("state"))){
//					testList.add(bos[i].getStringValue("id"));
//				}
//			   }
//			   if(testList.size()==bos.length){
//			   MessageBox.show(this,"当前柜员评分已经结束，请勿重复操作！！！");
//			   return;
//		   }
//				StringBuffer sb = new StringBuffer("");
//				Double FENZHI = 0.0;
//				Double KOUOFEN = 0.0;
//				Double result = 0.0;
//				String pfreason = "";
//				int count = 1;
//				BillVO vov = billListPanel_User_Post.getSelectedBillVO();
//				// String pfUsercode=vov.getStringValue("USERCODE");
//				String usercode = billListPanel_User_Post.getSelectedBillVO()
//						.getStringValue("usercode");
//				pfTime = UIUtil.getStringValueByDS(null,
//						"SELECT max(PFTIME) FROM WN_GYPF_TABLE WHERE USERCODE='"
//								+ usercode + "'");
//				if (vov== null) {//这里判断的是当前有没有选中一名柜员进行操作
//					MessageBox.show(this, "请选中一位柜员进行操作!!!");
//					return;
//				}
//				List list = new ArrayList<String>();
//				UpdateSQLBuilder update = new UpdateSQLBuilder(
//						billListPanel_User_check.getTempletVO().getTablename());
//				for (int i = 0; i < bos.length; i++) {//验证
//					if (bos[i].getStringValue("xiangmu").equals("总分")) {
//						continue;
//					}
//					if (bos[i].getStringValue("KOUOFEN") != null
//							&& !bos[i].getStringValue("KOUOFEN").isEmpty()) {
//						KOUOFEN = Double.parseDouble(bos[i]
//								.getStringValue("KOUOFEN"));
//						FENZHI = Double.parseDouble(bos[i]
//								.getStringValue("FENZHI"));
//						if (FENZHI < KOUOFEN) {//判断当前扣分项是不是大于总分值
//							sb.append("第" + (count) + "行数据项目为["
//									+ bos[i].getStringValue("XIANGMU")
//									+ "],指标为["
//									+ bos[i].getStringValue("ZHIBIAO")
//									+ "]扣分项大于分值  \n");
//						}
//					} else {//判断当前扣分项是否为空
//						sb.append("第" + (count) + "行数据项目为["
//								+ bos[i].getStringValue("XIANGMU") + "],指标为["
//								+ bos[i].getStringValue("ZHIBIAO")
//								+ "]扣分项为空  \n");
//					}
//				}
//
//				if (sb.length() <= 0) {
//					for (int i = 0; i < bos.length - 1; i++) {
//						KOUOFEN = Double.parseDouble(bos[i]
//								.getStringValue("KOUOFEN"));
//						FENZHI = Double.parseDouble(bos[i]
//								.getStringValue("FENZHI"));
//						if (KOUOFEN != 0) {
//							KOUOFEN = FENZHI;
//						}
//						pfreason = bos[i].getStringValue("FHREASON");
//						result = result + KOUOFEN;
//						count = count + 1;
//						update.setWhereCondition("id='"
//								+ bos[i].getStringValue("id") + "'");
//						update.putFieldValue("KOUOFEN", KOUOFEN);
//
//						update.putFieldValue("FHREASON", pfreason);
//						list.add(update.getSQL());
//					}
//					billListPanel_User_check.setRealValueAt(
//							String.valueOf(100 - result), bos.length - 1,
//							"KOUOFEN");
//					update.setWhereCondition("id='"
//							+ bos[bos.length - 1].getStringValue("id") + "'");
//					update.putFieldValue("KOUOFEN", 100 - result);
//					list.add(update.getSQL());
//					//这个update负责修改所有的值
//					UpdateSQLBuilder update2 = new UpdateSQLBuilder(
//							billListPanel_User_check.getTempletVO()
//									.getTablename());
//					update2.setWhereCondition("USERCODE='"
//							+ vov.getStringValue("USERCODE")
//							+ "' and USERDEPT='"
//							+ vov.getStringValue("USERDEPT") + "' and PFTIME ='"+pfTime+"'");
//					update2.putFieldValue("PFUSERNAME", PFUSERNAME);
//					update2.putFieldValue("PFSUERCODE", PFSUERCODE);
//					String PFDEPTCODE = USERCODES.get(PFUSERDEPT).toString();
//					update2.putFieldValue("PFUSERDEPT", PFDEPTCODE);
//					update2.putFieldValue("FHRESULT", "未复核");
//					list.add(update2.getSQL());
//					UIUtil.executeBatchByDS(null, list);
//					MessageBox.show(this, "保存完成");
//					billListPanel_User_check.refreshData();
//				} else {
//					MessageBox.show(this, sb.toString());
//				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (e.getSource() == btn_end) {//结束按钮
			try {
				// 获取到当前选中柜员信息数据
				BillVO gyselected = billListPanel_User_Post.getSelectedBillVO();
				String gyUserCode = gyselected.getStringValue("USERCODE");
				String gyUserName = gyselected.getStringValue("USERNAME");
				pfTime = UIUtil.getStringValueByDS(null,
						"SELECT max(PFTIME) FROM WN_GYPF_TABLE WHERE USERCODE='"
								+ gyUserCode + "'");
				String PFDEPTCODE = USERCODES.get(PFUSERDEPT).toString();
				
				String[] state = UIUtil.getStringArrayFirstColByDS(null,
						"select state from WN_GYPF_TABLE where state='评分中' AND USERCODE='"
								+ gyUserCode + "' and pftime='"+pfTime+"'");//
				if (state.length <= 0) {
					MessageBox.show(this, "当前柜员【" + gyUserName
							+ "】服务质量考核已经结束，无须重复结束！");
					return;
				};
			    final	BillVO[] bos = billListPanel_User_check.getBillVOs();
				//获取到复核人姓名，以及复核人部门
				HashMap<String, String> map = UIUtil.getHashMapBySQLByDS(null,
						"select id,name from pub_corp_dept");
				//PFUSERNAME 评分人名称
				final String pfUserDept=map.get(PFUSERDEPT);//评分人机构号
				final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
				new SplashWindow(this,new AbstractAction() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
					message=service.finishGradeScore(bos,PFUSERNAME,PFSUERCODE,pfUserDept);
						
					}
				});
				if(message.contains("成功")){
					MessageBox.show(this,message);
					billListPanel_User_check.refreshData();
				}else{
					MessageBox.show(this,message);
				}
	
			    //代码废弃 ，暂时保留
//				Double FENZHI=0.0;
//				Double KOUOFEN=0.0;
//				Double result=0.0;
//				StringBuffer sb = new StringBuffer();
//				HashMap<String, String> map = UIUtil.getHashMapBySQLByDS(null,
//						"select id,name from pub_corp_dept");
//				UpdateSQLBuilder update3 = new UpdateSQLBuilder(
//						billListPanel_User_check.getTempletVO().getTablename());
//				update3.setWhereCondition("USERCODE='" + gyUserCode
//						+ "' and PFTIME='" + pfTime + "'");
//				// MessageBox.show(this,billListPanel_User_check.getTempletVO().getTablename());
//				for (int j = 0; j < bos.length; j++) {//判断当前考核得分是否存在为空的值
//					if (bos[j].getStringValue("KOUOFEN") == null
//							|| bos[j].getStringValue("KOUOFEN").isEmpty()) {
//						sb.append("部门为["
//								+ map.get(bos[j].getStringValue("USERDEPT"))
//								+ "],柜员名称为["
//								+ bos[j].getStringValue("USERNAME")
//								+ "]评分未完成！ \n");
//					}
//				}
//				if (sb.length() > 0) {
//					if (MessageBox.confirm(this, "当前柜员【" + gyUserName
//							+ "】尚未完成,确定强制结束吗？  \n" + sb.toString())) {
//						UpdateSQLBuilder update = new UpdateSQLBuilder(
//								billListPanel_User_check.getTempletVO()
//										.getTablename());
//
//						List<String> sqlList = new ArrayList<String>();
//						UpdateSQLBuilder update2 = new UpdateSQLBuilder(
//								"WN_GYPF_TABLE");
//						for (int j = 0; j < bos.length; j++) {
//							if (bos[j].getStringValue("XIANGMU").equals("总分")) {
//								continue;
//							}
//							update.setWhereCondition(" USERCODE='" + gyUserCode
//									+ "' and PFTIME='" + pfTime
//									+ "' and zhibiao='"
//									+ bos[j].getStringValue("zhibiao") + "'");
//							// 获取到扣分项
//							FENZHI = Double.parseDouble(bos[j]
//									.getStringValue("FENZHI"));
//							if (bos[j].getStringValue("KOUOFEN") == null) {
//								KOUOFEN = 0.0;
//							}
//							KOUOFEN = Double.parseDouble(bos[j]
//									.getStringValue("KOUOFEN"));
//							if (FENZHI <= KOUOFEN || KOUOFEN != 0) {
//								KOUOFEN = FENZHI;
//							}
//							System.out.println("当前扣分项:"
//									+ bos[j].getStringValue("xiangmu") + ",扣分="
//									+ KOUOFEN);
//							result = result - KOUOFEN;
//							update.putFieldValue("KOUOFEN", KOUOFEN);
//							update.putFieldValue("PFUSERNAME", PFUSERNAME);
//							update.putFieldValue("PFSUERCODE", PFSUERCODE);
//							update.putFieldValue("PFUSERDEPT", PFDEPTCODE);
//							sqlList.add(update.getSQL());
//						}
//						UIUtil.executeBatchByDS(null, sqlList);
//						update2.setWhereCondition("USERCODE='" + gyUserCode
//								+ "' and pftime='" + pfTime
//								+ "' and xiangmu='总分'");
//						update2.putFieldValue("state", "评分结束");
//						update2.putFieldValue("kouofen", result);
//						update2.putFieldValue("PFUSERNAME", PFUSERNAME);
//						update2.putFieldValue("PFSUERCODE", PFSUERCODE);
//						update2.putFieldValue("PFUSERDEPT", PFDEPTCODE);
//						UIUtil.executeUpdateByDS(null, update2.getSQL());
//						MessageBox.show(this, "评分结束成功");
//						billListPanel_User_check.refreshData();
//					} else {
//						return;
//					}
//				} else {
//					UpdateSQLBuilder update = new UpdateSQLBuilder(
//							billListPanel_User_check.getTempletVO()
//									.getTablename());
//					List<String> sqlList = new ArrayList<String>();
//					UpdateSQLBuilder update2 = new UpdateSQLBuilder(
//							"WN_GYPF_TABLE");
//					for (int j = 0; j < bos.length; j++) {
//						update.setWhereCondition(" USERCODE='" + gyUserCode
//								+ "' and PFTIME='" + pfTime + "' and zhibiao='"
//								+ bos[j].getStringValue("zhibiao") + "'");
//						// 获取到扣分项
//						if (bos[j].getStringValue("XIANGMU").equals("总分")) {
//							continue;
//						}
//						FENZHI = Double.parseDouble(bos[j]
//								.getStringValue("FENZHI"));
//						if (bos[j].getStringValue("KOUOFEN") == null) {
//							KOUOFEN = 0.0;
//						}
//						KOUOFEN = Double.parseDouble(bos[j]
//								.getStringValue("KOUOFEN"));
//						if (FENZHI <= KOUOFEN || KOUOFEN != 0) {
//							KOUOFEN = FENZHI;
//						}
//						update.putFieldValue("state", "评分结束");
//						update.putFieldValue("KOUOFEN", KOUOFEN);
//						update.putFieldValue("PFUSERNAME", PFUSERNAME);
//						update.putFieldValue("PFSUERCODE", PFSUERCODE);
//						update.putFieldValue("PFUSERDEPT", PFDEPTCODE);
//						
//						sqlList.add(update.getSQL());
//						result = result - KOUOFEN;
//					}
//					UIUtil.executeBatchByDS(null, sqlList);
//					update2.setWhereCondition("USERCODE='" + gyUserCode
//							+ "' and pftime='" + pfTime + "' and xiangmu='总分'");
//					update2.putFieldValue("state", "评分结束");
//					update2.putFieldValue("kouofen", result);
//					update2.putFieldValue("PFUSERNAME", PFUSERNAME);
//					update2.putFieldValue("PFSUERCODE", PFSUERCODE);
//					update2.putFieldValue("PFUSERDEPT", PFDEPTCODE);
//					update2.putFieldValue("fhresult", "未复核");
//					UIUtil.executeUpdateByDS(null, update2.getSQL());
//					MessageBox.show(this, "评分结束成功");
//					billListPanel_User_check.refreshData();
//				}
			} catch (Exception eq) {
				MessageBox.show(this, "评分结束失败");
				eq.printStackTrace();
			}
		} else if (e.getSource() == btn_verify) {// 评分审核功能
			VersifySource();
		}
	}

	/**
	 * 评分复核功能
	 */
	private void VersifySource() {
		try {
			BillVO fhUser = billListPanel_User_Post.getSelectedBillVO();
			String fhUserCode = fhUser.getStringValue("USERCODE");
			HashVO[] pfNotEnd = UIUtil.getHashVoArrayByDS(null,
					"SELECT  * FROM WN_GYPF_TABLE WHERE PFTIME='" + pfTime
							+ "' and USERCODE='" + fhUserCode
							+ "' and state='评分中'");
			if (pfNotEnd.length > 0) {
				MessageBox.show(this,
						"当前柜员【" + fhUser.getStringValue("USERNAME")
								+ "】评分尚未结束，无法进行分数复核。");
				return;
			}
			pfTime = UIUtil.getStringValueByDS(null,
					"SELECT max(PFTIME) FROM WN_GYPF_TABLE WHERE USERCODE='"
							+ fhUserCode + "'");// 批复时间设置
			String count = UIUtil.getStringValueByDS(null,
					"select count(*) from WN_GYPF_TABLE where usercode='"
							+ fhUserCode + "' and pftime='" + pfTime
							+ "' and fhresult='复核通过'");
			if (Double.parseDouble(count) > 0) {
				MessageBox.show(this,
						"当前柜员【" + fhUser.getStringValue("USERNAME")
								+ "】评分复核完成,无需再次复核");
				return;
			}
			// 获取分数复核人员的信息
			int result = MessageBox.showOptionDialog(this, "当前柜员分数进行复核", "提示",
					new String[] { "复核通过", "复核退回" }, 1);
			String FHUSERNAME = PFUSERNAME;
			String FHUSERDEPT = USERCODES.get(PFUSERDEPT).toString();
			String FHTIME = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
					.format(new Date());

			UpdateSQLBuilder update = new UpdateSQLBuilder("WN_GYPF_TABLE");
			update.setWhereCondition("pfTime='" + pfTime + "' and USERCODE='"
					+ fhUserCode + "'");
			update.putFieldValue("FHUSERNAME", PFUSERNAME);
			update.putFieldValue("FHUSERDEPT", FHUSERDEPT);
			update.putFieldValue("FHTIME", FHTIME);
			if (result == 0) {// 复核通过
				// 设置复核信息
				update.putFieldValue("FHRESULT", "复核通过");
				update.putFieldValue("FHREASON", "");
				UIUtil.executeUpdateByDS(null, update.getSQL());// 执行修改
				billListPanel_User_check.setItemEditable("FHREASON", false);// 评分结束，无法修改审核不通过能容
				billListPanel_User_check.refreshData();
			} else if (result == 1) {// 复核不通过
				update.putFieldValue("FHRESULT", "复核未通过");
				String pfreason = JOptionPane.showInputDialog("请输入复核未通过的理由:");
				update.putFieldValue("FHREASON", pfreason);
				update.putFieldValue("STATE", "评分中");// 将状态从评分结束修改为评分中，由委派会计继续评分
				update.putFieldValue("PFUSERNAME", "");
				update.putFieldValue("PFSUERCODE", "");
				update.putFieldValue("PFUSERDEPT", "");
				UIUtil.executeUpdateByDS(null, update.getSQL());// 执行修改
				billListPanel_User_check.refreshData();
			} else {// 没有做任何事情
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 机构树选择事件
	 */
	@Override
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		str_currdeptid = _event.getCurrSelectedVO().getStringValue("id"); //
		str_currdeptname = _event.getCurrSelectedVO().getStringValue("name"); //
		if (billListPanel_User_Post != null) {
//			/**
//			 * 【2019-11-26】
//			 * 应客户要求，将不参与考核的人员不显示
//			 */
//			String unCheckCode="SELECT CODE FROM V_SAL_PERSONINFO WHERE ISUNCHECK='Y' AND  DEPTID ='"+str_currdeptid+"'";//获取到当前机构不参与考核的人名单 
			billListPanel_User_Post.queryDataByCondition("deptid='"
					+ str_currdeptid + "' and POSTNAME like '%柜员%' ",
					"seq,usercode"); //
		}
	}

	/**
	 * 获得机构模板
	 * 
	 * @return
	 */
	public String getCorpTempletCode() {
		return "PUB_CORP_DEPT_CODE1"; // 最简单的
	}

	@Override
	public void onBillListSelectChanged(BillListSelectionEvent e) {
		try {
			if (e.getSource() == billListPanel_User_Post) {
				BillVO vo = billListPanel_User_Post.getSelectedBillVO();
			
				
				pfTime = UIUtil.getStringValueByDS(null,
						"SELECT max(PFTIME) FROM WN_GYPF_TABLE WHERE USERCODE='"
								+ vo.getStringValue("usercode") + "'");//
				billListPanel_User_check.QueryDataByCondition("usercode='"
						+ vo.getStringValue("usercode") + "'   and pftime='"
						+ pfTime + "'");
//			MessageBox.show(this,"查询条件为:"+billListPanel_User_check.getQuickQueryPanel().getQuerySQL());
			} else if (e.getSource() == billListPanel_User_check) {
				BillVO vo = billListPanel_User_check.getSelectedBillVO();
				// 获取到当前复核人登录信息
				String userType = USERTYPE.get(PFSUERCODE).toString();
				if (vo.getStringValue("state").equals("评分结束")) {
					btn_save.setEnabled(false);// 设置"保存"功能不可用
					billListPanel_User_check.setItemEditable("KOUOFEN", false);// 评分完成之后，设置不可编辑
				} else {
					if (userType.contains("委派会计")) {
						btn_save.setEnabled(true);
						billListPanel_User_check.setItemEditable("KOUOFEN",
								true);
					} else {
						btn_save.setVisible(false);
						btn_end.setVisible(false);
						billListPanel_User_check.setItemEditable("KOUOFEN",
								false);
					}

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
