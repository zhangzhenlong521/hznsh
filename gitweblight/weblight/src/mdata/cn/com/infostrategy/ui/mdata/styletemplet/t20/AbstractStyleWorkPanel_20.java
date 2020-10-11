package cn.com.infostrategy.ui.mdata.styletemplet.t20;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;

/**
 * �������ķ��ģ��! ��ģ�������ص��ǿ�������"�ݸ���","������","�Ѱ���"����ʾ���
 * ��ѹ���������һ���ǳ����õ�ͨ��ģ��,�Ժ�����һ������ģ�����Ϳ����ˡ�
 * Ҫ�ɹ�����������,�����¼����ϸ�Լ�����������أ�
 * 1.���б���������4���ֶΣ�create_userid   decimal,billtype  varchar(30),busitype  varchar(30),wfprinstanceid   decimal,
 * 2.ģ����������б��ѯ����ʾ,����ҳ��ᱨ��
 * 3.
 * @author lcj
 */
public abstract class AbstractStyleWorkPanel_20 extends AbstractStyleWorkPanel {

	private Pub_Templet_1VO pubTempletVO = null; //ģ�嶨��VO
	private BillListPanel billList_1, billList_2, billList_3; //�����б�
	private boolean isBillList_1Visable, isBillList_2Visable, isBillList_3Visable; //���������б��Ƿ���ʾ

	public AbstractStyleWorkPanel_20() {

	}

	public void initialize() {
		super.initialize(); //
		try {
			this.setLayout(new BorderLayout());
			pubTempletVO = UIUtil.getPub_Templet_1VO(getBillTempletCode()); //Ϊ���������ֻ��Ҫһ��ģ�嶨��VO�Ϳ�����!!!
			pubTempletVO.setIsshowlistquickquery(Boolean.TRUE); //��ʾ���ٲ�ѯ

			String str_userid = ClientEnvironment.getInstance().getLoginUserID(); //��¼��Ա����
			String str_tablename = pubTempletVO.getSavedtablename(); //����ı���...

			JTabbedPane pane = new JTabbedPane(JTabbedPane.LEFT); //
			if (getBillList_1Visable()) {
				billList_1 = new BillListPanel(pubTempletVO); //
				billList_1.addWorkFlowDealPanel(); //���ӹ��������
				billList_1.getWorkflowDealBtnPanel().hiddenAllBtns();
				billList_1.getWorkflowDealBtnPanel().setInsertBtnVisiable(true);
				billList_1.getWorkflowDealBtnPanel().setUpdateBtnVisiable(true);
				billList_1.getWorkflowDealBtnPanel().setDeleteBtnVisiable(true);
				billList_1.getWorkflowDealBtnPanel().setProcessBtnVisiable(true);
				billList_1.getWorkflowDealBtnPanel().setProcessBtnVisiable(true);
				//billList_1.QueryDataByCondition(" id<100");
				billList_1.setDataFilterCustCondition("create_userid = '" + str_userid + "' and wfprinstanceid is null"); //�Ƶ��˵�����,��û���������̵�!
				pane.addTab(getTabTitle_1(), billList_1); //
			}
			if (getBillList_2Visable()) {
				billList_2 = new BillListPanel(pubTempletVO); //
				billList_2.addWorkFlowDealPanel(); //
				billList_2.getWorkflowDealBtnPanel().hiddenAllBtns();
				if (new TBUtil().getSysOptionBooleanValue("����������ť��������Ƿ���ʾ���հ�ť", true)) {
					billList_2.getWorkflowDealBtnPanel().setReceiveBtnVisiable(true);
				}
				billList_2.getWorkflowDealBtnPanel().setProcessBtnVisiable(true);
				billList_2.getWorkflowDealBtnPanel().setMonitorBtnVisiable(true);
				billList_2.getWorkflowDealBtnPanel().setExportBtnVisiable(true); //
				//billList_2.getWorkflowDealBtnPanel().setDeleteBtnVisiable(true);
				billList_2.setDataFilterCustCondition("wfprinstanceid in (select t1.id from pub_wf_prinstance t1,pub_wf_dealpool t2 where t1.id=t2.prinstanceid and t1.billtablename='" + str_tablename + "' and t2.participant_user='" + ClientEnvironment.getInstance().getLoginUserID()
						+ "' and t2.issubmit='N' and t2.isprocess='N')"); //������,���ύ����δ������
				//billList_2.setAllBillListBtnVisiable(true); //
				//billList_2.QueryDataByCondition(null);	//�����������Զ����أ���  ��Ϊģ������Ȩ�޹��ˣ�Ҳ�ü���
				pane.addTab(getTabTitle_2(), billList_2); //
			}
			if (getBillList_3Visable()) {
				billList_3 = new BillListPanel(pubTempletVO); //
				billList_3.addWorkFlowDealPanel(); //
				billList_3.getWorkflowDealBtnPanel().hiddenAllBtns();
				billList_3.getWorkflowDealBtnPanel().setCancelBtnVisiable(true);
				billList_3.getWorkflowDealBtnPanel().setMonitorBtnVisiable(true);
				billList_3.getWorkflowDealBtnPanel().setExportBtnVisiable(true); //
				billList_3.setDataFilterCustCondition("wfprinstanceid in (select id from pub_wf_prinstance where billtablename='" + str_tablename + "' and submiterhist like '%;" + ClientEnvironment.getInstance().getLoginUserID() + ";%')"); //�Ѱ���
				billList_3.setAllBillListBtnVisiable(false); //		
				pane.addTab(getTabTitle_3(), billList_3); //
			}
			this.add(pane, BorderLayout.CENTER);
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
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

	//����"�ݸ���","������","�Ѱ���"����ʾ���
	public abstract boolean getBillList_1Visable();

	public abstract boolean getBillList_2Visable();

	public abstract boolean getBillList_3Visable();
}