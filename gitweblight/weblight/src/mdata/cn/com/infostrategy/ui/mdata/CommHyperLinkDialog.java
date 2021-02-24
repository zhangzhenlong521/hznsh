package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.styletemplet.t02.CommonHyperLinkworkPanel_02;

/**
 * ���ֿؼ��������һ��������!һ�㶼��ֱ��ά�����������õ�,
 * �������һ�����ͨ����,���Ǹ���һ��ģ�����,ֱ��ʹ�÷��ģ��2��һ��ҳ�������ɾ��!!!
 * @author xch
 *
 */
public class CommHyperLinkDialog extends AbstractHyperLinkDialog {

	private static final long serialVersionUID = 6517711268656222636L;
	private String str_templetCode = null;

	public CommHyperLinkDialog(Container _parent, String _templetCode) {
		super(_parent);
		this.str_templetCode = _templetCode; //
	}

	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout()); //
		CommonHyperLinkworkPanel_02 panel = new CommonHyperLinkworkPanel_02(str_templetCode); //
		panel.initialize(); //
		this.getContentPane().add(panel, BorderLayout.CENTER);

		JPanel panel_south = new JPanel();
		panel_south.setLayout(new FlowLayout());
		JButton btn_close = new WLTButton(UIUtil.getLanguage("�ر�"));
		btn_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CommHyperLinkDialog.this.dispose(); //
			}
		});
		panel_south.add(btn_close);

		this.getContentPane().add(panel_south, BorderLayout.SOUTH); //
	}

}
