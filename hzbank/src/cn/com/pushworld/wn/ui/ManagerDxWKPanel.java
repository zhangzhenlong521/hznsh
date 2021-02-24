package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
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
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

public class ManagerDxWKPanel extends AbstractWorkPanel implements
		BillTreeSelectListener, BillListSelectListener, ActionListener {

	private CommDMO dmo = new CommDMO();
	private BillListPanel billListPanel_User_Post = null;// 人员表
	private BillListPanel billListPanel_User_check = null;// 柜员评分
	private WLTButton btn_save, btn_end;// 保存评分，结束评分
	private String PFUSERNAME = ClientEnvironment.getInstance()
			.getLoginUserName();// 登录人员名称
	private String PFSUERCODE = ClientEnvironment.getInstance()
			.getLoginUserCode();// 登录人员编码
	private String PFUSERDEPT = ClientEnvironment.getInstance()
			.getLoginUserDeptId();// 登录人员机构号
	private WLTSplitPane splitPanel = null;
	private HashMap USERCODES = null;

	@Override
	public void initialize() {
		try {
			USERCODES = UIUtil.getHashMapBySQLByDS(null,
					"select id,code from PUB_CORP_DEPT");
		} catch (Exception e) {
			e.printStackTrace();
		}
		billListPanel_User_Post = new BillListPanel("V_PUB_USER_POST_zpy");// 右侧上上人员表
		billListPanel_User_Post.addBillListSelectListener(this);
		billListPanel_User_check = new BillListPanel(
				"WN_MANAGERDX_TABLE_Q01_ZPY");// 客户经理定性评分表
		splitPanel = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,
				billListPanel_User_Post, billListPanel_User_check);// 上下拆分
		splitPanel.setDividerLocation(200);
		btn_end = new WLTButton("结束评分");
		btn_save = new WLTButton("保存");
		btn_save.addActionListener(this);
		btn_end.addActionListener(this);
		billListPanel_User_check.addBillListButton(btn_save);
		billListPanel_User_check.addBillListButton(btn_end);
		billListPanel_User_check.repaintBillListButton();
		billListPanel_User_check.addBillListSelectListener(this);
		String PFDEPTCODE = USERCODES.get(PFUSERDEPT).toString();// 获取当前评分人的机构代码
		billListPanel_User_Post.queryDataByCondition("deptcode='" + PFDEPTCODE
				+ "' and POSTNAME like '%客户经理%'", "seq,usercode");
		this.add(splitPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {
			saveScore();// 保存分数
		} else if (e.getSource() == btn_end) {
			endScore();// 结束当前柜员的评分
		}
	}

	private void saveScore() {
		try {
			BillVO[] billVOs = billListPanel_User_check.getBillVOs();// 获取到当前页面全部评分项
			double fenzhi = 0.0;
			double koufen = 0.0;
			double result = 0.0;
			BillVO selectedBillVO = billListPanel_User_Post.getSelectedBillVO();
			String usercode = selectedBillVO.getStringValue("usercode");
			UpdateSQLBuilder update = new UpdateSQLBuilder("wn_managerdx_table");
			StringBuffer str = new StringBuffer();
			for (int i = 0; i < billVOs.length; i++) {
				if ("总分".equals(billVOs[i].getStringValue("xiangmu"))) {
					continue;
				}
				if (billVOs[i].getStringValue("koufen") == null
						|| billVOs[i].getStringValue("koufen").isEmpty()) {
					str.append("当前考核客户经理【"
							+ billVOs[i].getStringValue("username") + "】中考核项为【"
							+ billVOs[i].getStringValue("xiangmu")
							+ "】扣分项为空,请修正");
				} else {
					fenzhi = Double.parseDouble(billVOs[i]
							.getStringValue("fenzhi"));
					koufen = Double.parseDouble(billVOs[i]
							.getStringValue("koufen"));
					if (fenzhi < koufen) {
						str.append("当前考核客户经理【"
								+ billVOs[i].getStringValue("username")
								+ "】中考核项为【"
								+ billVOs[i].getStringValue("xiangmu")
								+ "】扣分超过此项分值,请修正");
					}
				}
			}
			if (str != null && str.length() > 0) {
				MessageBox.show(this, str.toString());
				return;
			}
			List<String> _sqllist = new ArrayList<String>();
			for (int i = 0; i < billVOs.length; i++) {
				String xiangmu = billVOs[i].getStringValue("xiangmu");
				if ("总分".equals(xiangmu)) {
					continue;
				}
				String khsm = billVOs[i].getStringValue("khsm");
				koufen = Double
						.parseDouble(billVOs[i].getStringValue("koufen"));
				result = result + koufen;
				update.setWhereCondition("usercode='" + usercode
						+ "' and khsm='" + khsm + "' and state='评分中'");
				update.putFieldValue("koufen", koufen);
				update.putFieldValue("pfusername", PFUSERNAME);
				update.putFieldValue("pfusercode", PFSUERCODE);
				update.putFieldValue("pfuserdept", USERCODES.get(PFUSERDEPT)
						.toString());
//				update.putFieldValue("pftime", new SimpleDateFormat(
//						"yyyy-MM-dd hh:mm:ss").format(new Date()));
				_sqllist.add(update.getSQL());
			}
			UpdateSQLBuilder update2 = new UpdateSQLBuilder(
					"wn_managerdx_table");
			update2.setWhereCondition("usercode='" + usercode
					+ "' and khsm='总分' and state='评分中'");
			update2.putFieldValue("fenzhi", 100.0 - result);
			update2.putFieldValue("pfusername", PFUSERNAME);
			update2.putFieldValue("pfusercode", PFSUERCODE);
			update2.putFieldValue("pfuserdept", USERCODES.get(PFUSERDEPT)
					.toString());
//			update2.putFieldValue("pftime", new SimpleDateFormat(
//					"yyyy-MM-dd hh:mm:ss").format(new Date()));
			_sqllist.add(update2.getSQL());
			UIUtil.executeBatchByDS(null, _sqllist);
			String sql = "select * from wn_managerdx_table where usercode='"
					+ usercode + "' and state='评分中'";
			billListPanel_User_check.QueryData(sql);
			MessageBox.show(this, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void endScore() {
		try {
			BillVO selectedBillVO = billListPanel_User_Post.getSelectedBillVO();
			String usercode = selectedBillVO.getStringValue("usercode");
			// 判断当前客户经理是否已经结束评分
			String[] state = UIUtil.getStringArrayFirstColByDS(null,
					"select state from wn_managerdx_table where state='评分中' AND USERCODE='"
							+ usercode + "'");
			if (state == null || state.length <= 0) {
				MessageBox.show(this, "当前客户经理【" + usercode
						+ "】定性指标考核已经结束，无须重复结束！");
				return;
			}
			BillVO[] bos = billListPanel_User_check.getBillVOs();
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < bos.length; j++) {
				String xiangmu = bos[j].getStringValue("xiangmu");
				if ("总分".equals(xiangmu)) {
					continue;
				}
				if (bos[j].getStringValue("KOUFEN") == null
						|| bos[j].getStringValue("KOUFEN").isEmpty()) {
					sb.append("客户经理名为[" + bos[j].getStringValue("USERNAME")
							+ "],考核项为[" + bos[j].getStringValue("xiangmu")
							+ "]评分未完成！ \n");
				}
			}
			if (sb.length() > 0) {
				if (MessageBox.confirm(this, "当前柜员【" + usercode
						+ "】尚未完成,确定强制结束吗？  \n" + sb.toString())) {
					double fenzhi = 0.0;
					double koufen = 0.0;
					double result = 0.0;
					UpdateSQLBuilder update = new UpdateSQLBuilder(
							billListPanel_User_check.getTempletVO()
									.getTablename());
					List<String> list = new ArrayList<String>();
					for (int i = 0; i < bos.length; i++) {// 保存每一项的明细
						String xiangmu = bos[i].getStringValue("xiangmu");
						if ("总分".equals(xiangmu)) {
							continue;
						}
						if (bos[i].getStringValue("koufen") == null) {
							koufen = 0.0;
						}
						koufen = Double.parseDouble(bos[i]
								.getStringValue("koufen"));
						fenzhi = Double.parseDouble(bos[i]
								.getStringValue("fenzhi"));
						if (fenzhi < koufen) {
							koufen = fenzhi;
						}
						result = result + koufen;
						update.setWhereCondition("usercode='" + usercode
								+ "' and state='评分中' and khsm='"
								+ bos[i].getStringValue("khsm") + "'");
						update.putFieldValue("state", "评分结束");
						update.putFieldValue("koufen", koufen);
						update.putFieldValue("pfusername", PFUSERNAME);
						update.putFieldValue("pfusercode", PFSUERCODE);
						update.putFieldValue("pfuserdept",
								USERCODES.get(PFUSERDEPT).toString());
//						update.putFieldValue("pftime", new SimpleDateFormat(
//								"yyyy-MM-dd hh:mm:ss").format(new Date()));
						list.add(update.getSQL());
					}
					UpdateSQLBuilder update2 = new UpdateSQLBuilder(
							billListPanel_User_check.getTempletVO()
									.getTablename());
					update2.setWhereCondition("usercode='" + usercode
							+ "' and state='评分中' and khsm='总分'");
					update2.putFieldValue("pfusername", PFUSERNAME);
					update2.putFieldValue("pfusercode", PFSUERCODE);
					update2.putFieldValue("pfuserdept",
							USERCODES.get(PFUSERDEPT).toString());
//					update2.putFieldValue("pftime", new SimpleDateFormat(
//							"yyyy-MM-dd hh:mm:ss").format(new Date()));
					update2.putFieldValue("state", "评分结束");
					update2.putFieldValue("fenzhi", 100.0 - result);
					list.add(update2.getSQL());
					UIUtil.executeBatchByDS(null, list);
				} else {
					return;
				}
			} else {
				double fenzhi = 0.0;
				double koufen = 0.0;
				double result = 0.0;
				UpdateSQLBuilder update = new UpdateSQLBuilder(
						billListPanel_User_check.getTempletVO().getTablename());
				List<String> list = new ArrayList<String>();
				for (int i = 0; i < bos.length; i++) {// 保存每一项的明细
					String xiangmu = bos[i].getStringValue("xiangmu");
					if ("总分".equals(xiangmu)) {
						continue;
					}
					if (bos[i].getStringValue("koufen") == null) {
						koufen = 0.0;
					}
					koufen = Double
							.parseDouble(bos[i].getStringValue("koufen"));
					fenzhi = Double
							.parseDouble(bos[i].getStringValue("fenzhi"));
					if (fenzhi < koufen) {
						koufen = fenzhi;
					}
					result = result + koufen;
					update.setWhereCondition("usercode='" + usercode
							+ "' and state='评分中' and khsm='"
							+ bos[i].getStringValue("khsm") + "'");
					update.putFieldValue("state", "评分结束");
					update.putFieldValue("koufen", koufen);
					update.putFieldValue("pfusername", PFUSERNAME);
					update.putFieldValue("pfusercode", PFSUERCODE);
					update.putFieldValue("pfuserdept", USERCODES
							.get(PFUSERDEPT).toString());
//					update.putFieldValue("pftime", new SimpleDateFormat(
//							"yyyy-MM-dd hh:mm:ss").format(new Date()));
					list.add(update.getSQL());
				}
				UpdateSQLBuilder update2 = new UpdateSQLBuilder(
						billListPanel_User_check.getTempletVO().getTablename());
				update2.setWhereCondition("usercode='" + usercode
						+ "' and state='评分中' and khsm='总分'");
				update2.putFieldValue("pfusername", PFUSERNAME);
				update2.putFieldValue("pfusercode", PFSUERCODE);
				update2.putFieldValue("pfuserdept", USERCODES.get(PFUSERDEPT)
						.toString());
//				update2.putFieldValue("pftime", new SimpleDateFormat(
//						"yyyy-MM-dd hh:mm:ss").format(new Date()));
				update2.putFieldValue("state", "评分结束");
				update2.putFieldValue("fenzhi", 100.0 - result);
				list.add(update2.getSQL());
				UIUtil.executeBatchByDS(null, list);
			}
			String pftime = UIUtil.getStringValueByDS(null,
					"select max(pftime) from wn_managerdx_table where usercode='"
							+ usercode + "'");
			billListPanel_User_check
					.QueryData("select * from wn_managerdx_table where usercode='"
							+ usercode + "' and pftime='" + pftime + "'");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onBillListSelectChanged(BillListSelectionEvent e) {
		try {
			if (e.getSource() == billListPanel_User_Post) {
				BillVO vo = billListPanel_User_Post.getSelectedBillVO();
				String pfTime = UIUtil.getStringValueByDS(null,
						"select max(pftime) from wn_managerdx_table where usercode='"
								+ vo.getStringValue("usercode") + "'");
				String sql = "select * from wn_managerdx_table where 1=1 and  usercode='"
						+ vo.getStringValue("usercode")
						+ "' and  pftime='"
						+ pfTime + "' order by xiangmu";
				billListPanel_User_check.QueryData(sql);
			} else if (e.getSource() == billListPanel_User_check) {
				BillVO vo = billListPanel_User_check.getSelectedBillVO();
				if (vo.getStringValue("state").equals("评分结束")) {
					btn_save.setEnabled(false);
					btn_end.setEnabled(false);
					billListPanel_User_check.setItemEditable("KOUFEN", false);// 评分完成之后，设置不可编辑
				} else {
					btn_save.setEnabled(true);
					btn_end.setEnabled(true);
					billListPanel_User_check.setItemEditable("KOUFEN", true);
				}
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	@Override
	public void onBillTreeSelectChanged(BillTreeSelectionEvent arg0) {

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