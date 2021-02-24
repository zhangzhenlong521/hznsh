package cn.com.infostrategy.ui.sysapp.corpdept;

import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

/**
 * ��λά���������׼��λ��ť�¼�����
 * @author hj
 * Feb 2, 2012 5:06:06 PM
 */
public class ImportPostGroupBtnAction implements WLTActionListener {
	private BillCardPanel cardPanel = null;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		if (cardPanel.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INIT)) { //�����������״̬���ð�ťʧЧ��
			MessageBox.show(cardPanel, "���״̬������ִ�д˲�����");
			return;
		}
		showPostGroupsDialog();
	}

	private void showPostGroupsDialog() {
		BillListDialog listdialog = new BillListDialog(cardPanel, "������׼��λ", "PUB_POST_CODE1", " deptid is null ", 800, 500);//����Ϊ�յĸ�λ�Ǹ�λ��
		listdialog.getBilllistPanel().setDataFilterCustCondition("deptid is null");//�������ݹ��ˣ���������ѯʱ���ݲ��ԡ����/2014-03-13��
		listdialog.getBilllistPanel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listdialog.setVisible(true);
		if (listdialog.getCloseType() == 1) {//��ѡ��λ��������ݻ��Զ����ص���λ����
			BillVO billvo = listdialog.getReturnBillVOs()[0]; //ȡ�÷��ص�BillVO
			cardPanel.setValueAt("name", billvo.getObject("name"));
			cardPanel.setValueAt("postlevel", billvo.getObject("postlevel"));
			cardPanel.setValueAt("descr", billvo.getObject("descr"));
			cardPanel.setValueAt("intent", billvo.getObject("intent"));
			cardPanel.setValueAt("innercontact", billvo.getObject("innercontact"));
			cardPanel.setValueAt("outcontact", billvo.getObject("outcontact"));
			cardPanel.setValueAt("education", billvo.getObject("education"));
			cardPanel.setValueAt("skill", billvo.getObject("skill"));
			cardPanel.setValueAt("refpostid", new RefItemVO(billvo.getStringValue("id"), "", billvo.getStringValue("name")));
		}
	}
}
