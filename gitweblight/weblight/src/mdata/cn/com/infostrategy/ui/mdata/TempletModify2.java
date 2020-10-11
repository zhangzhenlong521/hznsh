/**************************************************************************
 * $RCSfile: TempletModify2.java,v $  $Revision: 1.18 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.DefaultCellEditor;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.to.mdata.templetvo.TMO_Pub_Templet_1;
import cn.com.infostrategy.to.mdata.templetvo.TMO_Pub_Templet_1_item;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;

public class TempletModify2 extends BillDialog implements ActionListener {
	private static final long serialVersionUID = 1L;

	private String str_templetcode = null;
	private String xml_url = null;
	private BillCardPanel cardPanel = null;
	private BillListPanel listPanel = null;
	private WLTButton btn_exportXml, btn_insertrow, btn_delrow, btn_moveup, btn_movedown, btn_refresh, btn_save, btn_confirm, btn_cancel, btn_importOther; //
	Container parent = null; //Ԭ���� 20130314 ���  ����ʵ��ˢ�¸�ģ��

	private boolean isAppConf = false; //�Ƿ���ʵʩ��Ա����!!!

	private boolean isViewXml = false;

	public TempletModify2(Container _parent, String _templetcode) {
		this(_parent, _templetcode, false); //
	}

	public TempletModify2(Container _parent, String _templetcode, boolean _isAppConf) {
		super(_parent, "ģ��༭", 1024, 740);
		this.parent = _parent;
		str_templetcode = _templetcode;
		isAppConf = _isAppConf; //�Ƿ�ʵʩ��Ա����!
		initialize();
		this.setVisible(true); //
	}

	public TempletModify2(Container _parent, String _templetcode, boolean _isAppConf, boolean _isViewXml) {
		super(_parent, "ģ��鿴", 1024, 740);
		str_templetcode = _templetcode;
		isAppConf = _isAppConf; //�Ƿ�ʵʩ��Ա����!
		isViewXml = _isViewXml;
		initialize();
		this.setVisible(true); //
	}

	//׷��ֱ�Ӵ�xml·��ȡ �����/2013-03-25��
	public TempletModify2(Container _parent, String _templetcode, String _xml_url, boolean _isAppConf, boolean _isViewXml) {
		super(_parent, "ģ��鿴", 1024, 740);
		str_templetcode = _templetcode;
		xml_url = _xml_url;
		isAppConf = _isAppConf; //�Ƿ�ʵʩ��Ա����!
		isViewXml = _isViewXml;
		initialize();
		this.setVisible(true); //
	}

	private void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //
		cardPanel = new BillCardPanel(new TMO_Pub_Templet_1(isAppConf));
		cardPanel.updateCurrRow(); //  
		cardPanel.setEditable("PK_PUB_TEMPLET_1", false); //
		cardPanel.setEditable("TEMPLETCODE", false); //

		listPanel = new BillListPanel(new TMO_Pub_Templet_1_item(isAppConf), false);
		listPanel.setBillListOpaque(false); //͸����
		listPanel.initialize();

		if (isAppConf) { //ʵʩģʽ�Ƿ������޸������е�!
			listPanel.setItemEditable("itemkey", false); //
			listPanel.setItemEditable("itemtype", false); //
		} else {
			listPanel.setItemEditable(true); //
		}

		WLTSplitPane split = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, cardPanel, listPanel); //
		if (isAppConf) {
			split.setDividerLocation(100); //
		} else {
			split.setDividerLocation(300); //
		}
		this.getContentPane().add(split, BorderLayout.CENTER); //

		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //�������а�ť
		onRefresh(); //ˢ������
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 2)); //

		btn_exportXml = new WLTButton("����XML"); //
		btn_insertrow = new WLTButton("����"); //
		btn_delrow = new WLTButton("ɾ��"); //
		btn_moveup = new WLTButton("����"); //
		btn_movedown = new WLTButton("����"); //
		btn_refresh = new WLTButton("ˢ��"); //
		btn_save = new WLTButton("����"); //
		btn_confirm = new WLTButton("ȷ��"); //
		btn_cancel = new WLTButton("ȡ��"); //
		btn_importOther = new WLTButton("���ٸ���");
		btn_importOther.setToolTipText("������ģ�帴�Ʋ����ֶ�");
		btn_exportXml.addActionListener(this); //
		btn_insertrow.addActionListener(this); //
		btn_delrow.addActionListener(this); //
		btn_moveup.addActionListener(this); //
		btn_movedown.addActionListener(this); //
		btn_refresh.addActionListener(this); //
		btn_save.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		btn_importOther.addActionListener(this);
		if (!isViewXml) { //����ǲ��ǲ鿴xml��ģ��
			if (isAppConf) { //ʵʩģʽ,ֻ�ܵ�˳��!!!
				panel.add(btn_exportXml);
				panel.add(btn_moveup);
				panel.add(btn_movedown);
				panel.add(btn_refresh);
				panel.add(btn_save);
				panel.add(btn_confirm);
			} else { //����ģʽ!!ÿ����Ҫ
				panel.add(btn_exportXml);
				panel.add(btn_importOther);
				panel.add(btn_insertrow);
				panel.add(btn_delrow);
				panel.add(btn_moveup);
				panel.add(btn_movedown);
				panel.add(btn_refresh);
				panel.add(btn_save);
				panel.add(btn_confirm);
			}
		}
		panel.add(btn_cancel);
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_exportXml) {
			onExportXML(); //����XML
		} else if (e.getSource() == btn_insertrow) {
			onInsert();
		} else if (e.getSource() == btn_delrow) {
			onDelete();
		} else if (e.getSource() == btn_moveup) {
			onMoveup();
		} else if (e.getSource() == btn_movedown) {
			onMovedown();
		} else if (e.getSource() == btn_refresh) {
			onRefresh();
		} else if (e.getSource() == btn_save) {
			onSave();
		} else if (e.getSource() == btn_confirm) {
			onSaveAndExit();
		} else if (e.getSource() == btn_cancel) {
			onExit();
		} else if (e.getSource() == btn_importOther) {
			onImportOther();
		}
	}

	private void onImportOther() {
		MetaTempletQueryDialog dialog = new MetaTempletQueryDialog(this);
		dialog.setSize(800, 600);
		dialog.locationToCenterPosition();
		dialog.setVisible(true);
		String templetCode = dialog.getSelectTempletCode();
		if (dialog.getCloseType() == 0 && templetCode != null) {
			try {
				FrameWorkMetaDataServiceIfc service = UIUtil.getMetaDataService();
				String xml_url = dialog.getSelectTempletFrom();
				boolean isxml = false;
				if ("���ݿ�".equals(xml_url)) {

				} else {
					if (xml_url != null && xml_url.contains("XML_")) {
						xml_url = xml_url.substring(xml_url.indexOf("XML_"));
						isxml = true;
					}
				}
				BillListPanel copylistPanel = new BillListPanel(new TMO_Pub_Templet_1_item(isAppConf), false);
				copylistPanel.initialize();
				DefaultTMO tmo = null;
				if (isxml) {
					tmo = service.getDefaultTMOByCode(templetCode, 1); //ȡ��
					HashVO templet_1_item_vos[] = tmo.getPub_templet_1_itemData();
					copylistPanel.queryDataByHashVOs(templet_1_item_vos);
				} else {
					String str_pkvalue = UIUtil.getStringValueByDS(null, "select PK_PUB_TEMPLET_1 from PUB_TEMPLET_1 where TEMPLETCODE='" + templetCode + "'"); //
					copylistPanel.QueryData("select * from PUB_TEMPLET_1_ITEM where 1=1  and PK_PUB_TEMPLET_1=" + str_pkvalue + " order by showorder asc");
				}

				copylistPanel.setRowNumberChecked(true);
				BillListDialog listd = new BillListDialog(this, "", copylistPanel);
				listd.setVisible(true);
				if (listd.getCloseType() == 1) {
					BillVO rtvos[] = listd.getBilllistPanel().getCheckedBillVOs();
					int selectRow = listPanel.getSelectedRow();
					for (int i = 0; i < rtvos.length; i++) {
						onInsert();
						selectRow++;
						listPanel.stopEditing();
						String keys[] = rtvos[i].getKeys();
						for (int j = 0; j < keys.length; j++) {
							String key = keys[j];
							if (!key.equalsIgnoreCase("_RECORD_ROW_NUMBER") && !key.equalsIgnoreCase("PK_PUB_TEMPLET_1_ITEM") && !key.equalsIgnoreCase("PK_PUB_TEMPLET_1")) {
								listPanel.setValueAt(rtvos[i].getObject(key), selectRow, j);
							}
						}

					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	//������!!
	private void onInsert() {
		int li_row = listPanel.newRow(); //����һ��
		String str_parentid = cardPanel.getRealValueAt("PK_PUB_TEMPLET_1");
		listPanel.setValueAt(new StringItemVO(str_parentid), li_row, "PK_PUB_TEMPLET_1"); //

		//listPanel.setValueAt(str_parentid, li_row, "PK_PUB_TEMPLET_1"); //

		listPanel.setValueAt(new ComBoxItemVO("�ı���", "�ı���", "�ı���"), li_row, "ITEMTYPE"); //

		listPanel.setValueAt("Y", li_row, "CARDISSHOWABLE"); //
		listPanel.setValueAt(new ComBoxItemVO("1", "001", "ȫ���ɱ༭"), li_row, "CARDISEDITABLE"); //

		listPanel.setValueAt("Y", li_row, "LISTISSHOWABLE"); //

		//�б����Ƿ�ɱ༭��Ϊȫ������ �����/2013-03-13��
		listPanel.setValueAt(new ComBoxItemVO("4", "004", "ȫ������"), li_row, "LISTISEDITABLE");

		//�޸Ŀ�ƬĬ�Ͽ��Ϊ140 �����/2013-03-13��
		listPanel.setValueAt(new StringItemVO("140"), li_row, "CARDWIDTH");

		listPanel.setValueAt(new StringItemVO("125"), li_row, "LISTWIDTH"); //

		listPanel.setValueAt(new StringItemVO("" + (li_row + 1)), li_row, "SHOWORDER"); //

		//�Ƿ���뱣���ΪY �����/2013-03-13��
		listPanel.setValueAt("Y", li_row, "ISSAVE");

		//listPanel.setValueAt("N", li_row, "ISMUSTINPUT"); //

		listPanel.setValueAt(new ComBoxItemVO("2", "002", "ͨ�ò�ѯ"), li_row, "ISDEFAULTQUERY"); //

		//���Կ��Ƿ���ʾ��ΪY-���Կ��Ƿ�ɱ༭��ΪY �����/2013-03-13��
		listPanel.setValueAt("Y", li_row, "PROPISSHOWABLE"); //���Կ��Ƿ���ʾ!
		listPanel.setValueAt("Y", li_row, "PROPISEDITABLE"); //���Կ��Ƿ�ɱ༭!

		listPanel.setValueAt("N", li_row, "ISWRAP"); //

		//׷�ӱ���������-�б����Ƿ���뵼�� �����/2013-03-13��
		listPanel.setValueAt(new ComBoxItemVO("N", "N", "������"), li_row, "ISMUSTINPUT");
		listPanel.setValueAt("Y", li_row, "LISTISEXPORT");

		resetShowOrder(); //������һ��˳��

		listPanel.getTable().getCellEditor(li_row, 0).cancelCellEditing();
		listPanel.getTable().editCellAt(li_row, 0); //
		JTextField textField = ((JTextField) ((DefaultCellEditor) listPanel.getTable().getCellEditor(li_row, 0)).getComponent());
		textField.requestFocus();
	}

	//ɾ����!!
	private void onDelete() {
		listPanel.removeSelectedRows(); //ɾ������ѡ�����
		resetShowOrder(); //������һ��˳��
	}

	//����
	private boolean onSave() {
		if (parent instanceof BillCardPanel) {//����ǴӴ򿪵�ҳ����Ҽ��޸�ģ��
			((BillCardPanel) parent).setCanRefreshParent(true);//
		} else if (parent instanceof BillListPanel) {//����ǴӴ򿪵�ҳ����Ҽ��޸�ģ��
			((BillListPanel) parent).setCanRefreshParent(true);
		} else if (parent instanceof BillQueryPanel) {//��Ϊ���Ϊbillquery��ֻ���ܴ�ҳ����Ҽ��޸�ģ��
		}
		stopEditing();
		String str_sql_1 = cardPanel.getUpdateSQL();
		String[] str_sqls_2 = listPanel.getOperatorSQLs();
		ArrayList list = new ArrayList();
		list.add(str_sql_1);
		list.addAll(Arrays.asList(str_sqls_2)); //
		try {
			UIUtil.executeBatchByDS(null, list); //
			listPanel.setAllRowStatusAs("INIT");
			//JOptionPane.showMessageDialog(this, "�������ݳɹ�!!!"); ����ֱ��ˢ�¾Ͳ�Ҫ��ʾ�� Gwang 2013-06-22
			return true;
		} catch (Exception _ex) {
			MessageBox.showException(this, _ex); //
			return false;
		}

	}

	private void onMoveup() {
		if (listPanel.moveUpRow()) {
			resetShowOrder();
		}
	}

	private void onMovedown() {
		if (listPanel.moveDownRow()) {
			resetShowOrder(); //	
		}
	}

	//ˢ��
	private void onRefresh() {
		stopEditing(); //
		if (isViewXml) { //����ǲ鿴xml��
			try {
				DefaultTMO tmo = null;
				FrameWorkMetaDataServiceIfc service = UIUtil.getMetaDataService();

				//ֱ�Ӵ�xml·��ȡ �����/2013-03-25��
				if (xml_url != null && !xml_url.equals("")) {
					tmo = service.getDefaultTMOByCode(xml_url, 3); //ȡ��
				} else {
					tmo = service.getDefaultTMOByCode(str_templetcode, 1); //ȡ��
				}

				HashVO templet_1_vos = tmo.getPub_templet_1Data();
				HashVO templet_1_item_vos[] = tmo.getPub_templet_1_itemData();
				Object obj[][] = service.getBillListDataByHashVOs(cardPanel.getTempletVO().getParPub_Templet_1VO(), new HashVO[] { templet_1_vos }); //ֱ�Ӹ���hashvo�õ��ؼ�����
				if (obj != null && obj.length > 0) {
					cardPanel.setValue(obj[0]); //
				}
				listPanel.queryDataByHashVOs(templet_1_item_vos);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				cardPanel.refreshData("TEMPLETCODE='" + str_templetcode + "'");
				String str_pkvalue = UIUtil.getStringValueByDS(null, "select PK_PUB_TEMPLET_1 from PUB_TEMPLET_1 where TEMPLETCODE='" + str_templetcode + "'"); //
				listPanel.QueryData("select * from PUB_TEMPLET_1_ITEM where 1=1  and PK_PUB_TEMPLET_1=" + str_pkvalue + " order by showorder asc");
				//listPanel.queryDataByHashVOs(_hashVOs);  //
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}
	}

	//����XML
	private void onExportXML() {
		try {
			FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
			String str_xml = service.getXMlFromTableRecords(null, new String[] { "select * from pub_templet_1 where templetcode='" + str_templetcode + "'", //�������������!����һ��ͨ�õ����ܵĵ������б�ṹ��
					"select * from pub_templet_1_item where pk_pub_templet_1 in (select pk_pub_templet_1 from pub_templet_1 where templetcode='" + str_templetcode + "') order by showorder" }, //�ӱ����� 
					new String[][] { { "pub_templet_1", "pk_pub_templet_1", "S_PUB_TEMPLET_1" }, { "pub_templet_1_item", "pk_pub_templet_1_item", "S_PUB_TEMPLET_1_ITEM" } }, //�����ֶ�Լ��!! 
					new String[][] { { "pub_templet_1_item.pk_pub_templet_1", "pub_templet_1.pk_pub_templet_1" } }, null //���Լ��!!
					);
			JFileChooser fc = new JFileChooser();
			fc.setSelectedFile(new File("C:\\" + str_templetcode + ".xml")); //����Ĭ���ļ���!!!
			int li_result = fc.showSaveDialog(this); //
			if (li_result == JFileChooser.APPROVE_OPTION) { //�����ȷ����
				File saveToFile = fc.getSelectedFile(); //
				new TBUtil().writeBytesToOutputStream(new FileOutputStream(saveToFile, false), str_xml.getBytes("UTF-8")); //д�ļ�!!
				MessageBox.show(this, "��ģ�嵼����XMLд���ļ�[" + saveToFile.getAbsolutePath() + "]�ɹ�!"); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	//ȷ��,�����沢�˳�!!
	private void onSaveAndExit() {
		if (onSave()) { //�ȱ���
			this.dispose(); //
			setCloseType(1); //
			refreshPraent(this.parent);
		}
	}

	//�˳�!!!
	private void onExit() {
		setCloseType(2); //
		this.dispose(); //
		//�����´򿪲˵�ʵ��ˢ��  Ԭ����  20130313
		refreshPraent(this.parent);
	}

	//��д���׵Ĺر�ҳ�淽��
	@Override
	public void dispose() {
		super.dispose();
		refreshPraent(this.parent); //�ر�ҳ��ǰ����ˢ�¸�����ģ��
	}

	// �����´򿪲˵�ʵ��ˢ�� Ԭ���� 20130313 ȷ����ť�¼�
	public void refreshPraent(Container _parent) {
		if (_parent instanceof BillCardPanel) {
			BillCardPanel bcp = (BillCardPanel) _parent;
			BillVO billvo = bcp.getBillVO();// �Ȼ��ҳ���ֵ��Ҫ�������¼���
			bcp.reload(bcp.getTempletVO().getTempletcode());
			bcp.setBillVO(billvo);
			bcp.updateUI();
			String edittype = billvo.getEditType();//����ˢ�º��ҳ��״̬
			if (edittype != null && edittype.equals(WLTConstants.BILLDATAEDITSTATE_INIT)) {
			} else if (edittype != null && edittype.equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
				bcp.setEditableByEditInit();
				bcp.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
			} else if (edittype != null && edittype.equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
				bcp.setEditableByInsertInit();
				bcp.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
			}
		} else if (_parent instanceof BillListPanel) { // �����billlistpanel����Ҫˢ��
			//Ԭ���� 20130313
			//˼·��1���ȸ��²�ѯ����ģ��  2���ٸ���billlist��ģ��     3����ͳһˢ��billist�����ݣ�Ĭ��Ϊ�Զ�����    �������Ƿֱ������	
			BillListPanel billlist = (BillListPanel) _parent;
			String templete = billlist.getTempletVO().getTempletcode();
			//������Ҫ�����жϣ���Ϊ�е�ϵͳģ�岻�ܹ����أ�������Ҫ���жϸ�ģ���Ƿ����,Ϊ�˷��㲻���ⵥ���������������   20130410   Ԭ����
			int pageNo = billlist.getLi_currpage();
			int selectRow = billlist.getSelectedRow();
			if (null != templete && !templete.equals("") && billlist.getStr_realsql() != null && (!("1").equals(billlist.getIsRefreshParent()))) {//���һ��������ʾ�����billist����Ҫˢ�£���isRefreshParent����Ϊ1����
				billlist.getQuickQueryPanel().removeAll(); // ���ٲ�ѯ ���¼���
				billlist.getQuickQueryPanel().reload(templete);
				billlist.reload(); //billlist���¼���
				/*�������������ˢ�°�ť��  ���Ǻܶ���д�ڴ����е����������ȥ��	
				billlist.getBillListBtnPanel().removeAllButtons();  //�Ȱ�֮ǰ���е�buttonȥ�����¼���
				WLTButton []buttons=new WLTButton[billlist.getTempletVO().getListcustbtns().length];   
				//�Ȼ�ñ���ʱ������а�ť
				for(int i=0;i<buttons.length;i++){
					buttons[i]=new WLTButton(billlist.getTempletVO().getListcustbtns()[i]);
				}
				billlist.insertBatchBillListButton(buttons);
				billlist.repaintBillListButton();
				*/
				billlist.refreshData(); // �Զ���������
				if (billlist.getPageScrollable() && billlist.getLi_TotalRecordCount() != 0) {//���ֻ��0����¼�򲻽�����ת
					billlist.goToPage(pageNo);
				}
				billlist.setSelectedRow(selectRow);
			}
			/*if (billlist.getTempletVO() != null && billlist.getTempletVO().getAutoLoads() != 0) {
				billlist.refreshData(); // �Զ���������
			}*/
		} else if (_parent instanceof BillQueryPanel) { // ����һ�billquerypanel���Ҽ�--�༭����ģ��
			BillQueryPanel bqp = (BillQueryPanel) _parent;
			BillListPanel blp = bqp.getBillListPanel();
			String templeteCode = bqp.getTempletVO().getTempletcode(); // �Ȼ��ģ�����
			if (blp != null) {//����Ǳ�����billlistpanelΪ��
				blp.getQuickQueryPanel().removeAll();
				blp.getQuickQueryPanel().reload(templeteCode);
				blp.reload();
				blp.refreshData(); // �Զ���������
				/*if (bqp.getTempletVO() != null && bqp.getTempletVO().getAutoLoads() != 0) {
					blp.refreshData(); // �Զ���������
				}*/
			} else {//���Ϊ����
				bqp.removeAll();
				bqp.reload(templeteCode);
				bqp.updateUI();
			}
		} else if (_parent instanceof BillTreePanel) {

		} else if (_parent instanceof BillPropPanel) {
		}
	}

	//����˳��!!!
	private void resetShowOrder() {
		int li_rowcount = listPanel.getRowCount();
		for (int i = 0; i < li_rowcount; i++) {
			if (listPanel.getValueAt(i, "SHOWORDER") != null && Integer.parseInt("" + listPanel.getValueAt(i, "SHOWORDER")) != (i + 1)) {
				listPanel.setValueAt(new StringItemVO("" + (i + 1)), i, "SHOWORDER"); //
				if (!WLTConstants.BILLDATAEDITSTATE_INSERT.equals(listPanel.getRowNumberEditState(i))) {//ֻ����INIT��UPDATE״̬��������������������Ϊupdate״̬��insert������Ϊupdate�ͻ�������ݽ���ȥ��bug��
					listPanel.setRowStatusAs(i, WLTConstants.BILLDATAEDITSTATE_UPDATE); //����Ϊ�޸�״̬.	
				}
			}
		}
	}

	//ֹͣ�༭!!!
	private void stopEditing() {
		try {
			if (listPanel.getTable().getCellEditor() != null) {
				listPanel.getTable().getCellEditor().stopCellEditing(); //
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
