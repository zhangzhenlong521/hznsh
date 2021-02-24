package cn.com.infostrategy.ui.report;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.JPanel;

import org.jdesktop.jdic.desktop.Desktop;

import cn.com.infostrategy.ui.common.BillFrame;
import cn.com.infostrategy.ui.common.WLTButton;

/**
 * 只显示一个Html面板的窗口
 * @author xch
 *
 */
public class BillHtmlFrame extends BillFrame implements ActionListener {

	private static final long serialVersionUID = 2716617659235350546L;
	private BillHtmlPanel htmlPanel = null; //
	private WLTButton btn_ieopen, btn_cancel = null; //
	private URL url = null; //

	public BillHtmlFrame(String _title, String _html) {
		super(_title);
		htmlPanel = new BillHtmlPanel(); //
		this.getContentPane().add(htmlPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				htmlPanel.disPose(); //
			}
		});

		maxToScreenSizeBy1280AndLocationCenter(); //
		this.validate(); //
		htmlPanel.loadHtml(_html); //
	}

	public BillHtmlFrame(java.net.URL _url) {
		this(null, _url); //
	}

	/**
	 * 直接打开一个url
	 * @param _parent
	 * @param _url
	 */
	public BillHtmlFrame(String _title, java.net.URL _url) {
		super();
		if (_title != null) {
			this.setTitle(_title); //
		} else {
			this.setTitle(_url.toString()); //
		}
		this.url = _url; //
		htmlPanel = new BillHtmlPanel(false); //否带导出按钮!
		this.getContentPane().add(htmlPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				htmlPanel.disPose(); //
			}
		});
		maxToScreenSizeBy1280AndLocationCenter(); //
		this.validate(); //
		htmlPanel.loadWebContentByURL(_url); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_ieopen = new WLTButton("新窗口中打开"); //
		btn_cancel = new WLTButton("确定"); //
		btn_ieopen.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		if (url != null) {
			panel.add(btn_ieopen); //
		}
		panel.add(btn_cancel); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_ieopen) {
			openIE();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
	}

	private void openIE() {
		try {
			Desktop.browse(new URL(this.url.toString())); //使用jdic打开
		} catch (Throwable e) {
			e.printStackTrace(); //
			try {
				Runtime.getRuntime().exec("explorer.exe \"" + this.url.toString() + "\""); //直接调windows进程打开!!!
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}
	}

	private void onCancel() {
		System.out.println("释放。。。。");
		htmlPanel.disPose(); //
		this.dispose(); //
	}
}
