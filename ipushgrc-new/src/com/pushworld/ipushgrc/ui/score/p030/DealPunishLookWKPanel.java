package com.pushworld.ipushgrc.ui.score.p030;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.report.ReportExportWord;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.score.ScoreUIUtil;
import com.pushworld.ipushgrc.ui.score.p020.RegisterEditWKPanel;

/**
 * Υ�����-��Υ��ͷ���ѯ�����/2015-01-28��
 * �б���ť�����ˡ�����Excel����ť
 * @author lcj
 *
 */
public class DealPunishLookWKPanel extends AbstractWorkPanel implements ActionListener {
	private WLTButton btn_print = new WLTButton("��ӡ֪ͨ��");
	BillListPanel listPanel = new BillListPanel("SCORE_USER_LCJ_Q07");
	private ScoreUIUtil scoreuiutil = new ScoreUIUtil();
	private WLTButton btn_export;

	@Override
	public void initialize() {
		btn_export = new WLTButton("��������Word");
		String curryear = TBUtil.getTBUtil().getCurrDate().substring(0, 4);
		listPanel.QueryDataByCondition("EFFECTDATE like '" + curryear + "%'");//��ѯ����ȵļ�¼
		listPanel.addBatchBillListButton(new WLTButton[] { btn_print, btn_export });
		listPanel.repaintBillListButton();
		btn_print.addActionListener(this);
		btn_export.addActionListener(this);
		this.setLayout(new BorderLayout());
		this.add(listPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_print) {
			BillVO vo = listPanel.getSelectedBillVO();
			if (vo == null) {
				MessageBox.showSelectOne(this);
				return;
			}
			scoreuiutil.viewAndPrintCFBYCellPanel(this, vo.getStringValue("id"));
		} else if (e.getSource() == btn_export) {
			onExport();
		}

	}

	private void onExport() {
		final BillVO exportVOs[] = listPanel.getSelectedBillVOs();
		if (exportVOs.length == 0) {
			MessageBox.show(this, "������ѡ��һ����¼.");
			return;
		}
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("��ѡ��Ҫ���浽��Ŀ¼,��������" + exportVOs.length + "���ļ�.");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		int result = fc.showSaveDialog(this);

		if (result != JFileChooser.APPROVE_OPTION) { //�������ȷ����
			return;
		}
		final String savePath = fc.getSelectedFile().getPath();
		new SplashWindow(this, "���ڵ���...", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				SplashWindow splash = (SplashWindow) e.getSource();
				try {
					BillCellVO cellvo = null;
					try {
						cellvo = UIUtil.getMetaDataService().getBillCellVO("Υ����ֳͷ�֪ͨ��ģ��", null, null);
					} catch (Exception ex) {
						WLTLogger.getLogger(RegisterEditWKPanel.class).error("û���ҵ�[Υ����ֳͷ�֪ͨ��ģ��]ģ��.");
					}
					try {
						if (cellvo == null) {
							BillListPanel listpanel = new BillListPanel(new cn.com.infostrategy.ui.report.cellcompent.CellTMO());
							listpanel.QueryDataByCondition(" templetcode like '%����%'");
							BillListDialog listdialog = new BillListDialog(DealPunishLookWKPanel.this, "��ѡ�����Ӧ��ģ��", listpanel);
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
							exportword.exportWordFile(cellvo_new, savePath, i + "_" + exportVOs[i].getStringViewValue("deptid") + "_" + exportVOs[i].getStringViewValue("USERID"));
						} catch (Exception exx) {

						}
						splash.setWaitInfo("��������:" + (i + 1) + "/" + count);
					}
				} catch (Exception ex) {
					splash.closeWindow();
					ex.printStackTrace();
				}
			}
		}, false);
		MessageBox.show(this, "�����ɹ�");
	}
}