package cn.com.pushworld.salary.ui.paymanage;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.DeleteSQLBuilder;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.*;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

public class Salary_UserAccountSetWKPanel extends AbstractWorkPanel implements BillListSelectListener, ActionListener {
	private static final long serialVersionUID = 1L;
	private BillListPanel account_list, account_person_list,grid_list = null;
	private WLTSplitPane mainsp = null;
	private WLTButton importPerson, moveOutPerson,importGrid,moveGrid = null;
	private String selectaccountid = null;
	private WLTTabbedPane pane = null;//zzl[2020-5-9]
	private TBUtil tbUtil = null; //zzl[2020-5-11]
	public void initialize() {
		Boolean wgflg=getTBUtil().getSysOptionBooleanValue("�Ƿ���������ָ�����ģʽ", false);//zzl[2020-5-11]
		account_list = new BillListPanel("SAL_ACCOUNT_SET_CODE1");
		account_person_list = new BillListPanel("SAL_PERSONINFO_CODE1");
		grid_list = new BillListPanel("EXCEL_TAB_85_CODE_1");//zzl[2020-5-9��������ģ��
		importPerson = new WLTButton("������Ա", "office_163.gif");
		moveOutPerson = new WLTButton("�Ƴ���Ա", "office_165.gif");
		importGrid = new WLTButton("��������", "office_163.gif");//zzl[2020-5-9]
		moveGrid = new WLTButton("�Ƴ�����", "office_165.gif");
		importPerson.addActionListener(this);
		moveOutPerson.addActionListener(this);
		importGrid.addActionListener(this);
		moveGrid.addActionListener(this);
		account_person_list.addBatchBillListButton(new WLTButton[]{importPerson, moveOutPerson});
		account_person_list.repaintBillListButton();
		account_person_list.setRowNumberChecked(true);
		grid_list.addBatchBillListButton(new WLTButton[]{importGrid,moveGrid});
		grid_list.repaintBillListButton();
		grid_list.setRowNumberChecked(true);
		account_list.addBillListSelectListener(this);
		if(wgflg){
			pane = new WLTTabbedPane(); //
			pane.addTab("������Ա", UIUtil.getImage("office_134.gif"),
					account_person_list); //
			pane.addTab("��������", UIUtil.getImage("office_194.gif"),grid_list); //
			mainsp = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, account_list, pane);
			mainsp.setDividerLocation(500);
		}else{
			mainsp = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, account_list, account_person_list);
			mainsp.setDividerLocation(500);
		}
		this.add(mainsp, BorderLayout.CENTER);
	}
	public void onBillListSelectChanged(BillListSelectionEvent event) {
		if (event.getCurrSelectedVO() != null) {
			selectaccountid = event.getCurrSelectedVO().getPkValue();
			account_person_list.QueryDataByCondition("id in (select personinfoid from sal_account_personinfo where accountid=" + event.getCurrSelectedVO().getPkValue() + ")");
			grid_list.QueryDataByCondition("id in (select personinfoid from sal_account_personinfo where accountid=" + event.getCurrSelectedVO().getPkValue() + ")");
		}
	}
	
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == importPerson) {
			onImportP();
		} else if (arg0.getSource() == moveOutPerson) {
			onMoveOutP();
		} else if (arg0.getSource() == importGrid) {
			onImportGrid();
		} else if (arg0.getSource() == moveGrid) {
			onMoveOutGrid();
		}
	}

	private void onMoveOutGrid() {
		BillVO[] vos_ = grid_list.getCheckedBillVOs();
		if (vos_ == null || vos_.length <= 0) {
			MessageBox.show(grid_list, "�����ٹ�ѡһ����Ա��¼�����д˲���!");
			return;
		}
		String accountid = selectaccountid;
		List<String> delSql = new ArrayList<String>();
		for (int i = 0 ; i < vos_.length ; i++) {
			DeleteSQLBuilder dsb = new DeleteSQLBuilder("sal_account_personinfo");
			dsb.setWhereCondition("personinfoid=" + vos_[i].getPkValue() + " and accountid=" + accountid);
			delSql.add(dsb.getSQL());
		}
		try {
			UIUtil.executeBatchByDS(null, delSql);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(grid_list, "�Ƴ���Աʧ��,���ٴγ��Ի�����ϵϵͳ����Ա!");
			return;
		}
		MessageBox.show(grid_list, "�����ɹ�!");
		grid_list.refreshData();
	}

	private void onImportGrid() {
		final BillVO vo = account_list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(grid_list, "��ѡ��һ���������������д˲���!");
			return;
		}
		final BillListDialog dialog=new BillListDialog(this,"�����б�","EXCEL_TAB_85_CODE_2");
		dialog.getBilllistPanel().setRowNumberChecked(true);
		dialog.getBtn_confirm().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				onConfirm(vo,dialog);
			}
		});
		dialog.setVisible(true);
	}

	public void onImportP() {
		BillVO vo = account_list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.show(account_person_list, "��ѡ��һ���������������д˲���!");
			return;
		}
		SalaryImportPersonInfoDialog sipd = new SalaryImportPersonInfoDialog(account_person_list, vo);
		sipd.setVisible(true);
	}
	
	public void onMoveOutP() {
		BillVO[] vos_ = account_person_list.getCheckedBillVOs();
		if (vos_ == null || vos_.length <= 0) {
			MessageBox.show(account_person_list, "�����ٹ�ѡһ����Ա��¼�����д˲���!");
			return;
		}
		String accountid = selectaccountid;
		List<String> delSql = new ArrayList<String>();
		for (int i = 0 ; i < vos_.length ; i++) {
			DeleteSQLBuilder dsb = new DeleteSQLBuilder("sal_account_personinfo");
			dsb.setWhereCondition("personinfoid=" + vos_[i].getPkValue() + " and accountid=" + accountid);
			delSql.add(dsb.getSQL());
		}
		try {
			UIUtil.executeBatchByDS(null, delSql);
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(account_person_list, "�Ƴ���Աʧ��,���ٴγ��Ի�����ϵϵͳ����Ա!");
			return;
		}
		MessageBox.show(account_person_list, "�����ɹ�!");
		account_person_list.refreshData();
	}
	public void onConfirm(BillVO vo,BillListDialog bl) {
		BillVO[] vos = bl.getBilllistPanel().getCheckedBillVOs();
		if (vos == null || vos.length <= 0) {
			MessageBox.show(this, "�����ٹ�ѡһ����Ա��Ϣ���д˲���!");
			return;
		}

		BillVO[] selectvos = vos;
		try {
			HashMap map = UIUtil.getHashMapBySQLByDS(null, "select personinfoid, id from sal_account_personinfo where accountid=" + vo.getStringValue("id"));
			List<String> sqls = new ArrayList<String>();
			for (int i = 0 ; i < selectvos.length; i++) {
				if (map.containsKey(selectvos[i].getStringValue("id"))) {
					continue;
				}
				InsertSQLBuilder isb = new InsertSQLBuilder();
				isb.setTableName("sal_account_personinfo");
				isb.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_SAL_ACCOUNT_PERSONINFO"));
				isb.putFieldValue("accountid", vo.getStringValue("id"));
				isb.putFieldValue("personinfoid", selectvos[i].getStringValue("id"));
				sqls.add(isb.getSQL());
			}
			UIUtil.executeBatchByDS(null, sqls);
			grid_list.refreshData();
			MessageBox.show(bl.getBilllistPanel(), "�����ɹ�!");
			bl.dispose();
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(bl.getBilllistPanel(), "������Աʧ��!���ٴγ��Ի������Ա��ϵ!");
		}
	}
	public TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil; //
		}
		tbUtil = new TBUtil(); //
		return tbUtil;
	}

}
