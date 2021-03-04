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

	private FrameWorkCommServiceIfc frameWorkService = null; //远程访问服务..

	public ShowServerEnvDialog(Container _parent) {
		super(_parent, "查看服务器环境变量", 800, 600);
		initialize(); //
	}
	
	public static void openMe(java.awt.Container _parent, String _tile, String _type) {
		ShowServerEnvDialog d = new ShowServerEnvDialog(_parent); //
	}

	private void initialize() {
		JTabbedPane tabPane = new JTabbedPane(); //
		tabPane.addTab("SystemProperties", getSystemProteriesPanel());
		tabPane.addTab("ServerEnvironment", getServerEnvironmentPanel());
		tabPane.addTab("Server配置文件", getServerConfigXmlPanel());
		tabPane.addTab("当前在线用户", getServerOnlineUser()); //
		tabPane.addTab("数据源连接池状态", getDataSourcePoolActive()); //
		tabPane.addTab("远程服务池状态", getServicePoolActive()); //

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(tabPane, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPane(), BorderLayout.SOUTH); //

		this.setVisible(true);
	}

	private JPanel getSouthPane() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new FlowLayout());
		JButton btn_confirm = new JButton("确定"); //
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

		String[] str_title = new String[] { "key", "说明", "value" };
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
	 * 取得服务器端配置文件
	 * @return
	 */
	private JPanel getServerConfigXmlPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new BorderLayout());
		try {
			String str_text = getFrameWorkService().getServerConfigXML(); //配置文件
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
		String[] str_title = new String[] { "SessionID", "IP1", "IP2", "登录用户", "用户名称", "所属机构", "登录时间", "最后一次访问时间", "使用时长","当前时间", "发呆时长" };
		String[][] str_data = null;
		try {
			str_data = getFrameWorkService().getServerOnlineUser();
			JTable table = new JTable(str_data, str_title);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //
			table.getColumnModel().getColumn(0).setPreferredWidth(100); //sessionID
			table.getColumnModel().getColumn(1).setPreferredWidth(75); //IP1
			table.getColumnModel().getColumn(2).setPreferredWidth(75); //IP2
			table.getColumnModel().getColumn(3).setPreferredWidth(65); //用户编码
			table.getColumnModel().getColumn(4).setPreferredWidth(85); //人员名称
			table.getColumnModel().getColumn(5).setPreferredWidth(85); //所属机构
			table.getColumnModel().getColumn(6).setPreferredWidth(130); //登录时间
			table.getColumnModel().getColumn(7).setPreferredWidth(130); //最后一次访问时间
			table.getColumnModel().getColumn(8).setPreferredWidth(80); //使用时长
			table.getColumnModel().getColumn(9).setPreferredWidth(130); //当前时间
			table.getColumnModel().getColumn(10).setPreferredWidth(100); //发呆时间
			panel.add(new JScrollPane(table));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return panel;
	}

	/**
	 * 取得各个数据源的当前数量
	 * @return
	 */
	private JPanel getDataSourcePoolActive() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new BorderLayout());
		String[] str_title = new String[] { "数据源名称", "数据源说明", "当前活动", "当前实例数", "最大数", "当前状态" };
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
	 * 取得各个服务的当前数量
	 * @return
	 */
	private JPanel getServicePoolActive() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new BorderLayout());
		String[] str_title = new String[] { "服务名", "当前活动", "当前实例数", "最大数", "当前状态" }; //
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
	 * 取得FrameWorkService
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
