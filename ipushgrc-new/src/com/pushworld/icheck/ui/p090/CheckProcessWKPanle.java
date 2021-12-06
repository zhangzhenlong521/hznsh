package com.pushworld.icheck.ui.p090;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.cellcompent.ExcelUtil;
/**
 * 
 * @author zzl
 *
 */
public class CheckProcessWKPanle extends  AbstractWorkPanel implements ActionListener{
	private BillCellPanel cell=null;
	private BillQueryPanel query = new BillQueryPanel("CK_SCHEME_ZZL_E01_2");
	private WLTButton btn_export = new WLTButton("导出Excel");
	public void initialize() {
		cell=new BillCellPanel("过程及结论性文件统计");
		cell.setToolBarVisiable(false);
		query.addBillQuickActionListener(this);
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(getBtnPanel(), BorderLayout.NORTH);
		mainPanel.add(cell, BorderLayout.CENTER);
		WLTSplitPane splitpanel = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, query, mainPanel);
		splitpanel.setOpaque(false);
		splitpanel.setDividerLocation(60);
		this.add(splitpanel);
		
	}

	private Component getBtnPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setOpaque(false);
		btn_export.addActionListener(this);
		panel.add(btn_export);
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==query){
			onQuery();
		}else if(e.getSource()==btn_export){
			exportExcel();
		}
		
	}

	private void exportExcel() {
		int col=cell.getColumnCount();
		int row=cell.getRowCount();
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setDialogTitle("请选择要保存到的目录");
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setAcceptAllFileFilterUsed(false);
		int result = fc.showSaveDialog(this);
		if (result != JFileChooser.APPROVE_OPTION) { // 如果不是确定的
			return;
		}
		String savePath = fc.getSelectedFile().getPath();
		ExcelUtil excel=new ExcelUtil();	
		String [][] imexcel=new String[row][col];
		for(int i=0;i<row;i++){
			for(int j=0;j<col;j++){
				try {
					String str=cell.getValueAt(i, j);
					imexcel[i][j]=str;

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		savePath = savePath + "\\过程及结论性文件统计.xls";
		excel.setDataToExcelFile(imexcel, savePath);
		MessageBox.show(this,"导出成功");
		
	}

	private void onQuery() {
		int col=cell.getColumnCount();
		int row=cell.getRowCount();
		Object name=query.getValueAt("name");
		try {
			HashVO [] schemevo=UIUtil.getHashVoArrayByDS(null,"select * from CK_SCHEME where name='"+name+"'");
			if(schemevo.length <=0){
				MessageBox.show(this,"请选择方案名称查询");
				return; 
			}
			HashVO [] workvo=UIUtil.getHashVoArrayByDS(null,"select * from CK_MEMBER_WORK where schemeid='"+schemevo[0].getStringValue("id")+"'");
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<workvo.length;i++){
				sb.append(workvo[i].getStringValue("leader"));
				sb.append(workvo[i].getStringValue("teamusers"));
			}
		String usersid=sb.toString().replace(";", ",");
		usersid=usersid.substring(0,usersid.length()-1);
		String [] []username=UIUtil.getStringArrayByDS(null, "select id,name from pub_user where id in("+usersid+")");
			for(int c=0;c<username.length;c++){
				cell.setValueAt(username[c][1].toString(), 0, c+2);
				cell.setBackground("102,255,51", 0, c+2);
				HashVO [] createuservo=UIUtil.getHashVoArrayByDS(null,"select * from CK_SCHEME where name='"+name+"' and CREATER='"+username[c][0].toString()+"'");
				if(createuservo.length>0){
					String ssfa=String.valueOf(createuservo.length);
					cell.setValueAt(ssfa, 1, c+2);
				}
				HashVO [] problemvo=UIUtil.getHashVoArrayByDS(null,"select * from ck_problem_info where schemeid='"+schemevo[0].getStringValue("id")+"' and createuserid='"+username[c][0].toString()+"'");
				if(problemvo.length>0){
					String ssfa=String.valueOf(problemvo.length);
					cell.setValueAt(ssfa, 3, c+2);
				}
				HashVO[] record=UIUtil.getHashVoArrayByDS(null,"select * from  ck_record where userid='"+username[c][0].toString()+"' and schemeid='"+schemevo[0].getStringValue("id")+"' and confirmname='Y'");
				HashVO[] rd=UIUtil.getHashVoArrayByDS(null,"select * from  ck_record where userid='"+username[c][0].toString()+"' and schemeid='"+schemevo[0].getStringValue("id")+"' and confirmname2='Y'");
				if(record.length>0 || rd.length>0){
					String ssfa=String.valueOf(record.length+rd.length);
					cell.setValueAt(ssfa, 4, c+2);
				}
				HashVO[] record2=UIUtil.getHashVoArrayByDS(null,"select * from  ck_record where userid='"+username[c][0].toString()+"' and schemeid='"+schemevo[0].getStringValue("id")+"' and abarbeitungname='Y'");			
				if(record2.length >0){	
					String ssfa=String.valueOf(record2.length);
					cell.setValueAt(ssfa, 7, c+2);			
				}
				HashVO[] wl_record=UIUtil.getHashVoArrayByDS(null,"select * from ck_wl_record where schemeid='"+schemevo[0].getStringValue("id")+"' and userid='"+username[c][0].toString()+"'");
				if(wl_record.length>0){
					if(wl_record[0].getStringValue("report")!=null){
						cell.setValueAt("1", 5, c+2);
					}
					if(wl_record[0].getStringValue("advice")!=null){
						cell.setValueAt("1", 6, c+2);
					}
					if(wl_record[0].getStringValue("decision")!=null){
						cell.setValueAt("1", 10, c+2);
					}
					if(wl_record[0].getStringValue("risk")!=null){
						cell.setValueAt("1", 8, c+2);
					}
				}

			}	
			for(int r=0;r<row-2;r++){
				int num=0;
				for(int c=0;c<username.length;c++){
					String count=cell.getValueAt(r+1, c+2);
					if(count==null || count.equals("") || count.equals(null) || count.equals("null")){
						count="0";
					}
					int countnum=Integer.parseInt(count);
					num=num+countnum;
				}
				String strnum=String.valueOf(num);
				cell.setValueAt(strnum, r+1, 1);
			}
			for(int c=0;c<username.length+1;c++){
				int num=0;
				for(int r=0;r<row-2;r++){
					String count=cell.getValueAt(r+1, c+1);
					if(count==null || count.equals("") || count.equals(null) || count.equals("null")){
						count="0";
					}
					int countnum=Integer.parseInt(count);
					num=num+countnum;
				}
				String strnum=String.valueOf(num);
				cell.setValueAt(strnum, row-1, c+1);
			}
	}catch(Exception e){
		e.printStackTrace();
	}

}
}
