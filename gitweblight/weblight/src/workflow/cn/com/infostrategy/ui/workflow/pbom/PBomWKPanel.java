package cn.com.infostrategy.ui.workflow.pbom;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class PBomWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel billList = null; //
	private WLTButton btn_insert, btn_copy, btn_edit, btn_delete, btn_list, btn_config; //

	@Override
	public void initialize() {
		billList = new BillListPanel("PUB_BOM_CODE1"); //

		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); //��������
		btn_copy = new WLTButton("����");
		btn_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //�����༭
		btn_delete = new WLTButton("ɾ��"); //
		btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //
		btn_config = new WLTButton("����"); //
		btn_copy.addActionListener(this);
		btn_delete.addActionListener(this); //
		btn_config.addActionListener(this); //

		billList.addBatchBillListButton(new WLTButton[] { btn_insert, btn_copy, btn_edit, btn_delete, btn_list, btn_config }); //
		billList.repaintBillListButton(); //
		this.add(billList); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_delete) {
			onDelete(); //
		} else if (e.getSource() == btn_config) {
			onConfig(); //
		} else if (e.getSource() == btn_copy) {
			onCopy();
		}
	}

	/**
	 * ����bomͼ������������ӱ�������������ͬ�������͵�bomͼ���ӵ�Class�಻ͬ�������������ͼ������ͼƬ������һ�ţ������������������ȵ㣬���鷳�������Ӹø��ƹ��ܡ����/2012-07-25��
	 */
	private void onCopy() {
		BillVO selVO = billList.getSelectedBillVO(); //
		if (selVO == null) {
			MessageBox.showSelectOne(this);
			return; //
		}
		try {
			Vector vector = UIUtil.getHashVoArrayReturnVectorByMark(null, new String[] { "select * from pub_bom where id=" + selVO.getStringValue("id"), "select * from pub_bom_b where bomid=" + selVO.getStringValue("id"),
					"select * from pub_imgupload where batchid=" + selVO.getStringValue("bgimgname") + " order by seq" });
			ArrayList sqlList = new ArrayList();
			HashVO[] hashvos = (HashVO[]) vector.get(0);
			if (hashvos != null && hashvos.length > 0) {
				InsertSQLBuilder insertsql = new InsertSQLBuilder("pub_bom");//bom�����¼��ֻȡһ��
				String bomid = UIUtil.getSequenceNextValByDS(null, "s_pub_bom");
				String batchid = UIUtil.getSequenceNextValByDS(null, "S_PUB_IMGUPLOAD_BATCHID");
				insertsql.putFieldValue("ID", bomid);
				insertsql.putFieldValue("CODE", hashvos[0].getStringValue("CODE") + "_copy");
				insertsql.putFieldValue("BGIMGNAME", batchid);
				insertsql.putFieldValue("DESCR", hashvos[0].getStringValue("DESCR"));
				insertsql.putFieldValue("AFTERINITCLS", hashvos[0].getStringValue("AFTERINITCLS"));
				sqlList.add(insertsql.getSQL());

				HashVO[] hashvos_b = (HashVO[]) vector.get(1);
				if (hashvos_b != null && hashvos_b.length > 0) {
					for (int i = 0; i < hashvos_b.length; i++) {
						insertsql = new InsertSQLBuilder("pub_bom_b");//bom�ӱ��¼������
						insertsql.putFieldValue("ID", UIUtil.getSequenceNextValByDS(null, "s_pub_bom_b"));
						insertsql.putFieldValue("BOMID", bomid);
						insertsql.putFieldValue("X", hashvos_b[i].getStringValue("X"));
						insertsql.putFieldValue("Y", hashvos_b[i].getStringValue("Y"));
						insertsql.putFieldValue("WIDTH", hashvos_b[i].getStringValue("WIDTH"));
						insertsql.putFieldValue("HEIGHT", hashvos_b[i].getStringValue("HEIGHT"));
						insertsql.putFieldValue("ITEMKEY", hashvos_b[i].getStringValue("ITEMKEY"));
						insertsql.putFieldValue("ITEMNAME", hashvos_b[i].getStringValue("ITEMNAME"));
						insertsql.putFieldValue("BINDBOMCODE", hashvos_b[i].getStringValue("BINDBOMCODE"));
						insertsql.putFieldValue("BINDCLASSNAME", hashvos_b[i].getStringValue("BINDCLASSNAME"));
						insertsql.putFieldValue("BINDTYPE", hashvos_b[i].getStringValue("BINDTYPE"));
						insertsql.putFieldValue("BINDMENU", hashvos_b[i].getStringValue("BINDMENU"));
						insertsql.putFieldValue("LOADTYPE", hashvos_b[i].getStringValue("LOADTYPE"));
						sqlList.add(insertsql.getSQL());
					}
				}
				HashVO[] hashvos_img = (HashVO[]) vector.get(2);
				if (hashvos_img != null && hashvos_img.length > 0) {
					for (int i = 0; i < hashvos_img.length; i++) {
						insertsql = new InsertSQLBuilder("pub_imgupload");//�������е�ͼƬ���±���һ�ѣ���ֹ���ֶ��bomͼ����һ��ͼƬ��ɾ��ĳһ��bomͼ����������bomͼ�Ϳ�����ͼƬ�ˡ�
						insertsql.putFieldValue("ID", UIUtil.getSequenceNextValByDS(null, "s_pub_imgupload"));
						insertsql.putFieldValue("BATCHID", batchid);
						insertsql.putFieldValue("BILLTABLE", hashvos_img[i].getStringValue("BILLTABLE"));
						insertsql.putFieldValue("BILLPKNAME", hashvos_img[i].getStringValue("BILLPKNAME"));
						insertsql.putFieldValue("BILLPKVALUE", bomid);
						insertsql.putFieldValue("SEQ", hashvos_img[i].getStringValue("SEQ"));
						insertsql.putFieldValue("IMG0", hashvos_img[i].getStringValue("IMG0"));
						insertsql.putFieldValue("IMG1", hashvos_img[i].getStringValue("IMG1"));
						insertsql.putFieldValue("IMG2", hashvos_img[i].getStringValue("IMG2"));
						insertsql.putFieldValue("IMG3", hashvos_img[i].getStringValue("IMG3"));
						insertsql.putFieldValue("IMG4", hashvos_img[i].getStringValue("IMG4"));
						insertsql.putFieldValue("IMG5", hashvos_img[i].getStringValue("IMG5"));
						insertsql.putFieldValue("IMG6", hashvos_img[i].getStringValue("IMG6"));
						insertsql.putFieldValue("IMG7", hashvos_img[i].getStringValue("IMG7"));
						insertsql.putFieldValue("IMG8", hashvos_img[i].getStringValue("IMG8"));
						insertsql.putFieldValue("IMG9", hashvos_img[i].getStringValue("IMG9"));
						sqlList.add(insertsql.getSQL());
					}
				}
				UIUtil.executeBatchByDS(null, sqlList);
				MessageBox.show(this, "���Ƴɹ�!");
				billList.refreshData();
				int row = billList.findRow("id", bomid);
				if (row > -1) {
					billList.setSelectedRow(row);//��λ�������ļ�¼
				}
			} else {
				MessageBox.show(this, "�ü�¼�Ѳ�����!");
			}
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}

	/**
	 * ɾ��
	 */
	private void onDelete() {
		BillVO selVO = billList.getSelectedBillVO(); //
		if (selVO == null) {
			MessageBox.showSelectOne(this);
			return; //
		}
		if (!MessageBox.confirm(this, "��ȷ��Ҫɾ�������ü�¼��?")) {
			return; //
		}
		try {
			String str_id = selVO.getStringValue("id"); //����ֵ!
			String str_img = selVO.getStringValue("bgimgname"); //ͼƬ����pub_bom_b.bomid
			ArrayList al_sqls = new ArrayList(); //
			al_sqls.add("delete from pub_bom   where id='" + str_id + "'"); ////
			al_sqls.add("delete from pub_bom_b where bomid='" + str_id + "'"); ////
			if (str_img != null && !str_img.trim().equals("")) {
				al_sqls.add("delete from pub_imgupload where batchid='" + str_img + "'"); //
			}
			UIUtil.executeBatchByDS(null, al_sqls); //
			billList.removeSelectedRow(); //
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
		}
	}

	/**
	 * ����!!
	 */
	private void onConfig() {
		BillVO selVO = billList.getSelectedBillVO(); //
		if (selVO == null) {
			MessageBox.showSelectOne(this);
			return; //
		}
		final String str_bomCode = selVO.getStringValue("code"); //
		final BillBomPanel bomPanel = new BillBomPanel(str_bomCode); //
		bomPanel.setEditable(true);
		bomPanel.setCurrItemPanelEdit(); //
		final BillDialog dialog = new BillDialog(this, "�༭[" + str_bomCode + "],��������Ҽ����и��ֲ���!", 1000, 700); //
		dialog.getContentPane().add(bomPanel);
		dialog.setAddDefaultWindowListener(false);//��ִ��Ĭ�ϵĹر��¼�
		dialog.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if (MessageBox.showConfirmDialog(dialog, "���Ƿ�Ҫ�����޸�?") == JOptionPane.YES_OPTION) {
					bomPanel.getBomItemPanel(str_bomCode).onSave();
				}
				dialog.dispose();
			}
		});
		dialog.setVisible(true); //
	}

}
