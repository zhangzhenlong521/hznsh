package cn.com.infostrategy.ui.sysapp.database.compare;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.report.BillHtmlPanel;

public class DatabasesMenu extends JPanel implements ActionListener {

	/**
	 * 表示
	 */
	private static final long serialVersionUID = 205553401552341176L;

	private JPanel jPanel = null;
	private JLabel jLabel_Left = null;
	private JLabel jLabel_Right = null;
	private JComboBox jComboBox_Left = null;
	private JComboBox jComboBox_Right = null;
	private DataSourceVO[] dataSource = null;
	private JButton jButtonFor1 = null;
	private JButton jButtonFor2 = null;
	private JButton jButtonFor3 = null;
	private JButton jButtonFor4 = null;
	private BillHtmlPanel billHtmlPanel = null;
	private HashMap<String, String> map = null;

	public void initialize() {
		dataSource = ClientEnvironment.getInstance().getDataSourceVOs();
		billHtmlPanel = new BillHtmlPanel();
		map = new HashMap();
		jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,10));
		
		jLabel_Left = new JLabel("数据源1:", SwingConstants.RIGHT);
		jLabel_Right = new JLabel("数据源2:", SwingConstants.RIGHT);

		jLabel_Left.setPreferredSize(new Dimension(55, 20));
		jLabel_Right.setPreferredSize(new Dimension(55, 20));

		jComboBox_Left = new JComboBox();
		jComboBox_Right = new JComboBox();
		for (int i = 0; i < dataSource.length; i++) {
			jComboBox_Left.addItem(dataSource[i].getName());
			jComboBox_Right.addItem(dataSource[i].getName());
		}

		jButtonFor1 = new WLTButton("只在源1中的数据");
		jButtonFor2 = new WLTButton("只在源2中的数据");
		jButtonFor3 = new WLTButton("两个源中的不同数据");
		jButtonFor4 = new WLTButton("两个源中的相同数据");
		jButtonFor1.addActionListener(this);
		jButtonFor2.addActionListener(this);
		jButtonFor3.addActionListener(this);
		jButtonFor4.addActionListener(this);

		//		jButtonFor1.setPreferredSize(new Dimension(60,25));
		//		jButtonFor2.setPreferredSize(new Dimension(60,25));
		//		jButtonFor3.setPreferredSize(new Dimension(60,25));
		//		jButtonFor4.setPreferredSize(new Dimension(60,25));

		jComboBox_Left.setPreferredSize(new Dimension(100, 20));
		jComboBox_Right.setPreferredSize(new Dimension(100, 20));

		jPanel.add(jLabel_Left);
		jPanel.add(jComboBox_Left);
		jPanel.add(jLabel_Right);
		jPanel.add(jComboBox_Right);
		jPanel.add(jButtonFor1);
		jPanel.add(jButtonFor2);
		jPanel.add(jButtonFor3);
		//		jPanel.add(jButtonFor4);

		this.setLayout(new BorderLayout());
		this.add(jPanel, BorderLayout.NORTH);
		this.add(billHtmlPanel, BorderLayout.CENTER);

	}

	public void actionPerformed(ActionEvent e) {
		if(jComboBox_Left.getSelectedItem().toString().equals(jComboBox_Right.getSelectedItem().toString())){
			MessageBox.show("不能比较两个相同的数据源！");
			return ;
		}
		if (e.getSource() == jButtonFor1) {
			map.put("title", jButtonFor1.getText());
			map.put("datasource1", jComboBox_Left.getSelectedItem().toString());
			map.put("datasource2", jComboBox_Right.getSelectedItem().toString());
			map.put("type", "0");
			billHtmlPanel.loadhtml("cn.com.infostrategy.bs.sysapp.database.compare.CreateHtmlForMenuCompare", map);
		} else if (e.getSource() == jButtonFor2) {
			map.put("type", "1");
			map.put("title", jButtonFor2.getText());
			map.put("datasource1", jComboBox_Left.getSelectedItem().toString());
			map.put("datasource2", jComboBox_Right.getSelectedItem().toString());
			billHtmlPanel.loadhtml("cn.com.infostrategy.bs.sysapp.database.compare.CreateHtmlForMenuCompare", map);
		} else if (e.getSource() == jButtonFor3) {
			map.put("type", "2");
			map.put("title", jButtonFor3.getText());
			map.put("datasource1", jComboBox_Left.getSelectedItem().toString());
			map.put("datasource2", jComboBox_Right.getSelectedItem().toString());
			billHtmlPanel.loadhtml("cn.com.infostrategy.bs.sysapp.database.compare.CreateHtmlForMenuCompare", map);
		} else if (e.getSource() == jButtonFor4) {
			map.put("type", "3");
			map.put("title", jButtonFor4.getText());
			map.put("datasource1", jComboBox_Left.getSelectedItem().toString());
			map.put("datasource2", jComboBox_Right.getSelectedItem().toString());
			billHtmlPanel.loadhtml("cn.com.infostrategy.bs.sysapp.database.compare.CreateHtmlForMenuCompare", map);
		}
	}
}
