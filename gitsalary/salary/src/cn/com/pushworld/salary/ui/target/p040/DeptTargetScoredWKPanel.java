package cn.com.pushworld.salary.ui.target.p040;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
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
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.DivComponentLayoutUtil;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.DivComponentLayoutUtil.DivComponentCustomResetRectListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_TextField;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessDialog;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.ui.targetcheck.SalaryBomClickEvent;
import cn.com.pushworld.salary.ui.targetcheck.SalaryBomClickListener;
import cn.com.pushworld.salary.ui.targetcheck.SalaryBomPanel;

/**
 * ���ż�Ч���ֽ���
 * 
 * @author haoming create by 2013-6-28
 */
public class DeptTargetScoredWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 5488377030880962619L;
	private BillListPanel targetScoreListPanel = new BillListPanel("SAL_DEPT_CHECK_SCORE_CODE1"); // ָ���������顣
	private WLTButton btn_submit, btn_apply_modify; // �����޸�
	private SalaryTBUtil stbutil = new SalaryTBUtil();
	private HashVO currLogID = null; // ��ǰ�ƻ���־ID��
	private int currPageState = 0; // ��ǰҳ��״̬.0ֱ������״̬ 1�����޸ı�ѡ�� 2���������޸�ά����

	public static final String SCORE_STATUS_INIT = "���ύ";
	public static final String SCORE_STATUS_COMMIT = "���ύ";
	public static final String SCORE_STATUS_MODIFY = "�����޸�";
	private boolean editable = true;
	private boolean autoCheckCommit = true;
	private String userid = ClientEnvironment.getCurrSessionVO().getLoginUserId(); // ��ǰ��¼��ID

	public void initialize() {
		try {
			initBtn();
			this.add(getCheckScorePanel());
			resetButton();
			checkNeedPopTip();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initBtn() {
		btn_submit = new WLTButton("ȫ���ύ", UIUtil.getImage("zt_071.gif"));
		btn_submit.setPreferredSize(new Dimension(85, 21));
		btn_submit.addActionListener(this);
		btn_apply_modify = new WLTButton("�����޸�", UIUtil.getImage("script--pencil.png"));
		btn_apply_modify.addActionListener(this);
		targetScoreListPanel.addBatchBillListButton(new WLTButton[] { btn_submit, btn_apply_modify });
		targetScoreListPanel.repaintBillListButton();
		targetScoreListPanel.setHeaderCanSort(false);
		targetScoreListPanel.setCanShowCardInfo(false);
		targetScoreListPanel.putClientProperty("reedit", "Y");
		targetScoreListPanel.getTable().setFont(new Font("����", Font.BOLD, 14));
	}

	/*
	 * �Զ����ʼ���������������á�
	 */
	public void customInit(String _userid, boolean _editable) {
		initBtn();
		userid = _userid;
		editable = _editable;
		targetScoreListPanel.putClientProperty("this", this);
		targetScoreListPanel.putClientProperty("editable", _editable);
		this.add(targetScoreListPanel);
	}

	/*
	 * ��ʾ��ʾ��Ϣ,ǿ��������ʾ��
	 */
	public void showHelpInfo() {
		new Timer().schedule(new TimerTask() {
			public void run() {
				while (true) {
					if (targetScoreListPanel.isShowing()) {
						help();
						break;
					}
					try {
						Thread.currentThread().sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}, 0);
	}

	/*
	 *  ����Ƿ���Ҫ��ʾ����λ�á���ɫ�򣬱�ʶ��
	 */
	public void checkNeedPopTip() {
		BillVO[] allVO = targetScoreListPanel.getAllBillVOs();
		boolean haveSeleced = false; //�Ƿ��Ѿ���д��.
		for (int i = 0; i < allVO.length; i++) {
			String onescorevalue = allVO[i].getStringValue("checkdratio");
			if (onescorevalue != null && !"".equalsIgnoreCase(onescorevalue)) { // ���ȫ��û����д����ɫ���
				haveSeleced = true;
				break;
			}
		}
		if (!haveSeleced) {
			showHelpInfo(); //��ʶ��ʾ��д��Χ��
		}
	}

	/*
	 * ��ʶ�Ķ�����ѡ����д��
	 */
	private void help() {
		final MYAlphaPanel panel = new MYAlphaPanel();
		panel.setOpaque(false);
		Rectangle cell = targetScoreListPanel.getTable().getCellRect(0, 4, true);
		Rectangle rect = new Rectangle(cell.x, 0, cell.width, targetScoreListPanel.getTable().getVisibleRect().height);
		DivComponentLayoutUtil.getInstanse().addComponentOnDiv(targetScoreListPanel.getTable(), panel, rect, new DivComponentCustomResetRectListener() {
			public void resetComponentDivLocation(JComponent divComponent) {
				Point thisPoint = targetScoreListPanel.getTable().getLocationOnScreen(); //��ȡ��ǰҳ���λ��
				SwingUtilities.convertPointFromScreen(thisPoint, targetScoreListPanel.getTable().getRootPane().getLayeredPane()); //ȡ����ǰ������layeredpane��λ��.
				int tableHeaderHeight = targetScoreListPanel.getTable().getTableHeader().getHeight();
				Point headerPoint = targetScoreListPanel.getTable().getTableHeader().getLocationOnScreen();
				SwingUtilities.convertPointFromScreen(headerPoint, targetScoreListPanel.getTable().getRootPane().getLayeredPane()); //ȡ����ǰ������layeredpane��λ��.
				Rectangle cell = targetScoreListPanel.getTable().getCellRect(0, targetScoreListPanel.findColumnIndex("checkdratio"), true);
				Rectangle rect = new Rectangle(thisPoint.x + cell.x, headerPoint.y + tableHeaderHeight, cell.width, targetScoreListPanel.getTable().getVisibleRect().height);
				divComponent.setBounds(rect);
			}
		}, -1);
		final javax.swing.Timer timer = new javax.swing.Timer(20, new ActionListener() { //��һ��
					int index = 0;

					public void actionPerformed(ActionEvent actionevent) {
						panel.setAlpha((float) Math.abs(Math.sin(Math.toRadians(index))));
						panel.repaint();
						if (index >= 540) {
							javax.swing.Timer tim = (javax.swing.Timer) actionevent.getSource();
							DivComponentLayoutUtil.getInstanse().removeComponentOnDiv(targetScoreListPanel.getTable(), panel);
							panel.setVisible(false);
							tim.stop();
						}
						index += 2;
					}
				});
		timer.start();
	}

	public void refreshListDataByLogVO(HashVO _logVO) {
		currLogID = _logVO;
		if (currLogID != null) {
			targetScoreListPanel.QueryDataByCondition(" logid='" + currLogID.getStringValue("id") + "' and targettype='���Ŷ���ָ��'  and  scoreuser='" + userid + "'");
		}
		targetScoreListPanel.setTitleLabelText(currLogID.getStringValue("name") + "   " + targetScoreListPanel.getTempletVO().getTempletname());
		currPageState = 0;
		resetButton();
		checkNeedPopTip();
	}

	private JPanel getCheckScorePanel() {
		try {
			HashVO vos[] = UIUtil.getHashVoArrayByDS(null, "select t1.id,t1.checkdate,t1.status from SAL_TARGET_CHECK_LOG t1,sal_dept_check_score t2 where t1.id = t2.logid and t1.status!='���˽���' and t2.scoreuser='" + userid + "' and targettype='���Ŷ���ָ��'  group by t1.id");
			if (vos == null || vos.length == 0) {
				JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(FlowLayout.CENTER));
				WLTLabel label = new WLTLabel("Ŀǰȫ��û�п����еļƻ�...");
				label.setFont(new Font("����", Font.BOLD, 16));
				panel.add(label);
				return panel;
			}
			if (vos.length > 1) {
				return getPlanBomPanel(vos);
			} else {
				currLogID = vos[0];
			}
			if (currLogID != null) {
				targetScoreListPanel.QueryDataByCondition(" logid='" + currLogID.getStringValue("id") + "' and scoreuser='" + ClientEnvironment.getCurrSessionVO().getLoginUserId() + "' and targettype='���Ŷ���ָ��'");
			}
			targetScoreListPanel.setTitleLabelText(currLogID.getStringValue("name") + "   " + targetScoreListPanel.getTempletVO().getTempletname());
			return targetScoreListPanel;
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JPanel();
	}

	public void actionPerformed(ActionEvent e) {
		targetScoreListPanel.stopEditing(); // ֻҪ�ǰ�ť����ֹͣ��ǰ�༭
		if (e.getSource() == btn_submit) {
			onBtn1();
		} else if (e.getSource() == btn_apply_modify) {
			onBtn2();
		}
	}

	// ��һ����ť��ʵ���߼����˰�ť��ҵ��������á���Ҫ�������ύ��ȷ��������
	private void onBtn1() {
		switch (currPageState) {
		case 0:
			onSubmit(); // �ύ���п�����¼
			break;
		case 1:
			goToModify();// ǰ���޸Ľ��档
			break;
		case 2:
			onSubmitModifyByProcess();
			break;
		}
	}

	// ���ݵ�ǰ״̬���ð�ť
	private void resetButton() {
		BillVO vos[] = targetScoreListPanel.getAllBillVOs();
		boolean commited = false; // �Ƿ��Ѿ��ύ����
		boolean updatestate = false; //�Ƿ��Ǹ���
		if (vos.length > 0 && !SCORE_STATUS_INIT.equals(vos[0].getStringValue("status"))) {
			for (int i = 0; i < vos.length; i++) {
				String oneScoreStatus = vos[i].getStringValue("status");
				if (oneScoreStatus == null || oneScoreStatus.equals(SCORE_STATUS_INIT)) { // �����һ����¼�ǳ�ʼ��¼
					commited = false;
					updatestate = false;
					break;
				} else if (SCORE_STATUS_MODIFY.equals(oneScoreStatus)) {
					updatestate = true;
					commited = true; //	
				} else {
					commited = true; //					
				}
			}
		}

		switch (currPageState) {
		case 0:
			btn_submit.setText("�ύ");
			btn_submit.setIcon(UIUtil.getImage("zt_050.gif"));
			btn_submit.setToolTipText("�ύ���, �ύ�����޸�");
			btn_apply_modify.setText("�����޸�");
			btn_apply_modify.setIcon(UIUtil.getImage("script--pencil.png"));
			btn_apply_modify.setToolTipText("���ύ�Ŀ�����Ϣ����ͨ�����룬�޸�ʵ�ʿ۷ֱ�����");

			targetScoreListPanel.setItemVisible("recheckdratio", false);
			targetScoreListPanel.setRowNumberChecked(false);

			btn_submit.setVisible(!commited); // �Ѿ��ύ������ֻ���޸ġ�
			if (commited && !updatestate) { //����Ѿ��ύ
				setCurrCellPanelStatus(targetScoreListPanel, "�� �� ��");
			} else if (updatestate) {
				setCurrCellPanelStatus(targetScoreListPanel, "�����޸���");
			}
			btn_apply_modify.setVisible(commited);
			break;
		case 1:
			btn_submit.setText("��һ��");
			btn_submit.setIcon(new UIUtil().getImage("office_190.gif"));
			btn_submit.setToolTipText("");

			btn_apply_modify.setText("ȡ��");
			btn_apply_modify.setIcon(new UIUtil().getImage("office_078.gif"));
			targetScoreListPanel.setRowNumberChecked(true);

			btn_submit.setVisible(true); // �˽�����ʾ������ť
			btn_apply_modify.setVisible(true);
			DivComponentLayoutUtil.getInstanse().removeComponentOnDiv(targetScoreListPanel, statusPanel);
			break;
		case 2:
			btn_submit.setText("�ύ����");
			btn_submit.setIcon(new UIUtil().getImage("zt_028.gif"));
			btn_submit.setToolTipText("");
			btn_apply_modify.setText("����");
			btn_apply_modify.setIcon(new UIUtil().getImage("pagefirst.gif"));
			btn_apply_modify.setToolTipText("�������ֽ���");
			targetScoreListPanel.setItemVisible("recheckdratio", true);
			targetScoreListPanel.setRowNumberChecked(false);
			autoCheckCommit = true;//���ü���Զ��ύ
			break;
		}
	}

	/*
	private void setStatusIfNeed() { //�Ƿ���Ҫ����.�����ǡ����ύ�������޸������С�

	}
	*/
	/*
	 * �˰�ť��Ҫ�������Զ���ѡ������ ��Ҫ�޸ġ�ȡ�������ص�.����Ӱ������ʵ��ֵ
	 */
	private void onBtn2() {
		switch (currPageState) {
		case 0: // ��Ҫ�޸�
			iWantToModify();
			break;
		case 1: // ����
			onSelectModifyBack();
			break;
		case 2: // ����
			onModifyBack();
			break;
		}
	}

	// �ύ�����޸ĵļ�¼�߹�������
	private void onSubmitModifyByProcess() {
		BillVO allVOs[] = targetScoreListPanel.getAllBillVOs();
		for (int i = 0; i < allVOs.length; i++) {
			String recheckdratio = allVOs[i].getStringValue("recheckdratio");
			if (recheckdratio == null || recheckdratio.equals("")) {
				MessageBox.show(this, "������һ��[�¿۷ֱ�]û����д.");
				return;
			}
		}
		InsertSQLBuilder sqlBuilder = new InsertSQLBuilder("sal_score_process");
		String newID = "";
		try {
			newID = UIUtil.getSequenceNextValByDS(null, "S_sal_score_process");
			sqlBuilder.putFieldValue("id", newID);
			sqlBuilder.putFieldValue("name", currLogID.getStringValue("name") + " ���˿۷��޸�����.");
			sqlBuilder.putFieldValue("fid", currLogID.getStringValue("id"));
			sqlBuilder.putFieldValue("createtime", UIUtil.getServerCurrTime());
			sqlBuilder.putFieldValue("billtype", "��Ч����");
			sqlBuilder.putFieldValue("busitype", "����޸�����");
			sqlBuilder.putFieldValue("type", "��Ч��������޸�����");
			sqlBuilder.putFieldValue("createuser", ClientEnvironment.getCurrLoginUserVO().getId());
			sqlBuilder.putFieldValue("username", ClientEnvironment.getCurrLoginUserVO().getName());
			sqlBuilder.putFieldValue("deptid", ClientEnvironment.getCurrSessionVO().getLoginUserId());

			String itemids = stbutil.getMutilValueStr(stbutil.getBillVosItemList(allVOs, "id"));
			sqlBuilder.putFieldValue("itemids", itemids);
			int flag = UIUtil.executeUpdateByDS(null, sqlBuilder);
			if (flag > 0) { // �Ѿ��������ݿ�
				BillListPanel listPanel = new BillListPanel("SAL_SCORE_PROCESS_CODE1");
				listPanel.QueryDataByCondition(" id = '" + newID + "'");
				if (listPanel.getRowCount() > 0) {
					listPanel.setSelectedRow(0);

					BillVO billVO = listPanel.getSelectedBillVO(); //
					String str_pkValue = billVO.getPkValue(); //
					if (str_pkValue == null || str_pkValue.trim().equals("")) {
						MessageBox.show(listPanel, "�ô������������ҵ�񵥾�û�в�ѯ������!\n������Ϊϵͳ���Խ׶ε�������ɵ�,��������Ȼ�ܹ�����!\nϵͳ���ߺ������������ݺ������Ӧ�ò����ٴ���,��֪Ϥ!"); //
					}
					WorkFlowProcessDialog processDialog = null; //
					BillCardPanel cardPanel = new BillCardPanel(listPanel.getTempletVO()); // ����һ����Ƭ���!!
					cardPanel.setBillVO(billVO.deepClone()); // �ڿ�Ƭ����������,��Ҫ��¡һ��!!
					processDialog = new WorkFlowProcessDialog(this, "���̴���", cardPanel, listPanel); // ����!!!��Ȼ��������ʵ��Ϊ�գ�����Ϣ����id����������id������ʵ��id����ֵ��Ϊnull���ʱ����в�Ҫ��ʾ�ˡ����/2012-03-28��
					processDialog.setVisible(true); //
					if (processDialog.getCloseType() != 1) { // ���û���ύ��ֱ��ɾ����
						UIUtil.executeUpdateByDS(null, "delete from sal_score_process where id =" + newID);
					} else {
						// ���Էŵ���������
						UIUtil.executeUpdateByDS(null, "update sal_dept_check_score set status='" + SCORE_STATUS_MODIFY + "' where id in(" + TBUtil.getTBUtil().getInCondition(itemids) + ")");
						onModifyBack(); // ֱ�ӷ�����ҳ��
					}
				}
			}
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * �����޸ġ�
	 */
	private void iWantToModify() {
		BillVO[] currVOs = targetScoreListPanel.getAllBillVOs();
		List<Integer> canotModifyRows = new ArrayList<Integer>(); // �ҳ������޸ĵļ�¼
		for (int i = 0; i < currVOs.length; i++) {
			String status = currVOs[i].getStringValue("status");
			if (!"���ύ".equals(status)) { // ֻ�������ύ״̬���ſ���ѡ��ȥ�ύ�޸�����
				canotModifyRows.add(i);
			}
		}
		if (canotModifyRows.size() == targetScoreListPanel.getRowCount()) { // Ŀǰû�п������������
			MessageBox.show(this, "Ŀǰû��[���ύ]�ļ�¼���������޸�!");
			return;
		}
		int removeRows[] = new int[canotModifyRows.size()];
		for (int i = 0; i < removeRows.length; i++) {
			removeRows[i] = (Integer) canotModifyRows.get(i);
		}
		targetScoreListPanel.removeRows(removeRows);// �Ѷ�����Ƴ�����ֻ��ʾ�¡����ύ���ġ�
		currPageState = 1; // ��Ҫ�޸�ѡ�����
		resetButton();
	}

	// ��ѡ���޸Ľ��淵�ء�
	private void onSelectModifyBack() {
		targetScoreListPanel.refreshCurrData();
		currPageState = 0;
		resetButton();
	}

	/*
	 * �ύ
	 */
	private void onSubmit() {
		StringBuffer notFill = new StringBuffer();
		BillVO checkItemVos[] = targetScoreListPanel.getBillVOs();
		int notFillCount = 0; // ��¼δ��д����
		List updateSQLList = new ArrayList<UpdateSQLBuilder>();
		for (int i = 0; i < checkItemVos.length; i++) {
			if (checkItemVos[i].getStringValue("checkdratio") == null || checkItemVos[i].getStringValue("checkdratio").equals("")) {
				notFill.append("��" + (i + 1) + "��" + " " + checkItemVos[i].getStringValue("targetname") + "\r\n");
				notFillCount++;
			} else {//����Ѿ���ֵ����ô�͸���
				UpdateSQLBuilder updateSql = new UpdateSQLBuilder("sal_dept_check_score");
				String checkdratio = checkItemVos[i].getStringValue("checkdratio"); // �۷ֱ���
				String weights = checkItemVos[i].getStringValue("weights"); // Ȩ�ط�ֵ
				float f_weights = Float.parseFloat(weights); // Ȩ�ط�ֵ
				float f_checkratio = Float.parseFloat(checkdratio); // �۷ֱ���
				float lastScore = f_weights * (100 - f_checkratio) / 100;
				updateSql.putFieldValue("checkscore", lastScore + "");
				updateSql.putFieldValue("checkdratio", checkdratio);
				updateSql.putFieldValue("scoredeptname", ClientEnvironment.getLoginUserDeptName());
				updateSql.setWhereCondition("id=" + checkItemVos[i].getPkValue());
				updateSQLList.add(updateSql);
			}
		}
		if (notFillCount > 0) {
			if (notFillCount >= 5) {
				MessageBox.show(this, "����" + notFillCount + "����¼û������.");
			} else {
				MessageBox.show(this, "����ѡ���ָ���в���δ���֣�\r\n" + notFill.toString());
			}
			return;
		}
		//�����������ٱ���һ�ε�������.
		try {
			UIUtil.executeBatchByDS(null, updateSQLList, true, false);
		} catch (Exception e1) {
			MessageBox.showException(targetScoreListPanel, e1);
			e1.printStackTrace();
			return;
		}

		if (checkItemVos.length <= 0 || !MessageBox.confirm(this, "�ύ����Ҫ���޸���Ҫ����������\r\nȷ��Ҫ�ύ���м�¼��")) {
			return;
		}
		String update = "update sal_dept_check_score set status='" + SCORE_STATUS_COMMIT + "' where id in(" + stbutil.getInCondition(checkItemVos, "id") + ") and checkdratio is not null and (status='" + SCORE_STATUS_INIT + "' or status is null)";
		try {
			UIUtil.executeUpdateByDS(null, update);
			targetScoreListPanel.refreshData();
			String[][] result = UIUtil.getStringArrayByDS(null, "select count(id),checktype from sal_person_check_score where scoreuser = " + userid + " and (status <> '���ύ' or status is null) and logid=" + currLogID.getStringValue("id") + " group by checktype");
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
				MessageBox.show(targetScoreListPanel, msg.toString());
			} else {
				MessageBox.show(targetScoreListPanel, "���Ѿ�����˱������д������,лл��");
			}
			resetButton();
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// ǰ���޸Ľ���
	private void goToModify() {
		BillVO selectVOs[] = targetScoreListPanel.getCheckedBillVOs();
		if (selectVOs.length == 0) {
			MessageBox.show(this, "�빴ѡҪ�޸ĵ�����.");
			return;
		}
		targetScoreListPanel.QueryDataByCondition(" id in(" + stbutil.getInCondition(selectVOs, "id") + ")");
		currPageState = 2;
		resetButton();
	}

	// �����޸����´�ֽ���ֱ�ӷ������֡�
	private void onModifyBack() {
		targetScoreListPanel.QueryDataByCondition(" logid='" + currLogID.getStringValue("id") + "' and scoreuser='" + ClientEnvironment.getCurrSessionVO().getLoginUserId() + "'");
		targetScoreListPanel.setItemVisible("recheckdratio", false);
		targetScoreListPanel.setItemEditable("recheckdratio", false);
		targetScoreListPanel.setItemEditable("checkdratio", true);
		currPageState = 0;
		DivComponentLayoutUtil.getInstanse().removeComponentOnDiv(targetScoreListPanel, statusPanel);
		resetButton();
	}

	public void setSelectRow(int _row) {
		targetScoreListPanel.setSelectedRow(_row);
	}

	public void autoCheckIFCanCommit() {
		if (!autoCheckCommit) {//�Ƿ���Ҫ�Զ����
			return;
		}
		if (currPageState == 0) {//����
			BillVO vos[] = targetScoreListPanel.getAllBillVOs();
			for (int i = 0; i < vos.length; i++) {
				if (vos[i].getStringValue("checkdratio") == null || !SCORE_STATUS_INIT.equals(vos[i].getStringValue("status"))) { //���û����д����
					return;
				}
			}
		} else if (currPageState == 2) {//�����޸�
			BillVO vos[] = targetScoreListPanel.getAllBillVOs();
			for (int i = 0; i < vos.length; i++) {
				if (vos[i].getStringValue("recheckdratio") == null) { //
					return;
				}
			}
		}
		new Timer().schedule(new TimerTask() {
			public void run() {
				String msg = "";
				if (currPageState == 0) {
					msg = "��ҳ�����������,�Ƿ��ύ���?";
				} else if (currPageState == 2) {
					msg = "��Ҫ�����޸ĵļ�¼���ύ���Ƿ��ύ���?";
				}
				int index = MessageBox.showOptionDialog(targetScoreListPanel, msg, "ϵͳ��ʾ", new String[] { "�����ύ", "�Ժ��ύ" });
				if (index != 0) {
					autoCheckCommit = false;// �����ϲ���Զ���ʾ������Ϊfalse��
					return;
				}
				if (currPageState == 0) {
					onSubmit();
				} else if (currPageState == 2) {
					onSubmitModifyByProcess();
				}
			}
		}, 550); //�ӳ�һ����ٵ�����.���ܺ�һЩ��
	}

	/*
	 * �õ��Լ��ļƻ�
	 */
	SalaryBomPanel planBomPanel = new SalaryBomPanel();

	private SalaryBomPanel getPlanBomPanel(HashVO[] _planLogs) {
		for (int i = 0; i < _planLogs.length; i++) {
			_planLogs[i].setToStringFieldName("checkdate");
		}

		planBomPanel.addBomPanel(Arrays.asList(_planLogs));
		planBomPanel.addBomClickListener(new SalaryBomClickListener() {
			public void onBomClickListener(SalaryBomClickEvent event) {
				currLogID = event.getHashvo();
				targetScoreListPanel.QueryDataByCondition(" logid='" + currLogID.getStringValue("id") + "' and scoreuser='" + ClientEnvironment.getCurrSessionVO().getLoginUserId() + "'");
				planBomPanel.addBomPanel(targetScoreListPanel);
				currPageState = 0;
				resetButton();
			}
		});
		return planBomPanel;
	}

	private class MYAlphaPanel extends JPanel {
		private float alpha = 0;

		public void setAlpha(float alpha) {
			this.alpha = alpha;
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setComposite(AlphaComposite.SrcOver.derive(alpha));
			g2d.setStroke(new BasicStroke(2));
			g2d.setColor(Color.green);
			g2d.drawRect(1, 1, getWidth() - 2, getHeight() - 1);
		}
	}

	private JPanel statusPanel; //��ʾ״̬�µ����

	private void setCurrCellPanelStatus(final JComponent parent, final String _status) {
		new Timer().schedule(new TimerTask() {
			public void run() {
				while (!parent.isShowing()) {
					try {
						Thread.currentThread().sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}
				statusPanel = new JPanel() {
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
				statusPanel.setOpaque(false);
				Point p = parent.getLocationOnScreen();
				SwingUtilities.convertPointFromScreen(p, parent.getRootPane().getLayeredPane());
				BillListPanel cellPanel = (BillListPanel) parent;
				int tablewidth = (int) cellPanel.getTable().getVisibleRect().getWidth();
				int tableheight = (int) cellPanel.getTable().getVisibleRect().getHeight();
				Rectangle rect = new Rectangle((tablewidth / 2 - 40), tableheight / 2, 180, 130);
				DivComponentLayoutUtil.getInstanse().addComponentOnDiv(cellPanel, statusPanel, rect, null); //��Ӹ�����
			}
		}, 50);
	}
}

class ScoreDialog extends BillDialog implements ActionListener {
	private boolean canEdit;
	private WLTButton btn_forward, btn_next, btn_confirm;
	private BillListPanel listPanel;
	private BillCardPanel cardPanel;

	public ScoreDialog(Container _parent, boolean _canEdit, BillListPanel _listPanel) {
		super(_parent);
		this.canEdit = _canEdit;
		listPanel = _listPanel;
		init();
	}

	private void init() {
		cardPanel = new BillCardPanel("V_SAL_TARGET_ITEMSCORE_CODE1");
		this.setLayout(new BorderLayout());
		WLTPanel btnpanel = new WLTPanel(new FlowLayout(FlowLayout.CENTER));
		btn_forward = new WLTButton("��һ��", UIUtil.getImage("zt_072.gif"));
		btn_next = new WLTButton("��һ��", UIUtil.getImage("zt_073.gif"));
		btn_confirm = new WLTButton("ȷ��", UIUtil.getImage("office_175.gif"));
		btn_forward.addActionListener(this);
		btn_next.addActionListener(this);
		btn_confirm.addActionListener(this);
		btnpanel.add(btn_forward);
		btnpanel.add(btn_next);
		btnpanel.add(btn_confirm);
		this.add(cardPanel, BorderLayout.CENTER);
		this.add(btnpanel, BorderLayout.SOUTH);
		BillVO score = listPanel.getSelectedBillVO();
		String scoreid = score.getStringValue("scoreid");
		cardPanel.queryDataByCondition(" scoreid = " + scoreid);
		setSize(650, 600);
		this.locationToCenterPosition();
		SwingUtilities.invokeLater(new Runnable() { // �ӳ��趨��������û��Ĭ��λ�á�
					public void run() {
						setCurrPageEditState();
					}
				});

	}

	public BillCardPanel getCardPanel() {
		return cardPanel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_forward) {
			onForward();
		} else if (e.getSource() == btn_next) {
			onNext();
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		}
	}

	private void onForward() {
		if (!saveCurrPageData()) { //
			return;
		}
		int currIndex = listPanel.getSelectedRow();
		if (currIndex > 0) {
			listPanel.setSelectedRow(--currIndex);
		}
		BillVO score = listPanel.getSelectedBillVO();
		String scoreid = score.getStringValue("scoreid");
		cardPanel.queryDataByCondition(" scoreid = " + scoreid);
		setCurrPageEditState();
	}

	/*
	 * ���浱ǰҳ��ֵ������ˢ���б����ݡ�
	 */
	private boolean saveCurrPageData() {
		cardPanel.stopEditing();
		if (!cardPanel.checkValidate()) {
			return false;
		}
		if (cardPanel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
			BillVO scoreVO = cardPanel.getBillVO();
			UpdateSQLBuilder update = new UpdateSQLBuilder("sal_target_plan_score");
			update.putFieldValue("score", scoreVO.getStringValue("score"));
			update.putFieldValue("createdate", scoreVO.getStringValue("createdate"));
			update.putFieldValue("status", scoreVO.getStringValue("status"));
			update.setWhereCondition(" id =" + scoreVO.getStringValue("scoreid"));
			try {
				UIUtil.executeUpdateByDS(null, update);
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		listPanel.setValueAtRow(listPanel.getSelectedRow(), cardPanel.getBillVO());
		return true;
	}

	/*
	 * ��һ��
	 */
	private void onNext() {
		if (!saveCurrPageData()) {
			return;
		}
		int currIndex = listPanel.getSelectedRow();
		if (currIndex < listPanel.getRowCount() - 1) {
			listPanel.setSelectedRow(++currIndex);
		}
		BillVO score = listPanel.getSelectedBillVO();
		String scoreid = score.getStringValue("scoreid");
		cardPanel.queryDataByCondition(" scoreid = " + scoreid);
		setCurrPageEditState();
	}

	/*
	 * ȷ�����ر�
	 */
	private void onConfirm() {
		if (!saveCurrPageData()) {
			return;
		}
		this.dispose();

	}

	/*
	 * ���õ�ǰҳ��״̬
	 */
	public void setCurrPageEditState() {
		BillVO vo = cardPanel.getBillVO();
		if (DeptTargetScoredWKPanel.SCORE_STATUS_COMMIT.equals(vo.getStringValue("status"))) {
			cardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_INIT);
		} else {
			cardPanel.setEditableByEditInit();
			CardCPanel_TextField filed = (CardCPanel_TextField) cardPanel.getCompentByKey("score");
			filed.getTextField().requestFocus();
		}
		cardPanel.repaint();
	}
}
