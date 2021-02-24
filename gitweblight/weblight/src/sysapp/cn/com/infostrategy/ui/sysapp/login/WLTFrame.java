package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.LookAndFeel;

public class WLTFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private DeskTopPanel desktopPanel = null; //
	private String menuid = null;
	private AbstractWorkPanel workPanel;  //弹出的工作面板
	public WLTFrame(String _title) {

	}

	//public WLTFrame(DeskTopPanel _desktopPanel, String _menuid, String _name, String titltBarText, AbstractWorkPanel workTabPanel, Boolean bo_isextend, Integer li_exendheight) {
	public WLTFrame(DeskTopPanel _desktopPanel, String _menuid, String _name, String titltBarText, AbstractWorkPanel workTabPanel, Boolean bo_isextend, Integer li_exendheight, String weight, String height) {
		super(_name);
		this.desktopPanel = _desktopPanel; //
		this.menuid = _menuid; //
		this.setLocation(0, 0); //
		this.workPanel = workTabPanel;//
		Dimension dei = Toolkit.getDefaultToolkit().getScreenSize();
		int li_width = 1000; //
		if (weight != null) {
			li_width = Integer.parseInt(weight); //
		}

		int li_height = 700; //
		if (height != null) {
			li_height = Integer.parseInt(height); //
		}

		this.setSize(li_width, li_height); //
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				//onFrameClode();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				onFrameClode(); //
			}

		});

		JLabel label = new JLabel(titltBarText);
		label.setOpaque(true);
		label.setForeground(new java.awt.Color(254, 115, 17)); //桔黄色
		label.setBackground(LookAndFeel.billlistquickquerypanelbgcolor); //
		this.getContentPane().add(label, BorderLayout.NORTH); //

		if (bo_isextend != null && bo_isextend.booleanValue()) {
			JPanel panel_sss = new JPanel(new BorderLayout()); //
			panel_sss.setPreferredSize(new Dimension(1000, (li_exendheight == null ? 800 : li_exendheight.intValue()))); //
			panel_sss.add(workTabPanel); //
			JScrollPane scrollPanel = new JScrollPane(panel_sss);
			scrollPanel.getVerticalScrollBar().setPreferredSize(new Dimension(12, 100)); //
			scrollPanel.setBorder(BorderFactory.createEmptyBorder()); //
			this.add(scrollPanel, BorderLayout.CENTER);
		} else {
			this.getContentPane().add(workTabPanel, BorderLayout.CENTER);
		}

	}

	private void onFrameClode() {
		this.dispose(); //
		desktopPanel.closeFrame(this.menuid); //
	}
	public AbstractWorkPanel getWorkPanel(){
		return workPanel;//通过WltFrame可以得到其中的AbstractWorkPanel。
	}
	
}
