package com.pushworld.ipushgrc.ui.wfrisk.p010;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;

/**
 * �����嵥ά�������/2013-09-22��
 * ��ҵ�ڿ�Bom����ʾ��
 * @author lcj
 * 
 */

public class WFAndRiskBillEditWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private IPushGRCServiceIfc service;//��Ʒ����ӿ�
	private BillListPanel billList_cmpfile; // �����ļ��б�!
	private WLTButton btn_add;// ��ť���½��ļ���
	private WLTButton btn_edit;// ��ť���༭�ļ���
	private WLTButton btn_delete;// ��ť��ɾ���ļ���
	private boolean editable = true;//�Ƿ�ɱ༭
	private boolean stateeditable;//�ļ�״̬�Ƿ�ɱ༭
	private TBUtil tbutil;
	private String templetcode = "CMP_CMPFILE_CODE2_IC";

	public BillListPanel getBillList_cmpfile() {
		return billList_cmpfile;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getTempletcode() {
		return templetcode;
	}

	public void setTempletcode(String templetcode) {
		this.templetcode = templetcode;
	}

	public boolean isStateeditable() {
		return stateeditable;
	}

	/**
	 * ��������ʼ������
	 */
	public void initialize() {

		this.setLayout(new BorderLayout()); //
		try {
			service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
		tbutil = new TBUtil();
		stateeditable = tbutil.getSysOptionBooleanValue("�����ļ�ά��ʱ�ļ�״̬�Ƿ�ɱ༭", true);//Ĭ���������ͱ༭�ļ�ʱ�ļ�״̬�ǿɱ༭��
		if (this.editable) {//����ɱ༭������Ӱ�ť
			billList_cmpfile = new BillListPanel(templetcode); //
			btn_add = new WLTButton("�½�");
			btn_edit = new WLTButton("�༭�ļ�");
			btn_delete = new WLTButton("ɾ��");

			btn_add.addActionListener(this);
			btn_edit.addActionListener(this);
			btn_delete.addActionListener(this);
			billList_cmpfile.addBatchBillListButton(new WLTButton[] { btn_add, btn_delete, btn_edit });// �����ļ���Ӱ�ť
		} else {
			billList_cmpfile = new BillListPanel(templetcode); //
			billList_cmpfile.setDataFilterCustCondition("versionno is not null");
		}
		billList_cmpfile.setItemsVisible(new String[] { "publishdate", "versionno" }, false);
		billList_cmpfile.setTitleLabelText("�����嵥ά��");
		billList_cmpfile.repaintBillListButton();// �������»��ư�ť
		this.add(billList_cmpfile); //	
	}

	/**
	 * �б�ť�ĵ���¼�
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_add) {
			onAddFile();
		} else if (e.getSource() == btn_edit) {
			onEditFile();
		} else if (e.getSource() == btn_delete) {
			onDeleteFile();
		}
	}

	/**
	 * ��ť���½������߼�,�������б��棬��ƬҲ�б��棬�ͻ�������������Ϊ�����ﱣ���ˣ��������ļ���¼Ҳ�ͱ����ˣ���ֱ�ӹرմ����ˣ��ʽ��������أ��ڱ༭�ļ�ʱ����ʾ
	 */
	private void onAddFile() {
		BillCardPanel cardPanel = new BillCardPanel(billList_cmpfile.templetVO); // ����һ����Ƭ���
		cardPanel.insertRow(); // ��Ƭ����һ��!
		cardPanel.setEditableByInsertInit(); // ���ÿ�Ƭ�༭״̬Ϊ����ʱ������
		BillCardDialog dialog = new BillCardDialog(billList_cmpfile, billList_cmpfile.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); // ������Ƭ������
		cardPanel.setEditable("filestate", stateeditable);//�����ļ�״̬�Ƿ�ɱ༭,��������Ͼ����
		dialog.setVisible(true); // ��ʾ��Ƭ����
		if (dialog.getCloseType() == 1) { // �����ǵ��ȷ������!����Ƭ�е����ݸ����б�!
			int li_newrow = billList_cmpfile.newRow(false); //
			billList_cmpfile.setBillVOAt(li_newrow, dialog.getBillVO(), false);
			billList_cmpfile.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); // �����б���е�����Ϊ��ʼ��״̬.
			billList_cmpfile.setSelectedRow(li_newrow); //
			billList_cmpfile.refreshCurrSelectedRow();//��Ҫˢ��һ�£������ļ�״̬���Ϊ��ɫ�����/2012-03-19��
		}
	}

	/**
	 * ��ť���༭�ļ������߼�
	 */
	private void onEditFile() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_cmpfile.refreshCurrSelectedRow();//ˢ��һ�£���ֹ�����������޸����ļ�״̬����ͬ��������
		billVO = billList_cmpfile.getSelectedBillVO();
		String cmpfileid = billVO.getStringValue("id");
		String filestate = billVO.getStringValue("filestate");
		String view_filestate = billVO.getStringViewValue("filestate");
		if ((("2".equals(filestate) || "4".equals(filestate)) && !this.hasReject(billVO)) || "5".equals(filestate)) {//1- �༭��, 2- ����������, 3- ��Ч, 4- ��ֹ������, 5- ʧЧ
			if (MessageBox.showConfirmDialog(this, "���ļ���״̬Ϊ[" + view_filestate + "],���ܽ��б༭,�Ƿ���Ҫ�鿴��") != JOptionPane.YES_OPTION) {
				return;
			} else {
				onLookFile();
				return;
			}
		} else if ("3".equals(filestate)) {
			int li_result = MessageBox.showOptionDialog(this, "�������ļ��Ѿ�[����], ���ѡ��[�༭]״̬����Ϊ[�༭��],\r\n����Ҫ���·���!����������²���:", "��ʾ", new String[] { "�鿴", "�༭", "ȡ��" }, 450, 150); //
			if (li_result == 0) { //
				onLookFile();
				return;
			} else if (li_result == 1) { //
				try {
					UIUtil.executeUpdateByDS(null, "update cmp_cmpfile set filestate='1' where id=" + cmpfileid);
					billList_cmpfile.refreshCurrSelectedRow();
				} catch (Exception e) {
					MessageBox.showException(this, e);
				}
			} else {
				return;
			}
		}
		billVO = billList_cmpfile.getSelectedBillVO();//����Ҫ����һ��
		BillCardPanel cardPanel = new BillCardPanel(billList_cmpfile.templetVO); // ����һ����Ƭ���
		cardPanel.setBillVO(billVO); //
		BillCardDialog dialog = new BillCardDialog(billList_cmpfile, billList_cmpfile.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE); // ������Ƭ�༭��
		cardPanel.setEditable("filestate", stateeditable);//�����ļ�״̬�Ƿ�ɱ༭,��������Ͼ����
		dialog.setVisible(true); // ��ʾ��Ƭ����
		if (dialog.getCloseType() == 1) { // �����ǵ��ȷ������!����Ƭ�е����ݸ����б�!
			BillVO return_BillVO = dialog.getBillVO();
			return_BillVO.setObject("blcorpname", new StringItemVO(return_BillVO.getStringViewValue("blcorpid")));
			return_BillVO.setObject("bsactname", new StringItemVO(return_BillVO.getStringViewValue("bsactid")));
			return_BillVO.setObject("ictypename", new StringItemVO(return_BillVO.getStringViewValue("ictypeid")));

			billList_cmpfile.setBillVOAt(billList_cmpfile.getSelectedRow(), return_BillVO, false); //
			billList_cmpfile.setRowStatusAs(billList_cmpfile.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			String return_blcorpid = return_BillVO.getStringValue("blcorpid");
			String return_blcorpname = return_BillVO.getStringViewValue("blcorpid");

			//������Ҫ�ж�һ������������Ʊ��ˣ�Ҳ��Ҫ����һ�£������ڷ��յ�ͼ����ʾ���������⣡�����/2012-03-14��
			try {
				if (!billList_cmpfile.getTempletItemVO("bsactid").isCardisshowable() && billList_cmpfile.getTempletItemVO("ictypeid").isCardisshowable()) {//���ģ���в���ʾҵ������ʾ�ڿ���ϵ���򽫷��յ���ԭ��¼ҵ���ֶμ�¼�ڿ���ϵ���������ڷ��ձ��ټ�һ���ֶΣ��ڿز�Ʒ���õ������/2012-07-17��
					String return_ictypeid = return_BillVO.getStringValue("ictypeid");
					String return_ictypename = return_BillVO.getStringViewValue("ictypeid");
					UIUtil.executeUpdateByDS(null, "update cmp_risk set blcorpid =" + return_blcorpid + ",blcorpname='" + return_blcorpname + "',bsactid=" + return_ictypeid + ",bsactname='" + return_ictypename + "' where cmpfile_id = " + cmpfileid);
				} else {
					String return_bsactid = return_BillVO.getStringValue("bsactid");
					String return_bsactname = return_BillVO.getStringViewValue("bsactid");
					//�����ļ�������������ҵ��Ҫͬ�����·��յ��ϵ�����������ҵ��
					UIUtil.executeUpdateByDS(null, "update cmp_risk set blcorpid =" + return_blcorpid + ",blcorpname='" + return_blcorpname + "',bsactid=" + return_bsactid + ",bsactname='" + return_bsactname + "' where cmpfile_id = " + cmpfileid);
				}
			} catch (Exception e) {
				MessageBox.showException(this, e);
			}
			billList_cmpfile.refreshCurrSelectedRow();//��Ҫˢ��һ�£������ļ�״̬���Ϊ��ɫ�����/2012-03-19��
		}
	}

	/**
	 * ��ť��ɾ���ļ������߼���ֻ�Ƕ�û�а汾�ŵ������ļ�����ɾ����������ɾ�����̼������Ϣ
	 */
	private void onDeleteFile() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billList_cmpfile.refreshCurrSelectedRow();//���²�һ�£���Ϊҳ������ݿ�����ʮ���ӻ�ܾ���ǰ�����ݡ�
		billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO.getStringValue("versionno") != null && !"".equals(billVO.getStringValue("versionno"))) {
			MessageBox.show(this, "���ļ����а汾����ɾ��!"); //
			return;
		}
		if ("2".equals(billVO.getStringValue("filestate"))) {//û�а汾����������[����������]���ļ�Ҳ������ɾ��
			MessageBox.show(this, "���ļ���״̬Ϊ[" + billVO.getStringViewValue("filestate") + "],����ɾ��!"); //
			return;
		}
		if (MessageBox.showConfirmDialog(this, "�˲�����ɾ��������ص�����,�Ƿ�ɾ��?") != JOptionPane.YES_OPTION) {
			return; //
		}
		String cmpfileid = billVO.getStringValue("id");
		// ɾ�������ļ�Ҫ��¼��־,��̨�����Ȳ����������id�������Ӳ�ѯ
		try {
			service.deleteCmpFileById(cmpfileid);
			billList_cmpfile.removeSelectedRow();// ҳ��ɾ��
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}

	/**
	 * ��ť���������ֱ����WLTButton�����ģ�����ʾ�����ص��߼����ڿ�Ƭ��ʼ��ʱ����ģ��ʲ�������߼������༭�ļ���������ļ���״̬�ǲ��ɱ༭�ģ�����Ҫ��������߼�������ļ�
	 */
	private void onLookFile() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(billList_cmpfile.templetVO); // ����һ����Ƭ���
		cardPanel.setBillVO(billVO);
		cardPanel.setVisiable("btn_temp", false);//���ʱ���ذ�ť������ģ�塿
		cardPanel.setEditable(false);
		BillCardDialog dialog = new BillCardDialog(billList_cmpfile, billList_cmpfile.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT); // ������Ƭ�����
		dialog.setVisible(true); // ��ʾ��Ƭ����
	}

	private boolean hasReject(BillVO _billvo) {
		String processid = _billvo.getStringValue("WFPRINSTANCEID");
		try {
			if (processid == null || "".equals(processid.trim())) {//�����ļ������ͷ�ֹ��������̣���һ�����ύ�ˣ���ûѡ��һ���ڵĽ����ˣ���ֱ��ȡ���ύ�ˣ���ʱ�ļ�״̬�ѱ�ɡ����뷢���С��������ֹ�С����༭ʱ�����û���
				if ("2".equals(_billvo.getStringValue("filestate"))) {
					UIUtil.executeUpdateByDS(null, "update cmp_cmpfile set filestate='1' where id=" + _billvo.getStringValue("id"));
				} else if ("4".equals(_billvo.getStringValue("filestate"))) {
					UIUtil.executeUpdateByDS(null, "update cmp_cmpfile set filestate='3' where id=" + _billvo.getStringValue("id"));
				}
				billList_cmpfile.refreshCurrSelectedRow();
				return true;
			}

			String count = UIUtil.getStringValueByDS(null, "select count(id) from pub_wf_dealpool where rootinstanceid='" + processid + "' and submitisapprove='N'");//�Ƿ����˻�
			if ("0".equals(count)) {
				return false;
			}
			return true;
		} catch (Exception e) {
			MessageBox.showException(this, e);
			return false;
		}
	}

}
