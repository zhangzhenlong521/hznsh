package com.pushworld.ipushgrc.ui.law.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class ExportProcessWordTools extends AbstractWorkPanel implements ActionListener {
	private BillListPanel billListPanel;
	private WLTButton removelist;
	private WLTButton exportword;

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		billListPanel = new BillListPanel("CMP_CMPFILE_CODE5_1");
		removelist = new WLTButton("移除");
		removelist.addActionListener(this);
		exportword = new WLTButton("导出流程文件", "word.jpg");
		exportword.addActionListener(this);
		billListPanel.addBatchBillListButton(new WLTButton[] { removelist, exportword });
		billListPanel.repaintBillListButton();
		billListPanel.setVisible(true);
		this.add(billListPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == removelist) {
			int[] selectrows = billListPanel.getSelectedRows();
			if (selectrows == null || selectrows.length < 1) {
				MessageBox.showSelectOne(this);
				return;
			}
			billListPanel.removeSelectedRows();
		} else if (e.getSource() == exportword) {
			try {
				exportCMPFileAsDoc();
			} catch (Exception e1) {
				MessageBox.show("导出失败");
				e1.printStackTrace();
			}
		}
	}

	private void exportCMPFileAsDoc() throws Exception {
		final BillVO billVOs[] = billListPanel.getAllBillVOs();
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("请选择存放目录");
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int flag = chooser.showSaveDialog(this);
		if (flag == 1 || chooser.getSelectedFile() == null) {
			return;
		}
		final String str_path = chooser.getSelectedFile().getAbsolutePath(); //
		if (str_path == null) {
			return;
		}
		SplashWindow _splash = new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < billVOs.length; i++) {
					try {
						exportCMPFileAsDocFile((SplashWindow) e.getSource(), i,billVOs.length, billVOs[i].getStringValue("id"), billVOs[i].getStringValue("cmpfilename"), billVOs[i].getStringValue("versionno"), str_path);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}, 600, 130, 300, 300, false);
		MessageBox.show("导出成功");
	}

	private void exportCMPFileAsDocFile(SplashWindow splash, int _num, int sum ,String cmpFileID, String cmpFileName, String version, String path) throws Exception {
		TBUtil tbUtil = new TBUtil();
		UIUtil util = new UIUtil();
		String sql = "select * from cmp_cmpfile_histcontent " + "where cmpfile_id='" + cmpFileID + "' " + "and cmpfile_versionno = '" + version + "' " + "and contentname='DOC' " + "order by seq";
		HashVO[] hvs = util.getHashVoArrayByDS(null, sql);
		if (hvs.length < 1) {
			return;
		}
		StringBuilder sb_doc = new StringBuilder();
		String str_itemValue = null;
		for (int i = 0; i < hvs.length; i++) {
			for (int j = 0; j < 10; j++) {
				str_itemValue = hvs[i].getStringValue("doc" + j);
				if (str_itemValue == null || str_itemValue.trim().equals("")) {
					break;
				} else {
					sb_doc.append(str_itemValue.trim()); //拼接!!!
				}
			}
		}
		String str_64code = sb_doc.toString();
		byte[] bytes = tbUtil.convert64CodeToBytes(str_64code);
		byte[] unZipedBytes = tbUtil.decompressBytes(bytes); //解压!!这就是Word的实际内容,用于输出!!!

		String fileName = path+"\\" + cmpFileName + "_V" + version + ".doc";
		new TBUtil().writeBytesToOutputStream(new FileOutputStream(new File(fileName), false), unZipedBytes);
		splash.setWaitInfo("成功生成第" + _num + "个文件【" + cmpFileName + "_V" + version + "】 共有"+sum+"个文件");
	}
}
