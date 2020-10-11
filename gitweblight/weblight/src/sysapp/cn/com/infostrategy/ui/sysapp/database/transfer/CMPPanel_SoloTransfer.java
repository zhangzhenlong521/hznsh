package cn.com.infostrategy.ui.sysapp.database.transfer;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.RefDialog_Table;

public class CMPPanel_SoloTransfer extends AbstractWorkPanel implements ActionListener {

	private static final long serialVersionUID = 4745452861980942703L;

	BillListPanel mainList = null;

	JComboBox comboSourceDs = null;//ԭ����Դ
	JComboBox comboDestDs = null; //Ŀ������Դ

	JTextField textSourceTBname = null;//ԭ���ݿ��
	JTextField textDestTBname = null;//Ŀ�����ݿ��
	JTextField externalCondition = null;//����SQL����

	WLTButton btn_refSourceTBname = null;
	WLTButton btn_refDestTBname = null;

	WLTButton btn_refAllMap = null;

	WLTButton btn_queryTableStruct = null;//��ѯԴ���ݿ��ṹ
	WLTButton btn_transferData = null;//ת������
	WLTButton btn_save = null;
	Map<String, String> id_column = null;

	@Override
	public void initialize() {
		mainList = new BillListPanel("PUB_DATA_TRANSFER_MAPPING_CODE1");
		mainList.add(this.getNorthPanel(), BorderLayout.NORTH);//��ӵ����Ϸ�

		btn_queryTableStruct = new WLTButton("��ѯԴ���ֶ�");
		btn_queryTableStruct.addActionListener(this);
		btn_transferData = new WLTButton("ת������");
		btn_transferData.addActionListener(this);
		btn_save = new WLTButton("����Ӧ��ϵ���浽���ݿ�");
		btn_save.addActionListener(this);
		mainList.addBatchBillListButton(new WLTButton[] { btn_queryTableStruct, btn_transferData, btn_save });
		mainList.repaintBillListButton();//��Ӱ�ť
		btn_transferData.setEnabled(false);
		this.add(mainList);

		this.setLayout(new BorderLayout());
		this.add(mainList, BorderLayout.CENTER);
	}

	public WLTPanel getNorthPanel() {//�õ��б���Ϸ������
		WLTPanel panel = new WLTPanel();
		panel.setLayout(new GridLayout(2, 10, 0, 2));
		WLTPanel top = new WLTPanel();
		top.setLayout(new GridLayout(0, 9));
		JLabel sourceDsPromot = new JLabel("*Դ���ݿ�");
		comboSourceDs = new JComboBox();
		top.add(sourceDsPromot);
		top.add(comboSourceDs);
		JLabel sourceTBPromot = new JLabel("     *Դ���ݿ��");
		top.add(sourceTBPromot);
		textSourceTBname = new JTextField(25);
		top.add(textSourceTBname);
		btn_refSourceTBname = new WLTButton("ѡ��Դ���ݿ��");
		btn_refSourceTBname.addActionListener(this);
		top.add(btn_refSourceTBname);//Դ

		JLabel addtion = new JLabel("       ����SQL����");
		top.add(addtion);
		externalCondition = new JTextField(100);
		externalCondition.setText("1=1 ");
		externalCondition.addActionListener(this);
		top.add(externalCondition);

		WLTPanel bottom = new WLTPanel();
		bottom.setLayout(new GridLayout(0, 9));

		JLabel destPromot = new JLabel("*Ŀ�����ݿ�");
		comboDestDs = new JComboBox();
		bottom.add(destPromot);
		bottom.add(comboDestDs);
		JLabel destTBPromot = new JLabel("     *Ŀ�����ݿ��");
		bottom.add(destTBPromot);
		textDestTBname = new JTextField(25);
		bottom.add(textDestTBname);
		btn_refDestTBname = new WLTButton("ѡ��Ŀ�����ݿ��");
		btn_refDestTBname.addActionListener(this);
		bottom.add(btn_refDestTBname);

		JLabel destTBPromot1 = new JLabel(" ");
		bottom.add(destTBPromot1);
		btn_refAllMap = new WLTButton("�ӹ�ϵ����ѡ��");
		btn_refAllMap.addActionListener(this);
		bottom.add(btn_refAllMap);

		DataSourceVO[] dsVos = this.getDataSourceVOS();
		if (dsVos == null) {//���û��ȡ������Դ
			MessageBox.show(this, "ERROR!");
			return null;
		}
		for (DataSourceVO dsvo : dsVos) {
			comboSourceDs.addItem(dsvo.getName());
			comboDestDs.addItem(dsvo.getName());
		}

		panel.add(top);
		panel.add(bottom);
		return panel;
	}

