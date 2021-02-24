package com.pushworld.ipushgrc.ui.tools;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
public class ExportDBTableToXml extends AbstractWorkPanel implements ActionListener {

	private JComboBox dsComBobox_export = null; //����Դ!!!
	private JTextArea textArea = null; //
	private WLTButton btn_custtables = null; //

	private JComboBox dsComBobox_import = null; //����ʱѡ�������Դ!!!
	private WLTButton btn_import = null; //

	private TBUtil tbUtil = new TBUtil(); //

	private FrameWorkMetaDataServiceIfc services = null; //

	public void initialize() {
		DataSourceVO[] dsVOs = ClientEnvironment.getInstance().getDataSourceVOs(); //��������Դ!!!
		dsComBobox_export = new JComboBox(); //
		for (int i = 0; i < dsVOs.length; i++) {
			dsComBobox_export.addItem(dsVOs[i].getName()); //����Դ����!!
		}
		dsComBobox_export.setBounds(10, 10, 95, 20); //

		textArea = new JTextArea("cmp_issue,cmp_event,cmp_ward,cmp_check_item"); //
		textArea.setFont(LookAndFeel.font); ////
		textArea.setLineWrap(true); //
		JScrollPane scrollPanel = new JScrollPane(textArea); //
		scrollPanel.setBounds(10, 40, 500, 150); //

		btn_custtables = new WLTButton("����ָ����");
		btn_custtables.setBounds(520, 40, 125, 20); //
		btn_custtables.addActionListener(this); //

		JPanel panel_1 = WLTPanel.createDefaultPanel(null); //
		panel_1.add(dsComBobox_export); //
		panel_1.add(scrollPanel); //
		panel_1.add(btn_custtables); //

		//����ҳǩ!!
		dsComBobox_import = new JComboBox(); //
		for (int i = 0; i < dsVOs.length; i++) {
			dsComBobox_import.addItem(dsVOs[i].getName()); //����Դ����!!
		}
		dsComBobox_import.setBounds(10, 10, 100, 20); //

		btn_import = new WLTButton("����XML�ļ�"); //
		btn_import.setBounds(120, 10, 125, 20); //
		btn_import.addActionListener(this); //

		JPanel panel_2 = WLTPanel.createDefaultPanel(null); //
		panel_2.add(dsComBobox_import); //
		panel_2.add(btn_import); //

		JTabbedPane tabbedPanel = new JTabbedPane(); //
		tabbedPanel.addTab("����Ӧ��", panel_1); //
		tabbedPanel.addTab("����Ӧ��", panel_2); //

		this.setLayout(new BorderLayout()); //
		this.add(tabbedPanel); //
	}

