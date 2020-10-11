package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JPanel;

import org.jdesktop.jdic.browser.WebBrowserEvent;
import org.jdesktop.jdic.browser.WebBrowserListener;

import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.WLTButton;

/**
 * Office窗口
 * 
 * @author xch
 * 
 */
public class BillOfficeDialog extends BillDialog implements ActionListener {

	private WLTButton btn_close;
	private int closeType = -1;
	private BillOfficePanel billOfficePanel = null;
	private boolean issave = false;
	private boolean editable = false; //是否可编辑!
	private boolean printable = true; //是否可打印!
	private boolean titlebar = false; //是否显示菜单!
	private boolean ifshowsave = true;//是否显示保存
	private boolean ifshowprint_tao = true;//是否显示套打
	private boolean ifshowprint_all = true;//是否显示全打
	private boolean ifshowprint_fen = true;//是否显示分打
	private boolean ifshowprint = true;//是否显示打印
	private boolean ifshowwater = true;//是否显示水印
	private boolean ifshowshowcomment = true;//是否显示显示批注
	private boolean ifshowhidecomment = true;//隐藏批注
	private boolean ifshowedit = true;//修订
	private boolean ifshowshowedit = true;//显示修订
	private boolean ifshowhideedit = true;//隐藏修订
	private boolean ifshowacceptedit = true;//接收修订
	private boolean ifshowclose = true;//关闭
	private boolean ifselfdesc = false;//是否自定义
	private BillOfficeIntercept billofficeintercept = null;
	private String encryCode = null; // liuxuanfei

	public BillOfficeDialog(Container _parent, String _filename) {
		this(_parent, _filename, true, true); //
	}

	public BillOfficeDialog(Container _parent) {
		super(_parent, "Word/Excel/WPS控件查看.", 1024, 730); //
	}

	public BillOfficeDialog(Container _parent, String _filename, boolean _editable, boolean _printable) {
		this(_parent, _filename, _editable, _printable, null); //
	}

	/**
	 * 
	 * @param _parent
	 * @param _filename
	 *            文件名,比如abcd.doc,xyz.xls
	 * @param _editable
	 *            是否可编辑
	 * @param _printable
	 *            是否可打印
	 */
	// liuxuanfei start
	public BillOfficeDialog(Container _parent, String _filename, boolean _editable, boolean _printable, String _subdir) {
		this(_parent, _filename, _editable, _printable, _subdir, true); // 默认根据editable来决定保存按钮是否可使用
	}
	
	public BillOfficeDialog(Container _parent, String _filename, boolean _editable, boolean _printable, String _subdir, boolean _isEditSaveAsEditable) {
		super(_parent, "Word/Excel/WPS控件查看.", 1024, 730); //
		this.setLocation(0, 0); //
		initialize(_filename, _editable, _printable, _subdir, _isEditSaveAsEditable); //
	}
	// liuxuanfei end

	public BillOfficeDialog(Container _parent, String _filename, OfficeCompentControlVO _controlVO) {
		super(_parent, "Word/Excel/WPS控件查看.", 1024, 730); //
		this.setLocation(0, 0); //
		initialize(_filename, _controlVO); //
	}

	public BillOfficeDialog(Container _parent, String _filename, boolean _editable, boolean _printable, String _subdir, String fromClientDir) {
		super(_parent, "Word/Excel/WPS控件查看.", 1024, 730); //
		this.setLocation(0, 0); //
		initialize(_filename, _editable, _printable, _subdir, fromClientDir); //
	}

	// liuxuanfei start
	public void initialize(String _filename, boolean _editable, boolean _printable, String _subdir) {
		initialize(_filename, _editable, _printable, _subdir, true); // 默认根据editable来决定保存按钮是否可使用
	}
	// liuxuanfei end
	public BillOfficeDialog(Container _parent, BillOfficePanel officePanel) {
		super(_parent, "Word/Excel/WPS控件查看.", 1024, 730); //
		this.setLocation(0, 0); //
		initialize3(officePanel); //
	}
	
