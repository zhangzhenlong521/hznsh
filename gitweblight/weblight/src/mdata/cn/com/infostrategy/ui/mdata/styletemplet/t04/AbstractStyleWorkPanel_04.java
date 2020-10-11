/**************************************************************************
 * $RCSfile: AbstractStyleWorkPanel_04.java,v $  $Revision: 1.7 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.t04;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

/**
 * ���ģ��,�������,�ұ����б�
 * @author xch
 *
 */
public abstract class AbstractStyleWorkPanel_04 extends AbstractWorkPanel implements ActionListener, BillTreeSelectListener {

	private BillTreePanel billTreePanel = null; //����ģ��!
	private BillListPanel billListPanel = null; //���!!!

	private WLTButton btn_insert, btn_update, btn_delete; //��,ɾ,��
	private TBUtil tbUtil = new TBUtil(); //

	public abstract String getTreeTempeltCode(); //����ģ�����.

	public abstract String getTableTempletCode(); //���ģ�����.

	public abstract String getTreeAssocField(); //��������������ֶ���

	public abstract String getTableAssocField(); //����������������ֶ�

	public abstract String getCustAfterInitClass(); //���ʼ����!!

	public AbstractStyleWorkPanel_04() {
		super(); //
	}

	/**
	 * ���췽��!!!
	 * @param _parMap
	 */
	public AbstractStyleWorkPanel_04(HashMap _parMap) {
		super(_parMap); //
	}

	/**
	 * ��ʼ��ҳ��
	 */
	public void initialize() {
		billTreePanel = new BillTreePanel(getTreeTempeltCode()); //������,PUB_CORP_DEPT_1
		billTreePanel.setMoveUpDownBtnVisiable(false); //����!
		billTreePanel.queryDataByCondition(null); //
		billTreePanel.addBillTreeSelectListener(this); //ˢ���¼�����!!

		billListPanel = new BillListPanel(getTableTempletCode()); //���Ÿ����!CMP_DEPTDUTY_CODE1"
		btn_insert = new WLTButton("����"); //
		btn_insert.addActionListener(this); //

		btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //�޸�
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //ɾ��

		if (isCanEdit()) {
			billListPanel.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete }); //�������ð�ť!!!
			billListPanel.repaintBillListButton(); //
		}

		WLTSplitPane split = new WLTSplitPane(JSplitPane.HORIZONTAL_SPLIT, billTreePanel, billListPanel); //

		this.setLayout(new BorderLayout()); //
		this.add(split); //

		if (getCustAfterInitClass() != null) { //�����Ϊ��!!!
			try {
				IAfterInit_StyleWorkPanel_04 itcpt = (IAfterInit_StyleWorkPanel_04) (Class.forName(getCustAfterInitClass()).newInstance()); //
				itcpt.afterInitialize(this); //
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_insert) {
			onInsert(); //
		}
	}

	/**
	 * ����!!
	 */
	private void onInsert() {
		BillVO billVO = billTreePanel.getSelectedVO(); //
		if (billVO == null) {
			MessageBox.show(this, "��ѡ���������һ����¼���д˲���!"); //
			return;
		}
		HashMap defaultValueMap = new HashMap(); //
		defaultValueMap.put(getTableAssocField(), billVO.getStringValue(getTreeAssocField())); //
		billListPanel.doInsert(defaultValueMap); //
	}

	/**
	 * ����ѡ��仯!!
	 */
	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		billListPanel.clearTable(); //���������!!!
		BillVO billVO = _event.getCurrSelectedVO(); //
		if (billVO == null) {
			return;
		}
		String str_treepkvalue = billVO.getStringValue(getTreeAssocField()); //��������id.
		billListPanel.QueryDataByCondition(getTableAssocField() + "='" + tbUtil.getNullCondition(str_treepkvalue) + "'"); //
	}

	public BillTreePanel getBillTreePanel() {
		return billTreePanel;
	}

	public BillListPanel getBillListPanel() {
		return billListPanel;
	}

	//�Ƿ�ɱ༭!!!Ĭ����
	public boolean isCanEdit() {
		return true;
	}

}
