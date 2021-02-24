package cn.com.pushworld.wn.ui.hz.score.p01;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.report.cellcompent.ExcelUtil;
import cn.com.infostrategy.ui.sysapp.other.BigFileUpload;
import cn.com.infostrategy.ui.sysapp.other.RefDialog_Month;

public class DepartIndexExport extends AbstractWorkPanel implements ActionListener{
	WLTButton excel_export = new WLTButton("部门指标明细导出");
	private String selectDate = "";
    private ExcelUtil excelUtil=new ExcelUtil();
	@Override
	public void initialize() {
		this.setLayout(new FlowLayout(0));
        this.excel_export.setPreferredSize(new Dimension(100, 50));
        this.excel_export.addActionListener(this);
        this.add(this.excel_export);
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==this.excel_export) {
			String[] datas = getDate(this);
			selectDate = datas[0].toString();
            if (isExist(selectDate) == 1) {
                MessageBox.show("您所查询的日期支行指标明细不存在！");
                return;
            }else{
            	listExport(selectDate);
            }
        }		
	}
	/**
	 * 时间
	 * 
	 * @param _parent
	 * @return
	 */
	private String[] getDate(Container _parent) {
		String[] str = null;
		int a = 0;
		try {
			RefDialog_Month chooseMonth = new RefDialog_Month(_parent,
					"请选择导出数据的时间", new RefItemVO(selectDate, "", selectDate),
					null);
			chooseMonth.initialize();
			chooseMonth.setVisible(true);
			selectDate = chooseMonth.getReturnRefItemVO().getName();
			a = chooseMonth.getCloseType();
			str = new String[] { selectDate, String.valueOf(a) };
			return str;
		} catch (Exception e) {
			WLTLogger.getLogger(BigFileUpload.class).error(e);
		}
		return new String[] { "2013-08", String.valueOf(a) };
	}
	
	
	
	private void listExport(String selectDate) {
		try {
			String[][] departname = UIUtil.getStringArrayByDS(null,"select distinct(checkeddeptname) deptname from sal_dept_check_score where checkdate='"+selectDate+"'");
			String[][] indexname = UIUtil.getStringArrayByDS(null, "select distinct(targetname) targetname from sal_dept_check_score where checkdate='"+selectDate+"'");
//			HSSFCellStyle style = workbook.createCellStyle();
//			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
//			style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//以后可以设置的酷炫一点
			String[][] str = new String[departname.length+1][indexname.length+2];
			for(int i=1;i<=departname.length;i++){
				str[i][0]=departname[i-1][0];
				System.out.println(departname[i-1][0]);
			}
			for(int j=1;j<=indexname.length;j++){
				str[0][j]=indexname[j-1][0];
				System.out.println(indexname[j-1][0]);
			}
			for(int i=1;i<=departname.length;i++){
				for(int j=1;j<=indexname.length;j++){
					String value = UIUtil.getStringValueByDS(null, "select checkscore from sal_dept_check_score where checkdate='"+selectDate+"' and checkeddeptname='"+str[i][0]+"' and targetname='"+str[0][j]+"'");
					value = (value==null||value.equals(""))?"- -":value;
					str[i][j]=value;
				}
			}
			JFileChooser chooser = new JFileChooser();
	        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
	        chooser.setDialogTitle("请选择下载路径");
	        chooser.setApproveButtonText("选择");
	        FileFilter filter = new FileNameExtensionFilter("Microsoft Office Excel 工作表", "xls", "xlsx");
	        chooser.setSelectedFile(new File("支行指标明细.xls"));
	        chooser.setFileFilter(filter);
	        int flag = chooser.showOpenDialog(this);
	        if (flag != JFileChooser.APPROVE_OPTION || chooser.getSelectedFile() == null) {
	            return;
	        }
	        final String str_path = chooser.getSelectedFile().getAbsolutePath();
	        excelUtil.setDataToExcelFile(str,str_path);
	        MessageBox.show(this,"导出成功");
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this,"导出失败，请联系系统管理员");
		}
		
	}
	private int isExist(String selectDate) {
		try {
			String[][] dates = UIUtil.getStringArrayByDS(null, "select * from sal_dept_check_score where checkdate='"+selectDate+"'");
			System.out.println(dates.length);
			if(dates.length==0){
				return 1;
			}else{
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
		
	}

}
