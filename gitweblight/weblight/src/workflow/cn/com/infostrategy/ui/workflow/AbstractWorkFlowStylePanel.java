package cn.com.infostrategy.ui.workflow;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.workflow.engine.WorkflowUIUtil;

/**
 * ������������!! �������TaskAndMsgCenterPanel��ϵ������!! ����һЩ�߼�������ͨ��!
 * �������Ƿǳ���Ҫ��Ӧ��,��ʱ�����Ǻ���Ӧ��!! ���Լ򻯹������Ŀ������͵��ڴ�󽵵�����Ŀ�ɱ�!!! ������������ȫ���Է�װ������!!! ���Ա����������!!!
 * ��ǰ�Ĺ��������ķ������� Style19,���µķ�����˼·����ԭ��������ͬ! �����Ǵӵ��ݳ���������,���Ǵ����̳����󶨵���!!!!
 * 
 * �ݸ�����߼�����ǰһ��,ֱ���ж�prinstance is null
 * select * from %ҵ�����% where prinstance is null and create_userid=%��¼��Ա%
 * 
 * ��������߼���:
 * �ӱ�pub_task_deal����,��ѯ�� select t1.id t1_id,t1.taskclass t1_taskclass,t1.dealuser...,t2.* from pub_task_deal t1,%ҵ�����% t2 where t1.tabname=%ҵ�����% and t1.pkvalue=t2.id and t1.dealuser=%��¼��Ա% order by createtime desc
 * �������ڴ�����������е������ҵ����м�¼,Ȼ��ͨ��������ѯ��ҵ���ļ�¼,Ȼ��ģ������ҵ����ģ��,��Ȼ�ܶ�������!
 * 
 * �Ѱ�����߼���:
 * �ӱ�pub_task_off����,��ѯ��ʵ�����Ҵ���ļ�¼!!!
 * select t1.id t1_id,t1.taskclass t1_taskclass,t1.dealuser...,t2.* from pub_task_off t1,%ҵ�����% t2 where t1.tabname=%ҵ�����% and t1.pkvalue=t2.id and t1.realdealuser=%��¼��Ա% order by dealtime desc
 * 
 * ���ĳ�˴�����ĳ�������,���Զ������������ҵ��Ѱ��������ȥ!!!
 * ����ǳ��͵���Ϣ,��鿴��,�и���ȷ�ϡ���ť,Ҳ�ǽ����Ѱ�����!!!
 * ���ĳ�˴���û�����������е�����,����ζ������Ȼ������������,�����ʱ��������ѹ���(�����ѹ���,���������ѽ���),��ʱ���������ǹ�����Ϣ��!!! Ȼ��Ҳ�ǵ����ȷ�ϡ�����ҵ��Ѱ�����!
 * 
 * ��������ƽ���ʹ���ز���Ҳ��ü�,�����Ѱ����е����������»�д����������ȥ!!
 * 
 * �������Ժ����ṩWebչʾ�Ľ���,Ȼ����Ե���ύֱ�ӽ���ϵͳ����!! ������������ϵͳ�ӿڵ�����!!!
 * @author xch
 *
 */
public abstract class AbstractWorkFlowStylePanel extends AbstractWorkPanel {

	private static final long serialVersionUID = 5538656513105393516L;

	private JTabbedPane tabbedPanel = null; //
	private WLTTabbedPane wlttabbedPanel = null; //

	private Pub_Templet_1VO pubTempletVO = null; //ģ�嶨��VO
	private BillListPanel billList_1, billList_2, billList_3; //�����б�

	private WorkflowUIUtil wfUIUtil = null; //
	private String str_templetCode = null; //
	private String str_tableName = null; //��ѯ��ҵ�����,�ǳ���Ҫ!!��Ϊ���������Ѱ����еĲ�ѯSQL,����ͨ�������������̬ƴ�ӵ�!!!
	private String str_savedTableName = null; //����ı���!
	private String str_pkName = null; //

	/**
	 * ģ�����,������ģ��!!!
	 * @return
	 */
	public abstract String getBillTempletCode();

	/**
	 * ��ʾ�ݸ���,������,�Ѱ��䱾��ҳǩ,Ĭ����������ʾ!!
	 * @return
	 */
	public boolean[] getShowTabIndex() {
		return new boolean[] { true, true, true };
	}

	//ҳǩλ��..
	public int getTabbedPos() {
		if ("����".equals(getMenuConfMapValueAsStr("ҳǩλ��"))) {
			return JTabbedPane.TOP; //
		}
		return JTabbedPane.LEFT; //
	}

