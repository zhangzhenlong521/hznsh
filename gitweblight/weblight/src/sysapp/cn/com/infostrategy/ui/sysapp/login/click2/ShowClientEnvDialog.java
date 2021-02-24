/**************************************************************************
 * $RCSfile: ShowClientEnvDialog.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:19:33 $
 **************************************************************************/
package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import cn.com.infostrategy.to.common.RemoteCallParVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.RemoteCallClient;

public class ShowClientEnvDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = 8573901930687178737L;

	private JButton btn_confirm = new JButton("�ر�"); //

	private JTextField text_parFile = new JTextField(); //
	private JButton btn_chooseParFile = new JButton("ѡ���ļ�"); //
	private JLabel label_isOutPar = new JLabel("�Ƿ�����������=[]"); //
	private JButton btn_isOutPar = new JButton(); //�Ƿ�����ͻ��˵��������!! 
	private JTextField text_parInfo = new JTextField("������=[],������=[]"); //
	private boolean isCompress = false; //�Ƿ���ѹ�����ļ�!!

	private JTable callParVOTable = null; //���������VO
	private RemoteCallParVO loadedCallParVO = null; //���صĶ���!!!

	private JTextField text_exportFile = new JTextField(); //
	private JButton btn_exportToNewFile = new JButton("�������ļ�"); //

	public ShowClientEnvDialog(Container _parent) {
		super(_parent, "�鿴�ͻ��˻�������", 800, 600);
		initialize(); //
	}

	public static void openMe(java.awt.Container _parent, String _tile, String _type) {
		try {
			ShowClientEnvDialog dialog = new ShowClientEnvDialog(_parent);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void initialize() {
		JTabbedPane tabPane = new JTabbedPane(); //
		tabPane.addTab("ϵͳ����", getSystemProteriesPanel());
		tabPane.addTab("ClientEnvironment", getClientEnvironmentPanel());
		tabPane.addTab("ת���ͻ��˲���", getConvertClientParFilePanel());

		this.getContentPane().setLayout(new BorderLayout());

		this.getContentPane().add(tabPane, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPane(), BorderLayout.SOUTH); //

		this.setVisible(true);
	}

	//�·��İ�ť!!
	private JPanel getSouthPane() {
		JPanel panel = new JPanel(); ///
		panel.setLayout(new FlowLayout()); //
		btn_confirm.setPreferredSize(new Dimension(100, 20)); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm);
		return panel;
	}

	private JPanel getSystemProteriesPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new BorderLayout());

		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);

		Properties pr = System.getProperties();
		String[] str_keys = (String[]) pr.keySet().toArray(new String[0]);
		Arrays.sort(str_keys); //
		for (int i = 0; i < str_keys.length; i++) {
			if (pr.getProperty(str_keys[i]).endsWith("\r") || pr.getProperty(str_keys[i]).endsWith("\n")) {
				textArea.append(str_keys[i] + " = " + pr.getProperty(str_keys[i]));//
			} else {
				textArea.append(str_keys[i] + " = " + pr.getProperty(str_keys[i]) + "\r\n");//
			}
		}

		panel.add(new JScrollPane(textArea));
		textArea.select(0, 0); //
		return panel;
	}

	private JPanel getClientEnvironmentPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new BorderLayout()); //
		String[] str_title = new String[] { "key", "˵��", "value" };
		String[][] str_data = ClientEnvironment.getInstance().getAllData();
		if (str_data != null) {
			JTable table = new JTable(str_data, str_title); //
			panel.add(new JScrollPane(table)); //
		}
		return panel; //
	}

	private JPanel getConvertClientParFilePanel() {
		JPanel panel = new JPanel(new BorderLayout()); //

		//�Ϸ��İ�ť��
		JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		text_parFile.setEditable(false); //
		text_parInfo.setEditable(false); //

		text_parFile.setPreferredSize(new Dimension(200, 20)); //
		btn_chooseParFile.setPreferredSize(new Dimension(85, 20)); //
		text_parInfo.setPreferredSize(new Dimension(750, 20)); //
		btn_chooseParFile.addActionListener(this); //

		label_isOutPar.setPreferredSize(new Dimension(150, 20)); //
		btn_isOutPar.setPreferredSize(new Dimension(125, 20)); //

		label_isOutPar.setText("�Ƿ�����������=[" + ClientEnvironment.isOutPutCallObjToFile + "]"); ////
		btn_isOutPar.setText(ClientEnvironment.isOutPutCallObjToFile ? "�����������" : "�����������"); ////
		btn_isOutPar.addActionListener(this); //
		btn_isOutPar.setToolTipText("���ú�,ϵͳ����C:\\156Ŀ¼�´洢ÿ������Ĳ���!"); //

		panel_north.add(text_parFile); //
		panel_north.add(btn_chooseParFile); //ѡ������ļ�
		panel_north.add(label_isOutPar); //
		panel_north.add(btn_isOutPar); //

		panel_north.add(text_parInfo); //ѡ������ļ�
		panel_north.setPreferredSize(new Dimension(1000, 55)); //
		panel.add(panel_north, BorderLayout.NORTH); //

		//�м�ı��
		callParVOTable = new JTable(new DefaultTableModel(null, new String[] { "��������", "����ֵ", "�滻��Key", "�滻��Value" })); //
		callParVOTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //
		callParVOTable.setRowHeight(22); //
		callParVOTable.getColumnModel().getColumn(0).setPreferredWidth(100); //
		callParVOTable.getColumnModel().getColumn(1).setPreferredWidth(300); //
		callParVOTable.getColumnModel().getColumn(2).setPreferredWidth(85); //
		callParVOTable.getColumnModel().getColumn(3).setPreferredWidth(275); //
		panel.add(new JScrollPane(callParVOTable), BorderLayout.CENTER); //

		//�·��İ�ť��!
		JPanel panel_south = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		text_exportFile.setText("C:\\157"); //
		text_exportFile.setPreferredSize(new Dimension(200, 20)); //
		btn_exportToNewFile.setPreferredSize(new Dimension(100, 20)); //
		btn_exportToNewFile.addActionListener(this); //
		panel_south.add(text_exportFile); //
		panel_south.add(btn_exportToNewFile); //
		panel.add(panel_south, BorderLayout.SOUTH); //

		return panel; //
	}

	//�����ļ�!!
	//�����ļ�,�滻\\xΪ�մ�,ת��byte[],�����л���RemoteCallParVO,������!
	private void loadParFile(String _fileName) {
		TBUtil tbUtil = new TBUtil(); //
		FileInputStream ins = null; //
		ByteArrayOutputStream bout = null; //
		try {
			ins = new FileInputStream(_fileName); //
			bout = new ByteArrayOutputStream(); //
			byte[] bys = new byte[2048];
			int li_pos = -1; //
			while ((li_pos = ins.read(bys)) != -1) {
				bout.write(bys, 0, li_pos); //
			}
			byte[] returnBys = bout.toByteArray(); //���!!
			String str_text = new String(returnBys, "GBK"); //
			str_text = str_text.trim(); //
			str_text = tbUtil.replaceAll(str_text, "\r", ""); //���з�!
			str_text = tbUtil.replaceAll(str_text, "\n", ""); //���з�!
			str_text = tbUtil.replaceAll(str_text, "\\\\x", ""); //
			byte[] hexBytes = tbUtil.convertHexStringToBytes(str_text); //��16�����ַ���ת���ɶ�����!
			if (isCompress) { //�����ѹ�����ļ�,��Ҫ���з���ѹ!����û�����з����л�!!
				hexBytes = new RemoteCallClient().decompressBytes(hexBytes); //
			}
			loadedCallParVO = (RemoteCallParVO) tbUtil.deserialize(hexBytes); //�����л�
			String str_serviceName = loadedCallParVO.getServiceName(); //������!
			String str_methodName = loadedCallParVO.getMethodName(); //������!
			text_parInfo.setText("������=[" + str_serviceName + "],������=[" + str_methodName + "]"); //

			Class[] clses = loadedCallParVO.getParClasses(); //
			Object[] objs = loadedCallParVO.getParObjs(); //
			if (callParVOTable.getRowCount() >= 0 && callParVOTable.getCellEditor() != null) {
				callParVOTable.getCellEditor().stopCellEditing();
			}
			callParVOTable.clearSelection(); //���ѡ��!!
			DefaultTableModel tableModel = ((DefaultTableModel) callParVOTable.getModel()); //
			for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
				tableModel.removeRow(i); //
			}
			if (clses != null && clses.length > 0) {
				for (int i = 0; i < clses.length; i++) { //�������в���!!
					tableModel.addRow(new String[] { clses[i].getName(), "" + objs[i], null, null }); //����һ��!
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			JOptionPane.showMessageDialog(this, "�����쳣:" + ex.getMessage() + ",��������̨�鿴��ϸ!"); //
		} finally {
			try {
				if (ins != null) {
					ins.close(); //
				}
			} catch (Exception e) {
			}
			try {
				if (bout != null) {
					bout.close(); //�ر�!!
				}
			} catch (Exception e) {
			}
		}
	}

	//���ݱ���ж����,������µ�Ŀ¼��!!
	private void exportdParFile(String _exportDir) {
		try {
			TBUtil tbUtil = new TBUtil(); //
			int li_replace_row = -1; //
			String str_replace_oldValue = null; //
			String str_replace_key = null; // 
			String[] str_newValueItems = null; //
			if (callParVOTable.getRowCount() >= 0 && callParVOTable.getCellEditor() != null) {
				callParVOTable.getCellEditor().stopCellEditing();
			}
			for (int i = 0; i < callParVOTable.getRowCount(); i++) { //�������е���!
				String str_clsName = (String) callParVOTable.getValueAt(i, 0); //��ֵ
				str_replace_oldValue = (String) callParVOTable.getValueAt(i, 1); //�ɵ�ֵ
				str_replace_key = (String) callParVOTable.getValueAt(i, 2); //�滻ʲô!
				String str_newValue = (String) callParVOTable.getValueAt(i, 3); //��ֵ
				if (str_clsName.equalsIgnoreCase("java.lang.String") && str_replace_key != null && !str_replace_key.trim().equals("") && str_newValue != null && !str_newValue.trim().equals("")) { //Ŀ¼ֻ��String����!!
					li_replace_row = i; //
					str_newValueItems = tbUtil.split(str_newValue, ";"); ////
					break; //
				}
			}
			if (li_replace_row < 0) {
				JOptionPane.showMessageDialog(this, "û��ָ��һ�����������滻!\r\nע��:ֻ���滻һ��String���͵Ĳ���,������Ҫָ���滻��Key��Value"); //
				return; //
			}

			if (str_replace_oldValue != null && str_replace_oldValue.indexOf(str_replace_key) < 0) {
				JOptionPane.showMessageDialog(this, "ԭ����ֵ��û�йؼ���[" + str_replace_key + "],�⽫�����ܲ����滻Ч��!"); //
				return; //
			}

			if (JOptionPane.showConfirmDialog(this, "��ȷ��Ҫ�滻��[" + (li_replace_row + 1) + "]��������[" + str_replace_key + "]Ϊ[" + str_newValueItems.length + "]����ֵ��?", "��ʾ", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { //
				return;
			}

			String str_currtime = tbUtil.getCurrTime(false, false); //
			File fileDir = new File(_exportDir); //
			if (!fileDir.exists()) { //���û�����Ŀ¼,�򴴽�֮!
				fileDir.mkdirs(); //
			}
			FileOutputStream fout_hex = new FileOutputStream(_exportDir + "\\" + (isCompress ? "Y" : "N") + "_newpar_" + str_currtime + ".hex", false); //
			for (int i = 0; i < str_newValueItems.length; i++) { //�滻���е�
				String str_replacedValue = tbUtil.replaceAll(str_replace_oldValue, str_replace_key, str_newValueItems[i]); //
				RemoteCallParVO cloneParVO = (RemoteCallParVO) tbUtil.deepClone(this.loadedCallParVO); //
				cloneParVO.getParObjs()[li_replace_row] = str_replacedValue; //ֱ������!!!
				byte[] bytes = tbUtil.serialize(cloneParVO); //���л�
				if (isCompress) {
					bytes = new RemoteCallClient().compressBytes(bytes); //ѹ��һ��!���ܲ�û����������ѹ��!!!
				}
				String str_16text = tbUtil.convertBytesToHexString(bytes, true); //ת����16����!!
				if (i != str_newValueItems.length - 1) {
					str_16text = str_16text + "\n"; //
				}
				fout_hex.write(str_16text.getBytes()); //���!!
			}
			fout_hex.close(); //
			JOptionPane.showMessageDialog(this, "����ļ���Ŀ¼[" + _exportDir + "]�³ɹ�!!"); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_confirm) {
			this.dispose();
		} else if (_event.getSource() == btn_isOutPar) {
			if (btn_isOutPar.getText().equals("�����������")) {
				ClientEnvironment.isOutPutCallObjToFile = true; //
				label_isOutPar.setText("�Ƿ�����������=[" + ClientEnvironment.isOutPutCallObjToFile + "]"); //
				JOptionPane.showMessageDialog(this, "���óɹ�!��������,ÿ��Զ�̷��ʵĲ��������洢��C:\\156Ŀ¼��!"); //
				btn_isOutPar.setText("�����������"); //
			} else if (btn_isOutPar.getText().equals("�����������")) {
				ClientEnvironment.isOutPutCallObjToFile = false; //
				label_isOutPar.setText("�Ƿ�����������=[" + ClientEnvironment.isOutPutCallObjToFile + "]"); //
				JOptionPane.showMessageDialog(this, "���óɹ�!��������,���ٴ洢ÿ������Ĳ���!"); //
				btn_isOutPar.setText("�����������"); //
			}
		} else if (_event.getSource() == btn_chooseParFile) {
			JFileChooser fc = new JFileChooser(new File("C:/156"));
			fc.showOpenDialog(this);
			File selFile = fc.getSelectedFile();
			if (selFile == null) {
				return;
			}
			text_parFile.setText(selFile.getAbsolutePath()); //
			if (selFile.getName().startsWith("Y_")) {
				isCompress = true; //
			} else {
				isCompress = false; //
			}
			loadParFile(text_parFile.getText()); //�����ļ�
		} else if (_event.getSource() == btn_exportToNewFile) {
			String str_exportFile = text_exportFile.getText(); //
			if (str_exportFile == null || str_exportFile.trim().equals("")) {
				JOptionPane.showMessageDialog(this, "�붨�����Ŀ¼!"); //
				return;
			}
			exportdParFile(str_exportFile); //
		}
	}
}
