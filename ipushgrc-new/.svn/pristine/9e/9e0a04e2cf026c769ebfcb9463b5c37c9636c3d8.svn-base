package com.pushworld.ipushgrc.ui.score.p050;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.report.ReportExportWord;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;

import com.pushworld.ipushgrc.ui.score.ScoreUIUtil;
import com.pushworld.ipushgrc.ui.score.p020.RegisterEditWKPanel;

/**
 * 违规积分减免查询
 * @author haoming
 * create by 2015-1-9
 */
public class ReduceApplyQueryPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private BillListPanel listPanel = new BillListPanel("SCORE_REDUCE_ZYC_E02");
	private WLTButton btn_view, btn_export, btn_print;
	private ScoreUIUtil scoreuiutil = new ScoreUIUtil();

	public void initialize() {
		btn_view = new WLTButton("快速预览");
		btn_print = new WLTButton("打印预览","office_013.gif");
		btn_export = new WLTButton("批量导出Word");
		listPanel.addBatchBillListButton(new WLTButton[] { btn_view, btn_print, btn_export });
		listPanel.repaintBillListButton();
		btn_view.addActionListener(this);
		btn_export.addActionListener(this);
		btn_print.addActionListener(this);
		this.add(listPanel);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_view) {
			onView();
		} else if (e.getSource() == btn_export) {
			onExport();
		} else if (e.getSource() == btn_print) {
			onPrint();
		}
	}

	/**
	 * 预览
	 */
	private void onView() {
		final BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCellVO cellvo = null;
		try {
			cellvo = UIUtil.getMetaDataService().getBillCellVO("违规积分减免模板", null, null);
		} catch (Exception ex) {
			WLTLogger.getLogger(RegisterEditWKPanel.class).error("没有找到[违规积分减免模板]模版.");
		}
		try {
			if (cellvo == null) {
				BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
				listpanel.QueryDataByCondition(" templetcode like '%减免%'");
				BillListDialog listdialog = new BillListDialog(this, "请选择想对应的模版", listpanel);
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
			TBUtil tbUtil = new TBUtil();
			final BillDialog dialog = new BillDialog(this, "预览", 800, 800);
			BillVO vo = listPanel.getSelectedBillVO();

			HashVO hvo = new HashVO();
			String keys[] = vo.getKeys();
			String names[] = vo.getNames();
			for (int i = 0; i < keys.length; i++) {
				hvo.setAttributeValue(names[i].trim(), vo.getStringViewValue(keys[i]));
				hvo.setAttributeValue(keys[i], vo.getStringViewValue(keys[i]));
			}
			Pub_Templet_1VO templet_1VO = UIUtil.getPub_Templet_1VO("SCORE_USER_LCJ_E01");//子模板
			List filelist = new ArrayList();
			ReportExportWord word = new ReportExportWord();
			BillCellVO cellvo_new = UIUtil.getCommonService().parseCellTempetToWord(cellvo, hvo);
			BillCellPanel cellPanel = new BillCellPanel(cellvo_new,true,false);
			dialog.add(cellPanel, BorderLayout.CENTER);
			JPanel southpanel = new JPanel(new FlowLayout());//dialog下方的按钮面板
			WLTButton btn_close = new WLTButton("关闭"); //
			btn_close.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dialog.setCloseType(BillDialog.CANCEL);
					dialog.dispose(); //
				}
			});
			southpanel.add(btn_close);
			dialog.add(southpanel, BorderLayout.SOUTH);
			dialog.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void onExport() {
		final BillVO exportVOs[] = listPanel.getSelectedBillVOs();
		if (exportVOs.length == 0) {
			MessageBox.show(this, "请至少选择一条记录.");
			return;
		}
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("请选择要保存到的目录,本次生存" + exportVOs.length + "个文件.");
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
						cellvo = UIUtil.getMetaDataService().getBillCellVO("违规积分减免模板", null, null);
					} catch (Exception ex) {
						WLTLogger.getLogger(RegisterEditWKPanel.class).error("没有找到[违规积分减免模板]模版.");
					}
					try {
						if (cellvo == null) {
							BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
							listpanel.QueryDataByCondition(" templetcode like '%减免%'");
							BillListDialog listdialog = new BillListDialog(ReduceApplyQueryPanel.this, "请选择想对应的模版", listpanel);
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
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					int count = exportVOs.length;
					ReportExportWord exportword = new ReportExportWord();
					for (int i = 0; i < count; i++) {
						HashVO hvo = new HashVO();
						String keys[] = exportVOs[i].getKeys();
						String names[] = exportVOs[i].getNames();
						for (int j = 0; j < keys.length; j++) {
							hvo.setAttributeValue(names[j].trim(), exportVOs[i].getStringViewValue(keys[j]));
							hvo.setAttributeValue(keys[j], exportVOs[i].getStringViewValue(keys[j]));
						}

						BillCellVO cellvo_new = UIUtil.getCommonService().parseCellTempetToWord(cellvo, hvo);
						try {
							exportword.exportWordFile(cellvo_new, savePath, i + "_" + exportVOs[i].getStringViewValue("CORPID") + "_" + exportVOs[i].getStringViewValue("USERID"));
						} catch (Exception exx) {

						}
						splash.setWaitInfo("导出进度:" + (i + 1) + "/" + count);
					}
				} catch (Exception ex) {
					splash.closeWindow();
					ex.printStackTrace();
				}
			}
		}, false);
		MessageBox.show(this, "导出成功");
	}

	private void onPrint() {
		BillVO vo = listPanel.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		scoreuiutil.viewAndPrintJMBYCellPanel(this, vo.getPkValue());
	}
}
