package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import com.ibm.db2.jcc.a.de;

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

public class BMqualitativeScorePanel extends AbstractWorkPanel implements
		BillTreeSelectListener, BillListSelectListener, ActionListener {
	private BillTreePanel billTreePanel_Dept = null;// 机构树
	private BillListPanel billListPanel_Dept_check = null;// 部门评分
	private WLTSplitPane splitPanel_all = null;// 左右分割
	private WLTButton btn_save, btn_end;// 保存和结束按钮
	private String PFUSERNAME = ClientEnvironment.getInstance()
			.getLoginUserName();
	private String PFSUERCODE = ClientEnvironment.getInstance()
			.getLoginUserCode();
	private String PFUSERDEPT = ClientEnvironment.getInstance()
			.getLoginUserDeptId();
	private String PFUSERDEPTNAME = ClientEnvironment.getInstance()
			.getLoginUserDeptName();

	@Override
	public void initialize() {
		billTreePanel_Dept = new BillTreePanel("PUB_CORP_DEPT_CODE1");
		billListPanel_Dept_check = new BillListPanel("WN_BMPF_TABLE_Q01_ZPY");
		btn_save = new WLTButton("保存");
		btn_save.addActionListener(this);
		btn_end = new WLTButton("结束");
		btn_end.addActionListener(this);
		billListPanel_Dept_check.addBatchBillListButton(new WLTButton[] {
				btn_save, btn_end });
		billListPanel_Dept_check.repaintBillListButton();
		billTreePanel_Dept.addBillTreeSelectListener(this);
		billListPanel_Dept_check.addBillListSelectListener(this);
		splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT);
		billTreePanel_Dept.queryDataByCondition("1=1");
		billTreePanel_Dept
				.queryDataByCondition("CORPTYPE!='总行' AND CORPTYPE!='集团' AND CORPTYPE!='母行' ");
		splitPanel_all.add(billTreePanel_Dept);
		splitPanel_all.add(billListPanel_Dept_check);
		this.add(splitPanel_all);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {// 保存
			try {
				StringBuffer sb = new StringBuffer("");
				BillVO[] bos = billListPanel_Dept_check.getBillVOs();
				Double FENZHI = 0.0;
				Double KOUOFEN = 0.0;
				Double result = 0.0;
				int count = 1;
				List list = new ArrayList<String>();
				UpdateSQLBuilder update = new UpdateSQLBuilder(
						billListPanel_Dept_check.getTempletVO().getTablename());
				String deptN = "";// 负责获取到该项部门最后计算总分的项
				String khDeptName = "";
				if ("客户服务部".equals(PFUSERDEPTNAME)) {
					deptN = "文明客户服务部";
				} else if ("党建工作".equals(PFUSERDEPTNAME)) {
					deptN = "党建工作";
				} else if ("安全保卫部".equals(PFUSERDEPTNAME)) {
					deptN = "安全保卫";
				} else if ("内控合规部".equals(PFUSERDEPTNAME)) {
					deptN = "案件防控";
				}

				for (int i = 0; i <= bos.length - 1; i++) {
					System.out.println("当前考核项:"
							+ bos[i].getStringValue("xiangmu"));
					if (deptN.equals(bos[i].getStringValue("xiangmu"))) {
						continue;
					}
					khDeptName = bos[i].getStringValue("deptname");
					FENZHI = Double
							.parseDouble(bos[i].getStringValue("FENZHI"));
					if (bos[i].getStringValue("KOUFEN") != null) {
						KOUOFEN = Double.parseDouble(bos[i]
								.getStringValue("KOUFEN"));
						if (FENZHI < KOUOFEN) {
							sb.append("第" + (count) + "行数据项目为["
									+ bos[i].getStringValue("XIANGMU")
									+ "]扣分项大于分值   \n");
						}
					} else {
						sb.append("第" + (count) + "行数据项目为["
								+ bos[i].getStringValue("XIANGMU")
								+ "]扣分项为空   \n");
					}
					result = result + KOUOFEN;
					count = count + 1;
					update.setWhereCondition("id='"
							+ bos[i].getStringValue("id") + "'");
					update.putFieldValue("KOUFEN", KOUOFEN);
					list.add(update.getSQL());
				}
				if (sb.length() > 0) {
					MessageBox.show(this, sb.toString());
				} else {
					billListPanel_Dept_check.setRealValueAt(
							String.valueOf(100 - result), bos.length - 1,
							"KOUFEN");
					update.setWhereCondition("xiangmu='"
							+ bos[0].getStringValue("xiangmu")
							+ "' and deptname='"
							+ bos[0].getStringValue("deptname") + "'");
					update.putFieldValue("KOUFEN", 100.0 - result);
					list.add(update.getSQL());
					UpdateSQLBuilder update2 = new UpdateSQLBuilder(
							billListPanel_Dept_check.getTempletVO()
									.getTablename());
					String checkName = bos[1].getStringValue("xiangmu");
					update2.setWhereCondition("xiangmu like'"
							+ checkName.substring(0, checkName.indexOf("-"))
							+ "%' and deptname='"
							+ bos[1].getStringValue("deptname") + "'");
					update2.putFieldValue("PFUSERNAME", PFUSERNAME);
					update2.putFieldValue("PFSUERCODE", PFSUERCODE);
					update2.putFieldValue("PFUSERDEPT", PFUSERDEPT);
					list.add(update2.getSQL());
					UIUtil.executeBatchByDS(null, list);
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} else if (e.getSource() == btn_end) {// 结束
			try {
				String khDeptName = "";
				String[] state = UIUtil.getStringArrayFirstColByDS(null,
						"select state from wn_BMPF_table where state='评分中'");
				if (state.length <= 0) {
					MessageBox.show(this, "此次评分已全部结束，无须重复结束！");
					return;
				}
				BillVO vo = billTreePanel_Dept.getSelectedVO();
				BillVO[] bos = billListPanel_Dept_check.getBillVOs();
				String sql = "select * from wn_BMPF_table where 1=1 and deptname='"
						+ vo.getStringValue("name") + "' and state='评分中' ";
				String deptN = "";
				if ("客户服务部".equals(PFUSERDEPTNAME)) {
					sql = sql + " and  xiangmu like '文明客户服务%'";
					deptN = "文明客户服务部";
				} else if ("党委办公室".equals(PFUSERDEPTNAME)) {
					sql = sql + " and xiangmu like '党建工作%'";
					deptN = "党建工作";
				} else if ("安全保卫部".equals(PFUSERDEPTNAME)) {
					sql = sql + " and xiangmu like '安全保卫%'";
					deptN = "安全保卫";
				} else if ("内控合规部".equals(PFUSERDEPTNAME)) {
					sql = sql + " and xiangmu like '案件防控%'";
					deptN = "案件防控";
				}
				String _sql = sql + " and KOUFEN IS NULL";
				HashVO[] vos = UIUtil.getHashVoArrayByDS(null, _sql);// 查询当前部门尚未评分项
				if (vos.length > 0) {// 存在未评分的项
					if (MessageBox.confirm(this, "以下评分未完成,确定强制结束吗?")) {
						List<String> list = new ArrayList<String>();
						double result = 0.0;
						for (int i = 0; i < bos.length; i++) {
							if (deptN.equals(bos[i].getStringValue("xiangmu"))) {
								continue;
							}
							UpdateSQLBuilder update = new UpdateSQLBuilder(
									billListPanel_Dept_check.getTempletVO()
											.getTablename());
							update.setWhereCondition("1=1 and xiangmu ='"
									+ bos[i].getStringValue("xiangmu")
									+ "' AND DEPTNAME='"
									+ bos[i].getStringValue("deptname")
									+ "' and state='评分中'");
							String FENZHI = bos[i].getStringValue("fenzhi");
							String KOUFEN = bos[i].getStringValue("KOUFEN");
							String mention = bos[i].getStringValue("mention");
							if (KOUFEN == null || "".equals(KOUFEN)
									|| KOUFEN.isEmpty()) {
								KOUFEN = "0.0";
							}
							if (Double.parseDouble(KOUFEN) > Double
									.parseDouble(KOUFEN)) {
								KOUFEN = FENZHI;
							}
							result = result + Double.parseDouble(KOUFEN);
							update.putFieldValue("state", "评分结束");
							update.putFieldValue("PFSUERCODE", PFSUERCODE);
							update.putFieldValue("PFUSERDEPT", PFUSERDEPT);
							update.putFieldValue("KOUFEN", KOUFEN);
							update.putFieldValue("mention", mention);
							list.add(update.getSQL());
						}
						UpdateSQLBuilder update2 = new UpdateSQLBuilder(
								billListPanel_Dept_check.getTempletVO()
										.getTablename());
						update2.setWhereCondition("1=1 and xiangmu ='" + deptN
								+ "' and  DEPTNAME='"
								+ vo.getStringValue("name")
								+ "' and FENZHI IS NULL");
						update2.putFieldValue("KOUFEN", 100.0 - result);
						update2.putFieldValue("state", "评分结束");
						list.add(update2.getSQL());
						UIUtil.executeBatchByDS(null, list);
					} else {
						return;
					}
				} else {// 不存在未评分的项,直接结束
					double result = 0.0;
					List<String> list = new ArrayList<String>();
					UpdateSQLBuilder update = new UpdateSQLBuilder(
							billListPanel_Dept_check.getTempletVO()
									.getTablename());
					for (int i = 0; i < bos.length; i++) {
						if (deptN.equals(bos[i].getStringValue("xiangmu"))) {
							continue;
						}
						khDeptName = bos[i].getStringValue("DEPTNAME");
						String FENZHI = bos[i].getStringValue("fenzhi");
						String KOUFEN = bos[i].getStringValue("KOUFEN");
						String mention = bos[i].getStringValue("mention");
						if (KOUFEN == null || "".equals(KOUFEN)
								|| KOUFEN.isEmpty()) {
							KOUFEN = "0.0";
						}
						if (Double.parseDouble(KOUFEN) > Double
								.parseDouble(FENZHI)) {
							KOUFEN = FENZHI;
						}
						System.out.println(bos[i].getStringValue("xiangmu")
								+ ":" + KOUFEN);

						update.setWhereCondition("1=1 and xiangmu='"
								+ bos[i].getStringValue("xiangmu")
								+ "' AND DEPTNAME='"
								+ bos[i].getStringValue("deptname")
								+ "'and state='评分中'");
						update.putFieldValue("PFUSERNAME", PFUSERNAME);
						update.putFieldValue("PFSUERCODE", PFSUERCODE);
						update.putFieldValue("PFUSERDEPT", PFUSERDEPT);
						update.putFieldValue("KOUFEN", KOUFEN);
						update.putFieldValue("state", "评分结束");
						update.putFieldValue("mention", mention);
						result = result + Double.parseDouble(KOUFEN);
						list.add(update.getSQL());
					}
					UpdateSQLBuilder update2 = new UpdateSQLBuilder(
							billListPanel_Dept_check.getTempletVO()
									.getTablename());
					update2.setWhereCondition("1=1 and xiangmu='" + deptN
							+ "' and  DEPTNAME='" + vo.getStringValue("name")
							+ "' and  FENZHI IS NULL");
					update2.putFieldValue("KOUFEN", 100.0 - result);
					update2.putFieldValue("state", "评分结束");
					list.add(update2.getSQL());
					UIUtil.executeBatchByDS(null, list);
					list.clear();
				}
				String maxTime = UIUtil.getStringValueByDS(null,
						"select MAX(PFTIME) FROM WN_BMPF_TABLE ");

				billListPanel_Dept_check.queryDataByCondition(
						"1=1 and state='评分结束' and deptname='" + khDeptName
								+ "' and xiangmu like '" + deptN
								+ "%' and PFTIME='" + maxTime + "'", "fenzhi");
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

	}

	@Override
	public void onBillListSelectChanged(BillListSelectionEvent e) {

	}

	@Override
	public void onBillTreeSelectChanged(BillTreeSelectionEvent e) {
		try {
			String deptName = ClientEnvironment.getInstance()
					.getLoginUserDeptName();// 当前登录人机构
			String selectedDeptName = e.getCurrSelectedVO().getStringValue(
					"name");// 获取到选中部门
			System.out.println("部门:" + selectedDeptName);
			String maxTime = UIUtil.getStringValueByDS(null,
					"select MAX(PFTIME) FROM WN_BMPF_TABLE");
			if ("客户服务部".equals(deptName)) {
				billListPanel_Dept_check.queryDataByCondition(
						"1=1 and deptname='" + selectedDeptName
								+ "' and xiangmu like '文明客户服务部%'  and PFTIME='"
								+ maxTime + "' ", "xiangmu");

				System.out.println("查询的SQL:"
						+ billListPanel_Dept_check.getQuickQueryPanel()
								.getQuerySQL());
				String state = UIUtil.getStringValueByDS(null,
						"select state from WN_BMPF_TABLE WHERE deptname='"
								+ selectedDeptName
								+ "' and xiangmu like '文明客户服务部%'  and PFTIME='"
								+ maxTime + "' AND  ROWNUM=1");

				if ("评分结束".equals(state)) {
					billListPanel_Dept_check.setItemEditable("KOUFEN", false);
					billListPanel_Dept_check.setItemEditable("mention", false);
				} else {
					billListPanel_Dept_check.setItemEditable("KOUFEN", true);
					billListPanel_Dept_check.setItemEditable("mention", true);
				}
			} else if ("内控合规部".equals(deptName)) {
				billListPanel_Dept_check.queryDataByCondition(
						"1=1 and deptname='" + selectedDeptName
								+ "' and xiangmu like '案件防控%' and PFTIME='"
								+ maxTime + "'", "xiangmu");
				String state = UIUtil.getStringValueByDS(null,
						"select state from WN_BMPF_TABLE WHERE deptname='"
								+ selectedDeptName
								+ "' and xiangmu like '案件防控%'  and PFTIME='"
								+ maxTime + "' AND  ROWNUM=1");
				if ("评分结束".equals(state)) {
					billListPanel_Dept_check.setItemEditable("KOUFEN", false);
					billListPanel_Dept_check.setItemEditable("mention", false);
				} else {
					billListPanel_Dept_check.setItemEditable("KOUFEN", true);
					billListPanel_Dept_check.setItemEditable("mention", true);
				}
			} else if ("安全保卫部".equals(deptName)) {
				billListPanel_Dept_check.queryDataByCondition(
						"1=1 and deptname='" + selectedDeptName
								+ "' and xiangmu like '安全保卫%' and  PFTIME='"
								+ maxTime + "'", "xiangmu");
				String state = UIUtil.getStringValueByDS(null,
						"select state from WN_BMPF_TABLE WHERE deptname='"
								+ selectedDeptName
								+ "' and xiangmu like '安全保卫%'  and PFTIME='"
								+ maxTime + "' AND  ROWNUM=1");
				if ("评分结束".equals(state)) {
					billListPanel_Dept_check.setItemEditable("KOUFEN", false);
					billListPanel_Dept_check.setItemEditable("mention", false);
				} else {
					billListPanel_Dept_check.setItemEditable("KOUFEN", true);
					billListPanel_Dept_check.setItemEditable("mention", true);
				}
			} else if ("党委办公室".equals(deptName)) {
				billListPanel_Dept_check.queryDataByCondition(
						"1=1 and deptname='" + selectedDeptName
								+ "' and xiangmu like '党建%' and PFTIME='"
								+ maxTime + "'", "xiangmu");
				String state = UIUtil.getStringValueByDS(null,
						"select state from WN_BMPF_TABLE WHERE deptname='"
								+ selectedDeptName
								+ "' and xiangmu like '党建%'  and PFTIME='"
								+ maxTime + "' AND  ROWNUM=1");
				if ("评分结束".equals(state)) {
					billListPanel_Dept_check.setItemEditable("KOUFEN", false);
					billListPanel_Dept_check.setItemEditable("mention", false);
				} else {
					billListPanel_Dept_check.setItemEditable("KOUFEN", true);
					billListPanel_Dept_check.setItemEditable("mention", true);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}