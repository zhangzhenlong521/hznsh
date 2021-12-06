package com.pushworld.ipushlbs.ui.constactmanage.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillOfficeDialog;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
import com.pushworld.ipushlbs.ui.printcomm.CommonHtmlOfficeConfig;

public class UnFormatConstactWKPanel extends AbstractWorkPanel implements BillListHtmlHrefListener, ActionListener {
	private BillListPanel listpanel;
	private WLTButton btn_delete, btn_change; // ɾ����תΪ��ʽ
	private FrameWorkMetaDataServiceIfc serives = null;

	@Override
	public void initialize() {
		listpanel = new BillListPanel("LBS_UNSTDFILE_CODE1");
		listpanel.addBillListHtmlHrefListener(this);
		// ���ɾ����ť�¼�
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		btn_delete.addActionListener(this);
		// ���תΪ��ʽ��ͬ
		btn_change = new WLTButton("תΪ��ʽ��ͬ");
		btn_change.addActionListener(this);

		listpanel.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_POPINSERT), WLTButton.createButtonByType(WLTButton.LIST_POPEDIT), btn_delete, btn_change });
		listpanel.repaintBillListButton();
		this.add(listpanel);
	}

	private FrameWorkMetaDataServiceIfc getserives() {
		if (serives == null) {
			try {
				serives = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return serives;
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent event) {
		String str_filetest = event.getBillListPanel().getSelectedBillVO().getStringValue("testfile");
		if (str_filetest == null) {
			MessageBox.show(listpanel, "�ú�ͬδ��дword�ı�!");
		} else {
			BillListPanel panelh = event.getBillListPanel();
			BillVO billvo = panelh.getSelectedBillVO();
			// ����office����
			BillOfficeDialog dialog = new BillOfficeDialog(event.getBillListPanel());
			dialog.setTitle(new TBUtil().convertHexStringToStr(str_filetest.substring(str_filetest.lastIndexOf("_") + 1, str_filetest.lastIndexOf("."))));
			// ���õ����Ĵ�����ʾ
			CommonHtmlOfficeConfig.OfficeConfig(str_filetest, billvo, dialog);
			dialog.setVisible(true);
		}
	}

	// ɾ������
	private void onDelete() {
		BillVO billvo = listpanel.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (MessageBox.confirm(listpanel, "��ȷ��ɾ���ü�¼��?")) { // ȷ��Ҫɾ��
			this.deleteFile(listpanel.getSelectedBillVO(), null, true);// ɾ���������ļ�
			listpanel.doDelete(true); // ɾ����ǰ������
		}
	}

	// ɾ���ļ�.����ͨ��billvoɾ����Ҳ����ֱ��ͨ���ļ���ɾ��
	private void deleteFile(BillVO vo, String path, boolean isOfficeFile) {
		try {
			serives = getserives();
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_delete) // ɾ��ʱͬʱɾ������ļ�
			onDelete();
		else if (e.getSource() == btn_change) // �ϳ�
			onChange();
	}

	/**
	 * תΪ��ʽ��ͬ
	 */
	private void onChange() {
		BillVO billvo = listpanel.getSelectedBillVO();
		StringBuilder sql = new StringBuilder();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		try {
			if (MessageBox.showConfirmDialog(this, "ȷ��Ҫ���˷Ǹ�ʽ��ͬתΪ��ʽ��ͬ��?") == 0) {
				sql.append("select Code from LBS_STDFILE where Code = '" + billvo.getStringValue("Code").toString() + "'");
				HashVO[] hashVO = UIUtil.getHashVoArrayByDS(null, sql.toString());
				if (hashVO == null || hashVO.length == 0) {
					UIUtil.executeUpdateByDS(null, getInsertSql(billvo));
					MessageBox.show(this, "ת��ʽ��ͬ�ɹ�,��ת��ʽ��ͬΪδ����״̬,����ʹ���뷢��!");
				} else {
					MessageBox.show(this, "�Ѿ����ڣ��벻Ҫ�ظ�ת��");

				}

			} else {
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getInsertSql(BillVO billvo) {
		String seq_number1;
		// ʹ��InsertSQLBuilder��������
		InsertSQLBuilder sqlBuilder = new InsertSQLBuilder();
		try {

			HashMap<String, String> oldfileMap = new HashMap<String, String>();
			String officeFileName = billvo.getStringValue("testfile"); // office�ļ�
			if (officeFileName != null && !officeFileName.equals("")) {
				oldfileMap.put("office", officeFileName);
			}
			String annex = billvo.getStringValue("adjunct"); // �ı�����
			if (annex == null || annex.equals("null") || annex.equals("")) // ���Ϊ�գ��򲻽��и��Ʋ�����
				annex = "";
			// �����Ϊ�գ����Ƹ���
			else {
				oldfileMap.put("adjunt", annex);
			}
			// ����
			IPushGRCServiceIfc server = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			HashMap newFiles = server.bargainCopyFile(oldfileMap);
			String filename = (String) newFiles.get("office"); // office�ļ��ĸ��ư汾
			annex = (String) newFiles.get("adjunt"); // �����ĸ��ư汾

			seq_number1 = UIUtil.getSequenceNextValByDS(null, "s_lbs_stdfile"); // ������
			sqlBuilder.setTableName("lbs_stdfile"); // ����
			sqlBuilder.putFieldValue("id", seq_number1); // id
			sqlBuilder.putFieldValue("Code", billvo.getStringValue("Code"));// ����
			sqlBuilder.putFieldValue("Name", billvo.getStringValue("Name"));// ����
			sqlBuilder.putFieldValue("Type", billvo.getStringValue("Type"));// ҵ������
			sqlBuilder.putFieldValue("busiid", billvo.getStringValue("busiid"));// ��ͬ����
			sqlBuilder.putFieldValue("useway", billvo.getStringValue("useway"));// ��ͬ����
			sqlBuilder.putFieldValue("Reldate", UIUtil.getCurrDate());// ��������
			sqlBuilder.putFieldValue("Createorg", billvo.getStringValue("Createorg"));// ��������
			sqlBuilder.putFieldValue("adjunct", annex);// ����
			sqlBuilder.putFieldValue("editon", "");// �汾
			sqlBuilder.putFieldValue("isforeign", billvo.getStringValue("isforeign"));// �Ƿ�����
			sqlBuilder.putFieldValue("filestate", "δ����");// �Ƿ񷢲�
			sqlBuilder.putFieldValue("creater", billvo.getStringValue("creater"));// ������Ա
			sqlBuilder.putFieldValue("phone", billvo.getStringValue("phone"));// ��ϵ�绰
			sqlBuilder.putFieldValue("faxes", billvo.getStringValue("faxes"));// ����
			sqlBuilder.putFieldValue("testfile", filename);// ��ͬ�ı�
			sqlBuilder.putFieldValue("filedis", billvo.getStringValue("filedis"));// ��ͬ����
			sqlBuilder.putFieldValue("waigui", billvo.getStringValue("waigui"));// ���
			sqlBuilder.putFieldValue("neigui", billvo.getStringValue("neigui"));// �ڹ�
			sqlBuilder.putFieldValue("property", billvo.getStringValue("property"));// ��ͬ����
			sqlBuilder.putFieldValue("effect", "");// �Ƿ���Ч
			sqlBuilder.putFieldValue("secret", billvo.getStringValue("secret"));// ���̶ܳ�
			sqlBuilder.putFieldValue("fortruth", billvo.getStringValue("fortruth"));// �Ƿ����
			return sqlBuilder.getSQL();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
