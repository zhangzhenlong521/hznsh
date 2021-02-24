package cn.com.infostrategy.ui.mdata;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.*;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.to.mdata.templetvo.DefaultTMO;
import cn.com.infostrategy.ui.common.*;

/*
 * 元原模板配置..
 */
public class MetaTempletConfigPanel extends AbstractWorkPanel implements ActionListener {

	private static final long serialVersionUID = 3514696711209919711L;
	private JTextField search_text, search_table = null;
	private JComboBox search_box = null;

	private WLTButton btn_search, btn_config_dev, btn_config_app, btn_import, btn_delete, btn_copy, btn_compare, btn_importxml, btn_exportxml, btn_exportxml_all, btn_preview; //
	private BillListPanel blp_main;
	private FrameWorkMetaDataServiceIfc service = null;
	private final TextArea textArea=new TextArea();

	/**
	 * 是否是实施人员配置!!!
	 * @return
	 */
	public boolean isAppConfig() {
		if ("Y".equals(getMenuConfMapValueAsStr("是否实施配置"))) {
			return true; //
		}
		return false; //
	}

	public boolean isSelectPanel() {
		return false;
	}

	/**
	 * 初始化页面
	 */
	public void initialize() {

		this.setLayout(new BorderLayout());
		this.add(getNorthPanel(), BorderLayout.NORTH); //上面的按钮拦!
		try {
			blp_main = new BillListPanel(new DefaultTMO("单据模板", new String[][] { { "pk_pub_templet_1", "主键", "100", "N" }, //
					{ "templetcode", "模板编码", "220", "Y" }, //
					{ "templetname", "模板名称", "150", "Y" },//
					{ "tablename", "查询表名", "150", "Y" }, //
					{ "savedtablename", "保存表名", "150", "Y" }, //
					{ "datapolicy", "数据权限策略", "150", "Y" }, //数据权限策略很关键,最好在列表中一眼清！
					{ "savetype", "来源", "150", "Y" } })); //
			this.add(blp_main, BorderLayout.CENTER); ////
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	private JPanel getNorthPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT)); ////
		JLabel label = new JLabel("模板编码/名称:", SwingConstants.RIGHT); //
		search_text = new JTextField(); //
		search_text.setPreferredSize(new Dimension(150, 20)); //
		JLabel label_table = new JLabel("表名:", SwingConstants.RIGHT);
		search_table = new JTextField(); //
		search_table.setPreferredSize(new Dimension(100, 20));
		search_box = new JComboBox(new String[] { "DB", "XML", "ALL" });
		search_box.setPreferredSize(new Dimension(50, 20));
		btn_search = new WLTButton("查询"); //
		btn_import = new WLTButton("导入"); //
		btn_config_dev = new WLTButton("开发配置"); //
		btn_config_app = new WLTButton("实施配置"); //
		btn_delete = new WLTButton("删除"); //
		btn_copy = new WLTButton("拷贝"); //
		btn_compare = new WLTButton("对比");
		btn_importxml = new WLTButton("导入XML"); //
		btn_exportxml = new WLTButton("导出XML"); //
		btn_exportxml_all = new WLTButton("导出XML(全部)");
		btn_preview = new WLTButton("预览"); //预览 【杨科/2013-03-25】

		btn_search.addActionListener(this); //
		btn_import.addActionListener(this); //
		btn_config_dev.addActionListener(this); //
		btn_config_app.addActionListener(this); //
		btn_delete.addActionListener(this); //
		btn_copy.addActionListener(this); //
		btn_compare.addActionListener(this);
		btn_importxml.addActionListener(this); //
		btn_exportxml.addActionListener(this); //
		btn_exportxml_all.addActionListener(this);
		btn_preview.addActionListener(this);

