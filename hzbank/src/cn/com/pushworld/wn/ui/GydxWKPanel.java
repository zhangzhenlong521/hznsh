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
	private BillTreePanel billTreePanel_Dept = null;// ������
	private BillListPanel billListPanel_User_Post = null;// ��Ա��
	private BillListPanel billListPanel_User_check = null;// ��Ա����
	private WLTSplitPane splitPanel_all = null;
	private String str_currdeptid, str_currdeptname = null; // ��ǰ����ID����������
	private WLTSplitPane splitPanel = null;
	private WLTButton btn_save, btn_end, btn_verify;// �������֣��������֣����ָ���
	private String PFUSERNAME = ClientEnvironment.getInstance()
			.getLoginUserName();// ��¼��Ա����
	private String PFSUERCODE = ClientEnvironment.getInstance()
			.getLoginUserCode();// ��¼��Ա����
	private String PFUSERDEPT = ClientEnvironment.getInstance()
			.getLoginUserDeptId();// ��¼��Ա������
	private HashMap USERCODES = null;
	private HashMap USERTYPE = null;
	private String pfTime = null;
	private HashMap KJMAP = null;// ���ݱ�����Ļ����Ż�ȡ�������ί�ɻ�ƺ�
	private HashMap ZRMAP = null;

	@Override
	public void initialize() {
		try {
			USERCODES = UIUtil.getHashMapBySQLByDS(null,
					"select id,code from PUB_CORP_DEPT");
			USERTYPE = UIUtil.getHashMapBySQLByDS(null,
					"select USERCODE,POSTNAME from  V_PUB_USER_POST_1 where ISDEFAULT='Y'");// ������Ա���ֱ���ͬ���˿�����Ա���ֱ��е�����
			KJMAP = UIUtil
					.getHashMapBySQLByDS(null,
							"SELECT DEPTCODE,USERCODE FROM V_PUB_USER_POST_1 WHERE POSTNAME='ί�ɻ��'");
			ZRMAP = UIUtil
					.getHashMapBySQLByDS(
							null,
							"SELECT DEPTCODE,USERCODE FROM V_PUB_USER_POST_1 WHERE POSTNAME like '%����%' ORDER BY POSTNAME DESC");
		} catch (Exception e) {
			e.printStackTrace();
		}
		billTreePanel_Dept = new BillTreePanel(getCorpTempletCode());// ������:
																		// ��ȡ��Ӧ������
		billListPanel_User_Post = new BillListPanel("V_PUB_USER_POST_zpy");// �Ҳ�������Ա��
		billListPanel_User_Post.addBillListSelectListener(this);
		billListPanel_User_check = new BillListPanel("WN_GYDX_TABLE_Q01_ZPY");// ��Ա�������ֱ�
		splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT);// ˮƽ���
		billTreePanel_Dept.queryDataByCondition("1=1 ");
		billTreePanel_Dept
				.queryDataByCondition("CORPTYPE!='����' AND CORPTYPE!='����' AND CORPTYPE!='ĸ��' ");
		splitPanel = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,
				billListPanel_User_Post, billListPanel_User_check);// ���²��
		splitPanel.setDividerLocation(200);
		btn_end = new WLTButton("��������");
		btn_save = new WLTButton("����");
		btn_save.addActionListener(this);
		btn_end.addActionListener(this);
		billListPanel_User_check.addBillListButton(btn_save);
		billListPanel_User_check.addBillListButton(btn_end);
		// ���˹���ֻ���������ο��ţ�����������û���������Σ������㸱���ν��и���
