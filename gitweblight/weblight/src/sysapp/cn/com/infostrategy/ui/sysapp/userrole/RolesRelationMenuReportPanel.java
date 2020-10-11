package cn.com.infostrategy.ui.sysapp.userrole;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;

/**
 * ��ɫ�͹��ܵ�ͳ��
 * @author haoming
 * create by 2014-12-17
 */
public class RolesRelationMenuReportPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	BillCellPanel cellpanel = null;

	@Override
	public void initialize() {
		WLTPanel btnpanel = new WLTPanel(new FlowLayout(FlowLayout.LEFT));
		WLTButton btn_export = new WLTButton("����Excel");
		btn_export.addActionListener(this);
		btnpanel.add(btn_export);
		this.add(btnpanel, BorderLayout.NORTH);
		cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc sysService;
		try {
			sysService = (cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc) UIUtil.lookUpRemoteService(cn.com.infostrategy.ui.sysapp.login.SysAppServiceIfc.class);
			cellpanel = new BillCellPanel(sysService.getRolesAndMenuRelation());
			cellpanel.setLockedCell(1, 2);
			this.add(cellpanel, BorderLayout.CENTER);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void actionPerformed(ActionEvent e) {
		if (cellpanel != null) {
			cellpanel.exportExcel("��ɫ�͹��ܵ�ͳ��");
		}
	}

}
