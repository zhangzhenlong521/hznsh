/**************************************************************************
 * $RCSfile: QuickPutValueDialog.java,v $  $Revision: 1.7 $  $Date: 2013/02/28 06:14:47 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_CheckBox;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ComboBox;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Ref;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_TextArea;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_TextField;

public class QuickPutValueDialog extends BillDialog {
	private Object result = null;
	private Pub_Templet_1_ItemVO itemvo = null;
	JComponent panel = null;
	private boolean isModified = false;

	public QuickPutValueDialog(Container _parent, String _title, Pub_Templet_1_ItemVO _itemvo, boolean model) {
		super(JOptionPane.getFrameForComponent(_parent), _title, model);
		this.itemvo = _itemvo;
		initPanel();
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(initPanel(), BorderLayout.CENTER);
		this.getContentPane().add(getBtnpanel(), BorderLayout.SOUTH);
		if (panel != null)
			this.setSize((int) panel.getPreferredSize().getWidth() + 50, 100);
		else
			this.setSize(300, 184);
		this.setLocation(400, 200);
		this.setVisible(true);
	}

	private JPanel getBtnpanel() {
		JPanel rpanel = new JPanel();
		rpanel.setLayout(new FlowLayout());
		JButton btn = new JButton("确定");
		btn.setPreferredSize(new Dimension(65, 20));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});
		JButton cancel = new JButton("取消");
		cancel.setPreferredSize(new Dimension(65, 20));
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onExit();
			}
		});
		rpanel.add(btn);
		rpanel.add(cancel);
		return rpanel;
	}

	private JPanel initPanel() {
		JPanel rpanel = new JPanel();
		rpanel.setLayout(new FlowLayout());
		String str_type = itemvo.getItemtype();
		if (str_type.equals("文本框")) {
			panel = new CardCPanel_TextField(itemvo);
			rpanel.add(panel);
		} else if (str_type.equals("数字框")) {
			panel = new CardCPanel_TextField(itemvo, WLTConstants.COMP_NUMBERFIELD);
			rpanel.add(panel);
		} else if (str_type.equals("密码框")) {
			panel = new CardCPanel_TextField(itemvo, WLTConstants.COMP_PASSWORDFIELD);
			rpanel.add(panel);
		} else if (str_type.equals("下拉框")) {
			panel = new CardCPanel_ComboBox(itemvo);
			rpanel.add(panel);
		} else if (str_type.equals(WLTConstants.COMP_REFPANEL) || //表型参照1
				str_type.equals(WLTConstants.COMP_REFPANEL_TREE) || //树型参照1
				str_type.equals(WLTConstants.COMP_REFPANEL_MULTI) || //多选参照1
				str_type.equals(WLTConstants.COMP_REFPANEL_CUST) || //自定义参照
				str_type.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //列表模板参照
				str_type.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //树型模板参照
				str_type.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //注册样板参照
				str_type.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //注册参照
				str_type.equals(WLTConstants.COMP_DATE) || //日历
				str_type.equals(WLTConstants.COMP_DATETIME) || //时间
				str_type.equals(WLTConstants.COMP_BIGAREA) || //大文本框
				//str_type.equals(WLTConstants.COMP_FILECHOOSE) || //文件选择框!!!（以前也是当一个参照处理,但后来无数处用户要求将其拿到主页面上来,以符合大量网页系统的效果,少点一次,）
				str_type.equals(WLTConstants.COMP_COLOR) || //颜色选择框
				str_type.equals(WLTConstants.COMP_CALCULATE) || //计算器
				str_type.equals(WLTConstants.COMP_PICTURE) || //图片选择框
				str_type.equals(WLTConstants.COMP_EXCEL) || //EXCEL
				str_type.equals(WLTConstants.COMP_OFFICE) //office
		) {
			panel = new CardCPanel_Ref(itemvo, null, null);
			rpanel.add(panel);
		} else if (str_type.equals(WLTConstants.COMP_TEXTAREA)) { //多行文本框
			panel = new CardCPanel_TextArea(itemvo, null, null);
			rpanel.add(panel);
		} else if (str_type.equals("文件选择框")) {
			panel = new CardCPanel_Ref(itemvo, null, null);
			rpanel.add(panel);
		} else if (str_type.equals("颜色")) {
			panel = new CardCPanel_Ref(itemvo, null, null);
			rpanel.add(panel);
		} else if (str_type.equals("大文本框")) {
			panel = new CardCPanel_Ref(itemvo, null, null);
			rpanel.add(panel);
		} else if (str_type.equals("勾选框")) {
			panel = new CardCPanel_CheckBox(itemvo);
			rpanel.add(panel);
		}
		return rpanel;
	}

	public boolean isModified() {
		return this.isModified;
	}

	public void onConfirm() {
		this.isModified = true;
		setValue();
		this.dispose();
	}

	public void onExit() {
		this.isModified = false;
		this.dispose();
	}

	public void setValue() {
		if (panel != null)
			result = ((AbstractWLTCompentPanel) panel).getObject();
	}

	public Object getValue() {
		return result;
	}
}
/**************************************************************************
 * $RCSfile: QuickPutValueDialog.java,v $  $Revision: 1.7 $  $Date: 2013/02/28 06:14:47 $
 *
 * $Log: QuickPutValueDialog.java,v $
 * Revision 1.7  2013/02/28 06:14:47  wanggang
 * *** empty log message ***
 *
 * Revision 1.5  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:41:01  Administrator
 * *** empty log message ***
 *
 * Revision 1.4  2011/11/21 11:13:04  xch123
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:46  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:16  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:05  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:16  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:58  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:13:01  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/10/21 07:51:39  kuangli
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:51  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/02/19 07:31:01  wangjian
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:32  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:25  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2008/03/06 08:41:18  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/03/06 08:39:08  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:22  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:31  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:44  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:34  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 05:14:32  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
