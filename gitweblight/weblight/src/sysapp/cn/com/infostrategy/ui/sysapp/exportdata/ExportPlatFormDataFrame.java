/**************************************************************************
 * $RCSfile: ExportPlatFormDataFrame.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:19:33 $
 **************************************************************************/
package cn.com.infostrategy.ui.sysapp.exportdata;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * 为了快速部署,系统提供快速导出平台数据的功能!! 导出包括三种模式: 1.只导结构(pub_表结构),适合于第一次创建一个样台
 * 2.只导数据,适合于片本更新等 3.即导结构也导结构,适合于平台移植!!
 * 
 * @author user
 * 
 */
public class ExportPlatFormDataFrame extends JFrame {

	private static final long serialVersionUID = 3262282355206416248L;

	private static final Font textFont = new Font("宋体", Font.PLAIN, 12);

	private JCheckBox[] jcb_select = null;

	private JTextField jtf_path = null;

	private JButton jbt_path = null;

	private File f_all = null;

	private PrintWriter pw_all = null;

	private HashMap hm_values = null;

	/**
	 * 所有平台表名
	 */
	private final static String[] str_planttable = new String[] { //
	"pub_templet_1",//
			"pub_templet_1_item",// 四张表的顺序不要动
			"pub_querytemplet",//
			"pub_querytemplet_item",// 注意，如果需要添加新的平台表，请在下面添加

			"pub_menu", //菜单表
			"pub_user", //用户表
			"pub_comboboxdict",//
			"pub_fileupload", //
			"pub_formulafunctions", //
			"pub_news", //
			"pub_task_commit", //
			"pub_task_deal", //
			"pub_clusterhost", //集群信息表!!

			"pub_table_info", // 注意，如果需要添加新的平台表，请在这两个表前面添加
			"pub_table_indexes" //
	};

	/**
	 * 所有平台视图
	 */
	private final static String[] str_plantview = new String[] { "VI_MENU", "DB_TABLE", "DB_VIEW", "DB_TABLE_FIELD" };

	/**
	 * 所有平台存储过程
	 */
	private final static String[] str_plantpro = new String[] { "PR_INIT_PUB_TEMPLET" };

	/**
	 * 所有平台存储函数
	 */
	private final static String[] str_plantfn = new String[] {};

	private Vector vec_sqls = new Vector();

	private Vector vec_attrs = new Vector();

	private boolean bl_flag = false;

	public ExportPlatFormDataFrame() {
		this.setTitle("导出平台数据"); //
		this.setSize(500, 280);
		this.setLocation(200, 100);
		init();
	}

