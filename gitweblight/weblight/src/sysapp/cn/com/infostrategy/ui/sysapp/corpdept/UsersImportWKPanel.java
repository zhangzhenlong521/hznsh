package cn.com.infostrategy.ui.sysapp.corpdept;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.VFlowLayoutPanel;
import cn.com.infostrategy.ui.report.cellcompent.ExcelUtil;

/**
 * 人员_机构_角色批量导入 【杨科/2013-05-20】
 */

public class UsersImportWKPanel extends AbstractWorkPanel implements ActionListener{
	private WLTButton btn_file,btn_file_,btn_excel,btn_help,btn_compare,btn_compare_,btn_data,btn_data_upd,btn_data_del,btn_import,btn_upd,btn_del,btn_export;
	private JTextField tf_file;
	private VFlowLayoutPanel vflowPanel = null;
	private JPanel comparePanel = null;
	private JPanel dataPanel = null;
	private JPanel data_updPanel = null;
	private JPanel data_delPanel = null;
	private HashMap hm_user = new HashMap();
	private HashMap hm_dept = new HashMap();
	private HashMap hm_role = new HashMap();
	private ArrayList al_user = new ArrayList();
	private ArrayList al_user_upd_del = new ArrayList();
	private ArrayList al_user_error = new ArrayList();
	private JCheckBox[] checkBoxs_user = new JCheckBox[0];
	private JCheckBox[] checkBoxs_dept = new JCheckBox[0];
	private JCheckBox[] checkBoxs_role = new JCheckBox[0];
	private JCheckBox[] checkBoxs_user_ = null;
	private JCheckBox[] checkBoxs_dept_ = null;
	private JCheckBox[] checkBoxs_role_ = null;
	private Boolean importmark = false;
	private Boolean updmark = false;
	private Boolean delmark = false;
	private String[][] strs_user = new String[][]{{"登录名","姓名","性别","身份证号","人员职位","出生日期","学历","专业","联系电话","手机","Email","状态","密码","管理密码"}, 
			{"code","name","sex","idcardno","staff","birthyear","degreen","specialty","telephone","mobile","email","status","pwd","adminpwd"}};
	private String[][] strs_dept = new String[][]{{"机构组","人员排序"}, {"userdept","seq"}};
	private String[][] strs_role = new String[][]{{"角色组"}, {"roleid"}};
	private HashMap hm_excel = new HashMap();
	private BillListPanel compareListPanel = null;

	public UsersImportWKPanel(){
		initialize();
	}

