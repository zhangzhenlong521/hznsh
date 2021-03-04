package cn.com.pushworld.salary.ui.person.p030;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

/**
 * ѡ��������
 * @author Administrator
 *
 */
public class ChooseScorerRefDialog extends AbstractRefDialog implements ActionListener, BillListSelectListener, ChangeListener, BillTreeSelectListener {

	private WLTButton btnConfirm = null; //ȷ����ť
	private WLTButton btnCancel = null; // ȡ����ť
	private WLTButton btn_addShopCarUser, btn_addAllShopCarUser, btn_delShopCarUser, btn_delAllShopCarUser;
	private BillCardPanel billCard = null;
	private BillListPanel checktype_list, user_list, selectuser_list, corp_tree_user_list  = null;
	private BillTreePanel corp_tree = null;
	private RefItemVO refItemVO = null;
	private WLTSplitPane topspit, topspit1, mainspit = null;
	private WLTTabbedPane toptab = null;
	private TBUtil tb = new TBUtil();
	
	public ChooseScorerRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
		this.billCard = (BillCardPanel) panel;
		this.refItemVO = refItemVO;
	}
	
	public RefItemVO getReturnRefItemVO() {
		return refItemVO;
	}

	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout());
		this.setSize(800, 600);
		btnConfirm = new WLTButton("ȷ��");
		btnCancel = new WLTButton("ȡ��");
		btnConfirm.addActionListener(this);
		btnCancel.addActionListener(this);		
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(btnConfirm);
		southPanel.add(btnCancel);
		checktype_list = new BillListPanel("SAL_PERSON_CHECK_TYPE_CODE2");
		checktype_list.addBillListSelectListener(this);
		user_list = new BillListPanel("PUB_USER_SALARY");
		user_list.setRowNumberChecked(true);
		selectuser_list = new BillListPanel("PUB_USER_SALARY");
		selectuser_list.setTitleLabelText("��ѡ����Ա");
		selectuser_list.setRowNumberChecked(true);
		btn_addShopCarUser = new WLTButton("���", "office_059.gif");
		btn_delShopCarUser = new WLTButton("ɾ��", "office_081.gif");
		btn_addAllShopCarUser = new WLTButton("ȫ�����", "office_160.gif");
		btn_delAllShopCarUser = new WLTButton("ȫ��ɾ��", "office_125.gif");
		btn_addShopCarUser.setToolTipText("���Ϸ�ѡ�е���Ա��ӽ���");
		btn_delShopCarUser.setToolTipText("���·���ѡ�����Աɾ����");
		btn_addShopCarUser.addActionListener(this);
		btn_delShopCarUser.addActionListener(this);
		btn_addAllShopCarUser.addActionListener(this);
		btn_delAllShopCarUser.addActionListener(this);
		selectuser_list.addBatchBillListButton(new WLTButton[] { btn_addShopCarUser, btn_delShopCarUser, btn_addAllShopCarUser, btn_delAllShopCarUser });
		selectuser_list.repaintBillListButton();
		if(refItemVO != null && refItemVO.getId() != null) {
			try {
				selectuser_list.QueryDataByCondition("id in (" + tb.getInCondition(refItemVO.getId()) + ")");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		topspit = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, checktype_list, user_list);
		topspit.setDividerLocation(220);
		toptab = new WLTTabbedPane();
		toptab.addTab("�����˶������ѡ��", topspit);
		toptab.addTab("������ѡ��", new JPanel(new BorderLayout()));
		toptab.addChangeListener(this);
		mainspit = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, toptab, selectuser_list);
		mainspit.setDividerLocation(320);
		this.getContentPane().add(mainspit, BorderLayout.CENTER);
		this.getContentPane().add(southPanel, BorderLayout.SOUTH);
	}
	
	private WLTSplitPane getSecondPanel() {
		corp_tree_user_list = new BillListPanel("PUB_USER_SALARY");
		corp_tree_user_list.setRowNumberChecked(true);
		corp_tree = new BillTreePanel("PUB_CORP_DEPT_CODE1");
		corp_tree.queryDataByCondition("1=1");
		corp_tree.addBillTreeSelectListener(this);
		topspit1 = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, corp_tree, corp_tree_user_list);
		return topspit1;
	}


	public void actionPerformed(ActionEvent e) {
		if (btnConfirm == e.getSource()) {
			onConfirm(); // ���ȷ��
		} else if (btnCancel == e.getSource()) {
			onCancel(); // ���ȡ��
		} else if (btn_addShopCarUser == e.getSource()) {
			onAddShopCarUser();
		} else if (btn_delShopCarUser == e.getSource()) {
			onDelShopCarUser();
		} else if (btn_addAllShopCarUser == e.getSource()) {
			onAddAllShopCarUser();
		} else if (btn_delAllShopCarUser == e.getSource()) {
			onDelAllShopCarUser();
		}
	}
	
	private void onAddShopCarUser() {
		BillVO[] billVOs = null;
		if (toptab.getSelectedIndex() == 1) {
			billVOs = corp_tree_user_list.getCheckedBillVOs();
		} else {
			billVOs = user_list.getCheckedBillVOs();
		}
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "����Ϸ���Ա�б��й�ѡһ��������Ա���д˲���!");
			return;
		}
		onAddShopCarUserByBillVOs(billVOs);
	}

	/**
	 * ȫ�����
	 */
	private void onAddAllShopCarUser() {
		BillVO[] billVOs = null;
		if (toptab.getSelectedIndex() == 1) {
			billVOs = corp_tree_user_list.getAllBillVOs();
		} else {
			billVOs = user_list.getAllBillVOs();
		}
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "û�п�����ӵ���Ա,�޷����д˲���!");
			return;
		}
		onAddShopCarUserByBillVOs(billVOs);
	}

	private void onAddShopCarUserByBillVOs(BillVO[] _billVOs) {
		HashSet hst = new HashSet();
		BillVO[] billVOs_shopCardUser = selectuser_list.getAllBillVOs();
		if (billVOs_shopCardUser != null && billVOs_shopCardUser.length > 0) {
			for (int i = 0; i < billVOs_shopCardUser.length; i++) {
				String str_userid = billVOs_shopCardUser[i].getStringValue("id");
				if (str_userid != null) {
					hst.add(str_userid);
				}
			}
		}
		List vos = new ArrayList();
		for (int i = 0; i < _billVOs.length; i++) {
			String str_userid = _billVOs[i].getStringValue("id");
			if (!hst.contains(str_userid)) {
				vos.add(_billVOs[i]);
			}
		}
		selectuser_list.addBillVOs((BillVO[])vos.toArray(new BillVO[0]));
	}

	private void onDelShopCarUser() {
		BillVO[] billVOs = selectuser_list.getCheckedBillVOs();
		if (billVOs == null || billVOs.length <= 0) {
			MessageBox.show(this, "�����ѡ����Ա�б��й�ѡһ��������Ա���д˲���!");
			return;
		}
		if (!MessageBox.confirm(this, "��ȷ��Ҫɾ��ѡ�е���Ա��?")) {
			return;
		}
		selectuser_list.removeSelectedRows();
	}

	private void onDelAllShopCarUser() {
		int li_count = selectuser_list.getRowCount();
		if (li_count <= 0) {
		} else {
			if (!MessageBox.confirm(this, "��ȷ��Ҫɾ��������ѡ�����Աô?")) {
				return;
			}
		}
		selectuser_list.clearTable();
	}

	
	
	/**
	 * ȡ��
	 */
	private void onCancel() {
		this.setCloseType(BillDialog.CANCEL);
		this.dispose();
	}

	/**
	 * ȷ��
	 */
	private void onConfirm() {
		BillVO[] selVOS = selectuser_list.getAllBillVOs();
		if (selVOS == null || selVOS.length == 0) {
			MessageBox.show(this, "��ѡ��һ����Ա!");
			return;
		}
		StringBuilder userids = new StringBuilder(";");
		StringBuilder useridnames = new StringBuilder("");
		for (int i = 0; i < selVOS.length; i++) {
			userids.append(selVOS[i].getStringValue("id") + ";");
			useridnames.append(selVOS[i].getStringValue("name") + ";");
		}
		refItemVO = new RefItemVO(userids.toString(), "", useridnames.toString());
		this.setCloseType(BillDialog.CONFIRM);
		this.dispose();
	}

	public void onBillListSelectChanged(BillListSelectionEvent event) {
		String stationkinds = event.getBillListPanel().getSelectedBillVO().getRealValue("stationkinds");
		user_list.QueryDataByCondition("id in (select id from v_sal_personinfo where stationkind in (" + tb.getInCondition(stationkinds) + ")) ");
	}

	public void stateChanged(ChangeEvent arg0) {
		if (topspit1 == null && toptab.getSelectedIndex() == 1) {
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					toptab.getComponentAt(1).add(getSecondPanel(), BorderLayout.CENTER);
					toptab.getComponentAt(1).updateUI();
				}
			});
		}
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent event) {
		BillVO billVO_tree = corp_tree.getSelectedVO();
		if (billVO_tree != null) {
			corp_tree_user_list.QueryDataByCondition(" id in (select userid from pub_user_post where userdept  = '" + billVO_tree.getStringValue("id") + "')");
		}
	}

}
