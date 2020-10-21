package com.pushworld.ipushgrc.ui.wfrisk.p110;

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

import com.pushworld.ipushgrc.ui.wfrisk.CmpFileHistoryViewDialog;
import com.pushworld.ipushgrc.ui.wfrisk.CmpfileAndWFGraphDialog;

/**
 * ��ʷ�汾ά����ɾ�������ļ�����ʷ�汾�����°汾����ɾ����
 * @author lcj
 *
 */
public class WFAndRiskVersionWKPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener {
	private BillListPanel billList_cmpfile = null; //
	private WLTButton btn_version = null;

	@Override
	public void initialize() {
		String templetcode = TBUtil.getTBUtil().getSysOptionHashItemStringValue("�������������ļ���ģ��", "�汾����", "CMP_CMPFILE_CODE3");//��ǰ��ͬά��ģ�壬�����ϳ�Ͷ��ҵ�ڿأ��ͻ��ڿز�ͳһ���汾�������޸�֮�����/2015-01-05��
		billList_cmpfile = new BillListPanel(templetcode);
		if (billList_cmpfile.getTempletItemVO("versionno") != null) {
			billList_cmpfile.getTempletItemVO("versionno").setListishtmlhref(false);//���ò���ʾ����
		}
		billList_cmpfile.setDataFilterCustCondition("versionno is not null");//ֻ��ѯ���а汾�ŵ��ļ�
		btn_version = new WLTButton("��ʷ�汾");
		btn_version.addActionListener(this);
		billList_cmpfile.addBillListButton(btn_version);
		billList_cmpfile.repaintBillListButton();
		billList_cmpfile.addBillListHtmlHrefListener(this);
		this.add(billList_cmpfile); //
	}

	public void actionPerformed(ActionEvent e) {
		BillVO billVO = billList_cmpfile.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String cmpfileid = billVO.getStringValue("id");
		CmpFileHistoryViewDialog dialog = new CmpFileHistoryViewDialog(this, "�ļ�[" + billVO.getStringValue("cmpfilename") + "]����ʷ�汾", cmpfileid, true); //
		dialog.setVisible(true); //
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		onLookFileAndWf();
	}

	/**
	 * �ļ�����-���ӣ��鿴�����ļ�������������
	 */
	private void onLookFileAndWf() {
		BillVO billVO = billList_cmpfile.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "��ѡ��һ�������ļ�!"); //
			return;
		}
		CmpfileAndWFGraphDialog dialog = new CmpfileAndWFGraphDialog(this, "�鿴�ļ�������", billVO.getStringValue("id"));
		dialog.setVisible(true);
	}
}
