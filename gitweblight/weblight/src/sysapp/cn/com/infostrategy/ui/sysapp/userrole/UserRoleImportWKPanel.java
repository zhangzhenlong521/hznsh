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
 * ��Ա��ɫ���� �����/2012-09-07��
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
		
		btn_ur_import = new WLTButton("�û�����"); 
		btn_ur_like = new WLTButton("��ɫƥ��"); 
		btn_ur_join = new WLTButton("ִ�й���");
		btn_ur_clear = new WLTButton("�������");
		btn_ur_download = new WLTButton("����ģ��");
		
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
		chooser.setDialogTitle("��ѡ���ļ�Ŀ¼");
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
						{ "һ�����б���", excelFileData[i][0] }, 
						{ "һ����������", excelFileData[i][1] }, 
						{ "�������б���", excelFileData[i][2] }, 
						{ "������������", excelFileData[i][3] }, 
						{ "��������", excelFileData[i][4] }, 
						{ "����", excelFileData[i][5] }, 
						{ "�Ա�", excelFileData[i][6] }, 
						{ "���֤��", excelFileData[i][7] }, 
						{ "��λ", excelFileData[i][8] }, 
						{ "ְ��", excelFileData[i][9] }, 
						{ "��������", excelFileData[i][10] }, 
						{ "���ѧ��", excelFileData[i][11] }, 
						{ "רҵ", excelFileData[i][12] }, 
						{ "��ϵ�绰", excelFileData[i][13] }, 
						{ "�ֻ�", excelFileData[i][14] }, 
						{ "ϵͳ��ɫ", excelFileData[i][15] },
						{ "��Ա��", excelFileData[i][16] }
						}).getSQL());
			}
			UIUtil.executeBatchByDS(null, list_sqls);
			MessageBox.show(this, "����ɹ�!");
			billList_userroles.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private void urLike(){
    	try {
			HashMap param = new HashMap();
			UIUtil.commMethod("cn.com.infostrategy.bs.sysapp.userrole.UserRoleImport", "urLike", param);
			MessageBox.show(this, "ƥ�����!");
			billList_userroles.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }
    
    private void urJoin(){
    	try {
			HashMap param = new HashMap();
			UIUtil.commMethod("cn.com.infostrategy.bs.sysapp.userrole.UserRoleImport", "urJoin", param);
			MessageBox.show(this, "�������!");
			billList_userroles.refreshData();
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }
    
    private void urClear(){
    	try {
			UIUtil.executeUpdateByDS(null, "delete from pub_temp_users");
			MessageBox.show(this, "��ճɹ�!");
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
			File f = new File(new File(ClientEnvironment.str_downLoadFileDir + "�û���ɫ��������ģ��.xls").getCanonicalPath());
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
					MessageBox.show(billList_userroles, "�����ļ��ɹ�!");
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
				MessageBox.show(billList_userroles, "�����ļ�ʧ��!");
			}
		}
	}
}
