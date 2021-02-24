package cn.com.infostrategy.ui.sysapp.install;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.IAppletLoader;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc;

/**
 * 安装界面,
 * @author xch
 *
 */
public class InstallLoader implements IAppletLoader, Serializable, ActionListener {

	private static final long serialVersionUID = -2689635210485628183L;
	private JPanel mainPanel = null; //
	private JButton btn_install = null; //按钮!!
	private JTextArea textArea = null; //文本框!!
	private Vector v_msg = new Vector(); //
	private Font font = new Font("新宋体", Font.PLAIN, 12); //
	private ArrayList al_toggleButtons = new ArrayList(); //所有勾选框或单选按钮!!
	private HashMap initDataHis = new HashMap(); //初始化数据安装记录,后面安装的会冲掉前面。

	public void loadApplet(JApplet _applet, JPanel _mainPanel) throws Exception {
		mainPanel = _mainPanel; //
		mainPanel.removeAll(); //先清空所有控件
		mainPanel.setLayout(new BorderLayout()); //设置布局类

		_mainPanel.add(getNorthPanel(), BorderLayout.NORTH); //上面的面板
		textArea = new JTextArea(); //
		textArea.setFont(font); //
		_mainPanel.add(new JScrollPane(textArea), BorderLayout.CENTER); //

		_mainPanel.updateUI(); //

		//处理消息堆!!!
		new java.util.Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				dealMsg(); //		
			}
		}, 0, 50);
	}

	/**
	 * 上面的面板!!!
	 * @return
	 */
	private JPanel getNorthPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 5)); //
		try {

			btn_install = new JButton("安装"); //
			btn_install.setFont(font); //
			btn_install.setFocusable(false); //
			btn_install.setMargin(new Insets(0, 0, 0, 0)); //
			btn_install.setPreferredSize(new Dimension(80, 20)); //
			btn_install.addActionListener(this); //
			panel.add(btn_install); //

			//根据系统参数动态输出还可以安装哪些产品或项目??
			SysAppServiceIfc services = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
			String[][] str_installPackages = services.getAllInstallPackages(null); //取得所有安装的包!
			//for (int i = 0; i < str_installPackages.length; i++) {
			//System.out.println(str_installPackages[i][0] + "&" + str_installPackages[i][1] + "&" + str_installPackages[i][2]); //输出结果的样子:【/com/pushworld/ipushgrc/bs/install/】【普信GRC产品】【name=合规风险管理;xtdatadir=hegui@name=内部控制管理;xtdatadir=neikong@name=全面风险管理;xtdatadir=fengxian@name=操作风险管理;xtdatadir=caozuo】
			//}
			for (int i = 0; i < str_installPackages.length; i++) { //遍历所有安装包!!
				String str_apptext = str_installPackages[i][2]; //该产品的具体应用的名称!!
				if (str_apptext == null || str_apptext.trim().equals("")) { //如果为空!!则是勾选框!
					JCheckBox checkBox = new JCheckBox(str_installPackages[i][1]); //默认必须是选中的!
					checkBox.setFont(font); //
					checkBox.setFocusable(false); //
					checkBox.setToolTipText("安装包路径[" + str_installPackages[i][0] + "]"); //
					checkBox.putClientProperty("package_prefix", str_installPackages[i][0]); //平台的包名!
					if (i < 2) { //平台和合规产品,则强行勾选上,且不可编辑!!! Gwang
						checkBox.setSelected(true); //
						checkBox.setEnabled(false); //
					} else {
						checkBox.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); //
						//checkBox.setForeground(Color.BLUE); //
					}
					panel.add(checkBox); //
					al_toggleButtons.add(checkBox); //加入向量表中,安装时需要遍历这个!
				} else { //如果有多个应用,则是一堆单选按钮!!!
					JLabel label_1 = new JLabel(str_installPackages[i][1] + "【", SwingConstants.RIGHT); //
					label_1.setFont(font); //
					label_1.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0)); //
					//label_1.setForeground(Color.BLUE); //
					panel.add(label_1); //先加入一个说明!!
					String[] str_apps = split(str_apptext, "@"); //分割!即有多个应用!!!
					JRadioButton[] radioBtns = new JRadioButton[str_apps.length]; //
					ButtonGroup btnGroup = new ButtonGroup(); //
					for (int j = 0; j < str_apps.length; j++) { //
						HashMap parMap = convertStrToMapByExpress(str_apps[j], ";", "="); //将参数解析成map
						String str_appname = (String) parMap.get("name"); //具体应用的名称!!
						String str_xtdatadir = (String) parMap.get("xtdatadir"); //初始化数据的子目录!!
						radioBtns[j] = new JRadioButton(str_appname); //
						radioBtns[j].setFont(font); //
						radioBtns[j].setFocusable(false); //
						radioBtns[j].setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 2)); //
						radioBtns[j].setMargin(new Insets(0, 0, 0, 0)); //
						radioBtns[j].setToolTipText("安装包路径[" + str_installPackages[i][0] + "],初始化数据子路径[" + str_xtdatadir + "]"); //
						radioBtns[j].putClientProperty("package_prefix", str_installPackages[i][0]); //平台的包名!
						radioBtns[j].putClientProperty("xtdatadir", str_xtdatadir); //初始化数据的子目录!!
						btnGroup.add(radioBtns[j]); //
						panel.add(radioBtns[j]); //
						al_toggleButtons.add(radioBtns[j]); //加入向量表中,安装时需要遍历这个!
					}
					JLabel label_2 = new JLabel("】", SwingConstants.LEFT); //
					label_2.setFont(font); //
					//label_2.setForeground(Color.BLUE); //
					panel.add(label_2); //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_install) {
			onInstall(); //
		}
	}

	/**
	 * 执行安装!!
	 */
	private void onInstall() {
		if (btn_install.getText().equals("安装")) { //如果是非暂停状态
			if (JOptionPane.showConfirmDialog(mainPanel, "你是否真的想执行安装操作么?\r\n请注意正确选择安装内容与目标数据源!", "提示", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
				return;
			}
			btn_install.setText("停止安装"); //
			new Thread() {
				public void run() {
					doAction(); //必须新开一个线程处理任务,那个time才能产生效果!!!
				}
			}.start(); //
		} else if (btn_install.getText().equals("停止安装")) { //停止
			btn_install.setText("已停止"); //
			return;
		} else { //停止
			JOptionPane.showMessageDialog(mainPanel, "请关闭安装程序重新运行"); //
			return; //
		}
	}

	protected void doAction() {
		textArea.setText(""); //清空原来的数据!!
		try {
			long ll_1 = System.currentTimeMillis(); //
			for (int i = 0; i < al_toggleButtons.size(); i++) {
				JToggleButton btn = (JToggleButton) al_toggleButtons.get(i); //
				if (btn.isSelected()) { //必须是勾选上的才真正安装!!
					String str_name = btn.getText(); //
					String str_package = (String) btn.getClientProperty("package_prefix"); //包名!
					String str_xtdatadir = (String) btn.getClientProperty("xtdatadir"); //初始化数据的子目录!
					installOneAppByPackageName(str_package, str_name, str_xtdatadir); //安装这个包!!
				}
				if (btn_install.getText().equals("已停止")) { //万中中途错了,可以立即停止!
					return; //
				}
			}

			//最后构建反向序列!!!
			for (int i = 0; i < al_toggleButtons.size(); i++) {
				JToggleButton btn = (JToggleButton) al_toggleButtons.get(i); //
				if (btn.isSelected()) { //必须是勾选上的才真正安装!!
					String str_name = btn.getText(); //
					String str_package = (String) btn.getClientProperty("package_prefix"); //包名!
					v_msg.add("开始反向构建序列 [" + str_name + "].....\r\n"); //以前反向构建序列有个bug,即不管是否选中,都是为所有表构建序列,必须放在循环里面执行!【xch/2012-06-07】
					SysAppServiceIfc services = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //
					String str_reverseResult = services.reverseSetSequenceValue(str_package); //构建反向序列!
					v_msg.add("反向构建序列结束 [" + str_name + "],结果[" + str_reverseResult + "]....\r\n\r\n"); //
				}
			}

			//清空缓存!!
			FrameWorkCommServiceIfc commServices = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class); //
			ClientEnvironment.getInstance().setClientSysOptions(commServices.reLoadDataFromDB(false)); //重新加载缓存,曾经遇到过安装后立即登录,结果office控件中老是有问题,后来找了半天，才发现了是因为Tomat没有重启,缓存造成的!【xch/2012-03-06】
			v_msg.add("重置缓存成功....\r\n"); //

			long ll_2 = System.currentTimeMillis(); //
			v_msg.add("\r\n安装全部结束,共耗时[" + ((ll_2 - ll_1) / 1000) + "]秒,强烈建议重启中间件后再正式访问！！！\r\n");
			btn_install.setText("安装结束"); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			JOptionPane.showMessageDialog(mainPanel, "安装发生异常:[" + ex.getMessage() + "],请至控制台查看明细!!"); //
		}
	}

	/**
	 * 安装某一个产品/模块/项目
	 * @param _packageName
	 * @param _viewName
	 */
	private void installOneAppByPackageName(String _package, String _name, String _xtdatadir) throws Exception {
		SysAppServiceIfc services = (SysAppServiceIfc) UIUtil.lookUpRemoteService(SysAppServiceIfc.class); //

		//安装物理表!!
		if (btn_install.getText().equals("已停止")) {
			return; //
		}
		v_msg.add("准备安装 [" + _name + "] 物理表,安装包路径[" + _package + "database/tables.xml].....\r\n"); //
		String[] str_allPlatformTables = services.getAllIntallTablesByPackagePrefix(_package); //根据包名找物理表!
		if (str_allPlatformTables != null) { //如果有物理表!!
			v_msg.add("开始安装 [" + _name + "] 物理表,发现一共有[" + str_allPlatformTables.length + "]个表.....\r\n"); //
			for (int i = 0; i < str_allPlatformTables.length; i++) { //遍历安装表!!
				if (btn_install.getText().equals("已停止")) {
					return; //
				}
				String str_result = services.createTableByPackagePrefix(_package, str_allPlatformTables[i]); //安装物理表,即执行Create table脚本!!
				v_msg.add("<" + getNoStr(i, str_allPlatformTables.length) + "/" + str_allPlatformTables.length + ">安装 [" + _name + "] 物理表 [" + str_allPlatformTables[i] + "] 结束,结果:" + str_result + "\r\n"); //加入,然后等待另一个线程去处理!!!!
			}
		} else {
			v_msg.add("开始安装 [" + _name + "] 物理表,没有发现一个物理表定义文件!!\r\n"); //	
		}
		v_msg.add("\r\n");

		//安装视图!!
		if (btn_install.getText().equals("已停止")) {
			return; //
		}
		v_msg.add("准备安装 [" + _name + "] 视图,安装包路径[" + _package + "database/views.xml]....\r\n"); //
		String[] str_allPlatformViews = services.getAllIntallViewsByPackagePrefix(_package); //取得所有视图!!!
		if (str_allPlatformViews != null) {
			v_msg.add("开始安装 [" + _name + "] 视图,发现一共有[" + str_allPlatformViews.length + "]个视图.....\r\n"); //
			for (int i = 0; i < str_allPlatformViews.length; i++) {
				if (btn_install.getText().equals("已停止")) {
					return; //
				}
				String str_result = services.createViewByPackagePrefix(_package, str_allPlatformViews[i]);
				v_msg.add("<" + getNoStr(i, str_allPlatformViews.length) + "/" + str_allPlatformViews.length + ">安装 [" + _name + "] 视图 [" + str_allPlatformViews[i] + "] 结束,结果:" + str_result + "\r\n"); //加入,然后等待另一个线程去处理!!!!
			}
		} else {
			v_msg.add("开始安装 [" + _name + "] 视图,没有发现一个视图定义文件!!\r\n"); //	
		}
		v_msg.add("\r\n");

		//安装初始数据!!!
		if (btn_install.getText().equals("已停止")) {
			return; //
		}
		String[] str_xdtataitem = null; //
		if (_xtdatadir != null && !_xtdatadir.trim().equals("")) { //如果不为空,则先要安装公共的数据,再安装特定子目录的!!
			str_xdtataitem = new String[] { null, _xtdatadir }; //
		} else {
			str_xdtataitem = new String[] { null }; //
		}
		for (int r = 0; r < str_xdtataitem.length; r++) {
			if (btn_install.getText().equals("已停止")) {
				return; //
			}
			if (str_xdtataitem[r] == null) {
				v_msg.add("准备安装 [" + _name + "] 初始数据,安装包路径[" + _package + "xtdata/].....\r\n"); //
			} else {
				v_msg.add("准备安装 [" + _name + "] 初始数据,安装包路径[" + _package + "xtdata/" + str_xdtataitem[r] + "].....\r\n"); //
			}
			String[] str_allPlatformInitDataTables = services.getAllIntallInitDataByPackagePrefix(_package, str_xdtataitem[r]); //取得所有数据文件
			if (str_allPlatformInitDataTables != null) { //如果不为空
				v_msg.add("开始安装 [" + _name + "] 初始数据,发现有[" + str_allPlatformInitDataTables.length + "]个文件\r\n"); //

				List list = new ArrayList();
//				StringBuffer tables = new StringBuffer();//记录重复的表
				for (int i = 0; i < str_allPlatformInitDataTables.length; i++) { //
					String tableName = str_allPlatformInitDataTables[i];
					if (tableName.contains("_100")) { //law_law_100001.xml类型
						tableName = split(tableName, "_100")[0];
					} else {
						tableName = split(tableName, ".")[0]; // bsd_bsact.xml类型
					}
					if (initDataHis.containsKey(tableName)) { //判断执行到哪个包了，是否已经删除过数据。
						if (!(Boolean) initDataHis.get(tableName)) {
							list.add("delete from " + tableName);
							initDataHis.put(tableName, true);//已经删除过数据,每张表在一个包中只能清空一次
//							tables.append(tableName+"、");
						}
					} else {
						initDataHis.put(tableName, true); //如果第一次插入数据库，不用清除。							
					}
				}
				if (list.size() > 0) {
					v_msg.add("准备清除 [" + _name + "]已安装的重复数据,共[" + list.size() + "]张表\r\n"); //
					UIUtil.executeBatchByDS(null, list);
				}
				Iterator it = initDataHis.keySet().iterator();
				while (it.hasNext()) { //把已经安装过表记录全部重置
					String t_name = (String) it.next();
					initDataHis.put(t_name, false);
				}
				for (int i = 0; i < str_allPlatformInitDataTables.length; i++) {
					if (btn_install.getText().equals("已停止")) {
						return; //
					}
					String str_result = services.InsertInitDataByPackagePrefix(_package, str_xdtataitem[r], str_allPlatformInitDataTables[i]); //实际安装初始化数据!!!
					v_msg.add("<" + getNoStr(i, str_allPlatformInitDataTables.length) + "/" + str_allPlatformInitDataTables.length + ">安装 [" + _name + "] 初始数据 [" + str_allPlatformInitDataTables[i] + "] 结束,结果:" + str_result + "\r\n");//加入,然后等待另一个线程去处理!!!!
				}
			} else {
				v_msg.add("开始安装 [" + _name + "] 初始数据,没有发现一个数据文件!!\r\n"); //	
			}
			v_msg.add("\r\n");
		}

		v_msg.add("准备安装 [" + _name + "] 扩展数据,安装包路径[" + _package + "xtdata/].....\r\n"); //外规数据不打在产品jar中,原因有二:一是它太大,一个人就有8.3M,比产品本身两倍还大,二是它以后需要单独升级!所以在lib\下专门弄一个weblight_lawdata.jar【xch/2012-03-12】
		String[][] str_ext3DataFiles = services.getExt3DataXmlFiles(_package); //取得所有文件清单,即要在RegisterMenu.xml中注册结点<ext3data>
		if (str_ext3DataFiles.length == 1 && str_ext3DataFiles[0][1] == null) { //如果是失败,因为返回的Sting[][],没办法,只能使用特殊方式表示是安装失败,其实也可以使用AltAppException,但这里还要try{}catch{},麻烦!!
			v_msg.add("没有安装 [" + _name + "] 扩展数据,原因[" + str_ext3DataFiles[0][0] + "]\r\n"); //
		} else {
			v_msg.add("开始安装 [" + _name + "] 扩展数据,一共有[" + str_ext3DataFiles.length + "]个文件.....\r\n"); //
			for (int i = 0; i < str_ext3DataFiles.length; i++) {
				String str_installResult = services.installExt3Data(str_ext3DataFiles[i][1]); //安装!!
				v_msg.add("<" + getNoStr(i, str_ext3DataFiles.length) + "/" + str_ext3DataFiles.length + ">安装 [" + _name + "] 扩展数据之 [" + str_ext3DataFiles[i][0] + "]-[" + str_ext3DataFiles[i][1] + "],结果[" + str_installResult + "]\r\n"); //
			}
		}
		v_msg.add("\r\n"); //
	}

	private String getNoStr(int _i, int _length) {
		int li_area = 10000; //
		if (_length < 10) {
			li_area = 10;
		} else if (_length < 100) {
			li_area = 100;
		} else if (_length < 1000) {
			li_area = 1000;
		} else {
			li_area = 10000;
		}
		String str_no = "" + (li_area + _i + 1);
		return str_no.substring(1, str_no.length()); //
	}

	private String[] split(String _par, String _separator) {
		if (_par == null) {
			return null;
		}
		if (_par.trim().equals("")) {
			return new String[0];
		}
		if (_par.indexOf(_separator) < 0) {
			return new String[] { _par };
		}
		ArrayList al_temp = new ArrayList(); //
		String str_remain = _par; //
		int li_pos = str_remain.indexOf(_separator); //
		while (li_pos >= 0) {
			String str_1 = str_remain.substring(0, li_pos); //
			if (str_1 != null && !str_1.trim().equals("")) {
				al_temp.add(str_1); // 加入!!!
			}
			str_remain = str_remain.substring(li_pos + _separator.length(), str_remain.length()); //
			li_pos = str_remain.indexOf(_separator); //
		}

		if (str_remain != null && !str_remain.trim().equals("")) {
			al_temp.add(str_remain); //
		}

		return (String[]) al_temp.toArray(new String[0]); // //
	}

	public HashMap convertStrToMapByExpress(String _str, String _split1, String _split2) {
		LinkedHashMap map = new LinkedHashMap(); //有顺序!
		if (_str == null || _str.trim().equals("")) {
			return map;
		}
		String[] str_items = split(_str, _split1); //
		for (int i = 0; i < str_items.length; i++) {
			String[] str_items2 = split(str_items[i], _split2); //
			if (str_items2.length >= 2) {
				map.put(str_items2[0], str_items2[1]); // //送入哈希表!!!!
			}
		}
		return map;
	}

	/**
	 * 处理消息!!
	 */
	protected void dealMsg() {
		if (v_msg.isEmpty()) { //如果为空,则不做!
			return;
		}
		StringBuilder sb_newText = new StringBuilder(); //
		while (!v_msg.isEmpty()) { //如果有数据!!!
			sb_newText.append((String) v_msg.elementAt(0)); //
			v_msg.removeElementAt(0); //
		}
		textArea.append(sb_newText.toString()); //
		int li_length = textArea.getText().length(); //
		textArea.setSelectionStart(li_length); //
		textArea.setSelectionEnd(li_length); //
	}

}