	private void init() {
		String[] str_jcb = new String[] { "导出表结构", "导出视图脚本", "导出存储函数", "导出存储过程", "导出表初始数据" };
		jcb_select = new JCheckBox[5];

		JLabel jlb_title = new JLabel("请选择要导出的内容：");

		Box box_jcb = Box.createVerticalBox();
		box_jcb.add(Box.createVerticalStrut(20));
		box_jcb.add(jlb_title);
		box_jcb.add(Box.createVerticalStrut(10));
		for (int i = 0; i < 5; i++) {
			jcb_select[i] = getCheckBox(str_jcb[i]);
			box_jcb.add(jcb_select[i]);
		}

		JLabel jlb_path = new JLabel("导出路径:");
		jlb_path.setPreferredSize(new Dimension(80, 20));
		jlb_path.setHorizontalAlignment(JLabel.RIGHT);
		jtf_path = new JTextField(System.getProperty("user.home") + "\\桌面");
		jtf_path.setPreferredSize(new Dimension(320, 20));
		jbt_path = new JButton("路径…");
		jbt_path.setPreferredSize(new Dimension(75, 20));
		jbt_path.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onPath();
			}
		});

		JPanel jpn_path = new JPanel();
		jpn_path.setLayout(new FlowLayout());
		jpn_path.add(jlb_path);
		jpn_path.add(jtf_path);
		jpn_path.add(jbt_path);

		JPanel jpn_con = new JPanel();
		jpn_con.setLayout(new BorderLayout());
		jpn_con.add(new JLabel("   "), BorderLayout.WEST);
		jpn_con.add(box_jcb, BorderLayout.CENTER);
		jpn_con.add(jpn_path, BorderLayout.SOUTH);

		this.getContentPane().add(jpn_con, BorderLayout.CENTER);
		this.getContentPane().add(getBtnPanel(), BorderLayout.SOUTH);
		this.setVisible(true); //
	}

	/**
	 * 根据_text获得JCheckBox
	 * 
	 * @param _text
	 * @return
	 */
	private JCheckBox getCheckBox(String _text) {
		JCheckBox jcb_temp = new JCheckBox(_text);
		jcb_temp.setFont(textFont);
		jcb_temp.setBackground(new Color(0xFFFFFF));
		return jcb_temp;
	}

	protected void onPath() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showSaveDialog(this);
		if (result != 0) {
			return;
		}
		jtf_path.setText(chooser.getSelectedFile().getPath());
	}

	private JPanel getBtnPanel() {
		JPanel jpn_btn = new JPanel();
		JButton jbt_out = new JButton("导出");
		JButton jbt_cancel = new JButton("关闭");
		jbt_out.setPreferredSize(new Dimension(75, 20));
		jbt_cancel.setPreferredSize(new Dimension(75, 20));
		jbt_out.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onOut();
			}
		});
		jbt_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		jpn_btn.setLayout(new FlowLayout());
		jpn_btn.add(jbt_out);
		jpn_btn.add(jbt_cancel);

		return jpn_btn;
	}

	protected void onOut() {
		String str_path = makeDirs(jtf_path.getText(), "NOVA2");
		initAllFile(str_path);
		if (str_path == null) {
			return;
		}
		getPlantData();
		if (jcb_select[0].isSelected()) {
			pw_all.println("--table");
			exportStruct(str_path);
			pw_all.println("");
		}
		if (jcb_select[1].isSelected()) {
			pw_all.println("--view");
			exportView(str_path);
			pw_all.println("");
		}
		if (jcb_select[2].isSelected()) {
			pw_all.println("--function");
			exportFn(str_path);
			pw_all.println("");
		}
		if (jcb_select[3].isSelected()) {
			pw_all.println("--procedure");
			exportProcedure(str_path);
			pw_all.println("");
		}
		if (jcb_select[4].isSelected()) {
			pw_all.println("--initdata");
			exportData(str_path);
			pw_all.println("");
		}
		pw_all.close();

		if (bl_flag) {
			JOptionPane.showMessageDialog(this, "导出平台数据过程中出现问题！");
		} else {
			JOptionPane.showMessageDialog(this, "导出平台数据成功！");
		}
	}

	protected void onCancel() {
		this.dispose();
	}

	private HashMap getPlantData() {
		if (jcb_select[2].isSelected()) {
			for (int i = 0; i < str_plantfn.length; i++) {
				vec_attrs.add("FN_" + str_plantfn[i]);
				vec_sqls.add("Select TEXT From user_source Where TYPE = 'FUNCTION'" + " AND NAME = '" + str_plantfn[i].toUpperCase() + "' order by LINE");
			}
		}
		if (jcb_select[3].isSelected()) {
			for (int i = 0; i < str_plantpro.length; i++) {
				vec_attrs.add("PRO_" + str_plantpro[i]);
				vec_sqls.add("SELECT TEXT FROM user_source WHERE TYPE = 'PROCEDURE'" + " And NAME='" + str_plantpro[i].toUpperCase() + "' ORDER BY LINE");
			}
		}
		if (jcb_select[4].isSelected()) {
			for (int i = 0; i < str_planttable.length - 2; i++) {
				vec_attrs.add("COL_" + str_planttable[i]);// 用来查找每个表的列以及列的数据类型，如果是日期则需特殊处理
				vec_attrs.add("TAB_" + str_planttable[i]);
				vec_sqls.add("Select COLUMN_NAME, DATA_TYPE From cols Where TABLE_NAME = '" + str_planttable[i].toUpperCase() + "'");
				if (str_planttable[i].equalsIgnoreCase("pub_templet_1") || str_planttable[i].equalsIgnoreCase("pub_querytemplet")) {
					vec_sqls.add("Select * From " + str_planttable[i] + " Order By TEMPLETCODE");
				} else {
					vec_sqls.add("Select * From " + str_planttable[i]);
				}
			}
			vec_attrs.add("UPDATE_PUB_MENU");
			vec_sqls.add("select AA.name, BB.name  parentname from pub_menu AA left join pub_menu BB on AA.parentmenuid = BB.id");
		}
		if (vec_attrs.size() == 0) {
			return null;
		}

		String[] str_attrs = new String[vec_attrs.size()];
		String[] str_sqls = new String[vec_sqls.size()];
		for (int i = 0; i < str_attrs.length; i++) {
			str_attrs[i] = (String) vec_attrs.get(i);
			str_sqls[i] = (String) vec_sqls.get(i);
		}
		try {
			hm_values = UIUtil.getHashVoArrayReturnMapByMark(null, str_sqls, str_attrs);
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return hm_values;
	}

	/**
	 * 根据指定的路径来创建目录
	 * 
	 * @param _path
	 * @param _dir
	 * @return
	 */
	private String makeDirs(String _path, String _dir) {
		File f_dir = new File(_path + "\\" + _dir);
		if (f_dir.exists() && f_dir.isDirectory()) {
		} else if (!f_dir.mkdir()) {
			JOptionPane.showMessageDialog(this, "创建平台导出目录" + _dir + "出错！", "错误提示", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return f_dir.getPath();
	}

	/**
	 * 获得文件名001_XX.sql的格式
	 * 
	 * @param _id
	 * @param _tablename
	 * @return
	 */
	private String getFileName(int _id, String _tablename) {
		return ("" + (1001 + _id)).substring(1) + "_" + _tablename + ".sql";
	}

	/**
	 * 导出表结构
	 */
	private void exportStruct(String _path) {
		String str_path = makeDirs(_path, "01_table");

		GetTableStruct gts_plant = new GetTableStruct();
		for (int i = 0; i < str_planttable.length; i++) {
			File f_export = new File(str_path + "\\" + getFileName(i, str_planttable[i]));
			if (!f_export.exists()) {
				createAllFile(f_export.getPath(), _path);
				try {
					f_export.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				FileWriter fw_file = new FileWriter(f_export);
				PrintWriter pw_export = new PrintWriter(fw_file);

				pw_export.println("--表" + str_planttable[i] + "的结构");
				String str_value = gts_plant.getPub_ByTableName(str_planttable[i]);
				if (str_value == null || str_value.equals("")) {
					fw_file.close();
					continue;
				}
				pw_export.println(str_value);
				pw_export.println();
				fw_file.close();
			} catch (IOException e) {
				bl_flag = true;
				JOptionPane.showMessageDialog(this, f_export.getName() + "出错！");
			}
		}
	}

	/**
	 * 导出视图构建脚本
	 * 
	 * @param _path
	 */
	private void exportView(String _path) {
		String str_path = makeDirs(_path, "02_view");
		GetTableStruct gts_plant = new GetTableStruct();
		for (int i = 0; i < str_plantview.length; i++) {
			File f_export = new File(str_path + "\\" + getFileName(i, str_plantview[i]));
			if (!f_export.exists()) {
				createAllFile(f_export.getPath(), _path);
				try {
					f_export.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				FileWriter fw_file = new FileWriter(f_export);
				PrintWriter pw_export = new PrintWriter(fw_file);

				pw_export.println("--视图" + str_plantview[i] + "的构建脚本");

				String str_value = gts_plant.getPub_ByTableName(str_plantview[i]);
				if (str_value == null || str_value.equals("")) {
					fw_file.close();
					continue;
				}
				pw_export.println(str_value);

				fw_file.close();
			} catch (IOException e) {
				bl_flag = true;
				JOptionPane.showMessageDialog(this, f_export.getName() + "出错！");
			}
		}
	}

	/**
	 * 导出存储函数
	 * 
	 * @param _path
	 */
	private void exportFn(String _path) {
		String str_path = makeDirs(_path, "03_function");

		for (int i = 0; i < str_plantfn.length; i++) {
			File f_export = new File(str_path + "\\" + getFileName(i, str_plantfn[i]));
			if (!f_export.exists()) {
				createAllFile(f_export.getPath(), _path);
				try {
					f_export.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				FileWriter fw_file = new FileWriter(f_export);
				PrintWriter pw_export = new PrintWriter(fw_file);

				pw_export.println("--存储函数" + str_plantfn[i] + "的构建脚本");
				HashVO[] hv_value = (HashVO[]) hm_values.get("FN_" + str_plantfn[i]);
				if (hv_value.length == 0) {
					fw_file.close();
					continue;
				}
				pw_export.print("create or replace ");
				for (int j = 0; j < hv_value.length; j++) {
					pw_export.print(hv_value[j].getAttributeValue("TEXT"));
				}
				pw_export.println("");
				pw_export.println("/");
				pw_export.println("\n");
				fw_file.close();
			} catch (IOException e) {
				bl_flag = true;
				JOptionPane.showMessageDialog(this, f_export.getName() + "出错！");
			}
		}
	}

	/**
	 * 导出平台存储过程
	 * 
	 * @param _path
	 */
	private void exportProcedure(String _path) {
		String str_path = makeDirs(_path, "04_procedure");

		for (int i = 0; i < str_plantpro.length; i++) {
			File f_export = new File(str_path + "\\" + getFileName(i, str_plantpro[i]));
			if (!f_export.exists()) {
				createAllFile(f_export.getPath(), _path);
				try {
					f_export.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				FileWriter fw_file = new FileWriter(f_export);
				PrintWriter pw_export = new PrintWriter(fw_file);

				pw_export.println("--存储过程" + str_plantpro[i] + "的构建脚本");
				HashVO[] hv_value = (HashVO[]) hm_values.get("PRO_" + str_plantpro[i]);
				if (hv_value.length == 0) {
					fw_file.close();
					continue;
				}
				pw_export.print("create or replace ");
				for (int j = 0; j < hv_value.length; j++) {
					pw_export.print(hv_value[j].getAttributeValue("TEXT"));
				}
				pw_export.println("\n/");
				pw_export.println("\r\n");
				pw_export.println("\r\n");

				fw_file.close();
			} catch (IOException e) {
				bl_flag = true;
				JOptionPane.showMessageDialog(this, f_export.getName() + "出错！");
			}
		}
	}

	/**
	 * 导出表数据
	 */
	private void exportData(String _path) {
		String str_path = makeDirs(_path, "05_initdata");

		for (int i = 4; i < str_planttable.length - 2; i++) {
			File f_export = new File(str_path + "\\" + getFileName(i - 2, str_planttable[i]));
			if (!f_export.exists()) {
				createAllFile(f_export.getPath(), _path);
				try {
					f_export.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				FileWriter fw_file = new FileWriter(f_export);
				PrintWriter pw_export = new PrintWriter(fw_file);

				pw_export.println("--表" + str_planttable[i] + "的初始数据");
				HashVO[] hv_cols = (HashVO[]) hm_values.get("COL_" + str_planttable[i]);
				HashVO[] hv_data = (HashVO[]) hm_values.get("TAB_" + str_planttable[i]);

				if (hv_data.length <= 0) {
					continue;
				}
				pw_export.println("Delete From " + str_planttable[i] + ";");
				for (int k = 0; k < hv_data.length; k++) {
					pw_export.print("Insert Into " + str_planttable[i] + "(");

					String[] str_keys = hv_data[k].getKeys();
					String str_temp = "";
					String str_value = "s_" + str_planttable[i].trim() + ".nextval";
					for (int j = 0; j < str_keys.length; j++) {
						str_temp = str_temp + str_keys[j] + ", ";
						if (str_planttable[i].equalsIgnoreCase("pub_menu") && str_keys[j].equalsIgnoreCase("PARENTMENUID")) {
							str_value = str_value + ", null";// 插入菜单表，元素的父菜单全部置为null
						} else if (j > 0) {
							str_value = str_value + getInsertValue(hv_data[k].getStringValue(str_keys[j]), str_keys[j], hv_cols);
						}
					}
					pw_export.print(str_temp.substring(0, str_temp.length() - 2) + ")");
					pw_export.print(" values (");
					pw_export.println(str_value + ");");
				}
				pw_export.println();
				pw_export.println("commit;");
				pw_export.println();
				pw_export.println();

				if (str_planttable[i].equalsIgnoreCase("PUB_MENU")) {// 以下是处理Menu的父菜单的SQL
					HashVO[] hv_menu = (HashVO[]) hm_values.get("UPDATE_PUB_MENU");
					if (hv_menu.length <= 0) {
						fw_file.close();
						continue;
					}
					for (int k = 0; k < hv_menu.length; k++) {
						String str_name = hv_menu[k].getStringValue("NAME");
						String str_parent = hv_menu[k].getStringValue("PARENTNAME");
						String _sql = "Update Pub_menu AA Set AA.PARENTMENUID = (Select BB.ID From Pub_Menu BB Where BB.NAME";
						String _end_sql = " = '" + str_parent + "') Where AA.NAME = '" + str_name + "';";
						if (str_parent == null || str_parent.equalsIgnoreCase("null") || str_parent.equals("")) {
							continue;
						}
						pw_export.println(_sql + _end_sql);
					}
					pw_export.println();
					pw_export.println("commit;");
				}
				fw_file.close();
			} catch (IOException e) {
				bl_flag = true;
				JOptionPane.showMessageDialog(this, f_export.getName() + "出错！");
			}
		}
		outTemplet(str_path, _path);
	}

	/**
	 * 根据_value来确定要插入的值,如果该列的数据类型为DATE，则插入"SYSDATE"
	 * 
	 * @param _value:列的值
	 * @param _column:列名
	 * @param _hv
	 * @return
	 */
	private String getInsertValue(String _value, String _column, HashVO[] _hv) {
		String str_value = null;

		for (int i = 0; i < _hv.length; i++) {
			String str_col = _hv[i].getStringValue("COLUMN_NAME");
			if (str_col.equalsIgnoreCase(_column)) {
				String str_type = _hv[i].getStringValue("DATA_TYPE");
				if (str_type.equalsIgnoreCase("DATE")) {
					return ", SYSDATE";
				}
			}
		}
		if (_value == null || _value.equals("")) {
			str_value = ", null";
		} else {
			str_value = ", '" + convert(_value) + "'";
		}
		return str_value;
	}

	private boolean initAllFile(String _root) {
		f_all = new File(_root + "\\_all.sql");

		if (!f_all.exists()) {
			try {
				f_all.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "创建all.sql出错！");
				return false;
			}
		}
		try {
			FileWriter resultFile = new FileWriter(f_all, true);
			pw_all = new PrintWriter(resultFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 创建all.sql文件
	 * 
	 * @param _filepath
	 * @return
	 */
	private boolean createAllFile(String _filepath, String _root) {
		String all_name = "";
		StringTokenizer token_file = new StringTokenizer(_filepath, "\\");

		int index = 0;
		int count = token_file.countTokens();
		while (token_file.hasMoreTokens()) {
			all_name = all_name + token_file.nextToken() + "\\";
			index++;
			if (index == count - 1) {
				break;
			}
		}
		pw_all.println("@." + _filepath.substring(_root.length()));

		String file_name = token_file.nextToken();
		all_name = all_name + "_all.sql";
		File out_file = new File(all_name);
		try {
			FileWriter resultFile = new FileWriter(out_file, true);
			PrintWriter myFile = new PrintWriter(resultFile);
			myFile.println("@.\\" + file_name);
			myFile.close();
		} catch (IOException e) {
			bl_flag = true;
			JOptionPane.showMessageDialog(this, file_name + "写入_all.sql出错！");
			e.printStackTrace();
		}
		return false;
	}

	private void outTemplet(String _path, String _root) {
		File f_dir = new File(_path + "\\001_pub_templet");// 导出Pub_templet_1和Pub_templet_1_item
		if (!f_dir.exists() || !f_dir.isDirectory()) {
			if (!f_dir.mkdir()) {
				JOptionPane.showMessageDialog(this, "创建Templet平台导出目录出错！", "错误提示", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		HashVO[] hv_templet = (HashVO[]) hm_values.get("TAB_" + "pub_templet_1");
		HashVO[] hv_cols = (HashVO[]) hm_values.get("COL_" + "pub_templet_1");
		for (int i = 0; i < hv_templet.length; i++) {
			String str_path = f_dir.getPath() + "\\" + hv_templet[i].getStringValue("TEMPLETCODE") + ".sql";
			writeIntoFile(str_path, _root, hv_templet[i], hv_cols, "PUB_TEMPLET_1");
		}

		File f_dir2 = new File(_path + "\\002_pub_querytemplet");// 导出Pub_Querytemplet和Pub_QuerytempletＦ_item
		if (!f_dir2.exists() || !f_dir2.isDirectory()) {
			if (!f_dir2.mkdir()) {
				JOptionPane.showMessageDialog(this, "创建QueryTemplet平台导出目录出错！", "错误提示", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		HashVO[] hv_querytemplet = (HashVO[]) hm_values.get("TAB_" + "pub_querytemplet");
		HashVO[] hv_cols_item = (HashVO[]) hm_values.get("COL_" + "pub_querytemplet");
		for (int i = 0; i < hv_querytemplet.length; i++) {
			String str_path = f_dir2.getPath() + "\\" + hv_querytemplet[i].getStringValue("TEMPLETCODE") + ".sql";
			writeIntoFile(str_path, _root, hv_querytemplet[i], hv_cols_item, "PUB_QUERYTEMPLET");
		}
	}

	/**
	 * 把选择的模板的构造SQL语句写入文件
	 * 
	 * @param _path:子项文件的全路径
	 * @param _root:保存路径的根目录
	 * @param _hv:pub_templet_1或pub_querytemplet中的每个子项
	 * @param _hv_cols:pub_templet_1或pub_querytemplet中的每列以及每列的数据类型
	 * @param _tab:pub_templet_1或pub_querytemplet
	 */
	private void writeIntoFile(String _path, String _root, HashVO _hv, HashVO[] _hv_cols, String _tab) {
		if (_path == null || _path.equals("")) {
			JOptionPane.showMessageDialog(this, "请输入正确的文件名！");
		}
		File out_file = new File(_path);
		if (!out_file.exists()) {
			createAllFile(_path, _root);
			try {
				out_file.createNewFile();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "创建" + _path + "出错！");
				e.printStackTrace();
				return;
			}
		}
		try {
			FileWriter resultFile = new FileWriter(out_file);
			PrintWriter myFile = new PrintWriter(resultFile);
			String _code = _hv.getStringValue("templetcode");
			String _id = _hv.getStringValue("PK_" + _tab);

			myFile.println("Delete " + _tab + "_item Where pk_" + _tab + " In " + "(Select pk_" + _tab + " From " + _tab + " Where templetcode = '" + _code + "');");
			myFile.println("Delete " + _tab + " where templetcode ='" + _code + "';");
			myFile.println("");
			myFile.print("Insert Into " + _tab + " (");

			String[] str_keys = _hv.getKeys();
			String str_temp = "";
			String str_value = "s_" + _tab + ".nextval";
			for (int j = 0; j < str_keys.length; j++) {
				str_temp = str_temp + str_keys[j] + ", ";
				if (j > 0) {
					str_value = str_value + getInsertValue(_hv.getStringValue(str_keys[j]), str_keys[j], _hv_cols);
				}
			}
			myFile.print(str_temp.substring(0, str_temp.length() - 2) + ")");
			myFile.print(" values (");
			myFile.println(str_value + ");");
			myFile.println("");

			// 把每个Item的列和数据写入文件
			HashVO[] hv_cols = (HashVO[]) hm_values.get("COL_" + _tab.toLowerCase() + "_item");
			HashVO[] hv_item = (HashVO[]) hm_values.get("TAB_" + _tab.toLowerCase() + "_item");

			for (int k = 0; k < hv_item.length; k++) {
				String str_tempid = hv_item[k].getStringValue("PK_" + _tab);
				if (!str_tempid.equals(_id)) {
					continue;
				}
				myFile.print("Insert Into " + _tab + "_item (");
				str_keys = hv_item[k].getKeys();
				str_temp = "";
				str_value = "s_" + _tab + "_item.nextval";
				for (int j = 0; j < str_keys.length; j++) {
					str_temp = str_temp + str_keys[j] + ", ";
					if (j == 0) {
						continue;
					}
					if (str_keys[j].equalsIgnoreCase("PK_" + _tab)) {
						str_value = str_value + ", (select PK_" + _tab + "  From " + _tab + " Where TEMPLETCODE = '" + _code + "')";
					} else {
						str_value = str_value + getInsertValue(hv_item[k].getStringValue(str_keys[j]), str_keys[j], hv_cols);
					}
				}
				myFile.print(str_temp.substring(0, str_temp.length() - 2) + ")");
				myFile.print(" values (");
				myFile.println(str_value + ");");
			}
			myFile.println("commit;");
			resultFile.close();
		} catch (IOException e) {
			bl_flag = true;
			e.printStackTrace();
		}
	}

	private String convert(String _str) {
		if (_str == null) {
			return "";
		}
		return _str.replaceAll("'", "''");
	}
}
/*******************************************************************************
 * $RCSfile: ExportPlatFormDataFrame.java,v $ $Revision: 1.4 $ $Date: 2007/01/30
 * 07:41:11 $
 * 
 * $Log: ExportPlatFormDataFrame.java,v $
 * Revision 1.4  2012/09/14 09:19:33  xch123
 * 邮储现场回来统一更新
 *
 * Revision 1.1  2012/08/28 09:41:13  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:32:11  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:41  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:50  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:02:05  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:13:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:11:29  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:48  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:51  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:36  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:41  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:29:09  xch
 * *** empty log message ***
 *
 * Revision 1.4  2007/09/20 05:08:23  xch
 * *** empty log message ***
 *
 * Revision 1.13  2007/03/09 09:33:02  shxch
 * *** empty log message ***
 *
 * Revision 1.12  2007/03/07 02:01:55  shxch
 * *** empty log message ***
 *
 * Revision 1.11  2007/03/02 07:43:58  shxch
 * *** empty log message ***
 *
 * Revision 1.10  2007/02/27 09:38:47  shxch
 * *** empty log message ***
 *
 * Revision 1.9  2007/02/27 06:03:03  shxch
 * *** empty log message ***
 *
 * Revision 1.8  2007/02/01 08:15:41  shxch
 * *** empty log message ***
 *
 * Revision 1.7  2007/01/31 09:16:19  shxch
 * *** empty log message ***
 *
 * Revision 1.6  2007/01/31 09:15:11  shxch
 * 完善数据导出时等待框的显示
 * 
 * Revision 1.2 2007/01/30 05:00:02 lujian *** empty log message ***
 * 
 * 
 ******************************************************************************/
