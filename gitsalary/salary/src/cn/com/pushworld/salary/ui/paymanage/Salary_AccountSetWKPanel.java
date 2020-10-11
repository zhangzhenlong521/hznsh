package cn.com.pushworld.salary.ui.paymanage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.ImageIcon;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.DeleteSQLBuilder;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListButtonActinoListener;
import cn.com.infostrategy.ui.mdata.BillListButtonClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * 帐套管理即工资表需要哪些因子 工资条管理
 * 
 * @author Administrator
 * 
 */
public class Salary_AccountSetWKPanel extends AbstractWorkPanel implements BillListButtonActinoListener, ActionListener, BillListSelectListener {
	private static final long serialVersionUID = 1L;
	private BillListPanel account_list = null;
	private BillListPanel account_fa_list = null;
	private WLTSplitPane mainsp = null;
	private WLTButton addAccount, editAccount, delAccount, copyAccount = null;
	private WLTButton importFac, moveOutFac, seqUpMove, seqDownMove, saveViewName = null;

	private String editedAccountid = null;

	public Salary_AccountSetWKPanel() {

	}

	public Salary_AccountSetWKPanel(String _editedAccountid) {
		this.editedAccountid = _editedAccountid;
	}

	public void initialize() {
		account_fa_list = new BillListPanel("SAL_ACCOUNT_FACTOR_CODE1");
		account_fa_list.setRowNumberChecked(true);
		account_list = new BillListPanel("SAL_ACCOUNT_SET_CODE1");
		account_list.addBillListSelectListener(this);
		account_list.addBillListButtonActinoListener(this);
		if (editedAccountid == null) {
			mainsp = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, account_list, account_fa_list);
			mainsp.setDividerLocation(500);
			this.add(mainsp, BorderLayout.CENTER);
		} else {
			try {
				account_list.QueryDataByCondition("id=" + editedAccountid);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (account_list.getAllBillVOs() == null || account_list.getAllBillVOs().length <= 0) {
			} else {
				account_list.setSelectedRow(0);
			}
			this.add(account_fa_list, BorderLayout.CENTER);
		}
		addBillButton();
	}

	public BillListPanel getAccount_fa_list() {
		return account_fa_list;
	}

