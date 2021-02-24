package cn.com.infostrategy.ui.mdata.styletemplet.t2a;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;

/**
 * ���ģ��11,�����ű�,��һ��һ��ϵ(��һ����¼��һ����¼),һ������,һ���ӱ�,��ȥʱ��ʾ�����б�,Ȼ��ѡ��һ����¼�����������ť�����ӱ�Ƭҳ��.
 * ������ɺ�ص�����ҳ��,���������б��ӱ��ǿ�Ƭ
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_2A extends AbstractStyleWorkPanel {

	private static final long serialVersionUID = -8841608749741379946L;

	private BillListPanel parentBillListPanel = null; //�����б����

	private BillCardPanel childBillCardPanel = null; //�ӱ�Ƭ���

	private CardLayout cardLayout = null; //

	private JPanel toftPanel = null; //װ��CardLayout�����..

	protected abstract String getParentTempletCode(); //ȡ������ģ�����

	protected abstract String getParentAssocField(); //ȡ����������ֶ�

	protected abstract String getChildTempletCode(); //ȡ���ӱ�ģ�����

	protected abstract String getChildAssocField(); //ȡ���ӱ�����ֶ�

	private int leveltype = WLTConstants.LEVELTYPE_LIST; //

	Pub_Templet_1VO parentTempletVO = null;
	Pub_Templet_1VO childTempletVO = null;

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
			toftPanel.add("list", getParentBillListPanel()); //
			toftPanel.add("card", getChildBillCardPanel()); //
			this.add(toftPanel, BorderLayout.CENTER);
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ��ʼ��ϵͳ��ť��.
	 */
	private void initSysBtnPanel() {
		hiddenAllSysButtons(); //�������а�ť!!!
		if (isCanInsert()) {
			getSysButton(BTN_INSERT).setVisible(true);
		}

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
	public BillCardPanel getChildBillCardPanel() {
		if (childBillCardPanel == null) {
			childBillCardPanel = new BillCardPanel(childTempletVO); //
		}
		return childBillCardPanel;
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
		if (getLeveltype() == WLTConstants.LEVELTYPE_LIST) { //������б�״̬
			return getParentBillListPanel().getSelectedBillVO();
		} else if (getLeveltype() == WLTConstants.LEVELTYPE_CARD) { //����ǿ�Ƭ״̬
			return getChildBillCardPanel().getBillVO(); //
		} else {
			return null;
		}
	}

	public void writeBackWFPrinstance(BillVO _billVO) {
		getChildBillCardPanel().setRealValueAt("wfprinstanceid", _billVO.getStringValue("wfprinstanceid")); //��ҳ���д���̺����ɵ�����
	}

	protected void onInsert() {
		try {
			int li_row = getParentBillListPanel().getSelectedRow();
			if (li_row < 0) {
				return;
			}

			BillVO billVO = getParentBillListPanel().getBillVO(li_row); ////
			String str_parentID = billVO.getStringValue(getParentAssocField()); ////

			//		String str_condition = getChildAssocField() + "='" + str_parentID + "'";
			//		String str_sql = "select 1 from " + getChildBillCardPanel().getTempletVO().getTablename() + " where " + str_condition;
			//		try {
			//			String[][] str_data = UIUtil.getStringArrayByDS(getChildBillCardPanel().getTempletVO().getDatasourcename(), str_sql);
			//			if (str_data != null && str_data.length > 0) {
			//				MessageBox.show(this, "�Ѿ��а󶨵����ݣ�������������!");
			//				return;
			//			}
			//		} catch (Exception e) {
			//			e.printStackTrace();
			//		} //

			getChildBillCardPanel().insertRow(); ////
			getChildBillCardPanel().setCompentObjectValue(getChildAssocField(), new StringItemVO(str_parentID)); //

			hiddenAllSysButtons(); //�������а�ť!!!
			getSysButton(BTN_SAVE).setVisible(true); //
			getSysButton(BTN_SAVE_RETURN).setVisible(true); //
			getSysButton(BTN_RETURN).setVisible(true); //

			if (isCanWorkFlowDeal()) {
				if (getChildBillCardPanel().containsItemKey("WFPRINSTANCEID")) {
					refreshWorkFlowPanel(getChildBillCardPanel().getRealValueAt("WFPRINSTANCEID")); //
				}
			}

			getChildBillCardPanel().setEditableByInsertInit(); //
			getSysBtnPanel().updateUI(); //

			switchToCard();
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	/**
	 * 
	 */
	protected void onDelete() {
		try {
			int li_row = getParentBillListPanel().getSelectedRow(); //
			BillVO billVO = getParentBillListPanel().getSelectedBillVO(); //
			if (billVO == null) {
				MessageBox.show(this, "Please select a order."); //
				return;
			}

			String str_tablename = billVO.getSaveTableName(); //
			String str_pkfieldname = billVO.getPkName(); //
			String str_pkvalue = billVO.getPkValue(); //

			String str_sql = "delete from " + str_tablename + " where " + str_pkfieldname + "='" + str_pkvalue + "'";
			UIUtil.executeUpdateByDS(null, str_sql); //
			getParentBillListPanel().removeRow(li_row); //
			MessageBox.show(this, "Delete Success!"); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	protected void onEdit() {
		int li_row = getParentBillListPanel().getSelectedRow();
		if (li_row < 0) {
			return;
		}

		BillVO billVO = getParentBillListPanel().getBillVO(li_row); ////
		String str_parentID = billVO.getStringValue(getParentAssocField()); ////
		String str_condition = getChildAssocField() + "='" + str_parentID + "'";
		getChildBillCardPanel().queryDataByCondition(str_condition); //
		getChildBillCardPanel().setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE); //
		getChildBillCardPanel().setEditableByEditInit(); //

		hiddenAllSysButtons(); //�������а�ť!!!
		getSysButton(BTN_SAVE).setVisible(true); //
		getSysButton(BTN_SAVE_RETURN).setVisible(true); //
		getSysButton(BTN_RETURN).setVisible(true); //
		getSysButton(BTN_PRINT).setVisible(true); //

		if (isCanWorkFlowDeal()) {
			if (getChildBillCardPanel().containsItemKey("WFPRINSTANCEID")) {
				refreshWorkFlowPanel(getChildBillCardPanel().getRealValueAt("WFPRINSTANCEID")); //
			}
		}

		getSysBtnPanel().updateUI(); //

		switchToCard();
	}

	protected void onSave() throws Exception {
		try {
			getChildBillCardPanel().updateData();
			MessageBox.show(this, "Save Data Success!"); //
		} catch (Exception e) {
			e.printStackTrace();
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
		getChildBillCardPanel().queryDataByCondition(str_condition); //
		getChildBillCardPanel().setEditState(WLTConstants.BILLDATAEDITSTATE_INIT); //

		hiddenAllSysButtons(); //�������а�ť!!!
		getSysButton(BTN_RETURN).setVisible(true); //
		getSysButton(BTN_PRINT).setVisible(true); //
		getSysBtnPanel().updateUI(); //

		switchToCard();
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
