package com.pushworld.ipushgrc.ui.law;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.law.p010.ContentTree;
import com.pushworld.ipushgrc.ui.law.p010.LawContentVO;
import com.pushworld.ipushgrc.ui.law.p010.LawItemEditDialog;
import com.pushworld.ipushgrc.ui.rule.p010.RuleContentVO;
import com.pushworld.ipushgrc.ui.rule.p010.RuleItemEditDialog;

/**
 * 法规、制度导入界面。特殊标识符 $$$A $$$B $$$C 现支持自动格式化。 【第一章、第一节、第一条】； 【一、二、三、】；
 * 应该把对应模板中的必填项带进来。
 * 
 * @author hm
 * 
 */
public class LawOrRuleImportDialog extends BillDialog implements ActionListener {
	private JTextArea textArea = new JTextArea();

	private WLTButton confirm, btn_format, btn_showSamle, btn_help;

	private JCheckBox checkbox;
	private String type; // law,rule

	private Pub_Templet_1VO templetVO;

	private String selectID;

	private boolean imported = false; //是否已经导入过

	public LawOrRuleImportDialog(Container _container, String _title, int _width, int _height, String _type) {
		super(_container, _title, _width, _height);
		this.type = _type;
		this.getContentPane().add(getPanel());
	}

	public LawOrRuleImportDialog(BillListPanel _lawOrRuleListPanel, String _title, int _width, int _height, String _type) {
		super(_lawOrRuleListPanel, _title, _width, _height);
		if (_lawOrRuleListPanel != null) {
			BillVO selectVO = _lawOrRuleListPanel.getSelectedBillVO();
			if (selectVO != null) {
				selectID = selectVO.getStringValue("id");
			}
		}
		this.type = _type;
		this.getContentPane().add(getPanel());
	}

