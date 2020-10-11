package cn.com.pushworld.salary.ui.moneymaking;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.report.BillCellPanel;

/**
 * �ʲ���ծ�����桢�ֽ��������������ά��
 * @author ��Ӫ����2014/02/11��
 * */
public class AssetliabilityWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillListPanel listPanel;
	private WLTButton btn_add, btn_show, btn_edit, btn_update, btn_delete;
	private FrameWorkMetaDataServiceIfc metaservice = null;
	private String oldtempcode = null;
	private String str_tem_name = null;

	@Override
	public void initialize() {
		oldtempcode = this.getMenuConfMapValueAsStr("ģ������");
		String listtempcode = null;
		if (oldtempcode.equalsIgnoreCase("push_zc001")) {
			str_tem_name = "�ʲ���ծ";
			listtempcode = "MONEYMANAGE_CODE1";
		} else if (oldtempcode.equalsIgnoreCase("push_zc002")) {
			str_tem_name = "����";
			listtempcode = "MONEYMANAGE_CODE2";
		} else {
			str_tem_name = "�ֽ�����";
			listtempcode = "MONEYMANAGE_CODE3";
		}
		listPanel = new BillListPanel(listtempcode);
		btn_add = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		btn_show = new WLTButton("�鿴");
		btn_show.addActionListener(this);
		btn_edit = new WLTButton("�༭����");
		btn_edit.addActionListener(this);
		btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		btn_delete.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_update, btn_show, btn_delete, btn_edit });
		listPanel.QueryDataByCondition("style ='" + str_tem_name + "'");
		listPanel.getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				BillQueryPanel queryPanel = listPanel.getQuickQueryPanel();
				String sql = queryPanel.getQuerySQL(queryPanel.getAllQuickQueryCompents()) + " and style ='" + str_tem_name + "'";
				listPanel.QueryData(sql);
			}
		});
		listPanel.repaintBillListButton();
		this.add(listPanel);
		this.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(listPanel);
			return;
		}
		if (e.getSource() == btn_edit) {
			try {
				onEditdate(billVO);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == btn_show) {
			onShowpanel(billVO);
		} else if (e.getSource() == btn_delete) {
			doDelete();
		}
	}

	/**
	 * ɾ���¼�����
	 * */
	private void doDelete() {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (!MessageBox.confirmDel(this)) {
			return; //
		}
		List sqllist = new ArrayList();
		String sql_1 = "delete from pub_billcelltemplet_d where templet_h_id in (select id from pub_billcelltemplet_h where templetcode='" + billVO.getStringValue("cellpanelid") + "')";
		String sql_2 = "delete from pub_billcelltemplet_h where templetcode ='" + billVO.getStringValue("cellpanelid") + "'";
		sqllist.add(sql_1);
		sqllist.add(sql_2);
		try {
			UIUtil.executeBatchByDS(null, sqllist);
			listPanel.doDelete(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * excel�ĵ����ݵ���д
	 * */
	private void onEditdate(BillVO billVO) throws Exception {
		String state = billVO.getStringValue("states");
		final BillDialog billDialog = new BillDialog(this, "�ʲ���ծ��", 1024, 800);

		if (state.equals("����д")) {
			String cellpanelid = billVO.getStringValue("cellpanelid");
			if (MessageBox.showConfirmDialog(this, "�ñ�������д���Ƿ�����༭��") == JOptionPane.NO_OPTION) {
				BillCellPanel cellPanel = new BillCellPanel();
				cellPanel.loadBillCellData(getMetaService().getBillCellVO(billVO.getStringValue("cellpanelid"), null, null));
				cellPanel.setEditable(false);
				cellPanel.getTitleBtnBar().setVisible(false);
				billDialog.add(cellPanel, BorderLayout.CENTER);
				billDialog.addOptionButtonPanel(new String[] { "ȡ��" });
				billDialog.setVisible(true);
			} else {
				BillCellPanel cellPanel = new BillCellPanel();
				cellPanel.loadBillCellData(getMetaService().getBillCellVO(billVO.getStringValue("cellpanelid"), null, null));
				cellPanel.putClientProperty("this", this);
				BillCellVO billCellVO = cellPanel.getBillCellVO();
				BillCellItemVO[][] itemVOs = billCellVO.getCellItemVOs();
				for (int i = 0; i < itemVOs.length; i++) {
					for (int j = 0; j < itemVOs[i].length; j++) {
						if (itemVOs[i][j].getIseditable().equalsIgnoreCase("y")) {
							itemVOs[i][j].setEditformula("AddItemValueChangedListener(\"cn.com.pushworld.salary.ui.moneymaking.AssetliabilityCheckDateLinstener\",\"Cellִ��\")");
							itemVOs[i][j].setCellhelp(itemVOs[i][j].getValidateformula());
						}
					}
				}
				billDialog.add(cellPanel, BorderLayout.CENTER);
				billDialog.addOptionButtonPanel(new String[] { "ȷ��", "ȡ��" });
				billDialog.setVisible(true);
				if (billDialog.getCloseType() == 0) {
					cellPanel.dealSave();
				}
			}
		} else {
			try {
				String temcode = corycellpanel();
				BillCellPanel cellPanel = new BillCellPanel();
				cellPanel.loadBillCellData(getMetaService().getBillCellVO(temcode, null, null));
				cellPanel.putClientProperty("this", this);
				BillCellVO billCellVO = cellPanel.getBillCellVO();
				BillCellItemVO[][] itemVOs = billCellVO.getCellItemVOs();
				for (int i = 0; i < itemVOs.length; i++) {
					for (int j = 0; j < itemVOs[i].length; j++) {
						if (itemVOs[i][j].getIseditable().equalsIgnoreCase("y")) {
							itemVOs[i][j].setEditformula("AddItemValueChangedListener(\"cn.com.pushworld.salary.ui.moneymaking.AssetliabilityCheckDateLinstener\",\"Cellִ��\")");
							itemVOs[i][j].setCellhelp(itemVOs[i][j].getValidateformula());
						}
					}
				}
				billDialog.add(cellPanel, BorderLayout.CENTER);
				billDialog.addOptionButtonPanel(new String[] { "ȷ��", "ȡ��" });
				billDialog.setVisible(true);
				if (billDialog.getCloseType() == 0) {
					cellPanel.dealSave();
					UIUtil.executeUpdateByDS(null, "update moneymanage set states = '����д' , cellpanelid ='" + temcode + "'where id ='" + billVO.getStringValue("id") + "'");
					listPanel.refreshCurrSelectedRow();
				} else {
					cleardate(temcode);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ������Ƶ�ģ��
	 * */
	private void cleardate(String code) {
		List sqllist = new ArrayList();
		String sql_1 = "delete from pub_billcelltemplet_d where templet_h_id in (select id from pub_billcelltemplet_h where templetcode='" + code + "')";
		String sql_2 = "delete from pub_billcelltemplet_h where templetcode ='" + code + "'";
		sqllist.add(sql_1);
		sqllist.add(sql_2);
		try {
			UIUtil.executeBatchByDS(null, sqllist);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �鿴������Ϣ
	 * */
	private void onShowpanel(BillVO billVO) {
		String state = billVO.getStringValue("states");
		if (state == null || state.equals("δ��д")) {
			MessageBox.show(this, "�ñ���δ��д");
			return;
		}
		BillDialog billDialog = new BillDialog(this, "�ʲ���ծ��", 1024, 800);
		BillCellPanel cellPanel = new BillCellPanel(billVO.getStringValue("cellpanelid"));
		cellPanel.setEditable(false);
		cellPanel.getTitleBtnBar().setVisible(false);
		billDialog.add(cellPanel, BorderLayout.CENTER);
		billDialog.addOptionButtonPanel(new String[] { "ȡ��" });
		billDialog.setVisible(true);
	}

	/**
	 * ����ģ��
	 * */
	private String corycellpanel() throws Exception {
		String str_temp_code = ""; // �±���
		Vector v_sqls = new Vector();
		String sql = "select count(*) from pub_billcelltemplet_h where templetcode like \"" + oldtempcode + "_%\"";
		String count = UIUtil.getStringValueByDS(null, sql);
		if (count == null || count.equals("") || count.equals("0")) {
			str_temp_code = oldtempcode + "_1";
		} else {
			String sql_1 = "select max(templetcode)  from pub_billcelltemplet_h where templetcode like \"" + oldtempcode + "_%\"";
			String maxcode = UIUtil.getStringValueByDS(null, sql_1);
			String maxcodes = maxcode.substring(oldtempcode.length() + 1, maxcode.length());
			int index = Integer.parseInt(maxcodes) + 1;
			str_temp_code = oldtempcode + "_" + index;
		}

		HashVO[] hashVO = UIUtil.getHashVoArrayByDS(null, "select * from pub_billcelltemplet_h where templetcode ='" + oldtempcode + "'");
		if (hashVO == null || hashVO.length < 0) {
			return "";
		}
		String keys[] = hashVO[0].getKeys();
		String oldid = hashVO[0].getStringValue("id");
		InsertSQLBuilder ins = new InsertSQLBuilder("pub_billcelltemplet_h");
		String id = UIUtil.getSequenceNextValByDS(null, "S_PUB_BILLCELLTEMPLET_H");
		for (int i = 0; i < keys.length; i++) {
			if (keys[i].equals("id")) {
				ins.putFieldValue(keys[i], id);
			} else if (keys[i].equals("templetcode")) {
				ins.putFieldValue(keys[i], str_temp_code);
			} else {
				ins.putFieldValue(keys[i], hashVO[0].getStringValue(keys[i]));
			}
		}
		v_sqls.add(ins.getSQL());
		HashVO[] hashVOs = UIUtil.getHashVoArrayByDS(null, "select * from pub_billcelltemplet_d where templet_h_id ='" + oldid + "'");
		if (hashVOs != null) {
			String itemkeys[] = null;
			InsertSQLBuilder itemins = new InsertSQLBuilder("pub_billcelltemplet_d");
			for (int j = 0; j < hashVOs.length; j++) {
				if (j == 0) {
					itemkeys = hashVOs[0].getKeys();
				}
				String itemid = UIUtil.getSequenceNextValByDS(null, "S_PUB_BILLCELLTEMPLET_D");
				for (int k = 0; k < itemkeys.length; k++) {
					if (itemkeys[k].equals("id")) {
						itemins.putFieldValue(itemkeys[k], itemid);
					} else if (itemkeys[k].equals("templet_h_id")) {
						itemins.putFieldValue(itemkeys[k], id);
					} else {
						itemins.putFieldValue(itemkeys[k], hashVOs[j].getStringValue(itemkeys[k]));
					}
				}
				v_sqls.add(itemins.getSQL());
			}
		}
		UIUtil.executeBatchByDS(null, v_sqls); ////
		return str_temp_code;
	}

	private FrameWorkMetaDataServiceIfc getMetaService() throws Exception {
		if (metaservice == null) {
			metaservice = (FrameWorkMetaDataServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkMetaDataServiceIfc.class);
		}
		return metaservice;
	}
}
