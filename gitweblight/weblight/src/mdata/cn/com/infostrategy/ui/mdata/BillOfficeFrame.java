package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.OfficeCompentControlVO;
import cn.com.infostrategy.ui.common.BillFrame;
import cn.com.infostrategy.ui.common.WLTButton;

/**
 * Office����
 * @author xch
 *
 */
public class BillOfficeFrame extends BillFrame implements ActionListener {

	private WLTButton btn_close;
	private int closeType = -1;

	public BillOfficeFrame(Container _parent, String _filename) {
		this(_parent, _filename, true, true); //
	}

	public BillOfficeFrame(Container _parent, String _filename, boolean _editable, boolean _printable) {
		this(_parent, _filename, _editable, _printable, null); //
	}

	/**
	 * 
	 * @param _parent
	 * @param _filename �ļ���,����abcd.doc,xyz.xls
	 * @param _editable �Ƿ�ɱ༭
	 * @param _printable �Ƿ�ɴ�ӡ
	 */
	public BillOfficeFrame(Container _parent, String _filename, boolean _editable, boolean _printable, String _subdir) {
		super(_parent, "Word/Excel/WPS�ؼ��鿴.", 1024, 730); //
		this.setLocation(0, 0); //
		initialize(_filename, _editable, _printable, _subdir); //
	}

	public BillOfficeFrame(Container _parent, String _filename, OfficeCompentControlVO _controlVO) {
		super(_parent, "Word/Excel/WPS�ؼ��鿴.", 1024, 730); //
		this.setLocation(0, 0); //
		initialize(_filename, _controlVO); //
	}

	/**
	 * ��ʼ��ҳ��
	 * @param _filename
	 * @param _editable
	 * @param _printable
	 * @param _subdir
	 */
	private void initialize(String _filename, boolean _editable, boolean _printable, String _subdir) {
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE); //
		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //
		this.getContentPane().add(new BillOfficePanel(_filename, _editable, _printable, _subdir), BorderLayout.CENTER); //
	}

	/**
	 * ��ʼ��ҳ��
	 * @param _filename
	 * @param _controlVO
	 */
	private void initialize(String _filename, OfficeCompentControlVO _controlVO) {
		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE); //
		this.getContentPane().setLayout(new BorderLayout()); //
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH); //

		//		JInternalFrame iframe = new JInternalFrame(_filename, true, true, true, false); //
		//		iframe.getContentPane().add(new BillOfficePanel(_filename, _controlVO)); //
		//		iframe.setSize(800, 600); //
		//		iframe.setVisible(true); //
		//		JDesktopPane desktop = new JDesktopPane();
		//		desktop.add(iframe);
		//		this.getContentPane().add(desktop, BorderLayout.CENTER); //

		this.getContentPane().add(new BillOfficePanel(_filename, _controlVO), BorderLayout.CENTER); //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_close = new WLTButton("�ر�"); //
		btn_close.addActionListener(this); //
		panel.add(btn_close); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_close) {
			onConfirm();
		}
	}

	public void onConfirm() {
		closeType = 1;
		this.setVisible(false); //
	}

}