	// 创建导入面板
	public JPanel getPanel() {
		WLTPanel panel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new BorderLayout(), LookAndFeel.defaultShadeColor1, false);
		// top
		JPanel jpTop = new JPanel();
		jpTop.setLayout(new FlowLayout(FlowLayout.LEFT));
		String desc = "将格式化好的文本导入系统, [自动格式化]可\"基本\"完成格式化操作!";
		jpTop.add(new JLabel(desc));
		btn_showSamle = new WLTButton("查看范例");
		btn_format = new WLTButton("自动格式化");
		btn_format.addActionListener(this);
		btn_format.setToolTipText("自动格式化成带有特殊标记的文本");
		checkbox = new JCheckBox("生成长标题");
		btn_showSamle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					UIUtil.openRemoteServerFile(null, "/sample/外规导入测试文件.doc");
				} catch (Exception ex) {
					MessageBox.showException(null, ex);
					ex.printStackTrace(); //
				}
			}
		});
		confirm = new WLTButton("导入");
		btn_help = new WLTButton("帮助");
		btn_help.addActionListener(this);
		confirm.addActionListener(this);
		// jpTop.add(jbSamle);
		jpTop.add(btn_format);
		jpTop.add(confirm);
		jpTop.add(btn_help);
		jpTop.add(checkbox);
		panel.add(jpTop, BorderLayout.NORTH);
		textArea.setBackground(Color.WHITE);
		textArea.setForeground(Color.BLUE);
		textArea.setFont(new Font("宋体", Font.PLAIN, 12));
		textArea.select(0, 0);
		JScrollPane scrollPanel = new JScrollPane(textArea);
		panel.add(scrollPanel, BorderLayout.CENTER);
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		String text = textArea.getText(); // 要导入的内容
		if (e.getSource() == btn_format) {
			if ("".equals(text)) {
				MessageBox.show(this, "请输入要导入的文件内容!");
				return;
			}
			ArrayList list = getTextList(text);
			textArea.setText(addSpecChar(list));
		} else if (e.getSource() == btn_help) {
			MessageBox.showInfo(this, "格式化原则: 特殊标记+内容\n特殊标记包括($$$A, $$$B, $$$C...), 例如:\n\n文件标题\n$$$A第一章 总 则\n$$$B第一节 思路\n" + "$$$C第一条\n  第一条的内容...\n$$$C第二条\n  第二条的内容...\n$$$A第二章 约 定\n$$$B第二节 目标\n$$$C第三条\n  第三条的内容...");
		} else if (e.getSource() == confirm) {
			if ("".equals(text)) {
				MessageBox.show(this, "请输入要导入的文件内容!");
				return;
			}
			String dataSourceType = new TBUtil().getDefaultDataSourceType();
			String newdataid = null; // 插入的新数据id
			String templetCode = null;
			boolean issuccess = true;
			if (type.equals("law")) {
				try {
					int[] headerlengths = UIUtil.getTableDataStructByDS(null, "select a.lawname,b.itemtitle,b.itemcontent from law_law a,law_law_item b where 2=1").getHeaderLength();
					LawContentVO tree = new ContentTree(new String[] { "$$$A", "$$$B", "$$$C", "$$$D", "$$$E", "$$$F" }).parseLaw(text);
					tree.setParentContainer(this);
					tree.setPub_Templet_1VO(getPub_Templet_1VO());
					tree.setHeaderlengths(headerlengths);
					tree.setID(selectID);
					LinkedList sqlList = tree.saveToDataBase();
					if (sqlList == null) {
						return;
					}
					UIUtil.executeBatchByDS(null, sqlList);
					textArea.setText("");
					selectID = null; //导入一次以后就制空
					this.setCloseType(1);
					newdataid = tree.getID(); // 赋值
					templetCode = "LAW_LAW_CODE1";
				} catch (Exception e1) {
					MessageBox.show(this, e1.getMessage());
					e1.printStackTrace();
					return;
				}
			} else {
				try {
					int[] headerlengths = UIUtil.getTableDataStructByDS(null, "select a.rulename,b.itemtitle,b.itemcontent from rule_rule a,rule_rule_item b where 2=1").getHeaderLength();
					RuleContentVO tree = new ContentTree(new String[] { "$$$A", "$$$B", "$$$C", "$$$D", "$$$E", "$$$F" }).parseRule(text);
					tree.setParentContainer(this);
					tree.setPub_Templet_1VO(getPub_Templet_1VO());
					tree.setItemlength(headerlengths);
					tree.setID(selectID);
					LinkedList sqlList = tree.saveToDataBase();
					if (sqlList == null) {
						return;
					}
					UIUtil.executeBatchByDS(null, sqlList);
					textArea.setText("");
					selectID = null;
					this.setCloseType(1);
					newdataid = tree.getID();// 赋值
					templetCode = "RULE_RULE_CODE1";
				} catch (Exception e1) {
					MessageBox.show(this, e1.getMessage());
					e1.printStackTrace();
					return;
				}
			}
			if (newdataid != null && !newdataid.equals("")) {

			}
			int index = MessageBox.showOptionDialog(LawOrRuleImportDialog.this, "数据导入成功!要现在编辑属性吗？", "提示", new String[] { "编辑属性", "继续导入", "退出" });
			if (index == 0) {// 编辑

				BillCardDialog cardDialog = new BillCardDialog(LawOrRuleImportDialog.this, templetCode, WLTConstants.BILLDATAEDITSTATE_UPDATE);
				final BillCardPanel cardPanel = cardDialog.getBillcardPanel();
				cardPanel.queryDataByCondition(" id = " + newdataid);
				cardPanel.getRowNumberItemVO().setState(WLTConstants.BILLDATAEDITSTATE_UPDATE);// 设置此行的状态。
				WLTButton btn = new WLTButton("编辑条目");
				btn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (type.equalsIgnoreCase("law")) { // 法规
							LawItemEditDialog dialog = new LawItemEditDialog(cardPanel, "编辑条目", 900, 600, cardPanel.getBillVO());
							dialog.setVisible(true);
						} else {// 制度
							RuleItemEditDialog dialog = new RuleItemEditDialog(cardPanel, "编辑条目", 900, 600, cardPanel.getBillVO());
							dialog.setVisible(true);
						}
					}
				});
				cardPanel.addBatchBillCardButton(new WLTButton[] { btn });
				cardPanel.repaintBillCardButton();
				cardDialog.setVisible(true);
				if (cardDialog.getCloseType() == 1) {//判断是否有要废止的制度，如果有，则制度预警【李春娟/2015-12-29】
					insertAlarm(cardDialog.getBillVO());

				}
			} else if (index == 1) {// 继续导入

			} else { // 退出
				this.dispose();
			}
		}

	}

	/**
	 * 判断并插入内规预警!
	 * 目前内规预警做的比较简陋，后期需要补充 对某条外规做了哪些修改，比如：修改了2条条目内容，删除了1条条目，新增了3条。
	 * 最好能做出cvs对比文字效果。
	 */
	public void insertAlarm(BillVO _selectRule) {
		try {

			TBUtil tbutil = TBUtil.getTBUtil();
			ArrayList insertSQLlist = new ArrayList();
			String currdate = UIUtil.getServerCurrDate();
			String userid = ClientEnvironment.getInstance().getLoginUserID();
			String userdept = ClientEnvironment.getInstance().getLoginUserDeptId();
			String refrule2 = _selectRule.getStringValue("refrule2", "");
			InsertSQLBuilder sql = new InsertSQLBuilder("rule_alarm");
			sql.putFieldValue("alarmsource", "内部制度--制度维护");
			sql.putFieldValue("alarmsourcetab", "rule_rule");
			sql.putFieldValue("state", "未处理");
			sql.putFieldValue("alarmdate", currdate);
			sql.putFieldValue("creator", userid);
			sql.putFieldValue("createdept", userdept);
			sql.putFieldValue("alarmsourcepk", _selectRule.getStringValue("id"));
			if (refrule2 != null && !"".equals(refrule2)) {
				String[] refruleids = TBUtil.getTBUtil().split(refrule2, ";");
				HashMap ruleMap = UIUtil.getHashMapBySQLByDS(null, "select id,rulename from rule_rule where id in(" + TBUtil.getTBUtil().getInCondition(refrule2) + ")");

				for (int m = 0; m < refruleids.length; m++) {
					//该制度被哪些制度关联了，进行提醒【李春娟/2015-12-29】
					HashVO[] vos = UIUtil.getHashVoArrayByDS(null, "select * from rule_rule where refrule like '%;" + refruleids[m] + ";%'");
					if (vos != null && vos.length > 0) {
						for (int i = 0; i < vos.length; i++) {
							sql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_RULE_ALARM"));
							sql.putFieldValue("ruleid", vos[i].getStringValue("id"));
							sql.putFieldValue("rulecode", vos[i].getStringValue("rulecode"));
							sql.putFieldValue("rulename", vos[i].getStringValue("rulename"));
							sql.putFieldValue("alarmtargettab", "rule_rule");
							String rulename = refruleids[m];
							if (ruleMap.get(refruleids[m]) != null) {
								rulename = (String) ruleMap.get(refruleids[m]);
							}
							sql.putFieldValue("alarmreason", "《" + rulename + "》制度已废止，请更新相关制度!");
							insertSQLlist.add(sql.getSQL());
						}
					}
				}
				insertSQLlist.add("update rule_rule set state='失效' where id in(" + tbutil.getInCondition(refrule2) + ")");

			}
			//关联的表单【李春娟/2015-12-26】
			String formids = _selectRule.getStringValue("formids");
			if (formids != null && !"".equals(formids.trim())) {
				String tabname = tbutil.getSysOptionStringValue("制度关联的表单表名", "BSD_FORM");//表单里必须有code和name字段
				tabname = tabname.toLowerCase();
				HashVO[] vos = UIUtil.getHashVoArrayByDS(null, "select id,code,name from " + tabname + " where id in(" + tbutil.getInCondition(formids) + ")");
				if (vos != null && vos.length > 0) {
					sql.putFieldValue("alarmreason", "《" + _selectRule.getStringValue("rulename") + "》制度内容已修改，请更新相关表单!");
					for (int i = 0; i < vos.length; i++) {
						sql.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_RULE_ALARM"));
						sql.putFieldValue("ruleid", vos[i].getStringValue("id"));
						sql.putFieldValue("rulecode", vos[i].getStringValue("code"));
						sql.putFieldValue("rulename", vos[i].getStringValue("name"));
						sql.putFieldValue("alarmtargettab", tabname);
						insertSQLlist.add(sql.getSQL());
					}
				}
			}
			if (insertSQLlist.size() > 0) {
				UIUtil.executeBatchByDS(null, insertSQLlist);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 把内容一行行放入List中
	private ArrayList getTextList(String text) {
		// 替换全角空格
		text = text.replaceAll((char) 8195 + "", " "); // 在word中的全角空格。
		text = text.replaceAll("　", " ");
		text = text.replaceAll("\t", " ");
		ArrayList list = new ArrayList();
		if (!text.contains("\n")) {
			list.add(text);
			return list;
		}
		int index = 0;
		String s = "";
		while (true) {
			if (!text.equals("\n")) {
				index = text.indexOf('\n');
				if (index >= 0) {
					s = text.substring(0, index + 1);
					list.add(s);
				} else {
					list.add(text); // 最后一行且没有回车
					break;
				}
			} else if (text.length() != 1) {
				// 不是最后一个空回车
				list.add("\n");
			} else { // 最后一个空回车
				list.add("\n");
				break;
			}
			text = text.substring(index + 1, text.length());
		}
		return list;
	}

	// 自动加标记。
	private String addSpecChar(ArrayList textList) {
		StringBuffer sb = new StringBuffer();
		String s = new String();
		int num = 0;
		boolean falg = false; // true是包含章节条的样子，false是不包含。
		for (int i = 0; i < textList.size(); i++) {
			String str = (String) textList.get(i);
			if (str != null && !str.equals("")) {
				if (str.trim().indexOf("第一章") == 0 || str.trim().indexOf("第一节") == 0 || str.trim().indexOf("第一条") == 0) {
					falg = true;
					break;
				}
			}

		}
		if (falg) { // 类似于:第一章
			// 第一节
			for (Object object : textList) {
				s = object.toString();
				s = formatStringBlank(s);
				if (num == 0 && (s != null && s.length() > 0 && !s.equals("\n"))) { // 第一行数据肯定为标题
					sb.append(s.toString());
					num++;
					continue;
				}
				String[] ss = new String[2];
				if (s.contains(" ")) {
					ss[0] = s.substring(0, s.indexOf(" ")).trim();
					ss[1] = s.substring(s.indexOf(" "));
				} else {
					ss[0] = s;
					ss[1] = "";
				}
				if (s.startsWith("第")) {
					if (ss[0].endsWith("章") || ss[0].endsWith("章\n")) {
						sb.append("$$$A" + s);
						num++;
					} else if (ss[0].endsWith("节") || ss[0].endsWith("节\n")) {
						sb.append("$$$B" + s);
						num++;
					} else if (ss[0].endsWith("条") || ss[0].endsWith("条\n")) {
						if (!checkbox.isSelected()) {
							s = ss[0] + "\n" + ss[1];
						}
						sb.append("$$$C" + s);
						num++;
					}
				} else if (num == 1 && ss[0].length() > 0 && !ss[0].equals("\n")) {
					if (!ss[0].contains("$$$")) {
						sb.append("$$$A\n" + s);
					} else {
						sb.append(s.toString());
					}
					num++;
				} else {
					sb.append(s.toString());
				}
			}
		} else { // 类似于： 一、 二、
			String str = "一二三四五六七八九十、.";
			String numStr = "0123456789.";
			String flag = null; // 标志是大写的还是阿拉伯数字。为null的时候是初始化。它的值有 str,num.
			for (Object object : textList) {
				s = object.toString();
				s = formatStringBlank(s);
				if (num == 0 && (s != null && s.length() > 0 && !s.equals("\n"))) { // 第一行数据肯定为标题
					sb.append(s.toString());
					num++;
					continue;
				}
				String[] ss = new String[2];
				if (s.contains(" ")) {
					ss[0] = s.substring(0, s.indexOf(" ")).trim();
					ss[1] = s.substring(s.indexOf(" "));
				} else {
					ss[0] = s;
					ss[1] = "";
				}
				if (ss[0].contains("、") && ss[0].indexOf("、") != ss[0].length() - 1) { // 如果1、后面没有空格可能会出现
					// 1、财政部审核而不是1、 财政部审核
					ss[0] = s.substring(0, s.indexOf("、")).trim();
					ss[1] = s.substring(s.indexOf("、") + 1);
				} else if (ss[0].contains("、")) {
					ss[0] = s.substring(0, s.indexOf("、")).trim();
				}
				if ((s.length() > 0 && str.contains(s.substring(0, 1)) && !"num".equals(flag))) {
					// 是一 二 三样式的。
					int index = 0;
					boolean istitle = true;
					while (true) {
						if (index < ss[0].length()) {
							String s_1 = ss[0].substring(index, index + 1); // 这里可能出现 大写日期（二〇一二年二月二日）
							if (s_1 == null || s_1.equals("") || !str.contains(s_1)) { // 
								istitle = false;
								break;
							}
							index++;
						} else {
							break;
						}
					}
					if (!istitle) {
						sb.append(s.toString());
						continue;
					}
					if (!checkbox.isSelected()) {
						s = ss[0] + "\n" + ss[1];
					}
					sb.append("$$$B" + s); // 没有直接弄$$$A，
					num++;
					if (flag == null) {
						flag = "str";
					}
				} else if ((s.length() > 0 && numStr.contains(s.substring(0, 1)) && !"str".equals(flag))) {
					// 是1 1.1 1.2 2 2.1 2.2样式的
					int index = 0;
					String title = "";
					while (true) {
						if (index < s.length()) {
							String s_1 = s.substring(index, index + 1); // 截取段落开始的每一个字。直到不是数字为止。
							if (s_1 == null || s_1.equals("") || !numStr.contains(s_1)) { // 如果不符合
								title = s.substring(0, index);
								break;
							}
							index++;
						} else {
							break;
						}
					}
					if ("".equals(title)) {
						continue;
					}
					if (title.endsWith(".")) {
						title = title.substring(0, title.length() - 1);
					}
					ss[0] = s.substring(0, index).trim();
					ss[1] = s.substring(index);
					int contains = (title.split("\\.")).length;
					if (contains == 1) { // 有可能是2011年之类的那样就错了
						int t_year = Integer.parseInt(title);
						if (t_year > 1000) {
							sb.append(s.toString());
							continue; // 如果是年份，就搞错了，直接continue
						}
						if (!checkbox.isSelected()) {
							s = ss[0] + "\n" + ss[1];
						}
						sb.append("$$$A" + s); // 没有直接弄$$$A，
					} else if (contains == 2) {
						if (!checkbox.isSelected()) {
							s = ss[0] + "\n" + ss[1];
						}
						sb.append("$$$B" + s); // 没有直接弄$$$A，
					} else {
						sb.append(s.toString());
					}
					if (flag == null) {
						flag = "num";
					}
					num++;
				} else if (num == 1 && ss[0].length() > 0 && !ss[0].equals("\n")) {
					if (!ss[0].contains("$$$")) {
						sb.append("$$$B\n" + s);
					} else {
						sb.append(s.toString());
					}
					num++;
				} else {
					sb.append(s.toString());
				}
			}
		}
		return sb.toString();
	}

	/*
	 * 去除文字前面的空格。
	 */
	private String formatStringBlank(String _string) {
		while (_string.length() > 0 && _string.charAt(0) == 32) {
			_string = _string.substring(1);
		}
		return _string;
	}

	private Pub_Templet_1VO getPub_Templet_1VO() {
		if (templetVO == null) {
			if ("law".equals(type)) {
				try {
					templetVO = UIUtil.getPub_Templet_1VO("LAW_LAW_CODE2");
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					templetVO = UIUtil.getPub_Templet_1VO("RULE_RULE_CODE2");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return templetVO;
	}
}
