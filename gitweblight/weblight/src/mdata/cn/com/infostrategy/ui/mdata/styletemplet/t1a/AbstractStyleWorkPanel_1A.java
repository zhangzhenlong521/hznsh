package cn.com.infostrategy.ui.mdata.styletemplet.t1a;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListEditListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;

/**
 * ����һ��ʵ��! ���������ҳǩ,��һ��ǩ�ǿ�Ƭ,�ڶ���ҳǩ���б�!!!�������ǿ�Ƭ״̬! Ȼ��������б�ҳǩ�в�ѯ!!
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_1A extends AbstractStyleWorkPanel implements BillCardEditListener, BillListEditListener {

	private static final long serialVersionUID = 8348548020808291175L;

	private BillCardPanel billCardPanel = null; //

	private BillListPanel billListPanel = null; //

	private IUIIntercept_1A uiIntercept = null; //

	protected abstract String getTempletCodeCard();

	protected abstract String getTempletCodeList();

	public void initialize() {
		super.initialize(); //
		/**
		 * ����UI��������
		 */
		if (getUiinterceptor() != null && !getUiinterceptor().trim().equals("")) {
			try {
				uiIntercept = (IUIIntercept_1A) Class.forName(getUiinterceptor().trim()).newInstance(); //
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.setLayout(new BorderLayout()); //

		JPanel jpanelFirst = new JPanel(new BorderLayout());
		jpanelFirst.add(getBillCardPanel(), BorderLayout.CENTER); //
		jpanelFirst.add(getBtnBarPanel(), BorderLayout.NORTH); //

		JPanel jpanelSecond = new JPanel(new BorderLayout());

		JPanel btnBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		jpanelSecond.add(btnBarPanel, BorderLayout.NORTH);
		jpanelSecond.add(getBillListPanel(), BorderLayout.CENTER);

		JTabbedPane jTabbedPane = new JTabbedPane();

		jTabbedPane.add("��ϸ", jpanelFirst);
		jTabbedPane.add("�б�", jpanelSecond);

		this.add(jTabbedPane, BorderLayout.CENTER); //
		initsysbuton();
		try {
			getBillCardPanel().insertRow(); //
			getBillCardPanel().setEditableByInsertInit(); //

		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	public BillCardPanel getBillCardPanel() {
		if (billCardPanel == null) {
			billCardPanel = new BillCardPanel(getTempletCodeCard()); //"ESS_leavewithdraw_CODE1"
			billCardPanel.addBillCardEditListener(this); //
		}
		return billCardPanel;
	}

	public BillListPanel getBillListPanel() {
		if (billListPanel == null) {
			billListPanel = new BillListPanel(getTempletCodeList()); //"ESS_leavewithdraw_CODE1"
			billListPanel.addBillListEditListener(this); //
		}
		return billListPanel;
	}

	/**
	 * ��ʼ����ť
	 */
	private void initsysbuton() {
		getSysButton(BTN_INSERT).setVisible(true); //
		getSysButton(BTN_SAVE).setVisible(true); //
		getSysButton(BTN_WORKFLOW_SUBMIT).setVisible(true); //��������ʱֻ���ύ��ť!!!
	}

	public void onInsert() {
		try {
			initsysbuton();
			getBillCardPanel().insertRow(); //
			getBillCardPanel().setEditableByInsertInit(); //
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	public void onSave() {
		if (billCardPanel.checkValidate()) {
			try {
				dealSave(); //
				MessageBox.show(this, UIUtil.getLanguage("����ɹ�!"));
			} catch (Exception e) {
				MessageBox.showWarn(this, e.getMessage());
				e.printStackTrace();
			} //
		}

	}

	public void dealSave() throws Exception {
		BillVO billVO = getBillCardPanel().getBillVO();
		if (getBillCardPanel().getEditState().equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
			if (uiIntercept != null) {
				uiIntercept.beforeInsert(getBillCardPanel(), billVO); //����ǰ����,�����л����޸�һ��BillVO
			}
			getBillCardPanel().setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
		} else if (getBillCardPanel().getEditState().equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
			if (uiIntercept != null) {
				uiIntercept.beforeUpdate(getBillCardPanel(), billVO); //
			}
		}
	}

	/**
	 * ȡ�ù����������BillVO
	 * @return
	 */
	public BillVO getWorkFlowDealBillVO() {
		return getBillCardPanel().getBillVO(); //
	}

	/**
	 * ��ҳ���д����ʵ��
	 * @param _prInstanceId
	 */
	public void writeBackWFPrinstance(BillVO _billVO) {
		getBillCardPanel().setRealValueAt("wfprinstanceid", _billVO.getStringValue("wfprinstanceid")); //��ҳ���д���̺����ɵ�����
	}

	public void onBillCardValueChanged(BillCardEditEvent _evt) {

	}

	public boolean isCanDelete() {
		return false;
	}

	public boolean isCanEdit() {
		return false;
	}

	public boolean isCanInsert() {
		return false;
	}

	public boolean isCanWorkFlowDeal() {
		return false;
	}

	public boolean isCanWorkFlowMonitor() {
		return false;
	}

	public boolean isShowsystembutton() {
		return true;
	}

}