	public void actionPerformed(ActionEvent e) {
	     if (e.getSource() == btn_custtables) { //����ָ����!!
			String str_text = textArea.getText(); //
			if (str_text == null || str_text.trim().equals("")) {
				JOptionPane.showMessageDialog(this, "�������"); //
				return; //
			}
			str_text = str_text.trim(); //
			String[] str_tables = tbUtil.split(str_text, ","); //
			String[][] str_tabPKs = new String[str_tables.length][2]; //
			for (int i = 0; i < str_tables.length; i++) {
				String str_tabName = str_tables[i]; //
				String str_pk = "id"; //
				if (str_tabName.indexOf("(") > 0) { //���������,��ָ����������
					str_tabName = str_tables[i].substring(0, str_tables[i].indexOf("(")); //����!
					str_pk = str_tables[i].substring(str_tables[i].indexOf("(") + 1, str_tables[i].indexOf(")")); //�����ֶ���!
					if (str_pk.trim().equals("")) {
						str_pk = null;
					}
				}
				//System.out.println("����[" + str_tabName + "],����[" + str_pk + "]"); ///
				str_tabPKs[i] = new String[] { str_tabName, str_pk }; //
			}
			onExportXml(str_tabPKs); //
			if(MessageBox.confirm(this, "���ݵ����ɹ����Ƿ����ԭ�����ݣ�")){
				List delSql = new ArrayList();
				try {
					dumpTable(null,new String []{"cmp_issue","cmp_event","cmp_ward","cmp_check_item"},"temp");
				} catch (Exception e2) {
					e2.printStackTrace();
				}
				delSql.add("delete from cmp_issue where 1=1 ");
				delSql.add("delete from cmp_event where 1=1 ");
				delSql.add("delete from cmp_ward where 1=1 ");
				delSql.add("delete from cmp_check_item where 1=1 ");
				try {
					UIUtil.executeBatchByDS(null, delSql);
					MessageBox.show(this, "���ݱ��ݳɹ���");
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} else if (e.getSource() == btn_import) { //����!!!
			onImportXml(); //
		}
	}
	public void dumpTable(String dbName, String tableName[], String dumpName) throws Exception {
		String[][] str_allTables = UIUtil.getCommonService().getAllSysTableAndDescr(null, null, false, true); //
		List createList= new ArrayList();
		for (int j = 0; j < tableName.length; j++) {
			String tempTableName = tableName[j]+"_"+dumpName;
			for (int i = 0; i < str_allTables.length; i++) {
				
				if (tempTableName.equalsIgnoreCase(str_allTables[i][0])) {
					// ����ҵ��˱��ݱ���ô��ɾ�����ݱ����±���
					String dropTableSql = "drop table " + tempTableName;
					UIUtil.executeUpdateByDS(dbName, dropTableSql);
					// ɾ���ɹ��ڱ���
					break;
				}
			}
			String createTableSql = "create table " + tempTableName + " as select * from " + tableName[j];
			createList.add(createTableSql);
		}
		// �ر���
	
		UIUtil.executeBatchByDS(dbName, createList);
		// ���ݳɹ���
	}
	private void onExportXml(final String[][] _tablePKs) {
		if (JOptionPane.showConfirmDialog(this, "�������ִ�д˵�������ô?!", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}
		//�����ļ�ѡ���!ѡ��һ��·��!!!
		final String str_path = getChooseDir(); //
		if (str_path == null) {
			return;
		}
		long ll_begin = System.currentTimeMillis();
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				export((SplashWindow) e.getSource(), str_path, _tablePKs); //����
			}
		}, 600, 125, 300, 300); ///
		long ll_end = System.currentTimeMillis();
		JOptionPane.showMessageDialog(this, "�������,�ܹ���ʱ[" + ((ll_end - ll_begin) / 1000) + "]��!!"); //
	}

