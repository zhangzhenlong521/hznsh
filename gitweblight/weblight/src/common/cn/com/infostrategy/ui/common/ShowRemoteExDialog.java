/**************************************************************************
 * $RCSfile: ShowRemoteExDialog.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:17:30 $
 *
 * $Log: ShowRemoteExDialog.java,v $
 * Revision 1.5  2012/09/14 09:17:30  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2012/08/28 09:40:50  Administrator
 * *** empty log message ***
 *
 * Revision 1.4  2012/06/08 09:32:40  xch123
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:35  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:02  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:51  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:32:52  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:57  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:43  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/07/09 09:38:34  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:19  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/09/10 01:41:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/28 06:17:40  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/03/16 05:41:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:10  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:16  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:14  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:29  xch
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/07 08:36:38  shxch
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/07 08:32:20  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/07 02:01:55  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/02/10 09:36:50  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/02/10 08:59:52  shxch
 * *** empty log message ***
 *
 **************************************************************************/

package cn.com.infostrategy.ui.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;

/**
 * 显示远程异常的对话框
 * @author xch
 *  yuanjiangxiao   20130301重新修改
 */
public class ShowRemoteExDialog extends BillDialog implements ActionListener{

	private static final long serialVersionUID = 2148915094445394877L;
	private JButton btn_copy, btn_close = null;
	private JTextArea textArea_message = null; //上面的框
	private ImageIcon imageicon=null;
	private JLabel jl_pic=null;
	private JLabel jl_str1=null;
	private LinkLabel jl_str2=null;
	JScrollPane scroll_msg=null;
	private TBUtil tbUtil = new TBUtil(); //
	private boolean isshow=false;
	StringBuffer sb=new StringBuffer();
	public boolean isShowLink=true;  //是否默认显示有超链接
	public boolean isExpand=false;    //是否默认展开下面的错误信息 

	public ShowRemoteExDialog(Container _parent, WLTRemoteException _ex) {
		super(_parent, "发生错误");
		if(!isExpand){//默认不展开
			this.setSize(555, 175);
		}
		this.setMinimumSize(new Dimension(500,150));
		this.getContentPane().setLayout(new BorderLayout()); //
		sb.append(_ex.getMessage()+"\r\n");
		sb.append("客户器端异常堆栈:\r\n");
		sb.append(_ex.getClientStackDetail());   //客户端异常堆栈
		sb.append("----------------------------------------------------------------------------------\r\n");
		sb.append("服务器端异常堆栈:\r\n");
		sb.append(_ex.getServerStackDetail());	//服务器端异常堆栈

		//imageicon=new ImageIcon("c:/error.jpg");   
		JPanel jp_img=new JPanel();
		jp_img.setLayout(new BorderLayout());
		imageicon=UIUtil.getImage("error.jpg");
		/*if (imageicon.getIconWidth() != 555 || imageicon.getIconHeight() != 70) { //如果美工做的图片大小不是我想要的735*70,则进行拉伸处理!
			imageicon = new ImageIcon(tbUtil.getImageScale(imageicon.getImage(), 555, 70)); //
		}*/
		jl_pic = new JLabel(imageicon,2);  //显示图片
		//jl_pic.setBorder(BorderFactory.createLineBorder(Color.black));
		Font font_str=new Font("",Font.PLAIN,13);
		String label_text= new TBUtil().getSysOptionStringValue("远程报错介绍语", "很抱歉，出错了，请将错误详细信息发送给我们，帮助我们改进系统!");
		jl_str1= new JLabel(label_text);//显示错误信息
		jl_str1.setFont(font_str);
		jl_str1.setVisible(true);
		//jl_str1.setBorder(BorderFactory.createLineBorder(Color.red));
		jl_str2=new LinkLabel("详细错误报告");//显示错误信息链接
		jl_str2.setFont(font_str);
		jp_img.add(jl_pic,BorderLayout.NORTH);
		jp_img.add(jl_str1,BorderLayout.WEST);
		//jp_img.add(new JButton("测试"),BorderLayout.WEST);
		jp_img.add(jl_str2,BorderLayout.EAST);
		
		textArea_message = new JTextArea(10,40); //
		textArea_message.append(sb.toString());
		//textArea_message.setFont(f)
		//textArea_message.setPreferredSize(new Dimension(525,180));
		//textArea_message.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 5));
		
		textArea_message.setFont(font_str);
		textArea_message.setLineWrap(true); //
		textArea_message.setWrapStyleWord(true);
		textArea_message.setEditable(false);//设置不可编辑
		scroll_msg= new JScrollPane(textArea_message); // 错误信息加到滚动条中
		scroll_msg.setPreferredSize(new Dimension(535,180));
		scroll_msg.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 10, Color.white));
		scroll_msg.setVisible(false);//默认不显示
		
		this.getContentPane().add(jp_img,BorderLayout.NORTH);//图片面板
		this.getContentPane().add(scroll_msg,BorderLayout.CENTER);//错误信息以及错误信息面板
		this.getContentPane().add(getSouthPanel(false), BorderLayout.SOUTH); //
		this.setVisible(true); //
	}