	public DataSourceVO[] getDataSourceVOS() {
		return ClientEnvironment.getInstance().getDataSourceVOs();
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btn_queryTableStruct)//��ѯ��ṹ
			onQueryTableStruct();
		else if (source == btn_transferData)//ת������
			onTransferData();
		else if (source == btn_refSourceTBname)//ѡ��Դ���ݿ��
			onSelectSourceTable(true);
		else if (source == btn_refDestTBname)
			onSelectSourceTable(false);
		else if (source == btn_save)
			onSaveMappingRelation();
		else if (source == externalCondition)
			changeExternalCondition();
		else if (source == btn_refAllMap)
			onShowAllMap();
	}

	private void onShowAllMap() {
		try {
			String[][] str_allTables = UIUtil.getStringArrayByDS(mainList.getDataSourceName(), "select source_dsname,source_table,dest_dsname,dest_table from pub_data_transfer_mapping group  by  source_dsname,source_table,dest_dsname,dest_table Order By source_table desc");
			TableDataStruct struct = new TableDataStruct(); //
			struct.setHeaderName(new String[] { "Դ���ݿ�", "Դ���ݿ��", "Ŀ�����ݿ�", "Ŀ�����ݿ��" }); //
			String[][] str_bodydata = new String[str_allTables.length][4]; // 
			for (int i = 0; i < str_allTables.length; i++) {
				str_bodydata[i][0] = str_allTables[i][0]; //
				str_bodydata[i][1] = str_allTables[i][1]; //
				str_bodydata[i][2] = str_allTables[i][2]; //
				str_bodydata[i][3] = str_allTables[i][3]; //
			}
			struct.setBodyData(str_bodydata); //

			//��ѡ���ĶԻ���
			RefDialog_Table refDialog = new RefDialog_Table(this, "���б��ϵ", null, null, struct); //
			refDialog.initialize();
			refDialog.setVisible(true); //

			//ȡ�÷���ֵ!!
			if (refDialog.getCloseType() == 1) {
				HashVO hashvo = refDialog.getReturnRefItemVO().getHashVO();
				comboSourceDs.setSelectedItem(hashvo.getStringValue("Դ���ݿ�"));
				comboDestDs.setSelectedItem(hashvo.getStringValue("Ŀ�����ݿ�"));
				textSourceTBname.setText(hashvo.getStringValue("Դ���ݿ��")); //
				textDestTBname.setText(hashvo.getStringValue("Ŀ�����ݿ��"));
				onQueryTableStruct();
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void changeExternalCondition() {
		externalCondition.setText("");
	}

	private void onSaveMappingRelation() {
		List<String> sqls = new ArrayList<String>();
		try {
			id_column = UIUtil.getHashMapBySQLByDS(mainList.getDataSourceName(), "select id,source_colname from pub_data_transfer_mapping");
		} catch (WLTRemoteException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		BillVO[] vos = mainList.getAllBillVOs();
		for (BillVO vo : vos) {
			String[] keys = vo.getKeys();
			String columns = "(";
			String values = "(";
			String updates = "";
			for (String key : keys) {
				if (key.equalsIgnoreCase("_RECORD_ROW_NUMBER")) {
					continue;
				}
				String value = vo.getStringValue(key);
				if (value == null)
					value = "  ";
				columns += key + ",";
				values += "'" + value + "',";
				updates += key + "='" + value + "' ,";
			}
			if (columns.endsWith(","))
				columns = columns.substring(0, columns.length() - 1);
			if (values.endsWith(","))
				values = values.substring(0, values.length() - 1);//ȥ�����Ķ���,
			if (updates.endsWith(","))
				updates = updates.substring(0, updates.length() - 1);
			columns += ")";
			values += ")";

			String sql = "";
			if (id_column.containsKey(vo.getStringValue("id"))) {//���ݿ����Ѿ������id�ˣ�update
				sql = "update " + vo.getSaveTableName() + " set " + updates + " where id = '" + vo.getStringValue("id") + "'";
			} else {
				sql = "insert into " + vo.getSaveTableName() + "  " + columns + " values " + values;
			}
			sqls.add(sql);
		}

		try {
			UIUtil.executeBatchByDS(mainList.getDataSourceName(), sqls);
			mainList.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT);
		} catch (Exception e) {
			MessageBox.show(this, "��������ˣ�");
			e.printStackTrace();
			return;
		}
		MessageBox.show(this, "�ѱ��棡");
	}

	/**ѡ��Դ���ݿ��
	 * @param isOld  : �Ƿ���������Դ
	 */
	private void onSelectSourceTable(boolean isOld) {//ѡ��Դ���ݿ��
		try {
			FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //

			String[][] str_allTables = null;//
			if (isOld) {//�����������Դ
				str_allTables = service.getAllSysTableAndDescr(this.getSourceDsName(), null, false, true);
			} else {
				str_allTables = service.getAllSysTableAndDescr(this.getDestDsName(), null, false, true);
			}

			TableDataStruct struct = new TableDataStruct(); //
			struct.setHeaderName(new String[] { "��/��ͼ����", "����", "˵��" }); //
			String[][] str_bodydata = new String[str_allTables.length][3]; // 
			for (int i = 0; i < str_allTables.length; i++) {
				str_bodydata[i][0] = str_allTables[i][0]; //
				str_bodydata[i][1] = str_allTables[i][1]; //
				str_bodydata[i][2] = str_allTables[i][2]; //
			}
			struct.setBodyData(str_bodydata); //

			//��ѡ���ĶԻ���
			RefDialog_Table refDialog = new RefDialog_Table(this, "���б�����ͼ", null, null, struct); //
			refDialog.initialize();
			refDialog.setVisible(true); //

			//ȡ�÷���ֵ!!
			if (refDialog.getCloseType() == 1) {
				String str_chooseTabName = refDialog.getReturnRefItemVO().getId(); //
				if (isOld)
					textSourceTBname.setText(str_chooseTabName);
				else
					textDestTBname.setText(str_chooseTabName);
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private void onQueryTableStruct() {//��ѯ��������ֶ�
		if (!this.checkValidate()) {//����������
			return;
		}
		mainList.clearTable();

		HashVO[] dbvos = null;
		try {
			id_column = UIUtil.getHashMapBySQLByDS(mainList.getDataSourceName(), "select id,source_colname from pub_data_transfer_mapping ");
			dbvos = UIUtil.getHashVoArrayByDS(mainList.getDataSourceName(), "select * from pub_data_transfer_mapping " + " where " + " source_dsname ='" + this.getSourceDsName() + "'  " + " and source_table = '" + this.textSourceTBname.getText() + "'  " + " and dest_dsname = '"
					+ this.getDestDsName() + "' " + " and dest_table = '" + this.textDestTBname.getText() + "' ");
		} catch (WLTRemoteException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String tableName = textSourceTBname.getText();
		TableDataStruct struct = null;
		try {
			struct = UIUtil.getTableDataStructByDS(this.getSourceDsName(), "select * from  " + tableName + "  where 1=2");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (struct == null) {
			MessageBox.show(this, "Դ���ݿ������!");
			return;
		}

		TableDataStruct struct_new = null;
		try {
			struct_new = UIUtil.getTableDataStructByDS(this.getDestDsName(), "select * from  " + this.textDestTBname.getText() + "  where 1=2");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (struct_new == null) {
			MessageBox.show(this, "Ŀ�����ݿ������!");
			return;
		}

		String[] colNames = struct.getHeaderName();
		String[] colTypes = struct.getHeaderTypeName();
		int[] colLength = struct.getHeaderLength();
		String[] isNull = struct.getIsNullAble(); //������Ҫ��ȡ����  ��ȡ����������ʾ�������һ����ֵΪN��Ϊ����

		String[] colNames_new = struct_new.getHeaderName();
		for (int i = 0; i < colNames.length; i++) {
			int newRow = mainList.addEmptyRow();
			mainList.setValueAt(new StringItemVO(this.getSourceDsName()), newRow, "source_dsname");
			mainList.setValueAt(new StringItemVO(tableName), newRow, "source_table");
			mainList.setValueAt(new StringItemVO(colNames[i]), newRow, "source_colname");
			mainList.setValueAt(new StringItemVO(colTypes[i]), newRow, "source_coltype");
			mainList.setValueAt(new StringItemVO(colLength[i] + ""), newRow, "source_collength");
			mainList.setValueAt(new StringItemVO(this.getDestDsName()), newRow, "dest_dsname");
			mainList.setValueAt(new StringItemVO(textDestTBname.getText()), newRow, "dest_table");

			String colName = this.getStringValueByColName(colNames[i], "DEST_COLNAME", dbvos);//����Ŀ�����ݿ���ֶ�
			if (colName != null) {
				mainList.setValueAt(new RefItemVO(colName, colName, colName), newRow, "DEST_COLNAME");
			} else {
				for (int j = 0; j < colNames_new.length; j++) {
					if (colNames[i].equalsIgnoreCase(colNames_new[j])) {
						mainList.setValueAt(new RefItemVO(colNames[i], colNames[i], colNames[i]), newRow, "DEST_COLNAME");
						break;
					}
				}
			}
			String temp_ispk = isNull[0];
			String isPk = this.getStringValueByColName(colNames[i], "ispk", dbvos);//���� �Ƿ�������
			//����һ����ֻ������һ����������������ֻ�жϵ�һ���������Ĳ����ж�
			if (newRow == 0) {//���Ϊ��һ�У��������Ϊ�գ�������Ϊ����������ֻ�жϵ�һ�У���Ϊһ��Ĭ�ϵ�һ���Ƿ�Ϊ����������������ݲ�����
				if (temp_ispk.equals("N")) {
					mainList.setValueAt(new ComBoxItemVO("Y", "Y", "Y"), newRow, "ispk");
				} else {
					mainList.setValueAt(new ComBoxItemVO("N", "N", "N"), newRow, "ispk");
				}
			} else {
				mainList.setValueAt(new ComBoxItemVO("N", "N", "N"), newRow, "ispk");
			}

			if (this.getStringValueByColName(colNames[i], "id", dbvos) == null) {
				try {
					mainList.setValueAt(new StringItemVO(UIUtil.getSequenceNextValByDS(this.getDestDsName(), mainList.getTempletVO().getPksequencename())), newRow, "id");
				} catch (WLTRemoteException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				mainList.setValueAt(new StringItemVO(this.getStringValueByColName(colNames[i], "id", dbvos)), newRow, "id");
				mainList.setRowStatusAs(newRow, WLTConstants.BILLDATAEDITSTATE_INIT);
			}
		}

		this.btn_transferData.setEnabled(true);
	}

	private String getStringValueByColName(String identifierColName, String itemKey, HashVO[] vos) {
		String value = null;
		if (identifierColName == null || "".equals(identifierColName) || vos == null)
			return null;
		for (HashVO vo : vos) {
			if (identifierColName.equals(vo.getStringValue("source_colname"))) {
				value = vo.getStringValue(itemKey);
				break;
			}
		}
		return value;
	}

	private void onTransferData() {//ת������
		if (this.hasRepeatDestColum()) {
			return;
		}
		boolean isCreateId = false;
		String newtablename = this.getNewTableName();
		//�ж��Ƿ��ж���ɱ����ݺϳ�һ���±�����
		String sql = "select source_table from pub_data_transfer_mapping  where dest_table='" + newtablename + "' group  by source_table,dest_table";
		try {
			String[] oldtables = UIUtil.getStringArrayFirstColByDS(mainList.getDataSourceName(), sql);
			if (oldtables != null && oldtables.length > 1) {
				isCreateId = true;//����ж���ɱ����ݺϳ�һ���±����ݣ��������ݵ������������������ɲ��У����Ҹ�����������Ϊ������������������Ҳ����ˡ������ı�һ���ǹ�ϵ��ֻΪ�˲�ѯ��
				StringBuffer sb_tb = new StringBuffer();
				for (int i = 0; i < oldtables.length; i++) {
					sb_tb.append(oldtables[i]);
					sb_tb.append(",");
				}

				if (MessageBox.confirm(this, "���±��Ӧ����ɱ�,�Ƿ�ɾ��Ŀ�����ݿ�����ݣ�\r\n[" + newtablename + "=>" + sb_tb.substring(0, sb_tb.length() - 1) + "]")) {
					sql = "delete from " + this.getNewTableName();
					try {
						UIUtil.executeUpdateByDS(this.getDestDsName(), sql);
					} catch (Exception e1) {
						MessageBox.show(this, "ɾ��Ŀ�����ݿ�������ִ��SQL���\n" + sql + "\n�ֶ�ɾ����");
						e1.printStackTrace();
						return;
					}
					MessageBox.show(this, "Ŀ�����ݿ����ɾ����");
				}
			} else {
				if (MessageBox.confirm(this, "Ϊ����id�ظ����Ƿ�ɾ��Ŀ�����ݿ������ݣ�")) {
					sql = "delete from " + this.getNewTableName();
					try {
						UIUtil.executeUpdateByDS(this.getDestDsName(), sql);
					} catch (Exception e1) {
						MessageBox.show(this, "ɾ��Ŀ�����ݿ�������ִ��SQL���\n" + sql + "\n�ֶ�ɾ����");
						e1.printStackTrace();
						return;
					}
					MessageBox.show(this, "Ŀ�����ݿ����ɾ����");
				}
			}
		} catch (Exception e1) {
			MessageBox.show(this, "ɾ��Ŀ�����ݿ�������ִ��SQL���\n" + sql + "\n�ֶ�ɾ����");
			e1.printStackTrace();
			return;
		}

		final List<String> sqls = this.getInsertSQL(isCreateId);
		if (sqls == null) {
			MessageBox.show(this, "Ǩ�����ݳ���û�еõ�����Insert��SQL");
			return;
		}

		new SplashWindow(this, "����Ǩ������,���Ժ󡤡���������", new AbstractAction() {
			private static final long serialVersionUID = -4578320890467615588L;

			public void actionPerformed(ActionEvent e) {
				try {
					UIUtil.executeBatchByDS(getDestDsName(), sqls);
					MessageBox.show("Ǩ�Ƴɹ�!");
				} catch (Exception e1) {
					e1.printStackTrace();
					MessageBox.show("Ǩ��ʧ��,��鿴�ͻ��˿���̨!");
				}
			}
		});

	}

	private boolean hasRepeatDestColum() {//����Ӧ��Ŀ���ֶ��Ƿ����ظ���
		boolean flag = false;
		BillVO[] vos = mainList.getAllBillVOs();
		for (BillVO out : vos) {
			if (flag)
				break;
			String name = out.getStringValue("DEST_COLNAME");
			if (name == null || "".equals(name.trim()))
				continue;
			for (BillVO in : vos) {
				if (in.getStringValue("id").equals(out.getStringValue("id")))//���id��ͬ��˵����ͬһ��
					continue;
				if (in.getStringValue("DEST_COLNAME") == null || "".equals(in.getStringValue("DEST_COLNAME").trim()))
					continue;
				if (name.equals(in.getStringValue("DEST_COLNAME"))) {//���ظ���
					flag = true;
					MessageBox.show(this, "��Ŀ���ֶ������ظ��ˣ��ظ�������Ϊ��" + name);
					break;
				}
			}
		}
		return flag;
	}

	private String getExternalCondition() {//�õ�����SQL����
		String condition = externalCondition.getText();
		if (condition == null || "".equals(condition)) {
			condition = "1=1";
		}
		return condition;
	}

	private List<String> getInsertSQL(boolean _isCreateId) {
		if (_isCreateId) {//�����Ҫ���´��������Ļ�����Ҫ�����ݶ�ȡ����ƴ��sql������ֱ����һ��sql�Ϳ�����
			return getInsertSQLs(true);
		}
		//�����������������������Ҫ�ж��Ƿ�������
		List<String> sqls = new ArrayList<String>();
		BillVO[] vos = mainList.getAllBillVOs();
		if (vos == null) {
			MessageBox.show(this, "û�ҵ��κζ�Ӧ��ϵ");
			return null;
		}
		try {
			//insert into cmp_cmpfile_wfopereq (id,operatepost,itsystem,operatereq,operatedesc,limits,noendureduty) select id,duty_id,itsystem,operaterequire,duty_info,limits,noendureduty from cmp_wf_operaterequire
			//insert into copy_xu.wltdual(c1) select c1 from gdnx_xu.wltdual

			DataSourceVO[] dsvs = ClientEnvironment.getInstance().getDataSourceVOs();
			DataSourceVO dsv_dest = null;
			DataSourceVO dsv_source = null;
			for (int ii = 0; ii < dsvs.length; ii++) {
				if (dsvs[ii].getName().equals(this.getDestDsName())) {
					dsv_dest = dsvs[ii];
				} else if (dsvs[ii].getName().equals(this.getSourceDsName())) {
					dsv_source = dsvs[ii];
				}
			}
			//DataSourceVO dsv_dest= ServerEnvironment.getInstance().getDataSourceVO(this.getDestDsName());
			//DataSourceVO dsv_source= ServerEnvironment.getInstance().getDataSourceVO(this.getSourceDsName());
			//System.out.println("dsv.getUser()======================="+dsv_dest.get);
			String url_dest = dsv_dest.getDburl();//Ŀ�����ݿ�URL
			String url_source = dsv_source.getDburl();//Ŀ�����ݿ�URL
			String dbName_dest = getDbNameFormURL(url_dest);
			String dbName_source = getDbNameFormURL(url_source);

			String insertState = "insert into " + dbName_dest + "." + this.getNewTableName() + " (";
			String valueState = " select ";

			for (BillVO vo : vos) {
				if (vo.getStringValue("DEST_COLNAME") != null && !"".equals(vo.getStringValue("DEST_COLNAME").trim()) && vo.getStringValue("SOURCE_COLNAME") != null && !"".equals(vo.getStringValue("SOURCE_COLNAME").trim())) {
					insertState += vo.getStringValue("DEST_COLNAME") + ",";
					valueState += vo.getStringValue("SOURCE_COLNAME") + ",";
				}
			}
			if (insertState.endsWith(","))
				insertState = insertState.substring(0, insertState.length() - 1);
			insertState += ") ";
			if (valueState.endsWith(","))
				valueState = valueState.substring(0, valueState.length() - 1);
			String sql = insertState + valueState + " from " + dbName_source + "." + this.getOldTableName();
			sqls.add(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sqls;
	}

	//jdbc:mysql://192.168.0.5:3000/gdnx_xu?characterEncoding=GBK  ��Ҫȡ��gdnx_xu
	private String getDbNameFormURL(String url_dest) {
		String temp = url_dest.substring(0, url_dest.indexOf("?"));
		String temp1 = temp.substring(temp.lastIndexOf("/") + 1, temp.length());
		return temp1;
	}

	private List<String> getInsertSQLs(boolean _isCreateId) {
		List<String> sqls = new ArrayList<String>();
		String pkColumn = null;//�����ݿ���е� �����ֶ�

		Map<String, String> new_old = new HashMap<String, String>();
		Map<String, String> dest_id_null = null;

		String getIdSql = "select id,'name' from " + this.getNewTableName();

		try {
			dest_id_null = UIUtil.getHashMapBySQLByDS(this.getDestDsName(), getIdSql);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		BillVO[] vos = mainList.getAllBillVOs();
		if (vos == null) {
			MessageBox.show(this, "û�ҵ��κζ�Ӧ��ϵ");
			return null;
		}
		for (BillVO vo : vos) {
			if (vo.getStringValue("ispk").equals("Y")) {
				pkColumn = vo.getStringValue("source_colname");
			}
			if (vo.getStringValue("DEST_COLNAME") != null && !"".equals(vo.getStringValue("DEST_COLNAME").trim()))
				new_old.put(vo.getStringValue("DEST_COLNAME"), vo.getStringValue("source_colname"));
		}
		if (pkColumn == null) {
			MessageBox.show(this, "��ѡ������");
			return null;
		}

		HashVO[] allOldTableData = null;

		String getDataSQL = "select * from " + this.getOldTableName() + " where " + this.getExternalCondition();
		try {
			allOldTableData = UIUtil.getHashVoArrayByDS(this.getSourceDsName(), getDataSQL);
			if (allOldTableData == null)
				return null;
			for (HashVO data : allOldTableData) {
				if (data == null) {
					continue;
				}
				if (dest_id_null != null) {//Ŀ�����ݿ���е�id��Ϊ��
					if (dest_id_null.containsKey(data.getStringValue(pkColumn))) {//Ŀ�����ݿ���а��������ݿ���е�id��id�ظ���
						MessageBox.show(this, "ID�ظ��ˣ��ظ���idΪ��" + data.getStringValue(pkColumn));
						return null;
					}
				}

				String insertState = "insert into " + this.getNewTableName() + " (";
				String valueState = "  values(";

				Iterator<Map.Entry<String, String>> new_old_it = new_old.entrySet().iterator();
				while (new_old_it.hasNext()) {
					Map.Entry<String, String> entry = new_old_it.next();
					if (entry.getKey() != null && !"".equals(entry.getKey().trim())) {
						String value = data.getStringValue(entry.getValue());
						if (value == null)
							continue;
						insertState += entry.getKey() + ",";
						if (_isCreateId && new_old.get(entry.getKey()).equalsIgnoreCase(pkColumn)) {//����ɶ��žɱ��������һ���±����ݣ������һ����������ĵ��붼��Ҫ���´�������
							String newid = UIUtil.getSequenceNextValByDS(this.getDestDsName(), "S_" + this.getNewTableName());
							valueState += "'" + newid + "',";
						} else {
							valueState += "'" + value + "',";
						}
					}
				}

				if (insertState.endsWith(","))
					insertState = insertState.substring(0, insertState.length() - 1);
				insertState += ")";
				if (valueState.endsWith(","))
					valueState = valueState.substring(0, valueState.length() - 1);
				valueState += ")";

				String sql = insertState + " " + valueState;
				sqls.add(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sqls;
	}

	public boolean checkValidate() {

		String sourceDsName = (String) comboSourceDs.getSelectedItem();
		if (sourceDsName == null) {
			MessageBox.show(this, "��ѡ��Դ���ݿ�!");
			return false;
		}
		String destDsName = (String) comboDestDs.getSelectedItem();
		if (destDsName == null) {
			MessageBox.show(this, "��ѡ��Ŀ�����ݿ�!");
			return false;
		}
		if (sourceDsName.equals(destDsName)) {
			MessageBox.show(this, "��Դ����Ŀ��ⲻ����ͬ!");
			return false;
		}
		String sourceTBName = textSourceTBname.getText();
		if (sourceTBName == null || "".equals(sourceTBName)) {
			MessageBox.show(this, "��ѡ��Դ���ݿ��");
			return false;
		}

		String destTBName = textDestTBname.getText();
		if (destTBName == null || "".equals(destTBName)) {
			MessageBox.show(this, "��ѡ��Ŀ�����ݿ��");
			return false;
		}
		return true;
	}

	private String getSourceDsName() {
		String name = (String) comboSourceDs.getSelectedItem();
		if ("".equals(name)) {
			name = null;
		}
		return name;
	}

	private String getDestDsName() {
		String name = (String) comboDestDs.getSelectedItem();
		if ("".equals(name)) {
			name = null;
		}
		return name;
	}

	private String getOldTableName() {
		return textSourceTBname.getText();
	}

	private String getNewTableName() {
		return textDestTBname.getText();
	}
}
