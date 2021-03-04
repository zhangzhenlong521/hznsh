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
 * ��ʾԶ���쳣�ĶԻ���
 * @author xch
 *  yuanjiangxiao   20130301�����޸�
 */
public class ShowRemoteExDialog extends BillDialog implements ActionListener{

	private static final long serialVersionUID = 2148915094445394877L;
	private JButton btn_copy, btn_close = null;
	private JTextArea textArea_message = null; //����Ŀ�
	private ImageIcon imageicon=null;
	private JLabel jl_pic=null;
	private JLabel jl_str1=null;
	private LinkLabel jl_str2=null;
	JScrollPane scroll_msg=null;
	private TBUtil tbUtil = new TBUtil(); //
	private boolean isshow=false;
	StringBuffer sb=new StringBuffer();
	public boolean isShowLink=true;  //�Ƿ�Ĭ����ʾ�г�����
	public boolean isExpand=false;    //�Ƿ�Ĭ��չ������Ĵ�����Ϣ 

	public ShowRemoteExDialog(Container _parent, WLTRemoteException _ex) {
		super(_parent, "��������");
		if(!isExpand){//Ĭ�ϲ�չ��
			this.setSize(555, 175);
		}
		this.setMinimumSize(new Dimension(500,150));
		this.getContentPane().setLayout(new BorderLayout()); //
		sb.append(_ex.getMessage()+"\r\n");
		sb.append("�ͻ������쳣��ջ:\r\n");
		sb.append(_ex.getClientStackDetail());   //�ͻ����쳣��ջ
		sb.append("----------------------------------------------------------------------------------\r\n");
		sb.append("���������쳣��ջ:\r\n");
		sb.append(_ex.getServerStackDetail());	//���������쳣��ջ

		//imageicon=new ImageIcon("c:/error.jpg");   
		JPanel jp_img=new JPanel();
		jp_img.setLayout(new BorderLayout());
		imageicon=UIUtil.getImage("error.jpg");
		/*if (imageicon.getIconWidth() != 555 || imageicon.getIconHeight() != 70) { //�����������ͼƬ��С��������Ҫ��735*70,��������촦��!
			imageicon = new ImageIcon(tbUtil.getImageScale(imageicon.getImage(), 555, 70)); //
		}*/
		jl_pic = new JLabel(imageicon,2);  //��ʾͼƬ
		//jl_pic.setBorder(BorderFactory.createLineBorder(Color.black));
		Font font_str=new Font("",Font.PLAIN,13);
		String label_text= new TBUtil().getSysOptionStringValue("Զ�̱��������", "�ܱ�Ǹ�������ˣ��뽫������ϸ��Ϣ���͸����ǣ��������ǸĽ�ϵͳ!");
		jl_str1= new JLabel(label_text);//��ʾ������Ϣ
		jl_str1.setFont(font_str);
		jl_str1.setVisible(true);
		//jl_str1.setBorder(BorderFactory.createLineBorder(Color.red));
		jl_str2=new LinkLabel("��ϸ���󱨸�");//��ʾ������Ϣ����
		jl_str2.setFont(font_str);
		jp_img.add(jl_pic,BorderLayout.NORTH);
		jp_img.add(jl_str1,BorderLayout.WEST);
		//jp_img.add(new JButton("����"),BorderLayout.WEST);
		jp_img.add(jl_str2,BorderLayout.EAST);
		
		textArea_message = new JTextArea(10,40); //
		textArea_message.append(sb.toString());
		//textArea_message.setFont(f)
		//textArea_message.setPreferredSize(new Dimension(525,180));
		//textArea_message.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 5));
		
		textArea_message.setFont(font_str);
		textArea_message.setLineWrap(true); //
		textArea_message.setWrapStyleWord(true);
		textArea_message.setEditable(false);//���ò��ɱ༭
		scroll_msg= new JScrollPane(textArea_message); // ������Ϣ�ӵ���������
		scroll_msg.setPreferredSize(new Dimension(535,180));
		scroll_msg.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 10, Color.white));
		scroll_msg.setVisible(false);//Ĭ�ϲ���ʾ
		
		this.getContentPane().add(jp_img,BorderLayout.NORTH);//ͼƬ���
		this.getContentPane().add(scroll_msg,BorderLayout.CENTER);//������Ϣ�Լ�������Ϣ���
		this.getContentPane().add(getSouthPanel(false), BorderLayout.SOUTH); //
		this.setVisible(true); //
	}
/**
 * 
 * @param _parent   ������
 * @param errorMsg  �Զ��������Ϣ
 */
	
