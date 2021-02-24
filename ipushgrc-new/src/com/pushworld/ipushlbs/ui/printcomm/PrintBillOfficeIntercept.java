package com.pushworld.ipushlbs.ui.printcomm;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillOfficeDialog;
import cn.com.infostrategy.ui.mdata.BillOfficeIntercept;
import cn.com.infostrategy.ui.mdata.BillOfficePanel;

public class PrintBillOfficeIntercept extends BillOfficeIntercept  {
	String pictureid = null;
	//String billid ; //�������Ҫ��ӡ��id
	BillVO billvo ; // ��ӡ��billvo
	BillOfficeDialog officeDialog ; //����dialog������
	
	public PrintBillOfficeIntercept(BillVO billvo,BillOfficeDialog officedialog){
		this.billvo = billvo ;
		this.officeDialog = officedialog ;
	}
	
	public void setPictureid(String pictureid) {
		this.pictureid = pictureid;
	}

	@Override
	public void afterDocumenComplet(BillOfficePanel arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterSave(BillOfficePanel arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void print(BillOfficePanel bf) {
		/**
		 * û���ʲô�ð취д�������������ػ���ʹ��ӡ��ť�����ã���û�ɹ�����������������У�鷽ʽ
		 */

		String sql_query = "select print from " + billvo.getSaveTableName() + " where id = " + billvo.getStringValue("id") ;
		try {
			String printstate = UIUtil.getStringValueByDS(null, sql_query);
				if(printstate.equals("��")){
					bf.getWebBrowser().executeScript("alert('�Ѿ���ӡ�ˣ��������ظ���ӡ��');");
					return ;
				}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int count = Integer.parseInt(billvo.getStringValue("PRINT_TIMES")) + 1; //��ӡ������ +1 Ϊ�����
		String contactName = billvo.getStringValue("DEAL_CODE"); // 
		
		bf.getWebBrowser().executeScript("try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('123');} catch(e){} ;" );  //������
		bf.getWebBrowser().executeScript("TANGER_OCX_OBJ.FilePrint=true;\r\n"); 
		//bf.getWebBrowser().executeScript("try{TANGER_OCX_OBJ.PrintPreview();} catch(e){} ;");
		
		boolean flag = true ;  //�Ƿ����ˡ�ȷ������ӡ
		for(int i = 1 ; i < count ; i ++){
			if(i == 1){	
				bf.getWebBrowser().executeScript("addPageHeaderRight(\""+contactName+" - "+i+"\")");  // ���ϽǼ�Ψһ���룬��Ҫ�������λ�ã�Ҫ�ȼ�ҳü�����ӡ
				//�ж�ͨ������
//				if(FunctionManageImp.GetFuntionManageImp().getIsPrintWater().equals("1")){
//					bf.getWebBrowser().executeScript("addWater(\"����ˮӡ\")");
//				}
				String retValue = bf.getWebBrowser().executeScript("printoutword()"); // �Ƿ񵯳���ӡ�趨����,����ֵΪ�����ť��ȷ�������ߡ�ȡ����
				if(retValue.equals("-2") || retValue.equals("0")){  //���������ǡ�ȡ�������ߡ��رա���ť
					flag = false ;
					break ;
				}
			}
			else{
				bf.getWebBrowser().executeScript("removePageHeaderRight(\""+contactName+" - "+(i-1)+"\")");  // �����һ����ҳü��Ϣ
				bf.getWebBrowser().executeScript("addPageHeaderRight(\""+contactName+" - "+i+"\")");
				//�ж�ͨ������
//				if(FunctionManageImp.GetFuntionManageImp().getIsPrintWater().equals("1")){
//					bf.getWebBrowser().executeScript("addWater(\"����ˮӡ\")");
//				}
				bf.getWebBrowser().executeScript("var v_result=TANGER_OCX_OBJ.PrintOut(false)");  // ��������ӡ���ڣ��ظ���ӡ��
			}
		}
		bf.getWebBrowser().executeScript("TANGER_OCX_OBJ.FilePrint=false");
		//�����ӡ�ˣ��򽫲���һ����ӡ����
		if(flag){  //����������ȷ����ӡ����ôִ���������
			PrintCount();
		}
	}
	
	private void PrintCount() {
		String contact_id = "" ;
		try {
			// ��Ϊ��ʽ�ͷǸ�ʽ��ṹ��ͬ�����������������
			if(billvo.getTempletCode().equals("FORMAT_DEAL_CHECK_CODE2")){ //����Ǹ�ʽ��ͬ��ӡ
				contact_id = billvo.getStringValue("DEALDOC_NAME");
			}else{  // ����ǷǸ�ʽ��ͬ��ӡ
				if("".equals( billvo.getStringValue("DEAL_ID")) || billvo.getStringValue("DEAL_ID")==null ){ //����������õķǸ�ʽ��ͬ�������������ݣ�ֱ�ӽ���
					String sql = "update "+billvo.getSaveTableName()+" set print = '��' where id = '"+billvo.getStringValue("id")+"'" ;
					UIUtil.executeUpdateByDS(null, sql);
					return ;
				}
				else
					contact_id = billvo.getStringValue("DEAL_ID");
			}
			//��δ��ӡ��δ�Ѵ�ӡ
			String sql = "update "+billvo.getSaveTableName()+" set print = '��' where id = '"+billvo.getStringValue("id")+"'" ;
			UIUtil.executeUpdateByDS(null, sql);
			// ���� lbs_dept_print_contact ��ӡ����,�ֱ�Ϊ����,��ͬ,��ʽ/�Ǹ�ʽ,��ӡʱ��,��ӡ����
			sql = "insert into lbs_dept_print_contact (dept_id,contact_id,type,pdate,pcount )" +
					" values " +
					" ('"+billvo.getStringValue("SENDORG")+"','"+contact_id+
					"','"+billvo.getStringValue("CHEK_TYPE")+"','"+UIUtil.getCurrDate()+"'," +
					Integer.parseInt(billvo.getStringValue("PRINT_TIMES")) + ")" ;
			UIUtil.executeUpdateByDS(null, sql);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void printAll(BillOfficePanel bf) {//ȫ��
		try {
			//�Ᵽ��
			//bf.getWebBrowser().executeScript("try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('pushworld');} catch(e){} ;" );
			bf.getWebBrowser().executeScript("addPageHeaderRight(\"456\")");
			//������ˮӡ
//			bf.getWebBrowser().executeScript("addWater(\"��ҵ����\")");
//
//			//��������ͼƬ
//			String imgurl = "C://2of7.jpg";
//
//			//������C�̵�ˮӡͼƬд�����ݿ��У�pub_imgupload�Ǳ���ͼƬ���ݵı�
//			TBUtil tbUtil = new TBUtil(); //
//			byte[] bytes = tbUtil.readFromInputStreamToBytes(new FileInputStream(imgurl)); //ȡ��ͼƬ������!!!
//			String str_code64 = tbUtil.convertBytesTo64Code(bytes); //���ֽ�ת��64λ����!
//			String str_newBatchNO = UIUtil.getMetaDataService().saveImageUploadDocument(null, str_code64, null, null, null); //��ͼƬ�浽���ݿ��У����ҷ���batchid��һ��batchid��Ӧһ��ͼƬ��pub_imgupload
//			
//			bf.getWebBrowser().executeScript("addWaterPicture1('" + System.getProperty("CALLURL") + "/ImageServlet?fromtype=db&imgname=" + str_newBatchNO + "'," + 330 + "," + 580 + ");\r\n");//��������ˮӡ��������������ͼƬx��y��λ�ã����ʵ�����

			//��ӡ�ļ�
			bf.getWebBrowser().executeScript("printFile();");
			
			//��ӡ�����Ҫ��ͼƬ����ɾ��
			//UIUtil.executeUpdateByDS(null,"delete from pub_imgupload where batchid="+str_newBatchNO);
			
			//bf.getWebBrowser().executeScript("try{ TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,'pushworld');\r\n} catch(e){} ;" );
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("FF:ϵͳ��Ӻ�ͬȫ����ʷ��¼ʱ�����쳣!");
		}

	}

	@Override
	public void printFen(BillOfficePanel arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void printTao(BillOfficePanel arg0) {
		// TODO Auto-generated method stub

	}

}

