package com.pushworld.ipushgrc.ui.keywordreplace;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class TempUpLoadPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private BillListPanel billList;
	WLTButton helpInfoBtn=null;
	@Override
	public void initialize() {
		billList=new BillListPanel("PRINT_TEMPLATE_CODE1");
		helpInfoBtn=new WLTButton("帮助");
		billList.addBillListButton(helpInfoBtn);
		helpInfoBtn.addActionListener(this);
		billList.repaintBillListButton();
		this.add(billList);
	}
	public void actionPerformed(ActionEvent e) {
		StringBuilder sb_info = new StringBuilder(); //
		sb_info.append("******************** 模板配置说明*****************\r\n"); //
		sb_info.append("用word编辑模板，另存为xml或mht文件\r\n"); //
		sb_info.append("1.如果要导出的数据只涉及到一个单据模板，步骤如下:\r\n");
		sb_info.append("  ①模板文件的编码必须使用该单据模板的编码\r\n");
		sb_info.append("  ②模板文件配置中的关键字必须使用『表名.Itemkey』\r\n");
		sb_info.append("  ③后台代码直接调用TemplateToWordUIUtil中的createWordByOneListPanel方法即可\r\n");
		sb_info.append("  例子:如果导出pub_user的name,假设模板文件名为\"用户.xml\"\r\n");
		sb_info.append("      a.\"用户.xml\"的编码必须为pub_user模板，比如PUB_USER_CODE1\r\n");
		sb_info.append("      b.在用户.xml中必须使用类似『pub_user.name』的关键字,注意这里的name是中的PUB_USER_CODE1的ItemKey\r\n");
		sb_info.append("      c.后台直接调用createWordByOneListPanel\r\n");
		sb_info.append("2.如果要导出的数据不只涉及到一个单据模板或数据自定义的情况，步骤如下:\r\n");
		sb_info.append("  ①要把数据封装在一个HashMap里，key应该与模板文件中的关键字对应，用特殊符号『』包围关键字\r\n"); //
		sb_info.append("  ②模板文件的编码自定义，在程序中要根据此编码找到模板文件\r\n");
		sb_info.append("  ③后台代码直接调用TemplateToWordUIUtil中的createWordByMap方法即可\r\n");
		sb_info.append("  具体例子可参照CmpScorePunishWKPanel中的onExportWord方法\r\n");
		MessageBox.show(this,sb_info.toString());
	}
}