	public void initialize() {
		this.setLayout(new BorderLayout(0, 0));
		vflowPanel = getFilePanel();
		this.add(vflowPanel, BorderLayout.CENTER); 
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_file) {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("请选择文件目录");
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int flag = chooser.showSaveDialog(this);
			if (flag == 1 || chooser.getSelectedFile() == null) {
				return;
			}
			String str_path = chooser.getSelectedFile().getAbsolutePath(); 
			if (str_path == null) {
				return;
			}
			
			tf_file.setText(str_path);
			setCheckBoxPanel_Value();
		} else if (e.getSource() == btn_file_) {
			this.removeAll();
			this.add(vflowPanel);
			this.updateUI();
		} else if (e.getSource() == btn_excel) {
			if(MessageBox.showConfirmDialog(this, CheckBoxPanel(strs_user,strs_dept,strs_role), 400, 300)==0){
				onDownLoadTemplet(getCheckBox());
			}
		} else if (e.getSource() == btn_compare) {
			importmark = false;
			updmark = false;
			delmark = false;
			String tf_file_text = tf_file.getText();
			if(tf_file_text==null||"".equals(tf_file_text)){
				MessageBox.show(this, "请选择文件!");
				return;
			}
			
			hm_excel.clear();
			HashMap hm_excel_ = new HashMap();
			for(int i=0; i<strs_user[0].length; i++){
				hm_excel_.put(strs_user[0][i], strs_user[1][i]);
			}
			for(int i=0; i<strs_dept[0].length; i++){
				hm_excel_.put(strs_dept[0][i], strs_dept[1][i]);
			}
			for(int i=0; i<strs_role[0].length; i++){
				hm_excel_.put(strs_role[0][i], strs_role[1][i]);
			}
			
			//选择的文件数据
			String[][] excelFileData = new ExcelUtil().getExcelFileData(tf_file.getText(), 0);
			
			if(excelFileData.length<1){
				MessageBox.show(this, "EXCEL数据为空!");
				return;
			}
			
			String excel_0_error = "";
			String excel_0_all = "";
			for(int i = 0; i < excelFileData[0].length; i++){
				String excel_0 = excelFileData[0][i];
				if(excel_0!=null&&!excel_0.equals("")){
					if(hm_excel_.containsKey(excel_0)){
						excel_0_all += excel_0+";";
						hm_excel.put(excel_0, hm_excel_.get(excel_0));
					}else{
						excel_0_error += excel_0+";";
					}				
				}
			}
			
			if(!excel_0_error.equals("")){
				MessageBox.show(this, "EXCEL数据中 "+excel_0_error+" 列数据未匹配到模板数据!");
			}
			
			if(excel_0_all.equals("")){
				MessageBox.show(this, "EXCEL数据中所有列数据均未匹配到模板数据!请检查数据，校验停止");
				return;
			}
			
			if(!hm_excel.containsKey("登录名")){
				MessageBox.show(this, "EXCEL数据中没有 登录名 人员关键列!请检查数据，校验停止");
				return;
			}
			
			if(!hm_excel.containsKey("机构组")){
				String dept = "";
				for(int i=0; i<strs_dept[0].length; i++){
					if(hm_excel.containsKey(strs_dept[0][i])){
						dept += strs_dept[0][i]+";";
						hm_excel.remove(strs_dept[0][i]);
					}	
				}
				if(!dept.equals("")){
					MessageBox.show(this, "EXCEL数据中没有 机构组 机构关键列! "+dept+" 列数据也被清除");
				}
			}
			
			if(!hm_excel.containsKey("角色组")){
				String role = "";
				for(int i=0; i<strs_role[0].length; i++){
					if(hm_excel.containsKey(strs_role[0][i])){
						role += strs_role[0][i]+";";
						hm_excel.remove(strs_role[0][i]);
					}	
				}
				if(!role.equals("")){
					MessageBox.show(this, "EXCEL数据中没有 角色组 机构关键列! "+role+" 列数据也被清除");
				}
			}
			
			HashMap hm_table = new HashMap();
			try {
				TableDataStruct tableDataStruct = UIUtil.getTableDataStructByDS(null, "select * from pub_user where 1=2");
				String[] data = tableDataStruct.getHeaderName();
				for(int i=0;i<data.length;i++ ){
					hm_table.put(data[i].toLowerCase(), null);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			String table = "";
			for(int i=0; i<strs_user[1].length; i++){
				if(!hm_table.containsKey(strs_user[1][i])){
					if(hm_excel.containsKey(strs_user[0][i])){
						table += strs_user[0][i]+";";
						hm_excel.remove(strs_user[0][i]);	
					}
				}
			}
			
			if(!table.equals("")){
				MessageBox.show(this, "PUB_USER表中没有 "+table+" 列数据,故也被清除");
			}
			
			final HashVO[] hvos = new HashVO[excelFileData.length-1];
			for(int i = 1; i < excelFileData.length; i++){
				HashVO hvo = new HashVO();
				for(int j = 0; j < excelFileData[i].length; j++){
					if(excelFileData[0][j]!=null&&!excelFileData[0][j].equals("")&&hm_excel.containsKey(excelFileData[0][j])){
						hvo.setAttributeValue(excelFileData[0][j], excelFileData[i][j]); 	
					}
				}
				hvos[i-1] = hvo;
			}
			
			if(hvos.length<=0){
				MessageBox.show(this, "EXCEL数据为空!");
				return;
			}
			
			//getComparePanel(hvos);
			
			new SplashWindow(this, new AbstractAction() {
				public void actionPerformed(ActionEvent e) {
					getComparePanel(hvos);
				}
			}, 366, 366);
			
			this.removeAll();
			this.add(comparePanel);
			this.updateUI();
		} else if (e.getSource() == btn_compare_) {
			this.removeAll();
			this.add(comparePanel);
			this.updateUI();
		} else if (e.getSource() == btn_data) {
			if(al_user.size()<=0){
				MessageBox.show(this, "数据校验后,可以导入人员数量为0!");
				return;
			}
			
			getDataPanel();
			
			this.removeAll();
			this.add(dataPanel);
			this.updateUI();
		}else if (e.getSource() == btn_data_upd) {
			if(al_user_upd_del.size()<=0){
				MessageBox.show(this, "数据校验后,可以更新人员数量为0!");
				return;
			}
			
			getData_UpdPanel();
			
			this.removeAll();
			this.add(data_updPanel);
			this.updateUI();
		} else if (e.getSource() == btn_data_del) {
			if(al_user_upd_del.size()<=0){
				MessageBox.show(this, "数据校验后,可以删除人员数量为0!");
				return;
			}
			
			getData_DelPanel();
			
			this.removeAll();
			this.add(data_delPanel);
			this.updateUI();
		} else if (e.getSource() == btn_import) {
			if(importmark){
				MessageBox.show(this, "数据已导入，请重新校验!");
				return;
			}

			if(MessageBox.confirm("您确定要导入数据预览列表中数据吗?")){
				//dataImport();
				new SplashWindow(this, new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						dataImport();
					}
				}, 366, 366);
			}
		} else if (e.getSource() == btn_upd) {
			if(updmark){
				MessageBox.show(this, "数据已更新，请重新校验!");
				return;
			}
			if(delmark){
				MessageBox.show(this, "数据已删除，请重新校验!");
				return;
			}

			if(MessageBox.confirm("您确定要更新数据预览列表中数据吗?")){
				//dataUpd();
				new SplashWindow(this, new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						dataUpd();
					}
				}, 366, 366);
			}
		} else if (e.getSource() == btn_del) {
			if(delmark){
				MessageBox.show(this, "数据已删除，请重新校验!");
				return;
			}
			if(updmark){
				MessageBox.show(this, "数据已更新，请重新校验!");
				return;
			}

			if(MessageBox.confirm("您确定要删除数据预览列表中数据吗?")){
				//dataDel();
				new SplashWindow(this, new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						dataDel();
					}
				}, 366, 366);
			}
		} else if (e.getSource() == btn_export) {
			Pub_Templet_1VO tv = new Pub_Templet_1VO();
			tv.setTempletname("导出数据");
			compareListPanel.setTempletVO(tv);
			compareListPanel.exportToExcel();
		}
		
		if(checkBoxs_dept.length>0){
			if (e.getSource() == checkBoxs_dept[0]) {
				for(int i=1; i<checkBoxs_dept.length; i++){
					if(checkBoxs_dept[0].isSelected()){
						checkBoxs_dept[i].setEnabled(true);
					}else{
						checkBoxs_dept[i].setEnabled(false);
						checkBoxs_dept[i].setSelected(false);
					}
				}
			}			
		}

		if(checkBoxs_role.length>0){
			if (e.getSource() == checkBoxs_role[0]) {
				for(int i=1; i<checkBoxs_role.length; i++){
					if(checkBoxs_role[0].isSelected()){
						checkBoxs_role[i].setEnabled(true);
					}else{
						checkBoxs_role[i].setEnabled(false);
						checkBoxs_role[i].setSelected(false);
					}
				}
			}			
		}
	}
	
	private VFlowLayoutPanel getFilePanel(){
		JLabel label_file = new JLabel("文件路径："); 
		label_file.setHorizontalAlignment(SwingConstants.RIGHT); 
		label_file.setPreferredSize(new Dimension(100, 20)); 
		
		//文件路径
		tf_file = new JTextField();
		tf_file.setPreferredSize(new Dimension(400, 20));
		tf_file.setEditable(false);
		
		JLabel label_null = new JLabel("      "); 
		label_null.setHorizontalAlignment(SwingConstants.RIGHT); 
		label_null.setPreferredSize(new Dimension(50, 20)); 
		
		JLabel label_null_ = new JLabel("      "); 
		label_null_.setHorizontalAlignment(SwingConstants.RIGHT); 
		label_null_.setPreferredSize(new Dimension(350, 20)); 
		
		JLabel empty = new JLabel();
		empty.setText("<html><body><br><br><br><br><br><br></body></html>");
		
		btn_file = new WLTButton("文件选择");
		btn_excel = new WLTButton("模板下载");
		//btn_help = new WLTButton("帮助说明");
		btn_compare = new WLTButton("数据校验", UIUtil.getImage("zt_073.gif"));
		
		btn_file.addActionListener(this);
		btn_excel.addActionListener(this);
		//btn_help.addActionListener(this);
		btn_compare.addActionListener(this);

		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));  
		topPanel.add(label_null);
		topPanel.add(btn_file);
		topPanel.add(btn_excel);
		topPanel.add(label_null_);
		topPanel.add(btn_compare);
		
		JPanel ceterPanel = new JPanel();
		ceterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));  
		ceterPanel.add(label_file);
		ceterPanel.add(tf_file);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));  
		
		ArrayList al_compents = new ArrayList(); 
		al_compents.add(topPanel); 
		al_compents.add(ceterPanel); 
		al_compents.add(CheckBoxPanel_(strs_user,strs_dept,strs_role)); 
		al_compents.add(bottomPanel); 
		
		return new VFlowLayoutPanel(al_compents);
	}
	
    //下载模板
	public void onDownLoadTemplet(String[][] strs) {
		JFileChooser chooser = new JFileChooser();
		try {
			if(ClientEnvironment.str_downLoadFileDir == null || "".equals(ClientEnvironment.str_downLoadFileDir)) {
				ClientEnvironment.str_downLoadFileDir = "C://";
			}
			File f = new File(new File(ClientEnvironment.str_downLoadFileDir + "用户批量导入模板.xls").getCanonicalPath());
			chooser.setSelectedFile(f);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		int li_rewult = chooser.showSaveDialog(this);
		if (li_rewult == JFileChooser.APPROVE_OPTION) {
			File chooseFile = chooser.getSelectedFile();
			if (chooseFile != null) {
				ClientEnvironment.str_downLoadFileDir = chooseFile.getParent();
				String str_pathdir = chooseFile.getParent(); 
				if (str_pathdir.endsWith("\\")) {
					str_pathdir = str_pathdir.substring(0, str_pathdir.length() - 1); 
				}

				try {
					FileOutputStream fo = new FileOutputStream(chooseFile);
					HSSFWorkbook workbook = getWorkbook("用户", strs);
					workbook.write(fo);
					fo.close();
					MessageBox.show(this, "下载文件成功!");
					return;
				} catch (Exception e) {
					e.printStackTrace();
				} 
				MessageBox.show(this, "下载文件失败!");
			}
		}
	}
	
	//根据数组创建EXCEL
	public HSSFWorkbook getWorkbook(String name, String[][] strs){
	    HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
		workbook.setSheetName(0, name);
		HSSFRow row;
		HSSFCell cell;
		
		int[] cellwidth = null;
		if(strs.length>0){
			cellwidth=new int[strs[0].length];
		}

		for(int i=0;i<strs.length;i++){
			row = sheet.createRow(i);
			for(int j=0;j<strs[i].length;j++){
				cell = row.createCell(j);
        		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
        		cell.setCellType(HSSFCell.ENCODING_UTF_16);
	    		cell.setCellValue(strs[i][j]);	
	    		if(strs[i][j].length()>cellwidth[j]){
	    			cellwidth[j]=strs[i][j].length();
	    		}
			}
		}
        
		if(cellwidth!=null){
			for(int i=0;i<cellwidth.length;i++){
				sheet.setColumnWidth(i, (cellwidth[i]*512));
			}			
		}

		//锁定第一行
		sheet.createFreezePane(0, 1, 0, 1);
		return workbook;
	}
	
	private void getComparePanel(HashVO[] hvos){
		comparePanel = new JPanel();
		comparePanel.setLayout(new BorderLayout(0, 0));  
		
		JLabel label_null = new JLabel("      "); 
		label_null.setHorizontalAlignment(SwingConstants.RIGHT); 
		label_null.setPreferredSize(new Dimension(50, 20)); 
		
		JLabel label_null_ = new JLabel("      "); 
		label_null_.setHorizontalAlignment(SwingConstants.RIGHT); 
		label_null_.setPreferredSize(new Dimension(200, 20)); 
		
		btn_file_ = new WLTButton("返回", UIUtil.getImage("zt_072.gif"));
		btn_export = new WLTButton("导出");
		btn_data = new WLTButton("导入数据预览", UIUtil.getImage("zt_073.gif"));
		btn_data_upd = new WLTButton("更新数据预览", UIUtil.getImage("zt_073.gif"));
		btn_data_del = new WLTButton("删除数据预览", UIUtil.getImage("zt_073.gif"));
		
		btn_file_.addActionListener(this);
		btn_export.addActionListener(this);
		btn_data.addActionListener(this);
		btn_data_upd.addActionListener(this);
		btn_data_del.addActionListener(this);

		JPanel Panel = new JPanel();
		Panel.setLayout(new FlowLayout(FlowLayout.LEFT));  
		Panel.add(label_null);
		Panel.add(btn_file_);
		Panel.add(btn_export);
		Panel.add(label_null_);
		Panel.add(btn_data);
		Panel.add(btn_data_upd);
		Panel.add(btn_data_del);
		
		if(compareListPanel!=null){
			compareListPanel.removeAll();
		}
		
		compareListPanel = getCompareListPanel(hvos);

		comparePanel.add(compareListPanel, BorderLayout.CENTER);
		comparePanel.add(Panel, BorderLayout.NORTH);
		comparePanel.updateUI();
	}
	
	private BillListPanel getCompareListPanel(HashVO[] hvos){
		hm_user.clear();
		hm_dept.clear();
		hm_role.clear();
		al_user.clear();
		al_user_upd_del.clear();
		al_user_error.clear();
		
		try {
			//用户数据
			HashVO[] hvos_user = UIUtil.getHashVoArrayByDS(null, "select id,code from pub_user");
			for(int i=0;i<hvos_user.length;i++){
                String id = hvos_user[i].getStringValue("id"); 
                String code = hvos_user[i].getStringValue("code"); 
                if(code!=null&&!"".equals(code)){
                	hm_user.put(code,id);
                }
			}
			
			if(hm_excel.containsKey("机构组")){
				//部门数据
				HashVO[] hvos_dept = UIUtil.getHashVoArrayAsTreeStructByDS(null, " select * from pub_corp_dept ", "id", "parentid", null, null);
				for(int i=0;i<hvos_dept.length;i++){
	                String id = hvos_dept[i].getStringValue("id"); 
	                String parentpathnamelink1 = hvos_dept[i].getStringValue("$parentpathnamelink1"); 
	                if(parentpathnamelink1!=null&&!"".equals(parentpathnamelink1)){
	                	hm_dept.put(parentpathnamelink1,id);
	                }
				}			
			}
			
			if(hm_excel.containsKey("角色组")){
				//角色数据
				HashVO[] hvos_role = UIUtil.getHashVoArrayByDS(null, "select id,name from pub_role");
				for(int i=0;i<hvos_role.length;i++){
	                String id = hvos_role[i].getStringValue("id"); 
	                String name = hvos_role[i].getStringValue("name"); 
	                if(name!=null&&!"".equals(name)){
	                	hm_role.put(name,id);
	                }
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		HashMap hm_hvos = new HashMap();
		for(int i=0;i<hvos.length;i++){
			String usercode = hvos[i].getStringValue("登录名"); 
			if(usercode!=null&&!usercode.equals("")){
				if(hm_hvos.containsKey(usercode)){
					hm_hvos.put(usercode, "第"+(i+1)+"行,"+hm_hvos.get(usercode));
				}else{
					hm_hvos.put(usercode, "第"+(i+1)+"行");
				}			
			}
		}
		
		//登录名 机构组 角色组为空 用户名重复
		for(int i=0;i<hvos.length;i++){
			String reason = "";
			String usercode = hvos[i].getStringValue("登录名"); 
			String dept = hvos[i].getStringValue("机构组",""); 
			String role = hvos[i].getStringValue("角色组",""); 	
			if(usercode==null||usercode.equals("")){
				reason += "用户名为空;";
			}
			if(dept.equals("")){
				if(hm_excel.containsKey("机构组")){
					reason += "机构组为空;";		
				}
			}
			if(role.equals("")){
				if(hm_excel.containsKey("角色组")){
					reason += "角色组为空;";
				}
			}
			if(usercode!=null&&!usercode.equals("")){
				String hm_hvos_value = (String) hm_hvos.get(usercode);
				if(hm_hvos_value!=null&&!hm_hvos_value.equals("")&&hm_hvos_value.contains(",")){
					reason += hm_hvos_value+"用户名重复;";
				}
			}
			hvos[i].setAttributeValue("EXCEL校验失败原因", reason);
		}
		
		
		//匹配 用户-找出存在的/机构组 角色组-找出不存在的,以<>标记 追加 用户ID 机构组ID 角色组ID 行号
		for(int i=0;i<hvos.length;i++){
			String usercode = hvos[i].getStringValue("登录名"); 
			String usercodeid = "";
			if(hm_user.containsKey(usercode)){
				usercodeid = ""+hm_user.get(usercode);
			}
			
			String dept = hvos[i].getStringValue("机构组",""); 
			String role = hvos[i].getStringValue("角色组",""); 	
			
			String[] depts = dept.split(";");
			String dept_new = "";
			String deptid_new = "";
			if(hm_excel.containsKey("机构组")&&!"".equals(dept)){
				for(int j=0;j<depts.length;j++){
					if(hm_dept.containsKey(depts[j])){
						dept_new += depts[j]+";";
						deptid_new += hm_dept.get(depts[j])+";";
					}else{
						dept_new += "<"+depts[j]+">;";
						deptid_new += "<>;";
					}
				}
			}

			String[] roles = role.split(";");
			String role_new = "";
			String roleid_new = "";
			if(hm_excel.containsKey("角色组")&&!"".equals(role)){
				for(int j=0;j<roles.length;j++){
					if(hm_role.containsKey(roles[j])){
						role_new += roles[j]+";";
						roleid_new += hm_role.get(roles[j])+";";
					}else{
						role_new += "<"+roles[j]+">;";
						roleid_new += "<>;";
					}
				}		
			}
		
			if(!"".equals(dept_new)){
				hvos[i].setAttributeValue("机构组", dept_new.substring(0,dept_new.length()-1));
				hvos[i].setAttributeValue("机构组ID", deptid_new.substring(0,deptid_new.length()-1));
			}else{
				hvos[i].setAttributeValue("机构组", "");
				hvos[i].setAttributeValue("机构组ID", "");
			}
			
			if(!"".equals(role_new)){
				hvos[i].setAttributeValue("角色组", role_new.substring(0,role_new.length()-1));
				hvos[i].setAttributeValue("角色组ID", roleid_new.substring(0,roleid_new.length()-1));
			}else{
				hvos[i].setAttributeValue("角色组", "");
				hvos[i].setAttributeValue("角色组ID", "");
			}
			
			hvos[i].setAttributeValue("用户ID", usercodeid);
			hvos[i].setAttributeValue("行号", i);
		}
		
		//找出可导入人员数据
		for(int i=0;i<hvos.length;i++){
			String usercodeid = hvos[i].getStringValue("用户ID"); 
			String dept = hvos[i].getStringValue("机构组"); 
			String role = hvos[i].getStringValue("角色组"); 
			String reason = hvos[i].getStringValue("EXCEL校验失败原因"); 
			
			String DBreason = "";
			if(!"".equals(usercodeid)){
				DBreason += "用户名数据库存在;";
			}
			if(dept.contains("<")){
				DBreason += "机构组中某机构数据库中不存在;";
			}
			if(role.contains("<")){
				DBreason += "角色组中某角色数据库中不存在;";
			}
			
			hvos[i].setAttributeValue("数据库校验失败原因", DBreason);
			
			//用户存在的/机构组 角色组不存在的 校验失败
			if(!"".equals(usercodeid)||dept.contains("<")||role.contains("<")||!"".equals(reason)){
				al_user_error.add(hvos[i]);
			}else{
				al_user.add(hvos[i]);
			}
			
			if(DBreason.equals("用户名数据库存在;")&&"".equals(reason)){
				al_user_upd_del.add(hvos[i]);
			}
		}
		
		BillListPanel billListPanel = new BillListPanel(hvos);
		billListPanel.setItemsVisible(new String[]{"用户ID","机构组ID","角色组ID","行号"}, false);
		if(!hm_excel.containsKey("机构组")){
			billListPanel.setItemsVisible(new String[]{"机构组"}, false);
		}
		if(!hm_excel.containsKey("角色组")){
			billListPanel.setItemsVisible(new String[]{"角色组"}, false);
		}
		
		//标记有问题的 用户,机构组,角色组 校验失败
		for(int i=0;i<al_user_error.size();i++){
			HashVO hvo = (HashVO) al_user_error.get(i);
			if(hvo.getStringValue("机构组").contains("<")){
				billListPanel.setItemForeGroundColor("FF0000", hvo.getIntegerValue("行号"), "机构组");
			}
			if(hvo.getStringValue("角色组").contains("<")){
				billListPanel.setItemForeGroundColor("FF0000", hvo.getIntegerValue("行号"), "角色组");
			}
			if(!hvo.getStringValue("用户ID").equals("")){
				billListPanel.setItemForeGroundColor("FF0000", hvo.getIntegerValue("行号"), "登录名");
			}
			if(!hvo.getStringValue("数据库校验失败原因").equals("")){
				billListPanel.setItemForeGroundColor("FF0000", hvo.getIntegerValue("行号"), "数据库校验失败原因");
				billListPanel.setItemBackGroundColor(Color.lightGray, hvo.getIntegerValue("行号"));
			}
			if(!hvo.getStringValue("EXCEL校验失败原因").equals("")){
				billListPanel.setItemForeGroundColor("FF0000", hvo.getIntegerValue("行号"), "EXCEL校验失败原因");
				billListPanel.setItemBackGroundColor(Color.gray, hvo.getIntegerValue("行号"));
			}
		}
		
		return billListPanel;
	}
	
	private void getDataPanel(){
		dataPanel = new JPanel();
		dataPanel.setLayout(new BorderLayout(0, 0));  
		
		JLabel label_null = new JLabel("      "); 
		label_null.setHorizontalAlignment(SwingConstants.RIGHT); 
		label_null.setPreferredSize(new Dimension(50, 20)); 
		
		JLabel label_null_ = new JLabel("      "); 
		label_null_.setHorizontalAlignment(SwingConstants.RIGHT); 
		label_null_.setPreferredSize(new Dimension(400, 20)); 
		
		btn_compare_ = new WLTButton("返回", UIUtil.getImage("zt_072.gif"));
		btn_import = new WLTButton("执行导入", UIUtil.getImage("zt_073.gif"));
		
		btn_compare_.addActionListener(this);
		btn_import.addActionListener(this);

		JPanel Panel = new JPanel();
		Panel.setLayout(new FlowLayout(FlowLayout.LEFT));  
		Panel.add(label_null);
		Panel.add(btn_compare_);
		Panel.add(label_null_);
		Panel.add(btn_import);

		dataPanel.add(getDataTabPanel(), BorderLayout.CENTER);
		dataPanel.add(Panel, BorderLayout.NORTH);
		dataPanel.updateUI();
	}
	
	private void getData_UpdPanel(){
		data_updPanel = new JPanel();
		data_updPanel.setLayout(new BorderLayout(0, 0));  
		
		JLabel label_null = new JLabel("      "); 
		label_null.setHorizontalAlignment(SwingConstants.RIGHT); 
		label_null.setPreferredSize(new Dimension(50, 20)); 
		
		JLabel label_null_ = new JLabel("      "); 
		label_null_.setHorizontalAlignment(SwingConstants.RIGHT); 
		label_null_.setPreferredSize(new Dimension(400, 20)); 
		
		btn_compare_ = new WLTButton("返回", UIUtil.getImage("zt_072.gif"));
		btn_upd = new WLTButton("执行更新", UIUtil.getImage("zt_073.gif"));
		
		btn_compare_.addActionListener(this);
		btn_upd.addActionListener(this);

		JPanel Panel = new JPanel();
		Panel.setLayout(new FlowLayout(FlowLayout.LEFT));  
		Panel.add(label_null);
		Panel.add(btn_compare_);
		Panel.add(label_null_);
		Panel.add(btn_upd);

		data_updPanel.add(getData_UpdTabPanel(), BorderLayout.CENTER);
		data_updPanel.add(Panel, BorderLayout.NORTH);
		data_updPanel.updateUI();
	}
	
	private void getData_DelPanel(){
		data_delPanel = new JPanel();
		data_delPanel.setLayout(new BorderLayout(0, 0));  
		
		JLabel label_null = new JLabel("      "); 
		label_null.setHorizontalAlignment(SwingConstants.RIGHT); 
		label_null.setPreferredSize(new Dimension(50, 20)); 
		
		JLabel label_null_ = new JLabel("      "); 
		label_null_.setHorizontalAlignment(SwingConstants.RIGHT); 
		label_null_.setPreferredSize(new Dimension(400, 20)); 
		
		btn_compare_ = new WLTButton("返回", UIUtil.getImage("zt_072.gif"));
		btn_del = new WLTButton("执行删除", UIUtil.getImage("zt_073.gif"));
		
		btn_compare_.addActionListener(this);
		btn_del.addActionListener(this);

		JPanel Panel = new JPanel();
		Panel.setLayout(new FlowLayout(FlowLayout.LEFT));  
		Panel.add(label_null);
		Panel.add(btn_compare_);
		Panel.add(label_null_);
		Panel.add(btn_del);

		data_delPanel.add(getData_DelTabPanel(), BorderLayout.CENTER);
		data_delPanel.add(Panel, BorderLayout.NORTH);
		data_delPanel.updateUI();
	}
	
	private JTabbedPane getDataTabPanel(){
		ArrayList al_dept = new ArrayList();
		ArrayList al_role = new ArrayList();
		
		for(int i=0;i<al_user.size();i++){
			HashVO hvo_user = (HashVO) al_user.get(i);
			
			//机构-人员关联数据
			String dept = hvo_user.getStringValue("机构组");
			String[] depts = dept.split(";");
			if(!"".equals(dept)){
				for(int j=0;j<depts.length;j++){
					HashVO hvo_dept = new HashVO();
					hvo_dept.setAttributeValue("登录名",hvo_user.getStringValue("登录名"));
					hvo_dept.setAttributeValue("姓名",hvo_user.getStringValue("姓名"));
					hvo_dept.setAttributeValue("机构",depts[j]);
					al_dept.add(hvo_dept);		
				}				
			}
			
			//角色-人员关联数据
			String role = hvo_user.getStringValue("角色组");
			String[] roles = role.split(";");
			if(!"".equals(role)){
				for(int j=0;j<roles.length;j++){
					HashVO hvo_role = new HashVO();
					hvo_role.setAttributeValue("登录名",hvo_user.getStringValue("登录名"));
					hvo_role.setAttributeValue("姓名",hvo_user.getStringValue("姓名"));
					hvo_role.setAttributeValue("角色",roles[j]);
					al_role.add(hvo_role);		
				}			
			}
		}
		
		//人员数据
		HashVO[] hvos_user = (HashVO[]) al_user.toArray(new HashVO[0]);
		BillListPanel billListPanel_user = new BillListPanel(hvos_user);
		billListPanel_user.setItemsVisible(new String[]{"数据库校验失败原因","EXCEL校验失败原因","机构组","角色组","用户ID","机构组ID","角色组ID","行号"}, false);
		
		//机构-人员关联数据
		HashVO[] hvos_dept = (HashVO[]) al_dept.toArray(new HashVO[0]);
		BillListPanel billListPanel_dept = new BillListPanel(hvos_dept);
		
		//角色-人员关联数据
		HashVO[] hvos_role = (HashVO[]) al_role.toArray(new HashVO[0]);
		BillListPanel billListPanel_role = new BillListPanel(hvos_role);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("导入_人员数据", billListPanel_user);
		if(hm_excel.containsKey("机构组")){
			tabbedPane.addTab("导入_机构-人员关联数据", billListPanel_dept);
		}
		if(hm_excel.containsKey("角色组")){
			tabbedPane.addTab("导入_角色-人员关联数据", billListPanel_role);
		}
	
		return tabbedPane;
	}
	
	private JTabbedPane getData_UpdTabPanel(){
		ArrayList al_dept = new ArrayList();
		ArrayList al_role = new ArrayList();
		for(int i=0;i<al_user_upd_del.size();i++){
			HashVO hvo_user = (HashVO) al_user_upd_del.get(i);
			
			//机构-人员关联数据
			String dept = hvo_user.getStringValue("机构组");
			String[] depts = dept.split(";");
			if(!"".equals(dept)){
				for(int j=0;j<depts.length;j++){
					HashVO hvo_dept = new HashVO();
					hvo_dept.setAttributeValue("登录名",hvo_user.getStringValue("登录名"));
					hvo_dept.setAttributeValue("姓名",hvo_user.getStringValue("姓名"));
					hvo_dept.setAttributeValue("机构",depts[j]);
					al_dept.add(hvo_dept);		
				}				
			}
			
			//角色-人员关联数据
			String role = hvo_user.getStringValue("角色组");
			String[] roles = role.split(";");
			if(!"".equals(role)){
				for(int j=0;j<roles.length;j++){
					HashVO hvo_role = new HashVO();
					hvo_role.setAttributeValue("登录名",hvo_user.getStringValue("登录名"));
					hvo_role.setAttributeValue("姓名",hvo_user.getStringValue("姓名"));
					hvo_role.setAttributeValue("角色",roles[j]);
					al_role.add(hvo_role);		
				}			
			}
		}
		
		//人员数据
		HashVO[] hvos_user = (HashVO[]) al_user_upd_del.toArray(new HashVO[0]);
		BillListPanel billListPanel_user = new BillListPanel(hvos_user);
		billListPanel_user.setItemsVisible(new String[]{"数据库校验失败原因","EXCEL校验失败原因","机构组","角色组","用户ID","机构组ID","角色组ID","行号"}, false);
		
		//机构-人员关联数据
		HashVO[] hvos_dept = (HashVO[]) al_dept.toArray(new HashVO[0]);
		BillListPanel billListPanel_dept = new BillListPanel(hvos_dept);
		
		//角色-人员关联数据
		HashVO[] hvos_role = (HashVO[]) al_role.toArray(new HashVO[0]);
		BillListPanel billListPanel_role = new BillListPanel(hvos_role);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("更新_人员数据", billListPanel_user);
		if(hm_excel.containsKey("机构组")){
			tabbedPane.addTab("更新_机构-人员关联数据", billListPanel_dept);
		}
		if(hm_excel.containsKey("角色组")){
			tabbedPane.addTab("更新_角色-人员关联数据", billListPanel_role);
		}
	
		return tabbedPane;
	}
	
	private JTabbedPane getData_DelTabPanel(){
		//人员数据
		HashVO[] hvos_user = (HashVO[]) al_user_upd_del.toArray(new HashVO[0]);
		BillListPanel billListPanel_user = new BillListPanel(hvos_user);
		billListPanel_user.setItemsVisible(new String[]{"数据库校验失败原因","EXCEL校验失败原因","机构组","角色组","用户ID","机构组ID","角色组ID","行号"}, false);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("删除_人员数据", billListPanel_user);
	
		return tabbedPane;
	}
	
	private void dataImport(){
		try {
			ArrayList list_sqls = new ArrayList();	
			for(int i=0;i<al_user.size();i++){
				HashVO hvo_user = (HashVO) al_user.get(i);
				
				//人员数据
				String userid = UIUtil.getSequenceNextValByDS(null, "S_PUB_USER");
				InsertSQLBuilder sb_user = new InsertSQLBuilder("pub_user"); 
				sb_user.putFieldValue("id", userid); 
				sb_user.putFieldValue("code", hvo_user.getStringValue("登录名")); //登录名
				DESKeyTool desKeyTool = new DESKeyTool();
				for(int j=1;j<strs_user[0].length;j++){
					if(hvo_user.getStringValue(strs_user[0][j])!=null){
						if(strs_user[0][j].equals("密码")||strs_user[0][j].equals("管理密码")){
							sb_user.putFieldValue(strs_user[1][j], desKeyTool.encrypt(hvo_user.getStringValue(strs_user[0][j]))); //密码
						}else{
							sb_user.putFieldValue(strs_user[1][j], hvo_user.getStringValue(strs_user[0][j])); //用户其它数据列	
						}
					}
				}
				sb_user.putFieldValue("isfunfilter", "Y"); //是否权限过滤
				list_sqls.add(sb_user.getSQL());
				
				//机构-人员关联数据
				String deptid = hvo_user.getStringValue("机构组ID");
				String[] deptids = deptid.split(";");
				if(!"".equals(deptid)){
					for(int j=0;j<deptids.length;j++){
						InsertSQLBuilder sb_dept = new InsertSQLBuilder("pub_user_post"); 
						sb_dept.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_PUB_USER_POST")); 
						sb_dept.putFieldValue("userid", userid); //用户ID
						sb_dept.putFieldValue("userdept", deptids[j]); //机构ID
						for(int k=1;k<strs_dept[0].length;k++){
							if(hvo_user.getStringValue(strs_dept[0][k])!=null){
								sb_dept.putFieldValue(strs_dept[1][k], hvo_user.getStringValue(strs_dept[0][k])); //机构其它数据列
							}
						}
			            if(j==0){
			            	sb_dept.putFieldValue("isdefault", "Y"); //默认部门
			            }
						list_sqls.add(sb_dept.getSQL());
					}				
				}
				
				//角色-人员关联数据
				String roleid = hvo_user.getStringValue("角色组ID");
				String[] roleids = roleid.split(";");
				if(!"".equals(roleid)){
					for(int j=0;j<roleids.length;j++){
						InsertSQLBuilder sb_role = new InsertSQLBuilder("pub_user_role"); 
						sb_role.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_PUB_USER_ROLE")); 
						sb_role.putFieldValue("userid", userid); //用户ID
						sb_role.putFieldValue("roleid", roleids[j]); //角色ID
						for(int k=1;k<strs_role[0].length;k++){
							if(hvo_user.getStringValue(strs_role[0][k])!=null){
								sb_role.putFieldValue(strs_role[1][k], hvo_user.getStringValue(strs_role[0][k])); //角色其它数据列	
							}
						}
						list_sqls.add(sb_role.getSQL());	
					}				
				}
			}
			UIUtil.executeBatchByDS(null, list_sqls);
			importmark = true;
			MessageBox.show(this, "导入成功!");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		MessageBox.show(this, "导入失败!");
	}
	
	private void dataUpd(){
		try {
			ArrayList list_sqls = new ArrayList();	
			
			for(int i=0;i<al_user_upd_del.size();i++){
				HashVO hvo_user = (HashVO) al_user_upd_del.get(i);
				
				//人员数据
				String userid = hvo_user.getStringValue("用户ID","");
				
				UpdateSQLBuilder sb_user = new UpdateSQLBuilder("pub_user", "id="+userid); 
				sb_user.putFieldValue("code", hvo_user.getStringValue("登录名")); //登录名
				DESKeyTool desKeyTool = new DESKeyTool();
				for(int j=1;j<strs_user[0].length;j++){
					if(hvo_user.getStringValue(strs_user[0][j])!=null){
						if(strs_user[0][j].equals("密码")||strs_user[0][j].equals("管理密码")){
							sb_user.putFieldValue(strs_user[1][j], desKeyTool.encrypt(hvo_user.getStringValue(strs_user[0][j]))); //密码
						}else{
							sb_user.putFieldValue(strs_user[1][j], hvo_user.getStringValue(strs_user[0][j])); //用户其它数据列	
						}
					}
				}
				list_sqls.add(sb_user.getSQL());
				
				list_sqls.add("delete from pub_user_post where userid="+userid);
				list_sqls.add("delete from pub_user_role where userid="+userid);
				
				//机构-人员关联数据
				String deptid = hvo_user.getStringValue("机构组ID");
				String[] deptids = deptid.split(";");
				if(!"".equals(deptid)){
					for(int j=0;j<deptids.length;j++){
						InsertSQLBuilder sb_dept = new InsertSQLBuilder("pub_user_post"); 
						sb_dept.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_PUB_USER_POST")); 
						sb_dept.putFieldValue("userid", userid); //用户ID
						sb_dept.putFieldValue("userdept", deptids[j]); //机构ID
						for(int k=1;k<strs_dept[0].length;k++){
							if(hvo_user.getStringValue(strs_dept[0][k])!=null){
								sb_dept.putFieldValue(strs_dept[1][k], hvo_user.getStringValue(strs_dept[0][k])); //机构其它数据列
							}
						}
			            if(j==0){
			            	sb_dept.putFieldValue("isdefault", "Y"); //默认部门
			            }
						list_sqls.add(sb_dept.getSQL());
					}				
				}
				
				//角色-人员关联数据
				String roleid = hvo_user.getStringValue("角色组ID");
				String[] roleids = roleid.split(";");
				if(!"".equals(roleid)){
					for(int j=0;j<roleids.length;j++){
						InsertSQLBuilder sb_role = new InsertSQLBuilder("pub_user_role"); 
						sb_role.putFieldValue("id", UIUtil.getSequenceNextValByDS(null, "S_PUB_USER_ROLE")); 
						sb_role.putFieldValue("userid", userid); //用户ID
						sb_role.putFieldValue("roleid", roleids[j]); //角色ID
						for(int k=1;k<strs_role[0].length;k++){
							if(hvo_user.getStringValue(strs_role[0][k])!=null){
								sb_role.putFieldValue(strs_role[1][k], hvo_user.getStringValue(strs_role[0][k])); //角色其它数据列
							}
						}
						list_sqls.add(sb_role.getSQL());	
					}				
				}
			}
			
			UIUtil.executeBatchByDS(null, list_sqls);
			updmark = true;
			MessageBox.show(this, "更新成功!");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		MessageBox.show(this, "更新失败!");
	}
	
	private void dataDel(){
		try {
			ArrayList list_sqls = new ArrayList();	
			for(int i=0;i<al_user_upd_del.size();i++){
				HashVO hvo_user = (HashVO) al_user_upd_del.get(i);
				list_sqls.add("delete from pub_user where id="+hvo_user.getStringValue("用户ID",""));	
				list_sqls.add("delete from pub_user_post where userid="+hvo_user.getStringValue("用户ID",""));
				list_sqls.add("delete from pub_user_role where userid="+hvo_user.getStringValue("用户ID",""));
			}
			UIUtil.executeBatchByDS(null, list_sqls);
			delmark = true;
			MessageBox.show(this, "删除成功!");
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
		MessageBox.show(this, "删除失败!");
	}
	
	private VFlowLayoutPanel CheckBoxPanel(String[][] strs_user,String[][] strs_dept,String[][] strs_role){
		JLabel label_user = new JLabel("用户:"); 
		JLabel label_dept = new JLabel("机构:"); 
		JLabel label_role = new JLabel("角色:"); 
		
		JPanel checkBoxPanel_user = new JPanel();
		checkBoxPanel_user.setLayout(new FlowLayout(FlowLayout.LEFT));  
		checkBoxPanel_user.setPreferredSize(new Dimension(-1, 90));
		checkBoxPanel_user.add(label_user);
		checkBoxs_user = new JCheckBox[strs_user[0].length];
		for(int i=0; i<strs_user[0].length; i++){
			checkBoxs_user[i] = new JCheckBox(strs_user[0][i], false);
			checkBoxs_user[i].setOpaque(false);
			checkBoxs_user[i].addActionListener(this);
			checkBoxPanel_user.add(checkBoxs_user[i]);
		}
		
		JPanel checkBoxPanel_dept = new JPanel();
		checkBoxPanel_dept.setLayout(new FlowLayout(FlowLayout.LEFT));  
		checkBoxPanel_dept.setPreferredSize(new Dimension(-1, 30));
		checkBoxPanel_dept.add(label_dept);
		checkBoxs_dept = new JCheckBox[strs_dept[0].length];
		for(int i=0; i<strs_dept[0].length; i++){
			checkBoxs_dept[i] = new JCheckBox(strs_dept[0][i], false);
			checkBoxs_dept[i].setOpaque(false);
			checkBoxs_dept[i].addActionListener(this);
			checkBoxPanel_dept.add(checkBoxs_dept[i]);
		}
		
		JPanel checkBoxPanel_role = new JPanel();
		checkBoxPanel_role.setLayout(new FlowLayout(FlowLayout.LEFT)); 
		checkBoxPanel_role.setPreferredSize(new Dimension(-1, 30));
		checkBoxPanel_role.add(label_role);
		checkBoxs_role = new JCheckBox[strs_role[0].length];
		for(int i=0; i<strs_role[0].length; i++){
			checkBoxs_role[i] = new JCheckBox(strs_role[0][i], false);
			checkBoxs_role[i].setOpaque(false);
			checkBoxs_role[i].addActionListener(this);
			checkBoxPanel_role.add(checkBoxs_role[i]);
		}
		
		checkBoxs_user[0].setEnabled(false);
		checkBoxs_user[0].setSelected(true);
		checkBoxs_dept[0].setSelected(true);
		checkBoxs_role[0].setSelected(true);
		
		ArrayList al_compents = new ArrayList(); 
		al_compents.add(checkBoxPanel_user); 
		al_compents.add(checkBoxPanel_dept); 
		al_compents.add(checkBoxPanel_role); 
		
		return new VFlowLayoutPanel(al_compents);
	}
	
	private String[][] getCheckBox(){
		String selected = "";
		for(int i=0; i<checkBoxs_user.length; i++){
			if(checkBoxs_user[i].isSelected()){
				selected += checkBoxs_user[i].getText()+";";
			}
		}
		for(int i=0; i<checkBoxs_dept.length; i++){
			if(checkBoxs_dept[i].isSelected()){
				selected += checkBoxs_dept[i].getText()+";";
			}
		}
		for(int i=0; i<checkBoxs_role.length; i++){
			if(checkBoxs_role[i].isSelected()){
				selected += checkBoxs_role[i].getText()+";";
			}
		}

		if(selected.equals("")){
			return null;
		}else{
			return new String[][]{selected.substring(0, selected.length()-1).split(";")};
		}
	}
	
	private VFlowLayoutPanel CheckBoxPanel_(String[][] strs_user,String[][] strs_dept,String[][] strs_role){
		JLabel label_title = new JLabel(" 显示导入文件包含的列"); 
		label_title.setForeground(Color.blue);
		JLabel label_user = new JLabel("用户:"); 
		JLabel label_user_ = new JLabel("数据库:"); 
		JLabel label_dept = new JLabel("机构:"); 
		JLabel label_role = new JLabel("角色:"); 
		
		JPanel checkBoxPanel_user = new JPanel();
		checkBoxPanel_user.setLayout(new FlowLayout(FlowLayout.LEFT));  
		checkBoxPanel_user.setPreferredSize(new Dimension(-1, 25));
		checkBoxPanel_user.add(label_user);
		checkBoxs_user_ = new JCheckBox[strs_user[0].length];
		for(int i=0; i<strs_user[0].length; i++){
			checkBoxs_user_[i] = new JCheckBox(strs_user[0][i], false);
			checkBoxs_user_[i].setOpaque(false);
			checkBoxPanel_user.add(checkBoxs_user_[i]);
		}
		
		JPanel checkBoxPanel_dept = new JPanel();
		checkBoxPanel_dept.setLayout(new FlowLayout(FlowLayout.LEFT));  
		checkBoxPanel_dept.setPreferredSize(new Dimension(-1, 25));
		checkBoxPanel_dept.add(label_dept);
		checkBoxs_dept_ = new JCheckBox[strs_dept[0].length];
		for(int i=0; i<strs_dept[0].length; i++){
			checkBoxs_dept_[i] = new JCheckBox(strs_dept[0][i], false);
			checkBoxs_dept_[i].setOpaque(false);
			checkBoxPanel_dept.add(checkBoxs_dept_[i]);
		}
		
		JPanel checkBoxPanel_role = new JPanel();
		checkBoxPanel_role.setLayout(new FlowLayout(FlowLayout.LEFT)); 
		checkBoxPanel_role.setPreferredSize(new Dimension(-1, 25));
		checkBoxPanel_role.add(label_role);
		checkBoxs_role_ = new JCheckBox[strs_role[0].length];
		for(int i=0; i<strs_role[0].length; i++){
			checkBoxs_role_[i] = new JCheckBox(strs_role[0][i], false);
			checkBoxs_role_[i].setOpaque(false);
			checkBoxPanel_role.add(checkBoxs_role_[i]);
		}
		
		JPanel checkBoxPanel_user_ = new JPanel();
		checkBoxPanel_user_.setLayout(new FlowLayout(FlowLayout.LEFT));  
		checkBoxPanel_user_.setPreferredSize(new Dimension(-1, 40));
		checkBoxPanel_user_.add(label_user_);
		for(int i=0; i<strs_user[0].length; i++){
			checkBoxPanel_user_.add(new JLabel(strs_user[0][i]+"-"+strs_user[1][i]));
		}

		checkBoxs_user_[0].setSelected(true);
		checkBoxs_dept_[0].setSelected(true);
		checkBoxs_role_[0].setSelected(true);
		
		ArrayList al_compents = new ArrayList(); 
		al_compents.add(label_title); 
		al_compents.add(checkBoxPanel_user); 
		al_compents.add(checkBoxPanel_dept); 
		al_compents.add(checkBoxPanel_role); 
		//al_compents.add(checkBoxPanel_user_); 
		al_compents.add(getHelp()); 
		
		return new VFlowLayoutPanel(al_compents);
	}
	
	private void setCheckBoxPanel_Value(){
		String[][] excelFileData = new ExcelUtil().getExcelFileData(tf_file.getText(), 0);
		HashMap hm_excel_ = new HashMap();
		for(int i = 0; i < excelFileData[0].length; i++){
			String excel_0 = excelFileData[0][i];
			hm_excel_.put(excel_0, null);
		}
		
		for(int i=0; i<strs_user[0].length; i++){
			if(hm_excel_.containsKey(strs_user[0][i])){
				checkBoxs_user_[i].setSelected(true);
			}else{
				checkBoxs_user_[i].setSelected(false);	
			}
		}
		
		for(int i=0; i<strs_dept[0].length; i++){
			if(hm_excel_.containsKey(strs_dept[0][i])){
				checkBoxs_dept_[i].setSelected(true);
			}else{
				checkBoxs_dept_[i].setSelected(false);	
			}
		}
		
		for(int i=0; i<strs_role[0].length; i++){
			if(hm_excel_.containsKey(strs_role[0][i])){
				checkBoxs_role_[i].setSelected(true);
			}else{
				checkBoxs_role_[i].setSelected(false);	
			}
		}
	}
	
	private JPanel getHelp(){
		StringBuilder help = new StringBuilder();
		help.append("  帮助说明：\r\n\r\n");
		
		String sql = "";
		for(int i=0; i<strs_user[0].length; i++){
			if(i==7){
				sql += "\r\n    ";
			}
			sql += " " + strs_user[0][i]+"-"+strs_user[1][i];
		}
		
		help.append("①  数据库字段 "+sql+"\r\n\r\n");
		help.append("②  模板中登录名为必填项并且不可以重复，机构、角色可以缺省，当某人机构、角色为多个时以\";\"号进行分隔，例如：总行-法律\r\n    合规部;总行-计划财务部  一般员工;管理员\r\n\r\n");
		help.append("③  EXCEL模板数据整理完毕后，在导入前涉及到数字的列请设置为文本格式，设置方式为选中要设置的列，选择数据—分列，点击\r\n    下一步，在列数据格式中选择文本即可\r\n\r\n");
		help.append("④  进行数据更新时，如果导入的模板数据的登录名与数据库中登录名相同，则会更新此条记录\r\n\r\n");
		help.append("⑤  进行数据删除时会同时删除机构与人员关系表、角色与人员关系表中的相关记录\r\n\r\n");
		
		WLTTextArea jta_context = new WLTTextArea(10,120);
		jta_context.setBackground(LookAndFeel.systembgcolor); 
		jta_context.setText(help.toString());
		
		JPanel helpPanel = new JPanel();
		helpPanel.setLayout(new FlowLayout(FlowLayout.LEFT));  
		helpPanel.setPreferredSize(new Dimension(-1, 250));	
		helpPanel.add(jta_context);
		
		return helpPanel;
	}
	
}
