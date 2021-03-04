package cn.com.pushworld.salary.ui.person.p021;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.DivComponentLayoutUtil;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.layered.WLTLayeredPane;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.ui.SalaryUIUtil;
import cn.com.pushworld.salary.ui.person.p020.PersonMutualWKPanel;

/**
 * ���������ֽ���
 * ��ҳǩģʽ
 * @author haoming
 * create by 2016-1-15
 */
public class PostDutyMutualWKPanel_MultiTab extends AbstractWorkPanel implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 2940195240282541876L;
	private String userid = ClientEnvironment.getCurrSessionVO().getLoginUserId(); // ��ǰ��¼��ID
	private WLTTabbedPane tabpane = new WLTTabbedPane();// ���
	private HashVO currLog = null; // ��ǰ�ƻ���־ID��
	private boolean editable = true;
	private HashVO currCheckVOs[]; // ��ǰ��Ա���ֵ���������
	private TBUtil tbutil = new TBUtil();
	private String input_value_range = TBUtil.getTBUtil().getSysOptionStringValue("н��ģ����Ա�������", "0-10");
	private static final Logger logger = WLTLogger.getLogger(PersonMutualWKPanel.class);
	private SalaryTBUtil stbutil = new SalaryTBUtil();
	private List<BillCellPanel> allCellPanel = new ArrayList<BillCellPanel>();
	private int value_col_start_index = 2; // ��������ݴӵĵڼ��п�ʼ��
	private int value_row_start_index = 2; // ��������ݴӵĵڼ��п�ʼ��
	private List<Boolean> issubmited = new ArrayList<Boolean>(); //�������ݺ��ж��Ƿ��Ѿ�ȫ���ύ��
	private WLTTabbedPane tabp = new WLTTabbedPane();
	private WLTButton btn_submit = new WLTButton("�ύ", UIUtil.getImage("zt_050.gif"));
	List<HashVO> uservo = new ArrayList<HashVO>();//��Ҫ���۵���
	BillCellVO allcheckBillCellVO[] = null; //��Ҫ���۵�ҳ��

	private JPanel init() {
		HashVO vos[];
		try {
			// �����½�˿��������мƻ�
			vos = UIUtil.getHashVoArrayByDS(null, "select distinct(t1.id),t1.checkdate,t1.status,t1.name from SAL_TARGET_CHECK_LOG t1,sal_post_duty_target_list t2 where t1.id = t2.logid and t1.status!='���˽���' and t2.scoreuser='" + userid + "'");
			if (vos == null || vos.length == 0) {
				//�����һ���µġ�
				HashVO[] planvos = UIUtil.getHashVoArrayByDS(null, "select * from SAL_TARGET_CHECK_LOG where checkdate order by checkdate desc");
				if (planvos.length > 0) {
					planvos = new HashVO[] { planvos[0] };
				}

				JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
				WLTLabel label = new WLTLabel("Ŀǰȫ��û�п����еļƻ�...");
				label.setFont(new Font("����", Font.BOLD, 16));
				panel.add(label);
				return panel;
			}
			if (vos.length > 1) { // ����ж����bom��ʾ��
			} else {// ������о�һ��ִ����
				return loadPanelByLogPlanVO();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JPanel();
	}

	/*
	 * ���������е��ó�ʼ����������Ҫ����ҳǩ����ǰ�ļƻ�VO����ʼ��ҳǩ��
	 */
	public void customInit(WLTTabbedPane _tabPanel, HashVO _logVO, String _userid, boolean _editable) {
		tabpane = _tabPanel;
		currLog = _logVO;
		userid = _userid;
		editable = _editable;
		loadPanelByLogPlanVO();
	}

	/*
	 * ����ѡ��ļƻ�����ȡ������塣
	 */
	private JPanel loadPanelByLogPlanVO() {
		try {
			tabp.isSimpleStyle(true);
			tabp.addChangeListener(this);
			getBillCellVo();
			for (int i = 0; i < allcheckBillCellVO.length; i++) {
				BillCellPanel cellpanel = new BillCellPanel(allcheckBillCellVO[i]);
				cellpanel.putClientProperty("this", this);
				allCellPanel.add(cellpanel);
				Icon icon = null;
				if ("��".equals(uservo.get(i).getStringValue("sex"))) {
					icon = UIUtil.getImage("office_084.gif");
				} else {
					icon = UIUtil.getImage("office_015.gif");
				}
				tabp.addTab(uservo.get(i).getStringValue("name"), icon, cellpanel);
			}
			JPanel mainPanel = new WLTPanel(new BorderLayout());
			mainPanel.setOpaque(false);
			btn_submit.setToolTipText("�ύ���, �ύ�����޸�");
			btn_submit.addActionListener(this);
			WLTPanel btnPanel = new WLTPanel(new BorderLayout());
			btnPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			btnPanel.add(btn_submit, BorderLayout.WEST);
			StringBuffer showWarnInfo = new StringBuffer();
			showWarnInfo.append("&nbsp;&nbsp;&nbsp;&nbsp;��������ֱ�׼: �ǳ����� 9-10 ��; ���� 7-8 ��; �ϸ� 5-6 ��; �����ϸ� 3-4 ��; ���ϸ� 0-2��. ");
			JLabel info_label = new JLabel("<html><font color='blue'>" + showWarnInfo.toString() + "</font></html>");
			info_label.setPreferredSize(new Dimension(550 + LookAndFeel.getFONT_REVISE_SIZE() * showWarnInfo.length(), 18));
			info_label.setVerticalTextPosition(JLabel.CENTER);
			btnPanel.add(info_label, BorderLayout.CENTER);
			mainPanel.add(btnPanel, BorderLayout.NORTH);
			mainPanel.add(tabp, BorderLayout.CENTER);
			tabpane.addTab("��λ����", UIUtil.getImage("zt_023.gif"), mainPanel);
			for (int i = 0; i < issubmited.size(); i++) {
				if (issubmited.get(i)) {
					setCurrCellPanelStatus(allCellPanel.get(i), "�� �� ��");
					allCellPanel.get(i).setEditable(false);
				}
			}
			return tabpane;
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JPanel();
	}

	/*
	 * ��ɫ�����ı���
	 */
	private BillCellItemVO getBillCellVO_Blue(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setBackground("191,213,255");
		item.setFonttype("������");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		item.setIseditable("N");
		item.setCellhelp(null);
		return item;
	}

	/*
	 * Ĭ�ϰ�ɫ
	 */
	private BillCellItemVO getBillCellVO_Normal(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setFonttype("������");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		item.setCellhelp(null);
		return item;
	}

	/*
	 * ����
	 */
	private BillCellItemVO getBillTitleCellItemVO(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setCellvalue(value);
		item.setBackground("191,213,0");
		item.setFonttype("������");
		item.setFontsize("12");
		item.setFontstyle("1");
		item.setSpan("1,1");
		item.setIseditable("N");
		item.setCellhelp(null);
		return item;
	}

	public void initialize() {
		this.add(init());
	}

	// �õ�����¼��ֵ�ķ�Χ,�������ȵ�����.
	public int[] getInputNumRange() {
		String[] values = TBUtil.getTBUtil().split(input_value_range, "-");
		int begin = 0;
		int end = 10;
		try {
			if (values.length == 2) {
				begin = Integer.parseInt(values[0]);
				end = Integer.parseInt(values[1]);
				if (begin >= end) { // ���ֵǰ��˳�������⡣
					begin = 0;
					end = 10;
				}
			}
		} catch (Exception _ex) {
			logger.error("", _ex);
		}
		return new int[] { begin, end };
	}

	private boolean autoCheckCommit = true;

	public void autoCheckIFCanCommit() {
		if (!autoCheckCommit) {// �Ƿ���Ҫ�Զ����
			return;
		}
		int currIndex = tabp.getSelectedIndex();
		final BillCellPanel cellpanel = allCellPanel.get(currIndex);
		int lastColNum = cellpanel.getColumnCount();
		int lastRowNum = cellpanel.getRowCount();
		int alldatanum = 0;
		int range[] = getInputNumRange();
		try {
			for (int i = value_row_start_index; i < lastRowNum; i++) {
				for (int j = value_col_start_index; j < lastColNum; j++) {
					String cellValue = cellpanel.getValueAt(i, j);
					BillCellItemVO itemvo = cellpanel.getBillCellItemVOAt(i, j);
					if (itemvo != null && "Y".equals(itemvo.getCustProperty("nohave"))) {
						continue;
					}
					if (cellValue == null || "".equals(cellValue)) {
						return;
					} else {
						try {
							Float value = Float.parseFloat(cellValue); //
							if (value < range[0] || value > range[1]) {// Ӧ�ò������
								return;
							}
							alldatanum++;
						} catch (Exception _ex) {
							_ex.printStackTrace();
							return;
						}

					}
				}
			}
			// ȫ����д���ˡ�
			StringBuffer msgsb = new StringBuffer();
			new Timer().schedule(new TimerTask() {
				public void run() {
					String msg = "��ҳ�����������,�Ƿ��ύ���?";
					int index = MessageBox.showOptionDialog(cellpanel, msg, "ϵͳ��ʾ", new String[] { "�����ύ", "�Ժ��ύ" });
					if (index != 0) {
						autoCheckCommit = false;// �����ϲ���Զ���ʾ������Ϊfalse��
						return;
					}
					onCellPanelSubmit(cellpanel);
				}
			}, 300); // �ӳ�һ����ٵ�����.���ܺ�һЩ��
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void getBillCellVo() throws Exception {
		StringBuffer sqlsb = new StringBuffer();
		sqlsb.append("select t1.*,t2.descr,t2.evalstandard from  sal_person_postduty_score  t1 left join sal_post_duty_target_list t2 on t1.targetid=t2.id left join v_pub_user_post_2 u  on " + "u.userid = t1.checkeduser and u.isdefault='Y' left join pub_corp_Dept u2 on u2.id = u.deptid where t1.scoreuser='" + userid + "' and t1.logid='" + currLog.getStringValue("id")
				+ "' and t1.targettype ='��������ָ��' ");
		sqlsb.append(" order by t2.seq,u2.seq,u.seq");
		currCheckVOs = UIUtil.getHashVoArrayByDS(null, sqlsb.toString()); // ���ݲ�����Ա����
		//		LinkedHashMap<String, String> targetids = new LinkedHashMap<String, String>(); //�ϲ�����ָ��
		LinkedHashMap<String, List> checkedusers_targetlist = new LinkedHashMap<String, List>();
		for (int i = 0; i < currCheckVOs.length; i++) {
			String targetid = currCheckVOs[i].getStringValue("targetid");
			String targetname = currCheckVOs[i].getStringValue("targetname");
			String checkeduser = currCheckVOs[i].getStringValue("checkeduser", "");
			//			targetids.put(targetid, targetname);
			if (checkedusers_targetlist.containsKey(checkeduser)) {
				List map = checkedusers_targetlist.get(checkeduser);
				map.add(currCheckVOs[i]);
			} else {
				List map = new ArrayList();
				map.add(currCheckVOs[i]);
				checkedusers_targetlist.put(checkeduser, map);
			}
		}
		HashMap<String, String> haveFinishedNum = UIUtil.getHashMapBySQLByDS(null, "select checkeduser,count(id) num from sal_person_postduty_score t1 where status='���ύ' and t1.scoreuser='" + userid + "' and t1.logid='" + currLog.getStringValue("id") + "' group by checkeduser");

		HashVO checkedUserVOs[] = UIUtil.getHashVoArrayByDS(null, "select id,name,sex from v_sal_personinfo where id in(" + TBUtil.getTBUtil().getInCondition((String[]) (checkedusers_targetlist.keySet().toArray(new String[0]))) + ")");
		BillCellVO cellvo = new BillCellVO();
		//		String[] alltarget = (String[]) targetids.keySet().toArray(new String[0]);
		Iterator it = checkedusers_targetlist.entrySet().iterator();

		List cellVOList = new ArrayList();
		while (it.hasNext()) {
			List cellrows = new ArrayList<BillCellItemVO[]>();
			BillCellVO oneUserCellVO = new BillCellVO();
			Entry entry = (Entry) it.next();
			String checkeduser = (String) entry.getKey();
			String finishNum = haveFinishedNum.get(checkeduser);//�õ����ύ��������
			List user_target = (ArrayList) entry.getValue();
			for (int i = 0; i < checkedUserVOs.length; i++) {
				if (checkedUserVOs[i].getStringValue("id", "").equals(checkeduser)) {
					uservo.add(checkedUserVOs[i]);
					break;
				}
			}
			if (((user_target.size() + "").equals(finishNum))) { //�ж��Ѿ��ύ�������Ƿ����ָ����ͬ
				issubmited.add(Boolean.TRUE);
			} else {
				issubmited.add(Boolean.FALSE);
			}
			List<BillCellItemVO> titleRow = new ArrayList<BillCellItemVO>();
			BillCellItemVO cell_title_1 = getBillCellVO_Blue(currLog.getStringValue("name")); // �ƻ�����
			cell_title_1.setHalign(2); //
			cell_title_1.setFontsize("15");
			cell_title_1.setFontstyle("1");
			cell_title_1.setSpan("1,3");
			titleRow.add(cell_title_1);
			for (int i = 0; i < 2; i++) {
				titleRow.add(getBillCellVO_Normal(""));
			}
			cellrows.add((BillCellItemVO[]) titleRow.toArray(new BillCellItemVO[0]));

			BillCellItemVO cell_1_1 = getBillCellVO_Blue("��������"); //
			cell_1_1.setSpan("1,2");
			cell_1_1.setHalign(2);
			BillCellItemVO cell_1_2 = getBillCellVO_Blue("��������"); //
			BillCellItemVO cell_1_3 = getBillCellVO_Blue("�����"); // ��ͷ
			cell_1_1.setFontstyle("1");
			cell_1_3.setFontstyle("1");
			cell_1_3.setHalign(2);
			cell_1_3.setForeground("0,0,255");
			// ���һ��
			cellrows.add(new BillCellItemVO[] { cell_1_1, cell_1_2, cell_1_3 });

			for (int i = 0; i < user_target.size(); i++) {
				HashVO targetVO = (HashVO) user_target.get(i);
				BillCellItemVO target_name_cell_vo = getBillCellVO_Blue(targetVO.getStringValue("targetname"));

				String descr = targetVO.getStringValue("descr");
				StringBuffer descrsb = new StringBuffer();
				if (!tbutil.isEmpty(descr)) {
					descrsb.append(descr);
				}
				String evalstandard = targetVO.getStringValue("evalstandard");
				if (!tbutil.isEmpty(evalstandard)) {
					descrsb.append(evalstandard);
				}

				BillCellItemVO target_descr_cell_vo = getBillCellVO_Blue(descrsb.toString());
				target_descr_cell_vo.setCelltype("TEXTAREA");
				int length = tbutil.getStrWidth(target_descr_cell_vo.getCellvalue());
				if (length > 600) {
					target_descr_cell_vo.setColwidth((600) + "");
					target_descr_cell_vo.setRowheight("" + (length / 600 + 1) * 23);
				} else {
					target_descr_cell_vo.setColwidth(length + "");
				}
				BillCellItemVO cell_N = getBillCellVO_Normal(targetVO.getStringValue("checkscore"));// ��ʾ����
				cell_N.setHalign(2);
				cell_N.setCustProperty("hashvo", targetVO);
				cell_N.setEditformula("AddItemValueChangedListener(\"cn.com.pushworld.salary.ui.target.p070.PostDutyMutualCheckValueChangeLinstener\",\"Cellִ��\")");
				cell_N.setCelltype("NUMBERTEXT");
				cellrows.add(new BillCellItemVO[] { target_name_cell_vo, target_descr_cell_vo, cell_N });
			}
			oneUserCellVO.setCellItemVOs((BillCellItemVO[][]) cellrows.toArray(new BillCellItemVO[0][0]));
			oneUserCellVO.setRowlength(cellrows.size());
			oneUserCellVO.setCollength(3);
			cellVOList.add(oneUserCellVO);
		}
		allcheckBillCellVO = (BillCellVO[]) cellVOList.toArray(new BillCellVO[0]);
	}

	/*
	 * Excel������д�ύ.
	 */
	private void onCellPanelSubmit(BillCellPanel cellPanel) {
		cellPanel.stopEditing();
		int lastRowNum = cellPanel.getRowCount();
		int lastColNum = cellPanel.getColumnCount();
		int alldatanum = 0;
		if (issubmited.get(tabp.getSelectedIndex())) {
			MessageBox.show(cellPanel, "�Ѿ��ύ!");
			return;
		}
		try {
			int range[] = getInputNumRange();
			for (int i = value_row_start_index; i < lastRowNum; i++) {
				for (int j = value_col_start_index; j < lastColNum; j++) {
					String cellValue = cellPanel.getValueAt(i, j);
					BillCellItemVO itemvo = cellPanel.getBillCellItemVOAt(i, j);
					if ("N".equals(itemvo.getIseditable())) {
						continue;
					}
					if (cellValue == null || "".equals(cellValue)) {
						MessageBox.show(cellPanel, "��������û����д!");
						cellPanel.getTable().scrollRectToVisible(cellPanel.getTable().getCellRect(i, j, true));
						cellPanel.getTable().setColumnSelectionInterval(j, j);
						cellPanel.getTable().setRowSelectionInterval(i, i);
						return;
					} else {
						try {
							Float value = Float.parseFloat(cellValue); //
							if (value < range[0] || value > range[1]) {// Ӧ�ò������
								MessageBox.show(cellPanel, "����ֵ��Χ" + input_value_range + "֮��");
								cellPanel.getTable().scrollRectToVisible(cellPanel.getTable().getCellRect(i, j, true));
								cellPanel.getTable().setColumnSelectionInterval(j, j);
								cellPanel.getTable().setRowSelectionInterval(i, i);
								return;
							}
							alldatanum++;
						} catch (Exception _ex) {
							_ex.printStackTrace();
							MessageBox.show(cellPanel, "��дֵ��ΧΪ" + input_value_range + ",�����������ú�ɫ��ʶ,���޸ĺ����ύ.");
							cellPanel.getTable().scrollRectToVisible(cellPanel.getTable().getCellRect(i, j, true));
							cellPanel.getTable().setColumnSelectionInterval(j, j);
							cellPanel.getTable().setRowSelectionInterval(i, i);
							return;
						}

					}
				}
			}
			//			if ((Boolean) curTabVO[5]) {
			//				MessageBox.show(cellPanel, "�Ѿ��ύ!");
			//				return;
			//			}
			StringBuffer msgsb = new StringBuffer();
			// ��Ҫ���������˺Ϳͻ����Ƿ�һֱ.����ʱ������Щ����¼����,����û�и��µ����ݿ���.

			int currSelectIndex = tabp.getSelectedIndex();
			String currCheckedUserid = uservo.get(currSelectIndex).getStringValue("id");
			HashMap undo = UIUtil.getHashMapBySQLByDS(null, "select t1.id,checkscore from  sal_person_postduty_score t1  where checkeduser='" + currCheckedUserid + "' and t1.scoreuser='" + userid + "' and t1.logid='" + currLog.getStringValue("id") + "' and t1.targettype ='��������ָ��' and (t1.checkscore is null or t1.checkscore='') ");
			if (undo.size() > 0) { // ���ݿ����п�ֵ.ҳ��������.��Ҫ����ͬ��ҳ�����ݵ�������.
				List updateSqls = new ArrayList();
				for (int i = value_row_start_index; i < lastRowNum; i++) {
					for (int j = value_col_start_index; j < lastColNum; j++) {
						BillCellItemVO itemVO = cellPanel.getBillCellItemVOAt(i, j);
						if ("Y".equals(itemVO.getCustProperty("nohave"))) {
							continue;
						}
						HashVO hashvo = (HashVO) itemVO.getCustProperty("hashvo");
						if (!undo.containsKey(hashvo.getStringValue("id"))) {
							continue;
						}
						String score = cellPanel.getValueAt(i, j);
						UpdateSQLBuilder sqlBuilder = new UpdateSQLBuilder("sal_person_postduty_score"); // ����updateһ��
						if (hashvo != null) {
							sqlBuilder.setWhereCondition(" id = '" + hashvo.getStringValue("id") + "'");
						} else {
							MessageBox.showWarn(this, "����[" + convertIntColToEn(j + 1) + "" + (i + 1) + "]���ݴ����������ش���,�뾡����ϵ����Ա");
							InsertSQLBuilder insert = new SalaryUIUtil().getErrLogSql("��λְ�����������ûȡ��[hashvo]ֵ", "���ݴ���", "��������PostDutyMutualWKPanel��,����[" + convertIntColToEn(j + 1) + "" + (i + 1) + "]ִ��getCustProperty����ȡ����hashvoֵ��", "����");
							if (insert != null) {
								UIUtil.executeUpdateByDS(null, insert);
							}
							return;
						}
						sqlBuilder.putFieldValue("lasteditdate", UIUtil.getServerCurrDate());
						sqlBuilder.putFieldValue("checkscore", score);
						sqlBuilder.putFieldValue("status", "���ύ");
						updateSqls.add(sqlBuilder);
					}
				}
				UIUtil.executeBatchByDS(null, updateSqls, true, false);
				// �����²�һ�� ���ݿ�
				String strtotle = UIUtil.getStringValueByDS(null, "select count(t1.id) from  sal_person_postduty_score t1  where  t1.checkeduser = '" + currCheckedUserid + "' and t1.scoreuser='" + userid + "' and t1.logid='" + currLog.getStringValue("id") + "' and t1.targettype ='��������ָ��' and (t1.checkscore is null or t1.checkscore='')");
				if (!"0".equals(strtotle)) { // ��������ݿ��еĿ�ֵ���Ϻ��ٲ�һ�飬�ڲ�һ���Ͳ����ˡ�ֱ��ˢ��ҳ�����ݡ��û��Լ��㡣
					currCheckVOs = UIUtil.getHashVoArrayByDS(null, "select t1.* from  sal_person_postduty_score t1 left join sal_post_duty_target_list t2 on t1.targetid=t2.id where t1.checkeduser ='" + currCheckedUserid + "' and t1.scoreuser='" + userid + "' and t1.logid='" + currLog.getStringValue("id") + "' and t1.targettype ='��������ָ��' order by t1.targetid,t1.id ");
					//					cellPanel.loadBillCellData(getBillCellVo());// ���¼��ط�����������
					MessageBox.show(cellPanel, "ҳ������δ�ܼ�ʱͬ����������,�����Ѿ�ˢ��,�����º�ʵ��©��.");
					return;
				}
			}
			if (!MessageBox.confirm(cellPanel, "�ύ�����޸ģ�ȷ���ύ��")) {
				return;
			}
			UpdateSQLBuilder updateSql = new UpdateSQLBuilder("sal_person_postduty_score");
			updateSql.putFieldValue("status", "���ύ");
			updateSql.setWhereCondition(" checkeduser = '" + currCheckedUserid + "' and logid='" + currLog.getStringValue("id") + "' and   scoreuser='" + userid + "'");
			int num = UIUtil.executeUpdateByDS(null, updateSql);
			if (num == alldatanum) {
				cellPanel.setEditable(false);
				issubmited.set(currSelectIndex, Boolean.TRUE);
				setCurrCellPanelStatus(cellPanel, "�� �� ��");

				String unCheckedUser[] = UIUtil.getStringArrayFirstColByDS(null, "select distinct(checkeduser) checkeduser from sal_person_postduty_score where 1=1  and scoreuser='" + userid + "' and logid='" + currLog.getStringValue("id") + "' and targettype ='��������ָ��' and (checkscore is null or checkscore='')");

				if (unCheckedUser != null && unCheckedUser.length > 0) {
					int changeIndex = -1;
					for (int k = 0; k < uservo.size(); k++) {//�������пɴ����Ա
						for (int k2 = 0; k2 < unCheckedUser.length; k2++) {
							if (uservo.get(k).getStringValue("id", "").equals(unCheckedUser[k2])) {
								changeIndex = k;
								break;
							}
						}
						if (changeIndex >= 0) {
							break;
						}
					}
					tabp.setSelectedIndex(changeIndex);
					return;
				}
				String[][] result = UIUtil.getStringArrayByDS(null, "select count(id),checktype from sal_person_check_score where scoreuser = " + userid + " and (status <> '���ύ' or status is null) and logid=" + currLog.getStringValue("id") + " group by checktype" + " union all select count(id),targettype checktype from sal_dept_check_score where  scoreuser = " + userid
						+ " and targettype='���Ŷ���ָ��' and (status <> '���ύ' or status is null) and logid=" + currLog.getStringValue("id") + " group by targettype");
				StringBuffer msg = new StringBuffer("����û�ж� ");
				boolean have = false;
				for (int i = 0; i < result.length; i++) {
					if (!"0".equals(result[i][0])) {
						msg.append(result[i][1] + "��");
						have = true;
					}
				}
				if (have) {
					msg = new StringBuffer(msg.substring(0, msg.length() - 1));
				}
				if (have) {
					msg.append(" �������ֻ����ύ�����������Ϸ�ҳǩ����ɲ���.");
					MessageBox.show(cellPanel, msg.toString());
				} else {
					MessageBox.show(cellPanel, "���Ѿ�����˱������д������,лл��");
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setCurrCellPanelStatus(final JComponent parent, final String _status) {
		new Timer().schedule(new TimerTask() {
			public void run() {
				JPanel jp = new JPanel() {
					protected void paintComponent(Graphics g) {
						// ����
						if (parent != null && parent.isShowing()) { // �㵽bufferedimage���ر������
							super.paintComponent(g);
							Graphics2D g2d = (Graphics2D) g.create();
							g2d.translate(15, 8);
							g2d.setComposite(AlphaComposite.SrcOver.derive(0.6f));
							g2d.setColor(Color.red.brighter());
							g2d.rotate(0.4);
							g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
							g2d.setStroke(new BasicStroke(3.5f));
							g2d.drawRect(1, 1, 150, 30);
							g2d.setFont(new Font("����", Font.BOLD, 25));
							g2d.drawString(_status, 15, 25);
						}
					}
				};
				jp.setOpaque(false);
				int i = 0;
				while (!parent.isShowing()) {
					i++;
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (i > 300) { //30���ٳ�������ֱ�ӷ��ء�Ӧ�ò�����ô����
						return;
					}
				}
				Point p = parent.getLocationOnScreen();
				SwingUtilities.convertPointFromScreen(p, parent.getRootPane().getLayeredPane());
				BillCellPanel cellPanel = (BillCellPanel) parent;
				int tablewidth = (int) cellPanel.getTable().getVisibleRect().getWidth();
				int tableheight = (int) cellPanel.getTable().getVisibleRect().getHeight();
				Rectangle rect = new Rectangle((tablewidth / 2 - 40), tableheight / 2, 180, 130);
				DivComponentLayoutUtil.getInstanse().addComponentOnDiv(cellPanel, jp, rect, null); // ���Ӹ�����
			}
		}, 100);
	}

	private String convertIntColToEn(int _index) {
		return stbutil.convertIntColToEn(_index,false);
	}

	private String getColor(int index) {
		String[] beautifulColors = new String[] { "255,0,0", "255,60,0", "255,30,0",//
				"254,0,254", "0,254,254", "128,0,254", "0,128,0", "254,254,128", "128,0,128", "64,0,128", "128,128,0", "128,128,192", "254,128,192", "0,128,254", "128,64,0",//
				"225,213,218", "74,63,228", "200,234,210", "224,242,26", "231,192,229", "98,69,77", "192,188,226", "73,129,90", "241,244,210",//
				"225,39,214", "195,229,226", "169,143,80", "225,48,94", "100,99,108", "42,231,99", "153,159,87", "96,69,94", "74,143,138", "244,178,18" }; // һϵ�кÿ�����ɫ,һ��25��,���ν�������
		return beautifulColors[index % 30];
	}

	public void actionPerformed(ActionEvent arg0) {
		int index = tabp.getSelectedIndex();
		onCellPanelSubmit(allCellPanel.get(index));
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		boolean issub = issubmited.get(tabp.getSelectedIndex()); //�ж��Ƿ��Ѿ��ύ��
		btn_submit.setEnabled(!issub);
	}

}