public ShowRemoteExDialog(Container _parent,String errorMsg){
	super(_parent, "��������");
	this.isExpand=false;
	this.isShowLink=true;  //����Զ���ʾ������Ϣ������ʾ�����Ӱ�ť
	//555,355
	this.setMinimumSize(new Dimension(500,150));
	if(!isExpand){//Ĭ�ϲ�չ��
		this.setSize(555, 175);
	}else{ //isExpandΪtrue�����Զ�չ��
		this.setSize(555, 355);
	}
	
	this.getContentPane().setLayout(new BorderLayout()); //
	sb.append(errorMsg+"\r\n");
	//imageicon=new ImageIcon("c:/error1.jpg");   
	JPanel jp_img=new JPanel();
	jp_img.setLayout(new BorderLayout());
	imageicon=UIUtil.getImage("error.jpg");
/*	if (imageicon.getIconWidth() != 555 || imageicon.getIconHeight() != 70) { //�����������ͼƬ��С��������Ҫ��735*70,��������촦��!
		imageicon = new ImageIcon(tbUtil.getImageScale(imageicon.getImage(), 555, 70)); //
	}*/
	jl_pic = new JLabel(imageicon,2);  //��ʾͼƬ
	//jl_pic.setBorder(BorderFactory.createLineBorder(Color.black));
	Font font_str=new Font("",Font.PLAIN,13);
	String label_text= new TBUtil().getSysOptionStringValue("Զ�̱��������", "�ܱ�Ǹ�������ˣ��뽫������ϸ��Ϣ���͸����ǣ��������ǸĽ�ϵͳ!");
	jl_str1= new JLabel(label_text);//��ʾ������Ϣ
	jl_str1.setFont(font_str);
	jl_str1.setVisible(true);
	//jl_str1.setBorder(BorderFactory.createLineBorder(Color.red));
	jl_str2=new LinkLabel("��ϸ���󱨸�");//��ʾ������Ϣ����
	jl_str2.setFont(font_str);
	jp_img.add(jl_pic,BorderLayout.NORTH);
	jp_img.add(jl_str1,BorderLayout.WEST);
	//jp_img.add(new JButton("����"),BorderLayout.WEST);
	jp_img.add(jl_str2,BorderLayout.EAST);
	
	textArea_message = new JTextArea(10,40); //
	textArea_message.append(sb.toString());
	//textArea_message.setFont(f)
	//textArea_message.setPreferredSize(new Dimension(525,180));
	//textArea_message.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 5));
	
	textArea_message.setFont(font_str);
	textArea_message.setLineWrap(true); //
	textArea_message.setWrapStyleWord(true);
	textArea_message.setEditable(false);//���ò��ɱ༭
	scroll_msg= new JScrollPane(textArea_message); // ������Ϣ�ӵ���������
	scroll_msg.setPreferredSize(new Dimension(535,180));
	scroll_msg.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 10, Color.white));
	scroll_msg.setVisible(false);//Ĭ�ϲ���ʾ
	
	this.getContentPane().add(jp_img,BorderLayout.NORTH);//ͼƬ���
	this.getContentPane().add(scroll_msg,BorderLayout.CENTER);//������Ϣ�Լ�������Ϣ���
	this.getContentPane().add(getSouthPanel(false), BorderLayout.SOUTH); //
	this.setVisible(true);
}
/**
 * 
 * @param _parent��ǰ������errorMsg����ʾ�Ĵ�����Ϣ��
 * @param isExpand�Ƿ��Զ�չ��������ϢΪtrue��չ��������Զ�չ������ʾ��ϸ���󱨸�����
 * 
 */	
