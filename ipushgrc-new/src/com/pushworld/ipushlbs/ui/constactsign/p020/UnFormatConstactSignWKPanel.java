package com.pushworld.ipushlbs.ui.constactsign.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillOfficeDialog;

import com.pushworld.ipushlbs.ui.printcomm.CommonHtmlOfficeConfig;
import com.pushworld.ipushlbs.ui.printcomm.PrintBillOfficeIntercept;

/**
 * ��ӡ��ͬ
 * 
 * @author yinliang
 * @since 2011.12.15
 */
public class UnFormatConstactSignWKPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener {
	private BillListPanel billList;
	WLTButton btn_print;

	@Override
	public void initialize() {
		billList = new BillListPanel("UNFORMAT_DEAL_CHECK_CODE1_1");
		billList.addBillListHtmlHrefListener(this); // �����Ӽ���
		btn_print = new WLTButton("��ӡ");
		btn_print.addActionListener(this);
		btn_print.setVisible(true);
		billList.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD), btn_print });
		billList.repaintBillListButton();
		this.add(billList);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_print)
			onPrint(); // ��ӡ����
	}

	/**
	 * ������ӡ����
	 */
	private synchronized void onPrint() {
		BillVO billvo = billList.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		// �Ƿ��Ѵ�ӡ
		String sql_query = "select print from " + billvo.getSaveTableName() + " where id = " + billvo.getStringValue("id");
		try {
			String printstate = UIUtil.getStringValueByDS(null, sql_query);
			if (printstate.equals("��")) {
				MessageBox.show(billList, "�Ѿ���ӡ�������ٽ��д�ӡ��");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// �����м���officedialog����
		BillOfficeDialog officeDialog = new BillOfficeDialog(billList);
		String filename = billvo.getStringValue("DEAL_CONTENT");
		// ����office����
		officeDialog.setTitle(new TBUtil().convertHexStringToStr(filename.substring(filename.lastIndexOf("_") + 1, filename.lastIndexOf("."))));
		// ��ΪBillOfficeIntercept�ǳ����࣬�������ｨһ����ȥ�̳�����Ȼ��������newһ��
		officeDialog.addSomeActionListener(new PrintBillOfficeIntercept(billvo, officeDialog));

		officeDialog.setIfselfdesc(true); //
		officeDialog.setIfshowprint_all(false); // ȫ��
		officeDialog.setIfshowprint_fen(false); // �ִ�
		officeDialog.setIfshowsave(false); // ���水ť
		officeDialog.setIfshowprint_tao(false); // �״�
		officeDialog.setIfshowwater(false); // ˮӡ
		officeDialog.setIfshowprint(true); // ��ӡ
		officeDialog.setIfshowshowcomment(false);
		officeDialog.setIfshowhidecomment(false);
		officeDialog.setIfshowedit(false); // �޶�
		officeDialog.setIfshowshowedit(false); // ��ʾ�޶�
		officeDialog.setIfshowhideedit(false); // �����޶�
		officeDialog.setIfshowacceptedit(false); // �����޶�

		officeDialog.initialize(billvo.getStringValue("DEAL_CONTENT"), false, false, "officecompfile");
		officeDialog.setVisible(true);
		billList.refreshCurrSelectedRow();
	}

	// ������
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent event) {
		BillVO billvo = event.getBillListPanel().getSelectedBillVO();
		String filename = billvo.getStringValue("DEAL_CONTENT");
		// ����office����
		BillOfficeDialog dialog = new BillOfficeDialog(event.getBillListPanel());
		dialog.setTitle(new TBUtil().convertHexStringToStr(filename.substring(filename.lastIndexOf("_") + 1, filename.lastIndexOf("."))));
		// ���õ����Ĵ�����ʾ
		CommonHtmlOfficeConfig.OfficeConfig(filename, billvo, dialog);
		dialog.setVisible(true);
	}

}
