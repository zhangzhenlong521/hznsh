package cn.com.infostrategy.ui.sysapp.database.transfer;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.report.BillHtmlPanel;


public class CMPPanel_MultiTransfer extends AbstractWorkPanel  implements ActionListener {

	/**
	 * 表示号
	 */
	private static final long serialVersionUID = 340089321889669875L;

	private JPanel jPanel = null;
	private JLabel jLabel_Left = null;
	private JLabel jLabel_Right = null;
	private JLabel jLabel_Type = null;   //数据库类型    20130425
	private JLabel jLabel_Prefix = null;   //表名前缀    20130428
	private JComboBox jComboBox_Left = null;
	private JComboBox jComboBox_Right = null;
	private DataSourceVO[] dataSource = null;
	private JComboBox jComboBox_Type = null;    //数据库类型    20130425
	private JTextField jTextPrefix  =null;    //表名前缀   20130428
	private JButton jButton1 = null;   //表结构比较
	private JButton jButton2 = null;   //导入新增表的数据
	private JButton jButton3 = null;   //导入已有表的数据
	private JButton jButton4 = null;//将数据库1中的数据导出到数据库2中    袁江晓   20130502 添加
	private BillHtmlPanel billHtmlPanel = null;
	private HashMap map = null;

	public void initialize() {
		dataSource = ClientEnvironment.getInstance().getDataSourceVOs();
		billHtmlPanel = new BillHtmlPanel();
		map = new HashMap();
		jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT,0,10));
		jLabel_Left = new JLabel("数据源1:", SwingConstants.RIGHT);
		jLabel_Right = new JLabel("数据源2:", SwingConstants.RIGHT);
		jLabel_Type = new JLabel("类型", SwingConstants.RIGHT);
		jLabel_Prefix = new JLabel("表名前缀:", SwingConstants.RIGHT);
		jTextPrefix=new JTextField();

		jLabel_Left.setPreferredSize(new Dimension(55, 20));
		jLabel_Right.setPreferredSize(new Dimension(55,20));
		jLabel_Type.setPreferredSize(new Dimension(55,20));
		jLabel_Prefix.setPreferredSize(new Dimension(55,20));

		jComboBox_Left = new JComboBox();
		jComboBox_Right = new JComboBox();
		jComboBox_Type = new JComboBox();
		for (int i = 0; i < dataSource.length; i++) {
			jComboBox_Left.addItem(dataSource[i].getName());
			jComboBox_Right.addItem(dataSource[i].getName());
		}
		jComboBox_Type.addItem("ORACLE");
		jComboBox_Type.addItem("Mysql");
		
		
		jButton1 = new WLTButton("表结构比较");
		jButton2 = new WLTButton("导入新增表数据");
		jButton3 = new WLTButton("导入已有表数据");
		jButton4 = new WLTButton("导入所有数据");//如果没有表则在源2中创建，如果表机构缺少列不同则添加，如果源2中的重复表有数据则先删除掉
		jButton1.addActionListener(this);
		jButton2.addActionListener(this);
		jButton3.addActionListener(this);
		jButton4.addActionListener(this);

		jComboBox_Left.setPreferredSize(new Dimension(100, 20));
		jComboBox_Right.setPreferredSize(new Dimension(100, 20));
		jComboBox_Type.setPreferredSize(new Dimension(100, 20));
		jTextPrefix.setPreferredSize(new Dimension(70, 20));

		jPanel.add(jLabel_Left);
		jPanel.add(jComboBox_Left);
		jPanel.add(jLabel_Right);
		jPanel.add(jComboBox_Right);
		jPanel.add(jLabel_Type);
		jPanel.add(jComboBox_Type);
		
		jPanel.add(jLabel_Prefix);//表名前缀
		jPanel.add(jTextPrefix);  //表名前缀
		jPanel.add(jButton1);
		jPanel.add(jButton2);
		jPanel.add(jButton3);
		jPanel.add(jButton4);
		this.setLayout(new BorderLayout());
		this.add(jPanel, BorderLayout.NORTH);
		this.add(billHtmlPanel, BorderLayout.CENTER);

	}

	public void actionPerformed(ActionEvent e) {
		if(jComboBox_Left.getSelectedItem().toString().equals(jComboBox_Right.getSelectedItem().toString())){
			MessageBox.show("不能比较两个相同的数据源！");
			return ;
		}
		if (e.getSource() == jButton1) { //比较
			map.put("type", "1");
			map.put("title", jButton1.getText());
			map.put("datasource1", jComboBox_Left.getSelectedItem().toString());
			map.put("datasource2", jComboBox_Right.getSelectedItem().toString());
			map.put("databaseType", jComboBox_Type.getSelectedItem().toString());
			map.put("tabenamePrefix",jTextPrefix.getText());  //表前缀
			billHtmlPanel.loadhtml("cn.com.infostrategy.bs.sysapp.database.transfer.CreateHtmlForDatabasesCompare", map);
		}if (e.getSource() == jButton2) {//导入新增表数据
			if(MessageBox.confirm(this, "确认要导出新增的表结构及数据吗？")){
				map.put("type", "2");
				map.put("title", jButton1.getText());
				map.put("datasource1", jComboBox_Left.getSelectedItem().toString());
				map.put("datasource2", jComboBox_Right.getSelectedItem().toString());
				map.put("databaseType", jComboBox_Type.getSelectedItem().toString());
				map.put("tabenamePrefix",jTextPrefix.getText());  //表前缀
				billHtmlPanel.loadhtml("cn.com.infostrategy.bs.sysapp.database.transfer.CreateHtmlForDatabasesCompare", map);
			}
		}if (e.getSource() == jButton3) {//导入已有表数据
			map.put("type", "3");
			map.put("title", jButton1.getText());
			map.put("datasource1", jComboBox_Left.getSelectedItem().toString());
			map.put("datasource2", jComboBox_Right.getSelectedItem().toString());
			map.put("databaseType", jComboBox_Type.getSelectedItem().toString());
			map.put("tabenamePrefix",jTextPrefix.getText());  //表前缀
			billHtmlPanel.loadhtml("cn.com.infostrategy.bs.sysapp.database.transfer.CreateHtmlForDatabasesCompare", map);
		}else if (e.getSource() == jButton4) {  ////如果没有表则在源2中创建，如果表机构缺少列不同则添加，如果源2中的重复表有数据则先删除掉   袁江晓  添加导入表数据功能包括表结构
			String confirm_str="将数据源1中的数据导入到数据源2中，如果2中之前的数据将会被删除，是否继续？";
			if(!jTextPrefix.getText().trim().equals("")){
				confirm_str="将数据源1中以"+jTextPrefix.getText().trim()+"开头的表的数据导入到数据源2中，如果2中之前相应的表有数据将会被删除，是否继续？";
			}else{
				confirm_str="将数据源1中的数据导入到数据源2中，如果2中之前相应的表有数据将会被删除，是否继续？";
			}
			boolean bb=MessageBox.confirm(this, confirm_str);
			if(bb){
				map.put("type", "4");
				map.put("title", jButton4.getText());
				map.put("datasource1", jComboBox_Left.getSelectedItem().toString());
				map.put("datasource2", jComboBox_Right.getSelectedItem().toString());
				map.put("databaseType", jComboBox_Type.getSelectedItem().toString());
				map.put("tabenamePrefix",jTextPrefix.getText());  //表前缀
				billHtmlPanel.loadhtml("cn.com.infostrategy.bs.sysapp.database.transfer.CreateHtmlForDatabasesCompare", map);
			}
		}
	}
}
