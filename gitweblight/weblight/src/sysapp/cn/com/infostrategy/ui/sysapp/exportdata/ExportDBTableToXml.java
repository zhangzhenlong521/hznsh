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
 * 这个功能极其有用,以后让业务人员帮忙,在线上导出平台的相关表,然后压缩成一个文件,发邮件给我们! 我们在公司就能恢复出现场数据!!最起码模板数据能生成!!
 * 这个功能最重要的要点在于不能使系统内存溢出! 即流式导出!!!
 * 
 * 需要包装的内容:
 * 1.搞两个页签,分别叫导出.导出
 * 2.导出有多个快速按钮(包括将按钮,菜单,权限,工作流等),也有一个根据输入的表名导! 最好还有一个查询所有表的
 * 3.导出需要先选择一个目录,然后导出过程有等待框说明! 
 * 4.导出时最好一个表建一个子目录!! 导入也是先读目录后读文件!! 模板子表导出46秒,导入76秒,不压缩是60个文件,54M,压缩后是1.18M,小的很!其他表自然更小了!!
 * 
 * 5.可以尝试将整个系统的所有表导出来! 看到底要多长时间!! 这里最麻烦的就是处理超大文本字段!  以后尝试一下4000个字符的生成与插入耗时多少!!!
 * @author xch
 *
 */
public class ExportDBTableToXml extends AbstractWorkPanel implements ActionListener {

	private JComboBox dsComBobox_export = null; //数据源!!!
	private WLTButton btn_templet = null; //
	private WLTButton btn_sysroles, btn_workflows, btn_deploysc = null; //
	private JTextArea textArea_1, textArea_2 = null; //
	private WLTButton btn_custtables = null; //

	private JComboBox dsComBobox_import = null; //导入时选择的数据源!!!
	private WLTButton btn_import, btn_querydatacount = null; //
	private TBUtil tbUtil = new TBUtil(); //

	private FrameWorkMetaDataServiceIfc services = null; //

