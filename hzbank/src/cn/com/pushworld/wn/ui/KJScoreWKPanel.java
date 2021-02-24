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

	private BillListPanel billListPanel_User_Post = null;// ��Ա��
	private BillListPanel billListPanel_User_check = null;// ��Ա����
	private WLTSplitPane splitPanel = null;
	private WLTButton btn_save, btn_end;
	private String PFUSERNAME = ClientEnvironment.getInstance()
			.getLoginUserName();// ��¼��Ա����
	private String PFSUERCODE = ClientEnvironment.getInstance()
			.getLoginUserCode();// ��¼��Ա����
	private String PFUSERDEPT = ClientEnvironment.getInstance()
			.getLoginUserDeptId();// ��¼��Ա������
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
		btn_save = new WLTButton("����");
		btn_save.addActionListener(this);
		btn_end = new WLTButton("����");
		btn_end.addActionListener(this);
		billListPanel_User_check.addBatchBillListButton(new WLTButton[] {
				btn_save, btn_end });
		billListPanel_User_check.repaintBillListButton();
		billListPanel_User_Post.queryDataByCondition("deptid='" + PFUSERDEPT
				+ "' and POSTNAME ='ί�ɻ��'", "seq,usercode");
		billListPanel_User_Post.addBillListSelectListener(this);
		splitPanel = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,
				billListPanel_User_Post, billListPanel_User_check);// ���²��
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
				update.setWhereCondition("state='������' and usercode='"
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
				update.putFieldValue("state", "���ֽ���");
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

	private void saveScore() {// ��������,ֻ�޸ĵ�ǰ�������еĿ۷���
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
					str.append("��ǰί�ɻ�ơ�" + vo.getStringValue("username")
							+ "�������" + pfVos[i].getStringValue("xiangmu")
							+ "���۷���Ϊ�գ�����ȷ����");
				} else {
					koufen = Double.parseDouble(koufenStr);
					fenzhi = Double.parseDouble(pfVos[i]
							.getStringValue("fenzhi"));
					if (koufen > fenzhi) {
						str.append("��ǰί�ɻ�ơ�" + vo.getStringValue("username")
								+ "�������" + pfVos[i].getStringValue("xiangmu")
								+ "���۷�����ڵ�ǰ�������ֵ������ȷ����");
					}
				}
			}
			if (str.length() <= 0) {
				UpdateSQLBuilder update = new UpdateSQLBuilder(
						"wn_kjscore_table");
				List<String> sqList = new ArrayList<String>();
				for (int i = 0; i < pfVos.length; i++) {
					update.setWhereCondition("state='������' and usercode='"
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
			String sql = "SELECT * FROM wn_kjscore_table WHERE state='������' and usercode='"
					+ vo.getStringValue("usercode") + "'";
			billListPanel_User_check.QueryData(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * ��û���ģ��
	 * 
	 * @return
	 */
	public String getCorpTempletCode() {
		return "PUB_CORP_DEPT_CODE1"; // ��򵥵�
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
				// �жϱ���ͽ�����ť�ܷ�ʹ��
				HashVO[] pfVos = UIUtil.getHashVoArrayByDS(null,
						"select * from wn_kjscore_table where pftime='"
								+ pftime + "' and usercode='" + usercode
								+ "' and state='������'");
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