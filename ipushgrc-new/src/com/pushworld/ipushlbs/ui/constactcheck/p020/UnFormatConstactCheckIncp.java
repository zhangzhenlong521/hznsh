package com.pushworld.ipushlbs.ui.constactcheck.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillOfficeDialog;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
import com.pushworld.ipushlbs.ui.printcomm.CommonHtmlOfficeConfig;

/**
 * �Ǹ�ʽ��ͬ���--������
 * 
 * @author wupeng �Ǹ�ʽ��ͬ����½�����˵����
 *         ��������õķǸ�ʽ��������ô��ʽ����е�DEAL_ID����ֵ�ģ����˵�������õģ���ΪID�ǹ̶����صģ����Ժ�ͬ�����ֵȾ������޸İ�
 *         ����������ã����ʽ����е�DEAL_ID����û��ֵ�ģ�����ֶ���Ϊ�����䣬�����Ϳ������������õĻ���ֱ�Ӵ������ˡ� modify
 *         YINLiang
 */
public class UnFormatConstactCheckIncp extends AbstractWorkPanel implements ActionListener, BillCardEditListener, BillListHtmlHrefListener, BillListSelectListener {

	BillListPanel docList = null;// ��ʽ�ı���ͬ��ť
	WLTButton insert = null;// �б�����ϵ�������ť
	WLTButton edit = null;// �б�����ϵı༭��ť
	WLTButton delete = null;
	WLTButton wf_watchBtn = null;
	WLTButton btn_insert_card, btn_confirm; // �����½���Ƭ�е�������ť
	BillListDialog billListDialog; // ������ú�ͬ����ʱ�����Ĵ���
	BillCardPanel insertCard = null;// �б��������Ŀ�Ƭ

