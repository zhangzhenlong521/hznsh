package cn.com.infostrategy.ui.mdata.styletemplet.t06;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JSplitPane;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryEvent;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * ���ģ��6�ĳ�����!!!!ģ��6�����������б༭�޸ĵڶ��ű�
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_06 extends AbstractWorkPanel implements BillListSelectListener, ActionListener, BillListAfterQueryListener {

	protected BillListPanel parentBillListPanel = null; // ����,��ڱ�,���
	protected BillListPanel childBilllListPanel = null; // �ӱ�,������,�����,�ұ�
	private WLTButton btn_insert, btn_update, btn_delete, btn_list; //��,ɾ,��

	public abstract String getParentTempletCode(); //

	public abstract String getParentAssocField();

	public abstract String getChildTempletCode(); //

	public abstract String getChildAssocField();

	/**
	 *  Ĭ�������ҷָ�!
	 * @return
	 */
	public int getOrientation() {
		return JSplitPane.VERTICAL_SPLIT;
	}

	public boolean isShowsystembutton() {
		return true;
	}

	public String getCustomerpanel() {
		return null;
	}

	public void initialize() {
		try {
			parentBillListPanel = new BillListPanel(getParentTempletCode()); //����
			parentBillListPanel.setItemEditable(false); //
			parentBillListPanel.addBillListSelectListener(this); //
			parentBillListPanel.addBillListAfterQueryListener(this); //

			childBilllListPanel = new BillListPanel(getChildTempletCode()); //
			btn_insert = new WLTButton("����"); //
			btn_insert.addActionListener(this); //
			btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //�޸�
			btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //ɾ��
			btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //���
			childBilllListPanel.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_list }); //
			childBilllListPanel.repaintBillListButton(); //ˢ�°�ť

			JSplitPane splitPanel = new WLTSplitPane(getOrientation(), parentBillListPanel, childBilllListPanel);
			splitPanel.setDividerLocation(250);
			splitPanel.setOneTouchExpandable(true);

			this.setLayout(new BorderLayout()); //
			this.add(splitPanel, BorderLayout.CENTER); //

			afterInitialize(); //������ʼ��
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public void afterInitialize() throws Exception {
	}

	/**
	 * �б�ѡ��仯!!!
	 */
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO selVO = _event.getCurrSelectedVO(); //ѡ�õ�����!!
		if (selVO == null) {
			return;
		}
		String str_parentidValue = selVO.getStringValue(getParentAssocField()); //
		childBilllListPanel.QueryDataByCondition(getChildAssocField() + "='" + str_parentidValue + "'"); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_insert) {
			onInsert(); //
		}
	}

	public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
		childBilllListPanel.clearTable(); //
	}

	/**
	 * ��ȡ�ӱ��BillListPanel
	 * 
	 * @return BillListPanel
	 */
	public BillListPanel getChildBillListPanel() {
		return childBilllListPanel;
	}

	/**
	 * ��ȡ�����BillListPanel
	 * 
	 * @return BillListPanel
	 */
	public BillListPanel getParentBillListPanel() {
		return parentBillListPanel;
	}

	protected void onInsert() {
		BillVO billVO = parentBillListPanel.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.show(this, "��ѡ��[" + parentBillListPanel.getTempletVO().getTempletname() + "]��һ����¼���д˲���!"); //
			return; //
		}
		HashMap defaultValueMap = new HashMap(); //
		defaultValueMap.put(getChildAssocField(), billVO.getStringValue(getParentAssocField())); ////
		childBilllListPanel.doInsert(defaultValueMap); //���������!!
	}

}
