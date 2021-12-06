package com.pushworld.ipushgrc.ui.rule.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Toolkit;
import java.util.HashMap;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillOfficeDialog;
import cn.com.infostrategy.ui.report.BillHtmlPanel;

public class RuleShowHtmlDialog {
	private Container parent;
	boolean canexport = new TBUtil().getSysOptionBooleanValue("�ƶ��Ƿ�������", true);

	public RuleShowHtmlDialog(BillListPanel _listPanel) {
		this(_listPanel, new String[0]);
	}

	/**
	 * 
	 * @param _listPanel
	 * @param _ifHaveItem
	 * @param prikey  �ؼ���
	 */
	public RuleShowHtmlDialog(BillListPanel _listPanel, String[] prikey) {
		this(_listPanel, _listPanel, prikey);
	}

	public RuleShowHtmlDialog(Container _parent, BillListPanel _listPanel, String[] prikey) {
		this(_parent, _listPanel.getSelectedBillVO().convertToHashVO(), prikey);
	}

	public RuleShowHtmlDialog(Container _parent, HashVO vo, String[] prikey) {
		parent = _parent;
		boolean ifHaveItem = new TBUtil().getSysOptionBooleanValue("�ƶ��Ƿ����Ŀ", true);
		if (!ifHaveItem) { // û����Ŀ��ֱ�Ӵ�����
			this.showTextFile(vo);
		} else {
			this.showHtmlDialog(vo.getStringValue("id"), prikey);
		}
	}

