package cn.com.infostrategy.ui.sysapp.corpdept;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 对某个列表的数据根据seq字段进行排序
 * @author lcj
 *
 */
public class SeqListDialog extends BillDialog implements ActionListener {

	private BillListPanel billlistPanel = null;
	private String str_initSQL = null;
	private WLTButton btn_confirm, btn_cancel;
	private WLTButton btn_top,btn_bottom;
	private int closeType = -1;

	public SeqListDialog(Container _parent, String _title, Pub_Templet_1VO _templetVO, BillVO[] _billvos) {
		super(_parent, _title, 700, 500); //
		billlistPanel = new BillListPanel(_templetVO);
		btn_top = new WLTButton("置顶");
		btn_top = new WLTButton("置顶");
		billlistPanel.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP), WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN) });
		billlistPanel.repaintBillListButton();
		billlistPanel.addBillVOs(_billvos);
		billlistPanel.setItemVisible("seq", true);
		initialize(); //
	}

	public SeqListDialog(Container _parent, String _title, String _templetcode, int _width, int _height) {
		super(_parent, _title, _width, _height); //
		billlistPanel = new BillListPanel(_templetcode); //
		initialize(); //
	}

	public SeqListDialog(Container _parent, String _title, Pub_Templet_1VO _templetVO, String _initSQL, int _width, int _height) {
		super(_parent, _title, _width, _height); //
		this.str_initSQL = _initSQL;
		billlistPanel = new BillListPanel(_templetVO); //
		initialize(); //
	}

	private void initialize() {
		this.getContentPane().setLayout(new BorderLayout());
		if (str_initSQL != null) {
			billlistPanel.QueryDataByCondition(str_initSQL); //如果定义了初始化条件,则查询之
		}
		this.getContentPane().add(billlistPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	public void onConfirm() {
		try {
			billlistPanel.saveData(); //保存排序
			closeType = BillDialog.CONFIRM;
			this.dispose(); //
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	public void onCancel() {
		closeType = BillDialog.CANCEL;
		this.dispose();
	}

	public int getCloseType() {
		return closeType;
	}

	public BillListPanel getBilllistPanel() {
		return billlistPanel;
	}

}
