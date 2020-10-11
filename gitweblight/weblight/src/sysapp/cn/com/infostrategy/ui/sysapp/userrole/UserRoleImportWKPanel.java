package cn.com.infostrategy.ui.sysapp.userrole;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFileChooser;

import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.report.cellcompent.ExcelUtil;

/**
 * 人员角色关联 【杨科/2012-09-07】
 */

public class UserRoleImportWKPanel extends AbstractWorkPanel implements ActionListener{
	private BillListPanel billList_userroles = null;
	private WLTButton btn_ur_import,btn_ur_like,btn_ur_join,btn_ur_clear,btn_ur_download; 
	
	public UserRoleImportWKPanel() {
		initialize();
	}

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); 
		
		billList_userroles=new BillListPanel("PUB_TEMP_USERS_CODE1");
		
		btn_ur_import = new WLTButton("用户导入"); 
		btn_ur_like = new WLTButton("角色匹配"); 
		btn_ur_join = new WLTButton("执行关联");
		btn_ur_clear = new WLTButton("清空数据");
		btn_ur_download = new WLTButton("下载模板");
		
		btn_ur_import.addActionListener(this);
		btn_ur_like.addActionListener(this);
		btn_ur_join.addActionListener(this);
		btn_ur_clear.addActionListener(this);
		btn_ur_download.addActionListener(this);
		
		billList_userroles.addBatchBillListButton(new WLTButton[]{btn_ur_import,btn_ur_like,btn_ur_join,btn_ur_clear,btn_ur_download});
		billList_userroles.repaintBillListButton();
		
		this.add(billList_userroles); 
	}
	
	public void actionPerformed(ActionEvent e) {
        if(e.getSource() == btn_ur_import){
			urImport();
		}else if(e.getSource() == btn_ur_like){
			urLike();
		}else if(e.getSource() == btn_ur_join){
			urJoin();
		}else if(e.getSource() == btn_ur_clear){
			urClear();
		}else if(e.getSource() == btn_ur_download){
			onDownLoadTemplet();
		}
	}
    
    private void urImport(){
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
    	
    	try {
    		String[][] excelFileData = new ExcelUtil().getExcelFileData(str_path, 0);
			ArrayList list_sqls = new ArrayList();	
			list_sqls.add("delete from pub_temp_users");
			for(int i = 1; i < excelFileData.length; i++){
				if(excelFileData[i][5]==null||excelFileData[i][5].equals("")){
					continue;
				}
				String newid = UIUtil.getSequenceNextValByDS(null, "s_pub_temp_users");
				list_sqls.add(new InsertSQLBuilder("pub_temp_users", new String[][] { 
						{ "id", newid }, 
						{ "一级分行编码", excelFileData[i][0] }, 
						{ "一级分行名称", excelFileData[i][1] }, 
						{ "二级分行编码", excelFileData[i][2] }, 
						{ "二级分行名称", excelFileData[i][3] }, 
						{ "部门名称", excelFileData[i][4] }, 
						{ "姓名", excelFileData[i][5] }, 
						{ "性别", excelFileData[i][6] }, 
						{ "身份证号", excelFileData[i][7] }, 
						{ "岗位", excelFileData[i][8] }, 
						{ "职务", excelFileData[i][9] }, 
						{ "出生日期", excelFileData[i][10] }, 
						{ "最高学历", excelFileData[i][11] }, 
						{ "专业", excelFileData[i][12] }, 
						{ "联系电话", excelFileData[i][13] }, 
						{ "手机", excelFileData[i][14] }, 
						{ "系统角色", excelFileData[i][15] },
						{ "柜员号", excelFileData[i][16] }
						}).getSQL());
			}
			UIUtil.executeBatchByDS(null, list_sqls);
			MessageBox.show(this, "导入成功!");
			billList_userroles.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void urLike(){
    	try {
			HashMap param = new HashMap();
			UIUtil.commMethod("cn.com.infostrategy.bs.sysapp.userrole.UserRoleImport", "urLike", param);
			MessageBox.show(this, "匹配完毕!");
			billList_userroles.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }
    
    private void urJoin(){
    	try {
			HashMap param = new HashMap();
			UIUtil.commMethod("cn.com.infostrategy.bs.sysapp.userrole.UserRoleImport", "urJoin", param);
			MessageBox.show(this, "关联完毕!");
			billList_userroles.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }
    
    private void urClear(){
    	try {
			UIUtil.executeUpdateByDS(null, "delete from pub_temp_users");
			MessageBox.show(this, "清空成功!");
			billList_userroles.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }
	
	public void onDownLoadTemplet() {
		JFileChooser chooser = new JFileChooser();
		try {
			if(ClientEnvironment.str_downLoadFileDir == null || "".equals(ClientEnvironment.str_downLoadFileDir)) {
				ClientEnvironment.str_downLoadFileDir = "C://";
			}
			File f = new File(new File(ClientEnvironment.str_downLoadFileDir + "用户角色批量导入模板.xls").getCanonicalPath());
			chooser.setSelectedFile(f);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		int li_rewult = chooser.showSaveDialog(billList_userroles);
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
					byte[] date = UIUtil.getCommonService().getServerResourceFile2("/userroles.xls", "GBK");
					fo = new FileOutputStream(chooseFile);
					fo.write(date);
					MessageBox.show(billList_userroles, "下载文件成功!");
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
				MessageBox.show(billList_userroles, "下载文件失败!");
			}
		}
	}
}
