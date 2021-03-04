/**************************************************************************
 * $RCSfile: ShowServerEnvDialog.java,v $  $Revision: 1.2 $  $Date: 2012/09/14 09:19:33 $
 **************************************************************************/
package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;

public class ShowServerEnvDialog extends BillDialog {

	private static final long serialVersionUID = 8573901930687178737L;

	private FrameWorkCommServiceIfc frameWorkService = null; //Զ�̷��ʷ���..

	public ShowServerEnvDialog(Container _parent) {
		super(_parent, "�鿴��������������", 800, 600);
		initialize(); //
	}
	
	public static void openMe(java.awt.Container _parent, String _tile, String _type) {
		ShowServerEnvDialog d = new ShowServerEnvDialog(_parent); //
	}

	private void initialize() {
		JTabbedPane tabPane = new JTabbedPane(); //
		tabPane.addTab("SystemProperties", getSystemProteriesPanel());
		tabPane.addTab("ServerEnvironment", getServerEnvironmentPanel());
		tabPane.addTab("Server�����ļ�", getServerConfigXmlPanel());
		tabPane.addTab("��ǰ�����û�", getServerOnlineUser()); //
		tabPane.addTab("����Դ���ӳ�״̬", getDataSourcePoolActive()); //
		tabPane.addTab("Զ�̷����״̬", getServicePoolActive()); //

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(tabPane, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPane(), BorderLayout.SOUTH); //

		this.setVisible(true);
	}

	private JPanel getSouthPane() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new FlowLayout());
		JButton btn_confirm = new JButton("ȷ��"); //
		btn_confirm.setPreferredSize(new Dimension(100, 20)); //
		btn_confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ShowServerEnvDialog.this.dispose(); //
			}
		});
		panel.add(btn_confirm);
		return panel;
	}

	private JPanel getSystemProteriesPanel() {
		JPanel panel = new JPanel(new BorderLayout()); //
		JTextArea textArea = new JTextArea();
		textArea.setFont(LookAndFeel.font);  //
		textArea.setEditable(false);
		Properties pro = null; //
		try {
			pro = getFrameWorkService().getServerSystemProperties();
			String[] str_keys = (String[]) pro.keySet().toArray(new String[0]);
			Arrays.sort(str_keys); //
			for (int i = 0; i < str_keys.length; i++) {
				if (pro.getProperty(str_keys[i]).endsWith("\r") || pro.getProperty(str_keys[i]).endsWith("\n")) {
					textArea.append(str_keys[i] + " = " + pro.getProperty(str_keys[i]));//
				} else {
					textArea.append(str_keys[i] + " = " + pro.getProperty(str_keys[i]) + "\r\n");//
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); //
		}
		panel.add(new JScrollPane(textArea));
		return panel;
	}

	private JPanel getServerEnvironmentPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new BorderLayout());

		String[] str_title = new String[] { "key", "˵��", "value" };
		String[][] str_data = null;
		try {
			str_data = getFrameWorkService().getServerEnvironment();
			JTable table = new JTable(str_data, str_title);
			panel.add(new JScrollPane(table));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return panel;
	}

	/**
	 * ȡ�÷������������ļ�
	 * @return
	 */
	private JPanel getServerConfigXmlPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new BorderLayout());
		try {
			String str_text = getFrameWorkService().getServerConfigXML(); //�����ļ�
			JTextArea textarea = new JTextArea(str_text); //
			textarea.setFont(LookAndFeel.font);  //
			textarea.setEditable(false); //
			textarea.setForeground(Color.BLUE); //
			textarea.select(0, 0); //
			panel.add(new JScrollPane(textarea)); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		return panel;
	}

	private JPanel getServerOnlineUser() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new BorderLayout());
		String[] str_title = new String[] { "SessionID", "IP1", "IP2", "��¼�û�", "�û�����", "��������", "��¼ʱ��", "���һ�η���ʱ��", "ʹ��ʱ��","��ǰʱ��", "����ʱ��" };
		String[][] str_data = null;
		try {
			str_data = getFrameWorkService().getServerOnlineUser();
			JTable table = new JTable(str_data, str_title);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //
			table.getColumnModel().getColumn(0).setPreferredWidth(100); //sessionID
			table.getColumnModel().getColumn(1).setPreferredWidth(75); //IP1
			table.getColumnModel().getColumn(2).setPreferredWidth(75); //IP2
			table.getColumnModel().getColumn(3).setPreferredWidth(65); //�û�����
			table.getColumnModel().getColumn(4).setPreferredWidth(85); //��Ա����
			table.getColumnModel().getColumn(5).setPreferredWidth(85); //��������
			table.getColumnModel().getColumn(6).setPreferredWidth(130); //��¼ʱ��
			table.getColumnModel().getColumn(7).setPreferredWidth(130); //���һ�η���ʱ��
			table.getColumnModel().getColumn(8).setPreferredWidth(80); //ʹ��ʱ��
			table.getColumnModel().getColumn(9).setPreferredWidth(130); //��ǰʱ��
			table.getColumnModel().getColumn(10).setPreferredWidth(100); //����ʱ��
			panel.add(new JScrollPane(table));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return panel;
	}

	/**
	 * ȡ�ø�������Դ�ĵ�ǰ����
	 * @return
	 */
	private JPanel getDataSourcePoolActive() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new BorderLayout());
		String[] str_title = new String[] { "����Դ����", "����Դ˵��", "��ǰ�", "��ǰʵ����", "�����", "��ǰ״̬" };
		String[][] str_data = null;
		try {
			str_data = getFrameWorkService().getDatasourcePoolActiveNumbers(); //
			JTable table = new JTable(str_data, str_title);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //
			table.getColumnModel().getColumn(0).setPreferredWidth(125);
			table.getColumnModel().getColumn(1).setPreferredWidth(125);
			table.getColumnModel().getColumn(2).setPreferredWidth(50);
			table.getColumnModel().getColumn(3).setPreferredWidth(125);
			table.getColumnModel().getColumn(4).setPreferredWidth(175);
			table.getColumnModel().getColumn(5).setPreferredWidth(50);
			panel.add(new JScrollPane(table));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return panel;
	}

	/**
	 * ȡ�ø�������ĵ�ǰ����
	 * @return
	 */
	private JPanel getServicePoolActive() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new BorderLayout());
		String[] str_title = new String[] { "������", "��ǰ�", "��ǰʵ����", "�����", "��ǰ״̬" }; //
		String[][] str_data = null; //
		try {
			str_data = getFrameWorkService().getRemoteServicePoolActiveNumbers();
			JTable table = new JTable(str_data, str_title); //
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //
			table.getColumnModel().getColumn(0).setPreferredWidth(375);
			table.getColumnModel().getColumn(1).setPreferredWidth(50);
			table.getColumnModel().getColumn(2).setPreferredWidth(125);
			table.getColumnModel().getColumn(3).setPreferredWidth(225);
			table.getColumnModel().getColumn(4).setPreferredWidth(50);
			panel.add(new JScrollPane(table));
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return panel;
	}

	/**
	 * ȡ��FrameWorkService
	 * @return
	 */
	private FrameWorkCommServiceIfc getFrameWorkService() {
		if (frameWorkService != null) {
			return frameWorkService;
		}
		try {
			frameWorkService = (FrameWorkCommServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkCommServiceIfc.class);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return frameWorkService;
	}
}
