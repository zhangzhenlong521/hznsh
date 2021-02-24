package cn.com.infostrategy.ui.sysapp.database.compare;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

public class DatabaseCompare extends AbstractWorkPanel {

	/**
	 * ��ʾ
	 */
	private static final long serialVersionUID = -7379929844794709L;
	private DatabasesMenu databaseMenu = null;
	private DatabasesTemplet databaseTemplet = null;
	private DatabasesRegButton regButton = null;
	private DatabasesTableCompare databaseTable = null;
	private JTabbedPane jTabbedpane = null;
	private DatabasesFormatPanel databaseFormatPanel = null;

	public void initialize() {
		jTabbedpane = new JTabbedPane();

		databaseMenu = new DatabasesMenu(); //�˵�
		databaseTemplet = new DatabasesTemplet(); //ģ��
		regButton = new DatabasesRegButton(); //ע�ᰴť
		databaseTable = new DatabasesTableCompare(); //�����
		databaseFormatPanel = new DatabasesFormatPanel(); //ע������
		databaseMenu.initialize();
		databaseTemplet.initialize();
		regButton.initialize();
		databaseTable.initialize();
		databaseFormatPanel.initialize();

		jTabbedpane.add("���ݿ�Ƚ�", databaseTable);
		jTabbedpane.add("ע�ᰴť�Ƚ�", regButton);
		jTabbedpane.add("ģ��Ƚ�", databaseTemplet);
		jTabbedpane.add("ע������Ƚ�", databaseFormatPanel);
		jTabbedpane.add("�˵��Ƚ�", databaseMenu);
		this.setLayout(new BorderLayout());
		this.add(jTabbedpane, BorderLayout.CENTER);
	}

}