public ShowRemoteExDialog(Container _parent,String errorMsg,boolean isExpend){
	super(_parent, "��������");
	this.isExpand=isExpend;
	this.isShowLink=true;  //����Զ���ʾ������Ϣ������ʾ�����Ӱ�ť
	this.setMinimumSize(new Dimension(500,150));
	if(!isExpand){//Ĭ�ϲ�չ��
		this.setSize(555, 175);
	}else{ //isExpandΪtrue�����Զ�չ��
		this.setSize(555, 355);
	}
	sb.append("������Ϣ������ʾ��"+"\r\n");
	sb.append(errorMsg+"\r\n");
	this.getContentPane().setLayout(new BorderLayout()); //
	//imageicon=new ImageIcon("c:/error.jpg");   
	JPanel jp_img=new JPanel();
	jp_img.setLayout(new BorderLayout());
	imageicon=UIUtil.getImage("error.jpg");
/*	if (imageicon.getIconWidth() != 555 || imageicon.getIconHeight() != 70) { //�����������ͼƬ��С��������Ҫ��735*70,��������촦��!
		imageicon = new ImageIcon(tbUtil.getImageScale(imageicon.getImage(), 555, 70)); //
	}*/
	jl_pic = new JLabel(imageicon,2);  //��ʾͼƬ
	//jl_pic.setBorder(BorderFactory.createLineBorder(Color.black));
	Font font_str=new Font("",Font.PLAIN,13);
	String label_text= new TBUtil().getSysOptionStringValue("Զ�̱��������", "�ܱ�Ǹ�������ˣ��뽫������ϸ��Ϣ���͸����ǣ��������ǸĽ�ϵͳ!");
	jl_str1= new JLabel(label_text);//��ʾ������Ϣ
	jl_str1.setFont(font_str);
	jl_str1.setVisible(true);
	//jl_str1.setBorder(BorderFactory.createLineBorder(Color.red));
	jl_str2=new LinkLabel("��ϸ���󱨸�");//��ʾ������Ϣ����
	jl_str2.setFont(font_str);
	jp_img.add(jl_pic,BorderLayout.NORTH);
	jp_img.add(jl_str1,BorderLayout.WEST);
	//jp_img.add(new JButton("����"),BorderLayout.WEST);
	if(!isShowLink){  //Ĭ�ϲ���ʾ������
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
	textArea_message.setEditable(false);//���ò��ɱ༭
	scroll_msg= new JScrollPane(textArea_message); // ������Ϣ�ӵ���������
	scroll_msg.setPreferredSize(new Dimension(535,180));
	scroll_msg.setBorder(BorderFactory.createMatteBorder(5, 5, 5, 10, Color.white));
	scroll_msg.setVisible(true);//Ĭ�ϲ���ʾ
	
	this.getContentPane().add(jp_img,BorderLayout.NORTH);//ͼƬ���
	this.getContentPane().add(scroll_msg,BorderLayout.CENTER);//������Ϣ�Լ�������Ϣ���
	this.getContentPane().add(getSouthPanel(true), BorderLayout.SOUTH); //
	this.setVisible(true);
}
	private JPanel getSouthPanel(boolean isShowSave) {
		JPanel panel = new JPanel(); //
		panel.setLayout(new FlowLayout()); //
		btn_copy = new JButton("����"); //
		btn_copy.setPreferredSize(new Dimension(75, 20)); //
		btn_copy.addActionListener(this); //
		if(!isShowSave){
			btn_copy.setVisible(false);
		}
		btn_close = new JButton("�ر�"); //
		btn_close.setPreferredSize(new Dimension(75, 20)); //
		btn_close.addActionListener(this); //

		panel.add(btn_copy); //
		panel.add(btn_close); //
		return panel;
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_copy) {
			String str_text_1 = textArea_message.getText(); //
			String str_msg = "[" + ClientEnvironment.getCurrLoginUserVO().getCode() + "," + ClientEnvironment.getCurrLoginUserVO().getName() + "]��[" + new TBUtil().getCurrTime() + "]������Զ���쳣��Ϣ:\r\n"; //
			str_msg = str_msg+sb.toString() + "\r\n\r\n"; //�������˶�ջ
			//str_msg = str_msg + "�ѳɹ������쳣��Ϣ,�뷢������xuchanghua@pushworld.com.cn\r\n\r\n"; //
/*			StringSelection strcopy = new StringSelection(str_msg); //
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(strcopy, null); //
			JOptionPane.showMessageDialog(this, "�ѳɹ����쳣��Ϣ������ϵͳ������,�뽫��ճ����һ���ı��ļ���,Ȼ���͸�ϵͳ�ṩ���Թ�����."); //
*/
			String date1= new TBUtil().getCurrDate(false, false);
			String fileName="";
			SaveToLocal(str_msg, date1+"ϵͳ������־.txt");//Ĭ�ϱ���Ϊ.txt��ʽ	
		} else if (e.getSource() == btn_close) {
			this.dispose(); //
		}
	}
//���������أ��Ѵ������ݱ�����.txt�ļ���	
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
				MessageBox.show(ShowRemoteExDialog.this, "����ɹ�!");
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
			MessageBox.show(ShowRemoteExDialog.this, "�����ļ�ʧ��!");
		}
	}
}
//��ǩ�����ӱ���ɫ
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
						scroll_msg.setEnabled(false);//���ɱ༭
						isshow=true;
						ShowRemoteExDialog.this.setSize(ShowRemoteExDialog.this.getWidth(), 355);
						btn_copy.setVisible(true);
					}else{
						scroll_msg.setVisible(false);
						scroll_msg.setEnabled(false);//���ɱ༭
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