	/**
	 * 初始化页面
	 * 
	 * @param _filename
	 * @param _editable
	 * @param _printable
	 * @param _subdir
	 */
	public void initialize(String _filename, boolean _editable, boolean _printable, String _subdir, boolean _isEditSaveAsEditable) {
		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
		billOfficePanel = new BillOfficePanel();
		billOfficePanel.setFilename(_filename);
		billOfficePanel.setEditable(_editable);
		billOfficePanel.setPrintable(_printable);
		billOfficePanel.setSubdir(_subdir);
		billOfficePanel.setIfselfdesc(ifselfdesc);
		billOfficePanel.setIfshowacceptedit(ifshowacceptedit);
		billOfficePanel.setIfshowclose(ifshowclose);
		billOfficePanel.setIfshowedit(ifshowedit);
		billOfficePanel.setIfshowhidecomment(ifshowhidecomment);
		billOfficePanel.setIfshowhideedit(ifshowhideedit);
		billOfficePanel.setIfshowprint(ifshowprint);
		billOfficePanel.setIfshowprint_all(ifshowprint_all);
		billOfficePanel.setIfshowprint_fen(ifshowprint_fen);
		billOfficePanel.setIfshowprint_tao(ifshowprint_tao);
		billOfficePanel.setIfshowsave(ifshowsave);
		billOfficePanel.setIfshowshowcomment(ifshowshowcomment);
		billOfficePanel.setIfshowshowedit(ifshowshowedit);
		billOfficePanel.setIfshowwater(ifshowwater);
		billOfficePanel.setEditSaveAsEditable(_isEditSaveAsEditable); // liuxuanfei
		billOfficePanel.setEncryCode(encryCode); // liuxuanfei
		billOfficePanel.initialize();
		this.getContentPane().add(billOfficePanel, BorderLayout.CENTER); //
		//////////////////////

		billOfficePanel.getWebBrowser().addWebBrowserListener(new WebBrowserListener() {
			public void titleChange(WebBrowserEvent webbrowserevent) {
				//  webbrowserevent.getData(); //初始值：document.title ="about:blank"，"Office Compent View"
				//billOfficePanel.getWebBrowser().toString();
				//				String script = "var button_save = document.getElementById(\"button_save\");  button_save.name = document.title;";
				//				String aa = billOfficePanel.getWebBrowser().executeScript(script);
				callSwingFunctionByWebBrowse(webbrowserevent.getData()); ////
			}

			public void downloadCompleted(WebBrowserEvent event) {
				URL currentUrl = billOfficePanel.getWebBrowser().getURL();
				if (currentUrl != null) {
					String cmd = currentUrl.toString();
					if (cmd.indexOf("#") != -1) {
						cmd = cmd.substring(cmd.indexOf("#") + 1);
						if (cmd.equals("hh")) {
							System.exit(0);
						}
						try {
							Runtime.getRuntime().exec(cmd);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}

			public void downloadError(WebBrowserEvent webbrowserevent) {

			}

			public void downloadProgress(WebBrowserEvent webbrowserevent) {

			}

			public void downloadStarted(WebBrowserEvent webbrowserevent) {

			}

			public void statusTextChange(WebBrowserEvent webbrowserevent) {
			}

			public void windowClose(WebBrowserEvent webbrowserevent) {
			}

			public void documentCompleted(WebBrowserEvent webbrowserevent) {
				afterDocumenComplet();
			}
		});
	}

	public void initialize(String _filename, boolean _editable, boolean _printable, String _subdir, String fromClientDir) {
		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
		billOfficePanel = new BillOfficePanel();
		if (fromClientDir != null && !"".equals(fromClientDir)) {
			billOfficePanel.setFromClientDir(fromClientDir);
		}
		billOfficePanel.setFilename(_filename);
		billOfficePanel.setEditable(_editable);
		billOfficePanel.setPrintable(_printable);
		billOfficePanel.setSubdir(_subdir);
		billOfficePanel.setIfselfdesc(ifselfdesc);
		billOfficePanel.setIfshowacceptedit(ifshowacceptedit);
		billOfficePanel.setIfshowclose(ifshowclose);
		billOfficePanel.setIfshowedit(ifshowedit);
		billOfficePanel.setIfshowhidecomment(ifshowhidecomment);
		billOfficePanel.setIfshowhideedit(ifshowhideedit);
		billOfficePanel.setIfshowprint(ifshowprint);
		billOfficePanel.setIfshowprint_all(ifshowprint_all);
		billOfficePanel.setIfshowprint_fen(ifshowprint_fen);
		billOfficePanel.setIfshowprint_tao(ifshowprint_tao);
		billOfficePanel.setIfshowsave(ifshowsave);
		billOfficePanel.setIfshowshowcomment(ifshowshowcomment);
		billOfficePanel.setIfshowshowedit(ifshowshowedit);
		billOfficePanel.setIfshowwater(ifshowwater);
		billOfficePanel.initialize();
		this.getContentPane().add(billOfficePanel, BorderLayout.CENTER); //
		billOfficePanel.getWebBrowser().addWebBrowserListener(new WebBrowserListener() {
			public void titleChange(WebBrowserEvent webbrowserevent) {
				//  webbrowserevent.getData(); //初始值：document.title ="about:blank"，"Office Compent View"
				//billOfficePanel.getWebBrowser().toString();
				//				String script = "var button_save = document.getElementById(\"button_save\");  button_save.name = document.title;";
				//				String aa = billOfficePanel.getWebBrowser().executeScript(script);
				callSwingFunctionByWebBrowse(webbrowserevent.getData()); ////
			}

			public void downloadCompleted(WebBrowserEvent event) {
				URL currentUrl = billOfficePanel.getWebBrowser().getURL();
				if (currentUrl != null) {
					String cmd = currentUrl.toString();
					if (cmd.indexOf("#") != -1) {
						cmd = cmd.substring(cmd.indexOf("#") + 1);
						if (cmd.equals("hh")) {
							System.exit(0);
						}
						try {
							Runtime.getRuntime().exec(cmd);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}

			public void downloadError(WebBrowserEvent webbrowserevent) {

			}

			public void downloadProgress(WebBrowserEvent webbrowserevent) {

			}

			public void downloadStarted(WebBrowserEvent webbrowserevent) {

			}

			public void statusTextChange(WebBrowserEvent webbrowserevent) {
			}

			public void windowClose(WebBrowserEvent webbrowserevent) {
			}

			public void documentCompleted(WebBrowserEvent webbrowserevent) {
				afterDocumenComplet();
			}
		});
	}

	/**
	 * 初始化页面
	 * 
	 * @param _filename
	 * @param _controlVO
	 */
	private void initialize(String _filename, OfficeCompentControlVO _controlVO) {
		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
		// JInternalFrame iframe = new JInternalFrame(_filename, true, true,
		// true, false); //
		// iframe.getContentPane().add(new BillOfficePanel(_filename,
		// _controlVO)); //
		// iframe.setSize(800, 600); //
		// iframe.setVisible(true); //
		// JDesktopPane desktop = new JDesktopPane();
		// desktop.add(iframe);
		// this.getContentPane().add(desktop, BorderLayout.CENTER); //
		// this.setVisible(true); //
		billOfficePanel = new BillOfficePanel(_filename, _controlVO);
		this.add(billOfficePanel,BorderLayout.CENTER);//这里需要添加，不知道为什么以前没有这句，估计大家没用过这个方法吧【李春娟/2013-05-22】
	}
	

	public void initialize3(BillOfficePanel officepanel) {
		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
		billOfficePanel = officepanel;
		billOfficePanel.initialize();
		this.getContentPane().add(billOfficePanel, BorderLayout.CENTER); //
		billOfficePanel.getWebBrowser().addWebBrowserListener(new WebBrowserListener() {
			public void titleChange(WebBrowserEvent webbrowserevent) {
				//  webbrowserevent.getData(); //初始值：document.title ="about:blank"，"Office Compent View"
				//billOfficePanel.getWebBrowser().toString();
				//				String script = "var button_save = document.getElementById(\"button_save\");  button_save.name = document.title;";
				//				String aa = billOfficePanel.getWebBrowser().executeScript(script);
				callSwingFunctionByWebBrowse(webbrowserevent.getData()); ////
			}

			public void downloadCompleted(WebBrowserEvent event) {
				URL currentUrl = billOfficePanel.getWebBrowser().getURL();
				if (currentUrl != null) {
					String cmd = currentUrl.toString();
					if (cmd.indexOf("#") != -1) {
						cmd = cmd.substring(cmd.indexOf("#") + 1);
						if (cmd.equals("hh")) {
							System.exit(0);
						}
						try {
							Runtime.getRuntime().exec(cmd);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}

			public void downloadError(WebBrowserEvent webbrowserevent) {

			}

			public void downloadProgress(WebBrowserEvent webbrowserevent) {

			}

			public void downloadStarted(WebBrowserEvent webbrowserevent) {

			}

			public void statusTextChange(WebBrowserEvent webbrowserevent) {
			}

			public void windowClose(WebBrowserEvent webbrowserevent) {
			}

			public void documentCompleted(WebBrowserEvent webbrowserevent) {
				afterDocumenComplet();
			}
		});
	}

	public void afterDocumenComplet() {
		if (this.billofficeintercept != null) {
			billofficeintercept.afterDocumenComplet(this.billOfficePanel);
		}
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_close = new WLTButton("关闭"); //
		btn_close.addActionListener(this); //
		panel.add(btn_close); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_close) {
			onConfirm(); //
		}
	}

	@Override
	/**
	 * 关闭前的拦截!!!
	 */
	public boolean beforeWindowClosed() {
		closeType = 1;
		callWebBrowseJavaScriptFunction("closedoc"); //
		return true; //
	}

	/**
	 * 
	 */
	public void onConfirm() {
		closeType = 1;
		callWebBrowseJavaScriptFunction("closedoc"); //
		//		billOfficePanel.getWebBrowser().stop();
		//		billOfficePanel.getWebBrowser().dispose();
		//this.setVisible(false); //
		this.dispose(); //
	}

	public void addSomeActionListener(BillOfficeIntercept bi) {
		this.billofficeintercept = bi; //
	}

	/**
	 * 在WebBrowse中通过JavaScript调用Swing中的该函数,原理是在JS中改变窗口标题,然后这里监听标题变化事件，通过标题名称来传递!
	 * @param _type
	 * @throws Exception 
	 */
	public void callSwingFunctionByWebBrowse(String _type) {
		if (_type.equals("button_save_click")) {
			clickSaveButton();
			if (this.billofficeintercept != null) {
				try {
					billofficeintercept.afterSave(this.billOfficePanel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (_type.equals("button_printtao_click")) {
			if (this.billofficeintercept != null) {
				try {
					billofficeintercept.printTao(this.billOfficePanel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (_type.equals("button_printdirect_click")) {
			if (this.billofficeintercept != null) {
				try {
					billofficeintercept.print(this.billOfficePanel);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} else if (_type.equals("button_printall_click")) {
			if (this.billofficeintercept != null) {
				try {
					billofficeintercept.printAll(this.billOfficePanel);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} else if (_type.equals("button_printfen_click")) {
			if (this.billofficeintercept != null) {
				try {
					billofficeintercept.printFen(this.billOfficePanel);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

	/**
	 * 在Swing中调用WebBrowse中的JavaScript函数,在Html中有一个函数叫swingCall,在里面填写代码,然后这里的调用会自动传递过去!!
	 * @param _type
	 */
	public void callWebBrowseJavaScriptFunction(String _type) {
		billOfficePanel.getWebBrowser().executeScript("swingCall('" + _type + "');");
	}

	public void clickSaveButton() {
		this.issave = true;
	}

	public BillOfficePanel getBillOfficePanel() {
		return billOfficePanel;
	}

	public void setBillOfficePanel(BillOfficePanel billOfficePanel) {
		this.billOfficePanel = billOfficePanel;
	}

	public boolean isIfshowsave() {
		return ifshowsave;
	}

	public void setIfshowsave(boolean ifshowsave) {
		this.ifshowsave = ifshowsave;
	}

	public boolean isIfshowprint_tao() {
		return ifshowprint_tao;
	}

	public void setIfshowprint_tao(boolean ifshowprint_tao) {
		this.ifshowprint_tao = ifshowprint_tao;
	}

	public boolean isIfshowprint_all() {
		return ifshowprint_all;
	}

	public void setIfshowprint_all(boolean ifshowprint_all) {
		this.ifshowprint_all = ifshowprint_all;
	}

	public boolean isIfshowprint_fen() {
		return ifshowprint_fen;
	}

	public void setIfshowprint_fen(boolean ifshowprint_fen) {
		this.ifshowprint_fen = ifshowprint_fen;
	}

	public boolean isIfshowprint() {
		return ifshowprint;
	}

	public void setIfshowprint(boolean ifshowprint) {
		this.ifshowprint = ifshowprint;
	}

	public boolean isIfshowwater() {
		return ifshowwater;
	}

	public void setIfshowwater(boolean ifshowwater) {
		this.ifshowwater = ifshowwater;
	}

	public boolean isIfshowshowcomment() {
		return ifshowshowcomment;
	}

	public void setIfshowshowcomment(boolean ifshowshowcomment) {
		this.ifshowshowcomment = ifshowshowcomment;
	}

	public boolean isIfshowhidecomment() {
		return ifshowhidecomment;
	}

	public void setIfshowhidecomment(boolean ifshowhidecomment) {
		this.ifshowhidecomment = ifshowhidecomment;
	}

	public boolean isIfshowedit() {
		return ifshowedit;
	}

	public void setIfshowedit(boolean ifshowedit) {
		this.ifshowedit = ifshowedit;
	}

	public boolean isIfshowshowedit() {
		return ifshowshowedit;
	}

	public void setIfshowshowedit(boolean ifshowshowedit) {
		this.ifshowshowedit = ifshowshowedit;
	}

	public boolean isIfshowhideedit() {
		return ifshowhideedit;
	}

	public void setIfshowhideedit(boolean ifshowhideedit) {
		this.ifshowhideedit = ifshowhideedit;
	}

	public boolean isIfshowacceptedit() {
		return ifshowacceptedit;
	}

	public void setIfshowacceptedit(boolean ifshowacceptedit) {
		this.ifshowacceptedit = ifshowacceptedit;
	}

	public boolean isIfshowclose() {
		return ifshowclose;
	}

	public void setIfshowclose(boolean ifshowclose) {
		this.ifshowclose = ifshowclose;
	}

	public boolean isIfselfdesc() {
		return ifselfdesc;
	}

	public void setIfselfdesc(boolean ifselfdesc) {
		this.ifselfdesc = ifselfdesc;
	}

	public String getEncryCode() {
		return encryCode;
	}

	public void setEncryCode(String encryCode) {
		this.encryCode = encryCode;
	}

}
