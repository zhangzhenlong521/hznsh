package cn.com.pushworld.salary.ui.person.p070;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.paymanage.RefDialog_Month;

public class PersonQuantifyCheckTargetWKPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	private BillListPanel listPanel = null;
	private WLTButton btn_add, btn_edit, btn_delete; // �б��ϵ����а�ť
	private WLTTabbedPane tabPane;
	private BillListPanel postTypeListPanel, targetListPanel;
	private WLTButton btn_add_2, btn_edit_2, btn_delete2, btn_action_1, btn_action_2;
	private TBUtil tbUtil = null; //zzl[2020-5-11]
	private  Boolean wgflg=false;
	public void initialize() {
		wgflg=getTBUtil().getSysOptionBooleanValue("�Ƿ���������ָ�����ģʽ", false);//zzl[2020-5-11]
		tabPane = new WLTTabbedPane();
		tabPane.addChangeListener(this);
		tabPane.addTab("����λ��鿴", getPostTargetPanel());
		tabPane.addTab("ȫ��", getAllTargetPanel());
		this.add(tabPane);
	}

	private JComponent getPostTargetPanel() {
		postTypeListPanel = new BillListPanel("PUB_COMBOBOXDICT_SALARY");
		postTypeListPanel.QueryDataByCondition("type = 'н��_��λ����'");
		postTypeListPanel.setDataFilterCustCondition("type = 'н��_��λ����'");
		targetListPanel = new BillListPanel("SAL_PERSON_CHECK_LIST_QUANTIFY_GROUP");
		targetListPanel.setDataFilterCustCondition("targettype='Ա������ָ��'");
		postTypeListPanel.addBillListSelectListener(this);
		btn_add_2 = new WLTButton("����");
		btn_edit_2 = new WLTButton("�޸�");
		btn_delete2 = new WLTButton("ɾ��");
		btn_action_2 = new WLTButton("����Ч�湤��", UIUtil.getImage("bug_2.png"));
		btn_add_2.addActionListener(this);
		btn_edit_2.addActionListener(this);
		btn_delete2.addActionListener(this);
		btn_action_2.addActionListener(this);
		//2020��7��12��12:38:36 fj ���ǹ���ԱȨ�޿�������������ť
		if(!ClientEnvironment.isAdmin()){
			btn_add_2.setVisible(false);
			btn_edit_2.setVisible(false);
			btn_delete2.setVisible(false);
		}
		targetListPanel.addBatchBillListButton(new WLTButton[] { btn_add_2, btn_edit_2, btn_delete2, btn_action_2 });
		targetListPanel.repaintBillListButton();
		WLTSplitPane splitpane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, postTypeListPanel, targetListPanel);
		return splitpane;
	}

	/*
	 *
	 */
	private JPanel getAllTargetPanel() {
		if(wgflg){
			listPanel = new BillListPanel("SAL_PERSON_CHECK_LIST_QUANTIFY_WG");
		}else{
			listPanel = new BillListPanel("SAL_PERSON_CHECK_LIST_QUANTIFY");
		}
		listPanel.setDataFilterCustCondition("targettype='Ա������ָ��'");
		listPanel.QueryDataByCondition(null);
		btn_add = new WLTButton("����");
		btn_edit = new WLTButton("�޸�");
		btn_delete = new WLTButton("ɾ��");
		btn_action_1 = new WLTButton("����Ч�湤��", UIUtil.getImage("bug_2.png"));
		btn_add.addActionListener(this);
		btn_edit.addActionListener(this);
		btn_delete.addActionListener(this);
		btn_action_1.addActionListener(this);
		//2020��7��12��12:38:36 fj ���ǹ���ԱȨ�޿�������������ť
		if(!ClientEnvironment.isAdmin()){
			btn_add.setVisible(false);
			btn_edit.setVisible(false);
			btn_delete.setVisible(false);
		}
		listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete, btn_action_1 });
		listPanel.repaintBillListButton();
		return listPanel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_add) {
			onAdd(listPanel, 1);
		} else if (e.getSource() == btn_edit) {
			onEdit(listPanel);
		} else if (e.getSource() == btn_delete) {
			onDelete(listPanel);
		} else if (e.getSource() == btn_add_2) {
			onAdd2();
		} else if (e.getSource() == btn_edit_2) {
			onEdit2();
		} else if (e.getSource() == btn_delete2) {
			onDelete2();
		} else if (e.getSource() == btn_action_1) {
			BillVO vo = listPanel.getSelectedBillVO();
			onAction(listPanel, vo);
		} else if (e.getSource() == btn_action_2) {
			BillVO vo = targetListPanel.getSelectedBillVO();
			onAction(targetListPanel, vo);
		}
	}

	/**
	 * ����
	 */
	private void onAdd(BillListPanel listPanel, int tab) {
		BillCardPanel cardPanel = null;
		// ����һ����Ƭ���
		cardPanel = new BillCardPanel(listPanel.getTempletVO());
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		cardPanel.setRealValueAt("targettype", "Ա������ָ��");
		// ������
		BillCardDialog cardDialog = new BillCardDialog(listPanel, "����Ա������ָ��", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setVisible(true);

		// ȷ������
		if (cardDialog.getCloseType() == 1) {
			int li_newrow = listPanel.newRow(false); //
			listPanel.setBillVOAt(li_newrow, cardDialog.getBillVO(), false);
			listPanel.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); // �����б���е�����Ϊ��ʼ��״̬.
			listPanel.setSelectedRow(li_newrow); //		
			listPanel.refreshCurrSelectedRow();
		}
	}

	/**
	 * �༭
	 */
	private void onEdit(BillListPanel listPanel) {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(listPanel);
			return;
		}
		// ����һ����Ƭ���
		BillCardPanel cardPanel = new BillCardPanel(listPanel.getTempletVO());
		cardPanel.setBillVO(billVO);
		if(cardPanel.getRealValueAt("catalogid").equals("215") || cardPanel.getRealValueAt("catalogid").equals("����ָ��")){
			cardPanel.setVisiable("checkedwg",true);
			cardPanel.setVisiable("checkedpost",false);
		}else{
			cardPanel.setVisiable("checkedwg",false);
			cardPanel.setVisiable("checkedpost",true);
		}
		// ������
		BillCardDialog dialog = new BillCardDialog(this, "�޸�Ա������ָ��", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true);
		// ȷ������
		if (dialog.getCloseType() == 1) {
			if (listPanel.getSelectedRow() == -1) {
			} else {
				listPanel.setBillVOAt(listPanel.getSelectedRow(), dialog.getBillVO());
				listPanel.setRowStatusAs(listPanel.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
				listPanel.refreshCurrSelectedRow();
			}
		}
	}

	/**
	 * ɾ��
	 */
	private void onDelete(BillListPanel listPanel) {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(listPanel);
			return;
		}
		listPanel.doDelete(false);
		try {
			UIUtil.executeBatchByDS(null, new String[] { "delete from sal_person_check_post where targetid=" + billVO.getPkValue() });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onAdd2() {
		BillCardPanel cardPanel = null;
		// ����һ����Ƭ���
		cardPanel = new BillCardPanel(listPanel.getTempletVO());
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		cardPanel.setRealValueAt("targettype", "Ա������ָ��");

		// ������
		BillCardDialog cardDialog = new BillCardDialog(listPanel, "����Ա������ָ��", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardDialog.setVisible(true);
		if (cardDialog.getCloseType() == 1) {
			postTypeListPanel.QueryDataByCondition(null);
		}
	}

	private void onEdit2() {
		BillVO billVO = targetListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(targetListPanel);
			return;
		}
		// ����һ����Ƭ���
		BillCardPanel cardPanel = new BillCardPanel(listPanel.getTempletVO());
		cardPanel.queryDataByCondition("id = " + billVO.getPkValue());

		// ������
		BillCardDialog dialog = new BillCardDialog(this, "�޸�Ա������ָ��", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		dialog.setVisible(true);
		// ȷ������
		if (dialog.getCloseType() == 1) {
			targetListPanel.refreshData();
		}
	}

	private void onDelete2() {
		BillVO billVO = targetListPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(targetListPanel);
			return;
		}
		if (!MessageBox.confirmDel(this)) {
			return; //
		}
		try {
			UIUtil.executeBatchByDS(null, new String[] { "delete from sal_person_check_list where id = " + billVO.getPkValue(), "delete from sal_person_check_post where targetid=" + billVO.getPkValue() });
			targetListPanel.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent event) {
		BillVO selectVo = event.getCurrSelectedVO();
		targetListPanel.setDataFilterCustCondition("targettype='Ա������ָ��' and postid like '%;" + selectVo.getStringValue("id") + ";%'");
		targetListPanel.QueryDataByCondition("targettype='Ա������ָ��' and postid like '%;" + selectVo.getStringValue("id") + ";%'");
	}

	HashVO resuleVos[] = null;

	public void onAction(Container _parent, final BillVO _billvo) {
		if (_billvo == null) {
			MessageBox.showSelectOne(_parent);
			return;
		}
		final String date = getDate(_parent);
		if (TBUtil.isEmpty(date)) {
			return;
		}
		new SplashWindow(_parent, "ϵͳ����Ŭ��������...", new AbstractAction() {
			public void actionPerformed(final ActionEvent actionevent) {
				Timer timer = new Timer();
				try {
					timer.schedule(new TimerTask() {
						SplashWindow w = (SplashWindow) actionevent.getSource();

						public void run() { // ��Ҫ���߳��������Ƿ��Ѿ������ݼ��ؽ����ˡ�
							String schedule = null;
							Object obj[] = null;
							try {
								obj = (Object[]) getService().getRemoteActionSchedule("Ա������ָ������" + _billvo.getStringValue("id"), "Ա������ָ������");
							} catch (Exception e) {
								e.printStackTrace();
								this.cancel();
							}
							if (obj != null) {
								schedule = (String) obj[0];
								w.setWaitInfo(schedule);
							} else {
								w.setWaitInfo("ϵͳ����Ŭ��������...");
							}
						}
					}, 20, 1000);
					resuleVos = getService().calcOnePersonTarget_P_Money(_billvo.getStringValue("id"), date);
					timer.cancel();
				} catch (Exception e) {
					resuleVos = null;
					timer.cancel();
					MessageBox.showException((SplashWindow) actionevent.getSource(), e);
					e.printStackTrace();
				}
			}
		}, false);
		if (resuleVos != null) {
			Pub_Templet_1VO tmo = null;
			try {
				HashVO[] vos = UIUtil.getHashVoArrayByDS(null, "select * from SAL_TARGET_CATALOG where id='" + _billvo.getStringValue("catalogid") + "'");
				if (wgflg && vos[0].getStringValue("name").equals("����ָ��")) {
					tmo = UIUtil.getPub_Templet_1VO("V_SAL_SCORE_POST_DEPT_WG");
				} else {
					tmo = UIUtil.getPub_Templet_1VO("V_SAL_SCORE_POST_DEPT_CODE1");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String factors[] = TBUtil.getTBUtil().split(_billvo.getStringValue("factors", ""), ";");
			String newItems[][] = new String[factors.length][6];
			for (int i = 0; i < factors.length; i++) {
				newItems[i][0] = factors[i];
				newItems[i][1] = factors[i];
				newItems[i][2] = "�ı���";
				newItems[i][3] = "170";
				newItems[i][4] = "400";
				newItems[i][5] = "Y";
			}
			tmo.appendNewItemVOs("", newItems, false);
			BillListPanel list = new BillListPanel(tmo);
			BillListDialog listDialog = new BillListDialog(_parent, _billvo.getStringValue("name"), list, 1000, 800, false);
			listDialog.getBilllistPanel().queryDataByHashVOs(resuleVos);
			listDialog.getBilllistPanel().setQuickQueryPanelVisiable(false);
			listDialog.setVisible(true);
		}
	}

	public void stateChanged(ChangeEvent changeevent) {
		if (tabPane.getSelectedIndex() == 0) {
			targetListPanel.QueryDataByCondition(null);
		} else {
			listPanel.refreshData();
		}
	}

	private SalaryServiceIfc services;

	private SalaryServiceIfc getService() {
		if (services == null) {
			try {
				services = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return services;
	}

	private String getDate(Container _parent) {
		String selectDate = "2013-08";
		try {
			RefDialog_Month chooseMonth = new RefDialog_Month(_parent, "��ѡ�����ϴ����ݵĿ����·�", null, null);
			chooseMonth.initialize();
			chooseMonth.setVisible(true);
			if (chooseMonth.getCloseType() != 1) {
				return null;
			}
			selectDate = chooseMonth.getReturnRefItemVO().getName();
			return selectDate;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "2013-08";
	}
	public TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil; //
		}
		tbUtil = new TBUtil(); //
		return tbUtil;
	}
}
