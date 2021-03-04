package cn.com.infostrategy.ui.mdata.styletemplet.t19;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;

/**
 * �������ķ��ģ��!
 * ��ѹ���������һ���ǳ����õ�ͨ��ģ��,�Ժ�����һ������ģ�����Ϳ����ˡ�
 * Ҫ�ɹ�����������,�����¼����ϸ�Լ�����������أ�
 * 1.���б���������4���ֶΣ�create_userid   decimal,billtype  varchar(30),busitype  varchar(30),wfprinstanceid   decimal,
 * 2.ģ����������б��ѯ����ʾ,����ҳ��ᱨ��
 * 3.
 * @author xch
 */
public abstract class AbstractStyleWorkPanel_19 extends AbstractStyleWorkPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Pub_Templet_1VO pubTempletVO = null; //ģ�嶨��VO
	private BillListPanel billList_1, billList_2, billList_3; //�����б�

	public AbstractStyleWorkPanel_19() {

	}

	public void initialize() {
		super.initialize(); //
		try {
			this.setLayout(new BorderLayout());
			pubTempletVO = UIUtil.getPub_Templet_1VO(getBillTempletCode()); //Ϊ���������ֻ��Ҫһ��ģ�嶨��VO�Ϳ�����!!!
			pubTempletVO.setIsshowlistquickquery(Boolean.TRUE); //��ʾ���ٲ�ѯ
			billList_1 = new BillListPanel(pubTempletVO); //
			billList_2 = new BillListPanel(pubTempletVO); //
			billList_3 = new BillListPanel(pubTempletVO); //

			billList_1.addWorkFlowDealPanel(); //���ӹ��������
			billList_2.addWorkFlowDealPanel(); //
			billList_3.addWorkFlowDealPanel(); //

			billList_1.getWorkflowDealBtnPanel().hiddenAllBtns();
			billList_1.getWorkflowDealBtnPanel().setInsertBtnVisiable(true);
			billList_1.getWorkflowDealBtnPanel().setUpdateBtnVisiable(true);
			billList_1.getWorkflowDealBtnPanel().setDeleteBtnVisiable(true);
			billList_1.getWorkflowDealBtnPanel().setProcessBtnVisiable(true);

			billList_2.getWorkflowDealBtnPanel().hiddenAllBtns();
			if (new TBUtil().getSysOptionBooleanValue("����������ť��������Ƿ���ʾ���հ�ť", true)) {
				billList_2.getWorkflowDealBtnPanel().setReceiveBtnVisiable(true);
			}
			billList_2.getWorkflowDealBtnPanel().setProcessBtnVisiable(true);
			billList_2.getWorkflowDealBtnPanel().setMonitorBtnVisiable(true);
			billList_2.getWorkflowDealBtnPanel().setExportBtnVisiable(true); //��ʾ������ť!!!
			//billList_2.getWorkflowDealBtnPanel().setDeleteBtnVisiable(true);

			billList_3.getWorkflowDealBtnPanel().hiddenAllBtns();
			//			billList_3.getWorkflowDealBtnPanel().setCancelBtnVisiable(true);
			billList_3.getWorkflowDealBtnPanel().setMonitorBtnVisiable(true);
			billList_3.getWorkflowDealBtnPanel().setExportBtnVisiable(true); //��ʾ������ť!!!

			String str_userid = ClientEnvironment.getInstance().getLoginUserID(); //��¼��Ա����
			String str_savedTablename = pubTempletVO.getSavedtablename(); //����ı���...
			//String str_queryTablename = pubTempletVO.getTablename();//
			billList_1.setDataFilterCustCondition("create_userid = '" + str_userid + "' and wfprinstanceid is null"); //�Ƶ��˵�����,��û���������̵�!

			billList_2.setDataFilterCustCondition("wfprinstanceid in (select t1.id from pub_wf_prinstance t1,pub_wf_dealpool t2 where t1.id=t2.prinstanceid and t1.billtablename='" + str_savedTablename + "' and t2.participant_user='" + ClientEnvironment.getInstance().getLoginUserID()
					+ "' and t2.issubmit='N' and t2.isprocess='N')"); //������,���ύ����δ������

			billList_3.setDataFilterCustCondition("wfprinstanceid in (select id from pub_wf_prinstance where billtablename='" + str_savedTablename + "' and submiterhist like '%;" + ClientEnvironment.getInstance().getLoginUserID() + ";%')"); //�Ѱ���

			billList_2.setAllBillListBtnVisiable(false); //
			billList_3.setAllBillListBtnVisiable(false); //

			JTabbedPane pane = new JTabbedPane(JTabbedPane.LEFT); //
			pane.addTab(getTabTitle_1(), billList_1); //
			pane.addTab(getTabTitle_2(), billList_2); //
			pane.addTab(getTabTitle_3(), billList_3); //

			this.add(pane, BorderLayout.CENTER);
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public BillListPanel getBillListPanel_1() {
		return billList_1;
	}

	public BillListPanel getBillListPanel_2() {
		return billList_2;
	}

	public BillListPanel getBillListPanel_3() {
		return billList_3;
	}

	private String getTabTitle_1() {
		return "<html><table><tr><td>&nbsp;<b><font color=#65B1AB size=4>��&nbsp;��&nbsp;��</font></b>&nbsp;</td></tr></table></html>"; //
	}

	private String getTabTitle_2() {
		return "<html><table><tr><td>&nbsp;<b><font color=#65B1AB size=4>��&nbsp;��&nbsp;��</font></b>&nbsp;</td></tr></table></html>"; //
	}

	private String getTabTitle_3() {
		return "<html><table><tr><td>&nbsp;<b><font color=#65B1AB size=4>��&nbsp;��&nbsp;��</font></b>&nbsp;</td></tr></table></html>"; //
	}

	//���嵥��ģ�����
	public abstract String getBillTempletCode(); //
}