package com.pushworld.ipushlbs.ui.printcomm;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillOfficeDialog;
import cn.com.infostrategy.ui.mdata.BillOfficeIntercept;
import cn.com.infostrategy.ui.mdata.BillOfficePanel;

public class PrintBillOfficeIntercept extends BillOfficeIntercept  {
	String pictureid = null;
	//String billid ; //用来存放要打印的id
	BillVO billvo ; // 打印的billvo
	BillOfficeDialog officeDialog ; //将父dialog传进来
	
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
		 * 没想出什么好办法写。。本来想隐藏或者使打印按钮不可用，都没成功。。所以用了这种校验方式
		 */

		String sql_query = "select print from " + billvo.getSaveTableName() + " where id = " + billvo.getStringValue("id") ;
		try {
			String printstate = UIUtil.getStringValueByDS(null, sql_query);
				if(printstate.equals("是")){
					bf.getWebBrowser().executeScript("alert('已经打印了，不可以重复打印！');");
					return ;
				}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int count = Integer.parseInt(billvo.getStringValue("PRINT_TIMES")) + 1; //打印张数， +1 为编码号
		String contactName = billvo.getStringValue("DEAL_CODE"); // 
		
		bf.getWebBrowser().executeScript("try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('123');} catch(e){} ;" );  //接锁定
		bf.getWebBrowser().executeScript("TANGER_OCX_OBJ.FilePrint=true;\r\n"); 
		//bf.getWebBrowser().executeScript("try{TANGER_OCX_OBJ.PrintPreview();} catch(e){} ;");
		
		boolean flag = true ;  //是否点击了【确定】打印
		for(int i = 1 ; i < count ; i ++){
			if(i == 1){	
				bf.getWebBrowser().executeScript("addPageHeaderRight(\""+contactName+" - "+i+"\")");  // 右上角加唯一编码，需要放在这个位置，要先加页眉，后打印
				//判断通用条件
//				if(FunctionManageImp.GetFuntionManageImp().getIsPrintWater().equals("1")){
//					bf.getWebBrowser().executeScript("addWater(\"我是水印\")");
//				}
				String retValue = bf.getWebBrowser().executeScript("printoutword()"); // 是否弹出打印设定窗口,返回值为点击按钮【确定】或者【取消】
				if(retValue.equals("-2") || retValue.equals("0")){  //如果点击的是【取消】或者【关闭】按钮
					flag = false ;
					break ;
				}
			}
			else{
				bf.getWebBrowser().executeScript("removePageHeaderRight(\""+contactName+" - "+(i-1)+"\")");  // 清除上一条的页眉信息
				bf.getWebBrowser().executeScript("addPageHeaderRight(\""+contactName+" - "+i+"\")");
				//判断通用条件
//				if(FunctionManageImp.GetFuntionManageImp().getIsPrintWater().equals("1")){
//					bf.getWebBrowser().executeScript("addWater(\"我是水印\")");
//				}
				bf.getWebBrowser().executeScript("var v_result=TANGER_OCX_OBJ.PrintOut(false)");  // 不弹出打印窗口，重复打印。
			}
		}
		bf.getWebBrowser().executeScript("TANGER_OCX_OBJ.FilePrint=false");
		//如果打印了，则将插入一条打印数据
		if(flag){  //如果点击的是确定打印，那么执行下面操作
			PrintCount();
		}
	}
	
	private void PrintCount() {
		String contact_id = "" ;
		try {
			// 因为格式和非格式表结构不同，所以这里进行区分
			if(billvo.getTempletCode().equals("FORMAT_DEAL_CHECK_CODE2")){ //如果是格式合同打印
				contact_id = billvo.getStringValue("DEALDOC_NAME");
			}else{  // 如果是非格式合同打印
				if("".equals( billvo.getStringValue("DEAL_ID")) || billvo.getStringValue("DEAL_ID")==null ){ //如果不是引用的非格式合同范本，不插数据，直接结束
					String sql = "update "+billvo.getSaveTableName()+" set print = '是' where id = '"+billvo.getStringValue("id")+"'" ;
					UIUtil.executeUpdateByDS(null, sql);
					return ;
				}
				else
					contact_id = billvo.getStringValue("DEAL_ID");
			}
			//将未打印改未已打印
			String sql = "update "+billvo.getSaveTableName()+" set print = '是' where id = '"+billvo.getStringValue("id")+"'" ;
			UIUtil.executeUpdateByDS(null, sql);
			// 插入 lbs_dept_print_contact 打印数据,分别为机构,合同,格式/非格式,打印时间,打印张数
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
	public void printAll(BillOfficePanel bf) {//全打
		try {
			//解保护
			//bf.getWebBrowser().executeScript("try{  TANGER_OCX_OBJ.ActiveDocument.Unprotect('pushworld');} catch(e){} ;" );
			bf.getWebBrowser().executeScript("addPageHeaderRight(\"456\")");
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