	public void showTextFile(HashVO vo) {
		String textFile = vo.getStringValue("textfile");
		if (textFile == null || textFile.equals("")) {
			MessageBox.show(parent, "���ƶ�û�����ģ�");
			return;
		} else {
			try {
				openfileByOffice("", "/officecompfile/" + textFile);//����ƶ������ģ�����Office�ؼ��򿪡����/2015-09-07��
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//UIUtil.openRemoteServerFile("office", textFile);
		}
	}

	public RuleShowHtmlDialog(Container _parent, String ruleid, String[] prikey) {
		this.parent = _parent;
		this.showHtmlDialog(ruleid, prikey);
	}

	private void openfileByOffice(String dir, String str_filename) throws Exception {
		String filetype = str_filename.substring(str_filename.lastIndexOf(".") + 1, str_filename.length());
		if (filetype.equalsIgnoreCase("doc") || filetype.equalsIgnoreCase("docx") || filetype.equalsIgnoreCase("wps") || filetype.equalsIgnoreCase("xls") || filetype.equalsIgnoreCase("xlsx") || filetype.equalsIgnoreCase("ppt") || filetype.equalsIgnoreCase("pptx")) { //�ʴ���Ŀ��������ӿɱ༭���ļ����͡����/2012-11-06��
			if (str_filename.indexOf("/") != -1) {
				str_filename = str_filename.replaceAll("\\\\", "/");
			}
			String path = "", fileName = "";
			if (str_filename != null && str_filename.indexOf("/") != -1) {
				path = (TBUtil.isEmpty(dir) ? "" : dir) + str_filename.substring(0, str_filename.lastIndexOf("/"));
				fileName = str_filename.substring(str_filename.lastIndexOf("/") + 1, str_filename.length());
			}
			BillOfficeDialog dialog = new BillOfficeDialog(parent, fileName, canexport, canexport, path, false);
			dialog.maxToScreenSizeBy1280AndLocationCenter();
			dialog.setVisible(true);
		}
	}

	private String getViewFileName(String _realFileName) {
		if (_realFileName != null && _realFileName.indexOf("/") != -1) {
			String param = _realFileName.substring(_realFileName.lastIndexOf("/") + 1, _realFileName.length());
			if (param != null && param.startsWith("N")) {
				TBUtil tbUtil = new TBUtil(); //
				int li_extentNamePos = param.lastIndexOf("."); //�ļ�����չ����λ��!�������и���!������ҵ��Ŀ��������ļ��ǴӺ�̨�����!!Ҳ������û��꡵�!!���Ա���!
				if (li_extentNamePos > 0) {
					return tbUtil.convertHexStringToStr(param.substring(param.indexOf("_") + 1, li_extentNamePos)) + param.substring(li_extentNamePos, param.length()); //
				} else {
					return tbUtil.convertHexStringToStr(param.substring(param.indexOf("_") + 1, param.length())); ////
				}
			} else {
				return param; //��ǰ�İ汾Ҳ�д�·���ģ�
			}
		} else {
			if (_realFileName == null || _realFileName.indexOf("_") < 0) {
				return _realFileName; //
			}
			return _realFileName.substring(_realFileName.indexOf("_") + 1, _realFileName.length()); //
		}
	}

	/**
	 * �� html
	 * 
	 * @param _listPanel
	 */
	private void showHtmlDialog(String ruleid, String[] prikey) {
		//�����е��ļ�ֱ�����ĸ��������ж����ӱ��Ƿ�������.
		try {
			String itemcount = UIUtil.getStringValueByDS(null, "select count(id) from rule_rule_item where ruleid = " + ruleid);
			if (!"0".equals(itemcount)) {
				this.currShowHtmlDialog(parent, ruleid, prikey);
			} else {
				HashVO hvos[] = UIUtil.getHashVoArrayByDS(null, "select * from rule_rule where id = " + ruleid);
				if (hvos.length > 0) {
					String textfile = hvos[0].getStringValue("textfile");
					String attachfile = hvos[0].getStringValue("attachfile");
					if (!TBUtil.isEmpty(textfile)) {
						openfileByOffice("", "/officecompfile/" + textfile);
						//						UIUtil.openRemoteServerFile("office", textfile);
					} else if (!TBUtil.isEmpty(attachfile)) {
						String[] attachfiles = TBUtil.getTBUtil().split(attachfile, ";");
						if (attachfiles == null || attachfiles.length == 0) {
							MessageBox.show(parent, "���ƶ�û������.");
							return;
						} else {
							openfileByOffice("/upload", attachfiles[0]);//������ƶ�û����ĿҲû�����ģ����и���ʱ����ȡ��һ�����������/2015-01-27��
							//						UIUtil.openRemoteServerFile("office", attachfile);
						}

					} else {
						MessageBox.show(parent, "���ƶ�û������.");
					}
				} else {
					MessageBox.show(parent, "���ƶ��ѱ�ɾ��.");//��ɾ�����ƶȵ���鿴����û�з�Ӧ������Ҫ��ʾһ�¡����/2015-12-26��
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void currShowHtmlDialog(Container _parent, String _ruleId, String[] prikey) {
		BillHtmlPanel billHTMLPanel = new BillHtmlPanel(canexport);
		HashMap hashMap = new HashMap();
		hashMap.put("id", _ruleId);
		hashMap.put("primarykey", prikey);
		hashMap.put("table_main", "rule_rule");
		hashMap.put("table_main_file", "attachfile");
		hashMap.put("column_name", "rulename");
		hashMap.put("column_title", "itemtitle");
		hashMap.put("table_content", "rule_rule_item");
		hashMap.put("content", "itemcontent");
		hashMap.put("content_foreignkey", "ruleid");
		hashMap.put("content_filekey", "attachfile");
		hashMap.put("url", System.getProperty("CALLURL"));//
		billHTMLPanel.loadhtml("com.pushworld.ipushgrc.bs.law.p010.LawPrimaryKeyAllFileInquire", hashMap);
		BillDialog dialog = new BillDialog(_parent, "�ƶȲ鿴", 1000, Toolkit.getDefaultToolkit().getScreenSize().height - 30);
		dialog.setLayout(new BorderLayout());
		dialog.add(billHTMLPanel);
		dialog.setVisible(true);
	}
}
