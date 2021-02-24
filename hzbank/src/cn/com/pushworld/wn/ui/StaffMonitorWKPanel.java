package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;


import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
/**
 * Ա���쳣��Ϊ��Ϣ���
 * @author 85378
 */
public class StaffMonitorWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillListPanel listPanel,sonListPanel;
	private String str;
	private WLTButton exportButton,checkButton;
	private static String queryCondition;;
	private JFileChooser fileChooser;
	private JComboBox comboBox = null;
	private  BillCardPanel cardPanel;
	@Override
	public void initialize() {
		listPanel=new BillListPanel("WN_CURRENT_DEAL_RESULT_ZPY_Q01");
		sonListPanel=new BillListPanel("WN_CURRENT_CHECK_RESULT_ZPY_Q01");
//		exportButton=new WLTButton("���ݵ���");
//		exportButton.addActionListener(this);
		checkButton=new WLTButton("����");
		checkButton.addActionListener(this);
//		listPanel.addBillListButton(exportButton);
		listPanel.addBillListButton(checkButton);
		//��ȡ�����ٲ�ѯ�ļ���
		listPanel.getQuickQueryPanel().addBillQuickActionListener(this);//�Կ��ٲ�ѯ���м���
		listPanel.repaintBillListButton();
		listPanel.setRowNumberChecked(true);//��������
		this.add(listPanel);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
	    if(e.getSource()==exportButton){//�Բ�ѯ���ݽ��е�������
	    	try {
	    		if(queryCondition==null || "".equals(queryCondition)){
	    			queryCondition=" and 1=1";
	    		}
	             fileChooser=new JFileChooser();
		         String templetName = listPanel.getTempletVO().getTempletname();//��ȡ��ģ������
	             fileChooser.setSelectedFile(new File(templetName+".xls"));
	             int showOpenDialog = fileChooser.showOpenDialog(null);
	             final String filePath;
	             if(showOpenDialog==JFileChooser.APPROVE_OPTION){//ѡ����ļ�
	            	 filePath=fileChooser.getSelectedFile().getAbsolutePath();
	             }else {
	            	 return;
	             }
                 ////�������ݵ�������
	             new SplashWindow(this , new AbstractAction() {
					@Override
					public void actionPerformed(ActionEvent e) {
						str=createExcel(filePath);
					}
				});
	             MessageBox.show(this,str);
			} catch (Exception e2) {
				e2.printStackTrace();
			}
	    }else if(e.getSource()==listPanel.getQuickQueryPanel()){
	    	try {
	    		//��ȡ������sql�Ĳ�ѯ����
		    	queryCondition=listPanel.getQuickQueryPanel().getQuerySQLCondition();
		    	if(queryCondition==null || "".equals(queryCondition)){
		    		queryCondition="";
		    	}
		    	String sql="select * from wn_current_deal_date where 1=1 ";
		    	WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
		    	String datTxn = listPanel.getQuickQueryPanel().getRealValueAt("DAT_TXN");//����ʱ��
		    	String dealResult=listPanel.getQuickQueryPanel().getRealValueAt("DEAL_RESULT");//��ȡ��������
		    	String codAccTitle=listPanel.getQuickQueryPanel().getRealValueAt("COD_ACCT_TITLE");//��ȡ����ǰ�û�����
		    	String amtTxn2=listPanel.getQuickQueryPanel().getRealValueAt("AMT_TXN2");//��ȡ�����׽��
		    	String billListQuery="select * from WN_CURRENT_DEAL_RESULT where  1=1 ";
		    	HashMap<String, String> conditionMap=new HashMap<String, String>();
		    	if(datTxn!=null && !"".equals(datTxn)){
		    		conditionMap.put("DAT_TXN", datTxn);
		    		billListQuery=billListQuery+" and DAT_TXN like '"+datTxn.replace(";", "")+"%' ";
		    	}else {
		    		return;
		    	}
		    	if(dealResult!=null && !"".equals(dealResult)){
		    		conditionMap.put("DEAL_RESULT",dealResult );
		    		billListQuery=billListQuery+" and DEAL_RESULT='"+dealResult+"' ";
		    	}
		    	if(codAccTitle!=null && !"".equals(codAccTitle)){
		    		conditionMap.put("COD_ACCT_TITLE", codAccTitle);
		    		billListQuery=billListQuery+" and COD_ACCT_TITLE like '%"+codAccTitle+"%' ";
		    	}
		    	if(amtTxn2!=null && !"".equals(amtTxn2)){
		    		if(!amtTxn2.matches("([1-9]\\d*\\.?\\d*)|(0\\.\\d*[1-9])")){//�ж��û������Ƿ��Ƿ�����
			    		MessageBox.show(this,"��ǰ��������а��������֣�����������");
			    		return;
			    	}
		    		conditionMap.put("AMT_TXN2", amtTxn2);
		    		billListQuery=billListQuery+" and AMT_TXN2>="+Double.parseDouble(amtTxn2);
		    	}
		    	service.insertMonitorResult(sql,conditionMap);
		    	listPanel.QueryData(billListQuery);		   
			} catch (Exception e2) {
				e2.printStackTrace();
			}
	    }else  if(e.getSource()==checkButton){//�����ݽ��д���
	    	try{
	    		final BillVO[] checkUsers = listPanel.getCheckedBillVOs();
	    		String successResult="";
		    	if(checkUsers==null||checkUsers.length<=0){
		    		MessageBox.show(this,"��ѡ��һ�����ݽ��д���");
		    		return;
		    	}
		    	for (BillVO billVO : checkUsers) {
	    			String result=billVO.getStringValue("deal_result");
	    			if(!"δ����".equals(result)){
	    				successResult=successResult+billVO.getStringValue("COD_ACCT_NO")+" ";
	    			}
				}
	    		if(!"".equals(successResult)){
	    			MessageBox.show(this,"��ǰѡ�н����˺Ŵ��ڡ�"+successResult+"��״̬�ѱ����������ѡ��");
	    			return;
	    		}
		    	//������õ�������һ��Panel
		        final BillCardDialog cardDialog=new BillCardDialog(this,"Ա�����׺�ʵ","WN_CURRENT_CHECK_RESULT_ZPY_Q01",600,300);
			    cardDialog.getBillcardPanel().setEditable("CHECK_RESULT", true);
			    cardDialog.getBillcardPanel().setEditable("CHECK_REASON",true);
			    cardDialog.getBtn_save().setVisible(false);
//		        cardDialog.setRealSave(false);//������ϵͳ�Դ��ı��湦��
//		        cardDialog.setAddDefaultWindowListener(true);
		       
     		    final WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil.lookUpRemoteService(WnSalaryServiceIfc.class);
		        cardDialog.getBtn_confirm().addActionListener(new ActionListener() {
	            	@Override
					public void actionPerformed(ActionEvent event) {
	            	   Map<String, String> paraMap= new HashMap<String, String>();
	      		       paraMap.put("CHECK_RESULT", cardDialog.getCardItemValue("CHECK_RESULT"));
	      		       paraMap.put("CHECK_REASON", cardDialog.getCardItemValue("CHECK_REASON"));
	      		       paraMap.put("CHECK_USERCODE",cardDialog.getCardItemValue("CHECK_USERCODE"));
	      		       paraMap.put("CHECK_USERNAME",cardDialog.getCardItemValue("CHECK_USERNAME"));
	      		       paraMap.put("CHECK_DATE", cardDialog.getCardItemValue("CHECK_DATE"));
					   str=service.updateCheckState(checkUsers,paraMap);
					   cardDialog.closeMe();
	            	}
				});
		        cardDialog.setVisible(true);
	            listPanel.refreshData();//ˢ������
	    	}catch(Exception ex){
	    		ex.printStackTrace();
	    	}
	    }
	}
	public String createExcel(String filePath){
		String result="";
		try {
			 String templetName = listPanel.getTempletVO().getTempletname();//��ȡ��ģ������
			 Workbook monitorResult=new SXSSFWorkbook(100);
             Sheet firstSheet = monitorResult.createSheet("Ա����Ϣ���");//����sheet
             Row firstRow = firstSheet.createRow(0);//��������
             //��ȡ����ͷ��Ϣ
             Pub_Templet_1_ItemVO[] templetItemVOs = listPanel.getTempletItemVOs();
             List<String> unShowList=new ArrayList<String>();
             for (int i = 0,n = 0; i < templetItemVOs.length; i++) {
            	 Pub_Templet_1_ItemVO pub_Templet_1_ItemVO = templetItemVOs[i];
            	 String cellKey=pub_Templet_1_ItemVO.getItemkey();
            	 String cellName=pub_Templet_1_ItemVO.getItemname();
            	 if(pub_Templet_1_ItemVO.isListisshowable()){
            		 firstRow.createCell(i-n).setCellValue(cellName);
				 }else{
					 unShowList.add(cellKey);
					 n++;
				 }
			}
             String sql="select * from WN_CURRENT_DEAL_result where 1=1 "+queryCondition;
             HashVO[] hashVos = UIUtil.getHashVoArrayByDS(null, sql);
            Row nextRow = null;
            for (int i = 0; i < hashVos.length; i++) {
            	nextRow=firstSheet.createRow(i+1);//������һ������
            	String[] keys = hashVos[i].getKeys();
            	for (int j = 0,n=0; j < keys.length; j++) {//��ÿһ�����ݽ��д���
					if(unShowList.contains(keys[j].toUpperCase())){//�жϵ�ǰ���Ƿ��Ѿ�����
						n++;
						continue;
					}
					nextRow.createCell(j-n).setCellValue(hashVos[i].getStringValue(keys[j]));
				}
			}
            String fileName=filePath.substring(filePath.lastIndexOf("\\")+1,filePath.lastIndexOf("."));
            filePath=filePath.substring(0,filePath.lastIndexOf("\\"));
            File file=new File(filePath+"/"+fileName+".xls");
            int i=1;
            while(file.exists()){
            	fileName=templetName+i+".xls";
            	file=new File(filePath+"/"+fileName);
            	i++;
            }
            file.createNewFile();
            FileOutputStream fout = new FileOutputStream(file.getAbsolutePath());
            monitorResult.write(fout);
            fout.close();
            result="�����ɹ�";
		} catch (Exception e) {
			result="����ʧ��";
			e.printStackTrace();
		}
		return result;
	}
}