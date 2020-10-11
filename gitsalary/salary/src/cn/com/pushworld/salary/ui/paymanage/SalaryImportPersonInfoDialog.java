package cn.com.pushworld.salary.ui.paymanage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;

public class SalaryImportPersonInfoDialog extends BillDialog implements
		BillTreeSelectListener, ActionListener {
	private static final long serialVersionUID = 1L;
	private BillTreePanel corp_tree = null;
	protected BillListPanel bl = null;
	private WLTSplitPane wsp = null;
	protected BillVO accountvo = null;
	private WLTButton confimbtn, cancelbtn = null;
	protected BillListPanel parentlist = null;

	public SalaryImportPersonInfoDialog(BillListPanel _parent, BillVO _accountvo) {
		super(_parent, 850, 600);
		this.setTitle("增加人员");
		this.parentlist = _parent;
		this.accountvo = _accountvo;
		init();
	}

	public void init() {
		corp_tree = new BillTreePanel("PUB_CORP_DEPT_SELF");
		corp_tree.queryDataByCondition(" 1=1 ");
		corp_tree.addBillTreeSelectListener(this);
		bl = new BillListPanel("SAL_PERSONINFO_CODE1");
		bl.setRowNumberChecked(true);
		wsp = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, corp_tree, bl);
		this.add(wsp, BorderLayout.CENTER);
		this.add(getSouthPanel(), BorderLayout.SOUTH);
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		confimbtn = new WLTButton("添加");
		cancelbtn = new WLTButton("返回");
		confimbtn.addActionListener(this);
		cancelbtn.addActionListener(this);
		panel.add(confimbtn);
		panel.add(cancelbtn);
		return panel;
	}

	public void onBillTreeSelectChanged(BillTreeSelectionEvent event) {
		final BillVO[] allnodes = corp_tree.getSelectedChildPathBillVOs();
		if (allnodes != null && allnodes.length > 0) {
			new SplashWindow(bl, new AbstractAction() {
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent arg0) {
					try {
						List<String> allids = new ArrayList<String>();
						for (int i = 0; i < allnodes.length; i++) {
							allids.add(allnodes[i].getPkValue());
						}
						bl.QueryDataByCondition(" maindeptid in (" + UIUtil.getSubSQLFromTempSQLTableByIDs((String[]) allids.toArray(new String[0])) + ") ");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == confimbtn) {
			onConfirm();
		} else if (e.getSource() == cancelbtn) {
			onCancel();
		}
	}

	public void onConfirm() {
		BillVO[] vos = bl.getCheckedBillVOs();
		if (vos == null || vos.length <= 0) {
			MessageBox.show(bl, "请至少勾选一条人员信息进行此操作!");
			return;
		}
		
		BillVO[] selectvos = vos;
		try {
			HashMap map = UIUtil.getHashMapBySQLByDS(null, "select personinfoid, id from sal_account_personinfo where accountid=" + accountvo.getStringValue("id"));
			List<String> sqls = new ArrayList<String>();
			for (int i = 0 ; i < selectvos.length; i++) {
				if (map.containsKey(selectvos[i].getStringValue("id"))) {
					continue;
				}
				InsertSQLBuilder isb = new InsertSQLBuilder();
				isb.setTableName("sal_account_personinfo");
				isb.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_SAL_ACCOUNT_PERSONINFO"));
				isb.putFieldValue("accountid", accountvo.getStringValue("id"));
				isb.putFieldValue("personinfoid", selectvos[i].getStringValue("id"));
				sqls.add(isb.getSQL());
			}
			UIUtil.executeBatchByDS(null, sqls);
			parentlist.refreshData();
			MessageBox.show(bl, "操作成功!");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(bl, "增加人员失败!请再次尝试或与管理员联系!");
		}
	}

	public void onCancel() {
		this.dispose();
	}

}
