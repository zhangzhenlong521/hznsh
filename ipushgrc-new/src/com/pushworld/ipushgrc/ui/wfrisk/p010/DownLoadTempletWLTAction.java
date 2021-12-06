package com.pushworld.ipushgrc.ui.wfrisk.p010;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;

import cn.com.infostrategy.to.common.ClassFileVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.FrameWorkCommServiceIfc;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

public class DownLoadTempletWLTAction implements WLTActionListener {
	BillCardPanel cardPanel = null;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		BillVO billVO = cardPanel.getBillVO();
		String cmpfiletype = billVO.getStringValue("cmpfiletype");
		if (cmpfiletype == null || "".equals(cmpfiletype)) {
			MessageBox.show(cardPanel, "请先选择一个文件分类!");
			return;
		}
		FrameWorkCommServiceIfc service = (FrameWorkCommServiceIfc) UIUtil.lookUpRemoteService(FrameWorkCommServiceIfc.class);
		String cmpfilename = "cmpfile_templet" + cmpfiletype + ".doc";
		String filepath = service.getServerFile(cmpfilename);
		ClassFileVO filevo = UIUtil.downloadToClientByAbsolutePath(filepath);

		JFileChooser chooser = new JFileChooser();
		chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return true;
				} else {
					String filename = file.getName();
					return filename.endsWith(".doc");
				}
			}

			public String getDescription() {
				return "*.doc";
			}
		});
		File file = new File(new File("C:\\" + cmpfilename).getCanonicalPath());
		chooser.setSelectedFile(file);
		int li_rewult = chooser.showSaveDialog(cardPanel);
		if (li_rewult == JFileChooser.APPROVE_OPTION) {
			File curFile = chooser.getSelectedFile(); //
			if (curFile != null) {
				curFile.createNewFile();
				FileOutputStream out = new FileOutputStream(curFile, false);
				out.write(filevo.getByteCodes());
				out.close();
				MessageBox.show(cardPanel, curFile.getName() + "导出成功！");
			}
		}
	}
}