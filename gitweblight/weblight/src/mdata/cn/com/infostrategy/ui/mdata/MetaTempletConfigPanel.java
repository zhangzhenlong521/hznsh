package cn.com.infostrategy.ui.mdata;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.*;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.*;

/*
 * Ԫԭģ������..
 */
public class MetaTempletConfigPanel extends AbstractWorkPanel implements ActionListener {

	private static final long serialVersionUID = 3514696711209919711L;
	private JTextField search_text, search_table = null;
	private JComboBox search_box = null;

	private WLTButton btn_search, btn_config_dev, btn_config_app, btn_import, btn_delete, btn_copy, btn_compare, btn_importxml, btn_exportxml, btn_exportxml_all, btn_preview; //
	private BillListPanel blp_main;
	private FrameWorkMetaDataServiceIfc service = null;
	private final TextArea textArea=new TextArea();

	/**
	 * �Ƿ���ʵʩ��Ա����!!!
	 * @return
	 */
	public boolean isAppConfig() {
		if ("Y".equals(getMenuConfMapValueAsStr("�Ƿ�ʵʩ����"))) {
			return true; //
		}
		return false; //
	}

	public boolean isSelectPanel() {
		return false;
	}

	/**
	 * ��ʼ��ҳ��
	 */
	public void initialize() {

		this.setLayout(new BorderLayout());
		this.add(getNorthPanel(), BorderLayout.NORTH); //����İ�ť��!
		try {
			blp_main = new BillListPanel(new DefaultTMO("����ģ��", new String[][] { { "pk_pub_templet_1", "����", "100", "N" }, //
					{ "templetcode", "ģ�����", "220", "Y" }, //
					{ "templetname", "ģ������", "150", "Y" },//
					{ "tablename", "��ѯ����", "150", "Y" }, //
					{ "savedtablename", "�������", "150", "Y" }, //
					{ "datapolicy", "����Ȩ�޲���", "150", "Y" }, //����Ȩ�޲��Ժܹؼ�,������б���һ���壡
					{ "savetype", "��Դ", "150", "Y" } })); //
			this.add(blp_main, BorderLayout.CENTER); ////
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private JPanel getNorthPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT)); ////
		JLabel label = new JLabel("ģ�����/����:", SwingConstants.RIGHT); //
		search_text = new JTextField(); //
		search_text.setPreferredSize(new Dimension(150, 20)); //
		JLabel label_table = new JLabel("����:", SwingConstants.RIGHT);
		search_table = new JTextField(); //
		search_table.setPreferredSize(new Dimension(100, 20));
		search_box = new JComboBox(new String[] { "DB", "XML", "ALL" });
		search_box.setPreferredSize(new Dimension(50, 20));
		btn_search = new WLTButton("��ѯ"); //
		btn_import = new WLTButton("����"); //
		btn_config_dev = new WLTButton("��������"); //
		btn_config_app = new WLTButton("ʵʩ����"); //
		btn_delete = new WLTButton("ɾ��"); //
		btn_copy = new WLTButton("����"); //
		btn_compare = new WLTButton("�Ա�");
		btn_importxml = new WLTButton("����XML"); //
		btn_exportxml = new WLTButton("����XML"); //
		btn_exportxml_all = new WLTButton("����XML(ȫ��)");
		btn_preview = new WLTButton("Ԥ��"); //Ԥ�� �����/2013-03-25��

		btn_search.addActionListener(this); //
		btn_import.addActionListener(this); //
		btn_config_dev.addActionListener(this); //
		btn_config_app.addActionListener(this); //
		btn_delete.addActionListener(this); //
		btn_copy.addActionListener(this); //
		btn_compare.addActionListener(this);
		btn_importxml.addActionListener(this); //
		btn_exportxml.addActionListener(this); //
		btn_exportxml_all.addActionListener(this);
		btn_preview.addActionListener(this);