	@Override
	public void initialize() {
		docList = new BillListPanel("UNFORMAT_DEAL_CHECK_CODE1");
		docList.addBillListHtmlHrefListener(this);
		docList.addBillListSelectListener(this);
		insert = new WLTButton("�½�");
		insert.addActionListener(this);
		edit = new WLTButton("�༭");
		edit.addActionListener(this);
		delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		delete.addActionListener(this);
		wf_watchBtn = WLTButton.createButtonByType(WLTButton.LIST_WORKFLOWSTART_MONITOR);
		wf_watchBtn.addActionListener(this);

		docList.addBatchBillListButton(new WLTButton[] { insert, edit, delete, WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD), wf_watchBtn });
		docList.repaintBillListButton();
		this.add(docList);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == insert)// �Զ�������
			doInsert();
		else if (obj == edit)// �Զ���༭
			doEdit();
		else if (obj == wf_watchBtn)// ���̷���/���
			wfStartOrMonitor();
		else if (obj == delete)
			onDelete();
		else if (obj == btn_insert_card)
			doQuoted(); // ���ú�ͬ����
		else if (obj == btn_confirm)
			doInsertContact();

	}

	// �Զ�������
	private void doInsert() {
		BillListPanel list = this.getbilListPanel();
		insertCard = new BillCardPanel(list.templetVO);
		btn_insert_card = new WLTButton("���÷Ǹ�ʽ��ͬ����");
		insertCard.addBillCardButton(btn_insert_card);
		insertCard.repaintBillCardButton(); // �� ���ú�ͬ ��ť����
		btn_insert_card.addActionListener(this);

		BillCardDialog dialog = new BillCardDialog(list, "����" + list.templetVO.getTempletname(), insertCard, WLTConstants.BILLDATAEDITSTATE_INSERT);
		dialog.getBillcardPanel().insertRow();// �趨ָ����id
		dialog.setVisible(true);// ��ʾ

		if (dialog.getCloseType() == 1) {// ��������������ʾ����
			int newRow = list.newRow();
			list.setBillVOAt(newRow, dialog.getBillVO());
			list.setSelectedRow(newRow);
			list.refreshCurrSelectedRow();
		} else { // û�б���������¼����ô���Ƶ�������������ļ��ĸ���Ҳû���ˣ�ɾ��
			this.deleteFile(dialog.getBillVO(), null, true);
		}
	}

	// ���ú�ͬ����
	private void doQuoted() {
		billListDialog = new BillListDialog(insertCard, "", "LBS_UNSTDFILE_CODE3"); // ���غ�ͬ������
		BillListPanel billListPanel = billListDialog.getBilllistPanel();
		billListPanel.addBillListHtmlHrefListener(this);
		billListDialog.setVisible(true);
		if (billListDialog.getCloseType() != 1) {
			return;
		}
		doInsertContact();
	}

	// ͨ��ѡ���ĺ�ͬ���� �Զ������ͬ��Ϣ
	@SuppressWarnings("unchecked")
	private void doInsertContact() {
		BillListPanel panel_insert = billListDialog.getBilllistPanel(); // ��ǰpanel
		BillVO billvo_insert = panel_insert.getSelectedBillVO(); // ѡ�����billvo
		HashVO hashvo;
		try {
			IPushGRCServiceIfc server = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			HashMap oldfileMap = new HashMap();
			hashvo = UIUtil.getHashVoArrayByDS(null, "Select lb.id,lb.code,lb.Name,lb.testfile,lb.Type,co.Name typename,lb.busiid,st.Name businame,lb.property,pu.name proname " + "From lbs_unstdfile lb,CONTRACTBUSI co,LBS_STDFILE_TYPE st,PUB_COMBOBOXDICT pu  "
					+ "Where lb.Type=co.Id And lb.busiid=st.Id and pu.Type ='��ͬ_��ͬ����2' and pu.id=lb.property " + "and lb.id = " + billvo_insert.getStringValue("id"))[0];

			String officeFileName = hashvo.getStringValue("testfile"); // ��ͬ�ļ�����
			String dealId = hashvo.getStringValue("id"); // ��ͬ����ID
			String dealCode = hashvo.getStringValue("code"); // ��ͬ��������
			String dealName = hashvo.getStringValue("name"); // ��ͬ��������
			if (officeFileName != null && !officeFileName.equals("")) {
				oldfileMap.put("office", officeFileName);
			}
			// ����
			HashMap newFiles = server.bargainCopyFile(oldfileMap);
			String newOfficeName = (String) newFiles.get("office");
			// ��ֵ
			insertCard.setValueAt("DEAL_ID", new StringItemVO(dealId));
			insertCard.setValueAt("DEALDOC_NAME", new StringItemVO(dealName));
			insertCard.setValueAt("DEAL_CODE", new StringItemVO("�Ǹ�ʽ��ͬ/" + UIUtil.getCurrDate() + "/" + dealCode + "/" + UIUtil.getSequenceNextValByDS(null, "S_UNFORMAT_DEAL_CHECK")));
			insertCard.setValueAt("TYPE", new RefItemVO(hashvo.getStringValue("type"), "", hashvo.getStringValue("typename")));
			insertCard.setValueAt("BUSIID", new RefItemVO(hashvo.getStringValue("busiid"), "", hashvo.getStringValue("businame")));
			insertCard.setValueAt("PROPERTY", new ComBoxItemVO(hashvo.getStringValue("property"), "", hashvo.getStringValue("proname")));
			if (newOfficeName != null && !newOfficeName.equals("")) {
				String chineseName = new TBUtil().convertHexStringToStr(newOfficeName.substring(newOfficeName.lastIndexOf("_") + 1, newOfficeName.lastIndexOf("."))) + newOfficeName.substring(newOfficeName.lastIndexOf("."));// ���Ϻ�׺��
				insertCard.setValueAt("DEAL_CONTENT", new RefItemVO(newOfficeName, "", chineseName));// ���á���ͬ���ġ�һ���ֵ,��һ���Ǹ�RefItemVO
			} else {
				MessageBox.show(insertCard, "�ú�ͬû�з���!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void onDelete() {
		BillListPanel billList = (BillListPanel) this.getbilListPanel(); //
		int li_selRow = billList.getSelectedRow();
		if (li_selRow < 0) {
			MessageBox.showSelectOne(billList); //
			return;
		}
		if (!MessageBox.confirmDel(billList)) {
			return; //
		}
		this.deleteFile(billList.getSelectedBillVO(), null, true);// ɾ���������ļ�
		billList.doDelete(true); // ��������ɾ������!!!
	}

	/**
	 * ���̷��𡢡������
	 */
	private void wfStartOrMonitor() {
		BillListPanel list = this.getbilListPanel();
		BillVO vo = list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(list);
			return;
		}

		if (!vo.containsKey("wfprinstanceid")) {
			MessageBox.show(list, "ѡ�еļ�¼��û�ж��幤�����ֶ�(wfprinstanceid)!"); //
			return; //
		}

		String str_wfprinstanceid = vo.getStringValue("wfprinstanceid"); //
		if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) {// �������δ�����������̣�����������
			try {
				String curYear = UIUtil.getCurrDate().substring(0, 4);
				new cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory().dealAction("deal", list, null); // ������!f��������
				// ����������ͬʱ����������״̬��Ϊ�����У�����Ϊ�䴴������
				// ע�����½���һ�ű������ŵ�ǰ������ֵ�����µ�һ�꿪ʼ����Ŵ�1��ʼ ����type 1Ϊ��ʽ��ͬ 2Ϊ�Ǹ�ʽ��ͬ
				String prinstanceid = UIUtil.getStringValueByDS(null, "select wfprinstanceid from  " + list.templetVO.getTablename() + " where id = " + vo.getStringValue("id"));
				String process_code_max = UIUtil.getStringValueByDS(null, "select maxcode from lbs_process_id where type='2' and year = '" + curYear + "'"); // ȡ�õ�ǰ�����ĺ�ͬ������
				if (prinstanceid == null) // ������̲�δ����
					return;
				if (prinstanceid.equals(""))
					return;
				else {
					List<String> sqlList = new ArrayList<String>();
					String process_code; // ������
					String sql_up_maxcode; // ���±�code
					// ��������״̬��������
					if ("".equals(process_code_max) || process_code_max == null) { // �����ǰû������
						process_code = curYear + "�Ǹ�ʽ���� - " + 1;
						sql_up_maxcode = "insert into lbs_process_id values ('2','" + curYear + "'," + 1 + ") ";
					} else {
						process_code = curYear + "�Ǹ�ʽ���� - " + (Integer.parseInt(process_code_max) + 1);
						sql_up_maxcode = "update lbs_process_id set maxcode = " + (Integer.parseInt(process_code_max) + 1) + " where type = '2' and year = '" + curYear + "' ";

					}
					process_code = curYear + "�Ǹ�ʽ���� - " + UIUtil.getSequenceNextValByDS(null, "s_process_code");
					String sql_up = "update " + vo.getSaveTableName() + "  set endtype = '������',process_code = '" + process_code + "' where id = " + vo.getStringValue("id");
					sqlList.add(sql_up_maxcode);
					sqlList.add(sql_up);
					UIUtil.executeBatchByDS(null, sqlList); // ִ���ϱ�����sql

					list.refreshData();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {// ���̼��
			cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog wfMonitorDialog = new cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog(list, str_wfprinstanceid, vo); //
			wfMonitorDialog.setMaxWindowMenuBar();
			wfMonitorDialog.setVisible(true);
		}

	}

	// �Զ���༭
	private void doEdit() {
		BillListPanel list = this.getbilListPanel();
		if (list.getSelectedBillVO() == null) {
			MessageBox.showSelectOne(list);
			return;
		}
		String oldFile = list.getSelectedBillVO().getStringValue("DEAL_CONTENT");// �õ��ϵ�word�ļ�
		BillCardPanel editCard = new BillCardPanel(list.templetVO);
		editCard.addBillCardEditListener(this);
		editCard.setBillVO(list.getSelectedBillVO());

		BillCardDialog dialog = new BillCardDialog(list, "�༭" + list.getTempletVO().getTempletname(), editCard, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true);

		if (dialog.getCloseType() == 1) {// �������˱��水ť
			list.setBillVOAt(list.getSelectedRow(), dialog.getBillVO());
			list.refreshCurrSelectedRow();
		} else {
			return;
		}

		String newFile = dialog.getBillcardPanel().getBillVO().getStringValue("DEAL_CONTENT");// �õ��µ��ļ�
		if (oldFile != null) {// ���ļ���Ϊ��
			if (newFile == null || oldFile.equals(newFile)) {// ������ļ��ǿջ������ļ������ļ���ͬ����ɾ�����ļ�
				this.deleteFile(null, oldFile, true);
			}
		}

	}

	// ɾ���ļ�.����ͨ��billvoɾ����Ҳ����ֱ��ͨ���ļ���ɾ��
	private void deleteFile(BillVO vo, String path, boolean isOfficeFile) {
		try {
			FrameWorkMetaDataServiceIfc serives = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class);

			if (vo != null) {// ͨ��billvoɾ���ļ�
				String officefile = vo.getStringValue("DEAL_CONTENT");
				String tachfile = vo.getStringValue("FILES");
				if (officefile != null && !officefile.equals("")) {
					serives.deleteOfficeFileName(officefile);
				}
				if (tachfile != null && !tachfile.equals("")) {
					String files[] = tachfile.split(";");
					for (int i = 0; i < files.length; i++) {
						if (files[i] != null && !files[i].equals("")) {
							serives.deleteZipFileName(files[i]); // ɾ������
						}
					}
				}
			}

			if (path != null) {// ͨ��·��ɾ���ļ�
				if (isOfficeFile)
					serives.deleteOfficeFileName(path);
				else
					serives.deleteOfficeFileName(path);
			}

		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// ��Ƭ�༭������
	@SuppressWarnings("unchecked")
	public void onBillCardValueChanged(BillCardEditEvent event) {
		String itemkey = event.getItemKey();
		BillCardPanel card = (BillCardPanel) event.getSource();

		if (itemkey != null) {
			if (itemkey.equalsIgnoreCase("DEALDOC_NAME")) {// ��������仯���� ��ͬ�ı�����
				// ����
				RefItemVO refvo = (RefItemVO) event.getNewObject();
				if (refvo == null) {
					card.reset("DEAL_CONTENT");
					return;
				}
				String id = refvo.getId();
				String dealCode = null;// ��ͬ����
				try {
					IPushGRCServiceIfc server = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
					HashMap oldfileMap = new HashMap();
					String officeFileName = null;// office�ļ�

					// �����ݿ��в鴦office�ļ������ֺͺ�ͬ����
					if (getbilListPanel().getTempletVO().getTempletcode().equalsIgnoreCase("FORMAT_DEAL_CHECK_CODE1")) {
						HashVO hashvo = UIUtil.getHashVoArrayByDS(null, "select testfile ,code from v_lbs_stand_check_file where id = " + id)[0];
						officeFileName = hashvo.getStringValue("testfile"); // ��ͬ�ļ�����
						dealCode = hashvo.getStringValue("code"); // ��ͬ��������

					} else if (getbilListPanel().getTempletVO().getTempletcode().equalsIgnoreCase("UNFORMAT_DEAL_CHECK_CODE1")) {
						officeFileName = UIUtil.getStringValueByDS(getbilListPanel().getDataSourceName(), "select testfile from LBS_UNSTDFILE where id =" + id);// �õ��ļ�������
						dealCode = UIUtil.getStringValueByDS(getbilListPanel().getDataSourceName(), "select code from V_LBS_STAND_CHECK_FILE where id =" + id);// �õ���ͬ����
					}

					if (officeFileName != null && !officeFileName.equals("")) {
						oldfileMap.put("office", officeFileName);
					}

					// ����
					HashMap newFiles = server.bargainCopyFile(oldfileMap);
					String newOfficeName = (String) newFiles.get("office");
					// ��ֵ
					card.setValueAt("deal_code", new StringItemVO(dealCode));
					if (newOfficeName != null && !newOfficeName.equals("")) {

						String chineseName = new TBUtil().convertHexStringToStr(newOfficeName.substring(newOfficeName.lastIndexOf("_") + 1, newOfficeName.lastIndexOf("."))) + newOfficeName.substring(newOfficeName.lastIndexOf("."));// ���Ϻ�׺��

						RefItemVO ref = new RefItemVO();// �½�һ��refitemvo
						ref.setId(newOfficeName);// �ļ���
						ref.setCode(ref.getCode());// code
						ref.setName(chineseName);// ��ʾ����
						card.setValueAt("DEAL_CONTENT", ref);// ���á���ͬ���ġ�һ���ֵ,��һ���Ǹ�RefItemVO
					} else {
						MessageBox.show(card, "�ú�ͬû�з���!");
					}
				} catch (WLTRemoteException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	// ���������
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent event) {
		BillListPanel source = event.getBillListPanel();
		BillVO vo = source.getSelectedBillVO();

		try {
			BillOfficeDialog officeDialog = new BillOfficeDialog(source);
			if (event.getItemkey().equalsIgnoreCase("DEAL_CONTENT")) {
				String en = vo.getStringValue("DEAL_CONTENT");
				String cn = new TBUtil().convertHexStringToStr(en);
				cn = cn.substring(3, cn.length() - 1) + en.substring(en.lastIndexOf("."));
				officeDialog.setTitle(cn);
				CommonHtmlOfficeConfig.OfficeConfig(vo.getStringValue("DEAL_CONTENT"), vo, officeDialog);
				officeDialog.setVisible(true);
			} else if (event.getItemkey().equalsIgnoreCase("TESTFILE")) {
				String en = vo.getStringValue("TESTFILE");
				String cn = new TBUtil().convertHexStringToStr(en);
				cn = cn.substring(3, cn.length() - 1) + en.substring(en.lastIndexOf("."));
				officeDialog.setTitle(cn);
				CommonHtmlOfficeConfig.OfficeConfig(vo.getStringValue("TESTFILE"), vo, officeDialog);
				officeDialog.setVisible(true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private BillListPanel getbilListPanel() {
		return docList;
	}

	public void onBillListSelectChanged(BillListSelectionEvent event) {
		BillVO vo = event.getCurrSelectedVO();
		if (vo != null) {
			if (!vo.containsKey("wfprinstanceid")) {// �ֶ��в������������ֶ�
				return; //
			}
			String wf_id = vo.getStringValue("wfprinstanceid");
			if (wf_id == null || wf_id.trim().isEmpty()) {// û�ж�Ӧ�Ĺ�����,�޸ĺ�ɾ����ť����
				edit.setEnabled(true);
				delete.setEnabled(true);
			} else {
				edit.setEnabled(false);
				delete.setEnabled(false);
			}
		}
	}

}
