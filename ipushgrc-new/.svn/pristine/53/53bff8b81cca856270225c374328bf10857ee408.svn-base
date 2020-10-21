package com.pushworld.ipushgrc.ui.wfrisk.p010;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;

import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;

/**
 * 这个类是把Visio的SVG文件转换成普信流程图的工具类。里面解析SVG文件逻辑很复杂。还需优化！ 框如果窄，文字换行看不到了。。
 * @author haoming
 */
public class ImportSVGUtil implements Serializable {
	public void init(String cmpfileid, String cmpfilecode, WFGraphEditPanel wfGraphPanel) throws Exception {
		//选择文件、读取文件内容
		JFileChooser fc = new JFileChooser(getUploadDir());
		FileFilter filter = new FileNameExtensionFilter("可缩放的向量图像(*.svg)", "svg");
		fc.setFileFilter(filter);
		int choice = fc.showOpenDialog(wfGraphPanel);
		if (choice == JFileChooser.APPROVE_OPTION && fc.getSelectedFile() != null) {
			setUploadDir(fc.getSelectedFile().getParent());
			String process[] = null;
			File f = fc.getSelectedFile();
			try {
				IPushGRCServiceIfc server = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class);
				String str = new String(new TBUtil().readFromInputStreamToBytes(new FileInputStream(f)), "UTF-8");
				process = server.ImportVisioToProcess(cmpfileid, cmpfilecode, str);
			} catch (Exception ex) {
				MessageBox.showException(wfGraphPanel, ex);
				return;
			}
			if (process == null) {
				MessageBox.show(wfGraphPanel, "导入失败！");
				return;
			}
			wfGraphPanel.onAddAndSelectRadioBtn(process[0], process[1], process[2], true, false);
			MessageBox.show(wfGraphPanel, "导入成功！");
		}
	}

	/**
	 * 获得导入visio路径需记忆
	 * @return
	 */
	private String getUploadDir() {
		Object o = ClientEnvironment.getInstance().get("Visio导入路径");
		if (o == null) {
			return "C:\\";
		} else {
			return o.toString();
		}
	}

	private void setUploadDir(String uploadDir) {
		ClientEnvironment.getInstance().put("Visio导入路径", uploadDir);
	}
}
