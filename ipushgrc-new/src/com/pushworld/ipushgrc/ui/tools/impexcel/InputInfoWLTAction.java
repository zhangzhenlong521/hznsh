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
	    template_code=billCardPanel.getBillVO().getStringValue("template_code");//ģ�����
	    template_id=billCardPanel.getBillVO().getStringValue("template_id");//ģ��ID
	    type=billCardPanel.getBillVO().getStringValue("type");//����
	    papers_url=billCardPanel.getBillVO().getStringValue("papers_url");//��ַ
	    remarks=billCardPanel.getBillVO().getStringValue("remarks");//��ע����
        if(template_code==null || "".equals(template_code)){
        	MessageBox.show("��ѡ��ģ��!");
        	return;
        }
        if(papers_url==null || "".equals(papers_url)){
        	MessageBox.show("��ѡ���ļ�!");
        	return;
        }
        if(papers_url.indexOf(".xls")==-1){
        	MessageBox.show("�����ļ�������EXCEL!");
        	return;
        }
    	ExcelUtil excelUtil = new ExcelUtil();
    	String [][] str_content=excelUtil.getExcelFileData(papers_url);//�õ���Ӧ��ַ������
    	IPushGRCServiceIfc service = (IPushGRCServiceIfc) UIUtil.lookUpRemoteService(IPushGRCServiceIfc.class); //
		String[][] errorinfo=service.InputInfo(str_content, remarks,template_id,type); //
		if(errorinfo==null){
			MessageBox.show("��Ϣ����ɹ�!");
		}else{
			if(errorinfo[0][0].equals("error")){
				MessageBox.show("��ѡ����ȷ���������ͻ����ļ�!");
				return;
			}
			excelUtil.setDataToExcelFile(errorinfo, "C:\\error.xls");
			MessageBox.show("������Ϣ����ɹ�!������Ϣ�ѱ�����C:\\error.xls�ļ���!");
			Runtime.getRuntime().exec("explorer.exe \"C:\\error.xls\"");
		}
	    }

}
