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
	private BillTreePanel billTreePanel_Dept = null;// ������
	private BillListPanel billListPanel_Dept_check = null;// ��������
	private WLTSplitPane splitPanel_all = null;// ���ҷָ�
	private WLTButton btn_save, btn_end;// ����ͽ�����ť
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
		btn_save = new WLTButton("����");
		btn_save.addActionListener(this);
		btn_end = new WLTButton("����");
		btn_end.addActionListener(this);
		billListPanel_Dept_check.addBatchBillListButton(new WLTButton[] {
				btn_save, btn_end });
		billListPanel_Dept_check.repaintBillListButton();
		billTreePanel_Dept.addBillTreeSelectListener(this);
		billListPanel_Dept_check.addBillListSelectListener(this);
		splitPanel_all = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT);
		billTreePanel_Dept.queryDataByCondition("1=1");
		billTreePanel_Dept
				.queryDataByCondition("CORPTYPE!='����' AND CORPTYPE!='����' AND CORPTYPE!='ĸ��' ");
		splitPanel_all.add(billTreePanel_Dept);
		splitPanel_all.add(billListPanel_Dept_check);
		this.add(splitPanel_all);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_save) {// ����
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
				String deptN = "";// �����ȡ��������������ֵܷ���
				String khDeptName = "";
				if ("�ͻ�����".equals(PFUSERDEPTNAME)) {
					deptN = "�����ͻ�����";
				} else if ("��������".equals(PFUSERDEPTNAME)) {
					deptN = "��������";
				} else if ("��ȫ������".equals(PFUSERDEPTNAME)) {
					deptN = "��ȫ����";
				} else if ("�ڿغϹ沿".equals(PFUSERDEPTNAME)) {
					deptN = "��������";
				}

				for (int i = 0; i <= bos.length - 1; i++) {
					System.out.println("��ǰ������:"
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
							sb.append("��" + (count) + "��������ĿΪ["
									+ bos[i].getStringValue("XIANGMU")
									+ "]�۷�����ڷ�ֵ   \n");
						}
					} else {
						sb.append("��" + (count) + "��������ĿΪ["
								+ bos[i].getStringValue("XIANGMU")
								+ "]�۷���Ϊ��   \n");
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
		} else if (e.getSource() == btn_end) {// ����
			try {
				String khDeptName = "";
				String[] state = UIUtil.getStringArrayFirstColByDS(null,
						"select state from wn_BMPF_table where state='������'");
				if (state.length <= 0) {
					MessageBox.show(this, "�˴�������ȫ�������������ظ�������");
					return;
				}
				BillVO vo = billTreePanel_Dept.getSelectedVO();
				BillVO[] bos = billListPanel_Dept_check.getBillVOs();
				String sql = "select * from wn_BMPF_table where 1=1 and deptname='"
						+ vo.getStringValue("name") + "' and state='������' ";
				String deptN = "";
				if ("�ͻ�����".equals(PFUSERDEPTNAME)) {
					sql = sql + " and  xiangmu like '�����ͻ�����%'";
					deptN = "�����ͻ�����";
				} else if ("��ί�칫��".equals(PFUSERDEPTNAME)) {
					sql = sql + " and xiangmu like '��������%'";
					deptN = "��������";
				} else if ("��ȫ������".equals(PFUSERDEPTNAME)) {
					sql = sql + " and xiangmu like '��ȫ����%'";
					deptN = "��ȫ����";
				} else if ("�ڿغϹ沿".equals(PFUSERDEPTNAME)) {
					sql = sql + " and xiangmu like '��������%'";
					deptN = "��������";
				}
				String _sql = sql + " and KOUFEN IS NULL";
				HashVO[] vos = UIUtil.getHashVoArrayByDS(null, _sql);// ��ѯ��ǰ������δ������
				if (vos.length > 0) {// ����δ���ֵ���
					if (MessageBox.confirm(this, "��������δ���,ȷ��ǿ�ƽ�����?")) {
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
									+ "' and state='������'");
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
							update.putFieldValue("state", "���ֽ���");
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
						update2.putFieldValue("state", "���ֽ���");
						list.add(update2.getSQL());
						UIUtil.executeBatchByDS(null, list);
					} else {
						return;
					}
				} else {// ������δ���ֵ���,ֱ�ӽ���
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
								+ "'and state='������'");
						update.putFieldValue("PFUSERNAME", PFUSERNAME);
						update.putFieldValue("PFSUERCODE", PFSUERCODE);
						update.putFieldValue("PFUSERDEPT", PFUSERDEPT);
						update.putFieldValue("KOUFEN", KOUFEN);
						update.putFieldValue("state", "���ֽ���");
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
					update2.putFieldValue("state", "���ֽ���");
					list.add(update2.getSQL());
					UIUtil.executeBatchByDS(null, list);
					list.clear();
				}
				String maxTime = UIUtil.getStringValueByDS(null,
						"select MAX(PFTIME) FROM WN_BMPF_TABLE ");

				billListPanel_Dept_check.queryDataByCondition(
						"1=1 and state='���ֽ���' and deptname='" + khDeptName
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
					.getLoginUserDeptName();// ��ǰ��¼�˻���
			String selectedDeptName = e.getCurrSelectedVO().getStringValue(
					"name");// ��ȡ��ѡ�в���
			System.out.println("����:" + selectedDeptName);
			String maxTime = UIUtil.getStringValueByDS(null,
					"select MAX(PFTIME) FROM WN_BMPF_TABLE");
			if ("�ͻ�����".equals(deptName)) {
				billListPanel_Dept_check.queryDataByCondition(
						"1=1 and deptname='" + selectedDeptName
								+ "' and xiangmu like '�����ͻ�����%'  and PFTIME='"
								+ maxTime + "' ", "xiangmu");

				System.out.println("��ѯ��SQL:"
						+ billListPanel_Dept_check.getQuickQueryPanel()
								.getQuerySQL());
				String state = UIUtil.getStringValueByDS(null,
						"select state from WN_BMPF_TABLE WHERE deptname='"
								+ selectedDeptName
								+ "' and xiangmu like '�����ͻ�����%'  and PFTIME='"
								+ maxTime + "' AND  ROWNUM=1");

				if ("���ֽ���".equals(state)) {
					billListPanel_Dept_check.setItemEditable("KOUFEN", false);
					billListPanel_Dept_check.setItemEditable("mention", false);
				} else {
					billListPanel_Dept_check.setItemEditable("KOUFEN", true);
					billListPanel_Dept_check.setItemEditable("mention", true);
				}
			} else if ("�ڿغϹ沿".equals(deptName)) {
				billListPanel_Dept_check.queryDataByCondition(
						"1=1 and deptname='" + selectedDeptName
								+ "' and xiangmu like '��������%' and PFTIME='"
								+ maxTime + "'", "xiangmu");
				String state = UIUtil.getStringValueByDS(null,
						"select state from WN_BMPF_TABLE WHERE deptname='"
								+ selectedDeptName
								+ "' and xiangmu like '��������%'  and PFTIME='"
								+ maxTime + "' AND  ROWNUM=1");
				if ("���ֽ���".equals(state)) {
					billListPanel_Dept_check.setItemEditable("KOUFEN", false);
					billListPanel_Dept_check.setItemEditable("mention", false);
				} else {
					billListPanel_Dept_check.setItemEditable("KOUFEN", true);
					billListPanel_Dept_check.setItemEditable("mention", true);
				}
			} else if ("��ȫ������".equals(deptName)) {
				billListPanel_Dept_check.queryDataByCondition(
						"1=1 and deptname='" + selectedDeptName
								+ "' and xiangmu like '��ȫ����%' and  PFTIME='"
								+ maxTime + "'", "xiangmu");
				String state = UIUtil.getStringValueByDS(null,
						"select state from WN_BMPF_TABLE WHERE deptname='"
								+ selectedDeptName
								+ "' and xiangmu like '��ȫ����%'  and PFTIME='"
								+ maxTime + "' AND  ROWNUM=1");
				if ("���ֽ���".equals(state)) {
					billListPanel_Dept_check.setItemEditable("KOUFEN", false);
					billListPanel_Dept_check.setItemEditable("mention", false);
				} else {
					billListPanel_Dept_check.setItemEditable("KOUFEN", true);
					billListPanel_Dept_check.setItemEditable("mention", true);
				}
			} else if ("��ί�칫��".equals(deptName)) {
				billListPanel_Dept_check.queryDataByCondition(
						"1=1 and deptname='" + selectedDeptName
								+ "' and xiangmu like '����%' and PFTIME='"
								+ maxTime + "'", "xiangmu");
				String state = UIUtil.getStringValueByDS(null,
						"select state from WN_BMPF_TABLE WHERE deptname='"
								+ selectedDeptName
								+ "' and xiangmu like '����%'  and PFTIME='"
								+ maxTime + "' AND  ROWNUM=1");
				if ("���ֽ���".equals(state)) {
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