package cn.com.pushworld.zt.ui.other;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JFileChooser;

import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.report.ReportExportWord;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.pushworld.salary.ui.SalaryUIUtil;
import cn.com.pushworld.salary.ui.baseinfo.p010.UserDutiesWKPanel;

public class ExportWord extends AbstractWorkPanel implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -833479508380110724L;
	private WLTButton btn = new WLTButton("导出",UIUtil.getImage("word.jpg"));

	@Override
	public void initialize() {
		try {
			WLTTabbedPane tabPanel = new WLTTabbedPane();
			BillCellVO cellvo = null;
			try {
				cellvo = UIUtil.getMetaDataService().getBillCellVO("中投资产负债表_导出", null, null);
			} catch (Exception ex) {
				WLTLogger.getLogger(UserDutiesWKPanel.class).error("没有找到[员工简历]模版.");
			}
			if (cellvo == null) {
				BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
				listpanel.QueryDataByCondition(" templetcode like '%中投资产负债表_导出%'");
				BillListDialog listdialog = new BillListDialog(null, "请选择想对应的模版", listpanel);
				listdialog.setVisible(true);
				if (listdialog.getCloseType() != 1) {
					return;
				}
				BillVO rtvos[] = listdialog.getReturnBillVOs();
				if (rtvos.length > 0) {
					cellvo = UIUtil.getMetaDataService().getBillCellVO(rtvos[0].getStringValue("templetcode"), null, null);
				} else {
					return;
				}
			}
			BillCellVO cellvo_new = SalaryUIUtil.getService().parseCellTempetToWord(cellvo, null);
			BillCellPanel cellPanel = new BillCellPanel(cellvo_new);
			WLTPanel btnpanel = new WLTPanel(new FlowLayout(FlowLayout.LEFT));
			btn.addActionListener(this);
			btnpanel.add(btn);
			this.add(btnpanel, BorderLayout.NORTH);
			this.add(cellPanel, BorderLayout.CENTER);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("请选择要保存到的目录.");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		int result = fc.showSaveDialog(this);

		if (result != JFileChooser.APPROVE_OPTION) { //如果不是确定的
			return;
		}
		final String savePath = fc.getSelectedFile().getPath();
		new SplashWindow(this, "正在导出...", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				SplashWindow splash = (SplashWindow) e.getSource();
				try {
					BillCellVO cellvo = null;
					try {
						cellvo = UIUtil.getMetaDataService().getBillCellVO("中投资产负债表_导出", null, null);
					} catch (Exception ex) {
						WLTLogger.getLogger(UserDutiesWKPanel.class).error("没有找到[员工简历]模版.");
					}
					if (cellvo == null) {
						BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
						listpanel.QueryDataByCondition(" templetcode like '%中投资产负债表_导出%'");
						BillListDialog listdialog = new BillListDialog(ExportWord.this, "请选择想对应的模版", listpanel);
						listdialog.setVisible(true);
						if (listdialog.getCloseType() != 1) {
							return;
						}
						BillVO rtvos[] = listdialog.getReturnBillVOs();
						if (rtvos.length > 0) {
							cellvo = UIUtil.getMetaDataService().getBillCellVO(rtvos[0].getStringValue("templetcode"), null, null);
						} else {
							return;
						}
					}
					int count = 1;
					ReportExportWord exportword = new ReportExportWord();
					for (int i = 0; i < count; i++) {
						BillCellVO cellvo_new = SalaryUIUtil.getService().parseCellTempetToWord(cellvo, null);
						exportword.exportWordFile(cellvo_new, savePath, "中投资产负债表");
						splash.setWaitInfo("导出进度:" + (i + 1) + "/" + count);
					}
				} catch (Exception ex) {
					splash.closeWindow();
					ex.printStackTrace();
				}
			}
		}, false);

	}

}
