package com.pushworld.ipushgrc.ui.cmpreport.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.keywordreplace.TemplateToWordUIUtil;

/**
 * �Ϲ��ۺϱ���ѯ!!! Ȩ�����
 * ���ǶԺϹ汨��(cmp_report)�ĵ��б�
 * @author xch
 *
 */
public class CmpReportQueryWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel billList = null; //
	WLTButton export_btn = null;

	@Override
	public void initialize() {
		billList = new BillListPanel("CMP_REPORT_CODE1"); //
		export_btn = new WLTButton("����Word");
		//		export_btn.addActionListener(this);
		//		billList.addBillListButton(export_btn);
		boolean wf = TBUtil.getTBUtil().getSysOptionBooleanValue("�Ϲ汨���Ƿ��߹�����", true);//��Ŀʵʩʱ���ۺϱ���ֱ�ӽ�����ύ�������Ӹ����á�loj/2015-05-21��
		if (!wf) {
			billList.setItemVisible("state", false);//������߹�������������״̬��loj/2015-05-21��
		}
		billList.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });//���б���ǰû�а�ť����ͻأ�������������ť�����/2012-03-23��
		billList.repaintBillListButton();
		this.add(billList); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == export_btn) {
			TemplateToWordUIUtil tem_word_util = new TemplateToWordUIUtil();
			BillVO[] checkItemVOs = billList.getSelectedBillVOs();
			if (checkItemVOs == null || checkItemVOs.length == 0) {
				MessageBox.showSelectOne(this);
				return;
			}
			String[] fileNames = new String[checkItemVOs.length];
			for (int i = 0; i < fileNames.length; i++) {
				fileNames[i] = checkItemVOs[i].getStringValue("reportname");
			}
			try {
				tem_word_util.createWordByOneListPanel("CMP_REPORT_CODE1", checkItemVOs, fileNames, this);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
