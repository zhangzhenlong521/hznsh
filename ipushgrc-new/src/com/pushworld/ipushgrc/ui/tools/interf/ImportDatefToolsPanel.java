package com.pushworld.ipushgrc.ui.tools.interf;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;

/**
 * 接口需要数据导出工具
 * @author z
 *
 */
public class ImportDatefToolsPanel extends AbstractWorkPanel implements ActionListener {

	WLTButton import_btn = new WLTButton("执行数据交换");

	@Override
	public void initialize() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		WLTLabel seq_lab1 = new WLTLabel();
		seq_lab1.setText("将系统中的数据导入到流程银行平台数据库中");
		this.add(seq_lab1);
		this.add(import_btn);
		import_btn.addActionListener(this);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == import_btn) {
			importDFDatas();
		}
	}

	private void importDFDatas() {
		try {
			IPushGRCServiceIfc service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
			service.importDFDatas();
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.showException(this, e);
			return;
		}
		MessageBox.show(this, "数据交换成功!");
	}

}
