package cn.com.pushworld.salary.ui.baseinfo.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.report.ReportExportWord;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * 履职信息维护
 * @author haoming
 * create by 2013-10-16
 */
public class UserDutiesWKPanel extends AbstractWorkPanel implements BillListSelectListener, ActionListener {
	private static final long serialVersionUID = 1348852015137460855L;
	private BillListPanel personlistPanel = new BillListPanel("SAL_PERSONINFO_CODE1_DUTY");
	private BillListPanel dutieslistPanel = new BillListPanel("SAL_PERSONDUTIES_HIS_CODE1");
	private WLTButton btn_insert, btn_delete, btn_update;
	private WLTButton btn_view, btn_export_word;

	@Override
	public void initialize() {
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		btn_insert.addActionListener(this);
		dutieslistPanel.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete });
		dutieslistPanel.repaintBillListButton();
		personlistPanel.addBillListSelectListener(this);

		btn_view = new WLTButton("简历预览", UIUtil.getImage("eye.png"));
		btn_view.addActionListener(this);
		btn_export_word = new WLTButton("简历导出", UIUtil.getImage("word.jpg"));
		btn_export_word.addActionListener(this);
		personlistPanel.addBatchBillListButton(new WLTButton[] { btn_view, btn_export_word });
		personlistPanel.repaintBillListButton();
		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, personlistPanel, dutieslistPanel);
		split.setDividerLocation(410);
		this.add(split);
		personlistPanel.QueryData("select t1.* from v_sal_personinfo t1  order by t1.deptseq,t1.postseq ");
	}

	public void onBillListSelectChanged(BillListSelectionEvent event) {
		BillVO billvo = event.getCurrSelectedVO();
		if (billvo == null) {
			return;
		}
		dutieslistPanel.QueryDataByCondition(" userid = " + billvo.getPkValue());
	}

	public void actionPerformed(ActionEvent actionevent) {
		if (actionevent.getSource() == btn_insert) {
			onAdd();
		} else if (actionevent.getSource() == btn_view) {
			onView();
		} else if (actionevent.getSource() == btn_export_word) {
			onExport();
		}
	}

	private void onAdd() {
		BillVO selectvo = personlistPanel.getSelectedBillVO();
		if (selectvo == null) {
			MessageBox.showSelectOne(personlistPanel);
			return;
		}
		HashMap map = new HashMap();
		String id = selectvo.getStringValue("id");
		String name = selectvo.getStringValue("name");
		map.put("userid", new StringItemVO(id));
		map.put("username", new StringItemVO(name));
		dutieslistPanel.doInsert(map);
	}

	public void onView() {
		BillVO exportVOs[] = personlistPanel.getSelectedBillVOs();
		if (exportVOs == null || exportVOs.length == 0 || exportVOs.length > 10) {
			MessageBox.show(personlistPanel, "请选择1-10条数据,再就行预览");
			return;
		}
		try {
			WLTTabbedPane tabPanel = new WLTTabbedPane();
			BillCellVO cellvo = null;
			try {
				cellvo = UIUtil.getMetaDataService().getBillCellVO("员工简历", null, null);
			} catch (Exception ex) {
				WLTLogger.getLogger(UserDutiesWKPanel.class).error("没有找到[员工简历]模版.");
			}
			if (cellvo == null) {
				BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
				listpanel.QueryDataByCondition(" templetcode like '%简%'");
				BillListDialog listdialog = new BillListDialog(personlistPanel, "请选择想对应的模版", listpanel);
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
			BillDialog dialog = new BillDialog(personlistPanel, "预览", 1000, 800);
			if (exportVOs.length == 1) {
				HashVO hvo = exportVOs[0].convertToHashVO();
				hvo.setAttributeValue("checkeduser", hvo.getStringValue("id"));
				BillCellVO cellvo_new = SalaryUIUtil.getService().parseCellTempetToWord(cellvo, hvo);
				BillCellPanel cellPanel = new BillCellPanel(cellvo_new);
				dialog.add(cellPanel);
				dialog.setVisible(true);
			} else {
				for (int i = 0; i < exportVOs.length; i++) {
					HashVO hvo = exportVOs[i].convertToHashVO();
					hvo.setAttributeValue("checkeduser", hvo.getStringValue("id"));
					BillCellVO cellvo_new = SalaryUIUtil.getService().parseCellTempetToWord(cellvo, hvo);
					BillCellPanel cellPanel = new BillCellPanel(cellvo_new);
					tabPanel.addTab(exportVOs[i].getStringValue("name"), cellPanel);
				}
				dialog.add(tabPanel);
				dialog.setVisible(true);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * @param _exportType 0是全部导出,1是选择性导出
	 */
	public void onExport() {
		int index = MessageBox.showOptionDialog(personlistPanel, "请选择导出方式", "提示", new String[] { "导出选择行", "导出所有行" });
		if (index < 0) {
			return;
		}
		onExportWord(index);
	}

	private BillVO exportVOs[];

	public void onExportWord(int _exportType) {
		exportVOs = null;
		if (_exportType == 0) {
			exportVOs = personlistPanel.getSelectedBillVOs();
		} else if (_exportType == 1) {
			exportVOs = personlistPanel.getAllBillVOs();
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
		new SplashWindow(personlistPanel, "正在导出...", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				SplashWindow splash = (SplashWindow) e.getSource();
				try {
					BillCellVO cellvo = null;
					try {
						cellvo = UIUtil.getMetaDataService().getBillCellVO("员工简历", null, null);
					} catch (Exception ex) {
						WLTLogger.getLogger(UserDutiesWKPanel.class).error("没有找到[员工简历]模版.");
					}
					if (cellvo == null) {
						BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
						listpanel.QueryDataByCondition(" templetcode like '%简%'");
						BillListDialog listdialog = new BillListDialog(personlistPanel, "请选择想对应的模版", listpanel);
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
					int count = exportVOs.length;
					ReportExportWord exportword = new ReportExportWord();
					for (int i = 0; i < count; i++) {
						BillCellVO cellvo_new = SalaryUIUtil.getService().parseCellTempetToWord(cellvo, exportVOs[i].convertToHashVO());
						exportword.exportWordFile(cellvo_new, savePath, exportVOs[i].getStringValue("name"));
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
