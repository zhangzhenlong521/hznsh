package cn.com.infostrategy.ui.sysapp.login.index;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * 首页板块权限配置
 */
public class MWFPanel_Pub_Desktop_Right extends AbstractWorkPanel implements ActionListener, BillListSelectListener {

	private static final long serialVersionUID = 1L;
	private BillListPanel billListPanel_Desk = null;
	private BillListPanel billListPanel_Role = null;
	private WLTSplitPane sp = null;
	private WLTButton add, remove = null;

	public void initialize() {
		this.setLayout(new BorderLayout());
		billListPanel_Desk = new BillListPanel("PUB_DESKTOP_NEW_CODE1");
		billListPanel_Desk.addBillListSelectListener(this);
		billListPanel_Desk.getQuickQueryPanel().setVisible(true);
		billListPanel_Role = new BillListPanel("PUB_ROLE_1");
		add = WLTButton.createButtonByType(WLTButton.COMM, "绑定角色");
		remove = WLTButton.createButtonByType(WLTButton.COMM, "移除角色");
		add.addActionListener(this);
		remove.addActionListener(this);
		billListPanel_Role.addBatchBillListButton(new WLTButton[] { add, remove });
		billListPanel_Role.repaintBillListButton();
		sp = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billListPanel_Desk, billListPanel_Role);
		sp.setDividerLocation(400);
		this.add(sp, BorderLayout.CENTER);

	}

	public static void openMe(java.awt.Container _parent, String _tile, String _type) {
		MWFPanel_Pub_Desktop_Right configPanel = new MWFPanel_Pub_Desktop_Right();
		configPanel.initialize();
		JFrame frame = new JFrame("配置首页板块权限");
		frame.setSize(1000, 600);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().add(configPanel);
		frame.setVisible(true);
		frame.toFront();
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == add) {
			onDealAddRole();
		} else if (_event.getSource() == remove) {
			onDealRemoveRole();
		}
	}

	private void onDealAddRole() {
		BillVO desltpvo = billListPanel_Desk.getSelectedBillVO();
		if (desltpvo != null) {
			BillListDialog bld = new BillListDialog(billListPanel_Role, "绑定角色", "PUB_ROLE_1");
			bld.getBilllistPanel().QueryDataByCondition(" 1=1 ");
			bld.setVisible(true);
			if (bld.getCloseType() == BillDialog.CONFIRM) {
				BillVO[] vos = bld.getReturnBillVOs();
				try {
					HashMap map = UIUtil.getHashMapBySQLByDS(null, "select roleid, desktopid from pub_desktop_role where desktopid='" + desltpvo.getPkValue() + "'");
					List sql = new ArrayList();
					for (int i = 0; i < vos.length; i++) {
						if (!map.containsKey(vos[i].getPkValue())) {
							InsertSQLBuilder isb = new InsertSQLBuilder();
							isb.setTableName("pub_desktop_role");
							isb.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_PUB_DESKTOP_ROLE"));
							isb.putFieldValue("roleid", vos[i].getPkValue());
							isb.putFieldValue("desktopid", desltpvo.getPkValue());
							sql.add(isb.getSQL());
						}
					}
					UIUtil.executeBatchByDS(null, sql);
					billListPanel_Role.refreshData();
					MessageBox.show(billListPanel_Role, "操作成功!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			MessageBox.showSelectOne(billListPanel_Desk);
		}
	}

	private void onDealRemoveRole() {
		BillVO[] selectvos = billListPanel_Role.getSelectedBillVOs();
		BillVO desltpvo = billListPanel_Desk.getSelectedBillVO();
		if (desltpvo != null && selectvos != null && selectvos.length > 0) {
			StringBuffer sb = new StringBuffer("delete pub_desktop_role where desktopid='" + desltpvo.getPkValue() + "' and roleid in (");
			for (int i = 0; i < selectvos.length; i++) {
				if (i == 0) {
					sb.append(selectvos[i].getPkValue());
				} else {
					sb.append("," + selectvos[i].getPkValue());
				}
			}
			sb.append(")");
			try {
				UIUtil.executeBatchByDS(null, new String[] { sb.toString() });
				billListPanel_Role.removeSelectedRows();
				MessageBox.show(billListPanel_Role, "操作成功!");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO vo = _event.getCurrSelectedVO();
		if (vo != null) {
			billListPanel_Role.QueryDataByCondition(" id in (select roleid from pub_desktop_role where desktopid='" + vo.getPkValue() + "') ");
		}
	}
}
