package com.pushworld.ipushgrc.ui.score.p020;

import cn.com.infostrategy.ui.mdata.BillOfficeDialog;
import cn.com.infostrategy.ui.mdata.BillOfficeIntercept;
import cn.com.infostrategy.ui.mdata.BillOfficePanel;

public class PrintBillOfficeIntercept extends BillOfficeIntercept {
	String pictureid = null;
	BillOfficeDialog officeDialog; //将父dialog传进来

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
		 * 没想出什么好办法写。。本来想隐藏或者使打印按钮不可用，都没成功。。所以用了这种校验方式
		 */

//		bf.getWebBrowser().executeScript("try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('123');} catch(e){} ;"); //接锁定
		bf.getWebBrowser().executeScript("TANGER_OCX_OBJ.FilePrint=true;\r\n");
		String retValue = bf.getWebBrowser().executeScript("printoutword()"); // 是否弹出打印设定窗口,返回值为点击按钮【确定】或者【取消】
		bf.getWebBrowser().executeScript("TANGER_OCX_OBJ.FilePrint=false");
		//如果打印了，则将插入一条打印数据
	}

	@Override
	public void printAll(BillOfficePanel bf) {//全打
		try {
			//解保护
			//bf.getWebBrowser().executeScript("try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('pushworld');} catch(e){} ;" );
			//			bf.getWebBrowser().executeScript("addPageHeaderRight(\"456\")");
			//加文字水印
			//			bf.getWebBrowser().executeScript("addWater(\"兴业银行\")");
			//
			//			//这里生成图片
			//			String imgurl = "C://2of7.jpg";
			//
			//			//将本地C盘的水印图片写到数据库中，pub_imgupload是保存图片内容的表
			//			TBUtil tbUtil = new TBUtil(); //
			//			byte[] bytes = tbUtil.readFromInputStreamToBytes(new FileInputStream(imgurl)); //取得图片的内容!!!
			//			String str_code64 = tbUtil.convertBytesTo64Code(bytes); //将字节转成64位编码!
			//			String str_newBatchNO = UIUtil.getMetaDataService().saveImageUploadDocument(null, str_code64, null, null, null); //将图片存到数据库中，并且返回batchid，一个batchid对应一个图片。pub_imgupload
			//			
			//			bf.getWebBrowser().executeScript("addWaterPicture1('" + System.getProperty("CALLURL") + "/ImageServlet?fromtype=db&imgname=" + str_newBatchNO + "'," + 330 + "," + 580 + ");\r\n");//加条形码水印，后两个参数是图片x，y轴位置，可适当调整

			//打印文件
			bf.getWebBrowser().executeScript("printFile();");

			//打印完后需要将图片內容删除
			//UIUtil.executeUpdateByDS(null,"delete from pub_imgupload where batchid="+str_newBatchNO);

			//bf.getWebBrowser().executeScript("try{ TANGER_OCX_OBJ.ActiveDocument.Protect(2,true,'pushworld');\r\n} catch(e){} ;" );
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("FF:系统添加合同全打历史记录时出现异常!");
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
