package cn.com.infostrategy.ui.mdata.cardcomp;

import javax.swing.JPanel;

import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 参照窗口上方可以放一个自定义按钮面板...
 * @author xch
 *
 */
public abstract class AbstractRefDialogCustBtnPanel extends JPanel {

	public AbstractRefDialog refDiaglog = null; //参照窗口!
	public BillPanel billPanel = null; //从哪个面板弹出来的!比如卡片,列表等

	/**
	 * 初始化页面
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
