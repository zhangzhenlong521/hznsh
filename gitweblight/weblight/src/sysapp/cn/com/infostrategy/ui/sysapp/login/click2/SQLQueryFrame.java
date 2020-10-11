package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * SQL��ѯ������..
 * @author xch
 *
 */
public class SQLQueryFrame extends JFrame {

	private static final long serialVersionUID = 7612475787826401397L;

	public SQLQueryFrame() {
		this.setTitle("SQL Query ToolKit"); //
		this.setSize(1000, 700);  //
		this.setLocation(0, 0);

		JTabbedPane tabbed = new JTabbedPane(); //
		tabbed.setFocusable(false); //
		tabbed.addTab("��ѯ��1", new SQLQueryPanel()); //
		tabbed.addTab("��ѯ��2", new SQLQueryPanel()); //
		tabbed.addTab("��ѯ��3", new SQLQueryPanel()); //
		tabbed.addTab("��ѯ��4", new SQLQueryPanel()); //
		tabbed.addTab("��ѯ��5", new SQLQueryPanel()); //
		tabbed.addTab("��ѯ��6", new SQLQueryPanel()); //
		tabbed.addTab("��ѯ��7", new SQLQueryPanel()); //
		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(tabbed); //
	}
	
	public static void openMe(java.awt.Container _parent, String _tile, String _type) {
		SQLQueryFrame frame = new SQLQueryFrame(); //
		frame.setVisible(true);
		frame.toFront();
	}

}
