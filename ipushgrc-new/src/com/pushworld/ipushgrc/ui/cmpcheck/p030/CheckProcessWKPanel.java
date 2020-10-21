package com.pushworld.ipushgrc.ui.cmpcheck.p030;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.keywordreplace.TemplateToWordUIUtil;

/**
 * ������ʵʩ! ��¼����׸�,Υ���¼�,�ɹ�����,���� 
 * 
 * @author Gwang
 * 
 */
public class CheckProcessWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillCardPanel billCard_check;
	private BillListPanel billList_check_item = null; // ���!

	private WLTButton btn_sportList, btn_manuscript, btn_addevent, btn_addward, btn_addissue;
	private WLTButton btn_exportWord;

	private String loginUserID = ClientEnvironment.getCurrSessionVO().getLoginUserId();// ��½��ԱID

	private WLTButton impIssue = null;

	private BillCardPanel cardPanel = null;

	@Override
	public void initialize() {
		// �������
		billCard_check = new BillCardPanel("CMP_CHECK_CODE2");
		billCard_check.setVisiable("items", false);
		btn_sportList = new WLTButton("ѡ��");
		btn_sportList.addActionListener(this);
		billCard_check.addBatchBillCardButton(new WLTButton[] { btn_sportList });
		billCard_check.repaintBillCardButton();

		// ����ӱ�
		billList_check_item = new CheckItemListPanel("CMP_CHECK_ITEM_CODE2", 1);
		btn_manuscript = new WLTButton("���������");
		btn_manuscript.addActionListener(this);
		btn_addissue = new WLTButton("����¼��");
		btn_addissue.addActionListener(this);
		btn_addevent = new WLTButton("Υ���¼�¼��");
		btn_addevent.addActionListener(this);
		btn_addward = new WLTButton("�ɹ�����¼��");
		btn_addward.addActionListener(this);
		btn_exportWord = new WLTButton("����word");
		btn_exportWord.addActionListener(this);
		billList_check_item.addBatchBillListButton(new WLTButton[] { btn_manuscript, btn_addissue, btn_addevent, btn_addward, btn_exportWord }); //
		billList_check_item.repaintBillListButton(); // �ػ水ť!
		// �����ʼ��ʱ�ӱ�ťȫ�����ã� ������ѡ��һ�������¼
		btn_manuscript.setEnabled(false);
		btn_addissue.setEnabled(false);
		btn_addevent.setEnabled(false);
		btn_addward.setEnabled(false);
		btn_exportWord.setEnabled(false);
		// ���·ָ�
		WLTSplitPane split = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billCard_check, billList_check_item); //
		split.setDividerLocation(310); // ���½����С
		this.add(split);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_sportList) { // ����-ѡ��
			onSportList();
		} else if (obj == btn_manuscript) { // �׸�¼��
			onManuscript();
		} else if (obj == btn_addevent) { // Υ���¼�
			onAddEvent();
		} else if (obj == btn_addward) { // �ɹ�����
			onAddWard();
		} else if (obj == btn_addissue) { // ����
			onAddIssue();
		} else if (obj == btn_exportWord) {
			exportWord();
		} else if (obj == impIssue) { //��������ʶ��Υ���¼�
			onImpIssue();
		}
	}

	/*
	 * �½�Υ���¼�ʱ  �����⵼�벢��������
	 */
	private void onImpIssue() {

		BillListDialog dialog = new BillListDialog(cardPanel, "��ѡ���ֵ����⣡", "CMP_ISSUE_CODE1");
		BillListPanel issuepanel = dialog.getBilllistPanel();
		issuepanel.QueryDataByCondition(" cmp_check_item_id = " + billList_check_item.getSelectedBillVO().getStringValue("id"));
		dialog.setVisible(true);
		if (dialog.getCloseType() != 1) {
			return;
		}
		BillVO selectvo = dialog.getReturnBillVOs()[0];
		cardPanel.setValueAt("eventdescr", new StringItemVO(selectvo.getStringValue("issuedescr")));
		cardPanel.setValueAt("findchannel", selectvo.getComBoxItemVOValue("findchannel"));
		cardPanel.setValueAt("finddate", selectvo.getRefItemVOValue("finddate"));
		cardPanel.setValueAt("issueid", new StringItemVO(selectvo.getStringValue("id")));
		cardPanel.setValueAt("eventcorpid", selectvo.getRefItemVOValue("eventcorpid"));

	}

	public void exportWord() {
		BillVO[] checkItemVOs = billList_check_item.getSelectedBillVOs();
		if (checkItemVOs == null || checkItemVOs.length == 0) {
			MessageBox.showSelectOne(this);
			return;
		}
		int fileNum = checkItemVOs.length;
		String template_name = null;
		TemplateToWordUIUtil tem_word_util = new TemplateToWordUIUtil();
		try {
			template_name = TemplateToWordUIUtil.getTemplateName(this, billList_check_item.getTempletVO().getTempletcode());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (template_name == null) {
			return;
		}
		HashMap[] maps = new HashMap[fileNum];
		String[] fileNames = new String[fileNum];
		String checkname = billCard_check.getBillVO().getStringValue("checkname");
		TBUtil tbutil = new TBUtil();
		for (int i = 0; i < maps.length; i++) {
			maps[i] = TemplateToWordUIUtil.convertBillVOToMap(checkItemVOs[i]);
			StringBuffer issue_sb = new StringBuffer();
			try {
				String[] issuedesr = UIUtil.getStringArrayFirstColByDS(null, "select issuedescr from cmp_issue where cmp_check_item_id = " + checkItemVOs[i].getStringValue("id"));
				for (int j = 0; j < issuedesr.length; j++) {
					if (j == (issuedesr.length - 1)) {
						issue_sb.append((j + 1) + "��" + issuedesr[j]);
					} else {
						issue_sb.append((j + 1) + "��" + issuedesr[j] + "</w:t></w:r></w:p><w:p wsp:rsidR=\"00DD0DF9\" wsp:rsidRDefault=\"00760576\"><w:r><w:rPr><w:rFonts w:hint=\"fareast\"/><wx:font wx:val=\"����\"/></w:rPr><w:t>");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			maps[i].put("issue", issue_sb.toString());
			fileNames[i] = checkname + "-" + checkItemVOs[i].getStringValue("checkitem_descr") + "_" + (i + 1);//�ļ�����,��Ҫ����һ�£���Ϊ�ļ����в��ܰ���һ��������ţ�\/:*?"<>|
			fileNames[i] = tbutil.replaceAll(fileNames[i], "\\", "��");
			fileNames[i] = tbutil.replaceAll(fileNames[i], "/", "��");
			fileNames[i] = tbutil.replaceAll(fileNames[i], ":", "��");
			fileNames[i] = tbutil.replaceAll(fileNames[i], "*", "��");
			fileNames[i] = tbutil.replaceAll(fileNames[i], "?", "��");
			fileNames[i] = tbutil.replaceAll(fileNames[i], "\"", "��");
			fileNames[i] = tbutil.replaceAll(fileNames[i], "<", "��");
			fileNames[i] = tbutil.replaceAll(fileNames[i], ">", "��");
			fileNames[i] = tbutil.replaceAll(fileNames[i], "|", "��");//ע�������������ǲ�һ����Ŷ
			fileNames[i] = tbutil.replaceAll(fileNames[i], " ", "");
		}
		try {
			tem_word_util.createWordByMap(maps, fileNames, template_name, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * ����-ѡ��
	 */
	private void onSportList() {
		BillListDialog listDialog = new BillListDialog(this, "��ѡ��һ�����", "CMP_CHECK_CODE2");
		// ���������� δ��� and С���ڳ�Ա���鳤
		BillListPanel listpanel = listDialog.getBilllistPanel();
		listpanel.queryDataByCondition(" status < 3 and (teamleader = " + loginUserID + " or teamusers like '%;" + loginUserID + ";%')", "");
		listpanel.setDataFilterCustCondition(" status < 3 and (teamleader = " + loginUserID + " or teamusers like '%;" + loginUserID + ";%')");
		listpanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		listpanel.repaintBillListButton();
		listDialog.setVisible(true);
		if (listDialog.getCloseType() != 1) {
			return;
		}

		BillVO sportVo = listDialog.getReturnBillVOs()[0];

		if (sportVo.getStringValue("status").equals("1")) {
			int ret = MessageBox.showOptionDialog(this, "�Ƿ�ȷ����ʼʵʩ�����?", "��ʾ", new String[] { "ȷ��", "ȡ��" });
			if (ret == -1) {
				return;
			} else if (ret == 0) {
				updateStatus("2", sportVo.getStringValue("id"));
			} else {
				return;
			}
		}

		// ���������¼ˢ���ӱ�
		billCard_check.setBillVO(sportVo);
		billList_check_item.removeAllRows();
		billList_check_item.QueryDataByCondition(" cmp_check_id = " + sportVo.getStringValue("id"));

		// �����ӱ�ť����
		btn_manuscript.setEnabled(true);
		btn_addissue.setEnabled(true);
		btn_addevent.setEnabled(true);
		btn_addward.setEnabled(true);
		btn_exportWord.setEnabled(true);
		billList_check_item.putClientProperty("checkedcorp", sportVo.getRefItemVOValue("checkedcorp"));
	}

	/*
	 * �׸�¼�� ʵ�ʾ��Ƕ�ԭ�ӱ��¼�����޸�
	 */
	private void onManuscript() {
		BillVO checkItemVO = billList_check_item.getSelectedBillVO();
		if (checkItemVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel("CMP_CHECK_ITEM_CODE2");
		cardPanel.setBillVO(checkItemVO);
		BillCardDialog cardDialog = new BillCardDialog(this, "���׸�¼��", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		cardDialog.setVisible(true);

		if (cardDialog.getCloseType() == 1) {
			billList_check_item.refreshCurrSelectedRow();
		}
	}

	/*
	 * Υ���¼�¼�� ��Ϲ��¼������������ݣ� ���ͼ��������
	 */
	private void onAddEvent() {
		BillVO checkItemVO = billList_check_item.getSelectedBillVO();
		if (checkItemVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		addEvent(checkItemVO);
	}

	/*
	 * ����Υ���¼�
	 */
	private void addEvent(BillVO checkItemVO) {
		// �����������ڣ� �����ˢ���ӱ��ж�Ӧ����
		if (showAddWin(checkItemVO, "����-Υ���¼�", "CMP_EVENT_CODE1")) {
			billList_check_item.refreshCurrSelectedRow();
		}
	}

	/*
	 * �ɹ�����¼�� ��Ϲ�ɹ����������������ݣ� ���ͼ��������
	 */
	private void onAddWard() {
		BillVO checkItemVO = billList_check_item.getSelectedBillVO();
		if (checkItemVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		addWard(checkItemVO);
	}

	/*
	 * �����ɹ�����
	 */
	private void addWard(BillVO checkItemVO) {
		// �����������ڣ� �����ˢ���ӱ��ж�Ӧ����
		if (showAddWin(checkItemVO, "����-�ɹ�����", "CMP_WARD_CODE1")) {
			billList_check_item.refreshCurrSelectedRow();
		}
	}

	/*
	 * ��������¼�� ��Ϲ���������������ݣ� ���ͼ��������
	 */
	private void onAddIssue() {
		BillVO checkItemVO = billList_check_item.getSelectedBillVO();
		if (checkItemVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		addIssue(checkItemVO);
	}

	/*
	 * ������鷢������
	 */
	private void addIssue(BillVO checkItemVO) {
		// �����������ڣ� �����ˢ���ӱ��ж�Ӧ����
		if (showAddWin(checkItemVO, "����-�������", "CMP_ISSUE_CODE1")) {
			billList_check_item.refreshCurrSelectedRow();
		}
	}

	/***************************************************************************
	 * ���������Ի���
	 * 
	 * @param pk
	 * @param title
	 * @param templetCode
	 * @return true-��ȷ������
	 */
	private boolean showAddWin(BillVO checkItemVO, String title, String templetCode) {
		cardPanel = new BillCardPanel(templetCode);
		cardPanel.putClientProperty("checkedcorp", billCard_check.getBillVO().getRefItemVOValue("checkedcorp"));
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		if (cardPanel.getTempletVO().getSavedtablename().equalsIgnoreCase("cmp_event")) {
			impIssue = new WLTButton("�����������ʶ��");
			impIssue.setFont(LookAndFeel.font_b);
			impIssue.addActionListener(this);
			cardPanel.addBatchBillCardButton(new WLTButton[] { impIssue });
			cardPanel.repaintBillCardButton();
			cardPanel.setValueAt("sourcetype", new StringItemVO("��鷢��"));
		} else if (cardPanel.getTempletVO().getSavedtablename().equalsIgnoreCase("cmp_ward")) {
			cardPanel.setValueAt("sourcetype", new StringItemVO("��鷢��"));
		}
		BillCardDialog cardDialog = new BillCardDialog(this, title, cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardPanel.setValueAt("cmp_check_id", new StringItemVO(billCard_check.getBillVO().getStringValue("id"))); // ���id
		cardPanel.setValueAt("cmp_check_name", new StringItemVO(billCard_check.getBillVO().getStringValue("checkname")));// ��Ӧ�ļ������
		cardPanel.setValueAt("cmp_check_item_id", new StringItemVO(checkItemVO.getStringValue("id")));// ��Ӧ�ļ�����id

		String cmpfileID = checkItemVO.getStringValue("cmp_cmpfile_id");
		String cmpfileName = checkItemVO.getStringValue("cmp_cmpfile_name");
		cardPanel.setValueAt("cmp_cmpfile_id", new RefItemVO(cmpfileID, cmpfileID, cmpfileName)); // ��Ӧ�ļ����������ļ�
		cardPanel.setValueAt("cmp_cmpfile_name", new StringItemVO(cmpfileName)); // ��Ӧ�ļ����������ļ�		
		if (!TBUtil.isEmpty(cmpfileID)) {
			cardPanel.setEditable("cmp_cmpfile_id", false);
		}
		//cardPanel.setEditable("refrisks", false);
		cardDialog.setVisible(true);

		// ˢ���ӱ��ж�Ӧ����
		if (cardDialog.getCloseType() == 1) {
			//��billList_check_item���ι�ȥ,�����ڡ������������롱�Ի���ĸ����ڣ��Ա���ĳЩ���⣬���GeneralInsertIntoRiskEval��[YangQing/2013-09-18]
			new GeneralInsertIntoRiskEval(billList_check_item, cardPanel, cardPanel.getTempletVO().getTablename(), "¼��");
			return true;
		} else {//������û�б���ֱ��ȡ�������ж����Ƿ�������� �漰�Ŀͻ� �� ��Ƶ�Ա�����������ɾ���ġ�
			BillVO vo = cardPanel.getBillVO();
			TBUtil tbutil = new TBUtil();
			List deleteSQL = new ArrayList();
			if (vo.getStringValue("wardcust") != null && !vo.getStringValue("wardcust").equals("")) {
				String inCondition = tbutil.getInCondition(vo.getStringValue("wardcust"));
				deleteSQL.add("delete from cmp_wardevent_cust where id in (" + inCondition + ")"); // ɾ���漰�Ŀͻ��ӱ�����
			}
			if (vo.getStringValue("warduser") != null && !vo.getStringValue("warduser").equals("")) {
				String inCondition = tbutil.getInCondition(vo.getStringValue("warduser"));
				deleteSQL.add("delete from cmp_wardevent_user where id in (" + inCondition + ")"); // ɾ���漰��Ա���ӱ�����
			}
			if (vo.getStringValue("eventcust") != null && !vo.getStringValue("eventcust").equals("")) {
				String inCondition = tbutil.getInCondition(vo.getStringValue("eventcust"));
				deleteSQL.add("delete from cmp_wardevent_cust where id in (" + inCondition + ")"); // ɾ���漰�Ŀͻ��ӱ�����
			}
			if (vo.getStringValue("eventuser") != null && !vo.getStringValue("eventuser").equals("")) {
				String inCondition = tbutil.getInCondition(vo.getStringValue("eventuser"));
				deleteSQL.add("delete from cmp_wardevent_user where id in (" + inCondition + ")"); // ɾ���漰��Ա���ӱ�����
			}
			if (deleteSQL.size() > 0) {
				try {
					UIUtil.executeBatchByDS(null, deleteSQL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return false;
		}
	}

	private void updateStatus(String status, String pk) {
		String sql = "update cmp_check set status = '" + status + "' where id = " + pk;
		try {
			UIUtil.executeUpdateByDS(null, sql);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
