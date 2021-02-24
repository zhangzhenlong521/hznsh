/**************************************************************************
 * $RCSfile: RefDialog_Picture.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.mdata.BillPanel;

public class RefDialog_Picture extends AbstractRefDialog {

	private static final long serialVersionUID = 7240993480143688006L;

	private ImageIconFileListPanel flp_file = null;

	private JTextField jtf_name = null;

	private String str_init_image = null;

	private String str_selectedicon = null;

	public RefDialog_Picture(Container _parent, String _title, RefItemVO value, BillPanel panel) {
		super(_parent, _title, value, panel);
	}

	public void initialize() {
		if (getInitRefItemVO() != null) {
			str_init_image = getInitRefItemVO().getId();
		}

		flp_file = new ImageIconFileListPanel(str_init_image);
		this.getContentPane().setBackground(new Color(240, 240, 240));
		this.getContentPane().setLayout(new BorderLayout());

		this.getContentPane().add(new JLabel(""), BorderLayout.NORTH);
		this.getContentPane().add(flp_file, BorderLayout.CENTER);
		this.getContentPane().add(getBtnPanel(), BorderLayout.SOUTH);
	}

	private JPanel getBtnPanel() {
		JButton btn_ok = new JButton("确定");
		JButton btn_cancel = new JButton("取消");

		btn_ok.setPreferredSize(new Dimension(75, 20));
		btn_cancel.setPreferredSize(new Dimension(75, 20));

		btn_ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});
		btn_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});

		JPanel p_btn = new JPanel();
		p_btn.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));

		p_btn.add(btn_ok);
		p_btn.add(btn_cancel);

		return p_btn;
	}

	public int getInitWidth() {
		return 800;
	}

	public int getInitHeight() {
		return 600;
	}

	protected void onConfirm() {
		str_selectedicon = flp_file.getSelectedIcon();
		setCloseType(1);
		this.dispose();
	}

	protected void onCancel() {
		setCloseType(2);
		this.dispose();
	}

	public void setFileText(String _filename) {
		this.jtf_name.setText(_filename);
	}

	public String getSelectedIcon() {
		return str_selectedicon;
	}

	public RefItemVO getReturnRefItemVO() {
		return new RefItemVO(str_selectedicon, str_selectedicon, str_selectedicon); //
	}
}
