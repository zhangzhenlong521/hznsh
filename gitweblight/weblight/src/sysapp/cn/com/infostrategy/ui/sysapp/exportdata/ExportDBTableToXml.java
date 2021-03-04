package cn.com.infostrategy.ui.sysapp.exportdata;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

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
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.FrameWorkMetaDataServiceIfc;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

/**
 * ������ܼ�������,�Ժ���ҵ����Ա��æ,�����ϵ���ƽ̨����ر�,Ȼ��ѹ����һ���ļ�,���ʼ�������! �����ڹ�˾���ָܻ����ֳ�����!!������ģ������������!!
 * �����������Ҫ��Ҫ�����ڲ���ʹϵͳ�ڴ����! ����ʽ����!!!
 * 
 * ��Ҫ��װ������:
 * 1.������ҳǩ,�ֱ�е���.����
 * 2.�����ж�����ٰ�ť(��������ť,�˵�,Ȩ��,��������),Ҳ��һ����������ı�����! ��û���һ����ѯ���б��
 * 3.������Ҫ��ѡ��һ��Ŀ¼,Ȼ�󵼳������еȴ���˵��! 
 * 4.����ʱ���һ����һ����Ŀ¼!! ����Ҳ���ȶ�Ŀ¼����ļ�!! ģ���ӱ���46��,����76��,��ѹ����60���ļ�,54M,ѹ������1.18M,С�ĺ�!��������Ȼ��С��!!
 * 
 * 5.���Գ��Խ�����ϵͳ�����б�����! ������Ҫ�೤ʱ��!! �������鷳�ľ��Ǵ������ı��ֶ�!  �Ժ���һ��4000���ַ�������������ʱ����!!!
 * @author xch
 *
 */
public class ExportDBTableToXml extends AbstractWorkPanel implements ActionListener {

	private JComboBox dsComBobox_export = null; //����Դ!!!
	private WLTButton btn_templet = null; //
	private WLTButton btn_sysroles, btn_workflows, btn_deploysc = null; //
	private JTextArea textArea_1, textArea_2 = null; //
	private WLTButton btn_custtables = null; //

	private JComboBox dsComBobox_import = null; //����ʱѡ�������Դ!!!
	private WLTButton btn_import, btn_querydatacount = null; //
	private TBUtil tbUtil = new TBUtil(); //

	private FrameWorkMetaDataServiceIfc services = null; //

