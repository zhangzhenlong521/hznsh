package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTTextArea;

/**
 * 模板引用分析 【杨科/2013-03-04】
 */

public class TemplateQuoteWKPanel extends AbstractWorkPanel implements ActionListener {
	private WLTButton btn_quote; 
	private JTextField tf_templatecode;
	private JTextField tf_src;
	private WLTTextArea jta_quote;
	private HashMap hm = new HashMap();
	
	public TemplateQuoteWKPanel(){
		initialize();
	}

	public void initialize() {
		JLabel label_templatecode = new JLabel("模板编码："); 
		label_templatecode.setHorizontalAlignment(SwingConstants.RIGHT); 
		label_templatecode.setPreferredSize(new Dimension(100, 20)); 
		
		//模板编码
		tf_templatecode = new JTextField();
		tf_templatecode.setPreferredSize(new Dimension(250, 20));
		
		JLabel label_src = new JLabel("项目路径："); 
		label_src.setHorizontalAlignment(SwingConstants.RIGHT); 
		label_src.setPreferredSize(new Dimension(100, 20)); 
		
		//项目源码路径
		tf_src = new JTextField();
		tf_src.setPreferredSize(new Dimension(150, 20));
		if(System.getProperty("PROJECT_SRC")!=null){
			tf_src.setText(System.getProperty("PROJECT_SRC"));
		}
		
		btn_quote = new WLTButton("查找模板引用"); 
		btn_quote.addActionListener(this); 

		JPanel toppanel = new JPanel();
		toppanel.setLayout(new FlowLayout(FlowLayout.LEFT));  
		toppanel.add(label_templatecode);
		toppanel.add(tf_templatecode);
		toppanel.add(label_src);
		toppanel.add(tf_src);
		toppanel.add(btn_quote);
		
		JLabel label_quote = new JLabel("模板引用："); 
		label_quote.setPreferredSize(new Dimension(100, 20));
		label_quote.setHorizontalAlignment(SwingConstants.RIGHT); 
		label_quote.setVerticalAlignment(SwingConstants.TOP); 
		
		jta_quote = new WLTTextArea(10, 10);
		jta_quote.setLineWrap(true);
		jta_quote.setForeground(LookAndFeel.inputforecolor_enable); 
		jta_quote.setBackground(LookAndFeel.inputbgcolor_enable); 
		
		JScrollPane jsp = new JScrollPane();
		jsp.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, LookAndFeel.compBorderLineColor)); 
		jsp.setPreferredSize(new Dimension(800, 400));
		jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		jsp.getViewport().add(jta_quote);
		
		JPanel bottompanel   = new JPanel();
		bottompanel.setLayout(new FlowLayout(FlowLayout.LEFT));  
		bottompanel.add(label_quote);
		bottompanel.add(jsp);
		
		JPanel panel   = new JPanel();
		panel.setLayout(new BorderLayout());  
		panel.add(toppanel, BorderLayout.NORTH);
		panel.add(bottompanel, BorderLayout.CENTER);

		this.add(panel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_quote) {
			String templatecode = tf_templatecode.getText();
			if(templatecode==null||templatecode.equals("")){
				MessageBox.show(this, "请输入模板编码!");
				return;
			}
			String src = tf_src.getText();
			if(src==null||src.equals("")){
				MessageBox.show(this, "请输入项目地址!");
				return;
			}else{
				File dir = new File(src);
				if(!dir.exists() || !dir.isDirectory()){
					MessageBox.show(this, "项目地址不正确!");
					return;
				}
			}
			
			hm = new HashMap();
			FileSearch(tf_src.getText());
			jta_quote.setText(getTemplatecodeClass()+"\r\n\r\n"+getMenu());
		}	
	}
	
	public void FileSearch(String packagename){
		File dir = new File(packagename);
		if(!dir.exists() || !dir.isDirectory()){
			return;
		}
		
		File[] dirfiles = dir.listFiles(new FileFilter(){
			public boolean accept(File file){
				return (file.isDirectory()||(file.getName().endsWith("java")));
			}
		});
		
		for(File file : dirfiles){
			if(file.isDirectory()){
				FileSearch(file.getAbsolutePath());
			}else{
				FileSearchStr(file.getAbsolutePath());
			}
		}
	}
	
	public void FileSearchStr(String filename){
		StringBuilder strb = new StringBuilder();
		try {
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			while(true){
				String line = br.readLine();
				if(line == null){
					break;
				}
				strb.append(line);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String src = tf_src.getText();
		if(src.lastIndexOf("\\")==(src.length()-1)){
			filename = filename.substring(tf_src.getText().length(), filename.length());
		}else{
			filename = filename.substring(tf_src.getText().length()+1, filename.length());
		}
		filename = filename.replace("\\", ".");
		
		String templatecode = "\""+tf_templatecode.getText().trim()+"\"";
		String temp = strb.toString();	
		int searchcount = search(templatecode, temp);
		if(searchcount>0){
			hm.put(filename, searchcount);
		}
	}
	
	public String getTemplatecodeClass(){
		StringBuilder helpinfo = new StringBuilder();
		//helpinfo.append(">>>>>模板编码【"+tf_templatecode.getText()+"】在类中的引用情况如下：\r\n");
		helpinfo.append(">>>>>在类中的引用情况如下：\r\n");
		String[] arr = (String[]) hm.keySet().toArray(new String[0]);
		for(int i = 0; i < arr.length; i++){
			helpinfo.append("  "+(i+1)+" 【"+arr[i]+"】中引用【"+hm.get(arr[i])+"】次\r\n");
		}
		return helpinfo.toString();
	}
	
	public String getMenu() {
		HashVO[] hvs = null;
		try {
			hvs = UIUtil.getHashVoArrayByDS(null,
			    "select id,name,parentmenuid_name,usecmdtype,command,command2,command3 from vi_menu");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		StringBuilder code_menu = new StringBuilder();
		code_menu.append(">>>>>在菜单中的引用情况如下：\r\n");
		StringBuilder class_menu = new StringBuilder();
		class_menu.append(">>>>>关联到的类在菜单中的引用情况如下：\r\n");
		StringBuilder menu_all = new StringBuilder();
		menu_all.append(">>>>>在菜单中的引用情况如下（包括通过类关联到的）：\r\n");
		StringBuilder menu_quote = new StringBuilder();
		menu_quote.append(">>>>>在菜单中的引用情况如下（包括通过类关联到的，但只包括实际启用的菜单）：\r\n");

		String templatecode = "\""+tf_templatecode.getText().trim()+"\"";
		if(hvs!=null){
			for (int i = 0; i < hvs.length; i++) { 
				String menu = "  >>>菜单名：【"+hvs[i].getStringValue("name")+"】"+
							  "父菜单名：【"+hvs[i].getStringValue("parentmenuid_name")+"】"+
							  "菜单ID：【"+hvs[i].getStringValue("id")+"】"+
							  "启用类型：【"+hvs[i].getStringValue("usecmdtype")+"】\r\n";
				
				if(hvs[i].getStringValue("command")==null){
					hvs[i].setAttributeValue("command", "");
				}
				if(hvs[i].getStringValue("command2")==null){
					hvs[i].setAttributeValue("command2", "");
				}
				if(hvs[i].getStringValue("command3")==null){
					hvs[i].setAttributeValue("command3", "");
				}
				
				int searchcount1 = search(templatecode, hvs[i].getStringValue("command"));
				int searchcount2 = search(templatecode, hvs[i].getStringValue("command2"));
				int searchcount3 = search(templatecode, hvs[i].getStringValue("command3"));
				if(searchcount1>0||searchcount2>0||searchcount3>0){
					code_menu.append(menu);
					if(searchcount1>0){
						code_menu.append("    >菜单配置1：引用次数【"+searchcount1+"】\r\n");
					}
					if(searchcount2>0){ 
						code_menu.append("    >菜单配置2：引用次数【"+searchcount2+"】\r\n");
					}
					if(searchcount3>0){
						code_menu.append("    >菜单配置3：引用次数【"+searchcount3+"】\r\n");
					}
				}
				
				int mark = 0;
				String[] arr = (String[]) hm.keySet().toArray(new String[0]);
				int searchcount11_all = 0;
				int searchcount22_all = 0;
				int searchcount33_all = 0;
				for(int j = 0; j < arr.length; j++){
					String classname = arr[j].substring(0,arr[j].length()-5);;
					int searchcount11 = search(classname, hvs[i].getStringValue("command"));
					int searchcount22 = search(classname, hvs[i].getStringValue("command2"));
					int searchcount33 = search(classname, hvs[i].getStringValue("command3"));
					searchcount11_all = searchcount11_all+searchcount11;
					searchcount22_all = searchcount22_all+searchcount22;
					searchcount33_all = searchcount33_all+searchcount33;
					if(searchcount11>0||searchcount22>0||searchcount33>0){
						if(mark == 0){
							class_menu.append(menu);
							mark = 1;						
						}
						if(searchcount11>0){
							class_menu.append("    >菜单配置1：引用次数【"+searchcount11+"】关联类【"+classname+"】\r\n");
						}
						if(searchcount22>0){
							class_menu.append("    >菜单配置2：引用次数【"+searchcount22+"】关联类【"+classname+"】\r\n");
						}
						if(searchcount33>0){
							class_menu.append("    >菜单配置3：引用次数【"+searchcount33+"】关联类【"+classname+"】\r\n");
						}
					}
				}
				
				if(searchcount11_all>1||searchcount22_all>1||searchcount33_all>1){
					class_menu.append("\r\n     ---模板编码【"+tf_templatecode.getText()+"】关联到的类在菜单中的引用情况总计---");
					class_menu.append(menu);
					if(searchcount11_all>0){
						class_menu.append("    >菜单配置1：总计引用次数【"+searchcount11_all+"】\r\n");
					}
					if(searchcount22_all>0){
						class_menu.append("    >菜单配置2：总计引用次数【"+searchcount22_all+"】\r\n");
					}
					if(searchcount33_all>0){
						class_menu.append("    >菜单配置3：总计引用次数【"+searchcount33_all+"】\r\n");
					}
				}
				
				if((searchcount11_all+searchcount1)>0||(searchcount22_all+searchcount2)>0||(searchcount33_all+searchcount3)>0){
					menu_all.append(menu);
					if((searchcount11_all+searchcount1)>0){
						menu_all.append("    >菜单配置1：【"+hvs[i].getStringValue("command")+"】\r\n    总计引用次数【"+(searchcount11_all+searchcount1)+"】\r\n");
					}
					if((searchcount22_all+searchcount2)>0){
						menu_all.append("    >菜单配置2：【"+hvs[i].getStringValue("command2")+"】\r\n    总计引用次数【"+(searchcount22_all+searchcount2)+"】\r\n");
					}
					if((searchcount33_all+searchcount3)>0){
						menu_all.append("    >菜单配置3：【"+hvs[i].getStringValue("command3")+"】\r\n    总计引用次数【"+(searchcount33_all+searchcount3)+"】\r\n");
					}
				}
				
				if((searchcount11_all+searchcount1)>0||(searchcount22_all+searchcount2)>0||(searchcount33_all+searchcount3)>0){
					if((searchcount11_all+searchcount1)>0){
						if(hvs[i].getStringValue("usecmdtype").equals("1")){
							if(mark == 0){
								menu_quote.append(menu);
								mark = 1;						
							}
							menu_quote.append("    >菜单配置1：【"+hvs[i].getStringValue("command")+"】\r\n    总计引用次数【"+(searchcount11_all+searchcount1)+"】\r\n");	
						}
					}
					if((searchcount22_all+searchcount2)>0){
						if(hvs[i].getStringValue("usecmdtype").equals("2")){
							if(mark == 0){
								menu_quote.append(menu);
								mark = 1;						
							}
							menu_quote.append("    >菜单配置2：【"+hvs[i].getStringValue("command2")+"】\r\n    总计引用次数【"+(searchcount22_all+searchcount2)+"】\r\n");
						}
					}
					if((searchcount33_all+searchcount3)>0){
						if(hvs[i].getStringValue("usecmdtype").equals("3")){
							if(mark == 0){
								menu_quote.append(menu);
								mark = 1;						
							}
							menu_quote.append("    >菜单配置3：【"+hvs[i].getStringValue("command3")+"】\r\n    总计引用次数【"+(searchcount33_all+searchcount3)+"】\r\n");
						}
					}
				}
			}
		}
		//return code_menu.toString()+"\r\n\r\n"+class_menu.toString()+"\r\n\r\n"+menu_all.toString()+"\r\n\r\n"+menu_quote.toString();
		return code_menu.toString()+"\r\n\r\n"+class_menu.toString();
	}
	
	public int search(String stra, String strb){
		int count = 0;
		int index = 0;
		while(true){
			index = strb.indexOf(stra, index + 1);
			if(index > 0){
				count++;
			}else{
				break;
			}
		}
		return count;
	}

}
