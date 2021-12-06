package com.pushworld.ipushgrc.ui.rule.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListButtonActinoListener;
import cn.com.infostrategy.ui.mdata.BillListButtonClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.law.LawOrRuleImportDialog;

/**
 * �ڲ��ƶ�ά��!!
 * 
 * @author xch
 * 
 */
public class RuleRecordWKPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener, BillListButtonActinoListener {

	private BillListPanel billList_rule;
	private WLTButton btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
	private WLTButton btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
	private WLTButton btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
	private WLTButton btn_showword; // word����
	private WLTButton btn_exportallword; // word��������
	private WLTButton btn_editRuleitem = null, btn_import; //
	private TBUtil tbutil = new TBUtil();
	private boolean ifHaveItem = tbutil.getSysOptionBooleanValue("�ƶ��Ƿ����Ŀ", true);	

	public void initialize() {
		currInit("RULE_RULE_CODE1");
	}

	/*
	 * �����ڿز�Ʒ�ͺϹ��Ʒ�õı�ṹ��ͬ������ģ���в��ֲ��ղ�ͬ�������Ժ�д������ٸ�һ�㡣���԰�����ģ�嶼��������
	 */
	public void currInit(String _mainTemplet) {
		billList_rule = new BillListPanel(_mainTemplet);
		if (billList_rule.getQuickQueryPanel().getCompentByKey("primarykey") != null) {
			billList_rule.getQuickQueryPanel().setVisiable("primarykey", false); // ���عؼ��ֲ�ѯ���ı���!
		}
		btn_delete.addActionListener(this);
		btn_showword = new WLTButton("Word����", "zt_009.gif");
		btn_showword.addActionListener(this);
		btn_exportallword = new WLTButton("Word��������", "report_go.png");
		btn_exportallword.addActionListener(this);
		btn_update.addActionListener(this);
		
		/*
		//��ɽ����������ǹ���Աadmin���������ƶȣ����Ż����ɲ˵������Ƿ���ʾ��word�������͡�word���������� �����/2015-06-19��
		String showExport = this.getMenuConfMapValueAsStr("word����", "Y;N");//�������������ֵ���ֱ�����Ƿ���ʾ��word���������Ƿ���ʾ��word�����������������һ��ֵ����ȫ����ʾ����ʾ��
		boolean expOne = true;//Ϊ���ݿ��ǣ�Ĭ����ʾ��word������
		boolean expAll = false;//Ϊ���ݿ��ǣ�Ĭ�ϲ���ʾ��word����������
		if (showExport != null && showExport.length() > 0) {
			if (showExport.contains(";")) {
				String[] shows = TBUtil.getTBUtil().split(showExport, ";");
				if (!"Y".equalsIgnoreCase(shows[0])) {
					expOne = false;
				}
				if (shows.length > 1 && "Y".equalsIgnoreCase(shows[1])) {
					expAll = true;
				}
			} else if ("Y".equalsIgnoreCase(showExport.trim()) || "Y;".equalsIgnoreCase(showExport.trim())) {
				expAll = true;
			} else {
				expOne = false;
			}
		}
		
		if (ifHaveItem) {
			btn_editRuleitem = new WLTButton("�༭��Ŀ"); //
			btn_editRuleitem.addActionListener(this); //
			btn_import = new WLTButton("����");
			btn_import.addActionListener(this);
			if ((expOne && expAll) || usercode.equalsIgnoreCase("admin")) {//�������ʾ�������ǹ���Աadmin
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_editRuleitem, btn_import, btn_showword, btn_exportallword });
			} else if (expOne) {//��ʾ��word������������ʾ��word����������
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_editRuleitem, btn_import, btn_showword });
			} else if (expAll) {//����ʾ��word����������ʾ��word����������
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_editRuleitem, btn_import, btn_exportallword });
			} else {//����ʾ��word������������ʾ��word����������
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_editRuleitem, btn_import });
			}

		} else {
			if ((expOne && expAll) || usercode.equalsIgnoreCase("admin")) {//�������ʾ�������ǹ���Աadmin
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_showword, btn_exportallword });
			} else if (expOne) {//��ʾ��word������������ʾ��word����������
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_showword });
			} else if (expAll) {//����ʾ��word����������ʾ��word����������
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_exportallword });
			} else {//����ʾ��word������������ʾ��word����������
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete });
			}
		}
		*/
		
		/*̫�����ˣ� �Ż�һ��  Gwang 2016-01-27
		 * ��Word����������ֻ��admin����
		 * ��Word��������ϵͳ�������ã��������ƣ����ڲ��ƶ�Word������ť���Ž�ɫ��
		 */		
		boolean expOne = false;//��word��������ť�Ƿ���ʾ
		String expAllowRole = TBUtil.getTBUtil().getSysOptionStringValue("�ڲ��ƶ�Word������ť���Ž�ɫ", "none");		
		if (expAllowRole.equals("none")) {
			//������...
		}else {
			//��ĳЩ��ɫ����
			String[] userRole = ClientEnvironment.getInstance().getLoginUserRoleCodes();
			String[] allowRoles = expAllowRole.split(";");
			for (String allowRole : allowRoles) {
				for (String role : userRole) {
					if (allowRole.equals(role)) {
						expOne = true;
						break;
					}
				}
				if (expOne) break;
			}
	
		}
		String usercode = ClientEnvironment.getInstance().getLoginUserCode();
			
		if (ifHaveItem) {
			btn_editRuleitem = new WLTButton("�༭��Ŀ"); //
			btn_editRuleitem.addActionListener(this); //
			btn_import = new WLTButton("����");
			btn_import.addActionListener(this);			
			
			if (usercode.equalsIgnoreCase("admin")) {//�����admin������ʾ
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_editRuleitem, btn_import, btn_showword, btn_exportallword });
			} else if (expOne) {//ֻ��ʾ��word������
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_editRuleitem, btn_import, btn_showword });
			} else {//������ʾ
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_editRuleitem, btn_import });
			}

		} else {
			if (usercode.equalsIgnoreCase("admin")) {//�����admin������ʾ
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_showword, btn_exportallword });
			} else if (expOne) {//ֻ��ʾ��word������
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_showword });
			} else {//������ʾ
				billList_rule.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete });
			}
		}
		billList_rule.addBillListHtmlHrefListener(this);
		billList_rule.addBillListButtonActinoListener(this);
		billList_rule.repaintBillListButton();
		this.add(billList_rule);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_editRuleitem) {
			onEditRuleItem();
		} else if (obj == btn_delete) {
			onDeleteRuleItem();
		} else if (obj == btn_import) {
			onImport();
		} else if (obj == btn_showword) {
			onWordshow();
		} else if (obj == btn_exportallword) {
			onExportAllshow();//word��������
		} else if (obj == btn_update) {
			onEditRule();
		}
	}

	/*
	 * �޸İ�ť�߼������/2015-12-29��
	 */
	private void onEditRule() {
		BillVO billVO = billList_rule.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		BillCardPanel cardPanel = new BillCardPanel(billList_rule.getTempletVO());
		cardPanel.setBillVO(billVO); //
		String refrule2 = billVO.getStringValue("refrule2", "");
		String formids = billVO.getStringValue("formids", "");

		BillCardDialog dialog = new BillCardDialog(this, billList_rule.getTempletVO().getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) {
			if (billList_rule.getSelectedRow() != -1) {
				billList_rule.setBillVOAt(billList_rule.getSelectedRow(), dialog.getBillVO());
				billList_rule.setRowStatusAs(billList_rule.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			}
			BillVO billvo = dialog.getBillVO();
			if (billvo == null) {
				return;
			}
			String new_refrule2 = billvo.getStringValue("refrule2", "");
			String new_formids = billvo.getStringValue("formids", "");
			if (!refrule2.equals(new_refrule2) || !formids.equals(new_formids)) {
				insertAlarm(billvo, "update");
			}
		}

	}

	private void onImport() {
		LawOrRuleImportDialog importDialog = new LawOrRuleImportDialog(billList_rule, "�ڲ��ƶȵ���", 800, 700, "rule");
		importDialog.setVisible(true);
		if (importDialog.getCloseType() == 1) {
			billList_rule.refreshData();
		}
	}

	private void onDeleteRuleItem() {
		try {
			BillVO[] selectRule = billList_rule.getSelectedBillVOs();
			if (selectRule.length == 0) {
				MessageBox.showSelectOne(this);
				return;
			} else {
				if (MessageBox.showConfirmDialog(this, "��ȷ��ɾ����[" + selectRule.length + "]����¼��?") != JOptionPane.YES_OPTION) {
					return;
				}
			}
			List list = new ArrayList();
			for (int i = 0; i < selectRule.length; i++) {
				list.add(selectRule[i].getStringValue("id"));
			}
			String insql = tbutil.getInCondition(list);
			List sqllist = new ArrayList();
			sqllist.add("delete from rule_rule where id in ( " + insql + " )"); // ɾ�������������
			sqllist.add("delete from rule_rule_item where ruleid in ( " + insql + ")");// ����ɾ�����ļ�¼
			sqllist.add("delete from rule_eval_b where rule_eval_id in ( select id from rule_eval where ruleid in (" + insql + " ))"); //ɾ���ƶ������ӱ���Ϣ
			sqllist.add("delete from rule_eval where ruleid in(" + insql + ")"); //ɾ���ƶ�����
			UIUtil.executeBatchByDS(null, sqllist);
			//			insertAlarm(selectRule,"delete");
			billList_rule.removeSelectedRows();
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �жϲ������ڹ�Ԥ��!
	 * Ŀǰ�ڹ�Ԥ�����ıȽϼ�ª��������Ҫ���� ��ĳ�����������Щ�޸ģ����磺�޸���2����Ŀ���ݣ�ɾ����1����Ŀ��������3����
	 * ���������cvs�Ա�����Ч����
	 */
	public void insertAlarm(BillVO _selectRule, String _type) {
		try {

			TBUtil tbutil = TBUtil.getTBUtil();
			ArrayList insertSQLlist = new ArrayList();
			String currdate = UIUtil.getServerCurrDate();
			String userid = ClientEnvironment.getInstance().getLoginUserID();
			String userdept = ClientEnvironment.getInstance().getLoginUserDeptId();
			String refrule2 = _selectRule.getStringValue("refrule2", "");
			InsertSQLBuilder sql = new InsertSQLBuilder("rule_alarm");
			sql.putFieldValue("alarmsource", "�ڲ��ƶ�--�ƶ�ά��");
			sql.putFieldValue("alarmsourcetab", "rule_rule");
			sql.putFieldValue("state", "δ����");
			sql.putFieldValue("alarmdate", currdate);
			sql.putFieldValue("creator", userid);
			sql.putFieldValue("createdept", userdept);
			sql.putFieldValue("alarmsourcepk", _selectRule.getStringValue("id"));
			if (refrule2 != null && !"".equals(refrule2)) {
				String[] refruleids = TBUtil.getTBUtil().split(refrule2, ";");
				HashMap ruleMap = UIUtil.getHashMapBySQLByDS(null, "select id,rulename from rule_rule where id in(" + TBUtil.getTBUtil().getInCondition(refrule2) + ")");

				for (int m = 0; m < refruleids.length; m++) {
					//���ƶȱ���Щ�ƶȹ����ˣ��������ѡ����/2015-12-29��
					HashVO[] vos = UIUtil.getHashVoArrayByDS(null, "select * from rule_rule where refrule like '%;" + refruleids[m] + ";%'");
					if (vos != null && vos.length > 0) {
						for (int i = 0; i < vos.length; i++) {
							sql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_RULE_ALARM"));
							sql.putFieldValue("ruleid", vos[i].getStringValue("id"));
							sql.putFieldValue("rulecode", vos[i].getStringValue("rulecode"));
							sql.putFieldValue("rulename", vos[i].getStringValue("rulename"));
							sql.putFieldValue("alarmtargettab", "rule_rule");
							String rulename = refruleids[m];
							if (ruleMap.get(refruleids[m]) != null) {
								rulename = (String) ruleMap.get(refruleids[m]);
							}
							sql.putFieldValue("alarmreason", "��" + rulename + "���ƶ��ѷ�ֹ�����������ƶ�!");
							insertSQLlist.add(sql.getSQL());
						}
					}
				}
				insertSQLlist.add("update rule_rule set state='ʧЧ' where id in(" + tbutil.getInCondition(refrule2) + ")");

			}
			//�����ı������/2015-12-26��
			String formids = _selectRule.getStringValue("formids");
			if (formids != null && !"".equals(formids.trim())) {
				String tabname = tbutil.getSysOptionStringValue("�ƶȹ����ı�����", "BSD_FORM");//���������code��name�ֶ�
				tabname = tabname.toLowerCase();
				HashVO[] vos = UIUtil.getHashVoArrayByDS(null, "select id,code,name from " + tabname + " where id in(" + tbutil.getInCondition(formids) + ")");
				if (vos != null && vos.length > 0) {
					sql.putFieldValue("alarmreason", "��" + _selectRule.getStringValue("rulename") + "���ƶ��������޸ģ��������ر�!");
					for (int i = 0; i < vos.length; i++) {
						sql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_RULE_ALARM"));
						sql.putFieldValue("ruleid", vos[i].getStringValue("id"));
						sql.putFieldValue("rulecode", vos[i].getStringValue("code"));
						sql.putFieldValue("rulename", vos[i].getStringValue("name"));
						sql.putFieldValue("alarmtargettab", tabname);
						insertSQLlist.add(sql.getSQL());
					}
				}
			}
			if (insertSQLlist.size() > 0) {
				UIUtil.executeBatchByDS(null, insertSQLlist);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void onEditRuleItem() {
		BillVO selectRule = billList_rule.getSelectedBillVO();
		if (selectRule == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		RuleItemEditDialog dialog = new RuleItemEditDialog(this, "�༭��Ŀ(�޸ĺ��������棡)", 900, 600, selectRule); //
		dialog.setVisible(true); //
		if (dialog.isHaveChanged()) {
			insertAlarm(selectRule, "update");
		}
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		if (_event.getItemkey().equals("rulename"))
			showRuleHtml();
	}

	private void showRuleHtml() {
		new RuleShowHtmlDialog(billList_rule); //ͳһ����һ���� �ƶ���ϸ��
	}

	/**
	 * wordԤ��
	 * */
	private void onWordshow() {
		BillVO billVO = billList_rule.getSelectedBillVO();
		if (billVO == null || "".equals(billVO)) {
			MessageBox.showSelectOne(this);
			return;
		}
		new RuleWordBuilder(this, billVO.getStringValue("rulename"), billVO.getStringValue("id"));//�����/2015-01-27��
	}

	/**
	 * word��������
	 * */
	private void onExportAllshow() {
		BillVO[] billVO = billList_rule.getSelectedBillVOs();
		if (billVO == null || billVO.length < 1) {
			billVO = billList_rule.getAllBillVOs();
			if (billVO == null || billVO.length < 1) {
				MessageBox.show(this, "û���ƶȿɵ���");
				return;
			}
		}
		new RuleWordBuilder(this, billVO);//�����/2015-01-27��
	}

	public void onBillListAddButtonClicked(BillListButtonClickedEvent event) throws Exception {
		BillCardPanel card = event.getCardPanel();
		Object formids = card.getValueAt("formids");//��ر�
		if (formids == null) {
			return;
		}
		if (formids instanceof RefItemVO) {
			RefItemVO formVO = (RefItemVO) formids;

			String formNames = formVO.getName();
		}
	}

	public void onBillListAddButtonClicking(BillListButtonClickedEvent event) throws Exception {

	}

	public void onBillListButtonClicked(BillListButtonClickedEvent event) throws Exception {

	}

	public void onBillListDeleteButtonClicked(BillListButtonClickedEvent event) {

	}

	public void onBillListDeleteButtonClicking(BillListButtonClickedEvent event) throws Exception {

	}

	public void onBillListEditButtonClicked(BillListButtonClickedEvent event) {

	}

	public void onBillListEditButtonClicking(BillListButtonClickedEvent event) throws Exception {

	}

	public void onBillListLookAtButtonClicked(BillListButtonClickedEvent event) {

	}

	public void onBillListLookAtButtonClicking(BillListButtonClickedEvent event) {

	}
}