	private String getChooseDir() {
		JFileChooser chooser = new JFileChooser(new File("C:/"));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("ѡ��һ��Ŀ¼!"); //
		int li_rewult = chooser.showSaveDialog(this);
		if (li_rewult == JFileChooser.CANCEL_OPTION) {
			return null;// ���ȡ��ʲôҲ����
		}
		if (chooser.getSelectedFile().isFile()) {
			MessageBox.show(this, "����ѡ��һ��Ŀ¼,�������ļ�!!"); //
			return null;
		}
		String str_pathdir = chooser.getSelectedFile().getAbsolutePath(); // ;
		if (str_pathdir.endsWith("\\")) {
			str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1); //
		}
		return str_pathdir; //
	}

	/**
	 * ����!!!
	 * @param _splash
	 * @param _path
	 * @param _tablePKs
	 */
	private void export(SplashWindow _splash, String _path, String[][] _tablePKs) {
		try {
			for (int i = 0; i < _tablePKs.length; i++) { //�������б�!
				String str_tableName = _tablePKs[i][0]; //����
				String str_pkName = _tablePKs[i][1]; //������

				//����������Ŀ¼!!!
				String str_newPath = _path + "\\" + str_tableName; //���ϱ���!
				File filenewdir = new File(str_newPath); //
				if (!filenewdir.exists()) {
					filenewdir.mkdirs(); //����Ŀ¼!!!�Ժ��ٸ����ǰ�
				}
				String str_daName = (String) dsComBobox_export.getSelectedItem(); //

				int li_beginNo = 0; //��ʼ��!��Ϊ�и���,������Ҫ��һ��!
				if (str_pkName != null) {
					String str_minValue = UIUtil.getStringValueByDS(str_daName, "select min(" + str_pkName + ") from " + str_tableName); //
					if (str_minValue != null && !str_minValue.equals("")) {
						//System.out.println(str_pkName + "," + str_tableName);  //
						li_beginNo = Integer.parseInt(str_minValue)-1; //����Сֵ!!!
					}
				}
				int li_cout = 0; //
				int li_countall = Integer.parseInt(UIUtil.getStringValueByDS(str_daName, "select count(*) from " + str_tableName)); //
				int li_downedCount = 0; //
				while (1 == 1) { //��ѭ��
					long ll_1 = System.currentTimeMillis(); //
					HashMap returnMap = getService().getXMlFromTable1000Records(str_daName, str_tableName, str_pkName, li_beginNo); //
					if (returnMap == null) { //���Ϊ������ֱ�ӷ���
						break; //
					}
					int li_recordCount = (Integer) returnMap.get("��¼��"); //ʵ�ʵļ�¼��,Ӧ�ó����һҳ��,�����Ķ���500
					li_downedCount = li_downedCount + li_recordCount; //
					int li_perCent = ((li_downedCount * 100) / li_countall); //�ٷֱ�!  50/100
					li_beginNo = (Integer) returnMap.get("������"); //ʵ�����ݵĽ�����!					
					String str_xml = (String) returnMap.get("����"); //
					li_cout++; //
					String str_fileName = str_tableName + "_" + (100000 + li_cout) + ".xml"; //
					FileOutputStream fileOut = new FileOutputStream(str_newPath + "\\" + str_fileName, false); //
					tbUtil.writeStrToOutputStream(fileOut, str_xml); //������ļ�
					long ll_2 = System.currentTimeMillis(); //
					_splash.setWaitInfo("����[" + str_fileName + "],��ʱ[" + (ll_2 - ll_1) + "]!!\r\n������ɱ���[" + li_downedCount + "/" + li_countall + "=" + li_perCent + "%],�ܱ���[" + (i + 1) + "/" + _tablePKs.length + "]"); //
					if (str_pkName == null) { //�������Ϊ��,��Ϊ��һ��ȡ����������!��ֱ���˳�!!!����pub_sequence��!!
						break;
					}
					Thread.currentThread().sleep(100); //��Ϣһ��!!
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}


	//������밴ť!!
	private void onImportXml() {
		if (JOptionPane.showConfirmDialog(this, "�������ִ�д˵������ô?!\r\nǿ�ҽ������֮ǰ�ȱ���һ�����ݿ�!!", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}

		//�����ļ�ѡ���!ѡ��һ��·��!!!
		final String str_path = getChooseDir(); //
		if (str_path == null) {
			return;
		}
		long ll_begin = System.currentTimeMillis();
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				importXml((SplashWindow) e.getSource(), str_path); //
			}
		}, 600, 125, 300, 300); ///
		long ll_end = System.currentTimeMillis();
		JOptionPane.showMessageDialog(this, "�������,�ܹ���ʱ[" + ((ll_end - ll_begin) / 1000) + "]��!!"); //
	}

	/**
	 * ��������!!!
	 * @param _splash
	 * @param _rootDir
	 */
	private void importXml(SplashWindow _splash, String _rootDir) {
		try {
			String str_dsName = (String) dsComBobox_import.getSelectedItem(); //
			File rootDir = new File(_rootDir); //
			File[] tableDirFiles = rootDir.listFiles(); //����Ŀ¼!!
			for (int f = 0; f < tableDirFiles.length; f++) { //��������Ŀ¼
				if (tableDirFiles[f].isDirectory()) { //������Ŀ¼
					String str_tableName = tableDirFiles[f].getName(); //Ŀ¼�����Ǳ���!!!
					File[] files = tableDirFiles[f].listFiles(); //��������xml�ļ�!!
					for (int i = 0; i < files.length; i++) {
						long ll_1 = System.currentTimeMillis(); //
						String str_xml = tbUtil.readFromInputStreamToStr(new FileInputStream(files[i])); //
						getService().importXmlToTable1000Records(str_dsName, files[i].getName(), str_xml); //����һ�ű�!!
						long ll_2 = System.currentTimeMillis(); //
						_splash.setWaitInfo("����[" + files[i].getName() + "],��ʱ[" + (ll_2 - ll_1) + "],������ɱ���[" + (i + 1) + "/" + files.length + "],�ܹ�����[" + (f + 1) + "/" + tableDirFiles.length + "]"); //
						Thread.currentThread().sleep(100); //��Ϣһ��!!
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); //
			MessageBox.showException(this, e);
		}
	}

	public FrameWorkMetaDataServiceIfc getService() throws Exception {
		if (services != null) {
			return services; //
		}
		services = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
		return services; //
	}
}
