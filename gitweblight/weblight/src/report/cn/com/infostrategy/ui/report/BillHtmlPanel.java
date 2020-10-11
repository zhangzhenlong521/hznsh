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
 * ֧����ʾHTML��һ�����ؼ�!!
 * ʹ��JDIC����.
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
	private String importFileName = "C:\\exportData.html";//��ǰĬ��������ҵ���׼���set�����������ñ�����ļ�������ҵ�����

	public BillHtmlPanel() {
		this(true); //
	}

	public BillHtmlPanel(boolean _isCanExport) {
		this.setLayout(new BorderLayout(0, 0)); //
		this.setBorder(BorderFactory.createEmptyBorder()); //

		if (_isCanExport) { //������Ե���,����������ť!!
			JPanel panel_north = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1)); //
			btn_export = new WLTButton("����Html", UIUtil.getImage("blank.gif")); //
			btn_export.addActionListener(this); //
			panel_north.add(btn_export); //
			this.add(panel_north, BorderLayout.NORTH); //
		}
	}

	/**
	 * ����һ��HTML
	 * @param _classname,ʵ���ڷ�����������HTML������,������,����ʵ��cn.com.infostrategy.bs.common.WebCallBeanIfc �ӿ�
	 * @param _map �͸��������˵Ĳ���,����ʵ�����getHtmlContent(HashMap _map)�о��ܵõ�������!!
	 */
	public void loadhtml(final String _classname, final HashMap _map) {
		//		new SplashWindow(this, new AbstractAction() {
		//			public void actionPerformed(ActionEvent e) {
		doLoad(_classname, _map); //����������ݷ�ʽ��¼!
		//			}
		//		});
	}

	/**
	 * ֱ����һ��Htmlֱ����ʾ!
	 */
	public void loadHtml(String _html) {
		HashMap map = new HashMap(); //
		map.put("html", _html); //
		doLoad("cn.com.infostrategy.bs.common.DefaultWebCallBean", map); //
	}

	/**
	 * ֱ�Ӹ���һ��url��������
	 * @param _url
	 */
	public void loadWebContentByURL(java.net.URL _url) {
		try {
			webPanel = new WebBrowser(_url, false); //������Զ�̷���!!!
			JPanel panel_center = new JPanel(new BorderLayout()); //��Ҫ��webPanel�ŵ�jpanel��Ȼ���ٷŵ�billhtmlpanel�У�����ҳ���еĴ�ֱ���������ܹ�������ײ������а������ֱ������š����/2012-04-28��
			panel_center.add(webPanel, BorderLayout.CENTER);
			this.add(panel_center, BorderLayout.CENTER);//����������޸ģ�WebBrowser���治��Jscrollpane�����ˣ���������ҳ���λ�������/2012-03-05��
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
	}

	/**
	 * ȡ��WebCallID,Զ�̵���һ������,����һ��Ψһ�Ե�SessionID
	 */
	private String getWebCallId(String _classname, HashMap _map) throws Exception {
		parVO = new WebCallParVO(); //
		parVO.setCallClassName(_classname); //
		parVO.setParsMap(_map); //
		return getReportService().registerWebCallSessionID(parVO); //
	}

	private void doLoad(String _classname, HashMap _map) {
		try {
			parVO = new WebCallParVO(); //�ȹ�������
			parVO.setCallClassName(_classname); //����
			parVO.setParsMap(_map); //����
			str_htmlcontent = getReportService().getHtmlContent(parVO); //��һ��Զ�̵���,�����Html����,������̿��ܷǳ�����!!
			String str_htmlcontentid = getReportService().registerHtmlContentSessionID(str_htmlcontent); //�ڶ���Զ�̷���,��Htmlע�᷵��һ��SessionID!
			String str_url = System.getProperty("CALLURL") + "/WebCallServlet?htmlcontentid=" + str_htmlcontentid; //
			if(webPanel!=null){ //����Ѿ����ع�ҳ�棬ֱ���ڵ�ǰwebBrowser�д�[2012-05-12����]
				webPanel.setURL(new java.net.URL(str_url));
			}else{
				webPanel = new WebBrowser(new java.net.URL(str_url), false); //������Զ�̷���!!!
				webPanel.setMinimumSize(new Dimension(100,100)); //���WebBrowser����WLTSplitPane�ָ����ƶ� �����/2013-01-09��
				JPanel panel_center = new JPanel(new BorderLayout()); ////��Ҫ��webPanel�ŵ�jpanel��Ȼ���ٷŵ�billhtmlpanel�У�����ҳ���еĴ�ֱ���������ܹ�������ײ������а������ֱ������š����/2012-04-28��
				panel_center.add(webPanel, BorderLayout.CENTER);
				this.add(panel_center, BorderLayout.CENTER); //����������޸ģ�WebBrowser���治��Jscrollpane�����ˣ���������ҳ���λ�������/2012-03-05��
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
	 * �ͷ���Դ,���һ�������а���WebBrowser,�����رոô���ʱ��Ҫ��ոÿؼ�,�������ְ�������!!
	 */
	public void disPose() {
		if (webPanel != null) {
			try {
				webPanel.stop(); //
				System.out.println("Dispose WebBrowse�ˡ�����"); //
				webPanel.dispose(); //�ֶ��ͷ�,��ǰ�ϱ���,���°汾��Ȼ���ڹ���ʱʹ��autoDispose=false���Ϳ�����!
				webPanel = null; //
				this.removeAll(); //��һ�к������Ҫ��������򿪵�Frame��������ܱ�GC����...
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
	 * �õ���ҳ����!!!
	 * @return
	 */
	public String getHtmlcontent() {
		return str_htmlcontent;
	}

	private void onExport() {
		if (str_htmlcontent == null) {
			MessageBox.show(this, "��û�м�������,��������������!"); //
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
				if (!(fileName.endsWith(".html") || fileName.endsWith(".htm"))) {//��ҵ����
					curFile = new File(curFile.getAbsolutePath() + ".html");
				}
				if (curFile != null) {
					FileOutputStream fileOut = new FileOutputStream(curFile, false); //
					fileOut.write(str_htmlcontent.getBytes("GBK")); //
					fileOut.close(); //
					String str_filename = curFile.getAbsolutePath(); //
					if (JOptionPane.showConfirmDialog(this, "�������ݳɹ�!!�ļ�·����[" + str_filename + "],���Ƿ��������򿪸��ļ�?", "��ʾ", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION) {
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
