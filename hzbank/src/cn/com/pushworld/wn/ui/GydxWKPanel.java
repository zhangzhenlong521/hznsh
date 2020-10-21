package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.channels.SelectableChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;

import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;


public class GydxWKPanel extends AbstractWorkPanel implements
		BillTreeSelectListener, BillListSelectListener, ActionListener {
	// private CommDMO dmo = new CommDMO();
	private BillTreePanel billTreePanel_Dept = null;// 机构树
	private BillListPanel billListPanel_User_Post = null;// 人员表
	private BillListPanel billListPanel_User_check = null;// 柜员评分
	private WLTSplitPane splitPanel_all = null;
	private String str_currdeptid, str_currdeptname = null; // 当前机构ID及机构名称
	private WLTSplitPane splitPanel = null;
	private WLTButton btn_save, btn_end, btn_verify;// 保存评分，结束评分，评分复核
	private String PFUSERNAME = ClientEnvironment.getInstance()
			.getLoginUserName();// 登录人员名称
	private String PFSUERCODE = ClientEnvironment.getInstance()
			.getLoginUserCode();// 登录人员编码
	private String PFUSERDEPT = ClientEnvironment.getInstance()
			.getLoginUserDeptId();// 登录人员机构号
	private HashMap USERCODES = null;
	private HashMap USERTYPE = null;
	private String pfTime = null;
	private HashMap KJMAP = null;// 根据本网点的机构号获取到网点的委派会计号
	private HashMap ZRMAP = null;

	@Override
	public void initialize() {
		try {
			USERCODES = UIUtil.getHashMapBySQLByDS(null,
					"select id,code from PUB_CORP_DEPT");
			USERTYPE = UIUtil.getHashMapBySQLByDS(null,
					"select USERCODE,POSTNAME from  V_PUB_USER_POST_1 where ISDEFAULT='Y'");// 对于人员评分表，不同的人看到柜员评分表中的内容
			KJMAP = UIUtil
					.getHashMapBySQLByDS(null,
							"SELECT DEPTCODE,USERCODE FROM V_PUB_USER_POST_1 WHERE POSTNAME='委派会计'");
			ZRMAP = UIUtil
					.getHashMapBySQLByDS(
							null,
							"SELECT DEPTCODE,USERCODE FROM V_PUB_USER_POST_1 WHERE POSTNAME like '%主任%' ORDER BY POSTNAME DESC");
		} catch (Exception e) {
			e.printStackTrace();
		}
		billTreePanel_Dept = new BillTreePanel(getCorpTempletCode());// 机构树:
																		// 获取相应的数据
		billListPanel_User_Post = new BillListPanel("V_PUB_USER_POST_zpy");// 右侧上上人员表
		billListPanel_User_Post.addBillListSelectListener(this);
		billListPanel_User_check = new BillListPanel("WN_GYDX_TABLE_Q01_ZPY");// 柜员定性评分表
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
		billListPanel_User_check.addBillListButton(btn_save);
		billListPanel_User_check.addBillListButton(btn_end);
		// 复核功能只对网点主任开放，如果这个网点没有网点主任，由网点副主任进行复核
//		if ("主任".equals(USERTYPE.get(PFSUERCODE).toString())
//				|| "副主任".equals(USERTYPE.get(PFSUERCODE).toString())) {
		String curPostName=USERTYPE.get(PFSUERCODE).toString();
		if(curPostName.indexOf("主任")!=-1){
			btn_verify = new WLTButton("评分复核");
			btn_verify.addActionListener(this);
			billListPanel_User_check.addBillListButton(btn_verify);
			billListPanel_User_check.setItemEditable("fhreason", true);
			btn_save.setEnabled(false);
		} else {
			billListPanel_User_check.setItemEditable("fhreason", false);
		}
		billListPanel_User_check.repaintBillListButton();
		billListPanel_User_check.addBillListSelectListener(this);
		String PFDEPTCODE = USERCODES.get(PFUSERDEPT).toString();// 获取当前评分人的机构代码
		/**
		 * 【2019-11-26】应客户要求，将不参与考核的人不显示
		 */
		String unCheckCode="SELECT CODE FROM V_SAL_PERSONINFO WHERE ISUNCHECK='Y' ";
		billListPanel_User_Post.queryDataByCondition("deptcode='" + PFDEPTCODE
				+ "' and POSTNAME like '%柜员%' and usercode not in ("+unCheckCode+")", "seq,usercode");
		if ("282006".equals(PFDEPTCODE)) {
			splitPanel_all.add(billTreePanel_Dept);// 如果当前登录人属于运营管理部，则显示机构树
			billTreePanel_Dept.addBillTreeSelectListener(this);
		}
		splitPanel_all.add(splitPanel);
		splitPanel_all.setDividerLocation(180);
		this.add(splitPanel_all);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {// 点击保存数据
			saveScore();
		} else if (e.getSource() == btn_end) {// 点击结束评分
			endScore();
		} else if (e.getSource() == btn_verify) {// 网点主任进行复核
			verifyScore();
		}
	}

	private void saveScore() {// 点击保存评分，这个时候只计算柜员的最终得分，不修改状态。保存功能可以由委派会计和网点主任使用
		try {
			// 获取到当前选中人员的信息
			BillVO[] billVOs = billListPanel_User_check.getBillVOs();
			String deptcode = billListPanel_User_Post.getSelectedBillVO()
					.getStringValue("deptcode");
			StringBuffer sb = new StringBuffer();
			double fenzhi = 0.0;
			double koufen = 0.0;
			String fhreason = "";
			double result = 0.0;
			BillVO vov = billListPanel_User_Post.getSelectedBillVO();// 获取到当前评分柜员的信息
			if (vov == null) {
				MessageBox.show(this, "请选择一条数据操作!");
				return;
			}
			String gyusercode = vov.getStringValue("usercode");// 获取到当前柜员的登录信息
			List list = new ArrayList<String>();
			UpdateSQLBuilder update = new UpdateSQLBuilder(
					billListPanel_User_check.getTempletVO().getTablename());// 设置修改
			for (int i = 0; i < billVOs.length; i++) {
				if ("总分".equals(billVOs[i].getStringValue("xiangmu"))) {
					continue;
				}
				fenzhi = Double
						.parseDouble(billVOs[i].getStringValue("fenzhi"));// 获取到当前考核项的分值
				fhreason = billVOs[i].getStringValue("FHREASON");// 获取到复核理由
				if (billVOs[i].getStringValue("KOUFEN") != null
						&& !billVOs[i].getStringValue("KOUFEN").isEmpty()) {
					koufen = Double.parseDouble(billVOs[i]
							.getStringValue("KOUFEN"));
					if (fenzhi < koufen) {
						sb.append("第" + (i + 1) + "行数据项目为["
								+ billVOs[i].getStringValue("khsm") + "],项目为["
								+ billVOs[i].getStringValue("xiangmu")
								+ "]扣分项大于分值  \n");
					}
				} else {
					sb.append("第" + (i + 1) + "行数据项目为["
							+ billVOs[i].getStringValue("khsm") + "],项目为["
							+ billVOs[i].getStringValue("xiangmu")
							+ "]扣分项为空  \n");
				}
				result = result + koufen;
				update.setWhereCondition("usercode='"
						+ billVOs[i].getStringValue("usercode")
						+ "' and khsm='" + billVOs[i].getStringValue("khsm")
						+ "' and state='评分中'");
				update.putFieldValue("KOUFEN", koufen);
				update.putFieldValue("FHREASON", fhreason);
				list.add(update.getSQL());
			}
			if (sb.length() <= 0) {//
			// billListPanel_User_check.setRealValueAt(String.valueOf(100 -
			// result), billVOs.length - 1, "fenzhi");
				update.setWhereCondition("usercode='"
						+ vov.getStringValue("usercode")
						+ "' and state='评分中' and xiangmu='总分'");
				update.putFieldValue("fenzhi", 100.0 - result);
				list.add(update.getSQL());
				// 设置批复人的信息
				UpdateSQLBuilder update2 = new UpdateSQLBuilder(
						billListPanel_User_check.getTempletVO().getTablename());
				update2.setWhereCondition("USERCODE='" + gyusercode
						+ "' and state='评分中'");
				update2.putFieldValue("PFUSERNAME", PFUSERNAME);
				update2.putFieldValue("PFUSERCODE", PFSUERCODE);
//				update2.putFieldValue("pftime", new SimpleDateFormat(
//						"yyyy-MM-dd hh:mm:ss").format(new Date()));// 插入评分时间
				String PFDEPTCODE = USERCODES.get(PFUSERDEPT).toString();
				update2.putFieldValue("PFUSERDEPT", PFDEPTCODE);
				// MessageBox.show(this,update2.getSQL());
				list.add(update2.getSQL());
				UIUtil.executeBatchByDS(null, list);
				MessageBox.show(this, "保存完成");
				pfTime = UIUtil.getStringValueByDS(null,
						"SELECT max(PFTIME) FROM wn_gydx_table WHERE USERCODE='"
								+ gyusercode + "'");

				String sql = "select * from wn_gydx_table where usercode='"
						+ gyusercode + "' and pftime='" + pfTime
						+ "' and state='评分中' order by khsm asc";
				billListPanel_User_check.QueryData(sql);
			} else {
				MessageBox.show(this, sb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void endScore() {// 结束评分 1.计算最后总的分值；2.修改评分状态
		try {
			Double fenzhi = 0.0;
			Double koufen = 0.0;
			Double result = 100.0;
			// 获取到当前柜员的信息
			BillVO gyselected = billListPanel_User_Post.getSelectedBillVO();
			String gyUserCode = gyselected.getStringValue("USERCODE");// 获取到当前选中人的柜员号
			String gyUserName = gyselected.getStringValue("USERNAME");// 获取到当前选中柜员的姓名

			String[] state = UIUtil.getStringArrayFirstColByDS(null,
					"select state from wn_gydx_table where state='评分中' AND USERCODE='"
							+ gyUserCode + "'");
			if (state == null || state.length <= 0) {
				MessageBox.show(this, "当前柜员【" + gyUserName
						+ "】定性指标考核已经结束，无须重复结束！");
				return;
			}
			// HashVO[] vos = UIUtil.getHashVoArrayByDS(null,
			// "select USERDEPT,USERNAME,PFTIME from wn_gydx_table where 1=1 and KOUFEN is null group by USERDEPT,USERNAME,PFTIME order by USERDEPT");
			BillVO[] bos = billListPanel_User_check.getBillVOs();
			StringBuffer sb = new StringBuffer();// 获取到当前评分项
			HashMap<String, String> map = UIUtil.getHashMapBySQLByDS(null,
					"select id,code from pub_corp_dept");
			UpdateSQLBuilder update3 = new UpdateSQLBuilder(
					billListPanel_User_check.getTempletVO().getTablename());
			update3.setWhereCondition("USERCODE='" + gyUserCode
					+ "' and state='评分中'");
			for (int j = 0; j < bos.length; j++) {
				if ("总分".equals(bos[j].getStringValue("xiangmu"))) {
					continue;
				}
				if (bos[j].getStringValue("KOUFEN") == null
						|| bos[j].getStringValue("KOUFEN").isEmpty()) {
					sb.append("柜员名为[" + bos[j].getStringValue("USERNAME")
							+ "],考核项为[" + bos[j].getStringValue("xiangmu")
							+ "]评分未完成！ \n");
				}
			}
			String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
					.format(new Date());
			if (sb.length() > 0) {// 存在未评分的项
				if (MessageBox.confirm(this, "当前柜员【" + gyUserName
						+ "】尚未完成,确定强制结束吗？  \n" + sb.toString())) {
					UpdateSQLBuilder update = new UpdateSQLBuilder(
							billListPanel_User_check.getTempletVO()
									.getTablename());
					List<String> sqlList = new ArrayList<String>();
					UpdateSQLBuilder update2 = new UpdateSQLBuilder(
							billListPanel_User_check.getTempletVO()
									.getTablename());
					for (int j = 0; j < bos.length; j++) {
						if ("总分".equals(bos[j].getStringValue("xiangmu"))) {
							continue;
						}
						update.setWhereCondition(" USERCODE='" + gyUserCode
								+ "'  and khsm='"
								+ bos[j].getStringValue("khsm")
								+ "' and  state='评分中'");
						fenzhi = Double.parseDouble(bos[j]
								.getStringValue("fenzhi"));
						if (bos[j].getStringValue("KOUFEN") == null) {
							koufen = 0.0;
						}
						koufen = Double.parseDouble(bos[j]
								.getStringValue("KOUFEN"));
						if (fenzhi <= koufen) {
							koufen = fenzhi;
						}
						result = result - koufen;
						update.putFieldValue("KOUFEN", koufen);
						update.putFieldValue("state", "评分结束");
						update.putFieldValue("FHRESULT", "未复核");
						update.putFieldValue("PFUSERNAME", PFUSERNAME);
						update.putFieldValue("PFUSERCODE", PFSUERCODE);
						update.putFieldValue("PFUSERDEPT", map.get(PFUSERDEPT));
//						update.putFieldValue("pftime", time);
						sqlList.add(update.getSQL());
					}
					UIUtil.executeBatchByDS(null, sqlList);
					update2.setWhereCondition("USERCODE='" + gyUserCode
							+ "' and state='评分中' and xiangmu='总分'");
					update2.putFieldValue("state", "评分结束");
					update2.putFieldValue("fenzhi", result);
					update2.putFieldValue("PFUSERNAME", PFUSERNAME);
					update2.putFieldValue("PFUSERCODE", PFSUERCODE);
					update2.putFieldValue("PFUSERDEPT", map.get(PFUSERDEPT));
//					update2.putFieldValue("pftime", time);
					update2.putFieldValue("koufen", "");
					UIUtil.executeUpdateByDS(null, update2.getSQL());
					MessageBox.show(this, "评分结束成功");
					// billListPanel_User_check.refreshData();
				} else {
					return;
				}
			} else {
				UpdateSQLBuilder update = new UpdateSQLBuilder(
						billListPanel_User_check.getTempletVO().getTablename());
				List<String> sqlList = new ArrayList<String>();
				UpdateSQLBuilder update2 = new UpdateSQLBuilder(
						billListPanel_User_check.getTempletVO().getTablename());
				for (int j = 0; j < bos.length; j++) {
					if (bos[j].getStringValue("XIANGMU").equals("总分")) {
						continue;
					}
					update.setWhereCondition(" USERCODE='" + gyUserCode
							+ "'  and khsm='" + bos[j].getStringValue("khsm")
							+ "'  and state='评分中'");
					fenzhi = Double
							.parseDouble(bos[j].getStringValue("FENZHI"));
					if (bos[j].getStringValue("KOUFEN") == null) {
						koufen = 0.0;
					}
					koufen = Double
							.parseDouble(bos[j].getStringValue("KOUFEN"));
					if (fenzhi <= koufen) {
						koufen = fenzhi;
					}
					update.putFieldValue("state", "评分结束");
					update.putFieldValue("KOUFEN", koufen);
					update.putFieldValue("PFUSERNAME", PFUSERNAME);
					update.putFieldValue("PFUSERCODE", PFSUERCODE);
					update.putFieldValue("PFUSERDEPT", map.get(PFUSERDEPT));
//					update.putFieldValue("pftime", time);
					sqlList.add(update.getSQL());
					result = result - koufen;
				}
				UIUtil.executeBatchByDS(null, sqlList);
				update2.setWhereCondition("USERCODE='" + gyUserCode
						+ "' and state='评分中' and xiangmu='总分'");
				update2.putFieldValue("state", "评分结束");
				update2.putFieldValue("fenzhi", result);
				update2.putFieldValue("koufen", "");
				update2.putFieldValue("PFUSERNAME", PFUSERNAME);
				update2.putFieldValue("PFUSERCODE", PFSUERCODE);
				update2.putFieldValue("PFUSERDEPT", map.get(PFUSERDEPT));
//				update2.putFieldValue("pftime", time);
				UIUtil.executeUpdateByDS(null, update2.getSQL());
				MessageBox.show(this, "评分结束成功");
				pfTime = UIUtil.getStringValueByDS(null,
						"SELECT max(PFTIME) FROM wn_gydx_table WHERE USERCODE='"
								+ gyUserCode + "'");
				String sql = "select * from wn_gydx_table where usercode='"
						+ gyUserCode + "' and pftime='" + pfTime + "' ";
				billListPanel_User_check.QueryData(sql);
				// billListPanel_User_check.refreshData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void verifyScore() {// 网点主任对评分进行复核,复核结果分为:复核通过，复核不通过
		try {
			BillVO fhUser = billListPanel_User_Post.getSelectedBillVO();
			String fhUserCode = fhUser.getStringValue("USERCODE");
			pfTime = UIUtil.getStringValueByDS(null,
					"SELECT max(PFTIME) FROM wn_gydx_table WHERE USERCODE='"
							+ fhUserCode + "'");// 批复时间设置
			HashVO[] pfNotEnd = UIUtil.getHashVoArrayByDS(null,
					"SELECT  * FROM wn_gydx_table WHERE  USERCODE='"
							+ fhUserCode + "' and state='评分中'");
			if (pfNotEnd.length > 0) {
				MessageBox.show(this,
						"当前柜员【" + fhUser.getStringValue("USERNAME")
								+ "】评分尚未结束，无法进行分数复核。");
				return;
			}
			// 判断当前柜员复核是否通过，对于复核通过的柜员无需再次进行复核;
			HashVO[] pfsuccess = UIUtil.getHashVoArrayByDS(null,
					"SELECT  * FROM wn_gydx_table WHERE  USERCODE='"
							+ fhUserCode
							+ "' and fhresult='复核通过'  and pftime='" + pfTime
							+ "'");
			if (pfsuccess.length > 0) {
				MessageBox.show(this,
						"当前柜员【" + fhUser.getStringValue("USERNAME")
								+ "】复核已经完成或当前评分尚未结束，请勿重复操作。");
				return;
			}
			int result = MessageBox.showOptionDialog(this, "当前柜员分数进行复核", "提示",
					new String[] { "复核通过", "复核退回" }, 1);
			String FHUSERNAME = PFUSERNAME;
			String FHUSERDEPT = USERCODES.get(PFUSERDEPT).toString();
			String FHTIME = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());
			UpdateSQLBuilder update = new UpdateSQLBuilder("wn_gydx_table");
			update.setWhereCondition("pfTime='" + pfTime + "' and USERCODE='"
					+ fhUserCode + "'");
			update.putFieldValue("FHUSERNAME", PFUSERNAME);
			update.putFieldValue("FHUSERDEPT", FHUSERDEPT);
			update.putFieldValue("FHTIME", FHTIME);
			if (result == 0) {
				update.putFieldValue("FHRESULT", "复核通过");
				update.putFieldValue("fhreason", "");
				UIUtil.executeUpdateByDS(null, update.getSQL());// 执行修改
				billListPanel_User_check.setItemEditable("FHREASON", false);// 评分结束，无法修改审核不通过能容
				billListPanel_User_check.refreshData();
			} else if (result == 1) {
				update.putFieldValue("FHRESULT", "复核未通过");
				String pfreason = JOptionPane.showInputDialog("请输入复核未通过的理由:");
				update.putFieldValue("FHREASON", pfreason);
				update.putFieldValue("STATE", "评分中");// 将状态从评分结束修改为评分中，由委派会计继续评分
				update.putFieldValue("PFUSERNAME", "");
				update.putFieldValue("PFUSERCODE", "");
				update.putFieldValue("PFUSERDEPT", "");
				UIUtil.executeUpdateByDS(null, update.getSQL());// 执行修改
				billListPanel_User_check.refreshData();
			} else {
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBillListSelectChanged(BillListSelectionEvent e) {
		try {
			if (e.getSource() == billListPanel_User_Post) {
				BillVO vo = billListPanel_User_Post.getSelectedBillVO();
				pfTime = UIUtil.getStringValueByDS(null,
						"select max(pftime) from  WN_GYDX_TABLE where usercode='"
								+ vo.getStringValue("usercode") + "'");
				String sql = "select * from wn_gydx_table where 1=1 and usercode='"
						+ vo.getStringValue("usercode")
						+ "' and (pftime='"
						+ pfTime + "')  order by xiangmu";
				billListPanel_User_check.QueryData(sql);
			}
			if (e.getSource() == billListPanel_User_check) {
				BillVO vo = billListPanel_User_check.getSelectedBillVO();
				if (vo.getStringValue("state").equals("评分结束")) {
					btn_save.setEnabled(false);
					btn_end.setEnabled(false);
					billListPanel_User_check.setItemEditable("KOUFEN", false);// 评分完成之后，设置不可编辑
				} else {
					// 获取到当前柜员的网点号
					String deptcode = billListPanel_User_Post
							.getSelectedBillVO().getStringValue("deptcode");
					// 获取到当前机构的委派会计的机构号
					if (PFSUERCODE.equals(KJMAP.get(deptcode))) {// 保证登录人员是委派会计的情况下允许评分
						billListPanel_User_check
								.setItemEditable("KOUFEN", true);
						btn_end.setEnabled(true);
						btn_save.setEnabled(true);
					} else {
						billListPanel_User_check.setItemEditable("KOUFEN",
								false);
						btn_end.setEnabled(false);
						btn_save.setEnabled(false);
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		str_currdeptid = _event.getCurrSelectedVO().getStringValue("id"); //
		str_currdeptname = _event.getCurrSelectedVO().getStringValue("name"); //
		if (billListPanel_User_Post != null) {
			billListPanel_User_Post.queryDataByCondition("deptid='"
					+ str_currdeptid + "' and POSTNAME like '%柜员%'",
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
}
