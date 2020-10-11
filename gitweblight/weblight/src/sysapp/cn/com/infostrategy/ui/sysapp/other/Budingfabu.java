package cn.com.infostrategy.ui.sysapp.other;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.mdata.HFlowLayoutPanel;
import cn.com.infostrategy.ui.mdata.VFlowLayoutPanel;

public class Budingfabu extends AbstractWorkPanel {

	@Override
	public void initialize() {
          this.setLayout(new BorderLayout());
				
				WLTLabel wltl = new WLTLabel("SQL补丁更新");
				wltl.setPreferredSize(new Dimension(100,20));
				wltl.setBorder(BorderFactory.createEtchedBorder());
				WLTButton daoru = new WLTButton("导入");
				WLTLabel wltl2 = new WLTLabel("JAR补丁更新");
				wltl2.setPreferredSize(new Dimension(100,20));
				wltl2.setBorder(BorderFactory.createEtchedBorder());
				WLTButton daoru2 = new WLTButton("导入");
				final VFlowLayoutPanel bp = new VFlowLayoutPanel(new JComponent[]{new HFlowLayoutPanel(new JComponent[]{wltl,daoru}),new HFlowLayoutPanel(new JComponent[]{wltl2,daoru2})});
				bp.setLayout(new FlowLayout(FlowLayout.LEFT));
				daoru.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						JFileChooser chooser = new JFileChooser();
						chooser.setFileFilter(new FileFilter() {
							public boolean accept(File f) {
								if (f.getAbsolutePath().endsWith(".push") || f.isDirectory()) {
									return true;
								}
								return false;
							}

							public String getDescription() {
								return "需要导入的.push类型文件";
							}
						});
						int result = chooser.showOpenDialog(bp); //防止在最右下角弹出!
						if (result == 0 && chooser.getSelectedFile() != null) {
							File allChooseFiles = chooser.getSelectedFile(); //
							if (!allChooseFiles.getName().endsWith(".push")) {
								MessageBox.showInfo(bp, "请选择.push类型文件");
								return;
							}
							try {
								UIUtil.executeBatchByDSNoLog(null, getSecretString(chooser.getSelectedFile()));
							} catch (Exception e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
								MessageBox.showInfo(bp, "执行失败");
								return;
							}
							MessageBox.showInfo(bp, "执行成功");
						} else {
							return;
						}
					}

				});
				this.add(bp);
	}
	private String getSecretString(File f) {
		StringBuffer sb = new StringBuffer();
		FileReader noteOpenReader = null;
		BufferedReader noteOpenBufferd = null;
		String noteString = null;
		try {
			noteOpenReader = new FileReader(f);
			noteOpenBufferd = new BufferedReader(noteOpenReader);
			while ((noteString = noteOpenBufferd.readLine()) != null) {
				sb.append(noteString);
			}
			return sb.toString();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				if (noteOpenReader != null)
					noteOpenReader.close();
				if (noteOpenBufferd != null)
					noteOpenBufferd.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";

	}
}
