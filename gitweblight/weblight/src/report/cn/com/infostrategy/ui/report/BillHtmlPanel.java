package cn.com.infostrategy.ui.report;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.jdesktop.jdic.browser.WebBrowser;

import cn.com.infostrategy.to.common.WebCallParVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.RemoteServiceFactory;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 支持显示HTML的一个面板控件!!
 * 使用JDIC技术.
 * @author xch
 *
 */
public class BillHtmlPanel extends BillPanel implements ActionListener {

	private static final long serialVersionUID = -4701383323887475535L;

	private WLTButton btn_export = null; //
	public WebBrowser webPanel = null;

	private ReportServiceIfc reportService; // 

	private WebCallParVO parVO = null;
	private String str_htmlcontent = null; //
	private String importFileName = "C:\\exportData.html";//以前默认名称兴业不妥加了set方法可以设置保存的文件名（兴业提出）

	public BillHtmlPanel() {
		this(true); //
	}

	public BillHtmlPanel(boolean _isCanExport) {
		this.setLayout(new BorderLayout(0, 0)); //
		this.setBorder(BorderFactory.createEmptyBorder()); //

		if (_isCanExport) { //如果可以导出,即带导出按钮!!
			JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1)); //
			btn_export = new WLTButton("导出Html", UIUtil.getImage("blank.gif")); //
			btn_export.addActionListener(this); //
			panel_north.add(btn_export); //
			this.add(panel_north, BorderLayout.NORTH); //
		}
	}

	/**
	 * 加载一个HTML
	 * @param _classname,实际在服务器端生成HTML的类名,反射用,必须实现cn.com.infostrategy.bs.common.WebCallBeanIfc 接口
	 * @param _map 送给服务器端的参数,即从实现类的getHtmlContent(HashMap _map)中就能得到该数据!!
	 */
	public void loadhtml(final String _classname, final HashMap _map) {
		//		new SplashWindow(this, new AbstractAction() {
		//			public void actionPerformed(ActionEvent e) {
		doLoad(_classname, _map); //如果是桌面快捷方式登录!
		//			}
		//		});
	}

	/**
	 * 直接送一段Html直接显示!
	 */
	public void loadHtml(String _html) {
		HashMap map = new HashMap(); //
		map.put("html", _html); //
		doLoad("cn.com.infostrategy.bs.common.DefaultWebCallBean", map); //
	}

	/**
	 * 直接根据一个url加载内容
	 * @param _url
	 */
	public void loadWebContentByURL(java.net.URL _url) {
		try {
			webPanel = new WebBrowser(_url, false); //第三次远程访问!!!
			JPanel panel_center = new JPanel(new BorderLayout()); //需要将webPanel放到jpanel中然后再放到billhtmlpanel中，否则页面中的垂直滚动条不能滚动到最底部，总有半行文字被覆盖着【李春娟/2012-04-28】
			panel_center.add(webPanel, BorderLayout.CENTER);
			this.add(panel_center, BorderLayout.CENTER);//这里进行了修改，WebBrowser外面不用Jscrollpane包裹了，否则会出现页面错位。【李春娟/2012-03-05】
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * 取得WebCallID,远程调用一个服务,生成一个唯一性的SessionID
	 */
	private String getWebCallId(String _classname, HashMap _map) throws Exception {
		parVO = new WebCallParVO(); //
		parVO.setCallClassName(_classname); //
		parVO.setParsMap(_map); //
		return getReportService().registerWebCallSessionID(parVO); //
	}

	private void doLoad(String _classname, HashMap _map) {
		try {
			parVO = new WebCallParVO(); //先构建参数
			parVO.setCallClassName(_classname); //设置
			parVO.setParsMap(_map); //设置
			str_htmlcontent = getReportService().getHtmlContent(parVO); //第一次远程调用,先算出Html内容,这个过程可能非常缓慢!!
			String str_htmlcontentid = getReportService().registerHtmlContentSessionID(str_htmlcontent); //第二次远程访问,将Html注册返回一个SessionID!
			String str_url = System.getProperty("CALLURL") + "/WebCallServlet?htmlcontentid=" + str_htmlcontentid; //
			if(webPanel!=null){ //如果已经加载过页面，直接在当前webBrowser中打开[2012-05-12郝明]
				webPanel.setURL(new java.net.URL(str_url));
			}else{
				webPanel = new WebBrowser(new java.net.URL(str_url), false); //第三次远程访问!!!
				webPanel.setMinimumSize(new Dimension(100,100)); //解决WebBrowser不随WLTSplitPane分隔条移动 【杨科/2013-01-09】
				JPanel panel_center = new JPanel(new BorderLayout()); ////需要将webPanel放到jpanel中然后再放到billhtmlpanel中，否则页面中的垂直滚动条不能滚动到最底部，总有半行文字被覆盖着【李春娟/2012-04-28】
				panel_center.add(webPanel, BorderLayout.CENTER);
				this.add(panel_center, BorderLayout.CENTER); //这里进行了修改，WebBrowser外面不用Jscrollpane包裹了，否则会出现页面错位。【李春娟/2012-03-05】
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	private ReportServiceIfc getReportService() throws Exception {
		if (reportService != null) {
			return reportService;
		}
		reportService = (ReportServiceIfc) RemoteServiceFactory.getInstance().lookUpService(ReportServiceIfc.class);
		return reportService;
	}

	/**
	 * 释放资源,如果一个窗口中包含WebBrowser,则好象关闭该窗口时需要清空该控件,否则会出现白屏现象!!
	 */
	public void disPose() {
		if (webPanel != null) {
			try {
				webPanel.stop(); //
				System.out.println("Dispose WebBrowse了。。。"); //
				webPanel.dispose(); //手动释放,以前老报错,用新版本后，然后在构造时使用autoDispose=false，就可以了!
				webPanel = null; //
				this.removeAll(); //这一行好象必须要做，否则打开的Frame窗口死活不能被GC回收...
			} catch (Exception e) {
				e.printStackTrace(); //
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_export) {
			onExport(); //
		}
	}

	/**
	 * 得到网页内容!!!
	 * @return
	 */
	public String getHtmlcontent() {
		return str_htmlcontent;
	}

	private void onExport() {
		if (str_htmlcontent == null) {
			MessageBox.show(this, "还没有加载数据,不能做导出操作!"); //
			return;
		}

		try {
			JFileChooser chooser = new JFileChooser();
			chooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
				public boolean accept(File file) {
					String filename = file.getName();
					return file.isDirectory() || filename.endsWith(".html");
				}

				public String getDescription() {
					return "*.html";
				}
			});

			try {
				File f = new File(new File(importFileName).getCanonicalPath());
				chooser.setSelectedFile(f);
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}

			int li_rewult = chooser.showSaveDialog(this);
			if (li_rewult == JFileChooser.APPROVE_OPTION) {
				File curFile = chooser.getSelectedFile(); //
				String fileName = curFile.getName();
				if (!(fileName.endsWith(".html") || fileName.endsWith(".htm"))) {//兴业需求
					curFile = new File(curFile.getAbsolutePath() + ".html");
				}
				if (curFile != null) {
					FileOutputStream fileOut = new FileOutputStream(curFile, false); //
					fileOut.write(str_htmlcontent.getBytes("GBK")); //
					fileOut.close(); //
					String str_filename = curFile.getAbsolutePath(); //
					if (JOptionPane.showConfirmDialog(this, "导出数据成功!!文件路径是[" + str_filename + "],你是否想立即打开该文件?", "提示", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION) {
						Runtime.getRuntime().exec("explorer.exe \"" + str_filename + "\""); //
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String getImportFileName() {
		return importFileName;
	}

	public void setImportFileName(String importFileName) {
		this.importFileName = importFileName;
	}

}
