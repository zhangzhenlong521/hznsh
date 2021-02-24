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
 * ���桢�ƶȵ�����档�����ʶ�� $$$A $$$B $$$C ��֧���Զ���ʽ���� ����һ�¡���һ�ڡ���һ������ ��һ��������������
 * Ӧ�ðѶ�Ӧģ���еı������������
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

	private boolean imported = false; //�Ƿ��Ѿ������

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

	// �����������
	public JPanel getPanel() {
		WLTPanel panel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new BorderLayout(), LookAndFeel.defaultShadeColor1, false);
		// top
		JPanel jpTop = new JPanel();
		jpTop.setLayout(new FlowLayout(FlowLayout.LEFT));
		String desc = "����ʽ���õ��ı�����ϵͳ, [�Զ���ʽ��]��\"����\"��ɸ�ʽ������!";
		jpTop.add(new JLabel(desc));
		btn_showSamle = new WLTButton("�鿴����");
		btn_format = new WLTButton("�Զ���ʽ��");
		btn_format.addActionListener(this);
		btn_format.setToolTipText("�Զ���ʽ���ɴ��������ǵ��ı�");
		checkbox = new JCheckBox("���ɳ�����");
		btn_showSamle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					UIUtil.openRemoteServerFile(null, "/sample/��浼������ļ�.doc");
				} catch (Exception ex) {
					MessageBox.showException(null, ex);
					ex.printStackTrace(); //
				}
			}
		});
		confirm = new WLTButton("����");
		btn_help = new WLTButton("����");
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
		textArea.setFont(new Font("����", Font.PLAIN, 12));
		textArea.select(0, 0);
		JScrollPane scrollPanel = new JScrollPane(textArea);
		panel.add(scrollPanel, BorderLayout.CENTER);
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		String text = textArea.getText(); // Ҫ���������
		if (e.getSource() == btn_format) {
			if ("".equals(text)) {
				MessageBox.show(this, "������Ҫ������ļ�����!");
				return;
			}
			ArrayList list = getTextList(text);
			textArea.setText(addSpecChar(list));
		} else if (e.getSource() == btn_help) {
			MessageBox.showInfo(this, "��ʽ��ԭ��: ������+����\n�����ǰ���($$$A, $$$B, $$$C...), ����:\n\n�ļ�����\n$$$A��һ�� �� ��\n$$$B��һ�� ˼·\n" + "$$$C��һ��\n  ��һ��������...\n$$$C�ڶ���\n  �ڶ���������...\n$$$A�ڶ��� Լ ��\n$$$B�ڶ��� Ŀ��\n$$$C������\n  ������������...");
		} else if (e.getSource() == confirm) {
			if ("".equals(text)) {
				MessageBox.show(this, "������Ҫ������ļ�����!");
				return;
			}
			String dataSourceType = new TBUtil().getDefaultDataSourceType();
			String newdataid = null; // �����������id
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
					selectID = null; //����һ���Ժ���ƿ�
					this.setCloseType(1);
					newdataid = tree.getID(); // ��ֵ
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
					newdataid = tree.getID();// ��ֵ
					templetCode = "RULE_RULE_CODE1";
				} catch (Exception e1) {
					MessageBox.show(this, e1.getMessage());
					e1.printStackTrace();
					return;
				}
			}
			if (newdataid != null && !newdataid.equals("")) {

			}
			int index = MessageBox.showOptionDialog(LawOrRuleImportDialog.this, "���ݵ���ɹ�!Ҫ���ڱ༭������", "��ʾ", new String[] { "�༭����", "��������", "�˳�" });
			if (index == 0) {// �༭

				BillCardDialog cardDialog = new BillCardDialog(LawOrRuleImportDialog.this, templetCode, WLTConstants.BILLDATAEDITSTATE_UPDATE);
				final BillCardPanel cardPanel = cardDialog.getBillcardPanel();
				cardPanel.queryDataByCondition(" id = " + newdataid);
				cardPanel.getRowNumberItemVO().setState(WLTConstants.BILLDATAEDITSTATE_UPDATE);// ���ô��е�״̬��
				WLTButton btn = new WLTButton("�༭��Ŀ");
				btn.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if (type.equalsIgnoreCase("law")) { // ����
							LawItemEditDialog dialog = new LawItemEditDialog(cardPanel, "�༭��Ŀ", 900, 600, cardPanel.getBillVO());
							dialog.setVisible(true);
						} else {// �ƶ�
							RuleItemEditDialog dialog = new RuleItemEditDialog(cardPanel, "�༭��Ŀ", 900, 600, cardPanel.getBillVO());
							dialog.setVisible(true);
						}
					}
				});
				cardPanel.addBatchBillCardButton(new WLTButton[] { btn });
				cardPanel.repaintBillCardButton();
				cardDialog.setVisible(true);
				if (cardDialog.getCloseType() == 1) {//�ж��Ƿ���Ҫ��ֹ���ƶȣ�����У����ƶ�Ԥ�������/2015-12-29��
					insertAlarm(cardDialog.getBillVO());

				}
			} else if (index == 1) {// ��������

			} else { // �˳�
				this.dispose();
			}
		}

	}

	/**
	 * �жϲ������ڹ�Ԥ��!
	 * Ŀǰ�ڹ�Ԥ�����ıȽϼ�ª��������Ҫ���� ��ĳ�����������Щ�޸ģ����磺�޸���2����Ŀ���ݣ�ɾ����1����Ŀ��������3����
	 * ���������cvs�Ա�����Ч����
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
			sql.putFieldValue("alarmsource", "�ڲ��ƶ�--�ƶ�ά��");
			sql.putFieldValue("alarmsourcetab", "rule_rule");
			sql.putFieldValue("state", "δ����");
			sql.putFieldValue("alarmdate", currdate);
			sql.putFieldValue("creator", userid);
			sql.putFieldValue("createdept", userdept);
			sql.putFieldValue("alarmsourcepk", _selectRule.getStringValue("id"));
			if (refrule2 != null && !"".equals(refrule2)) {
				String[] refruleids = TBUtil.getTBUtil().split(refrule2, ";");
				HashMap ruleMap = UIUtil.getHashMapBySQLByDS(null, "select id,rulename from rule_rule where id in(" + TBUtil.getTBUtil().getInCondition(refrule2) + ")");

				for (int m = 0; m < refruleids.length; m++) {
					//���ƶȱ���Щ�ƶȹ����ˣ��������ѡ����/2015-12-29��
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
							sql.putFieldValue("alarmreason", "��" + rulename + "���ƶ��ѷ�ֹ�����������ƶ�!");
							insertSQLlist.add(sql.getSQL());
						}
					}
				}
				insertSQLlist.add("update rule_rule set state='ʧЧ' where id in(" + tbutil.getInCondition(refrule2) + ")");

			}
			//�����ı������/2015-12-26��
			String formids = _selectRule.getStringValue("formids");
			if (formids != null && !"".equals(formids.trim())) {
				String tabname = tbutil.getSysOptionStringValue("�ƶȹ����ı�����", "BSD_FORM");//���������code��name�ֶ�
				tabname = tabname.toLowerCase();
				HashVO[] vos = UIUtil.getHashVoArrayByDS(null, "select id,code,name from " + tabname + " where id in(" + tbutil.getInCondition(formids) + ")");
				if (vos != null && vos.length > 0) {
					sql.putFieldValue("alarmreason", "��" + _selectRule.getStringValue("rulename") + "���ƶ��������޸ģ��������ر�!");
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

	// ������һ���з���List��
	private ArrayList getTextList(String text) {
		// �滻ȫ�ǿո�
		text = text.replaceAll((char) 8195 + "", " "); // ��word�е�ȫ�ǿո�
		text = text.replaceAll("��", " ");
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
					list.add(text); // ���һ����û�лس�
					break;
				}
			} else if (text.length() != 1) {
				// �������һ���ջس�
				list.add("\n");
			} else { // ���һ���ջس�
				list.add("\n");
				break;
			}
			text = text.substring(index + 1, text.length());
		}
		return list;
	}

	// �Զ��ӱ�ǡ�
	private String addSpecChar(ArrayList textList) {
		StringBuffer sb = new StringBuffer();
		String s = new String();
		int num = 0;
		boolean falg = false; // true�ǰ����½��������ӣ�false�ǲ�������
		for (int i = 0; i < textList.size(); i++) {
			String str = (String) textList.get(i);
			if (str != null && !str.equals("")) {
				if (str.trim().indexOf("��һ��") == 0 || str.trim().indexOf("��һ��") == 0 || str.trim().indexOf("��һ��") == 0) {
					falg = true;
					break;
				}
			}

		}
		if (falg) { // ������:��һ��
			// ��һ��
			for (Object object : textList) {
				s = object.toString();
				s = formatStringBlank(s);
				if (num == 0 && (s != null && s.length() > 0 && !s.equals("\n"))) { // ��һ�����ݿ϶�Ϊ����
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
				if (s.startsWith("��")) {
					if (ss[0].endsWith("��") || ss[0].endsWith("��\n")) {
						sb.append("$$$A" + s);
						num++;
					} else if (ss[0].endsWith("��") || ss[0].endsWith("��\n")) {
						sb.append("$$$B" + s);
						num++;
					} else if (ss[0].endsWith("��") || ss[0].endsWith("��\n")) {
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
		} else { // �����ڣ� һ�� ����
			String str = "һ�����������߰˾�ʮ��.";
			String numStr = "0123456789.";
			String flag = null; // ��־�Ǵ�д�Ļ��ǰ��������֡�Ϊnull��ʱ���ǳ�ʼ��������ֵ�� str,num.
			for (Object object : textList) {
				s = object.toString();
				s = formatStringBlank(s);
				if (num == 0 && (s != null && s.length() > 0 && !s.equals("\n"))) { // ��һ�����ݿ϶�Ϊ����
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
				if (ss[0].contains("��") && ss[0].indexOf("��") != ss[0].length() - 1) { // ���1������û�пո���ܻ����
					// 1����������˶�����1�� ���������
					ss[0] = s.substring(0, s.indexOf("��")).trim();
					ss[1] = s.substring(s.indexOf("��") + 1);
				} else if (ss[0].contains("��")) {
					ss[0] = s.substring(0, s.indexOf("��")).trim();
				}
				if ((s.length() > 0 && str.contains(s.substring(0, 1)) && !"num".equals(flag))) {
					// ��һ �� ����ʽ�ġ�
					int index = 0;
					boolean istitle = true;
					while (true) {
						if (index < ss[0].length()) {
							String s_1 = ss[0].substring(index, index + 1); // ������ܳ��� ��д���ڣ�����һ������¶��գ�
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
					sb.append("$$$B" + s); // û��ֱ��Ū$$$A��
					num++;
					if (flag == null) {
						flag = "str";
					}
				} else if ((s.length() > 0 && numStr.contains(s.substring(0, 1)) && !"str".equals(flag))) {
					// ��1 1.1 1.2 2 2.1 2.2��ʽ��
					int index = 0;
					String title = "";
					while (true) {
						if (index < s.length()) {
							String s_1 = s.substring(index, index + 1); // ��ȡ���俪ʼ��ÿһ���֡�ֱ����������Ϊֹ��
							if (s_1 == null || s_1.equals("") || !numStr.contains(s_1)) { // ���������
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
					if (contains == 1) { // �п�����2011��֮��������ʹ���
						int t_year = Integer.parseInt(title);
						if (t_year > 1000) {
							sb.append(s.toString());
							continue; // �������ݣ��͸���ˣ�ֱ��continue
						}
						if (!checkbox.isSelected()) {
							s = ss[0] + "\n" + ss[1];
						}
						sb.append("$$$A" + s); // û��ֱ��Ū$$$A��
					} else if (contains == 2) {
						if (!checkbox.isSelected()) {
							s = ss[0] + "\n" + ss[1];
						}
						sb.append("$$$B" + s); // û��ֱ��Ū$$$A��
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
	 * ȥ������ǰ��Ŀո�
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
