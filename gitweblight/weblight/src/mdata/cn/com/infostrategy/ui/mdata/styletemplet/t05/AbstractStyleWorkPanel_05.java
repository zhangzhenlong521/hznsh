/**************************************************************************
 * $RCSfile: AbstractStyleWorkPanel_05.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:22:58 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t05;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardEditEvent;
import cn.com.infostrategy.ui.mdata.BillCardEditListener;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListEditEvent;
import cn.com.infostrategy.ui.mdata.BillListEditListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.styletemplet.AbstractStyleWorkPanel;

/**
 * ���ģ��5,�����һ����,�ұ���һ����,���������������ģ��2һ���ǲ���ʾ��ʽ
 * ����ģ��4������
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_05 extends AbstractStyleWorkPanel implements BillCardEditListener, BillListEditListener, BillTreeSelectListener {
	
	private BillTreePanel billTreePanel = null;
	private BillListPanel billListPanel = null; //���!!!
	private BillCardPanel billCardPanel = null; //���!!!

	private JPanel panel_allscreem = null;
	private CardLayout cardlayout = null;

	private IUIIntercept_05 uiIntercept = null; //ui��������

	//��������
	public abstract String getTreeTempeltCode(); //����ģ�����.		

	public abstract String getTreeAssocField(); //��������������ֶ���

	//�������
	public abstract String getTableTempletCode(); //���ģ�����.

	public abstract String getTableAssocField(); //����������������ֶ�

	/**
	 * ��ʼ��ҳ��
	 */
	public void initialize() {
		super.initialize(); //

		if (getUiinterceptor() != null && !getUiinterceptor().trim().equals("")) {
			try {
				uiIntercept = (IUIIntercept_05) Class.forName(getUiinterceptor().trim()).newInstance(); //
			} catch (Exception e) {
				e.printStackTrace(); //
			}
		}

		this.setLayout(new BorderLayout());
		Box box = Box.createVerticalBox();
		box.add(getBtnBarPanel());
		initSysBtnPanel();

		panel_allscreem = new JPanel();
		cardlayout = new CardLayout();
		panel_allscreem.setLayout(cardlayout);

		panel_allscreem.add(getBillListPanel(), "table"); //
		panel_allscreem.add(getBillCardPanel(), "card"); //

		JSplitPane splitPanel = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, getBillTreePanel(), panel_allscreem);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(box, BorderLayout.NORTH);
		panel.add(splitPanel, BorderLayout.CENTER);

		this.add(panel, BorderLayout.CENTER);
	}

	private void initSysBtnPanel() {
		hiddenAllSysButtons();
		if (isCanInsert()) {
			getSysButton(BTN_INSERT).setVisible(true); //
		}

		if (isCanDelete()) {
			getSysButton(BTN_DELETE).setVisible(true); //
		}

		if (isCanEdit()) {
			getSysButton(BTN_EDIT).setVisible(true); //
		}

		if (isCanInsert() || isCanDelete() || isCanEdit()) {
			getSysButton(BTN_SAVE).setVisible(true); //
		}

		getSysButton(BTN_LIST).setVisible(true); //�鿴
	}

	public void onBillCardValueChanged(BillCardEditEvent _evt) {

	}

	public void onBillListValueChanged(BillListEditEvent _evt) {

	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		BillVO billVO = _event.getCurrSelectedVO();
		if (billVO == null) {
			return;
		}
		String str_tree_value = ((StringItemVO) billVO.getObject(getTreeAssocField())).getStringValue(); //
		String str_condition = getTableAssocField() + "='" + str_tree_value + "'";
		getBillListPanel().QueryDataByCondition(str_condition); //
	}

	protected void onInsert() {
		try {
			BillVO billVO = getBillTreePanel().getSelectedVO(); //
			if (billVO == null) {
				return;
			}

			hiddenAllSysButtons();
			getSysButton(BTN_SAVE).setVisible(true); //����
			getSysButton(BTN_SAVE_RETURN).setVisible(true); //���淵��
			getSysButton(BTN_RETURN).setVisible(true); //����

			StringItemVO str_accovalue = (StringItemVO) billVO.getObject(getTreeAssocField()); //
			getBillCardPanel().insertRow(); //
			getBillCardPanel().setValueAt(getTableAssocField(), str_accovalue);
			getBillCardPanel().setEditableByInsertInit(); //
			switchToCard();
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	protected void onSave() throws Exception {
		try {
			getBillCardPanel().updateData();
		} catch (Exception e) {
			throw (e);
		} //
	}

	protected void onDelete() {

	};

	protected void onSaveReturn() {
		try {
			onSave();
			onReturn(); //
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ����..
	 */
	protected void onReturn() {
		initSysBtnPanel(); //
		switchToTable(); //
	}

	/**
	 * �鿴
	 */
	protected void onList() {

	}

	/**
	 * ��ȡBillListPanel..
	 * @return BillListPanel
	 */
	public BillListPanel getBillListPanel() {
		if (billListPanel == null) {
			billListPanel = new BillListPanel(getTableTempletCode());
			billListPanel.addBillListEditListener(this);
			billListPanel.getQuickQueryPanel().setVisible(false); //���ز�ѯ��
		}
		return billListPanel;
	}

	/**
	 * ��ȡBillListPanel..
	 * @return BillListPanel
	 */
	public BillCardPanel getBillCardPanel() {
		if (billCardPanel == null) {
			billCardPanel = new BillCardPanel(getTableTempletCode());
			billCardPanel.addBillCardEditListener(this); //
		}
		return billCardPanel;
	}

	public BillTreePanel getBillTreePanel() {
		if (billTreePanel == null) {
			billTreePanel = new BillTreePanel(getTreeTempeltCode());
			billTreePanel.addBillTreeSelectListener(this); //
			billTreePanel.queryDataByCondition("1=1"); //
		}
		return billTreePanel;
	}

	private void switchToCard() {
		cardlayout.show(panel_allscreem, "card"); //
	}

	private void switchToTable() {
		cardlayout.show(panel_allscreem, "table"); //
	}

	public boolean isCanDelete() {
		return true;
	}

	public boolean isCanEdit() {
		return false;
	}

	public boolean isCanInsert() {
		return true;
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