		panel.add(label); //
		panel.add(search_text); //
		panel.add(label_table);
		panel.add(search_table);
		panel.add(search_box); //
		panel.add(btn_search); //
		if (isAppConfig()) { //如果是实施人员配置,则只要该按钮
			panel.add(btn_config_app); //
			panel.add(btn_exportxml); //
			panel.add(btn_preview);
		} else if (!isSelectPanel()) {
			panel.add(btn_import); //
			panel.add(btn_config_dev); //
			panel.add(btn_delete); //
			panel.add(btn_copy); //
			panel.add(btn_compare);
			panel.add(btn_importxml); //
			panel.add(btn_exportxml); //
			panel.add(btn_exportxml_all);
			panel.add(btn_preview);
		}
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_search) {
			onSearch(); //
		} else if (e.getSource() == btn_import) {
			onReference(); //从表导入!
		} else if (e.getSource() == btn_config_dev) {
			onConfig(false); //
		} else if (e.getSource() == btn_config_app) {
			onConfig(true); //实施配置!
		} else if (e.getSource() == btn_delete) {
			onDelete(); //删除
		} else if (e.getSource() == btn_copy) {
			onCopy(); //拷贝
		} else if (e.getSource() == btn_importxml) {
			onImportXMLTemplet(); //导入XML
		} else if (e.getSource() == btn_exportxml) {
			onExportXMLTemplet(); //导出XML
		} else if (e.getSource() == btn_exportxml_all) {
			onExportXMLTempletAll(); //导出XML
		} else if (e.getSource() == btn_compare) {
			onCompare(); //比较
		} else if (e.getSource() == btn_preview) {
			onPreview(); //预览
		}
	}

	//	private String getQueryCols() {
	//		return " pk_pub_templet_1,templetcode,templetname,tablename,savedtablename "; //
	//	}

	private void onCompare() {
		int[] li_ = blp_main.getTable().getSelectedRows();
		if (li_ == null || li_.length <= 1) {
			JOptionPane.showMessageDialog(this, "请选择两条记录进行比较!");
			return;
		}
		String code1 = blp_main.getRealValueAtModel(li_[0], "TEMPLETCODE");
		String code2 = blp_main.getRealValueAtModel(li_[1], "TEMPLETCODE");
		String t1 = blp_main.getRealValueAtModel(li_[0], "savetype");
		String t2 = blp_main.getRealValueAtModel(li_[1], "savetype");
		int type1 = 0, type2 = 0;
		if (!t1.equals("数据库")) {
			//直接从xml路径取 【杨科/2013-03-25】
			if (t1 != null && !t1.equals("") && t1.indexOf("XML_") > 0) {
				code1 = "/" + t1.substring(t1.indexOf("XML_") + 4);
				type1 = 3;
			} else {
				type1 = 1;
			}
		}
		if (!t2.equals("数据库")) {
			//直接从xml路径取 【杨科/2013-03-25】
			if (t2 != null && !t2.equals("") && t2.indexOf("XML_") > 0) {
				code2 = "/" + t2.substring(t2.indexOf("XML_") + 4);
				type2 = 3;
			} else {
				type2 = 1;
			}
		}
		TempletCompareDialog cd = new TempletCompareDialog(blp_main, code1, type1, code2, type2);//采用已有的比较功能
		cd.getBtnp().setVisible(false);
		cd.setVisible(true);
	}

	/**
	 * 查询
	 */
	public void onSearch() {

		FrameWorkMetaDataServiceIfc service = null;
		String str_text = search_text.getText();
		String str_table = search_table.getText();
		try {
			service = getService();
			List<Object> list = service.getAllTemplate(str_text, str_table, search_box.getSelectedItem().toString());
			HashVO[] vos = (HashVO[]) list.get(0);
			HashMap datapolicyMap = UIUtil.getHashMapBySQLByDS(null, "select id,name from pub_datapolicy"); //
			for (int i = 0; i < vos.length; i++) {
				String str_policyId = vos[i].getStringValue("datapolicy"); //
				if (datapolicyMap.containsKey(str_policyId)) {
					vos[i].setAttributeValue("datapolicy", (String) datapolicyMap.get(str_policyId)); //
				}
			}
			blp_main.putValue(vos);
			blp_main.getLabel_pagedesc().setText("共" + vos.length + "条");
			blp_main.setAllRowStatusAs(WLTConstants.BILLDATAEDITSTATE_INIT);
			blp_main.revalidate();
			if (list.get(1) != null && !"".equals(list.get(1).toString().trim())) {
				MessageBox.showWarn(blp_main, list.get(1).toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(blp_main, e.fillInStackTrace());
		}
		//		if (str_text == null || str_text.trim().equals("")) {
		//			blp_main.QueryData("select " + getQueryCols() + " from pub_templet_1 order by templetcode asc"); //
		//		} else {
		//			blp_main.QueryData("select " + getQueryCols() + " from pub_templet_1 where lower(templetcode) like '%" + str_text.trim().toLowerCase() + "%' or lower(templetname) like '%" + str_text.trim().toLowerCase() + "%' order by templetcode asc"); //
		//		}
	}

	/**
	 * 配置!!
	 */
	private void onConfig(boolean _isAppConf) {
		BillVO billVO = blp_main.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String str_templetCode = billVO.getStringValue("templetcode"); //
		if ("数据库".equals(billVO.getStringValue("savetype"))) {
			TempletModify2 mfdialog = new TempletModify2(this, str_templetCode, _isAppConf); //进行配置!!
		} else {
			//直接从xml路径取 【杨科/2013-03-25】
			String savetype = billVO.getStringValue("savetype");
			if (savetype != null && !savetype.equals("") && savetype.indexOf("XML_") > 0) {
				String xml_url = "/" + savetype.substring(savetype.indexOf("XML_") + 4);
				TempletModify2 mfdialog = new TempletModify2(this, str_templetCode, xml_url, _isAppConf, true); //进行配置!!
			} else {
				TempletModify2 mfdialog = new TempletModify2(this, str_templetCode, _isAppConf, true); //进行配置!!
			}
		}
	}

	/**
	 * 导入!
	 */
	private void onReference() {
		SelectTableDialog refTableDialog = new SelectTableDialog(this, "根据一个表或视图的结构快速创建模板"); //选择表
		refTableDialog.setVisible(true);
		if (refTableDialog.getCloseType() == 1) {
			search_text.setText(refTableDialog.getReturnTempletCode()); //
			onSearch(); //
		}
	}

	/**
	 * 删除!
	 */
	private void onDelete() {
		BillVO billVO = blp_main.getSelectedBillVO(); //
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		if (!"数据库".equals(billVO.getStringValue("savetype"))) {
			MessageBox.show(this, "非数据库模板无法直接删除!");
			return;
		}

		String str_pk_pub_templet_1 = billVO.getStringValue("pk_pub_templet_1"); //
		String str_templetCode = billVO.getStringValue("templetcode"); //
		String str_templetName = billVO.getStringValue("templetname"); //
		if (!MessageBox.confirm(this, "您确定要删除模板【" + str_templetCode + "/" + str_templetName + "】吗?")) { ////
			return; //
		}
		String str_sql_1 = "delete from pub_templet_1_item where pk_pub_templet_1='" + str_pk_pub_templet_1 + "'"; //
		String str_sql_2 = "delete from pub_templet_1      where pk_pub_templet_1='" + str_pk_pub_templet_1 + "'"; //
		try {
			UIUtil.executeBatchByDS(null, new String[] { str_sql_1, str_sql_2 }); //
			blp_main.removeRow(true); //
			MessageBox.show(this, "删除模板成功!"); //
		} catch (Exception e) {
			MessageBox.showException(this, e); //
		}
	}
	//得到选择的数据
	
	public BillVO getSelectBillVO() {
		return blp_main.getSelectedBillVO();
	}

	/**
	 * 拷贝!!!
	 */
	private void onCopy() {
		int li_count = blp_main.getTable().getSelectedRowCount();
		if (li_count <= 0) {
			MessageBox.showSelectOne(this);
			return;
		}
		int selected_rows = blp_main.getTable().getSelectedRow();
		String str_oldparent_id = blp_main.getRealValueAtModel(selected_rows, "PK_PUB_TEMPLET_1"); // 原来记录的主键!!
		String str_oldtablename = blp_main.getRealValueAtModel(selected_rows, "TABLENAME"); // 原来查询表名!!!【李春娟/2012-07-13】
		String str_oldparentcode = blp_main.getRealValueAtModel(selected_rows, "TEMPLETCODE"); // 原来模板编码!!!
		String str_oldparentname = blp_main.getRealValueAtModel(selected_rows, "TEMPLETNAME"); // 原来模板名!!!

		ShowCopyTempleteDialog showCopyTempleteDialog = new ShowCopyTempleteDialog(this, str_oldtablename, str_oldparentcode, str_oldparentname);
		showCopyTempleteDialog.setVisible(true);

		try {
			if (showCopyTempleteDialog.getCloseType() == 0) {
				Vector v_sqls = new Vector();
				String str_temp_code = showCopyTempleteDialog.getTempleteCode(); // 新编码
				String str_tem_name = showCopyTempleteDialog.getTempleteName(); // 新名称

				String fromtype = blp_main.getRealValueAtModel(selected_rows, "savetype");
				if (fromtype != null && fromtype.indexOf("XML") > 0) {
					try {
						FrameWorkMetaDataServiceIfc service = getService(); //
						HashMap p = new HashMap();
						p.put("pub_templet_1_templetcode", str_temp_code);
						p.put("pub_templet_1_templetname", str_tem_name);
						service.importRecordsXMLToTable(new String[] { "/" + fromtype.substring(fromtype.indexOf("_") + 1) }, null, true, p);
						JOptionPane.showMessageDialog(this, "复制模板已完成!");
					} catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(this, "复制模板出错!");
					}
					return;
				}

				HashVOStruct vos_parent = UIUtil.getHashVoStructByDS(null, "select * from pub_templet_1 where pk_pub_templet_1='" + str_oldparent_id + "'");
				String[] str_headname = vos_parent.getHeaderName(); //

				String str_newparentid = null; //
				StringBuffer sb_1 = new StringBuffer();
				sb_1.append("insert into pub_templet_1 ");
				sb_1.append("( ");
				for (int i = 0; i < str_headname.length; i++) {
					sb_1.append(str_headname[i]);
					if (i != str_headname.length - 1) {
						sb_1.append(","); //
					}
				}

				sb_1.append(") select ");
				for (int i = 0; i < str_headname.length; i++) { //
					if (str_headname[i].equalsIgnoreCase("pk_pub_templet_1")) { //主键
						str_newparentid = UIUtil.getSequenceNextValByDS(null, "s_pub_templet_1");
						sb_1.append("'" + str_newparentid + "'");
					} else if (str_headname[i].equalsIgnoreCase("templetcode")) { //新编码
						sb_1.append("'" + str_temp_code + "'");
					} else if (str_headname[i].equalsIgnoreCase("templetname")) { //新名称
						sb_1.append("'" + str_tem_name + "'");
					} else { //直接拷贝原来的
						sb_1.append(str_headname[i]);
					}
					if (i != str_headname.length - 1) {
						sb_1.append(","); //
					}
				}
				sb_1.append(" from pub_templet_1 where pk_pub_templet_1='" + str_oldparent_id + "'"); //

				v_sqls.add(sb_1.toString()); //

				HashVOStruct childHVOStruct = UIUtil.getHashVoStructByDS(null, "select * from pub_templet_1_item where pk_pub_templet_1='" + str_oldparent_id + "'");
				String[] childHeaderNames = childHVOStruct.getHeaderName(); //
				HashVO[] childHVOs = childHVOStruct.getHashVOs(); //

				for (int i = 0; i < childHVOs.length; i++) { //遍历所有记录
					StringBuffer sb_2 = new StringBuffer();
					sb_2.append("insert into pub_templet_1_item ");
					sb_2.append("( ");
					for (int j = 0; j < childHeaderNames.length; j++) {
						sb_2.append(childHeaderNames[j]);
						if (j != childHeaderNames.length - 1) {
							sb_2.append(","); //
						}
					}

					sb_2.append(") select "); ////
					for (int j = 0; j < childHeaderNames.length; j++) { //
						if (childHeaderNames[j].equalsIgnoreCase("pk_pub_templet_1_item")) { //主键
							sb_2.append("'" + UIUtil.getSequenceNextValByDS(null, "s_pub_templet_1_item") + "'");
						} else if (childHeaderNames[j].equalsIgnoreCase("pk_pub_templet_1")) { //父记录主键
							sb_2.append("'" + str_newparentid + "'");
						} else { //直接拷贝原来的
							sb_2.append(childHeaderNames[j]);
						}
						if (j != childHeaderNames.length - 1) {
							sb_2.append(","); //
						}
					}
					sb_2.append(" from pub_templet_1_item where pk_pub_templet_1_item='" + childHVOs[i].getStringValue("pk_pub_templet_1_item") + "'"); //

					v_sqls.add(sb_2.toString());
				}
				UIUtil.executeBatchByDS(null, v_sqls); ////
				JOptionPane.showMessageDialog(this, "复制模板已完成!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "创建复制模板出错!");
		}
	}

	/**
	 * 从一个XML中导入模板!!!
	 */
	private void onImportXMLTemplet() {//导入XML格式模版
				final BillDialog dialog = new BillDialog(this, "导入XML", 1000, 700);  //
				dialog.setLayout(new BorderLayout());
				textArea.setBackground(Color.WHITE); //
				textArea.setForeground(Color.BLUE); //
				textArea.setFont(new Font("宋体", Font.PLAIN, 12));
				textArea.select(0, 0); //
				JPanel jp = new JPanel();
				jp.setLayout(new FlowLayout(FlowLayout.CENTER));
				WLTButton confirm = new WLTButton("确定");
				WLTButton cancel = new WLTButton("取消");
				confirm.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							if (textArea == null || textArea.getText().trim().equals("") || textArea.getText() == null) {
								MessageBox.show("请输入需要导入的模版!!");
								return;
							}
							FrameWorkMetaDataServiceIfc service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class); //
							service.importXMLTemplet(null, textArea.getText()); //导入模板!!!
							MessageBox.show(MetaTempletConfigPanel.this, "导入XML格式模版成功!!!");
						} catch (Exception ex) {
							ex.printStackTrace(); //
						}
						textArea.setText("");
						dialog.dispose();
					}
				});
				cancel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dialog.dispose();

					}
				});  //
				jp.add(confirm);
				jp.add(cancel);
				dialog.getContentPane().add(textArea, BorderLayout.CENTER); //
				dialog.getContentPane().add(jp, BorderLayout.SOUTH);
				dialog.setVisible(true); //
	}

	/**
	 * 将模板导出成XML(单个)
	 */
	private void onExportXMLTemplet() {//导出XML格式模版
		int row = blp_main.getSelectedRow();
		if (row < 0) {
			MessageBox.showSelectOne(this);
			return;
		}

		String templetCode = blp_main.getValueAt(row, "templetcode").toString();
		String savetype = blp_main.getValueAt(row, "savetype").toString();
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("保存到");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);

		int result = fc.showSaveDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) { //如果是确定的
			String savePath = fc.getSelectedFile().getPath() + "\\";
			boolean rs = false;
			if (savetype != null && savetype.indexOf("XML") >= 0) {
				rs = doExportXMLTemplet(savePath, templetCode, "/" + savetype.substring(savetype.indexOf("_") + 1));
			} else {
				rs = doExportXMLTemplet(savePath, templetCode, null);
			}
			if (rs) {
				MessageBox.show(this, "模板成功导出到 [" + savePath + templetCode + ".xml]");
			}

		}
	}

	/**
	 * 将模板导出成XML(看到的全部)
	 */
	private void onExportXMLTempletAll() {
		//String savePath = "";
		//String templetCode = "";
		final int rowCount = blp_main.getRowCount();
		if (rowCount <= 0) {
			MessageBox.showSelectOne(this);
			return;
		}

		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("保存到");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		int result = fc.showSaveDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) { //如果是确定的
			final String savePath = fc.getSelectedFile().getPath() + "\\";
			new SplashWindow(this, "正在导出,请等待...", new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					try {
						String savetype = null;
						for (int i = 0; i < rowCount; i++) {
							savetype = blp_main.getValueAt(i, "savetype").toString();
							if (savetype != null && savetype.indexOf("XML") >= 0) {
								doExportXMLTemplet(savePath, blp_main.getValueAt(i, "templetcode").toString(), "/" + savetype.substring(savetype.indexOf("_") + 1));
							} else {
								doExportXMLTemplet(savePath, blp_main.getValueAt(i, "templetcode").toString(), null);
							}
							((SplashWindow) e.getSource()).setWaitInfo("正在导出,请等待...[" + (i + 1) + "/" + rowCount + "]");
						}
					} catch (Exception _ex) {
						SplashWindow.closeSplashWindow();
						_ex.printStackTrace();
						MessageBox.show(blp_main, "导出失败!");
						return;
					}
					MessageBox.show(blp_main, "模板成功导出到 [" + savePath + "], " + "合计[" + rowCount + "]个");
				}
			});

		}
	}

	private boolean doExportXMLTemplet(String savePath, String templetCode, String xmlFile) {
		boolean ret = false;
		if (TBUtil.isEmpty(templetCode)) {
			return false;
		}
		try {
			FrameWorkMetaDataServiceIfc service = getService(); //
			String str_xml = null;
			if (xmlFile != null && !"".equals(xmlFile.trim())) {
				str_xml = service.getXMlFromTableRecords(null, null, null, null, xmlFile);
			} else {
				str_xml = service.getXMlFromTableRecords(null, new String[] { "select * from pub_templet_1 where templetcode='" + templetCode + "'", //主表的所有数据!这是一个通用的万能的导出所有表结构的
						"select * from pub_templet_1_item where pk_pub_templet_1 in (select pk_pub_templet_1 from pub_templet_1 where templetcode='" + templetCode + "') order by showorder" }, ////子表的所有数据!
						new String[][] { { "pub_templet_1", "pk_pub_templet_1", "S_PUB_TEMPLET_1" }, { "pub_templet_1_item", "pk_pub_templet_1_item", "S_PUB_TEMPLET_1_ITEM" } }, //主键字段约束!! 
						new String[][] { { "pub_templet_1_item.pk_pub_templet_1", "pub_templet_1.pk_pub_templet_1" } }, null //外键约束!!
						);
			}
			File saveToFile = new File(savePath + templetCode + ".xml");
			new TBUtil().writeBytesToOutputStream(new FileOutputStream(saveToFile, false), str_xml.getBytes("UTF-8")); //写文件!!
			ret = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	//预览 【杨科/2013-03-25】
	public void onPreview() {
		BillVO billVO = blp_main.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String str_templetCode = billVO.getStringValue("templetcode");
		if ("数据库".equals(billVO.getStringValue("savetype"))) {
			BillCardDialog billCardDialog = new BillCardDialog(this, str_templetCode, new BillCardPanel(str_templetCode), WLTConstants.BILLDATAEDITSTATE_INIT);
			billCardDialog.setVisible(true);
		} else {
			try {
				AbstractTMO tmo = null;
				String savetype = billVO.getStringValue("savetype");
				if (savetype != null && !savetype.equals("") && savetype.indexOf("XML_") > 0) {
					tmo = UIUtil.getMetaDataService().getDefaultTMOByCode("/" + savetype.substring(savetype.indexOf("XML_") + 4), 3);
				} else {
					tmo = UIUtil.getMetaDataService().getDefaultTMOByCode(str_templetCode, 1);
				}
				BillCardDialog billCardDialog = new BillCardDialog(this, str_templetCode, new BillCardPanel(tmo), WLTConstants.BILLDATAEDITSTATE_INIT);
				billCardDialog.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public FrameWorkMetaDataServiceIfc getService() throws Exception {
		if (service == null) {
			service = (FrameWorkMetaDataServiceIfc) UIUtil.lookUpRemoteService(FrameWorkMetaDataServiceIfc.class);
		}
		return service;
	}

	public JTextField getSearch_text() {
		return search_text;
	}

	public void setSearch_text(JTextField search_text) {
		this.search_text = search_text;
	}

	public JTextField getSearch_table() {
		return search_table;
	}

	public void setSearch_table(JTextField search_table) {
		this.search_table = search_table;
	}

	public JComboBox getSearch_box() {
		return search_box;
	}

	public void setSearch_box(JComboBox search_box) {
		this.search_box = search_box;
	}

}
