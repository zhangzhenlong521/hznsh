package cn.com.infostrategy.ui.mdata.cardcomp;

import javax.swing.JPanel;

import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * ���մ����Ϸ����Է�һ���Զ��尴ť���...
 * @author xch
 *
 */
public abstract class AbstractRefDialogCustBtnPanel extends JPanel {

	public AbstractRefDialog refDiaglog = null; //���մ���!
	public BillPanel billPanel = null; //���ĸ���嵯������!���翨Ƭ,�б��

	/**
	 * ��ʼ��ҳ��
	 */
	public abstract void initialize(); //

	public AbstractRefDialog getRefDiaglog() {
		return refDiaglog;
	}

	public void setRefDiaglog(AbstractRefDialog refDiaglog) {
		this.refDiaglog = refDiaglog;
	}

	public BillPanel getBillPanel() {
		return billPanel;
	}

	public void setBillPanel(BillPanel billPanel) {
		this.billPanel = billPanel;
	}
}