	@Override
	public void initialize() {
		DataSourceVO[] dsVOs = ClientEnvironment.getInstance().getDataSourceVOs(); //所有数据源!!!
		dsComBobox_export = new JComboBox(); //
		for (int i = 0; i < dsVOs.length; i++) {
			dsComBobox_export.addItem(dsVOs[i].getName()); //数据源名称!!
		}
		dsComBobox_export.setBounds(10, 10, 95, 20); //

		btn_templet = new WLTButton("导出模板系列"); //
		btn_templet.setBounds(110, 10, 125, 20); //
		btn_templet.addActionListener(this); //

		btn_sysroles = new WLTButton("导出权限系列");
		btn_sysroles.setBounds(240, 10, 125, 20); //
		btn_sysroles.addActionListener(this);

		btn_workflows = new WLTButton("导出工作流系列");
		btn_workflows.setBounds(370, 10, 125, 20); //
		btn_workflows.addActionListener(this); //

		btn_deploysc = new WLTButton("部署生产机系列");
		btn_deploysc.setBounds(500, 10, 125, 20); //
		btn_deploysc.addActionListener(this); //

		textArea_1 = new JTextArea("pub_menu,pub_role,pub_comboboxdict(pk_pub_comboboxdict),pub_templet_1(pk_pub_templet_1),pub_sequence()"); //
		textArea_1.setFont(LookAndFeel.font); ////
		textArea_1.setLineWrap(true); //
		JScrollPane scrollPanel = new JScrollPane(textArea_1); //
		scrollPanel.setBounds(10, 40, 500, 150); //

		btn_custtables = new WLTButton("导出指定表");
		btn_custtables.setBounds(520, 40, 125, 20); //
		btn_custtables.addActionListener(this); //

		//导出应用,即第一个页签中的应用!
		JPanel panel_1 = WLTPanel.createDefaultPanel(null); //
		panel_1.add(dsComBobox_export); //
		panel_1.add(btn_templet); //
		panel_1.add(btn_sysroles); //
		panel_1.add(btn_workflows); //
		panel_1.add(btn_deploysc); //
		panel_1.add(scrollPanel); //
		panel_1.add(btn_custtables); //

		//导入页签!!
		dsComBobox_import = new JComboBox(); //
		for (int i = 0; i < dsVOs.length; i++) {
			dsComBobox_import.addItem(dsVOs[i].getName()); //数据源名称!!
		}
		dsComBobox_import.setBounds(10, 10, 100, 20); //

		btn_import = new WLTButton("导入XML文件"); //
		btn_import.addActionListener(this); //

		btn_querydatacount = new WLTButton("查看有数据的表"); //
		btn_querydatacount.addActionListener(this); //

		textArea_2 = new JTextArea(); //
		textArea_2.setEditable(false); //
		textArea_2.setFont(LookAndFeel.font); ////
		textArea_2.setLineWrap(true); //
		JScrollPane scrollPanel2 = new JScrollPane(textArea_2); //

		//导入应用,即第二个页签中的内容!!
		JPanel panel_2 = WLTPanel.createDefaultPanel(new BorderLayout()); //
		JPanel northPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
		northPanel2.setOpaque(false); //透明!

		northPanel2.add(dsComBobox_import); //
		northPanel2.add(btn_import); //
		northPanel2.add(btn_querydatacount); //

		panel_2.add(northPanel2, BorderLayout.NORTH); //
		panel_2.add(scrollPanel2, BorderLayout.CENTER); //

		JTabbedPane tabbedPanel = new JTabbedPane(); //
		tabbedPanel.addTab("导出应用", panel_1); //
		tabbedPanel.addTab("导入应用", panel_2); //

		this.setLayout(new BorderLayout()); //
		this.add(tabbedPanel); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_templet) {
			onExportXml(getTempletTableDefines()); //
		} else if (e.getSource() == btn_sysroles) {
			onExportXml(getSysUserRoleCorpTableDefines()); //
		} else if (e.getSource() == btn_workflows) { //导出工作流!
			onExportXml(getWorkFlowTableDefines()); //
		} else if (e.getSource() == btn_deploysc) { //常用部署生产机表
			onExportXml(getDeploySC()); //
		} else if (e.getSource() == btn_custtables) { //导出指定表!!
			String str_text = textArea_1.getText(); //
			if (str_text == null || str_text.trim().equals("")) {
				MessageBox.show(this, "请填写表名!"); //
				return; //
			}
			str_text = str_text.trim(); //
			String[] str_tables = tbUtil.split(str_text, ","); //
			String[][] str_tabPKs = new String[str_tables.length][2]; //
			for (int i = 0; i < str_tables.length; i++) {
				String str_tabName = str_tables[i].trim(); //
				String str_pk = "id"; //
				if (str_tabName.indexOf("(") > 0) { //如果有括号,即指定了主键名
					str_tabName = str_tables[i].substring(0, str_tables[i].indexOf("(")); //表名!
					str_pk = str_tables[i].substring(str_tables[i].indexOf("(") + 1, str_tables[i].indexOf(")")); //主键字段名!
					if (str_pk.trim().equals("")) {
						str_pk = null;
					}
				}
				//System.out.println("表名[" + str_tabName + "],主键[" + str_pk + "]"); ///
				str_tabPKs[i] = new String[] { str_tabName, str_pk }; //
			}
			onExportXml(str_tabPKs); //
		} else if (e.getSource() == btn_import) { //导入!!!
			onImportXml(); //
		} else if (e.getSource() == btn_querydatacount) { //查询有数据的表格!!
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					onQueryAllTableDataCount(); //
				}
			}); //	
		}
	}

	private void onExportXml(final String[][] _tablePKs) {
		int li_rs = MessageBox.showOptionDialog(this, "请选择操作操作类型!在做导出", "提示", new String[] { "生成备份表SQL", "生成删除表数据SQL", "导出XML", "取消" }, 500, 150); //
		if (li_rs == 0) {
			StringBuilder sb_sql = new StringBuilder(); //
			sb_sql.append("在目标上先执行以下操作,把这些表先备份一下:\r\n"); //
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
			sb_sql.append("在目标库中导入前先清空其数据,因为前面已经备份,所以即使错了也能恢复:\r\n"); //
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
		if (MessageBox.showConfirmDialog(this, "您确定要执行此导出操作吗?", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}
		//弹出文件选择框!选择一个路径!!!
		final String str_path = getChooseDir(); //
		if (str_path == null) {
			return;
		}
		long ll_begin = System.currentTimeMillis();
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				export((SplashWindow) e.getSource(), str_path, _tablePKs); //导出
			}
		}, 600, 130, 300, 300); //提示信息显示不完整，故增加窗口高度【李春娟/2012-03-27】
		long ll_end = System.currentTimeMillis();
		MessageBox.show(this, "处理结束,总共耗时[" + ((ll_end - ll_begin) / 1000) + "]秒!"); //
	}

	private String getChooseDir() {
		JFileChooser chooser = new JFileChooser(new File("C:/"));
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("请选择一个目录"); //
		int li_rewult = chooser.showSaveDialog(this);
		if (li_rewult == JFileChooser.CANCEL_OPTION) {
			return null;// 点击取消什么也不做
		}
		if (chooser.getSelectedFile().isFile()) {
			MessageBox.show(this, "请选择一个目录,而非文件!"); //
			return null;
		}
		String str_pathdir = chooser.getSelectedFile().getAbsolutePath(); // ;
		if (str_pathdir.endsWith("\\")) {
			str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1); //
		}
		return str_pathdir; //
	}

	/**
	 * 导出!!!
	 * @param _splash
	 * @param _path
	 * @param _tablePKs
	 */
	private void export(SplashWindow _splash, String _path, String[][] _tablePKs) {
		try {
			for (int i = 0; i < _tablePKs.length; i++) { //遍历所有表!
				String str_tableName = _tablePKs[i][0]; //表名
				String str_pkName = _tablePKs[i][1]; //主键名

				//创建表名的目录!!!
				String str_newPath = _path + "\\" + str_tableName; //加上表名!
				File filenewdir = new File(str_newPath); //
				if (!filenewdir.exists()) {
					filenewdir.mkdirs(); //创建目录!!!以后再搞序号前辍
				}
				String str_daName = (String) dsComBobox_export.getSelectedItem(); //

				int li_beginNo = 0; //起始号!因为有负数,所以需要搞一把!
				if (str_pkName != null) {
					String str_minValue = UIUtil.getStringValueByDS(str_daName, "select min(" + str_pkName + ") from " + str_tableName); //
					if (str_minValue != null && !str_minValue.equals("")) {
						li_beginNo = Integer.parseInt(str_minValue) - 1; //用最小值!!!
					}
				}
				int li_cout = 0; //
				int li_countall = Integer.parseInt(UIUtil.getStringValueByDS(str_daName, "select count(*) from " + str_tableName)); //
				int li_downedCount = 0; //
				while (1 == 1) { //死循环
					long ll_1 = System.currentTimeMillis(); //
					HashMap returnMap = getService().getXMlFromTable1000Records(str_daName, str_tableName, str_pkName, li_beginNo); //
					if (returnMap == null) { //如果为空了则直接返回
						break; //
					}
					int li_recordCount = (Integer) returnMap.get("记录数"); //实际的记录数,应该除最后一页外,其他的都是500
					li_downedCount = li_downedCount + li_recordCount; //
					int li_perCent = ((li_downedCount * 100) / li_countall); //百分比!  50/100
					li_beginNo = (Integer) returnMap.get("结束点"); //实际内容的结束号!					
					String str_xml = (String) returnMap.get("内容"); //
					li_cout++; //
					String str_fileName = str_tableName + "_" + (100000 + li_cout) + ".xml"; //
					FileOutputStream fileOut = new FileOutputStream(str_newPath + "\\" + str_fileName, false); //
					tbUtil.writeStrToOutputStream(fileOut, str_xml); //输出至文件
					long ll_2 = System.currentTimeMillis(); //
					_splash.setWaitInfo("处理[" + str_fileName + "],耗时[" + (ll_2 - ll_1) + "]!!\r\n本表完成比例[" + li_downedCount + "/" + li_countall + "=" + li_perCent + "%],总比例[" + (i + 1) + "/" + _tablePKs.length + "]"); //
					if (str_pkName == null) { //如果主键为空,因为是一次取出所有数据!则直接退出!!!比如pub_sequence表!!
						break;
					}
					Thread.currentThread().sleep(100); //休息一会!!
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	public String[][] getTempletTableDefines() {
		return new String[][] { // 
		{ "pub_templet_1", "pk_pub_templet_1" }, //模板主表
				{ "pub_templet_1_item", "pk_pub_templet_1_item" }, //模板子表
				{ "pub_regbuttons", "id" }, //注册按钮
				{ "pub_comboboxdict", "pk_pub_comboboxdict" }, //
				{ "pub_regformatpanel", "id" }, //
				{ "pub_refregister", "id" }, //
				{ "pub_option", "id" }, //
				{ "pub_sequence", null }, //如果没有主键
		};
	}

	public String[][] getSysUserRoleCorpTableDefines() {
		return new String[][] { // 
		{ "pub_menu", "id" }, //菜单
				{ "pub_corp_dept", "id" }, //机构
				{ "pub_user", "id" }, //人员
				{ "pub_role", "id" }, //角色
				{ "pub_user_role", "id" }, //人员与角色
				{ "pub_user_menu", "id" }, //人员与菜单
				{ "pub_role_menu", "id" }, //角色与菜单
				{ "pub_user_post", "id" }, //人员机构
		};
	}

	public String[][] getWorkFlowTableDefines() {
		return new String[][] { // 
		{ "pub_billtype", "id" }, //单据类型
				{ "pub_busitype", "id" }, //业务类型
				{ "pub_wf_process", "id" }, //流程
				{ "pub_wf_activity", "id" }, //环节
				{ "pub_wf_transition", "id" }, //连线
				{ "pub_wf_group", "id" }, //组
				{ "pub_wf_dypardefines", "id" }, //动态参与者
				{ "pub_workflowassign", "id" }, //流程分配
				{ "pub_wf_prinstance", "id" }, //流程实例
				{ "pub_wf_dealpool", "id" }, //流程池
				{ "pub_task_deal", "id" }, //待办任务
				{ "pub_task_off", "id" }, //已办任务
		};
	}

	//部署至生产机时最常用的15张表,即这些表以后永远是单向部署!!
	//在生产机上永远不要修改这些配置!
	public String[][] getDeploySC() {
		return new String[][] { // 
		{ "pub_templet_1", "pk_pub_templet_1" }, //模板主表
				{ "pub_templet_1_item", "pk_pub_templet_1_item" }, //模板子表
				{ "pub_billcelltemplet_h", "id" }, //Excel主表
				{ "pub_billcelltemplet_d", "id" }, //Excel子表
				{ "pub_regbuttons", "id" }, //注册按钮
				{ "pub_regformatpanel", "id" }, //注册样板
				{ "pub_refregister", "id" }, //注册参照
				{ "pub_wf_process", "id" }, //流程配置
				{ "pub_wf_activity", "id" }, //流程环节
				{ "pub_wf_transition", "id" }, //连线
				{ "pub_wf_group", "id" }, //流程组
				{ "pub_wf_dypardefines", "id" }, //动态参与者
				{ "pub_workflowassign", "id" }, //流程分配
				{ "pub_datapolicy", "id" }, //数据权限策略!
				{ "pub_datapolicy_b", "id" }, //策略子表
				{ "pub_comboboxdict", "pk_pub_comboboxdict" }, //

		};
	}

	/**
	 * 查询所有表格数据结果集,在安装与实施过程中到处用到!! 【xch/2012-02-23】
	 */
	private void onQueryAllTableDataCount() {
		try {
			SysAppServiceIfc sevice = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			String str_info = sevice.getAllTableRecordCountInfo(null); //
			textArea_2.setText(""); //先清空数据
			textArea_2.setText(str_info); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	//点击导入按钮!!
	private void onImportXml() {
		if (MessageBox.showConfirmDialog(this, "您确定要执行此导入操作吗?\r\n强烈建议操作之前先备份一下数据库!\r\n一定要注意数据源是否正确!", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
			return;
		}

		String str_dsName = (String) dsComBobox_import.getSelectedItem(); //
		if (!MessageBox.confirm(this, "你确认要导入到【" + str_dsName + "】中吗？？？？\r\n\r\n经常有人把数据导入错误的地方！！！！！\r\n这是非常危险的！！！！")) {
			return; //
		}

		//弹出文件选择框!选择一个路径!!!
		final String str_path = getChooseDir(); //
		if (str_path == null) {
			return;
		}
		long ll_begin = System.currentTimeMillis();
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				importXml((SplashWindow) e.getSource(), str_path); //
			}
		}, 600, 130, 300, 300); //提示信息显示不完整，故增加窗口高度【李春娟/2012-03-27】
		long ll_end = System.currentTimeMillis();
		MessageBox.show(this, "导入结束,总共耗时[" + ((ll_end - ll_begin) / 1000) + "]秒!"); //
	}

	/**
	 * 导入数据!!!
	 * @param _splash
	 * @param _rootDir
	 */
	private void importXml(SplashWindow _splash, String _rootDir) {
		try {
			String str_dsName = (String) dsComBobox_import.getSelectedItem(); //
			File rootDir = new File(_rootDir); //
			File[] tableDirFiles = rootDir.listFiles(); //表名目录!!
			ArrayList tablenames = new ArrayList();//为下面更新sequence而记录表名【李春娟/2014-02-28】
			for (int f = 0; f < tableDirFiles.length; f++) { //遍历所有目录
				if (tableDirFiles[f].isDirectory()) { //必须是目录
					String str_tableName = tableDirFiles[f].getName(); //目录名就是表名!!!
					tablenames.add(str_tableName.toUpperCase());//【李春娟/2014-02-28】
					UIUtil.executeUpdateByDS(str_dsName, "delete from  " + str_tableName + " where 1=1"); //先彻底干掉表中的数据!!!
					File[] files = tableDirFiles[f].listFiles(); //遍历所有xml文件!!
					for (int i = 0; i < files.length; i++) {
						long ll_1 = System.currentTimeMillis(); //
						String str_xml = tbUtil.readFromInputStreamToStr(new FileInputStream(files[i])); //
						getService().importXmlToTable1000Records(str_dsName, files[i].getName(), str_xml); //导入一张表!!
						long ll_2 = System.currentTimeMillis(); //
						_splash.setWaitInfo("导入[" + files[i].getName() + "],耗时[" + (ll_2 - ll_1) + "],本表完成比例[" + (i + 1) + "/" + files.length + "],总共比例[" + (f + 1) + "/" + tableDirFiles.length + "]"); //
						Thread.currentThread().sleep(100); //休息一会!!
					}
				}
			}
			//更新sequence【李春娟/2014-02-28】
			_splash.setWaitInfo("正在努力更新Sequence..."); //
			SysAppServiceIfc service = (SysAppServiceIfc) RemoteServiceFactory.getInstance().lookUpService(SysAppServiceIfc.class); //定义远程服务
			service.resetSequence(tablenames);//重置Sequence，否则以后很可能会出现主键冲突，如果tables.xml中有则更新【李春娟/2014-02-28】
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