		panel.add(label); //
		panel.add(search_text); //
		panel.add(label_table);
		panel.add(search_table);
		panel.add(search_box); //
		panel.add(btn_search); //
		if (isAppConfig()) { //�����ʵʩ��Ա����,��ֻҪ�ð�ť
			panel.add(btn_config_app); //
			panel.add(btn_exportxml); //
			panel.add(btn_preview);
		} else if (!isSelectPanel()) {
			panel.add(btn_import); //
			panel.add(btn_config_dev); //
			panel.add(btn_delete); //
			panel.add(btn_copy); //
			panel.add(btn_compare);
			panel.add(btn_importxml); //
			panel.add(btn_exportxml); //
			panel.add(btn_exportxml_all);
			panel.add(btn_preview);
		}
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_search) {
			onSearch(); //
		} else if (e.getSource() == btn_import) {
			onReference(); //�ӱ���!
		} else if (e.getSource() == btn_config_dev) {
			onConfig(false); //
		} else if (e.getSource() == btn_config_app) {
			onConfig(true); //ʵʩ����!
		} else if (e.getSource() == btn_delete) {
			onDelete(); //ɾ��
		} else if (e.getSource() == btn_copy) {
			onCopy(); //����
		} else if (e.getSource() == btn_importxml) {
			onImportXMLTemplet(); //����XML
		} else if (e.getSource() == btn_exportxml) {
			onExportXMLTemplet(); //����XML
		} else if (e.getSource() == btn_exportxml_all) {
			onExportXMLTempletAll(); //����XML
		} else if (e.getSource() == btn_compare) {
			onCompare(); //�Ƚ�
		} else if (e.getSource() == btn_preview) {
			onPreview(); //Ԥ��
		}
	}

	//	private String getQueryCols() {
	//		return " pk_pub_templet_1,templetcode,templetname,tablename,savedtablename "; //
	//	}

	private void onCompare() {
		int[] li_ = blp_main.getTable().getSelectedRows();
		if (li_ == null || li_.length <= 1) {
			JOptionPane.showMessageDialog(this, "��ѡ��������¼���бȽ�!");
			return;
		}
		String code1 = blp_main.getRealValueAtModel(li_[0], "TEMPLETCODE");
		String code2 = blp_main.getRealValueAtModel(li_[1], "TEMPLETCODE");
		String t1 = blp_main.getRealValueAtModel(li_[0], "savetype");
		String t2 = blp_main.getRealValueAtModel(li_[1], "savetype");
		int type1 = 0, type2 = 0;
		if (!t1.equals("���ݿ�")) {
			//ֱ�Ӵ�xml·��ȡ �����/2013-03-25��
			if (t1 != null && !t1.equals("") && t1.indexOf("XML_") > 0) {
				code1 = "/" + t1.substring(t1.indexOf("XML_") + 4);
				type1 = 3;
			} else {
				type1 = 1;
			}
		}
		if (!t2.equals("���ݿ�")) {
			//ֱ�Ӵ�xml·��ȡ �����/2013-03-25��
			if (t2 != null && !t2.equals("") && t2.indexOf("XML_") > 0) {
				code2 = "/" + t2.substring(t2.indexOf("XML_") + 4);
				type2 = 3;
			} else {
				type2 = 1;
			}
		}
		TempletCompareDialog cd = new TempletCompareDialog(blp_main, code1, type1, code2, type2);//�������еıȽϹ���
		cd.getBtnp().setVisible(false);
		cd.setVisible(true);
	}

	/**
	 * ��ѯ
	 */
	public void onSearch() {

		FrameWorkMetaDataServiceIfc service = null;
		String str_text = search_text.getText();
		String str_table = search_table.getText();
		try {
			service = getService();
			List<Object> list = service.getAllTemplate(str_text, str_table, search_box.getSelectedItem().toString());
			HashVO[] vos = (HashVO[]) list.get(0);
			HashMap datapolicyMap = UIUtil.getHashMapBySQLByDS(null, "select id,name from pub_datapolicy"); //
			for (int i = 0; i < vos.length; i++) {
				String str_policyId = vos[i].getStringValue("datapolicy"); //
				if (datapolicyMap.containsKey(str_policyId)) {
					vos[i].setAttributeValue("datapolicy", (String) datapolicyMap.get(str_policyId)); //
				}
			}
			blp_main.putValue(vos);
			blp_main.getLabel_pagedesc().setText("��" + vos.length + "��");
			blp_main.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT);
			blp_main.revalidate();
			if (list.get(1) != null && !"".equals(list.get(1).toString().trim())) {
				MessageBox.showWarn(blp_main, list.get(1).toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(blp_main, e.fillInStackTrace());
		}
		//		if (str_text == null || str_text.trim().equals("")) {
		//			blp_main.QueryData("select " + getQueryCols() + " from pub_templet_1 order by templetcode asc"); //
		//		} else {
		//			blp_main.QueryData("select " + getQueryCols() + " from pub_templet_1 where lower(templetcode) like '%" + str_text.trim().toLowerCase() + "%' or lower(templetname) like '%" + str_text.trim().toLowerCase() + "%' order by templetcode asc"); //
		//		}
	}

	/**
	 * ����!!
	 */
	private void onConfig(boolean _isAppConf) {
		BillVO billVO = blp_main.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String str_templetCode = billVO.getStringValue("templetcode"); //
		if ("���ݿ�".equals(billVO.getStringValue("savetype"))) {
			TempletModify2 mfdialog = new TempletModify2(this, str_templetCode, _isAppConf); //��������!!
		} else {
			//ֱ�Ӵ�xml·��ȡ �����/2013-03-25��
			String savetype = billVO.getStringValue("savetype");
			if (savetype != null && !savetype.equals("") && savetype.indexOf("XML_") > 0) {
				String xml_url = "/" + savetype.substring(savetype.indexOf("XML_") + 4);
				TempletModify2 mfdialog = new TempletModify2(this, str_templetCode, xml_url, _isAppConf, true); //��������!!
			} else {
				TempletModify2 mfdialog = new TempletModify2(this, str_templetCode, _isAppConf, true); //��������!!
			}
		}
	}

	/**
	 * ����!
	 */
	private void onReference() {
		SelectTableDialog refTableDialog = new SelectTableDialog(this, "����һ�������ͼ�Ľṹ���ٴ���ģ��"); //ѡ���
		refTableDialog.setVisible(true);
		if (refTableDialog.getCloseType() == 1) {
			search_text.setText(refTableDialog.getReturnTempletCode()); //
			onSearch(); //
		}
	}

	/**
	 * ɾ��!
	 */
	private void onDelete() {
		BillVO billVO = blp_main.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		if (!"���ݿ�".equals(billVO.getStringValue("savetype"))) {
			MessageBox.show(this, "�����ݿ�ģ���޷�ֱ��ɾ��!");
			return;
		}

		String str_pk_pub_templet_1 = billVO.getStringValue("pk_pub_templet_1"); //
		String str_templetCode = billVO.getStringValue("templetcode"); //
		String str_templetName = billVO.getStringValue("templetname"); //
		if (!MessageBox.confirm(this, "��ȷ��Ҫɾ��ģ�塾" + str_templetCode + "/" + str_templetName + "����?")) { ////
			return; //
		}
		String str_sql_1 = "delete from pub_templet_1_item where pk_pub_templet_1='" + str_pk_pub_templet_1 + "'"; //
		String str_sql_2 = "delete from pub_templet_1      where pk_pub_templet_1='" + str_pk_pub_templet_1 + "'"; //
		try {
			UIUtil.executeBatchByDS(null, new String[] { str_sql_1, str_sql_2 }); //
			blp_main.removeRow(true); //
			MessageBox.show(this, "ɾ��ģ��ɹ�!"); //
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}
	//�õ�ѡ�������
	
	public BillVO getSelectBillVO() {
		return blp_main.getSelectedBillVO();
	}

	/**
	 * ����!!!
	 */
	private void onCopy() {
		int li_count = blp_main.getTable().getSelectedRowCount();
		if (li_count <= 0) {
			MessageBox.showSelectOne(this);
			return;
		}
		int selected_rows = blp_main.getTable().getSelectedRow();
		String str_oldparent_id = blp_main.getRealValueAtModel(selected_rows, "PK_PUB_TEMPLET_1"); // ԭ����¼������!!
		String str_oldtablename = blp_main.getRealValueAtModel(selected_rows, "TABLENAME"); // ԭ����ѯ����!!!�����/2012-07-13��
		String str_oldparentcode = blp_main.getRealValueAtModel(selected_rows, "TEMPLETCODE"); // ԭ��ģ�����!!!
		String str_oldparentname = blp_main.getRealValueAtModel(selected_rows, "TEMPLETNAME"); // ԭ��ģ����!!!

		ShowCopyTempleteDialog showCopyTempleteDialog = new ShowCopyTempleteDialog(this, str_oldtablename, str_oldparentcode, str_oldparentname);
		showCopyTempleteDialog.setVisible(true);

		try {
			if (showCopyTempleteDialog.getCloseType() == 0) {
				Vector v_sqls = new Vector();
				String str_temp_code = showCopyTempleteDialog.getTempleteCode(); // �±���
				String str_tem_name = showCopyTempleteDialog.getTempleteName(); // ������

				String fromtype = blp_main.getRealValueAtModel(selected_rows, "savetype");
				if (fromtype != null && fromtype.indexOf("XML") > 0) {
					try {
						FrameWorkMetaDataServiceIfc service = getService(); //
						HashMap p = new HashMap();
						p.put("pub_templet_1_templetcode", str_temp_code);
						p.put("pub_templet_1_templetname", str_tem_name);
						service.importRecordsXMLToTable(new String[] { "/" + fromtype.substring(fromtype.indexOf("_") + 1) }, null, true, p);
						JOptionPane.showMessageDialog(this, "����ģ�������!");
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(this, "����ģ�����!");
					}
					return;
				}

				HashVOStruct vos_parent = UIUtil.getHashVoStructByDS(null, "select * from pub_templet_1 where pk_pub_templet_1='" + str_oldparent_id + "'");
				String[] str_headname = vos_parent.getHeaderName(); //

				String str_newparentid = null; //
				StringBuffer sb_1 = new StringBuffer();
				sb_1.append("insert into pub_templet_1 ");
				sb_1.append("( ");
				for (int i = 0; i < str_headname.length; i++) {
					sb_1.append(str_headname[i]);
					if (i != str_headname.length - 1) {
						sb_1.append(","); //
					}
				}

				sb_1.append(") select ");
				for (int i = 0; i < str_headname.length; i++) { //
					if (str_headname[i].equalsIgnoreCase("pk_pub_templet_1")) { //����
						str_newparentid = UIUtil.getSequenceNextValByDS(null, "s_pub_templet_1");
						sb_1.append("'" + str_newparentid + "'");
					} else if (str_headname[i].equalsIgnoreCase("templetcode")) { //�±���
						sb_1.append("'" + str_temp_code + "'");
					} else if (str_headname[i].equalsIgnoreCase("templetname")) { //������
						sb_1.append("'" + str_tem_name + "'");
					} else { //ֱ�ӿ���ԭ����
						sb_1.append(str_headname[i]);
					}
					if (i != str_headname.length - 1) {
						sb_1.append(","); //
					}
				}
				sb_1.append(" from pub_templet_1 where pk_pub_templet_1='" + str_oldparent_id + "'"); //

				v_sqls.add(sb_1.toString()); //

				HashVOStruct childHVOStruct = UIUtil.getHashVoStructByDS(null, "select * from pub_templet_1_item where pk_pub_templet_1='" + str_oldparent_id + "'");
				String[] childHeaderNames = childHVOStruct.getHeaderName(); //
				HashVO[] childHVOs = childHVOStruct.getHashVOs(); //

				for (int i = 0; i < childHVOs.length; i++) { //�������м�¼
					StringBuffer sb_2 = new StringBuffer();
					sb_2.append("insert into pub_templet_1_item ");
					sb_2.append("( ");
					for (int j = 0; j < childHeaderNames.length; j++) {
						sb_2.append(childHeaderNames[j]);
						if (j != childHeaderNames.length - 1) {
							sb_2.append(","); //
						}
					}

					sb_2.append(") select "); ////
					for (int j = 0; j < childHeaderNames.length; j++) { //
						if (childHeaderNames[j].equalsIgnoreCase("pk_pub_templet_1_item")) { //����
							sb_2.append("'" + UIUtil.getSequenceNextValByDS(null, "s_pub_templet_1_item") + "'");
						} else if (childHeaderNames[j].equalsIgnoreCase("pk_pub_templet_1")) { //����¼����
							sb_2.append("'" + str_newparentid + "'");
						} else { //ֱ�ӿ���ԭ����
							sb_2.append(childHeaderNames[j]);
						}
						if (j != childHeaderNames.length - 1) {
							sb_2.append(","); //
						}
					}
					sb_2.append(" from pub_templet_1_item where pk_pub_templet_1_item='" + childHVOs[i].getStringValue("pk_pub_templet_1_item") + "'"); //

					v_sqls.add(sb_2.toString());
				}
				UIUtil.executeBatchByDS(null, v_sqls); ////
				JOptionPane.showMessageDialog(this, "����ģ�������!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "��������ģ�����!");
		}
	}

	/**
	 * ��һ��XML�е���ģ��!!!
	 */
	private void onImportXMLTemplet() {//����XML��ʽģ��
				final BillDialog dialog = new BillDialog(this, "����XML", 1000, 700);  //
				dialog.setLayout(new BorderLayout());
				textArea.setBackground(Color.WHITE); //
				textArea.setForeground(Color.BLUE); //
				textArea.setFont(new Font("����", Font.PLAIN, 12));
				textArea.select(0, 0); //
				JPanel jp = new JPanel();
				jp.setLayout(new FlowLayout(FlowLayout.CENTER));
				WLTButton confirm = new WLTButton("ȷ��");
				WLTButton cancel = new WLTButton("ȡ��");
				confirm.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							if (textArea == null || textArea.getText().trim().equals("") || textArea.getText() == null) {
								MessageBox.show("��������Ҫ�����ģ��!!");
								return;
							}
							FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
							service.importXMLTemplet(null, textArea.getText()); //����ģ��!!!
							MessageBox.show(MetaTempletConfigPanel.this, "����XML��ʽģ��ɹ�!!!");
						} catch (Exception ex) {
							ex.printStackTrace(); //
						}
						textArea.setText("");
						dialog.dispose();
					}
				});
				cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dialog.dispose();

					}
				});  //
				jp.add(confirm);
				jp.add(cancel);
				dialog.getContentPane().add(textArea, BorderLayout.CENTER); //
				dialog.getContentPane().add(jp, BorderLayout.SOUTH);
				dialog.setVisible(true); //
	}

	/**
	 * ��ģ�嵼����XML(����)
	 */
	private void onExportXMLTemplet() {//����XML��ʽģ��
		int row = blp_main.getSelectedRow();
		if (row < 0) {
			MessageBox.showSelectOne(this);
			return;
		}

		String templetCode = blp_main.getValueAt(row, "templetcode").toString();
		String savetype = blp_main.getValueAt(row, "savetype").toString();
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("���浽");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);

		int result = fc.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) { //�����ȷ����
			String savePath = fc.getSelectedFile().getPath() + "\\";
			boolean rs = false;
			if (savetype != null && savetype.indexOf("XML") >= 0) {
				rs = doExportXMLTemplet(savePath, templetCode, "/" + savetype.substring(savetype.indexOf("_") + 1));
			} else {
				rs = doExportXMLTemplet(savePath, templetCode, null);
			}
			if (rs) {
				MessageBox.show(this, "ģ��ɹ������� [" + savePath + templetCode + ".xml]");
			}

		}
	}

	/**
	 * ��ģ�嵼����XML(������ȫ��)
	 */
	private void onExportXMLTempletAll() {
		//String savePath = "";
		//String templetCode = "";
		final int rowCount = blp_main.getRowCount();
		if (rowCount <= 0) {
			MessageBox.showSelectOne(this);
			return;
		}

		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("���浽");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		int result = fc.showSaveDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) { //�����ȷ����
			final String savePath = fc.getSelectedFile().getPath() + "\\";
			new SplashWindow(this, "���ڵ���,��ȴ�...", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					try {
						String savetype = null;
						for (int i = 0; i < rowCount; i++) {
							savetype = blp_main.getValueAt(i, "savetype").toString();
							if (savetype != null && savetype.indexOf("XML") >= 0) {
								doExportXMLTemplet(savePath, blp_main.getValueAt(i, "templetcode").toString(), "/" + savetype.substring(savetype.indexOf("_") + 1));
							} else {
								doExportXMLTemplet(savePath, blp_main.getValueAt(i, "templetcode").toString(), null);
							}
							((SplashWindow) e.getSource()).setWaitInfo("���ڵ���,��ȴ�...[" + (i + 1) + "/" + rowCount + "]");
						}
					} catch (Exception _ex) {
						SplashWindow.closeSplashWindow();
						_ex.printStackTrace();
						MessageBox.show(blp_main, "����ʧ��!");
						return;
					}
					MessageBox.show(blp_main, "ģ��ɹ������� [" + savePath + "], " + "�ϼ�[" + rowCount + "]��");
				}
			});

		}
	}

	private boolean doExportXMLTemplet(String savePath, String templetCode, String xmlFile) {
		boolean ret = false;
		if (TBUtil.isEmpty(templetCode)) {
			return false;
		}
		try {
			FrameWorkMetaDataServiceIfc service = getService(); //
			String str_xml = null;
			if (xmlFile != null && !"".equals(xmlFile.trim())) {
				str_xml = service.getXMlFromTableRecords(null, null, null, null, xmlFile);
			} else {
				str_xml = service.getXMlFromTableRecords(null, new String[] { "select * from pub_templet_1 where templetcode='" + templetCode + "'", //�������������!����һ��ͨ�õ����ܵĵ������б�ṹ��
						"select * from pub_templet_1_item where pk_pub_templet_1 in (select pk_pub_templet_1 from pub_templet_1 where templetcode='" + templetCode + "') order by showorder" }, ////�ӱ����������!
						new String[][] { { "pub_templet_1", "pk_pub_templet_1", "S_PUB_TEMPLET_1" }, { "pub_templet_1_item", "pk_pub_templet_1_item", "S_PUB_TEMPLET_1_ITEM" } }, //�����ֶ�Լ��!! 
						new String[][] { { "pub_templet_1_item.pk_pub_templet_1", "pub_templet_1.pk_pub_templet_1" } }, null //���Լ��!!
						);
			}
			File saveToFile = new File(savePath + templetCode + ".xml");
			new TBUtil().writeBytesToOutputStream(new FileOutputStream(saveToFile, false), str_xml.getBytes("UTF-8")); //д�ļ�!!
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	//Ԥ�� �����/2013-03-25��
	public void onPreview() {
		BillVO billVO = blp_main.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String str_templetCode = billVO.getStringValue("templetcode");
		if ("���ݿ�".equals(billVO.getStringValue("savetype"))) {
			BillCardDialog billCardDialog = new BillCardDialog(this, str_templetCode, new BillCardPanel(str_templetCode), WLTConstants.BILLDATAEDITSTATE_INIT);
			billCardDialog.setVisible(true);
		} else {
			try {
				AbstractTMO tmo = null;
				String savetype = billVO.getStringValue("savetype");
				if (savetype != null && !savetype.equals("") && savetype.indexOf("XML_") > 0) {
					tmo = UIUtil.getMetaDataService().getDefaultTMOByCode("/" + savetype.substring(savetype.indexOf("XML_") + 4), 3);
				} else {
					tmo = UIUtil.getMetaDataService().getDefaultTMOByCode(str_templetCode, 1);
				}
				BillCardDialog billCardDialog = new BillCardDialog(this, str_templetCode, new BillCardPanel(tmo), WLTConstants.BILLDATAEDITSTATE_INIT);
				billCardDialog.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public FrameWorkMetaDataServiceIfc getService() throws Exception {
		if (service == null) {
			service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class);
		}
		return service;
	}

	public JTextField getSearch_text() {
		return search_text;
	}

	public void setSearch_text(JTextField search_text) {
		this.search_text = search_text;
	}

	public JTextField getSearch_table() {
		return search_table;
	}

	public void setSearch_table(JTextField search_table) {
		this.search_table = search_table;
	}

	public JComboBox getSearch_box() {
		return search_box;
	}

	public void setSearch_box(JComboBox search_box) {
		this.search_box = search_box;
	}

}
