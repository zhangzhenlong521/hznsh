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
	boolean canexport = new TBUtil().getSysOptionBooleanValue("制度是否允许拷贝", true);

	public RuleShowHtmlDialog(BillListPanel _listPanel) {
		this(_listPanel, new String[0]);
	}

	/**
	 * 
	 * @param _listPanel
	 * @param _ifHaveItem
	 * @param prikey  关键字
	 */
	public RuleShowHtmlDialog(BillListPanel _listPanel, String[] prikey) {
		this(_listPanel, _listPanel, prikey);
	}

	public RuleShowHtmlDialog(Container _parent, BillListPanel _listPanel, String[] prikey) {
		this(_parent, _listPanel.getSelectedBillVO().convertToHashVO(), prikey);
	}

	public RuleShowHtmlDialog(Container _parent, HashVO vo, String[] prikey) {
		parent = _parent;
		boolean ifHaveItem = new TBUtil().getSysOptionBooleanValue("制度是否分条目", true);
		if (!ifHaveItem) { // 没有条目，直接打开正文
			this.showTextFile(vo);
		} else {
			this.showHtmlDialog(vo.getStringValue("id"), prikey);
		}
	}

	public void showTextFile(HashVO vo) {
		String textFile = vo.getStringValue("textfile");
		if (textFile == null || textFile.equals("")) {
			MessageBox.show(parent, "该制度没有正文！");
			return;
		} else {
			try {
				openfileByOffice("", "/officecompfile/" + textFile);//如果制度有正文，则用Office控件打开【李春娟/2015-09-07】
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
		if (filetype.equalsIgnoreCase("doc") || filetype.equalsIgnoreCase("docx") || filetype.equalsIgnoreCase("wps") || filetype.equalsIgnoreCase("xls") || filetype.equalsIgnoreCase("xlsx") || filetype.equalsIgnoreCase("ppt") || filetype.equalsIgnoreCase("pptx")) { //邮储项目提出，增加可编辑的文件类型【李春娟/2012-11-06】
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
				int li_extentNamePos = param.lastIndexOf("."); //文件的扩展名的位置!即必须有个点!但在兴业项目中有许多文件是从后台灌入的!!也遇到到没后辍的!!所以报错!
				if (li_extentNamePos > 0) {
					return tbUtil.convertHexStringToStr(param.substring(param.indexOf("_") + 1, li_extentNamePos)) + param.substring(li_extentNamePos, param.length()); //
				} else {
					return tbUtil.convertHexStringToStr(param.substring(param.indexOf("_") + 1, param.length())); ////
				}
			} else {
				return param; //以前的版本也有存路径的？
			}
		} else {
			if (_realFileName == null || _realFileName.indexOf("_") < 0) {
				return _realFileName; //
			}
			return _realFileName.substring(_realFileName.indexOf("_") + 1, _realFileName.length()); //
		}
	}

	/**
	 * 打开 html
	 * 
	 * @param _listPanel
	 */
	private void showHtmlDialog(String ruleid, String[] prikey) {
		//可能有的文件直接贴的附件。先判断下子表是否有数据.
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
							MessageBox.show(parent, "该制度没有内容.");
							return;
						} else {
							openfileByOffice("/upload", attachfiles[0]);//如果该制度没有条目也没有正文，但有附件时，则取第一个附件【李春娟/2015-01-27】
							//						UIUtil.openRemoteServerFile("office", attachfile);
						}

					} else {
						MessageBox.show(parent, "该制度没有内容.");
					}
				} else {
					MessageBox.show(parent, "该制度已被删除.");//被删除的制度点击查看链接没有反应，故需要提示一下【李春娟/2015-12-26】
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
		BillDialog dialog = new BillDialog(_parent, "制度查看", 1000, Toolkit.getDefaultToolkit().getScreenSize().height - 30);
		dialog.setLayout(new BorderLayout());
		dialog.add(billHTMLPanel);
		dialog.setVisible(true);
	}
}