/**
 * 
 * @param _parent   父容器
 * @param errorMsg  自定义错误信息
 */
	
public ShowRemoteExDialog(Container _parent,String errorMsg){
	super(_parent, "发生错误");
	this.isExpand=false;
	this.isShowLink=true;  //如果自动显示错误信息，则不显示超链接按钮
	//555,355
	this.setMinimumSize(new Dimension(500,150));
	if(!isExpand){//默认不展开
		this.setSize(555, 175);
	}else{ //isExpand为true，则自动展开
		this.setSize(555, 355);
	}
	
	this.getContentPane().setLayout(new BorderLayout()); //
	sb.append(errorMsg+"\r\n");
	//imageicon=new ImageIcon("c:/error1.jpg");   
	JPanel jp_img=new JPanel();
	jp_img.setLayout(new BorderLayout());
	imageicon=UIUtil.getImage("error.jpg");
/*	if (imageicon.getIconWidth() != 555 || imageicon.getIconHeight() != 70) { //如果美工做的图片大小不是我想要的735*70,则进行拉伸处理!
		imageicon = new ImageIcon(tbUtil.getImageScale(imageicon.getImage(), 555, 70)); //
	}*/
	jl_pic = new JLabel(imageicon,2);  //显示图片
	//jl_pic.setBorder(BorderFactory.createLineBorder(Color.black));
	Font font_str=new Font("",Font.PLAIN,13);
	String label_text= new TBUtil().getSysOptionStringValue("远程报错介绍语", "很抱歉，出错了，请将错误详细信息发送给我们，帮助我们改进系统!");
	jl_str1= new JLabel(label_text);//显示错误信息
	jl_str1.setFont(font_str);
	jl_str1.setVisible(true);
	//jl_str1.setBorder(BorderFactory.createLineBorder(Color.red));
	jl_str2=new LinkLabel("详细错误报告");//显示错误信息链接
	jl_str2.setFont(font_str);
	jp_img.add(jl_pic,BorderLayout.NORTH);
	jp_img.add(jl_str1,BorderLayout.WEST);
	//jp_img.add(new JButton("测试"),BorderLayout.WEST);
	jp_img.add(jl_str2,BorderLayout.EAST);
	
	textArea_message = new JTextArea(10,40); //
	textArea_message.append(sb.toString());
	//textArea_message.setFont(f)
	//textArea_message.setPreferredSize(new Dimension(525,180));
	//textArea_message.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 5));
	
	textArea_message.setFont(font_str);
	textArea_message.setLineWrap(true); //
	textArea_message.setWrapStyleWord(true);
	textArea_message.setEditable(false);//设置不可编辑
	scroll_msg= new JScrollPane(textArea_message); // 错误信息加到滚动条中
	scroll_msg.setPreferredSize(new Dimension(535,180));
	scroll_msg.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 10, Color.white));
	scroll_msg.setVisible(false);//默认不显示
	
	this.getContentPane().add(jp_img,BorderLayout.NORTH);//图片面板
	this.getContentPane().add(scroll_msg,BorderLayout.CENTER);//错误信息以及错误信息面板
	this.getContentPane().add(getSouthPanel(false), BorderLayout.SOUTH); //
	this.setVisible(true);
}
/**
 * 
 * @param _parent当前容器，errorMsg是显示的错误信息，
 * @param isExpand是否自动展开错误信息为true则展开，如果自动展开则不显示详细错误报告链接
 * 
 */	