	/**
	 * ��ʼ���߼�
	 */
	@Override
	public void initialize() {
		try {
			this.setLayout(new BorderLayout());
			pubTempletVO = UIUtil.getPub_Templet_1VO(getBillTempletCode()); //Ϊ���������ֻ��Ҫһ��ģ�嶨��VO�Ϳ�����!!!
			str_templetCode = pubTempletVO.getTempletcode(); //ģ�����
			str_tableName = pubTempletVO.getTablename(); //��ѯ�ı���!!!
			str_savedTableName = pubTempletVO.getSavedtablename(); //����ı���!
			str_pkName = pubTempletVO.getPkname(); //������
			pubTempletVO.setIsshowlistquickquery(Boolean.TRUE); //��ʾ���ٲ�ѯ

			if (getTabbedPos() == JTabbedPane.TOP) { //������ڶ���,��ʹ�������Լ���ҳǩ��ʱ��֧�����Ч��!! �Ժ���Ҫ֧��!!!
				wlttabbedPanel = new WLTTabbedPane(); //
			} else {
				tabbedPanel = new JTabbedPane(getTabbedPos()); //
			}
			//tabbedPanel.addChangeListener(this);  //

			boolean[] showTables = getShowTabIndex(); //
			//�ݸ���
			int li_indexcount = -1; //�ۼ��ܹ��м���ҳ��!
			if (showTables != null && showTables.length >= 1 && showTables[0]) {
				billList_1 = new BillListPanel(pubTempletVO); //
				billList_1.setItemsVisible(getWorkflowUIUtil().getDraftTaskHiddenFields(), false); //����ʱ��,�����������
				billList_1.addWorkFlowDealPanel(); //
				billList_1.getWorkflowDealBtnPanel().hiddenAllBtns();
				billList_1.getWorkflowDealBtnPanel().setInsertBtnVisiable(true);
				billList_1.getWorkflowDealBtnPanel().setUpdateBtnVisiable(true);
				billList_1.getWorkflowDealBtnPanel().setDeleteBtnVisiable(true);
				billList_1.getWorkflowDealBtnPanel().setProcessBtnVisiable(true);

				String str_userid = ClientEnvironment.getInstance().getLoginUserID(); //��¼��Ա����
				billList_1.setDataFilterCustCondition("create_userid = '" + str_userid + "' and wfprinstanceid is null"); //�Ƶ��˵�����,��û���������̵�!
				if (wlttabbedPanel != null) { //�����ͼƬ!!
					wlttabbedPanel.addTab(getTabTitle(1, getTabbedPos()), UIUtil.getImage(getTabImage(1)), billList_1); //
				} else {
					tabbedPanel.addTab(getTabTitle(1, getTabbedPos()), billList_1); //
				}
				li_indexcount++; //
			}

			//������..
			if (showTables != null && showTables.length >= 2 && showTables[1]) {
				billList_2 = new BillListPanel(pubTempletVO); //
				//billList_2.setAllBillListBtnVisiable(false); //��������ҵ��ť!!
				billList_2.setItemsVisible(getWorkflowUIUtil().getDealTaskHiddenFields(), false); //����Ǵ�������ǿ��������Щ�ֶ�!
				billList_2.setItemsVisible(getWorkflowUIUtil().getDealTaskShowFields(), true); //����Ǵ�������,��ǿ����ʾ��Щ�ֶ�!
				billList_2.addWorkFlowDealPanel(); //
				billList_2.getWorkflowDealBtnPanel().hiddenAllBtns(); //�������а�ť!!!
				billList_2.getWorkflowDealBtnPanel().setProcessBtnVisiable(true);
				billList_2.getWorkflowDealBtnPanel().setMonitorBtnVisiable(true);
				billList_2.getWorkflowDealBtnPanel().setExportBtnVisiable(true); //��ʾ������ť!!!

				billList_2.getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onBillListQuery_2(); //
					}
				}); //
				if (wlttabbedPanel != null) { //�����ͼƬ!!
					wlttabbedPanel.addTab(getTabTitle(2, getTabbedPos()), UIUtil.getImage(getTabImage(2)), billList_2); //
				} else {
					tabbedPanel.addTab(getTabTitle(2, getTabbedPos()), billList_2); //
				}
				li_indexcount++; //

