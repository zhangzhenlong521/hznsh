package com.pushworld.ipushlbs.ui.constactmanage.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillOfficeDialog;

import com.pushworld.ipushlbs.ui.printcomm.CommonHtmlOfficeConfig;
import com.pushworld.ipushlbs.ui.printcomm.PrintBillOfficeIntercept;

/**
 * �Ǳ�׼��ͬ��ѯ����
 * 
 * @author yinliang
 * @since 2011.12.13
 */
public class UnFormatConstactViewWKPanel extends AbstractWorkPanel implements BillListHtmlHrefListener, ActionListener {
	private WLTButton btn_print;
	private BillListPanel billlist;

	/**
	 * �����ӡ��༭��ɾ����ť��Ϊ������
	 */
	@Override
	public void initialize() {
		billlist = new BillListPanel("LBS_UNSTDFILE_CODE3"); // �Ǳ�׼��ͬ�ļ�ģ��
		billlist.addBillListHtmlHrefListener(this); // ���볬���Ӽ���
		btn_print = new WLTButton("��ӡ");
		btn_print.addActionListener(this);
		btn_print.setVisible(true);
		billlist.addBillListButton(btn_print);
		billlist.repaintBillListButton();
		this.add(billlist);
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		String str_filetest = _event.getBillListPanel().getSelectedBillVO().getStringValue("testfile");
		if (str_filetest == null) {
			MessageBox.show(billlist, "�ú�ͬδ��дword�ı���");
		} else {
			BillOfficeDialog offdialog = new BillOfficeDialog(_event.getBillListPanel());
			offdialog.setTitle(new TBUtil().convertHexStringToStr(str_filetest.substring(str_filetest.lastIndexOf("_") + 1, str_filetest.lastIndexOf("."))));
			CommonHtmlOfficeConfig.OfficeConfig(str_filetest, billlist.getSelectedBillVO(), offdialog);
			offdialog.setVisible(true);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_print)
			onPrint(); // ��ӡ����
	}

	/**
	 * ������ӡ����
	 */
	private void onPrint() {
		BillVO billvo = billlist.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		// �����м���officedialog����,ʹ���±ߵĹ��췽����Ȼ��Ϊ��set���֣�����������
		BillOfficeDialog officeDialog = new BillOfficeDialog(billlist);

		String filename = billvo.getStringValue("TESTFILE");
		officeDialog.setTitle(new TBUtil().convertHexStringToStr(filename.substring(filename.lastIndexOf("_") + 1, filename.lastIndexOf("."))));
		// ��ΪBillOfficeIntercept�ǳ����࣬�������ｨһ����ȥ�̳�����Ȼ��������newһ��
		officeDialog.setIfselfdesc(true);
		officeDialog.setEnabled(true);
		officeDialog.addSomeActionListener(new PrintBillOfficeIntercept(billvo, officeDialog));
		//
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
		officeDialog.initialize(billvo.getStringValue("TESTFILE"), false, false, "officecompfile");

		officeDialog.setVisible(true);
	}

}
