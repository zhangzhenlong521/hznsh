package com.pushworld.ipushgrc.ui.tools.impexcel;


import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;
import cn.com.infostrategy.ui.report.cellcompent.ExcelUtil;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.pushworld.ipushgrc.ui.IPushGRCServiceIfc;
/**
 * 
 * @author wdl
 *
 */
public class InputInfoWLTAction implements WLTActionListener{
private BillCardPanel billCardPanel=null;
private Connection conn=null;
private PreparedStatement psmt=null; 
private String template_code="";
private String template_id="";
private String type="";
private String papers_url="";
private String remarks="";
	public void actionPerformed(WLTActionEvent _event) throws Exception {
		// TODO Auto-generated method stub
	    billCardPanel=(BillCardPanel) _event.getBillPanelFrom();
	    template_code=billCardPanel.getBillVO().getStringValue("template_code");//模板编码
	    template_id=billCardPanel.getBillVO().getStringValue("template_id");//模板ID
	    type=billCardPanel.getBillVO().getStringValue("type");//类型
	    papers_url=billCardPanel.getBillVO().getStringValue("papers_url");//地址
	    remarks=billCardPanel.getBillVO().getStringValue("remarks");//备注条件
        if(template_code==null || "".equals(template_code)){
        	MessageBox.show("请选择模板!");
        	return;
        }
        if(papers_url==null || "".equals(papers_url)){
        	MessageBox.show("请选择文件!");
        	return;
        }
        if(papers_url.indexOf(".xls")==-1){
        	MessageBox.show("导入文件必须是EXCEL!");
        	return;
        }
    	ExcelUtil excelUtil = new ExcelUtil();
    	String [][] str_content=excelUtil.getExcelFileData(papers_url);//得到对应地址的数据
    	IPushGRCServiceIfc service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class); //
		String[][] errorinfo=service.InputInfo(str_content, remarks,template_id,type); //
		if(errorinfo==null){
			MessageBox.show("信息导入成功!");
		}else{
			if(errorinfo[0][0].equals("error")){
				MessageBox.show("请选择正确的数据类型或者文件!");
				return;
			}
			excelUtil.setDataToExcelFile(errorinfo, "C:\\error.xls");
			MessageBox.show("部分信息导入成功!错误信息已保存在C:\\error.xls文件中!");
			Runtime.getRuntime().exec("explorer.exe \"C:\\error.xls\"");
		}
	    }

}