	@Override
	public void initialize() {
		DataSourceVO[] dsVOs = ClientEnvironment.getInstance().getDataSourceVOs(); //��������Դ!!!
		dsComBobox_export = new JComboBox(); //
		for (int i = 0; i < dsVOs.length; i++) {
			dsComBobox_export.addItem(dsVOs[i].getName()); //����Դ����!!
		}
		dsComBobox_export.setBounds(10, 10, 95, 20); //

		btn_templet = new WLTButton("����ģ��ϵ��"); //
		btn_templet.setBounds(110, 10, 125, 20); //
		btn_templet.addActionListener(this); //

		btn_sysroles = new WLTButton("����Ȩ��ϵ��");
		btn_sysroles.setBounds(240, 10, 125, 20); //
		btn_sysroles.addActionListener(this);

		btn_workflows = new WLTButton("����������ϵ��");
		btn_workflows.setBounds(370, 10, 125, 20); //
		btn_workflows.addActionListener(this); //

		btn_deploysc = new WLTButton("����������ϵ��");
		btn_deploysc.setBounds(500, 10, 125, 20); //
		btn_deploysc.addActionListener(this); //

		textArea_1 = new JTextArea("pub_menu,pub_role,pub_comboboxdict(pk_pub_comboboxdict),pub_templet_1(pk_pub_templet_1),pub_sequence()"); //
		textArea_1.setFont(LookAndFeel.font); ////
		textArea_1.setLineWrap(true); //
		JScrollPane scrollPanel = new JScrollPane(textArea_1); //
		scrollPanel.setBounds(10, 40, 500, 150); //

		btn_custtables = new WLTButton("����ָ����");
		btn_custtables.setBounds(520, 40, 125, 20); //
		btn_custtables.addActionListener(this); //

		//����Ӧ��,����һ��ҳǩ�е�Ӧ��!
		JPanel panel_1 = WLTPanel.createDefaultPanel(null); //
		panel_1.add(dsComBobox_export); //
		panel_1.add(btn_templet); //
		panel_1.add(btn_sysroles); //
		panel_1.add(btn_workflows); //
		panel_1.add(btn_deploysc); //
		panel_1.add(scrollPanel); //
		panel_1.add(btn_custtables); //

		//����ҳǩ!!
		dsComBobox_import = new JComboBox(); //
		for (int i = 0; i < dsVOs.length; i++) {
			dsComBobox_import.addItem(dsVOs[i].getName()); //����Դ����!!
		}
		dsComBobox_import.setBounds(10, 10, 100, 20); //

		btn_import = new WLTButton("����XML�ļ�"); //
		btn_import.addActionListener(this); //

		btn_querydatacount = new WLTButton("�鿴�����ݵı�"); //
		btn_querydatacount.addActionListener(this); //

		textArea_2 = new JTextArea(); //
		textArea_2.setEditable(false); //
		textArea_2.setFont(LookAndFeel.font); ////
		textArea_2.setLineWrap(true); //
		JScrollPane scrollPanel2 = new JScrollPane(textArea_2); //

		//����Ӧ��,���ڶ���ҳǩ�е�����!!
		JPanel panel_2 = WLTPanel.createDefaultPanel(new BorderLayout()); //
		JPanel northPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		northPanel2.setOpaque(false); //͸��!

		northPanel2.add(dsComBobox_import); //
		northPanel2.add(btn_import); //
		northPanel2.add(btn_querydatacount); //

		panel_2.add(northPanel2, BorderLayout.NORTH); //
		panel_2.add(scrollPanel2, BorderLayout.CENTER); //

		JTabbedPane tabbedPanel = new JTabbedPane(); //
		tabbedPanel.addTab("����Ӧ��", panel_1); //
		tabbedPanel.addTab("����Ӧ��", panel_2); //

		this.setLayout(new BorderLayout()); //
		this.add(tabbedPanel); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_templet) {
			onExportXml(getTempletTableDefines()); //
		} else if (e.getSource() == btn_sysroles) {
			onExportXml(getSysUserRoleCorpTableDefines()); //
		} else if (e.getSource() == btn_workflows) { //����������!
			onExportXml(getWorkFlowTableDefines()); //
		} else if (e.getSource() == btn_deploysc) { //���ò�����������
			onExportXml(getDeploySC()); //
		} else if (e.getSource() == btn_custtables) { //����ָ����!!
			String str_text = textArea_1.getText(); //
			if (str_text == null || str_text.trim().equals("")) {
				MessageBox.show(this, "����д����!"); //
				return; //
			}
			str_text = str_text.trim(); //
			String[] str_tables = tbUtil.split(str_text, ","); //
			String[][] str_tabPKs = new String[str_tables.length][2]; //
			for (int i = 0; i < str_tables.length; i++) {
				String str_tabName = str_tables[i].trim(); //
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
		} else if (e.getSource() == btn_import) { //����!!!
			onImportXml(); //
		} else if (e.getSource() == btn_querydatacount) { //��ѯ�����ݵı��!!
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					onQueryAllTableDataCount(); //
				}
			}); //	
		}
	}

	private void onExportXml(final String[][] _tablePKs) {
		int li_rs = MessageBox.showOptionDialog(this, "��ѡ�������������!��������", "��ʾ", new String[] { "���ɱ��ݱ�SQL", "����ɾ��������SQL", "����XML", "ȡ��" }, 500, 150); //
		if (li_rs == 0) {
			StringBuilder sb_sql = new StringBuilder(); //
			sb_sql.append("��Ŀ������ִ�����²���,����Щ���ȱ���һ��:\r\n"); //
			for (int i = 0; i < _tablePKs.length; i++) {
				sb_sql.append("drop   table " + _tablePKs[i][0] + "_2;\r\n"); //
			}
			sb_sql.append("\r\n"); //
			for (int i = 0; i < _tablePKs.length; i++) {
				sb_sql.append("create table " + _tablePKs[i][0] + "_2 as select * from " + _tablePKs[i][0] + ";\r\n"); //
			}
			MessageBox.show(this, sb_sql.toString()); //
		} else if (li_rs == 1) {
			StringBuilder sb_sql = new StringBuilder(); //
			sb_sql.append("��Ŀ����е���ǰ�����������,��Ϊǰ���Ѿ�����,���Լ�ʹ����Ҳ�ָܻ�:\r\n"); //
			for (int i = 0; i < _tablePKs.length; i++) {
				sb_sql.append("truncate table " + _tablePKs[i][0] + ";\r\n"); //
			}
			sb_sql.append("\r\n"); //
			MessageBox.show(this, sb_sql.toString()); //
		} else if (li_rs == 2) {
			realExportXml(_tablePKs); //
		}
	}

	private void realExportXml(final String[][] _tablePKs) {
		if (MessageBox.showConfirmDialog(this, "��ȷ��Ҫִ�д˵���������?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
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
		}, 600, 130, 300, 300); //��ʾ��Ϣ��ʾ�������������Ӵ��ڸ߶ȡ����/2012-03-27��
		long ll_end = System.currentTimeMillis();
		MessageBox.show(this, "�������,�ܹ���ʱ[" + ((ll_end - ll_begin) / 1000) + "]��!"); //
	}

	private String getChooseDir() {
		JFileChooser chooser = new JFileChooser(new File("C:/"));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("��ѡ��һ��Ŀ¼"); //
		int li_rewult = chooser.showSaveDialog(this);
		if (li_rewult == JFileChooser.CANCEL_OPTION) {
			return null;// ���ȡ��ʲôҲ����
		}
		if (chooser.getSelectedFile().isFile()) {
			MessageBox.show(this, "��ѡ��һ��Ŀ¼,�����ļ�!"); //
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
						li_beginNo = Integer.parseInt(str_minValue) - 1; //����Сֵ!!!
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

	public String[][] getTempletTableDefines() {
		return new String[][] { // 
		{ "pub_templet_1", "pk_pub_templet_1" }, //ģ������
				{ "pub_templet_1_item", "pk_pub_templet_1_item" }, //ģ���ӱ�
				{ "pub_regbuttons", "id" }, //ע�ᰴť
				{ "pub_comboboxdict", "pk_pub_comboboxdict" }, //
				{ "pub_regformatpanel", "id" }, //
				{ "pub_refregister", "id" }, //
				{ "pub_option", "id" }, //
				{ "pub_sequence", null }, //���û������
		};
	}

	public String[][] getSysUserRoleCorpTableDefines() {
		return new String[][] { // 
		{ "pub_menu", "id" }, //�˵�
				{ "pub_corp_dept", "id" }, //����
				{ "pub_user", "id" }, //��Ա
				{ "pub_role", "id" }, //��ɫ
				{ "pub_user_role", "id" }, //��Ա���ɫ
				{ "pub_user_menu", "id" }, //��Ա��˵�
				{ "pub_role_menu", "id" }, //��ɫ��˵�
				{ "pub_user_post", "id" }, //��Ա����
		};
	}

	public String[][] getWorkFlowTableDefines() {
		return new String[][] { // 
		{ "pub_billtype", "id" }, //��������
				{ "pub_busitype", "id" }, //ҵ������
				{ "pub_wf_process", "id" }, //����
				{ "pub_wf_activity", "id" }, //����
				{ "pub_wf_transition", "id" }, //����
				{ "pub_wf_group", "id" }, //��
				{ "pub_wf_dypardefines", "id" }, //��̬������
				{ "pub_workflowassign", "id" }, //���̷���
				{ "pub_wf_prinstance", "id" }, //����ʵ��
				{ "pub_wf_dealpool", "id" }, //���̳�
				{ "pub_task_deal", "id" }, //��������
				{ "pub_task_off", "id" }, //�Ѱ�����
		};
	}

	//������������ʱ��õ�15�ű�,����Щ���Ժ���Զ�ǵ�����!!
	//������������Զ��Ҫ�޸���Щ����!
	public String[][] getDeploySC() {
		return new String[][] { // 
		{ "pub_templet_1", "pk_pub_templet_1" }, //ģ������
				{ "pub_templet_1_item", "pk_pub_templet_1_item" }, //ģ���ӱ�
				{ "pub_billcelltemplet_h", "id" }, //Excel����
				{ "pub_billcelltemplet_d", "id" }, //Excel�ӱ�
				{ "pub_regbuttons", "id" }, //ע�ᰴť
				{ "pub_regformatpanel", "id" }, //ע������
				{ "pub_refregister", "id" }, //ע�����
				{ "pub_wf_process", "id" }, //��������
				{ "pub_wf_activity", "id" }, //���̻���
				{ "pub_wf_transition", "id" }, //����
				{ "pub_wf_group", "id" }, //������
				{ "pub_wf_dypardefines", "id" }, //��̬������
				{ "pub_workflowassign", "id" }, //���̷���
				{ "pub_datapolicy", "id" }, //����Ȩ�޲���!
				{ "pub_datapolicy_b", "id" }, //�����ӱ�
				{ "pub_comboboxdict", "pk_pub_comboboxdict" }, //

		};
	}

	/**
	 * ��ѯ���б�����ݽ����,�ڰ�װ��ʵʩ�����е����õ�!! ��xch/2012-02-23��
	 */
	private void onQueryAllTableDataCount() {
		try {
			SysAppServiceIfc sevice = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			String str_info = sevice.getAllTableRecordCountInfo(null); //
			textArea_2.setText(""); //���������
			textArea_2.setText(str_info); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//������밴ť!!
	private void onImportXml() {
		if (MessageBox.showConfirmDialog(this, "��ȷ��Ҫִ�д˵��������?\r\nǿ�ҽ������֮ǰ�ȱ���һ�����ݿ�!\r\nһ��Ҫע������Դ�Ƿ���ȷ!", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}

		String str_dsName = (String) dsComBobox_import.getSelectedItem(); //
		if (!MessageBox.confirm(this, "��ȷ��Ҫ���뵽��" + str_dsName + "�����𣿣�����\r\n\r\n�������˰����ݵ������ĵط�����������\r\n���Ƿǳ�Σ�յģ�������")) {
			return; //
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
		}, 600, 130, 300, 300); //��ʾ��Ϣ��ʾ�������������Ӵ��ڸ߶ȡ����/2012-03-27��
		long ll_end = System.currentTimeMillis();
		MessageBox.show(this, "�������,�ܹ���ʱ[" + ((ll_end - ll_begin) / 1000) + "]��!"); //
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
			ArrayList tablenames = new ArrayList();//Ϊ�������sequence����¼���������/2014-02-28��
			for (int f = 0; f < tableDirFiles.length; f++) { //��������Ŀ¼
				if (tableDirFiles[f].isDirectory()) { //������Ŀ¼
					String str_tableName = tableDirFiles[f].getName(); //Ŀ¼�����Ǳ���!!!
					tablenames.add(str_tableName.toUpperCase());//�����/2014-02-28��
					UIUtil.executeUpdateByDS(str_dsName, "delete from  " + str_tableName + " where 1=1"); //�ȳ��׸ɵ����е�����!!!
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
			//����sequence�����/2014-02-28��
			_splash.setWaitInfo("����Ŭ������Sequence..."); //
			SysAppServiceIfc service = (SysAppServiceIfc) RemoteServiceFactory.getInstance().lookUpService(SysAppServiceIfc.class); //����Զ�̷���
			service.resetSequence(tablenames);//����Sequence�������Ժ�ܿ��ܻ����������ͻ�����tables.xml��������¡����/2014-02-28��
		} catch (Exception e) {
			e.printStackTrace(); //
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
