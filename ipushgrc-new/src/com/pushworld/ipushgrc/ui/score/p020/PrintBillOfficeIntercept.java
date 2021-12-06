package com.pushworld.ipushgrc.ui.score.p020;

import cn.com.infostrategy.ui.mdata.BillOfficeDialog;
import cn.com.infostrategy.ui.mdata.BillOfficeIntercept;
import cn.com.infostrategy.ui.mdata.BillOfficePanel;

public class PrintBillOfficeIntercept extends BillOfficeIntercept {
	String pictureid = null;
	BillOfficeDialog officeDialog; //����dialog������

	public PrintBillOfficeIntercept(BillOfficeDialog officedialog) {
		this.officeDialog = officedialog;
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

//		bf.getWebBrowser().executeScript("try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('123');} catch(e){} ;"); //������
		bf.getWebBrowser().executeScript("TANGER_OCX_OBJ.FilePrint=true;\r\n");
		String retValue = bf.getWebBrowser().executeScript("printoutword()"); // �Ƿ񵯳���ӡ�趨����,����ֵΪ�����ť��ȷ�������ߡ�ȡ����
		bf.getWebBrowser().executeScript("TANGER_OCX_OBJ.FilePrint=false");
		//�����ӡ�ˣ��򽫲���һ����ӡ����
	}

	@Override
	public void printAll(BillOfficePanel bf) {//ȫ��
		try {
			//�Ᵽ��
			//bf.getWebBrowser().executeScript("try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('pushworld');} catch(e){} ;" );
			//			bf.getWebBrowser().executeScript("addPageHeaderRight(\"456\")");
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
