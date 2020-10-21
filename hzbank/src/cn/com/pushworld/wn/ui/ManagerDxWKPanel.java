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
	private BillListPanel billListPanel_User_Post = null;// ��Ա��
	private BillListPanel billListPanel_User_check = null;// ��Ա����
	private WLTButton btn_save, btn_end;// �������֣���������
	private String PFUSERNAME = ClientEnvironment.getInstance()
			.getLoginUserName();// ��¼��Ա����
	private String PFSUERCODE = ClientEnvironment.getInstance()
			.getLoginUserCode();// ��¼��Ա����
	private String PFUSERDEPT = ClientEnvironment.getInstance()
			.getLoginUserDeptId();// ��¼��Ա������
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
		billListPanel_User_Post = new BillListPanel("V_PUB_USER_POST_zpy");// �Ҳ�������Ա��
		billListPanel_User_Post.addBillListSelectListener(this);
		billListPanel_User_check = new BillListPanel(
				"WN_MANAGERDX_TABLE_Q01_ZPY");// �ͻ����������ֱ�
		splitPanel = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,
				billListPanel_User_Post, billListPanel_User_check);// ���²��
		splitPanel.setDividerLocation(200);
		btn_end = new WLTButton("��������");
		btn_save = new WLTButton("����");
		btn_save.addActionListener(this);
		btn_end.addActionListener(this);
		billListPanel_User_check.addBillListButton(btn_save);
		billListPanel_User_check.addBillListButton(btn_end);
		billListPanel_User_check.repaintBillListButton();
		billListPanel_User_check.addBillListSelectListener(this);
		String PFDEPTCODE = USERCODES.get(PFUSERDEPT).toString();// ��ȡ��ǰ�����˵Ļ�������
		billListPanel_User_Post.queryDataByCondition("deptcode='" + PFDEPTCODE
				+ "' and POSTNAME like '%�ͻ�����%'", "seq,usercode");
		this.add(splitPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {
			saveScore();// �������
		} else if (e.getSource() == btn_end) {
			endScore();// ������ǰ��Ա������
		}
	}

	private void saveScore() {
		try {
			BillVO[] billVOs = billListPanel_User_check.getBillVOs();// ��ȡ����ǰҳ��ȫ��������
			double fenzhi = 0.0;
			double koufen = 0.0;
			double result = 0.0;
			BillVO selectedBillVO = billListPanel_User_Post.getSelectedBillVO();
			String usercode = selectedBillVO.getStringValue("usercode");
			UpdateSQLBuilder update = new UpdateSQLBuilder("wn_managerdx_table");
			StringBuffer str = new StringBuffer();
			for (int i = 0; i < billVOs.length; i++) {
				if ("�ܷ�".equals(billVOs[i].getStringValue("xiangmu"))) {
					continue;
				}
				if (billVOs[i].getStringValue("koufen") == null
						|| billVOs[i].getStringValue("koufen").isEmpty()) {
					str.append("��ǰ���˿ͻ�����"
							+ billVOs[i].getStringValue("username") + "���п�����Ϊ��"
							+ billVOs[i].getStringValue("xiangmu")
							+ "���۷���Ϊ��,������");
				} else {
					fenzhi = Double.parseDouble(billVOs[i]
							.getStringValue("fenzhi"));
					koufen = Double.parseDouble(billVOs[i]
							.getStringValue("koufen"));
					if (fenzhi < koufen) {
						str.append("��ǰ���˿ͻ�����"
								+ billVOs[i].getStringValue("username")
								+ "���п�����Ϊ��"
								+ billVOs[i].getStringValue("xiangmu")
								+ "���۷ֳ��������ֵ,������");
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
				if ("�ܷ�".equals(xiangmu)) {
					continue;
				}
				String khsm = billVOs[i].getStringValue("khsm");
				koufen = Double
						.parseDouble(billVOs[i].getStringValue("koufen"));
				result = result + koufen;
				update.setWhereCondition("usercode='" + usercode
						+ "' and khsm='" + khsm + "' and state='������'");
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
					+ "' and khsm='�ܷ�' and state='������'");
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
					+ usercode + "' and state='������'";
			billListPanel_User_check.QueryData(sql);
			MessageBox.show(this, "����ɹ�");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void endScore() {
		try {
			BillVO selectedBillVO = billListPanel_User_Post.getSelectedBillVO();
			String usercode = selectedBillVO.getStringValue("usercode");
			// �жϵ�ǰ�ͻ������Ƿ��Ѿ���������
			String[] state = UIUtil.getStringArrayFirstColByDS(null,
					"select state from wn_managerdx_table where state='������' AND USERCODE='"
							+ usercode + "'");
			if (state == null || state.length <= 0) {
				MessageBox.show(this, "��ǰ�ͻ�����" + usercode
						+ "������ָ�꿼���Ѿ������������ظ�������");
				return;
			}
			BillVO[] bos = billListPanel_User_check.getBillVOs();
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < bos.length; j++) {
				String xiangmu = bos[j].getStringValue("xiangmu");
				if ("�ܷ�".equals(xiangmu)) {
					continue;
				}
				if (bos[j].getStringValue("KOUFEN") == null
						|| bos[j].getStringValue("KOUFEN").isEmpty()) {
					sb.append("�ͻ�������Ϊ[" + bos[j].getStringValue("USERNAME")
							+ "],������Ϊ[" + bos[j].getStringValue("xiangmu")
							+ "]����δ��ɣ� \n");
				}
			}
			if (sb.length() > 0) {
				if (MessageBox.confirm(this, "��ǰ��Ա��" + usercode
						+ "����δ���,ȷ��ǿ�ƽ�����  \n" + sb.toString())) {
					double fenzhi = 0.0;
					double koufen = 0.0;
					double result = 0.0;
					UpdateSQLBuilder update = new UpdateSQLBuilder(
							billListPanel_User_check.getTempletVO()
									.getTablename());
					List<String> list = new ArrayList<String>();
					for (int i = 0; i < bos.length; i++) {// ����ÿһ�����ϸ
						String xiangmu = bos[i].getStringValue("xiangmu");
						if ("�ܷ�".equals(xiangmu)) {
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
								+ "' and state='������' and khsm='"
								+ bos[i].getStringValue("khsm") + "'");
						update.putFieldValue("state", "���ֽ���");
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
							+ "' and state='������' and khsm='�ܷ�'");
					update2.putFieldValue("pfusername", PFUSERNAME);
					update2.putFieldValue("pfusercode", PFSUERCODE);
					update2.putFieldValue("pfuserdept",
							USERCODES.get(PFUSERDEPT).toString());
//					update2.putFieldValue("pftime", new SimpleDateFormat(
//							"yyyy-MM-dd hh:mm:ss").format(new Date()));
					update2.putFieldValue("state", "���ֽ���");
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
				for (int i = 0; i < bos.length; i++) {// ����ÿһ�����ϸ
					String xiangmu = bos[i].getStringValue("xiangmu");
					if ("�ܷ�".equals(xiangmu)) {
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
							+ "' and state='������' and khsm='"
							+ bos[i].getStringValue("khsm") + "'");
					update.putFieldValue("state", "���ֽ���");
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
						+ "' and state='������' and khsm='�ܷ�'");
				update2.putFieldValue("pfusername", PFUSERNAME);
				update2.putFieldValue("pfusercode", PFSUERCODE);
				update2.putFieldValue("pfuserdept", USERCODES.get(PFUSERDEPT)
						.toString());
//				update2.putFieldValue("pftime", new SimpleDateFormat(
//						"yyyy-MM-dd hh:mm:ss").format(new Date()));
				update2.putFieldValue("state", "���ֽ���");
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
				if (vo.getStringValue("state").equals("���ֽ���")) {
					btn_save.setEnabled(false);
					btn_end.setEnabled(false);
					billListPanel_User_check.setItemEditable("KOUFEN", false);// �������֮�����ò��ɱ༭
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
	 * ��û���ģ��
	 * 
	 * @return
	 */
	public String getCorpTempletCode() {
		return "PUB_CORP_DEPT_CODE1"; // ��򵥵�
	}
}