	public void addBillButton() {

		addAccount = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		editAccount = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		delAccount = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		copyAccount = new WLTButton("复制", "office_145.gif");
		copyAccount.addActionListener(this);
		account_list.addBatchBillListButton(new WLTButton[] { addAccount, editAccount, delAccount, copyAccount });
		account_list.repaintBillListButton();
		importFac = new WLTButton("增加项目", "add.gif");
		moveOutFac = new WLTButton("移除项目", "del.gif");

		//上移， 下移
		ImageIcon iconDown = UIUtil.getImage("down1.gif");
		TBUtil tbUtil = new TBUtil();
		ImageIcon iconUp = new ImageIcon(tbUtil.getImageRotate(iconDown.getImage(), 180)); //转180度!		
		seqUpMove = new WLTButton("");
		seqUpMove.setIcon(iconUp);
		seqUpMove.setToolTipText("项目顺序上移");
		seqDownMove = new WLTButton("");
		seqDownMove.setIcon(iconDown);
		seqDownMove.setToolTipText("项目顺序下移");

		saveViewName = new WLTButton("保存", "zt_068.gif");
		saveViewName.setToolTipText("保存显示名的修改");
		account_fa_list.addBatchBillListButton(new WLTButton[] { importFac, moveOutFac, seqUpMove, seqDownMove, saveViewName });
		account_fa_list.repaintBillListButton();
		seqDownMove.addActionListener(this);
		importFac.addActionListener(this);
		moveOutFac.addActionListener(this);
		seqUpMove.addActionListener(this);
		saveViewName.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == importFac) {
			onImportF();
		} else if (e.getSource() == moveOutFac) {
			onMoveOutF();
		} else if (e.getSource() == seqUpMove) {
			onSeqUpMoveF();
		} else if (e.getSource() == seqDownMove) {
			onSeqDownMoveF();
		} else if (e.getSource() == saveViewName) {
			onSaveViewName();
		} else if (e.getSource() == copyAccount) {
			onCopyA();
		}
	}

	/**
	 * 复制一个帐套 Gwang 2013-09-07
	 */
	private void onCopyA() {
		BillVO vo = account_list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(account_list, "请选择一个工资帐套来进行此操作!");
			return;
		}

		String fromAccountID = vo.getStringValue("id");
		ArrayList<String> sqlList = new ArrayList<String>();
		InsertSQLBuilder isb = new InsertSQLBuilder();
		isb.setTableName("sal_account_set");
		try {

			//主表
			String toAccountID = UIUtil.getSequenceNextValByDS(null, "S_SAL_ACCOUNT_SET");
			isb.putFieldValue("id", toAccountID);
			isb.putFieldValue("code", vo.getStringValue("code") + " - 副本");
			isb.putFieldValue("name", vo.getStringValue("name") + " - 副本");
			isb.putFieldValue("createdate", UIUtil.getCurrDate());
			isb.putFieldValue("blcorp", vo.getStringValue("blcorp"));
			isb.putFieldValue("createuser", vo.getStringValue("createuser"));
			sqlList.add(isb.getSQL());

			//子表
			sqlList.addAll(this.getCopyfactorSQL(fromAccountID, toAccountID));

			UIUtil.executeBatchByDS(null, sqlList);
			account_list.refreshData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//取得子表sql
	private ArrayList<String> getCopyfactorSQL(String fromAccountID, String toAccountID) {
		ArrayList<String> sqlList = new ArrayList<String>();
		String sql = "select * from sal_account_factor where accountid = " + fromAccountID;
		InsertSQLBuilder isb = new InsertSQLBuilder();
		isb.setTableName("sal_account_factor");
		try {
			HashVO[] vos = UIUtil.getHashVoArrayByDS(null, sql);
			for (HashVO vo : vos) {
				isb.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_SAL_ACCOUNT_FACTOR"));
				isb.putFieldValue("accountid", toAccountID);
				isb.putFieldValue("factorid", vo.getStringValue("factorid"));
				isb.putFieldValue("seq", vo.getStringValue("seq"));
				isb.putFieldValue("viewname", vo.getStringValue("viewname"));
				sqlList.add(isb.getSQL());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sqlList;
	}

	public void onImportF() {
		BillVO vo = account_list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(account_fa_list, "请选择一个工资帐套来进行此操作!");
			return;
		}
		BillListDialog bld = new BillListDialog(account_fa_list, "选择工资单项目", "SAL_FACTOR_DEF_CODE1", 800, 600) {
			public void onConfirm() {
				try {
					if (billlistPanel.getCheckedBillVOs() == null || billlistPanel.getCheckedBillVOs().length <= 0) {
						MessageBox.show(account_fa_list, "请至少勾选一条记录来进行此操作!");
						return;
					}
					this.setCloseType(BillDialog.CONFIRM);
					this.dispose();
				} catch (Exception e) {
					MessageBox.showException(account_fa_list, e);
				}
			}
		};
		bld.getBilllistPanel().setRowNumberChecked(true);
		bld.setVisible(true);
		if (bld.getCloseType() != BillDialog.CONFIRM) {
			return;
		}
		BillVO[] selectvos = bld.getBilllistPanel().getCheckedBillVOs();
		try {
			HashMap map = UIUtil.getHashMapBySQLByDS(null, "select factorid, id from sal_account_factor where accountid=" + vo.getStringValue("id"));
			List<String> sqls = new ArrayList<String>();
			//			DeleteSQLBuilder dsb = new DeleteSQLBuilder();
			//			dsb.setTableName("sal_account_factor");
			//			dsb.setWhereCondition("accountid=" + vo.getStringValue("id"));
			//			sqls.add(dsb.getSQL());
			String maxseq = UIUtil.getStringValueByDS(null, "select max(seq) from sal_account_factor where accountid=" + vo.getStringValue("id"));
			if (maxseq == null || "".equals(maxseq)) {
				maxseq = "0";
			}
			int j = 1;
			for (int i = 0; i < selectvos.length; i++) {
				if (map.containsKey(selectvos[i].getStringValue("id"))) {
					continue;
				}
				InsertSQLBuilder isb = new InsertSQLBuilder();
				isb.setTableName("sal_account_factor");
				isb.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_SAL_ACCOUNT_FACTOR"));
				isb.putFieldValue("accountid", vo.getStringValue("id"));
				isb.putFieldValue("factorid", selectvos[i].getStringValue("id"));
				isb.putFieldValue("viewname", selectvos[i].getStringValue("name"));
				isb.putFieldValue("seq", Integer.parseInt(maxseq) + j);
				j = j + 1;
				sqls.add(isb.getSQL());
			}
			UIUtil.executeBatchByDS(null, sqls);
			MessageBox.show(account_fa_list, "操作成功!");
			account_fa_list.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(account_fa_list, "新增项目失败!请再次尝试或与管理员联系!");
		}
	}

	public void onMoveOutF() {
		BillVO[] vos_ = account_fa_list.getCheckedBillVOs();
		if (vos_ == null || vos_.length <= 0) {
			MessageBox.show(account_fa_list, "请至少勾选一条项目记录来进行此操作!");
			return;
		}
		String accountid = vos_[0].getStringValue("accountid");
		List<String> delSql = new ArrayList<String>();
		for (int i = 0; i < vos_.length; i++) {
			DeleteSQLBuilder dsb = new DeleteSQLBuilder("sal_account_factor");
			dsb.setWhereCondition("id=" + vos_[i].getPkValue());
			delSql.add(dsb.getSQL());
		}
		try {
			UIUtil.executeBatchByDS(null, delSql);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(account_fa_list, "删除项目失败,请再次尝试或与系统管理员联系!");
			return;
		}
		try {
			HashVO[] vos = UIUtil.getHashVoArrayByDS(null, "select * from sal_account_factor where accountid=" + accountid + " order by seq ");
			if (vos != null && vos.length > 0) {
				List<String> sqls = new ArrayList<String>();
				for (int i = 0; i < vos.length; i++) {
					UpdateSQLBuilder usb = new UpdateSQLBuilder();
					usb.setTableName("sal_account_factor");
					usb.putFieldValue("seq", i + 1);
					usb.setWhereCondition("id=" + vos[i].getStringValue("id"));
					sqls.add(usb.getSQL());
				}
				UIUtil.executeBatchByDS(null, sqls);
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(account_fa_list, "重置项目顺序失败!请手动调整顺序!");
		}
		MessageBox.show(account_fa_list, "操作成功!");
		account_fa_list.refreshData();
	}

	public void onSeqUpMoveF() {
		if (checkChecked(account_fa_list)) {
			moveUpRow();
			try {
				resetSeqByUI();
			} catch (Exception e) {
				MessageBox.show(account_fa_list, "界面操作成功但是数据库保存失败,请再次尝试或与系统管理员联系!");
				return;
			}
		}
	}

	public void onSeqDownMoveF() {
		if (checkChecked(account_fa_list)) {
			moveDownRow();
			try {
				resetSeqByUI();
			} catch (Exception e) {
				MessageBox.show(account_fa_list, "界面操作成功但是数据库保存失败,请再次尝试或与系统管理员联系!");
				return;
			}
		}
	}

	public void onSaveViewName() {
		if (account_fa_list.checkValidate()) {
			if (account_fa_list.saveData()) {
				MessageBox.show(account_fa_list, "保存成功!");
			}
		}
	}

	public void resetSeqByUI() throws Exception {
		BillVO[] vos = account_fa_list.getAllBillVOs();
		if (vos != null && vos.length > 0) {
			List<String> sqls = new ArrayList<String>();
			for (int i = 0; i < vos.length; i++) {
				UpdateSQLBuilder usb = new UpdateSQLBuilder();
				usb.setTableName("sal_account_factor");
				usb.putFieldValue("seq", i + 1);
				usb.setWhereCondition("id=" + vos[i].getStringValue("id"));
				sqls.add(usb.getSQL());
				account_fa_list.setRealValueAt((i + 1) + "", i, "seq");
			}
			UIUtil.executeBatchByDS(null, sqls);
		}
	}

	public boolean checkChecked(BillListPanel bl) {
		BillVO[] vos_ = bl.getCheckedBillVOs();
		if (vos_ == null || vos_.length <= 0) {
			MessageBox.show(bl, "请至少勾选一条项目记录来进行此操作!");
			return false;
		}
		return true;
	}

	public void onBillListSelectChanged(BillListSelectionEvent event) {
		account_fa_list.QueryDataByCondition("accountid=" + event.getCurrSelectedVO().getPkValue());
	}

	public boolean moveUpRow() {
		account_fa_list.stopEditing();
		int[] li_currRows = account_fa_list.getSelectedRows(true);
		int sign_stop = -1;
		for (int i = 0; i < li_currRows.length; i++) {
			int li_currRow = li_currRows[i];
			if (li_currRow < 0) {
				return false;
			}
			if (li_currRow > 0 && (li_currRow - 1) != sign_stop) {
				account_fa_list.bo_ifProgramIsEditing = true;
				account_fa_list.getTableModel().moveRow(li_currRow, li_currRow, li_currRow - 1);
				account_fa_list.getTable().removeRowSelectionInterval(li_currRow, li_currRow);
				account_fa_list.getTable().addRowSelectionInterval(li_currRow - 1, li_currRow - 1);
				account_fa_list.bo_ifProgramIsEditing = false;
				sign_stop = li_currRow - 1;
			} else {
				sign_stop = li_currRow;
			}
		}
		return true;
	}

	public boolean moveDownRow() {
		account_fa_list.stopEditing();
		int[] li_currRows = account_fa_list.getSelectedRows(true);
		int sign_stop = account_fa_list.getTableModel().getRowCount();
		for (int i = li_currRows.length - 1; i >= 0; i--) {
			int li_currRow = li_currRows[i];
			if (li_currRow < 0) {
				return false;
			}
			if (li_currRow >= 0 && (li_currRow + 1) != sign_stop) {
				account_fa_list.bo_ifProgramIsEditing = true;
				account_fa_list.getTableModel().moveRow(li_currRow, li_currRow, li_currRow + 1);
				account_fa_list.getTable().removeRowSelectionInterval(li_currRow, li_currRow);
				account_fa_list.getTable().addRowSelectionInterval(li_currRow + 1, li_currRow + 1);
				account_fa_list.bo_ifProgramIsEditing = false;
				sign_stop = li_currRow + 1;
			} else {
				sign_stop = li_currRow;
			}
		}
		return true;
	}

	public void onBillListAddButtonClicked(BillListButtonClickedEvent event) throws Exception {
		BillCardPanel cardpanel = event.getCardPanel();
		BillVO billvo = cardpanel.getBillVO();
		if (billvo != null) {
			if ("Y".equals(billvo.getStringValue("isdefault"))) {
				UIUtil.executeUpdateByDS(null, "update sal_account_set set isdefault = 'N' where id !=" + billvo.getPkValue());
				account_list.refreshData();
				BillVO vos[] = account_list.getBillVOs();
				for (int i = 0; i < vos.length; i++) {
					if (billvo.getPkValue().equals(vos[i].getPkValue())) {
						account_list.setSelectedRow(i);
						break;
					}
				}

			}
		}
	}

	public void onBillListAddButtonClicking(BillListButtonClickedEvent event) throws Exception {
		// TODO Auto-generated method stub

	}

	public void onBillListButtonClicked(BillListButtonClickedEvent event) throws Exception {
		// TODO Auto-generated method stub

	}

	public void onBillListDeleteButtonClicked(BillListButtonClickedEvent event) {
		account_list.refreshData();
		BillVO vos[] = account_list.getAllBillVOs();
		boolean falg = false; //是否有默认的薪酬帐套
		for (int i = 0; i < vos.length; i++) {
			if ("Y".equals(vos[i].getStringValue("isdefault"))) {
				falg = true;
				break;
			}
		}
		account_fa_list.removeAllRows();
		if (vos.length == 0) {
			return;
		}
		if (!falg) {
			MessageBox.show(this, "请选择一个帐套设定为默认薪酬帐套.");
		}
	}

	public void onBillListDeleteButtonClicking(BillListButtonClickedEvent event) throws Exception {
		BillVO delvo = event.getBillListPanel().getSelectedBillVO();
		String del_1 = "delete from sal_account_factor where accountid =" + delvo.getStringValue("id"); //删除帐套明细
		String del_2 = "delete from sal_account_personinfo where accountid =" + delvo.getStringValue("id");//删除帐套关联的人员
		try {
			UIUtil.executeBatchByDS(null, new String[] { del_1, del_2 });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onBillListEditButtonClicked(BillListButtonClickedEvent event) {
		BillCardPanel cardpanel = event.getCardPanel();
		BillVO billvo = cardpanel.getBillVO();
		if (billvo != null) {
			if ("Y".equals(billvo.getStringValue("isdefault"))) {
				try {
					UIUtil.executeUpdateByDS(null, "update sal_account_set set isdefault = 'N' where id !=" + billvo.getPkValue());
					account_list.refreshData();
					BillVO vos[] = account_list.getBillVOs();
					for (int i = 0; i < vos.length; i++) {
						if (billvo.getPkValue().equals(vos[i].getPkValue())) {
							account_list.setSelectedRow(i);
							break;
						}
					}
				} catch (WLTRemoteException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void onBillListEditButtonClicking(BillListButtonClickedEvent event) throws Exception {
		// TODO Auto-generated method stub

	}

	public void onBillListLookAtButtonClicked(BillListButtonClickedEvent event) {
		// TODO Auto-generated method stub

	}

	public void onBillListLookAtButtonClicking(BillListButtonClickedEvent event) {
		// TODO Auto-generated method stub

	}

}