//		if ("����".equals(USERTYPE.get(PFSUERCODE).toString())
//				|| "������".equals(USERTYPE.get(PFSUERCODE).toString())) {
		String curPostName=USERTYPE.get(PFSUERCODE).toString();
		if(curPostName.indexOf("����")!=-1){
			btn_verify = new WLTButton("���ָ���");
			btn_verify.addActionListener(this);
			billListPanel_User_check.addBillListButton(btn_verify);
			billListPanel_User_check.setItemEditable("fhreason", true);
			btn_save.setEnabled(false);
		} else {
			billListPanel_User_check.setItemEditable("fhreason", false);
		}
		billListPanel_User_check.repaintBillListButton();
		billListPanel_User_check.addBillListSelectListener(this);
		String PFDEPTCODE = USERCODES.get(PFUSERDEPT).toString();// ��ȡ��ǰ�����˵Ļ�������
		/**
		 * ��2019-11-26��Ӧ�ͻ�Ҫ�󣬽������뿼�˵��˲���ʾ
		 */
		String unCheckCode="SELECT CODE FROM V_SAL_PERSONINFO WHERE ISUNCHECK='Y' ";
		billListPanel_User_Post.queryDataByCondition("deptcode='" + PFDEPTCODE
				+ "' and POSTNAME like '%��Ա%' and usercode not in ("+unCheckCode+")", "seq,usercode");
		if ("282006".equals(PFDEPTCODE)) {
			splitPanel_all.add(billTreePanel_Dept);// �����ǰ��¼��������Ӫ����������ʾ������
			billTreePanel_Dept.addBillTreeSelectListener(this);
		}
		splitPanel_all.add(splitPanel);
		splitPanel_all.setDividerLocation(180);
		this.add(splitPanel_all);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {// �����������
			saveScore();
		} else if (e.getSource() == btn_end) {// �����������
			endScore();
		} else if (e.getSource() == btn_verify) {// �������ν��и���
			verifyScore();
		}
	}

	private void saveScore() {// ����������֣����ʱ��ֻ�����Ա�����յ÷֣����޸�״̬�����湦�ܿ�����ί�ɻ�ƺ���������ʹ��
		try {
			// ��ȡ����ǰѡ����Ա����Ϣ
			BillVO[] billVOs = billListPanel_User_check.getBillVOs();
			String deptcode = billListPanel_User_Post.getSelectedBillVO()
					.getStringValue("deptcode");
			StringBuffer sb = new StringBuffer();
			double fenzhi = 0.0;
			double koufen = 0.0;
			String fhreason = "";
			double result = 0.0;
			BillVO vov = billListPanel_User_Post.getSelectedBillVO();// ��ȡ����ǰ���ֹ�Ա����Ϣ
			if (vov == null) {
				MessageBox.show(this, "��ѡ��һ�����ݲ���!");
				return;
			}
			String gyusercode = vov.getStringValue("usercode");// ��ȡ����ǰ��Ա�ĵ�¼��Ϣ
			List list = new ArrayList<String>();
			UpdateSQLBuilder update = new UpdateSQLBuilder(
					billListPanel_User_check.getTempletVO().getTablename());// �����޸�
			for (int i = 0; i < billVOs.length; i++) {
				if ("�ܷ�".equals(billVOs[i].getStringValue("xiangmu"))) {
					continue;
				}
				fenzhi = Double
						.parseDouble(billVOs[i].getStringValue("fenzhi"));// ��ȡ����ǰ������ķ�ֵ
				fhreason = billVOs[i].getStringValue("FHREASON");// ��ȡ����������
				if (billVOs[i].getStringValue("KOUFEN") != null
						&& !billVOs[i].getStringValue("KOUFEN").isEmpty()) {
					koufen = Double.parseDouble(billVOs[i]
							.getStringValue("KOUFEN"));
					if (fenzhi < koufen) {
						sb.append("��" + (i + 1) + "��������ĿΪ["
								+ billVOs[i].getStringValue("khsm") + "],��ĿΪ["
								+ billVOs[i].getStringValue("xiangmu")
								+ "]�۷�����ڷ�ֵ  \n");
					}
				} else {
					sb.append("��" + (i + 1) + "��������ĿΪ["
							+ billVOs[i].getStringValue("khsm") + "],��ĿΪ["
							+ billVOs[i].getStringValue("xiangmu")
							+ "]�۷���Ϊ��  \n");
				}
				result = result + koufen;
				update.setWhereCondition("usercode='"
						+ billVOs[i].getStringValue("usercode")
						+ "' and khsm='" + billVOs[i].getStringValue("khsm")
						+ "' and state='������'");
				update.putFieldValue("KOUFEN", koufen);
				update.putFieldValue("FHREASON", fhreason);
				list.add(update.getSQL());
			}
			if (sb.length() <= 0) {//
			// billListPanel_User_check.setRealValueAt(String.valueOf(100 -
			// result), billVOs.length - 1, "fenzhi");
				update.setWhereCondition("usercode='"
						+ vov.getStringValue("usercode")
						+ "' and state='������' and xiangmu='�ܷ�'");
				update.putFieldValue("fenzhi", 100.0 - result);
				list.add(update.getSQL());
				// ���������˵���Ϣ
				UpdateSQLBuilder update2 = new UpdateSQLBuilder(
						billListPanel_User_check.getTempletVO().getTablename());
				update2.setWhereCondition("USERCODE='" + gyusercode
						+ "' and state='������'");
				update2.putFieldValue("PFUSERNAME", PFUSERNAME);
				update2.putFieldValue("PFUSERCODE", PFSUERCODE);
//				update2.putFieldValue("pftime", new SimpleDateFormat(
//						"yyyy-MM-dd hh:mm:ss").format(new Date()));// ��������ʱ��
				String PFDEPTCODE = USERCODES.get(PFUSERDEPT).toString();
				update2.putFieldValue("PFUSERDEPT", PFDEPTCODE);
				// MessageBox.show(this,update2.getSQL());
				list.add(update2.getSQL());
				UIUtil.executeBatchByDS(null, list);
				MessageBox.show(this, "�������");
				pfTime = UIUtil.getStringValueByDS(null,
						"SELECT max(PFTIME) FROM wn_gydx_table WHERE USERCODE='"
								+ gyusercode + "'");

				String sql = "select * from wn_gydx_table where usercode='"
						+ gyusercode + "' and pftime='" + pfTime
						+ "' and state='������' order by khsm asc";
				billListPanel_User_check.QueryData(sql);
			} else {
				MessageBox.show(this, sb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void endScore() {// �������� 1.��������ܵķ�ֵ��2.�޸�����״̬
		try {
			Double fenzhi = 0.0;
			Double koufen = 0.0;
			Double result = 100.0;
			// ��ȡ����ǰ��Ա����Ϣ
			BillVO gyselected = billListPanel_User_Post.getSelectedBillVO();
			String gyUserCode = gyselected.getStringValue("USERCODE");// ��ȡ����ǰѡ���˵Ĺ�Ա��
			String gyUserName = gyselected.getStringValue("USERNAME");// ��ȡ����ǰѡ�й�Ա������

			String[] state = UIUtil.getStringArrayFirstColByDS(null,
					"select state from wn_gydx_table where state='������' AND USERCODE='"
							+ gyUserCode + "'");
			if (state == null || state.length <= 0) {
				MessageBox.show(this, "��ǰ��Ա��" + gyUserName
						+ "������ָ�꿼���Ѿ������������ظ�������");
				return;
			}
			// HashVO[] vos = UIUtil.getHashVoArrayByDS(null,
			// "select USERDEPT,USERNAME,PFTIME from wn_gydx_table where 1=1 and KOUFEN is null group by USERDEPT,USERNAME,PFTIME order by USERDEPT");
			BillVO[] bos = billListPanel_User_check.getBillVOs();
			StringBuffer sb = new StringBuffer();// ��ȡ����ǰ������
			HashMap<String, String> map = UIUtil.getHashMapBySQLByDS(null,
					"select id,code from pub_corp_dept");
			UpdateSQLBuilder update3 = new UpdateSQLBuilder(
					billListPanel_User_check.getTempletVO().getTablename());
			update3.setWhereCondition("USERCODE='" + gyUserCode
					+ "' and state='������'");
			for (int j = 0; j < bos.length; j++) {
				if ("�ܷ�".equals(bos[j].getStringValue("xiangmu"))) {
					continue;
				}
				if (bos[j].getStringValue("KOUFEN") == null
						|| bos[j].getStringValue("KOUFEN").isEmpty()) {
					sb.append("��Ա��Ϊ[" + bos[j].getStringValue("USERNAME")
							+ "],������Ϊ[" + bos[j].getStringValue("xiangmu")
							+ "]����δ��ɣ� \n");
				}
			}
			String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
					.format(new Date());
			if (sb.length() > 0) {// ����δ���ֵ���
				if (MessageBox.confirm(this, "��ǰ��Ա��" + gyUserName
						+ "����δ���,ȷ��ǿ�ƽ�����  \n" + sb.toString())) {
					UpdateSQLBuilder update = new UpdateSQLBuilder(
							billListPanel_User_check.getTempletVO()
									.getTablename());
					List<String> sqlList = new ArrayList<String>();
					UpdateSQLBuilder update2 = new UpdateSQLBuilder(
							billListPanel_User_check.getTempletVO()
									.getTablename());
					for (int j = 0; j < bos.length; j++) {
						if ("�ܷ�".equals(bos[j].getStringValue("xiangmu"))) {
							continue;
						}
						update.setWhereCondition(" USERCODE='" + gyUserCode
								+ "'  and khsm='"
								+ bos[j].getStringValue("khsm")
								+ "' and  state='������'");
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
						update.putFieldValue("state", "���ֽ���");
						update.putFieldValue("FHRESULT", "δ����");
						update.putFieldValue("PFUSERNAME", PFUSERNAME);
						update.putFieldValue("PFUSERCODE", PFSUERCODE);
						update.putFieldValue("PFUSERDEPT", map.get(PFUSERDEPT));
//						update.putFieldValue("pftime", time);
						sqlList.add(update.getSQL());
					}
					UIUtil.executeBatchByDS(null, sqlList);
					update2.setWhereCondition("USERCODE='" + gyUserCode
							+ "' and state='������' and xiangmu='�ܷ�'");
					update2.putFieldValue("state", "���ֽ���");
					update2.putFieldValue("fenzhi", result);
					update2.putFieldValue("PFUSERNAME", PFUSERNAME);
					update2.putFieldValue("PFUSERCODE", PFSUERCODE);
					update2.putFieldValue("PFUSERDEPT", map.get(PFUSERDEPT));
//					update2.putFieldValue("pftime", time);
					update2.putFieldValue("koufen", "");
					UIUtil.executeUpdateByDS(null, update2.getSQL());
					MessageBox.show(this, "���ֽ����ɹ�");
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
					if (bos[j].getStringValue("XIANGMU").equals("�ܷ�")) {
						continue;
					}
					update.setWhereCondition(" USERCODE='" + gyUserCode
							+ "'  and khsm='" + bos[j].getStringValue("khsm")
							+ "'  and state='������'");
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
					update.putFieldValue("state", "���ֽ���");
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
						+ "' and state='������' and xiangmu='�ܷ�'");
				update2.putFieldValue("state", "���ֽ���");
				update2.putFieldValue("fenzhi", result);
				update2.putFieldValue("koufen", "");
				update2.putFieldValue("PFUSERNAME", PFUSERNAME);
				update2.putFieldValue("PFUSERCODE", PFSUERCODE);
				update2.putFieldValue("PFUSERDEPT", map.get(PFUSERDEPT));
//				update2.putFieldValue("pftime", time);
				UIUtil.executeUpdateByDS(null, update2.getSQL());
				MessageBox.show(this, "���ֽ����ɹ�");
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

	private void verifyScore() {// �������ζ����ֽ��и���,���˽����Ϊ:����ͨ�������˲�ͨ��
		try {
			BillVO fhUser = billListPanel_User_Post.getSelectedBillVO();
			String fhUserCode = fhUser.getStringValue("USERCODE");
			pfTime = UIUtil.getStringValueByDS(null,
					"SELECT max(PFTIME) FROM wn_gydx_table WHERE USERCODE='"
							+ fhUserCode + "'");// ����ʱ������
			HashVO[] pfNotEnd = UIUtil.getHashVoArrayByDS(null,
					"SELECT  * FROM wn_gydx_table WHERE  USERCODE='"
							+ fhUserCode + "' and state='������'");
			if (pfNotEnd.length > 0) {
				MessageBox.show(this,
						"��ǰ��Ա��" + fhUser.getStringValue("USERNAME")
								+ "��������δ�������޷����з������ˡ�");
				return;
			}
			// �жϵ�ǰ��Ա�����Ƿ�ͨ�������ڸ���ͨ���Ĺ�Ա�����ٴν��и���;
			HashVO[] pfsuccess = UIUtil.getHashVoArrayByDS(null,
					"SELECT  * FROM wn_gydx_table WHERE  USERCODE='"
							+ fhUserCode
							+ "' and fhresult='����ͨ��'  and pftime='" + pfTime
							+ "'");
			if (pfsuccess.length > 0) {
				MessageBox.show(this,
						"��ǰ��Ա��" + fhUser.getStringValue("USERNAME")
								+ "�������Ѿ���ɻ�ǰ������δ�����������ظ�������");
				return;
			}
			int result = MessageBox.showOptionDialog(this, "��ǰ��Ա�������и���", "��ʾ",
					new String[] { "����ͨ��", "�����˻�" }, 1);
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
				update.putFieldValue("FHRESULT", "����ͨ��");
				update.putFieldValue("fhreason", "");
				UIUtil.executeUpdateByDS(null, update.getSQL());// ִ���޸�
				billListPanel_User_check.setItemEditable("FHREASON", false);// ���ֽ������޷��޸���˲�ͨ������
				billListPanel_User_check.refreshData();
			} else if (result == 1) {
				update.putFieldValue("FHRESULT", "����δͨ��");
				String pfreason = JOptionPane.showInputDialog("�����븴��δͨ��������:");
				update.putFieldValue("FHREASON", pfreason);
				update.putFieldValue("STATE", "������");// ��״̬�����ֽ����޸�Ϊ�����У���ί�ɻ�Ƽ�������
				update.putFieldValue("PFUSERNAME", "");
				update.putFieldValue("PFUSERCODE", "");
				update.putFieldValue("PFUSERDEPT", "");
				UIUtil.executeUpdateByDS(null, update.getSQL());// ִ���޸�
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
				if (vo.getStringValue("state").equals("���ֽ���")) {
					btn_save.setEnabled(false);
					btn_end.setEnabled(false);
					billListPanel_User_check.setItemEditable("KOUFEN", false);// �������֮�����ò��ɱ༭
				} else {
					// ��ȡ����ǰ��Ա�������
					String deptcode = billListPanel_User_Post
							.getSelectedBillVO().getStringValue("deptcode");
					// ��ȡ����ǰ������ί�ɻ�ƵĻ�����
					if (PFSUERCODE.equals(KJMAP.get(deptcode))) {// ��֤��¼��Ա��ί�ɻ�Ƶ��������������
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
					+ str_currdeptid + "' and POSTNAME like '%��Ա%'",
					"seq,usercode"); //
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
}
