package cn.com.infostrategy.ui.mdata.styletemplet.t2b;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;

/**
 * ���ģ��13,�����ű�ʵ���������ӱ�,��ѡ������Ȼ����һ����¼�����ӱ�༭�������ӱ����б�ģʽ,��ͬʱֻ�ܿ���һ��,ֻ�༭�ӱ������ӱ����б�ģʽ
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_2B extends AbstractStyleWorkPanel {

	private static final long serialVersionUID = -8841608749741379946L;

	private BillListPanel parentBillListPanel = null; //�����б����

	private BillListPanel childBillListPanel = null; //�ӱ��б����

	private JPanel childcustomerpanel = null;// �ӱ���ɾ�����.

	private CardLayout cardLayout = null; //

	private JPanel toftPanel = null; //װ��CardLayout�����..

	private IUIIntercept_2B uiinterceptor = null; //ǰ��������

	protected abstract String getParentTempletCode(); //ȡ������ģ�����

	protected abstract String getParentAssocField(); //ȡ����������ֶ�

	protected abstract String getChildTempletCode(); //ȡ���ӱ�ģ�����

	protected abstract String getChildAssocField(); //ȡ���ӱ�����ֶ�

	private int leveltype = WLTConstants.LEVELTYPE_LIST; //

	Pub_Templet_1VO parentTempletVO = null;
	Pub_Templet_1VO childTempletVO = null;

	/**
	 * ��ʼ��ҳ��
	 */
	public void initialize() {
		super.initialize();

		try {
			Pub_Templet_1VO[] templetVOs = UIUtil.getPub_Templet_1VOs(new String[] { getParentTempletCode(), getChildTempletCode() }); //
			parentTempletVO = templetVOs[0];
			childTempletVO = templetVOs[1];

			this.setLayout(new BorderLayout()); //
			this.add(getBtnBarPanel(), BorderLayout.NORTH);
			initSysBtnPanel(); //

			cardLayout = new CardLayout(); //
			toftPanel = new JPanel(cardLayout); //
			toftPanel.add("list", getParentBillListPanel()); //����,�б���ʽ.
			toftPanel.add("card", getChildBillListPanel()); //�ӱ�,�б���ʽ.
			this.add(toftPanel, BorderLayout.CENTER); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ��ʼ��ϵͳ��ť��.
	 */
	private void initSysBtnPanel() {
		hiddenAllSysButtons(); //�������а�ť!!!

		if (isCanEdit()) {
			getSysButton(BTN_EDIT).setVisible(true);
		}

		if (isCanDelete()) {
			getSysButton(BTN_DELETE).setVisible(true);
		}

		getSysButton(BTN_LIST).setVisible(true); //�鿴

		if (isCanWorkFlowDeal()) {
			//getSysButton(BTN_WORKFLOW_SUBMIT).setVisible(true); //���̴���
		}

		if (isCanWorkFlowMonitor()) {
			getSysButton(BTN_WORKFLOW_MONITOR).setVisible(true); //���̼��
		}

		getSysBtnPanel().updateUI(); //
	}

	/**
	 * ȡ�������б����..
	 * @return
	 */
	public BillListPanel getParentBillListPanel() {
		if (parentBillListPanel == null) {
			parentBillListPanel = new BillListPanel(parentTempletVO); //
			parentBillListPanel.setItemEditable(false); //
			parentBillListPanel.getQuickQueryPanel().setVisible(true); //
		}
		return parentBillListPanel;
	}

	/**
	 * ȡ���ӱ�Ƭ���..
	 * @return
	 */
	public BillListPanel getChildBillListPanel() {
		if (childBillListPanel == null) {
			childBillListPanel = new BillListPanel(childTempletVO); //
			childBillListPanel.setCustomerNavigationJPanel(getChildCustomerJPanel()); //
			childBillListPanel.setCustomerNavigationJPanelVisible(true); //�����ص�!!
			childBillListPanel.setLoadedWorkPanel(this);
			childBillListPanel.setItemEditable(true); //
		}
		return childBillListPanel;
	}

	public void switchToCard() {
		cardLayout.show(toftPanel, "card"); //
		leveltype = WLTConstants.LEVELTYPE_CARD;
	}

	public void switchToList() {
		cardLayout.show(toftPanel, "list"); //
		leveltype = WLTConstants.LEVELTYPE_LIST;
	}

	/**
	 * ���ع��������������VO
	 */
	public BillVO getWorkFlowDealBillVO() {
		return getParentBillListPanel().getSelectedBillVO(); //
	}

	protected void onEdit() {
		int li_row = getParentBillListPanel().getSelectedRow();
		if (li_row < 0) {
			return;
		}

		BillVO billVO = getParentBillListPanel().getBillVO(li_row); ////
		String str_parentID = billVO.getStringValue(getParentAssocField()); ////
		String str_condition = getChildAssocField() + "='" + str_parentID + "'";

		getChildBillListPanel().QueryDataByCondition(str_condition); //ˢ������
		getChildBillListPanel().setItemEditable(true);

		hiddenAllSysButtons(); //�������а�ť!!!
		getSysButton(BTN_SAVE).setVisible(true); //
		getSysButton(BTN_SAVE_RETURN).setVisible(true); //
		getSysButton(BTN_RETURN).setVisible(true); //

		getSysBtnPanel().updateUI(); //
		switchToCard();
	}

	/**
	 * ���水ť����!!
	 */
	protected void onSave() {
		getChildBillListPanel().stopEditing();

		if (!getChildBillListPanel().checkValidate()) { //У��
			return;
		}

		//HashMap BillVOMap = new HashMap();
		try {
			BillVO[] insertvo = getChildBillListPanel().getInsertedBillVOs();
			BillVO[] updatevo = getChildBillListPanel().getUpdatedBillVOs();
			BillVO[] deletevo = getChildBillListPanel().getDeletedBillVOs();

			// ִ���ύǰ����.
			if (this.uiinterceptor != null) {
				try {
					uiinterceptor.dealBeforeCommit(getChildBillListPanel(), insertvo, deletevo, updatevo);
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageBox.show(this, ex.getMessage(), WLTConstants.MESSAGE_WARN);
					return;
				}
			}

			//����Զ���ύ���ݿ�!!!!
			//HashMap returnMap = getMetaService().style13_dealCommit(getChildBillListPanel().getDataSourceName(), getBsinterceptor(), insertvo, deletevo, updatevo); //����Զ�̷���!!

			getChildBillListPanel().clearDeleteBillVOs();
			for (int i = 0; i < getChildBillListPanel().getTable().getRowCount(); i++) {
				RowNumberItemVO itemvo = (RowNumberItemVO) getChildBillListPanel().getValueAt(i, "_RECORD_ROW_NUMBER");
				if (itemvo.getState().equals("UPDATE")) {
					if (getChildBillListPanel().containsItemKey("VERSION")) { //����а汾�ֶ�!!
						int version = new Integer(getChildBillListPanel().getValueAt(i, "VERSION").toString()).intValue() + 1;
						getChildBillListPanel().setValueAt(new Integer(version).toString(), i, "VERSION");
					}
				}
			}
			//
			//			BillVO[] returnInsertVOs = (BillVO[]) returnMap.get("INSERT"); //...
			//			BillVO[] returnDeleteVOs = (BillVO[]) returnMap.get("DELETE"); //...
			//			BillVO[] returnUpdateVOs = (BillVO[]) returnMap.get("UPDATE"); //...

			// ִ���ύ������.

			//getChildBillListPanel().stopEditing();
			getChildBillListPanel().setAllRowStatusAs("INIT");
			setInformation("Save Success"); //
			//MessageBox.show(this, WLTConstants.STRING_OPERATION_SUCCESS); //
		} catch (Exception e) {
			e.printStackTrace(); //
			MessageBox.showException(this, e); //
		}
	}

	protected void onSaveReturn() throws Exception {
		try {
			onSave();
			onReturn();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onReturn() {
		hiddenAllSysButtons(); //�������а�ť!!!
		initSysBtnPanel(); //
		switchToList();
	}

	protected void onList() {
		int li_row = getParentBillListPanel().getSelectedRow();
		if (li_row < 0) {
			return;
		}

		BillVO billVO = getParentBillListPanel().getBillVO(li_row); ////
		String str_parentID = billVO.getStringValue(getParentAssocField()); ////
		String str_condition = getChildAssocField() + "='" + str_parentID + "'";
		getChildBillListPanel().QueryDataByCondition(str_condition); //
		getChildBillListPanel().setItemEditable(false); //

		hiddenAllSysButtons(); //�������а�ť!!!
		getSysButton(BTN_RETURN).setVisible(true); //
		getSysBtnPanel().updateUI(); //
		switchToCard();
	}

	protected JPanel getChildCustomerJPanel() {
		if (childcustomerpanel != null)
			return childcustomerpanel;

		childcustomerpanel = new JPanel();
		FlowLayout layout = new FlowLayout(FlowLayout.LEFT);
		layout.setHgap(10);
		childcustomerpanel.setLayout(layout);

		JButton btn_insert = new JButton(UIUtil.getImage("insert.gif")); //
		JButton btn_delete = new JButton(UIUtil.getImage("delete.gif")); //
		JButton btn_refresh = new JButton(UIUtil.getImage("refresh.gif")); //

		btn_insert.setToolTipText("������¼");
		btn_delete.setToolTipText("ɾ����¼");
		btn_refresh.setToolTipText("ˢ��");

		btn_insert.setPreferredSize(new Dimension(18, 18));
		btn_delete.setPreferredSize(new Dimension(18, 18));
		btn_refresh.setPreferredSize(new Dimension(18, 18));

		btn_insert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onChildInsert();
			}
		});

		btn_delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onChildDelete();
			}
		});

		btn_refresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onChildRefresh();
			}
		});

		childcustomerpanel.add(btn_insert);
		childcustomerpanel.add(btn_delete);
		childcustomerpanel.add(btn_refresh);

		return childcustomerpanel;
	}

	protected void onChildInsert() {
		String str_parentpk = getParentBillListPanel().getRealValueAtModel(getParentBillListPanel().getSelectedRow(), getParentAssocField());
		int li_row = getChildBillListPanel().newRow(); //
		getChildBillListPanel().setValueAt(str_parentpk, li_row, getChildAssocField());

		//		// ִ���ӱ����� ������;
		//		if (uiinterceptor != null) {
		//			try {
		//				uiinterceptor.actionAfterInsert_child(getChildBillListPanel(), li_row);
		//			} catch (Exception e1) {
		//				MessageBox.show(this, e1.getMessage(), WLTConstants.MESSAGE_WARN);
		//				e1.printStackTrace();
		//				return;
		//			}
		//		}
	}

	protected void onChildDelete() {
		int li_selrow = getChildBillListPanel().getSelectedRow();
		if (li_selrow < 0) {
			return;
		}

		//		// ִ���ӱ�ɾ��ǰ����;
		//		if (uiinterceptor != null) {
		//			try {
		//				uiinterceptor.actionBeforeDelete_child(child_BillListPanel, li_selrow);
		//			} catch (Exception e1) {
		//				MessageBox.show(this, e1.getMessage(), WLTConstants.MESSAGE_WARN);
		//				e1.printStackTrace();
		//				return;
		//			}
		//		}
		getChildBillListPanel().removeRow();
	}

	public void onChildRefresh() {
		String str_parentpk = getParentBillListPanel().getRealValueAtModel(getParentBillListPanel().getSelectedRow(), getParentAssocField());
		getChildBillListPanel().QueryDataByCondition(getChildAssocField() + "='" + str_parentpk + "'"); //ˢ���ӱ�����!
	}

	/**
	 * �Ƿ���������
	 */
	public boolean isCanInsert() {
		return true;
	}

	/**
	 * �Ƿ�����ɾ��
	 */
	public boolean isCanDelete() {
		return true;
	}

	/**
	 * �Ƿ�����༭����
	 */
	public boolean isCanEdit() {
		return true;
	}

	/**
	 * �Ƿ���ʾϵͳ��Ť��
	 */
	public boolean isShowsystembutton() {
		return true;
	}

	/**
	 * �Ƿ���ʾ��������ť��
	 */
	public boolean isCanWorkFlowDeal() {
		return false;
	}

	public boolean isCanWorkFlowMonitor() {
		return false;
	}

	public int getLeveltype() {
		return leveltype;
	}

}