public ShowRemoteExDialog(Container _parent,String errorMsg,boolean isExpend){
	super(_parent, "发生错误");
	this.isExpand=isExpend;
	this.isShowLink=true;  //如果自动显示错误信息，则不显示超链接按钮
	this.setMinimumSize(new Dimension(500,150));
	if(!isExpand){//默认不展开
		this.setSize(555, 175);
	}else{ //isExpand为true，则自动展开
		this.setSize(555, 355);
	}
	sb.append("错误信息如下所示："+"\r\n");
	sb.append(errorMsg+"\r\n");
	this.getContentPane().setLayout(new BorderLayout()); //
	//imageicon=new ImageIcon("c:/error.jpg");   
	JPanel jp_img=new JPanel();
	jp_img.setLayout(new BorderLayout());
	imageicon=UIUtil.getImage("error.jpg");
/*	if (imageicon.getIconWidth() != 555 || imageicon.getIconHeight() != 70) { //如果美工做的图片大小不是我想要的735*70,则进行拉伸处理!
		imageicon = new ImageIcon(tbUtil.getImageScale(imageicon.getImage(), 555, 70)); //
	}*/
	jl_pic = new JLabel(imageicon,2);  //显示图片
	//jl_pic.setBorder(BorderFactory.createLineBorder(Color.black));
	Font font_str=new Font("",Font.PLAIN,13);
	String label_text= new TBUtil().getSysOptionStringValue("远程报错介绍语", "很抱歉，出错了，请将错误详细信息发送给我们，帮助我们改进系统!");
	jl_str1= new JLabel(label_text);//显示错误信息
	jl_str1.setFont(font_str);
	jl_str1.setVisible(true);
	//jl_str1.setBorder(BorderFactory.createLineBorder(Color.red));
	jl_str2=new LinkLabel("详细错误报告");//显示错误信息链接
	jl_str2.setFont(font_str);
	jp_img.add(jl_pic,BorderLayout.NORTH);
	jp_img.add(jl_str1,BorderLayout.WEST);
	//jp_img.add(new JButton("测试"),BorderLayout.WEST);
	if(!isShowLink){  //默认不显示超链接
		jp_img.add(jl_str2,BorderLayout.EAST);
	}
	
	textArea_message = new JTextArea(10,40); //
	textArea_message.append(sb.toString());
	//textArea_message.setFont(f)
	//textArea_message.setPreferredSize(new Dimension(525,180));
	//textArea_message.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 5));
	
	textArea_message.setFont(font_str);
	textArea_message.setLineWrap(true); //
	textArea_message.setWrapStyleWord(true);
	textArea_message.setEditable(false);//设置不可编辑
	scroll_msg= new JScrollPane(textArea_message); // 错误信息加到滚动条中
	scroll_msg.setPreferredSize(new Dimension(535,180));
	scroll_msg.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 10, Color.white));
	scroll_msg.setVisible(true);//默认不显示
	
	this.getContentPane().add(jp_img,BorderLayout.NORTH);//图片面板
	this.getContentPane().add(scroll_msg,BorderLayout.CENTER);//错误信息以及错误信息面板
	this.getContentPane().add(getSouthPanel(true), BorderLayout.SOUTH); //
	this.setVisible(true);
}
	private JPanel getSouthPanel(boolean isShowSave) {
		JPanel panel = new JPanel(); //
		panel.setLayout(new FlowLayout()); //
		btn_copy = new JButton("保存"); //
		btn_copy.setPreferredSize(new Dimension(75, 20)); //
		btn_copy.addActionListener(this); //
		if(!isShowSave){
			btn_copy.setVisible(false);
		}
		btn_close = new JButton("关闭"); //
		btn_close.setPreferredSize(new Dimension(75, 20)); //
		btn_close.addActionListener(this); //

		panel.add(btn_copy); //
		panel.add(btn_close); //
		return panel;
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_copy) {
			String str_text_1 = textArea_message.getText(); //
			String str_msg = "[" + ClientEnvironment.getCurrLoginUserVO().getCode() + "," + ClientEnvironment.getCurrLoginUserVO().getName() + "]于[" + new TBUtil().getCurrTime() + "]拷贝的远程异常信息:\r\n"; //
			str_msg = str_msg+sb.toString() + "\r\n\r\n"; //服务器端堆栈
			//str_msg = str_msg + "已成功拷贝异常信息,请发至邮箱xuchanghua@pushworld.com.cn\r\n\r\n"; //
/*			StringSelection strcopy = new StringSelection(str_msg); //
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(strcopy, null); //
			JOptionPane.showMessageDialog(this, "已成功将异常信息拷贝至系统剪贴板,请将其粘贴到一个文本文件中,然后发送给系统提供商以供分析."); //
*/
			String date1= new TBUtil().getCurrDate(false, false);
			String fileName="";
			SaveToLocal(str_msg, date1+"系统错误日志.txt");//默认保存为.txt格式	
		} else if (e.getSource() == btn_close) {
			this.dispose(); //
		}
	}