				if (wlttabbedPanel != null) { //�����ͼƬ!!
					wlttabbedPanel.setSelectedIndex(li_indexcount); //
				} else {
					tabbedPanel.setSelectedIndex(li_indexcount); //
				}
			}

			//�Ѱ���..
			if (showTables != null && showTables.length >= 3 && showTables[2]) {
				billList_3 = new BillListPanel(pubTempletVO); //
				//billList_3.setAllBillListBtnVisiable(false); //��������ҵ��ť!!
				billList_3.setItemsVisible(getWorkflowUIUtil().getOffTaskHiddenFields(), false); //������Ѱ�����ǿ��������Щ�ֶ�!
				billList_3.setItemsVisible(getWorkflowUIUtil().getOffTaskShowFields(), true); //������Ѱ�����,��ǿ����ʾ��Щ�ֶ�!
				billList_3.addWorkFlowDealPanel(); //
				billList_3.getWorkflowDealBtnPanel().hiddenAllBtns();
				billList_3.getWorkflowDealBtnPanel().setMonitorBtnVisiable(true);
				billList_3.getWorkflowDealBtnPanel().setExportBtnVisiable(true); //��ʾ������ť!!!
				billList_3.getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						onBillListQuery_3(); //
					}
				}); //
				if (wlttabbedPanel != null) { //�����ͼƬ!!
					wlttabbedPanel.addTab(getTabTitle(3, getTabbedPos()), UIUtil.getImage(getTabImage(3)), billList_3); //
				} else {
					tabbedPanel.addTab(getTabTitle(3, getTabbedPos()), billList_3); //
				}
				li_indexcount++;
			}

			if (billList_2 != null) { //����д�����,���Զ����������е�����ֱ�Ӳ����!!
				String str_sql = new WorkflowUIUtil().getDealTaskSQL(str_templetCode, str_tableName, str_savedTableName, str_pkName, ClientEnvironment.getInstance().getLoginUserID(), null, false); //
				billList_2.queryDataByDS(null, str_sql, true); //ֱ��ͨ��SQL��ѯ!!
			}
			if (wlttabbedPanel != null) { //�����ͼƬ!!
				this.add(wlttabbedPanel, BorderLayout.CENTER); ////
			} else {
				this.add(tabbedPanel, BorderLayout.CENTER); ////
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * �������еĲ�ѯ!!
	 */
	private void onBillListQuery_2() {
		BillQueryPanel billQueryPanel = billList_2.getQuickQueryPanel(); //ȡ�ò�ѯ���!!
		String str_condition = billQueryPanel.getQuerySQLCondition(true, "t2"); //ȡ�ò�ѯ����!
		if (str_condition == null) {
			return; //���У�鲻��,������ѯ!
		}
		String str_sql = new WorkflowUIUtil().getDealTaskSQL(str_templetCode, this.str_tableName, str_savedTableName, this.str_pkName, ClientEnvironment.getCurrSessionVO().getLoginUserId(), str_condition, false); //
		billList_2.queryDataByDS(null, str_sql, true); //ֱ��ͨ��SQL����!!!
	}

	/**
	 * �Ѱ����еĲ�ѯ!!
	 */
	private void onBillListQuery_3() {
		BillQueryPanel billQueryPanel = billList_3.getQuickQueryPanel(); //ȡ�ò�ѯ���!!!
		String str_condition = billQueryPanel.getQuerySQLCondition(true, "t2"); //ȡ�ò�ѯ����!,����ģ���ж��������!!
		if (str_condition == null) {
			return; //���У�鲻��,������ѯ!
		}
		String str_sql = new WorkflowUIUtil().getOffTaskSQL(str_templetCode, this.str_tableName, str_savedTableName, this.str_pkName, ClientEnvironment.getInstance().getLoginUserID(), str_condition, false);
		billList_3.queryDataByDS(null, str_sql, true); //ֱ��ͨ��SQL����!!!
	}

	private WorkflowUIUtil getWorkflowUIUtil() {
		if (wfUIUtil != null) {
			return wfUIUtil;
		}
		wfUIUtil = new WorkflowUIUtil(); //
		return wfUIUtil;
	}

	//ȡ��ͼ���ͼƬ����!!
	private String getTabImage(int _type) {
		if (_type == 1) {
			return "office_167.gif"; //�ݸ���
		} else if (_type == 2) {
			return "zt_057.gif"; //
		} else if (_type == 3) {
			return "office_138.gif"; //
		} else {
			return null; //
		}
	}

	/**
	 * ��ǩ˵��,Ϊ�˺ÿ�,ʹ��Html���ʹ���Եô�һ��!!!
	 * @param _type
	 * @return
	 */
	private String getTabTitle(int _type, int _tabbedPos) {
		if (_tabbedPos == JTabbedPane.LEFT) {
			if (_type == 1) {
				return "<html><table><tr><td>&nbsp;<b><font color=#65B1AB size=4>��&nbsp;��&nbsp;��</font></b>&nbsp;</td></tr></table></html>"; //
			} else if (_type == 2) {
				return "<html><table><tr><td>&nbsp;<b><font color=#65B1AB size=4>��&nbsp;��&nbsp;��</font></b>&nbsp;</td></tr></table></html>"; //
			} else if (_type == 3) {
				return "<html><table><tr><td>&nbsp;<b><font color=#65B1AB size=4>��&nbsp;��&nbsp;��</font></b>&nbsp;</td></tr></table></html>"; //
			}
		} else { //�ڶ�
			if (_type == 1) {
				return "�ݸ���"; //
			} else if (_type == 2) {
				return "������"; //
			} else if (_type == 3) {
				return "�Ѱ���"; //
			}
		}
		return "";
	}

	public BillListPanel getBillList_1() {
		return billList_1;
	}

	public BillListPanel getBillList_2() {
		return billList_2;
	}

	public BillListPanel getBillList_3() {
		return billList_3;
	}

}
