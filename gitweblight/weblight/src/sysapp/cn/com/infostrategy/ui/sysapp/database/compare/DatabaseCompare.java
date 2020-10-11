package cn.com.infostrategy.ui.sysapp.database.compare;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

public class DatabaseCompare extends AbstractWorkPanel {

	/**
	 * 表示
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

		databaseMenu = new DatabasesMenu(); //菜单
		databaseTemplet = new DatabasesTemplet(); //模板
		regButton = new DatabasesRegButton(); //注册按钮
		databaseTable = new DatabasesTableCompare(); //物理表
		databaseFormatPanel = new DatabasesFormatPanel(); //注册样板
		databaseMenu.initialize();
		databaseTemplet.initialize();
		regButton.initialize();
		databaseTable.initialize();
		databaseFormatPanel.initialize();

		jTabbedpane.add("数据库比较", databaseTable);
		jTabbedpane.add("注册按钮比较", regButton);
		jTabbedpane.add("模版比较", databaseTemplet);
		jTabbedpane.add("注册样版比较", databaseFormatPanel);
		jTabbedpane.add("菜单比较", databaseMenu);
		this.setLayout(new BorderLayout());
		this.add(jTabbedpane, BorderLayout.CENTER);
	}

}
