package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 首页配置
 * @author xch
 *
 */
public class MWFPanel_Pub_Desktop_New extends AbstractWorkPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private BillListPanel billListPanel = null;
	private WLTButton moveup, movedown;

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout());
		billListPanel = new BillListPanel("PUB_DESKTOP_NEW_CODE1"); //
		moveup = billListPanel.getBillListBtn("上移");  //
		movedown = billListPanel.getBillListBtn("下移");  //
		
		moveup.addActionListener(this); //
		movedown.addActionListener(this); //
		billListPanel.getQuickQueryPanel().setVisible(true);
		this.add(billListPanel, BorderLayout.CENTER);

	}
	
	public static void openMe(java.awt.Container _parent, String _tile, String _type) {
		MWFPanel_Pub_Desktop_New configPanel = new MWFPanel_Pub_Desktop_New(); //
		configPanel.initialize(); //
		JFrame frame = new JFrame("配置首页布局");
		frame.setSize(1000, 600); //
		frame.getContentPane().add(configPanel); //
		frame.setVisible(true);
		frame.toFront(); //
	}


	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == moveup) {
			onMoveup(); //
		} else if (_event.getSource() == movedown) {
			onMovedown(); //
		}
	}

	private void onMoveup() {
		if (billListPanel.moveUpRow()) {
			resetShowOrder();
		}
	}

	private void onMovedown() {
		if (billListPanel.moveDownRow()) {
			resetShowOrder(); //	
		}
	}

	private void resetShowOrder() {
		int li_rowcount = billListPanel.getRowCount();
		for (int i = 0; i < li_rowcount; i++) {
			if (billListPanel.getValueAt(i, "SHOWORDER") != null && Integer.parseInt("" + billListPanel.getValueAt(i, "SHOWORDER")) != (i + 1)) {
				billListPanel.setValueAt("" + (i + 1), i, "SHOWORDER"); //
			}
		}
	}

}
