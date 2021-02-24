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

	private JButton btn_confirm = new JButton("关闭"); //

	private JTextField text_parFile = new JTextField(); //
	private JButton btn_chooseParFile = new JButton("选择文件"); //
	private JLabel label_isOutPar = new JLabel("是否输出请求参数=[]"); //
	private JButton btn_isOutPar = new JButton(); //是否输出客户端的请求参数!! 
	private JTextField text_parInfo = new JTextField("服务名=[],方法名=[]"); //
	private boolean isCompress = false; //是否是压缩的文件!!

	private JTable callParVOTable = null; //请求参数的VO
	private RemoteCallParVO loadedCallParVO = null; //加载的对象!!!

	private JTextField text_exportFile = new JTextField(); //
	private JButton btn_exportToNewFile = new JButton("生成新文件"); //

	public ShowClientEnvDialog(Container _parent) {
		super(_parent, "查看客户端环境变量", 800, 600);
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
		tabPane.addTab("系统属性", getSystemProteriesPanel());
		tabPane.addTab("ClientEnvironment", getClientEnvironmentPanel());
		tabPane.addTab("转换客户端参数", getConvertClientParFilePanel());

		this.getContentPane().setLayout(new BorderLayout());

		this.getContentPane().add(tabPane, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPane(), BorderLayout.SOUTH); //

		this.setVisible(true);
	}

	//下方的按钮!!
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
		String[] str_title = new String[] { "key", "说明", "value" };
		String[][] str_data = ClientEnvironment.getInstance().getAllData();
		if (str_data != null) {
			JTable table = new JTable(str_data, str_title); //
			panel.add(new JScrollPane(table)); //
		}
		return panel; //
	}

	private JPanel getConvertClientParFilePanel() {
		JPanel panel = new JPanel(new BorderLayout()); //

		//上方的按钮栏
		JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		text_parFile.setEditable(false); //
		text_parInfo.setEditable(false); //

		text_parFile.setPreferredSize(new Dimension(200, 20)); //
		btn_chooseParFile.setPreferredSize(new Dimension(85, 20)); //
		text_parInfo.setPreferredSize(new Dimension(750, 20)); //
		btn_chooseParFile.addActionListener(this); //

		label_isOutPar.setPreferredSize(new Dimension(150, 20)); //
		btn_isOutPar.setPreferredSize(new Dimension(125, 20)); //

		label_isOutPar.setText("是否输出请求参数=[" + ClientEnvironment.isOutPutCallObjToFile + "]"); ////
		btn_isOutPar.setText(ClientEnvironment.isOutPutCallObjToFile ? "禁用输出参数" : "启用输出参数"); ////
		btn_isOutPar.addActionListener(this); //
		btn_isOutPar.setToolTipText("启用后,系统将在C:\\156目录下存储每次请求的参数!"); //

		panel_north.add(text_parFile); //
		panel_north.add(btn_chooseParFile); //选择参数文件
		panel_north.add(label_isOutPar); //
		panel_north.add(btn_isOutPar); //

		panel_north.add(text_parInfo); //选择参数文件
		panel_north.setPreferredSize(new Dimension(1000, 55)); //
		panel.add(panel_north, BorderLayout.NORTH); //

		//中间的表格
		callParVOTable = new JTable(new DefaultTableModel(null, new String[] { "参数类型", "参数值", "替换的Key", "替换的Value" })); //
		callParVOTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //
		callParVOTable.setRowHeight(22); //
		callParVOTable.getColumnModel().getColumn(0).setPreferredWidth(100); //
		callParVOTable.getColumnModel().getColumn(1).setPreferredWidth(300); //
		callParVOTable.getColumnModel().getColumn(2).setPreferredWidth(85); //
		callParVOTable.getColumnModel().getColumn(3).setPreferredWidth(275); //
		panel.add(new JScrollPane(callParVOTable), BorderLayout.CENTER); //

		//下方的按钮栏!
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

	//载入文件!!
	//读入文件,替换\\x为空串,转成byte[],反序列化成RemoteCallParVO,读入表格!
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
			byte[] returnBys = bout.toByteArray(); //输出!!
			String str_text = new String(returnBys, "GBK"); //
			str_text = str_text.trim(); //
			str_text = tbUtil.replaceAll(str_text, "\r", ""); //换行符!
			str_text = tbUtil.replaceAll(str_text, "\n", ""); //换行符!
			str_text = tbUtil.replaceAll(str_text, "\\\\x", ""); //
			byte[] hexBytes = tbUtil.convertHexStringToBytes(str_text); //将16进制字符串转换成二进制!
			if (isCompress) { //如果是压缩的文件,则还要进行反解压!否则没法进行反序列化!!
				hexBytes = new RemoteCallClient().decompressBytes(hexBytes); //
			}
			loadedCallParVO = (RemoteCallParVO) tbUtil.deserialize(hexBytes); //反序列化
			String str_serviceName = loadedCallParVO.getServiceName(); //服务名!
			String str_methodName = loadedCallParVO.getMethodName(); //方法名!
			text_parInfo.setText("服务名=[" + str_serviceName + "],方法名=[" + str_methodName + "]"); //

			Class[] clses = loadedCallParVO.getParClasses(); //
			Object[] objs = loadedCallParVO.getParObjs(); //
			if (callParVOTable.getRowCount() >= 0 && callParVOTable.getCellEditor() != null) {
				callParVOTable.getCellEditor().stopCellEditing();
			}
			callParVOTable.clearSelection(); //清空选择!!
			DefaultTableModel tableModel = ((DefaultTableModel) callParVOTable.getModel()); //
			for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
				tableModel.removeRow(i); //
			}
			if (clses != null && clses.length > 0) {
				for (int i = 0; i < clses.length; i++) { //遍历所有参数!!
					tableModel.addRow(new String[] { clses[i].getName(), "" + objs[i], null, null }); //插入一行!
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			JOptionPane.showMessageDialog(this, "发生异常:" + ex.getMessage() + ",请至控制台查看详细!"); //
		} finally {
			try {
				if (ins != null) {
					ins.close(); //
				}
			} catch (Exception e) {
			}
			try {
				if (bout != null) {
					bout.close(); //关闭!!
				}
			} catch (Exception e) {
			}
		}
	}

	//根据表格中定义的,输出至新的目录中!!
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
			for (int i = 0; i < callParVOTable.getRowCount(); i++) { //遍历所有的行!
				String str_clsName = (String) callParVOTable.getValueAt(i, 0); //新值
				str_replace_oldValue = (String) callParVOTable.getValueAt(i, 1); //旧的值
				str_replace_key = (String) callParVOTable.getValueAt(i, 2); //替换什么!
				String str_newValue = (String) callParVOTable.getValueAt(i, 3); //新值
				if (str_clsName.equalsIgnoreCase("java.lang.String") && str_replace_key != null && !str_replace_key.trim().equals("") && str_newValue != null && !str_newValue.trim().equals("")) { //目录只对String处理!!
					li_replace_row = i; //
					str_newValueItems = tbUtil.split(str_newValue, ";"); ////
					break; //
				}
			}
			if (li_replace_row < 0) {
				JOptionPane.showMessageDialog(this, "没有指定一个参数进行替换!\r\n注意:只能替换一个String类型的参数,并且需要指定替换的Key与Value"); //
				return; //
			}

			if (str_replace_oldValue != null && str_replace_oldValue.indexOf(str_replace_key) < 0) {
				JOptionPane.showMessageDialog(this, "原来的值中没有关键字[" + str_replace_key + "],这将不可能产生替换效果!"); //
				return; //
			}

			if (JOptionPane.showConfirmDialog(this, "您确定要替换第[" + (li_replace_row + 1) + "]个参数中[" + str_replace_key + "]为[" + str_newValueItems.length + "]个新值吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) { //
				return;
			}

			String str_currtime = tbUtil.getCurrTime(false, false); //
			File fileDir = new File(_exportDir); //
			if (!fileDir.exists()) { //如果没有这个目录,则创建之!
				fileDir.mkdirs(); //
			}
			FileOutputStream fout_hex = new FileOutputStream(_exportDir + "\\" + (isCompress ? "Y" : "N") + "_newpar_" + str_currtime + ".hex", false); //
			for (int i = 0; i < str_newValueItems.length; i++) { //替换所有的
				String str_replacedValue = tbUtil.replaceAll(str_replace_oldValue, str_replace_key, str_newValueItems[i]); //
				RemoteCallParVO cloneParVO = (RemoteCallParVO) tbUtil.deepClone(this.loadedCallParVO); //
				cloneParVO.getParObjs()[li_replace_row] = str_replacedValue; //直接设置!!!
				byte[] bytes = tbUtil.serialize(cloneParVO); //序列化
				if (isCompress) {
					bytes = new RemoteCallClient().compressBytes(bytes); //压缩一把!可能并没有真正的做压缩!!!
				}
				String str_16text = tbUtil.convertBytesToHexString(bytes, true); //转换成16进制!!
				if (i != str_newValueItems.length - 1) {
					str_16text = str_16text + "\n"; //
				}
				fout_hex.write(str_16text.getBytes()); //输出!!
			}
			fout_hex.close(); //
			JOptionPane.showMessageDialog(this, "输出文件在目录[" + _exportDir + "]下成功!!"); //
		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	public void actionPerformed(ActionEvent _event) {
		if (_event.getSource() == btn_confirm) {
			this.dispose();
		} else if (_event.getSource() == btn_isOutPar) {
			if (btn_isOutPar.getText().equals("启用输出参数")) {
				ClientEnvironment.isOutPutCallObjToFile = true; //
				label_isOutPar.setText("是否输出请求参数=[" + ClientEnvironment.isOutPutCallObjToFile + "]"); //
				JOptionPane.showMessageDialog(this, "启用成功!从现在起,每次远程访问的参数都将存储在C:\\156目录下!"); //
				btn_isOutPar.setText("禁用输出参数"); //
			} else if (btn_isOutPar.getText().equals("禁用输出参数")) {
				ClientEnvironment.isOutPutCallObjToFile = false; //
				label_isOutPar.setText("是否输出请求参数=[" + ClientEnvironment.isOutPutCallObjToFile + "]"); //
				JOptionPane.showMessageDialog(this, "禁用成功!从现在起,不再存储每次请求的参数!"); //
				btn_isOutPar.setText("启用输出参数"); //
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
			loadParFile(text_parFile.getText()); //载入文件
		} else if (_event.getSource() == btn_exportToNewFile) {
			String str_exportFile = text_exportFile.getText(); //
			if (str_exportFile == null || str_exportFile.trim().equals("")) {
				JOptionPane.showMessageDialog(this, "请定义输出目录!"); //
				return;
			}
			exportdParFile(str_exportFile); //
		}
	}
}
