package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ibm.db2.jcc.t4.sb;

import cn.com.infostrategy.bs.mdata.styletemplet.t01.DMO_01;
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

public class KJScoreWKPanel extends AbstractWorkPanel implements
		ActionListener, BillListSelectListener {

	private BillListPanel billListPanel_User_Post = null;// 人员表
	private BillListPanel billListPanel_User_check = null;// 柜员评分
	private WLTSplitPane splitPanel = null;
	private WLTButton btn_save, btn_end;
	private String PFUSERNAME = ClientEnvironment.getInstance()
			.getLoginUserName();// 登录人员名称
	private String PFSUERCODE = ClientEnvironment.getInstance()
			.getLoginUserCode();// 登录人员编码
	private String PFUSERDEPT = ClientEnvironment.getInstance()
			.getLoginUserDeptId();// 登录人员机构号
	private Map<String, String> DeptMap = null;

	@Override
	public void initialize() {
		try {
			DeptMap = UIUtil.getHashMapBySQLByDS(null,
					"SELECT DEPTID,DEPTCODE FROM V_PUB_USER_POST_1");
		} catch (Exception e) {
			e.printStackTrace();
		}
		billListPanel_User_Post = new BillListPanel("V_PUB_USER_POST_zpy");
		billListPanel_User_check = new BillListPanel("WN_KJSCORE_TABLE_ZPY_Q01");
		btn_save = new WLTButton("保存");
		btn_save.addActionListener(this);
		btn_end = new WLTButton("结束");
		btn_end.addActionListener(this);
		billListPanel_User_check.addBatchBillListButton(new WLTButton[] {
				btn_save, btn_end });
		billListPanel_User_check.repaintBillListButton();
		billListPanel_User_Post.queryDataByCondition("deptid='" + PFUSERDEPT
				+ "' and POSTNAME ='委派会计'", "seq,usercode");
		billListPanel_User_Post.addBillListSelectListener(this);
		splitPanel = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,
				billListPanel_User_Post, billListPanel_User_check);// 上下拆分
		splitPanel.setDividerLocation(200);
		this.add(splitPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {
			saveScore();
		} else if (e.getSource() == btn_end) {
			endScore();
		}

	}

	private void endScore() {
		try {
			BillVO vo = billListPanel_User_Post.getSelectedBillVO();
			BillVO[] pfVos = billListPanel_User_check.getSelectedBillVOs();
			double fenzhi = 0.0;
			double koufen = 0.0;
			UpdateSQLBuilder update = new UpdateSQLBuilder("wn_kjscore_table");
			List<String> sqList = new ArrayList<String>();
			for (int i = 0; i < pfVos.length; i++) {
				update.setWhereCondition("state='评分中' and usercode='"
						+ vo.getStringValue("usercode") + "' and xiangmu='"
						+ pfVos[i].getStringValue("xiangmu") + "'");
				String koufenStr = pfVos[i].getStringValue("koufen");
				fenzhi = Double.parseDouble(pfVos[i].getStringValue("fenzhi"));
				if (koufenStr == null || koufenStr.isEmpty()) {
					koufen = 0.0;
				}
				koufen = Double.parseDouble(pfVos[i].getStringValue("koufen"));
				if (koufen >= fenzhi) {
					koufen = fenzhi;
				}
				update.putFieldValue("koufen", koufen);
				update.putFieldValue("pfusername", PFUSERNAME);
				update.putFieldValue("pfusercode", PFSUERCODE);
				update.putFieldValue("pfuserdept", DeptMap.get(PFUSERDEPT));
				update.putFieldValue("state", "评分结束");
				sqList.add(update.getSQL());
			}
			UIUtil.executeBatchByDS(null, sqList);
			String pfTime = UIUtil.getStringValueByDS(null,
					"select max(pftime) from wn_kjscore_table where usercode='"
							+ vo.getStringValue("usercode") + "'");
			String sql = "SELECT * FROM wn_kjscore_table WHERE usercode='"
					+ vo.getStringValue("usercode") + "' and pftime='" + pfTime
					+ "'";
			billListPanel_User_check.QueryData(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void saveScore() {// 保存评分,只修改当前评分项中的扣分项
		try {
			BillVO vo = billListPanel_User_Post.getSelectedBillVO();
			BillVO[] pfVos = billListPanel_User_check.getSelectedBillVOs();
			double fenzhi = 0.0;
			double koufen = 0.0;
			StringBuilder str = new StringBuilder("");
			for (int i = 0; i < pfVos.length; i++) {
				String koufenStr = pfVos[i].getStringValue("koufen");
				if (koufenStr == null || koufenStr.isEmpty()
						|| koufenStr.length() == 0) {
					str.append("当前委派会计【" + vo.getStringValue("username")
							+ "】考核项【" + pfVos[i].getStringValue("xiangmu")
							+ "】扣分项为空，请正确输入");
				} else {
					koufen = Double.parseDouble(koufenStr);
					fenzhi = Double.parseDouble(pfVos[i]
							.getStringValue("fenzhi"));
					if (koufen > fenzhi) {
						str.append("当前委派会计【" + vo.getStringValue("username")
								+ "】考核项【" + pfVos[i].getStringValue("xiangmu")
								+ "】扣分项大于当前考核项分值，请正确输入");
					}
				}
			}
			if (str.length() <= 0) {
				UpdateSQLBuilder update = new UpdateSQLBuilder(
						"wn_kjscore_table");
				List<String> sqList = new ArrayList<String>();
				for (int i = 0; i < pfVos.length; i++) {
					update.setWhereCondition("state='评分中' and usercode='"
							+ vo.getStringValue("usercode") + "' and xiangmu='"
							+ pfVos[i].getStringValue("xiangmu") + "'");
					koufen = Double.parseDouble(pfVos[i]
							.getStringValue("koufen"));
					update.putFieldValue("koufen", koufen);
					update.putFieldValue("pfusername", PFUSERNAME);
					update.putFieldValue("pfusercode", PFSUERCODE);
					update.putFieldValue("pfuserdept", DeptMap.get(PFUSERDEPT));
					sqList.add(update.getSQL());
				}
				UIUtil.executeBatchByDS(null, sqList);
			} else {
				MessageBox.show(this, str.toString());
				return;
			}
			String sql = "SELECT * FROM wn_kjscore_table WHERE state='评分中' and usercode='"
					+ vo.getStringValue("usercode") + "'";
			billListPanel_User_check.QueryData(sql);
		} catch (Exception e) {
			e.printStackTrace();
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

	// @Override
	// public void onBillListSelectChanged(BillListSelectionEvent arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	@Override
	public void onBillListSelectChanged(BillListSelectionEvent e) {
		if (e.getSource() == billListPanel_User_Post) {
			try {
				BillVO vo = billListPanel_User_Post.getSelectedBillVO();
				String usercode = vo.getStringValue("usercode");
				String pftime = UIUtil.getStringValueByDS(null,
						"select max(pftime) from wn_kjscore_table where usercode='"
								+ usercode + "'");
				String sql = "select * from wn_kjscore_table where pftime='"
						+ pftime + "' and usercode='" + usercode + "'";
				billListPanel_User_check.QueryData(sql);
				// 判断保存和结束按钮能否使用
				HashVO[] pfVos = UIUtil.getHashVoArrayByDS(null,
						"select * from wn_kjscore_table where pftime='"
								+ pftime + "' and usercode='" + usercode
								+ "' and state='评分中'");
				if (pfVos.length <= 0) {
					btn_end.setEnabled(false);
					btn_save.setEnabled(false);
					billListPanel_User_check.setItemEditable("koufen", false);
				} else {
					btn_end.setEnabled(true);
					btn_save.setEnabled(true);
					billListPanel_User_check.setItemEditable("koufen", true);
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

}