//保存至本地，把错误内容保存至.txt文件中	
private void SaveToLocal(String str_msg, String fileName) {
	JFileChooser chooser = new JFileChooser();
	try {
		if(ClientEnvironment.str_downLoadFileDir == null || "".equals(ClientEnvironment.str_downLoadFileDir)) {
			ClientEnvironment.str_downLoadFileDir = "C://";
		}
		File f = new File(new File(ClientEnvironment.str_downLoadFileDir + fileName).getCanonicalPath());
		chooser.setSelectedFile(f);
	} catch (Exception ex) {
		ex.printStackTrace();
	}
	int li_rewult = chooser.showSaveDialog(ShowRemoteExDialog.this);
	if (li_rewult == JFileChooser.APPROVE_OPTION) {
		File chooseFile = chooser.getSelectedFile();
		if (chooseFile != null) {
			ClientEnvironment.str_downLoadFileDir = chooseFile.getParent();
			String str_pathdir = chooseFile.getParent(); //
			if (str_pathdir.endsWith("\\")) {
				str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1); //
			}
			FileOutputStream fo = null;
			try {
				byte[] date =str_msg.getBytes();
				fo = new FileOutputStream(chooseFile);
				fo.write(date);
				MessageBox.show(ShowRemoteExDialog.this, "保存成功!");
				return;
			} catch (Exception e) {
				if(fo != null) {
					try {
						fo.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				e.printStackTrace();
			} finally {
				if(fo != null) {
					try {
						fo.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			MessageBox.show(ShowRemoteExDialog.this, "保存文件失败!");
		}
	}
}
//标签加链接变颜色
class LinkLabel extends JLabel{
	private String text;
	private boolean isSupported;
	public LinkLabel(String text){
		this.text=text;
		setText(false);
		addMouseListener(new MouseAdapter(){
			public void mouseEntered(MouseEvent e){
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			public void mouseClicked(MouseEvent e){
				try{
					setText(true);
					if(!isshow){
						scroll_msg.setVisible(true);
						scroll_msg.setEnabled(false);//不可编辑
						isshow=true;
						ShowRemoteExDialog.this.setSize(ShowRemoteExDialog.this.getWidth(), 355);
						btn_copy.setVisible(true);
					}else{
						scroll_msg.setVisible(false);
						scroll_msg.setEnabled(false);//不可编辑
						isshow=false;
						//ShowRemoteExDialog.this.setSize(555, 175);
						ShowRemoteExDialog.this.setSize(ShowRemoteExDialog.this.getWidth(), 175);
						btn_copy.setVisible(false);
					}
					//ShowRemoteExDialog.this.getSouthPanel().updateUI();
				}catch(Exception ex){
				}
			}
		});
	}
	private void setText(boolean b){
		if(!b){
			setText("<html><font color=blue><u>" + text);
		}else{
			setText("<html><font color=red><u>" + text);
		}
	}
}
	
	public static void main(String args[]){
		System.out.println("1111111111111111111");
		Frame ff1=new Frame();
		WLTRemoteException we=new WLTRemoteException("111222234ertert2222222222");
		//ShowRemoteExDialog sre=new ShowRemoteExDialog(ff1, "awerweeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
		//ShowRemoteExDialog sre2=new ShowRemoteExDialog(ff1, we);
		ShowRemoteExDialog sre=new ShowRemoteExDialog(ff1, "awerweeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee",true);
	}	
}
