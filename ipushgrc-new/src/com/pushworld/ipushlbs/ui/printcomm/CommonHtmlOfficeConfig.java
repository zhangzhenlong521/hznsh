package com.pushworld.ipushlbs.ui.printcomm;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillOfficeDialog;
/**
 * 设置超链接 office控件
 * @author yinliang
 * @since 2012.01.17
 *
 */
public class CommonHtmlOfficeConfig {
	public static void OfficeConfig(String filename,BillVO billvo,BillOfficeDialog officeDialog){
		
		officeDialog.addSomeActionListener(new PrintBillOfficeIntercept(billvo,officeDialog));
		officeDialog.setIfselfdesc(true);  //
		officeDialog.setIfshowprint_all(false);  //全打
		officeDialog.setIfshowprint_fen(false); //分打
		officeDialog.setIfshowsave(false); // 保存按钮
		officeDialog.setIfshowprint_tao(false); // 套打
		officeDialog.setIfshowwater(false);  //水印
		officeDialog.setIfshowprint(false);  //打印
		officeDialog.setIfshowshowcomment(false);
		officeDialog.setIfshowhidecomment(false);
		officeDialog.setIfshowedit(false); // 修订
		officeDialog.setIfshowshowedit(false); // 显示修订
		officeDialog.setIfshowhideedit(false); // 隐藏修订
		officeDialog.setIfshowacceptedit(false); // 接收修订

		officeDialog.initialize(filename, false, false, "officecompfile");
	}
}
