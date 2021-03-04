/**************************************************************************
 * $RCSfile: ShowCopyTempleteDialog.java,v $  $Revision: 1.7 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTPanel;

public class ShowCopyTempleteDialog extends BillDialog {

	private static final long serialVersionUID = 1L;

	private JLabel table_name_Label = null;

	private JLabel templet_code_Label = null;

	private JLabel templet_name_Label = null;

	private JTextField table_name_text = null;

	private JTextField templet_code_text = null;

	private JTextField templet_name_text = null;

	private String str_oldtablename = null;//原来的模板查询表名
	private String str_oldcode = null; // 原来的模板编码!!
	private String str_oldname = null; //原来的模板名称

	protected int li_closeType;

	private JButton btn_confirm;

	private JButton btn_cancel;

	private String str_newtemplete_code;

	private String str_newtemplete_name;

	public ShowCopyTempleteDialog(Container _parent, String _oldcode) {
		this(_parent, _oldcode, _oldcode, _oldcode);
	}

	public ShowCopyTempleteDialog(Container _parent, String _oldtablename, String _oldcode, String _oldname) {
		super(_parent, "复制模板", 550, 300); //
		this.str_oldtablename = _oldtablename;
		this.str_oldcode = _oldcode; //
		this.str_oldname = _oldname;

		this.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				li_closeType = 2;
			}

			public void windowClosing(WindowEvent e) {
				li_closeType = 2;
			}
		});
		initialize(); //
	}

	private void initialize() {
		table_name_Label = new JLabel("表名：");
		templet_code_Label = new JLabel("模板编码：");
		templet_name_Label = new JLabel("模板名称：");

		table_name_Label.setHorizontalAlignment(JLabel.RIGHT);
		templet_code_Label.setHorizontalAlignment(JLabel.RIGHT);
		templet_name_Label.setHorizontalAlignment(JLabel.RIGHT);

		table_name_Label.setPreferredSize(new Dimension(80, 20));
		templet_code_Label.setPreferredSize(new Dimension(80, 20));
		templet_name_Label.setPreferredSize(new Dimension(80, 20));

		table_name_text = new JTextField();
		templet_code_text = new JTextField();
		templet_name_text = new JTextField();

		table_name_text.setPreferredSize(new Dimension(320, 20));
		templet_code_text.setPreferredSize(new Dimension(320, 20));
		templet_name_text.setPreferredSize(new Dimension(320, 20));

		table_name_text.setEditable(false);
		table_name_text.setText(str_oldtablename);
		templet_code_text.setText(str_oldcode + "_1"); //
		templet_name_text.setText(str_oldname); //以前复制模板时，模板名称跟编码一样，经常需要将旧模板名称重写一遍，故这里直接就设置为旧的模板名称得了【李春娟/2012-07-13】

		btn_confirm = new JButton("确定");
		btn_confirm.setPreferredSize(new Dimension(90, 20));
		btn_confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}

		});

		btn_cancel = new JButton("取消");
		btn_cancel.setPreferredSize(new Dimension(90, 20));
		btn_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}

		});
		Box table_name_box = Box.createHorizontalBox();
		table_name_box.add(Box.createHorizontalStrut(20));
		table_name_box.add(table_name_Label);
		table_name_box.add(table_name_text);
		table_name_box.add(Box.createGlue());

		Box templet_code_box = Box.createHorizontalBox();
		templet_code_box.add(Box.createHorizontalStrut(20));
		templet_code_box.add(templet_code_Label);
		templet_code_box.add(templet_code_text);
		templet_code_box.add(Box.createGlue());

		Box templet_name_box = Box.createHorizontalBox();
		templet_name_box.add(Box.createHorizontalStrut(20));
		templet_name_box.add(templet_name_Label);
		templet_name_box.add(templet_name_text);
		templet_name_box.add(Box.createGlue());

		JPanel center_panel = new WLTPanel(1, LookAndFeel.cardbgcolor);

		center_panel.add(table_name_box);
		center_panel.add(templet_code_box);
		center_panel.add(templet_name_box);

		Box btn_box = Box.createHorizontalBox();
		btn_box.add(Box.createHorizontalGlue());
		btn_box.add(btn_confirm);
		btn_box.add(Box.createHorizontalStrut(40));
		btn_box.add(btn_cancel);
		btn_box.add(Box.createHorizontalGlue());

		Box center_box = Box.createVerticalBox();
		center_box.add(Box.createVerticalStrut(30));
		center_box.add(table_name_box);
		center_box.add(Box.createVerticalStrut(20));
		center_box.add(templet_code_box);
		center_box.add(Box.createVerticalStrut(20));
		center_box.add(templet_name_box);
		center_box.add(Box.createVerticalStrut(40));
		center_box.add(btn_box);
		center_box.add(Box.createVerticalStrut(20));
		center_panel.add(center_box, BorderLayout.CENTER);

		this.getContentPane().add(center_panel, BorderLayout.CENTER);
	}

	protected void onCancel() {
		this.li_closeType = 1;
		this.dispose();
	}

	/**
	 * 关闭
	 */
	protected void onClose() {
		this.li_closeType = 2;
		this.dispose();
	}

	protected void onConfirm() {
		this.str_newtemplete_code = this.templet_code_text.getText();
		this.str_newtemplete_name = this.templet_name_text.getText();
		this.li_closeType = 0;

		if (!ensureNotEmpty()) {
			return;
		}
		String[][] str_getArray = null;
		try {
			str_getArray = UIUtil.getStringArrayByDS(null, "select * from PUB_TEMPLET_1 where TEMPLETCODE = '" + str_newtemplete_code.toUpperCase() + "'");
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (str_getArray.length > 0) {
			JOptionPane.showMessageDialog(this, "你要插入的模版已经存在，请重新输入模板编码!", "操作提示", JOptionPane.WARNING_MESSAGE);
			return;
		}
		this.dispose();
	}

	public String getTempleteName() {
		return this.str_newtemplete_name;
	}

	public String getTempleteCode() {
		return this.str_newtemplete_code;
	}

	public int getCloseType() {
		return this.li_closeType;
	}

	private boolean ensureNotEmpty() {
		if (str_newtemplete_code.equals("")) {
			JOptionPane.showMessageDialog(this, "模板编码名不能为空！");
			return false;
		}
		if (str_newtemplete_name.equals("")) {
			JOptionPane.showMessageDialog(this, "模板名不能为空！");
			return false;
		}
		return true;
	}

	public JTextField getTemplet_code_text() {
		return templet_code_text;
	}

	public void setTemplet_code_text(JTextField templet_code_text) {
		this.templet_code_text = templet_code_text;
	}

	public JTextField getTemplet_name_text() {
		return templet_name_text;
	}

	public void setTemplet_name_text(JTextField templet_name_text) {
		this.templet_name_text = templet_name_text;
	}
}
/**************************************************************************
 * $RCSfile: ShowCopyTempleteDialog.java,v $  $Revision: 1.7 $  $Date: 2012/09/14 09:22:57 $
 *
 * $Log: ShowCopyTempleteDialog.java,v $
 * Revision 1.7  2012/09/14 09:22:57  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:58  Administrator
 * *** empty log message ***
 *
 * Revision 1.6  2012/07/13 05:33:08  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.5  2012/06/19 10:40:43  sunfujun
 * 20120619/sunfujun/主要修改了关于xml模板查询其他几个地方的优化
 *
 * Revision 1.4  2012/06/19 06:54:40  sunfujun
 * sunfujun/20120619/背景色修改
 *
 * Revision 1.3  2011/10/10 06:31:45  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:14  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:04  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:13  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:54  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:57  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:43  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:30  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:21  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:19  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:29  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:40  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:06  xch
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/07 02:01:54  shxch
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/02 05:02:48  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/02/10 08:51:58  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:56:13  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
