package cn.com.infostrategy.ui.report;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.JPanel;

import org.jdesktop.jdic.desktop.Desktop;

import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.WLTButton;

/**
 * ֻ��ʾһ��Html���Ĵ���
 * @author xch
 *
 */
public class BillHtmlDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = 2716617659235350546L;
	private BillHtmlPanel htmlPanel = null; //
	private WLTButton btn_ieopen, btn_cancel = null; //
	private URL url = null; //

	public BillHtmlDialog(Container _parent, String _title, String _html) {
		super(_parent, _title, 800, 500);
		htmlPanel = new BillHtmlPanel(); //
		this.getContentPane().add(htmlPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				htmlPanel.disPose(); //
			}
		});

		this.validate(); //
		htmlPanel.loadHtml(_html); //
	}

	public BillHtmlDialog(Container _parent, java.net.URL _url) {
		this(_parent, null, _url); //
	}

	/**
	 * ֱ�Ӵ�һ��url
	 * @param _parent
	 * @param _url
	 */
	public BillHtmlDialog(Container _parent, String _title, java.net.URL _url) {
		super(_parent, (_title == null ? _url.toString() : _title));
		this.url = _url; //
		maxToScreenSizeBy1280AndLocationCenter(); //��������!
		htmlPanel = new BillHtmlPanel(false); //���������ť!
		this.getContentPane().add(htmlPanel, BorderLayout.CENTER); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				htmlPanel.disPose(); //
			}
		});

		this.validate(); //
		htmlPanel.loadWebContentByURL(_url); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_ieopen = new WLTButton("�´����д�"); //
		btn_cancel = new WLTButton("ȷ��"); //
		btn_ieopen.addActionListener(this); //
		btn_cancel.addActionListener(this); //
		if (this.url != null) {
			panel.add(btn_ieopen); //	
		}
		panel.add(btn_cancel); //
		return panel;
	}

	//�����´��ڰ�ť�Ƿ���ʾ!
	public void setNewOpenButtonVisible(boolean _visible) {
		btn_ieopen.setVisible(_visible); //
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
			Desktop.browse(new URL(this.url.toString())); //ʹ��jdic��
		} catch (Throwable e) {
			e.printStackTrace(); //
			try {
				Runtime.getRuntime().exec("explorer.exe \"" + this.url.toString() + "\""); //ֱ�ӵ�windows���̴�!!!	
			} catch (Exception ex) {
				ex.printStackTrace(); //
			}
		}
	}

	private void onCancel() {
		System.out.println("�ͷš�������");
		htmlPanel.disPose(); //
		this.dispose(); //
	}

	public WLTButton getBtn_cancel() {
		return btn_cancel;
	}
}
