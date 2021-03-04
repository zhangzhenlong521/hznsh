package cn.com.pushworld.salary.ui.target.p090;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * Ա����Ч����ָ��Ч�湤������
 * @author haoming
 * create by 2013-8-31
 */
public class PersonJXDetail extends AbstractWorkPanel implements BillListHtmlHrefListener {

	private static final long serialVersionUID = 1L;
	private boolean calcQQ = TBUtil.getTBUtil().getSysOptionBooleanValue("�Ƿ�������鹤��", true); //Ĭ�ϼ������鹤�ʡ�������������鹤�ʣ��ܷ�����������Ҫ��ġ�

	public void initialize() {
		String checkDate = new SalaryUIUtil().getCheckDate();
		WLTTabbedPane tabPanel = new WLTTabbedPane();
		BillListPanel listPanel = new BillListPanel("V_SAL_SCORE_POST_DEPT_CODE1");
		listPanel.addBillListHtmlHrefListener(this);
		QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) listPanel.getQuickQueryPanel().getCompentByKey("checkdate");
		if (dateRef != null) {
			dateRef.setValue(checkDate);
		}
		tabPanel.addTab("Ա����ָ��Ч�湤��", listPanel);

		BillListPanel listPanel_xytotle = new BillListPanel("V_SAL_PERSON_XY_PAY_CODE2");
		QueryCPanel_UIRefPanel dateRef2 = (QueryCPanel_UIRefPanel) listPanel_xytotle.getQuickQueryPanel().getCompentByKey("checkdate");
		if (dateRef != null) {
			dateRef2.setValue(checkDate);
		}

		tabPanel.addTab("Ա������Ч�湤��", listPanel_xytotle);

		BillListPanel listPanel_totle = new BillListPanel("V_SAL_PERSON_XY_PAY_CODE1");
		if (!calcQQ) {
			listPanel_totle.setItemVisible("money", false);
		}
		QueryCPanel_UIRefPanel dateRef3 = (QueryCPanel_UIRefPanel) listPanel_totle.getQuickQueryPanel().getCompentByKey("checkdate");
		if (dateRef != null) {
			dateRef3.setValue(checkDate);
		}

		tabPanel.addTab("Ա�����˵÷ּ����鹤��", listPanel_totle);

		this.add(tabPanel);
	}

	//������ӣ���ʾ��������е���ϸֵ
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent event) {
		if ("realvalue".equals(event.getItemkey())) {
			BillVO svo = event.getBillListPanel().getSelectedBillVO();
			HashVO vos[] = null;
			try {
				vos = UIUtil.getHashVoArrayByDS(null, "select name,value,type from sal_factor_calvalue where logid = '" + svo.getStringValue("logid") + "' and foreignid ='" + svo.getStringValue("id") + "' and type='Ա������ָ��' ");
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (vos == null || vos.length == 0) {
				MessageBox.show(this, "��ָ��û�м�¼��������е�ֵ");
				return;
			}
			BillListPanel l = new BillListPanel(vos);
			BillListDialog listdialog = new BillListDialog(this, "��ϸ", l, 500, 600, false);
			listdialog.setVisible(true);
		}
	}

}
