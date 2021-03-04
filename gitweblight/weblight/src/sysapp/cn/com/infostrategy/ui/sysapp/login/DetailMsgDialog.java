/**************************************************************************
 * $RCSfile: DetailMsgDialog.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:19:33 $
 **************************************************************************/
package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.ui.common.BillDialog;


public class DetailMsgDialog extends BillDialog {

	private static final long serialVersionUID = 5540334144291572263L;
	
	private final static Font f_title = new Font("宋体", Font.BOLD, 14);

	private String str_title = null;
	
	private String str_content = null;
	
	public DetailMsgDialog(Container _parent, String _title, String _context) {
		super(_parent, "查看" + _title + "详情", 750, 650); //

		str_title = _title;
		str_content = _context;
		initialize();
	}

	private void initialize() {
		this.setLayout(new BorderLayout());
		
		JLabel jlb_title = new JLabel(str_title);
		
		JPanel jpn_title = new JPanel();
		jpn_title.setLayout(new FlowLayout(FlowLayout.CENTER, 20,10));
		jpn_title.add(jlb_title);
		jlb_title.setFont(f_title);
		
		JTextArea jta_context = new JTextArea(str_content);
		jta_context.setEditable(false);
		jta_context.setLineWrap(true);
		jta_context.setWrapStyleWord(true);
		jta_context.setBorder(null);
		
		JScrollPane jsp_text = new JScrollPane(jta_context);
		

		JButton jbt_confirm = new JButton("确定");
		jbt_confirm.setPreferredSize(new Dimension(75,20));
		jbt_confirm.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				onconfirm();
			}});
		jbt_confirm.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					onconfirm();
				}
			}
		});
		JPanel jpn_btn = new JPanel();
		jpn_btn.setLayout(new FlowLayout(FlowLayout.CENTER, 20,10));
		jpn_btn.add(jbt_confirm);
		
		
		this.add(jpn_title, BorderLayout.NORTH);
		this.add(jsp_text, BorderLayout.CENTER);
		this.add(jpn_btn, BorderLayout.SOUTH);
		this.add(new JLabel("  "), BorderLayout.EAST);
		this.add(new JLabel("  "), BorderLayout.WEST);
	}

	protected void onconfirm() {
		this.dispose();
	}
}
/**************************************************************************
 * $RCSfile: DetailMsgDialog.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:19:33 $
 *
 * $Log: DetailMsgDialog.java,v $
 * Revision 1.4  2012/09/14 09:19:33  xch123
 * 邮储现场回来统一更新
 *
 * Revision 1.1  2012/08/28 09:41:13  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:32:08  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:42  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:51  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:59  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:13:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:11:30  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:48  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:51  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:36  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:41  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:29:10  xch
 * *** empty log message ***
 *
 * Revision 1.4  2007/09/20 05:08:31  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/02/10 08:59:36  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:20:39  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/